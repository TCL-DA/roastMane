package com.sun.javafx.image;

import java.nio.IntBuffer;

public interface IntPixelGetter extends PixelGetter<IntBuffer> {
   int getArgb(int[] var1, int var2);

   int getArgbPre(int[] var1, int var2);
}
