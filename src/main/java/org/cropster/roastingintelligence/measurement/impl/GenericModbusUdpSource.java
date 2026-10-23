package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusUDPTransaction;
import com.ghgande.j2mod.modbus.net.UDPMasterConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericModbusUdpSource extends GenericModbusSource {
   private final Logger logger = LoggerFactory.getLogger(GenericModbusUdpSource.class);
   private final UDPMasterConnection _connection;

   public GenericModbusUdpSource(
      MeasurementSourceFactory factory, InetAddress ipAddress, int port, Set<GenericModbusSource.ModbusConfig> targets, boolean bigEndian
   ) throws UnknownHostException {
      super(factory, targets, bigEndian);
      InetAddress localAddress = InetAddress.getLocalHost();

      try {
         for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            for (InterfaceAddress ifaceAddress : iface.getInterfaceAddresses()) {
               if (ifaceAddress.getNetworkPrefixLength() != 0) {
                  byte[] ifaceSubnetMask = NetworkUtil.getSubnetMask(ifaceAddress.getAddress().getAddress(), ifaceAddress.getNetworkPrefixLength());
                  byte[] subnetMask = NetworkUtil.getSubnetMask(ipAddress.getAddress(), ifaceAddress.getNetworkPrefixLength());
                  if (Arrays.equals(ifaceSubnetMask, subnetMask)) {
                     localAddress = ifaceAddress.getAddress();
                     break;
                  }
               }
            }
         }
      } catch (Exception var13) {
         this.logger.error("Error determining local address", var13);
      }

      this.logger.info("Using local address {}", localAddress);
      this._connection = new UDPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
   }

   @Override
   public void open() {
      try {
         this._connection.connect();
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening Modbus connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isConnected();
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
      this.fireStatusChanged();
   }

   protected ModbusUDPTransaction createModbusTransaction() {
      return new ModbusUDPTransaction(this._connection);
   }
}
