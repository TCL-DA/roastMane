package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import com.phidget22.PhidgetException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class Phidget1132SourceFactory extends HubSensorSourceFactory {
   private static final String PARAM_MIN_CURRENT = "minCurrent";
   private static final String PARAM_MAX_CURRENT = "maxCurrent";
   protected Double _minCurrent = 4.0;
   protected Double _maxCurrent = 20.0;

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Unit getUnit(String measureName) {
      return !measureName.equals("gas") && !measureName.equals("airflow") ? null : Unit.PERCENT;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      parameters.add(new SourceParameter("minCurrent", Double.class, this._minCurrent != null ? String.valueOf(this._minCurrent) : null));
      parameters.add(new SourceParameter("maxCurrent", Double.class, this._maxCurrent != null ? String.valueOf(this._maxCurrent) : null));
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Double[] minMaxValues = checkMinMaxValues(parameters, "minCurrent", "maxCurrent");
      this._minCurrent = minMaxValues[0];
      this._maxCurrent = minMaxValues[1];
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{
            new Phidget1132Source(
               this, this._minCurrent, this._maxCurrent, this._phidgetHub.equals("VINT Hub"), this._host, this._port, this.getDeviceDetector()
            )
         };
      } catch (PhidgetException var3) {
         throw new IOException("Error instantiating " + Phidget1132Source.class.getName(), var3);
      }
   }
}
