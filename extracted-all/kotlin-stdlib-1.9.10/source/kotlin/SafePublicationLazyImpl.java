package kotlin

import java.io.Serializable
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater

private class SafePublicationLazyImpl<T>(initializer: () -> Any) : Lazy<T>, Serializable {
   @JvmStatic
   public SafePublicationLazyImpl.Companion Companion = new SafePublicationLazyImpl.Companion(null);
   @JvmStatic
   private AtomicReferenceFieldUpdater<SafePublicationLazyImpl<?>, Object> valueUpdater = AtomicReferenceFieldUpdater.newUpdater(
      SafePublicationLazyImpl.class, Object.class, "_value"
   );

   private final var _value: Any?
   private final val final: Any
   private final var initializer: (() -> Any)?
      private set
   public open val value: Any
      public open get() {
         if (this._value != UNINITIALIZED_VALUE.INSTANCE) {
            return (T)this._value;
         } else {
            if (this.initializer != null) {
               var newValue: Any = this.initializer.invoke();
               if (valueUpdater.compareAndSet(this, UNINITIALIZED_VALUE.INSTANCE, newValue)) {
                  this.initializer = null;
                  return (T)newValue;
               }
            }

            return (T)this._value;
         }
      }


   init {
      this.initializer = initializer;
      this._value = UNINITIALIZED_VALUE.INSTANCE;
      this.final = UNINITIALIZED_VALUE.INSTANCE;
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

   companion object Companion private constructor() {
      private final val valueUpdater: AtomicReferenceFieldUpdater<SafePublicationLazyImpl<*>, Any>

   }
}
