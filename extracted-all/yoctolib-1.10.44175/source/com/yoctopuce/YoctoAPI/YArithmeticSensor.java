package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YArithmeticSensor extends YSensor {
   public static final String DESCRIPTION_INVALID = "!INVALID!";
   public static final String COMMAND_INVALID = "!INVALID!";
   protected String _description = "!INVALID!";
   protected String _command = "!INVALID!";
   protected YArithmeticSensor.UpdateCallback _valueCallbackArithmeticSensor = null;
   protected YArithmeticSensor.TimedReportCallback _timedReportCallbackArithmeticSensor = null;

   protected YArithmeticSensor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "ArithmeticSensor";
   }

   protected YArithmeticSensor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("description")) {
         this._description = json_val.getString("description");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int set_unit(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("unit", newval);
         return 0;
      }
   }

   public int setUnit(String newval) throws YAPI_Exception {
      return this.set_unit(newval);
   }

   public String get_description() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._description;
      }
   }

   public String getDescription() throws YAPI_Exception {
      return this.get_description();
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

   public static YArithmeticSensor FindArithmeticSensor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YArithmeticSensor obj = (YArithmeticSensor)YFunction._FindFromCache("ArithmeticSensor", func);
         if (obj == null) {
            obj = new YArithmeticSensor(func);
            YFunction._AddToCache("ArithmeticSensor", func, obj);
         }

         return obj;
      }
   }

   public static YArithmeticSensor FindArithmeticSensorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YArithmeticSensor obj = (YArithmeticSensor)YFunction._FindFromCacheInContext(yctx, "ArithmeticSensor", func);
         if (obj == null) {
            obj = new YArithmeticSensor(yctx, func);
            YFunction._AddToCache("ArithmeticSensor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YArithmeticSensor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackArithmeticSensor = callback;
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
      if (this._valueCallbackArithmeticSensor != null) {
         this._valueCallbackArithmeticSensor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YArithmeticSensor.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackArithmeticSensor = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackArithmeticSensor != null) {
         this._timedReportCallbackArithmeticSensor.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public double defineExpression(String expr, String descr) throws YAPI_Exception {
      String id = this.get_functionId();
      id = id.substring(16, 16 + id.length() - 16);
      String fname = String.format(Locale.US, "arithmExpr%s.txt", id);
      String content = String.format(Locale.US, "// %s\n%s", descr, expr);
      byte[] data = this._uploadEx(fname, content.getBytes());
      String diags = new String(data);
      if (!diags.substring(0, 8).equals("Result: ")) {
         throw new YAPI_Exception(-2, diags);
      } else {
         return Double.valueOf(diags.substring(8, 8 + diags.length() - 8));
      }
   }

   public String loadExpression() throws YAPI_Exception {
      String id = this.get_functionId();
      id = id.substring(16, 16 + id.length() - 16);
      String fname = String.format(Locale.US, "arithmExpr%s.txt", id);
      String content = new String(this._download(fname));
      int idx = content.indexOf("\n");
      if (idx > 0) {
         content = content.substring(idx + 1, idx + 1 + content.length() - (idx + 1));
      }

      return content;
   }

   public int defineAuxiliaryFunction(String name, ArrayList<Double> inputValues, ArrayList<Double> outputValues) throws YAPI_Exception {
      int siz = inputValues.size();
      if (siz <= 1) {
         throw new YAPI_Exception(-2, "auxiliary function must be defined by at least two points");
      } else if (siz != outputValues.size()) {
         throw new YAPI_Exception(-2, "table sizes mismatch");
      } else {
         String defstr = "";

         for (int idx = 0; idx < siz; idx++) {
            double inputVal = inputValues.get(idx);
            double outputVal = outputValues.get(idx);
            defstr = String.format(Locale.US, "%s%f:%f\n", defstr, inputVal, outputVal);
         }

         String fname = String.format(Locale.US, "userMap%s.txt", name);
         return this._upload(fname, defstr.getBytes());
      }
   }

   public int loadAuxiliaryFunction(String name, ArrayList<Double> inputValues, ArrayList<Double> outputValues) throws YAPI_Exception {
      String fname = String.format(Locale.US, "userMap%s.txt", name);
      byte[] defbin = this._download(fname);
      int siz = defbin.length;
      if (siz <= 0) {
         throw new YAPI_Exception(-2, "auxiliary function does not exist");
      } else {
         inputValues.clear();
         outputValues.clear();
         return 0;
      }
   }

   public YArithmeticSensor nextArithmeticSensor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindArithmeticSensorInContext(this._yapi, next_hwid);
   }

   public static YArithmeticSensor FirstArithmeticSensor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("ArithmeticSensor");
         return next_hwid == null ? null : FindArithmeticSensorInContext(yctx, next_hwid);
      }
   }

   public static YArithmeticSensor FirstArithmeticSensorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("ArithmeticSensor");
      return next_hwid == null ? null : FindArithmeticSensorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YArithmeticSensor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YArithmeticSensor var1, String var2);
   }
}
