/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.KotlinNothingValueException
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmOverloads
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  okio.BufferedSink
 *  okio.BufferedSource
 *  okio.ForwardingSource
 *  okio.Okio
 *  okio.Sink
 *  okio.Source
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.Util;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.cache.FaultHidingSink;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000y\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010)\n\u0002\b\u0007*\u0001\u0014\u0018\u0000 [2\u00020\u00012\u00020\u0002:\u0004[\\]^B7\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\b\u00108\u001a\u000209H\u0002J\b\u0010:\u001a\u000209H\u0016J!\u0010;\u001a\u0002092\n\u0010<\u001a\u00060=R\u00020\u00002\u0006\u0010>\u001a\u00020\u0010H\u0000\u00a2\u0006\u0002\b?J\u0006\u0010@\u001a\u000209J \u0010A\u001a\b\u0018\u00010=R\u00020\u00002\u0006\u0010B\u001a\u00020(2\b\b\u0002\u0010C\u001a\u00020\u000bH\u0007J\u0006\u0010D\u001a\u000209J\b\u0010E\u001a\u000209H\u0016J\u0017\u0010F\u001a\b\u0018\u00010GR\u00020\u00002\u0006\u0010B\u001a\u00020(H\u0086\u0002J\u0006\u0010H\u001a\u000209J\u0006\u0010I\u001a\u00020\u0010J\b\u0010J\u001a\u00020\u0010H\u0002J\b\u0010K\u001a\u00020%H\u0002J\b\u0010L\u001a\u000209H\u0002J\b\u0010M\u001a\u000209H\u0002J\u0010\u0010N\u001a\u0002092\u0006\u0010O\u001a\u00020(H\u0002J\r\u0010P\u001a\u000209H\u0000\u00a2\u0006\u0002\bQJ\u000e\u0010R\u001a\u00020\u00102\u0006\u0010B\u001a\u00020(J\u0019\u0010S\u001a\u00020\u00102\n\u0010T\u001a\u00060)R\u00020\u0000H\u0000\u00a2\u0006\u0002\bUJ\b\u0010V\u001a\u00020\u0010H\u0002J\u0006\u00105\u001a\u00020\u000bJ\u0010\u0010W\u001a\f\u0012\b\u0012\u00060GR\u00020\u00000XJ\u0006\u0010Y\u001a\u000209J\u0010\u0010Z\u001a\u0002092\u0006\u0010B\u001a\u00020(H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0015R\u001a\u0010\u0016\u001a\u00020\u0010X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u0003\u001a\u00020\u0004X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u000e\u0010\u001f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010%X\u0082\u000e\u00a2\u0006\u0002\n\u0000R$\u0010&\u001a\u0012\u0012\u0004\u0012\u00020(\u0012\b\u0012\u00060)R\u00020\u00000'X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R&\u0010\n\u001a\u00020\u000b2\u0006\u0010,\u001a\u00020\u000b8F@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u000e\u00101\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u00020\bX\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107\u00a8\u0006_"}, d2={"Lokhttp3/internal/cache/DiskLruCache;", "Ljava/io/Closeable;", "Ljava/io/Flushable;", "fileSystem", "Lokhttp3/internal/io/FileSystem;", "directory", "Ljava/io/File;", "appVersion", "", "valueCount", "maxSize", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "(Lokhttp3/internal/io/FileSystem;Ljava/io/File;IIJLokhttp3/internal/concurrent/TaskRunner;)V", "civilizedFileSystem", "", "cleanupQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "cleanupTask", "okhttp3/internal/cache/DiskLruCache$cleanupTask$1", "Lokhttp3/internal/cache/DiskLruCache$cleanupTask$1;", "closed", "getClosed$okhttp", "()Z", "setClosed$okhttp", "(Z)V", "getDirectory", "()Ljava/io/File;", "getFileSystem$okhttp", "()Lokhttp3/internal/io/FileSystem;", "hasJournalErrors", "initialized", "journalFile", "journalFileBackup", "journalFileTmp", "journalWriter", "Lokio/BufferedSink;", "lruEntries", "Ljava/util/LinkedHashMap;", "", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "getLruEntries$okhttp", "()Ljava/util/LinkedHashMap;", "value", "getMaxSize", "()J", "setMaxSize", "(J)V", "mostRecentRebuildFailed", "mostRecentTrimFailed", "nextSequenceNumber", "redundantOpCount", "size", "getValueCount$okhttp", "()I", "checkNotClosed", "", "close", "completeEdit", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "success", "completeEdit$okhttp", "delete", "edit", "key", "expectedSequenceNumber", "evictAll", "flush", "get", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "initialize", "isClosed", "journalRebuildRequired", "newJournalWriter", "processJournal", "readJournal", "readJournalLine", "line", "rebuildJournal", "rebuildJournal$okhttp", "remove", "removeEntry", "entry", "removeEntry$okhttp", "removeOldestEntry", "snapshots", "", "trimToSize", "validateKey", "Companion", "Editor", "Entry", "Snapshot", "okhttp"})
@SourceDebugExtension(value={"SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,1065:1\n1#2:1066\n608#3,4:1067\n37#4,2:1071\n37#4,2:1073\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n*L\n215#1:1067,4\n672#1:1071,2\n721#1:1073,2\n*E\n"})
public final class DiskLruCache
implements Closeable,
Flushable {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final FileSystem fileSystem;
    @NotNull
    private final File directory;
    private final int appVersion;
    private final int valueCount;
    private long maxSize;
    @NotNull
    private final File journalFile;
    @NotNull
    private final File journalFileTmp;
    @NotNull
    private final File journalFileBackup;
    private long size;
    @Nullable
    private BufferedSink journalWriter;
    @NotNull
    private final LinkedHashMap<String, Entry> lruEntries;
    private int redundantOpCount;
    private boolean hasJournalErrors;
    private boolean civilizedFileSystem;
    private boolean initialized;
    private boolean closed;
    private boolean mostRecentTrimFailed;
    private boolean mostRecentRebuildFailed;
    private long nextSequenceNumber;
    @NotNull
    private final TaskQueue cleanupQueue;
    @NotNull
    private final cleanupTask.1 cleanupTask;
    @JvmField
    @NotNull
    public static final String JOURNAL_FILE = "journal";
    @JvmField
    @NotNull
    public static final String JOURNAL_FILE_TEMP = "journal.tmp";
    @JvmField
    @NotNull
    public static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    @JvmField
    @NotNull
    public static final String MAGIC = "libcore.io.DiskLruCache";
    @JvmField
    @NotNull
    public static final String VERSION_1 = "1";
    @JvmField
    public static final long ANY_SEQUENCE_NUMBER = -1L;
    @JvmField
    @NotNull
    public static final Regex LEGAL_KEY_PATTERN = new Regex("[a-z0-9_-]{1,120}");
    @JvmField
    @NotNull
    public static final String CLEAN = "CLEAN";
    @JvmField
    @NotNull
    public static final String DIRTY = "DIRTY";
    @JvmField
    @NotNull
    public static final String REMOVE = "REMOVE";
    @JvmField
    @NotNull
    public static final String READ = "READ";

    public DiskLruCache(@NotNull FileSystem fileSystem, @NotNull File directory, int appVersion, int valueCount, long maxSize, @NotNull TaskRunner taskRunner) {
        Intrinsics.checkNotNullParameter((Object)fileSystem, (String)"fileSystem");
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter((Object)taskRunner, (String)"taskRunner");
        this.fileSystem = fileSystem;
        this.directory = directory;
        this.appVersion = appVersion;
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.lruEntries = new LinkedHashMap(0, 0.75f, true);
        this.cleanupQueue = taskRunner.newQueue();
        String string = Util.okHttpName + " Cache";
        this.cleanupTask = new Task(this, string){
            final /* synthetic */ DiskLruCache this$0;
            {
                this.this$0 = $receiver;
                super($super_call_param$1, false, 2, null);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public long runOnce() {
                DiskLruCache diskLruCache = this.this$0;
                DiskLruCache diskLruCache2 = this.this$0;
                DiskLruCache diskLruCache3 = diskLruCache;
                synchronized (diskLruCache3) {
                    block9: {
                        boolean bl = false;
                        if (DiskLruCache.access$getInitialized$p(diskLruCache2) && !diskLruCache2.getClosed$okhttp()) break block9;
                        long l = -1L;
                        return l;
                    }
                    try {
                        diskLruCache2.trimToSize();
                    }
                    catch (IOException _) {
                        DiskLruCache.access$setMostRecentTrimFailed$p(diskLruCache2, true);
                    }
                    try {
                        if (DiskLruCache.access$journalRebuildRequired(diskLruCache2)) {
                            diskLruCache2.rebuildJournal$okhttp();
                            DiskLruCache.access$setRedundantOpCount$p(diskLruCache2, 0);
                        }
                    }
                    catch (IOException _) {
                        DiskLruCache.access$setMostRecentRebuildFailed$p(diskLruCache2, true);
                        DiskLruCache.access$setJournalWriter$p(diskLruCache2, Okio.buffer((Sink)Okio.blackhole()));
                    }
                    long l = -1L;
                    return l;
                }
            }
        };
        if (!(maxSize > 0L)) {
            boolean $i$a$-require-DiskLruCache$32 = false;
            String $i$a$-require-DiskLruCache$32 = "maxSize <= 0";
            throw new IllegalArgumentException($i$a$-require-DiskLruCache$32.toString());
        }
        if (!(this.valueCount > 0)) {
            boolean bl = false;
            String string2 = "valueCount <= 0";
            throw new IllegalArgumentException(string2.toString());
        }
        this.journalFile = new File(this.directory, JOURNAL_FILE);
        this.journalFileTmp = new File(this.directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(this.directory, JOURNAL_FILE_BACKUP);
    }

    @NotNull
    public final FileSystem getFileSystem$okhttp() {
        return this.fileSystem;
    }

    @NotNull
    public final File getDirectory() {
        return this.directory;
    }

    public final int getValueCount$okhttp() {
        return this.valueCount;
    }

    public final synchronized long getMaxSize() {
        return this.maxSize;
    }

    public final synchronized void setMaxSize(long value) {
        this.maxSize = value;
        if (this.initialized) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
        }
    }

    @NotNull
    public final LinkedHashMap<String, Entry> getLruEntries$okhttp() {
        return this.lruEntries;
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
    public final synchronized void initialize() throws IOException {
        DiskLruCache $this$assertThreadHoldsLock$iv = this;
        boolean $i$f$assertThreadHoldsLock = false;
        if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
        }
        if (this.initialized) {
            return;
        }
        if (this.fileSystem.exists(this.journalFileBackup)) {
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.delete(this.journalFileBackup);
            } else {
                this.fileSystem.rename(this.journalFileBackup, this.journalFile);
            }
        }
        this.civilizedFileSystem = Util.isCivilized(this.fileSystem, this.journalFileBackup);
        if (this.fileSystem.exists(this.journalFile)) {
            try {
                this.readJournal();
                this.processJournal();
                this.initialized = true;
                return;
            }
            catch (IOException journalIsCorrupt) {
                Platform.Companion.get().log("DiskLruCache " + this.directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing", 5, journalIsCorrupt);
                try {
                    this.delete();
                }
                finally {
                    this.closed = false;
                }
            }
        }
        this.rebuildJournal$okhttp();
        this.initialized = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final void readJournal() throws IOException {
        Closeable closeable = (Closeable)Okio.buffer((Source)this.fileSystem.source(this.journalFile));
        Throwable throwable = null;
        try {
            BufferedSource source = (BufferedSource)closeable;
            boolean bl = false;
            String magic = source.readUtf8LineStrict();
            String version = source.readUtf8LineStrict();
            String appVersionString = source.readUtf8LineStrict();
            String valueCountString = source.readUtf8LineStrict();
            String blank = source.readUtf8LineStrict();
            if (!Intrinsics.areEqual((Object)MAGIC, (Object)magic)) throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
            if (!Intrinsics.areEqual((Object)VERSION_1, (Object)version)) throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
            if (!Intrinsics.areEqual((Object)String.valueOf(this.appVersion), (Object)appVersionString)) throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
            if (!Intrinsics.areEqual((Object)String.valueOf(this.valueCount), (Object)valueCountString)) throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
            if (((CharSequence)blank).length() > 0) {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
            }
            int lineCount = 0;
            try {
                while (true) {
                    this.readJournalLine(source.readUtf8LineStrict());
                    ++lineCount;
                }
            }
            catch (EOFException _) {
                this.redundantOpCount = lineCount - this.lruEntries.size();
                if (!source.exhausted()) {
                    this.rebuildJournal$okhttp();
                } else {
                    this.journalWriter = this.newJournalWriter();
                }
                Unit unit = Unit.INSTANCE;
                return;
            }
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
    }

    private final BufferedSink newJournalWriter() throws FileNotFoundException {
        Sink fileSink = this.fileSystem.appendingSink(this.journalFile);
        FaultHidingSink faultHidingSink2 = new FaultHidingSink(fileSink, (Function1<? super IOException, Unit>)((Function1)new Function1<IOException, Unit>(this){
            final /* synthetic */ DiskLruCache this$0;
            {
                this.this$0 = $receiver;
                super(1);
            }

            public final void invoke(@NotNull IOException it) {
                Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                DiskLruCache $this$assertThreadHoldsLock$iv = this.this$0;
                boolean $i$f$assertThreadHoldsLock = false;
                if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
                    throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
                }
                DiskLruCache.access$setHasJournalErrors$p(this.this$0, true);
            }
        }));
        return Okio.buffer((Sink)((Sink)faultHidingSink2));
    }

    private final void readJournalLine(String line) throws IOException {
        Entry entry;
        int firstSpace = StringsKt.indexOf$default((CharSequence)line, (char)' ', (int)0, (boolean)false, (int)6, null);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        }
        int keyBegin = firstSpace + 1;
        int secondSpace = StringsKt.indexOf$default((CharSequence)line, (char)' ', (int)keyBegin, (boolean)false, (int)4, null);
        String key = null;
        if (secondSpace == -1) {
            String string = line.substring(keyBegin);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).substring(startIndex)");
            key = string;
            if (firstSpace == REMOVE.length() && StringsKt.startsWith$default((String)line, (String)REMOVE, (boolean)false, (int)2, null)) {
                this.lruEntries.remove(key);
                return;
            }
        } else {
            String string = line.substring(keyBegin, secondSpace);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String\u2026ing(startIndex, endIndex)");
            key = string;
        }
        if ((entry = this.lruEntries.get(key)) == null) {
            entry = new Entry(key);
            ((Map)this.lruEntries).put(key, entry);
        }
        if (secondSpace != -1 && firstSpace == CLEAN.length() && StringsKt.startsWith$default((String)line, (String)CLEAN, (boolean)false, (int)2, null)) {
            String string = line.substring(secondSpace + 1);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).substring(startIndex)");
            char[] cArray = new char[]{' '};
            List parts = StringsKt.split$default((CharSequence)string, (char[])cArray, (boolean)false, (int)0, (int)6, null);
            entry.setReadable$okhttp(true);
            entry.setCurrentEditor$okhttp(null);
            entry.setLengths$okhttp(parts);
        } else if (secondSpace == -1 && firstSpace == DIRTY.length() && StringsKt.startsWith$default((String)line, (String)DIRTY, (boolean)false, (int)2, null)) {
            entry.setCurrentEditor$okhttp(new Editor(entry));
        } else if (secondSpace != -1 || firstSpace != READ.length() || !StringsKt.startsWith$default((String)line, (String)READ, (boolean)false, (int)2, null)) {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    private final void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            int t;
            int n;
            Entry entry;
            Intrinsics.checkNotNullExpressionValue((Object)i.next(), (String)"i.next()");
            if (entry.getCurrentEditor$okhttp() == null) {
                n = this.valueCount;
                for (t = 0; t < n; ++t) {
                    this.size += entry.getLengths$okhttp()[t];
                }
                continue;
            }
            entry.setCurrentEditor$okhttp(null);
            n = this.valueCount;
            for (t = 0; t < n; ++t) {
                this.fileSystem.delete(entry.getCleanFiles$okhttp().get(t));
                this.fileSystem.delete(entry.getDirtyFiles$okhttp().get(t));
            }
            i.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void rebuildJournal$okhttp() throws IOException {
        BufferedSink bufferedSink = this.journalWriter;
        if (bufferedSink != null) {
            bufferedSink.close();
        }
        Closeable closeable = (Closeable)Okio.buffer((Sink)this.fileSystem.sink(this.journalFileTmp));
        Throwable throwable = null;
        try {
            BufferedSink sink = (BufferedSink)closeable;
            boolean bl = false;
            sink.writeUtf8(MAGIC).writeByte(10);
            sink.writeUtf8(VERSION_1).writeByte(10);
            sink.writeDecimalLong((long)this.appVersion).writeByte(10);
            sink.writeDecimalLong((long)this.valueCount).writeByte(10);
            sink.writeByte(10);
            for (Entry entry : this.lruEntries.values()) {
                if (entry.getCurrentEditor$okhttp() != null) {
                    sink.writeUtf8(DIRTY).writeByte(32);
                    sink.writeUtf8(entry.getKey$okhttp());
                    sink.writeByte(10);
                    continue;
                }
                sink.writeUtf8(CLEAN).writeByte(32);
                sink.writeUtf8(entry.getKey$okhttp());
                entry.writeLengths$okhttp(sink);
                sink.writeByte(10);
            }
            Unit unit = Unit.INSTANCE;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
        if (this.fileSystem.exists(this.journalFile)) {
            this.fileSystem.rename(this.journalFile, this.journalFileBackup);
        }
        this.fileSystem.rename(this.journalFileTmp, this.journalFile);
        this.fileSystem.delete(this.journalFileBackup);
        this.journalWriter = this.newJournalWriter();
        this.hasJournalErrors = false;
        this.mostRecentRebuildFailed = false;
    }

    @Nullable
    public final synchronized Snapshot get(@NotNull String key) throws IOException {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        Entry entry = this.lruEntries.get(key);
        if (entry == null) {
            return null;
        }
        Entry entry2 = entry;
        Snapshot snapshot = entry2.snapshot$okhttp();
        if (snapshot == null) {
            return null;
        }
        Snapshot snapshot2 = snapshot;
        int n = this.redundantOpCount;
        this.redundantOpCount = n + 1;
        BufferedSink bufferedSink = this.journalWriter;
        Intrinsics.checkNotNull((Object)bufferedSink);
        bufferedSink.writeUtf8(READ).writeByte(32).writeUtf8(key).writeByte(10);
        if (this.journalRebuildRequired()) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
        }
        return snapshot2;
    }

    @JvmOverloads
    @Nullable
    public final synchronized Editor edit(@NotNull String key, long expectedSequenceNumber) throws IOException {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        Entry entry = this.lruEntries.get(key);
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER && (entry == null || entry.getSequenceNumber$okhttp() != expectedSequenceNumber)) {
            return null;
        }
        Entry entry2 = entry;
        if ((entry2 != null ? entry2.getCurrentEditor$okhttp() : null) != null) {
            return null;
        }
        if (entry != null && entry.getLockingSourceCount$okhttp() != 0) {
            return null;
        }
        if (this.mostRecentTrimFailed || this.mostRecentRebuildFailed) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            return null;
        }
        BufferedSink bufferedSink = this.journalWriter;
        Intrinsics.checkNotNull((Object)bufferedSink);
        BufferedSink journalWriter = bufferedSink;
        journalWriter.writeUtf8(DIRTY).writeByte(32).writeUtf8(key).writeByte(10);
        journalWriter.flush();
        if (this.hasJournalErrors) {
            return null;
        }
        if (entry == null) {
            entry = new Entry(key);
            ((Map)this.lruEntries).put(key, entry);
        }
        Editor editor = new Editor(entry);
        entry.setCurrentEditor$okhttp(editor);
        return editor;
    }

    public static /* synthetic */ Editor edit$default(DiskLruCache diskLruCache, String string, long l, int n, Object object) throws IOException {
        if ((n & 2) != 0) {
            l = ANY_SEQUENCE_NUMBER;
        }
        return diskLruCache.edit(string, l);
    }

    public final synchronized long size() throws IOException {
        this.initialize();
        return this.size;
    }

    public final synchronized void completeEdit$okhttp(@NotNull Editor editor, boolean success) throws IOException {
        BufferedSink bufferedSink;
        int i;
        int n;
        Intrinsics.checkNotNullParameter((Object)editor, (String)"editor");
        Entry entry = editor.getEntry$okhttp();
        if (!Intrinsics.areEqual((Object)entry.getCurrentEditor$okhttp(), (Object)editor)) {
            String string = "Check failed.";
            throw new IllegalStateException(string.toString());
        }
        if (success && !entry.getReadable$okhttp()) {
            n = this.valueCount;
            for (i = 0; i < n; ++i) {
                boolean[] blArray = editor.getWritten$okhttp();
                Intrinsics.checkNotNull((Object)blArray);
                if (!blArray[i]) {
                    editor.abort();
                    throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                }
                if (this.fileSystem.exists(entry.getDirtyFiles$okhttp().get(i))) continue;
                editor.abort();
                return;
            }
        }
        n = this.valueCount;
        for (i = 0; i < n; ++i) {
            File dirty = entry.getDirtyFiles$okhttp().get(i);
            if (success && !entry.getZombie$okhttp()) {
                long newLength;
                if (!this.fileSystem.exists(dirty)) continue;
                File clean = entry.getCleanFiles$okhttp().get(i);
                this.fileSystem.rename(dirty, clean);
                long oldLength = entry.getLengths$okhttp()[i];
                entry.getLengths$okhttp()[i] = newLength = this.fileSystem.size(clean);
                this.size = this.size - oldLength + newLength;
                continue;
            }
            this.fileSystem.delete(dirty);
        }
        entry.setCurrentEditor$okhttp(null);
        if (entry.getZombie$okhttp()) {
            this.removeEntry$okhttp(entry);
            return;
        }
        int n2 = this.redundantOpCount;
        this.redundantOpCount = n2 + 1;
        BufferedSink bufferedSink2 = this.journalWriter;
        Intrinsics.checkNotNull((Object)bufferedSink2);
        BufferedSink $this$completeEdit_u24lambda_u244 = bufferedSink = bufferedSink2;
        boolean bl = false;
        if (entry.getReadable$okhttp() || success) {
            entry.setReadable$okhttp(true);
            $this$completeEdit_u24lambda_u244.writeUtf8(CLEAN).writeByte(32);
            $this$completeEdit_u24lambda_u244.writeUtf8(entry.getKey$okhttp());
            entry.writeLengths$okhttp($this$completeEdit_u24lambda_u244);
            $this$completeEdit_u24lambda_u244.writeByte(10);
            if (success) {
                long l = this.nextSequenceNumber;
                this.nextSequenceNumber = l + 1L;
                entry.setSequenceNumber$okhttp(l);
            }
        } else {
            this.lruEntries.remove(entry.getKey$okhttp());
            $this$completeEdit_u24lambda_u244.writeUtf8(REMOVE).writeByte(32);
            $this$completeEdit_u24lambda_u244.writeUtf8(entry.getKey$okhttp());
            $this$completeEdit_u24lambda_u244.writeByte(10);
        }
        $this$completeEdit_u24lambda_u244.flush();
        if (this.size > this.maxSize || this.journalRebuildRequired()) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
        }
    }

    private final boolean journalRebuildRequired() {
        int redundantOpCompactThreshold = 2000;
        return this.redundantOpCount >= redundantOpCompactThreshold && this.redundantOpCount >= this.lruEntries.size();
    }

    public final synchronized boolean remove(@NotNull String key) throws IOException {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        Entry entry = this.lruEntries.get(key);
        if (entry == null) {
            return false;
        }
        Entry entry2 = entry;
        boolean removed = this.removeEntry$okhttp(entry2);
        if (removed && this.size <= this.maxSize) {
            this.mostRecentTrimFailed = false;
        }
        return removed;
    }

    public final boolean removeEntry$okhttp(@NotNull Entry entry) throws IOException {
        BufferedSink it;
        Intrinsics.checkNotNullParameter((Object)entry, (String)"entry");
        if (!this.civilizedFileSystem) {
            if (entry.getLockingSourceCount$okhttp() > 0) {
                BufferedSink bufferedSink = this.journalWriter;
                if (bufferedSink != null) {
                    it = bufferedSink;
                    boolean bl = false;
                    it.writeUtf8(DIRTY);
                    it.writeByte(32);
                    it.writeUtf8(entry.getKey$okhttp());
                    it.writeByte(10);
                    it.flush();
                }
            }
            if (entry.getLockingSourceCount$okhttp() > 0 || entry.getCurrentEditor$okhttp() != null) {
                entry.setZombie$okhttp(true);
                return true;
            }
        }
        Editor editor = entry.getCurrentEditor$okhttp();
        if (editor != null) {
            editor.detach$okhttp();
        }
        int n = this.valueCount;
        for (int i = 0; i < n; ++i) {
            this.fileSystem.delete(entry.getCleanFiles$okhttp().get(i));
            this.size -= entry.getLengths$okhttp()[i];
            entry.getLengths$okhttp()[i] = 0L;
        }
        int n2 = this.redundantOpCount;
        this.redundantOpCount = n2 + 1;
        BufferedSink bufferedSink = this.journalWriter;
        if (bufferedSink != null) {
            it = bufferedSink;
            boolean bl = false;
            it.writeUtf8(REMOVE);
            it.writeByte(32);
            it.writeUtf8(entry.getKey$okhttp());
            it.writeByte(10);
        }
        this.lruEntries.remove(entry.getKey$okhttp());
        if (this.journalRebuildRequired()) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
        }
        return true;
    }

    private final synchronized void checkNotClosed() {
        if (!(!this.closed)) {
            boolean bl = false;
            String string = "cache is closed";
            throw new IllegalStateException(string.toString());
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        if (!this.initialized) {
            return;
        }
        this.checkNotClosed();
        this.trimToSize();
        BufferedSink bufferedSink = this.journalWriter;
        Intrinsics.checkNotNull((Object)bufferedSink);
        bufferedSink.flush();
    }

    public final synchronized boolean isClosed() {
        return this.closed;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!this.initialized || this.closed) {
            this.closed = true;
            return;
        }
        Collection<Entry> collection = this.lruEntries.values();
        Intrinsics.checkNotNullExpressionValue(collection, (String)"lruEntries.values");
        Collection<Entry> $this$toTypedArray$iv = collection;
        boolean $i$f$toTypedArray = false;
        Collection<Entry> thisCollection$iv = $this$toTypedArray$iv;
        for (Entry entry : thisCollection$iv.toArray(new Entry[0])) {
            if (entry.getCurrentEditor$okhttp() == null) continue;
            Editor editor = entry.getCurrentEditor$okhttp();
            if (editor == null) continue;
            editor.detach$okhttp();
        }
        this.trimToSize();
        BufferedSink bufferedSink = this.journalWriter;
        Intrinsics.checkNotNull((Object)bufferedSink);
        bufferedSink.close();
        this.journalWriter = null;
        this.closed = true;
    }

    public final void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            if (this.removeOldestEntry()) continue;
            return;
        }
        this.mostRecentTrimFailed = false;
    }

    private final boolean removeOldestEntry() {
        for (Entry toEvict : this.lruEntries.values()) {
            if (toEvict.getZombie$okhttp()) continue;
            Intrinsics.checkNotNullExpressionValue((Object)toEvict, (String)"toEvict");
            this.removeEntry$okhttp(toEvict);
            return true;
        }
        return false;
    }

    public final void delete() throws IOException {
        this.close();
        this.fileSystem.deleteContents(this.directory);
    }

    public final synchronized void evictAll() throws IOException {
        this.initialize();
        Collection<Entry> collection = this.lruEntries.values();
        Intrinsics.checkNotNullExpressionValue(collection, (String)"lruEntries.values");
        Collection<Entry> $this$toTypedArray$iv = collection;
        boolean $i$f$toTypedArray = false;
        Collection<Entry> thisCollection$iv = $this$toTypedArray$iv;
        for (Entry entry : thisCollection$iv.toArray(new Entry[0])) {
            Intrinsics.checkNotNullExpressionValue((Object)entry, (String)"entry");
            this.removeEntry$okhttp(entry);
        }
        this.mostRecentTrimFailed = false;
    }

    private final void validateKey(String key) {
        if (!LEGAL_KEY_PATTERN.matches((CharSequence)key)) {
            boolean bl = false;
            String string = "keys must match regex [a-z0-9_-]{1,120}: \"" + key + '\"';
            throw new IllegalArgumentException(string.toString());
        }
    }

    @NotNull
    public final synchronized Iterator<Snapshot> snapshots() throws IOException {
        this.initialize();
        return new Iterator<Snapshot>(this){
            @NotNull
            private final Iterator<Entry> delegate;
            @Nullable
            private Snapshot nextSnapshot;
            @Nullable
            private Snapshot removeSnapshot;
            final /* synthetic */ DiskLruCache this$0;
            {
                this.this$0 = $receiver;
                Iterator<Entry> iterator = new ArrayList<Entry>($receiver.getLruEntries$okhttp().values()).iterator();
                Intrinsics.checkNotNullExpressionValue(iterator, (String)"ArrayList(lruEntries.values).iterator()");
                this.delegate = iterator;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public boolean hasNext() {
                if (this.nextSnapshot != null) {
                    return true;
                }
                DiskLruCache diskLruCache = this.this$0;
                DiskLruCache diskLruCache2 = this.this$0;
                DiskLruCache diskLruCache3 = diskLruCache;
                synchronized (diskLruCache3) {
                    block7: {
                        boolean bl = false;
                        if (!diskLruCache2.getClosed$okhttp()) break block7;
                        boolean bl2 = false;
                        return bl2;
                    }
                    while (this.delegate.hasNext()) {
                        Object object = this.delegate.next();
                        if (object == null || (object = ((Entry)object).snapshot$okhttp()) == null) continue;
                        this.nextSnapshot = object;
                        boolean bl = true;
                        return bl;
                    }
                    Unit unit = Unit.INSTANCE;
                }
                return false;
            }

            @NotNull
            public Snapshot next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.removeSnapshot = this.nextSnapshot;
                this.nextSnapshot = null;
                Snapshot snapshot = this.removeSnapshot;
                Intrinsics.checkNotNull((Object)snapshot);
                return snapshot;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void remove() {
                Snapshot removeSnapshot = this.removeSnapshot;
                if (removeSnapshot == null) {
                    boolean bl = false;
                    String string = "remove() before next()";
                    throw new IllegalStateException(string.toString());
                }
                try {
                    this.this$0.remove(removeSnapshot.key());
                }
                catch (IOException iOException) {
                }
                finally {
                    this.removeSnapshot = null;
                }
            }
        };
    }

    @JvmOverloads
    @Nullable
    public final Editor edit(@NotNull String key) throws IOException {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        return DiskLruCache.edit$default(this, key, 0L, 2, null);
    }

    public static final /* synthetic */ void access$setHasJournalErrors$p(DiskLruCache $this, boolean bl) {
        $this.hasJournalErrors = bl;
    }

    public static final /* synthetic */ boolean access$getInitialized$p(DiskLruCache $this) {
        return $this.initialized;
    }

    public static final /* synthetic */ void access$setMostRecentTrimFailed$p(DiskLruCache $this, boolean bl) {
        $this.mostRecentTrimFailed = bl;
    }

    public static final /* synthetic */ boolean access$journalRebuildRequired(DiskLruCache $this) {
        return $this.journalRebuildRequired();
    }

    public static final /* synthetic */ void access$setRedundantOpCount$p(DiskLruCache $this, int n) {
        $this.redundantOpCount = n;
    }

    public static final /* synthetic */ void access$setMostRecentRebuildFailed$p(DiskLruCache $this, boolean bl) {
        $this.mostRecentRebuildFailed = bl;
    }

    public static final /* synthetic */ void access$setJournalWriter$p(DiskLruCache $this, BufferedSink bufferedSink) {
        $this.journalWriter = bufferedSink;
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\f8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\u00068\u0006X\u0087D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2={"Lokhttp3/internal/cache/DiskLruCache$Companion;", "", "()V", "ANY_SEQUENCE_NUMBER", "", "CLEAN", "", "DIRTY", "JOURNAL_FILE", "JOURNAL_FILE_BACKUP", "JOURNAL_FILE_TEMP", "LEGAL_KEY_PATTERN", "Lkotlin/text/Regex;", "MAGIC", "READ", "REMOVE", "VERSION_1", "okhttp"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0018\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0013\b\u0000\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fJ\r\u0010\u0011\u001a\u00020\u000fH\u0000\u00a2\u0006\u0002\b\u0012J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0015\u001a\u00020\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0002\u001a\u00060\u0003R\u00020\u0004X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0019"}, d2={"Lokhttp3/internal/cache/DiskLruCache$Editor;", "", "entry", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "Lokhttp3/internal/cache/DiskLruCache;", "(Lokhttp3/internal/cache/DiskLruCache;Lokhttp3/internal/cache/DiskLruCache$Entry;)V", "done", "", "getEntry$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Entry;", "written", "", "getWritten$okhttp", "()[Z", "abort", "", "commit", "detach", "detach$okhttp", "newSink", "Lokio/Sink;", "index", "", "newSource", "Lokio/Source;", "okhttp"})
    public final class Editor {
        @NotNull
        private final Entry entry;
        @Nullable
        private final boolean[] written;
        private boolean done;

        public Editor(Entry entry) {
            Intrinsics.checkNotNullParameter((Object)entry, (String)"entry");
            this.entry = entry;
            this.written = this.entry.getReadable$okhttp() ? null : new boolean[DiskLruCache.this.getValueCount$okhttp()];
        }

        @NotNull
        public final Entry getEntry$okhttp() {
            return this.entry;
        }

        @Nullable
        public final boolean[] getWritten$okhttp() {
            return this.written;
        }

        public final void detach$okhttp() {
            if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
                if (DiskLruCache.this.civilizedFileSystem) {
                    DiskLruCache.this.completeEdit$okhttp(this, false);
                } else {
                    this.entry.setZombie$okhttp(true);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public final Source newSource(int index) {
            DiskLruCache diskLruCache = DiskLruCache.this;
            DiskLruCache diskLruCache2 = DiskLruCache.this;
            DiskLruCache diskLruCache3 = diskLruCache;
            synchronized (diskLruCache3) {
                Source source;
                block7: {
                    boolean bl = false;
                    if (!(!this.done)) {
                        String string = "Check failed.";
                        throw new IllegalStateException(string.toString());
                    }
                    if (this.entry.getReadable$okhttp() && Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this) && !this.entry.getZombie$okhttp()) break block7;
                    Source source2 = null;
                    return source2;
                }
                try {
                    source = diskLruCache2.getFileSystem$okhttp().source(this.entry.getCleanFiles$okhttp().get(index));
                }
                catch (FileNotFoundException _) {
                    source = null;
                }
                Source source3 = source;
                return source3;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @NotNull
        public final Sink newSink(int index) {
            DiskLruCache diskLruCache = DiskLruCache.this;
            DiskLruCache diskLruCache2 = DiskLruCache.this;
            DiskLruCache diskLruCache3 = diskLruCache;
            synchronized (diskLruCache3) {
                block9: {
                    boolean bl = false;
                    if (!(!this.done)) {
                        String string = "Check failed.";
                        throw new IllegalStateException(string.toString());
                    }
                    if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) break block9;
                    Sink sink = Okio.blackhole();
                    return sink;
                }
                if (!this.entry.getReadable$okhttp()) {
                    Intrinsics.checkNotNull((Object)this.written);
                    this.written[index] = true;
                }
                File dirtyFile = this.entry.getDirtyFiles$okhttp().get(index);
                Sink sink = null;
                try {
                    sink = diskLruCache2.getFileSystem$okhttp().sink(dirtyFile);
                }
                catch (FileNotFoundException _) {
                    Sink sink2 = Okio.blackhole();
                    return sink2;
                }
                Sink sink3 = (Sink)new FaultHidingSink(sink, (Function1<? super IOException, Unit>)((Function1)new Function1<IOException, Unit>(diskLruCache2, this){
                    final /* synthetic */ DiskLruCache this$0;
                    final /* synthetic */ Editor this$1;
                    {
                        this.this$0 = $receiver;
                        this.this$1 = $receiver2;
                        super(1);
                    }

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public final void invoke(@NotNull IOException it) {
                        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
                        DiskLruCache diskLruCache = this.this$0;
                        Editor editor = this.this$1;
                        DiskLruCache diskLruCache2 = diskLruCache;
                        synchronized (diskLruCache2) {
                            boolean bl = false;
                            editor.detach$okhttp();
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }));
                return sink3;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void commit() throws IOException {
            DiskLruCache diskLruCache = DiskLruCache.this;
            DiskLruCache diskLruCache2 = DiskLruCache.this;
            DiskLruCache diskLruCache3 = diskLruCache;
            synchronized (diskLruCache3) {
                boolean bl = false;
                if (!(!this.done)) {
                    String string = "Check failed.";
                    throw new IllegalStateException(string.toString());
                }
                if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
                    diskLruCache2.completeEdit$okhttp(this, true);
                }
                this.done = true;
                Unit unit = Unit.INSTANCE;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final void abort() throws IOException {
            DiskLruCache diskLruCache = DiskLruCache.this;
            DiskLruCache diskLruCache2 = DiskLruCache.this;
            DiskLruCache diskLruCache3 = diskLruCache;
            synchronized (diskLruCache3) {
                boolean bl = false;
                if (!(!this.done)) {
                    String string = "Check failed.";
                    throw new IllegalStateException(string.toString());
                }
                if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
                    diskLruCache2.completeEdit$okhttp(this, false);
                }
                this.done = true;
                Unit unit = Unit.INSTANCE;
            }
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000v\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0016\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0001\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0080\u0004\u0018\u00002\u00020\u0001B\u000f\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010.\u001a\u00020/2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u000301H\u0002J\u0010\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u001aH\u0002J\u001b\u00105\u001a\u0002062\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u000301H\u0000\u00a2\u0006\u0002\b7J\u0013\u00108\u001a\b\u0018\u000109R\u00020\fH\u0000\u00a2\u0006\u0002\b:J\u0015\u0010;\u001a\u0002062\u0006\u0010<\u001a\u00020=H\u0000\u00a2\u0006\u0002\b>R\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR \u0010\n\u001a\b\u0018\u00010\u000bR\u00020\fX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\tR\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\u0016X\u0080\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\u00020\u001aX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u001f\u001a\u00020 X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010%\u001a\u00020&X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020 X\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b,\u0010\"\"\u0004\b-\u0010$\u00a8\u0006?"}, d2={"Lokhttp3/internal/cache/DiskLruCache$Entry;", "", "key", "", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;)V", "cleanFiles", "", "Ljava/io/File;", "getCleanFiles$okhttp", "()Ljava/util/List;", "currentEditor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getCurrentEditor$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Editor;", "setCurrentEditor$okhttp", "(Lokhttp3/internal/cache/DiskLruCache$Editor;)V", "dirtyFiles", "getDirtyFiles$okhttp", "getKey$okhttp", "()Ljava/lang/String;", "lengths", "", "getLengths$okhttp", "()[J", "lockingSourceCount", "", "getLockingSourceCount$okhttp", "()I", "setLockingSourceCount$okhttp", "(I)V", "readable", "", "getReadable$okhttp", "()Z", "setReadable$okhttp", "(Z)V", "sequenceNumber", "", "getSequenceNumber$okhttp", "()J", "setSequenceNumber$okhttp", "(J)V", "zombie", "getZombie$okhttp", "setZombie$okhttp", "invalidLengths", "", "strings", "", "newSource", "Lokio/Source;", "index", "setLengths", "", "setLengths$okhttp", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "snapshot$okhttp", "writeLengths", "writer", "Lokio/BufferedSink;", "writeLengths$okhttp", "okhttp"})
    @SourceDebugExtension(value={"SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,1065:1\n608#2,4:1066\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n*L\n1001#1:1066,4\n*E\n"})
    public final class Entry {
        @NotNull
        private final String key;
        @NotNull
        private final long[] lengths;
        @NotNull
        private final List<File> cleanFiles;
        @NotNull
        private final List<File> dirtyFiles;
        private boolean readable;
        private boolean zombie;
        @Nullable
        private Editor currentEditor;
        private int lockingSourceCount;
        private long sequenceNumber;

        public Entry(String key) {
            Intrinsics.checkNotNullParameter((Object)key, (String)"key");
            this.key = key;
            this.lengths = new long[DiskLruCache.this.getValueCount$okhttp()];
            this.cleanFiles = new ArrayList();
            this.dirtyFiles = new ArrayList();
            StringBuilder fileBuilder = new StringBuilder(this.key).append('.');
            int truncateTo = fileBuilder.length();
            int n = DiskLruCache.this.getValueCount$okhttp();
            for (int i = 0; i < n; ++i) {
                fileBuilder.append(i);
                ((Collection)this.cleanFiles).add(new File(DiskLruCache.this.getDirectory(), fileBuilder.toString()));
                fileBuilder.append(".tmp");
                ((Collection)this.dirtyFiles).add(new File(DiskLruCache.this.getDirectory(), fileBuilder.toString()));
                fileBuilder.setLength(truncateTo);
            }
        }

        @NotNull
        public final String getKey$okhttp() {
            return this.key;
        }

        @NotNull
        public final long[] getLengths$okhttp() {
            return this.lengths;
        }

        @NotNull
        public final List<File> getCleanFiles$okhttp() {
            return this.cleanFiles;
        }

        @NotNull
        public final List<File> getDirtyFiles$okhttp() {
            return this.dirtyFiles;
        }

        public final boolean getReadable$okhttp() {
            return this.readable;
        }

        public final void setReadable$okhttp(boolean bl) {
            this.readable = bl;
        }

        public final boolean getZombie$okhttp() {
            return this.zombie;
        }

        public final void setZombie$okhttp(boolean bl) {
            this.zombie = bl;
        }

        @Nullable
        public final Editor getCurrentEditor$okhttp() {
            return this.currentEditor;
        }

        public final void setCurrentEditor$okhttp(@Nullable Editor editor) {
            this.currentEditor = editor;
        }

        public final int getLockingSourceCount$okhttp() {
            return this.lockingSourceCount;
        }

        public final void setLockingSourceCount$okhttp(int n) {
            this.lockingSourceCount = n;
        }

        public final long getSequenceNumber$okhttp() {
            return this.sequenceNumber;
        }

        public final void setSequenceNumber$okhttp(long l) {
            this.sequenceNumber = l;
        }

        public final void setLengths$okhttp(@NotNull List<String> strings) throws IOException {
            Intrinsics.checkNotNullParameter(strings, (String)"strings");
            if (strings.size() != DiskLruCache.this.getValueCount$okhttp()) {
                this.invalidLengths(strings);
                throw new KotlinNothingValueException();
            }
            try {
                int n = strings.size();
                for (int i = 0; i < n; ++i) {
                    this.lengths[i] = Long.parseLong(strings.get(i));
                }
            }
            catch (NumberFormatException _) {
                this.invalidLengths(strings);
                throw new KotlinNothingValueException();
            }
        }

        public final void writeLengths$okhttp(@NotNull BufferedSink writer) throws IOException {
            Intrinsics.checkNotNullParameter((Object)writer, (String)"writer");
            for (long length : this.lengths) {
                writer.writeByte(32).writeDecimalLong(length);
            }
        }

        private final Void invalidLengths(List<String> strings) throws IOException {
            throw new IOException("unexpected journal line: " + strings);
        }

        @Nullable
        public final Snapshot snapshot$okhttp() {
            DiskLruCache $this$assertThreadHoldsLock$iv = DiskLruCache.this;
            boolean $i$f$assertThreadHoldsLock = false;
            if (Util.assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock$iv)) {
                throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock$iv));
            }
            if (!this.readable) {
                return null;
            }
            if (!DiskLruCache.this.civilizedFileSystem && (this.currentEditor != null || this.zombie)) {
                return null;
            }
            List sources = new ArrayList();
            long[] lengths = (long[])this.lengths.clone();
            try {
                int n = DiskLruCache.this.getValueCount$okhttp();
                for (int i = 0; i < n; ++i) {
                    ((Collection)sources).add(this.newSource(i));
                }
                return new Snapshot(this.key, this.sequenceNumber, sources, lengths);
            }
            catch (FileNotFoundException _) {
                for (Source source : sources) {
                    Util.closeQuietly((Closeable)source);
                }
                try {
                    DiskLruCache.this.removeEntry$okhttp(this);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                return null;
            }
        }

        private final Source newSource(int index) {
            Source fileSource = DiskLruCache.this.getFileSystem$okhttp().source(this.cleanFiles.get(index));
            if (DiskLruCache.this.civilizedFileSystem) {
                return fileSource;
            }
            int n = this.lockingSourceCount;
            this.lockingSourceCount = n + 1;
            return (Source)new ForwardingSource(fileSource, DiskLruCache.this, this){
                private boolean closed;
                final /* synthetic */ DiskLruCache this$0;
                final /* synthetic */ Entry this$1;
                {
                    this.this$0 = $receiver;
                    this.this$1 = $receiver2;
                    super($fileSource);
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void close() {
                    super.close();
                    if (!this.closed) {
                        this.closed = true;
                        DiskLruCache diskLruCache = this.this$0;
                        Entry entry = this.this$1;
                        DiskLruCache diskLruCache2 = this.this$0;
                        DiskLruCache diskLruCache3 = diskLruCache;
                        synchronized (diskLruCache3) {
                            boolean bl = false;
                            int n = entry.getLockingSourceCount$okhttp();
                            entry.setLockingSourceCount$okhttp(n + -1);
                            if (entry.getLockingSourceCount$okhttp() == 0 && entry.getZombie$okhttp()) {
                                diskLruCache2.removeEntry$okhttp(entry);
                            }
                            Unit unit = Unit.INSTANCE;
                        }
                    }
                }
            };
        }
    }

    @Metadata(mv={1, 8, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B-\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\b\u0010\f\u001a\u00020\rH\u0016J\f\u0010\u000e\u001a\b\u0018\u00010\u000fR\u00020\u0010J\u000e\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0002\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2={"Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Ljava/io/Closeable;", "key", "", "sequenceNumber", "", "sources", "", "Lokio/Source;", "lengths", "", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;JLjava/util/List;[J)V", "close", "", "edit", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getLength", "index", "", "getSource", "okhttp"})
    public final class Snapshot
    implements Closeable {
        @NotNull
        private final String key;
        private final long sequenceNumber;
        @NotNull
        private final List<Source> sources;
        @NotNull
        private final long[] lengths;

        public Snapshot(String key, @NotNull long sequenceNumber, @NotNull List<? extends Source> sources, long[] lengths) {
            Intrinsics.checkNotNullParameter((Object)key, (String)"key");
            Intrinsics.checkNotNullParameter(sources, (String)"sources");
            Intrinsics.checkNotNullParameter((Object)lengths, (String)"lengths");
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.sources = sources;
            this.lengths = lengths;
        }

        @NotNull
        public final String key() {
            return this.key;
        }

        @Nullable
        public final Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        @NotNull
        public final Source getSource(int index) {
            return this.sources.get(index);
        }

        public final long getLength(int index) {
            return this.lengths[index];
        }

        @Override
        public void close() {
            for (Source source : this.sources) {
                Util.closeQuietly((Closeable)source);
            }
        }
    }
}

