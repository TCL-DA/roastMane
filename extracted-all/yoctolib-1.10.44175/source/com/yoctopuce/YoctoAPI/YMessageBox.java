package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YMessageBox extends YFunction {
   public static final int SLOTSINUSE_INVALID = -1;
   public static final int SLOTSCOUNT_INVALID = -1;
   public static final String SLOTSBITMAP_INVALID = "!INVALID!";
   public static final int PDUSENT_INVALID = -1;
   public static final int PDURECEIVED_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _slotsInUse = -1;
   protected int _slotsCount = -1;
   protected String _slotsBitmap = "!INVALID!";
   protected int _pduSent = -1;
   protected int _pduReceived = -1;
   protected String _command = "!INVALID!";
   protected YMessageBox.UpdateCallback _valueCallbackMessageBox = null;
   protected int _nextMsgRef = 0;
   protected String _prevBitmapStr;
   protected ArrayList<YSms> _pdus = new ArrayList<>();
   protected ArrayList<YSms> _messages = new ArrayList<>();
   protected boolean _gsm2unicodeReady;
   protected ArrayList<Integer> _gsm2unicode = new ArrayList<>();
   protected byte[] _iso2gsm;

   protected YMessageBox(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "MessageBox";
   }

   protected YMessageBox(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("slotsInUse")) {
         this._slotsInUse = json_val.getInt("slotsInUse");
      }

      if (json_val.has("slotsCount")) {
         this._slotsCount = json_val.getInt("slotsCount");
      }

      if (json_val.has("slotsBitmap")) {
         this._slotsBitmap = json_val.getString("slotsBitmap");
      }

      if (json_val.has("pduSent")) {
         this._pduSent = json_val.getInt("pduSent");
      }

      if (json_val.has("pduReceived")) {
         this._pduReceived = json_val.getInt("pduReceived");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_slotsInUse() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._slotsInUse;
      }
   }

   public int getSlotsInUse() throws YAPI_Exception {
      return this.get_slotsInUse();
   }

   public int get_slotsCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._slotsCount;
      }
   }

   public int getSlotsCount() throws YAPI_Exception {
      return this.get_slotsCount();
   }

   public String get_slotsBitmap() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._slotsBitmap;
      }
   }

   public int get_pduSent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._pduSent;
      }
   }

   public int getPduSent() throws YAPI_Exception {
      return this.get_pduSent();
   }

   public int set_pduSent(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("pduSent", rest_val);
         return 0;
      }
   }

   public int setPduSent(int newval) throws YAPI_Exception {
      return this.set_pduSent(newval);
   }

   public int get_pduReceived() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._pduReceived;
      }
   }

   public int getPduReceived() throws YAPI_Exception {
      return this.get_pduReceived();
   }

   public int set_pduReceived(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("pduReceived", rest_val);
         return 0;
      }
   }

   public int setPduReceived(int newval) throws YAPI_Exception {
      return this.set_pduReceived(newval);
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

   public static YMessageBox FindMessageBox(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMessageBox obj = (YMessageBox)YFunction._FindFromCache("MessageBox", func);
         if (obj == null) {
            obj = new YMessageBox(func);
            YFunction._AddToCache("MessageBox", func, obj);
         }

         return obj;
      }
   }

   public static YMessageBox FindMessageBoxInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMessageBox obj = (YMessageBox)YFunction._FindFromCacheInContext(yctx, "MessageBox", func);
         if (obj == null) {
            obj = new YMessageBox(yctx, func);
            YFunction._AddToCache("MessageBox", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMessageBox.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMessageBox = callback;
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
      if (this._valueCallbackMessageBox != null) {
         this._valueCallbackMessageBox.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int nextMsgRef() {
      this._nextMsgRef++;
      return this._nextMsgRef;
   }

   public int clearSIMSlot(int slot) throws YAPI_Exception {
      this._prevBitmapStr = "";
      return this.set_command(String.format(Locale.US, "DS%d", slot));
   }

   public YSms fetchPdu(int slot) throws YAPI_Exception {
      new ArrayList();
      byte[] binPdu = this._download(String.format(Locale.US, "sms.json?pos=%d&len=1", slot));
      ArrayList<String> arrPdu = this._json_get_array(binPdu);
      String hexPdu = this._decode_json_string((String)arrPdu.get(0));
      YSms sms = new YSms(this);
      sms.set_slot(slot);
      sms.parsePdu(YAPIContext._hexStrToBin(hexPdu));
      return sms;
   }

   public int initGsm2Unicode() {
      this._gsm2unicode.clear();
      this._gsm2unicode.add(64);
      this._gsm2unicode.add(163);
      this._gsm2unicode.add(36);
      this._gsm2unicode.add(165);
      this._gsm2unicode.add(232);
      this._gsm2unicode.add(233);
      this._gsm2unicode.add(249);
      this._gsm2unicode.add(236);
      this._gsm2unicode.add(242);
      this._gsm2unicode.add(199);
      this._gsm2unicode.add(10);
      this._gsm2unicode.add(216);
      this._gsm2unicode.add(248);
      this._gsm2unicode.add(13);
      this._gsm2unicode.add(197);
      this._gsm2unicode.add(229);
      this._gsm2unicode.add(916);
      this._gsm2unicode.add(95);
      this._gsm2unicode.add(934);
      this._gsm2unicode.add(915);
      this._gsm2unicode.add(923);
      this._gsm2unicode.add(937);
      this._gsm2unicode.add(928);
      this._gsm2unicode.add(936);
      this._gsm2unicode.add(931);
      this._gsm2unicode.add(920);
      this._gsm2unicode.add(926);
      this._gsm2unicode.add(27);
      this._gsm2unicode.add(198);
      this._gsm2unicode.add(230);
      this._gsm2unicode.add(223);
      this._gsm2unicode.add(201);

      for (int i = 32; i <= 122; i++) {
         this._gsm2unicode.add(i);
      }

      this._gsm2unicode.set(36, 164);
      this._gsm2unicode.set(64, 161);
      this._gsm2unicode.set(91, 196);
      this._gsm2unicode.set(92, 214);
      this._gsm2unicode.set(93, 209);
      this._gsm2unicode.set(94, 220);
      this._gsm2unicode.set(95, 167);
      this._gsm2unicode.set(96, 191);
      this._gsm2unicode.add(228);
      this._gsm2unicode.add(246);
      this._gsm2unicode.add(241);
      this._gsm2unicode.add(252);
      this._gsm2unicode.add(224);
      this._iso2gsm = new byte[256];

      for (int var3 = 0; var3 <= 127; var3++) {
         int uni = this._gsm2unicode.get(var3);
         if (uni <= 255) {
            this._iso2gsm[uni] = (byte)(var3 & 0xFF);
         }
      }

      for (int var4 = 0; var4 < 4; var4++) {
         this._iso2gsm[91 + var4] = 27;
         this._iso2gsm[123 + var4] = 27;
      }

      this._gsm2unicodeReady = true;
      return 0;
   }

   public ArrayList<Integer> gsm2unicode(byte[] gsm) {
      ArrayList<Integer> res = new ArrayList<>();
      if (!this._gsm2unicodeReady) {
         this.initGsm2Unicode();
      }

      int gsmlen = gsm.length;
      int reslen = gsmlen;

      for (int i = 0; i < gsmlen; i++) {
         if ((gsm[i] & 255) == 27) {
            reslen--;
         }
      }

      res.clear();

      for (int var7 = 0; var7 < gsmlen; var7++) {
         int uni = this._gsm2unicode.get(gsm[var7] & 255);
         if (uni == 27 && var7 + 1 < gsmlen) {
            uni = gsm[++var7] & 255;
            if (uni < 60) {
               if (uni < 41) {
                  if (uni == 20) {
                     uni = 94;
                  } else if (uni == 40) {
                     uni = 123;
                  } else {
                     uni = 0;
                  }
               } else if (uni == 41) {
                  uni = 125;
               } else if (uni == 47) {
                  uni = 92;
               } else {
                  uni = 0;
               }
            } else if (uni < 62) {
               if (uni == 60) {
                  uni = 91;
               } else if (uni == 61) {
                  uni = 126;
               } else {
                  uni = 0;
               }
            } else if (uni == 62) {
               uni = 93;
            } else if (uni == 64) {
               uni = 124;
            } else if (uni == 101) {
               uni = 164;
            } else {
               uni = 0;
            }
         }

         if (uni > 0) {
            res.add(uni);
         }
      }

      return res;
   }

   public String gsm2str(byte[] gsm) {
      if (!this._gsm2unicodeReady) {
         this.initGsm2Unicode();
      }

      int gsmlen = gsm.length;
      int reslen = gsmlen;

      for (int i = 0; i < gsmlen; i++) {
         if ((gsm[i] & 255) == 27) {
            reslen--;
         }
      }

      byte[] resbin = new byte[reslen];
      int var8 = 0;

      for (reslen = 0; var8 < gsmlen; var8++) {
         int uni = this._gsm2unicode.get(gsm[var8] & 255);
         if (uni == 27 && var8 + 1 < gsmlen) {
            uni = gsm[++var8] & 255;
            if (uni < 60) {
               if (uni < 41) {
                  if (uni == 20) {
                     uni = 94;
                  } else if (uni == 40) {
                     uni = 123;
                  } else {
                     uni = 0;
                  }
               } else if (uni == 41) {
                  uni = 125;
               } else if (uni == 47) {
                  uni = 92;
               } else {
                  uni = 0;
               }
            } else if (uni < 62) {
               if (uni == 60) {
                  uni = 91;
               } else if (uni == 61) {
                  uni = 126;
               } else {
                  uni = 0;
               }
            } else if (uni == 62) {
               uni = 93;
            } else if (uni == 64) {
               uni = 124;
            } else if (uni == 101) {
               uni = 164;
            } else {
               uni = 0;
            }
         }

         if (uni > 0 && uni < 256) {
            resbin[reslen] = (byte)(uni & 0xFF);
            reslen++;
         }
      }

      String resstr = new String(resbin);
      if (resstr.length() > reslen) {
         resstr = resstr.substring(0, reslen);
      }

      return resstr;
   }

   public byte[] str2gsm(String msg) {
      if (!this._gsm2unicodeReady) {
         this.initGsm2Unicode();
      }

      byte[] asc = msg.getBytes();
      int asclen = asc.length;
      int extra = 0;

      for (int i = 0; i < asclen; i++) {
         int ch = asc[i] & 255;
         int gsm7 = this._iso2gsm[ch] & 255;
         if (gsm7 == 27) {
            extra++;
         }

         if (gsm7 == 0) {
            return new byte[0];
         }
      }

      byte[] res = new byte[asclen + extra];
      int wpos = 0;

      for (int var10 = 0; var10 < asclen; var10++) {
         int chx = asc[var10] & 255;
         int gsm7x = this._iso2gsm[chx] & 255;
         res[wpos] = (byte)(gsm7x & 0xFF);
         wpos++;
         if (gsm7x == 27) {
            byte var13;
            if (chx < 100) {
               if (chx < 93) {
                  if (chx < 92) {
                     var13 = 60;
                  } else {
                     var13 = 47;
                  }
               } else if (chx < 94) {
                  var13 = 62;
               } else {
                  var13 = 20;
               }
            } else if (chx < 125) {
               if (chx < 124) {
                  var13 = 40;
               } else {
                  var13 = 64;
               }
            } else if (chx < 126) {
               var13 = 41;
            } else {
               var13 = 61;
            }

            res[wpos] = (byte)(var13 & 255);
            wpos++;
         }
      }

      return res;
   }

   public int checkNewMessages() throws YAPI_Exception {
      ArrayList<YSms> newArr = new ArrayList<>();
      ArrayList<YSms> newMsg = new ArrayList<>();
      ArrayList<YSms> newAgg = new ArrayList<>();
      ArrayList<String> signatures = new ArrayList<>();
      String bitmapStr = this.get_slotsBitmap();
      if (bitmapStr.equals(this._prevBitmapStr)) {
         return 0;
      } else {
         byte[] prevBitmap = YAPIContext._hexStrToBin(this._prevBitmapStr);
         byte[] newBitmap = YAPIContext._hexStrToBin(bitmapStr);
         this._prevBitmapStr = bitmapStr;
         int nslots = 8 * newBitmap.length;
         newArr.clear();
         newMsg.clear();
         signatures.clear();
         int nsig = 0;

         for (int pduIdx = 0; pduIdx < this._pdus.size(); pduIdx++) {
            YSms sms = this._pdus.get(pduIdx);
            int slot = sms.get_slot();
            int idx = slot >> 3;
            if (idx < newBitmap.length) {
               int bitVal = 1 << (slot & 7);
               if ((newBitmap[idx] & 255 & bitVal) != 0) {
                  newArr.add(sms);
                  if (sms.get_concatCount() == 0) {
                     newMsg.add(sms);
                  } else {
                     String sig = sms.get_concatSignature();

                     for (int i = 0; i < nsig && sig.length() > 0; i++) {
                        if (signatures.get(i).equals(sig)) {
                           sig = "";
                        }
                     }

                     if (sig.length() > 0) {
                        signatures.add(sig);
                        nsig++;
                     }
                  }
               }
            }
         }

         for (int slot = 0; slot < nslots; slot++) {
            int idx = slot >> 3;
            int bitVal = 1 << (slot & 7);
            int prevBit = 0;
            if (idx < prevBitmap.length) {
               prevBit = prevBitmap[idx] & 255 & bitVal;
            }

            if ((newBitmap[idx] & 255 & bitVal) != 0 && prevBit == 0) {
               YSms sms = this.fetchPdu(slot);
               newArr.add(sms);
               if (sms.get_concatCount() == 0) {
                  newMsg.add(sms);
               } else {
                  String sig = sms.get_concatSignature();

                  for (int ix = 0; ix < nsig && sig.length() > 0; ix++) {
                     if (signatures.get(ix).equals(sig)) {
                        sig = "";
                     }
                  }

                  if (sig.length() > 0) {
                     signatures.add(sig);
                     nsig++;
                  }
               }
            }
         }

         this._pdus = newArr;

         for (int ixx = 0; ixx < nsig; ixx++) {
            String sig = signatures.get(ixx);
            int cnt = 0;

            for (int var20 = 0; var20 < this._pdus.size(); var20++) {
               YSms sms = this._pdus.get(var20);
               if (sms.get_concatCount() > 0 && sms.get_concatSignature().equals(sig)) {
                  if (cnt == 0) {
                     cnt = sms.get_concatCount();
                     newAgg.clear();
                  }

                  newAgg.add(sms);
               }
            }

            if (cnt > 0 && newAgg.size() == cnt) {
               YSms sms = new YSms(this);
               sms.set_parts(newAgg);
               newMsg.add(sms);
            }
         }

         this._messages = newMsg;
         return 0;
      }
   }

   public ArrayList<YSms> get_pdus() throws YAPI_Exception {
      this.checkNewMessages();
      return this._pdus;
   }

   public int clearPduCounters() throws YAPI_Exception {
      int retcode = this.set_pduReceived(0);
      return retcode != 0 ? retcode : this.set_pduSent(0);
   }

   public int sendTextMessage(String recipient, String message) throws YAPI_Exception {
      YSms sms = new YSms(this);
      sms.set_recipient(recipient);
      sms.addText(message);
      return sms.send();
   }

   public int sendFlashMessage(String recipient, String message) throws YAPI_Exception {
      YSms sms = new YSms(this);
      sms.set_recipient(recipient);
      sms.set_msgClass(0);
      sms.addText(message);
      return sms.send();
   }

   public YSms newMessage(String recipient) throws YAPI_Exception {
      YSms sms = new YSms(this);
      sms.set_recipient(recipient);
      return sms;
   }

   public ArrayList<YSms> get_messages() throws YAPI_Exception {
      this.checkNewMessages();
      return this._messages;
   }

   public YMessageBox nextMessageBox() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMessageBoxInContext(this._yapi, next_hwid);
   }

   public static YMessageBox FirstMessageBox() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("MessageBox");
         return next_hwid == null ? null : FindMessageBoxInContext(yctx, next_hwid);
      }
   }

   public static YMessageBox FirstMessageBoxInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("MessageBox");
      return next_hwid == null ? null : FindMessageBoxInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMessageBox var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMessageBox var1, String var2);
   }
}
