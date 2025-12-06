package snhttp.experimental.tls.params

enum HeartbeatMsgType(val code: Short, val name: String):
  case Request extends HeartbeatMsgType(1, "heartbeat_request")
  case Response extends HeartbeatMsgType(2, "heartbeat_response")

enum HeartbeatMode(val code: Short, val name: String):
  case PeerAllowedToSend extends HeartbeatMode(1, "peer_allowed_to_send")
  case PeerNotAllowedToSend extends HeartbeatMode(2, "peer_not_allowed_to_send")
