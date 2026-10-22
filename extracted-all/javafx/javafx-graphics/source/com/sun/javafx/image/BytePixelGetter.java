package com.sun.javafx.image;

import java.nio.ByteBuffer;

public interface BytePixelGetter extends PixelGetter<ByteBuffer> {
   int getArgb(byte[] var1, int var2);

   int getArgbPre(byte[] var1, int var2);
}
