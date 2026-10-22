package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Arc;

public class ArcHelper extends ShapeHelper {
   private static final ArcHelper theInstance = new ArcHelper();
   private static ArcHelper.ArcAccessor arcAccessor;

   private static ArcHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Arc var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return arcAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      arcAccessor.doUpdatePeer(var1);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return arcAccessor.doConfigShape(var1);
   }

   public static void setArcAccessor(ArcHelper.ArcAccessor var0) {
      if (arcAccessor != null) {
         throw new IllegalStateException();
      } else {
         arcAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Arc.class);
   }

   public interface ArcAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
