package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.net.AbstractModbusListener;

public abstract class ModbusRequest extends ModbusMessageImpl {
   public static ModbusRequest createModbusRequest(int functionCode) {
      ModbusRequest request;
      switch (functionCode) {
         case 1:
            request = new ReadCoilsRequest();
            break;
         case 2:
            request = new ReadInputDiscretesRequest();
            break;
         case 3:
            request = new ReadMultipleRegistersRequest();
            break;
         case 4:
            request = new ReadInputRegistersRequest();
            break;
         case 5:
            request = new WriteCoilRequest();
            break;
         case 6:
            request = new WriteSingleRegisterRequest();
            break;
         case 7:
            request = new ReadExceptionStatusRequest();
            break;
         case 8:
            request = new ReadSerialDiagnosticsRequest();
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
            request = new IllegalFunctionRequest(functionCode);
            break;
         case 11:
            request = new ReadCommEventCounterRequest();
            break;
         case 12:
            request = new ReadCommEventLogRequest();
            break;
         case 15:
            request = new WriteMultipleCoilsRequest();
            break;
         case 16:
            request = new WriteMultipleRegistersRequest();
            break;
         case 17:
            request = new ReportSlaveIDRequest();
            break;
         case 20:
            request = new ReadFileRecordRequest();
            break;
         case 21:
            request = new WriteFileRecordRequest();
            break;
         case 22:
            request = new MaskWriteRegisterRequest();
            break;
         case 23:
            request = new ReadWriteMultipleRequest();
            break;
         case 24:
            request = new ReadFIFOQueueRequest();
            break;
         case 43:
            request = new ReadMEIRequest();
      }

      return request;
   }

   public abstract ModbusResponse getResponse();

   public abstract ModbusResponse createResponse(AbstractModbusListener var1);

   public ModbusResponse createExceptionResponse(int code) {
      return this.updateResponseWithHeader(new ExceptionResponse(this.getFunctionCode(), code), true);
   }

   ModbusResponse updateResponseWithHeader(ModbusResponse response) {
      return this.updateResponseWithHeader(response, false);
   }

   ModbusResponse updateResponseWithHeader(ModbusResponse response, boolean ignoreFunctionCode) {
      response.setHeadless(this.isHeadless());
      if (!this.isHeadless()) {
         response.setTransactionID(this.getTransactionID());
         response.setProtocolID(this.getProtocolID());
      } else {
         response.setHeadless();
      }

      response.setUnitID(this.getUnitID());
      if (!ignoreFunctionCode) {
         response.setFunctionCode(this.getFunctionCode());
      }

      return response;
   }
}
