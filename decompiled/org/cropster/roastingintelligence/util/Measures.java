package org.cropster.roastingintelligence.util;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.util.model.Unit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Measures {
   public static final String STATE_PATTERN = ".+[sS]tate";
   public static final String CHARGE_STATE = "chargeState";
   public static final String DISCHARGE_STATE = "dischargeState";
   public static final String BURNER_STATE = "burnerState";
   public static final String COOLER_STATE = "coolerState";
   public static final String STIRRER_STATE = "stirrerState";
   public static final String STANDBY_STATE = "standbyState";
   public static final String DRUM_STATE = "drumState";
   public static final String REFERENCE_PREFIX = "Perfect ";
   public static final String TEMPERATURE_PATTERN = ".+Temperature";
   public static final String BEAN_TEMPERATURE = "beanTemperature";
   public static final String PREDICTION = "beanTemperaturePrediction";
   public static final String ENVIRONMENTAL_TEMPERATURE = "environmentalTemperature";
   public static final String EXHAUST_TEMPERATURE_CONTROL = "exhaustTemperatureControl";
   public static final String SET_TEMPERATURE_CONTROL = "setTemperatureControl";
   public static final String CONTROL_PATTERN = ".+[cC]ontrol";
   public static final String PREDICTION_PATTERN = ".+[pP]rediction";
   public static final String PREDICTION_ROR_PATTERN = "\\[.+\\]RoR.*";
   public static final String GAS_CONTROL = "gasControl";
   public static final String AIRFLOW_CONTROL = "airflowControl";
   public static final String DRUM_PRESSURE_CONTROL = "drumPressureControl";
   public static final String DRUM_SPEED_CONTROL = "drumSpeedControl";
   public static final String FAN_SPEED_CONTROL = "fanSpeedControl";
   public static final String EXHAUST_TEMPERATURE = "exhaustTemperature";
   public static final String BURNER_TEMPERATURE = "burnerTemperature";
   public static final String INLET_TEMPERATURE = "inletTemperature";
   public static final String STACK_TEMPERATURE = "stackTemperature";
   public static final String RETURN_TEMPERATURE = "returnTemperature";
   public static final String AFTERBURNER_TEMPERATURE = "afterburnerTemperature";
   public static final String AFTERBURNER2_TEMPERATURE = "afterburnerTemperature2";
   public static final String DRUM_TEMPERATURE = "drumTemperature";
   public static final String OTHER_TEMPERATURE = "otherTemperature";
   public static final String AMBIENT_TEMPERATURE = "ambientTemperature";
   public static final String DRUM_PRESSURE = "drumPressure";
   public static final String DRUM_SPEED = "drumSpeed";
   public static final String FAN_SPEED = "fanSpeed";
   public static final String GAS = "gas";
   public static final String GAS_PRESSURE = "gasPressure";
   public static final String AIRFLOW = "airflow";
   public static final String CUSTOM = "custom";
   public static final String CUSTOM_CONTROL = "customControl";
   public static final String CHARGE_TEMPERATURE = "chargeTemperature";
   public static final String END_TEMPERATURE = "endTemperature";
   public static final String ROAST_VALUE = "roastValue";
   public static final String ROAST_VALUE2 = "roastValue2";
   public static final String ROAST_COLOR = "roastColor";
   public static final String DURATION = "duration";
   public static final String DEVELOPMENT_TIME = "developmentTime";
   public static final String DEVELOPMENT_TIME_RATIO = "developmentTimeRatio";
   public static final String START_WEIGHT = "startWeight";
   public static final String END_WEIGHT = "endWeight";
   public static final String WEIGHT_LOSS = "weightLoss";
   public static final String SIMPLE_ROR_10 = "simpleRor10";
   public static final String SIMPLE_ROR_15 = "simpleRor15";
   public static final String SIMPLE_ROR_30 = "simpleRor30";
   public static final String SIMPLE_ROR_60 = "simpleRor60";
   public static final String BEAN_ROR = "beanRoR";
   public static final String CONSTANT_ROR = "constantRoR";
   public static final String CONSTANT_BEAN_TEMPERATURE = "constantBeanTemp";
   private static final Map<String, Unit> DEFAULT_MEASURE_UNITS = new HashMap<>();

   public static String curveNameForReference(String curveName) {
      return curveName + "Reference";
   }

   public static String curveNameForBetweenBatch(String curveName) {
      return curveName + "BetweenBatch";
   }

   public static Map<String, Unit> getDefaultMeasureUnits() {
      return Collections.unmodifiableMap(DEFAULT_MEASURE_UNITS);
   }

   public static void initDefaultMeasureUnits(Preferences preferences) {
      DEFAULT_MEASURE_UNITS.put("roastValue", preferences.getUnit(Unit.AGTRON_COMMERCIAL));
      DEFAULT_MEASURE_UNITS.put("roastValue2", preferences.getUnit(Unit.AGTRON_COMMERCIAL));
      DEFAULT_MEASURE_UNITS.put("afterburnerTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("afterburnerTemperature2", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("ambientTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("beanTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("burnerTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("drumTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("endTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("environmentalTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("exhaustTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("inletTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("otherTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("beanTemperaturePrediction", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("returnTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("stackTemperature", preferences.getUnit(Unit.CELSIUS));
      DEFAULT_MEASURE_UNITS.put("roastColor", preferences.getUnit(Unit.COLORTRACK));
      DEFAULT_MEASURE_UNITS.put("endWeight", preferences.getUnit(Unit.KG));
      DEFAULT_MEASURE_UNITS.put("startWeight", preferences.getUnit(Unit.KG));
      DEFAULT_MEASURE_UNITS.put("gasPressure", preferences.getUnit(Unit.MILLIBAR));
      DEFAULT_MEASURE_UNITS.put("drumPressure", preferences.getUnit(Unit.MILLIBAR));
      DEFAULT_MEASURE_UNITS.put("airflow", preferences.getUnit(Unit.PERCENT));
      DEFAULT_MEASURE_UNITS.put("developmentTimeRatio", preferences.getUnit(Unit.PERCENT));
      DEFAULT_MEASURE_UNITS.put("drumSpeed", preferences.getUnit(Unit.PERCENT));
      DEFAULT_MEASURE_UNITS.put("gas", preferences.getUnit(Unit.PERCENT));
      DEFAULT_MEASURE_UNITS.put("weightLoss", preferences.getUnit(Unit.PERCENT));
      DEFAULT_MEASURE_UNITS.put("developmentTime", preferences.getUnit(Unit.SECOND));
      DEFAULT_MEASURE_UNITS.put("duration", preferences.getUnit(Unit.SECOND));
   }

   private Measures() {
   }
}
