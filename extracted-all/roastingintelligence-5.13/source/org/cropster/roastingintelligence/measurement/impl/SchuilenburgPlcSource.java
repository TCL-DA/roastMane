package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchuilenburgPlcSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(SchuilenburgPlcSource.class);
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 500;
   private static final int EXHAUST_TEMPERATURE_REGISTER = 501;
   private static final int BURNER_TEMPERATURE_REGISTER = 502;
   private static final int GAS_REGISTER = 510;
   private static final int AIRFLOW_REGISTER = 520;
   private static final int DRUM_SPEED_REGISTER = 521;
   private static final int DRUM_PRESSURE_REGISTER = 530;
   private static final int GAS_CONTROL_REGISTER = 310;
   private static final int AIRFLOW_CONTROL_REGISTER = 311;
   private static final int REQUEST_OFFSET = 500;
   private static final int REQUEST_LENGTH = 31;
   private static final int REQUEST2_OFFSET = 310;
   private static final int REQUEST2_LENGTH = 2;
   private static final int MODBUS_TCP_RETRIES = 1;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();

   public SchuilenburgPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public SchuilenburgPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      this.logger.info("Open connection to {}:{}", this._connection.getAddress(), this._connection.getPort());

      try {
         this._connection.connect();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening Modbus connection", var2);
         IOUtils.closeQuietly(this);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isConnected();
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   public double getDoubleValue(int value) {
      return Double.valueOf((double)value) / 10.0;
   }

   private void checkWordCount(ReadMultipleRegistersRequest request, ReadMultipleRegistersResponse response) throws ModbusException {
      if (request.getWordCount() != response.getWordCount()) {
         throw new ModbusException(
            String.format("Response/request word count not matching. (request: %d, response: %d)", request.getWordCount(), response.getWordCount())
         );
      }
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(500, 31);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("beanTemperature", this.getDoubleValue(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getDoubleValue(response.getRegisterValue(1)));
            values.put("burnerTemperature", this.getDoubleValue(response.getRegisterValue(2)));
            values.put("gas", this.getDoubleValue(response.getRegisterValue(10)));
            values.put("airflow", this.getDoubleValue(response.getRegisterValue(20)));
            values.put("drumSpeed", this.getDoubleValue(response.getRegisterValue(21)));
            values.put("drumPressure", this.getDoubleValue(response.getRegisterValue(30)));
            ReadMultipleRegistersRequest request2 = new ReadMultipleRegistersRequest(310, 2);
            request2.setUnitID(1);
            transaction.setRequest(request2);
            transaction.execute();
            ReadMultipleRegistersResponse response2 = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request2, response2);
            values.put("gasControl", this.getDoubleValue(response2.getRegisterValue(0)));
            values.put("airflowControl", this.getDoubleValue(response2.getRegisterValue(1)));
         } catch (Exception var7) {
            this.logger.error("Error processing Modbus request", var7);
            IOUtils.closeQuietly(this);
         }

         return values;
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      int writeRegister = switch (field) {
         case "gasControl" -> 310;
         case "airflowControl" -> 311;
         default -> throw new IllegalArgumentException(String.format("Field %s not supported!", field));
      };
      if (!this.isReady()) {
         this.open();
         if (!this.isReady()) {
            this.logger.error("Failed to write {} value because connecting failed", field);
            return;
         }
      }

      try {
         this.logger.debug("Write {} value: {}", field, value);
         ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
         transaction.setRetries(1);
         int newValue = value.intValue() * 10;
         WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(writeRegister, new SimpleRegister(newValue));
         request.setUnitID(1);
         transaction.setRequest(request);
         transaction.execute();
         WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
         if (response.getRegisterValue() != newValue) {
            throw new IOException(String.format("Error writing %s value", field));
         }
      } catch (Exception var8) {
         this.logger.error("Error writing to PLC", var8);
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
      this.fireStatusChanged();
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
            return 0.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
            return 100.0;
         default:
            return null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }
}
