package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YRangeFinder extends YSensor {
   public static final int RANGEFINDERMODE_DEFAULT = 0;
   public static final int RANGEFINDERMODE_LONG_RANGE = 1;
   public static final int RANGEFINDERMODE_HIGH_ACCURACY = 2;
   public static final int RANGEFINDERMODE_HIGH_SPEED = 3;
   public static final int RANGEFINDERMODE_INVALID = -1;
   public static final long TIMEFRAME_INVALID = -9223372036854775807L;
   public static final int QUALITY_INVALID = -1;
   public static final String HARDWARECALIBRATION_INVALID = "!INVALID!";
   public static final double CURRENTTEMPERATURE_INVALID = -1.79769313486231E308;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _rangeFinderMode = -1;
   protected long _timeFrame = -9223372036854775807L;
   protected int _quality = -1;
   protected String _hardwareCalibration = "!INVALID!";
   protected double _currentTemperature = -1.79769313486231E308;
   protected String _command = "!INVALID!";
   protected YRangeFinder.UpdateCallback _valueCallbackRangeFinder = null;
   protected YRangeFinder.TimedReportCallback _timedReportCallbackRangeFinder = null;

   protected YRangeFinder(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "RangeFinder";
   }

   protected YRangeFinder(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("rangeFinderMode")) {
         this._rangeFinderMode = json_val.getInt("rangeFinderMode");
      }

      if (json_val.has("timeFrame")) {
         this._timeFrame = json_val.getLong("timeFrame");
      }

      if (json_val.has("quality")) {
         this._quality = json_val.getInt("quality");
      }

      if (json_val.has("hardwareCalibration")) {
         this._hardwareCalibration = json_val.getString("hardwareCalibration");
      }

      if (json_val.has("currentTemperature")) {
         this._currentTemperature = (double)Math.round(json_val.getDouble("currentTemperature") * 1000.0 / 65536.0) / 1000.0;
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

   public int get_rangeFinderMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rangeFinderMode;
      }
   }

   public int getRangeFinderMode() throws YAPI_Exception {
      return this.get_rangeFinderMode();
   }

   public int set_rangeFinderMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("rangeFinderMode", rest_val);
         return 0;
      }
   }

   public int setRangeFinderMode(int newval) throws YAPI_Exception {
      return this.set_rangeFinderMode(newval);
   }

   public long get_timeFrame() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._timeFrame;
      }
   }

   public long getTimeFrame() throws YAPI_Exception {
      return this.get_timeFrame();
   }

   public int set_timeFrame(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("timeFrame", rest_val);
         return 0;
      }
   }

   public int setTimeFrame(long newval) throws YAPI_Exception {
      return this.set_timeFrame(newval);
   }

   public int get_quality() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._quality;
      }
   }

   public int getQuality() throws YAPI_Exception {
      return this.get_quality();
   }

   public String get_hardwareCalibration() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? "!INVALID!"
            : this._hardwareCalibration;
      }
   }

   public int set_hardwareCalibration(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("hardwareCalibration", newval);
         return 0;
      }
   }

   public double get_currentTemperature() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentTemperature;
      }
   }

   public double getCurrentTemperature() throws YAPI_Exception {
      return this.get_currentTemperature();
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

   public static YRangeFinder FindRangeFinder(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YRangeFinder obj = (YRangeFinder)YFunction._FindFromCache("RangeFinder", func);
         if (obj == null) {
            obj = new YRangeFinder(func);
            YFunction._AddToCache("RangeFinder", func, obj);
         }

         return obj;
      }
   }

   public static YRangeFinder FindRangeFinderInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YRangeFinder obj = (YRangeFinder)YFunction._FindFromCacheInContext(yctx, "RangeFinder", func);
         if (obj == null) {
            obj = new YRangeFinder(yctx, func);
            YFunction._AddToCache("RangeFinder", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YRangeFinder.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackRangeFinder = callback;
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
      if (this._valueCallbackRangeFinder != null) {
         this._valueCallbackRangeFinder.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YRangeFinder.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackRangeFinder = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackRangeFinder != null) {
         this._timedReportCallbackRangeFinder.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public double get_hardwareCalibrationTemperature() throws YAPI_Exception {
      String hwcal = this.get_hardwareCalibration();
      return !hwcal.substring(0, 1).equals("@") ? -1.79769313486231E308 : (double)YAPIContext._atoi(hwcal.substring(1, 1 + hwcal.length()));
   }

   public int triggerTemperatureCalibration() throws YAPI_Exception {
      return this.set_command("T");
   }

   public int triggerSpadCalibration() throws YAPI_Exception {
      return this.set_command("S");
   }

   public int triggerOffsetCalibration(double targetDist) throws YAPI_Exception {
      int distmm;
      if (this.get_unit().equals("\"")) {
         distmm = (int)((double)Math.round(targetDist * 25.4));
      } else {
         distmm = (int)((double)Math.round(targetDist));
      }

      return this.set_command(String.format(Locale.US, "O%d", distmm));
   }

   public int triggerXTalkCalibration(double targetDist) throws YAPI_Exception {
      int distmm;
      if (this.get_unit().equals("\"")) {
         distmm = (int)((double)Math.round(targetDist * 25.4));
      } else {
         distmm = (int)((double)Math.round(targetDist));
      }

      return this.set_command(String.format(Locale.US, "X%d", distmm));
   }

   public int cancelCoverGlassCalibrations() throws YAPI_Exception {
      return this.set_hardwareCalibration("");
   }

   public YRangeFinder nextRangeFinder() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindRangeFinderInContext(this._yapi, next_hwid);
   }

   public static YRangeFinder FirstRangeFinder() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("RangeFinder");
         return next_hwid == null ? null : FindRangeFinderInContext(yctx, next_hwid);
      }
   }

   public static YRangeFinder FirstRangeFinderInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("RangeFinder");
      return next_hwid == null ? null : FindRangeFinderInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YRangeFinder var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YRangeFinder var1, String var2);
   }
}
