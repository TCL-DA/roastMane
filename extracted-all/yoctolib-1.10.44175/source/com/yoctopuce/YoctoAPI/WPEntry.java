package com.yoctopuce.YoctoAPI;

class WPEntry {
   private String _logicalName = "";
   private final String _productName;
   private final int _productId;
   private final String _networkUrl;
   private int _beacon;
   private final String _serialNumber;

   public WPEntry(YJSONObject json) throws Exception {
      this._serialNumber = json.getString("serialNumber");
      this._logicalName = json.getString("logicalName");
      this._productName = json.getString("productName");
      this._productId = json.getInt("productId");
      String networkUrl = json.getString("networkUrl");
      this._networkUrl = networkUrl.substring(0, networkUrl.length() - 4);
      this._beacon = json.getInt("beacon");
   }

   @Override
   public String toString() {
      return "WPEntry [serialNumber="
         + this._serialNumber
         + ", logicalName="
         + this._logicalName
         + ", productName="
         + this._productName
         + ", productId="
         + this._productId
         + ", networkUrl="
         + this._networkUrl
         + ", beacon="
         + this._beacon
         + "]";
   }

   public WPEntry(String logicalName, String productName, int productId, String networkUrl, int beacon, String serialNumber) {
      this._logicalName = logicalName;
      this._productName = productName;
      this._productId = productId;
      this._networkUrl = networkUrl;
      this._beacon = beacon;
      this._serialNumber = serialNumber;
   }

   public int getBeacon() {
      return this._beacon;
   }

   public void setBeacon(int _beacon) {
      this._beacon = _beacon;
   }

   public void setLogicalName(String logicalName) {
      assert logicalName != null;

      this._logicalName = logicalName;
   }

   public String getLogicalName() {
      return this._logicalName;
   }

   public String getNetworkUrl() {
      return this._networkUrl;
   }

   public int getProductId() {
      return this._productId;
   }

   public String getProductName() {
      return this._productName;
   }

   public String getSerialNumber() {
      return this._serialNumber;
   }
}
