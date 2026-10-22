package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.impl.DiedrichValueConverter;
import org.cropster.roastingintelligence.sensor.impl.DiedrichVoltageValueConverter;

@NotMutuallyExclusiveSource
public class DiedrichGasAirflowSourceFactory extends Phidget22SourceFactory {
   private static final String PARAM_GAS = "diedrichGasPressure";
   private DiedrichGasAirflowSourceFactory.DiedrichGas _gasPressure;

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter gasPressure = new SourceParameter("diedrichGasPressure", String.class, this._gasPressure != null ? this._gasPressure.name() : null);
      gasPressure.setOptions(
         new String[]{
            DiedrichGasAirflowSourceFactory.DiedrichGas.VOLTAGE_DIVIDER.name(),
            DiedrichGasAirflowSourceFactory.DiedrichGas.WC6.name(),
            DiedrichGasAirflowSourceFactory.DiedrichGas.WC7.name(),
            DiedrichGasAirflowSourceFactory.DiedrichGas.WC11.name()
         }
      );
      parameters.add(gasPressure);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("diedrichGasPressure".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Maximum gas pressure required!");
            }

            this._gasPressure = DiedrichGasAirflowSourceFactory.DiedrichGas.valueOf(value);
         }
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      return !measureName.equals("gas") && !measureName.equals("airflow") ? null : Unit.PERCENT;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new DiedrichGasAirflowPhidget22Source(this, switch (this._gasPressure) {
         case VOLTAGE_DIVIDER -> new DiedrichVoltageValueConverter();
         case WC6 -> new DiedrichValueConverter(1.7);
         case WC7 -> new DiedrichValueConverter(1.9);
         case WC11 -> new DiedrichValueConverter(3.1);
         default -> throw new IllegalStateException("No maximum gas pressure");
      })};
   }

   public static enum DiedrichGas {
      WC6,
      WC7,
      WC11,
      VOLTAGE_DIVIDER;
   }
}
