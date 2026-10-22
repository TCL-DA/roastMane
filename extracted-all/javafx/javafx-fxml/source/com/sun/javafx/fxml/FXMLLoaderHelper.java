package com.sun.javafx.fxml;

import com.sun.javafx.util.Utils;
import javafx.fxml.FXMLLoader;

public class FXMLLoaderHelper {
   private static FXMLLoaderHelper.FXMLLoaderAccessor fxmlLoaderAccessor;

   private FXMLLoaderHelper() {
   }

   public static void setStaticLoad(FXMLLoader var0, boolean var1) {
      fxmlLoaderAccessor.setStaticLoad(var0, var1);
   }

   public static void setFXMLLoaderAccessor(FXMLLoaderHelper.FXMLLoaderAccessor var0) {
      if (fxmlLoaderAccessor != null) {
         throw new IllegalStateException();
      } else {
         fxmlLoaderAccessor = var0;
      }
   }

   static {
      Utils.forceInit(FXMLLoader.class);
   }

   public interface FXMLLoaderAccessor {
      void setStaticLoad(FXMLLoader var1, boolean var2);
   }
}
