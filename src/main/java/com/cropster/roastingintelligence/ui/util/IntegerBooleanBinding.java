package com.cropster.roastingintelligence.ui.util;

import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableBooleanValue;

public class IntegerBooleanBinding extends IntegerBinding {
   private ObservableBooleanValue observableValue;

   public IntegerBooleanBinding(ObservableBooleanValue observableValue) {
      super.bind(new Observable[]{observableValue});
      this.observableValue = observableValue;
   }

   protected int computeValue() {
      return this.observableValue.get() ? 1 : 0;
   }
}
