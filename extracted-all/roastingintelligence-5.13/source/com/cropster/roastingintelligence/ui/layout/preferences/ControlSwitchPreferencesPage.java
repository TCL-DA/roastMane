package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class ControlSwitchPreferencesPage extends GridPane implements Initializable {
   @FXML
   private EmptyStateList<ControlStateConfiguration> switchControlStateView;

   public ControlSwitchPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public ListView<ControlStateConfiguration> getSwitchControlChannelView() {
      return this.switchControlStateView.getListView();
   }

   public EmptyStateList<ControlStateConfiguration> getSwitchControlsEmptyStateList() {
      return this.switchControlStateView;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.getSwitchControlChannelView().refresh();
         }
      });
   }
}
