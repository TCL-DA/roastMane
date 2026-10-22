package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
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

public class RceMitsubishiSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(RceMitsubishiSource.class);
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 231;
   private static final int EXHAUST_TEMPERATURE_REGISTER = 233;
   private static final int GAS_REGISTER = 255;
   private static final int AIRFLOW_REGISTER = 253;
   private static final int DRUM_SPEED_REGISTER = 250;
   private static final int REQUEST_OFFSET = 231;
   private static final int REQUEST_LENGTH = 25;
   private static final int MODBUS_TCP_RETRIES = 1;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();

   public RceMitsubishiSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public RceMitsubishiSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
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

   public double getTemperature(int value) {
      return Double.valueOf((double)value) / 10.0;
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
            transaction.setRetries(1);
            ModbusRequest request = new ReadMultipleRegistersRequest(231, 25);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getDoubleWord(response, 0));
            values.put("exhaustTemperature", this.getDoubleWord(response, 2));
            double gas = (double)response.getRegisterValue(24);
            values.put("gas", gas);
            values.put("gasControl", gas);
            double airflow = (double)response.getRegisterValue(22);
            values.put("airflow", airflow);
            values.put("airflowControl", airflow);
            double drumSpeed = (double)response.getRegisterValue(19);
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
            return values;
         } catch (Exception var11) {
            this.logger.error("Error processing Modbus request", var11);
            IOUtils.closeQuietly(this);
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      int writeRegister = switch (field) {
         case "gasControl" -> 255;
         case "airflowControl" -> 253;
         case "drumSpeedControl" -> 250;
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
         int newValue = value.intValue();
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
            return 5.0;
         case "airflowControl":
            return 20.0;
         case "drumSpeedControl":
            return 9.0;
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
         case "drumSpeedControl":
            return 43.0;
         default:
            return null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }
}
