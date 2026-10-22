package kotlin.io.path

import java.io.Closeable
import java.net.URI
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileStore
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.UserPrincipal
import java.util.Arrays
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPathUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathUtils.kt\nkotlin/io/path/PathsKt__PathUtilsKt\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1174:1\n26#2:1175\n26#2:1179\n1#3:1176\n1855#4,2:1177\n*S KotlinDebug\n*F\n+ 1 PathUtils.kt\nkotlin/io/path/PathsKt__PathUtilsKt\n*L\n221#1:1175\n616#1:1179\n440#1:1177,2\n*E\n"])
// $VF: Class flags could not be determined
internal class PathsKt__PathUtilsKt : PathsKt__PathRecursiveFunctionsKt {
   public final val extension: String
      public final get() {
         var var10000: Path = `$this$extension`.getFileName();
         if (var10000 != null) {
            var var1: java.lang.String = var10000.toString();
            if (var1 != null) {
               var var2: java.lang.String = StringsKt.substringAfterLast(var1, '.', "");
               if (var2 != null) {
                  return var2;
               }
            }
         }

         return "";
      }

   public final val invariantSeparatorsPath: String
      public final inline get() {
         return PathsKt.getInvariantSeparatorsPathString(`$this$invariantSeparatorsPath`);
      }

   public final val invariantSeparatorsPathString: String
      public final get() {
         var separator: java.lang.String = `$this$invariantSeparatorsPathString`.getFileSystem().getSeparator();
         var var2: java.lang.String;
         if (!(separator == "/")) {
            var2 = `$this$invariantSeparatorsPathString`.toString();
            var2 = StringsKt.replace$default(var2, separator, "/", false, 4, null);
         } else {
            var2 = `$this$invariantSeparatorsPathString`.toString();
         }

         return var2;
      }

   public final val name: String
      public final get() {
         var var10000: Path = `$this$name`.getFileName();
         var var1: java.lang.String = if (var10000 != null) var10000.toString() else null;
         if (var1 == null) {
            var1 = "";
         }

         return var1;
      }

   public final val nameWithoutExtension: String
      public final get() {
         var var10000: Path = `$this$nameWithoutExtension`.getFileName();
         if (var10000 != null) {
            var var1: java.lang.String = var10000.toString();
            if (var1 != null) {
               var var2: java.lang.String = StringsKt.substringBeforeLast$default(var1, ".", null, 2, null);
               if (var2 != null) {
                  return var2;
               }
            }
         }

         return "";
      }

   public final val pathString: String
      public final inline get() {
         return `$this$pathString`.toString();
      }


   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.absolute(): Path {
      var var10000: Path = `$this$absolute`.toAbsolutePath();
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.absolutePathString(): String {
      return `$this$absolutePathString`.toAbsolutePath().toString();
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @JvmStatic
   public fun Path.relativeTo(base: Path): Path {
      try {
         return PathRelativizer.INSTANCE.tryRelativeTo(`$this$relativeTo`, base);
      } catch (IllegalArgumentException var4) {
         throw new IllegalArgumentException(var4.getMessage() + "\nthis path: " + `$this$relativeTo` + "\nbase path: " + base, var4);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @JvmStatic
   public fun Path.relativeToOrSelf(base: Path): Path {
      var var10000: Path = PathsKt.relativeToOrNull(`$this$relativeToOrSelf`, base);
      if (var10000 == null) {
         var10000 = `$this$relativeToOrSelf`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @JvmStatic
   public fun Path.relativeToOrNull(base: Path): Path? {
      var var2: Path;
      try {
         var2 = PathRelativizer.INSTANCE.tryRelativeTo(`$this$relativeToOrNull`, base);
      } catch (IllegalArgumentException var4) {
         var2 = null;
      }

      return var2;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.copyTo(target: Path, overwrite: Boolean): Path {
      var var10000: Array<CopyOption> = if (overwrite) new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} else new CopyOption[0];
      var var6: Path = Files.copy(`$this$copyTo`, target, Arrays.copyOf(var10000, var10000.length));
      return var6;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.copyTo(target: Path, vararg options: CopyOption): Path {
      var var10000: Path = Files.copy(`$this$copyTo`, target, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.exists(vararg options: LinkOption): Boolean {
      return Files.exists(`$this$exists`, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.notExists(vararg options: LinkOption): Boolean {
      return Files.notExists(`$this$notExists`, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isRegularFile(vararg options: LinkOption): Boolean {
      return Files.isRegularFile(`$this$isRegularFile`, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isDirectory(vararg options: LinkOption): Boolean {
      return Files.isDirectory(`$this$isDirectory`, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isSymbolicLink(): Boolean {
      return Files.isSymbolicLink(`$this$isSymbolicLink`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isExecutable(): Boolean {
      return Files.isExecutable(`$this$isExecutable`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.isHidden(): Boolean {
      return Files.isHidden(`$this$isHidden`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isReadable(): Boolean {
      return Files.isReadable(`$this$isReadable`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path.isWritable(): Boolean {
      return Files.isWritable(`$this$isWritable`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.isSameFileAs(other: Path): Boolean {
      return Files.isSameFile(`$this$isSameFileAs`, other);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Path.listDirectoryEntries(glob: String): List<Path> {
      label19: {
         var var2: Closeable = Files.newDirectoryStream(`$this$listDirectoryEntries`, glob);
         var var3: java.lang.Throwable = null;

         try {
            try {
               var it: DirectoryStream = var2 as DirectoryStream;
               var var10: java.util.List = CollectionsKt.toList(it);
            } catch (java.lang.Throwable var6) {
               var3 = var6;
               throw var6;
            }
         } catch (java.lang.Throwable var7) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun <T> Path.useDirectoryEntries(glob: String, block: (Sequence<Path>) -> T): T {
      label19: {
         var var3: Closeable = Files.newDirectoryStream(`$this$useDirectoryEntries`, glob);
         var var4: java.lang.Throwable = null;

         try {
            try {
               var it: DirectoryStream = var3 as DirectoryStream;
               it = (DirectoryStream)block.invoke(CollectionsKt.asSequence(it));
            } catch (java.lang.Throwable var7) {
               var4 = var7;
               throw var7;
            }
         } catch (java.lang.Throwable var8) {
            InlineMarker.finallyStart(1);
            CloseableKt.closeFinally(var3, var4);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var3, null);
         InlineMarker.finallyEnd(1);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.forEachDirectoryEntry(glob: String, action: (Path) -> Unit) {
      label28: {
         var var3: Closeable = Files.newDirectoryStream(`$this$forEachDirectoryEntry`, glob);
         var var4: java.lang.Throwable = null;

         try {
            try {
               var it: DirectoryStream = var3 as DirectoryStream;

               var `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  action.invoke(`element$iv`);
               }
            } catch (java.lang.Throwable var11) {
               var4 = var11;
               throw var11;
            }
         } catch (java.lang.Throwable var12) {
            InlineMarker.finallyStart(1);
            CloseableKt.closeFinally(var3, var4);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var3, null);
         InlineMarker.finallyEnd(1);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.fileSize(): Long {
      return Files.size(`$this$fileSize`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.deleteExisting() {
      Files.delete(`$this$deleteExisting`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.deleteIfExists(): Boolean {
      return Files.deleteIfExists(`$this$deleteIfExists`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.createDirectory(vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createDirectory(`$this$createDirectory`, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.createDirectories(vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createDirectories(`$this$createDirectories`, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.9")
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Path.createParentDirectories(vararg attributes: FileAttribute<*>): Path {
      var parent: Path = `$this$createParentDirectories`.getParent();
      if (parent != null) {
         var var10001: Array<LinkOption> = new LinkOption[0];
         if (!Files.isDirectory(parent, Arrays.copyOf(var10001, var10001.length))) {
            try {
               var var9: Array<FileAttribute> = Arrays.copyOf(attributes, attributes.length);
            } catch (FileAlreadyExistsException var7) {
               var10001 = new LinkOption[0];
               if (!Files.isDirectory(parent, Arrays.copyOf(var10001, var10001.length))) {
                  throw var7;
               }
            }
         }
      }

      return `$this$createParentDirectories`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.moveTo(target: Path, vararg options: CopyOption): Path {
      var var10000: Path = Files.move(`$this$moveTo`, target, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.moveTo(target: Path, overwrite: Boolean): Path {
      var var10000: Array<CopyOption> = if (overwrite) new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} else new CopyOption[0];
      var var6: Path = Files.move(`$this$moveTo`, target, Arrays.copyOf(var10000, var10000.length));
      return var6;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.fileStore(): FileStore {
      var var10000: FileStore = Files.getFileStore(`$this$fileStore`);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.getAttribute(attribute: String, vararg options: LinkOption): Any? {
      return Files.getAttribute(`$this$getAttribute`, attribute, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.setAttribute(attribute: String, value: Any?, vararg options: LinkOption): Path {
      var var10000: Path = Files.setAttribute(`$this$setAttribute`, attribute, value, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @PublishedApi
   @JvmStatic
   internal fun fileAttributeViewNotAvailable(path: Path, attributeViewClass: Class<*>): Nothing {
      throw new UnsupportedOperationException("The desired attribute view type " + attributeViewClass + " is not available for the file " + path + '.');
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.readAttributes(attributes: String, vararg options: LinkOption): Map<String, Any?> {
      var var10000: java.util.Map = Files.readAttributes(`$this$readAttributes`, attributes, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.getLastModifiedTime(vararg options: LinkOption): FileTime {
      var var10000: FileTime = Files.getLastModifiedTime(`$this$getLastModifiedTime`, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.setLastModifiedTime(value: FileTime): Path {
      var var10000: Path = Files.setLastModifiedTime(`$this$setLastModifiedTime`, value);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.getOwner(vararg options: LinkOption): UserPrincipal? {
      return Files.getOwner(`$this$getOwner`, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.setOwner(value: UserPrincipal): Path {
      var var10000: Path = Files.setOwner(`$this$setOwner`, value);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.getPosixFilePermissions(vararg options: LinkOption): Set<PosixFilePermission> {
      var var10000: java.util.Set = Files.getPosixFilePermissions(`$this$getPosixFilePermissions`, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.setPosixFilePermissions(value: Set<PosixFilePermission>): Path {
      var var10000: Path = Files.setPosixFilePermissions(`$this$setPosixFilePermissions`, value);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.createLinkPointingTo(target: Path): Path {
      var var10000: Path = Files.createLink(`$this$createLinkPointingTo`, target);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.createSymbolicLinkPointingTo(target: Path, vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createSymbolicLink(`$this$createSymbolicLinkPointingTo`, target, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.readSymbolicLink(): Path {
      var var10000: Path = Files.readSymbolicLink(`$this$readSymbolicLink`);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.createFile(vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createFile(`$this$createFile`, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun createTempFile(prefix: String?, suffix: String?, vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createTempFile(prefix, suffix, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun createTempFile(directory: Path?, prefix: String?, suffix: String?, vararg attributes: FileAttribute<*>): Path {
      var var10000: Path;
      if (directory != null) {
         var10000 = Files.createTempFile(directory, prefix, suffix, Arrays.copyOf(attributes, attributes.length));
      } else {
         var10000 = Files.createTempFile(prefix, suffix, Arrays.copyOf(attributes, attributes.length));
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun createTempDirectory(prefix: String?, vararg attributes: FileAttribute<*>): Path {
      var var10000: Path = Files.createTempDirectory(prefix, Arrays.copyOf(attributes, attributes.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun createTempDirectory(directory: Path?, prefix: String?, vararg attributes: FileAttribute<*>): Path {
      var var10000: Path;
      if (directory != null) {
         var10000 = Files.createTempDirectory(directory, prefix, Arrays.copyOf(attributes, attributes.length));
      } else {
         var10000 = Files.createTempDirectory(prefix, Arrays.copyOf(attributes, attributes.length));
      }

      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun Path.div(other: Path): Path {
      var var10000: Path = `$this$div`.resolve(other);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun Path.div(other: String): Path {
      var var10000: Path = `$this$div`.resolve(other);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path(path: String): Path {
      var var10000: Path = Paths.get(path);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Path(base: String, vararg subpaths: String): Path {
      var var10000: Path = Paths.get(base, Arrays.copyOf(subpaths, subpaths.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun URI.toPath(): Path {
      var var10000: Path = Paths.get(`$this$toPath`);
      return var10000;
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun Path.walk(vararg options: PathWalkOption): Sequence<Path> {
      return new PathTreeWalk(`$this$walk`, options);
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun Path.visitFileTree(visitor: FileVisitor<Path>, maxDepth: Int, followLinks: Boolean) {
      Files.walkFileTree(`$this$visitFileTree`, if (followLinks) SetsKt.setOf(FileVisitOption.FOLLOW_LINKS) else SetsKt.emptySet(), maxDepth, visitor);
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun Path.visitFileTree(maxDepth: Int, followLinks: Boolean, builderAction: (FileVisitorBuilder) -> Unit) {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      PathsKt.visitFileTree(`$this$visitFileTree`, PathsKt.fileVisitor(builderAction), maxDepth, followLinks);
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun fileVisitor(builderAction: (FileVisitorBuilder) -> Unit): FileVisitor<Path> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var1: FileVisitorBuilderImpl = new FileVisitorBuilderImpl();
      builderAction.invoke(var1);
      return var1.build();
   }

   open fun PathsKt__PathUtilsKt() {
   }
}
