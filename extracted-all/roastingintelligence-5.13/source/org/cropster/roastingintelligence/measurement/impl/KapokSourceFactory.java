package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class KapokSourceFactory extends SerialSourceFactory {
   private static final String PARAM_SLAVE_BEAN = "beanTemperatureSlave";
   private static final String PARAM_SLAVE_EXHAUST = "exhaustTemperatureSlave";
   private static final String PARAM_SLAVE_INLET = "inletTemperatureSlave";
   private static final String[] SLAVE_ID_OPTIONS = new String[]{"1", "2", "3"};
   private static final String[] INLET_SLAVE_ID_OPTIONS = new String[]{null, "1", "2", "3"};
   private Integer _beanTemperatureSlave = 1;
   private Integer _exhaustTemperatureSlave = 2;
   private Integer _inletTemperatureSlave = null;

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"exhaustTemperature".equals(measureName) && !"inletTemperature".equals(measureName)
         ? null
         : Unit.CELSIUS;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter beanSlaveIdParameter = new SourceParameter(
         "beanTemperatureSlave", String.class, this._beanTemperatureSlave != null ? this._beanTemperatureSlave.toString() : null
      );
      beanSlaveIdParameter.setOptions(SLAVE_ID_OPTIONS);
      parameters.add(beanSlaveIdParameter);
      SourceParameter exhaustSlaveIdParameter = new SourceParameter(
         "exhaustTemperatureSlave", String.class, this._exhaustTemperatureSlave != null ? this._exhaustTemperatureSlave.toString() : null
      );
      exhaustSlaveIdParameter.setOptions(SLAVE_ID_OPTIONS);
      parameters.add(exhaustSlaveIdParameter);
      SourceParameter inletSlaveIdParameter = new SourceParameter(
         "inletTemperatureSlave", String.class, this._inletTemperatureSlave != null ? this._inletTemperatureSlave.toString() : null
      );
      inletSlaveIdParameter.setOptions(INLET_SLAVE_ID_OPTIONS);
      parameters.add(inletSlaveIdParameter);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Integer beanTemperatureSlave = null;
      Integer exhaustTemperatureSlave = null;
      Integer inletTemperatureSlave = null;

      for (SourceParameter parameter : parameters) {
         if ("beanTemperatureSlave".equals(parameter.getName())) {
            beanTemperatureSlave = StringUtils.isNotBlank(parameter.getValue()) ? Integer.parseInt(parameter.getValue()) : null;
         } else if ("exhaustTemperatureSlave".equals(parameter.getName())) {
            exhaustTemperatureSlave = StringUtils.isNotBlank(parameter.getValue()) ? Integer.parseInt(parameter.getValue()) : null;
         } else if ("inletTemperatureSlave".equals(parameter.getName())) {
            inletTemperatureSlave = StringUtils.isNotBlank(parameter.getValue()) ? Integer.parseInt(parameter.getValue()) : null;
         }
      }

      if (beanTemperatureSlave == null) {
         throw new IllegalArgumentException("Bean temperature Slave ID must be specified");
      } else {
         Set<Integer> hashCodes = new HashSet<>();

         for (Integer slaveId : new Integer[]{beanTemperatureSlave, exhaustTemperatureSlave, inletTemperatureSlave}) {
            if (slaveId != null && (slaveId < 1 || slaveId > 3)) {
               throw new IllegalArgumentException("Specify a Slave ID between 1 and 3");
            }

            if (hashCodes.contains(slaveId)) {
               throw new IllegalArgumentException("Duplicate Slave IDs");
            }

            if (slaveId != null) {
               hashCodes.add(slaveId);
            }
         }

         this._beanTemperatureSlave = beanTemperatureSlave;
         this._exhaustTemperatureSlave = exhaustTemperatureSlave;
         this._inletTemperatureSlave = inletTemperatureSlave;
      }
   }

   protected SerialParameters getSerialParameters() {
      SerialParameters parameters = new SerialParameters();
      parameters.setPortName(this._serialPort);
      parameters.setBaudRate(9600);
      parameters.setDatabits(8);
      parameters.setParity(0);
      parameters.setStopbits(1);
      parameters.setEncoding("ascii");
      parameters.setEcho(false);
      return parameters;
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         Map<String, KapokSourceFactory.ControllerUnit> unit = new HashMap<>();
         if (this._beanTemperatureSlave != null) {
            unit.put("beanTemperature", new KapokSourceFactory.ControllerUnit("beanTemperature", this._beanTemperatureSlave, false));
         }

         if (this._exhaustTemperatureSlave != null) {
            unit.put("exhaustTemperature", new KapokSourceFactory.ControllerUnit("exhaustTemperature", this._exhaustTemperatureSlave, false));
         }

         if (this._inletTemperatureSlave != null) {
            unit.put("inletTemperature", new KapokSourceFactory.ControllerUnit("inletTemperature", this._inletTemperatureSlave, true));
         }

         return new MeasurementSource[]{new KapokSource(this, this.getSerialParameters(), unit)};
      } catch (Exception var3) {
         throw new IOException("Error initializing " + KapokSource.class.getSimpleName(), var3);
      }
   }

   class ControllerUnit {
      private String _name;
      private int _unitId;
      private int _maxRetriesBeforeDisable = 5;
      private int _retriesBeforeDisable = this._maxRetriesBeforeDisable;
      private boolean _enabled = false;
      private boolean _disableOnError = false;

      ControllerUnit(String name, int unitID) {
         this(name, unitID, false);
      }

      ControllerUnit(String name, int unitID, boolean disableOnError) {
         this._name = name;
         this._unitId = unitID;
         this._disableOnError = disableOnError;
      }

      public String getName() {
         return this._name;
      }

      public void setName(String name) {
         this._name = name;
      }

      public int getUnitID() {
         return this._unitId;
      }

      public void setUnitID(int unitID) {
         this._unitId = unitID;
      }

      public int getRetriesBeforeDisable() {
         return this._retriesBeforeDisable;
      }

      public void setRetriesBeforeDisable(int retriesBeforeDisable) {
         this._retriesBeforeDisable = retriesBeforeDisable;
         if (this._disableOnError && retriesBeforeDisable <= 0) {
            this.setEnabled(false);
         }
      }

      public boolean isEnabled() {
         return this._enabled;
      }

      public void setEnabled(boolean enabled) {
         this._enabled = enabled;
      }

      public int getMaxRetriesBeforeDisable() {
         return this._maxRetriesBeforeDisable;
      }

      public void setMaxRetriesBeforeDisable(int maxRetriesBeforeDisable) {
         this._maxRetriesBeforeDisable = maxRetriesBeforeDisable;
      }

      public void resetRetriesBeforeDisable() {
         this._retriesBeforeDisable = this._maxRetriesBeforeDisable;
      }

      public boolean isDisableOnError() {
         return this._disableOnError;
      }

      public void setDisableOnError(boolean disableOnError) {
         this._disableOnError = disableOnError;
      }
   }
}
