package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.Defaults;

public class InterfacePreferencesPage extends GridPane implements Initializable {
   @FXML
   private FormComponent replacementLotsSelectionForm;
   @FXML
   private CheckBox automaticallyReplaceLotCheckbox;
   @FXML
   private CheckBox betweenBatchProtocolCheckbox;
   @FXML
   private ToggleGroup timerToggleGroup;
   @FXML
   private CheckBox stopRoastingConfirmationCheckbox;
   @FXML
   private CheckBox condensedCheckbox;
   @FXML
   private FormComponent sliderControlsForm;
   @FXML
   private ToggleGroup buttonsPositionToggleGroup;

   public InterfacePreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      PreferencesPanel.addTitleArrow(this.sliderControlsForm);
   }

   public FormComponent getReplacementLotsSelectionForm() {
      return this.replacementLotsSelectionForm;
   }

   public boolean isAutomaticallyReplaceLotEnabled() {
      return this.automaticallyReplaceLotCheckbox.isSelected();
   }

   public void setAutomaticallyReplaceLotEnabled(boolean automaticallyReplaceLotEnabled) {
      this.automaticallyReplaceLotCheckbox.setSelected(automaticallyReplaceLotEnabled);
   }

   public boolean isBetweenBatchProtocolEnabled() {
      return this.betweenBatchProtocolCheckbox.isSelected();
   }

   public void setBetweenBatchProtocolEnabled(boolean betweenBatchProtocolEnabled) {
      this.betweenBatchProtocolCheckbox.setSelected(betweenBatchProtocolEnabled);
   }

   public AppModel.TimerType getTimerType() {
      return AppModel.TimerType.valueOf(this.timerToggleGroup.getSelectedToggle().getUserData().toString());
   }

   public void setTimerType(AppModel.TimerType timerType) {
      JavaFxUtil.selectToggle(this.timerToggleGroup, timerType != null ? timerType.name() : Defaults.TIMER_TYPE.name());
   }

   public boolean isStopRoastingConfirmationEnabled() {
      return this.stopRoastingConfirmationCheckbox.isSelected();
   }

   public void setStopRoastingConfirmationEnabled(boolean stopRoastingConfirmationEnabled) {
      this.stopRoastingConfirmationCheckbox.setSelected(stopRoastingConfirmationEnabled);
   }

   public boolean isSliderControlButtonsOnTop() {
      Side side = Side.valueOf(this.buttonsPositionToggleGroup.getSelectedToggle().getUserData().toString());
      return side == Side.TOP;
   }

   public void setSliderControlButtonsOnTop(boolean sliderControlsButtonsOnTop) {
      JavaFxUtil.selectToggle(this.buttonsPositionToggleGroup, sliderControlsButtonsOnTop ? Side.TOP.name() : Side.BOTTOM.name());
   }

   public boolean isCondensedChannelStatus() {
      return this.condensedCheckbox.isSelected();
   }

   public void setCondensedChannelStatus(boolean condensedChannelStatus) {
      this.condensedCheckbox.setSelected(condensedChannelStatus);
   }
}
