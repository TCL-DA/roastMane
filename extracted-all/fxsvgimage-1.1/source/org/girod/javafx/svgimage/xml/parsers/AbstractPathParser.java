package org.girod.javafx.svgimage.xml.parsers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractPathParser {
   public static final short PATH_NONE = -1;
   public static final short MOVE_TO = 0;
   public static final short CLOSE_PATH = 1;
   public static final short LINE_TO = 2;
   public static final short HORIZONTAL_LINE_TO = 3;
   public static final short VERTICAL_LINE_TO = 4;
   public static final short CUBIC_CURVE = 5;
   public static final short SMOOTH_CUBIC_CURVE = 6;
   public static final short QUADRATIC_CURVE = 7;
   public static final short SMOOTH_QUADRATIC_CURVE = 8;
   public static final short ELLIPTICAL_CURVE = 9;
   public static final Pattern LETTER = Pattern.compile("[sSlLhHvVmMcCqQtTaAzZ]");
   public static final Pattern PLUSMINUS = Pattern.compile("[+-]?\\d*([eE][+-]\\d+)?(\\.\\d+)?");

   protected void decomposePart(List<String> list, String token) {
      int offset = 0;
      Matcher m = PLUSMINUS.matcher(token);
      String part = token;

      while (true) {
         boolean found = m.find(offset);
         if (!found) {
            list.add(part);
            break;
         }

         int start = m.start();
         int end = m.end();
         String value = token.substring(start, end);
         list.add(value);
         offset = end;
         if (end >= token.length()) {
            break;
         }

         part = token.substring(end);
      }
   }
}
