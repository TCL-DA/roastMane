package com.ghgande.j2mod.modbus.util;

import java.util.Properties;

public class SerialParameters {
   private static final boolean DEFAULT_RS485_MODE = false;
   private static final boolean DEFAULT_RS485_TX_ENABLE_ACTIVE_HIGH = true;
   private static final boolean DEFAULT_RS485_ENABLE_TERMINATION = false;
   private static final boolean DEFAULT_RS485_TX_DURING_RX = false;
   private static final int DEFAULT_RS485_DELAY_BEFORE_TX_MICROSECONDS = 1000;
   private static final int DEFAULT_RS485_DELAY_AFTER_TX_MICROSECONDS = 1000;
   private String portName;
   private int baudRate;
   private int flowControlIn;
   private int flowControlOut;
   private int databits;
   private int stopbits;
   private int parity;
   private String encoding;
   private boolean echo;
   private int openDelay;
   private boolean rs485Mode;
   private boolean rs485TxEnableActiveHigh;
   private boolean rs485EnableTermination;
   private boolean rs485RxDuringTx;
   private int rs485DelayBeforeTxMicroseconds;
   private int rs485DelayAfterTxMicroseconds;

   public SerialParameters() {
      this.portName = "";
      this.baudRate = 9600;
      this.flowControlIn = 0;
      this.flowControlOut = 0;
      this.databits = 8;
      this.stopbits = 1;
      this.parity = 0;
      this.encoding = "rtu";
      this.echo = false;
      this.openDelay = 0;
      this.rs485Mode = false;
      this.rs485TxEnableActiveHigh = true;
      this.rs485DelayBeforeTxMicroseconds = 1000;
      this.rs485DelayAfterTxMicroseconds = 1000;
   }

   public SerialParameters(String portName, int baudRate, int flowControlIn, int flowControlOut, int databits, int stopbits, int parity, boolean echo) {
      this();
      this.portName = portName;
      this.baudRate = baudRate;
      this.flowControlIn = flowControlIn;
      this.flowControlOut = flowControlOut;
      this.databits = databits;
      this.stopbits = stopbits;
      this.parity = parity;
      this.echo = echo;
   }

   public SerialParameters(
      String portName,
      int baudRate,
      int flowControlIn,
      int flowControlOut,
      int databits,
      int stopbits,
      int parity,
      boolean echo,
      boolean rs485Mode,
      boolean rs485TxEnableActiveHigh,
      int rs485DelayBeforeTxMicroseconds,
      int rs485DelayAfterTxMicroseconds
   ) {
      this(portName, baudRate, flowControlIn, flowControlOut, databits, stopbits, parity, echo);
      this.rs485Mode = rs485Mode;
      this.rs485TxEnableActiveHigh = rs485TxEnableActiveHigh;
      this.rs485DelayBeforeTxMicroseconds = rs485DelayBeforeTxMicroseconds;
      this.rs485DelayAfterTxMicroseconds = rs485DelayAfterTxMicroseconds;
   }

   public SerialParameters(Properties props, String prefix) {
      if (prefix == null) {
         prefix = "";
      }

      this.setPortName(props.getProperty(prefix + "portName", ""));
      this.setBaudRate(props.getProperty(prefix + "baudRate", "9600"));
      this.setFlowControlIn(props.getProperty(prefix + "flowControlIn", "0"));
      this.setFlowControlOut(props.getProperty(prefix + "flowControlOut", "0"));
      this.setParity(props.getProperty(prefix + "parity", "0"));
      this.setDatabits(props.getProperty(prefix + "databits", "8"));
      this.setStopbits(props.getProperty(prefix + "stopbits", "1"));
      this.setEncoding(props.getProperty(prefix + "encoding", "ascii"));
      this.setEcho("true".equals(props.getProperty(prefix + "echo")));
      this.setOpenDelay(props.getProperty(prefix + "openDelay", "0"));
      this.setRs485Mode("true".equals(props.getProperty(prefix + "rs485Mode", Boolean.toString(false))));
      this.setRs485TxEnableActiveHigh("true".equals(props.getProperty(prefix + "rs485TxEnableActiveHigh", Boolean.toString(true))));
      this.setRs485DelayBeforeTxMicroseconds(props.getProperty(prefix + "rs485DelayBeforeTxMicroseconds", Integer.toString(1000)));
      this.setRs485DelayAfterTxMicroseconds(props.getProperty(prefix + "rs485DelayAfterTxMicroseconds", Integer.toString(1000)));
   }

   public String getPortName() {
      return this.portName;
   }

   public void setPortName(String name) {
      this.portName = name;
   }

   public void setBaudRate(int rate) {
      this.baudRate = rate;
   }

   public int getBaudRate() {
      return this.baudRate;
   }

   public void setBaudRate(String rate) {
      this.baudRate = Integer.parseInt(rate);
   }

   public String getBaudRateString() {
      return Integer.toString(this.baudRate);
   }

   public void setFlowControlIn(int flowcontrol) {
      this.flowControlIn = flowcontrol;
   }

   public int getFlowControlIn() {
      return this.flowControlIn;
   }

   public void setFlowControlIn(String flowcontrol) {
      this.flowControlIn = this.stringToFlow(flowcontrol);
   }

   public String getFlowControlInString() {
      return this.flowToString(this.flowControlIn);
   }

   public void setFlowControlOut(int flowControlOut) {
      this.flowControlOut = flowControlOut;
   }

   public int getFlowControlOut() {
      return this.flowControlOut;
   }

   public void setFlowControlOut(String flowControlOut) {
      this.flowControlOut = this.stringToFlow(flowControlOut);
   }

   public String getFlowControlOutString() {
      return this.flowToString(this.flowControlOut);
   }

   public void setDatabits(int databits) {
      this.databits = databits;
   }

   public int getDatabits() {
      return this.databits;
   }

   public void setDatabits(String databits) {
      if (!ModbusUtil.isBlank(databits) && databits.matches("[0-9]+")) {
         this.databits = Integer.parseInt(databits);
      } else {
         this.databits = 8;
      }
   }

   public String getDatabitsString() {
      return this.databits + "";
   }

   public void setStopbits(int stopbits) {
      this.stopbits = stopbits;
   }

   public int getStopbits() {
      return this.stopbits;
   }

   public void setStopbits(String stopbits) {
      if (ModbusUtil.isBlank(stopbits) || stopbits.equals("1")) {
         this.stopbits = 1;
      } else if (stopbits.equals("1.5")) {
         this.stopbits = 2;
      } else if (stopbits.equals("2")) {
         this.stopbits = 3;
      }
   }

   public String getStopbitsString() {
      switch (this.stopbits) {
         case 1:
            return "1";
         case 2:
            return "1.5";
         case 3:
            return "2";
         default:
            return "1";
      }
   }

   public void setParity(int parity) {
      this.parity = parity;
   }

   public int getParity() {
      return this.parity;
   }

   public void setParity(String parity) {
      if (ModbusUtil.isBlank(parity) || parity.equalsIgnoreCase("none")) {
         this.parity = 0;
      } else if (parity.equalsIgnoreCase("even")) {
         this.parity = 2;
      } else if (parity.equalsIgnoreCase("odd")) {
         this.parity = 1;
      } else if (parity.equalsIgnoreCase("mark")) {
         this.parity = 3;
      } else if (parity.equalsIgnoreCase("space")) {
         this.parity = 4;
      } else {
         this.parity = 0;
      }
   }

   public String getParityString() {
      switch (this.parity) {
         case 0:
            return "none";
         case 1:
            return "odd";
         case 2:
            return "even";
         case 3:
            return "mark";
         case 4:
            return "space";
         default:
            return "none";
      }
   }

   public String getEncoding() {
      return this.encoding;
   }

   public void setEncoding(String enc) {
      if (ModbusUtil.isBlank(enc) || !enc.equalsIgnoreCase("ascii") && !enc.equalsIgnoreCase("rtu")) {
         this.encoding = "ascii";
      } else {
         this.encoding = enc;
      }
   }

   public boolean isEcho() {
      return this.echo;
   }

   public void setEcho(boolean newEcho) {
      this.echo = newEcho;
   }

   private int stringToFlow(String flowcontrol) {
      if (ModbusUtil.isBlank(flowcontrol) || flowcontrol.equalsIgnoreCase("none")) {
         return 0;
      } else if (flowcontrol.equalsIgnoreCase("xon/xoff out")) {
         return 1048576;
      } else if (flowcontrol.equalsIgnoreCase("xon/xoff in")) {
         return 65536;
      } else if (flowcontrol.equalsIgnoreCase("rts/cts")) {
         return 17;
      } else {
         return flowcontrol.equalsIgnoreCase("dsr/dtr") ? 4352 : 0;
      }
   }

   private String flowToString(int flowcontrol) {
      switch (flowcontrol) {
         case 0:
            return "none";
         case 16:
            return "rts/cts";
         case 4096:
            return "dsr/dtr";
         case 65536:
            return "xon/xoff in";
         case 1048576:
            return "xon/xoff out";
         default:
            return "none";
      }
   }

   public int getOpenDelay() {
      return this.openDelay;
   }

   public void setOpenDelay(int openDelay) {
      this.openDelay = openDelay;
   }

   public void setOpenDelay(String openDelay) {
      this.openDelay = Integer.parseInt(openDelay);
   }

   public boolean getRs485Mode() {
      return this.rs485Mode;
   }

   public void setRs485Mode(boolean enable) {
      this.rs485Mode = enable;
   }

   public boolean getRs485TxEnableActiveHigh() {
      return this.rs485TxEnableActiveHigh;
   }

   public void setRs485TxEnableActiveHigh(boolean activeHigh) {
      this.rs485TxEnableActiveHigh = activeHigh;
   }

   public boolean getRs485EnableTermination() {
      return this.rs485EnableTermination;
   }

   public void setRs485EnableTermination(boolean enable) {
      this.rs485EnableTermination = enable;
   }

   public boolean getRs485RxDuringTx() {
      return this.rs485RxDuringTx;
   }

   public void setRs485RxDuringTx(boolean enable) {
      this.rs485RxDuringTx = enable;
   }

   public int getRs485DelayBeforeTxMicroseconds() {
      return this.rs485DelayBeforeTxMicroseconds;
   }

   public void setRs485DelayBeforeTxMicroseconds(int microseconds) {
      if (microseconds < 0) {
         throw new IllegalArgumentException("Expecting non-negative delay.");
      } else {
         this.rs485DelayBeforeTxMicroseconds = microseconds;
      }
   }

   public void setRs485DelayBeforeTxMicroseconds(String microseconds) {
      this.setRs485DelayBeforeTxMicroseconds(Integer.parseInt(microseconds));
   }

   public int getRs485DelayAfterTxMicroseconds() {
      return this.rs485DelayAfterTxMicroseconds;
   }

   public void setRs485DelayAfterTxMicroseconds(int microseconds) {
      if (microseconds < 0) {
         throw new IllegalArgumentException("Expecting non-negative delay.");
      } else {
         this.rs485DelayAfterTxMicroseconds = microseconds;
      }
   }

   public void setRs485DelayAfterTxMicroseconds(String microseconds) {
      this.setRs485DelayAfterTxMicroseconds(Integer.parseInt(microseconds));
   }

   @Override
   public String toString() {
      return "SerialParameters{portName='"
         + this.portName
         + '\''
         + ", baudRate="
         + this.baudRate
         + ", flowControlIn="
         + this.flowControlIn
         + ", flowControlOut="
         + this.flowControlOut
         + ", databits="
         + this.databits
         + ", stopbits="
         + this.stopbits
         + ", parity="
         + this.parity
         + ", encoding='"
         + this.encoding
         + '\''
         + ", echo="
         + this.echo
         + ", openDelay="
         + this.openDelay
         + ", rs485Mode="
         + this.rs485Mode
         + ", rs485TxEnableActiveHight="
         + this.rs485TxEnableActiveHigh
         + ", rs485EnableTermination="
         + this.rs485EnableTermination
         + ", rs485RxDuringTx"
         + this.rs485RxDuringTx
         + ", rs485DelayBeforeTxMicroseconds="
         + this.rs485DelayBeforeTxMicroseconds
         + ", rs485DelayAfterTxMicroseconds="
         + this.rs485DelayAfterTxMicroseconds
         + '}';
   }
}
