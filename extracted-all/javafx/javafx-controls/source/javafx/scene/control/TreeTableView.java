package javafx.scene.control;

import com.sun.javafx.collections.MappingChange;
import com.sun.javafx.collections.NonIterableChange;
import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange;
import com.sun.javafx.collections.NonIterableChange.SimpleAddChange;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import com.sun.javafx.scene.control.SelectedCellsMap;
import com.sun.javafx.scene.control.TableColumnComparatorBase;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import com.sun.javafx.scene.control.behavior.TreeTableCellBehavior;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.util.Callback;

@DefaultProperty("root")
public class TreeTableView<S> extends Control {
   private static final EventType<?> EDIT_ANY_EVENT = new EventType(Event.ANY, "TREE_TABLE_VIEW_EDIT");
   private static final EventType<?> EDIT_START_EVENT = new EventType(editAnyEvent(), "EDIT_START");
   private static final EventType<?> EDIT_CANCEL_EVENT = new EventType(editAnyEvent(), "EDIT_CANCEL");
   private static final EventType<?> EDIT_COMMIT_EVENT = new EventType(editAnyEvent(), "EDIT_COMMIT");
   public static final Callback<TreeTableView.ResizeFeatures, Boolean> UNCONSTRAINED_RESIZE_POLICY = new Callback<TreeTableView.ResizeFeatures, Boolean>() {
      @Override
      public String toString() {
         return "unconstrained-resize";
      }

      public Boolean call(TreeTableView.ResizeFeatures var1) {
         double var2 = TableUtil.resize(var1.getColumn(), var1.getDelta());
         return Double.compare(var2, 0.0) == 0;
      }
   };
   public static final Callback<TreeTableView.ResizeFeatures, Boolean> CONSTRAINED_RESIZE_POLICY = new Callback<TreeTableView.ResizeFeatures, Boolean>() {
      private boolean isFirstRun = true;

      @Override
      public String toString() {
         return "constrained-resize";
      }

      public Boolean call(TreeTableView.ResizeFeatures var1) {
         TreeTableView var2 = var1.getTable();
         ObservableList var3 = var2.getVisibleLeafColumns();
         Boolean var4 = TableUtil.constrainedResize(var1, this.isFirstRun, var2.contentWidth, var3);
         this.isFirstRun = !this.isFirstRun ? false : !var4;
         return var4;
      }
   };
   public static final Callback<TreeTableView, Boolean> DEFAULT_SORT_POLICY = new Callback<TreeTableView, Boolean>() {
      public Boolean call(TreeTableView var1) {
         try {
            TreeItem var2 = var1.getRoot();
            if (var2 != null && !var2.getChildren().isEmpty()) {
               TreeSortMode var3 = var1.getSortMode();
               if (var3 == null) {
                  return false;
               } else {
                  var2.lastSortMode = var3;
                  var2.lastComparator = var1.getComparator();
                  var2.sort();
                  return true;
               }
            } else {
               return false;
            }
         } catch (UnsupportedOperationException var4) {
            return false;
         }
      }
   };
   private boolean expandedItemCountDirty = true;
   private Map<Integer, SoftReference<TreeItem<S>>> treeItemCacheMap = new HashMap<>();
   private final ObservableList<TreeTableColumn<S, ?>> columns = FXCollections.observableArrayList();
   private final ObservableList<TreeTableColumn<S, ?>> visibleLeafColumns = FXCollections.observableArrayList();
   private final ObservableList<TreeTableColumn<S, ?>> unmodifiableVisibleLeafColumns = FXCollections.unmodifiableObservableList(this.visibleLeafColumns);
   private ObservableList<TreeTableColumn<S, ?>> sortOrder = FXCollections.observableArrayList();
   double contentWidth;
   private boolean isInited = false;
   private final EventHandler<TreeItem.TreeModificationEvent<S>> rootEvent = var1x -> {
      EventType var2 = var1x.getEventType();

      boolean var3;
      for (var3 = false; var2 != null; var2 = var2.getSuperType()) {
         if (var2.equals(TreeItem.expandedItemCountChangeEvent())) {
            var3 = true;
            break;
         }
      }

      if (var3) {
         this.expandedItemCountDirty = true;
         this.requestLayout();
      }
   };
   private final ListChangeListener<TreeTableColumn<S, ?>> columnsObserver = new ListChangeListener<TreeTableColumn<S, ?>>() {
      public void onChanged(Change<? extends TreeTableColumn<S, ?>> var1) {
         ObservableList var2 = TreeTableView.this.getColumns();

         while (var1.next()) {
            if (var1.wasAdded()) {
               ArrayList var3 = new ArrayList();

               for (TreeTableColumn var5 : var1.getAddedSubList()) {
                  if (var5 != null) {
                     int var6 = 0;

                     for (TreeTableColumn var8 : var2) {
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

                  for (TreeTableColumn var21 : var3) {
                     var15 = var15 + "'" + var21.getText() + "', ";
                  }

                  throw new IllegalStateException("Duplicate TreeTableColumns detected in TreeTableView columns list with titles " + var15);
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

               for (TreeTableColumn var25 : var16) {
                  var25.setTreeTableView(null);
               }
            }

            if (var1.wasAdded()) {
               var14.removeAll(var19);

               for (TreeTableColumn var26 : var19) {
                  var26.setTreeTableView(TreeTableView.this);
               }
            }

            TableUtil.removeColumnsListener(var16, TreeTableView.this.weakColumnsObserver);
            TableUtil.addColumnsListener(var19, TreeTableView.this.weakColumnsObserver);
            TableUtil.removeTableColumnListener(
               var1.getRemoved(),
               TreeTableView.this.weakColumnVisibleObserver,
               TreeTableView.this.weakColumnSortableObserver,
               TreeTableView.this.weakColumnSortTypeObserver,
               TreeTableView.this.weakColumnComparatorObserver
            );
            TableUtil.addTableColumnListener(
               var1.getAddedSubList(),
               TreeTableView.this.weakColumnVisibleObserver,
               TreeTableView.this.weakColumnSortableObserver,
               TreeTableView.this.weakColumnSortTypeObserver,
               TreeTableView.this.weakColumnComparatorObserver
            );
         }

         TreeTableView.this.updateVisibleLeafColumns();
         TreeTableView.this.sortOrder.removeAll(var14);
         TreeTableView.TreeTableViewFocusModel var17 = TreeTableView.this.getFocusModel();
         TreeTableView.TreeTableViewSelectionModel var20 = TreeTableView.this.getSelectionModel();
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
            TreeTablePosition var28 = var17.getFocusedCell();
            boolean var9 = false;

            for (TreeTableColumn var11 : var24) {
               var9 = var28 != null && var28.getTableColumn() == var11;
               if (var9) {
                  break;
               }
            }

            if (var9) {
               int var33 = TreeTableView.this.lastKnownColumnIndex.getOrDefault(var28.getTableColumn(), Integer.valueOf(0));
               int var36 = var33 == 0 ? 0 : Math.min(TreeTableView.this.getVisibleLeafColumns().size() - 1, var33 - 1);
               var17.focus(var28.getRow(), TreeTableView.this.getVisibleLeafColumn(var36));
            }
         }

         if (var20 != null) {
            for (TreeTablePosition var34 : new ArrayList(var20.getSelectedCells())) {
               boolean var37 = false;

               for (TreeTableColumn var13 : var24) {
                  var37 = var34 != null && var34.getTableColumn() == var13;
                  if (var37) {
                     break;
                  }
               }

               if (var37) {
                  int var38 = TreeTableView.this.lastKnownColumnIndex.getOrDefault(var34.getTableColumn(), Integer.valueOf(-1));
                  if (var38 != -1) {
                     if (var20 instanceof TreeTableView.TreeTableViewArrayListSelectionModel) {
                        TreeTablePosition var39 = new TreeTablePosition(TreeTableView.this, var34.getRow(), var34.getTableColumn());
                        var39.fixedColumnIndex = var38;
                        ((TreeTableView.TreeTableViewArrayListSelectionModel)var20).clearSelection(var39);
                     } else {
                        var20.clearSelection(var34.getRow(), var34.getTableColumn());
                     }
                  }
               }
            }
         }

         TreeTableView.this.lastKnownColumnIndex.clear();

         for (TreeTableColumn var32 : TreeTableView.this.getColumns()) {
            int var35 = TreeTableView.this.getVisibleLeafIndex(var32);
            if (var35 > -1) {
               TreeTableView.this.lastKnownColumnIndex.put(var32, var35);
            }
         }
      }
   };
   private final WeakHashMap<TreeTableColumn<S, ?>, Integer> lastKnownColumnIndex = new WeakHashMap<>();
   private final InvalidationListener columnVisibleObserver = var1x -> this.updateVisibleLeafColumns();
   private final InvalidationListener columnSortableObserver = var1x -> {
      TreeTableColumn var2 = (TreeTableColumn)((BooleanProperty)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_SORTABLE_CHANGE, var2);
      }
   };
   private final InvalidationListener columnSortTypeObserver = var1x -> {
      TreeTableColumn var2 = (TreeTableColumn)((ObjectProperty)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_SORT_TYPE_CHANGE, var2);
      }
   };
   private final InvalidationListener columnComparatorObserver = var1x -> {
      TreeTableColumn var2 = (TreeTableColumn)((SimpleObjectProperty)var1x).getBean();
      if (this.getSortOrder().contains(var2)) {
         this.doSort(TableUtil.SortEventType.COLUMN_COMPARATOR_CHANGE, var2);
      }
   };
   private final InvalidationListener cellSelectionModelInvalidationListener = var1x -> {
      boolean var2 = ((BooleanProperty)var1x).get();
      this.pseudoClassStateChanged(PSEUDO_CLASS_CELL_SELECTION, var2);
      this.pseudoClassStateChanged(PSEUDO_CLASS_ROW_SELECTION, !var2);
   };
   private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakRootEventListener;
   private final WeakInvalidationListener weakColumnVisibleObserver = new WeakInvalidationListener(this.columnVisibleObserver);
   private final WeakInvalidationListener weakColumnSortableObserver = new WeakInvalidationListener(this.columnSortableObserver);
   private final WeakInvalidationListener weakColumnSortTypeObserver = new WeakInvalidationListener(this.columnSortTypeObserver);
   private final WeakInvalidationListener weakColumnComparatorObserver = new WeakInvalidationListener(this.columnComparatorObserver);
   private final WeakListChangeListener<TreeTableColumn<S, ?>> weakColumnsObserver = new WeakListChangeListener(this.columnsObserver);
   private final WeakInvalidationListener weakCellSelectionModelInvalidationListener = new WeakInvalidationListener(this.cellSelectionModelInvalidationListener);
   private ObjectProperty<TreeItem<S>> root = new SimpleObjectProperty<TreeItem<S>>(this, "root") {
      private WeakReference<TreeItem<S>> weakOldItem;

      protected void invalidated() {
         TreeItem var1 = this.weakOldItem == null ? null : this.weakOldItem.get();
         if (var1 != null && TreeTableView.this.weakRootEventListener != null) {
            var1.removeEventHandler(TreeItem.treeNotificationEvent(), TreeTableView.this.weakRootEventListener);
         }

         TreeItem var2 = TreeTableView.this.getRoot();
         if (var2 != null) {
            TreeTableView.this.weakRootEventListener = new WeakEventHandler(TreeTableView.this.rootEvent);
            TreeTableView.this.getRoot().addEventHandler(TreeItem.treeNotificationEvent(), TreeTableView.this.weakRootEventListener);
            this.weakOldItem = new WeakReference<>(var2);
         }

         TreeTableView.this.getSortOrder().clear();
         TreeTableView.this.expandedItemCountDirty = true;
         TreeTableView.this.updateRootExpanded();
      }
   };
   private BooleanProperty showRoot;
   private ObjectProperty<TreeTableColumn<S, ?>> treeColumn;
   private ObjectProperty<TreeTableView.TreeTableViewSelectionModel<S>> selectionModel;
   private ObjectProperty<TreeTableView.TreeTableViewFocusModel<S>> focusModel;
   private ReadOnlyIntegerWrapper expandedItemCount = new ReadOnlyIntegerWrapper(this, "expandedItemCount", 0);
   private BooleanProperty editable;
   private ReadOnlyObjectWrapper<TreeTablePosition<S, ?>> editingCell;
   private BooleanProperty tableMenuButtonVisible;
   private ObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>> columnResizePolicy;
   private ObjectProperty<Callback<TreeTableView<S>, TreeTableRow<S>>> rowFactory;
   private ObjectProperty<Node> placeholder;
   private DoubleProperty fixedCellSize;
   private ObjectProperty<TreeSortMode> sortMode;
   private ReadOnlyObjectWrapper<Comparator<TreeItem<S>>> comparator;
   private ObjectProperty<Callback<TreeTableView<S>, Boolean>> sortPolicy;
   private ObjectProperty<EventHandler<SortEvent<TreeTableView<S>>>> onSort;
   private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
   private ObjectProperty<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>> onScrollToColumn;
   private boolean sortingInProgress;
   private boolean sortLock = false;
   private TableUtil.SortEventType lastSortEventType = null;
   private Object[] lastSortEventSupportInfo = null;
   private static final String DEFAULT_STYLE_CLASS = "tree-table-view";
   private static final PseudoClass PSEUDO_CLASS_CELL_SELECTION = PseudoClass.getPseudoClass("cell-selection");
   private static final PseudoClass PSEUDO_CLASS_ROW_SELECTION = PseudoClass.getPseudoClass("row-selection");

   public TreeTableView() {
      this(null);
   }

   public TreeTableView(TreeItem<S> var1) {
      this.getStyleClass().setAll(new String[]{"tree-table-view"});
      this.setAccessibleRole(AccessibleRole.TREE_TABLE_VIEW);
      this.setRoot(var1);
      this.updateExpandedItemCount(var1);
      this.setSelectionModel(new TreeTableView.TreeTableViewArrayListSelectionModel<>(this));
      this.setFocusModel(new TreeTableView.TreeTableViewFocusModel<>(this));
      this.getColumns().addListener(this.weakColumnsObserver);
      this.getSortOrder().addListener(var1x -> this.doSort(TableUtil.SortEventType.SORT_ORDER_CHANGE, var1x));
      this.getProperties().addListener(var1x -> {
         if (var1x.wasAdded() && "TableView.contentWidth".equals(var1x.getKey())) {
            if (var1x.getValueAdded() instanceof Number) {
               this.setContentWidth((Double)var1x.getValueAdded());
            }

            this.getProperties().remove("TableView.contentWidth");
         }
      });
      this.pseudoClassStateChanged(PseudoClass.getPseudoClass(this.getColumnResizePolicy().toString()), true);
      this.isInited = true;
   }

   public static <S> EventType<TreeTableView.EditEvent<S>> editAnyEvent() {
      return (EventType<TreeTableView.EditEvent<S>>)EDIT_ANY_EVENT;
   }

   public static <S> EventType<TreeTableView.EditEvent<S>> editStartEvent() {
      return (EventType<TreeTableView.EditEvent<S>>)EDIT_START_EVENT;
   }

   public static <S> EventType<TreeTableView.EditEvent<S>> editCancelEvent() {
      return (EventType<TreeTableView.EditEvent<S>>)EDIT_CANCEL_EVENT;
   }

   public static <S> EventType<TreeTableView.EditEvent<S>> editCommitEvent() {
      return (EventType<TreeTableView.EditEvent<S>>)EDIT_COMMIT_EVENT;
   }

   @Deprecated(
      since = "8u20"
   )
   public static int getNodeLevel(TreeItem<?> var0) {
      return TreeView.getNodeLevel(var0);
   }

   public final void setRoot(TreeItem<S> var1) {
      this.rootProperty().set(var1);
   }

   public final TreeItem<S> getRoot() {
      return this.root == null ? null : (TreeItem)this.root.get();
   }

   public final ObjectProperty<TreeItem<S>> rootProperty() {
      return this.root;
   }

   public final void setShowRoot(boolean var1) {
      this.showRootProperty().set(var1);
   }

   public final boolean isShowRoot() {
      return this.showRoot == null ? true : this.showRoot.get();
   }

   public final BooleanProperty showRootProperty() {
      if (this.showRoot == null) {
         this.showRoot = new SimpleBooleanProperty(this, "showRoot", true) {
            protected void invalidated() {
               TreeTableView.this.updateRootExpanded();
               TreeTableView.this.updateExpandedItemCount(TreeTableView.this.getRoot());
            }
         };
      }

      return this.showRoot;
   }

   public final ObjectProperty<TreeTableColumn<S, ?>> treeColumnProperty() {
      if (this.treeColumn == null) {
         this.treeColumn = new SimpleObjectProperty(this, "treeColumn", null);
      }

      return this.treeColumn;
   }

   public final void setTreeColumn(TreeTableColumn<S, ?> var1) {
      this.treeColumnProperty().set(var1);
   }

   public final TreeTableColumn<S, ?> getTreeColumn() {
      return this.treeColumn == null ? null : (TreeTableColumn)this.treeColumn.get();
   }

   public final void setSelectionModel(TreeTableView.TreeTableViewSelectionModel<S> var1) {
      this.selectionModelProperty().set(var1);
   }

   public final TreeTableView.TreeTableViewSelectionModel<S> getSelectionModel() {
      return this.selectionModel == null ? null : (TreeTableView.TreeTableViewSelectionModel)this.selectionModel.get();
   }

   public final ObjectProperty<TreeTableView.TreeTableViewSelectionModel<S>> selectionModelProperty() {
      if (this.selectionModel == null) {
         this.selectionModel = new SimpleObjectProperty<TreeTableView.TreeTableViewSelectionModel<S>>(this, "selectionModel") {
            TreeTableView.TreeTableViewSelectionModel<S> oldValue = null;

            protected void invalidated() {
               if (this.oldValue != null) {
                  this.oldValue.cellSelectionEnabledProperty().removeListener(TreeTableView.this.weakCellSelectionModelInvalidationListener);
                  if (this.oldValue instanceof TreeTableView.TreeTableViewArrayListSelectionModel) {
                     ((TreeTableView.TreeTableViewArrayListSelectionModel)this.oldValue).dispose();
                  }
               }

               this.oldValue = (TreeTableView.TreeTableViewSelectionModel<S>)this.get();
               if (this.oldValue != null) {
                  this.oldValue.cellSelectionEnabledProperty().addListener(TreeTableView.this.weakCellSelectionModelInvalidationListener);
                  TreeTableView.this.weakCellSelectionModelInvalidationListener.invalidated(this.oldValue.cellSelectionEnabledProperty());
               }
            }
         };
      }

      return this.selectionModel;
   }

   public final void setFocusModel(TreeTableView.TreeTableViewFocusModel<S> var1) {
      this.focusModelProperty().set(var1);
   }

   public final TreeTableView.TreeTableViewFocusModel<S> getFocusModel() {
      return this.focusModel == null ? null : (TreeTableView.TreeTableViewFocusModel)this.focusModel.get();
   }

   public final ObjectProperty<TreeTableView.TreeTableViewFocusModel<S>> focusModelProperty() {
      if (this.focusModel == null) {
         this.focusModel = new SimpleObjectProperty(this, "focusModel");
      }

      return this.focusModel;
   }

   public final ReadOnlyIntegerProperty expandedItemCountProperty() {
      return this.expandedItemCount.getReadOnlyProperty();
   }

   private void setExpandedItemCount(int var1) {
      this.expandedItemCount.set(var1);
   }

   public final int getExpandedItemCount() {
      if (this.expandedItemCountDirty) {
         this.updateExpandedItemCount(this.getRoot());
      }

      return this.expandedItemCount.get();
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

   private void setEditingCell(TreeTablePosition<S, ?> var1) {
      this.editingCellPropertyImpl().set(var1);
   }

   public final TreeTablePosition<S, ?> getEditingCell() {
      return this.editingCell == null ? null : (TreeTablePosition)this.editingCell.get();
   }

   public final ReadOnlyObjectProperty<TreeTablePosition<S, ?>> editingCellProperty() {
      return this.editingCellPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<TreeTablePosition<S, ?>> editingCellPropertyImpl() {
      if (this.editingCell == null) {
         this.editingCell = new ReadOnlyObjectWrapper(this, "editingCell");
      }

      return this.editingCell;
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

   public final void setColumnResizePolicy(Callback<TreeTableView.ResizeFeatures, Boolean> var1) {
      this.columnResizePolicyProperty().set(var1);
   }

   public final Callback<TreeTableView.ResizeFeatures, Boolean> getColumnResizePolicy() {
      return this.columnResizePolicy == null ? UNCONSTRAINED_RESIZE_POLICY : (Callback)this.columnResizePolicy.get();
   }

   public final ObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>> columnResizePolicyProperty() {
      if (this.columnResizePolicy == null) {
         this.columnResizePolicy = new SimpleObjectProperty<Callback<TreeTableView.ResizeFeatures, Boolean>>(
            this, "columnResizePolicy", UNCONSTRAINED_RESIZE_POLICY
         ) {
            private Callback<TreeTableView.ResizeFeatures, Boolean> oldPolicy;

            protected void invalidated() {
               if (TreeTableView.this.isInited) {
                  ((Callback)this.get()).call(new TreeTableView.ResizeFeatures(TreeTableView.this, null, 0.0));
                  if (this.oldPolicy != null) {
                     PseudoClass var1 = PseudoClass.getPseudoClass(this.oldPolicy.toString());
                     TreeTableView.this.pseudoClassStateChanged(var1, false);
                  }

                  if (this.get() != null) {
                     PseudoClass var2 = PseudoClass.getPseudoClass(((Callback)this.get()).toString());
                     TreeTableView.this.pseudoClassStateChanged(var2, true);
                  }

                  this.oldPolicy = (Callback<TreeTableView.ResizeFeatures, Boolean>)this.get();
               }
            }
         };
      }

      return this.columnResizePolicy;
   }

   public final ObjectProperty<Callback<TreeTableView<S>, TreeTableRow<S>>> rowFactoryProperty() {
      if (this.rowFactory == null) {
         this.rowFactory = new SimpleObjectProperty(this, "rowFactory");
      }

      return this.rowFactory;
   }

   public final void setRowFactory(Callback<TreeTableView<S>, TreeTableRow<S>> var1) {
      this.rowFactoryProperty().set(var1);
   }

   public final Callback<TreeTableView<S>, TreeTableRow<S>> getRowFactory() {
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

   public final void setFixedCellSize(double var1) {
      this.fixedCellSizeProperty().set(var1);
   }

   public final double getFixedCellSize() {
      return this.fixedCellSize == null ? -1.0 : this.fixedCellSize.get();
   }

   public final DoubleProperty fixedCellSizeProperty() {
      if (this.fixedCellSize == null) {
         this.fixedCellSize = new StyleableDoubleProperty(-1.0) {
            public CssMetaData<TreeTableView<?>, Number> getCssMetaData() {
               return TreeTableView.StyleableProperties.FIXED_CELL_SIZE;
            }

            public Object getBean() {
               return TreeTableView.this;
            }

            public String getName() {
               return "fixedCellSize";
            }
         };
      }

      return this.fixedCellSize;
   }

   public final ObjectProperty<TreeSortMode> sortModeProperty() {
      if (this.sortMode == null) {
         this.sortMode = new SimpleObjectProperty(this, "sortMode", TreeSortMode.ALL_DESCENDANTS);
      }

      return this.sortMode;
   }

   public final void setSortMode(TreeSortMode var1) {
      this.sortModeProperty().set(var1);
   }

   public final TreeSortMode getSortMode() {
      return this.sortMode == null ? TreeSortMode.ALL_DESCENDANTS : (TreeSortMode)this.sortMode.get();
   }

   private void setComparator(Comparator<TreeItem<S>> var1) {
      this.comparatorPropertyImpl().set(var1);
   }

   public final Comparator<TreeItem<S>> getComparator() {
      return this.comparator == null ? null : (Comparator)this.comparator.get();
   }

   public final ReadOnlyObjectProperty<Comparator<TreeItem<S>>> comparatorProperty() {
      return this.comparatorPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<Comparator<TreeItem<S>>> comparatorPropertyImpl() {
      if (this.comparator == null) {
         this.comparator = new ReadOnlyObjectWrapper(this, "comparator");
      }

      return this.comparator;
   }

   public final void setSortPolicy(Callback<TreeTableView<S>, Boolean> var1) {
      this.sortPolicyProperty().set(var1);
   }

   public final Callback<TreeTableView<S>, Boolean> getSortPolicy() {
      return this.sortPolicy == null ? DEFAULT_SORT_POLICY : (Callback)this.sortPolicy.get();
   }

   public final ObjectProperty<Callback<TreeTableView<S>, Boolean>> sortPolicyProperty() {
      if (this.sortPolicy == null) {
         this.sortPolicy = new SimpleObjectProperty<Callback<TreeTableView<S>, Boolean>>(this, "sortPolicy", DEFAULT_SORT_POLICY) {
            protected void invalidated() {
               TreeTableView.this.sort();
            }
         };
      }

      return this.sortPolicy;
   }

   public void setOnSort(EventHandler<SortEvent<TreeTableView<S>>> var1) {
      this.onSortProperty().set(var1);
   }

   public EventHandler<SortEvent<TreeTableView<S>>> getOnSort() {
      return this.onSort != null ? (EventHandler)this.onSort.get() : null;
   }

   public ObjectProperty<EventHandler<SortEvent<TreeTableView<S>>>> onSortProperty() {
      if (this.onSort == null) {
         this.onSort = new ObjectPropertyBase<EventHandler<SortEvent<TreeTableView<S>>>>() {
            protected void invalidated() {
               EventType var1 = SortEvent.sortEvent();
               EventHandler var2 = (EventHandler)this.get();
               TreeTableView.this.setEventHandler(var1, var2);
            }

            public Object getBean() {
               return TreeTableView.this;
            }

            public String getName() {
               return "onSort";
            }
         };
      }

      return this.onSort;
   }

   @Override
   protected void layoutChildren() {
      if (this.expandedItemCountDirty) {
         this.updateExpandedItemCount(this.getRoot());
      }

      super.layoutChildren();
   }

   public void scrollTo(int var1) {
      ControlUtils.scrollToIndex(this, var1);
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
               TreeTableView.this.setEventHandler(ScrollToEvent.scrollToTopIndex(), (EventHandler)this.get());
            }

            public Object getBean() {
               return TreeTableView.this;
            }

            public String getName() {
               return "onScrollTo";
            }
         };
      }

      return this.onScrollTo;
   }

   public void scrollToColumn(TreeTableColumn<S, ?> var1) {
      ControlUtils.scrollToColumn(this, var1);
   }

   public void scrollToColumnIndex(int var1) {
      if (this.getColumns() != null) {
         ControlUtils.scrollToColumn(this, (TableColumnBase<?, ?>)this.getColumns().get(var1));
      }
   }

   public void setOnScrollToColumn(EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>> var1) {
      this.onScrollToColumnProperty().set(var1);
   }

   public EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>> getOnScrollToColumn() {
      return this.onScrollToColumn != null ? (EventHandler)this.onScrollToColumn.get() : null;
   }

   public ObjectProperty<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>> onScrollToColumnProperty() {
      if (this.onScrollToColumn == null) {
         this.onScrollToColumn = new ObjectPropertyBase<EventHandler<ScrollToEvent<TreeTableColumn<S, ?>>>>() {
            protected void invalidated() {
               EventType var1 = ScrollToEvent.scrollToColumn();
               TreeTableView.this.setEventHandler(var1, (EventHandler)this.get());
            }

            public Object getBean() {
               return TreeTableView.this;
            }

            public String getName() {
               return "onScrollToColumn";
            }
         };
      }

      return this.onScrollToColumn;
   }

   public int getRow(TreeItem<S> var1) {
      return TreeUtil.getRow(var1, this.getRoot(), this.expandedItemCountDirty, this.isShowRoot());
   }

   public TreeItem<S> getTreeItem(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         int var2 = this.isShowRoot() ? var1 : var1 + 1;
         if (this.expandedItemCountDirty) {
            this.updateExpandedItemCount(this.getRoot());
         } else if (this.treeItemCacheMap.containsKey(var2)) {
            SoftReference var3 = this.treeItemCacheMap.get(var2);
            TreeItem var4 = (TreeItem)var3.get();
            if (var4 != null) {
               return var4;
            }
         }

         TreeItem var5 = TreeUtil.getItem(this.getRoot(), var2, this.expandedItemCountDirty);
         this.treeItemCacheMap.put(var2, new SoftReference<>(var5));
         return var5;
      }
   }

   public int getTreeItemLevel(TreeItem<?> var1) {
      TreeItem var2 = this.getRoot();
      if (var1 == null) {
         return -1;
      } else if (var1 == var2) {
         return 0;
      } else {
         int var3 = 0;

         for (TreeItem var4 = var1.getParent(); var4 != null; var4 = var4.getParent()) {
            var3++;
            if (var4 == var2) {
               break;
            }
         }

         return var3;
      }
   }

   public final ObservableList<TreeTableColumn<S, ?>> getColumns() {
      return this.columns;
   }

   public final ObservableList<TreeTableColumn<S, ?>> getSortOrder() {
      return this.sortOrder;
   }

   public boolean resizeColumn(TreeTableColumn<S, ?> var1, double var2) {
      return var1 != null && Double.compare(var2, 0.0) != 0
         ? (Boolean)this.getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<>(this, var1, var2))
         : false;
   }

   public void edit(int var1, TreeTableColumn<S, ?> var2) {
      if (this.isEditable() && (var2 == null || var2.isEditable())) {
         if (var1 < 0 && var2 == null) {
            this.setEditingCell(null);
         } else {
            this.setEditingCell(new TreeTablePosition<>(this, var1, var2));
         }
      }
   }

   public ObservableList<TreeTableColumn<S, ?>> getVisibleLeafColumns() {
      return this.unmodifiableVisibleLeafColumns;
   }

   public int getVisibleLeafIndex(TreeTableColumn<S, ?> var1) {
      return this.getVisibleLeafColumns().indexOf(var1);
   }

   public TreeTableColumn<S, ?> getVisibleLeafColumn(int var1) {
      return var1 >= 0 && var1 < this.visibleLeafColumns.size() ? (TreeTableColumn)this.visibleLeafColumns.get(var1) : null;
   }

   boolean isSortingInProgress() {
      return this.sortingInProgress;
   }

   public void sort() {
      this.sortingInProgress = true;
      ObservableList var1 = this.getSortOrder();
      Comparator var2 = this.getComparator();
      this.setComparator(var1.isEmpty() ? null : new TableColumnComparatorBase.TreeTableColumnComparator<>(var1));
      SortEvent var3 = new SortEvent<>(this, this);
      this.fireEvent(var3);
      if (var3.isConsumed()) {
         this.sortingInProgress = false;
      } else {
         ArrayList var4 = new ArrayList(this.getSelectionModel().getSelectedCells());
         int var5 = var4.size();
         this.getSelectionModel().startAtomic();
         Callback var6 = this.getSortPolicy();
         if (var6 == null) {
            this.sortingInProgress = false;
         } else {
            Boolean var7 = (Boolean)var6.call(this);
            if (this.getSortMode() == TreeSortMode.ALL_DESCENDANTS) {
               HashSet var8 = new HashSet();

               for (TreeTablePosition var10 : var4) {
                  if (var10.getTreeItem() != null) {
                     for (TreeItem var11 = var10.getTreeItem().getParent(); var11 != null && var8.add(var11); var11 = var11.getParent()) {
                        var11.getChildren();
                     }
                  }
               }
            }

            this.getSelectionModel().stopAtomic();
            if (var7 != null && var7) {
               if (this.getSelectionModel() instanceof TreeTableView.TreeTableViewArrayListSelectionModel) {
                  TreeTableView.TreeTableViewArrayListSelectionModel var13 = (TreeTableView.TreeTableViewArrayListSelectionModel)this.getSelectionModel();
                  ObservableList var14 = var13.getSelectedCells();
                  ArrayList var15 = new ArrayList();

                  for (int var16 = 0; var16 < var5; var16++) {
                     TreeTablePosition var12 = (TreeTablePosition)var4.get(var16);
                     if (!var14.contains(var12)) {
                        var15.add(var12);
                     }
                  }

                  if (!var15.isEmpty()) {
                     GenericAddRemoveChange var17 = new GenericAddRemoveChange(0, var5, var15, var14);
                     var13.fireCustomSelectedCellsListChangeEvent(var17);
                  }
               }

               this.getSelectionModel().setSelectedIndex(this.getRow(this.getSelectionModel().getSelectedItem()));
               this.getFocusModel().focus(this.getSelectionModel().getSelectedIndex());
            } else {
               this.sortLock = true;
               TableUtil.handleSortFailure(var1, this.lastSortEventType, this.lastSortEventSupportInfo);
               this.setComparator(var2);
               this.sortLock = false;
            }

            this.sortingInProgress = false;
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

   private void updateExpandedItemCount(TreeItem<S> var1) {
      this.setExpandedItemCount(TreeUtil.updateExpandedItemCount(var1, this.expandedItemCountDirty, this.isShowRoot()));
      if (this.expandedItemCountDirty) {
         this.treeItemCacheMap.clear();
      }

      this.expandedItemCountDirty = false;
   }

   private void updateRootExpanded() {
      if (!this.isShowRoot() && this.getRoot() != null && !this.getRoot().isExpanded()) {
         this.getRoot().setExpanded(true);
      }
   }

   private void setContentWidth(double var1) {
      this.contentWidth = var1;
      if (this.isInited) {
         this.getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<>(this, null, 0.0));
      }
   }

   private void updateVisibleLeafColumns() {
      ArrayList var1 = new ArrayList();
      this.buildVisibleLeafColumns(this.getColumns(), var1);
      this.visibleLeafColumns.setAll(var1);
      this.getColumnResizePolicy().call(new TreeTableView.ResizeFeatures<>(this, null, 0.0));
   }

   private void buildVisibleLeafColumns(List<TreeTableColumn<S, ?>> var1, List<TreeTableColumn<S, ?>> var2) {
      for (TreeTableColumn var4 : var1) {
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
      return TreeTableView.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TreeTableViewSkin<>(this);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case ROW_COUNT:
            return this.getExpandedItemCount();
         case COLUMN_COUNT:
            return this.getVisibleLeafColumns().size();
         case SELECTED_ITEMS:
            ObservableList var10 = (ObservableList)super.queryAccessibleAttribute(var1, var2);
            ArrayList var11 = new ArrayList();

            for (TreeTableRow var6 : var10) {
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
            TreeTableRow var8 = (TreeTableRow)super.queryAccessibleAttribute(var1, var2);
            return var8 != null ? var8.queryAccessibleAttribute(var1, var2) : null;
         case MULTIPLE_SELECTION:
            TreeTableView.TreeTableViewSelectionModel var3 = this.getSelectionModel();
            return var3 != null && var3.getSelectionMode() == SelectionMode.MULTIPLE;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   public static class EditEvent<S> extends Event {
      private static final long serialVersionUID = -4437033058917528976L;
      public static final EventType<?> ANY = TreeTableView.EDIT_ANY_EVENT;
      private final TreeTableView<S> source;
      private final S oldValue;
      private final S newValue;
      private final transient TreeItem<S> treeItem;

      public EditEvent(TreeTableView<S> var1, EventType<? extends TreeTableView.EditEvent> var2, TreeItem<S> var3, S var4, S var5) {
         super(var1, Event.NULL_SOURCE_TARGET, var2);
         this.source = var1;
         this.oldValue = (S)var4;
         this.newValue = (S)var5;
         this.treeItem = var3;
      }

      public TreeTableView<S> getSource() {
         return this.source;
      }

      public TreeItem<S> getTreeItem() {
         return this.treeItem;
      }

      public S getNewValue() {
         return this.newValue;
      }

      public S getOldValue() {
         return this.oldValue;
      }
   }

   public static class ResizeFeatures<S> extends ResizeFeaturesBase<TreeItem<S>> {
      private TreeTableView<S> treeTable;

      public ResizeFeatures(TreeTableView<S> var1, TreeTableColumn<S, ?> var2, Double var3) {
         super(var2, var3);
         this.treeTable = var1;
      }

      public TreeTableColumn<S, ?> getColumn() {
         return (TreeTableColumn<S, ?>)super.getColumn();
      }

      public TreeTableView<S> getTable() {
         return this.treeTable;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TreeTableView<?>, Number> FIXED_CELL_SIZE = new CssMetaData<TreeTableView<?>, Number>(
         "-fx-fixed-cell-size", SizeConverter.getInstance(), -1.0
      ) {
         public Double getInitialValue(TreeTableView<?> var1) {
            return var1.getFixedCellSize();
         }

         public boolean isSettable(TreeTableView<?> var1) {
            return var1.fixedCellSize == null || !var1.fixedCellSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TreeTableView<?> var1) {
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

   static class TreeTableViewArrayListSelectionModel<S> extends TreeTableView.TreeTableViewSelectionModel<S> {
      private final com.sun.javafx.collections.MappingChange.Map<TreeTablePosition<S, ?>, Integer> cellToIndicesMap = var0 -> var0.getRow();
      private TreeTableView<S> treeTableView = null;
      private ChangeListener<TreeItem<S>> rootPropertyListener = (var1x, var2, var3) -> {
         this.updateDefaultSelection();
         this.updateTreeEventListener(var2, var3);
      };
      private InvalidationListener showRootPropertyListener = var1x -> this.shiftSelection(0, this.treeTableView.isShowRoot() ? 1 : -1, null);
      private EventHandler<TreeItem.TreeModificationEvent<S>> treeItemListener = new EventHandler<TreeItem.TreeModificationEvent<S>>() {
         public void handle(TreeItem.TreeModificationEvent<S> var1) {
            if (TreeTableViewArrayListSelectionModel.this.getSelectedIndex() != -1 || TreeTableViewArrayListSelectionModel.this.getSelectedItem() != null) {
               TreeItem var2 = var1.getTreeItem();
               if (var2 != null) {
                  int var3 = TreeTableViewArrayListSelectionModel.this.getSelectedIndex();
                  TreeTableViewArrayListSelectionModel.this.treeTableView.expandedItemCountDirty = true;
                  int var4 = TreeTableViewArrayListSelectionModel.this.treeTableView.getRow(var2);
                  int var5 = 0;
                  Change var6 = var1.getChange();
                  if (var6 != null) {
                     var6.next();
                  }

                  do {
                     int var7 = var6 == null ? 0 : var6.getAddedSize();
                     int var8 = var6 == null ? 0 : var6.getRemovedSize();
                     if (var1.wasExpanded()) {
                        var5 += var2.getExpandedDescendentCount(false) - 1;
                        var4++;
                     } else if (var1.wasCollapsed()) {
                        var2.getExpandedDescendentCount(false);
                        int var24 = var2.previousExpandedDescendentCount;
                        int var27 = TreeTableViewArrayListSelectionModel.this.getSelectedIndex();
                        boolean var30 = var27 >= var4 + 1 && var27 < var4 + var24;
                        boolean var33 = false;
                        boolean var35 = TreeTableViewArrayListSelectionModel.this.isCellSelectionEnabled();
                        ObservableList var37 = TreeTableViewArrayListSelectionModel.this.getTreeTableView().getVisibleLeafColumns();
                        TreeTableViewArrayListSelectionModel.this.selectedIndices._beginChange();
                        int var38 = var4 + 1;
                        int var39 = var4 + var24;
                        ArrayList var17 = new ArrayList();
                        TreeTableColumn var18 = null;

                        for (int var19 = var38; var19 < var39; var19++) {
                           if (var35) {
                              for (int var20 = 0; var20 < var37.size(); var20++) {
                                 TreeTableColumn var21 = (TreeTableColumn)var37.get(var20);
                                 if (TreeTableViewArrayListSelectionModel.this.isSelected(var19, var21)) {
                                    var33 = true;
                                    TreeTableViewArrayListSelectionModel.this.clearSelection(var19, var21);
                                    var18 = var21;
                                 }
                              }
                           } else if (TreeTableViewArrayListSelectionModel.this.isSelected(var19)) {
                              var33 = true;
                              var17.add(var19);
                           }
                        }

                        if (!var17.isEmpty()) {
                           TreeTableViewArrayListSelectionModel.this.selectedIndices
                              ._nextRemove(TreeTableViewArrayListSelectionModel.this.selectedIndices.indexOf(var17.get(0)), var17);
                        }

                        for (int var41 : var17) {
                           TreeTableViewArrayListSelectionModel.this.startAtomic();
                           TreeTableViewArrayListSelectionModel.this.clearSelection(
                              new TreeTablePosition<>(TreeTableViewArrayListSelectionModel.this.treeTableView, var41, null, false)
                           );
                           TreeTableViewArrayListSelectionModel.this.stopAtomic();
                        }

                        TreeTableViewArrayListSelectionModel.this.selectedIndices._endChange();
                        if (var30 && var33) {
                           TreeTableViewArrayListSelectionModel.this.select(var4, var18);
                        }

                        var5 += -var24 + 1;
                        var4++;
                     } else if (!var1.wasPermutated()) {
                        if (var1.wasAdded()) {
                           var5 += var2.isExpanded() ? var7 : 0;
                           var4 = TreeTableViewArrayListSelectionModel.this.treeTableView.getRow((TreeItem<S>)var1.getChange().getAddedSubList().get(0));
                           TreeTablePosition var23 = (TreeTablePosition)TreeTableCellBehavior.<TreeTablePosition>getAnchor(
                              TreeTableViewArrayListSelectionModel.this.treeTableView, null
                           );
                           if (var23 != null) {
                              boolean var26 = TreeTableViewArrayListSelectionModel.this.isSelected(var23.getRow(), var23.getTableColumn());
                              if (var26) {
                                 TreeTablePosition var29 = new TreeTablePosition<>(
                                    TreeTableViewArrayListSelectionModel.this.treeTableView, var23.getRow() + var5, var23.getTableColumn()
                                 );
                                 TreeTableCellBehavior.setAnchor(TreeTableViewArrayListSelectionModel.this.treeTableView, var29, false);
                              }
                           }
                        } else if (var1.wasRemoved()) {
                           var5 += var2.isExpanded() ? -var8 : 0;
                           var4 += var1.getFrom() + 1;
                           ObservableList var22 = TreeTableViewArrayListSelectionModel.this.getSelectedIndices();
                           ObservableList var25 = TreeTableViewArrayListSelectionModel.this.getSelectedItems();
                           TreeItem var28 = TreeTableViewArrayListSelectionModel.this.getSelectedItem();
                           List var32 = var1.getChange().getRemoved();

                           for (int var34 = 0; var34 < var22.size() && !var25.isEmpty(); var34++) {
                              int var36 = (Integer)var22.get(var34);
                              if (var36 > var25.size()) {
                                 break;
                              }

                              if (var32.size() == 1
                                 && var25.size() == 1
                                 && var28 != null
                                 && var28.equals(var32.get(0))
                                 && var3 < TreeTableViewArrayListSelectionModel.this.getItemCount()) {
                                 int var15 = var3 == 0 ? 0 : var3 - 1;
                                 TreeItem var16 = TreeTableViewArrayListSelectionModel.this.getModelItem(var15);
                                 if (!var28.equals(var16)) {
                                    TreeTableViewArrayListSelectionModel.this.clearAndSelect(var15);
                                 }
                              }
                           }
                        }
                     } else {
                        ArrayList var9 = new ArrayList(TreeTableViewArrayListSelectionModel.this.selectedCellsMap.getSelectedCells());
                        ArrayList var10 = new ArrayList();
                        boolean var11 = false;

                        for (TreeTablePosition var13 : var9) {
                           int var14 = TreeTableViewArrayListSelectionModel.this.treeTableView.getRow(var13.getTreeItem());
                           if (var13.getRow() != var14) {
                              var11 = true;
                           }

                           var10.add(new TreeTablePosition(var13, var14));
                        }

                        if (var11) {
                           if (TreeTableViewArrayListSelectionModel.this.treeTableView.isSortingInProgress()) {
                              TreeTableViewArrayListSelectionModel.this.startAtomic();
                              TreeTableViewArrayListSelectionModel.this.selectedCellsMap.setAll(var10);
                              TreeTableViewArrayListSelectionModel.this.stopAtomic();
                           } else {
                              TreeTableViewArrayListSelectionModel.this.startAtomic();
                              TreeTableViewArrayListSelectionModel.this.quietClearSelection();
                              TreeTableViewArrayListSelectionModel.this.stopAtomic();
                              TreeTableViewArrayListSelectionModel.this.selectedCellsMap.setAll(var10);
                              int var31 = TreeTableViewArrayListSelectionModel.this.treeTableView
                                 .getRow(TreeTableViewArrayListSelectionModel.this.getSelectedItem());
                              TreeTableViewArrayListSelectionModel.this.setSelectedIndex(var31);
                              TreeTableViewArrayListSelectionModel.this.focus(var31);
                           }
                        }
                     }
                  } while (var1.getChange() != null && var1.getChange().next());

                  if (var5 != 0) {
                     TreeTableViewArrayListSelectionModel.this.shiftSelection(
                        var4,
                        var5,
                        new Callback<MultipleSelectionModelBase.ShiftParams, Void>() {
                           public Void call(MultipleSelectionModelBase.ShiftParams var1) {
                              TreeTableViewArrayListSelectionModel.this.startAtomic();
                              int var2 = var1.getClearIndex();
                              int var3 = var1.getSetIndex();
                              TreeTablePosition var4 = null;
                              if (var2 > -1) {
                                 for (int var5 = 0; var5 < TreeTableViewArrayListSelectionModel.this.selectedCellsMap.size(); var5++) {
                                    TreeTablePosition var6 = TreeTableViewArrayListSelectionModel.this.selectedCellsMap.get(var5);
                                    if (var6.getRow() == var2) {
                                       var4 = var6;
                                       TreeTableViewArrayListSelectionModel.this.selectedCellsMap.remove(var6);
                                    } else if (var6.getRow() == var3 && !var1.isSelected()) {
                                       TreeTableViewArrayListSelectionModel.this.selectedCellsMap.remove(var6);
                                    }
                                 }
                              }

                              if (var4 != null && var1.isSelected()) {
                                 TreeTablePosition var7 = new TreeTablePosition<>(
                                    TreeTableViewArrayListSelectionModel.this.treeTableView, var1.getSetIndex(), var4.getTableColumn()
                                 );
                                 TreeTableViewArrayListSelectionModel.this.selectedCellsMap.add(var7);
                              }

                              TreeTableViewArrayListSelectionModel.this.stopAtomic();
                              return null;
                           }
                        }
                     );
                  }
               }
            }
         }
      };
      private WeakChangeListener<TreeItem<S>> weakRootPropertyListener = new WeakChangeListener(this.rootPropertyListener);
      private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakTreeItemListener;
      private final SelectedCellsMap<TreeTablePosition<S, ?>> selectedCellsMap;
      private final ReadOnlyUnbackedObservableList<TreeTablePosition<S, ?>> selectedCellsSeq;

      public TreeTableViewArrayListSelectionModel(TreeTableView<S> var1) {
         super(var1);
         this.treeTableView = var1;
         this.treeTableView.rootProperty().addListener(this.weakRootPropertyListener);
         this.treeTableView.showRootProperty().addListener(this.showRootPropertyListener);
         this.updateTreeEventListener(null, var1.getRoot());
         this.selectedCellsMap = new SelectedCellsMap<TreeTablePosition<S, ?>>(this::fireCustomSelectedCellsListChangeEvent) {
            @Override
            public boolean isCellSelectionEnabled() {
               return TreeTableViewArrayListSelectionModel.this.isCellSelectionEnabled();
            }
         };
         this.selectedCellsSeq = new ReadOnlyUnbackedObservableList<TreeTablePosition<S, ?>>() {
            public TreeTablePosition<S, ?> get(int var1) {
               return TreeTableViewArrayListSelectionModel.this.selectedCellsMap.get(var1);
            }

            @Override
            public int size() {
               return TreeTableViewArrayListSelectionModel.this.selectedCellsMap.size();
            }
         };
         this.updateDefaultSelection();
         this.cellSelectionEnabledProperty().addListener(var2 -> {
            this.updateDefaultSelection();
            TableCellBehaviorBase.setAnchor(var1, this.getFocusedCell(), true);
         });
      }

      private void dispose() {
         this.treeTableView.rootProperty().removeListener(this.weakRootPropertyListener);
         this.treeTableView.showRootProperty().removeListener(this.showRootPropertyListener);
         TreeItem var1 = this.treeTableView.getRoot();
         if (var1 != null) {
            var1.removeEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }
      }

      private void updateTreeEventListener(TreeItem<S> var1, TreeItem<S> var2) {
         if (var1 != null && this.weakTreeItemListener != null) {
            var1.removeEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }

         if (var2 != null) {
            this.weakTreeItemListener = new WeakEventHandler(this.treeItemListener);
            var2.addEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }
      }

      @Override
      public ObservableList<TreeTablePosition<S, ?>> getSelectedCells() {
         return this.selectedCellsSeq;
      }

      @Override
      public void clearAndSelect(int var1) {
         this.clearAndSelect(var1, null);
      }

      @Override
      public void clearAndSelect(int var1, TableColumnBase<TreeItem<S>, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TreeTablePosition var3 = new TreeTablePosition<>(this.getTreeTableView(), var1, (TreeTableColumn)var2);
            boolean var4 = this.isCellSelectionEnabled();
            TreeTableCellBehavior.setAnchor(this.treeTableView, var3, false);
            ArrayList var5 = new ArrayList(this.selectedCellsMap.getSelectedCells());
            boolean var6 = this.isSelected(var1, var2);
            if (var6 && var5.size() == 1) {
               TreeTablePosition var7 = (TreeTablePosition)this.getSelectedCells().get(0);
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
               for (TreeTablePosition var8 : var5) {
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

            this.fireCustomSelectedCellsListChangeEvent((Change<? extends TreeTablePosition<S, ?>>)var11);
         }
      }

      @Override
      public void select(int var1) {
         this.select(var1, null);
      }

      @Override
      public void select(int var1, TableColumnBase<TreeItem<S>, ?> var2) {
         if (var1 >= 0 && var1 < this.getRowCount()) {
            if (this.isCellSelectionEnabled() && var2 == null) {
               ObservableList var3 = this.getTreeTableView().getVisibleLeafColumns();

               for (int var4 = 0; var4 < var3.size(); var4++) {
                  this.select(var1, (TableColumnBase<TreeItem<S>, ?>)var3.get(var4));
               }
            } else {
               if (TableCellBehavior.hasDefaultAnchor(this.treeTableView)) {
                  TableCellBehavior.removeAnchor(this.treeTableView);
               }

               if (this.getSelectionMode() == SelectionMode.SINGLE) {
                  this.quietClearSelection();
               }

               this.selectedCellsMap.add(new TreeTablePosition<>(this.getTreeTableView(), var1, (TreeTableColumn)var2));
               this.updateSelectedIndex(var1);
               this.focus(var1, (TreeTableColumn<S, ?>)var2);
            }
         }
      }

      public void select(TreeItem<S> var1) {
         if (var1 == null && this.getSelectionMode() == SelectionMode.SINGLE) {
            this.clearSelection();
         } else {
            int var2 = this.treeTableView.getRow(var1);
            if (var2 > -1) {
               if (this.isSelected(var2)) {
                  return;
               }

               if (this.getSelectionMode() == SelectionMode.SINGLE) {
                  this.quietClearSelection();
               }

               this.select(var2);
            } else {
               this.setSelectedIndex(-1);
               this.setSelectedItem(var1);
            }
         }
      }

      @Override
      public void selectIndices(int var1, int... var2) {
         if (var2 == null) {
            this.select(var1);
         } else {
            int var3 = this.getRowCount();
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
                     ObservableList var6 = this.getTreeTableView().getVisibleLeafColumns();

                     for (int var7 = 0; var7 < var6.size(); var7++) {
                        if (!this.selectedCellsMap.isSelected(var1, var7)) {
                           var11.add(new TreeTablePosition<>(this.getTreeTableView(), var1, (TreeTableColumn)var6.get(var7)));
                        }
                     }
                  } else {
                     boolean var12 = this.selectedCellsMap.isSelected(var1, -1);
                     if (!var12) {
                        var11.add(new TreeTablePosition<>(this.getTreeTableView(), var1, null));
                     }
                  }

                  var10 = var1;
               }

               for (int var13 = 0; var13 < var2.length; var13++) {
                  int var14 = var2[var13];
                  if (var14 >= 0 && var14 < var3) {
                     var10 = var14;
                     if (this.isCellSelectionEnabled()) {
                        ObservableList var8 = this.getTreeTableView().getVisibleLeafColumns();

                        for (int var9 = 0; var9 < var8.size(); var9++) {
                           if (!this.selectedCellsMap.isSelected(var14, var9)) {
                              var11.add(new TreeTablePosition<>(this.getTreeTableView(), var14, (TreeTableColumn)var8.get(var9)));
                              var10 = var14;
                           }
                        }
                     } else if (!this.selectedCellsMap.isSelected(var14, -1)) {
                        var11.add(new TreeTablePosition<>(this.getTreeTableView(), var14, null));
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
               TreeTablePosition var3 = null;

               for (int var4 = 0; var4 < this.getTreeTableView().getVisibleLeafColumns().size(); var4++) {
                  TreeTableColumn var2 = (TreeTableColumn)this.getTreeTableView().getVisibleLeafColumns().get(var4);

                  for (int var5 = 0; var5 < this.getRowCount(); var5++) {
                     var3 = new TreeTablePosition<>(this.getTreeTableView(), var5, var2);
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

               for (int var7 = 0; var7 < this.getRowCount(); var7++) {
                  var6.add(new TreeTablePosition<>(this.getTreeTableView(), var7, null));
               }

               this.selectedCellsMap.setAll(var6);
               int var8 = this.getFocusedIndex();
               if (var8 == -1) {
                  int var9 = this.getItemCount();
                  if (var9 > 0) {
                     this.select(var9 - 1);
                     this.focus((TreeTablePosition<S, ?>)var6.get(var6.size() - 1));
                  }
               } else {
                  this.select(var8);
                  this.focus(var8);
               }
            }
         }
      }

      @Override
      public void selectRange(int var1, TableColumnBase<TreeItem<S>, ?> var2, int var3, TableColumnBase<TreeItem<S>, ?> var4) {
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
            this.select(var3, var4);
         } else {
            this.startAtomic();
            int var5 = this.getItemCount();
            boolean var6 = this.isCellSelectionEnabled();
            int var7 = this.treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>)var2);
            int var8 = this.treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>)var4);
            int var9 = Math.min(var7, var8);
            int var10 = Math.max(var7, var8);
            int var11 = Math.min(var1, var3);
            int var12 = Math.max(var1, var3);
            ArrayList var13 = new ArrayList();

            for (int var14 = var11; var14 <= var12; var14++) {
               if (var14 >= 0 && var14 < var5) {
                  if (!var6) {
                     var13.add(new TreeTablePosition<>(this.treeTableView, var14, (TreeTableColumn)var2));
                  } else {
                     for (int var15 = var9; var15 <= var10; var15++) {
                        TreeTableColumn var16 = this.treeTableView.getVisibleLeafColumn(var15);
                        if (var16 != null || !var6) {
                           var13.add(new TreeTablePosition<>(this.treeTableView, var14, var16));
                        }
                     }
                  }
               }
            }

            var13.removeAll(this.getSelectedCells());
            this.selectedCellsMap.addAll(var13);
            this.stopAtomic();
            this.updateSelectedIndex(var3);
            this.focus(var3, (TreeTableColumn<S, ?>)var4);
            TreeTableColumn var21 = (TreeTableColumn)var2;
            TreeTableColumn var22 = var6 ? (TreeTableColumn)var4 : var21;
            int var23 = this.selectedCellsMap.indexOf(new TreeTablePosition<>(this.treeTableView, var1, var21));
            int var17 = this.selectedCellsMap.indexOf(new TreeTablePosition<>(this.treeTableView, var3, var22));
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
      public void clearSelection(int var1, TableColumnBase<TreeItem<S>, ?> var2) {
         this.clearSelection(new TreeTablePosition<>(this.getTreeTableView(), var1, (TreeTableColumn)var2));
      }

      private void clearSelection(TreeTablePosition<S, ?> var1) {
         boolean var2 = this.isCellSelectionEnabled();
         int var3 = var1.getRow();
         boolean var4 = var1.getTableColumn() == null;
         ArrayList var5 = new ArrayList();

         for (TreeTablePosition var7 : this.getSelectedCells()) {
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
               NonIterableChange var2 = new NonIterableChange<TreeTablePosition<S, ?>>(0, 0, this.selectedCellsSeq) {
                  public List<TreeTablePosition<S, ?>> getRemoved() {
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
      public boolean isSelected(int var1, TableColumnBase<TreeItem<S>, ?> var2) {
         boolean var3 = this.isCellSelectionEnabled();
         if (var3 && var2 == null) {
            int var6 = this.treeTableView.getVisibleLeafColumns().size();

            for (int var5 = 0; var5 < var6; var5++) {
               if (!this.selectedCellsMap.isSelected(var1, var5)) {
                  return false;
               }
            }

            return true;
         } else {
            int var4 = var3 && var2 != null ? this.treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>)var2) : -1;
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
            TreeTablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() - 1 >= 0) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
            } else if (var1.getRow() < this.getRowCount() - 1) {
               this.select(var1.getRow() - 1, this.getTableColumn(this.getTreeTableView().getVisibleLeafColumns().size() - 1));
            }
         } else {
            int var2 = this.getFocusedIndex();
            if (var2 == -1) {
               this.select(this.getRowCount() - 1);
            } else if (var2 > 0) {
               this.select(var2 - 1);
            }
         }
      }

      @Override
      public void selectNext() {
         if (this.isCellSelectionEnabled()) {
            TreeTablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() + 1 < this.getTreeTableView().getVisibleLeafColumns().size()) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), 1));
            } else if (var1.getRow() < this.getRowCount() - 1) {
               this.select(var1.getRow() + 1, this.getTableColumn(0));
            }
         } else {
            int var2 = this.getFocusedIndex();
            if (var2 == -1) {
               this.select(0);
            } else if (var2 < this.getRowCount() - 1) {
               this.select(var2 + 1);
            }
         }
      }

      @Override
      public void selectAboveCell() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (var1.getRow() == -1) {
            this.select(this.getRowCount() - 1);
         } else if (var1.getRow() > 0) {
            this.select(var1.getRow() - 1, var1.getTableColumn());
         }
      }

      @Override
      public void selectBelowCell() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (var1.getRow() == -1) {
            this.select(0);
         } else if (var1.getRow() < this.getRowCount() - 1) {
            this.select(var1.getRow() + 1, var1.getTableColumn());
         }
      }

      @Override
      public void selectFirst() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
         }

         if (this.getRowCount() > 0) {
            if (this.isCellSelectionEnabled()) {
               this.select(0, var1.getTableColumn());
            } else {
               this.select(0);
            }
         }
      }

      @Override
      public void selectLast() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
         }

         int var2 = this.getRowCount();
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
            TreeTablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() - 1 >= 0) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
            }
         }
      }

      @Override
      public void selectRightCell() {
         if (this.isCellSelectionEnabled()) {
            TreeTablePosition var1 = this.getFocusedCell();
            if (var1.getColumn() + 1 < this.getTreeTableView().getVisibleLeafColumns().size()) {
               this.select(var1.getRow(), this.getTableColumn(var1.getTableColumn(), 1));
            }
         }
      }

      private void updateDefaultSelection() {
         int var1 = -1;
         TreeItem var2 = this.getSelectedItem();
         if (var2 != null) {
            var1 = this.treeTableView.getRow(var2);
         }

         int var3 = var1 != -1 ? var1 : (this.treeTableView.getExpandedItemCount() > 0 ? 0 : -1);
         this.clearSelection();
         this.select(var1, this.isCellSelectionEnabled() ? this.getTableColumn(0) : null);
         this.focus(var3, this.isCellSelectionEnabled() ? this.getTableColumn(0) : null);
      }

      private TreeTableColumn<S, ?> getTableColumn(int var1) {
         return this.getTreeTableView().getVisibleLeafColumn(var1);
      }

      private TreeTableColumn<S, ?> getTableColumn(TreeTableColumn<S, ?> var1, int var2) {
         int var3 = this.getTreeTableView().getVisibleLeafIndex(var1);
         int var4 = var3 + var2;
         return this.getTreeTableView().getVisibleLeafColumn(var4);
      }

      private void updateSelectedIndex(int var1) {
         this.setSelectedIndex(var1);
         this.setSelectedItem(this.getModelItem(var1));
      }

      @Override
      public void focus(int var1) {
         this.focus(var1, null);
      }

      private void focus(int var1, TreeTableColumn<S, ?> var2) {
         this.focus(new TreeTablePosition<>(this.getTreeTableView(), var1, var2));
      }

      private void focus(TreeTablePosition<S, ?> var1) {
         if (this.getTreeTableView().getFocusModel() != null) {
            this.getTreeTableView().getFocusModel().focus(var1.getRow(), var1.getTableColumn());
            this.getTreeTableView().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
         }
      }

      @Override
      public int getFocusedIndex() {
         return this.getFocusedCell().getRow();
      }

      private TreeTablePosition<S, ?> getFocusedCell() {
         return this.treeTableView.getFocusModel() == null
            ? new TreeTablePosition<>(this.treeTableView, -1, null)
            : this.treeTableView.getFocusModel().getFocusedCell();
      }

      private int getRowCount() {
         return this.treeTableView.getExpandedItemCount();
      }

      private void fireCustomSelectedCellsListChangeEvent(Change<? extends TreeTablePosition<S, ?>> var1) {
         ControlUtils.updateSelectedIndices(this, var1);
         if (!this.isAtomic()) {
            this.selectedCellsSeq.callObservers(new MappingChange(var1, MappingChange.NOOP_MAP, this.selectedCellsSeq));
         }
      }
   }

   public static class TreeTableViewFocusModel<S> extends TableFocusModel<TreeItem<S>, TreeTableColumn<S, ?>> {
      private final TreeTableView<S> treeTableView;
      private final TreeTablePosition EMPTY_CELL;
      private final ChangeListener<TreeItem<S>> rootPropertyListener = (var1x, var2x, var3x) -> this.updateTreeEventListener(var2x, var3x);
      private final WeakChangeListener<TreeItem<S>> weakRootPropertyListener = new WeakChangeListener(this.rootPropertyListener);
      private final InvalidationListener showRootListener;
      private EventHandler<TreeItem.TreeModificationEvent<S>> treeItemListener = new EventHandler<TreeItem.TreeModificationEvent<S>>() {
         public void handle(TreeItem.TreeModificationEvent<S> var1) {
            if (TreeTableViewFocusModel.this.getFocusedIndex() != -1) {
               int var2 = 0;
               if (var1.getChange() != null) {
                  var1.getChange().next();
               }

               do {
                  int var3 = TreeTableViewFocusModel.this.treeTableView.getRow(var1.getTreeItem());
                  if (var1.wasExpanded()) {
                     if (var3 < TreeTableViewFocusModel.this.getFocusedIndex()) {
                        var2 += var1.getTreeItem().getExpandedDescendentCount(false) - 1;
                     }
                  } else if (var1.wasCollapsed()) {
                     if (var3 < TreeTableViewFocusModel.this.getFocusedIndex()) {
                        var2 += -var1.getTreeItem().previousExpandedDescendentCount + 1;
                     }
                  } else if (var1.wasAdded()) {
                     TreeItem var10 = var1.getTreeItem();
                     if (var10.isExpanded()) {
                        for (int var12 = 0; var12 < var1.getAddedChildren().size(); var12++) {
                           TreeItem var6 = (TreeItem)var1.getAddedChildren().get(var12);
                           var3 = TreeTableViewFocusModel.this.treeTableView.getRow(var6);
                           if (var6 != null && var3 <= var2 + TreeTableViewFocusModel.this.getFocusedIndex()) {
                              var2 += var6.getExpandedDescendentCount(false);
                           }
                        }
                     }
                  } else if (var1.wasRemoved()) {
                     var3 += var1.getFrom() + 1;

                     for (int var4 = 0; var4 < var1.getRemovedChildren().size(); var4++) {
                        TreeItem var5 = (TreeItem)var1.getRemovedChildren().get(var4);
                        if (var5 != null && var5.equals(TreeTableViewFocusModel.this.getFocusedItem())) {
                           TreeTableViewFocusModel.this.focus(Math.max(0, TreeTableViewFocusModel.this.getFocusedIndex() - 1));
                           return;
                        }
                     }

                     if (var3 <= TreeTableViewFocusModel.this.getFocusedIndex()) {
                        var2 += var1.getTreeItem().isExpanded() ? -var1.getRemovedSize() : 0;
                     }
                  }
               } while (var1.getChange() != null && var1.getChange().next());

               if (var2 != 0) {
                  TreeTablePosition var9 = TreeTableViewFocusModel.this.getFocusedCell();
                  int var11 = var9.getRow() + var2;
                  if (var11 >= 0) {
                     Platform.runLater(() -> TreeTableViewFocusModel.this.focus(var11, var9.getTableColumn()));
                  }
               }
            }
         }
      };
      private WeakEventHandler<TreeItem.TreeModificationEvent<S>> weakTreeItemListener;
      private ReadOnlyObjectWrapper<TreeTablePosition<S, ?>> focusedCell;

      public TreeTableViewFocusModel(TreeTableView<S> var1) {
         if (var1 == null) {
            throw new NullPointerException("TableView can not be null");
         } else {
            this.treeTableView = var1;
            this.EMPTY_CELL = new TreeTablePosition(var1, -1, null);
            this.treeTableView.rootProperty().addListener(this.weakRootPropertyListener);
            this.updateTreeEventListener(null, var1.getRoot());
            int var2 = this.getItemCount() > 0 ? 0 : -1;
            TreeTablePosition var3 = new TreeTablePosition(var1, var2, null);
            this.setFocusedCell(var3);
            this.showRootListener = var1x -> {
               if (this.isFocused(0)) {
                  this.focus(-1);
                  this.focus(0);
               }
            };
            var1.showRootProperty().addListener(new WeakInvalidationListener(this.showRootListener));
            this.focusedCellProperty().addListener(var1x -> var1.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM));
         }
      }

      private void updateTreeEventListener(TreeItem<S> var1, TreeItem<S> var2) {
         if (var1 != null && this.weakTreeItemListener != null) {
            var1.removeEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }

         if (var2 != null) {
            this.weakTreeItemListener = new WeakEventHandler(this.treeItemListener);
            var2.addEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }
      }

      @Override
      protected int getItemCount() {
         return this.treeTableView.getExpandedItemCount();
      }

      protected TreeItem<S> getModelItem(int var1) {
         return var1 >= 0 && var1 < this.getItemCount() ? this.treeTableView.getTreeItem(var1) : null;
      }

      public final ReadOnlyObjectProperty<TreeTablePosition<S, ?>> focusedCellProperty() {
         return this.focusedCellPropertyImpl().getReadOnlyProperty();
      }

      private void setFocusedCell(TreeTablePosition<S, ?> var1) {
         this.focusedCellPropertyImpl().set(var1);
      }

      public final TreeTablePosition<S, ?> getFocusedCell() {
         return this.focusedCell == null ? this.EMPTY_CELL : (TreeTablePosition)this.focusedCell.get();
      }

      private ReadOnlyObjectWrapper<TreeTablePosition<S, ?>> focusedCellPropertyImpl() {
         if (this.focusedCell == null) {
            this.focusedCell = new ReadOnlyObjectWrapper<TreeTablePosition<S, ?>>(this.EMPTY_CELL) {
               private TreeTablePosition<S, ?> old;

               protected void invalidated() {
                  if (this.get() != null) {
                     if (this.old == null || !this.old.equals(this.get())) {
                        TreeTableViewFocusModel.this.setFocusedIndex(((TreeTablePosition)this.get()).getRow());
                        TreeTableViewFocusModel.this.setFocusedItem(TreeTableViewFocusModel.this.getModelItem(((TreeTablePosition)this.getValue()).getRow()));
                        this.old = (TreeTablePosition<S, ?>)this.get();
                     }
                  }
               }

               public Object getBean() {
                  return TreeTableViewFocusModel.this;
               }

               public String getName() {
                  return "focusedCell";
               }
            };
         }

         return this.focusedCell;
      }

      public void focus(int var1, TreeTableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TreeTablePosition var3 = this.getFocusedCell();
            TreeTablePosition var4 = new TreeTablePosition<>(this.treeTableView, var1, var2);
            this.setFocusedCell(var4);
            if (var4.equals(var3)) {
               this.setFocusedIndex(var1);
               this.setFocusedItem(this.getModelItem(var1));
            }
         } else {
            this.setFocusedCell(this.EMPTY_CELL);
         }
      }

      public void focus(TreeTablePosition<S, ?> var1) {
         if (var1 != null) {
            this.focus(var1.getRow(), var1.getTableColumn());
         }
      }

      public boolean isFocused(int var1, TreeTableColumn<S, ?> var2) {
         if (var1 >= 0 && var1 < this.getItemCount()) {
            TreeTablePosition var3 = this.getFocusedCell();
            boolean var4 = var2 == null || var2.equals(var3.getTableColumn());
            return var3.getRow() == var1 && var4;
         } else {
            return false;
         }
      }

      @Override
      public void focus(int var1) {
         if (this.treeTableView.expandedItemCountDirty) {
            this.treeTableView.updateExpandedItemCount(this.treeTableView.getRoot());
         }

         if (var1 >= 0 && var1 < this.getItemCount()) {
            this.setFocusedCell(new TreeTablePosition<>(this.treeTableView, var1, null));
         } else {
            this.setFocusedCell(this.EMPTY_CELL);
         }
      }

      @Override
      public void focusAboveCell() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (this.getFocusedIndex() == -1) {
            this.focus(this.getItemCount() - 1, var1.getTableColumn());
         } else if (this.getFocusedIndex() > 0) {
            this.focus(this.getFocusedIndex() - 1, var1.getTableColumn());
         }
      }

      @Override
      public void focusBelowCell() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (this.getFocusedIndex() == -1) {
            this.focus(0, var1.getTableColumn());
         } else if (this.getFocusedIndex() != this.getItemCount() - 1) {
            this.focus(this.getFocusedIndex() + 1, var1.getTableColumn());
         }
      }

      @Override
      public void focusLeftCell() {
         TreeTablePosition var1 = this.getFocusedCell();
         if (var1.getColumn() > 0) {
            this.focus(var1.getRow(), this.getTableColumn(var1.getTableColumn(), -1));
         }
      }

      @Override
      public void focusRightCell() {
         TreeTablePosition var1 = this.getFocusedCell();
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

      private int getColumnCount() {
         return this.treeTableView.getVisibleLeafColumns().size();
      }

      private TreeTableColumn<S, ?> getTableColumn(TreeTableColumn<S, ?> var1, int var2) {
         int var3 = this.treeTableView.getVisibleLeafIndex(var1);
         int var4 = var3 + var2;
         return this.treeTableView.getVisibleLeafColumn(var4);
      }
   }

   public abstract static class TreeTableViewSelectionModel<S> extends TableSelectionModel<TreeItem<S>> {
      private final TreeTableView<S> treeTableView;

      public TreeTableViewSelectionModel(TreeTableView<S> var1) {
         if (var1 == null) {
            throw new NullPointerException("TreeTableView can not be null");
         } else {
            this.treeTableView = var1;
         }
      }

      public abstract ObservableList<TreeTablePosition<S, ?>> getSelectedCells();

      public TreeTableView<S> getTreeTableView() {
         return this.treeTableView;
      }

      public TreeItem<S> getModelItem(int var1) {
         return this.treeTableView.getTreeItem(var1);
      }

      @Override
      protected int getItemCount() {
         return this.treeTableView.getExpandedItemCount();
      }

      @Override
      public void focus(int var1) {
         this.focus(var1, null);
      }

      @Override
      public int getFocusedIndex() {
         return this.getFocusedCell().getRow();
      }

      @Override
      public void selectRange(int var1, TableColumnBase<TreeItem<S>, ?> var2, int var3, TableColumnBase<TreeItem<S>, ?> var4) {
         int var5 = this.treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>)var2);
         int var6 = this.treeTableView.getVisibleLeafIndex((TreeTableColumn<S, ?>)var4);

         for (int var7 = var1; var7 <= var3; var7++) {
            for (int var8 = var5; var8 <= var6; var8++) {
               this.select(var7, this.treeTableView.getVisibleLeafColumn(var8));
            }
         }
      }

      private void focus(int var1, TreeTableColumn<S, ?> var2) {
         this.focus(new TreeTablePosition<>(this.getTreeTableView(), var1, var2));
      }

      private void focus(TreeTablePosition<S, ?> var1) {
         if (this.getTreeTableView().getFocusModel() != null) {
            this.getTreeTableView().getFocusModel().focus(var1.getRow(), var1.getTableColumn());
         }
      }

      private TreeTablePosition<S, ?> getFocusedCell() {
         return this.treeTableView.getFocusModel() == null
            ? new TreeTablePosition<>(this.treeTableView, -1, null)
            : this.treeTableView.getFocusModel().getFocusedCell();
      }
   }
}
