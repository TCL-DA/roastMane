package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class LineHelper extends ShapeHelper {
   private static final LineHelper theInstance = new LineHelper();
   private static LineHelper.LineAccessor lineAccessor;

   private static LineHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Line var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return lineAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      lineAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return lineAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Paint cssGetFillInitialValueImpl(Shape var1) {
      return lineAccessor.doCssGetFillInitialValue(var1);
   }

   @Override
   protected Paint cssGetStrokeInitialValueImpl(Shape var1) {
      return lineAccessor.doCssGetStrokeInitialValue(var1);
   }

   @Override
   protected com.sun.javafx.geom.Shape configShapeImpl(Shape var1) {
      return lineAccessor.doConfigShape(var1);
   }

   public static void setLineAccessor(LineHelper.LineAccessor var0) {
      if (lineAccessor != null) {
         throw new IllegalStateException();
      } else {
         lineAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Line.class);
   }

   public interface LineAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Paint doCssGetFillInitialValue(Shape var1);

      Paint doCssGetStrokeInitialValue(Shape var1);

      com.sun.javafx.geom.Shape doConfigShape(Shape var1);
   }
}
