package snhttp.jdk.net.http.internal

import java.io.{EOFException, IOException, InputStream}
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.Objects.requireNonNull
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

trait SeekableInputStream extends InputStream:
  def seek(pos: Long): Unit
  def position(): Long
  def size(): Long
  def isSeekable(): Boolean
  def isClosed(): Boolean

class DelegateSeekableInputStream(
    maxCachedBytes: Long,
    delegate: InputStream,
) extends SeekableInputStream:

  require(maxCachedBytes >= 0, "maxCachedBytes must not be negative")
  require(
    maxCachedBytes <= Int.MaxValue,
    "maxCachedBytes must not be greater than Int.MaxValue",
  )

  private val chunkSize = PropertyUtils.INTERNAL_BUFFER_SIZE.max(4096)
  private val cache = ByteBuffer.allocate(maxCachedBytes.toInt)
  private val seekable = maxCachedBytes > 0

  private val lock = new ReentrantLock()
  private var currPosition = 0L
  private var cachedBytes = 0L
  private var cacheOverflowed = false
  private var eof = false

  private val closed = new AtomicBoolean(false)

  override def seek(pos: Long): Unit = {
    ensureOpen()
    if (pos < 0) throw new IOException("Negative seek position")
    if (!isSeekable()) throw new IOException("InputStream is not seekable")

    lock.lock()
    try
      if (pos <= cachedBytes) //
        currPosition = pos
      else {
        if (pos > maxCachedBytes)
          throw new IOException("InputStream is not seekable")

        val scratch = new Array[Byte](chunkSize.min((pos - cachedBytes).min(Int.MaxValue).toInt))

        while currPosition < pos
        do {
          val toRead = scratch.length.min((pos - currPosition).toInt.min(Int.MaxValue))
          val readBytes = read(scratch, 0, toRead)
          if (readBytes < 0)
            throw new EOFException("Reached end of stream")
          if (!isSeekable())
            throw new IOException("InputStream is not seekable")
        }
      }
    finally //
      lock.unlock()
  }

  override def position(): Long =
    currPosition

  override def size(): Long =
    if eof && isSeekable()
    then //
      cachedBytes
    else //
      throw new UnsupportedOperationException("Size not supported")

  override def isSeekable(): Boolean =
    seekable && !cacheOverflowed

  override def isClosed(): Boolean =
    closed.get()

  override def read(): Int =
    val one = new Array[Byte](1)
    val readBytes = read(one, 0, 1)
    if readBytes < 0 then -1 else one(0) & 0xff

  override def read(bytes: Array[Byte], offset: Int, length: Int): Int = {
    ensureOpen()
    requireNonNull(bytes, "bytes must not be null")
    if (offset < 0 || length < 0 || length > (bytes.length - offset))
      throw new IndexOutOfBoundsException()
    if length == 0
    then //
      0
    else {
      lock.lock()
      try
        var totalRead = 0
        var earlyExit = false

        if currPosition < cachedBytes
        then {
          val copied = copyFromCache(bytes, offset, length)
          currPosition += copied
          totalRead += copied
        }

        while totalRead < length && !eof && !earlyExit
        do {
          val readBytes = delegate.read(bytes, offset + totalRead, length - totalRead)

          if (readBytes < 0) //
            eof = true
          else if (readBytes == 0) //
            earlyExit = true
          else
            appendToCache(bytes, offset + totalRead, readBytes)
            currPosition += readBytes
            totalRead += readBytes
        }

        if totalRead == 0
        then //
          if eof then -1 else 0
        else //
          totalRead
      finally //
        lock.unlock()
    }
  }

  override def readNBytes(length: Int): Array[Byte] = {
    if (length < 0)
      throw new IllegalArgumentException("length must not be negative")
    val bytes = new Array[Byte](length)
    val readBytes = readNBytes(bytes, 0, length)

    if readBytes == length
    then bytes
    else Arrays.copyOf(bytes, readBytes)
  }

  override def close(): Unit =
    if (!closed.compareAndExchange(false, true)) {
      cache.clear()
      delegate.close()
    }

  /*
   * helpers
   */

  private inline def ensureOpen(): Unit =
    if (closed.get())
      throw new IOException("InputStream is closed")

  private def copyFromCache(bytes: Array[Byte], offset: Int, length: Int): Int = {
    val available = (cachedBytes - currPosition).min(length.toLong).toInt
    val source = cache.duplicate()
    source.position(currPosition.toInt)
    source.limit((currPosition + available).toInt)
    source.get(bytes, offset, available)
    available
  }

  private def appendToCache(bytes: Array[Byte], offset: Int, length: Int): Unit =
    if (seekable && !cacheOverflowed) {
      val writable = (cache.capacity() - cachedBytes).min(length.toLong).toInt
      if (writable > 0)
        val target = cache.duplicate()
        target.position(cachedBytes.toInt)
        target.put(bytes, offset, writable)
        cachedBytes += writable

      if (writable < length)
        cacheOverflowed = true
    }
