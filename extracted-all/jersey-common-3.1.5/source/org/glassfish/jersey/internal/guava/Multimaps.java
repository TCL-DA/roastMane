package org.glassfish.jersey.internal.guava;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public final class Multimaps {
   private Multimaps() {
   }

   public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory) {
      return new Multimaps.CustomListMultimap<>(map, factory);
   }

   static boolean equalsImpl(Multimap<?, ?> multimap, Object object) {
      if (object == multimap) {
         return true;
      } else if (object instanceof Multimap) {
         Multimap<?, ?> that = (Multimap<?, ?>)object;
         return multimap.asMap().equals(that.asMap());
      } else {
         return false;
      }
   }

   private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V> {
      private static final long serialVersionUID = 0L;
      transient Supplier<? extends List<V>> factory;

      CustomListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory) {
         super(map);
         this.factory = Preconditions.checkNotNull(factory);
      }

      @Override
      protected List<V> createCollection() {
         return (List<V>)this.factory.get();
      }

      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.factory);
         stream.writeObject(this.backingMap());
      }

      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.factory = (Supplier<? extends List<V>>)stream.readObject();
         Map<K, Collection<V>> map = (Map<K, Collection<V>>)stream.readObject();
         this.setMap(map);
      }
   }

   abstract static class Entries<K, V> extends AbstractCollection<Entry<K, V>> {
      abstract Multimap<K, V> multimap();

      @Override
      public int size() {
         return this.multimap().size();
      }

      @Override
      public boolean contains(Object o) {
         if (o instanceof Entry) {
            Entry<?, ?> entry = (Entry<?, ?>)o;
            return this.multimap().containsEntry(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object o) {
         if (o instanceof Entry) {
            Entry<?, ?> entry = (Entry<?, ?>)o;
            return this.multimap().remove(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         this.multimap().clear();
      }
   }
}
