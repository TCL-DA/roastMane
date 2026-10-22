package com.fasterxml.jackson.core.io;

import java.util.Arrays;

public final class CharTypes {
   protected static final char[] HC = "0123456789ABCDEF".toCharArray();
   protected static final char[] HClower = "0123456789abcdef".toCharArray();
   protected static final byte[] HB;
   protected static final byte[] HBlower;
   protected static final int[] sInputCodes;
   protected static final int[] sInputCodesUTF8;
   protected static final int[] sInputCodesJsNames;
   protected static final int[] sInputCodesUtf8JsNames;
   protected static final int[] sInputCodesComment;
   protected static final int[] sInputCodesWS;
   protected static final int[] sOutputEscapes128;
   protected static final int[] sHexValues;

   public static int[] getInputCodeLatin1() {
      return sInputCodes;
   }

   public static int[] getInputCodeUtf8() {
      return sInputCodesUTF8;
   }

   public static int[] getInputCodeLatin1JsNames() {
      return sInputCodesJsNames;
   }

   public static int[] getInputCodeUtf8JsNames() {
      return sInputCodesUtf8JsNames;
   }

   public static int[] getInputCodeComment() {
      return sInputCodesComment;
   }

   public static int[] getInputCodeWS() {
      return sInputCodesWS;
   }

   public static int[] get7BitOutputEscapes() {
      return sOutputEscapes128;
   }

   public static int[] get7BitOutputEscapes(int quoteChar) {
      return quoteChar == 34 ? sOutputEscapes128 : CharTypes.AltEscapes.instance.escapesFor(quoteChar);
   }

   public static int charToHex(int ch) {
      return sHexValues[ch & 0xFF];
   }

   public static char hexToChar(int ch) {
      return HC[ch];
   }

   public static void appendQuoted(StringBuilder sb, String content) {
      int[] escCodes = sOutputEscapes128;
      int escLen = escCodes.length;
      int i = 0;

      for (int len = content.length(); i < len; i++) {
         char c = content.charAt(i);
         if (c < escLen && escCodes[c] != 0) {
            sb.append('\\');
            int escCode = escCodes[c];
            if (escCode < 0) {
               sb.append('u');
               sb.append('0');
               sb.append('0');
               sb.append(HC[c >> 4]);
               sb.append(HC[c & 15]);
            } else {
               sb.append((char)escCode);
            }
         } else {
            sb.append(c);
         }
      }
   }

   @Deprecated
   public static char[] copyHexChars() {
      return copyHexChars(true);
   }

   public static char[] copyHexChars(boolean uppercase) {
      return uppercase ? (char[])HC.clone() : (char[])HClower.clone();
   }

   @Deprecated
   public static byte[] copyHexBytes() {
      return copyHexBytes(true);
   }

   public static byte[] copyHexBytes(boolean uppercase) {
      return uppercase ? (byte[])HB.clone() : (byte[])HBlower.clone();
   }

   static {
      int len = HC.length;
      HB = new byte[len];
      HBlower = new byte[len];

      for (int i = 0; i < len; i++) {
         HB[i] = (byte)HC[i];
         HBlower[i] = (byte)HClower[i];
      }

      int[] table = new int[256];

      for (int i = 0; i < 32; i++) {
         table[i] = -1;
      }

      table[34] = 1;
      table[92] = 1;
      sInputCodes = table;
      int[] tablex = new int[sInputCodes.length];
      System.arraycopy(sInputCodes, 0, tablex, 0, tablex.length);

      for (int c = 128; c < 256; c++) {
         int code;
         if ((c & 224) == 192) {
            code = 2;
         } else if ((c & 240) == 224) {
            code = 3;
         } else if ((c & 248) == 240) {
            code = 4;
         } else {
            code = -1;
         }

         tablex[c] = code;
      }

      sInputCodesUTF8 = tablex;
      int[] tablexx = new int[256];
      Arrays.fill(tablexx, -1);

      for (int i = 33; i < 256; i++) {
         if (Character.isJavaIdentifierPart((char)i)) {
            tablexx[i] = 0;
         }
      }

      tablexx[64] = 0;
      tablexx[35] = 0;
      tablexx[42] = 0;
      tablexx[45] = 0;
      tablexx[43] = 0;
      sInputCodesJsNames = tablexx;
      int[] tablexxx = new int[256];
      System.arraycopy(sInputCodesJsNames, 0, tablexxx, 0, tablexxx.length);
      Arrays.fill(tablexxx, 128, 128, 0);
      sInputCodesUtf8JsNames = tablexxx;
      int[] buf = new int[256];
      System.arraycopy(sInputCodesUTF8, 128, buf, 128, 128);
      Arrays.fill(buf, 0, 32, -1);
      buf[9] = 0;
      buf[10] = 10;
      buf[13] = 13;
      buf[42] = 42;
      sInputCodesComment = buf;
      int[] bufx = new int[256];
      System.arraycopy(sInputCodesUTF8, 128, bufx, 128, 128);
      Arrays.fill(bufx, 0, 32, -1);
      bufx[32] = 1;
      bufx[9] = 1;
      bufx[10] = 10;
      bufx[13] = 13;
      bufx[47] = 47;
      bufx[35] = 35;
      sInputCodesWS = bufx;
      int[] tablexxxx = new int[128];

      for (int ix = 0; ix < 32; ix++) {
         tablexxxx[ix] = -1;
      }

      tablexxxx[34] = 34;
      tablexxxx[92] = 92;
      tablexxxx[8] = 98;
      tablexxxx[9] = 116;
      tablexxxx[12] = 102;
      tablexxxx[10] = 110;
      tablexxxx[13] = 114;
      sOutputEscapes128 = tablexxxx;
      sHexValues = new int[256];
      Arrays.fill(sHexValues, -1);
      len = 0;

      while (len < 10) {
         sHexValues[48 + len] = len++;
      }

      for (int ix = 0; ix < 6; ix++) {
         sHexValues[97 + ix] = 10 + ix;
         sHexValues[65 + ix] = 10 + ix;
      }
   }

   private static class AltEscapes {
      public static final CharTypes.AltEscapes instance = new CharTypes.AltEscapes();
      private int[][] _altEscapes = new int[128][];

      public int[] escapesFor(int quoteChar) {
         int[] esc = this._altEscapes[quoteChar];
         if (esc == null) {
            esc = Arrays.copyOf(CharTypes.sOutputEscapes128, 128);
            if (esc[quoteChar] == 0) {
               esc[quoteChar] = -1;
            }

            this._altEscapes[quoteChar] = esc;
         }

         return esc;
      }
   }
}
