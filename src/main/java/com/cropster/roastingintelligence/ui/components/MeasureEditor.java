package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.TextFieldUtils;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Locale.Category;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.Measure;

public class MeasureEditor extends BorderPane implements Initializable {
   @FXML
   private TextField input;
   @FXML
   private Label unitLabel;
   private final ChangeListener<Boolean> focusListener = new MeasureEditor.FocusListener();
   private final ObjectProperty<Unit> unit = new SimpleObjectProperty();
   private ObjectProperty<Number> value = new SimpleObjectProperty();
   private final BooleanProperty nullable = new SimpleBooleanProperty();
   private final BooleanProperty fractionEnabled = new SimpleBooleanProperty(true);
   private final BooleanProperty hideUnit = new SimpleBooleanProperty();
   private String initialInput;
   private int maximumFractionDigits = 3;
   private boolean consumeEnterKeyEvent = false;

   public MeasureEditor() {
      JavaFxUtil.loadComponent(this);
   }

   public MeasureEditor(Unit unit) {
      this();
      this.setUnit(unit);
   }

   private NumberFormat createFormat(Locale locale) {
      DecimalFormatSymbols formatSymbols = DecimalFormatSymbols.getInstance(locale);
      if (formatSymbols.getMinusSign() == 8722) {
         formatSymbols.setMinusSign('-');
      }

      DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getInstance(locale);
      numberFormat.setDecimalFormatSymbols(formatSymbols);
      return numberFormat;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.unitLabel
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> this.unit.get() != null ? org.cropster.roastingintelligence.util.Messages.m_((Enum<?>)this.unit.get()) : "?", new Observable[]{this.unit}
            )
         );
      this.unitLabel
         .tooltipProperty()
         .bind(
            Bindings.createObjectBinding(
               () -> this.unit.get() != null && org.cropster.roastingintelligence.util.Messages.m_((Enum<?>)this.unit.get()).length() > 3
                     ? JavaFxUtil.configureTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_((Enum<?>)this.unit.get())))
                     : null,
               new Observable[]{this.unit}
            )
         );
      this.unitLabel.visibleProperty().bind(this.hideUnit.not());
      this.updateFormatter();
      this.fractionEnabled.addListener((s, o, n) -> this.updateFormatter());
      this.input.focusedProperty().addListener((s, o, n) -> {
         if (n) {
            this.input.setPromptText(this.input.getText());
         }
      });
      this.input.focusedProperty().addListener(this.focusListener);
      this.input.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> this.input.setPromptText(this.input.getText()));
      this.input.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.ENTER && this.consumeEnterKeyEvent) {
            e.consume();
         }
      });
   }

   public boolean isConsumeEnterKeyEvent() {
      return this.consumeEnterKeyEvent;
   }

   public void setConsumeEnterKeyEvent(boolean consumeEnterKeyEvent) {
      this.consumeEnterKeyEvent = consumeEnterKeyEvent;
   }

   public void enableUnitValueCoupling(boolean relative) {
      this.unit.addListener(new MeasureEditor.UnitListener(relative));
   }

   public TextField getInput() {
      return this.input;
   }

   public Label getUnitLabel() {
      return this.unitLabel;
   }

   public ObjectProperty<Unit> unitProperty() {
      return this.unit;
   }

   public Unit getUnit() {
      return (Unit)this.unit.get();
   }

   public void setUnit(Unit unit) {
      this.unit.set(unit);
   }

   public ObjectProperty<Number> valueProperty() {
      return this.value;
   }

   public Number getValue() {
      return (Number)this.value.get();
   }

   public void setValue(Number value) {
      this.value.set(value);
   }

   public BooleanProperty editableProperty() {
      return this.input.editableProperty();
   }

   public boolean isEditable() {
      return this.input.isEditable();
   }

   public void setEditable(boolean editable) {
      this.input.setEditable(editable);
   }

   public BooleanProperty nullableProperty() {
      return this.nullable;
   }

   public boolean isNullable() {
      return this.nullable.get();
   }

   public void setNullable(boolean nullable) {
      this.nullable.set(nullable);
      if (nullable) {
         this.input.focusedProperty().removeListener(this.focusListener);
      } else {
         this.input.focusedProperty().addListener(this.focusListener);
      }
   }

   public Measure getMeasure() {
      return this.value.get() != null && this.unit.get() != null ? new Measure(((Number)this.value.get()).doubleValue(), (Unit)this.unit.get()) : null;
   }

   public void setMeasure(Measure measure) {
      if (measure != null) {
         this.value.set(measure.getAmount());
         this.unit.set(measure.getUnit());
      } else {
         this.value.set(null);
         this.unit.set(null);
      }
   }

   public boolean isFractionEnabled() {
      return this.fractionEnabled.get();
   }

   public BooleanProperty fractionEnabledProperty() {
      return this.fractionEnabled;
   }

   public void setFractionEnabled(boolean fractionEnabled) {
      this.fractionEnabled.set(fractionEnabled);
   }

   public BooleanProperty hideUnitProperty() {
      return this.hideUnit;
   }

   public boolean isHideUnit() {
      return this.hideUnit.get();
   }

   public void setHideUnit(boolean hideUnit) {
      this.hideUnit.set(hideUnit);
   }

   public void setTextFormatter(TextFormatter<?> formatter) {
      Validators.Validator validator = Validators.getValidator(this);
      if (validator != null) {
         validator.uninstall();
      }

      TextFieldUtils.setTextFormatter(this.input, formatter);
      this.value = ((TextFormatter<Number>)this.input.getTextFormatter()).valueProperty();
      if (validator != null) {
         validator.install();
      }
   }

   private void updateFormatter() {
      NumberFormat format = this.createFormat(Locale.getDefault(Category.FORMAT));
      if (this.maximumFractionDigits == -1) {
         this.maximumFractionDigits = format.getMaximumFractionDigits();
      }

      format.setMinimumFractionDigits(0);
      format.setMaximumFractionDigits(this.isFractionEnabled() ? this.maximumFractionDigits : 0);
      this.setTextFormatter(new TextFormatter(new NumberStringConverter(format)));
   }

   private final class FocusListener implements ChangeListener<Boolean> {
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
         if (newValue) {
            MeasureEditor.this.initialInput = MeasureEditor.this.input.getText();
         } else if (StringUtils.isBlank(MeasureEditor.this.input.getText())) {
            MeasureEditor.this.input.setText(MeasureEditor.this.initialInput);
         }
      }
   }

   private class UnitListener implements ChangeListener<Unit> {
      private final boolean relative;

      UnitListener(boolean relative) {
         this.relative = relative;
      }

      public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {
         if (MeasureEditor.this.getValue() != null && oldValue != null) {
            BigDecimal v = BigDecimal.valueOf(MeasureEditor.this.getValue().doubleValue());
            v = this.relative ? newValue.fromRelative(v, oldValue) : newValue.from(v, oldValue);
            MeasureEditor.this.setValue(v.setScale(0, RoundingMode.HALF_EVEN).doubleValue());
         }
      }
   }
}
