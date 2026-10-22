@file:JvmName(name = "-FileSystem")

@file:SourceDebugExtension(["SMAP\nFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,155:1\n52#2,5:156\n52#2,21:161\n60#2,10:182\n57#2,2:192\n71#2,2:194\n*S KotlinDebug\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n*L\n65#1:156,5\n66#1:161,21\n65#1:182,10\n65#1:192,2\n65#1:194,2\n*E\n"])

package okio.internal

import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@Throws(java/io/IOException::class)
internal fun FileSystem.commonMetadata(path: Path): FileMetadata {
   var var10000: FileMetadata = `$this$commonMetadata`.metadataOrNull(path);
   if (var10000 == null) {
      throw new FileNotFoundException("no such file: " + path);
   } else {
      return var10000;
   }
}

@Throws(java/io/IOException::class)
internal fun FileSystem.commonExists(path: Path): Boolean {
   return `$this$commonExists`.metadataOrNull(path) != null;
}

@Throws(java/io/IOException::class)
internal fun FileSystem.commonCreateDirectories(dir: Path, mustCreate: Boolean) {
   var directories: ArrayDeque = new ArrayDeque();

   for (Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
      directories.addFirst(path);
   }

   if (mustCreate && directories.isEmpty()) {
      throw new IOException(dir + " already exists.");
   } else {
      for (Path toCreate : directories) {
         `$this$commonCreateDirectories`.createDirectory(toCreate);
      }
   }
}

@Throws(java/io/IOException::class)
internal fun FileSystem.commonCopy(source: Path, target: Path) {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
   //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
   //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
   //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
   //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
   //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
   //   at org.jetbrains.java.decompiler.util.collections.ListStack.pop(ListStack.java:31)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processBlock(ExprProcessor.java:445)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:134)
   //
   // Bytecode:
   // 000: aload 0
   // 001: ldc "<this>"
   // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 006: aload 1
   // 007: ldc "source"
   // 009: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 00c: aload 2
   // 00d: ldc "target"
   // 00f: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 012: aload 0
   // 013: aload 1
   // 014: invokevirtual okio/FileSystem.source (Lokio/Path;)Lokio/Source;
   // 017: checkcast java/io/Closeable
   // 01a: astore 3
   // 01b: bipush 0
   // 01c: istore 4
   // 01e: aconst_null
   // 01f: astore 5
   // 021: aconst_null
   // 022: astore 6
   // 024: nop
   // 025: aload 3
   // 026: checkcast okio/Source
   // 029: astore 7
   // 02b: bipush 0
   // 02c: istore 8
   // 02e: aload 0
   // 02f: aload 2
   // 030: invokevirtual okio/FileSystem.sink (Lokio/Path;)Lokio/Sink;
   // 033: invokestatic okio/Okio.buffer (Lokio/Sink;)Lokio/BufferedSink;
   // 036: checkcast java/io/Closeable
   // 039: astore 9
   // 03b: bipush 0
   // 03c: istore 10
   // 03e: aconst_null
   // 03f: astore 11
   // 041: aconst_null
   // 042: astore 12
   // 044: nop
   // 045: aload 9
   // 047: checkcast okio/BufferedSink
   // 04a: astore 13
   // 04c: bipush 0
   // 04d: istore 14
   // 04f: aload 13
   // 051: aload 7
   // 053: invokeinterface okio/BufferedSink.writeAll (Lokio/Source;)J 2
   // 058: invokestatic java/lang/Long.valueOf (J)Ljava/lang/Long;
   // 05b: astore 11
   // 05d: nop
   // 05e: aload 9
   // 060: dup
   // 061: ifnull 06c
   // 064: invokeinterface java/io/Closeable.close ()V 1
   // 069: goto 079
   // 06c: pop
   // 06d: goto 079
   // 070: astore 13
   // 072: aload 13
   // 074: astore 12
   // 076: goto 079
   // 079: goto 0ce
   // 07c: astore 13
   // 07e: aload 13
   // 080: astore 12
   // 082: nop
   // 083: aload 9
   // 085: dup
   // 086: ifnull 091
   // 089: invokeinterface java/io/Closeable.close ()V 1
   // 08e: goto 0aa
   // 091: pop
   // 092: goto 0aa
   // 095: astore 13
   // 097: aload 12
   // 099: ifnonnull 0a3
   // 09c: aload 13
   // 09e: astore 12
   // 0a0: goto 0aa
   // 0a3: aload 12
   // 0a5: aload 13
   // 0a7: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
   // 0aa: goto 0ce
   // 0ad: astore 13
   // 0af: nop
   // 0b0: aload 9
   // 0b2: dup
   // 0b3: ifnull 0be
   // 0b6: invokeinterface java/io/Closeable.close ()V 1
   // 0bb: goto 0cb
   // 0be: pop
   // 0bf: goto 0cb
   // 0c2: astore 14
   // 0c4: aload 14
   // 0c6: astore 12
   // 0c8: goto 0cb
   // 0cb: aload 13
   // 0cd: athrow
   // 0ce: aload 12
   // 0d0: dup
   // 0d1: ifnull 0d5
   // 0d4: athrow
   // 0d5: pop
   // 0d6: aload 11
   // 0d8: dup
   // 0d9: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
   // 0dc: checkcast java/lang/Number
   // 0df: invokevirtual java/lang/Number.longValue ()J
   // 0e2: nop
   // 0e3: invokestatic java/lang/Long.valueOf (J)Ljava/lang/Long;
   // 0e6: astore 5
   // 0e8: nop
   // 0e9: aload 3
   // 0ea: dup
   // 0eb: ifnull 0f6
   // 0ee: invokeinterface java/io/Closeable.close ()V 1
   // 0f3: goto 103
   // 0f6: pop
   // 0f7: goto 103
   // 0fa: astore 7
   // 0fc: aload 7
   // 0fe: astore 6
   // 100: goto 103
   // 103: goto 156
   // 106: astore 7
   // 108: aload 7
   // 10a: astore 6
   // 10c: nop
   // 10d: aload 3
   // 10e: dup
   // 10f: ifnull 11a
   // 112: invokeinterface java/io/Closeable.close ()V 1
   // 117: goto 133
   // 11a: pop
   // 11b: goto 133
   // 11e: astore 7
   // 120: aload 6
   // 122: ifnonnull 12c
   // 125: aload 7
   // 127: astore 6
   // 129: goto 133
   // 12c: aload 6
   // 12e: aload 7
   // 130: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
   // 133: goto 156
   // 136: astore 7
   // 138: nop
   // 139: aload 3
   // 13a: dup
   // 13b: ifnull 146
   // 13e: invokeinterface java/io/Closeable.close ()V 1
   // 143: goto 153
   // 146: pop
   // 147: goto 153
   // 14a: astore 8
   // 14c: aload 8
   // 14e: astore 6
   // 150: goto 153
   // 153: aload 7
   // 155: athrow
   // 156: aload 6
   // 158: dup
   // 159: ifnull 15d
   // 15c: athrow
   // 15d: pop
   // 15e: aload 5
   // 160: dup
   // 161: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
   // 164: pop
   // 165: return
}

@Throws(java/io/IOException::class)
internal fun FileSystem.commonDeleteRecursively(fileOrDirectory: Path, mustExist: Boolean) {
   var iterator: java.util.Iterator = SequencesKt.sequence(
         (new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(`$this$commonDeleteRecursively`, fileOrDirectory, null) {
            int label;

            {
               super(2, `$completion`);
               this.$this_commonDeleteRecursively = `$receiver`;
               this.$fileOrDirectory = `$fileOrDirectory`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               var var3: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     var `$this$sequence`: SequenceScope = this.L$0 as SequenceScope;
                     var var10001: FileSystem = this.$this_commonDeleteRecursively;
                     var var10002: ArrayDeque = new ArrayDeque();
                     var var10003: Path = this.$fileOrDirectory;
                     var var10006: Continuation = this as Continuation;
                     this.label = 1;
                     if (-FileSystem.collectRecursively(`$this$sequence`, var10001, var10002, var10003, false, true, var10006) === var3) {
                        return var3;
                     }
                     break;
                  case 1:
                     ResultKt.throwOnFailure(`$result`);
                     break;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               return Unit.INSTANCE;
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
               var var3: Function2 = new <anonymous constructor>(this.$this_commonDeleteRecursively, this.$fileOrDirectory, `$completion`);
               var3.L$0 = value;
               return var3 as Continuation<Unit>;
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2
      )
      .iterator();

   while (iterator.hasNext()) {
      `$this$commonDeleteRecursively`.delete(iterator.next() as Path, mustExist && !iterator.hasNext());
   }
}

@Throws(java/io/IOException::class)
internal fun FileSystem.commonListRecursively(dir: Path, followSymlinks: Boolean): Sequence<Path> {
   return SequencesKt.sequence(
      (new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(dir, `$this$commonListRecursively`, followSymlinks, null) {
         Object L$1;
         Object L$2;
         int label;

         {
            super(2, `$completion`);
            this.$dir = `$dir`;
            this.$this_commonListRecursively = `$receiver`;
            this.$followSymlinks = `$followSymlinks`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         public final Object invokeSuspend(@NotNull Object $result) {
            var var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var stack: ArrayDeque;
            var var4: java.util.Iterator;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  stack = new ArrayDeque();
                  stack.addLast(this.$dir);
                  var4 = this.$this_commonListRecursively.list(this.$dir).iterator();
                  break;
               case 1:
                  var4 = this.L$2 as java.util.Iterator;
                  stack = this.L$1 as ArrayDeque;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while (var4.hasNext()) {
               var child: Path = var4.next() as Path;
               var var10001: FileSystem = this.$this_commonListRecursively;
               var var10004: Boolean = this.$followSymlinks;
               var var10006: Continuation = this as Continuation;
               this.L$0 = `$this$sequence`;
               this.L$1 = stack;
               this.L$2 = var4;
               this.label = 1;
               if (-FileSystem.collectRecursively(`$this$sequence`, var10001, stack, child, var10004, false, var10006) === var6) {
                  return var6;
               }
            }

            return Unit.INSTANCE;
         }

         @NotNull
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$dir, this.$this_commonListRecursively, this.$followSymlinks, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2
   );
}

internal suspend fun SequenceScope<Path>.collectRecursively(
   fileSystem: FileSystem,
   stack: ArrayDeque<Path>,
   path: Path,
   followSymlinks: Boolean,
   postorder: Boolean
) {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
   //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
   //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
   //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
   //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
   //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
   //   at org.jetbrains.java.decompiler.util.collections.ListStack.pop(ListStack.java:31)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processBlock(ExprProcessor.java:573)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:134)
   //
   // Bytecode:
   // 000: aload 6
   // 002: instanceof okio/internal/-FileSystem$collectRecursively$1
   // 005: ifeq 029
   // 008: aload 6
   // 00a: checkcast okio/internal/-FileSystem$collectRecursively$1
   // 00d: astore 13
   // 00f: aload 13
   // 011: getfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 014: ldc -2147483648
   // 016: iand
   // 017: ifeq 029
   // 01a: aload 13
   // 01c: dup
   // 01d: getfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 020: ldc -2147483648
   // 022: isub
   // 023: putfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 026: goto 034
   // 029: new okio/internal/-FileSystem$collectRecursively$1
   // 02c: dup
   // 02d: aload 6
   // 02f: invokespecial okio/internal/-FileSystem$collectRecursively$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 032: astore 13
   // 034: aload 13
   // 036: getfield okio/internal/-FileSystem$collectRecursively$1.result Ljava/lang/Object;
   // 039: astore 12
   // 03b: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03e: astore 14
   // 040: aload 13
   // 042: getfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 045: tableswitch 567 0 3 31 101 404 551
   // 064: aload 12
   // 066: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 069: iload 5
   // 06b: ifne 0e4
   // 06e: aload 0
   // 06f: aload 3
   // 070: aload 13
   // 072: aload 13
   // 074: aload 0
   // 075: putfield okio/internal/-FileSystem$collectRecursively$1.L$0 Ljava/lang/Object;
   // 078: aload 13
   // 07a: aload 1
   // 07b: putfield okio/internal/-FileSystem$collectRecursively$1.L$1 Ljava/lang/Object;
   // 07e: aload 13
   // 080: aload 2
   // 081: putfield okio/internal/-FileSystem$collectRecursively$1.L$2 Ljava/lang/Object;
   // 084: aload 13
   // 086: aload 3
   // 087: putfield okio/internal/-FileSystem$collectRecursively$1.L$3 Ljava/lang/Object;
   // 08a: aload 13
   // 08c: iload 4
   // 08e: putfield okio/internal/-FileSystem$collectRecursively$1.Z$0 Z
   // 091: aload 13
   // 093: iload 5
   // 095: putfield okio/internal/-FileSystem$collectRecursively$1.Z$1 Z
   // 098: aload 13
   // 09a: bipush 1
   // 09b: putfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 09e: invokevirtual kotlin/sequences/SequenceScope.yield (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 0a1: dup
   // 0a2: aload 14
   // 0a4: if_acmpne 0e3
   // 0a7: aload 14
   // 0a9: areturn
   // 0aa: aload 13
   // 0ac: getfield okio/internal/-FileSystem$collectRecursively$1.Z$1 Z
   // 0af: istore 5
   // 0b1: aload 13
   // 0b3: getfield okio/internal/-FileSystem$collectRecursively$1.Z$0 Z
   // 0b6: istore 4
   // 0b8: aload 13
   // 0ba: getfield okio/internal/-FileSystem$collectRecursively$1.L$3 Ljava/lang/Object;
   // 0bd: checkcast okio/Path
   // 0c0: astore 3
   // 0c1: aload 13
   // 0c3: getfield okio/internal/-FileSystem$collectRecursively$1.L$2 Ljava/lang/Object;
   // 0c6: checkcast kotlin/collections/ArrayDeque
   // 0c9: astore 2
   // 0ca: aload 13
   // 0cc: getfield okio/internal/-FileSystem$collectRecursively$1.L$1 Ljava/lang/Object;
   // 0cf: checkcast okio/FileSystem
   // 0d2: astore 1
   // 0d3: aload 13
   // 0d5: getfield okio/internal/-FileSystem$collectRecursively$1.L$0 Ljava/lang/Object;
   // 0d8: checkcast kotlin/sequences/SequenceScope
   // 0db: astore 0
   // 0dc: aload 12
   // 0de: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 0e1: aload 12
   // 0e3: pop
   // 0e4: aload 1
   // 0e5: aload 3
   // 0e6: invokevirtual okio/FileSystem.listOrNull (Lokio/Path;)Ljava/util/List;
   // 0e9: dup
   // 0ea: ifnonnull 0f1
   // 0ed: pop
   // 0ee: invokestatic kotlin/collections/CollectionsKt.emptyList ()Ljava/util/List;
   // 0f1: astore 7
   // 0f3: aload 7
   // 0f5: checkcast java/util/Collection
   // 0f8: invokeinterface java/util/Collection.isEmpty ()Z 1
   // 0fd: ifne 104
   // 100: bipush 1
   // 101: goto 105
   // 104: bipush 0
   // 105: ifeq 233
   // 108: aload 3
   // 109: astore 8
   // 10b: bipush 0
   // 10c: istore 9
   // 10e: nop
   // 10f: iload 4
   // 111: ifeq 139
   // 114: aload 2
   // 115: aload 8
   // 117: invokevirtual kotlin/collections/ArrayDeque.contains (Ljava/lang/Object;)Z
   // 11a: ifeq 139
   // 11d: new java/io/IOException
   // 120: dup
   // 121: new java/lang/StringBuilder
   // 124: dup
   // 125: invokespecial java/lang/StringBuilder.<init> ()V
   // 128: ldc_w "symlink cycle at "
   // 12b: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 12e: aload 3
   // 12f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
   // 132: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 135: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 138: athrow
   // 139: aload 1
   // 13a: aload 8
   // 13c: invokestatic okio/internal/-FileSystem.symlinkTarget (Lokio/FileSystem;Lokio/Path;)Lokio/Path;
   // 13f: dup
   // 140: ifnonnull 147
   // 143: pop
   // 144: goto 14f
   // 147: astore 8
   // 149: iinc 9 1
   // 14c: goto 10e
   // 14f: iload 4
   // 151: ifne 159
   // 154: iload 9
   // 156: ifne 233
   // 159: aload 2
   // 15a: aload 8
   // 15c: invokevirtual kotlin/collections/ArrayDeque.addLast (Ljava/lang/Object;)V
   // 15f: nop
   // 160: aload 7
   // 162: invokeinterface java/util/List.iterator ()Ljava/util/Iterator; 1
   // 167: astore 10
   // 169: aload 10
   // 16b: invokeinterface java/util/Iterator.hasNext ()Z 1
   // 170: ifeq 221
   // 173: aload 10
   // 175: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
   // 17a: checkcast okio/Path
   // 17d: astore 11
   // 17f: aload 0
   // 180: aload 1
   // 181: aload 2
   // 182: aload 11
   // 184: iload 4
   // 186: ifeq 18d
   // 189: bipush 1
   // 18a: goto 18e
   // 18d: bipush 0
   // 18e: iload 5
   // 190: ifeq 197
   // 193: bipush 1
   // 194: goto 198
   // 197: bipush 0
   // 198: aload 13
   // 19a: aload 13
   // 19c: aload 0
   // 19d: putfield okio/internal/-FileSystem$collectRecursively$1.L$0 Ljava/lang/Object;
   // 1a0: aload 13
   // 1a2: aload 1
   // 1a3: putfield okio/internal/-FileSystem$collectRecursively$1.L$1 Ljava/lang/Object;
   // 1a6: aload 13
   // 1a8: aload 2
   // 1a9: putfield okio/internal/-FileSystem$collectRecursively$1.L$2 Ljava/lang/Object;
   // 1ac: aload 13
   // 1ae: aload 3
   // 1af: putfield okio/internal/-FileSystem$collectRecursively$1.L$3 Ljava/lang/Object;
   // 1b2: aload 13
   // 1b4: aload 10
   // 1b6: putfield okio/internal/-FileSystem$collectRecursively$1.L$4 Ljava/lang/Object;
   // 1b9: aload 13
   // 1bb: iload 4
   // 1bd: putfield okio/internal/-FileSystem$collectRecursively$1.Z$0 Z
   // 1c0: aload 13
   // 1c2: iload 5
   // 1c4: putfield okio/internal/-FileSystem$collectRecursively$1.Z$1 Z
   // 1c7: aload 13
   // 1c9: bipush 2
   // 1ca: putfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 1cd: invokestatic okio/internal/-FileSystem.collectRecursively (Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 1d0: dup
   // 1d1: aload 14
   // 1d3: if_acmpne 21d
   // 1d6: aload 14
   // 1d8: areturn
   // 1d9: aload 13
   // 1db: getfield okio/internal/-FileSystem$collectRecursively$1.Z$1 Z
   // 1de: istore 5
   // 1e0: aload 13
   // 1e2: getfield okio/internal/-FileSystem$collectRecursively$1.Z$0 Z
   // 1e5: istore 4
   // 1e7: aload 13
   // 1e9: getfield okio/internal/-FileSystem$collectRecursively$1.L$4 Ljava/lang/Object;
   // 1ec: checkcast java/util/Iterator
   // 1ef: astore 10
   // 1f1: aload 13
   // 1f3: getfield okio/internal/-FileSystem$collectRecursively$1.L$3 Ljava/lang/Object;
   // 1f6: checkcast okio/Path
   // 1f9: astore 3
   // 1fa: aload 13
   // 1fc: getfield okio/internal/-FileSystem$collectRecursively$1.L$2 Ljava/lang/Object;
   // 1ff: checkcast kotlin/collections/ArrayDeque
   // 202: astore 2
   // 203: aload 13
   // 205: getfield okio/internal/-FileSystem$collectRecursively$1.L$1 Ljava/lang/Object;
   // 208: checkcast okio/FileSystem
   // 20b: astore 1
   // 20c: aload 13
   // 20e: getfield okio/internal/-FileSystem$collectRecursively$1.L$0 Ljava/lang/Object;
   // 211: checkcast kotlin/sequences/SequenceScope
   // 214: astore 0
   // 215: nop
   // 216: aload 12
   // 218: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 21b: aload 12
   // 21d: pop
   // 21e: goto 169
   // 221: aload 2
   // 222: invokevirtual kotlin/collections/ArrayDeque.removeLast ()Ljava/lang/Object;
   // 225: pop
   // 226: goto 233
   // 229: astore 10
   // 22b: aload 2
   // 22c: invokevirtual kotlin/collections/ArrayDeque.removeLast ()Ljava/lang/Object;
   // 22f: pop
   // 230: aload 10
   // 232: athrow
   // 233: iload 5
   // 235: ifeq 278
   // 238: aload 0
   // 239: aload 3
   // 23a: aload 13
   // 23c: aload 13
   // 23e: aconst_null
   // 23f: putfield okio/internal/-FileSystem$collectRecursively$1.L$0 Ljava/lang/Object;
   // 242: aload 13
   // 244: aconst_null
   // 245: putfield okio/internal/-FileSystem$collectRecursively$1.L$1 Ljava/lang/Object;
   // 248: aload 13
   // 24a: aconst_null
   // 24b: putfield okio/internal/-FileSystem$collectRecursively$1.L$2 Ljava/lang/Object;
   // 24e: aload 13
   // 250: aconst_null
   // 251: putfield okio/internal/-FileSystem$collectRecursively$1.L$3 Ljava/lang/Object;
   // 254: aload 13
   // 256: aconst_null
   // 257: putfield okio/internal/-FileSystem$collectRecursively$1.L$4 Ljava/lang/Object;
   // 25a: aload 13
   // 25c: bipush 3
   // 25d: putfield okio/internal/-FileSystem$collectRecursively$1.label I
   // 260: invokevirtual kotlin/sequences/SequenceScope.yield (Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 263: dup
   // 264: aload 14
   // 266: if_acmpne 273
   // 269: aload 14
   // 26b: areturn
   // 26c: aload 12
   // 26e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 271: aload 12
   // 273: pop
   // 274: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 277: areturn
   // 278: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 27b: areturn
   // 27c: new java/lang/IllegalStateException
   // 27f: dup
   // 280: ldc_w "call to 'resume' before 'invoke' with coroutine"
   // 283: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 286: athrow
}

@Throws(java/io/IOException::class)
internal fun FileSystem.symlinkTarget(path: Path): Path? {
   var var10000: Path = `$this$symlinkTarget`.metadata(path).getSymlinkTarget();
   if (var10000 == null) {
      return null;
   } else {
      var var3: Path = path.parent();
      return var3.resolve(var10000);
   }
}

