package org.girod.javafx.svgimage.xml.specs;

import java.util.List;
import java.util.Map;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PercentParser;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public class RadialGradientSpec extends GradientSpec {
   private RadialGradient gradient = null;

   public RadialGradientSpec(XMLNode node) {
      super(node);
   }

   public RadialGradientSpec(XMLNode node, String href) {
      super(node, href);
   }

   public void setRadialGradient(RadialGradient gradient) {
      this.gradient = gradient;
   }

   @Override
   public void resolve(Map<String, GradientSpec> gradients, Viewport viewport) {
      if (!this.isResolved) {
         RadialGradientSpec radialSpec = null;
         if (this.href != null && gradients.containsKey(this.href)) {
            GradientSpec spec = gradients.get(this.href);
            if (spec instanceof RadialGradientSpec) {
               radialSpec = (RadialGradientSpec)spec;
               radialSpec.resolve(gradients, viewport);
            }
         }

         Double fx = null;
         Double fy = null;
         Double cx = null;
         Double cy = null;
         Double r = null;
         CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
         this.isResolved = true;
         boolean hasPos = false;
         boolean hasSpread = false;
         boolean isProportional = false;

         for (String attrname : this.xmlNode.getAttributes().keySet()) {
            switch (attrname) {
               case "gradientUnits":
                  String gradientUnits = this.xmlNode.getAttributeValue(attrname);
                  if (!gradientUnits.equals("userSpaceOnUse")) {
                     return;
                  }
                  break;
               case "spreadMethod":
                  String methodS = this.xmlNode.getAttributeValue(attrname);
                  cycleMethod = this.getCycleMethod(methodS);
                  hasSpread = false;
                  break;
               case "fx":
                  fx = PercentParser.parseValue(this.xmlNode, attrname, true);
                  hasPos = true;
                  break;
               case "fy":
                  fy = PercentParser.parseValue(this.xmlNode, attrname, true);
                  hasPos = true;
                  break;
               case "cx":
                  cx = PercentParser.parseValue(this.xmlNode, attrname, true);
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "cy":
                  cy = PercentParser.parseValue(this.xmlNode, attrname, true);
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "r":
                  r = PercentParser.parseValue(this.xmlNode, attrname, true);
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "gradientTransform":
                  this.transformList = TransformUtils.extractTransforms(this.xmlNode.getAttributeValue(attrname), viewport);
            }
         }

         this.specStops = this.buildStops(this, this.xmlNode, "radialGradient");
         if (this.specStops.isEmpty() && radialSpec != null) {
            this.specStops = radialSpec.getStops();
         }

         if (this.transformList == null && radialSpec != null) {
            this.transformList = radialSpec.getTransformList();
         }

         if (!hasPos && radialSpec != null) {
            RadialGradient refGradient = radialSpec.gradient;
            fx = refGradient.getFocusAngle();
            fy = refGradient.getFocusDistance();
            cx = refGradient.getCenterX();
            cy = refGradient.getCenterY();
            r = refGradient.getRadius();
            isProportional = refGradient.isProportional();
         }

         if (!hasSpread && radialSpec != null) {
            RadialGradient refGradient = radialSpec.gradient;
            cycleMethod = refGradient.getCycleMethod();
         }

         if (cx != null && cy != null && r != null) {
            double fDistance = 0.0;
            double fAngle = 0.0;
            if (this.transformList != null && !this.transformList.isEmpty()) {
               Transform concatTransform = null;

               for (Transform theTransform : this.transformList) {
                  if (concatTransform == null) {
                     concatTransform = theTransform;
                  } else {
                     concatTransform = concatTransform.createConcatenation(theTransform);
                  }
               }

               if (concatTransform != null) {
                  double tempCx = cx;
                  double tempCy = cy;
                  double tempR = r;
                  cx = tempCx * concatTransform.getMxx() + tempCy * concatTransform.getMxy() + concatTransform.getTx();
                  cy = tempCx * concatTransform.getMyx() + tempCy * concatTransform.getMyy() + concatTransform.getTy();
                  r = Math.sqrt(
                     tempR * concatTransform.getMxx() * tempR * concatTransform.getMxx() + tempR * concatTransform.getMyx() * tempR * concatTransform.getMyx()
                  );
                  if (fx != null && fy != null) {
                     double tempFx = fx;
                     double tempFy = fy;
                     fx = tempFx * concatTransform.getMxx() + tempFy * concatTransform.getMxy() + concatTransform.getTx();
                     fy = tempFx * concatTransform.getMyx() + tempFy * concatTransform.getMyy() + concatTransform.getTy();
                  } else {
                     fAngle = Math.asin(concatTransform.getMyx()) * 180.0 / Math.PI;
                     fDistance = Math.sqrt((cx - tempCx) * (cx - tempCx) + (cy - tempCy) * (cy - tempCy));
                  }
               }
            }

            if (fx != null && fy != null) {
               fDistance = Math.sqrt((fx - cx) * (fx - cx) + (fy - cy) * (fy - cy)) / r;
               fAngle = Math.atan2(cy - fy, cx - fx) * 180.0 / Math.PI;
            }

            List<Stop> stops = this.convertStops(this.specStops);
            this.gradient = new RadialGradient(fAngle, fDistance, cx, cy, r, isProportional, cycleMethod, stops);
            this.isResolved = true;
         }
      }
   }

   public RadialGradient getRadialGradient() {
      return this.gradient;
   }

   @Override
   public Paint getPaint() {
      return this.gradient;
   }
}
