package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiedrichProtosXSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(DiedrichProtosXSource.class);
   private static final int UNIT_ID = 1;
   private static final String IP_ADDRESS = "192.168.251.200";
   private TCPMasterConnection _connection;

   public DiedrichProtosXSource(MeasurementSourceFactory factory) {
      super(factory);

      try {
         this._connection = new TCPMasterConnection(InetAddress.getByName("192.168.251.200"));
         this._connection.setPort(502);
         this._connection.setTimeout(300);
      } catch (UnknownHostException var3) {
         this.logger.error("Error configuring connection to Protos-X device", var3);
      }
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to Protos-X device at {}", this._connection.getAddress());
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
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   Double getTemperature(int value) {
      return Unit.FAHRENHEIT.from(BigDecimal.valueOf((double)value / 10.0), Unit.CELSIUS).doubleValue();
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
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            ModbusRequest request = new ReadMultipleRegistersRequest(3, 3);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put("exhaustTemperature", this.getTemperature(response.getRegisterValue(0)));
            values.put("inletTemperature", this.getTemperature(response.getRegisterValue(2)));
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            if (!this.isReady()) {
               IOUtils.closeQuietly(this);
               this.fireStatusChanged();
            }
         }

         return values;
      }
   }
}
