package okhttp3.internal.cache

import java.io.Closeable
import java.io.EOFException
import java.io.File
import java.io.FileNotFoundException
import java.io.Flushable
import java.io.IOException
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.NoSuchElementException
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okhttp3.internal.concurrent.Task
import okhttp3.internal.concurrent.TaskQueue
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.io.FileSystem
import okhttp3.internal.platform.Platform
import okio.BufferedSink
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Sink
import okio.Source
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,1065:1\n1#2:1066\n608#3,4:1067\n37#4,2:1071\n37#4,2:1073\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n*L\n215#1:1067,4\n672#1:1071,2\n721#1:1073,2\n*E\n"])
class DiskLruCache internal constructor(fileSystem: FileSystem, directory: File, appVersion: Int, valueCount: Int, maxSize: Long, taskRunner: TaskRunner) :
   Closeable,
   Flushable {
   @JvmStatic
   public DiskLruCache.Companion Companion = new DiskLruCache.Companion(null);
   @JvmField
   @JvmStatic
   public java.lang.String JOURNAL_FILE = "journal";
   @JvmField
   @JvmStatic
   public java.lang.String JOURNAL_FILE_TEMP = "journal.tmp";
   @JvmField
   @JvmStatic
   public java.lang.String JOURNAL_FILE_BACKUP = "journal.bkp";
   @JvmField
   @JvmStatic
   public java.lang.String MAGIC = "libcore.io.DiskLruCache";
   @JvmField
   @JvmStatic
   public java.lang.String VERSION_1 = "1";
   @JvmField
   @JvmStatic
   public long ANY_SEQUENCE_NUMBER = -1L;
   @JvmField
   @JvmStatic
   public Regex LEGAL_KEY_PATTERN = new Regex("[a-z0-9_-]{1,120}");
   @JvmField
   @JvmStatic
   public java.lang.String CLEAN = "CLEAN";
   @JvmField
   @JvmStatic
   public java.lang.String DIRTY = "DIRTY";
   @JvmField
   @JvmStatic
   public java.lang.String REMOVE = "REMOVE";
   @JvmField
   @JvmStatic
   public java.lang.String READ = "READ";

   private final val appVersion: Int
   private final var civilizedFileSystem: Boolean
   private final val cleanupQueue: TaskQueue
   private final val cleanupTask: <unrepresentable>
   internal final var closed: Boolean
   public final val directory: File
   internal final val fileSystem: FileSystem
   private final var hasJournalErrors: Boolean
   private final var initialized: Boolean
   private final val journalFile: File
   private final val journalFileBackup: File
   private final val journalFileTmp: File
   private final var journalWriter: BufferedSink?
   internal final val lruEntries: LinkedHashMap<String, okhttp3.internal.cache.DiskLruCache.Entry>
   public final var maxSize: Long
      public final get() {
         return this.maxSize;
      }

      public final set(value) {
         this.maxSize = value;
         if (this.initialized) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
         }
      }

   private final var mostRecentRebuildFailed: Boolean
   private final var mostRecentTrimFailed: Boolean
   private final var nextSequenceNumber: Long
   private final var redundantOpCount: Int
   private final var size: Long
   internal final val valueCount: Int

   init {
      this.fileSystem = fileSystem;
      this.directory = directory;
      this.appVersion = appVersion;
      this.valueCount = valueCount;
      this.maxSize = maxSize;
      this.lruEntries = new LinkedHashMap<>(0, 0.75F, true);
      this.cleanupQueue = taskRunner.newQueue();
      this.cleanupTask = new Task(this, Util.okHttpName + " Cache") {
         {
            super(`$super_call_param$1`, false, 2, null);
            this.this$0 = `$receiver`;
         }

         @Override
         public long runOnce() {
            label40: {
               var var1: DiskLruCache = this.this$0;
               var var2: DiskLruCache = this.this$0;
               synchronized (this.this$0){} // $VF: monitorenter 

               label37: {
                  try {
                     if (!DiskLruCache.access$getInitialized$p(var2) || var2.getClosed$okhttp()) {
                        break label37;
                     }

                     try {
                        var2.trimToSize();
                     } catch (IOException var11) {
                        DiskLruCache.access$setMostRecentTrimFailed$p(var2, true);
                     }

                     try {
                        if (DiskLruCache.access$journalRebuildRequired(var2)) {
                           var2.rebuildJournal$okhttp();
                           DiskLruCache.access$setRedundantOpCount$p(var2, 0);
                        }
                     } catch (IOException var10) {
                        DiskLruCache.access$setMostRecentRebuildFailed$p(var2, true);
                        DiskLruCache.access$setJournalWriter$p(var2, Okio.buffer(Okio.blackhole()));
                     }
                  } catch (java.lang.Throwable var12) {
                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }
         }
      };
      if (maxSize <= 0L) {
         throw new IllegalArgumentException("maxSize <= 0".toString());
      } else if (this.valueCount <= 0) {
         throw new IllegalArgumentException("valueCount <= 0".toString());
      } else {
         this.journalFile = new File(this.directory, JOURNAL_FILE);
         this.journalFileTmp = new File(this.directory, JOURNAL_FILE_TEMP);
         this.journalFileBackup = new File(this.directory, JOURNAL_FILE_BACKUP);
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun initialize() {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else if (!this.initialized) {
         label61: {
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
               } catch (IOException var4) {
                  Platform.Companion.get().log("DiskLruCache " + this.directory + " is corrupt: " + var4.getMessage() + ", removing", 5, var4);

                  try {
                     this.delete();
                  } catch (java.lang.Throwable var3) {
                     this.closed = false;
                  }

                  this.closed = false;
               }
            }

            this.rebuildJournal$okhttp();
            this.initialized = true;
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readJournal() {
      label57: {
         var var1: Closeable = Okio.buffer(this.fileSystem.source(this.journalFile)) as Closeable;
         var var2: java.lang.Throwable = null;

         try {
            try {
               var source: BufferedSource = var1 as BufferedSource;
               var magic: java.lang.String = (var1 as BufferedSource).readUtf8LineStrict();
               var version: java.lang.String = (var1 as BufferedSource).readUtf8LineStrict();
               var appVersionString: java.lang.String = source.readUtf8LineStrict();
               var valueCountString: java.lang.String = source.readUtf8LineStrict();
               var blank: java.lang.String = source.readUtf8LineStrict();
               if (!(MAGIC == magic)
                  || !(VERSION_1 == version)
                  || !(java.lang.String.valueOf(this.appVersion) == appVersionString)
                  || !(java.lang.String.valueOf(this.valueCount) == valueCountString)
                  || blank.length() > 0) {
                  throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
               }

               var lineCount: Int = 0;

               while (true) {
                  try {
                     this.readJournalLine(source.readUtf8LineStrict());
                     lineCount++;
                  } catch (EOFException var12) {
                     this.redundantOpCount = lineCount - this.lruEntries.size();
                     if (!source.exhausted()) {
                        this.rebuildJournal$okhttp();
                        break;
                     }

                     this.journalWriter = this.newJournalWriter();
                     break;
                  }
               }
            } catch (java.lang.Throwable var13) {
               var2 = var13;
               throw var13;
            }
         } catch (java.lang.Throwable var14) {
            CloseableKt.closeFinally(var1, var2);
         }

         CloseableKt.closeFinally(var1, null);
      }
   }

   @Throws(java/io/FileNotFoundException::class)
   private fun newJournalWriter(): BufferedSink {
      return Okio.buffer((new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile), (new Function1<IOException, Unit>(this) {
         {
            super(1);
            this.this$0 = `$receiver`;
         }

         public final void invoke(@NotNull IOException it) {
            var `$this$assertThreadHoldsLock$iv`: Any = this.this$0;
            if (Util.assertionsEnabled && !Thread.holdsLock(this.this$0)) {
               throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + `$this$assertThreadHoldsLock$iv`);
            } else {
               DiskLruCache.access$setHasJournalErrors$p(this.this$0, true);
            }
         }
      }) as Function1<? super IOException, Unit>)) as Sink);
   }

   @Throws(java/io/IOException::class)
   private fun readJournalLine(line: String) {
      var firstSpace: Int = StringsKt.indexOf$default(line, ' ', 0, false, 6, null);
      if (firstSpace == -1) {
         throw new IOException("unexpected journal line: " + line);
      } else {
         var keyBegin: Int = firstSpace + 1;
         var secondSpace: Int = StringsKt.indexOf$default(line, ' ', firstSpace + 1, false, 4, null);
         var var9: java.lang.String;
         if (secondSpace == -1) {
            var var10000: java.lang.String = line.substring(keyBegin);
            var9 = var10000;
            if (firstSpace == REMOVE.length() && StringsKt.startsWith$default(line, REMOVE, false, 2, null)) {
               this.lruEntries.remove(var10000);
               return;
            }
         } else {
            var var10: java.lang.String = line.substring(keyBegin, secondSpace);
            var9 = var10;
         }

         var entry: DiskLruCache.Entry = this.lruEntries.get(var9);
         if (entry == null) {
            entry = new DiskLruCache.Entry(this, var9);
            this.lruEntries.put(var9, entry);
         }

         if (secondSpace != -1 && firstSpace == CLEAN.length() && StringsKt.startsWith$default(line, CLEAN, false, 2, null)) {
            var var11: java.lang.String = line.substring(secondSpace + 1);
            var parts: java.util.List = StringsKt.split$default(var11, new char[]{' '}, false, 0, 6, null);
            entry.setReadable$okhttp(true);
            entry.setCurrentEditor$okhttp(null);
            entry.setLengths$okhttp(parts);
         } else if (secondSpace == -1 && firstSpace == DIRTY.length() && StringsKt.startsWith$default(line, DIRTY, false, 2, null)) {
            entry.setCurrentEditor$okhttp(new DiskLruCache.Editor(this, entry));
         } else if (secondSpace != -1 || firstSpace != READ.length() || !StringsKt.startsWith$default(line, READ, false, 2, null)) {
            throw new IOException("unexpected journal line: " + line);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun processJournal() {
      this.fileSystem.delete(this.journalFileTmp);
      var i: java.util.Iterator = this.lruEntries.values().iterator();

      while (i.hasNext()) {
         var var10000: Any = i.next();
         var entry: DiskLruCache.Entry = var10000 as DiskLruCache.Entry;
         if ((var10000 as DiskLruCache.Entry).getCurrentEditor$okhttp() == null) {
            var var5: Int = 0;

            for (int var6 = this.valueCount; t < var6; t++) {
               this.size = this.size + entry.getLengths$okhttp()[var5];
            }
         } else {
            entry.setCurrentEditor$okhttp(null);
            var t: Int = 0;

            for (int var4 = this.valueCount; t < var4; t++) {
               this.fileSystem.delete(entry.getCleanFiles$okhttp().get(t));
               this.fileSystem.delete(entry.getDirtyFiles$okhttp().get(t));
            }

            i.remove();
         }
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   internal fun rebuildJournal() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield okhttp3/internal/cache/DiskLruCache.journalWriter Lokio/BufferedSink;
      // 004: dup
      // 005: ifnull 010
      // 008: invokeinterface okio/BufferedSink.close ()V 1
      // 00d: goto 011
      // 010: pop
      // 011: aload 0
      // 012: getfield okhttp3/internal/cache/DiskLruCache.fileSystem Lokhttp3/internal/io/FileSystem;
      // 015: aload 0
      // 016: getfield okhttp3/internal/cache/DiskLruCache.journalFileTmp Ljava/io/File;
      // 019: invokeinterface okhttp3/internal/io/FileSystem.sink (Ljava/io/File;)Lokio/Sink; 2
      // 01e: invokestatic okio/Okio.buffer (Lokio/Sink;)Lokio/BufferedSink;
      // 021: checkcast java/io/Closeable
      // 024: astore 1
      // 025: aconst_null
      // 026: astore 2
      // 027: nop
      // 028: aload 1
      // 029: checkcast okio/BufferedSink
      // 02c: astore 3
      // 02d: bipush 0
      // 02e: istore 4
      // 030: aload 3
      // 031: getstatic okhttp3/internal/cache/DiskLruCache.MAGIC Ljava/lang/String;
      // 034: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 039: bipush 10
      // 03b: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 040: pop
      // 041: aload 3
      // 042: getstatic okhttp3/internal/cache/DiskLruCache.VERSION_1 Ljava/lang/String;
      // 045: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 04a: bipush 10
      // 04c: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 051: pop
      // 052: aload 3
      // 053: aload 0
      // 054: getfield okhttp3/internal/cache/DiskLruCache.appVersion I
      // 057: i2l
      // 058: invokeinterface okio/BufferedSink.writeDecimalLong (J)Lokio/BufferedSink; 3
      // 05d: bipush 10
      // 05f: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 064: pop
      // 065: aload 3
      // 066: aload 0
      // 067: getfield okhttp3/internal/cache/DiskLruCache.valueCount I
      // 06a: i2l
      // 06b: invokeinterface okio/BufferedSink.writeDecimalLong (J)Lokio/BufferedSink; 3
      // 070: bipush 10
      // 072: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 077: pop
      // 078: aload 3
      // 079: bipush 10
      // 07b: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 080: pop
      // 081: aload 0
      // 082: getfield okhttp3/internal/cache/DiskLruCache.lruEntries Ljava/util/LinkedHashMap;
      // 085: invokevirtual java/util/LinkedHashMap.values ()Ljava/util/Collection;
      // 088: invokeinterface java/util/Collection.iterator ()Ljava/util/Iterator; 1
      // 08d: astore 5
      // 08f: aload 5
      // 091: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 096: ifeq 105
      // 099: aload 5
      // 09b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0a0: checkcast okhttp3/internal/cache/DiskLruCache$Entry
      // 0a3: astore 6
      // 0a5: aload 6
      // 0a7: invokevirtual okhttp3/internal/cache/DiskLruCache$Entry.getCurrentEditor$okhttp ()Lokhttp3/internal/cache/DiskLruCache$Editor;
      // 0aa: ifnull 0d6
      // 0ad: aload 3
      // 0ae: getstatic okhttp3/internal/cache/DiskLruCache.DIRTY Ljava/lang/String;
      // 0b1: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 0b6: bipush 32
      // 0b8: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 0bd: pop
      // 0be: aload 3
      // 0bf: aload 6
      // 0c1: invokevirtual okhttp3/internal/cache/DiskLruCache$Entry.getKey$okhttp ()Ljava/lang/String;
      // 0c4: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 0c9: pop
      // 0ca: aload 3
      // 0cb: bipush 10
      // 0cd: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 0d2: pop
      // 0d3: goto 08f
      // 0d6: aload 3
      // 0d7: getstatic okhttp3/internal/cache/DiskLruCache.CLEAN Ljava/lang/String;
      // 0da: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 0df: bipush 32
      // 0e1: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 0e6: pop
      // 0e7: aload 3
      // 0e8: aload 6
      // 0ea: invokevirtual okhttp3/internal/cache/DiskLruCache$Entry.getKey$okhttp ()Ljava/lang/String;
      // 0ed: invokeinterface okio/BufferedSink.writeUtf8 (Ljava/lang/String;)Lokio/BufferedSink; 2
      // 0f2: pop
      // 0f3: aload 6
      // 0f5: aload 3
      // 0f6: invokevirtual okhttp3/internal/cache/DiskLruCache$Entry.writeLengths$okhttp (Lokio/BufferedSink;)V
      // 0f9: aload 3
      // 0fa: bipush 10
      // 0fc: invokeinterface okio/BufferedSink.writeByte (I)Lokio/BufferedSink; 2
      // 101: pop
      // 102: goto 08f
      // 105: nop
      // 106: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 109: astore 3
      // 10a: aload 1
      // 10b: aload 2
      // 10c: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 10f: goto 11f
      // 112: astore 3
      // 113: aload 3
      // 114: astore 2
      // 115: aload 3
      // 116: athrow
      // 117: astore 3
      // 118: aload 1
      // 119: aload 2
      // 11a: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 11d: aload 3
      // 11e: athrow
      // 11f: aload 0
      // 120: getfield okhttp3/internal/cache/DiskLruCache.fileSystem Lokhttp3/internal/io/FileSystem;
      // 123: aload 0
      // 124: getfield okhttp3/internal/cache/DiskLruCache.journalFile Ljava/io/File;
      // 127: invokeinterface okhttp3/internal/io/FileSystem.exists (Ljava/io/File;)Z 2
      // 12c: ifeq 140
      // 12f: aload 0
      // 130: getfield okhttp3/internal/cache/DiskLruCache.fileSystem Lokhttp3/internal/io/FileSystem;
      // 133: aload 0
      // 134: getfield okhttp3/internal/cache/DiskLruCache.journalFile Ljava/io/File;
      // 137: aload 0
      // 138: getfield okhttp3/internal/cache/DiskLruCache.journalFileBackup Ljava/io/File;
      // 13b: invokeinterface okhttp3/internal/io/FileSystem.rename (Ljava/io/File;Ljava/io/File;)V 3
      // 140: aload 0
      // 141: getfield okhttp3/internal/cache/DiskLruCache.fileSystem Lokhttp3/internal/io/FileSystem;
      // 144: aload 0
      // 145: getfield okhttp3/internal/cache/DiskLruCache.journalFileTmp Ljava/io/File;
      // 148: aload 0
      // 149: getfield okhttp3/internal/cache/DiskLruCache.journalFile Ljava/io/File;
      // 14c: invokeinterface okhttp3/internal/io/FileSystem.rename (Ljava/io/File;Ljava/io/File;)V 3
      // 151: aload 0
      // 152: getfield okhttp3/internal/cache/DiskLruCache.fileSystem Lokhttp3/internal/io/FileSystem;
      // 155: aload 0
      // 156: getfield okhttp3/internal/cache/DiskLruCache.journalFileBackup Ljava/io/File;
      // 159: invokeinterface okhttp3/internal/io/FileSystem.delete (Ljava/io/File;)V 2
      // 15e: aload 0
      // 15f: aload 0
      // 160: invokespecial okhttp3/internal/cache/DiskLruCache.newJournalWriter ()Lokio/BufferedSink;
      // 163: putfield okhttp3/internal/cache/DiskLruCache.journalWriter Lokio/BufferedSink;
      // 166: aload 0
      // 167: bipush 0
      // 168: putfield okhttp3/internal/cache/DiskLruCache.hasJournalErrors Z
      // 16b: aload 0
      // 16c: bipush 0
      // 16d: putfield okhttp3/internal/cache/DiskLruCache.mostRecentRebuildFailed Z
      // 170: return
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public operator fun get(key: String): okhttp3.internal.cache.DiskLruCache.Snapshot? {
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      var var10000: DiskLruCache.Entry = this.lruEntries.get(key);
      if (var10000 == null) {
         return null;
      } else {
         var var5: DiskLruCache.Snapshot = var10000.snapshot$okhttp();
         if (var5 == null) {
            return null;
         } else {
            var var4: Int = this.redundantOpCount++;
            var var6: BufferedSink = this.journalWriter;
            var6.writeUtf8(READ).writeByte(32).writeUtf8(key).writeByte(10);
            if (this.journalRebuildRequired()) {
               TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            }

            return var5;
         }
      }
   }

   @JvmOverloads
   @Synchronized
   @Throws(java/io/IOException::class)
   public fun edit(key: String, expectedSequenceNumber: Long): okhttp3.internal.cache.DiskLruCache.Editor? {
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      var entry: DiskLruCache.Entry = this.lruEntries.get(key);
      if (expectedSequenceNumber == ANY_SEQUENCE_NUMBER || entry != null && entry.getSequenceNumber$okhttp() == expectedSequenceNumber) {
         if ((if (entry != null) entry.getCurrentEditor$okhttp() else null) != null) {
            return null;
         } else if (entry != null && entry.getLockingSourceCount$okhttp() != 0) {
            return null;
         } else if (!this.mostRecentTrimFailed && !this.mostRecentRebuildFailed) {
            var var10000: BufferedSink = this.journalWriter;
            var10000.writeUtf8(DIRTY).writeByte(32).writeUtf8(key).writeByte(10);
            var10000.flush();
            if (this.hasJournalErrors) {
               return null;
            } else {
               if (entry == null) {
                  entry = new DiskLruCache.Entry(this, key);
                  this.lruEntries.put(key, entry);
               }

               var editor: DiskLruCache.Editor = new DiskLruCache.Editor(this, entry);
               entry.setCurrentEditor$okhttp(editor);
               return editor;
            }
         } else {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            return null;
         }
      } else {
         return null;
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun size(): Long {
      this.initialize();
      return this.size;
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   internal fun completeEdit(editor: okhttp3.internal.cache.DiskLruCache.Editor, success: Boolean) {
      var entry: DiskLruCache.Entry = editor.getEntry$okhttp();
      if (!(entry.getCurrentEditor$okhttp() == editor)) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         if (success && !entry.getReadable$okhttp()) {
            var i: Int = 0;

            for (int $this$completeEdit_u24lambda_u244 = this.valueCount; i < $this$completeEdit_u24lambda_u244; i++) {
               var var10000: BooleanArray = editor.getWritten$okhttp();
               if (!var10000[i]) {
                  editor.abort();
                  throw new IllegalStateException("Newly created entry didn't create value for index " + i);
               }

               if (!this.fileSystem.exists(entry.getDirtyFiles$okhttp().get(i))) {
                  editor.abort();
                  return;
               }
            }
         }

         var var12: Int = 0;

         for (int var15 = this.valueCount; i < var15; i++) {
            var var6: File = entry.getDirtyFiles$okhttp().get(var12);
            if (!success || entry.getZombie$okhttp()) {
               this.fileSystem.delete(var6);
            } else if (this.fileSystem.exists(var6)) {
               var clean: File = entry.getCleanFiles$okhttp().get(var12);
               this.fileSystem.rename(var6, clean);
               var oldLength: Long = entry.getLengths$okhttp()[var12];
               var newLength: Long = this.fileSystem.size(clean);
               entry.getLengths$okhttp()[var12] = newLength;
               this.size = this.size - oldLength + newLength;
            }
         }

         entry.setCurrentEditor$okhttp(null);
         if (entry.getZombie$okhttp()) {
            this.removeEntry$okhttp(entry);
         } else {
            var12 = this.redundantOpCount++;
            var var19: BufferedSink = this.journalWriter;
            if (!entry.getReadable$okhttp() && !success) {
               this.lruEntries.remove(entry.getKey$okhttp());
               var19.writeUtf8(REMOVE).writeByte(32);
               var19.writeUtf8(entry.getKey$okhttp());
               var19.writeByte(10);
            } else {
               entry.setReadable$okhttp(true);
               var19.writeUtf8(CLEAN).writeByte(32);
               var19.writeUtf8(entry.getKey$okhttp());
               entry.writeLengths$okhttp(var19);
               var19.writeByte(10);
               if (success) {
                  entry.setSequenceNumber$okhttp((long)(this.nextSequenceNumber++));
               }
            }

            var19.flush();
            if (this.size > this.maxSize || this.journalRebuildRequired()) {
               TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            }
         }
      }
   }

   private fun journalRebuildRequired(): Boolean {
      return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun remove(key: String): Boolean {
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      var var10000: DiskLruCache.Entry = this.lruEntries.get(key);
      if (var10000 == null) {
         return false;
      } else {
         var removed: Boolean = this.removeEntry$okhttp(var10000);
         if (removed && this.size <= this.maxSize) {
            this.mostRecentTrimFailed = false;
         }

         return removed;
      }
   }

   @Throws(java/io/IOException::class)
   internal fun removeEntry(entry: okhttp3.internal.cache.DiskLruCache.Entry): Boolean {
      if (!this.civilizedFileSystem) {
         if (entry.getLockingSourceCount$okhttp() > 0) {
            if (this.journalWriter != null) {
               var it: BufferedSink = this.journalWriter;
               this.journalWriter.writeUtf8(DIRTY);
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

      var var10000: DiskLruCache.Editor = entry.getCurrentEditor$okhttp();
      if (var10000 != null) {
         var10000.detach$okhttp();
      }

      var i: Int = 0;

      for (int var3 = this.valueCount; i < var3; i++) {
         this.fileSystem.delete(entry.getCleanFiles$okhttp().get(i));
         this.size = this.size - entry.getLengths$okhttp()[i];
         entry.getLengths$okhttp()[i] = 0L;
      }

      i = this.redundantOpCount++;
      if (this.journalWriter != null) {
         var var7: BufferedSink = this.journalWriter;
         this.journalWriter.writeUtf8(REMOVE);
         var7.writeByte(32);
         var7.writeUtf8(entry.getKey$okhttp());
         var7.writeByte(10);
      }

      this.lruEntries.remove(entry.getKey$okhttp());
      if (this.journalRebuildRequired()) {
         TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
      }

      return true;
   }

   @Synchronized
   private fun checkNotClosed() {
      if (this.closed) {
         throw new IllegalStateException("cache is closed".toString());
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public override fun flush() {
      if (this.initialized) {
         this.checkNotClosed();
         this.trimToSize();
         var var10000: BufferedSink = this.journalWriter;
         var10000.flush();
      }
   }

   @Synchronized
   public fun isClosed(): Boolean {
      return this.closed;
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public override fun close() {
      if (this.initialized && !this.closed) {
         var var10000: java.util.Collection = this.lruEntries.values();

         for (DiskLruCache.Entry entry : var10000.toArray(new DiskLruCache.Entry[0])) {
            if (entry.getCurrentEditor$okhttp() != null) {
               var var7: DiskLruCache.Editor = entry.getCurrentEditor$okhttp();
               if (var7 != null) {
                  var7.detach$okhttp();
               }
            }
         }

         this.trimToSize();
         var var8: BufferedSink = this.journalWriter;
         var8.close();
         this.journalWriter = null;
         this.closed = true;
      } else {
         this.closed = true;
      }
   }

   @Throws(java/io/IOException::class)
   public fun trimToSize() {
      while (this.size > this.maxSize) {
         if (!this.removeOldestEntry()) {
            return;
         }
      }

      this.mostRecentTrimFailed = false;
   }

   private fun removeOldestEntry(): Boolean {
      for (DiskLruCache.Entry toEvict : this.lruEntries.values()) {
         if (!toEvict.getZombie$okhttp()) {
            this.removeEntry$okhttp(toEvict);
            return true;
         }
      }

      return false;
   }

   @Throws(java/io/IOException::class)
   public fun delete() {
      this.close();
      this.fileSystem.deleteContents(this.directory);
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun evictAll() {
      this.initialize();
      var var10000: java.util.Collection = this.lruEntries.values();

      for (DiskLruCache.Entry entry : var10000.toArray(new DiskLruCache.Entry[0])) {
         this.removeEntry$okhttp(entry);
      }

      this.mostRecentTrimFailed = false;
   }

   private fun validateKey(key: String) {
      if (!LEGAL_KEY_PATTERN.matches(key)) {
         throw new IllegalArgumentException(("keys must match regex [a-z0-9_-]{1,120}: \"" + key + '"').toString());
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun snapshots(): MutableIterator<okhttp3.internal.cache.DiskLruCache.Snapshot> {
      this.initialize();
      return new java.util.Iterator<DiskLruCache.Snapshot>(this) {
         @NotNull
         private final java.util.Iterator<DiskLruCache.Entry> delegate;
         @Nullable
         private DiskLruCache.Snapshot nextSnapshot;
         @Nullable
         private DiskLruCache.Snapshot removeSnapshot;

         {
            this.this$0 = `$receiver`;
            var var10001: java.util.Iterator = new ArrayList<>(`$receiver`.getLruEntries$okhttp().values()).iterator();
            this.delegate = var10001;
         }

         @Override
         public boolean hasNext() {
            if (this.nextSnapshot != null) {
               return true;
            } else {
               label75: {
                  var var1: DiskLruCache = this.this$0;
                  var var2: DiskLruCache = this.this$0;
                  synchronized (this.this$0){} // $VF: monitorenter 

                  label40: {
                     label39: {
                        try {
                           if (var2.getClosed$okhttp()) {
                              break label40;
                           }

                           while (this.delegate.hasNext()) {
                              var var10001: DiskLruCache.Entry = this.delegate.next();
                              if (var10001 != null) {
                                 var var10: DiskLruCache.Snapshot = var10001.snapshot$okhttp();
                                 if (var10 != null) {
                                    this.nextSnapshot = var10;
                                    break label39;
                                 }
                              }
                           }
                        } catch (java.lang.Throwable var7) {
                           // $VF: monitorexit
                        }

                        // $VF: monitorexit
                     }

                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }
            }
         }

         @NotNull
         public DiskLruCache.Snapshot next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.removeSnapshot = this.nextSnapshot;
               this.nextSnapshot = null;
               var var10000: DiskLruCache.Snapshot = this.removeSnapshot;
               return var10000;
            }
         }

         @Override
         public void remove() {
            var removeSnapshot: DiskLruCache.Snapshot = this.removeSnapshot;
            if (this.removeSnapshot == null) {
               throw new IllegalStateException("remove() before next()".toString());
            } else {
               label43: {
                  label44: {
                     try {
                        try {
                           this.this$0.remove(removeSnapshot.key());
                           break label44;
                        } catch (IOException var3) {
                        }
                     } catch (java.lang.Throwable var4) {
                        this.removeSnapshot = null;
                     }

                     this.removeSnapshot = null;
                     return;
                  }

                  this.removeSnapshot = null;
               }
            }
         }
      };
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun edit(key: java.lang.String): DiskLruCache.Editor? {
      return edit$default(this, key, 0L, 2, null);
   }

   companion object Companion private constructor() {
      public final val ANY_SEQUENCE_NUMBER: Long
      public final val CLEAN: String
      public final val DIRTY: String
      public final val JOURNAL_FILE: String
      public final val JOURNAL_FILE_BACKUP: String
      public final val JOURNAL_FILE_TEMP: String
      public final val LEGAL_KEY_PATTERN: Regex
      public final val MAGIC: String
      public final val READ: String
      public final val REMOVE: String
      public final val VERSION_1: String

   }

   inner class Editor internal constructor(entry: okhttp3.internal.cache.DiskLruCache.Entry) {
      private final var done: Boolean
      internal final val entry: okhttp3.internal.cache.DiskLruCache.Entry
      internal final val written: BooleanArray?

      init {
         this.this$0 = `this$0`;
         this.entry = entry;
         this.written = if (this.entry.getReadable$okhttp()) null else new boolean[this.this$0.getValueCount$okhttp()];
      }

      internal fun detach() {
         if (this.entry.getCurrentEditor$okhttp() == this) {
            if (DiskLruCache.access$getCivilizedFileSystem$p(this.this$0)) {
               this.this$0.completeEdit$okhttp(this, false);
            } else {
               this.entry.setZombie$okhttp(true);
            }
         }
      }

      public fun newSource(index: Int): Source? {
         var var2: DiskLruCache;
         var var9: Any;
         label40: {
            var2 = this.this$0;
            var var3: DiskLruCache = this.this$0;
            var var8: Source;
            synchronized (this.this$0) {
               if (this.done) {
                  throw new IllegalStateException("Check failed.".toString());
               }

               if (!this.entry.getReadable$okhttp() || !(this.entry.getCurrentEditor$okhttp() == this) || this.entry.getZombie$okhttp()) {
                  var9 = null;
                  break label40;
               }

               var var7: Source;
               try {
                  var7 = var3.getFileSystem$okhttp().source(this.entry.getCleanFiles$okhttp().get(index));
               } catch (FileNotFoundException var10) {
                  var7 = null;
               }

               var8 = var7;
            }

            // $VF: monitorexit
            return var8;
         }

         // $VF: monitorexit
         return (Source)var9;
      }

      public fun newSink(index: Int): Sink {
         var var2: DiskLruCache;
         var var11: Sink;
         label48: {
            var var10: Sink;
            label47: {
               var2 = this.this$0;
               var var3: DiskLruCache = this.this$0;
               var var9: Sink;
               synchronized (this.this$0) {
                  if (this.done) {
                     throw new IllegalStateException("Check failed.".toString());
                  }

                  if (!(this.entry.getCurrentEditor$okhttp() == this)) {
                     var11 = Okio.blackhole();
                     break label48;
                  }

                  if (!this.entry.getReadable$okhttp()) {
                     var var10000: BooleanArray = this.written;
                     var10000[index] = true;
                  }

                  var dirtyFile: File = this.entry.getDirtyFiles$okhttp().get(index);

                  var var14: Sink;
                  try {
                     var14 = var3.getFileSystem$okhttp().sink(dirtyFile);
                  } catch (FileNotFoundException var12) {
                     var10 = Okio.blackhole();
                     break label47;
                  }

                  var9 = (new FaultHidingSink(var14, (new Function1<IOException, Unit>(var3, this) {
                     {
                        super(1);
                        this.this$0 = `$receiver`;
                        this.this$1 = `$receiver`;
                     }

                     // $VF: Extended synchronized range to monitorexit
                     public final void invoke(@NotNull IOException it) {
                        var var3: DiskLruCache.Editor = this.this$1;
                        synchronized (this.this$0) {
                           var3.detach$okhttp();
                        }
                     }
                  }) as Function1<? super IOException, Unit>)) as Sink;
               }

               // $VF: monitorexit
               return var9;
            }

            // $VF: monitorexit
            return var10;
         }

         // $VF: monitorexit
         return var11;
      }

      @Throws(java/io/IOException::class)
      public fun commit() {
         var var2: DiskLruCache = this.this$0;
         synchronized (this.this$0) {
            if (this.done) {
               throw new IllegalStateException("Check failed.".toString());
            } else {
               if (this.entry.getCurrentEditor$okhttp() == this) {
                  var2.completeEdit$okhttp(this, true);
               }

               this.done = true;
            }
         }
      }

      @Throws(java/io/IOException::class)
      public fun abort() {
         var var2: DiskLruCache = this.this$0;
         synchronized (this.this$0) {
            if (this.done) {
               throw new IllegalStateException("Check failed.".toString());
            } else {
               if (this.entry.getCurrentEditor$okhttp() == this) {
                  var2.completeEdit$okhttp(this, false);
               }

               this.done = true;
            }
         }
      }
   }

   @SourceDebugExtension(["SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,1065:1\n608#2,4:1066\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n*L\n1001#1:1066,4\n*E\n"])
   internal inner class Entry internal constructor(key: String) {
      internal final val cleanFiles: MutableList<File>
      internal final var currentEditor: okhttp3.internal.cache.DiskLruCache.Editor?
      internal final val dirtyFiles: MutableList<File>
      internal final val key: String
      internal final val lengths: LongArray
      internal final var lockingSourceCount: Int
      internal final var readable: Boolean
      internal final var sequenceNumber: Long
      internal final var zombie: Boolean

      init {
         this.this$0 = `this$0`;
         this.key = key;
         this.lengths = new long[this.this$0.getValueCount$okhttp()];
         this.cleanFiles = new ArrayList<>();
         this.dirtyFiles = new ArrayList<>();
         var fileBuilder: StringBuilder = new StringBuilder(this.key).append('.');
         var truncateTo: Int = fileBuilder.length();
         var i: Int = 0;

         for (int var6 = this.this$0.getValueCount$okhttp(); i < var6; i++) {
            fileBuilder.append(i);
            this.cleanFiles.add(new File(this.this$0.getDirectory(), fileBuilder.toString()));
            fileBuilder.append(".tmp");
            this.dirtyFiles.add(new File(this.this$0.getDirectory(), fileBuilder.toString()));
            fileBuilder.setLength(truncateTo);
         }
      }

      @Throws(java/io/IOException::class)
      internal fun setLengths(strings: List<String>) {
         if (strings.size() != this.this$0.getValueCount$okhttp()) {
            this.invalidLengths(strings);
            throw new KotlinNothingValueException();
         } else {
            try {
               var _: Int = 0;

               for (int var3 = strings.size(); i < var3; i++) {
                  this.lengths[_] = java.lang.Long.parseLong(strings.get(_) as java.lang.String);
               }
            } catch (NumberFormatException var4) {
               this.invalidLengths(strings);
               throw new KotlinNothingValueException();
            }
         }
      }

      @Throws(java/io/IOException::class)
      internal fun writeLengths(writer: BufferedSink) {
         for (long length : this.lengths) {
            writer.writeByte(32).writeDecimalLong(length);
         }
      }

      @Throws(java/io/IOException::class)
      private fun invalidLengths(strings: List<String>): Nothing {
         throw new IOException("unexpected journal line: " + strings);
      }

      internal fun snapshot(): okhttp3.internal.cache.DiskLruCache.Snapshot? {
         var sources: Any = this.this$0;
         if (Util.assertionsEnabled && !Thread.holdsLock(this.this$0)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + sources);
         } else if (!this.readable) {
            return null;
         } else if (DiskLruCache.access$getCivilizedFileSystem$p(this.this$0) || this.currentEditor == null && !this.zombie) {
            sources = new ArrayList();
            var var9: LongArray = this.lengths.clone() as long[];

            try {
               var _: Int = 0;

               for (int var10 = this.this$0.getValueCount$okhttp(); i < var10; i++) {
                  (sources as java.util.Collection).add(this.newSource(_));
               }

               return this.this$0.new Snapshot(this.this$0, (long)this.key, this.sequenceNumber, (long[])sources, var9);
            } catch (FileNotFoundException var7) {
               for (Source source : $this$assertThreadHoldsLock$iv) {
                  Util.closeQuietly(source as Closeable);
               }

               try {
                  this.this$0.removeEntry$okhttp(this);
               } catch (IOException var6) {
               }

               return null;
            }
         } else {
            return null;
         }
      }

      private fun newSource(index: Int): Source {
         var fileSource: Source = this.this$0.getFileSystem$okhttp().source(this.cleanFiles.get(index));
         if (DiskLruCache.access$getCivilizedFileSystem$p(this.this$0)) {
            return fileSource;
         } else {
            var var3: Int = this.lockingSourceCount++;
            return (new ForwardingSource(fileSource, this.this$0, this) {
               private boolean closed;

               {
                  super(`$fileSource`);
                  this.this$0 = `$receiver`;
                  this.this$1 = `$receiver`;
               }

               // $VF: Extended synchronized range to monitorexit
               public void close() {
                  super.close();
                  if (!this.closed) {
                     this.closed = true;
                     var var2: DiskLruCache.Entry = this.this$1;
                     var var3: DiskLruCache = this.this$0;
                     synchronized (this.this$0) {
                        var2.setLockingSourceCount$okhttp(var2.getLockingSourceCount$okhttp() + -1);
                        if (var2.getLockingSourceCount$okhttp() == 0 && var2.getZombie$okhttp()) {
                           var3.removeEntry$okhttp(var2);
                        }
                     }
                  }
               }
            }) as Source;
         }
      }
   }

   inner class Snapshot internal constructor(key: String, sequenceNumber: Long, sources: List<Source>, lengths: LongArray) : Closeable {
      private final val key: String
      private final val lengths: LongArray
      private final val sequenceNumber: Long
      private final val sources: List<Source>

      init {
         this.this$0 = `this$0`;
         this.key = key;
         this.sequenceNumber = sequenceNumber;
         this.sources = sources;
         this.lengths = lengths;
      }

      public fun key(): String {
         return this.key;
      }

      @Throws(java/io/IOException::class)
      public fun edit(): okhttp3.internal.cache.DiskLruCache.Editor? {
         return this.this$0.edit(this.key, this.sequenceNumber);
      }

      public fun getSource(index: Int): Source {
         return this.sources.get(index);
      }

      public fun getLength(index: Int): Long {
         return this.lengths[index];
      }

      public override fun close() {
         for (Source source : this.sources) {
            Util.closeQuietly(source as Closeable);
         }
      }
   }
}
