package javafx.scene.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.paint.Color;

public interface PixelReader {
   PixelFormat getPixelFormat();

   int getArgb(int var1, int var2);

   Color getColor(int var1, int var2);

   <T extends Buffer> void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<T> var5, T var6, int var7);

   void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<ByteBuffer> var5, byte[] var6, int var7, int var8);

   void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<IntBuffer> var5, int[] var6, int var7, int var8);
}
