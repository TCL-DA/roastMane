package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.MaskWriteRegisterRequest;
import com.ghgande.j2mod.modbus.msg.MaskWriteRegisterResponse;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadCoilsRequest;
import com.ghgande.j2mod.modbus.msg.ReadCoilsResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleCoilsRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.BitVector;

public abstract class AbstractModbusMaster {
   private static final int DEFAULT_UNIT_ID = 1;
   protected ModbusTransaction transaction;
   private ReadCoilsRequest readCoilsRequest;
   private ReadInputDiscretesRequest readInputDiscretesRequest;
   private WriteCoilRequest writeCoilRequest;
   private WriteMultipleCoilsRequest writeMultipleCoilsRequest;
   private ReadInputRegistersRequest readInputRegistersRequest;
   private ReadMultipleRegistersRequest readMultipleRegistersRequest;
   private WriteSingleRegisterRequest writeSingleRegisterRequest;
   private WriteMultipleRegistersRequest writeMultipleRegistersRequest;
   private MaskWriteRegisterRequest maskWriteRegisterRequest;
   protected int timeout = 3000;

   protected synchronized void setTransaction(ModbusTransaction transaction) {
      this.transaction = transaction;
   }

   public abstract void connect() throws Exception;

   public abstract void disconnect();

   public synchronized BitVector readCoils(int unitId, int ref, int count) throws ModbusException {
      this.checkTransaction();
      if (this.readCoilsRequest == null) {
         this.readCoilsRequest = new ReadCoilsRequest();
      }

      this.readCoilsRequest.setUnitID(unitId);
      this.readCoilsRequest.setReference(ref);
      this.readCoilsRequest.setBitCount(count);
      this.transaction.setRequest(this.readCoilsRequest);
      this.transaction.execute();
      BitVector bv = ((ReadCoilsResponse)this.getAndCheckResponse()).getCoils();
      bv.forceSize(count);
      return bv;
   }

   public synchronized boolean writeCoil(int unitId, int ref, boolean state) throws ModbusException {
      this.checkTransaction();
      if (this.writeCoilRequest == null) {
         this.writeCoilRequest = new WriteCoilRequest();
      }

      this.writeCoilRequest.setUnitID(unitId);
      this.writeCoilRequest.setReference(ref);
      this.writeCoilRequest.setCoil(state);
      this.transaction.setRequest(this.writeCoilRequest);
      this.transaction.execute();
      return ((WriteCoilResponse)this.getAndCheckResponse()).getCoil();
   }

   public synchronized void writeMultipleCoils(int unitId, int ref, BitVector coils) throws ModbusException {
      this.checkTransaction();
      if (this.writeMultipleCoilsRequest == null) {
         this.writeMultipleCoilsRequest = new WriteMultipleCoilsRequest();
      }

      this.writeMultipleCoilsRequest.setUnitID(unitId);
      this.writeMultipleCoilsRequest.setReference(ref);
      this.writeMultipleCoilsRequest.setCoils(coils);
      this.transaction.setRequest(this.writeMultipleCoilsRequest);
      this.transaction.execute();
   }

   public synchronized BitVector readInputDiscretes(int unitId, int ref, int count) throws ModbusException {
      this.checkTransaction();
      if (this.readInputDiscretesRequest == null) {
         this.readInputDiscretesRequest = new ReadInputDiscretesRequest();
      }

      this.readInputDiscretesRequest.setUnitID(unitId);
      this.readInputDiscretesRequest.setReference(ref);
      this.readInputDiscretesRequest.setBitCount(count);
      this.transaction.setRequest(this.readInputDiscretesRequest);
      this.transaction.execute();
      BitVector bv = ((ReadInputDiscretesResponse)this.getAndCheckResponse()).getDiscretes();
      bv.forceSize(count);
      return bv;
   }

   public synchronized InputRegister[] readInputRegisters(int unitId, int ref, int count) throws ModbusException {
      this.checkTransaction();
      if (this.readInputRegistersRequest == null) {
         this.readInputRegistersRequest = new ReadInputRegistersRequest();
      }

      this.readInputRegistersRequest.setUnitID(unitId);
      this.readInputRegistersRequest.setReference(ref);
      this.readInputRegistersRequest.setWordCount(count);
      this.transaction.setRequest(this.readInputRegistersRequest);
      this.transaction.execute();
      return ((ReadInputRegistersResponse)this.getAndCheckResponse()).getRegisters();
   }

   public synchronized Register[] readMultipleRegisters(int unitId, int ref, int count) throws ModbusException {
      this.checkTransaction();
      if (this.readMultipleRegistersRequest == null) {
         this.readMultipleRegistersRequest = new ReadMultipleRegistersRequest();
      }

      this.readMultipleRegistersRequest.setUnitID(unitId);
      this.readMultipleRegistersRequest.setReference(ref);
      this.readMultipleRegistersRequest.setWordCount(count);
      this.transaction.setRequest(this.readMultipleRegistersRequest);
      this.transaction.execute();
      return ((ReadMultipleRegistersResponse)this.getAndCheckResponse()).getRegisters();
   }

   public synchronized int writeSingleRegister(int unitId, int ref, Register register) throws ModbusException {
      this.checkTransaction();
      if (this.writeSingleRegisterRequest == null) {
         this.writeSingleRegisterRequest = new WriteSingleRegisterRequest();
      }

      this.writeSingleRegisterRequest.setUnitID(unitId);
      this.writeSingleRegisterRequest.setReference(ref);
      this.writeSingleRegisterRequest.setRegister(register);
      this.transaction.setRequest(this.writeSingleRegisterRequest);
      this.transaction.execute();
      return ((WriteSingleRegisterResponse)this.getAndCheckResponse()).getRegisterValue();
   }

   public synchronized int writeMultipleRegisters(int unitId, int ref, Register[] registers) throws ModbusException {
      this.checkTransaction();
      if (this.writeMultipleRegistersRequest == null) {
         this.writeMultipleRegistersRequest = new WriteMultipleRegistersRequest();
      }

      this.writeMultipleRegistersRequest.setUnitID(unitId);
      this.writeMultipleRegistersRequest.setReference(ref);
      this.writeMultipleRegistersRequest.setRegisters(registers);
      this.transaction.setRequest(this.writeMultipleRegistersRequest);
      this.transaction.execute();
      return ((WriteMultipleRegistersResponse)this.transaction.getResponse()).getWordCount();
   }

   public synchronized boolean maskWriteRegister(int unitId, int ref, int andMask, int orMask) throws ModbusException {
      this.checkTransaction();
      if (this.maskWriteRegisterRequest == null) {
         this.maskWriteRegisterRequest = new MaskWriteRegisterRequest();
      }

      this.maskWriteRegisterRequest.setUnitID(unitId);
      this.maskWriteRegisterRequest.setReference(ref);
      this.maskWriteRegisterRequest.setAndMask(andMask);
      this.maskWriteRegisterRequest.setOrMask(orMask);
      this.transaction.setRequest(this.maskWriteRegisterRequest);
      this.transaction.execute();
      MaskWriteRegisterResponse response = (MaskWriteRegisterResponse)this.getAndCheckResponse();
      return response.getReference() == this.maskWriteRegisterRequest.getReference()
         && response.getAndMask() == this.maskWriteRegisterRequest.getAndMask()
         && response.getOrMask() == this.maskWriteRegisterRequest.getOrMask();
   }

   public BitVector readCoils(int ref, int count) throws ModbusException {
      return this.readCoils(1, ref, count);
   }

   public boolean writeCoil(int ref, boolean state) throws ModbusException {
      return this.writeCoil(1, ref, state);
   }

   public void writeMultipleCoils(int ref, BitVector coils) throws ModbusException {
      this.writeMultipleCoils(1, ref, coils);
   }

   public BitVector readInputDiscretes(int ref, int count) throws ModbusException {
      return this.readInputDiscretes(1, ref, count);
   }

   public InputRegister[] readInputRegisters(int ref, int count) throws ModbusException {
      return this.readInputRegisters(1, ref, count);
   }

   public Register[] readMultipleRegisters(int ref, int count) throws ModbusException {
      return this.readMultipleRegisters(1, ref, count);
   }

   public int writeSingleRegister(int ref, Register register) throws ModbusException {
      return this.writeSingleRegister(1, ref, register);
   }

   public int writeMultipleRegisters(int ref, Register[] registers) throws ModbusException {
      return this.writeMultipleRegisters(1, ref, registers);
   }

   public boolean maskWriteRegister(int ref, int andMask, int orMask) throws ModbusException {
      return this.maskWriteRegister(1, ref, andMask, orMask);
   }

   private ModbusResponse getAndCheckResponse() throws ModbusException {
      ModbusResponse res = this.transaction.getResponse();
      if (res == null) {
         throw new ModbusException("No response");
      } else {
         return res;
      }
   }

   private void checkTransaction() throws ModbusException {
      if (this.transaction == null) {
         throw new ModbusException("No transaction created, probably not connected");
      }
   }

   public int getTimeout() {
      return this.timeout;
   }

   public void setTimeout(int timeout) {
      this.timeout = timeout;
   }

   public synchronized void setRetries(int retries) {
      if (this.transaction != null) {
         this.transaction.setRetries(retries);
      }
   }

   public synchronized void setCheckingValidity(boolean b) {
      if (this.transaction != null) {
         this.transaction.setCheckingValidity(b);
      }
   }

   public abstract AbstractModbusTransport getTransport();

   public abstract boolean isConnected();
}
