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
import org.cropster.roastingintelligence.model.ControlState;

public class ToperSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private ToperSourceFactory.ToperVersion _toperVersion = ToperSourceFactory.ToperVersion.VERSION1;
   private final ControlState[] supportedStates = new ControlState[]{
      new ControlState("drumState", ToperSourceFactory.class),
      new ControlState("chargeState", ToperSourceFactory.class),
      new ControlState("dischargeState", ToperSourceFactory.class),
      new ControlState("stirrerState", ToperSourceFactory.class),
      new ControlState("coolerState", ToperSourceFactory.class)
   };

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.137.90";
   }

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
         case "drumSpeedControl":
            return Unit.PERCENT;
         default:
            return null;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._toperVersion != null ? this._toperVersion.name() : null);
      version.setOptions(new String[]{ToperSourceFactory.ToperVersion.VERSION1.name(), ToperSourceFactory.ToperVersion.VERSION2.name()});
      version.setOptionDisplayName(ToperSourceFactory.ToperVersion.VERSION1.name(), "Version 1");
      version.setOptionDisplayName(ToperSourceFactory.ToperVersion.VERSION2.name(), "Version 2");
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

            this._toperVersion = ToperSourceFactory.ToperVersion.valueOf(value);
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return ToperSourceFactory.ToperVersion.VERSION2.equals(this._toperVersion)
            ? new MeasurementSource[]{new ToperV2Source(this, this._inetAddress)}
            : new MeasurementSource[]{new ToperSource(this, this._inetAddress)};
      } catch (Exception var3) {
         throw new IOException("Error initializing Toper source", var3);
      }
   }

   @Override
   public ControlState[] getSupportedStates(Set<SourceParameter> parameters) {
      String version = parameters.stream().filter(p -> "version".equals(p.getName())).map(p -> p.getValue()).findFirst().orElse(null);
      return ToperSourceFactory.ToperVersion.VERSION2.name().equals(version) ? this.supportedStates : new ControlState[0];
   }

   public static enum ToperVersion {
      VERSION1,
      VERSION2;
   }
}
