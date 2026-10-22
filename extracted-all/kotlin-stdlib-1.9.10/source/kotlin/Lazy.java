package kotlin

interface Lazy<T> {
   public val value: Any

   public abstract fun isInitialized(): Boolean {
   }
}
