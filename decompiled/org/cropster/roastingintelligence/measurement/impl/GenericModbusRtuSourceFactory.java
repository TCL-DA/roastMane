package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
public class GenericModbusRtuSourceFactory extends SerialSourceFactory {
   private static final String PARAM_ENCODING = "encoding";
   private static final String PARAM_BAUD_RATE = "baudRate";
   private static final String PARAM_DATA_BITS = "dataBits";
   private static final String PARAM_PARITY = "parity";
   private static final String PARAM_STOP_BITS = "stopBits";
   private static final String PARAM_MODBUS_FUNCTION = "modbusFunction";
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private static final String PARAM_BIG_ENDIAN = "bigEndian";
   private static final String PARAM_STORAGE_FORMAT = "storageFormatTemperature";
   private static final String PARAM_SLAVE_BEAN = "beanTemperatureSlave";
   private static final String PARAM_REGISTER_BEAN = "beanTemperatureRegister";
   private static final String PARAM_SLAVE_EXHAUST = "exhaustTemperatureSlave";
   private static final String PARAM_REGISTER_EXHAUST = "exhaustTemperatureRegister";
   private static final String[] ENCODINGS = new String[]{"rtu", "ascii"};
   private static final String[] BAUD_RATES = new String[]{"2400", "3600", "4800", "7200", "9600", "19200", "38400", "57600", "115200"};
   private static final String[] DATA_BITS = new String[]{"8", "7"};
   private static final String[] PARITIES = new String[]{"None", "Odd", "Even"};
   private static final String[] STOP_BITS = new String[]{"1", "2"};
   private static final String[] MODBUS_FUNCTIONS = new String[]{"3", "4"};
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private String _encoding = "rtu";
   private int _baudRate = 9600;
   private int _dataBits = 8;
   private int _parity = 0;
   private String _stopBits = "1";
   private int _modbusFunction = 3;
   private boolean _bigEndian;
   private Unit _temperatureUnit = Unit.CELSIUS;
   private Storage _temperatureStorage = Storage.FLOAT;
   private Integer _beanTemperatureSlave = 1;
   private Integer _beanTemperatureRegister;
   private Integer _exhaustTemperatureSlave;
   private Integer _exhaustTemperatureRegister;
   private boolean _filtered = false;

   private void checkSlaveId(int slaveId) {
      if (slaveId < 1 || slaveId > 247) {
         throw new IllegalArgumentException("Modbus Slave Id must be between 1 and 247!");
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter encoding = new SourceParameter("encoding", String.class, String.valueOf(this._encoding));
      encoding.setOptions(ENCODINGS);
      parameters.add(encoding);
      SourceParameter baudRate = new SourceParameter("baudRate", String.class, String.valueOf(this._baudRate));
      baudRate.setOptions(BAUD_RATES);
      parameters.add(baudRate);
      SourceParameter dataBits = new SourceParameter("dataBits", String.class, String.valueOf(this._dataBits));
      dataBits.setOptions(DATA_BITS);
      parameters.add(dataBits);
      SourceParameter parity = new SourceParameter("parity", String.class, PARITIES[this._parity]);
      parity.setOptions(PARITIES);
      parameters.add(parity);
      SourceParameter stopBits = new SourceParameter("stopBits", String.class, this._stopBits);
      stopBits.setOptions(STOP_BITS);
      parameters.add(stopBits);
      SourceParameter modbusFunction = new SourceParameter("modbusFunction", String.class, String.valueOf(this._modbusFunction));
      modbusFunction.setOptions(MODBUS_FUNCTIONS);
      parameters.add(modbusFunction);
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      parameters.add(new SourceParameter("bigEndian", Boolean.class, Boolean.valueOf(this._bigEndian).toString()));
      SourceParameter storage = new SourceParameter("storageFormatTemperature", String.class, this._temperatureStorage.name());
      storage.setOptions(GenericModbusTcpSourceFactory.STORAGE_OPTIONS);
      parameters.add(storage);
      parameters.add(new SourceParameter("beanTemperatureSlave", Integer.class, this._beanTemperatureSlave.toString()));
      parameters.add(
         new SourceParameter("beanTemperatureRegister", Integer.class, this._beanTemperatureRegister != null ? this._beanTemperatureRegister.toString() : null)
      );
      parameters.add(
         new SourceParameter("exhaustTemperatureSlave", Integer.class, this._exhaustTemperatureSlave != null ? this._exhaustTemperatureSlave.toString() : null)
      );
      parameters.add(
         new SourceParameter(
            "exhaustTemperatureRegister", Integer.class, this._exhaustTemperatureRegister != null ? this._exhaustTemperatureRegister.toString() : null
         )
      );
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      this._beanTemperatureRegister = null;
      this._exhaustTemperatureRegister = null;

      for (SourceParameter parameter : parameters) {
         if ("encoding".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._encoding = parameter.getValue();
         } else if ("baudRate".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._baudRate = Integer.valueOf(parameter.getValue());
         } else if ("dataBits".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._dataBits = Integer.valueOf(parameter.getValue());
         } else if ("parity".equals(parameter.getName())) {
            for (int i = 0; i < PARITIES.length; i++) {
               if (PARITIES[i].equals(parameter.getValue())) {
                  this._parity = i;
                  break;
               }
            }
         } else if ("stopBits".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._stopBits = parameter.getValue();
         } else if ("modbusFunction".equals(parameter.getName())) {
            this._modbusFunction = Integer.valueOf(parameter.getValue());
         } else if ("temperatureUnit".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._temperatureUnit = Unit.valueOf(parameter.getValue());
         } else if ("bigEndian".equals(parameter.getName())) {
            this._bigEndian = Boolean.valueOf(parameter.getValue());
         } else if ("storageFormatTemperature".equals(parameter.getName())) {
            this._temperatureStorage = Storage.valueOf(parameter.getValue());
         } else if (parameter.getName().endsWith("beanTemperatureSlave")) {
            String slaveString = parameter.getValue();
            if (!StringUtils.isNotBlank(slaveString)) {
               throw new IllegalArgumentException("Slave ID (Bean Temp.) required!");
            }

            this.checkSlaveId(Integer.parseInt(slaveString));
            this._beanTemperatureSlave = Integer.parseInt(slaveString);
         } else if (parameter.getName().endsWith("beanTemperatureRegister")) {
            String registerString = parameter.getValue();
            if (!StringUtils.isNotBlank(registerString)) {
               throw new IllegalArgumentException("Register (Bean Temp.) required!");
            }

            this._beanTemperatureRegister = Integer.parseInt(registerString);
         } else if (parameter.getName().endsWith("exhaustTemperatureSlave")) {
            String slaveString = parameter.getValue();
            if (StringUtils.isNotBlank(slaveString)) {
               this.checkSlaveId(Integer.parseInt(slaveString));
               this._exhaustTemperatureSlave = Integer.parseInt(slaveString);
            }
         } else if (parameter.getName().endsWith("exhaustTemperatureRegister")) {
            String registerString = parameter.getValue();
            if (StringUtils.isNotBlank(registerString)) {
               this._exhaustTemperatureRegister = Integer.parseInt(registerString);
            }
         } else if ("filter".equals(parameter.getName())) {
            this._filtered = Boolean.valueOf(parameter.getValue());
         }
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) ? null : this._temperatureUnit;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPort);
         parameters.setEncoding(this._encoding);
         parameters.setBaudRate(this._baudRate);
         parameters.setDatabits(this._dataBits);
         parameters.setParity(this._parity);
         parameters.setStopbits(this._stopBits);
         parameters.setEcho(false);
         Collection<GenericModbusSource.ModbusConfig> targets = new LinkedHashSet<>();
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("beanTemperature", this._beanTemperatureRegister);
         modbusConfig.setUnitId(this._beanTemperatureSlave);
         modbusConfig.setFunction(this._modbusFunction);
         modbusConfig.setStorage(this._temperatureStorage);
         targets.add(modbusConfig);
         if (this._exhaustTemperatureSlave != null && this._exhaustTemperatureRegister != null) {
            modbusConfig = new GenericModbusSource.ModbusConfig("exhaustTemperature", this._exhaustTemperatureRegister);
            modbusConfig.setUnitId(this._exhaustTemperatureSlave);
            modbusConfig.setFunction(this._modbusFunction);
            modbusConfig.setStorage(this._temperatureStorage);
            targets.add(modbusConfig);
         }

         return new MeasurementSource[]{new GenericModbusRtuSource(this, parameters, targets, this._bigEndian)};
      } catch (Exception var5) {
         throw new IOException(var5);
      }
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
