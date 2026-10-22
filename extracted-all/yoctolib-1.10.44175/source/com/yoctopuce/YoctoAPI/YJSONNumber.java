package com.yoctopuce.YoctoAPI;

class YJSONNumber extends YJSONContent {
   private long _intValue = 0L;
   private double _doubleValue = 0.0;
   private boolean _isFloat = false;

   YJSONNumber(String data, int start, int stop) {
      super(data, start, stop, YJSONContent.YJSONType.NUMBER);
   }

   @Override
   int parse() throws Exception {
      boolean neg = false;
      int cur_pos = SkipGarbage(this._data, this._data_start, this._data_boundary);
      if (cur_pos < this._data_boundary && this._data != null) {
         char sti = this._data.charAt(cur_pos);
         if (sti == '-') {
            neg = true;
            cur_pos++;
         }

         for (int start = cur_pos; cur_pos < this._data_boundary; cur_pos++) {
            sti = this._data.charAt(cur_pos);
            if (sti == '.' && !this._isFloat) {
               String int_part = this._data.substring(start, cur_pos);
               this._intValue = Long.valueOf(int_part);
               this._isFloat = true;
            } else if (sti < '0' || sti > '9') {
               String numberpart = this._data.substring(start, cur_pos);
               if (this._isFloat) {
                  this._doubleValue = Double.valueOf(numberpart);
               } else {
                  this._intValue = Long.valueOf(numberpart);
               }

               if (neg) {
                  this._doubleValue = 0.0 - this._doubleValue;
                  this._intValue = 0L - this._intValue;
               }

               return cur_pos - this._data_start;
            }
         }

         throw new Exception(this.formatError("unexpected end of data", cur_pos));
      } else {
         throw new Exception("no data");
      }
   }

   @Override
   String toJSON() {
      return this._isFloat ? Double.toString(this._doubleValue) : Long.toString(this._intValue);
   }

   long getLong() {
      return this._isFloat ? (long)this._doubleValue : this._intValue;
   }

   int getInt() {
      return this._isFloat ? (int)this._doubleValue : (int)this._intValue;
   }

   double getDouble() {
      return this._isFloat ? this._doubleValue : (double)this._intValue;
   }

   @Override
   public String toString() {
      return this._isFloat ? Double.toString(this._doubleValue) : Long.toString(this._intValue);
   }
}
