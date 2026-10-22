package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.TreeTableRowSkin;

public class TreeTableRow<T> extends IndexedCell<T> {
   private final ListChangeListener<Integer> selectedListener = var1 -> this.updateSelection();
   private final InvalidationListener focusedListener = var1 -> this.updateFocus();
   private final InvalidationListener editingListener = var1 -> this.updateEditing();
   private final InvalidationListener leafListener = new InvalidationListener() {
      public void invalidated(Observable var1) {
         TreeItem var2 = TreeTableRow.this.getTreeItem();
         if (var2 != null) {
            TreeTableRow.this.requestLayout();
         }
      }
   };
   private boolean oldExpanded;
   private final InvalidationListener treeItemExpandedInvalidationListener = var1 -> {
      boolean var2 = ((BooleanProperty)var1).get();
      this.pseudoClassStateChanged(EXPANDED_PSEUDOCLASS_STATE, var2);
      this.pseudoClassStateChanged(COLLAPSED_PSEUDOCLASS_STATE, !var2);
      if (var2 != this.oldExpanded) {
         this.notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
      }

      this.oldExpanded = var2;
   };
   private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener(this.selectedListener);
   private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(this.focusedListener);
   private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(this.editingListener);
   private final WeakInvalidationListener weakLeafListener = new WeakInvalidationListener(this.leafListener);
   private final WeakInvalidationListener weakTreeItemExpandedInvalidationListener = new WeakInvalidationListener(this.treeItemExpandedInvalidationListener);
   private ReadOnlyObjectWrapper<TreeItem<T>> treeItem = new ReadOnlyObjectWrapper<TreeItem<T>>(this, "treeItem") {
      TreeItem<T> oldValue = null;

      protected void invalidated() {
         if (this.oldValue != null) {
            this.oldValue.expandedProperty().removeListener(TreeTableRow.this.weakTreeItemExpandedInvalidationListener);
         }

         this.oldValue = (TreeItem<T>)this.get();
         if (this.oldValue != null) {
            TreeTableRow.this.oldExpanded = this.oldValue.isExpanded();
            this.oldValue.expandedProperty().addListener(TreeTableRow.this.weakTreeItemExpandedInvalidationListener);
            TreeTableRow.this.weakTreeItemExpandedInvalidationListener.invalidated(this.oldValue.expandedProperty());
         }
      }
   };
   private ObjectProperty<Node> disclosureNode = new SimpleObjectProperty(this, "disclosureNode");
   private ReadOnlyObjectWrapper<TreeTableView<T>> treeTableView = new ReadOnlyObjectWrapper<TreeTableView<T>>(this, "treeTableView") {
      private WeakReference<TreeTableView<T>> weakTreeTableViewRef;

      protected void invalidated() {
         if (this.weakTreeTableViewRef != null) {
            TreeTableView var3 = this.weakTreeTableViewRef.get();
            if (var3 != null) {
               TreeTableView.TreeTableViewSelectionModel var1 = var3.getSelectionModel();
               if (var1 != null) {
                  var1.getSelectedIndices().removeListener(TreeTableRow.this.weakSelectedListener);
               }

               TreeTableView.TreeTableViewFocusModel var2 = var3.getFocusModel();
               if (var2 != null) {
                  var2.focusedIndexProperty().removeListener(TreeTableRow.this.weakFocusedListener);
               }

               var3.editingCellProperty().removeListener(TreeTableRow.this.weakEditingListener);
            }

            this.weakTreeTableViewRef = null;
         }

         if (this.get() != null) {
            TreeTableView.TreeTableViewSelectionModel var4 = ((TreeTableView)this.get()).getSelectionModel();
            if (var4 != null) {
               var4.getSelectedIndices().addListener(TreeTableRow.this.weakSelectedListener);
            }

            TreeTableView.TreeTableViewFocusModel var5 = ((TreeTableView)this.get()).getFocusModel();
            if (var5 != null) {
               var5.focusedIndexProperty().addListener(TreeTableRow.this.weakFocusedListener);
            }

            ((TreeTableView)this.get()).editingCellProperty().addListener(TreeTableRow.this.weakEditingListener);
            this.weakTreeTableViewRef = new WeakReference<>((TreeTableView<T>)this.get());
         }

         TreeTableRow.this.updateItem();
         TreeTableRow.this.requestLayout();
      }
   };
   private int index = -1;
   private boolean isFirstRun = true;
   private static final String DEFAULT_STYLE_CLASS = "tree-table-row-cell";
   private static final PseudoClass EXPANDED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("expanded");
   private static final PseudoClass COLLAPSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("collapsed");

   public TreeTableRow() {
      this.getStyleClass().addAll(new String[]{"tree-table-row-cell"});
      this.setAccessibleRole(AccessibleRole.TREE_TABLE_ROW);
   }

   private void setTreeItem(TreeItem<T> var1) {
      this.treeItem.set(var1);
   }

   public final TreeItem<T> getTreeItem() {
      return (TreeItem<T>)this.treeItem.get();
   }

   public final ReadOnlyObjectProperty<TreeItem<T>> treeItemProperty() {
      return this.treeItem.getReadOnlyProperty();
   }

   public final void setDisclosureNode(Node var1) {
      this.disclosureNodeProperty().set(var1);
   }

   public final Node getDisclosureNode() {
      return (Node)this.disclosureNode.get();
   }

   public final ObjectProperty<Node> disclosureNodeProperty() {
      return this.disclosureNode;
   }

   private void setTreeTableView(TreeTableView<T> var1) {
      this.treeTableView.set(var1);
   }

   public final TreeTableView<T> getTreeTableView() {
      return (TreeTableView<T>)this.treeTableView.get();
   }

   public final ReadOnlyObjectProperty<TreeTableView<T>> treeTableViewProperty() {
      return this.treeTableView.getReadOnlyProperty();
   }

   @Override
   void indexChanged(int var1, int var2) {
      this.index = this.getIndex();
      this.updateItem();
      this.updateSelection();
      this.updateFocus();
   }

   @Override
   public void startEdit() {
      TreeTableView var1 = this.getTreeTableView();
      if (this.isEditable() && (var1 == null || var1.isEditable())) {
         super.startEdit();
         if (var1 != null) {
            var1.fireEvent(new TreeTableView.EditEvent<>(var1, TreeTableView.editStartEvent(), this.getTreeItem(), this.getItem(), null));
            var1.requestFocus();
         }
      }
   }

   @Override
   public void commitEdit(T var1) {
      if (this.isEditing()) {
         TreeItem var2 = this.getTreeItem();
         TreeTableView var3 = this.getTreeTableView();
         if (var3 != null) {
            var3.fireEvent(new TreeTableView.EditEvent<>(var3, TreeTableView.editCommitEvent(), var2, this.getItem(), (T)var1));
         }

         if (var2 != null) {
            var2.setValue(var1);
            this.updateTreeItem(var2);
            this.updateItem((T)var1, false);
         }

         super.commitEdit((T)var1);
         if (var3 != null) {
            var3.edit(-1, null);
            var3.requestFocus();
         }
      }
   }

   @Override
   public void cancelEdit() {
      if (this.isEditing()) {
         TreeTableView var1 = this.getTreeTableView();
         if (var1 != null) {
            var1.fireEvent(new TreeTableView.EditEvent<>(var1, TreeTableView.editCancelEvent(), this.getTreeItem(), this.getItem(), null));
         }

         super.cancelEdit();
         if (var1 != null) {
            var1.edit(-1, null);
            var1.requestFocus();
         }
      }
   }

   private void updateItem() {
      TreeTableView var1 = this.getTreeTableView();
      if (var1 != null) {
         boolean var2 = this.index >= 0 && this.index < var1.getExpandedItemCount();
         TreeItem var3 = this.getTreeItem();
         boolean var4 = this.isEmpty();
         if (var2) {
            TreeItem var5 = var1.getTreeItem(this.index);
            Object var6 = var5 == null ? null : var5.getValue();
            this.updateTreeItem(var5);
            this.updateItem((T)var6, false);
         } else if (!var4 && var3 != null || this.isFirstRun) {
            this.updateTreeItem(null);
            this.updateItem(null, true);
            this.isFirstRun = false;
         }
      }
   }

   private void updateSelection() {
      if (!this.isEmpty()) {
         if (this.index != -1 && this.getTreeTableView() != null) {
            if (this.getTreeTableView().getSelectionModel() != null) {
               boolean var1 = this.getTreeTableView().getSelectionModel().isSelected(this.index);
               if (this.isSelected() != var1) {
                  this.updateSelected(var1);
               }
            }
         }
      }
   }

   private void updateFocus() {
      if (this.getIndex() != -1 && this.getTreeTableView() != null) {
         if (this.getTreeTableView().getFocusModel() != null) {
            this.setFocused(this.getTreeTableView().getFocusModel().isFocused(this.getIndex()));
         }
      }
   }

   private void updateEditing() {
      if (this.getIndex() != -1 && this.getTreeTableView() != null && this.getTreeItem() != null) {
         TreeTablePosition var1 = this.getTreeTableView().getEditingCell();
         if (var1 == null || var1.getTableColumn() == null) {
            TreeItem var2 = var1 == null ? null : var1.getTreeItem();
            if (!this.isEditing() && this.getTreeItem().equals(var2)) {
               this.startEdit();
            } else if (this.isEditing() && !this.getTreeItem().equals(var2)) {
               this.cancelEdit();
            }
         }
      }
   }

   public final void updateTreeTableView(TreeTableView<T> var1) {
      this.setTreeTableView(var1);
   }

   public final void updateTreeItem(TreeItem<T> var1) {
      TreeItem var2 = this.getTreeItem();
      if (var2 != null) {
         var2.leafProperty().removeListener(this.weakLeafListener);
      }

      this.setTreeItem(var1);
      if (var1 != null) {
         var1.leafProperty().addListener(this.weakLeafListener);
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TreeTableRowSkin<>(this);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      TreeItem var3 = this.getTreeItem();
      TreeTableView var4 = this.getTreeTableView();
      switch (var1) {
         case TREE_ITEM_PARENT:
            if (var3 == null) {
               return null;
            } else {
               TreeItem var8 = var3.getParent();
               if (var8 == null) {
                  return null;
               }

               int var9 = var4.getRow(var8);
               return var4.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, var9);
            }
         case TREE_ITEM_COUNT:
            if (var3 == null) {
               return 0;
            } else {
               if (!var3.isExpanded()) {
                  return 0;
               }

               return var3.getChildren().size();
            }
         case TREE_ITEM_AT_INDEX:
            if (var3 == null) {
               return null;
            } else if (!var3.isExpanded()) {
               return null;
            } else {
               int var5 = (Integer)var2[0];
               if (var5 >= var3.getChildren().size()) {
                  return null;
               } else {
                  TreeItem var6 = (TreeItem)var3.getChildren().get(var5);
                  if (var6 == null) {
                     return null;
                  }

                  int var7 = var4.getRow(var6);
                  return var4.queryAccessibleAttribute(AccessibleAttribute.ROW_AT_INDEX, var7);
               }
            }
         case LEAF:
            return var3 == null ? true : var3.isLeaf();
         case EXPANDED:
            return var3 == null ? false : var3.isExpanded();
         case INDEX:
            return this.getIndex();
         case DISCLOSURE_LEVEL:
            return var4 == null ? 0 : var4.getTreeItemLevel(var3);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case EXPAND:
            TreeItem var4 = this.getTreeItem();
            if (var4 != null) {
               var4.setExpanded(true);
            }
            break;
         case COLLAPSE:
            TreeItem var3 = this.getTreeItem();
            if (var3 != null) {
               var3.setExpanded(false);
            }
            break;
         default:
            super.executeAccessibleAction(var1, new Object[0]);
      }
   }
}
