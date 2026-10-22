package org.controlsfx.control.decoration;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;

public abstract class Decoration {
   private volatile Map<String, Object> properties;

   protected Decoration() {
   }

   public abstract Node applyDecoration(Node var1);

   public abstract void removeDecoration(Node var1);

   public final synchronized Map<String, Object> getProperties() {
      if (this.properties == null) {
         this.properties = new HashMap<>();
      }

      return this.properties;
   }
}
