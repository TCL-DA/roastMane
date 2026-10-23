package org.cropster.roastingintelligence.measurement.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.SourceParameter;

public abstract class HubSensorSourceFactory extends AbstractPhidgetNetworkingSourceFactory {
   public static final String PARAM_PHIDGET_HUB = "phidgetHub";
   public static final String INTERFACEKIT = "InterfaceKit";
   public static final String HUB = "VINT Hub";
   protected String _phidgetHub = "VINT Hub";

   @Override
   public int getChannelCount() {
      return 8;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter phidgetHubParameter = new SourceParameter("phidgetHub", String.class, this._phidgetHub);
      phidgetHubParameter.setOptions(new String[]{"VINT Hub", "InterfaceKit"});
      parameters.add(phidgetHubParameter);
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if (parameter.getValue() != null && "phidgetHub".equals(parameter.getName())) {
            this._phidgetHub = parameter.getValue();
         }
      }
   }

   public static Double[] checkMinMaxValues(Set<SourceParameter> parameters, String minParam, String maxParam) throws IllegalArgumentException {
      Double minValue = null;
      Double maxValue = null;

      for (SourceParameter parameter : parameters) {
         if (minParam.equals(parameter.getName())) {
            if (StringUtils.isBlank(parameter.getValue())) {
               throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Minimum value required"));
            }

            minValue = Double.valueOf(parameter.getValue());
            if (minValue < 0.0) {
               throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Minimum value must be a positive value"));
            }
         } else if (maxParam.equals(parameter.getName())) {
            if (StringUtils.isBlank(parameter.getValue())) {
               throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Maximum value required"));
            }

            maxValue = Double.valueOf(parameter.getValue());
            if (maxValue < 0.0) {
               throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Maximum value must be a positive value"));
            }
         }
      }

      if (minValue == null || maxValue == null) {
         throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Minimum/maximum value required"));
      } else if (minValue >= maxValue) {
         throw new IllegalArgumentException(org.cropster.roastingintelligence.util.Messages.m_("Minimum value must be lower than maximum value"));
      } else {
         return new Double[]{minValue, maxValue};
      }
   }
}
