package javafx.scene.effect;

import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.util.Utils;
import com.sun.scenario.effect.PhongLighting;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;

public class Lighting extends Effect {
   private final Light defaultLight = new Light.Distant();
   private ObjectProperty<Light> light = new ObjectPropertyBase<Light>(new Light.Distant()) {
      public void invalidated() {
         Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
         Lighting.this.effectBoundsChanged();
      }

      public Object getBean() {
         return Lighting.this;
      }

      public String getName() {
         return "light";
      }
   };
   private final Lighting.LightChangeListener lightChangeListener = new Lighting.LightChangeListener();
   private ObjectProperty<Effect> bumpInput;
   private ObjectProperty<Effect> contentInput;
   private DoubleProperty diffuseConstant;
   private DoubleProperty specularConstant;
   private DoubleProperty specularExponent;
   private DoubleProperty surfaceScale;

   PhongLighting createPeer() {
      return new PhongLighting(this.getLightInternal().getPeer());
   }

   public Lighting() {
      Shadow var1 = new Shadow();
      var1.setRadius(10.0);
      this.setBumpInput(var1);
   }

   public Lighting(Light var1) {
      Shadow var2 = new Shadow();
      var2.setRadius(10.0);
      this.setBumpInput(var2);
      this.setLight(var1);
   }

   public final void setLight(Light var1) {
      this.lightProperty().set(var1);
   }

   public final Light getLight() {
      return (Light)this.light.get();
   }

   public final ObjectProperty<Light> lightProperty() {
      return this.light;
   }

   @Override
   Effect copy() {
      Lighting var1 = new Lighting(this.getLight());
      var1.setBumpInput(this.getBumpInput());
      var1.setContentInput(this.getContentInput());
      var1.setDiffuseConstant(this.getDiffuseConstant());
      var1.setSpecularConstant(this.getSpecularConstant());
      var1.setSpecularExponent(this.getSpecularExponent());
      var1.setSurfaceScale(this.getSurfaceScale());
      return var1;
   }

   public final void setBumpInput(Effect var1) {
      this.bumpInputProperty().set(var1);
   }

   public final Effect getBumpInput() {
      return this.bumpInput == null ? null : (Effect)this.bumpInput.get();
   }

   public final ObjectProperty<Effect> bumpInputProperty() {
      if (this.bumpInput == null) {
         this.bumpInput = new Effect.EffectInputProperty("bumpInput");
      }

      return this.bumpInput;
   }

   public final void setContentInput(Effect var1) {
      this.contentInputProperty().set(var1);
   }

   public final Effect getContentInput() {
      return this.contentInput == null ? null : (Effect)this.contentInput.get();
   }

   public final ObjectProperty<Effect> contentInputProperty() {
      if (this.contentInput == null) {
         this.contentInput = new Effect.EffectInputProperty("contentInput");
      }

      return this.contentInput;
   }

   @Override
   boolean checkChainContains(Effect var1) {
      Effect var2 = this.getBumpInput();
      Effect var3 = this.getContentInput();
      if (var3 == var1 || var2 == var1) {
         return true;
      } else {
         return var3 != null && var3.checkChainContains(var1) ? true : var2 != null && var2.checkChainContains(var1);
      }
   }

   public final void setDiffuseConstant(double var1) {
      this.diffuseConstantProperty().set(var1);
   }

   public final double getDiffuseConstant() {
      return this.diffuseConstant == null ? 1.0 : this.diffuseConstant.get();
   }

   public final DoubleProperty diffuseConstantProperty() {
      if (this.diffuseConstant == null) {
         this.diffuseConstant = new DoublePropertyBase(1.0) {
            public void invalidated() {
               Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Lighting.this;
            }

            public String getName() {
               return "diffuseConstant";
            }
         };
      }

      return this.diffuseConstant;
   }

   public final void setSpecularConstant(double var1) {
      this.specularConstantProperty().set(var1);
   }

   public final double getSpecularConstant() {
      return this.specularConstant == null ? 0.3 : this.specularConstant.get();
   }

   public final DoubleProperty specularConstantProperty() {
      if (this.specularConstant == null) {
         this.specularConstant = new DoublePropertyBase(0.3) {
            public void invalidated() {
               Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Lighting.this;
            }

            public String getName() {
               return "specularConstant";
            }
         };
      }

      return this.specularConstant;
   }

   public final void setSpecularExponent(double var1) {
      this.specularExponentProperty().set(var1);
   }

   public final double getSpecularExponent() {
      return this.specularExponent == null ? 20.0 : this.specularExponent.get();
   }

   public final DoubleProperty specularExponentProperty() {
      if (this.specularExponent == null) {
         this.specularExponent = new DoublePropertyBase(20.0) {
            public void invalidated() {
               Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Lighting.this;
            }

            public String getName() {
               return "specularExponent";
            }
         };
      }

      return this.specularExponent;
   }

   public final void setSurfaceScale(double var1) {
      this.surfaceScaleProperty().set(var1);
   }

   public final double getSurfaceScale() {
      return this.surfaceScale == null ? 1.5 : this.surfaceScale.get();
   }

   public final DoubleProperty surfaceScaleProperty() {
      if (this.surfaceScale == null) {
         this.surfaceScale = new DoublePropertyBase(1.5) {
            public void invalidated() {
               Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            }

            public Object getBean() {
               return Lighting.this;
            }

            public String getName() {
               return "surfaceScale";
            }
         };
      }

      return this.surfaceScale;
   }

   private Light getLightInternal() {
      Light var1 = this.getLight();
      return var1 == null ? this.defaultLight : var1;
   }

   @Override
   void update() {
      Effect var1 = this.getBumpInput();
      if (var1 != null) {
         var1.sync();
      }

      Effect var2 = this.getContentInput();
      if (var2 != null) {
         var2.sync();
      }

      PhongLighting var3 = (PhongLighting)this.getPeer();
      var3.setBumpInput(var1 == null ? null : var1.getPeer());
      var3.setContentInput(var2 == null ? null : var2.getPeer());
      var3.setDiffuseConstant((float)Utils.clamp(0.0, this.getDiffuseConstant(), 2.0));
      var3.setSpecularConstant((float)Utils.clamp(0.0, this.getSpecularConstant(), 2.0));
      var3.setSpecularExponent((float)Utils.clamp(0.0, this.getSpecularExponent(), 40.0));
      var3.setSurfaceScale((float)Utils.clamp(0.0, this.getSurfaceScale(), 10.0));
      this.lightChangeListener.register(this.getLight());
      this.getLightInternal().sync();
      var3.setLight(this.getLightInternal().getPeer());
   }

   @Override
   BaseBounds getBounds(BaseBounds var1, BaseTransform var2, Node var3, BoundsAccessor var4) {
      return getInputBounds(var1, var2, var3, var4, this.getContentInput());
   }

   private class LightChangeListener extends EffectChangeListener {
      Light light;

      public void register(Light var1) {
         this.light = var1;
         super.register(this.light == null ? null : this.light.effectDirtyProperty());
      }

      public void invalidated(Observable var1) {
         if (this.light.isEffectDirty()) {
            Lighting.this.markDirty(EffectDirtyBits.EFFECT_DIRTY);
            Lighting.this.effectBoundsChanged();
         }
      }
   }
}
