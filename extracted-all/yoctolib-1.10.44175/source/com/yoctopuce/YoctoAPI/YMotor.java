package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YMotor extends YFunction {
   public static final int MOTORSTATUS_IDLE = 0;
   public static final int MOTORSTATUS_BRAKE = 1;
   public static final int MOTORSTATUS_FORWD = 2;
   public static final int MOTORSTATUS_BACKWD = 3;
   public static final int MOTORSTATUS_LOVOLT = 4;
   public static final int MOTORSTATUS_HICURR = 5;
   public static final int MOTORSTATUS_HIHEAT = 6;
   public static final int MOTORSTATUS_FAILSF = 7;
   public static final int MOTORSTATUS_INVALID = -1;
   public static final double DRIVINGFORCE_INVALID = -1.79769313486231E308;
   public static final double BRAKINGFORCE_INVALID = -1.79769313486231E308;
   public static final double CUTOFFVOLTAGE_INVALID = -1.79769313486231E308;
   public static final int OVERCURRENTLIMIT_INVALID = -1;
   public static final double FREQUENCY_INVALID = -1.79769313486231E308;
   public static final int STARTERTIME_INVALID = -1;
   public static final int FAILSAFETIMEOUT_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _motorStatus = -1;
   protected double _drivingForce = -1.79769313486231E308;
   protected double _brakingForce = -1.79769313486231E308;
   protected double _cutOffVoltage = -1.79769313486231E308;
   protected int _overCurrentLimit = -1;
   protected double _frequency = -1.79769313486231E308;
   protected int _starterTime = -1;
   protected int _failSafeTimeout = -1;
   protected String _command = "!INVALID!";
   protected YMotor.UpdateCallback _valueCallbackMotor = null;

   protected YMotor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Motor";
   }

   protected YMotor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("motorStatus")) {
         this._motorStatus = json_val.getInt("motorStatus");
      }

      if (json_val.has("drivingForce")) {
         this._drivingForce = (double)Math.round(json_val.getDouble("drivingForce") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("brakingForce")) {
         this._brakingForce = (double)Math.round(json_val.getDouble("brakingForce") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("cutOffVoltage")) {
         this._cutOffVoltage = (double)Math.round(json_val.getDouble("cutOffVoltage") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("overCurrentLimit")) {
         this._overCurrentLimit = json_val.getInt("overCurrentLimit");
      }

      if (json_val.has("frequency")) {
         this._frequency = (double)Math.round(json_val.getDouble("frequency") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("starterTime")) {
         this._starterTime = json_val.getInt("starterTime");
      }

      if (json_val.has("failSafeTimeout")) {
         this._failSafeTimeout = json_val.getInt("failSafeTimeout");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_motorStatus() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._motorStatus;
      }
   }

   public int getMotorStatus() throws YAPI_Exception {
      return this.get_motorStatus();
   }

   public int set_motorStatus(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("motorStatus", rest_val);
         return 0;
      }
   }

   public int set_drivingForce(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("drivingForce", rest_val);
         return 0;
      }
   }

   public int setDrivingForce(double newval) throws YAPI_Exception {
      return this.set_drivingForce(newval);
   }

   public double get_drivingForce() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._drivingForce;
      }
   }

   public double getDrivingForce() throws YAPI_Exception {
      return this.get_drivingForce();
   }

   public int set_brakingForce(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("brakingForce", rest_val);
         return 0;
      }
   }

   public int setBrakingForce(double newval) throws YAPI_Exception {
      return this.set_brakingForce(newval);
   }

   public double get_brakingForce() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._brakingForce;
      }
   }

   public double getBrakingForce() throws YAPI_Exception {
      return this.get_brakingForce();
   }

   public int set_cutOffVoltage(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("cutOffVoltage", rest_val);
         return 0;
      }
   }

   public int setCutOffVoltage(double newval) throws YAPI_Exception {
      return this.set_cutOffVoltage(newval);
   }

   public double get_cutOffVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._cutOffVoltage;
      }
   }

   public double getCutOffVoltage() throws YAPI_Exception {
      return this.get_cutOffVoltage();
   }

   public int get_overCurrentLimit() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._overCurrentLimit;
      }
   }

   public int getOverCurrentLimit() throws YAPI_Exception {
      return this.get_overCurrentLimit();
   }

   public int set_overCurrentLimit(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("overCurrentLimit", rest_val);
         return 0;
      }
   }

   public int setOverCurrentLimit(int newval) throws YAPI_Exception {
      return this.set_overCurrentLimit(newval);
   }

   public int set_frequency(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("frequency", rest_val);
         return 0;
      }
   }

   public int setFrequency(double newval) throws YAPI_Exception {
      return this.set_frequency(newval);
   }

   public double get_frequency() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._frequency;
      }
   }

   public double getFrequency() throws YAPI_Exception {
      return this.get_frequency();
   }

   public int get_starterTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._starterTime;
      }
   }

   public int getStarterTime() throws YAPI_Exception {
      return this.get_starterTime();
   }

   public int set_starterTime(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("starterTime", rest_val);
         return 0;
      }
   }

   public int setStarterTime(int newval) throws YAPI_Exception {
      return this.set_starterTime(newval);
   }

   public int get_failSafeTimeout() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._failSafeTimeout;
      }
   }

   public int getFailSafeTimeout() throws YAPI_Exception {
      return this.get_failSafeTimeout();
   }

   public int set_failSafeTimeout(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("failSafeTimeout", rest_val);
         return 0;
      }
   }

   public int setFailSafeTimeout(int newval) throws YAPI_Exception {
      return this.set_failSafeTimeout(newval);
   }

   public String get_command() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._command;
      }
   }

   public int set_command(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("command", newval);
         return 0;
      }
   }

   public static YMotor FindMotor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMotor obj = (YMotor)YFunction._FindFromCache("Motor", func);
         if (obj == null) {
            obj = new YMotor(func);
            YFunction._AddToCache("Motor", func, obj);
         }

         return obj;
      }
   }

   public static YMotor FindMotorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMotor obj = (YMotor)YFunction._FindFromCacheInContext(yctx, "Motor", func);
         if (obj == null) {
            obj = new YMotor(yctx, func);
            YFunction._AddToCache("Motor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMotor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMotor = callback;
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
      if (this._valueCallbackMotor != null) {
         this._valueCallbackMotor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int keepALive() throws YAPI_Exception {
      return this.set_command("K");
   }

   public int resetStatus() throws YAPI_Exception {
      return this.set_motorStatus(0);
   }

   public int drivingForceMove(double targetPower, int delay) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "P%d,%d", (int)((double)Math.round(targetPower * 10.0)), delay));
   }

   public int brakingForceMove(double targetPower, int delay) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "B%d,%d", (int)((double)Math.round(targetPower * 10.0)), delay));
   }

   public YMotor nextMotor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMotorInContext(this._yapi, next_hwid);
   }

   public static YMotor FirstMotor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Motor");
         return next_hwid == null ? null : FindMotorInContext(yctx, next_hwid);
      }
   }

   public static YMotor FirstMotorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Motor");
      return next_hwid == null ? null : FindMotorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMotor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMotor var1, String var2);
   }
}
