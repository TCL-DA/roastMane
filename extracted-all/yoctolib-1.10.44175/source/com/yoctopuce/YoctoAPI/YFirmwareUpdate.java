package com.yoctopuce.YoctoAPI;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class YFirmwareUpdate {
   protected String _serial;
   protected byte[] _settings;
   protected String _firmwarepath;
   protected String _progress_msg;
   protected int _progress_c = 0;
   protected int _progress = 0;
   protected int _restore_step = 0;
   protected boolean _force;
   private final YAPIContext _yctx;
   private Thread _thread = null;

   static byte[] _downloadfile(String url) throws YAPI_Exception {
      ByteArrayOutputStream result = new ByteArrayOutputStream(1024);

      URL u;
      try {
         u = new URL(url);
      } catch (MalformedURLException var15) {
         throw new YAPI_Exception(-8, var15.getLocalizedMessage());
      }

      BufferedInputStream in = null;

      try {
         URLConnection connection = u.openConnection();
         in = new BufferedInputStream(connection.getInputStream());
         byte[] buffer = new byte[1024];
         int readed = 0;

         while (readed >= 0) {
            readed = in.read(buffer, 0, buffer.length);
            if (readed < 0) {
               break;
            }

            result.write(buffer, 0, readed);
         }
      } catch (IOException var16) {
         throw new YAPI_Exception(-8, "unable to contact www.yoctopuce.com :" + var16.getLocalizedMessage());
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException var14) {
            }
         }
      }

      return result.toByteArray();
   }

   static YFirmwareFile checkFirmware_r(File folder, String serial_base) throws YAPI_Exception {
      YFirmwareFile bestFirmware = null;
      if (folder.isFile()) {
         bestFirmware = _loadFirmwareFile(folder);
      } else {
         File[] listOfFiles = folder.listFiles();
         if (listOfFiles != null) {
            for (File subfile : listOfFiles) {
               if (subfile.getName().startsWith(serial_base)) {
                  YFirmwareFile firmware = null;
                  if (subfile.isFile()) {
                     try {
                        firmware = _loadFirmwareFile(subfile);
                     } catch (YAPI_Exception var10) {
                        continue;
                     }
                  } else if (subfile.isDirectory()) {
                     firmware = checkFirmware_r(subfile, serial_base);
                  }

                  if (firmware != null
                     && firmware.getSerial().startsWith(serial_base)
                     && (bestFirmware == null || bestFirmware.getFirmwareReleaseAsInt() < firmware.getFirmwareReleaseAsInt())) {
                     bestFirmware = firmware;
                  }
               }
            }
         }
      }

      return bestFirmware;
   }

   private static YFirmwareFile _loadFirmwareFile(File file) throws YAPI_Exception {
      FileInputStream in;
      try {
         in = new FileInputStream(file);
      } catch (FileNotFoundException var13) {
         throw new YAPI_Exception(-14, "File not found");
      }

      ByteArrayOutputStream result = new ByteArrayOutputStream(1024);

      try {
         byte[] buffer = new byte[1024];
         int readed = 0;

         while (readed >= 0) {
            readed = in.read(buffer, 0, buffer.length);
            if (readed < 0) {
               break;
            }

            result.write(buffer, 0, readed);
         }
      } catch (IOException var14) {
         throw new YAPI_Exception(-8, "unable to load file :" + var14.getLocalizedMessage());
      } finally {
         try {
            in.close();
         } catch (IOException var12) {
         }
      }

      return YFirmwareFile.Parse(file.getPath(), result.toByteArray());
   }

   public YFirmwareUpdate(YAPIContext yctx, String serial, String path, byte[] settings, boolean force) {
      this._serial = serial;
      this._firmwarepath = path;
      this._settings = settings;
      this._yctx = yctx;
      this._force = force;
   }

   public YFirmwareUpdate(YAPIContext yctx, String serial, String path, byte[] settings) {
      this(yctx, serial, path, settings, false);
   }

   public YFirmwareUpdate(String serial, String path, byte[] settings) {
      this(YAPI.GetYCtx(false), serial, path, settings, false);
   }

   private void _progress(int progress, String msg) {
      synchronized (this) {
         this._progress = progress;
         this._progress_msg = msg;
      }
   }

   private int _processMore_internal(int start) {
      synchronized (this) {
         if (start > 0 && (this._thread == null || !this._thread.isAlive())) {
            this._progress(0, "Firmware update started");
            this._thread = new Thread(
               new Runnable() {
                  @Override
                  public void run() {
                     try {
                        YFirmwareUpdate.this._progress(1, "Loading firmware");
                        YFirmwareFile firmware;
                        if (!YFirmwareUpdate.this._firmwarepath.startsWith("www.yoctopuce.com")
                           && !YFirmwareUpdate.this._firmwarepath.startsWith("http://www.yoctopuce.com")) {
                           firmware = YFirmwareUpdate._loadFirmwareFile(new File(YFirmwareUpdate.this._firmwarepath));
                        } else {
                           byte[] bytes = YFirmwareUpdate._downloadfile(YFirmwareUpdate.this._firmwarepath);
                           firmware = YFirmwareFile.Parse(YFirmwareUpdate.this._firmwarepath, bytes);
                        }

                        YFirmwareUpdate.this._progress(5, "check if module is already in bootloader");
                        YGenericHub hub = null;
                        YModule module = YModule.FindModuleInContext(YFirmwareUpdate.this._yctx, YFirmwareUpdate.this._serial + ".module");
                        if (module.isOnline()) {
                           YDevice yDevice = module.getYDevice();
                           hub = yDevice.getHub();
                        } else {
                           for (YGenericHub h : YFirmwareUpdate.this._yctx._hubs) {
                              ArrayList<String> bootloaders = h.getBootloaders();
                              if (bootloaders.contains(YFirmwareUpdate.this._serial)) {
                                 hub = h;
                                 break;
                              }
                           }
                        }

                        if (hub == null) {
                           throw new YAPI_Exception(-4, "device " + YFirmwareUpdate.this._serial + " is not detected");
                        }

                        hub.firmwareUpdate(YFirmwareUpdate.this._serial, firmware, YFirmwareUpdate.this._settings, new YGenericHub.UpdateProgress() {
                           @Override
                           public void firmware_progress(int percent, String message) {
                              YFirmwareUpdate.this._progress(5 + percent * 80 / 100, message);
                           }
                        });
                        if (hub.isCallbackMode() && hub.getSerialNumber().equals(YFirmwareUpdate.this._serial)) {
                           YFirmwareUpdate.this._progress(100, "Success (WebSocket Callback)");
                           return;
                        }

                        YFirmwareUpdate.this._progress(80, "wait to the device restart");
                        long timeout = YAPI.GetTickCount() + 60000L;
                        module.clearCache();

                        while (!module.isOnline() && timeout > YAPI.GetTickCount()) {
                           Thread.sleep(500L);

                           try {
                              YFirmwareUpdate.this._yctx.UpdateDeviceList();
                           } catch (YAPI_Exception var7) {
                           }
                        }

                        if (module.isOnline()) {
                           if (YFirmwareUpdate.this._settings != null) {
                              module.set_allSettingsAndFiles(YFirmwareUpdate.this._settings);
                              module.saveToFlash();
                           }

                           String realFw = module.get_firmwareRelease();
                           if (realFw.equals(firmware.getFirmwareRelease())) {
                              YFirmwareUpdate.this._progress(100, "Success");
                           } else {
                              YFirmwareUpdate.this._progress(-8, "Unable to update firmware");
                           }
                        } else {
                           YFirmwareUpdate.this._progress(-4, "Device did not reboot correctly");
                        }
                     } catch (YAPI_Exception var8) {
                        YFirmwareUpdate.this._progress(var8.errorType, var8.getLocalizedMessage());
                        var8.printStackTrace();
                     } catch (InterruptedException var9) {
                     }
                  }
               },
               "Update" + this._serial
            );
            this._thread.start();
         }

         return 0;
      }
   }

   private static String CheckFirmware_internal(String serial, String path, int minrelease) {
      String link = "";
      Integer best_rev = 0;

      try {
         if (!path.startsWith("www.yoctopuce.com") && !path.startsWith("http://www.yoctopuce.com")) {
            File folder = new File(path);
            YFirmwareFile firmware = checkFirmware_r(folder, serial.substring(0, 8));
            if (firmware != null) {
               best_rev = firmware.getFirmwareReleaseAsInt();
               link = firmware.getPath();
            }
         } else {
            byte[] json = _downloadfile("http://www.yoctopuce.com//FR/common/getLastFirmwareLink.php?serial=" + serial);

            YJSONObject obj;
            try {
               obj = new YJSONObject(new String(json, Charset.forName("ISO_8859_1")));
               obj.parse();
            } catch (Exception var10) {
               throw new YAPI_Exception(-8, var10.getLocalizedMessage());
            }

            try {
               link = obj.getString("link");
               best_rev = obj.getInt("version");
            } catch (Exception var9) {
               throw new YAPI_Exception(-8, "invalid respond form www.yoctopuce.com" + var9.getLocalizedMessage());
            }
         }

         if (minrelease != 0) {
            return minrelease < best_rev ? link : "";
         } else {
            return link;
         }
      } catch (Exception var11) {
         return "error:" + var11.getLocalizedMessage();
      }
   }

   private static ArrayList<String> GetAllBootLoadersInContext_internal(YAPIContext yctx) {
      ArrayList<String> res = new ArrayList<>();

      for (YGenericHub h : yctx._hubs) {
         try {
            ArrayList<String> bootloaders;
            try {
               bootloaders = h.getBootloaders();
            } catch (InterruptedException var6) {
               throw new YAPI_Exception(-8, "Thread has been interrupted");
            }

            if (bootloaders != null) {
               res.addAll(bootloaders);
            }
         } catch (YAPI_Exception var7) {
            yctx._Log(var7.getLocalizedMessage());
         }
      }

      return res;
   }

   private static ArrayList<String> GetAllBootLoaders_internal() {
      return GetAllBootLoadersInContext(YAPI.GetYCtx(false));
   }

   public int _processMore(int newupdate) {
      return this._processMore_internal(newupdate);
   }

   public static ArrayList<String> GetAllBootLoaders() {
      return GetAllBootLoaders_internal();
   }

   public static ArrayList<String> GetAllBootLoadersInContext(YAPIContext yctx) {
      return GetAllBootLoadersInContext_internal(yctx);
   }

   public static String CheckFirmware(String serial, String path, int minrelease) {
      return CheckFirmware_internal(serial, path, minrelease);
   }

   public int get_progress() {
      if (this._progress >= 0) {
         this._processMore(0);
      }

      return this._progress;
   }

   public String get_progressMessage() {
      return this._progress_msg;
   }

   public int startUpdate() {
      String err = new String(this._settings);
      int leng = err.length();
      if (leng >= 6 && "error:".equals(err.substring(0, 6))) {
         this._progress = -1;
         this._progress_msg = err.substring(6, 6 + leng - 6);
      } else {
         this._progress = 0;
         this._progress_c = 0;
         this._processMore(1);
      }

      return this._progress;
   }
}
