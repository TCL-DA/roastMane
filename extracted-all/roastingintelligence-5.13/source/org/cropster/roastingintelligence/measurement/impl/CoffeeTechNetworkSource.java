package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeTechNetworkSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CoffeeTechNetworkSource.class);
   private static final int UNIT_ID = 1;
   public static final int REGISTER_DRUM_TEMPERATURE = 0;
   public static final int REGISTER_BEAN_TEMPERATURE = 2;
   public static final int REGISTER_GAS_CONTROL = 6;
   public static final int REGISTER_DRUM_SPEED = 8;
   public static final int REGISTER_DRUM_SPEED_CONTROL = 8;
   public static final int REGISTER_FAN_SPEED = 10;
   public static final int REGISTER_FAN_SPEED_CONTROL = 10;
   public static final int REGISTER_GAS = 6;
   private static final int MODBUS_TCP_RETRIES = 1;
   private final TCPMasterConnection _connection;
   private Set<String> channelNames = Collections.emptySet();

   public CoffeeTechNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public CoffeeTechNetworkSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this.logger.info("Open connection to {}", this._connection.getAddress().getHostAddress());
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
      this.channelNames = channels.stream().map(Channel::getName).collect(Collectors.toUnmodifiableSet());
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

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new LinkedHashMap<>();

         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(0, 16);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("drumTemperature", this.getDoubleWord(response, 0));
            values.put("beanTemperature", this.getDoubleWord(response, 2));
            values.put("gas", this.getDoubleWord(response, 6));
            values.put("drumSpeed", this.getDoubleWord(response, 8));
            values.put("fanSpeed", this.getDoubleWord(response, 10));
            values.put("drumSpeedControl", this.getDoubleWord(response, 8));
            values.put("fanSpeedControl", this.getDoubleWord(response, 10));
            values.put("gasControl", this.getDoubleWord(response, 6));
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            IOUtils.closeQuietly(this);
            this.open();
         }

         return values;
      }
   }

   private void checkWordCount(ReadMultipleRegistersRequest request, ReadMultipleRegistersResponse response) throws ModbusException {
      if (request.getWordCount() != response.getWordCount()) {
         throw new ModbusException(
            String.format("Response/request word count not matching. (request: %d, response: %d)", request.getWordCount(), response.getWordCount())
         );
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
      this.fireStatusChanged();
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this.channelNames;
   }

   @Override
   public Double getMaxValue(String field) {
      switch (field) {
         case "gasControl":
            return 100.0;
         case "drumSpeedControl":
         case "fanSpeedControl":
            return 70.0;
         default:
            return null;
      }
   }

   @Override
   public Double getMinValue(String field) {
      switch (field) {
         case "gasControl":
            return 0.0;
         case "drumSpeedControl":
         case "fanSpeedControl":
            return 30.0;
         default:
            return null;
      }
   }

   @Override
   public void setValue(String field, Double value) {
      switch (field) {
         case "gasControl":
         case "drumSpeedControl":
         case "fanSpeedControl":
            if (Objects.isNull(value)) {
               throw new NullPointerException(MessageFormat.format("Null ''value'' parameter for ''{0}''", field));
            } else {
               double max = this.getMaxValue(field);
               double min = this.getMinValue(field);
               if (value > max) {
                  throw new IllegalArgumentException(MessageFormat.format("Value [{0}] of ''{1}'' is larger than the allowed maximum [{2}]", value, field, max));
               } else if (value < min) {
                  throw new IllegalArgumentException(
                     MessageFormat.format("Value [{0}] of ''{1}'' is smaller than the allowed minimum [{2}]", value, field, min)
                  );
               } else {
                  if (!this.isReady()) {
                     this.open();
                     if (!this.isReady()) {
                        this.logger.error("Failed to write {} value because connecting failed", field);
                        return;
                     }
                  }

                  try {
                     this.logger.debug("Write {} value: {}", field, value);
                     ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
                     transaction.setRetries(1);
                     Double validValue = this.getValidControlValue(field, value);
                     int intValueRepresentation = Float.floatToIntBits(validValue.floatValue());
                     int lowerValue = intValueRepresentation & 65535;
                     int higherValue = intValueRepresentation >> 16 & 65535;

                     WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(switch (field) {
                        case "gasControl" -> 6;
                        case "drumSpeedControl" -> 8;
                        default -> 10;
                     }, new Register[]{new SimpleRegister(lowerValue), new SimpleRegister(higherValue)});
                     request.setUnitID(1);
                     transaction.setRequest(request);
                     transaction.execute();
                     WriteMultipleRegistersResponse response = (WriteMultipleRegistersResponse)transaction.getResponse();
                     if (response.getWordCount() != 2) {
                        throw new IOException(String.format("Error writing %s value", field));
                     }
                  } catch (Exception var11) {
                     this.logger.error("Error writing to PLC", var11);
                  }

                  return;
               }
            }
         default:
            throw new IllegalArgumentException("Field not supported: " + field);
      }
   }

   @Override
   public Double getValidControlValue(String measurement, double value) {
      if (!"gasControl".equals(measurement)) {
         return value;
      } else {
         return value <= 1.0 ? 1.0 : value;
      }
   }
}
