package javafx.scene.control;

import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.ListViewSkin;
import javafx.util.Callback;
import javafx.util.Pair;

@DefaultProperty("items")
public class ListView<T> extends Control {
   private static final EventType<?> EDIT_ANY_EVENT = new EventType(Event.ANY, "LIST_VIEW_EDIT");
   private static final EventType<?> EDIT_START_EVENT = new EventType(editAnyEvent(), "EDIT_START");
   private static final EventType<?> EDIT_CANCEL_EVENT = new EventType(editAnyEvent(), "EDIT_CANCEL");
   private static final EventType<?> EDIT_COMMIT_EVENT = new EventType(editAnyEvent(), "EDIT_COMMIT");
   private boolean selectFirstRowByDefault = true;
   private EventHandler<ListView.EditEvent<T>> DEFAULT_EDIT_COMMIT_HANDLER = var1x -> {
      int var2 = var1x.getIndex();
      ObservableList var3 = this.getItems();
      if (var2 >= 0 && var2 < var3.size()) {
         var3.set(var2, var1x.getNewValue());
      }
   };
   private ObjectProperty<ObservableList<T>> items;
   private ObjectProperty<Node> placeholder;
   private ObjectProperty<MultipleSelectionModel<T>> selectionModel = new SimpleObjectProperty(this, "selectionModel");
   private ObjectProperty<FocusModel<T>> focusModel;
   private ObjectProperty<Orientation> orientation;
   private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;
   private DoubleProperty fixedCellSize;
   private BooleanProperty editable;
   private ReadOnlyIntegerWrapper editingIndex;
   private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditStart;
   private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCommit;
   private ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCancel;
   private ObjectProperty<EventHandler<ScrollToEvent<Integer>>> onScrollTo;
   private static final String DEFAULT_STYLE_CLASS = "list-view";
   private static final PseudoClass PSEUDO_CLASS_VERTICAL = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass PSEUDO_CLASS_HORIZONTAL = PseudoClass.getPseudoClass("horizontal");

   public static <T> EventType<ListView.EditEvent<T>> editAnyEvent() {
      return (EventType<ListView.EditEvent<T>>)EDIT_ANY_EVENT;
   }

   public static <T> EventType<ListView.EditEvent<T>> editStartEvent() {
      return (EventType<ListView.EditEvent<T>>)EDIT_START_EVENT;
   }

   public static <T> EventType<ListView.EditEvent<T>> editCancelEvent() {
      return (EventType<ListView.EditEvent<T>>)EDIT_CANCEL_EVENT;
   }

   public static <T> EventType<ListView.EditEvent<T>> editCommitEvent() {
      return (EventType<ListView.EditEvent<T>>)EDIT_COMMIT_EVENT;
   }

   public ListView() {
      this(FXCollections.observableArrayList());
   }

   public ListView(ObservableList<T> var1) {
      this.getStyleClass().setAll(new String[]{"list-view"});
      this.setAccessibleRole(AccessibleRole.LIST_VIEW);
      this.setItems(var1);
      this.setSelectionModel(new ListView.ListViewBitSetSelectionModel<>(this));
      this.setFocusModel(new ListView.ListViewFocusModel<>(this));
      this.setOnEditCommit(this.DEFAULT_EDIT_COMMIT_HANDLER);
      this.getProperties().addListener(var1x -> {
         if (var1x.wasAdded() && "selectFirstRowByDefault".equals(var1x.getKey())) {
            Boolean var2 = (Boolean)var1x.getValueAdded();
            if (var2 == null) {
               return;
            }

            this.selectFirstRowByDefault = var2;
         }
      });
      this.pseudoClassStateChanged(PSEUDO_CLASS_VERTICAL, true);
   }

   public final void setItems(ObservableList<T> var1) {
      this.itemsProperty().set(var1);
   }

   public final ObservableList<T> getItems() {
      return this.items == null ? null : (ObservableList)this.items.get();
   }

   public final ObjectProperty<ObservableList<T>> itemsProperty() {
      if (this.items == null) {
         this.items = new SimpleObjectProperty(this, "items");
      }

      return this.items;
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

   public final void setSelectionModel(MultipleSelectionModel<T> var1) {
      this.selectionModelProperty().set(var1);
   }

   public final MultipleSelectionModel<T> getSelectionModel() {
      return this.selectionModel == null ? null : (MultipleSelectionModel)this.selectionModel.get();
   }

   public final ObjectProperty<MultipleSelectionModel<T>> selectionModelProperty() {
      return this.selectionModel;
   }

   public final void setFocusModel(FocusModel<T> var1) {
      this.focusModelProperty().set(var1);
   }

   public final FocusModel<T> getFocusModel() {
      return this.focusModel == null ? null : (FocusModel)this.focusModel.get();
   }

   public final ObjectProperty<FocusModel<T>> focusModelProperty() {
      if (this.focusModel == null) {
         this.focusModel = new SimpleObjectProperty(this, "focusModel");
      }

      return this.focusModel;
   }

   public final void setOrientation(Orientation var1) {
      this.orientationProperty().set(var1);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.VERTICAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty<Orientation>(Orientation.VERTICAL) {
            public void invalidated() {
               boolean var1 = this.get() == Orientation.VERTICAL;
               ListView.this.pseudoClassStateChanged(ListView.PSEUDO_CLASS_VERTICAL, var1);
               ListView.this.pseudoClassStateChanged(ListView.PSEUDO_CLASS_HORIZONTAL, !var1);
            }

            public CssMetaData<ListView<?>, Orientation> getCssMetaData() {
               return ListView.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return ListView.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public final void setCellFactory(Callback<ListView<T>, ListCell<T>> var1) {
      this.cellFactoryProperty().set(var1);
   }

   public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
      return this.cellFactory == null ? null : (Callback)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
      if (this.cellFactory == null) {
         this.cellFactory = new SimpleObjectProperty(this, "cellFactory");
      }

      return this.cellFactory;
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
            public CssMetaData<ListView<?>, Number> getCssMetaData() {
               return ListView.StyleableProperties.FIXED_CELL_SIZE;
            }

            public Object getBean() {
               return ListView.this;
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

   private void setEditingIndex(int var1) {
      this.editingIndexPropertyImpl().set(var1);
   }

   public final int getEditingIndex() {
      return this.editingIndex == null ? -1 : this.editingIndex.get();
   }

   public final ReadOnlyIntegerProperty editingIndexProperty() {
      return this.editingIndexPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyIntegerWrapper editingIndexPropertyImpl() {
      if (this.editingIndex == null) {
         this.editingIndex = new ReadOnlyIntegerWrapper(this, "editingIndex", -1);
      }

      return this.editingIndex;
   }

   public final void setOnEditStart(EventHandler<ListView.EditEvent<T>> var1) {
      this.onEditStartProperty().set(var1);
   }

   public final EventHandler<ListView.EditEvent<T>> getOnEditStart() {
      return this.onEditStart == null ? null : (EventHandler)this.onEditStart.get();
   }

   public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditStartProperty() {
      if (this.onEditStart == null) {
         this.onEditStart = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
            protected void invalidated() {
               ListView.this.setEventHandler(ListView.editStartEvent(), (EventHandler)this.get());
            }

            public Object getBean() {
               return ListView.this;
            }

            public String getName() {
               return "onEditStart";
            }
         };
      }

      return this.onEditStart;
   }

   public final void setOnEditCommit(EventHandler<ListView.EditEvent<T>> var1) {
      this.onEditCommitProperty().set(var1);
   }

   public final EventHandler<ListView.EditEvent<T>> getOnEditCommit() {
      return this.onEditCommit == null ? null : (EventHandler)this.onEditCommit.get();
   }

   public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCommitProperty() {
      if (this.onEditCommit == null) {
         this.onEditCommit = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
            protected void invalidated() {
               ListView.this.setEventHandler(ListView.editCommitEvent(), (EventHandler)this.get());
            }

            public Object getBean() {
               return ListView.this;
            }

            public String getName() {
               return "onEditCommit";
            }
         };
      }

      return this.onEditCommit;
   }

   public final void setOnEditCancel(EventHandler<ListView.EditEvent<T>> var1) {
      this.onEditCancelProperty().set(var1);
   }

   public final EventHandler<ListView.EditEvent<T>> getOnEditCancel() {
      return this.onEditCancel == null ? null : (EventHandler)this.onEditCancel.get();
   }

   public final ObjectProperty<EventHandler<ListView.EditEvent<T>>> onEditCancelProperty() {
      if (this.onEditCancel == null) {
         this.onEditCancel = new ObjectPropertyBase<EventHandler<ListView.EditEvent<T>>>() {
            protected void invalidated() {
               ListView.this.setEventHandler(ListView.editCancelEvent(), (EventHandler)this.get());
            }

            public Object getBean() {
               return ListView.this;
            }

            public String getName() {
               return "onEditCancel";
            }
         };
      }

      return this.onEditCancel;
   }

   public void edit(int var1) {
      if (this.isEditable()) {
         this.setEditingIndex(var1);
      }
   }

   public void scrollTo(int var1) {
      ControlUtils.scrollToIndex(this, var1);
   }

   public void scrollTo(T var1) {
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
               ListView.this.setEventHandler(ScrollToEvent.scrollToTopIndex(), (EventHandler)this.get());
            }

            public Object getBean() {
               return ListView.this;
            }

            public String getName() {
               return "onScrollTo";
            }
         };
      }

      return this.onScrollTo;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ListViewSkin<>(this);
   }

   public void refresh() {
      this.getProperties().put("recreateKey", Boolean.TRUE);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ListView.StyleableProperties.STYLEABLES;
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
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   public static class EditEvent<T> extends Event {
      private final T newValue;
      private final int editIndex;
      private final ListView<T> source;
      private static final long serialVersionUID = 20130724L;
      public static final EventType<?> ANY = ListView.EDIT_ANY_EVENT;

      public EditEvent(ListView<T> var1, EventType<? extends ListView.EditEvent<T>> var2, T var3, int var4) {
         super(var1, Event.NULL_SOURCE_TARGET, var2);
         this.source = var1;
         this.editIndex = var4;
         this.newValue = (T)var3;
      }

      public ListView<T> getSource() {
         return this.source;
      }

      public int getIndex() {
         return this.editIndex;
      }

      public T getNewValue() {
         return this.newValue;
      }

      public String toString() {
         return "ListViewEditEvent [ newValue: " + this.getNewValue() + ", ListView: " + this.getSource() + " ]";
      }
   }

   static class ListViewBitSetSelectionModel<T> extends MultipleSelectionModelBase<T> {
      private final ListChangeListener<T> itemsContentObserver = new ListChangeListener<T>() {
         public void onChanged(Change<? extends T> var1) {
            ListViewBitSetSelectionModel.this.updateItemCount();
            boolean var2 = true;

            while (var1.next()) {
               Object var3 = ListViewBitSetSelectionModel.this.getSelectedItem();
               int var4 = ListViewBitSetSelectionModel.this.getSelectedIndex();
               if (ListViewBitSetSelectionModel.this.listView.getItems() == null || ListViewBitSetSelectionModel.this.listView.getItems().isEmpty()) {
                  ListViewBitSetSelectionModel.this.selectedItemChange = var1;
                  ListViewBitSetSelectionModel.this.clearSelection();
                  ListViewBitSetSelectionModel.this.selectedItemChange = null;
               } else if (var4 == -1 && var3 != null) {
                  int var7 = ListViewBitSetSelectionModel.this.listView.getItems().indexOf(var3);
                  if (var7 != -1) {
                     ListViewBitSetSelectionModel.this.setSelectedIndex(var7);
                     var2 = false;
                  }
               } else if (var1.wasRemoved()
                  && var1.getRemovedSize() == 1
                  && !var1.wasAdded()
                  && var3 != null
                  && var3.equals(var1.getRemoved().get(0))
                  && ListViewBitSetSelectionModel.this.getSelectedIndex() < ListViewBitSetSelectionModel.this.getItemCount()) {
                  int var5 = var4 == 0 ? 0 : var4 - 1;
                  Object var6 = ListViewBitSetSelectionModel.this.getModelItem(var5);
                  if (!var3.equals(var6)) {
                     ListViewBitSetSelectionModel.this.startAtomic();
                     ListViewBitSetSelectionModel.this.clearSelection(var4);
                     ListViewBitSetSelectionModel.this.stopAtomic();
                     ListViewBitSetSelectionModel.this.select(var6);
                  }
               }
            }

            if (var2) {
               ListViewBitSetSelectionModel.this.updateSelection(var1);
            }
         }
      };
      private final InvalidationListener itemsObserver;
      private WeakListChangeListener<T> weakItemsContentObserver = new WeakListChangeListener(this.itemsContentObserver);
      private final ListView<T> listView;
      private int itemCount = 0;
      private int previousModelSize = 0;

      public ListViewBitSetSelectionModel(final ListView<T> var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("ListView can not be null");
         } else {
            this.listView = var1;
            this.itemsObserver = new InvalidationListener() {
               private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(var1.getItems());

               public void invalidated(Observable var1x) {
                  ObservableList var2 = this.weakItemsRef.get();
                  this.weakItemsRef = new WeakReference<>(var1.getItems());
                  ListViewBitSetSelectionModel.this.updateItemsObserver(var2, var1.getItems());
               }
            };
            this.listView.itemsProperty().addListener(new WeakInvalidationListener(this.itemsObserver));
            if (var1.getItems() != null) {
               this.listView.getItems().addListener(this.weakItemsContentObserver);
            }

            this.updateItemCount();
            this.updateDefaultSelection();
         }
      }

      private void updateSelection(Change<? extends T> var1) {
         var1.reset();
         ArrayList var2 = new ArrayList();

         while (var1.next()) {
            if (var1.wasReplaced()) {
               if (var1.getList().isEmpty()) {
                  this.clearSelection();
               } else {
                  int var11 = this.getSelectedIndex();
                  if (this.previousModelSize == var1.getRemovedSize()) {
                     this.clearSelection();
                  } else if (var11 < this.getItemCount() && var11 >= 0) {
                     this.startAtomic();
                     this.clearSelection(var11);
                     this.stopAtomic();
                     this.select(var11);
                  } else {
                     this.clearSelection();
                  }
               }
            } else if (!var1.wasAdded() && !var1.wasRemoved()) {
               if (var1.wasPermutated()) {
                  int var10 = var1.getTo() - var1.getFrom();
                  HashMap var4 = new HashMap(var10);

                  for (int var5 = var1.getFrom(); var5 < var1.getTo(); var5++) {
                     var4.put(var5, var1.getPermutation(var5));
                  }

                  ArrayList var12 = new ArrayList(this.getSelectedIndices());
                  this.clearSelection();
                  ArrayList var6 = new ArrayList(this.getSelectedIndices().size());

                  for (int var7 = 0; var7 < var12.size(); var7++) {
                     int var8 = (Integer)var12.get(var7);
                     if (var4.containsKey(var8)) {
                        Integer var9 = (Integer)var4.get(var8);
                        var6.add(var9);
                     }
                  }

                  if (!var6.isEmpty()) {
                     if (var6.size() == 1) {
                        this.select((Integer)var6.get(0));
                     } else {
                        int[] var13 = new int[var6.size() - 1];

                        for (int var14 = 0; var14 < var6.size() - 1; var14++) {
                           var13[var14] = (Integer)var6.get(var14 + 1);
                        }

                        this.selectIndices((Integer)var6.get(0), var13);
                     }
                  }
               }
            } else {
               int var3 = var1.wasAdded() ? var1.getAddedSize() : -var1.getRemovedSize();
               var2.add(new Pair(var1.getFrom(), var3));
            }
         }

         if (!var2.isEmpty()) {
            this.shiftSelection(var2, null);
         }

         this.previousModelSize = this.getItemCount();
      }

      @Override
      public void selectAll() {
         int var1 = (Integer)ListCellBehavior.<Integer>getAnchor(this.listView, Integer.valueOf(-1));
         super.selectAll();
         ListCellBehavior.setAnchor(this.listView, Integer.valueOf(var1), false);
      }

      @Override
      public void clearAndSelect(int var1) {
         ListCellBehavior.setAnchor(this.listView, Integer.valueOf(var1), false);
         super.clearAndSelect(var1);
      }

      @Override
      protected void focus(int var1) {
         if (this.listView.getFocusModel() != null) {
            this.listView.getFocusModel().focus(var1);
            this.listView.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
         }
      }

      @Override
      protected int getFocusedIndex() {
         return this.listView.getFocusModel() == null ? -1 : this.listView.getFocusModel().getFocusedIndex();
      }

      @Override
      protected int getItemCount() {
         return this.itemCount;
      }

      @Override
      protected T getModelItem(int var1) {
         ObservableList var2 = this.listView.getItems();
         if (var2 == null) {
            return null;
         } else {
            return (T)(var1 >= 0 && var1 < this.itemCount ? var2.get(var1) : null);
         }
      }

      private void updateItemCount() {
         if (this.listView == null) {
            this.itemCount = -1;
         } else {
            ObservableList var1 = this.listView.getItems();
            this.itemCount = var1 == null ? -1 : var1.size();
         }
      }

      private void updateItemsObserver(ObservableList<T> var1, ObservableList<T> var2) {
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentObserver);
         }

         if (var2 != null) {
            var2.addListener(this.weakItemsContentObserver);
         }

         this.updateItemCount();
         this.updateDefaultSelection();
      }

      private void updateDefaultSelection() {
         int var1 = -1;
         int var2 = -1;
         if (this.listView.getItems() != null) {
            Object var3 = this.getSelectedItem();
            if (var3 != null) {
               var1 = this.listView.getItems().indexOf(var3);
               var2 = var1;
            }

            if (this.listView.selectFirstRowByDefault && var2 == -1) {
               var2 = this.listView.getItems().size() > 0 ? 0 : -1;
            }
         }

         this.clearSelection();
         this.select(var1);
      }
   }

   static class ListViewFocusModel<T> extends FocusModel<T> {
      private final ListView<T> listView;
      private int itemCount = 0;
      private final InvalidationListener itemsObserver;
      private final ListChangeListener<T> itemsContentListener = var1x -> {
         this.updateItemCount();

         while (var1x.next()) {
            int var2 = var1x.getFrom();
            if (!var1x.wasReplaced() && var1x.getAddedSize() != this.getItemCount()) {
               if (this.getFocusedIndex() != -1 && var2 <= this.getFocusedIndex()) {
                  var1x.reset();
                  boolean var3 = false;
                  boolean var4 = false;
                  int var5 = 0;

                  int var6;
                  for (var6 = 0; var1x.next(); var6 += var1x.getRemovedSize()) {
                     var3 |= var1x.wasAdded();
                     var4 |= var1x.wasRemoved();
                     var5 += var1x.getAddedSize();
                  }

                  if (var3 && !var4) {
                     this.focus(Math.min(this.getItemCount() - 1, this.getFocusedIndex() + var5));
                     continue;
                  }

                  if (!var3 && var4) {
                     this.focus(Math.max(0, this.getFocusedIndex() - var6));
                  }
                  continue;
               }

               return;
            }

            this.updateDefaultFocus();
            return;
         }
      };
      private WeakListChangeListener<T> weakItemsContentListener = new WeakListChangeListener(this.itemsContentListener);

      public ListViewFocusModel(final ListView<T> var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("ListView can not be null");
         } else {
            this.listView = var1;
            this.itemsObserver = new InvalidationListener() {
               private WeakReference<ObservableList<T>> weakItemsRef = new WeakReference<>(var1.getItems());

               public void invalidated(Observable var1x) {
                  ObservableList var2 = this.weakItemsRef.get();
                  this.weakItemsRef = new WeakReference<>(var1.getItems());
                  ListViewFocusModel.this.updateItemsObserver(var2, var1.getItems());
               }
            };
            this.listView.itemsProperty().addListener(new WeakInvalidationListener(this.itemsObserver));
            if (var1.getItems() != null) {
               this.listView.getItems().addListener(this.weakItemsContentListener);
            }

            this.updateItemCount();
            this.updateDefaultFocus();
            this.focusedIndexProperty().addListener(var1x -> var1.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM));
         }
      }

      private void updateItemsObserver(ObservableList<T> var1, ObservableList<T> var2) {
         if (var1 != null) {
            var1.removeListener(this.weakItemsContentListener);
         }

         if (var2 != null) {
            var2.addListener(this.weakItemsContentListener);
         }

         this.updateItemCount();
         this.updateDefaultFocus();
      }

      @Override
      protected int getItemCount() {
         return this.itemCount;
      }

      @Override
      protected T getModelItem(int var1) {
         if (this.isEmpty()) {
            return null;
         } else {
            return (T)(var1 >= 0 && var1 < this.itemCount ? this.listView.getItems().get(var1) : null);
         }
      }

      private boolean isEmpty() {
         return this.itemCount == -1;
      }

      private void updateItemCount() {
         if (this.listView == null) {
            this.itemCount = -1;
         } else {
            ObservableList var1 = this.listView.getItems();
            this.itemCount = var1 == null ? -1 : var1.size();
         }
      }

      private void updateDefaultFocus() {
         int var1 = -1;
         if (this.listView.getItems() != null) {
            Object var2 = this.getFocusedItem();
            if (var2 != null) {
               var1 = this.listView.getItems().indexOf(var2);
            }

            if (var1 == -1) {
               var1 = this.listView.getItems().size() > 0 ? 0 : -1;
            }
         }

         this.focus(var1);
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<ListView<?>, Orientation> ORIENTATION = new CssMetaData<ListView<?>, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.VERTICAL
      ) {
         public Orientation getInitialValue(ListView<?> var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(ListView<?> var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(ListView<?> var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final CssMetaData<ListView<?>, Number> FIXED_CELL_SIZE = new CssMetaData<ListView<?>, Number>(
         "-fx-fixed-cell-size", SizeConverter.getInstance(), -1.0
      ) {
         public Double getInitialValue(ListView<?> var1) {
            return var1.getFixedCellSize();
         }

         public boolean isSettable(ListView<?> var1) {
            return var1.fixedCellSize == null || !var1.fixedCellSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ListView<?> var1) {
            return (StyleableProperty<Number>)var1.fixedCellSizeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(ORIENTATION);
         var0.add(FIXED_CELL_SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
