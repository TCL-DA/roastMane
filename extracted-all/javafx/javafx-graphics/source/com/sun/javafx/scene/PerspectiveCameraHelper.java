package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

public class PerspectiveCameraHelper extends CameraHelper {
   private static final PerspectiveCameraHelper theInstance = new PerspectiveCameraHelper();
   private static PerspectiveCameraHelper.PerspectiveCameraAccessor perspectiveCameraAccessor;

   private static PerspectiveCameraHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(PerspectiveCamera var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return perspectiveCameraAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      perspectiveCameraAccessor.doUpdatePeer(var1);
   }

   public static void setPerspectiveCameraAccessor(PerspectiveCameraHelper.PerspectiveCameraAccessor var0) {
      if (perspectiveCameraAccessor != null) {
         throw new IllegalStateException();
      } else {
         perspectiveCameraAccessor = var0;
      }
   }

   static {
      Utils.forceInit(PerspectiveCamera.class);
   }

   public interface PerspectiveCameraAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);
   }
}
