package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YSpiPort extends YFunction {
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
   public static final int VOLTAGELEVEL_OFF = 0;
   public static final int VOLTAGELEVEL_TTL3V = 1;
   public static final int VOLTAGELEVEL_TTL3VR = 2;
   public static final int VOLTAGELEVEL_TTL5V = 3;
   public static final int VOLTAGELEVEL_TTL5VR = 4;
   public static final int VOLTAGELEVEL_RS232 = 5;
   public static final int VOLTAGELEVEL_RS485 = 6;
   public static final int VOLTAGELEVEL_TTL1V8 = 7;
   public static final int VOLTAGELEVEL_INVALID = -1;
   public static final String SPIMODE_INVALID = "!INVALID!";
   public static final int SSPOLARITY_ACTIVE_LOW = 0;
   public static final int SSPOLARITY_ACTIVE_HIGH = 1;
   public static final int SSPOLARITY_INVALID = -1;
   public static final int SHIFTSAMPLING_OFF = 0;
   public static final int SHIFTSAMPLING_ON = 1;
   public static final int SHIFTSAMPLING_INVALID = -1;
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
   protected int _voltageLevel = -1;
   protected String _spiMode = "!INVALID!";
   protected int _ssPolarity = -1;
   protected int _shiftSampling = -1;
   protected YSpiPort.UpdateCallback _valueCallbackSpiPort = null;
   protected int _rxptr = 0;
   protected byte[] _rxbuff;
   protected int _rxbuffptr = 0;

   protected YSpiPort(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "SpiPort";
   }

   protected YSpiPort(String func) {
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

      if (json_val.has("voltageLevel")) {
         this._voltageLevel = json_val.getInt("voltageLevel");
      }

      if (json_val.has("spiMode")) {
         this._spiMode = json_val.getString("spiMode");
      }

      if (json_val.has("ssPolarity")) {
         this._ssPolarity = json_val.getInt("ssPolarity") > 0 ? 1 : 0;
      }

      if (json_val.has("shiftSampling")) {
         this._shiftSampling = json_val.getInt("shiftSampling") > 0 ? 1 : 0;
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

   public int get_voltageLevel() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._voltageLevel;
      }
   }

   public int getVoltageLevel() throws YAPI_Exception {
      return this.get_voltageLevel();
   }

   public int set_voltageLevel(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("voltageLevel", rest_val);
         return 0;
      }
   }

   public int setVoltageLevel(int newval) throws YAPI_Exception {
      return this.set_voltageLevel(newval);
   }

   public String get_spiMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._spiMode;
      }
   }

   public String getSpiMode() throws YAPI_Exception {
      return this.get_spiMode();
   }

   public int set_spiMode(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("spiMode", newval);
         return 0;
      }
   }

   public int setSpiMode(String newval) throws YAPI_Exception {
      return this.set_spiMode(newval);
   }

   public int get_ssPolarity() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._ssPolarity;
      }
   }

   public int getSsPolarity() throws YAPI_Exception {
      return this.get_ssPolarity();
   }

   public int set_ssPolarity(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("ssPolarity", rest_val);
         return 0;
      }
   }

   public int setSsPolarity(int newval) throws YAPI_Exception {
      return this.set_ssPolarity(newval);
   }

   public int get_shiftSampling() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._shiftSampling;
      }
   }

   public int getShiftSampling() throws YAPI_Exception {
      return this.get_shiftSampling();
   }

   public int set_shiftSampling(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("shiftSampling", rest_val);
         return 0;
      }
   }

   public int setShiftSampling(int newval) throws YAPI_Exception {
      return this.set_shiftSampling(newval);
   }

   public static YSpiPort FindSpiPort(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YSpiPort obj = (YSpiPort)YFunction._FindFromCache("SpiPort", func);
         if (obj == null) {
            obj = new YSpiPort(func);
            YFunction._AddToCache("SpiPort", func, obj);
         }

         return obj;
      }
   }

   public static YSpiPort FindSpiPortInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YSpiPort obj = (YSpiPort)YFunction._FindFromCacheInContext(yctx, "SpiPort", func);
         if (obj == null) {
            obj = new YSpiPort(yctx, func);
            YFunction._AddToCache("SpiPort", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YSpiPort.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackSpiPort = callback;
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
      if (this._valueCallbackSpiPort != null) {
         this._valueCallbackSpiPort.yNewValue(this, value);
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

   public int writeByte(int code) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "$%02X", code));
   }

   public int writeStr(String text) throws YAPI_Exception {
      byte[] buff = text.getBytes();
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
            return this.sendCommand(String.format(Locale.US, "+%s", text));
         }
      }

      return this._upload("txdata", buff);
   }

   public int writeBin(byte[] buff) throws YAPI_Exception {
      return this._upload("txdata", buff);
   }

   public int writeArray(ArrayList<Integer> byteList) throws YAPI_Exception {
      int bufflen = byteList.size();
      byte[] buff = new byte[bufflen];

      for (int idx = 0; idx < bufflen; idx++) {
         int hexb = byteList.get(idx);
         buff[idx] = (byte)(hexb & 0xFF);
      }

      return this._upload("txdata", buff);
   }

   public int writeHex(String hexString) throws YAPI_Exception {
      int bufflen = hexString.length();
      if (bufflen < 100) {
         return this.sendCommand(String.format(Locale.US, "$%s", hexString));
      } else {
         bufflen >>= 1;
         byte[] buff = new byte[bufflen];

         for (int idx = 0; idx < bufflen; idx++) {
            int hexb = Integer.valueOf(hexString.substring(2 * idx, 2 * idx + 2), 16);
            buff[idx] = (byte)(hexb & 0xFF);
         }

         return this._upload("txdata", buff);
      }
   }

   public int writeLine(String text) throws YAPI_Exception {
      byte[] buff = String.format(Locale.US, "%s\r\n", text).getBytes();
      int bufflen = buff.length - 2;
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
            return this.sendCommand(String.format(Locale.US, "!%s", text));
         }
      }

      return this._upload("txdata", buff);
   }

   public int readByte() throws YAPI_Exception {
      int bufflen = this._rxbuff.length;
      if (this._rxptr >= this._rxbuffptr && this._rxptr < this._rxbuffptr + bufflen) {
         int res = this._rxbuff[this._rxptr - this._rxbuffptr] & 255;
         this._rxptr++;
         return res;
      } else {
         int currpos = this._rxptr;
         int reqlen = 1024;
         byte[] buff = this.readBin(reqlen);
         bufflen = buff.length;
         if (this._rxptr == currpos + bufflen) {
            int res = buff[0] & 255;
            this._rxptr = currpos + 1;
            this._rxbuffptr = currpos;
            this._rxbuff = buff;
            return res;
         } else {
            this._rxptr = currpos;
            int var8 = 16;
            buff = this.readBin(var8);
            bufflen = buff.length;
            if (this._rxptr == currpos + bufflen) {
               int res = buff[0] & 255;
               this._rxptr = currpos + 1;
               this._rxbuffptr = currpos;
               this._rxbuff = buff;
               return res;
            } else {
               this._rxptr = currpos;
               buff = this._download(String.format(Locale.US, "rxdata.bin?pos=%d&len=1", this._rxptr));
               bufflen = buff.length - 1;
               int endpos = 0;

               for (int mult = 1; bufflen > 0 && (buff[bufflen] & 255) != 64; bufflen--) {
                  endpos += mult * ((buff[bufflen] & 255) - 48);
                  mult *= 10;
               }

               this._rxptr = endpos;
               return bufflen == 0 ? -9 : buff[0] & 0xFF;
            }
         }
      }
   }

   public String readStr(int nChars) throws YAPI_Exception {
      if (nChars > 65535) {
         nChars = 65535;
      }

      byte[] buff = this._download(String.format(Locale.US, "rxdata.bin?pos=%d&len=%d", this._rxptr, nChars));
      int bufflen = buff.length - 1;
      int endpos = 0;

      for (int mult = 1; bufflen > 0 && (buff[bufflen] & 255) != 64; bufflen--) {
         endpos += mult * ((buff[bufflen] & 255) - 48);
         mult *= 10;
      }

      this._rxptr = endpos;
      return new String(buff).substring(0, bufflen);
   }

   public byte[] readBin(int nChars) throws YAPI_Exception {
      if (nChars > 65535) {
         nChars = 65535;
      }

      byte[] buff = this._download(String.format(Locale.US, "rxdata.bin?pos=%d&len=%d", this._rxptr, nChars));
      int bufflen = buff.length - 1;
      int endpos = 0;

      for (int mult = 1; bufflen > 0 && (buff[bufflen] & 255) != 64; bufflen--) {
         endpos += mult * ((buff[bufflen] & 255) - 48);
         mult *= 10;
      }

      this._rxptr = endpos;
      byte[] res = new byte[bufflen];

      for (int idx = 0; idx < bufflen; idx++) {
         res[idx] = (byte)(buff[idx] & 255 & 0xFF);
      }

      return res;
   }

   public ArrayList<Integer> readArray(int nChars) throws YAPI_Exception {
      ArrayList<Integer> res = new ArrayList<>();
      if (nChars > 65535) {
         nChars = 65535;
      }

      byte[] buff = this._download(String.format(Locale.US, "rxdata.bin?pos=%d&len=%d", this._rxptr, nChars));
      int bufflen = buff.length - 1;
      int endpos = 0;

      for (int mult = 1; bufflen > 0 && (buff[bufflen] & 255) != 64; bufflen--) {
         endpos += mult * ((buff[bufflen] & 255) - 48);
         mult *= 10;
      }

      this._rxptr = endpos;
      res.clear();

      for (int idx = 0; idx < bufflen; idx++) {
         int b = buff[idx] & 255;
         res.add(b);
      }

      return res;
   }

   public String readHex(int nBytes) throws YAPI_Exception {
      if (nBytes > 65535) {
         nBytes = 65535;
      }

      byte[] buff = this._download(String.format(Locale.US, "rxdata.bin?pos=%d&len=%d", this._rxptr, nBytes));
      int bufflen = buff.length - 1;
      int endpos = 0;

      for (int mult = 1; bufflen > 0 && (buff[bufflen] & 255) != 64; bufflen--) {
         endpos += mult * ((buff[bufflen] & 255) - 48);
         mult *= 10;
      }

      this._rxptr = endpos;
      String res = "";

      int ofs;
      for (ofs = 0; ofs + 3 < bufflen; ofs += 4) {
         res = String.format(Locale.US, "%s%02X%02X%02X%02X", res, buff[ofs] & 255, buff[ofs + 1] & 255, buff[ofs + 2] & 255, buff[ofs + 3] & 255);
      }

      while (ofs < bufflen) {
         res = String.format(Locale.US, "%s%02X", res, buff[ofs] & 255);
         ofs++;
      }

      return res;
   }

   public int set_SS(int val) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "S%d", val));
   }

   public ArrayList<YSpiSnoopingRecord> snoopMessages(int maxWait) throws YAPI_Exception {
      new ArrayList();
      ArrayList<YSpiSnoopingRecord> res = new ArrayList<>();
      String url = String.format(Locale.US, "rxmsg.json?pos=%d&maxw=%d&t=0", this._rxptr, maxWait);
      byte[] msgbin = this._download(url);
      ArrayList<String> msgarr = this._json_get_array(msgbin);
      int msglen = msgarr.size();
      if (msglen == 0) {
         return res;
      } else {
         this._rxptr = YAPIContext._atoi((String)msgarr.get(--msglen));

         for (int idx = 0; idx < msglen; idx++) {
            res.add(new YSpiSnoopingRecord((String)msgarr.get(idx)));
         }

         return res;
      }
   }

   public YSpiPort nextSpiPort() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindSpiPortInContext(this._yapi, next_hwid);
   }

   public static YSpiPort FirstSpiPort() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("SpiPort");
         return next_hwid == null ? null : FindSpiPortInContext(yctx, next_hwid);
      }
   }

   public static YSpiPort FirstSpiPortInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("SpiPort");
      return next_hwid == null ? null : FindSpiPortInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YSpiPort var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YSpiPort var1, String var2);
   }
}
