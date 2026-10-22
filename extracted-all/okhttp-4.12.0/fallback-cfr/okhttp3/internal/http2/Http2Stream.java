/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  okio.AsyncTimeout
 *  okio.Buffer
 *  okio.BufferedSource
 *  okio.Sink
 *  okio.Source
 *  okio.Timeout
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Headers;
import okhttp3.internal.Util;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Header;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.StreamResetException;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 _2\u00020\u0001:\u0004_`abB1\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020#J\r\u0010C\u001a\u00020AH\u0000\u00a2\u0006\u0002\bDJ\r\u0010E\u001a\u00020AH\u0000\u00a2\u0006\u0002\bFJ\u0018\u0010G\u001a\u00020A2\u0006\u0010H\u001a\u00020\u000f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\u001a\u0010I\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0002J\u000e\u0010J\u001a\u00020A2\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010K\u001a\u00020A2\u0006\u0010L\u001a\u00020\nJ\u0006\u0010M\u001a\u00020NJ\u0006\u0010O\u001a\u00020PJ\u0006\u0010,\u001a\u00020QJ\u0016\u0010R\u001a\u00020A2\u0006\u00104\u001a\u00020S2\u0006\u0010T\u001a\u00020\u0003J\u0016\u0010U\u001a\u00020A2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\b\u001a\u00020\u0007J\u000e\u0010V\u001a\u00020A2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010W\u001a\u00020\nJ\u0006\u0010L\u001a\u00020\nJ\r\u0010X\u001a\u00020AH\u0000\u00a2\u0006\u0002\bYJ$\u0010Z\u001a\u00020A2\f\u0010[\u001a\b\u0012\u0004\u0012\u00020]0\\2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010^\u001a\u00020\u0007J\u0006\u0010>\u001a\u00020QR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001e\u0010\u000e\u001a\u0004\u0018\u00010\u000f8@X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u001f\u001a\u00020\u00078F\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010 R\u0011\u0010!\u001a\u00020\u00078F\u00a2\u0006\u0006\u001a\u0004\b!\u0010 R$\u0010$\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R$\u0010)\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010&\"\u0004\b+\u0010(R\u0018\u0010,\u001a\u00060-R\u00020\u0000X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0018\u00100\u001a\u000601R\u00020\u0000X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0018\u00104\u001a\u000605R\u00020\u0000X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R$\u00108\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b9\u0010&\"\u0004\b:\u0010(R$\u0010;\u001a\u00020#2\u0006\u0010\"\u001a\u00020#@@X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b<\u0010&\"\u0004\b=\u0010(R\u0018\u0010>\u001a\u00060-R\u00020\u0000X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010/\u00a8\u0006c"}, d2={"Lokhttp3/internal/http2/Http2Stream;", "", "id", "", "connection", "Lokhttp3/internal/http2/Http2Connection;", "outFinished", "", "inFinished", "headers", "Lokhttp3/Headers;", "(ILokhttp3/internal/http2/Http2Connection;ZZLokhttp3/Headers;)V", "getConnection", "()Lokhttp3/internal/http2/Http2Connection;", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "getErrorCode$okhttp", "()Lokhttp3/internal/http2/ErrorCode;", "setErrorCode$okhttp", "(Lokhttp3/internal/http2/ErrorCode;)V", "errorException", "Ljava/io/IOException;", "getErrorException$okhttp", "()Ljava/io/IOException;", "setErrorException$okhttp", "(Ljava/io/IOException;)V", "hasResponseHeaders", "headersQueue", "Ljava/util/ArrayDeque;", "getId", "()I", "isLocallyInitiated", "()Z", "isOpen", "<set-?>", "", "readBytesAcknowledged", "getReadBytesAcknowledged", "()J", "setReadBytesAcknowledged$okhttp", "(J)V", "readBytesTotal", "getReadBytesTotal", "setReadBytesTotal$okhttp", "readTimeout", "Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "getReadTimeout$okhttp", "()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "sink", "Lokhttp3/internal/http2/Http2Stream$FramingSink;", "getSink$okhttp", "()Lokhttp3/internal/http2/Http2Stream$FramingSink;", "source", "Lokhttp3/internal/http2/Http2Stream$FramingSource;", "getSource$okhttp", "()Lokhttp3/internal/http2/Http2Stream$FramingSource;", "writeBytesMaximum", "getWriteBytesMaximum", "setWriteBytesMaximum$okhttp", "writeBytesTotal", "getWriteBytesTotal", "setWriteBytesTotal$okhttp", "writeTimeout", "getWriteTimeout$okhttp", "addBytesToWriteWindow", "", "delta", "cancelStreamIfNecessary", "cancelStreamIfNecessary$okhttp", "checkOutNotClosed", "checkOutNotClosed$okhttp", "close", "rstStatusCode", "closeInternal", "closeLater", "enqueueTrailers", "trailers", "getSink", "Lokio/Sink;", "getSource", "Lokio/Source;", "Lokio/Timeout;", "receiveData", "Lokio/BufferedSource;", "length", "receiveHeaders", "receiveRstStream", "takeHeaders", "waitForIo", "waitForIo$okhttp", "writeHeaders", "responseHeaders", "", "Lokhttp3/internal/http2/Header;", "flushHeaders", "Companion", "FramingSink", "FramingSource", "StreamTimeout", "okhttp"})
@SourceDebugExtension(value={"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n1#2:689\n615#3,4:690\n615#3,4:694\n563#3:698\n615#3,4:699\n615#3,4:703\n563#3:707\n563#3:708\n615#3,4:709\n563#3:713\n557#3:714\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n*L\n176#1:690,4\n255#1:694,4\n263#1:698\n274#1:699,4\n281#1:703,4\n295#1:707\n305#1:708\n491#1:709,4\n637#1:713\n657#1:714\n*E\n"})
public final class Http2Stream {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int id;
    @NotNull
    private final Http2Connection connection;
    private long readBytesTotal;
    private long readBytesAcknowledged;
    private long writeBytesTotal;
    private long writeBytesMaximum;
    @NotNull
    private final ArrayDeque<Headers> headersQueue;
    private boolean hasResponseHeaders;
    @NotNull
    private final FramingSource source;
    @NotNull
    private final FramingSink sink;
    @NotNull
    private final StreamTimeout readTimeout;
    @NotNull
    private final StreamTimeout writeTimeout;
    @Nullable
    private ErrorCode errorCode;
    @Nullable
    private IOException errorException;
    public static final long EMIT_BUFFER_SIZE = 16384L;

    public Http2Stream(int id, @NotNull Http2Connection connection, boolean outFinished, boolean inFinished, @Nullable Headers headers) {
        Intrinsics.checkNotNullParameter((Object)connection, (String)"connection");
        this.id = id;
        this.connection = connection;
        this.writeBytesMaximum = this.connection.getPeerSettings().getInitialWindowSize();
        this.headersQueue = new ArrayDeque();
        this.source = new FramingSource(this.connection.getOkHttpSettings().getInitialWindowSize(), inFinished);
        this.sink = new FramingSink(this, outFinished);
        this.readTimeout = new StreamTimeout();
        this.writeTimeout = new StreamTimeout();
        if (headers != null) {
            if (!(!this.isLocallyInitiated())) {
                boolean $i$a$-check-Http2Stream$32 = false;
                String $i$a$-check-Http2Stream$32 = "locally-initiated streams shouldn't have headers yet";
                throw new IllegalStateException($i$a$-check-Http2Stream$32.toString());
            }
            ((Collection)this.headersQueue).add(headers);
        } else if (!this.isLocallyInitiated()) {
            boolean bl = false;
            String string = "remotely-initiated streams should have headers";
            throw new IllegalStateException(string.toString());
        }
    }

    public final int getId() {
        return this.id;
    }

    @NotNull
    public final Http2Connection getConnection() {
        return this.connection;
    }

    public final long getReadBytesTotal() {
        return this.readBytesTotal;
    }

    public final void setReadBytesTotal$okhttp(long l) {
        this.readBytesTotal = l;
    }

    public final long getReadBytesAcknowledged() {
        return this.readBytesAcknowledged;
    }

    public final void setReadBytesAcknowledged$okhttp(long l) {
        this.readBytesAcknowledged = l;
    }

    public final long getWriteBytesTotal() {
        return this.writeBytesTotal;
    }

    public final void setWriteBytesTotal$okhttp(long l) {
        this.writeBytesTotal = l;
    }

    public final long getWriteBytesMaximum() {
        return this.writeBytesMaximum;
    }

    public final void setWriteBytesMaximum$okhttp(long l) {
        this.writeBytesMaximum = l;
    }

    @NotNull
    public final FramingSource getSource$okhttp() {
        return this.source;
    }

    @NotNull
    public final FramingSink getSink$okhttp() {
        return this.sink;
    }

    @NotNull
    public final StreamTimeout getReadTimeout$okhttp() {
        return this.readTimeout;
    }

    @NotNull
    public final StreamTimeout getWriteTimeout$okhttp() {
        return this.writeTimeout;
    }

    @Nullable
    public final synchronized ErrorCode getErrorCode$okhttp() {
        return this.errorCode;
    }

    public final void setErrorCode$okhttp(@Nullable ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Nullable
    public final IOException getErrorException$okhttp() {
        return this.errorException;
    }

    public final void setErrorException$okhttp(@Nullable IOException iOException) {
        this.errorException = iOException;
    }

    public final synchronized boolean isOpen() {
        if (this.errorCode != null) {
            return false;
        }
        return !this.source.getFinished$okhttp() && !this.source.getClosed$okhttp() || !this.sink.getFinished() && !this.sink.getClosed() || !this.hasResponseHeaders;
    }

    public final boolean isLocallyInitiated() {
        boolean streamIsClient = (this.id & 1) == 1;
        return this.connection.getClient$okhttp() == streamIsClient;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final synchronized Headers takeHeaders() throws IOException {
        Throwable throwable;
        this.readTimeout.enter();
        try {
            while (this.headersQueue.isEmpty() && this.errorCode == null) {
                this.waitForIo$okhttp();
            }
        }
        finally {
            this.readTimeout.exitAndThrowIfTimedOut();
        }
        if (!((Collection)this.headersQueue).isEmpty()) {
            Headers headers = this.headersQueue.removeFirst();
            Intrinsics.checkNotNullExpressionValue((Object)headers, (String)"headersQueue.removeFirst()");
            return headers;
        }
        IOException iOException = this.errorException;
        if (iOException != null) {
            throwable = iOException;
        } else {
            ErrorCode errorCode = this.errorCode;
            Intrinsics.checkNotNull((Object)((Object)errorCode));
            throwable = new StreamResetException(errorCode);
        }
        throw throwable;
    }

    @NotNull
    public final synchronized Headers trailers() throws IOException {
        if (this.source.getFinished$okhttp() && this.source.getReceiveBuffer().exhausted() && this.source.getReadBuffer().exhausted()) {
            Headers headers = this.source.getTrailers();
            if (headers == null) {
                headers = Util.EMPTY_HEADERS;
            }
            return headers;
        }
        if (this.errorCode != null) {
            Throwable throwable;
            IOException iOException = this.errorException;
            if (iOException != null) {
                throwable = iOException;
            } else {
                ErrorCode errorCode = this.errorCode;
                Intrinsics.checkNotNull((Object)((Object)errorCode));
                throwable = new StreamResetException(errorCode);
            }
            throw throwable;
        }
        throw new IllegalStateException("too early; can't read the trailers yet");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void writeHeaders(@NotNull List<Header> responseHeaders, boolean outFinished, boolean flushHeaders) throws IOException {
        Object object;
        Intrinsics.checkNotNullParameter(responseHeaders, (String)"responseHeaders");
        Http2Stream $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        boolean flushHeaders2 = false;
        flushHeaders2 = flushHeaders;
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            boolean bl = false;
            this.hasResponseHeaders = true;
            if (outFinished) {
                this.sink.setFinished(true);
            }
            object = Unit.INSTANCE;
        }
        if (!flushHeaders2) {
            object = this.connection;
            synchronized (object) {
                boolean bl = false;
                flushHeaders2 = this.connection.getWriteBytesTotal() >= this.connection.getWriteBytesMaximum();
                Unit unit = Unit.INSTANCE;
            }
        }
        this.connection.writeHeaders$okhttp(this.id, outFinished, responseHeaders);
        if (flushHeaders2) {
            this.connection.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void enqueueTrailers(@NotNull Headers trailers) {
        Intrinsics.checkNotNullParameter((Object)trailers, (String)"trailers");
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            boolean bl = false;
            if (!(!this.sink.getFinished())) {
                boolean $i$a$-check-Http2Stream$enqueueTrailers$1$22 = false;
                String $i$a$-check-Http2Stream$enqueueTrailers$1$22 = "already finished";
                throw new IllegalStateException($i$a$-check-Http2Stream$enqueueTrailers$1$22.toString());
            }
            if (!(trailers.size() != 0)) {
                boolean bl2 = false;
                String string = "trailers.size() == 0";
                throw new IllegalArgumentException(string.toString());
            }
            this.sink.setTrailers(trailers);
            Unit unit = Unit.INSTANCE;
        }
    }

    @NotNull
    public final Timeout readTimeout() {
        return (Timeout)this.readTimeout;
    }

    @NotNull
    public final Timeout writeTimeout() {
        return (Timeout)this.writeTimeout;
    }

    @NotNull
    public final Source getSource() {
        return this.source;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final Sink getSink() {
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            boolean bl = false;
            if (!(this.hasResponseHeaders || this.isLocallyInitiated())) {
                boolean bl2 = false;
                String string = "reply before requesting the sink";
                throw new IllegalStateException(string.toString());
            }
            Unit unit = Unit.INSTANCE;
        }
        return this.sink;
    }

    public final void close(@NotNull ErrorCode rstStatusCode, @Nullable IOException errorException) throws IOException {
        Intrinsics.checkNotNullParameter((Object)((Object)rstStatusCode), (String)"rstStatusCode");
        if (!this.closeInternal(rstStatusCode, errorException)) {
            return;
        }
        this.connection.writeSynReset$okhttp(this.id, rstStatusCode);
    }

    public final void closeLater(@NotNull ErrorCode errorCode) {
        Intrinsics.checkNotNullParameter((Object)((Object)errorCode), (String)"errorCode");
        if (!this.closeInternal(errorCode, null)) {
            return;
        }
        this.connection.writeSynResetLater$okhttp(this.id, errorCode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean closeInternal(ErrorCode errorCode, IOException errorException) {
        Http2Stream $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            block7: {
                block6: {
                    boolean bl = false;
                    if (this.errorCode == null) break block6;
                    boolean bl2 = false;
                    return bl2;
                }
                this.errorCode = errorCode;
                this.errorException = errorException;
                Http2Stream $this$notifyAll$iv = this;
                boolean $i$f$notifyAll = false;
                Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                ((Object)$this$notifyAll$iv).notifyAll();
                if (!this.source.getFinished$okhttp() || !this.sink.getFinished()) break block7;
                boolean bl = false;
                return bl;
            }
            Unit unit = Unit.INSTANCE;
        }
        this.connection.removeStream$okhttp(this.id);
        return true;
    }

    public final void receiveData(@NotNull BufferedSource source, int length) throws IOException {
        Intrinsics.checkNotNullParameter((Object)source, (String)"source");
        Http2Stream $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        this.source.receive$okhttp(source, length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void receiveHeaders(@NotNull Headers headers, boolean inFinished) {
        Intrinsics.checkNotNullParameter((Object)headers, (String)"headers");
        Http2Stream $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        boolean open = false;
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            boolean bl = false;
            if (!this.hasResponseHeaders || !inFinished) {
                this.hasResponseHeaders = true;
                ((Collection)this.headersQueue).add(headers);
            } else {
                this.source.setTrailers(headers);
            }
            if (inFinished) {
                this.source.setFinished$okhttp(true);
            }
            open = this.isOpen();
            Http2Stream $this$notifyAll$iv = this;
            boolean $i$f$notifyAll = false;
            Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
            Unit unit = Unit.INSTANCE;
        }
        if (!open) {
            this.connection.removeStream$okhttp(this.id);
        }
    }

    public final synchronized void receiveRstStream(@NotNull ErrorCode errorCode) {
        Intrinsics.checkNotNullParameter((Object)((Object)errorCode), (String)"errorCode");
        if (this.errorCode == null) {
            this.errorCode = errorCode;
            Http2Stream $this$notifyAll$iv = this;
            boolean $i$f$notifyAll = false;
            Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void cancelStreamIfNecessary$okhttp() throws IOException {
        Http2Stream $this$assertThreadDoesntHoldLock$iv = this;
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
        }
        boolean open = false;
        boolean cancel = false;
        Http2Stream http2Stream = this;
        synchronized (http2Stream) {
            boolean bl = false;
            cancel = !this.source.getFinished$okhttp() && this.source.getClosed$okhttp() && (this.sink.getFinished() || this.sink.getClosed());
            open = this.isOpen();
            Unit unit = Unit.INSTANCE;
        }
        if (cancel) {
            this.close(ErrorCode.CANCEL, null);
        } else if (!open) {
            this.connection.removeStream$okhttp(this.id);
        }
    }

    public final void addBytesToWriteWindow(long delta) {
        this.writeBytesMaximum += delta;
        if (delta > 0L) {
            Http2Stream $this$notifyAll$iv = this;
            boolean $i$f$notifyAll = false;
            Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$notifyAll$iv).notifyAll();
        }
    }

    public final void checkOutNotClosed$okhttp() throws IOException {
        if (this.sink.getClosed()) {
            throw new IOException("stream closed");
        }
        if (this.sink.getFinished()) {
            throw new IOException("stream finished");
        }
        if (this.errorCode != null) {
            Throwable throwable;
            IOException iOException = this.errorException;
            if (iOException != null) {
                throwable = iOException;
            } else {
                ErrorCode errorCode = this.errorCode;
                Intrinsics.checkNotNull((Object)((Object)errorCode));
                throwable = new StreamResetException(errorCode);
            }
            throw throwable;
        }
    }

    public final void waitForIo$okhttp() throws InterruptedIOException {
        try {
            Http2Stream $this$wait$iv = this;
            boolean $i$f$wait = false;
            Intrinsics.checkNotNull((Object)$this$wait$iv, (String)"null cannot be cast to non-null type java.lang.Object");
            ((Object)$this$wait$iv).wait();
        }
        catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0080T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2={"Lokhttp3/internal/http2/Http2Stream$Companion;", "", "()V", "EMIT_BUFFER_SIZE", "", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0080\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\b\u0010\u0018\u001a\u00020\u0015H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\r2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\tR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001f"}, d2={"Lokhttp3/internal/http2/Http2Stream$FramingSink;", "Lokio/Sink;", "finished", "", "(Lokhttp3/internal/http2/Http2Stream;Z)V", "closed", "getClosed", "()Z", "setClosed", "(Z)V", "getFinished", "setFinished", "sendBuffer", "Lokio/Buffer;", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "close", "", "emitFrame", "outFinishedOnLastFrame", "flush", "timeout", "Lokio/Timeout;", "write", "source", "byteCount", "", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n615#2,4:689\n615#2,4:693\n615#2,4:697\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n*L\n528#1:689,4\n573#1:693,4\n589#1:697,4\n*E\n"})
    public final class FramingSink
    implements Sink {
        private boolean finished;
        @NotNull
        private final Buffer sendBuffer;
        @Nullable
        private Headers trailers;
        private boolean closed;
        final /* synthetic */ Http2Stream this$0;

        public FramingSink(Http2Stream this$0, boolean finished) {
            this.this$0 = this$0;
            this.finished = finished;
            this.sendBuffer = new Buffer();
        }

        public /* synthetic */ FramingSink(Http2Stream http2Stream, boolean bl, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 1) != 0) {
                bl = false;
            }
            this(http2Stream, bl);
        }

        public final boolean getFinished() {
            return this.finished;
        }

        public final void setFinished(boolean bl) {
            this.finished = bl;
        }

        @Nullable
        public final Headers getTrailers() {
            return this.trailers;
        }

        public final void setTrailers(@Nullable Headers headers) {
            this.trailers = headers;
        }

        public final boolean getClosed() {
            return this.closed;
        }

        public final void setClosed(boolean bl) {
            this.closed = bl;
        }

        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            Intrinsics.checkNotNullParameter((Object)source, (String)"source");
            Http2Stream $this$assertThreadDoesntHoldLock$iv = this.this$0;
            boolean $i$f$assertThreadDoesntHoldLock = false;
            if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
            }
            this.sendBuffer.write(source, byteCount);
            while (this.sendBuffer.size() >= 16384L) {
                this.emitFrame(false);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final void emitFrame(boolean outFinishedOnLastFrame) throws IOException {
            long toWrite = 0L;
            boolean outFinished = false;
            Http2Stream http2Stream = this.this$0;
            Http2Stream http2Stream2 = this.this$0;
            Http2Stream http2Stream3 = http2Stream;
            synchronized (http2Stream3) {
                boolean bl = false;
                http2Stream2.getWriteTimeout$okhttp().enter();
                try {
                    while (http2Stream2.getWriteBytesTotal() >= http2Stream2.getWriteBytesMaximum() && !this.finished && !this.closed && http2Stream2.getErrorCode$okhttp() == null) {
                        http2Stream2.waitForIo$okhttp();
                    }
                }
                finally {
                    http2Stream2.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
                }
                http2Stream2.checkOutNotClosed$okhttp();
                toWrite = Math.min(http2Stream2.getWriteBytesMaximum() - http2Stream2.getWriteBytesTotal(), this.sendBuffer.size());
                http2Stream2.setWriteBytesTotal$okhttp(http2Stream2.getWriteBytesTotal() + toWrite);
                outFinished = outFinishedOnLastFrame && toWrite == this.sendBuffer.size();
                Unit unit = Unit.INSTANCE;
            }
            this.this$0.getWriteTimeout$okhttp().enter();
            try {
                this.this$0.getConnection().writeData(this.this$0.getId(), outFinished, this.sendBuffer, toWrite);
            }
            finally {
                this.this$0.getWriteTimeout$okhttp().exitAndThrowIfTimedOut();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void flush() throws IOException {
            Http2Stream $this$assertThreadDoesntHoldLock$iv = this.this$0;
            boolean $i$f$assertThreadDoesntHoldLock = false;
            if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
            }
            Http2Stream http2Stream = this.this$0;
            Http2Stream http2Stream2 = this.this$0;
            Http2Stream http2Stream3 = http2Stream;
            synchronized (http2Stream3) {
                boolean bl = false;
                http2Stream2.checkOutNotClosed$okhttp();
                Unit unit = Unit.INSTANCE;
            }
            while (this.sendBuffer.size() > 0L) {
                this.emitFrame(false);
                this.this$0.getConnection().flush();
            }
        }

        @NotNull
        public Timeout timeout() {
            return (Timeout)this.this$0.getWriteTimeout$okhttp();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            Http2Stream $this$assertThreadDoesntHoldLock$iv = this.this$0;
            boolean $i$f$assertThreadDoesntHoldLock22 = false;
            if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
            }
            boolean outFinished = false;
            Http2Stream $i$f$assertThreadDoesntHoldLock22 = this.this$0;
            Http2Stream http2Stream = this.this$0;
            Http2Stream http2Stream2 = $i$f$assertThreadDoesntHoldLock22;
            synchronized (http2Stream2) {
                boolean bl = false;
                if (this.closed) {
                    return;
                }
                outFinished = http2Stream.getErrorCode$okhttp() == null;
                Unit unit = Unit.INSTANCE;
            }
            if (!this.this$0.getSink$okhttp().finished) {
                boolean hasData = this.sendBuffer.size() > 0L;
                boolean hasTrailers = this.trailers != null;
                if (hasTrailers) {
                    while (this.sendBuffer.size() > 0L) {
                        this.emitFrame(false);
                    }
                    Http2Connection http2Connection = this.this$0.getConnection();
                    int n = this.this$0.getId();
                    Headers headers = this.trailers;
                    Intrinsics.checkNotNull((Object)headers);
                    http2Connection.writeHeaders$okhttp(n, outFinished, Util.toHeaderList(headers));
                } else if (hasData) {
                    while (this.sendBuffer.size() > 0L) {
                        this.emitFrame(true);
                    }
                } else if (outFinished) {
                    this.this$0.getConnection().writeData(this.this$0.getId(), true, null, 0L);
                }
            }
            Http2Stream http2Stream3 = this.this$0;
            synchronized (http2Stream3) {
                boolean bl = false;
                this.closed = true;
                Unit unit = Unit.INSTANCE;
            }
            this.this$0.getConnection().flush();
            this.this$0.cancelStreamIfNecessary$okhttp();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0017\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\u0018\u0010\u001c\u001a\u00020\u00032\u0006\u0010\u001d\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\u0003H\u0016J\u001d\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!2\u0006\u0010\u001e\u001a\u00020\u0003H\u0000\u00a2\u0006\u0002\b\"J\b\u0010#\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0003H\u0002R\u001a\u0010\u0007\u001a\u00020\u0005X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0004\u001a\u00020\u0005X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019\u00a8\u0006&"}, d2={"Lokhttp3/internal/http2/Http2Stream$FramingSource;", "Lokio/Source;", "maxByteCount", "", "finished", "", "(Lokhttp3/internal/http2/Http2Stream;JZ)V", "closed", "getClosed$okhttp", "()Z", "setClosed$okhttp", "(Z)V", "getFinished$okhttp", "setFinished$okhttp", "readBuffer", "Lokio/Buffer;", "getReadBuffer", "()Lokio/Buffer;", "receiveBuffer", "getReceiveBuffer", "trailers", "Lokhttp3/Headers;", "getTrailers", "()Lokhttp3/Headers;", "setTrailers", "(Lokhttp3/Headers;)V", "close", "", "read", "sink", "byteCount", "receive", "source", "Lokio/BufferedSource;", "receive$okhttp", "timeout", "Lokio/Timeout;", "updateConnectionFlowControl", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n1#2:689\n615#3,4:690\n615#3,4:694\n563#3:698\n563#3:699\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n*L\n407#1:690,4\n418#1:694,4\n458#1:698\n480#1:699\n*E\n"})
    public final class FramingSource
    implements Source {
        private final long maxByteCount;
        private boolean finished;
        @NotNull
        private final Buffer receiveBuffer;
        @NotNull
        private final Buffer readBuffer;
        @Nullable
        private Headers trailers;
        private boolean closed;

        public FramingSource(long maxByteCount, boolean finished) {
            this.maxByteCount = maxByteCount;
            this.finished = finished;
            this.receiveBuffer = new Buffer();
            this.readBuffer = new Buffer();
        }

        public final boolean getFinished$okhttp() {
            return this.finished;
        }

        public final void setFinished$okhttp(boolean bl) {
            this.finished = bl;
        }

        @NotNull
        public final Buffer getReceiveBuffer() {
            return this.receiveBuffer;
        }

        @NotNull
        public final Buffer getReadBuffer() {
            return this.readBuffer;
        }

        @Nullable
        public final Headers getTrailers() {
            return this.trailers;
        }

        public final void setTrailers(@Nullable Headers headers) {
            this.trailers = headers;
        }

        public final boolean getClosed$okhttp() {
            return this.closed;
        }

        public final void setClosed$okhttp(boolean bl) {
            this.closed = bl;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public long read(@NotNull Buffer sink, long byteCount) throws IOException {
            IOException errorExceptionToDeliver;
            long readBytesDelivered;
            boolean tryAgain;
            Intrinsics.checkNotNullParameter((Object)sink, (String)"sink");
            if (!(byteCount >= 0L)) {
                boolean bl = false;
                String string = "byteCount < 0: " + byteCount;
                throw new IllegalArgumentException(string.toString());
            }
            do {
                tryAgain = false;
                readBytesDelivered = 0L;
                readBytesDelivered = -1L;
                errorExceptionToDeliver = null;
                Http2Stream http2Stream = Http2Stream.this;
                Http2Stream http2Stream2 = Http2Stream.this;
                Http2Stream http2Stream3 = http2Stream;
                synchronized (http2Stream3) {
                    boolean bl = false;
                    http2Stream2.getReadTimeout$okhttp().enter();
                    try {
                        IOException iOException;
                        if (http2Stream2.getErrorCode$okhttp() != null && !this.finished && (iOException = http2Stream2.getErrorException$okhttp()) == null) {
                            ErrorCode errorCode = http2Stream2.getErrorCode$okhttp();
                            Intrinsics.checkNotNull((Object)((Object)errorCode));
                            iOException = errorExceptionToDeliver = (IOException)new StreamResetException(errorCode);
                        }
                        if (this.closed) {
                            throw new IOException("stream closed");
                        }
                        if (this.readBuffer.size() > 0L) {
                            readBytesDelivered = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                            http2Stream2.setReadBytesTotal$okhttp(http2Stream2.getReadBytesTotal() + readBytesDelivered);
                            long unacknowledgedBytesRead = http2Stream2.getReadBytesTotal() - http2Stream2.getReadBytesAcknowledged();
                            if (errorExceptionToDeliver == null && unacknowledgedBytesRead >= (long)(http2Stream2.getConnection().getOkHttpSettings().getInitialWindowSize() / 2)) {
                                http2Stream2.getConnection().writeWindowUpdateLater$okhttp(http2Stream2.getId(), unacknowledgedBytesRead);
                                http2Stream2.setReadBytesAcknowledged$okhttp(http2Stream2.getReadBytesTotal());
                            }
                        } else if (!this.finished && errorExceptionToDeliver == null) {
                            http2Stream2.waitForIo$okhttp();
                            tryAgain = true;
                        }
                    }
                    finally {
                        http2Stream2.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                    }
                    Unit unit = Unit.INSTANCE;
                }
            } while (tryAgain);
            if (readBytesDelivered != -1L) {
                return readBytesDelivered;
            }
            if (errorExceptionToDeliver != null) {
                throw (Throwable)errorExceptionToDeliver;
            }
            return -1L;
        }

        private final void updateConnectionFlowControl(long read) {
            Http2Stream $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            boolean $i$f$assertThreadDoesntHoldLock = false;
            if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
            }
            Http2Stream.this.getConnection().updateConnectionFlowControl$okhttp(read);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void receive$okhttp(@NotNull BufferedSource source, long byteCount) throws IOException {
            Intrinsics.checkNotNullParameter((Object)source, (String)"source");
            Http2Stream $this$assertThreadDoesntHoldLock$iv = Http2Stream.this;
            boolean $i$f$assertThreadDoesntHoldLock = false;
            if (Util.assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock$iv));
            }
            long remainingByteCount = 0L;
            remainingByteCount = byteCount;
            while (remainingByteCount > 0L) {
                Object object;
                boolean finished = false;
                boolean flowControlError = false;
                Http2Stream http2Stream = Http2Stream.this;
                synchronized (http2Stream) {
                    boolean bl = false;
                    finished = this.finished;
                    flowControlError = remainingByteCount + this.readBuffer.size() > this.maxByteCount;
                    object = Unit.INSTANCE;
                }
                if (flowControlError) {
                    source.skip(remainingByteCount);
                    Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                    return;
                }
                if (finished) {
                    source.skip(remainingByteCount);
                    return;
                }
                long read = source.read(this.receiveBuffer, remainingByteCount);
                if (read == -1L) {
                    throw new EOFException();
                }
                remainingByteCount -= read;
                object = Http2Stream.this;
                Http2Stream http2Stream2 = Http2Stream.this;
                Object object2 = object;
                synchronized (object2) {
                    boolean bl = false;
                    if (this.closed) {
                        this.receiveBuffer.clear();
                    } else {
                        boolean wasEmpty = this.readBuffer.size() == 0L;
                        this.readBuffer.writeAll((Source)this.receiveBuffer);
                        if (wasEmpty) {
                            Http2Stream $this$notifyAll$iv = http2Stream2;
                            boolean $i$f$notifyAll = false;
                            Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                            ((Object)$this$notifyAll$iv).notifyAll();
                        }
                    }
                    Unit unit = Unit.INSTANCE;
                }
            }
            this.updateConnectionFlowControl(byteCount);
        }

        @NotNull
        public Timeout timeout() {
            return (Timeout)Http2Stream.this.getReadTimeout$okhttp();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            long bytesDiscarded = 0L;
            Http2Stream http2Stream = Http2Stream.this;
            Http2Stream http2Stream2 = Http2Stream.this;
            Http2Stream http2Stream3 = http2Stream;
            synchronized (http2Stream3) {
                boolean bl = false;
                this.closed = true;
                bytesDiscarded = this.readBuffer.size();
                this.readBuffer.clear();
                Http2Stream $this$notifyAll$iv = http2Stream2;
                boolean $i$f$notifyAll = false;
                Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                ((Object)$this$notifyAll$iv).notifyAll();
                Unit unit = Unit.INSTANCE;
            }
            if (bytesDiscarded > 0L) {
                this.updateConnectionFlowControl(bytesDiscarded);
            }
            Http2Stream.this.cancelStreamIfNecessary$okhttp();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0014J\b\u0010\b\u001a\u00020\u0004H\u0014\u00a8\u0006\t"}, d2={"Lokhttp3/internal/http2/Http2Stream$StreamTimeout;", "Lokio/AsyncTimeout;", "(Lokhttp3/internal/http2/Http2Stream;)V", "exitAndThrowIfTimedOut", "", "newTimeoutException", "Ljava/io/IOException;", "cause", "timedOut", "okhttp"})
    public final class StreamTimeout
    extends AsyncTimeout {
        protected void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
            Http2Stream.this.getConnection().sendDegradedPingLater$okhttp();
        }

        @NotNull
        protected IOException newTimeoutException(@Nullable IOException cause) {
            SocketTimeoutException socketTimeoutException;
            SocketTimeoutException $this$newTimeoutException_u24lambda_u240 = socketTimeoutException = new SocketTimeoutException("timeout");
            boolean bl = false;
            if (cause != null) {
                $this$newTimeoutException_u24lambda_u240.initCause(cause);
            }
            return socketTimeoutException;
        }

        public final void exitAndThrowIfTimedOut() throws IOException {
            if (this.exit()) {
                throw this.newTimeoutException(null);
            }
        }
    }
}

