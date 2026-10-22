package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.AccordionBehavior;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.shape.Rectangle;

public class AccordionSkin extends SkinBase<Accordion> {
   private TitledPane firstTitledPane;
   private Rectangle clipRect;
   private boolean forceRelayout = false;
   private boolean relayout = false;
   private double previousHeight = 0.0;
   private TitledPane expandedPane = null;
   private TitledPane previousPane = null;
   private Map<TitledPane, ChangeListener<Boolean>> listeners = new HashMap<>();
   private final BehaviorBase<Accordion> behavior;

   public AccordionSkin(Accordion var1) {
      super(var1);
      this.behavior = new AccordionBehavior(var1);
      var1.getPanes().addListener(var2 -> {
         if (this.firstTitledPane != null) {
            this.firstTitledPane.getStyleClass().remove("first-titled-pane");
         }

         if (!var1.getPanes().isEmpty()) {
            this.firstTitledPane = (TitledPane)var1.getPanes().get(0);
            this.firstTitledPane.getStyleClass().add("first-titled-pane");
         } else {
            this.firstTitledPane = null;
         }

         this.getChildren().setAll(var1.getPanes());

         while (var2.next()) {
            this.removeTitledPaneListeners(var2.getRemoved());
            this.initTitledPaneListeners(var2.getAddedSubList());
         }

         this.forceRelayout = true;
      });
      if (!var1.getPanes().isEmpty()) {
         this.firstTitledPane = (TitledPane)var1.getPanes().get(0);
         this.firstTitledPane.getStyleClass().add("first-titled-pane");
      }

      this.clipRect = new Rectangle(var1.getWidth(), var1.getHeight());
      this.getSkinnable().setClip(this.clipRect);
      this.initTitledPaneListeners(var1.getPanes());
      this.getChildren().setAll(var1.getPanes());
      this.getSkinnable().requestLayout();
      this.registerChangeListener(this.getSkinnable().widthProperty(), var1x -> this.clipRect.setWidth(this.getSkinnable().getWidth()));
      this.registerChangeListener(this.getSkinnable().heightProperty(), var1x -> {
         this.clipRect.setHeight(this.getSkinnable().getHeight());
         this.relayout = true;
      });
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.expandedPane != null) {
         var11 += this.expandedPane.minHeight(var1);
      }

      if (this.previousPane != null && !this.previousPane.equals(this.expandedPane)) {
         var11 += this.previousPane.minHeight(var1);
      }

      for (Node var14 : this.getChildren()) {
         TitledPane var15 = (TitledPane)var14;
         if (!var15.equals(this.expandedPane) && !var15.equals(this.previousPane)) {
            Skin var16 = ((TitledPane)var14).getSkin();
            if (var16 instanceof TitledPaneSkin) {
               TitledPaneSkin var17 = (TitledPaneSkin)var16;
               var11 += var17.getTitleRegionSize(var1);
            } else {
               var11 += var15.minHeight(var1);
            }
         }
      }

      return var11 + var3 + var7;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      if (this.expandedPane != null) {
         var11 += this.expandedPane.prefHeight(var1);
      }

      if (this.previousPane != null && !this.previousPane.equals(this.expandedPane)) {
         var11 += this.previousPane.prefHeight(var1);
      }

      for (Node var14 : this.getChildren()) {
         TitledPane var15 = (TitledPane)var14;
         if (!var15.equals(this.expandedPane) && !var15.equals(this.previousPane)) {
            Skin var16 = ((TitledPane)var14).getSkin();
            if (var16 instanceof TitledPaneSkin) {
               TitledPaneSkin var17 = (TitledPaneSkin)var16;
               var11 += var17.getTitleRegionSize(var1);
            } else {
               var11 += var15.prefHeight(var1);
            }
         }
      }

      return var11 + var3 + var7;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      boolean var9 = this.forceRelayout || this.relayout && this.previousHeight != var7;
      this.forceRelayout = false;
      this.previousHeight = var7;
      double var10 = 0.0;

      for (TitledPane var13 : this.getSkinnable().getPanes()) {
         if (!var13.equals(this.expandedPane)) {
            TitledPaneSkin var14 = (TitledPaneSkin)var13.getSkin();
            var10 += this.snapSizeY(var14.getTitleRegionSize(var5));
         }
      }

      double var24 = var7 - var10;

      for (TitledPane var15 : this.getSkinnable().getPanes()) {
         Skin var16 = var15.getSkin();
         double var17;
         if (var16 instanceof TitledPaneSkin) {
            ((TitledPaneSkin)var16).setMaxTitledPaneHeightForAccordion(var24);
            var17 = this.snapSizeY(((TitledPaneSkin)var16).getTitledPaneHeightForAccordion());
         } else {
            var17 = var15.prefHeight(var5);
         }

         var15.resize(var5, var17);
         boolean var19 = true;
         if (!var9 && this.previousPane != null && this.expandedPane != null) {
            ObservableList var20 = this.getSkinnable().getPanes();
            int var21 = var20.indexOf(this.previousPane);
            int var22 = var20.indexOf(this.expandedPane);
            int var23 = var20.indexOf(var15);
            if (var21 < var22) {
               if (var23 <= var22) {
                  var15.relocate(var1, var3);
                  var3 += var17;
                  var19 = false;
               }
            } else if (var21 > var22) {
               if (var23 <= var21) {
                  var15.relocate(var1, var3);
                  var3 += var17;
                  var19 = false;
               }
            } else {
               var15.relocate(var1, var3);
               var3 += var17;
               var19 = false;
            }
         }

         if (var19) {
            var15.relocate(var1, var3);
            var3 += var17;
         }
      }
   }

   private void initTitledPaneListeners(List<? extends TitledPane> var1) {
      for (TitledPane var3 : var1) {
         var3.setExpanded(var3 == this.getSkinnable().getExpandedPane());
         if (var3.isExpanded()) {
            this.expandedPane = var3;
         }

         ChangeListener var4 = this.expandedPropertyListener(var3);
         var3.expandedProperty().addListener(var4);
         this.listeners.put(var3, var4);
      }
   }

   private void removeTitledPaneListeners(List<? extends TitledPane> var1) {
      for (TitledPane var3 : var1) {
         if (this.listeners.containsKey(var3)) {
            var3.expandedProperty().removeListener(this.listeners.get(var3));
            this.listeners.remove(var3);
         }
      }
   }

   private ChangeListener<Boolean> expandedPropertyListener(TitledPane var1) {
      return (var2, var3, var4) -> {
         this.previousPane = this.expandedPane;
         Accordion var5 = this.getSkinnable();
         if (var4) {
            if (this.expandedPane != null) {
               this.expandedPane.setExpanded(false);
            }

            if (var1 != null) {
               var5.setExpandedPane(var1);
            }

            this.expandedPane = var5.getExpandedPane();
         } else {
            this.expandedPane = null;
            var5.setExpandedPane(null);
         }
      };
   }
}
