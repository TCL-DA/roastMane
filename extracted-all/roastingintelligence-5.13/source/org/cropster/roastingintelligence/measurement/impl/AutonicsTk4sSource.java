package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonicsTk4sSource extends AbstractMeasurementSource {
   private static final int CONNECTION_RETRIES = 5;
   private final Logger logger = LoggerFactory.getLogger(AutonicsTk4sSource.class);
   private final SerialConnection _connection;
   private boolean _unit2Enabled;
   private boolean _unit2Connected = false;
   private int _unit2Retries = 5;
   private boolean _unit3Enabled;
   private boolean _unit3Connected = false;
   private int _unit3Retries = 5;

   public AutonicsTk4sSource(AutonicsTk4sSourceFactory factory, SerialParameters parameters) throws Exception {
      super(factory);
      this._connection = new SerialConnection(parameters);
   }

   @Override
   public void open() {
      try {
         this._unit2Connected = false;
         this._unit2Retries = 5;
         this._unit3Connected = false;
         this._unit3Retries = 5;
         this._connection.open();
         this._connection.setTimeout(300);
         this.fireStatusChanged();
      } catch (Exception var2) {
         this._connection.close();
         this.logger.error("Error opening serial Modbus RTU connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public void configure(Collection<Channel> channels) {
      for (Channel channel : channels) {
         if ("exhaustTemperature".equals(channel.getName())) {
            this._unit2Enabled = true;
         } else if ("environmentalTemperature".equals(channel.getName())) {
            this._unit3Enabled = true;
         }
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private Double sendRequest(ModbusSerialTransaction transaction, int unitId) throws ModbusIOException, ModbusSlaveException, ModbusException {
      ModbusRequest request = new ReadInputRegistersRequest(1000, 3);
      request.setUnitID(unitId);
      request.setHeadless();
      transaction.setRequest(request);
      transaction.execute();
      ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
      Double temperature = this.getValue(response, 0);
      int decimalPoint = response.getRegisterValue(1);
      if (decimalPoint > 0) {
         temperature = temperature / ((double)decimalPoint * 10.0);
      }

      int unit = response.getRegisterValue(2);
      if (unit == 1) {
         temperature = Unit.CELSIUS.from(BigDecimal.valueOf(temperature), Unit.FAHRENHEIT).doubleValue();
      }

      return temperature;
   }

   private Double getValue(ReadInputRegistersResponse response, int i) {
      int value = response.getRegisterValue(i);
      return (double)value;
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(0);
         Map<String, Double> temperatureValues = new LinkedHashMap<>();

         try {
            Double beanTemperature = this.sendRequest(transaction, 1);
            temperatureValues.put("beanTemperature", beanTemperature);
         } catch (Exception var4) {
            this.logger.error("Error processing Modbus request (unit 1)", var4);
         }

         if (this._unit2Enabled) {
            try {
               Double exhaustTemperature = this.sendRequest(transaction, 2);
               temperatureValues.put("exhaustTemperature", exhaustTemperature);
               this._unit2Connected = true;
            } catch (Exception var6) {
               if (!this._unit2Connected) {
                  this._unit2Retries--;
                  if (this._unit2Retries <= 0) {
                     this._unit2Enabled = false;
                  }
               }

               this.logger.error("Error processing Modbus request (unit 2)", var6);
            }

            if (this._unit3Enabled) {
               try {
                  Double environmentalTemperature = this.sendRequest(transaction, 3);
                  temperatureValues.put("environmentalTemperature", environmentalTemperature);
                  this._unit3Connected = true;
               } catch (Exception var5) {
                  if (!this._unit3Connected) {
                     this._unit3Retries--;
                     if (this._unit3Retries <= 0) {
                        this._unit3Enabled = false;
                     }
                  }

                  this.logger.error("Error processing Modbus request (unit 3)", var5);
               }
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
