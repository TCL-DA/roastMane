package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YStepperMotor extends YFunction {
   public static final int MOTORSTATE_ABSENT = 0;
   public static final int MOTORSTATE_ALERT = 1;
   public static final int MOTORSTATE_HI_Z = 2;
   public static final int MOTORSTATE_STOP = 3;
   public static final int MOTORSTATE_RUN = 4;
   public static final int MOTORSTATE_BATCH = 5;
   public static final int MOTORSTATE_INVALID = -1;
   public static final int DIAGS_INVALID = -1;
   public static final double STEPPOS_INVALID = -1.79769313486231E308;
   public static final double SPEED_INVALID = -1.79769313486231E308;
   public static final double PULLINSPEED_INVALID = -1.79769313486231E308;
   public static final double MAXACCEL_INVALID = -1.79769313486231E308;
   public static final double MAXSPEED_INVALID = -1.79769313486231E308;
   public static final int STEPPING_MICROSTEP16 = 0;
   public static final int STEPPING_MICROSTEP8 = 1;
   public static final int STEPPING_MICROSTEP4 = 2;
   public static final int STEPPING_HALFSTEP = 3;
   public static final int STEPPING_FULLSTEP = 4;
   public static final int STEPPING_INVALID = -1;
   public static final int OVERCURRENT_INVALID = -1;
   public static final int TCURRSTOP_INVALID = -1;
   public static final int TCURRRUN_INVALID = -1;
   public static final String ALERTMODE_INVALID = "!INVALID!";
   public static final String AUXMODE_INVALID = "!INVALID!";
   public static final int AUXSIGNAL_INVALID = Integer.MIN_VALUE;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _motorState = -1;
   protected int _diags = -1;
   protected double _stepPos = -1.79769313486231E308;
   protected double _speed = -1.79769313486231E308;
   protected double _pullinSpeed = -1.79769313486231E308;
   protected double _maxAccel = -1.79769313486231E308;
   protected double _maxSpeed = -1.79769313486231E308;
   protected int _stepping = -1;
   protected int _overcurrent = -1;
   protected int _tCurrStop = -1;
   protected int _tCurrRun = -1;
   protected String _alertMode = "!INVALID!";
   protected String _auxMode = "!INVALID!";
   protected int _auxSignal = Integer.MIN_VALUE;
   protected String _command = "!INVALID!";
   protected YStepperMotor.UpdateCallback _valueCallbackStepperMotor = null;

   protected YStepperMotor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "StepperMotor";
   }

   protected YStepperMotor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("motorState")) {
         this._motorState = json_val.getInt("motorState");
      }

      if (json_val.has("diags")) {
         this._diags = json_val.getInt("diags");
      }

      if (json_val.has("stepPos")) {
         this._stepPos = (double)Math.round(json_val.getDouble("stepPos") / 16.0);
      }

      if (json_val.has("speed")) {
         this._speed = (double)Math.round(json_val.getDouble("speed") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("pullinSpeed")) {
         this._pullinSpeed = (double)Math.round(json_val.getDouble("pullinSpeed") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("maxAccel")) {
         this._maxAccel = (double)Math.round(json_val.getDouble("maxAccel") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("maxSpeed")) {
         this._maxSpeed = (double)Math.round(json_val.getDouble("maxSpeed") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("stepping")) {
         this._stepping = json_val.getInt("stepping");
      }

      if (json_val.has("overcurrent")) {
         this._overcurrent = json_val.getInt("overcurrent");
      }

      if (json_val.has("tCurrStop")) {
         this._tCurrStop = json_val.getInt("tCurrStop");
      }

      if (json_val.has("tCurrRun")) {
         this._tCurrRun = json_val.getInt("tCurrRun");
      }

      if (json_val.has("alertMode")) {
         this._alertMode = json_val.getString("alertMode");
      }

      if (json_val.has("auxMode")) {
         this._auxMode = json_val.getString("auxMode");
      }

      if (json_val.has("auxSignal")) {
         this._auxSignal = json_val.getInt("auxSignal");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_motorState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._motorState;
      }
   }

   public int getMotorState() throws YAPI_Exception {
      return this.get_motorState();
   }

   public int get_diags() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._diags;
      }
   }

   public int getDiags() throws YAPI_Exception {
      return this.get_diags();
   }

   public int set_stepPos(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Double.toString((double)Math.round(newval * 100.0) / 100.0);
         this._setAttr("stepPos", rest_val);
         return 0;
      }
   }

   public int setStepPos(double newval) throws YAPI_Exception {
      return this.set_stepPos(newval);
   }

   public double get_stepPos() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._stepPos;
      }
   }

   public double getStepPos() throws YAPI_Exception {
      return this.get_stepPos();
   }

   public double get_speed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._speed;
      }
   }

   public double getSpeed() throws YAPI_Exception {
      return this.get_speed();
   }

   public int set_pullinSpeed(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("pullinSpeed", rest_val);
         return 0;
      }
   }

   public int setPullinSpeed(double newval) throws YAPI_Exception {
      return this.set_pullinSpeed(newval);
   }

   public double get_pullinSpeed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._pullinSpeed;
      }
   }

   public double getPullinSpeed() throws YAPI_Exception {
      return this.get_pullinSpeed();
   }

   public int set_maxAccel(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("maxAccel", rest_val);
         return 0;
      }
   }

   public int setMaxAccel(double newval) throws YAPI_Exception {
      return this.set_maxAccel(newval);
   }

   public double get_maxAccel() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._maxAccel;
      }
   }

   public double getMaxAccel() throws YAPI_Exception {
      return this.get_maxAccel();
   }

   public int set_maxSpeed(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("maxSpeed", rest_val);
         return 0;
      }
   }

   public int setMaxSpeed(double newval) throws YAPI_Exception {
      return this.set_maxSpeed(newval);
   }

   public double get_maxSpeed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._maxSpeed;
      }
   }

   public double getMaxSpeed() throws YAPI_Exception {
      return this.get_maxSpeed();
   }

   public int get_stepping() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._stepping;
      }
   }

   public int getStepping() throws YAPI_Exception {
      return this.get_stepping();
   }

   public int set_stepping(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("stepping", rest_val);
         return 0;
      }
   }

   public int setStepping(int newval) throws YAPI_Exception {
      return this.set_stepping(newval);
   }

   public int get_overcurrent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._overcurrent;
      }
   }

   public int getOvercurrent() throws YAPI_Exception {
      return this.get_overcurrent();
   }

   public int set_overcurrent(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("overcurrent", rest_val);
         return 0;
      }
   }

   public int setOvercurrent(int newval) throws YAPI_Exception {
      return this.set_overcurrent(newval);
   }

   public int get_tCurrStop() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._tCurrStop;
      }
   }

   public int getTCurrStop() throws YAPI_Exception {
      return this.get_tCurrStop();
   }

   public int set_tCurrStop(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("tCurrStop", rest_val);
         return 0;
      }
   }

   public int setTCurrStop(int newval) throws YAPI_Exception {
      return this.set_tCurrStop(newval);
   }

   public int get_tCurrRun() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._tCurrRun;
      }
   }

   public int getTCurrRun() throws YAPI_Exception {
      return this.get_tCurrRun();
   }

   public int set_tCurrRun(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("tCurrRun", rest_val);
         return 0;
      }
   }

   public int setTCurrRun(int newval) throws YAPI_Exception {
      return this.set_tCurrRun(newval);
   }

   public String get_alertMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._alertMode;
      }
   }

   public int set_alertMode(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("alertMode", newval);
         return 0;
      }
   }

   public String get_auxMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._auxMode;
      }
   }

   public int set_auxMode(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("auxMode", newval);
         return 0;
      }
   }

   public int get_auxSignal() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._auxSignal;
      }
   }

   public int getAuxSignal() throws YAPI_Exception {
      return this.get_auxSignal();
   }

   public int set_auxSignal(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("auxSignal", rest_val);
         return 0;
      }
   }

   public int setAuxSignal(int newval) throws YAPI_Exception {
      return this.set_auxSignal(newval);
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

   public static YStepperMotor FindStepperMotor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YStepperMotor obj = (YStepperMotor)YFunction._FindFromCache("StepperMotor", func);
         if (obj == null) {
            obj = new YStepperMotor(func);
            YFunction._AddToCache("StepperMotor", func, obj);
         }

         return obj;
      }
   }

   public static YStepperMotor FindStepperMotorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YStepperMotor obj = (YStepperMotor)YFunction._FindFromCacheInContext(yctx, "StepperMotor", func);
         if (obj == null) {
            obj = new YStepperMotor(yctx, func);
            YFunction._AddToCache("StepperMotor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YStepperMotor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackStepperMotor = callback;
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
      if (this._valueCallbackStepperMotor != null) {
         this._valueCallbackStepperMotor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String command) throws YAPI_Exception {
      String id = this.get_functionId();
      id = id.substring(12, 13);
      String url = String.format(Locale.US, "cmd.txt?%s=%s", id, command);
      byte[] retBin = this._download(url);
      int res = retBin[0] & 255;
      if (res < 58) {
         if (res != 48) {
            throw new YAPI_Exception(-6, "Motor command pipeline is full, try again later");
         }
      } else if (res != 48) {
         throw new YAPI_Exception(-8, "Motor command failed permanently");
      }

      return 0;
   }

   public int reset() throws YAPI_Exception {
      return this.set_command("Z");
   }

   public int findHomePosition(double speed) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "H%d", (int)((double)Math.round(1000.0 * speed))));
   }

   public int changeSpeed(double speed) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "R%d", (int)((double)Math.round(1000.0 * speed))));
   }

   public int moveTo(double absPos) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "M%d", (int)((double)Math.round(16.0 * absPos))));
   }

   public int moveRel(double relPos) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "m%d", (int)((double)Math.round(16.0 * relPos))));
   }

   public int moveRelSlow(double relPos, double maxSpeed) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "m%d@%d", (int)((double)Math.round(16.0 * relPos)), (int)((double)Math.round(1000.0 * maxSpeed))));
   }

   public int pause(int waitMs) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "_%d", waitMs));
   }

   public int emergencyStop() throws YAPI_Exception {
      return this.set_command("!");
   }

   public int alertStepOut() throws YAPI_Exception {
      return this.set_command(".");
   }

   public int alertStepDir(int dir) throws YAPI_Exception {
      if (dir == 0) {
         throw new YAPI_Exception(-2, "direction must be +1 or -1");
      } else {
         return dir > 0 ? this.set_command(".+") : this.set_command(".-");
      }
   }

   public int abortAndBrake() throws YAPI_Exception {
      return this.set_command("B");
   }

   public int abortAndHiZ() throws YAPI_Exception {
      return this.set_command("z");
   }

   public YStepperMotor nextStepperMotor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindStepperMotorInContext(this._yapi, next_hwid);
   }

   public static YStepperMotor FirstStepperMotor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("StepperMotor");
         return next_hwid == null ? null : FindStepperMotorInContext(yctx, next_hwid);
      }
   }

   public static YStepperMotor FirstStepperMotorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("StepperMotor");
      return next_hwid == null ? null : FindStepperMotorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YStepperMotor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YStepperMotor var1, String var2);
   }
}
