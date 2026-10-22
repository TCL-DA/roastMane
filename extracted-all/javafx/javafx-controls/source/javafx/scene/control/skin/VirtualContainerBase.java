package javafx.scene.control.skin;

import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.control.SkinBase;

public abstract class VirtualContainerBase<C extends Control, I extends IndexedCell> extends SkinBase<C> {
   private boolean itemCountDirty;
   private final VirtualFlow<I> flow = this.createVirtualFlow();
   private EventHandler<? super ScrollToEvent<Integer>> scrollToEventHandler = var1x -> {
      if (this.itemCountDirty) {
         this.updateItemCount();
         this.itemCountDirty = false;
      }

      this.flow.scrollToTop((Integer)var1x.getScrollTarget());
   };

   public VirtualContainerBase(C var1) {
      super((C)var1);
      var1.addEventHandler(ScrollToEvent.scrollToTopIndex(), this.scrollToEventHandler);
   }

   protected abstract int getItemCount();

   protected abstract void updateItemCount();

   protected VirtualFlow<I> createVirtualFlow() {
      return new VirtualFlow<>();
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         this.getSkinnable().removeEventHandler(ScrollToEvent.scrollToTopIndex(), this.scrollToEventHandler);
         super.dispose();
      }
   }

   protected final VirtualFlow<I> getVirtualFlow() {
      return this.flow;
   }

   protected final void markItemCountDirty() {
      this.itemCountDirty = true;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.checkState();
   }

   double getMaxCellWidth(int var1) {
      return this.snappedLeftInset() + this.flow.getMaxCellWidth(var1) + this.snappedRightInset();
   }

   double getVirtualFlowPreferredHeight(int var1) {
      double var2 = 1.0;

      for (int var4 = 0; var4 < var1 && var4 < this.getItemCount(); var4++) {
         var2 += this.flow.getCellLength(var4);
      }

      return var2 + this.snappedTopInset() + this.snappedBottomInset();
   }

   void checkState() {
      if (this.itemCountDirty) {
         this.updateItemCount();
         this.itemCountDirty = false;
      }
   }

   void requestRebuildCells() {
      this.flow.rebuildCells();
   }
}
