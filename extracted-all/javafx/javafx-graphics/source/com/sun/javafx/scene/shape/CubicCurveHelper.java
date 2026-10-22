package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.CubicCurve;

public class CubicCurveHelper extends ShapeHelper {
   private static final CubicCurveHelper theInstance = new CubicCurveHelper();
   private static CubicCurveHelper.CubicCurveAccessor cubicCurveAccessor;

   private static CubicCurveHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(CubicCurve var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return cubicCurveAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      cubicCurveAccessor.doUpdatePeer(var1);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return cubicCurveAccessor.doConfigShape(var1);
   }

   public static void setCubicCurveAccessor(CubicCurveHelper.CubicCurveAccessor var0) {
      if (cubicCurveAccessor != null) {
         throw new IllegalStateException();
      } else {
         cubicCurveAccessor = var0;
      }
   }

   static {
      Utils.forceInit(CubicCurve.class);
   }

   public interface CubicCurveAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
