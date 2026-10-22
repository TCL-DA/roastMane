package org.controlsfx.control;

import impl.org.controlsfx.skin.ToggleSwitchSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;

public class ToggleSwitch extends Labeled {
   private BooleanProperty selected;
   private static final String DEFAULT_STYLE_CLASS = "toggle-switch";
   private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

   public ToggleSwitch() {
      this.initialize();
   }

   public ToggleSwitch(String text) {
      super(text);
      this.initialize();
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"toggle-switch"});
   }

   public final void setSelected(boolean value) {
      this.selectedProperty().set(value);
   }

   public final boolean isSelected() {
      return this.selected == null ? false : this.selected.get();
   }

   public final BooleanProperty selectedProperty() {
      if (this.selected == null) {
         this.selected = new BooleanPropertyBase() {
            protected void invalidated() {
               Boolean v = this.get();
               ToggleSwitch.this.pseudoClassStateChanged(ToggleSwitch.PSEUDO_CLASS_SELECTED, v);
            }

            public Object getBean() {
               return ToggleSwitch.this;
            }

            public String getName() {
               return "selected";
            }
         };
      }

      return this.selected;
   }

   public void fire() {
      if (!this.isDisabled()) {
         this.setSelected(!this.isSelected());
         this.fireEvent(new ActionEvent());
      }
   }

   protected Skin<?> createDefaultSkin() {
      return new ToggleSwitchSkin(this);
   }

   public String getUserAgentStylesheet() {
      return ToggleSwitch.class.getResource("toggleswitch.css").toExternalForm();
   }
}
