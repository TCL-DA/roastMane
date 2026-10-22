package org.girod.javafx.svgimage.xml.specs;

import java.util.List;
import java.util.Map;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public class LinearGradientSpec extends GradientSpec {
   private LinearGradient gradient = null;

   public LinearGradientSpec(XMLNode node) {
      super(node);
   }

   public LinearGradientSpec(XMLNode node, String href) {
      super(node, href);
   }

   public void setLinearGradient(LinearGradient gradient) {
      this.gradient = gradient;
   }

   @Override
   public void resolve(Map<String, GradientSpec> gradients, Viewport viewport) {
      if (!this.isResolved) {
         LinearGradientSpec linearSpec = null;
         if (this.href != null && gradients.containsKey(this.href)) {
            GradientSpec spec = gradients.get(this.href);
            if (spec instanceof LinearGradientSpec) {
               linearSpec = (LinearGradientSpec)spec;
               linearSpec.resolve(gradients, viewport);
            }
         }

         double x1 = 0.0;
         double y1 = 0.0;
         double x2 = 1.0;
         double y2 = 0.0;
         CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
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
                  hasSpread = true;
                  break;
               case "x1":
                  x1 = this.getGradientPos(this.xmlNode, "x1");
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "y1":
                  y1 = this.getGradientPos(this.xmlNode, "y1");
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "x2":
                  x2 = this.getGradientPos(this.xmlNode, "x2");
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "y2":
                  y2 = this.getGradientPos(this.xmlNode, "y2");
                  isProportional = isProportional || ParserUtils.isPercent(this.xmlNode, attrname);
                  hasPos = true;
                  break;
               case "gradientTransform":
                  this.transformList = TransformUtils.extractTransforms(this.xmlNode.getAttributeValue(attrname), viewport);
            }
         }

         this.specStops = this.buildStops(this, this.xmlNode, "linearGradient");
         if (this.specStops.isEmpty() && linearSpec != null) {
            this.specStops = linearSpec.getStops();
         }

         if (this.transformList == null && linearSpec != null) {
            this.transformList = linearSpec.getTransformList();
         }

         if (!hasPos && linearSpec != null) {
            LinearGradient refGradient = linearSpec.gradient;
            x1 = refGradient.getStartX();
            y1 = refGradient.getStartY();
            x2 = refGradient.getEndX();
            y2 = refGradient.getEndY();
            isProportional = refGradient.isProportional();
         }

         if (!hasSpread && linearSpec != null) {
            LinearGradient refGradient = linearSpec.gradient;
            cycleMethod = refGradient.getCycleMethod();
         }

         if ((x1 != 0.0 || y1 != 0.0 || x2 != 0.0 || y2 != 0.0) && this.transformList != null && !this.transformList.isEmpty()) {
            Transform concatTransform = null;

            for (Transform theTransform : this.transformList) {
               if (concatTransform == null) {
                  concatTransform = theTransform;
               } else {
                  concatTransform = concatTransform.createConcatenation(theTransform);
               }
            }

            if (concatTransform != null) {
               double x1d = x1;
               double x2d = x2;
               x1 = x1 * concatTransform.getMxx() + y1 * concatTransform.getMxy() + concatTransform.getTx();
               y1 = x1d * concatTransform.getMyx() + y1 * concatTransform.getMyy() + concatTransform.getTy();
               x2 = x2 * concatTransform.getMxx() + y2 * concatTransform.getMxy() + concatTransform.getTx();
               y2 = x2d * concatTransform.getMyx() + y2 * concatTransform.getMyy() + concatTransform.getTy();
            }
         }

         List<Stop> stops = this.convertStops(this.specStops);
         this.gradient = new LinearGradient(x1, y1, x2, y2, isProportional, cycleMethod, stops);
         this.isResolved = true;
      }
   }

   public LinearGradient getLinearGradient() {
      return this.gradient;
   }

   @Override
   public Paint getPaint() {
      return this.gradient;
   }
}
