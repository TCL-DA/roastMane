package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.model.Unit;
import com.phidget22.RTDType;
import com.phidget22.RTDWireSetup;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

@NotMutuallyExclusiveSource
public class PhidgetTmp1200SourceFactory extends AbstractPhidgetNetworkingSourceFactory {
   private static final String PARAM_RTD_WIRE_SETUP = "rtdWireSetup";
   protected RTDWireSetup _rtdWireSetup = RTDWireSetup.WIRES_3;

   @Override
   public int getChannelCount() {
      return 6;
   }

   @Override
   public String[] getTypes() {
      return new String[]{RTDType.PT100_3850.name(), RTDType.PT1000_3850.name(), RTDType.PT100_3920.name(), RTDType.PT1000_3920.name()};
   }

   @Override
   public Unit getUnit(String measureName) {
      return measureName.matches(".+Temperature") ? Unit.CELSIUS : null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("rtdWireSetup", String.class, this._rtdWireSetup != null ? this._rtdWireSetup.name() : null);
      version.setOptions(new String[]{RTDWireSetup.WIRES_2.name(), RTDWireSetup.WIRES_3.name(), RTDWireSetup.WIRES_4.name()});
      parameters.add(version);
      parameters.addAll(super.getParameters());
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);

      for (SourceParameter parameter : parameters) {
         if ("rtdWireSetup".equals(parameter.getName()) && StringUtils.isNotBlank(parameter.getValue())) {
            this._rtdWireSetup = RTDWireSetup.valueOf(parameter.getValue());
         }
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         return new MeasurementSource[]{new PhidgetTmp1200Source(this, this._rtdWireSetup, this._host, this._port, this.getDeviceDetector())};
      } catch (Exception var3) {
         throw new IOException("Error instantiating " + PhidgetTmp1200Source.class.getName(), var3);
      }
   }
}
