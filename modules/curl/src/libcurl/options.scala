/**
 * Note:
 *
 * This file is manually aligned with codebase
 *
 * https://github.com/curl/curl/blob/d21e75a6ae0cda978e68b26579e5665a0a92ca0d/include/curl/options.h
 *
 * and all symbols are declared in the order they first appear.
 *
 * Please follow the same order when adding or updating symbols and revise the commit hash.
 */
package snhttp.experimental.libcurl

import scala.scalanative.posix.sys.socket
import scala.scalanative.unsafe.{Tag, Ptr, Zone, CString, CStruct4, CLong}
import scala.scalanative.unsafe.{alloc, extern, name}
import scala.scalanative.unsigned.*

import _type.{_BindgenEnumCLong, _BindgenEnumUInt}

@extern
object options:
  //
  // parts defined in <curl/curl.h>
  //

  /** bitmask defines for `CURLOPT_HEADEROPT` */
  @name("CURLOPT_HEADEROPT")
  opaque type CurlOptHeaderOpt = CLong
  object CurlOptHeaderOpt extends _BindgenEnumCLong[CurlOptHeaderOpt]:
    private inline def define(inline v: Int): CurlOptHeaderOpt = v

    val UNIFIED = define(0)
    val SEPARATE = define(1 << 0)

  /* bits for the `CURLOPT_ALTSVC_CTRL` option */
  @name("CURLOPT_ALTSVC_CTRL")
  opaque type CurlOptAltSvcCtrl = CLong
  object CurlOptAltSvcCtrl extends _BindgenEnumCLong[CurlOptAltSvcCtrl]:
    private inline def define(inline v: Int): CurlOptAltSvcCtrl = v

    val READONLYFILE = define(1 << 2)
    val H1 = define(1 << 3)
    val H2 = define(1 << 4)
    val H3 = define(1 << 5)

  @name("CURLoption")
  opaque type CurlOption = CLong
  object CurlOption extends _BindgenEnumCLong[CurlOption]:
    private inline def define(inline a: Int): CurlOption = a

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

    extension (value: CurlOption)
      def getName: String =
        value match
          case WRITEDATA                  => "CURLOPT_WRITEDATA"
          case URL                        => "CURLOPT_URL"
          case PORT                       => "CURLOPT_PORT"
          case PROXY                      => "CURLOPT_PROXY"
          case USERPWD                    => "CURLOPT_USERPWD"
          case PROXYUSERPWD               => "CURLOPT_PROXYUSERPWD"
          case RANGE                      => "CURLOPT_RANGE"
          case READDATA                   => "CURLOPT_READDATA"
          case ERRORBUFFER                => "CURLOPT_ERRORBUFFER"
          case WRITEFUNCTION              => "CURLOPT_WRITEFUNCTION"
          case READFUNCTION               => "CURLOPT_READFUNCTION"
          case TIMEOUT                    => "CURLOPT_TIMEOUT"
          case INFILESIZE                 => "CURLOPT_INFILESIZE"
          case POSTFIELDS                 => "CURLOPT_POSTFIELDS"
          case REFERER                    => "CURLOPT_REFERER"
          case FTPPORT                    => "CURLOPT_FTPPORT"
          case USERAGENT                  => "CURLOPT_USERAGENT"
          case LOW_SPEED_LIMIT            => "CURLOPT_LOW_SPEED_LIMIT"
          case LOW_SPEED_TIME             => "CURLOPT_LOW_SPEED_TIME"
          case RESUME_FROM                => "CURLOPT_RESUME_FROM"
          case COOKIE                     => "CURLOPT_COOKIE"
          case HTTPHEADER                 => "CURLOPT_HTTPHEADER"
          case HTTPPOST                   => "CURLOPT_HTTPPOST"
          case SSLCERT                    => "CURLOPT_SSLCERT"
          case KEYPASSWD                  => "CURLOPT_KEYPASSWD"
          case CRLF                       => "CURLOPT_CRLF"
          case QUOTE                      => "CURLOPT_QUOTE"
          case HEADERDATA                 => "CURLOPT_HEADERDATA"
          case COOKIEFILE                 => "CURLOPT_COOKIEFILE"
          case SSLVERSION                 => "CURLOPT_SSLVERSION"
          case TIMECONDITION              => "CURLOPT_TIMECONDITION"
          case TIMEVALUE                  => "CURLOPT_TIMEVALUE"
          case CUSTOMREQUEST              => "CURLOPT_CUSTOMREQUEST"
          case STDERR                     => "CURLOPT_STDERR"
          case POSTQUOTE                  => "CURLOPT_POSTQUOTE"
          case OBSOLETE40                 => "CURLOPT_OBSOLETE40"
          case VERBOSE                    => "CURLOPT_VERBOSE"
          case HEADER                     => "CURLOPT_HEADER"
          case NOPROGRESS                 => "CURLOPT_NOPROGRESS"
          case NOBODY                     => "CURLOPT_NOBODY"
          case FAILONERROR                => "CURLOPT_FAILONERROR"
          case UPLOAD                     => "CURLOPT_UPLOAD"
          case POST                       => "CURLOPT_POST"
          case DIRLISTONLY                => "CURLOPT_DIRLISTONLY"
          case APPEND                     => "CURLOPT_APPEND"
          case NETRC                      => "CURLOPT_NETRC"
          case FOLLOWLOCATION             => "CURLOPT_FOLLOWLOCATION"
          case TRANSFERTEXT               => "CURLOPT_TRANSFERTEXT"
          case PUT                        => "CURLOPT_PUT"
          case PROGRESSFUNCTION           => "CURLOPT_PROGRESSFUNCTION"
          case XFERINFODATA               => "CURLOPT_XFERINFODATA"
          case AUTOREFERER                => "CURLOPT_AUTOREFERER"
          case PROXYPORT                  => "CURLOPT_PROXYPORT"
          case POSTFIELDSIZE              => "CURLOPT_POSTFIELDSIZE"
          case HTTPPROXYTUNNEL            => "CURLOPT_HTTPPROXYTUNNEL"
          case INTERFACE                  => "CURLOPT_INTERFACE"
          case KRBLEVEL                   => "CURLOPT_KRBLEVEL"
          case SSL_VERIFYPEER             => "CURLOPT_SSL_VERIFYPEER"
          case CAINFO                     => "CURLOPT_CAINFO"
          case MAXREDIRS                  => "CURLOPT_MAXREDIRS"
          case FILETIME                   => "CURLOPT_FILETIME"
          case TELNETOPTIONS              => "CURLOPT_TELNETOPTIONS"
          case MAXCONNECTS                => "CURLOPT_MAXCONNECTS"
          case OBSOLETE72                 => "CURLOPT_OBSOLETE72"
          case FRESH_CONNECT              => "CURLOPT_FRESH_CONNECT"
          case FORBID_REUSE               => "CURLOPT_FORBID_REUSE"
          case RANDOM_FILE                => "CURLOPT_RANDOM_FILE"
          case EGDSOCKET                  => "CURLOPT_EGDSOCKET"
          case CONNECTTIMEOUT             => "CURLOPT_CONNECTTIMEOUT"
          case HEADERFUNCTION             => "CURLOPT_HEADERFUNCTION"
          case HTTPGET                    => "CURLOPT_HTTPGET"
          case SSL_VERIFYHOST             => "CURLOPT_SSL_VERIFYHOST"
          case COOKIEJAR                  => "CURLOPT_COOKIEJAR"
          case SSL_CIPHER_LIST            => "CURLOPT_SSL_CIPHER_LIST"
          case HTTP_VERSION               => "CURLOPT_HTTP_VERSION"
          case FTP_USE_EPSV               => "CURLOPT_FTP_USE_EPSV"
          case SSLCERTTYPE                => "CURLOPT_SSLCERTTYPE"
          case SSLKEY                     => "CURLOPT_SSLKEY"
          case SSLKEYTYPE                 => "CURLOPT_SSLKEYTYPE"
          case SSLENGINE                  => "CURLOPT_SSLENGINE"
          case SSLENGINE_DEFAULT          => "CURLOPT_SSLENGINE_DEFAULT"
          case DNS_USE_GLOBAL_CACHE       => "CURLOPT_DNS_USE_GLOBAL_CACHE"
          case DNS_CACHE_TIMEOUT          => "CURLOPT_DNS_CACHE_TIMEOUT"
          case PREQUOTE                   => "CURLOPT_PREQUOTE"
          case DEBUGFUNCTION              => "CURLOPT_DEBUGFUNCTION"
          case DEBUGDATA                  => "CURLOPT_DEBUGDATA"
          case COOKIESESSION              => "CURLOPT_COOKIESESSION"
          case CAPATH                     => "CURLOPT_CAPATH"
          case BUFFERSIZE                 => "CURLOPT_BUFFERSIZE"
          case NOSIGNAL                   => "CURLOPT_NOSIGNAL"
          case SHARE                      => "CURLOPT_SHARE"
          case PROXYTYPE                  => "CURLOPT_PROXYTYPE"
          case ACCEPT_ENCODING            => "CURLOPT_ACCEPT_ENCODING"
          case PRIVATE                    => "CURLOPT_PRIVATE"
          case HTTP200ALIASES             => "CURLOPT_HTTP200ALIASES"
          case UNRESTRICTED_AUTH          => "CURLOPT_UNRESTRICTED_AUTH"
          case FTP_USE_EPRT               => "CURLOPT_FTP_USE_EPRT"
          case HTTPAUTH                   => "CURLOPT_HTTPAUTH"
          case SSL_CTX_FUNCTION           => "CURLOPT_SSL_CTX_FUNCTION"
          case SSL_CTX_DATA               => "CURLOPT_SSL_CTX_DATA"
          case FTP_CREATE_MISSING_DIRS    => "CURLOPT_FTP_CREATE_MISSING_DIRS"
          case PROXYAUTH                  => "CURLOPT_PROXYAUTH"
          case SERVER_RESPONSE_TIMEOUT    => "CURLOPT_SERVER_RESPONSE_TIMEOUT"
          case IPRESOLVE                  => "CURLOPT_IPRESOLVE"
          case MAXFILESIZE                => "CURLOPT_MAXFILESIZE"
          case INFILESIZE_LARGE           => "CURLOPT_INFILESIZE_LARGE"
          case RESUME_FROM_LARGE          => "CURLOPT_RESUME_FROM_LARGE"
          case MAXFILESIZE_LARGE          => "CURLOPT_MAXFILESIZE_LARGE"
          case NETRC_FILE                 => "CURLOPT_NETRC_FILE"
          case USE_SSL                    => "CURLOPT_USE_SSL"
          case POSTFIELDSIZE_LARGE        => "CURLOPT_POSTFIELDSIZE_LARGE"
          case TCP_NODELAY                => "CURLOPT_TCP_NODELAY"
          case FTPSSLAUTH                 => "CURLOPT_FTPSSLAUTH"
          case IOCTLFUNCTION              => "CURLOPT_IOCTLFUNCTION"
          case IOCTLDATA                  => "CURLOPT_IOCTLDATA"
          case FTP_ACCOUNT                => "CURLOPT_FTP_ACCOUNT"
          case COOKIELIST                 => "CURLOPT_COOKIELIST"
          case IGNORE_CONTENT_LENGTH      => "CURLOPT_IGNORE_CONTENT_LENGTH"
          case FTP_SKIP_PASV_IP           => "CURLOPT_FTP_SKIP_PASV_IP"
          case FTP_FILEMETHOD             => "CURLOPT_FTP_FILEMETHOD"
          case LOCALPORT                  => "CURLOPT_LOCALPORT"
          case LOCALPORTRANGE             => "CURLOPT_LOCALPORTRANGE"
          case CONNECT_ONLY               => "CURLOPT_CONNECT_ONLY"
          case CONV_FROM_NETWORK_FUNCTION => "CURLOPT_CONV_FROM_NETWORK_FUNCTION"
          case CONV_TO_NETWORK_FUNCTION   => "CURLOPT_CONV_TO_NETWORK_FUNCTION"
          case CONV_FROM_UTF8_FUNCTION    => "CURLOPT_CONV_FROM_UTF8_FUNCTION"
          case MAX_SEND_SPEED_LARGE       => "CURLOPT_MAX_SEND_SPEED_LARGE"
          case MAX_RECV_SPEED_LARGE       => "CURLOPT_MAX_RECV_SPEED_LARGE"
          case FTP_ALTERNATIVE_TO_USER    => "CURLOPT_FTP_ALTERNATIVE_TO_USER"
          case SOCKOPTFUNCTION            => "CURLOPT_SOCKOPTFUNCTION"
          case SOCKOPTDATA                => "CURLOPT_SOCKOPTDATA"
          case SSL_SESSIONID_CACHE        => "CURLOPT_SSL_SESSIONID_CACHE"
          case SSH_AUTH_TYPES             => "CURLOPT_SSH_AUTH_TYPES"
          case SSH_PUBLIC_KEYFILE         => "CURLOPT_SSH_PUBLIC_KEYFILE"
          case SSH_PRIVATE_KEYFILE        => "CURLOPT_SSH_PRIVATE_KEYFILE"
          case FTP_SSL_CCC                => "CURLOPT_FTP_SSL_CCC"
          case TIMEOUT_MS                 => "CURLOPT_TIMEOUT_MS"
          case CONNECTTIMEOUT_MS          => "CURLOPT_CONNECTTIMEOUT_MS"
          case HTTP_TRANSFER_DECODING     => "CURLOPT_HTTP_TRANSFER_DECODING"
          case HTTP_CONTENT_DECODING      => "CURLOPT_HTTP_CONTENT_DECODING"
          case NEW_FILE_PERMS             => "CURLOPT_NEW_FILE_PERMS"
          case NEW_DIRECTORY_PERMS        => "CURLOPT_NEW_DIRECTORY_PERMS"
          case POSTREDIR                  => "CURLOPT_POSTREDIR"
          case SSH_HOST_PUBLIC_KEY_MD5    => "CURLOPT_SSH_HOST_PUBLIC_KEY_MD5"
          case OPENSOCKETFUNCTION         => "CURLOPT_OPENSOCKETFUNCTION"
          case OPENSOCKETDATA             => "CURLOPT_OPENSOCKETDATA"
          case COPYPOSTFIELDS             => "CURLOPT_COPYPOSTFIELDS"
          case PROXY_TRANSFER_MODE        => "CURLOPT_PROXY_TRANSFER_MODE"
          case SEEKFUNCTION               => "CURLOPT_SEEKFUNCTION"
          case SEEKDATA                   => "CURLOPT_SEEKDATA"
          case CRLFILE                    => "CURLOPT_CRLFILE"
          case ISSUERCERT                 => "CURLOPT_ISSUERCERT"
          case ADDRESS_SCOPE              => "CURLOPT_ADDRESS_SCOPE"
          case CERTINFO                   => "CURLOPT_CERTINFO"
          case USERNAME                   => "CURLOPT_USERNAME"
          case PASSWORD                   => "CURLOPT_PASSWORD"
          case PROXYUSERNAME              => "CURLOPT_PROXYUSERNAME"
          case PROXYPASSWORD              => "CURLOPT_PROXYPASSWORD"
          case NOPROXY                    => "CURLOPT_NOPROXY"
          case TFTP_BLKSIZE               => "CURLOPT_TFTP_BLKSIZE"
          case SOCKS5_GSSAPI_SERVICE      => "CURLOPT_SOCKS5_GSSAPI_SERVICE"
          case SOCKS5_GSSAPI_NEC          => "CURLOPT_SOCKS5_GSSAPI_NEC"
          case PROTOCOLS                  => "CURLOPT_PROTOCOLS"
          case REDIR_PROTOCOLS            => "CURLOPT_REDIR_PROTOCOLS"
          case SSH_KNOWNHOSTS             => "CURLOPT_SSH_KNOWNHOSTS"
          case SSH_KEYFUNCTION            => "CURLOPT_SSH_KEYFUNCTION"
          case SSH_KEYDATA                => "CURLOPT_SSH_KEYDATA"
          case MAIL_FROM                  => "CURLOPT_MAIL_FROM"
          case MAIL_RCPT                  => "CURLOPT_MAIL_RCPT"
          case FTP_USE_PRET               => "CURLOPT_FTP_USE_PRET"
          case RTSP_REQUEST               => "CURLOPT_RTSP_REQUEST"
          case RTSP_SESSION_ID            => "CURLOPT_RTSP_SESSION_ID"
          case RTSP_STREAM_URI            => "CURLOPT_RTSP_STREAM_URI"
          case RTSP_TRANSPORT             => "CURLOPT_RTSP_TRANSPORT"
          case RTSP_CLIENT_CSEQ           => "CURLOPT_RTSP_CLIENT_CSEQ"
          case RTSP_SERVER_CSEQ           => "CURLOPT_RTSP_SERVER_CSEQ"
          case INTERLEAVEDATA             => "CURLOPT_INTERLEAVEDATA"
          case INTERLEAVEFUNCTION         => "CURLOPT_INTERLEAVEFUNCTION"
          case WILDCARDMATCH              => "CURLOPT_WILDCARDMATCH"
          case CHUNK_BGN_FUNCTION         => "CURLOPT_CHUNK_BGN_FUNCTION"
          case CHUNK_END_FUNCTION         => "CURLOPT_CHUNK_END_FUNCTION"
          case FNMATCH_FUNCTION           => "CURLOPT_FNMATCH_FUNCTION"
          case CHUNK_DATA                 => "CURLOPT_CHUNK_DATA"
          case FNMATCH_DATA               => "CURLOPT_FNMATCH_DATA"
          case RESOLVE                    => "CURLOPT_RESOLVE"
          case TLSAUTH_USERNAME           => "CURLOPT_TLSAUTH_USERNAME"
          case TLSAUTH_PASSWORD           => "CURLOPT_TLSAUTH_PASSWORD"
          case TLSAUTH_TYPE               => "CURLOPT_TLSAUTH_TYPE"
          case TRANSFER_ENCODING          => "CURLOPT_TRANSFER_ENCODING"
          case CLOSESOCKETFUNCTION        => "CURLOPT_CLOSESOCKETFUNCTION"
          case CLOSESOCKETDATA            => "CURLOPT_CLOSESOCKETDATA"
          case GSSAPI_DELEGATION          => "CURLOPT_GSSAPI_DELEGATION"
          case DNS_SERVERS                => "CURLOPT_DNS_SERVERS"
          case ACCEPTTIMEOUT_MS           => "CURLOPT_ACCEPTTIMEOUT_MS"
          case TCP_KEEPALIVE              => "CURLOPT_TCP_KEEPALIVE"
          case TCP_KEEPIDLE               => "CURLOPT_TCP_KEEPIDLE"
          case TCP_KEEPINTVL              => "CURLOPT_TCP_KEEPINTVL"
          case SSL_OPTIONS                => "CURLOPT_SSL_OPTIONS"
          case MAIL_AUTH                  => "CURLOPT_MAIL_AUTH"
          case SASL_IR                    => "CURLOPT_SASL_IR"
          case XFERINFOFUNCTION           => "CURLOPT_XFERINFOFUNCTION"
          case XOAUTH2_BEARER             => "CURLOPT_XOAUTH2_BEARER"
          case DNS_INTERFACE              => "CURLOPT_DNS_INTERFACE"
          case DNS_LOCAL_IP4              => "CURLOPT_DNS_LOCAL_IP4"
          case DNS_LOCAL_IP6              => "CURLOPT_DNS_LOCAL_IP6"
          case LOGIN_OPTIONS              => "CURLOPT_LOGIN_OPTIONS"
          case SSL_ENABLE_NPN             => "CURLOPT_SSL_ENABLE_NPN"
          case SSL_ENABLE_ALPN            => "CURLOPT_SSL_ENABLE_ALPN"
          case EXPECT_100_TIMEOUT_MS      => "CURLOPT_EXPECT_100_TIMEOUT_MS"
          case PROXYHEADER                => "CURLOPT_PROXYHEADER"
          case HEADEROPT                  => "CURLOPT_HEADEROPT"
          case PINNEDPUBLICKEY            => "CURLOPT_PINNEDPUBLICKEY"
          case UNIX_SOCKET_PATH           => "CURLOPT_UNIX_SOCKET_PATH"
          case SSL_VERIFYSTATUS           => "CURLOPT_SSL_VERIFYSTATUS"
          case SSL_FALSESTART             => "CURLOPT_SSL_FALSESTART"
          case PATH_AS_IS                 => "CURLOPT_PATH_AS_IS"
          case PROXY_SERVICE_NAME         => "CURLOPT_PROXY_SERVICE_NAME"
          case SERVICE_NAME               => "CURLOPT_SERVICE_NAME"
          case PIPEWAIT                   => "CURLOPT_PIPEWAIT"
          case DEFAULT_PROTOCOL           => "CURLOPT_DEFAULT_PROTOCOL"
          case STREAM_WEIGHT              => "CURLOPT_STREAM_WEIGHT"
          case STREAM_DEPENDS             => "CURLOPT_STREAM_DEPENDS"
          case STREAM_DEPENDS_E           => "CURLOPT_STREAM_DEPENDS_E"
          case TFTP_NO_OPTIONS            => "CURLOPT_TFTP_NO_OPTIONS"
          case CONNECT_TO                 => "CURLOPT_CONNECT_TO"
          case TCP_FASTOPEN               => "CURLOPT_TCP_FASTOPEN"
          case KEEP_SENDING_ON_ERROR      => "CURLOPT_KEEP_SENDING_ON_ERROR"
          case PROXY_CAINFO               => "CURLOPT_PROXY_CAINFO"
          case PROXY_CAPATH               => "CURLOPT_PROXY_CAPATH"
          case PROXY_SSL_VERIFYPEER       => "CURLOPT_PROXY_SSL_VERIFYPEER"
          case PROXY_SSL_VERIFYHOST       => "CURLOPT_PROXY_SSL_VERIFYHOST"
          case PROXY_SSLVERSION           => "CURLOPT_PROXY_SSLVERSION"
          case PROXY_TLSAUTH_USERNAME     => "CURLOPT_PROXY_TLSAUTH_USERNAME"
          case PROXY_TLSAUTH_PASSWORD     => "CURLOPT_PROXY_TLSAUTH_PASSWORD"
          case PROXY_TLSAUTH_TYPE         => "CURLOPT_PROXY_TLSAUTH_TYPE"
          case PROXY_SSLCERT              => "CURLOPT_PROXY_SSLCERT"
          case PROXY_SSLCERTTYPE          => "CURLOPT_PROXY_SSLCERTTYPE"
          case PROXY_SSLKEY               => "CURLOPT_PROXY_SSLKEY"
          case PROXY_SSLKEYTYPE           => "CURLOPT_PROXY_SSLKEYTYPE"
          case PROXY_KEYPASSWD            => "CURLOPT_PROXY_KEYPASSWD"
          case PROXY_SSL_CIPHER_LIST      => "CURLOPT_PROXY_SSL_CIPHER_LIST"
          case PROXY_CRLFILE              => "CURLOPT_PROXY_CRLFILE"
          case PROXY_SSL_OPTIONS          => "CURLOPT_PROXY_SSL_OPTIONS"
          case PRE_PROXY                  => "CURLOPT_PRE_PROXY"
          case PROXY_PINNEDPUBLICKEY      => "CURLOPT_PROXY_PINNEDPUBLICKEY"
          case ABSTRACT_UNIX_SOCKET       => "CURLOPT_ABSTRACT_UNIX_SOCKET"
          case SUPPRESS_CONNECT_HEADERS   => "CURLOPT_SUPPRESS_CONNECT_HEADERS"
          case REQUEST_TARGET             => "CURLOPT_REQUEST_TARGET"
          case SOCKS5_AUTH                => "CURLOPT_SOCKS5_AUTH"
          case SSH_COMPRESSION            => "CURLOPT_SSH_COMPRESSION"
          case MIMEPOST                   => "CURLOPT_MIMEPOST"
          case TIMEVALUE_LARGE            => "CURLOPT_TIMEVALUE_LARGE"
          case HAPPY_EYEBALLS_TIMEOUT_MS  => "CURLOPT_HAPPY_EYEBALLS_TIMEOUT_MS"
          case RESOLVER_START_FUNCTION    => "CURLOPT_RESOLVER_START_FUNCTION"
          case RESOLVER_START_DATA        => "CURLOPT_RESOLVER_START_DATA"
          case HAPROXYPROTOCOL            => "CURLOPT_HAPROXYPROTOCOL"
          case DNS_SHUFFLE_ADDRESSES      => "CURLOPT_DNS_SHUFFLE_ADDRESSES"
          case TLS13_CIPHERS              => "CURLOPT_TLS13_CIPHERS"
          case PROXY_TLS13_CIPHERS        => "CURLOPT_PROXY_TLS13_CIPHERS"
          case DISALLOW_USERNAME_IN_URL   => "CURLOPT_DISALLOW_USERNAME_IN_URL"
          case DOH_URL                    => "CURLOPT_DOH_URL"
          case UPLOAD_BUFFERSIZE          => "CURLOPT_UPLOAD_BUFFERSIZE"
          case UPKEEP_INTERVAL_MS         => "CURLOPT_UPKEEP_INTERVAL_MS"
          case CURLU                      => "CURLOPT_CURLU"
          case TRAILERFUNCTION            => "CURLOPT_TRAILERFUNCTION"
          case TRAILERDATA                => "CURLOPT_TRAILERDATA"
          case HTTP09_ALLOWED             => "CURLOPT_HTTP09_ALLOWED"
          case ALTSVC_CTRL                => "CURLOPT_ALTSVC_CTRL"
          case ALTSVC                     => "CURLOPT_ALTSVC"
          case MAXAGE_CONN                => "CURLOPT_MAXAGE_CONN"
          case SASL_AUTHZID               => "CURLOPT_SASL_AUTHZID"
          case MAIL_RCPT_ALLOWFAILS       => "CURLOPT_MAIL_RCPT_ALLOWFAILS"
          case SSLCERT_BLOB               => "CURLOPT_SSLCERT_BLOB"
          case SSLKEY_BLOB                => "CURLOPT_SSLKEY_BLOB"
          case PROXY_SSLCERT_BLOB         => "CURLOPT_PROXY_SSLCERT_BLOB"
          case PROXY_SSLKEY_BLOB          => "CURLOPT_PROXY_SSLKEY_BLOB"
          case ISSUERCERT_BLOB            => "CURLOPT_ISSUERCERT_BLOB"
          case PROXY_ISSUERCERT           => "CURLOPT_PROXY_ISSUERCERT"
          case PROXY_ISSUERCERT_BLOB      => "CURLOPT_PROXY_ISSUERCERT_BLOB"
          case SSL_EC_CURVES              => "CURLOPT_SSL_EC_CURVES"
          case HSTS_CTRL                  => "CURLOPT_HSTS_CTRL"
          case HSTS                       => "CURLOPT_HSTS"
          case HSTSREADFUNCTION           => "CURLOPT_HSTSREADFUNCTION"
          case HSTSREADDATA               => "CURLOPT_HSTSREADDATA"
          case HSTSWRITEFUNCTION          => "CURLOPT_HSTSWRITEFUNCTION"
          case HSTSWRITEDATA              => "CURLOPT_HSTSWRITEDATA"
          case AWS_SIGV4                  => "CURLOPT_AWS_SIGV4"
          case DOH_SSL_VERIFYPEER         => "CURLOPT_DOH_SSL_VERIFYPEER"
          case DOH_SSL_VERIFYHOST         => "CURLOPT_DOH_SSL_VERIFYHOST"
          case DOH_SSL_VERIFYSTATUS       => "CURLOPT_DOH_SSL_VERIFYSTATUS"
          case CAINFO_BLOB                => "CURLOPT_CAINFO_BLOB"
          case PROXY_CAINFO_BLOB          => "CURLOPT_PROXY_CAINFO_BLOB"
          case SSH_HOST_PUBLIC_KEY_SHA256 => "CURLOPT_SSH_HOST_PUBLIC_KEY_SHA256"
          case PREREQFUNCTION             => "CURLOPT_PREREQFUNCTION"
          case PREREQDATA                 => "CURLOPT_PREREQDATA"
          case MAXLIFETIME_CONN           => "CURLOPT_MAXLIFETIME_CONN"
          case MIME_OPTIONS               => "CURLOPT_MIME_OPTIONS"
          case SSH_HOSTKEYFUNCTION        => "CURLOPT_SSH_HOSTKEYFUNCTION"
          case SSH_HOSTKEYDATA            => "CURLOPT_SSH_HOSTKEYDATA"
          case PROTOCOLS_STR              => "CURLOPT_PROTOCOLS_STR"
          case REDIR_PROTOCOLS_STR        => "CURLOPT_REDIR_PROTOCOLS_STR"
          case WS_OPTIONS                 => "CURLOPT_WS_OPTIONS"
          case CA_CACHE_TIMEOUT           => "CURLOPT_CA_CACHE_TIMEOUT"
          case QUICK_EXIT                 => "CURLOPT_QUICK_EXIT"
          case HAPROXY_CLIENT_IP          => "CURLOPT_HAPROXY_CLIENT_IP"
          case LASTENTRY                  => "CURLOPT_LASTENTRY"

    extension (a: CurlOption)
      inline def &(b: CurlOption): CurlOption = a & b
      inline def |(b: CurlOption): CurlOption = a | b
      inline def is(b: CurlOption): Boolean = (a & b) == b

  /**
   * Below here follows defines for the CURLOPT_IPRESOLVE option. If a host name resolves addresses
   * using more than one IP protocol version, this option might be handy to force libcurl to use a
   * specific IP version.
   */
  @name("CURLOPT_IPRESOLVE")
  opaque type CurlOptIpResolve = CLong
  object CurlOptIpResolve extends _BindgenEnumCLong[CurlOptIpResolve]:

    inline def define(inline a: Int): CurlOptIpResolve = a

    /** default, uses addresses to all IP versions that your system allows */
    val WHATEVER = define(0)

    /** uses only IPv4 addresses/connections */
    val V4 = define(1)

    /** uses only IPv6 addresses/connections */
    val V6 = define(2)

  /** These enums are for use with the `CURLOPT_HTTP_VERSION` option. */
  @name("CURLOPT_HTTP_VERSION")
  opaque type CurlOptHttpVersion = CLong
  object CurlOptHttpVersion extends _BindgenEnumCLong[CurlOptHttpVersion]:

    inline def define(inline a: Int): CurlOptHttpVersion = a

    /**
     * setting this means we don't care, and that we'd like the library to choose the best possible
     * for us!
     */
    val VERSION_NONE = define(0)

    /** please use HTTP 1.0 in the request */
    val VERSION_1_0 = define(1)

    /** please use HTTP 1.1 in the request */
    val VERSION_1_1 = define(2)

    /** please use HTTP 2 in the request */
    val VERSION_2_0 = define(3)

    /** use version 2 for HTTPS, version 1.1 for HTTP */
    val VERSION_2TLS = define(4)

    /** please use HTTP 2 without HTTP/1.1 Upgrade */
    val VERSION_2_PRIOR_KNOWLEDGE = define(5)

    /**
     * Use HTTP/3, fallback to HTTP/2 or HTTP/1 if needed. For HTTPS only. For HTTP, this option
     * makes libcurl return error.
     */
    val VERSION_3 = define(30)

    /**
     * Use HTTP/3 without fallback. For HTTPS only. For HTTP, this makes libcurl return error.
     */
    val VERSION_3ONLY = define(31)

    /** ILLEGAL http version */
    val VERSION_LAST = define(32)

  //
  // include <curl/options.h>
  //

  @name("curl_easytype")
  opaque type CurlEasyType = UInt
  object CurlEasyType extends _BindgenEnumUInt[CurlEasyType]:
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

    extension (inline value: CurlEasyType)
      inline def getName: String =
        inline value match
          case LONG     => "CURLOT_LONG"
          case VALUES   => "CURLOT_VALUES"
          case OFF_T    => "CURLOT_OFF_T"
          case OBJECT   => "CURLOT_OBJECT"
          case STRING   => "CURLOT_STRING"
          case SLIST    => "CURLOT_SLIST"
          case CBPTR    => "CURLOT_CBPTR"
          case BLOB     => "CURLOT_BLOB"
          case FUNCTION => "CURLOT_FUNCTION"

    extension (a: CurlEasyType)
      inline def &(b: CurlEasyType): CurlEasyType = a & b
      inline def |(b: CurlEasyType): CurlEasyType = a | b
      inline def is(b: CurlEasyType): Boolean = (a & b) == b

  @name("curl_easyoption")
  opaque type CurlEasyOption = CStruct4[
    /** name */
    CString,
    /** id */
    CurlOption,
    /** type */
    CurlEasyType,
    /** flags */
    UInt,
  ]
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
