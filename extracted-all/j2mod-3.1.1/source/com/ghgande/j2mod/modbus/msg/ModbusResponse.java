package com.ghgande.j2mod.modbus.msg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ModbusResponse extends ModbusMessageImpl {
   private static final Logger logger = LoggerFactory.getLogger(ModbusResponse.class);
   private ModbusResponse.AuxiliaryMessageTypes auxiliaryType = ModbusResponse.AuxiliaryMessageTypes.NONE;

   public static ModbusResponse createModbusResponse(int functionCode) {
      ModbusResponse response;
      switch (functionCode) {
         case 1:
            response = new ReadCoilsResponse();
            break;
         case 2:
            response = new ReadInputDiscretesResponse();
            break;
         case 3:
            response = new ReadMultipleRegistersResponse();
            break;
         case 4:
            response = new ReadInputRegistersResponse();
            break;
         case 5:
            response = new WriteCoilResponse();
            break;
         case 6:
            response = new WriteSingleRegisterResponse();
            break;
         case 7:
            response = new ReadExceptionStatusResponse();
            break;
         case 8:
            response = new ReadSerialDiagnosticsResponse();
            break;
         case 9:
         case 10:
         case 13:
         case 14:
         case 18:
         case 19:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         default:
            if ((functionCode & 128) != 0) {
               response = new ExceptionResponse(functionCode);
            } else {
               response = new ExceptionResponse();
            }
            break;
         case 11:
            response = new ReadCommEventCounterResponse();
            break;
         case 12:
            response = new ReadCommEventLogResponse();
            break;
         case 15:
            response = new WriteMultipleCoilsResponse();
            break;
         case 16:
            response = new WriteMultipleRegistersResponse();
            break;
         case 17:
            response = new ReportSlaveIDResponse();
            break;
         case 20:
            response = new ReadFileRecordResponse();
            break;
         case 21:
            response = new WriteFileRecordResponse();
            break;
         case 22:
            response = new MaskWriteRegisterResponse();
            break;
         case 23:
            response = new ReadWriteMultipleResponse();
            break;
         case 24:
            response = new ReadFIFOQueueResponse();
            break;
         case 43:
            response = new ReadMEIResponse();
      }

      return response;
   }

   protected void setMessage(byte[] msg) {
      try {
         this.readData(new DataInputStream(new ByteArrayInputStream(msg)));
      } catch (IOException var3) {
         logger.error("Problem setting response message - {}", var3.getMessage());
      }
   }

   public ModbusResponse.AuxiliaryMessageTypes getAuxiliaryType() {
      return this.auxiliaryType;
   }

   public void setAuxiliaryType(ModbusResponse.AuxiliaryMessageTypes auxiliaryType) {
      this.auxiliaryType = auxiliaryType;
   }

   public static enum AuxiliaryMessageTypes {
      NONE,
      UNIT_ID_MISSMATCH;
   }
}
