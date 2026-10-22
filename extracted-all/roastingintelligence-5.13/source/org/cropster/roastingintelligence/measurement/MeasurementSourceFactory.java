package org.cropster.roastingintelligence.measurement;

import com.cropster.util.model.Unit;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.CurveModel;

public interface MeasurementSourceFactory {
   Comparator<? super MeasurementSourceFactory> SOURCE_FACTORY_COMPARATOR = (sf1, sf2) -> {
      int g1 = groupIndexOf(sf1);
      int g2 = groupIndexOf(sf2);
      return g1 != g2
         ? g1 - g2
         : org.cropster.roastingintelligence.util.Messages.m_(sf1.toString())
            .compareToIgnoreCase(org.cropster.roastingintelligence.util.Messages.m_(sf2.toString()));
   };

   static int groupIndexOf(MeasurementSourceFactory sf) {
      if (sf == null) {
         return 0;
      } else {
         int index = ArrayUtils.indexOf(Defaults.SOURCE_FACTORIES, sf.getClass());
         return Stream.of(Defaults.SOURCE_FACTORY_GROUPS).mapToInt(Integer::intValue).sorted().filter(g -> g > index).findFirst().orElse(Integer.MAX_VALUE);
      }
   }

   int getChannelCount();

   String[] getTypes();

   Unit getUnit(String var1);

   Set<SourceParameter> getParameters();

   void setup(Set<SourceParameter> var1) throws Exception;

   default List<String> getSetupWarnings() {
      return Collections.emptyList();
   }

   default List<MeasurementSourceFactory.ConfigurationWarning> getConfigurationWarnings(Set<Channel> channels) {
      return Collections.emptyList();
   }

   MeasurementSource[] getMeasurementSources(Collection<Channel> var1) throws IOException;

   default String getAlias(String measureName) {
      return measureName;
   }

   default boolean supportsAutoStart(Set<SourceParameter> parameters) {
      return false;
   }

   default boolean supportsAutoMode(String measureName) {
      return false;
   }

   default String getAutoModeMeasurement(String measureName) {
      return null;
   }

   default String getManualModeMeasurement(String measureName) {
      return null;
   }

   default ControlState[] getSupportedStates(Set<SourceParameter> parameters) {
      return new ControlState[0];
   }

   default int getSamplingInterval() {
      return 1000;
   }

   default CurveModel getFilterCurveModel(String measureName, CurveModel source) {
      return null;
   }

   default boolean supportsFilterOption(Set<SourceParameter> parameters) {
      return false;
   }

   public static record ConfigurationWarning(String title, String message) {
   }
}
