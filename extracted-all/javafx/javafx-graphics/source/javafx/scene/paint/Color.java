package javafx.scene.paint;

import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.util.Locale;
import java.util.Map;
import javafx.animation.Interpolatable;
import javafx.beans.NamedArg;

public final class Color extends Paint implements Interpolatable<Color> {
   private static final double DARKER_BRIGHTER_FACTOR = 0.7;
   private static final double SATURATE_DESATURATE_FACTOR = 0.7;
   private static final int PARSE_COMPONENT = 0;
   private static final int PARSE_PERCENT = 1;
   private static final int PARSE_ANGLE = 2;
   private static final int PARSE_ALPHA = 3;
   public static final Color TRANSPARENT = new Color(0.0, 0.0, 0.0, 0.0);
   public static final Color ALICEBLUE = new Color(0.9411765F, 0.972549F, 1.0F);
   public static final Color ANTIQUEWHITE = new Color(0.98039216F, 0.92156863F, 0.84313726F);
   public static final Color AQUA = new Color(0.0F, 1.0F, 1.0F);
   public static final Color AQUAMARINE = new Color(0.49803922F, 1.0F, 0.83137256F);
   public static final Color AZURE = new Color(0.9411765F, 1.0F, 1.0F);
   public static final Color BEIGE = new Color(0.9607843F, 0.9607843F, 0.8627451F);
   public static final Color BISQUE = new Color(1.0F, 0.89411765F, 0.76862746F);
   public static final Color BLACK = new Color(0.0F, 0.0F, 0.0F);
   public static final Color BLANCHEDALMOND = new Color(1.0F, 0.92156863F, 0.8039216F);
   public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F);
   public static final Color BLUEVIOLET = new Color(0.5411765F, 0.16862746F, 0.8862745F);
   public static final Color BROWN = new Color(0.64705884F, 0.16470589F, 0.16470589F);
   public static final Color BURLYWOOD = new Color(0.87058824F, 0.72156864F, 0.5294118F);
   public static final Color CADETBLUE = new Color(0.37254903F, 0.61960787F, 0.627451F);
   public static final Color CHARTREUSE = new Color(0.49803922F, 1.0F, 0.0F);
   public static final Color CHOCOLATE = new Color(0.8235294F, 0.4117647F, 0.11764706F);
   public static final Color CORAL = new Color(1.0F, 0.49803922F, 0.3137255F);
   public static final Color CORNFLOWERBLUE = new Color(0.39215687F, 0.58431375F, 0.92941177F);
   public static final Color CORNSILK = new Color(1.0F, 0.972549F, 0.8627451F);
   public static final Color CRIMSON = new Color(0.8627451F, 0.078431375F, 0.23529412F);
   public static final Color CYAN = new Color(0.0F, 1.0F, 1.0F);
   public static final Color DARKBLUE = new Color(0.0F, 0.0F, 0.54509807F);
   public static final Color DARKCYAN = new Color(0.0F, 0.54509807F, 0.54509807F);
   public static final Color DARKGOLDENROD = new Color(0.72156864F, 0.5254902F, 0.043137256F);
   public static final Color DARKGRAY = new Color(0.6627451F, 0.6627451F, 0.6627451F);
   public static final Color DARKGREEN = new Color(0.0F, 0.39215687F, 0.0F);
   public static final Color DARKGREY = DARKGRAY;
   public static final Color DARKKHAKI = new Color(0.7411765F, 0.7176471F, 0.41960785F);
   public static final Color DARKMAGENTA = new Color(0.54509807F, 0.0F, 0.54509807F);
   public static final Color DARKOLIVEGREEN = new Color(0.33333334F, 0.41960785F, 0.18431373F);
   public static final Color DARKORANGE = new Color(1.0F, 0.54901963F, 0.0F);
   public static final Color DARKORCHID = new Color(0.6F, 0.19607843F, 0.8F);
   public static final Color DARKRED = new Color(0.54509807F, 0.0F, 0.0F);
   public static final Color DARKSALMON = new Color(0.9137255F, 0.5882353F, 0.47843137F);
   public static final Color DARKSEAGREEN = new Color(0.56078434F, 0.7372549F, 0.56078434F);
   public static final Color DARKSLATEBLUE = new Color(0.28235295F, 0.23921569F, 0.54509807F);
   public static final Color DARKSLATEGRAY = new Color(0.18431373F, 0.30980393F, 0.30980393F);
   public static final Color DARKSLATEGREY = DARKSLATEGRAY;
   public static final Color DARKTURQUOISE = new Color(0.0F, 0.80784315F, 0.81960785F);
   public static final Color DARKVIOLET = new Color(0.5803922F, 0.0F, 0.827451F);
   public static final Color DEEPPINK = new Color(1.0F, 0.078431375F, 0.5764706F);
   public static final Color DEEPSKYBLUE = new Color(0.0F, 0.7490196F, 1.0F);
   public static final Color DIMGRAY = new Color(0.4117647F, 0.4117647F, 0.4117647F);
   public static final Color DIMGREY = DIMGRAY;
   public static final Color DODGERBLUE = new Color(0.11764706F, 0.5647059F, 1.0F);
   public static final Color FIREBRICK = new Color(0.69803923F, 0.13333334F, 0.13333334F);
   public static final Color FLORALWHITE = new Color(1.0F, 0.98039216F, 0.9411765F);
   public static final Color FORESTGREEN = new Color(0.13333334F, 0.54509807F, 0.13333334F);
   public static final Color FUCHSIA = new Color(1.0F, 0.0F, 1.0F);
   public static final Color GAINSBORO = new Color(0.8627451F, 0.8627451F, 0.8627451F);
   public static final Color GHOSTWHITE = new Color(0.972549F, 0.972549F, 1.0F);
   public static final Color GOLD = new Color(1.0F, 0.84313726F, 0.0F);
   public static final Color GOLDENROD = new Color(0.85490197F, 0.64705884F, 0.1254902F);
   public static final Color GRAY = new Color(0.5019608F, 0.5019608F, 0.5019608F);
   public static final Color GREEN = new Color(0.0F, 0.5019608F, 0.0F);
   public static final Color GREENYELLOW = new Color(0.6784314F, 1.0F, 0.18431373F);
   public static final Color GREY = GRAY;
   public static final Color HONEYDEW = new Color(0.9411765F, 1.0F, 0.9411765F);
   public static final Color HOTPINK = new Color(1.0F, 0.4117647F, 0.7058824F);
   public static final Color INDIANRED = new Color(0.8039216F, 0.36078432F, 0.36078432F);
   public static final Color INDIGO = new Color(0.29411766F, 0.0F, 0.50980395F);
   public static final Color IVORY = new Color(1.0F, 1.0F, 0.9411765F);
   public static final Color KHAKI = new Color(0.9411765F, 0.9019608F, 0.54901963F);
   public static final Color LAVENDER = new Color(0.9019608F, 0.9019608F, 0.98039216F);
   public static final Color LAVENDERBLUSH = new Color(1.0F, 0.9411765F, 0.9607843F);
   public static final Color LAWNGREEN = new Color(0.4862745F, 0.9882353F, 0.0F);
   public static final Color LEMONCHIFFON = new Color(1.0F, 0.98039216F, 0.8039216F);
   public static final Color LIGHTBLUE = new Color(0.6784314F, 0.84705883F, 0.9019608F);
   public static final Color LIGHTCORAL = new Color(0.9411765F, 0.5019608F, 0.5019608F);
   public static final Color LIGHTCYAN = new Color(0.8784314F, 1.0F, 1.0F);
   public static final Color LIGHTGOLDENRODYELLOW = new Color(0.98039216F, 0.98039216F, 0.8235294F);
   public static final Color LIGHTGRAY = new Color(0.827451F, 0.827451F, 0.827451F);
   public static final Color LIGHTGREEN = new Color(0.5647059F, 0.93333334F, 0.5647059F);
   public static final Color LIGHTGREY = LIGHTGRAY;
   public static final Color LIGHTPINK = new Color(1.0F, 0.7137255F, 0.75686276F);
   public static final Color LIGHTSALMON = new Color(1.0F, 0.627451F, 0.47843137F);
   public static final Color LIGHTSEAGREEN = new Color(0.1254902F, 0.69803923F, 0.6666667F);
   public static final Color LIGHTSKYBLUE = new Color(0.5294118F, 0.80784315F, 0.98039216F);
   public static final Color LIGHTSLATEGRAY = new Color(0.46666667F, 0.53333336F, 0.6F);
   public static final Color LIGHTSLATEGREY = LIGHTSLATEGRAY;
   public static final Color LIGHTSTEELBLUE = new Color(0.6901961F, 0.76862746F, 0.87058824F);
   public static final Color LIGHTYELLOW = new Color(1.0F, 1.0F, 0.8784314F);
   public static final Color LIME = new Color(0.0F, 1.0F, 0.0F);
   public static final Color LIMEGREEN = new Color(0.19607843F, 0.8039216F, 0.19607843F);
   public static final Color LINEN = new Color(0.98039216F, 0.9411765F, 0.9019608F);
   public static final Color MAGENTA = new Color(1.0F, 0.0F, 1.0F);
   public static final Color MAROON = new Color(0.5019608F, 0.0F, 0.0F);
   public static final Color MEDIUMAQUAMARINE = new Color(0.4F, 0.8039216F, 0.6666667F);
   public static final Color MEDIUMBLUE = new Color(0.0F, 0.0F, 0.8039216F);
   public static final Color MEDIUMORCHID = new Color(0.7294118F, 0.33333334F, 0.827451F);
   public static final Color MEDIUMPURPLE = new Color(0.5764706F, 0.4392157F, 0.85882354F);
   public static final Color MEDIUMSEAGREEN = new Color(0.23529412F, 0.7019608F, 0.44313726F);
   public static final Color MEDIUMSLATEBLUE = new Color(0.48235294F, 0.40784314F, 0.93333334F);
   public static final Color MEDIUMSPRINGGREEN = new Color(0.0F, 0.98039216F, 0.6039216F);
   public static final Color MEDIUMTURQUOISE = new Color(0.28235295F, 0.81960785F, 0.8F);
   public static final Color MEDIUMVIOLETRED = new Color(0.78039217F, 0.08235294F, 0.52156866F);
   public static final Color MIDNIGHTBLUE = new Color(0.09803922F, 0.09803922F, 0.4392157F);
   public static final Color MINTCREAM = new Color(0.9607843F, 1.0F, 0.98039216F);
   public static final Color MISTYROSE = new Color(1.0F, 0.89411765F, 0.88235295F);
   public static final Color MOCCASIN = new Color(1.0F, 0.89411765F, 0.70980394F);
   public static final Color NAVAJOWHITE = new Color(1.0F, 0.87058824F, 0.6784314F);
   public static final Color NAVY = new Color(0.0F, 0.0F, 0.5019608F);
   public static final Color OLDLACE = new Color(0.99215686F, 0.9607843F, 0.9019608F);
   public static final Color OLIVE = new Color(0.5019608F, 0.5019608F, 0.0F);
   public static final Color OLIVEDRAB = new Color(0.41960785F, 0.5568628F, 0.13725491F);
   public static final Color ORANGE = new Color(1.0F, 0.64705884F, 0.0F);
   public static final Color ORANGERED = new Color(1.0F, 0.27058825F, 0.0F);
   public static final Color ORCHID = new Color(0.85490197F, 0.4392157F, 0.8392157F);
   public static final Color PALEGOLDENROD = new Color(0.93333334F, 0.9098039F, 0.6666667F);
   public static final Color PALEGREEN = new Color(0.59607846F, 0.9843137F, 0.59607846F);
   public static final Color PALETURQUOISE = new Color(0.6862745F, 0.93333334F, 0.93333334F);
   public static final Color PALEVIOLETRED = new Color(0.85882354F, 0.4392157F, 0.5764706F);
   public static final Color PAPAYAWHIP = new Color(1.0F, 0.9372549F, 0.8352941F);
   public static final Color PEACHPUFF = new Color(1.0F, 0.85490197F, 0.7254902F);
   public static final Color PERU = new Color(0.8039216F, 0.52156866F, 0.24705882F);
   public static final Color PINK = new Color(1.0F, 0.7529412F, 0.79607844F);
   public static final Color PLUM = new Color(0.8666667F, 0.627451F, 0.8666667F);
   public static final Color POWDERBLUE = new Color(0.6901961F, 0.8784314F, 0.9019608F);
   public static final Color PURPLE = new Color(0.5019608F, 0.0F, 0.5019608F);
   public static final Color RED = new Color(1.0F, 0.0F, 0.0F);
   public static final Color ROSYBROWN = new Color(0.7372549F, 0.56078434F, 0.56078434F);
   public static final Color ROYALBLUE = new Color(0.25490198F, 0.4117647F, 0.88235295F);
   public static final Color SADDLEBROWN = new Color(0.54509807F, 0.27058825F, 0.07450981F);
   public static final Color SALMON = new Color(0.98039216F, 0.5019608F, 0.44705883F);
   public static final Color SANDYBROWN = new Color(0.95686275F, 0.6431373F, 0.3764706F);
   public static final Color SEAGREEN = new Color(0.18039216F, 0.54509807F, 0.34117648F);
   public static final Color SEASHELL = new Color(1.0F, 0.9607843F, 0.93333334F);
   public static final Color SIENNA = new Color(0.627451F, 0.32156864F, 0.1764706F);
   public static final Color SILVER = new Color(0.7529412F, 0.7529412F, 0.7529412F);
   public static final Color SKYBLUE = new Color(0.5294118F, 0.80784315F, 0.92156863F);
   public static final Color SLATEBLUE = new Color(0.41568628F, 0.3529412F, 0.8039216F);
   public static final Color SLATEGRAY = new Color(0.4392157F, 0.5019608F, 0.5647059F);
   public static final Color SLATEGREY = SLATEGRAY;
   public static final Color SNOW = new Color(1.0F, 0.98039216F, 0.98039216F);
   public static final Color SPRINGGREEN = new Color(0.0F, 1.0F, 0.49803922F);
   public static final Color STEELBLUE = new Color(0.27450982F, 0.50980395F, 0.7058824F);
   public static final Color TAN = new Color(0.8235294F, 0.7058824F, 0.54901963F);
   public static final Color TEAL = new Color(0.0F, 0.5019608F, 0.5019608F);
   public static final Color THISTLE = new Color(0.84705883F, 0.7490196F, 0.84705883F);
   public static final Color TOMATO = new Color(1.0F, 0.3882353F, 0.2784314F);
   public static final Color TURQUOISE = new Color(0.2509804F, 0.8784314F, 0.8156863F);
   public static final Color VIOLET = new Color(0.93333334F, 0.50980395F, 0.93333334F);
   public static final Color WHEAT = new Color(0.9607843F, 0.87058824F, 0.7019608F);
   public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F);
   public static final Color WHITESMOKE = new Color(0.9607843F, 0.9607843F, 0.9607843F);
   public static final Color YELLOW = new Color(1.0F, 1.0F, 0.0F);
   public static final Color YELLOWGREEN = new Color(0.6039216F, 0.8039216F, 0.19607843F);
   private final float red;
   private final float green;
   private final float blue;
   private final float opacity;
   private Object platformPaint;

   public static Color color(double var0, double var2, double var4, double var6) {
      return new Color(var0, var2, var4, var6);
   }

   public static Color color(double var0, double var2, double var4) {
      return new Color(var0, var2, var4, 1.0);
   }

   public static Color rgb(int var0, int var1, int var2, double var3) {
      checkRGB(var0, var1, var2);
      return new Color((double)var0 / 255.0, (double)var1 / 255.0, (double)var2 / 255.0, var3);
   }

   public static Color rgb(int var0, int var1, int var2) {
      checkRGB(var0, var1, var2);
      return new Color((double)var0 / 255.0, (double)var1 / 255.0, (double)var2 / 255.0, 1.0);
   }

   public static Color grayRgb(int var0) {
      return rgb(var0, var0, var0);
   }

   public static Color grayRgb(int var0, double var1) {
      return rgb(var0, var0, var0, var1);
   }

   public static Color gray(double var0, double var2) {
      return new Color(var0, var0, var0, var2);
   }

   public static Color gray(double var0) {
      return gray(var0, 1.0);
   }

   private static void checkRGB(int var0, int var1, int var2) {
      if (var0 < 0 || var0 > 255) {
         throw new IllegalArgumentException("Color.rgb's red parameter (" + var0 + ") expects color values 0-255");
      } else if (var1 < 0 || var1 > 255) {
         throw new IllegalArgumentException("Color.rgb's green parameter (" + var1 + ") expects color values 0-255");
      } else if (var2 < 0 || var2 > 255) {
         throw new IllegalArgumentException("Color.rgb's blue parameter (" + var2 + ") expects color values 0-255");
      }
   }

   public static Color hsb(double var0, double var2, double var4, double var6) {
      checkSB(var2, var4);
      double[] var8 = Utils.HSBtoRGB(var0, var2, var4);
      return new Color(var8[0], var8[1], var8[2], var6);
   }

   public static Color hsb(double var0, double var2, double var4) {
      return hsb(var0, var2, var4, 1.0);
   }

   private static void checkSB(double var0, double var2) {
      if (var0 < 0.0 || var0 > 1.0) {
         throw new IllegalArgumentException("Color.hsb's saturation parameter (" + var0 + ") expects values 0.0-1.0");
      } else if (var2 < 0.0 || var2 > 1.0) {
         throw new IllegalArgumentException("Color.hsb's brightness parameter (" + var2 + ") expects values 0.0-1.0");
      }
   }

   public static Color web(String var0, double var1) {
      if (var0 == null) {
         throw new NullPointerException("The color components or name must be specified");
      } else if (var0.isEmpty()) {
         throw new IllegalArgumentException("Invalid color specification");
      } else {
         String var3 = var0.toLowerCase(Locale.ROOT);
         if (var3.startsWith("#")) {
            var3 = var3.substring(1);
         } else if (var3.startsWith("0x")) {
            var3 = var3.substring(2);
         } else if (var3.startsWith("rgb")) {
            if (var3.startsWith("(", 3)) {
               return parseRGBColor(var3, 4, false, var1);
            }

            if (var3.startsWith("a(", 3)) {
               return parseRGBColor(var3, 5, true, var1);
            }
         } else if (var3.startsWith("hsl")) {
            if (var3.startsWith("(", 3)) {
               return parseHSLColor(var3, 4, false, var1);
            }

            if (var3.startsWith("a(", 3)) {
               return parseHSLColor(var3, 5, true, var1);
            }
         } else {
            Color var4 = Color.NamedColors.get(var3);
            if (var4 != null) {
               if (var1 == 1.0) {
                  return var4;
               }

               return color((double)var4.red, (double)var4.green, (double)var4.blue, var1);
            }
         }

         int var10 = var3.length();

         try {
            if (var10 == 3) {
               int var13 = Integer.parseInt(var3.substring(0, 1), 16);
               int var16 = Integer.parseInt(var3.substring(1, 2), 16);
               int var19 = Integer.parseInt(var3.substring(2, 3), 16);
               return color((double)var13 / 15.0, (double)var16 / 15.0, (double)var19 / 15.0, var1);
            }

            if (var10 == 4) {
               int var12 = Integer.parseInt(var3.substring(0, 1), 16);
               int var15 = Integer.parseInt(var3.substring(1, 2), 16);
               int var18 = Integer.parseInt(var3.substring(2, 3), 16);
               int var20 = Integer.parseInt(var3.substring(3, 4), 16);
               return color((double)var12 / 15.0, (double)var15 / 15.0, (double)var18 / 15.0, var1 * (double)var20 / 15.0);
            }

            if (var10 == 6) {
               int var11 = Integer.parseInt(var3.substring(0, 2), 16);
               int var14 = Integer.parseInt(var3.substring(2, 4), 16);
               int var17 = Integer.parseInt(var3.substring(4, 6), 16);
               return rgb(var11, var14, var17, var1);
            }

            if (var10 == 8) {
               int var5 = Integer.parseInt(var3.substring(0, 2), 16);
               int var6 = Integer.parseInt(var3.substring(2, 4), 16);
               int var7 = Integer.parseInt(var3.substring(4, 6), 16);
               int var8 = Integer.parseInt(var3.substring(6, 8), 16);
               return rgb(var5, var6, var7, var1 * (double)var8 / 255.0);
            }
         } catch (NumberFormatException var9) {
         }

         throw new IllegalArgumentException("Invalid color specification");
      }
   }

   private static Color parseRGBColor(String var0, int var1, boolean var2, double var3) {
      try {
         int var5 = var0.indexOf(44, var1);
         int var6 = var5 < 0 ? -1 : var0.indexOf(44, var5 + 1);
         int var7 = var6 < 0 ? -1 : var0.indexOf(var2 ? 44 : 41, var6 + 1);
         int var8 = var2 ? (var7 < 0 ? -1 : var0.indexOf(41, var7 + 1)) : var7;
         if (var8 >= 0) {
            double var9 = parseComponent(var0, var1, var5, 0);
            double var11 = parseComponent(var0, var5 + 1, var6, 0);
            double var13 = parseComponent(var0, var6 + 1, var7, 0);
            if (var2) {
               var3 *= parseComponent(var0, var7 + 1, var8, 3);
            }

            return new Color(var9, var11, var13, var3);
         }
      } catch (NumberFormatException var15) {
      }

      throw new IllegalArgumentException("Invalid color specification");
   }

   private static Color parseHSLColor(String var0, int var1, boolean var2, double var3) {
      try {
         int var5 = var0.indexOf(44, var1);
         int var6 = var5 < 0 ? -1 : var0.indexOf(44, var5 + 1);
         int var7 = var6 < 0 ? -1 : var0.indexOf(var2 ? 44 : 41, var6 + 1);
         int var8 = var2 ? (var7 < 0 ? -1 : var0.indexOf(41, var7 + 1)) : var7;
         if (var8 >= 0) {
            double var9 = parseComponent(var0, var1, var5, 2);
            double var11 = parseComponent(var0, var5 + 1, var6, 1);
            double var13 = parseComponent(var0, var6 + 1, var7, 1);
            if (var2) {
               var3 *= parseComponent(var0, var7 + 1, var8, 3);
            }

            return hsb(var9, var11, var13, var3);
         }
      } catch (NumberFormatException var15) {
      }

      throw new IllegalArgumentException("Invalid color specification");
   }

   private static double parseComponent(String var0, int var1, int var2, int var3) {
      var0 = var0.substring(var1, var2).trim();
      if (var0.endsWith("%")) {
         if (var3 > 1) {
            throw new IllegalArgumentException("Invalid color specification");
         }

         var3 = 1;
         var0 = var0.substring(0, var0.length() - 1).trim();
      } else if (var3 == 1) {
         throw new IllegalArgumentException("Invalid color specification");
      }

      double var4 = var3 == 0 ? (double)Integer.parseInt(var0) : Double.parseDouble(var0);
      switch (var3) {
         case 0:
            return var4 <= 0.0 ? 0.0 : (var4 >= 255.0 ? 1.0 : var4 / 255.0);
         case 1:
            return var4 <= 0.0 ? 0.0 : (var4 >= 100.0 ? 1.0 : var4 / 100.0);
         case 2:
            return var4 < 0.0 ? var4 % 360.0 + 360.0 : (var4 > 360.0 ? var4 % 360.0 : var4);
         case 3:
            return var4 < 0.0 ? 0.0 : (var4 > 1.0 ? 1.0 : var4);
         default:
            throw new IllegalArgumentException("Invalid color specification");
      }
   }

   public static Color web(String var0) {
      return web(var0, 1.0);
   }

   public static Color valueOf(String var0) {
      if (var0 == null) {
         throw new NullPointerException("color must be specified");
      } else {
         return web(var0);
      }
   }

   private static int to32BitInteger(int var0, int var1, int var2, int var3) {
      int var4 = var0 << 8;
      var4 |= var1;
      var4 <<= 8;
      var4 |= var2;
      var4 <<= 8;
      return var4 | var3;
   }

   public double getHue() {
      return Utils.RGBtoHSB((double)this.red, (double)this.green, (double)this.blue)[0];
   }

   public double getSaturation() {
      return Utils.RGBtoHSB((double)this.red, (double)this.green, (double)this.blue)[1];
   }

   public double getBrightness() {
      return Utils.RGBtoHSB((double)this.red, (double)this.green, (double)this.blue)[2];
   }

   public Color deriveColor(double var1, double var3, double var5, double var7) {
      double[] var9 = Utils.RGBtoHSB((double)this.red, (double)this.green, (double)this.blue);
      double var10 = var9[2];
      if (var10 == 0.0 && var5 > 1.0) {
         var10 = 0.05;
      }

      double var12 = ((var9[0] + var1) % 360.0 + 360.0) % 360.0;
      double var14 = Math.max(Math.min(var9[1] * var3, 1.0), 0.0);
      var10 = Math.max(Math.min(var10 * var5, 1.0), 0.0);
      double var16 = Math.max(Math.min((double)this.opacity * var7, 1.0), 0.0);
      return hsb(var12, var14, var10, var16);
   }

   public Color brighter() {
      return this.deriveColor(0.0, 1.0, 1.4285714285714286, 1.0);
   }

   public Color darker() {
      return this.deriveColor(0.0, 1.0, 0.7, 1.0);
   }

   public Color saturate() {
      return this.deriveColor(0.0, 1.4285714285714286, 1.0, 1.0);
   }

   public Color desaturate() {
      return this.deriveColor(0.0, 0.7, 1.0, 1.0);
   }

   public Color grayscale() {
      double var1 = 0.21 * (double)this.red + 0.71 * (double)this.green + 0.07 * (double)this.blue;
      return color(var1, var1, var1, (double)this.opacity);
   }

   public Color invert() {
      return color(1.0 - (double)this.red, 1.0 - (double)this.green, 1.0 - (double)this.blue, (double)this.opacity);
   }

   public final double getRed() {
      return (double)this.red;
   }

   public final double getGreen() {
      return (double)this.green;
   }

   public final double getBlue() {
      return (double)this.blue;
   }

   public final double getOpacity() {
      return (double)this.opacity;
   }

   @Override
   public final boolean isOpaque() {
      return this.opacity >= 1.0F;
   }

   public Color(
      @NamedArg("red") double var1, @NamedArg("green") double var3, @NamedArg("blue") double var5, @NamedArg(value = "opacity",defaultValue = "1") double var7
   ) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new IllegalArgumentException("Color's red value (" + var1 + ") must be in the range 0.0-1.0");
      } else if (var3 < 0.0 || var3 > 1.0) {
         throw new IllegalArgumentException("Color's green value (" + var3 + ") must be in the range 0.0-1.0");
      } else if (var5 < 0.0 || var5 > 1.0) {
         throw new IllegalArgumentException("Color's blue value (" + var5 + ") must be in the range 0.0-1.0");
      } else if (!(var7 < 0.0) && !(var7 > 1.0)) {
         this.red = (float)var1;
         this.green = (float)var3;
         this.blue = (float)var5;
         this.opacity = (float)var7;
      } else {
         throw new IllegalArgumentException("Color's opacity value (" + var7 + ") must be in the range 0.0-1.0");
      }
   }

   private Color(float var1, float var2, float var3) {
      this.red = var1;
      this.green = var2;
      this.blue = var3;
      this.opacity = 1.0F;
   }

   @Override
   Object acc_getPlatformPaint() {
      if (this.platformPaint == null) {
         this.platformPaint = Toolkit.getToolkit().getPaint(this);
      }

      return this.platformPaint;
   }

   public Color interpolate(Color var1, double var2) {
      if (var2 <= 0.0) {
         return this;
      } else if (var2 >= 1.0) {
         return var1;
      } else {
         float var4 = (float)var2;
         return new Color(
            (double)(this.red + (var1.red - this.red) * var4),
            (double)(this.green + (var1.green - this.green) * var4),
            (double)(this.blue + (var1.blue - this.blue) * var4),
            (double)(this.opacity + (var1.opacity - this.opacity) * var4)
         );
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Color)) {
         return false;
      } else {
         Color var2 = (Color)var1;
         return this.red == var2.red && this.green == var2.green && this.blue == var2.blue && this.opacity == var2.opacity;
      }
   }

   @Override
   public int hashCode() {
      int var1 = (int)Math.round((double)this.red * 255.0);
      int var2 = (int)Math.round((double)this.green * 255.0);
      int var3 = (int)Math.round((double)this.blue * 255.0);
      int var4 = (int)Math.round((double)this.opacity * 255.0);
      return to32BitInteger(var1, var2, var3, var4);
   }

   @Override
   public String toString() {
      int var1 = (int)Math.round((double)this.red * 255.0);
      int var2 = (int)Math.round((double)this.green * 255.0);
      int var3 = (int)Math.round((double)this.blue * 255.0);
      int var4 = (int)Math.round((double)this.opacity * 255.0);
      return String.format("0x%02x%02x%02x%02x", var1, var2, var3, var4);
   }

   private static final class NamedColors {
      private static final Map<String, Color> NAMED_COLORS = Map.ofEntries(
         Map.entry("aliceblue", Color.ALICEBLUE),
         Map.entry("antiquewhite", Color.ANTIQUEWHITE),
         Map.entry("aqua", Color.AQUA),
         Map.entry("aquamarine", Color.AQUAMARINE),
         Map.entry("azure", Color.AZURE),
         Map.entry("beige", Color.BEIGE),
         Map.entry("bisque", Color.BISQUE),
         Map.entry("black", Color.BLACK),
         Map.entry("blanchedalmond", Color.BLANCHEDALMOND),
         Map.entry("blue", Color.BLUE),
         Map.entry("blueviolet", Color.BLUEVIOLET),
         Map.entry("brown", Color.BROWN),
         Map.entry("burlywood", Color.BURLYWOOD),
         Map.entry("cadetblue", Color.CADETBLUE),
         Map.entry("chartreuse", Color.CHARTREUSE),
         Map.entry("chocolate", Color.CHOCOLATE),
         Map.entry("coral", Color.CORAL),
         Map.entry("cornflowerblue", Color.CORNFLOWERBLUE),
         Map.entry("cornsilk", Color.CORNSILK),
         Map.entry("crimson", Color.CRIMSON),
         Map.entry("cyan", Color.CYAN),
         Map.entry("darkblue", Color.DARKBLUE),
         Map.entry("darkcyan", Color.DARKCYAN),
         Map.entry("darkgoldenrod", Color.DARKGOLDENROD),
         Map.entry("darkgray", Color.DARKGRAY),
         Map.entry("darkgreen", Color.DARKGREEN),
         Map.entry("darkgrey", Color.DARKGREY),
         Map.entry("darkkhaki", Color.DARKKHAKI),
         Map.entry("darkmagenta", Color.DARKMAGENTA),
         Map.entry("darkolivegreen", Color.DARKOLIVEGREEN),
         Map.entry("darkorange", Color.DARKORANGE),
         Map.entry("darkorchid", Color.DARKORCHID),
         Map.entry("darkred", Color.DARKRED),
         Map.entry("darksalmon", Color.DARKSALMON),
         Map.entry("darkseagreen", Color.DARKSEAGREEN),
         Map.entry("darkslateblue", Color.DARKSLATEBLUE),
         Map.entry("darkslategray", Color.DARKSLATEGRAY),
         Map.entry("darkslategrey", Color.DARKSLATEGREY),
         Map.entry("darkturquoise", Color.DARKTURQUOISE),
         Map.entry("darkviolet", Color.DARKVIOLET),
         Map.entry("deeppink", Color.DEEPPINK),
         Map.entry("deepskyblue", Color.DEEPSKYBLUE),
         Map.entry("dimgray", Color.DIMGRAY),
         Map.entry("dimgrey", Color.DIMGREY),
         Map.entry("dodgerblue", Color.DODGERBLUE),
         Map.entry("firebrick", Color.FIREBRICK),
         Map.entry("floralwhite", Color.FLORALWHITE),
         Map.entry("forestgreen", Color.FORESTGREEN),
         Map.entry("fuchsia", Color.FUCHSIA),
         Map.entry("gainsboro", Color.GAINSBORO),
         Map.entry("ghostwhite", Color.GHOSTWHITE),
         Map.entry("gold", Color.GOLD),
         Map.entry("goldenrod", Color.GOLDENROD),
         Map.entry("gray", Color.GRAY),
         Map.entry("green", Color.GREEN),
         Map.entry("greenyellow", Color.GREENYELLOW),
         Map.entry("grey", Color.GREY),
         Map.entry("honeydew", Color.HONEYDEW),
         Map.entry("hotpink", Color.HOTPINK),
         Map.entry("indianred", Color.INDIANRED),
         Map.entry("indigo", Color.INDIGO),
         Map.entry("ivory", Color.IVORY),
         Map.entry("khaki", Color.KHAKI),
         Map.entry("lavender", Color.LAVENDER),
         Map.entry("lavenderblush", Color.LAVENDERBLUSH),
         Map.entry("lawngreen", Color.LAWNGREEN),
         Map.entry("lemonchiffon", Color.LEMONCHIFFON),
         Map.entry("lightblue", Color.LIGHTBLUE),
         Map.entry("lightcoral", Color.LIGHTCORAL),
         Map.entry("lightcyan", Color.LIGHTCYAN),
         Map.entry("lightgoldenrodyellow", Color.LIGHTGOLDENRODYELLOW),
         Map.entry("lightgray", Color.LIGHTGRAY),
         Map.entry("lightgreen", Color.LIGHTGREEN),
         Map.entry("lightgrey", Color.LIGHTGREY),
         Map.entry("lightpink", Color.LIGHTPINK),
         Map.entry("lightsalmon", Color.LIGHTSALMON),
         Map.entry("lightseagreen", Color.LIGHTSEAGREEN),
         Map.entry("lightskyblue", Color.LIGHTSKYBLUE),
         Map.entry("lightslategray", Color.LIGHTSLATEGRAY),
         Map.entry("lightslategrey", Color.LIGHTSLATEGREY),
         Map.entry("lightsteelblue", Color.LIGHTSTEELBLUE),
         Map.entry("lightyellow", Color.LIGHTYELLOW),
         Map.entry("lime", Color.LIME),
         Map.entry("limegreen", Color.LIMEGREEN),
         Map.entry("linen", Color.LINEN),
         Map.entry("magenta", Color.MAGENTA),
         Map.entry("maroon", Color.MAROON),
         Map.entry("mediumaquamarine", Color.MEDIUMAQUAMARINE),
         Map.entry("mediumblue", Color.MEDIUMBLUE),
         Map.entry("mediumorchid", Color.MEDIUMORCHID),
         Map.entry("mediumpurple", Color.MEDIUMPURPLE),
         Map.entry("mediumseagreen", Color.MEDIUMSEAGREEN),
         Map.entry("mediumslateblue", Color.MEDIUMSLATEBLUE),
         Map.entry("mediumspringgreen", Color.MEDIUMSPRINGGREEN),
         Map.entry("mediumturquoise", Color.MEDIUMTURQUOISE),
         Map.entry("mediumvioletred", Color.MEDIUMVIOLETRED),
         Map.entry("midnightblue", Color.MIDNIGHTBLUE),
         Map.entry("mintcream", Color.MINTCREAM),
         Map.entry("mistyrose", Color.MISTYROSE),
         Map.entry("moccasin", Color.MOCCASIN),
         Map.entry("navajowhite", Color.NAVAJOWHITE),
         Map.entry("navy", Color.NAVY),
         Map.entry("oldlace", Color.OLDLACE),
         Map.entry("olive", Color.OLIVE),
         Map.entry("olivedrab", Color.OLIVEDRAB),
         Map.entry("orange", Color.ORANGE),
         Map.entry("orangered", Color.ORANGERED),
         Map.entry("orchid", Color.ORCHID),
         Map.entry("palegoldenrod", Color.PALEGOLDENROD),
         Map.entry("palegreen", Color.PALEGREEN),
         Map.entry("paleturquoise", Color.PALETURQUOISE),
         Map.entry("palevioletred", Color.PALEVIOLETRED),
         Map.entry("papayawhip", Color.PAPAYAWHIP),
         Map.entry("peachpuff", Color.PEACHPUFF),
         Map.entry("peru", Color.PERU),
         Map.entry("pink", Color.PINK),
         Map.entry("plum", Color.PLUM),
         Map.entry("powderblue", Color.POWDERBLUE),
         Map.entry("purple", Color.PURPLE),
         Map.entry("red", Color.RED),
         Map.entry("rosybrown", Color.ROSYBROWN),
         Map.entry("royalblue", Color.ROYALBLUE),
         Map.entry("saddlebrown", Color.SADDLEBROWN),
         Map.entry("salmon", Color.SALMON),
         Map.entry("sandybrown", Color.SANDYBROWN),
         Map.entry("seagreen", Color.SEAGREEN),
         Map.entry("seashell", Color.SEASHELL),
         Map.entry("sienna", Color.SIENNA),
         Map.entry("silver", Color.SILVER),
         Map.entry("skyblue", Color.SKYBLUE),
         Map.entry("slateblue", Color.SLATEBLUE),
         Map.entry("slategray", Color.SLATEGRAY),
         Map.entry("slategrey", Color.SLATEGREY),
         Map.entry("snow", Color.SNOW),
         Map.entry("springgreen", Color.SPRINGGREEN),
         Map.entry("steelblue", Color.STEELBLUE),
         Map.entry("tan", Color.TAN),
         Map.entry("teal", Color.TEAL),
         Map.entry("thistle", Color.THISTLE),
         Map.entry("tomato", Color.TOMATO),
         Map.entry("transparent", Color.TRANSPARENT),
         Map.entry("turquoise", Color.TURQUOISE),
         Map.entry("violet", Color.VIOLET),
         Map.entry("wheat", Color.WHEAT),
         Map.entry("white", Color.WHITE),
         Map.entry("whitesmoke", Color.WHITESMOKE),
         Map.entry("yellow", Color.YELLOW),
         Map.entry("yellowgreen", Color.YELLOWGREEN)
      );

      private static Color get(String var0) {
         return NAMED_COLORS.get(var0);
      }
   }
}
