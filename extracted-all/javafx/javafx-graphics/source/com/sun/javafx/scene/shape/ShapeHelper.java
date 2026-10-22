package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public abstract class ShapeHelper extends NodeHelper {
   private static ShapeHelper.ShapeAccessor shapeAccessor;

   public static Paint cssGetFillInitialValue(Shape var0) {
      return ((ShapeHelper)getHelper(var0)).cssGetFillInitialValueImpl(var0);
   }

   public static Paint cssGetStrokeInitialValue(Shape var0) {
      return ((ShapeHelper)getHelper(var0)).cssGetStrokeInitialValueImpl(var0);
   }

   public static com.sun.javafx.geom.Shape configShape(Shape var0) {
      return ((ShapeHelper)getHelper(var0)).configShapeImpl(var0);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      shapeAccessor.doUpdatePeer(var1);
   }

   @Override
   protected void markDirtyImpl(Node var1, DirtyBits var2) {
      shapeAccessor.doMarkDirty(var1, var2);
      super.markDirtyImpl(var1, var2);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return shapeAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return shapeAccessor.doComputeContains(var1, var2, var4);
   }

   protected Paint cssGetFillInitialValueImpl(Shape var1) {
      return shapeAccessor.doCssGetFillInitialValue(var1);
   }

   protected Paint cssGetStrokeInitialValueImpl(Shape var1) {
      return shapeAccessor.doCssGetStrokeInitialValue(var1);
   }

   protected abstract com.sun.javafx.geom.Shape configShapeImpl(Shape var1);

   public static NGShape.Mode getMode(Shape var0) {
      return shapeAccessor.getMode(var0);
   }

   public static void setMode(Shape var0, NGShape.Mode var1) {
      shapeAccessor.setMode(var0, var1);
   }

   public static void setShapeChangeListener(Shape var0, Runnable var1) {
      shapeAccessor.setShapeChangeListener(var0, var1);
   }

   public static void setShapeAccessor(ShapeHelper.ShapeAccessor var0) {
      if (shapeAccessor != null) {
         throw new IllegalStateException();
      } else {
         shapeAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Shape.class);
   }

   public interface ShapeAccessor {
      void doUpdatePeer(Node var1);

      void doMarkDirty(Node var1, DirtyBits var2);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      Paint doCssGetFillInitialValue(Shape var1);

      Paint doCssGetStrokeInitialValue(Shape var1);

      NGShape.Mode getMode(Shape var1);

      void setMode(Shape var1, NGShape.Mode var2);

      void setShapeChangeListener(Shape var1, Runnable var2);
   }
}
