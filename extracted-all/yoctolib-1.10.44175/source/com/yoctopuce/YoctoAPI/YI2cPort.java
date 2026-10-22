package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YI2cPort extends YFunction {
   public static final int RXCOUNT_INVALID = -1;
   public static final int TXCOUNT_INVALID = -1;
   public static final int ERRCOUNT_INVALID = -1;
   public static final int RXMSGCOUNT_INVALID = -1;
   public static final int TXMSGCOUNT_INVALID = -1;
   public static final String LASTMSG_INVALID = "!INVALID!";
   public static final String CURRENTJOB_INVALID = "!INVALID!";
   public static final String STARTUPJOB_INVALID = "!INVALID!";
   public static final int JOBMAXTASK_INVALID = -1;
   public static final int JOBMAXSIZE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   public static final String PROTOCOL_INVALID = "!INVALID!";
   public static final int I2CVOLTAGELEVEL_OFF = 0;
   public static final int I2CVOLTAGELEVEL_3V3 = 1;
   public static final int I2CVOLTAGELEVEL_1V8 = 2;
   public static final int I2CVOLTAGELEVEL_INVALID = -1;
   public static final String I2CMODE_INVALID = "!INVALID!";
   protected int _rxCount = -1;
   protected int _txCount = -1;
   protected int _errCount = -1;
   protected int _rxMsgCount = -1;
   protected int _txMsgCount = -1;
   protected String _lastMsg = "!INVALID!";
   protected String _currentJob = "!INVALID!";
   protected String _startupJob = "!INVALID!";
   protected int _jobMaxTask = -1;
   protected int _jobMaxSize = -1;
   protected String _command = "!INVALID!";
   protected String _protocol = "!INVALID!";
   protected int _i2cVoltageLevel = -1;
   protected String _i2cMode = "!INVALID!";
   protected YI2cPort.UpdateCallback _valueCallbackI2cPort = null;
   protected int _rxptr = 0;
   protected byte[] _rxbuff;
   protected int _rxbuffptr = 0;

   protected YI2cPort(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "I2cPort";
   }

   protected YI2cPort(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("rxCount")) {
         this._rxCount = json_val.getInt("rxCount");
      }

      if (json_val.has("txCount")) {
         this._txCount = json_val.getInt("txCount");
      }

      if (json_val.has("errCount")) {
         this._errCount = json_val.getInt("errCount");
      }

      if (json_val.has("rxMsgCount")) {
         this._rxMsgCount = json_val.getInt("rxMsgCount");
      }

      if (json_val.has("txMsgCount")) {
         this._txMsgCount = json_val.getInt("txMsgCount");
      }

      if (json_val.has("lastMsg")) {
         this._lastMsg = json_val.getString("lastMsg");
      }

      if (json_val.has("currentJob")) {
         this._currentJob = json_val.getString("currentJob");
      }

      if (json_val.has("startupJob")) {
         this._startupJob = json_val.getString("startupJob");
      }

      if (json_val.has("jobMaxTask")) {
         this._jobMaxTask = json_val.getInt("jobMaxTask");
      }

      if (json_val.has("jobMaxSize")) {
         this._jobMaxSize = json_val.getInt("jobMaxSize");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      if (json_val.has("protocol")) {
         this._protocol = json_val.getString("protocol");
      }

      if (json_val.has("i2cVoltageLevel")) {
         this._i2cVoltageLevel = json_val.getInt("i2cVoltageLevel");
      }

      if (json_val.has("i2cMode")) {
         this._i2cMode = json_val.getString("i2cMode");
      }

      super._parseAttr(json_val);
   }

   public int get_rxCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rxCount;
      }
   }

   public int getRxCount() throws YAPI_Exception {
      return this.get_rxCount();
   }

   public int get_txCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._txCount;
      }
   }

   public int getTxCount() throws YAPI_Exception {
      return this.get_txCount();
   }

   public int get_errCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._errCount;
      }
   }

   public int getErrCount() throws YAPI_Exception {
      return this.get_errCount();
   }

   public int get_rxMsgCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rxMsgCount;
      }
   }

   public int getRxMsgCount() throws YAPI_Exception {
      return this.get_rxMsgCount();
   }

   public int get_txMsgCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._txMsgCount;
      }
   }

   public int getTxMsgCount() throws YAPI_Exception {
      return this.get_txMsgCount();
   }

   public String get_lastMsg() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._lastMsg;
      }
   }

   public String getLastMsg() throws YAPI_Exception {
      return this.get_lastMsg();
   }

   public String get_currentJob() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._currentJob;
      }
   }

   public String getCurrentJob() throws YAPI_Exception {
      return this.get_currentJob();
   }

   public int set_currentJob(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("currentJob", newval);
         return 0;
      }
   }

   public int setCurrentJob(String newval) throws YAPI_Exception {
      return this.set_currentJob(newval);
   }

   public String get_startupJob() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._startupJob;
      }
   }

   public String getStartupJob() throws YAPI_Exception {
      return this.get_startupJob();
   }

   public int set_startupJob(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("startupJob", newval);
         return 0;
      }
   }

   public int setStartupJob(String newval) throws YAPI_Exception {
      return this.set_startupJob(newval);
   }

   public int get_jobMaxTask() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._jobMaxTask;
      }
   }

   public int getJobMaxTask() throws YAPI_Exception {
      return this.get_jobMaxTask();
   }

   public int get_jobMaxSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._jobMaxSize;
      }
   }

   public int getJobMaxSize() throws YAPI_Exception {
      return this.get_jobMaxSize();
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

   public String get_protocol() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._protocol;
      }
   }

   public String getProtocol() throws YAPI_Exception {
      return this.get_protocol();
   }

   public int set_protocol(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("protocol", newval);
         return 0;
      }
   }

   public int setProtocol(String newval) throws YAPI_Exception {
      return this.set_protocol(newval);
   }

   public int get_i2cVoltageLevel() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._i2cVoltageLevel;
      }
   }

   public int getI2cVoltageLevel() throws YAPI_Exception {
      return this.get_i2cVoltageLevel();
   }

   public int set_i2cVoltageLevel(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("i2cVoltageLevel", rest_val);
         return 0;
      }
   }

   public int setI2cVoltageLevel(int newval) throws YAPI_Exception {
      return this.set_i2cVoltageLevel(newval);
   }

   public String get_i2cMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._i2cMode;
      }
   }

   public String getI2cMode() throws YAPI_Exception {
      return this.get_i2cMode();
   }

   public int set_i2cMode(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("i2cMode", newval);
         return 0;
      }
   }

   public int setI2cMode(String newval) throws YAPI_Exception {
      return this.set_i2cMode(newval);
   }

   public static YI2cPort FindI2cPort(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YI2cPort obj = (YI2cPort)YFunction._FindFromCache("I2cPort", func);
         if (obj == null) {
            obj = new YI2cPort(func);
            YFunction._AddToCache("I2cPort", func, obj);
         }

         return obj;
      }
   }

   public static YI2cPort FindI2cPortInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YI2cPort obj = (YI2cPort)YFunction._FindFromCacheInContext(yctx, "I2cPort", func);
         if (obj == null) {
            obj = new YI2cPort(yctx, func);
            YFunction._AddToCache("I2cPort", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YI2cPort.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackI2cPort = callback;
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
      if (this._valueCallbackI2cPort != null) {
         this._valueCallbackI2cPort.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String text) throws YAPI_Exception {
      return this.set_command(text);
   }

   public String readLine() throws YAPI_Exception {
      new ArrayList();
      String url = String.format(Locale.US, "rxmsg.json?pos=%d&len=1&maxw=1", this._rxptr);
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return "";
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));
         return msglen == 0 ? "" : this._json_get_string(((String)msgarr.get(0)).getBytes());
      }
   }

   public ArrayList<String> readMessages(String pattern, int maxWait) throws YAPI_Exception {
      new ArrayList();
      ArrayList<String> res = new ArrayList<>();
      String url = String.format(Locale.US, "rxmsg.json?pos=%d&maxw=%d&pat=%s", this._rxptr, maxWait, pattern);
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return res;
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));

         for (int idx = 0; idx < msglen; idx++) {
            res.add(this._json_get_string(((String)msgarr.get(idx)).getBytes()));
         }

         return res;
      }
   }

   public int read_seek(int absPos) {
      this._rxptr = absPos;
      return 0;
   }

   public int read_tell() {
      return this._rxptr;
   }

   public int read_avail() throws YAPI_Exception {
      byte[] buff = this._download(String.format(Locale.US, "rxcnt.bin?pos=%d", this._rxptr));
      int bufflen = buff.length - 1;

      while (bufflen > 0 && (buff[bufflen] & 255) != 64) {
         bufflen--;
      }

      return YAPIContext._atoi(new String(buff).substring(0, bufflen));
   }

   public String queryLine(String query, int maxWait) throws YAPI_Exception {
      new ArrayList();
      String url = String.format(Locale.US, "rxmsg.json?len=1&maxw=%d&cmd=!%s", maxWait, this._escapeAttr(query));
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return "";
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));
         return msglen == 0 ? "" : this._json_get_string(((String)msgarr.get(0)).getBytes());
      }
   }

   public String queryHex(String hexString, int maxWait) throws YAPI_Exception {
      new ArrayList();
      String url = String.format(Locale.US, "rxmsg.json?len=1&maxw=%d&cmd=$%s", maxWait, hexString);
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return "";
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));
         return msglen == 0 ? "" : this._json_get_string(((String)msgarr.get(0)).getBytes());
      }
   }

   public int uploadJob(String jobfile, String jsonDef) throws YAPI_Exception {
      this._upload(jobfile, jsonDef.getBytes());
      return 0;
   }

   public int selectJob(String jobfile) throws YAPI_Exception {
      return this.set_currentJob(jobfile);
   }

   public int reset() throws YAPI_Exception {
      this._rxptr = 0;
      this._rxbuffptr = 0;
      this._rxbuff = new byte[0];
      return this.sendCommand("Z");
   }

   public int i2cSendBin(int slaveAddr, byte[] buff) throws YAPI_Exception {
      String msg = String.format(Locale.US, "@%02x:", slaveAddr);
      int nBytes = buff.length;

      for (int idx = 0; idx < nBytes; idx++) {
         int val = buff[idx] & 255;
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      String reply = this.queryLine(msg, 1000);
      if (reply.length() <= 0) {
         throw new YAPI_Exception(-8, "No response from I2C device");
      } else {
         int var8 = reply.indexOf("[N]!");
         if (var8 >= 0) {
            throw new YAPI_Exception(-8, "No I2C ACK received");
         } else {
            var8 = reply.indexOf("!");
            if (var8 >= 0) {
               throw new YAPI_Exception(-8, "I2C protocol error");
            } else {
               return 0;
            }
         }
      }
   }

   public int i2cSendArray(int slaveAddr, ArrayList<Integer> values) throws YAPI_Exception {
      String msg = String.format(Locale.US, "@%02x:", slaveAddr);
      int nBytes = values.size();

      for (int idx = 0; idx < nBytes; idx++) {
         int val = values.get(idx);
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      String reply = this.queryLine(msg, 1000);
      if (reply.length() <= 0) {
         throw new YAPI_Exception(-8, "No response from I2C device");
      } else {
         int var8 = reply.indexOf("[N]!");
         if (var8 >= 0) {
            throw new YAPI_Exception(-8, "No I2C ACK received");
         } else {
            var8 = reply.indexOf("!");
            if (var8 >= 0) {
               throw new YAPI_Exception(-8, "I2C protocol error");
            } else {
               return 0;
            }
         }
      }
   }

   public byte[] i2cSendAndReceiveBin(int slaveAddr, byte[] buff, int rcvCount) throws YAPI_Exception {
      String msg = String.format(Locale.US, "@%02x:", slaveAddr);
      int nBytes = buff.length;

      for (int idx = 0; idx < nBytes; idx++) {
         int val = buff[idx] & 255;
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      for (int var10 = 0; var10 < rcvCount; var10++) {
         msg = String.format(Locale.US, "%sxx", msg);
      }

      String reply = this.queryLine(msg, 1000);
      byte[] rcvbytes = new byte[0];
      if (reply.length() <= 0) {
         throw new YAPI_Exception(-8, "No response from I2C device");
      } else {
         int var11 = reply.indexOf("[N]!");
         if (var11 >= 0) {
            throw new YAPI_Exception(-8, "No I2C ACK received");
         } else {
            var11 = reply.indexOf("!");
            if (var11 >= 0) {
               throw new YAPI_Exception(-8, "I2C protocol error");
            } else {
               reply = reply.substring(reply.length() - 2 * rcvCount, reply.length() - 2 * rcvCount + 2 * rcvCount);
               return YAPIContext._hexStrToBin(reply);
            }
         }
      }
   }

   public ArrayList<Integer> i2cSendAndReceiveArray(int slaveAddr, ArrayList<Integer> values, int rcvCount) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      String msg = String.format(Locale.US, "@%02x:", slaveAddr);
      int nBytes = values.size();

      for (int idx = 0; idx < nBytes; idx++) {
         int val = values.get(idx);
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      for (int var11 = 0; var11 < rcvCount; var11++) {
         msg = String.format(Locale.US, "%sxx", msg);
      }

      String reply = this.queryLine(msg, 1000);
      if (reply.length() <= 0) {
         throw new YAPI_Exception(-8, "No response from I2C device");
      } else {
         int var12 = reply.indexOf("[N]!");
         if (var12 >= 0) {
            throw new YAPI_Exception(-8, "No I2C ACK received");
         } else {
            var12 = reply.indexOf("!");
            if (var12 >= 0) {
               throw new YAPI_Exception(-8, "I2C protocol error");
            } else {
               reply = reply.substring(reply.length() - 2 * rcvCount, reply.length() - 2 * rcvCount + 2 * rcvCount);
               byte[] rcvbytes = YAPIContext._hexStrToBin(reply);
               res.clear();

               for (int var14 = 0; var14 < rcvCount; var14++) {
                  int val = rcvbytes[var14] & 255;
                  res.add(val);
               }

               return res;
            }
         }
      }
   }

   public int writeStr(String codes) throws YAPI_Exception {
      byte[] buff = codes.getBytes();
      int bufflen = buff.length;
      if (bufflen < 100) {
         int ch = 32;
         int idx = 0;

         while (idx < bufflen && ch != 0) {
            ch = buff[idx] & 255;
            if (ch >= 32 && ch < 127) {
               idx++;
            } else {
               ch = 0;
            }
         }

         if (idx >= bufflen) {
            return this.sendCommand(String.format(Locale.US, "+%s", codes));
         }
      }

      return this._upload("txdata", buff);
   }

   public int writeLine(String codes) throws YAPI_Exception {
      int bufflen = codes.length();
      if (bufflen < 100) {
         return this.sendCommand(String.format(Locale.US, "!%s", codes));
      } else {
         byte[] buff = String.format(Locale.US, "%s\n", codes).getBytes();
         return this._upload("txdata", buff);
      }
   }

   public int writeByte(int code) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "+%02X", code));
   }

   public int writeHex(String hexString) throws YAPI_Exception {
      int bufflen = hexString.length();
      if (bufflen < 100) {
         return this.sendCommand(String.format(Locale.US, "+%s", hexString));
      } else {
         byte[] buff = hexString.getBytes();
         return this._upload("txdata", buff);
      }
   }

   public int writeBin(byte[] buff) throws YAPI_Exception {
      String msg = "";
      int nBytes = buff.length;

      for (int idx = 0; idx < nBytes; idx++) {
         int val = buff[idx] & 255;
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      return this.writeHex(msg);
   }

   public int writeArray(ArrayList<Integer> byteList) throws YAPI_Exception {
      String msg = "";
      int nBytes = byteList.size();

      for (int idx = 0; idx < nBytes; idx++) {
         int val = byteList.get(idx);
         msg = String.format(Locale.US, "%s%02x", msg, val);
      }

      return this.writeHex(msg);
   }

   public ArrayList<YI2cSnoopingRecord> snoopMessages(int maxWait) throws YAPI_Exception {
      new ArrayList();
      ArrayList<YI2cSnoopingRecord> res = new ArrayList<>();
      String url = String.format(Locale.US, "rxmsg.json?pos=%d&maxw=%d&t=0", this._rxptr, maxWait);
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return res;
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));

         for (int idx = 0; idx < msglen; idx++) {
            res.add(new YI2cSnoopingRecord((String)msgarr.get(idx)));
         }

         return res;
      }
   }

   public YI2cPort nextI2cPort() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindI2cPortInContext(this._yapi, next_hwid);
   }

   public static YI2cPort FirstI2cPort() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("I2cPort");
         return next_hwid == null ? null : FindI2cPortInContext(yctx, next_hwid);
      }
   }

   public static YI2cPort FirstI2cPortInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("I2cPort");
      return next_hwid == null ? null : FindI2cPortInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YI2cPort var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YI2cPort var1, String var2);
   }
}
