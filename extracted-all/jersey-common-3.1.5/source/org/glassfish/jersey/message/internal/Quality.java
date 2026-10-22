package org.glassfish.jersey.message.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Quality {
   public static final Comparator<Qualified> QUALIFIED_COMPARATOR = new Comparator<Qualified>() {
      public int compare(Qualified o1, Qualified o2) {
         return Quality.compare(o2.getQuality(), o1.getQuality());
      }
   };
   public static final Comparator<Integer> QUALITY_VALUE_COMPARATOR = new Comparator<Integer>() {
      public int compare(Integer q1, Integer q2) {
         return Quality.compare(q2, q1);
      }
   };
   public static final String QUALITY_PARAMETER_NAME = "q";
   public static final String QUALITY_SOURCE_PARAMETER_NAME = "qs";
   public static final int MINIMUM = 0;
   public static final int MAXIMUM = 1000;
   public static final int DEFAULT = 1000;

   private Quality() {
      throw new AssertionError("Instantiation not allowed.");
   }

   static Map<String, String> enhanceWithQualityParameter(Map<String, String> parameters, String qualityParamName, int quality) {
      if (quality != 1000 || parameters != null && !parameters.isEmpty() && parameters.containsKey(qualityParamName)) {
         if (parameters != null && !parameters.isEmpty()) {
            try {
               parameters.put(qualityParamName, qualityValueToString((float)quality));
               return parameters;
            } catch (UnsupportedOperationException var5) {
               Map<String, String> result = new HashMap<>(parameters);
               result.put(qualityParamName, qualityValueToString((float)quality));
               return result;
            }
         } else {
            return Collections.singletonMap(qualityParamName, qualityValueToString((float)quality));
         }
      } else {
         return parameters;
      }
   }

   private static int compare(int x, int y) {
      return x < y ? -1 : (x == y ? 0 : 1);
   }

   private static String qualityValueToString(float quality) {
      StringBuilder qsb = new StringBuilder(String.format(Locale.US, "%3.3f", quality / 1000.0F));

      int lastIndex;
      while ((lastIndex = qsb.length() - 1) > 2 && qsb.charAt(lastIndex) == '0') {
         qsb.deleteCharAt(lastIndex);
      }

      return qsb.toString();
   }
}
