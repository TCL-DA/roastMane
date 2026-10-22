package com.yoctopuce.YoctoAPI;

public class YProximity extends YSensor {
   public static final double SIGNALVALUE_INVALID = -1.79769313486231E308;
   public static final int DETECTIONTHRESHOLD_INVALID = -1;
   public static final int DETECTIONHYSTERESIS_INVALID = -1;
   public static final int PRESENCEMINTIME_INVALID = -1;
   public static final int REMOVALMINTIME_INVALID = -1;
   public static final int ISPRESENT_FALSE = 0;
   public static final int ISPRESENT_TRUE = 1;
   public static final int ISPRESENT_INVALID = -1;
   public static final long LASTTIMEAPPROACHED_INVALID = -9223372036854775807L;
   public static final long LASTTIMEREMOVED_INVALID = -9223372036854775807L;
   public static final long PULSECOUNTER_INVALID = -9223372036854775807L;
   public static final long PULSETIMER_INVALID = -9223372036854775807L;
   public static final int PROXIMITYREPORTMODE_NUMERIC = 0;
   public static final int PROXIMITYREPORTMODE_PRESENCE = 1;
   public static final int PROXIMITYREPORTMODE_PULSECOUNT = 2;
   public static final int PROXIMITYREPORTMODE_INVALID = -1;
   protected double _signalValue = -1.79769313486231E308;
   protected int _detectionThreshold = -1;
   protected int _detectionHysteresis = -1;
   protected int _presenceMinTime = -1;
   protected int _removalMinTime = -1;
   protected int _isPresent = -1;
   protected long _lastTimeApproached = -9223372036854775807L;
   protected long _lastTimeRemoved = -9223372036854775807L;
   protected long _pulseCounter = -9223372036854775807L;
   protected long _pulseTimer = -9223372036854775807L;
   protected int _proximityReportMode = -1;
   protected YProximity.UpdateCallback _valueCallbackProximity = null;
   protected YProximity.TimedReportCallback _timedReportCallbackProximity = null;

   protected YProximity(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Proximity";
   }

   protected YProximity(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("signalValue")) {
         this._signalValue = (double)Math.round(json_val.getDouble("signalValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("detectionThreshold")) {
         this._detectionThreshold = json_val.getInt("detectionThreshold");
      }

      if (json_val.has("detectionHysteresis")) {
         this._detectionHysteresis = json_val.getInt("detectionHysteresis");
      }

      if (json_val.has("presenceMinTime")) {
         this._presenceMinTime = json_val.getInt("presenceMinTime");
      }

      if (json_val.has("removalMinTime")) {
         this._removalMinTime = json_val.getInt("removalMinTime");
      }

      if (json_val.has("isPresent")) {
         this._isPresent = json_val.getInt("isPresent") > 0 ? 1 : 0;
      }

      if (json_val.has("lastTimeApproached")) {
         this._lastTimeApproached = json_val.getLong("lastTimeApproached");
      }

      if (json_val.has("lastTimeRemoved")) {
         this._lastTimeRemoved = json_val.getLong("lastTimeRemoved");
      }

      if (json_val.has("pulseCounter")) {
         this._pulseCounter = json_val.getLong("pulseCounter");
      }

      if (json_val.has("pulseTimer")) {
         this._pulseTimer = json_val.getLong("pulseTimer");
      }

      if (json_val.has("proximityReportMode")) {
         this._proximityReportMode = json_val.getInt("proximityReportMode");
      }

      super._parseAttr(json_val);
   }

   public double get_signalValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : (double)Math.round(this._signalValue * 1000.0) / 1000.0;
      }
   }

   public double getSignalValue() throws YAPI_Exception {
      return this.get_signalValue();
   }

   public int get_detectionThreshold() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._detectionThreshold;
      }
   }

   public int getDetectionThreshold() throws YAPI_Exception {
      return this.get_detectionThreshold();
   }

   public int set_detectionThreshold(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("detectionThreshold", rest_val);
         return 0;
      }
   }

   public int setDetectionThreshold(int newval) throws YAPI_Exception {
      return this.set_detectionThreshold(newval);
   }

   public int get_detectionHysteresis() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._detectionHysteresis;
      }
   }

   public int getDetectionHysteresis() throws YAPI_Exception {
      return this.get_detectionHysteresis();
   }

   public int set_detectionHysteresis(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("detectionHysteresis", rest_val);
         return 0;
      }
   }

   public int setDetectionHysteresis(int newval) throws YAPI_Exception {
      return this.set_detectionHysteresis(newval);
   }

   public int get_presenceMinTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._presenceMinTime;
      }
   }

   public int getPresenceMinTime() throws YAPI_Exception {
      return this.get_presenceMinTime();
   }

   public int set_presenceMinTime(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("presenceMinTime", rest_val);
         return 0;
      }
   }

   public int setPresenceMinTime(int newval) throws YAPI_Exception {
      return this.set_presenceMinTime(newval);
   }

   public int get_removalMinTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._removalMinTime;
      }
   }

   public int getRemovalMinTime() throws YAPI_Exception {
      return this.get_removalMinTime();
   }

   public int set_removalMinTime(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("removalMinTime", rest_val);
         return 0;
      }
   }

   public int setRemovalMinTime(int newval) throws YAPI_Exception {
      return this.set_removalMinTime(newval);
   }

   public int get_isPresent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._isPresent;
      }
   }

   public int getIsPresent() throws YAPI_Exception {
      return this.get_isPresent();
   }

   public long get_lastTimeApproached() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._lastTimeApproached;
      }
   }

   public long getLastTimeApproached() throws YAPI_Exception {
      return this.get_lastTimeApproached();
   }

   public long get_lastTimeRemoved() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._lastTimeRemoved;
      }
   }

   public long getLastTimeRemoved() throws YAPI_Exception {
      return this.get_lastTimeRemoved();
   }

   public long get_pulseCounter() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._pulseCounter;
      }
   }

   public long getPulseCounter() throws YAPI_Exception {
      return this.get_pulseCounter();
   }

   public int set_pulseCounter(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("pulseCounter", rest_val);
         return 0;
      }
   }

   public long get_pulseTimer() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._pulseTimer;
      }
   }

   public long getPulseTimer() throws YAPI_Exception {
      return this.get_pulseTimer();
   }

   public int get_proximityReportMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._proximityReportMode;
      }
   }

   public int getProximityReportMode() throws YAPI_Exception {
      return this.get_proximityReportMode();
   }

   public int set_proximityReportMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("proximityReportMode", rest_val);
         return 0;
      }
   }

   public int setProximityReportMode(int newval) throws YAPI_Exception {
      return this.set_proximityReportMode(newval);
   }

   public static YProximity FindProximity(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YProximity obj = (YProximity)YFunction._FindFromCache("Proximity", func);
         if (obj == null) {
            obj = new YProximity(func);
            YFunction._AddToCache("Proximity", func, obj);
         }

         return obj;
      }
   }

   public static YProximity FindProximityInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YProximity obj = (YProximity)YFunction._FindFromCacheInContext(yctx, "Proximity", func);
         if (obj == null) {
            obj = new YProximity(yctx, func);
            YFunction._AddToCache("Proximity", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YProximity.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackProximity = callback;
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
      if (this._valueCallbackProximity != null) {
         this._valueCallbackProximity.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YProximity.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackProximity = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackProximity != null) {
         this._timedReportCallbackProximity.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int resetCounter() throws YAPI_Exception {
      return this.set_pulseCounter(0L);
   }

   public YProximity nextProximity() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindProximityInContext(this._yapi, next_hwid);
   }

   public static YProximity FirstProximity() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Proximity");
         return next_hwid == null ? null : FindProximityInContext(yctx, next_hwid);
      }
   }

   public static YProximity FirstProximityInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Proximity");
      return next_hwid == null ? null : FindProximityInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YProximity var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YProximity var1, String var2);
   }
}
