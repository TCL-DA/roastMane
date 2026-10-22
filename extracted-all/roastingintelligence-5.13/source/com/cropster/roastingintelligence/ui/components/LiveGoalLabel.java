package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.SearchHighlightingListCell;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class LiveGoalLabel extends VBox implements Initializable {
   @FXML
   private Label dragLabel;
   @FXML
   private TextFlow nameTextFlow;
   @FXML
   private Label displayModeLabel;
   @FXML
   private ToggleGroup displayModeGroup;
   @FXML
   private ToggleButton meetTheReferenceButton;
   @FXML
   private ToggleButton rangeButton;
   @FXML
   private ToggleButton targetButton;
   private final Map<String, String> localizedNames;
   private final ReadOnlyStringProperty searchTextProperty;
   private final ObjectProperty<LiveGoalDisplayMode> selectedDisplayMode = new SimpleObjectProperty(LiveGoalDisplayMode.HIDDEN);
   private final Map<String, Set<LiveGoalDisplayMode>> supportedDisplayModes;
   private final Tooltip tooltip = new Tooltip();

   public LiveGoalLabel(
      ReadOnlyStringProperty searchTextProperty, Map<String, Set<LiveGoalDisplayMode>> supportedDisplayModes, Map<String, String> localizedNames
   ) {
      JavaFxUtil.loadComponent(this);
      this.localizedNames = localizedNames;
      this.searchTextProperty = searchTextProperty;
      this.supportedDisplayModes = supportedDisplayModes;
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      javafx.scene.control.Tooltip.install(this.nameTextFlow, this.tooltip);
      this.meetTheReferenceButton.setUserData(LiveGoalDisplayMode.MEET_THE_REFERENCE);
      this.rangeButton.setUserData(LiveGoalDisplayMode.RANGE);
      this.targetButton.setUserData(LiveGoalDisplayMode.TARGET);
      this.selectedDisplayMode.bind(Bindings.createObjectBinding(() -> {
         Toggle selectedToggle = this.displayModeGroup.getSelectedToggle();
         return selectedToggle == null ? LiveGoalDisplayMode.HIDDEN : (LiveGoalDisplayMode)selectedToggle.getUserData();
      }, new Observable[]{this.displayModeGroup.selectedToggleProperty()}));
   }

   public void init(LiveGoal liveGoal, boolean smartGoalsEnabled) {
      String id = liveGoal.getId();
      LiveGoalDisplayMode displayMode = liveGoal.getDisplayMode();
      String name = this.localizedNames.get(id);
      this.nameTextFlow
         .getChildren()
         .setAll(
            SearchHighlightingListCell.getHighlightedTextComponents(
               name, (String)this.searchTextProperty.get(), true, "-fx-font-weight: bold; -fx-fill: -fx-color-info-60;", "-fx-font-weight: bold;"
            )
         );
      this.tooltip.setText(name);
      this.getStyleClass().remove("darker");
      this.displayModeLabel.setVisible(false);
      if (displayMode == LiveGoalDisplayMode.HIDDEN) {
         this.getStyleClass().add("darker");
      } else {
         this.displayModeLabel.setText(org.cropster.roastingintelligence.util.Messages.m_(displayMode.name()));
         this.displayModeLabel.setVisible(true);
      }

      Set<LiveGoalDisplayMode> displayModes = this.supportedDisplayModes.getOrDefault(id, Set.of());
      this.updateToggle(this.targetButton, LiveGoalDisplayMode.TARGET, displayMode, displayModes.contains(LiveGoalDisplayMode.TARGET));
      this.updateToggle(
         this.meetTheReferenceButton,
         LiveGoalDisplayMode.MEET_THE_REFERENCE,
         displayMode,
         displayModes.contains(LiveGoalDisplayMode.MEET_THE_REFERENCE),
         smartGoalsEnabled
      );
      this.updateToggle(this.rangeButton, LiveGoalDisplayMode.RANGE, displayMode, displayModes.contains(LiveGoalDisplayMode.RANGE), smartGoalsEnabled);
   }

   private void updateToggle(ToggleButton button, LiveGoalDisplayMode mode, LiveGoalDisplayMode currentMode, boolean supported) {
      button.setDisable(!supported);
      button.setSelected(supported && currentMode == mode);
   }

   private void updateToggle(ToggleButton button, LiveGoalDisplayMode mode, LiveGoalDisplayMode currentMode, boolean supported, boolean smartGoalsEnabled) {
      this.updateToggle(button, mode, currentMode, supported);
      button.setVisible(smartGoalsEnabled);
   }

   public ReadOnlyObjectProperty<LiveGoalDisplayMode> selectedDisplayModeProperty() {
      return this.selectedDisplayMode;
   }

   public LiveGoalDisplayMode getSelectedDisplayMode() {
      return (LiveGoalDisplayMode)this.selectedDisplayMode.get();
   }

   public Label getDragLabel() {
      return this.dragLabel;
   }
}
