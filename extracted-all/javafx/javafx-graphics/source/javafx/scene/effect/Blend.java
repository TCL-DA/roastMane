package javafx.scene.effect;

import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.util.Utils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;

public class Blend extends Effect {
   private ObjectProperty<BlendMode> mode;
   private DoubleProperty opacity;
   private ObjectProperty<Effect> bottomInput;
   private ObjectProperty<Effect> topInput;

   private static com.sun.scenario.effect.Blend.Mode toPGMode(BlendMode var0) {
      if (var0 == null) {
         return com.sun.scenario.effect.Blend.Mode.SRC_OVER;
      } else if (var0 == BlendMode.SRC_OVER) {
         return com.sun.scenario.effect.Blend.Mode.SRC_OVER;
      } else if (var0 == BlendMode.SRC_ATOP) {
         return com.sun.scenario.effect.Blend.Mode.SRC_ATOP;
      } else if (var0 == BlendMode.ADD) {
         return com.sun.scenario.effect.Blend.Mode.ADD;
      } else if (var0 == BlendMode.MULTIPLY) {
         return com.sun.scenario.effect.Blend.Mode.MULTIPLY;
      } else if (var0 == BlendMode.SCREEN) {
         return com.sun.scenario.effect.Blend.Mode.SCREEN;
      } else if (var0 == BlendMode.OVERLAY) {
         return com.sun.scenario.effect.Blend.Mode.OVERLAY;
      } else if (var0 == BlendMode.DARKEN) {
         return com.sun.scenario.effect.Blend.Mode.DARKEN;
      } else if (var0 == BlendMode.LIGHTEN) {
         return com.sun.scenario.effect.Blend.Mode.LIGHTEN;
      } else if (var0 == BlendMode.COLOR_DODGE) {
         return com.sun.scenario.effect.Blend.Mode.COLOR_DODGE;
      } else if (var0 == BlendMode.COLOR_BURN) {
         return com.sun.scenario.effect.Blend.Mode.COLOR_BURN;
      } else if (var0 == BlendMode.HARD_LIGHT) {
         return com.sun.scenario.effect.Blend.Mode.HARD_LIGHT;
      } else if (var0 == BlendMode.SOFT_LIGHT) {
         return com.sun.scenario.effect.Blend.Mode.SOFT_LIGHT;
      } else if (var0 == BlendMode.DIFFERENCE) {
         return com.sun.scenario.effect.Blend.Mode.DIFFERENCE;
      } else if (var0 == BlendMode.EXCLUSION) {
         return com.sun.scenario.effect.Blend.Mode.EXCLUSION;
      } else if (var0 == BlendMode.RED) {
         return com.sun.scenario.effect.Blend.Mode.RED;
      } else if (var0 == BlendMode.GREEN) {
         return com.sun.scenario.effect.Blend.Mode.GREEN;
      } else if (var0 == BlendMode.BLUE) {
         return com.sun.scenario.effect.Blend.Mode.BLUE;
      } else {
         throw new AssertionError("Unrecognized blend mode: {mode}");
      }
   }

   static com.sun.scenario.effect.Blend.Mode getToolkitMode(BlendMode var0) {
      return toPGMode(var0);
   }

   public Blend() {
   }

   public Blend(BlendMode var1) {
      this.setMode(var1);
   }

   public Blend(BlendMode var1, Effect var2, Effect var3) {
      this.setMode(var1);
      this.setBottomInput(var2);
      this.setTopInput(var3);
   }

   com.sun.scenario.effect.Blend createPeer() {
      return new com.sun.scenario.effect.Blend(
         toPGMode(BlendMode.SRC_OVER), com.sun.scenario.effect.Effect.DefaultInput, com.sun.scenario.effect.Effect.DefaultInput
      );
   }

   public final void setMode(BlendMode var1) {
      this.modeProperty().set(var1);
   }

   public final BlendMode getMode() {
      return this.mode == null ? BlendMode.SRC_OVER : (BlendMode)this.mode.get();
   }

   public final ObjectProperty<BlendMode> modeProperty() {
      if (this.mode == null) {
         this.mode = new ObjectPropertyBase<BlendMode>(BlendMode.SRC_OVER) {
            public void invalidated() {
               Blend.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Blend.this;
            }

            public String getName() {
               return "mode";
            }
         };
      }

      return this.mode;
   }

   public final void setOpacity(double var1) {
      this.opacityProperty().set(var1);
   }

   public final double getOpacity() {
      return this.opacity == null ? 1.0 : this.opacity.get();
   }

   public final DoubleProperty opacityProperty() {
      if (this.opacity == null) {
         this.opacity = new DoublePropertyBase(1.0) {
            public void invalidated() {
               Blend.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Blend.this;
            }

            public String getName() {
               return "opacity";
            }
         };
      }

      return this.opacity;
   }

   public final void setBottomInput(Effect var1) {
      this.bottomInputProperty().set(var1);
   }

   public final Effect getBottomInput() {
      return this.bottomInput == null ? null : (Effect)this.bottomInput.get();
   }

   public final ObjectProperty<Effect> bottomInputProperty() {
      if (this.bottomInput == null) {
         this.bottomInput = new Effect.EffectInputProperty("bottomInput");
      }

      return this.bottomInput;
   }

   public final void setTopInput(Effect var1) {
      this.topInputProperty().set(var1);
   }

   public final Effect getTopInput() {
      return this.topInput == null ? null : (Effect)this.topInput.get();
   }

   public final ObjectProperty<Effect> topInputProperty() {
      if (this.topInput == null) {
         this.topInput = new Effect.EffectInputProperty("topInput");
      }

      return this.topInput;
   }

   @Override
   boolean checkChainContains(Effect var1) {
      Effect var2 = this.getTopInput();
      Effect var3 = this.getBottomInput();
      if (var2 == var1 || var3 == var1) {
         return true;
      } else {
         return var2 != null && var2.checkChainContains(var1) ? true : var3 != null && var3.checkChainContains(var1);
      }
   }

   @Override
   void update() {
      Effect var1 = this.getBottomInput();
      Effect var2 = this.getTopInput();
      if (var2 != null) {
         var2.sync();
      }

      if (var1 != null) {
         var1.sync();
      }

      com.sun.scenario.effect.Blend var3 = (com.sun.scenario.effect.Blend)this.getPeer();
      var3.setTopInput(var2 == null ? null : var2.getPeer());
      var3.setBottomInput(var1 == null ? null : var1.getPeer());
      var3.setOpacity((float)Utils.clamp(0.0, this.getOpacity(), 1.0));
      var3.setMode(toPGMode(this.getMode()));
   }

   @Override
   BaseBounds getBounds(BaseBounds var1, BaseTransform var2, Node var3, BoundsAccessor var4) {
      RectBounds var5 = new RectBounds();
      RectBounds var6 = new RectBounds();
      BaseBounds var9 = getInputBounds(var6, var2, var3, var4, this.getBottomInput());
      BaseBounds var8 = getInputBounds(var5, var2, var3, var4, this.getTopInput());
      return var8.deriveWithUnion(var9);
   }

   @Override
   Effect copy() {
      return new Blend(this.getMode(), this.getBottomInput(), this.getTopInput());
   }
}
