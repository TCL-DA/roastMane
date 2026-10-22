package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.model.RoastingComment;

public class CommentPopupHeader extends VBox implements Initializable {
   @FXML
   private TimeEditor timeInput;
   @FXML
   private MeasureEditor tempInput;

   public CommentPopupHeader() {
      JavaFxUtil.loadComponent(this);
      this.timeInput.visibleProperty().bind(this.tempInput.visibleProperty().not());
      this.timeInput.managedProperty().bind(this.timeInput.visibleProperty());
      this.tempInput.managedProperty().bind(this.tempInput.visibleProperty());
   }

   public void initialize(URL location, ResourceBundle resources) {
      HBox.setHgrow(this.timeInput.getInput(), Priority.ALWAYS);
      Validators.setValidator(this.timeInput);
      HBox.setHgrow(this.tempInput.getInput(), Priority.ALWAYS);
      Validators.setValidator(this.tempInput, 10000.0);
   }

   public void initTempInput(Unit unit) {
      this.tempInput.setVisible(unit != null);
      this.tempInput.setUnit(unit);
   }

   public TimeEditor getTimeInput() {
      return this.timeInput;
   }

   public MeasureEditor getTempInput() {
      return this.tempInput;
   }

   public interface CommentValueProvider {
      RoastingComment nextValue(String var1);
   }
}
