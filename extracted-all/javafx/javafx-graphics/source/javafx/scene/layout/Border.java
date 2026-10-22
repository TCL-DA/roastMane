package javafx.scene.layout;

import com.sun.javafx.UnmodifiableArrayList;
import com.sun.javafx.css.SubCssMetaData;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import com.sun.javafx.scene.layout.region.BorderImageWidthConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
import com.sun.javafx.scene.layout.region.SliceSequenceConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.URLConverter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public final class Border {
   static final CssMetaData<Node, Paint[]> BORDER_COLOR = new SubCssMetaData<>("-fx-border-color", LayeredBorderPaintConverter.getInstance());
   static final CssMetaData<Node, BorderStrokeStyle[][]> BORDER_STYLE = new SubCssMetaData<>("-fx-border-style", LayeredBorderStyleConverter.getInstance());
   static final CssMetaData<Node, Margins[]> BORDER_WIDTH = new SubCssMetaData<>("-fx-border-width", Margins.SequenceConverter.getInstance());
   static final CssMetaData<Node, CornerRadii[]> BORDER_RADIUS = new SubCssMetaData<>("-fx-border-radius", CornerRadiiConverter.getInstance());
   static final CssMetaData<Node, Insets[]> BORDER_INSETS = new SubCssMetaData<>("-fx-border-insets", InsetsConverter.SequenceConverter.getInstance());
   static final CssMetaData<Node, String[]> BORDER_IMAGE_SOURCE = new SubCssMetaData<>("-fx-border-image-source", URLConverter.SequenceConverter.getInstance());
   static final CssMetaData<Node, RepeatStruct[]> BORDER_IMAGE_REPEAT = new SubCssMetaData<>(
      "-fx-border-image-repeat", RepeatStructConverter.getInstance(), new RepeatStruct[]{new RepeatStruct(BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT)}
   );
   static final CssMetaData<Node, BorderImageSlices[]> BORDER_IMAGE_SLICE = new SubCssMetaData<>(
      "-fx-border-image-slice", SliceSequenceConverter.getInstance(), new BorderImageSlices[]{BorderImageSlices.DEFAULT}
   );
   static final CssMetaData<Node, BorderWidths[]> BORDER_IMAGE_WIDTH = new SubCssMetaData<>(
      "-fx-border-image-width", BorderImageWidthConverter.getInstance(), new BorderWidths[]{BorderWidths.DEFAULT}
   );
   static final CssMetaData<Node, Insets[]> BORDER_IMAGE_INSETS = new SubCssMetaData<>(
      "-fx-border-image-insets", InsetsConverter.SequenceConverter.getInstance(), new Insets[]{Insets.EMPTY}
   );
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES = Collections.unmodifiableList(
      Arrays.asList(
         BORDER_COLOR,
         BORDER_STYLE,
         BORDER_WIDTH,
         BORDER_RADIUS,
         BORDER_INSETS,
         BORDER_IMAGE_SOURCE,
         BORDER_IMAGE_REPEAT,
         BORDER_IMAGE_SLICE,
         BORDER_IMAGE_WIDTH,
         BORDER_IMAGE_INSETS
      )
   );
   public static final Border EMPTY = new Border((BorderStroke[])null, null);
   final List<BorderStroke> strokes;
   final List<BorderImage> images;
   final Insets outsets;
   final Insets insets;
   private final int hash;

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   public final List<BorderStroke> getStrokes() {
      return this.strokes;
   }

   public final List<BorderImage> getImages() {
      return this.images;
   }

   public final Insets getOutsets() {
      return this.outsets;
   }

   public final Insets getInsets() {
      return this.insets;
   }

   public final boolean isEmpty() {
      return this.strokes.isEmpty() && this.images.isEmpty();
   }

   public Border(@NamedArg("strokes") BorderStroke... var1) {
      this(var1, null);
   }

   public Border(@NamedArg("images") BorderImage... var1) {
      this(null, var1);
   }

   public Border(@NamedArg("strokes") List<BorderStroke> var1, @NamedArg("images") List<BorderImage> var2) {
      this(var1 == null ? null : var1.toArray(new BorderStroke[var1.size()]), var2 == null ? null : var2.toArray(new BorderImage[var2.size()]));
   }

   public Border(@NamedArg("strokes") BorderStroke[] var1, @NamedArg("images") BorderImage[] var2) {
      double var3 = 0.0;
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      double var11 = 0.0;
      double var13 = 0.0;
      double var15 = 0.0;
      double var17 = 0.0;
      if (var1 != null && var1.length != 0) {
         BorderStroke[] var19 = new BorderStroke[var1.length];
         int var20 = 0;

         for (int var21 = 0; var21 < var1.length; var21++) {
            BorderStroke var22 = var1[var21];
            if (var22 != null) {
               var19[var20++] = var22;
               double var23 = var22.innerEdge.getTop();
               double var25 = var22.innerEdge.getRight();
               double var27 = var22.innerEdge.getBottom();
               double var29 = var22.innerEdge.getLeft();
               var3 = var3 >= var23 ? var3 : var23;
               var5 = var5 >= var25 ? var5 : var25;
               var7 = var7 >= var27 ? var7 : var27;
               var9 = var9 >= var29 ? var9 : var29;
               double var31 = var22.outerEdge.getTop();
               double var33 = var22.outerEdge.getRight();
               double var35 = var22.outerEdge.getBottom();
               double var37 = var22.outerEdge.getLeft();
               var11 = var11 >= var31 ? var11 : var31;
               var13 = var13 >= var33 ? var13 : var33;
               var15 = var15 >= var35 ? var15 : var35;
               var17 = var17 >= var37 ? var17 : var37;
            }
         }

         this.strokes = new UnmodifiableArrayList(var19, var20);
      } else {
         this.strokes = Collections.emptyList();
      }

      if (var2 != null && var2.length != 0) {
         BorderImage[] var39 = new BorderImage[var2.length];
         int var42 = 0;

         for (int var43 = 0; var43 < var2.length; var43++) {
            BorderImage var44 = var2[var43];
            if (var44 != null) {
               var39[var42++] = var44;
               double var45 = var44.innerEdge.getTop();
               double var46 = var44.innerEdge.getRight();
               double var47 = var44.innerEdge.getBottom();
               double var48 = var44.innerEdge.getLeft();
               var3 = var3 >= var45 ? var3 : var45;
               var5 = var5 >= var46 ? var5 : var46;
               var7 = var7 >= var47 ? var7 : var47;
               var9 = var9 >= var48 ? var9 : var48;
               double var49 = var44.outerEdge.getTop();
               double var50 = var44.outerEdge.getRight();
               double var51 = var44.outerEdge.getBottom();
               double var52 = var44.outerEdge.getLeft();
               var11 = var11 >= var49 ? var11 : var49;
               var13 = var13 >= var50 ? var13 : var50;
               var15 = var15 >= var51 ? var15 : var51;
               var17 = var17 >= var52 ? var17 : var52;
            }
         }

         this.images = new UnmodifiableArrayList(var39, var42);
      } else {
         this.images = Collections.emptyList();
      }

      this.outsets = new Insets(var11, var13, var15, var17);
      this.insets = new Insets(var3, var5, var7, var9);
      int var40 = this.strokes.hashCode();
      var40 = 31 * var40 + this.images.hashCode();
      this.hash = var40;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Border var2 = (Border)var1;
         if (this.hash != var2.hash) {
            return false;
         } else {
            return !this.images.equals(var2.images) ? false : this.strokes.equals(var2.strokes);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.hash;
   }
}
