package org.controlsfx.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;

class DialogUtils {
   static void forcefullyHideDialog(Dialog<?> dialog) {
      DialogPane dialogPane = dialog.getDialogPane();
      if (containsCancelButton(dialog)) {
         dialog.hide();
      } else {
         dialogPane.getButtonTypes().add(ButtonType.CANCEL);
         dialog.hide();
         dialogPane.getButtonTypes().remove(ButtonType.CANCEL);
      }
   }

   static boolean containsCancelButton(Dialog<?> dialog) {
      DialogPane dialogPane = dialog.getDialogPane();

      for (ButtonType type : dialogPane.getButtonTypes()) {
         if (type.getButtonData() == ButtonData.CANCEL_CLOSE) {
            return true;
         }
      }

      return false;
   }
}
