package java.nio.snhttp.utils

import java.nio.ByteBuffer
import java.nio.PointerByteBuffer as _PointerByteBuffer
import scalanative.unsafe.Ptr

object PointerByteBuffer:
  def wrap(ptr: Ptr[Byte], capacity: Int): ByteBuffer =
    _PointerByteBuffer.wrap(ptr, capacity)
