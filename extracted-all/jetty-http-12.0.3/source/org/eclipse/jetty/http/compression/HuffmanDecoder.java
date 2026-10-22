package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.util.CharsetStringBuilder.Iso88591StringBuilder;

public class HuffmanDecoder {
   private final Iso88591StringBuilder _builder = new Iso88591StringBuilder();
   private int _length = 0;
   private int _count = 0;
   private int _node = 0;
   private int _current = 0;
   private int _bits = 0;

   public void setLength(int length) {
      if (this._count != 0) {
         throw new IllegalStateException();
      } else {
         this._length = length;
      }
   }

   public String decode(ByteBuffer buffer) throws EncodingException {
      while (this._count < this._length) {
         if (!buffer.hasRemaining()) {
            return null;
         }

         int b = buffer.get() & 255;
         this._current = this._current << 8 | b;
         this._bits += 8;

         while (this._bits >= 8) {
            int i = this._current >>> this._bits - 8 & 0xFF;
            this._node = Huffman.tree[this._node * 256 + i];
            if (Huffman.rowbits[this._node] != 0) {
               if (Huffman.rowsym[this._node] == 256) {
                  this.reset();
                  throw new EncodingException("eos_in_content");
               }

               char c = Huffman.rowsym[this._node];
               c = HttpTokens.sanitizeFieldVchar(c);
               this._builder.append((byte)c);
               this._bits = this._bits - Huffman.rowbits[this._node];
               this._node = 0;
            } else {
               this._bits -= 8;
            }
         }

         this._count++;
      }

      while (this._bits > 0) {
         int i = this._current << 8 - this._bits & 0xFF;
         int lastNode = this._node;
         this._node = Huffman.tree[this._node * 256 + i];
         if (Huffman.rowbits[this._node] == 0 || Huffman.rowbits[this._node] > this._bits) {
            int requiredPadding = 0;

            for (int j = 0; j < this._bits; j++) {
               requiredPadding = requiredPadding << 1 | 1;
            }

            if (i >> 8 - this._bits != requiredPadding) {
               throw new EncodingException("incorrect_padding");
            }

            this._node = lastNode;
            break;
         }

         char c = Huffman.rowsym[this._node];
         c = HttpTokens.sanitizeFieldVchar(c);
         this._builder.append((byte)c);
         this._bits = this._bits - Huffman.rowbits[this._node];
         this._node = 0;
      }

      if (this._node != 0) {
         this.reset();
         throw new EncodingException("bad_termination");
      } else {
         String value = this._builder.build();
         this.reset();
         return value;
      }
   }

   public void reset() {
      this._builder.reset();
      this._count = 0;
      this._current = 0;
      this._node = 0;
      this._bits = 0;
   }
}
