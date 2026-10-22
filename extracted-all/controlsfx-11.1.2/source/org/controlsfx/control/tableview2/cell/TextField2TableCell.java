package org.controlsfx.control.tableview2.cell;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class TextField2TableCell<S, T> extends TextFieldTableCell<S, T> {
   private TextField textField;

   public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
      return forTableColumn(new DefaultStringConverter());
   }

   public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(StringConverter<T> converter) {
      return list -> new TextField2TableCell(converter);
   }

   public TextField2TableCell() {
      this(null);
   }

   public TextField2TableCell(final StringConverter<T> converter) {
      super(converter);
      this.graphicProperty()
         .addListener(
            new InvalidationListener() {
               public void invalidated(Observable observable) {
                  if (TextField2TableCell.this.getGraphic() != null && TextField2TableCell.this.getGraphic() instanceof TextField) {
                     TextField2TableCell.this.textField = (TextField)TextField2TableCell.this.getGraphic();
                     TextField2TableCell.this.textField.focusedProperty().addListener((obs, ov, nv) -> {
                        if (!nv) {
                           TextField2TableCell.this.commitEdit(converter.fromString(TextField2TableCell.this.textField.getText()));
                        }
                     });
                     TextField2TableCell.this.textField
                        .addEventFilter(
                           KeyEvent.KEY_PRESSED,
                           event -> {
                              TableViewSelectionModel<S> selectionModel = TextField2TableCell.this.getTableView().getSelectionModel();
                              if (event.getCode() != null && selectionModel != null) {
                                 switch (event.getCode()) {
                                    case ESCAPE:
                                       TextField2TableCell.this.textField.setText(converter.toString(TextField2TableCell.this.getItem()));
                                       TextField2TableCell.this.cancelEdit();
                                       event.consume();
                                       break;
                                    case TAB:
                                       TextField2TableCell.this.cancelEdit();
                                       event.consume();
                                       if (selectionModel.isCellSelectionEnabled()) {
                                          int columnIndex = TextField2TableCell.this.getTableView()
                                             .getVisibleLeafIndex(TextField2TableCell.this.getTableColumn());
                                          if (event.isShiftDown()) {
                                             if (columnIndex > 0) {
                                                selectionModel.clearAndSelect(
                                                   TextField2TableCell.this.getIndex(),
                                                   TextField2TableCell.this.getTableView().getVisibleLeafColumn(columnIndex - 1)
                                                );
                                             } else if (TextField2TableCell.this.getIndex() > 0) {
                                                selectionModel.clearAndSelect(
                                                   TextField2TableCell.this.getIndex() - 1,
                                                   TextField2TableCell.this.getTableView()
                                                      .getVisibleLeafColumn(TextField2TableCell.this.getTableView().getVisibleLeafColumns().size() - 1)
                                                );
                                             }
                                          } else if (columnIndex + 1 < TextField2TableCell.this.getTableView().getVisibleLeafColumns().size()) {
                                             selectionModel.clearAndSelect(
                                                TextField2TableCell.this.getIndex(),
                                                TextField2TableCell.this.getTableView().getVisibleLeafColumn(columnIndex + 1)
                                             );
                                          } else if (TextField2TableCell.this.getIndex() < TextField2TableCell.this.getTableView().getItems().size() - 1) {
                                             selectionModel.clearAndSelect(
                                                TextField2TableCell.this.getIndex() + 1, TextField2TableCell.this.getTableView().getVisibleLeafColumn(0)
                                             );
                                          }
                                       } else {
                                          selectionModel.clearAndSelect(
                                             event.isShiftDown() ? TextField2TableCell.this.getIndex() - 1 : TextField2TableCell.this.getIndex() + 1
                                          );
                                       }
                                       break;
                                    case UP:
                                       TextField2TableCell.this.cancelEdit();
                                       event.consume();
                                       selectionModel.clearAndSelect(TextField2TableCell.this.getIndex() - 1, TextField2TableCell.this.getTableColumn());
                                       break;
                                    case DOWN:
                                       TextField2TableCell.this.cancelEdit();
                                       event.consume();
                                       selectionModel.clearAndSelect(TextField2TableCell.this.getIndex() + 1, TextField2TableCell.this.getTableColumn());
                                 }
                              }
                           }
                        );
                     TextField2TableCell.this.graphicProperty().removeListener(this);
                  }
               }
            }
         );
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
}
