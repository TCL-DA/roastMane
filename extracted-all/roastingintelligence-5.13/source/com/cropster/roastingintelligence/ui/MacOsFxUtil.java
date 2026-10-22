package com.cropster.roastingintelligence.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.InputMethodEvent;
import org.apache.commons.lang3.SystemUtils;

public final class MacOsFxUtil {
   private static final String IME_COMPOSITION_RESET_HANDLER_INSTALLED_PROPERTY = MacOsFxUtil.class.getName() + ".imeCompositionResetHandlerInstalled";

   public static void installImeCompositionResetWorkaround(TextInputControl textInput) {
      if (textInput != null && SystemUtils.IS_OS_MAC_OSX) {
         if (!Boolean.TRUE.equals(textInput.getProperties().get(IME_COMPOSITION_RESET_HANDLER_INSTALLED_PROPERTY))) {
            textInput.getProperties().put(IME_COMPOSITION_RESET_HANDLER_INSTALLED_PROPERTY, Boolean.TRUE);
            textInput.addEventHandler(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, event -> {
               Scene scene = textInput.getScene();
               boolean deletedComposition = event.getComposed().isEmpty() && (event.getCommitted().isEmpty() || event.getCommitted().indexOf(8) >= 0);
               if (deletedComposition && scene != null) {
                  scene.enableInputMethodEvents(false);
                  Platform.runLater(() -> scene.enableInputMethodEvents(true));
               }
            });
         }
      }
   }

   private MacOsFxUtil() {
   }
}
