package kotlin

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nAssertionsJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AssertionsJVM.kt\nkotlin/PreconditionsKt__AssertionsJVMKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,39:1\n1#2:40\n*E\n"])
// $VF: Class flags could not be determined
internal class PreconditionsKt__AssertionsJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun assert(value: Boolean) {
      if (_Assertions.ENABLED && !value) {
         throw new AssertionError("Assertion failed");
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun assert(value: Boolean, lazyMessage: () -> Any) {
      if (_Assertions.ENABLED && !value) {
         throw new AssertionError(lazyMessage.invoke());
      }
   }

   open fun PreconditionsKt__AssertionsJVMKt() {
   }
}
