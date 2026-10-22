package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;

public class ImfPlcSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_LEGACY_PLC = "legacyPlc";
   private boolean _legacyPlc;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.2.32";
   }

   @Override
   public Unit getUnit(String measureName) {
      return !"beanTemperature".equals(measureName) && !"inletTemperature".equals(measureName) && !"burnerTemperature".equals(measureName)
         ? null
         : Unit.CELSIUS;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      parameters.add(new SourceParameter("legacyPlc", Boolean.class, String.valueOf(this._legacyPlc)));
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("legacyPlc".equals(parameter.getName())) {
            this._legacyPlc = "true".equalsIgnoreCase(parameter.getValue());
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new ImfPlcSource(this, this._inetAddress, this._legacyPlc)};
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._legacyPlc ? new RemoveDuplicatesCurveModel(source) : null;
   }
}
