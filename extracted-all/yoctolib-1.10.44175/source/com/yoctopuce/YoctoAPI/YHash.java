package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.HashMap;

class YHash {
   private final HashMap<String, YDevice> _devs = new HashMap<>();
   private final HashMap<String, String> _snByName = new HashMap<>();
   private final HashMap<String, YFunctionType> _fnByType = new HashMap<>(2);
   private final YAPIContext _yctx;

   public YHash(YAPIContext yctx) {
      this._yctx = yctx;
   }

   void reset() {
      this._fnByType.put("Module", new YFunctionType("Module", this._yctx));
   }

   synchronized void reindexDevice(YDevice dev) {
      String serial = dev.getSerialNumber();
      String lname = dev.getLogicalName();
      this._devs.put(serial, dev);
      if (!lname.equals("")) {
         this._snByName.put(lname, serial);
      }

      YFunctionType module = this._fnByType.get("Module");
      YPEntry moduleYPEntry = dev.getModuleYPEntry();
      module.reindexFunction(moduleYPEntry);

      for (YPEntry yp : dev.getFunctions()) {
         String classname = yp.getClassname();
         YFunctionType functionType = this._fnByType.get(classname);
         if (functionType == null) {
            functionType = new YFunctionType(classname, this._yctx);
            this._fnByType.put(classname, functionType);
         }

         functionType.reindexFunction(yp);
      }
   }

   synchronized YDevice getDevice(String device) {
      YDevice dev = null;
      if (this._devs.containsKey(device)) {
         dev = this._devs.get(device);
      } else if (this._snByName.containsKey(device)) {
         String serial = this._snByName.get(device);
         dev = this._devs.get(serial);
      }

      return dev;
   }

   synchronized void forgetDevice(String serial) {
      YDevice dev = this._devs.get(serial);
      if (dev != null) {
         String lname = dev.getLogicalName();
         this._devs.remove(serial);
         if (this._snByName.containsKey(lname) && this._snByName.get(lname).equals(serial)) {
            this._snByName.remove(lname);
         }

         YFunctionType module = this._fnByType.get("Module");
         module.forgetFunction(serial + ".module");

         for (YPEntry yp : dev.getFunctions()) {
            YFunctionType functionType = this._fnByType.get(yp.getClassname());
            if (functionType != null) {
               functionType.forgetFunction(yp.getHardwareId());
            }
         }
      }
   }

   private YFunctionType getFnByType(String className) {
      YFunctionType yFunctionType = this._fnByType.get(className);
      if (yFunctionType == null) {
         yFunctionType = new YFunctionType(className, this._yctx);
         this._fnByType.put(className, yFunctionType);
      }

      return yFunctionType;
   }

   synchronized YPEntry resolveFunction(String className, String func) throws YAPI_Exception {
      if (!YAPI._BaseType.containsKey(className)) {
         return this.getFnByType(className).getYPEntry(func);
      } else {
         YPEntry.BaseClass baseType = YAPI._BaseType.get(className);

         for (YFunctionType subClassType : this._fnByType.values()) {
            try {
               YPEntry yp = subClassType.getYPEntry(func);
               if (yp.matchBaseType(baseType)) {
                  return yp;
               }
            } catch (YAPI_Exception var7) {
            }
         }

         throw new YAPI_Exception(-4, "No function of type " + className + " found");
      }
   }

   synchronized String resolveHwID(String className, String func) throws YAPI_Exception {
      return this.resolveFunction(className, func).getHardwareId();
   }

   synchronized String resolveFuncId(String className, String func) throws YAPI_Exception {
      return this.resolveFunction(className, func).getFuncId();
   }

   synchronized String resolveSerial(String className, String func) throws YAPI_Exception {
      return this.resolveFunction(className, func).getSerial();
   }

   synchronized void setFunction(String className, String func, YFunction yfunc) {
      this.getFnByType(className).setFunction(func, yfunc);
   }

   synchronized YFunction getFunction(String className, String func) {
      return this.getFnByType(className).getFunction(func);
   }

   void setFunctionValue(String hwid, String pubval) {
      String classname = YAPIContext.functionClass(hwid);
      synchronized (this) {
         YFunctionType fnByType = this.getFnByType(classname);
         fnByType.setFunctionValue(hwid, pubval);
      }
   }

   synchronized String getFirstHardwareId(String className) {
      if (!YAPI._BaseType.containsKey(className)) {
         YFunctionType ft = this.getFnByType(className);
         YPEntry yp = ft.getFirstYPEntry();
         return yp == null ? null : yp.getHardwareId();
      } else {
         YPEntry.BaseClass baseType = YAPI._BaseType.get(className);

         for (YFunctionType subClassType : this._fnByType.values()) {
            YPEntry yp = subClassType.getFirstYPEntry();
            if (yp != null && yp.matchBaseType(baseType)) {
               return yp.getHardwareId();
            }
         }

         return null;
      }
   }

   synchronized String getNextHardwareId(String className, String hwid) {
      if (!YAPI._BaseType.containsKey(className)) {
         YFunctionType ft = this.getFnByType(className);
         YPEntry yp = ft.getNextYPEntry(hwid);
         return yp == null ? null : yp.getHardwareId();
      } else {
         YPEntry.BaseClass baseType = YAPI._BaseType.get(className);
         String prevclass = YAPIContext.functionClass(hwid);
         YPEntry res = this.getFnByType(prevclass).getNextYPEntry(hwid);
         if (res != null) {
            return res.getHardwareId();
         } else {
            for (String altClassName : this._fnByType.keySet()) {
               if (!prevclass.equals("")) {
                  if (altClassName.equals(prevclass)) {
                     prevclass = "";
                  }
               } else {
                  YFunctionType functionType = this._fnByType.get(altClassName);
                  res = functionType.getFirstYPEntry();
                  if (res != null && res.matchBaseType(baseType)) {
                     return res.getHardwareId();
                  }
               }
            }

            return null;
         }
      }
   }

   public synchronized void reindexYellowPages(HashMap<String, ArrayList<YPEntry>> yellowPages) {
      for (String classname : yellowPages.keySet()) {
         YFunctionType ftype = this.getFnByType(classname);

         for (YPEntry yprec : yellowPages.get(classname)) {
            ftype.reindexFunction(yprec);
         }
      }
   }

   public void clear() {
      this._devs.clear();
      this._snByName.clear();
      this._fnByType.clear();
   }
}
