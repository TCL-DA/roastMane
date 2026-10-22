package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToperSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(ToperSource.class);
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 0;
   private final TCPMasterConnection _connection;

   public ToperSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public ToperSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to {}", this._connection.getAddress());
         this._connection.connect();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening Modbus connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isConnected();
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   Double getTemperature(int value) {
      return (double)value / 10.0;
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            ModbusRequest request = new ReadMultipleRegistersRequest(0, 3);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getTemperature(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getTemperature(response.getRegisterValue(2)));
            return values;
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            if (!this._connection.isConnected()) {
               this._connection.close();
               this.fireStatusChanged();
            }

            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }
}
