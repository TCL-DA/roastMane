package com.sun.javafx.image;

import java.nio.Buffer;

public interface PixelSetter<T extends Buffer> {
   AlphaType getAlphaType();

   int getNumElements();

   void setArgb(T var1, int var2, int var3);

   void setArgbPre(T var1, int var2, int var3);
}
