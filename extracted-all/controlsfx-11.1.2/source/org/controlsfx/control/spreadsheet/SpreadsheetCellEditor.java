package org.controlsfx.control.spreadsheet;

import impl.org.controlsfx.i18n.Localization;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public abstract class SpreadsheetCellEditor {
   private static final double MAX_EDITOR_HEIGHT = 50.0;
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.##########");
   SpreadsheetView view;

   public SpreadsheetCellEditor(SpreadsheetView view) {
      this.view = view;
   }

   public final void endEdit(boolean b) {
      this.view.getCellsViewSkin().getSpreadsheetCellEditorImpl().endEdit(b);
   }

   public void startEdit(Object item) {
      this.startEdit(item, null);
   }

   public abstract void startEdit(Object var1, String var2, Object... var3);

   public abstract Control getEditor();

   public abstract String getControlValue();

   public abstract void end();

   public double getMaxHeight() {
      return 50.0;
   }

   public static class DateEditor extends SpreadsheetCellEditor {
      private final DatePicker datePicker;
      private EventHandler<KeyEvent> eh;
      private ChangeListener<LocalDate> cl;
      private boolean ending = false;

      public DateEditor(SpreadsheetView view, StringConverter<LocalDate> converter) {
         super(view);
         this.datePicker = new DatePicker();
         this.datePicker.setConverter(converter);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof LocalDate) {
            this.datePicker.setValue((LocalDate)value);
         }

         this.attachEnterEscapeEventHandler();
         this.datePicker.show();
         this.datePicker.getEditor().requestFocus();
      }

      @Override
      public void end() {
         if (this.datePicker.isShowing()) {
            this.datePicker.hide();
         }

         this.datePicker.removeEventFilter(KeyEvent.KEY_PRESSED, this.eh);
         this.datePicker.valueProperty().removeListener(this.cl);
      }

      public DatePicker getEditor() {
         return this.datePicker;
      }

      @Override
      public String getControlValue() {
         return this.datePicker.getEditor().getText();
      }

      private void attachEnterEscapeEventHandler() {
         this.eh = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ENTER) {
                  DateEditor.this.ending = true;
                  DateEditor.this.endEdit(true);
                  DateEditor.this.ending = false;
               } else if (t.getCode() == KeyCode.ESCAPE) {
                  DateEditor.this.endEdit(false);
               }
            }
         };
         this.datePicker.addEventFilter(KeyEvent.KEY_PRESSED, this.eh);
         this.cl = new ChangeListener<LocalDate>() {
            public void changed(ObservableValue<? extends LocalDate> arg0, LocalDate arg1, LocalDate arg2) {
               if (!DateEditor.this.ending) {
                  DateEditor.this.endEdit(true);
               }
            }
         };
         this.datePicker.valueProperty().addListener(this.cl);
      }
   }

   public static class DoubleEditor extends SpreadsheetCellEditor {
      private final TextField tf = new TextField() {
         public void insertText(int index, String text) {
            String fixedText = this.fixText(text);
            super.insertText(index, fixedText);
         }

         public void replaceText(int start, int end, String text) {
            String fixedText = this.fixText(text);
            super.replaceText(start, end, fixedText);
         }

         public void replaceText(IndexRange range, String text) {
            this.replaceText(range.getStart(), range.getEnd(), text);
         }

         private String fixText(String text) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Localization.getLocale());
            text = text.replace(' ', ' ');
            return text.replaceAll("\\.", Character.toString(symbols.getDecimalSeparator()));
         }
      };

      public DoubleEditor(SpreadsheetView view) {
         super(view);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof Double) {
            SpreadsheetCellEditor.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Localization.getLocale()));
            this.tf.setText(((Double)value).isNaN() ? "" : SpreadsheetCellEditor.decimalFormat.format(value));
         } else {
            this.tf.setText(null);
         }

         this.tf.getStyleClass().removeAll(new String[]{"error"});
         this.attachEnterEscapeEventHandler();
         this.tf.requestFocus();
         this.tf.selectAll();
      }

      @Override
      public void end() {
         this.tf.setOnKeyPressed(null);
      }

      public TextField getEditor() {
         return this.tf;
      }

      @Override
      public String getControlValue() {
         NumberFormat format = NumberFormat.getInstance(Localization.getLocale());
         ParsePosition parsePosition = new ParsePosition(0);
         if (this.tf.getText() != null) {
            Number number = format.parse(this.tf.getText(), parsePosition);
            if (number != null && parsePosition.getIndex() == this.tf.getText().length()) {
               return String.valueOf(number.doubleValue());
            }
         }

         return this.tf.getText();
      }

      private void attachEnterEscapeEventHandler() {
         this.tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ENTER) {
                  try {
                     if (DoubleEditor.this.tf.getText().equals("")) {
                        DoubleEditor.this.endEdit(true);
                     } else {
                        DoubleEditor.this.tryParsing();
                        DoubleEditor.this.endEdit(true);
                     }
                  } catch (Exception var3) {
                  }
               } else if (t.getCode() == KeyCode.ESCAPE) {
                  DoubleEditor.this.endEdit(false);
               }
            }
         });
         this.tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               try {
                  if (DoubleEditor.this.tf.getText().equals("")) {
                     DoubleEditor.this.tf.getStyleClass().removeAll(new String[]{"error"});
                  } else {
                     DoubleEditor.this.tryParsing();
                     DoubleEditor.this.tf.getStyleClass().removeAll(new String[]{"error"});
                  }
               } catch (Exception var3) {
                  DoubleEditor.this.tf.getStyleClass().add("error");
               }
            }
         });
      }

      private void tryParsing() throws ParseException {
         NumberFormat format = NumberFormat.getNumberInstance(Localization.getLocale());
         ParsePosition parsePosition = new ParsePosition(0);
         format.parse(this.tf.getText(), parsePosition);
         if (parsePosition.getIndex() != this.tf.getText().length()) {
            throw new ParseException("Invalid input", parsePosition.getIndex());
         }
      }
   }

   public static class IntegerEditor extends SpreadsheetCellEditor {
      private final TextField tf = new TextField();

      public IntegerEditor(SpreadsheetView view) {
         super(view);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof Integer) {
            this.tf.setText(Integer.toString((Integer)value));
         } else {
            this.tf.setText(null);
         }

         this.tf.getStyleClass().removeAll(new String[]{"error"});
         this.attachEnterEscapeEventHandler();
         this.tf.requestFocus();
         this.tf.selectAll();
      }

      @Override
      public void end() {
         this.tf.setOnKeyPressed(null);
      }

      public TextField getEditor() {
         return this.tf;
      }

      @Override
      public String getControlValue() {
         return this.tf.getText();
      }

      private void attachEnterEscapeEventHandler() {
         this.tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ENTER) {
                  try {
                     if (IntegerEditor.this.tf.getText().equals("")) {
                        IntegerEditor.this.endEdit(true);
                     } else {
                        Integer.parseInt(IntegerEditor.this.tf.getText());
                        IntegerEditor.this.endEdit(true);
                     }
                  } catch (Exception var3) {
                  }
               } else if (t.getCode() == KeyCode.ESCAPE) {
                  IntegerEditor.this.endEdit(false);
               }
            }
         });
         this.tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               try {
                  if (IntegerEditor.this.tf.getText().equals("")) {
                     IntegerEditor.this.tf.getStyleClass().removeAll(new String[]{"error"});
                  } else {
                     Integer.parseInt(IntegerEditor.this.tf.getText());
                     IntegerEditor.this.tf.getStyleClass().removeAll(new String[]{"error"});
                  }
               } catch (Exception var3) {
                  IntegerEditor.this.tf.getStyleClass().add("error");
               }
            }
         });
      }
   }

   public static class ListEditor<R> extends SpreadsheetCellEditor {
      private final List<String> itemList;
      private final ComboBox<String> cb;
      private String originalValue;

      public ListEditor(SpreadsheetView view, List<String> itemList) {
         super(view);
         this.itemList = itemList;
         this.cb = new ComboBox();
         this.cb.setVisibleRowCount(5);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof String) {
            this.originalValue = value.toString();
         } else {
            this.originalValue = null;
         }

         ObservableList<String> items = FXCollections.observableList(this.itemList);
         this.cb.setItems(items);
         this.cb.setValue(this.originalValue);
         this.attachEnterEscapeEventHandler();
         this.cb.show();
         this.cb.requestFocus();
      }

      @Override
      public void end() {
         this.cb.setOnKeyPressed(null);
      }

      public ComboBox<String> getEditor() {
         return this.cb;
      }

      @Override
      public String getControlValue() {
         return (String)this.cb.getSelectionModel().getSelectedItem();
      }

      private void attachEnterEscapeEventHandler() {
         this.cb.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ESCAPE) {
                  ListEditor.this.cb.setValue(ListEditor.this.originalValue);
                  ListEditor.this.endEdit(false);
               } else if (t.getCode() == KeyCode.ENTER) {
                  ListEditor.this.endEdit(true);
               }
            }
         });
      }
   }

   public static class ObjectEditor extends SpreadsheetCellEditor {
      private final TextField tf = new TextField();

      public ObjectEditor(SpreadsheetView view) {
         super(view);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof String) {
            this.tf.setText(value.toString());
         }

         this.attachEnterEscapeEventHandler();
         this.tf.requestFocus();
         this.tf.end();
      }

      @Override
      public String getControlValue() {
         return this.tf.getText();
      }

      @Override
      public void end() {
         this.tf.setOnKeyPressed(null);
      }

      public TextField getEditor() {
         return this.tf;
      }

      private void attachEnterEscapeEventHandler() {
         this.tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ENTER) {
                  ObjectEditor.this.endEdit(true);
               } else if (t.getCode() == KeyCode.ESCAPE) {
                  ObjectEditor.this.endEdit(false);
               }
            }
         });
      }
   }

   public static class StringEditor extends SpreadsheetCellEditor {
      private final TextField tf = new TextField();

      public StringEditor(SpreadsheetView view) {
         super(view);
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof String || value == null) {
            this.tf.setText((String)value);
         }

         this.attachEnterEscapeEventHandler();
         this.tf.requestFocus();
         this.tf.selectAll();
      }

      @Override
      public String getControlValue() {
         return this.tf.getText();
      }

      @Override
      public void end() {
         this.tf.setOnKeyPressed(null);
      }

      public TextField getEditor() {
         return this.tf;
      }

      private void attachEnterEscapeEventHandler() {
         this.tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent t) {
               if (t.getCode() == KeyCode.ENTER) {
                  StringEditor.this.endEdit(true);
               } else if (t.getCode() == KeyCode.ESCAPE) {
                  StringEditor.this.endEdit(false);
               }
            }
         });
      }
   }

   public static class TextAreaEditor extends SpreadsheetCellEditor {
      private final TextArea textArea = new TextArea();

      public TextAreaEditor(SpreadsheetView view) {
         super(view);
         this.textArea.setWrapText(true);
         this.textArea.minHeightProperty().bind(this.textArea.maxHeightProperty());
      }

      @Override
      public void startEdit(Object value, String format, Object... options) {
         if (value instanceof String || value == null) {
            this.textArea.setText((String)value);
         }

         this.attachEnterEscapeEventHandler();
         this.textArea.requestFocus();
         this.textArea.selectAll();
      }

      @Override
      public String getControlValue() {
         return this.textArea.getText();
      }

      @Override
      public void end() {
         this.textArea.setOnKeyPressed(null);
      }

      public TextArea getEditor() {
         return this.textArea;
      }

      @Override
      public double getMaxHeight() {
         return Double.MAX_VALUE;
      }

      private void attachEnterEscapeEventHandler() {
         this.textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
               if (keyEvent.getCode() == KeyCode.ENTER) {
                  if (keyEvent.isShiftDown()) {
                     TextAreaEditor.this.textArea.replaceSelection("\n");
                  } else {
                     TextAreaEditor.this.endEdit(true);
                  }
               } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                  TextAreaEditor.this.endEdit(false);
               } else if (keyEvent.getCode() == KeyCode.TAB) {
                  if (keyEvent.isShiftDown()) {
                     TextAreaEditor.this.textArea.replaceSelection("\t");
                     keyEvent.consume();
                  } else {
                     TextAreaEditor.this.endEdit(true);
                  }
               }
            }
         });
      }
   }
}
