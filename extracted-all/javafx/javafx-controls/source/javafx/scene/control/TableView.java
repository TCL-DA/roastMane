package javafx.scene.control;

import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.MappingChange.Map;
import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange;
import com.sun.javafx.collections.NonIterableChange.SimpleAddChange;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.SelectedCellsMap;
import com.sun.javafx.scene.control.TableColumnComparatorBase;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.WeakHashMap;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.SortedList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.TableViewSkin;
import javafx.util.Callback;

@DefaultProperty("items")
public class TableView<S> extends Control {
   static final String SET_CONTENT_WIDTH = "TableView.contentWidth";
   public static final Callback<TableView.ResizeFeatures, Boolean> UNCONSTRAINED_RESIZE_POLICY = new Callback<TableView.ResizeFeatures, Boolean>() {
      @Override
      public String toString() {
         return "unconstrained-resize";
      }

      public Boolean call(TableView.ResizeFeatures var1) {
         double var2 = TableUtil.resize(var1.getColumn(), var1.getDelta());
         return Double.compare(var2, 0.0) == 0;
      }
   };
   public static final Callback<TableView.ResizeFeatures, Boolean> CONSTRAINED_RESIZE_POLICY = new Callback<TableView.ResizeFeatures, Boolean>() {
      private boolean isFirstRun = true;

      @Override
      public String toString() {
         return "constrained-resize";
      }

      public Boolean call(TableView.ResizeFeatures var1) {
         TableView var2 = var1.getTable();
         ObservableList var3 = var2.getVisibleLeafColumns();
         Boolean var4 = TableUtil.constrainedResize(var1, this.isFirstRun, var2.contentWidth, var3);
         this.isFirstRun = !this.isFirstRun ? false : !var4;
         return var4;
      }
   };
   public static final Callback<TableView, Boolean> DEFAULT_SORT_POLICY = new Callback<TableView, Boolean>() {
      public Boolean call(TableView var1) {
         try {
            ObservableList var2 = var1.getItems();
            if (var2 instanceof SortedList) {
               SortedList var7 = (SortedList)var2;
               boolean var4 = var7.comparatorProperty().isEqualTo(var1.comparatorProperty()).get();
               if (!var4 && Logging.getControlsLogger().isLoggable(Level.INFO)) {
                  String var5 = "TableView items list is a SortedList, but the SortedList comparator should be bound to the TableView comparator for sorting to be enabled (e.g. sortedList.comparatorProperty().bind(tableView.comparatorProperty());).";
                  Logging.getControlsLogger().info(var5);
               }

               return var4;
            } else if (var2 != null && !var2.isEmpty()) {
               Comparator var3 = var1.getComparator();
               if (var3 == null) {
                  return true;
               } else {
                  FXCollections.sort(var2, var3);
                  return true;
               }
            } else {
               return true;
            }
         } catch (UnsupportedOperationException var6) {
            return false;
         }
      }
   };
   private final ObservableList<TableColumn<S, ?>> columns = FXCollections.observableArrayList();
   private final ObservableList<TableColumn<S, ?>> visibleLeafColumns = FXCollections.observableArrayList();
   private final ObservableList<TableColumn<S, ?>> unmodifiableVisibleLeafColumns = FXCollections.unmodifiableObservableList(this.visibleLeafColumns);
   private ObservableList<TableColumn<S, ?>> sortOrder = FXCollections.observableArrayList();
   private double contentWidth;
   private boolean isInited = false;
   private final ListChangeListener<TableColumn<S, ?>> columnsObserver = new ListChangeListener<TableColumn<S, ?>>() {
      public void onChanged(Change<? extends TableColumn<S, ?>> var1) {
         ObservableList var2 = TableView.this.getColumns();

         while (var1.next()) {
            if (var1.wasAdded()) {
               ArrayList var3 = new ArrayList();

               for (TableColumn var5 : var1.getAddedSubList()) {
                  if (var5 != null) {
                     int var6 = 0;

                     for (TableColumn var8 : var2) {
                        if (var5 == var8) {
                           var6++;
                        }
                     }

                     if (var6 > 1) {
                        var3.add(var5);
                     }
                  }
               }

               if (!var3.isEmpty()) {
                  String var15 = "";

                  for (TableColumn var21 : var3) {
                     var15 = var15 + "'" + var21.getText() + "', ";
                  }

                  throw new IllegalStateException("Duplicate TableColumns detected in TableView columns list with titles " + var15);
               }
            }
         }

         var1.reset();
         ArrayList var14 = new ArrayList();

         while (var1.next()) {
            List var16 = var1.getRemoved();
            List var19 = var1.getAddedSubList();
            if (var1.wasRemoved()) {
               var14.addAll(var16);

               for (TableColumn var25 : var16) {
                  var25.setTableView(null);
               }
            }

            if (var1.wasAdded()) {
               var14.removeAll(var19);

               for (TableColumn var26 : var19) {
                  var26.setTableView(TableView.this);
               }
            }

            TableUtil.removeColumnsListener(var16, TableView.this.weakColumnsObserver);
            TableUtil.addColumnsListener(var19, TableView.this.weakColumnsObserver);
            TableUtil.removeTableColumnListener(
               var1.getRemoved(),
               TableView.this.weakColumnVisibleObserver,
               TableView.this.weakColumnSortableObserver,
               TableView.this.weakColumnSortTypeObserver,
               TableView.this.weakColumnComparatorObserver
            );
            TableUtil.addTableColumnListener(
               var1.getAddedSubList(),
               TableView.this.weakColumnVisibleObserver,
               TableView.this.weakColumnSortableObserver,
               TableView.this.weakColumnSortTypeObserver,
               TableView.this.weakColumnComparatorObserver
            );
         }

         TableView.this.updateVisibleLeafColumns();
         TableView.this.sortOrder.removeAll(var14);
         TableView.TableViewFocusModel var17 = TableView.this.getFocusModel();
         TableView.TableViewSelectionModel var20 = TableView.this.getSelectionModel();
         var1.reset();
         ArrayList var24 = new ArrayList();
         ArrayList var27 = new ArrayList();

         while (var1.next()) {
            if (var1.wasRemoved()) {
               var24.addAll(var1.getRemoved());
            }

            if (var1.wasAdded()) {
               var27.addAll(var1.getAddedSubList());
            }
         }

         var24.removeAll(var27);
         if (var17 != null) {
            TablePosition var28 = var17.getFocusedCell();
            boolean var9 = false;

            for (TableColumn var11 : var24) {
               var9 = var28 != null && var28.getTableColumn() == var11;
               if (var9) {
                  break;
               }
            }

            if (var9) {
               int var33 = TableView.this.lastKnownColumnIndex.getOrDefault(var28.getTableColumn(), Integer.valueOf(0));
               int var36 = var33 == 0 ? 0 : Math.min(TableView.this.getVisibleLeafColumns().size() - 1, var33 - 1);
               var17.focus(var28.getRow(), TableView.this.getVisibleLeafColumn(var36));
            }
         }

         if (var20 != null) {
            for (TablePosition var34 : new ArrayList(var20.getSelectedCells())) {
               boolean var37 = false;

               for (TableColumn var13 : var24) {
                  var37 = var34 != null && var34.getTableColumn() == var13;
                  if (var37) {
                     break;
                  }
               }

               if (var37) {
                  int var38 = TableView.this.lastKnownColumnIndex.getOrDefault(var34.getTableColumn(), Integer.valueOf(-1));
                  if (var38 != -1) {
                     if (var20 instanceof TableView.TableViewArrayListSelectionModel) {
                        TablePosition var39 = new TablePosition(TableView.this, var34.getRow(), var34.getTableColumn());
                        var39.fixedColumnIndex = var38;
                        ((TableView.TableViewArrayListSelectionModel)var20).clearSelection(var39);
                     } else {
                        var20.clearSelection(var34.getRow(), var34.getTableColumn());
                     }
                  }
               }
            }
         }

         TableView.this.lastKnownColumnIndex.clear();

         for (TableColumn var32 : TableView.this.getColumns()) {
            int var35 = TableView.this.getVisibleLeafIndex(var32);
            if (var35 > -1) {
               TableView.this.lastKnownColumnIndex.put(var32, var35);
            }
         }
      }
   };
   private final WeakHashMap<TableColumn<S, ?>, Integer> lastKnownColumnIndex = new WeakHashMap<>();
   private final InvalidationListener columnVisibleObserver = var1x -> this.updateVisibleLeafColumns();
   private final InvalidationListener columnSortableObserver = var1x -> {
      Object var2 = ((Property)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_SORTABLE_CHANGE, var2);
      }
   };
   private final InvalidationListener columnSortTypeObserver = var1x -> {
      Object var2 = ((Property)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_SORT_TYPE_CHANGE, var2);
      }
   };
   private final InvalidationListener columnComparatorObserver = var1x -> {
      Object var2 = ((Property)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_COMPARATOR_CHANGE, var2);
      }
   };
   private final InvalidationListener cellSelectionModelInvalidationListener = var1x -> {
      boolean var2 = ((BooleanProperty)var1x).get();
      this.pseudoClassStateChanged(PSEUDO_CLASS_CELL_SELECTION, var2);
      this.pseudoClassStateChanged(PSEUDO_CLASS_ROW_SELECTION, !var2);
   };
   private final WeakInvalidationListener weakColumnVisibleObserver = new WeakInvalidationListener(this.columnVisibleObserver);
   private final WeakInvalidationListener weakColumnSortableObserver = new WeakInvalidationListener(this.columnSortableObserver);
   private final WeakInvalidationListener weakColumnSortTypeObserver = new WeakInvalidationListener(this.columnSortTypeObserver);
   private final WeakInvalidationListener weakColumnComparatorObserver = new WeakInvalidationListener(this.columnComparatorObserver);
   private final WeakListChangeListener<TableColumn<S, ?>> weakColumnsObserver = new WeakListChangeListener(this.columnsObserver);
   private final WeakInvalidationListener weakCellSelectionModelInvalidationListener = new WeakInvalidationListener(this.cellSelectionModelInvalidationListener);
   private ObjectProperty<ObservableList<S>> items = new SimpleObjectProperty<ObservableList<S>>(this, "items") {
      WeakReference<ObservableList<S>> oldItemsRef;

      protected void invalidated() {
         ObservableList var1 = this.oldItemsRef == null ? null : this.oldItemsRef.get();
         ObservableList var2 = TableView.this.getItems();
         if (var2 == null || var2 != var1) {
            if (!(var2 instanceof SortedList)) {
               TableView.this.getSortOrder().clear();
            }

            this.oldItemsRef = new WeakReference<>(var2);
         }
      }
   };
   private BooleanProperty tableMenuButtonVisible;
   private ObjectProperty<Callback<TableView.ResizeFeatures, Boolean>> columnResizePolicy;
   private ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactory;
   private ObjectProperty<Node> placeholder;
   private ObjectProperty<TableView.TableViewSelectionModel<S>> selectionModel = new SimpleObjectProperty<TableView.TableViewSelectionModel<S>>(
      this, "selectionModel"
   ) {
      TableView.TableViewSelectionModel<S> oldValue = null;

      protected void invalidated() {
         if (this.oldValue != null) {
            this.oldValue.cellSelectionEnabledProperty().removeListener(TableView.this.weakCellSelectionModelInvalidationListener);
            if (this.oldValue instanceof TableView.TableViewArrayListSelectionModel) {
               ((TableView.TableViewArrayListSelectionModel)this.oldValue).dispose();
            }
         }

         this.oldValue = (TableView.TableViewSelectionModel<S>)this.get();
         if (this.oldValue != null) {
            this.oldValue.cellSelectionEnabledProperty().addListener(TableView.this.weakCellSelectionModelInvalidationListener);
            TableView.this.weakCellSelectionModelInvalidationListener.invalidated(this.oldValue.cellSelectionEnabledProperty());
         }
      }
   };
   private ObjectProperty<TableView.TableViewFocusModel<S>> focusModel;
   private BooleanProperty editable;
   private DoubleProperty fixedCellSize;
   private ReadOnlyObjectWrapper<TablePosition<S, ?>> editingCell;
   private ReadOnlyObjectWrapper<Comparator<S>> comparator;
   private ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicy;
   private ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSort;
   private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
   private ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumn;
   private boolean sortLock = false;
   private TableUtil.SortEventType lastSortEventType = null;
   private Object[] lastSortEventSupportInfo = null;
   private static final String DEFAULT_STYLE_CLASS = "table-view";
   private static final PseudoClass PSEUDO_CLASS_CELL_SELECTION = PseudoClass.getPseudoClass("cell-selection");
   private static final PseudoClass PSEUDO_CLASS_ROW_SELECTION = PseudoClass.getPseudoClass("row-selection");

   public TableView() {
      this(FXCollections.observableArrayList());
   }

   public TableView(ObservableList<S> var1) {
      this.getStyleClass().setAll(new String[]{"table-view"});
      this.setAccessibleRole(AccessibleRole.TABLE_VIEW);
      this.setItems(var1);
      this.setSelectionModel(new TableView.TableViewArrayListSelectionModel<>(this));
      this.setFocusModel(new TableView.TableViewFocusModel<>(this));
      this.getColumns().addListener(this.weakColumnsObserver);
      this.getSortOrder().addListener(var1x -> this.doSort(TableUtil.SortEventType.SORT_ORDER_CHANGE, var1x));
      this.getProperties().addListener(new MapChangeListener<Object, Object>() {
         public void onChanged(javafx.collections.MapChangeListener.Change<? extends Object, ? extends Object> var1) {
            if (var1.wasAdded() && "TableView.contentWidth".equals(var1.getKey())) {
               if (var1.getValueAdded() instanceof Number) {
                  TableView.this.setContentWidth((Double)var1.getValueAdded());
               }

               TableView.this.getProperties().remove("TableView.contentWidth");
            }
         }
      });
      this.pseudoClassStateChanged(PseudoClass.getPseudoClass(this.getColumnResizePolicy().toString()), true);
      this.isInited = true;
   }

   public final ObjectProperty<ObservableList<S>> itemsProperty() {
      return this.items;
   }

   public final void setItems(ObservableList<S> var1) {
      this.itemsProperty().set(var1);
   }

   public final ObservableList<S> getItems() {
      return (ObservableList<S>)this.items.get();
   }

   public final BooleanProperty tableMenuButtonVisibleProperty() {
      if (this.tableMenuButtonVisible == null) {
         this.tableMenuButtonVisible = new SimpleBooleanProperty(this, "tableMenuButtonVisible");
      }

      return this.tableMenuButtonVisible;
   }

   public final void setTableMenuButtonVisible(boolean var1) {
      this.tableMenuButtonVisibleProperty().set(var1);
   }

   public final boolean isTableMenuButtonVisible() {
      return this.tableMenuButtonVisible == null ? false : this.tableMenuButtonVisible.get();
   }

   public final void setColumnResizePolicy(Callback<TableView.ResizeFeatures, Boolean> var1) {
      this.columnResizePolicyProperty().set(var1);
   }

   public final Callback<TableView.ResizeFeatures, Boolean> getColumnResizePolicy() {
      return this.columnResizePolicy == null ? UNCONSTRAINED_RESIZE_POLICY : (Callback)this.columnResizePolicy.get();
   }

   public final ObjectProperty<Callback<TableView.ResizeFeatures, Boolean>> columnResizePolicyProperty() {
      if (this.columnResizePolicy == null) {
         this.columnResizePolicy = new SimpleObjectProperty<Callback<TableView.ResizeFeatures, Boolean>>(
            this, "columnResizePolicy", UNCONSTRAINED_RESIZE_POLICY
         ) {
            private Callback<TableView.ResizeFeatures, Boolean> oldPolicy;

            protected void invalidated() {
               if (TableView.this.isInited) {
                  ((Callback)this.get()).call(new TableView.ResizeFeatures(TableView.this, null, 0.0));
                  if (this.oldPolicy != null) {
                     PseudoClass var1 = PseudoClass.getPseudoClass(this.oldPolicy.toString());
                     TableView.this.pseudoClassStateChanged(var1, false);
                  }

                  if (this.get() != null) {
                     PseudoClass var2 = PseudoClass.getPseudoClass(((Callback)this.get()).toString());
                     TableView.this.pseudoClassStateChanged(var2, true);
                  }

                  this.oldPolicy = (Callback<TableView.ResizeFeatures, Boolean>)this.get();
               }
            }
         };
      }

      return this.columnResizePolicy;
   }

   public final ObjectProperty<Callback<TableView<S>, TableRow<S>>> rowFactoryProperty() {
      if (this.rowFactory == null) {
         this.rowFactory = new SimpleObjectProperty(this, "rowFactory");
      }

      return this.rowFactory;
   }

   public final void setRowFactory(Callback<TableView<S>, TableRow<S>> var1) {
      this.rowFactoryProperty().set(var1);
   }

   public final Callback<TableView<S>, TableRow<S>> getRowFactory() {
      return this.rowFactory == null ? null : (Callback)this.rowFactory.get();
   }

   public final ObjectProperty<Node> placeholderProperty() {
      if (this.placeholder == null) {
         this.placeholder = new SimpleObjectProperty(this, "placeholder");
      }

      return this.placeholder;
   }

   public final void setPlaceholder(Node var1) {
      this.placeholderProperty().set(var1);
   }

   public final Node getPlaceholder() {
      return this.placeholder == null ? null : (Node)this.placeholder.get();
   }

   public final ObjectProperty<TableView.TableViewSelectionModel<S>> selectionModelProperty() {
      return this.selectionModel;
   }

   public final void setSelectionModel(TableView.TableViewSelectionModel<S> var1) {
      this.selectionModelProperty().set(var1);
   }

   public final TableView.TableViewSelectionModel<S> getSelectionModel() {
      return (TableView.TableViewSelectionModel<S>)this.selectionModel.get();
   }

   public final void setFocusModel(TableView.TableViewFocusModel<S> var1) {
      this.focusModelProperty().set(var1);
   }

   public final TableView.TableViewFocusModel<S> getFocusModel() {
      return this.focusModel == null ? null : (TableView.TableViewFocusModel)this.focusModel.get();
   }

   public final ObjectProperty<TableView.TableViewFocusModel<S>> focusModelProperty() {
      if (this.focusModel == null) {
         this.focusModel = new SimpleObjectProperty(this, "focusModel");
      }

      return this.focusModel;
   }

   public final void setEditable(boolean var1) {
      this.editableProperty().set(var1);
   }

   public final boolean isEditable() {
      return this.editable == null ? false : this.editable.get();
   }

   public final BooleanProperty editableProperty() {
      if (this.editable == null) {
         this.editable = new SimpleBooleanProperty(this, "editable", false);
      }

      return this.editable;
   }

   public final void setFixedCellSize(double var1) {
      this.fixedCellSizeProperty().set(var1);
   }

   public final double getFixedCellSize() {
      return this.fixedCellSize == null ? -1.0 : this.fixedCellSize.get();
   }

   public final DoubleProperty fixedCellSizeProperty() {
      if (this.fixedCellSize == null) {
         this.fixedCellSize = new StyleableDoubleProperty(-1.0) {
            public CssMetaData<TableView<?>, Number> getCssMetaData() {
               return TableView.StyleableProperties.FIXED_CELL_SIZE;
            }

            public Object getBean() {
               return TableView.this;
            }

            public String getName() {
               return "fixedCellSize";
            }
         };
      }

      return this.fixedCellSize;
   }

   private void setEditingCell(TablePosition<S, ?> var1) {
      this.editingCellPropertyImpl().set(var1);
   }

   public final TablePosition<S, ?> getEditingCell() {
      return this.editingCell == null ? null : (TablePosition)this.editingCell.get();
   }

   public final ReadOnlyObjectProperty<TablePosition<S, ?>> editingCellProperty() {
      return this.editingCellPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<TablePosition<S, ?>> editingCellPropertyImpl() {
      if (this.editingCell == null) {
         this.editingCell = new ReadOnlyObjectWrapper(this, "editingCell");
      }

      return this.editingCell;
   }

   private void setComparator(Comparator<S> var1) {
      this.comparatorPropertyImpl().set(var1);
   }

   public final Comparator<S> getComparator() {
      return this.comparator == null ? null : (Comparator)this.comparator.get();
   }

   public final ReadOnlyObjectProperty<Comparator<S>> comparatorProperty() {
      return this.comparatorPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<Comparator<S>> comparatorPropertyImpl() {
      if (this.comparator == null) {
         this.comparator = new ReadOnlyObjectWrapper(this, "comparator");
      }

      return this.comparator;
   }

   public final void setSortPolicy(Callback<TableView<S>, Boolean> var1) {
      this.sortPolicyProperty().set(var1);
   }

   public final Callback<TableView<S>, Boolean> getSortPolicy() {
      return this.sortPolicy == null ? DEFAULT_SORT_POLICY : (Callback)this.sortPolicy.get();
   }

   public final ObjectProperty<Callback<TableView<S>, Boolean>> sortPolicyProperty() {
      if (this.sortPolicy == null) {
         this.sortPolicy = new SimpleObjectProperty<Callback<TableView<S>, Boolean>>(this, "sortPolicy", DEFAULT_SORT_POLICY) {
            protected void invalidated() {
               TableView.this.sort();
            }
         };
      }

      return this.sortPolicy;
   }

   public void setOnSort(EventHandler<SortEvent<TableView<S>>> var1) {
      this.onSortProperty().set(var1);
   }

   public EventHandler<SortEvent<TableView<S>>> getOnSort() {
      return this.onSort != null ? (EventHandler)this.onSort.get() : null;
   }

   public ObjectProperty<EventHandler<SortEvent<TableView<S>>>> onSortProperty() {
      if (this.onSort == null) {
         this.onSort = new ObjectPropertyBase<EventHandler<SortEvent<TableView<S>>>>() {
            protected void invalidated() {
               EventType var1 = SortEvent.sortEvent();
               EventHandler var2 = (EventHandler)this.get();
               TableView.this.setEventHandler(var1, var2);
            }

            public Object getBean() {
               return TableView.this;
            }

            public String getName() {
               return "onSort";
            }
         };
      }

      return this.onSort;
   }

   public final ObservableList<TableColumn<S, ?>> getColumns() {
      return this.columns;
   }

   public final ObservableList<TableColumn<S, ?>> getSortOrder() {
      return this.sortOrder;
   }

   public void scrollTo(int var1) {
      ControlUtils.scrollToIndex(this, var1);
   }

   public void scrollTo(S var1) {
      if (this.getItems() != null) {
         int var2 = this.getItems().indexOf(var1);
         if (var2 >= 0) {
            ControlUtils.scrollToIndex(this, var2);
         }
      }
   }

   public void setOnScrollTo(EventHandler<ScrollToEvent<Integer>> var1) {
      this.onScrollToProperty().set(var1);
   }

   public EventHandler<ScrollToEvent<Integer>> getOnScrollTo() {
      return this.onScrollTo != null ? (EventHandler)this.onScrollTo.get() : null;
   }

   public ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollToProperty() {
      if (this.onScrollTo == null) {
         this.onScrollTo = new ObjectPropertyBase<EventHandler<ScrollToEvent<Integer>>>() {
            protected void invalidated() {
               TableView.this.setEventHandler(ScrollToEvent.scrollToTopIndex(), (EventHandler)this.get());
            }

            public Object getBean() {
               return TableView.this;
            }

            public String getName() {
               return "onScrollTo";
            }
         };
      }

      return this.onScrollTo;
   }

   public void scrollToColumn(TableColumn<S, ?> var1) {
      ControlUtils.scrollToColumn(this, var1);
   }

   public void scrollToColumnIndex(int var1) {
      if (this.getColumns() != null) {
         ControlUtils.scrollToColumn(this, (TableColumnBase<?, ?>)this.getColumns().get(var1));
      }
   }

   public void setOnScrollToColumn(EventHandler<ScrollToEvent<TableColumn<S, ?>>> var1) {
      this.onScrollToColumnProperty().set(var1);
   }

   public EventHandler<ScrollToEvent<TableColumn<S, ?>>> getOnScrollToColumn() {
      return this.onScrollToColumn != null ? (EventHandler)this.onScrollToColumn.get() : null;
   }

   public ObjectProperty<EventHandler<ScrollToEvent<TableColumn<S, ?>>>> onScrollToColumnProperty() {
      if (this.onScrollToColumn == null) {
         this.onScrollToColumn = new ObjectPropertyBase<EventHandler<ScrollToEvent<TableColumn<S, ?>>>>() {
            protected void invalidated() {
               EventType var1 = ScrollToEvent.scrollToColumn();
               TableView.this.setEventHandler(var1, (EventHandler)this.get());
            }

            public Object getBean() {
               return TableView.this;
            }

            public String getName() {
               return "onScrollToColumn";
            }
         };
      }

      return this.onScrollToColumn;
   }

   public boolean resizeColumn(TableColumn<S, ?> var1, double var2) {
      return var1 != null && Double.compare(var2, 0.0) != 0
         ? (Boolean)this.getColumnResizePolicy().call(new TableView.ResizeFeatures<>(this, var1, var2))
         : false;
   }

   public void edit(int var1, TableColumn<S, ?> var2) {
      if (this.isEditable() && (var2 == null || var2.isEditable())) {
         if (var1 < 0 && var2 == null) {
            this.setEditingCell(null);
         } else {
            this.setEditingCell(new TablePosition<>(this, var1, var2));
         }
      }
   }

   public ObservableList<TableColumn<S, ?>> getVisibleLeafColumns() {
      return this.unmodifiableVisibleLeafColumns;
   }

   public int getVisibleLeafIndex(TableColumn<S, ?> var1) {
      return this.visibleLeafColumns.indexOf(var1);
   }

   public TableColumn<S, ?> getVisibleLeafColumn(int var1) {
      return var1 >= 0 && var1 < this.visibleLeafColumns.size() ? (TableColumn)this.visibleLeafColumns.get(var1) : null;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TableViewSkin<>(this);
   }

   public void sort() {
      ObservableList var1 = this.getSortOrder();
      Comparator var2 = this.getComparator();
      this.setComparator(var1.isEmpty() ? null : new TableColumnComparatorBase.TableColumnComparator<>(var1));
      SortEvent var3 = new SortEvent<>(this, this);
      this.fireEvent(var3);
      if (!var3.isConsumed()) {
         ArrayList var4 = new ArrayList(this.getSelectionModel().getSelectedCells());
         int var5 = var4.size();
         this.getSelectionModel().startAtomic();
         Callback var6 = this.getSortPolicy();
         if (var6 != null) {
            Boolean var7 = (Boolean)var6.call(this);
            this.getSelectionModel().stopAtomic();
            if (var7 == null || !var7) {
               this.sortLock = true;
               TableUtil.handleSortFailure(var1, this.lastSortEventType, this.lastSortEventSupportInfo);
               this.setComparator(var2);
               this.sortLock = false;
            } else if (this.getSelectionModel() instanceof TableView.TableViewArrayListSelectionModel) {
               TableView.TableViewArrayListSelectionModel var8 = (TableView.TableViewArrayListSelectionModel)this.getSelectionModel();
               ObservableList var9 = var8.getSelectedCells();
               ArrayList var10 = new ArrayList();

               for (int var11 = 0; var11 < var5; var11++) {
                  TablePosition var12 = (TablePosition)var4.get(var11);
                  if (!var9.contains(var12)) {
                     var10.add(var12);
                  }
               }

               if (!var10.isEmpty()) {
                  GenericAddRemoveChange var13 = new GenericAddRemoveChange(0, var5, var10, var9);
                  var8.fireCustomSelectedCellsListChangeEvent(var13);
               }
            }
         }
      }
   }

   public void refresh() {
      this.getProperties().put("recreateKey", Boolean.TRUE);
   }

   private void doSort(TableUtil.SortEventType var1, Object... var2) {
      if (!this.sortLock) {
         this.lastSortEventType = var1;
         this.lastSortEventSupportInfo = var2;
         this.sort();
         this.lastSortEventType = null;
         this.lastSortEventSupportInfo = null;
      }
   }

   private void setContentWidth(double var1) {
      this.contentWidth = var1;
      if (this.isInited) {
         this.getColumnResizePolicy().call(new TableView.ResizeFeatures<>(this, null, 0.0));
      }
   }

   private void updateVisibleLeafColumns() {
      ArrayList var1 = new ArrayList();
      this.buildVisibleLeafColumns(this.getColumns(), var1);
      this.visibleLeafColumns.setAll(var1);
      this.getColumnResizePolicy().call(new TableView.ResizeFeatures<>(this, null, 0.0));
   }

   private void buildVisibleLeafColumns(List<TableColumn<S, ?>> var1, List<TableColumn<S, ?>> var2) {
      for (TableColumn var4 : var1) {
         if (var4 != null) {
            boolean var5 = !var4.getColumns().isEmpty();
            if (var5) {
               this.buildVisibleLeafColumns(var4.getColumns(), var2);
            } else if (var4.isVisible()) {
               var2.add(var4);
            }
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TableView.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case COLUMN_COUNT:
            return this.getVisibleLeafColumns().size();
         case ROW_COUNT:
            return this.getItems().size();
         case SELECTED_ITEMS:
            ObservableList var10 = (ObservableList)super.queryAccessibleAttribute(var1, var2);
            ArrayList var11 = new ArrayList();

            for (TableRow var6 : var10) {
               ObservableList var7 = (ObservableList)var6.queryAccessibleAttribute(var1, var2);
               if (var7 != null) {
                  var11.addAll(var7);
               }
            }

            return FXCollections.observableArrayList(var11);
         case FOCUS_ITEM:
            Node var9 = (Node)super.queryAccessibleAttribute(var1, var2);
            if (var9 == null) {
               return null;
            }

            Node var4 = (Node)var9.queryAccessibleAttribute(var1, var2);
            return var4 != null ? var4 : var9;
         case CELL_AT_ROW_COLUMN:
            TableRow var8 = (TableRow)super.queryAccessibleAttribute(var1, var2);
            return var8 != null ? var8.queryAccessibleAttribute(var1, var2) : null;
         case MULTIPLE_SELECTION:
            TableView.TableViewSelectionModel var3 = this.getSelectionModel();
            return var3 != null && var3.getSelectionMode() == SelectionMode.MULTIPLE;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   public static class ResizeFeatures<S> extends ResizeFeaturesBase<S> {
      private TableView<S> table;

      public ResizeFeatures(TableView<S> var1, TableColumn<S, ?> var2, Double var3) {
         super(var2, var3);
         this.table = var1;
      }

      public TableColumn<S, ?> getColumn() {
         return (TableColumn<S, ?>)super.getColumn();
      }

      public TableView<S> getTable() {
         return this.table;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TableView<?>, Number> FIXED_CELL_SIZE = new CssMetaData<TableView<?>, Number>(
         "-fx-fixed-cell-size", SizeConverter.getInstance(), -1.0
      ) {
         public Double getInitialValue(TableView<?> var1) {
            return var1.getFixedCellSize();
         }

         public boolean isSettable(TableView<?> var1) {
            return var1.fixedCellSize == null || !var1.fixedCellSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TableView<?> var1) {
            return (StyleableProperty<Number>)var1.fixedCellSizeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(FIXED_CELL_SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   static class TableViewArrayListSelectionModel<S> extends TableView.TableViewSelectionModel<S> {
      private int itemCount = 0;
      private final Map<TablePosition<S, ?>, Integer> cellToIndicesMap = var0 -> var0.getRow();
      private final TableView<S> tableView;
      final InvalidationListener itemsPropertyListener;
      final ListChangeListener<S> itemsContentListener = var1x -> {
         this.updateItemCount();
         List var2x = this.getTableModel();
         boolean var3 = true;

         while (var1x.next()) {
            if (var1x.wasReplaced() || var1x.getAddedSize() == this.getItemCount()) {
               this.selectedItemChange = var1x;
               this.updateDefaultSelection();
               this.selectedItemChange = null;
               return;
            }

            Object var4 = this.getSelectedItem();
            int var5 = this.getSelectedIndex();
            if (var2x != null && !var2x.isEmpty()) {
               if (this.getSelectedIndex() == -1 && this.getSelectedItem() != null) {
                  int var8 = var2x.indexOf(this.getSelectedItem());
                  if (var8 != -1) {
                     this.setSelectedIndex(var8);
                     var3 = false;
                  }
               } else if (var1x.wasRemoved()
                  && var1x.getRemovedSize() == 1
                  && !var1x.wasAdded()
                  && var4 != null
                  && var4.equals(var1x.getRemoved().get(0))
                  && this.getSelectedIndex() < this.getItemCount()) {
                  int var6 = var5 == 0 ? 0 : var5 - 1;
                  Object var7 = this.getModelItem(var6);
                  if (!var4.equals(var7)) {
                     this.clearAndSelect(var6);
                  }
               }
            } else {
               this.clearSelection();
            }
         }

         if (var3) {
            this.updateSelection(var1x);
         }
      };
      final WeakListChangeListener<S> weakItemsContentListener = new WeakListChangeListener(this.itemsContentListener);
      private final SelectedCellsMap<TablePosition<S, ?>> selectedCellsMap;
      private final ReadOnlyUnbackedObservableList<TablePosition<S, ?>> selectedCellsSeq;
      private int previousModelSize = 0;

      public TableViewArrayListSelectionModel(final TableView<S> var1) {
         super(var1);
         this.tableView = var1;
         this.itemsPropertyListener = new InvalidationListener() {
            private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>(var1.getItems());

            public void invalidated(Observable var1x) {
               ObservableList var2 = this.weakItemsRef.get();
               this.weakItemsRef = new WeakReference<>(var1.getItems());
               TableViewArrayListSelectionModel.this.updateItemsObserver(var2, var1.getItems());
            }
         };
         this.tableView.itemsProperty().addListener(this.itemsPropertyListener);
         this.selectedCellsMap = new SelectedCellsMap<TablePosition<S, ?>>(this::fireCustomSelectedCellsListChangeEvent) {
            @Override
            public boolean isCellSelectionEnabled() {
               return TableViewArrayListSelectionModel.this.isCellSelectionEnabled();
            }
         };
         this.selectedCellsSeq = new ReadOnlyUnbackedObservableList<TablePosition<S, ?>>() {
            public TablePosition<S, ?> get(int var1) {
               return TableViewArrayListSelectionModel.this.selectedCellsMap.get(var1);
            }

            @Override
            public int size() {
               return TableViewArrayListSelectionModel.this.selectedCellsMap.size();
            }
         };
         ObservableList var2 = this.getTableView().getItems();
         if (var2 != null) {
            var2.addListener(this.weakItemsContentListener);
         }

         this.updateItemCount();
         this.updateDefaultSelection();
         this.cellSelectionEnabledProperty().addListener(var2x -> {
            this.updateDefaultSelection();
            TableCellBehaviorBase.setAnchor(var1, this.getFocusedCell(), true);
         });
      }

      private void dispose() {
         this.tableView.itemsProperty().removeListener(this.itemsPropertyListener);
         ObservableList var1 = this.getTableView().getItems();
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentListener);
         }
      }

      @Override
      public ObservableList<TablePosition> getSelectedCells() {
         return (ObservableList<TablePosition>)this.selectedCellsSeq;
      }

      private void updateSelection(Change<? extends S> var1) {
         var1.reset();
         int var2 = 0;
         int var3 = -1;

         while (var1.next()) {
            if (var1.wasReplaced()) {
               if (var1.getList().isEmpty()) {
                  this.clearSelection();
               } else {
                  int var14 = this.getSelectedIndex();
                  if (this.previousModelSize == var1.getRemovedSize()) {
                     this.clearSelection();
                  } else if (var14 < this.getItemCount() && var14 >= 0) {
                     this.startAtomic();
                     this.clearSelection(var14);
                     this.stopAtomic();
                     this.select(var14);
                  } else {
                     this.clearSelection();
                  }
               }
            } else if (!var1.wasAdded() && !var1.wasRemoved()) {
               if (var1.wasPermutated()) {
                  this.startAtomic();
                  int var4 = this.getSelectedIndex();
                  int var5 = var1.getTo() - var1.getFrom();
                  HashMap var6 = new HashMap(var5);

                  for (int var7 = var1.getFrom(); var7 < var1.getTo(); var7++) {
                     var6.put(var7, var1.getPermutation(var7));
                  }

                  ArrayList var17 = new ArrayList((ObservableList)this.getSelectedCells());
                  ArrayList var8 = new ArrayList(var17.size());
                  boolean var9 = false;

                  for (int var10 = 0; var10 < var17.size(); var10++) {
                     TablePosition var11 = (TablePosition)var17.get(var10);
                     int var12 = var11.getRow();
                     if (var6.containsKey(var12)) {
                        int var13 = (Integer)var6.get(var12);
                        var9 = var9 || var13 != var12;
                        var8.add(new TablePosition(var11.getTableView(), var13, var11.getTableColumn()));
                     }
                  }

                  if (var9) {
                     this.quietClearSelection();
                     this.stopAtomic();
                     this.selectedCellsMap.setAll(var8);
                     if (var4 >= 0 && var4 < this.itemCount) {
                        int var18 = var1.getPermutation(var4);
                        this.setSelectedIndex(var18);
                        this.focus(var18);
                     }
                  } else {
                     this.stopAtomic();
                  }
               }
            } else {
               var3 = var1.getFrom();
               var2 += var1.wasAdded() ? var1.getAddedSize() : -var1.getRemovedSize();
            }
         }

         TablePosition var15 = (TablePosition)TableCellBehavior.<TablePosition>getAnchor(this.tableView, null);
         if (var2 != 0 && var3 >= 0 && var15 != null && (var1.wasRemoved() || var1.wasAdded()) && this.isSelected(var15.getRow(), var15.getTableColumn())) {
            TablePosition var16 = new TablePosition<>(this.tableView, var15.getRow() + var2, var15.getTableColumn());
            TableCellBehavior.setAnchor(this.tableView, var16, false);
         }

         this.shiftSelection(var3, var2, new Callback<MultipleSelectionModelBase.ShiftParams, Void>() {
            public Void call(MultipleSelectionModelBase.ShiftParams var1) {
               TableViewArrayListSelectionModel.this.startAtomic();
               int var2 = var1.getClearIndex();
               int var3 = var1.getSetIndex();
               TablePosition var4 = null;
               if (var2 > -1) {
                  for (int var5 = 0; var5 < TableViewArrayListSelectionModel.this.selectedCellsMap.size(); var5++) {
                     TablePosition var6 = TableViewArrayListSelectionModel.this.selectedCellsMap.get(var5);
                     if (var6.getRow() == var2) {
                        var4 = var6;
                        TableViewArrayListSelectionModel.this.selectedCellsMap.remove(var6);
                     } else if (var6.getRow() == var3 && !var1.isSelected()) {
                        TableViewArrayListSelectionModel.this.selectedCellsMap.remove(var6);
                     }
                  }
               }

               if (var4 != null && var1.isSelected()) {
                  TablePosition var7 = new TablePosition<>(TableViewArrayListSelectionModel.this.tableView, var1.getSetIndex(), var4.getTableColumn());
                  TableViewArrayListSelectionModel.this.selectedCellsMap.add(var7);
               }

               TableViewArrayListSelectionModel.this.stopAtomic();
               return null;
            }
         });
         this.previousModelSize = this.getItemCount();
      }

      @Override
      public void clearAndSelect(int var1) {
         this.clearAndSelect(var1, null);
      }

      @Override
      public void clearAndSelect(int var1, TableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TablePosition var3 = new TablePosition<>(this.getTableView(), var1, var2);
            boolean var4 = this.isCellSelectionEnabled();
            TableCellBehavior.setAnchor(this.tableView, var3, false);
            ArrayList var5 = new ArrayList(this.selectedCellsMap.getSelectedCells());
            boolean var6 = this.isSelected(var1, var2);
            if (var6 && var5.size() == 1) {
               TablePosition var7 = (TablePosition)this.getSelectedCells().get(0);
               if (this.getSelectedItem() == this.getModelItem(var1) && var7.getRow() == var1 && var7.getTableColumn() == var2) {
                  return;
               }
            }

            this.startAtomic();
            this.clearSelection();
            this.select(var1, var2);
            this.stopAtomic();
            if (var4) {
               var5.remove(var3);
            } else {
               for (TablePosition var8 : var5) {
                  if (var8.getRow() == var1) {
                     var5.remove(var8);
                     break;
                  }
               }
            }

            Object var11;
            if (var6) {
               var11 = ControlUtils.buildClearAndSelectChange(this.selectedCellsSeq, var5, var3, Comparator.comparing(TablePositionBase::getRow));
            } else {
               int var12 = var4 ? 0 : Math.max(0, this.selectedCellsSeq.indexOf(var3));
               int var9 = var4 ? this.getSelectedCells().size() : 1;
               var11 = new GenericAddRemoveChange(var12, var12 + var9, var5, this.selectedCellsSeq);
            }

            this.fireCustomSelectedCellsListChangeEvent((Change<? extends TablePosition<S, ?>>)var11);
         }
      }

      @Override
      public void select(int var1) {
         this.select(var1, null);
      }

      @Override
      public void select(int var1, TableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            if (this.isCellSelectionEnabled() && var2 == null) {
               ObservableList var3 = this.getTableView().getVisibleLeafColumns();

               for (int var4 = 0; var4 < var3.size(); var4++) {
                  this.select(var1, (TableColumn<S, ?>)var3.get(var4));
               }
            } else {
               if (TableCellBehavior.hasDefaultAnchor(this.tableView)) {
                  TableCellBehavior.removeAnchor(this.tableView);
               }

               if (this.getSelectionMode() == SelectionMode.SINGLE) {
                  this.quietClearSelection();
               }

               this.selectedCellsMap.add(new TablePosition<>(this.getTableView(), var1, var2));
               this.updateSelectedIndex(var1);
               this.focus(var1, var2);
            }
         }
      }

      @Override
      public void select(S var1) {
         if (var1 == null && this.getSelectionMode() == SelectionMode.SINGLE) {
            this.clearSelection();
         } else {
            Object var2 = null;

            for (int var3 = 0; var3 < this.getItemCount(); var3++) {
               var2 = this.getModelItem(var3);
               if (var2 != null && var2.equals(var1)) {
                  if (this.isSelected(var3)) {
                     return;
                  }

                  if (this.getSelectionMode() == SelectionMode.SINGLE) {
                     this.quietClearSelection();
                  }

                  this.select(var3);
                  return;
               }
            }

            this.setSelectedIndex(-1);
            this.setSelectedItem((S)var1);
         }
      }

      @Override
      public void selectIndices(int var1, int... var2) {
         if (var2 == null) {
            this.select(var1);
         } else {
            int var3 = this.getItemCount();
            if (this.getSelectionMode() == SelectionMode.SINGLE) {
               this.quietClearSelection();

               for (int var4 = var2.length - 1; var4 >= 0; var4--) {
                  int var5 = var2[var4];
                  if (var5 >= 0 && var5 < var3) {
                     this.select(var5);
                     break;
                  }
               }

               if (this.selectedCellsMap.isEmpty() && var1 > 0 && var1 < var3) {
                  this.select(var1);
               }
            } else {
               int var10 = -1;
               LinkedHashSet var11 = new LinkedHashSet();
               if (var1 >= 0 && var1 < var3) {
                  if (this.isCellSelectionEnabled()) {
                     ObservableList var6 = this.getTableView().getVisibleLeafColumns();

                     for (int var7 = 0; var7 < var6.size(); var7++) {
                        if (!this.selectedCellsMap.isSelected(var1, var7)) {
                           var11.add(new TablePosition<>(this.getTableView(), var1, (TableColumn)var6.get(var7)));
                        }
                     }
                  } else {
                     boolean var12 = this.selectedCellsMap.isSelected(var1, -1);
                     if (!var12) {
                        var11.add(new TablePosition<>(this.getTableView(), var1, null));
                     }
                  }

                  var10 = var1;
               }

               for (int var13 = 0; var13 < var2.length; var13++) {
                  int var14 = var2[var13];
                  if (var14 >= 0 && var14 < var3) {
                     var10 = var14;
                     if (this.isCellSelectionEnabled()) {
                        ObservableList var8 = this.getTableView().getVisibleLeafColumns();

                        for (int var9 = 0; var9 < var8.size(); var9++) {
                           if (!this.selectedCellsMap.isSelected(var14, var9)) {
                              var11.add(new TablePosition<>(this.getTableView(), var14, (TableColumn)var8.get(var9)));
                              var10 = var14;
                           }
                        }
                     } else if (!this.selectedCellsMap.isSelected(var14, -1)) {
                        var11.add(new TablePosition<>(this.getTableView(), var14, null));
                     }
                  }
               }

               this.selectedCellsMap.addAll(var11);
               if (var10 != -1) {
                  this.select(var10);
               }
            }
         }
      }

      @Override
      public void selectAll() {
         if (this.getSelectionMode() != SelectionMode.SINGLE) {
            if (this.isCellSelectionEnabled()) {
               ArrayList var1 = new ArrayList();
               TablePosition var3 = null;

               for (int var4 = 0; var4 < this.getTableView().getVisibleLeafColumns().size(); var4++) {
                  TableColumn var2 = (TableColumn)this.getTableView().getVisibleLeafColumns().get(var4);

                  for (int var5 = 0; var5 < this.getItemCount(); var5++) {
                     var3 = new TablePosition<>(this.getTableView(), var5, var2);
                     var1.add(var3);
                  }
               }

               this.selectedCellsMap.setAll(var1);
               if (var3 != null) {
                  this.select(var3.getRow(), var3.getTableColumn());
                  this.focus(var3.getRow(), var3.getTableColumn());
               }
            } else {
               ArrayList var6 = new ArrayList();

               for (int var7 = 0; var7 < this.getItemCount(); var7++) {
                  var6.add(new TablePosition<>(this.getTableView(), var7, null));
               }

               this.selectedCellsMap.setAll(var6);
               int var8 = this.getFocusedIndex();
               if (var8 == -1) {
                  int var9 = this.getItemCount();
                  if (var9 > 0) {
                     this.select(var9 - 1);
                     this.focus((TablePosition<S, ?>)var6.get(var6.size() - 1));
                  }
               } else {
                  this.select(var8);
                  this.focus(var8);
               }
            }
         }
      }

      @Override
      public void selectRange(int var1, TableColumnBase<S, ?> var2, int var3, TableColumnBase<S, ?> var4) {
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
            this.select(var3, var4);
         } else {
            this.startAtomic();
            int var5 = this.getItemCount();
            boolean var6 = this.isCellSelectionEnabled();
            int var7 = this.tableView.getVisibleLeafIndex((TableColumn<S, ?>)var2);
            int var8 = this.tableView.getVisibleLeafIndex((TableColumn<S, ?>)var4);
            int var9 = Math.min(var7, var8);
            int var10 = Math.max(var7, var8);
            int var11 = Math.min(var1, var3);
            int var12 = Math.max(var1, var3);
            ArrayList var13 = new ArrayList();

            for (int var14 = var11; var14 <= var12; var14++) {
               if (var14 >= 0 && var14 < var5) {
                  if (!var6) {
                     var13.add(new TablePosition<>(this.tableView, var14, (TableColumn)var2));
                  } else {
                     for (int var15 = var9; var15 <= var10; var15++) {
                        TableColumn var16 = this.tableView.getVisibleLeafColumn(var15);
                        if (var16 != null || !var6) {
                           var13.add(new TablePosition<>(this.tableView, var14, var16));
                        }
                     }
                  }
               }
            }

            var13.removeAll(this.getSelectedCells());
            this.selectedCellsMap.addAll(var13);
            this.stopAtomic();
            this.updateSelectedIndex(var3);
            this.focus(var3, (TableColumn<S, ?>)var4);
            TableColumn var21 = (TableColumn)var2;
            TableColumn var22 = var6 ? (TableColumn)var4 : var21;
            int var23 = this.selectedCellsMap.indexOf(new TablePosition<>(this.tableView, var1, var21));
            int var17 = this.selectedCellsMap.indexOf(new TablePosition<>(this.tableView, var3, var22));
            if (var23 > -1 && var17 > -1) {
               int var18 = Math.min(var23, var17);
               int var19 = Math.max(var23, var17);
               SimpleAddChange var20 = new SimpleAddChange(var18, var19 + 1, this.selectedCellsSeq);
               this.fireCustomSelectedCellsListChangeEvent(var20);
            }
         }
      }

      @Override
      public void clearSelection(int var1) {
         this.clearSelection(var1, null);
      }

      @Override
      public void clearSelection(int var1, TableColumn<S, ?> var2) {
         this.clearSelection(new TablePosition<>(this.getTableView(), var1, var2));
      }

      private void clearSelection(TablePosition<S, ?> var1) {
         boolean var2 = this.isCellSelectionEnabled();
         int var3 = var1.getRow();
         boolean var4 = var1.getTableColumn() == null;
         ArrayList var5 = new ArrayList();

         for (TablePosition var7 : this.getSelectedCells()) {
            if (!var2) {
               if (var7.getRow() == var3) {
                  var5.add(var7);
                  break;
               }
            } else if (var4 && var7.getRow() == var3) {
               var5.add(var7);
            } else if (var7.equals(var1)) {
               var5.add(var1);
               break;
            }
         }

         var5.stream().forEach(this.selectedCellsMap::remove);
         if (this.isEmpty() && !this.isAtomic()) {
            this.updateSelectedIndex(-1);
            this.selectedCellsMap.clear();
         }
      }

      @Override
      public void clearSelection() {
         final ArrayList var1 = new ArrayList(this.getSelectedCells());
         this.quietClearSelection();
         if (!this.isAtomic()) {
            this.updateSelectedIndex(-1);
            this.focus(-1);
            if (!var1.isEmpty()) {
               NonIterableChange var2 = new NonIterableChange<TablePosition<S, ?>>(0, 0, this.selectedCellsSeq) {
                  public List<TablePosition<S, ?>> getRemoved() {
                     return var1;
                  }
               };
               this.fireCustomSelectedCellsListChangeEvent(var2);
            }
         }
      }

      private void quietClearSelection() {
         this.startAtomic();
         this.selectedCellsMap.clear();
         this.stopAtomic();
      }

      @Override
      public boolean isSelected(int var1) {
         return this.isSelected(var1, null);
      }

      @Override
      public boolean isSelected(int var1, TableColumn<S, ?> var2) {
         boolean var3 = this.isCellSelectionEnabled();
         if (var3 && var2 == null) {
            int var6 = this.tableView.getVisibleLeafColumns().size();

            for (int var5 = 0; var5 < var6; var5++) {
               if (!this.selectedCellsMap.isSelected(var1, var5)) {
                  return false;
               }
            }

            return true;
         } else {
            int var4 = var3 && var2 != null ? this.tableView.getVisibleLeafIndex(var2) : -1;
            return this.selectedCellsMap.isSelected(var1, var4);
         }
      }

      @Override
      public boolean isEmpty() {
         return this.selectedCellsMap.isEmpty();
      }

      @Override
      public void selectPrevious() {
         if (this.isCellSelectionEnabled()) {
            TablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() - 1 >= 0) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
            } else if (var1.getRow() < this.getItemCount() - 1) {
               this.select(var1.getRow() - 1, this.getTableColumn(this.getTableView().getVisibleLeafColumns().size() - 1));
            }
         } else {
            int var2 = this.getFocusedIndex();
            if (var2 == -1) {
               this.select(this.getItemCount() - 1);
            } else if (var2 > 0) {
               this.select(var2 - 1);
            }
         }
      }

      @Override
      public void selectNext() {
         if (this.isCellSelectionEnabled()) {
            TablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() + 1 < this.getTableView().getVisibleLeafColumns().size()) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), 1));
            } else if (var1.getRow() < this.getItemCount() - 1) {
               this.select(var1.getRow() + 1, this.getTableColumn(0));
            }
         } else {
            int var2 = this.getFocusedIndex();
            if (var2 == -1) {
               this.select(0);
            } else if (var2 < this.getItemCount() - 1) {
               this.select(var2 + 1);
            }
         }
      }

      @Override
      public void selectAboveCell() {
         TablePosition var1 = this.getFocusedCell();
         if (var1.getRow() == -1) {
            this.select(this.getItemCount() - 1);
         } else if (var1.getRow() > 0) {
            this.select(var1.getRow() - 1, var1.getTableColumn());
         }
      }

      @Override
      public void selectBelowCell() {
         TablePosition var1 = this.getFocusedCell();
         if (var1.getRow() == -1) {
            this.select(0);
         } else if (var1.getRow() < this.getItemCount() - 1) {
            this.select(var1.getRow() + 1, var1.getTableColumn());
         }
      }

      @Override
      public void selectFirst() {
         TablePosition var1 = this.getFocusedCell();
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
         }

         if (this.getItemCount() > 0) {
            if (this.isCellSelectionEnabled()) {
               this.select(0, var1.getTableColumn());
            } else {
               this.select(0);
            }
         }
      }

      @Override
      public void selectLast() {
         TablePosition var1 = this.getFocusedCell();
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
         }

         int var2 = this.getItemCount();
         if (var2 > 0 && this.getSelectedIndex() < var2 - 1) {
            if (this.isCellSelectionEnabled()) {
               this.select(var2 - 1, var1.getTableColumn());
            } else {
               this.select(var2 - 1);
            }
         }
      }

      @Override
      public void selectLeftCell() {
         if (this.isCellSelectionEnabled()) {
            TablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() - 1 >= 0) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
            }
         }
      }

      @Override
      public void selectRightCell() {
         if (this.isCellSelectionEnabled()) {
            TablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() + 1 < this.getTableView().getVisibleLeafColumns().size()) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), 1));
            }
         }
      }

      private void updateItemsObserver(ObservableList<S> var1, ObservableList<S> var2) {
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentListener);
         }

         if (var2 != null) {
            var2.addListener(this.weakItemsContentListener);
         }

         this.updateItemCount();
         this.updateDefaultSelection();
      }

      private void updateDefaultSelection() {
         int var1 = -1;
         if (this.tableView.getItems() != null) {
            Object var2 = this.getSelectedItem();
            if (var2 != null) {
               var1 = this.tableView.getItems().indexOf(var2);
            }
         }

         this.clearSelection();
         this.select(var1, this.isCellSelectionEnabled() ? this.getTableColumn(0) : null);
      }

      private TableColumn<S, ?> getTableColumn(int var1) {
         return this.getTableView().getVisibleLeafColumn(var1);
      }

      private TableColumn<S, ?> getTableColumn(TableColumn<S, ?> var1, int var2) {
         int var3 = this.getTableView().getVisibleLeafIndex(var1);
         int var4 = var3 + var2;
         return this.getTableView().getVisibleLeafColumn(var4);
      }

      private void updateSelectedIndex(int var1) {
         this.setSelectedIndex(var1);
         this.setSelectedItem(this.getModelItem(var1));
      }

      @Override
      protected int getItemCount() {
         return this.itemCount;
      }

      private void updateItemCount() {
         if (this.tableView == null) {
            this.itemCount = -1;
         } else {
            List var1 = this.getTableModel();
            this.itemCount = var1 == null ? -1 : var1.size();
         }
      }

      private void fireCustomSelectedCellsListChangeEvent(Change<? extends TablePosition<S, ?>> var1) {
         ControlUtils.updateSelectedIndices(this, var1);
         if (!this.isAtomic()) {
            this.selectedCellsSeq.callObservers(new MappingChange(var1, MappingChange.NOOP_MAP, this.selectedCellsSeq));
         }
      }
   }

   public static class TableViewFocusModel<S> extends TableFocusModel<S, TableColumn<S, ?>> {
      private final TableView<S> tableView;
      private final TablePosition<S, ?> EMPTY_CELL;
      private final InvalidationListener itemsObserver;
      private final ListChangeListener<S> itemsContentListener = var1x -> {
         var1x.next();
         if (!var1x.wasReplaced() && var1x.getAddedSize() != this.getItemCount()) {
            TablePosition var2 = this.getFocusedCell();
            int var3 = var2.getRow();
            if (var3 != -1 && var1x.getFrom() <= var3) {
               var1x.reset();
               boolean var4 = false;
               boolean var5 = false;
               int var6 = 0;

               int var7;
               for (var7 = 0; var1x.next(); var7 += var1x.getRemovedSize()) {
                  var4 |= var1x.wasAdded();
                  var5 |= var1x.wasRemoved();
                  var6 += var1x.getAddedSize();
               }

               if (var4 && !var5) {
                  if (var6 < var1x.getList().size()) {
                     int var9 = Math.min(this.getItemCount() - 1, this.getFocusedIndex() + var6);
                     this.focus(var9, var2.getTableColumn());
                  }
               } else if (!var4 && var5) {
                  int var8 = Math.max(0, this.getFocusedIndex() - var7);
                  if (var8 < 0) {
                     this.focus(0, var2.getTableColumn());
                  } else {
                     this.focus(var8, var2.getTableColumn());
                  }
               }
            }
         } else {
            this.updateDefaultFocus();
         }
      };
      private WeakListChangeListener<S> weakItemsContentListener = new WeakListChangeListener(this.itemsContentListener);
      private ReadOnlyObjectWrapper<TablePosition> focusedCell;

      public TableViewFocusModel(final TableView<S> var1) {
         if (var1 == null) {
            throw new NullPointerException("TableView can not be null");
         } else {
            this.tableView = var1;
            this.EMPTY_CELL = new TablePosition<>(var1, -1, null);
            this.itemsObserver = new InvalidationListener() {
               private WeakReference<ObservableList<S>> weakItemsRef = new WeakReference<>(var1.getItems());

               public void invalidated(Observable var1x) {
                  ObservableList var2 = this.weakItemsRef.get();
                  this.weakItemsRef = new WeakReference<>(var1.getItems());
                  TableViewFocusModel.this.updateItemsObserver(var2, var1.getItems());
               }
            };
            this.tableView.itemsProperty().addListener(new WeakInvalidationListener(this.itemsObserver));
            if (var1.getItems() != null) {
               this.tableView.getItems().addListener(this.weakItemsContentListener);
            }

            this.updateDefaultFocus();
            this.focusedCellProperty().addListener(var1x -> var1.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM));
         }
      }

      private void updateItemsObserver(ObservableList<S> var1, ObservableList<S> var2) {
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentListener);
         }

         if (var2 != null) {
            var2.addListener(this.weakItemsContentListener);
         }

         this.updateDefaultFocus();
      }

      @Override
      protected int getItemCount() {
         return this.tableView.getItems() == null ? -1 : this.tableView.getItems().size();
      }

      @Override
      protected S getModelItem(int var1) {
         if (this.tableView.getItems() == null) {
            return null;
         } else {
            return (S)(var1 >= 0 && var1 < this.getItemCount() ? this.tableView.getItems().get(var1) : null);
         }
      }

      public final ReadOnlyObjectProperty<TablePosition> focusedCellProperty() {
         return this.focusedCellPropertyImpl().getReadOnlyProperty();
      }

      private void setFocusedCell(TablePosition var1) {
         this.focusedCellPropertyImpl().set(var1);
      }

      public final TablePosition getFocusedCell() {
         return this.focusedCell == null ? this.EMPTY_CELL : (TablePosition)this.focusedCell.get();
      }

      private ReadOnlyObjectWrapper<TablePosition> focusedCellPropertyImpl() {
         if (this.focusedCell == null) {
            this.focusedCell = new ReadOnlyObjectWrapper<TablePosition>(this.EMPTY_CELL) {
               private TablePosition old;

               protected void invalidated() {
                  if (this.get() != null) {
                     if (this.old == null || !this.old.equals(this.get())) {
                        TableViewFocusModel.this.setFocusedIndex(((TablePosition)this.get()).getRow());
                        TableViewFocusModel.this.setFocusedItem((S)TableViewFocusModel.this.getModelItem(((TablePosition)this.getValue()).getRow()));
                        this.old = (TablePosition)this.get();
                     }
                  }
               }

               public Object getBean() {
                  return TableViewFocusModel.this;
               }

               public String getName() {
                  return "focusedCell";
               }
            };
         }

         return this.focusedCell;
      }

      public void focus(int var1, TableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TablePosition var3 = this.getFocusedCell();
            TablePosition var4 = new TablePosition<>(this.tableView, var1, var2);
            this.setFocusedCell(var4);
            if (var4.equals(var3)) {
               this.setFocusedIndex(var1);
               this.setFocusedItem(this.getModelItem(var1));
            }
         } else {
            this.setFocusedCell(this.EMPTY_CELL);
         }
      }

      public void focus(TablePosition var1) {
         if (var1 != null) {
            this.focus(var1.getRow(), var1.getTableColumn());
         }
      }

      public boolean isFocused(int var1, TableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TablePosition var3 = this.getFocusedCell();
            boolean var4 = var2 == null || var2.equals(var3.getTableColumn());
            return var3.getRow() == var1 && var4;
         } else {
            return false;
         }
      }

      @Override
      public void focus(int var1) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            this.setFocusedCell(new TablePosition<>(this.tableView, var1, null));
         } else {
            this.setFocusedCell(this.EMPTY_CELL);
         }
      }

      @Override
      public void focusAboveCell() {
         TablePosition var1 = this.getFocusedCell();
         if (this.getFocusedIndex() == -1) {
            this.focus(this.getItemCount() - 1, var1.getTableColumn());
         } else if (this.getFocusedIndex() > 0) {
            this.focus(this.getFocusedIndex() - 1, var1.getTableColumn());
         }
      }

      @Override
      public void focusBelowCell() {
         TablePosition var1 = this.getFocusedCell();
         if (this.getFocusedIndex() == -1) {
            this.focus(0, var1.getTableColumn());
         } else if (this.getFocusedIndex() != this.getItemCount() - 1) {
            this.focus(this.getFocusedIndex() + 1, var1.getTableColumn());
         }
      }

      @Override
      public void focusLeftCell() {
         TablePosition var1 = this.getFocusedCell();
         if (var1.getColumn() > 0) {
            this.focus(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
         }
      }

      @Override
      public void focusRightCell() {
         TablePosition var1 = this.getFocusedCell();
         if (var1.getColumn() != this.getColumnCount() - 1) {
            this.focus(var1.getRow(), this.getTableColumn(var1.getTableColumn(), 1));
         }
      }

      @Override
      public void focusPrevious() {
         if (this.getFocusedIndex() == -1) {
            this.focus(0);
         } else if (this.getFocusedIndex() > 0) {
            this.focusAboveCell();
         }
      }

      @Override
      public void focusNext() {
         if (this.getFocusedIndex() == -1) {
            this.focus(0);
         } else if (this.getFocusedIndex() != this.getItemCount() - 1) {
            this.focusBelowCell();
         }
      }

      private void updateDefaultFocus() {
         int var1 = -1;
         if (this.tableView.getItems() != null) {
            Object var2 = this.getFocusedItem();
            if (var2 != null) {
               var1 = this.tableView.getItems().indexOf(var2);
            }

            if (var1 == -1) {
               var1 = this.tableView.getItems().size() > 0 ? 0 : -1;
            }
         }

         TablePosition var4 = this.getFocusedCell();
         TableColumn var3 = var4 != null && !this.EMPTY_CELL.equals(var4) ? var4.getTableColumn() : this.tableView.getVisibleLeafColumn(0);
         this.focus(var1, var3);
      }

      private int getColumnCount() {
         return this.tableView.getVisibleLeafColumns().size();
      }

      private TableColumn<S, ?> getTableColumn(TableColumn<S, ?> var1, int var2) {
         int var3 = this.tableView.getVisibleLeafIndex(var1);
         int var4 = var3 + var2;
         return this.tableView.getVisibleLeafColumn(var4);
      }
   }

   public abstract static class TableViewSelectionModel<S> extends TableSelectionModel<S> {
      private final TableView<S> tableView;
      boolean blockFocusCall = false;

      public TableViewSelectionModel(TableView<S> var1) {
         if (var1 == null) {
            throw new NullPointerException("TableView can not be null");
         } else {
            this.tableView = var1;
         }
      }

      public abstract ObservableList<TablePosition> getSelectedCells();

      @Override
      public boolean isSelected(int var1, TableColumnBase<S, ?> var2) {
         return this.isSelected(var1, (TableColumn<S, ?>)var2);
      }

      public abstract boolean isSelected(int var1, TableColumn<S, ?> var2);

      @Override
      public void select(int var1, TableColumnBase<S, ?> var2) {
         this.select(var1, (TableColumn<S, ?>)var2);
      }

      public abstract void select(int var1, TableColumn<S, ?> var2);

      @Override
      public void clearAndSelect(int var1, TableColumnBase<S, ?> var2) {
         this.clearAndSelect(var1, (TableColumn<S, ?>)var2);
      }

      public abstract void clearAndSelect(int var1, TableColumn<S, ?> var2);

      @Override
      public void clearSelection(int var1, TableColumnBase<S, ?> var2) {
         this.clearSelection(var1, (TableColumn<S, ?>)var2);
      }

      public abstract void clearSelection(int var1, TableColumn<S, ?> var2);

      @Override
      public void selectRange(int var1, TableColumnBase<S, ?> var2, int var3, TableColumnBase<S, ?> var4) {
         int var5 = this.tableView.getVisibleLeafIndex((TableColumn<S, ?>)var2);
         int var6 = this.tableView.getVisibleLeafIndex((TableColumn<S, ?>)var4);

         for (int var7 = var1; var7 <= var3; var7++) {
            for (int var8 = var5; var8 <= var6; var8++) {
               this.select(var7, this.tableView.getVisibleLeafColumn(var8));
            }
         }
      }

      public TableView<S> getTableView() {
         return this.tableView;
      }

      protected List<S> getTableModel() {
         return this.tableView.getItems();
      }

      @Override
      protected S getModelItem(int var1) {
         return (S)(var1 >= 0 && var1 < this.getItemCount() ? this.tableView.getItems().get(var1) : null);
      }

      @Override
      protected int getItemCount() {
         return this.getTableModel().size();
      }

      @Override
      public void focus(int var1) {
         this.focus(var1, null);
      }

      @Override
      public int getFocusedIndex() {
         return this.getFocusedCell().getRow();
      }

      void focus(int var1, TableColumn<S, ?> var2) {
         this.focus(new TablePosition<>(this.getTableView(), var1, var2));
         this.getTableView().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
      }

      void focus(TablePosition<S, ?> var1) {
         if (!this.blockFocusCall) {
            if (this.getTableView().getFocusModel() != null) {
               this.getTableView().getFocusModel().focus(var1.getRow(), var1.getTableColumn());
            }
         }
      }

      TablePosition<S, ?> getFocusedCell() {
         return this.getTableView().getFocusModel() == null
            ? new TablePosition<>(this.getTableView(), -1, null)
            : this.getTableView().getFocusModel().getFocusedCell();
      }
   }
}
