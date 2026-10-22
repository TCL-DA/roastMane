package org.controlsfx.property.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.dialog.FontSelectorDialog;

public class Editors {
   private Editors() {
   }

   public static final PropertyEditor<?> createTextEditor(PropertySheet.Item property) {
      return new AbstractPropertyEditor<String, TextField>(property, new TextField()) {
         {
            Editors.enableAutoSelectAll((TextInputControl)this.getEditor());
         }

         protected StringProperty getObservableValue() {
            return this.getEditor().textProperty();
         }

         public void setValue(String value) {
            this.getEditor().setText(value);
         }
      };
   }

   public static final PropertyEditor<?> createNumericEditor(final PropertySheet.Item property) {
      return new AbstractPropertyEditor<Number, NumericField>(property, new NumericField((Class<? extends Number>)property.getType())) {
         private Class<? extends Number> sourceClass = (Class<? extends Number>)property.getType();

         {
            Editors.enableAutoSelectAll((TextInputControl)this.getEditor());
         }

         @Override
         protected ObservableValue<Number> getObservableValue() {
            return this.getEditor().valueProperty();
         }

         public Number getValue() {
            if (this.sourceClass == Byte.class) {
               return Byte.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == Short.class) {
               return Short.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == Integer.class) {
               return Integer.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == Long.class) {
               return Long.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == Float.class) {
               return Float.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == Double.class) {
               return Double.valueOf(this.getEditor().getText());
            } else if (this.sourceClass == BigInteger.class) {
               return new BigInteger(this.getEditor().getText());
            } else if (this.sourceClass == BigDecimal.class) {
               return new BigDecimal(this.getEditor().getText());
            } else {
               try {
                  return this.sourceClass.getConstructor(String.class).newInstance(this.getEditor().getText());
               } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var2) {
                  var2.printStackTrace();
                  return null;
               }
            }
         }

         public void setValue(Number value) {
            this.sourceClass = (Class<? extends Number>)value.getClass();
            this.getEditor().setValue(value);
            this.getEditor().setText(value.toString());
         }
      };
   }

   public static final PropertyEditor<?> createCheckEditor(PropertySheet.Item property) {
      return new AbstractPropertyEditor<Boolean, CheckBox>(property, new CheckBox()) {
         protected BooleanProperty getObservableValue() {
            return this.getEditor().selectedProperty();
         }

         public void setValue(Boolean value) {
            this.getEditor().setSelected(value);
         }
      };
   }

   public static final <T> PropertyEditor<?> createChoiceEditor(PropertySheet.Item property, final Collection<T> choices) {
      return new AbstractPropertyEditor<T, ComboBox<T>>(property, new ComboBox()) {
         {
            this.getEditor().setItems(FXCollections.observableArrayList(choices));
         }

         @Override
         protected ObservableValue<T> getObservableValue() {
            return this.getEditor().getSelectionModel().selectedItemProperty();
         }

         @Override
         public void setValue(T value) {
            this.getEditor().getSelectionModel().select(value);
         }
      };
   }

   public static final PropertyEditor<?> createColorEditor(PropertySheet.Item property) {
      return new AbstractPropertyEditor<Color, ColorPicker>(property, new ColorPicker()) {
         @Override
         protected ObservableValue<Color> getObservableValue() {
            return this.getEditor().valueProperty();
         }

         public void setValue(Color value) {
            this.getEditor().setValue(value);
         }
      };
   }

   public static final PropertyEditor<?> createDateEditor(PropertySheet.Item property) {
      return new AbstractPropertyEditor<LocalDate, DatePicker>(property, new DatePicker()) {
         @Override
         protected ObservableValue<LocalDate> getObservableValue() {
            return this.getEditor().valueProperty();
         }

         public void setValue(LocalDate value) {
            this.getEditor().setValue(value);
         }
      };
   }

   public static final PropertyEditor<?> createFontEditor(PropertySheet.Item property) {
      return new AbstractPropertyEditor<Font, AbstractObjectField<Font>>(property, new AbstractObjectField<Font>() {
         @Override
         protected Class<Font> getType() {
            return Font.class;
         }

         protected String objectToString(Font font) {
            return font == null ? "" : String.format("%s, %.1f", font.getName(), font.getSize());
         }

         protected Font edit(Font font) {
            FontSelectorDialog dlg = new FontSelectorDialog(font);
            Optional<Font> optionalFont = dlg.showAndWait();
            return optionalFont.orElse(null);
         }
      }) {
         @Override
         protected ObservableValue<Font> getObservableValue() {
            return this.getEditor().getObjectProperty();
         }

         public void setValue(Font value) {
            this.getEditor().getObjectProperty().set(value);
         }
      };
   }

   public static final Optional<PropertyEditor<?>> createCustomEditor(PropertySheet.Item property) {
      return property.getPropertyEditorClass()
         .map(
            cls -> {
               try {
                  Constructor<?> cn = cls.getConstructor(PropertySheet.Item.class);
                  if (cn != null) {
                     return (PropertyEditor<?>)cn.newInstance(property);
                  }
               } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var3) {
                  var3.printStackTrace();
               }

               return null;
            }
         );
   }

   private static void enableAutoSelectAll(TextInputControl control) {
      control.focusedProperty().addListener((o, oldValue, newValue) -> {
         if (newValue) {
            Platform.runLater(() -> control.selectAll());
         }
      });
   }
}
