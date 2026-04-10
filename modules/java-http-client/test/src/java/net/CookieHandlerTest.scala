package snhttp.java.net

import java.net.{CookieHandler, CookieManager}

import utest.{Tests, test, assert}

class CookieHandlerTest extends utest.TestSuite:

  val tests = Tests {
    test("getDefault returns null initially") {
      // Reset to ensure clean state
      CookieHandler.setDefault(null)
      assert(CookieHandler.getDefault() == null)
    }

    test("setDefault and getDefault work correctly") {
      val manager = new CookieManager()
      CookieHandler.setDefault(manager)
      assert(CookieHandler.getDefault() == manager)
      
      // Clean up
      CookieHandler.setDefault(null)
    }

    test("setDefault can reset to null") {
      val manager = new CookieManager()
      CookieHandler.setDefault(manager)
      assert(CookieHandler.getDefault() != null)
      
      CookieHandler.setDefault(null)
      assert(CookieHandler.getDefault() == null)
    }
  }
