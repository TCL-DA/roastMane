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

public class AtillaSourceFactory extends NetworkSourceFactory {
   private AtillaSourceFactory.AtillaVersion _machineVersion = AtillaSourceFactory.AtillaVersion.VERSION1;
   private static final String PARAM_VERSION = "version";

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.0.20";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "drumTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName)) {
         return AtillaSourceFactory.AtillaVersion.VERSION1.equals(this._machineVersion) ? Unit.MILLIBAR : Unit.PERCENT;
      } else if ("airflow".equals(measureName)) {
         return AtillaSourceFactory.AtillaVersion.VERSION1.equals(this._machineVersion) ? Unit.RPM : Unit.PERCENT;
      } else if ("gasControl".equals(measureName) || "airflowControl".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return !"drumSpeed".equals(measureName) && !"drumSpeedControl".equals(measureName) ? null : Unit.RPM;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter version = new SourceParameter("version", String.class, this._machineVersion != null ? this._machineVersion.name() : null);
      version.setOptions(
         new String[]{
            AtillaSourceFactory.AtillaVersion.VERSION1.name(),
            AtillaSourceFactory.AtillaVersion.VERSION2.name(),
            AtillaSourceFactory.AtillaVersion.VERSION3.name()
         }
      );
      version.setOptionDisplayName(AtillaSourceFactory.AtillaVersion.VERSION1.name(), "Atilla Gold Plus");
      version.setOptionDisplayName(AtillaSourceFactory.AtillaVersion.VERSION2.name(), "Atilla Gold Plus & Automation");
      version.setOptionDisplayName(AtillaSourceFactory.AtillaVersion.VERSION3.name(), "Atilla Gold Plus & Automation RE");
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
               throw new IllegalArgumentException("Version of the Atilla machine required!");
            }

            this._machineVersion = AtillaSourceFactory.AtillaVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      MeasurementSource source = null;

      try {
         source = (MeasurementSource)(switch (this._machineVersion) {
            case VERSION1 -> new AtillaSource(this, this._inetAddress);
            case VERSION2 -> new AtillaV2Source(this, this._inetAddress, false);
            case VERSION3 -> new AtillaV2Source(this, this._inetAddress, true);
            default -> throw new IllegalStateException("Invalid version");
         });
         return new MeasurementSource[]{source};
      } catch (Exception var4) {
         if (source != null) {
            source.close();
         }

         throw var4;
      }
   }

   @Override
   public boolean supportsAutoStart(Set<SourceParameter> parameters) {
      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value != null && value.trim().length() != 0) {
               AtillaSourceFactory.AtillaVersion machineVersion = AtillaSourceFactory.AtillaVersion.valueOf(value);
               if (!AtillaSourceFactory.AtillaVersion.VERSION2.equals(machineVersion) && !AtillaSourceFactory.AtillaVersion.VERSION3.equals(machineVersion)) {
                  continue;
               }

               return true;
            }

            throw new IllegalArgumentException("Version of the Atilla machine required!");
         }
      }

      return false;
   }

   public static enum AtillaVersion {
      VERSION1,
      VERSION2,
      VERSION3;
   }
}
