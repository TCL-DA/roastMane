package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.skin.SeparatorSkin;

public class Separator extends Control {
   private ObjectProperty<Orientation> orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
      protected void invalidated() {
         boolean var1 = this.get() == Orientation.VERTICAL;
         Separator.this.pseudoClassStateChanged(Separator.VERTICAL_PSEUDOCLASS_STATE, var1);
         Separator.this.pseudoClassStateChanged(Separator.HORIZONTAL_PSEUDOCLASS_STATE, !var1);
      }

      public CssMetaData<Separator, Orientation> getCssMetaData() {
         return Separator.StyleableProperties.ORIENTATION;
      }

      public Object getBean() {
         return Separator.this;
      }

      public String getName() {
         return "orientation";
      }
   };
   private ObjectProperty<HPos> halignment;
   private ObjectProperty<VPos> valignment;
   private static final String DEFAULT_STYLE_CLASS = "separator";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public Separator() {
      this(Orientation.HORIZONTAL);
   }

   public Separator(Orientation var1) {
      this.getStyleClass().setAll(new String[]{"separator"});
      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
      this.pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, var1 != Orientation.VERTICAL);
      this.pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, var1 == Orientation.VERTICAL);
      ((StyleableProperty)this.orientationProperty()).applyStyle(null, var1 != null ? var1 : Orientation.HORIZONTAL);
   }

   public final void setOrientation(Orientation var1) {
      this.orientation.set(var1);
   }

   public final Orientation getOrientation() {
      return (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      return this.orientation;
   }

   public final void setHalignment(HPos var1) {
      this.halignmentProperty().set(var1);
   }

   public final HPos getHalignment() {
      return this.halignment == null ? HPos.CENTER : (HPos)this.halignment.get();
   }

   public final ObjectProperty<HPos> halignmentProperty() {
      if (this.halignment == null) {
         this.halignment = new StyleableObjectProperty<HPos>(HPos.CENTER) {
            public Object getBean() {
               return Separator.this;
            }

            public String getName() {
               return "halignment";
            }

            public CssMetaData<Separator, HPos> getCssMetaData() {
               return Separator.StyleableProperties.HALIGNMENT;
            }
         };
      }

      return this.halignment;
   }

   public final void setValignment(VPos var1) {
      this.valignmentProperty().set(var1);
   }

   public final VPos getValignment() {
      return this.valignment == null ? VPos.CENTER : (VPos)this.valignment.get();
   }

   public final ObjectProperty<VPos> valignmentProperty() {
      if (this.valignment == null) {
         this.valignment = new StyleableObjectProperty<VPos>(VPos.CENTER) {
            public Object getBean() {
               return Separator.this;
            }

            public String getName() {
               return "valignment";
            }

            public CssMetaData<Separator, VPos> getCssMetaData() {
               return Separator.StyleableProperties.VALIGNMENT;
            }
         };
      }

      return this.valignment;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new SeparatorSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return Separator.StyleableProperties.STYLEABLES;
   }

   @Override
   protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }

   private static class StyleableProperties {
      private static final CssMetaData<Separator, Orientation> ORIENTATION = new CssMetaData<Separator, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(Separator var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(Separator var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(Separator var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final CssMetaData<Separator, HPos> HALIGNMENT = new CssMetaData<Separator, HPos>(
         "-fx-halignment", new EnumConverter(HPos.class), HPos.CENTER
      ) {
         public boolean isSettable(Separator var1) {
            return var1.halignment == null || !var1.halignment.isBound();
         }

         public StyleableProperty<HPos> getStyleableProperty(Separator var1) {
            return (StyleableProperty<HPos>)var1.halignmentProperty();
         }
      };
      private static final CssMetaData<Separator, VPos> VALIGNMENT = new CssMetaData<Separator, VPos>(
         "-fx-valignment", new EnumConverter(VPos.class), VPos.CENTER
      ) {
         public boolean isSettable(Separator var1) {
            return var1.valignment == null || !var1.valignment.isBound();
         }

         public StyleableProperty<VPos> getStyleableProperty(Separator var1) {
            return (StyleableProperty<VPos>)var1.valignmentProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(ORIENTATION);
         var0.add(HALIGNMENT);
         var0.add(VALIGNMENT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
