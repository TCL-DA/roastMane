package com.fasterxml.jackson.dataformat.yaml.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class StringQuotingChecker implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(
      Arrays.asList(
         "false",
         "False",
         "FALSE",
         "n",
         "N",
         "no",
         "No",
         "NO",
         "null",
         "Null",
         "NULL",
         "on",
         "On",
         "ON",
         "off",
         "Off",
         "OFF",
         "true",
         "True",
         "TRUE",
         "y",
         "Y",
         "yes",
         "Yes",
         "YES"
      )
   );

   public abstract boolean needToQuoteName(String var1);

   public abstract boolean needToQuoteValue(String var1);

   protected boolean isReservedKeyword(String value) {
      return value.length() == 0 ? true : this._isReservedKeyword(value.charAt(0), value);
   }

   protected boolean _isReservedKeyword(int firstChar, String name) {
      switch (firstChar) {
         case 70:
         case 78:
         case 79:
         case 84:
         case 89:
         case 102:
         case 110:
         case 111:
         case 116:
         case 121:
            return RESERVED_KEYWORDS.contains(name);
         case 126:
            return true;
         default:
            return false;
      }
   }

   protected boolean looksLikeYAMLNumber(String name) {
      return name.length() > 0 ? this._looksLikeYAMLNumber(name.charAt(0), name) : false;
   }

   protected boolean _looksLikeYAMLNumber(int firstChar, String name) {
      switch (firstChar) {
         case 43:
         case 45:
         case 46:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            return true;
         case 44:
         case 47:
         default:
            return false;
      }
   }

   protected boolean valueHasQuotableChar(String inputStr) {
      int end = inputStr.length();

      for (int i = 0; i < end; i++) {
         switch (inputStr.charAt(i)) {
            case '#':
               if (i > 0) {
                  char d = inputStr.charAt(i - 1);
                  if (' ' == d || '\t' == d) {
                     return true;
                  }
               }
               break;
            case ',':
            case '[':
            case ']':
            case '{':
            case '}':
               return true;
            case ':':
               if (i < end - 1) {
                  char d = inputStr.charAt(i + 1);
                  if (' ' == d || '\t' == d) {
                     return true;
                  }
               }
         }
      }

      return false;
   }

   protected boolean nameHasQuotableChar(String inputStr) {
      int end = inputStr.length();

      for (int i = 0; i < end; i++) {
         int ch = inputStr.charAt(i);
         if (ch < 32) {
            return true;
         }
      }

      return false;
   }

   public static class Default extends StringQuotingChecker implements Serializable {
      private static final long serialVersionUID = 1L;
      private static final StringQuotingChecker.Default INSTANCE = new StringQuotingChecker.Default();

      public static StringQuotingChecker.Default instance() {
         return INSTANCE;
      }

      @Override
      public boolean needToQuoteName(String name) {
         return this.isReservedKeyword(name) || this.looksLikeYAMLNumber(name) || this.nameHasQuotableChar(name);
      }

      @Override
      public boolean needToQuoteValue(String value) {
         return this.isReservedKeyword(value) || this.valueHasQuotableChar(value);
      }
   }
}
