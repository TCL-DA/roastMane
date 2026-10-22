package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.snap7.moka7.S7;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetronciniS7MkSource extends AbstractSiemensS7Source {
   private final Logger logger = LoggerFactory.getLogger(PetronciniS7MkSource.class);
   private static final int SLOT = 1;
   private final int _offset;
   private final int _beanTempOffset;
   private final int _exhaustTempOffset;
   private final int _burnerTempOffset;

   public PetronciniS7MkSource(
      MeasurementSourceFactory factory, InetAddress ipAddress, int offset, int beanTempOffset, int exhaustTempOffset, int burnerTempOffset
   ) {
      super(factory, ipAddress, 1);
      this._offset = offset;
      this._beanTempOffset = beanTempOffset;
      this._exhaustTempOffset = exhaustTempOffset;
      this._burnerTempOffset = burnerTempOffset;
   }

   @Override
   public synchronized Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         Map<String, Double> values = new HashMap<>();

         try {
            byte[] buffer = new byte[6];
            int errorCode;
            synchronized (this) {
               errorCode = this._client.ReadArea(131, 0, this._offset, buffer.length, buffer);
            }

            this.handleError(errorCode, buffer);
            int beanTemperature = S7.GetWordAt(buffer, this._beanTempOffset);
            values.put("beanTemperature", (double)beanTemperature);
            int exhaustTemperature = S7.GetWordAt(buffer, this._exhaustTempOffset);
            values.put("exhaustTemperature", (double)exhaustTemperature);
            int burnerTemperature = S7.GetWordAt(buffer, this._burnerTempOffset);
            values.put("burnerTemperature", (double)burnerTemperature);
         } catch (Exception var8) {
            this.logger.error("Error reading from PLC", var8);
         }

         return values;
      }
   }
}
