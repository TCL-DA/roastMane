package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;

class YJSONArray extends YJSONContent {
   private ArrayList<YJSONContent> _arrayValue = new ArrayList<>();

   YJSONArray(String data, int start, int stop) {
      super(data, start, stop, YJSONContent.YJSONType.ARRAY);
   }

   YJSONArray(String data) {
      super(data, 0, data.length(), YJSONContent.YJSONType.ARRAY);
   }

   YJSONArray() {
      super(YJSONContent.YJSONType.ARRAY);
   }

   int length() {
      return this._arrayValue.size();
   }

   @Override
   int parse() throws Exception {
      int cur_pos = SkipGarbage(this._data, this._data_start, this._data_boundary);
      if (cur_pos < this._data_boundary && this._data.charAt(cur_pos) == '[') {
         cur_pos++;
         YJSONContent.Tjstate state = YJSONContent.Tjstate.JWAITFORDATA;

         while (cur_pos < this._data_boundary) {
            char sti = this._data.charAt(cur_pos);
            switch (state) {
               case JWAITFORDATA:
                  if (sti == '{') {
                     YJSONObject jobj = new YJSONObject(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._arrayValue.add(jobj);
                     state = YJSONContent.Tjstate.JWAITFORNEXTARRAYITEM;
                     continue;
                  }

                  if (sti == '[') {
                     YJSONArray jobj = new YJSONArray(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._arrayValue.add(jobj);
                     state = YJSONContent.Tjstate.JWAITFORNEXTARRAYITEM;
                     continue;
                  }

                  if (sti == '"') {
                     YJSONString jobj = new YJSONString(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._arrayValue.add(jobj);
                     state = YJSONContent.Tjstate.JWAITFORNEXTARRAYITEM;
                     continue;
                  }

                  if (sti == '-' || sti >= '0' && sti <= '9') {
                     YJSONNumber jobj = new YJSONNumber(this._data, cur_pos, this._data_boundary);
                     int len = jobj.parse();
                     cur_pos += len;
                     this._arrayValue.add(jobj);
                     state = YJSONContent.Tjstate.JWAITFORNEXTARRAYITEM;
                     continue;
                  }

                  if (sti == ']') {
                     this._data_len = cur_pos + 1 - this._data_start;
                     return this._data_len;
                  }

                  if (sti != ' ' && sti != '\n' && sti != '\r') {
                     throw new Exception(this.formatError("invalid char: was expecting  \",0..9,t or f", cur_pos));
                  }
                  break;
               case JWAITFORNEXTARRAYITEM:
                  if (sti == ',') {
                     state = YJSONContent.Tjstate.JWAITFORDATA;
                  } else {
                     if (sti == ']') {
                        this._data_len = cur_pos + 1 - this._data_start;
                        return this._data_len;
                     }

                     if (sti != ' ' && sti != '\n' && sti != '\r') {
                        throw new Exception(this.formatError("invalid char: was expecting ,", cur_pos));
                     }
                  }
                  break;
               default:
                  throw new Exception(this.formatError("invalid state for YJSONObject", cur_pos));
            }

            cur_pos++;
         }

         throw new Exception(this.formatError("unexpected end of data", cur_pos));
      } else {
         throw new Exception(this.formatError("Opening braces was expected", cur_pos));
      }
   }

   YJSONObject getYJSONObject(int i) {
      return (YJSONObject)this._arrayValue.get(i);
   }

   String getString(int i) {
      YJSONString ystr = (YJSONString)this._arrayValue.get(i);
      return ystr.getString();
   }

   YJSONContent get(int i) {
      return this._arrayValue.get(i);
   }

   YJSONArray getYJSONArray(int i) {
      return (YJSONArray)this._arrayValue.get(i);
   }

   int getInt(int i) {
      YJSONNumber ystr = (YJSONNumber)this._arrayValue.get(i);
      return ystr.getInt();
   }

   long getLong(int i) {
      YJSONNumber ystr = (YJSONNumber)this._arrayValue.get(i);
      return ystr.getLong();
   }

   double getDouble(int i) {
      YJSONNumber ystr = (YJSONNumber)this._arrayValue.get(i);
      return ystr.getDouble();
   }

   void put(String flatAttr) {
      YJSONString strobj = new YJSONString();
      strobj.setContent(flatAttr);
      this._arrayValue.add(strobj);
   }

   @Override
   String toJSON() {
      StringBuilder res = new StringBuilder();
      res.append('[');
      String sep = "";

      for (YJSONContent yjsonContent : this._arrayValue) {
         String subres = yjsonContent.toJSON();
         res.append(sep);
         res.append(subres);
         sep = ",";
      }

      res.append(']');
      return res.toString();
   }

   @Override
   public String toString() {
      StringBuilder res = new StringBuilder();
      res.append('[');
      String sep = "";

      for (YJSONContent yjsonContent : this._arrayValue) {
         String subres = yjsonContent.toString();
         res.append(sep);
         res.append(subres);
         sep = ",";
      }

      res.append(']');
      return res.toString();
   }
}
