package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
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
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.AutoStartSource;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.AutoStartListener;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtillaV2Source extends AbstractMeasurementSource implements MeasurementReaderWriter, AutoStartSource, SettingsListener {
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int UNIT_ID = 1;
   private static final int OFFSET = 2000;
   private static final int OFFSET_2ND_REQUEST = 2220;
   private static final int GAS_REGISTER = 2100;
   private static final int AIRFLOW_REGISTER = 2102;
   private static final int DRUM_SPEED_REGISTER = 2101;
   private static final int GAS_CONTROL_REGISTER = 2220;
   private static final int DRUM_SPEED_CONTROL_REGISTER = 2240;
   private static final int AIRFLOW_CONTROL_REGISTER = 2260;
   private static final int CTRL_REGISTER = 1000;
   private final Logger logger = LoggerFactory.getLogger(AtillaV2Source.class);
   private final TCPMasterConnection _connection;
   private Set<String> _channelNames = Collections.emptySet();
   private AutoStartListener _roastingStartListener;
   private boolean _roasting = false;
   private boolean _autoStartEnabled = false;
   private boolean _flameCutting = false;

   public AtillaV2Source(MeasurementSourceFactory factory, InetAddress ipAddress, boolean flameCutting) {
      this(factory, ipAddress, 502, flameCutting);
   }

   public AtillaV2Source(MeasurementSourceFactory factory, InetAddress ipAddress, int port, boolean flameCutting) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
      this._flameCutting = flameCutting;
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
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
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

   private void checkWordCount(ReadMultipleRegistersRequest request, ReadMultipleRegistersResponse response) throws ModbusException {
      if (request.getWordCount() != response.getWordCount()) {
         throw new ModbusException(
            String.format("Response/request word count not matching. (request: %d, response: %d)", request.getWordCount(), response.getWordCount())
         );
      }
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
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(2000, 103);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("beanTemperature", this.getDoubleWord(response, 0));
            values.put("exhaustTemperature", this.getDoubleWord(response, 2));
            values.put("drumTemperature", this.getDoubleWord(response, 4));
            values.put("gas", (double)response.getRegisterValue(100));
            values.put("airflow", (double)response.getRegisterValue(102));
            values.put("drumSpeed", (double)response.getRegisterValue(101));
            request = new ReadMultipleRegistersRequest(2220, 41);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            values.put("gasControl", (double)response.getRegisterValue(0));
            values.put("airflowControl", (double)response.getRegisterValue(40));
            values.put("drumSpeedControl", (double)response.getRegisterValue(20));
            if (!this._autoStartEnabled) {
               return values;
            }

            request = new ReadMultipleRegistersRequest(1000, 1);
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
            response = (ReadMultipleRegistersResponse)transaction.getResponse();
            this.checkWordCount(request, response);
            boolean roastingBit = (response.getRegisterValue(0) & 1) == 1;
            this.logger.debug("Roasting: {}", roastingBit);
            if (roastingBit && !this._roasting) {
               this._roastingStartListener.autoStart();
            } else if (!roastingBit && this._roasting) {
               this._roastingStartListener.autoStop();
            }

            this._roasting = roastingBit;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request", var6);
            IOUtils.closeQuietly(this);
            this.open();
         }

         return values;
      }
   }

   @Override
   public void setValue(String field, Double value) {
      int register = switch (field) {
         case "gasControl" -> 2220;
         case "airflowControl" -> 2260;
         case "drumSpeedControl" -> 2240;
         default -> throw new IllegalArgumentException("Field not supported!");
      };
      if (!this.isReady()) {
         this.open();
         if (!this.isReady()) {
            this.logger.error("Failed to write {} value because connecting failed", field);
            return;
         }
      }

      synchronized (this) {
         try {
            this.logger.debug("Write {} value: {}", field, value);
            int newValue = this.getValidControlValue(field, value).intValue();
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            ModbusRequest request = new WriteMultipleRegistersRequest(register, new Register[]{new SimpleRegister(newValue)});
            request.setUnitID(1);
            transaction.setRequest(request);
            transaction.execute();
         } catch (Exception var9) {
            this.logger.error("Error writing to PLC", var9);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
      this.fireStatusChanged();
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
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   @Override
   public Double getValidControlValue(String measurement, double value) {
      if (!"gasControl".equals(measurement)) {
         return value;
      } else {
         return !this._flameCutting && value <= 1.0 ? 1.0 : value;
      }
   }

   @Override
   public void addRoastingStartListener(AutoStartListener listener) {
      this._roastingStartListener = listener;
   }

   @Override
   public void settingsChanged(AppModel appModel) {
      this._autoStartEnabled = AppModel.AutoStartMode.MACHINE_EVENT.equals(appModel.getAutoStartMode());
   }
}
