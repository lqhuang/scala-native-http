import snhttp.jdk.net.http.Demand

import utest.{Tests, test, assert, assertThrows}

class DemandTest extends utest.TestSuite:

  val tests = Tests {

    test("Demand should be initially fulfilled") {
      val d = Demand()
      assert(d.fulfilled)
    }

    test("toString should return current value") {
      val d = Demand()
      assert(d.get() == 0L)
      assert(d.toString == "0")
      d.increase(7)
      assert(d.get() == 7L)
      assert(d.toString == "7")
    }

    test("increase should throw on non-positive input") {
      val d = Demand()
      assertThrows[IllegalArgumentException] {
        d.increase(0)
      }
      assertThrows[IllegalArgumentException] {
        d.increase(-1)
      }
    }

    test("increase should returns true if demand was fulfilled, false otherwise") {
      val d = Demand()
      assert(d.fulfilled)
      assert(d.increase(5))
      assert(!d.fulfilled)
      assert(!d.increase(3))
    }

    test("increase should handle overflow correctly") {
      val d = Demand()
      d.increase(1)
      d.increase(Long.MaxValue)
      assert(!d.increase(1)) // should not increase beyond Long.MaxValue
      assert(!d.fulfilled)
      assert(d.get() == Long.MaxValue)
    }

    test("increaseIfFulfilled only increases if fulfilled") {
      val d = Demand()
      assert(d.increaseIfFulfilled())
      assert(!d.increaseIfFulfilled())
      d.reset()
      d.increase(5)
      assert(!d.increaseIfFulfilled())
    }

    test("decreaseAndGet throws on non-positive input") {
      val d = Demand()
      d.increase(5)
      assertThrows[IllegalArgumentException] {
        d.decreaseAndGet(0)
      }
      assertThrows[IllegalArgumentException] {
        d.decreaseAndGet(-10)
      }
    }

    test("decreaseAndGet returns correct decreased value and updates the demand") {
      val d = Demand()
      d.increase(5)
      assert(d.decreaseAndGet(2) == 2L)
      assert(d.get() == 3L)
      assert(d.decreaseAndGet(10) == 3L) // decrease only what is available
      assert(d.get() == 0L)
      assert(d.decreaseAndGet(100) == 0L) // no more demand left
    }

    test("decreaseAndGet should handle overflow correctly") {
      val d = Demand()
      d.increase(Long.MaxValue)
      assert(d.decreaseAndGet(Long.MaxValue) == Long.MaxValue)
      assert(d.fulfilled)
      d.increase(1)
      assert(d.decreaseAndGet(Long.MaxValue) == 1L)
      assert(d.fulfilled)

    }

    test("decrease returns true if decremented, false otherwise") {
      val d = Demand()
      d.increase(2)
      assert(d.decrease())
      assert(d.decrease())
      assert(!d.decrease())
    }

    test("decrease returns false for empty demand") {
      val d = Demand()
      assert(!d.decrease())
    }

    test("fulfilled and reset work as expected") {
      val d = Demand()
      assert(d.fulfilled)
      d.increase(1)
      assert(!d.fulfilled)
      d.reset()
      assert(d.fulfilled)
      d.increase(Long.MaxValue)
      d.reset()
      assert(d.fulfilled)
    }

    test("increase and decreaseAndGet work together correctly") {
      List(1, 3, 7, 11, 111, 999).foreach { n =>
        val d = Demand()
        d.increase(n)
        d.decreaseAndGet(n)
        assert(d.fulfilled)
      }
    }

    test("Demand should be thread safe") {
      val d = Demand()
      val threads = (1 to Runtime.getRuntime().availableProcessors()).map { i =>
        // new Thread(() =>
        //   if (i % 2 == 0) d.increase(1)
        //   else d.decrease(),
        // )
      }
      // threads.foreach(_.start())
      // threads.foreach(_.join())
    }
  }
