package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class BescaManualSourceFactory extends SerialSourceFactory {
   private static final String PARAM_VERSION = "version";
   private BescaManualSourceFactory.BescaManualVersion _bescaManualVersion = BescaManualSourceFactory.BescaManualVersion.MANUAL_V2;

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) ? null : Unit.CELSIUS;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._bescaManualVersion != null ? this._bescaManualVersion.name() : null);
      version.setOptions(
         new String[]{
            BescaManualSourceFactory.BescaManualVersion.MANUAL_V1.name(),
            BescaManualSourceFactory.BescaManualVersion.MANUAL_V2.name(),
            BescaManualSourceFactory.BescaManualVersion.BEE.name()
         }
      );
      version.setOptionDisplayName(BescaManualSourceFactory.BescaManualVersion.MANUAL_V1.name(), "V1");
      version.setOptionDisplayName(BescaManualSourceFactory.BescaManualVersion.MANUAL_V2.name(), "V2");
      version.setOptionDisplayName(BescaManualSourceFactory.BescaManualVersion.BEE.name(), "Bee");
      parameters.add(version);
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Besca Manual machine required!");
            }

            this._bescaManualVersion = BescaManualSourceFactory.BescaManualVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPort);
         parameters.setBaudRate(9600);
         parameters.setDatabits(8);
         parameters.setParity(0);
         parameters.setStopbits(1);
         parameters.setEncoding("rtu");
         parameters.setEcho(false);
         return BescaManualSourceFactory.BescaManualVersion.MANUAL_V2.equals(this._bescaManualVersion)
            ? new MeasurementSource[]{new BescaManualV2Source(this, parameters)}
            : new MeasurementSource[]{new BescaManualSource(this, parameters, this._bescaManualVersion)};
      } catch (Exception var3) {
         throw new IOException(
            "Error initializing "
               + (BescaManualSourceFactory.BescaManualVersion.MANUAL_V2.equals(this._bescaManualVersion) ? BescaManualV2Source.class : BescaManualSource.class)
                  .getSimpleName(),
            var3
         );
      }
   }

   public static enum BescaManualVersion {
      MANUAL_V1,
      MANUAL_V2,
      BEE;
   }
}
