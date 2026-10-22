package javafx.scene;

import com.sun.javafx.scene.AmbientLightHelper;
import com.sun.javafx.sg.prism.NGAmbientLight;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.paint.Color;

public class AmbientLight extends LightBase {
   public AmbientLight() {
      AmbientLightHelper.initHelper(this);
   }

   public AmbientLight(Color var1) {
      super(var1);
      AmbientLightHelper.initHelper(this);
   }

   private NGNode doCreatePeer() {
      return new NGAmbientLight();
   }

   static {
      AmbientLightHelper.setAmbientLightAccessor(new AmbientLightHelper.AmbientLightAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((AmbientLight)var1).doCreatePeer();
         }
      });
   }
}
