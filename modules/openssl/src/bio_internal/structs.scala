package snhttp.experimental.openssl.bio_internal

import _root_.scala.scalanative.unsafe.*
import _root_.scala.scalanative.unsigned.*
import _root_.scala.scalanative.libc.*
import _root_.scala.scalanative.*

object structs:

  import _root_.snhttp.experimental.openssl.bio_internal.aliases.*
  import _root_.snhttp.experimental.openssl.bio_internal.unions.*

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type BIO = CStruct0

  object BIO:
    given _tag: Tag[BIO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_ADDRINFO = CStruct0

  object BIO_ADDRINFO:
    given _tag: Tag[BIO_ADDRINFO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_METHOD = CStruct0

  object BIO_METHOD:
    given _tag: Tag[BIO_METHOD] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_MMSG_CB_ARGS = CStruct5[Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t]]

  object BIO_MMSG_CB_ARGS:
    given _tag: Tag[BIO_MMSG_CB_ARGS] =
      Tag.materializeCStruct5Tag[Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t]]

    // Allocates BIO_MMSG_CB_ARGS on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[BIO_MMSG_CB_ARGS] =
      scala.scalanative.unsafe.alloc[BIO_MMSG_CB_ARGS](1)
    def apply(
        msg: Ptr[BIO_MSG],
        stride: size_t,
        num_msg: size_t,
        flags: uint64_t,
        msgs_processed: Ptr[size_t],
    )(using Zone): Ptr[BIO_MMSG_CB_ARGS] =
      val ____ptr = apply()
      (!____ptr).msg = msg
      (!____ptr).stride = stride
      (!____ptr).num_msg = num_msg
      (!____ptr).flags = flags
      (!____ptr).msgs_processed = msgs_processed
      ____ptr

    extension (struct: BIO_MMSG_CB_ARGS)
      def msg: Ptr[BIO_MSG] = struct._1
      def msg_=(value: Ptr[BIO_MSG]): Unit = !struct.at1 = value
      def stride: size_t = struct._2
      def stride_=(value: size_t): Unit = !struct.at2 = value
      def num_msg: size_t = struct._3
      def num_msg_=(value: size_t): Unit = !struct.at3 = value
      def flags: uint64_t = struct._4
      def flags_=(value: uint64_t): Unit = !struct.at4 = value
      def msgs_processed: Ptr[size_t] = struct._5
      def msgs_processed_=(value: Ptr[size_t]): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_MSG = CStruct5[Ptr[Byte], size_t, Ptr[BIO_ADDR], Ptr[BIO_ADDR], uint64_t]

  object BIO_MSG:
    given _tag: Tag[BIO_MSG] =
      Tag.materializeCStruct5Tag[Ptr[Byte], size_t, Ptr[BIO_ADDR], Ptr[BIO_ADDR], uint64_t]

    // Allocates BIO_MSG on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[BIO_MSG] = scala.scalanative.unsafe.alloc[BIO_MSG](1)
    def apply(
        data: Ptr[Byte],
        data_len: size_t,
        peer: Ptr[BIO_ADDR],
        local: Ptr[BIO_ADDR],
        flags: uint64_t,
    )(using Zone): Ptr[BIO_MSG] =
      val ____ptr = apply()
      (!____ptr).data = data
      (!____ptr).data_len = data_len
      (!____ptr).peer = peer
      (!____ptr).local = local
      (!____ptr).flags = flags
      ____ptr

    extension (struct: BIO_MSG)
      def data: Ptr[Byte] = struct._1
      def data_=(value: Ptr[Byte]): Unit = !struct.at1 = value
      def data_len: size_t = struct._2
      def data_len_=(value: size_t): Unit = !struct.at2 = value
      def peer: Ptr[BIO_ADDR] = struct._3
      def peer_=(value: Ptr[BIO_ADDR]): Unit = !struct.at3 = value
      def local: Ptr[BIO_ADDR] = struct._4
      def local_=(value: Ptr[BIO_ADDR]): Unit = !struct.at4 = value
      def flags: uint64_t = struct._5
      def flags_=(value: uint64_t): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type BIO_POLL_DESCRIPTOR = CStruct2[uint32_t, BIO_POLL_DESCRIPTOR.Value]

  object BIO_POLL_DESCRIPTOR:
    given _tag: Tag[BIO_POLL_DESCRIPTOR] =
      Tag.materializeCStruct2Tag[uint32_t, BIO_POLL_DESCRIPTOR.Value]

    // Allocates BIO_POLL_DESCRIPTOR on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[BIO_POLL_DESCRIPTOR] =
      scala.scalanative.unsafe.alloc[BIO_POLL_DESCRIPTOR](1)
    def apply(`type`: uint32_t, value: BIO_POLL_DESCRIPTOR.Value)(using
        Zone,
    ): Ptr[BIO_POLL_DESCRIPTOR] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).value = value
      ____ptr

    extension (struct: BIO_POLL_DESCRIPTOR)
      def `type`: uint32_t = struct._1
      def type_=(value: uint32_t): Unit = !struct.at1 = value
      def value: BIO_POLL_DESCRIPTOR.Value = struct._2
      def value_=(value: BIO_POLL_DESCRIPTOR.Value): Unit = !struct.at2 = value

    /**
     * [bindgen] header: /usr/include/openssl/bio.h
     */
    opaque type Value = CArray[Byte, Nat._8]
    object Value:
      given _tag: Tag[Value] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

      def apply()(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        ___ptr

      @scala.annotation.targetName("apply_fd")
      def apply(fd: CInt)(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[CInt]].update(0, fd)
        ___ptr

      @scala.annotation.targetName("apply_custom")
      def apply(custom: Ptr[Byte])(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[Ptr[Byte]]].update(0, custom)
        ___ptr

      @scala.annotation.targetName("apply_custom_ui")
      def apply(custom_ui: uintptr_t)(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[uintptr_t]].update(0, custom_ui)
        ___ptr

      @scala.annotation.targetName("apply_ssl")
      def apply(ssl: Ptr[SSL])(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[Ptr[SSL]]].update(0, ssl)
        ___ptr

      extension (struct: Value)
        def fd: CInt = !struct.at(0).asInstanceOf[Ptr[CInt]]
        def fd_=(value: CInt): Unit = !struct.at(0).asInstanceOf[Ptr[CInt]] = value
        def custom: Ptr[Byte] = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]]
        def custom_=(value: Ptr[Byte]): Unit = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]] = value
        def custom_ui: uintptr_t = !struct.at(0).asInstanceOf[Ptr[uintptr_t]]
        def custom_ui_=(value: uintptr_t): Unit = !struct.at(0).asInstanceOf[Ptr[uintptr_t]] = value
        def ssl: Ptr[SSL] = !struct.at(0).asInstanceOf[Ptr[Ptr[SSL]]]
        def ssl_=(value: Ptr[SSL]): Unit = !struct.at(0).asInstanceOf[Ptr[Ptr[SSL]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/core.h
   */
  opaque type OSSL_CORE_BIO = CStruct0

  object OSSL_CORE_BIO:
    given _tag: Tag[OSSL_CORE_BIO] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type OSSL_LIB_CTX = CStruct0

  object OSSL_LIB_CTX:
    given _tag: Tag[OSSL_LIB_CTX] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/types.h
   */
  opaque type SSL = CStruct0

  object SSL:
    given _tag: Tag[SSL] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_addrinfo_st = CStruct0

  object bio_addrinfo_st:
    given _tag: Tag[bio_addrinfo_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_method_st = CStruct0

  object bio_method_st:
    given _tag: Tag[bio_method_st] = Tag.materializeCStruct0Tag

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_mmsg_cb_args_st = CStruct5[Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t]]

  object bio_mmsg_cb_args_st:
    given _tag: Tag[bio_mmsg_cb_args_st] =
      Tag.materializeCStruct5Tag[Ptr[BIO_MSG], size_t, size_t, uint64_t, Ptr[size_t]]

    // Allocates bio_mmsg_cb_args_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[bio_mmsg_cb_args_st] =
      scala.scalanative.unsafe.alloc[bio_mmsg_cb_args_st](1)
    def apply(
        msg: Ptr[BIO_MSG],
        stride: size_t,
        num_msg: size_t,
        flags: uint64_t,
        msgs_processed: Ptr[size_t],
    )(using Zone): Ptr[bio_mmsg_cb_args_st] =
      val ____ptr = apply()
      (!____ptr).msg = msg
      (!____ptr).stride = stride
      (!____ptr).num_msg = num_msg
      (!____ptr).flags = flags
      (!____ptr).msgs_processed = msgs_processed
      ____ptr

    extension (struct: bio_mmsg_cb_args_st)
      def msg: Ptr[BIO_MSG] = struct._1
      def msg_=(value: Ptr[BIO_MSG]): Unit = !struct.at1 = value
      def stride: size_t = struct._2
      def stride_=(value: size_t): Unit = !struct.at2 = value
      def num_msg: size_t = struct._3
      def num_msg_=(value: size_t): Unit = !struct.at3 = value
      def flags: uint64_t = struct._4
      def flags_=(value: uint64_t): Unit = !struct.at4 = value
      def msgs_processed: Ptr[size_t] = struct._5
      def msgs_processed_=(value: Ptr[size_t]): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_msg_st = CStruct5[Ptr[Byte], size_t, Ptr[BIO_ADDR], Ptr[BIO_ADDR], uint64_t]

  object bio_msg_st:
    given _tag: Tag[bio_msg_st] =
      Tag.materializeCStruct5Tag[Ptr[Byte], size_t, Ptr[BIO_ADDR], Ptr[BIO_ADDR], uint64_t]

    // Allocates bio_msg_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[bio_msg_st] = scala.scalanative.unsafe.alloc[bio_msg_st](1)
    def apply(
        data: Ptr[Byte],
        data_len: size_t,
        peer: Ptr[BIO_ADDR],
        local: Ptr[BIO_ADDR],
        flags: uint64_t,
    )(using Zone): Ptr[bio_msg_st] =
      val ____ptr = apply()
      (!____ptr).data = data
      (!____ptr).data_len = data_len
      (!____ptr).peer = peer
      (!____ptr).local = local
      (!____ptr).flags = flags
      ____ptr

    extension (struct: bio_msg_st)
      def data: Ptr[Byte] = struct._1
      def data_=(value: Ptr[Byte]): Unit = !struct.at1 = value
      def data_len: size_t = struct._2
      def data_len_=(value: size_t): Unit = !struct.at2 = value
      def peer: Ptr[BIO_ADDR] = struct._3
      def peer_=(value: Ptr[BIO_ADDR]): Unit = !struct.at3 = value
      def local: Ptr[BIO_ADDR] = struct._4
      def local_=(value: Ptr[BIO_ADDR]): Unit = !struct.at4 = value
      def flags: uint64_t = struct._5
      def flags_=(value: uint64_t): Unit = !struct.at5 = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type bio_poll_descriptor_st = CStruct2[uint32_t, bio_poll_descriptor_st.Value]

  object bio_poll_descriptor_st:
    given _tag: Tag[bio_poll_descriptor_st] =
      Tag.materializeCStruct2Tag[uint32_t, bio_poll_descriptor_st.Value]

    // Allocates bio_poll_descriptor_st on the heap – fields are not initalised or zeroed out
    def apply()(using Zone): Ptr[bio_poll_descriptor_st] =
      scala.scalanative.unsafe.alloc[bio_poll_descriptor_st](1)
    def apply(`type`: uint32_t, value: bio_poll_descriptor_st.Value)(using
        Zone,
    ): Ptr[bio_poll_descriptor_st] =
      val ____ptr = apply()
      (!____ptr).`type` = `type`
      (!____ptr).value = value
      ____ptr

    extension (struct: bio_poll_descriptor_st)
      def `type`: uint32_t = struct._1
      def type_=(value: uint32_t): Unit = !struct.at1 = value
      def value: bio_poll_descriptor_st.Value = struct._2
      def value_=(value: bio_poll_descriptor_st.Value): Unit = !struct.at2 = value

    /**
     * [bindgen] header: /usr/include/openssl/bio.h
     */
    opaque type Value = CArray[Byte, Nat._8]
    object Value:
      given _tag: Tag[Value] = Tag.CArray[CChar, Nat._8](Tag.Byte, Tag.Nat8)

      def apply()(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        ___ptr

      @scala.annotation.targetName("apply_fd")
      def apply(fd: CInt)(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[CInt]].update(0, fd)
        ___ptr

      @scala.annotation.targetName("apply_custom")
      def apply(custom: Ptr[Byte])(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[Ptr[Byte]]].update(0, custom)
        ___ptr

      @scala.annotation.targetName("apply_custom_ui")
      def apply(custom_ui: uintptr_t)(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[uintptr_t]].update(0, custom_ui)
        ___ptr

      @scala.annotation.targetName("apply_ssl")
      def apply(ssl: Ptr[SSL])(using Zone): Ptr[Value] =
        val ___ptr = _root_.scala.scalanative.unsafe.alloc[Value](1)
        val un = !___ptr
        un.at(0).asInstanceOf[Ptr[Ptr[SSL]]].update(0, ssl)
        ___ptr

      extension (struct: Value)
        def fd: CInt = !struct.at(0).asInstanceOf[Ptr[CInt]]
        def fd_=(value: CInt): Unit = !struct.at(0).asInstanceOf[Ptr[CInt]] = value
        def custom: Ptr[Byte] = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]]
        def custom_=(value: Ptr[Byte]): Unit = !struct.at(0).asInstanceOf[Ptr[Ptr[Byte]]] = value
        def custom_ui: uintptr_t = !struct.at(0).asInstanceOf[Ptr[uintptr_t]]
        def custom_ui_=(value: uintptr_t): Unit = !struct.at(0).asInstanceOf[Ptr[uintptr_t]] = value
        def ssl: Ptr[SSL] = !struct.at(0).asInstanceOf[Ptr[Ptr[SSL]]]
        def ssl_=(value: Ptr[SSL]): Unit = !struct.at(0).asInstanceOf[Ptr[Ptr[SSL]]] = value

  /**
   * [bindgen] header: /usr/include/openssl/bio.h
   */
  opaque type hostent = CStruct0

  object hostent:
    given _tag: Tag[hostent] = Tag.materializeCStruct0Tag
