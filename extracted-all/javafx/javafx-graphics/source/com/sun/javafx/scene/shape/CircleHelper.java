package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

public class CircleHelper extends ShapeHelper {
   private static final CircleHelper theInstance = new CircleHelper();
   private static CircleHelper.CircleAccessor circleAccessor;

   private static CircleHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Circle var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return circleAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      circleAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return circleAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return circleAccessor.doConfigShape(var1);
   }

   public static void setCircleAccessor(CircleHelper.CircleAccessor var0) {
      if (circleAccessor != null) {
         throw new IllegalStateException();
      } else {
         circleAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Circle.class);
   }

   public interface CircleAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
