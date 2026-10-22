package com.cropster.roastingintelligence.ui.util;

import java.util.Locale;
import javafx.scene.control.ListCell;

public class LocaleListCell extends ListCell<Locale> {
   public static final Locale SYSTEM_DEFAULT = new Locale("");

   protected void updateItem(Locale item, boolean empty) {
      super.updateItem(item, empty);
      if (item == SYSTEM_DEFAULT) {
         this.setText(org.cropster.roastingintelligence.util.Messages.m_("System default"));
      } else if (item != null) {
         String text = item.getDisplayName();
         if (!item.getLanguage().equals(Locale.getDefault().getLanguage())) {
            text = text + " / " + item.getDisplayName(item);
         }

         this.setText(text);
      } else {
         this.setText(null);
      }

      this.setGraphic(null);
   }
}
