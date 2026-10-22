package javafx.scene.shape;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.Area;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public abstract class Shape extends Node {
   private NGShape.Mode mode = NGShape.Mode.FILL;
   private ObjectProperty<Paint> fill;
   Paint old_fill;
   private ObjectProperty<Paint> stroke;
   private final AbstractNotifyListener platformImageChangeListener = new AbstractNotifyListener() {
      public void invalidated(Observable var1) {
         NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_FILL);
         NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKE);
         NodeHelper.geomChanged(Shape.this);
         Shape.this.checkModeChanged();
      }
   };
   Paint old_stroke;
   private BooleanProperty smooth;
   private static final double MIN_STROKE_WIDTH = 0.0;
   private static final double MIN_STROKE_MITER_LIMIT = 1.0;
   private Reference<Runnable> shapeChangeListener;
   private boolean strokeAttributesDirty = true;
   private Shape.StrokeAttributes strokeAttributes;
   private static final StrokeType DEFAULT_STROKE_TYPE = StrokeType.CENTERED;
   private static final double DEFAULT_STROKE_WIDTH = 1.0;
   private static final StrokeLineJoin DEFAULT_STROKE_LINE_JOIN = StrokeLineJoin.MITER;
   private static final StrokeLineCap DEFAULT_STROKE_LINE_CAP = StrokeLineCap.SQUARE;
   private static final double DEFAULT_STROKE_MITER_LIMIT = 10.0;
   private static final double DEFAULT_STROKE_DASH_OFFSET = 0.0;
   private static final float[] DEFAULT_PG_STROKE_DASH_ARRAY = new float[0];

   StrokeLineJoin convertLineJoin(StrokeLineJoin var1) {
      return var1;
   }

   public final void setStrokeType(StrokeType var1) {
      this.strokeTypeProperty().set(var1);
   }

   public final StrokeType getStrokeType() {
      return this.strokeAttributes == null ? DEFAULT_STROKE_TYPE : this.strokeAttributes.getType();
   }

   public final ObjectProperty<StrokeType> strokeTypeProperty() {
      return this.getStrokeAttributes().typeProperty();
   }

   public final void setStrokeWidth(double var1) {
      this.strokeWidthProperty().set(var1);
   }

   public final double getStrokeWidth() {
      return this.strokeAttributes == null ? 1.0 : this.strokeAttributes.getWidth();
   }

   public final DoubleProperty strokeWidthProperty() {
      return this.getStrokeAttributes().widthProperty();
   }

   public final void setStrokeLineJoin(StrokeLineJoin var1) {
      this.strokeLineJoinProperty().set(var1);
   }

   public final StrokeLineJoin getStrokeLineJoin() {
      return this.strokeAttributes == null ? DEFAULT_STROKE_LINE_JOIN : this.strokeAttributes.getLineJoin();
   }

   public final ObjectProperty<StrokeLineJoin> strokeLineJoinProperty() {
      return this.getStrokeAttributes().lineJoinProperty();
   }

   public final void setStrokeLineCap(StrokeLineCap var1) {
      this.strokeLineCapProperty().set(var1);
   }

   public final StrokeLineCap getStrokeLineCap() {
      return this.strokeAttributes == null ? DEFAULT_STROKE_LINE_CAP : this.strokeAttributes.getLineCap();
   }

   public final ObjectProperty<StrokeLineCap> strokeLineCapProperty() {
      return this.getStrokeAttributes().lineCapProperty();
   }

   public final void setStrokeMiterLimit(double var1) {
      this.strokeMiterLimitProperty().set(var1);
   }

   public final double getStrokeMiterLimit() {
      return this.strokeAttributes == null ? 10.0 : this.strokeAttributes.getMiterLimit();
   }

   public final DoubleProperty strokeMiterLimitProperty() {
      return this.getStrokeAttributes().miterLimitProperty();
   }

   public final void setStrokeDashOffset(double var1) {
      this.strokeDashOffsetProperty().set(var1);
   }

   public final double getStrokeDashOffset() {
      return this.strokeAttributes == null ? 0.0 : this.strokeAttributes.getDashOffset();
   }

   public final DoubleProperty strokeDashOffsetProperty() {
      return this.getStrokeAttributes().dashOffsetProperty();
   }

   public final ObservableList<Double> getStrokeDashArray() {
      return this.getStrokeAttributes().dashArrayProperty();
   }

   private NGShape.Mode computeMode() {
      if (this.getFill() != null && this.getStroke() != null) {
         return NGShape.Mode.STROKE_FILL;
      } else if (this.getFill() != null) {
         return NGShape.Mode.FILL;
      } else {
         return this.getStroke() != null ? NGShape.Mode.STROKE : NGShape.Mode.EMPTY;
      }
   }

   NGShape.Mode getMode() {
      return this.mode;
   }

   void setMode(NGShape.Mode var1) {
   }

   private void checkModeChanged() {
      NGShape.Mode var1 = this.computeMode();
      if (this.mode != var1) {
         this.mode = var1;
         NodeHelper.markDirty(this, DirtyBits.SHAPE_MODE);
         NodeHelper.geomChanged(this);
      }
   }

   public final void setFill(Paint var1) {
      this.fillProperty().set(var1);
   }

   public final Paint getFill() {
      return (Paint)(this.fill == null ? Color.BLACK : (Paint)this.fill.get());
   }

   public final ObjectProperty<Paint> fillProperty() {
      if (this.fill == null) {
         this.fill = new StyleableObjectProperty<Paint>(Color.BLACK) {
            boolean needsListener = false;

            public void invalidated() {
               Paint var1 = (Paint)this.get();
               if (this.needsListener) {
                  Toolkit.getPaintAccessor().removeListener(Shape.this.old_fill, Shape.this.platformImageChangeListener);
               }

               this.needsListener = var1 != null && Toolkit.getPaintAccessor().isMutable(var1);
               Shape.this.old_fill = var1;
               if (this.needsListener) {
                  Toolkit.getPaintAccessor().addListener(var1, Shape.this.platformImageChangeListener);
               }

               NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_FILL);
               Shape.this.checkModeChanged();
            }

            @Override
            public CssMetaData<Shape, Paint> getCssMetaData() {
               return Shape.StyleableProperties.FILL;
            }

            public Object getBean() {
               return Shape.this;
            }

            public String getName() {
               return "fill";
            }
         };
      }

      return this.fill;
   }

   public final void setStroke(Paint var1) {
      this.strokeProperty().set(var1);
   }

   public final Paint getStroke() {
      return this.stroke == null ? null : (Paint)this.stroke.get();
   }

   public final ObjectProperty<Paint> strokeProperty() {
      if (this.stroke == null) {
         this.stroke = new StyleableObjectProperty<Paint>() {
            boolean needsListener = false;

            public void invalidated() {
               Paint var1 = (Paint)this.get();
               if (this.needsListener) {
                  Toolkit.getPaintAccessor().removeListener(Shape.this.old_stroke, Shape.this.platformImageChangeListener);
               }

               this.needsListener = var1 != null && Toolkit.getPaintAccessor().isMutable(var1);
               Shape.this.old_stroke = var1;
               if (this.needsListener) {
                  Toolkit.getPaintAccessor().addListener(var1, Shape.this.platformImageChangeListener);
               }

               NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKE);
               Shape.this.checkModeChanged();
            }

            @Override
            public CssMetaData<Shape, Paint> getCssMetaData() {
               return Shape.StyleableProperties.STROKE;
            }

            public Object getBean() {
               return Shape.this;
            }

            public String getName() {
               return "stroke";
            }
         };
      }

      return this.stroke;
   }

   public final void setSmooth(boolean var1) {
      this.smoothProperty().set(var1);
   }

   public final boolean isSmooth() {
      return this.smooth == null ? true : this.smooth.get();
   }

   public final BooleanProperty smoothProperty() {
      if (this.smooth == null) {
         this.smooth = new StyleableBooleanProperty(true) {
            public void invalidated() {
               NodeHelper.markDirty(Shape.this, DirtyBits.NODE_SMOOTH);
            }

            @Override
            public CssMetaData<Shape, Boolean> getCssMetaData() {
               return Shape.StyleableProperties.SMOOTH;
            }

            public Object getBean() {
               return Shape.this;
            }

            public String getName() {
               return "smooth";
            }
         };
      }

      return this.smooth;
   }

   private Paint doCssGetFillInitialValue() {
      return Color.BLACK;
   }

   private Paint doCssGetStrokeInitialValue() {
      return null;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Shape.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      return this.computeShapeBounds(var1, var2, ShapeHelper.configShape(this));
   }

   private boolean doComputeContains(double var1, double var3) {
      return this.computeShapeContains(var1, var3, ShapeHelper.configShape(this));
   }

   private void updatePGShape() {
      NGShape var1 = NodeHelper.getPeer(this);
      if (this.strokeAttributesDirty && this.getStroke() != null) {
         float[] var2 = this.hasStrokeDashArray() ? toPGDashArray(this.getStrokeDashArray()) : DEFAULT_PG_STROKE_DASH_ARRAY;
         var1.setDrawStroke(
            (float)Utils.clampMin(this.getStrokeWidth(), 0.0),
            this.getStrokeType(),
            this.getStrokeLineCap(),
            this.convertLineJoin(this.getStrokeLineJoin()),
            (float)Utils.clampMin(this.getStrokeMiterLimit(), 1.0),
            var2,
            (float)this.getStrokeDashOffset()
         );
         this.strokeAttributesDirty = false;
      }

      if (NodeHelper.isDirty(this, DirtyBits.SHAPE_MODE)) {
         var1.setMode(this.mode);
      }

      if (NodeHelper.isDirty(this, DirtyBits.SHAPE_FILL)) {
         Paint var3 = this.getFill();
         var1.setFillPaint(var3 == null ? null : Toolkit.getPaintAccessor().getPlatformPaint(var3));
      }

      if (NodeHelper.isDirty(this, DirtyBits.SHAPE_STROKE)) {
         Paint var4 = this.getStroke();
         var1.setDrawPaint(var4 == null ? null : Toolkit.getPaintAccessor().getPlatformPaint(var4));
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_SMOOTH)) {
         var1.setSmooth(this.isSmooth());
      }
   }

   private void doMarkDirty(DirtyBits var1) {
      Runnable var2 = this.shapeChangeListener != null ? this.shapeChangeListener.get() : null;
      if (var2 != null && NodeHelper.isDirtyEmpty(this)) {
         var2.run();
      }
   }

   void setShapeChangeListener(Runnable var1) {
      if (this.shapeChangeListener != null) {
         this.shapeChangeListener.clear();
      }

      this.shapeChangeListener = var1 != null ? new WeakReference<>(var1) : null;
   }

   private void doUpdatePeer() {
      this.updatePGShape();
   }

   BaseBounds computeBounds(BaseBounds var1, BaseTransform var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      if (!(var11 < 0.0) && !(var13 < 0.0)) {
         double var15 = var7;
         double var17 = var9;
         double var23 = var5;
         double var19;
         double var21;
         if (var2.isTranslateOrIdentity()) {
            var19 = var11 + var7;
            var21 = var13 + var9;
            if (var2.getType() == 1) {
               double var25 = var2.getMxt();
               double var27 = var2.getMyt();
               var15 = var7 + var25;
               var17 = var9 + var27;
               var19 += var25;
               var21 += var27;
            }

            var23 = var5 + var3;
         } else {
            var15 = var7 - var3;
            var17 = var9 - var3;
            var19 = var11 + var3 * 2.0;
            var21 = var13 + var3 * 2.0;
            double var45 = var2.getMxx();
            double var47 = var2.getMxy();
            double var29 = var2.getMyx();
            double var31 = var2.getMyy();
            double var33 = var15 * var45 + var17 * var47 + var2.getMxt();
            double var35 = var15 * var29 + var17 * var31 + var2.getMyt();
            var45 *= var19;
            var47 *= var21;
            var29 *= var19;
            var31 *= var21;
            var15 = Math.min(Math.min(0.0, var45), Math.min(var47, var45 + var47)) + var33;
            var17 = Math.min(Math.min(0.0, var29), Math.min(var31, var29 + var31)) + var35;
            var19 = Math.max(Math.max(0.0, var45), Math.max(var47, var45 + var47)) + var33;
            var21 = Math.max(Math.max(0.0, var29), Math.max(var31, var29 + var31)) + var35;
         }

         var15 -= var23;
         var17 -= var23;
         var19 += var23;
         var21 += var23;
         return var1.deriveWithNewBounds((float)var15, (float)var17, 0.0F, (float)var19, (float)var21, 0.0F);
      } else {
         return var1.makeEmpty();
      }
   }

   BaseBounds computeShapeBounds(BaseBounds var1, BaseTransform var2, com.sun.javafx.geom.Shape var3) {
      if (this.mode == NGShape.Mode.EMPTY) {
         return var1.makeEmpty();
      } else {
         float[] var4 = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
         boolean var5 = this.mode != NGShape.Mode.STROKE;
         boolean var6 = this.mode != NGShape.Mode.FILL;
         if (var6 && this.getStrokeType() == StrokeType.INSIDE) {
            var5 = true;
            var6 = false;
         }

         if (var6) {
            StrokeType var7 = this.getStrokeType();
            double var8 = Utils.clampMin(this.getStrokeWidth(), 0.0);
            StrokeLineCap var10 = this.getStrokeLineCap();
            StrokeLineJoin var11 = this.convertLineJoin(this.getStrokeLineJoin());
            float var12 = (float)Utils.clampMin(this.getStrokeMiterLimit(), 1.0);
            Toolkit.getToolkit().accumulateStrokeBounds(var3, var4, var7, var8, var10, var11, var12, var2);
            var4[0] = (float)((double)var4[0] - 0.5);
            var4[1] = (float)((double)var4[1] - 0.5);
            var4[2] = (float)((double)var4[2] + 0.5);
            var4[3] = (float)((double)var4[3] + 0.5);
         } else if (var5) {
            com.sun.javafx.geom.Shape.accumulate(var4, var3, var2);
         }

         return !(var4[2] < var4[0]) && !(var4[3] < var4[1]) ? var1.deriveWithNewBounds(var4[0], var4[1], 0.0F, var4[2], var4[3], 0.0F) : var1.makeEmpty();
      }
   }

   boolean computeShapeContains(double var1, double var3, com.sun.javafx.geom.Shape var5) {
      if (this.mode == NGShape.Mode.EMPTY) {
         return false;
      } else {
         boolean var6 = this.mode != NGShape.Mode.STROKE;
         boolean var7 = this.mode != NGShape.Mode.FILL;
         if (var7 && var6 && this.getStrokeType() == StrokeType.INSIDE) {
            var7 = false;
         }

         if (var6 && var5.contains((float)var1, (float)var3)) {
            return true;
         } else if (var7) {
            StrokeType var8 = this.getStrokeType();
            double var9 = Utils.clampMin(this.getStrokeWidth(), 0.0);
            StrokeLineCap var11 = this.getStrokeLineCap();
            StrokeLineJoin var12 = this.convertLineJoin(this.getStrokeLineJoin());
            float var13 = (float)Utils.clampMin(this.getStrokeMiterLimit(), 1.0);
            return Toolkit.getToolkit().strokeContains(var5, var1, var3, var8, var9, var11, var12, var13);
         } else {
            return false;
         }
      }
   }

   private Shape.StrokeAttributes getStrokeAttributes() {
      if (this.strokeAttributes == null) {
         this.strokeAttributes = new Shape.StrokeAttributes();
      }

      return this.strokeAttributes;
   }

   private boolean hasStrokeDashArray() {
      return this.strokeAttributes != null && this.strokeAttributes.hasDashArray();
   }

   private static float[] toPGDashArray(List<Double> var0) {
      int var1 = var0.size();
      float[] var2 = new float[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = ((Double)var0.get(var3)).floatValue();
      }

      return var2;
   }

   public static Shape union(Shape var0, Shape var1) {
      Area var2 = var0.getTransformedArea();
      var2.add(var1.getTransformedArea());
      return createFromGeomShape(var2);
   }

   public static Shape subtract(Shape var0, Shape var1) {
      Area var2 = var0.getTransformedArea();
      var2.subtract(var1.getTransformedArea());
      return createFromGeomShape(var2);
   }

   public static Shape intersect(Shape var0, Shape var1) {
      Area var2 = var0.getTransformedArea();
      var2.intersect(var1.getTransformedArea());
      return createFromGeomShape(var2);
   }

   private Area getTransformedArea() {
      return this.getTransformedArea(calculateNodeToSceneTransform(this));
   }

   private Area getTransformedArea(BaseTransform var1) {
      if (this.mode == NGShape.Mode.EMPTY) {
         return new Area();
      } else {
         com.sun.javafx.geom.Shape var2 = ShapeHelper.configShape(this);
         if (this.mode != NGShape.Mode.FILL && (this.mode != NGShape.Mode.STROKE_FILL || this.getStrokeType() != StrokeType.INSIDE)) {
            StrokeType var3 = this.getStrokeType();
            double var4 = Utils.clampMin(this.getStrokeWidth(), 0.0);
            StrokeLineCap var6 = this.getStrokeLineCap();
            StrokeLineJoin var7 = this.convertLineJoin(this.getStrokeLineJoin());
            float var8 = (float)Utils.clampMin(this.getStrokeMiterLimit(), 1.0);
            float[] var9 = this.hasStrokeDashArray() ? toPGDashArray(this.getStrokeDashArray()) : DEFAULT_PG_STROKE_DASH_ARRAY;
            com.sun.javafx.geom.Shape var10 = Toolkit.getToolkit()
               .createStrokedShape(var2, var3, var4, var6, var7, var8, var9, (float)this.getStrokeDashOffset());
            if (this.mode == NGShape.Mode.STROKE) {
               return createTransformedArea(var10, var1);
            } else {
               Area var11 = new Area(var2);
               var11.add(new Area(var10));
               return createTransformedArea(var11, var1);
            }
         } else {
            return createTransformedArea(var2, var1);
         }
      }
   }

   private static BaseTransform calculateNodeToSceneTransform(Node var0) {
      Affine3D var1 = new Affine3D();

      do {
         var1.preConcatenate(NodeHelper.getLeafTransform((Node)var0));
         var0 = ((Node)var0).getParent();
      } while (var0 != null);

      return var1;
   }

   private static Area createTransformedArea(com.sun.javafx.geom.Shape var0, BaseTransform var1) {
      return var1.isIdentity() ? new Area(var0) : new Area(var0.getPathIterator(var1));
   }

   private static Path createFromGeomShape(com.sun.javafx.geom.Shape var0) {
      Path var1 = new Path();
      ObservableList var2 = var1.getElements();
      PathIterator var3 = var0.getPathIterator(null);

      for (float[] var4 = new float[6]; !var3.isDone(); var3.next()) {
         int var5 = var3.currentSegment(var4);
         switch (var5) {
            case 0:
               var2.add(new MoveTo((double)var4[0], (double)var4[1]));
               break;
            case 1:
               var2.add(new LineTo((double)var4[0], (double)var4[1]));
               break;
            case 2:
               var2.add(new QuadCurveTo((double)var4[0], (double)var4[1], (double)var4[2], (double)var4[3]));
               break;
            case 3:
               var2.add(new CubicCurveTo((double)var4[0], (double)var4[1], (double)var4[2], (double)var4[3], (double)var4[4], (double)var4[5]));
               break;
            case 4:
               var2.add(new ClosePath());
         }
      }

      var1.setFillRule(var3.getWindingRule() == 0 ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
      var1.setFill(Color.BLACK);
      var1.setStroke(null);
      return var1;
   }

   static {
      ShapeHelper.setShapeAccessor(new ShapeHelper.ShapeAccessor() {
         @Override
         public void doUpdatePeer(Node var1) {
            ((Shape)var1).doUpdatePeer();
         }

         @Override
         public void doMarkDirty(Node var1, DirtyBits var2) {
            ((Shape)var1).doMarkDirty(var2);
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Shape)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Shape)var1).doComputeContains(var2, var4);
         }

         @Override
         public Paint doCssGetFillInitialValue(Shape var1) {
            return var1.doCssGetFillInitialValue();
         }

         @Override
         public Paint doCssGetStrokeInitialValue(Shape var1) {
            return var1.doCssGetStrokeInitialValue();
         }

         @Override
         public NGShape.Mode getMode(Shape var1) {
            return var1.getMode();
         }

         @Override
         public void setMode(Shape var1, NGShape.Mode var2) {
            var1.setMode(var2);
         }

         @Override
         public void setShapeChangeListener(Shape var1, Runnable var2) {
            var1.setShapeChangeListener(var2);
         }
      });
   }

   private final class StrokeAttributes {
      private ObjectProperty<StrokeType> type;
      private DoubleProperty width;
      private ObjectProperty<StrokeLineJoin> lineJoin;
      private ObjectProperty<StrokeLineCap> lineCap;
      private DoubleProperty miterLimit;
      private DoubleProperty dashOffset;
      private ObservableList<Double> dashArray;
      private ObjectProperty<Number[]> cssDashArray = null;

      public final StrokeType getType() {
         return this.type == null ? Shape.DEFAULT_STROKE_TYPE : (StrokeType)this.type.get();
      }

      public final ObjectProperty<StrokeType> typeProperty() {
         if (this.type == null) {
            this.type = new StyleableObjectProperty<StrokeType>(Shape.DEFAULT_STROKE_TYPE) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_TYPE);
               }

               @Override
               public CssMetaData<Shape, StrokeType> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_TYPE;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeType";
               }
            };
         }

         return this.type;
      }

      public double getWidth() {
         return this.width == null ? 1.0 : this.width.get();
      }

      public final DoubleProperty widthProperty() {
         if (this.width == null) {
            this.width = new StyleableDoubleProperty(1.0) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_WIDTH);
               }

               @Override
               public CssMetaData<Shape, Number> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_WIDTH;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeWidth";
               }
            };
         }

         return this.width;
      }

      public StrokeLineJoin getLineJoin() {
         return this.lineJoin == null ? Shape.DEFAULT_STROKE_LINE_JOIN : (StrokeLineJoin)this.lineJoin.get();
      }

      public final ObjectProperty<StrokeLineJoin> lineJoinProperty() {
         if (this.lineJoin == null) {
            this.lineJoin = new StyleableObjectProperty<StrokeLineJoin>(Shape.DEFAULT_STROKE_LINE_JOIN) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_LINE_JOIN);
               }

               @Override
               public CssMetaData<Shape, StrokeLineJoin> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_LINE_JOIN;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeLineJoin";
               }
            };
         }

         return this.lineJoin;
      }

      public StrokeLineCap getLineCap() {
         return this.lineCap == null ? Shape.DEFAULT_STROKE_LINE_CAP : (StrokeLineCap)this.lineCap.get();
      }

      public final ObjectProperty<StrokeLineCap> lineCapProperty() {
         if (this.lineCap == null) {
            this.lineCap = new StyleableObjectProperty<StrokeLineCap>(Shape.DEFAULT_STROKE_LINE_CAP) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_LINE_CAP);
               }

               @Override
               public CssMetaData<Shape, StrokeLineCap> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_LINE_CAP;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeLineCap";
               }
            };
         }

         return this.lineCap;
      }

      public double getMiterLimit() {
         return this.miterLimit == null ? 10.0 : this.miterLimit.get();
      }

      public final DoubleProperty miterLimitProperty() {
         if (this.miterLimit == null) {
            this.miterLimit = new StyleableDoubleProperty(10.0) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_MITER_LIMIT);
               }

               @Override
               public CssMetaData<Shape, Number> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_MITER_LIMIT;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeMiterLimit";
               }
            };
         }

         return this.miterLimit;
      }

      public double getDashOffset() {
         return this.dashOffset == null ? 0.0 : this.dashOffset.get();
      }

      public final DoubleProperty dashOffsetProperty() {
         if (this.dashOffset == null) {
            this.dashOffset = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_DASH_OFFSET);
               }

               @Override
               public CssMetaData<Shape, Number> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_DASH_OFFSET;
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "strokeDashOffset";
               }
            };
         }

         return this.dashOffset;
      }

      public ObservableList<Double> dashArrayProperty() {
         if (this.dashArray == null) {
            this.dashArray = new TrackableObservableList<Double>() {
               protected void onChanged(Change<Double> var1) {
                  StrokeAttributes.this.invalidated(Shape.StyleableProperties.STROKE_DASH_ARRAY);
               }
            };
         }

         return this.dashArray;
      }

      private ObjectProperty<Number[]> cssDashArrayProperty() {
         if (this.cssDashArray == null) {
            this.cssDashArray = new StyleableObjectProperty<Number[]>() {
               public void set(Number[] var1) {
                  ObservableList var2 = StrokeAttributes.this.dashArrayProperty();
                  var2.clear();
                  if (var1 != null && var1.length > 0) {
                     for (int var3 = 0; var3 < var1.length; var3++) {
                        var2.add(var1[var3].doubleValue());
                     }
                  }
               }

               public Double[] get() {
                  ObservableList var1 = StrokeAttributes.this.dashArrayProperty();
                  return var1.toArray(new Double[var1.size()]);
               }

               public Object getBean() {
                  return Shape.this;
               }

               public String getName() {
                  return "cssDashArray";
               }

               @Override
               public CssMetaData<Shape, Number[]> getCssMetaData() {
                  return Shape.StyleableProperties.STROKE_DASH_ARRAY;
               }
            };
         }

         return this.cssDashArray;
      }

      public boolean canSetType() {
         return this.type == null || !this.type.isBound();
      }

      public boolean canSetWidth() {
         return this.width == null || !this.width.isBound();
      }

      public boolean canSetLineJoin() {
         return this.lineJoin == null || !this.lineJoin.isBound();
      }

      public boolean canSetLineCap() {
         return this.lineCap == null || !this.lineCap.isBound();
      }

      public boolean canSetMiterLimit() {
         return this.miterLimit == null || !this.miterLimit.isBound();
      }

      public boolean canSetDashOffset() {
         return this.dashOffset == null || !this.dashOffset.isBound();
      }

      public boolean hasDashArray() {
         return this.dashArray != null;
      }

      private void invalidated(CssMetaData<Shape, ?> var1) {
         NodeHelper.markDirty(Shape.this, DirtyBits.SHAPE_STROKEATTRS);
         Shape.this.strokeAttributesDirty = true;
         if (var1 != Shape.StyleableProperties.STROKE_DASH_OFFSET) {
            NodeHelper.geomChanged(Shape.this);
         }
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<Shape, Paint> FILL = new CssMetaData<Shape, Paint>("-fx-fill", PaintConverter.getInstance(), Color.BLACK) {
         public boolean isSettable(Shape var1) {
            return var1.fill == null || !var1.fill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Paint>)var1.fillProperty();
         }

         public Paint getInitialValue(Shape var1) {
            return ShapeHelper.cssGetFillInitialValue(var1);
         }
      };
      private static final CssMetaData<Shape, Boolean> SMOOTH = new CssMetaData<Shape, Boolean>("-fx-smooth", BooleanConverter.getInstance(), Boolean.TRUE) {
         public boolean isSettable(Shape var1) {
            return var1.smooth == null || !var1.smooth.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Boolean>)var1.smoothProperty();
         }
      };
      private static final CssMetaData<Shape, Paint> STROKE = new CssMetaData<Shape, Paint>("-fx-stroke", PaintConverter.getInstance()) {
         public boolean isSettable(Shape var1) {
            return var1.stroke == null || !var1.stroke.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Paint>)var1.strokeProperty();
         }

         public Paint getInitialValue(Shape var1) {
            return ShapeHelper.cssGetStrokeInitialValue(var1);
         }
      };
      private static final CssMetaData<Shape, Number[]> STROKE_DASH_ARRAY = new CssMetaData<Shape, Number[]>(
         "-fx-stroke-dash-array", SizeConverter.SequenceConverter.getInstance(), new Double[0]
      ) {
         public boolean isSettable(Shape var1) {
            return true;
         }

         public StyleableProperty<Number[]> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Number[]>)var1.getStrokeAttributes().cssDashArrayProperty();
         }
      };
      private static final CssMetaData<Shape, Number> STROKE_DASH_OFFSET = new CssMetaData<Shape, Number>(
         "-fx-stroke-dash-offset", SizeConverter.getInstance(), 0.0
      ) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetDashOffset();
         }

         public StyleableProperty<Number> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Number>)var1.strokeDashOffsetProperty();
         }
      };
      private static final CssMetaData<Shape, StrokeLineCap> STROKE_LINE_CAP = new CssMetaData<Shape, StrokeLineCap>(
         "-fx-stroke-line-cap", new EnumConverter<>(StrokeLineCap.class), StrokeLineCap.SQUARE
      ) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetLineCap();
         }

         public StyleableProperty<StrokeLineCap> getStyleableProperty(Shape var1) {
            return (StyleableProperty<StrokeLineCap>)var1.strokeLineCapProperty();
         }
      };
      private static final CssMetaData<Shape, StrokeLineJoin> STROKE_LINE_JOIN = new CssMetaData<Shape, StrokeLineJoin>(
         "-fx-stroke-line-join", new EnumConverter<>(StrokeLineJoin.class), StrokeLineJoin.MITER
      ) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetLineJoin();
         }

         public StyleableProperty<StrokeLineJoin> getStyleableProperty(Shape var1) {
            return (StyleableProperty<StrokeLineJoin>)var1.strokeLineJoinProperty();
         }
      };
      private static final CssMetaData<Shape, StrokeType> STROKE_TYPE = new CssMetaData<Shape, StrokeType>(
         "-fx-stroke-type", new EnumConverter<>(StrokeType.class), StrokeType.CENTERED
      ) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetType();
         }

         public StyleableProperty<StrokeType> getStyleableProperty(Shape var1) {
            return (StyleableProperty<StrokeType>)var1.strokeTypeProperty();
         }
      };
      private static final CssMetaData<Shape, Number> STROKE_MITER_LIMIT = new CssMetaData<Shape, Number>(
         "-fx-stroke-miter-limit", SizeConverter.getInstance(), 10.0
      ) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetMiterLimit();
         }

         public StyleableProperty<Number> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Number>)var1.strokeMiterLimitProperty();
         }
      };
      private static final CssMetaData<Shape, Number> STROKE_WIDTH = new CssMetaData<Shape, Number>("-fx-stroke-width", SizeConverter.getInstance(), 1.0) {
         public boolean isSettable(Shape var1) {
            return var1.strokeAttributes == null || var1.strokeAttributes.canSetWidth();
         }

         public StyleableProperty<Number> getStyleableProperty(Shape var1) {
            return (StyleableProperty<Number>)var1.strokeWidthProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Node.getClassCssMetaData());
         var0.add(FILL);
         var0.add(SMOOTH);
         var0.add(STROKE);
         var0.add(STROKE_DASH_ARRAY);
         var0.add(STROKE_DASH_OFFSET);
         var0.add(STROKE_LINE_CAP);
         var0.add(STROKE_LINE_JOIN);
         var0.add(STROKE_TYPE);
         var0.add(STROKE_MITER_LIMIT);
         var0.add(STROKE_WIDTH);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
