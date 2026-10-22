package impl.org.controlsfx.collections;

import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

public abstract class NonIterableChange<E> extends Change<E> {
   private final int from;
   private final int to;
   private boolean invalid = true;

   protected NonIterableChange(int from, int to, ObservableList<E> list) {
      super(list);
      this.from = from;
      this.to = to;
   }

   public int getFrom() {
      this.checkState();
      return this.from;
   }

   public int getTo() {
      this.checkState();
      return this.to;
   }

   protected int[] getPermutation() {
      this.checkState();
      return new int[0];
   }

   public boolean next() {
      if (this.invalid) {
         this.invalid = false;
         return true;
      } else {
         return false;
      }
   }

   public void reset() {
      this.invalid = true;
   }

   public void checkState() {
      if (this.invalid) {
         throw new IllegalStateException("Invalid change state: Make sure to call next() before inspecting the change.");
      }
   }

   public String toString() {
      boolean tempInvalid = this.invalid;
      this.invalid = false;
      String string;
      if (this.wasPermutated()) {
         string = ChangeHelper.permChangeToString(this.getPermutation());
      } else if (this.wasUpdated()) {
         string = ChangeHelper.updateChangeToString(this.from, this.to);
      } else {
         string = ChangeHelper.addRemoveChangeToString(this.from, this.to, this.getList(), this.getRemoved());
      }

      this.invalid = tempInvalid;
      return "{ " + string + " }";
   }

   public static class GenericAddRemoveChange<E> extends NonIterableChange<E> {
      private final List<E> removed;

      public GenericAddRemoveChange(int from, int to, List<E> removed, ObservableList<E> list) {
         super(from, to, list);
         this.removed = removed;
      }

      public List<E> getRemoved() {
         this.checkState();
         return this.removed;
      }
   }

   public static class SimpleAddChange<E> extends NonIterableChange<E> {
      public SimpleAddChange(int from, int to, ObservableList<E> list) {
         super(from, to, list);
      }

      public boolean wasRemoved() {
         this.checkState();
         return false;
      }

      public List<E> getRemoved() {
         this.checkState();
         return Collections.emptyList();
      }
   }

   public static class SimpleRemovedChange<E> extends NonIterableChange<E> {
      private final List<E> removed;

      public SimpleRemovedChange(int from, int to, E removed, ObservableList<E> list) {
         super(from, to, list);
         this.removed = Collections.singletonList(removed);
      }

      public boolean wasRemoved() {
         this.checkState();
         return true;
      }

      public List<E> getRemoved() {
         this.checkState();
         return this.removed;
      }
   }
}
