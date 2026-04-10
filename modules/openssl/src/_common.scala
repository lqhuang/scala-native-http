package snhttp.experimental.openssl
package _common

import scala.scalanative.unsafe.CVarArgList
import scala.scalanative.unsigned.UByte

export scala.scalanative.libc.stdio.FILE
export scala.scalanative.libc.stddef.size_t
export scala.scalanative.libc.stdint.{uint32_t, uint64_t, int64_t, uint16_t, uintptr_t}
export scala.scalanative.posix.sys.types.{ssize_t, time_t}
export scala.scalanative.posix.sys.time.timeval
export scala.scalanative.posix.time.tm
export scala.scalanative.posix.unistd.off_t

type uint8_t = UByte
type va_list = CVarArgList
