package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class RectangleHelper extends ShapeHelper {
   private static final RectangleHelper theInstance = new RectangleHelper();
   private static RectangleHelper.RectangleAccessor rectangleAccessor;

   private static RectangleHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Rectangle var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return rectangleAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      rectangleAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return rectangleAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return rectangleAccessor.doConfigShape(var1);
   }

   public static void setRectangleAccessor(RectangleHelper.RectangleAccessor var0) {
      if (rectangleAccessor != null) {
         throw new IllegalStateException();
      } else {
         rectangleAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Rectangle.class);
   }

   public interface RectangleAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
