package javafx.beans.property;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;

public abstract class ReadOnlyLongPropertyBase extends ReadOnlyLongProperty {
   ExpressionHelper<Number> helper;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super Number> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super Number> var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      ExpressionHelper.fireValueChangedEvent(this.helper);
   }
}
