package kotlin

import java.io.Serializable
import kotlin.jvm.functions.Function0

private class SynchronizedLazyImpl<T>(initializer: () -> Any, lock: Any?) : Lazy<T>, Serializable {
   private final var _value: Any?
   private final var initializer: (() -> Any)?
      private set
   private final val lock: Any
   public open val value: Any
      public open get() {
         if (this._value != UNINITIALIZED_VALUE.INSTANCE) {
            return (T)this._value;
         } else {
            synchronized (this.lock) {
               var var10000: Any;
               if (this._value != UNINITIALIZED_VALUE.INSTANCE) {
                  var10000 = (Function0)this._value;
               } else {
                  var10000 = this.initializer;
                  var typedValue: Any = var10000.invoke();
                  this._value = typedValue;
                  this.initializer = null;
                  var10000 = (Function0)typedValue;
               }

               return (T)var10000;
            }
         }
      }


   init {
      this.initializer = initializer;
      this._value = UNINITIALIZED_VALUE.INSTANCE;
      var var10001: Any = lock;
      if (lock == null) {
         var10001 = this;
      }

      this.lock = var10001;
   }

   public override fun isInitialized(): Boolean {
      return this._value != UNINITIALIZED_VALUE.INSTANCE;
   }

   public override fun toString(): String {
      return if (this.isInitialized()) java.lang.String.valueOf(this.getValue()) else "Lazy value not initialized yet.";
   }

   private fun writeReplace(): Any {
      return new InitializedLazyImpl(this.getValue());
   }
}
