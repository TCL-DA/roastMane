package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.snap7.moka7.S7;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetronciniS7DbSource extends AbstractSiemensS7Source {
   private final Logger logger = LoggerFactory.getLogger(PetronciniS7DbSource.class);
   private static final int SLOT = 1;
   private static final int DB = 31;
   private static final int BEAN_TEMPERATURE_OFFSET = 0;
   private static final int BURNER_TEMPERATURE_OFFSET = 4;
   private static final int EXHAUST_TEMPERATURE_OFFSET = 8;
   private static final int GAS_OFFSET = 32;
   private static final int AIRFLOW_OFFSET = 34;
   private static final int DRUM_SPEED_OFFSET = 36;

   public PetronciniS7DbSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      super(factory, ipAddress, 1);
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new HashMap<>();

         try {
            byte[] buffer = new byte[38];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 31, 0, buffer.length, buffer);
            }

            this.handleError(errorCode, buffer);
            float beanTemperature = S7.GetFloatAt(buffer, 0);
            values.put("beanTemperature", (double)beanTemperature);
            float burnerTemperature = S7.GetFloatAt(buffer, 4);
            values.put("burnerTemperature", (double)burnerTemperature);
            float exhaustTemperature = S7.GetFloatAt(buffer, 8);
            values.put("exhaustTemperature", (double)exhaustTemperature);
            int gas = S7.GetWordAt(buffer, 32);
            values.put("gas", (double)gas);
            int airflow = S7.GetWordAt(buffer, 34);
            values.put("airflow", (double)airflow);
            int drumSpeed = S7.GetWordAt(buffer, 36);
            values.put("drumSpeed", (double)drumSpeed);
         } catch (Exception var11) {
            this.logger.error("Error reading from PLC DB {}", 31, var11);
         }

         return values;
      }
   }
}
