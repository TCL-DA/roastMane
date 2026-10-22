package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class YCellular extends YFunction {
   public static final int LINKQUALITY_INVALID = -1;
   public static final String CELLOPERATOR_INVALID = "!INVALID!";
   public static final String CELLIDENTIFIER_INVALID = "!INVALID!";
   public static final int CELLTYPE_GPRS = 0;
   public static final int CELLTYPE_EGPRS = 1;
   public static final int CELLTYPE_WCDMA = 2;
   public static final int CELLTYPE_HSDPA = 3;
   public static final int CELLTYPE_NONE = 4;
   public static final int CELLTYPE_CDMA = 5;
   public static final int CELLTYPE_LTE_M = 6;
   public static final int CELLTYPE_NB_IOT = 7;
   public static final int CELLTYPE_EC_GSM_IOT = 8;
   public static final int CELLTYPE_INVALID = -1;
   public static final String IMSI_INVALID = "!INVALID!";
   public static final String MESSAGE_INVALID = "!INVALID!";
   public static final String PIN_INVALID = "!INVALID!";
   public static final String RADIOCONFIG_INVALID = "!INVALID!";
   public static final String LOCKEDOPERATOR_INVALID = "!INVALID!";
   public static final int AIRPLANEMODE_OFF = 0;
   public static final int AIRPLANEMODE_ON = 1;
   public static final int AIRPLANEMODE_INVALID = -1;
   public static final int ENABLEDATA_HOMENETWORK = 0;
   public static final int ENABLEDATA_ROAMING = 1;
   public static final int ENABLEDATA_NEVER = 2;
   public static final int ENABLEDATA_NEUTRALITY = 3;
   public static final int ENABLEDATA_INVALID = -1;
   public static final String APN_INVALID = "!INVALID!";
   public static final String APNSECRET_INVALID = "!INVALID!";
   public static final int PINGINTERVAL_INVALID = -1;
   public static final int DATASENT_INVALID = -1;
   public static final int DATARECEIVED_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _linkQuality = -1;
   protected String _cellOperator = "!INVALID!";
   protected String _cellIdentifier = "!INVALID!";
   protected int _cellType = -1;
   protected String _imsi = "!INVALID!";
   protected String _message = "!INVALID!";
   protected String _pin = "!INVALID!";
   protected String _radioConfig = "!INVALID!";
   protected String _lockedOperator = "!INVALID!";
   protected int _airplaneMode = -1;
   protected int _enableData = -1;
   protected String _apn = "!INVALID!";
   protected String _apnSecret = "!INVALID!";
   protected int _pingInterval = -1;
   protected int _dataSent = -1;
   protected int _dataReceived = -1;
   protected String _command = "!INVALID!";
   protected YCellular.UpdateCallback _valueCallbackCellular = null;

   protected YCellular(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Cellular";
   }

   protected YCellular(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("linkQuality")) {
         this._linkQuality = json_val.getInt("linkQuality");
      }

      if (json_val.has("cellOperator")) {
         this._cellOperator = json_val.getString("cellOperator");
      }

      if (json_val.has("cellIdentifier")) {
         this._cellIdentifier = json_val.getString("cellIdentifier");
      }

      if (json_val.has("cellType")) {
         this._cellType = json_val.getInt("cellType");
      }

      if (json_val.has("imsi")) {
         this._imsi = json_val.getString("imsi");
      }

      if (json_val.has("message")) {
         this._message = json_val.getString("message");
      }

      if (json_val.has("pin")) {
         this._pin = json_val.getString("pin");
      }

      if (json_val.has("radioConfig")) {
         this._radioConfig = json_val.getString("radioConfig");
      }

      if (json_val.has("lockedOperator")) {
         this._lockedOperator = json_val.getString("lockedOperator");
      }

      if (json_val.has("airplaneMode")) {
         this._airplaneMode = json_val.getInt("airplaneMode") > 0 ? 1 : 0;
      }

      if (json_val.has("enableData")) {
         this._enableData = json_val.getInt("enableData");
      }

      if (json_val.has("apn")) {
         this._apn = json_val.getString("apn");
      }

      if (json_val.has("apnSecret")) {
         this._apnSecret = json_val.getString("apnSecret");
      }

      if (json_val.has("pingInterval")) {
         this._pingInterval = json_val.getInt("pingInterval");
      }

      if (json_val.has("dataSent")) {
         this._dataSent = json_val.getInt("dataSent");
      }

      if (json_val.has("dataReceived")) {
         this._dataReceived = json_val.getInt("dataReceived");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_linkQuality() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._linkQuality;
      }
   }

   public int getLinkQuality() throws YAPI_Exception {
      return this.get_linkQuality();
   }

   public String get_cellOperator() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._cellOperator;
      }
   }

   public String getCellOperator() throws YAPI_Exception {
      return this.get_cellOperator();
   }

   public String get_cellIdentifier() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._cellIdentifier;
      }
   }

   public String getCellIdentifier() throws YAPI_Exception {
      return this.get_cellIdentifier();
   }

   public int get_cellType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._cellType;
      }
   }

   public int getCellType() throws YAPI_Exception {
      return this.get_cellType();
   }

   public String get_imsi() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._imsi;
      }
   }

   public String getImsi() throws YAPI_Exception {
      return this.get_imsi();
   }

   public String get_message() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._message;
      }
   }

   public String getMessage() throws YAPI_Exception {
      return this.get_message();
   }

   public String get_pin() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._pin;
      }
   }

   public String getPin() throws YAPI_Exception {
      return this.get_pin();
   }

   public int set_pin(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("pin", newval);
         return 0;
      }
   }

   public int setPin(String newval) throws YAPI_Exception {
      return this.set_pin(newval);
   }

   public String get_radioConfig() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._radioConfig;
      }
   }

   public String getRadioConfig() throws YAPI_Exception {
      return this.get_radioConfig();
   }

   public int set_radioConfig(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("radioConfig", newval);
         return 0;
      }
   }

   public int setRadioConfig(String newval) throws YAPI_Exception {
      return this.set_radioConfig(newval);
   }

   public String get_lockedOperator() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._lockedOperator;
      }
   }

   public String getLockedOperator() throws YAPI_Exception {
      return this.get_lockedOperator();
   }

   public int set_lockedOperator(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("lockedOperator", newval);
         return 0;
      }
   }

   public int setLockedOperator(String newval) throws YAPI_Exception {
      return this.set_lockedOperator(newval);
   }

   public int get_airplaneMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._airplaneMode;
      }
   }

   public int getAirplaneMode() throws YAPI_Exception {
      return this.get_airplaneMode();
   }

   public int set_airplaneMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("airplaneMode", rest_val);
         return 0;
      }
   }

   public int setAirplaneMode(int newval) throws YAPI_Exception {
      return this.set_airplaneMode(newval);
   }

   public int get_enableData() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._enableData;
      }
   }

   public int getEnableData() throws YAPI_Exception {
      return this.get_enableData();
   }

   public int set_enableData(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("enableData", rest_val);
         return 0;
      }
   }

   public int setEnableData(int newval) throws YAPI_Exception {
      return this.set_enableData(newval);
   }

   public String get_apn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._apn;
      }
   }

   public String getApn() throws YAPI_Exception {
      return this.get_apn();
   }

   public int set_apn(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("apn", newval);
         return 0;
      }
   }

   public int setApn(String newval) throws YAPI_Exception {
      return this.set_apn(newval);
   }

   public String get_apnSecret() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._apnSecret;
      }
   }

   public String getApnSecret() throws YAPI_Exception {
      return this.get_apnSecret();
   }

   public int set_apnSecret(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("apnSecret", newval);
         return 0;
      }
   }

   public int get_pingInterval() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._pingInterval;
      }
   }

   public int getPingInterval() throws YAPI_Exception {
      return this.get_pingInterval();
   }

   public int set_pingInterval(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("pingInterval", rest_val);
         return 0;
      }
   }

   public int setPingInterval(int newval) throws YAPI_Exception {
      return this.set_pingInterval(newval);
   }

   public int get_dataSent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._dataSent;
      }
   }

   public int getDataSent() throws YAPI_Exception {
      return this.get_dataSent();
   }

   public int set_dataSent(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("dataSent", rest_val);
         return 0;
      }
   }

   public int setDataSent(int newval) throws YAPI_Exception {
      return this.set_dataSent(newval);
   }

   public int get_dataReceived() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._dataReceived;
      }
   }

   public int getDataReceived() throws YAPI_Exception {
      return this.get_dataReceived();
   }

   public int set_dataReceived(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("dataReceived", rest_val);
         return 0;
      }
   }

   public int setDataReceived(int newval) throws YAPI_Exception {
      return this.set_dataReceived(newval);
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

   public static YCellular FindCellular(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YCellular obj = (YCellular)YFunction._FindFromCache("Cellular", func);
         if (obj == null) {
            obj = new YCellular(func);
            YFunction._AddToCache("Cellular", func, obj);
         }

         return obj;
      }
   }

   public static YCellular FindCellularInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YCellular obj = (YCellular)YFunction._FindFromCacheInContext(yctx, "Cellular", func);
         if (obj == null) {
            obj = new YCellular(yctx, func);
            YFunction._AddToCache("Cellular", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YCellular.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackCellular = callback;
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
      if (this._valueCallbackCellular != null) {
         this._valueCallbackCellular.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendPUK(String puk, String newPin) throws YAPI_Exception {
      String gsmMsg = this.get_message();
      if (!gsmMsg.substring(0, 13).equals("Enter SIM PUK")) {
         throw new YAPI_Exception(-2, "PUK not expected at this time");
      } else {
         return newPin.equals("")
            ? this.set_command(String.format(Locale.US, "AT+CPIN=%s,0000;+CLCK=SC,0,0000", puk))
            : this.set_command(String.format(Locale.US, "AT+CPIN=%s,%s", puk, newPin));
      }
   }

   public int set_apnAuth(String username, String password) throws YAPI_Exception {
      return this.set_apnSecret(String.format(Locale.US, "%s,%s", username, password));
   }

   public int clearDataCounters() throws YAPI_Exception {
      int retcode = this.set_dataReceived(0);
      return retcode != 0 ? retcode : this.set_dataSent(0);
   }

   public String _AT(String cmd) throws YAPI_Exception {
      int cmdLen = cmd.length();

      for (int chrPos = cmd.indexOf("#"); chrPos >= 0; chrPos = cmd.indexOf("#")) {
         cmd = String.format(Locale.US, "%s%c23%s", cmd.substring(0, chrPos), 37, cmd.substring(chrPos + 1, chrPos + 1 + cmdLen - chrPos - 1));
         cmdLen += 2;
      }

      for (int var13 = cmd.indexOf("+"); var13 >= 0; var13 = cmd.indexOf("+")) {
         cmd = String.format(Locale.US, "%s%c2B%s", cmd.substring(0, var13), 37, cmd.substring(var13 + 1, var13 + 1 + cmdLen - var13 - 1));
         cmdLen += 2;
      }

      for (int var14 = cmd.indexOf("="); var14 >= 0; var14 = cmd.indexOf("=")) {
         cmd = String.format(Locale.US, "%s%c3D%s", cmd.substring(0, var14), 37, cmd.substring(var14 + 1, var14 + 1 + cmdLen - var14 - 1));
         cmdLen += 2;
      }

      cmd = String.format(Locale.US, "at.txt?cmd=%s", cmd);
      String res = "";
      int waitMore = 24;

      while (waitMore > 0) {
         byte[] buff = this._download(cmd);
         int bufflen = buff.length;
         String buffstr = new String(buff);
         int buffstrlen = buffstr.length();
         int idx = bufflen - 1;

         while (idx > 0 && (buff[idx] & 255) != 64 && (buff[idx] & 255) != 10 && (buff[idx] & 255) != 13) {
            idx--;
         }

         if ((buff[idx] & 255) == 64) {
            int suffixlen = bufflen - idx;
            cmd = String.format(Locale.US, "at.txt?cmd=%s", buffstr.substring(buffstrlen - suffixlen, buffstrlen - suffixlen + suffixlen));
            buffstr = buffstr.substring(0, buffstrlen - suffixlen);
            waitMore--;
         } else {
            waitMore = 0;
         }

         res = String.format(Locale.US, "%s%s", res, buffstr);
      }

      return res;
   }

   public ArrayList<String> get_availableOperators() throws YAPI_Exception {
      ArrayList<String> res = new ArrayList<>();
      String cops = this._AT("+COPS=?");
      int slen = cops.length();
      res.clear();

      for (int idx = cops.indexOf("("); idx >= 0; idx = cops.indexOf("(")) {
         slen -= idx + 1;
         cops = cops.substring(idx + 1, idx + 1 + slen);
         idx = cops.indexOf("\"");
         if (idx > 0) {
            slen -= idx + 1;
            cops = cops.substring(idx + 1, idx + 1 + slen);
            idx = cops.indexOf("\"");
            if (idx > 0) {
               res.add(cops.substring(0, idx));
            }
         }
      }

      return res;
   }

   public ArrayList<YCellRecord> quickCellSurvey() throws YAPI_Exception {
      new ArrayList();
      ArrayList<YCellRecord> res = new ArrayList<>();
      String moni = this._AT("+CCED=0;#MONI=7;#MONI");
      String mccs = moni.substring(7, 10);
      if (mccs.substring(0, 1).equals("0")) {
         mccs = mccs.substring(1, 3);
      }

      if (mccs.substring(0, 1).equals("0")) {
         mccs = mccs.substring(1, 2);
      }

      int mcc = YAPIContext._atoi(mccs);
      String mncs = moni.substring(11, 14);
      if (mncs.substring(2, 3).equals(",")) {
         mncs = mncs.substring(0, 2);
      }

      if (mncs.substring(0, 1).equals("0")) {
         mncs = mncs.substring(1, 1 + mncs.length() - 1);
      }

      int mnc = YAPIContext._atoi(mncs);
      ArrayList<String> recs = new ArrayList<>(Arrays.asList(moni.split("#")));
      res.clear();

      for (String ii : recs) {
         int llen = ii.length() - 2;
         if (llen >= 44 && ii.substring(41, 44).equals("dbm")) {
            int lac = Integer.valueOf(ii.substring(16, 20), 16);
            int cellId = Integer.valueOf(ii.substring(23, 27), 16);
            String dbms = ii.substring(37, 41);
            if (dbms.substring(0, 1).equals(" ")) {
               dbms = dbms.substring(1, 4);
            }

            int dbm = YAPIContext._atoi(dbms);
            int tad;
            String oper;
            if (llen > 66) {
               String tads = ii.substring(54, 56);
               if (tads.substring(0, 1).equals(" ")) {
                  tads = tads.substring(1, 4);
               }

               tad = YAPIContext._atoi(tads);
               oper = ii.substring(66, 66 + llen - 66);
            } else {
               tad = -1;
               oper = "";
            }

            if (lac < 65535) {
               res.add(new YCellRecord(mcc, mnc, lac, cellId, dbm, tad, oper));
            }
         }
      }

      return res;
   }

   public String imm_decodePLMN(String mccmnc) {
      int inputlen = mccmnc.length();
      if (inputlen < 5) {
         return mccmnc;
      } else {
         int mcc = YAPIContext._atoi(mccmnc.substring(0, 3));
         if (mcc < 200) {
            return mccmnc;
         } else {
            int npos;
            if (mccmnc.substring(3, 4).equals(" ")) {
               npos = 4;
            } else {
               npos = 3;
            }

            int plmnid = mcc;

            while (plmnid < 100000 && npos < inputlen) {
               String ch = mccmnc.substring(npos, npos + 1);
               int nval = YAPIContext._atoi(ch);
               if (ch.equals(Integer.toString(nval))) {
                  plmnid = plmnid * 10 + nval;
                  npos++;
               } else {
                  npos = inputlen;
               }
            }

            if (plmnid < 20201) {
               return mccmnc;
            } else {
               if (plmnid < 50503) {
                  if (plmnid < 40407) {
                     if (plmnid < 25008) {
                        if (plmnid < 23102) {
                           if (plmnid < 21601) {
                              if (plmnid < 20809) {
                                 if (plmnid < 20408) {
                                    if (plmnid < 20210) {
                                       if (plmnid == 20201) {
                                          return "Cosmote";
                                       }

                                       if (plmnid == 20202) {
                                          return "Cosmote";
                                       }

                                       if (plmnid == 20205) {
                                          return "Vodafone GR";
                                       }

                                       if (plmnid == 20209) {
                                          return "Wind GR";
                                       }
                                    } else {
                                       if (plmnid == 20210) {
                                          return "Wind GR";
                                       }

                                       if (plmnid == 20402) {
                                          return "Tele2 NL";
                                       }

                                       if (plmnid == 20403) {
                                          return "Voiceworks";
                                       }

                                       if (plmnid == 20404) {
                                          return "Vodafone NL";
                                       }
                                    }
                                 } else if (plmnid < 20601) {
                                    if (plmnid == 20408) {
                                       return "KPN";
                                    }

                                    if (plmnid == 20410) {
                                       return "KPN";
                                    }

                                    if (plmnid == 20416) {
                                       return "T-Mobile (BEN)";
                                    }

                                    if (plmnid == 20420) {
                                       return "T-Mobile NL";
                                    }
                                 } else if (plmnid < 20620) {
                                    if (plmnid == 20601) {
                                       return "Proximus";
                                    }

                                    if (plmnid == 20610) {
                                       return "Orange Belgium";
                                    }
                                 } else {
                                    if (plmnid == 20620) {
                                       return "Base";
                                    }

                                    if (plmnid == 20801) {
                                       return "Orange FR";
                                    }

                                    if (plmnid == 20802) {
                                       return "Orange FR";
                                    }
                                 }
                              } else if (plmnid < 20836) {
                                 if (plmnid < 20815) {
                                    if (plmnid == 20809) {
                                       return "SFR";
                                    }

                                    if (plmnid == 20810) {
                                       return "SFR";
                                    }

                                    if (plmnid == 20813) {
                                       return "SFR";
                                    }

                                    if (plmnid == 20814) {
                                       return "SNCF Réseau";
                                    }
                                 } else {
                                    if (plmnid == 20815) {
                                       return "Free FR";
                                    }

                                    if (plmnid == 20816) {
                                       return "Free FR";
                                    }

                                    if (plmnid == 20820) {
                                       return "Bouygues";
                                    }

                                    if (plmnid == 20835) {
                                       return "Free FR";
                                    }
                                 }
                              } else if (plmnid < 21401) {
                                 if (plmnid == 20836) {
                                    return "Free FR";
                                 }

                                 if (plmnid == 20888) {
                                    return "Bouygues";
                                 }

                                 if (plmnid == 21210) {
                                    return "Office des Telephones";
                                 }

                                 if (plmnid == 21303) {
                                    return "Som, Mobiland";
                                 }
                              } else if (plmnid < 21404) {
                                 if (plmnid == 21401) {
                                    return "Vodafone ES";
                                 }

                                 if (plmnid == 21403) {
                                    return "Orange ES";
                                 }
                              } else {
                                 if (plmnid == 21404) {
                                    return "Yoigo";
                                 }

                                 if (plmnid == 21407) {
                                    return "Movistar ES";
                                 }

                                 if (plmnid == 21451) {
                                    return "ADIF";
                                 }
                              }
                           } else if (plmnid < 22210) {
                              if (plmnid < 21901) {
                                 if (plmnid < 21699) {
                                    if (plmnid == 21601) {
                                       return "Telenor Hungary";
                                    }

                                    if (plmnid == 21603) {
                                       return "DIGI";
                                    }

                                    if (plmnid == 21630) {
                                       return "Telekom HU";
                                    }

                                    if (plmnid == 21670) {
                                       return "Vodafone HU";
                                    }
                                 } else {
                                    if (plmnid == 21699) {
                                       return "MAV GSM-R";
                                    }

                                    if (plmnid == 21803) {
                                       return "HT-ERONET";
                                    }

                                    if (plmnid == 21805) {
                                       return "m:tel BiH";
                                    }

                                    if (plmnid == 21890) {
                                       return "BH Mobile";
                                    }
                                 }
                              } else if (plmnid < 22003) {
                                 if (plmnid == 21901) {
                                    return "T-Mobile HR";
                                 }

                                 if (plmnid == 21902) {
                                    return "Tele2 HR";
                                 }

                                 if (plmnid == 21910) {
                                    return "A1 HR";
                                 }

                                 if (plmnid == 22001) {
                                    return "Telenor RS";
                                 }
                              } else if (plmnid < 22101) {
                                 if (plmnid == 22003) {
                                    return "mts";
                                 }

                                 if (plmnid == 22005) {
                                    return "VIP";
                                 }
                              } else {
                                 if (plmnid == 22101) {
                                    return "Vala";
                                 }

                                 if (plmnid == 22102) {
                                    return "IPKO";
                                 }

                                 if (plmnid == 22201) {
                                    return "TIM IT";
                                 }
                              }
                           } else if (plmnid < 22801) {
                              if (plmnid < 22299) {
                                 if (plmnid == 22210) {
                                    return "Vodafone IT";
                                 }

                                 if (plmnid == 22230) {
                                    return "RFI";
                                 }

                                 if (plmnid == 22250) {
                                    return "Iliad";
                                 }

                                 if (plmnid == 22288) {
                                    return "Wind IT";
                                 }
                              } else if (plmnid < 22603) {
                                 if (plmnid == 22299) {
                                    return "3 Italia";
                                 }

                                 if (plmnid == 22601) {
                                    return "Vodafone RO";
                                 }
                              } else {
                                 if (plmnid == 22603) {
                                    return "Telekom RO";
                                 }

                                 if (plmnid == 22605) {
                                    return "Digi.Mobil";
                                 }

                                 if (plmnid == 22610) {
                                    return "Orange RO";
                                 }
                              }
                           } else if (plmnid < 22808) {
                              if (plmnid == 22801) {
                                 return "Swisscom CH";
                              }

                              if (plmnid == 22802) {
                                 return "Sunrise";
                              }

                              if (plmnid == 22803) {
                                 return "Salt";
                              }

                              if (plmnid == 22806) {
                                 return "SBB-CFF-FFS";
                              }
                           } else if (plmnid < 23002) {
                              if (plmnid == 22808) {
                                 return "Tele4u";
                              }

                              if (plmnid == 23001) {
                                 return "T-Mobile CZ";
                              }
                           } else {
                              if (plmnid == 23002) {
                                 return "O2 CZ";
                              }

                              if (plmnid == 23003) {
                                 return "Vodafone CZ";
                              }

                              if (plmnid == 23101) {
                                 return "Orange SK";
                              }
                           }
                        } else if (plmnid < 23458) {
                           if (plmnid < 23411) {
                              if (plmnid < 23205) {
                                 if (plmnid < 23106) {
                                    if (plmnid == 23102) {
                                       return "Telekom SK";
                                    }

                                    if (plmnid == 23103) {
                                       return "4ka";
                                    }

                                    if (plmnid == 23104) {
                                       return "Telekom SK";
                                    }

                                    if (plmnid == 23105) {
                                       return "Orange SK";
                                    }
                                 } else {
                                    if (plmnid == 23106) {
                                       return "O2 SK";
                                    }

                                    if (plmnid == 23199) {
                                       return "?SR";
                                    }

                                    if (plmnid == 23201) {
                                       return "A1.net";
                                    }

                                    if (plmnid == 23203) {
                                       return "Magenta";
                                    }
                                 }
                              } else if (plmnid < 23402) {
                                 if (plmnid == 23205) {
                                    return "3 AT";
                                 }

                                 if (plmnid == 23210) {
                                    return "3 AT";
                                 }

                                 if (plmnid == 23291) {
                                    return "GSM-R A";
                                 }

                                 if (plmnid == 23400) {
                                    return "BT";
                                 }
                              } else if (plmnid < 23403) {
                                 if (plmnid == 23402) {
                                    return "O2 (UK)";
                                 }

                                 if (plmnid == 23403) {
                                    return "Airtel-Vodafone GG";
                                 }
                              } else {
                                 if (plmnid == 23403) {
                                    return "Airtel-Vodafone JE";
                                 }

                                 if (plmnid == 23403) {
                                    return "Airtel-Vodafone GB";
                                 }

                                 if (plmnid == 23410) {
                                    return "O2 (UK)";
                                 }
                              }
                           } else if (plmnid < 23436) {
                              if (plmnid < 23419) {
                                 if (plmnid == 23411) {
                                    return "O2 (UK)";
                                 }

                                 if (plmnid == 23412) {
                                    return "Railtrack";
                                 }

                                 if (plmnid == 23413) {
                                    return "Railtrack";
                                 }

                                 if (plmnid == 23415) {
                                    return "Vodafone UK";
                                 }
                              } else if (plmnid < 23430) {
                                 if (plmnid == 23419) {
                                    return "Private Mobile Networks PMN";
                                 }

                                 if (plmnid == 23420) {
                                    return "3 GB";
                                 }
                              } else {
                                 if (plmnid == 23430) {
                                    return "T-Mobile UK";
                                 }

                                 if (plmnid == 23433) {
                                    return "Orange GB";
                                 }

                                 if (plmnid == 23434) {
                                    return "Orange GB";
                                 }
                              }
                           } else if (plmnid < 23450) {
                              if (plmnid == 23436) {
                                 return "Sure Mobile IM";
                              }

                              if (plmnid == 23436) {
                                 return "Sure Mobile GB";
                              }

                              if (plmnid == 23450) {
                                 return "JT GG";
                              }

                              if (plmnid == 23450) {
                                 return "JT JE";
                              }
                           } else if (plmnid < 23455) {
                              if (plmnid == 23450) {
                                 return "JT GB";
                              }

                              if (plmnid == 23455) {
                                 return "Sure Mobile GG";
                              }
                           } else {
                              if (plmnid == 23455) {
                                 return "Sure Mobile JE";
                              }

                              if (plmnid == 23455) {
                                 return "Sure Mobile GB";
                              }

                              if (plmnid == 23458) {
                                 return "Pronto GSM IM";
                              }
                           }
                        } else if (plmnid < 24405) {
                           if (plmnid < 24001) {
                              if (plmnid < 23806) {
                                 if (plmnid == 23458) {
                                    return "Pronto GSM GB";
                                 }

                                 if (plmnid == 23476) {
                                    return "BT";
                                 }

                                 if (plmnid == 23801) {
                                    return "TDC";
                                 }

                                 if (plmnid == 23802) {
                                    return "Telenor DK";
                                 }
                              } else {
                                 if (plmnid == 23806) {
                                    return "3 DK";
                                 }

                                 if (plmnid == 23820) {
                                    return "Telia DK";
                                 }

                                 if (plmnid == 23823) {
                                    return "GSM-R DK";
                                 }

                                 if (plmnid == 23877) {
                                    return "Telenor DK";
                                 }
                              }
                           } else if (plmnid < 24024) {
                              if (plmnid == 24001) {
                                 return "Telia SE";
                              }

                              if (plmnid == 24002) {
                                 return "3 SE";
                              }

                              if (plmnid == 24007) {
                                 return "Tele2 SE";
                              }

                              if (plmnid == 24021) {
                                 return "MobiSir";
                              }
                           } else if (plmnid < 24202) {
                              if (plmnid == 24024) {
                                 return "Sweden 2G";
                              }

                              if (plmnid == 24201) {
                                 return "Telenor NO";
                              }
                           } else {
                              if (plmnid == 24202) {
                                 return "Telia NO";
                              }

                              if (plmnid == 24214) {
                                 return "ice";
                              }

                              if (plmnid == 24403) {
                                 return "DNA";
                              }
                           }
                        } else if (plmnid < 24605) {
                           if (plmnid < 24436) {
                              if (plmnid == 24405) {
                                 return "Elisa FI";
                              }

                              if (plmnid == 24407) {
                                 return "Nokia";
                              }

                              if (plmnid == 24412) {
                                 return "DNA";
                              }

                              if (plmnid == 24414) {
                                 return "Ålcom";
                              }
                           } else if (plmnid < 24601) {
                              if (plmnid == 24436) {
                                 return "Telia / DNA";
                              }

                              if (plmnid == 24491) {
                                 return "Telia FI";
                              }
                           } else {
                              if (plmnid == 24601) {
                                 return "Telia LT";
                              }

                              if (plmnid == 24602) {
                                 return "BIT?";
                              }

                              if (plmnid == 24603) {
                                 return "Tele2 LT";
                              }
                           }
                        } else if (plmnid < 24801) {
                           if (plmnid == 24605) {
                              return "LitRail";
                           }

                           if (plmnid == 24701) {
                              return "LMT";
                           }

                           if (plmnid == 24702) {
                              return "Tele2 LV";
                           }

                           if (plmnid == 24705) {
                              return "Bite";
                           }
                        } else if (plmnid < 24803) {
                           if (plmnid == 24801) {
                              return "Telia EE";
                           }

                           if (plmnid == 24802) {
                              return "Elisa EE";
                           }
                        } else {
                           if (plmnid == 24803) {
                              return "Tele2 EE";
                           }

                           if (plmnid == 25001) {
                              return "MTS RU";
                           }

                           if (plmnid == 25002) {
                              return "MegaFon RU";
                           }
                        }
                     } else if (plmnid < 28405) {
                        if (plmnid < 26801) {
                           if (plmnid < 25706) {
                              if (plmnid < 25099) {
                                 if (plmnid < 25033) {
                                    if (plmnid == 25008) {
                                       return "Vainah Telecom";
                                    }

                                    if (plmnid == 25020) {
                                       return "Tele2 RU";
                                    }

                                    if (plmnid == 25027) {
                                       return "Letai";
                                    }

                                    if (plmnid == 25032) {
                                       return "Win Mobile";
                                    }
                                 } else {
                                    if (plmnid == 25033) {
                                       return "Sevmobile";
                                    }

                                    if (plmnid == 25034) {
                                       return "Krymtelekom";
                                    }

                                    if (plmnid == 25035) {
                                       return "MOTIV";
                                    }

                                    if (plmnid == 25060) {
                                       return "Volna mobile";
                                    }
                                 }
                              } else if (plmnid < 25506) {
                                 if (plmnid == 25099) {
                                    return "Beeline RU";
                                 }

                                 if (plmnid == 25501) {
                                    return "Vodafone UA";
                                 }

                                 if (plmnid == 25502) {
                                    return "Kyivstar";
                                 }

                                 if (plmnid == 25503) {
                                    return "Kyivstar";
                                 }
                              } else if (plmnid < 25701) {
                                 if (plmnid == 25506) {
                                    return "lifecell";
                                 }

                                 if (plmnid == 25599) {
                                    return "Phoenix UA";
                                 }
                              } else {
                                 if (plmnid == 25701) {
                                    return "A1 BY";
                                 }

                                 if (plmnid == 25702) {
                                    return "MTS BY";
                                 }

                                 if (plmnid == 25704) {
                                    return "life:)";
                                 }
                              }
                           } else if (plmnid < 26015) {
                              if (plmnid < 25915) {
                                 if (plmnid == 25706) {
                                    return "beCloud";
                                 }

                                 if (plmnid == 25901) {
                                    return "Orange MD";
                                 }

                                 if (plmnid == 25902) {
                                    return "Moldcell";
                                 }

                                 if (plmnid == 25905) {
                                    return "Unité";
                                 }
                              } else if (plmnid < 26002) {
                                 if (plmnid == 25915) {
                                    return "IDC";
                                 }

                                 if (plmnid == 26001) {
                                    return "Plus";
                                 }
                              } else {
                                 if (plmnid == 26002) {
                                    return "T-Mobile PL";
                                 }

                                 if (plmnid == 26003) {
                                    return "Orange PL";
                                 }

                                 if (plmnid == 26006) {
                                    return "Play";
                                 }
                              }
                           } else if (plmnid < 26202) {
                              if (plmnid == 26015) {
                                 return "Aero2";
                              }

                              if (plmnid == 26016) {
                                 return "Aero2";
                              }

                              if (plmnid == 26034) {
                                 return "NetWorkS!";
                              }

                              if (plmnid == 26201) {
                                 return "Telekom DE";
                              }
                           } else if (plmnid < 26209) {
                              if (plmnid == 26202) {
                                 return "Vodafone DE";
                              }

                              if (plmnid == 26203) {
                                 return "O2 DE";
                              }
                           } else {
                              if (plmnid == 26209) {
                                 return "Vodafone DE";
                              }

                              if (plmnid == 26601) {
                                 return "GibTel";
                              }

                              if (plmnid == 26609) {
                                 return "Shine";
                              }
                           }
                        } else if (plmnid < 27601) {
                           if (plmnid < 27202) {
                              if (plmnid < 27071) {
                                 if (plmnid == 26801) {
                                    return "Vodafone PT";
                                 }

                                 if (plmnid == 26803) {
                                    return "NOS";
                                 }

                                 if (plmnid == 26806) {
                                    return "MEO";
                                 }

                                 if (plmnid == 27001) {
                                    return "POST";
                                 }
                              } else {
                                 if (plmnid == 27071) {
                                    return "CFL";
                                 }

                                 if (plmnid == 27077) {
                                    return "Tango";
                                 }

                                 if (plmnid == 27099) {
                                    return "Orange LU";
                                 }

                                 if (plmnid == 27201) {
                                    return "Vodafone IE";
                                 }
                              }
                           } else if (plmnid < 27401) {
                              if (plmnid == 27202) {
                                 return "3 IE";
                              }

                              if (plmnid == 27203) {
                                 return "Eir";
                              }

                              if (plmnid == 27205) {
                                 return "3 IE";
                              }

                              if (plmnid == 27207) {
                                 return "Eir";
                              }
                           } else if (plmnid < 27404) {
                              if (plmnid == 27401) {
                                 return "Síminn";
                              }

                              if (plmnid == 27402) {
                                 return "Vodafone IS";
                              }
                           } else {
                              if (plmnid == 27404) {
                                 return "Viking";
                              }

                              if (plmnid == 27408) {
                                 return "On-waves";
                              }

                              if (plmnid == 27411) {
                                 return "Nova";
                              }
                           }
                        } else if (plmnid < 28201) {
                           if (plmnid < 27821) {
                              if (plmnid == 27601) {
                                 return "Telekom.al";
                              }

                              if (plmnid == 27602) {
                                 return "Vodafone AL";
                              }

                              if (plmnid == 27603) {
                                 return "Eagle Mobile";
                              }

                              if (plmnid == 27801) {
                                 return "Vodafone MT";
                              }
                           } else if (plmnid < 28001) {
                              if (plmnid == 27821) {
                                 return "GO";
                              }

                              if (plmnid == 27877) {
                                 return "Melita";
                              }
                           } else {
                              if (plmnid == 28001) {
                                 return "Cytamobile-Vodafone";
                              }

                              if (plmnid == 28010) {
                                 return "Epic";
                              }

                              if (plmnid == 28020) {
                                 return "PrimeTel";
                              }
                           }
                        } else if (plmnid < 28304) {
                           if (plmnid == 28201) {
                              return "Geocell";
                           }

                           if (plmnid == 28202) {
                              return "Magti";
                           }

                           if (plmnid == 28204) {
                              return "Beeline GE";
                           }

                           if (plmnid == 28301) {
                              return "Beeline AM";
                           }
                        } else if (plmnid < 28310) {
                           if (plmnid == 28304) {
                              return "Karabakh Telecom";
                           }

                           if (plmnid == 28305) {
                              return "VivaCell-MTS";
                           }
                        } else {
                           if (plmnid == 28310) {
                              return "Ucom";
                           }

                           if (plmnid == 28401) {
                              return "A1 BG";
                           }

                           if (plmnid == 28403) {
                              return "Vivacom";
                           }
                        }
                     } else if (plmnid < 36251) {
                        if (plmnid < 29501) {
                           if (plmnid < 28967) {
                              if (plmnid < 28602) {
                                 if (plmnid == 28405) {
                                    return "Telenor BG";
                                 }

                                 if (plmnid == 28407) {
                                    return "????";
                                 }

                                 if (plmnid == 28413) {
                                    return "??.???";
                                 }

                                 if (plmnid == 28601) {
                                    return "Turkcell";
                                 }
                              } else {
                                 if (plmnid == 28602) {
                                    return "Vodafone TR";
                                 }

                                 if (plmnid == 28603) {
                                    return "Türk Telekom";
                                 }

                                 if (plmnid == 28801) {
                                    return "Føroya Tele";
                                 }

                                 if (plmnid == 28802) {
                                    return "Hey";
                                 }
                              }
                           } else if (plmnid < 29341) {
                              if (plmnid == 28967) {
                                 return "Aquafon";
                              }

                              if (plmnid == 28988) {
                                 return "A-Mobile";
                              }

                              if (plmnid == 29201) {
                                 return "PRIMA";
                              }

                              if (plmnid == 29340) {
                                 return "A1 SI";
                              }
                           } else if (plmnid < 29401) {
                              if (plmnid == 29341) {
                                 return "Mobitel SI";
                              }

                              if (plmnid == 29370) {
                                 return "Telemach";
                              }
                           } else {
                              if (plmnid == 29401) {
                                 return "Telekom.mk";
                              }

                              if (plmnid == 29402) {
                                 return "vip";
                              }

                              if (plmnid == 29403) {
                                 return "vip";
                              }
                           }
                        } else if (plmnid < 34001) {
                           if (plmnid < 29702) {
                              if (plmnid == 29501) {
                                 return "Swisscom LI";
                              }

                              if (plmnid == 29502) {
                                 return "7acht";
                              }

                              if (plmnid == 29505) {
                                 return "FL1";
                              }

                              if (plmnid == 29701) {
                                 return "Telenor ME";
                              }
                           } else if (plmnid < 30801) {
                              if (plmnid == 29702) {
                                 return "T-Mobile ME";
                              }

                              if (plmnid == 29703) {
                                 return "m:tel CG";
                              }
                           } else {
                              if (plmnid == 30801) {
                                 return "Ameris";
                              }

                              if (plmnid == 30802) {
                                 return "GLOBALTEL";
                              }

                              if (plmnid == 34001) {
                                 return "Orange BL/GF/GP/MF/MQ";
                              }
                           }
                        } else if (plmnid < 34008) {
                           if (plmnid == 34001) {
                              return "Orange GF";
                           }

                           if (plmnid == 34002) {
                              return "SFR Caraïbe BL/GF/GP/MF/MQ";
                           }

                           if (plmnid == 34002) {
                              return "SFR Caraïbe GF";
                           }

                           if (plmnid == 34003) {
                              return "Chippie BL/GF/GP/MF/MQ";
                           }
                        } else if (plmnid < 34020) {
                           if (plmnid == 34008) {
                              return "Dauphin";
                           }

                           if (plmnid == 34020) {
                              return "Digicel BL/GF/GP/MF/MQ";
                           }
                        } else {
                           if (plmnid == 34020) {
                              return "Digicel GF";
                           }

                           if (plmnid == 35000) {
                              return "One";
                           }

                           if (plmnid == 35002) {
                              return "Mobility";
                           }
                        }
                     } else if (plmnid < 37202) {
                        if (plmnid < 36291) {
                           if (plmnid < 36268) {
                              if (plmnid == 36251) {
                                 return "Telcell";
                              }

                              if (plmnid == 36254) {
                                 return "ECC";
                              }

                              if (plmnid == 36259) {
                                 return "Chippie BQ/CW/SX";
                              }

                              if (plmnid == 36260) {
                                 return "Chippie BQ/CW/SX";
                              }
                           } else {
                              if (plmnid == 36268) {
                                 return "Digicel BQ/CW/SX";
                              }

                              if (plmnid == 36269) {
                                 return "Digicel BQ/CW/SX";
                              }

                              if (plmnid == 36276) {
                                 return "Digicel BQ/CW/SX";
                              }

                              if (plmnid == 36278) {
                                 return "Telbo";
                              }
                           }
                        } else if (plmnid < 36449) {
                           if (plmnid == 36291) {
                              return "Chippie BQ/CW/SX";
                           }

                           if (plmnid == 36301) {
                              return "SETAR";
                           }

                           if (plmnid == 36302) {
                              return "Digicel AW";
                           }

                           if (plmnid == 36439) {
                              return "BTC";
                           }
                        } else if (plmnid < 37001) {
                           if (plmnid == 36449) {
                              return "Aliv";
                           }

                           if (plmnid == 36801) {
                              return "CUBACEL";
                           }
                        } else {
                           if (plmnid == 37001) {
                              return "Altice";
                           }

                           if (plmnid == 37002) {
                              return "Claro DO";
                           }

                           if (plmnid == 37004) {
                              return "Viva DO";
                           }
                        }
                     } else if (plmnid < 40107) {
                        if (plmnid < 40002) {
                           if (plmnid == 37202) {
                              return "Digicel HT";
                           }

                           if (plmnid == 37203) {
                              return "Natcom";
                           }

                           if (plmnid == 37412) {
                              return "bmobile TT";
                           }

                           if (plmnid == 40001) {
                              return "Azercell";
                           }
                        } else if (plmnid < 40006) {
                           if (plmnid == 40002) {
                              return "Bakcell";
                           }

                           if (plmnid == 40004) {
                              return "Nar Mobile";
                           }
                        } else {
                           if (plmnid == 40006) {
                              return "Naxtel";
                           }

                           if (plmnid == 40101) {
                              return "Beeline KZ";
                           }

                           if (plmnid == 40102) {
                              return "Kcell";
                           }
                        }
                     } else if (plmnid < 40401) {
                        if (plmnid == 40107) {
                           return "Altel";
                        }

                        if (plmnid == 40177) {
                           return "Tele2.kz";
                        }

                        if (plmnid == 40211) {
                           return "B-Mobile";
                        }

                        if (plmnid == 40277) {
                           return "TashiCell";
                        }
                     } else if (plmnid < 40403) {
                        if (plmnid == 40401) {
                           return "Vodafone India";
                        }

                        if (plmnid == 40402) {
                           return "AirTel";
                        }
                     } else {
                        if (plmnid == 40403) {
                           return "AirTel";
                        }

                        if (plmnid == 40404) {
                           return "IDEA";
                        }

                        if (plmnid == 40405) {
                           return "Vodafone India";
                        }
                     }
                  } else if (plmnid < 42001) {
                     if (plmnid < 40493) {
                        if (plmnid < 40450) {
                           if (plmnid < 40427) {
                              if (plmnid < 40416) {
                                 if (plmnid < 40412) {
                                    if (plmnid == 40407) {
                                       return "IDEA";
                                    }

                                    if (plmnid == 40409) {
                                       return "Reliance";
                                    }

                                    if (plmnid == 40410) {
                                       return "AirTel";
                                    }

                                    if (plmnid == 40411) {
                                       return "Vodafone India";
                                    }
                                 } else {
                                    if (plmnid == 40412) {
                                       return "IDEA";
                                    }

                                    if (plmnid == 40413) {
                                       return "Vodafone India";
                                    }

                                    if (plmnid == 40414) {
                                       return "IDEA";
                                    }

                                    if (plmnid == 40415) {
                                       return "Vodafone India";
                                    }
                                 }
                              } else if (plmnid < 40420) {
                                 if (plmnid == 40416) {
                                    return "Airtel IN";
                                 }

                                 if (plmnid == 40417) {
                                    return "AIRCEL";
                                 }

                                 if (plmnid == 40418) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40419) {
                                    return "IDEA";
                                 }
                              } else if (plmnid < 40422) {
                                 if (plmnid == 40420) {
                                    return "Vodafone India";
                                 }

                                 if (plmnid == 40421) {
                                    return "Loop Mobile";
                                 }
                              } else {
                                 if (plmnid == 40422) {
                                    return "IDEA";
                                 }

                                 if (plmnid == 40424) {
                                    return "IDEA";
                                 }

                                 if (plmnid == 40425) {
                                    return "AIRCEL";
                                 }
                              }
                           } else if (plmnid < 40438) {
                              if (plmnid < 40431) {
                                 if (plmnid == 40427) {
                                    return "Vodafone India";
                                 }

                                 if (plmnid == 40428) {
                                    return "AIRCEL";
                                 }

                                 if (plmnid == 40429) {
                                    return "AIRCEL";
                                 }

                                 if (plmnid == 40430) {
                                    return "Vodafone India";
                                 }
                              } else if (plmnid < 40435) {
                                 if (plmnid == 40431) {
                                    return "AirTel";
                                 }

                                 if (plmnid == 40434) {
                                    return "cellone";
                                 }
                              } else {
                                 if (plmnid == 40435) {
                                    return "Aircel";
                                 }

                                 if (plmnid == 40436) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40437) {
                                    return "Aircel";
                                 }
                              }
                           } else if (plmnid < 40444) {
                              if (plmnid == 40438) {
                                 return "cellone";
                              }

                              if (plmnid == 40441) {
                                 return "Aircel";
                              }

                              if (plmnid == 40442) {
                                 return "Aircel";
                              }

                              if (plmnid == 40443) {
                                 return "Vodafone India";
                              }
                           } else if (plmnid < 40446) {
                              if (plmnid == 40444) {
                                 return "IDEA";
                              }

                              if (plmnid == 40445) {
                                 return "Airtel IN";
                              }
                           } else {
                              if (plmnid == 40446) {
                                 return "Vodafone India";
                              }

                              if (plmnid == 40448) {
                                 return "Dishnet Wireless";
                              }

                              if (plmnid == 40449) {
                                 return "Airtel IN";
                              }
                           }
                        } else if (plmnid < 40471) {
                           if (plmnid < 40458) {
                              if (plmnid < 40454) {
                                 if (plmnid == 40450) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40451) {
                                    return "cellone";
                                 }

                                 if (plmnid == 40452) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40453) {
                                    return "cellone";
                                 }
                              } else {
                                 if (plmnid == 40454) {
                                    return "cellone";
                                 }

                                 if (plmnid == 40455) {
                                    return "cellone";
                                 }

                                 if (plmnid == 40456) {
                                    return "IDEA";
                                 }

                                 if (plmnid == 40457) {
                                    return "cellone";
                                 }
                              }
                           } else if (plmnid < 40464) {
                              if (plmnid == 40458) {
                                 return "cellone";
                              }

                              if (plmnid == 40459) {
                                 return "cellone";
                              }

                              if (plmnid == 40460) {
                                 return "Vodafone India";
                              }

                              if (plmnid == 40462) {
                                 return "cellone";
                              }
                           } else if (plmnid < 40467) {
                              if (plmnid == 40464) {
                                 return "cellone";
                              }

                              if (plmnid == 40466) {
                                 return "cellone";
                              }
                           } else {
                              if (plmnid == 40467) {
                                 return "Reliance";
                              }

                              if (plmnid == 40468) {
                                 return "DOLPHIN";
                              }

                              if (plmnid == 40469) {
                                 return "DOLPHIN";
                              }
                           }
                        } else if (plmnid < 40480) {
                           if (plmnid < 40475) {
                              if (plmnid == 40471) {
                                 return "cellone";
                              }

                              if (plmnid == 40472) {
                                 return "cellone";
                              }

                              if (plmnid == 40473) {
                                 return "cellone";
                              }

                              if (plmnid == 40474) {
                                 return "cellone";
                              }
                           } else if (plmnid < 40477) {
                              if (plmnid == 40475) {
                                 return "cellone";
                              }

                              if (plmnid == 40476) {
                                 return "cellone";
                              }
                           } else {
                              if (plmnid == 40477) {
                                 return "cellone";
                              }

                              if (plmnid == 40478) {
                                 return "IDEA";
                              }

                              if (plmnid == 40479) {
                                 return "cellone";
                              }
                           }
                        } else if (plmnid < 40485) {
                           if (plmnid == 40480) {
                              return "cellone";
                           }

                           if (plmnid == 40481) {
                              return "cellone";
                           }

                           if (plmnid == 40483) {
                              return "Reliance";
                           }

                           if (plmnid == 40484) {
                              return "Vodafone India";
                           }
                        } else if (plmnid < 40490) {
                           if (plmnid == 40485) {
                              return "Reliance";
                           }

                           if (plmnid == 40486) {
                              return "Vodafone India";
                           }
                        } else {
                           if (plmnid == 40490) {
                              return "AirTel";
                           }

                           if (plmnid == 40491) {
                              return "AIRCEL";
                           }

                           if (plmnid == 40492) {
                              return "AirTel";
                           }
                        }
                     } else if (plmnid < 41004) {
                        if (plmnid < 40517) {
                           if (plmnid < 40507) {
                              if (plmnid < 40503) {
                                 if (plmnid == 40493) {
                                    return "AirTel";
                                 }

                                 if (plmnid == 40495) {
                                    return "AirTel";
                                 }

                                 if (plmnid == 40496) {
                                    return "AirTel";
                                 }

                                 if (plmnid == 40501) {
                                    return "Reliance";
                                 }
                              } else {
                                 if (plmnid == 40503) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40504) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40505) {
                                    return "Reliance";
                                 }

                                 if (plmnid == 40506) {
                                    return "Reliance";
                                 }
                              }
                           } else if (plmnid < 40511) {
                              if (plmnid == 40507) {
                                 return "Reliance";
                              }

                              if (plmnid == 40508) {
                                 return "Reliance";
                              }

                              if (plmnid == 40509) {
                                 return "Reliance";
                              }

                              if (plmnid == 40510) {
                                 return "Reliance";
                              }
                           } else if (plmnid < 40513) {
                              if (plmnid == 40511) {
                                 return "Reliance";
                              }

                              if (plmnid == 40512) {
                                 return "Reliance";
                              }
                           } else {
                              if (plmnid == 40513) {
                                 return "Reliance";
                              }

                              if (plmnid == 40514) {
                                 return "Reliance";
                              }

                              if (plmnid == 40515) {
                                 return "Reliance";
                              }
                           }
                        } else if (plmnid < 40553) {
                           if (plmnid < 40521) {
                              if (plmnid == 40517) {
                                 return "Reliance";
                              }

                              if (plmnid == 40518) {
                                 return "Reliance";
                              }

                              if (plmnid == 40519) {
                                 return "Reliance";
                              }

                              if (plmnid == 40520) {
                                 return "Reliance";
                              }
                           } else if (plmnid < 40523) {
                              if (plmnid == 40521) {
                                 return "Reliance";
                              }

                              if (plmnid == 40522) {
                                 return "Reliance";
                              }
                           } else {
                              if (plmnid == 40523) {
                                 return "Reliance";
                              }

                              if (plmnid == 40551) {
                                 return "AirTel";
                              }

                              if (plmnid == 40552) {
                                 return "AirTel";
                              }
                           }
                        } else if (plmnid < 40566) {
                           if (plmnid == 40553) {
                              return "AirTel";
                           }

                           if (plmnid == 40554) {
                              return "AirTel";
                           }

                           if (plmnid == 40555) {
                              return "Airtel IN";
                           }

                           if (plmnid == 40556) {
                              return "AirTel";
                           }
                        } else if (plmnid < 41001) {
                           if (plmnid == 40566) {
                              return "Vodafone India";
                           }

                           if (plmnid == 40570) {
                              return "IDEA";
                           }
                        } else {
                           if (plmnid == 41001) {
                              return "Jazz";
                           }

                           if (plmnid == 41002) {
                              return "3G EVO / CharJi 4G";
                           }

                           if (plmnid == 41003) {
                              return "Ufone";
                           }
                        }
                     } else if (plmnid < 41406) {
                        if (plmnid < 41250) {
                           if (plmnid < 41008) {
                              if (plmnid == 41004) {
                                 return "Zong";
                              }

                              if (plmnid == 41005) {
                                 return "SCO Mobile";
                              }

                              if (plmnid == 41006) {
                                 return "Telenor PK";
                              }

                              if (plmnid == 41007) {
                                 return "Jazz";
                              }
                           } else {
                              if (plmnid == 41008) {
                                 return "SCO Mobile";
                              }

                              if (plmnid == 41201) {
                                 return "AWCC";
                              }

                              if (plmnid == 41220) {
                                 return "Roshan";
                              }

                              if (plmnid == 41240) {
                                 return "MTN AF";
                              }
                           }
                        } else if (plmnid < 41302) {
                           if (plmnid == 41250) {
                              return "Etisalat AF";
                           }

                           if (plmnid == 41280) {
                              return "Salaam";
                           }

                           if (plmnid == 41288) {
                              return "Salaam";
                           }

                           if (plmnid == 41301) {
                              return "Mobitel LK";
                           }
                        } else if (plmnid < 41309) {
                           if (plmnid == 41302) {
                              return "Dialog";
                           }

                           if (plmnid == 41305) {
                              return "Airtel LK";
                           }
                        } else {
                           if (plmnid == 41309) {
                              return "Hutch";
                           }

                           if (plmnid == 41401) {
                              return "MPT";
                           }

                           if (plmnid == 41405) {
                              return "Ooredoo MM";
                           }
                        }
                     } else if (plmnid < 41800) {
                        if (plmnid < 41601) {
                           if (plmnid == 41406) {
                              return "Telenor MM";
                           }

                           if (plmnid == 41409) {
                              return "Mytel";
                           }

                           if (plmnid == 41501) {
                              return "Alfa";
                           }

                           if (plmnid == 41503) {
                              return "Touch";
                           }
                        } else if (plmnid < 41677) {
                           if (plmnid == 41601) {
                              return "zain JO";
                           }

                           if (plmnid == 41603) {
                              return "Umniah";
                           }
                        } else {
                           if (plmnid == 41677) {
                              return "Orange JO";
                           }

                           if (plmnid == 41701) {
                              return "Syriatel";
                           }

                           if (plmnid == 41702) {
                              return "MTN SY";
                           }
                        }
                     } else if (plmnid < 41830) {
                        if (plmnid == 41800) {
                           return "Asia Cell";
                        }

                        if (plmnid == 41805) {
                           return "Asia Cell";
                        }

                        if (plmnid == 41808) {
                           return "SanaTel";
                        }

                        if (plmnid == 41820) {
                           return "Zain IQ";
                        }
                     } else if (plmnid < 41902) {
                        if (plmnid == 41830) {
                           return "Zain IQ";
                        }

                        if (plmnid == 41840) {
                           return "Korek";
                        }
                     } else {
                        if (plmnid == 41902) {
                           return "zain KW";
                        }

                        if (plmnid == 41903) {
                           return "K.S.C Ooredoo";
                        }

                        if (plmnid == 41904) {
                           return "STC KW";
                        }
                     }
                  } else if (plmnid < 44054) {
                     if (plmnid < 43211) {
                        if (plmnid < 42506) {
                           if (plmnid < 42203) {
                              if (plmnid < 42101) {
                                 if (plmnid == 42001) {
                                    return "Al Jawal (STC )";
                                 }

                                 if (plmnid == 42003) {
                                    return "Mobily";
                                 }

                                 if (plmnid == 42004) {
                                    return "Zain SA";
                                 }

                                 if (plmnid == 42021) {
                                    return "RGSM";
                                 }
                              } else {
                                 if (plmnid == 42101) {
                                    return "SabaFon";
                                 }

                                 if (plmnid == 42102) {
                                    return "MTN YE";
                                 }

                                 if (plmnid == 42104) {
                                    return "Y";
                                 }

                                 if (plmnid == 42202) {
                                    return "Omantel";
                                 }
                              }
                           } else if (plmnid < 42502) {
                              if (plmnid == 42203) {
                                 return "ooredoo OM";
                              }

                              if (plmnid == 42402) {
                                 return "Etisalat AE";
                              }

                              if (plmnid == 42403) {
                                 return "du";
                              }

                              if (plmnid == 42501) {
                                 return "Partner";
                              }
                           } else if (plmnid < 42505) {
                              if (plmnid == 42502) {
                                 return "Cellcom IL";
                              }

                              if (plmnid == 42503) {
                                 return "Pelephone";
                              }
                           } else {
                              if (plmnid == 42505) {
                                 return "Jawwal IL";
                              }

                              if (plmnid == 42505) {
                                 return "Jawwal PS";
                              }

                              if (plmnid == 42506) {
                                 return "Wataniya Mobile";
                              }
                           }
                        } else if (plmnid < 42701) {
                           if (plmnid < 42510) {
                              if (plmnid == 42506) {
                                 return "Wataniya";
                              }

                              if (plmnid == 42507) {
                                 return "Hot Mobile";
                              }

                              if (plmnid == 42508) {
                                 return "Golan Telecom";
                              }

                              if (plmnid == 42509) {
                                 return "We4G";
                              }
                           } else if (plmnid < 42602) {
                              if (plmnid == 42510) {
                                 return "Partner";
                              }

                              if (plmnid == 42601) {
                                 return "Batelco";
                              }
                           } else {
                              if (plmnid == 42602) {
                                 return "zain BH";
                              }

                              if (plmnid == 42604) {
                                 return "STC BH";
                              }

                              if (plmnid == 42605) {
                                 return "Batelco";
                              }
                           }
                        } else if (plmnid < 42898) {
                           if (plmnid == 42701) {
                              return "ooredoo QA";
                           }

                           if (plmnid == 42702) {
                              return "Vodafone QA";
                           }

                           if (plmnid == 42888) {
                              return "Unitel MN";
                           }

                           if (plmnid == 42891) {
                              return "Skytel";
                           }
                        } else if (plmnid < 42901) {
                           if (plmnid == 42898) {
                              return "G-Mobile";
                           }

                           if (plmnid == 42899) {
                              return "Mobicom";
                           }
                        } else {
                           if (plmnid == 42901) {
                              return "Namaste / NT Mobile / Sky Phone";
                           }

                           if (plmnid == 42902) {
                              return "Ncell";
                           }

                           if (plmnid == 42904) {
                              return "SmartCell";
                           }
                        }
                     } else if (plmnid < 43408) {
                        if (plmnid < 43270) {
                           if (plmnid < 43220) {
                              if (plmnid == 43211) {
                                 return "IR-TCI (Hamrah-e-Avval)";
                              }

                              if (plmnid == 43212) {
                                 return "Avacell(HiWEB)";
                              }

                              if (plmnid == 43214) {
                                 return "TKC/KFZO";
                              }

                              if (plmnid == 43219) {
                                 return "Espadan (JV-PJS)";
                              }
                           } else {
                              if (plmnid == 43220) {
                                 return "RighTel";
                              }

                              if (plmnid == 43221) {
                                 return "RighTel";
                              }

                              if (plmnid == 43232) {
                                 return "Taliya";
                              }

                              if (plmnid == 43235) {
                                 return "MTN Irancell";
                              }
                           }
                        } else if (plmnid < 43293) {
                           if (plmnid == 43270) {
                              return "MTCE";
                           }

                           if (plmnid == 43271) {
                              return "KOOHE NOOR";
                           }

                           if (plmnid == 43290) {
                              return "Iraphone";
                           }

                           if (plmnid == 43293) {
                              return "Iraphone";
                           }
                        } else if (plmnid < 43404) {
                           if (plmnid == 43293) {
                              return "Farzanegan Pars";
                           }

                           if (plmnid == 43299) {
                              return "TCI (GSM WLL)";
                           }
                        } else {
                           if (plmnid == 43404) {
                              return "Beeline UZ";
                           }

                           if (plmnid == 43405) {
                              return "Ucell";
                           }

                           if (plmnid == 43407) {
                              return "Mobiuz";
                           }
                        }
                     } else if (plmnid < 43802) {
                        if (plmnid < 43604) {
                           if (plmnid == 43408) {
                              return "UzMobile";
                           }

                           if (plmnid == 43601) {
                              return "Tcell";
                           }

                           if (plmnid == 43602) {
                              return "Tcell";
                           }

                           if (plmnid == 43603) {
                              return "MegaFon TJ";
                           }
                        } else if (plmnid < 43701) {
                           if (plmnid == 43604) {
                              return "Babilon-M";
                           }

                           if (plmnid == 43605) {
                              return "ZET-Mobile";
                           }
                        } else {
                           if (plmnid == 43701) {
                              return "Beeline KG";
                           }

                           if (plmnid == 43705) {
                              return "MegaCom";
                           }

                           if (plmnid == 43709) {
                              return "O!";
                           }
                        }
                     } else if (plmnid < 44021) {
                        if (plmnid == 43802) {
                           return "TM-Cell";
                        }

                        if (plmnid == 44010) {
                           return "NTT docomo";
                        }

                        if (plmnid == 44011) {
                           return "Rakuten Mobile";
                        }

                        if (plmnid == 44020) {
                           return "SoftBank";
                        }
                     } else if (plmnid < 44051) {
                        if (plmnid == 44021) {
                           return "SoftBank";
                        }

                        if (plmnid == 44050) {
                           return "au";
                        }
                     } else {
                        if (plmnid == 44051) {
                           return "au";
                        }

                        if (plmnid == 44052) {
                           return "au";
                        }

                        if (plmnid == 44053) {
                           return "au";
                        }
                     }
                  } else if (plmnid < 45606) {
                     if (plmnid < 45205) {
                        if (plmnid < 44101) {
                           if (plmnid < 44073) {
                              if (plmnid == 44054) {
                                 return "au";
                              }

                              if (plmnid == 44070) {
                                 return "au";
                              }

                              if (plmnid == 44071) {
                                 return "au";
                              }

                              if (plmnid == 44072) {
                                 return "au";
                              }
                           } else {
                              if (plmnid == 44073) {
                                 return "au";
                              }

                              if (plmnid == 44074) {
                                 return "au";
                              }

                              if (plmnid == 44075) {
                                 return "au";
                              }

                              if (plmnid == 44076) {
                                 return "au";
                              }
                           }
                        } else if (plmnid < 45008) {
                           if (plmnid == 44101) {
                              return "SoftBank";
                           }

                           if (plmnid == 45004) {
                              return "KT";
                           }

                           if (plmnid == 45005) {
                              return "SKTelecom";
                           }

                           if (plmnid == 45006) {
                              return "LG U+";
                           }
                        } else if (plmnid < 45201) {
                           if (plmnid == 45008) {
                              return "olleh";
                           }

                           if (plmnid == 45012) {
                              return "SKTelecom";
                           }
                        } else {
                           if (plmnid == 45201) {
                              return "MobiFone";
                           }

                           if (plmnid == 45202) {
                              return "Vinaphone";
                           }

                           if (plmnid == 45204) {
                              return "Viettel Mobile";
                           }
                        }
                     } else if (plmnid < 45500) {
                        if (plmnid < 45404) {
                           if (plmnid == 45205) {
                              return "Vietnamobile";
                           }

                           if (plmnid == 45207) {
                              return "Gmobile";
                           }

                           if (plmnid == 45400) {
                              return "1O1O / One2Free / New World Mobility / SUNMobile";
                           }

                           if (plmnid == 45403) {
                              return "3 HK";
                           }
                        } else if (plmnid < 45412) {
                           if (plmnid == 45404) {
                              return "3 (2G)";
                           }

                           if (plmnid == 45406) {
                              return "SmarTone HK";
                           }
                        } else {
                           if (plmnid == 45412) {
                              return "CMCC HK";
                           }

                           if (plmnid == 45416) {
                              return "PCCW Mobile (2G)";
                           }

                           if (plmnid == 45420) {
                              return "PCCW Mobile (4G)";
                           }
                        }
                     } else if (plmnid < 45601) {
                        if (plmnid == 45500) {
                           return "SmarTone MO";
                        }

                        if (plmnid == 45501) {
                           return "CTM";
                        }

                        if (plmnid == 45505) {
                           return "3 MO";
                        }

                        if (plmnid == 45507) {
                           return "China Telecom MO";
                        }
                     } else if (plmnid < 45603) {
                        if (plmnid == 45601) {
                           return "Cellcard";
                        }

                        if (plmnid == 45602) {
                           return "Smart KH";
                        }
                     } else {
                        if (plmnid == 45603) {
                           return "qb";
                        }

                        if (plmnid == 45604) {
                           return "qb";
                        }

                        if (plmnid == 45605) {
                           return "Smart KH";
                        }
                     }
                  } else if (plmnid < 46697) {
                     if (plmnid < 45708) {
                        if (plmnid < 45618) {
                           if (plmnid == 45606) {
                              return "Smart KH";
                           }

                           if (plmnid == 45608) {
                              return "Metfone";
                           }

                           if (plmnid == 45609) {
                              return "Metfone";
                           }

                           if (plmnid == 45611) {
                              return "SEATEL";
                           }
                        } else {
                           if (plmnid == 45618) {
                              return "Cellcard";
                           }

                           if (plmnid == 45701) {
                              return "LaoTel";
                           }

                           if (plmnid == 45702) {
                              return "ETL";
                           }

                           if (plmnid == 45703) {
                              return "Unitel LA";
                           }
                        }
                     } else if (plmnid < 46020) {
                        if (plmnid == 45708) {
                           return "Beeline LA";
                        }

                        if (plmnid == 46000) {
                           return "China Mobile";
                        }

                        if (plmnid == 46001) {
                           return "China Unicom";
                        }

                        if (plmnid == 46003) {
                           return "China Telecom CN";
                        }
                     } else if (plmnid < 46605) {
                        if (plmnid == 46020) {
                           return "China Tietong";
                        }

                        if (plmnid == 46601) {
                           return "FarEasTone";
                        }
                     } else {
                        if (plmnid == 46605) {
                           return "APTG";
                        }

                        if (plmnid == 46689) {
                           return "T Star";
                        }

                        if (plmnid == 46692) {
                           return "Chunghwa";
                        }
                     }
                  } else if (plmnid < 50211) {
                     if (plmnid < 47004) {
                        if (plmnid == 46697) {
                           return "Taiwan Mobile";
                        }

                        if (plmnid == 47001) {
                           return "Grameenphone";
                        }

                        if (plmnid == 47002) {
                           return "Robi";
                        }

                        if (plmnid == 47003) {
                           return "Banglalink";
                        }
                     } else if (plmnid < 47009) {
                        if (plmnid == 47004) {
                           return "TeleTalk";
                        }

                        if (plmnid == 47007) {
                           return "Airtel BD";
                        }
                     } else {
                        if (plmnid == 47009) {
                           return "ollo";
                        }

                        if (plmnid == 47201) {
                           return "Dhiraagu";
                        }

                        if (plmnid == 47202) {
                           return "Ooredoo MV";
                        }
                     }
                  } else if (plmnid < 50217) {
                     if (plmnid == 50211) {
                        return "TM Homeline";
                     }

                     if (plmnid == 50212) {
                        return "Maxis";
                     }

                     if (plmnid == 50213) {
                        return "Celcom";
                     }

                     if (plmnid == 50216) {
                        return "DiGi";
                     }
                  } else if (plmnid < 50219) {
                     if (plmnid == 50217) {
                        return "Maxis";
                     }

                     if (plmnid == 50218) {
                        return "U Mobile";
                     }
                  } else {
                     if (plmnid == 50219) {
                        return "Celcom";
                     }

                     if (plmnid == 50501) {
                        return "Telstra";
                     }

                     if (plmnid == 50502) {
                        return "Optus";
                     }
                  }
               } else if (plmnid < 72402) {
                  if (plmnid < 62303) {
                     if (plmnid < 60501) {
                        if (plmnid < 53901) {
                           if (plmnid < 52001) {
                              if (plmnid < 51011) {
                                 if (plmnid < 50516) {
                                    if (plmnid == 50503) {
                                       return "Vodafone AU";
                                    }

                                    if (plmnid == 50510) {
                                       return "Norfolk Is.";
                                    }

                                    if (plmnid == 50510) {
                                       return "Norfolk Telecom";
                                    }

                                    if (plmnid == 50513) {
                                       return "RailCorp";
                                    }
                                 } else {
                                    if (plmnid == 50516) {
                                       return "VicTrack";
                                    }

                                    if (plmnid == 51001) {
                                       return "Indosat Ooredoo";
                                    }

                                    if (plmnid == 51009) {
                                       return "Smartfren";
                                    }

                                    if (plmnid == 51010) {
                                       return "Telkomsel";
                                    }
                                 }
                              } else if (plmnid < 51402) {
                                 if (plmnid == 51011) {
                                    return "XL";
                                 }

                                 if (plmnid == 51028) {
                                    return "Fren/Hepi";
                                 }

                                 if (plmnid == 51089) {
                                    return "3 ID";
                                 }

                                 if (plmnid == 51401) {
                                    return "Telkomcel";
                                 }
                              } else if (plmnid < 51502) {
                                 if (plmnid == 51402) {
                                    return "TT";
                                 }

                                 if (plmnid == 51403) {
                                    return "Telemor";
                                 }
                              } else {
                                 if (plmnid == 51502) {
                                    return "Globe";
                                 }

                                 if (plmnid == 51503) {
                                    return "SMART PH";
                                 }

                                 if (plmnid == 51505) {
                                    return "Sun Cellular";
                                 }
                              }
                           } else if (plmnid < 52505) {
                              if (plmnid < 52018) {
                                 if (plmnid == 52001) {
                                    return "AIS";
                                 }

                                 if (plmnid == 52003) {
                                    return "AIS";
                                 }

                                 if (plmnid == 52004) {
                                    return "TrueMove H";
                                 }

                                 if (plmnid == 52005) {
                                    return "dtac";
                                 }
                              } else {
                                 if (plmnid == 52018) {
                                    return "dtac";
                                 }

                                 if (plmnid == 52099) {
                                    return "TrueMove";
                                 }

                                 if (plmnid == 52501) {
                                    return "SingTel";
                                 }

                                 if (plmnid == 52503) {
                                    return "M1";
                                 }
                              }
                           } else if (plmnid < 53024) {
                              if (plmnid == 52505) {
                                 return "StarHub";
                              }

                              if (plmnid == 52811) {
                                 return "DST";
                              }

                              if (plmnid == 53001) {
                                 return "Vodafone NZ";
                              }

                              if (plmnid == 53005) {
                                 return "Spark";
                              }
                           } else if (plmnid < 53701) {
                              if (plmnid == 53024) {
                                 return "2degrees";
                              }

                              if (plmnid == 53602) {
                                 return "Digicel NR";
                              }
                           } else {
                              if (plmnid == 53701) {
                                 return "bmobile PG";
                              }

                              if (plmnid == 53702) {
                                 return "citifon";
                              }

                              if (plmnid == 53703) {
                                 return "Digicel PG";
                              }
                           }
                        } else if (plmnid < 54801) {
                           if (plmnid < 54202) {
                              if (plmnid < 54100) {
                                 if (plmnid == 53901) {
                                    return "U-Call";
                                 }

                                 if (plmnid == 53988) {
                                    return "Digicel TO";
                                 }

                                 if (plmnid == 54001) {
                                    return "BREEZE";
                                 }

                                 if (plmnid == 54002) {
                                    return "BeMobile";
                                 }
                              } else {
                                 if (plmnid == 54100) {
                                    return "AIL";
                                 }

                                 if (plmnid == 54101) {
                                    return "SMILE";
                                 }

                                 if (plmnid == 54105) {
                                    return "Digicel VU";
                                 }

                                 if (plmnid == 54201) {
                                    return "Vodafone FJ";
                                 }
                              }
                           } else if (plmnid < 54509) {
                              if (plmnid == 54202) {
                                 return "Digicel FJ";
                              }

                              if (plmnid == 54203) {
                                 return "TFL";
                              }

                              if (plmnid == 54411) {
                                 return "Bluesky AS";
                              }

                              if (plmnid == 54501) {
                                 return "Kiribati - ATH";
                              }
                           } else if (plmnid < 54705) {
                              if (plmnid == 54509) {
                                 return "Kiribati - Frigate Net";
                              }

                              if (plmnid == 54601) {
                                 return "Mobilis NC";
                              }
                           } else {
                              if (plmnid == 54705) {
                                 return "Ora";
                              }

                              if (plmnid == 54715) {
                                 return "Vodafone PF";
                              }

                              if (plmnid == 54720) {
                                 return "Vini";
                              }
                           }
                        } else if (plmnid < 60203) {
                           if (plmnid < 55202) {
                              if (plmnid == 54801) {
                                 return "Bluesky CK";
                              }

                              if (plmnid == 54901) {
                                 return "Digicel WS";
                              }

                              if (plmnid == 54927) {
                                 return "Bluesky WS";
                              }

                              if (plmnid == 55201) {
                                 return "PNCC";
                              }
                           } else if (plmnid < 55501) {
                              if (plmnid == 55202) {
                                 return "PT Waves";
                              }

                              if (plmnid == 55301) {
                                 return "TTC";
                              }
                           } else {
                              if (plmnid == 55501) {
                                 return "Telecom Niue";
                              }

                              if (plmnid == 60201) {
                                 return "Orange EG";
                              }

                              if (plmnid == 60202) {
                                 return "Vodafone EG";
                              }
                           }
                        } else if (plmnid < 60303) {
                           if (plmnid == 60203) {
                              return "Etisalat EG";
                           }

                           if (plmnid == 60204) {
                              return "WE";
                           }

                           if (plmnid == 60301) {
                              return "Mobilis DZ";
                           }

                           if (plmnid == 60302) {
                              return "Djezzy";
                           }
                        } else if (plmnid < 60401) {
                           if (plmnid == 60303) {
                              return "Ooredoo DZ";
                           }

                           if (plmnid == 60400) {
                              return "Orange Morocco";
                           }
                        } else {
                           if (plmnid == 60401) {
                              return "IAM";
                           }

                           if (plmnid == 60402) {
                              return "INWI";
                           }

                           if (plmnid == 60405) {
                              return "INWI";
                           }
                        }
                     } else if (plmnid < 61403) {
                        if (plmnid < 61002) {
                           if (plmnid < 60703) {
                              if (plmnid < 60601) {
                                 if (plmnid == 60501) {
                                    return "Orange TN";
                                 }

                                 if (plmnid == 60502) {
                                    return "Tunicell";
                                 }

                                 if (plmnid == 60503) {
                                    return "OOREDOO TN";
                                 }

                                 if (plmnid == 60600) {
                                    return "Libyana";
                                 }
                              } else {
                                 if (plmnid == 60601) {
                                    return "Madar";
                                 }

                                 if (plmnid == 60603) {
                                    return "Libya Phone";
                                 }

                                 if (plmnid == 60701) {
                                    return "Gamcel";
                                 }

                                 if (plmnid == 60702) {
                                    return "Africell GM";
                                 }
                              }
                           } else if (plmnid < 60803) {
                              if (plmnid == 60703) {
                                 return "Comium";
                              }

                              if (plmnid == 60704) {
                                 return "QCell";
                              }

                              if (plmnid == 60801) {
                                 return "Orange SN";
                              }

                              if (plmnid == 60802) {
                                 return "Free SN";
                              }
                           } else if (plmnid < 60902) {
                              if (plmnid == 60803) {
                                 return "Expresso";
                              }

                              if (plmnid == 60901) {
                                 return "Mattel";
                              }
                           } else {
                              if (plmnid == 60902) {
                                 return "Chinguitel";
                              }

                              if (plmnid == 60910) {
                                 return "Mauritel";
                              }

                              if (plmnid == 61001) {
                                 return "Malitel";
                              }
                           }
                        } else if (plmnid < 61204) {
                           if (plmnid < 61103) {
                              if (plmnid == 61002) {
                                 return "Orange ML";
                              }

                              if (plmnid == 61003) {
                                 return "Telecel ML";
                              }

                              if (plmnid == 61101) {
                                 return "Orange GN";
                              }

                              if (plmnid == 61102) {
                                 return "Sotelgui";
                              }
                           } else if (plmnid < 61105) {
                              if (plmnid == 61103) {
                                 return "Telecel Guinee";
                              }

                              if (plmnid == 61104) {
                                 return "MTN GN";
                              }
                           } else {
                              if (plmnid == 61105) {
                                 return "Cellcom GN";
                              }

                              if (plmnid == 61202) {
                                 return "Moov CI";
                              }

                              if (plmnid == 61203) {
                                 return "Orange CI";
                              }
                           }
                        } else if (plmnid < 61301) {
                           if (plmnid == 61204) {
                              return "KoZ";
                           }

                           if (plmnid == 61205) {
                              return "MTN CI";
                           }

                           if (plmnid == 61206) {
                              return "GreenN";
                           }

                           if (plmnid == 61207) {
                              return "café";
                           }
                        } else if (plmnid < 61303) {
                           if (plmnid == 61301) {
                              return "Telmob";
                           }

                           if (plmnid == 61302) {
                              return "Orange BF";
                           }
                        } else {
                           if (plmnid == 61303) {
                              return "Telecel Faso";
                           }

                           if (plmnid == 61401) {
                              return "SahelCom";
                           }

                           if (plmnid == 61402) {
                              return "Airtel NE";
                           }
                        }
                     } else if (plmnid < 61909) {
                        if (plmnid < 61701) {
                           if (plmnid < 61601) {
                              if (plmnid == 61403) {
                                 return "Moov NE";
                              }

                              if (plmnid == 61404) {
                                 return "Orange NE";
                              }

                              if (plmnid == 61501) {
                                 return "Togo Cell";
                              }

                              if (plmnid == 61503) {
                                 return "Moov TG";
                              }
                           } else {
                              if (plmnid == 61601) {
                                 return "Libercom";
                              }

                              if (plmnid == 61602) {
                                 return "Moov BJ";
                              }

                              if (plmnid == 61603) {
                                 return "MTN BJ";
                              }

                              if (plmnid == 61604) {
                                 return "BBCOM";
                              }
                           }
                        } else if (plmnid < 61804) {
                           if (plmnid == 61701) {
                              return "my.t";
                           }

                           if (plmnid == 61703) {
                              return "CHILI";
                           }

                           if (plmnid == 61710) {
                              return "Emtel";
                           }

                           if (plmnid == 61801) {
                              return "Lonestar Cell MTN";
                           }
                        } else if (plmnid < 61901) {
                           if (plmnid == 61804) {
                              return "Novafone";
                           }

                           if (plmnid == 61807) {
                              return "Orange LBR";
                           }
                        } else {
                           if (plmnid == 61901) {
                              return "Orange SL";
                           }

                           if (plmnid == 61903) {
                              return "Africell SL";
                           }

                           if (plmnid == 61905) {
                              return "Africell SL";
                           }
                        }
                     } else if (plmnid < 62130) {
                        if (plmnid < 62006) {
                           if (plmnid == 61909) {
                              return "Smart Mobile SL";
                           }

                           if (plmnid == 62001) {
                              return "MTN GH";
                           }

                           if (plmnid == 62002) {
                              return "Vodafone GH";
                           }

                           if (plmnid == 62003) {
                              return "AirtelTigo";
                           }
                        } else if (plmnid < 62120) {
                           if (plmnid == 62006) {
                              return "AirtelTigo";
                           }

                           if (plmnid == 62007) {
                              return "Globacom";
                           }
                        } else {
                           if (plmnid == 62120) {
                              return "Airtel NG";
                           }

                           if (plmnid == 62122) {
                              return "InterC";
                           }

                           if (plmnid == 62127) {
                              return "Smile NG";
                           }
                        }
                     } else if (plmnid < 62201) {
                        if (plmnid == 62130) {
                           return "MTN NG";
                        }

                        if (plmnid == 62140) {
                           return "Ntel";
                        }

                        if (plmnid == 62150) {
                           return "Glo";
                        }

                        if (plmnid == 62160) {
                           return "9mobile";
                        }
                     } else if (plmnid < 62207) {
                        if (plmnid == 62201) {
                           return "Airtel TD";
                        }

                        if (plmnid == 62203) {
                           return "Tigo TD";
                        }
                     } else {
                        if (plmnid == 62207) {
                           return "Salam";
                        }

                        if (plmnid == 62301) {
                           return "Moov CF";
                        }

                        if (plmnid == 62302) {
                           return "TC";
                        }
                     }
                  } else if (plmnid < 64201) {
                     if (plmnid < 63409) {
                        if (plmnid < 62910) {
                           if (plmnid < 62601) {
                              if (plmnid < 62403) {
                                 if (plmnid == 62303) {
                                    return "Orange CF";
                                 }

                                 if (plmnid == 62304) {
                                    return "Azur";
                                 }

                                 if (plmnid == 62401) {
                                    return "MTN Cameroon";
                                 }

                                 if (plmnid == 62402) {
                                    return "Orange CM";
                                 }
                              } else {
                                 if (plmnid == 62403) {
                                    return "Camtel";
                                 }

                                 if (plmnid == 62404) {
                                    return "Nexttel";
                                 }

                                 if (plmnid == 62501) {
                                    return "CVMOVEL";
                                 }

                                 if (plmnid == 62502) {
                                    return "T+";
                                 }
                              }
                           } else if (plmnid < 62801) {
                              if (plmnid == 62601) {
                                 return "CSTmovel";
                              }

                              if (plmnid == 62602) {
                                 return "Unitel STP";
                              }

                              if (plmnid == 62701) {
                                 return "Orange GQ";
                              }

                              if (plmnid == 62703) {
                                 return "Muni";
                              }
                           } else if (plmnid < 62803) {
                              if (plmnid == 62801) {
                                 return "Libertis";
                              }

                              if (plmnid == 62802) {
                                 return "Moov GA";
                              }
                           } else {
                              if (plmnid == 62803) {
                                 return "Airtel GA";
                              }

                              if (plmnid == 62901) {
                                 return "Airtel CG";
                              }

                              if (plmnid == 62907) {
                                 return "Airtel CG";
                              }
                           }
                        } else if (plmnid < 63201) {
                           if (plmnid < 63086) {
                              if (plmnid == 62910) {
                                 return "Libertis Telecom";
                              }

                              if (plmnid == 63001) {
                                 return "Vodacom CD";
                              }

                              if (plmnid == 63002) {
                                 return "Airtel CD";
                              }

                              if (plmnid == 63005) {
                                 return "Supercell";
                              }
                           } else if (plmnid < 63090) {
                              if (plmnid == 63086) {
                                 return "Orange RDC";
                              }

                              if (plmnid == 63089) {
                                 return "Orange RDC";
                              }
                           } else {
                              if (plmnid == 63090) {
                                 return "Africell CD";
                              }

                              if (plmnid == 63102) {
                                 return "UNITEL AO";
                              }

                              if (plmnid == 63104) {
                                 return "MOVICEL";
                              }
                           }
                        } else if (plmnid < 63301) {
                           if (plmnid == 63201) {
                              return "Guinetel";
                           }

                           if (plmnid == 63202) {
                              return "MTN Areeba";
                           }

                           if (plmnid == 63203) {
                              return "Orange GW";
                           }

                           if (plmnid == 63207) {
                              return "Guinetel";
                           }
                        } else if (plmnid < 63401) {
                           if (plmnid == 63301) {
                              return "Cable & Wireless SC";
                           }

                           if (plmnid == 63310) {
                              return "Airtel SC";
                           }
                        } else {
                           if (plmnid == 63401) {
                              return "Zain SD";
                           }

                           if (plmnid == 63402) {
                              return "MTN SD";
                           }

                           if (plmnid == 63407) {
                              return "Sudani One";
                           }
                        }
                     } else if (plmnid < 63902) {
                        if (plmnid < 63720) {
                           if (plmnid < 63601) {
                              if (plmnid == 63409) {
                                 return "khartoum INC";
                              }

                              if (plmnid == 63510) {
                                 return "MTN RW";
                              }

                              if (plmnid == 63513) {
                                 return "Airtel RW";
                              }

                              if (plmnid == 63517) {
                                 return "Olleh";
                              }
                           } else {
                              if (plmnid == 63601) {
                                 return "MTN ET";
                              }

                              if (plmnid == 63701) {
                                 return "Telesom";
                              }

                              if (plmnid == 63704) {
                                 return "Somafone";
                              }

                              if (plmnid == 63710) {
                                 return "Nationlink";
                              }
                           }
                        } else if (plmnid < 63760) {
                           if (plmnid == 63720) {
                              return "SOMNET";
                           }

                           if (plmnid == 63730) {
                              return "Golis";
                           }

                           if (plmnid == 63750) {
                              return "Hormuud";
                           }

                           if (plmnid == 63757) {
                              return "UNITEL SO";
                           }
                        } else if (plmnid < 63771) {
                           if (plmnid == 63760) {
                              return "Nationlink";
                           }

                           if (plmnid == 63767) {
                              return "Horntel Group";
                           }
                        } else {
                           if (plmnid == 63771) {
                              return "Somtel";
                           }

                           if (plmnid == 63782) {
                              return "Telcom";
                           }

                           if (plmnid == 63801) {
                              return "Evatis";
                           }
                        }
                     } else if (plmnid < 64009) {
                        if (plmnid < 64002) {
                           if (plmnid == 63902) {
                              return "Safaricom";
                           }

                           if (plmnid == 63903) {
                              return "Airtel KE";
                           }

                           if (plmnid == 63907) {
                              return "Telkom KE";
                           }

                           if (plmnid == 63910) {
                              return "Faiba 4G";
                           }
                        } else if (plmnid < 64004) {
                           if (plmnid == 64002) {
                              return "tiGO";
                           }

                           if (plmnid == 64003) {
                              return "Zantel";
                           }
                        } else {
                           if (plmnid == 64004) {
                              return "Vodacom TZ";
                           }

                           if (plmnid == 64005) {
                              return "Airtel TZ";
                           }

                           if (plmnid == 64007) {
                              return "TTCL Mobile";
                           }
                        }
                     } else if (plmnid < 64111) {
                        if (plmnid == 64009) {
                           return "Halotel";
                        }

                        if (plmnid == 64011) {
                           return "SmileCom";
                        }

                        if (plmnid == 64101) {
                           return "Airtel UG";
                        }

                        if (plmnid == 64110) {
                           return "MTN UG";
                        }
                     } else if (plmnid < 64118) {
                        if (plmnid == 64111) {
                           return "Uganda Telecom";
                        }

                        if (plmnid == 64114) {
                           return "Africell UG";
                        }
                     } else {
                        if (plmnid == 64118) {
                           return "Smart UG";
                        }

                        if (plmnid == 64122) {
                           return "Airtel UG";
                        }

                        if (plmnid == 64133) {
                           return "Smile UG";
                        }
                     }
                  } else if (plmnid < 65501) {
                     if (plmnid < 64703) {
                        if (plmnid < 64501) {
                           if (plmnid < 64282) {
                              if (plmnid == 64201) {
                                 return "econet Leo";
                              }

                              if (plmnid == 64203) {
                                 return "Onatel";
                              }

                              if (plmnid == 64207) {
                                 return "Smart Mobile BI";
                              }

                              if (plmnid == 64208) {
                                 return "Lumitel";
                              }
                           } else {
                              if (plmnid == 64282) {
                                 return "econet Leo";
                              }

                              if (plmnid == 64301) {
                                 return "mCel";
                              }

                              if (plmnid == 64303) {
                                 return "Movitel";
                              }

                              if (plmnid == 64304) {
                                 return "Vodacom MZ";
                              }
                           }
                        } else if (plmnid < 64602) {
                           if (plmnid == 64501) {
                              return "Airtel ZM";
                           }

                           if (plmnid == 64502) {
                              return "MTN ZM";
                           }

                           if (plmnid == 64503) {
                              return "ZAMTEL";
                           }

                           if (plmnid == 64601) {
                              return "Airtel MG";
                           }
                        } else if (plmnid < 64700) {
                           if (plmnid == 64602) {
                              return "Orange MG";
                           }

                           if (plmnid == 64604) {
                              return "Telma";
                           }
                        } else {
                           if (plmnid == 64700) {
                              return "Orange YT/RE";
                           }

                           if (plmnid == 64701) {
                              return "Maoré Mobile";
                           }

                           if (plmnid == 64702) {
                              return "Only";
                           }
                        }
                     } else if (plmnid < 65010) {
                        if (plmnid < 64804) {
                           if (plmnid == 64703) {
                              return "Free YT/RE";
                           }

                           if (plmnid == 64710) {
                              return "SFR Réunion";
                           }

                           if (plmnid == 64801) {
                              return "Net*One";
                           }

                           if (plmnid == 64803) {
                              return "Telecel ZW";
                           }
                        } else if (plmnid < 64903) {
                           if (plmnid == 64804) {
                              return "Econet";
                           }

                           if (plmnid == 64901) {
                              return "MTC";
                           }
                        } else {
                           if (plmnid == 64903) {
                              return "TN Mobile";
                           }

                           if (plmnid == 65001) {
                              return "TNM";
                           }

                           if (plmnid == 65002) {
                              return "Access";
                           }
                        }
                     } else if (plmnid < 65202) {
                        if (plmnid == 65010) {
                           return "Airtel MW";
                        }

                        if (plmnid == 65101) {
                           return "Vodacom LS";
                        }

                        if (plmnid == 65102) {
                           return "Econet Telecom";
                        }

                        if (plmnid == 65201) {
                           return "Mascom";
                        }
                     } else if (plmnid < 65310) {
                        if (plmnid == 65202) {
                           return "Orange BW";
                        }

                        if (plmnid == 65204) {
                           return "beMobile";
                        }
                     } else {
                        if (plmnid == 65310) {
                           return "Swazi MTN";
                        }

                        if (plmnid == 65401) {
                           return "HURI";
                        }

                        if (plmnid == 65402) {
                           return "TELCO SA";
                        }
                     }
                  } else if (plmnid < 70602) {
                     if (plmnid < 65902) {
                        if (plmnid < 65514) {
                           if (plmnid == 65501) {
                              return "Vodacom ZA";
                           }

                           if (plmnid == 65502) {
                              return "Telkom ZA";
                           }

                           if (plmnid == 65507) {
                              return "Cell C";
                           }

                           if (plmnid == 65510) {
                              return "MTN ZA";
                           }
                        } else {
                           if (plmnid == 65514) {
                              return "Neotel";
                           }

                           if (plmnid == 65519) {
                              return "Rain";
                           }

                           if (plmnid == 65701) {
                              return "Eritel";
                           }

                           if (plmnid == 65801) {
                              return "Sure SH";
                           }
                        }
                     } else if (plmnid < 70269) {
                        if (plmnid == 65902) {
                           return "MTN SS";
                        }

                        if (plmnid == 65903) {
                           return "Gemtel";
                        }

                        if (plmnid == 65906) {
                           return "Zain SS";
                        }

                        if (plmnid == 70267) {
                           return "DigiCell";
                        }
                     } else if (plmnid < 70402) {
                        if (plmnid == 70269) {
                           return "SMART BZ";
                        }

                        if (plmnid == 70401) {
                           return "Claro GT";
                        }
                     } else {
                        if (plmnid == 70402) {
                           return "Tigo GT";
                        }

                        if (plmnid == 70403) {
                           return "movistar GT";
                        }

                        if (plmnid == 70601) {
                           return "Claro SV";
                        }
                     }
                  } else if (plmnid < 71204) {
                     if (plmnid < 71030) {
                        if (plmnid == 70602) {
                           return "Digicel SV";
                        }

                        if (plmnid == 70603) {
                           return "Tigo SV";
                        }

                        if (plmnid == 70604) {
                           return "movistar SV";
                        }

                        if (plmnid == 71021) {
                           return "Claro NI";
                        }
                     } else if (plmnid < 71201) {
                        if (plmnid == 71030) {
                           return "movistar NI";
                        }

                        if (plmnid == 71073) {
                           return "Claro NI";
                        }
                     } else {
                        if (plmnid == 71201) {
                           return "Kölbi ICE";
                        }

                        if (plmnid == 71202) {
                           return "Kölbi ICE";
                        }

                        if (plmnid == 71203) {
                           return "Claro CR";
                        }
                     }
                  } else if (plmnid < 71404) {
                     if (plmnid == 71204) {
                        return "movistar CR";
                     }

                     if (plmnid == 71401) {
                        return "Cable & Wireless PA";
                     }

                     if (plmnid == 71402) {
                        return "movistar PA";
                     }

                     if (plmnid == 71403) {
                        return "Claro PA";
                     }
                  } else if (plmnid < 71610) {
                     if (plmnid == 71404) {
                        return "Digicel PA";
                     }

                     if (plmnid == 71606) {
                        return "Movistar PE";
                     }
                  } else {
                     if (plmnid == 71610) {
                        return "Claro PE";
                     }

                     if (plmnid == 71615) {
                        return "Bitel";
                     }

                     if (plmnid == 71617) {
                        return "Entel PE";
                     }
                  }
               } else if (plmnid < 312330) {
                  if (plmnid < 310080) {
                     if (plmnid < 74602) {
                        if (plmnid < 73009) {
                           if (plmnid < 72423) {
                              if (plmnid < 72406) {
                                 if (plmnid == 72402) {
                                    return "TIM BR";
                                 }

                                 if (plmnid == 72403) {
                                    return "TIM BR";
                                 }

                                 if (plmnid == 72404) {
                                    return "TIM BR";
                                 }

                                 if (plmnid == 72405) {
                                    return "Claro BR";
                                 }
                              } else {
                                 if (plmnid == 72406) {
                                    return "Vivo";
                                 }

                                 if (plmnid == 72410) {
                                    return "Vivo";
                                 }

                                 if (plmnid == 72411) {
                                    return "Vivo";
                                 }

                                 if (plmnid == 72415) {
                                    return "Sercomtel";
                                 }
                              }
                           } else if (plmnid < 72434) {
                              if (plmnid == 72423) {
                                 return "Vivo";
                              }

                              if (plmnid == 72431) {
                                 return "Oi";
                              }

                              if (plmnid == 72432) {
                                 return "Algar Telecom";
                              }

                              if (plmnid == 72433) {
                                 return "Algar Telecom";
                              }
                           } else if (plmnid < 73001) {
                              if (plmnid == 72434) {
                                 return "Algar Telecom";
                              }

                              if (plmnid == 72439) {
                                 return "Nextel";
                              }
                           } else {
                              if (plmnid == 73001) {
                                 return "entel";
                              }

                              if (plmnid == 73002) {
                                 return "movistar CL";
                              }

                              if (plmnid == 73003) {
                                 return "CLARO CL";
                              }
                           }
                        } else if (plmnid < 73801) {
                           if (plmnid < 73404) {
                              if (plmnid == 73009) {
                                 return "WOM";
                              }

                              if (plmnid == 73010) {
                                 return "entel";
                              }

                              if (plmnid == 73099) {
                                 return "Will";
                              }

                              if (plmnid == 73402) {
                                 return "Digitel GSM";
                              }
                           } else if (plmnid < 73601) {
                              if (plmnid == 73404) {
                                 return "movistar VE";
                              }

                              if (plmnid == 73406) {
                                 return "Movilnet";
                              }
                           } else {
                              if (plmnid == 73601) {
                                 return "Viva BO";
                              }

                              if (plmnid == 73602) {
                                 return "Entel BO";
                              }

                              if (plmnid == 73603) {
                                 return "Tigo BO";
                              }
                           }
                        } else if (plmnid < 74401) {
                           if (plmnid == 73801) {
                              return "Digicel GY";
                           }

                           if (plmnid == 74000) {
                              return "Movistar EC";
                           }

                           if (plmnid == 74001) {
                              return "Claro EC";
                           }

                           if (plmnid == 74002) {
                              return "CNT Mobile";
                           }
                        } else if (plmnid < 74404) {
                           if (plmnid == 74401) {
                              return "VOX";
                           }

                           if (plmnid == 74402) {
                              return "Claro PY";
                           }
                        } else {
                           if (plmnid == 74404) {
                              return "Tigo PY";
                           }

                           if (plmnid == 74405) {
                              return "Personal PY";
                           }

                           if (plmnid == 74406) {
                              return "Copaco";
                           }
                        }
                     } else if (plmnid < 302270) {
                        if (plmnid < 90115) {
                           if (plmnid < 74810) {
                              if (plmnid == 74602) {
                                 return "Telesur";
                              }

                              if (plmnid == 74603) {
                                 return "Digicel SR";
                              }

                              if (plmnid == 74801) {
                                 return "Antel";
                              }

                              if (plmnid == 74807) {
                                 return "Movistar UY";
                              }
                           } else {
                              if (plmnid == 74810) {
                                 return "Claro UY";
                              }

                              if (plmnid == 90112) {
                                 return "Telenor INTL";
                              }

                              if (plmnid == 90113) {
                                 return "GSM.AQ";
                              }

                              if (plmnid == 90114) {
                                 return "AeroMobile";
                              }
                           }
                        } else if (plmnid < 90127) {
                           if (plmnid == 90115) {
                              return "OnAir";
                           }

                           if (plmnid == 90118) {
                              return "Cellular @Sea";
                           }

                           if (plmnid == 90119) {
                              return "Vodafone Malta Maritime";
                           }

                           if (plmnid == 90126) {
                              return "TIM@sea";
                           }
                        } else if (plmnid < 90132) {
                           if (plmnid == 90127) {
                              return "OnMarine";
                           }

                           if (plmnid == 90131) {
                              return "Orange INTL";
                           }
                        } else {
                           if (plmnid == 90132) {
                              return "Sky High";
                           }

                           if (plmnid == 99501) {
                              return "FonePlus";
                           }

                           if (plmnid == 302220) {
                              return "Telus Mobility, Koodo Mobile, Public Mobile";
                           }
                        }
                     } else if (plmnid < 302720) {
                        if (plmnid < 302510) {
                           if (plmnid == 302270) {
                              return "EastLink";
                           }

                           if (plmnid == 302480) {
                              return "Qiniq";
                           }

                           if (plmnid == 302490) {
                              return "Freedom Mobile";
                           }

                           if (plmnid == 302500) {
                              return "Videotron";
                           }
                        } else if (plmnid < 302610) {
                           if (plmnid == 302510) {
                              return "Videotron";
                           }

                           if (plmnid == 302530) {
                              return "Keewaytinook Mobile";
                           }
                        } else {
                           if (plmnid == 302610) {
                              return "Bell Mobility";
                           }

                           if (plmnid == 302620) {
                              return "ICE Wireless";
                           }

                           if (plmnid == 302660) {
                              return "MTS CA";
                           }
                        }
                     } else if (plmnid < 310030) {
                        if (plmnid == 302720) {
                           return "Rogers Wireless";
                        }

                        if (plmnid == 302780) {
                           return "SaskTel";
                        }

                        if (plmnid == 310012) {
                           return "Verizon";
                        }

                        if (plmnid == 310020) {
                           return "Union Wireless";
                        }
                     } else if (plmnid < 310032) {
                        if (plmnid == 310030) {
                           return "AT&T US";
                        }

                        if (plmnid == 310032) {
                           return "IT&E Wireless GU";
                        }
                     } else {
                        if (plmnid == 310032) {
                           return "IT&E Wireless US";
                        }

                        if (plmnid == 310066) {
                           return "U.S. Cellular";
                        }

                        if (plmnid == 310070) {
                           return "AT&T US";
                        }
                     }
                  } else if (plmnid < 311080) {
                     if (plmnid < 310370) {
                        if (plmnid < 310150) {
                           if (plmnid < 310110) {
                              if (plmnid == 310080) {
                                 return "AT&T US";
                              }

                              if (plmnid == 310090) {
                                 return "AT&T US";
                              }

                              if (plmnid == 310100) {
                                 return "Plateau Wireless";
                              }

                              if (plmnid == 310110) {
                                 return "IT&E Wireless MP";
                              }
                           } else {
                              if (plmnid == 310110) {
                                 return "IT&E Wireless US";
                              }

                              if (plmnid == 310120) {
                                 return "Sprint";
                              }

                              if (plmnid == 310140) {
                                 return "GTA Wireless GU";
                              }

                              if (plmnid == 310140) {
                                 return "GTA Wireless US";
                              }
                           }
                        } else if (plmnid < 310190) {
                           if (plmnid == 310150) {
                              return "AT&T US";
                           }

                           if (plmnid == 310160) {
                              return "T-Mobile US";
                           }

                           if (plmnid == 310170) {
                              return "AT&T US";
                           }

                           if (plmnid == 310180) {
                              return "West Central";
                           }
                        } else if (plmnid < 310320) {
                           if (plmnid == 310190) {
                              return "GCI";
                           }

                           if (plmnid == 310260) {
                              return "T-Mobile US";
                           }
                        } else {
                           if (plmnid == 310320) {
                              return "Cellular One";
                           }

                           if (plmnid == 310370) {
                              return "Docomo GU";
                           }

                           if (plmnid == 310370) {
                              return "Docomo MP";
                           }
                        }
                     } else if (plmnid < 310740) {
                        if (plmnid < 310410) {
                           if (plmnid == 310370) {
                              return "Docomo US";
                           }

                           if (plmnid == 310390) {
                              return "Cellular One of East Texas";
                           }

                           if (plmnid == 310400) {
                              return "iConnect GU";
                           }

                           if (plmnid == 310400) {
                              return "iConnect US";
                           }
                        } else if (plmnid < 310450) {
                           if (plmnid == 310410) {
                              return "AT&T US";
                           }

                           if (plmnid == 310430) {
                              return "GCI";
                           }
                        } else {
                           if (plmnid == 310450) {
                              return "Viaero";
                           }

                           if (plmnid == 310540) {
                              return "Phoenix US";
                           }

                           if (plmnid == 310680) {
                              return "AT&T US";
                           }
                        }
                     } else if (plmnid < 310990) {
                        if (plmnid == 310740) {
                           return "Viaero";
                        }

                        if (plmnid == 310770) {
                           return "iWireless";
                        }

                        if (plmnid == 310790) {
                           return "BLAZE";
                        }

                        if (plmnid == 310950) {
                           return "AT&T US";
                        }
                     } else if (plmnid < 311030) {
                        if (plmnid == 310990) {
                           return "Evolve Broadband";
                        }

                        if (plmnid == 311020) {
                           return "Chariton Valley";
                        }
                     } else {
                        if (plmnid == 311030) {
                           return "Indigo Wireless";
                        }

                        if (plmnid == 311040) {
                           return "Choice Wireless";
                        }

                        if (plmnid == 311070) {
                           return "AT&T US";
                        }
                     }
                  } else if (plmnid < 311670) {
                     if (plmnid < 311470) {
                        if (plmnid < 311320) {
                           if (plmnid == 311080) {
                              return "Pine Cellular";
                           }

                           if (plmnid == 311090) {
                              return "AT&T US";
                           }

                           if (plmnid == 311230) {
                              return "C Spire Wireless";
                           }

                           if (plmnid == 311290) {
                              return "BLAZE";
                           }
                        } else {
                           if (plmnid == 311320) {
                              return "Choice Wireless";
                           }

                           if (plmnid == 311330) {
                              return "Bug Tussel Wireless";
                           }

                           if (plmnid == 311370) {
                              return "GCI Wireless";
                           }

                           if (plmnid == 311450) {
                              return "PTCI";
                           }
                        }
                     } else if (plmnid < 311550) {
                        if (plmnid == 311470) {
                           return "Viya";
                        }

                        if (plmnid == 311480) {
                           return "Verizon";
                        }

                        if (plmnid == 311490) {
                           return "Sprint";
                        }

                        if (plmnid == 311530) {
                           return "NewCore";
                        }
                     } else if (plmnid < 311580) {
                        if (plmnid == 311550) {
                           return "Choice Wireless";
                        }

                        if (plmnid == 311560) {
                           return "OTZ Cellular";
                        }
                     } else {
                        if (plmnid == 311580) {
                           return "U.S. Cellular";
                        }

                        if (plmnid == 311640) {
                           return "Rock Wireless";
                        }

                        if (plmnid == 311650) {
                           return "United Wireless";
                        }
                     }
                  } else if (plmnid < 312120) {
                     if (plmnid < 311850) {
                        if (plmnid == 311670) {
                           return "Pine Belt Wireless";
                        }

                        if (plmnid == 311780) {
                           return "ASTCA US";
                        }

                        if (plmnid == 311780) {
                           return "ASTCA AS";
                        }

                        if (plmnid == 311840) {
                           return "Cellcom US";
                        }
                     } else if (plmnid < 311950) {
                        if (plmnid == 311850) {
                           return "Cellcom US";
                        }

                        if (plmnid == 311860) {
                           return "STRATA";
                        }
                     } else {
                        if (plmnid == 311950) {
                           return "ETC";
                        }

                        if (plmnid == 311970) {
                           return "Big River Broadband";
                        }

                        if (plmnid == 312030) {
                           return "Bravado Wireless";
                        }
                     }
                  } else if (plmnid < 312170) {
                     if (plmnid == 312120) {
                        return "Appalachian Wireless";
                     }

                     if (plmnid == 312130) {
                        return "Appalachian Wireless";
                     }

                     if (plmnid == 312150) {
                        return "NorthwestCell";
                     }

                     if (plmnid == 312160) {
                        return "Chat Mobility";
                     }
                  } else if (plmnid < 312260) {
                     if (plmnid == 312170) {
                        return "Chat Mobility";
                     }

                     if (plmnid == 312220) {
                        return "Chariton Valley";
                     }
                  } else {
                     if (plmnid == 312260) {
                        return "NewCore";
                     }

                     if (plmnid == 312270) {
                        return "Pioneer Cellular";
                     }

                     if (plmnid == 312280) {
                        return "Pioneer Cellular";
                     }
                  }
               } else if (plmnid < 405803) {
                  if (plmnid < 360110) {
                     if (plmnid < 338180) {
                        if (plmnid < 330120) {
                           if (plmnid < 312860) {
                              if (plmnid == 312330) {
                                 return "Nemont";
                              }

                              if (plmnid == 312400) {
                                 return "Mid-Rivers Wireless";
                              }

                              if (plmnid == 312470) {
                                 return "Carolina West Wireless";
                              }

                              if (plmnid == 312720) {
                                 return "Southern LINC";
                              }
                           } else {
                              if (plmnid == 312860) {
                                 return "ClearTalk";
                              }

                              if (plmnid == 312900) {
                                 return "ClearTalk";
                              }

                              if (plmnid == 313100) {
                                 return "FirstNet";
                              }

                              if (plmnid == 330110) {
                                 return "Claro Puerto Rico";
                              }
                           }
                        } else if (plmnid < 334140) {
                           if (plmnid == 330120) {
                              return "Open Mobile";
                           }

                           if (plmnid == 334020) {
                              return "Telcel";
                           }

                           if (plmnid == 334050) {
                              return "AT&T / Unefon";
                           }

                           if (plmnid == 334090) {
                              return "AT&T MX";
                           }
                        } else if (plmnid < 338050) {
                           if (plmnid == 334140) {
                              return "Altan Redes";
                           }

                           if (plmnid == 338050) {
                              return "Digicel JM";
                           }
                        } else {
                           if (plmnid == 338050) {
                              return "Digicel LC";
                           }

                           if (plmnid == 338050) {
                              return "Digicel TC";
                           }

                           if (plmnid == 338050) {
                              return "Digicel Bermuda";
                           }
                        }
                     } else if (plmnid < 348570) {
                        if (plmnid < 344050) {
                           if (plmnid == 338180) {
                              return "FLOW JM";
                           }

                           if (plmnid == 342600) {
                              return "FLOW BB";
                           }

                           if (plmnid == 342750) {
                              return "Digicel BB";
                           }

                           if (plmnid == 344030) {
                              return "APUA";
                           }
                        } else if (plmnid < 346050) {
                           if (plmnid == 344050) {
                              return "Digicel AG";
                           }

                           if (plmnid == 344920) {
                              return "FLOW AG";
                           }
                        } else {
                           if (plmnid == 346050) {
                              return "Digicel KY";
                           }

                           if (plmnid == 346140) {
                              return "FLOW KY";
                           }

                           if (plmnid == 348170) {
                              return "FLOW VG";
                           }
                        }
                     } else if (plmnid < 354860) {
                        if (plmnid == 348570) {
                           return "CCT Boatphone";
                        }

                        if (plmnid == 348770) {
                           return "Digicel VG";
                        }

                        if (plmnid == 352030) {
                           return "Digicel GD";
                        }

                        if (plmnid == 352110) {
                           return "FLOW GD";
                        }
                     } else if (plmnid < 356110) {
                        if (plmnid == 354860) {
                           return "FLOW MS";
                        }

                        if (plmnid == 356050) {
                           return "Digicel KN";
                        }
                     } else {
                        if (plmnid == 356110) {
                           return "FLOW KN";
                        }

                        if (plmnid == 358110) {
                           return "FLOW LC";
                        }

                        if (plmnid == 360050) {
                           return "Digicel VC";
                        }
                     }
                  } else if (plmnid < 405039) {
                     if (plmnid < 405028) {
                        if (plmnid < 374130) {
                           if (plmnid == 360110) {
                              return "FLOW VC";
                           }

                           if (plmnid == 365840) {
                              return "FLOW AI";
                           }

                           if (plmnid == 366020) {
                              return "Digicel DM";
                           }

                           if (plmnid == 366110) {
                              return "FLOW DM";
                           }
                        } else {
                           if (plmnid == 374130) {
                              return "Digicel TT";
                           }

                           if (plmnid == 376350) {
                              return "FLOW TC";
                           }

                           if (plmnid == 405025) {
                              return "TATA DOCOMO";
                           }

                           if (plmnid == 405027) {
                              return "TATA DOCOMO";
                           }
                        }
                     } else if (plmnid < 405034) {
                        if (plmnid == 405028) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405030) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405031) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405032) {
                           return "TATA DOCOMO";
                        }
                     } else if (plmnid < 405036) {
                        if (plmnid == 405034) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405035) {
                           return "TATA DOCOMO";
                        }
                     } else {
                        if (plmnid == 405036) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405037) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405038) {
                           return "TATA DOCOMO";
                        }
                     }
                  } else if (plmnid < 405751) {
                     if (plmnid < 405044) {
                        if (plmnid == 405039) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405041) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405042) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405043) {
                           return "TATA DOCOMO";
                        }
                     } else if (plmnid < 405046) {
                        if (plmnid == 405044) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405045) {
                           return "TATA DOCOMO";
                        }
                     } else {
                        if (plmnid == 405046) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405047) {
                           return "TATA DOCOMO";
                        }

                        if (plmnid == 405750) {
                           return "Vodafone India";
                        }
                     }
                  } else if (plmnid < 405755) {
                     if (plmnid == 405751) {
                        return "Vodafone India";
                     }

                     if (plmnid == 405752) {
                        return "Vodafone India";
                     }

                     if (plmnid == 405753) {
                        return "Vodafone India";
                     }

                     if (plmnid == 405754) {
                        return "Vodafone India";
                     }
                  } else if (plmnid < 405799) {
                     if (plmnid == 405755) {
                        return "Vodafone India";
                     }

                     if (plmnid == 405756) {
                        return "Vodafone India";
                     }
                  } else {
                     if (plmnid == 405799) {
                        return "IDEA";
                     }

                     if (plmnid == 405800) {
                        return "AIRCEL";
                     }

                     if (plmnid == 405801) {
                        return "AIRCEL";
                     }
                  }
               } else if (plmnid < 405866) {
                  if (plmnid < 405848) {
                     if (plmnid < 405819) {
                        if (plmnid < 405809) {
                           if (plmnid == 405803) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405804) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405805) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405806) {
                              return "AIRCEL";
                           }
                        } else {
                           if (plmnid == 405809) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405810) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405811) {
                              return "AIRCEL";
                           }

                           if (plmnid == 405818) {
                              return "Uninor";
                           }
                        }
                     } else if (plmnid < 405827) {
                        if (plmnid == 405819) {
                           return "Uninor";
                        }

                        if (plmnid == 405820) {
                           return "Uninor";
                        }

                        if (plmnid == 405821) {
                           return "Uninor";
                        }

                        if (plmnid == 405822) {
                           return "Uninor";
                        }
                     } else if (plmnid < 405845) {
                        if (plmnid == 405827) {
                           return "Videocon Datacom";
                        }

                        if (plmnid == 405840) {
                           return "Jio";
                        }
                     } else {
                        if (plmnid == 405845) {
                           return "IDEA";
                        }

                        if (plmnid == 405846) {
                           return "IDEA";
                        }

                        if (plmnid == 405847) {
                           return "IDEA";
                        }
                     }
                  } else if (plmnid < 405857) {
                     if (plmnid < 405852) {
                        if (plmnid == 405848) {
                           return "IDEA";
                        }

                        if (plmnid == 405849) {
                           return "IDEA";
                        }

                        if (plmnid == 405850) {
                           return "IDEA";
                        }

                        if (plmnid == 405851) {
                           return "IDEA";
                        }
                     } else if (plmnid < 405854) {
                        if (plmnid == 405852) {
                           return "IDEA";
                        }

                        if (plmnid == 405853) {
                           return "IDEA";
                        }
                     } else {
                        if (plmnid == 405854) {
                           return "Jio";
                        }

                        if (plmnid == 405855) {
                           return "Jio";
                        }

                        if (plmnid == 405856) {
                           return "Jio";
                        }
                     }
                  } else if (plmnid < 405861) {
                     if (plmnid == 405857) {
                        return "Jio";
                     }

                     if (plmnid == 405858) {
                        return "Jio";
                     }

                     if (plmnid == 405859) {
                        return "Jio";
                     }

                     if (plmnid == 405860) {
                        return "Jio";
                     }
                  } else if (plmnid < 405863) {
                     if (plmnid == 405861) {
                        return "Jio";
                     }

                     if (plmnid == 405862) {
                        return "Jio";
                     }
                  } else {
                     if (plmnid == 405863) {
                        return "Jio";
                     }

                     if (plmnid == 405864) {
                        return "Jio";
                     }

                     if (plmnid == 405865) {
                        return "Jio";
                     }
                  }
               } else if (plmnid < 708002) {
                  if (plmnid < 405874) {
                     if (plmnid < 405870) {
                        if (plmnid == 405866) {
                           return "Jio";
                        }

                        if (plmnid == 405867) {
                           return "Jio";
                        }

                        if (plmnid == 405868) {
                           return "Jio";
                        }

                        if (plmnid == 405869) {
                           return "Jio";
                        }
                     } else {
                        if (plmnid == 405870) {
                           return "Jio";
                        }

                        if (plmnid == 405871) {
                           return "Jio";
                        }

                        if (plmnid == 405872) {
                           return "Jio";
                        }

                        if (plmnid == 405873) {
                           return "Jio";
                        }
                     }
                  } else if (plmnid < 405910) {
                     if (plmnid == 405874) {
                        return "Jio";
                     }

                     if (plmnid == 405880) {
                        return "Uninor";
                     }

                     if (plmnid == 405908) {
                        return "IDEA";
                     }

                     if (plmnid == 405909) {
                        return "IDEA";
                     }
                  } else if (plmnid < 405929) {
                     if (plmnid == 405910) {
                        return "IDEA";
                     }

                     if (plmnid == 405911) {
                        return "IDEA";
                     }
                  } else {
                     if (plmnid == 405929) {
                        return "Uninor";
                     }

                     if (plmnid == 502153) {
                        return "unifi";
                     }

                     if (plmnid == 708001) {
                        return "Claro HN";
                     }
                  }
               } else if (plmnid < 732099) {
                  if (plmnid < 722070) {
                     if (plmnid == 708002) {
                        return "Tigo HN";
                     }

                     if (plmnid == 708030) {
                        return "Hondutel";
                     }

                     if (plmnid == 714020) {
                        return "movistar PA";
                     }

                     if (plmnid == 722010) {
                        return "Movistar AR";
                     }
                  } else if (plmnid < 722320) {
                     if (plmnid == 722070) {
                        return "Movistar AR";
                     }

                     if (plmnid == 722310) {
                        return "Claro AR";
                     }
                  } else {
                     if (plmnid == 722320) {
                        return "Claro AR";
                     }

                     if (plmnid == 722330) {
                        return "Claro AR";
                     }

                     if (plmnid == 722341) {
                        return "Personal AR";
                     }
                  }
               } else if (plmnid < 732123) {
                  if (plmnid == 732099) {
                     return "EMCALI";
                  }

                  if (plmnid == 732101) {
                     return "Claro CO";
                  }

                  if (plmnid == 732103) {
                     return "Tigo CO";
                  }

                  if (plmnid == 732111) {
                     return "Tigo CO";
                  }
               } else if (plmnid < 732187) {
                  if (plmnid == 732123) {
                     return "movistar CO";
                  }

                  if (plmnid == 732130) {
                     return "AVANTEL";
                  }
               } else {
                  if (plmnid == 732187) {
                     return "eTb";
                  }

                  if (plmnid == 738002) {
                     return "GT&T Cellink Plus";
                  }

                  if (plmnid == 750001) {
                     return "Sure FK";
                  }
               }

               return mccmnc;
            }
         }
      }
   }

   public String decodePLMN(String mccmnc) {
      return this.imm_decodePLMN(mccmnc);
   }

   public YCellular nextCellular() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindCellularInContext(this._yapi, next_hwid);
   }

   public static YCellular FirstCellular() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Cellular");
         return next_hwid == null ? null : FindCellularInContext(yctx, next_hwid);
      }
   }

   public static YCellular FirstCellularInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Cellular");
      return next_hwid == null ? null : FindCellularInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YCellular var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YCellular var1, String var2);
   }
}
