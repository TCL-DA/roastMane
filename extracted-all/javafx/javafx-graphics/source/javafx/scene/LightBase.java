package javafx.scene;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.LightBaseHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.tk.Toolkit;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;

public abstract class LightBase extends Node {
   private Affine3D localToSceneTx = new Affine3D();
   private ObjectProperty<Color> color;
   private BooleanProperty lightOn;
   private ObservableList<Node> scope;
   private ObservableList<Node> exclusionScope;

   protected LightBase() {
      this(Color.WHITE);
   }

   protected LightBase(Color var1) {
      LightBaseHelper.initHelper(this);
      if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
         String var2 = LightBase.class.getName();
         PlatformLogger.getLogger(var2).warning("System can't support ConditionalFeature.SCENE3D");
      }

      this.setColor(var1);
      this.localToSceneTransformProperty().addListener(var1x -> NodeHelper.markDirty(this, DirtyBits.NODE_LIGHT_TRANSFORM));
   }

   public final void setColor(Color var1) {
      this.colorProperty().set(var1);
   }

   public final Color getColor() {
      return this.color == null ? null : (Color)this.color.get();
   }

   public final ObjectProperty<Color> colorProperty() {
      if (this.color == null) {
         this.color = new SimpleObjectProperty<Color>(this, "color") {
            protected void invalidated() {
               NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
            }
         };
      }

      return this.color;
   }

   public final void setLightOn(boolean var1) {
      this.lightOnProperty().set(var1);
   }

   public final boolean isLightOn() {
      return this.lightOn == null ? true : this.lightOn.get();
   }

   public final BooleanProperty lightOnProperty() {
      if (this.lightOn == null) {
         this.lightOn = new SimpleBooleanProperty(this, "lightOn", true) {
            protected void invalidated() {
               NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
            }
         };
      }

      return this.lightOn;
   }

   public ObservableList<Node> getScope() {
      if (this.scope == null) {
         this.scope = new TrackableObservableList<Node>() {
            protected void onChanged(Change<Node> var1) {
               LightBase.this.doOnChanged(var1, LightBase.this.exclusionScope);
            }
         };
      }

      return this.scope;
   }

   public ObservableList<Node> getExclusionScope() {
      if (this.exclusionScope == null) {
         this.exclusionScope = new TrackableObservableList<Node>() {
            protected void onChanged(Change<Node> var1) {
               LightBase.this.doOnChanged(var1, LightBase.this.scope);
            }
         };
      }

      return this.exclusionScope;
   }

   private void doOnChanged(Change<Node> var1, ObservableList<Node> var2) {
      NodeHelper.markDirty(this, DirtyBits.NODE_LIGHT_SCOPE);

      while (var1.next()) {
         var1.getRemoved().forEach(this::markChildrenDirty);
         var1.getAddedSubList().forEach(var2x -> {
            if (var2 == null || !var2.remove(var2x)) {
               this.markChildrenDirty(var2x);
            }
         });
      }
   }

   @Override
   void scenesChanged(Scene var1, SubScene var2, Scene var3, SubScene var4) {
      if (var4 != null) {
         var4.removeLight(this);
      } else if (var3 != null) {
         var3.removeLight(this);
      }

      if (var2 != null) {
         var2.addLight(this);
      } else if (var1 != null) {
         var1.addLight(this);
      }
   }

   DoubleProperty getLightDoubleProperty(String var1, double var2) {
      return new SimpleDoubleProperty(this, var1, var2) {
         protected void invalidated() {
            NodeHelper.markDirty(LightBase.this, DirtyBits.NODE_LIGHT);
         }
      };
   }

   private void markOwnerDirty() {
      SubScene var1 = this.getSubScene();
      if (var1 != null) {
         var1.markContentDirty();
      } else {
         Scene var2 = this.getScene();
         if (var2 != null) {
            var2.setNeedsRepaint();
         }
      }
   }

   private void markChildrenDirty(Node var1) {
      if (var1 instanceof Shape3D) {
         NodeHelper.markDirty((Shape3D)var1, DirtyBits.NODE_DRAWMODE);
      } else if (var1 instanceof Parent) {
         for (Node var3 : ((Parent)var1).getChildren()) {
            if ((this.scope == null || !this.getScope().contains(var3)) && (this.exclusionScope == null || !this.getExclusionScope().contains(var3))) {
               this.markChildrenDirty(var3);
            }
         }
      }
   }

   private void doMarkDirty(DirtyBits var1) {
      if (this.scope == null || this.getScope().isEmpty()) {
         this.markOwnerDirty();
      } else if (var1 != DirtyBits.NODE_LIGHT_SCOPE) {
         this.getScope().forEach(this::markChildrenDirty);
      }
   }

   private void doUpdatePeer() {
      NGLightBase var1 = this.getPeer();
      if (this.isDirty(DirtyBits.NODE_LIGHT)) {
         var1.setColor(
            this.getColor() == null ? Toolkit.getPaintAccessor().getPlatformPaint(Color.WHITE) : Toolkit.getPaintAccessor().getPlatformPaint(this.getColor())
         );
         var1.setLightOn(this.isLightOn());
      }

      if (this.isDirty(DirtyBits.NODE_LIGHT_SCOPE)) {
         if (this.scope != null) {
            if (this.getScope().isEmpty()) {
               var1.setScope(List.of());
            } else {
               var1.setScope(this.getScope().stream().map(var0 -> var0.getPeer()).collect(Collectors.toList()));
            }
         }

         if (this.exclusionScope != null) {
            if (this.getExclusionScope().isEmpty()) {
               var1.setExclusionScope(List.of());
            } else {
               var1.setExclusionScope(this.getExclusionScope().stream().map(var0 -> var0.getPeer()).collect(Collectors.toList()));
            }
         }
      }

      if (this.isDirty(DirtyBits.NODE_LIGHT_TRANSFORM)) {
         this.localToSceneTx.setToIdentity();
         TransformHelper.apply(this.getLocalToSceneTransform(), this.localToSceneTx);
         var1.setWorldTransform(this.localToSceneTx);
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      return new BoxBounds();
   }

   private boolean doComputeContains(double var1, double var3) {
      return false;
   }

   static {
      LightBaseHelper.setLightBaseAccessor(new LightBaseHelper.LightBaseAccessor() {
         @Override
         public void doMarkDirty(Node var1, DirtyBits var2) {
            ((LightBase)var1).doMarkDirty(var2);
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((LightBase)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((LightBase)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((LightBase)var1).doComputeContains(var2, var4);
         }
      });
   }
}
