package impl.org.controlsfx.collections;

import java.util.AbstractList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

public final class MappingChange<E, F> extends Change<F> {
   private final MappingChange.Map<E, F> map;
   private final Change<? extends E> original;
   private List<F> removed;
   public static final MappingChange.Map NOOP_MAP = new MappingChange.Map() {
      @Override
      public Object map(Object original) {
         return original;
      }
   };

   public MappingChange(Change<? extends E> original, MappingChange.Map<E, F> map, ObservableList<F> list) {
      super(list);
      this.original = original;
      this.map = map;
   }

   public boolean next() {
      return this.original.next();
   }

   public void reset() {
      this.original.reset();
   }

   public int getFrom() {
      return this.original.getFrom();
   }

   public int getTo() {
      return this.original.getTo();
   }

   public List<F> getRemoved() {
      if (this.removed == null) {
         this.removed = new AbstractList<F>() {
            @Override
            public F get(int index) {
               return MappingChange.this.map.map((E)MappingChange.this.original.getRemoved().get(index));
            }

            @Override
            public int size() {
               return MappingChange.this.original.getRemovedSize();
            }
         };
      }

      return this.removed;
   }

   protected int[] getPermutation() {
      return new int[0];
   }

   public boolean wasPermutated() {
      return this.original.wasPermutated();
   }

   public boolean wasUpdated() {
      return this.original.wasUpdated();
   }

   public int getPermutation(int i) {
      return this.original.getPermutation(i);
   }

   public String toString() {
      int size = 0;
      int pos = 0;
      int posToEnd = 0;

      while (this.next()) {
         posToEnd++;
      }

      this.reset();

      while (this.next()) {
         size++;
      }

      this.reset();
      StringBuilder b = new StringBuilder();
      b.append("{ ");

      while (this.next()) {
         if (this.wasPermutated()) {
            b.append(ChangeHelper.permChangeToString(this.getPermutation()));
         } else if (this.wasUpdated()) {
            b.append(ChangeHelper.updateChangeToString(this.getFrom(), this.getTo()));
         } else {
            b.append(ChangeHelper.addRemoveChangeToString(this.getFrom(), this.getTo(), this.getList(), this.getRemoved()));
         }

         if (pos != size) {
            b.append(", ");
         }
      }

      b.append(" }");
      this.reset();
      pos = size - posToEnd;

      while (pos-- > 0) {
         this.next();
      }

      return b.toString();
   }

   public interface Map<E, F> {
      F map(E var1);
   }
}
