import snhttp.jdk.Demand

class DemandTest extends munit.FunSuite:
  test("increase should throw on non-positive input") {
    val d = Demand()
    intercept[IllegalArgumentException] {
      d.increase(0)
    }
    intercept[IllegalArgumentException] {
      d.increase(-1)
    }
  }

  test("increase returns true if demand was fulfilled, false otherwise") {
    val d = Demand()
    assert(d.fulfilled)
    assert(d.increase(5))
    assert(!d.fulfilled)
    assert(!d.increase(3))
  }

  test("increaseIfFulfilled only increases if fulfilled") {
    val d = Demand()
    assert(d.increaseIfFulfilled())
    assert(!d.increaseIfFulfilled())
    d.reset()
    assert(d.increaseIfFulfilled())
  }

  test("decreaseAndGet returns correct value and throws on non-positive input") {
    val d = Demand()
    d.increase(5)
    assertEquals(d.decreaseAndGet(2), 2L)
    assertEquals(d.get(), 3L)
    assertEquals(d.decreaseAndGet(10), 3L)
    assertEquals(d.get(), 0L)
    intercept[IllegalArgumentException] {
      d.decreaseAndGet(0)
    }
  }

  test("tryDecrement returns true if decremented, false otherwise") {
    val d = Demand()
    d.increase(2)
    assert(d.decrease())
    assert(d.decrease())
    assert(!d.decrease())
  }

  test("fulfilled and reset work as expected") {
    val d = Demand()
    assert(d.fulfilled)
    d.increase(1)
    assert(!d.fulfilled)
    d.reset()
    assert(d.fulfilled)
  }

  test("get and toString return current value") {
    val d = Demand()
    assertEquals(d.get(), 0L)
    assertEquals(d.toString, "0")
    d.increase(7)
    assertEquals(d.get(), 7L)
    assertEquals(d.toString, "7")
  }
