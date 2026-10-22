package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class YFirmwareFile {
   private static final int BYN_REV_V4 = 4;
   private static final int BYN_REV_V5 = 5;
   private static final int BYN_REV_V6 = 6;
   private static final int MAX_ROM_ZONES_PER_FILES = 16;
   private static final int MAX_FLASH_ZONES_PER_FILES = 4;
   private static final int BYN_HEAD_SIZE_V4 = 104;
   private static final int BYN_HEAD_SIZE_V5 = 128;
   private static final int BYN_HEAD_SIZE_V6 = 144;
   private static final int BYN_MD5_OFS_V6 = 112;
   private final String _path;
   private final String _serial;
   private final String _pictype;
   private final String _product;
   private final String _firmware;
   private final String _prog_version;
   private final int _ROM_nb_zone;
   private final int _FLA_nb_zone;
   private final int _ROM_total_size;
   private final int _FLA_total_size;
   private final byte[] _data;
   private int _zone_ofs;

   public YFirmwareFile.byn_zone getBynZone(int zOfs) {
      return new YFirmwareFile.byn_zone(this._data, zOfs);
   }

   public int getFirstZoneOfs() {
      return this._zone_ofs;
   }

   private YFirmwareFile(
      String path,
      String serial,
      String pictype,
      String product,
      String firmware,
      String prog_version,
      int ROM_nb_zone,
      int FLA_nb_zone,
      int ROM_total_size,
      int FLA_total_size,
      byte[] data,
      int zone_ofs
   ) {
      this._path = path;
      this._serial = serial;
      this._pictype = pictype;
      this._product = product;
      this._firmware = firmware;
      this._prog_version = prog_version;
      this._ROM_nb_zone = ROM_nb_zone;
      this._FLA_nb_zone = FLA_nb_zone;
      this._ROM_total_size = ROM_total_size;
      this._FLA_total_size = FLA_total_size;
      this._data = data;
      this._zone_ofs = zone_ofs;
   }

   public static YFirmwareFile Parse(String path, byte[] data) throws YAPI_Exception {
      ByteBuffer buffer = ByteBuffer.wrap(data);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      if (buffer.get() == 66 && buffer.get() == 89 && buffer.get() == 78 && buffer.get() == 0) {
         int rev = buffer.getShort();
         byte[] serial_buf = new byte[20];
         buffer.get(serial_buf);
         String serial = getString(serial_buf);
         byte[] pictype_buf = new byte[20];
         buffer.get(pictype_buf);
         String pictype = getString(pictype_buf);
         byte[] product_buf = new byte[28];
         buffer.get(product_buf);
         String product = getString(product_buf);
         byte[] firmware_buf = new byte[22];
         buffer.get(firmware_buf);
         String firmware = getString(firmware_buf);
         if (serial.length() >= 20) {
            throw new YAPI_Exception(-2, "Bad serial_buf");
         } else if (product.length() >= 28) {
            throw new YAPI_Exception(-2, "Bad product name");
         } else if (firmware.length() >= 22) {
            throw new YAPI_Exception(-2, "Bad firmware revision");
         } else {
            int ROM_nb_zone = 0;
            int FLA_nb_zone = 0;
            int ROM_total_size = 0;
            int FLA_total_size = 0;
            String prog_version = "";
            int zone_ofs;
            switch (rev) {
               case 4:
                  zone_ofs = 104;
                  ROM_nb_zone = buffer.getInt();
                  int datasizex = buffer.getInt();
                  if (ROM_nb_zone > 16) {
                     throw new YAPI_Exception(-2, "Too many zones");
                  }

                  if (datasizex != data.length - 104) {
                     throw new YAPI_Exception(-2, "Incorrect file size");
                  }
                  break;
               case 5:
                  zone_ofs = 128;
                  byte[] prog_buf = new byte[22];
                  buffer.get(prog_buf);
                  prog_version = checkProgField(prog_buf);
                  buffer.getShort();
                  ROM_nb_zone = buffer.getInt();
                  int datasize = buffer.getInt();
                  if (ROM_nb_zone > 16) {
                     throw new YAPI_Exception(-2, "Too many zones");
                  }

                  if (datasize != data.length - 128) {
                     throw new YAPI_Exception(-2, "Incorrect file size");
                  }
                  break;
               case 6:
                  zone_ofs = 144;
                  byte[] md5check = new byte[16];
                  buffer.get(md5check);

                  try {
                     MessageDigest digest = MessageDigest.getInstance("MD5");
                     digest.update(data, 112, data.length - 112);
                     byte[] messageDigest = digest.digest();
                     if (!Arrays.equals(md5check, messageDigest)) {
                        throw new YAPI_Exception(-2, "Invalid checksum");
                     }
                  } catch (NoSuchAlgorithmException var24) {
                     throw new YAPI_Exception(-8, "Unable to verfiy MD5 of firmware " + path);
                  }

                  byte[] prog_bufx = new byte[22];
                  buffer.get(prog_bufx);
                  prog_version = checkProgField(prog_bufx);
                  ROM_nb_zone = buffer.get();
                  FLA_nb_zone = buffer.get();
                  ROM_total_size = buffer.getInt();
                  FLA_total_size = buffer.getInt();
                  if (ROM_nb_zone > 16) {
                     throw new YAPI_Exception(-2, "Too many ROM zones");
                  }

                  if (FLA_nb_zone > 4) {
                     throw new YAPI_Exception(-2, "Too many FLASH zones");
                  }
                  break;
               default:
                  throw new YAPI_Exception(-2, "unknown BYN file revision");
            }

            return new YFirmwareFile(
               path, serial, pictype, product, firmware, prog_version, ROM_nb_zone, FLA_nb_zone, ROM_total_size, FLA_total_size, data, zone_ofs
            );
         }
      } else {
         throw new YAPI_Exception(-2, "Not a firmware file");
      }
   }

   private static String getString(byte[] serial_buf) {
      int i = 0;

      while (i < serial_buf.length && serial_buf[i] != 0) {
         i++;
      }

      return new String(serial_buf, 0, i, Charset.forName("ISO_8859_1"));
   }

   private static String checkProgField(byte[] prog_buf) throws YAPI_Exception {
      String prog_version = getString(prog_buf);
      if (!prog_version.equals("")) {
         int byn = Integer.valueOf(prog_version);

         try {
            int tools = Integer.parseInt("44175");
            if (byn > tools) {
               throw new YAPI_Exception(-5, "Too recent firmware. Please update the yoctopuce library");
            }
         } catch (NumberFormatException var4) {
         }
      }

      return prog_version;
   }

   public String getSerial() {
      return this._serial;
   }

   public String getPictype() {
      return this._pictype;
   }

   public String getProduct() {
      return this._product;
   }

   public String getFirmwareRelease() {
      return this._firmware;
   }

   public int getFirmwareReleaseAsInt() {
      try {
         return Integer.parseInt(this._firmware);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   public String getProg_version() {
      return this._prog_version;
   }

   public int getROM_nb_zone() {
      return this._ROM_nb_zone;
   }

   public int getFLA_nb_zone() {
      return this._FLA_nb_zone;
   }

   public int getROM_total_size() {
      return this._ROM_total_size;
   }

   public int getFLA_total_size() {
      return this._FLA_total_size;
   }

   public byte[] getData() {
      return this._data;
   }

   public String getPath() {
      return this._path;
   }

   public class byn_zone {
      public static final int SIZE = 8;
      public final int addr_page;
      public final int len;

      public byn_zone(byte[] data, int zOfs) {
         ByteBuffer bb = ByteBuffer.wrap(data, zOfs, 8);
         bb.order(ByteOrder.LITTLE_ENDIAN);
         this.addr_page = bb.getInt();
         this.len = bb.getInt();
      }
   }
}
