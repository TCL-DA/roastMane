package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.JWSSignerOption;
import java.util.Set;

public class OptionUtils {
   public static <T extends JWSSignerOption> boolean optionIsPresent(Set<JWSSignerOption> opts, Class<T> tClass) {
      if (opts != null && !opts.isEmpty()) {
         for (JWSSignerOption o : opts) {
            if (o.getClass().isAssignableFrom(tClass)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
