package javafx.scene.control;

import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
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
import javafx.scene.control.skin.TreeViewSkin;
import javafx.util.Callback;

@DefaultProperty("root")
public class TreeView<T> extends Control {
   private static final EventType<?> EDIT_ANY_EVENT = new EventType(Event.ANY, "TREE_VIEW_EDIT");
   private static final EventType<?> EDIT_START_EVENT = new EventType(editAnyEvent(), "EDIT_START");
   private static final EventType<?> EDIT_CANCEL_EVENT = new EventType(editAnyEvent(), "EDIT_CANCEL");
   private static final EventType<?> EDIT_COMMIT_EVENT = new EventType(editAnyEvent(), "EDIT_COMMIT");
   private boolean expandedItemCountDirty = true;
   private Map<Integer, SoftReference<TreeItem<T>>> treeItemCacheMap = new HashMap<>();
   private final EventHandler<TreeItem.TreeModificationEvent<T>> rootEvent = var1x -> {
      EventType var2x = var1x.getEventType();

      boolean var3;
      for (var3 = false; var2x != null; var2x = var2x.getSuperType()) {
         if (var2x.equals(TreeItem.expandedItemCountChangeEvent())) {
            var3 = true;
            break;
         }
      }

      if (var3) {
         this.expandedItemCountDirty = true;
         this.requestLayout();
      }
   };
   private WeakEventHandler<TreeItem.TreeModificationEvent<T>> weakRootEventListener;
   private ObjectProperty<Callback<TreeView<T>, TreeCell<T>>> cellFactory;
   private ObjectProperty<TreeItem<T>> root = new SimpleObjectProperty<TreeItem<T>>(this, "root") {
      private WeakReference<TreeItem<T>> weakOldItem;

      protected void invalidated() {
         TreeItem var1 = this.weakOldItem == null ? null : this.weakOldItem.get();
         if (var1 != null && TreeView.this.weakRootEventListener != null) {
            var1.removeEventHandler(TreeItem.treeNotificationEvent(), TreeView.this.weakRootEventListener);
         }

         TreeItem var2 = TreeView.this.getRoot();
         if (var2 != null) {
            TreeView.this.weakRootEventListener = new WeakEventHandler(TreeView.this.rootEvent);
            TreeView.this.getRoot().addEventHandler(TreeItem.treeNotificationEvent(), TreeView.this.weakRootEventListener);
            this.weakOldItem = new WeakReference<>(var2);
         }

         TreeView.this.edit(null);
         TreeView.this.expandedItemCountDirty = true;
         TreeView.this.updateRootExpanded();
      }
   };
   private BooleanProperty showRoot;
   private ObjectProperty<MultipleSelectionModel<TreeItem<T>>> selectionModel;
   private ObjectProperty<FocusModel<TreeItem<T>>> focusModel;
   private ReadOnlyIntegerWrapper expandedItemCount = new ReadOnlyIntegerWrapper(this, "expandedItemCount", 0);
   private DoubleProperty fixedCellSize;
   private BooleanProperty editable;
   private ReadOnlyObjectWrapper<TreeItem<T>> editingItem;
   private ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditStart;
   private ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditCommit;
   private ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditCancel;
   private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
   private static final String DEFAULT_STYLE_CLASS = "tree-view";

   public static <T> EventType<TreeView.EditEvent<T>> editAnyEvent() {
      return (EventType<TreeView.EditEvent<T>>)EDIT_ANY_EVENT;
   }

   public static <T> EventType<TreeView.EditEvent<T>> editStartEvent() {
      return (EventType<TreeView.EditEvent<T>>)EDIT_START_EVENT;
   }

   public static <T> EventType<TreeView.EditEvent<T>> editCancelEvent() {
      return (EventType<TreeView.EditEvent<T>>)EDIT_CANCEL_EVENT;
   }

   public static <T> EventType<TreeView.EditEvent<T>> editCommitEvent() {
      return (EventType<TreeView.EditEvent<T>>)EDIT_COMMIT_EVENT;
   }

   @Deprecated(
      since = "8u20"
   )
   public static int getNodeLevel(TreeItem<?> var0) {
      if (var0 == null) {
         return -1;
      } else {
         int var1 = 0;

         for (TreeItem var2 = var0.getParent(); var2 != null; var2 = var2.getParent()) {
            var1++;
         }

         return var1;
      }
   }

   public TreeView() {
      this(null);
   }

   public TreeView(TreeItem<T> var1) {
      this.getStyleClass().setAll(new String[]{"tree-view"});
      this.setAccessibleRole(AccessibleRole.TREE_VIEW);
      this.setRoot(var1);
      this.updateExpandedItemCount(var1);
      TreeView.TreeViewBitSetSelectionModel var2 = new TreeView.TreeViewBitSetSelectionModel<>(this);
      this.setSelectionModel(var2);
      this.setFocusModel(new TreeView.TreeViewFocusModel<>(this));
   }

   public final void setCellFactory(Callback<TreeView<T>, TreeCell<T>> var1) {
      this.cellFactoryProperty().set(var1);
   }

   public final Callback<TreeView<T>, TreeCell<T>> getCellFactory() {
      return this.cellFactory == null ? null : (Callback)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<TreeView<T>, TreeCell<T>>> cellFactoryProperty() {
      if (this.cellFactory == null) {
         this.cellFactory = new SimpleObjectProperty(this, "cellFactory");
      }

      return this.cellFactory;
   }

   public final void setRoot(TreeItem<T> var1) {
      this.rootProperty().set(var1);
   }

   public final TreeItem<T> getRoot() {
      return this.root == null ? null : (TreeItem)this.root.get();
   }

   public final ObjectProperty<TreeItem<T>> rootProperty() {
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
               TreeView.this.updateRootExpanded();
               TreeView.this.updateExpandedItemCount(TreeView.this.getRoot());
            }
         };
      }

      return this.showRoot;
   }

   public final void setSelectionModel(MultipleSelectionModel<TreeItem<T>> var1) {
      this.selectionModelProperty().set(var1);
   }

   public final MultipleSelectionModel<TreeItem<T>> getSelectionModel() {
      return this.selectionModel == null ? null : (MultipleSelectionModel)this.selectionModel.get();
   }

   public final ObjectProperty<MultipleSelectionModel<TreeItem<T>>> selectionModelProperty() {
      if (this.selectionModel == null) {
         this.selectionModel = new SimpleObjectProperty(this, "selectionModel");
      }

      return this.selectionModel;
   }

   public final void setFocusModel(FocusModel<TreeItem<T>> var1) {
      this.focusModelProperty().set(var1);
   }

   public final FocusModel<TreeItem<T>> getFocusModel() {
      return this.focusModel == null ? null : (FocusModel)this.focusModel.get();
   }

   public final ObjectProperty<FocusModel<TreeItem<T>>> focusModelProperty() {
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

   public final void setFixedCellSize(double var1) {
      this.fixedCellSizeProperty().set(var1);
   }

   public final double getFixedCellSize() {
      return this.fixedCellSize == null ? -1.0 : this.fixedCellSize.get();
   }

   public final DoubleProperty fixedCellSizeProperty() {
      if (this.fixedCellSize == null) {
         this.fixedCellSize = new StyleableDoubleProperty(-1.0) {
            public CssMetaData<TreeView<?>, Number> getCssMetaData() {
               return TreeView.StyleableProperties.FIXED_CELL_SIZE;
            }

            public Object getBean() {
               return TreeView.this;
            }

            public String getName() {
               return "fixedCellSize";
            }
         };
      }

      return this.fixedCellSize;
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

   private void setEditingItem(TreeItem<T> var1) {
      this.editingItemPropertyImpl().set(var1);
   }

   public final TreeItem<T> getEditingItem() {
      return this.editingItem == null ? null : (TreeItem)this.editingItem.get();
   }

   public final ReadOnlyObjectProperty<TreeItem<T>> editingItemProperty() {
      return this.editingItemPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<TreeItem<T>> editingItemPropertyImpl() {
      if (this.editingItem == null) {
         this.editingItem = new ReadOnlyObjectWrapper(this, "editingItem");
      }

      return this.editingItem;
   }

   public final void setOnEditStart(EventHandler<TreeView.EditEvent<T>> var1) {
      this.onEditStartProperty().set(var1);
   }

   public final EventHandler<TreeView.EditEvent<T>> getOnEditStart() {
      return this.onEditStart == null ? null : (EventHandler)this.onEditStart.get();
   }

   public final ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditStartProperty() {
      if (this.onEditStart == null) {
         this.onEditStart = new SimpleObjectProperty<EventHandler<TreeView.EditEvent<T>>>(this, "onEditStart") {
            protected void invalidated() {
               TreeView.this.setEventHandler(TreeView.editStartEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditStart;
   }

   public final void setOnEditCommit(EventHandler<TreeView.EditEvent<T>> var1) {
      this.onEditCommitProperty().set(var1);
   }

   public final EventHandler<TreeView.EditEvent<T>> getOnEditCommit() {
      return this.onEditCommit == null ? null : (EventHandler)this.onEditCommit.get();
   }

   public final ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditCommitProperty() {
      if (this.onEditCommit == null) {
         this.onEditCommit = new SimpleObjectProperty<EventHandler<TreeView.EditEvent<T>>>(this, "onEditCommit") {
            protected void invalidated() {
               TreeView.this.setEventHandler(TreeView.editCommitEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCommit;
   }

   public final void setOnEditCancel(EventHandler<TreeView.EditEvent<T>> var1) {
      this.onEditCancelProperty().set(var1);
   }

   public final EventHandler<TreeView.EditEvent<T>> getOnEditCancel() {
      return this.onEditCancel == null ? null : (EventHandler)this.onEditCancel.get();
   }

   public final ObjectProperty<EventHandler<TreeView.EditEvent<T>>> onEditCancelProperty() {
      if (this.onEditCancel == null) {
         this.onEditCancel = new SimpleObjectProperty<EventHandler<TreeView.EditEvent<T>>>(this, "onEditCancel") {
            protected void invalidated() {
               TreeView.this.setEventHandler(TreeView.editCancelEvent(), (EventHandler)this.get());
            }
         };
      }

      return this.onEditCancel;
   }

   @Override
   protected void layoutChildren() {
      if (this.expandedItemCountDirty) {
         this.updateExpandedItemCount(this.getRoot());
      }

      super.layoutChildren();
   }

   public void edit(TreeItem<T> var1) {
      if (this.isEditable()) {
         this.setEditingItem(var1);
      }
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
               TreeView.this.setEventHandler(ScrollToEvent.scrollToTopIndex(), (EventHandler)this.get());
            }

            public Object getBean() {
               return TreeView.this;
            }

            public String getName() {
               return "onScrollTo";
            }
         };
      }

      return this.onScrollTo;
   }

   public int getRow(TreeItem<T> var1) {
      return TreeUtil.getRow(var1, this.getRoot(), this.expandedItemCountDirty, this.isShowRoot());
   }

   public TreeItem<T> getTreeItem(int var1) {
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

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TreeViewSkin(this);
   }

   public void refresh() {
      this.getProperties().put("recreateKey", Boolean.TRUE);
   }

   private void updateExpandedItemCount(TreeItem<T> var1) {
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

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TreeView.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case MULTIPLE_SELECTION:
            MultipleSelectionModel var3 = this.getSelectionModel();
            return var3 != null && var3.getSelectionMode() == SelectionMode.MULTIPLE;
         case ROW_COUNT:
            return this.getExpandedItemCount();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   public static class EditEvent<T> extends Event {
      private static final long serialVersionUID = -4437033058917528976L;
      public static final EventType<?> ANY = TreeView.EDIT_ANY_EVENT;
      private final TreeView<T> source;
      private final T oldValue;
      private final T newValue;
      private final transient TreeItem<T> treeItem;

      public EditEvent(TreeView<T> var1, EventType<? extends TreeView.EditEvent> var2, TreeItem<T> var3, T var4, T var5) {
         super(var1, Event.NULL_SOURCE_TARGET, var2);
         this.source = var1;
         this.oldValue = (T)var4;
         this.newValue = (T)var5;
         this.treeItem = var3;
      }

      public TreeView<T> getSource() {
         return this.source;
      }

      public TreeItem<T> getTreeItem() {
         return this.treeItem;
      }

      public T getNewValue() {
         return this.newValue;
      }

      public T getOldValue() {
         return this.oldValue;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TreeView<?>, Number> FIXED_CELL_SIZE = new CssMetaData<TreeView<?>, Number>(
         "-fx-fixed-cell-size", SizeConverter.getInstance(), -1.0
      ) {
         public Double getInitialValue(TreeView<?> var1) {
            return var1.getFixedCellSize();
         }

         public boolean isSettable(TreeView<?> var1) {
            return var1.fixedCellSize == null || !var1.fixedCellSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TreeView<?> var1) {
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

   static class TreeViewBitSetSelectionModel<T> extends MultipleSelectionModelBase<TreeItem<T>> {
      private TreeView<T> treeView = null;
      private ChangeListener<TreeItem<T>> rootPropertyListener = (var1x, var2, var3) -> {
         this.updateDefaultSelection();
         this.updateTreeEventListener(var2, var3);
      };
      private EventHandler<TreeItem.TreeModificationEvent<T>> treeItemListener = var1x -> {
         if (this.getSelectedIndex() != -1 || this.getSelectedItem() != null) {
            TreeItem var2 = var1x.getTreeItem();
            if (var2 != null) {
               this.treeView.expandedItemCountDirty = true;
               int var3 = this.treeView.getRow(var2);
               int var4 = 0;
               Change var5 = var1x.getChange();
               if (var5 != null) {
                  var5.next();
               }

               do {
                  int var6 = var5 == null ? 0 : var5.getAddedSize();
                  int var7 = var5 == null ? 0 : var5.getRemovedSize();
                  if (var1x.wasExpanded()) {
                     var4 += var2.getExpandedDescendentCount(false) - 1;
                     var3++;
                  } else if (!var1x.wasCollapsed()) {
                     if (!var1x.wasPermutated()) {
                        if (var1x.wasAdded()) {
                           var4 += var2.isExpanded() ? var6 : 0;
                           var3 = this.treeView.getRow((TreeItem<T>)var1x.getChange().getAddedSubList().get(0));
                        } else if (var1x.wasRemoved()) {
                           var4 += var2.isExpanded() ? -var7 : 0;
                           var3 += var1x.getFrom() + 1;
                           ObservableList var18 = this.getSelectedIndices();
                           int var19 = this.getSelectedIndex();
                           ObservableList var20 = this.getSelectedItems();
                           TreeItem var21 = this.getSelectedItem();
                           List var22 = var1x.getChange().getRemoved();

                           for (int var23 = 0; var23 < var18.size() && !var20.isEmpty(); var23++) {
                              int var24 = (Integer)var18.get(var23);
                              if (var24 > var20.size()) {
                                 break;
                              }

                              if (var22.size() == 1 && var20.size() == 1 && var21 != null && var21.equals(var22.get(0)) && var19 < this.getItemCount()) {
                                 int var26 = var19 == 0 ? 0 : var19 - 1;
                                 TreeItem var27 = this.getModelItem(var26);
                                 if (!var21.equals(var27)) {
                                    this.select(var27);
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     var2.getExpandedDescendentCount(false);
                     int var8 = var2.previousExpandedDescendentCount;
                     int var9 = this.getSelectedIndex();
                     boolean var10 = var9 >= var3 + 1 && var9 < var3 + var8;
                     boolean var11 = false;
                     this.selectedIndices._beginChange();
                     int var12 = var3 + 1;
                     int var13 = var3 + var8;
                     ArrayList var14 = new ArrayList();

                     for (int var15 = var12; var15 < var13; var15++) {
                        if (this.isSelected(var15)) {
                           var11 = true;
                           var14.add(var15);
                        }
                     }

                     if (!var14.isEmpty()) {
                        this.selectedIndices._nextRemove(this.selectedIndices.indexOf(var14.get(0)), var14);
                     }

                     for (int var16 : var14) {
                        this.startAtomic();
                        this.clearSelection(var16);
                        this.stopAtomic();
                     }

                     this.selectedIndices._endChange();
                     if (var10 && var11) {
                        this.select(var3);
                     }

                     var4 += -var8 + 1;
                     var3++;
                  }
               } while (var1x.getChange() != null && var1x.getChange().next());

               this.shiftSelection(var3, var4, null);
               if (var1x.wasAdded() || var1x.wasRemoved()) {
                  Integer var17 = (Integer)TreeCellBehavior.<Integer>getAnchor(this.treeView, null);
                  if (var17 != null && this.isSelected(var17 + var4)) {
                     TreeCellBehavior.setAnchor(this.treeView, Integer.valueOf(var17 + var4), false);
                  }
               }
            }
         }
      };
      private WeakChangeListener<TreeItem<T>> weakRootPropertyListener = new WeakChangeListener(this.rootPropertyListener);
      private WeakEventHandler<TreeItem.TreeModificationEvent<T>> weakTreeItemListener;
      private InvalidationListener showRootListener;

      public TreeViewBitSetSelectionModel(TreeView<T> var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("TreeView can not be null");
         } else {
            this.treeView = var1;
            this.treeView.rootProperty().addListener(this.weakRootPropertyListener);
            this.showRootListener = var2 -> this.shiftSelection(0, var1.isShowRoot() ? 1 : -1, null);
            this.treeView.showRootProperty().addListener(new WeakInvalidationListener(this.showRootListener));
            this.updateTreeEventListener(null, var1.getRoot());
            this.updateDefaultSelection();
         }
      }

      private void updateTreeEventListener(TreeItem<T> var1, TreeItem<T> var2) {
         if (var1 != null && this.weakTreeItemListener != null) {
            var1.removeEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }

         if (var2 != null) {
            this.weakTreeItemListener = new WeakEventHandler(this.treeItemListener);
            var2.addEventHandler(TreeItem.expandedItemCountChangeEvent(), this.weakTreeItemListener);
         }
      }

      @Override
      public void selectAll() {
         int var1 = (Integer)TreeCellBehavior.<Integer>getAnchor(this.treeView, Integer.valueOf(-1));
         super.selectAll();
         TreeCellBehavior.setAnchor(this.treeView, Integer.valueOf(var1), false);
      }

      public void select(TreeItem<T> var1) {
         if (var1 == null && this.getSelectionMode() == SelectionMode.SINGLE) {
            this.clearSelection();
         } else {
            if (var1 != null) {
               for (TreeItem var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
                  var2.setExpanded(true);
               }
            }

            this.treeView.updateExpandedItemCount(this.treeView.getRoot());
            int var3 = this.treeView.getRow(var1);
            if (var3 == -1) {
               this.setSelectedIndex(-1);
               this.setSelectedItem(var1);
            } else {
               this.select(var3);
            }
         }
      }

      @Override
      public void clearAndSelect(int var1) {
         TreeCellBehavior.setAnchor(this.treeView, Integer.valueOf(var1), false);
         super.clearAndSelect(var1);
      }

      @Override
      protected void focus(int var1) {
         if (this.treeView.getFocusModel() != null) {
            this.treeView.getFocusModel().focus(var1);
         }

         this.treeView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
      }

      @Override
      protected int getFocusedIndex() {
         return this.treeView.getFocusModel() == null ? -1 : this.treeView.getFocusModel().getFocusedIndex();
      }

      @Override
      protected int getItemCount() {
         return this.treeView == null ? 0 : this.treeView.getExpandedItemCount();
      }

      public TreeItem<T> getModelItem(int var1) {
         if (this.treeView == null) {
            return null;
         } else {
            return var1 >= 0 && var1 < this.treeView.getExpandedItemCount() ? this.treeView.getTreeItem(var1) : null;
         }
      }

      private void updateDefaultSelection() {
         this.clearSelection();
         this.focus(this.getItemCount() > 0 ? 0 : -1);
      }
   }

   static class TreeViewFocusModel<T> extends FocusModel<TreeItem<T>> {
      private final TreeView<T> treeView;
      private final ChangeListener<TreeItem<T>> rootPropertyListener = (var1x, var2, var3) -> this.updateTreeEventListener(var2, var3);
      private final WeakChangeListener<TreeItem<T>> weakRootPropertyListener = new WeakChangeListener(this.rootPropertyListener);
      private final InvalidationListener showRootListener;
      private EventHandler<TreeItem.TreeModificationEvent<T>> treeItemListener = new EventHandler<TreeItem.TreeModificationEvent<T>>() {
         public void handle(TreeItem.TreeModificationEvent<T> var1) {
            if (TreeViewFocusModel.this.getFocusedIndex() != -1) {
               int var2 = TreeViewFocusModel.this.treeView.getRow(var1.getTreeItem());
               int var3 = 0;
               if (var1.getChange() != null) {
                  var1.getChange().next();
               }

               do {
                  if (var1.wasExpanded()) {
                     if (var2 < TreeViewFocusModel.this.getFocusedIndex()) {
                        var3 += var1.getTreeItem().getExpandedDescendentCount(false) - 1;
                     }
                  } else if (var1.wasCollapsed()) {
                     if (var2 < TreeViewFocusModel.this.getFocusedIndex()) {
                        var3 += -var1.getTreeItem().previousExpandedDescendentCount + 1;
                     }
                  } else if (var1.wasAdded()) {
                     TreeItem var7 = var1.getTreeItem();
                     if (var7.isExpanded()) {
                        for (int var9 = 0; var9 < var1.getAddedChildren().size(); var9++) {
                           TreeItem var6 = (TreeItem)var1.getAddedChildren().get(var9);
                           var2 = TreeViewFocusModel.this.treeView.getRow(var6);
                           if (var6 != null && var2 <= var3 + TreeViewFocusModel.this.getFocusedIndex()) {
                              var3 += var6.getExpandedDescendentCount(false);
                           }
                        }
                     }
                  } else if (var1.wasRemoved()) {
                     var2 += var1.getFrom() + 1;

                     for (int var4 = 0; var4 < var1.getRemovedChildren().size(); var4++) {
                        TreeItem var5 = (TreeItem)var1.getRemovedChildren().get(var4);
                        if (var5 != null && var5.equals(TreeViewFocusModel.this.getFocusedItem())) {
                           TreeViewFocusModel.this.focus(Math.max(0, TreeViewFocusModel.this.getFocusedIndex() - 1));
                           return;
                        }
                     }

                     if (var2 <= TreeViewFocusModel.this.getFocusedIndex()) {
                        var3 += var1.getTreeItem().isExpanded() ? -var1.getRemovedSize() : 0;
                     }
                  }
               } while (var1.getChange() != null && var1.getChange().next());

               if (var3 != 0) {
                  int var8 = TreeViewFocusModel.this.getFocusedIndex() + var3;
                  if (var8 >= 0) {
                     Platform.runLater(() -> TreeViewFocusModel.this.focus(var8));
                  }
               }
            }
         }
      };
      private WeakEventHandler<TreeItem.TreeModificationEvent<T>> weakTreeItemListener;

      public TreeViewFocusModel(TreeView<T> var1) {
         this.treeView = var1;
         this.treeView.rootProperty().addListener(this.weakRootPropertyListener);
         this.updateTreeEventListener(null, var1.getRoot());
         if (var1.getExpandedItemCount() > 0) {
            this.focus(0);
         }

         this.showRootListener = var1x -> {
            if (this.isFocused(0)) {
               this.focus(-1);
               this.focus(0);
            }
         };
         var1.showRootProperty().addListener(new WeakInvalidationListener(this.showRootListener));
         this.focusedIndexProperty().addListener(var1x -> var1.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM));
      }

      private void updateTreeEventListener(TreeItem<T> var1, TreeItem<T> var2) {
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
         return this.treeView == null ? -1 : this.treeView.getExpandedItemCount();
      }

      protected TreeItem<T> getModelItem(int var1) {
         if (this.treeView == null) {
            return null;
         } else {
            return var1 >= 0 && var1 < this.treeView.getExpandedItemCount() ? this.treeView.getTreeItem(var1) : null;
         }
      }

      @Override
      public void focus(int var1) {
         if (this.treeView.expandedItemCountDirty) {
            this.treeView.updateExpandedItemCount(this.treeView.getRoot());
         }

         super.focus(var1);
      }
   }
}
