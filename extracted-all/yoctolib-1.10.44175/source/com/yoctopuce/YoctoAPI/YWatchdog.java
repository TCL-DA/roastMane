package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YWatchdog extends YFunction {
   public static final int STATE_A = 0;
   public static final int STATE_B = 1;
   public static final int STATE_INVALID = -1;
   public static final int STATEATPOWERON_UNCHANGED = 0;
   public static final int STATEATPOWERON_A = 1;
   public static final int STATEATPOWERON_B = 2;
   public static final int STATEATPOWERON_INVALID = -1;
   public static final long MAXTIMEONSTATEA_INVALID = -9223372036854775807L;
   public static final long MAXTIMEONSTATEB_INVALID = -9223372036854775807L;
   public static final int OUTPUT_OFF = 0;
   public static final int OUTPUT_ON = 1;
   public static final int OUTPUT_INVALID = -1;
   public static final long PULSETIMER_INVALID = -9223372036854775807L;
   public static final long COUNTDOWN_INVALID = -9223372036854775807L;
   public static final int AUTOSTART_OFF = 0;
   public static final int AUTOSTART_ON = 1;
   public static final int AUTOSTART_INVALID = -1;
   public static final int RUNNING_OFF = 0;
   public static final int RUNNING_ON = 1;
   public static final int RUNNING_INVALID = -1;
   public static final long TRIGGERDELAY_INVALID = -9223372036854775807L;
   public static final long TRIGGERDURATION_INVALID = -9223372036854775807L;
   public static final YWatchdog.YDelayedPulse DELAYEDPULSETIMER_INVALID = null;
   protected int _state = -1;
   protected int _stateAtPowerOn = -1;
   protected long _maxTimeOnStateA = -9223372036854775807L;
   protected long _maxTimeOnStateB = -9223372036854775807L;
   protected int _output = -1;
   protected long _pulseTimer = -9223372036854775807L;
   protected YWatchdog.YDelayedPulse _delayedPulseTimer = new YWatchdog.YDelayedPulse();
   protected long _countdown = -9223372036854775807L;
   protected int _autoStart = -1;
   protected int _running = -1;
   protected long _triggerDelay = -9223372036854775807L;
   protected long _triggerDuration = -9223372036854775807L;
   protected YWatchdog.UpdateCallback _valueCallbackWatchdog = null;
   protected int _firm = 0;

   protected YWatchdog(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Watchdog";
   }

   protected YWatchdog(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("state")) {
         this._state = json_val.getInt("state") > 0 ? 1 : 0;
      }

      if (json_val.has("stateAtPowerOn")) {
         this._stateAtPowerOn = json_val.getInt("stateAtPowerOn");
      }

      if (json_val.has("maxTimeOnStateA")) {
         this._maxTimeOnStateA = json_val.getLong("maxTimeOnStateA");
      }

      if (json_val.has("maxTimeOnStateB")) {
         this._maxTimeOnStateB = json_val.getLong("maxTimeOnStateB");
      }

      if (json_val.has("output")) {
         this._output = json_val.getInt("output") > 0 ? 1 : 0;
      }

      if (json_val.has("pulseTimer")) {
         this._pulseTimer = json_val.getLong("pulseTimer");
      }

      if (json_val.has("delayedPulseTimer")) {
         YJSONObject subjson = json_val.getYJSONObject("delayedPulseTimer");
         if (subjson.has("moving")) {
            this._delayedPulseTimer.moving = subjson.getInt("moving");
         }

         if (subjson.has("target")) {
            this._delayedPulseTimer.target = subjson.getInt("target");
         }

         if (subjson.has("ms")) {
            this._delayedPulseTimer.ms = subjson.getInt("ms");
         }
      }

      if (json_val.has("countdown")) {
         this._countdown = json_val.getLong("countdown");
      }

      if (json_val.has("autoStart")) {
         this._autoStart = json_val.getInt("autoStart") > 0 ? 1 : 0;
      }

      if (json_val.has("running")) {
         this._running = json_val.getInt("running") > 0 ? 1 : 0;
      }

      if (json_val.has("triggerDelay")) {
         this._triggerDelay = json_val.getLong("triggerDelay");
      }

      if (json_val.has("triggerDuration")) {
         this._triggerDuration = json_val.getLong("triggerDuration");
      }

      super._parseAttr(json_val);
   }

   public int get_state() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._state;
      }
   }

   public int getState() throws YAPI_Exception {
      return this.get_state();
   }

   public int set_state(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("state", rest_val);
         return 0;
      }
   }

   public int setState(int newval) throws YAPI_Exception {
      return this.set_state(newval);
   }

   public int get_stateAtPowerOn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._stateAtPowerOn;
      }
   }

   public int getStateAtPowerOn() throws YAPI_Exception {
      return this.get_stateAtPowerOn();
   }

   public int set_stateAtPowerOn(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("stateAtPowerOn", rest_val);
         return 0;
      }
   }

   public int setStateAtPowerOn(int newval) throws YAPI_Exception {
      return this.set_stateAtPowerOn(newval);
   }

   public long get_maxTimeOnStateA() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._maxTimeOnStateA;
      }
   }

   public long getMaxTimeOnStateA() throws YAPI_Exception {
      return this.get_maxTimeOnStateA();
   }

   public int set_maxTimeOnStateA(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("maxTimeOnStateA", rest_val);
         return 0;
      }
   }

   public int setMaxTimeOnStateA(long newval) throws YAPI_Exception {
      return this.set_maxTimeOnStateA(newval);
   }

   public long get_maxTimeOnStateB() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._maxTimeOnStateB;
      }
   }

   public long getMaxTimeOnStateB() throws YAPI_Exception {
      return this.get_maxTimeOnStateB();
   }

   public int set_maxTimeOnStateB(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("maxTimeOnStateB", rest_val);
         return 0;
      }
   }

   public int setMaxTimeOnStateB(long newval) throws YAPI_Exception {
      return this.set_maxTimeOnStateB(newval);
   }

   public int get_output() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._output;
      }
   }

   public int getOutput() throws YAPI_Exception {
      return this.get_output();
   }

   public int set_output(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("output", rest_val);
         return 0;
      }
   }

   public int setOutput(int newval) throws YAPI_Exception {
      return this.set_output(newval);
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

   public int set_pulseTimer(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("pulseTimer", rest_val);
         return 0;
      }
   }

   public int pulse(int ms_duration) throws YAPI_Exception {
      String rest_val = Long.toString((long)ms_duration);
      this._setAttr("pulseTimer", rest_val);
      return 0;
   }

   public YWatchdog.YDelayedPulse get_delayedPulseTimer() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? DELAYEDPULSETIMER_INVALID
            : this._delayedPulseTimer;
      }
   }

   public int set_delayedPulseTimer(YWatchdog.YDelayedPulse newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "%d:%d", newval.target, newval.ms);
         this._setAttr("delayedPulseTimer", rest_val);
         return 0;
      }
   }

   public int delayedPulse(int ms_delay, int ms_duration) throws YAPI_Exception {
      String rest_val = String.format(Locale.US, "%d:%d", ms_delay, ms_duration);
      this._setAttr("delayedPulseTimer", rest_val);
      return 0;
   }

   public long get_countdown() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._countdown;
      }
   }

   public long getCountdown() throws YAPI_Exception {
      return this.get_countdown();
   }

   public int get_autoStart() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._autoStart;
      }
   }

   public int getAutoStart() throws YAPI_Exception {
      return this.get_autoStart();
   }

   public int set_autoStart(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("autoStart", rest_val);
         return 0;
      }
   }

   public int setAutoStart(int newval) throws YAPI_Exception {
      return this.set_autoStart(newval);
   }

   public int get_running() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._running;
      }
   }

   public int getRunning() throws YAPI_Exception {
      return this.get_running();
   }

   public int set_running(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("running", rest_val);
         return 0;
      }
   }

   public int setRunning(int newval) throws YAPI_Exception {
      return this.set_running(newval);
   }

   public int resetWatchdog() throws YAPI_Exception {
      String rest_val = "1";
      this._setAttr("running", rest_val);
      return 0;
   }

   public long get_triggerDelay() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._triggerDelay;
      }
   }

   public long getTriggerDelay() throws YAPI_Exception {
      return this.get_triggerDelay();
   }

   public int set_triggerDelay(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("triggerDelay", rest_val);
         return 0;
      }
   }

   public int setTriggerDelay(long newval) throws YAPI_Exception {
      return this.set_triggerDelay(newval);
   }

   public long get_triggerDuration() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._triggerDuration;
      }
   }

   public long getTriggerDuration() throws YAPI_Exception {
      return this.get_triggerDuration();
   }

   public int set_triggerDuration(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("triggerDuration", rest_val);
         return 0;
      }
   }

   public int setTriggerDuration(long newval) throws YAPI_Exception {
      return this.set_triggerDuration(newval);
   }

   public static YWatchdog FindWatchdog(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YWatchdog obj = (YWatchdog)YFunction._FindFromCache("Watchdog", func);
         if (obj == null) {
            obj = new YWatchdog(func);
            YFunction._AddToCache("Watchdog", func, obj);
         }

         return obj;
      }
   }

   public static YWatchdog FindWatchdogInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YWatchdog obj = (YWatchdog)YFunction._FindFromCacheInContext(yctx, "Watchdog", func);
         if (obj == null) {
            obj = new YWatchdog(yctx, func);
            YFunction._AddToCache("Watchdog", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YWatchdog.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackWatchdog = callback;
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
      if (this._valueCallbackWatchdog != null) {
         this._valueCallbackWatchdog.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int toggle() throws YAPI_Exception {
      if (this._firm == 0) {
         YModule mo = this.get_module();
         String fw = mo.get_firmwareRelease();
         if (fw.equals("!INVALID!")) {
            return -1;
         }

         this._firm = YAPIContext._atoi(fw);
      }

      if (this._firm < 34921) {
         int sta = this.get_state();
         if (sta == -1) {
            return -1;
         } else {
            if (sta == 1) {
               this.set_state(0);
            } else {
               this.set_state(1);
            }

            return 0;
         }
      } else {
         return this._setAttr("state", "X");
      }
   }

   public YWatchdog nextWatchdog() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindWatchdogInContext(this._yapi, next_hwid);
   }

   public static YWatchdog FirstWatchdog() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Watchdog");
         return next_hwid == null ? null : FindWatchdogInContext(yctx, next_hwid);
      }
   }

   public static YWatchdog FirstWatchdogInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Watchdog");
      return next_hwid == null ? null : FindWatchdogInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YWatchdog var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YWatchdog var1, String var2);
   }

   public static class YDelayedPulse {
      public int target = Integer.MIN_VALUE;
      public int ms = Integer.MIN_VALUE;
      public int moving = -1;
   }
}
