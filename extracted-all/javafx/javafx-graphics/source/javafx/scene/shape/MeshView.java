package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.scene.shape.MeshViewHelper;
import com.sun.javafx.sg.prism.NGMeshView;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;

public class MeshView extends Shape3D {
   private ObjectProperty<Mesh> mesh;

   public MeshView() {
      MeshViewHelper.initHelper(this);
   }

   public MeshView(Mesh var1) {
      MeshViewHelper.initHelper(this);
      this.setMesh(var1);
   }

   public final void setMesh(Mesh var1) {
      this.meshProperty().set(var1);
   }

   public final Mesh getMesh() {
      return this.mesh == null ? null : (Mesh)this.mesh.get();
   }

   public final ObjectProperty<Mesh> meshProperty() {
      if (this.mesh == null) {
         this.mesh = new SimpleObjectProperty<Mesh>(this, "mesh") {
            private Mesh old = null;
            private final ChangeListener<Boolean> meshChangeListener = (var1x, var2x, var3x) -> {
               if (var3x) {
                  NodeHelper.markDirty(MeshView.this, DirtyBits.MESH_GEOM);
                  NodeHelper.geomChanged(MeshView.this);
               }
            };
            private final WeakChangeListener<Boolean> weakMeshChangeListener = new WeakChangeListener(MeshView.super.meshChangeListener);

            protected void invalidated() {
               if (this.old != null) {
                  this.old.dirtyProperty().removeListener(this.weakMeshChangeListener);
               }

               Mesh var1 = (Mesh)this.get();
               if (var1 != null) {
                  var1.dirtyProperty().addListener(this.weakMeshChangeListener);
               }

               NodeHelper.markDirty(MeshView.this, DirtyBits.MESH);
               NodeHelper.markDirty(MeshView.this, DirtyBits.MESH_GEOM);
               NodeHelper.geomChanged(MeshView.this);
               this.old = var1;
            }
         };
      }

      return this.mesh;
   }

   private void doUpdatePeer() {
      NGMeshView var1 = NodeHelper.getPeer(this);
      if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM) && this.getMesh() != null) {
         this.getMesh().updatePG();
      }

      if (NodeHelper.isDirty(this, DirtyBits.MESH)) {
         var1.setMesh(this.getMesh() == null ? null : this.getMesh().getPGMesh());
      }
   }

   private NGNode doCreatePeer() {
      return new NGMeshView();
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (this.getMesh() != null) {
         var1 = this.getMesh().computeBounds(var1);
         var1 = var2.transform(var1, var1);
      } else {
         var1.makeEmpty();
      }

      return var1;
   }

   private boolean doComputeContains(double var1, double var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   private boolean doComputeIntersects(PickRay var1, PickResultChooser var2) {
      return MeshHelper.computeIntersects(this.getMesh(), var1, var2, this, this.getCullFace(), true);
   }

   static {
      MeshViewHelper.setMeshViewAccessor(new MeshViewHelper.MeshViewAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((MeshView)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((MeshView)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((MeshView)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((MeshView)var1).doComputeContains(var2, var4);
         }

         @Override
         public boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3) {
            return ((MeshView)var1).doComputeIntersects(var2, var3);
         }
      });
   }
}
