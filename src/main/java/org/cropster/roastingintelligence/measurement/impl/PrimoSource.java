package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.math.BigDecimal;
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

public class PrimoSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(PrimoSource.class);
   private static final int RETRIES = 0;
   private static final int TRANS_DELAY = 50;
   private static final int READ_TIMEOUT = 400;
   private static final int UNIT_ID = 1;
   private static final int GAS_CONTROL_REGISTER = 4114;
   private static final int SET_TEMPERATURE_CONTROL_REGISTER = 4097;
   private static final int CONTROL_METHOD_REGISTER = 4101;
   private static final int CONTROL_METHOD_MANUAL = 2;
   private static final int CONTROL_METHOD_PID = 0;
   protected final SerialConnection _connection;
   private int _controlMethod = -1;
   private Unit _tempUnitRoaster = Unit.FAHRENHEIT;
   private Unit _tempUnitCropster;
   private Set<String> _channelNames;
   private Boolean _autoMode = null;

   public PrimoSource(MeasurementSourceFactory factory, SerialParameters parameters, Unit tempUnitCropster) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(400);
      this._tempUnitCropster = tempUnitCropster;
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
         this.fireStatusChanged();
         this._controlMethod = -1;
         ModbusSerialTransaction transaction = this.getModbusTransaction();

         try {
            ModbusRequest request = new ReadInputDiscretesRequest(2065, 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse)transaction.getResponse();
            BitVector coils = response.getDiscretes();
            this._tempUnitRoaster = coils.getBit(0) ? Unit.CELSIUS : Unit.FAHRENHEIT;
            this.logger.info("Temperature unit: {}", this._tempUnitRoaster);
         } catch (ModbusException var5) {
            this.logger.error("Error executing Modbus transaction", var5);
         }
      } catch (Exception var6) {
         this._connection.close();
         this.logger.error("Error opening serial Modbus RTU connection", var6);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   public ModbusSerialTransaction getModbusTransaction() {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
      transaction.setRetries(0);
      transaction.setTransDelayMS(50);
      return transaction;
   }

   private Double getDoubleValue(int registerValue) {
      return (double)registerValue / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();
         ModbusSerialTransaction transaction = this.getModbusTransaction();

         try {
            ModbusRequest request = new ReadMultipleRegistersRequest(4096, 6);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            if (response.getWordCount() == 6) {
               int controlMethod = response.getRegisterValue(5);
               if (this._controlMethod != controlMethod) {
                  this.logger.info("Control method changed to: {}", controlMethod);
                  this._controlMethod = controlMethod;
                  switch (this._controlMethod) {
                     case 0:
                        this._autoMode = true;
                        break;
                     case 2:
                        this._autoMode = false;
                        break;
                     default:
                        this._autoMode = null;
                  }
               }

               Double beanTemp = this.getDoubleValue(response.getRegisterValue(0));
               beanTemp = this._tempUnitCropster.from(BigDecimal.valueOf(beanTemp), this._tempUnitRoaster).doubleValue();
               values.put("beanTemperature", beanTemp);
               if (this._autoMode != null && this._autoMode) {
                  Double setTemp = this.getDoubleValue(response.getRegisterValue(1));
                  setTemp = this._tempUnitCropster.from(BigDecimal.valueOf(setTemp), this._tempUnitRoaster).doubleValue();
                  values.put("setTemperatureControl", setTemp);
               }
            } else {
               this.logger.error("Error executing Modbus transaction: response has invalid length: {}", response.getHexMessage());
            }

            request = new ReadMultipleRegistersRequest(4114, 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            if (response.getWordCount() == 1) {
               Double gasValue = this.getDoubleValue(response.getRegisterValue(0));
               values.put("gas", gasValue);
               if (this._autoMode != null && !this._autoMode) {
                  values.put("gasControl", gasValue);
               }
            } else {
               this.logger.error("Error executing Modbus transaction: response has invalid length: {}", response.getHexMessage());
            }
         } catch (ModbusException var8) {
            this.logger.error("Error executing Modbus transaction", var8);
            if (!this.isReady()) {
               this.fireStatusChanged();
               return values;
            }
         } catch (Exception var9) {
            this.logger.error("Error processing Modbus request", var9);
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
         }

         return values;
      }
   }

   @Override
   public void setValue(String field, Double value) {
      if (!"gasControl".equals(field) && !"setTemperatureControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported: " + field);
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         byte request = -1;
         switch (field.hashCode()) {
            case -480521909:
               if (field.equals("setTemperatureControl")) {
                  request = 0;
               }
            default:
               int writeRegister;
               int newValue;
               switch (request) {
                  case 0:
                     newValue = (int)(this._tempUnitRoaster.from(BigDecimal.valueOf(value), this._tempUnitCropster).doubleValue() * 10.0);
                     writeRegister = 4097;
                     break;
                  default:
                     newValue = (int)(value * 10.0);
                     writeRegister = 4114;
               }

               try {
                  if (newValue < 0) {
                     throw new IOException(String.format("Error writing %s value: Negative values not allowed", field));
                  }

                  this.logger.debug("Write {} value: {}", field, value);
                  ModbusSerialTransaction transaction = this.getModbusTransaction();
                  WriteSingleRegisterRequest requestx = new WriteSingleRegisterRequest(writeRegister, new SimpleRegister(newValue));
                  requestx.setUnitID(1);
                  transaction.setRequest(requestx);
                  synchronized (this) {
                     transaction.execute();
                  }

                  WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
                  if (response.getRegisterValue() != newValue) {
                     throw new IOException(String.format("Error writing %s value", field));
                  }
               } catch (Exception var10) {
                  this.logger.error("Error writing to PID", var10);
               }
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
         case "setTemperatureControl":
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
         case "setTemperatureControl":
            return this._tempUnitCropster.from(BigDecimal.valueOf(500.0), Unit.FAHRENHEIT).doubleValue();
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
               int newValue = autoMode ? 0 : 2;
               ModbusSerialTransaction transaction = this.getModbusTransaction();
               WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(4101, new SimpleRegister(newValue));
               request.setUnitID(1);
               transaction.setRequest(request);
               transaction.execute();
               WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
               if (response.getRegisterValue() != newValue) {
                  throw new IOException(String.format("Error writing auto mode", autoMode));
               }

               this._autoMode = autoMode;
            }
         } catch (Exception var10) {
            this.logger.error("Error writing to PID", var10);
         }
      }
   }

   @Override
   public Double getValidControlValue(String measurement, double value) {
      return "setTemperatureControl".equals(measurement)
            && Unit.CELSIUS.equals(this._tempUnitRoaster)
            && Unit.FAHRENHEIT.equals(this._tempUnitCropster)
            && value < 32.0
         ? 32.0
         : value;
   }
}
