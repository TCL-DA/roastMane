package org.jfree.fx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class FXGraphicsDevice extends GraphicsDevice {
   private final String id;
   GraphicsConfiguration defaultConfig;

   public FXGraphicsDevice(String id, GraphicsConfiguration defaultConfig) {
      this.id = id;
      this.defaultConfig = defaultConfig;
   }

   @Override
   public int getType() {
      return 0;
   }

   @Override
   public String getIDstring() {
      return this.id;
   }

   @Override
   public GraphicsConfiguration[] getConfigurations() {
      return new GraphicsConfiguration[]{this.getDefaultConfiguration()};
   }

   @Override
   public GraphicsConfiguration getDefaultConfiguration() {
      return this.defaultConfig;
   }
}
