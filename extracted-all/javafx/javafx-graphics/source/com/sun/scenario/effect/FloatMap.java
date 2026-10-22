package com.sun.scenario.effect;

import com.sun.scenario.effect.impl.Renderer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class FloatMap {
   private final int width;
   private final int height;
   private final FloatBuffer buf;
   private boolean cacheValid;
   private Map<FilterContext, FloatMap.Entry> cache;

   public FloatMap(int var1, int var2) {
      if (var1 > 0 && var1 <= 4096 && var2 > 0 && var2 <= 4096) {
         this.width = var1;
         this.height = var2;
         int var3 = var1 * var2 * 4;
         this.buf = FloatBuffer.wrap(new float[var3]);
      } else {
         throw new IllegalArgumentException("Width and height must be in the range [1, 4096]");
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float[] getData() {
      return this.buf.array();
   }

   public FloatBuffer getBuffer() {
      return this.buf;
   }

   public float getSample(int var1, int var2, int var3) {
      return this.buf.get((var1 + var2 * this.width) * 4 + var3);
   }

   public void setSample(int var1, int var2, int var3, float var4) {
      this.buf.put((var1 + var2 * this.width) * 4 + var3, var4);
      this.cacheValid = false;
   }

   public void setSamples(int var1, int var2, float var3) {
      int var4 = (var1 + var2 * this.width) * 4;
      this.buf.put(var4 + 0, var3);
      this.cacheValid = false;
   }

   public void setSamples(int var1, int var2, float var3, float var4) {
      int var5 = (var1 + var2 * this.width) * 4;
      this.buf.put(var5 + 0, var3);
      this.buf.put(var5 + 1, var4);
      this.cacheValid = false;
   }

   public void setSamples(int var1, int var2, float var3, float var4, float var5) {
      int var6 = (var1 + var2 * this.width) * 4;
      this.buf.put(var6 + 0, var3);
      this.buf.put(var6 + 1, var4);
      this.buf.put(var6 + 2, var5);
      this.cacheValid = false;
   }

   public void setSamples(int var1, int var2, float var3, float var4, float var5, float var6) {
      int var7 = (var1 + var2 * this.width) * 4;
      this.buf.put(var7 + 0, var3);
      this.buf.put(var7 + 1, var4);
      this.buf.put(var7 + 2, var5);
      this.buf.put(var7 + 3, var6);
      this.cacheValid = false;
   }

   public void put(float[] var1) {
      this.buf.rewind();
      this.buf.put(var1);
      this.buf.rewind();
      this.cacheValid = false;
   }

   public LockableResource getAccelData(FilterContext var1) {
      if (this.cache == null) {
         this.cache = new HashMap<>();
      } else if (!this.cacheValid) {
         for (FloatMap.Entry var3 : this.cache.values()) {
            var3.valid = false;
         }

         this.cacheValid = true;
      }

      Renderer var5 = Renderer.getRenderer(var1);
      FloatMap.Entry var6 = this.cache.get(var1);
      if (var6 != null) {
         var6.texture.lock();
         if (var6.texture.isLost()) {
            var6.texture.unlock();
            this.cache.remove(var1);
            var6 = null;
         }
      }

      if (var6 == null) {
         LockableResource var4 = var5.createFloatTexture(this.width, this.height);
         var6 = new FloatMap.Entry(var4);
         this.cache.put(var1, var6);
      }

      if (!var6.valid) {
         var5.updateFloatTexture(var6.texture, this);
         var6.valid = true;
      }

      return var6.texture;
   }

   private static class Entry {
      LockableResource texture;
      boolean valid;

      Entry(LockableResource var1) {
         this.texture = var1;
      }
   }
}
