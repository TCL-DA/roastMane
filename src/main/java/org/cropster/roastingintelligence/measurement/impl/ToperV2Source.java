package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadCoilsRequest;
import com.ghgande.j2mod.modbus.msg.ReadCoilsResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.ControlStateSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToperV2Source extends AbstractMeasurementSource implements MeasurementReaderWriter, ControlStateSource {
   private final Logger logger = LoggerFactory.getLogger(ToperV2Source.class);
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 0;
   private static final int EXHAUST_TEMPERATURE_REGISTER = 2;
   private static final int BURNER_TEMPERATURE_REGISTER = 20;
   private static final int GAS_REGISTER = 1105;
   private static final int AIRFLOW_REGISTER = 1040;
   private static final int DRUM_SPEED_REGISTER = 1215;
   private static final int OFFSET_REQUEST_1 = 0;
   private static final int OFFSET_REQUEST_2 = 1040;
   private static final int STIRRER_REGISTER = 6;
   private static final int COOLER_REGISTER = 7;
   private static final int DRUM_REGISTER = 4;
   private static final int CHARGE_REGISTER = 32;
   private static final int DISCHARGE_REGISTER = 33;
   private static final int OFFSET_REQUEST_3 = 4;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();
   private final Map<String, ToperV2Source.ToperControlState> controlStates = new LinkedHashMap<>();
   private final Map<ControlState, Boolean> readControlStateCache = Collections.synchronizedMap(new HashMap<>());

   public ToperV2Source(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public ToperV2Source(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
      List<ControlState> supportedStates = List.of(factory.getSupportedStates(factory.getParameters()));
      this.addControlState(supportedStates, "stirrerState", 6);
      this.addControlState(supportedStates, "coolerState", 7);
      this.addControlState(supportedStates, "drumState", 4);
      this.addControlState(supportedStates, "chargeState", 32);
      this.addControlState(supportedStates, "dischargeState", 33);
   }

   void addControlState(List<ControlState> states, String stateName, int register) {
      states.stream()
         .filter(state -> state.getName().equals(stateName))
         .findFirst()
         .ifPresent(state -> this.controlStates.put(stateName, new ToperV2Source.ToperControlState(state, register)));
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to {}", this._connection.getAddress());
         this._connection.connect();
      } catch (Exception var2) {
         this.logger.error("Error opening Modbus connection", var2);
      }

      this.fireStatusChanged();
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
      return (double)value;
   }

   Double getTemperatureValue(int value) {
      return (double)value / 10.0;
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
         this.readControlStateCache.clear();
         return Collections.emptyMap();
      } else {
         ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
         transaction.setRetries(1);
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 21);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("beanTemperature", this.getTemperatureValue(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getTemperatureValue(response.getRegisterValue(2)));
            values.put("burnerTemperature", this.getTemperatureValue(response.getRegisterValue(20)));
            request = new ReadMultipleRegistersRequest(1040, 66);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            Double gas = this.getDoubleValue(response.getRegisterValue(65));
            values.put("gas", gas);
            values.put("gasControl", gas);
            Double airflow = this.getDoubleValue(response.getRegisterValue(0));
            values.put("airflow", airflow);
            values.put("airflowControl", airflow);
            request = new ReadMultipleRegistersRequest(1215, 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            Double drumSpeed = this.getDoubleValue(response.getRegisterValue(0));
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
         } catch (Exception var14) {
            this.logger.error("Error processing Modbus request", var14);
            IOUtils.closeQuietly(this);
            return values;
         }

         Map<ControlState, Boolean> stateValues = new HashMap<>();

         try {
            ModbusRequest requestCoils = new ReadCoilsRequest(4, 30);
            requestCoils.setUnitID(1);
            transaction.setRequest(requestCoils);
            transaction.execute();
            ReadCoilsResponse responseCoils = (ReadCoilsResponse)transaction.getResponse();
            this.controlStates
               .forEach(
                  (stateName, toperControlState) -> stateValues.put(
                        toperControlState.getControlState(), responseCoils.getCoilStatus(toperControlState.getRegister() - 4)
                     )
               );
         } catch (Exception var12) {
            this.logger.error("Error processing Modbus request", var12);
            IOUtils.closeQuietly(this);
         } finally {
            this.readControlStateCache.putAll(stateValues);
         }

         return values;
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      int register = switch (field) {
         case "gasControl" -> 1105;
         case "airflowControl" -> 1040;
         case "drumSpeedControl" -> 1215;
         default -> throw new IllegalArgumentException("Field not supported!");
      };
      if (this.notReady()) {
         this.logger.error("Failed to write {} value because connecting failed", field);
      } else {
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
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this.readControlStateCache.clear();
      this._connection.close();
      this.fireStatusChanged();
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

   @Override
   public Map<ControlState, Boolean> getStates() {
      return Collections.unmodifiableMap(this.readControlStateCache);
   }

   @Override
   public Boolean getState(ControlState state) {
      return state == null ? null : this.readControlStateCache.get(state);
   }

   @Override
   public void setState(ControlState state, Boolean value) {
      if (state == null) {
         this.logger.warn("Null state");
      } else {
         String stateName = state.getName();
         if (value == null) {
            this.logger.warn("Null value for state {}", stateName);
         } else if (this.notReady()) {
            this.logger.error("Failed to write {} state value because connecting failed", stateName);
         } else {
            Integer register = Optional.ofNullable(this.controlStates.get(state.getName())).map(ToperV2Source.ToperControlState::getRegister).orElse(null);
            if (register == null) {
               this.logger.error("State {} not supported", stateName);
            } else {
               this.logger.info("Writing {} value: {}", stateName, value);
               ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
               transaction.setRetries(1);
               WriteCoilRequest request = new WriteCoilRequest(register, value);
               request.setUnitID(1);
               transaction.setRequest(request);

               try {
                  transaction.execute();
                  WriteCoilResponse response = (WriteCoilResponse)transaction.getResponse();
                  if (response.getCoil() != value) {
                     throw new IOException("Response value not matching");
                  }
               } catch (Exception var8) {
                  this.logger.error("Error writing {} state", state.getName(), var8);
               }
            }
         }
      }
   }

   private boolean notReady() {
      if (!this.isReady()) {
         this.open();
         return !this.isReady();
      } else {
         return false;
      }
   }

   static class ToperControlState {
      private ControlState controlState;
      private int register;

      ToperControlState(ControlState controlState, int register) {
         this.controlState = controlState;
         this.register = register;
      }

      public ControlState getControlState() {
         return this.controlState;
      }

      public int getRegister() {
         return this.register;
      }
   }
}
