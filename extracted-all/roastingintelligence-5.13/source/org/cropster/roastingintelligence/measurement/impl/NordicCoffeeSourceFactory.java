package org.cropster.roastingintelligence.measurement.impl;

import com.cropster.util.model.Unit;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.IdentityValueConverter;
import org.cropster.roastingintelligence.util.Storage;
import org.cropster.roastingintelligence.util.ValueConverter;

public class NordicCoffeeSourceFactory extends NetworkSourceFactory {
   private static final String PARAM_VERSION = "version";
   private static final int NORDIC_MODBUS_PORT = 8888;
   private static final String PARAM_MIN_GAS = "minGas";
   private static final String PARAM_MAX_GAS = "maxGas";
   private static final String PARAM_MIN_AIRFLOW = "minAirflow";
   private static final String PARAM_MAX_AIRFLOW = "maxAirflow";
   private static final String PARAM_MIN_DRUM_SPEED = "minDrumSpeed";
   private static final String PARAM_MAX_DRUM_SPEED = "maxDrumSpeed";
   private static final int LOW_LIMIT = 0;
   private static final int HIGH_LIMIT = 100;
   private static final int TEMPERATURE_REGISTER = 18176;
   private static final int BEAN_TEMPERATURE_UNIT_ID = 5;
   private static final int EXHAUST_TEMPERATURE_UNIT_ID = 6;
   private static final int BURNER_TEMPERATURE_UNIT_ID = 7;
   private static final int DRUM_GAS_AIRFLOW_UNIT_ID = 4;
   private static final int AIRFLOW_REGISTER = 4613;
   private static final int AIRFLOW_CONTROL_REGISTER = 4648;
   private static final int DRUM_SPEED_REGISTER = 4608;
   private static final int DRUM_SPEED_CONTROL_REGISTER = 4646;
   private static final int GAS_REGISTER = 4659;
   private static final int GAS_CONTROL_REGISTER = 4651;
   private NordicCoffeeSourceFactory.NordicVersion _version = NordicCoffeeSourceFactory.NordicVersion.VERSION1;
   private int _minGas = 0;
   private int _maxGas = 100;
   private int _minAirflow = 0;
   private int _maxAirflow = 100;
   private int _minDrumSpeed = 0;
   private int _maxDrumSpeed = 100;

   @Override
   protected String getDefaultIpAddress() {
      return "192.168.97.7";
   }

   @Override
   public Unit getUnit(String measureName) {
      if ("beanTemperature".equals(measureName) || "exhaustTemperature".equals(measureName) || "burnerTemperature".equals(measureName)) {
         return Unit.CELSIUS;
      } else {
         return !"gas".equals(measureName)
               && !"gasControl".equals(measureName)
               && !"airflow".equals(measureName)
               && !"airflowControl".equals(measureName)
               && !"drumSpeed".equals(measureName)
               && !"drumSpeedControl".equals(measureName)
            ? null
            : Unit.PERCENT;
      }
   }

   public Integer validateParameter(SourceParameter parameter, int min, int max) {
      Integer parameterValue = null;
      if (StringUtils.isBlank(parameter.getValue())) {
         throw new IllegalArgumentException(String.format("%s required", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName())));
      } else {
         parameterValue = Integer.valueOf(parameter.getValue());
         if (parameterValue < min) {
            throw new IllegalArgumentException(
               String.format("%s must be greater than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), min)
            );
         } else if (parameterValue > max) {
            throw new IllegalArgumentException(
               String.format("%s must be smaller than %d", org.cropster.roastingintelligence.util.Messages.m_(parameter.getName()), max)
            );
         } else {
            return parameterValue;
         }
      }
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      SourceParameter version = new SourceParameter("version", String.class, this._version != null ? this._version.name() : null);
      version.setOptions(new String[]{NordicCoffeeSourceFactory.NordicVersion.VERSION1.name(), NordicCoffeeSourceFactory.NordicVersion.VERSION2.name()});
      version.setOptionDisplayName(NordicCoffeeSourceFactory.NordicVersion.VERSION1.name(), "Delta PLC");
      version.setOptionDisplayName(NordicCoffeeSourceFactory.NordicVersion.VERSION2.name(), "Microchip PLC");
      parameters.add(version);
      parameters.addAll(super.getParameters());
      SourceParameter minGasParameter = new SourceParameter("minGas", String.class, String.valueOf(this._minGas));
      parameters.add(minGasParameter);
      SourceParameter maxGasParameter = new SourceParameter("maxGas", String.class, String.valueOf(this._maxGas));
      parameters.add(maxGasParameter);
      SourceParameter minAirflowParameter = new SourceParameter("minAirflow", String.class, String.valueOf(this._minAirflow));
      parameters.add(minAirflowParameter);
      SourceParameter maxAirflowParameter = new SourceParameter("maxAirflow", String.class, String.valueOf(this._maxAirflow));
      parameters.add(maxAirflowParameter);
      SourceParameter minDrumSpeedParameter = new SourceParameter("minDrumSpeed", String.class, String.valueOf(this._minDrumSpeed));
      parameters.add(minDrumSpeedParameter);
      SourceParameter maxDrumSpeedParameter = new SourceParameter("maxDrumSpeed", String.class, String.valueOf(this._maxDrumSpeed));
      parameters.add(maxDrumSpeedParameter);
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      Integer minGas = this._minGas;
      Integer maxGas = this._maxGas;
      Integer minAirflow = this._minAirflow;
      Integer maxAirflow = this._maxAirflow;
      Integer minDrumSpeed = this._minDrumSpeed;
      Integer maxDrumSpeed = this._maxDrumSpeed;

      for (SourceParameter parameter : parameters) {
         if ("version".equals(parameter.getName())) {
            String value = parameter.getValue();
            if (value == null || value.trim().length() == 0) {
               throw new IllegalArgumentException("Version of the Nordic Coffee machine required!");
            }

            this._version = NordicCoffeeSourceFactory.NordicVersion.valueOf(value);
         } else if ("minGas".equals(parameter.getName())) {
            minGas = this.validateParameter(parameter, 0, 100);
         } else if ("maxGas".equals(parameter.getName())) {
            maxGas = this.validateParameter(parameter, 0, 100);
         } else if ("minAirflow".equals(parameter.getName())) {
            minAirflow = this.validateParameter(parameter, 0, 100);
         } else if ("maxAirflow".equals(parameter.getName())) {
            maxAirflow = this.validateParameter(parameter, 0, 100);
         } else if ("minDrumSpeed".equals(parameter.getName())) {
            minDrumSpeed = this.validateParameter(parameter, 0, 100);
         } else if ("maxDrumSpeed".equals(parameter.getName())) {
            maxDrumSpeed = this.validateParameter(parameter, 0, 100);
         }
      }

      if (minGas >= maxGas) {
         throw new IllegalArgumentException(
            String.format(
               "%s must be smaller than %s",
               org.cropster.roastingintelligence.util.Messages.m_("minGas"),
               org.cropster.roastingintelligence.util.Messages.m_("maxGas")
            )
         );
      } else {
         this._minGas = minGas;
         this._maxGas = maxGas;
         if (minAirflow >= maxAirflow) {
            throw new IllegalArgumentException(
               String.format(
                  "%s must be smaller than %s",
                  org.cropster.roastingintelligence.util.Messages.m_("minAirflow"),
                  org.cropster.roastingintelligence.util.Messages.m_("maxAirflow")
               )
            );
         } else {
            this._minAirflow = minAirflow;
            this._maxAirflow = maxAirflow;
            if (minDrumSpeed >= maxDrumSpeed) {
               throw new IllegalArgumentException(
                  String.format(
                     "%s must be smaller than %s",
                     org.cropster.roastingintelligence.util.Messages.m_("minDrumSpeed"),
                     org.cropster.roastingintelligence.util.Messages.m_("maxDrumSpeed")
                  )
               );
            } else {
               this._minDrumSpeed = minDrumSpeed;
               this._maxDrumSpeed = maxDrumSpeed;
            }
         }
      }
   }

   @Override
   public List<MeasurementSourceFactory.ConfigurationWarning> getConfigurationWarnings(Set<Channel> channels) {
      if (!NordicCoffeeSourceFactory.NordicVersion.VERSION2.equals(this._version)) {
         return Collections.emptyList();
      } else if (channels.stream().noneMatch(channel -> channel.getName().matches(".+[cC]ontrol"))) {
         return Collections.emptyList();
      } else {
         List<MeasurementSourceFactory.ConfigurationWarning> warnings = new ArrayList<>();
         warnings.add(
            new MeasurementSourceFactory.ConfigurationWarning(
               org.cropster.roastingintelligence.util.Messages.m_("Slider control values may be out of sync with the machine"),
               org.cropster.roastingintelligence.util.Messages.m_(
                  "Displayed values may not reflect the machine's actual state. Enter a value in RI to trigger a change. Always verify readings after adjusting control values or reconnecting the machine."
               )
            )
         );
         return warnings;
      }
   }

   @Override
   public MeasurementSource[] getMeasurementSources(Collection<Channel> channels) throws IOException {
      Map<String, NordicCoffeeSourceFactory.ModbusTarget> targets = new LinkedHashMap<>();
      Map<String, NordicCoffeeSourceFactory.ModbusControlTarget> controls = new LinkedHashMap<>();
      NordicCoffeeSourceFactory.ModbusTarget beanTempConfig = new NordicCoffeeSourceFactory.ModbusTarget("beanTemperature", 18176);
      beanTempConfig.setUnitId(5);
      beanTempConfig.setStorage(Storage.DECIMAL1_16);
      targets.put(beanTempConfig.getName(), beanTempConfig);
      NordicCoffeeSourceFactory.ModbusTarget exhaustTempConfig = new NordicCoffeeSourceFactory.ModbusTarget("exhaustTemperature", 18176);
      exhaustTempConfig.setUnitId(6);
      exhaustTempConfig.setStorage(Storage.DECIMAL1_16);
      targets.put(exhaustTempConfig.getName(), exhaustTempConfig);
      NordicCoffeeSourceFactory.ModbusTarget burnerTempConfig = new NordicCoffeeSourceFactory.ModbusTarget("burnerTemperature", 18176);
      burnerTempConfig.setUnitId(7);
      burnerTempConfig.setStorage(Storage.DECIMAL1_16);
      targets.put(burnerTempConfig.getName(), burnerTempConfig);
      NordicCoffeeSourceFactory.ModbusTarget airflowConfig = new NordicCoffeeSourceFactory.ModbusTarget("airflow", 4613);
      airflowConfig.setUnitId(4);
      airflowConfig.setStorage(Storage.INTEGER_16);
      targets.put(airflowConfig.getName(), airflowConfig);
      NordicCoffeeSourceFactory.ModbusControlTarget airflowControlConfig = new NordicCoffeeSourceFactory.ModbusControlTarget("airflowControl", 4648);
      airflowControlConfig.setUnitId(4);
      airflowControlConfig.setStorage(Storage.INTEGER_16);
      airflowControlConfig.setMin(this._minAirflow);
      airflowControlConfig.setMax(this._maxAirflow);
      if (NordicCoffeeSourceFactory.NordicVersion.VERSION1.equals(this._version)) {
         targets.put(airflowControlConfig.getName(), airflowControlConfig);
      }

      controls.put(airflowControlConfig.getName(), airflowControlConfig);
      NordicCoffeeSourceFactory.ModbusTarget gasConfig = new NordicCoffeeSourceFactory.ModbusTarget("gas", 4659);
      gasConfig.setUnitId(4);
      gasConfig.setStorage(Storage.INTEGER_16);
      targets.put(gasConfig.getName(), gasConfig);
      NordicCoffeeSourceFactory.ModbusControlTarget gasControlConfig = new NordicCoffeeSourceFactory.ModbusControlTarget("gasControl", 4651);
      gasControlConfig.setUnitId(4);
      gasControlConfig.setStorage(Storage.INTEGER_16);
      gasControlConfig.setMin(this._minGas);
      gasControlConfig.setMax(this._maxGas);
      if (NordicCoffeeSourceFactory.NordicVersion.VERSION1.equals(this._version)) {
         targets.put(gasControlConfig.getName(), gasControlConfig);
      }

      controls.put(gasControlConfig.getName(), gasControlConfig);
      NordicCoffeeSourceFactory.ModbusTarget drumSpeedConfig = new NordicCoffeeSourceFactory.ModbusTarget("drumSpeed", 4608);
      drumSpeedConfig.setUnitId(4);
      drumSpeedConfig.setStorage(Storage.INTEGER_16);
      targets.put(drumSpeedConfig.getName(), drumSpeedConfig);
      NordicCoffeeSourceFactory.ModbusControlTarget drumSpeedControlConfig = new NordicCoffeeSourceFactory.ModbusControlTarget("drumSpeedControl", 4646);
      drumSpeedControlConfig.setUnitId(4);
      drumSpeedControlConfig.setStorage(Storage.INTEGER_16);
      drumSpeedControlConfig.setMin(this._minDrumSpeed);
      drumSpeedControlConfig.setMax(this._maxDrumSpeed);
      if (NordicCoffeeSourceFactory.NordicVersion.VERSION1.equals(this._version)) {
         targets.put(drumSpeedControlConfig.getName(), drumSpeedControlConfig);
      }

      controls.put(drumSpeedControlConfig.getName(), drumSpeedControlConfig);

      try {
         MeasurementSource source;
         if (NordicCoffeeSourceFactory.NordicVersion.VERSION1.equals(this._version)) {
            source = new NordicCoffeeSource(this, this._inetAddress, 8888, targets, controls);
         } else {
            if (!NordicCoffeeSourceFactory.NordicVersion.VERSION2.equals(this._version)) {
               throw new IllegalArgumentException("Invalid Nordic Coffee version");
            }

            source = new NordicCoffeeV2Source(this, this._inetAddress, 8888, targets, controls);
         }

         return new MeasurementSource[]{source};
      } catch (Exception var14) {
         throw new IOException("Error initializing measurement source " + NordicCoffeeSource.class.getSimpleName());
      }
   }

   public static class ModbusControlTarget extends NordicCoffeeSourceFactory.ModbusTarget {
      private int _min = 0;
      private int _max = 100;

      public ModbusControlTarget(String name, int register) {
         super(name, register);
      }

      public int getMin() {
         return this._min;
      }

      public void setMin(int min) {
         this._min = min;
      }

      public int getMax() {
         return this._max;
      }

      public void setMax(int max) {
         this._max = max;
      }
   }

   public static class ModbusTarget extends GenericModbusSource.ModbusConfig {
      private ValueConverter _converter = new IdentityValueConverter();
      private BiConsumer<Map<String, Double>, Double> _putFunction = (values, value) -> values.put(this.getName(), value);

      public ModbusTarget(String name, int register) {
         super(name, register);
      }

      public void setPutFunction(BiConsumer<Map<String, Double>, Double> putFunction) {
         this._putFunction = putFunction;
      }

      public ValueConverter getConverter() {
         return this._converter;
      }

      public void setConverter(ValueConverter converter) {
         this._converter = converter;
      }

      public Double getValue(ModbusTransaction transaction) throws ModbusException {
         ModbusRequest request;
         if (this.getFunction() == 4) {
            request = new ReadInputRegistersRequest(this.getRegister(), this.getStorage().getRegisterCount());
         } else {
            request = new ReadMultipleRegistersRequest(this.getRegister(), this.getStorage().getRegisterCount());
         }

         request.setUnitID(this.getUnitId());
         transaction.setRequest(request);
         transaction.execute();
         Double value;
         if (this.getFunction() == 4) {
            ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
            value = this.getDoubleValue(response, this);
         } else {
            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse)transaction.getResponse();
            value = this.getDoubleValue(response, this);
         }

         return value;
      }

      public void putValue(Map<String, Double> values, ModbusTransaction transaction) throws ModbusException {
         Double value = this.getValue(transaction);
         value = this._converter.convertValueToPercent(value);
         this._putFunction.accept(values, value);
      }

      private double getDoubleValue(InputRegister[] registers, NordicCoffeeSourceFactory.ModbusTarget target) {
         int[] registerValues = new int[registers.length];

         for (int i = 0; i < registerValues.length; i++) {
            registerValues[i] = registers[i].getValue();
         }

         int value = target.getStorage().extract(registerValues, false);
         return target.getStorage().decode(value);
      }

      private double getDoubleValue(ReadInputRegistersResponse response, NordicCoffeeSourceFactory.ModbusTarget target) {
         return this.getDoubleValue(response.getRegisters(), target);
      }

      private double getDoubleValue(ReadMultipleRegistersResponse response, NordicCoffeeSourceFactory.ModbusTarget target) {
         return this.getDoubleValue(response.getRegisters(), target);
      }
   }

   public static enum NordicVersion {
      VERSION1,
      VERSION2;
   }
}
