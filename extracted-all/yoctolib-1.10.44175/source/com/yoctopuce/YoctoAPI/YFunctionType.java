package com.yoctopuce.YoctoAPI;

import java.util.HashMap;
import java.util.Iterator;

class YFunctionType {
   private String _className;
   private final YAPIContext _yctx;
   private final HashMap<String, YPEntry> _ypEntries = new HashMap<>();
   private final HashMap<String, YFunction> _connectedFns = new HashMap<>();
   private final HashMap<String, YFunction> _requestedFns = new HashMap<>();
   private final HashMap<String, String> _hwIdByName = new HashMap<>();

   public YFunctionType(String classname, YAPIContext yctx) {
      this._className = classname;
      this._yctx = yctx;
   }

   public void reindexFunction(YPEntry yp) {
      String oldLogicalName = "";
      String hardwareId = yp.getHardwareId();
      if (this._ypEntries.containsKey(hardwareId)) {
         oldLogicalName = this._ypEntries.get(hardwareId).getLogicalName();
      }

      if (!oldLogicalName.equals("") && !oldLogicalName.equals(yp.getLogicalName()) && this._hwIdByName.get(oldLogicalName).equals(hardwareId)) {
         this._hwIdByName.remove(oldLogicalName);
      }

      if (!yp.getLogicalName().equals("")) {
         this._hwIdByName.put(yp.getLogicalName(), hardwareId);
      }

      this._ypEntries.put(yp.getHardwareId(), yp);
   }

   public void forgetFunction(String hwid) {
      if (this._ypEntries.containsKey(hwid)) {
         String currname = this._ypEntries.get(hwid).getLogicalName();
         if (!currname.equals("") && this._hwIdByName.get(currname).equals(hwid)) {
            this._hwIdByName.remove(currname);
         }

         this._ypEntries.remove(hwid);
      }
   }

   private YFunctionType.HWID resolve(String func) throws YAPI_Exception {
      int dotpos = func.indexOf(46);
      if (dotpos < 0) {
         if (this._hwIdByName.containsKey(func)) {
            String hwid = this._hwIdByName.get(func);
            return new YFunctionType.HWID(hwid);
         }

         func = func + String.format(".%c%s", Character.toLowerCase(this._className.charAt(0)), this._className.substring(1));
      }

      YFunctionType.HWID hwid = new YFunctionType.HWID(func);
      if (this._ypEntries.containsKey(hwid.toString())) {
         return hwid;
      } else {
         if (hwid.module.length() > 0) {
            YDevice dev = this._yctx._yHash.getDevice(hwid.module);
            if (dev == null) {
               throw new YAPI_Exception(-4, "Device [" + hwid.module + "] not online");
            }

            String serial = dev.getSerialNumber();
            hwid = new YFunctionType.HWID(serial, hwid.getFunction());
            if (this._ypEntries.containsKey(hwid.toString())) {
               return hwid;
            }

            for (YPEntry yp : dev.getFunctions()) {
               if (yp.getLogicalName().equals(hwid.getFunction()) && this._ypEntries.containsValue(yp)) {
                  return new YFunctionType.HWID(serial, yp.getFuncId());
               }
            }
         } else {
            for (String hwid_str : this._connectedFns.keySet()) {
               YFunctionType.HWID tmpid = new YFunctionType.HWID(hwid_str);
               if (tmpid.getFunction().equals(hwid.getFunction())) {
                  return tmpid;
               }
            }
         }

         throw new YAPI_Exception(-4, "No function [" + hwid.function + "] found on device [" + hwid.module + "]");
      }
   }

   public void setFunction(String func, YFunction yfunc) {
      try {
         YFunctionType.HWID hwid = this.resolve(func);
         this._connectedFns.put(hwid.toString(), yfunc);
      } catch (YAPI_Exception var5) {
         this._requestedFns.put(func, yfunc);
      }
   }

   public YFunction getFunction(String func) {
      try {
         YFunctionType.HWID hwid = this.resolve(func);
         if (this._connectedFns.containsKey(hwid.toString())) {
            return this._connectedFns.get(hwid.toString());
         }

         if (this._requestedFns.containsKey(func)) {
            YFunction req_fn = this._requestedFns.get(func);
            this._connectedFns.put(hwid.toString(), req_fn);
            this._requestedFns.remove(func);
            return req_fn;
         }
      } catch (YAPI_Exception var4) {
         if (this._requestedFns.containsKey(func)) {
            return this._requestedFns.get(func);
         }
      }

      return null;
   }

   public YPEntry getYPEntry(String func) throws YAPI_Exception {
      YFunctionType.HWID hwid = this.resolve(func);
      return this._ypEntries.get(hwid.toString());
   }

   public void setFunctionValue(String hwid, String pubval) {
      YPEntry yp = this._ypEntries.get(hwid);
      if (yp != null) {
         if (!yp.getAdvertisedValue().equals(pubval)) {
            yp.setAdvertisedValue(pubval);
         }
      }
   }

   public YPEntry getFirstYPEntry() {
      Iterator var1 = this._ypEntries.keySet().iterator();
      if (var1.hasNext()) {
         String key = (String)var1.next();
         return this._ypEntries.get(key);
      } else {
         return null;
      }
   }

   public YPEntry getNextYPEntry(String hwid) {
      boolean found = false;

      for (String iter_hwid : this._ypEntries.keySet()) {
         if (found) {
            return this._ypEntries.get(iter_hwid);
         }

         if (hwid.equals(iter_hwid)) {
            found = true;
         }
      }

      return null;
   }

   public static class HWID {
      private final String module;
      private final String function;

      public String getModule() {
         return this.module;
      }

      public String getFunction() {
         return this.function;
      }

      public HWID(String module, String function) {
         this.module = module;
         this.function = function;
      }

      public HWID(String full_hwid) {
         int pos = full_hwid.indexOf(46);
         this.module = full_hwid.substring(0, pos);
         this.function = full_hwid.substring(pos + 1);
      }

      @Override
      public String toString() {
         return this.module + "." + this.function;
      }
   }
}
