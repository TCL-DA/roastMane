package org.controlsfx.control;

import impl.org.controlsfx.skin.PlusMinusSliderSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.InputEvent;

public class PlusMinusSlider extends ControlsFXControl {
   private static final String DEFAULT_STYLE_CLASS = "plus-minus-slider";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
   private ReadOnlyDoubleWrapper value = new ReadOnlyDoubleWrapper(this, "value", 0.0);
   private ObjectProperty<Orientation> orientation;
   private ObjectProperty<EventHandler<PlusMinusSlider.PlusMinusEvent>> onValueChanged = new ObjectPropertyBase<EventHandler<PlusMinusSlider.PlusMinusEvent>>() {
      protected void invalidated() {
         PlusMinusSlider.this.setEventHandler(PlusMinusSlider.PlusMinusEvent.VALUE_CHANGED, (EventHandler)this.get());
      }

      public Object getBean() {
         return PlusMinusSlider.this;
      }

      public String getName() {
         return "onValueChanged";
      }
   };

   public PlusMinusSlider() {
      this.getStyleClass().add("plus-minus-slider");
      this.setOrientation(Orientation.HORIZONTAL);
      this.getProperties().addListener(new MapChangeListener<Object, Object>() {
         public void onChanged(Change<? extends Object, ? extends Object> change) {
            if (change.getKey().equals("plusminusslidervalue") && change.getValueAdded() != null) {
               Double valueAdded = (Double)change.getValueAdded();
               PlusMinusSlider.this.value.set(valueAdded);
               change.getMap().remove("plusminusslidervalue");
            }
         }
      });
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(PlusMinusSlider.class, "plusminusslider.css");
   }

   protected Skin<?> createDefaultSkin() {
      return new PlusMinusSliderSkin(this);
   }

   public final ReadOnlyDoubleProperty valueProperty() {
      return this.value.getReadOnlyProperty();
   }

   public final double getValue() {
      return this.valueProperty().get();
   }

   public final void setOrientation(Orientation value) {
      this.orientationProperty().set(value);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty<Orientation>(null) {
            protected void invalidated() {
               boolean vertical = this.get() == Orientation.VERTICAL;
               PlusMinusSlider.this.pseudoClassStateChanged(PlusMinusSlider.VERTICAL_PSEUDOCLASS_STATE, vertical);
               PlusMinusSlider.this.pseudoClassStateChanged(PlusMinusSlider.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
            }

            public CssMetaData<PlusMinusSlider, Orientation> getCssMetaData() {
               return PlusMinusSlider.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return PlusMinusSlider.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public final ObjectProperty<EventHandler<PlusMinusSlider.PlusMinusEvent>> onValueChangedProperty() {
      return this.onValueChanged;
   }

   public final void setOnValueChanged(EventHandler<PlusMinusSlider.PlusMinusEvent> value) {
      this.onValueChangedProperty().set(value);
   }

   public final EventHandler<PlusMinusSlider.PlusMinusEvent> getOnValueChanged() {
      return (EventHandler<PlusMinusSlider.PlusMinusEvent>)this.onValueChangedProperty().get();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return PlusMinusSlider.StyleableProperties.STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   public static class PlusMinusEvent extends InputEvent {
      private static final long serialVersionUID = 2881004583512990781L;
      public static final EventType<PlusMinusSlider.PlusMinusEvent> ANY = new EventType(InputEvent.ANY, "ANY" + UUID.randomUUID().toString());
      public static final EventType<PlusMinusSlider.PlusMinusEvent> VALUE_CHANGED = new EventType(ANY, "VALUE_CHANGED" + UUID.randomUUID().toString());
      private double value;

      public PlusMinusEvent(Object source, EventTarget target, EventType<? extends InputEvent> eventType, double value) {
         super(source, target, eventType);
         this.value = value;
      }

      public double getValue() {
         return this.value;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<PlusMinusSlider, Orientation> ORIENTATION = new CssMetaData<PlusMinusSlider, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.VERTICAL
      ) {
         public Orientation getInitialValue(PlusMinusSlider node) {
            return node.getOrientation();
         }

         public boolean isSettable(PlusMinusSlider n) {
            return n.orientation == null || !n.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(PlusMinusSlider n) {
            return (StyleableProperty<Orientation>)n.orientationProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
         styleables.add(ORIENTATION);
         STYLEABLES = Collections.unmodifiableList(styleables);
      }
   }
}
