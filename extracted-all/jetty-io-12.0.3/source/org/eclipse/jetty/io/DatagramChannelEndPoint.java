package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatagramChannelEndPoint extends SelectableChannelEndPoint {
   public static final SocketAddress EOF = InetSocketAddress.createUnresolved("", 0);
   private static final Logger LOG = LoggerFactory.getLogger(DatagramChannelEndPoint.class);

   public DatagramChannelEndPoint(DatagramChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
      super(scheduler, channel, selector, key);
   }

   public DatagramChannel getChannel() {
      return (DatagramChannel)super.getChannel();
   }

   public SocketAddress receive(ByteBuffer buffer) throws IOException {
      if (this.isInputShutdown()) {
         return EOF;
      } else {
         int pos = BufferUtil.flipToFill(buffer);
         SocketAddress peer = this.getChannel().receive(buffer);
         BufferUtil.flipToFlush(buffer, pos);
         if (peer == null) {
            return null;
         } else {
            this.notIdle();
            int filled = buffer.remaining();
            if (LOG.isDebugEnabled()) {
               LOG.debug("filled {} {}", filled, BufferUtil.toDetailString(buffer));
            }

            return peer;
         }
      }
   }

   public boolean send(SocketAddress address, ByteBuffer... buffers) throws IOException {
      boolean flushedAll = true;
      long flushed = 0L;

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug("flushing {} buffer(s) to {}", buffers.length, address);
         }

         for (ByteBuffer buffer : buffers) {
            int sent = this.getChannel().send(buffer, address);
            if (sent == 0) {
               flushedAll = false;
               break;
            }

            flushed += (long)sent;
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("flushed {} byte(s), all flushed? {} - {}", new Object[]{flushed, flushedAll, this});
         }
      } catch (IOException var11) {
         throw new EofException(var11);
      }

      if (flushed > 0L) {
         this.notIdle();
      }

      return flushedAll;
   }

   public void write(Callback callback, SocketAddress address, ByteBuffer... buffers) throws WritePendingException {
      this.getWriteFlusher().write(callback, address, buffers);
   }
}
