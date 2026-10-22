package okio

import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nNioSystemFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioSystemFileSystem.kt\nokio/NioSystemFileSystem\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,92:1\n1#2:93\n*E\n"])
internal open class NioSystemFileSystem : JvmSystemFileSystem {
   public override fun metadataOrNull(path: Path): FileMetadata? {
      return this.metadataOrNull(path.toNioPath());
   }

   protected fun metadataOrNull(nioPath: java.nio.file.Path): FileMetadata? {
      var var7: BasicFileAttributes;
      try {
         var7 = Files.readAttributes(nioPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      } catch (NoSuchFileException var5) {
         return null;
      } catch (FileSystemException var6) {
         return null;
      }

      var var8: java.nio.file.Path = if (var7.isSymbolicLink()) Files.readSymbolicLink(nioPath) else null;
      var var10002: Boolean = var7.isRegularFile();
      var var10003: Boolean = var7.isDirectory();
      var var10004: Path = if (var8 != null) Path.Companion.get$default(Path.Companion, var8, false, 1, null) else null;
      var var10005: java.lang.Long = var7.size();
      var var10006: FileTime = var7.creationTime();
      var var9: java.lang.Long = if (var10006 != null) this.zeroToNull(var10006) else null;
      var var10007: FileTime = var7.lastModifiedTime();
      var var10: java.lang.Long = if (var10007 != null) this.zeroToNull(var10007) else null;
      var var10008: FileTime = var7.lastAccessTime();
      return new FileMetadata(var10002, var10003, var10004, var10005, var9, var10, if (var10008 != null) this.zeroToNull(var10008) else null, null, 128, null);
   }

   private fun FileTime.zeroToNull(): Long? {
      var var2: java.lang.Long = `$this$zeroToNull`.toMillis();
      return if (var2.longValue() != 0L) var2 else null;
   }

   public override fun atomicMove(source: Path, target: Path) {
      try {
         Files.move(source.toNioPath(), target.toNioPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (NoSuchFileException var4) {
         throw new FileNotFoundException(var4.getMessage());
      } catch (UnsupportedOperationException var5) {
         throw new IOException("atomic move not supported");
      }
   }

   public override fun createSymlink(source: Path, target: Path) {
      Files.createSymbolicLink(source.toNioPath(), target.toNioPath());
   }

   public override fun toString(): String {
      return "NioSystemFileSystem";
   }
}
