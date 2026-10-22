package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public class ReferenceInfoPanel extends VBox implements Initializable {
   private static final String MANUAL_URL_REFERENCE = "https://help.cropster.com/en/knowledge/setting-a-reference-curve";

   public ReferenceInfoPanel() {
      JavaFxUtil.loadComponent(this);
   }

   @FXML
   public void showManual(ActionEvent e) {
      RoastingIntelligenceApp.showDocument("https://help.cropster.com/en/knowledge/setting-a-reference-curve");
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }
}
