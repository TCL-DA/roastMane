package com.sun.javafx.scene.media;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.media.MediaView;

public class MediaViewHelper extends NodeHelper {
   private static final MediaViewHelper theInstance = new MediaViewHelper();
   private static MediaViewHelper.MediaViewAccessor mediaViewAccessor;

   private static MediaViewHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(MediaView var0) {
      setHelper(var0, getInstance());
   }

   protected NGNode createPeerImpl(Node var1) {
      return mediaViewAccessor.doCreatePeer(var1);
   }

   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      mediaViewAccessor.doUpdatePeer(var1);
   }

   protected void transformsChangedImpl(Node var1) {
      super.transformsChangedImpl(var1);
      mediaViewAccessor.doTransformsChanged(var1);
   }

   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return mediaViewAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return mediaViewAccessor.doComputeContains(var1, var2, var4);
   }

   public static void setMediaViewAccessor(MediaViewHelper.MediaViewAccessor var0) {
      if (mediaViewAccessor != null) {
         throw new IllegalStateException();
      } else {
         mediaViewAccessor = var0;
      }
   }

   static {
      Utils.forceInit(MediaView.class);
   }

   public interface MediaViewAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      void doTransformsChanged(Node var1);

      boolean doComputeContains(Node var1, double var2, double var4);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);
   }
}
