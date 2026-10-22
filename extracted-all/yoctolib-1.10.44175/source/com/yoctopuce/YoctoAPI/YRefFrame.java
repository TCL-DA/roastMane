package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YRefFrame extends YFunction {
   public static final int MOUNTPOS_INVALID = -1;
   public static final double BEARING_INVALID = -1.79769313486231E308;
   public static final String CALIBRATIONPARAM_INVALID = "!INVALID!";
   public static final int FUSIONMODE_NDOF = 0;
   public static final int FUSIONMODE_NDOF_FMC_OFF = 1;
   public static final int FUSIONMODE_M4G = 2;
   public static final int FUSIONMODE_COMPASS = 3;
   public static final int FUSIONMODE_IMU = 4;
   public static final int FUSIONMODE_INCLIN_90DEG_1G8 = 5;
   public static final int FUSIONMODE_INCLIN_90DEG_3G6 = 6;
   public static final int FUSIONMODE_INCLIN_10DEG = 7;
   public static final int FUSIONMODE_INVALID = -1;
   protected int _mountPos = -1;
   protected double _bearing = -1.79769313486231E308;
   protected String _calibrationParam = "!INVALID!";
   protected int _fusionMode = -1;
   protected YRefFrame.UpdateCallback _valueCallbackRefFrame = null;
   protected boolean _calibV2;
   protected int _calibStage = 0;
   protected String _calibStageHint;
   protected int _calibStageProgress = 0;
   protected int _calibProgress = 0;
   protected String _calibLogMsg;
   protected String _calibSavedParams;
   protected int _calibCount = 0;
   protected int _calibInternalPos = 0;
   protected int _calibPrevTick = 0;
   protected ArrayList<Integer> _calibOrient = new ArrayList<>();
   protected ArrayList<Double> _calibDataAccX = new ArrayList<>();
   protected ArrayList<Double> _calibDataAccY = new ArrayList<>();
   protected ArrayList<Double> _calibDataAccZ = new ArrayList<>();
   protected ArrayList<Double> _calibDataAcc = new ArrayList<>();
   protected double _calibAccXOfs = 0.0;
   protected double _calibAccYOfs = 0.0;
   protected double _calibAccZOfs = 0.0;
   protected double _calibAccXScale = 0.0;
   protected double _calibAccYScale = 0.0;
   protected double _calibAccZScale = 0.0;

   protected YRefFrame(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "RefFrame";
   }

   protected YRefFrame(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("mountPos")) {
         this._mountPos = json_val.getInt("mountPos");
      }

      if (json_val.has("bearing")) {
         this._bearing = (double)Math.round(json_val.getDouble("bearing") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("calibrationParam")) {
         this._calibrationParam = json_val.getString("calibrationParam");
      }

      if (json_val.has("fusionMode")) {
         this._fusionMode = json_val.getInt("fusionMode");
      }

      super._parseAttr(json_val);
   }

   public int get_mountPos() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._mountPos;
      }
   }

   public int set_mountPos(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("mountPos", rest_val);
         return 0;
      }
   }

   public int set_bearing(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("bearing", rest_val);
         return 0;
      }
   }

   public int setBearing(double newval) throws YAPI_Exception {
      return this.set_bearing(newval);
   }

   public double get_bearing() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._bearing;
      }
   }

   public double getBearing() throws YAPI_Exception {
      return this.get_bearing();
   }

   public String get_calibrationParam() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._calibrationParam;
      }
   }

   public int set_calibrationParam(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("calibrationParam", newval);
         return 0;
      }
   }

   public int get_fusionMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._fusionMode;
      }
   }

   public int getFusionMode() throws YAPI_Exception {
      return this.get_fusionMode();
   }

   public int set_fusionMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("fusionMode", rest_val);
         return 0;
      }
   }

   public int setFusionMode(int newval) throws YAPI_Exception {
      return this.set_fusionMode(newval);
   }

   public static YRefFrame FindRefFrame(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YRefFrame obj = (YRefFrame)YFunction._FindFromCache("RefFrame", func);
         if (obj == null) {
            obj = new YRefFrame(func);
            YFunction._AddToCache("RefFrame", func, obj);
         }

         return obj;
      }
   }

   public static YRefFrame FindRefFrameInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YRefFrame obj = (YRefFrame)YFunction._FindFromCacheInContext(yctx, "RefFrame", func);
         if (obj == null) {
            obj = new YRefFrame(yctx, func);
            YFunction._AddToCache("RefFrame", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YRefFrame.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackRefFrame = callback;
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
      if (this._valueCallbackRefFrame != null) {
         this._valueCallbackRefFrame.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YRefFrame.MOUNTPOSITION get_mountPosition() throws YAPI_Exception {
      int position = this.get_mountPos();
      return position < 0 ? YRefFrame.MOUNTPOSITION.INVALID : YRefFrame.MOUNTPOSITION.fromInt(position >> 2);
   }

   public YRefFrame.MOUNTORIENTATION get_mountOrientation() throws YAPI_Exception {
      int position = this.get_mountPos();
      return position < 0 ? YRefFrame.MOUNTORIENTATION.INVALID : YRefFrame.MOUNTORIENTATION.fromInt(position & 3);
   }

   public int set_mountPosition(YRefFrame.MOUNTPOSITION position, YRefFrame.MOUNTORIENTATION orientation) throws YAPI_Exception {
      int mixedPos = (position.value << 2) + orientation.value;
      return this.set_mountPos(mixedPos);
   }

   public int get_calibrationState() throws YAPI_Exception {
      new ArrayList();
      String calibParam = this.get_calibrationParam();
      ArrayList<Integer> iCalib = YAPIContext._decodeFloats(calibParam);
      int caltyp = (Integer)iCalib.get(0) / 1000;
      return caltyp != 33 ? -3 : (Integer)iCalib.get(1) / 1000;
   }

   public int get_measureQuality() throws YAPI_Exception {
      new ArrayList();
      String calibParam = this.get_calibrationParam();
      ArrayList<Integer> iCalib = YAPIContext._decodeFloats(calibParam);
      int caltyp = (Integer)iCalib.get(0) / 1000;
      return caltyp != 33 ? -3 : (Integer)iCalib.get(2) / 1000;
   }

   public int _calibSort(int start, int stopidx) {
      int changed = 1;

      while (changed > 0) {
         changed = 0;
         double a = this._calibDataAcc.get(start);

         for (int idx = start + 1; idx < stopidx; idx++) {
            double b = this._calibDataAcc.get(idx);
            if (a > b) {
               this._calibDataAcc.set(idx - 1, b);
               this._calibDataAcc.set(idx, a);
               double xa = this._calibDataAccX.get(idx - 1);
               double xb = this._calibDataAccX.get(idx);
               this._calibDataAccX.set(idx - 1, xb);
               this._calibDataAccX.set(idx, xa);
               xa = this._calibDataAccY.get(idx - 1);
               xb = this._calibDataAccY.get(idx);
               this._calibDataAccY.set(idx - 1, xb);
               this._calibDataAccY.set(idx, xa);
               xa = this._calibDataAccZ.get(idx - 1);
               xb = this._calibDataAccZ.get(idx);
               this._calibDataAccZ.set(idx - 1, xb);
               this._calibDataAccZ.set(idx, xa);
               changed++;
            } else {
               a = b;
            }
         }
      }

      return 0;
   }

   public int start3DCalibration() throws YAPI_Exception {
      if (!this.isOnline()) {
         return -4;
      } else {
         if (this._calibStage != 0) {
            this.cancel3DCalibration();
         }

         this._calibSavedParams = this.get_calibrationParam();
         this._calibV2 = YAPIContext._atoi(this._calibSavedParams) == 33;
         this.set_calibrationParam("0");
         this._calibCount = 50;
         this._calibStage = 1;
         this._calibStageHint = "Set down the device on a steady horizontal surface";
         this._calibStageProgress = 0;
         this._calibProgress = 1;
         this._calibInternalPos = 0;
         this._calibPrevTick = (int)(YAPIContext.GetTickCount() & 2147483647L);
         this._calibOrient.clear();
         this._calibDataAccX.clear();
         this._calibDataAccY.clear();
         this._calibDataAccZ.clear();
         this._calibDataAcc.clear();
         return 0;
      }
   }

   public int more3DCalibration() throws YAPI_Exception {
      return this._calibV2 ? this.more3DCalibrationV2() : this.more3DCalibrationV1();
   }

   public int more3DCalibrationV1() throws YAPI_Exception {
      if (this._calibStage == 0) {
         return -2;
      } else if (this._calibProgress == 100) {
         return 0;
      } else {
         int currTick = (int)(YAPIContext.GetTickCount() & 2147483647L);
         if ((currTick - this._calibPrevTick & 2147483647) < 160) {
            return 0;
         } else {
            this._calibStageHint = "Set down the device on a steady horizontal surface";
            this._calibPrevTick = currTick + 500 & 2147483647;
            byte[] jsonData = this._download("api/accelerometer.json");
            double xVal = (double)YAPIContext._atoi(this._json_get_key(jsonData, "xValue")) / 65536.0;
            double yVal = (double)YAPIContext._atoi(this._json_get_key(jsonData, "yValue")) / 65536.0;
            double zVal = (double)YAPIContext._atoi(this._json_get_key(jsonData, "zValue")) / 65536.0;
            double xSq = xVal * xVal;
            if (xSq >= 0.04 && xSq < 0.64) {
               return 0;
            } else if (xSq >= 1.44) {
               return 0;
            } else {
               double ySq = yVal * yVal;
               if (ySq >= 0.04 && ySq < 0.64) {
                  return 0;
               } else if (ySq >= 1.44) {
                  return 0;
               } else {
                  double zSq = zVal * zVal;
                  if (zSq >= 0.04 && zSq < 0.64) {
                     return 0;
                  } else if (zSq >= 1.44) {
                     return 0;
                  } else {
                     double norm = Math.sqrt(xSq + ySq + zSq);
                     if (!(norm < 0.8) && !(norm > 1.2)) {
                        this._calibPrevTick = currTick;
                        int orient = 0;
                        if (zSq > 0.5) {
                           if (zVal > 0.0) {
                              orient = 0;
                           } else {
                              orient = 1;
                           }
                        }

                        if (xSq > 0.5) {
                           if (xVal > 0.0) {
                              orient = 2;
                           } else {
                              orient = 3;
                           }
                        }

                        if (ySq > 0.5) {
                           if (yVal > 0.0) {
                              orient = 4;
                           } else {
                              orient = 5;
                           }
                        }

                        if (this._calibStageProgress == 0) {
                           int idx = 0;

                           int err;
                           for (err = 0; idx + 1 < this._calibStage; idx++) {
                              if (this._calibOrient.get(idx) == orient) {
                                 err = 1;
                              }
                           }

                           if (err != 0) {
                              this._calibStageHint = "Turn the device on another face";
                              return 0;
                           }

                           this._calibOrient.add(orient);
                        } else if (orient != this._calibOrient.get(this._calibStage - 1)) {
                           this._calibStageHint = "Not yet done, please move back to the previous face";
                           return 0;
                        }

                        this._calibStageHint = "calibrating..";
                        this._calibDataAccX.add(xVal);
                        this._calibDataAccY.add(yVal);
                        this._calibDataAccZ.add(zVal);
                        this._calibDataAcc.add(norm);
                        this._calibInternalPos++;
                        this._calibProgress = 1 + 16 * (this._calibStage - 1) + 16 * this._calibInternalPos / this._calibCount;
                        if (this._calibInternalPos < this._calibCount) {
                           this._calibStageProgress = 1 + 99 * this._calibInternalPos / this._calibCount;
                           return 0;
                        } else {
                           int intpos = (this._calibStage - 1) * this._calibCount;
                           this._calibSort(intpos, intpos + this._calibCount);
                           intpos += this._calibCount / 2;
                           this._calibLogMsg = String.format(
                              Locale.US,
                              "Stage %d: median is %d,%d,%d",
                              this._calibStage,
                              (int)((double)Math.round(1000.0 * this._calibDataAccX.get(intpos))),
                              (int)((double)Math.round(1000.0 * this._calibDataAccY.get(intpos))),
                              (int)((double)Math.round(1000.0 * this._calibDataAccZ.get(intpos)))
                           );
                           this._calibStage++;
                           if (this._calibStage < 7) {
                              this._calibStageHint = "Turn the device on another face";
                              this._calibPrevTick = currTick + 500 & 2147483647;
                              this._calibStageProgress = 0;
                              this._calibInternalPos = 0;
                              return 0;
                           } else {
                              xVal = 0.0;
                              yVal = 0.0;
                              zVal = 0.0;

                              for (int idx = 0; idx < 6; idx++) {
                                 intpos = idx * this._calibCount + this._calibCount / 2;
                                 orient = this._calibOrient.get(idx);
                                 if (orient == 0 || orient == 1) {
                                    zVal += this._calibDataAccZ.get(intpos);
                                 }

                                 if (orient == 2 || orient == 3) {
                                    xVal += this._calibDataAccX.get(intpos);
                                 }

                                 if (orient == 4 || orient == 5) {
                                    yVal += this._calibDataAccY.get(intpos);
                                 }
                              }

                              this._calibAccXOfs = xVal / 2.0;
                              this._calibAccYOfs = yVal / 2.0;
                              this._calibAccZOfs = zVal / 2.0;

                              for (int var38 = 0; var38 < this._calibDataAcc.size(); var38++) {
                                 xVal = this._calibDataAccX.get(var38) - this._calibAccXOfs;
                                 yVal = this._calibDataAccY.get(var38) - this._calibAccYOfs;
                                 zVal = this._calibDataAccZ.get(var38) - this._calibAccZOfs;
                                 norm = Math.sqrt(xVal * xVal + yVal * yVal + zVal * zVal);
                                 this._calibDataAcc.set(var38, norm);
                              }

                              for (int var34 = 0; var34 < 6; var34++) {
                                 intpos = var34 * this._calibCount;
                                 this._calibSort(intpos, intpos + this._calibCount);
                              }

                              xVal = 0.0;
                              yVal = 0.0;
                              zVal = 0.0;

                              for (int var35 = 0; var35 < 6; var35++) {
                                 intpos = var35 * this._calibCount + this._calibCount / 2;
                                 orient = this._calibOrient.get(var35);
                                 if (orient == 0 || orient == 1) {
                                    zVal += this._calibDataAcc.get(intpos);
                                 }

                                 if (orient == 2 || orient == 3) {
                                    xVal += this._calibDataAcc.get(intpos);
                                 }

                                 if (orient == 4 || orient == 5) {
                                    yVal += this._calibDataAcc.get(intpos);
                                 }
                              }

                              this._calibAccXScale = xVal / 2.0;
                              this._calibAccYScale = yVal / 2.0;
                              this._calibAccZScale = zVal / 2.0;
                              this._calibProgress = 100;
                              this._calibStageHint = "Calibration data ready for saving";
                              return 0;
                           }
                        }
                     } else {
                        return 0;
                     }
                  }
               }
            }
         }
      }
   }

   public int more3DCalibrationV2() throws YAPI_Exception {
      new ArrayList();
      if (this._calibStage == 0) {
         return -2;
      } else if (this._calibProgress == 100) {
         return 0;
      } else {
         if (this._calibStage == 1) {
            int currTick = (int)(YAPIContext.GetTickCount() & 2147483647L);
            currTick = currTick - this._calibPrevTick & 2147483647;
            if (currTick < 1600) {
               this._calibStageHint = "Set down the device on a steady horizontal surface";
               this._calibStageProgress = currTick / 40;
               this._calibProgress = 1;
               return 0;
            }
         }

         byte[] calibParam = this._download("api/refFrame/calibrationParam.txt");
         ArrayList<Integer> iCalib = YAPIContext._decodeFloats(new String(calibParam));
         int cal3 = (Integer)iCalib.get(1) / 1000;
         int calAcc = cal3 / 100;
         int calMag = cal3 / 10 - 10 * calAcc;
         int calGyr = cal3 % 10;
         if (calGyr < 3) {
            this._calibStageHint = "Set down the device on a steady horizontal surface";
            this._calibStageProgress = 40 + calGyr * 20;
            this._calibProgress = 4 + calGyr * 2;
         } else {
            this._calibStage = 2;
            if (calMag < 3) {
               this._calibStageHint = "Slowly draw '8' shapes along the 3 axis";
               this._calibStageProgress = 1 + calMag * 33;
               this._calibProgress = 10 + calMag * 5;
            } else {
               this._calibStage = 3;
               if (calAcc < 3) {
                  this._calibStageHint = "Slowly turn the device, stopping at each 90 degrees";
                  this._calibStageProgress = 1 + calAcc * 33;
                  this._calibProgress = 25 + calAcc * 25;
               } else {
                  this._calibStageProgress = 99;
                  this._calibProgress = 100;
               }
            }
         }

         return 0;
      }
   }

   public String get_3DCalibrationHint() {
      return this._calibStageHint;
   }

   public int get_3DCalibrationProgress() {
      return this._calibProgress;
   }

   public int get_3DCalibrationStage() {
      return this._calibStage;
   }

   public int get_3DCalibrationStageProgress() {
      return this._calibStageProgress;
   }

   public String get_3DCalibrationLogMsg() {
      String msg = this._calibLogMsg;
      this._calibLogMsg = "";
      return msg;
   }

   public int save3DCalibration() throws YAPI_Exception {
      return this._calibV2 ? this.save3DCalibrationV2() : this.save3DCalibrationV1();
   }

   public int save3DCalibrationV1() throws YAPI_Exception {
      if (this._calibProgress != 100) {
         return -2;
      } else {
         int shiftX = -((int)((double)Math.round(this._calibAccXOfs / 7.32E-4)));
         if (shiftX < 0) {
            shiftX += 65536;
         }

         int shiftY = -((int)((double)Math.round(this._calibAccYOfs / 7.32E-4)));
         if (shiftY < 0) {
            shiftY += 65536;
         }

         int shiftZ = -((int)((double)Math.round(this._calibAccZOfs / 7.32E-4)));
         if (shiftZ < 0) {
            shiftZ += 65536;
         }

         int scaleX = (int)((double)Math.round(2048.0 / this._calibAccXScale)) - 2048;
         int scaleY = (int)((double)Math.round(2048.0 / this._calibAccYScale)) - 2048;
         int scaleZ = (int)((double)Math.round(2048.0 / this._calibAccZScale)) - 2048;
         int scaleExp;
         if (scaleX < -2048 || scaleX >= 2048 || scaleY < -2048 || scaleY >= 2048 || scaleZ < -2048 || scaleZ >= 2048) {
            scaleExp = 3;
         } else if (scaleX < -1024 || scaleX >= 1024 || scaleY < -1024 || scaleY >= 1024 || scaleZ < -1024 || scaleZ >= 1024) {
            scaleExp = 2;
         } else if (scaleX >= -512 && scaleX < 512 && scaleY >= -512 && scaleY < 512 && scaleZ >= -512 && scaleZ < 512) {
            scaleExp = 0;
         } else {
            scaleExp = 1;
         }

         if (scaleExp > 0) {
            scaleX >>= scaleExp;
            scaleY >>= scaleExp;
            scaleZ >>= scaleExp;
         }

         if (scaleX < 0) {
            scaleX += 1024;
         }

         if (scaleY < 0) {
            scaleY += 1024;
         }

         if (scaleZ < 0) {
            scaleZ += 1024;
         }

         int scaleLo = ((scaleY & 15) << 12) + (scaleX << 2) + scaleExp;
         int scaleHi = (scaleZ << 6) + (scaleY >> 4);
         String newcalib = String.format(Locale.US, "5,%d,%d,%d,%d,%d", shiftX, shiftY, shiftZ, scaleLo, scaleHi);
         this._calibStage = 0;
         return this.set_calibrationParam(newcalib);
      }
   }

   public int save3DCalibrationV2() throws YAPI_Exception {
      return this.set_calibrationParam("5,5,5,5,5,5");
   }

   public int cancel3DCalibration() throws YAPI_Exception {
      if (this._calibStage == 0) {
         return 0;
      } else {
         this._calibStage = 0;
         return this.set_calibrationParam(this._calibSavedParams);
      }
   }

   public YRefFrame nextRefFrame() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindRefFrameInContext(this._yapi, next_hwid);
   }

   public static YRefFrame FirstRefFrame() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("RefFrame");
         return next_hwid == null ? null : FindRefFrameInContext(yctx, next_hwid);
      }
   }

   public static YRefFrame FirstRefFrameInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("RefFrame");
      return next_hwid == null ? null : FindRefFrameInContext(yctx, next_hwid);
   }

   public static enum MOUNTORIENTATION {
      TWELVE(0),
      THREE(1),
      SIX(2),
      NINE(3),
      INVALID(4);

      public final int value;

      private MOUNTORIENTATION(int val) {
         this.value = val;
      }

      public static YRefFrame.MOUNTORIENTATION fromInt(int intval) {
         switch (intval) {
            case 0:
               return TWELVE;
            case 1:
               return THREE;
            case 2:
               return SIX;
            case 3:
               return NINE;
            case 4:
               return INVALID;
            default:
               return null;
         }
      }
   }

   public static enum MOUNTPOSITION {
      BOTTOM(0),
      TOP(1),
      FRONT(2),
      REAR(3),
      RIGHT(4),
      LEFT(5),
      INVALID(6);

      public final int value;

      private MOUNTPOSITION(int val) {
         this.value = val;
      }

      public static YRefFrame.MOUNTPOSITION fromInt(int intval) {
         switch (intval) {
            case 0:
               return BOTTOM;
            case 1:
               return TOP;
            case 2:
               return FRONT;
            case 3:
               return REAR;
            case 4:
               return RIGHT;
            case 5:
               return LEFT;
            case 6:
               return INVALID;
            default:
               return null;
         }
      }
   }

   public interface TimedReportCallback {
      void timedReportCallback(YRefFrame var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YRefFrame var1, String var2);
   }
}
