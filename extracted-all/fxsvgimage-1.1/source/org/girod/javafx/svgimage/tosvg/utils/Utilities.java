package org.girod.javafx.svgimage.tosvg.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import org.girod.javafx.svgimage.tosvg.wrappers.BackgroundWrapper;
import org.girod.javafx.svgimage.tosvg.wrappers.BorderWrapper;

public class Utilities implements CSSProperties {
   private Utilities() {
   }

   public static String convertColor(Color color) {
      int red = (int)(255.0 * color.getRed());
      int green = (int)(255.0 * color.getGreen());
      int blue = (int)(255.0 * color.getBlue());
      return "rgb(" + red + "," + green + "," + blue + ")";
   }

   public static FontWeight getWeight(Font font) {
      StringTokenizer tok = new StringTokenizer(font.getStyle());

      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().toLowerCase();
         if (tk.equals("bold")) {
            return FontWeight.BOLD;
         }

         if (tk.equals("bolder")) {
            return FontWeight.EXTRA_BOLD;
         }

         if (tk.equals("lighter")) {
            return FontWeight.LIGHT;
         }
      }

      return FontWeight.NORMAL;
   }

   public static FontPosture getPosture(Font font) {
      StringTokenizer tok = new StringTokenizer(font.getStyle());

      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().toLowerCase();
         if (tk.equals("italic")) {
            return FontPosture.ITALIC;
         }
      }

      return FontPosture.REGULAR;
   }

   public static Set<String> extractPseudoClasses(Node node) {
      ObservableSet<PseudoClass> pseudoClasses = node.getPseudoClassStates();
      Set<String> set = new HashSet<>();
      Iterator<PseudoClass> it = pseudoClasses.iterator();

      while (it.hasNext()) {
         set.add(it.next().getPseudoClassName());
      }

      return set;
   }

   public static Map<String, CSSProperty> extractProperties(Node node) {
      Map<String, CSSProperty> props = new HashMap<>();

      for (CssMetaData<Node, ?> metaData : node.getCssMetaData()) {
         StyleableProperty<?> prop = metaData.getStyleableProperty(node);
         String propName = metaData.getProperty();
         Object value = prop.getValue();
         if (value != null) {
            props.put(propName, new CSSProperty(prop, value));
         }
      }

      return props;
   }

   public static List<BackgroundWrapper> getPaintList(Region region, Background background, NodeConverter converter, double opacity) {
      List<BackgroundWrapper> paints = new ArrayList<>();
      List<BackgroundFill> fills = background.getFills();
      List<BackgroundImage> images = background.getImages();
      if (!images.isEmpty()) {
         for (BackgroundImage image : images) {
            BackgroundSize size = image.getSize();
            double width = size.getWidth();
            double height = size.getHeight();
            if (size.isContain()) {
               width = region.getWidth();
               height = region.getHeight();
            } else if (size.isWidthAsPercentage() || size.isHeightAsPercentage()) {
               double fitWidth = region.getWidth();
               double fitHeight = region.getHeight();
               if (size.isWidthAsPercentage()) {
                  fitWidth = width * fitWidth;
               }

               if (size.isHeightAsPercentage()) {
                  fitHeight = height * fitHeight;
               }

               Image _image = image.getImage();
               if (fitWidth < 0.0) {
                  fitHeight = _image.getWidth() / _image.getHeight() * fitHeight;
               }

               if (fitHeight < 0.0) {
                  fitHeight = _image.getHeight() / _image.getWidth() * fitWidth;
               }

               width = fitWidth;
               height = fitHeight;
            }

            if (width > 0.0 && height > 0.0) {
               Image _image1 = image.getImage();
               ImagePattern paint = new ImagePattern(_image1);
               Rectangle2D rec = new Rectangle2D(0.0, 0.0, width, height);
               BackgroundWrapper wrapper = new BackgroundWrapper(paint);
               wrapper.setImagePosition(image.getPosition());
               double x = rec.getMinX();
               double y = rec.getMinY();
               BackgroundPosition position = image.getPosition();
               if (converter.getAllProperties().containsKey("-fx-background-position")) {
                  position = (BackgroundPosition)converter.getAllProperties().get("-fx-background-position");
               }

               if (position != null) {
                  if (position.getHorizontalPosition() == 0.5) {
                     x += (region.getWidth() - width) / 2.0;
                  }

                  if (position.getVerticalPosition() == 0.5) {
                     y += (region.getHeight() - height) / 2.0;
                  }
               }

               wrapper.setPosition(x, y);
               wrapper.setImagePosition(image.getPosition());
               wrapper.setSize(rec.getWidth(), rec.getHeight());
               paints.add(wrapper);
            }
         }
      }

      if (!fills.isEmpty()) {
         for (BackgroundFill fill : fills) {
            Paint paintx = fill.getFill();
            Insets insets = fill.getInsets();
            double xx = insets.getLeft();
            double yx = insets.getTop();
            double widthx = region.getWidth() - insets.getRight();
            double heightx = region.getHeight() - insets.getBottom();
            if (widthx > 0.0 && heightx > 0.0 && paintx != null) {
               BackgroundWrapper wrapperx = new BackgroundWrapper(paintx);
               CornerRadii radii = fill.getRadii();
               wrapperx.setRadii(radii);
               wrapperx.setPosition(xx, yx);
               wrapperx.setSize(widthx, heightx);
               paints.add(wrapperx);
            }
         }
      }

      return paints;
   }

   public static List<BorderWrapper> getPaintList(Region region, Border border) {
      List<BorderWrapper> borders = new ArrayList<>();
      List<BorderStroke> strokes = border.getStrokes();
      if (!strokes.isEmpty()) {
         for (BorderStroke stroke : strokes) {
            CornerRadii radii = stroke.getRadii();
            BorderWidths widths = stroke.getWidths();
            double width = (widths.getBottom() + widths.getLeft() + widths.getRight() + widths.getTop()) / 4.0;
            if (width != 0.0) {
               Paint paint = stroke.getBottomStroke();
               if (paint == null) {
                  paint = stroke.getLeftStroke();
               }

               if (paint == null) {
                  paint = stroke.getRightStroke();
               }

               if (paint == null) {
                  paint = stroke.getTopStroke();
               }

               if (paint != null) {
                  BorderWrapper wrapper = new BorderWrapper(paint, width);
                  BorderStrokeStyle strokeStyle = mergeBorderStyles(stroke);
                  if (strokeStyle != null) {
                     wrapper.setStrokeStyle(strokeStyle);
                  }

                  if (radii != null) {
                     wrapper.setRadii(radii);
                  }

                  borders.add(wrapper);
               }
            }
         }
      }

      return borders;
   }

   private static BorderStrokeStyle mergeBorderStyles(BorderStroke stroke) {
      BorderStrokeStyle botstyle = stroke.getBottomStyle();
      BorderStrokeStyle topstyle = stroke.getTopStyle();
      BorderStrokeStyle leftstyle = stroke.getLeftStyle();
      BorderStrokeStyle rightstyle = stroke.getRightStyle();
      if (botstyle.getDashArray().isEmpty() && topstyle.getDashArray().isEmpty() && leftstyle.getDashArray().isEmpty() && rightstyle.getDashArray().isEmpty()) {
         return null;
      } else if (!botstyle.getDashArray().isEmpty()) {
         return botstyle;
      } else if (!topstyle.getDashArray().isEmpty()) {
         return topstyle;
      } else if (!leftstyle.getDashArray().isEmpty()) {
         return leftstyle;
      } else {
         return !rightstyle.getDashArray().isEmpty() ? rightstyle : null;
      }
   }

   public static String getScale(Scale scale) {
      return "scale(" + scale.getX() + "," + scale.getY() + ")";
   }

   public static List<String> splitString(String string, int partitionSize) {
      List<String> parts = new ArrayList<>();
      int len = string.length();
      int i = 0;

      while (i < len) {
         parts.add(string.substring(i, Math.min(len, i + partitionSize)));
         i += partitionSize;
      }

      return parts;
   }

   public static String getRelativePath(File baseDir, File file, boolean strict) {
      File parent = file;
      String path = file.getAbsolutePath();
      boolean isUnderBaseDir = false;

      while (true) {
         parent = parent.getParentFile();
         if (parent == null) {
            if (!isUnderBaseDir && !strict) {
               String newPath = convertToRelativePath(baseDir, file);
               if (newPath != null) {
                  path = newPath;
               }
            }

            return path.replace('\\', '/');
         }

         if (parent.equals(baseDir)) {
            isUnderBaseDir = true;

            try {
               String path1 = baseDir.getCanonicalPath();
               String path2 = file.getCanonicalPath();
               int index = path2.lastIndexOf(path1);
               if (index != -1) {
                  String dirPath = path1.substring(index);
                  path = path2.substring(dirPath.length() + 1);
               }
            } catch (IOException var10) {
               System.err.println(var10.getMessage());
            }
         }
      }
   }

   private static String convertToRelativePath(File baseDir, File file) {
      StringBuilder relPathBuilder = null;
      String absPath = baseDir.getAbsolutePath();
      String relPath = file.getAbsolutePath();
      absPath = absPath.replaceAll("\\\\", "/");
      relPath = relPath.replaceAll("\\\\", "/");
      String[] absoluteDirs = absPath.split("/");
      String[] relativeDirs = relPath.split("/");
      int length = absoluteDirs.length < relativeDirs.length ? absoluteDirs.length : relativeDirs.length;
      int lastCommonRoot = -1;
      int index = 0;

      while (index < length && absoluteDirs[index].equals(relativeDirs[index])) {
         lastCommonRoot = index++;
      }

      if (lastCommonRoot != -1) {
         relPathBuilder = new StringBuilder();

         for (int var12 = lastCommonRoot + 1; var12 < absoluteDirs.length; var12++) {
            if (absoluteDirs[var12].length() > 0) {
               relPathBuilder.append("../");
            }
         }

         for (int var13 = lastCommonRoot + 1; var13 < relativeDirs.length - 1; var13++) {
            relPathBuilder.append(relativeDirs[var13]).append("/");
         }

         relPathBuilder.append(relativeDirs[relativeDirs.length - 1]);
      }

      return relPathBuilder == null ? null : relPathBuilder.toString();
   }
}
