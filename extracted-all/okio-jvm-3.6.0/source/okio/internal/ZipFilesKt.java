@file:SourceDebugExtension(["SMAP\nZipFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,459:1\n1045#2:460\n*S KotlinDebug\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n*L\n156#1:460\n*E\n"])

package okio.internal

import java.io.IOException
import java.util.GregorianCalendar
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.LongRef
import kotlin.jvm.internal.Ref.ObjectRef
import okio.BufferedSource
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.ZipFileSystem

private const val BIT_FLAG_ENCRYPTED: Int = 1
private const val BIT_FLAG_UNSUPPORTED_MASK: Int = 1
private const val CENTRAL_FILE_HEADER_SIGNATURE: Int = 33639248
internal const val COMPRESSION_METHOD_DEFLATED: Int = 8
internal const val COMPRESSION_METHOD_STORED: Int = 0
private const val END_OF_CENTRAL_DIRECTORY_SIGNATURE: Int = 101010256
private const val HEADER_ID_EXTENDED_TIMESTAMP: Int = 21589
private const val HEADER_ID_ZIP64_EXTENDED_INFO: Int = 1
private const val LOCAL_FILE_HEADER_SIGNATURE: Int = 67324752
private const val MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE: Long = 4294967295L
private const val ZIP64_EOCD_RECORD_SIGNATURE: Int = 101075792
private const val ZIP64_LOCATOR_SIGNATURE: Int = 117853008
private final val hex: String
   private final get() {
      var var10000: StringBuilder = new StringBuilder().append("0x");
      var var10001: java.lang.String = Integer.toString(`$this$hex`, CharsKt.checkRadix(16));
      return var10000.append(var10001).toString();
   }


@Throws(java/io/IOException::class)
internal fun openZip(zipPath: Path, fileSystem: FileSystem, predicate: (ZipEntry) -> Boolean): ZipFileSystem {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
   //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
   //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
   //
   // Bytecode:
   // 000: aload 0
   // 001: ldc "zipPath"
   // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 006: aload 1
   // 007: ldc "fileSystem"
   // 009: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 00c: aload 2
   // 00d: ldc "predicate"
   // 00f: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 012: aload 1
   // 013: aload 0
   // 014: invokevirtual okio/FileSystem.openReadOnly (Lokio/Path;)Lokio/FileHandle;
   // 017: checkcast java/io/Closeable
   // 01a: astore 3
   // 01b: aconst_null
   // 01c: astore 4
   // 01e: nop
   // 01f: aload 3
   // 020: checkcast okio/FileHandle
   // 023: astore 5
   // 025: bipush 0
   // 026: istore 6
   // 028: aload 5
   // 02a: invokevirtual okio/FileHandle.size ()J
   // 02d: bipush 22
   // 02f: i2l
   // 030: lsub
   // 031: lstore 7
   // 033: lload 7
   // 035: lconst_0
   // 036: lcmp
   // 037: ifge 059
   // 03a: new java/io/IOException
   // 03d: dup
   // 03e: new java/lang/StringBuilder
   // 041: dup
   // 042: invokespecial java/lang/StringBuilder.<init> ()V
   // 045: ldc "not a zip: size="
   // 047: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 04a: aload 5
   // 04c: invokevirtual okio/FileHandle.size ()J
   // 04f: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
   // 052: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 055: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 058: athrow
   // 059: lload 7
   // 05b: ldc2_w 65536
   // 05e: lsub
   // 05f: lconst_0
   // 060: invokestatic java/lang/Math.max (JJ)J
   // 063: lstore 9
   // 065: lconst_0
   // 066: lstore 11
   // 068: aconst_null
   // 069: astore 13
   // 06b: aconst_null
   // 06c: astore 14
   // 06e: nop
   // 06f: aload 5
   // 071: lload 7
   // 073: invokevirtual okio/FileHandle.source (J)Lokio/Source;
   // 076: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
   // 079: astore 15
   // 07b: nop
   // 07c: aload 15
   // 07e: invokeinterface okio/BufferedSource.readIntLe ()I 1
   // 083: ldc 101010256
   // 085: if_icmpne 0ad
   // 088: lload 7
   // 08a: lstore 11
   // 08c: aload 15
   // 08e: invokestatic okio/internal/ZipFilesKt.readEocdRecord (Lokio/BufferedSource;)Lokio/internal/EocdRecord;
   // 091: astore 13
   // 093: aload 15
   // 095: aload 13
   // 097: invokevirtual okio/internal/EocdRecord.getCommentByteCount ()I
   // 09a: i2l
   // 09b: invokeinterface okio/BufferedSource.readUtf8 (J)Ljava/lang/String; 3
   // 0a0: astore 14
   // 0a2: nop
   // 0a3: aload 15
   // 0a5: invokeinterface okio/BufferedSource.close ()V 1
   // 0aa: goto 0dd
   // 0ad: aload 15
   // 0af: invokeinterface okio/BufferedSource.close ()V 1
   // 0b4: goto 0c3
   // 0b7: astore 16
   // 0b9: aload 15
   // 0bb: invokeinterface okio/BufferedSource.close ()V 1
   // 0c0: aload 16
   // 0c2: athrow
   // 0c3: lload 7
   // 0c5: ldc2_w -1
   // 0c8: ladd
   // 0c9: lstore 7
   // 0cb: lload 7
   // 0cd: lload 9
   // 0cf: lcmp
   // 0d0: ifge 06e
   // 0d3: new java/io/IOException
   // 0d6: dup
   // 0d7: ldc "not a zip: end of central directory signature not found"
   // 0d9: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 0dc: athrow
   // 0dd: lload 11
   // 0df: bipush 20
   // 0e1: i2l
   // 0e2: lsub
   // 0e3: lstore 17
   // 0e5: lload 17
   // 0e7: lconst_0
   // 0e8: lcmp
   // 0e9: ifle 1f1
   // 0ec: aload 5
   // 0ee: lload 17
   // 0f0: invokevirtual okio/FileHandle.source (J)Lokio/Source;
   // 0f3: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
   // 0f6: checkcast java/io/Closeable
   // 0f9: astore 19
   // 0fb: aconst_null
   // 0fc: astore 20
   // 0fe: nop
   // 0ff: aload 19
   // 101: checkcast okio/BufferedSource
   // 104: astore 21
   // 106: bipush 0
   // 107: istore 22
   // 109: aload 21
   // 10b: invokeinterface okio/BufferedSource.readIntLe ()I 1
   // 110: ldc 117853008
   // 112: if_icmpne 1cc
   // 115: aload 21
   // 117: invokeinterface okio/BufferedSource.readIntLe ()I 1
   // 11c: istore 23
   // 11e: aload 21
   // 120: invokeinterface okio/BufferedSource.readLongLe ()J 1
   // 125: lstore 24
   // 127: aload 21
   // 129: invokeinterface okio/BufferedSource.readIntLe ()I 1
   // 12e: istore 26
   // 130: iload 26
   // 132: bipush 1
   // 133: if_icmpne 13b
   // 136: iload 23
   // 138: ifeq 145
   // 13b: new java/io/IOException
   // 13e: dup
   // 13f: ldc "unsupported zip: spanned"
   // 141: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 144: athrow
   // 145: aload 5
   // 147: lload 24
   // 149: invokevirtual okio/FileHandle.source (J)Lokio/Source;
   // 14c: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
   // 14f: checkcast java/io/Closeable
   // 152: astore 27
   // 154: aconst_null
   // 155: astore 28
   // 157: nop
   // 158: aload 27
   // 15a: checkcast okio/BufferedSource
   // 15d: astore 29
   // 15f: bipush 0
   // 160: istore 30
   // 162: aload 29
   // 164: invokeinterface okio/BufferedSource.readIntLe ()I 1
   // 169: istore 31
   // 16b: iload 31
   // 16d: ldc 101075792
   // 16f: if_icmpeq 19e
   // 172: new java/io/IOException
   // 175: dup
   // 176: new java/lang/StringBuilder
   // 179: dup
   // 17a: invokespecial java/lang/StringBuilder.<init> ()V
   // 17d: ldc "bad zip: expected "
   // 17f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 182: ldc 101075792
   // 184: invokestatic okio/internal/ZipFilesKt.getHex (I)Ljava/lang/String;
   // 187: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 18a: ldc " but was "
   // 18c: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 18f: iload 31
   // 191: invokestatic okio/internal/ZipFilesKt.getHex (I)Ljava/lang/String;
   // 194: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
   // 197: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
   // 19a: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 19d: athrow
   // 19e: aload 29
   // 1a0: aload 13
   // 1a2: invokestatic okio/internal/ZipFilesKt.readZip64EocdRecord (Lokio/BufferedSource;Lokio/internal/EocdRecord;)Lokio/internal/EocdRecord;
   // 1a5: astore 13
   // 1a7: nop
   // 1a8: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 1ab: astore 29
   // 1ad: aload 27
   // 1af: aload 28
   // 1b1: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 1b4: goto 1cc
   // 1b7: astore 29
   // 1b9: aload 29
   // 1bb: astore 28
   // 1bd: aload 29
   // 1bf: athrow
   // 1c0: astore 29
   // 1c2: aload 27
   // 1c4: aload 28
   // 1c6: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 1c9: aload 29
   // 1cb: athrow
   // 1cc: nop
   // 1cd: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 1d0: astore 21
   // 1d2: aload 19
   // 1d4: aload 20
   // 1d6: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 1d9: goto 1f1
   // 1dc: astore 21
   // 1de: aload 21
   // 1e0: astore 20
   // 1e2: aload 21
   // 1e4: athrow
   // 1e5: astore 21
   // 1e7: aload 19
   // 1e9: aload 20
   // 1eb: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 1ee: aload 21
   // 1f0: athrow
   // 1f1: new java/util/ArrayList
   // 1f4: dup
   // 1f5: invokespecial java/util/ArrayList.<init> ()V
   // 1f8: checkcast java/util/List
   // 1fb: astore 19
   // 1fd: aload 5
   // 1ff: aload 13
   // 201: invokevirtual okio/internal/EocdRecord.getCentralDirectoryOffset ()J
   // 204: invokevirtual okio/FileHandle.source (J)Lokio/Source;
   // 207: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
   // 20a: checkcast java/io/Closeable
   // 20d: astore 20
   // 20f: aconst_null
   // 210: astore 21
   // 212: nop
   // 213: aload 20
   // 215: checkcast okio/BufferedSource
   // 218: astore 22
   // 21a: bipush 0
   // 21b: istore 23
   // 21d: lconst_0
   // 21e: lstore 24
   // 220: aload 13
   // 222: invokevirtual okio/internal/EocdRecord.getEntryCount ()J
   // 225: lstore 32
   // 227: lload 24
   // 229: lload 32
   // 22b: lcmp
   // 22c: ifge 275
   // 22f: aload 22
   // 231: invokestatic okio/internal/ZipFilesKt.readEntry (Lokio/BufferedSource;)Lokio/internal/ZipEntry;
   // 234: astore 28
   // 236: aload 28
   // 238: invokevirtual okio/internal/ZipEntry.getOffset ()J
   // 23b: aload 13
   // 23d: invokevirtual okio/internal/EocdRecord.getCentralDirectoryOffset ()J
   // 240: lcmp
   // 241: iflt 24e
   // 244: new java/io/IOException
   // 247: dup
   // 248: ldc "bad zip: local file header offset >= central directory offset"
   // 24a: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
   // 24d: athrow
   // 24e: aload 2
   // 24f: aload 28
   // 251: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
   // 256: checkcast java/lang/Boolean
   // 259: invokevirtual java/lang/Boolean.booleanValue ()Z
   // 25c: ifeq 26c
   // 25f: aload 19
   // 261: checkcast java/util/Collection
   // 264: aload 28
   // 266: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
   // 26b: pop
   // 26c: lload 24
   // 26e: lconst_1
   // 26f: ladd
   // 270: lstore 24
   // 272: goto 227
   // 275: nop
   // 276: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 279: astore 22
   // 27b: aload 20
   // 27d: aload 21
   // 27f: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 282: goto 29a
   // 285: astore 22
   // 287: aload 22
   // 289: astore 21
   // 28b: aload 22
   // 28d: athrow
   // 28e: astore 22
   // 290: aload 20
   // 292: aload 21
   // 294: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 297: aload 22
   // 299: athrow
   // 29a: aload 19
   // 29c: invokestatic okio/internal/ZipFilesKt.buildIndex (Ljava/util/List;)Ljava/util/Map;
   // 29f: astore 20
   // 2a1: new okio/ZipFileSystem
   // 2a4: dup
   // 2a5: aload 0
   // 2a6: aload 1
   // 2a7: aload 20
   // 2a9: aload 14
   // 2ab: invokespecial okio/ZipFileSystem.<init> (Lokio/Path;Lokio/FileSystem;Ljava/util/Map;Ljava/lang/String;)V
   // 2ae: astore 34
   // 2b0: aload 3
   // 2b1: aload 4
   // 2b3: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 2b6: aload 34
   // 2b8: areturn
   // 2b9: astore 5
   // 2bb: aload 5
   // 2bd: astore 4
   // 2bf: aload 5
   // 2c1: athrow
   // 2c2: astore 5
   // 2c4: aload 3
   // 2c5: aload 4
   // 2c7: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
   // 2ca: aload 5
   // 2cc: athrow
}

@Throws(java/io/IOException::class)
@JvmSynthetic
fun `openZip$default`(var0: Path, var1: FileSystem, var2: Function1, var3: Int, var4: Any): ZipFileSystem {
   if ((var3 and 4) != 0) {
      var2 = <unrepresentable>.INSTANCE;
   }

   return openZip(var0, var1, var2);
}

private fun buildIndex(entries: List<ZipEntry>): Map<Path, ZipEntry> {
   var root: Path = Path.Companion.get$default(Path.Companion, "/", false, 1, null);
   var result: java.util.Map = MapsKt.mutableMapOf(new Pair[]{TuplesKt.to(root, new ZipEntry(root, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null))});

   for (ZipEntry entry : CollectionsKt.sortedWith(entries, new ZipFilesKt$buildIndex$$inlined$sortedBy$1())) {
      if (result.put(var10.getCanonicalPath(), var10) == null) {
         var child: ZipEntry = var10;

         while (true) {
            var var10000: Path = child.getCanonicalPath().parent();
            if (var10000 == null) {
               break;
            }

            var parentEntry: ZipEntry = result.get(var10000) as ZipEntry;
            if (parentEntry != null) {
               parentEntry.getChildren().add(child.getCanonicalPath());
               break;
            }

            parentEntry = new ZipEntry(var10000, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null);
            result.put(var10000, parentEntry);
            parentEntry.getChildren().add(child.getCanonicalPath());
            child = parentEntry;
         }
      }
   }

   return result;
}

@Throws(java/io/IOException::class)
internal fun BufferedSource.readEntry(): ZipEntry {
   var signature: Int = `$this$readEntry`.readIntLe();
   if (signature != 33639248) {
      throw new IOException("bad zip: expected " + getHex(33639248) + " but was " + getHex(signature));
   } else {
      `$this$readEntry`.skip(4L);
      var bitFlag: Int = `$this$readEntry`.readShortLe() and '\uffff';
      if ((bitFlag and 1) != 0) {
         throw new IOException("unsupported zip: general purpose bit flag=" + getHex(bitFlag));
      } else {
         var compressionMethod: Int = `$this$readEntry`.readShortLe() and '\uffff';
         var lastModifiedAtMillis: java.lang.Long = dosDateTimeToEpochMillis(
            `$this$readEntry`.readShortLe() and '\uffff', `$this$readEntry`.readShortLe() and '\uffff'
         );
         var crc: Long = (long)`$this$readEntry`.readIntLe() and 4294967295L;
         var compressedSize: LongRef = new LongRef();
         compressedSize.element = (long)`$this$readEntry`.readIntLe() and 4294967295L;
         var size: LongRef = new LongRef();
         size.element = (long)`$this$readEntry`.readIntLe() and 4294967295L;
         var nameSize: Int = `$this$readEntry`.readShortLe() and '\uffff';
         var extraSize: Int = `$this$readEntry`.readShortLe() and '\uffff';
         var commentByteCount: Int = `$this$readEntry`.readShortLe() and '\uffff';
         `$this$readEntry`.skip(8L);
         var offset: LongRef = new LongRef();
         offset.element = (long)`$this$readEntry`.readIntLe() and 4294967295L;
         var name: java.lang.String = `$this$readEntry`.readUtf8((long)nameSize);
         if (StringsKt.contains$default(name, '\u0000', false, 2, null)) {
            throw new IOException("bad zip: filename contains 0x00");
         } else {
            var isDirectory: Long = 0L;
            if (size.element == 4294967295L) {
               isDirectory = 0L + (long)8;
            }

            if (compressedSize.element == 4294967295L) {
               isDirectory += (long)8;
            }

            if (offset.element == 4294967295L) {
               isDirectory += (long)8;
            }

            var hasZip64Extra: BooleanRef = new BooleanRef();
            readExtra(
               `$this$readEntry`,
               extraSize,
               (new Function2<Integer, java.lang.Long, Unit>(hasZip64Extra, isDirectory, size, `$this$readEntry`, compressedSize, offset) {
                  {
                     super(2);
                     this.$hasZip64Extra = `$hasZip64Extra`;
                     this.$requiredZip64ExtraSize = `$requiredZip64ExtraSize`;
                     this.$size = `$size`;
                     this.$this_readEntry = `$receiver`;
                     this.$compressedSize = `$compressedSize`;
                     this.$offset = `$offset`;
                  }

                  public final void invoke(int headerId, long dataSize) {
                     if (headerId == 1) {
                        if (this.$hasZip64Extra.element) {
                           throw new IOException("bad zip: zip64 extra repeated");
                        }

                        this.$hasZip64Extra.element = true;
                        if (dataSize < this.$requiredZip64ExtraSize) {
                           throw new IOException("bad zip: zip64 extra too short");
                        }

                        this.$size.element = if (this.$size.element == 4294967295L) this.$this_readEntry.readLongLe() else this.$size.element;
                        this.$compressedSize.element = if (this.$compressedSize.element == 4294967295L) this.$this_readEntry.readLongLe() else 0L;
                        this.$offset.element = if (this.$offset.element == 4294967295L) this.$this_readEntry.readLongLe() else 0L;
                     }
                  }
               }) as Function2<? super Integer, ? super java.lang.Long, Unit>
            );
            if (isDirectory > 0L && !hasZip64Extra.element) {
               throw new IOException("bad zip: zip64 extra required but absent");
            } else {
               return new ZipEntry(
                  Path.Companion.get$default(Path.Companion, "/", false, 1, null).resolve(name),
                  StringsKt.endsWith$default(name, "/", false, 2, null),
                  `$this$readEntry`.readUtf8((long)commentByteCount),
                  crc,
                  compressedSize.element,
                  size.element,
                  compressionMethod,
                  lastModifiedAtMillis,
                  offset.element
               );
            }
         }
      }
   }
}

@Throws(java/io/IOException::class)
private fun BufferedSource.readEocdRecord(): EocdRecord {
   var diskNumber: Int = `$this$readEocdRecord`.readShortLe() and '\uffff';
   var diskWithCentralDir: Int = `$this$readEocdRecord`.readShortLe() and '\uffff';
   var entryCount: Long = (long)(`$this$readEocdRecord`.readShortLe() and '\uffff');
   if (entryCount == (long)(`$this$readEocdRecord`.readShortLe() and '\uffff') && diskNumber == 0 && diskWithCentralDir == 0) {
      `$this$readEocdRecord`.skip(4L);
      return new EocdRecord(entryCount, (long)`$this$readEocdRecord`.readIntLe() and 4294967295L, `$this$readEocdRecord`.readShortLe() and '\uffff');
   } else {
      throw new IOException("unsupported zip: spanned");
   }
}

@Throws(java/io/IOException::class)
private fun BufferedSource.readZip64EocdRecord(regularRecord: EocdRecord): EocdRecord {
   `$this$readZip64EocdRecord`.skip(12L);
   var diskNumber: Int = `$this$readZip64EocdRecord`.readIntLe();
   var diskWithCentralDirStart: Int = `$this$readZip64EocdRecord`.readIntLe();
   var entryCount: Long = `$this$readZip64EocdRecord`.readLongLe();
   if (entryCount == `$this$readZip64EocdRecord`.readLongLe() && diskNumber == 0 && diskWithCentralDirStart == 0) {
      `$this$readZip64EocdRecord`.skip(8L);
      return new EocdRecord(entryCount, `$this$readZip64EocdRecord`.readLongLe(), regularRecord.getCommentByteCount());
   } else {
      throw new IOException("unsupported zip: spanned");
   }
}

private fun BufferedSource.readExtra(extraSize: Int, block: (Int, Long) -> Unit) {
   var remaining: Long = (long)extraSize;

   while (remaining != 0L) {
      if (remaining < 4L) {
         throw new IOException("bad zip: truncated header in extra field");
      }

      var headerId: Int = `$this$readExtra`.readShortLe() and '\uffff';
      var dataSize: Long = (long)`$this$readExtra`.readShortLe() and 65535L;
      remaining = remaining - (long)4;
      if (remaining - (long)4 < dataSize) {
         throw new IOException("bad zip: truncated value in extra field");
      }

      `$this$readExtra`.require(dataSize);
      var sizeBefore: Long = `$this$readExtra`.getBuffer().size();
      block.invoke(headerId, dataSize);
      var fieldRemaining: Long = dataSize + `$this$readExtra`.getBuffer().size() - sizeBefore;
      if (fieldRemaining < 0L) {
         throw new IOException("unsupported zip: too many bytes processed for " + headerId);
      }

      if (fieldRemaining > 0L) {
         `$this$readExtra`.getBuffer().skip(fieldRemaining);
      }

      remaining = remaining - dataSize;
   }
}

internal fun BufferedSource.skipLocalHeader() {
   readOrSkipLocalHeader(`$this$skipLocalHeader`, null);
}

internal fun BufferedSource.readLocalHeader(basicMetadata: FileMetadata): FileMetadata {
   var var10000: FileMetadata = readOrSkipLocalHeader(`$this$readLocalHeader`, basicMetadata);
   return var10000;
}

private fun BufferedSource.readOrSkipLocalHeader(basicMetadata: FileMetadata?): FileMetadata? {
   var lastModifiedAtMillis: ObjectRef = new ObjectRef();
   lastModifiedAtMillis.element = if (basicMetadata != null) basicMetadata.getLastModifiedAtMillis() else null;
   var lastAccessedAtMillis: ObjectRef = new ObjectRef();
   var createdAtMillis: ObjectRef = new ObjectRef();
   var signature: Int = `$this$readOrSkipLocalHeader`.readIntLe();
   if (signature != 67324752) {
      throw new IOException("bad zip: expected " + getHex(67324752) + " but was " + getHex(signature));
   } else {
      `$this$readOrSkipLocalHeader`.skip(2L);
      var bitFlag: Int = `$this$readOrSkipLocalHeader`.readShortLe() and '\uffff';
      if ((bitFlag and 1) != 0) {
         throw new IOException("unsupported zip: general purpose bit flag=" + getHex(bitFlag));
      } else {
         `$this$readOrSkipLocalHeader`.skip(18L);
         var fileNameLength: Long = (long)`$this$readOrSkipLocalHeader`.readShortLe() and 65535L;
         var extraSize: Int = `$this$readOrSkipLocalHeader`.readShortLe() and '\uffff';
         `$this$readOrSkipLocalHeader`.skip(fileNameLength);
         if (basicMetadata == null) {
            `$this$readOrSkipLocalHeader`.skip((long)extraSize);
            return null;
         } else {
            readExtra(
               `$this$readOrSkipLocalHeader`,
               extraSize,
               (new Function2<Integer, java.lang.Long, Unit>(`$this$readOrSkipLocalHeader`, lastModifiedAtMillis, lastAccessedAtMillis, createdAtMillis) {
                  {
                     super(2);
                     this.$this_readOrSkipLocalHeader = `$receiver`;
                     this.$lastModifiedAtMillis = `$lastModifiedAtMillis`;
                     this.$lastAccessedAtMillis = `$lastAccessedAtMillis`;
                     this.$createdAtMillis = `$createdAtMillis`;
                  }

                  public final void invoke(int headerId, long dataSize) {
                     if (headerId == 21589) {
                        if (dataSize < 1L) {
                           throw new IOException("bad zip: extended timestamp extra too short");
                        }

                        var flags: Int = this.$this_readOrSkipLocalHeader.readByte() and 255;
                        var hasLastModifiedAtMillis: Boolean = (flags and 1) == 1;
                        var hasLastAccessedAtMillis: Boolean = (flags and 2) == 2;
                        var hasCreatedAtMillis: Boolean = (flags and 4) == 4;
                        var `$this$invoke_u24lambda_u240`: BufferedSource = this.$this_readOrSkipLocalHeader;
                        var result: Long = 1L;
                        if (hasLastModifiedAtMillis) {
                           result = 1L + 4L;
                        }

                        if (hasLastAccessedAtMillis) {
                           result += 4L;
                        }

                        if (hasCreatedAtMillis) {
                           result += 4L;
                        }

                        if (dataSize < result) {
                           throw new IOException("bad zip: extended timestamp extra too short");
                        }

                        if (hasLastModifiedAtMillis) {
                           this.$lastModifiedAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                        }

                        if (hasLastAccessedAtMillis) {
                           this.$lastAccessedAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                        }

                        if (hasCreatedAtMillis) {
                           this.$createdAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                        }
                     }
                  }
               }) as Function2<? super Integer, ? super java.lang.Long, Unit>
            );
            return new FileMetadata(
               basicMetadata.isRegularFile(),
               basicMetadata.isDirectory(),
               null,
               basicMetadata.getSize(),
               createdAtMillis.element as java.lang.Long,
               lastModifiedAtMillis.element as java.lang.Long,
               lastAccessedAtMillis.element as java.lang.Long,
               null,
               128,
               null
            );
         }
      }
   }
}

private fun dosDateTimeToEpochMillis(date: Int, time: Int): Long? {
   if (time == -1) {
      return null;
   } else {
      var cal: GregorianCalendar = new GregorianCalendar();
      cal.set(14, 0);
      cal.set(1980 + (date shr 9 and 127), (date shr 5 and 15) - 1, date and 31, time shr 11 and 31, time shr 5 and 63, (time and 31) shl 1);
      return cal.getTime().getTime();
   }
}

