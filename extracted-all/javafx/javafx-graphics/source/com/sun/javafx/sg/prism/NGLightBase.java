package com.sun.javafx.sg.prism;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import com.sun.prism.paint.Color;
import java.util.List;

public class NGLightBase extends NGNode {
   private Color color = Color.WHITE;
   private boolean lightOn = true;
   private Affine3D worldTransform;
   List<NGNode> scopedNodes = List.of();
   List<NGNode> excludedNodes = List.of();

   protected NGLightBase() {
   }

   @Override
   public void setTransformMatrix(BaseTransform var1) {
      super.setTransformMatrix(var1);
   }

   @Override
   protected void doRender(Graphics var1) {
   }

   @Override
   protected void renderContent(Graphics var1) {
   }

   @Override
   protected boolean hasOverlappingContents() {
      return false;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Object var1) {
      if (!this.color.equals(var1)) {
         this.color = (Color)var1;
         this.visualsChanged();
      }
   }

   public boolean isLightOn() {
      return this.lightOn;
   }

   public void setLightOn(boolean var1) {
      if (this.lightOn != var1) {
         this.visualsChanged();
         this.lightOn = var1;
      }
   }

   public Affine3D getWorldTransform() {
      return this.worldTransform;
   }

   public void setWorldTransform(Affine3D var1) {
      this.worldTransform = var1;
   }

   public void setScope(List<NGNode> var1) {
      if (!this.scopedNodes.equals(var1)) {
         this.scopedNodes = var1;
         this.visualsChanged();
      }
   }

   public void setExclusionScope(List<NGNode> var1) {
      if (!this.excludedNodes.equals(var1)) {
         this.excludedNodes = var1;
         this.visualsChanged();
      }
   }

   final boolean affects(NGShape3D var1) {
      if (!this.lightOn) {
         return false;
      } else if (this.scopedNodes.isEmpty() && this.excludedNodes.isEmpty()) {
         return true;
      } else if (this.scopedNodes.contains(var1)) {
         return true;
      } else if (this.excludedNodes.contains(var1)) {
         return false;
      } else {
         for (NGNode var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
            if (this.scopedNodes.contains(var2)) {
               return true;
            }

            if (this.excludedNodes.contains(var2)) {
               return false;
            }
         }

         return this.scopedNodes.isEmpty();
      }
   }

   @Override
   public void release() {
   }
}
