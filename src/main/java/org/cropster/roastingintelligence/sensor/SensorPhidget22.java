package org.cropster.roastingintelligence.sensor;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.model.Channel;

public interface SensorPhidget22 {
   Phidget getPhidget();

   String getPhidgetName();

   Double getValue();

   void setValue(Double var1);

   void open();

   void close();

   void setChannel(int var1) throws PhidgetException;

   void configure() throws PhidgetException;

   void setListener(SensorListenerPhidget22 var1);

   SensorListenerPhidget22 getListener();

   boolean getAttached();

   void configureChannel(Channel var1) throws PhidgetException;
}
