package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class CoffedSerialSourceFactory extends SerialSourceFactory {
   private static final String PARAM_VERSION = "version";
   private static final String PARAM_MIN_GAS = "minGas";
   private static final String PARAM_MAX_GAS = "maxGas";
   private static final String PARAM_MIN_AIRFLOW = "minAirflow";
   private static final String PARAM_MAX_AIRFLOW = "maxAirflow";
   private static final int GAS_MIN = 0;
   private static final int GAS_MAX = 100;
   private static final int AIRFLOW_MIN = 0;
   private static final int AIRFLOW_MAX = 100;
   private static final String[] GAS_OPTIONS = new String[21];
   private static final String[] AIRFLOW_OPTIONS = new String[21];
   private CoffedSerialSourceFactory.CoffedManualVersion _coffedManualVersion;
   private int _minGas = 0;
   private int _maxGas = 100;
   private int _minAirflow = 0;
   private int _maxAirflow = 100;

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName) && !"gasControl".equals(measureName) && !"airflow".equals(measureName) && !"airflowControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._coffedManualVersion != null ? this._coffedManualVersion.name() : null);
      version.setOptions(Arrays.stream(CoffedSerialSourceFactory.CoffedManualVersion.values()).map(Enum::name).toArray(String[]::new));
      version.setOptionDisplayName(CoffedSerialSourceFactory.CoffedManualVersion.SR3.name(), "SR3");
      version.setOptionDisplayName(CoffedSerialSourceFactory.CoffedManualVersion.SR3_DELTA.name(), "SR3 Delta");
      version.setOptionDisplayName(CoffedSerialSourceFactory.CoffedManualVersion.SR5.name(), "SR5");
      version.setOptionDisplayName(CoffedSerialSourceFactory.CoffedManualVersion.SR5_DELTA.name(), "SR5 Delta");
      parameters.add(version);
      parameters.addAll(super.getParameters());
      SourceParameter minGasParameter = new SourceParameter("minGas", String.class, String.valueOf(this._minGas));
      minGasParameter.setOptions(GAS_OPTIONS);
      parameters.add(minGasParameter);
      SourceParameter maxGasParameter = new SourceParameter("maxGas", String.class, String.valueOf(this._maxGas));
      maxGasParameter.setOptions(GAS_OPTIONS);
      parameters.add(maxGasParameter);
      SourceParameter minAirflowParameter = new SourceParameter("minAirflow", String.class, String.valueOf(this._minAirflow));
      minAirflowParameter.setOptions(AIRFLOW_OPTIONS);
      parameters.add(minAirflowParameter);
      SourceParameter maxAirflowParameter = new SourceParameter("maxAirflow", String.class, String.valueOf(this._maxAirflow));
      maxAirflowParameter.setOptions(AIRFLOW_OPTIONS);
      parameters.add(maxAirflowParameter);
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
               String.format("%s must be greater than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName(), min))
            );
         } else if (parameterValue > max) {
            throw new IllegalArgumentException(
               String.format("%s must be smaller than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName(), max))
            );
         } else {
            return parameterValue;
         }
      }
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Integer minGas = this._minGas;
      Integer maxGas = this._maxGas;
      Integer minAirflow = this._minAirflow;
      Integer maxAirflow = this._maxAirflow;

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Coffed Manual machine required!");
            }

            this._coffedManualVersion = CoffedSerialSourceFactory.CoffedManualVersion.valueOf(value);
         } else if ("minGas".equals(parameter.getName())) {
            minGas = this.validateParameter(parameter, 0, 100);
         } else if ("maxGas".equals(parameter.getName())) {
            maxGas = this.validateParameter(parameter, 0, 100);
         } else if ("minAirflow".equals(parameter.getName())) {
            minAirflow = this.validateParameter(parameter, 0, 100);
         } else if ("maxAirflow".equals(parameter.getName())) {
            maxAirflow = this.validateParameter(parameter, 0, 100);
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
         }
      }
   }

   protected SerialParameters getSerialParameters() {
      SerialParameters parameters = new SerialParameters();
      parameters.setPortName(this._serialPort);
      parameters.setDatabits(8);
      parameters.setParity(0);
      parameters.setEncoding("rtu");
      parameters.setEcho(false);
      if (this._coffedManualVersion.name().endsWith("DELTA")) {
         parameters.setBaudRate(9600);
         parameters.setStopbits(1);
      } else {
         parameters.setBaudRate(115200);
         parameters.setStopbits(3);
      }

      return parameters;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         if (!CoffedSerialSourceFactory.CoffedManualVersion.SR3.equals(this._coffedManualVersion)
            && !CoffedSerialSourceFactory.CoffedManualVersion.SR3_DELTA.equals(this._coffedManualVersion)) {
            CoffedSerialGasAirflowSource source = new CoffedSerialGasAirflowSource(this, this.getSerialParameters(), this._coffedManualVersion);
            source.setMinGas(this._minGas);
            source.setMaxGas(this._maxGas);
            source.setMinAirflow(this._minAirflow);
            source.setMaxAirflow(this._maxAirflow);
            return new MeasurementSource[]{source};
         } else {
            return new MeasurementSource[]{new CoffedSerialSource(this, this.getSerialParameters(), this._coffedManualVersion)};
         }
      } catch (Exception var3) {
         throw new IOException("Error initializing " + CoffedSerialSource.class.getSimpleName(), var3);
      }
   }

   static {
      Arrays.setAll(GAS_OPTIONS, i -> String.valueOf(0 + i * 5));
      Arrays.setAll(AIRFLOW_OPTIONS, i -> String.valueOf(0 + i * 5));
   }

   public static enum CoffedManualVersion {
      SR3,
      SR3_DELTA,
      SR5,
      SR5_DELTA;
   }
}
