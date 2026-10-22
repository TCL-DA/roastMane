package com.fazecast.jSerialComm;

import java.util.EventObject;

public class SerialPortEvent extends EventObject {
   private static final long serialVersionUID = 3060830619653354150L;
   private final int eventType;
   private final byte[] serialData;

   public SerialPortEvent(SerialPort comPort, int serialEventType) {
      super(comPort);
      this.eventType = serialEventType;
      this.serialData = null;
   }

   public SerialPortEvent(SerialPort comPort, int serialEventType, byte[] data) {
      super(comPort);
      this.eventType = serialEventType;
      this.serialData = data;
   }

   @Override
   public final String toString() {
      switch (this.eventType) {
         case 1:
            return "LISTENING_EVENT_DATA_AVAILABLE";
         case 16:
            return "LISTENING_EVENT_DATA_RECEIVED";
         case 256:
            return "LISTENING_EVENT_DATA_WRITTEN";
         case 65536:
            return "LISTENING_EVENT_BREAK_INTERRUPT";
         case 131072:
            return "LISTENING_EVENT_CARRIER_DETECT";
         case 262144:
            return "LISTENING_EVENT_CTS";
         case 524288:
            return "LISTENING_EVENT_DSR";
         case 1048576:
            return "LISTENING_EVENT_RING_INDICATOR";
         case 2097152:
            return "LISTENING_EVENT_FRAMING_ERROR";
         case 4194304:
            return "LISTENING_EVENT_FIRMWARE_OVERRUN_ERROR";
         case 8388608:
            return "LISTENING_EVENT_SOFTWARE_OVERRUN_ERROR";
         case 16777216:
            return "LISTENING_EVENT_PARITY_ERROR";
         case 268435456:
            return "LISTENING_EVENT_PORT_DISCONNECTED";
         default:
            return "LISTENING_EVENT_UNKNOWN_TYPE";
      }
   }

   public final SerialPort getSerialPort() {
      return (SerialPort)this.source;
   }

   public final int getEventType() {
      return this.eventType;
   }

   public final byte[] getReceivedData() {
      return this.serialData;
   }
}
