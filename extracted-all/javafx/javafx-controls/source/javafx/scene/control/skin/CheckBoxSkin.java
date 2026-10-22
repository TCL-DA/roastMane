package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;

public class CheckBoxSkin extends LabeledSkinBase<CheckBox> {
   private final StackPane box = new StackPane();
   private StackPane innerbox;
   private final BehaviorBase<CheckBox> behavior;

   public CheckBoxSkin(CheckBox var1) {
      super(var1);
      this.behavior = new ButtonBehavior<>(var1);
      this.box.getStyleClass().setAll(new String[]{"box"});
      this.innerbox = new StackPane();
      this.innerbox.getStyleClass().setAll(new String[]{"mark"});
      this.innerbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
      this.box.getChildren().add(this.innerbox);
      this.updateChildren();
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected void updateChildren() {
      super.updateChildren();
      if (this.box != null) {
         this.getChildren().add(this.box);
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      return super.computeMinWidth(var1, var3, var5, var7, var9) + this.snapSizeX(this.box.minWidth(-1.0));
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      return Math.max(super.computeMinHeight(var1 - this.box.minWidth(-1.0), var3, var5, var7, var9), var3 + this.box.minHeight(-1.0) + var7);
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return super.computePrefWidth(var1, var3, var5, var7, var9) + this.snapSizeX(this.box.prefWidth(-1.0));
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return Math.max(super.computePrefHeight(var1 - this.box.prefWidth(-1.0), var3, var5, var7, var9), var3 + this.box.prefHeight(-1.0) + var7);
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      CheckBox var9 = this.getSkinnable();
      double var10 = this.snapSizeX(this.box.prefWidth(-1.0));
      double var12 = this.snapSizeY(this.box.prefHeight(-1.0));
      double var14 = Math.max(var9.prefWidth(-1.0), var9.minWidth(-1.0));
      double var16 = Math.min(var14 - var10, var5 - this.snapSizeX(var10));
      double var18 = Math.min(var9.prefHeight(var16), var7);
      double var20 = Math.max(var12, var18);
      double var22 = Utils.computeXOffset(var5, var16 + var10, var9.getAlignment().getHpos()) + var1;
      double var24 = Utils.computeYOffset(var7, var20, var9.getAlignment().getVpos()) + var3;
      this.layoutLabelInArea(var22 + var10, var24, var16, var20, var9.getAlignment());
      this.box.resize(var10, var12);
      this.positionInArea(this.box, var22, var24, var10, var20, 0.0, var9.getAlignment().getHpos(), var9.getAlignment().getVpos());
   }
}
