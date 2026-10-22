package javafx.scene.control.skin;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.ColorPickerBehavior;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

public class ColorPickerSkin extends ComboBoxPopupControl<Color> {
   private Label displayNode;
   private StackPane pickerColorBox;
   private Rectangle colorRect;
   private ColorPalette popupContent;
   private final ColorPickerBehavior behavior;
   BooleanProperty colorLabelVisible = new StyleableBooleanProperty(true) {
      public void invalidated() {
         if (ColorPickerSkin.this.displayNode != null) {
            if (ColorPickerSkin.this.colorLabelVisible.get()) {
               ColorPickerSkin.this.displayNode.setText(ColorPickerSkin.colorDisplayName(((ColorPicker)ColorPickerSkin.this.getSkinnable()).getValue()));
            } else {
               ColorPickerSkin.this.displayNode.setText("");
            }
         }
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "colorLabelVisible";
      }

      public CssMetaData<ColorPicker, Boolean> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.COLOR_LABEL_VISIBLE;
      }
   };
   private final StyleableStringProperty imageUrl = new StyleableStringProperty() {
      public void applyStyle(StyleOrigin var1, String var2) {
         super.applyStyle(var1, var2);
         if (var2 == null) {
            if (ColorPickerSkin.this.pickerColorBox.getChildren().size() == 2) {
               ColorPickerSkin.this.pickerColorBox.getChildren().remove(1);
            }
         } else if (ColorPickerSkin.this.pickerColorBox.getChildren().size() == 2) {
            ImageView var3 = (ImageView)ColorPickerSkin.this.pickerColorBox.getChildren().get(1);
            var3.setImage(StyleManager.getInstance().getCachedImage(var2));
         } else {
            ColorPickerSkin.this.pickerColorBox.getChildren().add(new ImageView(StyleManager.getInstance().getCachedImage(var2)));
         }
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "imageUrl";
      }

      public CssMetaData<ColorPicker, String> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.GRAPHIC;
      }
   };
   private final StyleableDoubleProperty colorRectWidth = new StyleableDoubleProperty(12.0) {
      protected void invalidated() {
         if (ColorPickerSkin.this.pickerColorBox != null) {
            ColorPickerSkin.this.pickerColorBox.requestLayout();
         }
      }

      public CssMetaData<ColorPicker, Number> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.COLOR_RECT_WIDTH;
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "colorRectWidth";
      }
   };
   private final StyleableDoubleProperty colorRectHeight = new StyleableDoubleProperty(12.0) {
      protected void invalidated() {
         if (ColorPickerSkin.this.pickerColorBox != null) {
            ColorPickerSkin.this.pickerColorBox.requestLayout();
         }
      }

      public CssMetaData<ColorPicker, Number> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.COLOR_RECT_HEIGHT;
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "colorRectHeight";
      }
   };
   private final StyleableDoubleProperty colorRectX = new StyleableDoubleProperty(0.0) {
      protected void invalidated() {
         if (ColorPickerSkin.this.pickerColorBox != null) {
            ColorPickerSkin.this.pickerColorBox.requestLayout();
         }
      }

      public CssMetaData<ColorPicker, Number> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.COLOR_RECT_X;
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "colorRectX";
      }
   };
   private final StyleableDoubleProperty colorRectY = new StyleableDoubleProperty(0.0) {
      protected void invalidated() {
         if (ColorPickerSkin.this.pickerColorBox != null) {
            ColorPickerSkin.this.pickerColorBox.requestLayout();
         }
      }

      public CssMetaData<ColorPicker, Number> getCssMetaData() {
         return ColorPickerSkin.StyleableProperties.COLOR_RECT_Y;
      }

      public Object getBean() {
         return ColorPickerSkin.this;
      }

      public String getName() {
         return "colorRectY";
      }
   };
   private static final Map<Color, String> COLOR_NAME_MAP = Map.ofEntries(
      Map.entry(Color.TRANSPARENT, Properties.getColorPickerString("colorName.transparent")),
      Map.entry(Color.BLACK, Properties.getColorPickerString("colorName.black")),
      Map.entry(Color.BLUE, Properties.getColorPickerString("colorName.blue")),
      Map.entry(Color.CYAN, Properties.getColorPickerString("colorName.cyan")),
      Map.entry(Color.DARKBLUE, Properties.getColorPickerString("colorName.darkblue")),
      Map.entry(Color.DARKCYAN, Properties.getColorPickerString("colorName.darkcyan")),
      Map.entry(Color.DARKGRAY, Properties.getColorPickerString("colorName.darkgray")),
      Map.entry(Color.DARKGREEN, Properties.getColorPickerString("colorName.darkgreen")),
      Map.entry(Color.DARKMAGENTA, Properties.getColorPickerString("colorName.darkmagenta")),
      Map.entry(Color.DARKRED, Properties.getColorPickerString("colorName.darkred")),
      Map.entry(Color.GRAY, Properties.getColorPickerString("colorName.gray")),
      Map.entry(Color.GREEN, Properties.getColorPickerString("colorName.green")),
      Map.entry(Color.LIGHTBLUE, Properties.getColorPickerString("colorName.lightblue")),
      Map.entry(Color.LIGHTCYAN, Properties.getColorPickerString("colorName.lightcyan")),
      Map.entry(Color.LIGHTGRAY, Properties.getColorPickerString("colorName.lightgray")),
      Map.entry(Color.LIGHTGREEN, Properties.getColorPickerString("colorName.lightgreen")),
      Map.entry(Color.LIGHTYELLOW, Properties.getColorPickerString("colorName.lightyellow")),
      Map.entry(Color.LIME, Properties.getColorPickerString("colorName.lime")),
      Map.entry(Color.MAGENTA, Properties.getColorPickerString("colorName.magenta")),
      Map.entry(Color.MAROON, Properties.getColorPickerString("colorName.maroon")),
      Map.entry(Color.MEDIUMBLUE, Properties.getColorPickerString("colorName.mediumblue")),
      Map.entry(Color.NAVY, Properties.getColorPickerString("colorName.navy")),
      Map.entry(Color.OLIVE, Properties.getColorPickerString("colorName.olive")),
      Map.entry(Color.ORANGE, Properties.getColorPickerString("colorName.orange")),
      Map.entry(Color.PINK, Properties.getColorPickerString("colorName.pink")),
      Map.entry(Color.PURPLE, Properties.getColorPickerString("colorName.purple")),
      Map.entry(Color.RED, Properties.getColorPickerString("colorName.red")),
      Map.entry(Color.TEAL, Properties.getColorPickerString("colorName.teal")),
      Map.entry(Color.WHITE, Properties.getColorPickerString("colorName.white")),
      Map.entry(Color.YELLOW, Properties.getColorPickerString("colorName.yellow"))
   );
   private static final Map<Color, String> CSS_NAME_MAP = Map.ofEntries(
      Map.entry(Color.ALICEBLUE, "aliceblue"),
      Map.entry(Color.ANTIQUEWHITE, "antiquewhite"),
      Map.entry(Color.AQUAMARINE, "aquamarine"),
      Map.entry(Color.AZURE, "azure"),
      Map.entry(Color.BEIGE, "beige"),
      Map.entry(Color.BISQUE, "bisque"),
      Map.entry(Color.BLACK, "black"),
      Map.entry(Color.BLANCHEDALMOND, "blanchedalmond"),
      Map.entry(Color.BLUE, "blue"),
      Map.entry(Color.BLUEVIOLET, "blueviolet"),
      Map.entry(Color.BROWN, "brown"),
      Map.entry(Color.BURLYWOOD, "burlywood"),
      Map.entry(Color.CADETBLUE, "cadetblue"),
      Map.entry(Color.CHARTREUSE, "chartreuse"),
      Map.entry(Color.CHOCOLATE, "chocolate"),
      Map.entry(Color.CORAL, "coral"),
      Map.entry(Color.CORNFLOWERBLUE, "cornflowerblue"),
      Map.entry(Color.CORNSILK, "cornsilk"),
      Map.entry(Color.CRIMSON, "crimson"),
      Map.entry(Color.CYAN, "cyan"),
      Map.entry(Color.DARKBLUE, "darkblue"),
      Map.entry(Color.DARKCYAN, "darkcyan"),
      Map.entry(Color.DARKGOLDENROD, "darkgoldenrod"),
      Map.entry(Color.DARKGRAY, "darkgray"),
      Map.entry(Color.DARKGREEN, "darkgreen"),
      Map.entry(Color.DARKKHAKI, "darkkhaki"),
      Map.entry(Color.DARKMAGENTA, "darkmagenta"),
      Map.entry(Color.DARKOLIVEGREEN, "darkolivegreen"),
      Map.entry(Color.DARKORANGE, "darkorange"),
      Map.entry(Color.DARKORCHID, "darkorchid"),
      Map.entry(Color.DARKRED, "darkred"),
      Map.entry(Color.DARKSALMON, "darksalmon"),
      Map.entry(Color.DARKSEAGREEN, "darkseagreen"),
      Map.entry(Color.DARKSLATEBLUE, "darkslateblue"),
      Map.entry(Color.DARKSLATEGRAY, "darkslategray"),
      Map.entry(Color.DARKTURQUOISE, "darkturquoise"),
      Map.entry(Color.DARKVIOLET, "darkviolet"),
      Map.entry(Color.DEEPPINK, "deeppink"),
      Map.entry(Color.DEEPSKYBLUE, "deepskyblue"),
      Map.entry(Color.DIMGRAY, "dimgray"),
      Map.entry(Color.DODGERBLUE, "dodgerblue"),
      Map.entry(Color.FIREBRICK, "firebrick"),
      Map.entry(Color.FLORALWHITE, "floralwhite"),
      Map.entry(Color.FORESTGREEN, "forestgreen"),
      Map.entry(Color.GAINSBORO, "gainsboro"),
      Map.entry(Color.GHOSTWHITE, "ghostwhite"),
      Map.entry(Color.GOLD, "gold"),
      Map.entry(Color.GOLDENROD, "goldenrod"),
      Map.entry(Color.GRAY, "gray"),
      Map.entry(Color.GREEN, "green"),
      Map.entry(Color.GREENYELLOW, "greenyellow"),
      Map.entry(Color.HONEYDEW, "honeydew"),
      Map.entry(Color.HOTPINK, "hotpink"),
      Map.entry(Color.INDIANRED, "indianred"),
      Map.entry(Color.INDIGO, "indigo"),
      Map.entry(Color.IVORY, "ivory"),
      Map.entry(Color.KHAKI, "khaki"),
      Map.entry(Color.LAVENDER, "lavender"),
      Map.entry(Color.LAVENDERBLUSH, "lavenderblush"),
      Map.entry(Color.LAWNGREEN, "lawngreen"),
      Map.entry(Color.LEMONCHIFFON, "lemonchiffon"),
      Map.entry(Color.LIGHTBLUE, "lightblue"),
      Map.entry(Color.LIGHTCORAL, "lightcoral"),
      Map.entry(Color.LIGHTCYAN, "lightcyan"),
      Map.entry(Color.LIGHTGOLDENRODYELLOW, "lightgoldenrodyellow"),
      Map.entry(Color.LIGHTGRAY, "lightgray"),
      Map.entry(Color.LIGHTGREEN, "lightgreen"),
      Map.entry(Color.LIGHTPINK, "lightpink"),
      Map.entry(Color.LIGHTSALMON, "lightsalmon"),
      Map.entry(Color.LIGHTSEAGREEN, "lightseagreen"),
      Map.entry(Color.LIGHTSKYBLUE, "lightskyblue"),
      Map.entry(Color.LIGHTSLATEGRAY, "lightslategray"),
      Map.entry(Color.LIGHTSTEELBLUE, "lightsteelblue"),
      Map.entry(Color.LIGHTYELLOW, "lightyellow"),
      Map.entry(Color.LIME, "lime"),
      Map.entry(Color.LIMEGREEN, "limegreen"),
      Map.entry(Color.LINEN, "linen"),
      Map.entry(Color.MAGENTA, "magenta"),
      Map.entry(Color.MAROON, "maroon"),
      Map.entry(Color.MEDIUMAQUAMARINE, "mediumaquamarine"),
      Map.entry(Color.MEDIUMBLUE, "mediumblue"),
      Map.entry(Color.MEDIUMORCHID, "mediumorchid"),
      Map.entry(Color.MEDIUMPURPLE, "mediumpurple"),
      Map.entry(Color.MEDIUMSEAGREEN, "mediumseagreen"),
      Map.entry(Color.MEDIUMSLATEBLUE, "mediumslateblue"),
      Map.entry(Color.MEDIUMSPRINGGREEN, "mediumspringgreen"),
      Map.entry(Color.MEDIUMTURQUOISE, "mediumturquoise"),
      Map.entry(Color.MEDIUMVIOLETRED, "mediumvioletred"),
      Map.entry(Color.MIDNIGHTBLUE, "midnightblue"),
      Map.entry(Color.MINTCREAM, "mintcream"),
      Map.entry(Color.MISTYROSE, "mistyrose"),
      Map.entry(Color.MOCCASIN, "moccasin"),
      Map.entry(Color.NAVAJOWHITE, "navajowhite"),
      Map.entry(Color.NAVY, "navy"),
      Map.entry(Color.OLDLACE, "oldlace"),
      Map.entry(Color.OLIVE, "olive"),
      Map.entry(Color.OLIVEDRAB, "olivedrab"),
      Map.entry(Color.ORANGE, "orange"),
      Map.entry(Color.ORANGERED, "orangered"),
      Map.entry(Color.ORCHID, "orchid"),
      Map.entry(Color.PALEGOLDENROD, "palegoldenrod"),
      Map.entry(Color.PALEGREEN, "palegreen"),
      Map.entry(Color.PALETURQUOISE, "paleturquoise"),
      Map.entry(Color.PALEVIOLETRED, "palevioletred"),
      Map.entry(Color.PAPAYAWHIP, "papayawhip"),
      Map.entry(Color.PEACHPUFF, "peachpuff"),
      Map.entry(Color.PERU, "peru"),
      Map.entry(Color.PINK, "pink"),
      Map.entry(Color.PLUM, "plum"),
      Map.entry(Color.POWDERBLUE, "powderblue"),
      Map.entry(Color.PURPLE, "purple"),
      Map.entry(Color.RED, "red"),
      Map.entry(Color.ROSYBROWN, "rosybrown"),
      Map.entry(Color.ROYALBLUE, "royalblue"),
      Map.entry(Color.SADDLEBROWN, "saddlebrown"),
      Map.entry(Color.SALMON, "salmon"),
      Map.entry(Color.SANDYBROWN, "sandybrown"),
      Map.entry(Color.SEAGREEN, "seagreen"),
      Map.entry(Color.SEASHELL, "seashell"),
      Map.entry(Color.SIENNA, "sienna"),
      Map.entry(Color.SILVER, "silver"),
      Map.entry(Color.SKYBLUE, "skyblue"),
      Map.entry(Color.SLATEBLUE, "slateblue"),
      Map.entry(Color.SLATEGRAY, "slategray"),
      Map.entry(Color.SNOW, "snow"),
      Map.entry(Color.SPRINGGREEN, "springgreen"),
      Map.entry(Color.STEELBLUE, "steelblue"),
      Map.entry(Color.TAN, "tan"),
      Map.entry(Color.TEAL, "teal"),
      Map.entry(Color.THISTLE, "thistle"),
      Map.entry(Color.TOMATO, "tomato"),
      Map.entry(Color.TRANSPARENT, "transparent"),
      Map.entry(Color.TURQUOISE, "turquoise"),
      Map.entry(Color.VIOLET, "violet"),
      Map.entry(Color.WHEAT, "wheat"),
      Map.entry(Color.WHITE, "white"),
      Map.entry(Color.WHITESMOKE, "whitesmoke"),
      Map.entry(Color.YELLOW, "yellow"),
      Map.entry(Color.YELLOWGREEN, "yellowgreen")
   );

   public ColorPickerSkin(ColorPicker var1) {
      super(var1);
      this.behavior = new ColorPickerBehavior(var1);
      this.updateComboBoxMode();
      this.registerChangeListener(var1.valueProperty(), var1x -> this.updateColor());
      this.displayNode = new Label();
      this.displayNode.getStyleClass().add("color-picker-label");
      this.displayNode.setManaged(false);
      this.pickerColorBox = new ColorPickerSkin.PickerColorBox();
      this.pickerColorBox.getStyleClass().add("picker-color");
      this.colorRect = new Rectangle(12.0, 12.0);
      this.colorRect.getStyleClass().add("picker-color-rect");
      this.updateColor();
      this.pickerColorBox.getChildren().add(this.colorRect);
      this.displayNode.setGraphic(this.pickerColorBox);
      if (var1.isShowing()) {
         this.show();
      }
   }

   private final StringProperty imageUrlProperty() {
      return this.imageUrl;
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      if (!this.colorLabelVisible.get()) {
         return super.computePrefWidth(var1, var3, var5, var7, var9);
      } else {
         String var11 = this.displayNode.getText();
         double var12 = 0.0;

         for (String var15 : COLOR_NAME_MAP.values()) {
            this.displayNode.setText(var15);
            var12 = Math.max(var12, super.computePrefWidth(var1, var3, var5, var7, var9));
         }

         this.displayNode.setText(Utils.formatHexString(Color.BLACK));
         var12 = Math.max(var12, super.computePrefWidth(var1, var3, var5, var7, var9));
         this.displayNode.setText(var11);
         return var12;
      }
   }

   @Override
   protected Node getPopupContent() {
      if (this.popupContent == null) {
         this.popupContent = new ColorPalette((ColorPicker)this.getSkinnable());
         this.popupContent.setPopupControl(this.getPopup());
      }

      return this.popupContent;
   }

   @Override
   public void show() {
      super.show();
      ColorPicker var1 = (ColorPicker)this.getSkinnable();
      this.popupContent.updateSelection(var1.getValue());
   }

   @Override
   public Node getDisplayNode() {
      return this.displayNode;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.updateComboBoxMode();
      super.layoutChildren(var1, var3, var5, var7);
   }

   @Override
   void focusLost() {
   }

   @Override
   ComboBoxBaseBehavior getBehavior() {
      return this.behavior;
   }

   private void updateComboBoxMode() {
      ObservableList var1 = ((ComboBoxBase)this.getSkinnable()).getStyleClass();
      if (var1.contains("button")) {
         this.setMode(ComboBoxMode.BUTTON);
      } else if (var1.contains("split-button")) {
         this.setMode(ComboBoxMode.SPLITBUTTON);
      }
   }

   static String colorDisplayName(Color var0) {
      if (var0 != null) {
         String var1 = COLOR_NAME_MAP.get(var0);
         if (var1 == null) {
            var1 = Utils.formatHexString(var0);
         }

         return var1;
      } else {
         return null;
      }
   }

   static String tooltipString(Color var0) {
      if (var0 != null) {
         String var1 = "";
         String var2 = COLOR_NAME_MAP.get(var0);
         if (var2 != null) {
            var1 = var1 + var2 + " ";
         }

         var1 = var1 + Utils.formatHexString(var0);
         String var3 = CSS_NAME_MAP.get(var0);
         if (var3 != null) {
            var1 = var1 + " (css: " + var3 + ")";
         }

         return var1;
      } else {
         return null;
      }
   }

   private void updateColor() {
      ColorPicker var1 = (ColorPicker)this.getSkinnable();
      this.colorRect.setFill((Paint)var1.getValue());
      if (this.colorLabelVisible.get()) {
         this.displayNode.setText(colorDisplayName(var1.getValue()));
      } else {
         this.displayNode.setText("");
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ColorPickerSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected StringConverter<Color> getConverter() {
      return null;
   }

   @Override
   protected TextField getEditor() {
      return null;
   }

   private class PickerColorBox extends StackPane {
      protected void layoutChildren() {
         double var1 = this.snappedTopInset();
         double var3 = this.snappedLeftInset();
         double var5 = this.getWidth();
         double var7 = this.getHeight();
         double var9 = this.snappedRightInset();
         double var11 = this.snappedBottomInset();
         ColorPickerSkin.this.colorRect.setX(this.snapPositionX(ColorPickerSkin.this.colorRectX.get()));
         ColorPickerSkin.this.colorRect.setY(this.snapPositionY(ColorPickerSkin.this.colorRectY.get()));
         ColorPickerSkin.this.colorRect.setWidth(this.snapSizeX(ColorPickerSkin.this.colorRectWidth.get()));
         ColorPickerSkin.this.colorRect.setHeight(this.snapSizeY(ColorPickerSkin.this.colorRectHeight.get()));
         if (this.getChildren().size() == 2) {
            ImageView var13 = (ImageView)this.getChildren().get(1);
            Pos var14 = StackPane.getAlignment(var13);
            this.layoutInArea(
               var13,
               var3,
               var1,
               var5 - var3 - var9,
               var7 - var1 - var11,
               0.0,
               getMargin(var13),
               var14 != null ? var14.getHpos() : this.getAlignment().getHpos(),
               var14 != null ? var14.getVpos() : this.getAlignment().getVpos()
            );
            ColorPickerSkin.this.colorRect.setLayoutX(var13.getLayoutX());
            ColorPickerSkin.this.colorRect.setLayoutY(var13.getLayoutY());
         } else {
            Pos var15 = StackPane.getAlignment(ColorPickerSkin.this.colorRect);
            this.layoutInArea(
               ColorPickerSkin.this.colorRect,
               var3,
               var1,
               var5 - var3 - var9,
               var7 - var1 - var11,
               0.0,
               getMargin(ColorPickerSkin.this.colorRect),
               var15 != null ? var15.getHpos() : this.getAlignment().getHpos(),
               var15 != null ? var15.getVpos() : this.getAlignment().getVpos()
            );
         }
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<ColorPicker, Boolean> COLOR_LABEL_VISIBLE = new CssMetaData<ColorPicker, Boolean>(
         "-fx-color-label-visible", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.colorLabelVisible == null || !var2.colorLabelVisible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return (StyleableProperty<Boolean>)var2.colorLabelVisible;
         }
      };
      private static final CssMetaData<ColorPicker, Number> COLOR_RECT_WIDTH = new CssMetaData<ColorPicker, Number>(
         "-fx-color-rect-width", SizeConverter.getInstance(), 12.0
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return !var2.colorRectWidth.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.colorRectWidth;
         }
      };
      private static final CssMetaData<ColorPicker, Number> COLOR_RECT_HEIGHT = new CssMetaData<ColorPicker, Number>(
         "-fx-color-rect-height", SizeConverter.getInstance(), 12.0
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return !var2.colorRectHeight.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.colorRectHeight;
         }
      };
      private static final CssMetaData<ColorPicker, Number> COLOR_RECT_X = new CssMetaData<ColorPicker, Number>(
         "-fx-color-rect-x", SizeConverter.getInstance(), 0
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return !var2.colorRectX.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.colorRectX;
         }
      };
      private static final CssMetaData<ColorPicker, Number> COLOR_RECT_Y = new CssMetaData<ColorPicker, Number>(
         "-fx-color-rect-y", SizeConverter.getInstance(), 0
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return !var2.colorRectY.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.colorRectY;
         }
      };
      private static final CssMetaData<ColorPicker, String> GRAPHIC = new CssMetaData<ColorPicker, String>(
         "-fx-graphic", javafx.css.converter.StringConverter.getInstance()
      ) {
         public boolean isSettable(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return !var2.imageUrl.isBound();
         }

         public StyleableProperty<String> getStyleableProperty(ColorPicker var1) {
            ColorPickerSkin var2 = (ColorPickerSkin)var1.getSkin();
            return var2.imageUrl;
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(ComboBoxBaseSkin.getClassCssMetaData());
         var0.add(COLOR_LABEL_VISIBLE);
         var0.add(COLOR_RECT_WIDTH);
         var0.add(COLOR_RECT_HEIGHT);
         var0.add(COLOR_RECT_X);
         var0.add(COLOR_RECT_Y);
         var0.add(GRAPHIC);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
