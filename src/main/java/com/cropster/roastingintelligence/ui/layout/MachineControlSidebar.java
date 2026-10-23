package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@DefaultProperty("content")
public class MachineControlSidebar extends DetachableSidebar implements Initializable {
   @FXML
   protected Button layoutButton;
   private final VBox contentPanel = new VBox();

   public MachineControlSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   public ObservableList<Node> getContent() {
      return this.contentPanel.getChildren();
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.layoutButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Toggle slider controls layout")));
      JavaFxUtil.configureTooltips(this);
      JavaFxUtil.setDefaultLineSpacing(this);
      this.setCenter(this.contentPanel);
   }

   @Override
   public void hideWindow() {
      super.hideWindow();
      this.getStyleClass().add("pane--cutoff-right");
   }

   @Override
   public void showWindow() {
      this.getStyleClass().remove("pane--cutoff-right");
      super.showWindow();
      Stage w = this.getDetachedWindow();
      w.setMinWidth(0.0);
      w.setMaxWidth(Double.MAX_VALUE);
      w.sizeToScene();
      w.setMinWidth(w.getWidth());
      w.setMaxWidth(w.getWidth());
   }

   @Override
   void detach() {
      this.showWindow();
   }

   public Button getLayoutButton() {
      return this.layoutButton;
   }
}
