package com.yoctopuce.YoctoAPI;

abstract class YJSONContent {
   final String _data;
   int _data_start;
   int _data_len;
   int _data_boundary;
   private final YJSONContent.YJSONType _type;

   static YJSONContent ParseJson(String data, int start, int stop) throws Exception {
      int cur_pos = SkipGarbage(data, start, stop);
      char c = data.charAt(cur_pos);
      YJSONContent res;
      if (c == '[') {
         res = new YJSONArray(data, start, stop);
      } else if (c == '{') {
         res = new YJSONObject(data, start, stop);
      } else if (c == '"') {
         res = new YJSONString(data, start, stop);
      } else {
         res = new YJSONNumber(data, start, stop);
      }

      res.parse();
      return res;
   }

   YJSONContent(String data, int start, int stop, YJSONContent.YJSONType type) {
      this._data = data;
      this._data_start = start;
      this._data_boundary = stop;
      this._type = type;
   }

   YJSONContent(YJSONContent.YJSONType type) {
      this._data = null;
      this._type = type;
   }

   YJSONContent.YJSONType getJSONType() {
      return this._type;
   }

   abstract int parse() throws Exception;

   static int SkipGarbage(String data, int start, int stop) {
      if (stop <= start) {
         return start;
      } else {
         while (start < stop) {
            char sti = data.charAt(start);
            if (sti != ' ' && sti != '\n' && sti != '\r') {
               break;
            }

            start++;
         }

         return start;
      }
   }

   String formatError(String errmsg, int cur_pos) {
      int ststart = cur_pos - 10;
      int stend = cur_pos + 10;
      if (ststart < 0) {
         ststart = 0;
      }

      if (stend > this._data_boundary) {
         stend = this._data_boundary;
      }

      return this._data != null ? errmsg + " near " + this._data.substring(ststart, stend) : errmsg;
   }

   abstract String toJSON();

   static enum Tjstate {
      JSTART,
      JWAITFORNAME,
      JWAITFORENDOFNAME,
      JWAITFORCOLON,
      JWAITFORDATA,
      JWAITFORNEXTSTRUCTMEMBER,
      JWAITFORNEXTARRAYITEM,
      JWAITFORSTRINGVALUE,
      JWAITFORSTRINGVALUE_ESC,
      JWAITFORINTVALUE,
      JWAITFORBOOLVALUE;
   }

   static enum YJSONType {
      STRING,
      NUMBER,
      ARRAY,
      OBJECT;
   }
}
