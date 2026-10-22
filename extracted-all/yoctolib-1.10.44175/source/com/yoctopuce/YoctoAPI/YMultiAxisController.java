package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YMultiAxisController extends YFunction {
   public static final int NAXIS_INVALID = -1;
   public static final int GLOBALSTATE_ABSENT = 0;
   public static final int GLOBALSTATE_ALERT = 1;
   public static final int GLOBALSTATE_HI_Z = 2;
   public static final int GLOBALSTATE_STOP = 3;
   public static final int GLOBALSTATE_RUN = 4;
   public static final int GLOBALSTATE_BATCH = 5;
   public static final int GLOBALSTATE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _nAxis = -1;
   protected int _globalState = -1;
   protected String _command = "!INVALID!";
   protected YMultiAxisController.UpdateCallback _valueCallbackMultiAxisController = null;

   protected YMultiAxisController(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "MultiAxisController";
   }

   protected YMultiAxisController(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("nAxis")) {
         this._nAxis = json_val.getInt("nAxis");
      }

      if (json_val.has("globalState")) {
         this._globalState = json_val.getInt("globalState");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_nAxis() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._nAxis;
      }
   }

   public int getNAxis() throws YAPI_Exception {
      return this.get_nAxis();
   }

   public int set_nAxis(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("nAxis", rest_val);
         return 0;
      }
   }

   public int setNAxis(int newval) throws YAPI_Exception {
      return this.set_nAxis(newval);
   }

   public int get_globalState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._globalState;
      }
   }

   public int getGlobalState() throws YAPI_Exception {
      return this.get_globalState();
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

   public static YMultiAxisController FindMultiAxisController(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMultiAxisController obj = (YMultiAxisController)YFunction._FindFromCache("MultiAxisController", func);
         if (obj == null) {
            obj = new YMultiAxisController(func);
            YFunction._AddToCache("MultiAxisController", func, obj);
         }

         return obj;
      }
   }

   public static YMultiAxisController FindMultiAxisControllerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMultiAxisController obj = (YMultiAxisController)YFunction._FindFromCacheInContext(yctx, "MultiAxisController", func);
         if (obj == null) {
            obj = new YMultiAxisController(yctx, func);
            YFunction._AddToCache("MultiAxisController", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMultiAxisController.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMultiAxisController = callback;
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
      if (this._valueCallbackMultiAxisController != null) {
         this._valueCallbackMultiAxisController.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String command) throws YAPI_Exception {
      String url = String.format(Locale.US, "cmd.txt?X=%s", command);
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

   public int findHomePosition(ArrayList<Double> speed) throws YAPI_Exception {
      int ndim = speed.size();
      String cmd = String.format(Locale.US, "H%d", (int)((double)Math.round((float)(1000 * speed.get(0).intValue()))));

      for (int i = 1; i < ndim; i++) {
         cmd = String.format(Locale.US, "%s,%d", cmd, (int)((double)Math.round((float)(1000 * speed.get(i).intValue()))));
      }

      return this.sendCommand(cmd);
   }

   public int moveTo(ArrayList<Double> absPos) throws YAPI_Exception {
      int ndim = absPos.size();
      String cmd = String.format(Locale.US, "M%d", (int)((double)Math.round((float)(16 * absPos.get(0).intValue()))));

      for (int i = 1; i < ndim; i++) {
         cmd = String.format(Locale.US, "%s,%d", cmd, (int)((double)Math.round((float)(16 * absPos.get(i).intValue()))));
      }

      return this.sendCommand(cmd);
   }

   public int moveRel(ArrayList<Double> relPos) throws YAPI_Exception {
      int ndim = relPos.size();
      String cmd = String.format(Locale.US, "m%d", (int)((double)Math.round((float)(16 * relPos.get(0).intValue()))));

      for (int i = 1; i < ndim; i++) {
         cmd = String.format(Locale.US, "%s,%d", cmd, (int)((double)Math.round((float)(16 * relPos.get(i).intValue()))));
      }

      return this.sendCommand(cmd);
   }

   public int pause(int waitMs) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "_%d", waitMs));
   }

   public int emergencyStop() throws YAPI_Exception {
      return this.set_command("!");
   }

   public int abortAndBrake() throws YAPI_Exception {
      return this.set_command("B");
   }

   public int abortAndHiZ() throws YAPI_Exception {
      return this.set_command("z");
   }

   public YMultiAxisController nextMultiAxisController() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMultiAxisControllerInContext(this._yapi, next_hwid);
   }

   public static YMultiAxisController FirstMultiAxisController() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("MultiAxisController");
         return next_hwid == null ? null : FindMultiAxisControllerInContext(yctx, next_hwid);
      }
   }

   public static YMultiAxisController FirstMultiAxisControllerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("MultiAxisController");
      return next_hwid == null ? null : FindMultiAxisControllerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMultiAxisController var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMultiAxisController var1, String var2);
   }
}
