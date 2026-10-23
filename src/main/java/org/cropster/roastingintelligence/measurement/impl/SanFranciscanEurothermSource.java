package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SanFranciscanEurothermSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(SanFranciscanEurothermSource.class);
   private static final int MODBUS_RETRIES = 2;
   private final SerialConnection _connection;

   public SanFranciscanEurothermSource(MeasurementSourceFactory factory, SerialParameters parameters) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
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
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   Double getDoubleValue(int value) {
      return (double)value / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(2);
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusRequest request = new ReadInputRegistersRequest(289, 2);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
            values.put("beanTemperature", this.getDoubleValue(response.getRegisterValue(0)));
            values.put("environmentalTemperature", this.getDoubleValue(response.getRegisterValue(1)));
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            if (!this._connection.isOpen()) {
               IOUtils.closeQuietly(this);
               this.fireStatusChanged();
               return Collections.emptyMap();
            }
         }

         return values;
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close port");
      this._connection.close();
   }
}
