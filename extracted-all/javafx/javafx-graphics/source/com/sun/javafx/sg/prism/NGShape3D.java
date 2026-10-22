package com.sun.javafx.sg.prism;

import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.util.Utils;
import com.sun.prism.Graphics;
import com.sun.prism.Material;
import com.sun.prism.MeshView;
import com.sun.prism.PrinterGraphics;
import com.sun.prism.ResourceFactory;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public abstract class NGShape3D extends NGNode {
   private NGPhongMaterial material;
   private DrawMode drawMode;
   private CullFace cullFace;
   private boolean materialDirty = false;
   private boolean drawModeDirty = false;
   NGTriangleMesh mesh;
   private MeshView meshView;

   public void setMaterial(NGPhongMaterial var1) {
      this.material = var1;
      this.materialDirty = true;
      this.visualsChanged();
   }

   public void setDrawMode(Object var1) {
      this.drawMode = (DrawMode)var1;
      this.drawModeDirty = true;
      this.visualsChanged();
   }

   public void setCullFace(Object var1) {
      this.cullFace = (CullFace)var1;
      this.visualsChanged();
   }

   void invalidate() {
      this.meshView = null;
      this.visualsChanged();
   }

   private void renderMeshView(Graphics var1) {
      var1.setup3DRendering();
      ResourceFactory var2 = var1.getResourceFactory();
      if (var2 != null && !var2.isDisposed()) {
         if (this.meshView != null && !this.meshView.isValid()) {
            this.meshView.dispose();
            this.meshView = null;
         }

         if (this.meshView == null && this.mesh != null) {
            this.meshView = var2.createMeshView(this.mesh.createMesh(var2));
            this.materialDirty = this.drawModeDirty = true;
         }

         if (this.meshView != null && this.mesh.validate()) {
            Material var3 = this.material.createMaterial(var2);
            if (this.materialDirty) {
               this.meshView.setMaterial(var3);
               this.materialDirty = false;
            }

            int var4 = this.cullFace.ordinal();
            if (this.cullFace.ordinal() != MeshView.CULL_NONE && var1.getTransformNoClone().getDeterminant() < 0.0) {
               var4 = var4 == MeshView.CULL_BACK ? MeshView.CULL_FRONT : MeshView.CULL_BACK;
            }

            this.meshView.setCullingMode(var4);
            if (this.drawModeDirty) {
               this.meshView.setWireframe(this.drawMode == DrawMode.LINE);
               this.drawModeDirty = false;
            }

            int var5 = 0;
            if (var1.getLights() != null && var1.getLights()[0] != null) {
               float var18 = 0.0F;
               float var7 = 0.0F;
               float var8 = 0.0F;

               for (NGLightBase var12 : var1.getLights()) {
                  if (var12 == null) {
                     break;
                  }

                  if (var12.affects(this)) {
                     float var13 = var12.getColor().getRed();
                     float var14 = var12.getColor().getGreen();
                     float var15 = var12.getColor().getBlue();
                     if (var13 != 0.0F || var14 != 0.0F || var15 != 0.0F) {
                        if (var12 instanceof NGPointLight) {
                           NGPointLight var16 = (NGPointLight)var12;
                           Affine3D var17 = var16.getWorldTransform();
                           this.meshView
                              .setLight(
                                 var5++,
                                 (float)var17.getMxt(),
                                 (float)var17.getMyt(),
                                 (float)var17.getMzt(),
                                 var13,
                                 var14,
                                 var15,
                                 1.0F,
                                 var16.getCa(),
                                 var16.getLa(),
                                 var16.getQa(),
                                 var16.getMaxRange(),
                                 (float)var16.getDirection().getX(),
                                 (float)var16.getDirection().getY(),
                                 (float)var16.getDirection().getZ(),
                                 var16.getInnerAngle(),
                                 var16.getOuterAngle(),
                                 var16.getFalloff()
                              );
                        } else if (var12 instanceof NGAmbientLight) {
                           var18 += var13;
                           var8 += var14;
                           var7 += var15;
                        }
                     }
                  }
               }

               var18 = Utils.clamp(0.0F, var18, 1.0F);
               var8 = Utils.clamp(0.0F, var8, 1.0F);
               var7 = Utils.clamp(0.0F, var7, 1.0F);
               this.meshView.setAmbientLight(var18, var8, var7);
            } else {
               this.meshView.setAmbientLight(0.0F, 0.0F, 0.0F);
               Vec3d var6 = var1.getCameraNoClone().getPositionInWorld(null);
               this.meshView
                  .setLight(
                     var5++,
                     (float)var6.x,
                     (float)var6.y,
                     (float)var6.z,
                     1.0F,
                     1.0F,
                     1.0F,
                     1.0F,
                     NGPointLight.getDefaultCa(),
                     NGPointLight.getDefaultLa(),
                     NGPointLight.getDefaultQa(),
                     NGPointLight.getDefaultMaxRange(),
                     (float)NGPointLight.getSimulatedDirection().getX(),
                     (float)NGPointLight.getSimulatedDirection().getY(),
                     (float)NGPointLight.getSimulatedDirection().getZ(),
                     NGPointLight.getSimulatedInnerAngle(),
                     NGPointLight.getSimulatedOuterAngle(),
                     NGPointLight.getSimulatedFalloff()
                  );
            }

            while (var5 < 3) {
               this.meshView.setLight(var5++, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            }

            this.meshView.render(var1);
         }
      }
   }

   public void setMesh(NGTriangleMesh var1) {
      this.mesh = var1;
      this.meshView = null;
      this.visualsChanged();
   }

   @Override
   protected void renderContent(Graphics var1) {
      if (Platform.isSupported(ConditionalFeature.SCENE3D) && this.material != null && !(var1 instanceof PrinterGraphics)) {
         this.renderMeshView(var1);
      }
   }

   @Override
   boolean isShape3D() {
      return true;
   }

   @Override
   protected boolean hasOverlappingContents() {
      return false;
   }

   @Override
   public void release() {
   }
}
