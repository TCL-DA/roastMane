package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.Storage;

public class BertoSourceFactory extends SerialSourceFactory {
   private static final int BEAN_TEMPERATURE_UNIT_ID = 1;
   private static final int EXHAUST_TEMPERATURE_UNIT_ID = 2;
   private static final int DRUM_TEMPERATURE_UNIT_ID = 3;
   private static final int INLET_TEMPERATURE_UNIT_ID = 4;
   private static final int TEMPERATURE_REGISTER = 1000;
   private static final int GAS_CONTROL_REGISTER = 1;
   private static final int AIRFLOW_UNIT_ID = 12;
   private static final int AIRFLOW_REGISTER = 1000;
   private static final int AIRFLOW_CONTROL_REGISTER = 0;

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "exhaustTemperature".equals(measureName)
         || "drumTemperature".equals(measureName)
         || "inletTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName) && !"gasControl".equals(measureName) && !"airflow".equals(measureName) && !"airflowControl".equals(measureName)
            ? null
            : Unit.PERCENT;
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
         parameters.setStopbits(3);
         parameters.setEncoding("rtu");
         parameters.setEcho(false);
         Map<String, BertoSource.ModbusTarget> targets = new LinkedHashMap<>();
         Map<String, BertoSource.ModbusControlTarget> controls = new HashMap<>(3);
         BertoSource.ModbusTarget beanTempConfig = new BertoSource.ModbusTarget("beanTemperature", 1000);
         beanTempConfig.setUnitId(1);
         beanTempConfig.setFunction(4);
         beanTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put(beanTempConfig.getName(), beanTempConfig);
         BertoSource.ModbusTarget exhaustTempConfig = new BertoSource.ModbusTarget("exhaustTemperature", 1000);
         exhaustTempConfig.setUnitId(2);
         exhaustTempConfig.setFunction(4);
         exhaustTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put(exhaustTempConfig.getName(), exhaustTempConfig);
         BertoSource.ModbusTarget drumTempConfig = new BertoSource.ModbusTarget("drumTemperature", 1000);
         drumTempConfig.setUnitId(3);
         drumTempConfig.setFunction(4);
         drumTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put(drumTempConfig.getName(), drumTempConfig);
         BertoSource.ModbusTarget inletTempConfig = new BertoSource.ModbusTarget("inletTemperature", 1000);
         inletTempConfig.setUnitId(4);
         inletTempConfig.setFunction(4);
         inletTempConfig.setStorage(Storage.DECIMAL1_16);
         targets.put(inletTempConfig.getName(), inletTempConfig);
         BertoSource.ModbusControlTarget gasControlConfig = new BertoSource.ModbusControlTarget("gas", 1);
         gasControlConfig.setFunction(3);
         gasControlConfig.setUnitId(1);
         gasControlConfig.setStorage(Storage.DECIMAL1_16);
         gasControlConfig.setPutFunction((values, value) -> {
            values.put("gas", value);
            values.put("gasControl", value);
         });
         gasControlConfig.setMin(0);
         gasControlConfig.setMax(100);
         targets.put("gas", gasControlConfig);
         controls.put("gasControl", gasControlConfig);
         BertoSource.ModbusTarget airflowConfig = new BertoSource.ModbusTarget("airflow", 1000);
         airflowConfig.setFunction(4);
         airflowConfig.setUnitId(12);
         airflowConfig.setStorage(Storage.INTEGER_16);
         targets.put("airflow", airflowConfig);
         BertoSource.ModbusControlTarget airflowControlConfig = new BertoSource.ModbusControlTarget("airflowControl", 0);
         airflowControlConfig.setFunction(3);
         airflowControlConfig.setUnitId(12);
         airflowControlConfig.setStorage(Storage.INTEGER_16);
         airflowControlConfig.setPutFunction((values, value) -> values.put("airflowControl", value));
         airflowControlConfig.setMin(0);
         airflowControlConfig.setMax(100);
         controls.put("airflowControl", airflowControlConfig);
         return new MeasurementSource[]{new BertoSource(this, parameters, targets, controls)};
      } catch (Exception var12) {
         throw new IOException(var12);
      }
   }
}
