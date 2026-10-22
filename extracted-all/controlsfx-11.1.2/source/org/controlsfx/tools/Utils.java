package org.controlsfx.tools;

import javafx.scene.Node;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class Utils {
   public static Window getWindow(Object owner) throws IllegalArgumentException {
      if (owner == null) {
         Window window = null;

         for (Window w : Window.getWindows()) {
            window = w;
            if (w.isFocused() && !(w instanceof PopupWindow)) {
               return w;
            }
         }

         return window;
      } else if (owner instanceof Window) {
         return (Window)owner;
      } else if (owner instanceof Node) {
         return ((Node)owner).getScene().getWindow();
      } else {
         throw new IllegalArgumentException("Unknown owner: " + owner.getClass());
      }
   }

   public static final String getExcelLetterFromNumber(int number) {
      String letter = "";

      while (number >= 0) {
         int remainder = number % 26;
         letter = (char)(remainder + 65) + letter;
         number = number / 26 - 1;
      }

      return letter;
   }

   public static double clamp(double min, double value, double max) {
      if (value < min) {
         return min;
      } else {
         return value > max ? max : value;
      }
   }

   public static double nearest(double less, double value, double more) {
      double lessDiff = value - less;
      double moreDiff = more - value;
      return lessDiff < moreDiff ? less : more;
   }
}
