package org.controlsfx.control.tableview2.cell;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ComboBox2TableCell<S, T> extends ComboBoxTableCell<S, T> {
   private ComboBox<T> comboBox;
   private final EventHandler<KeyEvent> keyEventHandler = event -> {
      TableViewSelectionModel<S> selectionModel = this.getTableView().getSelectionModel();
      if (this.comboBox != null && event.getCode() != null && selectionModel != null) {
         switch (event.getCode()) {
            case ESCAPE:
               this.comboBox.getEditor().setText(this.getConverter().toString(this.getItem()));
               this.cancelEdit();
               event.consume();
               break;
            case TAB:
               this.cancelEdit();
               event.consume();
               if (selectionModel.isCellSelectionEnabled()) {
                  int columnIndex = this.getTableView().getVisibleLeafIndex(this.getTableColumn());
                  if (event.isShiftDown()) {
                     if (columnIndex > 0) {
                        selectionModel.clearAndSelect(this.getIndex(), this.getTableView().getVisibleLeafColumn(columnIndex - 1));
                     } else if (this.getIndex() > 0) {
                        selectionModel.clearAndSelect(
                           this.getIndex() - 1, this.getTableView().getVisibleLeafColumn(this.getTableView().getVisibleLeafColumns().size() - 1)
                        );
                     }
                  } else if (columnIndex + 1 < this.getTableView().getVisibleLeafColumns().size()) {
                     selectionModel.clearAndSelect(this.getIndex(), this.getTableView().getVisibleLeafColumn(columnIndex + 1));
                  } else if (this.getIndex() < this.getTableView().getItems().size() - 1) {
                     selectionModel.clearAndSelect(this.getIndex() + 1, this.getTableView().getVisibleLeafColumn(0));
                  }
               } else {
                  selectionModel.clearAndSelect(event.isShiftDown() ? this.getIndex() - 1 : this.getIndex() + 1);
               }
               break;
            case UP:
               this.cancelEdit();
               event.consume();
               selectionModel.clearAndSelect(this.getIndex() - 1, this.getTableColumn());
               break;
            case DOWN:
               this.cancelEdit();
               event.consume();
               selectionModel.clearAndSelect(this.getIndex() + 1, this.getTableColumn());
         }
      }
   };
   private final ChangeListener<Boolean> focusListener = (obs, ov, nv) -> {
      if (!nv && this.comboBox != null && this.getConverter() != null) {
         String text = this.comboBox.getEditor().getText();
         T t = (T)this.getConverter().fromString(text);
         this.commitEdit(t);
      }
   };

   @SafeVarargs
   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(T... items) {
      return forTableColumn(null, items);
   }

   @SafeVarargs
   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> converter, T... items) {
      return forTableColumn(converter, FXCollections.observableArrayList(items));
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(ObservableList<T> items) {
      return forTableColumn(null, items);
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> converter, ObservableList<T> items) {
      return list -> new ComboBox2TableCell(converter, items);
   }

   public ComboBox2TableCell() {
      this(FXCollections.observableArrayList());
   }

   @SafeVarargs
   public ComboBox2TableCell(T... items) {
      this(FXCollections.observableArrayList(items));
   }

   @SafeVarargs
   public ComboBox2TableCell(StringConverter<T> converter, T... items) {
      this(converter, FXCollections.observableArrayList(items));
   }

   public ComboBox2TableCell(ObservableList<T> items) {
      this(null, items);
   }

   public ComboBox2TableCell(StringConverter<T> converter, ObservableList<T> items) {
      super(converter, items);
      this.setComboBoxEditable(true);
      this.graphicProperty().addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            if (ComboBox2TableCell.this.getGraphic() != null && ComboBox2TableCell.this.getGraphic() instanceof ComboBox) {
               ComboBox2TableCell.this.comboBox = (ComboBox<T>)ComboBox2TableCell.this.getGraphic();
               ComboBox2TableCell.this.comboBox.editableProperty().addListener((obs, ov, nv) -> ComboBox2TableCell.this.updateListeners(nv));
               ComboBox2TableCell.this.updateListeners(ComboBox2TableCell.this.comboBox.isEditable());
               ComboBox2TableCell.this.graphicProperty().removeListener(this);
            }
         }
      });
   }

   public void commitEdit(T item) {
      if (!this.isEditing() && !item.equals(this.getItem())) {
         TableView<S> table = this.getTableView();
         if (table != null) {
            TableColumn<S, T> column = this.getTableColumn();
            CellEditEvent<S, T> event = new CellEditEvent(table, new TablePosition(table, this.getIndex(), column), TableColumn.editCommitEvent(), item);
            Event.fireEvent(column, event);
         }
      }

      super.commitEdit(item);
   }

   private void updateListeners(boolean editable) {
      if (editable) {
         this.comboBox.getEditor().focusedProperty().addListener(this.focusListener);
         this.comboBox.addEventFilter(KeyEvent.KEY_PRESSED, this.keyEventHandler);
      } else {
         this.comboBox.getEditor().focusedProperty().removeListener(this.focusListener);
         this.comboBox.removeEventFilter(KeyEvent.KEY_PRESSED, this.keyEventHandler);
      }
   }
}
