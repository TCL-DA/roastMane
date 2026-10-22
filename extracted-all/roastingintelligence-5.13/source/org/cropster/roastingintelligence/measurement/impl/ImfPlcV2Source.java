package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImfPlcV2Source extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(ImfPlcV2Source.class);
   private static final int CUSTOM_CONTROL_REGISTER = 22;
   private static final int DRUM_SPEED_CONTROL_REGISTER = 32;
   private static final int AIRFLOW_CONTROL_REGISTER = 42;
   private static final int INLET_TEMPERATURE_CONTROL_REGISTER = 24;
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();
   private Boolean _autoMode = null;
   private Unit _tempUnitRoaster = Unit.CELSIUS;
   private final Unit _tempUnitCropster;

   public ImfPlcV2Source(MeasurementSourceFactory factory, InetAddress ipAddress, Unit tempUnit) {
      this(factory, ipAddress, 502, tempUnit);
   }

   public ImfPlcV2Source(MeasurementSourceFactory factory, InetAddress ipAddress, int port, Unit tempUnit) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
      this._tempUnitCropster = tempUnit;
   }

   @Override
   public void open() {
      try {
         this.logger.info("Opening connection to {}", this._connection.getAddress().getHostAddress());
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
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getFloatingPointValue(ReadMultipleRegistersResponse response, int register) {
      return this.getFloatingPointValue(response.getRegisters(), register);
   }

   private double getFloatingPointValue(InputRegister[] registers, int register) {
      int lower = registers[register + 1].getValue();
      int higher = registers[register].getValue();
      int value = higher << 16 | lower;
      return (double)Float.intBitsToFloat(value);
   }

   private Register[] getWords(Double value) {
      int bitsAsInt = Float.floatToIntBits(value.floatValue());
      int hiWord = bitsAsInt >> 16;
      int loWord = bitsAsInt & 65535;
      return new SimpleRegister[]{new SimpleRegister(hiWord), new SimpleRegister(loWord)};
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         this._autoMode = null;
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new ReadMultipleRegistersRequest(10, 42);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            int confRoaster = response.getRegisterValue(40);
            this.logger.debug("Configuration Roaster: {}", StringUtils.leftPad(Integer.toBinaryString(confRoaster), 16, "0"));
            this._tempUnitRoaster = (confRoaster >> 8 & 1) == 1 ? Unit.FAHRENHEIT : Unit.CELSIUS;
            int confCropster = response.getRegisterValue(41);
            this.logger.debug("Configuration roastMane: {}", StringUtils.leftPad(Integer.toBinaryString(confCropster), 16, "0"));
            this._autoMode = (confCropster >> 8 & 1) == 0;
            values.put(
               "beanTemperature", this._tempUnitCropster.from(BigDecimal.valueOf(this.getFloatingPointValue(response, 0)), this._tempUnitRoaster).doubleValue()
            );
            values.put(
               "inletTemperature",
               this._tempUnitCropster.from(BigDecimal.valueOf(this.getFloatingPointValue(response, 2)), this._tempUnitRoaster).doubleValue()
            );
            values.put(
               "burnerTemperature",
               this._tempUnitCropster.from(BigDecimal.valueOf(this.getFloatingPointValue(response, 4)), this._tempUnitRoaster).doubleValue()
            );
            values.put("custom", this.getFloatingPointValue(response, 10));
            if (!this._autoMode) {
               values.put("customControl", this.getFloatingPointValue(response, 12));
            } else {
               values.put(
                  "setTemperatureControl",
                  this._tempUnitCropster.from(BigDecimal.valueOf(this.getFloatingPointValue(response, 14)), this._tempUnitRoaster).doubleValue()
               );
            }

            values.put("drumSpeed", this.getFloatingPointValue(response, 20));
            values.put("drumSpeedControl", this.getFloatingPointValue(response, 22));
            values.put("airflow", this.getFloatingPointValue(response, 30));
            values.put("airflowControl", this.getFloatingPointValue(response, 32));
            return values;
         } catch (Exception var7) {
            this.logger.error("Error processing Modbus request", var7);
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
            return values;
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Closing connection to {}", this._connection.getAddress().getHostAddress());
      this._autoMode = null;
      this._connection.close();
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "customControl":
            return 0.0;
         case "setTemperatureControl":
            return this._tempUnitCropster.from(BigDecimal.valueOf(0.0), Unit.CELSIUS).doubleValue();
         case "airflowControl":
         case "drumSpeedControl":
            return 50.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "customControl":
         case "airflowControl":
         case "drumSpeedControl":
            return 100.0;
         case "setTemperatureControl":
            return this._tempUnitCropster.from(BigDecimal.valueOf(550.0), Unit.CELSIUS).doubleValue();
         default:
            return null;
      }
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      int register;
      switch (field) {
         case "customControl":
            register = 22;
            if (this._autoMode == null || this._autoMode) {
               this.logger.error("Writing Vortex control is only possible in manual mode");
               return;
            }
            break;
         case "airflowControl":
            register = 42;
            break;
         case "drumSpeedControl":
            register = 32;
            break;
         case "setTemperatureControl":
            register = 24;
            value = this._tempUnitRoaster.from(BigDecimal.valueOf(value), this._tempUnitCropster).doubleValue();
            if (this._autoMode == null || !this._autoMode) {
               this.logger.error("Writing set temperature control is only possible in auto mode");
               return;
            }
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
         this.logger.debug("Write {} value: {}", field, value);
         ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
         transaction.setRetries(1);
         WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(register, this.getWords(value));
         request.setUnitID(1);
         transaction.setRequest(request);
         transaction.execute();
      } catch (Exception var6) {
         this.logger.error("Error writing to PLC", var6);
      }
   }

   @Override
   public Boolean getAutoMode(String measurement) {
      return !"customControl".equals(measurement) && !"setTemperatureControl".equals(measurement) ? null : this._autoMode;
   }

   @Override
   public void setAutoMode(boolean autoMode, String measurement) {
      if ("customControl".equals(measurement) || "setTemperatureControl".equals(measurement)) {
         try {
            synchronized (this) {
               ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
               transaction.setRetries(1);
               int confCropster = 0;
               if (!autoMode) {
                  confCropster |= 256;
               }

               this.logger.info("Writing auto mode {}: {}", autoMode, StringUtils.leftPad(Integer.toBinaryString(confCropster), 16, "0"));
               ModbusRequest request = new WriteSingleRegisterRequest(51, new SimpleRegister(confCropster));
               request.setUnitID(1);
               transaction.setRequest(request);
               transaction.execute();
               WriteSingleRegisterResponse responseWrite = (WriteSingleRegisterResponse)transaction.getResponse();
               if (responseWrite.getRegisterValue() != confCropster) {
                  throw new IOException(String.format("Error writing auto mode: %b", autoMode));
               }

               this._autoMode = autoMode;
            }
         } catch (Exception var10) {
            this.logger.error("Error writing to PLC", var10);
         }
      }
   }
}
