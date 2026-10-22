package org.apache.logging.log4j.core.util.datetime;

import java.text.FieldPosition;

@Deprecated
public abstract class Format {
   public final String format(final Object obj) {
      return this.format(obj, new StringBuilder(), new FieldPosition(0)).toString();
   }

   public abstract StringBuilder format(Object obj, StringBuilder toAppendTo, FieldPosition pos);
}
