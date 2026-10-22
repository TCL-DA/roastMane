package javafx.scene.control;

public abstract class SingleSelectionModel<T> extends SelectionModel<T> {
   @Override
   public void clearSelection() {
      this.updateSelectedIndex(-1);
   }

   @Override
   public void clearSelection(int var1) {
      if (this.getSelectedIndex() == var1) {
         this.clearSelection();
      }
   }

   @Override
   public boolean isEmpty() {
      return this.getItemCount() == 0 || this.getSelectedIndex() == -1;
   }

   @Override
   public boolean isSelected(int var1) {
      return this.getSelectedIndex() == var1;
   }

   @Override
   public void clearAndSelect(int var1) {
      this.select(var1);
   }

   @Override
   public void select(T var1) {
      if (var1 == null) {
         this.setSelectedIndex(-1);
         this.setSelectedItem(null);
      } else {
         int var2 = this.getItemCount();

         for (int var3 = 0; var3 < var2; var3++) {
            Object var4 = this.getModelItem(var3);
            if (var4 != null && var4.equals(var1)) {
               this.select(var3);
               return;
            }
         }

         this.setSelectedItem((T)var1);
      }
   }

   @Override
   public void select(int var1) {
      if (var1 == -1) {
         this.clearSelection();
      } else {
         int var2 = this.getItemCount();
         if (var2 != 0 && var1 >= 0 && var1 < var2) {
            this.updateSelectedIndex(var1);
         }
      }
   }

   @Override
   public void selectPrevious() {
      if (this.getSelectedIndex() != 0) {
         this.select(this.getSelectedIndex() - 1);
      }
   }

   @Override
   public void selectNext() {
      this.select(this.getSelectedIndex() + 1);
   }

   @Override
   public void selectFirst() {
      if (this.getItemCount() > 0) {
         this.select(0);
      }
   }

   @Override
   public void selectLast() {
      int var1 = this.getItemCount();
      if (var1 > 0 && this.getSelectedIndex() < var1 - 1) {
         this.select(var1 - 1);
      }
   }

   protected abstract T getModelItem(int var1);

   protected abstract int getItemCount();

   private void updateSelectedIndex(int var1) {
      int var2 = this.getSelectedIndex();
      Object var3 = this.getSelectedItem();
      this.setSelectedIndex(var1);
      if (var2 != -1 || var3 == null || var1 != -1) {
         this.setSelectedItem(this.getModelItem(this.getSelectedIndex()));
      }
   }
}
