package okhttp3.internal.io

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import okio.Okio
import okio.Sink
import okio.Source

interface FileSystem {
   @JvmStatic
   FileSystem.Companion Companion = FileSystem.Companion.$$INSTANCE;
   @JvmField
   @JvmStatic
   FileSystem SYSTEM = new FileSystem.Companion.SystemFileSystem();

   @Throws(java/io/FileNotFoundException::class)
   public abstract fun source(file: File): Source {
   }

   @Throws(java/io/FileNotFoundException::class)
   public abstract fun sink(file: File): Sink {
   }

   @Throws(java/io/FileNotFoundException::class)
   public abstract fun appendingSink(file: File): Sink {
   }

   @Throws(java/io/IOException::class)
   public abstract fun delete(file: File) {
   }

   public abstract fun exists(file: File): Boolean {
   }

   public abstract fun size(file: File): Long {
   }

   @Throws(java/io/IOException::class)
   public abstract fun rename(from: File, to: File) {
   }

   @Throws(java/io/IOException::class)
   public abstract fun deleteContents(directory: File) {
   }

   companion object Companion private constructor() {
      public final val SYSTEM: FileSystem


      private class SystemFileSystem : FileSystem {
         @Throws(java/io/FileNotFoundException::class)
         public override fun source(file: File): Source {
            return Okio.source(file);
         }

         @Throws(java/io/FileNotFoundException::class)
         public override fun sink(file: File): Sink {
            var var2: Sink;
            try {
               var2 = Okio.sink$default(file, false, 1, null);
            } catch (FileNotFoundException var4) {
               file.getParentFile().mkdirs();
               var2 = Okio.sink$default(file, false, 1, null);
            }

            return var2;
         }

         @Throws(java/io/FileNotFoundException::class)
         public override fun appendingSink(file: File): Sink {
            var var2: Sink;
            try {
               var2 = Okio.appendingSink(file);
            } catch (FileNotFoundException var4) {
               file.getParentFile().mkdirs();
               var2 = Okio.appendingSink(file);
            }

            return var2;
         }

         @Throws(java/io/IOException::class)
         public override fun delete(file: File) {
            if (!file.delete() && file.exists()) {
               throw new IOException("failed to delete " + file);
            }
         }

         public override fun exists(file: File): Boolean {
            return file.exists();
         }

         public override fun size(file: File): Long {
            return file.length();
         }

         @Throws(java/io/IOException::class)
         public override fun rename(from: File, to: File) {
            this.delete(to);
            if (!from.renameTo(to)) {
               throw new IOException("failed to rename " + from + " to " + to);
            }
         }

         @Throws(java/io/IOException::class)
         public override fun deleteContents(directory: File) {
            var var10000: Array<File> = directory.listFiles();
            if (var10000 == null) {
               throw new IOException("not a readable directory: " + directory);
            } else {
               for (File file : var10000) {
                  if (file.isDirectory()) {
                     this.deleteContents(file);
                  }

                  if (!file.delete()) {
                     throw new IOException("failed to delete " + file);
                  }
               }
            }
         }

         public override fun toString(): String {
            return "FileSystem.SYSTEM";
         }
      }
   }
}
