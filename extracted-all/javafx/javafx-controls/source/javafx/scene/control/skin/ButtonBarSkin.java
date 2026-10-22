package javafx.scene.control.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ButtonBarSkin extends SkinBase<ButtonBar> {
   private static final double GAP_SIZE = 10.0;
   private static final String CATEGORIZED_TYPES = "LRHEYNXBIACO";
   private static final double DO_NOT_CHANGE_SIZE = Double.MAX_VALUE;
   private HBox layout;
   private InvalidationListener buttonDataListener = var1x -> this.layoutButtons();

   public ButtonBarSkin(ButtonBar var1) {
      super(var1);
      this.layout = new HBox(10.0) {
         protected void layoutChildren() {
            ButtonBarSkin.this.resizeButtons();
            super.layoutChildren();
         }
      };
      this.layout.setAlignment(Pos.CENTER);
      this.layout.getStyleClass().add("container");
      this.getChildren().add(this.layout);
      this.layoutButtons();
      this.updateButtonListeners(var1.getButtons(), true);
      var1.getButtons().addListener(var1x -> {
         while (var1x.next()) {
            this.updateButtonListeners(var1x.getRemoved(), false);
            this.updateButtonListeners(var1x.getAddedSubList(), true);
         }

         this.layoutButtons();
      });
      this.registerChangeListener(var1.buttonOrderProperty(), var1x -> this.layoutButtons());
      this.registerChangeListener(var1.buttonMinWidthProperty(), var1x -> this.resizeButtons());
   }

   private void updateButtonListeners(List<? extends Node> var1, boolean var2) {
      if (var1 != null) {
         for (Node var4 : var1) {
            ObservableMap var5 = var4.getProperties();
            if (var5.containsKey("javafx.scene.control.ButtonBar.ButtonData")) {
               ObjectProperty var6 = (ObjectProperty)var5.get("javafx.scene.control.ButtonBar.ButtonData");
               if (var6 != null) {
                  if (var2) {
                     var6.addListener(this.buttonDataListener);
                  } else {
                     var6.removeListener(this.buttonDataListener);
                  }
               }
            }
         }
      }
   }

   private void layoutButtons() {
      ButtonBar var1 = this.getSkinnable();
      ObservableList var2 = var1.getButtons();
      double var3 = var1.getButtonMinWidth();
      String var5 = this.getSkinnable().getButtonOrder();
      this.layout.getChildren().clear();
      if (var5 == null) {
         throw new IllegalStateException("ButtonBar buttonOrder string can not be null");
      } else {
         if (var5.equals("")) {
            ButtonBarSkin.Spacer.DYNAMIC.add(this.layout, true);

            for (Node var7 : var2) {
               this.sizeButton(var7, var3, Double.MAX_VALUE, Double.MAX_VALUE);
               this.layout.getChildren().add(var7);
               HBox.setHgrow(var7, Priority.NEVER);
            }
         } else {
            this.doButtonOrderLayout(var5);
         }
      }
   }

   private void doButtonOrderLayout(String var1) {
      ButtonBar var2 = this.getSkinnable();
      ObservableList var3 = var2.getButtons();
      double var4 = var2.getButtonMinWidth();
      Map var6 = this.buildButtonMap(var3);
      char[] var7 = var1.toCharArray();
      int var8 = 0;
      ButtonBarSkin.Spacer var9 = ButtonBarSkin.Spacer.NONE;

      for (int var10 = 0; var10 < var7.length; var10++) {
         char var11 = var7[var10];
         boolean var12 = var8 <= 0 && var8 >= var3.size() - 1;
         boolean var13 = !this.layout.getChildren().isEmpty();
         if (var11 == '+') {
            var9 = var9.replace(ButtonBarSkin.Spacer.DYNAMIC);
         } else if (var11 == '_' && var13) {
            var9 = var9.replace(ButtonBarSkin.Spacer.FIXED);
         } else {
            List var14 = (List)var6.get(String.valueOf(var11).toUpperCase());
            if (var14 != null) {
               var9.add(this.layout, var12);

               for (Node var16 : var14) {
                  this.sizeButton(var16, var4, Double.MAX_VALUE, Double.MAX_VALUE);
                  this.layout.getChildren().add(var16);
                  HBox.setHgrow(var16, Priority.NEVER);
                  var8++;
               }

               var9 = var9.replace(ButtonBarSkin.Spacer.NONE);
            }
         }
      }

      boolean var17 = false;
      int var19 = var3.size();

      for (int var20 = 0; var20 < var19; var20++) {
         Node var22 = (Node)var3.get(var20);
         if (var22 instanceof Button && ((Button)var22).isDefaultButton()) {
            var22.requestFocus();
            var17 = true;
            break;
         }
      }

      if (!var17) {
         for (int var21 = 0; var21 < var19; var21++) {
            Node var23 = (Node)var3.get(var21);
            ButtonBar.ButtonData var24 = ButtonBar.getButtonData(var23);
            if (var24 != null && var24.isDefaultButton()) {
               var23.requestFocus();
               var17 = true;
               break;
            }
         }
      }
   }

   private void resizeButtons() {
      ButtonBar var1 = this.getSkinnable();
      double var2 = var1.getButtonMinWidth();
      ObservableList var4 = var1.getButtons();
      double var5 = var2;

      for (Node var8 : var4) {
         if (ButtonBar.isButtonUniformSize(var8)) {
            var5 = Math.max(var8.prefWidth(-1.0), var5);
         }
      }

      for (Node var10 : var4) {
         if (ButtonBar.isButtonUniformSize(var10)) {
            this.sizeButton(var10, Double.MAX_VALUE, var5, Double.MAX_VALUE);
         }
      }
   }

   private void sizeButton(Node var1, double var2, double var4, double var6) {
      if (var1 instanceof Region) {
         Region var8 = (Region)var1;
         if (var2 != Double.MAX_VALUE) {
            var8.setMinWidth(var2);
         }

         if (var4 != Double.MAX_VALUE) {
            var8.setPrefWidth(var4);
         }

         if (var6 != Double.MAX_VALUE) {
            var8.setMaxWidth(var6);
         }
      }
   }

   private String getButtonType(Node var1) {
      ButtonBar.ButtonData var2 = ButtonBar.getButtonData(var1);
      if (var2 == null) {
         var2 = ButtonBar.ButtonData.OTHER;
      }

      String var3 = var2.getTypeCode();
      var3 = var3.length() > 0 ? var3.substring(0, 1) : "";
      return "LRHEYNXBIACO".contains(var3.toUpperCase()) ? var3 : ButtonBar.ButtonData.OTHER.getTypeCode();
   }

   private Map<String, List<Node>> buildButtonMap(List<? extends Node> var1) {
      HashMap var2 = new HashMap();

      for (Node var4 : var1) {
         if (var4 != null) {
            String var5 = this.getButtonType(var4);
            Object var6 = (List)var2.get(var5);
            if (var6 == null) {
               var6 = new ArrayList();
               var2.put(var5, var6);
            }

            var6.add(var4);
         }
      }

      return var2;
   }

   private static enum Spacer {
      FIXED {
         @Override
         protected Node create(boolean var1) {
            if (var1) {
               return null;
            } else {
               Region var2 = new Region();
               ButtonBar.setButtonData(var2, ButtonBar.ButtonData.SMALL_GAP);
               var2.setMinWidth(10.0);
               HBox.setHgrow(var2, Priority.NEVER);
               return var2;
            }
         }
      },
      DYNAMIC {
         @Override
         protected Node create(boolean var1) {
            Region var2 = new Region();
            ButtonBar.setButtonData(var2, ButtonBar.ButtonData.BIG_GAP);
            var2.setMinWidth(var1 ? 0.0 : 10.0);
            HBox.setHgrow(var2, Priority.ALWAYS);
            return var2;
         }

         @Override
         public ButtonBarSkin.Spacer replace(ButtonBarSkin.Spacer var1) {
            return (ButtonBarSkin.Spacer)(FIXED == var1 ? this : var1);
         }
      },
      NONE;

      protected Node create(boolean var1) {
         return null;
      }

      public ButtonBarSkin.Spacer replace(ButtonBarSkin.Spacer var1) {
         return var1;
      }

      public void add(Pane var1, boolean var2) {
         Node var3 = this.create(var2);
         if (var3 != null) {
            var1.getChildren().add(var3);
         }
      }
   }
}
