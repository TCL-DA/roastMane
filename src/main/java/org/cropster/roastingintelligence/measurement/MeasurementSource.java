package org.cropster.roastingintelligence.measurement;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;

public interface MeasurementSource extends Closeable {
   MeasurementSourceFactory getFactory();

   void open();

   boolean isReady();

   void configure(Collection<Channel> var1);

   Double getValue(String var1);

   Map<String, Double> getValues();

   void addMeasurementListener(MeasurementListener var1);

   void removeMeasurementListener(MeasurementListener var1);

   boolean isValid(String var1, Double var2);
}
