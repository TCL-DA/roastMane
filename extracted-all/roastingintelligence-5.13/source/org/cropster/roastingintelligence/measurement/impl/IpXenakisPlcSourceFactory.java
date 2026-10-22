package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class IpXenakisPlcSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private IpXenakisPlcSourceFactory.IpccPlcVersion _machineVersion = IpXenakisPlcSourceFactory.IpccPlcVersion.VERSION1;

   @Override
   protected String getDefaultIpAddress() {
      return "10.100.0.15";
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter version = new SourceParameter("version", String.class, this._machineVersion != null ? this._machineVersion.name() : null);
      version.setOptions(new String[]{IpXenakisPlcSourceFactory.IpccPlcVersion.VERSION1.name(), IpXenakisPlcSourceFactory.IpccPlcVersion.VERSION2.name()});
      version.setOptionDisplayName(IpXenakisPlcSourceFactory.IpccPlcVersion.VERSION1.name(), "Omron PLC");
      version.setOptionDisplayName(IpXenakisPlcSourceFactory.IpccPlcVersion.VERSION2.name(), "Mitsubishi PLC");
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
               throw new IllegalArgumentException("Version of the IP-CC iRm machine required!");
            }

            this._machineVersion = IpXenakisPlcSourceFactory.IpccPlcVersion.valueOf(value);
         }
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "exhaustTemperature".equals(measureName)
         || "burnerTemperature".equals(measureName)
         || "otherTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName)
               && !"gasControl".equals(measureName)
               && !"airflow".equals(measureName)
               && !"airflowControl".equals(measureName)
               && !"drumSpeed".equals(measureName)
               && !"drumSpeedControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      MeasurementSource source = null;

      try {
         source = (MeasurementSource)(switch (this._machineVersion) {
            case VERSION1 -> new IpXenakisPlcSource(this, this._inetAddress);
            case VERSION2 -> new IpccMitsubishiPlcSource(this, this._inetAddress);
            default -> throw new IllegalStateException("Invalid machine version");
         });
         return new MeasurementSource[]{source};
      } catch (Exception var4) {
         if (source != null) {
            source.close();
         }

         throw new IOException(var4);
      }
   }

   public static enum IpccPlcVersion {
      VERSION1,
      VERSION2;
   }
}
