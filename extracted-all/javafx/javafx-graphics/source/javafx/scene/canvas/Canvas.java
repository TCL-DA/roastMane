package javafx.scene.canvas;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.canvas.CanvasHelper;
import com.sun.javafx.sg.prism.GrowableDataBuffer;
import com.sun.javafx.sg.prism.NGCanvas;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;

public class Canvas extends Node {
   static final int DEFAULT_VAL_BUF_SIZE = 1024;
   static final int DEFAULT_OBJ_BUF_SIZE = 32;
   private static final int SIZE_HISTORY = 5;
   private GrowableDataBuffer current;
   private boolean rendererBehind;
   private int[] recentvalsizes;
   private int[] recentobjsizes;
   private int lastsizeindex;
   private GraphicsContext theContext;
   private DoubleProperty width;
   private DoubleProperty height;

   public Canvas() {
      this(0.0, 0.0);
   }

   public Canvas(double var1, double var3) {
      CanvasHelper.initHelper(this);
      this.recentvalsizes = new int[5];
      this.recentobjsizes = new int[5];
      this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
      this.setWidth(var1);
      this.setHeight(var3);
   }

   private static int max(int[] var0, int var1) {
      for (int var5 : var0) {
         if (var1 < var5) {
            var1 = var5;
         }
      }

      return var1;
   }

   GrowableDataBuffer getBuffer() {
      NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
      NodeHelper.markDirty(this, DirtyBits.NODE_FORCE_SYNC);
      if (this.current == null) {
         int var1 = max(this.recentvalsizes, 1024);
         int var2 = max(this.recentobjsizes, 32);
         this.current = GrowableDataBuffer.getBuffer(var1, var2);
         this.theContext.updateDimensions();
      }

      return this.current;
   }

   boolean isRendererFallingBehind() {
      return this.rendererBehind;
   }

   public GraphicsContext getGraphicsContext2D() {
      if (this.theContext == null) {
         this.theContext = new GraphicsContext(this);
      }

      return this.theContext;
   }

   public final void setWidth(double var1) {
      this.widthProperty().set(var1);
   }

   public final double getWidth() {
      return this.width == null ? 0.0 : this.width.get();
   }

   public final DoubleProperty widthProperty() {
      if (this.width == null) {
         this.width = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(Canvas.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(Canvas.this);
               if (Canvas.this.theContext != null) {
                  Canvas.this.theContext.updateDimensions();
               }
            }

            public Object getBean() {
               return Canvas.this;
            }

            public String getName() {
               return "width";
            }
         };
      }

      return this.width;
   }

   public final void setHeight(double var1) {
      this.heightProperty().set(var1);
   }

   public final double getHeight() {
      return this.height == null ? 0.0 : this.height.get();
   }

   public final DoubleProperty heightProperty() {
      if (this.height == null) {
         this.height = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(Canvas.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(Canvas.this);
               if (Canvas.this.theContext != null) {
                  Canvas.this.theContext.updateDimensions();
               }
            }

            public Object getBean() {
               return Canvas.this;
            }

            public String getName() {
               return "height";
            }
         };
      }

      return this.height;
   }

   private NGNode doCreatePeer() {
      return new NGCanvas();
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
         NGCanvas var1 = NodeHelper.getPeer(this);
         var1.updateBounds((float)this.getWidth(), (float)this.getHeight());
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
         NGCanvas var2 = NodeHelper.getPeer(this);
         if (this.current != null && !this.current.isEmpty()) {
            if (--this.lastsizeindex < 0) {
               this.lastsizeindex = 4;
            }

            this.recentvalsizes[this.lastsizeindex] = this.current.writeValuePosition();
            this.recentobjsizes[this.lastsizeindex] = this.current.writeObjectPosition();
            this.rendererBehind = var2.updateRendering(this.current);
            this.current = null;
         }
      }
   }

   private boolean doComputeContains(double var1, double var3) {
      double var5 = this.getWidth();
      double var7 = this.getHeight();
      return var5 > 0.0 && var7 > 0.0 && var1 >= 0.0 && var3 >= 0.0 && var1 < var5 && var3 < var7;
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      RectBounds var3 = new RectBounds(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
      return var2.transform(var3, var3);
   }

   static {
      CanvasHelper.setCanvasAccessor(new CanvasHelper.CanvasAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Canvas)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Canvas)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Canvas)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Canvas)var1).doComputeContains(var2, var4);
         }
      });
   }
}
