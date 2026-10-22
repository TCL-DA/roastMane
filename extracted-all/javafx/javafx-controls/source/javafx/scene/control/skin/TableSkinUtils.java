package javafx.scene.control.skin;

import com.sun.javafx.scene.control.TreeTableViewBackingList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

class TableSkinUtils {
   private TableSkinUtils() {
   }

   public static boolean resizeColumn(TableViewSkinBase<?, ?, ?, ?, ?> var0, TableColumnBase<?, ?> var1, double var2) {
      if (!var1.isResizable()) {
         return false;
      } else {
         Control var4 = var0.getSkinnable();
         if (var4 instanceof TableView) {
            return ((TableView)var4).resizeColumn((TableColumn)var1, var2);
         } else {
            return var4 instanceof TreeTableView ? ((TreeTableView)var4).resizeColumn((TreeTableColumn)var1, var2) : false;
         }
      }
   }

   public static ObjectProperty<Callback<ResizeFeaturesBase, Boolean>> columnResizePolicyProperty(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).columnResizePolicyProperty();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).columnResizePolicyProperty() : null;
      }
   }

   public static BooleanProperty tableMenuButtonVisibleProperty(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).tableMenuButtonVisibleProperty();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).tableMenuButtonVisibleProperty() : null;
      }
   }

   public static ObjectProperty<Node> placeholderProperty(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).placeholderProperty();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).placeholderProperty() : null;
      }
   }

   public static <C extends Control, I extends IndexedCell<?>> ObjectProperty<Callback<C, I>> rowFactoryProperty(TableViewSkinBase<?, ?, C, I, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).rowFactoryProperty();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).rowFactoryProperty() : null;
      }
   }

   public static ObservableList<TableColumnBase<?, ?>> getSortOrder(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getSortOrder();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getSortOrder() : FXCollections.emptyObservableList();
      }
   }

   public static ObservableList<TableColumnBase<?, ?>> getColumns(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getColumns();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getColumns() : FXCollections.emptyObservableList();
      }
   }

   public static <T> TableSelectionModel<T> getSelectionModel(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getSelectionModel();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getSelectionModel() : null;
      }
   }

   public static <T> TableFocusModel<T, ?> getFocusModel(TableViewSkinBase<T, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getFocusModel();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getFocusModel() : null;
      }
   }

   public static <T, TC extends TableColumnBase<T, ?>> TablePositionBase<? extends TC> getFocusedCell(TableViewSkinBase<?, T, ?, ?, TC> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getFocusModel().getFocusedCell();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getFocusModel().getFocusedCell() : null;
      }
   }

   public static <TC extends TableColumnBase<?, ?>> ObservableList<TC> getVisibleLeafColumns(TableViewSkinBase<?, ?, ?, ?, TC> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).getVisibleLeafColumns();
      } else {
         return var1 instanceof TreeTableView ? ((TreeTableView)var1).getVisibleLeafColumns() : FXCollections.emptyObservableList();
      }
   }

   public static int getVisibleLeafIndex(TableViewSkinBase<?, ?, ?, ?, ?> var0, TableColumnBase var1) {
      Control var2 = var0.getSkinnable();
      if (var2 instanceof TableView) {
         return ((TableView)var2).getVisibleLeafIndex((TableColumn)var1);
      } else {
         return var2 instanceof TreeTableView ? ((TreeTableView)var2).getVisibleLeafIndex((TreeTableColumn)var1) : -1;
      }
   }

   public static <T, TC extends TableColumnBase<T, ?>> TC getVisibleLeafColumn(TableViewSkinBase<?, T, ?, ?, TC> var0, int var1) {
      Control var2 = var0.getSkinnable();
      if (var2 instanceof TableView) {
         return (TC)((TableView)var2).getVisibleLeafColumn(var1);
      } else {
         return (TC)(var2 instanceof TreeTableView ? ((TreeTableView)var2).getVisibleLeafColumn(var1) : null);
      }
   }

   public static <T> ObjectProperty<ObservableList<T>> itemsProperty(TableViewSkinBase<?, ?, ?, ?, ?> var0) {
      Control var1 = var0.getSkinnable();
      if (var1 instanceof TableView) {
         return ((TableView)var1).itemsProperty();
      } else if (var1 instanceof TreeTableView && var0 instanceof TreeTableViewSkin) {
         TreeTableViewSkin var2 = (TreeTableViewSkin)var0;
         if (var2.tableBackingListProperty == null) {
            var2.tableBackingList = new TreeTableViewBackingList<>((TreeTableView<T>)var1);
            var2.tableBackingListProperty = new SimpleObjectProperty(var2.tableBackingList);
         }

         return var2.tableBackingListProperty;
      } else {
         return null;
      }
   }
}
