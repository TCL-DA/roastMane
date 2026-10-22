package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.cropster.roastingintelligence.util.Storage;

@NotMutuallyExclusiveSource
public class GenericModbusTcpSourceFactory extends NetworkSourceFactory {
   static final String[] MEASURES = new String[]{"beanTemperature", "exhaustTemperature", "burnerTemperature", "gas"};
   private static final Set<String> SUPPORTED_TEMPERATURES = new HashSet<>(Arrays.asList("beanTemperature", "exhaustTemperature", "burnerTemperature"));
   private static final String PARAM_PORT = "port";
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private static final String PARAM_BIG_ENDIAN = "bigEndian";
   private static final String PARAM_STORAGE_TEMPERATURE = "storageFormatTemperature";
   private static final String PARAM_STORAGE_GAS = "storageFormatGas";
   private static final String PARAM_REGISTER_SUFFIX = "Register";
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   public static final String[] STORAGE_OPTIONS = new String[]{
      Storage.FLOAT.name(),
      Storage.INTEGER.name(),
      Storage.INTEGER_16.name(),
      Storage.DECIMAL1.name(),
      Storage.DECIMAL1_16.name(),
      Storage.DECIMAL2.name(),
      Storage.DECIMAL2_16.name(),
      Storage.DECIMAL3.name()
   };
   protected int _port = 502;
   protected final Set<GenericModbusSource.ModbusConfig> _registers = new LinkedHashSet<>();
   protected Unit _temperatureUnit = Unit.CELSIUS;
   protected boolean _bigEndian;
   protected Storage _temperatureStorage = Storage.FLOAT;
   protected Storage _gasStorage = Storage.INTEGER;
   private boolean _filtered = false;

   @Override
   protected String getDefaultIpAddress() {
      return null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      parameters.add(new SourceParameter("port", String.class, String.valueOf(this._port)));
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      parameters.add(new SourceParameter("bigEndian", Boolean.class, Boolean.valueOf(this._bigEndian).toString()));
      SourceParameter temperatureStorageParameter = new SourceParameter("storageFormatTemperature", String.class, this._temperatureStorage.name());
      temperatureStorageParameter.setOptions(STORAGE_OPTIONS);
      parameters.add(temperatureStorageParameter);
      SourceParameter gasStorageParameter = new SourceParameter("storageFormatGas", String.class, this._gasStorage.name());
      gasStorageParameter.setOptions(STORAGE_OPTIONS);
      parameters.add(gasStorageParameter);

      for (String t : MEASURES) {
         String register = null;

         for (GenericModbusSource.ModbusConfig config : this._registers) {
            if (t.equals(config.getName())) {
               register = String.valueOf(config.getRegister());
            }
         }

         parameters.add(new SourceParameter(t + "Register", String.class, register));
      }

      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      this._registers.clear();

      for (SourceParameter parameter : parameters) {
         if ("port".equals(parameter.getName())) {
            this._port = Integer.valueOf(parameter.getValue());
         } else if ("temperatureUnit".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._temperatureUnit = Unit.valueOf(parameter.getValue());
         } else if ("bigEndian".equals(parameter.getName())) {
            this._bigEndian = Boolean.valueOf(parameter.getValue());
         } else if ("storageFormatTemperature".equals(parameter.getName())) {
            this._temperatureStorage = Storage.valueOf(parameter.getValue());
         } else if ("storageFormatGas".equals(parameter.getName())) {
            this._gasStorage = Storage.valueOf(parameter.getValue());
         } else if (parameter.getName().endsWith("Register")) {
            String t = parameter.getName().replace("Register", "");
            String registerString = parameter.getValue();
            if (StringUtils.isNotBlank(registerString)) {
               int register = Integer.parseInt(registerString);
               this._registers.add(new GenericModbusSource.ModbusConfig(t, register));
            }
         } else if ("filter".equals(parameter.getName())) {
            this._filtered = Boolean.valueOf(parameter.getValue());
         }
      }

      for (GenericModbusSource.ModbusConfig config : this._registers) {
         if (SUPPORTED_TEMPERATURES.contains(config.getName())) {
            config.setStorage(this._temperatureStorage);
         } else {
            config.setStorage(this._gasStorage);
         }
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      if (SUPPORTED_TEMPERATURES.contains(measureName)) {
         return this._temperatureUnit;
      } else {
         return "gas".equals(measureName) ? Unit.PERCENT : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new GenericModbusTcpSource(this, this._inetAddress, this._port, this._registers, this._bigEndian)};
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._filtered && measureName.matches(".+Temperature") ? new RemoveDuplicatesCurveModel(source) : null;
   }

   @Override
   public boolean supportsFilterOption(Set<SourceParameter> parameters) {
      for (SourceParameter parameter : parameters) {
         if ("storageFormatTemperature".equals(parameter.getName())
            && (Storage.INTEGER.name().equals(parameter.getValue()) || Storage.INTEGER_16.name().equals(parameter.getValue()))) {
            return true;
         }
      }

      return false;
   }
}
