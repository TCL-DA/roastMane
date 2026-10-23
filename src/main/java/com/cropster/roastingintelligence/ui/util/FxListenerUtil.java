package com.cropster.roastingintelligence.ui.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public final class FxListenerUtil {
   private FxListenerUtil() {
      throw new IllegalStateException("No instance");
   }

   public static <T> void replaceListener(ObservableValue<T> property, ChangeListener<? super T> listener) {
      if (listener != null) {
         property.removeListener(listener);
         property.addListener(listener);
      }
   }

   public static <T> void removeListener(ObservableValue<T> property, ChangeListener<? super T> listener) {
      if (listener != null) {
         property.removeListener(listener);
      }
   }
}
