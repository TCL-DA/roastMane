package javafx.scene.control;

import com.sun.javafx.binding.ExpressionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.TextFieldSkin;

public class TextField extends TextInputControl {
   public static final int DEFAULT_PREF_COLUMN_COUNT = 12;
   private IntegerProperty prefColumnCount = new StyleableIntegerProperty(12) {
      private int oldValue = this.get();

      protected void invalidated() {
         int var1 = this.get();
         if (var1 < 0) {
            if (this.isBound()) {
               this.unbind();
            }

            this.set(this.oldValue);
            throw new IllegalArgumentException("value cannot be negative.");
         } else {
            this.oldValue = var1;
         }
      }

      public CssMetaData<TextField, Number> getCssMetaData() {
         return TextField.StyleableProperties.PREF_COLUMN_COUNT;
      }

      public Object getBean() {
         return TextField.this;
      }

      public String getName() {
         return "prefColumnCount";
      }
   };
   private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
      protected void invalidated() {
         TextField.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
      }

      public Object getBean() {
         return TextField.this;
      }

      public String getName() {
         return "onAction";
      }
   };
   private ObjectProperty<Pos> alignment;

   public TextField() {
      this("");
   }

   public TextField(String var1) {
      super(new TextField.TextFieldContent());
      this.getStyleClass().add("text-field");
      this.setAccessibleRole(AccessibleRole.TEXT_FIELD);
      this.setText(var1);
   }

   public CharSequence getCharacters() {
      return ((TextField.TextFieldContent)this.getContent()).characters;
   }

   public final IntegerProperty prefColumnCountProperty() {
      return this.prefColumnCount;
   }

   public final int getPrefColumnCount() {
      return this.prefColumnCount.getValue();
   }

   public final void setPrefColumnCount(int var1) {
      this.prefColumnCount.setValue(var1);
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
      return this.onAction;
   }

   public final EventHandler<ActionEvent> getOnAction() {
      return (EventHandler<ActionEvent>)this.onActionProperty().get();
   }

   public final void setOnAction(EventHandler<ActionEvent> var1) {
      this.onActionProperty().set(var1);
   }

   public final ObjectProperty<Pos> alignmentProperty() {
      if (this.alignment == null) {
         this.alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT) {
            public CssMetaData<TextField, Pos> getCssMetaData() {
               return TextField.StyleableProperties.ALIGNMENT;
            }

            public Object getBean() {
               return TextField.this;
            }

            public String getName() {
               return "alignment";
            }
         };
      }

      return this.alignment;
   }

   public final void setAlignment(Pos var1) {
      this.alignmentProperty().set(var1);
   }

   public final Pos getAlignment() {
      return this.alignment == null ? Pos.CENTER_LEFT : (Pos)this.alignment.get();
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TextFieldSkin(this);
   }

   @Override
   String filterInput(String var1) {
      return TextInputControl.filterInput(var1, true, true);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TextField.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   private static class StyleableProperties {
      private static final CssMetaData<TextField, Pos> ALIGNMENT = new CssMetaData<TextField, Pos>(
         "-fx-alignment", new EnumConverter(Pos.class), Pos.CENTER_LEFT
      ) {
         public boolean isSettable(TextField var1) {
            return var1.alignment == null || !var1.alignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(TextField var1) {
            return (StyleableProperty<Pos>)var1.alignmentProperty();
         }
      };
      private static final CssMetaData<TextField, Number> PREF_COLUMN_COUNT = new CssMetaData<TextField, Number>(
         "-fx-pref-column-count", SizeConverter.getInstance(), 12
      ) {
         public boolean isSettable(TextField var1) {
            return var1.prefColumnCount == null || !var1.prefColumnCount.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TextField var1) {
            return (StyleableProperty<Number>)var1.prefColumnCountProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(TextInputControl.getClassCssMetaData());
         var0.add(ALIGNMENT);
         var0.add(PREF_COLUMN_COUNT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   private static final class TextFieldContent implements TextInputControl.Content {
      private ExpressionHelper<String> helper = null;
      private StringBuilder characters = new StringBuilder();

      @Override
      public String get(int var1, int var2) {
         return this.characters.substring(var1, var2);
      }

      @Override
      public void insert(int var1, String var2, boolean var3) {
         var2 = TextInputControl.filterInput(var2, true, true);
         if (!var2.isEmpty()) {
            this.characters.insert(var1, var2);
            if (var3) {
               ExpressionHelper.fireValueChangedEvent(this.helper);
            }
         }
      }

      @Override
      public void delete(int var1, int var2, boolean var3) {
         if (var2 > var1) {
            this.characters.delete(var1, var2);
            if (var3) {
               ExpressionHelper.fireValueChangedEvent(this.helper);
            }
         }
      }

      @Override
      public int length() {
         return this.characters.length();
      }

      public String get() {
         return this.characters.toString();
      }

      public void addListener(ChangeListener<? super String> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super String> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public String getValue() {
         return this.get();
      }

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }
   }
}
