package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;

public class FileManager extends OutputStreamManager {
   private final boolean isAppend;
   private final boolean createOnDemand;
   private final boolean isLocking;
   private final String advertiseURI;
   private final int bufferSize;
   private final Set<PosixFilePermission> filePermissions;
   private final String fileOwner;
   private final String fileGroup;
   private final boolean attributeViewEnabled;

   @Deprecated
   protected FileManager(
      final String fileName,
      final OutputStream os,
      final boolean append,
      final boolean locking,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final boolean writeHeader
   ) {
      this(fileName, os, append, locking, advertiseURI, layout, writeHeader, ByteBuffer.wrap(new byte[bufferSize]));
   }

   @Deprecated
   protected FileManager(
      final String fileName,
      final OutputStream os,
      final boolean append,
      final boolean locking,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(os, fileName, layout, writeHeader, buffer);
      this.isAppend = append;
      this.createOnDemand = false;
      this.isLocking = locking;
      this.advertiseURI = advertiseURI;
      this.bufferSize = buffer.capacity();
      this.filePermissions = null;
      this.fileOwner = null;
      this.fileGroup = null;
      this.attributeViewEnabled = false;
   }

   @Deprecated
   protected FileManager(
      final LoggerContext loggerContext,
      final String fileName,
      final OutputStream os,
      final boolean append,
      final boolean locking,
      final boolean createOnDemand,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(loggerContext, os, fileName, createOnDemand, layout, writeHeader, buffer);
      this.isAppend = append;
      this.createOnDemand = createOnDemand;
      this.isLocking = locking;
      this.advertiseURI = advertiseURI;
      this.bufferSize = buffer.capacity();
      this.filePermissions = null;
      this.fileOwner = null;
      this.fileGroup = null;
      this.attributeViewEnabled = false;
   }

   @SuppressFBWarnings(
      value = {"OVERLY_PERMISSIVE_FILE_PERMISSION"},
      justification = "File permissions are specified in the configuration file."
   )
   protected FileManager(
      final LoggerContext loggerContext,
      final String fileName,
      final OutputStream os,
      final boolean append,
      final boolean locking,
      final boolean createOnDemand,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final boolean writeHeader,
      final ByteBuffer buffer
   ) {
      super(loggerContext, os, fileName, createOnDemand, layout, writeHeader, buffer);
      this.isAppend = append;
      this.createOnDemand = createOnDemand;
      this.isLocking = locking;
      this.advertiseURI = advertiseURI;
      this.bufferSize = buffer.capacity();
      Set<String> views = FileSystems.getDefault().supportedFileAttributeViews();
      if (views.contains("posix")) {
         this.filePermissions = filePermissions != null ? PosixFilePermissions.fromString(filePermissions) : null;
         this.fileGroup = fileGroup;
      } else {
         this.filePermissions = null;
         this.fileGroup = null;
         if (filePermissions != null) {
            LOGGER.warn("Posix file attribute permissions defined but it is not supported by this files system.");
         }

         if (fileGroup != null) {
            LOGGER.warn("Posix file attribute group defined but it is not supported by this files system.");
         }
      }

      if (views.contains("owner")) {
         this.fileOwner = fileOwner;
      } else {
         this.fileOwner = null;
         if (fileOwner != null) {
            LOGGER.warn("Owner file attribute defined but it is not supported by this files system.");
         }
      }

      this.attributeViewEnabled = this.filePermissions != null || this.fileOwner != null || this.fileGroup != null;
   }

   public static FileManager getFileManager(
      final String fileName,
      final boolean append,
      boolean locking,
      final boolean bufferedIo,
      final boolean createOnDemand,
      final String advertiseURI,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final String filePermissions,
      final String fileOwner,
      final String fileGroup,
      final Configuration configuration
   ) {
      boolean actualLocking = !bufferedIo && locking;
      int actualBufferSize = bufferedIo ? bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
      return narrow(
         FileManager.class,
         getManager(
            fileName,
            (name, data) -> {
               Objects.requireNonNull(name, "filename is missing");
               File file = new File(name);

               try {
                  FileUtils.makeParentDirs(file);
                  boolean writeHeader = !append || !file.exists();
                  ByteBuffer byteBuffer = ByteBuffer.allocate(actualBufferSize);
                  FileOutputStream fos = createOnDemand ? null : new FileOutputStream(file, append);
                  FileManager fm = new FileManager(
                     data.getLoggerContext(),
                     name,
                     fos,
                     append,
                     actualLocking,
                     createOnDemand,
                     advertiseURI,
                     layout,
                     filePermissions,
                     fileOwner,
                     fileGroup,
                     writeHeader,
                     byteBuffer
                  );
                  if (fos != null && fm.attributeViewEnabled) {
                     fm.defineAttributeView(file.toPath());
                  }

                  return fm;
               } catch (IOException var16) {
                  LOGGER.error("FileManager ({}): {}", name, var16);
                  return null;
               }
            },
            new ConfigurationFactoryData(configuration)
         )
      );
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The destination file is specified in the configuration file."
   )
   @Override
   protected OutputStream createOutputStream() throws IOException {
      String filename = this.getFileName();
      LOGGER.debug("Now writing to {} at {}", filename, new Date());
      File file = new File(filename);
      this.createParentDir(file);
      FileOutputStream fos = new FileOutputStream(file, this.isAppend);
      if (file.exists() && file.length() == 0L) {
         try {
            FileTime now = FileTime.fromMillis(System.currentTimeMillis());
            Files.setAttribute(file.toPath(), "creationTime", now);
         } catch (Exception var5) {
            LOGGER.warn("Unable to set current file time for {}", filename);
         }

         this.writeHeader(fos);
      }

      this.defineAttributeView(Paths.get(filename));
      return fos;
   }

   protected void createParentDir(final File file) {
   }

   protected void defineAttributeView(final Path path) {
      if (this.attributeViewEnabled) {
         try {
            path.toFile().createNewFile();
            FileUtils.defineFilePosixAttributeView(path, this.filePermissions, this.fileOwner, this.fileGroup);
         } catch (Exception var3) {
            LOGGER.error("Could not define attribute view on path \"{}\" got {}", path, var3.getMessage(), var3);
         }
      }
   }

   @Override
   protected synchronized void write(final byte[] bytes, final int offset, final int length, final boolean immediateFlush) {
      if (this.isLocking) {
         try {
            FileChannel channel = ((FileOutputStream)this.getOutputStream()).getChannel();
            FileLock lock = channel.lock(0L, Long.MAX_VALUE, false);

            try {
               super.write(bytes, offset, length, immediateFlush);
            } catch (Throwable var10) {
               if (lock != null) {
                  try {
                     lock.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (lock != null) {
               lock.close();
            }
         } catch (IOException var11) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var11);
         }
      } else {
         super.write(bytes, offset, length, immediateFlush);
      }
   }

   @Override
   protected synchronized void writeToDestination(final byte[] bytes, final int offset, final int length) {
      if (this.isLocking) {
         try {
            FileChannel channel = ((FileOutputStream)this.getOutputStream()).getChannel();
            FileLock lock = channel.lock(0L, Long.MAX_VALUE, false);

            try {
               super.writeToDestination(bytes, offset, length);
            } catch (Throwable var9) {
               if (lock != null) {
                  try {
                     lock.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (lock != null) {
               lock.close();
            }
         } catch (IOException var10) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var10);
         }
      } else {
         super.writeToDestination(bytes, offset, length);
      }
   }

   public String getFileName() {
      return this.getName();
   }

   public boolean isAppend() {
      return this.isAppend;
   }

   public boolean isCreateOnDemand() {
      return this.createOnDemand;
   }

   public boolean isLocking() {
      return this.isLocking;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public Set<PosixFilePermission> getFilePermissions() {
      return this.filePermissions;
   }

   public String getFileOwner() {
      return this.fileOwner;
   }

   public String getFileGroup() {
      return this.fileGroup;
   }

   public boolean isAttributeViewEnabled() {
      return this.attributeViewEnabled;
   }

   @Override
   public Map<String, String> getContentFormat() {
      Map<String, String> result = new HashMap<>(super.getContentFormat());
      result.put("fileURI", this.advertiseURI);
      return result;
   }
}
