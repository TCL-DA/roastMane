package com.fasterxml.jackson.core.io.doubleparser;

abstract class AbstractFloatingPointBitsFromCharArray extends AbstractFloatValueParser {
   private static boolean isDigit(char c) {
      return '0' <= c && c <= '9';
   }

   private int skipWhitespace(char[] str, int index, int endIndex) {
      while (index < endIndex && (str[index] & 255) <= 32) {
         index++;
      }

      return index;
   }

   private long parseDecFloatLiteral(char[] str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
      long significand = 0L;
      int significandStartIndex = index;
      int virtualIndexOfPoint = -1;
      boolean illegal = false;

      char ch;
      for (ch = '\u0000'; index < endIndex; index++) {
         ch = str[index];
         if (isDigit(ch)) {
            significand = 10L * significand + (long)ch - 48L;
         } else {
            if (ch != '.') {
               break;
            }

            illegal |= virtualIndexOfPoint >= 0;

            for (virtualIndexOfPoint = index; index < endIndex - 8; index += 8) {
               int eightDigits = this.tryToParseEightDigits(str, index + 1);
               if (eightDigits < 0) {
                  break;
               }

               significand = 100000000L * significand + (long)eightDigits;
            }
         }
      }

      int significandEndIndex = index;
      int exponent;
      int digitCount;
      if (virtualIndexOfPoint < 0) {
         digitCount = index - significandStartIndex;
         virtualIndexOfPoint = index;
         exponent = 0;
      } else {
         digitCount = index - significandStartIndex - 1;
         exponent = virtualIndexOfPoint - index + 1;
      }

      int expNumber = 0;
      if (ch == 'e' || ch == 'E') {
         index++;
         ch = index < endIndex ? str[index] : 0;
         boolean neg_exp = ch == '-';
         if (neg_exp || ch == '+') {
            index++;
            ch = index < endIndex ? str[index] : 0;
         }

         illegal |= !isDigit(ch);

         do {
            if (expNumber < 1024) {
               expNumber = 10 * expNumber + ch - 48;
            }

            index++;
            ch = index < endIndex ? str[index] : 0;
         } while (isDigit(ch));

         if (neg_exp) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      }

      if (index < endIndex && (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F')) {
         index++;
      }

      index = this.skipWhitespace(str, index, endIndex);
      if (!illegal && index >= endIndex && (hasLeadingZero || digitCount != 0)) {
         int skipCountInTruncatedDigits = 0;
         int exponentOfTruncatedSignificand;
         boolean isSignificandTruncated;
         if (digitCount > 19) {
            significand = 0L;

            for (index = significandStartIndex; index < significandEndIndex; index++) {
               ch = str[index];
               if (ch == '.') {
                  skipCountInTruncatedDigits++;
               } else {
                  if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                     break;
                  }

                  significand = 10L * significand + (long)ch - 48L;
               }
            }

            isSignificandTruncated = index < significandEndIndex;
            exponentOfTruncatedSignificand = virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber;
         } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
         }

         return this.valueOfFloatLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      } else {
         return -1L;
      }
   }

   public long parseFloatingPointLiteral(char[] str, int offset, int length) {
      int endIndex = offset + length;
      if (offset >= 0 && endIndex <= str.length) {
         int index = this.skipWhitespace(str, offset, endIndex);
         if (index == endIndex) {
            return -1L;
         } else {
            char ch = str[index];
            boolean isNegative = ch == '-';
            if (isNegative || ch == '+') {
               index++;
               ch = index < endIndex ? str[index] : 0;
               if (ch == 0) {
                  return -1L;
               }
            }

            if (ch >= 'I') {
               return ch == 'N' ? this.parseNaN(str, index, endIndex) : this.parseInfinity(str, index, endIndex, isNegative);
            } else {
               boolean hasLeadingZero = ch == '0';
               if (hasLeadingZero) {
                  index++;
                  ch = index < endIndex ? str[index] : 0;
                  if (ch == 'x' || ch == 'X') {
                     return this.parseHexFloatLiteral(str, index + 1, offset, endIndex, isNegative);
                  }
               }

               return this.parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
            }
         }
      } else {
         return -1L;
      }
   }

   private long parseHexFloatLiteral(char[] str, int index, int startIndex, int endIndex, boolean isNegative) {
      long significand = 0L;
      int exponent = 0;
      int significandStartIndex = index;
      int virtualIndexOfPoint = -1;
      boolean illegal = false;

      char ch;
      for (ch = '\u0000'; index < endIndex; index++) {
         ch = str[index];
         int hexValue = ch > 127 ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
         if (hexValue >= 0) {
            significand = significand << 4 | (long)hexValue;
         } else {
            if (hexValue != -4) {
               break;
            }

            illegal |= virtualIndexOfPoint >= 0;
            virtualIndexOfPoint = index;
         }
      }

      int significandEndIndex = index;
      int digitCount;
      if (virtualIndexOfPoint < 0) {
         digitCount = index - significandStartIndex;
         virtualIndexOfPoint = index;
      } else {
         digitCount = index - significandStartIndex - 1;
         exponent = Math.min(virtualIndexOfPoint - index + 1, 1024) * 4;
      }

      int expNumber = 0;
      boolean hasExponent = ch == 'p' || ch == 'P';
      if (hasExponent) {
         index++;
         ch = index < endIndex ? str[index] : 0;
         boolean neg_exp = ch == '-';
         if (neg_exp || ch == '+') {
            index++;
            ch = index < endIndex ? str[index] : 0;
         }

         illegal |= !isDigit(ch);

         do {
            if (expNumber < 1024) {
               expNumber = 10 * expNumber + ch - 48;
            }

            index++;
            ch = index < endIndex ? str[index] : 0;
         } while (isDigit(ch));

         if (neg_exp) {
            expNumber = -expNumber;
         }

         exponent += expNumber;
      }

      if (index < endIndex && (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F')) {
         index++;
      }

      index = this.skipWhitespace(str, index, endIndex);
      if (!illegal && index >= endIndex && digitCount != 0 && hasExponent) {
         int skipCountInTruncatedDigits = 0;
         boolean isSignificandTruncated;
         if (digitCount > 16) {
            significand = 0L;

            for (index = significandStartIndex; index < significandEndIndex; index++) {
               ch = str[index];
               int hexValue = ch > 127 ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
               if (hexValue >= 0) {
                  if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                     break;
                  }

                  significand = significand << 4 | (long)hexValue;
               } else {
                  skipCountInTruncatedDigits++;
               }
            }

            isSignificandTruncated = index < significandEndIndex;
         } else {
            isSignificandTruncated = false;
         }

         return this.valueOfHexLiteral(
            str,
            startIndex,
            endIndex,
            isNegative,
            significand,
            exponent,
            isSignificandTruncated,
            virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber
         );
      } else {
         return -1L;
      }
   }

   private long parseInfinity(char[] str, int index, int endIndex, boolean negative) {
      if (index + 7 < endIndex
         && str[index] == 'I'
         && str[index + 1] == 'n'
         && str[index + 2] == 'f'
         && str[index + 3] == 'i'
         && str[index + 4] == 'n'
         && str[index + 5] == 'i'
         && str[index + 6] == 't'
         && str[index + 7] == 'y') {
         index = this.skipWhitespace(str, index + 8, endIndex);
         if (index == endIndex) {
            return negative ? this.negativeInfinity() : this.positiveInfinity();
         }
      }

      return -1L;
   }

   private long parseNaN(char[] str, int index, int endIndex) {
      if (index + 2 < endIndex && str[index + 1] == 'a' && str[index + 2] == 'N') {
         index = this.skipWhitespace(str, index + 3, endIndex);
         if (index == endIndex) {
            return this.nan();
         }
      }

      return -1L;
   }

   private int tryToParseEightDigits(char[] str, int offset) {
      return FastDoubleSwar.tryToParseEightDigitsUtf16(str, offset);
   }

   abstract long nan();

   abstract long negativeInfinity();

   abstract long positiveInfinity();

   abstract long valueOfFloatLiteral(char[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

   abstract long valueOfHexLiteral(char[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}
