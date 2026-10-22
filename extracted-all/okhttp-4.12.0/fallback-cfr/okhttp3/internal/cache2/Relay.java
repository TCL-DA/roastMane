/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  okio.Buffer
 *  okio.ByteString
 *  okio.Source
 *  okio.Timeout
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okhttp3.internal.cache2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.Util;
import okhttp3.internal.cache2.FileOperator;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\t\u0018\u0000 :2\u00020\u0001:\u0002:;B3\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u000bJ\u000e\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\tJ\b\u00105\u001a\u0004\u0018\u00010\u0005J \u00106\u001a\u0002032\u0006\u00107\u001a\u00020\t2\u0006\u00104\u001a\u00020\u00072\u0006\u00108\u001a\u00020\u0007H\u0002J\u0010\u00109\u001a\u0002032\u0006\u00104\u001a\u00020\u0007H\u0002R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u00138F\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u0015R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001d\u001a\u00020\u001eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u0011\u0010'\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010\u0011\"\u0004\b*\u0010+R\u001c\u0010,\u001a\u0004\u0018\u00010-X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101\u00a8\u0006<"}, d2={"Lokhttp3/internal/cache2/Relay;", "", "file", "Ljava/io/RandomAccessFile;", "upstream", "Lokio/Source;", "upstreamPos", "", "metadata", "Lokio/ByteString;", "bufferMaxSize", "(Ljava/io/RandomAccessFile;Lokio/Source;JLokio/ByteString;J)V", "buffer", "Lokio/Buffer;", "getBuffer", "()Lokio/Buffer;", "getBufferMaxSize", "()J", "complete", "", "getComplete", "()Z", "setComplete", "(Z)V", "getFile", "()Ljava/io/RandomAccessFile;", "setFile", "(Ljava/io/RandomAccessFile;)V", "isClosed", "sourceCount", "", "getSourceCount", "()I", "setSourceCount", "(I)V", "getUpstream", "()Lokio/Source;", "setUpstream", "(Lokio/Source;)V", "upstreamBuffer", "getUpstreamBuffer", "getUpstreamPos", "setUpstreamPos", "(J)V", "upstreamReader", "Ljava/lang/Thread;", "getUpstreamReader", "()Ljava/lang/Thread;", "setUpstreamReader", "(Ljava/lang/Thread;)V", "commit", "", "upstreamSize", "newSource", "writeHeader", "prefix", "metadataSize", "writeMetadata", "Companion", "RelaySource", "okhttp"})
public final class Relay {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private RandomAccessFile file;
    @Nullable
    private Source upstream;
    private long upstreamPos;
    @NotNull
    private final ByteString metadata;
    private final long bufferMaxSize;
    @Nullable
    private Thread upstreamReader;
    @NotNull
    private final Buffer upstreamBuffer;
    private boolean complete;
    @NotNull
    private final Buffer buffer;
    private int sourceCount;
    private static final int SOURCE_UPSTREAM = 1;
    private static final int SOURCE_FILE = 2;
    @JvmField
    @NotNull
    public static final ByteString PREFIX_CLEAN = ByteString.Companion.encodeUtf8("OkHttp cache v1\n");
    @JvmField
    @NotNull
    public static final ByteString PREFIX_DIRTY = ByteString.Companion.encodeUtf8("OkHttp DIRTY :(\n");
    private static final long FILE_HEADER_SIZE = 32L;

    private Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize) {
        this.file = file;
        this.upstream = upstream;
        this.upstreamPos = upstreamPos;
        this.metadata = metadata;
        this.bufferMaxSize = bufferMaxSize;
        this.upstreamBuffer = new Buffer();
        this.complete = this.upstream == null;
        this.buffer = new Buffer();
    }

    @Nullable
    public final RandomAccessFile getFile() {
        return this.file;
    }

    public final void setFile(@Nullable RandomAccessFile randomAccessFile) {
        this.file = randomAccessFile;
    }

    @Nullable
    public final Source getUpstream() {
        return this.upstream;
    }

    public final void setUpstream(@Nullable Source source) {
        this.upstream = source;
    }

    public final long getUpstreamPos() {
        return this.upstreamPos;
    }

    public final void setUpstreamPos(long l) {
        this.upstreamPos = l;
    }

    public final long getBufferMaxSize() {
        return this.bufferMaxSize;
    }

    @Nullable
    public final Thread getUpstreamReader() {
        return this.upstreamReader;
    }

    public final void setUpstreamReader(@Nullable Thread thread) {
        this.upstreamReader = thread;
    }

    @NotNull
    public final Buffer getUpstreamBuffer() {
        return this.upstreamBuffer;
    }

    public final boolean getComplete() {
        return this.complete;
    }

    public final void setComplete(boolean bl) {
        this.complete = bl;
    }

    @NotNull
    public final Buffer getBuffer() {
        return this.buffer;
    }

    public final int getSourceCount() {
        return this.sourceCount;
    }

    public final void setSourceCount(int n) {
        this.sourceCount = n;
    }

    public final boolean isClosed() {
        return this.file == null;
    }

    private final void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException {
        Buffer buffer;
        Buffer $this$writeHeader_u24lambda_u240 = buffer = new Buffer();
        boolean bl = false;
        $this$writeHeader_u24lambda_u240.write(prefix);
        $this$writeHeader_u24lambda_u240.writeLong(upstreamSize);
        $this$writeHeader_u24lambda_u240.writeLong(metadataSize);
        if (!($this$writeHeader_u24lambda_u240.size() == 32L)) {
            String string = "Failed requirement.";
            throw new IllegalArgumentException(string.toString());
        }
        Buffer header = buffer;
        RandomAccessFile randomAccessFile = this.file;
        Intrinsics.checkNotNull((Object)randomAccessFile);
        FileChannel fileChannel = randomAccessFile.getChannel();
        Intrinsics.checkNotNullExpressionValue((Object)fileChannel, (String)"file!!.channel");
        FileOperator fileOperator = new FileOperator(fileChannel);
        fileOperator.write(0L, header, 32L);
    }

    private final void writeMetadata(long upstreamSize) throws IOException {
        Buffer metadataBuffer = new Buffer();
        metadataBuffer.write(this.metadata);
        RandomAccessFile randomAccessFile = this.file;
        Intrinsics.checkNotNull((Object)randomAccessFile);
        FileChannel fileChannel = randomAccessFile.getChannel();
        Intrinsics.checkNotNullExpressionValue((Object)fileChannel, (String)"file!!.channel");
        FileOperator fileOperator = new FileOperator(fileChannel);
        fileOperator.write(32L + upstreamSize, metadataBuffer, this.metadata.size());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void commit(long upstreamSize) throws IOException {
        this.writeMetadata(upstreamSize);
        RandomAccessFile randomAccessFile = this.file;
        Intrinsics.checkNotNull((Object)randomAccessFile);
        randomAccessFile.getChannel().force(false);
        this.writeHeader(PREFIX_CLEAN, upstreamSize, this.metadata.size());
        RandomAccessFile randomAccessFile2 = this.file;
        Intrinsics.checkNotNull((Object)randomAccessFile2);
        randomAccessFile2.getChannel().force(false);
        Relay relay = this;
        synchronized (relay) {
            boolean bl = false;
            this.complete = true;
            Unit unit = Unit.INSTANCE;
        }
        Source source = this.upstream;
        if (source != null) {
            Util.closeQuietly((Closeable)source);
        }
        this.upstream = null;
    }

    @NotNull
    public final ByteString metadata() {
        return this.metadata;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public final Source newSource() {
        Relay relay = this;
        synchronized (relay) {
            block4: {
                boolean bl = false;
                if (this.file != null) break block4;
                Source source = null;
                return source;
            }
            int n = this.sourceCount;
            this.sourceCount = n + 1;
            int n2 = n;
        }
        return new RelaySource();
    }

    public /* synthetic */ Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize, DefaultConstructorMarker $constructor_marker) {
        this(file, upstream, upstreamPos, metadata, bufferMaxSize);
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J&\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0004J\u000e\u0010\u0013\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Lokhttp3/internal/cache2/Relay$Companion;", "", "()V", "FILE_HEADER_SIZE", "", "PREFIX_CLEAN", "Lokio/ByteString;", "PREFIX_DIRTY", "SOURCE_FILE", "", "SOURCE_UPSTREAM", "edit", "Lokhttp3/internal/cache2/Relay;", "file", "Ljava/io/File;", "upstream", "Lokio/Source;", "metadata", "bufferMaxSize", "read", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Relay edit(@NotNull File file, @NotNull Source upstream, @NotNull ByteString metadata, long bufferMaxSize) throws IOException {
            Intrinsics.checkNotNullParameter((Object)file, (String)"file");
            Intrinsics.checkNotNullParameter((Object)upstream, (String)"upstream");
            Intrinsics.checkNotNullParameter((Object)metadata, (String)"metadata");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            Relay result = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize, null);
            randomAccessFile.setLength(0L);
            result.writeHeader(PREFIX_DIRTY, -1L, -1L);
            return result;
        }

        @NotNull
        public final Relay read(@NotNull File file) throws IOException {
            Intrinsics.checkNotNullParameter((Object)file, (String)"file");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
            Intrinsics.checkNotNullExpressionValue((Object)fileChannel, (String)"randomAccessFile.channel");
            FileOperator fileOperator = new FileOperator(fileChannel);
            Buffer header = new Buffer();
            fileOperator.read(0L, header, 32L);
            ByteString prefix = header.readByteString((long)PREFIX_CLEAN.size());
            if (!Intrinsics.areEqual((Object)prefix, (Object)PREFIX_CLEAN)) {
                throw new IOException("unreadable cache file");
            }
            long upstreamSize = header.readLong();
            long metadataSize = header.readLong();
            Buffer metadataBuffer = new Buffer();
            fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
            ByteString metadata = metadataBuffer.readByteString();
            return new Relay(randomAccessFile, null, upstreamSize, metadata, 0L, null);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2={"Lokhttp3/internal/cache2/Relay$RelaySource;", "Lokio/Source;", "(Lokhttp3/internal/cache2/Relay;)V", "fileOperator", "Lokhttp3/internal/cache2/FileOperator;", "sourcePos", "", "timeout", "Lokio/Timeout;", "close", "", "read", "sink", "Lokio/Buffer;", "byteCount", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nRelay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,356:1\n563#2:357\n*S KotlinDebug\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n*L\n267#1:357\n*E\n"})
    public final class RelaySource
    implements Source {
        @NotNull
        private final Timeout timeout = new Timeout();
        @Nullable
        private FileOperator fileOperator;
        private long sourcePos;

        public RelaySource() {
            RandomAccessFile randomAccessFile = Relay.this.getFile();
            Intrinsics.checkNotNull((Object)randomAccessFile);
            FileChannel fileChannel = randomAccessFile.getChannel();
            Intrinsics.checkNotNullExpressionValue((Object)fileChannel, (String)"file!!.channel");
            this.fileOperator = new FileOperator(fileChannel);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public long read(@NotNull Buffer sink, long byteCount) throws IOException {
            Object object;
            long upstreamBytesRead;
            block21: {
                Relay bytesToRead2;
                Relay relay;
                long l;
                int n;
                Intrinsics.checkNotNullParameter((Object)sink, (String)"sink");
                if (!(this.fileOperator != null)) {
                    String string = "Check failed.";
                    throw new IllegalStateException(string.toString());
                }
                Relay relay2 = Relay.this;
                Relay relay3 = Relay.this;
                Relay relay4 = relay2;
                synchronized (relay4) {
                    int n2;
                    block20: {
                        long upstreamPos;
                        boolean bl = false;
                        while (this.sourcePos == (upstreamPos = relay3.getUpstreamPos())) {
                            if (relay3.getComplete()) {
                                return -1L;
                            }
                            if (relay3.getUpstreamReader() != null) {
                                this.timeout.waitUntilNotified((Object)relay3);
                                continue;
                            }
                            relay3.setUpstreamReader(Thread.currentThread());
                            n2 = 1;
                            break block20;
                        }
                        long bufferPos22 = relay3.getUpstreamPos() - relay3.getBuffer().size();
                        if (this.sourcePos >= bufferPos22) {
                            long bytesToRead2 = Math.min(byteCount, relay3.getUpstreamPos() - this.sourcePos);
                            relay3.getBuffer().copyTo(sink, this.sourcePos - bufferPos22, bytesToRead2);
                            this.sourcePos += bytesToRead2;
                            return bytesToRead2;
                        }
                        n2 = 2;
                    }
                    n = n2;
                }
                int source = n;
                if (source == 2) {
                    long bytesToRead3 = Math.min(byteCount, Relay.this.getUpstreamPos() - this.sourcePos);
                    FileOperator fileOperator = this.fileOperator;
                    Intrinsics.checkNotNull((Object)fileOperator);
                    fileOperator.read(32L + this.sourcePos, sink, bytesToRead3);
                    this.sourcePos += bytesToRead3;
                    return bytesToRead3;
                }
                try {
                    Source source2 = Relay.this.getUpstream();
                    Intrinsics.checkNotNull((Object)source2);
                    upstreamBytesRead = source2.read(Relay.this.getUpstreamBuffer(), Relay.this.getBufferMaxSize());
                    if (upstreamBytesRead != -1L) break block21;
                    Relay.this.commit(Relay.this.getUpstreamPos());
                    l = -1L;
                    Relay bufferPos22 = Relay.this;
                    relay = Relay.this;
                    bytesToRead2 = bufferPos22;
                }
                catch (Throwable throwable) {
                    relay3 = Relay.this;
                    relay4 = Relay.this;
                    Relay relay5 = relay3;
                    synchronized (relay5) {
                        boolean bl = false;
                        relay4.setUpstreamReader(null);
                        Relay $this$notifyAll$iv = relay4;
                        boolean $i$f$notifyAll = false;
                        Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                        $this$notifyAll$iv.notifyAll();
                        Unit unit = Unit.INSTANCE;
                        throw throwable;
                    }
                }
                synchronized (bytesToRead2) {
                    boolean $i$a$-synchronized-Relay$RelaySource$read$32 = false;
                    relay.setUpstreamReader(null);
                    Relay $this$notifyAll$iv = relay;
                    boolean $i$f$notifyAll = false;
                    Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                    ((Object)$this$notifyAll$iv).notifyAll();
                    Unit $i$a$-synchronized-Relay$RelaySource$read$32 = Unit.INSTANCE;
                    return l;
                }
            }
            long bytesRead = Math.min(upstreamBytesRead, byteCount);
            Relay.this.getUpstreamBuffer().copyTo(sink, 0L, bytesRead);
            this.sourcePos += bytesRead;
            FileOperator fileOperator = this.fileOperator;
            Intrinsics.checkNotNull((Object)fileOperator);
            fileOperator.write(32L + Relay.this.getUpstreamPos(), Relay.this.getUpstreamBuffer().clone(), upstreamBytesRead);
            Relay bufferPos22 = Relay.this;
            Relay relay = Relay.this;
            Relay bytesToRead2 = bufferPos22;
            synchronized (bytesToRead2) {
                boolean bl = false;
                relay.getBuffer().write(relay.getUpstreamBuffer(), upstreamBytesRead);
                if (relay.getBuffer().size() > relay.getBufferMaxSize()) {
                    relay.getBuffer().skip(relay.getBuffer().size() - relay.getBufferMaxSize());
                }
                relay.setUpstreamPos(relay.getUpstreamPos() + upstreamBytesRead);
                object = Unit.INSTANCE;
            }
            long bufferPos22 = bytesRead;
            bytesToRead2 = Relay.this;
            object = Relay.this;
            Relay relay6 = bytesToRead2;
            synchronized (relay6) {
                boolean bl = false;
                ((Relay)object).setUpstreamReader(null);
                Object $this$notifyAll$iv = object;
                boolean $i$f$notifyAll = false;
                Intrinsics.checkNotNull((Object)$this$notifyAll$iv, (String)"null cannot be cast to non-null type java.lang.Object");
                $this$notifyAll$iv.notifyAll();
                Unit unit = Unit.INSTANCE;
                return bufferPos22;
            }
        }

        @NotNull
        public Timeout timeout() {
            return this.timeout;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void close() throws IOException {
            block5: {
                if (this.fileOperator == null) {
                    return;
                }
                this.fileOperator = null;
                RandomAccessFile fileToClose = null;
                Relay relay = Relay.this;
                Relay relay2 = Relay.this;
                Relay relay3 = relay;
                synchronized (relay3) {
                    boolean bl = false;
                    int n = relay2.getSourceCount();
                    relay2.setSourceCount(n + -1);
                    if (relay2.getSourceCount() == 0) {
                        fileToClose = relay2.getFile();
                        relay2.setFile(null);
                    }
                    Unit unit = Unit.INSTANCE;
                }
                RandomAccessFile randomAccessFile = fileToClose;
                if (randomAccessFile == null) break block5;
                Util.closeQuietly(randomAccessFile);
            }
        }
    }
}

