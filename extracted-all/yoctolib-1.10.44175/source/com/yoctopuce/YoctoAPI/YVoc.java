package com.yoctopuce.YoctoAPI;

public class YVoc extends YSensor {
   protected YVoc.UpdateCallback _valueCallbackVoc = null;
   protected YVoc.TimedReportCallback _timedReportCallbackVoc = null;

   protected YVoc(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Voc";
   }

   protected YVoc(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      super._parseAttr(json_val);
   }

   public static YVoc FindVoc(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YVoc obj = (YVoc)YFunction._FindFromCache("Voc", func);
         if (obj == null) {
            obj = new YVoc(func);
            YFunction._AddToCache("Voc", func, obj);
         }

         return obj;
      }
   }

   public static YVoc FindVocInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YVoc obj = (YVoc)YFunction._FindFromCacheInContext(yctx, "Voc", func);
         if (obj == null) {
            obj = new YVoc(yctx, func);
            YFunction._AddToCache("Voc", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YVoc.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackVoc = callback;
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
      if (this._valueCallbackVoc != null) {
         this._valueCallbackVoc.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YVoc.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackVoc = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackVoc != null) {
         this._timedReportCallbackVoc.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YVoc nextVoc() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindVocInContext(this._yapi, next_hwid);
   }

   public static YVoc FirstVoc() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Voc");
         return next_hwid == null ? null : FindVocInContext(yctx, next_hwid);
      }
   }

   public static YVoc FirstVocInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Voc");
      return next_hwid == null ? null : FindVocInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YVoc var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YVoc var1, String var2);
   }
}
