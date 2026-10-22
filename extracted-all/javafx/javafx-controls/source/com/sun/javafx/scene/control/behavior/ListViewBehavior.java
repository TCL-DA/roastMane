package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.FocusModel;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class ListViewBehavior<T> extends BehaviorBase<ListView<T>> {
   private final InputMap<ListView<T>> listViewInputMap;
   private final EventHandler<KeyEvent> keyEventListener = var1x -> {
      if (!var1x.isConsumed()) {
         this.isShiftDown = var1x.getEventType() == KeyEvent.KEY_PRESSED && var1x.isShiftDown();
         this.isShortcutDown = var1x.getEventType() == KeyEvent.KEY_PRESSED && var1x.isShortcutDown();
      }
   };
   private boolean isShiftDown = false;
   private boolean isShortcutDown = false;
   private Callback<Boolean, Integer> onScrollPageUp;
   private Callback<Boolean, Integer> onScrollPageDown;
   private Runnable onFocusPreviousRow;
   private Runnable onFocusNextRow;
   private Runnable onSelectPreviousRow;
   private Runnable onSelectNextRow;
   private Runnable onMoveToFirstCell;
   private Runnable onMoveToLastCell;
   private boolean selectionChanging = false;
   private final ListChangeListener<Integer> selectedIndicesListener = var1x -> {
      int var2x = this.getAnchor();

      while (var1x.next()) {
         if (var1x.wasReplaced() && ListCellBehavior.hasDefaultAnchor(this.getNode())) {
            ListCellBehavior.removeAnchor(this.getNode());
         } else {
            int var3x = var1x.wasPermutated() ? var1x.getTo() - var1x.getFrom() : 0;
            MultipleSelectionModel var4x = this.getNode().getSelectionModel();
            if (!this.selectionChanging) {
               if (var4x.isEmpty()) {
                  var2x = -1;
               } else if (this.hasAnchor() && !var4x.isSelected(this.getAnchor() + var3x)) {
                  var2x = -1;
               }
            }

            if (var2x == -1) {
               int var5x = var1x.getAddedSize();
               var2x = var5x > 0 ? (Integer)var1x.getAddedSubList().get(var5x - 1) : var2x;
            }
         }
      }

      if (var2x > -1) {
         this.setAnchor(var2x);
      }
   };
   private final ListChangeListener<T> itemsListListener = var1x -> {
      while (var1x.next()) {
         if (this.hasAnchor()) {
            int var2x = this.hasAnchor() ? this.getAnchor() : 0;
            if (var1x.wasAdded() && var1x.getFrom() <= var2x) {
               var2x += var1x.getAddedSize();
            } else if (var1x.wasRemoved() && var1x.getFrom() <= var2x) {
               var2x -= var1x.getRemovedSize();
            }

            this.setAnchor(var2x < 0 ? 0 : var2x);
         }
      }
   };
   private final ChangeListener<ObservableList<T>> itemsListener = new ChangeListener<ObservableList<T>>() {
      public void changed(ObservableValue<? extends ObservableList<T>> var1, ObservableList<T> var2, ObservableList<T> var3) {
         if (var2 != null) {
            var2.removeListener(ListViewBehavior.this.weakItemsListListener);
         }

         if (var3 != null) {
            var3.addListener(ListViewBehavior.this.weakItemsListListener);
         }
      }
   };
   private final ChangeListener<MultipleSelectionModel<T>> selectionModelListener = new ChangeListener<MultipleSelectionModel<T>>() {
      public void changed(ObservableValue<? extends MultipleSelectionModel<T>> var1, MultipleSelectionModel<T> var2, MultipleSelectionModel<T> var3) {
         if (var2 != null) {
            var2.getSelectedIndices().removeListener(ListViewBehavior.this.weakSelectedIndicesListener);
         }

         if (var3 != null) {
            var3.getSelectedIndices().addListener(ListViewBehavior.this.weakSelectedIndicesListener);
         }
      }
   };
   private final WeakChangeListener<ObservableList<T>> weakItemsListener = new WeakChangeListener(this.itemsListener);
   private final WeakListChangeListener<Integer> weakSelectedIndicesListener = new WeakListChangeListener(this.selectedIndicesListener);
   private final WeakListChangeListener<T> weakItemsListListener = new WeakListChangeListener(this.itemsListListener);
   private final WeakChangeListener<MultipleSelectionModel<T>> weakSelectionModelListener = new WeakChangeListener(this.selectionModelListener);
   private TwoLevelFocusListBehavior tlFocus;

   public ListViewBehavior(ListView<T> var1) {
      super(var1);
      this.listViewInputMap = this.createInputMap();
      Supplier var2 = (Supplier)var1.getProperties().get("editableComboBox");
      Predicate var3 = var1x -> var2 != null;
      Predicate var4 = var1x -> var2 != null && (Boolean)var2.get();
      if (var2 == null) {
         this.addDefaultMapping(this.listViewInputMap, FocusTraversalInputMap.getFocusTraversalMappings());
      }

      this.addDefaultMapping(
         this.listViewInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME), var1x -> this.selectFirstRow(), var4),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END), var1x -> this.selectLastRow(), var4),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.selectAllToFirstRow(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.selectAllToLastRow(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shift(), var1x -> this.selectAllPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shift(), var1x -> this.selectAllPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shift(), var1x -> this.selectAllToFocus(false)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().shift(), var1x -> this.selectAllToFocus(true)),
            new InputMap.KeyMapping(KeyCode.PAGE_UP, var1x -> this.scrollPageUp()),
            new InputMap.KeyMapping(KeyCode.PAGE_DOWN, var1x -> this.scrollPageDown()),
            new InputMap.KeyMapping(KeyCode.ENTER, var1x -> this.activate()),
            new InputMap.KeyMapping(KeyCode.SPACE, var1x -> this.activate()),
            new InputMap.KeyMapping(KeyCode.F2, var1x -> this.activate()),
            new InputMap.KeyMapping(KeyCode.ESCAPE, var1x -> this.cancelEdit()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.A).shortcut(), var1x -> this.selectAll(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut(), var1x -> this.focusFirstRow(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut(), var1x -> this.focusLastRow(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut(), var1x -> this.focusPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut(), var1x -> this.focusPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.BACK_SLASH).shortcut(), var1x -> this.clearSelection()),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
      InputMap var5 = new InputMap(var1);
      var5.setInterceptor(var0 -> !PlatformUtil.isMac());
      this.addDefaultMapping(
         var5, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.listViewInputMap, var5);
      InputMap var6 = new InputMap(var1);
      var6.setInterceptor(var0 -> PlatformUtil.isMac());
      this.addDefaultMapping(
         var6, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.listViewInputMap, var6);
      InputMap var7 = new InputMap(var1);
      var7.setInterceptor(var1x -> var1.getOrientation() != Orientation.VERTICAL);
      this.addDefaultMapping(
         var7,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.KP_UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(KeyCode.KP_DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_UP).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_DOWN).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut(), var1x -> this.focusPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut(), var1x -> this.focusNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut().shift(), var1x -> this.discontinuousSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut().shift(), var1x -> this.discontinuousSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut().shift(), var1x -> this.discontinuousSelectPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut().shift(), var1x -> this.discontinuousSelectPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut().shift(), var1x -> this.discontinuousSelectAllToFirstRow(), var3),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut().shift(), var1x -> this.discontinuousSelectAllToLastRow(), var3)
         }
      );
      this.addDefaultChildMap(this.listViewInputMap, var7);
      InputMap var8 = new InputMap(var1);
      var8.setInterceptor(var1x -> var1.getOrientation() != Orientation.HORIZONTAL);
      this.addDefaultMapping(
         var8,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.LEFT, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.KP_LEFT, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.RIGHT, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(KeyCode.KP_RIGHT, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_LEFT).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_RIGHT).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shortcut(), var1x -> this.focusPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shortcut(), var1x -> this.focusNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shortcut().shift(), var1x -> this.discontinuousSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shortcut().shift(), var1x -> this.discontinuousSelectNextRow())
         }
      );
      this.addDefaultChildMap(this.listViewInputMap, var8);
      var1.addEventFilter(KeyEvent.ANY, this.keyEventListener);
      var1.itemsProperty().addListener(this.weakItemsListener);
      if (var1.getItems() != null) {
         var1.getItems().addListener(this.weakItemsListListener);
      }

      var1.selectionModelProperty().addListener(this.weakSelectionModelListener);
      if (var1.getSelectionModel() != null) {
         var1.getSelectionModel().getSelectedIndices().addListener(this.weakSelectedIndicesListener);
      }

      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusListBehavior(var1);
      }
   }

   @Override
   public InputMap<ListView<T>> getInputMap() {
      return this.listViewInputMap;
   }

   @Override
   public void dispose() {
      ListView var1 = this.getNode();
      ListCellBehavior.removeAnchor(var1);
      var1.selectionModelProperty().removeListener(this.weakSelectionModelListener);
      if (var1.getSelectionModel() != null) {
         var1.getSelectionModel().getSelectedIndices().removeListener(this.weakSelectedIndicesListener);
      }

      var1.itemsProperty().removeListener(this.weakItemsListener);
      if (var1.getItems() != null) {
         var1.getItems().removeListener(this.weakItemsListListener);
      }

      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      var1.removeEventFilter(KeyEvent.ANY, this.keyEventListener);
      super.dispose();
   }

   public void setOnScrollPageUp(Callback<Boolean, Integer> var1) {
      this.onScrollPageUp = var1;
   }

   public void setOnScrollPageDown(Callback<Boolean, Integer> var1) {
      this.onScrollPageDown = var1;
   }

   public void setOnFocusPreviousRow(Runnable var1) {
      this.onFocusPreviousRow = var1;
   }

   public void setOnFocusNextRow(Runnable var1) {
      this.onFocusNextRow = var1;
   }

   public void setOnSelectPreviousRow(Runnable var1) {
      this.onSelectPreviousRow = var1;
   }

   public void setOnSelectNextRow(Runnable var1) {
      this.onSelectNextRow = var1;
   }

   public void setOnMoveToFirstCell(Runnable var1) {
      this.onMoveToFirstCell = var1;
   }

   public void setOnMoveToLastCell(Runnable var1) {
      this.onMoveToLastCell = var1;
   }

   private void setAnchor(int var1) {
      ListCellBehavior.setAnchor(this.getNode(), var1 < 0 ? null : var1, false);
   }

   private int getAnchor() {
      return (Integer)ListCellBehavior.<Integer>getAnchor(this.getNode(), Integer.valueOf(this.getNode().getFocusModel().getFocusedIndex()));
   }

   private boolean hasAnchor() {
      return ListCellBehavior.hasNonDefaultAnchor(this.getNode());
   }

   private void mousePressed(MouseEvent var1) {
      if (!var1.isShiftDown() && !var1.isSynthesized()) {
         int var2 = this.getNode().getSelectionModel().getSelectedIndex();
         this.setAnchor(var2);
      }

      if (!this.getNode().isFocused() && this.getNode().isFocusTraversable()) {
         this.getNode().requestFocus();
      }
   }

   private int getRowCount() {
      return this.getNode().getItems() == null ? 0 : this.getNode().getItems().size();
   }

   private void clearSelection() {
      this.getNode().getSelectionModel().clearSelection();
   }

   private void scrollPageUp() {
      int var1 = -1;
      if (this.onScrollPageUp != null) {
         var1 = (Integer)this.onScrollPageUp.call(false);
      }

      if (var1 != -1) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            var2.clearAndSelect(var1);
         }
      }
   }

   private void scrollPageDown() {
      int var1 = -1;
      if (this.onScrollPageDown != null) {
         var1 = (Integer)this.onScrollPageDown.call(false);
      }

      if (var1 != -1) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            var2.clearAndSelect(var1);
         }
      }
   }

   private void focusFirstRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         var1.focus(0);
         if (this.onMoveToFirstCell != null) {
            this.onMoveToFirstCell.run();
         }
      }
   }

   private void focusLastRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         var1.focus(this.getRowCount() - 1);
         if (this.onMoveToLastCell != null) {
            this.onMoveToLastCell.run();
         }
      }
   }

   private void focusPreviousRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            var1.focusPrevious();
            if (!this.isShortcutDown || this.getAnchor() == -1) {
               this.setAnchor(var1.getFocusedIndex());
            }

            if (this.onFocusPreviousRow != null) {
               this.onFocusPreviousRow.run();
            }
         }
      }
   }

   private void focusNextRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            var1.focusNext();
            if (!this.isShortcutDown || this.getAnchor() == -1) {
               this.setAnchor(var1.getFocusedIndex());
            }

            if (this.onFocusNextRow != null) {
               this.onFocusNextRow.run();
            }
         }
      }
   }

   private void focusPageUp() {
      int var1 = (Integer)this.onScrollPageUp.call(true);
      FocusModel var2 = this.getNode().getFocusModel();
      if (var2 != null) {
         var2.focus(var1);
      }
   }

   private void focusPageDown() {
      int var1 = (Integer)this.onScrollPageDown.call(true);
      FocusModel var2 = this.getNode().getFocusModel();
      if (var2 != null) {
         var2.focus(var1);
      }
   }

   private void alsoSelectPreviousRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            if (this.isShiftDown && this.getAnchor() != -1) {
               int var3 = var1.getFocusedIndex() - 1;
               if (var3 < 0) {
                  return;
               }

               int var4 = this.getAnchor();
               if (!this.hasAnchor()) {
                  this.setAnchor(var1.getFocusedIndex());
               }

               if (var2.getSelectedIndices().size() > 1) {
                  this.clearSelectionOutsideRange(var4, var3);
               }

               if (var4 > var3) {
                  var2.selectRange(var4, var3 - 1);
               } else {
                  var2.selectRange(var4, var3 + 1);
               }
            } else {
               var2.selectPrevious();
            }

            this.onSelectPreviousRow.run();
         }
      }
   }

   private void alsoSelectNextRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         MultipleSelectionModel var2 = this.getNode().getSelectionModel();
         if (var2 != null) {
            if (this.isShiftDown && this.getAnchor() != -1) {
               int var3 = var1.getFocusedIndex() + 1;
               int var4 = this.getAnchor();
               if (!this.hasAnchor()) {
                  this.setAnchor(var1.getFocusedIndex());
               }

               if (var2.getSelectedIndices().size() > 1) {
                  this.clearSelectionOutsideRange(var4, var3);
               }

               if (var4 > var3) {
                  var2.selectRange(var4, var3 - 1);
               } else {
                  var2.selectRange(var4, var3 + 1);
               }
            } else {
               var2.selectNext();
            }

            this.onSelectNextRow.run();
         }
      }
   }

   private void clearSelectionOutsideRange(int var1, int var2) {
      MultipleSelectionModel var3 = this.getNode().getSelectionModel();
      if (var3 != null) {
         int var4 = Math.min(var1, var2);
         int var5 = Math.max(var1, var2);
         ArrayList var6 = new ArrayList(var3.getSelectedIndices());
         this.selectionChanging = true;

         for (int var7 = 0; var7 < var6.size(); var7++) {
            int var8 = (Integer)var6.get(var7);
            if (var8 < var4 || var8 > var5) {
               var3.clearSelection(var8);
            }
         }

         this.selectionChanging = false;
      }
   }

   private void selectPreviousRow() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         int var2 = var1.getFocusedIndex();
         if (var2 > 0) {
            this.setAnchor(var2 - 1);
            this.getNode().getSelectionModel().clearAndSelect(var2 - 1);
            this.onSelectPreviousRow.run();
         }
      }
   }

   private void selectNextRow() {
      ListView var1 = this.getNode();
      FocusModel var2 = var1.getFocusModel();
      if (var2 != null) {
         int var3 = var2.getFocusedIndex();
         if (var3 != this.getRowCount() - 1) {
            MultipleSelectionModel var4 = var1.getSelectionModel();
            if (var4 != null) {
               this.setAnchor(var3 + 1);
               var4.clearAndSelect(var3 + 1);
               if (this.onSelectNextRow != null) {
                  this.onSelectNextRow.run();
               }
            }
         }
      }
   }

   private void selectFirstRow() {
      if (this.getRowCount() > 0) {
         this.getNode().getSelectionModel().clearAndSelect(0);
         if (this.onMoveToFirstCell != null) {
            this.onMoveToFirstCell.run();
         }
      }
   }

   private void selectLastRow() {
      this.getNode().getSelectionModel().clearAndSelect(this.getRowCount() - 1);
      if (this.onMoveToLastCell != null) {
         this.onMoveToLastCell.run();
      }
   }

   private void selectAllPageUp() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         int var2 = var1.getFocusedIndex();
         if (this.isShiftDown) {
            var2 = this.getAnchor() == -1 ? var2 : this.getAnchor();
            this.setAnchor(var2);
         }

         int var3 = (Integer)this.onScrollPageUp.call(false);
         int var4 = var2 < var3 ? 1 : -1;
         MultipleSelectionModel var5 = this.getNode().getSelectionModel();
         if (var5 != null) {
            this.selectionChanging = true;
            if (var5.getSelectionMode() == SelectionMode.SINGLE) {
               var5.select(var3);
            } else {
               var5.clearSelection();
               var5.selectRange(var2, var3 + var4);
            }

            this.selectionChanging = false;
         }
      }
   }

   private void selectAllPageDown() {
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         int var2 = var1.getFocusedIndex();
         if (this.isShiftDown) {
            var2 = this.getAnchor() == -1 ? var2 : this.getAnchor();
            this.setAnchor(var2);
         }

         int var3 = (Integer)this.onScrollPageDown.call(false);
         int var4 = var2 < var3 ? 1 : -1;
         MultipleSelectionModel var5 = this.getNode().getSelectionModel();
         if (var5 != null) {
            this.selectionChanging = true;
            if (var5.getSelectionMode() == SelectionMode.SINGLE) {
               var5.select(var3);
            } else {
               var5.clearSelection();
               var5.selectRange(var2, var3 + var4);
            }

            this.selectionChanging = false;
         }
      }
   }

   private void selectAllToFirstRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            if (this.isShiftDown) {
               var3 = this.hasAnchor() ? this.getAnchor() : var3;
            }

            var1.clearSelection();
            var1.selectRange(var3, -1);
            var2.focus(0);
            if (this.isShiftDown) {
               this.setAnchor(var3);
            }

            if (this.onMoveToFirstCell != null) {
               this.onMoveToFirstCell.run();
            }
         }
      }
   }

   private void selectAllToLastRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            if (this.isShiftDown) {
               var3 = this.hasAnchor() ? this.getAnchor() : var3;
            }

            var1.clearSelection();
            var1.selectRange(var3, this.getRowCount());
            if (this.isShiftDown) {
               this.setAnchor(var3);
            }

            if (this.onMoveToLastCell != null) {
               this.onMoveToLastCell.run();
            }
         }
      }
   }

   private void selectAll() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         var1.selectAll();
      }
   }

   private void selectAllToFocus(boolean var1) {
      ListView var2 = this.getNode();
      if (var2.getEditingIndex() < 0) {
         MultipleSelectionModel var3 = var2.getSelectionModel();
         if (var3 != null) {
            FocusModel var4 = var2.getFocusModel();
            if (var4 != null) {
               int var5 = var4.getFocusedIndex();
               int var6 = this.getAnchor();
               var3.clearSelection();
               int var8 = var6 > var5 ? var5 - 1 : var5 + 1;
               var3.selectRange(var6, var8);
               this.setAnchor(var1 ? var5 : var6);
            }
         }
      }
   }

   private void cancelEdit() {
      this.getNode().edit(-1);
   }

   private void activate() {
      int var1 = this.getNode().getFocusModel().getFocusedIndex();
      this.getNode().getSelectionModel().select(var1);
      this.setAnchor(var1);
      if (var1 >= 0) {
         this.getNode().edit(var1);
      }
   }

   private void toggleFocusOwnerSelection() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            if (var1.isSelected(var3)) {
               var1.clearSelection(var3);
               var2.focus(var3);
            } else {
               var1.select(var3);
            }

            this.setAnchor(var3);
         }
      }
   }

   private void discontinuousSelectPreviousRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() != SelectionMode.MULTIPLE) {
            this.selectPreviousRow();
         } else {
            FocusModel var2 = this.getNode().getFocusModel();
            if (var2 != null) {
               int var3 = var2.getFocusedIndex();
               int var4 = var3 - 1;
               if (var4 >= 0) {
                  int var5 = var3;
                  if (this.isShiftDown) {
                     var5 = this.getAnchor() == -1 ? var3 : this.getAnchor();
                  }

                  var1.selectRange(var4, var5 + 1);
                  var2.focus(var4);
                  if (this.onFocusPreviousRow != null) {
                     this.onFocusPreviousRow.run();
                  }
               }
            }
         }
      }
   }

   private void discontinuousSelectNextRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() != SelectionMode.MULTIPLE) {
            this.selectNextRow();
         } else {
            FocusModel var2 = this.getNode().getFocusModel();
            if (var2 != null) {
               int var3 = var2.getFocusedIndex();
               int var4 = var3 + 1;
               if (var4 < this.getRowCount()) {
                  int var5 = var3;
                  if (this.isShiftDown) {
                     var5 = this.getAnchor() == -1 ? var3 : this.getAnchor();
                  }

                  var1.selectRange(var5, var4 + 1);
                  var2.focus(var4);
                  if (this.onFocusNextRow != null) {
                     this.onFocusNextRow.run();
                  }
               }
            }
         }
      }
   }

   private void discontinuousSelectPageUp() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = this.getAnchor();
            int var4 = (Integer)this.onScrollPageUp.call(false);
            var1.selectRange(var3, var4 - 1);
         }
      }
   }

   private void discontinuousSelectPageDown() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = this.getAnchor();
            int var4 = (Integer)this.onScrollPageDown.call(false);
            var1.selectRange(var3, var4 + 1);
         }
      }
   }

   private void discontinuousSelectAllToFirstRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            var1.selectRange(0, var3);
            var2.focus(0);
            if (this.onMoveToFirstCell != null) {
               this.onMoveToFirstCell.run();
            }
         }
      }
   }

   private void discontinuousSelectAllToLastRow() {
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         FocusModel var2 = this.getNode().getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex() + 1;
            var1.selectRange(var3, this.getRowCount());
            if (this.onMoveToLastCell != null) {
               this.onMoveToLastCell.run();
            }
         }
      }
   }
}
