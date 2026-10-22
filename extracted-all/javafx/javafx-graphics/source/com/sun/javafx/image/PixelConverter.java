package com.sun.javafx.image;

import java.nio.Buffer;

public interface PixelConverter<T extends Buffer, U extends Buffer> {
   void convert(T var1, int var2, int var3, U var4, int var5, int var6, int var7, int var8);

   PixelGetter<T> getGetter();

   PixelSetter<U> getSetter();
}
