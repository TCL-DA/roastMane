package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NordicCoffeeSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(NordicCoffeeSource.class);
   protected static final int READ_TIMEOUT = 800;
   private static final int RETRIES = 1;
   protected final TCPMasterConnection _connection;
   protected final Map<String, NordicCoffeeSourceFactory.ModbusTarget> _targets;
   protected final Map<String, NordicCoffeeSourceFactory.ModbusControlTarget> _controls;
   protected final Map<String, NordicCoffeeSourceFactory.ModbusTarget> _enabledTargets = new LinkedHashMap<>();

   public NordicCoffeeSource(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      Map<String, NordicCoffeeSourceFactory.ModbusTarget> targets,
      Map<String, NordicCoffeeSourceFactory.ModbusControlTarget> controls
   ) {
      this(factory, ipAddress, 502, targets, controls);
   }

   public NordicCoffeeSource(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      int port,
      Map<String, NordicCoffeeSourceFactory.ModbusTarget> targets,
      Map<String, NordicCoffeeSourceFactory.ModbusControlTarget> controls
   ) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(800);
      this._targets = targets;
      this._controls = controls;
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
   public synchronized void configure(Collection<Channel> channels) {
      this._enabledTargets.clear();

      for (Channel channel : channels) {
         if (this._targets.containsKey(channel.getName())) {
            this._enabledTargets.put(channel.getName(), this._targets.get(channel.getName()));
         }
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
      Map<String, Double> values = new LinkedHashMap<>();
      if (!this.isReady()) {
         return values;
      } else {
         ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
         transaction.setRetries(1);

         for (NordicCoffeeSourceFactory.ModbusTarget target : this._enabledTargets.values()) {
            try {
               target.putValue(values, transaction);
            } catch (Exception var6) {
               this.logger.error("Error processing Modbus request for {}", target.getName(), var6);
               IOUtils.closeQuietly(this);
               this.open();
               if (!this.isReady()) {
                  this.fireStatusChanged();
                  this.logger.error("Failed to read {} value because connecting failed", target.getName(), var6);
                  return values;
               }
            }
         }

         return values;
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
            this.logger.info("Write {} value: {}", field, value);
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            NordicCoffeeSourceFactory.ModbusControlTarget target = this._controls.get(field);
            if (target == null) {
               throw new IllegalArgumentException("Field not supported: " + field);
            }

            int newValue = value.intValue();
            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(target.getRegister(), new SimpleRegister(newValue));
            request.setUnitID(target.getUnitId());
            transaction.setRequest(request);
            transaction.setRetries(2);
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
      NordicCoffeeSourceFactory.ModbusControlTarget target = this._controls.get(field);
      return target == null ? null : (double)target.getMin();
   }

   @Override
   public Double getMaxValue(String field) {
      NordicCoffeeSourceFactory.ModbusControlTarget target = this._controls.get(field);
      return target == null ? null : (double)target.getMax();
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._enabledTargets.keySet();
   }
}
