package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;

public class PolygonHelper extends ShapeHelper {
   private static final PolygonHelper theInstance = new PolygonHelper();
   private static PolygonHelper.PolygonAccessor polygonAccessor;

   private static PolygonHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Polygon var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return polygonAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      polygonAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return polygonAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return polygonAccessor.doConfigShape(var1);
   }

   public static void setPolygonAccessor(PolygonHelper.PolygonAccessor var0) {
      if (polygonAccessor != null) {
         throw new IllegalStateException();
      } else {
         polygonAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Polygon.class);
   }

   public interface PolygonAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
