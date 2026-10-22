package org.eclipse.jetty.websocket.core;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.compression.DeflaterPool;
import org.eclipse.jetty.util.compression.InflaterPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Abstract Extension")
public class AbstractExtension implements Extension {
   private final Logger log = LoggerFactory.getLogger(this.getClass());
   private CoreSession coreSession;
   private ByteBufferPool bufferPool;
   private ExtensionConfig config;
   private OutgoingFrames nextOutgoing;
   private IncomingFrames nextIncoming;
   private DeflaterPool deflaterPool;
   private InflaterPool inflaterPool;

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      this.config = config;
      this.bufferPool = components.getByteBufferPool();
      this.deflaterPool = components.getDeflaterPool();
      this.inflaterPool = components.getInflaterPool();
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      this.nextIncomingFrame(frame, callback);
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      this.nextOutgoingFrame(frame, callback, batch);
   }

   public ByteBufferPool getByteBufferPool() {
      return this.bufferPool;
   }

   @Override
   public ExtensionConfig getConfig() {
      return this.config;
   }

   public DeflaterPool getDeflaterPool() {
      return this.deflaterPool;
   }

   public InflaterPool getInflaterPool() {
      return this.inflaterPool;
   }

   @Override
   public String getName() {
      return this.config.getName();
   }

   @ManagedAttribute(
      name = "Next Incoming Frame Handler",
      readonly = true
   )
   public IncomingFrames getNextIncoming() {
      return this.nextIncoming;
   }

   @ManagedAttribute(
      name = "Next Outgoing Frame Handler",
      readonly = true
   )
   public OutgoingFrames getNextOutgoing() {
      return this.nextOutgoing;
   }

   @Override
   public boolean isRsv1User() {
      return false;
   }

   @Override
   public boolean isRsv2User() {
      return false;
   }

   @Override
   public boolean isRsv3User() {
      return false;
   }

   protected void nextIncomingFrame(Frame frame, Callback callback) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("nextIncomingFrame({})", frame);
      }

      this.nextIncoming.onFrame(frame, callback);
   }

   protected void nextOutgoingFrame(Frame frame, Callback callback, boolean batch) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("nextOutgoingFrame({})", frame);
      }

      this.nextOutgoing.sendFrame(frame, callback, batch);
   }

   @Override
   public void setNextIncomingFrames(IncomingFrames nextIncoming) {
      this.nextIncoming = nextIncoming;
   }

   @Override
   public void setNextOutgoingFrames(OutgoingFrames nextOutgoing) {
      this.nextOutgoing = nextOutgoing;
   }

   @Override
   public void setCoreSession(CoreSession coreSession) {
      this.coreSession = coreSession;
   }

   public CoreSession getCoreSession() {
      return this.coreSession;
   }

   protected Configuration getConfiguration() {
      return this.coreSession;
   }

   @Override
   public String toString() {
      return String.format("%s[%s]", this.getClass().getSimpleName(), this.config.getParameterizedName());
   }
}
