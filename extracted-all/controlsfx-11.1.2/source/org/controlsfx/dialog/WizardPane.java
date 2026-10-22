package org.controlsfx.dialog;

import javafx.scene.control.DialogPane;

public class WizardPane extends DialogPane {
   public WizardPane() {
      this.getStylesheets().add(Wizard.class.getResource("wizard.css").toExternalForm());
      this.getStyleClass().add("wizard-pane");
   }

   public void onEnteringPage(Wizard wizard) {
   }

   public void onExitingPage(Wizard wizard) {
   }
}
