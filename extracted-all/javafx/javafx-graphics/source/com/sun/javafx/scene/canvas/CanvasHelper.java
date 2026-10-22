package com.sun.javafx.scene.canvas;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

public class CanvasHelper extends NodeHelper {
   private static final CanvasHelper theInstance = new CanvasHelper();
   private static CanvasHelper.CanvasAccessor canvasAccessor;

   private static CanvasHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Canvas var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return canvasAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      canvasAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return canvasAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return canvasAccessor.doComputeContains(var1, var2, var4);
   }

   public static void setCanvasAccessor(CanvasHelper.CanvasAccessor var0) {
      if (canvasAccessor != null) {
         throw new IllegalStateException();
      } else {
         canvasAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Canvas.class);
   }

   public interface CanvasAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);
   }
}
