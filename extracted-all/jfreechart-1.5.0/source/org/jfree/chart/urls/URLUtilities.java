package org.jfree.chart.urls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

public class URLUtilities {
   private static final Class[] STRING_ARGS_2 = new Class[]{String.class, String.class};

   public static String encode(String s, String encoding) {
      Class c = URLEncoder.class;
      String result = null;

      try {
         Method m = c.getDeclaredMethod("encode", STRING_ARGS_2);

         try {
            result = (String)m.invoke(null, s, encoding);
         } catch (InvocationTargetException var6) {
            var6.printStackTrace();
         } catch (IllegalAccessException var7) {
            var7.printStackTrace();
         }
      } catch (NoSuchMethodException var8) {
         result = URLEncoder.encode(s);
      }

      return result;
   }
}
