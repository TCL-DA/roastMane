package com.yoctopuce.YoctoAPI;

public class YGroundSpeed extends YSensor {
   protected YGroundSpeed.UpdateCallback _valueCallbackGroundSpeed = null;
   protected YGroundSpeed.TimedReportCallback _timedReportCallbackGroundSpeed = null;

   protected YGroundSpeed(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "GroundSpeed";
   }

   protected YGroundSpeed(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YGroundSpeed FindGroundSpeed(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YGroundSpeed obj = (YGroundSpeed)YFunction._FindFromCache("GroundSpeed", func);
         if (obj == null) {
            obj = new YGroundSpeed(func);
            YFunction._AddToCache("GroundSpeed", func, obj);
         }

         return obj;
      }
   }

   public static YGroundSpeed FindGroundSpeedInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YGroundSpeed obj = (YGroundSpeed)YFunction._FindFromCacheInContext(yctx, "GroundSpeed", func);
         if (obj == null) {
            obj = new YGroundSpeed(yctx, func);
            YFunction._AddToCache("GroundSpeed", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YGroundSpeed.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackGroundSpeed = callback;
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
      if (this._valueCallbackGroundSpeed != null) {
         this._valueCallbackGroundSpeed.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YGroundSpeed.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackGroundSpeed = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackGroundSpeed != null) {
         this._timedReportCallbackGroundSpeed.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YGroundSpeed nextGroundSpeed() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindGroundSpeedInContext(this._yapi, next_hwid);
   }

   public static YGroundSpeed FirstGroundSpeed() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("GroundSpeed");
         return next_hwid == null ? null : FindGroundSpeedInContext(yctx, next_hwid);
      }
   }

   public static YGroundSpeed FirstGroundSpeedInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("GroundSpeed");
      return next_hwid == null ? null : FindGroundSpeedInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YGroundSpeed var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YGroundSpeed var1, String var2);
   }
}
