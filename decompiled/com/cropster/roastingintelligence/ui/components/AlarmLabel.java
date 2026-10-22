package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.AlarmsUtil;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class AlarmLabel extends VBox {
   @FXML
   private Button menuButton;
   @FXML
   private ToggleSwitch enabledButton;
   @FXML
   private Label visibleIcon;
   @FXML
   private Label directionIcon;
   @FXML
   private Label audibleIcon;
   @FXML
   private Label titleLabel;
   @FXML
   private Label phaseLabel;
   @FXML
   private Circle visiblePreview;
   private final ChangeListener<Boolean> enabledListener;
   private final Consumer<ActionEvent> menuAction;
   private Alarm alarm;

   public AlarmLabel(Consumer<ActionEvent> menuAction, Consumer<Alarm> alarmReplacer) {
      JavaFxUtil.loadComponent(this);
      this.menuAction = menuAction;
      this.enabledListener = (s, o, n) -> alarmReplacer.accept(Alarm.builder(this.alarm, true).withEnabled(!this.alarm.isEnabled()).build());
   }

   public void init(Alarm alarm) {
      this.alarm = alarm;
      String directionTooltipText = null;
      if (alarm.getType() == AlarmType.EVENT_BASED && !"time".equals(alarm.getTrigger())) {
         directionTooltipText = switch (alarm.getTargetDirection()) {
            case ANY -> org.cropster.roastingintelligence.util.Messages.m_("Triggered before and after event");
            case HIGHER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered after event");
            case LOWER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered before event");
         };
      } else if (alarm.getType() == AlarmType.MEASURE_BASED) {
         directionTooltipText = switch (alarm.getTargetDirection()) {
            case ANY -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when increasing and decreasing");
            case HIGHER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when decreasing");
            case LOWER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when increasing");
         };
      }

      this.directionIcon.setTooltip(directionTooltipText != null ? new Tooltip(directionTooltipText) : null);
      this.directionIcon.setVisible(AlarmsUtil.hasDirection(alarm));
      this.directionIcon.setGraphic(AlarmsUtil.iconForDirection(alarm).load(0.75));
      this.visiblePreview.setFill(JavaFxUtil.colorFromAwt(alarm.getVisualFeedbackColor()));
      this.visibleIcon.setVisible(alarm.isVisualFeedback());
      this.audibleIcon.setVisible(alarm.getAudibleFeedback() != null);
      this.titleLabel.setDisable(!Alarm.isSupported(alarm));
      this.titleLabel.setText(alarm.toString());
      this.titleLabel.setTooltip(new Tooltip(alarm.toString()));
      this.phaseLabel.setText(org.cropster.roastingintelligence.util.Messages.m_(alarm.getTriggeringPhase().name()));
      this.phaseLabel.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_(alarm.getTriggeringPhase().name())));
      this.enabledButton.onProperty().removeListener(this.enabledListener);
      this.enabledButton.setOn(alarm.isEnabled());
      this.enabledButton.onProperty().addListener(this.enabledListener);
      this.enabledButton
         .setTooltip(
            new Tooltip(
               alarm.isEnabled() ? org.cropster.roastingintelligence.util.Messages.m_("Disable") : org.cropster.roastingintelligence.util.Messages.m_("Enable")
            )
         );
   }

   public Button getMenuButton() {
      return this.menuButton;
   }

   @FXML
   protected void showMenu(ActionEvent e) {
      this.menuAction.accept(e);
   }
}
