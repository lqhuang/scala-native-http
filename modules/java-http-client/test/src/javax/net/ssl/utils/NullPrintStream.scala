/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.conscrypt.javax.net.ssl

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import java.util.Locale

/**
 * A PrintStream that throws away its output.
 */
final class NullPrintStream extends PrintStream(new ByteArrayOutputStream()):

  override def checkError(): Boolean = false

  override protected def clearError(): Unit = {}

  override def close(): Unit = {}

  override def flush(): Unit = {}

  override def format(format: String, args: Object*): PrintStream = this

  override def format(l: Locale, format: String, args: Object*): PrintStream = this

  override def printf(format: String, args: Object*): PrintStream = this

  override def printf(l: Locale, format: String, args: Object*): PrintStream = this

  override def print(charArray: Array[Char]): Unit = {}

  override def print(ch: Char): Unit = {}

  override def print(dnum: Double): Unit = {}

  override def print(fnum: Float): Unit = {}

  override def print(inum: Int): Unit = {}

  override def print(lnum: Long): Unit = {}

  override def print(obj: Object): Unit = {}

  override def print(str: String): Unit = {}

  override def print(bool: Boolean): Unit = {}

  override def println(): Unit = {}

  override def println(charArray: Array[Char]): Unit = {}

  override def println(ch: Char): Unit = {}

  override def println(dnum: Double): Unit = {}

  override def println(fnum: Float): Unit = {}

  override def println(inum: Int): Unit = {}

  override def println(lnum: Long): Unit = {}

  override def println(obj: Object): Unit = {}

  override def println(str: String): Unit = {}

  override def println(bool: Boolean): Unit = {}

  override protected def setError(): Unit = {}

  override def write(buffer: Array[Byte], offset: Int, length: Int): Unit = {}

  override def write(oneByte: Int): Unit = {}

  override def append(c: Char): PrintStream = this

  override def append(csq: CharSequence): PrintStream = this

  override def append(csq: CharSequence, start: Int, end: Int): PrintStream = this
