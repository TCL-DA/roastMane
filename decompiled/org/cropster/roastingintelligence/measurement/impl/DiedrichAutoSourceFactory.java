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

public class DiedrichAutoSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private DiedrichAutoSourceFactory.DiedrichPLCVersion _diedrichVersion = DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION1;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.251.202";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "inletTemperature".equals(measureName)) {
         return Unit.FAHRENHEIT;
      } else {
         return !"gas".equals(measureName) && !"airflow".equals(measureName) && !"gasControl".equals(measureName) && !"airflowControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._diedrichVersion != null ? this._diedrichVersion.name() : null);
      version.setOptions(
         new String[]{
            DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION1.name(),
            DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION2.name(),
            DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION3.name()
         }
      );
      version.setOptionDisplayName(DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION1.name(), "IR series - ZII/Z2.5");
      version.setOptionDisplayName(DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION2.name(), "CR series - ZII/Z2.5");
      version.setOptionDisplayName(DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION3.name(), "DR/CR series - Semi-Auto/ZIII");
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
               throw new IllegalArgumentException("Version of Diedrich machine required!");
            }

            this._diedrichVersion = DiedrichAutoSourceFactory.DiedrichPLCVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      if (DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION1.equals(this._diedrichVersion)) {
         return new MeasurementSource[]{new DiedrichAutoSource(this, this._inetAddress)};
      } else if (DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION2.equals(this._diedrichVersion)) {
         return new MeasurementSource[]{new DiedrichAutoSource(this, this._inetAddress), new DiedrichProtosXSource(this)};
      } else if (DiedrichAutoSourceFactory.DiedrichPLCVersion.VERSION3.equals(this._diedrichVersion)) {
         return new MeasurementSource[]{new DiedrichAutoZ3Source(this, this._inetAddress)};
      } else {
         throw new IOException("Invalid Diedrich version");
      }
   }

   public static enum DiedrichPLCVersion {
      VERSION1,
      VERSION2,
      VERSION3;
   }
}
