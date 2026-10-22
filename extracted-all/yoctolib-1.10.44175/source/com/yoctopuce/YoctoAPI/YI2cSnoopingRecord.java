package com.yoctopuce.YoctoAPI;

public class YI2cSnoopingRecord {
   protected int _tim = 0;
   protected int _dir = 0;
   protected String _msg;

   YI2cSnoopingRecord(String json_str) throws YAPI_Exception {
      try {
         YJSONObject json = new YJSONObject(json_str);
         json.parse();
         this._tim = json.getInt("t");
         String m = json.getString("m");
         this._dir = m.charAt(0) == '<' ? 1 : 0;
         this._msg = m.substring(1);
      } catch (Exception var4) {
         throw new YAPI_Exception(-8, "invalid json struct for YI2cSnoopingRecord");
      }
   }

   public int get_time() {
      return this._tim;
   }

   public int get_direction() {
      return this._dir;
   }

   public String get_message() {
      return this._msg;
   }
}
