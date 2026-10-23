package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MillCityV5Source extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(MillCityV5Source.class);
   private static final int MODBUS_RETRIES = 3;
   private static final int TRANS_DELAY = 50;
   private static final int BEAN_ENVIRONMENTAL_TEMP_SLAVE_ID = 1;
   private static final int BEAN_TEMP_REGISTER = 0;
   private static final int ENVIRONMENTAL_TEMP_REGISTER = 3;
   private static final int AIRFLOW_SLAVE_ID = 6;
   private static final int AIRFLOW_REGISTER = 8451;
   private static final int DRUM_TEMP_SLAVE_ID = 5;
   private static final int DRUM_TEMP_REGISTER_FOTEK = 257;
   private static final int DRUM_TEMP_REGISTER_DELTA = 18176;
   private static final int CONNECTION_RETRIES = 5;
   private boolean _airflowConfigured = false;
   private boolean _drumTempConfigured = false;
   private boolean _drumTempEnabled = true;
   private boolean _drumTempConnected = false;
   private int _drumTempRetries = 5;
   private int _drumTrempRegister = 257;
   private final SerialConnection _connection;
   private final Unit _temperatureUnit;

   public MillCityV5Source(MeasurementSourceFactory factory, SerialParameters parameters, MillCitySourceFactory.MillCityVersion version, Unit temperatureUnit) {
      super(factory);
      if (MillCitySourceFactory.MillCityVersion.VERSION5.equals(version)) {
         this._drumTrempRegister = 18176;
      } else {
         if (!MillCitySourceFactory.MillCityVersion.VERSION5B.equals(version)) {
            throw new IllegalArgumentException("Invalid machine version");
         }

         this._drumTrempRegister = 257;
      }

      this._connection = new SerialConnection(parameters);
      this._connection.setTimeout(300);
      this._temperatureUnit = temperatureUnit;
      if (this._temperatureUnit == null) {
         throw new IllegalArgumentException("Temperature unit required");
      }
   }

   @Override
   public void open() {
      try {
         this._drumTempEnabled = true;
         this._drumTempConnected = false;
         this._drumTempRetries = 5;
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
      this._airflowConfigured = false;
      this._drumTempConfigured = false;

      for (Channel channel : channels) {
         if ("airflow".equals(channel.getName())) {
            this._airflowConfigured = true;
         } else if ("drumTemperature".equals(channel.getName())) {
            this._drumTempConfigured = true;
            this._drumTempEnabled = true;
            this._drumTempConnected = false;
            this._drumTempRetries = 5;
         }
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private Double getTemperatureValue(int value) {
      return (double)value / 10.0;
   }

   private Double getAirflowValue(int value) {
      return (double)value / 100.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         transaction.setRetries(3);
         transaction.setTransDelayMS(50);
         Map<String, Double> values = new LinkedHashMap<>();
         ModbusRequest request = null;
         ReadMultipleRegistersResponse response = null;

         try {
            ModbusRequest var9 = new ReadMultipleRegistersRequest(0, 4);
            var9.setUnitID(1);
            var9.setHeadless();
            transaction.setRequest(var9);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put(
               "beanTemperature",
               this._temperatureUnit.from(BigDecimal.valueOf(this.getTemperatureValue(response.getRegisterValue(0))), Unit.CELSIUS).doubleValue()
            );
            values.put(
               "environmentalTemperature",
               this._temperatureUnit.from(BigDecimal.valueOf(this.getTemperatureValue(response.getRegisterValue(3))), Unit.CELSIUS).doubleValue()
            );
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
            if (!this._connection.isOpen()) {
               IOUtils.closeQuietly(this);
               this.fireStatusChanged();
               return Collections.emptyMap();
            }
         }

         if (this._drumTempConfigured && this._drumTempEnabled) {
            try {
               ModbusRequest var10 = new ReadMultipleRegistersRequest(this._drumTrempRegister, 1);
               var10.setUnitID(5);
               var10.setHeadless();
               transaction.setRequest(var10);
               transaction.execute();
               response = (ReadMultipleRegistersResponse)transaction.getResponse();
               this._drumTempConnected = true;
               values.put("drumTemperature", this.getTemperatureValue(response.getRegisterValue(0)));
            } catch (Exception var7) {
               this.logger.error("Error processing Modbus request (optional drum temp)");
               if (!this._drumTempConnected) {
                  this._drumTempRetries--;
                  if (this._drumTempRetries <= 0) {
                     this._drumTempEnabled = false;
                     this.logger.info("Disabling optional drum temp");
                  }
               }

               if (!this._connection.isOpen()) {
                  IOUtils.closeQuietly(this);
                  this.fireStatusChanged();
                  return Collections.emptyMap();
               }
            }
         }

         if (!this._airflowConfigured) {
            return values;
         } else {
            try {
               ModbusRequest var11 = new ReadMultipleRegistersRequest(8451, 1);
               var11.setUnitID(6);
               var11.setHeadless();
               transaction.setRequest(var11);
               transaction.execute();
               response = (ReadMultipleRegistersResponse)transaction.getResponse();
               values.put("airflow", this.getAirflowValue(response.getRegisterValue(0)));
            } catch (Exception var6) {
               this.logger.error("Error processing Modbus request", var6);
               if (!this._connection.isOpen()) {
                  IOUtils.closeQuietly(this);
                  this.fireStatusChanged();
                  return Collections.emptyMap();
               }
            }

            return values;
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close port");
      this._connection.close();
   }
}
