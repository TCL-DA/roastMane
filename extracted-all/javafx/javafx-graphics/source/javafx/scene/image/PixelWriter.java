package javafx.scene.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.paint.Color;

public interface PixelWriter {
   PixelFormat getPixelFormat();

   void setArgb(int var1, int var2, int var3);

   void setColor(int var1, int var2, Color var3);

   <T extends Buffer> void setPixels(int var1, int var2, int var3, int var4, PixelFormat<T> var5, T var6, int var7);

   void setPixels(int var1, int var2, int var3, int var4, PixelFormat<ByteBuffer> var5, byte[] var6, int var7, int var8);

   void setPixels(int var1, int var2, int var3, int var4, PixelFormat<IntBuffer> var5, int[] var6, int var7, int var8);

   void setPixels(int var1, int var2, int var3, int var4, PixelReader var5, int var6, int var7);
}
