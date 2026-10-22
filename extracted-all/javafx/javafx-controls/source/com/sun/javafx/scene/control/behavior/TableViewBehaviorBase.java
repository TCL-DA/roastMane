package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.SizeLimitedList;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public abstract class TableViewBehaviorBase<C extends Control, T, TC extends TableColumnBase<T, ?>> extends BehaviorBase<C> {
   private final InputMap<C> tableViewInputMap;
   protected boolean isShortcutDown = false;
   protected boolean isShiftDown = false;
   private boolean selectionPathDeviated = false;
   protected boolean selectionChanging = false;
   private final EventHandler<KeyEvent> keyEventListener = var1x -> {
      if (!var1x.isConsumed()) {
         this.isShiftDown = var1x.getEventType() == KeyEvent.KEY_PRESSED && var1x.isShiftDown();
         this.isShortcutDown = var1x.getEventType() == KeyEvent.KEY_PRESSED && var1x.isShortcutDown();
      }
   };
   private final SizeLimitedList<TablePositionBase> selectionHistory = new SizeLimitedList<>(10);
   protected final ListChangeListener<TablePositionBase> selectedCellsListener = var1x -> {
      while (var1x.next()) {
         if (var1x.wasReplaced() && TreeTableCellBehavior.hasDefaultAnchor(this.getNode())) {
            TreeTableCellBehavior.removeAnchor(this.getNode());
         }

         if (var1x.wasAdded()) {
            TableSelectionModel var2x = this.getSelectionModel();
            if (var2x == null) {
               return;
            }

            TablePositionBase var3x = this.getAnchor();
            boolean var4x = var2x.isCellSelectionEnabled();
            int var5x = var1x.getAddedSize();
            List var6 = var1x.getAddedSubList();

            for (TablePositionBase var8 : var6) {
               if (!this.selectionHistory.contains(var8)) {
                  this.selectionHistory.add(var8);
               }
            }

            if (var5x > 0 && !this.hasAnchor()) {
               TablePositionBase var9 = (TablePositionBase)var6.get(var5x - 1);
               this.setAnchor(var9);
            }

            if (var3x != null && var4x && !this.selectionPathDeviated) {
               for (int var10 = 0; var10 < var5x; var10++) {
                  TablePositionBase var11 = (TablePositionBase)var6.get(var10);
                  if (var3x.getRow() != -1 && var11.getRow() != var3x.getRow() && var11.getColumn() != var3x.getColumn()) {
                     this.setSelectionPathDeviated(true);
                     break;
                  }
               }
            }
         }
      }
   };
   protected final WeakListChangeListener<TablePositionBase> weakSelectedCellsListener = new WeakListChangeListener(this.selectedCellsListener);
   private Callback<Boolean, Integer> onScrollPageUp;
   private Callback<Boolean, Integer> onScrollPageDown;
   private Runnable onFocusPreviousRow;
   private Runnable onFocusNextRow;
   private Runnable onSelectPreviousRow;
   private Runnable onSelectNextRow;
   private Runnable onMoveToFirstCell;
   private Runnable onMoveToLastCell;
   private Runnable onSelectRightCell;
   private Runnable onSelectLeftCell;
   private Runnable onFocusRightCell;
   private Runnable onFocusLeftCell;

   public TableViewBehaviorBase(C var1) {
      super((C)var1);
      this.tableViewInputMap = this.createInputMap();
      InputMap.KeyMapping var2;
      InputMap.KeyMapping var3;
      this.addDefaultMapping(
         this.tableViewInputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.TAB, FocusTraversalInputMap::traverseNext),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).shift(), FocusTraversalInputMap::traversePrevious),
            new InputMap.KeyMapping(KeyCode.HOME, var1x -> this.selectFirstRow()),
            new InputMap.KeyMapping(KeyCode.END, var1x -> this.selectLastRow()),
            new InputMap.KeyMapping(KeyCode.PAGE_UP, var1x -> this.scrollUp()),
            new InputMap.KeyMapping(KeyCode.PAGE_DOWN, var1x -> this.scrollDown()),
            new InputMap.KeyMapping(KeyCode.LEFT, var1x -> {
               if (this.isRTL()) {
                  this.selectRightCell();
               } else {
                  this.selectLeftCell();
               }
            }),
            new InputMap.KeyMapping(KeyCode.KP_LEFT, var1x -> {
               if (this.isRTL()) {
                  this.selectRightCell();
               } else {
                  this.selectLeftCell();
               }
            }),
            new InputMap.KeyMapping(KeyCode.RIGHT, var1x -> {
               if (this.isRTL()) {
                  this.selectLeftCell();
               } else {
                  this.selectRightCell();
               }
            }),
            new InputMap.KeyMapping(KeyCode.KP_RIGHT, var1x -> {
               if (this.isRTL()) {
                  this.selectLeftCell();
               } else {
                  this.selectRightCell();
               }
            }),
            new InputMap.KeyMapping(KeyCode.UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.KP_UP, var1x -> this.selectPreviousRow()),
            new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(KeyCode.KP_DOWN, var1x -> this.selectNextRow()),
            new InputMap.KeyMapping(KeyCode.LEFT, var1x -> {
               if (this.isRTL()) {
                  this.focusTraverseRight();
               } else {
                  this.focusTraverseLeft();
               }
            }),
            new InputMap.KeyMapping(KeyCode.KP_LEFT, var1x -> {
               if (this.isRTL()) {
                  this.focusTraverseRight();
               } else {
                  this.focusTraverseLeft();
               }
            }),
            new InputMap.KeyMapping(KeyCode.RIGHT, var1x -> {
               if (this.isRTL()) {
                  this.focusTraverseLeft();
               } else {
                  this.focusTraverseRight();
               }
            }),
            new InputMap.KeyMapping(KeyCode.KP_RIGHT, var1x -> {
               if (this.isRTL()) {
                  this.focusTraverseLeft();
               } else {
                  this.focusTraverseRight();
               }
            }),
            new InputMap.KeyMapping(KeyCode.UP, FocusTraversalInputMap::traverseUp),
            new InputMap.KeyMapping(KeyCode.KP_UP, FocusTraversalInputMap::traverseUp),
            new InputMap.KeyMapping(KeyCode.DOWN, FocusTraversalInputMap::traverseDown),
            new InputMap.KeyMapping(KeyCode.KP_DOWN, FocusTraversalInputMap::traverseDown),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shift(), var1x -> this.selectAllToFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shift(), var1x -> this.selectAllToLastRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shift(), var1x -> this.selectAllPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shift(), var1x -> this.selectAllPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shift(), var1x -> this.alsoSelectPrevious()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_UP).shift(), var1x -> this.alsoSelectPrevious()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shift(), var1x -> this.alsoSelectNext()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_DOWN).shift(), var1x -> this.alsoSelectNext()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shift(), var1x -> this.selectAllToFocus(false)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().shift(), var1x -> this.selectAllToFocus(true)),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shift(), var1x -> {
               if (this.isRTL()) {
                  this.alsoSelectRightCell();
               } else {
                  this.alsoSelectLeftCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_LEFT).shift(), var1x -> {
               if (this.isRTL()) {
                  this.alsoSelectRightCell();
               } else {
                  this.alsoSelectLeftCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shift(), var1x -> {
               if (this.isRTL()) {
                  this.alsoSelectLeftCell();
               } else {
                  this.alsoSelectRightCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_RIGHT).shift(), var1x -> {
               if (this.isRTL()) {
                  this.alsoSelectLeftCell();
               } else {
                  this.alsoSelectRightCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut(), var1x -> this.focusPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut(), var1x -> this.focusNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shortcut(), var1x -> {
               if (this.isRTL()) {
                  this.focusLeftCell();
               } else {
                  this.focusRightCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_RIGHT).shortcut(), var1x -> {
               if (this.isRTL()) {
                  this.focusLeftCell();
               } else {
                  this.focusRightCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shortcut(), var1x -> {
               if (this.isRTL()) {
                  this.focusRightCell();
               } else {
                  this.focusLeftCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.KP_LEFT).shortcut(), var1x -> {
               if (this.isRTL()) {
                  this.focusRightCell();
               } else {
                  this.focusLeftCell();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.A).shortcut(), var1x -> this.selectAll()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut(), var1x -> this.focusFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut(), var1x -> this.focusLastRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut(), var1x -> this.focusPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut(), var1x -> this.focusPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shortcut().shift(), var1x -> this.discontinuousSelectPreviousRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shortcut().shift(), var1x -> this.discontinuousSelectNextRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shortcut().shift(), var1x -> {
               if (this.isRTL()) {
                  this.discontinuousSelectNextColumn();
               } else {
                  this.discontinuousSelectPreviousColumn();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shortcut().shift(), var1x -> {
               if (this.isRTL()) {
                  this.discontinuousSelectPreviousColumn();
               } else {
                  this.discontinuousSelectNextColumn();
               }
            }),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_UP).shortcut().shift(), var1x -> this.discontinuousSelectPageUp()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.PAGE_DOWN).shortcut().shift(), var1x -> this.discontinuousSelectPageDown()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.HOME).shortcut().shift(), var1x -> this.discontinuousSelectAllToFirstRow()),
            new InputMap.KeyMapping(new KeyBinding(KeyCode.END).shortcut().shift(), var1x -> this.discontinuousSelectAllToLastRow()),
            var2 = new InputMap.KeyMapping(KeyCode.ENTER, this::activate),
            new InputMap.KeyMapping(KeyCode.SPACE, this::activate),
            new InputMap.KeyMapping(KeyCode.F2, this::activate),
            var3 = new InputMap.KeyMapping(KeyCode.ESCAPE, this::cancelEdit),
            new InputMap.MouseMapping(MouseEvent.MOUSE_PRESSED, this::mousePressed)
         }
      );
      var2.setAutoConsume(false);
      var3.setAutoConsume(false);
      InputMap var4 = new InputMap(var1);
      var4.setInterceptor(var0 -> !PlatformUtil.isMac());
      this.addDefaultMapping(
         var4, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).shortcut().ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.tableViewInputMap, var4);
      InputMap var5 = new InputMap(var1);
      var5.setInterceptor(var0 -> PlatformUtil.isMac());
      this.addDefaultMapping(
         var5, new InputMap.Mapping[]{new InputMap.KeyMapping(new KeyBinding(KeyCode.SPACE).ctrl(), var1x -> this.toggleFocusOwnerSelection())}
      );
      this.addDefaultChildMap(this.tableViewInputMap, var5);
      var1.addEventFilter(KeyEvent.ANY, this.keyEventListener);
   }

   @Override
   public InputMap<C> getInputMap() {
      return this.tableViewInputMap;
   }

   protected void setAnchor(TablePositionBase var1) {
      TableCellBehaviorBase.setAnchor(this.getNode(), (C)var1, false);
      this.setSelectionPathDeviated(false);
   }

   protected TablePositionBase getAnchor() {
      return (TablePositionBase)TableCellBehaviorBase.getAnchor(this.getNode(), (C)this.getFocusedCell());
   }

   protected boolean hasAnchor() {
      return TableCellBehaviorBase.hasNonDefaultAnchor(this.getNode());
   }

   protected abstract int getItemCount();

   protected abstract TableFocusModel getFocusModel();

   protected abstract TableSelectionModel<T> getSelectionModel();

   protected abstract ObservableList<? extends TablePositionBase> getSelectedCells();

   protected abstract TablePositionBase getFocusedCell();

   protected abstract int getVisibleLeafIndex(TableColumnBase var1);

   protected abstract TableColumnBase getVisibleLeafColumn(int var1);

   protected abstract boolean isControlEditable();

   protected abstract void editCell(int var1, TableColumnBase var2);

   protected abstract ObservableList<? extends TableColumnBase> getVisibleLeafColumns();

   protected abstract TablePositionBase<TC> getTablePosition(int var1, TableColumnBase<T, ?> var2);

   protected void setAnchor(int var1, TableColumnBase var2) {
      this.setAnchor(var1 == -1 && var2 == null ? null : this.getTablePosition(var1, var2));
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

   public void setOnSelectRightCell(Runnable var1) {
      this.onSelectRightCell = var1;
   }

   public void setOnSelectLeftCell(Runnable var1) {
      this.onSelectLeftCell = var1;
   }

   public void setOnFocusRightCell(Runnable var1) {
      this.onFocusRightCell = var1;
   }

   public void setOnFocusLeftCell(Runnable var1) {
      this.onFocusLeftCell = var1;
   }

   public void mousePressed(MouseEvent var1) {
      if (!this.getNode().isFocused() && this.getNode().isFocusTraversable()) {
         this.getNode().requestFocus();
      }
   }

   protected boolean isRTL() {
      return this.getNode().getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
   }

   private void setSelectionPathDeviated(boolean var1) {
      this.selectionPathDeviated = var1;
   }

   protected void scrollUp() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && !this.getSelectedCells().isEmpty()) {
         TablePositionBase var2 = (TablePositionBase)this.getSelectedCells().get(0);
         int var3 = -1;
         if (this.onScrollPageUp != null) {
            var3 = (Integer)this.onScrollPageUp.call(false);
         }

         if (var3 != -1) {
            var1.clearAndSelect(var3, var2.getTableColumn());
         }
      }
   }

   protected void scrollDown() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && !this.getSelectedCells().isEmpty()) {
         TablePositionBase var2 = (TablePositionBase)this.getSelectedCells().get(0);
         int var3 = -1;
         if (this.onScrollPageDown != null) {
            var3 = (Integer)this.onScrollPageDown.call(false);
         }

         if (var3 != -1) {
            var1.clearAndSelect(var3, var2.getTableColumn());
         }
      }
   }

   protected void focusFirstRow() {
      TableFocusModel var1 = this.getFocusModel();
      if (var1 != null) {
         TableColumnBase var2 = this.getFocusedCell() == null ? null : this.getFocusedCell().getTableColumn();
         var1.focus(0, (TC)var2);
         if (this.onMoveToFirstCell != null) {
            this.onMoveToFirstCell.run();
         }
      }
   }

   protected void focusLastRow() {
      TableFocusModel var1 = this.getFocusModel();
      if (var1 != null) {
         TableColumnBase var2 = this.getFocusedCell() == null ? null : this.getFocusedCell().getTableColumn();
         var1.focus(this.getItemCount() - 1, (TC)var2);
         if (this.onMoveToLastCell != null) {
            this.onMoveToLastCell.run();
         }
      }
   }

   protected void focusPreviousRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            if (var1.isCellSelectionEnabled()) {
               var2.focusAboveCell();
            } else {
               var2.focusPrevious();
            }

            if (!this.isShortcutDown || this.getAnchor() == null) {
               this.setAnchor(var2.getFocusedIndex(), null);
            }

            if (this.onFocusPreviousRow != null) {
               this.onFocusPreviousRow.run();
            }
         }
      }
   }

   protected void focusNextRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            if (var1.isCellSelectionEnabled()) {
               var2.focusBelowCell();
            } else {
               var2.focusNext();
            }

            if (!this.isShortcutDown || this.getAnchor() == null) {
               this.setAnchor(var2.getFocusedIndex(), null);
            }

            if (this.onFocusNextRow != null) {
               this.onFocusNextRow.run();
            }
         }
      }
   }

   protected void focusLeftCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            var2.focusLeftCell();
            if (this.onFocusLeftCell != null) {
               this.onFocusLeftCell.run();
            }
         }
      }
   }

   protected void focusRightCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            var2.focusRightCell();
            if (this.onFocusRightCell != null) {
               this.onFocusRightCell.run();
            }
         }
      }
   }

   protected void focusPageUp() {
      int var1 = (Integer)this.onScrollPageUp.call(true);
      TableFocusModel var2 = this.getFocusModel();
      if (var2 != null) {
         TableColumnBase var3 = this.getFocusedCell() == null ? null : this.getFocusedCell().getTableColumn();
         var2.focus(var1, (TC)var3);
      }
   }

   protected void focusPageDown() {
      int var1 = (Integer)this.onScrollPageDown.call(true);
      TableFocusModel var2 = this.getFocusModel();
      if (var2 != null) {
         TableColumnBase var3 = this.getFocusedCell() == null ? null : this.getFocusedCell().getTableColumn();
         var2.focus(var1, (TC)var3);
      }
   }

   protected void clearSelection() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         var1.clearSelection();
      }
   }

   protected void clearSelectionOutsideRange(int var1, int var2, TableColumnBase<T, ?> var3) {
      TableSelectionModel var4 = this.getSelectionModel();
      if (var4 != null) {
         int var5 = Math.min(var1, var2);
         int var6 = Math.max(var1, var2);
         ArrayList var7 = new ArrayList(var4.getSelectedIndices());
         this.selectionChanging = true;

         for (int var8 = 0; var8 < var7.size(); var8++) {
            int var9 = (Integer)var7.get(var8);
            if (var9 < var5 || var9 > var6) {
               var4.clearSelection(var9, var3);
            }
         }

         this.selectionChanging = false;
      }
   }

   protected void alsoSelectPrevious() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() == SelectionMode.SINGLE) {
            this.selectPreviousRow();
         } else {
            TableFocusModel var2 = this.getFocusModel();
            if (var2 != null) {
               if (var1.isCellSelectionEnabled()) {
                  this.updateCellVerticalSelection(-1, () -> this.getSelectionModel().selectAboveCell());
               } else if (this.isShiftDown && this.hasAnchor()) {
                  this.updateRowSelection(-1);
               } else {
                  var1.selectPrevious();
               }

               this.onSelectPreviousRow.run();
            }
         }
      }
   }

   protected void alsoSelectNext() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() == SelectionMode.SINGLE) {
            this.selectNextRow();
         } else {
            TableFocusModel var2 = this.getFocusModel();
            if (var2 != null) {
               if (var1.isCellSelectionEnabled()) {
                  this.updateCellVerticalSelection(1, () -> this.getSelectionModel().selectBelowCell());
               } else if (this.isShiftDown && this.hasAnchor()) {
                  this.updateRowSelection(1);
               } else {
                  var1.selectNext();
               }

               this.onSelectNextRow.run();
            }
         }
      }
   }

   protected void alsoSelectLeftCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && var1.isCellSelectionEnabled()) {
         this.updateCellHorizontalSelection(-1, () -> this.getSelectionModel().selectLeftCell());
         this.onSelectLeftCell.run();
      }
   }

   protected void alsoSelectRightCell() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && var1.isCellSelectionEnabled()) {
         this.updateCellHorizontalSelection(1, () -> this.getSelectionModel().selectRightCell());
         this.onSelectRightCell.run();
      }
   }

   protected void updateRowSelection(int var1) {
      TableSelectionModel var2 = this.getSelectionModel();
      if (var2 != null && var2.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel var3 = this.getFocusModel();
         if (var3 != null) {
            int var4 = var3.getFocusedIndex() + var1;
            TablePositionBase var5 = this.getAnchor();
            if (!this.hasAnchor()) {
               this.setAnchor(this.getFocusedCell());
            }

            if (var2.getSelectedIndices().size() > 1) {
               this.clearSelectionOutsideRange(var5.getRow(), var4, null);
            }

            if (var5.getRow() > var4) {
               var2.selectRange(var5.getRow(), var4 - 1);
            } else {
               var2.selectRange(var5.getRow(), var4 + 1);
            }
         }
      }
   }

   protected void updateCellVerticalSelection(int var1, Runnable var2) {
      TableSelectionModel var3 = this.getSelectionModel();
      if (var3 != null && var3.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel var4 = this.getFocusModel();
         if (var4 != null) {
            TablePositionBase var5 = this.getFocusedCell();
            int var6 = var5.getRow();
            if (this.isShiftDown && var3.isSelected(var6 + var1, var5.getTableColumn())) {
               int var13 = var6 + var1;
               boolean var14 = false;
               if (this.selectionHistory.size() >= 2) {
                  TablePositionBase var15 = this.selectionHistory.get(1);
                  var14 = var15.getRow() == var13 && var15.getColumn() == var5.getColumn();
               }

               int var16 = this.selectionPathDeviated ? (var14 ? var6 : var13) : var6;
               var3.clearSelection(var16, var5.getTableColumn());
               var4.focus(var13, (TC)var5.getTableColumn());
            } else if (this.isShiftDown && this.getAnchor() != null && !this.selectionPathDeviated) {
               int var11 = var4.getFocusedIndex() + var1;
               var11 = Math.max(Math.min(this.getItemCount() - 1, var11), 0);
               int var8 = Math.min(this.getAnchor().getRow(), var11);
               int var9 = Math.max(this.getAnchor().getRow(), var11);
               if (var3.getSelectedIndices().size() > 1) {
                  this.clearSelectionOutsideRange(var8, var9, var5.getTableColumn());
               }

               for (int var10 = var8; var10 <= var9; var10++) {
                  if (!var3.isSelected(var10, var5.getTableColumn())) {
                     var3.select(var10, var5.getTableColumn());
                  }
               }

               var4.focus(var11, (TC)var5.getTableColumn());
            } else {
               int var7 = var4.getFocusedIndex();
               if (!var3.isSelected(var7, var5.getTableColumn())) {
                  var3.select(var7, var5.getTableColumn());
               }

               var2.run();
            }
         }
      }
   }

   protected void updateCellHorizontalSelection(int var1, Runnable var2) {
      TableSelectionModel var3 = this.getSelectionModel();
      if (var3 != null && var3.getSelectionMode() != SelectionMode.SINGLE) {
         TableFocusModel var4 = this.getFocusModel();
         if (var4 != null) {
            TablePositionBase var5 = this.getFocusedCell();
            if (var5 != null && var5.getTableColumn() != null) {
               boolean var6 = false;
               TableColumnBase var7 = this.getColumn(var5.getTableColumn(), var1);
               if (var7 == null) {
                  var7 = var5.getTableColumn();
                  var6 = true;
               }

               int var8 = var5.getRow();
               if (this.isShiftDown && var3.isSelected(var8, var7)) {
                  if (var6) {
                     return;
                  }

                  boolean var14 = false;
                  ObservableList var16 = this.getSelectedCells();
                  if (var16.size() >= 2) {
                     TablePositionBase var17 = (TablePositionBase)var16.get(var16.size() - 2);
                     var14 = var17.getRow() == var8 && var17.getTableColumn().equals(var7);
                  }

                  TableColumnBase var18 = this.selectionPathDeviated ? (var14 ? var5.getTableColumn() : var7) : var5.getTableColumn();
                  var3.clearSelection(var8, var18);
                  var4.focus(var8, (TC)var7);
               } else if (this.isShiftDown && this.getAnchor() != null && !this.selectionPathDeviated) {
                  int var9 = this.getAnchor().getColumn();
                  int var10 = this.getVisibleLeafIndex(var5.getTableColumn()) + var1;
                  var10 = Math.max(Math.min(this.getVisibleLeafColumns().size() - 1, var10), 0);
                  int var11 = Math.min(var9, var10);
                  int var12 = Math.max(var9, var10);

                  for (int var13 = var11; var13 <= var12; var13++) {
                     var3.select(var5.getRow(), this.getColumn(var13));
                  }

                  var4.focus(var5.getRow(), (TC)this.getColumn(var10));
               } else {
                  var2.run();
               }
            }
         }
      }
   }

   protected TableColumnBase getColumn(int var1) {
      return this.getVisibleLeafColumn(var1);
   }

   protected TableColumnBase getColumn(TableColumnBase var1, int var2) {
      return this.getVisibleLeafColumn(this.getVisibleLeafIndex(var1) + var2);
   }

   protected void selectFirstRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         ObservableList var2 = this.getSelectedCells();
         TableColumnBase var3 = var2.size() == 0 ? null : ((TablePositionBase)var2.get(0)).getTableColumn();
         var1.clearAndSelect(0, var3);
         if (this.onMoveToFirstCell != null) {
            this.onMoveToFirstCell.run();
         }
      }
   }

   protected void selectLastRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         ObservableList var2 = this.getSelectedCells();
         TableColumnBase var3 = var2.size() == 0 ? null : ((TablePositionBase)var2.get(0)).getTableColumn();
         var1.clearAndSelect(this.getItemCount() - 1, var3);
         if (this.onMoveToLastCell != null) {
            this.onMoveToLastCell.run();
         }
      }
   }

   protected void selectPreviousRow() {
      this.selectCell(-1, 0);
      if (this.onSelectPreviousRow != null) {
         this.onSelectPreviousRow.run();
      }
   }

   protected void selectNextRow() {
      this.selectCell(1, 0);
      if (this.onSelectNextRow != null) {
         this.onSelectNextRow.run();
      }
   }

   protected void selectLeftCell() {
      this.selectCell(0, -1);
      if (this.onSelectLeftCell != null) {
         this.onSelectLeftCell.run();
      }
   }

   protected void selectRightCell() {
      this.selectCell(0, 1);
      if (this.onSelectRightCell != null) {
         this.onSelectRightCell.run();
      }
   }

   protected void selectCell(int var1, int var2) {
      TableSelectionModel var3 = this.getSelectionModel();
      if (var3 != null) {
         TableFocusModel var4 = this.getFocusModel();
         if (var4 != null) {
            TablePositionBase var5 = this.getFocusedCell();
            int var6 = var5.getRow();
            int var7 = this.getVisibleLeafIndex(var5.getTableColumn());
            if (var1 <= 0 || var6 < this.getItemCount() - 1) {
               if (var2 >= 0 || var7 > 0) {
                  if (var2 <= 0 || var7 < this.getVisibleLeafColumns().size() - 1) {
                     if (var2 <= 0 || var7 != -1) {
                        TableColumnBase var8 = var5.getTableColumn();
                        var8 = this.getColumn(var8, var2);
                        int var9 = var6 <= 0 && var1 <= 0 ? 0 : var5.getRow() + var1;
                        var3.clearAndSelect(var9, var8);
                        this.setAnchor(var9, var8);
                     }
                  }
               }
            }
         }
      }
   }

   protected void cancelEdit(KeyEvent var1) {
      if (this.isControlEditable()) {
         this.editCell(-1, null);
         var1.consume();
      }
   }

   protected void activate(KeyEvent var1) {
      TableSelectionModel var2 = this.getSelectionModel();
      if (var2 != null) {
         TableFocusModel var3 = this.getFocusModel();
         if (var3 != null) {
            TablePositionBase var4 = this.getFocusedCell();
            TableColumnBase var5 = var4.getTableColumn();
            var2.select(var4.getRow(), var5);
            this.setAnchor(var4);
            if (var5 != null) {
               boolean var6 = this.isControlEditable() && var5.isEditable();
               if (var6 && var4.getRow() >= 0) {
                  this.editCell(var4.getRow(), var5);
                  var1.consume();
               }
            }
         }
      }
   }

   protected void selectAllToFocus(boolean var1) {
      TableSelectionModel var2 = this.getSelectionModel();
      if (var2 != null) {
         TableFocusModel var3 = this.getFocusModel();
         if (var3 != null) {
            TablePositionBase var4 = this.getFocusedCell();
            int var5 = var4.getRow();
            TablePositionBase var6 = this.getAnchor();
            int var7 = var6.getRow();
            var2.clearSelection();
            if (!var2.isCellSelectionEnabled()) {
               int var9 = var7 > var5 ? var5 - 1 : var5 + 1;
               var2.selectRange(var7, var9);
            } else {
               var2.selectRange(var6.getRow(), var6.getTableColumn(), var4.getRow(), var4.getTableColumn());
            }

            this.setAnchor(var1 ? var4 : var6);
         }
      }
   }

   protected void selectAll() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         var1.selectAll();
      }
   }

   protected void selectAllToFirstRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            boolean var3 = var1.getSelectionMode() == SelectionMode.SINGLE;
            TablePositionBase var4 = this.getFocusedCell();
            TableColumnBase var5 = this.getFocusedCell().getTableColumn();
            int var6 = var4.getRow();
            if (this.isShiftDown) {
               var6 = this.getAnchor() == null ? var6 : this.getAnchor().getRow();
            }

            var1.clearSelection();
            if (!var1.isCellSelectionEnabled()) {
               if (var3) {
                  var1.select(0);
               } else {
                  var1.selectRange(var6, -1);
               }

               var2.focus(0);
            } else {
               if (var3) {
                  var1.select(0, var5);
               } else {
                  var1.selectRange(var6, var5, -1, var5);
               }

               var2.focus(0, (TC)var5);
            }

            if (this.isShiftDown) {
               this.setAnchor(var6, var5);
            }

            if (this.onMoveToFirstCell != null) {
               this.onMoveToFirstCell.run();
            }
         }
      }
   }

   protected void selectAllToLastRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = this.getItemCount();
            TablePositionBase var4 = this.getFocusedCell();
            TableColumnBase var5 = this.getFocusedCell().getTableColumn();
            int var6 = var4.getRow();
            if (this.isShiftDown) {
               var6 = this.getAnchor() == null ? var6 : this.getAnchor().getRow();
            }

            var1.clearSelection();
            if (!var1.isCellSelectionEnabled()) {
               var1.selectRange(var6, var3);
            } else {
               var1.selectRange(var6, var5, var3 - 1, var5);
            }

            if (this.isShiftDown) {
               this.setAnchor(var6, var5);
            }

            if (this.onMoveToLastCell != null) {
               this.onMoveToLastCell.run();
            }
         }
      }
   }

   protected void selectAllPageUp() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            TableColumnBase var4 = var1.isCellSelectionEnabled() ? this.getFocusedCell().getTableColumn() : null;
            if (this.isShiftDown) {
               var3 = this.getAnchor() == null ? var3 : this.getAnchor().getRow();
               this.setAnchor(var3, var4);
            }

            int var5 = (Integer)this.onScrollPageUp.call(false);
            this.selectionChanging = true;
            if (var1.getSelectionMode() != null && var1.getSelectionMode() != SelectionMode.SINGLE) {
               var1.clearSelection();
               if (var1.isCellSelectionEnabled()) {
                  var1.selectRange(var3, var4, var5, var4);
               } else {
                  int var6 = var3 < var5 ? 1 : -1;
                  var1.selectRange(var3, var5 + var6);
               }
            } else if (var1.isCellSelectionEnabled()) {
               var1.select(var5, var4);
            } else {
               var1.select(var5);
            }

            this.selectionChanging = false;
         }
      }
   }

   protected void selectAllPageDown() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            TableColumnBase var4 = var1.isCellSelectionEnabled() ? this.getFocusedCell().getTableColumn() : null;
            if (this.isShiftDown) {
               var3 = this.getAnchor() == null ? var3 : this.getAnchor().getRow();
               this.setAnchor(var3, var4);
            }

            int var5 = (Integer)this.onScrollPageDown.call(false);
            this.selectionChanging = true;
            if (var1.getSelectionMode() != null && var1.getSelectionMode() != SelectionMode.SINGLE) {
               var1.clearSelection();
               if (var1.isCellSelectionEnabled()) {
                  var1.selectRange(var3, var4, var5, var4);
               } else {
                  int var6 = var3 < var5 ? 1 : -1;
                  var1.selectRange(var3, var5 + var6);
               }
            } else if (var1.isCellSelectionEnabled()) {
               var1.select(var5, var4);
            } else {
               var1.select(var5);
            }

            this.selectionChanging = false;
         }
      }
   }

   protected void toggleFocusOwnerSelection() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            TablePositionBase var3 = this.getFocusedCell();
            if (var1.isSelected(var3.getRow(), var3.getTableColumn())) {
               var1.clearSelection(var3.getRow(), var3.getTableColumn());
               var2.focus(var3.getRow(), (TC)var3.getTableColumn());
            } else {
               var1.select(var3.getRow(), var3.getTableColumn());
            }

            this.setAnchor(var3.getRow(), var3.getTableColumn());
         }
      }
   }

   protected void discontinuousSelectPreviousRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() != SelectionMode.MULTIPLE) {
            this.selectPreviousRow();
         } else {
            TableFocusModel var2 = this.getFocusModel();
            if (var2 != null) {
               int var3 = var2.getFocusedIndex();
               int var4 = var3 - 1;
               if (var4 >= 0) {
                  int var5 = var3;
                  TableColumnBase var6 = var1.isCellSelectionEnabled() ? this.getFocusedCell().getTableColumn() : null;
                  if (this.isShiftDown) {
                     var5 = this.getAnchor() == null ? var3 : this.getAnchor().getRow();
                  }

                  if (!var1.isCellSelectionEnabled()) {
                     var1.selectRange(var4, var5 + 1);
                     var2.focus(var4);
                  } else {
                     for (int var7 = var4; var7 < var5 + 1; var7++) {
                        var1.select(var7, var6);
                     }

                     var2.focus(var4, (TC)var6);
                  }

                  if (this.onFocusPreviousRow != null) {
                     this.onFocusPreviousRow.run();
                  }
               }
            }
         }
      }
   }

   protected void discontinuousSelectNextRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         if (var1.getSelectionMode() != SelectionMode.MULTIPLE) {
            this.selectNextRow();
         } else {
            TableFocusModel var2 = this.getFocusModel();
            if (var2 != null) {
               int var3 = var2.getFocusedIndex();
               int var4 = var3 + 1;
               if (var4 < this.getItemCount()) {
                  int var5 = var3;
                  TableColumnBase var6 = var1.isCellSelectionEnabled() ? this.getFocusedCell().getTableColumn() : null;
                  if (this.isShiftDown) {
                     var5 = this.getAnchor() == null ? var3 : this.getAnchor().getRow();
                  }

                  if (!var1.isCellSelectionEnabled()) {
                     var1.selectRange(var5, var4 + 1);
                     var2.focus(var4);
                  } else {
                     for (int var7 = var5; var7 < var4 + 1; var7++) {
                        var1.select(var7, var6);
                     }

                     var2.focus(var4, (TC)var6);
                  }

                  if (this.onFocusNextRow != null) {
                     this.onFocusNextRow.run();
                  }
               }
            }
         }
      }
   }

   protected void discontinuousSelectPreviousColumn() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && var1.isCellSelectionEnabled()) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            TableColumnBase var3 = this.getColumn(this.getFocusedCell().getTableColumn(), -1);
            var1.select(var2.getFocusedIndex(), var3);
         }
      }
   }

   protected void discontinuousSelectNextColumn() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null && var1.isCellSelectionEnabled()) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            TableColumnBase var3 = this.getColumn(this.getFocusedCell().getTableColumn(), 1);
            var1.select(var2.getFocusedIndex(), var3);
         }
      }
   }

   protected void discontinuousSelectPageUp() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = this.hasAnchor() ? this.getAnchor().getRow() : var2.getFocusedIndex();
            int var4 = (Integer)this.onScrollPageUp.call(false);
            if (!var1.isCellSelectionEnabled()) {
               var1.selectRange(var3, var4 - 1);
            }
         }
      }
   }

   protected void discontinuousSelectPageDown() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = this.hasAnchor() ? this.getAnchor().getRow() : var2.getFocusedIndex();
            int var4 = (Integer)this.onScrollPageDown.call(false);
            if (!var1.isCellSelectionEnabled()) {
               var1.selectRange(var3, var4 + 1);
            }
         }
      }
   }

   protected void discontinuousSelectAllToFirstRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex();
            if (!var1.isCellSelectionEnabled()) {
               var1.selectRange(0, var3);
               var2.focus(0);
            } else {
               for (int var4 = 0; var4 < var3; var4++) {
                  var1.select(var4, this.getFocusedCell().getTableColumn());
               }

               var2.focus(0, (TC)this.getFocusedCell().getTableColumn());
            }

            if (this.onMoveToFirstCell != null) {
               this.onMoveToFirstCell.run();
            }
         }
      }
   }

   protected void discontinuousSelectAllToLastRow() {
      TableSelectionModel var1 = this.getSelectionModel();
      if (var1 != null) {
         TableFocusModel var2 = this.getFocusModel();
         if (var2 != null) {
            int var3 = var2.getFocusedIndex() + 1;
            if (!var1.isCellSelectionEnabled()) {
               var1.selectRange(var3, this.getItemCount());
            } else {
               for (int var4 = var3; var4 < this.getItemCount(); var4++) {
                  var1.select(var4, this.getFocusedCell().getTableColumn());
               }
            }

            if (this.onMoveToLastCell != null) {
               this.onMoveToLastCell.run();
            }
         }
      }
   }

   private EventHandler<KeyEvent> focusTraverseLeft() {
      return FocusTraversalInputMap::traverseLeft;
   }

   private EventHandler<KeyEvent> focusTraverseRight() {
      return FocusTraversalInputMap::traverseRight;
   }
}
