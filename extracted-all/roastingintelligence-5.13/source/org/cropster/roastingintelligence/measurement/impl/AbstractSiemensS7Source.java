package org.cropster.roastingintelligence.measurement.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import net.sourceforge.snap7.moka7.S7Client;
import org.apache.commons.io.IOUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSiemensS7Source extends AbstractMeasurementSource {
   private static final int RACK = 0;
   private static final int SLOT = 0;
   private final Logger logger = LoggerFactory.getLogger(AbstractSiemensS7Source.class);
   private final InetAddress _ipAddress;
   private final int _slot;
   protected volatile S7Client _client;
   private volatile boolean _ready;
   private ReentrantLock _connectLock = new ReentrantLock();

   public AbstractSiemensS7Source(MeasurementSourceFactory factory, InetAddress ipAddress, int slot) {
      super(factory);
      this._ipAddress = ipAddress;
      this._slot = slot;
   }

   public AbstractSiemensS7Source(MeasurementSourceFactory factory, InetAddress ipAddress) {
      this(factory, ipAddress, 0);
   }

   protected void handleError(int errorCode, byte[] buffer) throws IOException {
      if (errorCode != 0) {
         IOUtils.closeQuietly(this);
         throw new IOException(String.format("Error reading from Siemens PLC: %s - %s", errorCode, S7Client.ErrorText(errorCode)));
      } else {
         if (this.logger.isTraceEnabled()) {
            this.logger.trace(Arrays.toString(buffer));
         }
      }
   }

   @Override
   public void open() {
      this._connectLock.lock();
      if (this._ready) {
         this._connectLock.unlock();
      } else {
         try {
            String ip = this._ipAddress.getHostAddress();
            this._client = new S7Client();
            this._client.RecvTimeout = 300;
            int errorCode = this._client.ConnectTo(ip, 0, this._slot);
            if (errorCode == 0) {
               this._ready = true;
               this.fireStatusChanged();
            } else {
               this.logger.error("Error connecting to Siemens PLC (slot {}): {} - {}", new Object[]{this._slot, errorCode, S7Client.ErrorText(errorCode)});
            }
         } catch (Exception var3) {
            this.logger.error("Error opening PLC connection", var3);
         }

         this._connectLock.unlock();
      }
   }

   @Override
   public boolean isReady() {
      return this._ready;
   }

   @Override
   public void configure(Collection<Channel> channels) {
   }

   @Override
   public Double getValue(String channelName) {
      return this.getValues().get(channelName);
   }

   @Override
   public synchronized void close() throws IOException {
      this._ready = false;
      this._client.Disconnect();
      this.fireStatusChanged();
   }
}
