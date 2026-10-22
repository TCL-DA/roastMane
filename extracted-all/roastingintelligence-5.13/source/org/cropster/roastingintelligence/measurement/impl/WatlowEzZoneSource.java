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

public class WatlowEzZoneSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(WatlowEzZoneSource.class);
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private static final int COMM_UNIT_CELSIUS = 15;
   private static final int[] ADDRESSES = new int[]{450, 360};
   private volatile String[] _temperatures;
   private final SerialConnection _connection;
   private int _commUnit;

   public WatlowEzZoneSource(WatlowEzZoneSourceFactory factory, SerialParameters parameters) throws Exception {
      super(factory);
      this._connection = new SerialConnection(parameters);
   }

   @Override
   public void open() {
      try {
         this._connection.open();
         this._connection.setTimeout(300);
         this._commUnit = this.determineCommUnit();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this._connection.close();
         this.logger.error("Error opening serial Modbus RTU connection", var2);
      }
   }

   private int determineCommUnit() throws ModbusIOException, ModbusSlaveException, ModbusException {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
      transaction.setRetries(1);
      ModbusRequest request = new ReadInputRegistersRequest(2970, 1);
      request.setUnitID(1);
      request.setHeadless();
      transaction.setRequest(request);
      transaction.execute();
      ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
      return response.getRegisterValue(0);
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public void configure(Collection<Channel> channels) {
      for (Channel channel : channels) {
         if ("exhaustTemperature".equals(channel.getName())) {
            this._temperatures = new String[]{"beanTemperature", "exhaustTemperature"};
            return;
         }
      }

      this._temperatures = new String[]{"beanTemperature", "environmentalTemperature"};
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private Double getValue(ReadInputRegistersResponse response, int i) {
      int higher = response.getRegisterValue(i);
      int lower = response.getRegisterValue(i + 1);
      int bits = higher << 16 | lower;
      return (double)Float.intBitsToFloat(bits);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
            transaction.setRetries(1);
            Map<String, Double> temperatureValues = new LinkedHashMap<>();

            for (int i = 0; i < this._temperatures.length; i++) {
               ReadInputRegistersRequest request = new ReadInputRegistersRequest(ADDRESSES[i], 2);
               request.setUnitID(1);
               request.setHeadless();
               transaction.setRequest(request);
               transaction.execute();
               ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
               double temperature = this.getValue(response, 0);
               if (this._commUnit == 15) {
                  temperature = Unit.FAHRENHEIT.from(BigDecimal.valueOf(temperature), Unit.CELSIUS).doubleValue();
               }

               temperatureValues.put(this._temperatures[i], temperature);
            }

            return temperatureValues;
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
            if (!this._connection.isOpen()) {
               this.fireStatusChanged();
            }

            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
