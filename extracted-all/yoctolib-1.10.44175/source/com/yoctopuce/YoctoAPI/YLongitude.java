package com.yoctopuce.YoctoAPI;

public class YLongitude extends YSensor {
   protected YLongitude.UpdateCallback _valueCallbackLongitude = null;
   protected YLongitude.TimedReportCallback _timedReportCallbackLongitude = null;

   protected YLongitude(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Longitude";
   }

   protected YLongitude(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YLongitude FindLongitude(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YLongitude obj = (YLongitude)YFunction._FindFromCache("Longitude", func);
         if (obj == null) {
            obj = new YLongitude(func);
            YFunction._AddToCache("Longitude", func, obj);
         }

         return obj;
      }
   }

   public static YLongitude FindLongitudeInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YLongitude obj = (YLongitude)YFunction._FindFromCacheInContext(yctx, "Longitude", func);
         if (obj == null) {
            obj = new YLongitude(yctx, func);
            YFunction._AddToCache("Longitude", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YLongitude.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackLongitude = callback;
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
      if (this._valueCallbackLongitude != null) {
         this._valueCallbackLongitude.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YLongitude.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackLongitude = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackLongitude != null) {
         this._timedReportCallbackLongitude.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YLongitude nextLongitude() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindLongitudeInContext(this._yapi, next_hwid);
   }

   public static YLongitude FirstLongitude() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Longitude");
         return next_hwid == null ? null : FindLongitudeInContext(yctx, next_hwid);
      }
   }

   public static YLongitude FirstLongitudeInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Longitude");
      return next_hwid == null ? null : FindLongitudeInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YLongitude var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YLongitude var1, String var2);
   }
}
