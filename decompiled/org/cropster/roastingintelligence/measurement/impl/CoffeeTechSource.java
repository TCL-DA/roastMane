package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeTechSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(CoffeeTechSource.class);
   private static final int MODBUS_RETRIES = 0;
   private static final int RETRIES = 3;
   private static final int READ_TIMEOUT = 50;
   private final Map<String, GenericModbusSource.ModbusConfig> _targets;
   private final Collection<GenericModbusSource.ModbusConfig> _configuredTargets = new ArrayList<>();
   private final SerialConnection _connection;

   public CoffeeTechSource(MeasurementSourceFactory sourceFactory, SerialParameters parameters, Map<String, GenericModbusSource.ModbusConfig> targets) {
      super(sourceFactory);
      this._targets = targets;
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(50);
   }

   @Override
   public void open() {
      try {
         this._connection.open();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening serial Modbus RTU connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._configuredTargets.clear();

      for (Channel channel : channels) {
         this._configuredTargets.add(this._targets.get(channel.getName()));
      }
   }

   protected ModbusTransaction createModbusTransaction() {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
      transaction.setRetries(0);
      return transaction;
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }

   private double getDoubleValue(InputRegister[] registers, GenericModbusSource.ModbusConfig target) {
      int[] registerValues = new int[registers.length];

      for (int i = 0; i < registerValues.length; i++) {
         registerValues[i] = registers[i].getValue();
      }

      int value = target.getStorage().extract(registerValues, false);
      return target.getStorage().decode(value);
   }

   private double getDoubleValue(ReadInputRegistersResponse response, GenericModbusSource.ModbusConfig target) {
      return this.getDoubleValue(response.getRegisters(), target);
   }

   private double getDoubleValue(ReadMultipleRegistersResponse response, GenericModbusSource.ModbusConfig target) {
      return this.getDoubleValue(response.getRegisters(), target);
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTransaction transaction = this.createModbusTransaction();
            Map<String, Double> temperatureValues = new LinkedHashMap<>();

            for (GenericModbusSource.ModbusConfig target : this._configuredTargets) {
               for (int retry = 1; retry <= 3; retry++) {
                  ModbusRequest request;
                  if (target.getFunction() == 4) {
                     request = new ReadInputRegistersRequest(target.getRegister(), target.getStorage().getRegisterCount());
                  } else {
                     request = new ReadMultipleRegistersRequest(target.getRegister(), target.getStorage().getRegisterCount());
                  }

                  request.setUnitID(target.getUnitId());
                  transaction.setRequest(request);

                  try {
                     transaction.execute();
                     if (target.getFunction() == 4) {
                        ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
                        temperatureValues.put(target.getName(), this.getDoubleValue(response, target));
                     } else {
                        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
                        temperatureValues.put(target.getName(), this.getDoubleValue(response, target));
                     }
                     break;
                  } catch (ModbusException var8) {
                     if (retry == 3) {
                        this.logger.error("Error executing Modbus transaction for {} (try {}): {}", new Object[]{target.getName(), retry, var8.getMessage()});
                     } else if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Error executing Modbus transaction for {} (try {}): {}", new Object[]{target.getName(), retry, var8.getMessage()});
                     }

                     if (!this.isReady()) {
                        this.fireStatusChanged();
                        return temperatureValues;
                     }
                  }
               }
            }

            return temperatureValues;
         } catch (Exception var9) {
            this.logger.error("Error processing Modbus request", var9);
            IOUtils.closeQuietly(this);
            return Collections.emptyMap();
         }
      }
   }
}
