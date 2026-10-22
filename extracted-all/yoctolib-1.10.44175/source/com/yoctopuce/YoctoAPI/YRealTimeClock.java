package com.yoctopuce.YoctoAPI;

public class YRealTimeClock extends YFunction {
   public static final long UNIXTIME_INVALID = -9223372036854775807L;
   public static final String DATETIME_INVALID = "!INVALID!";
   public static final int UTCOFFSET_INVALID = Integer.MIN_VALUE;
   public static final int TIMESET_FALSE = 0;
   public static final int TIMESET_TRUE = 1;
   public static final int TIMESET_INVALID = -1;
   protected long _unixTime = -9223372036854775807L;
   protected String _dateTime = "!INVALID!";
   protected int _utcOffset = Integer.MIN_VALUE;
   protected int _timeSet = -1;
   protected YRealTimeClock.UpdateCallback _valueCallbackRealTimeClock = null;

   protected YRealTimeClock(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "RealTimeClock";
   }

   protected YRealTimeClock(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("unixTime")) {
         this._unixTime = json_val.getLong("unixTime");
      }

      if (json_val.has("dateTime")) {
         this._dateTime = json_val.getString("dateTime");
      }

      if (json_val.has("utcOffset")) {
         this._utcOffset = json_val.getInt("utcOffset");
      }

      if (json_val.has("timeSet")) {
         this._timeSet = json_val.getInt("timeSet") > 0 ? 1 : 0;
      }

      super._parseAttr(json_val);
   }

   public long get_unixTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._unixTime;
      }
   }

   public long getUnixTime() throws YAPI_Exception {
      return this.get_unixTime();
   }

   public int set_unixTime(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("unixTime", rest_val);
         return 0;
      }
   }

   public int setUnixTime(long newval) throws YAPI_Exception {
      return this.set_unixTime(newval);
   }

   public String get_dateTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._dateTime;
      }
   }

   public String getDateTime() throws YAPI_Exception {
      return this.get_dateTime();
   }

   public int get_utcOffset() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._utcOffset;
      }
   }

   public int getUtcOffset() throws YAPI_Exception {
      return this.get_utcOffset();
   }

   public int set_utcOffset(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("utcOffset", rest_val);
         return 0;
      }
   }

   public int setUtcOffset(int newval) throws YAPI_Exception {
      return this.set_utcOffset(newval);
   }

   public int get_timeSet() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._timeSet;
      }
   }

   public int getTimeSet() throws YAPI_Exception {
      return this.get_timeSet();
   }

   public static YRealTimeClock FindRealTimeClock(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YRealTimeClock obj = (YRealTimeClock)YFunction._FindFromCache("RealTimeClock", func);
         if (obj == null) {
            obj = new YRealTimeClock(func);
            YFunction._AddToCache("RealTimeClock", func, obj);
         }

         return obj;
      }
   }

   public static YRealTimeClock FindRealTimeClockInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YRealTimeClock obj = (YRealTimeClock)YFunction._FindFromCacheInContext(yctx, "RealTimeClock", func);
         if (obj == null) {
            obj = new YRealTimeClock(yctx, func);
            YFunction._AddToCache("RealTimeClock", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YRealTimeClock.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackRealTimeClock = callback;
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
      if (this._valueCallbackRealTimeClock != null) {
         this._valueCallbackRealTimeClock.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YRealTimeClock nextRealTimeClock() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindRealTimeClockInContext(this._yapi, next_hwid);
   }

   public static YRealTimeClock FirstRealTimeClock() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("RealTimeClock");
         return next_hwid == null ? null : FindRealTimeClockInContext(yctx, next_hwid);
      }
   }

   public static YRealTimeClock FirstRealTimeClockInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("RealTimeClock");
      return next_hwid == null ? null : FindRealTimeClockInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YRealTimeClock var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YRealTimeClock var1, String var2);
   }
}
