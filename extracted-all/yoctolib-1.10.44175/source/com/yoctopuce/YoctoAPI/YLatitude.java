package com.yoctopuce.YoctoAPI;

public class YLatitude extends YSensor {
   protected YLatitude.UpdateCallback _valueCallbackLatitude = null;
   protected YLatitude.TimedReportCallback _timedReportCallbackLatitude = null;

   protected YLatitude(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Latitude";
   }

   protected YLatitude(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YLatitude FindLatitude(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YLatitude obj = (YLatitude)YFunction._FindFromCache("Latitude", func);
         if (obj == null) {
            obj = new YLatitude(func);
            YFunction._AddToCache("Latitude", func, obj);
         }

         return obj;
      }
   }

   public static YLatitude FindLatitudeInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YLatitude obj = (YLatitude)YFunction._FindFromCacheInContext(yctx, "Latitude", func);
         if (obj == null) {
            obj = new YLatitude(yctx, func);
            YFunction._AddToCache("Latitude", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YLatitude.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackLatitude = callback;
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
      if (this._valueCallbackLatitude != null) {
         this._valueCallbackLatitude.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YLatitude.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackLatitude = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackLatitude != null) {
         this._timedReportCallbackLatitude.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YLatitude nextLatitude() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindLatitudeInContext(this._yapi, next_hwid);
   }

   public static YLatitude FirstLatitude() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Latitude");
         return next_hwid == null ? null : FindLatitudeInContext(yctx, next_hwid);
      }
   }

   public static YLatitude FirstLatitudeInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Latitude");
      return next_hwid == null ? null : FindLatitudeInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YLatitude var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YLatitude var1, String var2);
   }
}
