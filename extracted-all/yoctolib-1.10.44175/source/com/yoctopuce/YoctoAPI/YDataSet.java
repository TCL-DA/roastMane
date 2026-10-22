package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YDataSet {
   protected YFunction _parent;
   protected String _hardwareId;
   protected String _functionId;
   protected String _unit;
   protected double _startTimeMs = 0.0;
   protected double _endTimeMs = 0.0;
   protected int _progress = 0;
   protected ArrayList<Integer> _calib = new ArrayList<>();
   protected ArrayList<YDataStream> _streams = new ArrayList<>();
   protected YMeasure _summary;
   protected ArrayList<YMeasure> _preview = new ArrayList<>();
   protected ArrayList<YMeasure> _measures = new ArrayList<>();
   protected double _summaryMinVal = 0.0;
   protected double _summaryMaxVal = 0.0;
   protected double _summaryTotalAvg = 0.0;
   protected double _summaryTotalTime = 0.0;

   YDataSet(YFunction parent, String functionId, String unit, double startTime, double endTime) {
      this._parent = parent;
      this._functionId = functionId;
      this._unit = unit;
      this._startTimeMs = startTime * 1000.0;
      this._endTimeMs = endTime * 1000.0;
      this._progress = -1;
      this._hardwareId = "";
      this._summary = new YMeasure();
   }

   public YDataSet(YFunction parent) {
      this._parent = parent;
      this._startTimeMs = 0.0;
      this._endTimeMs = 0.0;
      this._hardwareId = "";
      this._summary = new YMeasure();
   }

   protected int _parse(String json_str) throws YAPI_Exception {
      try {
         YJSONObject json = new YJSONObject(json_str);
         json.parse();
         this._functionId = json.getString("id");
         this._unit = json.getString("unit");
         if (json.has("calib")) {
            this._calib = YAPIContext._decodeFloats(json.getString("calib"));
            this._calib.set(0, this._calib.get(0) / 1000);
         } else {
            this._calib = YAPIContext._decodeWords(json.getString("cal"));
         }

         this._streams = new ArrayList<>();
         this._preview = new ArrayList<>();
         this._measures = new ArrayList<>();
         YJSONArray jstreams = json.getYJSONArray("streams");

         for (int i = 0; i < jstreams.length(); i++) {
            YDataStream stream = this._parent._findDataStream(this, jstreams.getString(i));
            double streamStartTime = (double)Math.round(stream.get_realStartTimeUTC() * 1000.0);
            double streamEndTime = (double)Math.round(streamStartTime + stream.get_realDuration() * 1000.0);
            if ((!(this._startTimeMs > 0.0) || !(streamEndTime <= this._startTimeMs)) && (!(this._endTimeMs > 0.0) || !(streamStartTime >= this._endTimeMs))) {
               this._streams.add(stream);
            }
         }
      } catch (Exception var10) {
         throw new YAPI_Exception(-8, "invalid json structure for YDataSet: " + var10.getMessage());
      }

      this._progress = 0;
      return this.get_progress();
   }

   public ArrayList<Integer> _get_calibration() {
      return this._calib;
   }

   public int loadSummary(byte[] data) throws YAPI_Exception {
      new ArrayList();
      new ArrayList();
      if (this._progress < 0) {
         String strdata = new String(data);
         if (strdata.equals("{}")) {
            this._parent._throw(-5, "device firmware is too old");
            return -5;
         }

         int res = this._parse(strdata);
         if (res < 0) {
            return res;
         }
      }

      double summaryTotalTime = 0.0;
      double summaryTotalAvg = 0.0;
      double summaryMinVal = Double.MAX_VALUE;
      double summaryMaxVal = Double.MIN_VALUE;
      double summaryStartMs = Double.MAX_VALUE;
      double summaryStopMs = Double.MIN_VALUE;

      for (YDataStream ii : this._streams) {
         double streamStartTimeMs = (double)Math.round(ii.get_realStartTimeUTC() * 1000.0);
         double streamDuration = ii.get_realDuration();
         double streamEndTimeMs = streamStartTimeMs + (double)Math.round(streamDuration * 1000.0);
         double previewMinVal;
         double previewMaxVal;
         double previewAvgVal;
         double previewStartMs;
         double previewStopMs;
         double previewDuration;
         if (!(streamStartTimeMs >= this._startTimeMs) || this._endTimeMs != 0.0 && !(streamEndTimeMs <= this._endTimeMs)) {
            String url = ii._get_url();
            data = this._parent._download(url);
            ii._parseStream(data);
            ArrayList<ArrayList<Double>> dataRows = ii.get_dataRows();
            if (dataRows.size() == 0) {
               return this.get_progress();
            }

            double tim = streamStartTimeMs;
            double fitv = (double)Math.round(ii.get_firstDataSamplesInterval() * 1000.0);
            double itv = (double)Math.round(ii.get_dataSamplesInterval() * 1000.0);
            int nCols = ((ArrayList)dataRows.get(0)).size();
            int minCol = 0;
            int avgCol;
            if (nCols > 2) {
               avgCol = 1;
            } else {
               avgCol = 0;
            }

            int maxCol;
            if (nCols > 2) {
               maxCol = 2;
            } else {
               maxCol = 0;
            }

            double previewTotalTime = 0.0;
            double previewTotalAvg = 0.0;
            previewStartMs = streamEndTimeMs;
            previewStopMs = streamStartTimeMs;
            previewMinVal = Double.MAX_VALUE;
            previewMaxVal = Double.MIN_VALUE;

            for (int m_pos = 0; m_pos < dataRows.size(); m_pos++) {
               ArrayList<Double> measure_data = (ArrayList)dataRows.get(m_pos);
               double mitv;
               if (m_pos == 0) {
                  mitv = fitv;
               } else {
                  mitv = itv;
               }

               double end_ = tim + mitv;
               if (end_ > this._startTimeMs && (this._endTimeMs == 0.0 || tim < this._endTimeMs)) {
                  double minVal = (Double)measure_data.get(minCol);
                  double avgVal = (Double)measure_data.get(avgCol);
                  double maxVal = (Double)measure_data.get(maxCol);
                  if (previewStartMs > tim) {
                     previewStartMs = tim;
                  }

                  if (previewStopMs < end_) {
                     previewStopMs = end_;
                  }

                  if (previewMinVal > minVal) {
                     previewMinVal = minVal;
                  }

                  if (previewMaxVal < maxVal) {
                     previewMaxVal = maxVal;
                  }

                  previewTotalAvg += avgVal * mitv;
                  previewTotalTime += mitv;
               }

               tim = end_;
            }

            if (previewTotalTime > 0.0) {
               previewAvgVal = previewTotalAvg / previewTotalTime;
               previewDuration = (previewStopMs - previewStartMs) / 1000.0;
            } else {
               previewAvgVal = 0.0;
               previewDuration = 0.0;
            }
         } else {
            previewMinVal = ii.get_minValue();
            previewAvgVal = ii.get_averageValue();
            previewMaxVal = ii.get_maxValue();
            previewStartMs = streamStartTimeMs;
            previewStopMs = streamEndTimeMs;
            previewDuration = streamDuration;
         }

         this._preview.add(new YMeasure(previewStartMs / 1000.0, previewStopMs / 1000.0, previewMinVal, previewAvgVal, previewMaxVal));
         if (summaryMinVal > previewMinVal) {
            summaryMinVal = previewMinVal;
         }

         if (summaryMaxVal < previewMaxVal) {
            summaryMaxVal = previewMaxVal;
         }

         if (summaryStartMs > previewStartMs) {
            summaryStartMs = previewStartMs;
         }

         if (summaryStopMs < previewStopMs) {
            summaryStopMs = previewStopMs;
         }

         summaryTotalAvg += previewAvgVal * previewDuration;
         summaryTotalTime += previewDuration;
      }

      if (this._startTimeMs == 0.0 || this._startTimeMs > summaryStartMs) {
         this._startTimeMs = summaryStartMs;
      }

      if (this._endTimeMs == 0.0 || this._endTimeMs < summaryStopMs) {
         this._endTimeMs = summaryStopMs;
      }

      if (summaryTotalTime > 0.0) {
         this._summary = new YMeasure(summaryStartMs / 1000.0, summaryStopMs / 1000.0, summaryMinVal, summaryTotalAvg / summaryTotalTime, summaryMaxVal);
      } else {
         this._summary = new YMeasure(0.0, 0.0, -1.79769313486231E308, -1.79769313486231E308, -1.79769313486231E308);
      }

      return this.get_progress();
   }

   public int processMore(int progress, byte[] data) throws YAPI_Exception {
      new ArrayList();
      if (progress != this._progress) {
         return this._progress;
      } else if (this._progress < 0) {
         return this.loadSummary(data);
      } else {
         YDataStream stream = this._streams.get(this._progress);
         stream._parseStream(data);
         ArrayList<ArrayList<Double>> dataRows = stream.get_dataRows();
         this._progress++;
         if (dataRows.size() == 0) {
            return this.get_progress();
         } else {
            double tim = (double)Math.round(stream.get_realStartTimeUTC() * 1000.0);
            double fitv = (double)Math.round(stream.get_firstDataSamplesInterval() * 1000.0);
            double itv = (double)Math.round(stream.get_dataSamplesInterval() * 1000.0);
            if (fitv == 0.0) {
               fitv = itv;
            }

            if (tim < itv) {
               tim = itv;
            }

            int nCols = ((ArrayList)dataRows.get(0)).size();
            int minCol = 0;
            int avgCol;
            if (nCols > 2) {
               avgCol = 1;
            } else {
               avgCol = 0;
            }

            int maxCol;
            if (nCols > 2) {
               maxCol = 2;
            } else {
               maxCol = 0;
            }

            boolean firstMeasure = true;

            for (ArrayList<Double> ii : dataRows) {
               double end_;
               if (firstMeasure) {
                  end_ = tim + fitv;
                  firstMeasure = false;
               } else {
                  end_ = tim + itv;
               }

               if (end_ > this._startTimeMs && (this._endTimeMs == 0.0 || tim < this._endTimeMs)) {
                  this._measures.add(new YMeasure(tim / 1000.0, end_ / 1000.0, ii.get(minCol), ii.get(avgCol), ii.get(maxCol)));
               }

               tim = end_;
            }

            return this.get_progress();
         }
      }
   }

   public ArrayList<YDataStream> get_privateDataStreams() {
      return this._streams;
   }

   public String get_hardwareId() throws YAPI_Exception {
      if (!this._hardwareId.equals("")) {
         return this._hardwareId;
      } else {
         YModule mo = this._parent.get_module();
         this._hardwareId = String.format(Locale.US, "%s.%s", mo.get_serialNumber(), this.get_functionId());
         return this._hardwareId;
      }
   }

   public String get_functionId() {
      return this._functionId;
   }

   public String get_unit() throws YAPI_Exception {
      return this._unit;
   }

   public long get_startTimeUTC() {
      return this.imm_get_startTimeUTC();
   }

   public long imm_get_startTimeUTC() {
      return (long)(this._startTimeMs / 1000.0);
   }

   public long get_endTimeUTC() {
      return this.imm_get_endTimeUTC();
   }

   public long imm_get_endTimeUTC() {
      return (long)((double)Math.round(this._endTimeMs / 1000.0));
   }

   public int get_progress() {
      if (this._progress < 0) {
         return 0;
      } else {
         return this._progress >= this._streams.size() ? 100 : (1 + (1 + this._progress) * 98) / (1 + this._streams.size());
      }
   }

   public int loadMore() throws YAPI_Exception {
      String url;
      if (this._progress < 0) {
         url = String.format(Locale.US, "logger.json?id=%s", this._functionId);
         if (this._startTimeMs != 0.0) {
            url = String.format(Locale.US, "%s&from=%d", url, this.imm_get_startTimeUTC());
         }

         if (this._endTimeMs != 0.0) {
            url = String.format(Locale.US, "%s&to=%d", url, this.imm_get_endTimeUTC() + 1L);
         }
      } else {
         if (this._progress >= this._streams.size()) {
            return 100;
         }

         YDataStream stream = this._streams.get(this._progress);
         url = stream._get_url();
      }

      try {
         return this.processMore(this._progress, this._parent._download(url));
      } catch (Exception var4) {
         return this.processMore(this._progress, this._parent._download(url));
      }
   }

   public YMeasure get_summary() {
      return this._summary;
   }

   public ArrayList<YMeasure> get_preview() throws YAPI_Exception {
      return this._preview;
   }

   public ArrayList<YMeasure> get_measuresAt(YMeasure measure) throws YAPI_Exception {
      new ArrayList();
      ArrayList<YMeasure> measures = new ArrayList<>();
      double startUtcMs = measure.get_startTimeUTC() * 1000.0;
      YDataStream stream = null;

      for (YDataStream ii : this._streams) {
         if ((double)Math.round(ii.get_realStartTimeUTC() * 1000.0) == startUtcMs) {
            stream = ii;
         }
      }

      if (stream == null) {
         return measures;
      } else {
         ArrayList<ArrayList<Double>> dataRows = stream.get_dataRows();
         if (dataRows.size() == 0) {
            return measures;
         } else {
            double tim = (double)Math.round(stream.get_realStartTimeUTC() * 1000.0);
            double itv = (double)Math.round(stream.get_dataSamplesInterval() * 1000.0);
            if (tim < itv) {
               tim = itv;
            }

            int nCols = ((ArrayList)dataRows.get(0)).size();
            int minCol = 0;
            int avgCol;
            if (nCols > 2) {
               avgCol = 1;
            } else {
               avgCol = 0;
            }

            int maxCol;
            if (nCols > 2) {
               maxCol = 2;
            } else {
               maxCol = 0;
            }

            for (ArrayList<Double> iix : dataRows) {
               double end_ = tim + itv;
               if (end_ > this._startTimeMs && (this._endTimeMs == 0.0 || tim < this._endTimeMs)) {
                  measures.add(new YMeasure(tim / 1000.0, end_ / 1000.0, iix.get(minCol), iix.get(avgCol), iix.get(maxCol)));
               }

               tim = end_;
            }

            return measures;
         }
      }
   }

   public ArrayList<YMeasure> get_measures() throws YAPI_Exception {
      return this._measures;
   }
}
