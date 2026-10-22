package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
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

public class SivetzClickPlcSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(SivetzClickPlcSource.class);
   private static final int UNIT_ID = 1;
   private final TCPMasterConnection _connection;
   private boolean _airflowConfigured = false;

   public SivetzClickPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public SivetzClickPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to {}", this._connection.getAddress().getHostAddress());
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
      this._airflowConfigured = channels.stream().anyMatch(channel -> "airflow".equals(channel.getName()));
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

   private void checkWordCount(ReadMultipleRegistersRequest request, ReadMultipleRegistersResponse response) throws ModbusException {
      if (request.getWordCount() != response.getWordCount()) {
         throw new ModbusException(
            String.format("Response/request word count not matching. (request: %d, response: %d)", request.getWordCount(), response.getWordCount())
         );
      }
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(28672, 12);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("inletTemperature", this.getDoubleWord(response, 0));
            values.put("beanTemperature", this.getDoubleWord(response, 2));
            values.put("exhaustTemperature", this.getDoubleWord(response, 4));
            values.put("gas", this.getDoubleWord(response, 10));
            if (!this._airflowConfigured) {
               return values;
            }

            request = new ReadMultipleRegistersRequest(1001, 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put("airflow", (double)response.getRegisterValue(0) / 70.0);
            this.checkWordCount(request, response);
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            IOUtils.closeQuietly(this);
         }

         return values;
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
      this.fireStatusChanged();
   }
}
