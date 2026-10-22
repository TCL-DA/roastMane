package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.AutoStartSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.AutoStartListener;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoringPlcSource extends AbstractMeasurementSource implements AutoStartSource, SettingsListener {
   private final Logger logger = LoggerFactory.getLogger(LoringPlcSource.class);
   private static final int MODBUS_TCP_RETRIES = 1;
   private static final int ROASTING_BIT_V2 = 284;
   private final TCPMasterConnection _connection;
   private AutoStartListener _roastingStartListener;
   private Boolean _roastingMode = null;
   private boolean _autoStartEnabled = false;

   public LoringPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 502);
   }

   public LoringPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port) {
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
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private int decodeBcd(int bcd) {
      int shift = 0;
      int factor = 1;
      int decimal = 0;

      for (int i = 0; i < 8; i++) {
         int bcdDigit = bcd >>> shift & 15;
         if (bcdDigit > 9) {
            throw new IllegalArgumentException(String.format("Invalid BCD digit %d: 0x%s", i, StringUtils.leftPad(Integer.toHexString(bcd), 8, "0")));
         }

         decimal += bcdDigit * factor;
         shift += 4;
         factor *= 10;
      }

      return decimal;
   }

   private int getWord(ReadMultipleRegistersResponse response, int i) {
      return this.decodeBcd(response.getRegisterValue(i));
   }

   private int getDoubleWord(ReadMultipleRegistersResponse response, int i) {
      int lower = response.getRegisterValue(i);
      int higher = response.getRegisterValue(i + 1);
      return this.decodeBcd(higher << 16 | lower);
   }

   private boolean getRoastingModeV2(ModbusTCPTransaction transaction) throws ModbusException {
      ReadInputDiscretesRequest request = new ReadInputDiscretesRequest(284, 1);
      transaction.setRequest(request);
      transaction.execute();
      ReadInputDiscretesResponse response = (ReadInputDiscretesResponse)transaction.getResponse();
      if (this.logger.isDebugEnabled()) {
         this.logger.debug("Roasting Bit V2: {}", response.getDiscretes());
      }

      return response.getDiscreteStatus(0);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            transaction.setRetries(1);
            if (this._autoStartEnabled) {
               Boolean roastingBit = this._roastingMode;

               try {
                  roastingBit = this.getRoastingModeV2(transaction);
                  if (this._roastingMode != null && roastingBit && !this._roastingMode) {
                     this._roastingStartListener.autoStart();
                  } else if (this._roastingMode != null && !roastingBit && this._roastingMode) {
                     this._roastingStartListener.autoStop();
                  }

                  this._roastingMode = roastingBit;
               } catch (ModbusException var5) {
                  this.logger.error("Error reading roasting bit", var5);
               }
            }

            ModbusRequest request = new ReadMultipleRegistersRequest(768, 13);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> temperatureValues = new LinkedHashMap<>();
            temperatureValues.put("beanTemperature", (double)this.getDoubleWord(response, 0) / 10.0);
            temperatureValues.put("inletTemperature", (double)this.getDoubleWord(response, 2) / 10.0);
            temperatureValues.put("stackTemperature", (double)this.getDoubleWord(response, 4) / 10.0);
            temperatureValues.put("returnTemperature", (double)this.getDoubleWord(response, 6) / 10.0);
            temperatureValues.put("gas", (double)this.getWord(response, 10));
            return temperatureValues;
         } catch (Exception var6) {
            this.logger.error("Error processing Modbus request", var6);
            this._connection.close();
            this.fireStatusChanged();
            this._roastingMode = null;
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
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
