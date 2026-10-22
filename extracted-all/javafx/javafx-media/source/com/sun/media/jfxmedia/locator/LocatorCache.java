package com.sun.media.jfxmedia.locator;

import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.MediaDisposer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class LocatorCache {
   private final Map<URI, WeakReference<LocatorCache.CacheReference>> uriCache = new HashMap<>();
   private final LocatorCache.CacheDisposer cacheDisposer = new LocatorCache.CacheDisposer();

   public static LocatorCache locatorCache() {
      return LocatorCache.CacheInitializer.globalInstance;
   }

   private LocatorCache() {
   }

   public LocatorCache.CacheReference registerURICache(URI var1, ByteBuffer var2, String var3) {
      if (Logger.canLog(1)) {
         Logger.logMsg(1, "New cache entry: URI " + var1 + ", buffer " + var2 + ", MIME type " + var3);
      }

      if (!var2.isDirect()) {
         var2.rewind();
         ByteBuffer var4 = ByteBuffer.allocateDirect(var2.capacity());
         var4.put(var2);
         var2 = var4;
      }

      LocatorCache.CacheReference var8 = new LocatorCache.CacheReference(var2, var3);
      synchronized (this.uriCache) {
         this.uriCache.put(var1, new WeakReference<>(var8));
      }

      MediaDisposer.addResourceDisposer(var8, var1, this.cacheDisposer);
      return var8;
   }

   public LocatorCache.CacheReference fetchURICache(URI var1) {
      synchronized (this.uriCache) {
         WeakReference var3 = this.uriCache.get(var1);
         if (null == var3) {
            return null;
         } else {
            LocatorCache.CacheReference var4 = (LocatorCache.CacheReference)var3.get();
            if (null != var4) {
               if (Logger.canLog(1)) {
                  Logger.logMsg(1, "Fetched cache entry: URI " + var1 + ", buffer " + var4.getBuffer() + ", MIME type " + var4.getMIMEType());
               }

               return var4;
            } else {
               return null;
            }
         }
      }
   }

   public boolean isCached(URI var1) {
      synchronized (this.uriCache) {
         return this.uriCache.containsKey(var1);
      }
   }

   private class CacheDisposer implements MediaDisposer.ResourceDisposer {
      @Override
      public void disposeResource(Object var1) {
         if (var1 instanceof URI) {
            synchronized (LocatorCache.this.uriCache) {
               LocatorCache.this.uriCache.remove((URI)var1);
            }
         }
      }
   }

   private static class CacheInitializer {
      private static final LocatorCache globalInstance = new LocatorCache();
   }

   public static class CacheReference {
      private final ByteBuffer buffer;
      private String mimeType;

      public CacheReference(ByteBuffer var1, String var2) {
         this.buffer = var1;
         this.mimeType = var2;
      }

      public ByteBuffer getBuffer() {
         return this.buffer;
      }

      public String getMIMEType() {
         return this.mimeType;
      }
   }
}
