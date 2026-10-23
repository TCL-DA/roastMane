package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
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
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DongyiSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(DongyiSource.class);
   private static final int UNIT_ID = 1;
   private static final int READ_TIMEOUT = 800;
   private final SerialConnection _connection;
   private int _beanTemperatureRegister = 4;
   private boolean _bigEndian = false;
   private double _decimalCorrection = 1.0;

   public DongyiSource(MeasurementSourceFactory factory, SerialParameters parameters, int beanTemperatureRegister, boolean bigEndian, double decimalCorrection) {
      super(factory);
      this._connection = new SerialConnection(parameters);
      this._beanTemperatureRegister = beanTemperatureRegister;
      this._bigEndian = bigEndian;
      this._decimalCorrection = decimalCorrection;
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection");
         this._connection.open();
         this._connection.setTimeout(800);
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening serial Modbus connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getValue(ReadMultipleRegistersResponse response, int i, boolean bigEndian) {
      int higher = response.getRegisterValue(i + (bigEndian ? 0 : 1));
      int lower = response.getRegisterValue(i + (bigEndian ? 1 : 0));
      int bits = higher << 16 | lower;
      return (double)Float.intBitsToFloat(bits);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(this._beanTemperatureRegister, 4);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.setRetries(0);
            transaction.setTransDelayMS(50);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put("beanTemperature", this.getValue(response, 0, this._bigEndian) * this._decimalCorrection);
            Double exhaustTemperature = this.getValue(response, 2, this._bigEndian) * this._decimalCorrection;
            values.put("exhaustTemperature", exhaustTemperature);
            values.put("environmentalTemperature", exhaustTemperature);
            return values;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request", var6);
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Closing connection");
      this._connection.close();
   }
}
