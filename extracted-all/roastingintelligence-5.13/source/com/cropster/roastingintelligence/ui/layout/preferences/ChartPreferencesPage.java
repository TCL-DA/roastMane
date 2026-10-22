package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.TimeEditor;
import com.cropster.roastingintelligence.ui.layout.CommentsCustomizationTable;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartPreferencesPage extends GridPane implements Initializable {
   private final Logger logger = LoggerFactory.getLogger(ChartPreferencesPage.class);
   private final AtomicBoolean updatingGasCurveAsCommentsEditor = new AtomicBoolean(false);
   @FXML
   private TimeEditor chartXAxisField;
   @FXML
   private MeasureEditor chartYAxisMinField;
   @FXML
   private MeasureEditor chartYAxisMaxField;
   @FXML
   private MeasureEditor chartRorMinField;
   @FXML
   private MeasureEditor chartRorMaxField;
   @FXML
   private Button chartAxesResetButton;
   @FXML
   private Label chartAxesChangedLabel;
   @FXML
   private Label markEventsPrefixLabel;
   @FXML
   private ToggleGroup markEventsToggleGroup;
   @FXML
   private RadioButton markEventsByTempRadio;
   @FXML
   private RadioButton markEventsByTimeRadio;
   @FXML
   private CheckBox eventAutoMarkingCheckbox;
   @FXML
   private CheckBox referenceCommentHistoryHiddenCheckbox;
   @FXML
   private CheckBox gasCurveAsCommentsCheckbox;
   @FXML
   private MeasureEditor gasCurveAsCommentsEditor;
   @FXML
   private Label gasCurveAsCommentsSuffixLabel;
   @FXML
   private CommentsCustomizationTable commentsCustomizationTable;
   @FXML
   private Label xAxisLabel;
   @FXML
   private Label yAxisLabel;
   @FXML
   private FormComponent eventsForm;
   @FXML
   private FormComponent commentsForm;
   private Runnable chartAxesResetter;

   public ChartPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      String translation = org.cropster.roastingintelligence.util.Messages.m_("Show gas curve as comments when there are no more than {0}");
      String[] translationParts = translation.split("\\{0\\}");
      this.gasCurveAsCommentsCheckbox.setText(translationParts[0]);
      if (translationParts.length > 1) {
         this.gasCurveAsCommentsSuffixLabel.setText(translationParts[1]);
      }

      Validators.setValidator(this.gasCurveAsCommentsEditor, -1, Integer.valueOf(1000));
      this.gasCurveAsCommentsEditor.valueProperty().addListener((s, o, n) -> {
         if (n != null) {
            this.setGasCurveAsCommentsThreshold(n.intValue());
         }
      });
      String key = "Mark or hide by ☑ Bean Temperature ☑ Time";
      String message = org.cropster.roastingintelligence.util.Messages.m_(key);
      String[] markText = message.split("☑");
      if (markText.length != 3) {
         this.logger.error("Inaccurate translation: {}", message);
         markText = key.split(" ☑ ");
      }

      this.markEventsPrefixLabel.setText(markText[0]);
      this.markEventsByTempRadio.setText(markText[1]);
      this.markEventsByTimeRadio.setText(markText[2]);
      Validators.setValidator(this.chartXAxisField, 1000L, Validators.ABSOLUTE_MAX_TIME);
      this.chartYAxisMinField.enableUnitValueCoupling(false);
      this.chartYAxisMaxField.enableUnitValueCoupling(false);
      this.chartRorMinField.enableUnitValueCoupling(true);
      this.chartRorMaxField.enableUnitValueCoupling(true);
      Validators.setValidator(this.chartYAxisMinField, 10000.0);
      Validators.setValidator(this.chartYAxisMaxField, 10000.0);
      Validators.bindRanges(this.chartYAxisMinField, this.chartYAxisMaxField, 1);
      Validators.setValidator(this.chartRorMinField, -1000.0, 1000.0);
      Validators.setValidator(this.chartRorMaxField, -1000.0, 1000.0);
      Validators.bindRanges(this.chartRorMinField, this.chartRorMaxField, 1);
      this.chartAxesChangedLabel.managedProperty().bind(this.chartAxesChangedLabel.visibleProperty());
      this.resetChangeLabel();
      PreferencesPanel.addTitleArrow(this.xAxisLabel);
      PreferencesPanel.addTitleArrow(this.yAxisLabel);
      PreferencesPanel.addTitleArrow(this.eventsForm);
      PreferencesPanel.addTitleArrow(this.commentsForm);
   }

   @FXML
   public void resetChartAxes() {
      if (this.chartAxesResetter != null) {
         this.chartAxesResetter.run();
      }
   }

   public void setChartAxesResetter(Runnable chartAxesResetter) {
      this.chartAxesResetter = chartAxesResetter;
   }

   public ObjectProperty<Long> chartXAxisProperty() {
      return this.chartXAxisField.valueProperty();
   }

   public long getChartXAxis() {
      return this.chartXAxisField.getValue() / 1000L;
   }

   public void setChartXAxis(long chartXAxis) {
      this.chartXAxisField.setValue(chartXAxis * 1000L);
   }

   public ObjectProperty<Number> chartYAxisMinProperty() {
      return this.chartYAxisMinField.valueProperty();
   }

   public int getChartYAxisMin() {
      return this.chartYAxisMinField.getValue().intValue();
   }

   public void setChartYAxisMin(int chartYAxisMin) {
      this.chartYAxisMinField.setValue(chartYAxisMin);
   }

   public ObjectProperty<Unit> chartYAxisMinUnitProperty() {
      return this.chartYAxisMinField.unitProperty();
   }

   public ObjectProperty<Number> chartYAxisMaxProperty() {
      return this.chartYAxisMaxField.valueProperty();
   }

   public int getChartYAxisMax() {
      return this.chartYAxisMaxField.getValue().intValue();
   }

   public void setChartYAxisMax(int chartYAxisMax) {
      this.chartYAxisMaxField.setValue(chartYAxisMax);
   }

   public ObjectProperty<Unit> chartYAxisMaxUnitProperty() {
      return this.chartYAxisMaxField.unitProperty();
   }

   public ObjectProperty<Number> chartRorMinProperty() {
      return this.chartRorMinField.valueProperty();
   }

   public int getChartRorMin() {
      return this.chartRorMinField.getValue().intValue();
   }

   public void setChartRorMin(int chartRorMin) {
      this.chartRorMinField.setValue(chartRorMin);
   }

   public ObjectProperty<Unit> chartRorMinUnitProperty() {
      return this.chartRorMinField.unitProperty();
   }

   public ObjectProperty<Number> chartRorMaxProperty() {
      return this.chartRorMaxField.valueProperty();
   }

   public int getChartRorMax() {
      return this.chartRorMaxField.getValue().intValue();
   }

   public void setChartRorMax(int chartRorMax) {
      this.chartRorMaxField.setValue(chartRorMax);
   }

   public ObjectProperty<Unit> chartRorMaxUnitProperty() {
      return this.chartRorMaxField.unitProperty();
   }

   public boolean isCommentingByTempEnabled() {
      return "TEMP".equalsIgnoreCase(this.markEventsToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setCommentingByTempEnabled(boolean commentingByTempEnabled) {
      JavaFxUtil.selectToggle(this.markEventsToggleGroup, commentingByTempEnabled ? "TEMP" : "TIME");
   }

   public boolean isEventAutoMarkingEnabled() {
      return this.eventAutoMarkingCheckbox.isSelected();
   }

   public void setEventAutoMarkingEnabled(boolean eventAutoMarkingEnabled) {
      this.eventAutoMarkingCheckbox.setSelected(eventAutoMarkingEnabled);
   }

   public boolean isReferenceCommentHistoryHidden() {
      return this.referenceCommentHistoryHiddenCheckbox.isSelected();
   }

   public void setReferenceCommentHistoryHidden(boolean hideReferenePastComments) {
      this.referenceCommentHistoryHiddenCheckbox.setSelected(hideReferenePastComments);
   }

   public int getGasCurveAsCommentsThreshold() {
      return this.gasCurveAsCommentsCheckbox.isSelected() ? this.gasCurveAsCommentsEditor.getValue().intValue() : -1;
   }

   public void setGasCurveAsCommentsThreshold(int gasCurveAsCommentsThreshold) {
      if (this.updatingGasCurveAsCommentsEditor.compareAndSet(false, true)) {
         try {
            if (gasCurveAsCommentsThreshold > 0) {
               this.gasCurveAsCommentsCheckbox.setSelected(true);
               this.gasCurveAsCommentsEditor.setValue(Math.min(1000, gasCurveAsCommentsThreshold));
            } else {
               this.gasCurveAsCommentsCheckbox.setSelected(false);
               this.gasCurveAsCommentsEditor.setValue(25);
            }
         } finally {
            this.updatingGasCurveAsCommentsEditor.set(false);
         }
      }
   }

   public String[] getEnabledEventsSidebar() {
      return this.commentsCustomizationTable.getEnabledEventsSidebar();
   }

   public void setEnabledEventsSidebar(String... events) {
      this.commentsCustomizationTable.setEnabledEventsSidebar(events);
   }

   public String[] getEnabledEventsChart() {
      return this.commentsCustomizationTable.getEnabledEventsChart();
   }

   public void setEnabledEventsChart(String... events) {
      this.commentsCustomizationTable.setEnabledEventsChart(events);
   }

   public String[] getEnabledEventsPopup() {
      return this.commentsCustomizationTable.getEnabledEventsPopup();
   }

   public void setEnabledEventsPopup(String... events) {
      this.commentsCustomizationTable.setEnabledEventsPopup(events);
   }

   public Set<String> getPreFills() {
      Set<String> preFills = new LinkedHashSet<>();
      if (this.commentsCustomizationTable.isGasPreFillEnabled()) {
         preFills.add("gas");
      }

      if (this.commentsCustomizationTable.isAirflowPreFillEnabled()) {
         preFills.add("airflow");
      }

      return preFills;
   }

   public void setPreFills(Set<String> preFills) {
      this.commentsCustomizationTable.setGasPreFillEnabled(preFills.contains("gas"));
      this.commentsCustomizationTable.setAirflowPreFillEnabled(preFills.contains("airflow"));
   }

   public void setChartAxesChangedLabelVisible(boolean visible) {
      this.chartAxesChangedLabel.setVisible(visible);
   }

   public void resetChangeLabel() {
      this.chartAxesChangedLabel.setVisible(false);
   }

   public BooleanProperty chartAxesResetButtonDisableProperty() {
      return this.chartAxesResetButton.disableProperty();
   }
}
