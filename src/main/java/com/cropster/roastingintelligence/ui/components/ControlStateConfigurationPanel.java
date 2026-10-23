package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.listener.ControlStateConfigurationListener;
import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.model.SeparatorType;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.impl.simulation.DeviceSimulatorSourceFactory;

public class ControlStateConfigurationPanel extends AbstractDraggableConfigurationPanel {
   @FXML
   private Label dragRegion;
   @FXML
   private ToggleSwitch enableSwitch;
   @FXML
   private TitledPaneRight titledPanel;
   @FXML
   private GridPane configurationPanel;
   @FXML
   private CheckBox customNameCheckbox;
   @FXML
   private TextField customNameField;
   @FXML
   private CheckBox separatorCheckbox;
   @FXML
   private ToggleGroup separatorToggleGroup;
   @FXML
   private RadioButton separatorAboveRadio;
   @FXML
   private RadioButton separatorBelowRadio;
   @FXML
   private CheckBox timerEnabledCheckbox;
   @FXML
   private TimeEditor timerIntervalEditor;
   private final StringProperty stateName = new SimpleStringProperty();
   private final StringProperty visibleName = new SimpleStringProperty();
   private final ControlStateConfigurationListener controlStateConfigurationListener;
   private ControlStateConfiguration stateConfiguration;
   private boolean initializing = false;

   public ControlStateConfigurationPanel(ControlStateConfigurationListener controlStateConfigurationListener) {
      JavaFxUtil.loadComponent(this);
      this.controlStateConfigurationListener = controlStateConfigurationListener;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.initializeEnableSwitch();
      this.initializeEnableSwitchText();
      this.initializeVisibleName();
      this.initializeCustomName();
      this.initializeSeparator();
      this.initializeTimerEnabled();
   }

   private void initializeTimerEnabled() {
      this.timerEnabledCheckbox.setSelected(false);
      this.timerEnabledCheckbox.selectedProperty().addListener((s, o, n) -> {
         if (this.stateConfiguration != null) {
            this.stateConfiguration.setOffTimerInterval(0L);
            if (n) {
               Long value = this.timerIntervalEditor.getValue();
               if (value != null) {
                  this.stateConfiguration.setOffTimerInterval(value);
               }
            }

            this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
         }
      });
      this.timerIntervalEditor.valueProperty().addListener((s, o, n) -> {
         if (this.stateConfiguration != null) {
            this.stateConfiguration.setOffTimerInterval(0L);
            if (this.timerEnabledCheckbox.isSelected() && n != null) {
               this.stateConfiguration.setOffTimerInterval(n);
            }

            this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
         }
      });
   }

   private void initializeSeparator() {
      this.separatorCheckbox.setSelected(false);
      this.separatorAboveRadio.setSelected(true);
      this.separatorCheckbox.selectedProperty().addListener((s, o, n) -> this.updateSeparatorConfiguration(n));
      this.separatorToggleGroup.selectedToggleProperty().addListener((s, o, n) -> this.updateSeparatorConfiguration(n != null));
   }

   private void updateSeparatorConfiguration(boolean differentFromNone) {
      if (this.stateConfiguration != null) {
         this.stateConfiguration.setSeparator(SeparatorType.NONE);
         if (differentFromNone) {
            if (this.separatorAboveRadio.isSelected()) {
               this.stateConfiguration.setSeparator(SeparatorType.ABOVE);
            } else if (this.separatorBelowRadio.isSelected()) {
               this.stateConfiguration.setSeparator(SeparatorType.BELOW);
            }
         }

         this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
      }
   }

   private void initializeCustomName() {
      this.customNameCheckbox.setSelected(false);
      this.customNameCheckbox.selectedProperty().addListener((s, o, n) -> {
         if (this.stateConfiguration != null) {
            boolean selected = n;
            this.stateConfiguration.setCustomName(selected && StringUtils.isNotBlank(this.customNameField.getText()) ? this.customNameField.getText() : null);
            if (selected) {
               this.customNameField.requestFocus();
            }

            this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
         }
      });
      this.customNameField.textProperty().addListener((s, o, n) -> {
         if (this.stateConfiguration != null) {
            this.stateConfiguration.setCustomName(StringUtils.trimToNull(n));
            this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
         }
      });
   }

   private void initializeVisibleName() {
      this.visibleName
         .bind(
            Bindings.createStringBinding(
               () -> this.enableSwitch.isOn() && this.customNameCheckbox.isSelected() && StringUtils.isNotBlank(this.customNameField.getText())
                     ? this.customNameField.getText()
                     : (String)this.stateName.get(),
               new Observable[]{
                  this.stateName, this.customNameCheckbox.selectedProperty(), this.customNameField.textProperty(), this.enableSwitch.onProperty()
               }
            )
         );
   }

   private void initializeEnableSwitchText() {
      Label switchTextLabel = this.enableSwitch.getTextLabel();
      switchTextLabel.setGraphicTextGap(0.0);
      switchTextLabel.setContentDisplay(ContentDisplay.RIGHT);
      switchTextLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {
         String extraText = "";
         if (this.enableSwitch.isOn() && this.customNameCheckbox.isSelected() && StringUtils.isNotBlank(this.customNameField.getText())) {
            extraText = " - " + (String)this.stateName.get();
         }

         String demoSource = this.getDemoSourceName();
         if (demoSource != null) {
            extraText = extraText + " [" + demoSource + "]";
         }

         Label graphicContent = null;
         if (StringUtils.isNotBlank(extraText)) {
            graphicContent = new Label(extraText);
            graphicContent.getStyleClass().add("channel-secondary-title");
         }

         return graphicContent;
      }, new Observable[]{this.stateName, this.customNameCheckbox.selectedProperty(), this.customNameField.textProperty(), this.enableSwitch.onProperty()}));
   }

   private void initializeEnableSwitch() {
      this.enableSwitch.setOn(false);
      this.enableSwitch.onProperty().addListener((s, o, n) -> {
         if (n != null) {
            boolean enabled = n;
            if (this.stateConfiguration != null) {
               this.stateConfiguration.setEnabled(enabled);
            }

            if (!this.titledPanel.isExpanded() && enabled && !this.initializing) {
               this.titledPanel.setExpanded(true);
            }

            this.controlStateConfigurationListener.settingsChanged(this.stateConfiguration);
         }
      });
   }

   public void init(ControlStateConfiguration stateConfiguration) {
      assert Platform.isFxApplicationThread();

      this.initializing = true;

      try {
         this.reset();
         this.stateConfiguration = stateConfiguration;
         this.stateName.set(org.cropster.roastingintelligence.util.Messages.m_(stateConfiguration.getState().getName()));
         this.enableSwitch.setOn(stateConfiguration.isEnabled());
         if (StringUtils.isNotBlank(stateConfiguration.getCustomName())) {
            this.customNameField.setText(stateConfiguration.getCustomName());
            this.customNameCheckbox.setSelected(true);
         }

         switch (stateConfiguration.getSeparator()) {
            case ABOVE:
               this.separatorCheckbox.setSelected(true);
               this.separatorAboveRadio.setSelected(true);
               break;
            case BELOW:
               this.separatorCheckbox.setSelected(true);
               this.separatorBelowRadio.setSelected(true);
               break;
            default:
               this.separatorCheckbox.setSelected(false);
         }

         long offTimerInterval = stateConfiguration.getOffTimerInterval();
         if (offTimerInterval > 0L) {
            this.timerEnabledCheckbox.setSelected(true);
            this.timerIntervalEditor.setValue(offTimerInterval);
         } else {
            this.timerEnabledCheckbox.setSelected(false);
            this.timerIntervalEditor.setValue(0L);
         }
      } finally {
         this.initializing = false;
      }
   }

   @Override
   public void reset() {
      this.stateConfiguration = null;
      this.stateName.set(null);
      this.enableSwitch.setOn(false);
      this.customNameCheckbox.setSelected(false);
      this.customNameField.setText(null);
      this.separatorCheckbox.setSelected(false);
   }

   @Override
   public Node getDragRegion() {
      return this.dragRegion;
   }

   public ReadOnlyStringProperty stateNameProperty() {
      return this.stateName;
   }

   public String getStateName() {
      return (String)this.stateName.get();
   }

   public ReadOnlyStringProperty visibleNameProperty() {
      return this.visibleName;
   }

   public String getVisibleName() {
      return (String)this.visibleName.get();
   }

   @Override
   public BooleanProperty expandedProperty() {
      return this.titledPanel.expandedProperty();
   }

   private String getDemoSourceName() {
      String demoSource = null;
      if (this.stateConfiguration != null && DeviceSimulatorSourceFactory.class.equals(this.stateConfiguration.getState().getSourceFactory())) {
         demoSource = org.cropster.roastingintelligence.util.Messages.m_(DeviceSimulatorSourceFactory.class.getSimpleName());
         demoSource = demoSource.replaceAll("\\((.*?)\\)", "");
         demoSource = demoSource.replace("  ", " ");
      }

      return demoSource != null ? demoSource.trim() : null;
   }
}
