package javafx.scene.control;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public abstract class FocusModel<T> {
   private ReadOnlyIntegerWrapper focusedIndex = new ReadOnlyIntegerWrapper(this, "focusedIndex", -1);
   private ReadOnlyObjectWrapper<T> focusedItem = new ReadOnlyObjectWrapper(this, "focusedItem");

   public FocusModel() {
      this.focusedIndexProperty().addListener(var1 -> this.setFocusedItem(this.getModelItem(this.getFocusedIndex())));
   }

   public final ReadOnlyIntegerProperty focusedIndexProperty() {
      return this.focusedIndex.getReadOnlyProperty();
   }

   public final int getFocusedIndex() {
      return this.focusedIndex.get();
   }

   final void setFocusedIndex(int var1) {
      this.focusedIndex.set(var1);
   }

   public final ReadOnlyObjectProperty<T> focusedItemProperty() {
      return this.focusedItem.getReadOnlyProperty();
   }

   public final T getFocusedItem() {
      return (T)this.focusedItemProperty().get();
   }

   final void setFocusedItem(T var1) {
      this.focusedItem.set(var1);
   }

   protected abstract int getItemCount();

   protected abstract T getModelItem(int var1);

   public boolean isFocused(int var1) {
      return var1 >= 0 && var1 < this.getItemCount() ? this.getFocusedIndex() == var1 : false;
   }

   public void focus(int var1) {
      if (var1 >= 0 && var1 < this.getItemCount()) {
         int var2 = this.getFocusedIndex();
         this.setFocusedIndex(var1);
         if (var2 == var1) {
            this.setFocusedItem(this.getModelItem(var1));
         }
      } else {
         this.setFocusedIndex(-1);
      }
   }

   public void focusPrevious() {
      if (this.getFocusedIndex() == -1) {
         this.focus(0);
      } else if (this.getFocusedIndex() > 0) {
         this.focus(this.getFocusedIndex() - 1);
      }
   }

   public void focusNext() {
      if (this.getFocusedIndex() == -1) {
         this.focus(0);
      } else if (this.getFocusedIndex() != this.getItemCount() - 1) {
         this.focus(this.getFocusedIndex() + 1);
      }
   }
}
