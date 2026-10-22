package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

class WSStream {
   public static int MAX_DATA_LEN = 124;
   private final int _channel;
   private final int _type;
   private final int _contentLen;
   private final byte[] _data;

   WSStream(int type, int channel, int size, ByteBuffer requestBB) {
      this._channel = channel;
      this._type = type;
      this._contentLen = size;
      this._data = new byte[this._contentLen + 1];
      this._data[0] = (byte)((this._type << 3) + channel & 0xFF);
      if (size > 0) {
         requestBB.get(this._data, 1, size);
      }
   }

   WSStream(int type, int channel, int size, ByteBuffer requestBB, byte lastByte) {
      this._channel = channel;
      this._type = type;
      this._contentLen = size + 1;
      this._data = new byte[this._contentLen + 1];
      this._data[0] = (byte)((this._type << 3) + channel & 0xFF);
      if (size > 0) {
         requestBB.get(this._data, 1, size);
      }

      this._data[size + 1] = lastByte;
   }

   public byte[] getData() {
      return this._data;
   }

   public int getType() {
      return this._type;
   }

   @Override
   public String toString() {
      String content = new String(this._data, 1, this._contentLen, Charset.forName("ISO-8859-1"));
      String type = "unk";
      switch (this._type) {
         case 0:
            type = "EMPTY";
            break;
         case 1:
            type = "TCP";
            break;
         case 2:
            type = "TCP_CLOSE";
            break;
         case 3:
            type = "NOTICE";
            break;
         case 4:
            type = "REPORT";
            break;
         case 5:
            type = "META";
            break;
         case 6:
            type = "REPORT_V2";
            break;
         case 7:
            type = "NOTICE_V2";
            break;
         case 8:
            type = "TCP_NOTIF";
            break;
         case 9:
            type = "TCP_ASYNCCLOSE";
      }

      return String.format(Locale.US, "[c=%d t=%s l=%d]:[%s]", this._channel, type, this._contentLen, content);
   }

   public int getContentLen() {
      return this._contentLen;
   }

   public void getContent(ByteBuffer bb) {
      bb.put(this._data, 1, this._contentLen);
   }

   ByteBuffer getContent() {
      return ByteBuffer.wrap(this._data);
   }
}
