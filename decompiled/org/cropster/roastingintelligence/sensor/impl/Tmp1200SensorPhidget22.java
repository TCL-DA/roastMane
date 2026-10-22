package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.PhidgetException;
import com.phidget22.RTDType;
import com.phidget22.RTDWireSetup;
import com.phidget22.TemperatureSensor;
import org.cropster.roastingintelligence.model.Channel;

public class Tmp1200SensorPhidget22 extends TemperatureSensorPhidget22 {
   private RTDType _rtdType;
   private final RTDWireSetup _wireSetup;

   public Tmp1200SensorPhidget22(int channel, RTDType type, RTDWireSetup wireSetup, String host, Integer port) throws PhidgetException {
      super(channel, true, host, port);
      if (type == null) {
         throw new IllegalArgumentException("RTD type not set");
      } else if (wireSetup == null) {
         throw new IllegalArgumentException("RTD Wire Setup not set");
      } else {
         this._rtdType = type;
         this._wireSetup = wireSetup;
      }
   }

   public Tmp1200SensorPhidget22(TemperatureSensor phidget, RTDType type, RTDWireSetup wireSetup, String host, Integer port) throws PhidgetException {
      super(phidget, true, host, port);
      if (type == null) {
         throw new IllegalArgumentException("RTD type not set");
      } else if (wireSetup == null) {
         throw new IllegalArgumentException("RTD Wire Setup not set");
      } else {
         this._rtdType = type;
         this._wireSetup = wireSetup;
      }
   }

   public RTDType getRtdType() {
      return this._rtdType;
   }

   public void setRtdType(RTDType rtdType) throws PhidgetException {
      this._rtdType = rtdType;
   }

   @Override
   public void configure() throws PhidgetException {
      this._phidget.setRTDType(this._rtdType);
      this._phidget.setRTDWireSetup(this._wireSetup);
      super.configure();
   }

   @Override
   public void configureChannel(Channel channel) throws PhidgetException {
      RTDType rtdType = RTDType.valueOf(channel.getProbeType());
      if (!this._rtdType.equals(rtdType)) {
         this.setRtdType(rtdType);
      }

      if (this._isAttached) {
         this.configure();
      }
   }
}
