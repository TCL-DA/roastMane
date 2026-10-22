package org.girod.javafx.svgimage.xml.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class ParserUtils implements SVGTags {
   private static final Pattern ZERO = Pattern.compile("[\\-−+]?0+");
   private static final Pattern FONT_SIZE_PAT = Pattern.compile("(\\d+\\.?\\d*)([a-z]+)?");
   private static final Pattern URL_PAT = Pattern.compile("url\\('?([^']+)'?\\)");
   private static final Pattern IMG_URL = Pattern.compile("data:image/(\\w+);base64,([^ ]+)\\s*");

   private ParserUtils() {
   }

   public static Color getColor(String value) {
      if (value.equals("none")) {
         return null;
      } else {
         try {
            return Color.web(value);
         } catch (IllegalArgumentException var2) {
            GlobalConfig.getInstance().handleParsingError("Color " + value + " is illegal");
            return null;
         }
      }
   }

   public static Image getImage(URL url, String href, double width, double height) {
      Matcher m = IMG_URL.matcher(href);
      if (m.matches()) {
         String content = m.group(2);
         byte[] imgarray = Base64.getDecoder().decode(content);

         try (InputStream stream = new ByteArrayInputStream(imgarray)) {
            return new Image(stream, width, height, true, true);
         } catch (IOException var28) {
            GlobalConfig.getInstance().handleParsingError("Image base64 " + href + " is invalid");
            return null;
         }
      } else {
         URL imageUrl;
         try {
            imageUrl = new URL(href);
         } catch (MalformedURLException var26) {
            try {
               imageUrl = new URL(url, href);
            } catch (MalformedURLException var25) {
               GlobalConfig.getInstance().handleParsingError("URL " + href + " is not well formed");
               return null;
            }
         }

         return new Image(imageUrl.toString(), width, height, true, true);
      }
   }

   public static String getURL(String value) {
      Matcher m = URL_PAT.matcher(value);
      if (m.matches()) {
         value = m.group(1);
      }

      if (value.startsWith("#")) {
         value = value.substring(1);
      }

      return value;
   }

   public static Paint expressPaint(Node contextNode, Map<String, Paint> gradients, String value) {
      Paint paint = null;
      if (value.equals("context-stroke")) {
         if (contextNode instanceof Shape) {
            Shape shape = (Shape)contextNode;
            return shape.getStroke();
         }
      } else if (value.equals("context-fill")) {
         if (contextNode instanceof Shape) {
            Shape shape = (Shape)contextNode;
            return shape.getFill();
         }
      } else if (!value.equals("none")) {
         if (value.startsWith("url(")) {
            String id = getURL(value);
            paint = gradients.get(id);
         } else {
            paint = getColor(value);
         }
      }

      return paint;
   }

   public static Paint expressPaint(Map<String, Paint> gradients, String value) {
      return expressPaint(null, gradients, value);
   }

   public static FillRule getClipRule(XMLNode node) {
      if (node.hasAttribute("clip-rule")) {
         String value = node.getAttributeValue("clip-rule");
         if (value.equals("nonzero")) {
            return FillRule.NON_ZERO;
         } else {
            return value.equals("evenodd") ? FillRule.EVEN_ODD : null;
         }
      } else {
         return null;
      }
   }

   public static FillRule getFillRule(XMLNode node) {
      if (node.hasAttribute("fill-rule")) {
         String value = node.getAttributeValue("fill-rule");
         if (value.equals("nonzero")) {
            return FillRule.NON_ZERO;
         } else {
            return value.equals("evenodd") ? FillRule.EVEN_ODD : null;
         }
      } else {
         return null;
      }
   }

   public static List<Double> parseDashArray(String value, Viewport viewport) {
      if (value != null && !value.equals("none")) {
         List<Double> list = new ArrayList<>();
         StringTokenizer tokenizer = new StringTokenizer(value, " ,");

         while (tokenizer.hasMoreTokens()) {
            String dash = tokenizer.nextToken();
            list.add(parseLengthValue(dash, true, null, viewport));
         }

         return list;
      } else {
         return null;
      }
   }

   public static Color getColor(String value, double opacity) {
      try {
         return Color.web(value, opacity);
      } catch (IllegalArgumentException var4) {
         GlobalConfig.getInstance().handleParsingError("Color " + value + " is illegal");
         return null;
      }
   }

   public static boolean parseVisibility(String value) {
      return !value.equals("hidden");
   }

   public static double parseOpacity(String value) {
      boolean isPercent = false;
      if (value.endsWith("%")) {
         value = value.substring(0, value.length() - 1);
         isPercent = true;
      }

      try {
         double opacity = Double.parseDouble(value);
         if (isPercent) {
            opacity /= 100.0;
         }

         return opacity;
      } catch (NumberFormatException var4) {
         GlobalConfig.getInstance().handleParsingError("Opacity " + value + " is not a number");
         return -1.0;
      }
   }

   public static void setFillOpacity(Node node, double fillOpacity) {
      if (node instanceof Shape && fillOpacity < 1.0) {
         Shape shape = (Shape)node;
         Paint paint = shape.getFill();
         if (paint != null && paint instanceof Color) {
            Color fill = (Color)paint;
            fill = fill.deriveColor(0.0, 1.0, 1.0, fillOpacity);
            shape.setFill(fill);
         }
      }
   }

   public static String parseFirstArgument(String value) {
      StringTokenizer tok = new StringTokenizer(value, " ");
      return tok.nextToken().trim();
   }

   public static int parseIntProtected(String valueS) {
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0;
      } else {
         try {
            return Integer.parseInt(valueS);
         } catch (NumberFormatException var3) {
            GlobalConfig.getInstance().handleParsingError("Value " + valueS + " is not a number");
            return 0;
         }
      }
   }

   public static double parseDoubleProtected(String valueS) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0.0;
      } else {
         try {
            return Double.parseDouble(valueS);
         } catch (NumberFormatException var4) {
            GlobalConfig.getInstance().handleParsingError("Value " + valueS + " is not a number");
            return 0.0;
         }
      }
   }

   public static double parseFontSize(String valueS) {
      Matcher m = FONT_SIZE_PAT.matcher(valueS);
      if (m.matches()) {
         int groupCount = m.groupCount();
         if (groupCount == 1) {
            return Double.parseDouble(valueS);
         } else {
            String value1 = m.group(1);
            double size = Double.parseDouble(value1);
            String unit = m.group(2);
            return unit != null && unit.equals("px") ? size * 1.25 : size;
         }
      } else {
         return 12.0;
      }
   }

   public static double parsePositionValue(String valueS, boolean isWidth, Bounds bounds, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      return m.matches() ? 0.0 : LengthParser.parsePosition(valueS, isWidth, bounds, viewport);
   }

   public static double parseLengthValue(String valueS, boolean isWidth, Bounds bounds, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      return m.matches() ? 0.0 : LengthParser.parseLength(valueS, isWidth, bounds, viewport);
   }

   public static void parseLengthValue(List<Double> args, String value, boolean isWidth, Bounds bounds, Viewport viewport) {
      value = value.replace('−', '-');
      double d = LengthParser.parseLength(value, isWidth, bounds, viewport);
      args.add(d);
   }

   public static double parseLineWidth(String value, Viewport viewport) {
      value = value.replace('−', '-');
      return LengthParser.parseLineWidth(value, viewport);
   }

   public static Effect expressFilter(Map<String, FilterSpec> filterSpecs, Node node, String value) {
      Map<String, Effect> namedEffects = new HashMap<>();
      List<FilterSpec.AppliedEffect> appliedEffects = new ArrayList<>();
      Effect lastEffect = null;
      boolean useSourceAlpha = false;
      if (!value.equals("none")) {
         if (value.startsWith("url(")) {
            String id = getURL(value);
            if (filterSpecs.containsKey(id)) {
               FilterSpec spec = filterSpecs.get(id);
               List<FilterSpec.FilterEffect> effects = spec.getEffects();

               for (int i = 0; i < effects.size(); i++) {
                  FilterSpec.FilterEffect filterEffect = effects.get(i);
                  if (filterEffect instanceof FilterSpec.FEComposite) {
                     FilterSpec.FEComposite feComposite = (FilterSpec.FEComposite)filterEffect;
                     boolean toApply = feComposite.shouldApply(appliedEffects, i);
                     if (!toApply) {
                        continue;
                     }
                  }

                  if (filterEffect.getInputType() == 3) {
                     useSourceAlpha = true;
                  }

                  String resultId = filterEffect.getResultId();
                  lastEffect = filterEffect.getEffect(node);
                  appliedEffects.add(new FilterSpec.AppliedEffect(filterEffect, lastEffect));
                  if (resultId != null && lastEffect != null) {
                     namedEffects.put(resultId, lastEffect);
                  }
               }
            }
         }

         ColorAdjust sourceAlpha = null;
         if (useSourceAlpha) {
            sourceAlpha = new ColorAdjust();
            sourceAlpha.setBrightness(-1.0);
         }

         Effect previousEffect = null;

         for (FilterSpec.AppliedEffect appliedEffect : appliedEffects) {
            FilterSpec.FilterEffect spec = appliedEffect.getEffectSpec();
            Effect effect = appliedEffect.getEffect();
            spec.resolveEffect(effect, sourceAlpha, previousEffect, namedEffects);
            previousEffect = effect;
         }
      }

      return lastEffect;
   }

   public static void setBaselineShift(Text text, String value) {
      if (value.equals("sub")) {
         text.setTranslateY(text.getFont().getSize() * 0.3);
      } else if (value.equals("super")) {
         text.setTranslateY(text.getFont().getSize() * -0.3);
      } else {
         boolean isPercent = false;
         if (value.endsWith("%")) {
            isPercent = true;
            value = value.substring(0, value.length());
         }

         try {
            double shift = -Double.parseDouble(value);
            if (isPercent) {
               shift *= 100.0;
            }

            text.setTranslateY(text.getFont().getSize() * shift);
         } catch (NumberFormatException var5) {
            GlobalConfig.getInstance().handleParsingError("Value " + value + " is not a number");
         }
      }
   }

   public static boolean hasXPosition(XMLNode node) {
      return node.hasAttribute("x") || node.hasAttribute("dx");
   }

   public static Map<String, String> getStyles(XMLNode node) {
      Map<String, String> styles = new HashMap<>();
      if (node.hasAttribute("style")) {
         String styleValue = node.getAttributeValue("style");
         StringTokenizer tok = new StringTokenizer(styleValue, ";");

         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken().trim();
            if (!tk.isEmpty()) {
               int index = tk.indexOf(58);
               if (index != -1) {
                  String key = tk.substring(0, index);
                  if (index < tk.length() - 1) {
                     String value = tk.substring(index + 1);
                     styles.put(key, value);
                  }
               }
            }
         }
      }

      return styles;
   }

   public static String mergeStyles(Map<String, String> styles, XMLNode node) {
      Map<String, String> var8 = new HashMap<>(styles);
      if (node.hasAttribute("style")) {
         String styleValue = node.getAttributeValue("style");
         StringTokenizer tok = new StringTokenizer(styleValue, ";");

         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken().trim();
            if (!tk.isEmpty()) {
               int index = tk.indexOf(58);
               if (index != -1) {
                  String key = tk.substring(0, index);
                  if (index < tk.length() - 1) {
                     String value = tk.substring(index + 1);
                     var8.put(key, value);
                  }
               }
            }
         }
      }

      StringBuilder buf = new StringBuilder();

      for (Entry<String, String> entry : var8.entrySet()) {
         buf.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
      }

      return buf.toString();
   }

   public static void propagateStyleAttributes(XMLNode parentNode, XMLNode childNode) {
      if (!childNode.getName().equals("tspan")) {
         for (Entry<String, String> entry : parentNode.attributes.entrySet()) {
            String var4 = entry.getKey();
            switch (var4) {
               case "style":
               case "stroke":
               case "fill":
               case "stroke-width":
               case "class":
                  if (!childNode.hasAttribute(entry.getKey())) {
                     childNode.addAttribute(entry.getKey(), entry.getValue());
                  }
            }
         }
      }
   }

   public static boolean setVisibility(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute("visibility")) {
         String visibilityS = xmlNode.getAttributeValue("visibility");
         boolean visible = parseVisibility(visibilityS);
         node.setVisible(visible);
         return visible;
      } else {
         return true;
      }
   }

   public static void setOpacity(Node node, XMLNode xmlNode) {
      if (xmlNode.hasAttribute("opacity")) {
         String opacityS = xmlNode.getAttributeValue("opacity");
         double opacity = parseOpacity(opacityS);
         if (opacity >= 0.0) {
            node.setOpacity(opacity);
         }
      }

      if (xmlNode.hasAttribute("fill-opacity") && node instanceof Shape) {
         String fillOpacityS = xmlNode.getAttributeValue("fill-opacity");
         double fillOpacity = parseOpacity(fillOpacityS);
         if (fillOpacity >= 0.0) {
            setFillOpacity(node, fillOpacity);
         }
      }
   }

   public static Viewport parseViewport(XMLNode xmlNode) {
      double viewboxX = 0.0;
      double viewboxY = 0.0;
      double width = 0.0;
      double height = 0.0;
      double viewboxWidth = 0.0;
      double viewboxHeight = 0.0;
      boolean hasWidthAndHeight = false;
      if (xmlNode.hasAttribute("viewBox")) {
         String box = xmlNode.getAttributeValue("viewBox");
         StringTokenizer tok = new StringTokenizer(box, " ,");
         if (tok.countTokens() >= 4) {
            viewboxX = parseDoubleProtected(tok.nextToken());
            viewboxY = parseDoubleProtected(tok.nextToken());
            viewboxWidth = parseDoubleProtected(tok.nextToken());
            viewboxHeight = parseDoubleProtected(tok.nextToken());
         }
      }

      if (xmlNode.hasAttribute("width") && xmlNode.hasAttribute("height")) {
         width = xmlNode.getDoubleValue("width", 0.0);
         height = xmlNode.getDoubleValue("height", 0.0);
         if (isPercent(xmlNode, "width")) {
            width = viewboxWidth * width / 100.0;
         }

         if (isPercent(xmlNode, "height")) {
            height = viewboxHeight * height / 100.0;
         }

         hasWidthAndHeight = true;
      }

      Viewport theViewport;
      if (hasWidthAndHeight) {
         theViewport = new Viewport(width, height);
      } else {
         theViewport = new Viewport();
      }

      if (xmlNode.hasAttribute("preserveAspectRatio")) {
         boolean preserve = getPreserveAspectRatio(xmlNode.getAttributeValue("preserveAspectRatio"));
         theViewport.setPreserveAspectRatio(preserve);
      }

      theViewport.setViewbox(viewboxX, viewboxY, viewboxWidth, viewboxHeight);
      return theViewport;
   }

   public static boolean getPreserveAspectRatio(String value) {
      if (!value.contains(" ")) {
         return !value.equals("none");
      } else {
         StringTokenizer tok = new StringTokenizer(value, " ");
         return !tok.nextToken().equals("none");
      }
   }

   public static Viewbox parseViewbox(XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute("width") && xmlNode.hasAttribute("height")) {
         double width = xmlNode.getLengthValue("width", viewport, 0.0);
         double height = xmlNode.getLengthValue("height", viewport, 0.0);
         if (xmlNode.hasAttribute("viewBox")) {
            String box = xmlNode.getAttributeValue("viewBox");
            StringTokenizer tok = new StringTokenizer(box, " ,");
            if (tok.countTokens() >= 4) {
               double viewboxX = parseDoubleProtected(tok.nextToken());
               double viewboxY = parseDoubleProtected(tok.nextToken());
               double viewboxWidth = parseDoubleProtected(tok.nextToken());
               double viewboxHeight = parseDoubleProtected(tok.nextToken());
               Viewbox theViewbox = new Viewbox(width, height);
               theViewbox.setViewbox(viewboxX, viewboxY, viewboxWidth, viewboxHeight);
               return theViewbox;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static Viewbox parseMarkerViewbox(XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute("markerWidth") && xmlNode.hasAttribute("markerHeight")) {
         double width = xmlNode.getLengthValue("markerWidth", viewport, 0.0);
         double height = xmlNode.getLengthValue("markerHeight", viewport, 0.0);
         if (xmlNode.hasAttribute("viewBox")) {
            String box = xmlNode.getAttributeValue("viewBox");
            StringTokenizer tok = new StringTokenizer(box, " ,");
            if (tok.countTokens() >= 4) {
               double viewboxX = parseDoubleProtected(tok.nextToken());
               double viewboxY = parseDoubleProtected(tok.nextToken());
               double viewboxWidth = parseDoubleProtected(tok.nextToken());
               double viewboxHeight = parseDoubleProtected(tok.nextToken());
               Viewbox theViewbox = new Viewbox(width, height);
               theViewbox.setViewbox(viewboxX, viewboxY, viewboxWidth, viewboxHeight);
               return theViewbox;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean isPercent(XMLNode xmlNode, String attrname) {
      if (xmlNode.hasAttribute(attrname)) {
         String value = xmlNode.getAttributeValue(attrname);
         return value.endsWith("%");
      } else {
         return true;
      }
   }

   public static List<Node> createNodeList(Node node) {
      if (node == null) {
         return null;
      } else {
         List<Node> list = new ArrayList<>(1);
         list.add(node);
         return list;
      }
   }
}
