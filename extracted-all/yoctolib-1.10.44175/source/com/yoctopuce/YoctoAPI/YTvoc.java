package com.yoctopuce.YoctoAPI;

public class YTvoc extends YSensor {
   protected YTvoc.UpdateCallback _valueCallbackTvoc = null;
   protected YTvoc.TimedReportCallback _timedReportCallbackTvoc = null;

   protected YTvoc(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Tvoc";
   }

   protected YTvoc(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YTvoc FindTvoc(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YTvoc obj = (YTvoc)YFunction._FindFromCache("Tvoc", func);
         if (obj == null) {
            obj = new YTvoc(func);
            YFunction._AddToCache("Tvoc", func, obj);
         }

         return obj;
      }
   }

   public static YTvoc FindTvocInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YTvoc obj = (YTvoc)YFunction._FindFromCacheInContext(yctx, "Tvoc", func);
         if (obj == null) {
            obj = new YTvoc(yctx, func);
            YFunction._AddToCache("Tvoc", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YTvoc.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackTvoc = callback;
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
      if (this._valueCallbackTvoc != null) {
         this._valueCallbackTvoc.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YTvoc.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackTvoc = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackTvoc != null) {
         this._timedReportCallbackTvoc.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YTvoc nextTvoc() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindTvocInContext(this._yapi, next_hwid);
   }

   public static YTvoc FirstTvoc() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Tvoc");
         return next_hwid == null ? null : FindTvocInContext(yctx, next_hwid);
      }
   }

   public static YTvoc FirstTvocInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Tvoc");
      return next_hwid == null ? null : FindTvocInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YTvoc var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YTvoc var1, String var2);
   }
}
