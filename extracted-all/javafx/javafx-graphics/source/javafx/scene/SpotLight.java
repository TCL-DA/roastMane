package javafx.scene;

import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SpotLightHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSpotLight;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class SpotLight extends PointLight {
   private ObjectProperty<Point3D> direction;
   private static final Point3D DEFAULT_DIRECTION = NGSpotLight.getDefaultDirection();
   private DoubleProperty innerAngle;
   private static final double DEFAULT_INNER_ANGLE = (double)NGSpotLight.getDefaultInnerAngle();
   private DoubleProperty outerAngle;
   private static final double DEFAULT_OUTER_ANGLE = (double)NGSpotLight.getDefaultOuterAngle();
   private DoubleProperty falloff;
   private static final double DEFAULT_FALLOFF = (double)NGSpotLight.getDefaultFalloff();

   public SpotLight() {
      SpotLightHelper.initHelper(this);
   }

   public SpotLight(Color var1) {
      super(var1);
      SpotLightHelper.initHelper(this);
   }

   public final void setDirection(Point3D var1) {
      this.directionProperty().set(var1);
   }

   public final Point3D getDirection() {
      return this.direction == null ? DEFAULT_DIRECTION : (Point3D)this.direction.get();
   }

   public final ObjectProperty<Point3D> directionProperty() {
      if (this.direction == null) {
         this.direction = new SimpleObjectProperty<Point3D>(this, "direction", DEFAULT_DIRECTION) {
            protected void invalidated() {
               NodeHelper.markDirty(SpotLight.this, DirtyBits.NODE_LIGHT);
            }
         };
      }

      return this.direction;
   }

   public final void setInnerAngle(double var1) {
      this.innerAngleProperty().set(var1);
   }

   public final double getInnerAngle() {
      return this.innerAngle == null ? DEFAULT_INNER_ANGLE : this.innerAngle.get();
   }

   public final DoubleProperty innerAngleProperty() {
      if (this.innerAngle == null) {
         this.innerAngle = this.getLightDoubleProperty("innerAngle", DEFAULT_INNER_ANGLE);
      }

      return this.innerAngle;
   }

   public final void setOuterAngle(double var1) {
      this.outerAngleProperty().set(var1);
   }

   public final double getOuterAngle() {
      return this.outerAngle == null ? DEFAULT_OUTER_ANGLE : this.outerAngle.get();
   }

   public final DoubleProperty outerAngleProperty() {
      if (this.outerAngle == null) {
         this.outerAngle = this.getLightDoubleProperty("outerAngle", DEFAULT_OUTER_ANGLE);
      }

      return this.outerAngle;
   }

   public final void setFalloff(double var1) {
      this.falloffProperty().set(var1);
   }

   public final double getFalloff() {
      return this.falloff == null ? DEFAULT_FALLOFF : this.falloff.get();
   }

   public final DoubleProperty falloffProperty() {
      if (this.falloff == null) {
         this.falloff = this.getLightDoubleProperty("falloff", DEFAULT_FALLOFF);
      }

      return this.falloff;
   }

   private NGNode doCreatePeer() {
      return new NGSpotLight();
   }

   private void doUpdatePeer() {
      if (this.isDirty(DirtyBits.NODE_LIGHT)) {
         NGSpotLight var1 = this.getPeer();
         var1.setDirection(this.getDirection());
         var1.setInnerAngle((float)this.getInnerAngle());
         var1.setOuterAngle((float)this.getOuterAngle());
         var1.setFalloff((float)this.getFalloff());
      }
   }

   static {
      SpotLightHelper.setSpotLightAccessor(new SpotLightHelper.SpotLightAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((SpotLight)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((SpotLight)var1).doUpdatePeer();
         }
      });
   }
}
