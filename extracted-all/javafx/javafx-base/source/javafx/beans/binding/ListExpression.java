package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class ListExpression<E> implements ObservableListValue<E> {
   private static final ObservableList EMPTY_LIST = FXCollections.emptyObservableList();

   public ObservableList<E> getValue() {
      return this.get();
   }

   public static <E> ListExpression<E> listExpression(final ObservableListValue<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("List must be specified.");
      } else {
         return (ListExpression<E>)(var0 instanceof ListExpression ? (ListExpression)var0 : new ListBinding<E>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected ObservableList<E> computeValue() {
               return var0.get();
            }

            @Override
            public ObservableList<ObservableListValue<E>> getDependencies() {
               return FXCollections.singletonObservableList(var0);
            }
         });
      }
   }

   public int getSize() {
      return this.size();
   }

   public abstract ReadOnlyIntegerProperty sizeProperty();

   public abstract ReadOnlyBooleanProperty emptyProperty();

   public ObjectBinding<E> valueAt(int var1) {
      return Bindings.valueAt(this, var1);
   }

   public ObjectBinding<E> valueAt(ObservableIntegerValue var1) {
      return Bindings.valueAt(this, var1);
   }

   public BooleanBinding isEqualTo(ObservableList<?> var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isNotEqualTo(ObservableList<?> var1) {
      return Bindings.notEqual(this, var1);
   }

   public BooleanBinding isNull() {
      return Bindings.isNull(this);
   }

   public BooleanBinding isNotNull() {
      return Bindings.isNotNull(this);
   }

   public StringBinding asString() {
      return (StringBinding)StringFormatter.convert(this);
   }

   @Override
   public int size() {
      ObservableList var1 = this.get();
      return var1 == null ? EMPTY_LIST.size() : var1.size();
   }

   @Override
   public boolean isEmpty() {
      ObservableList var1 = this.get();
      return var1 == null ? EMPTY_LIST.isEmpty() : var1.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.contains(var1) : var2.contains(var1);
   }

   @Override
   public Iterator<E> iterator() {
      ObservableList var1 = this.get();
      return var1 == null ? EMPTY_LIST.iterator() : var1.iterator();
   }

   @Override
   public Object[] toArray() {
      ObservableList var1 = this.get();
      return var1 == null ? EMPTY_LIST.toArray() : var1.toArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      ObservableList var2 = this.get();
      return (T[])(var2 == null ? EMPTY_LIST.toArray(var1) : var2.toArray(var1));
   }

   @Override
   public boolean add(E var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.add(var1) : var2.add(var1);
   }

   @Override
   public boolean remove(Object var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.remove(var1) : var2.remove(var1);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.contains(var1) : var2.containsAll(var1);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.addAll(var1) : var2.addAll(var1);
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      ObservableList var3 = this.get();
      return var3 == null ? EMPTY_LIST.addAll(var1, var2) : var3.addAll(var1, var2);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.removeAll(var1) : var2.removeAll(var1);
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.retainAll(var1) : var2.retainAll(var1);
   }

   @Override
   public void clear() {
      ObservableList var1 = this.get();
      if (var1 == null) {
         EMPTY_LIST.clear();
      } else {
         var1.clear();
      }
   }

   @Override
   public E get(int var1) {
      ObservableList var2 = this.get();
      return (E)(var2 == null ? EMPTY_LIST.get(var1) : var2.get(var1));
   }

   @Override
   public E set(int var1, E var2) {
      ObservableList var3 = this.get();
      return (E)(var3 == null ? EMPTY_LIST.set(var1, var2) : var3.set(var1, var2));
   }

   @Override
   public void add(int var1, E var2) {
      ObservableList var3 = this.get();
      if (var3 == null) {
         EMPTY_LIST.add(var1, var2);
      } else {
         var3.add(var1, var2);
      }
   }

   @Override
   public E remove(int var1) {
      ObservableList var2 = this.get();
      return (E)(var2 == null ? EMPTY_LIST.remove(var1) : var2.remove(var1));
   }

   @Override
   public int indexOf(Object var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.indexOf(var1) : var2.indexOf(var1);
   }

   @Override
   public int lastIndexOf(Object var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.lastIndexOf(var1) : var2.lastIndexOf(var1);
   }

   @Override
   public ListIterator<E> listIterator() {
      ObservableList var1 = this.get();
      return var1 == null ? EMPTY_LIST.listIterator() : var1.listIterator();
   }

   @Override
   public ListIterator<E> listIterator(int var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.listIterator(var1) : var2.listIterator(var1);
   }

   @Override
   public List<E> subList(int var1, int var2) {
      ObservableList var3 = this.get();
      return var3 == null ? EMPTY_LIST.subList(var1, var2) : var3.subList(var1, var2);
   }

   @Override
   public boolean addAll(E... var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.addAll(var1) : var2.addAll(var1);
   }

   @Override
   public boolean setAll(E... var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.setAll(var1) : var2.setAll(var1);
   }

   @Override
   public boolean setAll(Collection<? extends E> var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.setAll(var1) : var2.setAll(var1);
   }

   @Override
   public boolean removeAll(E... var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.removeAll(var1) : var2.removeAll(var1);
   }

   @Override
   public boolean retainAll(E... var1) {
      ObservableList var2 = this.get();
      return var2 == null ? EMPTY_LIST.retainAll(var1) : var2.retainAll(var1);
   }

   @Override
   public void remove(int var1, int var2) {
      ObservableList var3 = this.get();
      if (var3 == null) {
         EMPTY_LIST.remove(var1, var2);
      } else {
         var3.remove(var1, var2);
      }
   }
}
