package com.yoctopuce.YoctoAPI;

public class YPower extends YSensor {
   public static final double COSPHI_INVALID = -1.79769313486231E308;
   public static final double METER_INVALID = -1.79769313486231E308;
   public static final double DELIVEREDENERGYMETER_INVALID = -1.79769313486231E308;
   public static final double RECEIVEDENERGYMETER_INVALID = -1.79769313486231E308;
   public static final int METERTIMER_INVALID = -1;
   protected double _cosPhi = -1.79769313486231E308;
   protected double _meter = -1.79769313486231E308;
   protected double _deliveredEnergyMeter = -1.79769313486231E308;
   protected double _receivedEnergyMeter = -1.79769313486231E308;
   protected int _meterTimer = -1;
   protected YPower.UpdateCallback _valueCallbackPower = null;
   protected YPower.TimedReportCallback _timedReportCallbackPower = null;

   protected YPower(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Power";
   }

   protected YPower(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("cosPhi")) {
         this._cosPhi = (double)Math.round(json_val.getDouble("cosPhi") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("meter")) {
         this._meter = (double)Math.round(json_val.getDouble("meter") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("deliveredEnergyMeter")) {
         this._deliveredEnergyMeter = (double)Math.round(json_val.getDouble("deliveredEnergyMeter") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("receivedEnergyMeter")) {
         this._receivedEnergyMeter = (double)Math.round(json_val.getDouble("receivedEnergyMeter") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("meterTimer")) {
         this._meterTimer = json_val.getInt("meterTimer");
      }

      super._parseAttr(json_val);
   }

   public double get_cosPhi() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._cosPhi;
      }
   }

   public double getCosPhi() throws YAPI_Exception {
      return this.get_cosPhi();
   }

   public int set_meter(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("meter", rest_val);
         return 0;
      }
   }

   public double get_meter() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._meter;
      }
   }

   public double getMeter() throws YAPI_Exception {
      return this.get_meter();
   }

   public double get_deliveredEnergyMeter() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._deliveredEnergyMeter;
      }
   }

   public double getDeliveredEnergyMeter() throws YAPI_Exception {
      return this.get_deliveredEnergyMeter();
   }

   public double get_receivedEnergyMeter() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._receivedEnergyMeter;
      }
   }

   public double getReceivedEnergyMeter() throws YAPI_Exception {
      return this.get_receivedEnergyMeter();
   }

   public int get_meterTimer() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._meterTimer;
      }
   }

   public int getMeterTimer() throws YAPI_Exception {
      return this.get_meterTimer();
   }

   public static YPower FindPower(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPower obj = (YPower)YFunction._FindFromCache("Power", func);
         if (obj == null) {
            obj = new YPower(func);
            YFunction._AddToCache("Power", func, obj);
         }

         return obj;
      }
   }

   public static YPower FindPowerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPower obj = (YPower)YFunction._FindFromCacheInContext(yctx, "Power", func);
         if (obj == null) {
            obj = new YPower(yctx, func);
            YFunction._AddToCache("Power", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPower.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPower = callback;
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
      if (this._valueCallbackPower != null) {
         this._valueCallbackPower.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YPower.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackPower = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackPower != null) {
         this._timedReportCallbackPower.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int reset() throws YAPI_Exception {
      return this.set_meter(0.0);
   }

   public YPower nextPower() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPowerInContext(this._yapi, next_hwid);
   }

   public static YPower FirstPower() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Power");
         return next_hwid == null ? null : FindPowerInContext(yctx, next_hwid);
      }
   }

   public static YPower FirstPowerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Power");
      return next_hwid == null ? null : FindPowerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPower var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPower var1, String var2);
   }
}
