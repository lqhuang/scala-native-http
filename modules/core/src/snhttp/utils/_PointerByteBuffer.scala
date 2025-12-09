package java.nio.snhttp.utils

import java.nio.ByteBuffer
import java.nio.PointerByteBuffer as _PointerByteBuffer
import scalanative.unsafe

object PointerByteBuffer:
  def wrap(ptr: unsafe.Ptr[Byte], capacity: Int): ByteBuffer =
    _PointerByteBuffer.wrap(ptr, capacity)
