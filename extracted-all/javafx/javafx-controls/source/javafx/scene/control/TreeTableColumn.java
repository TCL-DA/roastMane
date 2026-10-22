package javafx.scene.control;

import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.util.Callback;

public class TreeTableColumn<S, T> extends TableColumnBase<TreeItem<S>, T> implements EventTarget {
   private static final EventType<?> EDIT_ANY_EVENT = new EventType(Event.ANY, "TREE_TABLE_COLUMN_EDIT");
   private static final EventType<?> EDIT_START_EVENT = new EventType(editAnyEvent(), "EDIT_START");
   private static final EventType<?> EDIT_CANCEL_EVENT = new EventType(editAnyEvent(), "EDIT_CANCEL");
   private static final EventType<?> EDIT_COMMIT_EVENT = new EventType(editAnyEvent(), "EDIT_COMMIT");
   public static final Callback<TreeTableColumn<?, ?>, TreeTableCell<?, ?>> DEFAULT_CELL_FACTORY = new Callback<TreeTableColumn<?, ?>, TreeTableCell<?, ?>>() {
      public TreeTableCell<?, ?> call(TreeTableColumn<?, ?> var1) {
         return new TreeTableCell() {
            @Override
            protected void updateItem(Object var1, boolean var2) {
               if (var1 != this.getItem()) {
                  super.updateItem(var1, var2);
                  if (var1 == null) {
                     super.setText(null);
                     super.setGraphic(null);
                  } else if (var1 instanceof Node) {
                     super.setText(null);
                     super.setGraphic((Node)var1);
                  } else {
                     super.setText(var1.toString());
                     super.setGraphic(null);
                  }
               }
            }
         };
      }
   };
   private EventHandler<TreeTableColumn.CellEditEvent<S, T>> DEFAULT_EDIT_COMMIT_HANDLER = var1 -> {
      ObservableValue var2 = this.getCellObservableValue(var1.getRowValue());
      if (var2 instanceof WritableValue) {
         ((WritableValue)var2).setValue(var1.getNewValue());
      }
   };
   private ListChangeListener<TreeTableColumn<S, ?>> columnsListener = new ListChangeListener<TreeTableColumn<S, ?>>() {
      public void onChanged(Change<? extends TreeTableColumn<S, ?>> var1) {
         while (var1.next()) {
            for (TreeTableColumn var3 : var1.getRemoved()) {
               if (!TreeTableColumn.this.getColumns().contains(var3)) {
                  var3.setTreeTableView(null);
                  var3.setParentColumn(null);
               }
            }

            for (TreeTableColumn var5 : var1.getAddedSubList()) {
               var5.setTreeTableView(TreeTableColumn.this.getTreeTableView());
            }

            TreeTableColumn.this.updateColumnWidths();
         }
      }
   };
   private WeakListChangeListener<TreeTableColumn<S, ?>> weakColumnsListener = new WeakListChangeListener(this.columnsListener);
   private final ObservableList<TreeTableColumn<S, ?>> columns = FXCollections.observableArrayList();
   private ReadOnlyObjectWrapper<TreeTableView<S>> treeTableView = new ReadOnlyObjectWrapper(this, "treeTableView");
   private ObjectProperty<Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>>> cellValueFactory;
   private final ObjectProperty<Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>> cellFactory = new SimpleObjectProperty<Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>>(
      this, "cellFactory", DEFAULT_CELL_FACTORY
   ) {
      protected void invalidated() {
         TreeTableView var1 = TreeTableColumn.this.getTreeTableView();
         if (var1 != null) {
            ObservableMap var2 = var1.getProperties();
            if (var2.containsKey("recreateKey")) {
               var2.remove("recreateKey");
            }

            var2.put("recreateKey", Boolean.TRUE);
         }
      }
   };
   private ObjectProperty<TreeTableColumn.SortType> sortType;
   private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditStart;
   private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditCommit;
   private ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditCancel;
   private static final String DEFAULT_STYLE_CLASS = "table-column";

   public static <S, T> EventType<TreeTableColumn.CellEditEvent<S, T>> editAnyEvent() {
      return (EventType<TreeTableColumn.CellEditEvent<S, T>>)EDIT_ANY_EVENT;
   }

   public static <S, T> EventType<TreeTableColumn.CellEditEvent<S, T>> editStartEvent() {
      return (EventType<TreeTableColumn.CellEditEvent<S, T>>)EDIT_START_EVENT;
   }

   public static <S, T> EventType<TreeTableColumn.CellEditEvent<S, T>> editCancelEvent() {
      return (EventType<TreeTableColumn.CellEditEvent<S, T>>)EDIT_CANCEL_EVENT;
   }

   public static <S, T> EventType<TreeTableColumn.CellEditEvent<S, T>> editCommitEvent() {
      return (EventType<TreeTableColumn.CellEditEvent<S, T>>)EDIT_COMMIT_EVENT;
   }

   public TreeTableColumn() {
      this.getStyleClass().add("table-column");
      this.setOnEditCommit(this.DEFAULT_EDIT_COMMIT_HANDLER);
      this.getColumns().addListener(this.weakColumnsListener);
      this.treeTableViewProperty().addListener(var1 -> {
         for (TreeTableColumn var3 : this.getColumns()) {
            var3.setTreeTableView(this.getTreeTableView());
         }
      });
   }

   public TreeTableColumn(String var1) {
      this();
      this.setText(var1);
   }

   public final ReadOnlyObjectProperty<TreeTableView<S>> treeTableViewProperty() {
      return this.treeTableView.getReadOnlyProperty();
   }

   final void setTreeTableView(TreeTableView<S> var1) {
      this.treeTableView.set(var1);
   }

   public final TreeTableView<S> getTreeTableView() {
      return (TreeTableView<S>)this.treeTableView.get();
   }

   public final void setCellValueFactory(Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> var1) {
      this.cellValueFactoryProperty().set(var1);
   }

   public final Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>> getCellValueFactory() {
      return this.cellValueFactory == null ? null : (Callback)this.cellValueFactory.get();
   }

   public final ObjectProperty<Callback<TreeTableColumn.CellDataFeatures<S, T>, ObservableValue<T>>> cellValueFactoryProperty() {
      if (this.cellValueFactory == null) {
         this.cellValueFactory = new SimpleObjectProperty(this, "cellValueFactory");
      }

      return this.cellValueFactory;
   }

   public final void setCellFactory(Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> var1) {
      this.cellFactory.set(var1);
   }

   public final Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> getCellFactory() {
      return (Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>>> cellFactoryProperty() {
      return this.cellFactory;
   }

   public final ObjectProperty<TreeTableColumn.SortType> sortTypeProperty() {
      if (this.sortType == null) {
         this.sortType = new SimpleObjectProperty(this, "sortType", TreeTableColumn.SortType.ASCENDING);
      }

      return this.sortType;
   }

   public final void setSortType(TreeTableColumn.SortType var1) {
      this.sortTypeProperty().set(var1);
   }

   public final TreeTableColumn.SortType getSortType() {
      return this.sortType == null ? TreeTableColumn.SortType.ASCENDING : (TreeTableColumn.SortType)this.sortType.get();
   }

   public final void setOnEditStart(EventHandler<TreeTableColumn.CellEditEvent<S, T>> var1) {
      this.onEditStartProperty().set(var1);
   }

   public final EventHandler<TreeTableColumn.CellEditEvent<S, T>> getOnEditStart() {
      return this.onEditStart == null ? null : (EventHandler)this.onEditStart.get();
   }

   public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditStartProperty() {
      if (this.onEditStart == null) {
         this.onEditStart = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>>(this, "onEditStart") {
            protected void invalidated() {
               TreeTableColumn.this.eventHandlerManager.setEventHandler(TreeTableColumn.editStartEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditStart;
   }

   public final void setOnEditCommit(EventHandler<TreeTableColumn.CellEditEvent<S, T>> var1) {
      this.onEditCommitProperty().set(var1);
   }

   public final EventHandler<TreeTableColumn.CellEditEvent<S, T>> getOnEditCommit() {
      return this.onEditCommit == null ? null : (EventHandler)this.onEditCommit.get();
   }

   public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditCommitProperty() {
      if (this.onEditCommit == null) {
         this.onEditCommit = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>>(this, "onEditCommit") {
            protected void invalidated() {
               TreeTableColumn.this.eventHandlerManager.setEventHandler(TreeTableColumn.editCommitEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCommit;
   }

   public final void setOnEditCancel(EventHandler<TreeTableColumn.CellEditEvent<S, T>> var1) {
      this.onEditCancelProperty().set(var1);
   }

   public final EventHandler<TreeTableColumn.CellEditEvent<S, T>> getOnEditCancel() {
      return this.onEditCancel == null ? null : (EventHandler)this.onEditCancel.get();
   }

   public final ObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>> onEditCancelProperty() {
      if (this.onEditCancel == null) {
         this.onEditCancel = new SimpleObjectProperty<EventHandler<TreeTableColumn.CellEditEvent<S, T>>>(this, "onEditCancel") {
            protected void invalidated() {
               TreeTableColumn.this.eventHandlerManager.setEventHandler(TreeTableColumn.editCancelEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCancel;
   }

   @Override
   public final ObservableList<TreeTableColumn<S, ?>> getColumns() {
      return this.columns;
   }

   @Override
   public final ObservableValue<T> getCellObservableValue(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         TreeTableView var2 = this.getTreeTableView();
         if (var2 != null && var1 < var2.getExpandedItemCount()) {
            TreeItem var3 = var2.getTreeItem(var1);
            return this.getCellObservableValue(var3);
         } else {
            return null;
         }
      }
   }

   public final ObservableValue<T> getCellObservableValue(TreeItem<S> var1) {
      Callback var2 = this.getCellValueFactory();
      if (var2 == null) {
         return null;
      } else {
         TreeTableView var3 = this.getTreeTableView();
         if (var3 == null) {
            return null;
         } else {
            TreeTableColumn.CellDataFeatures var4 = new TreeTableColumn.CellDataFeatures<>(var3, this, var1);
            return (ObservableValue<T>)var2.call(var4);
         }
      }
   }

   public String getTypeSelector() {
      return "TreeTableColumn";
   }

   public Styleable getStyleableParent() {
      return this.getTreeTableView();
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Collections.emptyList();
   }

   public Node getStyleableNode() {
      if (!(this.getTreeTableView().getSkin() instanceof TreeTableViewSkin)) {
         return null;
      } else {
         TreeTableViewSkin var1 = (TreeTableViewSkin)this.getTreeTableView().getSkin();
         TableHeaderRow var2 = null;

         for (Node var4 : var1.getChildren()) {
            if (var4 instanceof TableHeaderRow) {
               var2 = (TableHeaderRow)var4;
            }
         }

         NestedTableColumnHeader var6 = null;

         for (Node var5 : var2.getChildren()) {
            if (var5 instanceof NestedTableColumnHeader) {
               var6 = (NestedTableColumnHeader)var5;
            }
         }

         return this.scan(var6);
      }
   }

   private TableColumnHeader scan(TableColumnHeader var1) {
      if (this.equals(var1.getTableColumn())) {
         return var1;
      } else {
         if (var1 instanceof NestedTableColumnHeader) {
            NestedTableColumnHeader var2 = (NestedTableColumnHeader)var1;

            for (int var3 = 0; var3 < var2.getColumnHeaders().size(); var3++) {
               TableColumnHeader var4 = this.scan((TableColumnHeader)var2.getColumnHeaders().get(var3));
               if (var4 != null) {
                  return var4;
               }
            }
         }

         return null;
      }
   }

   public static class CellDataFeatures<S, T> {
      private final TreeTableView<S> treeTableView;
      private final TreeTableColumn<S, T> tableColumn;
      private final TreeItem<S> value;

      public CellDataFeatures(TreeTableView<S> var1, TreeTableColumn<S, T> var2, TreeItem<S> var3) {
         this.treeTableView = var1;
         this.tableColumn = var2;
         this.value = var3;
      }

      public TreeItem<S> getValue() {
         return this.value;
      }

      public TreeTableColumn<S, T> getTreeTableColumn() {
         return this.tableColumn;
      }

      public TreeTableView<S> getTreeTableView() {
         return this.treeTableView;
      }
   }

   public static class CellEditEvent<S, T> extends Event {
      private static final long serialVersionUID = -609964441682677579L;
      public static final EventType<?> ANY = TreeTableColumn.EDIT_ANY_EVENT;
      private final T newValue;
      private final transient TreeTablePosition<S, T> pos;

      public CellEditEvent(TreeTableView<S> var1, TreeTablePosition<S, T> var2, EventType<TreeTableColumn.CellEditEvent<S, T>> var3, T var4) {
         super(var1, Event.NULL_SOURCE_TARGET, var3);
         if (var1 == null) {
            throw new NullPointerException("TableView can not be null");
         } else {
            this.pos = var2;
            this.newValue = (T)var4;
         }
      }

      public TreeTableView<S> getTreeTableView() {
         return this.pos.getTreeTableView();
      }

      public TreeTableColumn<S, T> getTableColumn() {
         return this.pos.getTableColumn();
      }

      public TreeTablePosition<S, T> getTreeTablePosition() {
         return this.pos;
      }

      public T getNewValue() {
         return this.newValue;
      }

      public T getOldValue() {
         TreeItem var1 = this.getRowValue();
         return var1 != null && this.pos.getTableColumn() != null ? this.pos.getTableColumn().getCellData(var1) : null;
      }

      public TreeItem<S> getRowValue() {
         TreeTableView var1 = this.getTreeTableView();
         int var2 = this.pos.getRow();
         return var2 >= 0 && var2 < var1.getExpandedItemCount() ? var1.getTreeItem(var2) : null;
      }
   }

   public static enum SortType {
      ASCENDING,
      DESCENDING;
   }
}
