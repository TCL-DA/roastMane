package javafx.scene;

import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.Application;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.UnmodifiableListSet;
import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.geometry.BoundsUtils;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.perf.PerformanceTracker;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.scene.CameraHelper;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.EventHandlerProperties;
import com.sun.javafx.scene.LayoutFlags;
import com.sun.javafx.scene.NodeEventDispatcher;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.SceneUtils;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.scene.transform.TransformUtils;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.TempState;
import com.sun.javafx.util.Utils;
import com.sun.prism.impl.PrismSettings;
import com.sun.scenario.effect.EffectHelper;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.Selector;
import javafx.css.Style;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.CursorConverter;
import javafx.css.converter.EffectConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ZoomEvent;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Window;
import javafx.util.Callback;

@IDProperty("id")
public abstract class Node implements EventTarget, Styleable {
   private NodeHelper nodeHelper = null;
   private Set<DirtyBits> dirtyBits = EnumSet.allOf(DirtyBits.class);
   private BaseBounds _geomBounds = new RectBounds(0.0F, 0.0F, -1.0F, -1.0F);
   private BaseBounds _txBounds = new RectBounds(0.0F, 0.0F, -1.0F, -1.0F);
   private boolean pendingUpdateBounds = false;
   private static final Object USER_DATA_KEY = new Object();
   private ObservableMap<Object, Object> properties;
   private ReadOnlyObjectWrapper<Parent> parent;
   private final InvalidationListener parentDisabledChangedListener = var1 -> this.updateDisabled();
   private final InvalidationListener parentTreeVisibleChangedListener = var1 -> this.updateTreeVisible(true);
   private SubScene subScene = null;
   private Node.ReadOnlyObjectWrapperManualFire<Scene> scene = new Node.ReadOnlyObjectWrapperManualFire<>();
   private StringProperty id;
   private ObservableList<String> styleClass = new TrackableObservableList<String>() {
      protected void onChanged(Change<String> var1) {
         Node.this.reapplyCSS();
      }

      public String toString() {
         if (this.size() == 0) {
            return "";
         } else if (this.size() == 1) {
            return (String)this.get(0);
         } else {
            StringBuilder var1 = new StringBuilder();

            for (int var2 = 0; var2 < this.size(); var2++) {
               var1.append((String)this.get(var2));
               if (var2 + 1 < this.size()) {
                  var1.append(' ');
               }
            }

            return var1.toString();
         }
      }
   };
   private StringProperty style;
   private BooleanProperty visible;
   private DoubleProperty opacity;
   private ObjectProperty<BlendMode> blendMode;
   private boolean derivedDepthTest = true;
   private BooleanProperty pickOnBounds;
   private ReadOnlyBooleanWrapper disabled;
   private Node clipParent;
   private NGNode peer;
   private BooleanProperty managed;
   private DoubleProperty layoutX;
   private DoubleProperty layoutY;
   public static final double BASELINE_OFFSET_SAME_AS_HEIGHT = Double.NEGATIVE_INFINITY;
   private Node.LazyBoundsProperty layoutBounds = new Node.LazyBoundsProperty() {
      @Override
      protected Bounds computeBounds() {
         return NodeHelper.computeLayoutBounds(Node.this);
      }

      public Object getBean() {
         return Node.this;
      }

      public String getName() {
         return "layoutBounds";
      }
   };
   private BaseTransform localToParentTx = BaseTransform.IDENTITY_TRANSFORM;
   private boolean transformDirty = true;
   private BaseBounds txBounds = new RectBounds();
   private BaseBounds geomBounds = new RectBounds();
   private BaseBounds localBounds = null;
   boolean boundsChanged;
   private boolean geomBoundsInvalid = true;
   private boolean localBoundsInvalid = true;
   private boolean txBoundsInvalid = true;
   private static final double EPSILON_ABSOLUTE = 1.0E-5;
   private Node.NodeTransformation nodeTransformation;
   private static final double DEFAULT_TRANSLATE_X = 0.0;
   private static final double DEFAULT_TRANSLATE_Y = 0.0;
   private static final double DEFAULT_TRANSLATE_Z = 0.0;
   private static final double DEFAULT_SCALE_X = 1.0;
   private static final double DEFAULT_SCALE_Y = 1.0;
   private static final double DEFAULT_SCALE_Z = 1.0;
   private static final double DEFAULT_ROTATE = 0.0;
   private static final Point3D DEFAULT_ROTATION_AXIS = Rotate.Z_AXIS;
   private EventHandlerProperties eventHandlerProperties;
   private ObjectProperty<NodeOrientation> nodeOrientation;
   private Node.EffectiveOrientationProperty effectiveNodeOrientationProperty;
   private static final byte EFFECTIVE_ORIENTATION_LTR = 0;
   private static final byte EFFECTIVE_ORIENTATION_RTL = 1;
   private static final byte EFFECTIVE_ORIENTATION_MASK = 1;
   private static final byte AUTOMATIC_ORIENTATION_LTR = 0;
   private static final byte AUTOMATIC_ORIENTATION_RTL = 2;
   private static final byte AUTOMATIC_ORIENTATION_MASK = 2;
   private byte resolvedNodeOrientation = 0;
   private Node.MiscProperties miscProperties;
   private static final double DEFAULT_VIEW_ORDER = 0.0;
   private static final boolean DEFAULT_CACHE = false;
   private static final CacheHint DEFAULT_CACHE_HINT = CacheHint.DEFAULT;
   private static final Node DEFAULT_CLIP = null;
   private static final Cursor DEFAULT_CURSOR = null;
   private static final DepthTest DEFAULT_DEPTH_TEST = DepthTest.INHERIT;
   private static final boolean DEFAULT_DISABLE = false;
   private static final Effect DEFAULT_EFFECT = null;
   private static final InputMethodRequests DEFAULT_INPUT_METHOD_REQUESTS = null;
   private static final boolean DEFAULT_MOUSE_TRANSPARENT = false;
   private ReadOnlyBooleanWrapper hover;
   private ReadOnlyBooleanWrapper pressed;
   private Node.FocusedProperty focused;
   private BooleanProperty focusTraversable;
   private boolean treeVisible;
   private Node.TreeVisiblePropertyReadOnly treeVisibleRO;
   private boolean canReceiveFocus = false;
   private BooleanProperty showMnemonics;
   private Node labeledBy = null;
   private ObjectProperty<EventDispatcher> eventDispatcher;
   private NodeEventDispatcher internalEventDispatcher;
   private EventDispatcher preprocessMouseEventDispatcher;
   CssFlags cssFlag = CssFlags.CLEAN;
   final ObservableSet<PseudoClass> pseudoClassStates = new PseudoClassState();
   private final ObservableSet<PseudoClass> unmodifiablePseudoClassStates = FXCollections.unmodifiableObservableSet(this.pseudoClassStates);
   CssStyleHelper styleHelper;
   private static final PseudoClass HOVER_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("hover");
   private static final PseudoClass PRESSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("pressed");
   private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");
   private static final PseudoClass FOCUSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("focused");
   private static final PseudoClass SHOW_MNEMONICS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("show-mnemonics");
   private static final BoundsAccessor boundsAccessor = (var0, var1, var2) -> var2.getGeomBounds(var0, var1);
   private ObjectProperty<AccessibleRole> accessibleRole;
   Node.AccessibilityProperties accessibilityProperties;
   Accessible accessible;

   private void doMarkDirty(DirtyBits var1) {
      if (this.isDirtyEmpty()) {
         this.addToSceneDirtyList();
      }

      this.dirtyBits.add(var1);
   }

   private void addToSceneDirtyList() {
      Scene var1 = this.getScene();
      if (var1 != null) {
         var1.addToDirtyList(this);
         if (this.getSubScene() != null) {
            this.getSubScene().setDirty(this);
         }
      }
   }

   final boolean isDirty(DirtyBits var1) {
      return this.dirtyBits.contains(var1);
   }

   final void clearDirty(DirtyBits var1) {
      this.dirtyBits.remove(var1);
   }

   private void clearDirty() {
      this.dirtyBits.clear();
   }

   private boolean isDirtyEmpty() {
      return this.dirtyBits.isEmpty();
   }

   final void syncPeer() {
      if (!this.isDirtyEmpty() && (this.treeVisible || this.isDirty(DirtyBits.NODE_VISIBLE) || this.isDirty(DirtyBits.NODE_FORCE_SYNC))) {
         NodeHelper.updatePeer(this);
         this.clearDirty();
      }
   }

   void updateBounds() {
      Node var1 = this.getClip();
      if (var1 != null) {
         var1.updateBounds();
      }

      if (!this.treeVisible && !this.isDirty(DirtyBits.NODE_VISIBLE)) {
         if (this.isDirty(DirtyBits.NODE_TRANSFORM) || this.isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS) || this.isDirty(DirtyBits.NODE_BOUNDS)) {
            this.pendingUpdateBounds = true;
         }
      } else {
         if (this.pendingUpdateBounds) {
            NodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORM);
            NodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORMED_BOUNDS);
            NodeHelper.markDirty(this, DirtyBits.NODE_BOUNDS);
            this.pendingUpdateBounds = false;
         }

         if (this.isDirty(DirtyBits.NODE_TRANSFORM) || this.isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS)) {
            if (this.isDirty(DirtyBits.NODE_TRANSFORM)) {
               this.updateLocalToParentTransform();
            }

            this._txBounds = this.getTransformedBounds(this._txBounds, BaseTransform.IDENTITY_TRANSFORM);
         }

         if (this.isDirty(DirtyBits.NODE_BOUNDS)) {
            this._geomBounds = this.getGeomBounds(this._geomBounds, BaseTransform.IDENTITY_TRANSFORM);
         }
      }
   }

   private void doUpdatePeer() {
      NGNode var1 = this.getPeer();
      if (PrismSettings.printRenderGraph && this.isDirty(DirtyBits.DEBUG)) {
         String var2 = this.getId();
         String var3 = this.getClass().getSimpleName();
         if (var3.isEmpty()) {
            var3 = this.getClass().getName();
         }

         var1.setName(var2 == null ? var3 : var2 + "(" + var3 + ")");
      }

      if (this.isDirty(DirtyBits.NODE_TRANSFORM)) {
         var1.setTransformMatrix(this.localToParentTx);
      }

      if (this.isDirty(DirtyBits.NODE_VIEW_ORDER)) {
         var1.setViewOrder(this.getViewOrder());
      }

      if (this.isDirty(DirtyBits.NODE_BOUNDS)) {
         var1.setContentBounds(this._geomBounds);
      }

      if (this.isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS)) {
         var1.setTransformedBounds(this._txBounds, !this.isDirty(DirtyBits.NODE_BOUNDS));
      }

      if (this.isDirty(DirtyBits.NODE_OPACITY)) {
         var1.setOpacity((float)Utils.clamp(0.0, this.getOpacity(), 1.0));
      }

      if (this.isDirty(DirtyBits.NODE_CACHE)) {
         var1.setCachedAsBitmap(this.isCache(), this.getCacheHint());
      }

      if (this.isDirty(DirtyBits.NODE_CLIP)) {
         var1.setClipNode(this.getClip() != null ? this.getClip().getPeer() : null);
      }

      if (this.isDirty(DirtyBits.EFFECT_EFFECT) && this.getEffect() != null) {
         EffectHelper.sync(this.getEffect());
         var1.effectChanged();
      }

      if (this.isDirty(DirtyBits.NODE_EFFECT)) {
         var1.setEffect(this.getEffect() != null ? EffectHelper.getPeer(this.getEffect()) : null);
      }

      if (this.isDirty(DirtyBits.NODE_VISIBLE)) {
         var1.setVisible(this.isVisible());
      }

      if (this.isDirty(DirtyBits.NODE_DEPTH_TEST)) {
         var1.setDepthTest(this.isDerivedDepthTest());
      }

      if (this.isDirty(DirtyBits.NODE_BLENDMODE)) {
         BlendMode var4 = this.getBlendMode();
         var1.setNodeBlendMode(var4 == null ? null : EffectHelper.getToolkitBlendMode(var4));
      }
   }

   public final ObservableMap<Object, Object> getProperties() {
      if (this.properties == null) {
         this.properties = FXCollections.observableMap(new HashMap());
      }

      return this.properties;
   }

   public boolean hasProperties() {
      return this.properties != null && !this.properties.isEmpty();
   }

   public void setUserData(Object var1) {
      this.getProperties().put(USER_DATA_KEY, var1);
   }

   public Object getUserData() {
      return this.getProperties().get(USER_DATA_KEY);
   }

   final void setParent(Parent var1) {
      this.parentPropertyImpl().set(var1);
   }

   public final Parent getParent() {
      return this.parent == null ? null : (Parent)this.parent.get();
   }

   public final ReadOnlyObjectProperty<Parent> parentProperty() {
      return this.parentPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<Parent> parentPropertyImpl() {
      if (this.parent == null) {
         this.parent = new ReadOnlyObjectWrapper<Parent>() {
            private Parent oldParent;

            protected void invalidated() {
               if (this.oldParent != null) {
                  this.oldParent.disabledProperty().removeListener(Node.this.parentDisabledChangedListener);
                  this.oldParent.treeVisibleProperty().removeListener(Node.this.parentTreeVisibleChangedListener);
                  if (Node.this.nodeTransformation != null && Node.this.nodeTransformation.listenerReasons > 0) {
                     this.oldParent.localToSceneTransformProperty().removeListener(Node.this.nodeTransformation.getLocalToSceneInvalidationListener());
                  }
               }

               Node.this.updateDisabled();
               Node.this.computeDerivedDepthTest();
               Parent var1 = (Parent)this.get();
               if (var1 != null) {
                  var1.disabledProperty().addListener(Node.this.parentDisabledChangedListener);
                  var1.treeVisibleProperty().addListener(Node.this.parentTreeVisibleChangedListener);
                  if (Node.this.nodeTransformation != null && Node.this.nodeTransformation.listenerReasons > 0) {
                     var1.localToSceneTransformProperty().addListener(Node.this.nodeTransformation.getLocalToSceneInvalidationListener());
                  }

                  Node.this.reapplyCSS();
               } else {
                  Node.this.cssFlag = CssFlags.CLEAN;
               }

               Node.this.updateTreeVisible(true);
               this.oldParent = var1;
               Node.this.invalidateLocalToSceneTransform();
               Node.this.parentResolvedOrientationInvalidated();
               Node.this.notifyAccessibleAttributeChanged(AccessibleAttribute.PARENT);
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "parent";
            }
         };
      }

      return this.parent;
   }

   private void invalidatedScenes(Scene var1, SubScene var2) {
      Scene var3 = (Scene)this.sceneProperty().get();
      boolean var4 = var1 != var3;
      SubScene var5 = this.subScene;
      if (this.getClip() != null) {
         this.getClip().setScenes(var3, var5);
      }

      if (var4) {
         this.updateCanReceiveFocus();
         if (this.isFocusTraversable() && var3 != null) {
            var3.initializeInternalEventDispatcher();
         }

         this.focusSetDirty(var1);
         this.focusSetDirty(var3);
      }

      this.scenesChanged(var3, var5, var1, var2);
      if (var4) {
         this.reapplyCSS();
      }

      if (var4 && !this.isDirtyEmpty()) {
         this.addToSceneDirtyList();
      }

      if (var3 == null && this.peer != null) {
         this.peer.release();
      }

      if (var1 != null) {
         var1.clearNodeMnemonics(this);
      }

      if (this.getParent() == null) {
         this.parentResolvedOrientationInvalidated();
      }

      if (var4) {
         this.scene.fireSuperValueChangedEvent();
      }

      if (this.accessible != null) {
         if (var1 != null && var1 != var3 && var3 == null) {
            var1.addAccessible(this, this.accessible);
         } else {
            this.accessible.dispose();
         }

         this.accessible = null;
      }
   }

   final void setScenes(Scene var1, SubScene var2) {
      Scene var3 = (Scene)this.sceneProperty().get();
      if (var1 != var3 || var2 != this.subScene) {
         this.scene.set(var1);
         SubScene var4 = this.subScene;
         this.subScene = var2;
         this.invalidatedScenes(var3, var4);
         if (this instanceof SubScene) {
            SubScene var5 = (SubScene)this;
            var5.getRoot().setScenes(var1, var5);
         }
      }
   }

   final SubScene getSubScene() {
      return this.subScene;
   }

   public final Scene getScene() {
      return (Scene)this.scene.get();
   }

   public final ReadOnlyObjectProperty<Scene> sceneProperty() {
      return this.scene.getReadOnlyProperty();
   }

   void scenesChanged(Scene var1, SubScene var2, Scene var3, SubScene var4) {
   }

   public final void setId(String var1) {
      this.idProperty().set(var1);
   }

   @Override
   public final String getId() {
      return this.id == null ? null : (String)this.id.get();
   }

   public final StringProperty idProperty() {
      if (this.id == null) {
         this.id = new StringPropertyBase() {
            protected void invalidated() {
               Node.this.reapplyCSS();
               if (PrismSettings.printRenderGraph) {
                  NodeHelper.markDirty(Node.this, DirtyBits.DEBUG);
               }
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "id";
            }
         };
      }

      return this.id;
   }

   @Override
   public final ObservableList<String> getStyleClass() {
      return this.styleClass;
   }

   public final void setStyle(String var1) {
      this.styleProperty().set(var1);
   }

   @Override
   public final String getStyle() {
      return this.style == null ? "" : (String)this.style.get();
   }

   public final StringProperty styleProperty() {
      if (this.style == null) {
         this.style = new StringPropertyBase("") {
            public void set(String var1) {
               super.set(var1 != null ? var1 : "");
            }

            protected void invalidated() {
               Node.this.reapplyCSS();
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "style";
            }
         };
      }

      return this.style;
   }

   public final void setVisible(boolean var1) {
      this.visibleProperty().set(var1);
   }

   public final boolean isVisible() {
      return this.visible == null ? true : this.visible.get();
   }

   public final BooleanProperty visibleProperty() {
      if (this.visible == null) {
         this.visible = new StyleableBooleanProperty(true) {
            boolean oldValue = true;

            protected void invalidated() {
               if (this.oldValue != this.get()) {
                  NodeHelper.markDirty(Node.this, DirtyBits.NODE_VISIBLE);
                  NodeHelper.geomChanged(Node.this);
                  Node.this.updateTreeVisible(false);
                  if (Node.this.getParent() != null) {
                     Node.this.getParent().childVisibilityChanged(Node.this);
                  }

                  this.oldValue = this.get();
               }
            }

            @Override
            public CssMetaData getCssMetaData() {
               return Node.StyleableProperties.VISIBILITY;
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "visible";
            }
         };
      }

      return this.visible;
   }

   public final void setCursor(Cursor var1) {
      this.cursorProperty().set(var1);
   }

   public final Cursor getCursor() {
      return this.miscProperties == null ? DEFAULT_CURSOR : this.miscProperties.getCursor();
   }

   public final ObjectProperty<Cursor> cursorProperty() {
      return this.getMiscProperties().cursorProperty();
   }

   public final void setOpacity(double var1) {
      this.opacityProperty().set(var1);
   }

   public final double getOpacity() {
      return this.opacity == null ? 1.0 : this.opacity.get();
   }

   public final DoubleProperty opacityProperty() {
      if (this.opacity == null) {
         this.opacity = new StyleableDoubleProperty(1.0) {
            public void invalidated() {
               NodeHelper.markDirty(Node.this, DirtyBits.NODE_OPACITY);
            }

            @Override
            public CssMetaData getCssMetaData() {
               return Node.StyleableProperties.OPACITY;
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "opacity";
            }
         };
      }

      return this.opacity;
   }

   public final void setBlendMode(BlendMode var1) {
      this.blendModeProperty().set(var1);
   }

   public final BlendMode getBlendMode() {
      return this.blendMode == null ? null : (BlendMode)this.blendMode.get();
   }

   public final ObjectProperty<BlendMode> blendModeProperty() {
      if (this.blendMode == null) {
         this.blendMode = new StyleableObjectProperty<BlendMode>(null) {
            public void invalidated() {
               NodeHelper.markDirty(Node.this, DirtyBits.NODE_BLENDMODE);
            }

            @Override
            public CssMetaData getCssMetaData() {
               return Node.StyleableProperties.BLEND_MODE;
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "blendMode";
            }
         };
      }

      return this.blendMode;
   }

   public final void setClip(Node var1) {
      this.clipProperty().set(var1);
   }

   public final Node getClip() {
      return this.miscProperties == null ? DEFAULT_CLIP : this.miscProperties.getClip();
   }

   public final ObjectProperty<Node> clipProperty() {
      return this.getMiscProperties().clipProperty();
   }

   public final void setCache(boolean var1) {
      this.cacheProperty().set(var1);
   }

   public final boolean isCache() {
      return this.miscProperties == null ? false : this.miscProperties.isCache();
   }

   public final BooleanProperty cacheProperty() {
      return this.getMiscProperties().cacheProperty();
   }

   public final void setCacheHint(CacheHint var1) {
      this.cacheHintProperty().set(var1);
   }

   public final CacheHint getCacheHint() {
      return this.miscProperties == null ? DEFAULT_CACHE_HINT : this.miscProperties.getCacheHint();
   }

   public final ObjectProperty<CacheHint> cacheHintProperty() {
      return this.getMiscProperties().cacheHintProperty();
   }

   public final void setEffect(Effect var1) {
      this.effectProperty().set(var1);
   }

   public final Effect getEffect() {
      return this.miscProperties == null ? DEFAULT_EFFECT : this.miscProperties.getEffect();
   }

   public final ObjectProperty<Effect> effectProperty() {
      return this.getMiscProperties().effectProperty();
   }

   public final void setDepthTest(DepthTest var1) {
      this.depthTestProperty().set(var1);
   }

   public final DepthTest getDepthTest() {
      return this.miscProperties == null ? DEFAULT_DEPTH_TEST : this.miscProperties.getDepthTest();
   }

   public final ObjectProperty<DepthTest> depthTestProperty() {
      return this.getMiscProperties().depthTestProperty();
   }

   void computeDerivedDepthTest() {
      boolean var1;
      if (this.getDepthTest() == DepthTest.INHERIT) {
         if (this.getParent() != null) {
            var1 = this.getParent().isDerivedDepthTest();
         } else {
            var1 = true;
         }
      } else if (this.getDepthTest() == DepthTest.ENABLE) {
         var1 = true;
      } else {
         var1 = false;
      }

      if (this.isDerivedDepthTest() != var1) {
         NodeHelper.markDirty(this, DirtyBits.NODE_DEPTH_TEST);
         this.setDerivedDepthTest(var1);
      }
   }

   void setDerivedDepthTest(boolean var1) {
      this.derivedDepthTest = var1;
   }

   boolean isDerivedDepthTest() {
      return this.derivedDepthTest;
   }

   public final void setDisable(boolean var1) {
      this.disableProperty().set(var1);
   }

   public final boolean isDisable() {
      return this.miscProperties == null ? false : this.miscProperties.isDisable();
   }

   public final BooleanProperty disableProperty() {
      return this.getMiscProperties().disableProperty();
   }

   public final void setPickOnBounds(boolean var1) {
      this.pickOnBoundsProperty().set(var1);
   }

   public final boolean isPickOnBounds() {
      return this.pickOnBounds == null ? false : this.pickOnBounds.get();
   }

   public final BooleanProperty pickOnBoundsProperty() {
      if (this.pickOnBounds == null) {
         this.pickOnBounds = new SimpleBooleanProperty(this, "pickOnBounds");
      }

      return this.pickOnBounds;
   }

   protected final void setDisabled(boolean var1) {
      this.disabledPropertyImpl().set(var1);
   }

   public final boolean isDisabled() {
      return this.disabled == null ? false : this.disabled.get();
   }

   public final ReadOnlyBooleanProperty disabledProperty() {
      return this.disabledPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper disabledPropertyImpl() {
      if (this.disabled == null) {
         this.disabled = new ReadOnlyBooleanWrapper() {
            protected void invalidated() {
               Node.this.pseudoClassStateChanged(Node.DISABLED_PSEUDOCLASS_STATE, this.get());
               Node.this.updateCanReceiveFocus();
               Node.this.focusSetDirty(Node.this.getScene());
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "disabled";
            }
         };
      }

      return this.disabled;
   }

   private void updateDisabled() {
      boolean var1 = this.isDisable();
      if (!var1) {
         var1 = this.getParent() != null ? this.getParent().isDisabled() : this.getSubScene() != null && this.getSubScene().isDisabled();
      }

      this.setDisabled(var1);
      if (this instanceof SubScene) {
         ((SubScene)this).getRoot().setDisabled(var1);
      }
   }

   public Node lookup(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Selector var2 = Selector.createSelector(var1);
         return var2 != null && var2.applies(this) ? this : null;
      }
   }

   public Set<Node> lookupAll(String var1) {
      Selector var2 = Selector.createSelector(var1);
      Set var3 = Collections.emptySet();
      if (var2 == null) {
         return var3;
      } else {
         List var4 = this.lookupAll(var2, null);
         return (Set<Node>)(var4 == null ? var3 : new UnmodifiableListSet(var4));
      }
   }

   List<Node> lookupAll(Selector var1, List<Node> var2) {
      if (var1.applies(this)) {
         if (var2 == null) {
            var2 = new LinkedList();
         }

         var2.add(this);
      }

      return (List<Node>)var2;
   }

   public void toBack() {
      if (this.getParent() != null) {
         this.getParent().toBack(this);
      }
   }

   public void toFront() {
      if (this.getParent() != null) {
         this.getParent().toFront(this);
      }
   }

   private void doCSSPass() {
      if (this.cssFlag != CssFlags.CLEAN) {
         this.processCSS();
      }
   }

   private static void syncAll(Node var0) {
      var0.syncPeer();
      if (var0 instanceof Parent) {
         Parent var1 = (Parent)var0;
         int var2 = var1.getChildren().size();

         for (int var3 = 0; var3 < var2; var3++) {
            Node var4 = (Node)var1.getChildren().get(var3);
            if (var4 != null) {
               syncAll(var4);
            }
         }
      }

      if (var0.getClip() != null) {
         syncAll(var0.getClip());
      }
   }

   private void doLayoutPass() {
      if (this instanceof Parent) {
         Parent var1 = (Parent)this;

         for (int var2 = 0; var2 < 3; var2++) {
            var1.layout();
         }
      }
   }

   private void doCSSLayoutSyncForSnapshot() {
      this.doCSSPass();
      this.doLayoutPass();
      this.updateBounds();
      Scene.setAllowPGAccess(true);
      syncAll(this);
      Scene.setAllowPGAccess(false);
   }

   private WritableImage doSnapshot(SnapshotParameters var1, WritableImage var2) {
      if (this.getScene() != null) {
         this.getScene().doCSSLayoutSyncForSnapshot(this);
      } else {
         this.doCSSLayoutSyncForSnapshot();
      }

      Object var3 = BaseTransform.IDENTITY_TRANSFORM;
      if (var1.getTransform() != null) {
         Affine3D var4 = new Affine3D();
         TransformHelper.apply(var1.getTransform(), var4);
         var3 = var4;
      }

      Rectangle2D var12 = var1.getViewport();
      double var6;
      double var8;
      double var10;
      double var14;
      if (var12 != null) {
         var14 = var12.getMinX();
         var6 = var12.getMinY();
         var8 = var12.getWidth();
         var10 = var12.getHeight();
      } else {
         BaseBounds var13 = TempState.getInstance().bounds;
         var13 = this.getTransformedBounds(var13, (BaseTransform)var3);
         var14 = (double)var13.getMinX();
         var6 = (double)var13.getMinY();
         var8 = (double)var13.getWidth();
         var10 = (double)var13.getHeight();
      }

      return Scene.doSnapshot(
         this.getScene(), var14, var6, var8, var10, this, (BaseTransform)var3, var1.isDepthBufferInternal(), var1.getFill(), var1.getEffectiveCamera(), var2
      );
   }

   public WritableImage snapshot(SnapshotParameters var1, WritableImage var2) {
      Toolkit.getToolkit().checkFxUserThread();
      if (var1 == null) {
         var1 = new SnapshotParameters();
         Scene var3 = this.getScene();
         if (var3 != null) {
            var1.setCamera(var3.getEffectiveCamera());
            var1.setDepthBuffer(var3.isDepthBufferInternal());
            var1.setFill(var3.getFill());
         }
      }

      return this.doSnapshot(var1, var2);
   }

   public void snapshot(Callback<SnapshotResult, Void> var1, SnapshotParameters var2, WritableImage var3) {
      Toolkit.getToolkit().checkFxUserThread();
      if (var1 == null) {
         throw new NullPointerException("The callback must not be null");
      } else {
         if (var2 == null) {
            var2 = new SnapshotParameters();
            Scene var4 = this.getScene();
            if (var4 != null) {
               var2.setCamera(var4.getEffectiveCamera());
               var2.setDepthBuffer(var4.isDepthBufferInternal());
               var2.setFill(var4.getFill());
            }
         } else {
            var2 = var2.copy();
         }

         SnapshotParameters var9 = var2;
         Runnable var7 = () -> {
            WritableImage var4x = this.doSnapshot(var9, var3);
            SnapshotResult var5 = new SnapshotResult(var4x, this, var9);

            try {
               Void var6 = (Void)var1.call(var5);
            } catch (Throwable var7x) {
               System.err.println("Exception in snapshot callback");
               var7x.printStackTrace(System.err);
            }
         };
         Scene.addSnapshotRunnable(var7);
      }
   }

   public final void setOnDragEntered(EventHandler<? super DragEvent> var1) {
      this.onDragEnteredProperty().set(var1);
   }

   public final EventHandler<? super DragEvent> getOnDragEntered() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragEntered();
   }

   public final ObjectProperty<EventHandler<? super DragEvent>> onDragEnteredProperty() {
      return this.getEventHandlerProperties().onDragEnteredProperty();
   }

   public final void setOnDragExited(EventHandler<? super DragEvent> var1) {
      this.onDragExitedProperty().set(var1);
   }

   public final EventHandler<? super DragEvent> getOnDragExited() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragExited();
   }

   public final ObjectProperty<EventHandler<? super DragEvent>> onDragExitedProperty() {
      return this.getEventHandlerProperties().onDragExitedProperty();
   }

   public final void setOnDragOver(EventHandler<? super DragEvent> var1) {
      this.onDragOverProperty().set(var1);
   }

   public final EventHandler<? super DragEvent> getOnDragOver() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragOver();
   }

   public final ObjectProperty<EventHandler<? super DragEvent>> onDragOverProperty() {
      return this.getEventHandlerProperties().onDragOverProperty();
   }

   public final void setOnDragDropped(EventHandler<? super DragEvent> var1) {
      this.onDragDroppedProperty().set(var1);
   }

   public final EventHandler<? super DragEvent> getOnDragDropped() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragDropped();
   }

   public final ObjectProperty<EventHandler<? super DragEvent>> onDragDroppedProperty() {
      return this.getEventHandlerProperties().onDragDroppedProperty();
   }

   public final void setOnDragDone(EventHandler<? super DragEvent> var1) {
      this.onDragDoneProperty().set(var1);
   }

   public final EventHandler<? super DragEvent> getOnDragDone() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragDone();
   }

   public final ObjectProperty<EventHandler<? super DragEvent>> onDragDoneProperty() {
      return this.getEventHandlerProperties().onDragDoneProperty();
   }

   public Dragboard startDragAndDrop(TransferMode... var1) {
      if (this.getScene() != null) {
         return this.getScene().startDragAndDrop(this, var1);
      } else {
         throw new IllegalStateException("Cannot start drag and drop on node that is not in scene");
      }
   }

   public void startFullDrag() {
      if (this.getScene() != null) {
         this.getScene().startFullDrag(this);
      } else {
         throw new IllegalStateException("Cannot start full drag on node that is not in scene");
      }
   }

   final Node getClipParent() {
      return this.clipParent;
   }

   boolean isConnected() {
      return this.getParent() != null || this.clipParent != null;
   }

   boolean wouldCreateCycle(Node var1, Node var2) {
      if (var2 != null && var2.getClip() == null && !(var2 instanceof Parent)) {
         return false;
      } else {
         Object var3 = var1;

         while (var3 != var2) {
            if (((Node)var3).getParent() != null) {
               var3 = ((Node)var3).getParent();
            } else if (((Node)var3).getSubScene() != null) {
               var3 = ((Node)var3).getSubScene();
            } else {
               if (((Node)var3).clipParent == null) {
                  return false;
               }

               var3 = ((Node)var3).clipParent;
            }
         }

         return true;
      }
   }

   <P extends NGNode> P getPeer() {
      if (Utils.assertionEnabled() && this.getScene() != null && !Scene.isPGAccessAllowed()) {
         System.err.println();
         System.err.println("*** unexpected PG access");
         Thread.dumpStack();
      }

      if (this.peer == null) {
         this.peer = NodeHelper.createPeer(this);
      }

      return (P)this.peer;
   }

   protected Node() {
      this.updateTreeVisible(false);
   }

   public final void setManaged(boolean var1) {
      this.managedProperty().set(var1);
   }

   public final boolean isManaged() {
      return this.managed == null ? true : this.managed.get();
   }

   public final BooleanProperty managedProperty() {
      if (this.managed == null) {
         this.managed = new BooleanPropertyBase(true) {
            protected void invalidated() {
               Parent var1 = Node.this.getParent();
               if (var1 != null) {
                  var1.managedChildChanged();
               }

               Node.this.notifyManagedChanged();
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "managed";
            }
         };
      }

      return this.managed;
   }

   void notifyManagedChanged() {
   }

   public final void setLayoutX(double var1) {
      this.layoutXProperty().set(var1);
   }

   public final double getLayoutX() {
      return this.layoutX == null ? 0.0 : this.layoutX.get();
   }

   public final DoubleProperty layoutXProperty() {
      if (this.layoutX == null) {
         this.layoutX = new DoublePropertyBase(0.0) {
            protected void invalidated() {
               NodeHelper.transformsChanged(Node.this);
               Parent var1 = Node.this.getParent();
               if (var1 != null && !var1.isCurrentLayoutChild(Node.this)) {
                  if (Node.this.isManaged()) {
                     var1.requestLayout(true);
                  } else {
                     var1.clearSizeCache();
                     var1.requestParentLayout();
                  }
               }
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "layoutX";
            }
         };
      }

      return this.layoutX;
   }

   public final void setLayoutY(double var1) {
      this.layoutYProperty().set(var1);
   }

   public final double getLayoutY() {
      return this.layoutY == null ? 0.0 : this.layoutY.get();
   }

   public final DoubleProperty layoutYProperty() {
      if (this.layoutY == null) {
         this.layoutY = new DoublePropertyBase(0.0) {
            protected void invalidated() {
               NodeHelper.transformsChanged(Node.this);
               Parent var1 = Node.this.getParent();
               if (var1 != null && !var1.isCurrentLayoutChild(Node.this)) {
                  if (Node.this.isManaged()) {
                     var1.requestLayout(true);
                  } else {
                     var1.clearSizeCache();
                     var1.requestParentLayout();
                  }
               }
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "layoutY";
            }
         };
      }

      return this.layoutY;
   }

   public void relocate(double var1, double var3) {
      this.setLayoutX(var1 - this.getLayoutBounds().getMinX());
      this.setLayoutY(var3 - this.getLayoutBounds().getMinY());
      PlatformLogger var5 = Logging.getLayoutLogger();
      if (var5.isLoggable(Level.FINER)) {
         var5.finer(this.toString() + " moved to (" + var1 + "," + var3 + ")");
      }
   }

   public boolean isResizable() {
      return false;
   }

   public Orientation getContentBias() {
      return null;
   }

   public double minWidth(double var1) {
      return this.prefWidth(var1);
   }

   public double minHeight(double var1) {
      return this.prefHeight(var1);
   }

   public double prefWidth(double var1) {
      double var3 = this.getLayoutBounds().getWidth();
      return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
   }

   public double prefHeight(double var1) {
      double var3 = this.getLayoutBounds().getHeight();
      return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
   }

   public double maxWidth(double var1) {
      return this.prefWidth(var1);
   }

   public double maxHeight(double var1) {
      return this.prefHeight(var1);
   }

   public void resize(double var1, double var3) {
   }

   public final void autosize() {
      if (this.isResizable()) {
         Orientation var1 = this.getContentBias();
         double var2;
         double var4;
         if (var1 == null) {
            var2 = this.boundedSize(this.prefWidth(-1.0), this.minWidth(-1.0), this.maxWidth(-1.0));
            var4 = this.boundedSize(this.prefHeight(-1.0), this.minHeight(-1.0), this.maxHeight(-1.0));
         } else if (var1 == Orientation.HORIZONTAL) {
            var2 = this.boundedSize(this.prefWidth(-1.0), this.minWidth(-1.0), this.maxWidth(-1.0));
            var4 = this.boundedSize(this.prefHeight(var2), this.minHeight(var2), this.maxHeight(var2));
         } else {
            var4 = this.boundedSize(this.prefHeight(-1.0), this.minHeight(-1.0), this.maxHeight(-1.0));
            var2 = this.boundedSize(this.prefWidth(var4), this.minWidth(var4), this.maxWidth(var4));
         }

         this.resize(var2, var4);
      }
   }

   double boundedSize(double var1, double var3, double var5) {
      return Math.min(Math.max(var1, var3), Math.max(var3, var5));
   }

   public void resizeRelocate(double var1, double var3, double var5, double var7) {
      this.resize(var5, var7);
      this.relocate(var1, var3);
   }

   public double getBaselineOffset() {
      return this.isResizable() ? Double.NEGATIVE_INFINITY : this.getLayoutBounds().getHeight();
   }

   public double computeAreaInScreen() {
      return this.doComputeAreaInScreen();
   }

   private double doComputeAreaInScreen() {
      Scene var1 = this.getScene();
      if (var1 != null) {
         Bounds var2 = this.getBoundsInLocal();
         Camera var3 = var1.getEffectiveCamera();
         boolean var4 = var3 instanceof PerspectiveCamera;
         Transform var5 = this.getLocalToSceneTransform();
         Affine3D var6 = TempState.getInstance().tempTx;
         BoxBounds var7 = new BoxBounds(
            (float)var2.getMinX(), (float)var2.getMinY(), (float)var2.getMinZ(), (float)var2.getMaxX(), (float)var2.getMaxY(), (float)var2.getMaxZ()
         );
         if (var4) {
            Transform var8 = var3.getLocalToSceneTransform();
            if (var8.getMxx() == 1.0
               && var8.getMxy() == 0.0
               && var8.getMxz() == 0.0
               && var8.getMyx() == 0.0
               && var8.getMyy() == 1.0
               && var8.getMyz() == 0.0
               && var8.getMzx() == 0.0
               && var8.getMzy() == 0.0
               && var8.getMzz() == 1.0) {
               double var11;
               double var16;
               if (var5.getMxx() == 1.0
                  && var5.getMxy() == 0.0
                  && var5.getMxz() == 0.0
                  && var5.getMyx() == 0.0
                  && var5.getMyy() == 1.0
                  && var5.getMyz() == 0.0
                  && var5.getMzx() == 0.0
                  && var5.getMzy() == 0.0
                  && var5.getMzz() == 1.0) {
                  Vec3d var18 = TempState.getInstance().vec3d;
                  var18.set(0.0, 0.0, var2.getMinZ());
                  this.localToScene(var18);
                  var16 = var18.z;
                  var18.set(0.0, 0.0, var2.getMaxZ());
                  this.localToScene(var18);
                  var11 = var18.z;
               } else {
                  Bounds var13 = this.localToScene(var2);
                  var16 = var13.getMinZ();
                  var11 = var13.getMaxZ();
               }

               if (var16 > var3.getFarClipInScene() || var11 < var3.getNearClipInScene()) {
                  return 0.0;
               }
            } else {
               BoxBounds var9 = new BoxBounds();
               var6.setToIdentity();
               TransformHelper.apply(var5, var6);
               var6.preConcatenate(var3.getSceneToLocalTransform());
               var6.transform(var7, var9);
               if ((double)var9.getMinZ() > var3.getFarClip() || (double)var9.getMaxZ() < var3.getNearClip()) {
                  return 0.0;
               }
            }
         }

         GeneralTransform3D var15 = TempState.getInstance().projViewTx;
         var15.set(var3.getProjViewTransform());
         var6.setToIdentity();
         TransformHelper.apply(var5, var6);
         GeneralTransform3D var17 = var15.mul(var6);
         BaseBounds var14 = var17.transform(var7, var7);
         double var10 = (double)(var14.getWidth() * var14.getHeight());
         if (var4) {
            var14.intersectWith(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            var10 = !(var14.getWidth() < 0.0F) && !(var14.getHeight() < 0.0F) ? var10 : 0.0;
         }

         return var10 * (var3.getViewWidth() / 2.0 * var3.getViewHeight() / 2.0);
      } else {
         return 0.0;
      }
   }

   public final Bounds getBoundsInParent() {
      return (Bounds)this.boundsInParentProperty().get();
   }

   public final ReadOnlyObjectProperty<Bounds> boundsInParentProperty() {
      return this.getMiscProperties().boundsInParentProperty();
   }

   private void invalidateBoundsInParent() {
      if (this.miscProperties != null) {
         this.miscProperties.invalidateBoundsInParent();
      }
   }

   public final Bounds getBoundsInLocal() {
      return (Bounds)this.boundsInLocalProperty().get();
   }

   public final ReadOnlyObjectProperty<Bounds> boundsInLocalProperty() {
      return this.getMiscProperties().boundsInLocalProperty();
   }

   private void invalidateBoundsInLocal() {
      if (this.miscProperties != null) {
         this.miscProperties.invalidateBoundsInLocal();
      }
   }

   public final Bounds getLayoutBounds() {
      return (Bounds)this.layoutBoundsProperty().get();
   }

   public final ReadOnlyObjectProperty<Bounds> layoutBoundsProperty() {
      return this.layoutBounds;
   }

   private Bounds doComputeLayoutBounds() {
      BaseBounds var1 = TempState.getInstance().bounds;
      var1 = this.getGeomBounds(var1, BaseTransform.IDENTITY_TRANSFORM);
      return new BoundingBox(
         (double)var1.getMinX(), (double)var1.getMinY(), (double)var1.getMinZ(), (double)var1.getWidth(), (double)var1.getHeight(), (double)var1.getDepth()
      );
   }

   final void layoutBoundsChanged() {
      if (this.layoutBounds.valid) {
         this.layoutBounds.invalidate();
         if (this.nodeTransformation != null && this.nodeTransformation.hasScaleOrRotate() || this.hasMirroring()) {
            NodeHelper.transformsChanged(this);
         }
      }
   }

   BaseBounds getTransformedBounds(BaseBounds var1, BaseTransform var2) {
      this.updateLocalToParentTransform();
      if (var2.isTranslateOrIdentity()) {
         this.updateTxBounds();
         var1 = var1.deriveWithNewBounds(this.txBounds);
         if (!var2.isIdentity()) {
            double var30 = var2.getMxt();
            double var31 = var2.getMyt();
            double var32 = var2.getMzt();
            var1 = var1.deriveWithNewBounds(
               (float)((double)var1.getMinX() + var30),
               (float)((double)var1.getMinY() + var31),
               (float)((double)var1.getMinZ() + var32),
               (float)((double)var1.getMaxX() + var30),
               (float)((double)var1.getMaxY() + var31),
               (float)((double)var1.getMaxZ() + var32)
            );
         }

         return var1;
      } else if (this.localToParentTx.isIdentity()) {
         return this.getLocalBounds(var1, var2);
      } else {
         double var3 = var2.getMxx();
         double var5 = var2.getMxy();
         double var7 = var2.getMxz();
         double var9 = var2.getMxt();
         double var11 = var2.getMyx();
         double var13 = var2.getMyy();
         double var15 = var2.getMyz();
         double var17 = var2.getMyt();
         double var19 = var2.getMzx();
         double var21 = var2.getMzy();
         double var23 = var2.getMzz();
         double var25 = var2.getMzt();
         BaseTransform var27 = var2.deriveWithConcatenation(this.localToParentTx);
         var1 = this.getLocalBounds(var1, var27);
         if (var27 == var2) {
            var2.restoreTransform(var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23, var25);
         }

         return var1;
      }
   }

   BaseBounds getLocalBounds(BaseBounds var1, BaseTransform var2) {
      if (this.getEffect() == null && this.getClip() == null) {
         return this.getGeomBounds(var1, var2);
      } else if (var2.isTranslateOrIdentity()) {
         this.updateLocalBounds();
         var1 = var1.deriveWithNewBounds(this.localBounds);
         if (!var2.isIdentity()) {
            double var3 = var2.getMxt();
            double var5 = var2.getMyt();
            double var7 = var2.getMzt();
            var1 = var1.deriveWithNewBounds(
               (float)((double)var1.getMinX() + var3),
               (float)((double)var1.getMinY() + var5),
               (float)((double)var1.getMinZ() + var7),
               (float)((double)var1.getMaxX() + var3),
               (float)((double)var1.getMaxY() + var5),
               (float)((double)var1.getMaxZ() + var7)
            );
         }

         return var1;
      } else if (var2.is2D() && (var2.getType() & -76) != 0) {
         return this.computeLocalBounds(var1, var2);
      } else {
         this.updateLocalBounds();
         return var2.transform(this.localBounds, var1);
      }
   }

   BaseBounds getGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (var2.isTranslateOrIdentity()) {
         this.updateGeomBounds();
         var1 = var1.deriveWithNewBounds(this.geomBounds);
         if (!var2.isIdentity()) {
            double var3 = var2.getMxt();
            double var5 = var2.getMyt();
            double var7 = var2.getMzt();
            var1 = var1.deriveWithNewBounds(
               (float)((double)var1.getMinX() + var3),
               (float)((double)var1.getMinY() + var5),
               (float)((double)var1.getMinZ() + var7),
               (float)((double)var1.getMaxX() + var3),
               (float)((double)var1.getMaxY() + var5),
               (float)((double)var1.getMaxZ() + var7)
            );
         }

         return var1;
      } else if (var2.is2D() && (var2.getType() & -76) != 0) {
         return NodeHelper.computeGeomBounds(this, var1, var2);
      } else {
         this.updateGeomBounds();
         return var2.transform(this.geomBounds, var1);
      }
   }

   void updateGeomBounds() {
      if (this.geomBoundsInvalid) {
         this.geomBounds = NodeHelper.computeGeomBounds(this, this.geomBounds, BaseTransform.IDENTITY_TRANSFORM);
         this.geomBoundsInvalid = false;
      }
   }

   private BaseBounds computeLocalBounds(BaseBounds var1, BaseTransform var2) {
      if (this.getEffect() != null) {
         BaseBounds var3 = EffectHelper.getBounds(this.getEffect(), var1, var2, this, boundsAccessor);
         var1 = var1.deriveWithNewBounds(var3);
      } else {
         var1 = this.getGeomBounds(var1, var2);
      }

      if (this.getClip() != null && !(this instanceof Shape3D) && !(this.getClip() instanceof Shape3D)) {
         double var16 = (double)var1.getMinX();
         double var5 = (double)var1.getMinY();
         double var7 = (double)var1.getMaxX();
         double var9 = (double)var1.getMaxY();
         double var11 = (double)var1.getMinZ();
         double var13 = (double)var1.getMaxZ();
         var1 = this.getClip().getTransformedBounds(var1, var2);
         var1.intersectWith((float)var16, (float)var5, (float)var11, (float)var7, (float)var9, (float)var13);
      }

      return var1;
   }

   private void updateLocalBounds() {
      if (this.localBoundsInvalid) {
         if (this.getClip() == null && this.getEffect() == null) {
            this.localBounds = null;
         } else {
            this.localBounds = this.computeLocalBounds(
               (BaseBounds)(this.localBounds == null ? new RectBounds() : this.localBounds), BaseTransform.IDENTITY_TRANSFORM
            );
         }

         this.localBoundsInvalid = false;
      }
   }

   void updateTxBounds() {
      if (this.txBoundsInvalid) {
         this.updateLocalToParentTransform();
         this.txBounds = this.getLocalBounds(this.txBounds, this.localToParentTx);
         this.txBoundsInvalid = false;
      }
   }

   private void doGeomChanged() {
      if (this.geomBoundsInvalid) {
         NodeHelper.notifyLayoutBoundsChanged(this);
         this.transformedBoundsChanged();
      } else {
         this.geomBounds.makeEmpty();
         this.geomBoundsInvalid = true;
         NodeHelper.markDirty(this, DirtyBits.NODE_BOUNDS);
         NodeHelper.notifyLayoutBoundsChanged(this);
         this.localBoundsChanged();
      }
   }

   void localBoundsChanged() {
      this.localBoundsInvalid = true;
      this.invalidateBoundsInLocal();
      this.transformedBoundsChanged();
   }

   void transformedBoundsChanged() {
      if (!this.txBoundsInvalid) {
         this.txBounds.makeEmpty();
         this.txBoundsInvalid = true;
         this.invalidateBoundsInParent();
         NodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORMED_BOUNDS);
      }

      if (this.isVisible()) {
         this.notifyParentOfBoundsChange();
      }
   }

   private void doNotifyLayoutBoundsChanged() {
      this.layoutBoundsChanged();
      Parent var1 = this.getParent();
      if (this.isManaged() && var1 != null && (!(var1 instanceof Group) || this.isResizable()) && !var1.isPerformingLayout()) {
         var1.requestLayout(true);
      }
   }

   void notifyParentOfBoundsChange() {
      Parent var1 = this.getParent();
      if (var1 != null) {
         var1.childBoundsChanged(this);
      }

      if (this.clipParent != null) {
         this.clipParent.localBoundsChanged();
      }
   }

   public boolean contains(double var1, double var3) {
      return !this.containsBounds(var1, var3) ? false : this.isPickOnBounds() || NodeHelper.computeContains(this, var1, var3);
   }

   private boolean containsBounds(double var1, double var3) {
      TempState var5 = TempState.getInstance();
      BaseBounds var6 = var5.bounds;
      var6 = this.getLocalBounds(var6, BaseTransform.IDENTITY_TRANSFORM);
      if (var6.contains((float)var1, (float)var3)) {
         if (this.getClip() != null) {
            var5.point.x = (float)var1;
            var5.point.y = (float)var3;

            try {
               this.getClip().parentToLocal(var5.point);
            } catch (NoninvertibleTransformException var8) {
               return false;
            }

            if (!this.getClip().contains((double)var5.point.x, (double)var5.point.y)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean contains(Point2D var1) {
      return this.contains(var1.getX(), var1.getY());
   }

   public boolean intersects(double var1, double var3, double var5, double var7) {
      BaseBounds var9 = TempState.getInstance().bounds;
      var9 = this.getLocalBounds(var9, BaseTransform.IDENTITY_TRANSFORM);
      return var9.intersects((float)var1, (float)var3, (float)var5, (float)var7);
   }

   public boolean intersects(Bounds var1) {
      return this.intersects(var1.getMinX(), var1.getMinY(), var1.getWidth(), var1.getHeight());
   }

   public Point2D screenToLocal(double var1, double var3) {
      Scene var5 = this.getScene();
      if (var5 == null) {
         return null;
      } else {
         Window var6 = var5.getWindow();
         if (var6 == null) {
            return null;
         } else {
            com.sun.javafx.geom.Point2D var7 = TempState.getInstance().point;
            var7.setLocation((float)(var1 - var5.getX() - var6.getX()), (float)(var3 - var5.getY() - var6.getY()));
            SubScene var8 = this.getSubScene();
            if (var8 != null) {
               Point2D var9 = SceneUtils.sceneToSubScenePlane(var8, new Point2D((double)var7.x, (double)var7.y));
               if (var9 == null) {
                  return null;
               }

               var7.setLocation((float)var9.getX(), (float)var9.getY());
            }

            Point3D var12 = var5.getEffectiveCamera().pickProjectPlane((double)var7.x, (double)var7.y);
            var7.setLocation((float)var12.getX(), (float)var12.getY());

            try {
               this.sceneToLocal(var7);
            } catch (NoninvertibleTransformException var11) {
               return null;
            }

            return new Point2D((double)var7.x, (double)var7.y);
         }
      }
   }

   public Point2D screenToLocal(Point2D var1) {
      return this.screenToLocal(var1.getX(), var1.getY());
   }

   public Bounds screenToLocal(Bounds var1) {
      Point2D var2 = this.screenToLocal(var1.getMinX(), var1.getMinY());
      Point2D var3 = this.screenToLocal(var1.getMinX(), var1.getMaxY());
      Point2D var4 = this.screenToLocal(var1.getMaxX(), var1.getMinY());
      Point2D var5 = this.screenToLocal(var1.getMaxX(), var1.getMaxY());
      return BoundsUtils.createBoundingBox(var2, var3, var4, var5);
   }

   public Point2D sceneToLocal(double var1, double var3, boolean var5) {
      if (!var5) {
         return this.sceneToLocal(var1, var3);
      } else {
         com.sun.javafx.geom.Point2D var6 = TempState.getInstance().point;
         var6.setLocation((float)var1, (float)var3);
         SubScene var7 = this.getSubScene();
         if (var7 != null) {
            Point2D var8 = SceneUtils.sceneToSubScenePlane(var7, new Point2D((double)var6.x, (double)var6.y));
            if (var8 == null) {
               return null;
            }

            var6.setLocation((float)var8.getX(), (float)var8.getY());
         }

         try {
            this.sceneToLocal(var6);
            return new Point2D((double)var6.x, (double)var6.y);
         } catch (NoninvertibleTransformException var9) {
            return null;
         }
      }
   }

   public Point2D sceneToLocal(Point2D var1, boolean var2) {
      return this.sceneToLocal(var1.getX(), var1.getY(), var2);
   }

   public Bounds sceneToLocal(Bounds var1, boolean var2) {
      if (!var2) {
         return this.sceneToLocal(var1);
      } else if (var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var3 = this.sceneToLocal(var1.getMinX(), var1.getMinY(), true);
         Point2D var4 = this.sceneToLocal(var1.getMinX(), var1.getMaxY(), true);
         Point2D var5 = this.sceneToLocal(var1.getMaxX(), var1.getMinY(), true);
         Point2D var6 = this.sceneToLocal(var1.getMaxX(), var1.getMaxY(), true);
         return BoundsUtils.createBoundingBox(var3, var4, var5, var6);
      } else {
         return null;
      }
   }

   public Point2D sceneToLocal(double var1, double var3) {
      com.sun.javafx.geom.Point2D var5 = TempState.getInstance().point;
      var5.setLocation((float)var1, (float)var3);

      try {
         this.sceneToLocal(var5);
      } catch (NoninvertibleTransformException var7) {
         return null;
      }

      return new Point2D((double)var5.x, (double)var5.y);
   }

   public Point2D sceneToLocal(Point2D var1) {
      return this.sceneToLocal(var1.getX(), var1.getY());
   }

   public Point3D sceneToLocal(Point3D var1) {
      return this.sceneToLocal(var1.getX(), var1.getY(), var1.getZ());
   }

   public Point3D sceneToLocal(double var1, double var3, double var5) {
      try {
         return this.sceneToLocal0(var1, var3, var5);
      } catch (NoninvertibleTransformException var8) {
         return null;
      }
   }

   private Point3D sceneToLocal0(double var1, double var3, double var5) throws NoninvertibleTransformException {
      Vec3d var7 = TempState.getInstance().vec3d;
      var7.set(var1, var3, var5);
      this.sceneToLocal(var7);
      return new Point3D(var7.x, var7.y, var7.z);
   }

   public Bounds sceneToLocal(Bounds var1) {
      this.updateLocalToParentTransform();
      if (this.localToParentTx.is2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var11 = this.sceneToLocal(var1.getMinX(), var1.getMinY());
         Point2D var12 = this.sceneToLocal(var1.getMaxX(), var1.getMinY());
         Point2D var13 = this.sceneToLocal(var1.getMaxX(), var1.getMaxY());
         Point2D var14 = this.sceneToLocal(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var11, var12, var13, var14);
      } else {
         try {
            Point3D var2 = this.sceneToLocal0(var1.getMinX(), var1.getMinY(), var1.getMinZ());
            Point3D var3 = this.sceneToLocal0(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
            Point3D var4 = this.sceneToLocal0(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
            Point3D var5 = this.sceneToLocal0(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
            Point3D var6 = this.sceneToLocal0(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
            Point3D var7 = this.sceneToLocal0(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
            Point3D var8 = this.sceneToLocal0(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
            Point3D var9 = this.sceneToLocal0(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
            return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
         } catch (NoninvertibleTransformException var10) {
            return null;
         }
      }
   }

   public Point2D localToScreen(double var1, double var3) {
      return this.localToScreen(var1, var3, 0.0);
   }

   public Point2D localToScreen(Point2D var1) {
      return this.localToScreen(var1.getX(), var1.getY());
   }

   public Point2D localToScreen(double var1, double var3, double var5) {
      Scene var7 = this.getScene();
      if (var7 == null) {
         return null;
      } else {
         Window var8 = var7.getWindow();
         if (var8 == null) {
            return null;
         } else {
            Point3D var9 = this.localToScene(var1, var3, var5);
            SubScene var10 = this.getSubScene();
            if (var10 != null) {
               var9 = SceneUtils.subSceneToScene(var10, var9);
            }

            Point2D var11 = CameraHelper.project(SceneHelper.getEffectiveCamera(this.getScene()), var9);
            return new Point2D(var11.getX() + var7.getX() + var8.getX(), var11.getY() + var7.getY() + var8.getY());
         }
      }
   }

   public Point2D localToScreen(Point3D var1) {
      return this.localToScreen(var1.getX(), var1.getY(), var1.getZ());
   }

   public Bounds localToScreen(Bounds var1) {
      Point2D var2 = this.localToScreen(var1.getMinX(), var1.getMinY(), var1.getMinZ());
      Point2D var3 = this.localToScreen(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
      Point2D var4 = this.localToScreen(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
      Point2D var5 = this.localToScreen(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
      Point2D var6 = this.localToScreen(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
      Point2D var7 = this.localToScreen(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
      Point2D var8 = this.localToScreen(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
      Point2D var9 = this.localToScreen(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
      return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public Point2D localToScene(double var1, double var3) {
      com.sun.javafx.geom.Point2D var5 = TempState.getInstance().point;
      var5.setLocation((float)var1, (float)var3);
      this.localToScene(var5);
      return new Point2D((double)var5.x, (double)var5.y);
   }

   public Point2D localToScene(Point2D var1) {
      return this.localToScene(var1.getX(), var1.getY());
   }

   public Point3D localToScene(Point3D var1) {
      return this.localToScene(var1.getX(), var1.getY(), var1.getZ());
   }

   public Point3D localToScene(double var1, double var3, double var5) {
      Vec3d var7 = TempState.getInstance().vec3d;
      var7.set(var1, var3, var5);
      this.localToScene(var7);
      return new Point3D(var7.x, var7.y, var7.z);
   }

   public Point3D localToScene(Point3D var1, boolean var2) {
      Point3D var3 = this.localToScene(var1);
      if (var2) {
         SubScene var4 = this.getSubScene();
         if (var4 != null) {
            var3 = SceneUtils.subSceneToScene(var4, var3);
         }
      }

      return var3;
   }

   public Point3D localToScene(double var1, double var3, double var5, boolean var7) {
      return this.localToScene(new Point3D(var1, var3, var5), var7);
   }

   public Point2D localToScene(Point2D var1, boolean var2) {
      if (!var2) {
         return this.localToScene(var1);
      } else {
         Point3D var3 = this.localToScene(var1.getX(), var1.getY(), 0.0, var2);
         return new Point2D(var3.getX(), var3.getY());
      }
   }

   public Point2D localToScene(double var1, double var3, boolean var5) {
      return this.localToScene(new Point2D(var1, var3), var5);
   }

   public Bounds localToScene(Bounds var1, boolean var2) {
      if (!var2) {
         return this.localToScene(var1);
      } else {
         Point3D var3 = this.localToScene(var1.getMinX(), var1.getMinY(), var1.getMinZ(), true);
         Point3D var4 = this.localToScene(var1.getMinX(), var1.getMinY(), var1.getMaxZ(), true);
         Point3D var5 = this.localToScene(var1.getMinX(), var1.getMaxY(), var1.getMinZ(), true);
         Point3D var6 = this.localToScene(var1.getMinX(), var1.getMaxY(), var1.getMaxZ(), true);
         Point3D var7 = this.localToScene(var1.getMaxX(), var1.getMaxY(), var1.getMinZ(), true);
         Point3D var8 = this.localToScene(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ(), true);
         Point3D var9 = this.localToScene(var1.getMaxX(), var1.getMinY(), var1.getMinZ(), true);
         Point3D var10 = this.localToScene(var1.getMaxX(), var1.getMinY(), var1.getMaxZ(), true);
         return BoundsUtils.createBoundingBox(var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   public Bounds localToScene(Bounds var1) {
      this.updateLocalToParentTransform();
      if (this.localToParentTx.is2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var10 = this.localToScene(var1.getMinX(), var1.getMinY());
         Point2D var11 = this.localToScene(var1.getMaxX(), var1.getMinY());
         Point2D var12 = this.localToScene(var1.getMaxX(), var1.getMaxY());
         Point2D var13 = this.localToScene(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var10, var11, var12, var13);
      } else {
         Point3D var2 = this.localToScene(var1.getMinX(), var1.getMinY(), var1.getMinZ());
         Point3D var3 = this.localToScene(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
         Point3D var4 = this.localToScene(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
         Point3D var5 = this.localToScene(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var6 = this.localToScene(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
         Point3D var7 = this.localToScene(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var8 = this.localToScene(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
         Point3D var9 = this.localToScene(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
         return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public Point2D parentToLocal(double var1, double var3) {
      com.sun.javafx.geom.Point2D var5 = TempState.getInstance().point;
      var5.setLocation((float)var1, (float)var3);

      try {
         this.parentToLocal(var5);
      } catch (NoninvertibleTransformException var7) {
         return null;
      }

      return new Point2D((double)var5.x, (double)var5.y);
   }

   public Point2D parentToLocal(Point2D var1) {
      return this.parentToLocal(var1.getX(), var1.getY());
   }

   public Point3D parentToLocal(Point3D var1) {
      return this.parentToLocal(var1.getX(), var1.getY(), var1.getZ());
   }

   public Point3D parentToLocal(double var1, double var3, double var5) {
      Vec3d var7 = TempState.getInstance().vec3d;
      var7.set(var1, var3, var5);

      try {
         this.parentToLocal(var7);
      } catch (NoninvertibleTransformException var9) {
         return null;
      }

      return new Point3D(var7.x, var7.y, var7.z);
   }

   public Bounds parentToLocal(Bounds var1) {
      this.updateLocalToParentTransform();
      if (this.localToParentTx.is2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var10 = this.parentToLocal(var1.getMinX(), var1.getMinY());
         Point2D var11 = this.parentToLocal(var1.getMaxX(), var1.getMinY());
         Point2D var12 = this.parentToLocal(var1.getMaxX(), var1.getMaxY());
         Point2D var13 = this.parentToLocal(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var10, var11, var12, var13);
      } else {
         Point3D var2 = this.parentToLocal(var1.getMinX(), var1.getMinY(), var1.getMinZ());
         Point3D var3 = this.parentToLocal(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
         Point3D var4 = this.parentToLocal(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
         Point3D var5 = this.parentToLocal(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var6 = this.parentToLocal(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
         Point3D var7 = this.parentToLocal(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var8 = this.parentToLocal(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
         Point3D var9 = this.parentToLocal(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
         return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public Point2D localToParent(double var1, double var3) {
      com.sun.javafx.geom.Point2D var5 = TempState.getInstance().point;
      var5.setLocation((float)var1, (float)var3);
      this.localToParent(var5);
      return new Point2D((double)var5.x, (double)var5.y);
   }

   public Point2D localToParent(Point2D var1) {
      return this.localToParent(var1.getX(), var1.getY());
   }

   public Point3D localToParent(Point3D var1) {
      return this.localToParent(var1.getX(), var1.getY(), var1.getZ());
   }

   public Point3D localToParent(double var1, double var3, double var5) {
      Vec3d var7 = TempState.getInstance().vec3d;
      var7.set(var1, var3, var5);
      this.localToParent(var7);
      return new Point3D(var7.x, var7.y, var7.z);
   }

   public Bounds localToParent(Bounds var1) {
      this.updateLocalToParentTransform();
      if (this.localToParentTx.is2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var10 = this.localToParent(var1.getMinX(), var1.getMinY());
         Point2D var11 = this.localToParent(var1.getMaxX(), var1.getMinY());
         Point2D var12 = this.localToParent(var1.getMaxX(), var1.getMaxY());
         Point2D var13 = this.localToParent(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var10, var11, var12, var13);
      } else {
         Point3D var2 = this.localToParent(var1.getMinX(), var1.getMinY(), var1.getMinZ());
         Point3D var3 = this.localToParent(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
         Point3D var4 = this.localToParent(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
         Point3D var5 = this.localToParent(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var6 = this.localToParent(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
         Point3D var7 = this.localToParent(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var8 = this.localToParent(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
         Point3D var9 = this.localToParent(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
         return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   BaseTransform getLocalToParentTransform(BaseTransform var1) {
      this.updateLocalToParentTransform();
      var1.setTransform(this.localToParentTx);
      return var1;
   }

   final BaseTransform getLeafTransform() {
      return this.getLocalToParentTransform(TempState.getInstance().leafTx);
   }

   private void doTransformsChanged() {
      if (!this.transformDirty) {
         NodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORM);
         this.transformDirty = true;
         this.transformedBoundsChanged();
      }

      this.invalidateLocalToParentTransform();
      this.invalidateLocalToSceneTransform();
   }

   final double getPivotX() {
      Bounds var1 = this.getLayoutBounds();
      return var1.getMinX() + var1.getWidth() / 2.0;
   }

   final double getPivotY() {
      Bounds var1 = this.getLayoutBounds();
      return var1.getMinY() + var1.getHeight() / 2.0;
   }

   final double getPivotZ() {
      Bounds var1 = this.getLayoutBounds();
      return var1.getMinZ() + var1.getDepth() / 2.0;
   }

   void updateLocalToParentTransform() {
      if (this.transformDirty) {
         this.localToParentTx.setToIdentity();
         boolean var1 = false;
         double var2 = 0.0;
         if (this.hasMirroring()) {
            Scene var4 = this.getScene();
            if (var4 != null && var4.getRoot() == this) {
               var2 = var4.getWidth() / 2.0;
               if (var2 == 0.0) {
                  var2 = this.getPivotX();
               }

               this.localToParentTx = this.localToParentTx.deriveWithTranslation(var2, 0.0);
               this.localToParentTx = this.localToParentTx.deriveWithScale(-1.0, 1.0, 1.0);
               this.localToParentTx = this.localToParentTx.deriveWithTranslation(-var2, 0.0);
            } else {
               var1 = true;
               var2 = this.getPivotX();
            }
         }

         if (this.getScaleX() == 1.0 && this.getScaleY() == 1.0 && this.getScaleZ() == 1.0 && this.getRotate() == 0.0) {
            this.localToParentTx = this.localToParentTx
               .deriveWithTranslation(this.getTranslateX() + this.getLayoutX(), this.getTranslateY() + this.getLayoutY(), this.getTranslateZ());
         } else {
            double var10 = this.getPivotX();
            double var6 = this.getPivotY();
            double var8 = this.getPivotZ();
            this.localToParentTx = this.localToParentTx
               .deriveWithTranslation(
                  this.getTranslateX() + this.getLayoutX() + var10, this.getTranslateY() + this.getLayoutY() + var6, this.getTranslateZ() + var8
               );
            this.localToParentTx = this.localToParentTx
               .deriveWithRotation(
                  Math.toRadians(this.getRotate()), this.getRotationAxis().getX(), this.getRotationAxis().getY(), this.getRotationAxis().getZ()
               );
            this.localToParentTx = this.localToParentTx.deriveWithScale(this.getScaleX(), this.getScaleY(), this.getScaleZ());
            this.localToParentTx = this.localToParentTx.deriveWithTranslation(-var10, -var6, -var8);
         }

         if (this.hasTransforms()) {
            for (Transform var5 : this.getTransforms()) {
               this.localToParentTx = TransformHelper.derive(var5, this.localToParentTx);
            }
         }

         if (var1) {
            this.localToParentTx = this.localToParentTx.deriveWithTranslation(var2, 0.0);
            this.localToParentTx = this.localToParentTx.deriveWithScale(-1.0, 1.0, 1.0);
            this.localToParentTx = this.localToParentTx.deriveWithTranslation(-var2, 0.0);
         }

         this.transformDirty = false;
      }
   }

   void parentToLocal(com.sun.javafx.geom.Point2D var1) throws NoninvertibleTransformException {
      this.updateLocalToParentTransform();
      this.localToParentTx.inverseTransform(var1, var1);
   }

   void parentToLocal(Vec3d var1) throws NoninvertibleTransformException {
      this.updateLocalToParentTransform();
      this.localToParentTx.inverseTransform(var1, var1);
   }

   void sceneToLocal(com.sun.javafx.geom.Point2D var1) throws NoninvertibleTransformException {
      if (this.getParent() != null) {
         this.getParent().sceneToLocal(var1);
      }

      this.parentToLocal(var1);
   }

   void sceneToLocal(Vec3d var1) throws NoninvertibleTransformException {
      if (this.getParent() != null) {
         this.getParent().sceneToLocal(var1);
      }

      this.parentToLocal(var1);
   }

   void localToScene(com.sun.javafx.geom.Point2D var1) {
      this.localToParent(var1);
      if (this.getParent() != null) {
         this.getParent().localToScene(var1);
      }
   }

   void localToScene(Vec3d var1) {
      this.localToParent(var1);
      if (this.getParent() != null) {
         this.getParent().localToScene(var1);
      }
   }

   void localToParent(com.sun.javafx.geom.Point2D var1) {
      this.updateLocalToParentTransform();
      this.localToParentTx.transform(var1, var1);
   }

   void localToParent(Vec3d var1) {
      this.updateLocalToParentTransform();
      this.localToParentTx.transform(var1, var1);
   }

   private void doPickNodeLocal(PickRay var1, PickResultChooser var2) {
      this.intersects(var1, var2);
   }

   final void pickNode(PickRay var1, PickResultChooser var2) {
      if (this.isVisible() && !this.isDisable() && !this.isMouseTransparent()) {
         Vec3d var3 = var1.getOriginNoClone();
         double var4 = var3.x;
         double var6 = var3.y;
         double var8 = var3.z;
         Vec3d var10 = var1.getDirectionNoClone();
         double var11 = var10.x;
         double var13 = var10.y;
         double var15 = var10.z;
         this.updateLocalToParentTransform();

         try {
            this.localToParentTx.inverseTransform(var3, var3);
            this.localToParentTx.inverseDeltaTransform(var10, var10);
            NodeHelper.pickNodeLocal(this, var1, var2);
         } catch (NoninvertibleTransformException var18) {
         }

         var1.setOrigin(var4, var6, var8);
         var1.setDirection(var11, var13, var15);
      }
   }

   final boolean intersects(PickRay var1, PickResultChooser var2) {
      double var3 = this.intersectsBounds(var1);
      if (!Double.isNaN(var3)) {
         if (this.isPickOnBounds()) {
            if (var2 != null) {
               var2.offer(this, var3, PickResultChooser.computePoint(var1, var3));
            }

            return true;
         } else {
            return NodeHelper.computeIntersects(this, var1, var2);
         }
      } else {
         return false;
      }
   }

   private boolean doComputeIntersects(PickRay var1, PickResultChooser var2) {
      double var3 = var1.getOriginNoClone().z;
      double var5 = var1.getDirectionNoClone().z;
      if (almostZero(var5)) {
         return false;
      } else {
         double var7 = -var3 / var5;
         if (!(var7 < var1.getNearClip()) && !(var7 > var1.getFarClip())) {
            double var9 = var1.getOriginNoClone().x + var1.getDirectionNoClone().x * var7;
            double var11 = var1.getOriginNoClone().y + var1.getDirectionNoClone().y * var7;
            if (this.contains((double)((float)var9), (double)((float)var11))) {
               if (var2 != null) {
                  var2.offer(this, var7, PickResultChooser.computePoint(var1, var7));
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   final double intersectsBounds(PickRay var1) {
      Vec3d var2 = var1.getDirectionNoClone();
      Vec3d var7 = var1.getOriginNoClone();
      double var8 = var7.x;
      double var10 = var7.y;
      double var12 = var7.z;
      TempState var14 = TempState.getInstance();
      BaseBounds var15 = var14.bounds;
      var15 = this.getLocalBounds(var15, BaseTransform.IDENTITY_TRANSFORM);
      double var3;
      double var5;
      if (var2.x == 0.0 && var2.y == 0.0) {
         if (var2.z == 0.0) {
            return Double.NaN;
         }

         if (var8 < (double)var15.getMinX() || var8 > (double)var15.getMaxX() || var10 < (double)var15.getMinY() || var10 > (double)var15.getMaxY()) {
            return Double.NaN;
         }

         double var44 = 1.0 / var2.z;
         boolean var48 = var44 < 0.0;
         double var19 = (double)var15.getMinZ();
         double var21 = (double)var15.getMaxZ();
         var3 = ((var48 ? var21 : var19) - var12) * var44;
         var5 = ((var48 ? var19 : var21) - var12) * var44;
      } else if ((double)var15.getDepth() == 0.0) {
         if (almostZero(var2.z)) {
            return Double.NaN;
         }

         double var16 = ((double)var15.getMinZ() - var12) / var2.z;
         double var18 = var8 + var2.x * var16;
         double var20 = var10 + var2.y * var16;
         if (var18 < (double)var15.getMinX() || var18 > (double)var15.getMaxX() || var20 < (double)var15.getMinY() || var20 > (double)var15.getMaxY()) {
            return Double.NaN;
         }

         var5 = var16;
         var3 = var16;
      } else {
         double var43 = var2.x == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var2.x;
         double var47 = var2.y == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var2.y;
         double var51 = var2.z == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var2.z;
         boolean var22 = var43 < 0.0;
         boolean var23 = var47 < 0.0;
         boolean var24 = var51 < 0.0;
         double var25 = (double)var15.getMinX();
         double var27 = (double)var15.getMinY();
         double var29 = (double)var15.getMaxX();
         double var31 = (double)var15.getMaxY();
         var3 = Double.NEGATIVE_INFINITY;
         var5 = Double.POSITIVE_INFINITY;
         if (Double.isInfinite(var43)) {
            if (!(var25 <= var8) || !(var29 >= var8)) {
               return Double.NaN;
            }
         } else {
            var3 = ((var22 ? var29 : var25) - var8) * var43;
            var5 = ((var22 ? var25 : var29) - var8) * var43;
         }

         if (Double.isInfinite(var47)) {
            if (!(var27 <= var10) || !(var31 >= var10)) {
               return Double.NaN;
            }
         } else {
            double var33 = ((var23 ? var31 : var27) - var10) * var47;
            double var35 = ((var23 ? var27 : var31) - var10) * var47;
            if (var3 > var35 || var33 > var5) {
               return Double.NaN;
            }

            if (var33 > var3) {
               var3 = var33;
            }

            if (var35 < var5) {
               var5 = var35;
            }
         }

         double var55 = (double)var15.getMinZ();
         double var56 = (double)var15.getMaxZ();
         if (Double.isInfinite(var51)) {
            if (!(var55 <= var12) || !(var56 >= var12)) {
               return Double.NaN;
            }
         } else {
            double var37 = ((var24 ? var56 : var55) - var12) * var51;
            double var39 = ((var24 ? var55 : var56) - var12) * var51;
            if (var3 > var39 || var37 > var5) {
               return Double.NaN;
            }

            if (var37 > var3) {
               var3 = var37;
            }

            if (var39 < var5) {
               var5 = var39;
            }
         }
      }

      Node var45 = this.getClip();
      if (var45 != null && !(this instanceof Shape3D) && !(var45 instanceof Shape3D)) {
         double var17 = var2.x;
         double var49 = var2.y;
         double var52 = var2.z;
         var45.updateLocalToParentTransform();
         boolean var53 = true;

         try {
            var45.localToParentTx.inverseTransform(var7, var7);
            var45.localToParentTx.inverseDeltaTransform(var2, var2);
         } catch (NoninvertibleTransformException var41) {
            var53 = false;
         }

         var53 = var53 && var45.intersects(var1, null);
         var1.setOrigin(var8, var10, var12);
         var1.setDirection(var17, var49, var52);
         if (!var53) {
            return Double.NaN;
         }
      }

      if (!Double.isInfinite(var3) && !Double.isNaN(var3)) {
         double var46 = var1.getNearClip();
         double var50 = var1.getFarClip();
         if (var3 < var46) {
            return var5 >= var46 ? 0.0 : Double.NaN;
         } else {
            return var3 > var50 ? Double.NaN : var3;
         }
      } else {
         return Double.NaN;
      }
   }

   static boolean almostZero(double var0) {
      return var0 < 1.0E-5 && var0 > -1.0E-5;
   }

   public final DoubleProperty viewOrderProperty() {
      return this.getMiscProperties().viewOrderProperty();
   }

   public final void setViewOrder(double var1) {
      this.viewOrderProperty().set(var1);
   }

   public final double getViewOrder() {
      return this.miscProperties == null ? 0.0 : this.miscProperties.getViewOrder();
   }

   public final ObservableList<Transform> getTransforms() {
      return this.transformsProperty();
   }

   private ObservableList<Transform> transformsProperty() {
      return this.getNodeTransformation().getTransforms();
   }

   public final void setTranslateX(double var1) {
      this.translateXProperty().set(var1);
   }

   public final double getTranslateX() {
      return this.nodeTransformation == null ? 0.0 : this.nodeTransformation.getTranslateX();
   }

   public final DoubleProperty translateXProperty() {
      return this.getNodeTransformation().translateXProperty();
   }

   public final void setTranslateY(double var1) {
      this.translateYProperty().set(var1);
   }

   public final double getTranslateY() {
      return this.nodeTransformation == null ? 0.0 : this.nodeTransformation.getTranslateY();
   }

   public final DoubleProperty translateYProperty() {
      return this.getNodeTransformation().translateYProperty();
   }

   public final void setTranslateZ(double var1) {
      this.translateZProperty().set(var1);
   }

   public final double getTranslateZ() {
      return this.nodeTransformation == null ? 0.0 : this.nodeTransformation.getTranslateZ();
   }

   public final DoubleProperty translateZProperty() {
      return this.getNodeTransformation().translateZProperty();
   }

   public final void setScaleX(double var1) {
      this.scaleXProperty().set(var1);
   }

   public final double getScaleX() {
      return this.nodeTransformation == null ? 1.0 : this.nodeTransformation.getScaleX();
   }

   public final DoubleProperty scaleXProperty() {
      return this.getNodeTransformation().scaleXProperty();
   }

   public final void setScaleY(double var1) {
      this.scaleYProperty().set(var1);
   }

   public final double getScaleY() {
      return this.nodeTransformation == null ? 1.0 : this.nodeTransformation.getScaleY();
   }

   public final DoubleProperty scaleYProperty() {
      return this.getNodeTransformation().scaleYProperty();
   }

   public final void setScaleZ(double var1) {
      this.scaleZProperty().set(var1);
   }

   public final double getScaleZ() {
      return this.nodeTransformation == null ? 1.0 : this.nodeTransformation.getScaleZ();
   }

   public final DoubleProperty scaleZProperty() {
      return this.getNodeTransformation().scaleZProperty();
   }

   public final void setRotate(double var1) {
      this.rotateProperty().set(var1);
   }

   public final double getRotate() {
      return this.nodeTransformation == null ? 0.0 : this.nodeTransformation.getRotate();
   }

   public final DoubleProperty rotateProperty() {
      return this.getNodeTransformation().rotateProperty();
   }

   public final void setRotationAxis(Point3D var1) {
      this.rotationAxisProperty().set(var1);
   }

   public final Point3D getRotationAxis() {
      return this.nodeTransformation == null ? DEFAULT_ROTATION_AXIS : this.nodeTransformation.getRotationAxis();
   }

   public final ObjectProperty<Point3D> rotationAxisProperty() {
      return this.getNodeTransformation().rotationAxisProperty();
   }

   public final ReadOnlyObjectProperty<Transform> localToParentTransformProperty() {
      return this.getNodeTransformation().localToParentTransformProperty();
   }

   private void invalidateLocalToParentTransform() {
      if (this.nodeTransformation != null) {
         this.nodeTransformation.invalidateLocalToParentTransform();
      }
   }

   public final Transform getLocalToParentTransform() {
      return (Transform)this.localToParentTransformProperty().get();
   }

   public final ReadOnlyObjectProperty<Transform> localToSceneTransformProperty() {
      return this.getNodeTransformation().localToSceneTransformProperty();
   }

   private void invalidateLocalToSceneTransform() {
      if (this.nodeTransformation != null) {
         this.nodeTransformation.invalidateLocalToSceneTransform();
      }
   }

   public final Transform getLocalToSceneTransform() {
      return (Transform)this.localToSceneTransformProperty().get();
   }

   private Node.NodeTransformation getNodeTransformation() {
      if (this.nodeTransformation == null) {
         this.nodeTransformation = new Node.NodeTransformation();
      }

      return this.nodeTransformation;
   }

   private boolean hasTransforms() {
      return this.nodeTransformation != null && this.nodeTransformation.hasTransforms();
   }

   Transform getCurrentLocalToSceneTransformState() {
      return this.nodeTransformation != null && this.nodeTransformation.localToSceneTransform != null
         ? this.nodeTransformation.localToSceneTransform.transform
         : null;
   }

   private EventHandlerProperties getEventHandlerProperties() {
      if (this.eventHandlerProperties == null) {
         this.eventHandlerProperties = new EventHandlerProperties(this.getInternalEventDispatcher().getEventHandlerManager(), this);
      }

      return this.eventHandlerProperties;
   }

   public final void setNodeOrientation(NodeOrientation var1) {
      this.nodeOrientationProperty().set(var1);
   }

   public final NodeOrientation getNodeOrientation() {
      return this.nodeOrientation == null ? NodeOrientation.INHERIT : (NodeOrientation)this.nodeOrientation.get();
   }

   public final ObjectProperty<NodeOrientation> nodeOrientationProperty() {
      if (this.nodeOrientation == null) {
         this.nodeOrientation = new StyleableObjectProperty<NodeOrientation>(NodeOrientation.INHERIT) {
            protected void invalidated() {
               Node.this.nodeResolvedOrientationInvalidated();
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "nodeOrientation";
            }

            @Override
            public CssMetaData getCssMetaData() {
               throw new UnsupportedOperationException("Not supported yet.");
            }
         };
      }

      return this.nodeOrientation;
   }

   public final NodeOrientation getEffectiveNodeOrientation() {
      return getEffectiveOrientation(this.resolvedNodeOrientation) == 0 ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT;
   }

   public final ReadOnlyObjectProperty<NodeOrientation> effectiveNodeOrientationProperty() {
      if (this.effectiveNodeOrientationProperty == null) {
         this.effectiveNodeOrientationProperty = new Node.EffectiveOrientationProperty();
      }

      return this.effectiveNodeOrientationProperty;
   }

   public boolean usesMirroring() {
      return true;
   }

   final void parentResolvedOrientationInvalidated() {
      if (this.getNodeOrientation() == NodeOrientation.INHERIT) {
         this.nodeResolvedOrientationInvalidated();
      } else {
         NodeHelper.transformsChanged(this);
      }
   }

   final void nodeResolvedOrientationInvalidated() {
      byte var1 = this.resolvedNodeOrientation;
      this.resolvedNodeOrientation = (byte)(this.calcEffectiveNodeOrientation() | this.calcAutomaticNodeOrientation());
      if (this.effectiveNodeOrientationProperty != null && getEffectiveOrientation(this.resolvedNodeOrientation) != getEffectiveOrientation(var1)) {
         this.effectiveNodeOrientationProperty.invalidate();
      }

      NodeHelper.transformsChanged(this);
      if (this.resolvedNodeOrientation != var1) {
         this.nodeResolvedOrientationChanged();
      }
   }

   void nodeResolvedOrientationChanged() {
   }

   private Node getMirroringOrientationParent() {
      for (Parent var1 = this.getParent(); var1 != null; var1 = var1.getParent()) {
         if (var1.usesMirroring()) {
            return var1;
         }
      }

      SubScene var2 = this.getSubScene();
      return var2 != null ? var2 : null;
   }

   private Node getOrientationParent() {
      Parent var1 = this.getParent();
      if (var1 != null) {
         return var1;
      } else {
         SubScene var2 = this.getSubScene();
         return var2 != null ? var2 : null;
      }
   }

   private byte calcEffectiveNodeOrientation() {
      NodeOrientation var1 = this.getNodeOrientation();
      if (var1 != NodeOrientation.INHERIT) {
         return (byte)(var1 == NodeOrientation.LEFT_TO_RIGHT ? 0 : 1);
      } else {
         Node var2 = this.getOrientationParent();
         if (var2 != null) {
            return getEffectiveOrientation(var2.resolvedNodeOrientation);
         } else {
            Scene var3 = this.getScene();
            if (var3 != null) {
               return (byte)(var3.getEffectiveNodeOrientation() == NodeOrientation.LEFT_TO_RIGHT ? 0 : 1);
            } else {
               return 0;
            }
         }
      }
   }

   private byte calcAutomaticNodeOrientation() {
      if (!this.usesMirroring()) {
         return 0;
      } else {
         NodeOrientation var1 = this.getNodeOrientation();
         if (var1 != NodeOrientation.INHERIT) {
            return (byte)(var1 == NodeOrientation.LEFT_TO_RIGHT ? 0 : 2);
         } else {
            Node var2 = this.getMirroringOrientationParent();
            if (var2 != null) {
               return getAutomaticOrientation(var2.resolvedNodeOrientation);
            } else {
               Scene var3 = this.getScene();
               if (var3 != null) {
                  return (byte)(var3.getEffectiveNodeOrientation() == NodeOrientation.LEFT_TO_RIGHT ? 0 : 2);
               } else {
                  return 0;
               }
            }
         }
      }
   }

   final boolean hasMirroring() {
      Node var1 = this.getOrientationParent();
      byte var2 = getAutomaticOrientation(this.resolvedNodeOrientation);
      byte var3 = var1 != null ? getAutomaticOrientation(var1.resolvedNodeOrientation) : 0;
      return var2 != var3;
   }

   private static byte getEffectiveOrientation(byte var0) {
      return (byte)(var0 & 1);
   }

   private static byte getAutomaticOrientation(byte var0) {
      return (byte)(var0 & 2);
   }

   private Node.MiscProperties getMiscProperties() {
      if (this.miscProperties == null) {
         this.miscProperties = new Node.MiscProperties();
      }

      return this.miscProperties;
   }

   public final void setMouseTransparent(boolean var1) {
      this.mouseTransparentProperty().set(var1);
   }

   public final boolean isMouseTransparent() {
      return this.miscProperties == null ? false : this.miscProperties.isMouseTransparent();
   }

   public final BooleanProperty mouseTransparentProperty() {
      return this.getMiscProperties().mouseTransparentProperty();
   }

   protected final void setHover(boolean var1) {
      this.hoverPropertyImpl().set(var1);
   }

   public final boolean isHover() {
      return this.hover == null ? false : this.hover.get();
   }

   public final ReadOnlyBooleanProperty hoverProperty() {
      return this.hoverPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper hoverPropertyImpl() {
      if (this.hover == null) {
         this.hover = new ReadOnlyBooleanWrapper() {
            protected void invalidated() {
               PlatformLogger var1 = Logging.getInputLogger();
               if (var1.isLoggable(Level.FINER)) {
                  var1.finer(this + " hover=" + this.get());
               }

               Node.this.pseudoClassStateChanged(Node.HOVER_PSEUDOCLASS_STATE, this.get());
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "hover";
            }
         };
      }

      return this.hover;
   }

   protected final void setPressed(boolean var1) {
      this.pressedPropertyImpl().set(var1);
   }

   public final boolean isPressed() {
      return this.pressed == null ? false : this.pressed.get();
   }

   public final ReadOnlyBooleanProperty pressedProperty() {
      return this.pressedPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper pressedPropertyImpl() {
      if (this.pressed == null) {
         this.pressed = new ReadOnlyBooleanWrapper() {
            protected void invalidated() {
               PlatformLogger var1 = Logging.getInputLogger();
               if (var1.isLoggable(Level.FINER)) {
                  var1.finer(this + " pressed=" + this.get());
               }

               Node.this.pseudoClassStateChanged(Node.PRESSED_PSEUDOCLASS_STATE, this.get());
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "pressed";
            }
         };
      }

      return this.pressed;
   }

   public final void setOnContextMenuRequested(EventHandler<? super ContextMenuEvent> var1) {
      this.onContextMenuRequestedProperty().set(var1);
   }

   public final EventHandler<? super ContextMenuEvent> getOnContextMenuRequested() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.onContextMenuRequested();
   }

   public final ObjectProperty<EventHandler<? super ContextMenuEvent>> onContextMenuRequestedProperty() {
      return this.getEventHandlerProperties().onContextMenuRequestedProperty();
   }

   public final void setOnMouseClicked(EventHandler<? super MouseEvent> var1) {
      this.onMouseClickedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseClicked() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseClicked();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseClickedProperty() {
      return this.getEventHandlerProperties().onMouseClickedProperty();
   }

   public final void setOnMouseDragged(EventHandler<? super MouseEvent> var1) {
      this.onMouseDraggedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseDragged() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseDragged();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseDraggedProperty() {
      return this.getEventHandlerProperties().onMouseDraggedProperty();
   }

   public final void setOnMouseEntered(EventHandler<? super MouseEvent> var1) {
      this.onMouseEnteredProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseEntered() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseEntered();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseEnteredProperty() {
      return this.getEventHandlerProperties().onMouseEnteredProperty();
   }

   public final void setOnMouseExited(EventHandler<? super MouseEvent> var1) {
      this.onMouseExitedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseExited() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseExited();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseExitedProperty() {
      return this.getEventHandlerProperties().onMouseExitedProperty();
   }

   public final void setOnMouseMoved(EventHandler<? super MouseEvent> var1) {
      this.onMouseMovedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseMoved() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseMoved();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseMovedProperty() {
      return this.getEventHandlerProperties().onMouseMovedProperty();
   }

   public final void setOnMousePressed(EventHandler<? super MouseEvent> var1) {
      this.onMousePressedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMousePressed() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMousePressed();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMousePressedProperty() {
      return this.getEventHandlerProperties().onMousePressedProperty();
   }

   public final void setOnMouseReleased(EventHandler<? super MouseEvent> var1) {
      this.onMouseReleasedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnMouseReleased() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseReleased();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseReleasedProperty() {
      return this.getEventHandlerProperties().onMouseReleasedProperty();
   }

   public final void setOnDragDetected(EventHandler<? super MouseEvent> var1) {
      this.onDragDetectedProperty().set(var1);
   }

   public final EventHandler<? super MouseEvent> getOnDragDetected() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnDragDetected();
   }

   public final ObjectProperty<EventHandler<? super MouseEvent>> onDragDetectedProperty() {
      return this.getEventHandlerProperties().onDragDetectedProperty();
   }

   public final void setOnMouseDragOver(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragOverProperty().set(var1);
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragOver() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseDragOver();
   }

   public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragOverProperty() {
      return this.getEventHandlerProperties().onMouseDragOverProperty();
   }

   public final void setOnMouseDragReleased(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragReleasedProperty().set(var1);
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragReleased() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseDragReleased();
   }

   public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragReleasedProperty() {
      return this.getEventHandlerProperties().onMouseDragReleasedProperty();
   }

   public final void setOnMouseDragEntered(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragEnteredProperty().set(var1);
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragEntered() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseDragEntered();
   }

   public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragEnteredProperty() {
      return this.getEventHandlerProperties().onMouseDragEnteredProperty();
   }

   public final void setOnMouseDragExited(EventHandler<? super MouseDragEvent> var1) {
      this.onMouseDragExitedProperty().set(var1);
   }

   public final EventHandler<? super MouseDragEvent> getOnMouseDragExited() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnMouseDragExited();
   }

   public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragExitedProperty() {
      return this.getEventHandlerProperties().onMouseDragExitedProperty();
   }

   public final void setOnScrollStarted(EventHandler<? super ScrollEvent> var1) {
      this.onScrollStartedProperty().set(var1);
   }

   public final EventHandler<? super ScrollEvent> getOnScrollStarted() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnScrollStarted();
   }

   public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollStartedProperty() {
      return this.getEventHandlerProperties().onScrollStartedProperty();
   }

   public final void setOnScroll(EventHandler<? super ScrollEvent> var1) {
      this.onScrollProperty().set(var1);
   }

   public final EventHandler<? super ScrollEvent> getOnScroll() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnScroll();
   }

   public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollProperty() {
      return this.getEventHandlerProperties().onScrollProperty();
   }

   public final void setOnScrollFinished(EventHandler<? super ScrollEvent> var1) {
      this.onScrollFinishedProperty().set(var1);
   }

   public final EventHandler<? super ScrollEvent> getOnScrollFinished() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnScrollFinished();
   }

   public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollFinishedProperty() {
      return this.getEventHandlerProperties().onScrollFinishedProperty();
   }

   public final void setOnRotationStarted(EventHandler<? super RotateEvent> var1) {
      this.onRotationStartedProperty().set(var1);
   }

   public final EventHandler<? super RotateEvent> getOnRotationStarted() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnRotationStarted();
   }

   public final ObjectProperty<EventHandler<? super RotateEvent>> onRotationStartedProperty() {
      return this.getEventHandlerProperties().onRotationStartedProperty();
   }

   public final void setOnRotate(EventHandler<? super RotateEvent> var1) {
      this.onRotateProperty().set(var1);
   }

   public final EventHandler<? super RotateEvent> getOnRotate() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnRotate();
   }

   public final ObjectProperty<EventHandler<? super RotateEvent>> onRotateProperty() {
      return this.getEventHandlerProperties().onRotateProperty();
   }

   public final void setOnRotationFinished(EventHandler<? super RotateEvent> var1) {
      this.onRotationFinishedProperty().set(var1);
   }

   public final EventHandler<? super RotateEvent> getOnRotationFinished() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnRotationFinished();
   }

   public final ObjectProperty<EventHandler<? super RotateEvent>> onRotationFinishedProperty() {
      return this.getEventHandlerProperties().onRotationFinishedProperty();
   }

   public final void setOnZoomStarted(EventHandler<? super ZoomEvent> var1) {
      this.onZoomStartedProperty().set(var1);
   }

   public final EventHandler<? super ZoomEvent> getOnZoomStarted() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnZoomStarted();
   }

   public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomStartedProperty() {
      return this.getEventHandlerProperties().onZoomStartedProperty();
   }

   public final void setOnZoom(EventHandler<? super ZoomEvent> var1) {
      this.onZoomProperty().set(var1);
   }

   public final EventHandler<? super ZoomEvent> getOnZoom() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnZoom();
   }

   public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomProperty() {
      return this.getEventHandlerProperties().onZoomProperty();
   }

   public final void setOnZoomFinished(EventHandler<? super ZoomEvent> var1) {
      this.onZoomFinishedProperty().set(var1);
   }

   public final EventHandler<? super ZoomEvent> getOnZoomFinished() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnZoomFinished();
   }

   public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomFinishedProperty() {
      return this.getEventHandlerProperties().onZoomFinishedProperty();
   }

   public final void setOnSwipeUp(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeUpProperty().set(var1);
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeUp() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnSwipeUp();
   }

   public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeUpProperty() {
      return this.getEventHandlerProperties().onSwipeUpProperty();
   }

   public final void setOnSwipeDown(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeDownProperty().set(var1);
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeDown() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnSwipeDown();
   }

   public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeDownProperty() {
      return this.getEventHandlerProperties().onSwipeDownProperty();
   }

   public final void setOnSwipeLeft(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeLeftProperty().set(var1);
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeLeft() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnSwipeLeft();
   }

   public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeLeftProperty() {
      return this.getEventHandlerProperties().onSwipeLeftProperty();
   }

   public final void setOnSwipeRight(EventHandler<? super SwipeEvent> var1) {
      this.onSwipeRightProperty().set(var1);
   }

   public final EventHandler<? super SwipeEvent> getOnSwipeRight() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnSwipeRight();
   }

   public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeRightProperty() {
      return this.getEventHandlerProperties().onSwipeRightProperty();
   }

   public final void setOnTouchPressed(EventHandler<? super TouchEvent> var1) {
      this.onTouchPressedProperty().set(var1);
   }

   public final EventHandler<? super TouchEvent> getOnTouchPressed() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnTouchPressed();
   }

   public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchPressedProperty() {
      return this.getEventHandlerProperties().onTouchPressedProperty();
   }

   public final void setOnTouchMoved(EventHandler<? super TouchEvent> var1) {
      this.onTouchMovedProperty().set(var1);
   }

   public final EventHandler<? super TouchEvent> getOnTouchMoved() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnTouchMoved();
   }

   public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchMovedProperty() {
      return this.getEventHandlerProperties().onTouchMovedProperty();
   }

   public final void setOnTouchReleased(EventHandler<? super TouchEvent> var1) {
      this.onTouchReleasedProperty().set(var1);
   }

   public final EventHandler<? super TouchEvent> getOnTouchReleased() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnTouchReleased();
   }

   public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchReleasedProperty() {
      return this.getEventHandlerProperties().onTouchReleasedProperty();
   }

   public final void setOnTouchStationary(EventHandler<? super TouchEvent> var1) {
      this.onTouchStationaryProperty().set(var1);
   }

   public final EventHandler<? super TouchEvent> getOnTouchStationary() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnTouchStationary();
   }

   public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchStationaryProperty() {
      return this.getEventHandlerProperties().onTouchStationaryProperty();
   }

   public final void setOnKeyPressed(EventHandler<? super KeyEvent> var1) {
      this.onKeyPressedProperty().set(var1);
   }

   public final EventHandler<? super KeyEvent> getOnKeyPressed() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnKeyPressed();
   }

   public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyPressedProperty() {
      return this.getEventHandlerProperties().onKeyPressedProperty();
   }

   public final void setOnKeyReleased(EventHandler<? super KeyEvent> var1) {
      this.onKeyReleasedProperty().set(var1);
   }

   public final EventHandler<? super KeyEvent> getOnKeyReleased() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnKeyReleased();
   }

   public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyReleasedProperty() {
      return this.getEventHandlerProperties().onKeyReleasedProperty();
   }

   public final void setOnKeyTyped(EventHandler<? super KeyEvent> var1) {
      this.onKeyTypedProperty().set(var1);
   }

   public final EventHandler<? super KeyEvent> getOnKeyTyped() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnKeyTyped();
   }

   public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyTypedProperty() {
      return this.getEventHandlerProperties().onKeyTypedProperty();
   }

   public final void setOnInputMethodTextChanged(EventHandler<? super InputMethodEvent> var1) {
      this.onInputMethodTextChangedProperty().set(var1);
   }

   public final EventHandler<? super InputMethodEvent> getOnInputMethodTextChanged() {
      return this.eventHandlerProperties == null ? null : this.eventHandlerProperties.getOnInputMethodTextChanged();
   }

   public final ObjectProperty<EventHandler<? super InputMethodEvent>> onInputMethodTextChangedProperty() {
      return this.getEventHandlerProperties().onInputMethodTextChangedProperty();
   }

   public final void setInputMethodRequests(InputMethodRequests var1) {
      this.inputMethodRequestsProperty().set(var1);
   }

   public final InputMethodRequests getInputMethodRequests() {
      return this.miscProperties == null ? DEFAULT_INPUT_METHOD_REQUESTS : this.miscProperties.getInputMethodRequests();
   }

   public final ObjectProperty<InputMethodRequests> inputMethodRequestsProperty() {
      return this.getMiscProperties().inputMethodRequestsProperty();
   }

   protected final void setFocused(boolean var1) {
      Node.FocusedProperty var2 = this.focusedPropertyImpl();
      if (var2.value != var1) {
         var2.store(var1);
         var2.notifyListeners();
      }
   }

   public final boolean isFocused() {
      return this.focused == null ? false : this.focused.get();
   }

   public final ReadOnlyBooleanProperty focusedProperty() {
      return this.focusedPropertyImpl();
   }

   private Node.FocusedProperty focusedPropertyImpl() {
      if (this.focused == null) {
         this.focused = new Node.FocusedProperty();
      }

      return this.focused;
   }

   public final void setFocusTraversable(boolean var1) {
      this.focusTraversableProperty().set(var1);
   }

   public final boolean isFocusTraversable() {
      return this.focusTraversable == null ? false : this.focusTraversable.get();
   }

   public final BooleanProperty focusTraversableProperty() {
      if (this.focusTraversable == null) {
         this.focusTraversable = new StyleableBooleanProperty(false) {
            public void invalidated() {
               Scene var1 = Node.this.getScene();
               if (var1 != null) {
                  if (this.get()) {
                     var1.initializeInternalEventDispatcher();
                  }

                  Node.this.focusSetDirty(var1);
               }
            }

            @Override
            public CssMetaData getCssMetaData() {
               return Node.StyleableProperties.FOCUS_TRAVERSABLE;
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "focusTraversable";
            }
         };
      }

      return this.focusTraversable;
   }

   private void focusSetDirty(Scene var1) {
      if (var1 != null && (this == var1.getFocusOwner() || this.isFocusTraversable())) {
         var1.setFocusDirty(true);
      }
   }

   public void requestFocus() {
      if (this.getScene() != null) {
         this.getScene().requestFocus(this);
      }
   }

   final boolean traverse(Direction var1) {
      return this.getScene() == null ? false : this.getScene().traverse(this, var1);
   }

   @Override
   public String toString() {
      String var1 = this.getClass().getName();
      String var2 = var1.substring(var1.lastIndexOf(46) + 1);
      StringBuilder var3 = new StringBuilder(var2);
      boolean var4 = this.id != null && !"".equals(this.getId());
      boolean var5 = !this.getStyleClass().isEmpty();
      if (!var4) {
         var3.append('@');
         var3.append(Integer.toHexString(this.hashCode()));
      } else {
         var3.append("[id=");
         var3.append(this.getId());
         if (!var5) {
            var3.append("]");
         }
      }

      if (var5) {
         if (!var4) {
            var3.append('[');
         } else {
            var3.append(", ");
         }

         var3.append("styleClass=");
         var3.append(this.getStyleClass());
         var3.append("]");
      }

      return var3.toString();
   }

   private void preprocessMouseEvent(MouseEvent var1) {
      EventType var2 = var1.getEventType();
      if (var2 == MouseEvent.MOUSE_PRESSED) {
         for (Object var4 = this; var4 != null; var4 = ((Node)var4).getParent()) {
            ((Node)var4).setPressed(var1.isPrimaryButtonDown());
         }
      } else if (var2 == MouseEvent.MOUSE_RELEASED) {
         for (Object var3 = this; var3 != null; var3 = ((Node)var3).getParent()) {
            ((Node)var3).setPressed(var1.isPrimaryButtonDown());
         }
      } else {
         if (var1.getTarget() == this) {
            if (var2 == MouseEvent.MOUSE_ENTERED || var2 == MouseEvent.MOUSE_ENTERED_TARGET) {
               this.setHover(true);
               return;
            }

            if (var2 == MouseEvent.MOUSE_EXITED || var2 == MouseEvent.MOUSE_EXITED_TARGET) {
               this.setHover(false);
               return;
            }
         }
      }
   }

   void markDirtyLayoutBranch() {
      for (Parent var1 = this.getParent(); var1 != null && var1.layoutFlag == LayoutFlags.CLEAN; var1 = var1.getParent()) {
         var1.setLayoutFlag(LayoutFlags.DIRTY_BRANCH);
         if (var1.isSceneRoot()) {
            Toolkit.getToolkit().requestNextPulse();
            if (this.getSubScene() != null) {
               this.getSubScene().setDirtyLayout(var1);
            }
         }
      }
   }

   private boolean isWindowShowing() {
      Scene var1 = this.getScene();
      if (var1 == null) {
         return false;
      } else {
         Window var2 = var1.getWindow();
         return var2 != null && var2.isShowing();
      }
   }

   final boolean isTreeShowing() {
      return this.isTreeVisible() && this.isWindowShowing();
   }

   private void updateTreeVisible(boolean var1) {
      boolean var2 = this.isVisible();
      Object var3 = this.getParent() != null
         ? this.getParent()
         : (this.clipParent != null ? this.clipParent : (this.getSubScene() != null ? this.getSubScene() : null));
      if (var2) {
         var2 = var3 == null || ((Node)var3).isTreeVisible();
      }

      if (var1 && var3 != null && ((Node)var3).isTreeVisible() && this.isDirty(DirtyBits.NODE_VISIBLE)) {
         this.addToSceneDirtyList();
      }

      this.setTreeVisible(var2);
   }

   final void setTreeVisible(boolean var1) {
      if (this.treeVisible != var1) {
         this.treeVisible = var1;
         this.updateCanReceiveFocus();
         this.focusSetDirty(this.getScene());
         if (this.getClip() != null) {
            this.getClip().updateTreeVisible(true);
         }

         if (this.treeVisible && !this.isDirtyEmpty()) {
            this.addToSceneDirtyList();
         }

         ((Node.TreeVisiblePropertyReadOnly)this.treeVisibleProperty()).invalidate();
         if (this instanceof SubScene) {
            Parent var2 = ((SubScene)this).getRoot();
            if (var2 != null) {
               var2.setTreeVisible(var1 && var2.isVisible());
            }
         }
      }
   }

   final boolean isTreeVisible() {
      return this.treeVisibleProperty().get();
   }

   final BooleanExpression treeVisibleProperty() {
      if (this.treeVisibleRO == null) {
         this.treeVisibleRO = new Node.TreeVisiblePropertyReadOnly();
      }

      return this.treeVisibleRO;
   }

   private void setCanReceiveFocus(boolean var1) {
      this.canReceiveFocus = var1;
   }

   final boolean isCanReceiveFocus() {
      return this.canReceiveFocus;
   }

   private void updateCanReceiveFocus() {
      this.setCanReceiveFocus(this.getScene() != null && !this.isDisabled() && this.isTreeVisible());
   }

   String indent() {
      String var1 = "";

      for (Parent var2 = this.getParent(); var2 != null; var2 = var2.getParent()) {
         var1 = var1 + "  ";
      }

      return var1;
   }

   final void setShowMnemonics(boolean var1) {
      this.showMnemonicsProperty().set(var1);
   }

   final boolean isShowMnemonics() {
      return this.showMnemonics == null ? false : this.showMnemonics.get();
   }

   final BooleanProperty showMnemonicsProperty() {
      if (this.showMnemonics == null) {
         this.showMnemonics = new BooleanPropertyBase(false) {
            protected void invalidated() {
               Node.this.pseudoClassStateChanged(Node.SHOW_MNEMONICS_PSEUDOCLASS_STATE, this.get());
            }

            public Object getBean() {
               return Node.this;
            }

            public String getName() {
               return "showMnemonics";
            }
         };
      }

      return this.showMnemonics;
   }

   public final void setEventDispatcher(EventDispatcher var1) {
      this.eventDispatcherProperty().set(var1);
   }

   public final EventDispatcher getEventDispatcher() {
      return (EventDispatcher)this.eventDispatcherProperty().get();
   }

   public final ObjectProperty<EventDispatcher> eventDispatcherProperty() {
      this.initializeInternalEventDispatcher();
      return this.eventDispatcher;
   }

   public final <T extends Event> void addEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().getEventHandlerManager().addEventHandler(var1, var2);
   }

   public final <T extends Event> void removeEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().getEventHandlerManager().removeEventHandler(var1, var2);
   }

   public final <T extends Event> void addEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().getEventHandlerManager().addEventFilter(var1, var2);
   }

   public final <T extends Event> void removeEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().getEventHandlerManager().removeEventFilter(var1, var2);
   }

   protected final <T extends Event> void setEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().getEventHandlerManager().setEventHandler(var1, var2);
   }

   private NodeEventDispatcher getInternalEventDispatcher() {
      this.initializeInternalEventDispatcher();
      return this.internalEventDispatcher;
   }

   private void initializeInternalEventDispatcher() {
      if (this.internalEventDispatcher == null) {
         this.internalEventDispatcher = this.createInternalEventDispatcher();
         this.eventDispatcher = new SimpleObjectProperty(this, "eventDispatcher", this.internalEventDispatcher);
      }
   }

   private NodeEventDispatcher createInternalEventDispatcher() {
      return new NodeEventDispatcher(this);
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      if (this.preprocessMouseEventDispatcher == null) {
         this.preprocessMouseEventDispatcher = (var1x, var2x) -> {
            var1x = var2x.dispatchEvent(var1x);
            if (var1x instanceof MouseEvent) {
               this.preprocessMouseEvent((MouseEvent)var1x);
            }

            return var1x;
         };
      }

      var1 = var1.prepend(this.preprocessMouseEventDispatcher);
      Object var2 = this;

      do {
         if (((Node)var2).eventDispatcher != null) {
            EventDispatcher var3 = (EventDispatcher)((Node)var2).eventDispatcher.get();
            if (var3 != null) {
               var1 = var1.prepend(var3);
            }
         }

         Parent var5 = ((Node)var2).getParent();
         var2 = var5 != null ? var5 : ((Node)var2).getSubScene();
      } while (var2 != null);

      if (this.getScene() != null) {
         var1 = this.getScene().buildEventDispatchChain(var1);
      }

      return var1;
   }

   public final void fireEvent(Event var1) {
      if (var1 instanceof InputEvent) {
         PlatformLogger var2 = Logging.getInputLogger();
         if (var2.isLoggable(Level.FINE)) {
            EventType var3 = var1.getEventType();
            if (var3 == MouseEvent.MOUSE_ENTERED || var3 == MouseEvent.MOUSE_EXITED) {
               var2.finer(var1.toString());
            } else if (var3 != MouseEvent.MOUSE_MOVED && var3 != MouseEvent.MOUSE_DRAGGED) {
               var2.fine(var1.toString());
            } else {
               var2.finest(var1.toString());
            }
         }
      }

      Event.fireEvent(this, var1);
   }

   @Override
   public String getTypeSelector() {
      Class var1 = this.getClass();
      Package var2 = var1.getPackage();
      int var3 = 0;
      if (var2 != null) {
         var3 = var2.getName().length();
      }

      int var4 = var1.getName().length();
      int var5 = 0 < var3 && var3 < var4 ? var3 + 1 : 0;
      return var1.getName().substring(var5);
   }

   @Override
   public Styleable getStyleableParent() {
      return this.getParent();
   }

   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }

   protected Cursor getInitialCursor() {
      return null;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Node.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   static List<Style> getMatchingStyles(CssMetaData var0, Styleable var1) {
      return CssStyleHelper.getMatchingStyles(var1, var0);
   }

   final ObservableMap<StyleableProperty<?>, List<Style>> getStyleMap() {
      ObservableMap var1 = (ObservableMap)this.getProperties().get("STYLEMAP");
      Map var2 = CssStyleHelper.getMatchingStyles(var1, this);
      if (var2 != null) {
         return var2 instanceof ObservableMap ? (ObservableMap)var2 : FXCollections.observableMap(var2);
      } else {
         return FXCollections.emptyObservableMap();
      }
   }

   final void setStyleMap(ObservableMap<StyleableProperty<?>, List<Style>> var1) {
      if (var1 != null) {
         this.getProperties().put("STYLEMAP", var1);
      } else {
         this.getProperties().remove("STYLEMAP");
      }
   }

   Map<StyleableProperty<?>, List<Style>> findStyles(Map<StyleableProperty<?>, List<Style>> var1) {
      Map var2 = CssStyleHelper.getMatchingStyles(var1, this);
      return var2 != null ? var2 : Collections.emptyMap();
   }

   final CssFlags getCSSFlags() {
      return this.cssFlag;
   }

   private void requestCssStateTransition() {
      if (this.getScene() != null) {
         if (this.cssFlag == CssFlags.CLEAN || this.cssFlag == CssFlags.DIRTY_BRANCH) {
            this.cssFlag = CssFlags.UPDATE;
            this.notifyParentsOfInvalidatedCSS();
         }
      }
   }

   public final void pseudoClassStateChanged(PseudoClass var1, boolean var2) {
      boolean var3 = var2 ? this.pseudoClassStates.add(var1) : this.pseudoClassStates.remove(var1);
      if (var3 && this.styleHelper != null) {
         boolean var4 = this.styleHelper.pseudoClassStateChanged(var1);
         if (var4) {
            this.requestCssStateTransition();
         }
      }
   }

   @Override
   public final ObservableSet<PseudoClass> getPseudoClassStates() {
      return this.unmodifiablePseudoClassStates;
   }

   final void notifyParentsOfInvalidatedCSS() {
      SubScene var1 = this.getSubScene();
      Parent var2 = var1 != null ? var1.getRoot() : this.getScene().getRoot();
      if (!var2.isDirty(DirtyBits.NODE_CSS)) {
         NodeHelper.markDirty(var2, DirtyBits.NODE_CSS);
         if (var1 != null) {
            var1.cssFlag = CssFlags.UPDATE;
            var1.notifyParentsOfInvalidatedCSS();
         }
      }

      Parent var3 = this.getParent();

      while (var3 != null) {
         if (var3.cssFlag == CssFlags.CLEAN) {
            var3.cssFlag = CssFlags.DIRTY_BRANCH;
            var3 = var3.getParent();
         } else {
            var3 = null;
         }
      }
   }

   final void recalculateRelativeSizeProperties(Font var1) {
      if (this.styleHelper != null) {
         this.styleHelper.recalculateRelativeSizeProperties(this, var1);
      }
   }

   final void reapplyCSS() {
      if (this.getScene() != null) {
         if (this.cssFlag != CssFlags.REAPPLY) {
            if (this.cssFlag == CssFlags.DIRTY_BRANCH) {
               this.cssFlag = CssFlags.REAPPLY;
            } else if (this.cssFlag == CssFlags.UPDATE) {
               this.cssFlag = CssFlags.REAPPLY;
               this.notifyParentsOfInvalidatedCSS();
            } else {
               this.reapplyCss();
               if (this.getParent() != null && this.getParent().isPerformingLayout()) {
                  NodeHelper.processCSS(this);
               } else {
                  this.notifyParentsOfInvalidatedCSS();
               }
            }
         }
      }
   }

   private void reapplyCss() {
      CssStyleHelper var1 = this.styleHelper;
      this.cssFlag = CssFlags.REAPPLY;
      this.styleHelper = CssStyleHelper.createStyleHelper(this);
      if (this instanceof Parent) {
         boolean var2 = this.styleHelper == null || var1 != this.styleHelper || this.getParent() == null || this.getParent().cssFlag != CssFlags.CLEAN;
         if (var2) {
            ObservableList var3 = ((Parent)this).getChildren();
            int var4 = 0;

            for (int var5 = var3.size(); var4 < var5; var4++) {
               Node var6 = (Node)var3.get(var4);
               var6.reapplyCss();
            }
         }
      } else if (this instanceof SubScene) {
         Parent var7 = ((SubScene)this).getRoot();
         if (var7 != null) {
            var7.reapplyCss();
         }
      } else if (this.styleHelper == null) {
         this.cssFlag = CssFlags.CLEAN;
         return;
      }

      this.cssFlag = CssFlags.UPDATE;
   }

   void processCSS() {
      switch (this.cssFlag) {
         case CLEAN:
            break;
         case DIRTY_BRANCH:
            Parent var1 = (Parent)this;
            var1.cssFlag = CssFlags.CLEAN;
            ObservableList var2 = var1.getChildren();
            int var3 = 0;

            for (int var4 = var2.size(); var3 < var4; var3++) {
               ((Node)var2.get(var3)).processCSS();
            }
            break;
         case REAPPLY:
         case UPDATE:
         default:
            NodeHelper.processCSS(this);
      }
   }

   public final void applyCss() {
      if (this.getScene() != null) {
         if (this.cssFlag != CssFlags.REAPPLY) {
            this.cssFlag = CssFlags.UPDATE;
         }

         Object var1 = this;
         boolean var2 = this.getScene().getRoot().isDirty(DirtyBits.NODE_CSS);
         if (var2) {
            for (Parent var3 = this.getParent(); var3 != null; var3 = var3.getParent()) {
               if (var3.cssFlag == CssFlags.UPDATE || var3.cssFlag == CssFlags.REAPPLY) {
                  var1 = var3;
               }
            }

            if (var1 == this.getScene().getRoot()) {
               this.getScene().getRoot().clearDirty(DirtyBits.NODE_CSS);
            }
         }

         ((Node)var1).processCSS();
      }
   }

   private void doProcessCSS() {
      if (this.cssFlag != CssFlags.CLEAN) {
         if (this.cssFlag == CssFlags.REAPPLY) {
            this.reapplyCss();
         }

         this.cssFlag = CssFlags.CLEAN;
         if (this.styleHelper != null && this.getScene() != null) {
            this.styleHelper.transitionToState(this);
         }
      }
   }

   public final void setAccessibleRole(AccessibleRole var1) {
      if (var1 == null) {
         var1 = AccessibleRole.NODE;
      }

      this.accessibleRoleProperty().set(var1);
   }

   public final AccessibleRole getAccessibleRole() {
      return this.accessibleRole == null ? AccessibleRole.NODE : (AccessibleRole)this.accessibleRoleProperty().get();
   }

   public final ObjectProperty<AccessibleRole> accessibleRoleProperty() {
      if (this.accessibleRole == null) {
         this.accessibleRole = new SimpleObjectProperty(this, "accessibleRole", AccessibleRole.NODE);
      }

      return this.accessibleRole;
   }

   public final void setAccessibleRoleDescription(String var1) {
      this.accessibleRoleDescriptionProperty().set(var1);
   }

   public final String getAccessibleRoleDescription() {
      if (this.accessibilityProperties == null) {
         return null;
      } else {
         return this.accessibilityProperties.accessibleRoleDescription == null ? null : (String)this.accessibleRoleDescriptionProperty().get();
      }
   }

   public final ObjectProperty<String> accessibleRoleDescriptionProperty() {
      return this.getAccessibilityProperties().getAccessibleRoleDescription();
   }

   public final void setAccessibleText(String var1) {
      this.accessibleTextProperty().set(var1);
   }

   public final String getAccessibleText() {
      if (this.accessibilityProperties == null) {
         return null;
      } else {
         return this.accessibilityProperties.accessibleText == null ? null : (String)this.accessibleTextProperty().get();
      }
   }

   public final ObjectProperty<String> accessibleTextProperty() {
      return this.getAccessibilityProperties().getAccessibleText();
   }

   public final void setAccessibleHelp(String var1) {
      this.accessibleHelpProperty().set(var1);
   }

   public final String getAccessibleHelp() {
      if (this.accessibilityProperties == null) {
         return null;
      } else {
         return this.accessibilityProperties.accessibleHelp == null ? null : (String)this.accessibleHelpProperty().get();
      }
   }

   public final ObjectProperty<String> accessibleHelpProperty() {
      return this.getAccessibilityProperties().getAccessibleHelp();
   }

   private Node.AccessibilityProperties getAccessibilityProperties() {
      if (this.accessibilityProperties == null) {
         this.accessibilityProperties = new Node.AccessibilityProperties();
      }

      return this.accessibilityProperties;
   }

   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case ROLE:
            return this.getAccessibleRole();
         case ROLE_DESCRIPTION:
            return this.getAccessibleRoleDescription();
         case TEXT:
            return this.getAccessibleText();
         case HELP:
            return this.getAccessibleHelp();
         case PARENT:
            return this.getParent();
         case SCENE:
            return this.getScene();
         case BOUNDS:
            return this.localToScreen(this.getBoundsInLocal());
         case DISABLED:
            return this.isDisabled();
         case FOCUSED:
            return this.isFocused();
         case VISIBLE:
            return this.isVisible();
         case LABELED_BY:
            return this.labeledBy;
         default:
            return null;
      }
   }

   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case REQUEST_FOCUS:
            if (this.isFocusTraversable()) {
               this.requestFocus();
            }
            break;
         case SHOW_MENU:
            Bounds var3 = this.getBoundsInLocal();
            Point2D var4 = this.localToScreen(var3.getMaxX(), var3.getMaxY());
            ContextMenuEvent var5 = new ContextMenuEvent(
               ContextMenuEvent.CONTEXT_MENU_REQUESTED,
               var3.getMaxX(),
               var3.getMaxY(),
               var4.getX(),
               var4.getY(),
               false,
               new PickResult(this, var3.getMaxX(), var3.getMaxY())
            );
            Event.fireEvent(this, var5);
      }
   }

   public final void notifyAccessibleAttributeChanged(AccessibleAttribute var1) {
      if (this.accessible == null) {
         Scene var2 = this.getScene();
         if (var2 != null) {
            this.accessible = var2.removeAccessible(this);
         }
      }

      if (this.accessible != null) {
         this.accessible.sendNotification(var1);
      }
   }

   Accessible getAccessible() {
      if (this.accessible == null) {
         Scene var1 = this.getScene();
         if (var1 != null) {
            this.accessible = var1.removeAccessible(this);
         }
      }

      if (this.accessible == null) {
         this.accessible = Application.GetApplication().createAccessible();
         this.accessible.setEventHandler(new Accessible.EventHandler() {
            @Override
            public AccessControlContext getAccessControlContext() {
               Scene var1 = Node.this.getScene();
               if (var1 == null) {
                  throw new RuntimeException("Accessbility requested for node not on a scene");
               } else {
                  return var1.getPeer() != null ? var1.getPeer().getAccessControlContext() : var1.acc;
               }
            }

            @Override
            public Object getAttribute(AccessibleAttribute var1, Object... var2) {
               return Node.this.queryAccessibleAttribute(var1, var2);
            }

            @Override
            public void executeAction(AccessibleAction var1, Object... var2) {
               Node.this.executeAccessibleAction(var1, var2);
            }

            @Override
            public String toString() {
               String var1 = Node.this.getClass().getName();
               return var1.substring(var1.lastIndexOf(46) + 1);
            }
         });
      }

      return this.accessible;
   }

   void releaseAccessible() {
      Accessible var1 = this.accessible;
      if (var1 != null) {
         this.accessible = null;
         var1.dispose();
      }
   }

   static {
      PerformanceTracker.logEvent("Node class loaded");
      NodeHelper.setNodeAccessor(new NodeHelper.NodeAccessor() {
         @Override
         public NodeHelper getHelper(Node var1) {
            return var1.nodeHelper;
         }

         @Override
         public void setHelper(Node var1, NodeHelper var2) {
            var1.nodeHelper = var2;
         }

         @Override
         public void doMarkDirty(Node var1, DirtyBits var2) {
            var1.doMarkDirty(var2);
         }

         @Override
         public void doUpdatePeer(Node var1) {
            var1.doUpdatePeer();
         }

         @Override
         public BaseTransform getLeafTransform(Node var1) {
            return var1.getLeafTransform();
         }

         @Override
         public Bounds doComputeLayoutBounds(Node var1) {
            return var1.doComputeLayoutBounds();
         }

         @Override
         public void doTransformsChanged(Node var1) {
            var1.doTransformsChanged();
         }

         @Override
         public void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3) {
            var1.doPickNodeLocal(var2, var3);
         }

         @Override
         public boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3) {
            return var1.doComputeIntersects(var2, var3);
         }

         @Override
         public void doGeomChanged(Node var1) {
            var1.doGeomChanged();
         }

         @Override
         public void doNotifyLayoutBoundsChanged(Node var1) {
            var1.doNotifyLayoutBoundsChanged();
         }

         @Override
         public void doProcessCSS(Node var1) {
            var1.doProcessCSS();
         }

         @Override
         public boolean isDirty(Node var1, DirtyBits var2) {
            return var1.isDirty(var2);
         }

         @Override
         public boolean isDirtyEmpty(Node var1) {
            return var1.isDirtyEmpty();
         }

         @Override
         public void syncPeer(Node var1) {
            var1.syncPeer();
         }

         @Override
         public void layoutBoundsChanged(Node var1) {
            var1.layoutBoundsChanged();
         }

         @Override
         public <P extends NGNode> P getPeer(Node var1) {
            return var1.getPeer();
         }

         @Override
         public void setShowMnemonics(Node var1, boolean var2) {
            var1.setShowMnemonics(var2);
         }

         @Override
         public boolean isShowMnemonics(Node var1) {
            return var1.isShowMnemonics();
         }

         @Override
         public BooleanProperty showMnemonicsProperty(Node var1) {
            return var1.showMnemonicsProperty();
         }

         @Override
         public boolean traverse(Node var1, Direction var2) {
            return var1.traverse(var2);
         }

         @Override
         public double getPivotX(Node var1) {
            return var1.getPivotX();
         }

         @Override
         public double getPivotY(Node var1) {
            return var1.getPivotY();
         }

         @Override
         public double getPivotZ(Node var1) {
            return var1.getPivotZ();
         }

         @Override
         public void pickNode(Node var1, PickRay var2, PickResultChooser var3) {
            var1.pickNode(var2, var3);
         }

         @Override
         public boolean intersects(Node var1, PickRay var2, PickResultChooser var3) {
            return var1.intersects(var2, var3);
         }

         @Override
         public double intersectsBounds(Node var1, PickRay var2) {
            return var1.intersectsBounds(var2);
         }

         @Override
         public void layoutNodeForPrinting(Node var1) {
            var1.doCSSLayoutSyncForSnapshot();
         }

         @Override
         public boolean isDerivedDepthTest(Node var1) {
            return var1.isDerivedDepthTest();
         }

         @Override
         public SubScene getSubScene(Node var1) {
            return var1.getSubScene();
         }

         @Override
         public void setLabeledBy(Node var1, Node var2) {
            var1.labeledBy = var2;
         }

         @Override
         public Accessible getAccessible(Node var1) {
            return var1.getAccessible();
         }

         @Override
         public void reapplyCSS(Node var1) {
            var1.reapplyCSS();
         }

         @Override
         public void recalculateRelativeSizeProperties(Node var1, Font var2) {
            var1.recalculateRelativeSizeProperties(var2);
         }

         @Override
         public boolean isTreeVisible(Node var1) {
            return var1.isTreeVisible();
         }

         @Override
         public BooleanExpression treeVisibleProperty(Node var1) {
            return var1.treeVisibleProperty();
         }

         @Override
         public boolean isTreeShowing(Node var1) {
            return var1.isTreeShowing();
         }

         @Override
         public List<Style> getMatchingStyles(CssMetaData var1, Styleable var2) {
            return Node.getMatchingStyles(var1, var2);
         }

         @Override
         public Map<StyleableProperty<?>, List<Style>> findStyles(Node var1, Map<StyleableProperty<?>, List<Style>> var2) {
            return var1.findStyles(var2);
         }
      });
   }

   private class AccessibilityProperties {
      ObjectProperty<String> accessibleRoleDescription;
      ObjectProperty<String> accessibleText;
      ObjectProperty<String> accessibleHelp;

      ObjectProperty<String> getAccessibleRoleDescription() {
         if (this.accessibleRoleDescription == null) {
            this.accessibleRoleDescription = new SimpleObjectProperty(Node.this, "accessibleRoleDescription", null);
         }

         return this.accessibleRoleDescription;
      }

      ObjectProperty<String> getAccessibleText() {
         if (this.accessibleText == null) {
            this.accessibleText = new SimpleObjectProperty(Node.this, "accessibleText", null);
         }

         return this.accessibleText;
      }

      ObjectProperty<String> getAccessibleHelp() {
         if (this.accessibleHelp == null) {
            this.accessibleHelp = new SimpleObjectProperty(Node.this, "accessibleHelp", null);
         }

         return this.accessibleHelp;
      }
   }

   private final class EffectiveOrientationProperty extends ReadOnlyObjectPropertyBase<NodeOrientation> {
      public NodeOrientation get() {
         return Node.this.getEffectiveNodeOrientation();
      }

      public Object getBean() {
         return Node.this;
      }

      public String getName() {
         return "effectiveNodeOrientation";
      }

      public void invalidate() {
         this.fireValueChangedEvent();
      }
   }

   final class FocusedProperty extends ReadOnlyBooleanPropertyBase {
      private boolean value;
      private boolean valid = true;
      private boolean needsChangeEvent = false;

      public void store(boolean var1) {
         if (var1 != this.value) {
            this.value = var1;
            this.markInvalid();
         }
      }

      public void notifyListeners() {
         if (this.needsChangeEvent) {
            this.fireValueChangedEvent();
            this.needsChangeEvent = false;
         }
      }

      private void markInvalid() {
         if (this.valid) {
            this.valid = false;
            Node.this.pseudoClassStateChanged(Node.FOCUSED_PSEUDOCLASS_STATE, this.get());
            PlatformLogger var1 = Logging.getFocusLogger();
            if (var1.isLoggable(Level.FINE)) {
               var1.fine(this + " focused=" + this.get());
            }

            this.needsChangeEvent = true;
            Node.this.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUSED);
         }
      }

      public boolean get() {
         this.valid = true;
         return this.value;
      }

      public Object getBean() {
         return Node.this;
      }

      public String getName() {
         return "focused";
      }
   }

   private abstract static class LazyBoundsProperty extends ReadOnlyObjectProperty<Bounds> {
      private ExpressionHelper<Bounds> helper;
      private boolean valid;
      private Bounds bounds;

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public void addListener(ChangeListener<? super Bounds> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super Bounds> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public Bounds get() {
         if (!this.valid) {
            this.bounds = this.computeBounds();
            this.valid = true;
         }

         return this.bounds;
      }

      public void invalidate() {
         if (this.valid) {
            this.valid = false;
            ExpressionHelper.fireValueChangedEvent(this.helper);
         }
      }

      protected abstract Bounds computeBounds();
   }

   private abstract static class LazyTransformProperty extends ReadOnlyObjectProperty<Transform> {
      protected static final int VALID = 0;
      protected static final int INVALID = 1;
      protected static final int VALIDITY_UNKNOWN = 2;
      protected int valid = 1;
      private ExpressionHelper<Transform> helper;
      private Transform transform;
      private boolean canReuse = false;

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public void addListener(ChangeListener<? super Transform> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super Transform> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      protected Transform getInternalValue() {
         if (this.valid == 1 || this.valid == 2 && this.computeValidity() == 1) {
            this.transform = this.computeTransform(this.canReuse ? this.transform : null);
            this.canReuse = true;
            this.valid = this.validityKnown() ? 0 : 2;
         }

         return this.transform;
      }

      public Transform get() {
         this.transform = this.getInternalValue();
         this.canReuse = false;
         return this.transform;
      }

      public void validityUnknown() {
         if (this.valid == 0) {
            this.valid = 2;
         }
      }

      public void invalidate() {
         if (this.valid != 1) {
            this.valid = 1;
            ExpressionHelper.fireValueChangedEvent(this.helper);
         }
      }

      protected abstract boolean validityKnown();

      protected abstract int computeValidity();

      protected abstract Transform computeTransform(Transform var1);
   }

   private final class MiscProperties {
      private Node.LazyBoundsProperty boundsInParent;
      private Node.LazyBoundsProperty boundsInLocal;
      private BooleanProperty cache;
      private ObjectProperty<CacheHint> cacheHint;
      private ObjectProperty<Node> clip;
      private ObjectProperty<Cursor> cursor;
      private ObjectProperty<DepthTest> depthTest;
      private BooleanProperty disable;
      private ObjectProperty<Effect> effect;
      private ObjectProperty<InputMethodRequests> inputMethodRequests;
      private BooleanProperty mouseTransparent;
      private DoubleProperty viewOrder;

      public double getViewOrder() {
         return this.viewOrder == null ? 0.0 : this.viewOrder.get();
      }

      public final DoubleProperty viewOrderProperty() {
         if (this.viewOrder == null) {
            this.viewOrder = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  Parent var1 = Node.this.getParent();
                  if (var1 != null) {
                     var1.markViewOrderChildrenDirty();
                  }

                  NodeHelper.markDirty(Node.this, DirtyBits.NODE_VIEW_ORDER);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.VIEW_ORDER;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "viewOrder";
               }
            };
         }

         return this.viewOrder;
      }

      public final Bounds getBoundsInParent() {
         return (Bounds)this.boundsInParentProperty().get();
      }

      public final ReadOnlyObjectProperty<Bounds> boundsInParentProperty() {
         if (this.boundsInParent == null) {
            this.boundsInParent = new Node.LazyBoundsProperty() {
               @Override
               protected Bounds computeBounds() {
                  BaseBounds var1 = TempState.getInstance().bounds;
                  var1 = Node.this.getTransformedBounds(var1, BaseTransform.IDENTITY_TRANSFORM);
                  return new BoundingBox(
                     (double)var1.getMinX(),
                     (double)var1.getMinY(),
                     (double)var1.getMinZ(),
                     (double)var1.getWidth(),
                     (double)var1.getHeight(),
                     (double)var1.getDepth()
                  );
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "boundsInParent";
               }
            };
         }

         return this.boundsInParent;
      }

      public void invalidateBoundsInParent() {
         if (this.boundsInParent != null) {
            this.boundsInParent.invalidate();
         }
      }

      public final Bounds getBoundsInLocal() {
         return (Bounds)this.boundsInLocalProperty().get();
      }

      public final ReadOnlyObjectProperty<Bounds> boundsInLocalProperty() {
         if (this.boundsInLocal == null) {
            this.boundsInLocal = new Node.LazyBoundsProperty() {
               @Override
               protected Bounds computeBounds() {
                  BaseBounds var1 = TempState.getInstance().bounds;
                  var1 = Node.this.getLocalBounds(var1, BaseTransform.IDENTITY_TRANSFORM);
                  return new BoundingBox(
                     (double)var1.getMinX(),
                     (double)var1.getMinY(),
                     (double)var1.getMinZ(),
                     (double)var1.getWidth(),
                     (double)var1.getHeight(),
                     (double)var1.getDepth()
                  );
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "boundsInLocal";
               }
            };
         }

         return this.boundsInLocal;
      }

      public void invalidateBoundsInLocal() {
         if (this.boundsInLocal != null) {
            this.boundsInLocal.invalidate();
         }
      }

      public final boolean isCache() {
         return this.cache == null ? false : this.cache.get();
      }

      public final BooleanProperty cacheProperty() {
         if (this.cache == null) {
            this.cache = new BooleanPropertyBase(false) {
               protected void invalidated() {
                  NodeHelper.markDirty(Node.this, DirtyBits.NODE_CACHE);
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "cache";
               }
            };
         }

         return this.cache;
      }

      public final CacheHint getCacheHint() {
         return this.cacheHint == null ? Node.DEFAULT_CACHE_HINT : (CacheHint)this.cacheHint.get();
      }

      public final ObjectProperty<CacheHint> cacheHintProperty() {
         if (this.cacheHint == null) {
            this.cacheHint = new ObjectPropertyBase<CacheHint>(Node.DEFAULT_CACHE_HINT) {
               protected void invalidated() {
                  NodeHelper.markDirty(Node.this, DirtyBits.NODE_CACHE);
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "cacheHint";
               }
            };
         }

         return this.cacheHint;
      }

      public final Node getClip() {
         return this.clip == null ? Node.DEFAULT_CLIP : (Node)this.clip.get();
      }

      public final ObjectProperty<Node> clipProperty() {
         if (this.clip == null) {
            this.clip = new ObjectPropertyBase<Node>(Node.DEFAULT_CLIP) {
               private Node oldClip;

               protected void invalidated() {
                  Node var1 = (Node)this.get();
                  if (var1 != null && (var1.isConnected() && var1.clipParent != Node.this || Node.this.wouldCreateCycle(Node.this, var1))) {
                     String var2 = var1.isConnected() && var1.clipParent != Node.this ? "node already connected" : "cycle detected";
                     if (this.isBound()) {
                        this.unbind();
                        this.set(this.oldClip);
                        throw new IllegalArgumentException(
                           "Node's clip set to incorrect value  through binding ("
                              + var2
                              + ", node  = "
                              + Node.this
                              + ", clip = "
                              + MiscProperties.this.clip
                              + "). Binding has been removed."
                        );
                     } else {
                        this.set(this.oldClip);
                        throw new IllegalArgumentException(
                           "Node's clip set to incorrect value (" + var2 + ", node  = " + Node.this + ", clip = " + MiscProperties.this.clip + ")."
                        );
                     }
                  } else {
                     if (this.oldClip != null) {
                        this.oldClip.clipParent = null;
                        this.oldClip.setScenes(null, null);
                        this.oldClip.updateTreeVisible(false);
                     }

                     if (var1 != null) {
                        var1.clipParent = Node.this;
                        var1.setScenes(Node.this.getScene(), Node.this.getSubScene());
                        var1.updateTreeVisible(true);
                     }

                     NodeHelper.markDirty(Node.this, DirtyBits.NODE_CLIP);
                     Node.this.localBoundsChanged();
                     this.oldClip = var1;
                  }
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "clip";
               }
            };
         }

         return this.clip;
      }

      public final Cursor getCursor() {
         return this.cursor == null ? Node.DEFAULT_CURSOR : (Cursor)this.cursor.get();
      }

      public final ObjectProperty<Cursor> cursorProperty() {
         if (this.cursor == null) {
            this.cursor = new StyleableObjectProperty<Cursor>(Node.DEFAULT_CURSOR) {
               protected void invalidated() {
                  Scene var1 = Node.this.getScene();
                  if (var1 != null) {
                     var1.markCursorDirty();
                  }
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.CURSOR;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "cursor";
               }
            };
         }

         return this.cursor;
      }

      public final DepthTest getDepthTest() {
         return this.depthTest == null ? Node.DEFAULT_DEPTH_TEST : (DepthTest)this.depthTest.get();
      }

      public final ObjectProperty<DepthTest> depthTestProperty() {
         if (this.depthTest == null) {
            this.depthTest = new ObjectPropertyBase<DepthTest>(Node.DEFAULT_DEPTH_TEST) {
               protected void invalidated() {
                  Node.this.computeDerivedDepthTest();
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "depthTest";
               }
            };
         }

         return this.depthTest;
      }

      public final boolean isDisable() {
         return this.disable == null ? false : this.disable.get();
      }

      public final BooleanProperty disableProperty() {
         if (this.disable == null) {
            this.disable = new BooleanPropertyBase(false) {
               protected void invalidated() {
                  Node.this.updateDisabled();
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "disable";
               }
            };
         }

         return this.disable;
      }

      public final Effect getEffect() {
         return this.effect == null ? Node.DEFAULT_EFFECT : (Effect)this.effect.get();
      }

      public final ObjectProperty<Effect> effectProperty() {
         if (this.effect == null) {
            this.effect = new StyleableObjectProperty<Effect>(Node.DEFAULT_EFFECT) {
               private Effect oldEffect = null;
               private int oldBits;
               private final AbstractNotifyListener effectChangeListener = new AbstractNotifyListener() {
                  public void invalidated(Observable var1) {
                     int var2 = ((IntegerProperty)var1).get();
                     int var3 = var2 ^ oldBits;
                     oldBits = var2;
                     if (EffectDirtyBits.isSet(var3, EffectDirtyBits.EFFECT_DIRTY) && EffectDirtyBits.isSet(var2, EffectDirtyBits.EFFECT_DIRTY)) {
                        NodeHelper.markDirty(Node.this, DirtyBits.EFFECT_EFFECT);
                     }

                     if (EffectDirtyBits.isSet(var3, EffectDirtyBits.BOUNDS_CHANGED)) {
                        Node.this.localBoundsChanged();
                     }
                  }
               };

               protected void invalidated() {
                  Effect var1 = (Effect)this.get();
                  if (this.oldEffect != null) {
                     EffectHelper.effectDirtyProperty(this.oldEffect).removeListener(this.effectChangeListener.getWeakListener());
                  }

                  this.oldEffect = var1;
                  if (var1 != null) {
                     EffectHelper.effectDirtyProperty(var1).addListener(this.effectChangeListener.getWeakListener());
                     if (EffectHelper.isEffectDirty(var1)) {
                        NodeHelper.markDirty(Node.this, DirtyBits.EFFECT_EFFECT);
                     }

                     this.oldBits = EffectHelper.effectDirtyProperty(var1).get();
                  }

                  NodeHelper.markDirty(Node.this, DirtyBits.NODE_EFFECT);
                  Node.this.localBoundsChanged();
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.EFFECT;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "effect";
               }
            };
         }

         return this.effect;
      }

      public final InputMethodRequests getInputMethodRequests() {
         return this.inputMethodRequests == null ? Node.DEFAULT_INPUT_METHOD_REQUESTS : (InputMethodRequests)this.inputMethodRequests.get();
      }

      public ObjectProperty<InputMethodRequests> inputMethodRequestsProperty() {
         if (this.inputMethodRequests == null) {
            this.inputMethodRequests = new SimpleObjectProperty(Node.this, "inputMethodRequests", Node.DEFAULT_INPUT_METHOD_REQUESTS);
         }

         return this.inputMethodRequests;
      }

      public final boolean isMouseTransparent() {
         return this.mouseTransparent == null ? false : this.mouseTransparent.get();
      }

      public final BooleanProperty mouseTransparentProperty() {
         if (this.mouseTransparent == null) {
            this.mouseTransparent = new SimpleBooleanProperty(Node.this, "mouseTransparent", false);
         }

         return this.mouseTransparent;
      }

      public boolean canSetCursor() {
         return this.cursor == null || !this.cursor.isBound();
      }

      public boolean canSetEffect() {
         return this.effect == null || !this.effect.isBound();
      }
   }

   private final class NodeTransformation {
      private DoubleProperty translateX;
      private DoubleProperty translateY;
      private DoubleProperty translateZ;
      private DoubleProperty scaleX;
      private DoubleProperty scaleY;
      private DoubleProperty scaleZ;
      private DoubleProperty rotate;
      private ObjectProperty<Point3D> rotationAxis;
      private ObservableList<Transform> transforms;
      private Node.LazyTransformProperty localToParentTransform;
      private Node.LazyTransformProperty localToSceneTransform;
      private int listenerReasons = 0;
      private InvalidationListener localToSceneInvLstnr;

      private InvalidationListener getLocalToSceneInvalidationListener() {
         if (this.localToSceneInvLstnr == null) {
            this.localToSceneInvLstnr = var1 -> this.invalidateLocalToSceneTransform();
         }

         return this.localToSceneInvLstnr;
      }

      public void incListenerReasons() {
         if (this.listenerReasons == 0) {
            Parent var1 = Node.this.getParent();
            if (var1 != null) {
               var1.localToSceneTransformProperty().addListener(this.getLocalToSceneInvalidationListener());
            }
         }

         this.listenerReasons++;
      }

      public void decListenerReasons() {
         this.listenerReasons--;
         if (this.listenerReasons == 0) {
            Parent var1 = Node.this.getParent();
            if (var1 != null) {
               var1.localToSceneTransformProperty().removeListener(this.getLocalToSceneInvalidationListener());
            }

            if (this.localToSceneTransform != null) {
               this.localToSceneTransform.validityUnknown();
            }
         }
      }

      public final Transform getLocalToParentTransform() {
         return (Transform)this.localToParentTransformProperty().get();
      }

      public final ReadOnlyObjectProperty<Transform> localToParentTransformProperty() {
         if (this.localToParentTransform == null) {
            this.localToParentTransform = new Node.LazyTransformProperty() {
               @Override
               protected Transform computeTransform(Transform var1) {
                  Node.this.updateLocalToParentTransform();
                  return TransformUtils.immutableTransform(
                     var1,
                     Node.this.localToParentTx.getMxx(),
                     Node.this.localToParentTx.getMxy(),
                     Node.this.localToParentTx.getMxz(),
                     Node.this.localToParentTx.getMxt(),
                     Node.this.localToParentTx.getMyx(),
                     Node.this.localToParentTx.getMyy(),
                     Node.this.localToParentTx.getMyz(),
                     Node.this.localToParentTx.getMyt(),
                     Node.this.localToParentTx.getMzx(),
                     Node.this.localToParentTx.getMzy(),
                     Node.this.localToParentTx.getMzz(),
                     Node.this.localToParentTx.getMzt()
                  );
               }

               @Override
               protected boolean validityKnown() {
                  return true;
               }

               @Override
               protected int computeValidity() {
                  return this.valid;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "localToParentTransform";
               }
            };
         }

         return this.localToParentTransform;
      }

      public void invalidateLocalToParentTransform() {
         if (this.localToParentTransform != null) {
            this.localToParentTransform.invalidate();
         }
      }

      public final Transform getLocalToSceneTransform() {
         return (Transform)this.localToSceneTransformProperty().get();
      }

      public final ReadOnlyObjectProperty<Transform> localToSceneTransformProperty() {
         if (this.localToSceneTransform == null) {
            this.localToSceneTransform = new Node.NodeTransformation.LocalToSceneTransformProperty();
         }

         return this.localToSceneTransform;
      }

      public void invalidateLocalToSceneTransform() {
         if (this.localToSceneTransform != null) {
            this.localToSceneTransform.invalidate();
         }
      }

      public double getTranslateX() {
         return this.translateX == null ? 0.0 : this.translateX.get();
      }

      public final DoubleProperty translateXProperty() {
         if (this.translateX == null) {
            this.translateX = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.TRANSLATE_X;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "translateX";
               }
            };
         }

         return this.translateX;
      }

      public double getTranslateY() {
         return this.translateY == null ? 0.0 : this.translateY.get();
      }

      public final DoubleProperty translateYProperty() {
         if (this.translateY == null) {
            this.translateY = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.TRANSLATE_Y;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "translateY";
               }
            };
         }

         return this.translateY;
      }

      public double getTranslateZ() {
         return this.translateZ == null ? 0.0 : this.translateZ.get();
      }

      public final DoubleProperty translateZProperty() {
         if (this.translateZ == null) {
            this.translateZ = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.TRANSLATE_Z;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "translateZ";
               }
            };
         }

         return this.translateZ;
      }

      public double getScaleX() {
         return this.scaleX == null ? 1.0 : this.scaleX.get();
      }

      public final DoubleProperty scaleXProperty() {
         if (this.scaleX == null) {
            this.scaleX = new StyleableDoubleProperty(1.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.SCALE_X;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "scaleX";
               }
            };
         }

         return this.scaleX;
      }

      public double getScaleY() {
         return this.scaleY == null ? 1.0 : this.scaleY.get();
      }

      public final DoubleProperty scaleYProperty() {
         if (this.scaleY == null) {
            this.scaleY = new StyleableDoubleProperty(1.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.SCALE_Y;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "scaleY";
               }
            };
         }

         return this.scaleY;
      }

      public double getScaleZ() {
         return this.scaleZ == null ? 1.0 : this.scaleZ.get();
      }

      public final DoubleProperty scaleZProperty() {
         if (this.scaleZ == null) {
            this.scaleZ = new StyleableDoubleProperty(1.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.SCALE_Z;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "scaleZ";
               }
            };
         }

         return this.scaleZ;
      }

      public double getRotate() {
         return this.rotate == null ? 0.0 : this.rotate.get();
      }

      public final DoubleProperty rotateProperty() {
         if (this.rotate == null) {
            this.rotate = new StyleableDoubleProperty(0.0) {
               public void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               @Override
               public CssMetaData getCssMetaData() {
                  return Node.StyleableProperties.ROTATE;
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "rotate";
               }
            };
         }

         return this.rotate;
      }

      public Point3D getRotationAxis() {
         return this.rotationAxis == null ? Node.DEFAULT_ROTATION_AXIS : (Point3D)this.rotationAxis.get();
      }

      public final ObjectProperty<Point3D> rotationAxisProperty() {
         if (this.rotationAxis == null) {
            this.rotationAxis = new ObjectPropertyBase<Point3D>(Node.DEFAULT_ROTATION_AXIS) {
               protected void invalidated() {
                  NodeHelper.transformsChanged(Node.this);
               }

               public Object getBean() {
                  return Node.this;
               }

               public String getName() {
                  return "rotationAxis";
               }
            };
         }

         return this.rotationAxis;
      }

      public ObservableList<Transform> getTransforms() {
         if (this.transforms == null) {
            this.transforms = new TrackableObservableList<Transform>() {
               protected void onChanged(Change<Transform> var1) {
                  while (var1.next()) {
                     for (Transform var3 : var1.getRemoved()) {
                        TransformHelper.remove(var3, Node.this);
                     }

                     for (Transform var5 : var1.getAddedSubList()) {
                        TransformHelper.add(var5, Node.this);
                     }
                  }

                  NodeHelper.transformsChanged(Node.this);
               }
            };
         }

         return this.transforms;
      }

      public boolean canSetTranslateX() {
         return this.translateX == null || !this.translateX.isBound();
      }

      public boolean canSetTranslateY() {
         return this.translateY == null || !this.translateY.isBound();
      }

      public boolean canSetTranslateZ() {
         return this.translateZ == null || !this.translateZ.isBound();
      }

      public boolean canSetScaleX() {
         return this.scaleX == null || !this.scaleX.isBound();
      }

      public boolean canSetScaleY() {
         return this.scaleY == null || !this.scaleY.isBound();
      }

      public boolean canSetScaleZ() {
         return this.scaleZ == null || !this.scaleZ.isBound();
      }

      public boolean canSetRotate() {
         return this.rotate == null || !this.rotate.isBound();
      }

      public boolean hasTransforms() {
         return this.transforms != null && !this.transforms.isEmpty();
      }

      public boolean hasScaleOrRotate() {
         if (this.scaleX != null && this.scaleX.get() != 1.0) {
            return true;
         } else if (this.scaleY != null && this.scaleY.get() != 1.0) {
            return true;
         } else {
            return this.scaleZ != null && this.scaleZ.get() != 1.0 ? true : this.rotate != null && this.rotate.get() != 0.0;
         }
      }

      class LocalToSceneTransformProperty extends Node.LazyTransformProperty {
         private List localToSceneListeners;
         private long stamp;
         private long parentStamp;

         @Override
         protected Transform computeTransform(Transform var1) {
            this.stamp++;
            Node.this.updateLocalToParentTransform();
            Parent var2 = Node.this.getParent();
            if (var2 != null) {
               Node.NodeTransformation.LocalToSceneTransformProperty var3 = (Node.NodeTransformation.LocalToSceneTransformProperty)var2.localToSceneTransformProperty();
               Transform var4 = var3.getInternalValue();
               this.parentStamp = var3.stamp;
               return TransformUtils.immutableTransform(
                  var1, var4, ((Node.LazyTransformProperty)NodeTransformation.this.localToParentTransformProperty()).getInternalValue()
               );
            } else {
               return TransformUtils.immutableTransform(
                  var1, ((Node.LazyTransformProperty)NodeTransformation.this.localToParentTransformProperty()).getInternalValue()
               );
            }
         }

         public Object getBean() {
            return Node.this;
         }

         public String getName() {
            return "localToSceneTransform";
         }

         @Override
         protected boolean validityKnown() {
            return NodeTransformation.this.listenerReasons > 0;
         }

         @Override
         protected int computeValidity() {
            if (this.valid != 2) {
               return this.valid;
            } else {
               Node var1 = (Node)this.getBean();
               Parent var2 = var1.getParent();
               if (var2 != null) {
                  Node.NodeTransformation.LocalToSceneTransformProperty var3 = (Node.NodeTransformation.LocalToSceneTransformProperty)var2.localToSceneTransformProperty();
                  if (this.parentStamp != var3.stamp) {
                     this.valid = 1;
                     return 1;
                  } else {
                     int var4 = var3.computeValidity();
                     if (var4 == 1) {
                        this.valid = 1;
                     }

                     return var4;
                  }
               } else {
                  return 0;
               }
            }
         }

         @Override
         public void addListener(InvalidationListener var1) {
            NodeTransformation.this.incListenerReasons();
            if (this.localToSceneListeners == null) {
               this.localToSceneListeners = new LinkedList();
            }

            this.localToSceneListeners.add(var1);
            super.addListener(var1);
         }

         @Override
         public void addListener(ChangeListener<? super Transform> var1) {
            NodeTransformation.this.incListenerReasons();
            if (this.localToSceneListeners == null) {
               this.localToSceneListeners = new LinkedList();
            }

            this.localToSceneListeners.add(var1);
            super.addListener(var1);
         }

         @Override
         public void removeListener(InvalidationListener var1) {
            if (this.localToSceneListeners != null && this.localToSceneListeners.remove(var1)) {
               NodeTransformation.this.decListenerReasons();
            }

            super.removeListener(var1);
         }

         @Override
         public void removeListener(ChangeListener<? super Transform> var1) {
            if (this.localToSceneListeners != null && this.localToSceneListeners.remove(var1)) {
               NodeTransformation.this.decListenerReasons();
            }

            super.removeListener(var1);
         }
      }
   }

   private class ReadOnlyObjectWrapperManualFire<T> extends ReadOnlyObjectWrapper<T> {
      public Object getBean() {
         return Node.this;
      }

      public String getName() {
         return "scene";
      }

      protected void fireValueChangedEvent() {
      }

      public void fireSuperValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<Node, Cursor> CURSOR = new CssMetaData<Node, Cursor>("-fx-cursor", CursorConverter.getInstance()) {
         public boolean isSettable(Node var1) {
            return var1.miscProperties == null || var1.miscProperties.canSetCursor();
         }

         public StyleableProperty<Cursor> getStyleableProperty(Node var1) {
            return (StyleableProperty<Cursor>)var1.cursorProperty();
         }

         public Cursor getInitialValue(Node var1) {
            return var1.getInitialCursor();
         }
      };
      private static final CssMetaData<Node, Effect> EFFECT = new CssMetaData<Node, Effect>("-fx-effect", EffectConverter.getInstance()) {
         public boolean isSettable(Node var1) {
            return var1.miscProperties == null || var1.miscProperties.canSetEffect();
         }

         public StyleableProperty<Effect> getStyleableProperty(Node var1) {
            return (StyleableProperty<Effect>)var1.effectProperty();
         }
      };
      private static final CssMetaData<Node, Boolean> FOCUS_TRAVERSABLE = new CssMetaData<Node, Boolean>(
         "-fx-focus-traversable", BooleanConverter.getInstance(), Boolean.FALSE
      ) {
         public boolean isSettable(Node var1) {
            return var1.focusTraversable == null || !var1.focusTraversable.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Node var1) {
            return (StyleableProperty<Boolean>)var1.focusTraversableProperty();
         }

         public Boolean getInitialValue(Node var1) {
            return var1.getInitialFocusTraversable();
         }
      };
      private static final CssMetaData<Node, Number> OPACITY = new CssMetaData<Node, Number>("-fx-opacity", SizeConverter.getInstance(), 1.0) {
         public boolean isSettable(Node var1) {
            return var1.opacity == null || !var1.opacity.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.opacityProperty();
         }
      };
      private static final CssMetaData<Node, BlendMode> BLEND_MODE = new CssMetaData<Node, BlendMode>("-fx-blend-mode", new EnumConverter<>(BlendMode.class)) {
         public boolean isSettable(Node var1) {
            return var1.blendMode == null || !var1.blendMode.isBound();
         }

         public StyleableProperty<BlendMode> getStyleableProperty(Node var1) {
            return (StyleableProperty<BlendMode>)var1.blendModeProperty();
         }
      };
      private static final CssMetaData<Node, Number> ROTATE = new CssMetaData<Node, Number>("-fx-rotate", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.rotate == null || var1.nodeTransformation.canSetRotate();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.rotateProperty();
         }
      };
      private static final CssMetaData<Node, Number> SCALE_X = new CssMetaData<Node, Number>("-fx-scale-x", SizeConverter.getInstance(), 1.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.scaleX == null || var1.nodeTransformation.canSetScaleX();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.scaleXProperty();
         }
      };
      private static final CssMetaData<Node, Number> SCALE_Y = new CssMetaData<Node, Number>("-fx-scale-y", SizeConverter.getInstance(), 1.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.scaleY == null || var1.nodeTransformation.canSetScaleY();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.scaleYProperty();
         }
      };
      private static final CssMetaData<Node, Number> SCALE_Z = new CssMetaData<Node, Number>("-fx-scale-z", SizeConverter.getInstance(), 1.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.scaleZ == null || var1.nodeTransformation.canSetScaleZ();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.scaleZProperty();
         }
      };
      private static final CssMetaData<Node, Number> TRANSLATE_X = new CssMetaData<Node, Number>("-fx-translate-x", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.translateX == null || var1.nodeTransformation.canSetTranslateX();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.translateXProperty();
         }
      };
      private static final CssMetaData<Node, Number> TRANSLATE_Y = new CssMetaData<Node, Number>("-fx-translate-y", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.translateY == null || var1.nodeTransformation.canSetTranslateY();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.translateYProperty();
         }
      };
      private static final CssMetaData<Node, Number> TRANSLATE_Z = new CssMetaData<Node, Number>("-fx-translate-z", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(Node var1) {
            return var1.nodeTransformation == null || var1.nodeTransformation.translateZ == null || var1.nodeTransformation.canSetTranslateZ();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.translateZProperty();
         }
      };
      private static final CssMetaData<Node, Number> VIEW_ORDER = new CssMetaData<Node, Number>("-fx-view-order", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(Node var1) {
            return var1.miscProperties == null || var1.miscProperties.viewOrder == null || !var1.miscProperties.viewOrder.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(Node var1) {
            return (StyleableProperty<Number>)var1.viewOrderProperty();
         }
      };
      private static final CssMetaData<Node, Boolean> VISIBILITY = new CssMetaData<Node, Boolean>("visibility", new StyleConverter<String, Boolean>() {
         public Boolean convert(ParsedValue<String, Boolean> var1, Font var2) {
            String var3 = var1 != null ? (String)var1.getValue() : null;
            return "visible".equalsIgnoreCase(var3);
         }
      }, Boolean.TRUE) {
         public boolean isSettable(Node var1) {
            return var1.visible == null || !var1.visible.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(Node var1) {
            return (StyleableProperty<Boolean>)var1.visibleProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList();
         var0.add(CURSOR);
         var0.add(EFFECT);
         var0.add(FOCUS_TRAVERSABLE);
         var0.add(OPACITY);
         var0.add(BLEND_MODE);
         var0.add(ROTATE);
         var0.add(SCALE_X);
         var0.add(SCALE_Y);
         var0.add(SCALE_Z);
         var0.add(VIEW_ORDER);
         var0.add(TRANSLATE_X);
         var0.add(TRANSLATE_Y);
         var0.add(TRANSLATE_Z);
         var0.add(VISIBILITY);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   class TreeVisiblePropertyReadOnly extends BooleanExpression {
      private ExpressionHelper<Boolean> helper;
      private boolean valid;

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public void addListener(ChangeListener<? super Boolean> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super Boolean> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      protected void invalidate() {
         if (this.valid) {
            this.valid = false;
            ExpressionHelper.fireValueChangedEvent(this.helper);
         }
      }

      public boolean get() {
         this.valid = true;
         return Node.this.treeVisible;
      }
   }
}
