package org.apache.commons.io.file;

import org.apache.commons.io.IOUtils;

public enum StandardDeleteOption implements DeleteOption {
   OVERRIDE_READ_ONLY;

   public static boolean overrideReadOnly(DeleteOption[] options) {
      if (IOUtils.length((Object[])options) == 0) {
         return false;
      } else {
         for (DeleteOption deleteOption : options) {
            if (deleteOption == OVERRIDE_READ_ONLY) {
               return true;
            }
         }

         return false;
      }
   }
}
