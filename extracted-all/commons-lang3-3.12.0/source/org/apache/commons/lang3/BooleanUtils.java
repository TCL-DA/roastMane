package org.apache.commons.lang3;

import org.apache.commons.lang3.math.NumberUtils;

public class BooleanUtils {
   public static final String FALSE = "false";
   public static final String NO = "no";
   public static final String OFF = "off";
   public static final String ON = "on";
   public static final String TRUE = "true";
   public static final String YES = "yes";

   public static boolean and(boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");

      for (boolean element : array) {
         if (!element) {
            return false;
         }
      }

      return true;
   }

   public static Boolean and(Boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");

      try {
         boolean[] primitive = ArrayUtils.toPrimitive(array);
         return and(primitive) ? Boolean.TRUE : Boolean.FALSE;
      } catch (NullPointerException var2) {
         throw new IllegalArgumentException("The array must not contain any null elements");
      }
   }

   public static Boolean[] booleanValues() {
      return new Boolean[]{Boolean.FALSE, Boolean.TRUE};
   }

   public static int compare(boolean x, boolean y) {
      if (x == y) {
         return 0;
      } else {
         return x ? 1 : -1;
      }
   }

   public static boolean isFalse(Boolean bool) {
      return Boolean.FALSE.equals(bool);
   }

   public static boolean isNotFalse(Boolean bool) {
      return !isFalse(bool);
   }

   public static boolean isNotTrue(Boolean bool) {
      return !isTrue(bool);
   }

   public static boolean isTrue(Boolean bool) {
      return Boolean.TRUE.equals(bool);
   }

   public static Boolean negate(Boolean bool) {
      if (bool == null) {
         return null;
      } else {
         return bool ? Boolean.FALSE : Boolean.TRUE;
      }
   }

   public static boolean or(boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");

      for (boolean element : array) {
         if (element) {
            return true;
         }
      }

      return false;
   }

   public static Boolean or(Boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");

      try {
         boolean[] primitive = ArrayUtils.toPrimitive(array);
         return or(primitive) ? Boolean.TRUE : Boolean.FALSE;
      } catch (NullPointerException var2) {
         throw new IllegalArgumentException("The array must not contain any null elements");
      }
   }

   public static boolean[] primitiveValues() {
      return new boolean[]{false, true};
   }

   public static boolean toBoolean(Boolean bool) {
      return bool != null && bool;
   }

   public static boolean toBoolean(int value) {
      return value != 0;
   }

   public static boolean toBoolean(int value, int trueValue, int falseValue) {
      if (value == trueValue) {
         return true;
      } else if (value == falseValue) {
         return false;
      } else {
         throw new IllegalArgumentException("The Integer did not match either specified value");
      }
   }

   public static boolean toBoolean(Integer value, Integer trueValue, Integer falseValue) {
      if (value == null) {
         if (trueValue == null) {
            return true;
         }

         if (falseValue == null) {
            return false;
         }
      } else {
         if (value.equals(trueValue)) {
            return true;
         }

         if (value.equals(falseValue)) {
            return false;
         }
      }

      throw new IllegalArgumentException("The Integer did not match either specified value");
   }

   public static boolean toBoolean(String str) {
      return toBooleanObject(str) == Boolean.TRUE;
   }

   public static boolean toBoolean(String str, String trueString, String falseString) {
      if (str == trueString) {
         return true;
      } else if (str == falseString) {
         return false;
      } else {
         if (str != null) {
            if (str.equals(trueString)) {
               return true;
            }

            if (str.equals(falseString)) {
               return false;
            }
         }

         throw new IllegalArgumentException("The String did not match either specified value");
      }
   }

   public static boolean toBooleanDefaultIfNull(Boolean bool, boolean valueIfNull) {
      return bool == null ? valueIfNull : bool;
   }

   public static Boolean toBooleanObject(int value) {
      return value == 0 ? Boolean.FALSE : Boolean.TRUE;
   }

   public static Boolean toBooleanObject(int value, int trueValue, int falseValue, int nullValue) {
      if (value == trueValue) {
         return Boolean.TRUE;
      } else if (value == falseValue) {
         return Boolean.FALSE;
      } else if (value == nullValue) {
         return null;
      } else {
         throw new IllegalArgumentException("The Integer did not match any specified value");
      }
   }

   public static Boolean toBooleanObject(Integer value) {
      if (value == null) {
         return null;
      } else {
         return value == 0 ? Boolean.FALSE : Boolean.TRUE;
      }
   }

   public static Boolean toBooleanObject(Integer value, Integer trueValue, Integer falseValue, Integer nullValue) {
      if (value == null) {
         if (trueValue == null) {
            return Boolean.TRUE;
         }

         if (falseValue == null) {
            return Boolean.FALSE;
         }

         if (nullValue == null) {
            return null;
         }
      } else {
         if (value.equals(trueValue)) {
            return Boolean.TRUE;
         }

         if (value.equals(falseValue)) {
            return Boolean.FALSE;
         }

         if (value.equals(nullValue)) {
            return null;
         }
      }

      throw new IllegalArgumentException("The Integer did not match any specified value");
   }

   public static Boolean toBooleanObject(String str) {
      if (str == "true") {
         return Boolean.TRUE;
      } else if (str == null) {
         return null;
      } else {
         switch (str.length()) {
            case 1:
               char ch0 = str.charAt(0);
               if (ch0 == 'y' || ch0 == 'Y' || ch0 == 't' || ch0 == 'T' || ch0 == '1') {
                  return Boolean.TRUE;
               }

               if (ch0 == 'n' || ch0 == 'N' || ch0 == 'f' || ch0 == 'F' || ch0 == '0') {
                  return Boolean.FALSE;
               }
               break;
            case 2:
               char ch0xxxx = str.charAt(0);
               char ch1xxx = str.charAt(1);
               if ((ch0xxxx == 'o' || ch0xxxx == 'O') && (ch1xxx == 'n' || ch1xxx == 'N')) {
                  return Boolean.TRUE;
               }

               if ((ch0xxxx == 'n' || ch0xxxx == 'N') && (ch1xxx == 'o' || ch1xxx == 'O')) {
                  return Boolean.FALSE;
               }
               break;
            case 3:
               char ch0xxx = str.charAt(0);
               char ch1xx = str.charAt(1);
               char ch2xx = str.charAt(2);
               if ((ch0xxx == 'y' || ch0xxx == 'Y') && (ch1xx == 'e' || ch1xx == 'E') && (ch2xx == 's' || ch2xx == 'S')) {
                  return Boolean.TRUE;
               }

               if ((ch0xxx == 'o' || ch0xxx == 'O') && (ch1xx == 'f' || ch1xx == 'F') && (ch2xx == 'f' || ch2xx == 'F')) {
                  return Boolean.FALSE;
               }
               break;
            case 4:
               char ch0xx = str.charAt(0);
               char ch1x = str.charAt(1);
               char ch2x = str.charAt(2);
               char ch3x = str.charAt(3);
               if ((ch0xx == 't' || ch0xx == 'T') && (ch1x == 'r' || ch1x == 'R') && (ch2x == 'u' || ch2x == 'U') && (ch3x == 'e' || ch3x == 'E')) {
                  return Boolean.TRUE;
               }
               break;
            case 5:
               char ch0x = str.charAt(0);
               char ch1 = str.charAt(1);
               char ch2 = str.charAt(2);
               char ch3 = str.charAt(3);
               char ch4 = str.charAt(4);
               if ((ch0x == 'f' || ch0x == 'F')
                  && (ch1 == 'a' || ch1 == 'A')
                  && (ch2 == 'l' || ch2 == 'L')
                  && (ch3 == 's' || ch3 == 'S')
                  && (ch4 == 'e' || ch4 == 'E')) {
                  return Boolean.FALSE;
               }
         }

         return null;
      }
   }

   public static Boolean toBooleanObject(String str, String trueString, String falseString, String nullString) {
      if (str == null) {
         if (trueString == null) {
            return Boolean.TRUE;
         }

         if (falseString == null) {
            return Boolean.FALSE;
         }

         if (nullString == null) {
            return null;
         }
      } else {
         if (str.equals(trueString)) {
            return Boolean.TRUE;
         }

         if (str.equals(falseString)) {
            return Boolean.FALSE;
         }

         if (str.equals(nullString)) {
            return null;
         }
      }

      throw new IllegalArgumentException("The String did not match any specified value");
   }

   public static int toInteger(boolean bool) {
      return bool ? 1 : 0;
   }

   public static int toInteger(boolean bool, int trueValue, int falseValue) {
      return bool ? trueValue : falseValue;
   }

   public static int toInteger(Boolean bool, int trueValue, int falseValue, int nullValue) {
      if (bool == null) {
         return nullValue;
      } else {
         return bool ? trueValue : falseValue;
      }
   }

   public static Integer toIntegerObject(boolean bool) {
      return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
   }

   public static Integer toIntegerObject(boolean bool, Integer trueValue, Integer falseValue) {
      return bool ? trueValue : falseValue;
   }

   public static Integer toIntegerObject(Boolean bool) {
      if (bool == null) {
         return null;
      } else {
         return bool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
      }
   }

   public static Integer toIntegerObject(Boolean bool, Integer trueValue, Integer falseValue, Integer nullValue) {
      if (bool == null) {
         return nullValue;
      } else {
         return bool ? trueValue : falseValue;
      }
   }

   public static String toString(boolean bool, String trueString, String falseString) {
      return bool ? trueString : falseString;
   }

   public static String toString(Boolean bool, String trueString, String falseString, String nullString) {
      if (bool == null) {
         return nullString;
      } else {
         return bool ? trueString : falseString;
      }
   }

   public static String toStringOnOff(boolean bool) {
      return toString(bool, "on", "off");
   }

   public static String toStringOnOff(Boolean bool) {
      return toString(bool, "on", "off", null);
   }

   public static String toStringTrueFalse(boolean bool) {
      return toString(bool, "true", "false");
   }

   public static String toStringTrueFalse(Boolean bool) {
      return toString(bool, "true", "false", null);
   }

   public static String toStringYesNo(boolean bool) {
      return toString(bool, "yes", "no");
   }

   public static String toStringYesNo(Boolean bool) {
      return toString(bool, "yes", "no", null);
   }

   public static boolean xor(boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");
      boolean result = false;

      for (boolean element : array) {
         result ^= element;
      }

      return result;
   }

   public static Boolean xor(Boolean... array) {
      ObjectUtils.requireNonEmpty(array, "array");

      try {
         boolean[] primitive = ArrayUtils.toPrimitive(array);
         return xor(primitive) ? Boolean.TRUE : Boolean.FALSE;
      } catch (NullPointerException var2) {
         throw new IllegalArgumentException("The array must not contain any null elements");
      }
   }
}
