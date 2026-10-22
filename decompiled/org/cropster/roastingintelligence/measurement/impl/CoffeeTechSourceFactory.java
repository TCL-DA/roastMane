package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.Storage;

public class CoffeeTechSourceFactory extends SerialSourceFactory {
   private static final String[] BAUD_RATES = new String[]{"2400", "9600", "19200"};
   private static final String[] STOP_BITS = new String[]{"1", "2"};
   private static final String PARAM_BAUD_RATE = "baudRate";
   private static final String PARAM_STOP_BITS = "stopBits";
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private int _baudRate = 9600;
   private String _stopBits = "1";
   private Unit _temperatureUnit = Unit.CELSIUS;

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "drumTemperature".equals(measureName)) {
         return this._temperatureUnit;
      } else if ("airflow".equals(measureName)) {
         return Unit.PERCENT;
      } else {
         return "drumSpeed".equals(measureName) ? Unit.HZ : null;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter baudRate = new SourceParameter("baudRate", String.class, String.valueOf(this._baudRate));
      baudRate.setOptions(BAUD_RATES);
      parameters.add(baudRate);
      SourceParameter stopBits = new SourceParameter("stopBits", String.class, String.valueOf(this._stopBits));
      stopBits.setOptions(STOP_BITS);
      parameters.add(stopBits);
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("baudRate".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._baudRate = Integer.valueOf(parameter.getValue());
         } else if ("stopBits".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._stopBits = parameter.getValue();
         } else if ("temperatureUnit".equals(parameter.getName())) {
            this._temperatureUnit = Unit.FAHRENHEIT.name().equalsIgnoreCase(parameter.getValue()) ? Unit.FAHRENHEIT : Unit.CELSIUS;
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPort);
         parameters.setBaudRate(this._baudRate);
         parameters.setDatabits(8);
         parameters.setParity(0);
         parameters.setStopbits(this._stopBits);
         parameters.setEncoding("rtu");
         parameters.setEcho(false);
         Map<String, GenericModbusSource.ModbusConfig> targets = new HashMap<>();
         GenericModbusSource.ModbusConfig beanTempConfig = new GenericModbusSource.ModbusConfig("beanTemperature", 0);
         beanTempConfig.setUnitId(11);
         beanTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put("beanTemperature", beanTempConfig);
         GenericModbusSource.ModbusConfig exhaustTempConfig = new GenericModbusSource.ModbusConfig("exhaustTemperature", 0);
         exhaustTempConfig.setUnitId(13);
         exhaustTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put("exhaustTemperature", exhaustTempConfig);
         GenericModbusSource.ModbusConfig drumTempConfig = new GenericModbusSource.ModbusConfig("drumTemperature", 0);
         drumTempConfig.setUnitId(12);
         drumTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put("drumTemperature", drumTempConfig);
         GenericModbusSource.ModbusConfig drumSpeedConfig = new GenericModbusSource.ModbusConfig("drumSpeed", 8451);
         drumSpeedConfig.setUnitId(1);
         drumSpeedConfig.setStorage(Storage.DECIMAL2_16);
         targets.put("drumSpeed", drumSpeedConfig);
         GenericModbusSource.ModbusConfig airflowConfig = new GenericModbusSource.ModbusConfig("airflow", 8451);
         airflowConfig.setUnitId(2);
         airflowConfig.setStorage(Storage.DECIMAL2_16);
         targets.put("airflow", airflowConfig);
         return new MeasurementSource[]{new CoffeeTechSource(this, parameters, targets)};
      } catch (Exception var9) {
         throw new IOException(var9);
      }
   }
}
