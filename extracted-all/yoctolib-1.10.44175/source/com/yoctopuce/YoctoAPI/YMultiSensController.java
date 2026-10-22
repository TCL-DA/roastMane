package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YMultiSensController extends YFunction {
   public static final int NSENSORS_INVALID = -1;
   public static final int MAXSENSORS_INVALID = -1;
   public static final int MAINTENANCEMODE_FALSE = 0;
   public static final int MAINTENANCEMODE_TRUE = 1;
   public static final int MAINTENANCEMODE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _nSensors = -1;
   protected int _maxSensors = -1;
   protected int _maintenanceMode = -1;
   protected String _command = "!INVALID!";
   protected YMultiSensController.UpdateCallback _valueCallbackMultiSensController = null;

   protected YMultiSensController(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "MultiSensController";
   }

   protected YMultiSensController(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("nSensors")) {
         this._nSensors = json_val.getInt("nSensors");
      }

      if (json_val.has("maxSensors")) {
         this._maxSensors = json_val.getInt("maxSensors");
      }

      if (json_val.has("maintenanceMode")) {
         this._maintenanceMode = json_val.getInt("maintenanceMode") > 0 ? 1 : 0;
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_nSensors() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._nSensors;
      }
   }

   public int getNSensors() throws YAPI_Exception {
      return this.get_nSensors();
   }

   public int set_nSensors(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("nSensors", rest_val);
         return 0;
      }
   }

   public int setNSensors(int newval) throws YAPI_Exception {
      return this.set_nSensors(newval);
   }

   public int get_maxSensors() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._maxSensors;
      }
   }

   public int getMaxSensors() throws YAPI_Exception {
      return this.get_maxSensors();
   }

   public int get_maintenanceMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._maintenanceMode;
      }
   }

   public int getMaintenanceMode() throws YAPI_Exception {
      return this.get_maintenanceMode();
   }

   public int set_maintenanceMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("maintenanceMode", rest_val);
         return 0;
      }
   }

   public int setMaintenanceMode(int newval) throws YAPI_Exception {
      return this.set_maintenanceMode(newval);
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

   public static YMultiSensController FindMultiSensController(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMultiSensController obj = (YMultiSensController)YFunction._FindFromCache("MultiSensController", func);
         if (obj == null) {
            obj = new YMultiSensController(func);
            YFunction._AddToCache("MultiSensController", func, obj);
         }

         return obj;
      }
   }

   public static YMultiSensController FindMultiSensControllerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMultiSensController obj = (YMultiSensController)YFunction._FindFromCacheInContext(yctx, "MultiSensController", func);
         if (obj == null) {
            obj = new YMultiSensController(yctx, func);
            YFunction._AddToCache("MultiSensController", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMultiSensController.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMultiSensController = callback;
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
      if (this._valueCallbackMultiSensController != null) {
         this._valueCallbackMultiSensController.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int setupAddress(int addr) throws YAPI_Exception {
      String cmd = String.format(Locale.US, "A%d", addr);
      return this.set_command(cmd);
   }

   public YMultiSensController nextMultiSensController() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMultiSensControllerInContext(this._yapi, next_hwid);
   }

   public static YMultiSensController FirstMultiSensController() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("MultiSensController");
         return next_hwid == null ? null : FindMultiSensControllerInContext(yctx, next_hwid);
      }
   }

   public static YMultiSensController FirstMultiSensControllerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("MultiSensController");
      return next_hwid == null ? null : FindMultiSensControllerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMultiSensController var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMultiSensController var1, String var2);
   }
}
