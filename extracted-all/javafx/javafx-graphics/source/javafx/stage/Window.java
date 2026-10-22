package javafx.stage;

import com.sun.javafx.FXPermissions;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.stage.EmbeddedWindow;
import com.sun.javafx.stage.WindowEventDispatcher;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.stage.WindowPeerListener;
import com.sun.javafx.tk.TKPulseListener;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;

public class Window implements EventTarget {
   private static ObservableList<Window> windows = FXCollections.observableArrayList();
   private static ObservableList<Window> unmodifiableWindows = FXCollections.unmodifiableObservableList(windows);
   private WindowHelper windowHelper = null;
   final AccessControlContext acc = AccessController.getContext();
   private WindowPeerListener peerListener;
   private TKStage peer;
   private Window.TKBoundsConfigurator peerBoundsConfigurator = new Window.TKBoundsConfigurator();
   private boolean sizeToScene = false;
   private static final float CENTER_ON_SCREEN_X_FRACTION = 0.5F;
   private static final float CENTER_ON_SCREEN_Y_FRACTION = 0.33333334F;
   private ReadOnlyDoubleWrapper outputScaleX = new ReadOnlyDoubleWrapper(this, "outputScaleX", 1.0);
   private ReadOnlyDoubleWrapper outputScaleY = new ReadOnlyDoubleWrapper(this, "outputScaleY", 1.0);
   private BooleanProperty forceIntegerRenderScale = new SimpleBooleanProperty(this, "forceIntegerRenderScale", false) {
      protected void invalidated() {
         Window.this.updateRenderScales(Window.this.getOutputScaleX(), Window.this.getOutputScaleY());
      }
   };
   private DoubleProperty renderScaleX = new SimpleDoubleProperty(this, "renderScaleX", 1.0) {
      protected void invalidated() {
         Window.this.peerBoundsConfigurator.setRenderScaleX(this.get());
      }
   };
   private DoubleProperty renderScaleY = new SimpleDoubleProperty(this, "renderScaleY", 1.0) {
      protected void invalidated() {
         Window.this.peerBoundsConfigurator.setRenderScaleY(this.get());
      }
   };
   private boolean xExplicit = false;
   private ReadOnlyDoubleWrapper x = new ReadOnlyDoubleWrapper(this, "x", Double.NaN);
   private boolean yExplicit = false;
   private ReadOnlyDoubleWrapper y = new ReadOnlyDoubleWrapper(this, "y", Double.NaN);
   private boolean widthExplicit = false;
   private ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper(this, "width", Double.NaN);
   private boolean heightExplicit = false;
   private ReadOnlyDoubleWrapper height = new ReadOnlyDoubleWrapper(this, "height", Double.NaN);
   private ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper() {
      protected void invalidated() {
         Window.this.focusChanged(this.get());
      }

      public Object getBean() {
         return Window.this;
      }

      public String getName() {
         return "focused";
      }
   };
   private static final Object USER_DATA_KEY = new Object();
   private ObservableMap<Object, Object> properties;
   private Window.SceneModel scene = new Window.SceneModel();
   private DoubleProperty opacity;
   private ObjectProperty<EventHandler<WindowEvent>> onCloseRequest;
   private ObjectProperty<EventHandler<WindowEvent>> onShowing;
   private ObjectProperty<EventHandler<WindowEvent>> onShown;
   private ObjectProperty<EventHandler<WindowEvent>> onHiding;
   private ObjectProperty<EventHandler<WindowEvent>> onHidden;
   private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper() {
      private boolean oldVisible;

      protected void invalidated() {
         boolean var1 = this.get();
         if (this.oldVisible != var1) {
            if (!this.oldVisible && var1) {
               Window.this.fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_SHOWING));
            } else {
               Window.this.fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_HIDING));
            }

            this.oldVisible = var1;
            WindowHelper.visibleChanging(Window.this, var1);
            if (var1) {
               Window.this.hasBeenVisible = true;
               Window.windows.add(Window.this);
            } else {
               Window.windows.remove(Window.this);
            }

            Toolkit var2 = Toolkit.getToolkit();
            if (Window.this.peer != null) {
               if (var1) {
                  if (Window.this.peerListener == null) {
                     Window.this.peerListener = new WindowPeerListener(Window.this);
                  }

                  Window.this.peer.setTKStageListener(Window.this.peerListener);
                  var2.addStageTkPulseListener(Window.this.peerBoundsConfigurator);
                  boolean var3 = Window.this instanceof EmbeddedWindow;
                  if (var3 && Window.this.getScene() != null) {
                     SceneHelper.initPeer(Window.this.getScene());
                     Window.this.peer.setScene(SceneHelper.getPeer(Window.this.getScene()));
                     SceneHelper.preferredSize(Window.this.getScene());
                  }

                  Window.this.updateOutputScales((double)Window.this.peer.getOutputScaleX(), (double)Window.this.peer.getOutputScaleY());
                  Window.this.peerBoundsConfigurator.setRenderScaleX(Window.this.getRenderScaleX());
                  Window.this.peerBoundsConfigurator.setRenderScaleY(Window.this.getRenderScaleY());
                  if (!var3 && Window.this.getScene() != null) {
                     SceneHelper.initPeer(Window.this.getScene());
                     Window.this.peer.setScene(SceneHelper.getPeer(Window.this.getScene()));
                     SceneHelper.preferredSize(Window.this.getScene());
                  }

                  if (Window.this.getScene() == null || Window.this.widthExplicit && Window.this.heightExplicit) {
                     Window.this.peerBoundsConfigurator.setSize(Window.this.getWidth(), Window.this.getHeight(), -1.0, -1.0);
                  } else {
                     Window.this.adjustSize(true);
                  }

                  if (!Window.this.xExplicit && !Window.this.yExplicit) {
                     Window.this.centerOnScreen();
                  } else {
                     Window.this.peerBoundsConfigurator.setLocation(Window.this.getX(), Window.this.getY(), 0.0F, 0.0F);
                  }

                  Window.this.applyBounds();
                  Window.this.peer.setOpacity((float)Window.this.getOpacity());
                  Window.this.peer.setVisible(true);
                  Window.this.fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_SHOWN));
               } else {
                  Window.this.peer.setVisible(false);
                  Window.this.fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_HIDDEN));
                  if (Window.this.getScene() != null) {
                     Window.this.peer.setScene(null);
                     SceneHelper.disposePeer(Window.this.getScene());
                     StyleManager.getInstance().forget(Window.this.getScene());
                  }

                  var2.removeStageTkPulseListener(Window.this.peerBoundsConfigurator);
                  Window.this.peer.setTKStageListener(null);
                  Window.this.peer.close();
               }
            }

            if (var1) {
               var2.requestNextPulse();
            }

            WindowHelper.visibleChanged(Window.this, var1);
            if (Window.this.sizeToScene) {
               if (var1) {
                  Window.this.sizeToScene();
               }

               Window.this.sizeToScene = false;
            }
         }
      }

      public Object getBean() {
         return Window.this;
      }

      public String getName() {
         return "showing";
      }
   };
   boolean hasBeenVisible = false;
   private ObjectProperty<EventDispatcher> eventDispatcher;
   private WindowEventDispatcher internalEventDispatcher;
   private int focusGrabCounter;
   private final ReadOnlyObjectWrapper<Screen> screen = new ReadOnlyObjectWrapper(Screen.getPrimary());

   public static ObservableList<Window> getWindows() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(FXPermissions.ACCESS_WINDOW_LIST_PERMISSION);
      }

      return unmodifiableWindows;
   }

   protected Window() {
      this.initializeInternalEventDispatcher();
      WindowHelper.initHelper(this);
   }

   WindowPeerListener getPeerListener() {
      return this.peerListener;
   }

   void setPeerListener(WindowPeerListener var1) {
      this.peerListener = var1;
   }

   TKStage getPeer() {
      return this.peer;
   }

   void setPeer(TKStage var1) {
      this.peer = var1;
   }

   public void sizeToScene() {
      if (this.getScene() != null && this.peer != null) {
         SceneHelper.preferredSize(this.getScene());
         this.adjustSize(false);
      } else {
         this.sizeToScene = true;
      }
   }

   private void adjustSize(boolean var1) {
      if (this.getScene() != null) {
         if (this.peer != null) {
            double var2 = this.getScene().getWidth();
            double var4 = var2 > 0.0 ? var2 : -1.0;
            double var6 = -1.0;
            if (var1 && this.widthExplicit) {
               var6 = this.getWidth();
            } else if (var4 <= 0.0) {
               var6 = this.widthExplicit ? this.getWidth() : -1.0;
            } else {
               this.widthExplicit = false;
            }

            double var8 = this.getScene().getHeight();
            double var10 = var8 > 0.0 ? var8 : -1.0;
            double var12 = -1.0;
            if (var1 && this.heightExplicit) {
               var12 = this.getHeight();
            } else if (var10 <= 0.0) {
               var12 = this.heightExplicit ? this.getHeight() : -1.0;
            } else {
               this.heightExplicit = false;
            }

            this.peerBoundsConfigurator.setSize(var6, var12, var4, var10);
            this.applyBounds();
         }
      }
   }

   public void centerOnScreen() {
      this.xExplicit = false;
      this.yExplicit = false;
      if (this.peer != null) {
         Rectangle2D var1 = this.getWindowScreen().getVisualBounds();
         double var2 = var1.getMinX() + (var1.getWidth() - this.getWidth()) * 0.5;
         double var4 = var1.getMinY() + (var1.getHeight() - this.getHeight()) * 0.33333334F;
         this.x.set(var2);
         this.y.set(var4);
         this.peerBoundsConfigurator.setLocation(var2, var4, 0.5F, 0.33333334F);
         this.applyBounds();
      }
   }

   private void updateOutputScales(double var1, double var3) {
      this.updateRenderScales(var1, var3);
      this.outputScaleX.set(var1);
      this.outputScaleY.set(var3);
   }

   void updateRenderScales(double var1, double var3) {
      boolean var5 = this.forceIntegerRenderScale.get();
      if (!this.renderScaleX.isBound()) {
         this.renderScaleX.set(var5 ? Math.ceil(var1) : var1);
      }

      if (!this.renderScaleY.isBound()) {
         this.renderScaleY.set(var5 ? Math.ceil(var3) : var3);
      }
   }

   public final double getOutputScaleX() {
      return this.outputScaleX.get();
   }

   public final ReadOnlyDoubleProperty outputScaleXProperty() {
      return this.outputScaleX.getReadOnlyProperty();
   }

   public final double getOutputScaleY() {
      return this.outputScaleY.get();
   }

   public final ReadOnlyDoubleProperty outputScaleYProperty() {
      return this.outputScaleY.getReadOnlyProperty();
   }

   public final void setForceIntegerRenderScale(boolean var1) {
      this.forceIntegerRenderScale.set(var1);
   }

   public final boolean isForceIntegerRenderScale() {
      return this.forceIntegerRenderScale.get();
   }

   public final BooleanProperty forceIntegerRenderScaleProperty() {
      return this.forceIntegerRenderScale;
   }

   public final void setRenderScaleX(double var1) {
      this.renderScaleX.set(var1);
   }

   public final double getRenderScaleX() {
      return this.renderScaleX.get();
   }

   public final DoubleProperty renderScaleXProperty() {
      return this.renderScaleX;
   }

   public final void setRenderScaleY(double var1) {
      this.renderScaleY.set(var1);
   }

   public final double getRenderScaleY() {
      return this.renderScaleY.get();
   }

   public final DoubleProperty renderScaleYProperty() {
      return this.renderScaleY;
   }

   public final void setX(double var1) {
      this.setXInternal(var1);
   }

   public final double getX() {
      return this.x.get();
   }

   public final ReadOnlyDoubleProperty xProperty() {
      return this.x.getReadOnlyProperty();
   }

   void setXInternal(double var1) {
      this.x.set(var1);
      this.peerBoundsConfigurator.setX(var1, 0.0F);
      this.xExplicit = true;
   }

   public final void setY(double var1) {
      this.setYInternal(var1);
   }

   public final double getY() {
      return this.y.get();
   }

   public final ReadOnlyDoubleProperty yProperty() {
      return this.y.getReadOnlyProperty();
   }

   void setYInternal(double var1) {
      this.y.set(var1);
      this.peerBoundsConfigurator.setY(var1, 0.0F);
      this.yExplicit = true;
   }

   void notifyLocationChanged(double var1, double var3) {
      this.x.set(var1);
      this.y.set(var3);
   }

   public final void setWidth(double var1) {
      this.width.set(var1);
      this.peerBoundsConfigurator.setWindowWidth(var1);
      this.widthExplicit = true;
   }

   public final double getWidth() {
      return this.width.get();
   }

   public final ReadOnlyDoubleProperty widthProperty() {
      return this.width.getReadOnlyProperty();
   }

   public final void setHeight(double var1) {
      this.height.set(var1);
      this.peerBoundsConfigurator.setWindowHeight(var1);
      this.heightExplicit = true;
   }

   public final double getHeight() {
      return this.height.get();
   }

   public final ReadOnlyDoubleProperty heightProperty() {
      return this.height.getReadOnlyProperty();
   }

   void notifySizeChanged(double var1, double var3) {
      this.width.set(var1);
      this.height.set(var3);
   }

   final void setFocused(boolean var1) {
      this.focused.set(var1);
   }

   public final void requestFocus() {
      if (this.peer != null) {
         this.peer.requestFocus();
      }
   }

   public final boolean isFocused() {
      return this.focused.get();
   }

   public final ReadOnlyBooleanProperty focusedProperty() {
      return this.focused.getReadOnlyProperty();
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

   protected void setScene(Scene var1) {
      this.scene.set(var1);
   }

   public final Scene getScene() {
      return (Scene)this.scene.get();
   }

   public final ReadOnlyObjectProperty<Scene> sceneProperty() {
      return this.scene.getReadOnlyProperty();
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
            protected void invalidated() {
               if (Window.this.peer != null) {
                  Window.this.peer.setOpacity((float)this.get());
               }
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "opacity";
            }
         };
      }

      return this.opacity;
   }

   public final void setOnCloseRequest(EventHandler<WindowEvent> var1) {
      this.onCloseRequestProperty().set(var1);
   }

   public final EventHandler<WindowEvent> getOnCloseRequest() {
      return this.onCloseRequest != null ? (EventHandler)this.onCloseRequest.get() : null;
   }

   public final ObjectProperty<EventHandler<WindowEvent>> onCloseRequestProperty() {
      if (this.onCloseRequest == null) {
         this.onCloseRequest = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
            protected void invalidated() {
               Window.this.setEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (EventHandler)this.get());
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "onCloseRequest";
            }
         };
      }

      return this.onCloseRequest;
   }

   public final void setOnShowing(EventHandler<WindowEvent> var1) {
      this.onShowingProperty().set(var1);
   }

   public final EventHandler<WindowEvent> getOnShowing() {
      return this.onShowing == null ? null : (EventHandler)this.onShowing.get();
   }

   public final ObjectProperty<EventHandler<WindowEvent>> onShowingProperty() {
      if (this.onShowing == null) {
         this.onShowing = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
            protected void invalidated() {
               Window.this.setEventHandler(WindowEvent.WINDOW_SHOWING, (EventHandler)this.get());
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "onShowing";
            }
         };
      }

      return this.onShowing;
   }

   public final void setOnShown(EventHandler<WindowEvent> var1) {
      this.onShownProperty().set(var1);
   }

   public final EventHandler<WindowEvent> getOnShown() {
      return this.onShown == null ? null : (EventHandler)this.onShown.get();
   }

   public final ObjectProperty<EventHandler<WindowEvent>> onShownProperty() {
      if (this.onShown == null) {
         this.onShown = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
            protected void invalidated() {
               Window.this.setEventHandler(WindowEvent.WINDOW_SHOWN, (EventHandler)this.get());
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "onShown";
            }
         };
      }

      return this.onShown;
   }

   public final void setOnHiding(EventHandler<WindowEvent> var1) {
      this.onHidingProperty().set(var1);
   }

   public final EventHandler<WindowEvent> getOnHiding() {
      return this.onHiding == null ? null : (EventHandler)this.onHiding.get();
   }

   public final ObjectProperty<EventHandler<WindowEvent>> onHidingProperty() {
      if (this.onHiding == null) {
         this.onHiding = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
            protected void invalidated() {
               Window.this.setEventHandler(WindowEvent.WINDOW_HIDING, (EventHandler)this.get());
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "onHiding";
            }
         };
      }

      return this.onHiding;
   }

   public final void setOnHidden(EventHandler<WindowEvent> var1) {
      this.onHiddenProperty().set(var1);
   }

   public final EventHandler<WindowEvent> getOnHidden() {
      return this.onHidden == null ? null : (EventHandler)this.onHidden.get();
   }

   public final ObjectProperty<EventHandler<WindowEvent>> onHiddenProperty() {
      if (this.onHidden == null) {
         this.onHidden = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
            protected void invalidated() {
               Window.this.setEventHandler(WindowEvent.WINDOW_HIDDEN, (EventHandler)this.get());
            }

            public Object getBean() {
               return Window.this;
            }

            public String getName() {
               return "onHidden";
            }
         };
      }

      return this.onHidden;
   }

   private void setShowing(boolean var1) {
      Toolkit.getToolkit().checkFxUserThread();
      this.showing.set(var1);
   }

   public final boolean isShowing() {
      return this.showing.get();
   }

   public final ReadOnlyBooleanProperty showingProperty() {
      return this.showing.getReadOnlyProperty();
   }

   protected void show() {
      this.setShowing(true);
   }

   public void hide() {
      this.setShowing(false);
   }

   private void doVisibleChanging(boolean var1) {
      if (var1 && this.getScene() != null) {
         NodeHelper.reapplyCSS(this.getScene().getRoot());
      }
   }

   private void doVisibleChanged(boolean var1) {
      assert this.peer != null;

      if (!var1) {
         this.peerListener = null;
         this.peer = null;
      }
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

   WindowEventDispatcher getInternalEventDispatcher() {
      this.initializeInternalEventDispatcher();
      return this.internalEventDispatcher;
   }

   private void initializeInternalEventDispatcher() {
      if (this.internalEventDispatcher == null) {
         this.internalEventDispatcher = this.createInternalEventDispatcher();
         this.eventDispatcher = new SimpleObjectProperty(this, "eventDispatcher", this.internalEventDispatcher);
      }
   }

   WindowEventDispatcher createInternalEventDispatcher() {
      return new WindowEventDispatcher(this);
   }

   public final void fireEvent(Event var1) {
      Event.fireEvent(this, var1);
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      if (this.eventDispatcher != null) {
         EventDispatcher var2 = (EventDispatcher)this.eventDispatcher.get();
         if (var2 != null) {
            var1 = var1.prepend(var2);
         }
      }

      return var1;
   }

   void increaseFocusGrabCounter() {
      if (++this.focusGrabCounter == 1 && this.peer != null && this.isFocused()) {
         this.peer.grabFocus();
      }
   }

   void decreaseFocusGrabCounter() {
      if (--this.focusGrabCounter == 0 && this.peer != null) {
         this.peer.ungrabFocus();
      }
   }

   private void focusChanged(boolean var1) {
      if (this.focusGrabCounter > 0 && this.peer != null && var1) {
         this.peer.grabFocus();
      }
   }

   final void applyBounds() {
      this.peerBoundsConfigurator.apply();
   }

   Window getWindowOwner() {
      return null;
   }

   private Screen getWindowScreen() {
      Window var1 = this;

      while (Double.isNaN(var1.getX()) || Double.isNaN(var1.getY()) || Double.isNaN(var1.getWidth()) || Double.isNaN(var1.getHeight())) {
         var1 = var1.getWindowOwner();
         if (var1 == null) {
            return Screen.getPrimary();
         }
      }

      return Utils.getScreenForRectangle(new Rectangle2D(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight()));
   }

   private ReadOnlyObjectProperty<Screen> screenProperty() {
      return this.screen.getReadOnlyProperty();
   }

   private void notifyScreenChanged(Object var1, Object var2) {
      this.screen.set(Screen.getScreenForNative(var2));
   }

   static {
      WindowHelper.setWindowAccessor(new WindowHelper.WindowAccessor() {
         @Override
         public WindowHelper getHelper(Window var1) {
            return var1.windowHelper;
         }

         @Override
         public void setHelper(Window var1, WindowHelper var2) {
            var1.windowHelper = var2;
         }

         @Override
         public void doVisibleChanging(Window var1, boolean var2) {
            var1.doVisibleChanging(var2);
         }

         @Override
         public void doVisibleChanged(Window var1, boolean var2) {
            var1.doVisibleChanged(var2);
         }

         @Override
         public TKStage getPeer(Window var1) {
            return var1.getPeer();
         }

         @Override
         public void setPeer(Window var1, TKStage var2) {
            var1.setPeer(var2);
         }

         @Override
         public WindowPeerListener getPeerListener(Window var1) {
            return var1.getPeerListener();
         }

         @Override
         public void setPeerListener(Window var1, WindowPeerListener var2) {
            var1.setPeerListener(var2);
         }

         @Override
         public void setFocused(Window var1, boolean var2) {
            var1.setFocused(var2);
         }

         @Override
         public void notifyLocationChanged(Window var1, double var2, double var4) {
            var1.notifyLocationChanged(var2, var4);
         }

         @Override
         public void notifySizeChanged(Window var1, double var2, double var4) {
            var1.notifySizeChanged(var2, var4);
         }

         @Override
         public void notifyScaleChanged(Window var1, double var2, double var4) {
            var1.updateOutputScales(var2, var4);
         }

         @Override
         public void notifyScreenChanged(Window var1, Object var2, Object var3) {
            var1.notifyScreenChanged(var2, var3);
         }

         @Override
         public float getPlatformScaleX(Window var1) {
            TKStage var2 = var1.getPeer();
            return var2 == null ? 1.0F : var2.getPlatformScaleX();
         }

         @Override
         public float getPlatformScaleY(Window var1) {
            TKStage var2 = var1.getPeer();
            return var2 == null ? 1.0F : var2.getPlatformScaleY();
         }

         @Override
         public ReadOnlyObjectProperty<Screen> screenProperty(Window var1) {
            return var1.screenProperty();
         }

         @Override
         public AccessControlContext getAccessControlContext(Window var1) {
            return var1.acc;
         }
      });
   }

   private final class SceneModel extends ReadOnlyObjectWrapper<Scene> {
      private Scene oldScene;

      protected void invalidated() {
         Scene var1 = (Scene)this.get();
         if (this.oldScene != var1) {
            if (Window.this.peer != null) {
               Toolkit.getToolkit().checkFxUserThread();
            }

            this.updatePeerScene(null);
            if (this.oldScene != null) {
               SceneHelper.setWindow(this.oldScene, null);
               StyleManager.getInstance().forget(this.oldScene);
            }

            if (var1 != null) {
               Window var2 = var1.getWindow();
               if (var2 != null) {
                  var2.setScene(null);
               }

               SceneHelper.setWindow(var1, Window.this);
               this.updatePeerScene(SceneHelper.getPeer(var1));
               if (Window.this.isShowing()) {
                  NodeHelper.reapplyCSS(var1.getRoot());
                  if (!Window.this.widthExplicit || !Window.this.heightExplicit) {
                     SceneHelper.preferredSize(Window.this.getScene());
                     Window.this.adjustSize(true);
                  }
               }
            }

            this.oldScene = var1;
         }
      }

      public Object getBean() {
         return Window.this;
      }

      public String getName() {
         return "scene";
      }

      private void updatePeerScene(TKScene var1) {
         if (Window.this.peer != null) {
            Window.this.peer.setScene(var1);
         }
      }
   }

   private final class TKBoundsConfigurator implements TKPulseListener {
      private double renderScaleX;
      private double renderScaleY;
      private double x;
      private double y;
      private float xGravity;
      private float yGravity;
      private double windowWidth;
      private double windowHeight;
      private double clientWidth;
      private double clientHeight;
      private boolean dirty;

      public TKBoundsConfigurator() {
         this.reset();
      }

      public void setRenderScaleX(double var1) {
         this.renderScaleX = var1;
         this.setDirty();
      }

      public void setRenderScaleY(double var1) {
         this.renderScaleY = var1;
         this.setDirty();
      }

      public void setX(double var1, float var3) {
         this.x = var1;
         this.xGravity = var3;
         this.setDirty();
      }

      public void setY(double var1, float var3) {
         this.y = var1;
         this.yGravity = var3;
         this.setDirty();
      }

      public void setWindowWidth(double var1) {
         this.windowWidth = var1;
         this.setDirty();
      }

      public void setWindowHeight(double var1) {
         this.windowHeight = var1;
         this.setDirty();
      }

      public void setClientWidth(double var1) {
         this.clientWidth = var1;
         this.setDirty();
      }

      public void setClientHeight(double var1) {
         this.clientHeight = var1;
         this.setDirty();
      }

      public void setLocation(double var1, double var3, float var5, float var6) {
         this.x = var1;
         this.y = var3;
         this.xGravity = var5;
         this.yGravity = var6;
         this.setDirty();
      }

      public void setSize(double var1, double var3, double var5, double var7) {
         this.windowWidth = var1;
         this.windowHeight = var3;
         this.clientWidth = var5;
         this.clientHeight = var7;
         this.setDirty();
      }

      public void apply() {
         if (this.dirty) {
            if (Window.this.peer == null) {
               this.reset();
               return;
            }

            boolean var1 = !Double.isNaN(this.x);
            float var2 = var1 ? (float)this.x : 0.0F;
            boolean var3 = !Double.isNaN(this.y);
            float var4 = var3 ? (float)this.y : 0.0F;
            float var5 = (float)this.windowWidth;
            float var6 = (float)this.windowHeight;
            float var7 = (float)this.clientWidth;
            float var8 = (float)this.clientHeight;
            float var9 = this.xGravity;
            float var10 = this.yGravity;
            float var11 = (float)this.renderScaleX;
            float var12 = (float)this.renderScaleY;
            this.reset();
            Window.this.peer.setBounds(var2, var4, var1, var3, var5, var6, var7, var8, var9, var10, var11, var12);
         }
      }

      @Override
      public void pulse() {
         this.apply();
      }

      private void reset() {
         this.renderScaleX = 0.0;
         this.renderScaleY = 0.0;
         this.x = Double.NaN;
         this.y = Double.NaN;
         this.xGravity = 0.0F;
         this.yGravity = 0.0F;
         this.windowWidth = -1.0;
         this.windowHeight = -1.0;
         this.clientWidth = -1.0;
         this.clientHeight = -1.0;
         this.dirty = false;
      }

      private void setDirty() {
         if (!this.dirty) {
            Toolkit.getToolkit().requestNextPulse();
            this.dirty = true;
         }
      }
   }
}
