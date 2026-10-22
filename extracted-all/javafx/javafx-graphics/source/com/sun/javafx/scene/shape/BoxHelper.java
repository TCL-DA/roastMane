package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Box;

public class BoxHelper extends Shape3DHelper {
   private static final BoxHelper theInstance = new BoxHelper();
   private static BoxHelper.BoxAccessor boxAccessor;

   private static BoxHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Box var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return boxAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      boxAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return boxAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return boxAccessor.doComputeContains(var1, var2, var4);
   }

   @Override
   protected boolean computeIntersectsImpl(Node var1, PickRay var2, PickResultChooser var3) {
      return boxAccessor.doComputeIntersects(var1, var2, var3);
   }

   public static void setBoxAccessor(BoxHelper.BoxAccessor var0) {
      if (boxAccessor != null) {
         throw new IllegalStateException();
      } else {
         boxAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Box.class);
   }

   public interface BoxAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3);
   }
}
