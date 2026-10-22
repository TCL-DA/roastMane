package org.controlsfx.control;

import impl.org.controlsfx.skin.SegmentedButtonSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class SegmentedButton extends ControlsFXControl {
   public static final String STYLE_CLASS_DARK = "dark";
   private final ObservableList<ToggleButton> buttons;
   private final ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty(new ToggleGroup());

   public SegmentedButton() {
      this((ObservableList<ToggleButton>)null);
   }

   public SegmentedButton(ToggleButton... buttons) {
      this(buttons == null ? FXCollections.observableArrayList() : FXCollections.observableArrayList(buttons));
   }

   public SegmentedButton(ObservableList<ToggleButton> buttons) {
      this.getStyleClass().add("segmented-button");
      this.buttons = buttons == null ? FXCollections.observableArrayList() : buttons;
      this.setFocusTraversable(false);
   }

   protected Skin<?> createDefaultSkin() {
      return new SegmentedButtonSkin(this);
   }

   public final ObservableList<ToggleButton> getButtons() {
      return this.buttons;
   }

   public ObjectProperty<ToggleGroup> toggleGroupProperty() {
      return this.toggleGroup;
   }

   public ToggleGroup getToggleGroup() {
      return (ToggleGroup)this.toggleGroupProperty().getValue();
   }

   public void setToggleGroup(ToggleGroup toggleGroup) {
      this.toggleGroupProperty().setValue(toggleGroup);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(SegmentedButton.class, "segmentedbutton.css");
   }
}
