package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.sg.prism.NGTriangleMesh;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class Mesh {
   private MeshHelper meshHelper = null;
   private final BooleanProperty dirty = new SimpleBooleanProperty(true);

   protected Mesh() {
      if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
         String var1 = Mesh.class.getName();
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

   abstract NGTriangleMesh getPGMesh();

   abstract void updatePG();

   abstract BaseBounds computeBounds(BaseBounds var1);

   static {
      MeshHelper.setMeshAccessor(new MeshHelper.MeshAccessor() {
         @Override
         public MeshHelper getHelper(Mesh var1) {
            return var1.meshHelper;
         }

         @Override
         public void setHelper(Mesh var1, MeshHelper var2) {
            var1.meshHelper = var2;
         }
      });
   }
}
