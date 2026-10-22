package javafx.scene.text;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLayoutFactory;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.PathElement;

public class TextFlow extends Pane {
   private TextLayout layout;
   private boolean needsContent;
   private boolean inLayout;
   private ObjectProperty<TextAlignment> textAlignment;
   private DoubleProperty lineSpacing;
   private IntegerProperty tabSize;

   public TextFlow() {
      this.effectiveNodeOrientationProperty().addListener(var1 -> this.checkOrientation());
      this.setAccessibleRole(AccessibleRole.TEXT);
   }

   public TextFlow(Node... var1) {
      this();
      this.getChildren().addAll(var1);
   }

   private void checkOrientation() {
      NodeOrientation var1 = this.getEffectiveNodeOrientation();
      boolean var2 = var1 == NodeOrientation.RIGHT_TO_LEFT;
      int var3 = var2 ? 2048 : 1024;
      TextLayout var4 = this.getTextLayout();
      if (var4.setDirection(var3)) {
         this.requestLayout();
      }
   }

   public final HitInfo hitTest(Point2D var1) {
      if (var1 != null) {
         TextLayout var2 = this.getTextLayout();
         double var3 = var1.getX();
         double var5 = var1.getY();
         TextLayout.Hit var7 = var2.getHitInfo((float)var3, (float)var5);
         return new HitInfo(var7.getCharIndex(), var7.getInsertionIndex(), var7.isLeading(), null);
      } else {
         return null;
      }
   }

   public PathElement[] caretShape(int var1, boolean var2) {
      return this.getTextLayout().getCaretShape(var1, var2, 0.0F, 0.0F);
   }

   public final PathElement[] rangeShape(int var1, int var2) {
      return this.getRange(var1, var2, 1);
   }

   @Override
   public boolean usesMirroring() {
      return false;
   }

   @Override
   protected void setWidth(double var1) {
      if (var1 != this.getWidth()) {
         TextLayout var3 = this.getTextLayout();
         Insets var4 = this.getInsets();
         double var5 = this.snapSpaceX(var4.getLeft());
         double var7 = this.snapSpaceX(var4.getRight());
         double var9 = Math.max(1.0, var1 - var5 - var7);
         var3.setWrapWidth((float)var9);
         super.setWidth(var1);
      }
   }

   @Override
   protected double computePrefWidth(double var1) {
      TextLayout var3 = this.getTextLayout();
      var3.setWrapWidth(0.0F);
      double var4 = (double)var3.getBounds().getWidth();
      Insets var6 = this.getInsets();
      double var7 = this.snapSpaceX(var6.getLeft());
      double var9 = this.snapSpaceX(var6.getRight());
      double var11 = Math.max(1.0, this.getWidth() - var7 - var9);
      var3.setWrapWidth((float)var11);
      return var7 + var4 + var9;
   }

   @Override
   protected double computePrefHeight(double var1) {
      TextLayout var3 = this.getTextLayout();
      Insets var4 = this.getInsets();
      double var5 = this.snapSpaceX(var4.getLeft());
      double var7 = this.snapSpaceX(var4.getRight());
      if (var1 == -1.0) {
         var3.setWrapWidth(0.0F);
      } else {
         double var9 = Math.max(1.0, var1 - var5 - var7);
         var3.setWrapWidth((float)var9);
      }

      double var17 = (double)var3.getBounds().getHeight();
      double var11 = Math.max(1.0, this.getWidth() - var5 - var7);
      var3.setWrapWidth((float)var11);
      double var13 = this.snapSpaceY(var4.getTop());
      double var15 = this.snapSpaceY(var4.getBottom());
      return var13 + var17 + var15;
   }

   @Override
   protected double computeMinHeight(double var1) {
      return this.computePrefHeight(var1);
   }

   @Override
   public void requestLayout() {
      if (!this.inLayout) {
         this.needsContent = true;
         super.requestLayout();
      }
   }

   @Override
   public Orientation getContentBias() {
      return Orientation.HORIZONTAL;
   }

   @Override
   protected void layoutChildren() {
      this.inLayout = true;
      Insets var1 = this.getInsets();
      double var2 = this.snapSpaceY(var1.getTop());
      double var4 = this.snapSpaceX(var1.getLeft());
      GlyphList[] var6 = this.getTextLayout().getRuns();

      for (int var7 = 0; var7 < var6.length; var7++) {
         GlyphList var8 = var6[var7];
         TextSpan var9 = var8.getTextSpan();
         if (var9 instanceof TextFlow.EmbeddedSpan) {
            Node var10 = ((TextFlow.EmbeddedSpan)var9).getNode();
            com.sun.javafx.geom.Point2D var11 = var8.getLocation();
            double var12 = (double)(-var8.getLineBounds().getMinY());
            this.layoutInArea(
               var10,
               var4 + (double)var11.x,
               var2 + (double)var11.y,
               (double)var8.getWidth(),
               (double)var8.getHeight(),
               var12,
               null,
               true,
               true,
               HPos.CENTER,
               VPos.BASELINE
            );
         }
      }

      for (Node var16 : this.getManagedChildren()) {
         if (var16 instanceof Text) {
            Text var17 = (Text)var16;
            var17.layoutSpan(var6);
            BaseBounds var18 = var17.getSpanBounds();
            var17.relocate(var4 + (double)var18.getMinX(), var2 + (double)var18.getMinY());
         }
      }

      this.inLayout = false;
   }

   private PathElement[] getRange(int var1, int var2, int var3) {
      TextLayout var4 = this.getTextLayout();
      return var4.getRange(var1, var2, var3, 0.0F, 0.0F);
   }

   TextLayout getTextLayout() {
      if (this.layout == null) {
         TextLayoutFactory var1 = Toolkit.getToolkit().getTextLayoutFactory();
         this.layout = var1.createLayout();
         this.layout.setTabSize(this.getTabSize());
         this.needsContent = true;
      }

      if (this.needsContent) {
         List var11 = this.getManagedChildren();
         TextSpan[] var2 = new TextSpan[var11.size()];

         for (int var3 = 0; var3 < var2.length; var3++) {
            Node var4 = (Node)var11.get(var3);
            if (var4 instanceof Text) {
               var2[var3] = ((Text)var4).getTextSpan();
            } else {
               double var5 = var4.getBaselineOffset();
               if (var5 == Double.NEGATIVE_INFINITY) {
                  var5 = var4.getLayoutBounds().getHeight();
               }

               double var7 = this.computeChildPrefAreaWidth(var4, null);
               double var9 = this.computeChildPrefAreaHeight(var4, null);
               var2[var3] = new TextFlow.EmbeddedSpan(var4, var5, var7, var9);
            }
         }

         this.layout.setContent(var2);
         this.needsContent = false;
      }

      return this.layout;
   }

   public final void setTextAlignment(TextAlignment var1) {
      this.textAlignmentProperty().set(var1);
   }

   public final TextAlignment getTextAlignment() {
      return this.textAlignment == null ? TextAlignment.LEFT : (TextAlignment)this.textAlignment.get();
   }

   public final ObjectProperty<TextAlignment> textAlignmentProperty() {
      if (this.textAlignment == null) {
         this.textAlignment = new StyleableObjectProperty<TextAlignment>(TextAlignment.LEFT) {
            public Object getBean() {
               return TextFlow.this;
            }

            public String getName() {
               return "textAlignment";
            }

            @Override
            public CssMetaData<TextFlow, TextAlignment> getCssMetaData() {
               return TextFlow.StyleableProperties.TEXT_ALIGNMENT;
            }

            public void invalidated() {
               TextAlignment var1 = (TextAlignment)this.get();
               if (var1 == null) {
                  var1 = TextAlignment.LEFT;
               }

               TextLayout var2 = TextFlow.this.getTextLayout();
               var2.setAlignment(var1.ordinal());
               TextFlow.this.requestLayout();
            }
         };
      }

      return this.textAlignment;
   }

   public final void setLineSpacing(double var1) {
      this.lineSpacingProperty().set(var1);
   }

   public final double getLineSpacing() {
      return this.lineSpacing == null ? 0.0 : this.lineSpacing.get();
   }

   public final DoubleProperty lineSpacingProperty() {
      if (this.lineSpacing == null) {
         this.lineSpacing = new StyleableDoubleProperty(0.0) {
            public Object getBean() {
               return TextFlow.this;
            }

            public String getName() {
               return "lineSpacing";
            }

            @Override
            public CssMetaData<TextFlow, Number> getCssMetaData() {
               return TextFlow.StyleableProperties.LINE_SPACING;
            }

            public void invalidated() {
               TextLayout var1 = TextFlow.this.getTextLayout();
               if (var1.setLineSpacing((float)this.get())) {
                  TextFlow.this.requestLayout();
               }
            }
         };
      }

      return this.lineSpacing;
   }

   public final IntegerProperty tabSizeProperty() {
      if (this.tabSize == null) {
         this.tabSize = new StyleableIntegerProperty(8) {
            public Object getBean() {
               return TextFlow.this;
            }

            public String getName() {
               return "tabSize";
            }

            @Override
            public CssMetaData getCssMetaData() {
               return TextFlow.StyleableProperties.TAB_SIZE;
            }

            protected void invalidated() {
               TextLayout var1 = TextFlow.this.getTextLayout();
               if (var1.setTabSize(this.get())) {
                  TextFlow.this.requestLayout();
               }
            }
         };
      }

      return this.tabSize;
   }

   public final int getTabSize() {
      return this.tabSize == null ? 8 : this.tabSize.get();
   }

   public final void setTabSize(int var1) {
      this.tabSizeProperty().set(var1);
   }

   @Override
   public final double getBaselineOffset() {
      Insets var1 = this.getInsets();
      double var2 = this.snapSpaceY(var1.getTop());
      return var2 - (double)this.getTextLayout().getBounds().getMinY();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TextFlow.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   static double boundedSize(double var0, double var2, double var4) {
      double var6 = var2 >= var0 ? var2 : var0;
      double var8 = var0 >= var4 ? var0 : var4;
      return var6 <= var8 ? var6 : var8;
   }

   @Override
   double computeChildPrefAreaWidth(Node var1, Insets var2) {
      return this.computeChildPrefAreaWidth(var1, var2, -1.0);
   }

   double computeChildPrefAreaWidth(Node var1, Insets var2, double var3) {
      double var5 = var2 != null ? this.snapSpaceY(var2.getTop()) : 0.0;
      double var7 = var2 != null ? this.snapSpaceY(var2.getBottom()) : 0.0;
      double var9 = var2 != null ? this.snapSpaceX(var2.getLeft()) : 0.0;
      double var11 = var2 != null ? this.snapSpaceX(var2.getRight()) : 0.0;
      double var13 = -1.0;
      if (var1.getContentBias() == Orientation.VERTICAL) {
         var13 = this.snapSizeY(boundedSize(var1.minHeight(-1.0), var3 != -1.0 ? var3 - var5 - var7 : var1.prefHeight(-1.0), var1.maxHeight(-1.0)));
      }

      return var9 + this.snapSizeX(boundedSize(var1.minWidth(var13), var1.prefWidth(var13), var1.maxWidth(var13))) + var11;
   }

   @Override
   double computeChildPrefAreaHeight(Node var1, Insets var2) {
      return this.computeChildPrefAreaHeight(var1, var2, -1.0);
   }

   double computeChildPrefAreaHeight(Node var1, Insets var2, double var3) {
      double var5 = var2 != null ? this.snapSpaceY(var2.getTop()) : 0.0;
      double var7 = var2 != null ? this.snapSpaceY(var2.getBottom()) : 0.0;
      double var9 = var2 != null ? this.snapSpaceX(var2.getLeft()) : 0.0;
      double var11 = var2 != null ? this.snapSpaceX(var2.getRight()) : 0.0;
      double var13 = -1.0;
      if (var1.getContentBias() == Orientation.HORIZONTAL) {
         var13 = this.snapSizeX(boundedSize(var1.minWidth(-1.0), var3 != -1.0 ? var3 - var9 - var11 : var1.prefWidth(-1.0), var1.maxWidth(-1.0)));
      }

      return var5 + this.snapSizeY(boundedSize(var1.minHeight(var13), var1.prefHeight(var13), var1.maxHeight(var13))) + var7;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case TEXT:
            String var3 = this.getAccessibleText();
            if (var3 != null && !var3.isEmpty()) {
               return var3;
            } else {
               StringBuilder var4 = new StringBuilder();

               for (Node var6 : this.getChildren()) {
                  Object var7 = var6.queryAccessibleAttribute(AccessibleAttribute.TEXT, var2);
                  if (var7 != null) {
                     var4.append(var7.toString());
                  }
               }

               return var4.toString();
            }
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   private static class EmbeddedSpan implements TextSpan {
      RectBounds bounds;
      Node node;

      public EmbeddedSpan(Node var1, double var2, double var4, double var6) {
         this.node = var1;
         this.bounds = new RectBounds(0.0F, (float)(-var2), (float)var4, (float)(var6 - var2));
      }

      @Override
      public String getText() {
         return "￼";
      }

      @Override
      public Object getFont() {
         return null;
      }

      @Override
      public RectBounds getBounds() {
         return this.bounds;
      }

      public Node getNode() {
         return this.node;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<TextFlow, TextAlignment> TEXT_ALIGNMENT = new CssMetaData<TextFlow, TextAlignment>(
         "-fx-text-alignment", new EnumConverter<>(TextAlignment.class), TextAlignment.LEFT
      ) {
         public boolean isSettable(TextFlow var1) {
            return var1.textAlignment == null || !var1.textAlignment.isBound();
         }

         public StyleableProperty<TextAlignment> getStyleableProperty(TextFlow var1) {
            return (StyleableProperty<TextAlignment>)var1.textAlignmentProperty();
         }
      };
      private static final CssMetaData<TextFlow, Number> LINE_SPACING = new CssMetaData<TextFlow, Number>("-fx-line-spacing", SizeConverter.getInstance(), 0) {
         public boolean isSettable(TextFlow var1) {
            return var1.lineSpacing == null || !var1.lineSpacing.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TextFlow var1) {
            return (StyleableProperty<Number>)var1.lineSpacingProperty();
         }
      };
      private static final CssMetaData<TextFlow, Number> TAB_SIZE = new CssMetaData<TextFlow, Number>("-fx-tab-size", SizeConverter.getInstance(), 8) {
         public boolean isSettable(TextFlow var1) {
            return var1.tabSize == null || !var1.tabSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TextFlow var1) {
            return (StyleableProperty<Number>)var1.tabSizeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Pane.getClassCssMetaData());
         var0.add(TEXT_ALIGNMENT);
         var0.add(LINE_SPACING);
         var0.add(TAB_SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
