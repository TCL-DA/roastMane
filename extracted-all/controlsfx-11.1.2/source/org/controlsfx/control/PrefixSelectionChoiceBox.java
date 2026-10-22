package org.controlsfx.control;

import impl.org.controlsfx.tools.PrefixSelectionCustomizer;
import javafx.scene.control.ChoiceBox;

public class PrefixSelectionChoiceBox<T> extends ChoiceBox<T> {
   public PrefixSelectionChoiceBox() {
      PrefixSelectionCustomizer.customize(this);
   }
}
