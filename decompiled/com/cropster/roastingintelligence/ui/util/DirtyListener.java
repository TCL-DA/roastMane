package com.cropster.roastingintelligence.ui.util;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DirtyListener<T> extends SimpleBooleanProperty implements ChangeListener<T> {
   private final ObjectProperty<T> originalValue = new SimpleObjectProperty();

   public DirtyListener(T originalValue) {
      this.originalValue.set(originalValue);
   }

   public ObjectProperty<T> originalValueProperty() {
      return this.originalValue;
   }

   public T getOriginalValue() {
      return (T)this.originalValue.get();
   }

   public void setOriginalValue(T originalValue) {
      this.originalValue.set(originalValue);
      this.set(false);
   }

   public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
      this.set(!Objects.equals(this.originalValue.get(), newValue));
   }
}
