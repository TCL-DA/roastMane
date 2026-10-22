package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.TranslatedListCell;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.cropster.roastingintelligence.Defaults;

public class UnitPreferencesPage extends GridPane implements Initializable {
   @FXML
   private ToggleGroup weightUnitToggleGroup;
   @FXML
   private ToggleGroup temperatureUnitToggleGroup;
   @FXML
   private ToggleGroup pressureUnitToggleGroup;
   @FXML
   private ComboBox<Unit> roastValueUnitComboBox;
   @FXML
   private ComboBox<Unit> roastValue2UnitComboBox;

   public UnitPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      for (ComboBox<Unit> unitComboBox : Arrays.asList(this.roastValueUnitComboBox, this.roastValue2UnitComboBox)) {
         unitComboBox.setButtonCell(new TranslatedListCell());
         unitComboBox.setCellFactory(v -> new TranslatedListCell());
         unitComboBox.getItems().setAll(Defaults.ROAST_VALUES);
      }
   }

   public Unit getWeightUnit() {
      return Unit.valueOf(this.weightUnitToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setWeightUnit(Unit unit) {
      JavaFxUtil.selectToggle(this.weightUnitToggleGroup, unit.name());
   }

   public ToggleGroup getWeightUnitToggleGroup() {
      return this.weightUnitToggleGroup;
   }

   public Unit getTemperatureUnit() {
      return Unit.valueOf(this.temperatureUnitToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setTemperatureUnit(Unit unit) {
      JavaFxUtil.selectToggle(this.temperatureUnitToggleGroup, unit.name());
   }

   public ToggleGroup getTemperatureUnitToggleGroup() {
      return this.temperatureUnitToggleGroup;
   }

   public Unit getPressureUnit() {
      return Unit.valueOf(this.pressureUnitToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setPressureUnit(Unit unit) {
      JavaFxUtil.selectToggle(this.pressureUnitToggleGroup, unit.name());
   }

   public ToggleGroup getPressureUnitToggleGroup() {
      return this.pressureUnitToggleGroup;
   }

   public Unit getRoastValueUnit() {
      return (Unit)this.roastValueUnitComboBox.getValue();
   }

   public void setRoastValueUnit(Unit unit) {
      this.roastValueUnitComboBox.getSelectionModel().select(unit);
   }

   public Unit getRoastValue2Unit() {
      return (Unit)this.roastValue2UnitComboBox.getValue();
   }

   public void setRoastValue2Unit(Unit unit) {
      this.roastValue2UnitComboBox.getSelectionModel().select(unit);
   }

   public void reset() {
      this.setWeightUnit(Unit.KG);
      this.setTemperatureUnit(Unit.CELSIUS);
      this.setPressureUnit(Unit.KILOPASCAL);
      this.setRoastValueUnit(Unit.AGTRON_GOURMET);
      this.setRoastValue2Unit(Unit.AGTRON_GOURMET);
   }
}
