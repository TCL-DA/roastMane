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

public class ControlSliderPreferencesPage extends GridPane implements Initializable {
   @FXML
   private VBox sliderControlWarningPanel;
   @FXML
   private EmptyStateList<Channel> sliderControlChannelView;

   public ControlSliderPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public ListView<Channel> getSliderControlChannelView() {
      return this.sliderControlChannelView.getListView();
   }

   public EmptyStateList<Channel> getSliderControlsEmptyStateList() {
      return this.sliderControlChannelView;
   }

   public boolean isSliderControlWarningPanelVisible() {
      return this.sliderControlWarningPanel.isVisible();
   }

   public void setSliderControlWarningPanelVisible(boolean visible) {
      this.sliderControlWarningPanel.setVisible(visible);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.getSliderControlChannelView().refresh();
         }
      });
   }
}
