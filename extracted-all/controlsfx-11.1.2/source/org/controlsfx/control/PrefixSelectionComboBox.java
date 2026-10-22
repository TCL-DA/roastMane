package org.controlsfx.control;

import impl.org.controlsfx.tools.PrefixSelectionCustomizer;
import java.util.Optional;
import java.util.function.BiFunction;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;

public class PrefixSelectionComboBox<T> extends ComboBox<T> {
   private final ChangeListener<Boolean> focusedListener = (obs, ov, nv) -> {
      if (nv) {
         this.show();
      }
   };
   private final BooleanProperty displayOnFocusedEnabled = new SimpleBooleanProperty(this, "displayOnFocusedEnabled", false) {
      protected void invalidated() {
         if (this.get()) {
            PrefixSelectionComboBox.this.focusedProperty().addListener(PrefixSelectionComboBox.this.focusedListener);
         } else {
            PrefixSelectionComboBox.this.focusedProperty().removeListener(PrefixSelectionComboBox.this.focusedListener);
         }
      }
   };
   private final BooleanProperty backSpaceAllowed = new SimpleBooleanProperty(this, "backSpaceAllowed", false);
   private final IntegerProperty typingDelay = new SimpleIntegerProperty(this, "typingDelay", 500);
   private final ObjectProperty<BiFunction<ComboBox, String, Optional>> lookup = new SimpleObjectProperty(
      this, "lookup", PrefixSelectionCustomizer.DEFAULT_LOOKUP_COMBOBOX
   );

   public PrefixSelectionComboBox() {
      this.setEditable(false);
      PrefixSelectionCustomizer.customize(this);
   }

   public final boolean isDisplayOnFocusedEnabled() {
      return this.displayOnFocusedEnabled.get();
   }

   public final void setDisplayOnFocusedEnabled(boolean value) {
      this.displayOnFocusedEnabled.set(value);
   }

   public final BooleanProperty displayOnFocusedEnabledProperty() {
      return this.displayOnFocusedEnabled;
   }

   public final boolean isBackSpaceAllowed() {
      return this.backSpaceAllowed.get();
   }

   public final void setBackSpaceAllowed(boolean value) {
      this.backSpaceAllowed.set(value);
   }

   public final BooleanProperty backSpaceAllowedProperty() {
      return this.backSpaceAllowed;
   }

   public final int getTypingDelay() {
      return this.typingDelay.get();
   }

   public final void setTypingDelay(int value) {
      this.typingDelay.set(value);
   }

   public final IntegerProperty typingDelayProperty() {
      return this.typingDelay;
   }

   public final BiFunction<ComboBox, String, Optional> getLookup() {
      return (BiFunction<ComboBox, String, Optional>)this.lookup.get();
   }

   public final void setLookup(BiFunction<ComboBox, String, Optional> value) {
      this.lookup.set(value);
   }

   public final ObjectProperty<BiFunction<ComboBox, String, Optional>> lookupProperty() {
      return this.lookup;
   }
}
