package javafx.scene.shape;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

final class PredefinedMeshManager {
   private static final int INITAL_CAPACITY = 17;
   private static final float LOAD_FACTOR = 0.75F;
   private static final PredefinedMeshManager INSTANCE = new PredefinedMeshManager();
   private PredefinedMeshManager.TriangleMeshCache boxCache = null;
   private PredefinedMeshManager.TriangleMeshCache sphereCache = null;
   private PredefinedMeshManager.TriangleMeshCache cylinderCache = null;

   private PredefinedMeshManager() {
   }

   static PredefinedMeshManager getInstance() {
      return INSTANCE;
   }

   synchronized TriangleMesh getBoxMesh(float var1, float var2, float var3, Shape3D.Key var4) {
      if (this.boxCache == null) {
         this.boxCache = PredefinedMeshManager.BoxCacheLoader.INSTANCE;
      }

      TriangleMesh var5 = this.boxCache.get(var4);
      if (var5 == null) {
         var5 = Box.createMesh(var1, var2, var3);
         this.boxCache.put(var4, var5);
      } else {
         var5.incRef();
      }

      return var5;
   }

   synchronized TriangleMesh getSphereMesh(float var1, int var2, Shape3D.Key var3) {
      if (this.sphereCache == null) {
         this.sphereCache = PredefinedMeshManager.SphereCacheLoader.INSTANCE;
      }

      TriangleMesh var4 = this.sphereCache.get(var3);
      if (var4 == null) {
         var4 = Sphere.createMesh(var2, var1);
         this.sphereCache.put(var3, var4);
      } else {
         var4.incRef();
      }

      return var4;
   }

   synchronized TriangleMesh getCylinderMesh(float var1, float var2, int var3, Shape3D.Key var4) {
      if (this.cylinderCache == null) {
         this.cylinderCache = PredefinedMeshManager.CylinderCacheLoader.INSTANCE;
      }

      TriangleMesh var5 = this.cylinderCache.get(var4);
      if (var5 == null) {
         var5 = Cylinder.createMesh(var3, var1, var2);
         this.cylinderCache.put(var4, var5);
      } else {
         var5.incRef();
      }

      return var5;
   }

   synchronized void invalidateBoxMesh(Shape3D.Key var1) {
      if (this.boxCache != null) {
         this.boxCache.invalidateMesh(var1);
      }
   }

   synchronized void invalidateSphereMesh(Shape3D.Key var1) {
      if (this.sphereCache != null) {
         this.sphereCache.invalidateMesh(var1);
      }
   }

   synchronized void invalidateCylinderMesh(Shape3D.Key var1) {
      if (this.cylinderCache != null) {
         this.cylinderCache.invalidateMesh(var1);
      }
   }

   synchronized void dispose() {
      if (this.boxCache != null) {
         this.boxCache.clear();
      }

      if (this.sphereCache != null) {
         this.sphereCache.clear();
      }

      if (this.cylinderCache != null) {
         this.cylinderCache.clear();
      }
   }

   synchronized void printStats() {
      if (this.boxCache != null) {
         this.boxCache.printStats("BoxCache");
      }

      if (this.sphereCache != null) {
         this.sphereCache.printStats("SphereCache");
      }

      if (this.cylinderCache != null) {
         this.cylinderCache.printStats("CylinderCache");
      }
   }

   void test_clearCaches() {
      INSTANCE.dispose();
   }

   int test_getBoxCacheSize() {
      return INSTANCE.boxCache.size();
   }

   int test_getSphereCacheSize() {
      return INSTANCE.sphereCache.size();
   }

   int test_getCylinderCacheSize() {
      return INSTANCE.cylinderCache.size();
   }

   private static final class BoxCacheLoader {
      private static final PredefinedMeshManager.TriangleMeshCache INSTANCE = new PredefinedMeshManager.TriangleMeshCache();
   }

   private static final class CylinderCacheLoader {
      private static final PredefinedMeshManager.TriangleMeshCache INSTANCE = new PredefinedMeshManager.TriangleMeshCache();
   }

   private static final class SphereCacheLoader {
      private static final PredefinedMeshManager.TriangleMeshCache INSTANCE = new PredefinedMeshManager.TriangleMeshCache();
   }

   private static class TriangleMeshCache {
      Map<Shape3D.Key, SoftReference<TriangleMesh>> cache = new HashMap<>(17, 0.75F);

      private TriangleMesh get(Shape3D.Key var1) {
         this.cleanCache();
         return this.cache.containsKey(var1) ? this.cache.get(var1).get() : null;
      }

      private void put(Shape3D.Key var1, TriangleMesh var2) {
         this.cleanCache();
         if (var2 != null) {
            this.cache.put(var1, new SoftReference<>(var2));
         }
      }

      private void cleanCache() {
         this.cache.values().removeIf(var0 -> var0.get() == null);
      }

      private void clear() {
         this.cache.clear();
      }

      private int size() {
         this.cleanCache();
         return this.cache.size();
      }

      private void printStats(String var1) {
         System.out.println(var1 + " size:    " + this.size());
      }

      private void invalidateMesh(Shape3D.Key var1) {
         if (this.cache.containsKey(var1)) {
            TriangleMesh var2 = this.cache.get(var1).get();
            if (var2 != null) {
               var2.decRef();
               int var3 = var2.getRefCount();
               if (var3 == 0) {
                  this.cache.remove(var1);
               }
            } else {
               this.cache.remove(var1);
            }
         }
      }
   }
}
