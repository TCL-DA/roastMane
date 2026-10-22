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

public class CogenPlcSourceFactory extends NetworkSourceFactory {
   private boolean _filtered = false;

   @Override
   protected String getDefaultIpAddress() {
      return "";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "drumTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("gas".equals(measureName) || "gasControl".equals(measureName)) {
         return Unit.NUMBER;
      } else if ("drumPressure".equals(measureName)) {
         return Unit.PASCAL;
      } else if ("drumSpeed".equals(measureName) || "drumSpeedControl".equals(measureName)) {
         return Unit.RPM;
      } else {
         return !"airflow".equals(measureName) && !"airflowControl".equals(measureName) ? null : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("filter".equals(parameter.getName())) {
            this._filtered = Boolean.valueOf(parameter.getValue());
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new CogenPlcSource(this, this._inetAddress)};
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._filtered && measureName.matches(".+Temperature") ? new RemoveDuplicatesCurveModel(source) : null;
   }

   @Override
   public boolean supportsFilterOption(Set<SourceParameter> parameters) {
      return true;
   }
}
