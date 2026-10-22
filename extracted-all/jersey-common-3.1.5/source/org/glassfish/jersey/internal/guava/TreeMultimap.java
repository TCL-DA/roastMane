package org.glassfish.jersey.internal.guava;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V> {
   private static final long serialVersionUID = 0L;
   private transient Comparator<? super K> keyComparator;
   private transient Comparator<? super V> valueComparator;

   private TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
      super(new TreeMap<>(keyComparator));
      this.keyComparator = keyComparator;
      this.valueComparator = valueComparator;
   }

   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
      return new TreeMultimap<>(Ordering.natural(), Ordering.natural());
   }

   @Override
   SortedSet<V> createCollection() {
      return new TreeSet<>(this.valueComparator);
   }

   @Override
   Collection<V> createCollection(K key) {
      if (key == null) {
         this.keyComparator().compare(key, key);
      }

      return super.createCollection(key);
   }

   private Comparator<? super K> keyComparator() {
      return this.keyComparator;
   }

   @Override
   public Comparator<? super V> valueComparator() {
      return this.valueComparator;
   }

   NavigableMap<K, Collection<V>> backingMap() {
      return (NavigableMap<K, Collection<V>>)super.backingMap();
   }

   public NavigableSet<V> get(K key) {
      return (NavigableSet<V>)super.get(key);
   }

   @Override
   Collection<V> unmodifiableCollectionSubclass(Collection<V> collection) {
      return Sets.unmodifiableNavigableSet((NavigableSet<V>)collection);
   }

   @Override
   Collection<V> wrapCollection(K key, Collection<V> collection) {
      return new AbstractMapBasedMultimap.WrappedNavigableSet(key, (NavigableSet<V>)collection, null);
   }

   public NavigableSet<K> keySet() {
      return (NavigableSet<K>)super.keySet();
   }

   NavigableSet<K> createKeySet() {
      return new AbstractMapBasedMultimap.NavigableKeySet(this.backingMap());
   }

   public NavigableMap<K, Collection<V>> asMap() {
      return (NavigableMap<K, Collection<V>>)super.asMap();
   }

   NavigableMap<K, Collection<V>> createAsMap() {
      return new AbstractMapBasedMultimap.NavigableAsMap(this.backingMap());
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(this.keyComparator());
      stream.writeObject(this.valueComparator());
      Serialization.writeMultimap(this, stream);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.keyComparator = Preconditions.checkNotNull((Comparator<? super K>)stream.readObject());
      this.valueComparator = Preconditions.checkNotNull((Comparator<? super V>)stream.readObject());
      this.setMap(new TreeMap<>(this.keyComparator));
      Serialization.populateMultimap(this, stream);
   }
}
