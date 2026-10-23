package org.cropster.roastingintelligence.measurement;

import java.util.Set;

public interface MeasurementReaderWriter extends MeasurementSource {
   Double getMinValue(String var1);

   Double getMaxValue(String var1);

   void setValue(String var1, Double var2);

   Set<String> getConfiguredChannels();

   default void setAutoMode(boolean enabled, String measurement) {
   }

   default Boolean getAutoMode(String measurement) {
      return null;
   }

   default Double getValidControlValue(String measurement, double value) {
      return value;
   }
}
