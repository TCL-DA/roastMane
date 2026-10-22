package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

public class MeshViewHelper extends Shape3DHelper {
   private static final MeshViewHelper theInstance = new MeshViewHelper();
   private static MeshViewHelper.MeshViewAccessor meshViewAccessor;

   private static MeshViewHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(MeshView var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return meshViewAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      meshViewAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return meshViewAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return meshViewAccessor.doComputeContains(var1, var2, var4);
   }

   @Override
   protected boolean computeIntersectsImpl(Node var1, PickRay var2, PickResultChooser var3) {
      return meshViewAccessor.doComputeIntersects(var1, var2, var3);
   }

   public static void setMeshViewAccessor(MeshViewHelper.MeshViewAccessor var0) {
      if (meshViewAccessor != null) {
         throw new IllegalStateException();
      } else {
         meshViewAccessor = var0;
      }
   }

   static {
      Utils.forceInit(MeshView.class);
   }

   public interface MeshViewAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3);
   }
}
