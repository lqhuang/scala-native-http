package java.nio

import scala.scalanative.unsafe

private[nio] object PointerByteBuffer:
  def wrap(ptr: unsafe.Ptr[Byte], capacity: Int): ByteBuffer =
    throw new NotImplementedError(
      "This is a shim. Real implementation resides in official scala native javalib",
    )
