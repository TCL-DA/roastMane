package org.girod.javafx.svgimage.xml.builders;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.xml.parsers.ClippingFactory;
import org.girod.javafx.svgimage.xml.parsers.LengthParser;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.ExtendedFontPosture;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.specs.Styles;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class SVGStyleBuilder implements SVGTags {
   private static final Pattern STYLES = Pattern.compile("\\.[a-zA-Z_][a-zA-Z0-9_\\-]*\\s*\\{[a-zA-Z0-9_\\-+\\.\\s,:\\#;]+\\}\\s*");
   private static final Pattern RULE_CONTENT = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_\\-]*\\s*:\\s*[a-zA-Z0-9_\\-+,\\.\\#\\.]*");

   private SVGStyleBuilder() {
   }

   public static Styles parseStyle(String content, Viewport viewport) {
      Matcher m = STYLES.matcher(content);
      Styles styles = null;

      while (m.find()) {
         if (styles == null) {
            styles = new Styles();
         }

         String theRule = m.group();
         int parIndex = theRule.indexOf(123);
         String styleClass = theRule.substring(1, parIndex).trim();
         Styles.Rule rule = new Styles.Rule(styleClass);
         boolean isEmpty = true;
         String ruleContent = theRule.substring(parIndex + 1, theRule.length() - 2).trim();
         Matcher m2 = RULE_CONTENT.matcher(ruleContent);

         while (m2.find()) {
            if (isEmpty) {
               styles.addRule(rule);
               isEmpty = false;
            }

            String theProperty = m2.group();
            int index = theProperty.indexOf(58);
            String key = theProperty.substring(0, index).trim();
            String value = theProperty.substring(index + 1, theProperty.length()).trim();
            switch (key) {
               case "fill": {
                  Color col = ParserUtils.getColor(value);
                  rule.addProperty(key, (short)0, col);
                  break;
               }
               case "stroke": {
                  Color col = ParserUtils.getColor(value);
                  rule.addProperty(key, (short)1, col);
                  break;
               }
               case "stroke-width":
                  double width = ParserUtils.parseLineWidth(value, viewport);
                  rule.addProperty(key, (short)2, width);
                  break;
               case "stroke-dasharray":
                  List<Double> list = ParserUtils.parseDashArray(value, viewport);
                  if (list != null) {
                     rule.addProperty(key, (short)3, list);
                  }
                  break;
               case "font-family":
                  String fontFamily = value.replace("'", "");
                  rule.addProperty(key, (short)7, fontFamily);
                  break;
               case "font-weight":
                  FontWeight fontWeight = SVGShapeBuilder.getFontWeight(value);
                  rule.addProperty(key, (short)5, fontWeight);
                  break;
               case "font-style":
                  ExtendedFontPosture fontPosture = SVGShapeBuilder.getExtendedFontPosture(value);
                  rule.addProperty(key, (short)6, fontPosture);
                  break;
               case "font-size":
                  double size = ParserUtils.parseFontSize(value);
                  rule.addProperty(key, (short)4, size);
                  break;
               case "text-decoration":
                  rule.addProperty(key, (short)8, value);
                  break;
               case "opacity":
                  double opacityx = ParserUtils.parseOpacity(value);
                  if (opacityx >= 0.0) {
                     rule.addProperty(key, (short)9, opacityx);
                  }
                  break;
               case "fill-opacity":
                  double opacity = ParserUtils.parseOpacity(value);
                  if (opacity >= 0.0) {
                     rule.addProperty(key, (short)10, opacity);
                  }
                  break;
               case "transform":
                  List<Transform> transformList = TransformUtils.extractTransforms(value, viewport);
                  if (!transformList.isEmpty()) {
                     rule.addProperty(key, (short)11, transformList);
                  }
            }
         }
      }

      return styles;
   }

   public static MarkerContext setNodeStyle(Node node, XMLNode xmlNode, LoaderContext context, Viewport viewport) {
      return setNodeStyle(null, node, xmlNode, context, viewport);
   }

   public static boolean hasFill(XMLNode xmlNode) {
      if (xmlNode.hasAttribute("fill")) {
         return true;
      } else if (xmlNode.hasAttribute("clip-path")) {
         return true;
      } else if (xmlNode.hasAttribute("style")) {
         String styles = xmlNode.getAttributeValue("style");
         StringTokenizer tokenizer = new StringTokenizer(styles, ";");

         while (tokenizer.hasMoreTokens()) {
            String style = tokenizer.nextToken();
            StringTokenizer tokenizer2 = new StringTokenizer(style, ":");
            String styleName = tokenizer2.nextToken().trim();
            String styleValue = null;
            if (tokenizer2.hasMoreTokens()) {
               styleValue = tokenizer2.nextToken().trim();
            }

            if (styleValue != null) {
               switch (styleName) {
                  case "clip-path":
                     return true;
                  case "fill":
                     return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static MarkerContext setNodeStyle(MarkerContext markerContext, Node node, XMLNode xmlNode, LoaderContext context, Viewport viewport) {
      MarkerContext markerContextR = null;
      Node contextNode = null;
      if (markerContext != null) {
         contextNode = markerContext.getContextNode();
      }

      if (node instanceof Shape) {
         Shape shape = (Shape)node;
         if (markerContext == null) {
            markerContextR = MarkerBuilder.createMarkerContext(xmlNode, context);
         }

         if (xmlNode.hasAttribute("fill")) {
            Paint fill = ParserUtils.expressPaint(contextNode, context.gradients, xmlNode.getAttributeValue("fill"));
            shape.setFill(fill);
            if (markerContextR != null) {
               markerContextR.setContextFill(fill);
            }
         }

         if (xmlNode.hasAttribute("stroke")) {
            Paint stroke = ParserUtils.expressPaint(contextNode, context.gradients, xmlNode.getAttributeValue("stroke"));
            shape.setStroke(stroke);
            if (markerContextR != null) {
               markerContextR.setContextStroke(stroke);
            }
         }

         if (xmlNode.hasAttribute("stroke-width")) {
            double strokeWidth = xmlNode.getLineWidthValue("stroke-width", viewport, 1.0);
            shape.setStrokeWidth(strokeWidth);
         }

         if (xmlNode.hasAttribute("stroke-dasharray")) {
            String dashArray = xmlNode.getAttributeValue("stroke-dasharray");
            applyDash(shape, dashArray, viewport);
         }

         if (xmlNode.hasAttribute("stroke-dashoffset")) {
            String dashOffset = xmlNode.getAttributeValue("stroke-dashoffset");
            double offset = LengthParser.parseLength(dashOffset, viewport);
            shape.setStrokeDashOffset(offset);
         }

         if (xmlNode.hasAttribute("stroke-linejoin")) {
            String lineJoin = xmlNode.getAttributeValue("stroke-linejoin");
            applyLineJoin(shape, lineJoin);
         }

         if (xmlNode.hasAttribute("stroke-linecap")) {
            String lineCap = xmlNode.getAttributeValue("stroke-linecap");
            applyLineCap(shape, lineCap);
         }

         if (xmlNode.hasAttribute("stroke-miterlimit")) {
            String miterLimit = xmlNode.getAttributeValue("stroke-miterlimit");
            applyMiterLimit(shape, miterLimit, viewport);
         }
      }

      if (xmlNode.hasAttribute("class")) {
         String styleClasses = xmlNode.getAttributeValue("class");
         setStyleClass(node, styleClasses, context.svgStyle);
      }

      if (xmlNode.hasAttribute("clip-path") && context.clippingFactory != null) {
         String content = xmlNode.getAttributeValue("clip-path");
         setClipPath(node, content, context.clippingFactory, viewport);
      }

      if (xmlNode.hasAttribute("style")) {
         FontWeight fontWeight = FontWeight.NORMAL;
         FontPosture fontPosture = FontPosture.REGULAR;
         double fontSize = 12.0;
         String fontFamily = null;
         String styles = xmlNode.getAttributeValue("style");
         StringTokenizer tokenizer = new StringTokenizer(styles, ";");

         while (tokenizer.hasMoreTokens()) {
            String style = tokenizer.nextToken();
            StringTokenizer tokenizer2 = new StringTokenizer(style, ":");
            String styleName = tokenizer2.nextToken().trim();
            String styleValue = null;
            if (tokenizer2.hasMoreTokens()) {
               styleValue = tokenizer2.nextToken().trim();
            }

            if (styleValue != null) {
               switch (styleName) {
                  case "clip-path":
                     setClipPath(node, styleValue, context.clippingFactory, viewport);
                     break;
                  case "font-family":
                     if (node instanceof Text) {
                        fontFamily = styleValue.replace("'", "");
                     }
                     break;
                  case "font-weight":
                     if (node instanceof Text) {
                        fontWeight = SVGShapeBuilder.getFontWeight(styleValue);
                     }
                     break;
                  case "text-decoration":
                     if (node instanceof Text) {
                        SVGShapeBuilder.applyTextDecoration((Text)node, styleValue);
                     }
                     break;
                  case "font-style":
                     if (node instanceof Text) {
                        fontPosture = SVGShapeBuilder.applyFontPosture((Text)node, styleValue);
                     }
                     break;
                  case "font-size":
                     if (node instanceof Text) {
                        fontSize = ParserUtils.parseFontSize(styleValue);
                        fontSize = viewport.scaleLength(fontSize);
                     }
                     break;
                  case "marker-start":
                     String idxx = ParserUtils.getURL(styleValue);
                     if (markerContextR != null && context.hasMarker(idxx)) {
                        MarkerSpec marker = context.getMarker(idxx);
                        markerContextR.setMarkerStart(marker);
                     }
                     break;
                  case "marker-mid":
                     String idx = ParserUtils.getURL(styleValue);
                     if (markerContextR != null && context.hasMarker(idx)) {
                        MarkerSpec marker = context.getMarker(idx);
                        markerContextR.setMarkerMid(marker);
                     }
                     break;
                  case "marker-end":
                     String id = ParserUtils.getURL(styleValue);
                     if (markerContextR != null && context.hasMarker(id)) {
                        MarkerSpec marker = context.getMarker(id);
                        markerContextR.setMarkerEnd(marker);
                     }
                     break;
                  case "fill":
                     if (node instanceof Shape) {
                        Paint fill = ParserUtils.expressPaint(contextNode, context.gradients, styleValue);
                        ((Shape)node).setFill(fill);
                        if (markerContextR != null) {
                           markerContextR.setContextFill(fill);
                        }
                     }
                     break;
                  case "stroke":
                     if (node instanceof Shape) {
                        Paint stroke = ParserUtils.expressPaint(contextNode, context.gradients, styleValue);
                        ((Shape)node).setStroke(stroke);
                        if (markerContextR != null) {
                           markerContextR.setContextStroke(stroke);
                        }
                     }
                     break;
                  case "stroke-width":
                     if (node instanceof Shape) {
                        double strokeWidth = LengthParser.parseLineWidth(styleValue, viewport);
                        ((Shape)node).setStrokeWidth(strokeWidth);
                     }
                     break;
                  case "stroke-dasharray":
                     if (node instanceof Shape) {
                        applyDash((Shape)node, styleValue, viewport);
                     }
                     break;
                  case "stroke-dashoffset":
                     if (node instanceof Shape) {
                        double offset = LengthParser.parseLength(styleValue, viewport);
                        ((Shape)node).setStrokeDashOffset(offset);
                     }
                     break;
                  case "stroke-linecap":
                     if (node instanceof Shape) {
                        applyLineCap((Shape)node, styleValue);
                     }
                     break;
                  case "stroke-miterlimit":
                     if (node instanceof Shape) {
                        applyMiterLimit((Shape)node, styleValue, viewport);
                     }
                     break;
                  case "stroke-linejoin":
                     if (node instanceof Shape) {
                        applyLineJoin((Shape)node, styleValue);
                     }
                     break;
                  case "opacity":
                     double opacity = ParserUtils.parseOpacity(styleValue);
                     if (opacity >= 0.0) {
                        node.setOpacity(opacity);
                     }
                     break;
                  case "fill-opacity":
                     if (node instanceof Shape) {
                        double fillOpacity = ParserUtils.parseOpacity(styleValue);
                        if (fillOpacity >= 0.0) {
                           ParserUtils.setFillOpacity(node, fillOpacity);
                        }
                     }
                     break;
                  case "transform":
                     List<Transform> transformList = TransformUtils.extractTransforms(styleValue, viewport);
                     if (transformList.isEmpty()) {
                        break;
                     }

                     ObservableList<Transform> nodeTransforms = node.getTransforms();

                     for (Transform theTransForm : transformList) {
                        nodeTransforms.add(theTransForm);
                     }
                     break;
                  case "filter":
                     if (context.effectsSupported) {
                        Effect effect = expressFilter(node, styleValue, context.filterSpecs);
                        if (effect != null) {
                           node.setEffect(effect);
                        }
                     }
               }
            }
         }

         if (node instanceof Text) {
            Font font = Font.font(fontFamily, fontWeight, fontPosture, fontSize);
            ((Text)node).setFont(font);
         }
      }

      return markerContextR;
   }

   private static void applyMiterLimit(Shape shape, String styleValue, Viewport viewport) {
      try {
         double miterLimit = viewport.scaleLength(Double.parseDouble(styleValue));
         shape.setStrokeMiterLimit(miterLimit);
      } catch (NumberFormatException var5) {
         GlobalConfig.getInstance().handleParsingError("MiterLimit " + styleValue + " is not a number");
      }
   }

   private static void applyLineCap(Shape shape, String styleValue) {
      StrokeLineCap linecap = StrokeLineCap.BUTT;
      if (styleValue.equals("round")) {
         linecap = StrokeLineCap.ROUND;
      } else if (styleValue.equals("square")) {
         linecap = StrokeLineCap.SQUARE;
      } else if (!styleValue.equals("butt")) {
         linecap = StrokeLineCap.BUTT;
      }

      shape.setStrokeLineCap(linecap);
   }

   private static void applyLineJoin(Shape shape, String styleValue) {
      StrokeLineJoin linejoin = StrokeLineJoin.MITER;
      if (styleValue.equals("bevel")) {
         linejoin = StrokeLineJoin.BEVEL;
      } else if (styleValue.equals("round")) {
         linejoin = StrokeLineJoin.ROUND;
      } else if (!styleValue.equals("miter")) {
         linejoin = StrokeLineJoin.MITER;
      }

      shape.setStrokeLineJoin(linejoin);
   }

   private static void applyDash(Shape shape, String styleValue, Viewport viewport) {
      ObservableList<Double> array = shape.getStrokeDashArray();
      List<Double> list = ParserUtils.parseDashArray(styleValue, viewport);
      if (list != null) {
         for (int i = 0; i < list.size(); i++) {
            array.add(list.get(i));
         }
      }
   }

   private static void setClipPath(Node node, String spec, ClippingFactory clippingFactory, Viewport viewport) {
      if (spec.startsWith("url(#")) {
         String clipID = ParserUtils.getURL(spec);
         if (clippingFactory.hasClip(clipID)) {
            Shape clipShape = clippingFactory.createClip(clipID, node, viewport);
            if (clipShape != null) {
               node.setClip(clipShape);
            }
         }
      }
   }

   private static void setStyleClass(Node node, String styleClasses, Styles svgStyle) {
      StringTokenizer tok = new StringTokenizer(styleClasses, " ");

      while (tok.hasMoreTokens()) {
         String styleClass = tok.nextToken();
         node.getStyleClass().add(styleClass);
         if (svgStyle != null && svgStyle.hasRule(styleClass)) {
            Styles.Rule rule = svgStyle.getRule(styleClass);
            rule.apply(node);
         }
      }
   }

   private static Effect expressFilter(Node node, String value, Map<String, FilterSpec> filterSpecs) {
      return ParserUtils.expressFilter(filterSpecs, node, value);
   }
}
