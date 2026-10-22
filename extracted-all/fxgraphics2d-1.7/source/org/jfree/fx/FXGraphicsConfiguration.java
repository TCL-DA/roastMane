package org.jfree.fx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public class FXGraphicsConfiguration extends GraphicsConfiguration {
   private GraphicsDevice device;
   private final int width;
   private final int height;

   public FXGraphicsConfiguration(int width, int height) {
      this.width = width;
      this.height = height;
   }

   @Override
   public GraphicsDevice getDevice() {
      if (this.device == null) {
         this.device = new FXGraphicsDevice("FXGraphicsDevice", this);
      }

      return this.device;
   }

   @Override
   public ColorModel getColorModel() {
      return this.getColorModel(3);
   }

   @Override
   public ColorModel getColorModel(int transparency) {
      if (transparency == 3) {
         return ColorModel.getRGBdefault();
      } else {
         return transparency == 1 ? new DirectColorModel(32, 16711680, 65280, 255) : null;
      }
   }

   @Override
   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   @Override
   public AffineTransform getNormalizingTransform() {
      return new AffineTransform();
   }

   @Override
   public Rectangle getBounds() {
      return new Rectangle(this.width, this.height);
   }
}
