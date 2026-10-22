package javafx.scene.control.skin;

import com.sun.javafx.FXPermissions;
import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.scene.control.MenuBarButton;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

public class MenuBarSkin extends SkinBase<MenuBar> {
   private static final ObservableList<Window> stages;
   private final HBox container;
   private Menu openMenu;
   private MenuBarButton openMenuButton;
   private Menu focusedMenu;
   private int focusedMenuIndex = -1;
   private static WeakHashMap<Stage, Reference<MenuBarSkin>> systemMenuMap;
   private static List<MenuBase> wrappedDefaultMenus = new ArrayList<>();
   private static Stage currentMenuBarStage;
   private List<MenuBase> wrappedMenus;
   private WeakEventHandler<KeyEvent> weakSceneKeyEventHandler;
   private WeakEventHandler<MouseEvent> weakSceneMouseEventHandler;
   private WeakEventHandler<KeyEvent> weakSceneAltKeyEventHandler;
   private WeakChangeListener<Boolean> weakWindowFocusListener;
   private WeakChangeListener<Window> weakWindowSceneListener;
   private EventHandler<KeyEvent> keyEventHandler;
   private EventHandler<KeyEvent> altKeyEventHandler;
   private EventHandler<MouseEvent> mouseEventHandler;
   private ChangeListener<Boolean> menuBarFocusedPropertyListener;
   private ChangeListener<Scene> sceneChangeListener;
   private ChangeListener<Boolean> menuVisibilityChangeListener;
   private boolean pendingDismiss = false;
   private boolean altKeyPressed = false;
   private EventHandler<ActionEvent> menuActionEventHandler = var1x -> {
      if (var1x.getSource() instanceof CustomMenuItem) {
         CustomMenuItem var2x = (CustomMenuItem)var1x.getSource();
         if (!var2x.isHideOnClick()) {
            return;
         }
      }

      this.unSelectMenus();
   };
   private ListChangeListener<MenuItem> menuItemListener = var1x -> {
      while (var1x.next()) {
         for (MenuItem var3x : var1x.getAddedSubList()) {
            this.updateActionListeners(var3x, true);
         }

         for (MenuItem var5 : var1x.getRemoved()) {
            this.updateActionListeners(var5, false);
         }
      }
   };
   Runnable firstMenuRunnable = new Runnable() {
      @Override
      public void run() {
         if (MenuBarSkin.this.container.getChildren().size() > 0 && MenuBarSkin.this.container.getChildren().get(0) instanceof MenuButton) {
            if (MenuBarSkin.this.focusedMenuIndex != 0) {
               MenuBarSkin.this.unSelectMenus();
               MenuBarSkin.this.menuModeStart(0);
               MenuBarSkin.this.openMenuButton = (MenuBarButton)MenuBarSkin.this.container.getChildren().get(0);
               MenuBarSkin.this.openMenuButton.setHover();
            } else {
               MenuBarSkin.this.unSelectMenus();
            }
         }
      }
   };
   private DoubleProperty spacing;
   private ObjectProperty<Pos> containerAlignment;
   private static final CssMetaData<MenuBar, Number> SPACING = new CssMetaData<MenuBar, Number>("-fx-spacing", SizeConverter.getInstance(), 0.0) {
      public boolean isSettable(MenuBar var1) {
         MenuBarSkin var2 = (MenuBarSkin)var1.getSkin();
         return var2.spacing == null || !var2.spacing.isBound();
      }

      public StyleableProperty<Number> getStyleableProperty(MenuBar var1) {
         MenuBarSkin var2 = (MenuBarSkin)var1.getSkin();
         return (StyleableProperty<Number>)var2.spacingProperty();
      }
   };
   private static final CssMetaData<MenuBar, Pos> ALIGNMENT = new CssMetaData<MenuBar, Pos>("-fx-alignment", new EnumConverter(Pos.class), Pos.TOP_LEFT) {
      public boolean isSettable(MenuBar var1) {
         MenuBarSkin var2 = (MenuBarSkin)var1.getSkin();
         return var2.containerAlignment == null || !var2.containerAlignment.isBound();
      }

      public StyleableProperty<Pos> getStyleableProperty(MenuBar var1) {
         MenuBarSkin var2 = (MenuBarSkin)var1.getSkin();
         return (StyleableProperty<Pos>)var2.containerAlignmentProperty();
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public MenuBarSkin(MenuBar var1) {
      super(var1);
      this.container = new HBox();
      this.container.getStyleClass().add("container");
      this.getChildren().add(this.container);
      this.keyEventHandler = var2x -> {
         if (this.focusedMenu != null) {
            switch (var2x.getCode()) {
               case LEFT:
                  boolean var5 = var1.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
                  if (var1.getScene().getWindow().isFocused()) {
                     if (this.openMenu != null && !this.openMenu.isShowing()) {
                        if (var5) {
                           this.moveToMenu(Direction.NEXT, false);
                        } else {
                           this.moveToMenu(Direction.PREVIOUS, false);
                        }

                        var2x.consume();
                        return;
                     }

                     if (var5) {
                        this.moveToMenu(Direction.NEXT, true);
                     } else {
                        this.moveToMenu(Direction.PREVIOUS, true);
                     }
                  }

                  var2x.consume();
                  break;
               case RIGHT:
                  boolean var4 = var1.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
                  if (var1.getScene().getWindow().isFocused()) {
                     if (this.openMenu != null && !this.openMenu.isShowing()) {
                        if (var4) {
                           this.moveToMenu(Direction.PREVIOUS, false);
                        } else {
                           this.moveToMenu(Direction.NEXT, false);
                        }

                        var2x.consume();
                        return;
                     }

                     if (var4) {
                        this.moveToMenu(Direction.PREVIOUS, true);
                     } else {
                        this.moveToMenu(Direction.NEXT, true);
                     }
                  }

                  var2x.consume();
                  break;
               case DOWN:
                  if (var1.getScene().getWindow().isFocused() && this.focusedMenuIndex != -1) {
                     Menu var3x = (Menu)this.getSkinnable().getMenus().get(this.focusedMenuIndex);
                     this.showMenu(var3x, true);
                     var2x.consume();
                  }
                  break;
               case ESCAPE:
                  this.unSelectMenus();
                  var2x.consume();
            }
         }
      };
      this.menuBarFocusedPropertyListener = (var1x, var2x, var3x) -> {
         this.unSelectMenus();
         if (var3x && !this.container.getChildren().isEmpty()) {
            this.menuModeStart(0);
            this.openMenuButton = (MenuBarButton)this.container.getChildren().get(0);
            this.setFocusedMenuIndex(0);
            this.openMenuButton.setHover();
         }
      };
      this.weakSceneKeyEventHandler = new WeakEventHandler(this.keyEventHandler);
      Utils.executeOnceWhenPropertyIsNonNull(var1.sceneProperty(), var1x -> var1x.addEventFilter(KeyEvent.KEY_PRESSED, this.weakSceneKeyEventHandler));
      this.mouseEventHandler = var1x -> {
         Bounds var2x = this.container.localToScreen(this.container.getLayoutBounds());
         if (var2x == null || !var2x.contains(var1x.getScreenX(), var1x.getScreenY())) {
            this.unSelectMenus();
         }
      };
      this.weakSceneMouseEventHandler = new WeakEventHandler(this.mouseEventHandler);
      Utils.executeOnceWhenPropertyIsNonNull(var1.sceneProperty(), var1x -> var1x.addEventFilter(MouseEvent.MOUSE_CLICKED, this.weakSceneMouseEventHandler));
      this.weakWindowFocusListener = new WeakChangeListener((var1x, var2x, var3x) -> {
         if (!var3x) {
            this.unSelectMenus();
         }
      });
      Utils.executeOnceWhenPropertyIsNonNull(var1.sceneProperty(), var1x -> {
         if (var1x.getWindow() != null) {
            var1x.getWindow().focusedProperty().addListener(this.weakWindowFocusListener);
         } else {
            ChangeListener var2x = (var1xx, var2xx, var3x) -> {
               if (var2xx != null) {
                  var2xx.focusedProperty().removeListener(this.weakWindowFocusListener);
               }

               if (var3x != null) {
                  var3x.focusedProperty().addListener(this.weakWindowFocusListener);
               }
            };
            this.weakWindowSceneListener = new WeakChangeListener(var2x);
            var1x.windowProperty().addListener(this.weakWindowSceneListener);
         }
      });
      this.menuVisibilityChangeListener = (var1x, var2x, var3x) -> this.rebuildUI();
      this.rebuildUI();
      var1.getMenus().addListener(var1x -> this.rebuildUI());
      if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
         var1.useSystemMenuBarProperty().addListener(var1x -> this.rebuildUI());
      }

      KeyCombination var2;
      if (com.sun.javafx.util.Utils.isMac()) {
         var2 = KeyCombination.keyCombination("ctrl+F10");
      } else {
         var2 = KeyCombination.keyCombination("F10");
      }

      this.altKeyEventHandler = var1x -> {
         if (var1x.getEventType() == KeyEvent.KEY_PRESSED) {
            this.altKeyPressed = false;
            if (var1x.getCode() == KeyCode.ALT && !var1x.isConsumed()) {
               if (this.focusedMenuIndex == -1) {
                  this.altKeyPressed = true;
               }

               this.unSelectMenus();
            }
         } else if (var1x.getEventType() == KeyEvent.KEY_RELEASED) {
            if (this.altKeyPressed && var1x.getCode() == KeyCode.ALT && !var1x.isConsumed()) {
               this.firstMenuRunnable.run();
            }

            this.altKeyPressed = false;
         }
      };
      this.weakSceneAltKeyEventHandler = new WeakEventHandler(this.altKeyEventHandler);
      Utils.executeOnceWhenPropertyIsNonNull(var1.sceneProperty(), var2x -> {
         var2x.getAccelerators().put(var2, this.firstMenuRunnable);
         var2x.addEventHandler(KeyEvent.ANY, this.weakSceneAltKeyEventHandler);
      });
      ParentTraversalEngine var3 = new ParentTraversalEngine(this.getSkinnable());
      var3.addTraverseListener((var1x, var2x) -> {
         if (this.openMenu != null) {
            this.openMenu.hide();
         }

         this.setFocusedMenuIndex(0);
      });
      ParentHelper.setTraversalEngine(this.getSkinnable(), var3);
      var1.sceneProperty().addListener((var2x, var3x, var4) -> {
         if (var3x != null) {
            if (this.weakSceneKeyEventHandler != null) {
               var3x.removeEventFilter(KeyEvent.KEY_PRESSED, this.weakSceneKeyEventHandler);
            }

            if (this.weakSceneMouseEventHandler != null) {
               var3x.removeEventFilter(MouseEvent.MOUSE_CLICKED, this.weakSceneMouseEventHandler);
            }

            if (this.weakSceneAltKeyEventHandler != null) {
               var3x.removeEventHandler(KeyEvent.ANY, this.weakSceneAltKeyEventHandler);
            }
         }

         if (var3x != null) {
            var3x.getAccelerators().remove(var2);
         }

         if (var4 != null) {
            var4.getAccelerators().put(var2, this.firstMenuRunnable);
         }
      });
   }

   private void showMenu(Menu var1) {
      this.showMenu(var1, false);
   }

   private void showMenu(Menu var1, boolean var2) {
      if (this.openMenu != var1) {
         if (this.openMenu != null) {
            this.openMenu.hide();
         }

         this.openMenu = var1;
         if (!var1.isShowing() && !this.isMenuEmpty(var1)) {
            if (var2) {
               MenuButton var3 = this.getNodeForMenu(this.focusedMenuIndex);
               Skin var4 = var3.getSkin();
               if (var4 instanceof MenuButtonSkinBase) {
                  ((MenuButtonSkinBase)var4).requestFocusOnFirstMenuItem();
               }
            }

            this.openMenu.show();
         }
      }
   }

   void setFocusedMenuIndex(int var1) {
      this.focusedMenuIndex = var1 >= -1 && var1 < this.getSkinnable().getMenus().size() ? var1 : -1;
      this.focusedMenu = this.focusedMenuIndex != -1 ? (Menu)this.getSkinnable().getMenus().get(var1) : null;
      if (this.focusedMenuIndex != -1) {
         this.openMenuButton = (MenuBarButton)this.container.getChildren().get(this.focusedMenuIndex);
         this.openMenuButton.setHover();
      }
   }

   public static void setDefaultSystemMenuBar(MenuBar var0) {
      if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
         wrappedDefaultMenus.clear();

         for (Menu var2 : var0.getMenus()) {
            wrappedDefaultMenus.add(GlobalMenuAdapter.adapt(var2));
         }

         var0.getMenus().addListener(var1 -> {
            wrappedDefaultMenus.clear();

            for (Menu var3 : var0.getMenus()) {
               wrappedDefaultMenus.add(GlobalMenuAdapter.adapt(var3));
            }
         });
      }
   }

   private static MenuBarSkin getMenuBarSkin(Stage var0) {
      if (systemMenuMap == null) {
         return null;
      } else {
         Reference var1 = systemMenuMap.get(var0);
         return var1 == null ? null : (MenuBarSkin)var1.get();
      }
   }

   private static void setSystemMenu(Stage var0) {
      if (var0 != null && var0.isFocused()) {
         while (var0 != null && var0.getOwner() instanceof Stage) {
            MenuBarSkin var1 = getMenuBarSkin(var0);
            if (var1 != null && var1.wrappedMenus != null) {
               break;
            }

            var0 = (Stage)var0.getOwner();
         }
      } else {
         var0 = null;
      }

      if (var0 != currentMenuBarStage) {
         List var3 = null;
         if (var0 != null) {
            MenuBarSkin var2 = getMenuBarSkin(var0);
            if (var2 != null) {
               var3 = var2.wrappedMenus;
            }
         }

         if (var3 == null) {
            var3 = wrappedDefaultMenus;
         }

         Toolkit.getToolkit().getSystemMenu().setMenus(var3);
         currentMenuBarStage = var0;
      }
   }

   private static void initSystemMenuBar() {
      systemMenuMap = new WeakHashMap<>();
      InvalidationListener var0 = var0x -> setSystemMenu((Stage)((ReadOnlyProperty)var0x).getBean());

      for (Window var2 : stages) {
         var2.focusedProperty().addListener(var0);
      }

      stages.addListener(var1 -> {
         while (var1.next()) {
            for (Window var3 : var1.getRemoved()) {
               var3.focusedProperty().removeListener(var0);
            }

            for (Window var5 : var1.getAddedSubList()) {
               var5.focusedProperty().addListener(var0);
               setSystemMenu((Stage)var5);
            }
         }
      });
   }

   public final void setSpacing(double var1) {
      this.spacingProperty().set(this.snapSpaceX(var1));
   }

   public final double getSpacing() {
      return this.spacing == null ? 0.0 : this.snapSpaceX(this.spacing.get());
   }

   public final DoubleProperty spacingProperty() {
      if (this.spacing == null) {
         this.spacing = new StyleableDoubleProperty() {
            protected void invalidated() {
               double var1 = this.get();
               MenuBarSkin.this.container.setSpacing(var1);
            }

            public Object getBean() {
               return MenuBarSkin.this;
            }

            public String getName() {
               return "spacing";
            }

            public CssMetaData<MenuBar, Number> getCssMetaData() {
               return MenuBarSkin.SPACING;
            }
         };
      }

      return this.spacing;
   }

   public final void setContainerAlignment(Pos var1) {
      this.containerAlignmentProperty().set(var1);
   }

   public final Pos getContainerAlignment() {
      return this.containerAlignment == null ? Pos.TOP_LEFT : (Pos)this.containerAlignment.get();
   }

   public final ObjectProperty<Pos> containerAlignmentProperty() {
      if (this.containerAlignment == null) {
         this.containerAlignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
            public void invalidated() {
               Pos var1 = (Pos)this.get();
               MenuBarSkin.this.container.setAlignment(var1);
            }

            public Object getBean() {
               return MenuBarSkin.this;
            }

            public String getName() {
               return "containerAlignment";
            }

            public CssMetaData<MenuBar, Pos> getCssMetaData() {
               return MenuBarSkin.ALIGNMENT;
            }
         };
      }

      return this.containerAlignment;
   }

   @Override
   public void dispose() {
      this.cleanUpSystemMenu();
      super.dispose();
   }

   @Override
   protected double snappedTopInset() {
      return this.container.getChildren().isEmpty() ? 0.0 : super.snappedTopInset();
   }

   @Override
   protected double snappedBottomInset() {
      return this.container.getChildren().isEmpty() ? 0.0 : super.snappedBottomInset();
   }

   @Override
   protected double snappedLeftInset() {
      return this.container.getChildren().isEmpty() ? 0.0 : super.snappedLeftInset();
   }

   @Override
   protected double snappedRightInset() {
      return this.container.getChildren().isEmpty() ? 0.0 : super.snappedRightInset();
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.container.resizeRelocate(var1, var3, var5, var7);
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.container.minWidth(var1) + this.snappedLeftInset() + this.snappedRightInset();
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.container.prefWidth(var1) + this.snappedLeftInset() + this.snappedRightInset();
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.container.minHeight(var1) + this.snappedTopInset() + this.snappedBottomInset();
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.container.prefHeight(var1) + this.snappedTopInset() + this.snappedBottomInset();
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefHeight(-1.0);
   }

   MenuButton getNodeForMenu(int var1) {
      return var1 < this.container.getChildren().size() ? (MenuBarButton)this.container.getChildren().get(var1) : null;
   }

   int getFocusedMenuIndex() {
      return this.focusedMenuIndex;
   }

   private boolean menusContainCustomMenuItem() {
      for (Menu var2 : this.getSkinnable().getMenus()) {
         if (this.menuContainsCustomMenuItem(var2)) {
            System.err.println("Warning: MenuBar ignored property useSystemMenuBar because menus contain CustomMenuItem");
            return true;
         }
      }

      return false;
   }

   private boolean menuContainsCustomMenuItem(Menu var1) {
      for (MenuItem var3 : var1.getItems()) {
         if (var3 instanceof CustomMenuItem && !(var3 instanceof SeparatorMenuItem)) {
            return true;
         }

         if (var3 instanceof Menu && this.menuContainsCustomMenuItem((Menu)var3)) {
            return true;
         }
      }

      return false;
   }

   private int getMenuBarButtonIndex(MenuBarButton var1) {
      for (int var2 = 0; var2 < this.container.getChildren().size(); var2++) {
         MenuBarButton var3 = (MenuBarButton)this.container.getChildren().get(var2);
         if (var1 == var3) {
            return var2;
         }
      }

      return -1;
   }

   private void updateActionListeners(MenuItem var1, boolean var2) {
      if (var1 instanceof Menu) {
         Menu var3 = (Menu)var1;
         if (var2) {
            var3.getItems().addListener(this.menuItemListener);
         } else {
            var3.getItems().removeListener(this.menuItemListener);
         }

         for (MenuItem var5 : var3.getItems()) {
            this.updateActionListeners(var5, var2);
         }
      } else if (var2) {
         var1.addEventHandler(ActionEvent.ACTION, this.menuActionEventHandler);
      } else {
         var1.removeEventHandler(ActionEvent.ACTION, this.menuActionEventHandler);
      }
   }

   private void rebuildUI() {
      this.getSkinnable().focusedProperty().removeListener(this.menuBarFocusedPropertyListener);

      for (Menu var2 : this.getSkinnable().getMenus()) {
         this.updateActionListeners(var2, false);
         var2.visibleProperty().removeListener(this.menuVisibilityChangeListener);
      }

      for (Node var7 : this.container.getChildren()) {
         MenuBarButton var3 = (MenuBarButton)var7;
         var3.hide();
         var3.menu.showingProperty().removeListener(var3.menuListener);
         var3.disableProperty().unbind();
         var3.textProperty().unbind();
         var3.graphicProperty().unbind();
         var3.styleProperty().unbind();
         var3.dispose();
         var3.setSkin(null);
         Object var10 = null;
      }

      this.container.getChildren().clear();
      if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
         Scene var5 = this.getSkinnable().getScene();
         if (var5 != null) {
            if (this.sceneChangeListener == null) {
               this.sceneChangeListener = (var1, var2x, var3x) -> {
                  if (var2x != null && var2x.getWindow() instanceof Stage) {
                     Stage var4 = (Stage)var2x.getWindow();
                     MenuBarSkin var5x = getMenuBarSkin(var4);
                     if (var5x == this) {
                        var5x.wrappedMenus = null;
                        systemMenuMap.remove(var4);
                        if (currentMenuBarStage == var4) {
                           currentMenuBarStage = null;
                           setSystemMenu(var4);
                        }
                     } else if (this.getSkinnable().isUseSystemMenuBar()
                        && var5x != null
                        && var5x.getSkinnable() != null
                        && var5x.getSkinnable().isUseSystemMenuBar()) {
                        var5x.getSkinnable().setUseSystemMenuBar(false);
                     }
                  }

                  if (var3x != null && this.getSkinnable().isUseSystemMenuBar() && !this.menusContainCustomMenuItem() && var3x.getWindow() instanceof Stage) {
                     Stage var7x = (Stage)var3x.getWindow();
                     if (systemMenuMap == null) {
                        initSystemMenuBar();
                     }

                     this.wrappedMenus = new ArrayList<>();
                     systemMenuMap.put(var7x, new WeakReference<>(this));

                     for (Menu var6 : this.getSkinnable().getMenus()) {
                        this.wrappedMenus.add(GlobalMenuAdapter.adapt(var6));
                     }

                     currentMenuBarStage = null;
                     setSystemMenu(var7x);
                     this.getSkinnable().requestLayout();
                     Platform.runLater(() -> this.getSkinnable().requestLayout());
                  }
               };
               this.getSkinnable().sceneProperty().addListener(this.sceneChangeListener);
            }

            this.sceneChangeListener.changed(this.getSkinnable().sceneProperty(), var5, var5);
            if (currentMenuBarStage != null ? getMenuBarSkin(currentMenuBarStage) == this : this.getSkinnable().isUseSystemMenuBar()) {
               return;
            }
         } else if (currentMenuBarStage != null) {
            MenuBarSkin var8 = getMenuBarSkin(currentMenuBarStage);
            if (var8 == this) {
               setSystemMenu(null);
            }
         }
      }

      this.getSkinnable().focusedProperty().addListener(this.menuBarFocusedPropertyListener);

      for (Menu var9 : this.getSkinnable().getMenus()) {
         var9.visibleProperty().addListener(this.menuVisibilityChangeListener);
         if (var9.isVisible()) {
            MenuBarButton var11 = new MenuBarButton(this, var9);
            var11.setFocusTraversable(false);
            var11.getStyleClass().add("menu");
            var11.setStyle(var9.getStyle());
            var11.getItems().setAll(var9.getItems());
            this.container.getChildren().add(var11);
            var11.menuListener = (var3x, var4, var5x) -> {
               if (var9.isShowing()) {
                  var11.show();
                  this.menuModeStart(this.container.getChildren().indexOf(var11));
               } else {
                  var11.hide();
               }
            };
            var11.menu = var9;
            var9.showingProperty().addListener(var11.menuListener);
            var11.disableProperty().bindBidirectional(var9.disableProperty());
            var11.textProperty().bind(var9.textProperty());
            var11.graphicProperty().bind(var9.graphicProperty());
            var11.styleProperty().bind(var9.styleProperty());
            var11.getProperties().addListener(var2x -> {
               if (var2x.wasAdded() && "autoHide".equals(var2x.getKey())) {
                  var11.getProperties().remove("autoHide");
                  var9.hide();
               }
            });
            var11.showingProperty().addListener((var3x, var4, var5x) -> {
               if (var5x) {
                  if (this.openMenuButton == null && this.focusedMenuIndex != -1) {
                     this.openMenuButton = (MenuBarButton)this.container.getChildren().get(this.focusedMenuIndex);
                  }

                  if (this.openMenuButton != null && this.openMenuButton != var11) {
                     this.openMenuButton.clearHover();
                  }

                  this.openMenuButton = var11;
                  this.showMenu(var9);
               } else {
                  this.openMenu = null;
                  this.openMenuButton = null;
               }
            });
            var11.setOnMousePressed(var3x -> {
               this.pendingDismiss = var11.isShowing();
               if (var11.getScene().getWindow().isFocused()) {
                  this.showMenu(var9);
                  this.menuModeStart(this.getMenuBarButtonIndex(var11));
               }
            });
            var11.setOnMouseReleased(var2x -> {
               if (var11.getScene().getWindow().isFocused() && this.pendingDismiss) {
                  this.resetOpenMenu();
               }

               this.pendingDismiss = false;
            });
            var11.setOnMouseEntered(var3x -> {
               if (var11.getScene() != null && var11.getScene().getWindow() != null && var11.getScene().getWindow().isFocused()) {
                  if (this.openMenuButton != null && this.openMenuButton != var11) {
                     this.openMenuButton.clearHover();
                     this.openMenuButton = null;
                     this.openMenuButton = var11;
                  }

                  this.updateFocusedIndex();
                  if (this.openMenu != null && this.openMenu != var9) {
                     this.showMenu(var9);
                  }
               }
            });
            this.updateActionListeners(var9, true);
         }
      }

      this.getSkinnable().requestLayout();
   }

   private void cleanUpSystemMenu() {
      if (this.sceneChangeListener != null && this.getSkinnable() != null) {
         this.getSkinnable().sceneProperty().removeListener(this.sceneChangeListener);
         this.sceneChangeListener = null;
      }

      if (currentMenuBarStage != null && getMenuBarSkin(currentMenuBarStage) == this) {
         setSystemMenu(null);
      }

      if (systemMenuMap != null) {
         Iterator var1 = systemMenuMap.entrySet().iterator();

         while (var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            Reference var3 = (Reference)var2.getValue();
            MenuBarSkin var4 = var3 != null ? (MenuBarSkin)var3.get() : null;
            if (var4 == null || var4 == this) {
               var1.remove();
            }
         }
      }
   }

   private boolean isMenuEmpty(Menu var1) {
      boolean var2 = true;
      if (var1 != null) {
         for (MenuItem var4 : var1.getItems()) {
            if (var4 != null && var4.isVisible()) {
               var2 = false;
            }
         }
      }

      return var2;
   }

   private void resetOpenMenu() {
      if (this.openMenu != null) {
         this.openMenu.hide();
         this.openMenu = null;
      }
   }

   private void unSelectMenus() {
      this.clearMenuButtonHover();
      if (this.focusedMenuIndex != -1) {
         if (this.openMenu != null) {
            this.openMenu.hide();
            this.openMenu = null;
         }

         if (this.openMenuButton != null) {
            this.openMenuButton.clearHover();
            this.openMenuButton = null;
         }

         this.menuModeEnd();
      }
   }

   private void menuModeStart(int var1) {
      if (this.focusedMenuIndex == -1) {
         SceneHelper.getSceneAccessor().setTransientFocusContainer(this.getSkinnable().getScene(), this.getSkinnable());
      }

      this.setFocusedMenuIndex(var1);
   }

   private void menuModeEnd() {
      if (this.focusedMenuIndex != -1) {
         SceneHelper.getSceneAccessor().setTransientFocusContainer(this.getSkinnable().getScene(), null);
         this.getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE);
      }

      this.setFocusedMenuIndex(-1);
   }

   private void moveToMenu(Direction var1, boolean var2) {
      boolean var3 = var2 && this.focusedMenu.isShowing();
      this.findSibling(var1, this.focusedMenuIndex).ifPresent(var2x -> {
         this.setFocusedMenuIndex((Integer)var2x.getValue());
         if (var3) {
            this.showMenu((Menu)var2x.getKey(), false);
         }
      });
   }

   private Optional<Pair<Menu, Integer>> findSibling(Direction var1, int var2) {
      if (var2 == -1) {
         return Optional.empty();
      } else {
         int var3 = this.getSkinnable().getMenus().size();
         int var4 = 0;

         int var5;
         for (var5 = 0; var4 < var3; var2 = var5) {
            var4++;
            var5 = (var2 + (var1.isForward() ? 1 : -1)) % var3;
            if (var5 == -1) {
               var5 = var3 - 1;
            }

            if (!((Menu)this.getSkinnable().getMenus().get(var5)).isDisable()) {
               break;
            }
         }

         this.clearMenuButtonHover();
         return Optional.of(new Pair((Menu)this.getSkinnable().getMenus().get(var5), var5));
      }
   }

   private void updateFocusedIndex() {
      int var1 = 0;

      for (Node var3 : this.container.getChildren()) {
         if (var3.isHover()) {
            this.setFocusedMenuIndex(var1);
            return;
         }

         var1++;
      }

      this.menuModeEnd();
   }

   private void clearMenuButtonHover() {
      for (Node var2 : this.container.getChildren()) {
         if (var2.isHover()) {
            ((MenuBarButton)var2).clearHover();
            ((MenuBarButton)var2).disarm();
            return;
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_NODE:
            return this.openMenuButton;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   static {
      Predicate var0 = var0x -> var0x instanceof Stage;
      ObservableList var1 = AccessController.doPrivileged(() -> Window.getWindows(), null, FXPermissions.ACCESS_WINDOW_LIST_PERMISSION);
      stages = var1.filtered(var0);
      ArrayList var5 = new ArrayList<>(SkinBase.getClassCssMetaData());
      String var6 = ALIGNMENT.getProperty();
      int var2 = 0;

      for (int var3 = var5.size(); var2 < var3; var2++) {
         CssMetaData var4 = (CssMetaData)var5.get(var2);
         if (var6.equals(var4.getProperty())) {
            var5.remove(var4);
         }
      }

      var5.add(SPACING);
      var5.add(ALIGNMENT);
      STYLEABLES = Collections.unmodifiableList(var5);
   }
}
