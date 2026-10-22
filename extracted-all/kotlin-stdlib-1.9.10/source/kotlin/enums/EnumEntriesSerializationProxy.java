package kotlin.enums

import java.io.Serializable

internal class EnumEntriesSerializationProxy<E extends java.lang.Enum<E>>(vararg entries: Any) : Serializable {
   @JvmStatic
   private EnumEntriesSerializationProxy.Companion Companion = new EnumEntriesSerializationProxy.Companion(null);
   @JvmStatic
   private long serialVersionUID = 0L;

   private final val c: Class<Any>

   init {
      var var10001: Class = entries.getClass().getComponentType();
      this.c = var10001;
   }

   private fun readResolve(): Any {
      var var10000: Array<Any> = this.c.getEnumConstants();
      return EnumEntriesKt.enumEntries((E[])var10000);
   }

   private companion object Companion private constructor() {
      private const val serialVersionUID: Long

   }
}
