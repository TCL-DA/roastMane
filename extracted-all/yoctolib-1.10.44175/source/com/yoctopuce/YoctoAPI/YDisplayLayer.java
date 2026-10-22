package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YDisplayLayer {
   private YDisplay _display;
   private int _id;
   private StringBuilder _cmdbuff;
   private Boolean _hidden;

   public synchronized int flush_now() throws YAPI_Exception {
      int res = 0;
      if (this._cmdbuff.length() > 0) {
         res = this._display.sendCommand(this._cmdbuff.toString());
         this._cmdbuff.setLength(0);
      }

      return res;
   }

   private synchronized int command_push(String cmd) throws YAPI_Exception {
      int res = 0;
      if (this._cmdbuff.length() + cmd.length() >= 100) {
         res = this.flush_now();
      }

      if (this._cmdbuff.length() == 0) {
         this._cmdbuff.append(this._id);
      }

      this._cmdbuff.append(cmd);
      return res;
   }

   private synchronized int command_flush(String cmd) throws YAPI_Exception {
      int res = this.command_push(cmd);
      return this._hidden ? res : this.flush_now();
   }

   public YDisplayLayer(YDisplay parent, int id) {
      this._display = parent;
      this._id = id;
      this._cmdbuff = new StringBuilder(128);
      this._hidden = false;
   }

   public int reset() throws YAPI_Exception {
      this._hidden = false;
      return this.command_flush("X");
   }

   public int clear() throws YAPI_Exception {
      return this.command_flush("x");
   }

   public int selectColorPen(int color) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "c%06x", color));
   }

   public int selectGrayPen(int graylevel) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "g%d", graylevel));
   }

   public int selectEraser() throws YAPI_Exception {
      return this.command_push("e");
   }

   public int setAntialiasingMode(boolean mode) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "a%d", mode ? 1 : 0));
   }

   public int drawPixel(int x, int y) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "P%d,%d", x, y));
   }

   public int drawRect(int x1, int y1, int x2, int y2) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "R%d,%d,%d,%d", x1, y1, x2, y2));
   }

   public int drawBar(int x1, int y1, int x2, int y2) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "B%d,%d,%d,%d", x1, y1, x2, y2));
   }

   public int drawCircle(int x, int y, int r) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "C%d,%d,%d", x, y, r));
   }

   public int drawDisc(int x, int y, int r) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "D%d,%d,%d", x, y, r));
   }

   public int selectFont(String fontname) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "&%s%c", fontname, 27));
   }

   public int drawText(int x, int y, YDisplayLayer.ALIGN anchor, String text) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "T%d,%d,%d,%s%c", x, y, anchor.value, text, 27));
   }

   public int drawImage(int x, int y, String imagename) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "*%d,%d,%s%c", x, y, imagename, 27));
   }

   public int drawBitmap(int x, int y, int w, byte[] bitmap, int bgcol) throws YAPI_Exception {
      String destname = String.format(Locale.US, "layer%d:%d,%d@%d,%d", this._id, w, bgcol, x, y);
      return this._display.upload(destname, bitmap);
   }

   public int moveTo(int x, int y) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "@%d,%d", x, y));
   }

   public int lineTo(int x, int y) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "-%d,%d", x, y));
   }

   public int consoleOut(String text) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "!%s%c", text, 27));
   }

   public int setConsoleMargins(int x1, int y1, int x2, int y2) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "m%d,%d,%d,%d", x1, y1, x2, y2));
   }

   public int setConsoleBackground(int bgcol) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "b%d", bgcol));
   }

   public int setConsoleWordWrap(boolean wordwrap) throws YAPI_Exception {
      return this.command_push(String.format(Locale.US, "w%d", wordwrap ? 1 : 0));
   }

   public int clearConsole() throws YAPI_Exception {
      return this.command_flush("^");
   }

   public int setLayerPosition(int x, int y, int scrollTime) throws YAPI_Exception {
      return this.command_flush(String.format(Locale.US, "#%d,%d,%d", x, y, scrollTime));
   }

   public int hide() throws YAPI_Exception {
      this.command_push("h");
      this._hidden = true;
      return this.flush_now();
   }

   public int unhide() throws YAPI_Exception {
      this._hidden = false;
      return this.command_flush("s");
   }

   public YDisplay get_display() {
      return this._display;
   }

   public int get_displayWidth() throws YAPI_Exception {
      return this._display.get_displayWidth();
   }

   public int get_displayHeight() throws YAPI_Exception {
      return this._display.get_displayHeight();
   }

   public int get_layerWidth() throws YAPI_Exception {
      return this._display.get_layerWidth();
   }

   public int get_layerHeight() throws YAPI_Exception {
      return this._display.get_layerHeight();
   }

   public int resetHiddenFlag() {
      this._hidden = false;
      return 0;
   }

   public static enum ALIGN {
      TOP_LEFT(0),
      CENTER_LEFT(1),
      BASELINE_LEFT(2),
      BOTTOM_LEFT(3),
      TOP_CENTER(4),
      CENTER(5),
      BASELINE_CENTER(6),
      BOTTOM_CENTER(7),
      TOP_DECIMAL(8),
      CENTER_DECIMAL(9),
      BASELINE_DECIMAL(10),
      BOTTOM_DECIMAL(11),
      TOP_RIGHT(12),
      CENTER_RIGHT(13),
      BASELINE_RIGHT(14),
      BOTTOM_RIGHT(15);

      public final int value;

      private ALIGN(int val) {
         this.value = val;
      }

      public static YDisplayLayer.ALIGN fromInt(int intval) {
         switch (intval) {
            case 0:
               return TOP_LEFT;
            case 1:
               return CENTER_LEFT;
            case 2:
               return BASELINE_LEFT;
            case 3:
               return BOTTOM_LEFT;
            case 4:
               return TOP_CENTER;
            case 5:
               return CENTER;
            case 6:
               return BASELINE_CENTER;
            case 7:
               return BOTTOM_CENTER;
            case 8:
               return TOP_DECIMAL;
            case 9:
               return CENTER_DECIMAL;
            case 10:
               return BASELINE_DECIMAL;
            case 11:
               return BOTTOM_DECIMAL;
            case 12:
               return TOP_RIGHT;
            case 13:
               return CENTER_RIGHT;
            case 14:
               return BASELINE_RIGHT;
            case 15:
               return BOTTOM_RIGHT;
            default:
               return null;
         }
      }
   }
}
