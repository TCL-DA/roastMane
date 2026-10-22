package javafx.beans.property;

import com.sun.javafx.binding.SetExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class ReadOnlySetPropertyBase<E> extends ReadOnlySetProperty<E> {
   private SetExpressionHelper<E> helper;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableSet<E>> var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableSet<E>> var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(SetChangeListener<? super E> var1) {
      this.helper = SetExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(SetChangeListener<? super E> var1) {
      this.helper = SetExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      SetExpressionHelper.fireValueChangedEvent(this.helper);
   }

   protected void fireValueChangedEvent(SetChangeListener.Change<? extends E> var1) {
      SetExpressionHelper.fireValueChangedEvent(this.helper, var1);
   }
}
