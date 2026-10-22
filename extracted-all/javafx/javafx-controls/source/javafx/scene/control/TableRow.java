package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.TableRowSkin;

public class TableRow<T> extends IndexedCell<T> {
   private ListChangeListener<Integer> selectedListener = var1 -> this.updateSelection();
   private final InvalidationListener focusedListener = var1 -> this.updateFocus();
   private final InvalidationListener editingListener = var1 -> this.updateEditing();
   private final WeakListChangeListener<Integer> weakSelectedListener = new WeakListChangeListener(this.selectedListener);
   private final WeakInvalidationListener weakFocusedListener = new WeakInvalidationListener(this.focusedListener);
   private final WeakInvalidationListener weakEditingListener = new WeakInvalidationListener(this.editingListener);
   private ReadOnlyObjectWrapper<TableView<T>> tableView;
   private boolean isFirstRun = true;
   private static final String DEFAULT_STYLE_CLASS = "table-row-cell";

   public TableRow() {
      this.getStyleClass().addAll(new String[]{"table-row-cell"});
      this.setAccessibleRole(AccessibleRole.TABLE_ROW);
   }

   private void setTableView(TableView<T> var1) {
      this.tableViewPropertyImpl().set(var1);
   }

   public final TableView<T> getTableView() {
      return this.tableView == null ? null : (TableView)this.tableView.get();
   }

   public final ReadOnlyObjectProperty<TableView<T>> tableViewProperty() {
      return this.tableViewPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<TableView<T>> tableViewPropertyImpl() {
      if (this.tableView == null) {
         this.tableView = new ReadOnlyObjectWrapper<TableView<T>>() {
            private WeakReference<TableView<T>> weakTableViewRef;

            protected void invalidated() {
               if (this.weakTableViewRef != null) {
                  TableView var3 = this.weakTableViewRef.get();
                  if (var3 != null) {
                     TableView.TableViewSelectionModel var1 = var3.getSelectionModel();
                     if (var1 != null) {
                        var1.getSelectedIndices().removeListener(TableRow.this.weakSelectedListener);
                     }

                     TableView.TableViewFocusModel var2 = var3.getFocusModel();
                     if (var2 != null) {
                        var2.focusedCellProperty().removeListener(TableRow.this.weakFocusedListener);
                     }

                     var3.editingCellProperty().removeListener(TableRow.this.weakEditingListener);
                  }

                  this.weakTableViewRef = null;
               }

               TableView var6 = TableRow.this.getTableView();
               if (var6 != null) {
                  TableView.TableViewSelectionModel var4 = var6.getSelectionModel();
                  if (var4 != null) {
                     var4.getSelectedIndices().addListener(TableRow.this.weakSelectedListener);
                  }

                  TableView.TableViewFocusModel var5 = var6.getFocusModel();
                  if (var5 != null) {
                     var5.focusedCellProperty().addListener(TableRow.this.weakFocusedListener);
                  }

                  var6.editingCellProperty().addListener(TableRow.this.weakEditingListener);
                  this.weakTableViewRef = new WeakReference<>((TableView<T>)this.get());
               }
            }

            public Object getBean() {
               return TableRow.this;
            }

            public String getName() {
               return "tableView";
            }
         };
      }

      return this.tableView;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TableRowSkin<>(this);
   }

   @Override
   void indexChanged(int var1, int var2) {
      super.indexChanged(var1, var2);
      this.updateItem(var1);
      this.updateSelection();
      this.updateFocus();
   }

   private void updateItem(int var1) {
      TableView var2 = this.getTableView();
      if (var2 != null && var2.getItems() != null) {
         ObservableList var3 = var2.getItems();
         int var4 = var3 == null ? -1 : var3.size();
         int var5 = this.getIndex();
         boolean var6 = var5 >= 0 && var5 < var4;
         Object var7 = this.getItem();
         boolean var8 = this.isEmpty();
         if (var6) {
            Object var9 = var3.get(var5);
            if (var1 != var5 || this.isItemChanged((T)var7, (T)var9)) {
               this.updateItem((T)var9, false);
            }
         } else if (!var8 && var7 != null || this.isFirstRun) {
            this.updateItem(null, true);
            this.isFirstRun = false;
         }
      }
   }

   private void updateSelection() {
      if (this.getIndex() != -1) {
         TableView var1 = this.getTableView();
         boolean var2 = var1 != null
            && var1.getSelectionModel() != null
            && !var1.getSelectionModel().isCellSelectionEnabled()
            && var1.getSelectionModel().isSelected(this.getIndex());
         this.updateSelected(var2);
      }
   }

   private void updateFocus() {
      if (this.getIndex() != -1) {
         TableView var1 = this.getTableView();
         if (var1 != null) {
            TableView.TableViewSelectionModel var2 = var1.getSelectionModel();
            TableView.TableViewFocusModel var3 = var1.getFocusModel();
            if (var2 != null && var3 != null) {
               boolean var4 = !var2.isCellSelectionEnabled() && var3.isFocused(this.getIndex());
               this.setFocused(var4);
            }
         }
      }
   }

   private void updateEditing() {
      if (this.getIndex() != -1) {
         TableView var1 = this.getTableView();
         if (var1 != null) {
            TableView.TableViewSelectionModel var2 = var1.getSelectionModel();
            if (var2 != null && !var2.isCellSelectionEnabled()) {
               TablePosition var3 = var1.getEditingCell();
               if (var3 == null || var3.getTableColumn() == null) {
                  boolean var4 = var3 == null ? false : var3.getRow() == this.getIndex();
                  if (!this.isEditing() && var4) {
                     this.startEdit();
                  } else if (this.isEditing() && !var4) {
                     this.cancelEdit();
                  }
               }
            }
         }
      }
   }

   public final void updateTableView(TableView<T> var1) {
      this.setTableView(var1);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case INDEX:
            return this.getIndex();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
