package javafx.animation;

import com.sun.javafx.animation.KeyValueHelper;
import com.sun.javafx.animation.KeyValueType;
import javafx.beans.NamedArg;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableNumberValue;
import javafx.beans.value.WritableValue;

public final class KeyValue {
   private static final Interpolator DEFAULT_INTERPOLATOR = Interpolator.LINEAR;
   private final KeyValueType type;
   private final WritableValue<?> target;
   private final Object endValue;
   private final Interpolator interpolator;

   KeyValueType getType() {
      return this.type;
   }

   public WritableValue<?> getTarget() {
      return this.target;
   }

   public Object getEndValue() {
      return this.endValue;
   }

   public Interpolator getInterpolator() {
      return this.interpolator;
   }

   public <T> KeyValue(@NamedArg("target") WritableValue<T> var1, @NamedArg("endValue") T var2, @NamedArg("interpolator") Interpolator var3) {
      if (var1 == null) {
         throw new NullPointerException("Target needs to be specified");
      } else if (var3 == null) {
         throw new NullPointerException("Interpolator needs to be specified");
      } else {
         this.target = var1;
         this.endValue = var2;
         this.interpolator = var3;
         this.type = var1 instanceof WritableNumberValue
            ? (
               var1 instanceof WritableDoubleValue
                  ? KeyValueType.DOUBLE
                  : (
                     var1 instanceof WritableIntegerValue
                        ? KeyValueType.INTEGER
                        : (
                           var1 instanceof WritableFloatValue
                              ? KeyValueType.FLOAT
                              : (var1 instanceof WritableLongValue ? KeyValueType.LONG : KeyValueType.OBJECT)
                        )
                  )
            )
            : (var1 instanceof WritableBooleanValue ? KeyValueType.BOOLEAN : KeyValueType.OBJECT);
      }
   }

   public <T> KeyValue(@NamedArg("target") WritableValue<T> var1, @NamedArg("endValue") T var2) {
      this(var1, var2, DEFAULT_INTERPOLATOR);
   }

   @Override
   public String toString() {
      return "KeyValue [target=" + this.target + ", endValue=" + this.endValue + ", interpolator=" + this.interpolator + "]";
   }

   @Override
   public int hashCode() {
      assert this.target != null && this.interpolator != null;

      int var1 = 1;
      var1 = 31 * var1 + this.target.hashCode();
      var1 = 31 * var1 + (this.endValue == null ? 0 : this.endValue.hashCode());
      return 31 * var1 + this.interpolator.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyValue)) {
         return false;
      } else {
         KeyValue var2 = (KeyValue)var1;

         assert this.target != null && this.interpolator != null && var2.target != null && var2.interpolator != null;

         return this.target.equals(var2.target)
            && (this.endValue == null ? var2.endValue == null : this.endValue.equals(var2.endValue))
            && this.interpolator.equals(var2.interpolator);
      }
   }

   static {
      KeyValueHelper.setKeyValueAccessor(new KeyValueHelper.KeyValueAccessor() {
         @Override
         public KeyValueType getType(KeyValue var1) {
            return var1.getType();
         }
      });
   }
}
