package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;

public class AutonicsTk4sSourceFactory extends SerialSourceFactory {
   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) && !"environmentalTemperature".equals(measureName)
         ? null
         : Unit.CELSIUS;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPort);
         parameters.setBaudRate(9600);
         parameters.setDatabits(8);
         parameters.setParity(0);
         parameters.setStopbits(3);
         parameters.setEncoding("rtu");
         parameters.setEcho(false);
         return new MeasurementSource[]{new AutonicsTk4sSource(this, parameters)};
      } catch (Exception var3) {
         throw new IOException(var3);
      }
   }
}
