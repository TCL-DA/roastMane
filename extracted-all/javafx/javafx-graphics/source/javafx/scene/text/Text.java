package javafx.scene.text;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.TransformedShape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.scene.shape.TextHelper;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLayoutFactory;
import com.sun.javafx.scene.text.TextLine;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.sg.prism.NGText;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

@DefaultProperty("text")
public class Text extends Shape {
   private TextLayout layout;
   private static final PathElement[] EMPTY_PATH_ELEMENT_ARRAY = new PathElement[0];
   private boolean isSpan;
   private TextSpan textSpan;
   private GlyphList[] textRuns;
   private BaseBounds spanBounds;
   private boolean spanBoundsInvalid;
   private StringProperty text;
   private DoubleProperty x;
   private DoubleProperty y;
   private ObjectProperty<Font> font;
   private ObjectProperty<TextBoundsType> boundsType;
   private DoubleProperty wrappingWidth;
   private ObjectProperty<FontSmoothingType> fontSmoothingType;
   private Text.TextAttribute attributes;
   private static final VPos DEFAULT_TEXT_ORIGIN = VPos.BASELINE;
   private static final TextBoundsType DEFAULT_BOUNDS_TYPE = TextBoundsType.LOGICAL;
   private static final boolean DEFAULT_UNDERLINE = false;
   private static final boolean DEFAULT_STRIKETHROUGH = false;
   private static final TextAlignment DEFAULT_TEXT_ALIGNMENT = TextAlignment.LEFT;
   private static final double DEFAULT_LINE_SPACING = 0.0;
   private static final int DEFAULT_CARET_POSITION = -1;
   private static final int DEFAULT_SELECTION_START = -1;
   private static final int DEFAULT_SELECTION_END = -1;
   private static final Color DEFAULT_SELECTION_FILL = Color.WHITE;
   private static final boolean DEFAULT_CARET_BIAS = true;

   public Text() {
      TextHelper.initHelper(this);
      this.textRuns = null;
      this.spanBounds = new RectBounds();
      this.spanBoundsInvalid = true;
      this.setAccessibleRole(AccessibleRole.TEXT);
      InvalidationListener var1 = var1x -> this.checkSpan();
      this.parentProperty().addListener(var1);
      this.managedProperty().addListener(var1);
      this.effectiveNodeOrientationProperty().addListener(var1x -> this.checkOrientation());
      this.setPickOnBounds(true);
   }

   public Text(String var1) {
      this();
      this.setText(var1);
   }

   public Text(double var1, double var3, String var5) {
      this(var5);
      this.setX(var1);
      this.setY(var3);
   }

   private NGNode doCreatePeer() {
      return new NGText();
   }

   private boolean isSpan() {
      return this.isSpan;
   }

   private void checkSpan() {
      this.isSpan = this.isManaged() && this.getParent() instanceof TextFlow;
      if (this.isSpan() && !this.pickOnBoundsProperty().isBound()) {
         this.setPickOnBounds(false);
      }
   }

   private void checkOrientation() {
      if (!this.isSpan()) {
         NodeOrientation var1 = this.getEffectiveNodeOrientation();
         boolean var2 = var1 == NodeOrientation.RIGHT_TO_LEFT;
         int var3 = var2 ? 2048 : 1024;
         TextLayout var4 = this.getTextLayout();
         if (var4.setDirection(var3)) {
            this.needsTextLayout();
         }
      }
   }

   @Override
   public boolean usesMirroring() {
      return false;
   }

   private void needsFullTextLayout() {
      if (this.isSpan()) {
         this.textSpan = null;
      } else {
         TextLayout var1 = this.getTextLayout();
         String var2 = this.getTextInternal();
         Object var3 = this.getFontInternal();
         var1.setContent(var2, var3);
      }

      this.needsTextLayout();
   }

   private void needsTextLayout() {
      this.textRuns = null;
      NodeHelper.geomChanged(this);
      NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
   }

   TextSpan getTextSpan() {
      if (this.textSpan == null) {
         this.textSpan = new TextSpan() {
            @Override
            public String getText() {
               return Text.this.getTextInternal();
            }

            @Override
            public Object getFont() {
               return Text.this.getFontInternal();
            }

            @Override
            public RectBounds getBounds() {
               return null;
            }
         };
      }

      return this.textSpan;
   }

   private TextLayout getTextLayout() {
      if (this.isSpan()) {
         this.layout = null;
         TextFlow var5 = (TextFlow)this.getParent();
         return var5.getTextLayout();
      } else {
         if (this.layout == null) {
            TextLayoutFactory var1 = Toolkit.getToolkit().getTextLayoutFactory();
            this.layout = var1.createLayout();
            String var2 = this.getTextInternal();
            Object var3 = this.getFontInternal();
            TextAlignment var4 = this.getTextAlignment();
            if (var4 == null) {
               var4 = DEFAULT_TEXT_ALIGNMENT;
            }

            this.layout.setContent(var2, var3);
            this.layout.setAlignment(var4.ordinal());
            this.layout.setLineSpacing((float)this.getLineSpacing());
            this.layout.setWrapWidth((float)this.getWrappingWidth());
            if (this.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
               this.layout.setDirection(2048);
            } else {
               this.layout.setDirection(1024);
            }

            this.layout.setTabSize(this.getTabSize());
         }

         return this.layout;
      }
   }

   void layoutSpan(GlyphList[] var1) {
      TextSpan var2 = this.getTextSpan();
      int var3 = 0;

      for (int var4 = 0; var4 < var1.length; var4++) {
         GlyphList var5 = var1[var4];
         if (var5.getTextSpan() == var2) {
            var3++;
         }
      }

      this.textRuns = new GlyphList[var3];
      var3 = 0;

      for (int var7 = 0; var7 < var1.length; var7++) {
         GlyphList var8 = var1[var7];
         if (var8.getTextSpan() == var2) {
            this.textRuns[var3++] = var8;
         }
      }

      this.spanBoundsInvalid = true;
      NodeHelper.geomChanged(this);
      NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
   }

   BaseBounds getSpanBounds() {
      if (this.spanBoundsInvalid) {
         GlyphList[] var1 = this.getRuns();
         if (var1.length == 0) {
            this.spanBounds = this.spanBounds.makeEmpty();
         } else {
            float var2 = Float.POSITIVE_INFINITY;
            float var3 = Float.POSITIVE_INFINITY;
            float var4 = 0.0F;
            float var5 = 0.0F;

            for (int var6 = 0; var6 < var1.length; var6++) {
               GlyphList var7 = var1[var6];
               Point2D var8 = var7.getLocation();
               float var9 = var7.getWidth();
               float var10 = var7.getLineBounds().getHeight();
               var2 = Math.min(var8.x, var2);
               var3 = Math.min(var8.y, var3);
               var4 = Math.max(var8.x + var9, var4);
               var5 = Math.max(var8.y + var10, var5);
            }

            this.spanBounds = this.spanBounds.deriveWithNewBounds(var2, var3, 0.0F, var4, var5, 0.0F);
         }

         this.spanBoundsInvalid = false;
      }

      return this.spanBounds;
   }

   private GlyphList[] getRuns() {
      if (this.textRuns != null) {
         return this.textRuns;
      } else {
         if (this.isSpan()) {
            this.getParent().layout();
         } else {
            TextLayout var1 = this.getTextLayout();
            this.textRuns = var1.getRuns();
         }

         return this.textRuns;
      }
   }

   private com.sun.javafx.geom.Shape getShape() {
      TextLayout var1 = this.getTextLayout();
      int var2 = 1;
      if (this.isStrikethrough()) {
         var2 |= 4;
      }

      if (this.isUnderline()) {
         var2 |= 2;
      }

      TextSpan var3 = null;
      if (this.isSpan()) {
         var2 |= 16;
         var3 = this.getTextSpan();
      } else {
         var2 |= 8;
      }

      return var1.getShape(var2, var3);
   }

   private BaseBounds getVisualBounds() {
      if (ShapeHelper.getMode(this) != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
         return this.getShape().getBounds();
      } else {
         byte var1 = 1;
         if (this.isStrikethrough()) {
            var1 |= 4;
         }

         if (this.isUnderline()) {
            var1 |= 2;
         }

         return this.getTextLayout().getVisualBounds(var1);
      }
   }

   private BaseBounds getLogicalBounds() {
      TextLayout var1 = this.getTextLayout();
      return var1.getBounds();
   }

   public final void setText(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      this.textProperty().set(var1);
   }

   public final String getText() {
      return this.text == null ? "" : (String)this.text.get();
   }

   private String getTextInternal() {
      String var1 = this.getText();
      return var1 == null ? "" : var1;
   }

   public final StringProperty textProperty() {
      if (this.text == null) {
         this.text = new StringPropertyBase("") {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "text";
            }

            public void invalidated() {
               Text.this.needsFullTextLayout();
               Text.this.setSelectionStart(-1);
               Text.this.setSelectionEnd(-1);
               Text.this.setCaretPosition(-1);
               Text.this.setCaretBias(true);
               String var1 = this.get();
               if (var1 == null && !this.isBound()) {
                  this.set("");
               }

               Text.this.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
            }
         };
      }

      return this.text;
   }

   public final void setX(double var1) {
      this.xProperty().set(var1);
   }

   public final double getX() {
      return this.x == null ? 0.0 : this.x.get();
   }

   public final DoubleProperty xProperty() {
      if (this.x == null) {
         this.x = new DoublePropertyBase() {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "x";
            }

            public void invalidated() {
               NodeHelper.geomChanged(Text.this);
            }
         };
      }

      return this.x;
   }

   public final void setY(double var1) {
      this.yProperty().set(var1);
   }

   public final double getY() {
      return this.y == null ? 0.0 : this.y.get();
   }

   public final DoubleProperty yProperty() {
      if (this.y == null) {
         this.y = new DoublePropertyBase() {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "y";
            }

            public void invalidated() {
               NodeHelper.geomChanged(Text.this);
            }
         };
      }

      return this.y;
   }

   public final void setFont(Font var1) {
      this.fontProperty().set(var1);
   }

   public final Font getFont() {
      return this.font == null ? Font.getDefault() : (Font)this.font.get();
   }

   private Object getFontInternal() {
      Font var1 = this.getFont();
      if (var1 == null) {
         var1 = Font.getDefault();
      }

      return FontHelper.getNativeFont(var1);
   }

   public final ObjectProperty<Font> fontProperty() {
      if (this.font == null) {
         this.font = new StyleableObjectProperty<Font>(Font.getDefault()) {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "font";
            }

            @Override
            public CssMetaData<Text, Font> getCssMetaData() {
               return Text.StyleableProperties.FONT;
            }

            public void invalidated() {
               Text.this.needsFullTextLayout();
               NodeHelper.markDirty(Text.this, DirtyBits.TEXT_FONT);
            }
         };
      }

      return this.font;
   }

   public final void setTextOrigin(VPos var1) {
      this.textOriginProperty().set(var1);
   }

   public final VPos getTextOrigin() {
      return this.attributes != null && this.attributes.textOrigin != null ? this.attributes.getTextOrigin() : DEFAULT_TEXT_ORIGIN;
   }

   public final ObjectProperty<VPos> textOriginProperty() {
      return this.getTextAttribute().textOriginProperty();
   }

   public final void setBoundsType(TextBoundsType var1) {
      this.boundsTypeProperty().set(var1);
   }

   public final TextBoundsType getBoundsType() {
      return this.boundsType == null ? DEFAULT_BOUNDS_TYPE : (TextBoundsType)this.boundsTypeProperty().get();
   }

   public final ObjectProperty<TextBoundsType> boundsTypeProperty() {
      if (this.boundsType == null) {
         this.boundsType = new StyleableObjectProperty<TextBoundsType>(DEFAULT_BOUNDS_TYPE) {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "boundsType";
            }

            @Override
            public CssMetaData<Text, TextBoundsType> getCssMetaData() {
               return Text.StyleableProperties.BOUNDS_TYPE;
            }

            public void invalidated() {
               TextLayout var1 = Text.this.getTextLayout();
               short var2 = 0;
               if (Text.this.boundsType.get() == TextBoundsType.LOGICAL_VERTICAL_CENTER) {
                  var2 |= 16384;
               }

               if (var1.setBoundsType(var2)) {
                  Text.this.needsTextLayout();
               } else {
                  NodeHelper.geomChanged(Text.this);
               }
            }
         };
      }

      return this.boundsType;
   }

   public final void setWrappingWidth(double var1) {
      this.wrappingWidthProperty().set(var1);
   }

   public final double getWrappingWidth() {
      return this.wrappingWidth == null ? 0.0 : this.wrappingWidth.get();
   }

   public final DoubleProperty wrappingWidthProperty() {
      if (this.wrappingWidth == null) {
         this.wrappingWidth = new DoublePropertyBase() {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "wrappingWidth";
            }

            public void invalidated() {
               if (!Text.this.isSpan()) {
                  TextLayout var1 = Text.this.getTextLayout();
                  if (var1.setWrapWidth((float)this.get())) {
                     Text.this.needsTextLayout();
                  } else {
                     NodeHelper.geomChanged(Text.this);
                  }
               }
            }
         };
      }

      return this.wrappingWidth;
   }

   public final void setUnderline(boolean var1) {
      this.underlineProperty().set(var1);
   }

   public final boolean isUnderline() {
      return this.attributes != null && this.attributes.underline != null ? this.attributes.isUnderline() : false;
   }

   public final BooleanProperty underlineProperty() {
      return this.getTextAttribute().underlineProperty();
   }

   public final void setStrikethrough(boolean var1) {
      this.strikethroughProperty().set(var1);
   }

   public final boolean isStrikethrough() {
      return this.attributes != null && this.attributes.strikethrough != null ? this.attributes.isStrikethrough() : false;
   }

   public final BooleanProperty strikethroughProperty() {
      return this.getTextAttribute().strikethroughProperty();
   }

   public final void setTextAlignment(TextAlignment var1) {
      this.textAlignmentProperty().set(var1);
   }

   public final TextAlignment getTextAlignment() {
      return this.attributes != null && this.attributes.textAlignment != null ? this.attributes.getTextAlignment() : DEFAULT_TEXT_ALIGNMENT;
   }

   public final ObjectProperty<TextAlignment> textAlignmentProperty() {
      return this.getTextAttribute().textAlignmentProperty();
   }

   public final void setLineSpacing(double var1) {
      this.lineSpacingProperty().set(var1);
   }

   public final double getLineSpacing() {
      return this.attributes != null && this.attributes.lineSpacing != null ? this.attributes.getLineSpacing() : 0.0;
   }

   public final DoubleProperty lineSpacingProperty() {
      return this.getTextAttribute().lineSpacingProperty();
   }

   @Override
   public final double getBaselineOffset() {
      return this.baselineOffsetProperty().get();
   }

   public final ReadOnlyDoubleProperty baselineOffsetProperty() {
      return this.getTextAttribute().baselineOffsetProperty();
   }

   public final void setFontSmoothingType(FontSmoothingType var1) {
      this.fontSmoothingTypeProperty().set(var1);
   }

   public final FontSmoothingType getFontSmoothingType() {
      return this.fontSmoothingType == null ? FontSmoothingType.GRAY : (FontSmoothingType)this.fontSmoothingType.get();
   }

   public final ObjectProperty<FontSmoothingType> fontSmoothingTypeProperty() {
      if (this.fontSmoothingType == null) {
         this.fontSmoothingType = new StyleableObjectProperty<FontSmoothingType>(FontSmoothingType.GRAY) {
            public Object getBean() {
               return Text.this;
            }

            public String getName() {
               return "fontSmoothingType";
            }

            @Override
            public CssMetaData<Text, FontSmoothingType> getCssMetaData() {
               return Text.StyleableProperties.FONT_SMOOTHING_TYPE;
            }

            public void invalidated() {
               NodeHelper.markDirty(Text.this, DirtyBits.TEXT_ATTRS);
               NodeHelper.geomChanged(Text.this);
            }
         };
      }

      return this.fontSmoothingType;
   }

   private void doGeomChanged() {
      if (this.attributes != null) {
         if (this.attributes.caretBinding != null) {
            this.attributes.caretBinding.invalidate();
         }

         if (this.attributes.selectionBinding != null) {
            this.attributes.selectionBinding.invalidate();
         }
      }

      NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
   }

   public final PathElement[] getSelectionShape() {
      return (PathElement[])this.selectionShapeProperty().get();
   }

   public final ReadOnlyObjectProperty<PathElement[]> selectionShapeProperty() {
      return this.getTextAttribute().selectionShapeProperty();
   }

   public final void setSelectionStart(int var1) {
      if (var1 != -1 || this.attributes != null && this.attributes.selectionStart != null) {
         this.selectionStartProperty().set(var1);
      }
   }

   public final int getSelectionStart() {
      return this.attributes != null && this.attributes.selectionStart != null ? this.attributes.getSelectionStart() : -1;
   }

   public final IntegerProperty selectionStartProperty() {
      return this.getTextAttribute().selectionStartProperty();
   }

   public final void setSelectionEnd(int var1) {
      if (var1 != -1 || this.attributes != null && this.attributes.selectionEnd != null) {
         this.selectionEndProperty().set(var1);
      }
   }

   public final int getSelectionEnd() {
      return this.attributes != null && this.attributes.selectionEnd != null ? this.attributes.getSelectionEnd() : -1;
   }

   public final IntegerProperty selectionEndProperty() {
      return this.getTextAttribute().selectionEndProperty();
   }

   public final ObjectProperty<Paint> selectionFillProperty() {
      return this.getTextAttribute().selectionFillProperty();
   }

   public final void setSelectionFill(Paint var1) {
      this.selectionFillProperty().set(var1);
   }

   public final Paint getSelectionFill() {
      return (Paint)this.selectionFillProperty().get();
   }

   public final PathElement[] getCaretShape() {
      return (PathElement[])this.caretShapeProperty().get();
   }

   public final ReadOnlyObjectProperty<PathElement[]> caretShapeProperty() {
      return this.getTextAttribute().caretShapeProperty();
   }

   public final void setCaretPosition(int var1) {
      if (var1 != -1 || this.attributes != null && this.attributes.caretPosition != null) {
         this.caretPositionProperty().set(var1);
      }
   }

   public final int getCaretPosition() {
      return this.attributes != null && this.attributes.caretPosition != null ? this.attributes.getCaretPosition() : -1;
   }

   public final IntegerProperty caretPositionProperty() {
      return this.getTextAttribute().caretPositionProperty();
   }

   public final void setCaretBias(boolean var1) {
      if (!var1 || this.attributes != null && this.attributes.caretBias != null) {
         this.caretBiasProperty().set(var1);
      }
   }

   public final boolean isCaretBias() {
      return this.attributes != null && this.attributes.caretBias != null ? this.getTextAttribute().isCaretBias() : true;
   }

   public final BooleanProperty caretBiasProperty() {
      return this.getTextAttribute().caretBiasProperty();
   }

   public final HitInfo hitTest(javafx.geometry.Point2D var1) {
      if (var1 == null) {
         return null;
      } else {
         TextLayout var2 = this.getTextLayout();
         double var3 = var1.getX() - this.getX();
         double var5 = var1.getY() - this.getY() + (double)this.getYRendering();
         TextLayout.Hit var7 = var2.getHitInfo((float)var3, (float)var5);
         return new HitInfo(var7.getCharIndex(), var7.getInsertionIndex(), var7.isLeading(), this.getText());
      }
   }

   private PathElement[] getRange(int var1, int var2, int var3) {
      int var4 = this.getTextInternal().length();
      if (0 <= var1 && var1 < var2 && var2 <= var4) {
         TextLayout var5 = this.getTextLayout();
         float var6 = (float)this.getX();
         float var7 = (float)this.getY() - this.getYRendering();
         return var5.getRange(var1, var2, var3, var6, var7);
      } else {
         return EMPTY_PATH_ELEMENT_ARRAY;
      }
   }

   public final PathElement[] caretShape(int var1, boolean var2) {
      if (0 <= var1 && var1 <= this.getTextInternal().length()) {
         float var3 = (float)this.getX();
         float var4 = (float)this.getY() - this.getYRendering();
         return this.getTextLayout().getCaretShape(var1, var2, var3, var4);
      } else {
         return null;
      }
   }

   public final PathElement[] rangeShape(int var1, int var2) {
      return this.getRange(var1, var2, 1);
   }

   public final PathElement[] underlineShape(int var1, int var2) {
      return this.getRange(var1, var2, 2);
   }

   private float getYAdjustment(BaseBounds var1) {
      VPos var2 = this.getTextOrigin();
      if (var2 == null) {
         var2 = DEFAULT_TEXT_ORIGIN;
      }

      switch (var2) {
         case TOP:
            return -var1.getMinY();
         case BASELINE:
            return 0.0F;
         case CENTER:
            return -var1.getMinY() - var1.getHeight() / 2.0F;
         case BOTTOM:
            return -var1.getMinY() - var1.getHeight();
         default:
            return 0.0F;
      }
   }

   private float getYRendering() {
      if (this.isSpan()) {
         return 0.0F;
      } else {
         BaseBounds var1 = this.getLogicalBounds();
         VPos var2 = this.getTextOrigin();
         if (var2 == null) {
            var2 = DEFAULT_TEXT_ORIGIN;
         }

         if (this.getBoundsType() == TextBoundsType.VISUAL) {
            BaseBounds var3 = this.getVisualBounds();
            float var4 = var3.getMinY() - var1.getMinY();
            switch (var2) {
               case TOP:
                  return var4;
               case BASELINE:
                  return -var3.getMinY() + var4;
               case CENTER:
                  return var3.getHeight() / 2.0F + var4;
               case BOTTOM:
                  return var3.getHeight() + var4;
               default:
                  return 0.0F;
            }
         } else {
            switch (var2) {
               case TOP:
                  return 0.0F;
               case BASELINE:
                  return -var1.getMinY();
               case CENTER:
                  return var1.getHeight() / 2.0F;
               case BOTTOM:
                  return var1.getHeight();
               default:
                  return 0.0F;
            }
         }
      }
   }

   private Bounds doComputeLayoutBounds() {
      if (this.isSpan()) {
         BaseBounds var12 = this.getSpanBounds();
         double var13 = (double)var12.getWidth();
         double var14 = (double)var12.getHeight();
         return new BoundingBox(0.0, 0.0, var13, var14);
      } else if (this.getBoundsType() == TextBoundsType.VISUAL) {
         return TextHelper.superComputeLayoutBounds(this);
      } else {
         BaseBounds var1 = this.getLogicalBounds();
         double var2 = (double)var1.getMinX() + this.getX();
         double var4 = (double)var1.getMinY() + this.getY() + (double)this.getYAdjustment(var1);
         double var6 = (double)var1.getWidth();
         double var8 = (double)var1.getHeight();
         double var10 = this.getWrappingWidth();
         if (var10 != 0.0) {
            var6 = var10;
         }

         return new BoundingBox(var2, var4, var6, var8);
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (this.isSpan()) {
         if (ShapeHelper.getMode(this) != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
            return TextHelper.superComputeGeomBounds(this, var1, var2);
         } else {
            TextLayout var18 = this.getTextLayout();
            var1 = var18.getBounds(this.getTextSpan(), var1);
            BaseBounds var21 = this.getSpanBounds();
            float var23 = var1.getMinX() - var21.getMinX();
            float var25 = var1.getMinY() - var21.getMinY();
            float var27 = var23 + var1.getWidth();
            float var28 = var25 + var1.getHeight();
            var1 = var1.deriveWithNewBounds(var23, var25, 0.0F, var27, var28, 0.0F);
            return var2.transform(var1, var1);
         }
      } else if (this.getBoundsType() == TextBoundsType.VISUAL) {
         if (this.getTextInternal().length() == 0 || ShapeHelper.getMode(this) == NGShape.Mode.EMPTY) {
            return var1.makeEmpty();
         } else if (ShapeHelper.getMode(this) != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
            return TextHelper.superComputeGeomBounds(this, var1, var2);
         } else {
            BaseBounds var17 = this.getVisualBounds();
            float var20 = var17.getMinX() + (float)this.getX();
            float var22 = this.getYAdjustment(var17);
            float var24 = var17.getMinY() + var22 + (float)this.getY();
            var1.deriveWithNewBounds(var20, var24, 0.0F, var20 + var17.getWidth(), var24 + var17.getHeight(), 0.0F);
            return var2.transform(var1, var1);
         }
      } else {
         BaseBounds var3 = this.getLogicalBounds();
         float var4 = var3.getMinX() + (float)this.getX();
         float var5 = this.getYAdjustment(var3);
         float var6 = var3.getMinY() + var5 + (float)this.getY();
         float var7 = var3.getWidth();
         float var8 = var3.getHeight();
         float var9 = (float)this.getWrappingWidth();
         if (var9 > var7) {
            var7 = var9;
         } else if (var9 > 0.0F) {
            NodeOrientation var10 = this.getEffectiveNodeOrientation();
            if (var10 == NodeOrientation.RIGHT_TO_LEFT) {
               var4 -= var7 - var9;
            }
         }

         RectBounds var16 = new RectBounds(var4, var6, var4 + var7, var6 + var8);
         if (ShapeHelper.getMode(this) != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
            var1 = TextHelper.superComputeGeomBounds(this, var1, BaseTransform.IDENTITY_TRANSFORM);
         } else {
            TextLayout var29 = this.getTextLayout();
            var1 = var29.getBounds(null, var1);
            var4 = var1.getMinX() + (float)this.getX();
            var7 = var1.getWidth();
            var1 = var1.deriveWithNewBounds(var4, var6, 0.0F, var4 + var7, var6 + var8, 0.0F);
         }

         var1 = var1.deriveWithUnion(var16);
         return var2.transform(var1, var1);
      }
   }

   private boolean doComputeContains(double var1, double var3) {
      double var5 = var1 + (double)this.getSpanBounds().getMinX();
      double var7 = var3 + (double)this.getSpanBounds().getMinY();
      GlyphList[] var9 = this.getRuns();
      if (var9.length != 0) {
         for (int var10 = 0; var10 < var9.length; var10++) {
            GlyphList var11 = var9[var10];
            Point2D var12 = var11.getLocation();
            float var13 = var11.getWidth();
            RectBounds var14 = var11.getLineBounds();
            float var15 = var14.getHeight();
            if ((double)var12.x <= var5 && var5 < (double)(var12.x + var13) && (double)var12.y <= var7 && var7 < (double)(var12.y + var15)) {
               return true;
            }
         }
      }

      return false;
   }

   private com.sun.javafx.geom.Shape doConfigShape() {
      if (ShapeHelper.getMode(this) != NGShape.Mode.EMPTY && this.getTextInternal().length() != 0) {
         com.sun.javafx.geom.Shape var1 = this.getShape();
         float var2;
         float var3;
         if (this.isSpan()) {
            BaseBounds var4 = this.getSpanBounds();
            var2 = -var4.getMinX();
            var3 = -var4.getMinY();
         } else {
            var2 = (float)this.getX();
            var3 = this.getYAdjustment(this.getVisualBounds()) + (float)this.getY();
         }

         return TransformedShape.translatedShape(var1, (double)var2, (double)var3);
      } else {
         return new Path2D();
      }
   }

   public final IntegerProperty tabSizeProperty() {
      return this.getTextAttribute().tabSizeProperty();
   }

   public final int getTabSize() {
      return this.attributes != null && this.attributes.tabSize != null ? this.getTextAttribute().getTabSize() : 8;
   }

   public final void setTabSize(int var1) {
      this.tabSizeProperty().set(var1);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Text.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private void updatePGText() {
      NGText var1 = NodeHelper.getPeer(this);
      if (NodeHelper.isDirty(this, DirtyBits.TEXT_ATTRS)) {
         var1.setUnderline(this.isUnderline());
         var1.setStrikethrough(this.isStrikethrough());
         FontSmoothingType var2 = this.getFontSmoothingType();
         if (var2 == null) {
            var2 = FontSmoothingType.GRAY;
         }

         var1.setFontSmoothingType(var2.ordinal());
      }

      if (NodeHelper.isDirty(this, DirtyBits.TEXT_FONT)) {
         var1.setFont(this.getFontInternal());
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
         var1.setGlyphs(this.getRuns());
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
         if (this.isSpan()) {
            BaseBounds var7 = this.getSpanBounds();
            var1.setLayoutLocation(var7.getMinX(), var7.getMinY());
         } else {
            float var8 = (float)this.getX();
            float var3 = (float)this.getY();
            float var4 = this.getYRendering();
            var1.setLayoutLocation(-var8, var4 - var3);
         }
      }

      if (NodeHelper.isDirty(this, DirtyBits.TEXT_SELECTION)) {
         Object var9 = null;
         int var10 = this.getSelectionStart();
         int var11 = this.getSelectionEnd();
         int var5 = this.getTextInternal().length();
         if (0 <= var10 && var10 < var11 && var11 <= var5) {
            Paint var6 = (Paint)this.selectionFillProperty().get();
            var9 = var6 != null ? Toolkit.getPaintAccessor().getPlatformPaint(var6) : null;
         }

         var1.setSelection(var10, var11, var9);
      }
   }

   private void doUpdatePeer() {
      this.updatePGText();
   }

   private Text.TextAttribute getTextAttribute() {
      if (this.attributes == null) {
         this.attributes = new Text.TextAttribute();
      }

      return this.attributes;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Text[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("text=\"").append(this.getText()).append("\"");
      var1.append(", x=").append(this.getX());
      var1.append(", y=").append(this.getY());
      var1.append(", alignment=").append(this.getTextAlignment());
      var1.append(", origin=").append(this.getTextOrigin());
      var1.append(", boundsType=").append(this.getBoundsType());
      double var3 = this.getLineSpacing();
      if (var3 != 0.0) {
         var1.append(", lineSpacing=").append(var3);
      }

      double var5 = this.getWrappingWidth();
      if (var5 != 0.0) {
         var1.append(", wrappingWidth=").append(var5);
      }

      int var7 = this.getTabSize();
      if (var7 != 8) {
         var1.append(", tabSize=").append(var7);
      }

      var1.append(", font=").append(this.getFont());
      var1.append(", fontSmoothingType=").append(this.getFontSmoothingType());
      if (this.isStrikethrough()) {
         var1.append(", strikethrough");
      }

      if (this.isUnderline()) {
         var1.append(", underline");
      }

      var1.append(", fill=").append(this.getFill());
      Paint var8 = this.getStroke();
      if (var8 != null) {
         var1.append(", stroke=").append(var8);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case TEXT:
            String var23 = this.getAccessibleText();
            if (var23 != null && !var23.isEmpty()) {
               return var23;
            }

            return this.getText();
         case FONT:
            return this.getFont();
         case CARET_OFFSET:
            int var22 = this.getCaretPosition();
            if (var22 >= 0) {
               return var22;
            }

            return this.getText().length();
         case SELECTION_START:
            int var20 = this.getSelectionStart();
            if (var20 >= 0) {
               return var20;
            } else {
               var20 = this.getCaretPosition();
               if (var20 >= 0) {
                  return var20;
               }

               return this.getText().length();
            }
         case SELECTION_END:
            int var18 = this.getSelectionEnd();
            if (var18 >= 0) {
               return var18;
            } else {
               var18 = this.getCaretPosition();
               if (var18 >= 0) {
                  return var18;
               }

               return this.getText().length();
            }
         case LINE_FOR_OFFSET:
            int var17 = (Integer)var2[0];
            if (var17 > this.getTextInternal().length()) {
               return null;
            }

            TextLine[] var26 = this.getTextLayout().getLines();
            int var29 = 0;

            for (int var30 = 1; var30 < var26.length; var30++) {
               TextLine var31 = var26[var30];
               if (var31.getStart() > var17) {
                  break;
               }

               var29++;
            }

            return var29;
         case LINE_START:
            int var16 = (Integer)var2[0];
            TextLine[] var25 = this.getTextLayout().getLines();
            if (0 <= var16 && var16 < var25.length) {
               TextLine var28 = var25[var16];
               return var28.getStart();
            }

            return null;
         case LINE_END:
            int var15 = (Integer)var2[0];
            TextLine[] var24 = this.getTextLayout().getLines();
            if (0 <= var15 && var15 < var24.length) {
               TextLine var27 = var24[var15];
               return var27.getStart() + var27.getLength();
            }

            return null;
         case OFFSET_AT_POINT:
            javafx.geometry.Point2D var13 = (javafx.geometry.Point2D)var2[0];
            var13 = this.screenToLocal(var13);
            return this.hitTest(var13).getCharIndex();
         case BOUNDS_FOR_RANGE:
            int var3 = (Integer)var2[0];
            int var4 = (Integer)var2[1];
            PathElement[] var5 = this.rangeShape(var3, var4 + 1);
            Bounds[] var6 = new Bounds[var5.length / 5];
            byte var7 = 0;

            for (int var8 = 0; var8 < var6.length; var8++) {
               MoveTo var9 = (MoveTo)var5[var7];
               LineTo var10 = (LineTo)var5[var7 + 1];
               LineTo var11 = (LineTo)var5[var7 + 2];
               BoundingBox var12 = new BoundingBox(var9.getX(), var9.getY(), var10.getX() - var9.getX(), var11.getY() - var10.getY());
               var6[var8] = this.localToScreen(var12);
               var7 += 5;
            }

            return var6;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   static {
      TextHelper.setTextAccessor(new TextHelper.TextAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Text)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Text)var1).doUpdatePeer();
         }

         @Override
         public Bounds doComputeLayoutBounds(Node var1) {
            return ((Text)var1).doComputeLayoutBounds();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Text)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Text)var1).doComputeContains(var2, var4);
         }

         @Override
         public void doGeomChanged(Node var1) {
            ((Text)var1).doGeomChanged();
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((Text)var1).doConfigShape();
         }
      });
   }

   private static class StyleableProperties {
      private static final CssMetaData<Text, Font> FONT = new FontCssMetaData<Text>("-fx-font", Font.getDefault()) {
         public boolean isSettable(Text var1) {
            return var1.font == null || !var1.font.isBound();
         }

         public StyleableProperty<Font> getStyleableProperty(Text var1) {
            return (StyleableProperty<Font>)var1.fontProperty();
         }
      };
      private static final CssMetaData<Text, Boolean> UNDERLINE = new CssMetaData<Text, Boolean>("-fx-underline", BooleanConverter.getInstance(), Boolean.FALSE) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.underline == null || !var1.attributes.underline.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Text var1) {
            return (StyleableProperty<Boolean>)var1.underlineProperty();
         }
      };
      private static final CssMetaData<Text, Boolean> STRIKETHROUGH = new CssMetaData<Text, Boolean>(
         "-fx-strikethrough", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.strikethrough == null || !var1.attributes.strikethrough.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Text var1) {
            return (StyleableProperty<Boolean>)var1.strikethroughProperty();
         }
      };
      private static final CssMetaData<Text, TextAlignment> TEXT_ALIGNMENT = new CssMetaData<Text, TextAlignment>(
         "-fx-text-alignment", new EnumConverter<>(TextAlignment.class), TextAlignment.LEFT
      ) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.textAlignment == null || !var1.attributes.textAlignment.isBound();
         }

         public StyleableProperty<TextAlignment> getStyleableProperty(Text var1) {
            return (StyleableProperty<TextAlignment>)var1.textAlignmentProperty();
         }
      };
      private static final CssMetaData<Text, VPos> TEXT_ORIGIN = new CssMetaData<Text, VPos>("-fx-text-origin", new EnumConverter<>(VPos.class), VPos.BASELINE) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.textOrigin == null || !var1.attributes.textOrigin.isBound();
         }

         public StyleableProperty<VPos> getStyleableProperty(Text var1) {
            return (StyleableProperty<VPos>)var1.textOriginProperty();
         }
      };
      private static final CssMetaData<Text, FontSmoothingType> FONT_SMOOTHING_TYPE = new CssMetaData<Text, FontSmoothingType>(
         "-fx-font-smoothing-type", new EnumConverter<>(FontSmoothingType.class), FontSmoothingType.GRAY
      ) {
         public boolean isSettable(Text var1) {
            return var1.fontSmoothingType == null || !var1.fontSmoothingType.isBound();
         }

         public StyleableProperty<FontSmoothingType> getStyleableProperty(Text var1) {
            return (StyleableProperty<FontSmoothingType>)var1.fontSmoothingTypeProperty();
         }
      };
      private static final CssMetaData<Text, Number> LINE_SPACING = new CssMetaData<Text, Number>("-fx-line-spacing", SizeConverter.getInstance(), 0) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.lineSpacing == null || !var1.attributes.lineSpacing.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Text var1) {
            return (StyleableProperty<Number>)var1.lineSpacingProperty();
         }
      };
      private static final CssMetaData<Text, TextBoundsType> BOUNDS_TYPE = new CssMetaData<Text, TextBoundsType>(
         "-fx-bounds-type", new EnumConverter<>(TextBoundsType.class), Text.DEFAULT_BOUNDS_TYPE
      ) {
         public boolean isSettable(Text var1) {
            return var1.boundsType == null || !var1.boundsType.isBound();
         }

         public StyleableProperty<TextBoundsType> getStyleableProperty(Text var1) {
            return (StyleableProperty<TextBoundsType>)var1.boundsTypeProperty();
         }
      };
      private static final CssMetaData<Text, Number> TAB_SIZE = new CssMetaData<Text, Number>("-fx-tab-size", SizeConverter.getInstance(), 8) {
         public boolean isSettable(Text var1) {
            return var1.attributes == null || var1.attributes.tabSize == null || !var1.attributes.tabSize.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Text var1) {
            return (StyleableProperty<Number>)var1.tabSizeProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Shape.getClassCssMetaData());
         var0.add(FONT);
         var0.add(UNDERLINE);
         var0.add(STRIKETHROUGH);
         var0.add(TEXT_ALIGNMENT);
         var0.add(TEXT_ORIGIN);
         var0.add(FONT_SMOOTHING_TYPE);
         var0.add(LINE_SPACING);
         var0.add(BOUNDS_TYPE);
         var0.add(TAB_SIZE);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   private final class TextAttribute {
      private ObjectProperty<VPos> textOrigin;
      private BooleanProperty underline;
      private BooleanProperty strikethrough;
      private ObjectProperty<TextAlignment> textAlignment;
      private DoubleProperty lineSpacing;
      private ReadOnlyDoubleWrapper baselineOffset;
      private ObjectProperty<PathElement[]> selectionShape;
      private ObjectBinding<PathElement[]> selectionBinding;
      private ObjectProperty<Paint> selectionFill;
      private IntegerProperty selectionStart;
      private IntegerProperty selectionEnd;
      private ObjectProperty<PathElement[]> caretShape;
      private ObjectBinding<PathElement[]> caretBinding;
      private IntegerProperty caretPosition;
      private BooleanProperty caretBias;
      private IntegerProperty tabSize;

      final VPos getTextOrigin() {
         return this.textOrigin == null ? Text.DEFAULT_TEXT_ORIGIN : (VPos)this.textOrigin.get();
      }

      public final ObjectProperty<VPos> textOriginProperty() {
         if (this.textOrigin == null) {
            this.textOrigin = new StyleableObjectProperty<VPos>(Text.DEFAULT_TEXT_ORIGIN) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "textOrigin";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.TEXT_ORIGIN;
               }

               public void invalidated() {
                  NodeHelper.geomChanged(Text.this);
               }
            };
         }

         return this.textOrigin;
      }

      final boolean isUnderline() {
         return this.underline == null ? false : this.underline.get();
      }

      final BooleanProperty underlineProperty() {
         if (this.underline == null) {
            this.underline = new StyleableBooleanProperty() {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "underline";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.UNDERLINE;
               }

               public void invalidated() {
                  NodeHelper.markDirty(Text.this, DirtyBits.TEXT_ATTRS);
                  if (Text.this.getBoundsType() == TextBoundsType.VISUAL) {
                     NodeHelper.geomChanged(Text.this);
                  }
               }
            };
         }

         return this.underline;
      }

      final boolean isStrikethrough() {
         return this.strikethrough == null ? false : this.strikethrough.get();
      }

      final BooleanProperty strikethroughProperty() {
         if (this.strikethrough == null) {
            this.strikethrough = new StyleableBooleanProperty() {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "strikethrough";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.STRIKETHROUGH;
               }

               public void invalidated() {
                  NodeHelper.markDirty(Text.this, DirtyBits.TEXT_ATTRS);
                  if (Text.this.getBoundsType() == TextBoundsType.VISUAL) {
                     NodeHelper.geomChanged(Text.this);
                  }
               }
            };
         }

         return this.strikethrough;
      }

      final TextAlignment getTextAlignment() {
         return this.textAlignment == null ? Text.DEFAULT_TEXT_ALIGNMENT : (TextAlignment)this.textAlignment.get();
      }

      final ObjectProperty<TextAlignment> textAlignmentProperty() {
         if (this.textAlignment == null) {
            this.textAlignment = new StyleableObjectProperty<TextAlignment>(Text.DEFAULT_TEXT_ALIGNMENT) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "textAlignment";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.TEXT_ALIGNMENT;
               }

               public void invalidated() {
                  if (!Text.this.isSpan()) {
                     TextAlignment var1 = (TextAlignment)this.get();
                     if (var1 == null) {
                        var1 = Text.DEFAULT_TEXT_ALIGNMENT;
                     }

                     TextLayout var2 = Text.this.getTextLayout();
                     if (var2.setAlignment(var1.ordinal())) {
                        Text.this.needsTextLayout();
                     }
                  }
               }
            };
         }

         return this.textAlignment;
      }

      final double getLineSpacing() {
         return this.lineSpacing == null ? 0.0 : this.lineSpacing.get();
      }

      final DoubleProperty lineSpacingProperty() {
         if (this.lineSpacing == null) {
            this.lineSpacing = new StyleableDoubleProperty(0.0) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "lineSpacing";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.LINE_SPACING;
               }

               public void invalidated() {
                  if (!Text.this.isSpan()) {
                     TextLayout var1 = Text.this.getTextLayout();
                     if (var1.setLineSpacing((float)this.get())) {
                        Text.this.needsTextLayout();
                     }
                  }
               }
            };
         }

         return this.lineSpacing;
      }

      final ReadOnlyDoubleProperty baselineOffsetProperty() {
         if (this.baselineOffset == null) {
            this.baselineOffset = new ReadOnlyDoubleWrapper(Text.this, "baselineOffset") {
               {
                  this.bind(new DoubleBinding() {
                     {
                        this.bind(new Observable[]{Text.this.fontProperty()});
                     }

                     protected double computeValue() {
                        BaseBounds var1 = Text.this.getLogicalBounds();
                        return (double)(-var1.getMinY());
                     }
                  });
               }
            };
         }

         return this.baselineOffset.getReadOnlyProperty();
      }

      final ReadOnlyObjectProperty<PathElement[]> selectionShapeProperty() {
         if (this.selectionShape == null) {
            this.selectionBinding = new ObjectBinding<PathElement[]>() {
               {
                  this.bind(new Observable[]{TextAttribute.this.selectionStartProperty(), TextAttribute.this.selectionEndProperty()});
               }

               protected PathElement[] computeValue() {
                  int var1 = TextAttribute.this.getSelectionStart();
                  int var2 = TextAttribute.this.getSelectionEnd();
                  return Text.this.getRange(var1, var2, 1);
               }
            };
            this.selectionShape = new SimpleObjectProperty(Text.this, "selectionShape");
            this.selectionShape.bind(this.selectionBinding);
         }

         return this.selectionShape;
      }

      final ObjectProperty<Paint> selectionFillProperty() {
         if (this.selectionFill == null) {
            this.selectionFill = new ObjectPropertyBase<Paint>(Text.DEFAULT_SELECTION_FILL) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "selectionFill";
               }

               protected void invalidated() {
                  NodeHelper.markDirty(Text.this, DirtyBits.TEXT_SELECTION);
               }
            };
         }

         return this.selectionFill;
      }

      final int getSelectionStart() {
         return this.selectionStart == null ? -1 : this.selectionStart.get();
      }

      final IntegerProperty selectionStartProperty() {
         if (this.selectionStart == null) {
            this.selectionStart = new IntegerPropertyBase(-1) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "selectionStart";
               }

               protected void invalidated() {
                  NodeHelper.markDirty(Text.this, DirtyBits.TEXT_SELECTION);
                  Text.this.notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTION_START);
               }
            };
         }

         return this.selectionStart;
      }

      final int getSelectionEnd() {
         return this.selectionEnd == null ? -1 : this.selectionEnd.get();
      }

      final IntegerProperty selectionEndProperty() {
         if (this.selectionEnd == null) {
            this.selectionEnd = new IntegerPropertyBase(-1) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "selectionEnd";
               }

               protected void invalidated() {
                  NodeHelper.markDirty(Text.this, DirtyBits.TEXT_SELECTION);
                  Text.this.notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTION_END);
               }
            };
         }

         return this.selectionEnd;
      }

      final ReadOnlyObjectProperty<PathElement[]> caretShapeProperty() {
         if (this.caretShape == null) {
            this.caretBinding = new ObjectBinding<PathElement[]>() {
               {
                  this.bind(new Observable[]{TextAttribute.this.caretPositionProperty(), TextAttribute.this.caretBiasProperty()});
               }

               protected PathElement[] computeValue() {
                  int var1 = TextAttribute.this.getCaretPosition();
                  int var2 = Text.this.getTextInternal().length();
                  if (0 <= var1 && var1 <= var2) {
                     boolean var3 = TextAttribute.this.isCaretBias();
                     float var4 = (float)Text.this.getX();
                     float var5 = (float)Text.this.getY() - Text.this.getYRendering();
                     TextLayout var6 = Text.this.getTextLayout();
                     return var6.getCaretShape(var1, var3, var4, var5);
                  } else {
                     return Text.EMPTY_PATH_ELEMENT_ARRAY;
                  }
               }
            };
            this.caretShape = new SimpleObjectProperty(Text.this, "caretShape");
            this.caretShape.bind(this.caretBinding);
         }

         return this.caretShape;
      }

      final int getCaretPosition() {
         return this.caretPosition == null ? -1 : this.caretPosition.get();
      }

      final IntegerProperty caretPositionProperty() {
         if (this.caretPosition == null) {
            this.caretPosition = new IntegerPropertyBase(-1) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "caretPosition";
               }

               protected void invalidated() {
                  Text.this.notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTION_END);
               }
            };
         }

         return this.caretPosition;
      }

      final boolean isCaretBias() {
         return this.caretBias == null ? true : this.caretBias.get();
      }

      final BooleanProperty caretBiasProperty() {
         if (this.caretBias == null) {
            this.caretBias = new SimpleBooleanProperty(Text.this, "caretBias", true);
         }

         return this.caretBias;
      }

      final int getTabSize() {
         return this.tabSize == null ? 8 : this.tabSize.get();
      }

      final IntegerProperty tabSizeProperty() {
         if (this.tabSize == null) {
            this.tabSize = new StyleableIntegerProperty(8) {
               public Object getBean() {
                  return Text.this;
               }

               public String getName() {
                  return "tabSize";
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Text.StyleableProperties.TAB_SIZE;
               }

               protected void invalidated() {
                  if (!Text.this.isSpan()) {
                     TextLayout var1 = Text.this.getTextLayout();
                     if (var1.setTabSize(this.get())) {
                        Text.this.needsTextLayout();
                     }

                     NodeHelper.markDirty(Text.this, DirtyBits.TEXT_ATTRS);
                     if (Text.this.getBoundsType() == TextBoundsType.VISUAL) {
                        NodeHelper.geomChanged(Text.this);
                     }
                  }
               }
            };
         }

         return this.tabSize;
      }
   }
}
