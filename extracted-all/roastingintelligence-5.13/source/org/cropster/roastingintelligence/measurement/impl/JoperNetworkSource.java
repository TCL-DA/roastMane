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

public class JoperNetworkSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(JoperNetworkSource.class);
   private static final int UNIT_ID = 1;
   private static final int GAS_CONTROL_REGISTER = 988;
   private static final int AIRFLOW_CONTROL_REGISTER = 987;
   private static final int DRUM_SPEED_CONTROL_REGISTER = 986;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();

   public JoperNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public JoperNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection");
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
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
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
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new ReadMultipleRegistersRequest(979, 12);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getDoubleValue(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getDoubleValue(response.getRegisterValue(1)));
            values.put("otherTemperature", this.getDoubleValue(response.getRegisterValue(2)));
            values.put("environmentalTemperature", this.getDoubleValue(response.getRegisterValue(3)));
            values.put("drumSpeed", this.getDoubleValue(response.getRegisterValue(4)));
            values.put("drumPressure", this.getDoubleValue(response.getRegisterValue(5)));
            values.put("gas", this.getDoubleValue(response.getRegisterValue(6)));
            values.put("gasControl", this.getDoubleValue(response.getRegisterValue(9)));
            values.put("drumSpeedControl", this.getDoubleValue(response.getRegisterValue(10)));
            values.put("airflowControl", this.getDoubleValue(response.getRegisterValue(11)));
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
   public synchronized void setValue(String field, Double value) {
      if (!"gasControl".equals(field) && !"airflowControl".equals(field) && !"drumSpeedControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported!");
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }
         int register = switch (field) {
            case "airflowControl" -> 987;
            case "drumSpeedControl" -> 986;
            default -> 988;
         };

         try {
            this.logger.debug("Write {} value: {}", field, value);
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            int newValue = (int)Math.round(value * 10.0);
            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(register, new SimpleRegister(newValue));
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
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
         case "drumSpeedControl":
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
         case "drumSpeedControl":
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
