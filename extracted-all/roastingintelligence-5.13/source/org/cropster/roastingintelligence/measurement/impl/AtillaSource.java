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
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtillaSource extends AbstractMeasurementSource {
   private static final int UNIT_ID = 1;
   private final Logger logger = LoggerFactory.getLogger(AtillaSource.class);
   private final TCPMasterConnection _connection;

   public AtillaSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public AtillaSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
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

   private double getDoubleWord(ReadMultipleRegistersResponse response, int i) {
      int lower = response.getRegisterValue(i);
      int higher = response.getRegisterValue(i + 1);
      return (double)Float.intBitsToFloat(higher << 16 | lower);
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            ModbusRequest request = new ReadMultipleRegistersRequest(0, 10);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getDoubleWord(response, 0));
            values.put("exhaustTemperature", this.getDoubleWord(response, 2));
            values.put("gas", this.getDoubleWord(response, 4));
            values.put("drumSpeed", this.getDoubleWord(response, 6));
            values.put("airflow", this.getDoubleWord(response, 8));
            return values;
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
