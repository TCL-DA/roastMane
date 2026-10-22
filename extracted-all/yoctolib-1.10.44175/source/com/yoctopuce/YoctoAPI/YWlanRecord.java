package com.yoctopuce.YoctoAPI;

public class YWlanRecord {
   protected String _ssid;
   protected int _channel = 0;
   protected String _sec;
   protected int _rssi = 0;

   YWlanRecord(String json_str) throws YAPI_Exception {
      try {
         YJSONObject json = new YJSONObject(json_str);
         json.parse();
         this._ssid = json.getString("ssid");
         this._channel = json.getInt("channel");
         this._sec = json.getString("sec");
         this._rssi = json.getInt("rssi");
      } catch (Exception var3) {
         throw new YAPI_Exception(-8, "invalid json struct for YWlanRecord");
      }
   }

   public String get_ssid() {
      return this._ssid;
   }

   public int get_channel() {
      return this._channel;
   }

   public String get_security() {
      return this._sec;
   }

   public int get_linkQuality() {
      return this._rssi;
   }
}
