package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetronciniSourceFactory implements MeasurementSourceFactory {
   private final Logger logger = LoggerFactory.getLogger(PetronciniSourceFactory.class);
   private static final String PARAM_VERSION = "version";
   private static final String PARAM_HOST_OVERRIDE = "hostOverride";
   private PetronciniSourceFactory.PetronciniVersion _petronciniVersion = PetronciniSourceFactory.PetronciniVersion.VERSION1;
   private String _hostOverride;
   private InetAddress _inetAddress;
   private boolean _filtered = true;

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._petronciniVersion != null ? this._petronciniVersion.name() : null);
      version.setOptions(new String[]{PetronciniSourceFactory.PetronciniVersion.VERSION1.name(), PetronciniSourceFactory.PetronciniVersion.VERSION4.name()});
      version.setOptionDisplayName(PetronciniSourceFactory.PetronciniVersion.VERSION1.name(), "TT 5/10, TT 15/20 w/ MAESTRO i05 or lower");
      version.setOptionDisplayName(PetronciniSourceFactory.PetronciniVersion.VERSION4.name(), "TT 5/10, TT 15/20, TTG 60 w/ MAESTRO i06 or higher");
      parameters.add(version);
      parameters.add(new SourceParameter("hostOverride", String.class, this._hostOverride));
      SourceParameter filtered = new SourceParameter("filter", Boolean.class, String.valueOf(this._filtered));
      filtered.setHidden(true);
      parameters.add(filtered);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      this.logger.info("Parameters: {}", StringUtils.strip(parameters.toString(), "{}"));

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Petroncini machine required!");
            }

            this._petronciniVersion = PetronciniSourceFactory.PetronciniVersion.valueOf(value);
         } else if ("hostOverride".equals(parameter.getName())) {
            this._hostOverride = parameter.getValue();
            if (StringUtils.isNotBlank(this._hostOverride)) {
               this._inetAddress = InetAddress.getByName(this._hostOverride);
            }
         } else if ("filter".equals(parameter.getName())) {
            this._filtered = Boolean.valueOf(parameter.getValue());
         }
      }

      if (this._inetAddress == null) {
         this._inetAddress = InetAddress.getByName(this.getDefaultIpAddress());
      }

      this.logger.info("Using Hostname/IP address: {}", this._inetAddress.getHostAddress());
   }

   protected String getDefaultIpAddress() {
      return "192.168.100.1";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "burnerTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName) && !"airflow".equals(measureName) && !"drumSpeed".equals(measureName) ? null : Unit.PERCENT;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      try {
         if (PetronciniSourceFactory.PetronciniVersion.VERSION1.equals(this._petronciniVersion)) {
            return new MeasurementSource[]{new PetronciniS7MkSource(this, this._inetAddress, 32, 0, 2, 4)};
         } else if (PetronciniSourceFactory.PetronciniVersion.VERSION4.equals(this._petronciniVersion)) {
            return new MeasurementSource[]{new PetronciniS7DbSource(this, this._inetAddress)};
         } else {
            throw new IllegalStateException("Invalid version: " + this._petronciniVersion);
         }
      } catch (Exception var3) {
         throw new IOException("Error initializing Petroncini measurement source", var3);
      }
   }

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   @Override
   public CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return this._filtered && measureName.matches(".+Temperature") ? new RemoveDuplicatesCurveModel(source) : null;
   }

   @Override
   public boolean supportsFilterOption(Set<SourceParameter> parameters) {
      return true;
   }

   public static enum PetronciniVersion {
      VERSION1,
      VERSION4;
   }
}
