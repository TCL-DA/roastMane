package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyphoonSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(TyphoonSource.class);
   private static final int UNIT_ID = 1;
   private static final int READ_TIMEOUT = 800;
   private final int _beanTemperatureRegister;
   private final int _environmentalTemperatureRegister;
   private final int _gasRegister;
   private final int _minRegister;
   private final int _maxRegister;
   private static final int MODBUS_TCP_RETRIES = 1;
   private final TCPMasterConnection _connection;

   public TyphoonSource(
      MeasurementSourceFactory factory, InetAddress ipAddress, int beanTemperatureRegister, int environmentalTemperatureRegister, int gasRegister
   ) {
      this(factory, ipAddress, 502, beanTemperatureRegister, environmentalTemperatureRegister, gasRegister);
   }

   public TyphoonSource(
      MeasurementSourceFactory factory, InetAddress ipAddress, int port, int beanTemperatureRegister, int environmentalTemperatureRegister, int gasRegister
   ) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(800);
      this._beanTemperatureRegister = beanTemperatureRegister;
      this._environmentalTemperatureRegister = environmentalTemperatureRegister;
      this._gasRegister = gasRegister;
      this._minRegister = Math.min(this._beanTemperatureRegister, Math.min(this._environmentalTemperatureRegister, this._gasRegister));
      this._maxRegister = Math.max(this._beanTemperatureRegister, Math.max(this._environmentalTemperatureRegister, this._gasRegister));
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to {}", this._connection.getAddress());
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
   public void configure(Collection<Channel> channels) {
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
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new ReadMultipleRegistersRequest(this._minRegister, this._maxRegister - this._minRegister + 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getTemperature(response.getRegisterValue(this._beanTemperatureRegister - this._minRegister)));
            values.put("environmentalTemperature", this.getTemperature(response.getRegisterValue(this._environmentalTemperatureRegister - this._minRegister)));
            values.put("gas", (double)response.getRegisterValue(this._gasRegister - this._minRegister));
            return values;
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            if (!this._connection.isConnected()) {
               this._connection.close();
               this.fireStatusChanged();
            }

            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }
}
