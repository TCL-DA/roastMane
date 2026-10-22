package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.TriangleMesh;

public class TriangleMeshHelper extends MeshHelper {
   private static final TriangleMeshHelper theInstance = new TriangleMeshHelper();
   private static TriangleMeshHelper.TriangleMeshAccessor triangleMeshAccessor;

   private static TriangleMeshHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(TriangleMesh var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected boolean computeIntersectsImpl(Mesh var1, PickRay var2, PickResultChooser var3, Node var4, CullFace var5, boolean var6) {
      return triangleMeshAccessor.doComputeIntersects(var1, var2, var3, var4, var5, var6);
   }

   public static void setTriangleMeshAccessor(TriangleMeshHelper.TriangleMeshAccessor var0) {
      if (triangleMeshAccessor != null) {
         throw new IllegalStateException();
      } else {
         triangleMeshAccessor = var0;
      }
   }

   static {
      Utils.forceInit(TriangleMesh.class);
   }

   public interface TriangleMeshAccessor {
      boolean doComputeIntersects(Mesh var1, PickRay var2, PickResultChooser var3, Node var4, CullFace var5, boolean var6);
   }
}
