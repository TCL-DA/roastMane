package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.ui.AlarmController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.chart.ChartWrapper;
import java.awt.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.layout.AnchorPane;

public final class AlarmsUtil {
   private static final String CHAR_ALARMED_STYLE = "chart--alarmed";

   public static void addAlarmsBorderHandler(ChartWrapper chartWrapper, AlarmController alarmController) {
      AnchorPane chartAnchorPane = chartWrapper.getChartAnchorPane();
      ChangeListener<Boolean> visualAlarmEngagedListener = (v, o, n) -> {
         if (n) {
            chartAnchorPane.getStyleClass().add("chart--alarmed");
         } else {
            chartAnchorPane.getStyleClass().remove("chart--alarmed");
         }
      };
      chartWrapper.getProperties().put("visualAlarmEngagedListener", visualAlarmEngagedListener);
      alarmController.visualAlarmEngagedProperty().addListener(new WeakChangeListener(visualAlarmEngagedListener));
      if (alarmController.isVisualAlarmEngaged()) {
         chartAnchorPane.getStyleClass().add("chart--alarmed");
      }

      ObservableList<AlarmController.TriggeredAlarmHolder> triggeredAlarms = alarmController.getTriggeredAlarms();
      ListChangeListener<AlarmController.TriggeredAlarmHolder> triggeredAlarmsListener = c -> updateAlarmedChartColor(chartAnchorPane, triggeredAlarms);
      chartWrapper.getProperties().put("triggeredAlarmsListener", triggeredAlarmsListener);
      triggeredAlarms.addListener(new WeakListChangeListener(triggeredAlarmsListener));
      updateAlarmedChartColor(chartAnchorPane, triggeredAlarms);
   }

   public static Icon iconForDirection(Alarm alarm) {
      boolean isMeasure = alarm.getType() == AlarmType.MEASURE_BASED;

      return switch (alarm.getTargetDirection()) {
         case HIGHER -> isMeasure ? Icon.ARROW_DOWN : Icon.ARROW_RIGHT;
         case LOWER -> isMeasure ? Icon.ARROW_UP : Icon.ARROW_LEFT;
         case ANY -> isMeasure ? Icon.REORDER : Icon.SWITCH;
      };
   }

   public static boolean hasDirection(Alarm alarm) {
      return alarm.getType() == AlarmType.MEASURE_BASED || alarm.getType() == AlarmType.EVENT_BASED && !"time".equals(alarm.getTrigger());
   }

   private static String getAlarmedChartColorStyle(Color awtColor) {
      return "-fx-color-alarmed-chart: #" + JavaFxUtil.colorToHexString(JavaFxUtil.colorFromAwt(awtColor));
   }

   private static void updateAlarmedChartColor(AnchorPane chartAnchorPane, ObservableList<AlarmController.TriggeredAlarmHolder> triggeredAlarms) {
      if (!triggeredAlarms.isEmpty()) {
         chartAnchorPane.setStyle(
            getAlarmedChartColorStyle(
               ((AlarmController.TriggeredAlarmHolder)triggeredAlarms.get(triggeredAlarms.size() - 1)).getAlarm().getVisualFeedbackColor()
            )
         );
      }
   }

   private AlarmsUtil() {
   }
}
