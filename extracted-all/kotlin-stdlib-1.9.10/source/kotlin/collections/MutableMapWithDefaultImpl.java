package kotlin.collections

import kotlin.collections.MutableMap.MutableEntry
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nMapWithDefault.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MapWithDefault.kt\nkotlin/collections/MutableMapWithDefaultImpl\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,104:1\n341#2,6:105\n*S KotlinDebug\n*F\n+ 1 MapWithDefault.kt\nkotlin/collections/MutableMapWithDefaultImpl\n*L\n101#1:105,6\n*E\n"])
private class MutableMapWithDefaultImpl<K, V>(map: MutableMap<Any, Any>, default: (Any) -> Any) : MutableMapWithDefault<K, V> {
   private final val default: (Any) -> Any
   public open val entries: MutableSet<MutableEntry<Any, Any>>
      public open get() {
         return this.getMap().entrySet();
      }

   public open val keys: MutableSet<Any>
      public open get() {
         return this.getMap().keySet();
      }

   public open val map: MutableMap<Any, Any>
   public open val size: Int
      public open get() {
         return this.getMap().size();
      }

   public open val values: MutableCollection<Any>
      public open get() {
         return this.getMap().values();
      }


   init {
      this.map = map;
      this.default = var2;
   }

   public override operator fun equals(other: Any?): Boolean {
      return this.getMap().equals(other);
   }

   public override fun hashCode(): Int {
      return this.getMap().hashCode();
   }

   public override fun toString(): String {
      return this.getMap().toString();
   }

   public override fun isEmpty(): Boolean {
      return this.getMap().isEmpty();
   }

   public override fun containsKey(key: Any): Boolean {
      return this.getMap().containsKey(key);
   }

   public override fun containsValue(value: Any): Boolean {
      return this.getMap().containsValue(value);
   }

   public override operator fun get(key: Any): Any? {
      return this.getMap().get(key);
   }

   public override fun put(key: Any, value: Any): Any? {
      return this.getMap().put((K)key, (V)value);
   }

   public override fun remove(key: Any): Any? {
      return this.getMap().remove(key);
   }

   public override fun putAll(from: Map<out Any, Any>) {
      this.getMap().putAll(from);
   }

   public override fun clear() {
      this.getMap().clear();
   }

   public override fun getOrImplicitDefault(key: Any): Any {
      var `$this$getOrElseNullable$iv`: java.util.Map = this.getMap();
      var `value$iv`: Any = `$this$getOrElseNullable$iv`.get(key);
      return (V)(if (`value$iv` == null && !`$this$getOrElseNullable$iv`.containsKey(key)) this.default.invoke((K)key) else `value$iv`);
   }
}
