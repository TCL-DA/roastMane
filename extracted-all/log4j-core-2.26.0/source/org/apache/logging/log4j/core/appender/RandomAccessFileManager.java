package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class RandomAccessFileManager extends OutputStreamManager {
   static final int DEFAULT_BUFFER_SIZE = 262144;
   private final String advertiseURI;
   private final RandomAccessFile randomAccessFile;

   protected RandomAccessFileManager(
      final LoggerContext loggerContext,
      final RandomAccessFile file,
      final String fileName,
      final OutputStream os,
      final int bufferSize,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader
   ) {
      super(loggerContext, os, fileName, false, layout, writeHeader, ByteBuffer.wrap(new byte[bufferSize]));
      this.randomAccessFile = file;
      this.advertiseURI = advertiseURI;
   }

   public static RandomAccessFileManager getFileManager(
      final String fileName,
      final boolean append,
      final boolean immediateFlush,
      final int bufferSize,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final Configuration configuration
   ) {
      return narrow(RandomAccessFileManager.class, getManager(fileName, (name, data) -> {
         File file = new File(name);
         if (!append) {
            file.delete();
         }

         boolean writeHeader = !append || !file.exists();
         OutputStream os = NullOutputStream.getInstance();

         try {
            FileUtils.makeParentDirs(file);
            RandomAccessFile raf = new RandomAccessFile(name, "rw");
            if (append) {
               raf.seek(raf.length());
            } else {
               raf.setLength(0L);
            }

            return new RandomAccessFileManager(data.getLoggerContext(), raf, name, os, bufferSize, advertiseURI, layout, writeHeader);
         } catch (Exception var11) {
            LOGGER.error("RandomAccessFileManager (" + name + ") " + var11, var11);
            return null;
         }
      }, new ConfigurationFactoryData(configuration)));
   }

   @Deprecated
   public Boolean isEndOfBatch() {
      return Boolean.FALSE;
   }

   @Deprecated
   public void setEndOfBatch(final boolean endOfBatch) {
   }

   @Override
   protected void writeToDestination(final byte[] bytes, final int offset, final int length) {
      try {
         this.randomAccessFile.write(bytes, offset, length);
      } catch (IOException var6) {
         String msg = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(msg, var6);
      }
   }

   @Override
   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
   }

   @Override
   public synchronized boolean closeOutputStream() {
      this.flush();

      try {
         this.randomAccessFile.close();
         return true;
      } catch (IOException var2) {
         this.logError("Unable to close RandomAccessFile", var2);
         return false;
      }
   }

   public String getFileName() {
      return this.getName();
   }

   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   @Override
   public Map<String, String> getContentFormat() {
      Map<String, String> result = new HashMap<>(super.getContentFormat());
      result.put("fileURI", this.advertiseURI);
      return result;
   }
}
