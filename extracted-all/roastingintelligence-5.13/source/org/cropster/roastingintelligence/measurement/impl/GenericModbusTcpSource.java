package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericModbusTcpSource extends GenericModbusSourceJ2mod {
   private final Logger logger = LoggerFactory.getLogger(GenericModbusTcpSource.class);
   private final TCPMasterConnection _connection;

   public GenericModbusTcpSource(
      MeasurementSourceFactory factory, InetAddress ipAddress, int port, Set<GenericModbusSource.ModbusConfig> targets, boolean bigEndian
   ) throws UnknownHostException {
      super(factory, targets, bigEndian);
      this._connection = new TCPMasterConnection(ipAddress);
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

   @Override
   protected ModbusTransaction createModbusTransaction() {
      return new ModbusTCPTransaction(this._connection);
   }
}
