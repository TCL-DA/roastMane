package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.model.Channel;

public class MeasurementPreferencesPage extends GridPane implements Initializable {
   @FXML
   private VBox warningPanel;
   @FXML
   private EmptyStateList<Channel> channelView;

   public MeasurementPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public ListView<Channel> getChannelView() {
      return this.channelView.getListView();
   }

   public EmptyStateList<Channel> getEmptyStateList() {
      return this.channelView;
   }

   public boolean isWarningPanelVisible() {
      return this.warningPanel.isVisible();
   }

   public void setWarningPanelVisible(boolean visible) {
      this.warningPanel.setVisible(visible);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.getChannelView().refresh();
         }
      });
   }
}
