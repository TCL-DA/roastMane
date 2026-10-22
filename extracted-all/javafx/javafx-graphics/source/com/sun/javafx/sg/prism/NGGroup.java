package com.sun.javafx.sg.prism;

import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.scene.NodeHelper;
import com.sun.prism.Graphics;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrEffectHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;

public class NGGroup extends NGNode {
   private Blend.Mode blendMode = Blend.Mode.SRC_OVER;
   private List<NGNode> children = new ArrayList<>(1);
   private List<NGNode> unmod = Collections.unmodifiableList(this.children);
   private List<NGNode> removed;
   private final List<NGNode> viewOrderChildren = new ArrayList<>(1);
   private static final int REGION_INTERSECTS_MASK = 357913941;

   public List<NGNode> getChildren() {
      return this.unmod;
   }

   public void add(int var1, NGNode var2) {
      if (var1 >= -1 && var1 <= this.children.size()) {
         var2.setParent(this);
         this.childDirty = true;
         if (var1 == -1) {
            this.children.add(var2);
         } else {
            this.children.add(var1, var2);
         }

         var2.markDirty();
         this.markTreeDirtyNoIncrement();
         this.geometryChanged();
      } else {
         throw new IndexOutOfBoundsException("invalid index");
      }
   }

   public void clearFrom(int var1) {
      if (var1 < this.children.size()) {
         this.children.subList(var1, this.children.size()).clear();
         this.geometryChanged();
         this.childDirty = true;
         this.markTreeDirtyNoIncrement();
      }
   }

   public List<NGNode> getRemovedChildren() {
      return this.removed;
   }

   public void addToRemoved(NGNode var1) {
      if (this.removed == null) {
         this.removed = new ArrayList<>();
      }

      if (this.dirtyChildrenAccumulated <= 12) {
         this.removed.add(var1);
         this.dirtyChildrenAccumulated++;
         if (this.dirtyChildrenAccumulated > 12) {
            this.removed.clear();
         }
      }
   }

   @Override
   protected void clearDirty() {
      super.clearDirty();
      if (this.removed != null) {
         this.removed.clear();
      }
   }

   public void remove(NGNode var1) {
      this.children.remove(var1);
      this.geometryChanged();
      this.childDirty = true;
      this.markTreeDirtyNoIncrement();
   }

   public void remove(int var1) {
      this.children.remove(var1);
      this.geometryChanged();
      this.childDirty = true;
      this.markTreeDirtyNoIncrement();
   }

   public void clear() {
      this.children.clear();
      this.childDirty = false;
      this.geometryChanged();
      this.markTreeDirtyNoIncrement();
   }

   private List<NGNode> getOrderedChildren() {
      return !this.viewOrderChildren.isEmpty() ? this.viewOrderChildren : this.children;
   }

   public void setViewOrderChildren(List<Node> var1) {
      this.viewOrderChildren.clear();

      for (Node var3 : var1) {
         NGNode var4 = NodeHelper.getPeer(var3);
         this.viewOrderChildren.add(var4);
      }

      this.visualsChanged();
   }

   public void setBlendMode(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Mode must be non-null");
      } else {
         if (this.blendMode != var1) {
            this.blendMode = (Blend.Mode)var1;
            this.visualsChanged();
         }
      }
   }

   @Override
   public void renderForcedContent(Graphics var1) {
      List var2 = this.getOrderedChildren();
      if (var2 != null) {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            ((NGNode)var2.get(var3)).renderForcedContent(var1);
         }
      }
   }

   @Override
   protected void renderContent(Graphics var1) {
      List var2 = this.getOrderedChildren();
      if (var2 != null) {
         NodePath var3 = var1.getRenderRoot();
         int var4 = 0;
         if (var3 != null) {
            if (var3.hasNext()) {
               var3.next();
               var4 = var2.indexOf(var3.getCurrentNode());

               for (int var5 = 0; var5 < var4; var5++) {
                  ((NGNode)var2.get(var5)).clearDirtyTree();
               }
            } else {
               var1.setRenderRoot(null);
            }
         }

         if (this.blendMode != Blend.Mode.SRC_OVER && var2.size() >= 2) {
            Blend var17 = new Blend(this.blendMode, null, null);
            FilterContext var18 = getFilterContext(var1);
            ImageData var7 = null;
            boolean var8 = true;

            do {
               BaseTransform var9 = var1.getTransformNoClone().copy();
               if (var7 != null) {
                  var7.unref();
                  var7 = null;
               }

               Rectangle var10 = PrEffectHelper.getGraphicsClipNoClone(var1);

               for (int var11 = var4; var11 < var2.size(); var11++) {
                  NGNode var12 = (NGNode)var2.get(var11);
                  ImageData var13 = NodeEffectInput.getImageDataForNode(var18, var12, false, var9, var10);
                  if (var7 == null) {
                     var7 = var13;
                  } else {
                     ImageData var14 = var17.filterImageDatas(var18, var9, var10, null, new ImageData[]{var7, var13});
                     var7.unref();
                     var13.unref();
                     var7 = var14;
                  }
               }

               if (var7 != null && (var8 = var7.validate(var18))) {
                  Rectangle var19 = var7.getUntransformedBounds();
                  PrDrawable var20 = (PrDrawable)var7.getUntransformedImage();
                  var1.setTransform(var7.getTransform());
                  var1.drawTexture(var20.getTextureObject(), (float)var19.x, (float)var19.y, (float)var19.width, (float)var19.height);
               }
            } while (var7 == null || !var8);

            if (var7 != null) {
               var7.unref();
            }
         } else {
            for (int var16 = var4; var16 < var2.size(); var16++) {
               NGNode var6;
               try {
                  var6 = (NGNode)var2.get(var16);
               } catch (Exception var15) {
                  var6 = null;
               }

               if (var6 != null) {
                  var6.render(var1);
               }
            }
         }
      }
   }

   @Override
   protected boolean hasOverlappingContents() {
      if (this.blendMode != Blend.Mode.SRC_OVER) {
         return false;
      } else {
         List var1 = this.getOrderedChildren();
         int var2 = var1 == null ? 0 : var1.size();
         return var2 == 1 ? ((NGNode)var1.get(0)).hasOverlappingContents() : var2 != 0;
      }
   }

   public boolean isEmpty() {
      return this.children == null || this.children.isEmpty();
   }

   @Override
   protected boolean hasVisuals() {
      return false;
   }

   @Override
   protected boolean needsBlending() {
      Blend.Mode var1 = this.getNodeBlendMode();
      return var1 != null;
   }

   @Override
   protected NGNode.RenderRootResult computeRenderRoot(NodePath var1, RectBounds var2, int var3, BaseTransform var4, GeneralTransform3D var5) {
      if (var3 != -1) {
         int var6 = this.cullingBits >> var3 * 2;
         if ((var6 & 3) == 0) {
            return NGNode.RenderRootResult.NO_RENDER_ROOT;
         }

         if ((var6 & 2) != 0) {
            var3 = -1;
         }
      }

      if (!this.isVisible()) {
         return NGNode.RenderRootResult.NO_RENDER_ROOT;
      } else if ((double)this.getOpacity() == 1.0 && (this.getEffect() == null || !this.getEffect().reducesOpaquePixels()) && !this.needsBlending()) {
         if (this.getClipNode() != null) {
            NGNode var36 = this.getClipNode();
            RectBounds var7 = var36.getOpaqueRegion();
            if (var7 == null) {
               return NGNode.RenderRootResult.NO_RENDER_ROOT;
            }

            TEMP_TRANSFORM.deriveWithNewTransform(var4).deriveWithConcatenation(this.getTransform()).deriveWithConcatenation(var36.getTransform());
            if (!checkBoundsInQuad(var7, var2, TEMP_TRANSFORM, var5)) {
               return NGNode.RenderRootResult.NO_RENDER_ROOT;
            }
         }

         double var37 = var4.getMxx();
         double var8 = var4.getMxy();
         double var10 = var4.getMxz();
         double var12 = var4.getMxt();
         double var14 = var4.getMyx();
         double var16 = var4.getMyy();
         double var18 = var4.getMyz();
         double var20 = var4.getMyt();
         double var22 = var4.getMzx();
         double var24 = var4.getMzy();
         double var26 = var4.getMzz();
         double var28 = var4.getMzt();
         BaseTransform var30 = var4.deriveWithConcatenation(this.getTransform());
         NGNode.RenderRootResult var31 = NGNode.RenderRootResult.NO_RENDER_ROOT;
         boolean var32 = true;
         List var33 = this.getOrderedChildren();

         for (int var34 = var33.size() - 1; var34 >= 0; var34--) {
            NGNode var35 = (NGNode)var33.get(var34);
            var31 = var35.computeRenderRoot(var1, var2, var3, var30, var5);
            var32 &= var35.isClean();
            if (var31 == NGNode.RenderRootResult.HAS_RENDER_ROOT) {
               var1.add(this);
               break;
            }

            if (var31 == NGNode.RenderRootResult.HAS_RENDER_ROOT_AND_IS_CLEAN) {
               var1.add(this);
               if (!var32) {
                  var31 = NGNode.RenderRootResult.HAS_RENDER_ROOT;
               }
               break;
            }
         }

         var4.restoreTransform(var37, var8, var10, var12, var14, var16, var18, var20, var22, var24, var26, var28);
         return var31;
      } else {
         return NGNode.RenderRootResult.NO_RENDER_ROOT;
      }
   }

   @Override
   protected void markCullRegions(DirtyRegionContainer var1, int var2, BaseTransform var3, GeneralTransform3D var4) {
      super.markCullRegions(var1, var2, var3, var4);
      if (this.cullingBits == -1 || this.cullingBits != 0 && (this.cullingBits & 357913941) != 0) {
         double var5 = var3.getMxx();
         double var7 = var3.getMxy();
         double var9 = var3.getMxz();
         double var11 = var3.getMxt();
         double var13 = var3.getMyx();
         double var15 = var3.getMyy();
         double var17 = var3.getMyz();
         double var19 = var3.getMyt();
         double var21 = var3.getMzx();
         double var23 = var3.getMzy();
         double var25 = var3.getMzz();
         double var27 = var3.getMzt();
         BaseTransform var29 = var3.deriveWithConcatenation(this.getTransform());
         List var31 = this.getOrderedChildren();

         for (int var32 = 0; var32 < var31.size(); var32++) {
            NGNode var30 = (NGNode)var31.get(var32);
            var30.markCullRegions(var1, this.cullingBits, var29, var4);
         }

         var3.restoreTransform(var5, var7, var9, var11, var13, var15, var17, var19, var21, var23, var25, var27);
      }
   }

   @Override
   public void drawDirtyOpts(BaseTransform var1, GeneralTransform3D var2, Rectangle var3, int[] var4, int var5) {
      super.drawDirtyOpts(var1, var2, var3, var4, var5);
      BaseTransform var6 = var1.copy();
      var6 = var6.deriveWithConcatenation(this.getTransform());
      List var7 = this.getOrderedChildren();

      for (int var8 = 0; var8 < var7.size(); var8++) {
         NGNode var9 = (NGNode)var7.get(var8);
         var9.drawDirtyOpts(var6, var2, var3, var4, var5);
      }
   }
}
