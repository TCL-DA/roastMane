package com.fasterxml.jackson.core.io.doubleparser;

abstract class AbstractFloatValueParser {
   static final long PARSE_ERROR = -1L;
   static final long MINIMAL_NINETEEN_DIGIT_INTEGER = 1000000000000000000L;
   static final int MAX_EXPONENT_NUMBER = 1024;
   static final byte DECIMAL_POINT_CLASS = -4;
   static final byte OTHER_CLASS = -1;
   static final byte[] CHAR_TO_HEX_MAP = new byte[128];

   static {
      for (char ch = 0; ch < CHAR_TO_HEX_MAP.length; ch++) {
         CHAR_TO_HEX_MAP[ch] = -1;
      }

      for (char ch = '0'; ch <= '9'; ch++) {
         CHAR_TO_HEX_MAP[ch] = (byte)(ch - '0');
      }

      for (char ch = 'A'; ch <= 'F'; ch++) {
         CHAR_TO_HEX_MAP[ch] = (byte)(ch - 'A' + 10);
      }

      for (char ch = 'a'; ch <= 'f'; ch++) {
         CHAR_TO_HEX_MAP[ch] = (byte)(ch - 'a' + 10);
      }

      for (char ch = '.'; ch <= '.'; ch++) {
         CHAR_TO_HEX_MAP[ch] = -4;
      }
   }
}
