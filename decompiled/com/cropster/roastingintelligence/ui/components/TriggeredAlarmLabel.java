package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.listener.TriggeredAlarmEvent;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.ui.AlarmController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.AlarmsUtil;
import java.text.MessageFormat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class TriggeredAlarmLabel extends VBox {
   private static final String DARKER_STYLE = "darker";
   private static final String TAG_TRANSPARENT_STYLE = "tag--transparent";
   private static final String TAG_WARNING_STYLE = "tag--warning";
   @FXML
   private Button clearButton;
   @FXML
   private Label pastBadge;
   @FXML
   private Label activeBadge;
   @FXML
   private Label directionIcon;
   @FXML
   private Label visibleIcon;
   @FXML
   private Label audibleIcon;
   @FXML
   private Label titleLabel;
   @FXML
   private Label timeLabel;
   @FXML
   private Circle visiblePreview;
   private final ChangeListener<Number> progressListener = (s, o, n) -> this.updateProgressRepresentation(n.doubleValue() > 0.0);
   private final EventHandler<ActionEvent> clearAction;
   private AlarmController.TriggeredAlarmHolder alarmHolder;

   public TriggeredAlarmLabel(EventHandler<ActionEvent> clearAction) {
      JavaFxUtil.loadComponent(this);
      this.clearAction = clearAction;
   }

   public Button getClearButton() {
      return this.clearButton;
   }

   public AlarmController.TriggeredAlarmHolder getAlarmHolder() {
      return this.alarmHolder;
   }

   public void init(AlarmController.TriggeredAlarmHolder alarmHolder) {
      this.alarmHolder = alarmHolder;
      String directionTooltipText = null;
      if (alarmHolder.getAlarm().getType() == AlarmType.EVENT_BASED && !"time".equals(alarmHolder.getAlarm().getTrigger())) {
         directionTooltipText = switch (alarmHolder.getAlarm().getTargetDirection()) {
            case ANY -> org.cropster.roastingintelligence.util.Messages.m_("Triggered before and after event");
            case HIGHER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered after event");
            case LOWER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered before event");
         };
      } else if (alarmHolder.getAlarm().getType() == AlarmType.MEASURE_BASED) {
         directionTooltipText = switch (alarmHolder.getAlarm().getTargetDirection()) {
            case ANY -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when increasing and decreasing");
            case HIGHER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when decreasing");
            case LOWER -> org.cropster.roastingintelligence.util.Messages.m_("Triggered when increasing");
         };
      }

      this.directionIcon.setTooltip(directionTooltipText != null ? new Tooltip(directionTooltipText) : null);
      this.directionIcon.setVisible(AlarmsUtil.hasDirection(alarmHolder.getAlarm()));
      this.directionIcon.setGraphic(AlarmsUtil.iconForDirection(alarmHolder.getAlarm()).load(0.75));
      this.visiblePreview.setFill(JavaFxUtil.colorFromAwt(alarmHolder.getAlarm().getVisualFeedbackColor()));
      this.visibleIcon.setVisible(alarmHolder.getAlarm().isVisualFeedback());
      this.audibleIcon.setVisible(alarmHolder.getAlarm().getAudibleFeedback() != null);
      this.updateProgressRepresentation(alarmHolder.getProgress() > 0.0);
      alarmHolder.progressProperty().addListener(new WeakChangeListener(this.progressListener));
      this.titleLabel.setText(alarmHolder.getAlarm().toString());
      this.titleLabel.setTooltip(new Tooltip(this.titleLabel.getText()));
      TriggeredAlarmEvent event = alarmHolder.getTriggeredAlarmEvent();
      this.timeLabel
         .setText(
            MessageFormat.format(
               "{0,time,mm:ss} ({1,time,HH:mm:ss}) {2}",
               event.getTriggerTime(),
               event.getActualTriggerTime(),
               org.cropster.roastingintelligence.util.Messages.m_(event.getTriggerPhase().name())
            )
         );
      this.timeLabel.setTooltip(new Tooltip(this.timeLabel.getText()));
   }

   private void updateProgressRepresentation(boolean isInProgress) {
      this.activeBadge.setVisible(isInProgress);
      this.getStyleClass().removeAll(new String[]{"darker"});
      if (!isInProgress) {
         this.getStyleClass().add("darker");
      }
   }

   @FXML
   protected void clear(ActionEvent e) {
      this.clearAction.handle(e);
   }
}
