package com.cropster.roastingintelligence.util;

import com.cropster.roastingintelligence.Data;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Roast;

public final class DataUtil {
   public static String formatSourceLotsInfo(Data data, Roast roast) {
      if (roast.getLot().getSources().size() == 1) {
         Long sourceId = roast.getLot().getSources().iterator().next().getLot();
         Lot source = data.getLots().get(sourceId);
         return String.format(
               "%s - %s | %,.2f %s",
               source != null ? source.getIdTag() : "?",
               source != null ? source.getName() : "?",
               roast.getStartWeight().getAmount(),
               roast.getStartWeight().getUnit()
            )
            .trim();
      } else {
         return String.format("%d Lot(s) | %,.2f %s", roast.getLot().getSources().size(), roast.getStartWeight().getAmount(), roast.getStartWeight().getUnit())
            .trim();
      }
   }

   private DataUtil() {
   }
}
