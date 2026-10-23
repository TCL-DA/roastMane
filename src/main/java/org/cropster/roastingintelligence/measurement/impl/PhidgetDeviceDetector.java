package org.cropster.roastingintelligence.measurement.impl;

import com.phidget22.Manager;
import com.phidget22.ManagerAttachEvent;
import com.phidget22.ManagerAttachListener;
import com.phidget22.ManagerDetachEvent;
import com.phidget22.ManagerDetachListener;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhidgetDeviceDetector implements ManagerAttachListener, ManagerDetachListener {
   private final Logger logger = LoggerFactory.getLogger(PhidgetDeviceDetector.class);
   private Manager _phidgetManager;
   private boolean _isOpen = false;
   private final Map<String, PhidgetDeviceDetector.DeviceFilter> _deviceFilter = new HashMap<>();

   public PhidgetDeviceDetector() {
      try {
         this._phidgetManager = new Manager();
      } catch (PhidgetException var2) {
         this.logger.error("Error initializing Phidget Manager", var2);
      }
   }

   public synchronized void addDeviceFilter(String source, PhidgetDeviceDetector.DeviceFilter deviceFilter) {
      this.logger.debug("Add device filter {} - {}", source, deviceFilter.getClass().getSimpleName());
      if (!this._deviceFilter.containsKey(source)) {
         this._deviceFilter.put(source, deviceFilter);
      }
   }

   public synchronized void removeDeviceFilter(String source) {
      this.logger.debug("Remove device filter {}", source);
      this._deviceFilter.remove(source);
      if (this.isOpen() && this._deviceFilter.isEmpty()) {
         this.close();
      }
   }

   public String getPhidgetInfo(Phidget phidget) throws PhidgetException {
      return String.format(
         "%s, %s, %s, %s, %d, %b, %b, %d, %b, %d, %s, %s, %s",
         phidget.getDeviceName(),
         phidget.getDeviceClass(),
         phidget.getDeviceID(),
         phidget.getPhidgetIDString(),
         phidget.getDeviceSerialNumber(),
         phidget.getAttached(),
         phidget.getIsChannel(),
         phidget.getChannel(),
         phidget.getIsHubPortDevice(),
         phidget.getHubPort(),
         phidget.getChannelClassName(),
         phidget.getChannelSubclass().name(),
         phidget.getIsRemote()
            ? String.format("%s, %s, %s, %s", phidget.getServerName(), phidget.getServerPeerName(), phidget.getServerHostname(), phidget.getServerUniqueName())
            : "local"
      );
   }

   public void onAttach(ManagerAttachEvent ev) {
      Phidget phidget = ev.getChannel();
      if (this.logger.isDebugEnabled()) {
         try {
            if (phidget.getAttached()) {
               this.logger.debug("Device attached: {}", this.getPhidgetInfo(phidget));
            } else {
               this.logger.debug("Phidget not attached");
            }
         } catch (PhidgetException var7) {
            this.logger.error("Error attaching device", var7);
         }
      }

      for (PhidgetDeviceDetector.DeviceFilter deviceFilter : this._deviceFilter.values()) {
         try {
            if (deviceFilter.filter(phidget)) {
               deviceFilter.attachHandler(phidget);
               return;
            }
         } catch (PhidgetException var6) {
            this.logger.error("Error attaching device", var6);
         }
      }
   }

   public void onDetach(ManagerDetachEvent ev) {
      Phidget phidget = ev.getChannel();
      if (this.logger.isDebugEnabled()) {
         try {
            this.logger.debug("Device detached: {}", this.getPhidgetInfo(phidget));
         } catch (PhidgetException var7) {
            this.logger.error("Error detaching device", var7);
         }
      }

      for (PhidgetDeviceDetector.DeviceFilter deviceFilter : this._deviceFilter.values()) {
         try {
            if (deviceFilter.filter(phidget)) {
               deviceFilter.detachHandler(phidget);
               return;
            }
         } catch (PhidgetException var6) {
            this.logger.error("Error detaching device", var6);
         }
      }
   }

   public boolean isOpen() {
      return this._isOpen;
   }

   protected synchronized void open() {
      if (!this._isOpen) {
         try {
            this.logger.info("Open Phidget Manager");
            this._phidgetManager.addAttachListener(this);
            this._phidgetManager.addDetachListener(this);
            this._phidgetManager.open();
            this._isOpen = true;
         } catch (PhidgetException var2) {
            this.logger.error("Error opening Phidget Manager", var2);
            this._isOpen = false;
         }
      }
   }

   private synchronized void close() {
      try {
         this.logger.info("Close Phidget Manager");
         this._phidgetManager.removeAttachListener(this);
         this._phidgetManager.removeDetachListener(this);
         this._phidgetManager.close();
         this._isOpen = false;
      } catch (PhidgetException var2) {
         this.logger.error("Error closing Phidget Manager", var2);
      }

      this._isOpen = false;
   }

   public interface DeviceFilter {
      boolean filter(Phidget var1) throws PhidgetException;

      void attachHandler(Phidget var1);

      void detachHandler(Phidget var1);
   }
}
