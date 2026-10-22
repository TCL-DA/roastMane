package org.ocpsoft.prettytime;

public interface TimeFormat {
   String format(Duration var1);

   String formatUnrounded(Duration var1);

   String decorate(Duration var1, String var2);

   String decorateUnrounded(Duration var1, String var2);
}
