package com.yoctopuce.YoctoAPI;

public class YPressure extends YSensor {
   protected YPressure.UpdateCallback _valueCallbackPressure = null;
   protected YPressure.TimedReportCallback _timedReportCallbackPressure = null;

   protected YPressure(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Pressure";
   }

   protected YPressure(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YPressure FindPressure(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPressure obj = (YPressure)YFunction._FindFromCache("Pressure", func);
         if (obj == null) {
            obj = new YPressure(func);
            YFunction._AddToCache("Pressure", func, obj);
         }

         return obj;
      }
   }

   public static YPressure FindPressureInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPressure obj = (YPressure)YFunction._FindFromCacheInContext(yctx, "Pressure", func);
         if (obj == null) {
            obj = new YPressure(yctx, func);
            YFunction._AddToCache("Pressure", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPressure.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPressure = callback;
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
      if (this._valueCallbackPressure != null) {
         this._valueCallbackPressure.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YPressure.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackPressure = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackPressure != null) {
         this._timedReportCallbackPressure.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YPressure nextPressure() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPressureInContext(this._yapi, next_hwid);
   }

   public static YPressure FirstPressure() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Pressure");
         return next_hwid == null ? null : FindPressureInContext(yctx, next_hwid);
      }
   }

   public static YPressure FirstPressureInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Pressure");
      return next_hwid == null ? null : FindPressureInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPressure var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPressure var1, String var2);
   }
}
