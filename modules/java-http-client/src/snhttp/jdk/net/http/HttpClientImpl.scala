package snhttp.jdk.net.http

import java.io.IOException
import java.net.{Authenticator, CookieHandler, InetAddress, ProxySelector}
import java.net.http.{HttpClient, HttpRequest, HttpResponse, WebSocket}
import java.net.http.HttpClient.{Builder, Redirect, Version}
import java.net.http.HttpResponse.{BodyHandler, PushPromiseHandler}
import java.time.Duration
import java.util.Optional
import java.util.Objects.requireNonNull
import java.util.concurrent.{
  CompletableFuture,
  ConcurrentLinkedQueue,
  CountDownLatch,
  Executor,
  ForkJoinPool,
  TimeUnit,
}
import java.util.concurrent.ExecutionException
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.{SSLContext, SSLParameters}

import scala.collection.mutable.HashMap
import scala.scalanative.linux.{epoll, eventfd}
import scala.scalanative.linux.epoll.{epoll_create1, epoll_ctl, epoll_wait}
import scala.scalanative.libc.stddef.NULL as NullPtr
import scala.scalanative.posix.{unistd, errno}
import scala.scalanative.unsafe.{
  CLong,
  CString,
  CStruct2,
  CStruct3,
  CStruct4,
  CStruct6,
  CStruct7,
  CVoidPtr,
  Ptr,
  sizeof,
  Tag,
  UnsafeRichInt,
  Zone,
}
import scala.scalanative.unsafe.{stackalloc, alloc}
import scala.scalanative.unsigned.UnsignedRichInt
import scala.util.control.NonFatal

import _root_.snhttp.experimental.curl.curl.{
  CurlMulti,
  CurlMsg,
  CurlEasy,
  CurlShare,
  CurlLockData,
  CurlSocket,
  CurlCSelect,
  CurlPoll,
  CurlMultiErrCode,
  CurlSocketCallback,
  CurlMultiTimerCallback,
  CurlMultiOption,
}
import _root_.snhttp.experimental.curl.curl.CurlException
import _root_.snhttp.experimental.curl.libcurl
import _root_.snhttp.experimental.curl.libcurl.{CurlMultiHandle, CurlHandle}
import _root_.snhttp.experimental.openssl.libcrypto.{X509, X509_STORE, EVP_PKEY, stack_st_X509}
import _root_.snhttp.jdk.net.http.internal.{HttpConnection, SequentialScheduler, Utils}
import _root_.snhttp.jdk.net.ssl.SSLContextImpl
import _root_.snhttp.utils.PointerCleaner

class HttpClientBuilderImpl() extends Builder:

  protected[http] var _cookieHandler: Optional[CookieHandler] = Optional.empty()
  protected[http] var _connectTimeout: Optional[Duration] = Optional.empty()
  protected[http] var _redirect: Redirect = Redirect.NEVER
  protected[http] var _proxy: Optional[ProxySelector] = Optional.empty()
  protected[http] var _authenticator: Optional[Authenticator] = Optional.empty()
  // default to HTTP/2 for TLS, fallback to HTTP/1.1 for non-TLS
  protected[http] var _version: Version = Version.HTTP_2
  protected[http] var _executor: Optional[Executor] = Optional.empty()
  protected[http] var _sslContext: Optional[SSLContext] = Optional.empty()
  protected[http] var _sslParams: Optional[SSLParameters] = Optional.empty()
  protected[http] var _priority: Int = -1
  protected[http] var _localAddr: Optional[InetAddress] = Optional.empty()

  def cookieHandler(cookieHandler: CookieHandler): Builder =
    requireNonNull(cookieHandler)
    this._cookieHandler = Optional.of(cookieHandler)
    this

  def connectTimeout(duration: Duration): Builder =
    requireNonNull(duration)
    require(!duration.isNegative && !duration.isZero, "duration must be positive")
    this._connectTimeout = Optional.of(duration)
    this

  def sslContext(sslContext: SSLContext): Builder =
    requireNonNull(sslContext)
    this._sslContext = Optional.of(sslContext)
    this

  def sslParameters(sslParams: SSLParameters): Builder =
    requireNonNull(sslParams)
    this._sslParams = Optional.of(Utils.cloneSSLParameters(sslParams))
    this

  def executor(executor: Executor): Builder =
    requireNonNull(executor)
    this._executor = Optional.of(executor)
    this

  def followRedirects(redirect: Redirect): Builder =
    requireNonNull(redirect)
    this._redirect = redirect
    this

  def version(version: Version): Builder =
    requireNonNull(version)
    this._version = version
    this

  def priority(priority: Int): Builder =
    require(priority >= 1 && priority <= 256, "priority must be between 1 and 256 (inclusive)")
    this._priority = priority
    this

  def proxy(proxy: ProxySelector): Builder =
    requireNonNull(proxy)
    this._proxy = Optional.of(proxy)
    this

  def authenticator(authenticator: Authenticator): Builder =
    requireNonNull(authenticator)
    this._authenticator = Optional.of(authenticator)
    this

  override def localAddress(localAddr: InetAddress): Builder =
    requireNonNull(localAddr)
    this._localAddr = Optional.of(localAddr)
    this

  def build(): HttpClient =
    new HttpClientImpl(this)

end HttpClientBuilderImpl

final class HttpClientImpl(
    private[http] val builder: HttpClientBuilderImpl,
) extends HttpClient:

  import HttpClientImpl.*

  private[http] given zone: Zone = Zone.open()

  private[http] val multi = CurlMulti()
  private[http] val share = CurlShare(CurlLockData.COOKIE, CurlLockData.HSTS)

  private[http] val epfd = epoll_create1(epoll.EPOLL_CLOEXEC)
  if (epfd < 0)
    throw new RuntimeException(s"Failed to create epoll instance")

  private[http] val ctxptr = alloc[MultiContext]()
  ctxptr.multiRef = multi.ref
  ctxptr.epfd = epfd
  ctxptr.running = 0
  ctxptr.timeout = -1.toSize

  private[http] val connections = HashMap.empty[CurlEasy, HttpConnection[?]]

  private val _mainLoopScheduling = new AtomicBoolean(false)
  private val _cleanupScheduled = new AtomicBoolean(false)

  @volatile
  private var _shutdownCalled = false
  @volatile
  private var _shutdownNowCalled = false
  @volatile
  private var _terminated = false
  private val _shutdownLock = new ReentrantLock()
  private val _terminatedLatch = new CountDownLatch(1)

  multi.setPtrOption(CurlMultiOption.TIMERDATA, ctxptr)
  multi.setFuncPtrOption(CurlMultiOption.TIMERFUNCTION, timerCallback.asFuncPtr)

  multi.setPtrOption(CurlMultiOption.SOCKETDATA, ctxptr)
  multi.setFuncPtrOption(CurlMultiOption.SOCKETFUNCTION, socketCallback.asFuncPtr)

  private[http] val _executor = builder._executor.orElse(ForkJoinPool.commonPool())
  private val _scheduler = SequentialScheduler(_executor, maxTasksPerTurn = 4)

  private[http] lazy val _sslContext =
    builder._sslContext.orElse(SSLContext.getDefault())
  private[http] lazy val _sslParams =
    builder._sslParams.orElse(_sslContext.getDefaultSSLParameters())
  private[http] lazy val _sslCtxCustomData: Ptr[SslCtxCustomData] = {
    if (!_sslContext.isInstanceOf[SSLContextImpl])
      throw new IllegalArgumentException(
        s"Unsupported SSLContext implementation: ${_sslContext.getClass().getName()}. " +
          s"Only snhttp.jdk.net.ssl.SSLContextImpl is supported now.",
      )
    val ctxImpl = _sslContext.asInstanceOf[SSLContextImpl]

    // TODO: Add them to SslCtxCustomData and pass them to ssl_ctx_callback
    // _sslParams.getProtocols()
    // _sslParams.getCipherSuites()
    // _sslParams.getSignatureSchemes()

    if ctxImpl.underlying.keys.isEmpty
    then
      SslCtxCustomData(
        securityLevel = ctxImpl.underlying.defaultSslCtxSecurityLevel,
        insecure = if ctxImpl.underlying.insecure then 1 else 0,
        clientCerts = null,
        lengthOfclientCerts = 0,
        trustStore = ctxImpl.underlying.trustStore.orElse(null),
        sslParams = null,
      )
    else {
      val keys = ctxImpl.underlying.keys.get()
      val certs = alloc[ClientCert](keys.length)
      for i <- 0 until keys.length do
        val store = keys(i)
        (certs + i)._1 = store.cert.asInstanceOf[Ptr[X509]]
        (certs + i)._2 = store.pkey.asInstanceOf[Ptr[EVP_PKEY]]
        (certs + i)._3 = store.stackOfCA.asInstanceOf[Ptr[stack_st_X509]]

      SslCtxCustomData(
        securityLevel = ctxImpl.underlying.defaultSslCtxSecurityLevel,
        insecure = if ctxImpl.underlying.insecure then 1 else 0,
        clientCerts = certs,
        lengthOfclientCerts = keys.length,
        trustStore = ctxImpl.underlying.trustStore.orElse(null),
        sslParams = null,
      )
    }

  }

  /**
   * Main methods of HttpClient
   */

  def send[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): HttpResponse[T] = {
    requireNonNull(request, "request cannot be null")
    requireNonNull(responseBodyHandler, "responseBodyHandler cannot be null")
    requireNonShutdown()

    try
      sendAsync(request, responseBodyHandler).get()
    catch {
      case exc: ExecutionException =>
        throw exc.getCause()
    }
  }

  /** Equivalent to: `sendAsync(request, responseBodyHandler, null)`. */
  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
  ): CompletableFuture[HttpResponse[T]] =
    sendAsync(request, responseBodyHandler, null)

  def sendAsync[T](
      request: HttpRequest,
      responseBodyHandler: BodyHandler[T],
      pushPromiseHandler: PushPromiseHandler[T],
  ): CompletableFuture[HttpResponse[T]] = {
    requireNonNull(request, "`request: HttpRequest` cannot be null")
    requireNonNull(responseBodyHandler, "`responseBodyHandler: BodyHandler[T]` cannot be null")
    if (pushPromiseHandler != null)
      throw new NotImplementedError("`PushPromiseHandler` feature is not implemented yet.")

    val response = new CompletableFuture[HttpResponse[T]]()

    if !_shutdownCalled
    then
      val conn = new HttpConnection[T](request, response, responseBodyHandler, this)
      signalStart()
    else
      response.completeExceptionally(
        new IOException("HttpClient has been shutdown, no new request will be accepted."),
      )

    response
  }

  override def shutdown(): Unit =
    requestShutdown(immediate = false)

  /**
   * Note from JDK docs:
   *
   * Blocks until all operations have completed execution after a shutdown request, or the duration
   * elapses, or the current thread is interrupted, whichever happens first. Operations are any
   * tasks required to run a request previously submitted with send or sendAsync to completion.
   *
   * This method does not wait if the duration to wait is less than or equal to zero. In this case,
   * the method just tests if the thread has terminated.
   */
  override def awaitTermination(duration: Duration): Boolean = {
    requireNonNull(duration, "duration cannot be null")

    if (duration.isNegative() || duration.isZero()) //
      isTerminated()
    else
      _terminatedLatch.await(duration.toNanos(), TimeUnit.NANOSECONDS)
  }

  /**
   * Note from JDK docs:
   *
   * Note that isTerminated is never true unless either `shutdown` or `shutdownNow` was called
   * first.
   */
  override def isTerminated(): Boolean =
    _shutdownCalled && _terminated

  override def shutdownNow(): Unit =
    requestShutdown(immediate = true)

  override def close(): Unit = {
    var interrupted = false
    shutdown()

    while !isTerminated()
    do
      try _terminatedLatch.await()
      catch {
        case _: InterruptedException =>
          interrupted = true
          shutdownNow()
      }

    if (interrupted)
      Thread.currentThread().interrupt()
  }

  /*
   * Getters for HttpClient properties
   */
  def cookieHandler(): Optional[CookieHandler] =
    builder._cookieHandler

  def connectTimeout(): Optional[Duration] =
    builder._connectTimeout

  def followRedirects(): Redirect =
    builder._redirect

  def proxy(): Optional[ProxySelector] =
    builder._proxy

  /**
   * Note from JDK docs:
   *
   * If no `SSLContext` was set in this client's builder, then the default context is returned.
   */
  def sslContext(): SSLContext =
    _sslContext

  /**
   * Note from JDK docs:
   *
   * If no SSLParameters were set in the client's builder, then an implementation specific default
   * set of parameters, that the client will use, is returned.
   */
  def sslParameters(): SSLParameters =
    Utils.cloneSSLParameters(_sslParams)

  def authenticator(): Optional[Authenticator] =
    builder._authenticator

  def version(): Version =
    builder._version

  def executor(): Optional[Executor] =
    builder._executor

  override def newWebSocketBuilder(): WebSocket.Builder =
    requireNonShutdown()
    ???

  /*
   * Non-JDK public methods
   */

  private[http] def signalStart(): Unit =
    if (!_shutdownNowCalled && !_mainLoopScheduling.compareAndExchange(false, true))
      _scheduler.execute(() => runMainLoop())

  private[http] def collectInfo(): Unit = {
    val msgCount = stackalloc[Int]()
    while {
      val msg = multi.infoRead(msgCount)

      if !msgCount == 0 && msg == NullPtr
      then //
        false // break the loop
      else {
        val m = msg.asInstanceOf[CurlMsg]
        val conn = connections.getOrElse(
          m.curl,
          throw new IllegalStateException(
            s"Failed to find HttpConnection for CURL easy handle pointer: ${m.curl}",
          ),
        )
        conn.assignCurlErrMsg(m)
        true // continue the loop
      }
    }
    do ()
  }

  private[http] inline def unregisterConnection(conn: HttpConnection[?]): Unit =
    connections.remove(conn.easy): Unit
    multi.removeCurlEasy(conn.easy): Unit

  private[http] inline def registerConnection(conn: HttpConnection[?]): Unit =
    connections.put(conn.easy, conn): Unit
    multi.addCurlEasy(conn.easy): Unit

  /*
   * Private methods
   */

  private transparent inline def requireNonShutdown(): Unit =
    if (_shutdownCalled)
      throw new IOException(
        "HttpClient has been shutdown, no new request will be accepted.",
      )

  private transparent inline def toEpollTimeout(curlSocketActionTimeout: CLong): Int =
    Math.clamp(curlSocketActionTimeout.toInt, -1, Int.MaxValue)

  private def mainLoop(): Unit = {
    if (_shutdownNowCalled)
      return

    var break = false

    val bufferSize = 64
    val events = alloc[EpollEvent](bufferSize)

    // first kickstart
    var err = multi.socketAction(CurlSocket.TIMEOUT, CurlCSelect.NONE, ctxptr.runningPtr)
    collectInfo()
    // main loop, will be waken up by either socket events or timer events
    while //
      !break && ctxptr.running > 0 && err == CurlMultiErrCode.OK && !_shutdownNowCalled
    do {
      val timeout = toEpollTimeout(ctxptr.timeout)
      val recvEvents = epoll_wait(ctxptr.epfd, events, bufferSize, timeout)

      if (recvEvents < 0)
        if (errno.errno == errno.EINTR)
          () // Retry if interrupted by signal
        else if (_shutdownNowCalled)
          break = true
        else //
          throw new CurlException(s"epoll_wait failed with error ${errno.errno}")
      else if (_shutdownNowCalled) //
        break = true
      else if (recvEvents == 0) // timeout expired, simply kick the multi socket action again
        ctxptr.timeout = -1
        err = multi.socketAction(CurlSocket.TIMEOUT, CurlCSelect.NONE, ctxptr.runningPtr)
        collectInfo()
      else
        for i <- 0 until recvEvents
        do {
          val event = events(i)

          var action: CurlCSelect = CurlCSelect.NONE
          if ((event.events & epoll.EPOLLIN) != 0)
            action |= CurlCSelect.IN
          if ((event.events & epoll.EPOLLOUT) != 0)
            action |= CurlCSelect.OUT
          if ((event.events & (epoll.EPOLLERR | epoll.EPOLLHUP)) != 0)
            action |= CurlCSelect.ERR

          val epfd = event.data.asInstanceOf[FileDescriptor]
          err = multi.socketAction(CurlSocket.fromFileDescriptor(epfd), action, ctxptr.runningPtr)
        }

      collectInfo()
    }

    _mainLoopScheduling.set(false)
    if (_shutdownCalled)
      scheduleCleanup()
  }

  private def runMainLoop(): Unit =
    try //
      mainLoop()
    catch
      case exc: Throwable =>
        _mainLoopScheduling.set(false)
        failAllConnections(exc)
        if (_shutdownCalled)
          scheduleCleanup()
        throw exc

  private inline def failAllConnections(exc: Throwable): Unit =
    connections.valuesIterator.foreach(_.closeExceptionally(exc))

  private def requestShutdown(immediate: Boolean): Unit = {
    _shutdownLock.lock()
    try
      if (!_terminated) {
        if (immediate)
          _shutdownNowCalled = true
        _shutdownCalled = true
      }
    finally //
      _shutdownLock.unlock()

    scheduleCleanup()
  }

  private def scheduleCleanup(): Unit =
    if (!_cleanupScheduled.compareAndExchange(false, true))
      _scheduler.execute(() => cleanupResources())

  private def cleanupResources(): Unit = {
    var retryGracefulShutdown = false

    _shutdownLock.lock()
    try
      if (!_terminated) {
        if !_shutdownNowCalled && (connections.nonEmpty)
        then
          _cleanupScheduled.set(false)
          retryGracefulShutdown = true
        else {
          try
            if (connections.nonEmpty)
              failAllConnections(
                new IOException("HttpClient was shut down before the operation completed."),
              )
          finally {
            multi.cleanup()
            share.cleanup()
            ctxptr.epfd = -1
            unistd.close(epfd): Unit
            _terminated = true
            _terminatedLatch.countDown()
          }
        }
      }
    finally //
      _shutdownLock.unlock()

    if (retryGracefulShutdown)
      signalStart()
  }

private[http] object HttpClientImpl:

  type NullPtr = CVoidPtr
  type FileDescriptor = Int

  type EpollData = FileDescriptor // | CVoidPtr | ULong | UInt
  given Tag[EpollData] = Tag.Int
  // Tag.materializePtrWildcard.asInstanceOf[Tag[EpollData]]
  // Tag.ULong.asInstanceOf[Tag[EpollData]]

  type EpollEvent = CStruct2[
    /** events */
    Int,
    /** data */
    EpollData,
  ]
  extension (inline event: EpollEvent)
    inline def events: Int = event._1
    inline def events_=(value: Int): Unit = !event.at1 = value
    inline def data: EpollData = event._2
    inline def data_=(value: EpollData): Unit = !event.at2 = value

  /** Used as share data for multi callback functions */
  type MultiContext = CStruct4[
    Ptr[CurlMultiHandle],
    Int,
    Int,
    CLong,
  ]
  extension (inline ctx: MultiContext)
    inline def multiRef: Ptr[CurlMultiHandle] = ctx._1
    inline def multiRef_=(value: Ptr[CurlMultiHandle]): Unit = !ctx.at1 = value
    inline def epfd: Int = ctx._2
    inline def epfd_=(value: Int): Unit = !ctx.at2 = value
    inline def running: Int = ctx._3
    inline def running_=(value: Int): Unit = !ctx.at3 = value
    inline def runningPtr: Ptr[Int] = ctx.at3 // low level pointer getter
    inline def timeout: CLong = ctx._4
    inline def timeout_=(value: CLong): Unit = !ctx.at4 = value

  type SslCtxParameters = CStruct7[
    // min_tls_version
    CLong,
    // max_tls_version
    CLong,
    // for `SSL_CTX_set_cipher_list` (TLSv1.2 and below)
    CString,
    // for `SSL_CTX_set_ciphersuites` (TLSv1.3)
    CString,
    // applicationProtocols for `SSL_CTX_set_alpn_protos`
    CString,
    // signatureSchemes for `SSL_CTX_set1_sigalgs_list`
    CString,
    // namedGroups for `SSL_CTX_set1_groups_list`
    CString,
  ]

  type ClientCert =
    CStruct3[
      // cert
      Ptr[X509],
      // pkey
      Ptr[EVP_PKEY],
      // chain
      Ptr[stack_st_X509],
    ]
  object ClientCert:
    extension (inline crt: ClientCert)
      inline def cert: Ptr[X509] = crt._1
      inline def cert_=(value: Ptr[X509]): Unit = !crt.at1 = value
      inline def pkey: Ptr[EVP_PKEY] = crt._2
      inline def pkey_=(value: Ptr[EVP_PKEY]): Unit = !crt.at2 = value
      inline def chain: Ptr[stack_st_X509] = crt._3
      inline def chain_=(value: Ptr[stack_st_X509]): Unit = !crt.at3 = value

  /**
   * Collect tls config from Java's SSLContext and SSLParameters, and store them in a custom data
   * structure to be used in later curl easy handle creation and configuration (ssl_ctx_callback).
   */
  type SslCtxCustomData = CStruct6[
    // security level
    Int,
    // insecure or not, 0: insecure, 1: secure
    Int,
    // Client certs
    Ptr[ClientCert],
    // size of ClientCert array
    Int,
    // other certificate store, for `SSL_CTX_set_cert_store`
    Ptr[X509_STORE],
    // ssl parameters
    Ptr[SslCtxParameters],
  ]
  object SslCtxCustomData:

    def apply(
        securityLevel: Int,
        insecure: Int,
        clientCerts: Ptr[ClientCert] | Null,
        lengthOfclientCerts: Int,
        trustStore: Ptr[X509_STORE] | Null,
        sslParams: Ptr[SslCtxParameters] | Null,
    )(using Zone): Ptr[SslCtxCustomData] =
      val data = alloc[SslCtxCustomData]()
      data._1 = securityLevel
      data._2 = insecure
      data._3 = clientCerts
      data._4 = lengthOfclientCerts
      data._5 = trustStore
      data._6 = sslParams
      data

    extension (inline data: SslCtxCustomData)
      inline def securityLevel: Int = data._1
      inline def insecure: Int = data._2
      inline def clientCerts: Ptr[ClientCert] = data._3
      inline def lengthOfclientCerts: Int = data._4
      inline def trustStore: Ptr[X509_STORE] = data._5
      inline def sslCtxParams: Ptr[SslCtxParameters] = data._6

  final val socketCallback = CurlSocketCallback.fromScalaFunction {
    (easy: Ptr[CurlHandle], socket: CurlSocket, what: CurlPoll, clientp: Ptr[?], socketp: Ptr[?]) =>
      val ctxptr = clientp.asInstanceOf[Ptr[MultiContext]]

      if what == CurlPoll.REMOVE
      then { // Early return
        val eprc = epoll_ctl(ctxptr.epfd, epoll.EPOLL_CTL_DEL, socket.value, NullPtr)
        val err = libcurl.multiAssign(ctxptr.multiRef, socket, NullPtr)
        0
      } //
      else {
        val events = what match
          case CurlPoll.IN    => epoll.EPOLLIN
          case CurlPoll.OUT   => epoll.EPOLLOUT
          case CurlPoll.INOUT => epoll.EPOLLIN | epoll.EPOLLOUT
          case _              => -1
          // case CurlPoll.REMOVE => epoll.EPOLLERR // Unreachable

        if events == -1
        then //
          -1
        else {
          val epollEvent = stackalloc[EpollEvent]()
          epollEvent.events = events
          epollEvent.data = socket.value
          val op = if socketp == NullPtr then epoll.EPOLL_CTL_ADD else epoll.EPOLL_CTL_MOD

          var eprc = epoll_ctl(ctxptr.epfd, op, socket.value, epollEvent)
          if (eprc < 0 && op == epoll.EPOLL_CTL_ADD && errno.errno == errno.EEXIST)
            eprc = epoll_ctl(ctxptr.epfd, epoll.EPOLL_CTL_MOD, socket.value, epollEvent)
          if eprc < 0 && op == epoll.EPOLL_CTL_MOD && errno.errno == errno.ENOENT then
            eprc = epoll_ctl(ctxptr.epfd, epoll.EPOLL_CTL_ADD, socket.value, epollEvent)

          val ret =
            if eprc < 0 && errno.errno != errno.EINTR // retryable
            then
              // epoll_ctl failed, return error to libcurl,
              // libcurl will then call the callback again with `CURL_POLL_REMOVE`
              -1
            else
              val err = libcurl.multiAssign(ctxptr.multiRef, socket, ctxptr)
              0

          ret
        }
      }
  }

  final val timerCallback = CurlMultiTimerCallback.fromScalaFunction {
    (multi: Ptr[CurlMultiHandle], timeoutMs: CLong, clientp: CVoidPtr) =>
      val ctxptr = clientp.asInstanceOf[Ptr[MultiContext]]
      ctxptr.timeout = timeoutMs
      0
  }

end HttpClientImpl
