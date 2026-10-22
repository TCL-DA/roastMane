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

public class ModbusASCIITransport extends ModbusSerialTransport {
   private static final Logger logger = LoggerFactory.getLogger(ModbusASCIITransport.class);
   private static final String I_O_EXCEPTION_SERIAL_PORT_TIMEOUT = "I/O exception - Serial port timeout";
   private final byte[] inBuffer = new byte[256];
   private final BytesInputStream byteInputStream = new BytesInputStream(this.inBuffer);
   private final BytesOutputStream byteInputOutputStream = new BytesOutputStream(this.inBuffer);
   private final BytesOutputStream byteOutputStream = new BytesOutputStream(256);

   @Override
   protected void writeMessageOut(ModbusMessage msg) throws ModbusIOException {
      try {
         synchronized (this.byteOutputStream) {
            msg.setHeadless();
            msg.writeTo(this.byteOutputStream);
            byte[] buf = this.byteOutputStream.getBuffer();
            int len = this.byteOutputStream.size();
            this.writeAsciiByte(1000);
            this.writeAsciiBytes(buf, (long)len);
            if (logger.isDebugEnabled()) {
               logger.debug("Writing: {}", ModbusUtil.toHex(buf, 0, len));
            }

            this.writeAsciiByte(calculateLRC(buf, 0, len));
            this.writeAsciiByte(2000);
            this.byteOutputStream.reset();
            if (this.echo) {
               this.readEcho(len + 3);
            }
         }
      } catch (IOException var7) {
         throw new ModbusIOException("I/O failed to write");
      }
   }

   @Override
   public ModbusRequest readRequestIn(AbstractModbusListener listener) throws ModbusIOException {
      boolean done = false;
      ModbusRequest request = null;

      try {
         while (true) {
            if (this.readAsciiByte() == 1000) {
               label48: {
                  synchronized (this.inBuffer) {
                     this.byteInputOutputStream.reset();

                     int in;
                     while ((in = this.readAsciiByte()) != 2000) {
                        if (in == -1) {
                           throw new IOException("I/O exception - Serial port timeout");
                        }

                        this.byteInputOutputStream.writeByte(in);
                     }

                     if (this.inBuffer[this.byteInputOutputStream.size() - 1] != calculateLRC(this.inBuffer, 0, this.byteInputOutputStream.size(), 1)) {
                        break label48;
                     }

                     this.byteInputStream.reset(this.inBuffer, this.byteInputOutputStream.size());
                     this.byteInputStream.readUnsignedByte();
                     int functionCode = this.byteInputStream.readUnsignedByte();
                     request = ModbusRequest.createModbusRequest(functionCode);
                     request.setHeadless();
                     this.byteInputStream.reset(this.inBuffer, this.byteInputOutputStream.size());
                     request.readFrom(this.byteInputStream);
                  }

                  done = true;
               }

               if (done) {
                  return request;
               }
            }
         }
      } catch (Exception var9) {
         logger.debug(var9.getMessage());
         throw new ModbusIOException("I/O exception - failed to read");
      }
   }

   @Override
   protected ModbusResponse readResponseIn() throws ModbusIOException {
      boolean done = false;
      ModbusResponse response = null;

      try {
         int in;
         do {
            while ((in = this.readAsciiByte()) == 1000) {
               label52: {
                  synchronized (this.inBuffer) {
                     this.byteInputOutputStream.reset();

                     while ((in = this.readAsciiByte()) != 2000) {
                        if (in == -1) {
                           throw new IOException("I/O exception - Serial port timeout");
                        }

                        this.byteInputOutputStream.writeByte(in);
                     }

                     int len = this.byteInputOutputStream.size();
                     if (logger.isDebugEnabled()) {
                        logger.debug("Received: {}", ModbusUtil.toHex(this.inBuffer, 0, len));
                     }

                     if (this.inBuffer[len - 1] != calculateLRC(this.inBuffer, 0, len, 1)) {
                        break label52;
                     }

                     this.byteInputStream.reset(this.inBuffer, this.byteInputOutputStream.size());
                     this.byteInputStream.readUnsignedByte();
                     in = this.byteInputStream.readUnsignedByte();
                     response = ModbusResponse.createModbusResponse(in);
                     response.setHeadless();
                     this.byteInputStream.reset(this.inBuffer, this.byteInputOutputStream.size());
                     response.readFrom(this.byteInputStream);
                  }

                  done = true;
               }

               if (done) {
                  return response;
               }
            }
         } while (in != -1);

         throw new IOException("I/O exception - Serial port timeout");
      } catch (Exception var8) {
         logger.debug(var8.getMessage());
         throw new ModbusIOException("I/O exception - failed to read");
      }
   }

   private static int calculateLRC(byte[] data, int off, int length) {
      return calculateLRC(data, off, length, 0);
   }

   private static byte calculateLRC(byte[] data, int off, int length, int tailskip) {
      int lrc = 0;

      for (int i = off; i < length - tailskip; i++) {
         lrc += data[i] & 255;
      }

      return (byte)(-lrc & 0xFF);
   }
}
