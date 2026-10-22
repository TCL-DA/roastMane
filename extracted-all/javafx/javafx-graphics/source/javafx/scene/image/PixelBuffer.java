package javafx.scene.image;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Rectangle2D;
import javafx.util.Callback;

public class PixelBuffer<T extends Buffer> {
   private final T buffer;
   private final int width;
   private final int height;
   private final PixelFormat<T> pixelFormat;
   private final List<WeakReference<WritableImage>> imageRefs;

   public PixelBuffer(int var1, int var2, T var3, PixelFormat<T> var4) {
      Objects.requireNonNull(var3, "buffer must not be null.");
      Objects.requireNonNull(var4, "pixelFormat must not be null.");
      if (var1 > 0 && var2 > 0) {
         switch (var4.getType()) {
            case BYTE_BGRA_PRE:
               if (var3.capacity() / var1 / 4 < var2) {
                  throw new IllegalArgumentException("Insufficient memory allocated for ByteBuffer.");
               }

               if (!(var3 instanceof ByteBuffer)) {
                  throw new IllegalArgumentException("PixelFormat<ByteBuffer> requires a ByteBuffer.");
               }
               break;
            case INT_ARGB_PRE:
               if (var3.capacity() / var1 < var2) {
                  throw new IllegalArgumentException("Insufficient memory allocated for IntBuffer.");
               }

               if (!(var3 instanceof IntBuffer)) {
                  throw new IllegalArgumentException("PixelFormat<IntBuffer> requires an IntBuffer.");
               }
               break;
            default:
               throw new IllegalArgumentException("Unsupported PixelFormat: " + var4.getType());
         }

         this.buffer = (T)var3;
         this.width = var1;
         this.height = var2;
         this.pixelFormat = var4;
         this.imageRefs = new LinkedList<>();
      } else {
         throw new IllegalArgumentException("PixelBuffer dimensions must be positive (w,h > 0)");
      }
   }

   public T getBuffer() {
      return this.buffer;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public PixelFormat<T> getPixelFormat() {
      return this.pixelFormat;
   }

   public void updateBuffer(Callback<PixelBuffer<T>, Rectangle2D> var1) {
      Toolkit.getToolkit().checkFxUserThread();
      Objects.requireNonNull(var1, "callback must not be null.");
      Rectangle2D var2 = (Rectangle2D)var1.call(this);
      if (var2 != null) {
         if (var2.getWidth() > 0.0 && var2.getHeight() > 0.0) {
            int var3 = (int)Math.floor(var2.getMinX());
            int var4 = (int)Math.floor(var2.getMinY());
            int var5 = (int)Math.ceil(var2.getMaxX());
            int var6 = (int)Math.ceil(var2.getMaxY());
            this.bufferDirty(new Rectangle(var3, var4, var5 - var3, var6 - var4));
         }
      } else {
         this.bufferDirty(null);
      }
   }

   private void bufferDirty(Rectangle var1) {
      Iterator var2 = this.imageRefs.iterator();

      while (var2.hasNext()) {
         WritableImage var3 = (WritableImage)((WeakReference)var2.next()).get();
         if (var3 != null) {
            var3.bufferDirty(var1);
         } else {
            var2.remove();
         }
      }
   }

   void addImage(WritableImage var1) {
      this.imageRefs.add(new WeakReference<>(var1));
      this.imageRefs.removeIf(var0 -> var0.get() == null);
   }
}
