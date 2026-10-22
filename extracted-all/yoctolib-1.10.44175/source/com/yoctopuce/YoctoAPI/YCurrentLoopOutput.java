package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YCurrentLoopOutput extends YFunction {
   public static final double CURRENT_INVALID = -1.79769313486231E308;
   public static final String CURRENTTRANSITION_INVALID = "!INVALID!";
   public static final double CURRENTATSTARTUP_INVALID = -1.79769313486231E308;
   public static final int LOOPPOWER_NOPWR = 0;
   public static final int LOOPPOWER_LOWPWR = 1;
   public static final int LOOPPOWER_POWEROK = 2;
   public static final int LOOPPOWER_INVALID = -1;
   protected double _current = -1.79769313486231E308;
   protected String _currentTransition = "!INVALID!";
   protected double _currentAtStartUp = -1.79769313486231E308;
   protected int _loopPower = -1;
   protected YCurrentLoopOutput.UpdateCallback _valueCallbackCurrentLoopOutput = null;

   protected YCurrentLoopOutput(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "CurrentLoopOutput";
   }

   protected YCurrentLoopOutput(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("current")) {
         this._current = (double)Math.round(json_val.getDouble("current") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("currentTransition")) {
         this._currentTransition = json_val.getString("currentTransition");
      }

      if (json_val.has("currentAtStartUp")) {
         this._currentAtStartUp = (double)Math.round(json_val.getDouble("currentAtStartUp") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("loopPower")) {
         this._loopPower = json_val.getInt("loopPower");
      }

      super._parseAttr(json_val);
   }

   public int set_current(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("current", rest_val);
         return 0;
      }
   }

   public int setCurrent(double newval) throws YAPI_Exception {
      return this.set_current(newval);
   }

   public double get_current() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._current;
      }
   }

   public double getCurrent() throws YAPI_Exception {
      return this.get_current();
   }

   public String get_currentTransition() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._currentTransition;
      }
   }

   public int set_currentTransition(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("currentTransition", newval);
         return 0;
      }
   }

   public int set_currentAtStartUp(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("currentAtStartUp", rest_val);
         return 0;
      }
   }

   public int setCurrentAtStartUp(double newval) throws YAPI_Exception {
      return this.set_currentAtStartUp(newval);
   }

   public double get_currentAtStartUp() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentAtStartUp;
      }
   }

   public double getCurrentAtStartUp() throws YAPI_Exception {
      return this.get_currentAtStartUp();
   }

   public int get_loopPower() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._loopPower;
      }
   }

   public int getLoopPower() throws YAPI_Exception {
      return this.get_loopPower();
   }

   public static YCurrentLoopOutput FindCurrentLoopOutput(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YCurrentLoopOutput obj = (YCurrentLoopOutput)YFunction._FindFromCache("CurrentLoopOutput", func);
         if (obj == null) {
            obj = new YCurrentLoopOutput(func);
            YFunction._AddToCache("CurrentLoopOutput", func, obj);
         }

         return obj;
      }
   }

   public static YCurrentLoopOutput FindCurrentLoopOutputInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YCurrentLoopOutput obj = (YCurrentLoopOutput)YFunction._FindFromCacheInContext(yctx, "CurrentLoopOutput", func);
         if (obj == null) {
            obj = new YCurrentLoopOutput(yctx, func);
            YFunction._AddToCache("CurrentLoopOutput", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YCurrentLoopOutput.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackCurrentLoopOutput = callback;
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
      if (this._valueCallbackCurrentLoopOutput != null) {
         this._valueCallbackCurrentLoopOutput.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int currentMove(double mA_target, int ms_duration) throws YAPI_Exception {
      if (mA_target < 3.0) {
         mA_target = 3.0;
      }

      if (mA_target > 21.0) {
         mA_target = 21.0;
      }

      String newval = String.format(Locale.US, "%d:%d", (int)((double)Math.round(mA_target * 65536.0)), ms_duration);
      return this.set_currentTransition(newval);
   }

   public YCurrentLoopOutput nextCurrentLoopOutput() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindCurrentLoopOutputInContext(this._yapi, next_hwid);
   }

   public static YCurrentLoopOutput FirstCurrentLoopOutput() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("CurrentLoopOutput");
         return next_hwid == null ? null : FindCurrentLoopOutputInContext(yctx, next_hwid);
      }
   }

   public static YCurrentLoopOutput FirstCurrentLoopOutputInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("CurrentLoopOutput");
      return next_hwid == null ? null : FindCurrentLoopOutputInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YCurrentLoopOutput var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YCurrentLoopOutput var1, String var2);
   }
}
