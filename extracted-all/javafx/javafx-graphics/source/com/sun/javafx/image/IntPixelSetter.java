package com.sun.javafx.image;

import java.nio.IntBuffer;

public interface IntPixelSetter extends PixelSetter<IntBuffer> {
   void setArgb(int[] var1, int var2, int var3);

   void setArgbPre(int[] var1, int var2, int var3);
}
