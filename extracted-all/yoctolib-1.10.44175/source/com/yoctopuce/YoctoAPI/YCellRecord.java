package com.yoctopuce.YoctoAPI;

public class YCellRecord {
   protected String _oper;
   protected int _mcc = 0;
   protected int _mnc = 0;
   protected int _lac = 0;
   protected int _cid = 0;
   protected int _dbm = 0;
   protected int _tad = 0;

   public YCellRecord(int mcc, int mnc, int lac, int cellId, int dbm, int tad, String oper) {
      this._oper = oper;
      this._mcc = mcc;
      this._mnc = mnc;
      this._lac = lac;
      this._cid = cellId;
      this._dbm = dbm;
      this._tad = tad;
   }

   public String get_cellOperator() {
      return this._oper;
   }

   public int get_mobileCountryCode() {
      return this._mcc;
   }

   public int get_mobileNetworkCode() {
      return this._mnc;
   }

   public int get_locationAreaCode() {
      return this._lac;
   }

   public int get_cellId() {
      return this._cid;
   }

   public int get_signalStrength() {
      return this._dbm;
   }

   public int get_timingAdvance() {
      return this._tad;
   }
}
