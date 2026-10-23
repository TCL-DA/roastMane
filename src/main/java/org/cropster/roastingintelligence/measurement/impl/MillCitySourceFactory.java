package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import com.phidget22.PhidgetException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MillCitySourceFactory extends SerialSourceFactory {
   private final Logger logger = LoggerFactory.getLogger(MillCitySourceFactory.class);
   private static final String[] TEMP_OPTIONS = new String[]{Unit.CELSIUS.name(), Unit.FAHRENHEIT.name()};
   private static final String PARAM_VERSION = "millCityVersion";
   private static final String PARAM_TEMP_UNIT = "temperatureUnit";
   private MillCitySourceFactory.MillCityVersion _millCityVersion;
   private Unit _temperatureUnit = Unit.FAHRENHEIT;
   private boolean _filtered = false;
   private Phidget22SourceFactory phidgetSourceFactory = null;

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName)
         || "exhaustTemperature".equals(measureName)
         || "environmentalTemperature".equals(measureName)
         || "inletTemperature".equals(measureName)
         || "drumTemperature".equals(measureName)) {
         return this._temperatureUnit;
      } else if ("gas".equals(measureName)) {
         return Unit.KILOPASCAL;
      } else {
         return !"drumSpeed".equals(measureName) && !"airflow".equals(measureName) ? null : Unit.HZ;
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("millCityVersion", String.class, this._millCityVersion != null ? this._millCityVersion.name() : null);
      version.setOptions(
         new String[]{
            MillCitySourceFactory.MillCityVersion.VERSION1.name(),
            MillCitySourceFactory.MillCityVersion.VERSION2A.name(),
            MillCitySourceFactory.MillCityVersion.VERSION2B.name(),
            MillCitySourceFactory.MillCityVersion.VERSION3.name(),
            MillCitySourceFactory.MillCityVersion.VERSION4.name(),
            MillCitySourceFactory.MillCityVersion.VERSION5.name(),
            MillCitySourceFactory.MillCityVersion.VERSION5B.name()
         }
      );
      parameters.add(version);
      parameters.addAll(super.getParameters());
      SourceParameter temperatureUnit = new SourceParameter("temperatureUnit", String.class, this._temperatureUnit.name());
      temperatureUnit.setOptions(TEMP_OPTIONS);
      parameters.add(temperatureUnit);
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      this.logger.info("Parameters: {}", StringUtils.strip(parameters.toString(), "{}"));

      for (SourceParameter parameter : parameters) {
         if ("millCityVersion".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Mill City machine required!");
            }

            this._millCityVersion = MillCitySourceFactory.MillCityVersion.valueOf(value);
         } else if (!"serialPort".equals(parameter.getName())) {
            if ("temperatureUnit".equals(parameter.getName())) {
               this._temperatureUnit = Unit.FAHRENHEIT.name().equalsIgnoreCase(parameter.getValue()) ? Unit.FAHRENHEIT : Unit.CELSIUS;
            } else if ("filter".equals(parameter.getName())) {
               this._filtered = Boolean.valueOf(parameter.getValue());
            }
         } else {
            this._serialPort = parameter.getValue();
            if ((this._serialPort == null || this._serialPort.trim().length() == 0)
               && !MillCitySourceFactory.MillCityVersion.VERSION1.equals(this._millCityVersion)) {
               throw new IllegalArgumentException("Serial port required!");
            }

            if (this._serialPort != null) {
               this._serialPortName = this._serialPort.startsWith("/dev/") ? this._serialPort.substring(5) : this._serialPort;
            }

            this.checkLinuxGroupMembership("dialout");
         }
      }

      if (this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION1)
         || this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION2A)
         || this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION2B)
         || this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION3)) {
         this.phidgetSourceFactory = new MillCitySourceFactory.MillCityPhidgetSourceFactory();
         this.phidgetSourceFactory.setup(parameters);
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         SerialParameters parameters = new SerialParameters();
         parameters.setPortName(this._serialPortName);
         parameters.setEncoding("rtu");
         parameters.setDatabits(8);
         parameters.setParity(0);
         parameters.setStopbits(3);
         parameters.setEcho(false);
         if (!this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION5)
            && !this._millCityVersion.equals(MillCitySourceFactory.MillCityVersion.VERSION5B)) {
            parameters.setBaudRate(19200);
         } else {
            parameters.setBaudRate(9600);
         }

         switch (this._millCityVersion) {
            case VERSION1:
               return this.phidgetSourceFactory.getMeasurementSources(channels);
            case VERSION2A:
            case VERSION2B:
            case VERSION3:
               return new MeasurementSource[]{
                  this.phidgetSourceFactory.getMeasurementSources(channels)[0], new MillCitySource(this, parameters, this._millCityVersion)
               };
            case VERSION4:
               return new MeasurementSource[]{new MillCityMcrSource(this, parameters)};
            case VERSION5:
            case VERSION5B:
               return new MeasurementSource[]{new MillCityV5Source(this, parameters, this._millCityVersion, this._temperatureUnit)};
            default:
               throw new IllegalStateException("Invalid version");
         }
      } catch (Exception var3) {
         throw new RuntimeException("Error creating MillCity source", var3);
      }
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._filtered && MillCitySourceFactory.MillCityVersion.VERSION4.equals(this._millCityVersion) && measureName.matches(".+Temperature")
         ? new RemoveDuplicatesCurveModel(source, 0.8)
         : null;
   }

   @Override
   public boolean supportsFilterOption(Set<SourceParameter> parameters) {
      for (SourceParameter parameter : parameters) {
         if ("millCityVersion".equals(parameter.getName())
            && parameter.getValue() != null
            && MillCitySourceFactory.MillCityVersion.VERSION4.equals(MillCitySourceFactory.MillCityVersion.valueOf(parameter.getValue()))) {
            return true;
         }
      }

      return false;
   }

   class MillCityPhidgetSourceFactory extends Phidget22SourceFactory {
      @Override
      public int getChannelCount() {
         return this.getChannelCount();
      }

      @Override
      public String[] getTypes() {
         return this.getTypes();
      }

      @Override
      public Unit getUnit(String measureName) {
         return this.getUnit(measureName);
      }

      @Override
      public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
         MeasurementSource source;
         try {
            source = new MillCityPhidget22Source(
               MillCitySourceFactory.this, MillCitySourceFactory.this._temperatureUnit, MillCitySourceFactory.this._millCityVersion, this.getDeviceDetector()
            );
         } catch (PhidgetException var4) {
            throw new IOException(var4);
         }

         return new MeasurementSource[]{source};
      }
   }

   public static enum MillCityVersion {
      VERSION1,
      VERSION2A,
      VERSION2B,
      VERSION3,
      VERSION4,
      VERSION5,
      VERSION5B;
   }
}
