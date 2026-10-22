package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeetoolPlcSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int GAS_REGISTER = 6;
   private static final int AIRFLOW_REGISTER = 8;
   private static final int DRUM_SPEED_REGISTER = 10;
   private final Logger logger = LoggerFactory.getLogger(CoffeetoolPlcSource.class);
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();

   public CoffeetoolPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public CoffeetoolPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
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
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   public Unit getUnit() {
      return Unit.CELSIUS;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getDoubleWord(ReadMultipleRegistersResponse response, int i) {
      int lower = response.getRegisterValue(i);
      int higher = response.getRegisterValue(i + 1);
      return (double)Float.intBitsToFloat(higher << 16 | lower);
   }

   private Register[] getWords(Double value) {
      int bitsAsInt = Float.floatToIntBits(value.floatValue());
      int hiWord = bitsAsInt >> 16;
      int loWord = bitsAsInt & 65535;
      return new SimpleRegister[]{new SimpleRegister(loWord), new SimpleRegister(hiWord)};
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new ReadMultipleRegistersRequest(0, 12);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("burnerTemperature", this.getDoubleWord(response, 0));
            values.put("exhaustTemperature", this.getDoubleWord(response, 2));
            values.put("beanTemperature", this.getDoubleWord(response, 4));
            Double gasValue = this.getDoubleWord(response, 6);
            if (gasValue != null && Double.isFinite(gasValue)) {
               values.put("gas", gasValue);
               values.put("gasControl", gasValue);
            }

            Double airflowValue = this.getDoubleWord(response, 8);
            if (airflowValue != null && Double.isFinite(airflowValue)) {
               values.put("airflow", airflowValue);
               values.put("airflowControl", airflowValue);
            }

            Double drumSpeedValue = this.getDoubleWord(response, 10);
            if (drumSpeedValue != null && Double.isFinite(drumSpeedValue)) {
               values.put("drumSpeed", drumSpeedValue);
               values.put("drumSpeedControl", drumSpeedValue);
            }

            return values;
         } catch (Exception var8) {
            this.logger.error("Error processing Modbus request", var8);
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
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         try {
            this.logger.info("Writing {} value: {}", field, value);

            int ex = switch (field) {
               case "airflowControl" -> 8;
               case "drumSpeedControl" -> 10;
               default -> 6;
            };
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            Register[] newGasValue = this.getWords(value);
            WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(ex, newGasValue);
            transaction.setRequest(request);
            transaction.execute();
            WriteMultipleRegistersResponse response = (WriteMultipleRegistersResponse)transaction.getResponse();
            if (response.getReference() != ex || response.getWordCount() != newGasValue.length) {
               throw new IOException(String.format("Error writing %s value", field));
            }
         } catch (Exception var8) {
            this.logger.error("Error writing to PLC", var8);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }
}
