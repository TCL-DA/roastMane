package javafx.scene.control;

import com.sun.javafx.scene.control.FormatterAccessor;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.util.StringConverter;

public class TextFormatter<V> {
   private final StringConverter<V> valueConverter;
   private final UnaryOperator<TextFormatter.Change> filter;
   private Consumer<TextFormatter<?>> textUpdater;
   public static final StringConverter<String> IDENTITY_STRING_CONVERTER = new StringConverter<String>() {
      public String toString(String var1) {
         return var1 == null ? "" : var1;
      }

      public String fromString(String var1) {
         return var1;
      }
   };
   private final ObjectProperty<V> value = new ObjectPropertyBase<V>() {
      public Object getBean() {
         return TextFormatter.this;
      }

      public String getName() {
         return "value";
      }

      protected void invalidated() {
         if (TextFormatter.this.valueConverter == null && this.get() != null) {
            if (this.isBound()) {
               this.unbind();
            }

            throw new IllegalStateException("Value changes are not supported when valueConverter is not set");
         } else {
            TextFormatter.this.updateText();
         }
      }
   };

   public TextFormatter(@NamedArg("filter") UnaryOperator<TextFormatter.Change> var1) {
      this(null, null, var1);
   }

   public TextFormatter(
      @NamedArg("valueConverter") StringConverter<V> var1, @NamedArg("defaultValue") V var2, @NamedArg("filter") UnaryOperator<TextFormatter.Change> var3
   ) {
      this.filter = var3;
      this.valueConverter = var1;
      this.setValue((V)var2);
   }

   public TextFormatter(@NamedArg("valueConverter") StringConverter<V> var1, @NamedArg("defaultValue") V var2) {
      this(var1, (V)var2, null);
   }

   public TextFormatter(@NamedArg("valueConverter") StringConverter<V> var1) {
      this(var1, null, null);
   }

   public final StringConverter<V> getValueConverter() {
      return this.valueConverter;
   }

   public final UnaryOperator<TextFormatter.Change> getFilter() {
      return this.filter;
   }

   public final ObjectProperty<V> valueProperty() {
      return this.value;
   }

   public final void setValue(V var1) {
      if (this.valueConverter == null && var1 != null) {
         throw new IllegalStateException("Value changes are not supported when valueConverter is not set");
      } else {
         this.value.set(var1);
      }
   }

   public final V getValue() {
      return (V)this.value.get();
   }

   private void updateText() {
      if (this.textUpdater != null) {
         this.textUpdater.accept(this);
      }
   }

   void bindToControl(Consumer<TextFormatter<?>> var1) {
      if (this.textUpdater != null) {
         throw new IllegalStateException("Formatter is already used in other control");
      } else {
         this.textUpdater = var1;
      }
   }

   void unbindFromControl() {
      this.textUpdater = null;
   }

   void updateValue(String var1) {
      if (!this.value.isBound()) {
         try {
            Object var2 = this.valueConverter.fromString(var1);
            this.setValue((V)var2);
         } catch (Exception var3) {
            this.updateText();
         }
      }
   }

   public static final class Change implements Cloneable {
      private final FormatterAccessor accessor;
      private Control control;
      int start;
      int end;
      String text;
      int anchor;
      int caret;

      Change(Control var1, FormatterAccessor var2, int var3, int var4) {
         this(var1, var2, var4, var4, "", var3, var4);
      }

      Change(Control var1, FormatterAccessor var2, int var3, int var4, String var5) {
         this(var1, var2, var3, var4, var5, var3 + var5.length(), var3 + var5.length());
      }

      Change(Control var1, FormatterAccessor var2, int var3, int var4, String var5, int var6, int var7) {
         this.control = var1;
         this.accessor = var2;
         this.start = var3;
         this.end = var4;
         this.text = var5;
         this.anchor = var6;
         this.caret = var7;
      }

      public final Control getControl() {
         return this.control;
      }

      public final int getRangeStart() {
         return this.start;
      }

      public final int getRangeEnd() {
         return this.end;
      }

      public final void setRange(int var1, int var2) {
         int var3 = this.accessor.getTextLength();
         if (var1 >= 0 && var1 <= var3 && var2 >= 0 && var2 <= var3) {
            this.start = var1;
            this.end = var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final int getCaretPosition() {
         return this.caret;
      }

      public final int getAnchor() {
         return this.anchor;
      }

      public final int getControlCaretPosition() {
         return this.accessor.getCaret();
      }

      public final int getControlAnchor() {
         return this.accessor.getAnchor();
      }

      public final void selectRange(int var1, int var2) {
         if (var1 >= 0
            && var1 <= this.accessor.getTextLength() - (this.end - this.start) + this.text.length()
            && var2 >= 0
            && var2 <= this.accessor.getTextLength() - (this.end - this.start) + this.text.length()) {
            this.anchor = var1;
            this.caret = var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final IndexRange getSelection() {
         return IndexRange.normalize(this.anchor, this.caret);
      }

      public final void setAnchor(int var1) {
         if (var1 >= 0 && var1 <= this.accessor.getTextLength() - (this.end - this.start) + this.text.length()) {
            this.anchor = var1;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final void setCaretPosition(int var1) {
         if (var1 >= 0 && var1 <= this.accessor.getTextLength() - (this.end - this.start) + this.text.length()) {
            this.caret = var1;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final String getText() {
         return this.text;
      }

      public final void setText(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.text = var1;
         }
      }

      public final String getControlText() {
         return this.accessor.getText(0, this.accessor.getTextLength());
      }

      public final String getControlNewText() {
         return this.accessor.getText(0, this.start) + this.text + this.accessor.getText(this.end, this.accessor.getTextLength());
      }

      public final boolean isAdded() {
         return !this.text.isEmpty();
      }

      public final boolean isDeleted() {
         return this.start != this.end;
      }

      public final boolean isReplaced() {
         return this.isAdded() && this.isDeleted();
      }

      public final boolean isContentChange() {
         return this.isAdded() || this.isDeleted();
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("TextInputControl.Change [");
         if (this.isReplaced()) {
            var1.append(" replaced \"")
               .append(this.accessor.getText(this.start, this.end))
               .append("\" with \"")
               .append(this.text)
               .append("\" at (")
               .append(this.start)
               .append(", ")
               .append(this.end)
               .append(")");
         } else if (this.isDeleted()) {
            var1.append(" deleted \"")
               .append(this.accessor.getText(this.start, this.end))
               .append("\" at (")
               .append(this.start)
               .append(", ")
               .append(this.end)
               .append(")");
         } else if (this.isAdded()) {
            var1.append(" added \"").append(this.text).append("\" at ").append(this.start);
         }

         if (!this.isAdded() && !this.isDeleted()) {
            var1.append(" ");
         } else {
            var1.append("; ");
         }

         var1.append("new selection (anchor, caret): [").append(this.anchor).append(", ").append(this.caret).append("]");
         var1.append(" ]");
         return var1.toString();
      }

      public TextFormatter.Change clone() {
         try {
            return (TextFormatter.Change)super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new RuntimeException(var2);
         }
      }
   }
}
