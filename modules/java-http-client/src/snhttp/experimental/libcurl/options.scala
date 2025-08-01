package snhttp.experimental.libcurl

import scala.scalanative.unsafe.{Tag, Ptr, Zone, CString, CStruct4}
import scala.scalanative.unsigned.*
import scala.scalanative.posix.sys.socket
import scala.scalanative.unsafe.{alloc, name, extern}

@extern
object options:
  //
  // parts defined in <curl/curl.h>
  //

  /** bitmask defines for `CURLOPT_HEADEROPT` */
  opaque type CurlOpt_HeaderOpt = UInt
  object CurlOpt_HeaderOpt:
    given Tag[CurlOpt_HeaderOpt] = Tag.UInt

    inline def define(inline v: Int): CurlOpt_HeaderOpt = v.toUInt

    val UNIFIED = define(0)
    val SEPARATE = define(1 << 0)

  /* bits for the `CURLOPT_ALTSVC_CTRL` option */
  opaque type CurlOpt_AltSvcCtrl = UInt
  object CurlOpt_AltSvcCtrl:
    given Tag[CurlOpt_AltSvcCtrl] = Tag.UInt
    inline def define(inline v: Int): CurlOpt_AltSvcCtrl = v.toUInt

    val READONLYFILE = define(1 << 2)
    val H1 = define(1 << 3)
    val H2 = define(1 << 4)
    val H3 = define(1 << 5)

  @name("CURLoption")
  opaque type CurlOption = UInt
  object CurlOption:
    given Tag[CurlOption] = Tag.UInt

    inline def define(inline a: Long): CurlOption = a.toUInt

    val WRITEDATA = define(10001)
    val URL = define(10002)
    val PORT = define(3)
    val PROXY = define(10004)
    val USERPWD = define(10005)
    val PROXYUSERPWD = define(10006)
    val RANGE = define(10007)
    val READDATA = define(10009)
    val ERRORBUFFER = define(10010)
    val WRITEFUNCTION = define(20011)
    val READFUNCTION = define(20012)
    val TIMEOUT = define(13)
    val INFILESIZE = define(14)
    val POSTFIELDS = define(10015)
    val REFERER = define(10016)
    val FTPPORT = define(10017)
    val USERAGENT = define(10018)
    val LOW_SPEED_LIMIT = define(19)
    val LOW_SPEED_TIME = define(20)
    val RESUME_FROM = define(21)
    val COOKIE = define(10022)
    val HTTPHEADER = define(10023)
    val HTTPPOST = define(10024)
    val SSLCERT = define(10025)
    val KEYPASSWD = define(10026)
    val CRLF = define(27)
    val QUOTE = define(10028)
    val HEADERDATA = define(10029)
    val COOKIEFILE = define(10031)
    val SSLVERSION = define(32)
    val TIMECONDITION = define(33)
    val TIMEVALUE = define(34)
    val CUSTOMREQUEST = define(10036)
    val STDERR = define(10037)
    val POSTQUOTE = define(10039)
    val OBSOLETE40 = define(10040)
    val VERBOSE = define(41)
    val HEADER = define(42)
    val NOPROGRESS = define(43)
    val NOBODY = define(44)
    val FAILONERROR = define(45)
    val UPLOAD = define(46)
    val POST = define(47)
    val DIRLISTONLY = define(48)
    val APPEND = define(50)
    val NETRC = define(51)
    val FOLLOWLOCATION = define(52)
    val TRANSFERTEXT = define(53)
    val PUT = define(54)
    val PROGRESSFUNCTION = define(20056)
    val XFERINFODATA = define(10057)
    val AUTOREFERER = define(58)
    val PROXYPORT = define(59)
    val POSTFIELDSIZE = define(60)
    val HTTPPROXYTUNNEL = define(61)
    val INTERFACE = define(10062)
    val KRBLEVEL = define(10063)
    val SSL_VERIFYPEER = define(64)
    val CAINFO = define(10065)
    val MAXREDIRS = define(68)
    val FILETIME = define(69)
    val TELNETOPTIONS = define(10070)
    val MAXCONNECTS = define(71)
    val OBSOLETE72 = define(72)
    val FRESH_CONNECT = define(74)
    val FORBID_REUSE = define(75)
    val RANDOM_FILE = define(10076)
    val EGDSOCKET = define(10077)
    val CONNECTTIMEOUT = define(78)
    val HEADERFUNCTION = define(20079)
    val HTTPGET = define(80)
    val SSL_VERIFYHOST = define(81)
    val COOKIEJAR = define(10082)
    val SSL_CIPHER_LIST = define(10083)
    val HTTP_VERSION = define(84)
    val FTP_USE_EPSV = define(85)
    val SSLCERTTYPE = define(10086)
    val SSLKEY = define(10087)
    val SSLKEYTYPE = define(10088)
    val SSLENGINE = define(10089)
    val SSLENGINE_DEFAULT = define(90)
    val DNS_USE_GLOBAL_CACHE = define(91)
    val DNS_CACHE_TIMEOUT = define(92)
    val PREQUOTE = define(10093)
    val DEBUGFUNCTION = define(20094)
    val DEBUGDATA = define(10095)
    val COOKIESESSION = define(96)
    val CAPATH = define(10097)
    val BUFFERSIZE = define(98)
    val NOSIGNAL = define(99)
    val SHARE = define(10100)
    val PROXYTYPE = define(101)
    val ACCEPT_ENCODING = define(10102)
    val PRIVATE = define(10103)
    val HTTP200ALIASES = define(10104)
    val UNRESTRICTED_AUTH = define(105)
    val FTP_USE_EPRT = define(106)
    val HTTPAUTH = define(107)
    val SSL_CTX_FUNCTION = define(20108)
    val SSL_CTX_DATA = define(10109)
    val FTP_CREATE_MISSING_DIRS = define(110)
    val PROXYAUTH = define(111)
    val SERVER_RESPONSE_TIMEOUT = define(112)
    val IPRESOLVE = define(113)
    val MAXFILESIZE = define(114)
    val INFILESIZE_LARGE = define(30115)
    val RESUME_FROM_LARGE = define(30116)
    val MAXFILESIZE_LARGE = define(30117)
    val NETRC_FILE = define(10118)
    val USE_SSL = define(119)
    val POSTFIELDSIZE_LARGE = define(30120)
    val TCP_NODELAY = define(121)
    val FTPSSLAUTH = define(129)
    val IOCTLFUNCTION = define(20130)
    val IOCTLDATA = define(10131)
    val FTP_ACCOUNT = define(10134)
    val COOKIELIST = define(10135)
    val IGNORE_CONTENT_LENGTH = define(136)
    val FTP_SKIP_PASV_IP = define(137)
    val FTP_FILEMETHOD = define(138)
    val LOCALPORT = define(139)
    val LOCALPORTRANGE = define(140)
    val CONNECT_ONLY = define(141)
    val CONV_FROM_NETWORK_FUNCTION = define(20142)
    val CONV_TO_NETWORK_FUNCTION = define(20143)
    val CONV_FROM_UTF8_FUNCTION = define(20144)
    val MAX_SEND_SPEED_LARGE = define(30145)
    val MAX_RECV_SPEED_LARGE = define(30146)
    val FTP_ALTERNATIVE_TO_USER = define(10147)
    val SOCKOPTFUNCTION = define(20148)
    val SOCKOPTDATA = define(10149)
    val SSL_SESSIONID_CACHE = define(150)
    val SSH_AUTH_TYPES = define(151)
    val SSH_PUBLIC_KEYFILE = define(10152)
    val SSH_PRIVATE_KEYFILE = define(10153)
    val FTP_SSL_CCC = define(154)
    val TIMEOUT_MS = define(155)
    val CONNECTTIMEOUT_MS = define(156)
    val HTTP_TRANSFER_DECODING = define(157)
    val HTTP_CONTENT_DECODING = define(158)
    val NEW_FILE_PERMS = define(159)
    val NEW_DIRECTORY_PERMS = define(160)
    val POSTREDIR = define(161)
    val SSH_HOST_PUBLIC_KEY_MD5 = define(10162)
    val OPENSOCKETFUNCTION = define(20163)
    val OPENSOCKETDATA = define(10164)
    val COPYPOSTFIELDS = define(10165)
    val PROXY_TRANSFER_MODE = define(166)
    val SEEKFUNCTION = define(20167)
    val SEEKDATA = define(10168)
    val CRLFILE = define(10169)
    val ISSUERCERT = define(10170)
    val ADDRESS_SCOPE = define(171)
    val CERTINFO = define(172)
    val USERNAME = define(10173)
    val PASSWORD = define(10174)
    val PROXYUSERNAME = define(10175)
    val PROXYPASSWORD = define(10176)
    val NOPROXY = define(10177)
    val TFTP_BLKSIZE = define(178)
    val SOCKS5_GSSAPI_SERVICE = define(10179)
    val SOCKS5_GSSAPI_NEC = define(180)
    val PROTOCOLS = define(181)
    val REDIR_PROTOCOLS = define(182)
    val SSH_KNOWNHOSTS = define(10183)
    val SSH_KEYFUNCTION = define(20184)
    val SSH_KEYDATA = define(10185)
    val MAIL_FROM = define(10186)
    val MAIL_RCPT = define(10187)
    val FTP_USE_PRET = define(188)
    val RTSP_REQUEST = define(189)
    val RTSP_SESSION_ID = define(10190)
    val RTSP_STREAM_URI = define(10191)
    val RTSP_TRANSPORT = define(10192)
    val RTSP_CLIENT_CSEQ = define(193)
    val RTSP_SERVER_CSEQ = define(194)
    val INTERLEAVEDATA = define(10195)
    val INTERLEAVEFUNCTION = define(20196)
    val WILDCARDMATCH = define(197)
    val CHUNK_BGN_FUNCTION = define(20198)
    val CHUNK_END_FUNCTION = define(20199)
    val FNMATCH_FUNCTION = define(20200)
    val CHUNK_DATA = define(10201)
    val FNMATCH_DATA = define(10202)
    val RESOLVE = define(10203)
    val TLSAUTH_USERNAME = define(10204)
    val TLSAUTH_PASSWORD = define(10205)
    val TLSAUTH_TYPE = define(10206)
    val TRANSFER_ENCODING = define(207)
    val CLOSESOCKETFUNCTION = define(20208)
    val CLOSESOCKETDATA = define(10209)
    val GSSAPI_DELEGATION = define(210)
    val DNS_SERVERS = define(10211)
    val ACCEPTTIMEOUT_MS = define(212)
    val TCP_KEEPALIVE = define(213)
    val TCP_KEEPIDLE = define(214)
    val TCP_KEEPINTVL = define(215)
    val SSL_OPTIONS = define(216)
    val MAIL_AUTH = define(10217)
    val SASL_IR = define(218)
    val XFERINFOFUNCTION = define(20219)
    val XOAUTH2_BEARER = define(10220)
    val DNS_INTERFACE = define(10221)
    val DNS_LOCAL_IP4 = define(10222)
    val DNS_LOCAL_IP6 = define(10223)
    val LOGIN_OPTIONS = define(10224)
    val SSL_ENABLE_NPN = define(225)
    val SSL_ENABLE_ALPN = define(226)
    val EXPECT_100_TIMEOUT_MS = define(227)
    val PROXYHEADER = define(10228)
    val HEADEROPT = define(229)
    val PINNEDPUBLICKEY = define(10230)
    val UNIX_SOCKET_PATH = define(10231)
    val SSL_VERIFYSTATUS = define(232)
    val SSL_FALSESTART = define(233)
    val PATH_AS_IS = define(234)
    val PROXY_SERVICE_NAME = define(10235)
    val SERVICE_NAME = define(10236)
    val PIPEWAIT = define(237)
    val DEFAULT_PROTOCOL = define(10238)
    val STREAM_WEIGHT = define(239)
    val STREAM_DEPENDS = define(10240)
    val STREAM_DEPENDS_E = define(10241)
    val TFTP_NO_OPTIONS = define(242)
    val CONNECT_TO = define(10243)
    val TCP_FASTOPEN = define(244)
    val KEEP_SENDING_ON_ERROR = define(245)
    val PROXY_CAINFO = define(10246)
    val PROXY_CAPATH = define(10247)
    val PROXY_SSL_VERIFYPEER = define(248)
    val PROXY_SSL_VERIFYHOST = define(249)
    val PROXY_SSLVERSION = define(250)
    val PROXY_TLSAUTH_USERNAME = define(10251)
    val PROXY_TLSAUTH_PASSWORD = define(10252)
    val PROXY_TLSAUTH_TYPE = define(10253)
    val PROXY_SSLCERT = define(10254)
    val PROXY_SSLCERTTYPE = define(10255)
    val PROXY_SSLKEY = define(10256)
    val PROXY_SSLKEYTYPE = define(10257)
    val PROXY_KEYPASSWD = define(10258)
    val PROXY_SSL_CIPHER_LIST = define(10259)
    val PROXY_CRLFILE = define(10260)
    val PROXY_SSL_OPTIONS = define(261)
    val PRE_PROXY = define(10262)
    val PROXY_PINNEDPUBLICKEY = define(10263)
    val ABSTRACT_UNIX_SOCKET = define(10264)
    val SUPPRESS_CONNECT_HEADERS = define(265)
    val REQUEST_TARGET = define(10266)
    val SOCKS5_AUTH = define(267)
    val SSH_COMPRESSION = define(268)
    val MIMEPOST = define(10269)
    val TIMEVALUE_LARGE = define(30270)
    val HAPPY_EYEBALLS_TIMEOUT_MS = define(271)
    val RESOLVER_START_FUNCTION = define(20272)
    val RESOLVER_START_DATA = define(10273)
    val HAPROXYPROTOCOL = define(274)
    val DNS_SHUFFLE_ADDRESSES = define(275)
    val TLS13_CIPHERS = define(10276)
    val PROXY_TLS13_CIPHERS = define(10277)
    val DISALLOW_USERNAME_IN_URL = define(278)
    val DOH_URL = define(10279)
    val UPLOAD_BUFFERSIZE = define(280)
    val UPKEEP_INTERVAL_MS = define(281)
    val CURLU = define(10282)
    val TRAILERFUNCTION = define(20283)
    val TRAILERDATA = define(10284)
    val HTTP09_ALLOWED = define(285)
    val ALTSVC_CTRL = define(286)
    val ALTSVC = define(10287)
    val MAXAGE_CONN = define(288)
    val SASL_AUTHZID = define(10289)
    val MAIL_RCPT_ALLOWFAILS = define(290)
    val SSLCERT_BLOB = define(40291)
    val SSLKEY_BLOB = define(40292)
    val PROXY_SSLCERT_BLOB = define(40293)
    val PROXY_SSLKEY_BLOB = define(40294)
    val ISSUERCERT_BLOB = define(40295)
    val PROXY_ISSUERCERT = define(10296)
    val PROXY_ISSUERCERT_BLOB = define(40297)
    val SSL_EC_CURVES = define(10298)
    val HSTS_CTRL = define(299)
    val HSTS = define(10300)
    val HSTSREADFUNCTION = define(20301)
    val HSTSREADDATA = define(10302)
    val HSTSWRITEFUNCTION = define(20303)
    val HSTSWRITEDATA = define(10304)
    val AWS_SIGV4 = define(10305)
    val DOH_SSL_VERIFYPEER = define(306)
    val DOH_SSL_VERIFYHOST = define(307)
    val DOH_SSL_VERIFYSTATUS = define(308)
    val CAINFO_BLOB = define(40309)
    val PROXY_CAINFO_BLOB = define(40310)
    val SSH_HOST_PUBLIC_KEY_SHA256 = define(10311)
    val PREREQFUNCTION = define(20312)
    val PREREQDATA = define(10313)
    val MAXLIFETIME_CONN = define(314)
    val MIME_OPTIONS = define(315)
    val SSH_HOSTKEYFUNCTION = define(20316)
    val SSH_HOSTKEYDATA = define(10317)
    val PROTOCOLS_STR = define(10318)
    val REDIR_PROTOCOLS_STR = define(10319)
    val WS_OPTIONS = define(320)
    val CA_CACHE_TIMEOUT = define(321)
    val QUICK_EXIT = define(322)
    val HAPROXY_CLIENT_IP = define(10323)
    val LASTENTRY = define(10324)

    inline def getName(inline value: CurlOption): Option[String] =
      inline value match
        case WRITEDATA                  => Some("CURLOPT_WRITEDATA")
        case URL                        => Some("CURLOPT_URL")
        case PORT                       => Some("CURLOPT_PORT")
        case PROXY                      => Some("CURLOPT_PROXY")
        case USERPWD                    => Some("CURLOPT_USERPWD")
        case PROXYUSERPWD               => Some("CURLOPT_PROXYUSERPWD")
        case RANGE                      => Some("CURLOPT_RANGE")
        case READDATA                   => Some("CURLOPT_READDATA")
        case ERRORBUFFER                => Some("CURLOPT_ERRORBUFFER")
        case WRITEFUNCTION              => Some("CURLOPT_WRITEFUNCTION")
        case READFUNCTION               => Some("CURLOPT_READFUNCTION")
        case TIMEOUT                    => Some("CURLOPT_TIMEOUT")
        case INFILESIZE                 => Some("CURLOPT_INFILESIZE")
        case POSTFIELDS                 => Some("CURLOPT_POSTFIELDS")
        case REFERER                    => Some("CURLOPT_REFERER")
        case FTPPORT                    => Some("CURLOPT_FTPPORT")
        case USERAGENT                  => Some("CURLOPT_USERAGENT")
        case LOW_SPEED_LIMIT            => Some("CURLOPT_LOW_SPEED_LIMIT")
        case LOW_SPEED_TIME             => Some("CURLOPT_LOW_SPEED_TIME")
        case RESUME_FROM                => Some("CURLOPT_RESUME_FROM")
        case COOKIE                     => Some("CURLOPT_COOKIE")
        case HTTPHEADER                 => Some("CURLOPT_HTTPHEADER")
        case HTTPPOST                   => Some("CURLOPT_HTTPPOST")
        case SSLCERT                    => Some("CURLOPT_SSLCERT")
        case KEYPASSWD                  => Some("CURLOPT_KEYPASSWD")
        case CRLF                       => Some("CURLOPT_CRLF")
        case QUOTE                      => Some("CURLOPT_QUOTE")
        case HEADERDATA                 => Some("CURLOPT_HEADERDATA")
        case COOKIEFILE                 => Some("CURLOPT_COOKIEFILE")
        case SSLVERSION                 => Some("CURLOPT_SSLVERSION")
        case TIMECONDITION              => Some("CURLOPT_TIMECONDITION")
        case TIMEVALUE                  => Some("CURLOPT_TIMEVALUE")
        case CUSTOMREQUEST              => Some("CURLOPT_CUSTOMREQUEST")
        case STDERR                     => Some("CURLOPT_STDERR")
        case POSTQUOTE                  => Some("CURLOPT_POSTQUOTE")
        case OBSOLETE40                 => Some("CURLOPT_OBSOLETE40")
        case VERBOSE                    => Some("CURLOPT_VERBOSE")
        case HEADER                     => Some("CURLOPT_HEADER")
        case NOPROGRESS                 => Some("CURLOPT_NOPROGRESS")
        case NOBODY                     => Some("CURLOPT_NOBODY")
        case FAILONERROR                => Some("CURLOPT_FAILONERROR")
        case UPLOAD                     => Some("CURLOPT_UPLOAD")
        case POST                       => Some("CURLOPT_POST")
        case DIRLISTONLY                => Some("CURLOPT_DIRLISTONLY")
        case APPEND                     => Some("CURLOPT_APPEND")
        case NETRC                      => Some("CURLOPT_NETRC")
        case FOLLOWLOCATION             => Some("CURLOPT_FOLLOWLOCATION")
        case TRANSFERTEXT               => Some("CURLOPT_TRANSFERTEXT")
        case PUT                        => Some("CURLOPT_PUT")
        case PROGRESSFUNCTION           => Some("CURLOPT_PROGRESSFUNCTION")
        case XFERINFODATA               => Some("CURLOPT_XFERINFODATA")
        case AUTOREFERER                => Some("CURLOPT_AUTOREFERER")
        case PROXYPORT                  => Some("CURLOPT_PROXYPORT")
        case POSTFIELDSIZE              => Some("CURLOPT_POSTFIELDSIZE")
        case HTTPPROXYTUNNEL            => Some("CURLOPT_HTTPPROXYTUNNEL")
        case INTERFACE                  => Some("CURLOPT_INTERFACE")
        case KRBLEVEL                   => Some("CURLOPT_KRBLEVEL")
        case SSL_VERIFYPEER             => Some("CURLOPT_SSL_VERIFYPEER")
        case CAINFO                     => Some("CURLOPT_CAINFO")
        case MAXREDIRS                  => Some("CURLOPT_MAXREDIRS")
        case FILETIME                   => Some("CURLOPT_FILETIME")
        case TELNETOPTIONS              => Some("CURLOPT_TELNETOPTIONS")
        case MAXCONNECTS                => Some("CURLOPT_MAXCONNECTS")
        case OBSOLETE72                 => Some("CURLOPT_OBSOLETE72")
        case FRESH_CONNECT              => Some("CURLOPT_FRESH_CONNECT")
        case FORBID_REUSE               => Some("CURLOPT_FORBID_REUSE")
        case RANDOM_FILE                => Some("CURLOPT_RANDOM_FILE")
        case EGDSOCKET                  => Some("CURLOPT_EGDSOCKET")
        case CONNECTTIMEOUT             => Some("CURLOPT_CONNECTTIMEOUT")
        case HEADERFUNCTION             => Some("CURLOPT_HEADERFUNCTION")
        case HTTPGET                    => Some("CURLOPT_HTTPGET")
        case SSL_VERIFYHOST             => Some("CURLOPT_SSL_VERIFYHOST")
        case COOKIEJAR                  => Some("CURLOPT_COOKIEJAR")
        case SSL_CIPHER_LIST            => Some("CURLOPT_SSL_CIPHER_LIST")
        case HTTP_VERSION               => Some("CURLOPT_HTTP_VERSION")
        case FTP_USE_EPSV               => Some("CURLOPT_FTP_USE_EPSV")
        case SSLCERTTYPE                => Some("CURLOPT_SSLCERTTYPE")
        case SSLKEY                     => Some("CURLOPT_SSLKEY")
        case SSLKEYTYPE                 => Some("CURLOPT_SSLKEYTYPE")
        case SSLENGINE                  => Some("CURLOPT_SSLENGINE")
        case SSLENGINE_DEFAULT          => Some("CURLOPT_SSLENGINE_DEFAULT")
        case DNS_USE_GLOBAL_CACHE       => Some("CURLOPT_DNS_USE_GLOBAL_CACHE")
        case DNS_CACHE_TIMEOUT          => Some("CURLOPT_DNS_CACHE_TIMEOUT")
        case PREQUOTE                   => Some("CURLOPT_PREQUOTE")
        case DEBUGFUNCTION              => Some("CURLOPT_DEBUGFUNCTION")
        case DEBUGDATA                  => Some("CURLOPT_DEBUGDATA")
        case COOKIESESSION              => Some("CURLOPT_COOKIESESSION")
        case CAPATH                     => Some("CURLOPT_CAPATH")
        case BUFFERSIZE                 => Some("CURLOPT_BUFFERSIZE")
        case NOSIGNAL                   => Some("CURLOPT_NOSIGNAL")
        case SHARE                      => Some("CURLOPT_SHARE")
        case PROXYTYPE                  => Some("CURLOPT_PROXYTYPE")
        case ACCEPT_ENCODING            => Some("CURLOPT_ACCEPT_ENCODING")
        case PRIVATE                    => Some("CURLOPT_PRIVATE")
        case HTTP200ALIASES             => Some("CURLOPT_HTTP200ALIASES")
        case UNRESTRICTED_AUTH          => Some("CURLOPT_UNRESTRICTED_AUTH")
        case FTP_USE_EPRT               => Some("CURLOPT_FTP_USE_EPRT")
        case HTTPAUTH                   => Some("CURLOPT_HTTPAUTH")
        case SSL_CTX_FUNCTION           => Some("CURLOPT_SSL_CTX_FUNCTION")
        case SSL_CTX_DATA               => Some("CURLOPT_SSL_CTX_DATA")
        case FTP_CREATE_MISSING_DIRS    => Some("CURLOPT_FTP_CREATE_MISSING_DIRS")
        case PROXYAUTH                  => Some("CURLOPT_PROXYAUTH")
        case SERVER_RESPONSE_TIMEOUT    => Some("CURLOPT_SERVER_RESPONSE_TIMEOUT")
        case IPRESOLVE                  => Some("CURLOPT_IPRESOLVE")
        case MAXFILESIZE                => Some("CURLOPT_MAXFILESIZE")
        case INFILESIZE_LARGE           => Some("CURLOPT_INFILESIZE_LARGE")
        case RESUME_FROM_LARGE          => Some("CURLOPT_RESUME_FROM_LARGE")
        case MAXFILESIZE_LARGE          => Some("CURLOPT_MAXFILESIZE_LARGE")
        case NETRC_FILE                 => Some("CURLOPT_NETRC_FILE")
        case USE_SSL                    => Some("CURLOPT_USE_SSL")
        case POSTFIELDSIZE_LARGE        => Some("CURLOPT_POSTFIELDSIZE_LARGE")
        case TCP_NODELAY                => Some("CURLOPT_TCP_NODELAY")
        case FTPSSLAUTH                 => Some("CURLOPT_FTPSSLAUTH")
        case IOCTLFUNCTION              => Some("CURLOPT_IOCTLFUNCTION")
        case IOCTLDATA                  => Some("CURLOPT_IOCTLDATA")
        case FTP_ACCOUNT                => Some("CURLOPT_FTP_ACCOUNT")
        case COOKIELIST                 => Some("CURLOPT_COOKIELIST")
        case IGNORE_CONTENT_LENGTH      => Some("CURLOPT_IGNORE_CONTENT_LENGTH")
        case FTP_SKIP_PASV_IP           => Some("CURLOPT_FTP_SKIP_PASV_IP")
        case FTP_FILEMETHOD             => Some("CURLOPT_FTP_FILEMETHOD")
        case LOCALPORT                  => Some("CURLOPT_LOCALPORT")
        case LOCALPORTRANGE             => Some("CURLOPT_LOCALPORTRANGE")
        case CONNECT_ONLY               => Some("CURLOPT_CONNECT_ONLY")
        case CONV_FROM_NETWORK_FUNCTION => Some("CURLOPT_CONV_FROM_NETWORK_FUNCTION")
        case CONV_TO_NETWORK_FUNCTION   => Some("CURLOPT_CONV_TO_NETWORK_FUNCTION")
        case CONV_FROM_UTF8_FUNCTION    => Some("CURLOPT_CONV_FROM_UTF8_FUNCTION")
        case MAX_SEND_SPEED_LARGE       => Some("CURLOPT_MAX_SEND_SPEED_LARGE")
        case MAX_RECV_SPEED_LARGE       => Some("CURLOPT_MAX_RECV_SPEED_LARGE")
        case FTP_ALTERNATIVE_TO_USER    => Some("CURLOPT_FTP_ALTERNATIVE_TO_USER")
        case SOCKOPTFUNCTION            => Some("CURLOPT_SOCKOPTFUNCTION")
        case SOCKOPTDATA                => Some("CURLOPT_SOCKOPTDATA")
        case SSL_SESSIONID_CACHE        => Some("CURLOPT_SSL_SESSIONID_CACHE")
        case SSH_AUTH_TYPES             => Some("CURLOPT_SSH_AUTH_TYPES")
        case SSH_PUBLIC_KEYFILE         => Some("CURLOPT_SSH_PUBLIC_KEYFILE")
        case SSH_PRIVATE_KEYFILE        => Some("CURLOPT_SSH_PRIVATE_KEYFILE")
        case FTP_SSL_CCC                => Some("CURLOPT_FTP_SSL_CCC")
        case TIMEOUT_MS                 => Some("CURLOPT_TIMEOUT_MS")
        case CONNECTTIMEOUT_MS          => Some("CURLOPT_CONNECTTIMEOUT_MS")
        case HTTP_TRANSFER_DECODING     => Some("CURLOPT_HTTP_TRANSFER_DECODING")
        case HTTP_CONTENT_DECODING      => Some("CURLOPT_HTTP_CONTENT_DECODING")
        case NEW_FILE_PERMS             => Some("CURLOPT_NEW_FILE_PERMS")
        case NEW_DIRECTORY_PERMS        => Some("CURLOPT_NEW_DIRECTORY_PERMS")
        case POSTREDIR                  => Some("CURLOPT_POSTREDIR")
        case SSH_HOST_PUBLIC_KEY_MD5    => Some("CURLOPT_SSH_HOST_PUBLIC_KEY_MD5")
        case OPENSOCKETFUNCTION         => Some("CURLOPT_OPENSOCKETFUNCTION")
        case OPENSOCKETDATA             => Some("CURLOPT_OPENSOCKETDATA")
        case COPYPOSTFIELDS             => Some("CURLOPT_COPYPOSTFIELDS")
        case PROXY_TRANSFER_MODE        => Some("CURLOPT_PROXY_TRANSFER_MODE")
        case SEEKFUNCTION               => Some("CURLOPT_SEEKFUNCTION")
        case SEEKDATA                   => Some("CURLOPT_SEEKDATA")
        case CRLFILE                    => Some("CURLOPT_CRLFILE")
        case ISSUERCERT                 => Some("CURLOPT_ISSUERCERT")
        case ADDRESS_SCOPE              => Some("CURLOPT_ADDRESS_SCOPE")
        case CERTINFO                   => Some("CURLOPT_CERTINFO")
        case USERNAME                   => Some("CURLOPT_USERNAME")
        case PASSWORD                   => Some("CURLOPT_PASSWORD")
        case PROXYUSERNAME              => Some("CURLOPT_PROXYUSERNAME")
        case PROXYPASSWORD              => Some("CURLOPT_PROXYPASSWORD")
        case NOPROXY                    => Some("CURLOPT_NOPROXY")
        case TFTP_BLKSIZE               => Some("CURLOPT_TFTP_BLKSIZE")
        case SOCKS5_GSSAPI_SERVICE      => Some("CURLOPT_SOCKS5_GSSAPI_SERVICE")
        case SOCKS5_GSSAPI_NEC          => Some("CURLOPT_SOCKS5_GSSAPI_NEC")
        case PROTOCOLS                  => Some("CURLOPT_PROTOCOLS")
        case REDIR_PROTOCOLS            => Some("CURLOPT_REDIR_PROTOCOLS")
        case SSH_KNOWNHOSTS             => Some("CURLOPT_SSH_KNOWNHOSTS")
        case SSH_KEYFUNCTION            => Some("CURLOPT_SSH_KEYFUNCTION")
        case SSH_KEYDATA                => Some("CURLOPT_SSH_KEYDATA")
        case MAIL_FROM                  => Some("CURLOPT_MAIL_FROM")
        case MAIL_RCPT                  => Some("CURLOPT_MAIL_RCPT")
        case FTP_USE_PRET               => Some("CURLOPT_FTP_USE_PRET")
        case RTSP_REQUEST               => Some("CURLOPT_RTSP_REQUEST")
        case RTSP_SESSION_ID            => Some("CURLOPT_RTSP_SESSION_ID")
        case RTSP_STREAM_URI            => Some("CURLOPT_RTSP_STREAM_URI")
        case RTSP_TRANSPORT             => Some("CURLOPT_RTSP_TRANSPORT")
        case RTSP_CLIENT_CSEQ           => Some("CURLOPT_RTSP_CLIENT_CSEQ")
        case RTSP_SERVER_CSEQ           => Some("CURLOPT_RTSP_SERVER_CSEQ")
        case INTERLEAVEDATA             => Some("CURLOPT_INTERLEAVEDATA")
        case INTERLEAVEFUNCTION         => Some("CURLOPT_INTERLEAVEFUNCTION")
        case WILDCARDMATCH              => Some("CURLOPT_WILDCARDMATCH")
        case CHUNK_BGN_FUNCTION         => Some("CURLOPT_CHUNK_BGN_FUNCTION")
        case CHUNK_END_FUNCTION         => Some("CURLOPT_CHUNK_END_FUNCTION")
        case FNMATCH_FUNCTION           => Some("CURLOPT_FNMATCH_FUNCTION")
        case CHUNK_DATA                 => Some("CURLOPT_CHUNK_DATA")
        case FNMATCH_DATA               => Some("CURLOPT_FNMATCH_DATA")
        case RESOLVE                    => Some("CURLOPT_RESOLVE")
        case TLSAUTH_USERNAME           => Some("CURLOPT_TLSAUTH_USERNAME")
        case TLSAUTH_PASSWORD           => Some("CURLOPT_TLSAUTH_PASSWORD")
        case TLSAUTH_TYPE               => Some("CURLOPT_TLSAUTH_TYPE")
        case TRANSFER_ENCODING          => Some("CURLOPT_TRANSFER_ENCODING")
        case CLOSESOCKETFUNCTION        => Some("CURLOPT_CLOSESOCKETFUNCTION")
        case CLOSESOCKETDATA            => Some("CURLOPT_CLOSESOCKETDATA")
        case GSSAPI_DELEGATION          => Some("CURLOPT_GSSAPI_DELEGATION")
        case DNS_SERVERS                => Some("CURLOPT_DNS_SERVERS")
        case ACCEPTTIMEOUT_MS           => Some("CURLOPT_ACCEPTTIMEOUT_MS")
        case TCP_KEEPALIVE              => Some("CURLOPT_TCP_KEEPALIVE")
        case TCP_KEEPIDLE               => Some("CURLOPT_TCP_KEEPIDLE")
        case TCP_KEEPINTVL              => Some("CURLOPT_TCP_KEEPINTVL")
        case SSL_OPTIONS                => Some("CURLOPT_SSL_OPTIONS")
        case MAIL_AUTH                  => Some("CURLOPT_MAIL_AUTH")
        case SASL_IR                    => Some("CURLOPT_SASL_IR")
        case XFERINFOFUNCTION           => Some("CURLOPT_XFERINFOFUNCTION")
        case XOAUTH2_BEARER             => Some("CURLOPT_XOAUTH2_BEARER")
        case DNS_INTERFACE              => Some("CURLOPT_DNS_INTERFACE")
        case DNS_LOCAL_IP4              => Some("CURLOPT_DNS_LOCAL_IP4")
        case DNS_LOCAL_IP6              => Some("CURLOPT_DNS_LOCAL_IP6")
        case LOGIN_OPTIONS              => Some("CURLOPT_LOGIN_OPTIONS")
        case SSL_ENABLE_NPN             => Some("CURLOPT_SSL_ENABLE_NPN")
        case SSL_ENABLE_ALPN            => Some("CURLOPT_SSL_ENABLE_ALPN")
        case EXPECT_100_TIMEOUT_MS      => Some("CURLOPT_EXPECT_100_TIMEOUT_MS")
        case PROXYHEADER                => Some("CURLOPT_PROXYHEADER")
        case HEADEROPT                  => Some("CURLOPT_HEADEROPT")
        case PINNEDPUBLICKEY            => Some("CURLOPT_PINNEDPUBLICKEY")
        case UNIX_SOCKET_PATH           => Some("CURLOPT_UNIX_SOCKET_PATH")
        case SSL_VERIFYSTATUS           => Some("CURLOPT_SSL_VERIFYSTATUS")
        case SSL_FALSESTART             => Some("CURLOPT_SSL_FALSESTART")
        case PATH_AS_IS                 => Some("CURLOPT_PATH_AS_IS")
        case PROXY_SERVICE_NAME         => Some("CURLOPT_PROXY_SERVICE_NAME")
        case SERVICE_NAME               => Some("CURLOPT_SERVICE_NAME")
        case PIPEWAIT                   => Some("CURLOPT_PIPEWAIT")
        case DEFAULT_PROTOCOL           => Some("CURLOPT_DEFAULT_PROTOCOL")
        case STREAM_WEIGHT              => Some("CURLOPT_STREAM_WEIGHT")
        case STREAM_DEPENDS             => Some("CURLOPT_STREAM_DEPENDS")
        case STREAM_DEPENDS_E           => Some("CURLOPT_STREAM_DEPENDS_E")
        case TFTP_NO_OPTIONS            => Some("CURLOPT_TFTP_NO_OPTIONS")
        case CONNECT_TO                 => Some("CURLOPT_CONNECT_TO")
        case TCP_FASTOPEN               => Some("CURLOPT_TCP_FASTOPEN")
        case KEEP_SENDING_ON_ERROR      => Some("CURLOPT_KEEP_SENDING_ON_ERROR")
        case PROXY_CAINFO               => Some("CURLOPT_PROXY_CAINFO")
        case PROXY_CAPATH               => Some("CURLOPT_PROXY_CAPATH")
        case PROXY_SSL_VERIFYPEER       => Some("CURLOPT_PROXY_SSL_VERIFYPEER")
        case PROXY_SSL_VERIFYHOST       => Some("CURLOPT_PROXY_SSL_VERIFYHOST")
        case PROXY_SSLVERSION           => Some("CURLOPT_PROXY_SSLVERSION")
        case PROXY_TLSAUTH_USERNAME     => Some("CURLOPT_PROXY_TLSAUTH_USERNAME")
        case PROXY_TLSAUTH_PASSWORD     => Some("CURLOPT_PROXY_TLSAUTH_PASSWORD")
        case PROXY_TLSAUTH_TYPE         => Some("CURLOPT_PROXY_TLSAUTH_TYPE")
        case PROXY_SSLCERT              => Some("CURLOPT_PROXY_SSLCERT")
        case PROXY_SSLCERTTYPE          => Some("CURLOPT_PROXY_SSLCERTTYPE")
        case PROXY_SSLKEY               => Some("CURLOPT_PROXY_SSLKEY")
        case PROXY_SSLKEYTYPE           => Some("CURLOPT_PROXY_SSLKEYTYPE")
        case PROXY_KEYPASSWD            => Some("CURLOPT_PROXY_KEYPASSWD")
        case PROXY_SSL_CIPHER_LIST      => Some("CURLOPT_PROXY_SSL_CIPHER_LIST")
        case PROXY_CRLFILE              => Some("CURLOPT_PROXY_CRLFILE")
        case PROXY_SSL_OPTIONS          => Some("CURLOPT_PROXY_SSL_OPTIONS")
        case PRE_PROXY                  => Some("CURLOPT_PRE_PROXY")
        case PROXY_PINNEDPUBLICKEY      => Some("CURLOPT_PROXY_PINNEDPUBLICKEY")
        case ABSTRACT_UNIX_SOCKET       => Some("CURLOPT_ABSTRACT_UNIX_SOCKET")
        case SUPPRESS_CONNECT_HEADERS   => Some("CURLOPT_SUPPRESS_CONNECT_HEADERS")
        case REQUEST_TARGET             => Some("CURLOPT_REQUEST_TARGET")
        case SOCKS5_AUTH                => Some("CURLOPT_SOCKS5_AUTH")
        case SSH_COMPRESSION            => Some("CURLOPT_SSH_COMPRESSION")
        case MIMEPOST                   => Some("CURLOPT_MIMEPOST")
        case TIMEVALUE_LARGE            => Some("CURLOPT_TIMEVALUE_LARGE")
        case HAPPY_EYEBALLS_TIMEOUT_MS  => Some("CURLOPT_HAPPY_EYEBALLS_TIMEOUT_MS")
        case RESOLVER_START_FUNCTION    => Some("CURLOPT_RESOLVER_START_FUNCTION")
        case RESOLVER_START_DATA        => Some("CURLOPT_RESOLVER_START_DATA")
        case HAPROXYPROTOCOL            => Some("CURLOPT_HAPROXYPROTOCOL")
        case DNS_SHUFFLE_ADDRESSES      => Some("CURLOPT_DNS_SHUFFLE_ADDRESSES")
        case TLS13_CIPHERS              => Some("CURLOPT_TLS13_CIPHERS")
        case PROXY_TLS13_CIPHERS        => Some("CURLOPT_PROXY_TLS13_CIPHERS")
        case DISALLOW_USERNAME_IN_URL   => Some("CURLOPT_DISALLOW_USERNAME_IN_URL")
        case DOH_URL                    => Some("CURLOPT_DOH_URL")
        case UPLOAD_BUFFERSIZE          => Some("CURLOPT_UPLOAD_BUFFERSIZE")
        case UPKEEP_INTERVAL_MS         => Some("CURLOPT_UPKEEP_INTERVAL_MS")
        case CURLU                      => Some("CURLOPT_CURLU")
        case TRAILERFUNCTION            => Some("CURLOPT_TRAILERFUNCTION")
        case TRAILERDATA                => Some("CURLOPT_TRAILERDATA")
        case HTTP09_ALLOWED             => Some("CURLOPT_HTTP09_ALLOWED")
        case ALTSVC_CTRL                => Some("CURLOPT_ALTSVC_CTRL")
        case ALTSVC                     => Some("CURLOPT_ALTSVC")
        case MAXAGE_CONN                => Some("CURLOPT_MAXAGE_CONN")
        case SASL_AUTHZID               => Some("CURLOPT_SASL_AUTHZID")
        case MAIL_RCPT_ALLOWFAILS       => Some("CURLOPT_MAIL_RCPT_ALLOWFAILS")
        case SSLCERT_BLOB               => Some("CURLOPT_SSLCERT_BLOB")
        case SSLKEY_BLOB                => Some("CURLOPT_SSLKEY_BLOB")
        case PROXY_SSLCERT_BLOB         => Some("CURLOPT_PROXY_SSLCERT_BLOB")
        case PROXY_SSLKEY_BLOB          => Some("CURLOPT_PROXY_SSLKEY_BLOB")
        case ISSUERCERT_BLOB            => Some("CURLOPT_ISSUERCERT_BLOB")
        case PROXY_ISSUERCERT           => Some("CURLOPT_PROXY_ISSUERCERT")
        case PROXY_ISSUERCERT_BLOB      => Some("CURLOPT_PROXY_ISSUERCERT_BLOB")
        case SSL_EC_CURVES              => Some("CURLOPT_SSL_EC_CURVES")
        case HSTS_CTRL                  => Some("CURLOPT_HSTS_CTRL")
        case HSTS                       => Some("CURLOPT_HSTS")
        case HSTSREADFUNCTION           => Some("CURLOPT_HSTSREADFUNCTION")
        case HSTSREADDATA               => Some("CURLOPT_HSTSREADDATA")
        case HSTSWRITEFUNCTION          => Some("CURLOPT_HSTSWRITEFUNCTION")
        case HSTSWRITEDATA              => Some("CURLOPT_HSTSWRITEDATA")
        case AWS_SIGV4                  => Some("CURLOPT_AWS_SIGV4")
        case DOH_SSL_VERIFYPEER         => Some("CURLOPT_DOH_SSL_VERIFYPEER")
        case DOH_SSL_VERIFYHOST         => Some("CURLOPT_DOH_SSL_VERIFYHOST")
        case DOH_SSL_VERIFYSTATUS       => Some("CURLOPT_DOH_SSL_VERIFYSTATUS")
        case CAINFO_BLOB                => Some("CURLOPT_CAINFO_BLOB")
        case PROXY_CAINFO_BLOB          => Some("CURLOPT_PROXY_CAINFO_BLOB")
        case SSH_HOST_PUBLIC_KEY_SHA256 => Some("CURLOPT_SSH_HOST_PUBLIC_KEY_SHA256")
        case PREREQFUNCTION             => Some("CURLOPT_PREREQFUNCTION")
        case PREREQDATA                 => Some("CURLOPT_PREREQDATA")
        case MAXLIFETIME_CONN           => Some("CURLOPT_MAXLIFETIME_CONN")
        case MIME_OPTIONS               => Some("CURLOPT_MIME_OPTIONS")
        case SSH_HOSTKEYFUNCTION        => Some("CURLOPT_SSH_HOSTKEYFUNCTION")
        case SSH_HOSTKEYDATA            => Some("CURLOPT_SSH_HOSTKEYDATA")
        case PROTOCOLS_STR              => Some("CURLOPT_PROTOCOLS_STR")
        case REDIR_PROTOCOLS_STR        => Some("CURLOPT_REDIR_PROTOCOLS_STR")
        case WS_OPTIONS                 => Some("CURLOPT_WS_OPTIONS")
        case CA_CACHE_TIMEOUT           => Some("CURLOPT_CA_CACHE_TIMEOUT")
        case QUICK_EXIT                 => Some("CURLOPT_QUICK_EXIT")
        case HAPROXY_CLIENT_IP          => Some("CURLOPT_HAPROXY_CLIENT_IP")
        case LASTENTRY                  => Some("CURLOPT_LASTENTRY")
        case _                          => None
    extension (a: CurlOption)
      inline def &(b: CurlOption): CurlOption = a & b
      inline def |(b: CurlOption): CurlOption = a | b
      inline def is(b: CurlOption): Boolean = (a & b) == b

  /**
   * Below here follows defines for the CURLOPT_IPRESOLVE option. If a host name resolves addresses
   * using more than one IP protocol version, this option might be handy to force libcurl to use a
   * specific IP version.
   */
  opaque type CurlOpt_IpResolve = UInt
  object CurlOpt_IpResolve:
    given Tag[CurlOption] = Tag.UInt

    inline def define(inline a: Long): CurlOption = a.toUInt

    /**
     * default, uses addresses to all IP versions that your system allows
     */
    val WHATEVER = define(0)

    /** uses only IPv4 addresses/connections */
    val V4 = define(1)

    /** uses only IPv6 addresses/connections */
    val V6 = define(2)

  /** These enums are for use with the `CURLOPT_HTTP_VERSION` option. */
  opaque type CurlOpt_HttpVersion = UInt
  object CurlOpt_HttpVersion:
    given Tag[CurlOpt_HttpVersion] = Tag.UInt

    inline def define(inline a: Long): CurlOpt_HttpVersion = a.toUInt

    /**
     * setting this means we don't care, and that we'd like the library to choose the best possible
     * for us!
     */
    val VERSION_NONE: UInt = 0.toUInt

    /** please use HTTP 1.0 in the request */
    val VERSION_1_0: UInt = 1.toUInt

    /** please use HTTP 1.1 in the request */
    val VERSION_1_1: UInt = 2.toUInt

    /** please use HTTP 2 in the request */
    val VERSION_2_0: UInt = 3.toUInt

    /** use version 2 for HTTPS, version 1.1 for HTTP */
    val VERSION_2TLS: UInt = 4.toUInt

    /**
     * please use HTTP 2 without HTTP/1.1 Upgrade
     */
    val VERSION_2_PRIOR_KNOWLEDGE: UInt = 5.toUInt

    /**
     * Use HTTP/3, fallback to HTTP/2 or HTTP/1 if needed. For HTTPS only. For HTTP, this option
     * makes libcurl return error.
     */
    val VERSION_3: UInt = 30.toUInt

    /**
     * Use HTTP/3 without fallback. For HTTPS only. For HTTP, this makes libcurl return error.
     */
    val VERSION_3ONLY: UInt = 31.toUInt

    /**
     * *ILLEGAL* http version
     */
    val VERSION_LAST: UInt = 32.toUInt

  //
  // include <curl/options.h>
  //

  @name("curl_easytype")
  opaque type CurlEasyType = UInt
  object CurlEasyType:
    given Tag[CurlEasyType] = Tag.UInt

    inline def define(inline a: Long): CurlEasyType = a.toUInt

    /* long (a range of values) */
    val LONG = define(0)
    /* long (a defined set or bitmask) */
    val VALUES = define(1)
    /* curl_off_t (a range of values) */
    val OFF_T = define(2)
    /* pointer (void *) */
    val OBJECT = define(3)
    /* pointer (char * to null-terminated buffer) */
    val STRING = define(4)
    /* pointer (struct curl_slist *) */
    val SLIST = define(5)
    /* pointer (void * passed as-is to a callback) */
    val CBPTR = define(6)
    /* blob (struct curl_blob *) */
    val BLOB = define(7)
    /* function pointer */
    val FUNCTION = define(8)

    inline def getName(inline value: CurlEasyType): Option[String] =
      inline value match
        case LONG     => Some("CURLOT_LONG")
        case VALUES   => Some("CURLOT_VALUES")
        case OFF_T    => Some("CURLOT_OFF_T")
        case OBJECT   => Some("CURLOT_OBJECT")
        case STRING   => Some("CURLOT_STRING")
        case SLIST    => Some("CURLOT_SLIST")
        case CBPTR    => Some("CURLOT_CBPTR")
        case BLOB     => Some("CURLOT_BLOB")
        case FUNCTION => Some("CURLOT_FUNCTION")
        case _        => None

    extension (a: CurlEasyType)
      inline def &(b: CurlEasyType): CurlEasyType = a & b
      inline def |(b: CurlEasyType): CurlEasyType = a | b
      inline def is(b: CurlEasyType): Boolean = (a & b) == b

  @name("curl_easyoption")
  opaque type CurlEasyOption = CStruct4[CString, CurlOption, CurlEasyType, UInt]
  object CurlEasyOption:
    given Tag[CurlEasyOption] =
      Tag.materializeCStruct4Tag[CString, CurlOption, CurlEasyType, UInt]

    def apply(name: CString, id: CurlOption, `type`: CurlEasyType, flags: UInt)(using
        Zone,
    ): Ptr[CurlEasyOption] =
      val ptr = alloc[CurlEasyOption](1)
      (!ptr).name = name
      (!ptr).id = id
      (!ptr).`type` = `type`
      (!ptr).flags = flags
      ptr

    extension (struct: CurlEasyOption)
      def name: CString = struct._1
      def name_=(value: CString): Unit = !struct.at1 = value
      def id: CurlOption = struct._2
      def id_=(value: CurlOption): Unit = !struct.at2 = value
      def `type`: CurlEasyType = struct._3
      def type_=(value: CurlEasyType): Unit = !struct.at3 = value
      def flags: UInt = struct._4
      def flags_=(value: UInt): Unit = !struct.at4 = value

  @name("curl_easy_option_by_id")
  def easyOptionById(id: CurlOption): Ptr[CurlEasyOption] = extern

  @name("curl_easy_option_by_name")
  def easyOptionByName(name: CString): Ptr[CurlEasyOption] = extern

  @name("curl_easy_option_next")
  def easyOptionNext(prev: Ptr[CurlEasyOption]): Ptr[CurlEasyOption] = extern
