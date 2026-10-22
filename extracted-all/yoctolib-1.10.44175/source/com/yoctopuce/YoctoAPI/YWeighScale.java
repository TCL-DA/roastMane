package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YWeighScale extends YSensor {
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
   protected int _excitation = -1;
   protected double _tempAvgAdaptRatio = -1.79769313486231E308;
   protected double _tempChgAdaptRatio = -1.79769313486231E308;
   protected double _compTempAvg = -1.79769313486231E308;
   protected double _compTempChg = -1.79769313486231E308;
   protected double _compensation = -1.79769313486231E308;
   protected double _zeroTracking = -1.79769313486231E308;
   protected String _command = "!INVALID!";
   protected YWeighScale.UpdateCallback _valueCallbackWeighScale = null;
   protected YWeighScale.TimedReportCallback _timedReportCallbackWeighScale = null;

   protected YWeighScale(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "WeighScale";
   }

   protected YWeighScale(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
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

   public static YWeighScale FindWeighScale(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YWeighScale obj = (YWeighScale)YFunction._FindFromCache("WeighScale", func);
         if (obj == null) {
            obj = new YWeighScale(func);
            YFunction._AddToCache("WeighScale", func, obj);
         }

         return obj;
      }
   }

   public static YWeighScale FindWeighScaleInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YWeighScale obj = (YWeighScale)YFunction._FindFromCacheInContext(yctx, "WeighScale", func);
         if (obj == null) {
            obj = new YWeighScale(yctx, func);
            YFunction._AddToCache("WeighScale", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YWeighScale.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackWeighScale = callback;
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
      if (this._valueCallbackWeighScale != null) {
         this._valueCallbackWeighScale.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YWeighScale.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackWeighScale = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackWeighScale != null) {
         this._timedReportCallbackWeighScale.timedReportCallback(this, value);
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

   public int setCompensationTable(int tableIndex, ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      int siz = tempValues.size();
      if (siz == 1) {
         throw new YAPI_Exception(-2, "thermal compensation table must have at least two points");
      } else if (siz != compValues.size()) {
         throw new YAPI_Exception(-2, "table sizes mismatch");
      } else {
         int res = this.set_command(String.format(Locale.US, "%dZ", tableIndex));
         if (res != 0) {
            throw new YAPI_Exception(-8, "unable to reset thermal compensation table");
         } else {
            int found = 1;
            double prev = -999999.0;

            while (found > 0) {
               found = 0;
               double curr = 9.9999999E7;
               double currComp = -999999.0;

               for (int idx = 0; idx < siz; idx++) {
                  double idxTemp = tempValues.get(idx);
                  if (idxTemp > prev && idxTemp < curr) {
                     curr = idxTemp;
                     currComp = compValues.get(idx);
                     found = 1;
                  }
               }

               if (found > 0) {
                  res = this.set_command(
                     String.format(Locale.US, "%dm%d:%d", tableIndex, (int)((double)Math.round(1000.0 * curr)), (int)((double)Math.round(1000.0 * currComp)))
                  );
                  if (res != 0) {
                     throw new YAPI_Exception(-8, "unable to set thermal compensation table");
                  }

                  prev = curr;
               }
            }

            return 0;
         }
      }
   }

   public int loadCompensationTable(int tableIndex, ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      new ArrayList();
      String id = this.get_functionId();
      id = id.substring(10, 10 + id.length() - 10);
      byte[] bin_json = this._download(String.format(Locale.US, "extra.json?page=%d", 4 * YAPIContext._atoi(id) + tableIndex));
      ArrayList<String> paramlist = this._json_get_array(bin_json);
      int siz = paramlist.size() >> 1;
      tempValues.clear();
      compValues.clear();

      for (int idx = 0; idx < siz; idx++) {
         double temp = Double.valueOf((String)paramlist.get(2 * idx)) / 1000.0;
         double comp = Double.valueOf((String)paramlist.get(2 * idx + 1)) / 1000.0;
         tempValues.add(temp);
         compValues.add(comp);
      }

      return 0;
   }

   public int set_offsetAvgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.setCompensationTable(0, tempValues, compValues);
   }

   public int loadOffsetAvgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.loadCompensationTable(0, tempValues, compValues);
   }

   public int set_offsetChgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.setCompensationTable(1, tempValues, compValues);
   }

   public int loadOffsetChgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.loadCompensationTable(1, tempValues, compValues);
   }

   public int set_spanAvgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.setCompensationTable(2, tempValues, compValues);
   }

   public int loadSpanAvgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.loadCompensationTable(2, tempValues, compValues);
   }

   public int set_spanChgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.setCompensationTable(3, tempValues, compValues);
   }

   public int loadSpanChgCompensationTable(ArrayList<Double> tempValues, ArrayList<Double> compValues) throws YAPI_Exception {
      return this.loadCompensationTable(3, tempValues, compValues);
   }

   public YWeighScale nextWeighScale() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindWeighScaleInContext(this._yapi, next_hwid);
   }

   public static YWeighScale FirstWeighScale() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("WeighScale");
         return next_hwid == null ? null : FindWeighScaleInContext(yctx, next_hwid);
      }
   }

   public static YWeighScale FirstWeighScaleInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("WeighScale");
      return next_hwid == null ? null : FindWeighScaleInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YWeighScale var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YWeighScale var1, String var2);
   }
}
