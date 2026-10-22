package com.ghgande.j2mod.modbus.slave;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModbusSlaveFactory {
   private static final Map<String, ModbusSlave> slaves = new HashMap<>();

   private ModbusSlaveFactory() {
   }

   public static synchronized ModbusSlave createTCPSlave(int port, int poolSize) throws ModbusException {
      return createTCPSlave(port, poolSize, false);
   }

   public static synchronized ModbusSlave createTCPSlave(int port, int poolSize, boolean useRtuOverTcp) throws ModbusException {
      return createTCPSlave(null, port, poolSize, useRtuOverTcp);
   }

   public static synchronized ModbusSlave createTCPSlave(InetAddress address, int port, int poolSize, boolean useRtuOverTcp) throws ModbusException {
      return createTCPSlave(address, port, poolSize, useRtuOverTcp, 0);
   }

   public static synchronized ModbusSlave createTCPSlave(InetAddress address, int port, int poolSize, boolean useRtuOverTcp, int maxIdleSeconds) throws ModbusException {
      String key = ModbusSlaveType.TCP.getKey(port);
      if (slaves.containsKey(key)) {
         return slaves.get(key);
      } else {
         ModbusSlave slave = new ModbusSlave(address, port, poolSize, useRtuOverTcp, maxIdleSeconds);
         slaves.put(key, slave);
         return slave;
      }
   }

   public static synchronized ModbusSlave createUDPSlave(int port) throws ModbusException {
      return createUDPSlave(null, port);
   }

   public static synchronized ModbusSlave createUDPSlave(InetAddress address, int port) throws ModbusException {
      String key = ModbusSlaveType.UDP.getKey(port);
      if (slaves.containsKey(key)) {
         return slaves.get(key);
      } else {
         ModbusSlave slave = new ModbusSlave(address, port, false);
         slaves.put(key, slave);
         return slave;
      }
   }

   public static synchronized ModbusSlave createSerialSlave(SerialParameters serialParams) throws ModbusException {
      ModbusSlave slave = null;
      if (serialParams == null) {
         throw new ModbusException("Serial parameters are null");
      } else if (ModbusUtil.isBlank(serialParams.getPortName())) {
         throw new ModbusException("Serial port name is empty");
      } else {
         if (slaves.containsKey(serialParams.getPortName())) {
            slave = slaves.get(serialParams.getPortName());
            if (!serialParams.toString().equals(slave.getSerialParams().toString())) {
               close(slave);
               slave = null;
            }
         }

         if (slave == null) {
            slave = new ModbusSlave(serialParams);
            slaves.put(serialParams.getPortName(), slave);
            return slave;
         } else {
            return slave;
         }
      }
   }

   public static void close(ModbusSlave slave) {
      if (slave != null) {
         slave.closeListener();
         slaves.remove(slave.getType().getKey(slave.getPort()));
      }
   }

   public static void close() {
      for (ModbusSlave slave : new ArrayList<>(slaves.values())) {
         slave.close();
      }
   }

   public static ModbusSlave getSlave(int port) {
      return slaves.get(port + "");
   }

   public static ModbusSlave getSlave(String port) {
      return ModbusUtil.isBlank(port) ? null : slaves.get(port);
   }

   public static synchronized ModbusSlave getSlave(AbstractModbusListener listener) {
      for (ModbusSlave slave : slaves.values()) {
         if (slave.getListener().equals(listener)) {
            return slave;
         }
      }

      return null;
   }
}
