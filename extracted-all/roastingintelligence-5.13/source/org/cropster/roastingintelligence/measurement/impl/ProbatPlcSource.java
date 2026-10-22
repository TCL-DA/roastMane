package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusUDPTransaction;
import com.ghgande.j2mod.modbus.msg.MaskWriteRegisterRequest;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.net.UDPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProbatPlcSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(ProbatPlcSource.class);
   private Set<String> _channelNames = Collections.emptySet();
   private final UDPMasterConnection _connection;
   private boolean _beckhoffV41;

   public ProbatPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) throws UnknownHostException {
      super(factory);
      this._connection = new UDPMasterConnection(ipAddress);
      this._connection.setTimeout(300);
   }

   @Override
   public synchronized void open() {
      try {
         this._connection.connect();
         this.fireStatusChanged();
         ModbusUDPTransaction transaction = new ModbusUDPTransaction(this._connection);
         ModbusRequest request = new ReadMultipleRegistersRequest(12442, 1);
         transaction.setRequest(request);
         transaction.execute();
         ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
         this.logger.info("Using Probat PLC version {}", this.determineVersion(response.getRegisterValue(0)));
         transaction.setRequest(new MaskWriteRegisterRequest(12288, 65532, 2));
         transaction.execute();
      } catch (Exception var4) {
         this.logger.error("Error opening Modbus connection to Probat PLC", var4);
      }
   }

   private String determineVersion(int version) {
      if (version >= 11) {
         this._beckhoffV41 = true;
         return version + " (Beckhoff v4.1 - compatible with Probatone 5, 12, 25 series with Beckhoff SPS)";
      } else {
         return version + " (Beckhoff v4.0 - compatible with Probatone 5, 12, 25 series with Beckhoff/Wago SPS)";
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isConnected();
   }

   @Override
   public void configure(Collection<Channel> channels) {
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getWord(ReadMultipleRegistersResponse response, int register, int precision) {
      double value = (double)response.getRegisterValue(register);
      value /= Math.pow(10.0, (double)precision);
      return MathUtil.roundHalfToEven(value, precision);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusUDPTransaction transaction = new ModbusUDPTransaction(this._connection);
            transaction.setRequest(new ReadMultipleRegistersRequest(12390, 1));
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            int watchdogValue = response.getRegisterValue(0);
            transaction.setRequest(new WriteSingleRegisterRequest(12289, new SimpleRegister(watchdogValue)));
            transaction.execute();
            ModbusRequest request = new ReadMultipleRegistersRequest(12391, this._beckhoffV41 ? 75 : 2);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getWord(response, 1, 2));
            values.put("gas", this.getWord(response, 0, 1));
            values.put("gasControl", this.getWord(response, 0, 1));
            if (this._beckhoffV41) {
               values.put("exhaustTemperature", this.getWord(response, 2, 2));
               values.put("drumSpeed", this.getWord(response, 71, 0));
               values.put("drumSpeedControl", this.getWord(response, 71, 0));
               values.put("airflow", this.getWord(response, 72, 0));
               values.put("airflowControl", this.getWord(response, 72, 0));
               values.put("drumPressure", this.getWord(response, 74, 1));
            }

            return values;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request to Probat PLC", var6);
            this._connection.close();
            this.fireStatusChanged();
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 0.0;
         case "airflowControl":
         case "drumSpeedControl":
            return 50.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
         case "airflowControl":
         case "drumSpeedControl":
            return 100.0;
         default:
            return null;
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
               this.logger.error("Failed to write {} because connecting failed", field);
               return;
            }
         }

         try {
            this.logger.info("Writing {} value: {}", field, value);
            ModbusUDPTransaction transaction = new ModbusUDPTransaction(this._connection);
            int factor = 1;
            int register;
            byte var9;
            switch (field) {
               case "airflowControl":
                  register = 12363;
                  var9 = 10;
                  break;
               case "drumSpeedControl":
                  register = 12362;
                  var9 = 10;
                  break;
               default:
                  register = 12290;
                  var9 = 100;
            }

            transaction.setRequest(new WriteSingleRegisterRequest(register, new SimpleRegister(value.intValue() * var9)));
            transaction.execute();
            if ("gasControl".equals(field)) {
               transaction.setRequest(new MaskWriteRegisterRequest(12318, 65531, 4));
               transaction.execute();
            }
         } catch (Exception var8) {
            this.logger.error("Error writing to PLC", var8);
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      try {
         ModbusUDPTransaction transaction = new ModbusUDPTransaction(this._connection);
         transaction.setRequest(new MaskWriteRegisterRequest(12288, 65532, 3));
         transaction.execute();
      } catch (Exception var2) {
         this.logger.error("Error writing to PLC", var2);
      }

      this._connection.close();
      this.fireStatusChanged();
   }
}
