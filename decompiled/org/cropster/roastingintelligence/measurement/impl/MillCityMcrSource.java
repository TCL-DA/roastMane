package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MillCityMcrSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(MillCityMcrSource.class);
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private static final int UNIT_ID = 1;
   private final SerialConnection _connection;

   public MillCityMcrSource(MillCitySourceFactory factory, SerialParameters parameters) {
      super(factory);
      this._connection = new SerialConnection(parameters);
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

   private Double getTemperatureValue(int value) {
      return (double)value / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(1);
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusRequest request = new ReadMultipleRegistersRequest(4096, 7);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Double environmentalTemperature = this.getTemperatureValue(response.getRegisterValue(0));
            values.put("environmentalTemperature", environmentalTemperature);
            values.put("exhaustTemperature", environmentalTemperature);
            values.put("drumTemperature", this.getTemperatureValue(response.getRegisterValue(1)));
            values.put("beanTemperature", this.getTemperatureValue(response.getRegisterValue(2)));
            values.put("inletTemperature", this.getTemperatureValue(response.getRegisterValue(3)));
            values.put("gas", (double)response.getRegister(4).toShort() / 1000.0);
            values.put("drumSpeed", (double)response.getRegisterValue(5) / 100.0);
            values.put("airflow", (double)response.getRegisterValue(6) / 100.0);
            return values;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request", var6);
            if (!this.isReady()) {
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
