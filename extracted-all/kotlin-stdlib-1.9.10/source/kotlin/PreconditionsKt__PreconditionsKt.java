package kotlin

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPreconditions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Preconditions.kt\nkotlin/PreconditionsKt__PreconditionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,144:1\n1#2:145\n*E\n"])
// $VF: Class flags could not be determined
internal class PreconditionsKt__PreconditionsKt : PreconditionsKt__AssertionsJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun require(value: Boolean) {
      contract {
         returns() implies (value)
      }

      if (!value) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun require(value: Boolean, lazyMessage: () -> Any) {
      contract {
         returns() implies (value)
      }

      if (!value) {
         throw new IllegalArgumentException(lazyMessage.invoke().toString());
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T : Any> requireNotNull(value: T?): T {
      contract {
         returns() implies (value != null)
      }

      if (value == null) {
         throw new IllegalArgumentException("Required value was null.".toString());
      } else {
         return (T)value;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T : Any> requireNotNull(value: T?, lazyMessage: () -> Any): T {
      contract {
         returns() implies (value != null)
      }

      if (value == null) {
         throw new IllegalArgumentException(lazyMessage.invoke().toString());
      } else {
         return (T)value;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun check(value: Boolean) {
      contract {
         returns() implies (value)
      }

      if (!value) {
         throw new IllegalStateException("Check failed.".toString());
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun check(value: Boolean, lazyMessage: () -> Any) {
      contract {
         returns() implies (value)
      }

      if (!value) {
         throw new IllegalStateException(lazyMessage.invoke().toString());
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T : Any> checkNotNull(value: T?): T {
      contract {
         returns() implies (value != null)
      }

      if (value == null) {
         throw new IllegalStateException("Required value was null.".toString());
      } else {
         return (T)value;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T : Any> checkNotNull(value: T?, lazyMessage: () -> Any): T {
      contract {
         returns() implies (value != null)
      }

      if (value == null) {
         throw new IllegalStateException(lazyMessage.invoke().toString());
      } else {
         return (T)value;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun error(message: Any): Nothing {
      throw new IllegalStateException(message.toString());
   }

   open fun PreconditionsKt__PreconditionsKt() {
   }
}
