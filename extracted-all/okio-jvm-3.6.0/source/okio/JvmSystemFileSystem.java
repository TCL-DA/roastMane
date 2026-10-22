package okio

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.io.RandomAccessFile
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJvmSystemFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JvmSystemFileSystem.kt\nokio/JvmSystemFileSystem\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,158:1\n11400#2,3:159\n*S KotlinDebug\n*F\n+ 1 JvmSystemFileSystem.kt\nokio/JvmSystemFileSystem\n*L\n77#1:159,3\n*E\n"])
internal open class JvmSystemFileSystem : FileSystem {
   public override fun canonicalize(path: Path): Path {
      var canonicalFile: File = path.toFile().getCanonicalFile();
      if (!canonicalFile.exists()) {
         throw new FileNotFoundException("no such file");
      } else {
         var var10000: Path.Companion = Path.Companion;
         return Path.Companion.get$default(var10000, canonicalFile, false, 1, null);
      }
   }

   public override fun metadataOrNull(path: Path): FileMetadata? {
      var file: File = path.toFile();
      var isRegularFile: Boolean = file.isFile();
      var isDirectory: Boolean = file.isDirectory();
      var lastModifiedAtMillis: Long = file.lastModified();
      var size: Long = file.length();
      return if (!isRegularFile && !isDirectory && lastModifiedAtMillis == 0L && size == 0L && !file.exists())
         null
         else
         new FileMetadata(isRegularFile, isDirectory, null, size, null, lastModifiedAtMillis, null, null, 128, null);
   }

   public override fun list(dir: Path): List<Path> {
      var var10000: java.util.List = this.list(dir, true);
      return var10000;
   }

   public override fun listOrNull(dir: Path): List<Path>? {
      return this.list(dir, false);
   }

   private fun list(dir: Path, throwOnFailure: Boolean): List<Path>? {
      var file: File = dir.toFile();
      var entries: Array<java.lang.String> = file.list();
      if (entries == null) {
         if (throwOnFailure) {
            if (!file.exists()) {
               throw new FileNotFoundException("no such file: " + dir);
            } else {
               throw new IOException("failed to list " + dir);
            }
         } else {
            return null;
         }
      } else {
         var `destination$iv`: java.util.Collection = new ArrayList();

         for (Object item$iv : entries) {
            `destination$iv`.add(dir.resolve((java.lang.String)`item$iv`));
         }

         var result: java.util.List = `destination$iv` as java.util.List;
         CollectionsKt.sort(`destination$iv` as java.util.List);
         return result;
      }
   }

   public override fun openReadOnly(file: Path): FileHandle {
      return new JvmFileHandle(false, new RandomAccessFile(file.toFile(), "r"));
   }

   public override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
      if (mustCreate && mustExist) {
         throw new IllegalArgumentException("Cannot require mustCreate and mustExist at the same time.".toString());
      } else {
         if (mustCreate) {
            this.requireCreate(file);
         }

         if (mustExist) {
            this.requireExist(file);
         }

         return new JvmFileHandle(true, new RandomAccessFile(file.toFile(), "rw"));
      }
   }

   public override fun source(file: Path): Source {
      return Okio.source(file.toFile());
   }

   public override fun sink(file: Path, mustCreate: Boolean): Sink {
      if (mustCreate) {
         this.requireCreate(file);
      }

      return Okio.sink$default(file.toFile(), false, 1, null);
   }

   public override fun appendingSink(file: Path, mustExist: Boolean): Sink {
      if (mustExist) {
         this.requireExist(file);
      }

      return Okio.sink(file.toFile(), true);
   }

   public override fun createDirectory(dir: Path, mustCreate: Boolean) {
      if (!dir.toFile().mkdir()) {
         var var10000: FileMetadata = this.metadataOrNull(dir);
         if (var10000 != null && var10000.isDirectory()) {
            if (mustCreate) {
               throw new IOException(dir + " already exists.");
            }
         } else {
            throw new IOException("failed to create directory: " + dir);
         }
      }
   }

   public override fun atomicMove(source: Path, target: Path) {
      if (!source.toFile().renameTo(target.toFile())) {
         throw new IOException("failed to move " + source + " to " + target);
      }
   }

   public override fun delete(path: Path, mustExist: Boolean) {
      if (Thread.interrupted()) {
         throw new InterruptedIOException("interrupted");
      } else {
         var file: File = path.toFile();
         if (!file.delete()) {
            if (file.exists()) {
               throw new IOException("failed to delete " + path);
            }

            if (mustExist) {
               throw new FileNotFoundException("no such file: " + path);
            }
         }
      }
   }

   public override fun createSymlink(source: Path, target: Path) {
      throw new IOException("unsupported");
   }

   public override fun toString(): String {
      return "JvmSystemFileSystem";
   }

   private fun Path.requireExist() {
      if (!this.exists(`$this$requireExist`)) {
         throw new IOException(`$this$requireExist` + " doesn't exist.");
      }
   }

   private fun Path.requireCreate() {
      if (this.exists(`$this$requireCreate`)) {
         throw new IOException(`$this$requireCreate` + " already exists.");
      }
   }
}
