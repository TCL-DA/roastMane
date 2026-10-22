package javafx.scene.control.skin;

import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ChoiceBoxBehavior;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ChoiceBoxSkin<T> extends SkinBase<ChoiceBox<T>> {
   private ObservableList<T> choiceBoxItems;
   private ContextMenu popup;
   private StackPane openButton;
   private final ToggleGroup toggleGroup = new ToggleGroup();
   private SelectionModel<T> selectionModel;
   private Label label;
   private final BehaviorBase<ChoiceBox<T>> behavior;
   private final ListChangeListener<T> choiceBoxItemsListener = new ListChangeListener<T>() {
      public void onChanged(Change<? extends T> var1) {
         while (var1.next()) {
            if (var1.getRemovedSize() <= 0 && !var1.wasPermutated()) {
               for (int var5 = var1.getFrom(); var5 < var1.getTo(); var5++) {
                  Object var6 = var1.getList().get(var5);
                  ChoiceBoxSkin.this.addPopupItem(var6, var5);
               }
            } else {
               ChoiceBoxSkin.this.toggleGroup.getToggles().clear();
               ChoiceBoxSkin.this.popup.getItems().clear();
               int var2 = 0;

               for (Object var4 : var1.getList()) {
                  ChoiceBoxSkin.this.addPopupItem(var4, var2);
                  var2++;
               }
            }
         }

         ChoiceBoxSkin.this.updateSelection();
         ChoiceBoxSkin.this.getSkinnable().requestLayout();
      }
   };
   private final WeakListChangeListener<T> weakChoiceBoxItemsListener = new WeakListChangeListener(this.choiceBoxItemsListener);
   private final InvalidationListener itemsObserver;
   private InvalidationListener selectionChangeListener = var1x -> this.updateSelection();

   public ChoiceBoxSkin(ChoiceBox<T> var1) {
      super(var1);
      this.behavior = new ChoiceBoxBehavior<>(var1);
      this.initialize();
      this.itemsObserver = var1x -> this.updateChoiceBoxItems();
      var1.itemsProperty().addListener(this.itemsObserver);
      var1.requestLayout();
      this.registerChangeListener(var1.selectionModelProperty(), var1x -> this.updateSelectionModel());
      this.registerChangeListener(var1.showingProperty(), var1x -> {
         if (this.getSkinnable().isShowing()) {
            SingleSelectionModel var2 = this.getSkinnable().getSelectionModel();
            if (var2 == null) {
               return;
            }

            long var3 = (long)var2.getSelectedIndex();
            this.getSkinnable().autosize();
            double var5 = 0.0;
            if (this.popup.getSkin() != null) {
               ContextMenuContent var7 = (ContextMenuContent)this.popup.getSkin().getNode();
               if (var7 != null && var3 != -1L) {
                  var5 = -var7.getMenuYOffset((int)var3);
               }
            }

            this.popup.show(this.getSkinnable(), Side.BOTTOM, 2.0, var5);
         } else {
            this.popup.hide();
         }
      });
      this.registerChangeListener(var1.itemsProperty(), var1x -> {
         this.updateChoiceBoxItems();
         this.updatePopupItems();
         this.updateSelectionModel();
         this.updateSelection();
      });
      this.registerChangeListener(var1.converterProperty(), var1x -> {
         this.updateChoiceBoxItems();
         this.updatePopupItems();
         this.updateLabelText();
      });
      this.registerChangeListener(var1.valueProperty(), var1x -> this.updateLabelText());
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         this.getSkinnable().itemsProperty().removeListener(this.itemsObserver);
         if (this.choiceBoxItems != null) {
            this.choiceBoxItems.removeListener(this.weakChoiceBoxItemsListener);
            this.choiceBoxItems = null;
         }

         if (this.selectionModel != null) {
            this.selectionModel.selectedIndexProperty().removeListener(this.selectionChangeListener);
            this.selectionModel = null;
         }

         super.dispose();
         if (this.behavior != null) {
            this.behavior.dispose();
         }
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      double var9 = this.openButton.prefWidth(-1.0);
      this.label.resizeRelocate(var1, var3, var5, var7);
      this.openButton.resize(var9, this.openButton.prefHeight(-1.0));
      this.positionInArea(this.openButton, var1 + var5 - var9, var3, var9, var7, 0.0, HPos.CENTER, VPos.CENTER);
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.label.minWidth(-1.0) + this.openButton.minWidth(-1.0);
      double var13 = this.popup.minWidth(-1.0);
      return var9 + Math.max(var11, var13) + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.label.minHeight(-1.0);
      double var13 = this.openButton.minHeight(-1.0);
      return var3 + Math.max(var11, var13) + var7;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.label.prefWidth(-1.0) + this.openButton.prefWidth(-1.0);
      double var13 = this.popup.prefWidth(-1.0);
      if (var13 <= 0.0 && this.popup.getItems().size() > 0) {
         var13 = new Text(((MenuItem)this.popup.getItems().get(0)).getText()).prefWidth(-1.0);
      }

      return this.popup.getItems().size() == 0 ? 50.0 : var9 + Math.max(var11, var13) + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.label.prefHeight(-1.0);
      double var13 = this.openButton.prefHeight(-1.0);
      return var3 + Math.max(var11, var13) + var7;
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefHeight(var1);
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefWidth(var1);
   }

   private void initialize() {
      this.updateChoiceBoxItems();
      this.label = new Label();
      this.label.setMnemonicParsing(false);
      this.openButton = new StackPane();
      this.openButton.getStyleClass().setAll(new String[]{"open-button"});
      StackPane var1 = new StackPane();
      var1.getStyleClass().setAll(new String[]{"arrow"});
      this.openButton.getChildren().clear();
      this.openButton.getChildren().addAll(new Node[]{var1});
      this.popup = new ContextMenu();
      this.popup.showingProperty().addListener((var1x, var2, var3) -> {
         if (!var3) {
            this.getSkinnable().hide();
         }
      });
      this.popup.setId("choice-box-popup-menu");
      this.getChildren().setAll(new Node[]{this.label, this.openButton});
      this.updatePopupItems();
      this.updateSelectionModel();
      this.updateSelection();
      this.updateLabelText();
   }

   private void updateLabelText() {
      Object var1 = this.getSkinnable().getValue();
      this.label.setText(this.getDisplayText((T)var1));
   }

   private String getDisplayText(T var1) {
      if (this.getSkinnable().getConverter() != null) {
         return this.getSkinnable().getConverter().toString(var1);
      } else {
         return var1 == null ? "" : var1.toString();
      }
   }

   private void updateChoiceBoxItems() {
      if (this.choiceBoxItems != null) {
         this.choiceBoxItems.removeListener(this.weakChoiceBoxItemsListener);
      }

      this.choiceBoxItems = this.getSkinnable().getItems();
      if (this.choiceBoxItems != null) {
         this.choiceBoxItems.addListener(this.weakChoiceBoxItemsListener);
      }
   }

   String getChoiceBoxSelectedText() {
      return this.label.getText();
   }

   ContextMenu getChoiceBoxPopup() {
      return this.popup;
   }

   private void addPopupItem(T var1, int var2) {
      Object var3 = null;
      if (var1 instanceof Separator) {
         var3 = new SeparatorMenuItem();
      } else if (var1 instanceof SeparatorMenuItem) {
         var3 = (SeparatorMenuItem)var1;
      } else {
         RadioMenuItem var4 = new RadioMenuItem(this.getDisplayText((T)var1));
         var4.setId("choice-box-menu-item");
         var4.setToggleGroup(this.toggleGroup);
         var4.setOnAction(var3x -> {
            if (this.selectionModel != null) {
               int var4x = this.getSkinnable().getItems().indexOf(var1);
               this.selectionModel.select(var4x);
               var4.setSelected(true);
            }
         });
         var3 = var4;
      }

      ((MenuItem)var3).setMnemonicParsing(false);
      this.popup.getItems().add(var2, var3);
   }

   private void updatePopupItems() {
      this.toggleGroup.getToggles().clear();
      this.popup.getItems().clear();
      this.toggleGroup.selectToggle(null);

      for (int var1 = 0; var1 < this.choiceBoxItems.size(); var1++) {
         Object var2 = this.choiceBoxItems.get(var1);
         this.addPopupItem((T)var2, var1);
      }
   }

   private void updateSelectionModel() {
      if (this.selectionModel != null) {
         this.selectionModel.selectedIndexProperty().removeListener(this.selectionChangeListener);
      }

      this.selectionModel = this.getSkinnable().getSelectionModel();
      if (this.selectionModel != null) {
         this.selectionModel.selectedIndexProperty().addListener(this.selectionChangeListener);
      }
   }

   private void updateSelection() {
      if (this.selectionModel != null && !this.selectionModel.isEmpty()) {
         int var1 = this.selectionModel.getSelectedIndex();
         if (var1 == -1 || var1 > this.popup.getItems().size()) {
            return;
         }

         if (var1 < this.popup.getItems().size()) {
            MenuItem var2 = (MenuItem)this.popup.getItems().get(var1);
            if (var2 instanceof RadioMenuItem) {
               ((RadioMenuItem)var2).setSelected(true);
            } else {
               this.toggleGroup.selectToggle(null);
            }
         }
      } else {
         this.toggleGroup.selectToggle(null);
      }
   }
}
