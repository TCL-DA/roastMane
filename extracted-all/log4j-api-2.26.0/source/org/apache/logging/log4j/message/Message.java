package org.apache.logging.log4j.message;

import java.io.Serializable;

public interface Message extends Serializable {
   String getFormattedMessage();

   @Deprecated
   default String getFormat() {
      return null;
   }

   Object[] getParameters();

   Throwable getThrowable();
}
