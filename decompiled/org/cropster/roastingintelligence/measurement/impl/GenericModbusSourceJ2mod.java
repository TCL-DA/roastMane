package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericModbusSourceJ2mod extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(GenericModbusSourceJ2mod.class);
   private final Collection<GenericModbusSource.ModbusConfig> _targets;
   private final boolean _bigEndian;

   public GenericModbusSourceJ2mod(MeasurementSourceFactory factory, Collection<GenericModbusSource.ModbusConfig> targets, boolean bigEndian) {
      super(factory);
      this._targets = targets;
      this._bigEndian = bigEndian;
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   private double getDoubleValue(InputRegister[] registers, GenericModbusSource.ModbusConfig target) {
      int[] registerValues = new int[registers.length];

      for (int i = 0; i < registerValues.length; i++) {
         registerValues[i] = registers[i].getValue();
      }

      int value = target.getStorage().extract(registerValues, this._bigEndian);
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

   protected abstract ModbusTransaction createModbusTransaction();

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTransaction transaction = this.createModbusTransaction();
            Map<String, Double> temperatureValues = new LinkedHashMap<>();

            for (GenericModbusSource.ModbusConfig target : this._targets) {
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
               } catch (ModbusException var7) {
                  this.logger.error("Error executing Modbus transaction for {}", target.getName(), var7);
                  if (!this.isReady()) {
                     this.fireStatusChanged();
                     return temperatureValues;
                  }
               }
            }

            return temperatureValues;
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
            IOUtils.closeQuietly(this);
            return Collections.emptyMap();
         }
      }
   }
}
