package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;

public abstract class MeshHelper {
   private static MeshHelper.MeshAccessor meshAccessor;

   protected MeshHelper() {
   }

   private static MeshHelper getHelper(Mesh var0) {
      return meshAccessor.getHelper(var0);
   }

   protected static void setHelper(Mesh var0, MeshHelper var1) {
      meshAccessor.setHelper(var0, var1);
   }

   public static boolean computeIntersects(Mesh var0, PickRay var1, PickResultChooser var2, Node var3, CullFace var4, boolean var5) {
      return getHelper(var0).computeIntersectsImpl(var0, var1, var2, var3, var4, var5);
   }

   protected abstract boolean computeIntersectsImpl(Mesh var1, PickRay var2, PickResultChooser var3, Node var4, CullFace var5, boolean var6);

   public static void setMeshAccessor(MeshHelper.MeshAccessor var0) {
      if (meshAccessor != null) {
         throw new IllegalStateException();
      } else {
         meshAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Mesh.class);
   }

   public interface MeshAccessor {
      MeshHelper getHelper(Mesh var1);

      void setHelper(Mesh var1, MeshHelper var2);
   }
}
