package com.yoctopuce.YoctoAPI;

public class YWakeUpMonitor extends YFunction {
   public static final int POWERDURATION_INVALID = -1;
   public static final int SLEEPCOUNTDOWN_INVALID = -1;
   public static final long NEXTWAKEUP_INVALID = -9223372036854775807L;
   public static final int WAKEUPREASON_USBPOWER = 0;
   public static final int WAKEUPREASON_EXTPOWER = 1;
   public static final int WAKEUPREASON_ENDOFSLEEP = 2;
   public static final int WAKEUPREASON_EXTSIG1 = 3;
   public static final int WAKEUPREASON_SCHEDULE1 = 4;
   public static final int WAKEUPREASON_SCHEDULE2 = 5;
   public static final int WAKEUPREASON_INVALID = -1;
   public static final int WAKEUPSTATE_SLEEPING = 0;
   public static final int WAKEUPSTATE_AWAKE = 1;
   public static final int WAKEUPSTATE_INVALID = -1;
   public static final long RTCTIME_INVALID = -9223372036854775807L;
   protected int _powerDuration = -1;
   protected int _sleepCountdown = -1;
   protected long _nextWakeUp = -9223372036854775807L;
   protected int _wakeUpReason = -1;
   protected int _wakeUpState = -1;
   protected long _rtcTime = -9223372036854775807L;
   public static final int _endOfTime = 2145960000;
   protected YWakeUpMonitor.UpdateCallback _valueCallbackWakeUpMonitor = null;

   protected YWakeUpMonitor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "WakeUpMonitor";
   }

   protected YWakeUpMonitor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("powerDuration")) {
         this._powerDuration = json_val.getInt("powerDuration");
      }

      if (json_val.has("sleepCountdown")) {
         this._sleepCountdown = json_val.getInt("sleepCountdown");
      }

      if (json_val.has("nextWakeUp")) {
         this._nextWakeUp = json_val.getLong("nextWakeUp");
      }

      if (json_val.has("wakeUpReason")) {
         this._wakeUpReason = json_val.getInt("wakeUpReason");
      }

      if (json_val.has("wakeUpState")) {
         this._wakeUpState = json_val.getInt("wakeUpState");
      }

      if (json_val.has("rtcTime")) {
         this._rtcTime = json_val.getLong("rtcTime");
      }

      super._parseAttr(json_val);
   }

   public int get_powerDuration() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._powerDuration;
      }
   }

   public int getPowerDuration() throws YAPI_Exception {
      return this.get_powerDuration();
   }

   public int set_powerDuration(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("powerDuration", rest_val);
         return 0;
      }
   }

   public int setPowerDuration(int newval) throws YAPI_Exception {
      return this.set_powerDuration(newval);
   }

   public int get_sleepCountdown() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._sleepCountdown;
      }
   }

   public int getSleepCountdown() throws YAPI_Exception {
      return this.get_sleepCountdown();
   }

   public int set_sleepCountdown(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("sleepCountdown", rest_val);
         return 0;
      }
   }

   public int setSleepCountdown(int newval) throws YAPI_Exception {
      return this.set_sleepCountdown(newval);
   }

   public long get_nextWakeUp() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._nextWakeUp;
      }
   }

   public long getNextWakeUp() throws YAPI_Exception {
      return this.get_nextWakeUp();
   }

   public int set_nextWakeUp(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("nextWakeUp", rest_val);
         return 0;
      }
   }

   public int setNextWakeUp(long newval) throws YAPI_Exception {
      return this.set_nextWakeUp(newval);
   }

   public int get_wakeUpReason() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._wakeUpReason;
      }
   }

   public int getWakeUpReason() throws YAPI_Exception {
      return this.get_wakeUpReason();
   }

   public int get_wakeUpState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._wakeUpState;
      }
   }

   public int getWakeUpState() throws YAPI_Exception {
      return this.get_wakeUpState();
   }

   public int set_wakeUpState(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("wakeUpState", rest_val);
         return 0;
      }
   }

   public long get_rtcTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -9223372036854775807L : this._rtcTime;
      }
   }

   public static YWakeUpMonitor FindWakeUpMonitor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YWakeUpMonitor obj = (YWakeUpMonitor)YFunction._FindFromCache("WakeUpMonitor", func);
         if (obj == null) {
            obj = new YWakeUpMonitor(func);
            YFunction._AddToCache("WakeUpMonitor", func, obj);
         }

         return obj;
      }
   }

   public static YWakeUpMonitor FindWakeUpMonitorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YWakeUpMonitor obj = (YWakeUpMonitor)YFunction._FindFromCacheInContext(yctx, "WakeUpMonitor", func);
         if (obj == null) {
            obj = new YWakeUpMonitor(yctx, func);
            YFunction._AddToCache("WakeUpMonitor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YWakeUpMonitor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackWakeUpMonitor = callback;
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
      if (this._valueCallbackWakeUpMonitor != null) {
         this._valueCallbackWakeUpMonitor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int wakeUp() throws YAPI_Exception {
      return this.set_wakeUpState(1);
   }

   public int sleep(int secBeforeSleep) throws YAPI_Exception {
      int currTime = (int)this.get_rtcTime();
      if (currTime == 0) {
         throw new YAPI_Exception(-13, "RTC time not set");
      } else {
         this.set_nextWakeUp(2145960000L);
         this.set_sleepCountdown(secBeforeSleep);
         return 0;
      }
   }

   public int sleepFor(int secUntilWakeUp, int secBeforeSleep) throws YAPI_Exception {
      int currTime = (int)this.get_rtcTime();
      if (currTime == 0) {
         throw new YAPI_Exception(-13, "RTC time not set");
      } else {
         this.set_nextWakeUp((long)(currTime + secUntilWakeUp));
         this.set_sleepCountdown(secBeforeSleep);
         return 0;
      }
   }

   public int sleepUntil(int wakeUpTime, int secBeforeSleep) throws YAPI_Exception {
      int currTime = (int)this.get_rtcTime();
      if (currTime == 0) {
         throw new YAPI_Exception(-13, "RTC time not set");
      } else {
         this.set_nextWakeUp((long)wakeUpTime);
         this.set_sleepCountdown(secBeforeSleep);
         return 0;
      }
   }

   public int resetSleepCountDown() throws YAPI_Exception {
      this.set_sleepCountdown(0);
      this.set_nextWakeUp(0L);
      return 0;
   }

   public YWakeUpMonitor nextWakeUpMonitor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindWakeUpMonitorInContext(this._yapi, next_hwid);
   }

   public static YWakeUpMonitor FirstWakeUpMonitor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("WakeUpMonitor");
         return next_hwid == null ? null : FindWakeUpMonitorInContext(yctx, next_hwid);
      }
   }

   public static YWakeUpMonitor FirstWakeUpMonitorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("WakeUpMonitor");
      return next_hwid == null ? null : FindWakeUpMonitorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YWakeUpMonitor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YWakeUpMonitor var1, String var2);
   }
}
