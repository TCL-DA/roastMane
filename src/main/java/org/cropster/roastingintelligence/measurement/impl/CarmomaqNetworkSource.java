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
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarmomaqNetworkSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CarmomaqNetworkSource.class);
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int UNIT_ID = 1;
   private static final int GAS_REGISTER = 45;
   private static final int DRUM_SPEED_REGISTER = 46;
   private static final int AIRFLOW_REGISTER = 47;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();

   public CarmomaqNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public CarmomaqNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
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
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   Double getTemperature(int value) {
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
            ModbusRequest request = new ReadMultipleRegistersRequest(43, 5);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getTemperature(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getTemperature(response.getRegisterValue(1)));
            Double gas = (double)response.getRegisterValue(2);
            values.put("gas", gas);
            values.put("gasControl", gas);
            Double drumSpeed = (double)response.getRegisterValue(3);
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
            Double airflow = (double)response.getRegisterValue(4);
            values.put("airflow", airflow);
            values.put("airflowControl", airflow);
            return values;
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
            if (!this.isReady()) {
               this._connection.close();
               this.fireStatusChanged();
            }

            return Collections.emptyMap();
         }
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      int register = switch (field) {
         case "gasControl" -> 45;
         case "airflowControl" -> 47;
         case "drumSpeedControl" -> 46;
         default -> throw new IllegalArgumentException("Field not supported!");
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
            return 100.0;
         case "airflowControl":
         case "drumSpeedControl":
            return 120.0;
         default:
            return null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }
}
