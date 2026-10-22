package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.InfoBox;
import com.cropster.roastingintelligence.ui.components.ToggleSwitch;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.SystemUtils;

public class CommentsCustomizationTable extends BorderPane implements Initializable {
   private static final String GREEN = "d9dfd4";
   private static final String WHITE = "fcfbfc";
   private static final String GREEN_WHITE = "e7ebe3";
   private static final String[] COMMENTS = new String[]{"gas", "airflow", "colorChange", "firstCrack", "secondCrack", "TEXT_COMMENT"};
   @FXML
   private GridPane gridPane;
   @FXML
   private ToggleSwitch gasPreFillToggle;
   @FXML
   private ToggleSwitch airflowPreFillToggle;
   private final Map<String, CheckBox> sidebarCheckBoxes = new HashMap<>();
   private final Map<String, CheckBox> chartCheckBoxes = new HashMap<>();
   private final Map<String, CheckBox> popupCheckBoxes = new HashMap<>();

   public CommentsCustomizationTable() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      for (Node child : this.gridPane.getChildren()) {
         if (child instanceof Label label) {
            String columnIndex = label.getText();
            switch (columnIndex) {
               case "Ctrl+1":
                  label.setText(
                     SystemUtils.IS_OS_MAC
                        ? org.cropster.roastingintelligence.util.Messages.m_("Cmd+1")
                        : org.cropster.roastingintelligence.util.Messages.m_("Ctrl+1")
                  );
                  break;
               case "Ctrl+2":
                  label.setText(
                     SystemUtils.IS_OS_MAC
                        ? org.cropster.roastingintelligence.util.Messages.m_("Cmd+2")
                        : org.cropster.roastingintelligence.util.Messages.m_("Ctrl+2")
                  );
                  break;
               case "Ctrl+3":
                  label.setText(
                     SystemUtils.IS_OS_MAC
                        ? org.cropster.roastingintelligence.util.Messages.m_("Cmd+3")
                        : org.cropster.roastingintelligence.util.Messages.m_("Ctrl+3")
                  );
            }
         }
      }

      for (int i = 0; i < COMMENTS.length; i++) {
         this.sidebarCheckBoxes.put(COMMENTS[i], new CheckBox());
         this.chartCheckBoxes.put(COMMENTS[i], new CheckBox());
         this.popupCheckBoxes.put(COMMENTS[i], new CheckBox());
         this.gridPane.add(new Label(org.cropster.roastingintelligence.util.Messages.m_(COMMENTS[i])), 0, 2 + i);
         this.gridPane.add((Node)this.sidebarCheckBoxes.get(COMMENTS[i]), 1, 2 + i);
         this.gridPane.add((Node)this.chartCheckBoxes.get(COMMENTS[i]), 2, 2 + i);
         if (i != COMMENTS.length - 1) {
            this.gridPane.add((Node)this.popupCheckBoxes.get(COMMENTS[i]), 3, 2 + i);
         }
      }

      for (Node node : new ArrayList(this.gridPane.getChildrenUnmodifiable())) {
         Integer rowIndex = GridPane.getRowIndex(node);
         if (rowIndex != null && rowIndex > 0) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (columnIndex != null) {
               Integer columnSpan = GridPane.getColumnSpan(node);
               Integer rowSpan = GridPane.getRowSpan(node);
               this.gridPane.getChildren().remove(node);
               StackPane stackPane = new StackPane();
               if (!(node instanceof InfoBox)) {
                  stackPane.setPadding(new Insets(3.0));
               }

               stackPane.getChildren().add(node);
               StackPane.setAlignment(node, columnIndex == 0 ? Pos.CENTER_RIGHT : Pos.CENTER);
               if (rowIndex != 2 && rowIndex != 4 && rowIndex != 6 && rowIndex != 8) {
                  if (rowIndex > 0 && (columnIndex == 3 || columnIndex == 4)) {
                     stackPane.setStyle("-fx-background-color: #fcfbfc");
                  }
               } else {
                  stackPane.setStyle("-fx-background-color: #" + (columnIndex != 3 && columnIndex != 4 ? "d9dfd4" : "e7ebe3"));
               }

               this.gridPane.add(stackPane, columnIndex, rowIndex, columnSpan != null ? columnSpan : 1, rowSpan != null ? rowSpan : 1);
            }
         }
      }

      JavaFxUtil.setDefaultLineSpacing(this);
   }

   private String[] getEvents(Map<String, CheckBox> checkBoxes) {
      List<String> events = new ArrayList<>();

      for (Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
         if (entry.getValue().isSelected()) {
            events.add(entry.getKey());
         }
      }

      String[] array = events.toArray(new String[events.size()]);
      Arrays.sort((Object[])array);
      return array;
   }

   private void setEvents(Map<String, CheckBox> checkBoxes, String... events) {
      if (events != null) {
         Arrays.sort((Object[])events);

         for (Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
            entry.getValue().setSelected(Arrays.binarySearch(events, entry.getKey()) >= 0);
         }
      }
   }

   public String[] getEnabledEventsSidebar() {
      return this.getEvents(this.sidebarCheckBoxes);
   }

   public void setEnabledEventsSidebar(String... events) {
      this.setEvents(this.sidebarCheckBoxes, events);
   }

   public String[] getEnabledEventsChart() {
      return this.getEvents(this.chartCheckBoxes);
   }

   public void setEnabledEventsChart(String... events) {
      this.setEvents(this.chartCheckBoxes, events);
   }

   public String[] getEnabledEventsPopup() {
      return this.getEvents(this.popupCheckBoxes);
   }

   public void setEnabledEventsPopup(String... events) {
      this.setEvents(this.popupCheckBoxes, events);
   }

   public boolean isGasPreFillEnabled() {
      return this.gasPreFillToggle.isOn();
   }

   public void setGasPreFillEnabled(boolean gasPreFillEnabled) {
      this.gasPreFillToggle.setOn(gasPreFillEnabled);
   }

   public boolean isAirflowPreFillEnabled() {
      return this.airflowPreFillToggle.isOn();
   }

   public void setAirflowPreFillEnabled(boolean airflowPreFillEnabled) {
      this.airflowPreFillToggle.setOn(airflowPreFillEnabled);
   }
}
