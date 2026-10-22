package okio

import java.io.Closeable
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import okio.internal.-FileSystem
import okio.internal.ResourceFileSystem

@SourceDebugExtension(["SMAP\nFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystem.kt\nokio/FileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,165:1\n52#2,21:166\n52#2,21:187\n*S KotlinDebug\n*F\n+ 1 FileSystem.kt\nokio/FileSystem\n*L\n67#1:166,21\n81#1:187,21\n*E\n"])
abstract class FileSystem {
   @JvmStatic
   public FileSystem.Companion Companion = new FileSystem.Companion(null);
   @JvmField
   @JvmStatic
   public FileSystem SYSTEM;
   @JvmField
   @JvmStatic
   public Path SYSTEM_TEMPORARY_DIRECTORY;
   @JvmField
   @JvmStatic
   public FileSystem RESOURCES;

   @Throws(java/io/IOException::class)
   public abstract fun canonicalize(path: Path): Path {
   }

   @Throws(java/io/IOException::class)
   public fun metadata(path: Path): FileMetadata {
      return -FileSystem.commonMetadata(this, path);
   }

   @Throws(java/io/IOException::class)
   public abstract fun metadataOrNull(path: Path): FileMetadata? {
   }

   @Throws(java/io/IOException::class)
   public fun exists(path: Path): Boolean {
      return -FileSystem.commonExists(this, path);
   }

   @Throws(java/io/IOException::class)
   public abstract fun list(dir: Path): List<Path> {
   }

   public abstract fun listOrNull(dir: Path): List<Path>? {
   }

   public open fun listRecursively(dir: Path, followSymlinks: Boolean): Sequence<Path> {
      return -FileSystem.commonListRecursively(this, dir, followSymlinks);
   }

   public fun listRecursively(dir: Path): Sequence<Path> {
      return this.listRecursively(dir, false);
   }

   @Throws(java/io/IOException::class)
   public abstract fun openReadOnly(file: Path): FileHandle {
   }

   @Throws(java/io/IOException::class)
   public abstract fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
   }

   @Throws(java/io/IOException::class)
   public fun openReadWrite(file: Path): FileHandle {
      return this.openReadWrite(file, false, false);
   }

   @Throws(java/io/IOException::class)
   public abstract fun source(file: Path): Source {
   }

   @JvmName(name = "-read")
   @Throws(java/io/IOException::class)
   public inline fun <T> read(file: Path, readerAction: (BufferedSource) -> T): T {
      label68: {
         var `$this$use$iv`: Closeable = Okio.buffer(this.source(file));
         var `thrown$iv`: java.lang.Throwable = null;

         label69: {
            label70: {
               try {
                  try {
                     break label70;
                  } catch (java.lang.Throwable var13) {
                     `thrown$iv` = var13;
                  }
               } catch (java.lang.Throwable var14) {
                  InlineMarker.finallyStart(1);

                  try {
                     if (`$this$use$iv` != null) {
                        `$this$use$iv`.close();
                     }
                  } catch (java.lang.Throwable var10) {
                  }

                  InlineMarker.finallyEnd(1);
               }

               InlineMarker.finallyStart(1);

               try {
                  if (`$this$use$iv` != null) {
                     `$this$use$iv`.close();
                  }
               } catch (java.lang.Throwable var12) {
                  if (`thrown$iv` == null) {
                     `thrown$iv` = var12;
                  } else {
                     ExceptionsKt.addSuppressed(`thrown$iv`, var12);
                  }
               }

               InlineMarker.finallyEnd(1);
               break label69;
            }

            InlineMarker.finallyStart(1);

            try {
               if (`$this$use$iv` != null) {
                  `$this$use$iv`.close();
               }
            } catch (java.lang.Throwable var11) {
               `thrown$iv` = var11;
            }

            InlineMarker.finallyEnd(1);
         }

         if (`thrown$iv` != null) {
            throw `thrown$iv`;
         } else {
            return null;
         }
      }
   }

   @Throws(java/io/IOException::class)
   public abstract fun sink(file: Path, mustCreate: Boolean): Sink {
   }

   @Throws(java/io/IOException::class)
   public fun sink(file: Path): Sink {
      return this.sink(file, false);
   }

   @JvmName(name = "-write")
   @Throws(java/io/IOException::class)
   public inline fun <T> write(file: Path, mustCreate: Boolean, writerAction: (BufferedSink) -> T): T {
      label68: {
         var `$this$use$iv`: Closeable = Okio.buffer(this.sink(file, mustCreate));
         var `thrown$iv`: java.lang.Throwable = null;

         label69: {
            label70: {
               try {
                  try {
                     break label70;
                  } catch (java.lang.Throwable var14) {
                     `thrown$iv` = var14;
                  }
               } catch (java.lang.Throwable var15) {
                  InlineMarker.finallyStart(1);

                  try {
                     if (`$this$use$iv` != null) {
                        `$this$use$iv`.close();
                     }
                  } catch (java.lang.Throwable var11) {
                  }

                  InlineMarker.finallyEnd(1);
               }

               InlineMarker.finallyStart(1);

               try {
                  if (`$this$use$iv` != null) {
                     `$this$use$iv`.close();
                  }
               } catch (java.lang.Throwable var13) {
                  if (`thrown$iv` == null) {
                     `thrown$iv` = var13;
                  } else {
                     ExceptionsKt.addSuppressed(`thrown$iv`, var13);
                  }
               }

               InlineMarker.finallyEnd(1);
               break label69;
            }

            InlineMarker.finallyStart(1);

            try {
               if (`$this$use$iv` != null) {
                  `$this$use$iv`.close();
               }
            } catch (java.lang.Throwable var12) {
               `thrown$iv` = var12;
            }

            InlineMarker.finallyEnd(1);
         }

         if (`thrown$iv` != null) {
            throw `thrown$iv`;
         } else {
            return null;
         }
      }
   }

   @Throws(java/io/IOException::class)
   public abstract fun appendingSink(file: Path, mustExist: Boolean): Sink {
   }

   @Throws(java/io/IOException::class)
   public fun appendingSink(file: Path): Sink {
      return this.appendingSink(file, false);
   }

   @Throws(java/io/IOException::class)
   public abstract fun createDirectory(dir: Path, mustCreate: Boolean) {
   }

   @Throws(java/io/IOException::class)
   public fun createDirectory(dir: Path) {
      this.createDirectory(dir, false);
   }

   @Throws(java/io/IOException::class)
   public fun createDirectories(dir: Path, mustCreate: Boolean) {
      -FileSystem.commonCreateDirectories(this, dir, mustCreate);
   }

   @Throws(java/io/IOException::class)
   public fun createDirectories(dir: Path) {
      this.createDirectories(dir, false);
   }

   @Throws(java/io/IOException::class)
   public abstract fun atomicMove(source: Path, target: Path) {
   }

   @Throws(java/io/IOException::class)
   public open fun copy(source: Path, target: Path) {
      -FileSystem.commonCopy(this, source, target);
   }

   @Throws(java/io/IOException::class)
   public abstract fun delete(path: Path, mustExist: Boolean) {
   }

   @Throws(java/io/IOException::class)
   public fun delete(path: Path) {
      this.delete(path, false);
   }

   @Throws(java/io/IOException::class)
   public open fun deleteRecursively(fileOrDirectory: Path, mustExist: Boolean) {
      -FileSystem.commonDeleteRecursively(this, fileOrDirectory, mustExist);
   }

   @Throws(java/io/IOException::class)
   public fun deleteRecursively(fileOrDirectory: Path) {
      this.deleteRecursively(fileOrDirectory, false);
   }

   @Throws(java/io/IOException::class)
   public abstract fun createSymlink(source: Path, target: Path) {
   }

   @JvmName(name = "get")
   @JvmStatic
   fun java.nio.file.FileSystem.get(): FileSystem {
      return Companion.get(`$this$get`);
   }

   @JvmStatic
   fun {
      var `$this$SYSTEM_u24lambda_u242`: FileSystem.Companion = Companion;

      var var10000: JvmSystemFileSystem;
      try {
         Class.forName("java.nio.file.Files");
         var10000 = new NioSystemFileSystem();
      } catch (ClassNotFoundException var3) {
         var10000 = new JvmSystemFileSystem();
      }

      SYSTEM = var10000;
      var var4: Path.Companion = Path.Companion;
      var var10001: java.lang.String = System.getProperty("java.io.tmpdir");
      SYSTEM_TEMPORARY_DIRECTORY = Path.Companion.get$default(var4, var10001, false, 1, null);
      var var10002: ClassLoader = ResourceFileSystem.class.getClassLoader();
      RESOURCES = new ResourceFileSystem(var10002, false, null, 4, null);
   }

   companion object Companion private constructor() {
      public final val RESOURCES: FileSystem
      public final val SYSTEM: FileSystem
      public final val SYSTEM_TEMPORARY_DIRECTORY: Path


      @JvmName(name = "get")
      public fun java.nio.file.FileSystem.asOkioFileSystem(): FileSystem {
         return new NioFileSystemWrappingFileSystem(`$this$asOkioFileSystem`);
      }
   }
}
