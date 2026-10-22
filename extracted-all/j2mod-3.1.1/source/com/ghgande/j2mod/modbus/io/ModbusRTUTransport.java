package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusRTUTransport extends ModbusSerialTransport {
   private static final Logger logger = LoggerFactory.getLogger(ModbusRTUTransport.class);
   private final byte[] inBuffer = new byte[256];
   private final BytesInputStream byteInputStream = new BytesInputStream(this.inBuffer);
   private final BytesOutputStream byteInputOutputStream = new BytesOutputStream(this.inBuffer);
   private final BytesOutputStream byteOutputStream = new BytesOutputStream(256);
   private byte[] lastRequest = null;

   private void readRequestData(int byteCount, BytesOutputStream out) throws IOException {
      byteCount += 2;
      byte[] inpBuf = new byte[byteCount];
      this.readBytes(inpBuf, (long)byteCount);
      out.write(inpBuf, 0, byteCount);
   }

   private void getRequest(int function, BytesOutputStream out) throws IOException {
      byte[] inpBuf = new byte[256];

      try {
         if ((function & 128) == 0) {
            switch (function) {
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
                  this.readRequestData(4, out);
                  break;
               case 7:
               case 11:
               case 12:
               case 17:
                  this.readRequestData(0, out);
                  break;
               case 8:
               case 9:
               case 10:
               case 13:
               case 14:
               case 18:
               case 19:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 36:
               case 37:
               case 38:
               case 39:
               case 40:
               case 41:
               case 42:
               default:
                  throw new IOException(String.format("getResponse unrecognised function code [%s]", function));
               case 15:
               case 16: {
                  this.readBytes(inpBuf, 4L);
                  out.write(inpBuf, 0, 4);
                  int byteCount = this.readByte();
                  out.write(byteCount);
                  this.readRequestData(byteCount, out);
                  break;
               }
               case 20:
               case 21: {
                  int byteCount = this.readByte();
                  out.write(byteCount);
                  this.readRequestData(byteCount, out);
                  break;
               }
               case 22:
                  this.readRequestData(6, out);
                  break;
               case 23: {
                  this.readRequestData(8, out);
                  int byteCount = this.readByte();
                  out.write(byteCount);
                  this.readRequestData(byteCount, out);
                  break;
               }
               case 24:
                  this.readRequestData(2, out);
                  break;
               case 43:
                  this.readRequestData(3, out);
            }
         }
      } catch (IOException var6) {
         throw new IOException("getResponse serial port exception");
      }
   }

   private void getResponse(int function, BytesOutputStream out) throws IOException {
      byte[] inpBuf = new byte[256];

      try {
         if ((function & 128) == 0) {
            switch (function) {
               case 1:
               case 2:
               case 3:
               case 4:
               case 12:
               case 17:
               case 20:
               case 21:
               case 23:
                  int cnt = this.readByte();
                  out.write(cnt);
                  this.readRequestData(cnt, out);
                  break;
               case 5:
               case 6:
               case 8:
               case 11:
               case 15:
               case 16:
                  this.readRequestData(4, out);
                  break;
               case 7:
                  this.readRequestData(1, out);
                  break;
               case 9:
               case 10:
               case 13:
               case 14:
               case 18:
               case 19:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 36:
               case 37:
               case 38:
               case 39:
               case 40:
               case 41:
               case 42:
               default:
                  throw new IOException(String.format("getResponse unrecognised function code [%s]", function));
               case 22:
                  this.readRequestData(6, out);
                  break;
               case 24:
                  int b1 = (byte)(this.readByte() & 0xFF);
                  out.write(b1);
                  int b2 = (byte)(this.readByte() & 0xFF);
                  out.write(b2);
                  int byteCount = ModbusUtil.makeWord(b1, b2);
                  this.readRequestData(byteCount, out);
                  break;
               case 43:
                  int sc = this.readByte();
                  if (sc != 14) {
                     throw new IOException("Invalid subfunction code");
                  }

                  out.write(sc);
                  this.readBytes(inpBuf, 5L);
                  out.write(inpBuf, 0, 5);
                  int fieldCount = inpBuf[4];

                  for (int i = 0; i < fieldCount; i++) {
                     int id = this.readByte();
                     out.write(id);
                     int len = this.readByte();
                     out.write(len);
                     this.readBytes(inpBuf, (long)len);
                     out.write(inpBuf, 0, len);
                  }

                  if (fieldCount == 0) {
                     int err = this.readByte();
                     out.write(err);
                  }

                  this.readRequestData(0, out);
            }
         } else {
            this.readRequestData(1, out);
         }
      } catch (IOException var13) {
         throw new IOException(String.format("getResponse serial port exception - %s", var13.getMessage()));
      }
   }

   @Override
   protected void writeMessageOut(ModbusMessage msg) throws ModbusIOException {
      try {
         synchronized (this.byteOutputStream) {
            this.clearInput();
            this.byteOutputStream.reset();
            msg.setHeadless();
            msg.writeTo(this.byteOutputStream);
            int len = this.byteOutputStream.size();
            int[] crc = ModbusUtil.calculateCRC(this.byteOutputStream.getBuffer(), 0, len);
            this.byteOutputStream.writeByte(crc[0]);
            this.byteOutputStream.writeByte(crc[1]);
            this.writeBytes(this.byteOutputStream.getBuffer(), (long)this.byteOutputStream.size());
            if (logger.isDebugEnabled()) {
               logger.debug("Sent: {}", ModbusUtil.toHex(this.byteOutputStream.getBuffer(), 0, this.byteOutputStream.size()));
            }

            if (this.echo) {
               this.readEcho(len + 2);
            }

            this.lastRequest = new byte[len];
            System.arraycopy(this.byteOutputStream.getBuffer(), 0, this.lastRequest, 0, len);
         }
      } catch (IOException var7) {
         throw new ModbusIOException("I/O failed to write");
      }
   }

   @Override
   protected ModbusRequest readRequestIn(AbstractModbusListener listener) throws ModbusIOException {
      ModbusRequest request = null;

      try {
         while (request == null) {
            synchronized (this.byteInputStream) {
               int uid = this.readByte();
               this.byteInputOutputStream.reset();
               this.byteInputOutputStream.writeByte(uid);
               if (listener.getProcessImage(uid) != null) {
                  int fc = this.readByte();
                  this.byteInputOutputStream.writeByte(fc);
                  request = ModbusRequest.createModbusRequest(fc);
                  request.setHeadless();
                  this.getRequest(fc, this.byteInputOutputStream);
                  int dlength = this.byteInputOutputStream.size() - 2;
                  if (logger.isDebugEnabled()) {
                     logger.debug("Request: {}", ModbusUtil.toHex(this.byteInputOutputStream.getBuffer(), 0, dlength + 2));
                  }

                  this.byteInputStream.reset(this.inBuffer, dlength);
                  int[] crc = ModbusUtil.calculateCRC(this.inBuffer, 0, dlength);
                  if (ModbusUtil.unsignedByteToInt(this.inBuffer[dlength]) == crc[0] && ModbusUtil.unsignedByteToInt(this.inBuffer[dlength + 1]) == crc[1]) {
                     this.byteInputStream.reset(this.inBuffer, dlength);
                     request.readFrom(this.byteInputStream);
                     return request;
                  }

                  if (logger.isDebugEnabled()) {
                     logger.debug("CRC should be {}, {}", Integer.toHexString(crc[0]), Integer.toHexString(crc[1]));
                  }

                  this.clearInput();
                  throw new IOException("CRC Error in received frame: " + dlength + " bytes: " + ModbusUtil.toHex(this.byteInputStream.getBuffer(), 0, dlength));
               }

               while (true) {
                  boolean bytesAvailable = this.availableBytes() > 0;
                  if (!bytesAvailable) {
                     if (logger.isDebugEnabled()) {
                        logger.debug("Waiting for {} microsec", this.getMaxCharDelay());
                     }

                     bytesAvailable = this.spinUntilBytesAvailable(this.getMaxCharDelay());
                  }

                  if (!bytesAvailable) {
                     if (logger.isDebugEnabled()) {
                        logger.debug("Waiting for {} microsec", this.getCharIntervalMicro(2.0));
                     }

                     if (this.spinUntilBytesAvailable(this.getCharIntervalMicro(2.0))) {
                        if (logger.isDebugEnabled()) {
                           logger.debug(
                              "Discarding message (More than 1.5t between characters!) - {}",
                              ModbusUtil.toHex(this.byteInputOutputStream.getBuffer(), 0, this.byteInputOutputStream.size())
                           );
                        }
                     } else if (logger.isDebugEnabled()) {
                        logger.debug(
                           "Read message not meant for us: {}", ModbusUtil.toHex(this.byteInputOutputStream.getBuffer(), 0, this.byteInputOutputStream.size())
                        );
                     }
                     break;
                  }

                  while (this.availableBytes() > 0) {
                     this.byteInputOutputStream.writeByte(this.readByte());
                  }
               }
            }
         }

         return null;
      } catch (IOException var10) {
         if (logger.isDebugEnabled()) {
            logger.debug("Failed to read response! {}", var10.getMessage());
         }

         return null;
      }
   }

   @Override
   protected ModbusResponse readResponseIn() throws ModbusIOException {
      try {
         boolean done;
         ModbusResponse response;
         do {
            synchronized (this.byteInputStream) {
               int uid = this.readByte();
               if (uid == -1) {
                  throw new IOException("Error reading response");
               }

               int fc = this.readByte();
               this.byteInputOutputStream.reset();
               this.byteInputOutputStream.writeByte(uid);
               this.byteInputOutputStream.writeByte(fc);
               response = ModbusResponse.createModbusResponse(fc);
               response.setHeadless();
               this.getResponse(fc, this.byteInputOutputStream);
               int dlength = this.byteInputOutputStream.size() - 2;
               if (logger.isDebugEnabled()) {
                  logger.debug("Response: {}", ModbusUtil.toHex(this.byteInputOutputStream.getBuffer(), 0, dlength + 2));
               }

               this.byteInputStream.reset(this.inBuffer, dlength);
               int[] crc = ModbusUtil.calculateCRC(this.inBuffer, 0, dlength);
               if (ModbusUtil.unsignedByteToInt(this.inBuffer[dlength]) != crc[0] || ModbusUtil.unsignedByteToInt(this.inBuffer[dlength + 1]) != crc[1]) {
                  logger.debug("CRC should be {}, {}", crc[0], crc[1]);
                  throw new IOException("CRC Error in received frame: " + dlength + " bytes: " + ModbusUtil.toHex(this.byteInputStream.getBuffer(), 0, dlength));
               }

               this.byteInputStream.reset(this.inBuffer, dlength);
               response.readFrom(this.byteInputStream);
               done = true;
            }
         } while (!done);

         return response;
      } catch (IOException var10) {
         throw new ModbusIOException("I/O exception - failed to read response for request [%s] - %s", ModbusUtil.toHex(this.lastRequest), var10.getMessage());
      }
   }
}
