package com.sun.webkit.text;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

final class TextCodec {
   private final Charset charset;
   private static final Map<String, String> RE_MAP = Map.of("ISO-10646-UCS-2", "UTF-16");

   private TextCodec(String var1) {
      this.charset = Charset.forName(var1);
   }

   private byte[] encode(char[] var1) {
      ByteBuffer var2 = this.charset.encode(CharBuffer.wrap(var1));
      byte[] var3 = new byte[var2.remaining()];
      var2.get(var3);
      return var3;
   }

   private String decode(byte[] var1) {
      CharBuffer var2 = this.charset.decode(ByteBuffer.wrap(var1));
      char[] var3 = new char[var2.remaining()];
      var2.get(var3);
      return new String(var3);
   }

   private static String[] getEncodings() {
      ArrayList var0 = new ArrayList();
      SortedMap var1 = Charset.availableCharsets();

      for (Entry var3 : var1.entrySet()) {
         String var4 = (String)var3.getKey();
         var0.add(var4);
         var0.add(var4);
         Charset var5 = (Charset)var3.getValue();

         for (String var7 : var5.aliases()) {
            if (!var7.equals("8859_1")) {
               var0.add(var7);
               String var8 = RE_MAP.get(var7);
               var0.add(var8 == null ? var4 : var8);
            }
         }
      }

      return var0.toArray(new String[0]);
   }
}
