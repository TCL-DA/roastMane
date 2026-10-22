package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.cropster.roastingintelligence.util.Storage;

@NotMutuallyExclusiveSource
public class GenericSiemensS7SourceFactory extends NetworkSourceFactory {
   public static final String[] STORAGE_OPTIONS = new String[]{
      Storage.FLOAT.name(), Storage.INTEGER.name(), Storage.INTEGER_16.name(), Storage.DECIMAL1.name(), Storage.DECIMAL1_16.name()
   };
   private static final String PARAM_SLOT = "slot";
   private static final String PARAM_DB = "database";
   private static final String PARAM_STORAGE_FORMAT = "storageFormat";
   private static final String PARAM_REGISTER_BEAN = "beanTemperatureRegister";
   private static final String PARAM_REGISTER_EXHAUST = "exhaustTemperatureRegister";
   private static final String PARAM_REGISTER_GAS = "gasRegister";
   protected Integer _slot = 0;
   protected Integer _database;
   private Storage _storage = Storage.FLOAT;
   protected Integer _beanTemperatureRegister;
   protected Integer _exhaustTemperatureRegister;
   protected Integer _gasRegister;
   private boolean _filtered = false;

   @Override
   protected String getDefaultIpAddress() {
      return null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter slotParam = new SourceParameter("slot", String.class, this._slot.toString());
      slotParam.setOptions(new String[]{"0", "1", "2"});
      parameters.add(slotParam);
      parameters.add(new SourceParameter("database", String.class, this._database != null ? this._database.toString() : null));
      SourceParameter storage = new SourceParameter("storageFormat", String.class, this._storage.name());
      storage.setOptions(STORAGE_OPTIONS);
      parameters.add(storage);
      parameters.add(
         new SourceParameter("beanTemperatureRegister", String.class, this._beanTemperatureRegister != null ? this._beanTemperatureRegister.toString() : null)
      );
      parameters.add(
         new SourceParameter(
            "exhaustTemperatureRegister", Integer.class, this._exhaustTemperatureRegister != null ? this._exhaustTemperatureRegister.toString() : null
         )
      );
      parameters.add(new SourceParameter("gasRegister", Integer.class, this._gasRegister != null ? this._gasRegister.toString() : null));
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      this._database = null;
      this._beanTemperatureRegister = null;
      this._exhaustTemperatureRegister = null;
      this._gasRegister = null;

      for (SourceParameter parameter : parameters) {
         if (parameter.getValue() != null && !StringUtils.isBlank(parameter.getValue())) {
            if ("slot".equals(parameter.getName())) {
               this._slot = Integer.valueOf(parameter.getValue());
            } else if ("database".equals(parameter.getName())) {
               this._database = Integer.valueOf(parameter.getValue());
            } else if ("storageFormat".equals(parameter.getName())) {
               this._storage = Storage.valueOf(parameter.getValue());
            } else if (parameter.getName().endsWith("beanTemperatureRegister") && parameter.getValue().matches("\\d+")) {
               this._beanTemperatureRegister = Integer.parseInt(parameter.getValue());
            } else if (parameter.getName().endsWith("exhaustTemperatureRegister") && parameter.getValue().matches("\\d+")) {
               this._exhaustTemperatureRegister = Integer.parseInt(parameter.getValue());
            } else if (parameter.getName().endsWith("gasRegister") && parameter.getValue().matches("\\d+")) {
               this._gasRegister = Integer.parseInt(parameter.getValue());
            } else if ("filter".equals(parameter.getName())) {
               this._filtered = Boolean.valueOf(parameter.getValue());
            }
         }
      }

      if (this._slot == null) {
         throw new IllegalArgumentException("Slot required!");
      } else if (this._database == null) {
         throw new IllegalArgumentException("Database required!");
      } else if (this._beanTemperatureRegister == null) {
         throw new IllegalArgumentException("Register (Bean Temp.) required!");
      }
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return "gas".equals(measureName) ? Unit.PERCENT : null;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{
         new GenericSiemensS7Source(
            this,
            this._inetAddress,
            this._slot,
            this._database,
            this._storage,
            this._beanTemperatureRegister,
            this._exhaustTemperatureRegister,
            this._gasRegister
         )
      };
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._filtered && measureName.matches(".+Temperature") ? new RemoveDuplicatesCurveModel(source) : null;
   }

   @Override
   public boolean supportsFilterOption(Set<SourceParameter> parameters) {
      for (SourceParameter parameter : parameters) {
         if ("storageFormat".equals(parameter.getName())
            && (Storage.INTEGER.name().equals(parameter.getValue()) || Storage.INTEGER_16.name().equals(parameter.getValue()))) {
            return true;
         }
      }

      return false;
   }
}
