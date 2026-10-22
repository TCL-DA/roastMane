package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffedSerialSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(CoffedSerialSource.class);
   private static final int TRANS_DELAY = 50;
   private static final int READ_TIMEOUT = 300;
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private static final int BEAN_TEMPERATURE_UNIT_ID = 4;
   private static final int EXHAUST_TEMPERATURE_UNIT_ID = 3;
   protected final SerialConnection _connection;
   protected final Collection<Channel> _channels = new CopyOnWriteArrayList<>();
   private final Map<String, Integer> _unitIds = new HashMap<>();
   private final int _temperatureRegister;

   public CoffedSerialSource(MeasurementSourceFactory factory, SerialParameters parameters, CoffedSerialSourceFactory.CoffedManualVersion coffedManualVersion) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(300);
      this._temperatureRegister = coffedManualVersion.name().endsWith("DELTA") ? 4096 : 52;
      this._unitIds.put("exhaustTemperature", 3);
      this._unitIds.put("beanTemperature", 4);
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
      this._channels.clear();

      for (Channel channel : channels) {
         if (this._unitIds.containsKey(channel.getName())) {
            this._channels.add(channel.clone());
         }
      }
   }

   private Double getDoubleValue(int registerValue) {
      return (double)registerValue / 10.0;
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
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = this.getModbusTransaction();
         Map<String, Double> temperatureValues = new LinkedHashMap<>();

         for (Channel channel : this._channels) {
            try {
               ModbusRequest request = new ReadMultipleRegistersRequest(this._temperatureRegister, 1);
               request.setUnitID(this._unitIds.get(channel.getName()));
               request.setHeadless();
               transaction.setRequest(request);
               synchronized (this) {
                  transaction.execute();
               }

               ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
               temperatureValues.put(channel.getName(), this.getDoubleValue(response.getRegisterValue(0)));
            } catch (Exception var9) {
               this.logger.error("Error processing Modbus request reading {}", channel.getName(), var9);
               if (!this._connection.isOpen()) {
                  IOUtils.closeQuietly(this);
                  this.fireStatusChanged();
                  return temperatureValues;
               }
            }
         }

         return temperatureValues;
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close port");
      this._connection.close();
   }
}
