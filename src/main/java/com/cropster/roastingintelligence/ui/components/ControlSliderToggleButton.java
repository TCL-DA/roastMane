package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.Channel;

public class ControlSliderToggleButton extends AnchorPane {
   private static final Insets NO_SWITCH_PADDING = new Insets(0.0);
   private static final Insets SWITCH_PADDING = new Insets(0.0, 28.0, 0.0, 0.0);
   @FXML
   private ToggleButton toggleButton;
   @FXML
   private GridPane contentPane;
   @FXML
   private Label iconLabel;
   @FXML
   private Label nameLabel;
   @FXML
   private Label unitLabel;
   @FXML
   private Label valueLabel;
   @FXML
   private ToggleSwitch autoModeToggle;
   private final BooleanProperty displayable = new SimpleBooleanProperty(false);
   private ControlSlider slider;

   public ControlSliderToggleButton() {
      JavaFxUtil.loadComponent(this);
   }

   public void init(AppModel appModel) {
      Channel channel = appModel.getChannel(this.slider.getMeasurement());
      if (channel != null) {
         Platform.runLater(
            () -> JavaFxUtil.setStyleRecursively(
                  this.iconLabel.getGraphic(), "-fx-fill: #" + JavaFxUtil.colorToHexString(JavaFxUtil.colorFromAwt(channel.getColor())) + ";"
               )
         );
      }
   }

   public ControlSlider getSlider() {
      return this.slider;
   }

   public void setSlider(ControlSlider slider) {
      this.slider = slider;
      slider.setToggle(this);
      this.iconLabel
         .graphicProperty()
         .bind(Bindings.createObjectBinding(() -> slider.createIconGraphics("icon--muted"), new Observable[]{slider.measurementProperty()}));
      this.valueLabel.textProperty().bind(slider.getValueEditor().getInput().textProperty());
      this.unitLabel.textProperty().bind(slider.getValueEditor().getUnitLabel().textProperty());
      this.nameLabel.textProperty().bind(slider.getTitleLabel().textProperty());
      this.autoModeToggle.onProperty().bindBidirectional(slider.getAutoModeToggle().onProperty());
      this.autoModeToggle.visibleProperty().addListener((s, o, n) -> this.nameLabel.setPadding(n ? SWITCH_PADDING : NO_SWITCH_PADDING));
      this.autoModeToggle.visibleProperty().bind(slider.getAutoModeToggle().visibleProperty());
      slider.getAutoModeToggle().disabledProperty().addListener((s, o, n) -> this.autoModeToggle.setDisable(n));
   }

   public final BooleanProperty selectedProperty() {
      return this.toggleButton.selectedProperty();
   }

   public final boolean isSelected() {
      return this.toggleButton.isSelected();
   }

   public final void setSelected(boolean value) {
      this.toggleButton.setSelected(value);
   }

   public final ObjectProperty<ToggleGroup> toggleGroupProperty() {
      return this.toggleButton.toggleGroupProperty();
   }

   public final ToggleGroup getToggleGroup() {
      return this.toggleButton.getToggleGroup();
   }

   public final void setToggleGroup(ToggleGroup group) {
      this.toggleButton.setToggleGroup(group);
   }

   public final ReadOnlyBooleanProperty displayableProperty() {
      return this.displayable;
   }

   public boolean isDisplayable() {
      return this.displayable.get();
   }

   public void setDisplayable(boolean displayable) {
      this.displayable.set(displayable);
   }

   public ToggleSwitch getAutoModeToggle() {
      return this.autoModeToggle;
   }

   public ToggleButton getToggleButton() {
      return this.toggleButton;
   }
}
