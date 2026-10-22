package kotlin

// $VF: Class flags could not be determined
internal class LazyKt__LazyJVMKt {
   @JvmStatic
   public fun <T> lazy(initializer: () -> T): Lazy<T> {
      return new SynchronizedLazyImpl(initializer, null, 2, null);
   }

   @JvmStatic
   public fun <T> lazy(mode: LazyThreadSafetyMode, initializer: () -> T): Lazy<T> {
      var var10000: Lazy;
      switch (LazyKt__LazyJVMKt.WhenMappings.$EnumSwitchMapping$0[mode.ordinal()]) {
         case 1:
            var10000 = new SynchronizedLazyImpl(initializer, null, 2, null);
            break;
         case 2:
            var10000 = new SafePublicationLazyImpl(initializer);
            break;
         case 3:
            var10000 = new UnsafeLazyImpl(initializer);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @JvmStatic
   public fun <T> lazy(lock: Any?, initializer: () -> T): Lazy<T> {
      return new SynchronizedLazyImpl(initializer, lock);
   }

   open fun LazyKt__LazyJVMKt() {
   }
}
