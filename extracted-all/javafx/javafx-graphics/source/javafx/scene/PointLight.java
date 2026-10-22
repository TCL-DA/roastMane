package javafx.scene;

import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.PointLightHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPointLight;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;

public class PointLight extends LightBase {
   private DoubleProperty maxRange;
   private static final double DEFAULT_MAX_RANGE = (double)NGPointLight.getDefaultMaxRange();
   private DoubleProperty constantAttenuation;
   private static final double DEFAULT_CONSTANT_ATTENUATION = (double)NGPointLight.getDefaultCa();
   private DoubleProperty linearAttenuation;
   private static final double DEFAULT_LINEAR_ATTENUATION = (double)NGPointLight.getDefaultLa();
   private DoubleProperty quadraticAttenuation;
   private static final double DEFAULT_QUADRATIC_ATTENUATION = (double)NGPointLight.getDefaultQa();

   public PointLight() {
      PointLightHelper.initHelper(this);
   }

   public PointLight(Color var1) {
      super(var1);
      PointLightHelper.initHelper(this);
   }

   public final void setMaxRange(double var1) {
      this.maxRangeProperty().set(var1);
   }

   public final double getMaxRange() {
      return this.maxRange == null ? DEFAULT_MAX_RANGE : this.maxRange.get();
   }

   public final DoubleProperty maxRangeProperty() {
      if (this.maxRange == null) {
         this.maxRange = this.getLightDoubleProperty("maxRange", DEFAULT_MAX_RANGE);
      }

      return this.maxRange;
   }

   public final void setConstantAttenuation(double var1) {
      this.constantAttenuationProperty().set(var1);
   }

   public final double getConstantAttenuation() {
      return this.constantAttenuation == null ? DEFAULT_CONSTANT_ATTENUATION : this.constantAttenuation.get();
   }

   public final DoubleProperty constantAttenuationProperty() {
      if (this.constantAttenuation == null) {
         this.constantAttenuation = this.getLightDoubleProperty("constantAttenuation", DEFAULT_CONSTANT_ATTENUATION);
      }

      return this.constantAttenuation;
   }

   public final void setLinearAttenuation(double var1) {
      this.linearAttenuationProperty().set(var1);
   }

   public final double getLinearAttenuation() {
      return this.linearAttenuation == null ? DEFAULT_LINEAR_ATTENUATION : this.linearAttenuation.get();
   }

   public final DoubleProperty linearAttenuationProperty() {
      if (this.linearAttenuation == null) {
         this.linearAttenuation = this.getLightDoubleProperty("linearAttenuation", DEFAULT_LINEAR_ATTENUATION);
      }

      return this.linearAttenuation;
   }

   public final void setQuadraticAttenuation(double var1) {
      this.quadraticAttenuationProperty().set(var1);
   }

   public final double getQuadraticAttenuation() {
      return this.quadraticAttenuation == null ? DEFAULT_QUADRATIC_ATTENUATION : this.quadraticAttenuation.get();
   }

   public final DoubleProperty quadraticAttenuationProperty() {
      if (this.quadraticAttenuation == null) {
         this.quadraticAttenuation = this.getLightDoubleProperty("quadraticAttenuation", DEFAULT_QUADRATIC_ATTENUATION);
      }

      return this.quadraticAttenuation;
   }

   private NGNode doCreatePeer() {
      return new NGPointLight();
   }

   private void doUpdatePeer() {
      if (this.isDirty(DirtyBits.NODE_LIGHT)) {
         NGPointLight var1 = this.getPeer();
         var1.setCa((float)this.getConstantAttenuation());
         var1.setLa((float)this.getLinearAttenuation());
         var1.setQa((float)this.getQuadraticAttenuation());
         var1.setMaxRange((float)this.getMaxRange());
      }
   }

   static {
      PointLightHelper.setPointLightAccessor(new PointLightHelper.PointLightAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((PointLight)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((PointLight)var1).doUpdatePeer();
         }
      });
   }
}
