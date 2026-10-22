package org.girod.javafx.svgimage.xml.specs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;

public class Styles {
   public static final short FILL = 0;
   public static final short STROKE = 1;
   public static final short STROKE_WIDTH = 2;
   public static final short STROKE_DASHARRAY = 3;
   public static final short FONT_SIZE = 4;
   public static final short FONT_WEIGHT = 5;
   public static final short FONT_STYLE = 6;
   public static final short FONT_FAMILY = 7;
   public static final short TEXT_DECORATION = 8;
   public static final short OPACITY = 9;
   public static final short FILL_OPACITY = 10;
   public static final short TRANSFORM = 11;
   private final Map<String, Styles.Rule> rules = new HashMap<>();

   public void addRule(Styles.Rule rule) {
      this.rules.put(rule.getStyleClass(), rule);
   }

   public boolean hasRule(String styleClass) {
      return this.rules.containsKey(styleClass);
   }

   public Styles.Rule getRule(String styleClass) {
      return this.rules.get(styleClass);
   }

   public static class Property {
      public final short type;
      public final Object value;

      public Property(short type, Object value) {
         this.type = type;
         this.value = value;
      }
   }

   public static class Rule {
      private final String styleClass;
      private final Map<String, Styles.Property> properties = new HashMap<>();

      public Rule(String styleClass) {
         this.styleClass = styleClass;
      }

      public String getStyleClass() {
         return this.styleClass;
      }

      public void addProperty(String key, short type, Object value) {
         this.properties.put(key, new Styles.Property(type, value));
      }

      public Map<String, Styles.Property> getProperties() {
         return this.properties;
      }

      public void apply(Node node) {
         FontWeight fontWeight = FontWeight.NORMAL;
         ExtendedFontPosture fontPosture = new ExtendedFontPosture(FontPosture.REGULAR);
         double fontSize = 12.0;
         String fontFamily = null;
         boolean hasFontProperties = false;

         for (Styles.Property property : this.properties.values()) {
            Object value = property.value;
            switch (property.type) {
               case 0:
                  if (node instanceof Shape) {
                     ((Shape)node).setFill((Color)value);
                  }
                  break;
               case 1:
                  if (node instanceof Shape) {
                     ((Shape)node).setStroke((Color)value);
                  }
                  break;
               case 2:
                  if (node instanceof Shape) {
                     ((Shape)node).setStrokeWidth((Double)value);
                  }
                  break;
               case 3:
                  if (node instanceof Shape) {
                     ObservableList<Double> strokeArray = ((Shape)node).getStrokeDashArray();
                     List<Double> theArray = (List<Double>)value;
                     strokeArray.addAll(theArray);
                  }
                  break;
               case 4:
                  if (node instanceof Text) {
                     fontSize = (Double)value;
                     hasFontProperties = true;
                  }
                  break;
               case 5:
                  if (node instanceof Text) {
                     fontWeight = (FontWeight)value;
                     hasFontProperties = true;
                  }
                  break;
               case 6:
                  if (node instanceof Text) {
                     fontPosture = (ExtendedFontPosture)value;
                     hasFontProperties = true;
                  }
                  break;
               case 7:
                  if (node instanceof Text) {
                     fontFamily = ((String)value).replace("'", "");
                     hasFontProperties = true;
                  }
                  break;
               case 8:
                  if (node instanceof Text) {
                     SVGShapeBuilder.applyTextDecoration((Text)node, (String)value);
                  }
                  break;
               case 9:
                  if (node instanceof Shape) {
                     double opacity = (Double)value;
                     ((Shape)node).setOpacity(opacity);
                  }
                  break;
               case 10:
                  if (node instanceof Shape) {
                     double fillOpacity = (Double)value;
                     ParserUtils.setFillOpacity((Shape)node, fillOpacity);
                  }
                  break;
               case 11:
                  Transform transform = (Transform)value;
                  node.getTransforms().add(transform);
            }

            if (hasFontProperties && node instanceof Text) {
               Font font = Font.font(fontFamily, fontWeight, fontPosture.posture, fontSize);
               if (fontPosture.isOblique) {
                  SVGShapeBuilder.applyFontOblique((Text)node);
               }

               ((Text)node).setFont(font);
            }
         }
      }
   }
}
