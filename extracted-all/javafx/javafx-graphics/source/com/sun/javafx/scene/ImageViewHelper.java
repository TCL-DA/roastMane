package com.sun.javafx.scene;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class ImageViewHelper extends NodeHelper {
   private static final ImageViewHelper theInstance = new ImageViewHelper();
   private static ImageViewHelper.ImageViewAccessor imageViewAccessor;

   private static ImageViewHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(ImageView var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return imageViewAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      imageViewAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return imageViewAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return imageViewAccessor.doComputeContains(var1, var2, var4);
   }

   public static void setImageViewAccessor(ImageViewHelper.ImageViewAccessor var0) {
      if (imageViewAccessor != null) {
         throw new IllegalStateException();
      } else {
         imageViewAccessor = var0;
      }
   }

   static {
      Utils.forceInit(ImageView.class);
   }

   public interface ImageViewAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);
   }
}
