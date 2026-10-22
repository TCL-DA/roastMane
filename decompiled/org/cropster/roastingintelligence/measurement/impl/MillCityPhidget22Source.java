package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.phidget22.PhidgetException;
import com.phidget22.ThermocoupleType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;

public class MillCityPhidget22Source extends Phidget1048Source {
   private final Unit _temperatureUnit;
   private final MillCitySourceFactory.MillCityVersion _millCityVersion;

   MillCityPhidget22Source(
      MeasurementSourceFactory factory, Unit unit, MillCitySourceFactory.MillCityVersion millCityVersion, PhidgetDeviceDetector deviceDetector
   ) throws IOException, PhidgetException {
      super(factory);
      this._temperatureUnit = unit;
      this._millCityVersion = millCityVersion;
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      Set<Channel> temperatureChannels = new LinkedHashSet<>();

      for (Channel channel : channels) {
         if (channel.getName().equals("beanTemperature")) {
            Channel temperatureChannel = channel.clone();
            temperatureChannel.setIndex(0);
            temperatureChannel.setProbeType(ThermocoupleType.K.name());
            temperatureChannels.add(temperatureChannel);
         } else if (channel.getName().equals("exhaustTemperature") && this._millCityVersion != MillCitySourceFactory.MillCityVersion.VERSION3) {
            Channel temperatureChannel = channel.clone();
            temperatureChannel.setIndex(1);
            temperatureChannel.setProbeType(ThermocoupleType.K.name());
            temperatureChannels.add(temperatureChannel);
         } else if (channel.getName().equals("inletTemperature") && this._millCityVersion != MillCitySourceFactory.MillCityVersion.VERSION3) {
            Channel temperatureChannel = channel.clone();
            temperatureChannel.setIndex(2);
            temperatureChannel.setProbeType(ThermocoupleType.K.name());
            temperatureChannels.add(temperatureChannel);
         }
      }

      super.configure(temperatureChannels);
   }

   @Override
   public Map<String, Double> getValues() {
      if (this._temperatureUnit.equals(Unit.CELSIUS)) {
         return super.getValues();
      } else {
         Map<String, Double> values = super.getValues();

         for (Entry<String, Double> entry : values.entrySet()) {
            entry.setValue(Unit.FAHRENHEIT.from(BigDecimal.valueOf(entry.getValue()), Unit.CELSIUS).doubleValue());
         }

         return values;
      }
   }
}
