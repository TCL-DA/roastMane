package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public abstract class ComboBoxBaseSkin<T> extends SkinBase<ComboBoxBase<T>> {
   private Node displayNode;
   StackPane arrowButton;
   Region arrow;
   private ComboBoxMode mode = ComboBoxMode.COMBOBOX;
   private final EventHandler<MouseEvent> mouseEnteredEventHandler = var1x -> this.getBehavior().mouseEntered(var1x);
   private final EventHandler<MouseEvent> mousePressedEventHandler = var1x -> {
      this.getBehavior().mousePressed(var1x);
      var1x.consume();
   };
   private final EventHandler<MouseEvent> mouseReleasedEventHandler = var1x -> {
      this.getBehavior().mouseReleased(var1x);
      var1x.consume();
   };
   private final EventHandler<MouseEvent> mouseExitedEventHandler = var1x -> this.getBehavior().mouseExited(var1x);

   final ComboBoxMode getMode() {
      return this.mode;
   }

   final void setMode(ComboBoxMode var1) {
      this.mode = var1;
   }

   public ComboBoxBaseSkin(ComboBoxBase<T> var1) {
      super(var1);
      this.getChildren().clear();
      this.arrow = new Region();
      this.arrow.setFocusTraversable(false);
      this.arrow.getStyleClass().setAll(new String[]{"arrow"});
      this.arrow.setId("arrow");
      this.arrow.setMaxWidth(Double.NEGATIVE_INFINITY);
      this.arrow.setMaxHeight(Double.NEGATIVE_INFINITY);
      this.arrow.setMouseTransparent(true);
      this.arrowButton = new StackPane();
      this.arrowButton.setFocusTraversable(false);
      this.arrowButton.setId("arrow-button");
      this.arrowButton.getStyleClass().setAll(new String[]{"arrow-button"});
      this.arrowButton.getChildren().add(this.arrow);
      this.getChildren().add(this.arrowButton);
      this.getSkinnable().focusedProperty().addListener((var1x, var2, var3) -> {
         if (!var3) {
            this.focusLost();
         }
      });
      this.updateArrowButtonListeners();
      this.registerChangeListener(var1.editableProperty(), var1x -> {
         this.updateArrowButtonListeners();
         this.updateDisplayArea();
      });
      this.registerChangeListener(var1.showingProperty(), var1x -> {
         if (this.getSkinnable().isShowing()) {
            this.show();
         } else {
            this.hide();
         }
      });
      this.registerChangeListener(var1.valueProperty(), var1x -> this.updateDisplayArea());
   }

   public abstract Node getDisplayNode();

   public abstract void show();

   public abstract void hide();

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      if (this.displayNode == null) {
         this.updateDisplayArea();
      }

      double var9 = this.snapSizeX(this.arrow.prefWidth(-1.0));
      double var11 = this.isButton() ? 0.0 : this.arrowButton.snappedLeftInset() + var9 + this.arrowButton.snappedRightInset();
      if (this.displayNode != null) {
         this.displayNode.resizeRelocate(var1, var3, var5 - var11, var7);
      }

      this.arrowButton.setVisible(!this.isButton());
      if (!this.isButton()) {
         this.arrowButton.resize(var11, var7);
         this.positionInArea(this.arrowButton, var1 + var5 - var11, var3, var11, var7, 0.0, HPos.CENTER, VPos.CENTER);
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      if (this.displayNode == null) {
         this.updateDisplayArea();
      }

      double var11 = this.snapSizeX(this.arrow.prefWidth(-1.0));
      double var13 = this.isButton() ? 0.0 : this.arrowButton.snappedLeftInset() + var11 + this.arrowButton.snappedRightInset();
      double var15 = this.displayNode == null ? 0.0 : this.displayNode.prefWidth(var1);
      double var17 = var15 + var13;
      return var9 + var17 + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      if (this.displayNode == null) {
         this.updateDisplayArea();
      }

      double var11;
      if (this.displayNode == null) {
         double var13 = this.isButton() ? 0.0 : this.arrowButton.snappedTopInset() + this.arrow.prefHeight(-1.0) + this.arrowButton.snappedBottomInset();
         var11 = Math.max(21.0, var13);
      } else {
         var11 = this.displayNode.prefHeight(var1);
      }

      return var3 + var11 + var7;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefWidth(var1);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefHeight(var1);
   }

   @Override
   protected double computeBaselineOffset(double var1, double var3, double var5, double var7) {
      if (this.displayNode == null) {
         this.updateDisplayArea();
      }

      return this.displayNode != null
         ? this.displayNode.getLayoutBounds().getMinY() + this.displayNode.getLayoutY() + this.displayNode.getBaselineOffset()
         : super.computeBaselineOffset(var1, var3, var5, var7);
   }

   ComboBoxBaseBehavior getBehavior() {
      return null;
   }

   void focusLost() {
      this.getSkinnable().hide();
   }

   private boolean isButton() {
      return this.getMode() == ComboBoxMode.BUTTON;
   }

   private void updateArrowButtonListeners() {
      if (this.getSkinnable().isEditable()) {
         this.arrowButton.addEventHandler(MouseEvent.MOUSE_ENTERED, this.mouseEnteredEventHandler);
         this.arrowButton.addEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
         this.arrowButton.addEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
         this.arrowButton.addEventHandler(MouseEvent.MOUSE_EXITED, this.mouseExitedEventHandler);
      } else {
         this.arrowButton.removeEventHandler(MouseEvent.MOUSE_ENTERED, this.mouseEnteredEventHandler);
         this.arrowButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.mousePressedEventHandler);
         this.arrowButton.removeEventHandler(MouseEvent.MOUSE_RELEASED, this.mouseReleasedEventHandler);
         this.arrowButton.removeEventHandler(MouseEvent.MOUSE_EXITED, this.mouseExitedEventHandler);
      }
   }

   void updateDisplayArea() {
      ObservableList var1 = this.getChildren();
      Node var2 = this.displayNode;
      this.displayNode = this.getDisplayNode();
      if (var2 != null && var2 != this.displayNode) {
         var1.remove(var2);
      }

      if (this.displayNode != null && !var1.contains(this.displayNode)) {
         var1.add(this.displayNode);
         this.displayNode.applyCss();
      }
   }
}
