package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.AmbientLight;
import javafx.scene.Node;

public class AmbientLightHelper extends LightBaseHelper {
   private static final AmbientLightHelper theInstance = new AmbientLightHelper();
   private static AmbientLightHelper.AmbientLightAccessor ambientLightAccessor;

   private static AmbientLightHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(AmbientLight var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return ambientLightAccessor.doCreatePeer(var1);
   }

   public static void setAmbientLightAccessor(AmbientLightHelper.AmbientLightAccessor var0) {
      if (ambientLightAccessor != null) {
         throw new IllegalStateException();
      } else {
         ambientLightAccessor = var0;
      }
   }

   static {
      Utils.forceInit(AmbientLight.class);
   }

   public interface AmbientLightAccessor {
      NGNode doCreatePeer(Node var1);
   }
}
