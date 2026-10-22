package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YColorLedCluster extends YFunction {
   public static final int ACTIVELEDCOUNT_INVALID = -1;
   public static final int LEDTYPE_RGB = 0;
   public static final int LEDTYPE_RGBW = 1;
   public static final int LEDTYPE_INVALID = -1;
   public static final int MAXLEDCOUNT_INVALID = -1;
   public static final int BLINKSEQMAXCOUNT_INVALID = -1;
   public static final int BLINKSEQMAXSIZE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _activeLedCount = -1;
   protected int _ledType = -1;
   protected int _maxLedCount = -1;
   protected int _blinkSeqMaxCount = -1;
   protected int _blinkSeqMaxSize = -1;
   protected String _command = "!INVALID!";
   protected YColorLedCluster.UpdateCallback _valueCallbackColorLedCluster = null;

   protected YColorLedCluster(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "ColorLedCluster";
   }

   protected YColorLedCluster(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("activeLedCount")) {
         this._activeLedCount = json_val.getInt("activeLedCount");
      }

      if (json_val.has("ledType")) {
         this._ledType = json_val.getInt("ledType");
      }

      if (json_val.has("maxLedCount")) {
         this._maxLedCount = json_val.getInt("maxLedCount");
      }

      if (json_val.has("blinkSeqMaxCount")) {
         this._blinkSeqMaxCount = json_val.getInt("blinkSeqMaxCount");
      }

      if (json_val.has("blinkSeqMaxSize")) {
         this._blinkSeqMaxSize = json_val.getInt("blinkSeqMaxSize");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_activeLedCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._activeLedCount;
      }
   }

   public int getActiveLedCount() throws YAPI_Exception {
      return this.get_activeLedCount();
   }

   public int set_activeLedCount(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("activeLedCount", rest_val);
         return 0;
      }
   }

   public int setActiveLedCount(int newval) throws YAPI_Exception {
      return this.set_activeLedCount(newval);
   }

   public int get_ledType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._ledType;
      }
   }

   public int getLedType() throws YAPI_Exception {
      return this.get_ledType();
   }

   public int set_ledType(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("ledType", rest_val);
         return 0;
      }
   }

   public int setLedType(int newval) throws YAPI_Exception {
      return this.set_ledType(newval);
   }

   public int get_maxLedCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._maxLedCount;
      }
   }

   public int getMaxLedCount() throws YAPI_Exception {
      return this.get_maxLedCount();
   }

   public int get_blinkSeqMaxCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinkSeqMaxCount;
      }
   }

   public int getBlinkSeqMaxCount() throws YAPI_Exception {
      return this.get_blinkSeqMaxCount();
   }

   public int get_blinkSeqMaxSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinkSeqMaxSize;
      }
   }

   public int getBlinkSeqMaxSize() throws YAPI_Exception {
      return this.get_blinkSeqMaxSize();
   }

   public String get_command() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._command;
      }
   }

   public int set_command(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("command", newval);
         return 0;
      }
   }

   public static YColorLedCluster FindColorLedCluster(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YColorLedCluster obj = (YColorLedCluster)YFunction._FindFromCache("ColorLedCluster", func);
         if (obj == null) {
            obj = new YColorLedCluster(func);
            YFunction._AddToCache("ColorLedCluster", func, obj);
         }

         return obj;
      }
   }

   public static YColorLedCluster FindColorLedClusterInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YColorLedCluster obj = (YColorLedCluster)YFunction._FindFromCacheInContext(yctx, "ColorLedCluster", func);
         if (obj == null) {
            obj = new YColorLedCluster(yctx, func);
            YFunction._AddToCache("ColorLedCluster", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YColorLedCluster.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackColorLedCluster = callback;
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
      if (this._valueCallbackColorLedCluster != null) {
         this._valueCallbackColorLedCluster.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String command) throws YAPI_Exception {
      return this.set_command(command);
   }

   public int set_rgbColor(int ledIndex, int count, int rgbValue) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "SR%d,%d,%x", ledIndex, count, rgbValue));
   }

   public int set_rgbColorAtPowerOn(int ledIndex, int count, int rgbValue) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "SC%d,%d,%x", ledIndex, count, rgbValue));
   }

   public int set_hslColorAtPowerOn(int ledIndex, int count, int hslValue) throws YAPI_Exception {
      int rgbValue = this.hsl2rgb(hslValue);
      return this.sendCommand(String.format(Locale.US, "SC%d,%d,%x", ledIndex, count, rgbValue));
   }

   public int set_hslColor(int ledIndex, int count, int hslValue) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "SH%d,%d,%x", ledIndex, count, hslValue));
   }

   public int rgb_move(int ledIndex, int count, int rgbValue, int delay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "MR%d,%d,%x,%d", ledIndex, count, rgbValue, delay));
   }

   public int hsl_move(int ledIndex, int count, int hslValue, int delay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "MH%d,%d,%x,%d", ledIndex, count, hslValue, delay));
   }

   public int addRgbMoveToBlinkSeq(int seqIndex, int rgbValue, int delay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AR%d,%x,%d", seqIndex, rgbValue, delay));
   }

   public int addHslMoveToBlinkSeq(int seqIndex, int hslValue, int delay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AH%d,%x,%d", seqIndex, hslValue, delay));
   }

   public int addMirrorToBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AC%d,0,0", seqIndex));
   }

   public int addJumpToBlinkSeq(int seqIndex, int linkSeqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AC%d,100,%d,1000", seqIndex, linkSeqIndex));
   }

   public int addUnlinkToBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AC%d,100,-1,1000", seqIndex));
   }

   public int linkLedToBlinkSeq(int ledIndex, int count, int seqIndex, int offset) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "LS%d,%d,%d,%d", ledIndex, count, seqIndex, offset));
   }

   public int linkLedToBlinkSeqAtPowerOn(int ledIndex, int count, int seqIndex, int offset) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "LO%d,%d,%d,%d", ledIndex, count, seqIndex, offset));
   }

   public int linkLedToPeriodicBlinkSeq(int ledIndex, int count, int seqIndex, int periods) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "LP%d,%d,%d,%d", ledIndex, count, seqIndex, periods));
   }

   public int unlinkLedFromBlinkSeq(int ledIndex, int count) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "US%d,%d", ledIndex, count));
   }

   public int startBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "SS%d", seqIndex));
   }

   public int stopBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "XS%d", seqIndex));
   }

   public int resetBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "ZS%d", seqIndex));
   }

   public int set_blinkSeqStateAtPowerOn(int seqIndex, int autostart) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "AS%d,%d", seqIndex, autostart));
   }

   public int set_blinkSeqSpeed(int seqIndex, int speed) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "CS%d,%d", seqIndex, speed));
   }

   public int saveLedsConfigAtPowerOn() throws YAPI_Exception {
      return this.sendCommand("WL");
   }

   public int saveLedsState() throws YAPI_Exception {
      return this.sendCommand("WL");
   }

   public int saveBlinkSeq(int seqIndex) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "WS%d", seqIndex));
   }

   public int set_rgbColorBuffer(int ledIndex, byte[] buff) throws YAPI_Exception {
      return this._upload(String.format(Locale.US, "rgb:0:%d", ledIndex), buff);
   }

   public int set_rgbColorArray(int ledIndex, ArrayList<Integer> rgbList) throws YAPI_Exception {
      int listlen = rgbList.size();
      byte[] buff = new byte[3 * listlen];

      for (int idx = 0; idx < listlen; idx++) {
         int rgb = rgbList.get(idx);
         buff[3 * idx] = (byte)(rgb >> 16 & 0xFF & 0xFF);
         buff[3 * idx + 1] = (byte)(rgb >> 8 & 0xFF & 0xFF);
         buff[3 * idx + 2] = (byte)(rgb & 0xFF & 0xFF);
      }

      return this._upload(String.format(Locale.US, "rgb:0:%d", ledIndex), buff);
   }

   public int rgbArrayOfs_move(int ledIndex, ArrayList<Integer> rgbList, int delay) throws YAPI_Exception {
      int listlen = rgbList.size();
      byte[] buff = new byte[3 * listlen];

      for (int idx = 0; idx < listlen; idx++) {
         int rgb = rgbList.get(idx);
         buff[3 * idx] = (byte)(rgb >> 16 & 0xFF & 0xFF);
         buff[3 * idx + 1] = (byte)(rgb >> 8 & 0xFF & 0xFF);
         buff[3 * idx + 2] = (byte)(rgb & 0xFF & 0xFF);
      }

      return this._upload(String.format(Locale.US, "rgb:%d:%d", delay, ledIndex), buff);
   }

   public int rgbArray_move(ArrayList<Integer> rgbList, int delay) throws YAPI_Exception {
      return this.rgbArrayOfs_move(0, rgbList, delay);
   }

   public int set_hslColorBuffer(int ledIndex, byte[] buff) throws YAPI_Exception {
      return this._upload(String.format(Locale.US, "hsl:0:%d", ledIndex), buff);
   }

   public int set_hslColorArray(int ledIndex, ArrayList<Integer> hslList) throws YAPI_Exception {
      int listlen = hslList.size();
      byte[] buff = new byte[3 * listlen];

      for (int idx = 0; idx < listlen; idx++) {
         int hsl = hslList.get(idx);
         buff[3 * idx] = (byte)(hsl >> 16 & 0xFF & 0xFF);
         buff[3 * idx + 1] = (byte)(hsl >> 8 & 0xFF & 0xFF);
         buff[3 * idx + 2] = (byte)(hsl & 0xFF & 0xFF);
      }

      return this._upload(String.format(Locale.US, "hsl:0:%d", ledIndex), buff);
   }

   public int hslArray_move(ArrayList<Integer> hslList, int delay) throws YAPI_Exception {
      return this.hslArrayOfs_move(0, hslList, delay);
   }

   public int hslArrayOfs_move(int ledIndex, ArrayList<Integer> hslList, int delay) throws YAPI_Exception {
      int listlen = hslList.size();
      byte[] buff = new byte[3 * listlen];

      for (int idx = 0; idx < listlen; idx++) {
         int hsl = hslList.get(idx);
         buff[3 * idx] = (byte)(hsl >> 16 & 0xFF & 0xFF);
         buff[3 * idx + 1] = (byte)(hsl >> 8 & 0xFF & 0xFF);
         buff[3 * idx + 2] = (byte)(hsl & 0xFF & 0xFF);
      }

      return this._upload(String.format(Locale.US, "hsl:%d:%d", delay, ledIndex), buff);
   }

   public byte[] get_rgbColorBuffer(int ledIndex, int count) throws YAPI_Exception {
      return this._download(String.format(Locale.US, "rgb.bin?typ=0&pos=%d&len=%d", 3 * ledIndex, 3 * count));
   }

   public ArrayList<Integer> get_rgbColorArray(int ledIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=0&pos=%d&len=%d", 3 * ledIndex, 3 * count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int r = buff[3 * idx] & 255;
         int g = buff[3 * idx + 1] & 255;
         int b = buff[3 * idx + 2] & 255;
         res.add(r * 65536 + g * 256 + b);
      }

      return res;
   }

   public ArrayList<Integer> get_rgbColorArrayAtPowerOn(int ledIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=4&pos=%d&len=%d", 3 * ledIndex, 3 * count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int r = buff[3 * idx] & 255;
         int g = buff[3 * idx + 1] & 255;
         int b = buff[3 * idx + 2] & 255;
         res.add(r * 65536 + g * 256 + b);
      }

      return res;
   }

   public ArrayList<Integer> get_linkedSeqArray(int ledIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=1&pos=%d&len=%d", ledIndex, count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int seq = buff[idx] & 255;
         res.add(seq);
      }

      return res;
   }

   public ArrayList<Integer> get_blinkSeqSignatures(int seqIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=2&pos=%d&len=%d", 4 * seqIndex, 4 * count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int hh = buff[4 * idx] & 255;
         int hl = buff[4 * idx + 1] & 255;
         int lh = buff[4 * idx + 2] & 255;
         int ll = buff[4 * idx + 3] & 255;
         res.add((hh << 24) + (hl << 16) + (lh << 8) + ll);
      }

      return res;
   }

   public ArrayList<Integer> get_blinkSeqStateSpeed(int seqIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=6&pos=%d&len=%d", seqIndex, count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int lh = buff[2 * idx] & 255;
         int ll = buff[2 * idx + 1] & 255;
         res.add((lh << 8) + ll);
      }

      return res;
   }

   public ArrayList<Integer> get_blinkSeqStateAtPowerOn(int seqIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=5&pos=%d&len=%d", seqIndex, count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int started = buff[idx] & 255;
         res.add(started);
      }

      return res;
   }

   public ArrayList<Integer> get_blinkSeqState(int seqIndex, int count) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      byte[] buff = this._download(String.format(Locale.US, "rgb.bin?typ=3&pos=%d&len=%d", seqIndex, count));
      res.clear();

      for (int idx = 0; idx < count; idx++) {
         int started = buff[idx] & 255;
         res.add(started);
      }

      return res;
   }

   public int hsl2rgbInt(int temp1, int temp2, int temp3) {
      if (temp3 >= 170) {
         return (temp1 + 127) / 255;
      } else {
         if (temp3 > 42) {
            if (temp3 <= 127) {
               return (temp2 + 127) / 255;
            }

            temp3 = 170 - temp3;
         }

         return (temp1 * 255 + (temp2 - temp1) * 6 * temp3 + 32512) / 65025;
      }
   }

   public int hsl2rgb(int hslValue) {
      int L = hslValue & 0xFF;
      int S = hslValue >> 8 & 0xFF;
      int H = hslValue >> 16 & 0xFF;
      if (S == 0) {
         return (L << 16) + (L << 8) + L;
      } else {
         int temp2;
         if (L <= 127) {
            temp2 = L * (255 + S);
         } else {
            temp2 = (L + S) * 255 - L * S;
         }

         int temp1 = 510 * L - temp2;
         int temp3 = H + 85;
         if (temp3 > 255) {
            temp3 -= 255;
         }

         int R = this.hsl2rgbInt(temp1, temp2, temp3);
         temp3 = H;
         if (H > 255) {
            temp3 = H - 255;
         }

         int G = this.hsl2rgbInt(temp1, temp2, temp3);
         if (H >= 85) {
            temp3 = H - 85;
         } else {
            temp3 = H + 170;
         }

         int B = this.hsl2rgbInt(temp1, temp2, temp3);
         if (R > 255) {
            R = 255;
         }

         if (G > 255) {
            G = 255;
         }

         if (B > 255) {
            B = 255;
         }

         return (R << 16) + (G << 8) + B;
      }
   }

   public YColorLedCluster nextColorLedCluster() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindColorLedClusterInContext(this._yapi, next_hwid);
   }

   public static YColorLedCluster FirstColorLedCluster() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("ColorLedCluster");
         return next_hwid == null ? null : FindColorLedClusterInContext(yctx, next_hwid);
      }
   }

   public static YColorLedCluster FirstColorLedClusterInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("ColorLedCluster");
      return next_hwid == null ? null : FindColorLedClusterInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YColorLedCluster var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YColorLedCluster var1, String var2);
   }
}
