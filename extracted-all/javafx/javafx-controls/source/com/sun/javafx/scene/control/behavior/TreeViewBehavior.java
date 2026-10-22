package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.FocusModel;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class TreeViewBehavior<T> extends BehaviorBase<TreeView<T>> {
   private final InputMap<TreeView<T>> treeViewInputMap;
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
   private Runnable onSelectPreviousRow;
   private Runnable onSelectNextRow;
   private Runnable onMoveToFirstCell;
   private Runnable onMoveToLastCell;
   private Runnable onFocusPreviousRow;
   private Runnable onFocusNextRow;
   private boolean selectionChanging = false;
   private final ListChangeListener<Integer> selectedIndicesListener = var1x -> {
      int var2x = this.getAnchor();

      while (var1x.next()) {
         if (var1x.wasReplaced() && TreeCellBehavior.hasDefaultAnchor(this.getNode())) {
            TreeCellBehavior.removeAnchor(this.getNode());
         } else {
            int var3x = var1x.wasPermutated() ? var1x.getTo() - var1x.getFrom() : 0;
            MultipleSelectionModel var4 = this.getNode().getSelectionModel();
            if (!this.selectionChanging) {
               if (var4.isEmpty()) {
                  var2x = -1;
               } else if (this.hasAnchor() && !var4.isSelected(this.getAnchor() + var3x)) {
                  var2x = -1;
               }
            }

            if (var2x == -1) {
               int var5 = var1x.getAddedSize();
               var2x = var5 > 0 ? (Integer)var1x.getAddedSubList().get(var5 - 1) : var2x;
            }
         }
      }

      if (var2x > -1) {
         this.setAnchor(var2x);
      }
   };
   private final ChangeListener<MultipleSelectionModel<TreeItem<T>>> selectionModelListener = new ChangeListener<MultipleSelectionModel<TreeItem<T>>>() {
      public void changed(
         ObservableValue<? extends MultipleSelectionModel<TreeItem<T>>> var1,
         MultipleSelectionModel<TreeItem<T>> var2,
         MultipleSelectionModel<TreeItem<T>> var3
      ) {
         if (var2 != null) {
            var2.getSelectedIndices().removeListener(TreeViewBehavior.this.weakSelectedIndicesListener);
         }

         if (var3 != null) {
            var3.getSelectedIndices().addListener(TreeViewBehavior.this.weakSelectedIndicesListener);
         }
      }
   };
   private final WeakListChangeListener<Integer> weakSelectedIndicesListener = new WeakListChangeListener(this.selectedIndicesListener);
   private final WeakChangeListener<MultipleSelectionModel<TreeItem<T>>> weakSelectionModelListener = new WeakChangeListener(this.selectionModelListener);

   public void setOnScrollPageUp(Callback<Boolean, Integer> var1) {
      this.onScrollPageUp = var1;
   }

   public void setOnScrollPageDown(Callback<Boolean, Integer> var1) {
      this.onScrollPageDown = var1;
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

   public void setOnFocusPreviousRow(Runnable var1) {
      this.onFocusPreviousRow = var1;
   }

   public void setOnFocusNextRow(Runnable var1) {
      this.onFocusNextRow = var1;
   }

   public TreeViewBehavior(TreeView<T> var1) {
      super(var1);
      this.treeViewInputMap = this.createInputMap();
      this.addDefaultMapping(
         this.treeViewInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.HOME, var1x -> this.selectFirstRow()),
            new InputMap.KeyMapping(KeyCode.END, var1x -> this.selectLastRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.selectAllToFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.selectAllToLastRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shift(), var1x -> this.selectAllPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shift(), var1x -> this.selectAllPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shift(), var1x -> this.selectAllToFocus(false)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().shift(), var1x -> this.selectAllToFocus(true)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut(), var1x -> this.focusFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut(), var1x -> this.focusLastRow()),
            new InputMap.KeyMapping(KeyCode.PAGE_UP, var1x -> this.scrollUp()),
            new InputMap.KeyMapping(KeyCode.PAGE_DOWN, var1x -> this.scrollDown()),
            new InputMap.KeyMapping(KeyCode.SPACE, var1x -> this.toggleFocusOwnerSelection()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.A).shortcut(), var1x -> this.selectAll()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut(), var1x -> this.focusPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut(), var1x -> this.focusPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut(), var1x -> this.focusPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut(), var1x -> this.focusNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut().shift(), var1x -> this.discontinuousSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut().shift(), var1x -> this.discontinuousSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut().shift(), var1x -> this.discontinuousSelectPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut().shift(), var1x -> this.discontinuousSelectPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut().shift(), var1x -> this.discontinuousSelectAllToFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut().shift(), var1x -> this.discontinuousSelectAllToLastRow()),
            new InputMap.KeyMapping(KeyCode.LEFT, var2x -> this.rtl(var1, this::expandRow, this::collapseRow)),
            new InputMap.KeyMapping(KeyCode.KP_LEFT, var2x -> this.rtl(var1, this::expandRow, this::collapseRow)),
            new InputMap.KeyMapping(KeyCode.RIGHT, var2x -> this.rtl(var1, this::collapseRow, this::expandRow)),
            new InputMap.KeyMapping(KeyCode.KP_RIGHT, var2x -> this.rtl(var1, this::collapseRow, this::expandRow)),
            new InputMap.KeyMapping(KeyCode.MULTIPLY, var1x -> this.expandAll()),
            new InputMap.KeyMapping(KeyCode.ADD, var1x -> this.expandRow()),
            new InputMap.KeyMapping(KeyCode.SUBTRACT, var1x -> this.collapseRow()),
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.KP_UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(KeyCode.KP_DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_UP).shift(), var1x -> this.alsoSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_DOWN).shift(), var1x -> this.alsoSelectNextRow()),
            new InputMap.KeyMapping(KeyCode.ENTER, var1x -> this.edit()),
            new InputMap.KeyMapping(KeyCode.F2, var1x -> this.edit()),
            new InputMap.KeyMapping(KeyCode.ESCAPE, var1x -> this.cancelEdit()),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
      InputMap var2 = new InputMap(var1);
      var2.setInterceptor(var0 -> !PlatformUtil.isMac());
      this.addDefaultMapping(
         var2, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.treeViewInputMap, var2);
      InputMap var3 = new InputMap(var1);
      var3.setInterceptor(var0 -> PlatformUtil.isMac());
      this.addDefaultMapping(
         var3, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.treeViewInputMap, var3);
      var1.addEventFilter(KeyEvent.ANY, this.keyEventListener);
      var1.selectionModelProperty().addListener(this.weakSelectionModelListener);
      if (var1.getSelectionModel() != null) {
         var1.getSelectionModel().getSelectedIndices().addListener(this.weakSelectedIndicesListener);
      }
   }

   @Override
   public InputMap<TreeView<T>> getInputMap() {
      return this.treeViewInputMap;
   }

   @Override
   public void dispose() {
      this.getNode().selectionModelProperty().removeListener(this.weakSelectionModelListener);
      MultipleSelectionModel var1 = this.getNode().getSelectionModel();
      if (var1 != null) {
         var1.getSelectedIndices().removeListener(this.weakSelectedIndicesListener);
      }

      this.getNode().removeEventFilter(KeyEvent.ANY, this.keyEventListener);
      TreeCellBehavior.removeAnchor(this.getNode());
      super.dispose();
   }

   private void setAnchor(int var1) {
      TreeCellBehavior.setAnchor(this.getNode(), var1 < 0 ? null : var1, false);
   }

   private int getAnchor() {
      return (Integer)TreeCellBehavior.<Integer>getAnchor(this.getNode(), Integer.valueOf(this.getNode().getFocusModel().getFocusedIndex()));
   }

   private boolean hasAnchor() {
      return TreeCellBehavior.hasNonDefaultAnchor(this.getNode());
   }

   public void mousePressed(MouseEvent var1) {
      if (!var1.isShiftDown()) {
         int var2 = this.getNode().getSelectionModel().getSelectedIndex();
         this.setAnchor(var2);
      }

      if (!this.getNode().isFocused() && this.getNode().isFocusTraversable()) {
         this.getNode().requestFocus();
      }
   }

   private void clearSelection() {
      this.getNode().getSelectionModel().clearSelection();
   }

   private void scrollUp() {
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

   private void scrollDown() {
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
         var1.focus(this.getNode().getExpandedItemCount() - 1);
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
      FocusModel var1 = this.getNode().getFocusModel();
      if (var1 != null) {
         int var2 = var1.getFocusedIndex();
         if (var2 != this.getNode().getExpandedItemCount() - 1) {
            this.setAnchor(var2 + 1);
            this.getNode().getSelectionModel().clearAndSelect(var2 + 1);
            this.onSelectNextRow.run();
         }
      }
   }

   private void selectFirstRow() {
      if (this.getNode().getExpandedItemCount() > 0) {
         this.getNode().getSelectionModel().clearAndSelect(0);
         if (this.onMoveToFirstCell != null) {
            this.onMoveToFirstCell.run();
         }
      }
   }

   private void selectLastRow() {
      this.getNode().getSelectionModel().clearAndSelect(this.getNode().getExpandedItemCount() - 1);
      this.onMoveToLastCell.run();
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
            var1.selectRange(var3, this.getNode().getExpandedItemCount());
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
      this.getNode().getSelectionModel().selectAll();
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

   private void selectAllToFocus(boolean var1) {
      TreeView var2 = this.getNode();
      if (var2.getEditingItem() == null) {
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

   private void expandRow() {
      Callback var1 = var1x -> this.getNode().getRow(var1x);
      expandRow(this.getNode().getSelectionModel(), var1);
   }

   private void expandAll() {
      expandAll(this.getNode().getRoot());
   }

   private void collapseRow() {
      TreeView var1 = this.getNode();
      collapseRow(var1.getSelectionModel(), var1.getRoot(), var1.isShowRoot());
   }

   static <T> void expandRow(MultipleSelectionModel<TreeItem<T>> var0, Callback<TreeItem<T>, Integer> var1) {
      if (var0 != null) {
         TreeItem var2 = (TreeItem)var0.getSelectedItem();
         if (var2 != null && !var2.isLeaf()) {
            if (var2.isExpanded()) {
               ObservableList var3 = var2.getChildren();
               if (!var3.isEmpty()) {
                  var0.clearAndSelect((Integer)var1.call((TreeItem)var3.get(0)));
               }
            } else {
               var2.setExpanded(true);
            }
         }
      }
   }

   static <T> void expandAll(TreeItem<T> var0) {
      if (var0 != null) {
         var0.setExpanded(true);
         expandChildren(var0);
      }
   }

   private static <T> void expandChildren(TreeItem<T> var0) {
      if (var0 != null) {
         ObservableList var1 = var0.getChildren();
         if (var1 != null) {
            for (int var2 = 0; var2 < var1.size(); var2++) {
               TreeItem var3 = (TreeItem)var1.get(var2);
               if (var3 != null && !var3.isLeaf()) {
                  var3.setExpanded(true);
                  expandChildren(var3);
               }
            }
         }
      }
   }

   static <T> void collapseRow(MultipleSelectionModel<TreeItem<T>> var0, TreeItem<T> var1, boolean var2) {
      if (var0 != null) {
         TreeItem var3 = (TreeItem)var0.getSelectedItem();
         if (var3 != null) {
            if (var1 != null) {
               if (var2 || var3.isExpanded() && !var3.isLeaf() || !var1.equals(var3.getParent())) {
                  if (!var1.equals(var3) || var1.isExpanded() && !var1.getChildren().isEmpty()) {
                     if (!var3.isLeaf() && var3.isExpanded()) {
                        var3.setExpanded(false);
                     } else {
                        var0.clearSelection();
                        var0.select(var3.getParent());
                     }
                  }
               }
            }
         }
      }
   }

   private void cancelEdit() {
      this.getNode().edit(null);
   }

   private void edit() {
      TreeItem var1 = this.getNode().getSelectionModel().getSelectedItem();
      if (var1 != null) {
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
               if (var4 < this.getNode().getExpandedItemCount()) {
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
            var1.selectRange(var3, this.getNode().getExpandedItemCount());
            if (this.onMoveToLastCell != null) {
               this.onMoveToLastCell.run();
            }
         }
      }
   }
}
