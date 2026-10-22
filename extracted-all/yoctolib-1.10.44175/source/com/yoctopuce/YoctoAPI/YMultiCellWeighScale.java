package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YMultiCellWeighScale extends YSensor {
   public static final int CELLCOUNT_INVALID = -1;
   public static final int EXTERNALSENSE_FALSE = 0;
   public static final int EXTERNALSENSE_TRUE = 1;
   public static final int EXTERNALSENSE_INVALID = -1;
   public static final int EXCITATION_OFF = 0;
   public static final int EXCITATION_DC = 1;
   public static final int EXCITATION_AC = 2;
   public static final int EXCITATION_INVALID = -1;
   public static final double TEMPAVGADAPTRATIO_INVALID = -1.79769313486231E308;
   public static final double TEMPCHGADAPTRATIO_INVALID = -1.79769313486231E308;
   public static final double COMPTEMPAVG_INVALID = -1.79769313486231E308;
   public static final double COMPTEMPCHG_INVALID = -1.79769313486231E308;
   public static final double COMPENSATION_INVALID = -1.79769313486231E308;
   public static final double ZEROTRACKING_INVALID = -1.79769313486231E308;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _cellCount = -1;
   protected int _externalSense = -1;
   protected int _excitation = -1;
   protected double _tempAvgAdaptRatio = -1.79769313486231E308;
   protected double _tempChgAdaptRatio = -1.79769313486231E308;
   protected double _compTempAvg = -1.79769313486231E308;
   protected double _compTempChg = -1.79769313486231E308;
   protected double _compensation = -1.79769313486231E308;
   protected double _zeroTracking = -1.79769313486231E308;
   protected String _command = "!INVALID!";
   protected YMultiCellWeighScale.UpdateCallback _valueCallbackMultiCellWeighScale = null;
   protected YMultiCellWeighScale.TimedReportCallback _timedReportCallbackMultiCellWeighScale = null;

   protected YMultiCellWeighScale(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "MultiCellWeighScale";
   }

   protected YMultiCellWeighScale(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("cellCount")) {
         this._cellCount = json_val.getInt("cellCount");
      }

      if (json_val.has("externalSense")) {
         this._externalSense = json_val.getInt("externalSense") > 0 ? 1 : 0;
      }

      if (json_val.has("excitation")) {
         this._excitation = json_val.getInt("excitation");
      }

      if (json_val.has("tempAvgAdaptRatio")) {
         this._tempAvgAdaptRatio = (double)Math.round(json_val.getDouble("tempAvgAdaptRatio") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("tempChgAdaptRatio")) {
         this._tempChgAdaptRatio = (double)Math.round(json_val.getDouble("tempChgAdaptRatio") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("compTempAvg")) {
         this._compTempAvg = (double)Math.round(json_val.getDouble("compTempAvg") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("compTempChg")) {
         this._compTempChg = (double)Math.round(json_val.getDouble("compTempChg") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("compensation")) {
         this._compensation = (double)Math.round(json_val.getDouble("compensation") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("zeroTracking")) {
         this._zeroTracking = (double)Math.round(json_val.getDouble("zeroTracking") * 1000.0 / 65536.0) / 1000.0;
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

   public int get_cellCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._cellCount;
      }
   }

   public int getCellCount() throws YAPI_Exception {
      return this.get_cellCount();
   }

   public int set_cellCount(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("cellCount", rest_val);
         return 0;
      }
   }

   public int setCellCount(int newval) throws YAPI_Exception {
      return this.set_cellCount(newval);
   }

   public int get_externalSense() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._externalSense;
      }
   }

   public int getExternalSense() throws YAPI_Exception {
      return this.get_externalSense();
   }

   public int set_externalSense(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("externalSense", rest_val);
         return 0;
      }
   }

   public int setExternalSense(int newval) throws YAPI_Exception {
      return this.set_externalSense(newval);
   }

   public int get_excitation() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._excitation;
      }
   }

   public int getExcitation() throws YAPI_Exception {
      return this.get_excitation();
   }

   public int set_excitation(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("excitation", rest_val);
         return 0;
      }
   }

   public int setExcitation(int newval) throws YAPI_Exception {
      return this.set_excitation(newval);
   }

   public int set_tempAvgAdaptRatio(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("tempAvgAdaptRatio", rest_val);
         return 0;
      }
   }

   public int setTempAvgAdaptRatio(double newval) throws YAPI_Exception {
      return this.set_tempAvgAdaptRatio(newval);
   }

   public double get_tempAvgAdaptRatio() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._tempAvgAdaptRatio;
      }
   }

   public double getTempAvgAdaptRatio() throws YAPI_Exception {
      return this.get_tempAvgAdaptRatio();
   }

   public int set_tempChgAdaptRatio(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("tempChgAdaptRatio", rest_val);
         return 0;
      }
   }

   public int setTempChgAdaptRatio(double newval) throws YAPI_Exception {
      return this.set_tempChgAdaptRatio(newval);
   }

   public double get_tempChgAdaptRatio() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._tempChgAdaptRatio;
      }
   }

   public double getTempChgAdaptRatio() throws YAPI_Exception {
      return this.get_tempChgAdaptRatio();
   }

   public double get_compTempAvg() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._compTempAvg;
      }
   }

   public double getCompTempAvg() throws YAPI_Exception {
      return this.get_compTempAvg();
   }

   public double get_compTempChg() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._compTempChg;
      }
   }

   public double getCompTempChg() throws YAPI_Exception {
      return this.get_compTempChg();
   }

   public double get_compensation() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._compensation;
      }
   }

   public double getCompensation() throws YAPI_Exception {
      return this.get_compensation();
   }

   public int set_zeroTracking(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("zeroTracking", rest_val);
         return 0;
      }
   }

   public int setZeroTracking(double newval) throws YAPI_Exception {
      return this.set_zeroTracking(newval);
   }

   public double get_zeroTracking() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._zeroTracking;
      }
   }

   public double getZeroTracking() throws YAPI_Exception {
      return this.get_zeroTracking();
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

   public static YMultiCellWeighScale FindMultiCellWeighScale(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMultiCellWeighScale obj = (YMultiCellWeighScale)YFunction._FindFromCache("MultiCellWeighScale", func);
         if (obj == null) {
            obj = new YMultiCellWeighScale(func);
            YFunction._AddToCache("MultiCellWeighScale", func, obj);
         }

         return obj;
      }
   }

   public static YMultiCellWeighScale FindMultiCellWeighScaleInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMultiCellWeighScale obj = (YMultiCellWeighScale)YFunction._FindFromCacheInContext(yctx, "MultiCellWeighScale", func);
         if (obj == null) {
            obj = new YMultiCellWeighScale(yctx, func);
            YFunction._AddToCache("MultiCellWeighScale", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMultiCellWeighScale.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMultiCellWeighScale = callback;
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
      if (this._valueCallbackMultiCellWeighScale != null) {
         this._valueCallbackMultiCellWeighScale.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YMultiCellWeighScale.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackMultiCellWeighScale = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackMultiCellWeighScale != null) {
         this._timedReportCallbackMultiCellWeighScale.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int tare() throws YAPI_Exception {
      return this.set_command("T");
   }

   public int setupSpan(double currWeight, double maxWeight) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "S%d:%d", (int)((double)Math.round(1000.0 * currWeight)), (int)((double)Math.round(1000.0 * maxWeight))));
   }

   public YMultiCellWeighScale nextMultiCellWeighScale() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMultiCellWeighScaleInContext(this._yapi, next_hwid);
   }

   public static YMultiCellWeighScale FirstMultiCellWeighScale() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("MultiCellWeighScale");
         return next_hwid == null ? null : FindMultiCellWeighScaleInContext(yctx, next_hwid);
      }
   }

   public static YMultiCellWeighScale FirstMultiCellWeighScaleInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("MultiCellWeighScale");
      return next_hwid == null ? null : FindMultiCellWeighScaleInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMultiCellWeighScale var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMultiCellWeighScale var1, String var2);
   }
}
