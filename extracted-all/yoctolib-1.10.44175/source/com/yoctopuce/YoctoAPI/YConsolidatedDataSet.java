package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;

public class YConsolidatedDataSet {
   protected double _start = 0.0;
   protected double _end = 0.0;
   protected int _nsensors = 0;
   protected ArrayList<YSensor> _sensors = new ArrayList<>();
   protected ArrayList<YDataSet> _datasets = new ArrayList<>();
   protected ArrayList<Integer> _progresss = new ArrayList<>();
   protected ArrayList<Integer> _nextidx = new ArrayList<>();
   protected ArrayList<Double> _nexttim = new ArrayList<>();

   public YConsolidatedDataSet(double startTime, double endTime, ArrayList<YSensor> sensorList) {
      this.imm_init(startTime, endTime, sensorList);
   }

   public int imm_init(double startt, double endt, ArrayList<YSensor> sensorList) {
      this._start = startt;
      this._end = endt;
      this._sensors = sensorList;
      this._nsensors = -1;
      return 0;
   }

   public static YConsolidatedDataSet Init(ArrayList<String> sensorNames, double startTime, double endTime) throws YAPI_Exception {
      ArrayList<YSensor> sensorList = new ArrayList<>();
      int nSensors = sensorNames.size();
      sensorList.clear();

      for (int idx = 0; idx < nSensors; idx++) {
         String sensorName = sensorNames.get(idx);
         YSensor s = YSensor.FindSensor(sensorName);
         sensorList.add(s);
      }

      return new YConsolidatedDataSet(startTime, endTime, sensorList);
   }

   public int nextRecord(ArrayList<Double> datarec) throws YAPI_Exception {
      new ArrayList();
      if (this._nsensors == -1) {
         this._nsensors = this._sensors.size();
         this._datasets.clear();
         this._progresss.clear();
         this._nextidx.clear();
         this._nexttim.clear();

         for (int s = 0; s < this._nsensors; s++) {
            YSensor sensor = this._sensors.get(s);
            YDataSet newdataset = sensor.get_recordedData(this._start, this._end);
            this._datasets.add(newdataset);
            this._progresss.add(0);
            this._nextidx.add(0);
            this._nexttim.add(0.0);
         }
      }

      datarec.clear();
      double nexttime = 0.0;

      for (int s = 0; s < this._nsensors; s++) {
         double currnexttim = this._nexttim.get(s);
         if (currnexttim == 0.0) {
            int idx = this._nextidx.get(s);
            ArrayList<YMeasure> measures = this._datasets.get(s).get_measures();

            for (int currprogress = this._progresss.get(s); idx >= measures.size() && currprogress < 100; measures = this._datasets.get(s).get_measures()) {
               currprogress = this._datasets.get(s).loadMore();
               if (currprogress < 0) {
                  currprogress = 100;
               }

               this._progresss.set(s, currprogress);
            }

            if (idx < measures.size()) {
               currnexttim = ((YMeasure)measures.get(idx)).get_endTimeUTC();
               this._nexttim.set(s, currnexttim);
            }
         }

         if (currnexttim > 0.0 && (nexttime == 0.0 || nexttime > currnexttim)) {
            nexttime = currnexttim;
         }
      }

      if (nexttime == 0.0) {
         return 100;
      } else {
         datarec.clear();
         datarec.add(nexttime);
         int globprogress = 0;

         for (int var16 = 0; var16 < this._nsensors; var16++) {
            if (this._nexttim.get(var16) == nexttime) {
               int idx = this._nextidx.get(var16);
               ArrayList<YMeasure> var19 = this._datasets.get(var16).get_measures();
               double newvalue = ((YMeasure)var19.get(idx)).get_averageValue();
               datarec.add(newvalue);
               this._nexttim.set(var16, 0.0);
               this._nextidx.set(var16, idx + 1);
            } else {
               datarec.add(Double.NaN);
            }

            int currprogress = this._progresss.get(var16);
            globprogress += currprogress;
         }

         if (globprogress > 0) {
            globprogress /= this._nsensors;
            if (globprogress > 99) {
               globprogress = 99;
            }
         }

         return globprogress;
      }
   }
}
