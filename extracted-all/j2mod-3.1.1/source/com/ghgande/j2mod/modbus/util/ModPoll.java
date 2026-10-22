package com.ghgande.j2mod.modbus.util;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.facade.ModbusUDPMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import java.util.HashSet;
import java.util.Set;

public class ModPoll {
   public static void main(String[] args) {
      try {
         ModPoll.CommandLineParams params = new ModPoll.CommandLineParams(args);
         if ("ascii".equalsIgnoreCase(params.mode) || "rtu".equalsIgnoreCase(params.mode)) {
            mainSerial(params);
         } else if ("tcp".equalsIgnoreCase(params.mode) || "enc".equalsIgnoreCase(params.mode)) {
            mainTCP(params);
         } else if ("udp".equalsIgnoreCase(params.mode)) {
            mainUDP(params);
         }
      } catch (ModbusException var2) {
         System.out.println(var2.getMessage());
         showHelp();
      }
   }

   private static void mainSerial(ModPoll.CommandLineParams params) {
      SerialParameters parameters = new SerialParameters();
      parameters.setPortName(params.portname);
      parameters.setOpenDelay(1000);
      parameters.setEncoding("ascii".equalsIgnoreCase(params.mode) ? "ascii" : "rtu");
      parameters.setStopbits(params.stopBits);
      parameters.setParity(params.parity);
      parameters.setBaudRate(params.baudRate);
      parameters.setDatabits(params.dataBits);
      parameters.setFlowControlOut(0);
      parameters.setFlowControlIn(0);
      parameters.setEcho(false);
      parameters.setOpenDelay(0);
      ModbusSerialMaster master = new ModbusSerialMaster(parameters);
      execute(params, master);
   }

   private static void mainTCP(ModPoll.CommandLineParams params) {
      ModbusTCPMaster master = new ModbusTCPMaster(params.portname, params.port, "enc".equalsIgnoreCase(params.mode));
      execute(params, master);
   }

   private static void mainUDP(ModPoll.CommandLineParams params) throws ModbusException {
      if (params.portname == null) {
         throw new ModbusException("ERROR - you must specify a host name or IP address");
      } else {
         ModbusUDPMaster master = new ModbusUDPMaster(params.portname, params.port);
         execute(params, master);
      }
   }

   private static void execute(ModPoll.CommandLineParams params, AbstractModbusMaster master) {
      boolean first = true;

      while (params.continuous || first) {
         long startTime = System.currentTimeMillis();
         first = false;

         try {
            master.connect();
            System.out.println("-- Polling slave... (Ctrl-C to stop)");
            if (params.type == 1) {
               BitVector vector = master.readInputDiscretes(params.unit, params.reference, params.count);
               System.out.println(vector.toString());
            } else if (params.type == 3) {
               InputRegister[] regs = master.readInputRegisters(params.unit, params.reference, params.count);
               int cnt = 0;

               for (InputRegister reg : regs) {
                  System.out.printf("[%04d]: %s (%d)%n", params.reference + cnt, ModbusUtil.toHex(reg.toBytes()), reg.toUnsignedShort());
                  cnt++;
               }
            } else if (params.type == 4) {
               Register[] regs = master.readMultipleRegisters(params.unit, params.reference, params.count);
               int cnt = 0;

               for (Register reg : regs) {
                  System.out.printf("[%04d]: %s (%d)%n", params.reference + cnt, ModbusUtil.toHex(reg.toBytes()), reg.toUnsignedShort());
                  cnt++;
               }
            }
         } catch (Exception var14) {
            System.out.printf("ERROR - %s%n", var14.getMessage());
         } finally {
            master.disconnect();
         }

         if (params.continuous) {
            long sleepTime = (long)params.rate - (System.currentTimeMillis() - startTime);
            if (sleepTime > 0L) {
               ModbusUtil.sleep(sleepTime);
            }
         }
      }
   }

   private static void showHelp() {
      System.out.println("Usage: modpoll [OPTIONS] SERIALPORT|HOST");
      System.out.println("Arguments:");
      System.out.println("SERIALPORT    Serial port when using Modbus ASCII or Modbus RTU protocol");
      System.out.println("              COM1, COM2 ...                on Windows");
      System.out.println("              /dev/ttyS0, /dev/ttyS1 ...    on Linux");
      System.out.println("HOST          Host name or dotted IP address when using MODBUS/TCP protocol");
      System.out.println();
      System.out.println("General options:");
      System.out.println("-m ascii      Modbus ASCII protocol");
      System.out.println("-m rtu        Modbus RTU protocol (default if SERIALPORT contains /, \\ or COM)");
      System.out.println("-m tcp        MODBUS/TCP protocol (default otherwise)");
      System.out.println("-m udp        MODBUS UDP");
      System.out.println("-m enc        Encapsulated Modbus RTU over TCP");
      System.out.println("-a #          Slave address (1-255 for serial, 0-255 for TCP, 1 is default)\n");
      System.out.println("-r #          Start reference (1-65536, 1 is default)");
      System.out.println("-c #          Number of values to poll (1-125, 1 is default)");
      System.out.println("-t 1          Discrete input data type");
      System.out.println("-t 3          16-bit input register data type");
      System.out.println("-t 3:hex      16-bit input register data type with hex display");
      System.out.println("-t 3:int      32-bit integer data type in input register table");
      System.out.println("-t 3:mod      32-bit module 10000 data type in input register table");
      System.out.println("-t 3:float    32-bit float data type in input register table");
      System.out.println("-t 4          16-bit output (holding) register data type (default)");
      System.out.println("-t 4:hex      16-bit output (holding) register data type with hex display");
      System.out.println("-t 4:int      32-bit integer data type in output (holding) register table");
      System.out.println("-t 4:mod      32-bit module 10000 type in output (holding) register table");
      System.out.println("-t 4:float    32-bit float data type in output (holding) register table");
      System.out.println("-i            Slave operates on big-endian 32-bit integers");
      System.out.println("-f            Slave operates on big-endian 32-bit floats");
      System.out.println("-1            Poll only once only, otherwise every poll rate interval");
      System.out.println("-l            Poll rate in ms, (1000 is default)");
      System.out.println("-o #          Time-out in seconds (0.01 - 10.0, 1.0 s is default)");
      System.out.println();
      System.out.println("Options for MODBUS/TCP, UDP and RTU over TCP:");
      System.out.println("-p #          IP protocol port number (502 is default)");
      System.out.println();
      System.out.println("Options for Modbus ASCII and Modbus RTU:");
      System.out.println("-b #          Baudrate (e.g. 9600, 19200, ...) (19200 is default)");
      System.out.println("-d #          Databits (7 or 8 for ASCII protocol, 8 for RTU)");
      System.out.println("-s #          Stopbits (1 or 2, 1 is default)");
      System.out.println("-p none       No parity");
      System.out.println("-p even       Even parity (default)");
      System.out.println("-p odd        Odd parity");
   }

   private static class CommandLineParams {
      static final String MODE_ASCII = "ascii";
      static final String MODE_RTU = "rtu";
      static final String MODE_TCP = "tcp";
      static final String MODE_UDP = "udp";
      static final String MODE_ENC = "enc";
      private static final Set<String> ACCEPTABLE_MODES = new HashSet<>();
      private static final String ACCEPTABLE_MODES_DISPLAY = String.format("%s,%s,%s,%s,%s", "ascii", "rtu", "tcp", "udp", "enc");
      String portname = null;
      int parity = 0;
      int rate = 1000;
      int unit = -1;
      int reference = 0;
      int count = 1;
      int type = 3;
      int baudRate = 9600;
      int dataBits = 8;
      int stopBits = 1;
      int port = 502;
      boolean continuous = true;
      String mode = "tcp";
      String displayType = null;
      private String portOrParity;

      public CommandLineParams(String[] args) throws ModbusException {
         for (int arg = 0; arg < args.length; arg++) {
            if (args[arg].equals("-m")) {
               this.mode = args[++arg].toLowerCase();
            } else if (args[arg].equals("-a")) {
               this.unit = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-r")) {
               this.reference = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-c")) {
               this.count = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-t")) {
               String tmp = args[++arg];
               if (tmp.contains(":")) {
                  this.type = Integer.parseInt(tmp.split(" *: *")[0]);
                  tmp = tmp.split(" *: *")[1];
                  if (!tmp.isEmpty()) {
                     this.displayType = tmp;
                  }
               } else {
                  this.type = Integer.parseInt(tmp);
               }
            } else if (args[arg].equals("-1")) {
               this.continuous = false;
            } else if (args[arg].equals("-l")) {
               this.rate = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-b")) {
               this.baudRate = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-d")) {
               this.dataBits = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-s")) {
               this.stopBits = Integer.parseInt(args[++arg]);
            } else if (args[arg].equals("-p")) {
               this.portOrParity = args[++arg];
            } else {
               this.portname = args[arg];
            }
         }

         boolean isSerialMode = "ascii".equalsIgnoreCase(this.mode) || "rtu".equalsIgnoreCase(this.mode);
         if (isSerialMode) {
            if ("none".equalsIgnoreCase(this.portOrParity)) {
               this.parity = 0;
            } else if ("even".equalsIgnoreCase(this.portOrParity)) {
               this.parity = 2;
            } else if ("odd".equalsIgnoreCase(this.portOrParity)) {
               this.parity = 1;
            }
         } else if (this.portOrParity != null) {
            this.port = Integer.parseInt(this.portOrParity);
         }

         if (this.portname == null) {
            if (isSerialMode) {
               throw new ModbusException("ERROR - you must specify a port name");
            } else {
               throw new ModbusException("ERROR - you must specify a host name or IP address");
            }
         } else if (this.unit < 0 || this.unit > 255) {
            throw new ModbusException("ERROR - you must specify a valid unit ID (1-255) e.g. -a 49");
         } else if (this.mode == null || !ACCEPTABLE_MODES.contains(this.mode)) {
            throw new ModbusException("ERROR - mode must be one of [%s]", ACCEPTABLE_MODES_DISPLAY);
         }
      }

      static {
         ACCEPTABLE_MODES.add("ascii");
         ACCEPTABLE_MODES.add("rtu");
         ACCEPTABLE_MODES.add("tcp");
         ACCEPTABLE_MODES.add("udp");
         ACCEPTABLE_MODES.add("enc");
      }
   }
}
