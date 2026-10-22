package org.eclipse.jetty.websocket.core.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.AbstractExtension;
import org.eclipse.jetty.websocket.core.Configuration;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.eclipse.jetty.websocket.core.util.DemandChain;
import org.eclipse.jetty.websocket.core.util.DemandingFlusher;
import org.eclipse.jetty.websocket.core.util.FragmentingFlusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentExtension extends AbstractExtension implements DemandChain {
   private static final Logger LOG = LoggerFactory.getLogger(FragmentExtension.class);
   private final FragmentingFlusher outgoingFlusher;
   private final DemandingFlusher incomingFlusher;
   private final Configuration configuration = new Configuration.ConfigurationCustomizer();

   public FragmentExtension() {
      this.outgoingFlusher = new FragmentingFlusher(this.configuration) {
         @Override
         protected void forwardFrame(Frame frame, Callback callback, boolean batch) {
            FragmentExtension.this.nextOutgoingFrame(frame, callback, batch);
         }
      };
      this.incomingFlusher = new FragmentExtension.FragmentingDemandingFlusher();
   }

   @Override
   public void demand() {
      this.incomingFlusher.demand();
   }

   @Override
   public void setNextDemand(DemandChain nextDemand) {
      this.incomingFlusher.setNextDemand(nextDemand);
   }

   @Override
   public String getName() {
      return "fragment";
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      this.incomingFlusher.onFrame(frame, callback);
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      this.outgoingFlusher.sendFrame(frame, callback, batch);
   }

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      super.init(config, components);
      int maxLength = config.getParameter("maxLength", -1);
      this.configuration.setMaxFrameSize((long)maxLength);
   }

   public class FragmentingDemandingFlusher extends DemandingFlusher {
      public FragmentingDemandingFlusher() {
         super((x$0, x$1) -> FragmentExtension.this.nextIncomingFrame(x$0, x$1));
      }

      @Override
      protected boolean handle(Frame frame, Callback callback, boolean first) {
         if (first && frame.isControlFrame()) {
            this.emitFrame(frame, callback);
            return true;
         } else {
            ByteBuffer payload = frame.getPayload();
            int remaining = payload.remaining();
            long maxFrameSize = FragmentExtension.this.configuration.getMaxFrameSize();
            int fragmentSize = (int)Math.min((long)remaining, maxFrameSize);
            boolean continuation = frame.getOpCode() == 0 || !first;
            Frame fragment = new Frame(continuation ? 0 : frame.getOpCode());
            boolean finished = maxFrameSize <= 0L || (long)remaining <= maxFrameSize;
            fragment.setFin(frame.isFin() && finished);
            if (finished) {
               fragment.setPayload(payload);
            } else {
               int limit = payload.limit();
               int newLimit = payload.position() + fragmentSize;
               payload.limit(newLimit);
               ByteBuffer payloadFragment = payload.slice();
               payload.limit(limit);
               fragment.setPayload(payloadFragment);
               payload.position(newLimit);
               if (FragmentExtension.LOG.isDebugEnabled()) {
                  FragmentExtension.LOG.debug("Fragmented {}->{}", frame, fragment);
               }
            }

            Callback payloadCallback = Callback.from(() -> {
               if (finished) {
                  callback.succeeded();
               }
            }, t -> {
               callback.failed(t);
               this.failFlusher(t);
            });
            this.emitFrame(fragment, payloadCallback);
            return finished;
         }
      }
   }
}
