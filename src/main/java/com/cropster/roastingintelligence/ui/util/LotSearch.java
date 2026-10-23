package com.cropster.roastingintelligence.ui.util;

import javafx.scene.control.ComboBox;
import org.cropster.roastingintelligence.model.Lot;

public class LotSearch extends ComboBoxSearch<Lot> {
   public LotSearch(ComboBox<Lot> comboBox) {
      super(comboBox);
   }

   private boolean containsNormalized(String text, String searchTerm) {
      String normalizedText = this.normalize(text.toLowerCase());
      return normalizedText.contains(searchTerm);
   }

   protected boolean matches(Lot lot, String searchTerm) {
      if (lot == null) {
         return false;
      } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
         String s = searchTerm.toLowerCase();
         String normalizedSearchTerm = this.normalize(s);
         if (lot.getIdTag() != null && lot.getIdTag().toLowerCase().contains(s)) {
            return true;
         } else if (lot.getName() != null && this.containsNormalized(lot.getName(), normalizedSearchTerm)) {
            return true;
         } else {
            return lot.getProject() != null && this.containsNormalized(lot.getProject().getName(), normalizedSearchTerm)
               ? true
               : lot.getLocation() != null && this.containsNormalized(lot.getLocation().getName(), normalizedSearchTerm);
         }
      } else {
         return true;
      }
   }
}
