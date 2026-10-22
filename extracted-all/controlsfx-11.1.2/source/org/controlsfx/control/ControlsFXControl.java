package org.controlsfx.control;

import javafx.scene.control.Control;

abstract class ControlsFXControl extends Control {
   private String stylesheet;

   protected final String getUserAgentStylesheet(Class<?> clazz, String fileName) {
      if (this.stylesheet == null) {
         this.stylesheet = clazz.getResource(fileName).toExternalForm();
      }

      return this.stylesheet;
   }
}
