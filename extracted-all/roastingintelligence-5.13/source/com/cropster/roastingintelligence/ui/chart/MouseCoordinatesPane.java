package com.cropster.roastingintelligence.ui.chart;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MouseCoordinatesPane extends GridPane implements Initializable {
   @FXML
   private Label time;
   @FXML
   private Label temperature;
   @FXML
   private Label ror;
   @FXML
   private Label rorCaption;
   private final BooleanProperty rorVisible = new SimpleBooleanProperty(true);
   private final List<ColumnConstraints> rorCcolumnConstraints = new ArrayList<>(4);

   public MouseCoordinatesPane() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.rorCaption.managedProperty().bind(this.rorVisible);
      this.rorCaption.visibleProperty().bind(this.rorVisible);
      this.ror.managedProperty().bind(this.rorVisible);
      this.ror.visibleProperty().bind(this.rorVisible);
      this.rorCcolumnConstraints.add((ColumnConstraints)this.getColumnConstraints().get(5));
      this.rorCcolumnConstraints.add((ColumnConstraints)this.getColumnConstraints().get(6));
      this.rorCcolumnConstraints.add((ColumnConstraints)this.getColumnConstraints().get(7));
      this.rorCcolumnConstraints.add((ColumnConstraints)this.getColumnConstraints().get(8));
      this.rorVisible.addListener((v, o, n) -> {
         if (n) {
            this.getColumnConstraints().addAll(this.rorCcolumnConstraints);
         } else {
            this.getColumnConstraints().removeAll(this.rorCcolumnConstraints);
         }
      });
   }

   public Label getTimeLabel() {
      return this.time;
   }

   public Label getTemperatureLabel() {
      return this.temperature;
   }

   public Label getRorLabel() {
      return this.ror;
   }

   public BooleanProperty rorVisibleProperty() {
      return this.rorVisible;
   }

   public boolean isRorVisible() {
      return this.rorVisible.get();
   }

   public void setRorVisible(boolean visible) {
      this.rorVisible.set(visible);
   }
}
