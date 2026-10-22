package javafx.beans.binding;

import com.sun.javafx.binding.StringFormatter;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class SetExpression<E> implements ObservableSetValue<E> {
   private static final ObservableSet EMPTY_SET = new SetExpression.EmptyObservableSet();

   public ObservableSet<E> getValue() {
      return this.get();
   }

   public static <E> SetExpression<E> setExpression(final ObservableSetValue<E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Set must be specified.");
      } else {
         return (SetExpression<E>)(var0 instanceof SetExpression ? (SetExpression)var0 : new SetBinding<E>() {
            {
               super.bind(var0);
            }

            @Override
            public void dispose() {
               super.unbind(var0);
            }

            @Override
            protected ObservableSet<E> computeValue() {
               return var0.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
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

   public BooleanBinding isEqualTo(ObservableSet<?> var1) {
      return Bindings.equal(this, var1);
   }

   public BooleanBinding isNotEqualTo(ObservableSet<?> var1) {
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
      ObservableSet var1 = this.get();
      return var1 == null ? EMPTY_SET.size() : var1.size();
   }

   @Override
   public boolean isEmpty() {
      ObservableSet var1 = this.get();
      return var1 == null ? EMPTY_SET.isEmpty() : var1.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.contains(var1) : var2.contains(var1);
   }

   @Override
   public Iterator<E> iterator() {
      ObservableSet var1 = this.get();
      return var1 == null ? EMPTY_SET.iterator() : var1.iterator();
   }

   @Override
   public Object[] toArray() {
      ObservableSet var1 = this.get();
      return var1 == null ? EMPTY_SET.toArray() : var1.toArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      ObservableSet var2 = this.get();
      return (T[])(var2 == null ? EMPTY_SET.toArray(var1) : var2.toArray(var1));
   }

   @Override
   public boolean add(E var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.add(var1) : var2.add(var1);
   }

   @Override
   public boolean remove(Object var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.remove(var1) : var2.remove(var1);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.contains(var1) : var2.containsAll(var1);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.addAll(var1) : var2.addAll(var1);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.removeAll(var1) : var2.removeAll(var1);
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      ObservableSet var2 = this.get();
      return var2 == null ? EMPTY_SET.retainAll(var1) : var2.retainAll(var1);
   }

   @Override
   public void clear() {
      ObservableSet var1 = this.get();
      if (var1 == null) {
         EMPTY_SET.clear();
      } else {
         var1.clear();
      }
   }

   private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {
      private static final Iterator iterator = new Iterator() {
         @Override
         public boolean hasNext() {
            return false;
         }

         @Override
         public Object next() {
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };

      @Override
      public Iterator<E> iterator() {
         return iterator;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public void addListener(SetChangeListener<? super E> var1) {
      }

      @Override
      public void removeListener(SetChangeListener<? super E> var1) {
      }

      @Override
      public void addListener(InvalidationListener var1) {
      }

      @Override
      public void removeListener(InvalidationListener var1) {
      }
   }
}
