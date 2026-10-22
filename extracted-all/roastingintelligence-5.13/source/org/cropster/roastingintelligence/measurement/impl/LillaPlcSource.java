package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.snap7.moka7.S7;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LillaPlcSource extends AbstractSiemensS7Source {
   private final Logger logger = LoggerFactory.getLogger(LillaPlcSource.class);
   private static final int SLOT = 0;
   private static final int DB = 100;
   private static final int LENGTH = 16;

   public LillaPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress) {
      super(factory, ipAddress, 0);
   }

   @Override
   public Map<String, Double> getValues() {
      if (this.isReady()) {
         Map<String, Double> values = new HashMap<>();

         try {
            byte[] buffer = new byte[16];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(132, 100, 0, 16, buffer);
            }

            this.handleError(errorCode, buffer);
            if (errorCode != 0) {
               return values;
            }

            float airTemperature = S7.GetFloatAt(buffer, 8);
            values.put("exhaustTemperature", (double)airTemperature);
            float beanTemperature = S7.GetFloatAt(buffer, 0);
            values.put("beanTemperature", (double)beanTemperature);
            float burnerTemperature = S7.GetFloatAt(buffer, 4);
            values.put("burnerTemperature", (double)burnerTemperature);
            float stackTemperature = S7.GetFloatAt(buffer, 12);
            values.put("stackTemperature", (double)stackTemperature);
         } catch (Exception var9) {
            this.logger.error("Error reading from Lilla PLC DB {}", 100, var9);
         }

         return values;
      } else {
         return Collections.emptyMap();
      }
   }
}
