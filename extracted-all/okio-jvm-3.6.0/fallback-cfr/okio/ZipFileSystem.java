/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.ExceptionsKt
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okio;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.BufferedSource;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.InflaterSource;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import okio.internal.FixedLengthSource;
import okio.internal.ZipEntry;
import okio.internal.ZipFilesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000 '2\u00020\u0001:\u0001'B5\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0003H\u0016J\u0010\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0003H\u0016J\u0010\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0003H\u0002J\u0018\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u000fH\u0016J\u0018\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0003H\u0016J\u0018\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0016\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d2\u0006\u0010\u0018\u001a\u00020\u0003H\u0016J \u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u001d2\u0006\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u000fH\u0002J\u0018\u0010\u001f\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u001d2\u0006\u0010\u0018\u001a\u00020\u0003H\u0016J\u0012\u0010 \u001a\u0004\u0018\u00010!2\u0006\u0010\u0015\u001a\u00020\u0003H\u0016J\u0010\u0010\"\u001a\u00020#2\u0006\u0010\r\u001a\u00020\u0003H\u0016J \u0010$\u001a\u00020#2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010%\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u000fH\u0016J\u0010\u0010\u0012\u001a\u00020&2\u0006\u0010\r\u001a\u00020\u0003H\u0016R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2={"Lokio/ZipFileSystem;", "Lokio/FileSystem;", "zipPath", "Lokio/Path;", "fileSystem", "entries", "", "Lokio/internal/ZipEntry;", "comment", "", "(Lokio/Path;Lokio/FileSystem;Ljava/util/Map;Ljava/lang/String;)V", "appendingSink", "Lokio/Sink;", "file", "mustExist", "", "atomicMove", "", "source", "target", "canonicalize", "path", "canonicalizeInternal", "createDirectory", "dir", "mustCreate", "createSymlink", "delete", "list", "", "throwOnFailure", "listOrNull", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "openReadWrite", "sink", "Lokio/Source;", "Companion", "okio"})
@SourceDebugExtension(value={"SMAP\nZipFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,175:1\n52#2,5:176\n52#2,21:181\n60#2,10:202\n57#2,2:212\n71#2,2:214\n52#2,21:216\n*S KotlinDebug\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n*L\n102#1:176,5\n103#1:181,21\n102#1:202,10\n102#1:212,2\n102#1:214,2\n132#1:216,21\n*E\n"})
public final class ZipFileSystem
extends FileSystem {
    @NotNull
    private static final Companion Companion = new Companion(null);
    @NotNull
    private final Path zipPath;
    @NotNull
    private final FileSystem fileSystem;
    @NotNull
    private final Map<Path, ZipEntry> entries;
    @Nullable
    private final String comment;
    @NotNull
    private static final Path ROOT = Path.Companion.get$default(Path.Companion, "/", false, 1, null);

    public ZipFileSystem(@NotNull Path zipPath, @NotNull FileSystem fileSystem, @NotNull Map<Path, ZipEntry> entries, @Nullable String comment) {
        Intrinsics.checkNotNullParameter((Object)zipPath, (String)"zipPath");
        Intrinsics.checkNotNullParameter((Object)fileSystem, (String)"fileSystem");
        Intrinsics.checkNotNullParameter(entries, (String)"entries");
        this.zipPath = zipPath;
        this.fileSystem = fileSystem;
        this.entries = entries;
        this.comment = comment;
    }

    @Override
    @NotNull
    public Path canonicalize(@NotNull Path path) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Path canonical = this.canonicalizeInternal(path);
        if (!this.entries.containsKey(canonical)) {
            throw new FileNotFoundException(String.valueOf(path));
        }
        return canonical;
    }

    private final Path canonicalizeInternal(Path path) {
        return ROOT.resolve(path, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    @Nullable
    public FileMetadata metadataOrNull(@NotNull Path path) {
        Throwable thrown$iv;
        FileMetadata result$iv;
        block33: {
            Throwable thrown$iv2;
            FileMetadata result$iv2;
            Closeable $this$use$iv;
            block32: {
                Intrinsics.checkNotNullParameter((Object)path, (String)"path");
                Path canonicalPath = this.canonicalizeInternal(path);
                ZipEntry zipEntry = this.entries.get(canonicalPath);
                if (zipEntry == null) {
                    return null;
                }
                ZipEntry entry = zipEntry;
                FileMetadata basicMetadata = new FileMetadata(!entry.isDirectory(), entry.isDirectory(), null, entry.isDirectory() ? null : Long.valueOf(entry.getSize()), null, entry.getLastModifiedAtMillis(), null, null, 128, null);
                if (entry.getOffset() == -1L) {
                    return basicMetadata;
                }
                $this$use$iv = this.fileSystem.openReadOnly(this.zipPath);
                boolean $i$f$use = false;
                result$iv = null;
                thrown$iv = null;
                FileHandle fileHandle = (FileHandle)$this$use$iv;
                boolean bl = false;
                Closeable $this$use$iv2 = Okio.buffer(fileHandle.source(entry.getOffset()));
                boolean $i$f$use2 = false;
                result$iv2 = null;
                thrown$iv2 = null;
                BufferedSource source2 = (BufferedSource)$this$use$iv2;
                boolean bl2 = false;
                result$iv2 = ZipFilesKt.readLocalHeader(source2, basicMetadata);
                try {
                    Closeable closeable = $this$use$iv2;
                    if (closeable != null) {
                        closeable.close();
                    }
                    break block32;
                }
                catch (Throwable t$iv) {
                    thrown$iv2 = t$iv;
                }
                break block32;
                catch (Throwable t$iv) {
                    try {
                        thrown$iv2 = t$iv;
                    }
                    catch (Throwable throwable) {
                        try {
                            Closeable closeable = $this$use$iv2;
                            if (closeable != null) {
                                closeable.close();
                            }
                        }
                        catch (Throwable t$iv2) {
                            thrown$iv2 = t$iv2;
                        }
                        throw throwable;
                    }
                    try {
                        Closeable closeable = $this$use$iv2;
                        if (closeable != null) {
                            closeable.close();
                        }
                    }
                    catch (Throwable t$iv3) {
                        if (thrown$iv2 == null) {
                            thrown$iv2 = t$iv3;
                            break block32;
                        }
                        ExceptionsKt.addSuppressed((Throwable)thrown$iv2, (Throwable)t$iv3);
                    }
                }
            }
            Throwable throwable = thrown$iv2;
            if (throwable != null) {
                throw throwable;
            }
            FileMetadata fileMetadata = result$iv2;
            Intrinsics.checkNotNull((Object)fileMetadata);
            result$iv = fileMetadata;
            try {
                Closeable closeable = $this$use$iv;
                if (closeable != null) {
                    closeable.close();
                }
                break block33;
            }
            catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            break block33;
            catch (Throwable t$iv) {
                try {
                    thrown$iv = t$iv;
                }
                catch (Throwable throwable2) {
                    try {
                        Closeable closeable = $this$use$iv;
                        if (closeable != null) {
                            closeable.close();
                        }
                    }
                    catch (Throwable t$iv4) {
                        thrown$iv = t$iv4;
                    }
                    throw throwable2;
                }
                try {
                    Closeable closeable = $this$use$iv;
                    if (closeable != null) {
                        closeable.close();
                    }
                }
                catch (Throwable t$iv5) {
                    if (thrown$iv == null) {
                        thrown$iv = t$iv5;
                        break block33;
                    }
                    ExceptionsKt.addSuppressed((Throwable)thrown$iv, (Throwable)t$iv5);
                }
            }
        }
        Throwable throwable = thrown$iv;
        if (throwable != null) {
            throw throwable;
        }
        FileMetadata fileMetadata = result$iv;
        Intrinsics.checkNotNull(fileMetadata);
        return fileMetadata;
    }

    @Override
    @NotNull
    public FileHandle openReadOnly(@NotNull Path file) {
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        throw new UnsupportedOperationException("not implemented yet!");
    }

    @Override
    @NotNull
    public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        throw new IOException("zip entries are not writable");
    }

    @Override
    @NotNull
    public List<Path> list(@NotNull Path dir) {
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        List<Path> list = this.list(dir, true);
        Intrinsics.checkNotNull(list);
        return list;
    }

    @Override
    @Nullable
    public List<Path> listOrNull(@NotNull Path dir) {
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        return this.list(dir, false);
    }

    private final List<Path> list(Path dir, boolean throwOnFailure) {
        Path canonicalDir = this.canonicalizeInternal(dir);
        ZipEntry zipEntry = this.entries.get(canonicalDir);
        if (zipEntry == null) {
            if (throwOnFailure) {
                throw new IOException("not a directory: " + dir);
            }
            return null;
        }
        ZipEntry entry = zipEntry;
        return CollectionsKt.toList((Iterable)entry.getChildren());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    @NotNull
    public Source source(@NotNull Path file) throws IOException {
        Source source2;
        Throwable thrown$iv;
        BufferedSource result$iv;
        ZipEntry entry;
        block18: {
            Intrinsics.checkNotNullParameter((Object)file, (String)"file");
            Path canonicalPath = this.canonicalizeInternal(file);
            ZipEntry zipEntry = this.entries.get(canonicalPath);
            if (zipEntry == null) {
                throw new FileNotFoundException("no such file: " + file);
            }
            entry = zipEntry;
            Closeable $this$use$iv = this.fileSystem.openReadOnly(this.zipPath);
            boolean $i$f$use = false;
            result$iv = null;
            thrown$iv = null;
            FileHandle fileHandle = (FileHandle)$this$use$iv;
            boolean bl = false;
            result$iv = Okio.buffer(fileHandle.source(entry.getOffset()));
            try {
                Closeable closeable = $this$use$iv;
                if (closeable != null) {
                    closeable.close();
                }
                break block18;
            }
            catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            break block18;
            catch (Throwable t$iv) {
                try {
                    thrown$iv = t$iv;
                }
                catch (Throwable throwable) {
                    try {
                        Closeable closeable = $this$use$iv;
                        if (closeable != null) {
                            closeable.close();
                        }
                    }
                    catch (Throwable t$iv2) {
                        thrown$iv = t$iv2;
                    }
                    throw throwable;
                }
                try {
                    Closeable closeable = $this$use$iv;
                    if (closeable != null) {
                        closeable.close();
                    }
                }
                catch (Throwable t$iv3) {
                    if (thrown$iv == null) {
                        thrown$iv = t$iv3;
                        break block18;
                    }
                    ExceptionsKt.addSuppressed((Throwable)thrown$iv, (Throwable)t$iv3);
                }
            }
        }
        Throwable throwable = thrown$iv;
        if (throwable != null) {
            throw throwable;
        }
        BufferedSource bufferedSource = result$iv;
        Intrinsics.checkNotNull((Object)bufferedSource);
        BufferedSource source3 = bufferedSource;
        ZipFilesKt.skipLocalHeader(source3);
        if (entry.getCompressionMethod() == 0) {
            source2 = new FixedLengthSource(source3, entry.getSize(), true);
        } else {
            InflaterSource inflaterSource = new InflaterSource(new FixedLengthSource(source3, entry.getCompressedSize(), true), new Inflater(true));
            source2 = new FixedLengthSource(inflaterSource, entry.getSize(), false);
        }
        return source2;
    }

    @Override
    @NotNull
    public Sink sink(@NotNull Path file, boolean mustCreate) {
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        throw new IOException("zip file systems are read-only");
    }

    @Override
    @NotNull
    public Sink appendingSink(@NotNull Path file, boolean mustExist) {
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        throw new IOException("zip file systems are read-only");
    }

    @Override
    public void createDirectory(@NotNull Path dir, boolean mustCreate) {
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        throw new IOException("zip file systems are read-only");
    }

    @Override
    public void atomicMove(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        throw new IOException("zip file systems are read-only");
    }

    @Override
    public void delete(@NotNull Path path, boolean mustExist) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        throw new IOException("zip file systems are read-only");
    }

    @Override
    public void createSymlink(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        throw new IOException("zip file systems are read-only");
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lokio/ZipFileSystem$Companion;", "", "()V", "ROOT", "Lokio/Path;", "getROOT", "()Lokio/Path;", "okio"})
    private static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Path getROOT() {
            return ROOT;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

