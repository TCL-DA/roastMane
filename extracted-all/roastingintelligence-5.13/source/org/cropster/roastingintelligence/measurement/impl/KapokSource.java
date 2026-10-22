package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KapokSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(KapokSource.class);
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private static final int TRANS_DELAY = 50;
   private static final int READ_TIMEOUT = 500;
   private static final int TEMPERATURE_REGISTER = 18176;
   private final SerialConnection _connection;
   private final List<KapokSourceFactory.ControllerUnit> _channels = new ArrayList<>();
   private final Map<String, KapokSourceFactory.ControllerUnit> _unitIds;

   public KapokSource(MeasurementSourceFactory factory, SerialParameters parameters, Map<String, KapokSourceFactory.ControllerUnit> unitIds) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(500);
      this._unitIds = unitIds;
   }

   @Override
   public synchronized void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
         this.fireStatusChanged();

         for (KapokSourceFactory.ControllerUnit unit : this._unitIds.values()) {
            unit.setEnabled(true);
            unit.resetRetriesBeforeDisable();
         }
      } catch (Exception var3) {
         this._connection.close();
         this.logger.error("Error opening serial Modbus RTU connection", var3);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      this._channels.clear();

      for (Channel channel : channels) {
         KapokSourceFactory.ControllerUnit unit = this._unitIds.get(channel.getName());
         if (unit != null) {
            this._channels.add(unit);
         }
      }

      if (this._channels.size() > 0) {
         this._connection.setTimeout(900 / this._channels.size());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private Double getDoubleValue(int value) {
      return (double)value / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(1);
         transaction.setTransDelayMS(50);
         Map<String, Double> temperatureValues = new LinkedHashMap<>();

         for (KapokSourceFactory.ControllerUnit unit : this._channels) {
            if (!unit.isDisableOnError() || unit.isEnabled()) {
               try {
                  ModbusRequest request = new ReadMultipleRegistersRequest(18176, 1);
                  request.setUnitID(unit.getUnitID());
                  request.setHeadless();
                  transaction.setRequest(request);
                  transaction.execute();
                  unit.resetRetriesBeforeDisable();
                  ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
                  temperatureValues.put(unit.getName(), this.getDoubleValue(response.getRegisterValue(0)));
               } catch (Exception var7) {
                  this.logger.error("Error processing Modbus request (unit {})", unit.getUnitID(), var7);
                  if (!this._connection.isOpen()) {
                     this.fireStatusChanged();
                  }

                  if (unit.isDisableOnError()) {
                     unit.setRetriesBeforeDisable(unit.getRetriesBeforeDisable() - 1);
                     if (!unit.isEnabled()) {
                        this.logger.info("Disabling unit {} after Modbus read error", unit.getUnitID(), var7);
                     }
                  }
               }
            }
         }

         if (temperatureValues.isEmpty()) {
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
         }

         return temperatureValues;
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close port {}", this._connection.getDescriptivePortName());
      this._connection.close();
   }
}
