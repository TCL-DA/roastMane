package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YSensor extends YFunction {
   public static final String UNIT_INVALID = "!INVALID!";
   public static final double CURRENTVALUE_INVALID = -1.79769313486231E308;
   public static final double LOWESTVALUE_INVALID = -1.79769313486231E308;
   public static final double HIGHESTVALUE_INVALID = -1.79769313486231E308;
   public static final double CURRENTRAWVALUE_INVALID = -1.79769313486231E308;
   public static final String LOGFREQUENCY_INVALID = "!INVALID!";
   public static final String REPORTFREQUENCY_INVALID = "!INVALID!";
   public static final int ADVMODE_IMMEDIATE = 0;
   public static final int ADVMODE_PERIOD_AVG = 1;
   public static final int ADVMODE_PERIOD_MIN = 2;
   public static final int ADVMODE_PERIOD_MAX = 3;
   public static final int ADVMODE_INVALID = -1;
   public static final String CALIBRATIONPARAM_INVALID = "!INVALID!";
   public static final double RESOLUTION_INVALID = -1.79769313486231E308;
   public static final int SENSORSTATE_INVALID = Integer.MIN_VALUE;
   protected String _unit = "!INVALID!";
   protected double _currentValue = -1.79769313486231E308;
   protected double _lowestValue = -1.79769313486231E308;
   protected double _highestValue = -1.79769313486231E308;
   protected double _currentRawValue = -1.79769313486231E308;
   protected String _logFrequency = "!INVALID!";
   protected String _reportFrequency = "!INVALID!";
   protected int _advMode = -1;
   protected String _calibrationParam = "!INVALID!";
   protected double _resolution = -1.79769313486231E308;
   protected int _sensorState = Integer.MIN_VALUE;
   protected YSensor.UpdateCallback _valueCallbackSensor = null;
   protected YSensor.TimedReportCallback _timedReportCallbackSensor = null;
   protected double _prevTimedReport = 0.0;
   protected double _iresol = 0.0;
   protected double _offset = 0.0;
   protected double _scale = 0.0;
   protected double _decexp = 0.0;
   protected int _caltyp = 0;
   protected ArrayList<Integer> _calpar = new ArrayList<>();
   protected ArrayList<Double> _calraw = new ArrayList<>();
   protected ArrayList<Double> _calref = new ArrayList<>();
   protected YAPI.CalibrationHandlerCallback _calhdl;

   String _encodeCalibrationPoints(ArrayList<Double> rawValues, ArrayList<Double> refValues, String actualCparams) throws YAPI_Exception {
      int npt = rawValues.size() < refValues.size() ? rawValues.size() : refValues.size();
      int minRaw = 0;
      if (npt == 0) {
         return "0";
      } else {
         int pos = actualCparams.indexOf(44);
         if (!actualCparams.equals("") && !actualCparams.equals("0") && pos < 0) {
            ArrayList<Integer> iCalib = YAPIContext._decodeWords(actualCparams);
            int calibrationOffset = iCalib.get(0);
            int divisor = iCalib.get(1);
            int calibType;
            if (divisor > 0) {
               calibType = npt;
            } else {
               calibType = 10 + npt;
            }

            String res = Integer.toString(calibType);
            if (calibType <= 10) {
               for (int i = 0; i < npt; i++) {
                  int rawVal = (int)(rawValues.get(i) * (double)divisor - (double)calibrationOffset + 0.5);
                  if (rawVal >= minRaw && rawVal < 65536) {
                     int refVal = (int)(refValues.get(i) * (double)divisor - (double)calibrationOffset + 0.5);
                     if (refVal >= 0 && refVal < 65536) {
                        res = res + String.format(Locale.US, ",%d,%d", rawVal, refVal);
                        minRaw = rawVal + 1;
                     }
                  }
               }
            } else {
               for (int ix = 0; ix < npt; ix++) {
                  int rawVal = (int)YAPIContext._doubleToDecimal(rawValues.get(ix));
                  int refVal = (int)YAPIContext._doubleToDecimal(refValues.get(ix));
                  res = res + String.format(Locale.US, ",%d,%d", rawVal, refVal);
               }
            }

            return res;
         } else {
            this._throw(-3, "Device does not support new calibration parameters. Please upgrade your firmware");
            return "0";
         }
      }
   }

   static int _decodeCalibrationPoints(String calibParams, ArrayList<Integer> intPt, ArrayList<Double> rawPt, ArrayList<Double> calPt) {
      intPt.clear();
      rawPt.clear();
      calPt.clear();
      if (!calibParams.equals("") && !calibParams.equals("0")) {
         if (calibParams.indexOf(44) != -1) {
            return -1;
         } else {
            ArrayList<Integer> iCalib = YAPIContext._decodeWords(calibParams);
            if (iCalib.size() < 2) {
               return -1;
            } else if (iCalib.size() == 2) {
               return 0;
            } else {
               int pos = 0;
               double calibrationOffset = (double)iCalib.get(pos++).intValue();
               double divisor = (double)iCalib.get(pos++).intValue();
               int calibType = iCalib.get(pos++);
               if (calibType == 0) {
                  return 0;
               } else {
                  while (pos < iCalib.size()) {
                     int ival = iCalib.get(pos++);
                     double fval;
                     if (calibType <= 10) {
                        fval = ((double)ival + calibrationOffset) / divisor;
                     } else {
                        fval = YAPIContext._decimalToDouble(ival);
                     }

                     intPt.add(ival);
                     if ((intPt.size() & 1) == 1) {
                        rawPt.add(fval);
                     } else {
                        calPt.add(fval);
                     }
                  }

                  return intPt.size() < 10 ? -1 : calibType;
               }
            }
         }
      } else {
         return 0;
      }
   }

   protected YSensor(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Sensor";
   }

   protected YSensor(String func) {
      this(YAPI.GetYCtx(false), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("unit")) {
         this._unit = json_val.getString("unit");
      }

      if (json_val.has("currentValue")) {
         this._currentValue = (double)Math.round(json_val.getDouble("currentValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("lowestValue")) {
         this._lowestValue = (double)Math.round(json_val.getDouble("lowestValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("highestValue")) {
         this._highestValue = (double)Math.round(json_val.getDouble("highestValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("currentRawValue")) {
         this._currentRawValue = (double)Math.round(json_val.getDouble("currentRawValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("logFrequency")) {
         this._logFrequency = json_val.getString("logFrequency");
      }

      if (json_val.has("reportFrequency")) {
         this._reportFrequency = json_val.getString("reportFrequency");
      }

      if (json_val.has("advMode")) {
         this._advMode = json_val.getInt("advMode");
      }

      if (json_val.has("calibrationParam")) {
         this._calibrationParam = json_val.getString("calibrationParam");
      }

      if (json_val.has("resolution")) {
         this._resolution = (double)Math.round(json_val.getDouble("resolution") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("sensorState")) {
         this._sensorState = json_val.getInt("sensorState");
      }

      super._parseAttr(json_val);
   }

   public String get_unit() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._unit;
      }
   }

   public String getUnit() throws YAPI_Exception {
      return this.get_unit();
   }

   public double get_currentValue() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0) {
            return -1.79769313486231E308;
         } else {
            double res = this._applyCalibration(this._currentRawValue);
            if (res == -1.79769313486231E308) {
               res = this._currentValue;
            }

            res *= this._iresol;
            return (double)Math.round(res) / this._iresol;
         }
      }
   }

   public double getCurrentValue() throws YAPI_Exception {
      return this.get_currentValue();
   }

   public int set_lowestValue(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("lowestValue", rest_val);
         return 0;
      }
   }

   public int setLowestValue(double newval) throws YAPI_Exception {
      return this.set_lowestValue(newval);
   }

   public double get_lowestValue() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0) {
            return -1.79769313486231E308;
         } else {
            double res = this._lowestValue * this._iresol;
            return (double)Math.round(res) / this._iresol;
         }
      }
   }

   public double getLowestValue() throws YAPI_Exception {
      return this.get_lowestValue();
   }

   public int set_highestValue(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("highestValue", rest_val);
         return 0;
      }
   }

   public int setHighestValue(double newval) throws YAPI_Exception {
      return this.set_highestValue(newval);
   }

   public double get_highestValue() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0) {
            return -1.79769313486231E308;
         } else {
            double res = this._highestValue * this._iresol;
            return (double)Math.round(res) / this._iresol;
         }
      }
   }

   public double getHighestValue() throws YAPI_Exception {
      return this.get_highestValue();
   }

   public double get_currentRawValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentRawValue;
      }
   }

   public double getCurrentRawValue() throws YAPI_Exception {
      return this.get_currentRawValue();
   }

   public String get_logFrequency() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._logFrequency;
      }
   }

   public String getLogFrequency() throws YAPI_Exception {
      return this.get_logFrequency();
   }

   public int set_logFrequency(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("logFrequency", newval);
         return 0;
      }
   }

   public int setLogFrequency(String newval) throws YAPI_Exception {
      return this.set_logFrequency(newval);
   }

   public String get_reportFrequency() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._reportFrequency;
      }
   }

   public String getReportFrequency() throws YAPI_Exception {
      return this.get_reportFrequency();
   }

   public int set_reportFrequency(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("reportFrequency", newval);
         return 0;
      }
   }

   public int setReportFrequency(String newval) throws YAPI_Exception {
      return this.set_reportFrequency(newval);
   }

   public int get_advMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._advMode;
      }
   }

   public int getAdvMode() throws YAPI_Exception {
      return this.get_advMode();
   }

   public int set_advMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("advMode", rest_val);
         return 0;
      }
   }

   public int setAdvMode(int newval) throws YAPI_Exception {
      return this.set_advMode(newval);
   }

   public String get_calibrationParam() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._calibrationParam;
      }
   }

   public int set_calibrationParam(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("calibrationParam", newval);
         return 0;
      }
   }

   public int set_resolution(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("resolution", rest_val);
         return 0;
      }
   }

   public int setResolution(double newval) throws YAPI_Exception {
      return this.set_resolution(newval);
   }

   public double get_resolution() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._resolution;
      }
   }

   public double getResolution() throws YAPI_Exception {
      return this.get_resolution();
   }

   public int get_sensorState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._sensorState;
      }
   }

   public int getSensorState() throws YAPI_Exception {
      return this.get_sensorState();
   }

   public static YSensor FindSensor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YSensor obj = (YSensor)YFunction._FindFromCache("Sensor", func);
         if (obj == null) {
            obj = new YSensor(func);
            YFunction._AddToCache("Sensor", func, obj);
         }

         return obj;
      }
   }

   public static YSensor FindSensorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YSensor obj = (YSensor)YFunction._FindFromCacheInContext(yctx, "Sensor", func);
         if (obj == null) {
            obj = new YSensor(yctx, func);
            YFunction._AddToCache("Sensor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YSensor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackSensor = callback;
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
      if (this._valueCallbackSensor != null) {
         this._valueCallbackSensor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   @Override
   public int _parserHelper() {
      new ArrayList();
      this._caltyp = -1;
      this._scale = -1.0;
      this._calpar.clear();
      this._calraw.clear();
      this._calref.clear();
      if (this._resolution > 0.0) {
         this._iresol = (double)Math.round(1.0 / this._resolution);
      } else {
         this._iresol = 10000.0;
         this._resolution = 1.0E-4;
      }

      if (!this._calibrationParam.equals("") && !this._calibrationParam.equals("0")) {
         if (this._calibrationParam.indexOf(",") >= 0) {
            ArrayList<Integer> iCalib = YAPIContext._decodeFloats(this._calibrationParam);
            this._caltyp = (Integer)iCalib.get(0) / 1000;
            if (this._caltyp > 0) {
               if (this._caltyp < 30) {
                  this._caltyp = -1;
                  return 0;
               }

               this._calhdl = this._yapi._getCalibrationHandler(this._caltyp);
               if (this._calhdl == null) {
                  this._caltyp = -1;
                  return 0;
               }
            }

            this._offset = 0.0;
            this._scale = 1000.0;
            int maxpos = iCalib.size();
            this._calpar.clear();

            for (int position = 1; position < maxpos; position++) {
               this._calpar.add((Integer)iCalib.get(position));
            }

            this._calraw.clear();
            this._calref.clear();

            for (int var10 = 1; var10 + 1 < maxpos; var10 += 2) {
               double fRaw = ((Integer)iCalib.get(var10)).doubleValue();
               fRaw /= 1000.0;
               double fRef = ((Integer)iCalib.get(var10 + 1)).doubleValue();
               fRef /= 1000.0;
               this._calraw.add(fRaw);
               this._calref.add(fRef);
            }
         } else {
            ArrayList<Integer> var15 = YAPIContext._decodeWords(this._calibrationParam);
            if (var15.size() < 2) {
               this._caltyp = -1;
               return 0;
            }

            this._offset = 0.0;
            this._scale = 1.0;
            this._decexp = 1.0;

            for (int position = (Integer)var15.get(0); position > 0; position--) {
               this._decexp *= 10.0;
            }

            if (var15.size() == 2) {
               this._caltyp = 0;
               return 0;
            }

            this._caltyp = (Integer)var15.get(2);
            this._calhdl = this._yapi._getCalibrationHandler(this._caltyp);
            int maxpos;
            if (this._caltyp <= 10) {
               maxpos = this._caltyp;
            } else if (this._caltyp <= 20) {
               maxpos = this._caltyp - 10;
            } else {
               maxpos = 5;
            }

            maxpos = 3 + 2 * maxpos;
            if (maxpos > var15.size()) {
               maxpos = var15.size();
            }

            this._calpar.clear();
            this._calraw.clear();
            this._calref.clear();

            for (int var12 = 3; var12 + 1 < maxpos; var12 += 2) {
               int iRaw = (Integer)var15.get(var12);
               int iRef = (Integer)var15.get(var12 + 1);
               this._calpar.add(iRaw);
               this._calpar.add(iRef);
               this._calraw.add(YAPIContext._decimalToDouble(iRaw));
               this._calref.add(YAPIContext._decimalToDouble(iRef));
            }
         }

         return 0;
      } else {
         this._caltyp = 0;
         return 0;
      }
   }

   public boolean isSensorReady() {
      return !this.isOnline() ? false : this._sensorState == 0;
   }

   public YDataLogger get_dataLogger() throws YAPI_Exception {
      YModule modu = this.get_module();
      String serial = modu.get_serialNumber();
      if (serial.equals("!INVALID!")) {
         return null;
      } else {
         String hwid = serial + ".dataLogger";
         return YDataLogger.FindDataLogger(hwid);
      }
   }

   public int startDataLogger() throws YAPI_Exception {
      byte[] res = this._download("api/dataLogger/recording?recording=1");
      if (res.length <= 0) {
         throw new YAPI_Exception(-8, "unable to start datalogger");
      } else {
         return 0;
      }
   }

   public int stopDataLogger() throws YAPI_Exception {
      byte[] res = this._download("api/dataLogger/recording?recording=0");
      if (res.length <= 0) {
         throw new YAPI_Exception(-8, "unable to stop datalogger");
      } else {
         return 0;
      }
   }

   public YDataSet get_recordedData(double startTime, double endTime) throws YAPI_Exception {
      String funcid = this.get_functionId();
      String funit = this.get_unit();
      return new YDataSet(this, funcid, funit, startTime, endTime);
   }

   public int registerTimedReportCallback(YSensor.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackSensor = callback;
      return 0;
   }

   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackSensor != null) {
         this._timedReportCallbackSensor.timedReportCallback(this, value);
      }

      return 0;
   }

   public int calibrateFromPoints(ArrayList<Double> rawValues, ArrayList<Double> refValues) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = this._encodeCalibrationPoints(rawValues, refValues);
         return this._setAttr("calibrationParam", rest_val);
      }
   }

   public int loadCalibrationPoints(ArrayList<Double> rawValues, ArrayList<Double> refValues) throws YAPI_Exception {
      rawValues.clear();
      refValues.clear();
      synchronized (this) {
         if (this._scale == 0.0 && this.load(this._yapi._defaultCacheValidity) != 0) {
            return -4;
         } else if (this._caltyp < 0) {
            this._throw(-3, "Calibration parameters format mismatch. Please upgrade your library or firmware.");
            return -3;
         } else {
            rawValues.clear();
            refValues.clear();

            for (double ii : this._calraw) {
               rawValues.add(ii);
            }

            for (double ii : this._calref) {
               refValues.add(ii);
            }

            return 0;
         }
      }
   }

   public String _encodeCalibrationPoints(ArrayList<Double> rawValues, ArrayList<Double> refValues) throws YAPI_Exception {
      int npt = rawValues.size();
      if (npt != refValues.size()) {
         this._throw(-2, "Invalid calibration parameters (size mismatch)");
         return "!INVALID!";
      } else if (npt == 0) {
         return "0";
      } else if (this._scale == 0.0 && this.load(this._yapi._defaultCacheValidity) != 0) {
         return "!INVALID!";
      } else if (this._caltyp >= 0 && !(this._scale < 0.0)) {
         String res = String.format(Locale.US, "%d", 30);

         for (int idx = 0; idx < npt; idx++) {
            res = String.format(Locale.US, "%s,%f,%f", res, rawValues.get(idx), refValues.get(idx));
         }

         return res;
      } else {
         this._throw(-3, "Calibration parameters format mismatch. Please upgrade your library or firmware.");
         return "0";
      }
   }

   public double _applyCalibration(double rawValue) {
      if (rawValue == -1.79769313486231E308) {
         return -1.79769313486231E308;
      } else if (this._caltyp == 0) {
         return rawValue;
      } else if (this._caltyp < 0) {
         return -1.79769313486231E308;
      } else {
         return this._calhdl == null
            ? -1.79769313486231E308
            : this._calhdl.yCalibrationHandler(rawValue, this._caltyp, this._calpar, this._calraw, this._calref);
      }
   }

   public YMeasure _decodeTimedReport(double timestamp, double duration, ArrayList<Integer> report) {
      double startTime;
      if (duration > 0.0) {
         startTime = timestamp - duration;
      } else {
         startTime = this._prevTimedReport;
      }

      this._prevTimedReport = timestamp;
      if (startTime == 0.0) {
         startTime = timestamp;
      }

      double minVal;
      double avgVal;
      double maxVal;
      if (report.size() <= 5) {
         double poww = 1.0;
         double avgRaw = 0.0;
         int byteVal = 0;

         for (int i = 1; i < report.size(); i++) {
            byteVal = report.get(i);
            avgRaw += poww * (double)byteVal;
            poww *= 256.0;
         }

         if ((byteVal & 128) != 0) {
            avgRaw -= poww;
         }

         avgVal = avgRaw / 1000.0;
         if (this._caltyp != 0 && this._calhdl != null) {
            avgVal = this._calhdl.yCalibrationHandler(avgVal, this._caltyp, this._calpar, this._calraw, this._calref);
         }

         minVal = avgVal;
         maxVal = avgVal;
      } else {
         int sublen = 1 + (report.get(1) & 3);
         double poww = 1.0;
         double avgRaw = 0.0;
         int byteVal = 0;

         int i;
         for (i = 2; sublen > 0 && i < report.size(); sublen--) {
            byteVal = report.get(i);
            avgRaw += poww * (double)byteVal;
            poww *= 256.0;
            i++;
         }

         if ((byteVal & 128) != 0) {
            avgRaw -= poww;
         }

         sublen = 1 + (report.get(1) >> 2 & 3);
         poww = 1.0;

         double difRaw;
         for (difRaw = 0.0; sublen > 0 && i < report.size(); sublen--) {
            byteVal = report.get(i);
            difRaw += poww * (double)byteVal;
            poww *= 256.0;
            i++;
         }

         double minRaw = avgRaw - difRaw;
         sublen = 1 + (report.get(1) >> 4 & 3);
         poww = 1.0;

         for (difRaw = 0.0; sublen > 0 && i < report.size(); sublen--) {
            byteVal = report.get(i);
            difRaw += poww * (double)byteVal;
            poww *= 256.0;
            i++;
         }

         double maxRaw = avgRaw + difRaw;
         avgVal = avgRaw / 1000.0;
         minVal = minRaw / 1000.0;
         maxVal = maxRaw / 1000.0;
         if (this._caltyp != 0 && this._calhdl != null) {
            avgVal = this._calhdl.yCalibrationHandler(avgVal, this._caltyp, this._calpar, this._calraw, this._calref);
            minVal = this._calhdl.yCalibrationHandler(minVal, this._caltyp, this._calpar, this._calraw, this._calref);
            maxVal = this._calhdl.yCalibrationHandler(maxVal, this._caltyp, this._calpar, this._calraw, this._calref);
         }
      }

      return new YMeasure(startTime, timestamp, minVal, avgVal, maxVal);
   }

   public double _decodeVal(int w) {
      double val = (double)w;
      if (this._caltyp != 0 && this._calhdl != null) {
         val = this._calhdl.yCalibrationHandler(val, this._caltyp, this._calpar, this._calraw, this._calref);
      }

      return val;
   }

   public double _decodeAvg(int dw) {
      double val = (double)dw;
      if (this._caltyp != 0 && this._calhdl != null) {
         val = this._calhdl.yCalibrationHandler(val, this._caltyp, this._calpar, this._calraw, this._calref);
      }

      return val;
   }

   public YSensor nextSensor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindSensorInContext(this._yapi, next_hwid);
   }

   public static YSensor FirstSensor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Sensor");
         return next_hwid == null ? null : FindSensorInContext(yctx, next_hwid);
      }
   }

   public static YSensor FirstSensorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Sensor");
      return next_hwid == null ? null : FindSensorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YSensor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YSensor var1, String var2);
   }
}
