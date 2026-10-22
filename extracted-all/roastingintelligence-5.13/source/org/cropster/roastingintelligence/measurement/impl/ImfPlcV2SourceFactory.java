package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class ImfPlcV2SourceFactory extends NetworkSourceFactory {
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private Unit _temperatureUnit = Unit.CELSIUS;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.2.32";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "inletTemperature".equals(measureName)
         || "burnerTemperature".equals(measureName)
         || "setTemperatureControl".equals(measureName)) {
         return this._temperatureUnit;
      } else {
         return !"custom".equals(measureName)
               && !"customControl".equals(measureName)
               && !"airflow".equals(measureName)
               && !"airflowControl".equals(measureName)
               && !"drumSpeed".equals(measureName)
               && !"drumSpeedControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("temperatureUnit".equals(parameter.getName())) {
            this._temperatureUnit = Unit.FAHRENHEIT.name().equalsIgnoreCase(parameter.getValue()) ? Unit.FAHRENHEIT : Unit.CELSIUS;
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      return new MeasurementSource[]{new ImfPlcV2Source(this, this._inetAddress, this._temperatureUnit)};
   }

   @Override
   public boolean supportsAutoMode(String measureName) {
      return "customControl".equals(measureName) || "setTemperatureControl".equals(measureName);
   }

   @Override
   public String getAutoModeMeasurement(String measureName) {
      return !"customControl".equals(measureName) && !"setTemperatureControl".equals(measureName) ? null : "setTemperatureControl";
   }

   @Override
   public String getManualModeMeasurement(String measureName) {
      return !"customControl".equals(measureName) && !"setTemperatureControl".equals(measureName) ? null : "customControl";
   }

   @Override
   public List<MeasurementSourceFactory.ConfigurationWarning> getConfigurationWarnings(Set<Channel> channels) {
      if (channels.stream().noneMatch(channel -> channel.getName().matches(".+[cC]ontrol"))) {
         return Collections.emptyList();
      } else {
         List<MeasurementSourceFactory.ConfigurationWarning> warnings = new ArrayList<>();
         warnings.add(
            new MeasurementSourceFactory.ConfigurationWarning(
               org.cropster.roastingintelligence.util.Messages.m_("Slider control values may be out of sync with the machine"),
               org.cropster.roastingintelligence.util.Messages.m_(
                  "Displayed values may not reflect the machine's actual state. Slider control values only take effect if the setting \"roastMane - Enable Roasting\" is enabled in the IMF machine's control panel and the machine is in roasting phase."
               )
            )
         );
         return warnings;
      }
   }
}
