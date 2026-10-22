package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImfPlcSource extends AbstractMeasurementSource {
   private final Logger logger = LoggerFactory.getLogger(ImfPlcSource.class);
   private final TCPMasterConnection _connection;
   private final boolean _legacyPlc;

   public ImfPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, boolean legacyPlc) {
      this(factory, ipAddress, 502, legacyPlc);
   }

   public ImfPlcSource(MeasurementSourceFactory factory, InetAddress ipAddress, int port, boolean legacyPlc) {
      super(factory);
      this._connection = new TCPMasterConnection(ipAddress);
      this._connection.setPort(port);
      this._connection.setTimeout(300);
      this._legacyPlc = legacyPlc;
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
   public void configure(Collection<Channel> channels) {
   }

   public Unit getUnit() {
      return Unit.CELSIUS;
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   private double getFoatingPointValue(ReadMultipleRegistersResponse response, int register) {
      return this.getFoatingPointValue(response.getRegisters(), register);
   }

   private double getFoatingPointValue(InputRegister[] registers, int register) {
      int lower = registers[register + 1].getValue();
      int higher = registers[register].getValue();
      int value = higher << 16 | lower;
      return (double)Float.intBitsToFloat(value);
   }

   private double getIntegerValue(ReadMultipleRegistersResponse response, int register) {
      return (double)response.getRegister(register).getValue();
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(this._connection);
            ModbusRequest request = new ReadMultipleRegistersRequest(this._legacyPlc ? 0 : 10, this._legacyPlc ? 3 : 6);
            transaction.setRequest(request);
            transaction.execute();
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Map<String, Double> temperatureValues = new LinkedHashMap<>();
            if (this._legacyPlc) {
               temperatureValues.put("beanTemperature", this.getIntegerValue(response, 0));
               temperatureValues.put("inletTemperature", this.getIntegerValue(response, 1));
               temperatureValues.put("burnerTemperature", this.getIntegerValue(response, 2));
            } else {
               temperatureValues.put("beanTemperature", this.getFoatingPointValue(response, 0));
               temperatureValues.put("inletTemperature", this.getFoatingPointValue(response, 2));
               temperatureValues.put("burnerTemperature", this.getFoatingPointValue(response, 4));
            }

            return temperatureValues;
         } catch (Exception var5) {
            this.logger.error("Error processing Modbus request", var5);
            this._connection.close();
            this.fireStatusChanged();
            return Collections.emptyMap();
         }
      }
   }

   @Override
   public void close() throws IOException {
      this._connection.close();
   }
}
