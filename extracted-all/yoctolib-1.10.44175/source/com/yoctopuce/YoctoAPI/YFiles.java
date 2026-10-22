package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YFiles extends YFunction {
   public static final int FILESCOUNT_INVALID = -1;
   public static final int FREESPACE_INVALID = -1;
   protected int _filesCount = -1;
   protected int _freeSpace = -1;
   protected YFiles.UpdateCallback _valueCallbackFiles = null;

   protected YFiles(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Files";
   }

   protected YFiles(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("filesCount")) {
         this._filesCount = json_val.getInt("filesCount");
      }

      if (json_val.has("freeSpace")) {
         this._freeSpace = json_val.getInt("freeSpace");
      }

      super._parseAttr(json_val);
   }

   public int get_filesCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._filesCount;
      }
   }

   public int getFilesCount() throws YAPI_Exception {
      return this.get_filesCount();
   }

   public int get_freeSpace() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._freeSpace;
      }
   }

   public int getFreeSpace() throws YAPI_Exception {
      return this.get_freeSpace();
   }

   public static YFiles FindFiles(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YFiles obj = (YFiles)YFunction._FindFromCache("Files", func);
         if (obj == null) {
            obj = new YFiles(func);
            YFunction._AddToCache("Files", func, obj);
         }

         return obj;
      }
   }

   public static YFiles FindFilesInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YFiles obj = (YFiles)YFunction._FindFromCacheInContext(yctx, "Files", func);
         if (obj == null) {
            obj = new YFiles(yctx, func);
            YFunction._AddToCache("Files", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YFiles.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackFiles = callback;
      if (callback != null && this.isOnline()) {
         String val = this._advertisedValue;
         if (!val.equals("")) {
            this._invokeValueCallback(val);
         }
      }

      return 0;
   }

   @Override
   public int _invokeValueCallback(String value) {
      if (this._valueCallbackFiles != null) {
         this._valueCallbackFiles.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public byte[] sendCommand(String command) throws YAPI_Exception {
      String url = String.format(Locale.US, "files.json?a=%s", command);
      return this._download(url);
   }

   public int format_fs() throws YAPI_Exception {
      byte[] json = this.sendCommand("format");
      String res = this._json_get_key(json, "res");
      if (!res.equals("ok")) {
         throw new YAPI_Exception(-8, "format failed");
      } else {
         return 0;
      }
   }

   public ArrayList<YFileRecord> get_list(String pattern) throws YAPI_Exception {
      new ArrayList();
      ArrayList<YFileRecord> res = new ArrayList<>();
      byte[] json = this.sendCommand(String.format(Locale.US, "dir&f=%s", pattern));
      ArrayList<String> filelist = this._json_get_array(json);
      res.clear();

      for (String ii : filelist) {
         res.add(new YFileRecord(ii));
      }

      return res;
   }

   public boolean fileExist(String filename) throws YAPI_Exception {
      new ArrayList();
      if (filename.length() == 0) {
         return false;
      } else {
         byte[] json = this.sendCommand(String.format(Locale.US, "dir&f=%s", filename));
         ArrayList<String> filelist = this._json_get_array(json);
         return filelist.size() > 0;
      }
   }

   public byte[] download(String pathname) throws YAPI_Exception {
      return this._download(pathname);
   }

   public int upload(String pathname, byte[] content) throws YAPI_Exception {
      return this._upload(pathname, content);
   }

   public int remove(String pathname) throws YAPI_Exception {
      byte[] json = this.sendCommand(String.format(Locale.US, "del&f=%s", pathname));
      String res = this._json_get_key(json, "res");
      if (!res.equals("ok")) {
         throw new YAPI_Exception(-8, "unable to remove file");
      } else {
         return 0;
      }
   }

   public YFiles nextFiles() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindFilesInContext(this._yapi, next_hwid);
   }

   public static YFiles FirstFiles() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Files");
         return next_hwid == null ? null : FindFilesInContext(yctx, next_hwid);
      }
   }

   public static YFiles FirstFilesInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Files");
      return next_hwid == null ? null : FindFilesInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YFiles var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YFiles var1, String var2);
   }
}
