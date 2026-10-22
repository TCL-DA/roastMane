package com.yoctopuce.YoctoAPI;

public class YQt extends YSensor {
   protected YQt.UpdateCallback _valueCallbackQt = null;
   protected YQt.TimedReportCallback _timedReportCallbackQt = null;

   protected YQt(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Qt";
   }

   protected YQt(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YQt FindQt(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YQt obj = (YQt)YFunction._FindFromCache("Qt", func);
         if (obj == null) {
            obj = new YQt(func);
            YFunction._AddToCache("Qt", func, obj);
         }

         return obj;
      }
   }

   public static YQt FindQtInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YQt obj = (YQt)YFunction._FindFromCacheInContext(yctx, "Qt", func);
         if (obj == null) {
            obj = new YQt(yctx, func);
            YFunction._AddToCache("Qt", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YQt.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackQt = callback;
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
      if (this._valueCallbackQt != null) {
         this._valueCallbackQt.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YQt.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackQt = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackQt != null) {
         this._timedReportCallbackQt.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YQt nextQt() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindQtInContext(this._yapi, next_hwid);
   }

   public static YQt FirstQt() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Qt");
         return next_hwid == null ? null : FindQtInContext(yctx, next_hwid);
      }
   }

   public static YQt FirstQtInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Qt");
      return next_hwid == null ? null : FindQtInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YQt var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YQt var1, String var2);
   }
}
