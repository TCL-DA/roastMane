package javafx.scene.control;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import com.sun.javafx.scene.control.ControlHelper;
import com.sun.javafx.scene.control.Logging;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.CssParser.ParseError;
import javafx.css.converter.StringConverter;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Region;

public abstract class Control extends Region implements Skinnable {
   private List<CssMetaData<? extends Styleable, ?>> styleableProperties;
   private SkinBase<?> skinBase;
   private static final EventHandler<ContextMenuEvent> contextMenuHandler;
   private ObjectProperty<Skin<?>> skin = new StyleableObjectProperty<Skin<?>>() {
      private Skin<?> oldValue;

      public void set(Skin<?> var1) {
         if (var1 == null ? this.oldValue != null : this.oldValue == null || !var1.getClass().equals(this.oldValue.getClass())) {
            super.set(var1);
         }
      }

      protected void invalidated() {
         Skin var1 = (Skin)this.get();
         Control.this.currentSkinClassName = var1 == null ? null : var1.getClass().getName();
         Control.this.skinClassNameProperty().set(Control.this.currentSkinClassName);
         if (this.oldValue != null) {
            this.oldValue.dispose();
         }

         this.oldValue = var1;
         Control.this.skinBase = null;
         if (var1 instanceof SkinBase) {
            Control.this.skinBase = (SkinBase<?>)var1;
         } else {
            Node var2 = Control.this.getSkinNode();
            if (var2 != null) {
               Control.this.getChildren().setAll(new Node[]{var2});
            } else {
               Control.this.getChildren().clear();
            }
         }

         Control.this.styleableProperties = null;
         NodeHelper.reapplyCSS(Control.this);
         PlatformLogger var3 = Logging.getControlsLogger();
         if (var3.isLoggable(Level.FINEST)) {
            var3.finest("Stored skin[" + this.getValue() + "] on " + this);
         }
      }

      public CssMetaData getCssMetaData() {
         return Control.StyleableProperties.SKIN;
      }

      public Object getBean() {
         return Control.this;
      }

      public String getName() {
         return "skin";
      }
   };
   private ObjectProperty<Tooltip> tooltip;
   private ObjectProperty<ContextMenu> contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu") {
      private WeakReference<ContextMenu> contextMenuRef;

      protected void invalidated() {
         ContextMenu var1 = this.contextMenuRef == null ? null : this.contextMenuRef.get();
         if (var1 != null) {
            ControlAcceleratorSupport.removeAcceleratorsFromScene(var1.getItems(), Control.this);
         }

         ContextMenu var2 = (ContextMenu)this.get();
         this.contextMenuRef = new WeakReference<>(var2);
         if (var2 != null) {
            var2.setShowRelativeToWindow(true);
            ControlAcceleratorSupport.addAcceleratorsIntoScene(var2.getItems(), Control.this);
         }
      }
   };
   private String currentSkinClassName;
   private StringProperty skinClassName;
   private boolean skinCreationLocked;

   private static Class<?> loadClass(String var0, Object var1) throws ClassNotFoundException {
      try {
         return Class.forName(var0, false, Control.class.getClassLoader());
      } catch (ClassNotFoundException var7) {
         if (Thread.currentThread().getContextClassLoader() != null) {
            try {
               ClassLoader var8 = Thread.currentThread().getContextClassLoader();
               return Class.forName(var0, false, var8);
            } catch (ClassNotFoundException var6) {
            }
         }

         if (var1 != null) {
            for (Class var3 = var1.getClass(); var3 != null; var3 = var3.getSuperclass()) {
               try {
                  ClassLoader var4 = var3.getClassLoader();
                  return Class.forName(var0, false, var4);
               } catch (ClassNotFoundException var5) {
               }
            }
         }

         throw var7;
      }
   }

   @Override
   public final ObjectProperty<Skin<?>> skinProperty() {
      return this.skin;
   }

   @Override
   public final void setSkin(Skin<?> var1) {
      this.skinProperty().set(var1);
   }

   @Override
   public final Skin<?> getSkin() {
      return (Skin<?>)this.skinProperty().getValue();
   }

   public final ObjectProperty<Tooltip> tooltipProperty() {
      if (this.tooltip == null) {
         this.tooltip = new ObjectPropertyBase<Tooltip>() {
            private Tooltip old = null;

            protected void invalidated() {
               Tooltip var1 = (Tooltip)this.get();
               if (var1 != this.old) {
                  if (this.old != null) {
                     Tooltip.uninstall(Control.this, this.old);
                  }

                  if (var1 != null) {
                     Tooltip.install(Control.this, var1);
                  }

                  this.old = var1;
               }
            }

            public Object getBean() {
               return Control.this;
            }

            public String getName() {
               return "tooltip";
            }
         };
      }

      return this.tooltip;
   }

   public final void setTooltip(Tooltip var1) {
      this.tooltipProperty().setValue(var1);
   }

   public final Tooltip getTooltip() {
      return this.tooltip == null ? null : (Tooltip)this.tooltip.getValue();
   }

   public final ObjectProperty<ContextMenu> contextMenuProperty() {
      return this.contextMenu;
   }

   public final void setContextMenu(ContextMenu var1) {
      this.contextMenu.setValue(var1);
   }

   public final ContextMenu getContextMenu() {
      return this.contextMenu == null ? null : (ContextMenu)this.contextMenu.getValue();
   }

   protected Control() {
      ControlHelper.initHelper(this);
      this.currentSkinClassName = null;
      this.skinCreationLocked = false;
      StyleableProperty var1 = (StyleableProperty)this.focusTraversableProperty();
      var1.applyStyle(null, Boolean.TRUE);
      this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, contextMenuHandler);
   }

   public boolean isResizable() {
      return true;
   }

   protected double computeMinWidth(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computeMinWidth(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.minWidth(var1);
      }
   }

   protected double computeMinHeight(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computeMinHeight(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.minHeight(var1);
      }
   }

   protected double computeMaxWidth(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computeMaxWidth(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.maxWidth(var1);
      }
   }

   protected double computeMaxHeight(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computeMaxHeight(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.maxHeight(var1);
      }
   }

   protected double computePrefWidth(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computePrefWidth(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.prefWidth(var1);
      }
   }

   protected double computePrefHeight(double var1) {
      if (this.skinBase != null) {
         return this.skinBase.computePrefHeight(var1, this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var3 = this.getSkinNode();
         return var3 == null ? 0.0 : var3.prefHeight(var1);
      }
   }

   public double getBaselineOffset() {
      if (this.skinBase != null) {
         return this.skinBase.computeBaselineOffset(this.snappedTopInset(), this.snappedRightInset(), this.snappedBottomInset(), this.snappedLeftInset());
      } else {
         Node var1 = this.getSkinNode();
         return var1 == null ? 0.0 : var1.getBaselineOffset();
      }
   }

   protected void layoutChildren() {
      if (this.skinBase != null) {
         double var1 = this.snappedLeftInset();
         double var3 = this.snappedTopInset();
         double var5 = this.snapSizeX(this.getWidth()) - var1 - this.snappedRightInset();
         double var7 = this.snapSizeY(this.getHeight()) - var3 - this.snappedBottomInset();
         this.skinBase.layoutChildren(var1, var3, var5, var7);
      } else {
         Node var9 = this.getSkinNode();
         if (var9 != null) {
            var9.resizeRelocate(0.0, 0.0, this.getWidth(), this.getHeight());
         }
      }
   }

   protected Skin<?> createDefaultSkin() {
      return null;
   }

   ObservableList<Node> getControlChildren() {
      return this.getChildren();
   }

   private Node getSkinNode() {
      assert this.skinBase == null;

      Skin var1 = this.getSkin();
      return var1 == null ? null : var1.getNode();
   }

   StringProperty skinClassNameProperty() {
      if (this.skinClassName == null) {
         this.skinClassName = new StyleableStringProperty() {
            public void set(String var1) {
               if (var1 != null && !var1.isEmpty() && !var1.equals(this.get())) {
                  super.set(var1);
               }
            }

            public void invalidated() {
               if (this.get() != null && !this.get().equals(Control.this.currentSkinClassName)) {
                  Control.loadSkinClass(Control.this, (String)Control.this.skinClassName.get());
               }
            }

            public Object getBean() {
               return Control.this;
            }

            public String getName() {
               return "skinClassName";
            }

            public CssMetaData<Control, String> getCssMetaData() {
               return Control.StyleableProperties.SKIN;
            }
         };
      }

      return this.skinClassName;
   }

   static void loadSkinClass(Skinnable var0, String var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            Class var12 = loadClass(var1, var0);
            if (!Skin.class.isAssignableFrom(var12)) {
               String var16 = "'" + var1 + "' is not a valid Skin class for control " + var0;
               ObservableList var20 = StyleManager.getErrors();
               if (var20 != null) {
                  ParseError var25 = new ParseError(var16);
                  var20.add(var25);
               }

               Logging.getControlsLogger().severe(var16);
               return;
            }

            Constructor[] var15 = var12.getConstructors();
            Constructor var19 = null;

            for (Constructor var8 : var15) {
               Class[] var9 = var8.getParameterTypes();
               if (var9.length == 1 && Skinnable.class.isAssignableFrom(var9[0])) {
                  var19 = var8;
                  break;
               }
            }

            if (var19 == null) {
               String var23 = "No valid constructor defined in '"
                  + var1
                  + "' for control "
                  + var0
                  + ".\r\nYou must provide a constructor that accepts a single Skinnable (e.g. Control or PopupControl) parameter in "
                  + var1
                  + ".";
               ObservableList var26 = StyleManager.getErrors();
               if (var26 != null) {
                  ParseError var27 = new ParseError(var23);
                  var26.add(var27);
               }

               Logging.getControlsLogger().severe(var23);
            } else {
               Skin var24 = (Skin)var19.newInstance(var0);
               var0.skinProperty().set(var24);
            }
         } catch (InvocationTargetException var10) {
            String var14 = "Failed to load skin '" + var1 + "' for control " + var0;
            ObservableList var18 = StyleManager.getErrors();
            if (var18 != null) {
               ParseError var21 = new ParseError(var14 + " :" + var10.getLocalizedMessage());
               var18.add(var21);
            }

            Logging.getControlsLogger().severe(var14, var10.getCause());
         } catch (Exception var11) {
            String var13 = "Failed to load skin '" + var1 + "' for control " + var0;
            ObservableList var17 = StyleManager.getErrors();
            if (var17 != null) {
               ParseError var5 = new ParseError(var13 + " :" + var11.getLocalizedMessage());
               var17.add(var5);
            }

            Logging.getControlsLogger().severe(var13, var11);
         }
      } else {
         String var2 = "Empty -fx-skin property specified for control " + var0;
         ObservableList var3 = StyleManager.getErrors();
         if (var3 != null) {
            ParseError var4 = new ParseError(var2);
            var3.add(var4);
         }

         Logging.getControlsLogger().severe(var2);
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Control.StyleableProperties.STYLEABLES;
   }

   public final List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      if (this.styleableProperties == null) {
         HashMap var1 = new HashMap();
         List var2 = this.getControlCssMetaData();
         int var3 = 0;

         for (int var4 = var2 != null ? var2.size() : 0; var3 < var4; var3++) {
            CssMetaData var5 = (CssMetaData)var2.get(var3);
            if (var5 != null) {
               var1.put(var5.getProperty(), var5);
            }
         }

         var2 = this.skinBase != null ? this.skinBase.getCssMetaData() : null;
         var3 = 0;

         for (int var8 = var2 != null ? var2.size() : 0; var3 < var8; var3++) {
            CssMetaData var9 = (CssMetaData)var2.get(var3);
            if (var9 != null) {
               var1.put(var9.getProperty(), var9);
            }
         }

         this.styleableProperties = new ArrayList<>();
         this.styleableProperties.addAll(var1.values());
      }

      return this.styleableProperties;
   }

   protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private void doProcessCSS() {
      ControlHelper.superProcessCSS(this);
      if (this.getSkin() == null) {
         if (!this.skinCreationLocked) {
            try {
               this.skinCreationLocked = true;
               Skin var1 = this.createDefaultSkin();
               if (var1 != null) {
                  this.skinProperty().set(var1);
                  ControlHelper.superProcessCSS(this);
               } else {
                  String var2 = "The -fx-skin property has not been defined in CSS for " + this + " and createDefaultSkin() returned null.";
                  ObservableList var3 = StyleManager.getErrors();
                  if (var3 != null) {
                     ParseError var4 = new ParseError(var2);
                     var3.add(var4);
                  }

                  Logging.getControlsLogger().severe(var2);
               }
            } finally {
               this.skinCreationLocked = false;
            }
         }
      }
   }

   protected Boolean getInitialFocusTraversable() {
      return Boolean.TRUE;
   }

   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case HELP:
            String var5 = this.getAccessibleHelp();
            if (var5 != null && !var5.isEmpty()) {
               return var5;
            }

            Tooltip var4 = this.getTooltip();
            return var4 == null ? "" : var4.getText();
         default:
            if (this.skinBase != null) {
               Object var3 = this.skinBase.queryAccessibleAttribute(var1, var2);
               if (var3 != null) {
                  return var3;
               }
            }

            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      if (this.skinBase != null) {
         this.skinBase.executeAccessibleAction(var1, var2);
      }

      super.executeAccessibleAction(var1, var2);
   }

   static {
      ControlHelper.setControlAccessor(new ControlHelper.ControlAccessor() {
         @Override
         public void doProcessCSS(Node var1) {
            ((Control)var1).doProcessCSS();
         }

         @Override
         public StringProperty skinClassNameProperty(Control var1) {
            return var1.skinClassNameProperty();
         }
      });
      if (Application.getUserAgentStylesheet() == null) {
         PlatformImpl.setDefaultPlatformUserAgentStylesheet();
      }

      contextMenuHandler = var0 -> {
         if (!var0.isConsumed()) {
            Object var1 = var0.getSource();
            if (var1 instanceof Control) {
               Control var2 = (Control)var1;
               if (var2.getContextMenu() != null) {
                  var2.getContextMenu().show(var2, var0.getScreenX(), var0.getScreenY());
                  var0.consume();
               }
            }
         }
      };
   }

   private static class StyleableProperties {
      private static final CssMetaData<Control, String> SKIN = new CssMetaData<Control, String>("-fx-skin", StringConverter.getInstance()) {
         public boolean isSettable(Control var1) {
            return var1.skin == null || !var1.skin.isBound();
         }

         public StyleableProperty<String> getStyleableProperty(Control var1) {
            return (StyleableProperty<String>)var1.skinClassNameProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList(Region.getClassCssMetaData());
         var0.add(SKIN);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
