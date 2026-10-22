package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;

public class CylinderHelper extends Shape3DHelper {
   private static final CylinderHelper theInstance = new CylinderHelper();
   private static CylinderHelper.CylinderAccessor cylinderAccessor;

   private static CylinderHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Cylinder var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return cylinderAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      cylinderAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return cylinderAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return cylinderAccessor.doComputeContains(var1, var2, var4);
   }

   @Override
   protected boolean computeIntersectsImpl(Node var1, PickRay var2, PickResultChooser var3) {
      return cylinderAccessor.doComputeIntersects(var1, var2, var3);
   }

   public static void setCylinderAccessor(CylinderHelper.CylinderAccessor var0) {
      if (cylinderAccessor != null) {
         throw new IllegalStateException();
      } else {
         cylinderAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Cylinder.class);
   }

   public interface CylinderAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3);
   }
}
