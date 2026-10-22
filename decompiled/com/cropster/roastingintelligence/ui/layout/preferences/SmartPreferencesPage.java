package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.audio.Sound;
import com.cropster.roastingintelligence.ui.audio.SoundManager;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.components.WebLink;
import com.cropster.roastingintelligence.ui.components.skin.ValueTrackSliderSkin;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;

public class SmartPreferencesPage extends GridPane implements Initializable {
   public static final String TEST_SOUND_IDENTIFIER = "test-sound-identifier";
   @FXML
   private CheckBox curvePredictionCheckbox;
   @FXML
   private CheckBox firstCrackPredictionCheckbox;
   @FXML
   private CheckBox flickPredictionCheckbox;
   @FXML
   private CheckBox replayEnabledCheckbox;
   @FXML
   private CheckBox smartAlertsCheckbox;
   @FXML
   private CheckBox enableNotificationCheckbox;
   @FXML
   private MeasureEditor repetitionEditor;
   @FXML
   private Slider volumeSlider;
   @FXML
   private MeasureEditor volumeEditor;
   @FXML
   private Button volumeTestButton;
   @FXML
   private CheckBox resetGasThresholdCheckbox;
   @FXML
   private MeasureEditor resetGasThresholdField;
   @FXML
   private CheckBox autoDetectionCheckbox;
   @FXML
   private MeasureEditor startThresholdField;
   @FXML
   private MeasureEditor endThresholdField;
   @FXML
   private Button resetThresholdButton;
   @FXML
   private FormComponent alertsSoundRepetitionForm;
   @FXML
   private FormComponent alertsVolumeForm;
   @FXML
   private ToggleGroup autoStartToggleGroup;
   @FXML
   private GridPane smartDetectionAutoStartPanel;
   @FXML
   private VBox smartDetectionNoAutoStartPanel;
   @FXML
   private CheckBox startEndCheckbox;
   @FXML
   private RadioButton autoDetectionRadiobutton;
   @FXML
   private RadioButton autoStartRadiobutton;
   @FXML
   private Text autoStartDescription;
   @FXML
   private WebLink autoStartLink;
   @FXML
   private VBox thresholdFieldNoAutoStartPane;
   @FXML
   private VBox thresholdFieldAutoStartPane;
   @FXML
   private FormComponent smartGoalsAndReferencesForm;
   @FXML
   private CheckBox smartGoalsAndReferencesEnabledCheckBox;
   @FXML
   private CheckBox hideLiveGoalCurrentValueCheckBox;
   @FXML
   private CheckBox hideGoalStatisticsCheckBox;
   @FXML
   private MessageBox smartGoalsPromo;
   private final BooleanProperty autoStartPossible = new SimpleBooleanProperty(false);
   private final BooleanProperty smartGoalsEnabled = new SimpleBooleanProperty(false);
   private final ObjectProperty<Unit> temperatureUnit = new SimpleObjectProperty();
   private Service<Void> testSoundService = new Service<Void>() {
      private int r;
      private double v;

      protected Task<Void> createTask() {
         final SoundManager sm = SoundManager.get();
         SmartPreferencesPage.this.volumeTestButton.setText(org.cropster.roastingintelligence.util.Messages.m_("Stop"));
         SmartPreferencesPage.this.repetitionEditor.setDisable(true);
         SmartPreferencesPage.this.volumeSlider.setDisable(true);
         SmartPreferencesPage.this.volumeEditor.setDisable(true);
         this.r = sm.getRepetitions();
         this.v = sm.getVolume();
         sm.setRepetitions(SmartPreferencesPage.this.getAlarmSoundRepetitions());
         sm.setVolume(SmartPreferencesPage.this.getAlarmSoundVolume());
         sm.play(Sound.INFORMATIVE1, "test-sound-identifier");
         return new Task<Void>() {
            protected Void call() throws Exception {
               while (sm.isPlaying()) {
                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var2) {
                     break;
                  }
               }

               return null;
            }
         };
      }

      protected void succeeded() {
         this.restore();
      }

      protected void cancelled() {
         this.restore();
      }

      protected void failed() {
         this.restore();
      }

      private void restore() {
         SoundManager sm = SoundManager.get();
         sm.stop();
         sm.setRepetitions(this.r);
         sm.setVolume(this.v);
         SmartPreferencesPage.this.volumeTestButton.setText(org.cropster.roastingintelligence.util.Messages.m_("Test"));
         SmartPreferencesPage.this.repetitionEditor.setDisable(false);
         SmartPreferencesPage.this.volumeSlider.setDisable(false);
         SmartPreferencesPage.this.volumeEditor.setDisable(false);
      }
   };

   public SmartPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.firstCrackPredictionCheckbox.selectedProperty().addListener((s, o, n) -> {
         if (!n) {
            this.flickPredictionCheckbox.setSelected(false);
         }
      });
      this.resetGasThresholdField.setTextFormatter(new TextFormatter(new IntegerStringConverter()));
      this.startThresholdField.enableUnitValueCoupling(true);
      this.endThresholdField.enableUnitValueCoupling(true);
      Validators.setValidator(this.resetGasThresholdField, -1, Integer.valueOf(600));
      Validators.setValidator(this.startThresholdField, 1000.0);
      Validators.setValidator(this.endThresholdField, 1000.0);
      this.smartDetectionAutoStartPanel.managedProperty().bind(this.autoStartPossible);
      this.smartDetectionAutoStartPanel.visibleProperty().bind(this.autoStartPossible);
      this.thresholdFieldAutoStartPane.managedProperty().bind(this.autoStartPossible);
      this.thresholdFieldAutoStartPane.visibleProperty().bind(this.autoStartPossible);
      this.smartDetectionNoAutoStartPanel.managedProperty().bind(this.autoStartPossible.not());
      this.smartDetectionNoAutoStartPanel.visibleProperty().bind(this.autoStartPossible.not());
      this.thresholdFieldNoAutoStartPane.managedProperty().bind(this.autoStartPossible.not());
      this.thresholdFieldNoAutoStartPane.visibleProperty().bind(this.autoStartPossible.not());
      Validators.setValidator(this.repetitionEditor, 1, Integer.valueOf(100));
      this.volumeSlider.setMin(0.0);
      this.volumeSlider.setMax(100.0);
      this.volumeSlider.setValue(50.0);
      this.volumeSlider.setSkin(new ValueTrackSliderSkin(this.volumeSlider));
      this.volumeEditor.getInput().setOnAction(Event::consume);
      this.volumeEditor.setFractionEnabled(false);
      this.volumeEditor.setUnit(Unit.PERCENT);
      this.volumeEditor.valueProperty().bindBidirectional(this.volumeSlider.valueProperty());
      Validators.setValidator(this.volumeEditor, this.volumeSlider.getMin(), this.volumeSlider.getMax());
      this.smartGoalsPromo.visibleProperty().bind(this.smartGoalsEnabled.not().and(this.smartGoalsAndReferencesEnabledCheckBox.selectedProperty()));
      PreferencesPanel.addTitleArrow(this.alertsSoundRepetitionForm);
      PreferencesPanel.addTitleArrow(this.alertsVolumeForm);
   }

   public void showNoAutoStartPane() {
      this.thresholdFieldNoAutoStartPane.getChildren().addAll(this.thresholdFieldAutoStartPane.getChildren());
      this.thresholdFieldAutoStartPane.getChildren().clear();
   }

   public void showAutoStartPane(Class<? extends MeasurementSourceFactory> clazz) {
      this.showAutoStartPane();
      this.autoStartRadiobutton.setText(org.cropster.roastingintelligence.util.Messages.m_("AutoStartCaption", clazz));
      String caption = org.cropster.roastingintelligence.util.Messages.m_("AutoStartDescription", clazz);
      this.autoStartLink.setVisible(caption.equals(org.cropster.roastingintelligence.util.Messages.m_("AutoStartDescription")));
      this.autoStartDescription.setText(caption);
   }

   public void showAutoStartPane() {
      this.thresholdFieldAutoStartPane.getChildren().addAll(this.thresholdFieldNoAutoStartPane.getChildren());
      this.thresholdFieldNoAutoStartPane.getChildren().clear();
   }

   @FXML
   public void resetThresholds() {
      this.setStartThreshold(Integer.parseInt(this.getStartThresholdDefaultText()));
      this.setEndThreshold(Integer.parseInt(this.getEndThresholdDefaultText()));
   }

   @FXML
   public void enableSmartAlerts() {
      if (!this.smartAlertsCheckbox.isSelected() && this.testSoundService.isRunning()) {
         this.testSoundService.cancel();
      }
   }

   @FXML
   public synchronized void testVolume() {
      if (this.testSoundService.isRunning()) {
         this.testSoundService.cancel();
      } else {
         this.testSoundService.reset();
         this.testSoundService.start();
      }
   }

   public int getAlarmSoundRepetitions() {
      return Validators.getSafeValue(this.repetitionEditor).intValue();
   }

   public void setAlarmSoundRepetitions(int repetitions) {
      this.repetitionEditor.setValue(Math.max(1, Math.min(100, repetitions)));
   }

   public double getAlarmSoundVolume() {
      return this.volumeSlider.getValue() / 100.0;
   }

   public void setAlarmSoundVolume(double volume) {
      this.volumeSlider.setValue((double)((int)(Math.min(1.0, Math.max(0.0, volume)) * 100.0)));
   }

   public boolean isCurvePredictionEnabled() {
      return this.curvePredictionCheckbox.isSelected();
   }

   public void setCurvePredictionEnabled(boolean curvePredictionEnabled) {
      this.curvePredictionCheckbox.setSelected(curvePredictionEnabled);
   }

   public ReadOnlyBooleanProperty firstCrackPredictionCheckboxSelectedProperty() {
      return this.firstCrackPredictionCheckbox.selectedProperty();
   }

   public boolean isFirstCrackPredictionEnabled() {
      return this.firstCrackPredictionCheckbox.isSelected();
   }

   public void setFirstCrackEnabled(boolean firstCrackPredictionEnabled) {
      this.firstCrackPredictionCheckbox.setSelected(firstCrackPredictionEnabled);
   }

   public boolean isFlickPredictionEnabled() {
      return this.flickPredictionCheckbox.isSelected();
   }

   public void setFlickPredictionEnabled(boolean flickPredictionEnabled) {
      this.flickPredictionCheckbox.setSelected(flickPredictionEnabled);
   }

   public boolean isReplayEnabled() {
      return this.replayEnabledCheckbox.isSelected();
   }

   public void setReplayEnabled(boolean replayEnabled) {
      this.replayEnabledCheckbox.setSelected(replayEnabled);
   }

   public boolean isSoundAlarmOnEventsEnabled() {
      return this.smartAlertsCheckbox.isSelected();
   }

   public void setSoundAlarmOnEventsEnabled(boolean soundAlarmOnEventsEnabled) {
      this.smartAlertsCheckbox.setSelected(soundAlarmOnEventsEnabled);
   }

   public boolean isAlarmsToastNotificationEnabled() {
      return this.enableNotificationCheckbox.isSelected();
   }

   public void setAlarmsToastNotificationEnabled(boolean enabled) {
      this.enableNotificationCheckbox.setSelected(enabled);
   }

   public int getResetGasThreshold() {
      return this.resetGasThresholdCheckbox.isSelected() ? (Integer)this.resetGasThresholdField.getInput().getTextFormatter().getValue() : -1;
   }

   public void setResetGasThreshold(int resetGasThreshold) {
      if (resetGasThreshold >= 0) {
         this.resetGasThresholdCheckbox.setSelected(true);
         this.resetGasThresholdField.getInput().setText(String.valueOf(resetGasThreshold));
      } else {
         this.resetGasThresholdCheckbox.setSelected(false);
         this.resetGasThresholdField.getInput().setText(String.valueOf(-1));
      }
   }

   public boolean isAutoStartPossible() {
      return this.autoStartPossible.get();
   }

   public BooleanProperty autoStartPossibleProperty() {
      return this.autoStartPossible;
   }

   public boolean isAutoDetectionEnabled() {
      if (!this.isAutoStartPossible()) {
         return this.autoDetectionCheckbox.isSelected();
      } else if (!this.startEndCheckbox.isSelected()) {
         return false;
      } else {
         return this.autoDetectionRadiobutton.isSelected() ? true : this.autoDetectionCheckbox.isSelected();
      }
   }

   public void setAutoDetectionEnabled(boolean autoDetectionEnabled) {
      this.startEndCheckbox.setSelected(autoDetectionEnabled);
      this.autoDetectionRadiobutton.setSelected(autoDetectionEnabled);
      this.autoDetectionCheckbox.setSelected(autoDetectionEnabled);
   }

   public boolean isAutoStartEnabled() {
      return this.startEndCheckbox.isSelected() && this.autoStartRadiobutton.isSelected();
   }

   public void setAutoStartEnabled(boolean autoStartEnabled) {
      this.startEndCheckbox.setSelected(autoStartEnabled || this.isAutoDetectionEnabled());
      this.autoStartRadiobutton.setSelected(autoStartEnabled);
   }

   public int getStartThreshold() {
      return -this.startThresholdField.getValue().intValue();
   }

   public void setStartThreshold(int autoDetectionStartThreshold) {
      this.startThresholdField.setValue(Math.abs(autoDetectionStartThreshold));
   }

   public int getEndThreshold() {
      return -this.endThresholdField.getValue().intValue();
   }

   public void setEndThreshold(int autoDetectionEndThreshold) {
      this.endThresholdField.setValue(Math.abs(autoDetectionEndThreshold));
   }

   public boolean compareStartThresholds() {
      String stdt = this.getStartThresholdDefaultText();
      return stdt != null && stdt.equals(this.startThresholdField.getInput().getText());
   }

   public boolean compareEndThresholds() {
      String etdt = this.getEndThresholdDefaultText();
      return etdt != null && etdt.equals(this.endThresholdField.getInput().getText());
   }

   public ReadOnlyBooleanProperty autoDetectionRadiobuttonSelectedProperty() {
      return this.autoDetectionRadiobutton.selectedProperty();
   }

   public BooleanProperty autoDetectionRadiobuttonDisableProperty() {
      return this.autoDetectionRadiobutton.disableProperty();
   }

   public ReadOnlyBooleanProperty autoDetectionCheckboxSelectedProperty() {
      return this.autoDetectionCheckbox.selectedProperty();
   }

   public BooleanProperty autoStartRadiobuttonDisableProperty() {
      return this.autoStartRadiobutton.disableProperty();
   }

   public BooleanProperty flickPredictionCheckboxDisabledProperty() {
      return this.flickPredictionCheckbox.disableProperty();
   }

   public BooleanProperty resetThresholdButtonDisabledProperty() {
      return this.resetThresholdButton.disableProperty();
   }

   public ReadOnlyBooleanProperty startEndCheckboxSelectedProperty() {
      return this.startEndCheckbox.selectedProperty();
   }

   public BooleanProperty startThresholdFieldDisabledProperty() {
      return this.startThresholdField.disableProperty();
   }

   public ObjectProperty<Unit> startThresholdFieldUnitProperty() {
      return this.startThresholdField.unitProperty();
   }

   public ObjectProperty<Number> startThresholdFieldValueProperty() {
      return this.startThresholdField.valueProperty();
   }

   public BooleanProperty endThresholdFieldDisabledProperty() {
      return this.endThresholdField.disableProperty();
   }

   public ObjectProperty<Unit> endThresholdFieldUnitProperty() {
      return this.endThresholdField.unitProperty();
   }

   public ObjectProperty<Number> endThresholdFieldValueProperty() {
      return this.startThresholdField.valueProperty();
   }

   private String getStartThresholdDefaultText() {
      BigDecimal startThreshold = Defaults.TEMPERATURE_DROP_START;
      if (Unit.FAHRENHEIT.equals(this.getTemperatureUnit())) {
         startThreshold = Unit.FAHRENHEIT.fromRelative(Defaults.TEMPERATURE_DROP_START, Unit.CELSIUS);
      }

      return startThreshold.setScale(0, RoundingMode.HALF_EVEN).abs().toString();
   }

   private String getEndThresholdDefaultText() {
      BigDecimal endThreshold = Defaults.TEMPERATURE_DROP_END;
      if (Unit.FAHRENHEIT.equals(this.getTemperatureUnit())) {
         endThreshold = Unit.FAHRENHEIT.fromRelative(Defaults.TEMPERATURE_DROP_END, Unit.CELSIUS);
      }

      return endThreshold.setScale(0, RoundingMode.HALF_EVEN).abs().toString();
   }

   public Unit getTemperatureUnit() {
      return (Unit)this.temperatureUnit.get();
   }

   public ObjectProperty<Unit> temperatureUnitProperty() {
      return this.temperatureUnit;
   }

   public FormComponent getSmartGoalsAndReferencesForm() {
      return this.smartGoalsAndReferencesForm;
   }

   public boolean isSmartGoalsAndReferencesEnabled() {
      return this.smartGoalsAndReferencesEnabledCheckBox.isSelected();
   }

   public void setSmartGoalsAndReferencesEnabled(boolean enabled) {
      this.smartGoalsAndReferencesEnabledCheckBox.setSelected(enabled);
   }

   public boolean isHideLiveGoalCurrentValueEnabled() {
      return this.hideLiveGoalCurrentValueCheckBox.isSelected();
   }

   public void setHideLiveGoalCurrentValueEnabled(boolean enabled) {
      this.hideLiveGoalCurrentValueCheckBox.setSelected(enabled);
   }

   public boolean isHideGoalStatisticsEnabled() {
      return this.hideGoalStatisticsCheckBox.isSelected();
   }

   public void setHideGoalStatisticsEnabled(boolean enabled) {
      this.hideGoalStatisticsCheckBox.setSelected(enabled);
   }

   public void setSmartGoalsEnabled(boolean enabled) {
      this.smartGoalsEnabled.set(enabled);
   }
}
