package org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MemoryData implements Data {
   private static final Logger LOGGER = Logger.getLogger(MemoryData.class.getName());
   private final byte[] data;
   private final int len;
   private final MIMEConfig config;
   private static final boolean DELETE_ON_EXIT = Boolean.getBoolean("org.jvnet.mimepull.deletetemponexit");

   MemoryData(ByteBuffer buf, MIMEConfig config) {
      this.data = buf.array();
      this.len = buf.limit();
      this.config = config;
   }

   @Override
   public int size() {
      return this.len;
   }

   @Override
   public byte[] read() {
      return this.data;
   }

   @Override
   public long writeTo(DataFile file) {
      return file.writeTo(this.data, 0, this.len);
   }

   @Override
   public Data createNext(DataHead dataHead, ByteBuffer buf) {
      if (!this.config.isOnlyMemory() && dataHead.inMemory >= this.config.memoryThreshold) {
         try {
            String prefix = this.config.getTempFilePrefix();
            String suffix = this.config.getTempFileSuffix();
            File tempFile = createTempFile(prefix, suffix, this.config.getTempDir());
            if (DELETE_ON_EXIT) {
               tempFile.deleteOnExit();
            }

            if (LOGGER.isLoggable(Level.FINE)) {
               LOGGER.log(Level.FINE, "Created temp file = {0}", tempFile);
            }

            dataHead.dataFile = new DataFile(tempFile);
         } catch (IOException var6) {
            throw new MIMEParsingException(var6);
         }

         if (dataHead.head != null) {
            for (Chunk c = dataHead.head; c != null; c = c.next) {
               long pointer = c.data.writeTo(dataHead.dataFile);
               c.data = new FileData(dataHead.dataFile, pointer, this.len);
            }
         }

         return new FileData(dataHead.dataFile, buf);
      } else {
         return new MemoryData(buf, this.config);
      }
   }

   private static File createTempFile(String prefix, String suffix, File dir) throws IOException {
      if (dir != null) {
         Path path = dir.toPath();
         return Files.createTempFile(path, prefix, suffix).toFile();
      } else {
         return Files.createTempFile(prefix, suffix).toFile();
      }
   }
}
