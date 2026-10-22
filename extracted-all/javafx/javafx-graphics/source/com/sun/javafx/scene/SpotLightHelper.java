package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.SpotLight;

public class SpotLightHelper extends PointLightHelper {
   private static final SpotLightHelper theInstance = new SpotLightHelper();
   private static SpotLightHelper.SpotLightAccessor spotLightAccessor;

   private static SpotLightHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(SpotLight var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return spotLightAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      spotLightAccessor.doUpdatePeer(var1);
   }

   public static void setSpotLightAccessor(SpotLightHelper.SpotLightAccessor var0) {
      if (spotLightAccessor != null) {
         throw new IllegalStateException("Accessor already exists");
      } else {
         spotLightAccessor = var0;
      }
   }

   static {
      Utils.forceInit(SpotLight.class);
   }

   public interface SpotLightAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);
   }
}
