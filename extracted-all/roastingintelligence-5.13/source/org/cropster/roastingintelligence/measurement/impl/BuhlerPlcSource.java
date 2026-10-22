package org.cropster.roastingintelligence.measurement.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.snap7.moka7.S7;
import net.sourceforge.snap7.moka7.S7Client;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuhlerPlcSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(BuhlerPlcSource.class);
   private static final int RACK = 0;
   private static final int SLOT = 1;
   private static final int DB = 1604;
   private static final int LENGTH = 28;
   private final InetAddress _ipAddress;
   private volatile S7Client _client;

   public BuhlerPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      super(factory);
      this._ipAddress = ipAddress;
   }

   @Override
   public synchronized void open() {
      try {
         String ip = this._ipAddress.getHostAddress();
         this._client = new S7Client();
         this._client.RecvTimeout = 300;
         int errorCode = this._client.ConnectTo(ip, 0, 1);
         if (errorCode == 0) {
            this.fireStatusChanged();
         } else {
            this.logger.error("Error connecting to Buhler PLC: {} - {}", errorCode, S7Client.ErrorText(errorCode));
         }
      } catch (Exception var3) {
         this.logger.error("Error opening PLC connection", var3);
      }
   }

   @Override
   public boolean isReady() {
      return this._client != null && this._client.Connected;
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (this.isReady()) {
         try {
            byte[] buffer = new byte[28];
            int errorCode = this._client.ReadArea(132, 1604, 8, 28, buffer);
            if (errorCode != 0) {
               this.logger.error("Error reading from Buhler PLC: {} - {}", errorCode, S7Client.ErrorText(errorCode));
               return Collections.emptyMap();
            }

            if (this.logger.isTraceEnabled()) {
               this.logger.trace(Arrays.toString(buffer));
            }

            Map<String, Double> values = new HashMap<>();
            float drumSpeed = S7.GetFloatAt(buffer, 0);
            values.put("drumSpeed", (double)drumSpeed);
            float burnerTemperature = S7.GetFloatAt(buffer, 4);
            values.put("burnerTemperature", (double)burnerTemperature);
            float beanTemperature = S7.GetFloatAt(buffer, 8);
            values.put("beanTemperature", (double)beanTemperature);
            float drumPressure = S7.GetFloatAt(buffer, 12);
            values.put("drumPressure", (double)drumPressure);
            float airflow = S7.GetFloatAt(buffer, 16);
            values.put("airflow", (double)airflow);
            float afterBurnerTemperature = S7.GetFloatAt(buffer, 24);
            values.put("afterburnerTemperature", (double)afterBurnerTemperature);
            return values;
         } catch (Exception var10) {
            this.logger.error("Error reading from PLC", var10);
         }
      }

      return Collections.emptyMap();
   }

   @Override
   public synchronized void close() throws IOException {
      this._client.Disconnect();
      this.fireStatusChanged();
   }
}
