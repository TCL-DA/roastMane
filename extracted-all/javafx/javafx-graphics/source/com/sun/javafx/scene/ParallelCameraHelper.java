package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;

public class ParallelCameraHelper extends CameraHelper {
   private static final ParallelCameraHelper theInstance = new ParallelCameraHelper();
   private static ParallelCameraHelper.ParallelCameraAccessor parallelCameraAccessor;

   private static ParallelCameraHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(ParallelCamera var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return parallelCameraAccessor.doCreatePeer(var1);
   }

   public static void setParallelCameraAccessor(ParallelCameraHelper.ParallelCameraAccessor var0) {
      if (parallelCameraAccessor != null) {
         throw new IllegalStateException();
      } else {
         parallelCameraAccessor = var0;
      }
   }

   static {
      Utils.forceInit(ParallelCamera.class);
   }

   public interface ParallelCameraAccessor {
      NGNode doCreatePeer(Node var1);
   }
}
