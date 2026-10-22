package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericModbusRtuSource extends GenericModbusSourceJ2mod {
   private final Logger logger = LoggerFactory.getLogger(GenericModbusRtuSource.class);
   private final SerialConnection _connection;

   public GenericModbusRtuSource(
      GenericModbusRtuSourceFactory factory, SerialParameters parameters, Collection<GenericModbusSource.ModbusConfig> targets, boolean bigEndian
   ) {
      super(factory, targets, bigEndian);
      this._connection = new SerialConnection(parameters);
   }

   @Override
   public void open() {
      try {
         this._connection.open();
         this._connection.setTimeout(300);
         this.fireStatusChanged();
      } catch (Exception var2) {
         this.logger.error("Error opening serial Modbus connection", var2);
      }
   }

   @Override
   public boolean isReady() {
      return this._connection.isOpen();
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   protected ModbusTransaction createModbusTransaction() {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(this._connection);
      transaction.setRetries(3);
      return transaction;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
