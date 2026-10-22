package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.util.MachineListCell;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Machine;

public class ConfigurationPreferencesPage extends GridPane implements Initializable {
   @FXML
   private MessageBox warningMessageBox;
   @FXML
   private ComboBox<Machine> machineComboBox;
   @FXML
   private CheckBox timeLapseCheckBox;
   @FXML
   private ListView<MeasurementSourceFactory> connectorView;
   @FXML
   private Button addConnectorButton;
   @FXML
   private CheckBox filterCheckbox;
   private final BooleanProperty showDemoOptions = new SimpleBooleanProperty();
   private final BooleanProperty filterPossible = new SimpleBooleanProperty(false);

   public ConfigurationPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      Callback<ListView<Machine>, ListCell<Machine>> machineCellFactory = v -> new MachineListCell();
      this.machineComboBox.setButtonCell((ListCell)machineCellFactory.call(null));
      this.machineComboBox.setCellFactory(machineCellFactory);
      this.connectorView.setEditable(false);
   }

   public MessageBox getWarningMessageBox() {
      return this.warningMessageBox;
   }

   public ComboBox<Machine> getMachineComboBox() {
      return this.machineComboBox;
   }

   public ListView<MeasurementSourceFactory> getConnectorView() {
      return this.connectorView;
   }

   public Button getAddConnectorButton() {
      return this.addConnectorButton;
   }

   public boolean isTimeLapseEnabled() {
      return this.timeLapseCheckBox.isSelected();
   }

   public void setTimeLapseEnabled(boolean timeLapseEnabled) {
      this.timeLapseCheckBox.setSelected(timeLapseEnabled);
   }

   public BooleanProperty showDemoOptionsProperty() {
      return this.showDemoOptions;
   }

   public boolean isShowDemoOptions() {
      return this.showDemoOptions.get();
   }

   public void setShowDemoOptions(boolean showDemoOptions) {
      this.showDemoOptions.set(showDemoOptions);
   }

   public BooleanProperty filterCheckboxSelectedProperty() {
      return this.filterCheckbox.selectedProperty();
   }

   public BooleanProperty filterPossibleProperty() {
      return this.filterPossible;
   }

   public boolean isFilterPossible() {
      return this.filterPossible.get();
   }
}
