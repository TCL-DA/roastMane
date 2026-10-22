package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;

public class EllipseHelper extends ShapeHelper {
   private static final EllipseHelper theInstance = new EllipseHelper();
   private static EllipseHelper.EllipseAccessor ellipseAccessor;

   private static EllipseHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Ellipse var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return ellipseAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      ellipseAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return ellipseAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return ellipseAccessor.doConfigShape(var1);
   }

   public static void setEllipseAccessor(EllipseHelper.EllipseAccessor var0) {
      if (ellipseAccessor != null) {
         throw new IllegalStateException();
      } else {
         ellipseAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Ellipse.class);
   }

   public interface EllipseAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
