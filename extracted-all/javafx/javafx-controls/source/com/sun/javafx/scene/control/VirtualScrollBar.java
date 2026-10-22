package com.sun.javafx.scene.control;

import com.sun.javafx.util.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;

public class VirtualScrollBar extends ScrollBar {
   private final VirtualFlow flow;
   private boolean adjusting;
   private BooleanProperty virtual = new SimpleBooleanProperty(this, "virtual");

   public VirtualScrollBar(VirtualFlow var1) {
      this.flow = var1;
      super.valueProperty().addListener(var2 -> {
         if (this.isVirtual() && !this.adjusting) {
            var1.setPosition(this.getValue());
         }
      });
   }

   public final void setVirtual(boolean var1) {
      this.virtual.set(var1);
   }

   public final boolean isVirtual() {
      return this.virtual.get();
   }

   public final BooleanProperty virtualProperty() {
      return this.virtual;
   }

   @Override
   public void decrement() {
      if (this.isVirtual()) {
         this.flow.scrollPixels(-10.0);
      } else {
         super.decrement();
      }
   }

   @Override
   public void increment() {
      if (this.isVirtual()) {
         this.flow.scrollPixels(10.0);
      } else {
         super.increment();
      }
   }

   @Override
   public void adjustValue(double var1) {
      if (this.isVirtual()) {
         this.adjusting = true;
         double var3 = this.flow.getPosition();
         double var5 = (this.getMax() - this.getMin()) * Utils.clamp(0.0, var1, 1.0) + this.getMin();
         if (var5 < var3) {
            IndexedCell var7 = this.flow.getFirstVisibleCell();
            if (var7 == null) {
               return;
            }

            this.flow.scrollToBottom(var7);
         } else if (var5 > var3) {
            IndexedCell var8 = this.flow.getLastVisibleCell();
            if (var8 == null) {
               return;
            }

            this.flow.scrollToTop(var8);
         }

         this.adjusting = false;
      } else {
         super.adjustValue(var1);
      }
   }
}
