package java.net

import java.text.SimpleDateFormat
import java.util.{Calendar, GregorianCalendar, List as JList, Locale, Objects, StringTokenizer, TimeZone}
import java.util.NoSuchElementException

final class HttpCookie private[net] (name: String, value: String | Null, rawHeader: String | Null, creationTime: Long)
    extends Cloneable:

  private val cookieName: String =
    var tmp = name
    tmp = tmp.trim
    if tmp.isEmpty || !HttpCookie.isToken(tmp) || tmp.charAt(0) == '$' then
      throw IllegalArgumentException("Illegal cookie name")
    tmp

  private var cookieValue: String | Null = value
  private var comment: String | Null = null
  private var commentURL: String | Null = null
  private var toDiscard: Boolean = false
  private var domain: String | Null = null
  private var maxAge: Long = HttpCookie.MAX_AGE_UNSPECIFIED
  private var path: String | Null = null
  private var portlist: String | Null = null
  private var secure: Boolean = false
  private var httpOnly: Boolean = false
  private var version: Int = 1
  private val whenCreated: Long = creationTime
  private val originalHeader: String | Null = rawHeader

  def this(name: String, value: String) =
    this(name, value, null, System.currentTimeMillis())

  private def this(name: String, value: String, header: String | Null) =
    this(name, value, header, System.currentTimeMillis())

  def hasExpired(): Boolean = hasExpired(System.currentTimeMillis())

  def hasExpired(currentTimeMillis: Long): Boolean =
    if maxAge == 0 then true
    else if maxAge < 0 then false
    else
      val expiresAt = whenCreated + (maxAge * 1000)
      currentTimeMillis >= expiresAt

  def setComment(purpose: String): Unit =
    comment = purpose

  def getComment(): String | Null = comment

  def setCommentURL(purpose: String): Unit =
    commentURL = purpose

  def getCommentURL(): String | Null = commentURL

  def setDiscard(discard: Boolean): Unit =
    toDiscard = discard

  def getDiscard(): Boolean = toDiscard

  def setPortlist(ports: String): Unit =
    portlist = ports

  def getPortlist(): String | Null = portlist

  def setDomain(pattern: String | Null): Unit =
    if pattern != null then domain = pattern.toLowerCase(Locale.ROOT) else domain = null

  def getDomain(): String | Null = domain

  def setMaxAge(expiry: Long): Unit =
    maxAge = expiry

  def getMaxAge(): Long = maxAge

  def setPath(uri: String): Unit =
    path = uri

  def getPath(): String | Null = path

  def setSecure(flag: Boolean): Unit =
    secure = flag

  def getSecure(): Boolean = secure

  def getName(): String = cookieName

  def setValue(newValue: String): Unit =
    cookieValue = newValue

  def getValue(): String | Null = cookieValue

  def getVersion(): Int = version

  def setVersion(v: Int): Unit =
    if v != 0 && v != 1 then throw IllegalArgumentException("cookie version should be 0 or 1")
    version = v

  def isHttpOnly(): Boolean = httpOnly

  def setHttpOnly(httpOnly: Boolean): Unit =
    this.httpOnly = httpOnly

  override def toString(): String =
    if getVersion() > 0 then toRFC2965HeaderString() else toNetscapeHeaderString()

  override def equals(obj: Any): Boolean =
    obj match
      case other: HttpCookie =>
        if (this eq other) true
        else
          HttpCookie.equalsIgnoreCase(getName(), other.getName()) &&
          HttpCookie.equalsIgnoreCase(getDomain(), other.getDomain()) &&
          Objects.equals(getPath(), other.getPath())
      case _ => false

  override def hashCode(): Int =
    val h1 = getName().toLowerCase(Locale.ROOT).hashCode
    val h2 =
      val d = getDomain()
      if d != null then d.toLowerCase(Locale.ROOT).hashCode else 0
    val h3 =
      val p = getPath()
      if p != null then p.hashCode else 0
    h1 + h2 + h3

  override def clone(): AnyRef =
    try super.clone()
    catch case e: CloneNotSupportedException => throw RuntimeException(e.getMessage)

  private[net] def getCreationTime(): Long = whenCreated

  private def toNetscapeHeaderString(): String =
    getName() + "=" + getValue()

  private def toRFC2965HeaderString(): String =
    val sb = new java.lang.StringBuilder()
    sb.append(getName()).append("=\"").append(getValue()).append('"')
    if getPath() != null then sb.append(";$Path=\"").append(getPath()).append('"')
    if getDomain() != null then sb.append(";$Domain=\"").append(getDomain()).append('"')
    if getPortlist() != null then sb.append(";$Port=\"").append(getPortlist()).append('"')
    sb.toString

  private def expiryDate2DeltaSeconds(dateString: String | Null): Long =
    val cal = new GregorianCalendar(HttpCookie.GMT)
    var i = 0
    while i < HttpCookie.COOKIE_DATE_FORMATS.length do
      val df = new SimpleDateFormat(HttpCookie.COOKIE_DATE_FORMATS(i), Locale.US)
      cal.set(1970, 0, 1, 0, 0, 0)
      df.setTimeZone(HttpCookie.GMT)
      df.setLenient(false)
      df.set2DigitYearStart(cal.getTime)
      try
        cal.setTime(df.parse(dateString))
        if !HttpCookie.COOKIE_DATE_FORMATS(i).contains("yyyy") then
          var year = cal.get(Calendar.YEAR)
          year %= 100
          if year < 70 then year += 2000 else year += 1900
          cal.set(Calendar.YEAR, year)
        return (cal.getTimeInMillis - whenCreated) / 1000
      catch case _: Exception =>
        ()
      i += 1
    0

object HttpCookie:
  final val MAX_AGE_UNSPECIFIED: Long = -1L
  val GMT: TimeZone = TimeZone.getTimeZone("GMT")
  private val COOKIE_DATE_FORMATS: Array[String] = Array(
    "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'",
    "EEE',' dd MMM yyyy HH:mm:ss 'GMT'",
    "EEE MMM dd yyyy HH:mm:ss 'GMT'Z",
    "EEE',' dd-MMM-yy HH:mm:ss 'GMT'",
    "EEE',' dd MMM yy HH:mm:ss 'GMT'",
    "EEE MMM dd yy HH:mm:ss 'GMT'Z"
  )
  private val SET_COOKIE = "set-cookie:"
  private val SET_COOKIE2 = "set-cookie2:"
  private val TSPECIALS = ",; " // deliberately includes space

  private trait CookieAttributeAssignor:
    def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit

  private val ASSIGNORS: java.util.Map[String, CookieAttributeAssignor] =
    val map = new java.util.HashMap[String, CookieAttributeAssignor]()
    map.put("comment", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        if cookie.getComment() == null then cookie.setComment(attrValue)
    })
    map.put("commenturl", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        if cookie.getCommentURL() == null then cookie.setCommentURL(attrValue)
    })
    map.put("discard", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        cookie.setDiscard(true)
    })
    map.put("domain", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        if cookie.getDomain() == null then cookie.setDomain(attrValue)
    })
    map.put("path", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        if cookie.getPath() == null then cookie.setPath(attrValue)
    })
    map.put("port", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        if cookie.getPortlist() == null then cookie.setPortlist(if attrValue == null then "" else attrValue)
    })
    map.put("secure", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        cookie.setSecure(true)
    })
    map.put("httponly", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        cookie.setHttpOnly(true)
    })
    map.put("version", new CookieAttributeAssignor {
      override def assign(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
        try cookie.setVersion(Integer.parseInt(attrValue))
        catch case _: NumberFormatException => ()
    })
    map

  def parse(header: String): JList[HttpCookie] = parse(header, retainHeader = false, currentTimeMillis = -1L)

  def parse(header: String, retainHeader: Boolean): JList[HttpCookie] =
    parse(header, retainHeader, -1L)

  def domainMatches(domain: String | Null, host: String | Null): Boolean =
    if domain == null || host == null then return false

    val isLocalDomain = ".local".equalsIgnoreCase(domain)
    var embeddedDot = domain.indexOf('.')
    if embeddedDot == 0 then embeddedDot = domain.indexOf('.', 1)
    if !isLocalDomain && (embeddedDot == -1 || embeddedDot == domain.length - 1) then return false

    val firstDot = host.indexOf('.')
    if firstDot == -1 && (isLocalDomain || domain.equalsIgnoreCase(host + ".local")) then return true

    val diff = host.length - domain.length
    if diff == 0 then host.equalsIgnoreCase(domain)
    else if diff > 0 then
      val h = host.substring(0, diff)
      val d = host.substring(diff)
      h.indexOf('.') == -1 && d.equalsIgnoreCase(domain)
    else if diff == -1 && domain.charAt(0) == '.' then host.equalsIgnoreCase(domain.substring(1))
    else false

  def parse(header: String, retainHeader: Boolean, currentTimeMillis: Long): JList[HttpCookie] =
    Objects.requireNonNull(header)
    var work = header
    val version = guessCookieVersion(work)
    if startsWithIgnoreCase(work, SET_COOKIE2) then work = work.substring(SET_COOKIE2.length)
    else if startsWithIgnoreCase(work, SET_COOKIE) then work = work.substring(SET_COOKIE.length)

    val cookies = new java.util.ArrayList[HttpCookie]()
    if version == 0 then
      val cookie = parseInternal(work, retainHeader, currentTimeMillis)
      cookie.setVersion(0)
      cookies.add(cookie)
    else
      val cookieStrings = splitMultiCookies(work)
      val it = cookieStrings.iterator()
      while it.hasNext do
        val cookie = parseInternal(it.next(), retainHeader, currentTimeMillis)
        cookie.setVersion(1)
        cookies.add(cookie)
    cookies

  private def parseInternal(header: String, retainHeader: Boolean, currentTime: Long): HttpCookie =
    var currentTimeMillis = currentTime
    if currentTimeMillis == -1L then currentTimeMillis = System.currentTimeMillis()

    val tokenizer = new StringTokenizer(header, ";")
    val namevaluePair =
      try tokenizer.nextToken()
      catch case _: NoSuchElementException => throw IllegalArgumentException("Empty cookie header string")

    val index = namevaluePair.indexOf('=')
    if index == -1 then throw IllegalArgumentException("Invalid cookie name-value pair")
    val name = namevaluePair.substring(0, index).trim
    val value = stripOffSurroundingQuote(namevaluePair.substring(index + 1).trim)
    val cookie =
      if retainHeader then new HttpCookie(name, value, header, currentTimeMillis)
      else new HttpCookie(name, value, null, currentTimeMillis)

    var expiresValue: String | Null = null
    var maxAgeValue: String | Null = null

    while tokenizer.hasMoreTokens do
      val nv = tokenizer.nextToken()
      val idx = nv.indexOf('=')
      val attrName = if idx != -1 then nv.substring(0, idx).trim else nv.trim
      val attrValue = if idx != -1 then nv.substring(idx + 1).trim else null
      if attrName.equalsIgnoreCase("max-age") && maxAgeValue == null then maxAgeValue = attrValue
      else if attrName.equalsIgnoreCase("expires") && expiresValue == null then expiresValue = attrValue
      else assignAttribute(cookie, attrName, attrValue)

    assignMaxAgeAttribute(cookie, expiresValue, maxAgeValue)
    cookie

  private def assignAttribute(cookie: HttpCookie, attrName: String, attrValue: String | Null): Unit =
    val stripped = stripOffSurroundingQuote(attrValue)
    val assignor = ASSIGNORS.get(attrName.toLowerCase(Locale.ROOT))
    if assignor != null then assignor.assign(cookie, attrName, stripped)

  private def assignMaxAgeAttribute(cookie: HttpCookie, expiresValue: String | Null, maxAgeValue: String | Null): Unit =
    if cookie.getMaxAge() != MAX_AGE_UNSPECIFIED then return
    if expiresValue == null && maxAgeValue == null then return

    val expires = stripOffSurroundingQuote(expiresValue)
    val maxAge = stripOffSurroundingQuote(maxAgeValue)

    try
      if maxAge != null then
        val parsed = java.lang.Long.parseLong(maxAge)
        cookie.setMaxAge(parsed)
        return
    catch case _: NumberFormatException => ()

    try
      if expires != null then
        val delta = cookie.expiryDate2DeltaSeconds(expires)
        cookie.setMaxAge(if delta > 0 then delta else 0)
    catch case _: NumberFormatException => ()

  private def splitMultiCookies(header: String): JList[String] =
    val cookies = new java.util.ArrayList[String]()
    var quoteCount = 0
    var p = 0
    var q = 0
    while p < header.length do
      val c = header.charAt(p)
      if c == '"' then quoteCount += 1
      if c == ',' && quoteCount % 2 == 0 then
        cookies.add(header.substring(q, p))
        q = p + 1
      p += 1
    cookies.add(header.substring(q))
    cookies

  private def guessCookieVersion(header: String): Int =
    var lowered = header
    lowered = lowered.toLowerCase(Locale.ROOT)
    if lowered.contains("expires=") then 0
    else if lowered.contains("version=") then 1
    else if lowered.contains("max-age") then 1
    else if startsWithIgnoreCase(lowered, SET_COOKIE2) then 1
    else 0

  private def stripOffSurroundingQuote(str: String | Null): String | Null =
    if str != null && str.length > 2 then
      if str.charAt(0) == '"' && str.charAt(str.length - 1) == '"' then return str.substring(1, str.length - 1)
      if str.charAt(0) == '\'' && str.charAt(str.length - 1) == '\'' then return str.substring(1, str.length - 1)
    str

  private def startsWithIgnoreCase(s: String | Null, start: String): Boolean =
    if s == null || start == null then false
    else if s.length >= start.length && start.equalsIgnoreCase(s.substring(0, start.length)) then true
    else false

  private def isToken(value: String): Boolean =
    var i = 0
    while i < value.length do
      val c = value.charAt(i)
      if c < 0x20 || c >= 0x7f || TSPECIALS.indexOf(c) != -1 then return false
      i += 1
    true

  private def equalsIgnoreCase(s: String | Null, t: String | Null): Boolean =
    if s eq t then true
    else if s != null && t != null then s.equalsIgnoreCase(t)
    else false

  // SharedSecrets hook is intentionally omitted in Scala Native port.
