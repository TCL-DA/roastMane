package javafx.beans.property;

import com.sun.javafx.binding.ListExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ReadOnlyListPropertyBase<E> extends ReadOnlyListProperty<E> {
   private ListExpressionHelper<E> helper;

   @Override
   public void addListener(InvalidationListener var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(InvalidationListener var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ChangeListener<? super ObservableList<E>> var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ChangeListener<? super ObservableList<E>> var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   @Override
   public void addListener(ListChangeListener<? super E> var1) {
      this.helper = ListExpressionHelper.addListener(this.helper, this, var1);
   }

   @Override
   public void removeListener(ListChangeListener<? super E> var1) {
      this.helper = ListExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      ListExpressionHelper.fireValueChangedEvent(this.helper);
   }

   protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
      ListExpressionHelper.fireValueChangedEvent(this.helper, var1);
   }
}
