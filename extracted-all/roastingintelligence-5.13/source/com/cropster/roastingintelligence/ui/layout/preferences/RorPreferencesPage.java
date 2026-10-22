package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import com.cropster.roastingintelligence.ui.util.RendererListCell;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.util.Smoothing;

public class RorPreferencesPage extends GridPane implements Initializable {
   @FXML
   private ToggleGroup rorSmoothingToggleGroup;
   @FXML
   private ComboBox<Integer> rorComboBox;
   @FXML
   private FormComponent timeExpressionForm;

   public RorPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      Function<Integer, String> renderFunction = v -> v + "s";
      this.rorComboBox.setItems(FXCollections.observableArrayList(Defaults.RISE_RATES));
      this.rorComboBox.setButtonCell(new RendererListCell<>(renderFunction));
      this.rorComboBox.setCellFactory(v -> new RendererListCell<>(renderFunction));
      this.rorComboBox.setValue(60);
      PreferencesPanel.addTitleArrow(this.timeExpressionForm);
   }

   public Smoothing getRorSmoothing() {
      return Smoothing.valueOf(this.rorSmoothingToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setRorSmoothing(Smoothing smoothing) {
      JavaFxUtil.selectToggle(this.rorSmoothingToggleGroup, smoothing.name());
   }

   public int getRor() {
      return (Integer)this.rorComboBox.getValue();
   }

   public void setRor(int ror) {
      this.rorComboBox.setValue(ror);
   }

   public ReadOnlyObjectProperty<Integer> rorValueProperty() {
      return this.rorComboBox.valueProperty();
   }
}
