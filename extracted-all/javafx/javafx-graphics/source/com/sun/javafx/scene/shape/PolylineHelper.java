package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

public class PolylineHelper extends ShapeHelper {
   private static final PolylineHelper theInstance = new PolylineHelper();
   private static PolylineHelper.PolylineAccessor polylineAccessor;

   private static PolylineHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Polyline var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return polylineAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      polylineAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return polylineAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Paint cssGetFillInitialValueImpl(Shape var1) {
      return polylineAccessor.doCssGetFillInitialValue(var1);
   }

   @Override
   protected Paint cssGetStrokeInitialValueImpl(Shape var1) {
      return polylineAccessor.doCssGetStrokeInitialValue(var1);
   }

   @Override
   protected com.sun.javafx.geom.Shape configShapeImpl(Shape var1) {
      return polylineAccessor.doConfigShape(var1);
   }

   public static void setPolylineAccessor(PolylineHelper.PolylineAccessor var0) {
      if (polylineAccessor != null) {
         throw new IllegalStateException();
      } else {
         polylineAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Polyline.class);
   }

   public interface PolylineAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Paint doCssGetFillInitialValue(Shape var1);

      Paint doCssGetStrokeInitialValue(Shape var1);

      com.sun.javafx.geom.Shape doConfigShape(Shape var1);
   }
}
