package javafx.scene.control;

import com.sun.javafx.scene.control.FakeFocusTextField;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.DurationConverter;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.SpinnerSkin;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class Spinner<T> extends Control {
   private static final String DEFAULT_STYLE_CLASS = "spinner";
   public static final String STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL = "arrows-on-right-horizontal";
   public static final String STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL = "arrows-on-left-vertical";
   public static final String STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL = "arrows-on-left-horizontal";
   public static final String STYLE_CLASS_SPLIT_ARROWS_VERTICAL = "split-arrows-vertical";
   public static final String STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL = "split-arrows-horizontal";
   private ReadOnlyObjectWrapper<T> value = new ReadOnlyObjectWrapper(this, "value");
   private ObjectProperty<SpinnerValueFactory<T>> valueFactory = new SimpleObjectProperty<SpinnerValueFactory<T>>(this, "valueFactory") {
      protected void invalidated() {
         Spinner.this.value.unbind();
         SpinnerValueFactory var1 = (SpinnerValueFactory)this.get();
         if (var1 != null) {
            Spinner.this.value.bind(var1.valueProperty());
         }
      }
   };
   private BooleanProperty editable;
   private TextField textField;
   private ReadOnlyObjectWrapper<TextField> editor;
   private final ObjectProperty<Duration> initialDelay;
   private final ObjectProperty<Duration> repeatDelay;
   private static final CssMetaData<Spinner<?>, Duration> INITIAL_DELAY = new CssMetaData<Spinner<?>, Duration>(
      "-fx-initial-delay", DurationConverter.getInstance(), new Duration(300.0)
   ) {
      public boolean isSettable(Spinner<?> var1) {
         return !var1.initialDelayProperty().isBound();
      }

      public StyleableProperty<Duration> getStyleableProperty(Spinner<?> var1) {
         return (StyleableProperty<Duration>)var1.initialDelayProperty();
      }
   };
   private static final CssMetaData<Spinner<?>, Duration> REPEAT_DELAY = new CssMetaData<Spinner<?>, Duration>(
      "-fx-repeat-delay", DurationConverter.getInstance(), new Duration(60.0)
   ) {
      public boolean isSettable(Spinner<?> var1) {
         return !var1.repeatDelayProperty().isBound();
      }

      public StyleableProperty<Duration> getStyleableProperty(Spinner<?> var1) {
         return (StyleableProperty<Duration>)var1.repeatDelayProperty();
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public Spinner() {
      this.initialDelay = new SimpleStyleableObjectProperty(INITIAL_DELAY, this, "initialDelay", new Duration(300.0));
      this.repeatDelay = new SimpleStyleableObjectProperty(REPEAT_DELAY, this, "repeatDelay", new Duration(60.0));
      this.getStyleClass().add("spinner");
      this.setAccessibleRole(AccessibleRole.SPINNER);
      this.getEditor().setOnAction(var1 -> this.commitValue());
      this.getEditor().editableProperty().bind(this.editableProperty());
      this.value.addListener((var1, var2, var3) -> this.setText((T)var3));
      this.getProperties().addListener(var1 -> {
         if (var1.wasAdded() && var1.getKey() == "FOCUSED") {
            this.setFocused((Boolean)var1.getValueAdded());
            this.getProperties().remove("FOCUSED");
         }
      });
      this.focusedProperty().addListener(var1 -> {
         if (!this.isFocused()) {
            this.commitValue();
         }
      });
   }

   public Spinner(@NamedArg("min") int var1, @NamedArg("max") int var2, @NamedArg("initialValue") int var3) {
      this(new SpinnerValueFactory.IntegerSpinnerValueFactory(var1, var2, var3));
   }

   public Spinner(@NamedArg("min") int var1, @NamedArg("max") int var2, @NamedArg("initialValue") int var3, @NamedArg("amountToStepBy") int var4) {
      this(new SpinnerValueFactory.IntegerSpinnerValueFactory(var1, var2, var3, var4));
   }

   public Spinner(@NamedArg("min") double var1, @NamedArg("max") double var3, @NamedArg("initialValue") double var5) {
      this(new SpinnerValueFactory.DoubleSpinnerValueFactory(var1, var3, var5));
   }

   public Spinner(@NamedArg("min") double var1, @NamedArg("max") double var3, @NamedArg("initialValue") double var5, @NamedArg("amountToStepBy") double var7) {
      this(new SpinnerValueFactory.DoubleSpinnerValueFactory(var1, var3, var5, var7));
   }

   Spinner(@NamedArg("min") LocalDate var1, @NamedArg("max") LocalDate var2, @NamedArg("initialValue") LocalDate var3) {
      this(new SpinnerValueFactory.LocalDateSpinnerValueFactory(var1, var2, var3));
   }

   Spinner(
      @NamedArg("min") LocalDate var1,
      @NamedArg("max") LocalDate var2,
      @NamedArg("initialValue") LocalDate var3,
      @NamedArg("amountToStepBy") long var4,
      @NamedArg("temporalUnit") TemporalUnit var6
   ) {
      this(new SpinnerValueFactory.LocalDateSpinnerValueFactory(var1, var2, var3, var4, var6));
   }

   Spinner(@NamedArg("min") LocalTime var1, @NamedArg("max") LocalTime var2, @NamedArg("initialValue") LocalTime var3) {
      this(new SpinnerValueFactory.LocalTimeSpinnerValueFactory(var1, var2, var3));
   }

   Spinner(
      @NamedArg("min") LocalTime var1,
      @NamedArg("max") LocalTime var2,
      @NamedArg("initialValue") LocalTime var3,
      @NamedArg("amountToStepBy") long var4,
      @NamedArg("temporalUnit") TemporalUnit var6
   ) {
      this(new SpinnerValueFactory.LocalTimeSpinnerValueFactory(var1, var2, var3, var4, var6));
   }

   public Spinner(@NamedArg("items") ObservableList<T> var1) {
      this(new SpinnerValueFactory.ListSpinnerValueFactory<>(var1));
   }

   public Spinner(@NamedArg("valueFactory") SpinnerValueFactory<T> var1) {
      this();
      this.setValueFactory(var1);
   }

   public void increment() {
      this.increment(1);
   }

   public void increment(int var1) {
      SpinnerValueFactory var2 = this.getValueFactory();
      if (var2 == null) {
         throw new IllegalStateException("Can't increment Spinner with a null SpinnerValueFactory");
      } else {
         this.commitValue();
         var2.increment(var1);
      }
   }

   public void decrement() {
      this.decrement(1);
   }

   public void decrement(int var1) {
      SpinnerValueFactory var2 = this.getValueFactory();
      if (var2 == null) {
         throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
      } else {
         this.commitValue();
         var2.decrement(var1);
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new SpinnerSkin<>(this);
   }

   public final void commitValue() {
      if (this.isEditable()) {
         String var1 = this.getEditor().getText();
         SpinnerValueFactory var2 = this.getValueFactory();
         if (var2 != null) {
            StringConverter var3 = var2.getConverter();
            if (var3 != null) {
               Object var4 = var3.fromString(var1);
               var2.setValue(var4);
            }
         }
      }
   }

   public final void cancelEdit() {
      if (this.isEditable()) {
         Object var1 = this.getValue();
         SpinnerValueFactory var2 = this.getValueFactory();
         if (var2 != null) {
            StringConverter var3 = var2.getConverter();
            if (var3 != null) {
               String var4 = var3.toString(var1);
               this.getEditor().setText(var4);
            }
         }
      }
   }

   public final T getValue() {
      return (T)this.value.get();
   }

   public final ReadOnlyObjectProperty<T> valueProperty() {
      return this.value;
   }

   public final void setValueFactory(SpinnerValueFactory<T> var1) {
      this.valueFactory.setValue(var1);
   }

   public final SpinnerValueFactory<T> getValueFactory() {
      return (SpinnerValueFactory<T>)this.valueFactory.get();
   }

   public final ObjectProperty<SpinnerValueFactory<T>> valueFactoryProperty() {
      return this.valueFactory;
   }

   public final void setEditable(boolean var1) {
      this.editableProperty().set(var1);
   }

   public final boolean isEditable() {
      return this.editable == null ? false : this.editable.get();
   }

   public final BooleanProperty editableProperty() {
      if (this.editable == null) {
         this.editable = new SimpleBooleanProperty(this, "editable", false);
      }

      return this.editable;
   }

   public final ReadOnlyObjectProperty<TextField> editorProperty() {
      if (this.editor == null) {
         this.editor = new ReadOnlyObjectWrapper(this, "editor");
         this.textField = new FakeFocusTextField();
         this.textField.tooltipProperty().bind(this.tooltipProperty());
         this.editor.set(this.textField);
      }

      return this.editor.getReadOnlyProperty();
   }

   public final TextField getEditor() {
      return (TextField)this.editorProperty().get();
   }

   public final StringProperty promptTextProperty() {
      return this.getEditor().promptTextProperty();
   }

   public final String getPromptText() {
      return this.getEditor().getPromptText();
   }

   public final void setPromptText(String var1) {
      this.getEditor().setPromptText(var1);
   }

   public final ObjectProperty<Duration> initialDelayProperty() {
      return this.initialDelay;
   }

   public final void setInitialDelay(Duration var1) {
      if (var1 != null) {
         this.initialDelay.set(var1);
      }
   }

   public final Duration getInitialDelay() {
      return (Duration)this.initialDelay.get();
   }

   public final ObjectProperty<Duration> repeatDelayProperty() {
      return this.repeatDelay;
   }

   public final void setRepeatDelay(Duration var1) {
      if (var1 != null) {
         this.repeatDelay.set(var1);
      }
   }

   public final Duration getRepeatDelay() {
      return (Duration)this.repeatDelay.get();
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private void setText(T var1) {
      String var2 = null;
      SpinnerValueFactory var3 = this.getValueFactory();
      if (var3 != null) {
         StringConverter var4 = var3.getConverter();
         if (var4 != null) {
            var2 = var4.toString(var1);
         }
      }

      this.notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
      if (var2 == null) {
         if (var1 == null) {
            this.getEditor().clear();
            return;
         }

         var2 = var1.toString();
      }

      this.getEditor().setText(var2);
   }

   static int wrapValue(int var0, int var1, int var2) {
      if (var2 == 0) {
         throw new RuntimeException();
      } else {
         int var3 = var0 % var2;
         if (var3 > var1 && var2 < var1) {
            var3 = var3 + var2 - var1;
         } else if (var3 < var1 && var2 > var1) {
            var3 = var3 + var2 - var1;
         }

         return var3;
      }
   }

   static BigDecimal wrapValue(BigDecimal var0, BigDecimal var1, BigDecimal var2) {
      if (var2.doubleValue() == 0.0) {
         throw new RuntimeException();
      } else if (var0.compareTo(var1) < 0) {
         return var2;
      } else {
         return var0.compareTo(var2) > 0 ? var1 : var0;
      }
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case TEXT:
            Object var3 = this.getValue();
            SpinnerValueFactory var4 = this.getValueFactory();
            if (var4 != null) {
               StringConverter var5 = var4.getConverter();
               if (var5 != null) {
                  return var5.toString(var3);
               }
            }

            return var3 != null ? var3.toString() : "";
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case INCREMENT:
            this.increment();
            break;
         case DECREMENT:
            this.decrement();
            break;
         default:
            super.executeAccessibleAction(var1);
      }
   }

   static {
      ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
      var0.add(INITIAL_DELAY);
      var0.add(REPEAT_DELAY);
      STYLEABLES = Collections.unmodifiableList(var0);
   }
}
