package org.cropster.roastingintelligence.sensor.impl;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.PhidgetException;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.sensor.SensorListenerPhidget22;
import org.cropster.roastingintelligence.sensor.SensorPhidget22;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPhidget22Sensor implements SensorPhidget22, AttachListener, DetachListener, ErrorListener {
   private final Logger logger = LoggerFactory.getLogger(AbstractPhidget22Sensor.class);
   protected volatile Double _value;
   protected final int _channel;
   private SensorListenerPhidget22 _listener;
   protected boolean _isAttached = false;
   protected String _host;
   protected Integer _port;

   public AbstractPhidget22Sensor(int channel) {
      this(channel, null, null);
   }

   public AbstractPhidget22Sensor(int channel, String host, Integer port) {
      this._channel = channel;
      this._host = host;
      this._port = port;
   }

   @Override
   public void open() {
      try {
         if (this._host != null && this._port != null) {
            this.logger.info("Opening {} on server {}:{}", new Object[]{this.getPhidgetName(), this._host, this._port});
            this.getPhidget().setIsRemote(true);
            this.getPhidget().open();
         } else {
            this.logger.info("Opening {}", this.getPhidgetName());
            this.getPhidget().setIsLocal(true);
            this.getPhidget().open();
         }
      } catch (PhidgetException var2) {
         this.logger.error("Error opening {}", this.getPhidgetName(), var2);
      }
   }

   @Override
   public void close() {
      try {
         this.logger.info("Close {}", this.getPhidgetName());
         this.getPhidget().close();
         this.setListener(null);
         this.removeListeners();
      } catch (PhidgetException var2) {
         this.logger.error("Error closing {}", this.getPhidgetName(), var2);
      }
   }

   @Override
   public Double getValue() {
      return this._value;
   }

   @Override
   public void setValue(Double value) {
      this._value = value;
   }

   public void onAttach(AttachEvent ae) {
      if (this.logger.isInfoEnabled()) {
         this.logger.info("{} attached", this.getPhidgetName());
      }

      try {
         this.configure();
      } catch (PhidgetException var3) {
         this.logger.error("Error configuring sensor {}", this.getPhidgetName(), var3);
         this.close();
         this._isAttached = false;
         return;
      }

      this._isAttached = true;
      this._listener.sensorAttached();
   }

   public void onDetach(DetachEvent de) {
      if (this.logger.isInfoEnabled()) {
         this.logger.info("{} detached", this.getPhidgetName());
      }

      this.setValue(null);
      this._isAttached = false;
      if (this._listener != null) {
         this._listener.sensorDetached();
      }
   }

   public void onError(ErrorEvent ee) {
      if (this.logger.isErrorEnabled()) {
         int errorNo = ee.getCode().getCode();
         this.logger.error("{} error {}: {}", new Object[]{ee.getSource().getClass().getSimpleName(), errorNo, ee.getCode().getMessage()});
      }

      this.setValue(null);
   }

   protected void addListeners() {
      this.getPhidget().addAttachListener(this);
      this.getPhidget().addDetachListener(this);
      this.getPhidget().addErrorListener(this);
   }

   protected void removeListeners() {
      this.getPhidget().removeAttachListener(this);
      this.getPhidget().removeDetachListener(this);
      this.getPhidget().removeErrorListener(this);
   }

   @Override
   public SensorListenerPhidget22 getListener() {
      return this._listener;
   }

   @Override
   public void setListener(SensorListenerPhidget22 listener) {
      this._listener = listener;
   }

   @Override
   public boolean getAttached() {
      return this._isAttached;
   }

   @Override
   public void configureChannel(Channel channel) throws PhidgetException {
   }
}
