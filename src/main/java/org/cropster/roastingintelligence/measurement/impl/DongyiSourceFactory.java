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

public class DongyiSourceFactory extends SerialSourceFactory {
   private static final String PARAM_VERSION = "version";
   private static final String PARAM_DECIMAL_CORRECTION = "decimalCorrection";
   private static final String[] DECIMAL_CORRECTION_OPTIONS = new String[]{"0.1", "1", "10"};
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private String _decimalCorrection = "1";
   private DongyiSourceFactory.DongyiVersion _machineVersion = DongyiSourceFactory.DongyiVersion.VERSION1;
   private Unit _temperatureUnit = Unit.CELSIUS;

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) && !"environmentalTemperature".equals(measureName)
         ? null
         : this._temperatureUnit;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter version = new SourceParameter("version", String.class, this._machineVersion != null ? this._machineVersion.name() : null);
      version.setOptions(new String[]{DongyiSourceFactory.DongyiVersion.VERSION1.name(), DongyiSourceFactory.DongyiVersion.VERSION2.name()});
      version.setOptionDisplayName(DongyiSourceFactory.DongyiVersion.VERSION1.name(), "Version 1");
      version.setOptionDisplayName(DongyiSourceFactory.DongyiVersion.VERSION2.name(), "Version 2");
      parameters.add(version);
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      SourceParameter decimalCorrection = new SourceParameter("decimalCorrection", String.class, String.valueOf(this._decimalCorrection));
      decimalCorrection.setOptions(DECIMAL_CORRECTION_OPTIONS);
      decimalCorrection.setOptionDisplayName(DECIMAL_CORRECTION_OPTIONS[0], "1/10");
      decimalCorrection.setOptionDisplayName(DECIMAL_CORRECTION_OPTIONS[1], "1");
      decimalCorrection.setOptionDisplayName(DECIMAL_CORRECTION_OPTIONS[2], "10");
      parameters.add(decimalCorrection);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (StringUtils.isBlank(parameter.getValue())) {
               throw new IllegalArgumentException("Version of the Dongyi/Yoshan machine required!");
            }

            this._machineVersion = DongyiSourceFactory.DongyiVersion.valueOf(value);
         } else if ("decimalCorrection".equals(parameter.getName())
            && StringUtils.isNotBlank(parameter.getValue())
            && Arrays.asList(DECIMAL_CORRECTION_OPTIONS).contains(parameter.getValue())) {
            this._decimalCorrection = parameter.getValue();
         } else if ("temperatureUnit".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._temperatureUnit = Unit.valueOf(parameter.getValue());
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
         int beanTemperatureRegister = DongyiSourceFactory.DongyiVersion.VERSION1.equals(this._machineVersion) ? 0 : 4;
         boolean bigEndian = DongyiSourceFactory.DongyiVersion.VERSION1.equals(this._machineVersion);
         return new MeasurementSource[]{new DongyiSource(this, parameters, beanTemperatureRegister, bigEndian, Double.valueOf(this._decimalCorrection))};
      } catch (Exception var5) {
         throw new IOException(var5);
      }
   }

   public static enum DongyiVersion {
      VERSION1,
      VERSION2;
   }
}
