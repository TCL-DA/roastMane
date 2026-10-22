package com.sun.prism.j2d;

import com.sun.glass.ui.Screen;
import com.sun.javafx.font.FontFactory;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.ResourceFactory;
import java.util.HashMap;
import java.util.List;

public class J2DPipeline extends GraphicsPipeline {
   private static J2DPipeline theInstance;
   private final HashMap<Integer, J2DResourceFactory> factories = new HashMap<>(1);
   private FontFactory j2DFontFactory;

   @Override
   public boolean init() {
      return true;
   }

   private J2DPipeline() {
   }

   public static J2DPipeline getInstance() {
      if (theInstance == null) {
         theInstance = new J2DPipeline();
      }

      return theInstance;
   }

   @Override
   public int getAdapterOrdinal(Screen var1) {
      return Screen.getScreens().indexOf(var1);
   }

   @Override
   public ResourceFactory getResourceFactory(Screen var1) {
      Integer var2 = var1.getAdapterOrdinal();
      J2DResourceFactory var3 = this.factories.get(var2);
      if (var3 == null) {
         var3 = new J2DResourceFactory(var1);
         this.factories.put(var2, var3);
      }

      return var3;
   }

   @Override
   public ResourceFactory getDefaultResourceFactory(List<Screen> var1) {
      return this.getResourceFactory(Screen.getMainScreen());
   }

   @Override
   public boolean is3DSupported() {
      return false;
   }

   @Override
   public boolean isVsyncSupported() {
      return false;
   }

   @Override
   public boolean supportsShaderType(GraphicsPipeline.ShaderType var1) {
      return false;
   }

   @Override
   public boolean supportsShaderModel(GraphicsPipeline.ShaderModel var1) {
      return false;
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public FontFactory getFontFactory() {
      if (this.j2DFontFactory == null) {
         FontFactory var1 = super.getFontFactory();
         this.j2DFontFactory = new J2DFontFactory(var1);
      }

      return this.j2DFontFactory;
   }

   @Override
   public boolean isUploading() {
      return true;
   }
}
