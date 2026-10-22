package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YCarbonDioxide extends YSensor {
   public static final int ABCPERIOD_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _abcPeriod = -1;
   protected String _command = "!INVALID!";
   protected YCarbonDioxide.UpdateCallback _valueCallbackCarbonDioxide = null;
   protected YCarbonDioxide.TimedReportCallback _timedReportCallbackCarbonDioxide = null;

   protected YCarbonDioxide(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "CarbonDioxide";
   }

   protected YCarbonDioxide(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("abcPeriod")) {
         this._abcPeriod = json_val.getInt("abcPeriod");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_abcPeriod() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._abcPeriod;
      }
   }

   public int getAbcPeriod() throws YAPI_Exception {
      return this.get_abcPeriod();
   }

   public int set_abcPeriod(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("abcPeriod", rest_val);
         return 0;
      }
   }

   public int setAbcPeriod(int newval) throws YAPI_Exception {
      return this.set_abcPeriod(newval);
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

   public static YCarbonDioxide FindCarbonDioxide(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YCarbonDioxide obj = (YCarbonDioxide)YFunction._FindFromCache("CarbonDioxide", func);
         if (obj == null) {
            obj = new YCarbonDioxide(func);
            YFunction._AddToCache("CarbonDioxide", func, obj);
         }

         return obj;
      }
   }

   public static YCarbonDioxide FindCarbonDioxideInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YCarbonDioxide obj = (YCarbonDioxide)YFunction._FindFromCacheInContext(yctx, "CarbonDioxide", func);
         if (obj == null) {
            obj = new YCarbonDioxide(yctx, func);
            YFunction._AddToCache("CarbonDioxide", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YCarbonDioxide.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackCarbonDioxide = callback;
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
      if (this._valueCallbackCarbonDioxide != null) {
         this._valueCallbackCarbonDioxide.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YCarbonDioxide.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackCarbonDioxide = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackCarbonDioxide != null) {
         this._timedReportCallbackCarbonDioxide.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int triggerForcedCalibration(double refVal) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "F%dC", (int)((double)Math.round(1000.0 * refVal))));
   }

   public int triggerBaselineCalibration() throws YAPI_Exception {
      return this.set_command("BC");
   }

   public int triggetBaselineCalibration() throws YAPI_Exception {
      return this.triggerBaselineCalibration();
   }

   public int triggerZeroCalibration() throws YAPI_Exception {
      return this.set_command("ZC");
   }

   public int triggetZeroCalibration() throws YAPI_Exception {
      return this.triggerZeroCalibration();
   }

   public YCarbonDioxide nextCarbonDioxide() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindCarbonDioxideInContext(this._yapi, next_hwid);
   }

   public static YCarbonDioxide FirstCarbonDioxide() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("CarbonDioxide");
         return next_hwid == null ? null : FindCarbonDioxideInContext(yctx, next_hwid);
      }
   }

   public static YCarbonDioxide FirstCarbonDioxideInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("CarbonDioxide");
      return next_hwid == null ? null : FindCarbonDioxideInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YCarbonDioxide var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YCarbonDioxide var1, String var2);
   }
}
