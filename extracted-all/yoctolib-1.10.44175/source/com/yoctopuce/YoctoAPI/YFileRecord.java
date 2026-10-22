package com.yoctopuce.YoctoAPI;

public class YFileRecord {
   protected String _name;
   protected int _size = 0;
   protected int _crc = 0;

   public YFileRecord(String json_str) throws YAPI_Exception {
      try {
         YJSONObject json = new YJSONObject(json_str);
         json.parse();
         this._name = json.getString("name");
         this._crc = json.getInt("crc");
         this._size = json.getInt("size");
      } catch (Exception var3) {
         throw new YAPI_Exception(-8, "invalid json struct for YFileRecord");
      }
   }

   public String get_name() {
      return this._name;
   }

   public int get_size() {
      return this._size;
   }

   public int get_crc() {
      return this._crc;
   }
}
