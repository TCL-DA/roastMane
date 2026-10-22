package kotlin

import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class StandardKt__StandardKt {
   @InlineOnly
   @JvmStatic
   public inline fun TODO(): Nothing {
      throw new NotImplementedError(null, 1, null);
   }

   @InlineOnly
   @JvmStatic
   public inline fun TODO(reason: String): Nothing {
      throw new NotImplementedError("An operation is not implemented: " + reason);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <R> run(block: () -> R): R {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      return (R)block.invoke();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T, R> T.run(block: (T) -> R): R {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      return (R)block.invoke(`$this$run`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T, R> with(receiver: T, block: (T) -> R): R {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      return (R)block.invoke(receiver);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> T.apply(block: (T) -> Unit): T {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      block.invoke(`$this$apply`);
      return (T)`$this$apply`;
   }

   @InlineOnly
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T> T.also(block: (T) -> Unit): T {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      block.invoke(`$this$also`);
      return (T)`$this$also`;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T, R> T.let(block: (T) -> R): R {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      return (R)block.invoke(`$this$let`);
   }

   @InlineOnly
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T> T.takeIf(predicate: (T) -> Boolean): T? {
      contract {
         callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
      }

      return (T)(if (predicate.invoke(`$this$takeIf`)) `$this$takeIf` else null);
   }

   @InlineOnly
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T> T.takeUnless(predicate: (T) -> Boolean): T? {
      contract {
         callsInPlace(predicate, InvocationKind.EXACTLY_ONCE)
      }

      return (T)(if (!predicate.invoke(`$this$takeUnless`)) `$this$takeUnless` else null);
   }

   @InlineOnly
   @JvmStatic
   public inline fun repeat(times: Int, action: (Int) -> Unit) {
      contract {
         callsInPlace(action)
      }

      for (int index = 0; index < times; index++) {
         action.invoke(index);
      }
   }

   open fun StandardKt__StandardKt() {
   }
}
