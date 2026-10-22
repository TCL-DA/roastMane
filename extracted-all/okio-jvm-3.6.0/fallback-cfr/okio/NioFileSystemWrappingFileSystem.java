/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.path.PathsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.path.PathsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.FileHandle;
import okio.FileMetadata;
import okio.NioFileSystemFileHandle;
import okio.NioSystemFileSystem;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0010\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0018\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\nH\u0016J\u0018\u0010\u0014\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0012\u001a\u00020\bH\u0016J \u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00172\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\nH\u0002J\u0018\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00172\u0006\u0010\u0012\u001a\u00020\bH\u0016J\u0012\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0007\u001a\u00020\bH\u0016J \u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\nH\u0016J\u0010\u0010\r\u001a\u00020 2\u0006\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010!\u001a\u00020\"H\u0016J\f\u0010#\u001a\u00020$*\u00020\bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2={"Lokio/NioFileSystemWrappingFileSystem;", "Lokio/NioSystemFileSystem;", "nioFileSystem", "Ljava/nio/file/FileSystem;", "(Ljava/nio/file/FileSystem;)V", "appendingSink", "Lokio/Sink;", "file", "Lokio/Path;", "mustExist", "", "atomicMove", "", "source", "target", "canonicalize", "path", "createDirectory", "dir", "mustCreate", "createSymlink", "delete", "list", "", "throwOnFailure", "listOrNull", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "openReadWrite", "sink", "Lokio/Source;", "toString", "", "resolve", "Ljava/nio/file/Path;", "okio"})
@SourceDebugExtension(value={"SMAP\nNioFileSystemWrappingFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,192:1\n1620#2,3:193\n1#3:196\n37#4,2:197\n37#4,2:199\n37#4,2:201\n*S KotlinDebug\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n*L\n77#1:193,3\n104#1:197,2\n125#1:199,2\n138#1:201,2\n*E\n"})
public final class NioFileSystemWrappingFileSystem
extends NioSystemFileSystem {
    @NotNull
    private final FileSystem nioFileSystem;

    public NioFileSystemWrappingFileSystem(@NotNull FileSystem nioFileSystem) {
        Intrinsics.checkNotNullParameter((Object)nioFileSystem, (String)"nioFileSystem");
        this.nioFileSystem = nioFileSystem;
    }

    private final java.nio.file.Path resolve(Path $this$resolve) {
        java.nio.file.Path path = this.nioFileSystem.getPath($this$resolve.toString(), new String[0]);
        Intrinsics.checkNotNullExpressionValue((Object)path, (String)"getPath(...)");
        return path;
    }

    @Override
    @NotNull
    public Path canonicalize(@NotNull Path path) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        try {
            java.nio.file.Path path2 = this.resolve(path).toRealPath(new LinkOption[0]);
            Intrinsics.checkNotNullExpressionValue((Object)path2, (String)"toRealPath(...)");
            return Path.Companion.get$default(Path.Companion, path2, false, 1, null);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("no such file: " + path);
        }
    }

    @Override
    @Nullable
    public FileMetadata metadataOrNull(@NotNull Path path) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        return this.metadataOrNull(this.resolve(path));
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

    /*
     * WARNING - void declaration
     */
    private final List<Path> list(Path dir, boolean throwOnFailure) {
        void destination$iv;
        void $this$mapTo$iv;
        List list;
        java.nio.file.Path nioDir = this.resolve(dir);
        try {
            list = PathsKt.listDirectoryEntries$default((java.nio.file.Path)nioDir, null, (int)1, null);
        }
        catch (Exception e) {
            if (throwOnFailure) {
                LinkOption[] linkOptionArray = new LinkOption[]{};
                if (!Files.exists(nioDir, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) {
                    throw new FileNotFoundException("no such file: " + dir);
                }
                throw new IOException("failed to list " + dir);
            }
            return null;
        }
        List entries = list;
        Iterable e = entries;
        Collection collection = new ArrayList();
        boolean $i$f$mapTo = false;
        for (Object item$iv : $this$mapTo$iv) {
            void entry;
            java.nio.file.Path path = (java.nio.file.Path)item$iv;
            void var13_14 = destination$iv;
            boolean bl = false;
            var13_14.add(Path.Companion.get$default(Path.Companion, (java.nio.file.Path)entry, false, 1, null));
        }
        List result = (List)destination$iv;
        CollectionsKt.sort((List)result);
        return result;
    }

    @Override
    @NotNull
    public FileHandle openReadOnly(@NotNull Path file) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        try {
            object = new OpenOption[]{StandardOpenOption.READ};
            object = FileChannel.open(this.resolve(file), object);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("no such file: " + file);
        }
        Object channel = object;
        Intrinsics.checkNotNull((Object)channel);
        return new NioFileSystemFileHandle(false, (FileChannel)channel);
    }

    @Override
    @NotNull
    public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
        Object object;
        List $i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222;
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        if (!(!mustCreate || !mustExist)) {
            boolean $i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222 = false;
            String $i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222 = "Cannot require mustCreate and mustExist at the same time.";
            throw new IllegalArgumentException($i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222.toString());
        }
        List $this$openReadWrite_u24lambda_u242 = $i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222 = CollectionsKt.createListBuilder();
        boolean bl = false;
        $this$openReadWrite_u24lambda_u242.add(StandardOpenOption.READ);
        $this$openReadWrite_u24lambda_u242.add(StandardOpenOption.WRITE);
        if (mustCreate) {
            $this$openReadWrite_u24lambda_u242.add(StandardOpenOption.CREATE_NEW);
        } else if (!mustExist) {
            $this$openReadWrite_u24lambda_u242.add(StandardOpenOption.CREATE);
        }
        List openOptions = CollectionsKt.build((List)$i$a$-require-NioFileSystemWrappingFileSystem$openReadWrite$222);
        try {
            Collection $this$toTypedArray$iv = openOptions;
            boolean $i$f$toTypedArray = false;
            Collection thisCollection$iv = $this$toTypedArray$iv;
            object = thisCollection$iv.toArray(new StandardOpenOption[0]);
            object = FileChannel.open(this.resolve(file), Arrays.copyOf(object, ((StandardOpenOption[])object).length));
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("no such file: " + file);
        }
        Object channel = object;
        Intrinsics.checkNotNull((Object)channel);
        return new NioFileSystemFileHandle(true, (FileChannel)channel);
    }

    @Override
    @NotNull
    public Source source(@NotNull Path file) {
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        try {
            OpenOption[] openOptionArray = new OpenOption[]{};
            InputStream inputStream2 = Files.newInputStream(this.resolve(file), Arrays.copyOf(openOptionArray, openOptionArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)inputStream2, (String)"newInputStream(this, *options)");
            return Okio.source(inputStream2);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override
    @NotNull
    public Sink sink(@NotNull Path file, boolean mustCreate) {
        Iterable iterable;
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        List $this$sink_u24lambda_u243 = iterable = CollectionsKt.createListBuilder();
        boolean bl = false;
        if (mustCreate) {
            $this$sink_u24lambda_u243.add(StandardOpenOption.CREATE_NEW);
        }
        List openOptions = CollectionsKt.build((List)iterable);
        try {
            iterable = this.resolve(file);
            Collection $this$toTypedArray$iv = openOptions;
            boolean $i$f$toTypedArray = false;
            Collection thisCollection$iv = $this$toTypedArray$iv;
            OpenOption[] openOptionArray = thisCollection$iv.toArray(new StandardOpenOption[0]);
            openOptionArray = Arrays.copyOf(openOptionArray, openOptionArray.length);
            OutputStream outputStream2 = Files.newOutputStream((java.nio.file.Path)iterable, Arrays.copyOf(openOptionArray, openOptionArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)outputStream2, (String)"newOutputStream(this, *options)");
            return Okio.sink(outputStream2);
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException("no such file: " + file);
        }
    }

    @Override
    @NotNull
    public Sink appendingSink(@NotNull Path file, boolean mustExist) {
        Iterable iterable;
        Intrinsics.checkNotNullParameter((Object)file, (String)"file");
        List $this$appendingSink_u24lambda_u244 = iterable = CollectionsKt.createListBuilder();
        boolean bl = false;
        $this$appendingSink_u24lambda_u244.add(StandardOpenOption.APPEND);
        if (!mustExist) {
            $this$appendingSink_u24lambda_u244.add(StandardOpenOption.CREATE);
        }
        List openOptions = CollectionsKt.build((List)iterable);
        iterable = this.resolve(file);
        Collection $this$toTypedArray$iv = openOptions;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = $this$toTypedArray$iv;
        OpenOption[] openOptionArray = thisCollection$iv.toArray(new StandardOpenOption[0]);
        openOptionArray = Arrays.copyOf(openOptionArray, openOptionArray.length);
        OutputStream outputStream2 = Files.newOutputStream((java.nio.file.Path)iterable, Arrays.copyOf(openOptionArray, openOptionArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)outputStream2, (String)"newOutputStream(this, *options)");
        return Okio.sink(outputStream2);
    }

    @Override
    public void createDirectory(@NotNull Path dir, boolean mustCreate) {
        boolean alreadyExist;
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        FileMetadata fileMetadata = this.metadataOrNull(dir);
        boolean bl = fileMetadata != null ? fileMetadata.isDirectory() : (alreadyExist = false);
        if (alreadyExist && mustCreate) {
            throw new IOException(dir + " already exists.");
        }
        try {
            FileAttribute[] fileAttributeArray = new FileAttribute[]{};
            Intrinsics.checkNotNullExpressionValue((Object)Files.createDirectory(this.resolve(dir), Arrays.copyOf(fileAttributeArray, fileAttributeArray.length)), (String)"createDirectory(this, *attributes)");
        }
        catch (IOException e) {
            if (alreadyExist) {
                return;
            }
            throw new IOException("failed to create directory: " + dir, e);
        }
    }

    @Override
    public void atomicMove(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        try {
            java.nio.file.Path path = this.resolve(source2);
            java.nio.file.Path path2 = this.resolve(target);
            CopyOption[] copyOptionArray = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING};
            Intrinsics.checkNotNullExpressionValue((Object)Files.move(path, path2, Arrays.copyOf(copyOptionArray, copyOptionArray.length)), (String)"move(this, target, *options)");
        }
        catch (NoSuchFileException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        catch (UnsupportedOperationException e) {
            throw new IOException("atomic move not supported");
        }
    }

    @Override
    public void delete(@NotNull Path path, boolean mustExist) {
        block5: {
            Intrinsics.checkNotNullParameter((Object)path, (String)"path");
            if (Thread.interrupted()) {
                throw new InterruptedIOException("interrupted");
            }
            java.nio.file.Path nioPath = this.resolve(path);
            try {
                Files.delete(nioPath);
            }
            catch (NoSuchFileException e) {
                if (mustExist) {
                    throw new FileNotFoundException("no such file: " + path);
                }
            }
            catch (IOException e) {
                LinkOption[] linkOptionArray = new LinkOption[]{};
                if (!Files.exists(nioPath, Arrays.copyOf(linkOptionArray, linkOptionArray.length))) break block5;
                throw new IOException("failed to delete " + path);
            }
        }
    }

    @Override
    public void createSymlink(@NotNull Path source2, @NotNull Path target) {
        Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        FileAttribute[] fileAttributeArray = new FileAttribute[]{};
        Intrinsics.checkNotNullExpressionValue((Object)Files.createSymbolicLink(this.resolve(source2), this.resolve(target), Arrays.copyOf(fileAttributeArray, fileAttributeArray.length)), (String)"createSymbolicLink(this, target, *attributes)");
    }

    @Override
    @NotNull
    public String toString() {
        String string = Reflection.getOrCreateKotlinClass(this.nioFileSystem.getClass()).getSimpleName();
        Intrinsics.checkNotNull((Object)string);
        return string;
    }
}

