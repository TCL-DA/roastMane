package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YBuzzer extends YFunction {
   public static final double FREQUENCY_INVALID = -1.79769313486231E308;
   public static final int VOLUME_INVALID = -1;
   public static final int PLAYSEQSIZE_INVALID = -1;
   public static final int PLAYSEQMAXSIZE_INVALID = -1;
   public static final int PLAYSEQSIGNATURE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected double _frequency = -1.79769313486231E308;
   protected int _volume = -1;
   protected int _playSeqSize = -1;
   protected int _playSeqMaxSize = -1;
   protected int _playSeqSignature = -1;
   protected String _command = "!INVALID!";
   protected YBuzzer.UpdateCallback _valueCallbackBuzzer = null;

   protected YBuzzer(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Buzzer";
   }

   protected YBuzzer(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("frequency")) {
         this._frequency = (double)Math.round(json_val.getDouble("frequency") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("volume")) {
         this._volume = json_val.getInt("volume");
      }

      if (json_val.has("playSeqSize")) {
         this._playSeqSize = json_val.getInt("playSeqSize");
      }

      if (json_val.has("playSeqMaxSize")) {
         this._playSeqMaxSize = json_val.getInt("playSeqMaxSize");
      }

      if (json_val.has("playSeqSignature")) {
         this._playSeqSignature = json_val.getInt("playSeqSignature");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int set_frequency(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("frequency", rest_val);
         return 0;
      }
   }

   public int setFrequency(double newval) throws YAPI_Exception {
      return this.set_frequency(newval);
   }

   public double get_frequency() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._frequency;
      }
   }

   public double getFrequency() throws YAPI_Exception {
      return this.get_frequency();
   }

   public int get_volume() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._volume;
      }
   }

   public int getVolume() throws YAPI_Exception {
      return this.get_volume();
   }

   public int set_volume(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("volume", rest_val);
         return 0;
      }
   }

   public int setVolume(int newval) throws YAPI_Exception {
      return this.set_volume(newval);
   }

   public int get_playSeqSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._playSeqSize;
      }
   }

   public int getPlaySeqSize() throws YAPI_Exception {
      return this.get_playSeqSize();
   }

   public int get_playSeqMaxSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._playSeqMaxSize;
      }
   }

   public int getPlaySeqMaxSize() throws YAPI_Exception {
      return this.get_playSeqMaxSize();
   }

   public int get_playSeqSignature() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._playSeqSignature;
      }
   }

   public int getPlaySeqSignature() throws YAPI_Exception {
      return this.get_playSeqSignature();
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

   public static YBuzzer FindBuzzer(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YBuzzer obj = (YBuzzer)YFunction._FindFromCache("Buzzer", func);
         if (obj == null) {
            obj = new YBuzzer(func);
            YFunction._AddToCache("Buzzer", func, obj);
         }

         return obj;
      }
   }

   public static YBuzzer FindBuzzerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YBuzzer obj = (YBuzzer)YFunction._FindFromCacheInContext(yctx, "Buzzer", func);
         if (obj == null) {
            obj = new YBuzzer(yctx, func);
            YFunction._AddToCache("Buzzer", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YBuzzer.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackBuzzer = callback;
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
      if (this._valueCallbackBuzzer != null) {
         this._valueCallbackBuzzer.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String command) throws YAPI_Exception {
      return this.set_command(command);
   }

   public int addFreqMoveToPlaySeq(int freq, int msDelay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "A%d,%d", freq, msDelay));
   }

   public int addPulseToPlaySeq(int freq, int msDuration) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "B%d,%d", freq, msDuration));
   }

   public int addVolMoveToPlaySeq(int volume, int msDuration) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "C%d,%d", volume, msDuration));
   }

   public int addNotesToPlaySeq(String notes) throws YAPI_Exception {
      int tempo = 100;
      int prevPitch = 3;
      int prevDuration = 4;
      int prevFreq = 110;
      int note = -99;
      int num = 0;
      int typ = 3;
      byte[] ascNotes = notes.getBytes();
      int notesLen = ascNotes.length;

      for (int i = 0; i < notesLen; i++) {
         int ch = ascNotes[i] & 255;
         if (ch == 65) {
            note = 0;
         }

         if (ch == 66) {
            note = 2;
         }

         if (ch == 67) {
            note = 3;
         }

         if (ch == 68) {
            note = 5;
         }

         if (ch == 69) {
            note = 7;
         }

         if (ch == 70) {
            note = 8;
         }

         if (ch == 71) {
            note = 10;
         }

         if (ch == 35) {
            note++;
         }

         if (ch == 98) {
            note--;
         }

         if (ch == 39) {
            prevPitch += 12;
         }

         if (ch == 44) {
            prevPitch -= 12;
         }

         if (ch == 82) {
            typ = 0;
         }

         if (ch == 33) {
            typ = 1;
         }

         if (ch == 94) {
            typ = 2;
         }

         if (ch == 95) {
            typ = 4;
         }

         if (ch == 45) {
            typ = 5;
         }

         if (ch == 37 && num > 0) {
            tempo = num;
            num = 0;
         }

         if (ch >= 48 && ch <= 57) {
            num = num * 10 + (ch - 48);
         }

         if (ch == 46) {
            num = num * 2 / 3;
         }

         if ((ch == 32 || i + 1 == notesLen) && (note > -99 || typ != 3)) {
            if (num == 0) {
               num = prevDuration;
            } else {
               prevDuration = num;
            }

            int ms = (int)((double)Math.round(320000.0 / (double)(tempo * num)));
            if (typ == 0) {
               this.addPulseToPlaySeq(0, ms);
            } else {
               int dNote = note - prevPitch % 12;
               if (dNote > 6) {
                  dNote -= 12;
               }

               if (dNote <= -6) {
                  dNote += 12;
               }

               int pitch = prevPitch + dNote;
               int freq = (int)((double)Math.round(440.0 * Math.exp((double)pitch * 0.05776226504666)));
               int ms16 = ms >> 4;
               int rest = 0;
               if (typ == 3) {
                  rest = 2 * ms16;
               }

               if (typ == 2) {
                  rest = 8 * ms16;
               }

               if (typ == 1) {
                  rest = 12 * ms16;
               }

               if (typ == 5) {
                  this.addPulseToPlaySeq(prevFreq, ms16);
                  this.addFreqMoveToPlaySeq(freq, 8 * ms16);
                  this.addPulseToPlaySeq(freq, ms - 9 * ms16);
               } else {
                  this.addPulseToPlaySeq(freq, ms - rest);
                  if (rest > 0) {
                     this.addPulseToPlaySeq(0, rest);
                  }
               }

               prevFreq = freq;
               prevPitch = pitch;
            }

            note = -99;
            num = 0;
            typ = 3;
         }
      }

      return 0;
   }

   public int startPlaySeq() throws YAPI_Exception {
      return this.sendCommand("S");
   }

   public int stopPlaySeq() throws YAPI_Exception {
      return this.sendCommand("X");
   }

   public int resetPlaySeq() throws YAPI_Exception {
      return this.sendCommand("Z");
   }

   public int oncePlaySeq() throws YAPI_Exception {
      return this.sendCommand("s");
   }

   public int savePlaySeq() throws YAPI_Exception {
      return this.sendCommand("W");
   }

   public int reloadPlaySeq() throws YAPI_Exception {
      return this.sendCommand("R");
   }

   public int pulse(int frequency, int duration) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "P%d,%d", frequency, duration));
   }

   public int freqMove(int frequency, int duration) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "F%d,%d", frequency, duration));
   }

   public int volumeMove(int volume, int duration) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "V%d,%d", volume, duration));
   }

   public int playNotes(String notes) throws YAPI_Exception {
      this.resetPlaySeq();
      this.addNotesToPlaySeq(notes);
      return this.oncePlaySeq();
   }

   public YBuzzer nextBuzzer() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindBuzzerInContext(this._yapi, next_hwid);
   }

   public static YBuzzer FirstBuzzer() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Buzzer");
         return next_hwid == null ? null : FindBuzzerInContext(yctx, next_hwid);
      }
   }

   public static YBuzzer FirstBuzzerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Buzzer");
      return next_hwid == null ? null : FindBuzzerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YBuzzer var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YBuzzer var1, String var2);
   }
}
