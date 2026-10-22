package com.sun.javafx.scene;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Node;

public class CameraHelper extends NodeHelper {
   private static final CameraHelper theInstance = new CameraHelper();
   private static CameraHelper.CameraAccessor cameraAccessor;

   private static CameraHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Camera var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      throw new UnsupportedOperationException("Applications should not extend the Camera class directly.");
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      cameraAccessor.doUpdatePeer(var1);
   }

   @Override
   protected void markDirtyImpl(Node var1, DirtyBits var2) {
      super.markDirtyImpl(var1, var2);
      cameraAccessor.doMarkDirty(var1, var2);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return cameraAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return cameraAccessor.doComputeContains(var1, var2, var4);
   }

   public static Point2D project(Camera var0, Point3D var1) {
      return cameraAccessor.project(var0, var1);
   }

   public static Point2D pickNodeXYPlane(Camera var0, Node var1, double var2, double var4) {
      return cameraAccessor.pickNodeXYPlane(var0, var1, var2, var4);
   }

   public static Point3D pickProjectPlane(Camera var0, double var1, double var3) {
      return cameraAccessor.pickProjectPlane(var0, var1, var3);
   }

   public static void setCameraAccessor(CameraHelper.CameraAccessor var0) {
      if (cameraAccessor != null) {
         throw new IllegalStateException();
      } else {
         cameraAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Camera.class);
   }

   public interface CameraAccessor {
      void doMarkDirty(Node var1, DirtyBits var2);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      Point2D project(Camera var1, Point3D var2);

      Point2D pickNodeXYPlane(Camera var1, Node var2, double var3, double var5);

      Point3D pickProjectPlane(Camera var1, double var2, double var4);
   }
}
