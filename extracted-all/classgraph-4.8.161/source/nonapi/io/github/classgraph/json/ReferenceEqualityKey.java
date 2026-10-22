package nonapi.io.github.classgraph.json;

public class ReferenceEqualityKey<K> {
   private final K wrappedKey;

   public ReferenceEqualityKey(K wrappedKey) {
      this.wrappedKey = wrappedKey;
   }

   public K get() {
      return this.wrappedKey;
   }

   @Override
   public int hashCode() {
      K key = this.wrappedKey;
      return key == null ? 0 : System.identityHashCode(key);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof ReferenceEqualityKey) ? false : this.wrappedKey == ((ReferenceEqualityKey)obj).wrappedKey;
      }
   }

   @Override
   public String toString() {
      K key = this.wrappedKey;
      return key == null ? "null" : key.toString();
   }
}
