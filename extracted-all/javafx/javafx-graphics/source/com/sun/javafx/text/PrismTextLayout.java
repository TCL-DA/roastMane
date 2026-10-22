package com.sun.javafx.text;

import com.sun.javafx.font.CharToGlyphMapper;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.Metrics;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.Translate2D;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextSpan;
import java.text.Bidi;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

public class PrismTextLayout implements TextLayout {
   private static final BaseTransform IDENTITY = BaseTransform.IDENTITY_TRANSFORM;
   private static final int X_MIN_INDEX = 0;
   private static final int Y_MIN_INDEX = 1;
   private static final int X_MAX_INDEX = 2;
   private static final int Y_MAX_INDEX = 3;
   private static final Hashtable<Integer, LayoutCache> stringCache = new Hashtable<>();
   private static final Object CACHE_SIZE_LOCK = new Object();
   private static int cacheSize = 0;
   private static final int MAX_STRING_SIZE = 256;
   private static final int MAX_CACHE_SIZE = PrismFontFactory.cacheLayoutSize;
   private char[] text;
   private TextSpan[] spans;
   private PGFont font;
   private FontStrike strike;
   private Integer cacheKey;
   private TextLine[] lines;
   private TextRun[] runs;
   private int runCount;
   private BaseBounds logicalBounds;
   private RectBounds visualBounds;
   private float layoutWidth;
   private float layoutHeight;
   private float wrapWidth;
   private float spacing;
   private LayoutCache layoutCache;
   private Shape shape;
   private int flags;
   private int tabSize = 8;

   public PrismTextLayout() {
      this.logicalBounds = new RectBounds();
      this.flags = 262144;
   }

   private void reset() {
      this.layoutCache = null;
      this.runs = null;
      this.flags &= -2048;
      this.relayout();
   }

   private void relayout() {
      this.logicalBounds.makeEmpty();
      this.visualBounds = null;
      this.layoutWidth = this.layoutHeight = 0.0F;
      this.flags &= -1665;
      this.lines = null;
      this.shape = null;
   }

   @Override
   public boolean setContent(TextSpan[] var1) {
      if (var1 == null && this.spans == null) {
         return false;
      } else {
         if (var1 != null && this.spans != null && var1.length == this.spans.length) {
            int var2 = 0;

            while (var2 < var1.length && var1[var2] == this.spans[var2]) {
               var2++;
            }

            if (var2 == var1.length) {
               return false;
            }
         }

         this.reset();
         this.spans = var1;
         this.font = null;
         this.strike = null;
         this.text = null;
         this.cacheKey = null;
         return true;
      }
   }

   @Override
   public boolean setContent(String var1, Object var2) {
      this.reset();
      this.spans = null;
      this.font = (PGFont)var2;
      this.strike = ((PGFont)var2).getStrike(IDENTITY);
      this.text = var1.toCharArray();
      if (MAX_CACHE_SIZE > 0) {
         int var3 = var1.length();
         if (0 < var3 && var3 <= 256) {
            this.cacheKey = var1.hashCode() * this.strike.hashCode();
         }
      }

      return true;
   }

   @Override
   public boolean setDirection(int var1) {
      if ((this.flags & 15360) == var1) {
         return false;
      } else {
         this.flags &= -15361;
         this.flags |= var1 & 15360;
         this.reset();
         return true;
      }
   }

   @Override
   public boolean setBoundsType(int var1) {
      if ((this.flags & 16384) == var1) {
         return false;
      } else {
         this.flags &= -16385;
         this.flags |= var1 & 16384;
         this.reset();
         return true;
      }
   }

   @Override
   public boolean setAlignment(int var1) {
      int var2 = 262144;
      switch (var1) {
         case 0:
            var2 = 262144;
            break;
         case 1:
            var2 = 524288;
            break;
         case 2:
            var2 = 1048576;
            break;
         case 3:
            var2 = 2097152;
      }

      if ((this.flags & 3932160) == var2) {
         return false;
      } else {
         if (var2 == 2097152 || (this.flags & 2097152) != 0) {
            this.reset();
         }

         this.flags &= -3932161;
         this.flags |= var2;
         this.relayout();
         return true;
      }
   }

   @Override
   public boolean setWrapWidth(float var1) {
      if (Float.isInfinite(var1)) {
         var1 = 0.0F;
      }

      if (Float.isNaN(var1)) {
         var1 = 0.0F;
      }

      float var2 = this.wrapWidth;
      this.wrapWidth = Math.max(0.0F, var1);
      boolean var3 = true;
      if (this.lines != null && var2 != 0.0F && var1 != 0.0F && (this.flags & 262144) != 0) {
         if (var1 > var2) {
            if ((this.flags & 128) == 0) {
               var3 = false;
            }
         } else if (var1 >= this.layoutWidth) {
            var3 = false;
         }
      }

      if (var3) {
         this.relayout();
      }

      return var3;
   }

   @Override
   public boolean setLineSpacing(float var1) {
      if (this.spacing == var1) {
         return false;
      } else {
         this.spacing = var1;
         this.relayout();
         return true;
      }
   }

   private void ensureLayout() {
      if (this.lines == null) {
         this.layout();
      }
   }

   @Override
   public com.sun.javafx.scene.text.TextLine[] getLines() {
      this.ensureLayout();
      return this.lines;
   }

   @Override
   public GlyphList[] getRuns() {
      this.ensureLayout();
      GlyphList[] var1 = new GlyphList[this.runCount];
      int var2 = 0;

      for (int var3 = 0; var3 < this.lines.length; var3++) {
         TextRun[] var4 = this.lines[var3].getRuns();
         int var5 = var4.length;
         System.arraycopy(var4, 0, var1, var2, var5);
         var2 += var5;
      }

      return var1;
   }

   @Override
   public BaseBounds getBounds() {
      this.ensureLayout();
      return this.logicalBounds;
   }

   @Override
   public BaseBounds getBounds(TextSpan var1, BaseBounds var2) {
      this.ensureLayout();
      float var3 = Float.POSITIVE_INFINITY;
      float var4 = Float.POSITIVE_INFINITY;
      float var5 = Float.NEGATIVE_INFINITY;
      float var6 = Float.NEGATIVE_INFINITY;
      if (var1 != null) {
         for (int var7 = 0; var7 < this.lines.length; var7++) {
            TextLine var8 = this.lines[var7];
            TextRun[] var9 = var8.getRuns();

            for (int var10 = 0; var10 < var9.length; var10++) {
               TextRun var11 = var9[var10];
               TextSpan var12 = var11.getTextSpan();
               if (var12 == var1) {
                  Point2D var13 = var11.getLocation();
                  float var14 = var13.x;
                  if (var11.isLeftBearing()) {
                     var14 += var8.getLeftSideBearing();
                  }

                  float var15 = var13.x + var11.getWidth();
                  if (var11.isRightBearing()) {
                     var15 += var8.getRightSideBearing();
                  }

                  float var16 = var13.y;
                  float var17 = var13.y + var8.getBounds().getHeight() + this.spacing;
                  if (var14 < var3) {
                     var3 = var14;
                  }

                  if (var16 < var4) {
                     var4 = var16;
                  }

                  if (var15 > var5) {
                     var5 = var15;
                  }

                  if (var17 > var6) {
                     var6 = var17;
                  }
               }
            }
         }
      } else {
         var6 = 0.0F;
         var4 = 0.0F;

         for (int var18 = 0; var18 < this.lines.length; var18++) {
            TextLine var20 = this.lines[var18];
            RectBounds var22 = var20.getBounds();
            float var23 = var22.getMinX() + var20.getLeftSideBearing();
            if (var23 < var3) {
               var3 = var23;
            }

            float var24 = var22.getMaxX() + var20.getRightSideBearing();
            if (var24 > var5) {
               var5 = var24;
            }

            var6 += var22.getHeight();
         }

         if (this.isMirrored()) {
            float var19 = this.getMirroringWidth();
            float var21 = var3;
            var3 = var19 - var5;
            var5 = var19 - var21;
         }
      }

      return var2.deriveWithNewBounds(var3, var4, 0.0F, var5, var6, 0.0F);
   }

   @Override
   public PathElement[] getCaretShape(int var1, boolean var2, float var3, float var4) {
      this.ensureLayout();
      int var5 = 0;

      for (int var6 = this.getLineCount(); var5 < var6 - 1; var5++) {
         TextLine var7 = this.lines[var5];
         int var8 = var7.getStart() + var7.getLength();
         if (var8 > var1) {
            break;
         }
      }

      int var23 = -1;
      byte var24 = 0;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      TextLine var12 = this.lines[var5];
      TextRun[] var13 = var12.getRuns();
      int var14 = var13.length;
      int var15 = -1;

      for (int var16 = 0; var16 < var14; var16++) {
         TextRun var17 = var13[var16];
         int var18 = var17.getStart();
         int var19 = var17.getEnd();
         if (var18 <= var1 && var1 < var19) {
            if (!var17.isLinebreak()) {
               var15 = var16;
            }
            break;
         }
      }

      if (var15 != -1) {
         TextRun var31 = var13[var15];
         int var34 = var31.getStart();
         Point2D var38 = var31.getLocation();
         var9 = var38.x + var31.getXAtOffset(var1 - var34, var2);
         var10 = var38.y;
         var11 = var12.getBounds().getHeight();
         if (var2) {
            if (var15 > 0 && var1 == var34) {
               var24 = var31.getLevel();
               var23 = var1 - 1;
            }
         } else {
            int var42 = var31.getEnd();
            if (var15 + 1 < var13.length && var1 + 1 == var42) {
               var24 = var31.getLevel();
               var23 = var1 + 1;
            }
         }
      } else {
         int var32 = 0;
         var15 = 0;

         for (int var35 = 0; var35 < var14; var35++) {
            TextRun var39 = var13[var35];
            if (var39.getStart() >= var32 && !var39.isLinebreak()) {
               var32 = var39.getStart();
               var15 = var35;
            }
         }

         TextRun var36 = var13[var15];
         Point2D var40 = var36.getLocation();
         var9 = var40.x + (var36.isLeftToRight() ? var36.getWidth() : 0.0F);
         var10 = var40.y;
         var11 = var12.getBounds().getHeight();
      }

      if (this.isMirrored()) {
         var9 = this.getMirroringWidth() - var9;
      }

      var9 += var3;
      var10 += var4;
      if (var23 != -1) {
         for (int var33 = 0; var33 < var13.length; var33++) {
            TextRun var37 = var13[var33];
            int var41 = var37.getStart();
            int var43 = var37.getEnd();
            if (var41 <= var23 && var23 < var43 && (var37.getLevel() & 1) != (var24 & 1)) {
               Point2D var20 = var37.getLocation();
               float var21 = var20.x;
               if (var2) {
                  if ((var24 & 1) != 0) {
                     var21 += var37.getWidth();
                  }
               } else if ((var24 & 1) == 0) {
                  var21 += var37.getWidth();
               }

               if (this.isMirrored()) {
                  var21 = this.getMirroringWidth() - var21;
               }

               var21 += var3;
               return new PathElement[]{
                  new MoveTo((double)var9, (double)var10),
                  new LineTo((double)var9, (double)(var10 + var11 / 2.0F)),
                  new MoveTo((double)var21, (double)(var10 + var11 / 2.0F)),
                  new LineTo((double)var21, (double)(var10 + var11))
               };
            }
         }
      }

      return new PathElement[]{new MoveTo((double)var9, (double)var10), new LineTo((double)var9, (double)(var10 + var11))};
   }

   @Override
   public TextLayout.Hit getHitInfo(float var1, float var2) {
      int var3 = -1;
      boolean var4 = false;
      this.ensureLayout();
      int var5 = this.getLineIndex(var2);
      if (var5 >= this.getLineCount()) {
         var3 = this.getCharCount();
      } else {
         if (this.isMirrored()) {
            var1 = this.getMirroringWidth() - var1;
         }

         TextLine var6 = this.lines[var5];
         TextRun[] var7 = var6.getRuns();
         RectBounds var8 = var6.getBounds();
         TextRun var9 = null;
         var1 -= var8.getMinX();

         for (int var10 = 0; var10 < var7.length; var10++) {
            var9 = var7[var10];
            if (var1 < var9.getWidth()) {
               break;
            }

            if (var10 + 1 < var7.length) {
               if (var7[var10 + 1].isLinebreak()) {
                  break;
               }

               var1 -= var9.getWidth();
            }
         }

         if (var9 != null) {
            int[] var13 = new int[1];
            var3 = var9.getStart() + var9.getOffsetAtX(var1, var13);
            var4 = var13[0] == 0;
         } else {
            var3 = var6.getStart();
            var4 = true;
         }
      }

      return new TextLayout.Hit(var3, -1, var4);
   }

   @Override
   public PathElement[] getRange(int var1, int var2, int var3, float var4, float var5) {
      this.ensureLayout();
      int var6 = this.getLineCount();
      ArrayList var7 = new ArrayList();
      float var8 = 0.0F;

      for (int var9 = 0; var9 < var6; var9++) {
         TextLine var10 = this.lines[var9];
         RectBounds var11 = var10.getBounds();
         int var12 = var10.getStart();
         if (var12 >= var2) {
            break;
         }

         int var13 = var12 + var10.getLength();
         if (var1 > var13) {
            var8 += var11.getHeight() + this.spacing;
         } else {
            TextRun[] var14 = var10.getRuns();
            int var15 = Math.min(var13, var2) - Math.max(var12, var1);
            int var16 = 0;
            float var17 = -1.0F;
            float var18 = -1.0F;

            for (float var19 = var11.getMinX(); var15 > 0 && var16 < var14.length; var16++) {
               TextRun var20 = var14[var16];
               int var21 = var20.getStart();
               int var22 = var20.getEnd();
               float var23 = var20.getWidth();
               int var24 = Math.max(var21, Math.min(var1, var22));
               int var25 = Math.max(var21, Math.min(var2, var22));
               int var26 = var25 - var24;
               if (var26 != 0) {
                  boolean var27 = var20.isLeftToRight();
                  float var28;
                  if (var21 > var1) {
                     var28 = var27 ? var19 : var19 + var23;
                  } else {
                     var28 = var19 + var20.getXAtOffset(var1 - var21, true);
                  }

                  float var29;
                  if (var22 < var2) {
                     var29 = var27 ? var19 + var23 : var19;
                  } else {
                     var29 = var19 + var20.getXAtOffset(var2 - var21, true);
                  }

                  if (var28 > var29) {
                     float var30 = var28;
                     var28 = var29;
                     var29 = var30;
                  }

                  var15 -= var26;
                  float var35 = 0.0F;
                  float var31 = 0.0F;
                  switch (var3) {
                     case 1:
                        var35 = var8;
                        var31 = var8 + var11.getHeight();
                        break;
                     case 2:
                     case 4:
                        label82: {
                           FontStrike var32 = null;
                           if (this.spans != null) {
                              TextSpan var33 = var20.getTextSpan();
                              PGFont var34 = (PGFont)var33.getFont();
                              if (var34 == null) {
                                 break label82;
                              }

                              var32 = var34.getStrike(IDENTITY);
                           } else {
                              var32 = this.strike;
                           }

                           var35 = var8 - var20.getAscent();
                           Metrics var40 = var32.getMetrics();
                           if (var3 == 2) {
                              var35 += var40.getUnderLineOffset();
                              var31 = var35 + var40.getUnderLineThickness();
                           } else {
                              var35 += var40.getStrikethroughOffset();
                              var31 = var35 + var40.getStrikethroughThickness();
                           }
                        }
                     case 3:
                  }

                  if (var28 != var18) {
                     if (var17 != -1.0F && var18 != -1.0F) {
                        float var38 = var17;
                        float var41 = var18;
                        if (this.isMirrored()) {
                           float var43 = this.getMirroringWidth();
                           var38 = var43 - var17;
                           var41 = var43 - var18;
                        }

                        var7.add(new MoveTo((double)(var4 + var38), (double)(var5 + var35)));
                        var7.add(new LineTo((double)(var4 + var41), (double)(var5 + var35)));
                        var7.add(new LineTo((double)(var4 + var41), (double)(var5 + var31)));
                        var7.add(new LineTo((double)(var4 + var38), (double)(var5 + var31)));
                        var7.add(new LineTo((double)(var4 + var38), (double)(var5 + var35)));
                     }

                     var17 = var28;
                  }

                  var18 = var29;
                  if (var15 == 0) {
                     float var39 = var17;
                     float var42 = var29;
                     if (this.isMirrored()) {
                        float var44 = this.getMirroringWidth();
                        var39 = var44 - var17;
                        var42 = var44 - var29;
                     }

                     var7.add(new MoveTo((double)(var4 + var39), (double)(var5 + var35)));
                     var7.add(new LineTo((double)(var4 + var42), (double)(var5 + var35)));
                     var7.add(new LineTo((double)(var4 + var42), (double)(var5 + var31)));
                     var7.add(new LineTo((double)(var4 + var39), (double)(var5 + var31)));
                     var7.add(new LineTo((double)(var4 + var39), (double)(var5 + var35)));
                  }
               }

               var19 += var23;
            }

            var8 += var11.getHeight() + this.spacing;
         }
      }

      return var7.toArray(new PathElement[var7.size()]);
   }

   @Override
   public Shape getShape(int var1, TextSpan var2) {
      this.ensureLayout();
      boolean var3 = (var1 & 1) != 0;
      boolean var4 = (var1 & 2) != 0;
      boolean var5 = (var1 & 4) != 0;
      boolean var6 = (var1 & 8) != 0;
      if (this.shape != null && var3 && !var4 && !var5 && var6) {
         return this.shape;
      } else {
         Path2D var7 = new Path2D();
         Translate2D var8 = new Translate2D(0.0, 0.0);
         float var9 = 0.0F;
         if (var6) {
            var9 = -this.lines[0].getBounds().getMinY();
         }

         for (int var10 = 0; var10 < this.lines.length; var10++) {
            TextLine var11 = this.lines[var10];
            TextRun[] var12 = var11.getRuns();
            RectBounds var13 = var11.getBounds();
            float var14 = -var13.getMinY();

            for (int var15 = 0; var15 < var12.length; var15++) {
               TextRun var16 = var12[var15];
               FontStrike var17 = null;
               if (this.spans != null) {
                  TextSpan var18 = var16.getTextSpan();
                  if (var2 != null && var18 != var2) {
                     continue;
                  }

                  PGFont var19 = (PGFont)var18.getFont();
                  if (var19 == null) {
                     continue;
                  }

                  var17 = var19.getStrike(IDENTITY);
               } else {
                  var17 = this.strike;
               }

               Point2D var24 = var16.getLocation();
               float var25 = var24.x;
               float var20 = var24.y + var14 - var9;
               Metrics var21 = null;
               if (var4 || var5) {
                  var21 = var17.getMetrics();
               }

               if (var4) {
                  RoundRectangle2D var22 = new RoundRectangle2D();
                  var22.x = var25;
                  var22.y = var20 + var21.getUnderLineOffset();
                  var22.width = var16.getWidth();
                  var22.height = var21.getUnderLineThickness();
                  var7.append(var22, false);
               }

               if (var5) {
                  RoundRectangle2D var26 = new RoundRectangle2D();
                  var26.x = var25;
                  var26.y = var20 + var21.getStrikethroughOffset();
                  var26.width = var16.getWidth();
                  var26.height = var21.getStrikethroughThickness();
                  var7.append(var26, false);
               }

               if (var3 && var16.getGlyphCount() > 0) {
                  var8.restoreTransform(1.0, 0.0, 0.0, 1.0, (double)var25, (double)var20);
                  Path2D var27 = (Path2D)var17.getOutline(var16, var8);
                  var7.append(var27, false);
               }
            }
         }

         if (var3 && !var4 && !var5) {
            this.shape = var7;
         }

         return var7;
      }
   }

   @Override
   public boolean setTabSize(int var1) {
      if (var1 < 1) {
         var1 = 1;
      }

      if (this.tabSize != var1) {
         this.tabSize = var1;
         this.relayout();
         return true;
      } else {
         return false;
      }
   }

   private int getLineIndex(float var1) {
      int var2 = 0;
      float var3 = 0.0F;

      for (int var4 = this.getLineCount(); var2 < var4; var2++) {
         var3 += this.lines[var2].getBounds().getHeight() + this.spacing;
         if (var2 + 1 == var4) {
            var3 -= this.lines[var2].getLeading();
         }

         if (var3 > var1) {
            break;
         }
      }

      return var2;
   }

   private boolean copyCache() {
      int var1 = this.flags & 3932160;
      int var2 = this.flags & 16384;
      return this.wrapWidth != 0.0F || var1 != 262144 || var2 == 0 || this.isMirrored();
   }

   private void initCache() {
      if (this.cacheKey != null) {
         if (this.layoutCache == null) {
            LayoutCache var1 = stringCache.get(this.cacheKey);
            if (var1 != null && var1.font.equals(this.font) && Arrays.equals(var1.text, this.text)) {
               this.layoutCache = var1;
               this.runs = var1.runs;
               this.runCount = var1.runCount;
               this.flags = this.flags | var1.analysis;
            }
         }

         if (this.layoutCache != null) {
            if (this.copyCache()) {
               if (this.layoutCache.runs == this.runs) {
                  this.runs = new TextRun[this.runCount];
                  System.arraycopy(this.layoutCache.runs, 0, this.runs, 0, this.runCount);
               }
            } else if (this.layoutCache.lines != null) {
               this.runs = this.layoutCache.runs;
               this.runCount = this.layoutCache.runCount;
               this.flags = this.flags | this.layoutCache.analysis;
               this.lines = this.layoutCache.lines;
               this.layoutWidth = this.layoutCache.layoutWidth;
               this.layoutHeight = this.layoutCache.layoutHeight;
               float var2 = this.lines[0].getBounds().getMinY();
               this.logicalBounds = this.logicalBounds.deriveWithNewBounds(0.0F, var2, 0.0F, this.layoutWidth, this.layoutHeight + var2, 0.0F);
            }
         }
      }
   }

   private int getLineCount() {
      return this.lines.length;
   }

   private int getCharCount() {
      if (this.text != null) {
         return this.text.length;
      } else {
         int var1 = 0;

         for (int var2 = 0; var2 < this.lines.length; var2++) {
            var1 += this.lines[var2].getLength();
         }

         return var1;
      }
   }

   public TextSpan[] getTextSpans() {
      return this.spans;
   }

   public PGFont getFont() {
      return this.font;
   }

   public int getDirection() {
      if ((this.flags & 1024) != 0) {
         return 0;
      } else if ((this.flags & 2048) != 0) {
         return 1;
      } else if ((this.flags & 4096) != 0) {
         return -2;
      } else {
         return (this.flags & 8192) != 0 ? -1 : -2;
      }
   }

   public void addTextRun(TextRun var1) {
      if (this.runCount + 1 > this.runs.length) {
         TextRun[] var2 = new TextRun[this.runs.length + 64];
         System.arraycopy(this.runs, 0, var2, 0, this.runs.length);
         this.runs = var2;
      }

      this.runs[this.runCount++] = var1;
   }

   private void buildRuns(char[] var1) {
      this.runCount = 0;
      if (this.runs == null) {
         int var2 = Math.max(4, Math.min(var1.length / 16, 16));
         this.runs = new TextRun[var2];
      }

      GlyphLayout var4 = GlyphLayout.getInstance();
      this.flags = var4.breakRuns(this, var1, this.flags);
      var4.dispose();

      for (int var3 = this.runCount; var3 < this.runs.length; var3++) {
         this.runs[var3] = null;
      }
   }

   private void shape(TextRun var1, char[] var2, GlyphLayout var3) {
      FontStrike var4;
      PGFont var5;
      if (this.spans != null) {
         if (this.spans.length == 0) {
            return;
         }

         TextSpan var6 = var1.getTextSpan();
         var5 = (PGFont)var6.getFont();
         if (var5 == null) {
            RectBounds var7 = var6.getBounds();
            var1.setEmbedded(var7, var6.getText().length());
            return;
         }

         var4 = var5.getStrike(IDENTITY);
      } else {
         var5 = this.font;
         var4 = this.strike;
      }

      if (var1.getAscent() == 0.0F) {
         Metrics var16 = var4.getMetrics();
         if ((this.flags & 16384) == 16384) {
            float var18 = var16.getAscent();
            if (var5.getFamilyName().equals("Segoe UI")) {
               var18 = (float)((double)var18 * 0.8);
            }

            var18 = (float)((int)((double)var18 - 0.75));
            float var8 = (float)((int)((double)var16.getDescent() + 0.75));
            float var9 = (float)((int)((double)var16.getLineGap() + 0.75));
            float var10 = (float)((int)((double)var16.getCapHeight() + 0.75));
            float var11 = -var18 - var10;
            if (var11 > var8) {
               var8 = var11;
            } else {
               var18 += var11 - var8;
            }

            var1.setMetrics(var18, var8, var9);
         } else {
            var1.setMetrics(var16.getAscent(), var16.getDescent(), var16.getLineGap());
         }
      }

      if (!var1.isTab()) {
         if (!var1.isLinebreak()) {
            if (var1.getGlyphCount() <= 0) {
               if (var1.isComplex()) {
                  var3.layout(var1, var5, var4, var2);
               } else {
                  FontResource var17 = var4.getFontResource();
                  int var20 = var1.getStart();
                  int var21 = var1.getLength();
                  if (this.layoutCache == null) {
                     float var22 = var4.getSize();
                     CharToGlyphMapper var24 = var17.getGlyphMapper();
                     int[] var26 = new int[var21];
                     var24.charsToGlyphs(var20, var21, var2, var26);
                     float[] var12 = new float[var21 + 1 << 1];
                     float var13 = 0.0F;

                     for (int var14 = 0; var14 < var21; var14++) {
                        float var15 = var17.getAdvance(var26[var14], var22);
                        var12[var14 << 1] = var13;
                        var13 += var15;
                     }

                     var12[var21 << 1] = var13;
                     var1.shape(var21, var26, var12, null);
                  } else {
                     if (!this.layoutCache.valid) {
                        float var23 = var4.getSize();
                        CharToGlyphMapper var25 = var17.getGlyphMapper();
                        var25.charsToGlyphs(var20, var21, var2, this.layoutCache.glyphs, var20);
                        int var27 = var20 + var21;
                        float var28 = 0.0F;

                        for (int var29 = var20; var29 < var27; var29++) {
                           float var30 = var17.getAdvance(this.layoutCache.glyphs[var29], var23);
                           this.layoutCache.advances[var29] = var30;
                           var28 += var30;
                        }

                        var1.setWidth(var28);
                     }

                     var1.shape(var21, this.layoutCache.glyphs, this.layoutCache.advances);
                  }
               }
            }
         }
      }
   }

   private TextLine createLine(int var1, int var2, int var3) {
      int var4 = var2 - var1 + 1;
      TextRun[] var5 = new TextRun[var4];
      if (var1 < this.runCount) {
         System.arraycopy(this.runs, var1, var5, 0, var4);
      }

      float var6 = 0.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      int var10 = 0;

      for (int var11 = 0; var11 < var5.length; var11++) {
         TextRun var12 = var5[var11];
         var6 += var12.getWidth();
         var7 = Math.min(var7, var12.getAscent());
         var8 = Math.max(var8, var12.getDescent());
         var9 = Math.max(var9, var12.getLeading());
         var10 += var12.getLength();
      }

      if (var6 > this.layoutWidth) {
         this.layoutWidth = var6;
      }

      return new TextLine(var3, var10, var5, var6, var7, var8, var9);
   }

   private void reorderLine(TextLine var1) {
      TextRun[] var2 = var1.getRuns();
      int var3 = var2.length;
      if (var3 > 0 && var2[var3 - 1].isLinebreak()) {
         var3--;
      }

      if (var3 >= 2) {
         byte[] var4 = new byte[var3];

         for (int var5 = 0; var5 < var3; var5++) {
            var4[var5] = var2[var5].getLevel();
         }

         Bidi.reorderVisually(var4, 0, var2, 0, var3);
      }
   }

   private char[] getText() {
      if (this.text == null) {
         int var1 = 0;

         for (int var2 = 0; var2 < this.spans.length; var2++) {
            var1 += this.spans[var2].getText().length();
         }

         this.text = new char[var1];
         int var6 = 0;

         for (int var3 = 0; var3 < this.spans.length; var3++) {
            String var4 = this.spans[var3].getText();
            int var5 = var4.length();
            var4.getChars(0, var5, this.text, var6);
            var6 += var5;
         }
      }

      return this.text;
   }

   private boolean isSimpleLayout() {
      int var1 = this.flags & 3932160;
      boolean var2 = this.wrapWidth > 0.0F && var1 == 2097152;
      byte var3 = 24;
      return (this.flags & var3) == 0 && !var2;
   }

   private boolean isMirrored() {
      boolean var1 = false;
      switch (this.flags & 15360) {
         case 1024:
            var1 = false;
            break;
         case 2048:
            var1 = true;
            break;
         case 4096:
         case 8192:
            var1 = (this.flags & 256) != 0;
      }

      return var1;
   }

   private float getMirroringWidth() {
      return this.wrapWidth != 0.0F ? this.wrapWidth : this.layoutWidth;
   }

   private void reuseRuns() {
      this.runCount = 0;
      int var1 = 0;

      while (var1 < this.runs.length) {
         TextRun var2 = this.runs[var1];
         if (var2 == null) {
            break;
         }

         this.runs[var1] = null;
         var1++;
         TextRun var4;
         this.runs[this.runCount++] = var4 = var2.unwrap();
         if (var4.isSplit()) {
            var4.merge(null);

            while (var1 < this.runs.length) {
               TextRun var3 = this.runs[var1];
               if (var3 == null) {
                  break;
               }

               var4.merge(var3);
               this.runs[var1] = null;
               var1++;
               if (var3.isSplitLast()) {
                  break;
               }
            }
         }
      }
   }

   private float getTabAdvance() {
      float var1 = 0.0F;
      if (this.spans != null) {
         for (int var2 = 0; var2 < this.spans.length; var2++) {
            TextSpan var3 = this.spans[var2];
            PGFont var4 = (PGFont)var3.getFont();
            if (var4 != null) {
               FontStrike var5 = var4.getStrike(IDENTITY);
               var1 = var5.getCharAdvance(' ');
               break;
            }
         }
      } else {
         var1 = this.strike.getCharAdvance(' ');
      }

      return (float)this.tabSize * var1;
   }

   private void layout() {
      this.initCache();
      if (this.lines == null) {
         char[] var1 = this.getText();
         if ((this.flags & 2) != 0 && this.isSimpleLayout()) {
            this.reuseRuns();
         } else {
            this.buildRuns(var1);
         }

         GlyphLayout var2 = null;
         if ((this.flags & 16) != 0) {
            var2 = GlyphLayout.getInstance();
         }

         float var3 = 0.0F;
         if ((this.flags & 4) != 0) {
            var3 = this.getTabAdvance();
         }

         BreakIterator var4 = null;
         if (this.wrapWidth > 0.0F && (this.flags & 80) != 0) {
            var4 = BreakIterator.getLineInstance();
            var4.setText(new CharArrayIterator(var1));
         }

         int var5 = this.flags & 3932160;
         if (this.isSimpleLayout()) {
            if (this.layoutCache == null) {
               this.layoutCache = new LayoutCache();
               this.layoutCache.glyphs = new int[var1.length];
               this.layoutCache.advances = new float[var1.length];
            }
         } else {
            this.layoutCache = null;
         }

         float var6 = 0.0F;
         int var7 = 0;
         int var8 = 0;
         ArrayList var9 = new ArrayList();

         for (int var10 = 0; var10 < this.runCount; var10++) {
            TextRun var11 = this.runs[var10];
            this.shape(var11, var1, var2);
            if (var11.isTab()) {
               float var12 = (float)((int)(var6 / var3) + 1) * var3;
               var11.setWidth(var12 - var6);
            }

            float var34 = var11.getWidth();
            if (this.wrapWidth > 0.0F && var6 + var34 > this.wrapWidth && !var11.isLinebreak()) {
               int var13 = var11.getStart() + var11.getWrapIndex(this.wrapWidth - var6);
               int var14 = var13;
               int var15 = var11.getEnd();
               if (var13 + 1 < var15 && var1[var13] == ' ') {
                  var14 = var13 + 1;
               }

               int var16 = var14;
               if (var4 == null) {
                  for (boolean var17 = Character.isWhitespace(var1[var14]); var16 > var8; var16--) {
                     boolean var18 = Character.isWhitespace(var1[var16 - 1]);
                     if (!var17 && var18) {
                        break;
                     }

                     var17 = var18;
                  }
               } else {
                  var16 = !var4.isBoundary(var14) && var1[var14] != '\t' ? var4.preceding(var14) : var14;
               }

               if (var16 < var8) {
                  var16 = var8;
               }

               int var44 = var7;

               TextRun var46;
               for (var46 = null; var44 < this.runCount; var44++) {
                  var46 = this.runs[var44];
                  if (var46.getEnd() > var16) {
                     break;
                  }
               }

               if (var16 == var8) {
                  var46 = var11;
                  var44 = var10;
                  var16 = var13;
               }

               int var19 = var16 - var46.getStart();
               if (var19 == 0 && var44 != var7) {
                  var10 = var44 - 1;
               } else {
                  var10 = var44;
                  if (var19 == 0) {
                     var19++;
                  }

                  if (var19 < var46.getLength()) {
                     if (this.runCount >= this.runs.length) {
                        TextRun[] var20 = new TextRun[this.runs.length + 64];
                        System.arraycopy(this.runs, 0, var20, 0, var44 + 1);
                        System.arraycopy(this.runs, var44 + 1, var20, var44 + 2, this.runs.length - var44 - 1);
                        this.runs = var20;
                     } else {
                        System.arraycopy(this.runs, var44 + 1, this.runs, var44 + 2, this.runCount - var44 - 1);
                     }

                     this.runs[var44 + 1] = var46.split(var19);
                     if (var46.isComplex()) {
                        this.shape(var46, var1, var2);
                     }

                     this.runCount++;
                  }
               }

               if (var10 + 1 < this.runCount && !this.runs[var10 + 1].isLinebreak()) {
                  var11 = this.runs[var10];
                  var11.setSoftbreak();
                  this.flags |= 128;
               }
            }

            var6 += var34;
            if (var11.isBreak()) {
               TextLine var36 = this.createLine(var7, var10, var8);
               var9.add(var36);
               var7 = var10 + 1;
               var8 += var36.getLength();
               var6 = 0.0F;
            }
         }

         if (var2 != null) {
            var2.dispose();
         }

         var9.add(this.createLine(var7, this.runCount - 1, var8));
         this.lines = new TextLine[var9.size()];
         var9.toArray(this.lines);
         float var32 = Math.max(this.wrapWidth, this.layoutWidth);
         float var33 = 0.0F;
         float var35;
         if (this.isMirrored()) {
            var35 = 1.0F;
            if (var5 == 1048576) {
               var35 = 0.0F;
            }
         } else {
            var35 = 0.0F;
            if (var5 == 1048576) {
               var35 = 1.0F;
            }
         }

         if (var5 == 524288) {
            var35 = 0.5F;
         }

         for (int var37 = 0; var37 < this.lines.length; var37++) {
            TextLine var39 = this.lines[var37];
            int var41 = var39.getStart();
            RectBounds var43 = var39.getBounds();
            float var45 = (var32 - var43.getWidth()) * var35;
            var39.setAlignment(var45);
            boolean var47 = this.wrapWidth > 0.0F && var5 == 2097152;
            if (var47) {
               TextRun[] var48 = var39.getRuns();
               int var50 = var48.length;
               if (var50 > 0 && var48[var50 - 1].isSoftbreak()) {
                  int var21 = var41 + var39.getLength();
                  int var22 = 0;
                  boolean var23 = false;

                  for (int var24 = var21 - 1; var24 >= var41; var24--) {
                     if (!var23 && var1[var24] != ' ') {
                        var23 = true;
                     }

                     if (var23 && var1[var24] == ' ') {
                        var22++;
                     }
                  }

                  if (var22 != 0) {
                     float var54 = (var32 - var43.getWidth()) / (float)var22;

                     label214:
                     for (int var25 = 0; var25 < var50; var25++) {
                        TextRun var26 = var48[var25];
                        int var27 = var26.getStart();
                        int var28 = var26.getEnd();

                        for (int var29 = var27; var29 < var28; var29++) {
                           if (var1[var29] == ' ') {
                              var26.justify(var29 - var27, var54);
                              if (--var22 == 0) {
                                 break label214;
                              }
                           }
                        }
                     }

                     var45 = 0.0F;
                     var39.setAlignment(var45);
                     var39.setWidth(var32);
                  }
               }
            }

            if ((this.flags & 8) != 0) {
               this.reorderLine(var39);
            }

            this.computeSideBearings(var39);
            float var49 = var45;
            TextRun[] var51 = var39.getRuns();

            for (int var52 = 0; var52 < var51.length; var52++) {
               TextRun var53 = var51[var52];
               var53.setLocation(var49, var33);
               var53.setLine(var39);
               var49 += var53.getWidth();
            }

            if (var37 + 1 < this.lines.length) {
               var33 = Math.max(var33, var33 + var43.getHeight() + this.spacing);
            } else {
               var33 += var43.getHeight() - var39.getLeading();
            }
         }

         float var38 = this.lines[0].getBounds().getMinY();
         this.layoutHeight = var33;
         this.logicalBounds = this.logicalBounds.deriveWithNewBounds(0.0F, var38, 0.0F, this.layoutWidth, this.layoutHeight + var38, 0.0F);
         if (this.layoutCache != null) {
            if (this.cacheKey != null && !this.layoutCache.valid && !this.copyCache()) {
               this.layoutCache.font = this.font;
               this.layoutCache.text = this.text;
               this.layoutCache.runs = this.runs;
               this.layoutCache.runCount = this.runCount;
               this.layoutCache.lines = this.lines;
               this.layoutCache.layoutWidth = this.layoutWidth;
               this.layoutCache.layoutHeight = this.layoutHeight;
               this.layoutCache.analysis = this.flags & 2047;
               synchronized (CACHE_SIZE_LOCK) {
                  int var42 = var1.length;
                  if (cacheSize + var42 > MAX_CACHE_SIZE) {
                     stringCache.clear();
                     cacheSize = 0;
                  }

                  stringCache.put(this.cacheKey, this.layoutCache);
                  cacheSize += var42;
               }
            }

            this.layoutCache.valid = true;
         }
      }
   }

   @Override
   public BaseBounds getVisualBounds(int var1) {
      this.ensureLayout();
      if (this.strike == null) {
         return null;
      } else {
         boolean var2 = (var1 & 2) != 0;
         boolean var3 = (this.flags & 512) != 0;
         boolean var4 = (var1 & 4) != 0;
         boolean var5 = (this.flags & 1024) != 0;
         if (this.visualBounds != null && var2 == var3 && var4 == var5) {
            return this.visualBounds;
         } else {
            this.flags &= -1537;
            if (var2) {
               this.flags |= 512;
            }

            if (var4) {
               this.flags |= 1024;
            }

            this.visualBounds = new RectBounds();
            float var6 = Float.POSITIVE_INFINITY;
            float var7 = Float.POSITIVE_INFINITY;
            float var8 = Float.NEGATIVE_INFINITY;
            float var9 = Float.NEGATIVE_INFINITY;
            float[] var10 = new float[4];
            FontResource var11 = this.strike.getFontResource();
            Metrics var12 = this.strike.getMetrics();
            float var13 = this.strike.getSize();

            for (int var14 = 0; var14 < this.lines.length; var14++) {
               TextLine var15 = this.lines[var14];
               TextRun[] var16 = var15.getRuns();

               for (int var17 = 0; var17 < var16.length; var17++) {
                  TextRun var18 = var16[var17];
                  Point2D var19 = var18.getLocation();
                  if (!var18.isLinebreak()) {
                     int var20 = var18.getGlyphCount();

                     for (int var21 = 0; var21 < var20; var21++) {
                        int var22 = var18.getGlyphCode(var21);
                        if (var22 != 65535) {
                           var11.getGlyphBoundingBox(var18.getGlyphCode(var21), var13, var10);
                           if (var10[0] != var10[2]) {
                              float var23 = var19.x + var18.getPosX(var21);
                              float var24 = var19.y + var18.getPosY(var21);
                              float var25 = var23 + var10[0];
                              float var26 = var24 - var10[3];
                              float var27 = var23 + var10[2];
                              float var28 = var24 - var10[1];
                              if (var25 < var6) {
                                 var6 = var25;
                              }

                              if (var26 < var7) {
                                 var7 = var26;
                              }

                              if (var27 > var8) {
                                 var8 = var27;
                              }

                              if (var28 > var9) {
                                 var9 = var28;
                              }
                           }
                        }
                     }

                     if (var2) {
                        float var29 = var19.x;
                        float var31 = var19.y + var12.getUnderLineOffset();
                        float var33 = var29 + var18.getWidth();
                        float var35 = var31 + var12.getUnderLineThickness();
                        if (var29 < var6) {
                           var6 = var29;
                        }

                        if (var31 < var7) {
                           var7 = var31;
                        }

                        if (var33 > var8) {
                           var8 = var33;
                        }

                        if (var35 > var9) {
                           var9 = var35;
                        }
                     }

                     if (var4) {
                        float var30 = var19.x;
                        float var32 = var19.y + var12.getStrikethroughOffset();
                        float var34 = var30 + var18.getWidth();
                        float var36 = var32 + var12.getStrikethroughThickness();
                        if (var30 < var6) {
                           var6 = var30;
                        }

                        if (var32 < var7) {
                           var7 = var32;
                        }

                        if (var34 > var8) {
                           var8 = var34;
                        }

                        if (var36 > var9) {
                           var9 = var36;
                        }
                     }
                  }
               }
            }

            if (var6 < var8 && var7 < var9) {
               this.visualBounds.setBounds(var6, var7, var8, var9);
            }

            return this.visualBounds;
         }
      }
   }

   private void computeSideBearings(TextLine var1) {
      TextRun[] var2 = var1.getRuns();
      if (var2.length != 0) {
         float[] var3 = new float[4];
         FontResource var4 = null;
         float var5 = 0.0F;
         if (this.strike != null) {
            var4 = this.strike.getFontResource();
            var5 = this.strike.getSize();
         }

         float var6 = 0.0F;
         float var7 = 0.0F;

         label70:
         for (int var8 = 0; var8 < var2.length; var8++) {
            TextRun var9 = var2[var8];
            int var10 = var9.getGlyphCount();

            for (int var11 = 0; var11 < var10; var11++) {
               float var12 = var9.getAdvance(var11);
               if (var12 != 0.0F) {
                  int var13 = var9.getGlyphCode(var11);
                  if (var13 != 65535) {
                     FontResource var14 = var4;
                     if (var4 == null) {
                        TextSpan var15 = var9.getTextSpan();
                        PGFont var16 = (PGFont)var15.getFont();
                        var5 = var16.getSize();
                        var14 = var16.getFontResource();
                     }

                     var14.getGlyphBoundingBox(var13, var5, var3);
                     float var26 = var3[0];
                     var6 = Math.min(0.0F, var26 + var7);
                     var9.setLeftBearing();
                     break label70;
                  }
               }

               var7 += var12;
            }

            if (var10 == 0) {
               var7 += var9.getWidth();
            }
         }

         float var19 = 0.0F;
         var7 = 0.0F;

         label57:
         for (int var20 = var2.length - 1; var20 >= 0; var20--) {
            TextRun var21 = var2[var20];
            int var22 = var21.getGlyphCount();

            for (int var23 = var22 - 1; var23 >= 0; var23--) {
               float var24 = var21.getAdvance(var23);
               if (var24 != 0.0F) {
                  int var25 = var21.getGlyphCode(var23);
                  if (var25 != 65535) {
                     FontResource var27 = var4;
                     if (var4 == null) {
                        TextSpan var28 = var21.getTextSpan();
                        PGFont var17 = (PGFont)var28.getFont();
                        var5 = var17.getSize();
                        var27 = var17.getFontResource();
                     }

                     var27.getGlyphBoundingBox(var25, var5, var3);
                     float var29 = var3[2] - var24;
                     var19 = Math.max(0.0F, var29 - var7);
                     var21.setRightBearing();
                     break label57;
                  }
               }

               var7 += var24;
            }

            if (var22 == 0) {
               var7 += var21.getWidth();
            }
         }

         var1.setSideBearings(var6, var19);
      }
   }
}
