package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import java.io.IOException;
import java.util.Set;

public abstract class AbstractSerialConnection {
   public static final int NO_PARITY = 0;
   public static final int ODD_PARITY = 1;
   public static final int EVEN_PARITY = 2;
   public static final int MARK_PARITY = 3;
   public static final int SPACE_PARITY = 4;
   public static final int ONE_STOP_BIT = 1;
   public static final int ONE_POINT_FIVE_STOP_BITS = 2;
   public static final int TWO_STOP_BITS = 3;
   public static final int FLOW_CONTROL_DISABLED = 0;
   public static final int FLOW_CONTROL_RTS_ENABLED = 1;
   public static final int FLOW_CONTROL_CTS_ENABLED = 16;
   public static final int FLOW_CONTROL_DSR_ENABLED = 256;
   public static final int FLOW_CONTROL_DTR_ENABLED = 4096;
   public static final int FLOW_CONTROL_XONXOFF_IN_ENABLED = 65536;
   public static final int FLOW_CONTROL_XONXOFF_OUT_ENABLED = 1048576;
   public static final int OPEN_DELAY = 0;
   public static final int TIMEOUT_NONBLOCKING = 0;
   public static final int TIMEOUT_READ_SEMI_BLOCKING = 1;
   public static final int TIMEOUT_READ_BLOCKING = 16;
   public static final int TIMEOUT_WRITE_BLOCKING = 256;
   public static final int TIMEOUT_SCANNER = 4096;

   public abstract void open() throws IOException;

   public abstract AbstractModbusTransport getModbusTransport();

   public abstract int readBytes(byte[] var1, long var2);

   public abstract int writeBytes(byte[] var1, long var2);

   public abstract int bytesAvailable();

   public abstract void close();

   public abstract int getBaudRate();

   public abstract int getNumDataBits();

   public abstract int getNumStopBits();

   public abstract int getParity();

   public abstract String getPortName();

   public abstract String getDescriptivePortName();

   public abstract void setComPortTimeouts(int var1, int var2, int var3);

   public abstract boolean isOpen();

   public abstract int getTimeout();

   public abstract void setTimeout(int var1);

   public abstract Set<String> getCommPorts();
}
