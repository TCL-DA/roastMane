package org.cropster.roastingintelligence.measurement.impl;

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffedSerialGasAirflowSource extends CoffedSerialSource implements MeasurementReaderWriter {
   private final Logger logger = LoggerFactory.getLogger(CoffedSerialGasAirflowSource.class);
   private static final int AIRFLOW_REGISTER = 1;
   private static final int AIRFLOW_UNIT_ID = 1;
   private static final int GAS_REGISTER = 53;
   private static final int GAS_UNIT_ID = 2;
   private final CoffedSerialGasAirflowSource.LinearValueConverter _gasConverter;
   private final CoffedSerialGasAirflowSource.LinearValueConverter _airflowConverter;
   private Set<String> _channelNames;
   private int _minGas = 20;
   private int _maxGas = 100;
   private int _minAirflow = 60;
   private int _maxAirflow = 100;

   public CoffedSerialGasAirflowSource(
      MeasurementSourceFactory factory, SerialParameters parameters, CoffedSerialSourceFactory.CoffedManualVersion coffedManualVersion
   ) {
      super(factory, parameters, coffedManualVersion);
      this._gasConverter = new CoffedSerialGasAirflowSource.LinearValueConverter(0.0, 100.0);
      this._airflowConverter = new CoffedSerialGasAirflowSource.LinearValueConverter(2700.0, 21.0);
   }

   public void setMinGas(int minGas) {
      this._minGas = minGas;
   }

   public void setMaxGas(int maxGas) {
      this._maxGas = maxGas;
   }

   public void setMinAirflow(int minAirflow) {
      this._minAirflow = minAirflow;
   }

   public void setMaxAirflow(int maxAirflow) {
      this._maxAirflow = maxAirflow;
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
      super.configure(channels);
      this._channelNames = new LinkedHashSet<>();

      for (Channel channel : channels) {
         this._channelNames.add(channel.getName());
      }
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public Map<String, Double> getValues() {
      Map<String, Double> values = super.getValues();
      ModbusSerialTransaction transaction = this.getModbusTransaction();

      try {
         if (this._channelNames.contains("gas") || this._channelNames.contains("gasControl")) {
            ModbusRequest request = new ReadMultipleRegistersRequest(53, 1);
            request.setUnitID(2);
            request.setHeadless();
            transaction.setRequest(request);
            synchronized (this) {
               transaction.execute();
            }

            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Double gasValue = this._gasConverter.convertValueToPercent((double)response.getRegisterValue(0));
            values.put("gas", gasValue);
            values.put("gasControl", gasValue);
         }

         if (this._channelNames.contains("airflow") || this._channelNames.contains("airflowControl")) {
            ModbusRequest request = new ReadMultipleRegistersRequest(1, 1);
            request.setUnitID(1);
            request.setHeadless();
            transaction.setRequest(request);
            synchronized (this) {
               transaction.execute();
            }

            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            Double airflowValue = this._airflowConverter.convertValueToPercent((double)response.getRegisterValue(0));
            values.put("airflow", airflowValue);
            values.put("airflowControl", airflowValue);
         }
      } catch (Exception var9) {
         this.logger.error("Error processing Modbus request", var9);
         if (!this._connection.isOpen()) {
            IOUtils.closeQuietly(this);
            this.fireStatusChanged();
            return values;
         }
      }

      return values;
   }

   @Override
   public void setValue(String field, Double value) {
      if (!"gasControl".equals(field) && !"airflowControl".equals(field)) {
         throw new IllegalArgumentException("Field not supported!");
      } else {
         if (!this.isReady()) {
            this.open();
            if (!this.isReady()) {
               this.logger.error("Failed to write {} value because connecting failed", field);
               return;
            }
         }

         try {
            this.logger.debug("Write {} value: {}", field, value);
            CoffedSerialGasAirflowSource.LinearValueConverter valueConverter = "gasControl".equals(field) ? this._gasConverter : this._airflowConverter;
            int register = "gasControl".equals(field) ? 53 : 1;
            int unitId = "gasControl".equals(field) ? 2 : 1;
            ModbusSerialTransaction transaction = this.getModbusTransaction();
            int newValue = valueConverter.convertPercentToValue((double)value.intValue());
            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(register, new SimpleRegister(newValue));
            request.setUnitID(unitId);
            transaction.setRequest(request);
            synchronized (this) {
               transaction.execute();
            }

            WriteSingleRegisterResponse response = (WriteSingleRegisterResponse)transaction.getResponse();
            if (response.getRegisterValue() != newValue) {
               throw new IOException(String.format("Error writing %s value", field));
            }
         } catch (Exception var12) {
            this.logger.error("Error writing to PLC", var12);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.logger.info("Close connection");
      this._connection.close();
   }

   @Override
   public Double getMinValue(String field) {
      if ("gasControl".equals(field)) {
         return (double)this._minGas;
      } else {
         return "airflowControl".equals(field) ? (double)this._minAirflow : null;
      }
   }

   @Override
   public Double getMaxValue(String field) {
      if ("gasControl".equals(field)) {
         return (double)this._maxGas;
      } else {
         return "airflowControl".equals(field) ? (double)this._maxAirflow : null;
      }
   }

   @Override
   public Set<String> getConfiguredChannels() {
      return this._channelNames;
   }

   private class LinearValueConverter {
      private final double offset;
      private final double factor;

      LinearValueConverter(double offset, double factor) {
         this.offset = offset;
         this.factor = factor;
      }

      public double convertValueToPercent(double value) {
         return (value - this.offset) / this.factor;
      }

      public int convertPercentToValue(double percent) {
         return (int)(percent * this.factor + this.offset);
      }
   }
}
