package org.eclipse.jetty.websocket.core.internal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.core.AbstractExtension;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameCaptureExtension extends AbstractExtension {
   private static final Logger LOG = LoggerFactory.getLogger(FrameCaptureExtension.class);
   private static final int BUFSIZE = 32768;
   private Generator generator;
   private Path outputDir;
   private String prefix = "frame";
   private Path incomingFramesPath;
   private Path outgoingFramesPath;
   private final AtomicInteger incomingCount = new AtomicInteger(0);
   private final AtomicInteger outgoingCount = new AtomicInteger(0);
   private SeekableByteChannel incomingChannel;
   private SeekableByteChannel outgoingChannel;

   @Override
   public String getName() {
      return "@frame-capture";
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      this.saveFrame(frame, false);

      try {
         this.nextIncomingFrame(frame, callback);
      } catch (Throwable var4) {
         IO.close(this.incomingChannel);
         this.incomingChannel = null;
         throw var4;
      }
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      this.saveFrame(frame, true);

      try {
         this.nextOutgoingFrame(frame, callback, batch);
      } catch (Throwable var5) {
         IO.close(this.outgoingChannel);
         this.outgoingChannel = null;
         throw var5;
      }
   }

   private void saveFrame(Frame frame, boolean outgoing) {
      if (this.outputDir != null && this.generator != null) {
         SeekableByteChannel channel = outgoing ? this.outgoingChannel : this.incomingChannel;
         if (channel != null) {
            RetainableByteBuffer buffer = this.getByteBufferPool().acquire(32768, false);
            ByteBuffer byteBuffer = buffer.getByteBuffer();

            try {
               Frame f = Frame.copy(frame);
               f.setMask(null);
               this.generator.generateHeader(f, byteBuffer);
               channel.write(byteBuffer);
               if (frame.hasPayload()) {
                  channel.write(frame.getPayload().slice());
               }

               if (LOG.isDebugEnabled()) {
                  LOG.debug(
                     "Saved {} frame #{}",
                     outgoing ? "outgoing" : "incoming",
                     outgoing ? this.outgoingCount.incrementAndGet() : this.incomingCount.incrementAndGet()
                  );
               }
            } catch (IOException var10) {
               LOG.warn("Unable to save frame: {}", frame, var10);
            } finally {
               buffer.release();
            }
         }
      }
   }

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      super.init(config, components);
      String cfgOutputDir = config.getParameter("output-dir", null);
      if (StringUtil.isNotBlank(cfgOutputDir)) {
         Path path = new File(cfgOutputDir).toPath();
         if (Files.isDirectory(path) && Files.exists(path) && Files.isWritable(path)) {
            this.outputDir = path;
         } else {
            LOG.warn("Unable to configure {}: not a valid output directory", path.toAbsolutePath().toString());
         }
      }

      String cfgPrefix = config.getParameter("prefix", "frame");
      if (StringUtil.isNotBlank(cfgPrefix)) {
         this.prefix = cfgPrefix;
      }

      if (this.outputDir != null) {
         try {
            Path dir = this.outputDir.toRealPath();
            String tstamp = String.format("%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS", Calendar.getInstance());
            this.incomingFramesPath = dir.resolve(String.format("%s-%s-incoming.dat", this.prefix, tstamp));
            this.outgoingFramesPath = dir.resolve(String.format("%s-%s-outgoing.dat", this.prefix, tstamp));
            this.incomingChannel = Files.newByteChannel(this.incomingFramesPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            this.outgoingChannel = Files.newByteChannel(this.outgoingFramesPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            this.generator = new Generator();
         } catch (IOException var7) {
            LOG.warn("Unable to create capture file(s)", var7);
         }
      }
   }
}
