package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YDataStream {
   public static final double DATA_INVALID = -1.79769313486231E308;
   protected YFunction _parent;
   protected int _runNo = 0;
   protected long _utcStamp = 0L;
   protected int _nCols = 0;
   protected int _nRows = 0;
   protected double _startTime = 0.0;
   protected double _duration = 0.0;
   protected double _dataSamplesInterval = 0.0;
   protected double _firstMeasureDuration = 0.0;
   protected ArrayList<String> _columnNames = new ArrayList<>();
   protected String _functionId;
   protected boolean _isClosed;
   protected boolean _isAvg;
   protected double _minVal = 0.0;
   protected double _avgVal = 0.0;
   protected double _maxVal = 0.0;
   protected int _caltyp = 0;
   protected ArrayList<Integer> _calpar = new ArrayList<>();
   protected ArrayList<Double> _calraw = new ArrayList<>();
   protected ArrayList<Double> _calref = new ArrayList<>();
   protected ArrayList<ArrayList<Double>> _values = new ArrayList<>();
   protected YAPI.CalibrationHandlerCallback _calhdl = null;

   YDataStream(YFunction parent, YDataSet dataset, ArrayList<Integer> encoded) {
      this._parent = parent;
      this._initFromDataSet(dataset, encoded);
   }

   public int _initFromDataSet(YDataSet dataset, ArrayList<Integer> encoded) {
      new ArrayList();
      this._runNo = encoded.get(0) + (encoded.get(1) << 16);
      this._utcStamp = (long)(encoded.get(2) + (encoded.get(3) << 16));
      int val = encoded.get(4);
      this._isAvg = (val & 256) == 0;
      int samplesPerHour = val & 0xFF;
      if ((val & 256) != 0) {
         samplesPerHour *= 3600;
      } else if ((val & 512) != 0) {
         samplesPerHour *= 60;
      }

      this._dataSamplesInterval = 3600.0 / (double)samplesPerHour;
      int ms_offset = encoded.get(6);
      if (ms_offset < 1000) {
         this._startTime = (double)this._utcStamp + (double)ms_offset / 1000.0;
      } else {
         this._startTime = (double)this._utcStamp - this._dataSamplesInterval;
      }

      this._firstMeasureDuration = (double)encoded.get(5).intValue();
      if (!this._isAvg) {
         this._firstMeasureDuration /= 1000.0;
      }

      val = encoded.get(7);
      this._isClosed = val != 65535;
      if (val == 65535) {
         val = 0;
      }

      this._nRows = val;
      if (this._nRows > 0) {
         if (this._firstMeasureDuration > 0.0) {
            this._duration = this._firstMeasureDuration + (double)(this._nRows - 1) * this._dataSamplesInterval;
         } else {
            this._duration = (double)this._nRows * this._dataSamplesInterval;
         }
      } else {
         this._duration = 0.0;
      }

      ArrayList<Integer> iCalib = dataset._get_calibration();
      this._caltyp = (Integer)iCalib.get(0);
      if (this._caltyp != 0) {
         this._calhdl = this._parent._yapi._getCalibrationHandler(this._caltyp);
         int maxpos = iCalib.size();
         this._calpar.clear();
         this._calraw.clear();
         this._calref.clear();

         for (int i = 1; i < maxpos; i++) {
            this._calpar.add((Integer)iCalib.get(i));
         }

         for (int var14 = 1; var14 + 1 < maxpos; var14 += 2) {
            double fRaw = ((Integer)iCalib.get(var14)).doubleValue();
            fRaw /= 1000.0;
            double fRef = ((Integer)iCalib.get(var14 + 1)).doubleValue();
            fRef /= 1000.0;
            this._calraw.add(fRaw);
            this._calref.add(fRef);
         }
      }

      this._functionId = dataset.get_functionId();
      if (this._isAvg) {
         this._columnNames.clear();
         this._columnNames.add(String.format(Locale.US, "%s_min", this._functionId));
         this._columnNames.add(String.format(Locale.US, "%s_avg", this._functionId));
         this._columnNames.add(String.format(Locale.US, "%s_max", this._functionId));
         this._nCols = 3;
      } else {
         this._columnNames.clear();
         this._columnNames.add(this._functionId);
         this._nCols = 1;
      }

      if (this._nRows > 0) {
         this._avgVal = this._decodeAvg(encoded.get(8) + ((encoded.get(9) ^ 32768) << 16), 1);
         this._minVal = this._decodeVal(encoded.get(10) + (encoded.get(11) << 16));
         this._maxVal = this._decodeVal(encoded.get(12) + (encoded.get(13) << 16));
      }

      return 0;
   }

   public int _parseStream(byte[] sdata) throws YAPI_Exception {
      new ArrayList();
      ArrayList<Double> dat = new ArrayList<>();
      if (sdata.length == 0) {
         this._nRows = 0;
         return 0;
      } else {
         ArrayList<Integer> udat = YAPIContext._decodeWords(this._parent._json_get_string(sdata));
         this._values.clear();
         int idx = 0;
         if (this._isAvg) {
            while (idx + 3 < udat.size()) {
               dat.clear();
               dat.add(this._decodeVal((Integer)udat.get(idx + 2) + ((Integer)udat.get(idx + 3) << 16)));
               dat.add(this._decodeAvg((Integer)udat.get(idx) + (((Integer)udat.get(idx + 1) ^ 32768) << 16), 1));
               dat.add(this._decodeVal((Integer)udat.get(idx + 4) + ((Integer)udat.get(idx + 5) << 16)));
               idx += 6;
               this._values.add(new ArrayList<>(dat));
            }
         } else {
            while (idx + 1 < udat.size()) {
               dat.clear();
               dat.add(this._decodeAvg((Integer)udat.get(idx) + (((Integer)udat.get(idx + 1) ^ 32768) << 16), 1));
               this._values.add(new ArrayList<>(dat));
               idx += 2;
            }
         }

         this._nRows = this._values.size();
         return 0;
      }
   }

   public String _get_url() {
      return String.format(Locale.US, "logger.json?id=%s&run=%d&utc=%d", this._functionId, this._runNo, this._utcStamp);
   }

   public int loadStream() throws YAPI_Exception {
      return this._parseStream(this._parent._download(this._get_url()));
   }

   public double _decodeVal(int w) {
      double val = (double)w;
      val /= 1000.0;
      if (this._caltyp != 0 && this._calhdl != null) {
         val = this._calhdl.yCalibrationHandler(val, this._caltyp, this._calpar, this._calraw, this._calref);
      }

      return val;
   }

   public double _decodeAvg(int dw, int count) {
      double val = (double)dw;
      val /= 1000.0;
      if (this._caltyp != 0 && this._calhdl != null) {
         val = this._calhdl.yCalibrationHandler(val, this._caltyp, this._calpar, this._calraw, this._calref);
      }

      return val;
   }

   public boolean isClosed() {
      return this._isClosed;
   }

   public int get_runIndex() {
      return this._runNo;
   }

   public int get_startTime() {
      return (int)(this._utcStamp - System.currentTimeMillis() / 1000L);
   }

   public long get_startTimeUTC() {
      return (long)((int)((double)Math.round(this._startTime)));
   }

   public double get_realStartTimeUTC() {
      return this._startTime;
   }

   public int get_dataSamplesIntervalMs() {
      return (int)((double)Math.round(this._dataSamplesInterval * 1000.0));
   }

   public double get_dataSamplesInterval() {
      return this._dataSamplesInterval;
   }

   public double get_firstDataSamplesInterval() {
      return this._firstMeasureDuration;
   }

   public int get_rowCount() throws YAPI_Exception {
      if (this._nRows != 0 && this._isClosed) {
         return this._nRows;
      } else {
         this.loadStream();
         return this._nRows;
      }
   }

   public int get_columnCount() throws YAPI_Exception {
      if (this._nCols != 0) {
         return this._nCols;
      } else {
         this.loadStream();
         return this._nCols;
      }
   }

   public ArrayList<String> get_columnNames() throws YAPI_Exception {
      if (this._columnNames.size() != 0) {
         return this._columnNames;
      } else {
         this.loadStream();
         return this._columnNames;
      }
   }

   public double get_minValue() throws YAPI_Exception {
      return this._minVal;
   }

   public double get_averageValue() throws YAPI_Exception {
      return this._avgVal;
   }

   public double get_maxValue() throws YAPI_Exception {
      return this._maxVal;
   }

   public double get_realDuration() {
      return this._isClosed ? this._duration : (double)(System.currentTimeMillis() / 1000L) - (double)this._utcStamp;
   }

   public ArrayList<ArrayList<Double>> get_dataRows() throws YAPI_Exception {
      if (this._values.size() == 0 || !this._isClosed) {
         this.loadStream();
      }

      return this._values;
   }

   public double get_data(int row, int col) throws YAPI_Exception {
      if (this._values.size() == 0 || !this._isClosed) {
         this.loadStream();
      }

      if (row >= this._values.size()) {
         return -1.79769313486231E308;
      } else {
         return col >= this._values.get(row).size() ? -1.79769313486231E308 : this._values.get(row).get(col);
      }
   }
}
