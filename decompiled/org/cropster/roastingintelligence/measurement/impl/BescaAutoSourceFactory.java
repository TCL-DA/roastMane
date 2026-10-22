package org.cropster.roastingintelligence.measurement.impl;

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
import org.cropster.roastingintelligence.util.LinearValueConverter;

public class BescaAutoSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_LOWER_GAS_LIMIT = "lowerGasLimit";
   private static final String PARAM_UPPER_GAS_LIMIT = "upperGasLimit";
   private static final Integer GAS_LIMIT_MAX = 8191;
   private static final Integer LOWER_GAS_LIMIT_DEFAULT = 0;
   private static final Integer UPPER_GAS_LIMIT_DEFAULT = 4500;
   private static final String PARAM_LOWER_FAN_SPEED_LIMIT = "lowerFanSpeedLimit";
   private static final String PARAM_UPPER_FAN_SPEED_LIMIT = "upperFanSpeedLimit";
   private static final Integer FAN_SPEED_LIMIT_MAX = 600;
   private static final Integer LOWER_FAN_SPEED_LIMIT_DEFAULT = 125;
   private static final Integer UPPER_FAN_SPEED_LIMIT_DEFAULT = 375;
   private static final String PARAM_LOWER_DRUM_SPEED_LIMIT = "lowerDrumSpeedLimit";
   private static final String PARAM_UPPER_DRUM_SPEED_LIMIT = "upperDrumSpeedLimit";
   private static final Integer DRUM_SPEED_LIMIT_MAX = 600;
   private static final Integer LOWER_DRUM_SPEED_LIMIT_DEFAULT = 100;
   private static final Integer UPPER_DRUM_SPEED_LIMIT_DEFAULT = 500;
   private Integer _lowerGasLimit = LOWER_GAS_LIMIT_DEFAULT;
   private Integer _upperGasLimit = UPPER_GAS_LIMIT_DEFAULT;
   private Integer _lowerFanSpeedLimit = LOWER_FAN_SPEED_LIMIT_DEFAULT;
   private Integer _upperFanSpeedLimit = UPPER_FAN_SPEED_LIMIT_DEFAULT;
   private Integer _lowerDrumSpeedLimit = LOWER_DRUM_SPEED_LIMIT_DEFAULT;
   private Integer _upperDrumSpeedLimit = UPPER_DRUM_SPEED_LIMIT_DEFAULT;
   private boolean _filtered = false;

   @Override
   protected String getDefaultIpAddress() {
      return "10.0.0.9";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else if ("fanSpeed".equals(measureName)) {
         return Unit.HZ;
      } else {
         return !"gas".equals(measureName)
               && !"gasControl".equals(measureName)
               && !"airflow".equals(measureName)
               && !"airflowControl".equals(measureName)
               && !"drumSpeed".equals(measureName)
               && !"drumSpeedControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public String getAlias(String measureName) {
      return "drumSpeedControl".equals(measureName) ? "drumSpeed" : measureName;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter parameter = new SourceParameter("lowerGasLimit", Integer.class, String.valueOf(this._lowerGasLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("lowerGasLimit") + " (Flame Out Down Limit / Burner Signal Min)");
      parameters.add(parameter);
      parameter = new SourceParameter("upperGasLimit", Integer.class, String.valueOf(this._upperGasLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("upperGasLimit") + " (Flame Out Up Limit / Burner Signal Max)");
      parameters.add(parameter);
      parameter = new SourceParameter("lowerFanSpeedLimit", Integer.class, String.valueOf(this._lowerFanSpeedLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("lowerFanSpeedLimit") + " (Fan Output Speed Down Limit / Fan Speed Min)");
      parameters.add(parameter);
      parameter = new SourceParameter("upperFanSpeedLimit", Integer.class, String.valueOf(this._upperFanSpeedLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("upperFanSpeedLimit") + " (Fan Output Speed Up Limit / Fan Speed Max)");
      parameters.add(parameter);
      parameter = new SourceParameter("lowerDrumSpeedLimit", Integer.class, String.valueOf(this._lowerDrumSpeedLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("lowerDrumSpeedLimit") + " (Drum Output Speed Down Limit / Drum Speed Min)");
      parameters.add(parameter);
      parameter = new SourceParameter("upperDrumSpeedLimit", Integer.class, String.valueOf(this._upperDrumSpeedLimit));
      parameter.setDisplayName(org.cropster.roastingintelligence.util.Messages.m_("upperDrumSpeedLimit") + " (Drum Output Speed Up Limit / Drum Speed Max)");
      parameters.add(parameter);
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   public Integer validateParameter(SourceParameter parameter, int min, int max, int defaultValue) {
      Integer parameterValue = StringUtils.isNotBlank(parameter.getValue()) ? Integer.valueOf(parameter.getValue()) : defaultValue;
      if (parameterValue < min) {
         throw new IllegalArgumentException(
            String.format("%s must be greater than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), min)
         );
      } else if (parameterValue > max) {
         throw new IllegalArgumentException(
            String.format("%s must be smaller than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), max)
         );
      } else {
         return parameterValue;
      }
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Integer lowerGasLimit = LOWER_GAS_LIMIT_DEFAULT;
      Integer upperGasLimit = UPPER_GAS_LIMIT_DEFAULT;
      Integer lowerFanSpeedLimit = LOWER_FAN_SPEED_LIMIT_DEFAULT;
      Integer upperFanSpeedLimit = UPPER_FAN_SPEED_LIMIT_DEFAULT;
      Integer lowerDrumSpeedLimit = LOWER_DRUM_SPEED_LIMIT_DEFAULT;
      Integer upperDrumSpeedLimit = UPPER_DRUM_SPEED_LIMIT_DEFAULT;

      for (SourceParameter parameter : parameters) {
         if ("lowerGasLimit".equals(parameter.getName())) {
            lowerGasLimit = this.validateParameter(parameter, 0, GAS_LIMIT_MAX, LOWER_GAS_LIMIT_DEFAULT);
         } else if ("upperGasLimit".equals(parameter.getName())) {
            upperGasLimit = this.validateParameter(parameter, 0, GAS_LIMIT_MAX, UPPER_GAS_LIMIT_DEFAULT);
         } else if ("lowerFanSpeedLimit".equals(parameter.getName())) {
            lowerFanSpeedLimit = this.validateParameter(parameter, 0, FAN_SPEED_LIMIT_MAX, LOWER_FAN_SPEED_LIMIT_DEFAULT);
         } else if ("upperFanSpeedLimit".equals(parameter.getName())) {
            upperFanSpeedLimit = this.validateParameter(parameter, 0, FAN_SPEED_LIMIT_MAX, UPPER_FAN_SPEED_LIMIT_DEFAULT);
         } else if ("lowerDrumSpeedLimit".equals(parameter.getName())) {
            lowerDrumSpeedLimit = this.validateParameter(parameter, 0, DRUM_SPEED_LIMIT_MAX, LOWER_DRUM_SPEED_LIMIT_DEFAULT);
         } else if ("upperDrumSpeedLimit".equals(parameter.getName())) {
            upperDrumSpeedLimit = this.validateParameter(parameter, 0, DRUM_SPEED_LIMIT_MAX, UPPER_DRUM_SPEED_LIMIT_DEFAULT);
         } else if ("filter".equals(parameter.getName())) {
            this._filtered = Boolean.valueOf(parameter.getValue());
         }
      }

      if (lowerGasLimit >= upperGasLimit) {
         throw new IllegalArgumentException(
            String.format(
               "%s must be smaller than %s",
               org.cropster.roastingintelligence.util.Messages.m_("lowerGasLimit"),
               org.cropster.roastingintelligence.util.Messages.m_("upperGasLimit")
            )
         );
      } else {
         this._lowerGasLimit = lowerGasLimit;
         this._upperGasLimit = upperGasLimit;
         if (lowerFanSpeedLimit >= upperFanSpeedLimit) {
            throw new IllegalArgumentException(
               String.format(
                  "%s must be smaller than %s",
                  org.cropster.roastingintelligence.util.Messages.m_("lowerFanSpeedLimit"),
                  org.cropster.roastingintelligence.util.Messages.m_("upperFanSpeedLimit")
               )
            );
         } else {
            this._lowerFanSpeedLimit = lowerFanSpeedLimit;
            this._upperFanSpeedLimit = upperFanSpeedLimit;
            if (lowerDrumSpeedLimit >= upperDrumSpeedLimit) {
               throw new IllegalArgumentException(
                  String.format(
                     "%s must be smaller than %s",
                     org.cropster.roastingintelligence.util.Messages.m_("lowerDrumSpeedLimit"),
                     org.cropster.roastingintelligence.util.Messages.m_("upperDrumSpeedLimit")
                  )
               );
            } else {
               this._lowerDrumSpeedLimit = lowerDrumSpeedLimit;
               this._upperDrumSpeedLimit = upperDrumSpeedLimit;
            }
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      LinearValueConverter gasConverter = new LinearValueConverter((double)this._lowerGasLimit.intValue(), (double)this._upperGasLimit.intValue(), 0.0, 100.0);
      LinearValueConverter fanSpeedConverter = new LinearValueConverter(
         (double)this._lowerFanSpeedLimit.intValue(), (double)this._upperFanSpeedLimit.intValue(), 0.0, 100.0
      );
      LinearValueConverter drumSpeedConverter = new LinearValueConverter(
         (double)this._lowerDrumSpeedLimit.intValue(), (double)this._upperDrumSpeedLimit.intValue(), 0.0, 100.0
      );
      BescaAutoSource source = new BescaAutoSource(this, this._inetAddress, gasConverter, fanSpeedConverter, drumSpeedConverter);
      return new MeasurementSource[]{source};
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
