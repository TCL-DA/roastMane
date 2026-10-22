package javafx.beans.property;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ReadOnlyListWrapper<E> extends SimpleListProperty<E> {
   private ReadOnlyListWrapper<E>.ReadOnlyPropertyImpl readOnlyProperty;

   public ReadOnlyListWrapper() {
   }

   public ReadOnlyListWrapper(ObservableList<E> var1) {
      super(var1);
   }

   public ReadOnlyListWrapper(Object var1, String var2) {
      super(var1, var2);
   }

   public ReadOnlyListWrapper(Object var1, String var2, ObservableList<E> var3) {
      super(var1, var2, var3);
   }

   public ReadOnlyListProperty<E> getReadOnlyProperty() {
      if (this.readOnlyProperty == null) {
         this.readOnlyProperty = new ReadOnlyListWrapper.ReadOnlyPropertyImpl();
      }

      return this.readOnlyProperty;
   }

   @Override
   protected void fireValueChangedEvent() {
      super.fireValueChangedEvent();
      if (this.readOnlyProperty != null) {
         this.readOnlyProperty.fireValueChangedEvent();
      }
   }

   @Override
   protected void fireValueChangedEvent(ListChangeListener.Change<? extends E> var1) {
      super.fireValueChangedEvent(var1);
      if (this.readOnlyProperty != null) {
         var1.reset();
         this.readOnlyProperty.fireValueChangedEvent(var1);
      }
   }

   private class ReadOnlyPropertyImpl extends ReadOnlyListPropertyBase<E> {
      public ObservableList<E> get() {
         return ReadOnlyListWrapper.this.get();
      }

      @Override
      public Object getBean() {
         return ReadOnlyListWrapper.this.getBean();
      }

      @Override
      public String getName() {
         return ReadOnlyListWrapper.this.getName();
      }

      @Override
      public ReadOnlyIntegerProperty sizeProperty() {
         return ReadOnlyListWrapper.this.sizeProperty();
      }

      @Override
      public ReadOnlyBooleanProperty emptyProperty() {
         return ReadOnlyListWrapper.this.emptyProperty();
      }
   }
}
