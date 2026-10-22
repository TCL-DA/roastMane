package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import com.sun.javafx.scene.control.LabeledImpl;
import com.sun.javafx.scene.control.behavior.MenuButtonBehaviorBase;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class MenuButtonSkinBase<C extends MenuButton> extends SkinBase<C> {
   final LabeledImpl label;
   final StackPane arrow;
   final StackPane arrowButton;
   ContextMenu popup;
   boolean behaveLikeButton = false;
   private ListChangeListener<MenuItem> itemsChangedListener;
   private final ChangeListener<? super Scene> sceneChangeListener;
   boolean requestFocusOnFirstMenuItem = false;

   public MenuButtonSkinBase(C var1) {
      super((C)var1);
      if (var1.getOnMousePressed() == null) {
         var1.addEventHandler(MouseEvent.MOUSE_PRESSED, var1x -> {
            MenuButtonBehaviorBase var2x = this.getBehavior();
            if (var2x != null) {
               var2x.mousePressed(var1x, this.behaveLikeButton);
            }
         });
      }

      if (var1.getOnMouseReleased() == null) {
         var1.addEventHandler(MouseEvent.MOUSE_RELEASED, var1x -> {
            MenuButtonBehaviorBase var2x = this.getBehavior();
            if (var2x != null) {
               var2x.mouseReleased(var1x, this.behaveLikeButton);
            }
         });
      }

      this.label = new MenuButtonSkinBase.MenuLabeledImpl(this.getSkinnable());
      this.label.setMnemonicParsing(var1.isMnemonicParsing());
      this.label.setLabelFor(var1);
      this.arrow = new StackPane();
      this.arrow.getStyleClass().setAll(new String[]{"arrow"});
      this.arrow.setMaxWidth(Double.NEGATIVE_INFINITY);
      this.arrow.setMaxHeight(Double.NEGATIVE_INFINITY);
      this.arrowButton = new StackPane();
      this.arrowButton.getStyleClass().setAll(new String[]{"arrow-button"});
      this.arrowButton.getChildren().add(this.arrow);
      this.popup = new ContextMenu();
      this.popup.getItems().clear();
      this.popup.getItems().addAll(this.getSkinnable().getItems());
      this.getChildren().clear();
      this.getChildren().addAll(new Node[]{this.label, this.arrowButton});
      this.getSkinnable().requestLayout();
      this.itemsChangedListener = var1x -> {
         while (var1x.next()) {
            this.popup.getItems().removeAll(var1x.getRemoved());
            this.popup.getItems().addAll(var1x.getFrom(), var1x.getAddedSubList());
         }
      };
      var1.getItems().addListener(this.itemsChangedListener);
      if (this.getSkinnable().getScene() != null) {
         ControlAcceleratorSupport.addAcceleratorsIntoScene(this.getSkinnable().getItems(), this.getSkinnable());
      }

      this.sceneChangeListener = (var1x, var2x, var3) -> {
         if (var2x != null) {
            ControlAcceleratorSupport.removeAcceleratorsFromScene(this.getSkinnable().getItems(), var2x);
         }

         if (this.getSkinnable() != null && this.getSkinnable().getScene() != null) {
            ControlAcceleratorSupport.addAcceleratorsIntoScene(this.getSkinnable().getItems(), this.getSkinnable());
         }
      };
      var1.sceneProperty().addListener(this.sceneChangeListener);
      this.registerChangeListener(var1.showingProperty(), var1x -> {
         if (this.getSkinnable().isShowing()) {
            this.show();
         } else {
            this.hide();
         }
      });
      this.registerChangeListener(var1.focusedProperty(), var1x -> {
         if (!this.getSkinnable().isFocused() && this.getSkinnable().isShowing()) {
            this.hide();
         }

         if (!this.getSkinnable().isFocused() && this.popup.isShowing()) {
            this.hide();
         }
      });
      this.registerChangeListener(var1.mnemonicParsingProperty(), var1x -> {
         this.label.setMnemonicParsing(this.getSkinnable().isMnemonicParsing());
         this.getSkinnable().requestLayout();
      });
      ArrayList var2 = new ArrayList();
      this.registerChangeListener(this.popup.showingProperty(), var2x -> {
         if (!this.popup.isShowing() && this.getSkinnable().isShowing()) {
            this.getSkinnable().hide();
         }

         if (this.popup.isShowing()) {
            boolean var3 = NodeHelper.isShowMnemonics(this.getSkinnable());
            Utils.addMnemonics(this.popup, this.getSkinnable().getScene(), var3, var2);
         } else {
            Scene var5 = this.getSkinnable().getScene();
            ArrayList var4 = new ArrayList(var2);
            var2.clear();
            Platform.runLater(() -> var4.forEach(var5::removeMnemonic));
         }
      });
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         if (this.getSkinnable().getScene() != null) {
            ControlAcceleratorSupport.removeAcceleratorsFromScene(this.getSkinnable().getItems(), this.getSkinnable().getScene());
         }

         this.getSkinnable().sceneProperty().removeListener(this.sceneChangeListener);
         this.getSkinnable().getItems().removeListener(this.itemsChangedListener);
         super.dispose();
         if (this.popup != null) {
            if (this.popup.getSkin() != null && this.popup.getSkin().getNode() != null) {
               ContextMenuContent var1 = (ContextMenuContent)this.popup.getSkin().getNode();
               var1.dispose();
            }

            this.popup.setSkin(null);
            this.popup = null;
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      return var9 + this.label.minWidth(var1) + this.snapSizeX(this.arrowButton.minWidth(var1)) + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      return var3 + Math.max(this.label.minHeight(var1), this.snapSizeY(this.arrowButton.minHeight(-1.0))) + var7;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return var9 + this.label.prefWidth(var1) + this.snapSizeX(this.arrowButton.prefWidth(var1)) + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return var3 + Math.max(this.label.prefHeight(var1), this.snapSizeY(this.arrowButton.prefHeight(-1.0))) + var7;
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
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      double var9 = this.snapSizeX(this.arrowButton.prefWidth(-1.0));
      this.label.resizeRelocate(var1, var3, var5 - var9, var7);
      this.arrowButton.resizeRelocate(var1 + (var5 - var9), var3, var9, var7);
   }

   MenuButtonBehaviorBase<C> getBehavior() {
      return null;
   }

   private void show() {
      if (!this.popup.isShowing()) {
         this.popup.show(this.getSkinnable(), this.getSkinnable().getPopupSide(), 0.0, 0.0);
      }
   }

   private void hide() {
      if (this.popup.isShowing()) {
         this.popup.hide();
      }
   }

   void requestFocusOnFirstMenuItem() {
      this.requestFocusOnFirstMenuItem = true;
   }

   void putFocusOnFirstMenuItem() {
      Skin var1 = this.popup.getSkin();
      if (var1 instanceof ContextMenuSkin) {
         Node var2 = var1.getNode();
         if (var2 instanceof ContextMenuContent) {
            ((ContextMenuContent)var2).requestFocusOnIndex(0);
         }
      }
   }

   private static class MenuLabeledImpl extends LabeledImpl {
      MenuButton button;

      public MenuLabeledImpl(MenuButton var1) {
         super(var1);
         this.button = var1;
         this.addEventHandler(ActionEvent.ACTION, var1x -> {
            this.button.fireEvent(new ActionEvent());
            var1x.consume();
         });
      }
   }
}
