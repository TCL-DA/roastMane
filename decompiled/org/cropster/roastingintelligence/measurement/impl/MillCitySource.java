package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MillCitySource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(MillCitySource.class);
   private static final int MODBUS_FUNCTION = 3;
   private final SerialConnection _connection;
   private final Collection<GenericModbusSource.ModbusConfig> _targets = new LinkedHashSet<>();
   private Integer _beanTemperatureSlave;
   private Integer _beanTemperatureRegister;
   private Integer _exhaustTemperatureSlave;
   private Integer _exhaustTemperatureRegister;
   private Integer _gasSlave;
   private Integer _gasRegister;
   private Integer _incomingAirSlave;
   private Integer _incomingAirRegister;
   private Integer _drumSpeedSlave;
   private Integer _drumSpeedRegister;
   private Integer _enviromentTemperatureSlave;
   private Integer _enviromentTemperatureRegister;
   private Integer _airflowSlave;
   private Integer _airflowRegister;

   public MillCitySource(MillCitySourceFactory factory, SerialParameters parameters, MillCitySourceFactory.MillCityVersion millCityVersion) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      switch (millCityVersion) {
         case VERSION2A:
            this._gasSlave = 1;
            this._gasRegister = 0;
            this._airflowSlave = 6;
            this._airflowRegister = 4099;
            this._drumSpeedSlave = 5;
            this._drumSpeedRegister = 4099;
            break;
         case VERSION2B:
            this._gasSlave = 1;
            this._gasRegister = 0;
            this._airflowSlave = 6;
            this._airflowRegister = 8451;
            this._drumSpeedSlave = 5;
            this._drumSpeedRegister = 8451;
            break;
         case VERSION3:
            this._enviromentTemperatureSlave = 5;
            this._enviromentTemperatureRegister = 18176;
            this._exhaustTemperatureSlave = 2;
            this._exhaustTemperatureRegister = 4097;
            this._incomingAirSlave = 2;
            this._incomingAirRegister = 4096;
            this._gasSlave = 2;
            this._gasRegister = 4098;
            this._airflowSlave = 3;
            this._airflowRegister = 8451;
            this._drumSpeedSlave = 4;
            this._drumSpeedRegister = 8451;
            break;
         case VERSION4:
            this._beanTemperatureSlave = 1;
            this._beanTemperatureRegister = 4098;
            this._exhaustTemperatureSlave = 1;
            this._exhaustTemperatureRegister = 4096;
            this._incomingAirSlave = 1;
            this._incomingAirRegister = 4099;
            this._gasSlave = 1;
            this._gasRegister = 4100;
            this._airflowSlave = 1;
            this._airflowRegister = 4102;
            this._drumSpeedSlave = 1;
            this._drumSpeedRegister = 4101;
            break;
         default:
            throw new IllegalArgumentException("Invalid Mill city version");
      }

      if (this._beanTemperatureSlave != null && this._beanTemperatureRegister != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("beanTemperature", this._beanTemperatureRegister);
         modbusConfig.setUnitId(this._beanTemperatureSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL1_16);
         this._targets.add(modbusConfig);
      }

      if (this._exhaustTemperatureSlave != null && this._exhaustTemperatureRegister != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("exhaustTemperature", this._exhaustTemperatureRegister);
         modbusConfig.setUnitId(this._exhaustTemperatureSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL1_16);
         this._targets.add(modbusConfig);
      }

      if (this._gasSlave != null && this._gasRegister != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("gas", this._gasRegister);
         modbusConfig.setUnitId(this._gasSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL3_16);
         this._targets.add(modbusConfig);
      }

      if (this._incomingAirRegister != null && this._incomingAirSlave != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("inletTemperature", this._incomingAirRegister);
         modbusConfig.setUnitId(this._incomingAirSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL1_16);
         this._targets.add(modbusConfig);
      }

      if (this._drumSpeedRegister != null && this._drumSpeedSlave != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("drumSpeed", this._drumSpeedRegister);
         modbusConfig.setUnitId(this._drumSpeedSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL2_16);
         this._targets.add(modbusConfig);
      }

      if (this._enviromentTemperatureRegister != null && this._enviromentTemperatureSlave != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("environmentalTemperature", this._enviromentTemperatureRegister);
         modbusConfig.setUnitId(this._enviromentTemperatureSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL1_16);
         this._targets.add(modbusConfig);
      }

      if (this._airflowRegister != null && this._airflowSlave != null) {
         GenericModbusSource.ModbusConfig modbusConfig = new GenericModbusSource.ModbusConfig("airflow", this._airflowRegister);
         modbusConfig.setUnitId(this._airflowSlave);
         modbusConfig.setFunction(3);
         modbusConfig.setStorage(Storage.DECIMAL2_16);
         this._targets.add(modbusConfig);
      }
   }

   @Override
   public void open() {
      try {
         this._connection.open();
         this._connection.setTimeout(300);
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening serial Modbus connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getDoubleValue(InputRegister[] registers, GenericModbusSource.ModbusConfig target) {
      int[] registerValues = new int[registers.length];

      for (int i = 0; i < registerValues.length; i++) {
         registerValues[i] = registers[i].getValue();
      }

      int value = target.getStorage().extract(registerValues, false);
      return target.getStorage().decode(value);
   }

   private double getDoubleValue(ReadMultipleRegistersResponse response, GenericModbusSource.ModbusConfig target) {
      return this.getDoubleValue(response.getRegisters(), target);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusTransaction transaction = new ModbusSerialTransaction(this._connection);
         Map<String, Double> temperatureValues = new LinkedHashMap<>();

         for (GenericModbusSource.ModbusConfig target : this._targets) {
            try {
               ModbusRequest request = new ReadMultipleRegistersRequest(target.getRegister(), 2);
               request.setUnitID(target.getUnitId());
               transaction.setRequest(request);
               transaction.execute();
               ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
               temperatureValues.put(target.getName(), this.getDoubleValue(response, target));
            } catch (Exception var7) {
               this.logger.error("Error processing Modbus request", var7);
               IOUtils.closeQuietly(this);
               return Collections.emptyMap();
            }
         }

         return temperatureValues;
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
