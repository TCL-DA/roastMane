package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Shape3D;

public abstract class Shape3DHelper extends NodeHelper {
   private static Shape3DHelper.Shape3DAccessor shape3DAccessor;

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      shape3DAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return shape3DAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return shape3DAccessor.doComputeContains(var1, var2, var4);
   }

   public static void setShape3DAccessor(Shape3DHelper.Shape3DAccessor var0) {
      if (shape3DAccessor != null) {
         throw new IllegalStateException();
      } else {
         shape3DAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Shape3D.class);
   }

   public interface Shape3DAccessor {
      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);
   }
}
