/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.PublishedApi
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.InlineMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.-SegmentedByteString;
import okio.Buffer;
import okio.Segment;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u0000 \u001b2\u00020\u0001:\u0002\u001b\u001cB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\tH\u0001J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u0004J\u0012\u0010\u000e\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\tH\u0014J\u0010\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0007H\u0002J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u0014J\b\u0010\u0015\u001a\u00020\fH\u0014J%\u0010\u0016\u001a\u0002H\u0017\"\u0004\b\u0000\u0010\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00170\u0019H\u0086\b\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001aR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0000X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\u001d"}, d2={"Lokio/AsyncTimeout;", "Lokio/Timeout;", "()V", "inQueue", "", "next", "timeoutAt", "", "access$newTimeoutException", "Ljava/io/IOException;", "cause", "enter", "", "exit", "newTimeoutException", "remainingNanos", "now", "sink", "Lokio/Sink;", "source", "Lokio/Source;", "timedOut", "withTimeout", "T", "block", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "Companion", "Watchdog", "okio"})
public class AsyncTimeout
extends Timeout {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private boolean inQueue;
    @Nullable
    private AsyncTimeout next;
    private long timeoutAt;
    @NotNull
    private static final ReentrantLock lock = new ReentrantLock();
    @NotNull
    private static final Condition condition;
    private static final int TIMEOUT_WRITE_SIZE = 65536;
    private static final long IDLE_TIMEOUT_MILLIS;
    private static final long IDLE_TIMEOUT_NANOS;
    @Nullable
    private static AsyncTimeout head;

    public final void enter() {
        long timeoutNanos = this.timeoutNanos();
        boolean hasDeadline = this.hasDeadline();
        if (timeoutNanos == 0L && !hasDeadline) {
            return;
        }
        AsyncTimeout.Companion.scheduleTimeout(this, timeoutNanos, hasDeadline);
    }

    public final boolean exit() {
        return AsyncTimeout.Companion.cancelScheduledTimeout(this);
    }

    private final long remainingNanos(long now) {
        return this.timeoutAt - now;
    }

    protected void timedOut() {
    }

    @NotNull
    public final Sink sink(@NotNull Sink sink2) {
        Intrinsics.checkNotNullParameter((Object)sink2, (String)"sink");
        return new Sink(this, sink2){
            final /* synthetic */ AsyncTimeout this$0;
            final /* synthetic */ Sink $sink;
            {
                this.this$0 = $receiver;
                this.$sink = $sink;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             */
            public void write(@NotNull Buffer source2, long byteCount) {
                long toWrite;
                Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
                -SegmentedByteString.checkOffsetAndCount(source2.size(), 0L, byteCount);
                for (long remaining = byteCount; remaining > 0L; remaining -= toWrite) {
                    void this_$iv;
                    toWrite = 0L;
                    Intrinsics.checkNotNull((Object)source2.head);
                    while (toWrite < 65536L) {
                        Segment s;
                        int segmentSize = s.limit - s.pos;
                        if ((toWrite += (long)segmentSize) >= remaining) {
                            toWrite = remaining;
                            break;
                        }
                        Intrinsics.checkNotNull((Object)s.next);
                    }
                    AsyncTimeout segmentSize = this.this$0;
                    Sink sink2 = this.$sink;
                    boolean $i$f$withTimeout = false;
                    boolean throwOnTimeout$iv = false;
                    this_$iv.enter();
                    try {
                        boolean bl = false;
                        sink2.write(source2, toWrite);
                        Unit result$iv = Unit.INSTANCE;
                        throwOnTimeout$iv = true;
                        Unit unit = result$iv;
                        boolean timedOut$iv = this_$iv.exit();
                        if (!timedOut$iv) continue;
                    }
                    catch (IOException e$iv) {
                        try {
                            throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
                        }
                        catch (Throwable throwable) {
                            boolean timedOut$iv = this_$iv.exit();
                            if (timedOut$iv && throwOnTimeout$iv) {
                                throw this_$iv.access$newTimeoutException(null);
                            }
                            throw throwable;
                        }
                    }
                    throw this_$iv.access$newTimeoutException(null);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public void flush() {
                void this_$iv;
                AsyncTimeout asyncTimeout = this.this$0;
                Sink sink2 = this.$sink;
                boolean $i$f$withTimeout = false;
                boolean throwOnTimeout$iv = false;
                this_$iv.enter();
                try {
                    boolean bl = false;
                    sink2.flush();
                    Unit result$iv = Unit.INSTANCE;
                    throwOnTimeout$iv = true;
                    Unit unit = result$iv;
                    boolean timedOut$iv = this_$iv.exit();
                    if (!timedOut$iv) return;
                }
                catch (IOException e$iv) {
                    try {
                        throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
                    }
                    catch (Throwable throwable) {
                        boolean timedOut$iv = this_$iv.exit();
                        if (!timedOut$iv || !throwOnTimeout$iv) throw throwable;
                        throw this_$iv.access$newTimeoutException(null);
                    }
                }
                throw this_$iv.access$newTimeoutException(null);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public void close() {
                void this_$iv;
                AsyncTimeout asyncTimeout = this.this$0;
                Sink sink2 = this.$sink;
                boolean $i$f$withTimeout = false;
                boolean throwOnTimeout$iv = false;
                this_$iv.enter();
                try {
                    boolean bl = false;
                    sink2.close();
                    Unit result$iv = Unit.INSTANCE;
                    throwOnTimeout$iv = true;
                    Unit unit = result$iv;
                    boolean timedOut$iv = this_$iv.exit();
                    if (!timedOut$iv) return;
                }
                catch (IOException e$iv) {
                    try {
                        throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
                    }
                    catch (Throwable throwable) {
                        boolean timedOut$iv = this_$iv.exit();
                        if (!timedOut$iv || !throwOnTimeout$iv) throw throwable;
                        throw this_$iv.access$newTimeoutException(null);
                    }
                }
                throw this_$iv.access$newTimeoutException(null);
            }

            @NotNull
            public AsyncTimeout timeout() {
                return this.this$0;
            }

            @NotNull
            public String toString() {
                return "AsyncTimeout.sink(" + this.$sink + ')';
            }
        };
    }

    @NotNull
    public final Source source(@NotNull Source source2) {
        Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
        return new Source(this, source2){
            final /* synthetic */ AsyncTimeout this$0;
            final /* synthetic */ Source $source;
            {
                this.this$0 = $receiver;
                this.$source = $source;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public long read(@NotNull Buffer sink2, long byteCount) {
                void this_$iv;
                Intrinsics.checkNotNullParameter((Object)sink2, (String)"sink");
                AsyncTimeout asyncTimeout = this.this$0;
                Source source2 = this.$source;
                boolean $i$f$withTimeout = false;
                boolean throwOnTimeout$iv = false;
                this_$iv.enter();
                try {
                    boolean bl = false;
                    long result$iv = source2.read(sink2, byteCount);
                    throwOnTimeout$iv = true;
                    long l = result$iv;
                    boolean timedOut$iv = this_$iv.exit();
                    if (!timedOut$iv) return l;
                }
                catch (IOException e$iv) {
                    try {
                        throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
                    }
                    catch (Throwable throwable) {
                        boolean timedOut$iv = this_$iv.exit();
                        if (!timedOut$iv || !throwOnTimeout$iv) throw throwable;
                        throw this_$iv.access$newTimeoutException(null);
                    }
                }
                throw this_$iv.access$newTimeoutException(null);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public void close() {
                void this_$iv;
                AsyncTimeout asyncTimeout = this.this$0;
                Source source2 = this.$source;
                boolean $i$f$withTimeout = false;
                boolean throwOnTimeout$iv = false;
                this_$iv.enter();
                try {
                    boolean bl = false;
                    source2.close();
                    Unit result$iv = Unit.INSTANCE;
                    throwOnTimeout$iv = true;
                    Unit unit = result$iv;
                    boolean timedOut$iv = this_$iv.exit();
                    if (!timedOut$iv) return;
                }
                catch (IOException e$iv) {
                    try {
                        throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
                    }
                    catch (Throwable throwable) {
                        boolean timedOut$iv = this_$iv.exit();
                        if (!timedOut$iv || !throwOnTimeout$iv) throw throwable;
                        throw this_$iv.access$newTimeoutException(null);
                    }
                }
                throw this_$iv.access$newTimeoutException(null);
            }

            @NotNull
            public AsyncTimeout timeout() {
                return this.this$0;
            }

            @NotNull
            public String toString() {
                return "AsyncTimeout.source(" + this.$source + ')';
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final <T> T withTimeout(@NotNull Function0<? extends T> block) {
        Object object;
        Intrinsics.checkNotNullParameter(block, (String)"block");
        boolean $i$f$withTimeout = false;
        boolean throwOnTimeout = false;
        this.enter();
        try {
            Object result = block.invoke();
            throwOnTimeout = true;
            object = result;
        }
        catch (IOException e) {
            try {
                throw !this.exit() ? (Throwable)e : (Throwable)this.access$newTimeoutException(e);
            }
            catch (Throwable throwable) {
                InlineMarker.finallyStart((int)1);
                boolean timedOut = this.exit();
                if (timedOut && throwOnTimeout) {
                    throw this.access$newTimeoutException(null);
                }
                InlineMarker.finallyEnd((int)1);
                throw throwable;
            }
        }
        InlineMarker.finallyStart((int)1);
        boolean timedOut = this.exit();
        if (timedOut) {
            throw this.access$newTimeoutException(null);
        }
        InlineMarker.finallyEnd((int)1);
        return (T)object;
    }

    @PublishedApi
    @NotNull
    public final IOException access$newTimeoutException(@Nullable IOException cause) {
        return this.newTimeoutException(cause);
    }

    @NotNull
    protected IOException newTimeoutException(@Nullable IOException cause) {
        InterruptedIOException e = new InterruptedIOException("timeout");
        if (cause != null) {
            e.initCause(cause);
        }
        return e;
    }

    static {
        Condition condition = lock.newCondition();
        Intrinsics.checkNotNullExpressionValue((Object)condition, (String)"newCondition(...)");
        AsyncTimeout.condition = condition;
        IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
        IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0012\u001a\u0004\u0018\u00010\rH\u0000\u00a2\u0006\u0002\b\u0013J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\rH\u0002J \u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u0015H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001b"}, d2={"Lokio/AsyncTimeout$Companion;", "", "()V", "IDLE_TIMEOUT_MILLIS", "", "IDLE_TIMEOUT_NANOS", "TIMEOUT_WRITE_SIZE", "", "condition", "Ljava/util/concurrent/locks/Condition;", "getCondition", "()Ljava/util/concurrent/locks/Condition;", "head", "Lokio/AsyncTimeout;", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "awaitTimeout", "awaitTimeout$okio", "cancelScheduledTimeout", "", "node", "scheduleTimeout", "", "timeoutNanos", "hasDeadline", "okio"})
    @SourceDebugExtension(value={"SMAP\nAsyncTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AsyncTimeout.kt\nokio/AsyncTimeout$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,331:1\n1#2:332\n*E\n"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final ReentrantLock getLock() {
            return lock;
        }

        @NotNull
        public final Condition getCondition() {
            return condition;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final void scheduleTimeout(AsyncTimeout node, long timeoutNanos, boolean hasDeadline) {
            Lock lock = Companion.getLock();
            lock.lock();
            try {
                boolean bl = false;
                if (!(!node.inQueue)) {
                    boolean bl2 = false;
                    String string = "Unbalanced enter/exit";
                    throw new IllegalStateException(string.toString());
                }
                node.inQueue = true;
                if (head == null) {
                    head = new AsyncTimeout();
                    new Watchdog().start();
                }
                long now = System.nanoTime();
                if (timeoutNanos != 0L && hasDeadline) {
                    node.timeoutAt = now + Math.min(timeoutNanos, node.deadlineNanoTime() - now);
                } else if (timeoutNanos != 0L) {
                    node.timeoutAt = now + timeoutNanos;
                } else if (hasDeadline) {
                    node.timeoutAt = node.deadlineNanoTime();
                } else {
                    throw new AssertionError();
                }
                long remainingNanos = node.remainingNanos(now);
                AsyncTimeout asyncTimeout = head;
                Intrinsics.checkNotNull((Object)asyncTimeout);
                AsyncTimeout prev = asyncTimeout;
                while (true) {
                    block17: {
                        block16: {
                            if (prev.next == null) break block16;
                            AsyncTimeout asyncTimeout2 = prev.next;
                            Intrinsics.checkNotNull((Object)asyncTimeout2);
                            if (remainingNanos >= asyncTimeout2.remainingNanos(now)) break block17;
                        }
                        node.next = prev.next;
                        prev.next = node;
                        if (prev != head) break;
                        Companion.getCondition().signal();
                        break;
                    }
                    Intrinsics.checkNotNull((Object)prev.next);
                }
                Unit unit = Unit.INSTANCE;
            }
            finally {
                lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final boolean cancelScheduledTimeout(AsyncTimeout node) {
            Lock lock = Companion.getLock();
            lock.lock();
            try {
                boolean bl = false;
                if (!node.inQueue) {
                    boolean bl2 = false;
                    return bl2;
                }
                node.inQueue = false;
                AsyncTimeout prev = head;
                while (prev != null) {
                    if (prev.next == node) {
                        prev.next = node.next;
                        node.next = null;
                        boolean bl3 = false;
                        return bl3;
                    }
                    prev = prev.next;
                }
                boolean bl4 = true;
                return bl4;
            }
            finally {
                lock.unlock();
            }
        }

        @Nullable
        public final AsyncTimeout awaitTimeout$okio() throws InterruptedException {
            AsyncTimeout asyncTimeout = head;
            Intrinsics.checkNotNull((Object)asyncTimeout);
            AsyncTimeout node = asyncTimeout.next;
            if (node == null) {
                long startNanos = System.nanoTime();
                this.getCondition().await(IDLE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                AsyncTimeout asyncTimeout2 = head;
                Intrinsics.checkNotNull((Object)asyncTimeout2);
                return asyncTimeout2.next == null && System.nanoTime() - startNanos >= IDLE_TIMEOUT_NANOS ? head : null;
            }
            long waitNanos = node.remainingNanos(System.nanoTime());
            if (waitNanos > 0L) {
                this.getCondition().await(waitNanos, TimeUnit.NANOSECONDS);
                return null;
            }
            AsyncTimeout asyncTimeout3 = head;
            Intrinsics.checkNotNull((Object)asyncTimeout3);
            asyncTimeout3.next = node.next;
            node.next = null;
            return node;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0000\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016\u00a8\u0006\u0005"}, d2={"Lokio/AsyncTimeout$Watchdog;", "Ljava/lang/Thread;", "()V", "run", "", "okio"})
    private static final class Watchdog
    extends Thread {
        public Watchdog() {
            super("Okio Watchdog");
            this.setDaemon(true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            while (true) {
                try {
                    AsyncTimeout timedOut = null;
                    Lock lock = Companion.getLock();
                    lock.lock();
                    try {
                        boolean bl = false;
                        timedOut = Companion.awaitTimeout$okio();
                        if (timedOut == head) {
                            head = null;
                            return;
                        }
                        Unit unit = Unit.INSTANCE;
                    }
                    finally {
                        lock.unlock();
                    }
                    AsyncTimeout asyncTimeout = timedOut;
                    if (asyncTimeout != null) {
                        asyncTimeout.timedOut();
                    }
                }
                catch (InterruptedException interruptedException) {
                }
            }
        }
    }
}

