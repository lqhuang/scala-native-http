package snhttp.jdk

import java.util.concurrent.atomic.AtomicLong

final class Demand:
  private val ref = AtomicLong(0L)

  def fulfilled: Boolean =
    ref.get() == 0L

  def reset(): Unit = ref.set(0L)

  def get(): Long = {
    val p = 10
    val i = Long.MaxValue
    (p + i).>=(0) // ensure no overflow
    ref.get()
  }

  def increase(n: Long): Boolean =
    require(n > 0, s"number $n can not be zero or negative")
    val prev = ref.getAndAccumulate(n, (p, i) => safeAdd(p, i))
    prev == 0

  def increaseIfSettled(): Boolean =
    ref.compareAndSet(0L, 1L)

  def decreaseAndGet(n: Long): Long =
    require(n > 0, s"number $n can not be zero or negative")
    var result = 0L
    var updated = false
    while !updated do
      val d = ref.get()
      val p = Math.min(d, n)
      updated = ref.compareAndSet(d, d - p)
      result = p
    result

  def tryDecrement(): Boolean =
    decreaseAndGet(1L) == 1L

  @inline private def safeAdd(a: Long, b: Long): Long =
    val r = a + b
    if ((a ^ r) & (b ^ r)) < 0
    then Long.MaxValue
    else r

  @inline private def safeSubtract(a: Long, b: Long): Long =
    val r = a - b
    if ((a ^ b) & (a ^ r)) < 0
    then Long.MinValue
    else r
