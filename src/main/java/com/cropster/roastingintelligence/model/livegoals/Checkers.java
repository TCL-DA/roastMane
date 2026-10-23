package com.cropster.roastingintelligence.model.livegoals;

import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.checkers.ChargeChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.ConstantBeanTempChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.ConstantRoRChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.DevelopmentTimeChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.DevelopmentTimeRatioChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.DurationChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.EndChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.EndWeightChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.FirstCrackChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.RoRChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.RoastValue2Checker;
import com.cropster.roastingintelligence.model.livegoals.checkers.RoastValueChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.SimpleRorChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.StartWeightChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.WeightLossChecker;
import java.util.HashMap;
import java.util.Map;

public final class Checkers {
   private static final Map<String, Checker> DEFAULT_CHECKERS = Map.ofEntries(
      Map.entry(
         "chargeTemperature-afterburnerTemperature", new ChargeChecker("chargeTemperature-afterburnerTemperature", "afterburnerTemperature", false, false)
      ),
      Map.entry(
         "chargeTemperature-afterburnerTemperature2", new ChargeChecker("chargeTemperature-afterburnerTemperature2", "afterburnerTemperature2", false, false)
      ),
      Map.entry("chargeTemperature-burnerTemperature", new ChargeChecker("chargeTemperature-burnerTemperature", "burnerTemperature", false, false)),
      Map.entry("chargeTemperature-drumTemperature", new ChargeChecker("chargeTemperature-drumTemperature", "drumTemperature", false, false)),
      Map.entry("chargeTemperature-exhaustTemperature", new ChargeChecker("chargeTemperature-exhaustTemperature", "exhaustTemperature", false, false)),
      Map.entry("chargeTemperature-inletTemperature", new ChargeChecker("chargeTemperature-inletTemperature", "inletTemperature", false, false)),
      Map.entry("chargeTemperature-otherTemperature", new ChargeChecker("chargeTemperature-otherTemperature", "otherTemperature", false, false)),
      Map.entry("chargeTemperature-returnTemperature", new ChargeChecker("chargeTemperature-returnTemperature", "returnTemperature", false, false)),
      Map.entry("chargeTemperature-stackTemperature", new ChargeChecker("chargeTemperature-stackTemperature", "stackTemperature", false, false)),
      Map.entry("endTemperature-afterburnerTemperature", new EndChecker("endTemperature-afterburnerTemperature", "afterburnerTemperature", false, false)),
      Map.entry("endTemperature-afterburnerTemperature2", new EndChecker("endTemperature-afterburnerTemperature2", "afterburnerTemperature2", false, false)),
      Map.entry("endTemperature-burnerTemperature", new EndChecker("endTemperature-burnerTemperature", "burnerTemperature", false, false)),
      Map.entry("endTemperature-drumTemperature", new EndChecker("endTemperature-drumTemperature", "drumTemperature", false, false)),
      Map.entry("endTemperature-exhaustTemperature", new EndChecker("endTemperature-exhaustTemperature", "exhaustTemperature", false, false)),
      Map.entry("endTemperature-inletTemperature", new EndChecker("endTemperature-inletTemperature", "inletTemperature", false, false)),
      Map.entry("endTemperature-otherTemperature", new EndChecker("endTemperature-otherTemperature", "otherTemperature", false, false)),
      Map.entry("endTemperature-returnTemperature", new EndChecker("endTemperature-returnTemperature", "returnTemperature", false, false)),
      Map.entry("endTemperature-stackTemperature", new EndChecker("endTemperature-stackTemperature", "stackTemperature", false, false)),
      Map.entry("airflow", new ChargeChecker("airflow", false, false)),
      Map.entry("ambientTemperature", new ChargeChecker("ambientTemperature", false, false)),
      Map.entry("beanRoR", new RoRChecker()),
      Map.entry("chargeTemperature", new ChargeChecker("chargeTemperature", "beanTemperature", false, true)),
      Map.entry("constantBeanTemp", new ConstantBeanTempChecker()),
      Map.entry("constantRoR", new ConstantRoRChecker()),
      Map.entry("developmentTime", new DevelopmentTimeChecker()),
      Map.entry("developmentTimeRatio", new DevelopmentTimeRatioChecker()),
      Map.entry("duration", new DurationChecker()),
      Map.entry("endTemperature", new EndChecker("endTemperature", "beanTemperature", false, true)),
      Map.entry("endWeight", new EndWeightChecker()),
      Map.entry("firstCrack", new FirstCrackChecker()),
      Map.entry("gas", new ChargeChecker("gas", false, false)),
      Map.entry("roastValue", new RoastValueChecker()),
      Map.entry("roastValue2", new RoastValue2Checker()),
      Map.entry("simpleRor10", new SimpleRorChecker("simpleRor10", 10L)),
      Map.entry("simpleRor15", new SimpleRorChecker("simpleRor15", 15L)),
      Map.entry("simpleRor30", new SimpleRorChecker("simpleRor30", 30L)),
      Map.entry("simpleRor60", new SimpleRorChecker("simpleRor60", 60L)),
      Map.entry("startWeight", new StartWeightChecker()),
      Map.entry("weightLoss", new WeightLossChecker())
   );
   private static final Map<String, Checker> BB_CHECKERS = new HashMap<>(DEFAULT_CHECKERS);
   private static final Map<String, Checker> PR_CHECKERS = new HashMap<>(DEFAULT_CHECKERS);

   public static Checker getChecker(String goalName, RoastingPhase phase, boolean postRoast) {
      if (phase == null) {
         return null;
      } else {
         return switch (phase) {
            case ROASTING -> postRoast ? (Checker)PR_CHECKERS.get(goalName) : (Checker)DEFAULT_CHECKERS.get(goalName);
            case BETWEEN_BATCHES -> (Checker)BB_CHECKERS.get(goalName);
            default -> null;
         };
      }
   }

   private Checkers() {
   }

   static {
      BB_CHECKERS.put(
         "chargeTemperature-afterburnerTemperature", new ChargeChecker("chargeTemperature-afterburnerTemperature", "afterburnerTemperature", true, false)
      );
      BB_CHECKERS.put(
         "chargeTemperature-afterburnerTemperature2", new ChargeChecker("chargeTemperature-afterburnerTemperature2", "afterburnerTemperature2", true, false)
      );
      BB_CHECKERS.put("chargeTemperature-burnerTemperature", new ChargeChecker("chargeTemperature-burnerTemperature", "burnerTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-drumTemperature", new ChargeChecker("chargeTemperature-drumTemperature", "drumTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-exhaustTemperature", new ChargeChecker("chargeTemperature-exhaustTemperature", "exhaustTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-inletTemperature", new ChargeChecker("chargeTemperature-inletTemperature", "inletTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-otherTemperature", new ChargeChecker("chargeTemperature-otherTemperature", "otherTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-returnTemperature", new ChargeChecker("chargeTemperature-returnTemperature", "returnTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature-stackTemperature", new ChargeChecker("chargeTemperature-stackTemperature", "stackTemperature", true, false));
      BB_CHECKERS.put("endTemperature-afterburnerTemperature", new EndChecker("endTemperature-afterburnerTemperature", "afterburnerTemperature", true, false));
      BB_CHECKERS.put(
         "endTemperature-afterburnerTemperature2", new EndChecker("endTemperature-afterburnerTemperature2", "afterburnerTemperature2", true, false)
      );
      BB_CHECKERS.put("endTemperature-burnerTemperature", new EndChecker("endTemperature-burnerTemperature", "burnerTemperature", true, false));
      BB_CHECKERS.put("endTemperature-drumTemperature", new EndChecker("endTemperature-drumTemperature", "drumTemperature", true, false));
      BB_CHECKERS.put("endTemperature-exhaustTemperature", new EndChecker("endTemperature-exhaustTemperature", "exhaustTemperature", true, false));
      BB_CHECKERS.put("endTemperature-inletTemperature", new EndChecker("endTemperature-inletTemperature", "inletTemperature", true, false));
      BB_CHECKERS.put("endTemperature-otherTemperature", new EndChecker("endTemperature-otherTemperature", "otherTemperature", true, false));
      BB_CHECKERS.put("endTemperature-returnTemperature", new EndChecker("endTemperature-returnTemperature", "returnTemperature", true, false));
      BB_CHECKERS.put("endTemperature-stackTemperature", new EndChecker("endTemperature-stackTemperature", "stackTemperature", true, false));
      BB_CHECKERS.put("airflow", new ChargeChecker("airflow", true, false));
      BB_CHECKERS.put("ambientTemperature", new ChargeChecker("ambientTemperature", true, false));
      BB_CHECKERS.put("chargeTemperature", new ChargeChecker("chargeTemperature", "beanTemperature", true, true));
      BB_CHECKERS.put("duration", new DurationChecker(true));
      BB_CHECKERS.put("endTemperature", new EndChecker("endTemperature", "beanTemperature", true, true));
      BB_CHECKERS.put("gas", new ChargeChecker("gas", true, false));
      BB_CHECKERS.put("simpleRor10", new SimpleRorChecker("simpleRor10", 10L, true));
      BB_CHECKERS.put("simpleRor15", new SimpleRorChecker("simpleRor15", 15L, true));
      BB_CHECKERS.put("simpleRor30", new SimpleRorChecker("simpleRor30", 30L, true));
      BB_CHECKERS.put("simpleRor60", new SimpleRorChecker("simpleRor60", 60L, true));
      BB_CHECKERS.put("startWeight", new StartWeightChecker(true));
      PR_CHECKERS.put("endWeight", new EndWeightChecker(true));
      PR_CHECKERS.put("roastValue", new RoastValueChecker(true));
      PR_CHECKERS.put("roastValue2", new RoastValue2Checker(true));
      PR_CHECKERS.put("startWeight", BB_CHECKERS.get("startWeight"));
      PR_CHECKERS.put("weightLoss", new WeightLossChecker(true));
   }
}
