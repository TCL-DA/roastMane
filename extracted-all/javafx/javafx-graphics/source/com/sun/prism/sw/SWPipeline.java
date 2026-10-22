package com.sun.prism.sw;

import com.sun.glass.ui.Screen;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.ResourceFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;

public final class SWPipeline extends GraphicsPipeline {
   private static SWPipeline theInstance;
   private final HashMap<Integer, SWResourceFactory> factories = new HashMap<>(1);

   @Override
   public boolean init() {
      return true;
   }

   private SWPipeline() {
   }

   public static SWPipeline getInstance() {
      if (theInstance == null) {
         theInstance = new SWPipeline();
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
      SWResourceFactory var3 = this.factories.get(var2);
      if (var3 == null) {
         var3 = new SWResourceFactory(var1);
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
   public boolean isUploading() {
      return true;
   }

   static {
      Object var0 = AccessController.doPrivileged((PrivilegedAction)(() -> {
         NativeLibLoader.loadLibrary("prism_sw");
         return null;
      }));
   }
}
