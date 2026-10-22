package com.sun.javafx.webkit.theme;

import com.sun.webkit.graphics.ScrollBarTheme;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;

public final class ScrollBarWidget extends ScrollBar implements RenderThemeImpl.Widget {
   private ScrollBarThemeImpl sbtImpl;
   private boolean thicknessInitialized;

   public ScrollBarWidget(ScrollBarThemeImpl var1) {
      ScrollBarWidgetHelper.initHelper(this);
      this.thicknessInitialized = false;
      this.sbtImpl = var1;
      this.setOrientation(Orientation.VERTICAL);
      this.setMin(0.0);
      this.setManaged(false);
   }

   private void doUpdatePeer() {
      this.initializeThickness();
   }

   @Override
   public RenderThemeImpl.WidgetType getType() {
      return RenderThemeImpl.WidgetType.SCROLLBAR;
   }

   protected void layoutChildren() {
      super.layoutChildren();
      this.initializeThickness();
   }

   private void initializeThickness() {
      if (!this.thicknessInitialized) {
         ScrollBar var1 = this.sbtImpl.getTestSBRef();
         if (var1 == null) {
            return;
         }

         int var2 = (int)var1.prefWidth(-1.0);
         if (var2 != 0 && ScrollBarTheme.getThickness() != var2) {
            ScrollBarTheme.setThickness(var2);
         }

         this.thicknessInitialized = true;
      }
   }

   static {
      ScrollBarWidgetHelper.setScrollBarWidgetAccessor(new ScrollBarWidgetHelper.ScrollBarWidgetAccessor() {
         @Override
         public void doUpdatePeer(Node var1) {
            ((ScrollBarWidget)var1).doUpdatePeer();
         }
      });
   }
}
