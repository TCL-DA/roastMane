package kotlin.collections

import java.io.Serializable
import kotlin.jvm.internal.markers.KMappedMarker

private object EmptyMap private constructor() : java.util.Map, Serializable, KMappedMarker {
   @JvmStatic
   public EmptyMap INSTANCE = new EmptyMap();

   public open val entries: Set<kotlin.collections.Map.Entry<Any?, Nothing>>
      public open get() {
         return EmptySet.INSTANCE;
      }

   public open val keys: Set<Any?>
      public open get() {
         return EmptySet.INSTANCE;
      }

   private const val serialVersionUID: Long = 8246714829545688274L
   public open val size: Int
      public open get() {
         return 0;
      }

   public open val values: Collection<Nothing>
      public open get() {
         return EmptyList.INSTANCE;
      }



   public override operator fun equals(other: Any?): Boolean {
      return other is java.util.Map && (other as java.util.Map).isEmpty();
   }

   public override fun hashCode(): Int {
      return 0;
   }

   public override fun toString(): String {
      return "{}";
   }

   public override fun isEmpty(): Boolean {
      return true;
   }

   public override fun containsKey(key: Any?): Boolean {
      return false;
   }

   public open fun containsValue(value: Nothing): Boolean {
      return false;
   }

   public open operator fun get(key: Any?): Nothing? {
      return null;
   }

   private fun readResolve(): Any {
      return INSTANCE;
   }

   override fun clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   fun put(key: Any, value: Void): Void {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun putAll(from: java.util.Map) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   fun remove(key: Any): Void {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
