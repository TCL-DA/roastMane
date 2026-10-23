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

public class TyphoonSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private TyphoonSourceFactory.TyphoonVersion _version = TyphoonSourceFactory.TyphoonVersion.VERSION1;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.0.210";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "environmentalTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return "gas".equals(measureName) ? Unit.PERCENT : null;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter version = new SourceParameter("version", String.class, this._version != null ? this._version.name() : null);
      version.setOptions(new String[]{TyphoonSourceFactory.TyphoonVersion.VERSION1.name(), TyphoonSourceFactory.TyphoonVersion.VERSION2.name()});
      version.setOptionDisplayName(TyphoonSourceFactory.TyphoonVersion.VERSION1.name(), "Shoproaster");
      version.setOptionDisplayName(TyphoonSourceFactory.TyphoonVersion.VERSION2.name(), "Hybrid");
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
               throw new IllegalArgumentException("Version of Typhoon machine required!");
            }

            this._version = TyphoonSourceFactory.TyphoonVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         if (TyphoonSourceFactory.TyphoonVersion.VERSION1.equals(this._version)) {
            return new MeasurementSource[]{new TyphoonSource(this, this._inetAddress, 53, 54, 45)};
         } else if (TyphoonSourceFactory.TyphoonVersion.VERSION2.equals(this._version)) {
            return new MeasurementSource[]{new TyphoonSource(this, this._inetAddress, 819, 820, 818)};
         } else {
            throw new IllegalArgumentException("Version of Typhoon machine required!");
         }
      } catch (Exception var3) {
         throw new IOException("Error initializing measurement source " + TyphoonSource.class.getSimpleName());
      }
   }

   public static enum TyphoonVersion {
      VERSION1,
      VERSION2;
   }
}
