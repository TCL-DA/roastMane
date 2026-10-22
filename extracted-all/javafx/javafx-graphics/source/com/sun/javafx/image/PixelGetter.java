package com.sun.javafx.image;

import java.nio.Buffer;

public interface PixelGetter<T extends Buffer> {
   AlphaType getAlphaType();

   int getNumElements();

   int getArgb(T var1, int var2);

   int getArgbPre(T var1, int var2);
}
