package javafx.scene.control;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

@DefaultProperty("text")
public abstract class Labeled extends Control {
   private static final String DEFAULT_ELLIPSIS_STRING = "...";
   private StringProperty text;
   private ObjectProperty<Pos> alignment;
   private ObjectProperty<TextAlignment> textAlignment;
   private ObjectProperty<OverrunStyle> textOverrun;
   private StringProperty ellipsisString;
   private BooleanProperty wrapText;
   private ObjectProperty<Font> font;
   private ObjectProperty<Node> graphic;
   private StyleableStringProperty imageUrl = null;
   private BooleanProperty underline;
   private DoubleProperty lineSpacing;
   private ObjectProperty<ContentDisplay> contentDisplay;
   private ObjectProperty<Insets> labelPadding;
   private DoubleProperty graphicTextGap;
   private ObjectProperty<Paint> textFill;
   private BooleanProperty mnemonicParsing;

   public Labeled() {
   }

   public Labeled(String var1) {
      this.setText(var1);
   }

   public Labeled(String var1, Node var2) {
      this.setText(var1);
      ((StyleableProperty)this.graphicProperty()).applyStyle(null, var2);
   }

   public final StringProperty textProperty() {
      if (this.text == null) {
         this.text = new SimpleStringProperty(this, "text", "");
      }

      return this.text;
   }

   public final void setText(String var1) {
      this.textProperty().setValue(var1);
   }

   public final String getText() {
      return this.text == null ? "" : this.text.getValue();
   }

   public final ObjectProperty<Pos> alignmentProperty() {
      if (this.alignment == null) {
         this.alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT) {
            public CssMetaData<Labeled, Pos> getCssMetaData() {
               return Labeled.StyleableProperties.ALIGNMENT;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "alignment";
            }
         };
      }

      return this.alignment;
   }

   public final void setAlignment(Pos var1) {
      this.alignmentProperty().set(var1);
   }

   public final Pos getAlignment() {
      return this.alignment == null ? Pos.CENTER_LEFT : (Pos)this.alignment.get();
   }

   public final ObjectProperty<TextAlignment> textAlignmentProperty() {
      if (this.textAlignment == null) {
         this.textAlignment = new StyleableObjectProperty<TextAlignment>(TextAlignment.LEFT) {
            public CssMetaData<Labeled, TextAlignment> getCssMetaData() {
               return Labeled.StyleableProperties.TEXT_ALIGNMENT;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "textAlignment";
            }
         };
      }

      return this.textAlignment;
   }

   public final void setTextAlignment(TextAlignment var1) {
      this.textAlignmentProperty().setValue(var1);
   }

   public final TextAlignment getTextAlignment() {
      return this.textAlignment == null ? TextAlignment.LEFT : (TextAlignment)this.textAlignment.getValue();
   }

   public final ObjectProperty<OverrunStyle> textOverrunProperty() {
      if (this.textOverrun == null) {
         this.textOverrun = new StyleableObjectProperty<OverrunStyle>(OverrunStyle.ELLIPSIS) {
            public CssMetaData<Labeled, OverrunStyle> getCssMetaData() {
               return Labeled.StyleableProperties.TEXT_OVERRUN;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "textOverrun";
            }
         };
      }

      return this.textOverrun;
   }

   public final void setTextOverrun(OverrunStyle var1) {
      this.textOverrunProperty().setValue(var1);
   }

   public final OverrunStyle getTextOverrun() {
      return this.textOverrun == null ? OverrunStyle.ELLIPSIS : (OverrunStyle)this.textOverrun.getValue();
   }

   public final StringProperty ellipsisStringProperty() {
      if (this.ellipsisString == null) {
         this.ellipsisString = new StyleableStringProperty("...") {
            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "ellipsisString";
            }

            public CssMetaData<Labeled, String> getCssMetaData() {
               return Labeled.StyleableProperties.ELLIPSIS_STRING;
            }
         };
      }

      return this.ellipsisString;
   }

   public final void setEllipsisString(String var1) {
      this.ellipsisStringProperty().set(var1 == null ? "" : var1);
   }

   public final String getEllipsisString() {
      return this.ellipsisString == null ? "..." : (String)this.ellipsisString.get();
   }

   public final BooleanProperty wrapTextProperty() {
      if (this.wrapText == null) {
         this.wrapText = new StyleableBooleanProperty() {
            public CssMetaData<Labeled, Boolean> getCssMetaData() {
               return Labeled.StyleableProperties.WRAP_TEXT;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "wrapText";
            }
         };
      }

      return this.wrapText;
   }

   public final void setWrapText(boolean var1) {
      this.wrapTextProperty().setValue(var1);
   }

   public final boolean isWrapText() {
      return this.wrapText == null ? false : this.wrapText.getValue();
   }

   public Orientation getContentBias() {
      return this.isWrapText() ? Orientation.HORIZONTAL : null;
   }

   public final ObjectProperty<Font> fontProperty() {
      if (this.font == null) {
         this.font = new StyleableObjectProperty<Font>(Font.getDefault()) {
            private boolean fontSetByCss = false;

            public void applyStyle(StyleOrigin var1, Font var2) {
               try {
                  this.fontSetByCss = true;
                  super.applyStyle(var1, var2);
               } catch (Exception var7) {
                  throw var7;
               } finally {
                  this.fontSetByCss = false;
               }
            }

            public void set(Font var1) {
               Font var2 = (Font)this.get();
               if (var1 != null ? !var1.equals(var2) : var2 != null) {
                  super.set(var1);
                  NodeHelper.recalculateRelativeSizeProperties(Labeled.this, var1);
               }
            }

            protected void invalidated() {
               if (!this.fontSetByCss) {
                  NodeHelper.reapplyCSS(Labeled.this);
               }
            }

            public CssMetaData<Labeled, Font> getCssMetaData() {
               return Labeled.StyleableProperties.FONT;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "font";
            }
         };
      }

      return this.font;
   }

   public final void setFont(Font var1) {
      this.fontProperty().setValue(var1);
   }

   public final Font getFont() {
      return this.font == null ? Font.getDefault() : (Font)this.font.getValue();
   }

   public final ObjectProperty<Node> graphicProperty() {
      if (this.graphic == null) {
         this.graphic = new StyleableObjectProperty<Node>() {
            public CssMetaData getCssMetaData() {
               return Labeled.StyleableProperties.GRAPHIC;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "graphic";
            }
         };
      }

      return this.graphic;
   }

   public final void setGraphic(Node var1) {
      this.graphicProperty().setValue(var1);
   }

   public final Node getGraphic() {
      return this.graphic == null ? null : (Node)this.graphic.getValue();
   }

   private StyleableStringProperty imageUrlProperty() {
      if (this.imageUrl == null) {
         this.imageUrl = new StyleableStringProperty() {
            StyleOrigin origin = StyleOrigin.USER;

            public void applyStyle(StyleOrigin var1, String var2) {
               this.origin = var1;
               if (Labeled.this.graphic == null || !Labeled.this.graphic.isBound()) {
                  super.applyStyle(var1, var2);
               }

               this.origin = StyleOrigin.USER;
            }

            protected void invalidated() {
               String var1 = super.get();
               if (var1 == null) {
                  ((StyleableProperty)Labeled.this.graphicProperty()).applyStyle(this.origin, null);
               } else {
                  Node var2 = Labeled.this.getGraphic();
                  if (var2 instanceof ImageView) {
                     ImageView var3 = (ImageView)var2;
                     Image var4 = var3.getImage();
                     if (var4 != null) {
                        String var5 = var4.getUrl();
                        if (var1.equals(var5)) {
                           return;
                        }
                     }
                  }

                  Image var6 = StyleManager.getInstance().getCachedImage(var1);
                  if (var6 != null) {
                     ((StyleableProperty)Labeled.this.graphicProperty()).applyStyle(this.origin, new ImageView(var6));
                  }
               }
            }

            public String get() {
               Node var1 = Labeled.this.getGraphic();
               if (var1 instanceof ImageView) {
                  Image var2 = ((ImageView)var1).getImage();
                  if (var2 != null) {
                     return var2.getUrl();
                  }
               }

               return null;
            }

            public StyleOrigin getStyleOrigin() {
               return Labeled.this.graphic != null ? ((StyleableProperty)Labeled.this.graphic).getStyleOrigin() : null;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "imageUrl";
            }

            public CssMetaData<Labeled, String> getCssMetaData() {
               return Labeled.StyleableProperties.GRAPHIC;
            }
         };
      }

      return this.imageUrl;
   }

   public final BooleanProperty underlineProperty() {
      if (this.underline == null) {
         this.underline = new StyleableBooleanProperty(false) {
            public CssMetaData<Labeled, Boolean> getCssMetaData() {
               return Labeled.StyleableProperties.UNDERLINE;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "underline";
            }
         };
      }

      return this.underline;
   }

   public final void setUnderline(boolean var1) {
      this.underlineProperty().setValue(var1);
   }

   public final boolean isUnderline() {
      return this.underline == null ? false : this.underline.getValue();
   }

   public final DoubleProperty lineSpacingProperty() {
      if (this.lineSpacing == null) {
         this.lineSpacing = new StyleableDoubleProperty(0.0) {
            public CssMetaData<Labeled, Number> getCssMetaData() {
               return Labeled.StyleableProperties.LINE_SPACING;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "lineSpacing";
            }
         };
      }

      return this.lineSpacing;
   }

   public final void setLineSpacing(double var1) {
      this.lineSpacingProperty().setValue(var1);
   }

   public final double getLineSpacing() {
      return this.lineSpacing == null ? 0.0 : this.lineSpacing.getValue();
   }

   public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
      if (this.contentDisplay == null) {
         this.contentDisplay = new StyleableObjectProperty<ContentDisplay>(ContentDisplay.LEFT) {
            public CssMetaData<Labeled, ContentDisplay> getCssMetaData() {
               return Labeled.StyleableProperties.CONTENT_DISPLAY;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "contentDisplay";
            }
         };
      }

      return this.contentDisplay;
   }

   public final void setContentDisplay(ContentDisplay var1) {
      this.contentDisplayProperty().setValue(var1);
   }

   public final ContentDisplay getContentDisplay() {
      return this.contentDisplay == null ? ContentDisplay.LEFT : (ContentDisplay)this.contentDisplay.getValue();
   }

   public final ReadOnlyObjectProperty<Insets> labelPaddingProperty() {
      return this.labelPaddingPropertyImpl();
   }

   private ObjectProperty<Insets> labelPaddingPropertyImpl() {
      if (this.labelPadding == null) {
         this.labelPadding = new StyleableObjectProperty<Insets>(Insets.EMPTY) {
            private Insets lastValidValue = Insets.EMPTY;

            public void invalidated() {
               Insets var1 = (Insets)this.get();
               if (var1 == null) {
                  this.set(this.lastValidValue);
                  throw new NullPointerException("cannot set labelPadding to null");
               } else {
                  this.lastValidValue = var1;
                  Labeled.this.requestLayout();
               }
            }

            public CssMetaData<Labeled, Insets> getCssMetaData() {
               return Labeled.StyleableProperties.LABEL_PADDING;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "labelPadding";
            }
         };
      }

      return this.labelPadding;
   }

   private void setLabelPadding(Insets var1) {
      this.labelPaddingPropertyImpl().set(var1);
   }

   public final Insets getLabelPadding() {
      return this.labelPadding == null ? Insets.EMPTY : (Insets)this.labelPadding.get();
   }

   public final DoubleProperty graphicTextGapProperty() {
      if (this.graphicTextGap == null) {
         this.graphicTextGap = new StyleableDoubleProperty(4.0) {
            public CssMetaData<Labeled, Number> getCssMetaData() {
               return Labeled.StyleableProperties.GRAPHIC_TEXT_GAP;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "graphicTextGap";
            }
         };
      }

      return this.graphicTextGap;
   }

   public final void setGraphicTextGap(double var1) {
      this.graphicTextGapProperty().setValue(var1);
   }

   public final double getGraphicTextGap() {
      return this.graphicTextGap == null ? 4.0 : this.graphicTextGap.getValue();
   }

   public final void setTextFill(Paint var1) {
      this.textFillProperty().set(var1);
   }

   public final Paint getTextFill() {
      return (Paint)(this.textFill == null ? Color.BLACK : (Paint)this.textFill.get());
   }

   public final ObjectProperty<Paint> textFillProperty() {
      if (this.textFill == null) {
         this.textFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
            public CssMetaData<Labeled, Paint> getCssMetaData() {
               return Labeled.StyleableProperties.TEXT_FILL;
            }

            public Object getBean() {
               return Labeled.this;
            }

            public String getName() {
               return "textFill";
            }
         };
      }

      return this.textFill;
   }

   public final void setMnemonicParsing(boolean var1) {
      this.mnemonicParsingProperty().set(var1);
   }

   public final boolean isMnemonicParsing() {
      return this.mnemonicParsing == null ? false : this.mnemonicParsing.get();
   }

   public final BooleanProperty mnemonicParsingProperty() {
      if (this.mnemonicParsing == null) {
         this.mnemonicParsing = new SimpleBooleanProperty(this, "mnemonicParsing");
      }

      return this.mnemonicParsing;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString()).append("'").append(this.getText()).append("'");
      return var1.toString();
   }

   protected Pos getInitialAlignment() {
      return Pos.CENTER_LEFT;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Labeled.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final FontCssMetaData<Labeled> FONT = new FontCssMetaData<Labeled>("-fx-font", Font.getDefault()) {
         public boolean isSettable(Labeled var1) {
            return var1.font == null || !var1.font.isBound();
         }

         public StyleableProperty<Font> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Font>)var1.fontProperty();
         }
      };
      private static final CssMetaData<Labeled, Pos> ALIGNMENT = new CssMetaData<Labeled, Pos>("-fx-alignment", new EnumConverter(Pos.class), Pos.CENTER_LEFT) {
         public boolean isSettable(Labeled var1) {
            return var1.alignment == null || !var1.alignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Pos>)var1.alignmentProperty();
         }

         public Pos getInitialValue(Labeled var1) {
            return var1.getInitialAlignment();
         }
      };
      private static final CssMetaData<Labeled, TextAlignment> TEXT_ALIGNMENT = new CssMetaData<Labeled, TextAlignment>(
         "-fx-text-alignment", new EnumConverter(TextAlignment.class), TextAlignment.LEFT
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.textAlignment == null || !var1.textAlignment.isBound();
         }

         public StyleableProperty<TextAlignment> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<TextAlignment>)var1.textAlignmentProperty();
         }
      };
      private static final CssMetaData<Labeled, Paint> TEXT_FILL = new CssMetaData<Labeled, Paint>("-fx-text-fill", PaintConverter.getInstance(), Color.BLACK) {
         public boolean isSettable(Labeled var1) {
            return var1.textFill == null || !var1.textFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Paint>)var1.textFillProperty();
         }
      };
      private static final CssMetaData<Labeled, OverrunStyle> TEXT_OVERRUN = new CssMetaData<Labeled, OverrunStyle>(
         "-fx-text-overrun", new EnumConverter(OverrunStyle.class), OverrunStyle.ELLIPSIS
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.textOverrun == null || !var1.textOverrun.isBound();
         }

         public StyleableProperty<OverrunStyle> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<OverrunStyle>)var1.textOverrunProperty();
         }
      };
      private static final CssMetaData<Labeled, String> ELLIPSIS_STRING = new CssMetaData<Labeled, String>(
         "-fx-ellipsis-string", StringConverter.getInstance(), "..."
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.ellipsisString == null || !var1.ellipsisString.isBound();
         }

         public StyleableProperty<String> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<String>)var1.ellipsisStringProperty();
         }
      };
      private static final CssMetaData<Labeled, Boolean> WRAP_TEXT = new CssMetaData<Labeled, Boolean>("-fx-wrap-text", BooleanConverter.getInstance(), false) {
         public boolean isSettable(Labeled var1) {
            return var1.wrapText == null || !var1.wrapText.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Boolean>)var1.wrapTextProperty();
         }
      };
      private static final CssMetaData<Labeled, String> GRAPHIC = new CssMetaData<Labeled, String>("-fx-graphic", StringConverter.getInstance()) {
         public boolean isSettable(Labeled var1) {
            return var1.graphic == null || !var1.graphic.isBound();
         }

         public StyleableProperty<String> getStyleableProperty(Labeled var1) {
            return var1.imageUrlProperty();
         }
      };
      private static final CssMetaData<Labeled, Boolean> UNDERLINE = new CssMetaData<Labeled, Boolean>(
         "-fx-underline", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.underline == null || !var1.underline.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Boolean>)var1.underlineProperty();
         }
      };
      private static final CssMetaData<Labeled, Number> LINE_SPACING = new CssMetaData<Labeled, Number>("-fx-line-spacing", SizeConverter.getInstance(), 0) {
         public boolean isSettable(Labeled var1) {
            return var1.lineSpacing == null || !var1.lineSpacing.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Number>)var1.lineSpacingProperty();
         }
      };
      private static final CssMetaData<Labeled, ContentDisplay> CONTENT_DISPLAY = new CssMetaData<Labeled, ContentDisplay>(
         "-fx-content-display", new EnumConverter(ContentDisplay.class), ContentDisplay.LEFT
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.contentDisplay == null || !var1.contentDisplay.isBound();
         }

         public StyleableProperty<ContentDisplay> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<ContentDisplay>)var1.contentDisplayProperty();
         }
      };
      private static final CssMetaData<Labeled, Insets> LABEL_PADDING = new CssMetaData<Labeled, Insets>(
         "-fx-label-padding", InsetsConverter.getInstance(), Insets.EMPTY
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.labelPadding == null || !var1.labelPadding.isBound();
         }

         public StyleableProperty<Insets> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Insets>)var1.labelPaddingPropertyImpl();
         }
      };
      private static final CssMetaData<Labeled, Number> GRAPHIC_TEXT_GAP = new CssMetaData<Labeled, Number>(
         "-fx-graphic-text-gap", SizeConverter.getInstance(), 4.0
      ) {
         public boolean isSettable(Labeled var1) {
            return var1.graphicTextGap == null || !var1.graphicTextGap.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Labeled var1) {
            return (StyleableProperty<Number>)var1.graphicTextGapProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         Collections.addAll(
            var0,
            FONT,
            ALIGNMENT,
            TEXT_ALIGNMENT,
            TEXT_FILL,
            TEXT_OVERRUN,
            ELLIPSIS_STRING,
            WRAP_TEXT,
            GRAPHIC,
            UNDERLINE,
            LINE_SPACING,
            CONTENT_DISPLAY,
            LABEL_PADDING,
            GRAPHIC_TEXT_GAP
         );
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
