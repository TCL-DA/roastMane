package com.sun.javafx.scene.paint;

import com.sun.javafx.sg.prism.NGPhongMaterial;
import com.sun.javafx.util.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.scene.paint.Material;

public class MaterialHelper {
   private static MaterialHelper.MaterialAccessor materialAccessor;

   private MaterialHelper() {
   }

   public static BooleanProperty dirtyProperty(Material var0) {
      return materialAccessor.dirtyProperty(var0);
   }

   public static void updatePG(Material var0) {
      materialAccessor.updatePG(var0);
   }

   public static NGPhongMaterial getNGMaterial(Material var0) {
      return materialAccessor.getNGMaterial(var0);
   }

   public static void setMaterialAccessor(MaterialHelper.MaterialAccessor var0) {
      if (materialAccessor != null) {
         throw new IllegalStateException();
      } else {
         materialAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Material.class);
   }

   public interface MaterialAccessor {
      BooleanProperty dirtyProperty(Material var1);

      void updatePG(Material var1);

      NGPhongMaterial getNGMaterial(Material var1);
   }
}
