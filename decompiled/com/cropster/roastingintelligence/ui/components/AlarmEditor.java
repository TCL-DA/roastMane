package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmTargetDirection;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.audio.Sound;
import com.cropster.roastingintelligence.ui.audio.SoundManager;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.TranslatedListCell;
import com.cropster.roastingintelligence.ui.util.Validators;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.cropster.roastingintelligence.util.Measures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmEditor extends VBox implements Initializable {
   private static final String BORDER_STYLE = "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-30";
   private static final String UNKNOWN_ALARM_TYPE = "Unknown alarm type: ";
   @FXML
   private Label titleLabel;
   @FXML
   private ToggleGroup eventDirectionToggleGroup;
   @FXML
   private ToggleGroup measureDirectionToggleGroup;
   @FXML
   private ComboBox<String> triggerSelector;
   @FXML
   private ComboBox<RoastingPhase> phaseSelector;
   @FXML
   private VBox editorsBox;
   @FXML
   private FormComponent measureForm;
   @FXML
   private MeasureEditor targetValue;
   @FXML
   private FormComponent eventForm;
   @FXML
   private TimeEditor offsetValue;
   @FXML
   private Label offsetDirectionLabel;
   @FXML
   private FormComponent timeForm;
   @FXML
   private TimeEditor timeValue;
   @FXML
   private FormComponent phaseForm;
   @FXML
   private RadioButton visualFeedbackEnabler;
   @FXML
   private ColorPicker colorPicker;
   @FXML
   private ComboBox<Sound> soundSelector;
   @FXML
   private Button soundTestButton;
   @FXML
   private Button cancelButton;
   @FXML
   private Button saveButton;
   @FXML
   private FormComponent measureFormButtonsBar;
   @FXML
   private RadioToggleButton toggleButtonMeasureAbove;
   @FXML
   private RadioToggleButton toggleButtonMeasureBelow;
   @FXML
   private RadioToggleButton toggleButtonMeasureBoth;
   @FXML
   private FormComponent eventFormButtonsBar;
   @FXML
   private RadioToggleButton toggleButtonEventBefore;
   @FXML
   private RadioToggleButton toggleButtonEventAfter;
   @FXML
   private RadioToggleButton toggleButtonEventBoth;
   private final Logger logger = LoggerFactory.getLogger(AlarmEditor.class);
   private final BooleanProperty dirty = new SimpleBooleanProperty();
   private final DirtyListener<Color> colorDirty = new DirtyListener<>(null);
   private final DirtyListener<Toggle> eventDirectionDirty = new DirtyListener<>(null);
   private final DirtyListener<Toggle> measureDirectionDirty = new DirtyListener<>(null);
   private final DirtyListener<String> offsetValueDirty = new DirtyListener<>(null);
   private final DirtyListener<RoastingPhase> phaseDirty = new DirtyListener<>(null);
   private final DirtyListener<Sound> soundDirty = new DirtyListener<>(null);
   private final DirtyListener<String> targetValueDirty = new DirtyListener<>(null);
   private final DirtyListener<String> timeValueDirty = new DirtyListener<>(null);
   private final DirtyListener<String> triggerDirty = new DirtyListener<>(null);
   private final DirtyListener<Boolean> visualFeedbackDirty = new DirtyListener<>(null);
   private final ListView<Alarm> alarmView;
   private Alarm alarm;
   private Service<Void> testSoundService = new Service<Void>() {
      protected Task<Void> createTask() {
         AlarmEditor.this.soundTestButton.setGraphic(Icon.STOP.load());
         SoundManager.get().play((Sound)AlarmEditor.this.soundSelector.getValue());
         return new Task<Void>() {
            protected Void call() throws Exception {
               while (SoundManager.get().isPlaying()) {
                  Thread.sleep(100L);
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
         SoundManager.get().stop();
         AlarmEditor.this.soundTestButton.setGraphic(Icon.START.load());
      }
   };

   public AlarmEditor(ListView<Alarm> alarmsView) {
      JavaFxUtil.loadComponent(this);
      this.alarmView = alarmsView;
      this.saveButton
         .disableProperty()
         .bind(
            this.dirty
               .not()
               .and(
                  Bindings.createBooleanBinding(
                     () -> !this.alarmView.getProperties().containsKey("created-alarm"), new Observable[]{this.alarmView.getProperties()}
                  )
               )
         );
   }

   @FXML
   public void updateEditors(ActionEvent e) {
      String selectedItem = (String)this.triggerSelector.getValue();
      if (selectedItem != null) {
         this.updateEditorsVisibility(Alarm.typeFromTrigger(selectedItem), selectedItem);
      }
   }

   @FXML
   public synchronized void testSound(ActionEvent e) {
      if (this.testSoundService.isRunning()) {
         this.testSoundService.cancel();
      } else {
         this.testSoundService.reset();
         this.testSoundService.start();
      }
   }

   @FXML
   public void stopAudioFeedback(ActionEvent e) {
      if (this.testSoundService.isRunning()) {
         this.testSoundService.cancel();
      }
   }

   public Button getCancelButton() {
      return this.cancelButton;
   }

   public Button getSaveButton() {
      return this.saveButton;
   }

   public void init(Alarm alarm) {
      this.alarm = alarm;
      this.titleLabel.setDisable(!Alarm.isSupported(alarm));
      this.titleLabel.setText(alarm.toString());
      this.titleLabel.setTooltip(new Tooltip(alarm.toString()));
      this.triggerSelector.setValue(alarm.getTrigger());
      this.phaseSelector.setValue(alarm.getTriggeringPhase());
      this.targetValue.setMeasure(alarm.getTargetMeasure());
      this.offsetValue.setValue(alarm.getTimeOffset());
      this.timeValue.setValue(alarm.getTimeReference());
      this.updateEditorsVisibility(alarm.getType(), alarm.getTrigger());
      this.soundSelector.setValue(alarm.getAudibleFeedback());
      this.visualFeedbackEnabler.setSelected(alarm.isVisualFeedback());
      this.colorPicker.setValue(JavaFxUtil.colorFromAwt(alarm.getVisualFeedbackColor()));
      switch (alarm.getTargetDirection()) {
         case HIGHER:
            this.toggleButtonMeasureAbove.fire();
            this.toggleButtonEventAfter.fire();
            break;
         case LOWER:
            this.toggleButtonMeasureBelow.fire();
            this.toggleButtonEventBefore.fire();
            break;
         case ANY:
         default:
            this.toggleButtonMeasureBoth.fire();
            this.toggleButtonEventBoth.fire();
      }

      this.updateOffsetDirectionLabel();
      this.colorDirty.setOriginalValue((Color)this.colorPicker.getValue());
      this.eventDirectionDirty.setOriginalValue(this.eventDirectionToggleGroup.getSelectedToggle());
      this.measureDirectionDirty.setOriginalValue(this.measureDirectionToggleGroup.getSelectedToggle());
      this.offsetValueDirty.setOriginalValue(this.offsetValue.getInput().getText());
      this.phaseDirty.setOriginalValue((RoastingPhase)this.phaseSelector.getValue());
      this.soundDirty.setOriginalValue((Sound)this.soundSelector.getValue());
      this.targetValueDirty.setOriginalValue(this.targetValue.getInput().getText());
      this.timeValueDirty.setOriginalValue(this.timeValue.getInput().getText());
      this.triggerDirty.setOriginalValue((String)this.triggerSelector.getValue());
      this.visualFeedbackDirty.setOriginalValue(this.visualFeedbackEnabler.isSelected());
   }

   private void updateOffsetDirectionLabel() {
      String directionSign = "±";
      if (this.toggleButtonEventAfter.isSelected()) {
         directionSign = "+";
      } else if (this.toggleButtonEventBefore.isSelected()) {
         directionSign = "-";
      }

      this.offsetDirectionLabel.setText(directionSign);
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      ObservableList<String> items = FXCollections.observableArrayList();
      items.addAll(Alarm.MEASURES);
      items.addAll(Alarm.EVENTS);
      items.add("time");
      this.offsetValue.getInput().getStyleClass().add("offset-direction-input");
      Callback<ListView<String>, ListCell<String>> triggerCellFactory = listView -> (new TranslatedListCell<String>() {
               protected void updateItem(String item, boolean empty) {
                  super.updateItem(item, empty);
                  if (item != null) {
                     this.setText(Alarm.getTranslatedTriggerVisualName(item));
                  }
               }
            })
            .withStyles(
               Map.of(
                  Alarm.EVENTS.get(0),
                  "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-30",
                  "time",
                  "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-30"
               )
            );
      this.triggerSelector.setCellFactory(triggerCellFactory);
      this.triggerSelector.setButtonCell((ListCell)triggerCellFactory.call(null));
      this.triggerSelector.setItems(items);
      final StringConverter<String> factoryConverter = this.triggerSelector.getConverter();
      this.triggerSelector.setConverter(new StringConverter<String>() {
         public String toString(String object) {
            return object != null ? org.cropster.roastingintelligence.util.Messages.m_(factoryConverter.toString(object)) : null;
         }

         public String fromString(String string) {
            return (String)factoryConverter.fromString(string);
         }
      });
      this.triggerSelector.valueProperty().addListener((s, o, n) -> {
         if (n != null && Alarm.MEASURES.contains(n) && !n.equals(o)) {
            this.targetValue.setUnit(Measures.getDefaultMeasureUnits().get(n));
         }
      });
      BooleanBinding disableBinding = this.triggerSelector.getSelectionModel().selectedIndexProperty().isEqualTo(-1);
      this.editorsBox.disableProperty().bind(disableBinding);
      this.saveButton.disableProperty().bind(disableBinding);
      Callback<ListView<RoastingPhase>, ListCell<RoastingPhase>> phaseCellFactory = listView -> new ListCell<RoastingPhase>() {
            protected void updateItem(RoastingPhase item, boolean empty) {
               super.updateItem(item, empty);
               this.setText(item != null ? org.cropster.roastingintelligence.util.Messages.m_(item.name()) : null);
               this.setGraphic(null);
            }
         };
      this.phaseSelector.setCellFactory(phaseCellFactory);
      this.phaseSelector.setButtonCell((ListCell)phaseCellFactory.call(null));
      this.phaseSelector.setItems(FXCollections.observableArrayList(RoastingPhase.values()));
      this.visualFeedbackEnabler.setOnMouseClicked(e -> Platform.runLater(this.colorPicker::show));
      JavaFxUtil.bindStyleToColor("-fx-background-color", this.colorPicker.styleProperty(), this.colorPicker.valueProperty());
      this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
         if (KeyboardShortcut.ESC.match(e) && e.getTarget() == this.colorPicker) {
            e.consume();
            this.saveButton.requestFocus();
         }
      });
      Callback<ListView<Sound>, ListCell<Sound>> soundCellFactory = listView -> new ListCell<Sound>() {
            protected void updateItem(Sound item, boolean empty) {
               super.updateItem(item, empty);
               this.setText(
                  item != null
                     ? org.cropster.roastingintelligence.util.Messages.m_(item.getSoundName())
                     : org.cropster.roastingintelligence.util.Messages.m_("No sound selected (muted)")
               );
               this.setGraphic(null);
            }
         };
      this.soundSelector.setCellFactory(soundCellFactory);
      this.soundSelector.setButtonCell((ListCell)soundCellFactory.call(null));
      ObservableList<Sound> sounds = FXCollections.observableArrayList(Sound.values());
      sounds.add(0, null);
      this.soundSelector.setItems(sounds);
      this.soundTestButton.disableProperty().bind(this.soundSelector.valueProperty().isNull());
      this.eventDirectionToggleGroup.selectedToggleProperty().addListener((s, o, n) -> this.updateOffsetDirectionLabel());
      this.measureFormButtonsBar.getNodes().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
      this.eventFormButtonsBar.getNodes().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
      long maxTime = Duration.ofMinutes(59L).plusSeconds(59L).toMillis();
      Validators.setValidator(this.offsetValue, maxTime);
      Validators.setValidator(this.timeValue, maxTime);
      Validators.setValidator(this.targetValue, 10000.0);
      this.colorPicker.valueProperty().addListener(this.colorDirty);
      this.eventDirectionToggleGroup.selectedToggleProperty().addListener(this.eventDirectionDirty);
      this.measureDirectionToggleGroup.selectedToggleProperty().addListener(this.measureDirectionDirty);
      this.offsetValue.getInput().textProperty().addListener(this.offsetValueDirty);
      this.phaseSelector.valueProperty().addListener(this.phaseDirty);
      this.soundSelector.valueProperty().addListener(this.soundDirty);
      this.targetValue.getInput().textProperty().addListener(this.targetValueDirty);
      this.timeValue.getInput().textProperty().addListener(this.timeValueDirty);
      this.triggerSelector.valueProperty().addListener(this.triggerDirty);
      this.visualFeedbackEnabler.selectedProperty().addListener(this.visualFeedbackDirty);
      this.dirty
         .bind(
            this.colorDirty
               .or(this.eventDirectionDirty)
               .or(this.measureDirectionDirty)
               .or(this.offsetValueDirty)
               .or(this.phaseDirty)
               .or(this.soundDirty)
               .or(this.targetValueDirty)
               .or(this.timeValueDirty)
               .or(this.triggerDirty)
               .or(this.visualFeedbackDirty)
         );
   }

   public void updateEditorsVisibility(AlarmType type, String trigger) {
      switch (type) {
         case EVENT_BASED:
            boolean timeTrigger = "time".equals(trigger);
            this.eventForm.setVisible(!timeTrigger);
            this.timeForm.setVisible(timeTrigger);
            this.measureForm.setVisible(false);
            this.phaseForm.setVisible(timeTrigger);
            break;
         case MEASURE_BASED:
            this.measureForm.setVisible(true);
            this.eventForm.setVisible(false);
            this.timeForm.setVisible(false);
            this.phaseForm.setVisible(true);
            if (this.targetValue.getMeasure() == null) {
               this.targetValue.setValue(0.0);
            }
            break;
         default:
            this.logger.error("{}{}", "Unknown alarm type: ", type);
      }
   }

   public Alarm captureEditedAlarm() {
      Alarm editedAlarm = null;
      Sound audioFeedback = (Sound)this.soundSelector.getValue();
      boolean visualFeedback = this.visualFeedbackEnabler.isSelected();
      java.awt.Color visualFeedbackColor = JavaFxUtil.colorToAwt((Color)this.colorPicker.getValue());
      String trigger = (String)this.triggerSelector.getValue();
      if (trigger.equals(this.alarm.getTrigger())) {
         editedAlarm = this.captureSameTriggerAlarm(trigger, audioFeedback, visualFeedback, visualFeedbackColor);
      } else {
         editedAlarm = this.captureDifferentTriggerAlarm(trigger, audioFeedback, visualFeedback, visualFeedbackColor);
      }

      return editedAlarm;
   }

   private Alarm captureSameTriggerAlarm(String trigger, Sound audioFeedback, boolean visualFeedback, java.awt.Color visualFeedbackColor) {
      Alarm.AlarmBuilder builder = Alarm.builder(this.alarm, (RoastingPhase)this.phaseSelector.getValue(), true)
         .withAudibleFeedback(audioFeedback)
         .withVisualFeedback(visualFeedback)
         .withVisualFeedbackColor(visualFeedbackColor);
      AlarmType type = Alarm.typeFromTrigger(trigger);
      switch (type) {
         case EVENT_BASED:
            if ("time".equals(trigger)) {
               builder.withTimeReference(this.timeValue.getValue());
            } else {
               builder.withTimeOffset(this.offsetValue.getValue());
               builder.withTargetDirection(this.getEventDirection());
            }
            break;
         case MEASURE_BASED:
            builder.withTargetMeasure(this.targetValue.getMeasure());
            builder.withTargetDirection(this.getMeasureDirection());
            break;
         default:
            this.logger.error("{}{}", "Unknown alarm type: ", type);
      }

      return builder.build();
   }

   private Alarm captureDifferentTriggerAlarm(String trigger, Sound audioFeedback, boolean visualFeedback, java.awt.Color visualFeedbackColor) {
      AlarmType type = Alarm.typeFromTrigger(trigger);
      switch (type) {
         case EVENT_BASED:
            Alarm.AlarmBuilder builder = Alarm.eventBasedBuilder((RoastingPhase)this.phaseSelector.getValue(), trigger)
               .withId(this.alarm.getId())
               .withAudibleFeedback(audioFeedback)
               .withVisualFeedback(visualFeedback)
               .withVisualFeedbackColor(visualFeedbackColor);
            if ("time".equals(trigger)) {
               builder.withTimeReference(this.timeValue.getValue());
            } else {
               builder.withTimeOffset(this.offsetValue.getValue());
               builder.withTargetDirection(this.getEventDirection());
            }

            return builder.build();
         case MEASURE_BASED:
            return Alarm.measureBasedBuilder((RoastingPhase)this.phaseSelector.getValue(), trigger, this.targetValue.getMeasure())
               .withId(this.alarm.getId())
               .withTargetDirection(this.getMeasureDirection())
               .withAudibleFeedback(audioFeedback)
               .withVisualFeedback(visualFeedback)
               .withVisualFeedbackColor(visualFeedbackColor)
               .build();
         default:
            this.logger.error("{}{}", "Unknown alarm type: ", type);
            return null;
      }
   }

   private AlarmTargetDirection getMeasureDirection() {
      if (this.toggleButtonMeasureAbove.isSelected()) {
         return AlarmTargetDirection.HIGHER;
      } else {
         return this.toggleButtonMeasureBelow.isSelected() ? AlarmTargetDirection.LOWER : AlarmTargetDirection.ANY;
      }
   }

   private AlarmTargetDirection getEventDirection() {
      if (this.toggleButtonEventAfter.isSelected()) {
         return AlarmTargetDirection.HIGHER;
      } else {
         return this.toggleButtonEventBefore.isSelected() ? AlarmTargetDirection.LOWER : AlarmTargetDirection.ANY;
      }
   }
}
