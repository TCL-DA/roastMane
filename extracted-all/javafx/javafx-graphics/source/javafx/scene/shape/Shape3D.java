package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.scene.shape.Shape3DHelper;
import com.sun.javafx.sg.prism.NGShape3D;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

public abstract class Shape3D extends Node {
   private static final PhongMaterial DEFAULT_MATERIAL = new PhongMaterial();
   PredefinedMeshManager manager = PredefinedMeshManager.getInstance();
   Shape3D.Key key;
   private ObjectProperty<Material> material;
   private ObjectProperty<DrawMode> drawMode;
   private ObjectProperty<CullFace> cullFace;

   protected Shape3D() {
      if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
         String var1 = Shape3D.class.getName();
         PlatformLogger.getLogger(var1).warning("System can't support ConditionalFeature.SCENE3D");
      }
   }

   public final void setMaterial(Material var1) {
      this.materialProperty().set(var1);
   }

   public final Material getMaterial() {
      return this.material == null ? null : (Material)this.material.get();
   }

   public final ObjectProperty<Material> materialProperty() {
      if (this.material == null) {
         this.material = new SimpleObjectProperty<Material>(this, "material") {
            private Material old = null;
            private final ChangeListener<Boolean> materialChangeListener = (var1x, var2x, var3x) -> {
               if (var3x) {
                  NodeHelper.markDirty(Shape3D.this, DirtyBits.MATERIAL);
               }
            };
            private final WeakChangeListener<Boolean> weakMaterialChangeListener = new WeakChangeListener(Shape3D.super.materialChangeListener);

            protected void invalidated() {
               if (this.old != null) {
                  MaterialHelper.dirtyProperty(this.old).removeListener(this.weakMaterialChangeListener);
               }

               Material var1 = (Material)this.get();
               if (var1 != null) {
                  MaterialHelper.dirtyProperty(var1).addListener(this.weakMaterialChangeListener);
               }

               NodeHelper.markDirty(Shape3D.this, DirtyBits.MATERIAL);
               NodeHelper.geomChanged(Shape3D.this);
               this.old = var1;
            }
         };
      }

      return this.material;
   }

   public final void setDrawMode(DrawMode var1) {
      this.drawModeProperty().set(var1);
   }

   public final DrawMode getDrawMode() {
      return this.drawMode == null ? DrawMode.FILL : (DrawMode)this.drawMode.get();
   }

   public final ObjectProperty<DrawMode> drawModeProperty() {
      if (this.drawMode == null) {
         this.drawMode = new SimpleObjectProperty<DrawMode>(this, "drawMode", DrawMode.FILL) {
            protected void invalidated() {
               NodeHelper.markDirty(Shape3D.this, DirtyBits.NODE_DRAWMODE);
            }
         };
      }

      return this.drawMode;
   }

   public final void setCullFace(CullFace var1) {
      this.cullFaceProperty().set(var1);
   }

   public final CullFace getCullFace() {
      return this.cullFace == null ? CullFace.BACK : (CullFace)this.cullFace.get();
   }

   public final ObjectProperty<CullFace> cullFaceProperty() {
      if (this.cullFace == null) {
         this.cullFace = new SimpleObjectProperty<CullFace>(this, "cullFace", CullFace.BACK) {
            protected void invalidated() {
               NodeHelper.markDirty(Shape3D.this, DirtyBits.NODE_CULLFACE);
            }
         };
      }

      return this.cullFace;
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      return new BoxBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   private boolean doComputeContains(double var1, double var3) {
      return false;
   }

   private void doUpdatePeer() {
      NGShape3D var1 = NodeHelper.getPeer(this);
      if (NodeHelper.isDirty(this, DirtyBits.MATERIAL)) {
         Object var2 = this.getMaterial() == null ? DEFAULT_MATERIAL : this.getMaterial();
         MaterialHelper.updatePG((Material)var2);
         var1.setMaterial(MaterialHelper.getNGMaterial((Material)var2));
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_DRAWMODE)) {
         var1.setDrawMode(this.getDrawMode() == null ? DrawMode.FILL : this.getDrawMode());
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_CULLFACE)) {
         var1.setCullFace(this.getCullFace() == null ? CullFace.BACK : this.getCullFace());
      }
   }

   static {
      Shape3DHelper.setShape3DAccessor(new Shape3DHelper.Shape3DAccessor() {
         @Override
         public void doUpdatePeer(Node var1) {
            ((Shape3D)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Shape3D)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Shape3D)var1).doComputeContains(var2, var4);
         }
      });
   }

   abstract static class Key {
      @Override
      public abstract boolean equals(Object var1);

      @Override
      public abstract int hashCode();
   }
}
