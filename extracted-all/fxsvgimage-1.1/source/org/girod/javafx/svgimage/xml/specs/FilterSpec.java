package org.girod.javafx.svgimage.xml.specs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;

public class FilterSpec implements SVGTags {
   public static final short PREVIOUS_EFFECT = 0;
   public static final short NAMED_EFFECT = 1;
   public static final short SOURCE_GRAPHIC_EFFECT = 2;
   public static final short SOURCE_ALPHA_EFFECT = 3;
   private final List<FilterSpec.FilterEffect> effects = new ArrayList<>();
   private final Map<String, FilterSpec.FilterEffect> namedEffects = new HashMap<>();

   public void addEffect(String resultId, FilterSpec.FilterEffect effect) {
      this.effects.add(effect);
      if (resultId != null) {
         this.namedEffects.put(resultId, effect);
      }
   }

   public List<FilterSpec.FilterEffect> getEffects() {
      return this.effects;
   }

   public abstract static class AbstractFilterEffect implements FilterSpec.FilterEffect {
      protected final String resultId;
      protected String in = null;
      protected short inputType = 0;

      public AbstractFilterEffect(String resultId) {
         this.resultId = resultId;
      }

      @Override
      public short getInputType() {
         return this.inputType;
      }

      @Override
      public void setIn(String in) {
         this.in = in;
         if (in != null) {
            if (in.equals("SourceGraphic")) {
               this.inputType = 2;
            } else if (in.equals("SourceAlpha")) {
               this.inputType = 3;
            } else {
               this.inputType = 1;
            }
         }
      }

      @Override
      public String getResultId() {
         return this.resultId;
      }
   }

   public static class AppliedEffect {
      private final FilterSpec.FilterEffect effectSpec;
      private final Effect effect;

      public AppliedEffect(FilterSpec.FilterEffect effectSpec, Effect effect) {
         this.effectSpec = effectSpec;
         this.effect = effect;
      }

      public FilterSpec.FilterEffect getEffectSpec() {
         return this.effectSpec;
      }

      public String getResultId() {
         return this.effectSpec.getResultId();
      }

      public Effect getEffect() {
         return this.effect;
      }
   }

   public static class FEComposite extends FilterSpec.AbstractFilterEffect {
      public static final short OPERATOR_OVER = 0;
      public static final short OPERATOR_IN = 1;
      public static final short OPERATOR_OUT = 2;
      public static final short OPERATOR_ATOP = 3;
      public static final short OPERATOR_XOR = 4;
      public static final short OPERATOR_ARITHMETIC = 5;
      private short type = 0;
      private final String compIn;
      private final String compIn2;

      public FEComposite(String resultId, short type, String in, String in2) {
         super(resultId);
         this.type = type;
         this.compIn = in;
         this.compIn2 = in2;
      }

      public boolean shouldApply(List<FilterSpec.AppliedEffect> appliedEffects, int index) {
         boolean compInIsSourceGraphics = this.compIn != null && this.compIn.equals("SourceGraphic");
         if (!compInIsSourceGraphics) {
            return true;
         } else if (this.compIn2 != null && index != 0) {
            FilterSpec.AppliedEffect previousEffect = appliedEffects.get(index - 1);
            return !this.compIn2.equals(previousEffect.getResultId());
         } else {
            return false;
         }
      }

      public boolean isSecondLast() {
         return this.compIn != null && this.compIn.equals("SourceGraphic");
      }

      @Override
      public Effect getEffect(Node node) {
         switch (this.type) {
            case 0:
               return new Blend(BlendMode.SRC_OVER);
            case 1:
               return new Blend(BlendMode.OVERLAY);
            case 2:
               return new Blend(BlendMode.ADD);
            case 3:
               return new Blend(BlendMode.SRC_ATOP);
            case 4:
               return new Blend(BlendMode.EXCLUSION);
            case 5:
               return new Blend(BlendMode.SRC_OVER);
            default:
               return new Blend(BlendMode.SRC_OVER);
         }
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         Blend blend = (Blend)effect;
         if (this.compIn != null) {
            if (namedEffects.containsKey(this.compIn)) {
               blend.setTopInput(namedEffects.get(this.compIn));
            } else if (this.compIn.equals("SourceAlpha")) {
               blend.setTopInput(sourceAlpha);
            } else if (this.compIn.equals("SourceGraphic")) {
               blend.setTopInput(null);
            } else {
               blend.setTopInput(previousEffect);
            }
         } else {
            blend.setTopInput(previousEffect);
         }

         if (this.compIn2 != null) {
            if (namedEffects.containsKey(this.compIn2)) {
               blend.setBottomInput(namedEffects.get(this.compIn2));
            } else if (this.compIn2.equals("SourceAlpha")) {
               blend.setBottomInput(sourceAlpha);
            } else if (this.compIn2.equals("SourceGraphic")) {
               blend.setBottomInput(null);
            } else {
               blend.setBottomInput(previousEffect);
            }
         } else {
            blend.setBottomInput(previousEffect);
         }
      }
   }

   public static class FEDiffuseLighting extends FilterSpec.AbstractFilterEffect {
      public final double diffuseConstant;
      public final Light light;

      public FEDiffuseLighting(String resultId, double diffuseConstant, Light light) {
         super(resultId);
         this.diffuseConstant = diffuseConstant;
         this.light = light;
      }

      @Override
      public Effect getEffect(Node node) {
         Lighting lighting = new Lighting(this.light);
         lighting.setDiffuseConstant(this.diffuseConstant);
         lighting.setSpecularConstant(0.0);
         lighting.setSpecularExponent(0.0);
         lighting.setSurfaceScale(0.0);
         return lighting;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (this.inputType == 1 && namedEffects.containsKey(this.in)) {
            Effect _effect = namedEffects.get(this.in);
            ((Lighting)effect).setContentInput(_effect);
         } else if (this.inputType == 0) {
            ((Lighting)effect).setContentInput(previousEffect);
         } else if (this.inputType == 3) {
            ((Lighting)effect).setContentInput(sourceAlpha);
         }
      }
   }

   public static class FEDropShadow extends FilterSpec.AbstractFilterEffect {
      public final double dx;
      public final double dy;
      public final double stdDeviation;
      public final Color floodColor;

      public FEDropShadow(String resultId, double dx, double dy, double stdDeviation, Color floodColor) {
         super(resultId);
         this.dx = dx;
         this.dy = dy;
         this.stdDeviation = stdDeviation;
         this.floodColor = floodColor;
      }

      @Override
      public Effect getEffect(Node node) {
         return new DropShadow(this.stdDeviation, this.dx, this.dy, this.floodColor);
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (this.inputType == 1 && namedEffects.containsKey(this.in)) {
            Effect _effect = namedEffects.get(this.in);
            ((DropShadow)effect).setInput(_effect);
         } else if (this.inputType == 0) {
            ((DropShadow)effect).setInput(previousEffect);
         } else if (this.inputType == 3) {
            ((GaussianBlur)effect).setInput(sourceAlpha);
         }
      }
   }

   public static class FEFlood extends FilterSpec.AbstractFilterEffect {
      public final double x;
      public final double y;
      public final double width;
      public final double height;
      public final Color color;

      public FEFlood(String resultId, double x, double y, double width, double height, Color color) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.color = color;
      }

      @Override
      public Effect getEffect(Node node) {
         return new ColorInput(this.x, this.y, this.width, this.height, this.color);
      }
   }

   public static class FEGaussianBlur extends FilterSpec.AbstractFilterEffect {
      public final double stdDeviation;

      public FEGaussianBlur(String resultId, double stdDeviation) {
         super(resultId);
         this.stdDeviation = stdDeviation * 2.0;
      }

      @Override
      public Effect getEffect(Node node) {
         return new GaussianBlur(this.stdDeviation);
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (this.inputType == 1 && namedEffects.containsKey(this.in)) {
            Effect _effect = namedEffects.get(this.in);
            ((GaussianBlur)effect).setInput(_effect);
         } else if (this.inputType == 0) {
            ((GaussianBlur)effect).setInput(previousEffect);
         } else if (this.inputType == 3) {
            ((GaussianBlur)effect).setInput(sourceAlpha);
         }
      }
   }

   public static class FEImage extends FilterSpec.AbstractFilterEffect {
      public final double x;
      public final double y;
      public final Image source;

      public FEImage(String resultId, double x, double y, Image source) {
         super(resultId);
         this.x = x;
         this.y = y;
         this.source = source;
      }

      @Override
      public Effect getEffect(Node node) {
         return new ImageInput(this.source, this.x, this.y);
      }
   }

   public static class FEMerge extends FilterSpec.AbstractFilterEffect {
      private final List<String> mergeNodes = new ArrayList<>();

      public FEMerge(String resultId) {
         super(resultId);
      }

      public void addMergeNode(String in) {
         this.mergeNodes.add(in);
      }

      @Override
      public Effect getEffect(Node node) {
         return new Blend(BlendMode.ADD);
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         Blend blendEffect = (Blend)effect;
         Blend mergedEffect = null;

         for (int i = 0; i < this.mergeNodes.size(); i++) {
            String _resultId = this.mergeNodes.get(i);
            if (_resultId != null && (namedEffects.containsKey(_resultId) || _resultId.equals("SourceGraphic"))) {
               Effect _effect = namedEffects.get(_resultId);
               if (mergedEffect == null) {
                  mergedEffect = new Blend(BlendMode.ADD);
                  mergedEffect.setTopInput(_effect);
               } else if (mergedEffect.getBottomInput() == null) {
                  mergedEffect.setBottomInput(_effect);
               } else {
                  Blend newMergedEffect = new Blend(BlendMode.ADD);
                  newMergedEffect.setTopInput(mergedEffect);
                  mergedEffect = newMergedEffect;
               }
            } else if (_resultId != null && _resultId.equals("SourceAlpha")) {
               if (mergedEffect == null) {
                  mergedEffect = new Blend(BlendMode.ADD);
                  mergedEffect.setTopInput(sourceAlpha);
               } else if (mergedEffect.getBottomInput() == null) {
                  mergedEffect.setBottomInput(sourceAlpha);
               } else {
                  Blend newMergedEffect = new Blend(BlendMode.ADD);
                  newMergedEffect.setTopInput(sourceAlpha);
                  mergedEffect = newMergedEffect;
               }
            }
         }

         if (mergedEffect != null) {
            Effect topEffect = mergedEffect.getTopInput();
            blendEffect.setTopInput(topEffect);
            Effect bottomEffect = mergedEffect.getBottomInput();
            blendEffect.setBottomInput(bottomEffect);
         }
      }
   }

   public static class FEOffset extends FilterSpec.AbstractFilterEffect {
      public final double dx;
      public final double dy;

      public FEOffset(String resultId, double dx, double dy) {
         super(resultId);
         this.dx = dx;
         this.dy = dy;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (this.inputType == 1 && namedEffects.containsKey(this.in)) {
            Effect _effect = namedEffects.get(this.in);
            ((PerspectiveTransform)effect).setInput(_effect);
         } else if (this.inputType == 0) {
            ((PerspectiveTransform)effect).setInput(previousEffect);
         } else if (this.inputType == 3) {
            ((PerspectiveTransform)effect).setInput(sourceAlpha);
         }
      }

      @Override
      public Effect getEffect(Node node) {
         double ulx = this.dx + node.getLayoutX();
         double uly = this.dy + node.getLayoutY();
         double urx = ulx + node.getBoundsInLocal().getWidth();
         double lly = uly + node.getBoundsInLocal().getHeight();
         double lrx = ulx + node.getBoundsInLocal().getWidth();
         return new PerspectiveTransform(ulx, uly, urx, uly, lrx, lly, ulx, lly);
      }
   }

   public static class FESpecularLighting extends FilterSpec.AbstractFilterEffect {
      public final double specularConstant;
      public final double specularExponent;
      public final double surfaceScale;
      public final Light light;

      public FESpecularLighting(String resultId, double specularConstant, double specularExponent, double surfaceScale, Light light) {
         super(resultId);
         this.specularConstant = specularConstant;
         this.specularExponent = specularExponent;
         this.surfaceScale = surfaceScale;
         this.light = light;
      }

      @Override
      public Effect getEffect(Node node) {
         Lighting lighting = new Lighting(this.light);
         lighting.setSpecularConstant(this.specularConstant);
         lighting.setSpecularExponent(this.specularExponent);
         lighting.setSurfaceScale(this.surfaceScale);
         return lighting;
      }

      @Override
      public void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
         if (this.inputType == 1 && namedEffects.containsKey(this.in)) {
            Effect _effect = namedEffects.get(this.in);
            ((Lighting)effect).setContentInput(_effect);
         } else if (this.inputType == 0) {
            ((Lighting)effect).setContentInput(previousEffect);
         } else if (this.inputType == 3) {
            ((Lighting)effect).setContentInput(sourceAlpha);
         }
      }
   }

   public interface FilterEffect {
      String getResultId();

      short getInputType();

      void setIn(String var1);

      Effect getEffect(Node var1);

      default void resolveEffect(Effect effect, Effect sourceAlpha, Effect previousEffect, Map<String, Effect> namedEffects) {
      }
   }
}
