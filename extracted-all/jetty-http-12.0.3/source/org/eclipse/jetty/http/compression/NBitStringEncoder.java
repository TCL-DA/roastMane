package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpTokens;

public class NBitStringEncoder {
   private NBitStringEncoder() {
   }

   public static int octetsNeeded(int prefix, String value, boolean huffman) {
      if (prefix > 0 && prefix <= 8) {
         int contentPrefix = prefix == 1 ? 8 : prefix - 1;
         int encodedValueSize = huffman ? HuffmanEncoder.octetsNeeded(value) : value.length();
         int encodedLengthSize = NBitIntegerEncoder.octetsNeeded(contentPrefix, (long)encodedValueSize);
         return encodedLengthSize + encodedValueSize + (prefix == 1 ? 1 : 0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void encode(ByteBuffer buffer, int prefix, String value, boolean huffman) {
      if (prefix > 0 && prefix <= 8) {
         byte huffmanFlag = huffman ? (byte)(1 << prefix - 1) : 0;
         if (prefix == 8) {
            buffer.put(huffmanFlag);
         } else {
            int p = buffer.position() - 1;
            buffer.put(p, (byte)(buffer.get(p) | huffmanFlag));
         }

         prefix = prefix == 1 ? 8 : prefix - 1;
         if (huffman) {
            int encodedValueSize = HuffmanEncoder.octetsNeeded(value);
            NBitIntegerEncoder.encode(buffer, prefix, (long)encodedValueSize);
            HuffmanEncoder.encode(buffer, value);
         } else {
            int encodedValueSize = value.length();
            NBitIntegerEncoder.encode(buffer, prefix, (long)encodedValueSize);

            for (int i = 0; i < encodedValueSize; i++) {
               char c = value.charAt(i);
               c = HttpTokens.sanitizeFieldVchar(c);
               buffer.put((byte)c);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
