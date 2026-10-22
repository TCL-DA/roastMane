package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class PressureSensorPercentSourceFactory extends PressureSensorSourceFactory {
   private static final String PARAM_MIN_PRESSURE = "minPressure";
   private static final String PARAM_MAX_PRESSURE = "maxPressure";
   protected Double _minPressure = 0.0;
   protected Double _maxPressure = 2000.0;

   @Override
   public Unit getUnit(String measureName) {
      return !measureName.equals("gas") && !measureName.equals("airflow") ? null : Unit.PERCENT;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      parameters.add(new SourceParameter("minPressure", Double.class, this._minPressure != null ? String.valueOf(this._minPressure) : null));
      parameters.add(new SourceParameter("maxPressure", Double.class, this._maxPressure != null ? String.valueOf(this._maxPressure) : null));
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Double[] minMaxValues = checkMinMaxValues(parameters, "minPressure", "maxPressure");
      this._minPressure = minMaxValues[0];
      this._maxPressure = minMaxValues[1];
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{
         new VoltageRatioSource(
            this, this._minPressure, this._maxPressure, this._phidgetHub.equals("VINT Hub"), this._host, this._port, this.getDeviceDetector()
         )
      };
   }
}
