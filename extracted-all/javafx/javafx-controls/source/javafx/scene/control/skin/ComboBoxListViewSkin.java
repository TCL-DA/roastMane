package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.ComboBoxListViewBehavior;
import java.util.function.Supplier;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ComboBoxListViewSkin<T> extends ComboBoxPopupControl<T> {
   private static final String COMBO_BOX_ROWS_TO_MEASURE_WIDTH_KEY = "comboBoxRowsToMeasureWidth";
   private final ComboBox<T> comboBox;
   private ObservableList<T> comboBoxItems;
   private ListCell<T> buttonCell;
   private Callback<ListView<T>, ListCell<T>> cellFactory;
   private final ListView<T> listView;
   private ObservableList<T> listViewItems;
   private boolean listSelectionLock = false;
   private boolean listViewSelectionDirty = false;
   private final ComboBoxListViewBehavior behavior;
   private boolean itemCountDirty;
   private final ListChangeListener<T> listViewItemsListener = new ListChangeListener<T>() {
      public void onChanged(Change<? extends T> var1) {
         ComboBoxListViewSkin.this.itemCountDirty = true;
         ComboBoxListViewSkin.this.getSkinnable().requestLayout();
      }
   };
   private final InvalidationListener itemsObserver;
   private final WeakListChangeListener<T> weakListViewItemsListener = new WeakListChangeListener(this.listViewItemsListener);
   private final BooleanProperty hideOnClick = new SimpleBooleanProperty(this, "hideOnClick", true);
   private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
   private static final PseudoClass PSEUDO_CLASS_EMPTY = PseudoClass.getPseudoClass("empty");
   private static final PseudoClass PSEUDO_CLASS_FILLED = PseudoClass.getPseudoClass("filled");

   public ComboBoxListViewSkin(ComboBox<T> var1) {
      super(var1);
      this.behavior = new ComboBoxListViewBehavior(var1);
      this.comboBox = var1;
      this.updateComboBoxItems();
      this.itemsObserver = var1x -> {
         this.updateComboBoxItems();
         this.updateListViewItems();
      };
      var1.itemsProperty().addListener(new WeakInvalidationListener(this.itemsObserver));
      this.listView = this.createListView();
      this.listView.setManaged(false);
      this.getChildren().add(this.listView);
      this.updateListViewItems();
      this.updateCellFactory();
      this.updateButtonCell();
      this.updateValue();
      this.registerChangeListener(var1.itemsProperty(), var1x -> {
         this.updateComboBoxItems();
         this.updateListViewItems();
      });
      this.registerChangeListener(var1.promptTextProperty(), var1x -> this.updateDisplayNode());
      this.registerChangeListener(var1.cellFactoryProperty(), var1x -> this.updateCellFactory());
      this.registerChangeListener(var1.visibleRowCountProperty(), var1x -> {
         if (this.listView != null) {
            this.listView.requestLayout();
         }
      });
      this.registerChangeListener(var1.converterProperty(), var1x -> this.updateListViewItems());
      this.registerChangeListener(var1.buttonCellProperty(), var1x -> {
         this.updateButtonCell();
         this.updateDisplayArea();
      });
      this.registerChangeListener(var1.valueProperty(), var2 -> {
         this.updateValue();
         var1.fireEvent(new ActionEvent());
      });
      this.registerChangeListener(var1.editableProperty(), var1x -> this.updateEditable());
      if (this.comboBox.isShowing()) {
         this.show();
      }

      this.comboBox.sceneProperty().addListener(var1x -> {
         if (((ObservableValue)var1x).getValue() == null) {
            this.comboBox.hide();
         }
      });
   }

   public final BooleanProperty hideOnClickProperty() {
      return this.hideOnClick;
   }

   public final boolean isHideOnClick() {
      return this.hideOnClick.get();
   }

   public final void setHideOnClick(boolean var1) {
      this.hideOnClick.set(var1);
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected TextField getEditor() {
      return this.getSkinnable().isEditable() ? ((ComboBox)this.getSkinnable()).getEditor() : null;
   }

   @Override
   protected StringConverter<T> getConverter() {
      return ((ComboBox)this.getSkinnable()).getConverter();
   }

   @Override
   public Node getDisplayNode() {
      Object var1;
      if (this.comboBox.isEditable()) {
         var1 = this.getEditableInputNode();
      } else {
         var1 = this.buttonCell;
      }

      this.updateDisplayNode();
      return (Node)var1;
   }

   @Override
   public Node getPopupContent() {
      return this.listView;
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      this.reconfigurePopup();
      return 50.0;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = super.computePrefWidth(var1, var3, var5, var7, var9);
      double var13 = this.listView.prefWidth(var1);
      double var15 = Math.max(var11, var13);
      this.reconfigurePopup();
      return var15;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      this.reconfigurePopup();
      return super.computeMaxWidth(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      this.reconfigurePopup();
      return super.computeMinHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      this.reconfigurePopup();
      return super.computePrefHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      this.reconfigurePopup();
      return super.computeMaxHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      if (this.listViewSelectionDirty) {
         try {
            this.listSelectionLock = true;
            Object var9 = this.comboBox.getSelectionModel().getSelectedItem();
            this.listView.getSelectionModel().clearSelection();
            this.listView.getSelectionModel().select((T)var9);
         } finally {
            this.listSelectionLock = false;
            this.listViewSelectionDirty = false;
         }
      }

      super.layoutChildren(var1, var3, var5, var7);
   }

   @Override
   void updateDisplayNode() {
      if (this.getEditor() != null) {
         super.updateDisplayNode();
      } else {
         Object var1 = this.comboBox.getValue();
         int var2 = this.getIndexOfComboBoxValueInItemsList();
         if (var2 > -1) {
            this.buttonCell.setItem(null);
            this.buttonCell.updateIndex(var2);
         } else {
            this.buttonCell.updateIndex(-1);
            boolean var3 = this.updateDisplayText(this.buttonCell, (T)var1, false);
            this.buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, var3);
            this.buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_FILLED, !var3);
            this.buttonCell.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
         }
      }
   }

   @Override
   ComboBoxBaseBehavior getBehavior() {
      return this.behavior;
   }

   private void updateComboBoxItems() {
      this.comboBoxItems = this.comboBox.getItems();
      this.comboBoxItems = this.comboBoxItems == null ? FXCollections.emptyObservableList() : this.comboBoxItems;
   }

   private void updateListViewItems() {
      if (this.listViewItems != null) {
         this.listViewItems.removeListener(this.weakListViewItemsListener);
      }

      this.listViewItems = this.comboBoxItems;
      this.listView.setItems(this.listViewItems);
      if (this.listViewItems != null) {
         this.listViewItems.addListener(this.weakListViewItemsListener);
      }

      this.itemCountDirty = true;
      this.getSkinnable().requestLayout();
   }

   private void updateValue() {
      Object var1 = this.comboBox.getValue();
      MultipleSelectionModel var2 = this.listView.getSelectionModel();
      int var3 = this.getIndexOfComboBoxValueInItemsList();
      if (var1 == null && var3 == -1) {
         var2.clearSelection();
      } else if (var3 == -1) {
         this.listSelectionLock = true;
         var2.clearSelection();
         this.listSelectionLock = false;
      } else {
         int var4 = this.comboBox.getSelectionModel().getSelectedIndex();
         if (var4 >= 0 && var4 < this.comboBoxItems.size()) {
            Object var6 = this.comboBoxItems.get(var4);
            if ((var6 == null || !var6.equals(var1)) && (var6 != null || var1 != null)) {
               var2.select(var1);
            } else {
               var2.select(var4);
            }
         } else {
            int var5 = this.comboBoxItems.indexOf(var1);
            if (var5 == -1) {
               this.updateDisplayNode();
            } else {
               var2.select(var5);
            }
         }
      }
   }

   private boolean updateDisplayText(ListCell<T> var1, T var2, boolean var3) {
      if (var3) {
         if (var1 == null) {
            return true;
         } else {
            var1.setGraphic(null);
            var1.setText(null);
            return true;
         }
      } else if (var2 instanceof Node) {
         Node var7 = var1.getGraphic();
         Node var8 = (Node)var2;
         if (var7 == null || !var7.equals(var8)) {
            var1.setText(null);
            var1.setGraphic(var8);
         }

         return var8 == null;
      } else {
         StringConverter var4 = this.comboBox.getConverter();
         String var5 = this.comboBox.getPromptText();
         String var6 = var2 == null && var5 != null ? var5 : (var4 == null ? (var2 == null ? null : var2.toString()) : var4.toString(var2));
         var1.setText(var6);
         var1.setGraphic(null);
         return var6 == null || var6.isEmpty();
      }
   }

   private int getIndexOfComboBoxValueInItemsList() {
      Object var1 = this.comboBox.getValue();
      return this.comboBoxItems.indexOf(var1);
   }

   private void updateButtonCell() {
      this.buttonCell = this.comboBox.getButtonCell() != null ? this.comboBox.getButtonCell() : (ListCell)this.getDefaultCellFactory().call(this.listView);
      this.buttonCell.setMouseTransparent(true);
      this.buttonCell.updateListView(this.listView);
      this.buttonCell.setAccessibleRole(AccessibleRole.NODE);
   }

   private void updateCellFactory() {
      Callback var1 = this.comboBox.getCellFactory();
      this.cellFactory = var1 != null ? var1 : this.getDefaultCellFactory();
      this.listView.setCellFactory(this.cellFactory);
   }

   private Callback<ListView<T>, ListCell<T>> getDefaultCellFactory() {
      return new Callback<ListView<T>, ListCell<T>>() {
         public ListCell<T> call(ListView<T> var1) {
            return new ListCell<T>() {
               @Override
               public void updateItem(T var1, boolean var2) {
                  super.updateItem(var1, var2);
                  ComboBoxListViewSkin.this.updateDisplayText(this, (T)var1, var2);
               }
            };
         }
      };
   }

   private ListView<T> createListView() {
      ListView var1 = new ListView<T>() {
         {
            this.getProperties().put("selectFirstRowByDefault", false);
            this.getProperties().put("editableComboBox", (Supplier<Boolean>)() -> ComboBoxListViewSkin.this.getSkinnable().isEditable());
         }

         @Override
         protected double computeMinHeight(double var1) {
            return 30.0;
         }

         @Override
         protected double computePrefWidth(double var1) {
            double var3;
            if (this.getSkin() instanceof ListViewSkin) {
               ListViewSkin var5 = (ListViewSkin)this.getSkin();
               if (ComboBoxListViewSkin.this.itemCountDirty) {
                  var5.updateItemCount();
                  ComboBoxListViewSkin.this.itemCountDirty = false;
               }

               int var6 = -1;
               if (ComboBoxListViewSkin.this.comboBox.getProperties().containsKey("comboBoxRowsToMeasureWidth")) {
                  var6 = (Integer)ComboBoxListViewSkin.this.comboBox.getProperties().get("comboBoxRowsToMeasureWidth");
               }

               var3 = Math.max(ComboBoxListViewSkin.this.comboBox.getWidth(), var5.getMaxCellWidth(var6) + 30.0);
            } else {
               var3 = Math.max(100.0, ComboBoxListViewSkin.this.comboBox.getWidth());
            }

            if (this.getItems().isEmpty() && this.getPlaceholder() != null) {
               var3 = Math.max(super.computePrefWidth(var1), var3);
            }

            return Math.max(50.0, var3);
         }

         @Override
         protected double computePrefHeight(double var1) {
            return ComboBoxListViewSkin.this.getListViewPrefHeight();
         }
      };
      var1.setId("list-view");
      var1.placeholderProperty().bind(this.comboBox.placeholderProperty());
      var1.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      var1.setFocusTraversable(false);
      var1.getSelectionModel().selectedIndexProperty().addListener(var1x -> {
         if (!this.listSelectionLock) {
            int var2 = this.listView.getSelectionModel().getSelectedIndex();
            this.comboBox.getSelectionModel().select(var2);
            this.updateDisplayNode();
            this.comboBox.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
         }
      });
      this.comboBox.getSelectionModel().selectedItemProperty().addListener(var1x -> this.listViewSelectionDirty = true);
      var1.addEventFilter(MouseEvent.MOUSE_RELEASED, var1x -> {
         EventTarget var2 = var1x.getTarget();
         if (var2 instanceof Parent) {
            ObservableList var3 = ((Parent)var2).getStyleClass();
            if (var3.contains("thumb") || var3.contains("track") || var3.contains("decrement-arrow") || var3.contains("increment-arrow")) {
               return;
            }
         }

         if (this.isHideOnClick()) {
            this.comboBox.hide();
         }
      });
      var1.setOnKeyPressed(var1x -> {
         if (var1x.getCode() == KeyCode.ENTER || var1x.getCode() == KeyCode.SPACE || var1x.getCode() == KeyCode.ESCAPE) {
            this.comboBox.hide();
         }
      });
      return var1;
   }

   private double getListViewPrefHeight() {
      double var1;
      if (this.listView.getSkin() instanceof VirtualContainerBase) {
         int var3 = this.comboBox.getVisibleRowCount();
         VirtualContainerBase var4 = (VirtualContainerBase)this.listView.getSkin();
         var1 = var4.getVirtualFlowPreferredHeight(var3);
      } else {
         double var5 = (double)(this.comboBoxItems.size() * 25);
         var1 = Math.min(var5, 200.0);
      }

      return var1;
   }

   ListView<T> getListView() {
      return this.listView;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            if (this.comboBox.isShowing()) {
               return this.listView.queryAccessibleAttribute(var1, var2);
            }

            return null;
         case TEXT:
            String var3 = this.comboBox.getAccessibleText();
            if (var3 != null && !var3.isEmpty()) {
               return var3;
            } else {
               String var4 = this.comboBox.isEditable() ? this.getEditor().getText() : this.buttonCell.getText();
               if (var4 == null || var4.isEmpty()) {
                  var4 = this.comboBox.getPromptText();
               }

               return var4;
            }
         case SELECTION_START:
            return this.getEditor() != null ? this.getEditor().getSelection().getStart() : null;
         case SELECTION_END:
            return this.getEditor() != null ? this.getEditor().getSelection().getEnd() : null;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
