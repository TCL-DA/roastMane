package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.PointLight;

public class PointLightHelper extends LightBaseHelper {
   private static final PointLightHelper theInstance = new PointLightHelper();
   private static PointLightHelper.PointLightAccessor pointLightAccessor;

   private static PointLightHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(PointLight var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return pointLightAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      pointLightAccessor.doUpdatePeer(var1);
   }

   public static void setPointLightAccessor(PointLightHelper.PointLightAccessor var0) {
      if (pointLightAccessor != null) {
         throw new IllegalStateException();
      } else {
         pointLightAccessor = var0;
      }
   }

   static {
      Utils.forceInit(PointLight.class);
   }

   public interface PointLightAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);
   }
}
