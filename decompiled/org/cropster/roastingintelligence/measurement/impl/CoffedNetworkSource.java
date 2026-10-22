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

public class CoffedNetworkSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CoffedNetworkSource.class);
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 0;
   private static final int EXHAUST_TEMPERATURE_REGISTER = 10;
   private static final int AIRFLOW_REGISTER = 20;
   private static final int DRUM_SPEED_REGISTER = 30;
   private static final int GAS_REGISTER = 40;
   private static final int READ_TIMEOUT = 500;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();
   private final CoffedNetworkSource.Limits _limits;

   public CoffedNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress, CoffedNetworkSource.Limits limits) {
      this(factory, ipAddress, 502, limits);
   }

   public CoffedNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port, CoffedNetworkSource.Limits limits) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(500);
      this._limits = limits;
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
      return (double)value / 100.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(2);
            ModbusRequest request = new ReadMultipleRegistersRequest(0, 41);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getTemperature(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getTemperature(response.getRegisterValue(10)));
            Double airflow = (double)response.getRegisterValue(20);
            values.put("airflow", airflow);
            values.put("airflowControl", airflow);
            Double drumSpeed = (double)response.getRegisterValue(30);
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
            Double gas = (double)response.getRegisterValue(40);
            values.put("gas", gas);
            values.put("gasControl", gas);
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
      if (!"gasControl".equals(field) && !"airflowControl".equals(field) && !"drumSpeedControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported: " + field);
      } else {
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

            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(switch (field) {
               case "airflowControl" -> 20;
               case "drumSpeedControl" -> 30;
               default -> 40;
            }, new SimpleRegister(newValue));
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
      if ("gasControl".equals(field)) {
         return (double)this._limits.getMinGas();
      } else if ("airflowControl".equals(field)) {
         return (double)this._limits.getMinAirflow();
      } else {
         return "drumSpeedControl".equals(field) ? (double)this._limits.getMinDrumSpeed() : null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      if ("gasControl".equals(field)) {
         return (double)this._limits.getMaxGas();
      } else if ("airflowControl".equals(field)) {
         return (double)this._limits.getMaxAirflow();
      } else {
         return "drumSpeedControl".equals(field) ? (double)this._limits.getMaxDrumSpeed() : null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   public static class Limits {
      private int _minGas = 0;
      private int _maxGas = 100;
      private int _minAirflow = 45;
      private int _maxAirflow = 100;
      private int _minDrumSpeed = 22;
      private int _maxDrumSpeed = 100;

      public Limits(int minGas, int maxGas, int minAirflow, int maxAirflow, int minDrumSpeed, int maxDrumSpeed) {
         this._minGas = minGas;
         this._maxGas = maxGas;
         this._minAirflow = minAirflow;
         this._maxAirflow = maxAirflow;
         this._minDrumSpeed = minDrumSpeed;
         this._maxDrumSpeed = maxDrumSpeed;
      }

      public int getMinGas() {
         return this._minGas;
      }

      public void setMinGas(int minGas) {
         this._minGas = minGas;
      }

      public int getMaxGas() {
         return this._maxGas;
      }

      public void setMaxGas(int maxGas) {
         this._maxGas = maxGas;
      }

      public int getMinAirflow() {
         return this._minAirflow;
      }

      public void setMinAirflow(int minAirflow) {
         this._minAirflow = minAirflow;
      }

      public int getMaxAirflow() {
         return this._maxAirflow;
      }

      public void setMaxAirflow(int maxAirflow) {
         this._maxAirflow = maxAirflow;
      }

      public int getMinDrumSpeed() {
         return this._minDrumSpeed;
      }

      public void setMinDrumSpeed(int minDrumSpeed) {
         this._minDrumSpeed = minDrumSpeed;
      }

      public int getMaxDrumSpeed() {
         return this._maxDrumSpeed;
      }

      public void setMaxDrumSpeed(int maxDrumSpeed) {
         this._maxDrumSpeed = maxDrumSpeed;
      }
   }
}
