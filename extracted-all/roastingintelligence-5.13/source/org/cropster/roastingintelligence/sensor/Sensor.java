package org.cropster.roastingintelligence.sensor;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.model.Channel;

public interface Sensor extends Closeable {
   void open() throws IOException;

   boolean isAttached();

   void configure(Collection<Channel> var1);

   Double getAmbientTemperature();

   void addSensorListener(SensorListener var1);

   void removeSensorListener(SensorListener var1);
}
