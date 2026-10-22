package org.controlsfx.control;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class CheckTreeView<T> extends TreeView<T> {
   private ObjectProperty<CheckModel<TreeItem<T>>> checkModel = new SimpleObjectProperty(this, "checkModel");

   public CheckTreeView() {
      this(null);
   }

   public CheckTreeView(CheckBoxTreeItem<T> root) {
      super(root);
      this.rootProperty().addListener(o -> this.updateCheckModel());
      this.updateCheckModel();
      this.setCellFactory(CheckBoxTreeCell.forTreeView());
   }

   protected void updateCheckModel() {
      if (this.getRoot() != null) {
         this.setCheckModel(new CheckTreeView.CheckTreeViewCheckModel<>(this));
      }
   }

   public BooleanProperty getItemBooleanProperty(int index) {
      CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>)this.getTreeItem(index);
      return treeItem.selectedProperty();
   }

   public final void setCheckModel(CheckModel<TreeItem<T>> value) {
      this.checkModelProperty().set(value);
   }

   public final CheckModel<TreeItem<T>> getCheckModel() {
      return this.checkModel == null ? null : (CheckModel)this.checkModel.get();
   }

   public final ObjectProperty<CheckModel<TreeItem<T>>> checkModelProperty() {
      return this.checkModel;
   }

   private static class CheckTreeViewCheckModel<T> implements CheckModel<TreeItem<T>> {
      private final CheckTreeView<T> treeView;
      private final TreeItem<T> root;
      private ObservableList<TreeItem<T>> checkedItems = FXCollections.observableArrayList();

      CheckTreeViewCheckModel(CheckTreeView<T> treeView) {
         this.treeView = treeView;
         this.root = treeView.getRoot();
         this.root.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), e -> {
            CheckBoxTreeItem<T> treeItemx = e.getTreeItem();
            if (treeItemx.isSelected()) {
               this.check((TreeItem<T>)treeItemx);
            } else {
               this.clearCheck((TreeItem<T>)treeItemx);
            }
         });
         this.clearChecks();

         for (int i = 0; i < treeView.getExpandedItemCount(); i++) {
            CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>)treeView.getTreeItem(i);
            if (treeItem.isSelected() && !treeItem.isIndeterminate()) {
               this.check((TreeItem<T>)treeItem);
            }
         }
      }

      @Override
      public int getItemCount() {
         return this.treeView.getExpandedItemCount();
      }

      @Override
      public ObservableList<TreeItem<T>> getCheckedItems() {
         return this.checkedItems;
      }

      @Override
      public void checkAll() {
         this.iterateOverTree(this::check);
      }

      public void clearCheck(TreeItem<T> item) {
         if (item instanceof CheckBoxTreeItem) {
            ((CheckBoxTreeItem)item).setSelected(false);
         }

         this.checkedItems.remove(item);
      }

      @Override
      public void clearChecks() {
         for (TreeItem<T> item : new ArrayList(this.checkedItems)) {
            this.clearCheck(item);
         }
      }

      @Override
      public boolean isEmpty() {
         return this.checkedItems.isEmpty();
      }

      public boolean isChecked(TreeItem<T> item) {
         return this.checkedItems.contains(item);
      }

      public void check(TreeItem<T> item) {
         if (item instanceof CheckBoxTreeItem) {
            ((CheckBoxTreeItem)item).setSelected(true);
         }

         if (!this.checkedItems.contains(item)) {
            this.checkedItems.add(item);
         }
      }

      public void toggleCheckState(TreeItem<T> item) {
         if (this.isChecked(item)) {
            this.clearCheck(item);
         } else {
            this.check(item);
         }
      }

      private void iterateOverTree(Consumer<TreeItem<T>> consumer) {
         this.processNode(consumer, this.root);
      }

      private void processNode(Consumer<TreeItem<T>> consumer, TreeItem<T> node) {
         if (node != null) {
            consumer.accept(node);
            this.processChildren(consumer, node.getChildren());
         }
      }

      private void processChildren(Consumer<TreeItem<T>> consumer, List<TreeItem<T>> children) {
         if (children != null) {
            for (TreeItem<T> child : children) {
               this.processNode(consumer, child);
            }
         }
      }
   }
}
