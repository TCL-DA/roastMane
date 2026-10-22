package impl.org.controlsfx.spreadsheet;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class GridCellEditor {
   private final SpreadsheetHandle handle;
   private SpreadsheetCell modelCell;
   private CellView viewCell;
   private BooleanExpression focusProperty;
   private boolean editing = false;
   private SpreadsheetCellEditor spreadsheetCellEditor;
   private KeyCode lastKeyPressed;
   private final EventHandler<KeyEvent> enterKeyPressed = new EventHandler<KeyEvent>() {
      public void handle(KeyEvent t) {
         GridCellEditor.this.lastKeyPressed = t.getCode();
      }
   };
   private final ChangeListener<Boolean> focusListener = new ChangeListener<Boolean>() {
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocus) {
         if (!isFocus) {
            GridCellEditor.this.endEdit(true);
         }
      }
   };
   private final InvalidationListener endEditionListener = new InvalidationListener() {
      public void invalidated(Observable observable) {
         GridCellEditor.this.endEdit(true);
      }
   };

   public GridCellEditor(SpreadsheetHandle handle) {
      this.handle = handle;
   }

   public void updateDataCell(SpreadsheetCell cell) {
      this.modelCell = cell;
   }

   public void updateSpreadsheetCell(CellView cell) {
      this.viewCell = cell;
   }

   public void updateSpreadsheetCellEditor(SpreadsheetCellEditor spreadsheetCellEditor) {
      this.spreadsheetCellEditor = spreadsheetCellEditor;
   }

   public void endEdit(boolean commitValue) {
      if (commitValue && this.editing) {
         SpreadsheetView view = this.handle.getView();
         boolean match = this.modelCell.getCellType().match(this.spreadsheetCellEditor.getControlValue(), this.modelCell.getOptionsForEditor());
         if (match && this.viewCell != null) {
            Object value = this.modelCell.getCellType().convertValue(this.spreadsheetCellEditor.getControlValue());
            view.getGrid().setCellValue(this.modelCell.getRow(), this.modelCell.getColumn(), value);
            this.editing = false;
            this.viewCell.commitEdit(this.modelCell);
            this.end();
            this.spreadsheetCellEditor.end();
            if (KeyCode.ENTER.equals(this.lastKeyPressed)) {
               this.handle.getCellsViewSkin().getBehavior().selectCell(1, 0);
            } else if (KeyCode.TAB.equals(this.lastKeyPressed)) {
               this.handle.getView().getSelectionModel().clearAndSelectRightCell();
               this.handle.getCellsViewSkin().scrollHorizontally();
            }
         }
      }

      if (this.editing) {
         this.editing = false;
         if (this.viewCell != null) {
            this.viewCell.cancelEdit();
         }

         this.end();
         if (this.spreadsheetCellEditor != null) {
            this.spreadsheetCellEditor.end();
         }
      }
   }

   public boolean isEditing() {
      return this.editing;
   }

   public SpreadsheetCell getModelCell() {
      return this.modelCell;
   }

   void startEdit() {
      this.lastKeyPressed = null;
      this.editing = true;
      this.handle.getGridView().addEventFilter(KeyEvent.KEY_PRESSED, this.enterKeyPressed);
      this.handle.getCellsViewSkin().getVBar().valueProperty().addListener(this.endEditionListener);
      this.handle.getCellsViewSkin().getHBar().valueProperty().addListener(this.endEditionListener);
      Control editor = this.spreadsheetCellEditor.getEditor();
      Object value = this.modelCell.getItem();
      Double maxHeight = Math.min(this.viewCell.getHeight(), this.spreadsheetCellEditor.getMaxHeight());
      if (editor != null) {
         this.viewCell.setGraphic(editor);
         editor.setMaxHeight(maxHeight);
         editor.setPrefWidth(this.viewCell.getWidth());
      }

      this.spreadsheetCellEditor.startEdit(value, this.modelCell.getFormat(), this.modelCell.getOptionsForEditor());
      if (editor != null) {
         this.focusProperty = this.getFocusProperty(editor);
         this.focusProperty.addListener(this.focusListener);
      }
   }

   private void end() {
      if (this.focusProperty != null) {
         this.focusProperty.removeListener(this.focusListener);
         this.focusProperty = null;
      }

      this.handle.getCellsViewSkin().getVBar().valueProperty().removeListener(this.endEditionListener);
      this.handle.getCellsViewSkin().getHBar().valueProperty().removeListener(this.endEditionListener);
      this.handle.getGridView().removeEventFilter(KeyEvent.KEY_PRESSED, this.enterKeyPressed);
      this.modelCell = null;
      this.viewCell = null;
   }

   private BooleanExpression getFocusProperty(Control control) {
      return (BooleanExpression)(control instanceof TextArea ? Bindings.createBooleanBinding(() -> {
         if (this.handle.getView().getScene() == null) {
            return false;
         } else {
            for (Node n = this.handle.getView().getScene().getFocusOwner(); n != null; n = n.getParent()) {
               if (n == control) {
                  return true;
               }
            }

            return false;
         }
      }, new Observable[]{this.handle.getView().getScene().focusOwnerProperty()}) : control.focusedProperty());
   }
}
