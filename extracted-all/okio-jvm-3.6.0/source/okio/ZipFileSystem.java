package okio

import java.io.FileNotFoundException
import java.io.IOException
import kotlin.jvm.internal.SourceDebugExtension
import okio.internal.ZipEntry

@SourceDebugExtension(["SMAP\nZipFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,175:1\n52#2,5:176\n52#2,21:181\n60#2,10:202\n57#2,2:212\n71#2,2:214\n52#2,21:216\n*S KotlinDebug\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n*L\n102#1:176,5\n103#1:181,21\n102#1:202,10\n102#1:212,2\n102#1:214,2\n132#1:216,21\n*E\n"])
internal class ZipFileSystem internal constructor(zipPath: Path, fileSystem: FileSystem, entries: Map<Path, ZipEntry>, comment: String?) : FileSystem {
   @JvmStatic
   private ZipFileSystem.Companion Companion = new ZipFileSystem.Companion(null);
   @JvmStatic
   private Path ROOT = Path.Companion.get$default(Path.Companion, "/", false, 1, null);

   private final val comment: String?
   private final val entries: Map<Path, ZipEntry>
   private final val fileSystem: FileSystem
   private final val zipPath: Path

   init {
      this.zipPath = zipPath;
      this.fileSystem = fileSystem;
      this.entries = entries;
      this.comment = comment;
   }

   public override fun canonicalize(path: Path): Path {
      var canonical: Path = this.canonicalizeInternal(path);
      if (!this.entries.containsKey(canonical)) {
         throw new FileNotFoundException(java.lang.String.valueOf(path));
      } else {
         return canonical;
      }
   }

   private fun canonicalizeInternal(path: Path): Path {
      return ROOT.resolve(path, true);
   }

   public override fun metadataOrNull(path: Path): FileMetadata? {
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
      // 000: aload 1
      // 001: ldc "path"
      // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 006: aload 0
      // 007: aload 1
      // 008: invokespecial okio/ZipFileSystem.canonicalizeInternal (Lokio/Path;)Lokio/Path;
      // 00b: astore 2
      // 00c: aload 0
      // 00d: getfield okio/ZipFileSystem.entries Ljava/util/Map;
      // 010: aload 2
      // 011: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 016: checkcast okio/internal/ZipEntry
      // 019: dup
      // 01a: ifnonnull 020
      // 01d: pop
      // 01e: aconst_null
      // 01f: areturn
      // 020: astore 3
      // 021: new okio/FileMetadata
      // 024: dup
      // 025: aload 3
      // 026: invokevirtual okio/internal/ZipEntry.isDirectory ()Z
      // 029: ifne 030
      // 02c: bipush 1
      // 02d: goto 031
      // 030: bipush 0
      // 031: aload 3
      // 032: invokevirtual okio/internal/ZipEntry.isDirectory ()Z
      // 035: aconst_null
      // 036: aload 3
      // 037: invokevirtual okio/internal/ZipEntry.isDirectory ()Z
      // 03a: ifeq 041
      // 03d: aconst_null
      // 03e: goto 048
      // 041: aload 3
      // 042: invokevirtual okio/internal/ZipEntry.getSize ()J
      // 045: invokestatic java/lang/Long.valueOf (J)Ljava/lang/Long;
      // 048: aconst_null
      // 049: aload 3
      // 04a: invokevirtual okio/internal/ZipEntry.getLastModifiedAtMillis ()Ljava/lang/Long;
      // 04d: aconst_null
      // 04e: aconst_null
      // 04f: sipush 128
      // 052: aconst_null
      // 053: invokespecial okio/FileMetadata.<init> (ZZLokio/Path;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/util/Map;ILkotlin/jvm/internal/DefaultConstructorMarker;)V
      // 056: astore 4
      // 058: aload 3
      // 059: invokevirtual okio/internal/ZipEntry.getOffset ()J
      // 05c: ldc2_w -1
      // 05f: lcmp
      // 060: ifne 066
      // 063: aload 4
      // 065: areturn
      // 066: aload 0
      // 067: getfield okio/ZipFileSystem.fileSystem Lokio/FileSystem;
      // 06a: aload 0
      // 06b: getfield okio/ZipFileSystem.zipPath Lokio/Path;
      // 06e: invokevirtual okio/FileSystem.openReadOnly (Lokio/Path;)Lokio/FileHandle;
      // 071: checkcast java/io/Closeable
      // 074: astore 5
      // 076: bipush 0
      // 077: istore 6
      // 079: aconst_null
      // 07a: astore 7
      // 07c: aconst_null
      // 07d: astore 8
      // 07f: nop
      // 080: aload 5
      // 082: checkcast okio/FileHandle
      // 085: astore 9
      // 087: bipush 0
      // 088: istore 10
      // 08a: aload 9
      // 08c: aload 3
      // 08d: invokevirtual okio/internal/ZipEntry.getOffset ()J
      // 090: invokevirtual okio/FileHandle.source (J)Lokio/Source;
      // 093: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
      // 096: checkcast java/io/Closeable
      // 099: astore 11
      // 09b: bipush 0
      // 09c: istore 12
      // 09e: aconst_null
      // 09f: astore 13
      // 0a1: aconst_null
      // 0a2: astore 14
      // 0a4: nop
      // 0a5: aload 11
      // 0a7: checkcast okio/BufferedSource
      // 0aa: astore 15
      // 0ac: bipush 0
      // 0ad: istore 16
      // 0af: aload 15
      // 0b1: aload 4
      // 0b3: invokestatic okio/internal/ZipFilesKt.readLocalHeader (Lokio/BufferedSource;Lokio/FileMetadata;)Lokio/FileMetadata;
      // 0b6: astore 13
      // 0b8: nop
      // 0b9: aload 11
      // 0bb: dup
      // 0bc: ifnull 0c7
      // 0bf: invokeinterface java/io/Closeable.close ()V 1
      // 0c4: goto 0d1
      // 0c7: pop
      // 0c8: goto 0d1
      // 0cb: astore 15
      // 0cd: aload 15
      // 0cf: astore 14
      // 0d1: goto 123
      // 0d4: astore 15
      // 0d6: aload 15
      // 0d8: astore 14
      // 0da: nop
      // 0db: aload 11
      // 0dd: dup
      // 0de: ifnull 0e9
      // 0e1: invokeinterface java/io/Closeable.close ()V 1
      // 0e6: goto 102
      // 0e9: pop
      // 0ea: goto 102
      // 0ed: astore 15
      // 0ef: aload 14
      // 0f1: ifnonnull 0fb
      // 0f4: aload 15
      // 0f6: astore 14
      // 0f8: goto 102
      // 0fb: aload 14
      // 0fd: aload 15
      // 0ff: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
      // 102: goto 123
      // 105: astore 15
      // 107: nop
      // 108: aload 11
      // 10a: dup
      // 10b: ifnull 116
      // 10e: invokeinterface java/io/Closeable.close ()V 1
      // 113: goto 120
      // 116: pop
      // 117: goto 120
      // 11a: astore 16
      // 11c: aload 16
      // 11e: astore 14
      // 120: aload 15
      // 122: athrow
      // 123: aload 14
      // 125: dup
      // 126: ifnull 12a
      // 129: athrow
      // 12a: pop
      // 12b: aload 13
      // 12d: dup
      // 12e: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 131: nop
      // 132: astore 7
      // 134: nop
      // 135: aload 5
      // 137: dup
      // 138: ifnull 143
      // 13b: invokeinterface java/io/Closeable.close ()V 1
      // 140: goto 14d
      // 143: pop
      // 144: goto 14d
      // 147: astore 9
      // 149: aload 9
      // 14b: astore 8
      // 14d: goto 19f
      // 150: astore 9
      // 152: aload 9
      // 154: astore 8
      // 156: nop
      // 157: aload 5
      // 159: dup
      // 15a: ifnull 165
      // 15d: invokeinterface java/io/Closeable.close ()V 1
      // 162: goto 17e
      // 165: pop
      // 166: goto 17e
      // 169: astore 9
      // 16b: aload 8
      // 16d: ifnonnull 177
      // 170: aload 9
      // 172: astore 8
      // 174: goto 17e
      // 177: aload 8
      // 179: aload 9
      // 17b: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
      // 17e: goto 19f
      // 181: astore 9
      // 183: nop
      // 184: aload 5
      // 186: dup
      // 187: ifnull 192
      // 18a: invokeinterface java/io/Closeable.close ()V 1
      // 18f: goto 19c
      // 192: pop
      // 193: goto 19c
      // 196: astore 10
      // 198: aload 10
      // 19a: astore 8
      // 19c: aload 9
      // 19e: athrow
      // 19f: aload 8
      // 1a1: dup
      // 1a2: ifnull 1a6
      // 1a5: athrow
      // 1a6: pop
      // 1a7: aload 7
      // 1a9: dup
      // 1aa: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 1ad: areturn
   }

   public override fun openReadOnly(file: Path): FileHandle {
      throw new UnsupportedOperationException("not implemented yet!");
   }

   public override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
      throw new IOException("zip entries are not writable");
   }

   public override fun list(dir: Path): List<Path> {
      var var10000: java.util.List = this.list(dir, true);
      return var10000;
   }

   public override fun listOrNull(dir: Path): List<Path>? {
      return this.list(dir, false);
   }

   private fun list(dir: Path, throwOnFailure: Boolean): List<Path>? {
      var var10000: ZipEntry = this.entries.get(this.canonicalizeInternal(dir));
      if (var10000 == null) {
         if (throwOnFailure) {
            throw new IOException("not a directory: " + dir);
         } else {
            return null;
         }
      } else {
         return CollectionsKt.toList(var10000.getChildren());
      }
   }

   @Throws(java/io/IOException::class)
   public override fun source(file: Path): Source {
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
      // 000: aload 1
      // 001: ldc "file"
      // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 006: aload 0
      // 007: aload 1
      // 008: invokespecial okio/ZipFileSystem.canonicalizeInternal (Lokio/Path;)Lokio/Path;
      // 00b: astore 2
      // 00c: aload 0
      // 00d: getfield okio/ZipFileSystem.entries Ljava/util/Map;
      // 010: aload 2
      // 011: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 016: checkcast okio/internal/ZipEntry
      // 019: dup
      // 01a: ifnonnull 039
      // 01d: pop
      // 01e: new java/io/FileNotFoundException
      // 021: dup
      // 022: new java/lang/StringBuilder
      // 025: dup
      // 026: invokespecial java/lang/StringBuilder.<init> ()V
      // 029: ldc "no such file: "
      // 02b: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 02e: aload 1
      // 02f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      // 032: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 035: invokespecial java/io/FileNotFoundException.<init> (Ljava/lang/String;)V
      // 038: athrow
      // 039: astore 3
      // 03a: aload 0
      // 03b: getfield okio/ZipFileSystem.fileSystem Lokio/FileSystem;
      // 03e: aload 0
      // 03f: getfield okio/ZipFileSystem.zipPath Lokio/Path;
      // 042: invokevirtual okio/FileSystem.openReadOnly (Lokio/Path;)Lokio/FileHandle;
      // 045: checkcast java/io/Closeable
      // 048: astore 5
      // 04a: bipush 0
      // 04b: istore 6
      // 04d: aconst_null
      // 04e: astore 7
      // 050: aconst_null
      // 051: astore 8
      // 053: nop
      // 054: aload 5
      // 056: checkcast okio/FileHandle
      // 059: astore 9
      // 05b: bipush 0
      // 05c: istore 10
      // 05e: aload 9
      // 060: aload 3
      // 061: invokevirtual okio/internal/ZipEntry.getOffset ()J
      // 064: invokevirtual okio/FileHandle.source (J)Lokio/Source;
      // 067: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
      // 06a: astore 7
      // 06c: nop
      // 06d: aload 5
      // 06f: dup
      // 070: ifnull 07b
      // 073: invokeinterface java/io/Closeable.close ()V 1
      // 078: goto 085
      // 07b: pop
      // 07c: goto 085
      // 07f: astore 9
      // 081: aload 9
      // 083: astore 8
      // 085: goto 0d7
      // 088: astore 9
      // 08a: aload 9
      // 08c: astore 8
      // 08e: nop
      // 08f: aload 5
      // 091: dup
      // 092: ifnull 09d
      // 095: invokeinterface java/io/Closeable.close ()V 1
      // 09a: goto 0b6
      // 09d: pop
      // 09e: goto 0b6
      // 0a1: astore 9
      // 0a3: aload 8
      // 0a5: ifnonnull 0af
      // 0a8: aload 9
      // 0aa: astore 8
      // 0ac: goto 0b6
      // 0af: aload 8
      // 0b1: aload 9
      // 0b3: invokestatic kotlin/ExceptionsKt.addSuppressed (Ljava/lang/Throwable;Ljava/lang/Throwable;)V
      // 0b6: goto 0d7
      // 0b9: astore 9
      // 0bb: nop
      // 0bc: aload 5
      // 0be: dup
      // 0bf: ifnull 0ca
      // 0c2: invokeinterface java/io/Closeable.close ()V 1
      // 0c7: goto 0d4
      // 0ca: pop
      // 0cb: goto 0d4
      // 0ce: astore 10
      // 0d0: aload 10
      // 0d2: astore 8
      // 0d4: aload 9
      // 0d6: athrow
      // 0d7: aload 8
      // 0d9: dup
      // 0da: ifnull 0de
      // 0dd: athrow
      // 0de: pop
      // 0df: aload 7
      // 0e1: dup
      // 0e2: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0e5: astore 4
      // 0e7: aload 4
      // 0e9: invokestatic okio/internal/ZipFilesKt.skipLocalHeader (Lokio/BufferedSource;)V
      // 0ec: aload 3
      // 0ed: invokevirtual okio/internal/ZipEntry.getCompressionMethod ()I
      // 0f0: ifne 10a
      // 0f3: new okio/internal/FixedLengthSource
      // 0f6: dup
      // 0f7: aload 4
      // 0f9: checkcast okio/Source
      // 0fc: aload 3
      // 0fd: invokevirtual okio/internal/ZipEntry.getSize ()J
      // 100: bipush 1
      // 101: invokespecial okio/internal/FixedLengthSource.<init> (Lokio/Source;JZ)V
      // 104: checkcast okio/Source
      // 107: goto 143
      // 10a: new okio/InflaterSource
      // 10d: dup
      // 10e: new okio/internal/FixedLengthSource
      // 111: dup
      // 112: aload 4
      // 114: checkcast okio/Source
      // 117: aload 3
      // 118: invokevirtual okio/internal/ZipEntry.getCompressedSize ()J
      // 11b: bipush 1
      // 11c: invokespecial okio/internal/FixedLengthSource.<init> (Lokio/Source;JZ)V
      // 11f: checkcast okio/Source
      // 122: new java/util/zip/Inflater
      // 125: dup
      // 126: bipush 1
      // 127: invokespecial java/util/zip/Inflater.<init> (Z)V
      // 12a: invokespecial okio/InflaterSource.<init> (Lokio/Source;Ljava/util/zip/Inflater;)V
      // 12d: astore 6
      // 12f: new okio/internal/FixedLengthSource
      // 132: dup
      // 133: aload 6
      // 135: checkcast okio/Source
      // 138: aload 3
      // 139: invokevirtual okio/internal/ZipEntry.getSize ()J
      // 13c: bipush 0
      // 13d: invokespecial okio/internal/FixedLengthSource.<init> (Lokio/Source;JZ)V
      // 140: checkcast okio/Source
      // 143: areturn
   }

   public override fun sink(file: Path, mustCreate: Boolean): Sink {
      throw new IOException("zip file systems are read-only");
   }

   public override fun appendingSink(file: Path, mustExist: Boolean): Sink {
      throw new IOException("zip file systems are read-only");
   }

   public override fun createDirectory(dir: Path, mustCreate: Boolean) {
      throw new IOException("zip file systems are read-only");
   }

   public override fun atomicMove(source: Path, target: Path) {
      throw new IOException("zip file systems are read-only");
   }

   public override fun delete(path: Path, mustExist: Boolean) {
      throw new IOException("zip file systems are read-only");
   }

   public override fun createSymlink(source: Path, target: Path) {
      throw new IOException("zip file systems are read-only");
   }

   private companion object Companion private constructor() {
      public final val ROOT: Path

   }
}
