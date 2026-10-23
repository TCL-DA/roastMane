package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.LinearValueConverter;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BescaAutoSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(BescaAutoSource.class);
   private static final int GAS_REGISTER = 3904;
   private static final int AIRFLOW_REGISTER = 1003;
   private static final int DRUM_SPEED_REGISTER = 1001;
   private Set<String> _channelNames = Collections.emptySet();
   private final TCPMasterConnection _connection;
   private final LinearValueConverter _gasConverter;
   private final LinearValueConverter _drumSpeedConverter;
   private final LinearValueConverter _fanSpeedConverter;
   private final Map<String, BescaAutoSource.Control> _controls = new HashMap<>(3);

   public BescaAutoSource(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      LinearValueConverter gasConverter,
      LinearValueConverter fanSpeedConverter,
      LinearValueConverter drumSpeedConverter
   ) {
      this(factory, ipAddress, 502, gasConverter, fanSpeedConverter, drumSpeedConverter);
   }

   public BescaAutoSource(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      int port,
      LinearValueConverter gasConverter,
      LinearValueConverter fanSpeedConverter,
      LinearValueConverter drumSpeedConverter
   ) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
      this._gasConverter = gasConverter;
      this._fanSpeedConverter = fanSpeedConverter;
      this._drumSpeedConverter = drumSpeedConverter;
      this._controls.put("gasControl", new BescaAutoSource.Control(this._gasConverter, 3904));
      this._controls.put("airflowControl", new BescaAutoSource.Control(this._fanSpeedConverter, 1003));
      this._controls.put("drumSpeedControl", new BescaAutoSource.Control(this._drumSpeedConverter, 1001));
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

   Double getTemperature(int value) {
      return (double)value / 10.0;
   }

   Double getSpeed(int value) {
      return (double)value / 10.0;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            ModbusRequest request = new ReadMultipleRegistersRequest(1001, 3);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            Double percent = MathUtil.roundHalfToEven(this._drumSpeedConverter.convertValueToPercent((double)response.getRegisterValue(0)));
            if (percent != null && Double.isFinite(percent)) {
               values.put("drumSpeed", percent);
               values.put("drumSpeedControl", percent);
            }

            percent = MathUtil.roundHalfToEven(this._fanSpeedConverter.convertValueToPercent((double)response.getRegisterValue(2)));
            if (percent != null && Double.isFinite(percent)) {
               values.put("airflow", percent);
               values.put("airflowControl", percent);
            }

            values.put("fanSpeed", this.getSpeed(response.getRegisterValue(2)));
            request = new ReadMultipleRegistersRequest(6, 2);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            values.put("beanTemperature", this.getTemperature(response.getRegisterValue(0)));
            values.put("exhaustTemperature", this.getTemperature(response.getRegisterValue(1)));
            request = new ReadMultipleRegistersRequest(3904, 1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            percent = MathUtil.roundHalfToEven(this._gasConverter.convertValueToPercent((double)response.getRegisterValue(0)));
            if (percent != null && Double.isFinite(percent)) {
               values.put("gas", percent);
               values.put("gasControl", percent);
            }

            return values;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request", var6);
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
         case "airflowControl":
         case "drumSpeedControl":
            return 0.0;
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
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            BescaAutoSource.Control control = this._controls.get(field);
            int newGasValue = (int)MathUtil.roundHalfToEven(control.getConverter().convertPercentToValue(value), 0);
            transaction.setRequest(new WriteSingleRegisterRequest(control.getRegister(), new SimpleRegister(newGasValue)));
            transaction.execute();
            WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
            if (response.getRegisterValue() != newGasValue) {
               throw new IOException(String.format("Error writing %s value", field));
            }
         } catch (Exception var7) {
            this.logger.error("Error writing to PLC", var7);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }

   private class Control {
      private final LinearValueConverter _converter;
      private final int _register;

      Control(LinearValueConverter converter, int register) {
         this._converter = converter;
         this._register = register;
      }

      public LinearValueConverter getConverter() {
         return this._converter;
      }

      public int getRegister() {
         return this._register;
      }
   }
}
