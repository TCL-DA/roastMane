package com.yoctopuce.YoctoAPI;

class YPEntry {
   private final String _classname;
   private final String _serial;
   private final String _funcId;
   private final String _hwId;
   private String _logicalName = "";
   private String _advertisedValue = "";
   private int _index = -1;
   private final YPEntry.BaseClass _baseclass;

   YPEntry(YJSONObject json) throws Exception {
      String hardwareId = json.getString("hardwareId");
      int pos = hardwareId.indexOf(46);
      this._serial = hardwareId.substring(0, pos);
      this._funcId = hardwareId.substring(pos + 1);
      this._hwId = this._serial + "." + this._funcId;
      this._classname = YAPIContext.functionClass(this._funcId);
      this._logicalName = json.getString("logicalName");
      this._advertisedValue = json.getString("advertisedValue");

      try {
         this._index = json.getInt("index");
      } catch (Exception var5) {
         this._index = 0;
      }

      if (json.has("baseType")) {
         this._baseclass = YPEntry.BaseClass.values()[json.getInt("baseType")];
      } else {
         this._baseclass = YPEntry.BaseClass.Function;
      }
   }

   YPEntry(String serial, String functionID, YPEntry.BaseClass baseclass) {
      this._serial = serial;
      this._funcId = functionID;
      this._hwId = this._serial + "." + this._funcId;
      this._baseclass = baseclass;
      this._classname = YAPIContext.functionClass(this._funcId);
   }

   YPEntry(String classname, String serial, String funcId, String logicalName, String advertisedValue, int baseType, int funYdx) {
      this._serial = serial;
      this._funcId = funcId;
      this._hwId = this._serial + "." + this._funcId;
      this._logicalName = logicalName;
      this._advertisedValue = advertisedValue;
      this._baseclass = YPEntry.BaseClass.values()[baseType];
      this._index = funYdx;
      this._classname = classname;
   }

   @Override
   public String toString() {
      return "YPEntry{_classname='"
         + this._classname
         + '\''
         + ", _serial='"
         + this._serial
         + '\''
         + ", _funcId='"
         + this._funcId
         + '\''
         + ", _logicalName='"
         + this._logicalName
         + '\''
         + ", _advertisedValue='"
         + this._advertisedValue
         + '\''
         + ", _index="
         + this._index
         + ", _baseclass="
         + this._baseclass
         + '}';
   }

   String getAdvertisedValue() {
      return this._advertisedValue;
   }

   void setAdvertisedValue(String _advertisedValue) {
      this._advertisedValue = _advertisedValue;
   }

   String getHardwareId() {
      return this._hwId;
   }

   String getSerial() {
      return this._serial;
   }

   String getFuncId() {
      return this._funcId;
   }

   int getIndex() {
      return this._index;
   }

   void setIndex(int index) {
      this._index = index;
   }

   boolean matchBaseType(YPEntry.BaseClass baseclass) {
      return baseclass.equals(YPEntry.BaseClass.Function) || baseclass.equals(this._baseclass);
   }

   String getBaseType() {
      return this._baseclass.toString();
   }

   String getLogicalName() {
      return this._logicalName;
   }

   void setLogicalName(String _logicalName) {
      this._logicalName = _logicalName;
   }

   String getClassname() {
      return this._classname;
   }

   String getFriendlyName(YAPIContext ctx) throws YAPI_Exception {
      if (this._classname.equals("Module")) {
         return this._logicalName.equals("") ? this._serial + ".module" : this._logicalName + ".module";
      } else {
         YPEntry moduleYP = ctx._yHash.resolveFunction("Module", this._serial);
         String module = moduleYP.getFriendlyName(ctx);
         int pos = module.indexOf(".");
         module = module.substring(0, pos);
         return this._logicalName.equals("") ? module + "." + this._funcId : module + "." + this._logicalName;
      }
   }

   static enum BaseClass {
      Function(0),
      Sensor(1);

      private int _intval = 0;

      private BaseClass(int intval) {
         this._intval = intval;
      }

      @Override
      public String toString() {
         return this == Sensor ? "Sensor" : "Function";
      }

      public static YPEntry.BaseClass forByte(byte bval) {
         return values()[bval];
      }
   }
}
