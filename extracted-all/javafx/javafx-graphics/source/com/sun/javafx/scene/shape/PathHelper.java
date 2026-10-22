package com.sun.javafx.scene.shape;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

public class PathHelper extends ShapeHelper {
   private static final PathHelper theInstance = new PathHelper();
   private static PathHelper.PathAccessor pathAccessor;

   private static PathHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Path var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return pathAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      pathAccessor.doUpdatePeer(var1);
   }

   @Override
   protected Bounds computeLayoutBoundsImpl(Node var1) {
      Bounds var2 = pathAccessor.doComputeLayoutBounds(var1);
      return var2 != null ? var2 : super.computeLayoutBoundsImpl(var1);
   }

   @Override
   protected Paint cssGetFillInitialValueImpl(Shape var1) {
      return pathAccessor.doCssGetFillInitialValue(var1);
   }

   @Override
   protected Paint cssGetStrokeInitialValueImpl(Shape var1) {
      return pathAccessor.doCssGetStrokeInitialValue(var1);
   }

   @Override
   protected com.sun.javafx.geom.Shape configShapeImpl(Shape var1) {
      return pathAccessor.doConfigShape(var1);
   }

   public static void setPathAccessor(PathHelper.PathAccessor var0) {
      if (pathAccessor != null) {
         throw new IllegalStateException();
      } else {
         pathAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Path.class);
   }

   public interface PathAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      Bounds doComputeLayoutBounds(Node var1);

      Paint doCssGetFillInitialValue(Shape var1);

      Paint doCssGetStrokeInitialValue(Shape var1);

      com.sun.javafx.geom.Shape doConfigShape(Shape var1);
   }
}
