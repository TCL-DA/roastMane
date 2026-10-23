package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BertoSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(BertoSource.class);
   private static final int TRANS_DELAY = 50;
   private static final int READ_TIMEOUT = 300;
   private static final int MODBUS_SERIAL_RETRIES = 1;
   protected final SerialConnection _connection;
   private Map<String, BertoSource.ModbusTarget> _targets;
   private Map<String, BertoSource.ModbusControlTarget> _controls;
   private final Set<BertoSource.ModbusTarget> _enabledTargets = new LinkedHashSet<>();
   private final Map<String, BertoSource.ModbusTarget> _enabledControls = new LinkedHashMap<>();
   private final Set<String> _configuredChannels = new HashSet<>();

   public BertoSource(
      MeasurementSourceFactory factory,
      SerialParameters parameters,
      Map<String, BertoSource.ModbusTarget> targets,
      Map<String, BertoSource.ModbusControlTarget> controls
   ) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(300);
      this._targets = targets;
      this._controls = controls;
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this._connection.close();
         this.logger.error("Error opening serial Modbus RTU connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._enabledTargets.clear();
      this._enabledControls.clear();
      this._configuredChannels.clear();

      for (Channel channel : channels) {
         if (this._targets.containsKey(channel.getName())) {
            this._enabledTargets.add(this._targets.get(channel.getName()));
         }

         if (this._controls.containsKey(channel.getName())) {
            this._enabledTargets.add(this._controls.get(channel.getName()));
            this._enabledControls.put(channel.getName(), this._controls.get(channel.getName()));
         }
      }

      this._enabledTargets.forEach(target -> this._configuredChannels.add(target.getName()));
      this._configuredChannels.addAll(this._controls.keySet());
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   public ModbusSerialTransaction getModbusTransaction() {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
      transaction.setRetries(1);
      transaction.setTransDelayMS(50);
      return transaction;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      ModbusSerialTransaction transaction = this.getModbusTransaction();
      Map<String, Double> values = new LinkedHashMap<>();

      for (BertoSource.ModbusTarget target : this._enabledTargets) {
         try {
            target.putValue(values, transaction);
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request for {}", target.getName(), var6);
            if (!this.isReady()) {
               IOUtils.closeQuietly(this);
               return values;
            }
         }
      }

      return values;
   }

   @Override
   public synchronized void setValue(String field, Double value) {
      if (!this._enabledControls.containsKey(field)) {
         throw new IllegalArgumentException(String.format("Field %s not supported!", field));
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
            BertoSource.ModbusTarget target = this._controls.get(field);
            ModbusSerialTransaction transaction = this.getModbusTransaction();
            if (Storage.DECIMAL1_16.equals(target.getStorage())) {
               value = value * 10.0;
            }

            int newValue = (int)MathUtil.roundHalfToEven(value, 0);
            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(target.getRegister(), new SimpleRegister(newValue));
            request.setUnitID(target.getUnitId());
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
      this.fireStatusChanged();
   }

   @Override
   public Double getMinValue(String field) {
      return (double)this._controls.get(field).getMin();
   }

   @Override
   public Double getMaxValue(String field) {
      return (double)this._controls.get(field).getMax();
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._configuredChannels;
   }

   public static class ModbusControlTarget extends BertoSource.ModbusTarget {
      private int _min = 0;
      private int _max = 100;

      public ModbusControlTarget(String name, int register) {
         super(name, register);
      }

      public int getMin() {
         return this._min;
      }

      public void setMin(int min) {
         this._min = min;
      }

      public int getMax() {
         return this._max;
      }

      public void setMax(int max) {
         this._max = max;
      }
   }

   public static class ModbusTarget extends GenericModbusSource.ModbusConfig {
      private BiConsumer<Map<String, Double>, Double> _putFunction = (values, value) -> values.put(this.getName(), value);

      public ModbusTarget(String name, int register) {
         super(name, register);
      }

      public void setPutFunction(BiConsumer<Map<String, Double>, Double> putFunction) {
         this._putFunction = putFunction;
      }

      public Double getValue(ModbusTransaction transaction) throws ModbusException {
         ModbusRequest request;
         if (this.getFunction() == 4) {
            request = new ReadInputRegistersRequest(this.getRegister(), this.getStorage().getRegisterCount());
         } else {
            request = new ReadMultipleRegistersRequest(this.getRegister(), this.getStorage().getRegisterCount());
         }

         request.setUnitID(this.getUnitId());
         transaction.setRequest(request);
         transaction.execute();
         Double value;
         if (this.getFunction() == 4) {
            ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
            value = this.getDoubleValue(response, this);
         } else {
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            value = this.getDoubleValue(response, this);
         }

         return value;
      }

      public void putValue(Map<String, Double> values, ModbusTransaction transaction) throws ModbusException {
         Double value = this.getValue(transaction);
         this._putFunction.accept(values, value);
      }

      private double getDoubleValue(InputRegister[] registers, BertoSource.ModbusTarget target) {
         int[] registerValues = new int[registers.length];

         for (int i = 0; i < registerValues.length; i++) {
            registerValues[i] = registers[i].getValue();
         }

         int value = target.getStorage().extract(registerValues, false);
         return target.getStorage().decode(value);
      }

      private double getDoubleValue(ReadInputRegistersResponse response, BertoSource.ModbusTarget target) {
         return this.getDoubleValue(response.getRegisters(), target);
      }

      private double getDoubleValue(ReadMultipleRegistersResponse response, BertoSource.ModbusTarget target) {
         return this.getDoubleValue(response.getRegisters(), target);
      }
   }
}
