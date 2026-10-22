package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NordicCoffeeV2Source extends NordicCoffeeSource {
   private final Logger logger = LoggerFactory.getLogger(NordicCoffeeV2Source.class);
   private final Map<String, Double> _controlValues = new ConcurrentHashMap<>();

   public NordicCoffeeV2Source(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      int port,
      Map<String, NordicCoffeeSourceFactory.ModbusTarget> targets,
      Map<String, NordicCoffeeSourceFactory.ModbusControlTarget> controls
   ) {
      super(factory, ipAddress, port, targets, controls);

      for (NordicCoffeeSourceFactory.ModbusControlTarget control : controls.values()) {
         this._controlValues.put(control.getName(), (double)control.getMin());
      }
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      Map<String, Double> values = super.getValues();
      values.putAll(this._controlValues);
      return values;
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
            this._connection.setTimeout(100);
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            NordicCoffeeSourceFactory.ModbusControlTarget target = this._controls.get(field);
            if (target == null) {
               throw new IllegalArgumentException("Field not supported: " + field);
            }

            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(target.getRegister(), new SimpleRegister(value.intValue()));
            request.setUnitID(target.getUnitId());
            transaction.setRequest(request);
            transaction.execute();
         } catch (Exception var9) {
            if (!var9.getMessage().contains("Socket timeout reading response")) {
               this.logger.error("Error writing to PLC", var9);
            } else {
               this._controlValues.put(field, value);
            }
         } finally {
            this._connection.setTimeout(800);
         }
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      Set<String> configuredChannels = new LinkedHashSet<>(this._enabledTargets.keySet());
      configuredChannels.addAll(this._controls.keySet());
      return configuredChannels;
   }
}
