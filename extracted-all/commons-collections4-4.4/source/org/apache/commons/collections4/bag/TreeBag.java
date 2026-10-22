package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.SortedBag;

public class TreeBag<E> extends AbstractMapBag<E> implements SortedBag<E>, Serializable {
   private static final long serialVersionUID = -7740146511091606676L;

   public TreeBag() {
      super(new TreeMap<>());
   }

   public TreeBag(Comparator<? super E> comparator) {
      super(new TreeMap<>(comparator));
   }

   public TreeBag(Collection<? extends E> coll) {
      this();
      this.addAll(coll);
   }

   @Override
   public boolean add(E object) {
      if (this.comparator() != null || object instanceof Comparable) {
         return super.add(object);
      } else if (object == null) {
         throw new NullPointerException();
      } else {
         throw new IllegalArgumentException(
            "Objects of type " + object.getClass() + " cannot be added to a naturally ordered TreeBag as it does not implement Comparable"
         );
      }
   }

   @Override
   public E first() {
      return this.getMap().firstKey();
   }

   @Override
   public E last() {
      return this.getMap().lastKey();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.getMap().comparator();
   }

   protected SortedMap<E, AbstractMapBag.MutableInteger> getMap() {
      return (SortedMap<E, AbstractMapBag.MutableInteger>)super.getMap();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.comparator());
      super.doWriteObject(out);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      Comparator<? super E> comp = (Comparator<? super E>)in.readObject();
      super.doReadObject(new TreeMap<>(comp), in);
   }
}
