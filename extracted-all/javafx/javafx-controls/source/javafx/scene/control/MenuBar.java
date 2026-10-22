package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.MenuBarSkin;

@DefaultProperty("menus")
public class MenuBar extends Control {
   private ObservableList<Menu> menus = FXCollections.observableArrayList();
   private String BIND_MSG = "cannot uni-directionally bind to the system menu bar - use bindBidrectional instead";
   private BooleanProperty useSystemMenuBar;
   private static final String DEFAULT_STYLE_CLASS = "menu-bar";

   public MenuBar() {
      this((Menu[])null);
   }

   public MenuBar(Menu... var1) {
      this.getStyleClass().setAll(new String[]{"menu-bar"});
      this.setAccessibleRole(AccessibleRole.MENU_BAR);
      if (var1 != null) {
         this.getMenus().addAll(var1);
      }

      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
   }

   public final BooleanProperty useSystemMenuBarProperty() {
      if (this.useSystemMenuBar == null) {
         this.useSystemMenuBar = new StyleableBooleanProperty() {
            public CssMetaData<MenuBar, Boolean> getCssMetaData() {
               return MenuBar.StyleableProperties.USE_SYSTEM_MENU_BAR;
            }

            public Object getBean() {
               return MenuBar.this;
            }

            public String getName() {
               return "useSystemMenuBar";
            }

            public void bind(ObservableValue<? extends Boolean> var1) {
               throw new RuntimeException(MenuBar.this.BIND_MSG);
            }
         };
      }

      return this.useSystemMenuBar;
   }

   public final void setUseSystemMenuBar(boolean var1) {
      this.useSystemMenuBarProperty().setValue(var1);
   }

   public final boolean isUseSystemMenuBar() {
      return this.useSystemMenuBar == null ? false : this.useSystemMenuBar.getValue();
   }

   public final ObservableList<Menu> getMenus() {
      return this.menus;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new MenuBarSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return MenuBar.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }

   private static class StyleableProperties {
      private static final CssMetaData<MenuBar, Boolean> USE_SYSTEM_MENU_BAR = new CssMetaData<MenuBar, Boolean>(
         "-fx-use-system-menu-bar", BooleanConverter.getInstance(), false
      ) {
         public boolean isSettable(MenuBar var1) {
            return var1.useSystemMenuBar == null || !var1.useSystemMenuBar.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(MenuBar var1) {
            return (StyleableProperty<Boolean>)var1.useSystemMenuBarProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(USE_SYSTEM_MENU_BAR);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
