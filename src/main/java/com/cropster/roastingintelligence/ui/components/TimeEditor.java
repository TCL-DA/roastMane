package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.TextFieldUtils;
import com.cropster.util.text.DurationFormat;
import java.net.URL;
import java.text.Format;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.FormatStringConverter;
import org.apache.commons.lang3.StringUtils;

public class TimeEditor extends BorderPane implements Initializable {
   @FXML
   private TextField input;
   private final ObjectProperty<Long> valueProperty = new SimpleObjectProperty();
   private final BooleanProperty negativeAllowed = new SimpleBooleanProperty(true) {
      protected void invalidated() {
         Long timeValue = TimeEditor.this.getValue();
         TimeEditor.this.valueProperty.unbindBidirectional(TimeEditor.this.formatterValueProperty());
         TimeEditor.this.timeFormat = DurationFormat.getDurationInstance(this.get());
         TimeEditor.this.initializeFormatter();
         TimeEditor.this.setValue(timeValue);
      }
   };
   private final ChangeListener<Boolean> focusListener = new TimeEditor.FocusListener();
   private Format timeFormat = DurationFormat.getDurationInstance(this.negativeAllowed.get());
   private String initialInput;

   public TimeEditor() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.initializeFormatter();
      this.input.focusedProperty().addListener((s, o, n) -> {
         if (n) {
            this.input.setPromptText(this.input.getText());
         }
      });
      this.input.focusedProperty().addListener(this.focusListener);
      this.input.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> this.input.setPromptText(this.input.getText()));
   }

   private void initializeFormatter() {
      TextFieldUtils.setTextFormatter(this.input, new TextFormatter(new FormatStringConverter(this.timeFormat)));
      this.valueProperty.bindBidirectional(this.formatterValueProperty());
   }

   private ObjectProperty<Long> formatterValueProperty() {
      return ((TextFormatter<Long>)this.input.getTextFormatter()).valueProperty();
   }

   public TextField getInput() {
      return this.input;
   }

   public ObjectProperty<Long> valueProperty() {
      return this.valueProperty;
   }

   public Long getValue() {
      return (Long)this.valueProperty.get();
   }

   public void setValue(Long value) {
      this.valueProperty.set(value);
   }

   public ReadOnlyBooleanProperty negativeAllowedProperty() {
      return this.negativeAllowed;
   }

   public boolean isNegativeAllowed() {
      return this.negativeAllowed.get();
   }

   public void setNegativeAllowed(boolean negativeAllowed) {
      this.negativeAllowed.set(negativeAllowed);
   }

   private final class FocusListener implements ChangeListener<Boolean> {
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
         if (newValue) {
            TimeEditor.this.initialInput = TimeEditor.this.input.getText();
         } else if (StringUtils.isBlank(TimeEditor.this.input.getText())) {
            TimeEditor.this.input.setText(TimeEditor.this.initialInput);
         }
      }
   }
}
