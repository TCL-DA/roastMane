package okio.internal

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.Enumeration
import java.util.LinkedHashSet
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import okio.FileHandle
import okio.FileMetadata
import okio.FileSystem
import okio.Okio
import okio.Path
import okio.Sink
import okio.Source
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nResourceFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,210:1\n766#2:211\n857#2,2:212\n1549#2:214\n1620#2,3:215\n766#2:218\n857#2,2:219\n1549#2:221\n1620#2,3:222\n1603#2,9:225\n1855#2:234\n1856#2:236\n1612#2:237\n1603#2,9:238\n1855#2:247\n1856#2:249\n1612#2:250\n1#3:235\n1#3:248\n*S KotlinDebug\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n*L\n74#1:211\n74#1:212,2\n75#1:214\n75#1:215,3\n90#1:218\n90#1:219,2\n91#1:221\n91#1:222,3\n173#1:225,9\n173#1:234\n173#1:236\n173#1:237\n174#1:238,9\n174#1:247\n174#1:249\n174#1:250\n173#1:235\n174#1:248\n*E\n"])
internal class ResourceFileSystem internal constructor(classLoader: ClassLoader, indexEagerly: Boolean, systemFileSystem: FileSystem) : FileSystem {
   @JvmStatic
   private ResourceFileSystem.Companion Companion = new ResourceFileSystem.Companion(null);
   @JvmStatic
   private Path ROOT = Path.Companion.get$default(Path.Companion, "/", false, 1, null);

   private final val classLoader: ClassLoader
   private final val roots: List<Pair<FileSystem, Path>>
      private final get() {
         return this.roots$delegate.getValue() as java.util.List<Pair<FileSystem, Path>>;
      }

   private final val systemFileSystem: FileSystem

   init {
      this.classLoader = classLoader;
      this.systemFileSystem = systemFileSystem;
      this.roots$delegate = LazyKt.lazy((new Function0<java.util.List<? extends Pair<? extends FileSystem, ? extends Path>>>(this) {
         {
            super(0);
            this.this$0 = `$receiver`;
         }

         @NotNull
         public final java.util.List<Pair<FileSystem, Path>> invoke() {
            return ResourceFileSystem.access$toClasspathRoots(this.this$0, ResourceFileSystem.access$getClassLoader$p(this.this$0));
         }
      }) as Function0);
      if (indexEagerly) {
         this.getRoots().size();
      }
   }

   public override fun canonicalize(path: Path): Path {
      return this.canonicalizeInternal(path);
   }

   private fun canonicalizeInternal(path: Path): Path {
      return ROOT.resolve(path, true);
   }

   public override fun list(dir: Path): List<Path> {
      var relativePath: java.lang.String = this.toRelativePath(dir);
      var result: java.util.Set = new LinkedHashSet();
      var foundAny: Boolean = false;

      for (Pair var6 : this.getRoots()) {
         var fileSystem: FileSystem = var6.component1() as FileSystem;
         var base: Path = var6.component2() as Path;

         try {
            var var9: java.util.Collection = result;
            var `$this$map$iv`: java.lang.Iterable = fileSystem.list(base.resolve(relativePath));
            var `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$map$iv) {
               if (ResourceFileSystem.Companion.access$keepPath(Companion, `item$iv$iv` as Path)) {
                  `destination$iv$iv`.add(`item$iv$iv`);
               }
            }

            `$this$map$iv` = `destination$iv$iv` as java.util.List;
            `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(Companion.removeBase(var27 as Path, base));
            }

            CollectionsKt.addAll(var9, `destination$iv$iv` as java.util.List);
            foundAny = true;
         } catch (IOException var20) {
         }
      }

      if (!foundAny) {
         throw new FileNotFoundException("file not found: " + dir);
      } else {
         return CollectionsKt.toList(result);
      }
   }

   public override fun listOrNull(dir: Path): List<Path>? {
      var relativePath: java.lang.String = this.toRelativePath(dir);
      var result: java.util.Set = new LinkedHashSet();
      var foundAny: Boolean = false;

      for (Pair var6 : this.getRoots()) {
         var fileSystem: FileSystem = var6.component1() as FileSystem;
         var base: Path = var6.component2() as Path;
         var var10: java.util.List = fileSystem.listOrNull(base.resolve(relativePath));
         var var10000: java.util.List;
         if (var10 == null) {
            var10000 = null;
         } else {
            var `$this$map$iv`: java.lang.Iterable = var10;
            var `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$map$iv) {
               if (ResourceFileSystem.Companion.access$keepPath(Companion, `item$iv$iv` as Path)) {
                  `destination$iv$iv`.add(`item$iv$iv`);
               }
            }

            `$this$map$iv` = `destination$iv$iv` as java.util.List;
            `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(Companion.removeBase(var26 as Path, base));
            }

            var10000 = `destination$iv$iv` as java.util.List;
         }

         if (var10000 != null) {
            CollectionsKt.addAll(result, var10000);
            foundAny = true;
         }
      }

      return if (foundAny) CollectionsKt.toList(result) else null;
   }

   public override fun openReadOnly(file: Path): FileHandle {
      if (!ResourceFileSystem.Companion.access$keepPath(Companion, file)) {
         throw new FileNotFoundException("file not found: " + file);
      } else {
         var relativePath: java.lang.String = this.toRelativePath(file);

         for (Pair var4 : this.getRoots()) {
            var fileSystem: FileSystem = var4.component1() as FileSystem;
            var base: Path = var4.component2() as Path;

            try {
               return fileSystem.openReadOnly(base.resolve(relativePath));
            } catch (FileNotFoundException var8) {
            }
         }

         throw new FileNotFoundException("file not found: " + file);
      }
   }

   public override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
      throw new IOException("resources are not writable");
   }

   public override fun metadataOrNull(path: Path): FileMetadata? {
      if (!ResourceFileSystem.Companion.access$keepPath(Companion, path)) {
         return null;
      } else {
         var relativePath: java.lang.String = this.toRelativePath(path);

         for (Pair var4 : this.getRoots()) {
            var var10000: FileMetadata = (var4.component1() as FileSystem).metadataOrNull((var4.component2() as Path).resolve(relativePath));
            if (var10000 != null) {
               return var10000;
            }
         }

         return null;
      }
   }

   public override fun source(file: Path): Source {
      if (!ResourceFileSystem.Companion.access$keepPath(Companion, file)) {
         throw new FileNotFoundException("file not found: " + file);
      } else {
         var var10000: InputStream = this.classLoader.getResourceAsStream(Path.resolve$default(ROOT, file, false, 2, null).relativeTo(ROOT).toString());
         if (var10000 != null) {
            var var3: Source = Okio.source(var10000);
            if (var3 != null) {
               return var3;
            }
         }

         throw new FileNotFoundException("file not found: " + file);
      }
   }

   public override fun sink(file: Path, mustCreate: Boolean): Sink {
      throw new IOException(this + " is read-only");
   }

   public override fun appendingSink(file: Path, mustExist: Boolean): Sink {
      throw new IOException(this + " is read-only");
   }

   public override fun createDirectory(dir: Path, mustCreate: Boolean) {
      throw new IOException(this + " is read-only");
   }

   public override fun atomicMove(source: Path, target: Path) {
      throw new IOException(this + " is read-only");
   }

   public override fun delete(path: Path, mustExist: Boolean) {
      throw new IOException(this + " is read-only");
   }

   public override fun createSymlink(source: Path, target: Path) {
      throw new IOException(this + " is read-only");
   }

   private fun Path.toRelativePath(): String {
      return this.canonicalizeInternal(`$this$toRelativePath`).relativeTo(ROOT).toString();
   }

   private fun ClassLoader.toClasspathRoots(): List<Pair<FileSystem, Path>> {
      var var10000: Enumeration = `$this$toClasspathRoots`.getResources("");
      var var30: ArrayList = Collections.list(var10000);
      var `$this$mapNotNull$iv`: java.lang.Iterable = var30;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
         var it: URL = `element$iv$iv$iv` as URL;
         var var31: Pair = this.toFileRoot(it);
         if (var31 != null) {
            `destination$iv$iv`.add(var31);
         }
      }

      var var32: java.util.Collection = `destination$iv$iv` as java.util.List;
      var var10001: Enumeration = `$this$toClasspathRoots`.getResources("META-INF/MANIFEST.MF");
      var var34: ArrayList = Collections.list(var10001);
      `$this$mapNotNull$iv` = var34;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv$ivx : $this$mapNotNull$iv) {
         var var26: URL = `element$iv$iv$ivx` as URL;
         var var33: Pair = this.toJarRoot(var26);
         if (var33 != null) {
            `destination$iv$iv`.add(var33);
         }
      }

      return CollectionsKt.plus(var32, `destination$iv$iv` as java.util.List);
   }

   private fun URL.toFileRoot(): Pair<FileSystem, Path>? {
      return if (!(`$this$toFileRoot`.getProtocol() == "file"))
         null
         else
         TuplesKt.to(this.systemFileSystem, Path.Companion.get$default(Path.Companion, new File(`$this$toFileRoot`.toURI()), false, 1, null));
   }

   private fun URL.toJarRoot(): Pair<FileSystem, Path>? {
      var var10000: java.lang.String = `$this$toJarRoot`.toString();
      if (!StringsKt.startsWith$default(var10000, "jar:file:", false, 2, null)) {
         return null;
      } else {
         var suffixStart: Int = StringsKt.lastIndexOf$default(var10000, "!", 0, false, 6, null);
         if (suffixStart == -1) {
            return null;
         } else {
            var var6: Path.Companion = Path.Companion;
            var var10003: java.lang.String = var10000.substring(4, suffixStart);
            return TuplesKt.to(
               ZipFilesKt.openZip(
                  Path.Companion.get$default(var6, new File(URI.create(var10003)), false, 1, null), this.systemFileSystem, <unrepresentable>.INSTANCE
               ),
               ROOT
            );
         }
      }
   }

   private companion object Companion private constructor() {
      public final val ROOT: Path


      public fun Path.removeBase(base: Path): Path {
         return this.getROOT()
            .resolve(StringsKt.replace$default(StringsKt.removePrefix(`$this$removeBase`.toString(), base.toString()), '\\', '/', false, 4, null));
      }

      private fun keepPath(path: Path): Boolean {
         return !StringsKt.endsWith(path.name(), ".class", true);
      }
   }
}
