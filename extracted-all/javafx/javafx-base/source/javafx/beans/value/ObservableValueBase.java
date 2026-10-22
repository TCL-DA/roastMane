package javafx.beans.value;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;

public abstract class ObservableValueBase<T> implements ObservableValue<T> {
   private ExpressionHelper<T> helper;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void addListener(ChangeListener<? super T> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super T> var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      ExpressionHelper.fireValueChangedEvent(this.helper);
   }
}
