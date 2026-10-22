package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class CoffedNetworkSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_MIN_GAS = "minGas";
   private static final String PARAM_MAX_GAS = "maxGas";
   private static final String PARAM_MIN_AIRFLOW = "minAirflow";
   private static final String PARAM_MAX_AIRFLOW = "maxAirflow";
   private static final String PARAM_MIN_DRUM_SPEED = "minDrumSpeed";
   private static final String PARAM_MAX_DRUM_SPEED = "maxDrumSpeed";
   private static final int GAS_MIN = 0;
   private static final int GAS_MAX = 100;
   private static final int AIRFLOW_MIN = 45;
   private static final int AIRFLOW_MAX = 100;
   private static final int DRUM_SPEED_MIN = 22;
   private static final int DRUM_SPEED_MAX = 100;
   private Integer _minGas;
   private Integer _maxGas;
   private Integer _minAirflow;
   private Integer _maxAirflow;
   private Integer _minDrumSpeed;
   private Integer _maxDrumSpeed;

   @Override
   protected String getDefaultIpAddress() {
      return "10.0.0.100";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("drumSpeed".equals(measureName) || "drumSpeedControl".equals(measureName)) {
         return Unit.RPM;
      } else {
         return !"gas".equals(measureName) && !"airflow".equals(measureName) && !"gasControl".equals(measureName) && !"airflowControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter minGasParameter = new SourceParameter("minGas", String.class, this._minGas != null ? String.valueOf(this._minGas) : "");
      parameters.add(minGasParameter);
      SourceParameter maxGasParameter = new SourceParameter("maxGas", String.class, this._maxGas != null ? String.valueOf(this._maxGas) : "");
      parameters.add(maxGasParameter);
      SourceParameter minAirflowParameter = new SourceParameter("minAirflow", String.class, this._minAirflow != null ? String.valueOf(this._minAirflow) : "");
      parameters.add(minAirflowParameter);
      SourceParameter maxAirflowParameter = new SourceParameter("maxAirflow", String.class, this._maxAirflow != null ? String.valueOf(this._maxAirflow) : "");
      parameters.add(maxAirflowParameter);
      SourceParameter minDrumSpeedParameter = new SourceParameter(
         "minDrumSpeed", String.class, this._minDrumSpeed != null ? String.valueOf(this._minDrumSpeed) : ""
      );
      parameters.add(minDrumSpeedParameter);
      SourceParameter maxDrumSpeedParameter = new SourceParameter(
         "maxDrumSpeed", String.class, this._maxDrumSpeed != null ? String.valueOf(this._maxDrumSpeed) : ""
      );
      parameters.add(maxDrumSpeedParameter);
      return Collections.unmodifiableSet(parameters);
   }

   public Integer validateParameter(SourceParameter parameter, int min, int max) {
      Integer parameterValue = null;
      if (StringUtils.isBlank(parameter.getValue())) {
         throw new IllegalArgumentException(String.format("%s required", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName())));
      } else {
         parameterValue = Integer.valueOf(parameter.getValue());
         if (parameterValue < min) {
            throw new IllegalArgumentException(
               String.format("%s must be greater than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), min)
            );
         } else if (parameterValue > max) {
            throw new IllegalArgumentException(
               String.format("%s must be smaller than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), max)
            );
         } else {
            return parameterValue;
         }
      }
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Integer minGas = null;
      Integer maxGas = null;
      Integer minAirflow = null;
      Integer maxAirflow = null;
      Integer minDrumSpeed = null;
      Integer maxDrumSpeed = null;

      for (SourceParameter parameter : parameters) {
         if ("minGas".equals(parameter.getName())) {
            minGas = this.validateParameter(parameter, 0, 100);
         } else if ("maxGas".equals(parameter.getName())) {
            maxGas = this.validateParameter(parameter, 0, 100);
         } else if ("minAirflow".equals(parameter.getName())) {
            minAirflow = this.validateParameter(parameter, 45, 100);
         } else if ("maxAirflow".equals(parameter.getName())) {
            maxAirflow = this.validateParameter(parameter, 45, 100);
         } else if ("minDrumSpeed".equals(parameter.getName())) {
            minDrumSpeed = this.validateParameter(parameter, 22, 100);
         } else if ("maxDrumSpeed".equals(parameter.getName())) {
            maxDrumSpeed = this.validateParameter(parameter, 22, 100);
         }
      }

      if (minGas >= maxGas) {
         throw new IllegalArgumentException(
            String.format(
               "%s must be smaller than %s",
               org.cropster.roastingintelligence.util.Messages.m_("minGas"),
               org.cropster.roastingintelligence.util.Messages.m_("maxGas")
            )
         );
      } else {
         this._minGas = minGas;
         this._maxGas = maxGas;
         if (minAirflow >= maxAirflow) {
            throw new IllegalArgumentException(
               String.format(
                  "%s must be smaller than %s",
                  org.cropster.roastingintelligence.util.Messages.m_("minAirflow"),
                  org.cropster.roastingintelligence.util.Messages.m_("maxAirflow")
               )
            );
         } else {
            this._minAirflow = minAirflow;
            this._maxAirflow = maxAirflow;
            if (minDrumSpeed >= maxDrumSpeed) {
               throw new IllegalArgumentException(
                  String.format(
                     "%s must be smaller than %s",
                     org.cropster.roastingintelligence.util.Messages.m_("minDrumSpeed"),
                     org.cropster.roastingintelligence.util.Messages.m_("maxDrumSpeed")
                  )
               );
            } else {
               this._minDrumSpeed = minDrumSpeed;
               this._maxDrumSpeed = maxDrumSpeed;
            }
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{
            new CoffedNetworkSource(
               this,
               this._inetAddress,
               new CoffedNetworkSource.Limits(this._minGas, this._maxGas, this._minAirflow, this._maxAirflow, this._minDrumSpeed, this._maxDrumSpeed)
            )
         };
      } catch (Exception var3) {
         throw new IOException("Error initializing measurement source " + CoffedNetworkSource.class.getSimpleName());
      }
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
