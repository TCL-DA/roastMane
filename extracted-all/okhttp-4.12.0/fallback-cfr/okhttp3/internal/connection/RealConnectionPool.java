/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okhttp3.internal.connection;

import java.lang.ref.Reference;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.RealCall;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RealConnectionPool;
import okhttp3.internal.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0005*\u0001\u000e\u0018\u0000 (2\u00020\u0001:\u0001(B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ.\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u000e\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001b2\u0006\u0010\u001d\u001a\u00020\u0015J\u000e\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020\u0007J\u000e\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\u0012J\u0006\u0010\"\u001a\u00020\u0005J\u0006\u0010#\u001a\u00020$J\u0006\u0010%\u001a\u00020\u0005J\u0018\u0010&\u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0007H\u0002J\u000e\u0010'\u001a\u00020$2\u0006\u0010!\u001a\u00020\u0012R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000fR\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2={"Lokhttp3/internal/connection/RealConnectionPool;", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "maxIdleConnections", "", "keepAliveDuration", "", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "(Lokhttp3/internal/concurrent/TaskRunner;IJLjava/util/concurrent/TimeUnit;)V", "cleanupQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "cleanupTask", "okhttp3/internal/connection/RealConnectionPool$cleanupTask$1", "Lokhttp3/internal/connection/RealConnectionPool$cleanupTask$1;", "connections", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "Lokhttp3/internal/connection/RealConnection;", "keepAliveDurationNs", "callAcquirePooledConnection", "", "address", "Lokhttp3/Address;", "call", "Lokhttp3/internal/connection/RealCall;", "routes", "", "Lokhttp3/Route;", "requireMultiplexed", "cleanup", "now", "connectionBecameIdle", "connection", "connectionCount", "evictAll", "", "idleConnectionCount", "pruneAndGetAllocationCount", "put", "Companion", "okhttp"})
@SourceDebugExtension(value={"SMAP\nRealConnectionPool.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Util.kt\nokhttp3/internal/Util\n*L\n1#1,250:1\n1#2:251\n1774#3,4:252\n608#4,4:256\n608#4,4:260\n608#4,4:264\n*S KotlinDebug\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n*L\n60#1:252,4\n95#1:256,4\n106#1:260,4\n215#1:264,4\n*E\n"})
public final class RealConnectionPool {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int maxIdleConnections;
    private final long keepAliveDurationNs;
    @NotNull
    private final TaskQueue cleanupQueue;
    @NotNull
    private final cleanupTask.1 cleanupTask;
    @NotNull
    private final ConcurrentLinkedQueue<RealConnection> connections;

    public RealConnectionPool(@NotNull TaskRunner taskRunner, int maxIdleConnections, long keepAliveDuration, @NotNull TimeUnit timeUnit) {
        Intrinsics.checkNotNullParameter((Object)taskRunner, (String)"taskRunner");
        Intrinsics.checkNotNullParameter((Object)((Object)timeUnit), (String)"timeUnit");
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        this.cleanupQueue = taskRunner.newQueue();
        String string = Util.okHttpName + " ConnectionPool";
        this.cleanupTask = new Task(this, string){
            final /* synthetic */ RealConnectionPool this$0;
            {
                this.this$0 = $receiver;
                super($super_call_param$1, false, 2, null);
            }

            public long runOnce() {
                return this.this$0.cleanup(System.nanoTime());
            }
        };
        this.connections = new ConcurrentLinkedQueue();
        if (!(keepAliveDuration > 0L)) {
            boolean bl = false;
            String string2 = "keepAliveDuration <= 0: " + keepAliveDuration;
            throw new IllegalArgumentException(string2.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public final int idleConnectionCount() {
        int n;
        Iterable $this$count$iv = this.connections;
        boolean $i$f$count = false;
        if ($this$count$iv instanceof Collection && ((Collection)$this$count$iv).isEmpty()) {
            n = 0;
        } else {
            void var3_3;
            int count$iv = 0;
            for (Object element$iv : $this$count$iv) {
                boolean bl;
                RealConnection it = (RealConnection)element$iv;
                boolean bl2 = false;
                Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
                RealConnection realConnection = it;
                synchronized (realConnection) {
                    boolean bl3 = false;
                    bl = it.getCalls().isEmpty();
                }
                if (!bl || ++count$iv >= 0) continue;
                CollectionsKt.throwCountOverflow();
            }
            n = var3_3;
        }
        return n;
    }

    public final int connectionCount() {
        return this.connections.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean callAcquirePooledConnection(@NotNull Address address, @NotNull RealCall call, @Nullable List<Route> routes, boolean requireMultiplexed) {
        Intrinsics.checkNotNullParameter((Object)address, (String)"address");
        Intrinsics.checkNotNullParameter((Object)call, (String)"call");
        for (RealConnection connection : this.connections) {
            Intrinsics.checkNotNullExpressionValue((Object)connection, (String)"connection");
            RealConnection realConnection = connection;
            synchronized (realConnection) {
                block5: {
                    boolean bl = false;
                    if (requireMultiplexed && !connection.isMultiplexed$okhttp() || !connection.isEligible$okhttp(address, routes)) break block5;
                    call.acquireConnectionNoEvents(connection);
                    boolean bl2 = true;
                    return bl2;
                }
                Unit unit = Unit.INSTANCE;
            }
        }
        return false;
    }

    public final void put(@NotNull RealConnection connection) {
        Intrinsics.checkNotNullParameter((Object)connection, (String)"connection");
        RealConnection $this$assertThreadHoldsLock$iv = connection;
        boolean $i$f$assertThreadHoldsLock = false;
        if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
        }
        this.connections.add(connection);
        TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
    }

    public final boolean connectionBecameIdle(@NotNull RealConnection connection) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)connection, (String)"connection");
        RealConnection $this$assertThreadHoldsLock$iv = connection;
        boolean $i$f$assertThreadHoldsLock = false;
        if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
        }
        if (connection.getNoNewExchanges() || this.maxIdleConnections == 0) {
            connection.setNoNewExchanges(true);
            this.connections.remove(connection);
            if (this.connections.isEmpty()) {
                this.cleanupQueue.cancelAll();
            }
            bl = true;
        } else {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            bl = false;
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void evictAll() {
        Iterator<RealConnection> iterator = this.connections.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"connections.iterator()");
        Iterator<RealConnection> i = iterator;
        while (i.hasNext()) {
            Socket socketToClose;
            Socket socket;
            RealConnection connection = i.next();
            Intrinsics.checkNotNullExpressionValue((Object)connection, (String)"connection");
            RealConnection realConnection = connection;
            synchronized (realConnection) {
                Socket socket2;
                boolean bl = false;
                if (connection.getCalls().isEmpty()) {
                    i.remove();
                    connection.setNoNewExchanges(true);
                    socket2 = connection.socket();
                } else {
                    socket2 = null;
                }
                socket = socket2;
            }
            Socket socket3 = socketToClose = socket;
            if (socket3 == null) continue;
            Util.closeQuietly(socket3);
        }
        if (this.connections.isEmpty()) {
            this.cleanupQueue.cancelAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final long cleanup(long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = 0L;
        longestIdleDurationNs = Long.MIN_VALUE;
        for (RealConnection connection : this.connections) {
            Intrinsics.checkNotNullExpressionValue((Object)connection, (String)"connection");
            RealConnection realConnection = connection;
            synchronized (realConnection) {
                Integer n;
                boolean bl = false;
                if (this.pruneAndGetAllocationCount(connection, now) > 0) {
                    int n2 = inUseConnectionCount;
                    inUseConnectionCount = n2 + 1;
                    n = n2;
                } else {
                    ++idleConnectionCount;
                    long idleDurationNs = now - connection.getIdleAtNs$okhttp();
                    if (idleDurationNs > longestIdleDurationNs) {
                        longestIdleDurationNs = idleDurationNs;
                        longestIdleConnection = connection;
                    }
                    n = Unit.INSTANCE;
                }
                Integer n3 = n;
            }
        }
        if (longestIdleDurationNs >= this.keepAliveDurationNs || idleConnectionCount > this.maxIdleConnections) {
            RealConnection connection;
            RealConnection realConnection = longestIdleConnection;
            Intrinsics.checkNotNull(realConnection);
            RealConnection realConnection2 = connection = realConnection;
            synchronized (realConnection2) {
                block17: {
                    block16: {
                        boolean bl = false;
                        if (!(!((Collection)connection.getCalls()).isEmpty())) break block16;
                        long l = 0L;
                        return l;
                    }
                    if (connection.getIdleAtNs$okhttp() + longestIdleDurationNs == now) break block17;
                    long l = 0L;
                    return l;
                }
                connection.setNoNewExchanges(true);
                boolean bl = this.connections.remove(longestIdleConnection);
            }
            Util.closeQuietly(connection.socket());
            if (this.connections.isEmpty()) {
                this.cleanupQueue.cancelAll();
            }
            return 0L;
        }
        if (idleConnectionCount > 0) {
            return this.keepAliveDurationNs - longestIdleDurationNs;
        }
        if (inUseConnectionCount > 0) {
            return this.keepAliveDurationNs;
        }
        return -1L;
    }

    private final int pruneAndGetAllocationCount(RealConnection connection, long now) {
        RealConnection $this$assertThreadHoldsLock$iv = connection;
        boolean $i$f$assertThreadHoldsLock = false;
        if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
        }
        List<Reference<RealCall>> references = connection.getCalls();
        int i = 0;
        while (i < references.size()) {
            Reference<RealCall> reference = references.get(i);
            if (reference.get() != null) {
                ++i;
                continue;
            }
            Intrinsics.checkNotNull(reference, (String)"null cannot be cast to non-null type okhttp3.internal.connection.RealCall.CallReference");
            RealCall.CallReference callReference = (RealCall.CallReference)reference;
            String message = "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?";
            Platform.Companion.get().logCloseableLeak(message, callReference.getCallStackTrace());
            references.remove(i);
            connection.setNoNewExchanges(true);
            if (!references.isEmpty()) continue;
            connection.setIdleAtNs$okhttp(now - this.keepAliveDurationNs);
            return 0;
        }
        return references.size();
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2={"Lokhttp3/internal/connection/RealConnectionPool$Companion;", "", "()V", "get", "Lokhttp3/internal/connection/RealConnectionPool;", "connectionPool", "Lokhttp3/ConnectionPool;", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final RealConnectionPool get(@NotNull ConnectionPool connectionPool) {
            Intrinsics.checkNotNullParameter((Object)connectionPool, (String)"connectionPool");
            return connectionPool.getDelegate$okhttp();
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

