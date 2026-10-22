package com.sun.javafx.image;

import java.nio.ByteBuffer;

public interface BytePixelSetter extends PixelSetter<ByteBuffer> {
   void setArgb(byte[] var1, int var2, int var3);

   void setArgbPre(byte[] var1, int var2, int var3);
}
