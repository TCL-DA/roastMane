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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BescaManualV2Source extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(BescaManualV2Source.class);
   private static final int OPEN_DELAY = 500;
   private static final int TRANS_DELAY = 150;
   private static final int READ_TIMEOUT = 800;
   private static final int INIT_TIMEOUT = 3000;
   private static final int RETRIES = 1;
   private static final int UNIT_ID = 1;
   private static final int BEAN_TEMPERATURE_REGISTER = 45;
   private final SerialConnection _connection;
   private volatile boolean _initializing;

   public BescaManualV2Source(BescaManualSourceFactory factory, SerialParameters parameters) {
      super(factory);
      parameters.setOpenDelay(500);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(800);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
         this.logger.info("Port open {}", this._connection.isOpen());
         Thread initThread = new Thread("BescaV2Init") {
            @Override
            public void run() {
               try {
                  BescaManualV2Source.this._initializing = true;
                  Thread.sleep(3000L);
                  BescaManualV2Source.this._initializing = false;
                  BescaManualV2Source.this.logger.info("Port initialized");
                  BescaManualV2Source.this.fireStatusChanged();
               } catch (Exception var2) {
                  BescaManualV2Source.this.logger.error("Error initializing", var2);
                  BescaManualV2Source.this._initializing = false;
                  IOUtils.closeQuietly(BescaManualV2Source.this);
                  BescaManualV2Source.this.fireStatusChanged();
               }
            }
         };
         initThread.start();
      } catch (Exception var2) {
         this.logger.error("Error opening serial Modbus RTU connection", var2);
         this._initializing = false;
         IOUtils.closeQuietly(this);
         this.fireStatusChanged();
      }
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen() && !this._initializing;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   Double getDoubleValue(int value) {
      return (double)value / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(1);
         transaction.setTransDelayMS(150);
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusRequest request = new ReadMultipleRegistersRequest(45, 2);
            request.setUnitID(1);
            request.setHeadless();
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put("beanTemperature", this.getDoubleValue(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getDoubleValue(response.getRegisterValue(1)));
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            if (!this._connection.isOpen()) {
               IOUtils.closeQuietly(this);
               this.fireStatusChanged();
               return Collections.emptyMap();
            }
         }

         return values;
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close port {}", this._connection.getDescriptivePortName());
      this._connection.close();
   }
}
