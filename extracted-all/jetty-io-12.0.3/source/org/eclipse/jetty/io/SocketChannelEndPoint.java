package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketChannelEndPoint extends SelectableChannelEndPoint {
   private static final Logger LOG = LoggerFactory.getLogger(SocketChannelEndPoint.class);

   public SocketChannelEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
      super(scheduler, channel, selector, key);
   }

   public SocketChannel getChannel() {
      return (SocketChannel)super.getChannel();
   }

   @Override
   public SocketAddress getRemoteSocketAddress() {
      try {
         return this.getChannel().getRemoteAddress();
      } catch (Throwable var2) {
         if (LOG.isTraceEnabled()) {
            LOG.trace("Could not retrieve remote socket address", var2);
         }

         return null;
      }
   }

   @Override
   protected void doShutdownOutput() {
      try {
         this.getChannel().shutdownOutput();
      } catch (Throwable var2) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not shutdown output for {}", this.getChannel(), var2);
         }
      }
   }

   @Override
   public int fill(ByteBuffer buffer) throws IOException {
      if (this.isInputShutdown()) {
         return -1;
      } else {
         int pos = BufferUtil.flipToFill(buffer);

         int filled;
         try {
            filled = this.getChannel().read(buffer);
            if (filled > 0) {
               this.notIdle();
            } else if (filled == -1) {
               this.shutdownInput();
            }
         } catch (IOException var8) {
            LOG.debug("Unable to shutdown output", var8);
            this.shutdownInput();
            filled = -1;
         } finally {
            BufferUtil.flipToFlush(buffer, pos);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("filled {} {}", filled, BufferUtil.toDetailString(buffer));
         }

         return filled;
      }
   }

   @Override
   public boolean flush(ByteBuffer... buffers) throws IOException {
      long flushed;
      try {
         flushed = this.getChannel().write(buffers);
         if (LOG.isDebugEnabled()) {
            LOG.debug("flushed {} {}", flushed, this);
         }
      } catch (IOException var8) {
         throw new EofException(var8);
      }

      if (flushed > 0L) {
         this.notIdle();
      }

      for (ByteBuffer b : buffers) {
         if (!BufferUtil.isEmpty(b)) {
            return false;
         }
      }

      return true;
   }
}
