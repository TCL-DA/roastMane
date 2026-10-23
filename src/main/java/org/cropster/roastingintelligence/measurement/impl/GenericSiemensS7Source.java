package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.util.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericSiemensS7Source extends AbstractSiemensS7Source {
   private final Logger logger = LoggerFactory.getLogger(GenericSiemensS7Source.class);
   private static final String[] REGISTERS = new String[]{"beanTemperature", "exhaustTemperature", "gas"};
   private final int _db;
   private final Storage _storage;
   private final Integer[] _registers;

   public GenericSiemensS7Source(
      MeasurementSourceFactory factory,
      InetAddress ipAddress,
      int slot,
      int db,
      Storage storage,
      int beanTempertureRegister,
      Integer exhaustTemperatureRegister,
      Integer gasRegister
   ) {
      super(factory, ipAddress, slot);
      this._db = db;
      this._storage = storage;
      this._registers = new Integer[]{beanTempertureRegister, exhaustTemperatureRegister, gasRegister};
   }

   @Override
   public Map<String, Double> getValues() {
      if (this.isReady()) {
         Map<String, Double> values = new HashMap<>();

         try {
            for (int i = 0; i < this._registers.length; i++) {
               if (this._registers[i] != null) {
                  byte[] buffer = new byte[4];
                  int errorCode = this._client.ReadArea(132, this._db, this._registers[i], buffer.length, buffer);
                  this.handleError(errorCode, buffer);
                  double value = this._storage.extractS7(buffer);
                  values.put(REGISTERS[i], value);
               }
            }
         } catch (Exception var7) {
            this.logger.error("Error reading from PLC DB {}", this._db, var7);
         }

         return values;
      } else {
         return Collections.emptyMap();
      }
   }
}
