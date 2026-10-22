package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.util.statistic.SampleStatistic;

public class StatisticsHandler extends EventsHandler {
   private final CounterStatistic _requestStats = new CounterStatistic();
   private final SampleStatistic _requestTimeStats = new SampleStatistic();
   private final CounterStatistic _handleStats = new CounterStatistic();
   private final SampleStatistic _handleTimeStats = new SampleStatistic();
   private final LongAdder _failures = new LongAdder();
   private final LongAdder _handlingFailures = new LongAdder();
   private final LongAdder _responses1xx = new LongAdder();
   private final LongAdder _responses2xx = new LongAdder();
   private final LongAdder _responses3xx = new LongAdder();
   private final LongAdder _responses4xx = new LongAdder();
   private final LongAdder _responses5xx = new LongAdder();
   private final LongAdder _bytesRead = new LongAdder();
   private final LongAdder _bytesWritten = new LongAdder();
   private long _startTime = NanoTime.now();

   public StatisticsHandler() {
   }

   public StatisticsHandler(Handler handler) {
      super(handler);
   }

   @Override
   protected void doStart() throws Exception {
      this.reset();
      super.doStart();
   }

   @Override
   protected void onBeforeHandling(Request request) {
      this._requestStats.increment();
      this._handleStats.increment();
   }

   @Override
   protected void onAfterHandling(Request request, boolean handled, Throwable failure) {
      if (failure != null) {
         this._handlingFailures.increment();
      }

      this._handleStats.decrement();
      this._handleTimeStats.record(NanoTime.since(request.getHeadersNanoTime()));
   }

   @Override
   protected void onRequestRead(Request request, Chunk chunk) {
      if (chunk != null) {
         this._bytesRead.add((long)chunk.remaining());
      }
   }

   @Override
   protected void onResponseBegin(Request request, int status, HttpFields headers) {
      switch (status / 100) {
         case 1:
            this._responses1xx.increment();
            break;
         case 2:
            this._responses2xx.increment();
            break;
         case 3:
            this._responses3xx.increment();
            break;
         case 4:
            this._responses4xx.increment();
            break;
         case 5:
            this._responses5xx.increment();
      }
   }

   @Override
   protected void onResponseWrite(Request request, boolean last, ByteBuffer content) {
      int length = BufferUtil.length(content);
      if (length > 0) {
         this._bytesWritten.add((long)length);
      }
   }

   @Override
   protected void onComplete(Request request, Throwable failure) {
      if (failure != null) {
         this._failures.increment();
      }

      this._requestTimeStats.record(NanoTime.since(request.getBeginNanoTime()));
      this._requestStats.decrement();
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(
         out,
         indent,
         new Object[]{
            Dumpable.named("requestStats", this._requestStats),
            Dumpable.named("requestTimeStats", this._requestTimeStats),
            Dumpable.named("handleStats", this._handleStats),
            Dumpable.named("handleTimeStats", this._handleTimeStats),
            Dumpable.named("failures", this._failures),
            Dumpable.named("handlingFailures", this._handlingFailures),
            Dumpable.named("1xxResponses", this._responses1xx),
            Dumpable.named("2xxResponses", this._responses2xx),
            Dumpable.named("3xxResponses", this._responses3xx),
            Dumpable.named("4xxResponses", this._responses4xx),
            Dumpable.named("5xxResponses", this._responses5xx),
            Dumpable.named("bytesRead", this._bytesRead),
            Dumpable.named("bytesWritten", this._bytesWritten)
         }
      );
   }

   @ManagedOperation(
      value = "resets the statistics",
      impact = "ACTION"
   )
   public void reset() {
      this._startTime = NanoTime.now();
      this._requestStats.reset();
      this._requestTimeStats.reset();
      this._handleStats.reset();
      this._handleTimeStats.reset();
      this._failures.reset();
      this._handlingFailures.reset();
      this._responses1xx.reset();
      this._responses2xx.reset();
      this._responses3xx.reset();
      this._responses4xx.reset();
      this._responses5xx.reset();
      this._bytesRead.reset();
      this._bytesWritten.reset();
   }

   @Deprecated
   @ManagedAttribute("number of requests")
   public int getRequests() {
      return (int)this._requestStats.getTotal();
   }

   @ManagedAttribute("total number of requests")
   public int getRequestTotal() {
      return (int)this._requestStats.getTotal();
   }

   @ManagedAttribute("current number of active requests")
   public int getRequestsActive() {
      return (int)this._requestStats.getCurrent();
   }

   @ManagedAttribute("maximum number of active requests")
   public int getRequestsActiveMax() {
      return (int)this._requestStats.getMax();
   }

   @ManagedAttribute("total time spent in request execution (in ns)")
   public long getRequestTimeTotal() {
      return this._requestTimeStats.getTotal();
   }

   @ManagedAttribute("maximum request execution time (in ns)")
   public long getRequestTimeMax() {
      return this._requestTimeStats.getMax();
   }

   @ManagedAttribute("mean request execution time (in ns)")
   public double getRequestTimeMean() {
      return this._requestTimeStats.getMean();
   }

   @ManagedAttribute("standard deviation for request execution time (in ns)")
   public double getRequestTimeStdDev() {
      return this._requestTimeStats.getStdDev();
   }

   @ManagedAttribute("total number of calls to handle()")
   public int getHandleTotal() {
      return (int)this._handleStats.getTotal();
   }

   @ManagedAttribute("current number of requests in handle()")
   public int getHandleActive() {
      return (int)this._handleStats.getCurrent();
   }

   @ManagedAttribute("maximum number of requests in handle()")
   public int getHandleActiveMax() {
      return (int)this._handleStats.getMax();
   }

   @ManagedAttribute("maximum handle() execution time (in ns)")
   public long getHandleTimeMax() {
      return this._handleTimeStats.getMax();
   }

   @ManagedAttribute("total time spent in handle() execution (in ns)")
   public long getHandleTimeTotal() {
      return this._handleTimeStats.getTotal();
   }

   @ManagedAttribute("mean handle() execution time (in ns)")
   public double getHandleTimeMean() {
      return this._handleTimeStats.getMean();
   }

   @ManagedAttribute("standard deviation for handle() execution time (in ns)")
   public double getHandleTimeStdDev() {
      return this._handleTimeStats.getStdDev();
   }

   @ManagedAttribute("number of failed requests")
   public int getFailures() {
      return this._failures.intValue();
   }

   @ManagedAttribute("number of requests with 1xx response status")
   public int getResponses1xx() {
      return this._responses1xx.intValue();
   }

   @ManagedAttribute("number of requests with 2xx response status")
   public int getResponses2xx() {
      return this._responses2xx.intValue();
   }

   @ManagedAttribute("number of requests with 3xx response status")
   public int getResponses3xx() {
      return this._responses3xx.intValue();
   }

   @ManagedAttribute("number of requests with 4xx response status")
   public int getResponses4xx() {
      return this._responses4xx.intValue();
   }

   @ManagedAttribute("number of requests with 5xx response status")
   public int getResponses5xx() {
      return this._responses5xx.intValue();
   }

   @ManagedAttribute("number of requests that threw an exception from handle()")
   public int getHandlingFailures() {
      return this._handlingFailures.intValue();
   }

   @ManagedAttribute("bytes read count")
   public long getBytesRead() {
      return this._bytesRead.longValue();
   }

   @ManagedAttribute("bytes written count")
   public long getBytesWritten() {
      return this._bytesWritten.longValue();
   }

   @ManagedAttribute("duration for which statistics have been collected")
   public Duration getStatisticsDuration() {
      return Duration.ofNanos(NanoTime.since(this._startTime));
   }

   public static class MinimumDataRateHandler extends StatisticsHandler {
      private final long _minimumReadRate;
      private final long _minimumWriteRate;

      public MinimumDataRateHandler(long minimumReadRate, long minimumWriteRate) {
         this(null, minimumReadRate, minimumWriteRate);
      }

      public MinimumDataRateHandler(Handler handler, long minimumReadRate, long minimumWriteRate) {
         super(handler);
         this._minimumReadRate = minimumReadRate;
         this._minimumWriteRate = minimumWriteRate;
      }

      @Override
      public boolean handle(Request request, Response response, Callback callback) throws Exception {
         StatisticsHandler.MinimumDataRateHandler.MinimumDataRateRequest wrappedRequest = new StatisticsHandler.MinimumDataRateHandler.MinimumDataRateRequest(
            request
         );
         StatisticsHandler.MinimumDataRateHandler.MinimumDataRateResponse wrappedResponse = new StatisticsHandler.MinimumDataRateHandler.MinimumDataRateResponse(
            wrappedRequest, response
         );
         return super.handle(wrappedRequest, wrappedResponse, callback);
      }

      protected class MinimumDataRateRequest extends Request.Wrapper {
         private Chunk _errorContent;

         private MinimumDataRateRequest(Request request) {
            super(request);
         }

         private long dataRatePerSecond(long dataCount) {
            if (dataCount == 0L) {
               return 0L;
            } else {
               long delayInNs = NanoTime.since(this.getHeadersNanoTime());
               return delayInNs <= 0L ? Long.MAX_VALUE : dataCount * 1000000000L / delayInNs;
            }
         }

         @Override
         public void demand(Runnable demandCallback) {
            if (MinimumDataRateHandler.this._minimumReadRate > 0L) {
               long rr = this.dataRatePerSecond(MinimumDataRateHandler.this.getBytesRead());
               if (rr < MinimumDataRateHandler.this._minimumReadRate) {
                  this._errorContent = Chunk.from(new TimeoutException("read rate is too low: " + rr));
                  demandCallback.run();
                  return;
               }
            }

            super.demand(demandCallback);
         }

         @Override
         public Chunk read() {
            return this._errorContent != null ? this._errorContent : super.read();
         }
      }

      protected class MinimumDataRateResponse extends Response.Wrapper {
         public MinimumDataRateResponse(StatisticsHandler.MinimumDataRateHandler.MinimumDataRateRequest request, Response wrapped) {
            super(request, wrapped);
         }

         public StatisticsHandler.MinimumDataRateHandler.MinimumDataRateRequest getRequest() {
            return (StatisticsHandler.MinimumDataRateHandler.MinimumDataRateRequest)super.getRequest();
         }

         @Override
         public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
            if (MinimumDataRateHandler.this._minimumWriteRate > 0L) {
               long bytesWritten = MinimumDataRateHandler.this.getBytesWritten();
               if (bytesWritten > 0L) {
                  long wr = this.getRequest().dataRatePerSecond(bytesWritten);
                  if (wr < MinimumDataRateHandler.this._minimumWriteRate) {
                     TimeoutException cause = new TimeoutException("write rate is too low: " + wr);
                     this.getRequest()._errorContent = Chunk.from(cause);
                     callback.failed(cause);
                     return;
                  }
               }
            }

            super.write(last, byteBuffer, callback);
         }
      }
   }
}
