package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import com.cropster.roastingintelligence.ui.util.AnimatedBorders;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.SystemUtils;

public class PreferencesPanel extends VBox implements Initializable {
   private static final String TEXT_DISABLED = "text-disabled";
   private static final String TITLE_ARROW = "➔ ";
   @FXML
   private VBox mainContainer;
   @FXML
   private VBox menuContainer;
   @FXML
   private ToggleGroup menuToggleGroup;
   @FXML
   private ToggleButton generalItem;
   @FXML
   private ToggleButton unitItem;
   @FXML
   private ToggleButton interfaceItem;
   @FXML
   private ToggleButton rorItem;
   @FXML
   private ToggleButton smartItem;
   @FXML
   private ToggleButton chartItem;
   @FXML
   private ToggleButton printItem;
   @FXML
   private ToggleButton configurationItem;
   @FXML
   private Label configurationItemWaitLabel;
   @FXML
   private ToggleButton measurementItem;
   @FXML
   private ToggleButton controlSliderItem;
   @FXML
   private ToggleButton controlSwitchItem;
   @FXML
   private StackPane centralContainer;
   @FXML
   private ScrollPane generalPane;
   @FXML
   private GeneralPreferencesPage generalPreferencesPage;
   @FXML
   private ScrollPane unitPane;
   @FXML
   private UnitPreferencesPage unitPreferencesPage;
   @FXML
   private ScrollPane interfacePane;
   @FXML
   private InterfacePreferencesPage interfacePreferencesPage;
   @FXML
   private ScrollPane rorPane;
   @FXML
   private RorPreferencesPage rorPreferencesPage;
   @FXML
   private ScrollPane smartPane;
   @FXML
   private SmartPreferencesPage smartPreferencesPage;
   @FXML
   private ScrollPane chartPane;
   @FXML
   private ChartPreferencesPage chartPreferencesPage;
   @FXML
   private ScrollPane printPane;
   @FXML
   private PrintPreferencesPage printPreferencesPage;
   @FXML
   private ConfigurationPreferencesPage configurationPreferencesPage;
   @FXML
   private MeasurementPreferencesPage measurementPreferencesPage;
   @FXML
   private ControlSliderPreferencesPage controlSliderPreferencesPage;
   @FXML
   private ControlSwitchPreferencesPage controlSwitchPreferencesPage;
   @FXML
   private HBox buttonBar;
   @FXML
   private Button cancelButton;
   @FXML
   private Button saveButton;

   public static void addTitleArrow(FormComponent component) {
      component.setTitle("➔ " + component.getTitle());
   }

   public static void addTitleArrow(Label label) {
      label.setText("➔ " + label.getText());
   }

   public PreferencesPanel() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      JavaFxUtil.preventToggleGroupBeingUnselected(this.menuToggleGroup);
      if (SystemUtils.IS_OS_WINDOWS) {
         ArrayList<Node> buttons = new ArrayList<>(this.buttonBar.getChildren());
         Collections.reverse(buttons);
         this.buttonBar.getChildren().setAll(buttons);
      }

      JavaFxUtil.newRecursiveChildrenStream(this.centralContainer)
         .filter(RadioButton.class::isInstance)
         .filter(n -> n.getStyleClass().contains("translate-userdata"))
         .forEach(n -> {
            RadioButton r = (RadioButton)n;
            r.setText(org.cropster.roastingintelligence.util.Messages.m_(Unit.valueOf(r.getUserData().toString())));
         });
      this.controlSwitchItem.visibleProperty().bind(this.controlSwitchPreferencesPage.getSwitchControlsEmptyStateList().listEmptyProperty().not());
      JavaFxUtil.setDefaultLineSpacing(this.mainContainer);
   }

   public <T extends Node> T getPage(Class<T> pageClass) {
      return pageClass != null ? JavaFxUtil.newRecursiveChildrenStream(this.centralContainer, pageClass).findFirst().orElse(null) : null;
   }

   public void selectPage(Class<? extends Node> pageClass) {
      if (pageClass != null) {
         String pageIdentifier = pageClass.getSimpleName();
         if (this.menuContainer.lookup("#" + pageIdentifier) instanceof Toggle toggle) {
            Platform.runLater(() -> this.menuToggleGroup.selectToggle(toggle));
         }
      }
   }

   public void setDisablePartially(boolean disable) {
      this.centralContainer.getChildren().forEach(t -> this.setDisableRecursively(t, disable));
   }

   public void resetChangeLabels() {
      this.generalPreferencesPage.resetChangeLabel();
      this.chartPreferencesPage.resetChangeLabel();
   }

   public void setSaveHandler(EventHandler<ActionEvent> saveHandler) {
      this.saveButton.setOnAction(saveHandler);
   }

   public void setCancelHandler(EventHandler<ActionEvent> cancelHandler) {
      this.cancelButton.setOnAction(cancelHandler);
   }

   public void putConfigurationItemOnWait() {
      AnimatedBorders.createWaitRing(this.configurationItemWaitLabel, 16.0);
      this.configurationItem.setDisable(true);
   }

   public void resetConfigurationItem() {
      if (AnimatedBorders.isAnimatedBorderSet(this.configurationItemWaitLabel)) {
         AnimatedBorders.stopAnRestoreOriginalBorder(this.configurationItemWaitLabel);
      }

      this.configurationItem.setDisable(false);
   }

   private void setDisableRecursively(Node n, boolean disable) {
      ObservableList<String> styleClass = n.getStyleClass();
      if (!styleClass.contains("always-enabled") && !styleClass.contains("always-disabled")) {
         if (n instanceof ScrollPane s) {
            this.setDisableRecursively(s.getContent(), disable);
         } else {
            if (n instanceof Pane p) {
               if (!p.getChildrenUnmodifiable().isEmpty()) {
                  p.getChildrenUnmodifiable().forEach(c -> this.setDisableRecursively(c, disable));
                  return;
               }
            } else if (n instanceof Text) {
               if (disable) {
                  addTextDisabledStyle(styleClass);
               } else {
                  removeTextDisabledStyle(styleClass);
               }
            }

            try {
               n.setDisable(disable);
            } catch (Exception var6) {
            }
         }
      }
   }

   private static void addTextDisabledStyle(ObservableList<String> styleClass) {
      if (styleClass.stream().filter("text-disabled"::equals).count() == 0L) {
         styleClass.add("text-disabled");
      }
   }

   private static void removeTextDisabledStyle(ObservableList<String> styleClass) {
      for (long count = styleClass.stream().filter("text-disabled"::equals).count(); count > 0L; count--) {
         styleClass.remove("text-disabled");
      }
   }
}
