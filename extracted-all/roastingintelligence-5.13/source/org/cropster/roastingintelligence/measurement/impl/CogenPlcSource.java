package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
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

public class CogenPlcSource extends AbstractMeasurementSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CogenPlcSource.class);
   private static final int GAS_REGISTER = 168;
   private static final int AIRFLOW_REGISTER = 117;
   private static final int DRUM_SPEED_REGISTER = 114;
   private Set<String> _channelNames = Collections.emptySet();
   private final TCPMasterConnection _connection;

   public CogenPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public CogenPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
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

   private double getDoubleWord(ReadInputRegistersResponse response, int i) {
      int lower = response.getRegisterValue(i);
      int higher = response.getRegisterValue(i + 1);
      return (double)Float.intBitsToFloat(higher << 16 | lower);
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
            ModbusRequest request = new ReadInputRegistersRequest(0, 4);
            transaction.setRequest(request);
            transaction.execute();
            ReadInputRegistersResponse responseInputRegisters = (ReadInputRegistersResponse)transaction.getResponse();
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("beanTemperature", this.getDoubleWord(responseInputRegisters, 0));
            values.put("drumTemperature", this.getDoubleWord(responseInputRegisters, 2));
            ReadMultipleRegistersRequest requestHoldingRegisters = new ReadMultipleRegistersRequest(108, 61);
            transaction.setRequest(requestHoldingRegisters);
            transaction.execute();
            ReadMultipleRegistersResponse responseHoldingRegisters = (ReadMultipleRegistersResponse)transaction.getResponse();
            double gas = (double)responseHoldingRegisters.getRegisterValue(168 - requestHoldingRegisters.getReference());
            values.put("gas", gas);
            values.put("gasControl", gas);
            double airflow = this.getDoubleWord(responseHoldingRegisters, 117 - requestHoldingRegisters.getReference());
            values.put("airflow", airflow);
            values.put("airflowControl", airflow);
            double drumSpeed = this.getDoubleWord(responseHoldingRegisters, 114 - requestHoldingRegisters.getReference());
            values.put("drumSpeed", drumSpeed);
            values.put("drumSpeedControl", drumSpeed);
            values.put("drumPressure", this.getDoubleWord(responseHoldingRegisters, 0));
            return values;
         } catch (Exception var13) {
            this.logger.error("Error processing Modbus request", var13);
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
            return 10.0;
         case "airflowControl":
            return 100.0;
         case "drumSpeedControl":
            return 68.0;
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
            if ("gasControl".equals(field)) {
               int newValue = value.intValue();
               transaction.setRequest(new WriteSingleRegisterRequest(168, new SimpleRegister(newValue)));
               transaction.execute();
               WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
               if (response.getRegisterValue() != newValue) {
                  throw new IOException(String.format("Error writing %s value", field));
               }
            } else {
               int register = "airflowControl".equals(field) ? 117 : 114;
               Register[] newValue = this.getWords(value);
               WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(register, newValue);
               transaction.setRequest(request);
               transaction.execute();
               WriteMultipleRegistersResponse response = (WriteMultipleRegistersResponse)transaction.getResponse();
               if (response.getReference() != register || response.getWordCount() != newValue.length) {
                  throw new IOException(String.format("Error writing %s value", field));
               }
            }
         } catch (Exception var8) {
            this.logger.error("Error writing to PLC", var8);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
