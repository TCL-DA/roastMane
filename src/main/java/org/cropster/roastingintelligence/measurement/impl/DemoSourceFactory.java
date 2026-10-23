package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.util.Collect;
import com.cropster.util.model.Unit;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.RoastingManager;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.service.impl.JsonReader;

@NotMutuallyExclusiveSource
public class DemoSourceFactory implements MeasurementSourceFactory {
   private static final String[] TYPES = new String[]{"Demo"};
   public static final String DEMO_FILE = "demo-roast.json";
   protected Roast _demoRoast;

   private Roast getDemoRoast() {
      if (this._demoRoast == null) {
         File demoFile = new File(FileUtils.getUserDirectory(), "demo-roast.json");

         try {
            JsonReader jsonReader = new JsonReader();
            this._demoRoast = jsonReader.read(new FileReader(demoFile, Defaults.CHARSET));
         } catch (Exception var12) {
            throw new RuntimeException("Error loading " + demoFile.getAbsolutePath(), var12);
         }

         long duration = 0L;
         Map<String, Double> lastValues = new HashMap<>();

         for (Curve beanCurve = this._demoRoast.getCurves().get("beanTemperature"); duration <= beanCurve.lastKey(); duration += 1000L) {
            for (Entry<String, Curve> entry : this._demoRoast.getCurves().entrySet()) {
               String curveName = entry.getKey();
               SortedMap<Long, Double> curve = entry.getValue();
               Long key = Collect.proximate(curve, duration, 500L);
               if (key == null) {
                  Double lastValue = lastValues.containsKey(curveName) ? lastValues.get(curveName) : 0.0;
                  curve.put(Long.valueOf(duration), lastValue);
               } else {
                  lastValues.put(curveName, curve.get(key));
               }
            }
         }
      }

      return this._demoRoast;
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
   }

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return TYPES;
   }

   @Override
   public Unit getUnit(String measureName) {
      return !measureName.matches(".+[cC]ontrol") && this.getDemoRoast().getCurves().containsKey(measureName)
         ? this.getDemoRoast().getCurves().get(measureName).getUnit()
         : null;
   }

   @Override
   public Set<SourceParameter> getParameters() {
      return Collections.emptySet();
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      RoastingManager roastingManager = RoastingIntelligenceApp.retrieveSingleton(RoastingManager.class);
      DemoSourceImpl demoSource = new DemoSourceImpl(this, this.getDemoRoast());
      roastingManager.addRoastingListener(demoSource);
      return new MeasurementSource[]{demoSource};
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
