package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
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

public class DutchMasterSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(DutchMasterSource.class);
   private static final int UNIT_ID = 1;
   private static final int GAS_CONTROL_REGISTER = 32792;
   private static final int DRUM_PRESURE_CONTROL_REGISTER = 32790;
   private static final int DRUM_SPEED_CONTROL_REGISTER = 32791;
   private static final int SET_TEMPERATURE_CONTROL_REGISTER = 32789;
   private static final int REMOTE_CONTROL_BIT = 1;
   private static final int PID_OVERRIDE_BIT = 21;
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int READ_TIMEOUT = 800;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();
   private Boolean _autoMode = null;

   public DutchMasterSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public DutchMasterSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(800);
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

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new ReadInputRegistersRequest(32768, 25);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
            values.put("exhaustTemperature", (double)response.getRegisterValue(1) / 10.0);
            values.put("beanTemperature", (double)response.getRegisterValue(2) / 10.0);
            values.put("otherTemperature", (double)response.getRegisterValue(3) / 10.0);
            values.put("drumPressure", (double)response.getRegisterValue(4) / 10.0);
            values.put("fanSpeed", (double)response.getRegisterValue(5) / 1980.0 * 100.0);
            values.put("drumSpeed", (double)response.getRegisterValue(6));
            values.put("gas", (double)response.getRegisterValue(11) / 10.0);
            ModbusRequest requestCoils = new ReadInputDiscretesRequest(32768, 22);
            requestCoils.setUnitID(1);
            transaction.setRequest(requestCoils);
            transaction.execute();
            ReadInputDiscretesResponse coilsResponse = (ReadInputDiscretesResponse)transaction.getResponse();
            BitVector coils = coilsResponse.getDiscretes();
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Coils: {}", coils);
            }

            this._autoMode = !coils.getBit(21);
            if (!coils.getBit(1)) {
               this._autoMode = null;
               return values;
            } else {
               if (this._autoMode) {
                  values.put("setTemperatureControl", (double)response.getRegisterValue(21));
               } else {
                  values.put("gasControl", (double)Math.round((double)response.getRegisterValue(24) * 0.8 + 20.0));
               }

               values.put("drumPressureControl", (double)response.getRegisterValue(22));
               values.put("drumSpeedControl", (double)response.getRegisterValue(23));
               return values;
            }
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
            IOUtils.closeQuietly(this);
            return values;
         }
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      Double max = this.getMaxValue(field);
      Double min = this.getMinValue(field);
      if (max != null && min != null) {
         if (!(value > max) && !(value < min)) {
            int register;
            switch (field) {
               case "gasControl":
                  register = 32792;
                  value = (value - 20.0) * 1.25;
                  break;
               case "drumPressureControl":
                  register = 32790;
                  break;
               case "drumSpeedControl":
                  register = 32791;
                  break;
               case "setTemperatureControl":
                  register = 32789;
                  break;
               default:
                  throw new IllegalArgumentException("Field not supported!");
            }

            if (!this.isReady()) {
               this.open();
               if (!this.isReady()) {
                  this.logger.error("Failed to write {} value because connecting failed", field);
                  return;
               }
            }

            try {
               if (this.logger.isDebugEnabled()) {
                  this.logger.debug("Write {} value: {}", field, value);
               }

               ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
               transaction.setRetries(1);
               int newValue = (int)Math.round(value);
               WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(register, new SimpleRegister(newValue));
               request.setUnitID(1);
               transaction.setRequest(request);
               transaction.execute();
               WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
               if (response.getRegisterValue() != newValue) {
                  throw new IOException(String.format("Error writing %s value", field));
               }
            } catch (Exception var10) {
               this.logger.error("Error writing to PLC", var10);
            }
         } else {
            this.logger.error("Failed to write {} because value {} is outside the range {} - {}", new Object[]{field, value, min, max});
         }
      } else {
         throw new IllegalArgumentException("Field not supported!");
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
      this._autoMode = null;
      this.fireStatusChanged();
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 20.0;
         case "drumPressureControl":
            return 80.0;
         case "drumSpeedControl":
            return 20.0;
         case "setTemperatureControl":
            return 50.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
            return 100.0;
         case "drumPressureControl":
            return 150.0;
         case "drumSpeedControl":
            return 50.0;
         case "setTemperatureControl":
            return 250.0;
         default:
            return null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public Boolean getAutoMode(String measurement) {
      return !"gasControl".equals(measurement) && !"setTemperatureControl".equals(measurement) ? null : this._autoMode;
   }

   @Override
   public void setAutoMode(boolean autoMode, String measurement) {
      if ("gasControl".equals(measurement) || "setTemperatureControl".equals(measurement)) {
         try {
            synchronized (this) {
               ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
               transaction.setRetries(1);
               this.logger.info("Writing auto mode {}", autoMode);
               ModbusRequest request = new WriteCoilRequest(32789, !autoMode);
               request.setUnitID(1);
               transaction.setRequest(request);
               transaction.execute();
            }
         } catch (Exception var8) {
            this.logger.error("Error writing to PLC", var8);
         }
      }
   }
}
