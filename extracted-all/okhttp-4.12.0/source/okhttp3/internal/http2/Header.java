package okhttp3.internal.http2

import okio.ByteString

data class Header(name: ByteString, value: ByteString) {
   @JvmStatic
   public Header.Companion Companion = new Header.Companion(null);
   @JvmField
   @JvmStatic
   public ByteString PSEUDO_PREFIX = ByteString.Companion.encodeUtf8(":");
   @JvmStatic
   public java.lang.String RESPONSE_STATUS_UTF8 = ":status";
   @JvmStatic
   public java.lang.String TARGET_METHOD_UTF8 = ":method";
   @JvmStatic
   public java.lang.String TARGET_PATH_UTF8 = ":path";
   @JvmStatic
   public java.lang.String TARGET_SCHEME_UTF8 = ":scheme";
   @JvmStatic
   public java.lang.String TARGET_AUTHORITY_UTF8 = ":authority";
   @JvmField
   @JvmStatic
   public ByteString RESPONSE_STATUS = ByteString.Companion.encodeUtf8(":status");
   @JvmField
   @JvmStatic
   public ByteString TARGET_METHOD = ByteString.Companion.encodeUtf8(":method");
   @JvmField
   @JvmStatic
   public ByteString TARGET_PATH = ByteString.Companion.encodeUtf8(":path");
   @JvmField
   @JvmStatic
   public ByteString TARGET_SCHEME = ByteString.Companion.encodeUtf8(":scheme");
   @JvmField
   @JvmStatic
   public ByteString TARGET_AUTHORITY = ByteString.Companion.encodeUtf8(":authority");

   public final val hpackSize: Int
   public final val name: ByteString
   public final val value: ByteString

   init {
      this.name = name;
      this.value = value;
      this.hpackSize = 32 + this.name.size() + this.value.size();
   }

   public constructor(name: String, value: String) : this(ByteString.Companion.encodeUtf8(name), ByteString.Companion.encodeUtf8(value))
   public constructor(name: ByteString, value: String) : this(name, ByteString.Companion.encodeUtf8(value))
   public override fun toString(): String {
      return this.name.utf8() + ": " + this.value.utf8();
   }

   public operator fun component1(): ByteString {
      return this.name;
   }

   public operator fun component2(): ByteString {
      return this.value;
   }

   public fun copy(name: ByteString, value: ByteString): Header {
      return new Header(name, value);
   }

   public override fun hashCode(): Int {
      return this.name.hashCode() * 31 + this.value.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Header) {
         return false;
      } else {
         var var2: Header = other as Header;
         if (!(this.name == (other as Header).name)) {
            return false;
         } else {
            return this.value == var2.value;
         }
      }
   }

   companion object Companion private constructor() {
      public final val PSEUDO_PREFIX: ByteString
      public final val RESPONSE_STATUS: ByteString
      public const val RESPONSE_STATUS_UTF8: String
      public final val TARGET_AUTHORITY: ByteString
      public const val TARGET_AUTHORITY_UTF8: String
      public final val TARGET_METHOD: ByteString
      public const val TARGET_METHOD_UTF8: String
      public final val TARGET_PATH: ByteString
      public const val TARGET_PATH_UTF8: String
      public final val TARGET_SCHEME: ByteString
      public const val TARGET_SCHEME_UTF8: String

   }
}
