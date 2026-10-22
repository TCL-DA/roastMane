package org.eclipse.jetty.websocket.core.util;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.Configuration;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.internal.FrameEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FragmentingFlusher extends TransformingFlusher {
   private static final Logger LOG = LoggerFactory.getLogger(FragmentingFlusher.class);
   private final Configuration configuration;
   private FrameEntry current;
   private ByteBuffer payload;

   public FragmentingFlusher(Configuration configuration) {
      this.configuration = configuration;
   }

   protected abstract void forwardFrame(Frame var1, Callback var2, boolean var3);

   @Override
   protected boolean onFrame(Frame frame, Callback callback, boolean batch) {
      long maxFrameSize = this.configuration.getMaxFrameSize();
      if (!frame.isControlFrame() && maxFrameSize > 0L && (long)frame.getPayloadLength() > maxFrameSize) {
         this.current = new FrameEntry(frame, callback, batch);
         this.payload = frame.getPayload().slice();
         boolean finished = this.fragment(callback, true);
         if (finished) {
            this.current = null;
            this.payload = null;
         }

         return finished;
      } else {
         this.forwardFrame(frame, callback, batch);
         return true;
      }
   }

   @Override
   protected boolean transform(Callback callback) {
      boolean finished = this.fragment(callback, false);
      if (finished) {
         this.current = null;
         this.payload = null;
      }

      return finished;
   }

   private boolean fragment(Callback callback, boolean first) {
      Frame frame = this.current.frame;
      int remaining = this.payload.remaining();
      long maxFrameSize = this.configuration.getMaxFrameSize();
      int fragmentSize = (int)Math.min((long)remaining, maxFrameSize);
      boolean continuation = frame.getOpCode() == 0 || !first;
      Frame fragment = new Frame(continuation ? 0 : frame.getOpCode());
      boolean finished = maxFrameSize <= 0L || (long)remaining <= maxFrameSize;
      fragment.setFin(frame.isFin() && finished);
      if (finished) {
         fragment.setPayload(this.payload);
         this.forwardFrame(fragment, callback, this.current.batch);
         return true;
      } else {
         int limit = this.payload.limit();
         int newLimit = this.payload.position() + fragmentSize;
         this.payload.limit(newLimit);
         ByteBuffer payloadFragment = this.payload.slice();
         this.payload.limit(limit);
         fragment.setPayload(payloadFragment);
         this.payload.position(newLimit);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Fragmented {}->{}", frame, fragment);
         }

         this.forwardFrame(fragment, callback, this.current.batch);
         return false;
      }
   }
}
