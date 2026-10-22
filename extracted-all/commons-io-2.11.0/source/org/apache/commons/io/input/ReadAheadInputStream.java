package org.apache.commons.io.input;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReadAheadInputStream extends InputStream {
   private static final ThreadLocal<byte[]> oneByte = ThreadLocal.withInitial(() -> new byte[1]);
   private final ReentrantLock stateChangeLock = new ReentrantLock();
   private ByteBuffer activeBuffer;
   private ByteBuffer readAheadBuffer;
   private boolean endOfStream;
   private boolean readInProgress;
   private boolean readAborted;
   private Throwable readException;
   private boolean isClosed;
   private boolean isUnderlyingInputStreamBeingClosed;
   private boolean isReading;
   private final AtomicBoolean isWaiting = new AtomicBoolean(false);
   private final InputStream underlyingInputStream;
   private final ExecutorService executorService;
   private final boolean shutdownExecutorService;
   private final Condition asyncReadComplete = this.stateChangeLock.newCondition();

   private static ExecutorService newExecutorService() {
      return Executors.newSingleThreadExecutor(ReadAheadInputStream::newThread);
   }

   private static Thread newThread(Runnable r) {
      Thread thread = new Thread(r, "commons-io-read-ahead");
      thread.setDaemon(true);
      return thread;
   }

   public ReadAheadInputStream(InputStream inputStream, int bufferSizeInBytes) {
      this(inputStream, bufferSizeInBytes, newExecutorService(), true);
   }

   public ReadAheadInputStream(InputStream inputStream, int bufferSizeInBytes, ExecutorService executorService) {
      this(inputStream, bufferSizeInBytes, executorService, false);
   }

   private ReadAheadInputStream(InputStream inputStream, int bufferSizeInBytes, ExecutorService executorService, boolean shutdownExecutorService) {
      if (bufferSizeInBytes <= 0) {
         throw new IllegalArgumentException("bufferSizeInBytes should be greater than 0, but the value is " + bufferSizeInBytes);
      } else {
         this.executorService = Objects.requireNonNull(executorService, "executorService");
         this.underlyingInputStream = Objects.requireNonNull(inputStream, "inputStream");
         this.shutdownExecutorService = shutdownExecutorService;
         this.activeBuffer = ByteBuffer.allocate(bufferSizeInBytes);
         this.readAheadBuffer = ByteBuffer.allocate(bufferSizeInBytes);
         ((Buffer)this.activeBuffer).flip();
         ((Buffer)this.readAheadBuffer).flip();
      }
   }

   @Override
   public int available() throws IOException {
      this.stateChangeLock.lock();

      int var1;
      try {
         var1 = (int)Math.min(2147483647L, (long)this.activeBuffer.remaining() + (long)this.readAheadBuffer.remaining());
      } finally {
         this.stateChangeLock.unlock();
      }

      return var1;
   }

   private void checkReadException() throws IOException {
      if (this.readAborted) {
         if (this.readException instanceof IOException) {
            throw (IOException)this.readException;
         } else {
            throw new IOException(this.readException);
         }
      }
   }

   @Override
   public void close() throws IOException {
      boolean isSafeToCloseUnderlyingInputStream = false;
      this.stateChangeLock.lock();

      try {
         if (this.isClosed) {
            return;
         }

         this.isClosed = true;
         if (!this.isReading) {
            isSafeToCloseUnderlyingInputStream = true;
            this.isUnderlyingInputStreamBeingClosed = true;
         }
      } finally {
         this.stateChangeLock.unlock();
      }

      if (this.shutdownExecutorService) {
         try {
            this.executorService.shutdownNow();
            this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
         } catch (InterruptedException var11) {
            InterruptedIOException iio = new InterruptedIOException(var11.getMessage());
            iio.initCause(var11);
            throw iio;
         } finally {
            if (isSafeToCloseUnderlyingInputStream) {
               this.underlyingInputStream.close();
            }
         }
      }
   }

   private void closeUnderlyingInputStreamIfNecessary() {
      boolean needToCloseUnderlyingInputStream = false;
      this.stateChangeLock.lock();

      try {
         this.isReading = false;
         if (this.isClosed && !this.isUnderlyingInputStreamBeingClosed) {
            needToCloseUnderlyingInputStream = true;
         }
      } finally {
         this.stateChangeLock.unlock();
      }

      if (needToCloseUnderlyingInputStream) {
         try {
            this.underlyingInputStream.close();
         } catch (IOException var5) {
         }
      }
   }

   private boolean isEndOfStream() {
      return !this.activeBuffer.hasRemaining() && !this.readAheadBuffer.hasRemaining() && this.endOfStream;
   }

   @Override
   public int read() throws IOException {
      if (this.activeBuffer.hasRemaining()) {
         return this.activeBuffer.get() & 0xFF;
      } else {
         byte[] oneByteArray = oneByte.get();
         return this.read(oneByteArray, 0, 1) == -1 ? -1 : oneByteArray[0] & 0xFF;
      }
   }

   @Override
   public int read(byte[] b, int offset, int len) throws IOException {
      if (offset < 0 || len < 0 || len > b.length - offset) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0) {
         return 0;
      } else {
         if (!this.activeBuffer.hasRemaining()) {
            this.stateChangeLock.lock();

            try {
               this.waitForAsyncReadComplete();
               if (!this.readAheadBuffer.hasRemaining()) {
                  this.readAsync();
                  this.waitForAsyncReadComplete();
                  if (this.isEndOfStream()) {
                     return -1;
                  }
               }

               this.swapBuffers();
               this.readAsync();
            } finally {
               this.stateChangeLock.unlock();
            }
         }

         len = Math.min(len, this.activeBuffer.remaining());
         this.activeBuffer.get(b, offset, len);
         return len;
      }
   }

   private void readAsync() throws IOException {
      this.stateChangeLock.lock();

      byte[] arr;
      label37: {
         try {
            arr = this.readAheadBuffer.array();
            if (!this.endOfStream && !this.readInProgress) {
               this.checkReadException();
               ((Buffer)this.readAheadBuffer).position(0);
               ((Buffer)this.readAheadBuffer).flip();
               this.readInProgress = true;
               break label37;
            }
         } finally {
            this.stateChangeLock.unlock();
         }

         return;
      }

      this.executorService.execute(() -> {
         this.stateChangeLock.lock();

         try {
            if (this.isClosed) {
               this.readInProgress = false;
               return;
            }

            this.isReading = true;
         } finally {
            this.stateChangeLock.unlock();
         }

         int read = 0;
         int off = 0;
         int len = arr.length;
         Throwable exception = null;

         try {
            do {
               read = this.underlyingInputStream.read(arr, off, len);
               if (read <= 0) {
                  break;
               }

               off += read;
               len -= read;
            } while (len > 0 && !this.isWaiting.get());
         } catch (Throwable var39) {
            exception = var39;
            if (var39 instanceof Error) {
               throw (Error)var39;
            }
         } finally {
            this.stateChangeLock.lock();

            try {
               ((Buffer)this.readAheadBuffer).limit(off);
               if (read < 0 || exception instanceof EOFException) {
                  this.endOfStream = true;
               } else if (exception != null) {
                  this.readAborted = true;
                  this.readException = exception;
               }

               this.readInProgress = false;
               this.signalAsyncReadComplete();
            } finally {
               this.stateChangeLock.unlock();
            }

            this.closeUnderlyingInputStreamIfNecessary();
         }
      });
   }

   private void signalAsyncReadComplete() {
      this.stateChangeLock.lock();

      try {
         this.asyncReadComplete.signalAll();
      } finally {
         this.stateChangeLock.unlock();
      }
   }

   @Override
   public long skip(long n) throws IOException {
      if (n <= 0L) {
         return 0L;
      } else if (n <= (long)this.activeBuffer.remaining()) {
         ((Buffer)this.activeBuffer).position((int)n + this.activeBuffer.position());
         return n;
      } else {
         this.stateChangeLock.lock();

         long skipped;
         try {
            skipped = this.skipInternal(n);
         } finally {
            this.stateChangeLock.unlock();
         }

         return skipped;
      }
   }

   private long skipInternal(long n) throws IOException {
      assert this.stateChangeLock.isLocked();

      this.waitForAsyncReadComplete();
      if (this.isEndOfStream()) {
         return 0L;
      } else if ((long)this.available() >= n) {
         int toSkip = (int)n;
         toSkip -= this.activeBuffer.remaining();

         assert toSkip > 0;

         ((Buffer)this.activeBuffer).position(0);
         ((Buffer)this.activeBuffer).flip();
         ((Buffer)this.readAheadBuffer).position(toSkip + this.readAheadBuffer.position());
         this.swapBuffers();
         this.readAsync();
         return n;
      } else {
         int skippedBytes = this.available();
         long toSkip = n - (long)skippedBytes;
         ((Buffer)this.activeBuffer).position(0);
         ((Buffer)this.activeBuffer).flip();
         ((Buffer)this.readAheadBuffer).position(0);
         ((Buffer)this.readAheadBuffer).flip();
         long skippedFromInputStream = this.underlyingInputStream.skip(toSkip);
         this.readAsync();
         return (long)skippedBytes + skippedFromInputStream;
      }
   }

   private void swapBuffers() {
      ByteBuffer temp = this.activeBuffer;
      this.activeBuffer = this.readAheadBuffer;
      this.readAheadBuffer = temp;
   }

   private void waitForAsyncReadComplete() throws IOException {
      this.stateChangeLock.lock();

      try {
         this.isWaiting.set(true);

         while (this.readInProgress) {
            this.asyncReadComplete.await();
         }
      } catch (InterruptedException var6) {
         InterruptedIOException iio = new InterruptedIOException(var6.getMessage());
         iio.initCause(var6);
         throw iio;
      } finally {
         this.isWaiting.set(false);
         this.stateChangeLock.unlock();
      }

      this.checkReadException();
   }
}
