package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.ToolBarSkin;

@DefaultProperty("items")
public class ToolBar extends Control {
   private final ObservableList<Node> items = FXCollections.observableArrayList();
   private ObjectProperty<Orientation> orientation;
   private static final String DEFAULT_STYLE_CLASS = "tool-bar";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public ToolBar() {
      this.initialize();
   }

   public ToolBar(Node... var1) {
      this.initialize();
      this.items.addAll(var1);
   }

   private void initialize() {
      this.getStyleClass().setAll(new String[]{"tool-bar"});
      this.setAccessibleRole(AccessibleRole.TOOL_BAR);
      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
      this.pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
   }

   public final ObservableList<Node> getItems() {
      return this.items;
   }

   public final void setOrientation(Orientation var1) {
      this.orientationProperty().set(var1);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
            public void invalidated() {
               boolean var1 = this.get() == Orientation.VERTICAL;
               ToolBar.this.pseudoClassStateChanged(ToolBar.VERTICAL_PSEUDOCLASS_STATE, var1);
               ToolBar.this.pseudoClassStateChanged(ToolBar.HORIZONTAL_PSEUDOCLASS_STATE, !var1);
            }

            public Object getBean() {
               return ToolBar.this;
            }

            public String getName() {
               return "orientation";
            }

            public CssMetaData<ToolBar, Orientation> getCssMetaData() {
               return ToolBar.StyleableProperties.ORIENTATION;
            }
         };
      }

      return this.orientation;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ToolBarSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ToolBar.StyleableProperties.STYLEABLES;
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
      private static final CssMetaData<ToolBar, Orientation> ORIENTATION = new CssMetaData<ToolBar, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(ToolBar var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(ToolBar var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(ToolBar var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(ORIENTATION);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
