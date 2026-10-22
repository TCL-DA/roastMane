package kotlin

import java.io.Serializable

internal class InitializedLazyImpl<T>(value: Any) : Lazy<T>, Serializable {
   public open val value: Any

   init {
      this.value = (T)value;
   }

   public override fun isInitialized(): Boolean {
      return true;
   }

   public override fun toString(): String {
      return java.lang.String.valueOf(this.getValue());
   }
}
