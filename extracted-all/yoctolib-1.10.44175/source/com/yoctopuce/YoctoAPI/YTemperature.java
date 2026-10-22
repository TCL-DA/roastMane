package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YTemperature extends YSensor {
   public static final int SENSORTYPE_DIGITAL = 0;
   public static final int SENSORTYPE_TYPE_K = 1;
   public static final int SENSORTYPE_TYPE_E = 2;
   public static final int SENSORTYPE_TYPE_J = 3;
   public static final int SENSORTYPE_TYPE_N = 4;
   public static final int SENSORTYPE_TYPE_R = 5;
   public static final int SENSORTYPE_TYPE_S = 6;
   public static final int SENSORTYPE_TYPE_T = 7;
   public static final int SENSORTYPE_PT100_4WIRES = 8;
   public static final int SENSORTYPE_PT100_3WIRES = 9;
   public static final int SENSORTYPE_PT100_2WIRES = 10;
   public static final int SENSORTYPE_RES_OHM = 11;
   public static final int SENSORTYPE_RES_NTC = 12;
   public static final int SENSORTYPE_RES_LINEAR = 13;
   public static final int SENSORTYPE_RES_INTERNAL = 14;
   public static final int SENSORTYPE_IR = 15;
   public static final int SENSORTYPE_RES_PT1000 = 16;
   public static final int SENSORTYPE_CHANNEL_OFF = 17;
   public static final int SENSORTYPE_INVALID = -1;
   public static final double SIGNALVALUE_INVALID = -1.79769313486231E308;
   public static final String SIGNALUNIT_INVALID = "!INVALID!";
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _sensorType = -1;
   protected double _signalValue = -1.79769313486231E308;
   protected String _signalUnit = "!INVALID!";
   protected String _command = "!INVALID!";
   protected YTemperature.UpdateCallback _valueCallbackTemperature = null;
   protected YTemperature.TimedReportCallback _timedReportCallbackTemperature = null;

   protected YTemperature(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Temperature";
   }

   protected YTemperature(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("sensorType")) {
         this._sensorType = json_val.getInt("sensorType");
      }

      if (json_val.has("signalValue")) {
         this._signalValue = (double)Math.round(json_val.getDouble("signalValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("signalUnit")) {
         this._signalUnit = json_val.getString("signalUnit");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int set_unit(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("unit", newval);
         return 0;
      }
   }

   public int setUnit(String newval) throws YAPI_Exception {
      return this.set_unit(newval);
   }

   public int get_sensorType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._sensorType;
      }
   }

   public int getSensorType() throws YAPI_Exception {
      return this.get_sensorType();
   }

   public int set_sensorType(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("sensorType", rest_val);
         return 0;
      }
   }

   public int setSensorType(int newval) throws YAPI_Exception {
      return this.set_sensorType(newval);
   }

   public double get_signalValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : (double)Math.round(this._signalValue * 1000.0) / 1000.0;
      }
   }

   public double getSignalValue() throws YAPI_Exception {
      return this.get_signalValue();
   }

   public String get_signalUnit() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._signalUnit;
      }
   }

   public String getSignalUnit() throws YAPI_Exception {
      return this.get_signalUnit();
   }

   public String get_command() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._command;
      }
   }

   public int set_command(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("command", newval);
         return 0;
      }
   }

   public static YTemperature FindTemperature(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YTemperature obj = (YTemperature)YFunction._FindFromCache("Temperature", func);
         if (obj == null) {
            obj = new YTemperature(func);
            YFunction._AddToCache("Temperature", func, obj);
         }

         return obj;
      }
   }

   public static YTemperature FindTemperatureInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YTemperature obj = (YTemperature)YFunction._FindFromCacheInContext(yctx, "Temperature", func);
         if (obj == null) {
            obj = new YTemperature(yctx, func);
            YFunction._AddToCache("Temperature", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YTemperature.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackTemperature = callback;
      if (callback != null && this.isOnline()) {
         String val = this._advertisedValue;
         if (!val.equals("")) {
            this._invokeValueCallback(val);
         }
      }

      return 0;
   }

   @Override
   public int _invokeValueCallback(String value) {
      if (this._valueCallbackTemperature != null) {
         this._valueCallbackTemperature.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YTemperature.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackTemperature = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackTemperature != null) {
         this._timedReportCallbackTemperature.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int set_ntcParameters(double res25, double beta) throws YAPI_Exception {
      ArrayList<Double> tempValues = new ArrayList<>();
      ArrayList<Double> resValues = new ArrayList<>();
      double t0 = 298.15;
      double t1 = 373.15;
      double res100 = res25 * Math.exp(beta * (1.0 / t1 - 1.0 / t0));
      tempValues.clear();
      resValues.clear();
      tempValues.add(25.0);
      resValues.add(res25);
      tempValues.add(100.0);
      resValues.add(res100);
      return this.set_thermistorResponseTable(tempValues, resValues);
   }

   public int set_thermistorResponseTable(ArrayList<Double> tempValues, ArrayList<Double> resValues) throws YAPI_Exception {
      int siz = tempValues.size();
      if (siz < 2) {
         throw new YAPI_Exception(-2, "thermistor response table must have at least two points");
      } else if (siz != resValues.size()) {
         throw new YAPI_Exception(-2, "table sizes mismatch");
      } else {
         int res = this.set_command("Z");
         if (res != 0) {
            throw new YAPI_Exception(-8, "unable to reset thermistor parameters");
         } else {
            int found = 1;
            double prev = 0.0;

            while (found > 0) {
               found = 0;
               double curr = 9.9999999E7;
               double currTemp = -999999.0;

               for (int idx = 0; idx < siz; idx++) {
                  double idxres = resValues.get(idx);
                  if (idxres > prev && idxres < curr) {
                     curr = idxres;
                     currTemp = tempValues.get(idx);
                     found = 1;
                  }
               }

               if (found > 0) {
                  res = this.set_command(
                     String.format(Locale.US, "m%d:%d", (int)((double)Math.round(1000.0 * curr)), (int)((double)Math.round(1000.0 * currTemp)))
                  );
                  if (res != 0) {
                     throw new YAPI_Exception(-8, "unable to reset thermistor parameters");
                  }

                  prev = curr;
               }
            }

            return 0;
         }
      }
   }

   public int loadThermistorResponseTable(ArrayList<Double> tempValues, ArrayList<Double> resValues) throws YAPI_Exception {
      new ArrayList();
      ArrayList<Double> templist = new ArrayList<>();
      tempValues.clear();
      resValues.clear();
      String id = this.get_functionId();
      id = id.substring(11, 11 + id.length() - 11);
      if (id.equals("")) {
         id = "1";
      }

      byte[] bin_json = this._download(String.format(Locale.US, "extra.json?page=%s", id));
      ArrayList<String> paramlist = this._json_get_array(bin_json);
      int siz = paramlist.size() >> 1;
      templist.clear();

      for (int idx = 0; idx < siz; idx++) {
         double temp = Double.valueOf((String)paramlist.get(2 * idx + 1)) / 1000.0;
         templist.add(temp);
      }

      tempValues.clear();
      resValues.clear();
      int found = 1;
      double prev = -999999.0;

      while (found > 0) {
         found = 0;
         double curr = 999999.0;
         double currRes = -999999.0;

         for (int var19 = 0; var19 < siz; var19++) {
            double temp = templist.get(var19);
            if (temp > prev && temp < curr) {
               curr = temp;
               currRes = Double.valueOf((String)paramlist.get(2 * var19)) / 1000.0;
               found = 1;
            }
         }

         if (found > 0) {
            tempValues.add(curr);
            resValues.add(currRes);
            prev = curr;
         }
      }

      return 0;
   }

   public YTemperature nextTemperature() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindTemperatureInContext(this._yapi, next_hwid);
   }

   public static YTemperature FirstTemperature() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Temperature");
         return next_hwid == null ? null : FindTemperatureInContext(yctx, next_hwid);
      }
   }

   public static YTemperature FirstTemperatureInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Temperature");
      return next_hwid == null ? null : FindTemperatureInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YTemperature var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YTemperature var1, String var2);
   }
}
