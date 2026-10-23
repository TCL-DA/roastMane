package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class BuhlerPlcSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private BuhlerPlcSourceFactory.BuhlerPlcVersion _machineVersion = BuhlerPlcSourceFactory.BuhlerPlcVersion.VERSION1;
   private final Set<String> _supportedTemperatures = new HashSet<>(Arrays.asList("beanTemperature", "burnerTemperature", "afterburnerTemperature"));

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.1.10";
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter version = new SourceParameter("version", String.class, this._machineVersion != null ? this._machineVersion.name() : null);
      version.setOptions(new String[]{BuhlerPlcSourceFactory.BuhlerPlcVersion.VERSION1.name(), BuhlerPlcSourceFactory.BuhlerPlcVersion.VERSION2.name()});
      version.setOptionDisplayName(BuhlerPlcSourceFactory.BuhlerPlcVersion.VERSION1.name(), "RoastMaster 20");
      version.setOptionDisplayName(BuhlerPlcSourceFactory.BuhlerPlcVersion.VERSION2.name(), "RoastMaster 60/120/240");
      parameters.add(version);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Buhler machine required!");
            }

            this._machineVersion = BuhlerPlcSourceFactory.BuhlerPlcVersion.valueOf(value);
         }
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("drumPressure".equals(measureName)) {
         return Unit.MILLIBAR;
      } else if ("airflow".equals(measureName) || "drumSpeed".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return this._supportedTemperatures.contains(measureName) ? Unit.CELSIUS : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      MeasurementSource source = null;

      try {
         source = (MeasurementSource)(switch (this._machineVersion) {
            case VERSION1 -> new BuhlerPlcSource(this, this._inetAddress);
            case VERSION2 -> new BuhlerPlcV2Source(this, this._inetAddress);
            default -> throw new IllegalStateException("Invalid machine version");
         });
         return new MeasurementSource[]{source};
      } catch (Exception var4) {
         if (source != null) {
            source.close();
         }

         throw var4;
      }
   }

   public static enum BuhlerPlcVersion {
      VERSION1,
      VERSION2;
   }
}
