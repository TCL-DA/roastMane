package com.sun.javafx.scene;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.SubScene;

public class SubSceneHelper extends NodeHelper {
   private static final SubSceneHelper theInstance = new SubSceneHelper();
   private static SubSceneHelper.SubSceneAccessor subSceneAccessor;

   private static SubSceneHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(SubScene var0) {
      setHelper(var0, getInstance());
   }

   public static void superProcessCSS(Node var0) {
      ((SubSceneHelper)getHelper(var0)).superProcessCSSImpl(var0);
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return subSceneAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      subSceneAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return subSceneAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return subSceneAccessor.doComputeContains(var1, var2, var4);
   }

   void superProcessCSSImpl(Node var1) {
      super.processCSSImpl(var1);
   }

   @Override
   protected void processCSSImpl(Node var1) {
      subSceneAccessor.doProcessCSS(var1);
   }

   @Override
   protected void pickNodeLocalImpl(Node var1, PickRay var2, PickResultChooser var3) {
      subSceneAccessor.doPickNodeLocal(var1, var2, var3);
   }

   public static boolean isDepthBuffer(SubScene var0) {
      return subSceneAccessor.isDepthBuffer(var0);
   }

   public static Camera getEffectiveCamera(SubScene var0) {
      return subSceneAccessor.getEffectiveCamera(var0);
   }

   public static void setSubSceneAccessor(SubSceneHelper.SubSceneAccessor var0) {
      if (subSceneAccessor != null) {
         throw new IllegalStateException();
      } else {
         subSceneAccessor = var0;
      }
   }

   static {
      Utils.forceInit(SubScene.class);
   }

   public interface SubSceneAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      void doProcessCSS(Node var1);

      void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3);

      boolean isDepthBuffer(SubScene var1);

      Camera getEffectiveCamera(SubScene var1);
   }
}
