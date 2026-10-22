package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.TreeTableCellSkin;

public class TreeTableCell<S, T> extends IndexedCell<T> {
   boolean lockItemOnEdit = false;
   private boolean itemDirty = false;
   private ListChangeListener<TreeTablePosition<S, ?>> selectedListener = var1 -> {
      while (var1.next()) {
         if (var1.wasAdded() || var1.wasRemoved()) {
            this.updateSelection();
         }
      }
   };
   private final InvalidationListener focusedListener = var1 -> this.updateFocus();
   private final InvalidationListener tableRowUpdateObserver = var1 -> {
      this.itemDirty = true;
      this.requestLayout();
   };
   private final InvalidationListener editingListener = var1 -> this.updateEditing();
   private ListChangeListener<TreeTableColumn<S, ?>> visibleLeafColumnsListener = var1 -> this.updateColumnIndex();
   private ListChangeListener<String> columnStyleClassListener = var1 -> {
      while (var1.next()) {
         if (var1.wasRemoved()) {
            this.getStyleClass().removeAll(var1.getRemoved());
         }

         if (var1.wasAdded()) {
            this.getStyleClass().addAll(var1.getAddedSubList());
         }
      }
   };
   private final InvalidationListener rootPropertyListener = var1 -> this.updateItem(-1);
   private final InvalidationListener columnStyleListener = var1 -> {
      if (this.getTableColumn() != null) {
         this.possiblySetStyle(this.getTableColumn().getStyle());
      }
   };
   private final InvalidationListener columnIdListener = var1 -> {
      if (this.getTableColumn() != null) {
         this.possiblySetId(this.getTableColumn().getId());
      }
   };
   private final WeakListChangeListener<TreeTablePosition<S, ?>> weakSelectedListener = new WeakListChangeListener(this.selectedListener);
   private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(this.focusedListener);
   private final WeakInvalidationListener weaktableRowUpdateObserver = new WeakInvalidationListener(this.tableRowUpdateObserver);
   private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(this.editingListener);
   private final WeakListChangeListener<TreeTableColumn<S, ?>> weakVisibleLeafColumnsListener = new WeakListChangeListener(this.visibleLeafColumnsListener);
   private final WeakListChangeListener<String> weakColumnStyleClassListener = new WeakListChangeListener(this.columnStyleClassListener);
   private final WeakInvalidationListener weakColumnStyleListener = new WeakInvalidationListener(this.columnStyleListener);
   private final WeakInvalidationListener weakColumnIdListener = new WeakInvalidationListener(this.columnIdListener);
   private final WeakInvalidationListener weakRootPropertyListener = new WeakInvalidationListener(this.rootPropertyListener);
   private ReadOnlyObjectWrapper<TreeTableColumn<S, T>> tableColumn = new ReadOnlyObjectWrapper<TreeTableColumn<S, T>>(this, "tableColumn") {
      protected void invalidated() {
         TreeTableCell.this.updateColumnIndex();
      }
   };
   private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableView;
   private ReadOnlyObjectWrapper<TreeTableRow<S>> tableRow = new ReadOnlyObjectWrapper(this, "tableRow");
   private TreeTablePosition<S, T> editingCellAtStartEdit = null;
   private boolean isLastVisibleColumn = false;
   private int columnIndex = -1;
   private boolean updateEditingIndex = true;
   private ObservableValue<T> currentObservableValue = null;
   private boolean isFirstRun = true;
   private WeakReference<S> oldRowItemRef;
   private static final String DEFAULT_STYLE_CLASS = "tree-table-cell";
   private static final PseudoClass PSEUDO_CLASS_LAST_VISIBLE = PseudoClass.getPseudoClass("last-visible");

   public TreeTableCell() {
      this.getStyleClass().addAll(new String[]{"tree-table-cell"});
      this.setAccessibleRole(AccessibleRole.TREE_TABLE_CELL);
      this.updateColumnIndex();
   }

   public final ReadOnlyObjectProperty<TreeTableColumn<S, T>> tableColumnProperty() {
      return this.tableColumn.getReadOnlyProperty();
   }

   private void setTableColumn(TreeTableColumn<S, T> var1) {
      this.tableColumn.set(var1);
   }

   public final TreeTableColumn<S, T> getTableColumn() {
      return (TreeTableColumn<S, T>)this.tableColumn.get();
   }

   private void setTreeTableView(TreeTableView<S> var1) {
      this.treeTableViewPropertyImpl().set(var1);
   }

   public final TreeTableView<S> getTreeTableView() {
      return this.treeTableView == null ? null : (TreeTableView)this.treeTableView.get();
   }

   public final ReadOnlyObjectProperty<TreeTableView<S>> treeTableViewProperty() {
      return this.treeTableViewPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableViewPropertyImpl() {
      if (this.treeTableView == null) {
         this.treeTableView = new ReadOnlyObjectWrapper<TreeTableView<S>>(this, "treeTableView") {
            private WeakReference<TreeTableView<S>> weakTableViewRef;

            protected void invalidated() {
               if (this.weakTableViewRef != null) {
                  TreeTableView var3 = this.weakTableViewRef.get();
                  if (var3 != null) {
                     TreeTableView.TreeTableViewSelectionModel var1 = var3.getSelectionModel();
                     if (var1 != null) {
                        var1.getSelectedCells().removeListener(TreeTableCell.this.weakSelectedListener);
                     }

                     TreeTableView.TreeTableViewFocusModel var2 = var3.getFocusModel();
                     if (var2 != null) {
                        var2.focusedCellProperty().removeListener(TreeTableCell.this.weakFocusedListener);
                     }

                     var3.editingCellProperty().removeListener(TreeTableCell.this.weakEditingListener);
                     var3.getVisibleLeafColumns().removeListener(TreeTableCell.this.weakVisibleLeafColumnsListener);
                     var3.rootProperty().removeListener(TreeTableCell.this.weakRootPropertyListener);
                  }
               }

               TreeTableView var6 = (TreeTableView)this.get();
               if (var6 != null) {
                  TreeTableView.TreeTableViewSelectionModel var4 = var6.getSelectionModel();
                  if (var4 != null) {
                     var4.getSelectedCells().addListener(TreeTableCell.this.weakSelectedListener);
                  }

                  TreeTableView.TreeTableViewFocusModel var5 = var6.getFocusModel();
                  if (var5 != null) {
                     var5.focusedCellProperty().addListener(TreeTableCell.this.weakFocusedListener);
                  }

                  var6.editingCellProperty().addListener(TreeTableCell.this.weakEditingListener);
                  var6.getVisibleLeafColumns().addListener(TreeTableCell.this.weakVisibleLeafColumnsListener);
                  var6.rootProperty().addListener(TreeTableCell.this.weakRootPropertyListener);
                  this.weakTableViewRef = new WeakReference<>(var6);
               }

               TreeTableCell.this.updateColumnIndex();
            }
         };
      }

      return this.treeTableView;
   }

   private void setTableRow(TreeTableRow<S> var1) {
      this.tableRow.set(var1);
   }

   public final TreeTableRow<S> getTableRow() {
      return (TreeTableRow<S>)this.tableRow.get();
   }

   public final ReadOnlyObjectProperty<TreeTableRow<S>> tableRowProperty() {
      return this.tableRow.getReadOnlyProperty();
   }

   @Deprecated(
      since = "17"
   )
   public final TreeTableRow<S> getTreeTableRow() {
      return this.getTableRow();
   }

   @Override
   public void startEdit() {
      if (!this.isEditing()) {
         TreeTableView var1 = this.getTreeTableView();
         TreeTableColumn var2 = this.getTableColumn();
         TreeTableRow var3 = this.getTableRow();
         if (this.isEditable() && (var1 == null || var1.isEditable()) && (var2 == null || var2.isEditable()) && (var3 == null || var3.isEditable())) {
            if (!this.lockItemOnEdit) {
               this.updateItem(-1);
            }

            super.startEdit();
            if (var2 != null) {
               TreeTableColumn.CellEditEvent var4 = new TreeTableColumn.CellEditEvent(var1, var1.getEditingCell(), TreeTableColumn.editStartEvent(), null);
               Event.fireEvent(var2, var4);
            }

            this.editingCellAtStartEdit = new TreeTablePosition<>(var1, this.getIndex(), var2);
         }
      }
   }

   @Override
   public void commitEdit(T var1) {
      if (this.isEditing()) {
         TreeTableView var2 = this.getTreeTableView();
         if (var2 != null) {
            TreeTablePosition var3 = var2.getEditingCell();
            TreeTableColumn.CellEditEvent var4 = new TreeTableColumn.CellEditEvent<>(var2, var3, TreeTableColumn.editCommitEvent(), var1);
            Event.fireEvent(this.getTableColumn(), var4);
         }

         super.commitEdit((T)var1);
         this.updateItem((T)var1, false);
         if (var2 != null) {
            var2.edit(-1, null);
            ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(var2);
         }
      }
   }

   @Override
   public void cancelEdit() {
      if (this.isEditing()) {
         TreeTableView var1 = this.getTreeTableView();
         super.cancelEdit();
         if (var1 != null) {
            if (this.updateEditingIndex) {
               var1.edit(-1, null);
            }

            ControlUtils.requestFocusOnControlOnlyIfCurrentFocusOwnerIsChild(var1);
            TreeTableColumn.CellEditEvent var2 = new TreeTableColumn.CellEditEvent<>(var1, this.editingCellAtStartEdit, TreeTableColumn.editCancelEvent(), null);
            Event.fireEvent(this.getTableColumn(), var2);
         }
      }
   }

   @Override
   public void updateSelected(boolean var1) {
      if (this.getTableRow() != null && !this.getTableRow().isEmpty()) {
         this.setSelected(var1);
      }
   }

   @Override
   void indexChanged(int var1, int var2) {
      super.indexChanged(var1, var2);
      if (!this.isEditing() || var2 != var1) {
         this.updateItem(var1);
         this.updateSelection();
         this.updateFocus();
         this.updateEditing();
      }
   }

   private void updateColumnIndex() {
      TreeTableView var1 = this.getTreeTableView();
      TreeTableColumn var2 = this.getTableColumn();
      this.columnIndex = var1 != null && var2 != null ? var1.getVisibleLeafIndex(var2) : -1;
      this.isLastVisibleColumn = this.getTableColumn() != null && this.columnIndex != -1 && this.columnIndex == var1.getVisibleLeafColumns().size() - 1;
      this.pseudoClassStateChanged(PSEUDO_CLASS_LAST_VISIBLE, this.isLastVisibleColumn);
   }

   private void updateSelection() {
      if (!this.isEmpty()) {
         boolean var1 = this.isSelected();
         if (!this.isInCellSelectionMode()) {
            if (var1) {
               this.updateSelected(false);
            }
         } else {
            TreeTableView var2 = this.getTreeTableView();
            if (this.getIndex() != -1 && var2 != null) {
               TreeTableView.TreeTableViewSelectionModel var3 = var2.getSelectionModel();
               if (var3 == null) {
                  this.updateSelected(false);
               } else {
                  boolean var4 = var3.isSelected(this.getIndex(), this.getTableColumn());
                  if (var1 != var4) {
                     this.updateSelected(var4);
                  }
               }
            }
         }
      }
   }

   private void updateFocus() {
      boolean var1 = this.isFocused();
      if (!this.isInCellSelectionMode()) {
         if (var1) {
            this.setFocused(false);
         }
      } else {
         TreeTableView var2 = this.getTreeTableView();
         if (this.getIndex() != -1 && var2 != null) {
            TreeTableView.TreeTableViewFocusModel var3 = var2.getFocusModel();
            if (var3 == null) {
               this.setFocused(false);
            } else {
               this.setFocused(var3.isFocused(this.getIndex(), this.getTableColumn()));
            }
         }
      }
   }

   private void updateEditing() {
      TreeTableView var1 = this.getTreeTableView();
      if (this.getIndex() != -1 && var1 != null) {
         TreeTablePosition var2 = var1.getEditingCell();
         boolean var3 = this.match(var2);
         if (var3 && !this.isEditing()) {
            this.startEdit();
         } else if (!var3 && this.isEditing()) {
            this.doCancelEdit();
         }
      } else {
         if (this.isEditing()) {
            this.doCancelEdit();
         }
      }
   }

   private void doCancelEdit() {
      try {
         this.updateEditingIndex = false;
         this.cancelEdit();
      } finally {
         this.updateEditingIndex = true;
      }
   }

   private boolean match(TreeTablePosition var1) {
      return var1 != null && var1.getRow() == this.getIndex() && var1.getTableColumn() == this.getTableColumn();
   }

   private boolean isInCellSelectionMode() {
      TreeTableView var1 = this.getTreeTableView();
      if (var1 == null) {
         return false;
      } else {
         TreeTableView.TreeTableViewSelectionModel var2 = var1.getSelectionModel();
         return var2 != null && var2.isCellSelectionEnabled();
      }
   }

   private void updateItem(int var1) {
      if (this.currentObservableValue != null) {
         this.currentObservableValue.removeListener(this.weaktableRowUpdateObserver);
      }

      TreeTableView var2 = this.getTreeTableView();
      TreeTableColumn var3 = this.getTableColumn();
      int var4 = var2 == null ? -1 : this.getTreeTableView().getExpandedItemCount();
      int var5 = this.getIndex();
      boolean var6 = this.isEmpty();
      Object var7 = this.getItem();
      TreeTableRow var8 = this.getTableRow();
      Object var9 = var8 == null ? null : var8.getItem();
      boolean var10 = var5 >= var4;
      if (!var10 && var5 >= 0 && this.columnIndex >= 0 && this.isVisible() && var3 != null && var3.isVisible() && var2.getRoot() != null) {
         label58: {
            this.currentObservableValue = var3.getCellObservableValue(var5);
            Object var11 = this.currentObservableValue == null ? null : this.currentObservableValue.getValue();
            if (var1 == var5 && !this.isItemChanged((T)var7, (T)var11)) {
               Object var12 = this.oldRowItemRef != null ? this.oldRowItemRef.get() : null;
               if (var12 != null && var12.equals(var9)) {
                  break label58;
               }
            }

            this.updateItem((T)var11, false);
         }

         this.oldRowItemRef = (WeakReference<S>)(new WeakReference<>(var9));
         if (this.currentObservableValue != null) {
            this.currentObservableValue.addListener(this.weaktableRowUpdateObserver);
         }
      } else {
         if (!var6 && var7 != null || this.isFirstRun || var10) {
            this.updateItem(null, true);
            this.isFirstRun = false;
         }
      }
   }

   @Override
   protected void layoutChildren() {
      if (this.itemDirty) {
         this.updateItem(-1);
         this.itemDirty = false;
      }

      super.layoutChildren();
   }

   public final void updateTreeTableView(TreeTableView<S> var1) {
      this.setTreeTableView(var1);
   }

   public final void updateTableRow(TreeTableRow<S> var1) {
      this.setTableRow(var1);
   }

   public final void updateTableColumn(TreeTableColumn<S, T> var1) {
      TreeTableColumn var2 = this.getTableColumn();
      if (var2 != null) {
         var2.getStyleClass().removeListener(this.weakColumnStyleClassListener);
         this.getStyleClass().removeAll(var2.getStyleClass());
         var2.idProperty().removeListener(this.weakColumnIdListener);
         var2.styleProperty().removeListener(this.weakColumnStyleListener);
         String var3 = this.getId();
         String var4 = this.getStyle();
         if (var3 != null && var3.equals(var2.getId())) {
            this.setId(null);
         }

         if (var4 != null && var4.equals(var2.getStyle())) {
            this.setStyle("");
         }
      }

      this.setTableColumn(var1);
      if (var1 != null) {
         this.getStyleClass().addAll(var1.getStyleClass());
         var1.getStyleClass().addListener(this.weakColumnStyleClassListener);
         var1.idProperty().addListener(this.weakColumnIdListener);
         var1.styleProperty().addListener(this.weakColumnStyleListener);
         this.possiblySetId(var1.getId());
         this.possiblySetStyle(var1.getStyle());
      }
   }

   @Deprecated(
      since = "17"
   )
   public final void updateTreeTableRow(TreeTableRow<S> var1) {
      this.updateTableRow(var1);
   }

   @Deprecated(
      since = "17"
   )
   public final void updateTreeTableColumn(TreeTableColumn<S, T> var1) {
      this.updateTableColumn(var1);
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TreeTableCellSkin<>(this);
   }

   private void possiblySetId(String var1) {
      if (this.getId() == null || this.getId().isEmpty()) {
         this.setId(var1);
      }
   }

   private void possiblySetStyle(String var1) {
      if (this.getStyle() == null || this.getStyle().isEmpty()) {
         this.setStyle(var1);
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case ROW_INDEX:
            return this.getIndex();
         case COLUMN_INDEX:
            return this.columnIndex;
         case SELECTED:
            return this.isInCellSelectionMode() ? this.isSelected() : this.getTableRow().isSelected();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case REQUEST_FOCUS:
            TreeTableView var3 = this.getTreeTableView();
            if (var3 != null) {
               TreeTableView.TreeTableViewFocusModel var4 = var3.getFocusModel();
               if (var4 != null) {
                  var4.focus(this.getIndex(), this.getTableColumn());
               }
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }
}
