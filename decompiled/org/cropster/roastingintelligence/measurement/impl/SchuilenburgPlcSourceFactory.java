package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class SchuilenburgPlcSourceFactory extends NetworkSourceFactory {
   @Override
   public Unit getUnit(String measureName) {
      switch (measureName) {
         case "beanTemperature":
         case "exhaustTemperature":
         case "burnerTemperature":
            return Unit.CELSIUS;
         case "gas":
         case "gasControl":
         case "airflow":
         case "airflowControl":
         case "drumSpeed":
            return Unit.PERCENT;
         case "drumPressure":
            return Unit.MILLIBAR;
         default:
            return null;
      }
   }

   @Override
   protected String getDefaultIpAddress() {
      return "";
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{new SchuilenburgPlcSource(this, this._inetAddress)};
      } catch (Exception var3) {
         throw new IOException("Error initializing measurement source");
      }
   }
}
