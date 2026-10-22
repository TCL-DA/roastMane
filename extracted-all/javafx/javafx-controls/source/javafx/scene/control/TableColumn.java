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
import javafx.scene.control.skin.TableViewSkin;
import javafx.util.Callback;

public class TableColumn<S, T> extends TableColumnBase<S, T> implements EventTarget {
   private static final EventType<?> EDIT_ANY_EVENT = new EventType(Event.ANY, "TABLE_COLUMN_EDIT");
   private static final EventType<?> EDIT_START_EVENT = new EventType(editAnyEvent(), "EDIT_START");
   private static final EventType<?> EDIT_CANCEL_EVENT = new EventType(editAnyEvent(), "EDIT_CANCEL");
   private static final EventType<?> EDIT_COMMIT_EVENT = new EventType(editAnyEvent(), "EDIT_COMMIT");
   public static final Callback<TableColumn<?, ?>, TableCell<?, ?>> DEFAULT_CELL_FACTORY = new Callback<TableColumn<?, ?>, TableCell<?, ?>>() {
      public TableCell<?, ?> call(TableColumn<?, ?> var1) {
         return new TableCell<Object, Object>() {
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
   private EventHandler<TableColumn.CellEditEvent<S, T>> DEFAULT_EDIT_COMMIT_HANDLER = var1 -> {
      int var2 = var1.getTablePosition().getRow();
      ObservableList var3 = var1.getTableView().getItems();
      if (var3 != null && var2 >= 0 && var2 < var3.size()) {
         Object var4 = var3.get(var2);
         ObservableValue var5 = this.getCellObservableValue((S)var4);
         if (var5 instanceof WritableValue) {
            ((WritableValue)var5).setValue(var1.getNewValue());
         }
      }
   };
   private ListChangeListener<TableColumn<S, ?>> columnsListener = var1 -> {
      while (var1.next()) {
         for (TableColumn var3 : var1.getRemoved()) {
            if (!this.getColumns().contains(var3)) {
               var3.setTableView(null);
               var3.setParentColumn(null);
            }
         }

         for (TableColumn var5 : var1.getAddedSubList()) {
            var5.setTableView(this.getTableView());
         }

         this.updateColumnWidths();
      }
   };
   private WeakListChangeListener<TableColumn<S, ?>> weakColumnsListener = new WeakListChangeListener(this.columnsListener);
   private final ObservableList<TableColumn<S, ?>> columns = FXCollections.observableArrayList();
   private ReadOnlyObjectWrapper<TableView<S>> tableView = new ReadOnlyObjectWrapper(this, "tableView");
   private ObjectProperty<Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>>> cellValueFactory;
   private final ObjectProperty<Callback<TableColumn<S, T>, TableCell<S, T>>> cellFactory = new SimpleObjectProperty<Callback<TableColumn<S, T>, TableCell<S, T>>>(
      this, "cellFactory", DEFAULT_CELL_FACTORY
   ) {
      protected void invalidated() {
         TableView var1 = TableColumn.this.getTableView();
         if (var1 != null) {
            ObservableMap var2 = var1.getProperties();
            if (var2.containsKey("recreateKey")) {
               var2.remove("recreateKey");
            }

            var2.put("recreateKey", Boolean.TRUE);
         }
      }
   };
   private ObjectProperty<TableColumn.SortType> sortType;
   private ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditStart;
   private ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditCommit;
   private ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditCancel;
   private static final String DEFAULT_STYLE_CLASS = "table-column";

   public static <S, T> EventType<TableColumn.CellEditEvent<S, T>> editAnyEvent() {
      return (EventType<TableColumn.CellEditEvent<S, T>>)EDIT_ANY_EVENT;
   }

   public static <S, T> EventType<TableColumn.CellEditEvent<S, T>> editStartEvent() {
      return (EventType<TableColumn.CellEditEvent<S, T>>)EDIT_START_EVENT;
   }

   public static <S, T> EventType<TableColumn.CellEditEvent<S, T>> editCancelEvent() {
      return (EventType<TableColumn.CellEditEvent<S, T>>)EDIT_CANCEL_EVENT;
   }

   public static <S, T> EventType<TableColumn.CellEditEvent<S, T>> editCommitEvent() {
      return (EventType<TableColumn.CellEditEvent<S, T>>)EDIT_COMMIT_EVENT;
   }

   public TableColumn() {
      this.getStyleClass().add("table-column");
      this.setOnEditCommit(this.DEFAULT_EDIT_COMMIT_HANDLER);
      this.getColumns().addListener(this.weakColumnsListener);
      this.tableViewProperty().addListener(var1 -> {
         for (TableColumn var3 : this.getColumns()) {
            var3.setTableView(this.getTableView());
         }
      });
   }

   public TableColumn(String var1) {
      this();
      this.setText(var1);
   }

   public final ReadOnlyObjectProperty<TableView<S>> tableViewProperty() {
      return this.tableView.getReadOnlyProperty();
   }

   final void setTableView(TableView<S> var1) {
      this.tableView.set(var1);
   }

   public final TableView<S> getTableView() {
      return (TableView<S>)this.tableView.get();
   }

   public final void setCellValueFactory(Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> var1) {
      this.cellValueFactoryProperty().set(var1);
   }

   public final Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> getCellValueFactory() {
      return this.cellValueFactory == null ? null : (Callback)this.cellValueFactory.get();
   }

   public final ObjectProperty<Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>>> cellValueFactoryProperty() {
      if (this.cellValueFactory == null) {
         this.cellValueFactory = new SimpleObjectProperty(this, "cellValueFactory");
      }

      return this.cellValueFactory;
   }

   public final void setCellFactory(Callback<TableColumn<S, T>, TableCell<S, T>> var1) {
      this.cellFactory.set(var1);
   }

   public final Callback<TableColumn<S, T>, TableCell<S, T>> getCellFactory() {
      return (Callback<TableColumn<S, T>, TableCell<S, T>>)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<TableColumn<S, T>, TableCell<S, T>>> cellFactoryProperty() {
      return this.cellFactory;
   }

   public final ObjectProperty<TableColumn.SortType> sortTypeProperty() {
      if (this.sortType == null) {
         this.sortType = new SimpleObjectProperty(this, "sortType", TableColumn.SortType.ASCENDING);
      }

      return this.sortType;
   }

   public final void setSortType(TableColumn.SortType var1) {
      this.sortTypeProperty().set(var1);
   }

   public final TableColumn.SortType getSortType() {
      return this.sortType == null ? TableColumn.SortType.ASCENDING : (TableColumn.SortType)this.sortType.get();
   }

   public final void setOnEditStart(EventHandler<TableColumn.CellEditEvent<S, T>> var1) {
      this.onEditStartProperty().set(var1);
   }

   public final EventHandler<TableColumn.CellEditEvent<S, T>> getOnEditStart() {
      return this.onEditStart == null ? null : (EventHandler)this.onEditStart.get();
   }

   public final ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditStartProperty() {
      if (this.onEditStart == null) {
         this.onEditStart = new SimpleObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>>(this, "onEditStart") {
            protected void invalidated() {
               TableColumn.this.eventHandlerManager.setEventHandler(TableColumn.editStartEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditStart;
   }

   public final void setOnEditCommit(EventHandler<TableColumn.CellEditEvent<S, T>> var1) {
      this.onEditCommitProperty().set(var1);
   }

   public final EventHandler<TableColumn.CellEditEvent<S, T>> getOnEditCommit() {
      return this.onEditCommit == null ? null : (EventHandler)this.onEditCommit.get();
   }

   public final ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditCommitProperty() {
      if (this.onEditCommit == null) {
         this.onEditCommit = new SimpleObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>>(this, "onEditCommit") {
            protected void invalidated() {
               TableColumn.this.eventHandlerManager.setEventHandler(TableColumn.editCommitEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCommit;
   }

   public final void setOnEditCancel(EventHandler<TableColumn.CellEditEvent<S, T>> var1) {
      this.onEditCancelProperty().set(var1);
   }

   public final EventHandler<TableColumn.CellEditEvent<S, T>> getOnEditCancel() {
      return this.onEditCancel == null ? null : (EventHandler)this.onEditCancel.get();
   }

   public final ObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>> onEditCancelProperty() {
      if (this.onEditCancel == null) {
         this.onEditCancel = new SimpleObjectProperty<EventHandler<TableColumn.CellEditEvent<S, T>>>(this, "onEditCancel") {
            protected void invalidated() {
               TableColumn.this.eventHandlerManager.setEventHandler(TableColumn.editCancelEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCancel;
   }

   @Override
   public final ObservableList<TableColumn<S, ?>> getColumns() {
      return this.columns;
   }

   @Override
   public final ObservableValue<T> getCellObservableValue(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         TableView var2 = this.getTableView();
         if (var2 != null && var2.getItems() != null) {
            ObservableList var3 = var2.getItems();
            if (var1 >= var3.size()) {
               return null;
            } else {
               Object var4 = var3.get(var1);
               return this.getCellObservableValue((S)var4);
            }
         } else {
            return null;
         }
      }
   }

   @Override
   public final ObservableValue<T> getCellObservableValue(S var1) {
      Callback var2 = this.getCellValueFactory();
      if (var2 == null) {
         return null;
      } else {
         TableView var3 = this.getTableView();
         if (var3 == null) {
            return null;
         } else {
            TableColumn.CellDataFeatures var4 = new TableColumn.CellDataFeatures<>(var3, this, (S)var1);
            return (ObservableValue<T>)var2.call(var4);
         }
      }
   }

   public String getTypeSelector() {
      return "TableColumn";
   }

   public Styleable getStyleableParent() {
      return this.getTableView();
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Collections.emptyList();
   }

   public Node getStyleableNode() {
      if (!(this.getTableView().getSkin() instanceof TableViewSkin)) {
         return null;
      } else {
         TableViewSkin var1 = (TableViewSkin)this.getTableView().getSkin();
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
      private final TableView<S> tableView;
      private final TableColumn<S, T> tableColumn;
      private final S value;

      public CellDataFeatures(TableView<S> var1, TableColumn<S, T> var2, S var3) {
         this.tableView = var1;
         this.tableColumn = var2;
         this.value = (S)var3;
      }

      public S getValue() {
         return this.value;
      }

      public TableColumn<S, T> getTableColumn() {
         return this.tableColumn;
      }

      public TableView<S> getTableView() {
         return this.tableView;
      }
   }

   public static class CellEditEvent<S, T> extends Event {
      private static final long serialVersionUID = -609964441682677579L;
      public static final EventType<?> ANY = TableColumn.EDIT_ANY_EVENT;
      private final T newValue;
      private final transient TablePosition<S, T> pos;

      public CellEditEvent(TableView<S> var1, TablePosition<S, T> var2, EventType<TableColumn.CellEditEvent<S, T>> var3, T var4) {
         super(var1, Event.NULL_SOURCE_TARGET, var3);
         if (var1 == null) {
            throw new NullPointerException("TableView can not be null");
         } else {
            this.pos = var2;
            this.newValue = (T)var4;
         }
      }

      public TableView<S> getTableView() {
         return this.pos.getTableView();
      }

      public TableColumn<S, T> getTableColumn() {
         return this.pos.getTableColumn();
      }

      public TablePosition<S, T> getTablePosition() {
         return this.pos;
      }

      public T getNewValue() {
         return this.newValue;
      }

      public T getOldValue() {
         Object var1 = this.getRowValue();
         return var1 != null && this.pos.getTableColumn() != null ? this.pos.getTableColumn().getCellData((S)var1) : null;
      }

      public S getRowValue() {
         ObservableList var1 = this.getTableView().getItems();
         if (var1 == null) {
            return null;
         } else {
            int var2 = this.pos.getRow();
            return (S)(var2 >= 0 && var2 < var1.size() ? var1.get(var2) : null);
         }
      }
   }

   public static enum SortType {
      ASCENDING,
      DESCENDING;
   }
}
