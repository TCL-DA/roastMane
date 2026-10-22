package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;

public class YTilt extends YSensor {
   public static final int BANDWIDTH_INVALID = -1;
   public static final int AXIS_X = 0;
   public static final int AXIS_Y = 1;
   public static final int AXIS_Z = 2;
   public static final int AXIS_INVALID = -1;
   protected int _bandwidth = -1;
   protected int _axis = -1;
   protected YTilt.UpdateCallback _valueCallbackTilt = null;
   protected YTilt.TimedReportCallback _timedReportCallbackTilt = null;

   protected YTilt(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Tilt";
   }

   protected YTilt(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("bandwidth")) {
         this._bandwidth = json_val.getInt("bandwidth");
      }

      if (json_val.has("axis")) {
         this._axis = json_val.getInt("axis");
      }

      super._parseAttr(json_val);
   }

   public int get_bandwidth() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._bandwidth;
      }
   }

   public int getBandwidth() throws YAPI_Exception {
      return this.get_bandwidth();
   }

   public int set_bandwidth(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("bandwidth", rest_val);
         return 0;
      }
   }

   public int setBandwidth(int newval) throws YAPI_Exception {
      return this.set_bandwidth(newval);
   }

   public int get_axis() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._axis;
      }
   }

   public static YTilt FindTilt(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YTilt obj = (YTilt)YFunction._FindFromCache("Tilt", func);
         if (obj == null) {
            obj = new YTilt(func);
            YFunction._AddToCache("Tilt", func, obj);
         }

         return obj;
      }
   }

   public static YTilt FindTiltInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YTilt obj = (YTilt)YFunction._FindFromCacheInContext(yctx, "Tilt", func);
         if (obj == null) {
            obj = new YTilt(yctx, func);
            YFunction._AddToCache("Tilt", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YTilt.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackTilt = callback;
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
      if (this._valueCallbackTilt != null) {
         this._valueCallbackTilt.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YTilt.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackTilt = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackTilt != null) {
         this._timedReportCallbackTilt.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int calibrateToZero() throws YAPI_Exception {
      ArrayList<Double> rawVals = new ArrayList<>();
      ArrayList<Double> refVals = new ArrayList<>();
      double currentRawVal = this.get_currentRawValue();
      rawVals.clear();
      refVals.clear();
      rawVals.add(currentRawVal);
      refVals.add(0.0);
      return this.calibrateFromPoints(rawVals, refVals);
   }

   public int restoreZeroCalibration() throws YAPI_Exception {
      return this._setAttr("calibrationParam", "0");
   }

   public YTilt nextTilt() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindTiltInContext(this._yapi, next_hwid);
   }

   public static YTilt FirstTilt() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Tilt");
         return next_hwid == null ? null : FindTiltInContext(yctx, next_hwid);
      }
   }

   public static YTilt FirstTiltInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Tilt");
      return next_hwid == null ? null : FindTiltInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YTilt var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YTilt var1, String var2);
   }
}
