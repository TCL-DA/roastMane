package okhttp3

import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Collections
import java.util.Deque
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okhttp3.internal.connection.RealCall
import okhttp3.internal.connection.RealCall.AsyncCall

@SourceDebugExtension(["SMAP\nDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,241:1\n1#2:242\n615#3,4:243\n1549#4:247\n1620#4,3:248\n1549#4:251\n1620#4,3:252\n*S KotlinDebug\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n*L\n162#1:243,4\n222#1:247\n222#1:248,3\n227#1:251\n227#1:252,3\n*E\n"])
class Dispatcher {
   public final val executorService: ExecutorService
      public final get() {
         if (this.executorServiceOrNull == null) {
            this.executorServiceOrNull = new ThreadPoolExecutor(
               0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), Util.threadFactory(Util.okHttpName + " Dispatcher", false)
            );
         }

         var var10000: ExecutorService = this.executorServiceOrNull;
         return var10000;
      }

   private final var executorServiceOrNull: ExecutorService?
   public final var idleCallback: Runnable?
      public final get() {
         return this.idleCallback;
      }

      public final set(<set-?>) {
         this.idleCallback = var1;
      }

   public final var maxRequests: Int = 64
      public final get() {
         return this.maxRequests;
      }

      public final set(maxRequests) {
         if (maxRequests < 1) {
            throw new IllegalArgumentException(("max < 1: " + maxRequests).toString());
         } else {
            synchronized (this) {
               this.maxRequests = maxRequests;
            }

            this.promoteAndExecute();
         }
      }

   public final var maxRequestsPerHost: Int = 5
      public final get() {
         return this.maxRequestsPerHost;
      }

      public final set(maxRequestsPerHost) {
         if (maxRequestsPerHost < 1) {
            throw new IllegalArgumentException(("max < 1: " + maxRequestsPerHost).toString());
         } else {
            synchronized (this) {
               this.maxRequestsPerHost = maxRequestsPerHost;
            }

            this.promoteAndExecute();
         }
      }

   private final val readyAsyncCalls: ArrayDeque<AsyncCall> = new ArrayDeque()
   private final val runningAsyncCalls: ArrayDeque<AsyncCall> = new ArrayDeque()
   private final val runningSyncCalls: ArrayDeque<RealCall> = new ArrayDeque()


   public constructor(executorService: ExecutorService) : this() {
      this.executorServiceOrNull = executorService;
   }

   internal fun enqueue(call: AsyncCall) {
      synchronized (this) {
         this.readyAsyncCalls.add(call);
         if (!call.getCall().getForWebSocket()) {
            var existingCall: RealCall.AsyncCall = this.findExistingCallWithHost(call.getHost());
            if (existingCall != null) {
               call.reuseCallsPerHostFrom(existingCall);
            }
         }
      }

      this.promoteAndExecute();
   }

   private fun findExistingCallWithHost(host: String): AsyncCall? {
      for (RealCall.AsyncCall existingCall : this.runningAsyncCalls) {
         if (existingCall.getHost() == host) {
            return existingCall;
         }
      }

      for (RealCall.AsyncCall existingCallx : this.readyAsyncCalls) {
         if (existingCallx.getHost() == host) {
            return existingCallx;
         }
      }

      return null;
   }

   @Synchronized
   public fun cancelAll() {
      for (RealCall.AsyncCall call : this.readyAsyncCalls) {
         call.getCall().cancel();
      }

      for (RealCall.AsyncCall call : this.runningAsyncCalls) {
         var5.getCall().cancel();
      }

      for (RealCall call : this.runningSyncCalls) {
         var6.cancel();
      }
   }

   private fun promoteAndExecute(): Boolean {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var executableCalls: java.util.List = new ArrayList();
         var var9: Boolean;
         synchronized (this) {
            var var10000: java.util.Iterator = this.readyAsyncCalls.iterator();
            var asyncCall: java.util.Iterator = var10000;

            while (i.hasNext()) {
               var asyncCallx: RealCall.AsyncCall = asyncCall.next() as RealCall.AsyncCall;
               if (this.runningAsyncCalls.size() >= this.maxRequests) {
                  break;
               }

               if (asyncCallx.getCallsPerHost().get() < this.maxRequestsPerHost) {
                  asyncCall.remove();
                  asyncCallx.getCallsPerHost().incrementAndGet();
                  executableCalls.add(asyncCallx);
                  this.runningAsyncCalls.add(asyncCallx);
               }
            }

            var9 = this.runningCallsCount() > 0;
         }

         var i: Int = 0;

         for (int var11 = executableCalls.size(); i < var11; i++) {
            (executableCalls.get(i) as RealCall.AsyncCall).executeOn(this.executorService());
         }

         return var9;
      }
   }

   @Synchronized
   internal fun executed(call: RealCall) {
      this.runningSyncCalls.add(call);
   }

   internal fun finished(call: AsyncCall) {
      call.getCallsPerHost().decrementAndGet();
      this.finished(this.runningAsyncCalls, call);
   }

   internal fun finished(call: RealCall) {
      this.finished(this.runningSyncCalls, call);
   }

   private fun <T> finished(calls: Deque<T>, call: T) {
      var var7: Runnable;
      synchronized (this) {
         if (!calls.remove(call)) {
            throw new AssertionError("Call wasn't in-flight!");
         }

         var7 = this.idleCallback;
      }

      if (!this.promoteAndExecute() && var7 != null) {
         var7.run();
      }
   }

   @Synchronized
   public fun queuedCalls(): List<Call> {
      var `$this$map$iv`: java.lang.Iterable = this.readyAsyncCalls;
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.readyAsyncCalls, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as RealCall.AsyncCall).getCall());
      }

      var var10000: java.util.List = Collections.unmodifiableList(`destination$iv$iv` as java.util.List);
      return var10000;
   }

   @Synchronized
   public fun runningCalls(): List<Call> {
      var var10000: java.util.Collection = this.runningSyncCalls;
      var `$this$map$iv`: java.lang.Iterable = this.runningAsyncCalls;
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.runningAsyncCalls, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as RealCall.AsyncCall).getCall());
      }

      var var12: java.util.List = Collections.unmodifiableList(CollectionsKt.plus(var10000, `destination$iv$iv` as java.util.List));
      return var12;
   }

   @Synchronized
   public fun queuedCallsCount(): Int {
      return this.readyAsyncCalls.size();
   }

   @Synchronized
   public fun runningCallsCount(): Int {
      return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "executorService", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_executorService")
   public fun executorService(): ExecutorService {
      return this.executorService();
   }
}
