package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BescaManualSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(BescaManualSource.class);
   private static final int OPEN_DELAY = 500;
   private static final int TRANS_DELAY = 150;
   private static final int READ_TIMEOUT = 800;
   private static final int INIT_TIMEOUT = 3000;
   private static final int RETRIES = 1;
   private Integer _beanTemperatureSlave;
   private Integer _beanTemperatureRegister;
   private Integer _exhaustTemperatureSlave;
   private Integer _exhaustTemperatureRegister;
   private final SerialConnection _connection;
   private final Collection<GenericModbusSource.ModbusConfig> _targets = new LinkedHashSet<>();
   private volatile boolean _initializing;

   public BescaManualSource(BescaManualSourceFactory factory, SerialParameters parameters, BescaManualSourceFactory.BescaManualVersion bescaManualVersion) {
      super(factory);
      parameters.setOpenDelay(500);
      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(800);
      switch (bescaManualVersion) {
         case MANUAL_V1:
            this._beanTemperatureRegister = 0;
            this._beanTemperatureSlave = 1;
            this._exhaustTemperatureRegister = 0;
            this._exhaustTemperatureSlave = 2;
            break;
         case BEE:
            this._beanTemperatureRegister = 0;
            this._beanTemperatureSlave = 1;
            this._exhaustTemperatureRegister = 10;
            this._exhaustTemperatureSlave = 1;
            break;
         default:
            throw new IllegalArgumentException("Error setting the Besca Manual version");
      }

      GenericModbusSource.ModbusConfig modbusConfigBean = new GenericModbusSource.ModbusConfig("beanTemperature", this._beanTemperatureRegister);
      modbusConfigBean.setUnitId(this._beanTemperatureSlave);
      this._targets.add(modbusConfigBean);
      GenericModbusSource.ModbusConfig modbusConfigExhaust = new GenericModbusSource.ModbusConfig("exhaustTemperature", this._exhaustTemperatureRegister);
      modbusConfigExhaust.setUnitId(this._exhaustTemperatureSlave);
      this._targets.add(modbusConfigExhaust);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open port {}", this._connection.getDescriptivePortName());
         this._connection.open();
         this.logger.info("Port open {}", this._connection.isOpen());
         Thread initThread = new Thread("BescaInit") {
            @Override
            public void run() {
               try {
                  BescaManualSource.this._initializing = true;
                  Thread.sleep(3000L);
                  BescaManualSource.this._initializing = false;
                  BescaManualSource.this.logger.info("Port initialized");
                  BescaManualSource.this.fireStatusChanged();
               } catch (Exception var2) {
                  BescaManualSource.this.logger.error("Error initializing", var2);
                  BescaManualSource.this._initializing = false;
                  IOUtils.closeQuietly(BescaManualSource.this);
                  BescaManualSource.this.fireStatusChanged();
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

         for (GenericModbusSource.ModbusConfig target : this._targets) {
            try {
               ModbusRequest request = new ReadInputRegistersRequest(target.getRegister(), 1);
               request.setUnitID(target.getUnitId());
               request.setHeadless();
               transaction.setRequest(request);
               transaction.execute();
               ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
               values.put(target.getName(), this.getDoubleValue(response.getRegisterValue(0)));
            } catch (Exception var7) {
               this.logger.error("Error processing Modbus request", var7);
               if (!this._connection.isOpen()) {
                  IOUtils.closeQuietly(this);
                  this.fireStatusChanged();
                  return Collections.emptyMap();
               }
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
