package com.cropster.roastingintelligence.ui.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Function;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang3.StringUtils;

public class NumberInputValidBinding extends BooleanBinding {
   private final NumberFormat numberFormat = NumberFormat.getInstance();
   private final ObservableValue<String> obervableValue;
   private final Function<Number, Boolean> function;

   public NumberInputValidBinding(ObservableValue<String> obervableValue, Function<Number, Boolean> function) {
      super.bind(new Observable[]{obervableValue});
      this.obervableValue = obervableValue;
      this.function = function;
   }

   protected boolean computeValue() {
      String text = (String)this.obervableValue.getValue();
      if (text != null && StringUtils.isNotBlank(text)) {
         try {
            Number value = this.numberFormat.parse(text);
            if (value != null) {
               return this.function.apply(value);
            }
         } catch (ParseException var3) {
         }
      }

      return false;
   }
}
