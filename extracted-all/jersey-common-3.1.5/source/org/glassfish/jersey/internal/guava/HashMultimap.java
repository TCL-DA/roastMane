package org.glassfish.jersey.internal.guava;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class HashMultimap<K, V> extends AbstractSetMultimap<K, V> {
   private static final int DEFAULT_VALUES_PER_KEY = 2;
   private static final long serialVersionUID = 0L;
   private transient int expectedValuesPerKey = 2;

   private HashMultimap() {
      super(new HashMap<>());
   }

   public static <K, V> HashMultimap<K, V> create() {
      return new HashMultimap<>();
   }

   @Override
   Set<V> createCollection() {
      return Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeInt(this.expectedValuesPerKey);
      Serialization.writeMultimap(this, stream);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.expectedValuesPerKey = stream.readInt();
      int distinctKeys = Serialization.readCount(stream);
      Map<K, Collection<V>> map = Maps.newHashMapWithExpectedSize(distinctKeys);
      this.setMap(map);
      Serialization.populateMultimap(this, stream, distinctKeys);
   }
}
