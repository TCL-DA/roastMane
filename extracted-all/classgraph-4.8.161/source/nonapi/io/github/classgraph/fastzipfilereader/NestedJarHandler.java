package nonapi.io.github.classgraph.fastzipfilereader;

import io.github.classgraph.ModuleReaderProxy;
import io.github.classgraph.ModuleRef;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.fileslice.ArraySlice;
import nonapi.io.github.classgraph.fileslice.FileSlice;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.recycler.Resettable;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class NestedJarHandler {
   public final ScanSpec scanSpec;
   public ReflectionUtils reflectionUtils;
   private SingletonMap<File, PhysicalZipFile, IOException> canonicalFileToPhysicalZipFileMap = new SingletonMap<File, PhysicalZipFile, IOException>() {
      public PhysicalZipFile newInstance(File canonicalFile, LogNode log) throws IOException {
         return new PhysicalZipFile(canonicalFile, NestedJarHandler.this, log);
      }
   };
   private SingletonMap<FastZipEntry, ZipFileSlice, IOException> fastZipEntryToZipFileSliceMap = new SingletonMap<FastZipEntry, ZipFileSlice, IOException>() {
      public ZipFileSlice newInstance(FastZipEntry childZipEntry, LogNode log) throws IOException, InterruptedException {
         ZipFileSlice childZipEntrySlice;
         if (!childZipEntry.isDeflated) {
            childZipEntrySlice = new ZipFileSlice(childZipEntry);
         } else {
            if (log != null) {
               log.log("Inflating nested zip entry: " + childZipEntry + " ; uncompressed size: " + childZipEntry.uncompressedSize);
            }

            PhysicalZipFile physicalZipFile = new PhysicalZipFile(
               childZipEntry.getSlice().open(),
               childZipEntry.uncompressedSize >= 0L && childZipEntry.uncompressedSize <= 2147483639L ? (long)((int)childZipEntry.uncompressedSize) : -1L,
               childZipEntry.entryName,
               NestedJarHandler.this,
               log
            );
            childZipEntrySlice = new ZipFileSlice(physicalZipFile, childZipEntry);
         }

         return childZipEntrySlice;
      }
   };
   private SingletonMap<ZipFileSlice, LogicalZipFile, IOException> zipFileSliceToLogicalZipFileMap = new SingletonMap<ZipFileSlice, LogicalZipFile, IOException>(
      
   ) {
      public LogicalZipFile newInstance(ZipFileSlice zipFileSlice, LogNode log) throws IOException, InterruptedException {
         return new LogicalZipFile(zipFileSlice, NestedJarHandler.this, log);
      }
   };
   public SingletonMap<String, Entry<LogicalZipFile, String>, IOException> nestedPathToLogicalZipFileAndPackageRootMap = new SingletonMap<String, Entry<LogicalZipFile, String>, IOException>(
      
   ) {
      public Entry<LogicalZipFile, String> newInstance(String nestedJarPathRaw, LogNode log) throws IOException, InterruptedException {
         String nestedJarPath = FastPathResolver.resolve(nestedJarPathRaw);
         int lastPlingIdx = nestedJarPath.lastIndexOf(33);
         if (lastPlingIdx < 0) {
            boolean isURL = JarUtils.URL_SCHEME_PATTERN.matcher(nestedJarPath).matches();
            PhysicalZipFile physicalZipFile;
            if (isURL) {
               String scheme = nestedJarPath.substring(0, nestedJarPath.indexOf(58));
               if (NestedJarHandler.this.scanSpec.allowedURLSchemes == null || !NestedJarHandler.this.scanSpec.allowedURLSchemes.contains(scheme)) {
                  throw new IOException("Scanning of URL scheme \"" + scheme + "\" has not been enabled -- cannot scan classpath element: " + nestedJarPath);
               }

               physicalZipFile = NestedJarHandler.this.downloadJarFromURL(nestedJarPath, log);
            } else {
               try {
                  File canonicalFile = new File(nestedJarPath).getCanonicalFile();
                  physicalZipFile = NestedJarHandler.this.canonicalFileToPhysicalZipFileMap.get(canonicalFile, log);
               } catch (SingletonMap.NewInstanceException | SingletonMap.NullSingletonException var23) {
                  throw new IOException(
                     "Could not get PhysicalZipFile for path " + nestedJarPath + " : " + (var23.getCause() == null ? var23 : var23.getCause())
                  );
               } catch (SecurityException var24) {
                  throw new IOException("Path component " + nestedJarPath + " could not be canonicalized: " + var24);
               }
            }

            ZipFileSlice topLevelSlice = new ZipFileSlice(physicalZipFile);

            LogicalZipFile logicalZipFile;
            try {
               logicalZipFile = NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(topLevelSlice, log);
            } catch (SingletonMap.NullSingletonException var15) {
               throw new IOException("Could not get toplevel slice " + topLevelSlice + " : " + var15);
            } catch (SingletonMap.NewInstanceException var16) {
               throw new IOException("Could not get toplevel slice " + topLevelSlice, var16);
            }

            return new SimpleEntry<>(logicalZipFile, "");
         } else {
            String parentPath = nestedJarPath.substring(0, lastPlingIdx);
            String childPath = nestedJarPath.substring(lastPlingIdx + 1);
            childPath = FileUtils.sanitizeEntryPath(childPath, true, true);

            Entry<LogicalZipFile, String> parentLogicalZipFileAndPackageRoot;
            try {
               parentLogicalZipFileAndPackageRoot = NestedJarHandler.this.nestedPathToLogicalZipFileAndPackageRootMap.get(parentPath, log);
            } catch (SingletonMap.NullSingletonException var21) {
               throw new IOException("Could not get parent logical zipfile " + parentPath + " : " + var21);
            } catch (SingletonMap.NewInstanceException var22) {
               throw new IOException("Could not get parent logical zipfile " + parentPath, var22);
            }

            LogicalZipFile parentLogicalZipFile = parentLogicalZipFileAndPackageRoot.getKey();

            boolean isDirectory;
            for (isDirectory = false; childPath.endsWith("/"); childPath = childPath.substring(0, childPath.length() - 1)) {
               isDirectory = true;
            }

            FastZipEntry childZipEntry = null;
            if (!isDirectory) {
               for (FastZipEntry entry : parentLogicalZipFile.entries) {
                  if (entry.entryName.equals(childPath)) {
                     childZipEntry = entry;
                     break;
                  }
               }
            }

            if (childZipEntry == null) {
               String childPathPrefix = childPath + "/";

               for (FastZipEntry entryx : parentLogicalZipFile.entries) {
                  if (entryx.entryName.startsWith(childPathPrefix)) {
                     isDirectory = true;
                     break;
                  }
               }
            }

            if (isDirectory) {
               if (!childPath.isEmpty()) {
                  if (log != null) {
                     log.log("Path " + childPath + " in jarfile " + parentLogicalZipFile + " is a directory, not a file -- using as package root");
                  }

                  parentLogicalZipFile.classpathRoots.add(childPath);
               }

               return new SimpleEntry<>(parentLogicalZipFile, childPath);
            } else if (childZipEntry == null) {
               throw new IOException("Path " + childPath + " does not exist in jarfile " + parentLogicalZipFile);
            } else if (!NestedJarHandler.this.scanSpec.scanNestedJars) {
               throw new IOException("Nested jar scanning is disabled -- skipping nested jar " + nestedJarPath);
            } else {
               ZipFileSlice childZipEntrySlice;
               try {
                  childZipEntrySlice = NestedJarHandler.this.fastZipEntryToZipFileSliceMap.get(childZipEntry, log);
               } catch (SingletonMap.NullSingletonException var19) {
                  throw new IOException("Could not get child zip entry slice " + childZipEntry + " : " + var19);
               } catch (SingletonMap.NewInstanceException var20) {
                  throw new IOException("Could not get child zip entry slice " + childZipEntry, var20);
               }

               LogNode zipSliceLog = log == null ? null : log.log("Getting zipfile slice " + childZipEntrySlice + " for nested jar " + childZipEntry.entryName);

               LogicalZipFile childLogicalZipFile;
               try {
                  childLogicalZipFile = NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(childZipEntrySlice, zipSliceLog);
               } catch (SingletonMap.NullSingletonException var17) {
                  throw new IOException("Could not get child logical zipfile " + childZipEntrySlice + " : " + var17);
               } catch (SingletonMap.NewInstanceException var18) {
                  throw new IOException("Could not get child logical zipfile " + childZipEntrySlice, var18);
               }

               return new SimpleEntry<>(childLogicalZipFile, "");
            }
         }
      }
   };
   public SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap = new SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException>(
      
   ) {
      public Recycler<ModuleReaderProxy, IOException> newInstance(final ModuleRef moduleRef, LogNode ignored) {
         return new Recycler<ModuleReaderProxy, IOException>() {
            public ModuleReaderProxy newInstance() throws IOException {
               return moduleRef.open();
            }
         };
      }
   };
   private Recycler<NestedJarHandler.RecyclableInflater, RuntimeException> inflaterRecycler = new Recycler<NestedJarHandler.RecyclableInflater, RuntimeException>(
      
   ) {
      public NestedJarHandler.RecyclableInflater newInstance() throws RuntimeException {
         return new NestedJarHandler.RecyclableInflater();
      }
   };
   private Set<Slice> openSlices = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private Set<File> tempFiles = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public static final String TEMP_FILENAME_LEAF_SEPARATOR = "---";
   private final AtomicBoolean closed = new AtomicBoolean(false);
   public InterruptionChecker interruptionChecker;
   private static final int DEFAULT_BUFFER_SIZE = 16384;
   private static final int MAX_INITIAL_BUFFER_SIZE = 16777216;
   private static final int HTTP_TIMEOUT = 5000;
   private static Method runFinalizationMethod;

   public NestedJarHandler(ScanSpec scanSpec, InterruptionChecker interruptionChecker, ReflectionUtils reflectionUtils) {
      this.scanSpec = scanSpec;
      this.interruptionChecker = interruptionChecker;
      this.reflectionUtils = reflectionUtils;
   }

   private static String leafname(String path) {
      return path.substring(path.lastIndexOf(47) + 1);
   }

   private String sanitizeFilename(String filename) {
      return filename.replace('/', '_').replace('\\', '_').replace(':', '_').replace('?', '_').replace('&', '_').replace('=', '_').replace(' ', '_');
   }

   public File makeTempFile(String filePathBase, boolean onlyUseLeafname) throws IOException {
      File tempFile = File.createTempFile("ClassGraph--", "---" + this.sanitizeFilename(onlyUseLeafname ? leafname(filePathBase) : filePathBase));
      tempFile.deleteOnExit();
      this.tempFiles.add(tempFile);
      return tempFile;
   }

   void removeTempFile(File tempFile) throws IOException, SecurityException {
      if (this.tempFiles.remove(tempFile)) {
         Files.delete(tempFile.toPath());
      } else {
         throw new IOException("Not a temp file: " + tempFile);
      }
   }

   public void markSliceAsOpen(Slice slice) throws IOException {
      this.openSlices.add(slice);
   }

   public void markSliceAsClosed(Slice slice) {
      this.openSlices.remove(slice);
   }

   private PhysicalZipFile downloadJarFromURL(String jarURL, LogNode log) throws IOException, InterruptedException {
      URL url = null;

      try {
         url = new URL(jarURL);
      } catch (MalformedURLException var50) {
         try {
            url = new URI(jarURL).toURL();
         } catch (IllegalArgumentException | URISyntaxException | MalformedURLException var49) {
            throw new IOException("Could not parse URL: " + jarURL);
         }
      }

      String scheme = url.getProtocol();
      if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
         try {
            Path path = Paths.get(url.toURI());
            FileSystem fs = path.getFileSystem();
            if (log != null) {
               log.log("URL " + jarURL + " is backed by filesystem " + fs.getClass().getName());
            }

            return new PhysicalZipFile(path, this, log);
         } catch (SecurityException | URISyntaxException | IllegalArgumentException var57) {
            throw new IOException("Could not convert URL to URI (" + var57 + "): " + url);
         } catch (FileSystemNotFoundException var58) {
         }
      }

      PhysicalZipFile var13;
      try (NestedJarHandler.CloseableUrlConnection urlConn = new NestedJarHandler.CloseableUrlConnection(url)) {
         long contentLengthHint = -1L;
         urlConn.conn.setConnectTimeout(5000);
         urlConn.conn.connect();
         if (urlConn.httpConn != null) {
            if (urlConn.httpConn.getResponseCode() != 200) {
               throw new IOException("Got response code " + urlConn.httpConn.getResponseCode() + " for URL " + url);
            }
         } else if (url.getProtocol().equalsIgnoreCase("file")) {
            try {
               File file = Paths.get(url.toURI()).toFile();
               return new PhysicalZipFile(file, this, log);
            } catch (Exception var51) {
            }
         }

         contentLengthHint = urlConn.conn.getContentLengthLong();
         if (contentLengthHint < -1L) {
            contentLengthHint = -1L;
         }

         LogNode subLog = log == null ? null : log.log("Downloading jar from URL " + jarURL);

         try (InputStream inputStream = urlConn.conn.getInputStream()) {
            PhysicalZipFile physicalZipFile = new PhysicalZipFile(inputStream, contentLengthHint, jarURL, this, subLog);
            if (subLog != null) {
               subLog.addElapsedTime();
               subLog.log(
                  "***** Note that it is time-consuming to scan jars at non-\"file:\" URLs, the URL must be opened (possibly after an http(s) fetch) for every scan, and the same URL must also be separately opened by the ClassLoader *****"
               );
            }

            var13 = physicalZipFile;
         } catch (MalformedURLException var54) {
            throw new IOException("Malformed URL: " + jarURL);
         }
      }

      return var13;
   }

   public InputStream openInflaterInputStream(final InputStream rawInputStream) throws IOException {
      return new InputStream() {
         private final NestedJarHandler.RecyclableInflater recyclableInflater = NestedJarHandler.this.inflaterRecycler.acquire();
         private final Inflater inflater = NestedJarHandler.super.recyclableInflater.getInflater();
         private final AtomicBoolean closed = new AtomicBoolean();
         private final byte[] buf = new byte[8192];
         private static final int INFLATE_BUF_SIZE = 8192;

         @Override
         public int read() throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else if (this.inflater.finished()) {
               return -1;
            } else {
               int numDeflatedBytesRead = this.read(this.buf, 0, 1);
               return numDeflatedBytesRead < 0 ? -1 : this.buf[0] & 0xFF;
            }
         }

         @Override
         public int read(byte[] outBuf, int off, int len) throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else if (len < 0) {
               throw new IllegalArgumentException("len cannot be negative");
            } else if (len == 0) {
               return 0;
            } else {
               try {
                  int totInflatedBytes = 0;

                  while (!this.inflater.finished() && totInflatedBytes < len) {
                     int numInflatedBytes = this.inflater.inflate(outBuf, off + totInflatedBytes, len - totInflatedBytes);
                     if (numInflatedBytes == 0) {
                        if (this.inflater.needsDictionary()) {
                           throw new IOException("Inflater needs preset dictionary");
                        }

                        if (this.inflater.needsInput()) {
                           int numRawBytesRead = rawInputStream.read(this.buf, 0, this.buf.length);
                           if (numRawBytesRead == -1) {
                              this.buf[0] = 0;
                              this.inflater.setInput(this.buf, 0, 1);
                           } else {
                              this.inflater.setInput(this.buf, 0, numRawBytesRead);
                           }
                        }
                     } else {
                        totInflatedBytes += numInflatedBytes;
                     }
                  }

                  return totInflatedBytes == 0 ? -1 : totInflatedBytes;
               } catch (DataFormatException var7) {
                  throw new ZipException(var7.getMessage() != null ? var7.getMessage() : "Invalid deflated zip entry data");
               }
            }
         }

         @Override
         public long skip(long numToSkip) throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else if (numToSkip < 0L) {
               throw new IllegalArgumentException("numToSkip cannot be negative");
            } else if (numToSkip == 0L) {
               return 0L;
            } else if (this.inflater.finished()) {
               return -1L;
            } else {
               long totBytesSkipped = 0L;

               while (true) {
                  int readLen = (int)Math.min(numToSkip - totBytesSkipped, (long)this.buf.length);
                  int numBytesRead = this.read(this.buf, 0, readLen);
                  if (numBytesRead <= 0) {
                     return totBytesSkipped;
                  }

                  totBytesSkipped -= (long)numBytesRead;
               }
            }
         }

         @Override
         public int available() throws IOException {
            if (this.closed.get()) {
               throw new IOException("Already closed");
            } else {
               return this.inflater.finished() ? 0 : 1;
            }
         }

         @Override
         public synchronized void mark(int readlimit) {
            throw new IllegalArgumentException("Not supported");
         }

         @Override
         public synchronized void reset() throws IOException {
            throw new IllegalArgumentException("Not supported");
         }

         @Override
         public boolean markSupported() {
            return false;
         }

         @Override
         public void close() {
            if (!this.closed.getAndSet(true)) {
               try {
                  rawInputStream.close();
               } catch (Exception var2) {
               }

               NestedJarHandler.this.inflaterRecycler.recycle(this.recyclableInflater);
            }
         }
      };
   }

   public Slice readAllBytesWithSpilloverToDisk(InputStream inputStream, String tempFileBaseName, long inputStreamLengthHint, LogNode log) throws IOException {
      try (InputStream inptStream = inputStream) {
         if (inputStreamLengthHint > (long)this.scanSpec.maxBufferedJarRAMSize) {
            return this.spillToDisk(inptStream, tempFileBaseName, null, null, log);
         } else {
            int bufSize = inputStreamLengthHint == -1L
               ? this.scanSpec.maxBufferedJarRAMSize
               : (inputStreamLengthHint == 0L ? 16384 : Math.min((int)inputStreamLengthHint, this.scanSpec.maxBufferedJarRAMSize));
            byte[] buf = new byte[bufSize];
            int bufLength = buf.length;
            int bufBytesUsed = 0;
            int bytesRead = 0;

            while ((bytesRead = inptStream.read(buf, bufBytesUsed, bufLength - bufBytesUsed)) > 0) {
               bufBytesUsed += bytesRead;
            }

            if (bytesRead == 0) {
               byte[] overflowBuf = new byte[1];
               int overflowBufBytesUsed = inptStream.read(overflowBuf, 0, 1);
               if (overflowBufBytesUsed == 1) {
                  return this.spillToDisk(inptStream, tempFileBaseName, buf, overflowBuf, log);
               }
            }

            if (bufBytesUsed < buf.length) {
               buf = Arrays.copyOf(buf, bufBytesUsed);
            }

            return new ArraySlice(buf, false, 0L, this);
         }
      }
   }

   private FileSlice spillToDisk(InputStream inputStream, String tempFileBaseName, byte[] buf, byte[] overflowBuf, LogNode log) throws IOException {
      File tempFile;
      try {
         tempFile = this.makeTempFile(tempFileBaseName, true);
      } catch (IOException var19) {
         throw new IOException("Could not create temporary file: " + var19.getMessage());
      }

      if (log != null) {
         log.log("Could not fit InputStream content into max RAM buffer size, saving to temporary file: " + tempFileBaseName + " -> " + tempFile);
      }

      try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
         if (buf != null) {
            outputStream.write(buf);
            outputStream.write(overflowBuf);
         }

         byte[] copyBuf = new byte[8192];

         int bytesRead;
         while ((bytesRead = inputStream.read(copyBuf, 0, copyBuf.length)) > 0) {
            outputStream.write(copyBuf, 0, bytesRead);
         }
      }

      return new FileSlice(tempFile, this, log);
   }

   public static byte[] readAllBytesAsArray(InputStream inputStream, long uncompressedLengthHint) throws IOException {
      if (uncompressedLengthHint > 2147483639L) {
         throw new IOException("InputStream is too large to read");
      } else {
         byte[] var19;
         try (InputStream inptStream = inputStream) {
            int bufferSize = uncompressedLengthHint < 1L ? 16384 : Math.min((int)uncompressedLengthHint, 16777216);
            byte[] buf = new byte[bufferSize];
            int totBytesRead = 0;

            label109:
            while (true) {
               while ((bytesRead = inptStream.read(buf, totBytesRead, buf.length - totBytesRead)) <= 0) {
                  if (bytesRead < 0) {
                     break label109;
                  }

                  int extraByte = inptStream.read();
                  if (extraByte == -1) {
                     break label109;
                  }

                  if (buf.length == 2147483639) {
                     throw new IOException("InputStream too large to read into array");
                  }

                  buf = Arrays.copyOf(buf, (int)Math.min((long)buf.length * 2L, 2147483639L));
                  buf[totBytesRead++] = (byte)extraByte;
               }

               totBytesRead += bytesRead;
            }

            var19 = totBytesRead == buf.length ? buf : Arrays.copyOf(buf, totBytesRead);
         }

         return var19;
      }
   }

   public void close(LogNode log) {
      if (!this.closed.getAndSet(true)) {
         boolean interrupted = false;
         if (this.moduleRefToModuleReaderProxyRecyclerMap != null) {
            boolean completedWithoutInterruption = false;

            while (!completedWithoutInterruption) {
               try {
                  for (Recycler<ModuleReaderProxy, IOException> recycler : this.moduleRefToModuleReaderProxyRecyclerMap.values()) {
                     recycler.forceClose();
                  }

                  completedWithoutInterruption = true;
               } catch (InterruptedException var9) {
                  interrupted = true;
               }
            }

            this.moduleRefToModuleReaderProxyRecyclerMap.clear();
            this.moduleRefToModuleReaderProxyRecyclerMap = null;
         }

         if (this.zipFileSliceToLogicalZipFileMap != null) {
            this.zipFileSliceToLogicalZipFileMap.clear();
            this.zipFileSliceToLogicalZipFileMap = null;
         }

         if (this.nestedPathToLogicalZipFileAndPackageRootMap != null) {
            this.nestedPathToLogicalZipFileAndPackageRootMap.clear();
            this.nestedPathToLogicalZipFileAndPackageRootMap = null;
         }

         if (this.canonicalFileToPhysicalZipFileMap != null) {
            this.canonicalFileToPhysicalZipFileMap.clear();
            this.canonicalFileToPhysicalZipFileMap = null;
         }

         if (this.fastZipEntryToZipFileSliceMap != null) {
            this.fastZipEntryToZipFileSliceMap.clear();
            this.fastZipEntryToZipFileSliceMap = null;
         }

         if (this.openSlices != null) {
            while (!this.openSlices.isEmpty()) {
               for (Slice slice : new ArrayList<>(this.openSlices)) {
                  try {
                     slice.close();
                  } catch (IOException var7) {
                  }

                  this.markSliceAsClosed(slice);
               }
            }

            this.openSlices.clear();
            this.openSlices = null;
         }

         if (this.inflaterRecycler != null) {
            this.inflaterRecycler.forceClose();
            this.inflaterRecycler = null;
         }

         if (this.tempFiles != null) {
            LogNode rmLog = !this.tempFiles.isEmpty() && log != null ? log.log("Removing temporary files") : null;

            while (!this.tempFiles.isEmpty()) {
               for (File tempFile : new ArrayList<>(this.tempFiles)) {
                  try {
                     this.removeTempFile(tempFile);
                  } catch (SecurityException | IOException var8) {
                     if (rmLog != null) {
                        rmLog.log("Removing temporary file failed: " + tempFile);
                     }
                  }
               }
            }

            this.tempFiles = null;
         }

         if (interrupted) {
            this.interruptionChecker.interrupt();
         }
      }
   }

   public void runFinalizationMethod() {
      if (runFinalizationMethod == null) {
         runFinalizationMethod = this.reflectionUtils.staticMethodForNameOrNull("System", "runFinalization");
      }

      if (runFinalizationMethod != null) {
         try {
            runFinalizationMethod.invoke(null);
         } catch (Throwable var2) {
         }
      }
   }

   public void closeDirectByteBuffer(ByteBuffer backingByteBuffer) {
      FileUtils.closeDirectByteBuffer(backingByteBuffer, this.reflectionUtils, null);
   }

   private static class CloseableUrlConnection implements AutoCloseable {
      public final URLConnection conn;
      public final HttpURLConnection httpConn;

      public CloseableUrlConnection(URL url) throws IOException {
         this.conn = url.openConnection();
         this.httpConn = this.conn instanceof HttpURLConnection ? (HttpURLConnection)this.conn : null;
      }

      @Override
      public void close() {
         if (this.httpConn != null) {
            this.httpConn.disconnect();
         }
      }
   }

   private static class RecyclableInflater implements Resettable, AutoCloseable {
      private final Inflater inflater = new Inflater(true);

      private RecyclableInflater() {
      }

      public Inflater getInflater() {
         return this.inflater;
      }

      @Override
      public void reset() {
         this.inflater.reset();
      }

      @Override
      public void close() {
         this.inflater.end();
      }
   }
}
