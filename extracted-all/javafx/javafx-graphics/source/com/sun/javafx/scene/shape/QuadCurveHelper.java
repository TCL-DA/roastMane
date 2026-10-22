package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.QuadCurve;

public class QuadCurveHelper extends ShapeHelper {
   private static final QuadCurveHelper theInstance = new QuadCurveHelper();
   private static QuadCurveHelper.QuadCurveAccessor quadCurveAccessor;

   private static QuadCurveHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(QuadCurve var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return quadCurveAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      quadCurveAccessor.doUpdatePeer(var1);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return quadCurveAccessor.doConfigShape(var1);
   }

   public static void setQuadCurveAccessor(QuadCurveHelper.QuadCurveAccessor var0) {
      if (quadCurveAccessor != null) {
         throw new IllegalStateException();
      } else {
         quadCurveAccessor = var0;
      }
   }

   static {
      Utils.forceInit(QuadCurve.class);
   }

   public interface QuadCurveAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
