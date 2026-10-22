package javafx.scene.paint;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class Material {
   private final BooleanProperty dirty = new SimpleBooleanProperty(true);

   protected Material() {
      if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
         String var1 = Material.class.getName();
         PlatformLogger.getLogger(var1).warning("System can't support ConditionalFeature.SCENE3D");
      }
   }

   final boolean isDirty() {
      return this.dirty.getValue();
   }

   void setDirty(boolean var1) {
      this.dirty.setValue(var1);
   }

   final BooleanProperty dirtyProperty() {
      return this.dirty;
   }

   abstract void updatePG();

   abstract NGPhongMaterial getNGMaterial();

   static {
      MaterialHelper.setMaterialAccessor(new MaterialHelper.MaterialAccessor() {
         @Override
         public BooleanProperty dirtyProperty(Material var1) {
            return var1.dirtyProperty();
         }

         @Override
         public void updatePG(Material var1) {
            var1.updatePG();
         }

         @Override
         public NGPhongMaterial getNGMaterial(Material var1) {
            return var1.getNGMaterial();
         }
      });
   }
}
