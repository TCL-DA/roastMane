package kotlin

import java.io.Serializable
import kotlin.jvm.functions.Function0

internal class UnsafeLazyImpl<T>(initializer: () -> Any) : Lazy<T>, Serializable {
   private final var _value: Any?
   private final var initializer: (() -> Any)?
      private set
   public open val value: Any
      public open get() {
         if (this._value === UNINITIALIZED_VALUE.INSTANCE) {
            var var10001: Function0 = this.initializer;
            this._value = var10001.invoke();
            this.initializer = null;
         }

         return (T)this._value;
      }


   init {
      this.initializer = initializer;
      this._value = UNINITIALIZED_VALUE.INSTANCE;
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
