package nonapi.io.github.classgraph.utils;

public final class StringUtils {
   private StringUtils() {
   }

   public static String readString(byte[] arr, int startOffset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IllegalArgumentException {
      if ((long)startOffset >= 0L && numBytes >= 0 && startOffset + numBytes <= arr.length) {
         char[] chars = new char[numBytes];
         int byteIdx = 0;

         int charIdx;
         for (charIdx = 0; byteIdx < numBytes; byteIdx++) {
            int c = arr[startOffset + byteIdx] & 255;
            if (c > 127) {
               break;
            }

            chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
         }

         while (byteIdx < numBytes) {
            int c = arr[startOffset + byteIdx] & 255;
            switch (c >> 4) {
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
                  byteIdx++;
                  chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
                  break;
               case 8:
               case 9:
               case 10:
               case 11:
               default:
                  throw new IllegalArgumentException("Bad modified UTF8");
               case 12:
               case 13:
                  byteIdx += 2;
                  if (byteIdx > numBytes) {
                     throw new IllegalArgumentException("Bad modified UTF8");
                  }

                  int c2 = arr[startOffset + byteIdx - 1];
                  if ((c2 & 192) != 128) {
                     throw new IllegalArgumentException("Bad modified UTF8");
                  }

                  int c3 = (c & 31) << 6 | c2 & 63;
                  chars[charIdx++] = (char)(replaceSlashWithDot && c3 == 47 ? 46 : c3);
                  break;
               case 14:
                  byteIdx += 3;
                  if (byteIdx > numBytes) {
                     throw new IllegalArgumentException("Bad modified UTF8");
                  }

                  int c2 = arr[startOffset + byteIdx - 2];
                  int c3 = arr[startOffset + byteIdx - 1];
                  if ((c2 & 192) != 128 || (c3 & 192) != 128) {
                     throw new IllegalArgumentException("Bad modified UTF8");
                  }

                  int c4 = (c & 15) << 12 | (c2 & 63) << 6 | c3 & 63;
                  chars[charIdx++] = (char)(replaceSlashWithDot && c4 == 47 ? 46 : c4);
            }
         }

         if (charIdx == numBytes && !stripLSemicolon) {
            return new String(chars);
         } else if (!stripLSemicolon) {
            return new String(chars, 0, charIdx);
         } else if (charIdx >= 2 && chars[0] == 'L' && chars[charIdx - 1] == ';') {
            return new String(chars, 1, charIdx - 2);
         } else {
            throw new IllegalArgumentException("Expected string to start with 'L' and end with ';', got \"" + new String(chars) + "\"");
         }
      } else {
         throw new IllegalArgumentException("offset or numBytes out of range");
      }
   }

   public static void join(StringBuilder buf, String addAtBeginning, String sep, String addAtEnd, Iterable<?> iterable) {
      if (!addAtBeginning.isEmpty()) {
         buf.append(addAtBeginning);
      }

      boolean first = true;

      for (Object item : iterable) {
         if (first) {
            first = false;
         } else {
            buf.append(sep);
         }

         buf.append(item == null ? "null" : item.toString());
      }

      if (!addAtEnd.isEmpty()) {
         buf.append(addAtEnd);
      }
   }

   public static String join(String sep, Iterable<?> iterable) {
      StringBuilder buf = new StringBuilder();
      join(buf, "", sep, "", iterable);
      return buf.toString();
   }

   public static String join(String sep, Object... items) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;

      for (Object item : items) {
         if (first) {
            first = false;
         } else {
            buf.append(sep);
         }

         buf.append(item.toString());
      }

      return buf.toString();
   }
}
