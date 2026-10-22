package com.cropster.roastingintelligence.ui.util;

import java.util.function.Function;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;

public class DoubleFunctionBinding extends DoubleBinding {
   private final ObservableDoubleValue observableValue;
   private final Function<Double, Double> function;

   public DoubleFunctionBinding(ObservableDoubleValue observableValue, Function<Double, Double> function) {
      super.bind(new Observable[]{observableValue});
      this.observableValue = observableValue;
      this.function = function;
   }

   protected double computeValue() {
      return this.function.apply(this.observableValue.get());
   }
}
