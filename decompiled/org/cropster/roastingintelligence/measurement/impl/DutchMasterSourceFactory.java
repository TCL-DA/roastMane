package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class DutchMasterSourceFactory extends NetworkSourceFactory {
   @Override
   protected String getDefaultIpAddress() {
      return "192.168.3.159";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "exhaustTemperature".equals(measureName)
         || "otherTemperature".equals(measureName)
         || "setTemperatureControl".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName) || "gasControl".equals(measureName)) {
         return Unit.PERCENT;
      } else if ("fanSpeed".equals(measureName)) {
         return Unit.PERCENT;
      } else if ("drumSpeed".equals(measureName) || "drumSpeedControl".equals(measureName)) {
         return Unit.RPM;
      } else {
         return !"drumPressure".equals(measureName) && !"drumPressureControl".equals(measureName) ? null : Unit.PASCAL;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new DutchMasterSource(this, this._inetAddress)};
   }

   @Override
   public boolean supportsAutoMode(String measureName) {
      return "gasControl".equals(measureName) || "setTemperatureControl".equals(measureName);
   }

   @Override
   public String getAutoModeMeasurement(String measureName) {
      return this.supportsAutoMode(measureName) ? "setTemperatureControl" : null;
   }

   @Override
   public String getManualModeMeasurement(String measureName) {
      return this.supportsAutoMode(measureName) ? "gasControl" : null;
   }
}
