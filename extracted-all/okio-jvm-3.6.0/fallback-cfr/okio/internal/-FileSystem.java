/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.ExceptionsKt
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.ArrayDeque
 *  kotlin.collections.CollectionsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.ContinuationImpl
 *  kotlin.jvm.JvmName
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequenceScope
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okio.internal;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Source;
import okio.internal.-FileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0080@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\f\u001a\u001c\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0000\u001a\u001c\u0010\u0010\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\nH\u0000\u001a\u001c\u0010\u0013\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0016\u001a\u00020\n*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0019\u001a\u00020\u001a*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0003*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001c"}, d2={"collectRecursively", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "stack", "Lkotlin/collections/ArrayDeque;", "path", "followSymlinks", "", "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "commonCopy", "source", "target", "commonCreateDirectories", "dir", "mustCreate", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonExists", "commonListRecursively", "Lkotlin/sequences/Sequence;", "commonMetadata", "Lokio/FileMetadata;", "symlinkTarget", "okio"})
@JvmName(name="-FileSystem")
@SourceDebugExtension(value={"SMAP\nFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,155:1\n52#2,5:156\n52#2,21:161\n60#2,10:182\n57#2,2:192\n71#2,2:194\n*S KotlinDebug\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n*L\n65#1:156,5\n66#1:161,21\n65#1:182,10\n65#1:192,2\n65#1:194,2\n*E\n"})
public final class -FileSystem {
    @NotNull
    public static final FileMetadata commonMetadata(@NotNull FileSystem $this$commonMetadata, @NotNull Path path) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$commonMetadata, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        FileMetadata fileMetadata = $this$commonMetadata.metadataOrNull(path);
        if (fileMetadata == null) {
            throw new FileNotFoundException("no such file: " + path);
        }
        return fileMetadata;
    }

    public static final boolean commonExists(@NotNull FileSystem $this$commonExists, @NotNull Path path) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$commonExists, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        return $this$commonExists.metadataOrNull(path) != null;
    }

    public static final void commonCreateDirectories(@NotNull FileSystem $this$commonCreateDirectories, @NotNull Path dir, boolean mustCreate) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$commonCreateDirectories, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        ArrayDeque directories = new ArrayDeque();
        for (Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
            directories.addFirst((Object)path);
        }
        if (mustCreate && directories.isEmpty()) {
            throw new IOException(dir + " already exists.");
        }
        for (Path toCreate : directories) {
            $this$commonCreateDirectories.createDirectory(toCreate);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static final void commonCopy(@NotNull FileSystem $this$commonCopy, @NotNull Path source2, @NotNull Path target) throws IOException {
        Throwable thrown$iv;
        Long result$iv;
        block31: {
            Throwable thrown$iv2;
            Long result$iv2;
            Closeable $this$use$iv;
            block30: {
                Intrinsics.checkNotNullParameter((Object)$this$commonCopy, (String)"<this>");
                Intrinsics.checkNotNullParameter((Object)source2, (String)"source");
                Intrinsics.checkNotNullParameter((Object)target, (String)"target");
                $this$use$iv = $this$commonCopy.source(source2);
                boolean $i$f$use = false;
                result$iv = null;
                thrown$iv = null;
                Source bytesIn = (Source)$this$use$iv;
                boolean bl = false;
                Closeable $this$use$iv2 = Okio.buffer($this$commonCopy.sink(target));
                boolean $i$f$use2 = false;
                result$iv2 = null;
                thrown$iv2 = null;
                BufferedSink bytesOut = (BufferedSink)$this$use$iv2;
                boolean bl2 = false;
                result$iv2 = bytesOut.writeAll(bytesIn);
                try {
                    Closeable closeable = $this$use$iv2;
                    if (closeable != null) {
                        closeable.close();
                    }
                    break block30;
                }
                catch (Throwable t$iv) {
                    thrown$iv2 = t$iv;
                }
                break block30;
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
                            break block30;
                        }
                        ExceptionsKt.addSuppressed((Throwable)thrown$iv2, (Throwable)t$iv3);
                    }
                }
            }
            Throwable throwable = thrown$iv2;
            if (throwable != null) {
                throw throwable;
            }
            Long l = result$iv2;
            Intrinsics.checkNotNull((Object)l);
            result$iv = ((Number)l).longValue();
            try {
                Closeable closeable = $this$use$iv;
                if (closeable != null) {
                    closeable.close();
                }
                break block31;
            }
            catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            break block31;
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
                        break block31;
                    }
                    ExceptionsKt.addSuppressed((Throwable)thrown$iv, (Throwable)t$iv5);
                }
            }
        }
        Throwable throwable = thrown$iv;
        if (throwable != null) {
            throw throwable;
        }
        Intrinsics.checkNotNull(result$iv);
    }

    public static final void commonDeleteRecursively(@NotNull FileSystem $this$commonDeleteRecursively, @NotNull Path fileOrDirectory, boolean mustExist) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$commonDeleteRecursively, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)fileOrDirectory, (String)"fileOrDirectory");
        Sequence sequence2 = SequencesKt.sequence((Function2)((Function2)new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>($this$commonDeleteRecursively, fileOrDirectory, null){
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ FileSystem $this_commonDeleteRecursively;
            final /* synthetic */ Path $fileOrDirectory;
            {
                this.$this_commonDeleteRecursively = $receiver;
                this.$fileOrDirectory = $fileOrDirectory;
                super(2, $completion);
            }

            /*
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object object) {
                Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        void var2_3;
                        ResultKt.throwOnFailure((Object)object);
                        SequenceScope $this$sequence = (SequenceScope)this.L$0;
                        this.label = 1;
                        Object object3 = -FileSystem.collectRecursively((SequenceScope<? super Path>)var2_3, this.$this_commonDeleteRecursively, (ArrayDeque<Path>)new ArrayDeque(), this.$fileOrDirectory, false, true, (Continuation<? super Unit>)((Continuation)this));
                        if (object3 != object2) return Unit.INSTANCE;
                        return object2;
                    }
                    case 1: {
                        void $result;
                        ResultKt.throwOnFailure((Object)$result);
                        Object object3 = $result;
                        return Unit.INSTANCE;
                    }
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)function2;
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }));
        Iterator iterator = sequence2.iterator();
        while (iterator.hasNext()) {
            Path toDelete = (Path)iterator.next();
            $this$commonDeleteRecursively.delete(toDelete, mustExist && !iterator.hasNext());
        }
    }

    @NotNull
    public static final Sequence<Path> commonListRecursively(@NotNull FileSystem $this$commonListRecursively, @NotNull Path dir, boolean followSymlinks) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$commonListRecursively, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)dir, (String)"dir");
        return SequencesKt.sequence((Function2)((Function2)new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(dir, $this$commonListRecursively, followSymlinks, null){
            Object L$1;
            Object L$2;
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ Path $dir;
            final /* synthetic */ FileSystem $this_commonListRecursively;
            final /* synthetic */ boolean $followSymlinks;
            {
                this.$dir = $dir;
                this.$this_commonListRecursively = $receiver;
                this.$followSymlinks = $followSymlinks;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object var1_1) {
                var6_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure((Object)var1_1);
                        $this$sequence = (SequenceScope)this.L$0;
                        stack = new ArrayDeque();
                        stack.addLast((Object)this.$dir);
                        var4_5 = this.$this_commonListRecursively.list(this.$dir).iterator();
lbl9:
                        // 3 sources

                        while (var4_5.hasNext()) {
                            child = var4_5.next();
                            this.L$0 = $this$sequence;
                            this.L$1 = stack;
                            this.L$2 = var4_5;
                            this.label = 1;
                            v0 = -FileSystem.collectRecursively((SequenceScope<? super Path>)$this$sequence, this.$this_commonListRecursively, (ArrayDeque<Path>)stack, child, this.$followSymlinks, false, (Continuation<? super Unit>)((Continuation)this));
                            if (v0 != var6_2) continue;
                            return var6_2;
                        }
                        break;
                    }
                    case 1: {
                        var4_5 = (Iterator<Path>)this.L$2;
                        stack = (ArrayDeque)this.L$1;
                        $this$sequence = (SequenceScope)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v0 = $result;
                        ** GOTO lbl9
                    }
                }
                return Unit.INSTANCE;
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)function2;
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static final Object collectRecursively(@NotNull SequenceScope<? super Path> var0, @NotNull FileSystem var1_1, @NotNull ArrayDeque<Path> var2_2, @NotNull Path var3_3, boolean var4_4, boolean var5_5, @NotNull Continuation<? super Unit> var6_6) {
        if (!(var6_6 instanceof collectRecursively.1)) ** GOTO lbl-1000
        var13_7 = var6_6;
        if ((var13_7.label & -2147483648) != 0) {
            var13_7.label -= -2147483648;
        } else lbl-1000:
        // 2 sources

        {
            $continuation = new ContinuationImpl(var6_6){
                Object L$0;
                Object L$1;
                Object L$2;
                Object L$3;
                Object L$4;
                boolean Z$0;
                boolean Z$1;
                /* synthetic */ Object result;
                int label;

                @Nullable
                public final Object invokeSuspend(@NotNull Object $result) {
                    this.result = $result;
                    this.label |= Integer.MIN_VALUE;
                    return -FileSystem.collectRecursively(null, null, null, null, false, false, (Continuation<? super Unit>)((Continuation)this));
                }
            };
        }
        $result = $continuation.result;
        var14_9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch ($continuation.label) {
            case 0: {
                ResultKt.throwOnFailure((Object)$result);
                if (!postorder) {
                    $continuation.L$0 = $this$collectRecursively;
                    $continuation.L$1 = fileSystem;
                    $continuation.L$2 = stack;
                    $continuation.L$3 = path;
                    $continuation.Z$0 = followSymlinks;
                    $continuation.Z$1 = postorder;
                    $continuation.label = 1;
                    v0 = $this$collectRecursively.yield((Object)path, (Continuation)$continuation);
                    if (v0 == var14_9) {
                        return var14_9;
                    }
                }
                ** GOTO lbl33
            }
            case 1: {
                postorder = $continuation.Z$1;
                followSymlinks = $continuation.Z$0;
                path = (Path)$continuation.L$3;
                stack = (ArrayDeque)$continuation.L$2;
                fileSystem = (FileSystem)$continuation.L$1;
                $this$collectRecursively = (SequenceScope)$continuation.L$0;
                ResultKt.throwOnFailure((Object)$result);
                v0 = $result;
lbl33:
                // 2 sources

                if ((v1 = fileSystem.listOrNull(path)) == null) {
                    v1 = CollectionsKt.emptyList();
                }
                if (!(((Collection)(children = v1)).isEmpty() == false)) ** GOTO lbl86
                symlinkPath = path;
                symlinkCount = 0;
                while (true) {
                    if (followSymlinks && stack.contains((Object)symlinkPath)) {
                        throw new IOException("symlink cycle at " + path);
                    }
                    if (-FileSystem.symlinkTarget(fileSystem, symlinkPath) != null) ** GOTO lbl46
                    if (!followSymlinks) {
                        break;
                    }
                    ** GOTO lbl49
lbl46:
                    // 1 sources

                    ++symlinkCount;
                }
                if (symlinkCount != 0) ** GOTO lbl86
lbl49:
                // 2 sources

                stack.addLast((Object)symlinkPath);
                try {
                    var10_13 = children.iterator();
                }
                catch (Throwable var10_14) {
                    stack.removeLast();
                    throw var10_14;
                }
lbl58:
                // 3 sources

                while (var10_13.hasNext()) {
                    child = (Path)var10_13.next();
                    $continuation.L$0 = $this$collectRecursively;
                    $continuation.L$1 = fileSystem;
                    $continuation.L$2 = stack;
                    $continuation.L$3 = path;
                    $continuation.L$4 = var10_13;
                    $continuation.Z$0 = followSymlinks;
                    $continuation.Z$1 = postorder;
                    $continuation.label = 2;
                    v2 = -FileSystem.collectRecursively((SequenceScope<? super Path>)$this$collectRecursively, fileSystem, (ArrayDeque<Path>)stack, child, followSymlinks != false, postorder != false, (Continuation<? super Unit>)$continuation);
                    if (v2 != var14_9) continue;
                    return var14_9;
                }
                ** GOTO lbl84
            }
            case 2: {
                postorder = $continuation.Z$1;
                followSymlinks = $continuation.Z$0;
                var10_13 = (Iterator<E>)$continuation.L$4;
                path = (Path)$continuation.L$3;
                stack = (ArrayDeque)$continuation.L$2;
                fileSystem = (FileSystem)$continuation.L$1;
                $this$collectRecursively = (SequenceScope)$continuation.L$0;
                {
                    ResultKt.throwOnFailure((Object)$result);
                    v2 = $result;
                    ** GOTO lbl58
                }
            }
lbl84:
            // 1 sources

            stack.removeLast();
lbl86:
            // 3 sources

            if (!postorder) {
                return Unit.INSTANCE;
            }
            $continuation.L$0 = null;
            $continuation.L$1 = null;
            $continuation.L$2 = null;
            $continuation.L$3 = null;
            $continuation.L$4 = null;
            $continuation.label = 3;
            v3 = $this$collectRecursively.yield((Object)path, (Continuation)$continuation);
            if (v3 != var14_9) return Unit.INSTANCE;
            return var14_9;
            case 3: {
                ResultKt.throwOnFailure((Object)$result);
                v3 = $result;
                return Unit.INSTANCE;
            }
        }
        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
    }

    @Nullable
    public static final Path symlinkTarget(@NotNull FileSystem $this$symlinkTarget, @NotNull Path path) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$symlinkTarget, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        Path path2 = $this$symlinkTarget.metadata(path).getSymlinkTarget();
        if (path2 == null) {
            return null;
        }
        Path target = path2;
        Path path3 = path.parent();
        Intrinsics.checkNotNull((Object)path3);
        return path3.resolve(target);
    }
}

