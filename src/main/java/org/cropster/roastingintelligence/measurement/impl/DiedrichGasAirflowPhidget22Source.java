package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioSensorType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.cropster.roastingintelligence.sensor.SensorValueConverter;
import org.cropster.roastingintelligence.sensor.impl.VoltageRatioInputSensorPhidget22;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiedrichGasAirflowPhidget22Source extends AbstractPhidget22Source {
   private static final Logger LOGGER = LoggerFactory.getLogger(DiedrichGasAirflowPhidget22Source.class);
   private static final int GAS_CHANNEL = 0;
   private static final int AIR100_CHANNEL = 1;
   private static final int AIR0_CHANNEL = 3;
   private static final int DATA_INTERVAL = 1000;
   private static final double MAX_VOLTAGE_RATIO = 1.0;
   protected static final int MAX_PHIDGET21_SENSOR_VALUE = 1000;
   private final SensorValueConverter _gasValueConverter;

   public DiedrichGasAirflowPhidget22Source(MeasurementSourceFactory factory, SensorValueConverter gasValueConverter) {
      super(factory);
      this._gasValueConverter = gasValueConverter;
      Channel gasChannel = new Channel("Gas");
      gasChannel.setIndex(0);
      Channel air100Channel = new Channel("Air100");
      air100Channel.setIndex(1);
      Channel air0Channel = new Channel("Air0");
      air0Channel.setIndex(3);
      List<Channel> channels = new ArrayList<>();
      channels.add(gasChannel);
      channels.add(air100Channel);
      channels.add(air0Channel);

      for (Channel channel : channels) {
         try {
            SensorPhidget22 sensor = this.createSensor(channel);
            this._sensors.put(channel.getIndex(), sensor);
         } catch (PhidgetException var10) {
            throw new IllegalArgumentException(String.format("Error creating VoltageRatioInput at channel %d: %s", channel.getIndex(), var10));
         }
      }
   }

   @Override
   protected SensorPhidget22 createSensor(Channel channel) throws PhidgetException {
      SensorPhidget22 sensor = new VoltageRatioInputSensorPhidget22(
         channel.getIndex(), false, VoltageRatioSensorType.VOLTAGE_RATIO, 1000, this._host, this._port
      );
      sensor.setListener(this);
      return sensor;
   }

   @Override
   public synchronized void configure(Collection<Channel> channels) {
   }

   public static int mapToRange(double input, double maxInput, int maxRange) {
      int sensorValue = (int)MathUtil.roundHalfToEven(input / maxInput * (double)maxRange);
      if (sensorValue > maxRange) {
         LOGGER.error("Input higher than expected. Input: {}, max input: {}, sensor value: {}", new Object[]{input, maxInput, sensorValue});
      }

      return sensorValue;
   }

   @Override
   public Map<String, Double> getValues() {
      if (!this.isReady()) {
         return Collections.emptyMap();
      } else {
         try {
            Map<String, Double> values = new HashMap<>();
            Double gasValue = this._sensors.get(0).getValue();
            if (gasValue != null) {
               int channel0 = mapToRange(gasValue, 1.0, 1000);
               double gas = this._gasValueConverter.convertValue((double)channel0);
               values.put("gas", gas);
            }

            Double air100Value = this._sensors.get(1).getValue();
            Double air0Value = this._sensors.get(3).getValue();
            if (air100Value != null && air0Value != null) {
               int channel1 = mapToRange(air100Value, 1.0, 1000);
               int channel3 = mapToRange(air0Value, 1.0, 1000);
               boolean air100 = channel1 > 500;
               boolean air0 = channel3 > 500;
               Integer air = null;
               if (air100 && !air0) {
                  air = 100;
               } else if (!air100 && !air0) {
                  air = 50;
               } else if (!air100 && air0) {
                  air = 0;
               } else {
                  LOGGER.warn("Inconsistent sensor state: channel1={} and channel3={}", channel1, channel3);
               }

               if (air != null) {
                  values.put("airflow", air.doubleValue());
               }
            }

            return values;
         } catch (Exception var10) {
            LOGGER.error("Error reading values", var10);
            return Collections.emptyMap();
         }
      }
   }
}
