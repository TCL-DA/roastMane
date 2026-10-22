package com.sun.javafx.scene;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.LightBase;
import javafx.scene.Node;

public class LightBaseHelper extends NodeHelper {
   private static final LightBaseHelper theInstance = new LightBaseHelper();
   private static LightBaseHelper.LightBaseAccessor lightBaseAccessor;

   private static LightBaseHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(LightBase var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      throw new UnsupportedOperationException("Applications should not extend the LightBase class directly.");
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      lightBaseAccessor.doUpdatePeer(var1);
   }

   @Override
   protected void markDirtyImpl(Node var1, DirtyBits var2) {
      super.markDirtyImpl(var1, var2);
      lightBaseAccessor.doMarkDirty(var1, var2);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return lightBaseAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return lightBaseAccessor.doComputeContains(var1, var2, var4);
   }

   public static void setLightBaseAccessor(LightBaseHelper.LightBaseAccessor var0) {
      if (lightBaseAccessor != null) {
         throw new IllegalStateException();
      } else {
         lightBaseAccessor = var0;
      }
   }

   static {
      Utils.forceInit(LightBase.class);
   }

   public interface LightBaseAccessor {
      void doMarkDirty(Node var1, DirtyBits var2);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);
   }
}
