package org.girod.javafx.svgimage.tosvg.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.girod.javafx.svgimage.tosvg.utils.CSSProperties;
import org.girod.javafx.svgimage.tosvg.utils.CSSProperty;
import org.girod.javafx.svgimage.tosvg.utils.NodeConverter;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public abstract class AbstractConverter implements CSSProperties, NodeConverter {
   protected ConverterDelegate delegate = null;
   private final Node node;
   protected final XMLNode xmlParent;
   protected Map<String, CSSProperty> cssProperties = new HashMap<>();
   protected Map<String, Object> properties = new HashMap<>();
   protected Map<String, Object> allProperties = new HashMap<>();

   public AbstractConverter(ConverterDelegate delegate, Node node, XMLNode xmlParent) {
      this.delegate = delegate;
      this.node = node;
      this.xmlParent = xmlParent;
      this.cssProperties = Utilities.extractProperties(node);
      this.extractSetProperties();
   }

   public Parent getParent() {
      return null;
   }

   private Transform inverseTransform(Transform tr) throws NonInvertibleTransformException {
      return tr.createInverse();
   }

   protected Transform getTransformFromAncestor(Node node) {
      Node ancestor = node.getScene().getRoot();
      Transform nodeFromScene = node.getLocalToSceneTransform();
      Transform ancestorFromScene = ancestor.getLocalToSceneTransform();

      try {
         Transform inverseAncestor = this.inverseTransform(ancestorFromScene);
         return nodeFromScene.createConcatenation(inverseAncestor);
      } catch (NonInvertibleTransformException var6) {
         return node.getLocalToParentTransform();
      }
   }

   private void extractSetProperties() {
      for (Entry<String, CSSProperty> entry : this.cssProperties.entrySet()) {
         String key = entry.getKey();
         CSSProperty prop = entry.getValue();
         this.allProperties.put(key, entry.getValue().getValue());
         if (prop.getStyleOrigin() != null) {
            this.properties.put(key, entry.getValue().getValue());
         }
      }
   }

   protected void setClip(StringBuilder buf, String clipID) {
      if (clipID != null) {
         buf.append("clip-path: url(#").append(clipID).append(");");
      }
   }

   @Override
   public Map<String, CSSProperty> getCSSProperties() {
      return this.cssProperties;
   }

   @Override
   public Map<String, Object> getAllProperties() {
      return this.allProperties;
   }

   @Override
   public Map<String, Object> getProperties() {
      return this.properties;
   }

   private boolean toApply(Transform tr) {
      boolean toApply = true;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate)tr;
         if (rotate.getAngle() == 0.0) {
            toApply = false;
         }
      } else if (tr instanceof Scale) {
         Scale scale = (Scale)tr;
         if (scale.getMxx() == 1.0 && scale.getMyy() == 1.0) {
            toApply = false;
         }
      } else if (tr instanceof Translate) {
         Translate translate = (Translate)tr;
         if (translate.getTx() == 0.0 && translate.getTy() == 0.0) {
            toApply = false;
         }
      } else if (tr.isIdentity()) {
         toApply = false;
      }

      return toApply;
   }

   public void applyTransforms(XMLNode xmlNode) {
      StringBuilder buf = new StringBuilder();
      Transform conTransform = null;

      for (Transform tr : this.node.getTransforms()) {
         boolean toApply = this.toApply(tr);
         if (toApply) {
            if (conTransform == null) {
               conTransform = tr;
            } else {
               conTransform = conTransform.createConcatenation(tr);
            }
         }
      }

      Affine affine = null;
      Translate translate = null;
      Scale scale = null;
      Rotate rotate = null;
      Shear shear = null;
      if (conTransform != null) {
         if (conTransform instanceof Affine) {
            affine = (Affine)conTransform;
            double a = affine.getMxx();
            double b = affine.getMyx();
            double c = affine.getMxy();
            double d = affine.getMyy();
            double e = affine.getTx();
            double f = affine.getTy();
            buf.append("matrix(")
               .append(a)
               .append(" ")
               .append(b)
               .append(" ")
               .append(c)
               .append(" ")
               .append(d)
               .append(" ")
               .append(e)
               .append(" ")
               .append(f)
               .append(")");
         } else if (conTransform instanceof Translate) {
            translate = (Translate)conTransform;
         } else if (conTransform instanceof Scale) {
            scale = (Scale)conTransform;
         } else if (conTransform instanceof Rotate) {
            rotate = (Rotate)conTransform;
         } else if (conTransform instanceof Shear) {
            shear = (Shear)conTransform;
         }
      }

      boolean hasPrevious = false;
      if (affine != null) {
         double a = affine.getMxx();
         double b = affine.getMyx();
         double c = affine.getMxy();
         double d = affine.getMyy();
         double e = affine.getTx();
         double f = affine.getTy();
         hasPrevious = true;
         buf.append("matrix(")
            .append(a)
            .append(" ")
            .append(b)
            .append(" ")
            .append(c)
            .append(" ")
            .append(d)
            .append(" ")
            .append(e)
            .append(" ")
            .append(f)
            .append(")");
      }

      double translateX = this.node.getTranslateX() + this.node.getLayoutX();
      double translateY = this.node.getTranslateY() + this.node.getLayoutY();
      if (translateX != 0.0 || translateY != 0.0) {
         if (translate != null) {
            translate.setX(translate.getTx() + translateX);
            translate.setY(translate.getTy() + translateY);
         } else {
            translate = new Translate(translateX, translateY);
         }
      }

      if (translate != null) {
         double tx = translate.getTx();
         double ty = translate.getTy();
         if (hasPrevious) {
            buf.append(" ");
         }

         hasPrevious = true;
         buf.append("translate(").append(tx).append(" ").append(ty).append(")");
      }

      double scaleX = this.node.getScaleX();
      double scaleY = this.node.getScaleY();
      if (scaleX != 1.0 || scaleY != 1.0) {
         if (scale != null) {
            scale.setX(scale.getX() + scaleX);
            scale.setY(scale.getY() + scaleY);
         } else {
            scale = new Scale(scaleX, scaleY);
         }
      }

      if (scale != null) {
         scaleX = scale.getX();
         scaleY = scale.getY();
         if (hasPrevious) {
            buf.append(" ");
         }

         hasPrevious = true;
         buf.append("scale(").append(scaleX).append(" ").append(scaleY).append(")");
      }

      double rotation = this.node.getRotate();
      if (rotation != 0.0) {
         if (rotate != null) {
            rotate.setAngle(rotate.getAngle() + rotation);
         } else {
            Bounds bounds = this.node.getLayoutBounds();
            double centerX = (bounds.getMaxX() - bounds.getMinX()) / 2.0 + bounds.getMinX();
            double centerY = (bounds.getMaxY() - bounds.getMinY()) / 2.0 + bounds.getMinY();
            double angle = Math.toRadians(rotation);
            rotate = new Rotate();
            rotate.setAngle(angle);
            rotate.setPivotX(centerX);
            rotate.setPivotY(centerY);
         }
      }

      if (rotate != null) {
         double centerX = rotate.getPivotX();
         double centerY = rotate.getPivotY();
         double angle = rotate.getAngle();
         if (hasPrevious) {
            buf.append(" ");
         }

         hasPrevious = true;
         buf.append("rotate(").append(angle).append(" ").append(centerX).append(" ").append(centerY).append(")");
      }

      if (hasPrevious) {
         xmlNode.addAttribute("transform", buf.toString());
      }
   }

   public boolean hasVisibility() {
      return this.properties.containsKey("-visibility");
   }

   public boolean isVisible() {
      return this.properties.containsKey("-visibility") ? (Boolean)this.properties.get("-visibility") : true;
   }

   protected double getOpacity() {
      if (this.properties.containsKey("-fx-opacity")) {
         Number opacity = (Number)this.properties.get("-fx-opacity");
         return opacity.doubleValue();
      } else {
         return this.node.getOpacity();
      }
   }

   protected void addStroke(Paint paint, StringBuilder buf) {
      if (paint instanceof Color) {
         Color color = (Color)paint;
         String colS = Utilities.convertColor(color);
         buf.append("stroke:").append(colS).append(";");
      } else {
         buf.append("stroke:none;");
      }
   }

   protected void addFill(Paint paint, StringBuilder buf) {
      if (paint instanceof Color) {
         Color color = (Color)paint;
         String colS = Utilities.convertColor(color);
         buf.append("fill:").append(colS).append(";");
      } else {
         buf.append("fill:none;");
      }
   }

   protected void setStrokeOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color)paint;
         if (color.getOpacity() != 1.0) {
            node.addAttribute("stroke-opacity", color.getOpacity());
         }
      }
   }

   protected void setFillOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color)paint;
         if (color.getOpacity() != 1.0) {
            node.addAttribute("fill-opacity", color.getOpacity());
         }
      }
   }

   public Node getAdditionalNode() {
      return null;
   }
}
