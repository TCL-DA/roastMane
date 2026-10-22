package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.TimeEditor;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class FinishPanel extends GridPane implements Initializable {
   private static final int MAX_COMMENT_LENGTH = 250;
   @FXML
   private TimeEditor startTimeEditor;
   @FXML
   private TimeEditor endTimeEditor;
   @FXML
   private MeasureEditor startWeightEditor;
   @FXML
   private MeasureEditor endWeightEditor;
   @FXML
   private Label weightLossLabel;
   @FXML
   private MeasureEditor roastValue1Editor;
   @FXML
   private MeasureEditor roastValue2Editor;
   @FXML
   private TextArea notesArea;
   private final ObjectProperty<Long> startTime = new SimpleObjectProperty();
   private final ObjectProperty<Long> endTime = new SimpleObjectProperty();
   private final ObjectProperty<Unit> unit = new SimpleObjectProperty();
   private final ObjectProperty<Number> startWeight = new SimpleObjectProperty();
   private final ObjectProperty<Number> endWeight = new SimpleObjectProperty();
   private final ObjectProperty<Number> roastValue1 = new SimpleObjectProperty();
   private final ObjectProperty<Number> roastValue2 = new SimpleObjectProperty();
   @FXML
   private Label availableChars;
   private final StringProperty notes = new SimpleStringProperty();
   private final NumberFormat percentFormat = NumberFormat.getPercentInstance();

   public FinishPanel() {
      this.percentFormat.setMinimumFractionDigits(0);
      this.percentFormat.setMaximumFractionDigits(3);
      this.percentFormat.setRoundingMode(RoundingMode.HALF_EVEN);
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.startTimeEditor.valueProperty().bindBidirectional(this.startTime);
      this.endTimeEditor.valueProperty().bindBidirectional(this.endTime);
      this.startWeightEditor.valueProperty().bindBidirectional(this.startWeight);
      this.endWeightEditor.valueProperty().bindBidirectional(this.endWeight);
      this.weightLossLabel.textProperty().bind(new StringBinding() {
         {
            super.bind(new Observable[]{FinishPanel.this.startWeight, FinishPanel.this.endWeight});
         }

         protected String computeValue() {
            if (FinishPanel.this.startWeight.get() != null && FinishPanel.this.endWeight.get() != null) {
               double weightLossPercentage = 0.0;
               double start = ((Number)FinishPanel.this.startWeight.get()).doubleValue();
               if (start > 0.0) {
                  double end = ((Number)FinishPanel.this.endWeight.get()).doubleValue();
                  weightLossPercentage = (end - start) / start;
               }

               double amount = -weightLossPercentage;
               return FinishPanel.this.percentFormat.format(amount == 0.0 ? 0.0 : amount);
            } else {
               return "-";
            }
         }
      });
      this.roastValue1Editor.valueProperty().bindBidirectional(this.roastValue1);
      this.roastValue2Editor.valueProperty().bindBidirectional(this.roastValue2);
      this.notesArea.textProperty().bindBidirectional(this.notes);
      JavaFxUtil.createLengthOverrunProperty(250, this.notesArea, this.availableChars);
      this.notesArea.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(250));
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public void installValidators(Double maxStartWeight) {
      Validators.setValidator(this.startTimeEditor);
      Validators.setValidator(this.endTimeEditor);
      Validators.bindRanges(this.startTimeEditor, this.endTimeEditor, 1000L, true);
      Validators.setValidator(this.startWeightEditor, maxStartWeight != null ? maxStartWeight : 9999999.999);
      Validators.setValidator(this.endWeightEditor, maxStartWeight != null ? maxStartWeight : 9999999.999);
      Validators.bindRanges(this.endWeightEditor, this.startWeightEditor, 0, true);
      Validators.setValidator(this.roastValue1Editor);
      Validators.setValidator(this.roastValue2Editor);
   }

   public boolean hasValidRequiredValues() {
      return this.startTime.get() != null
         && this.endTime.get() != null
         && this.startWeight.get() != null
         && this.endWeight.get() != null
         && Validators.isValidValue(this.startTimeEditor)
         && Validators.isValidValue(this.endTimeEditor)
         && Validators.isValidValue(this.startWeightEditor)
         && Validators.isValidValue(this.endWeightEditor);
   }

   public ObjectProperty<Long> startTimeProperty() {
      return this.startTime;
   }

   public Long getStartTime() {
      return (Long)this.startTime.get();
   }

   public void setStartTime(Long startTime) {
      this.startTime.set(startTime);
   }

   public ObjectProperty<Long> endTimeProperty() {
      return this.endTime;
   }

   public Long getEndTime() {
      return (Long)this.endTime.get();
   }

   public void setEndTime(Long endTime) {
      this.endTime.set(endTime);
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

   public ObjectProperty<Number> startWeightProperty() {
      return this.startWeight;
   }

   public Number getStartWeight() {
      return (Number)this.startWeight.get();
   }

   public void setStartWeight(Number startWeight) {
      this.startWeight.set(startWeight);
   }

   public ObjectProperty<Number> endWeightProperty() {
      return this.endWeight;
   }

   public Number getEndWeight() {
      return (Number)this.endWeight.get();
   }

   public void setEndWeight(Number endWeight) {
      this.endWeight.set(endWeight);
   }

   public ObjectProperty<Number> roastValue1Property() {
      return this.roastValue1;
   }

   public Number getRoastValue1() {
      return (Number)this.roastValue1.get();
   }

   public void setRoastValue1(Number roastValue1) {
      this.roastValue1.set(roastValue1);
   }

   public Unit getRoastValue1Unit() {
      return this.roastValue1Editor.getUnit();
   }

   public void setRoastValue1Unit(Unit roastValue1Unit) {
      this.roastValue1Editor.setUnit(roastValue1Unit);
   }

   public ObjectProperty<Number> roastValue2Property() {
      return this.roastValue2;
   }

   public Number getRoastValue2() {
      return (Number)this.roastValue2.get();
   }

   public void setRoastValue2(Number roastValue2) {
      this.roastValue2.set(roastValue2);
   }

   public Unit getRoastValue2Unit() {
      return this.roastValue2Editor.getUnit();
   }

   public void setRoastValue2Unit(Unit roastValue2Unit) {
      this.roastValue2Editor.setUnit(roastValue2Unit);
   }

   public StringProperty notesProperty() {
      return this.notes;
   }

   public String getNotes() {
      return (String)this.notes.get();
   }

   public void setNotes(String notes) {
      this.notes.set(notes);
   }
}
