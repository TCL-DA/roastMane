package okio

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.OpenOption
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.FileAttribute
import java.util.ArrayList
import java.util.Arrays
import kotlin.io.path.PathsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nNioFileSystemWrappingFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,192:1\n1620#2,3:193\n1#3:196\n37#4,2:197\n37#4,2:199\n37#4,2:201\n*S KotlinDebug\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n*L\n77#1:193,3\n104#1:197,2\n125#1:199,2\n138#1:201,2\n*E\n"])
internal class NioFileSystemWrappingFileSystem(nioFileSystem: java.nio.file.FileSystem) : NioSystemFileSystem {
   private final val nioFileSystem: java.nio.file.FileSystem

   init {
      this.nioFileSystem = nioFileSystem;
   }

   private fun Path.resolve(): java.nio.file.Path {
      var var10000: java.nio.file.Path = this.nioFileSystem.getPath(`$this$resolve`.toString());
      return var10000;
   }

   public override fun canonicalize(path: Path): Path {
      try {
         var var10000: Path.Companion = Path.Companion;
         var var10001: java.nio.file.Path = this.resolve(path).toRealPath();
         return Path.Companion.get$default(var10000, var10001, false, 1, null);
      } catch (NoSuchFileException var3) {
         throw new FileNotFoundException("no such file: " + path);
      }
   }

   public override fun metadataOrNull(path: Path): FileMetadata? {
      return this.metadataOrNull(this.resolve(path));
   }

   public override fun list(dir: Path): List<Path> {
      var var10000: java.util.List = this.list(dir, true);
      return var10000;
   }

   public override fun listOrNull(dir: Path): List<Path>? {
      return this.list(dir, false);
   }

   private fun list(dir: Path, throwOnFailure: Boolean): List<Path>? {
      var nioDir: java.nio.file.Path = this.resolve(dir);

      var result: java.util.List;
      try {
         result = PathsKt.listDirectoryEntries$default(nioDir, null, 1, null);
      } catch (Exception var14) {
         if (throwOnFailure) {
            var var10001: Array<LinkOption> = new LinkOption[0];
            if (!Files.exists(nioDir, Arrays.copyOf(var10001, var10001.length))) {
               throw new FileNotFoundException("no such file: " + dir);
            }

            throw new IOException("failed to list " + dir);
         }

         return null;
      }

      var `$this$mapTo$iv`: java.lang.Iterable = result;
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object item$iv : $this$mapTo$iv) {
         `destination$iv`.add(Path.Companion.get$default(Path.Companion, `item$iv` as java.nio.file.Path, false, 1, null));
      }

      result = `destination$iv` as java.util.List;
      CollectionsKt.sort(`destination$iv` as java.util.List);
      return result;
   }

   public override fun openReadOnly(file: Path): FileHandle {
      var var6: FileChannel;
      try {
         var6 = FileChannel.open(this.resolve(file), StandardOpenOption.READ);
      } catch (NoSuchFileException var5) {
         throw new FileNotFoundException("no such file: " + file);
      }

      return new NioFileSystemFileHandle(false, var6);
   }

   public override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
      if (mustCreate && mustExist) {
         throw new IllegalArgumentException("Cannot require mustCreate and mustExist at the same time.".toString());
      } else {
         var channel: java.util.List = CollectionsKt.createListBuilder();
         channel.add(StandardOpenOption.READ);
         channel.add(StandardOpenOption.WRITE);
         if (mustCreate) {
            channel.add(StandardOpenOption.CREATE_NEW);
         } else if (!mustExist) {
            channel.add(StandardOpenOption.CREATE);
         }

         var openOptions: java.util.List = CollectionsKt.build(channel);

         var var13: FileChannel;
         try {
            var var10000: java.nio.file.Path = this.resolve(file);
            var `$this$openReadWrite_u24lambda_u242`: Array<StandardOpenOption> = openOptions.toArray(new StandardOpenOption[0]);
            var13 = FileChannel.open(var10000, Arrays.copyOf(`$this$openReadWrite_u24lambda_u242`, `$this$openReadWrite_u24lambda_u242`.length));
         } catch (NoSuchFileException var10) {
            throw new FileNotFoundException("no such file: " + file);
         }

         return new NioFileSystemFileHandle(true, var13);
      }
   }

   public override fun source(file: Path): Source {
      try {
         var var10000: java.nio.file.Path = this.resolve(file);
         var var10001: Array<OpenOption> = new OpenOption[0];
         var var4: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var10001, var10001.length));
         return Okio.source(var4);
      } catch (NoSuchFileException var3) {
         throw new FileNotFoundException("no such file: " + file);
      }
   }

   public override fun sink(file: Path, mustCreate: Boolean): Sink {
      var e: java.util.List = CollectionsKt.createListBuilder();
      if (mustCreate) {
         e.add(StandardOpenOption.CREATE_NEW);
      }

      var openOptions: java.util.List = CollectionsKt.build(e);

      try {
         var var10: java.nio.file.Path = this.resolve(file);
         var `$this$sink_u24lambda_u243`: Array<StandardOpenOption> = openOptions.toArray(new StandardOpenOption[0]);
         var var11: Array<OpenOption> = Arrays.copyOf(`$this$sink_u24lambda_u243`, `$this$sink_u24lambda_u243`.length);
         var var10000: OutputStream = Files.newOutputStream(var10, Arrays.copyOf(var11, var11.length));
         return Okio.sink(var10000);
      } catch (NoSuchFileException var9) {
         throw new FileNotFoundException("no such file: " + file);
      }
   }

   public override fun appendingSink(file: Path, mustExist: Boolean): Sink {
      var var4: java.util.List = CollectionsKt.createListBuilder();
      var4.add(StandardOpenOption.APPEND);
      if (!mustExist) {
         var4.add(StandardOpenOption.CREATE);
      }

      var openOptions: java.util.List = CollectionsKt.build(var4);
      var var9: java.nio.file.Path = this.resolve(file);
      var `$this$appendingSink_u24lambda_u244`: Array<StandardOpenOption> = openOptions.toArray(new StandardOpenOption[0]);
      var var10: Array<OpenOption> = Arrays.copyOf(`$this$appendingSink_u24lambda_u244`, `$this$appendingSink_u24lambda_u244`.length);
      var var10000: OutputStream = Files.newOutputStream(var9, Arrays.copyOf(var10, var10.length));
      return Okio.sink(var10000);
   }

   public override fun createDirectory(dir: Path, mustCreate: Boolean) {
      var var10000: FileMetadata = this.metadataOrNull(dir);
      var alreadyExist: Boolean = var10000 != null && var10000.isDirectory();
      if (alreadyExist && mustCreate) {
         throw new IOException(dir + " already exists.");
      } else {
         try {
            var var6: java.nio.file.Path = this.resolve(dir);
            var var10001: Array<FileAttribute> = new FileAttribute[0];
         } catch (IOException var5) {
            if (!alreadyExist) {
               throw new IOException("failed to create directory: " + dir, var5);
            }
         }
      }
   }

   public override fun atomicMove(source: Path, target: Path) {
      try {
         var e: java.nio.file.Path = this.resolve(source);
         var var4: java.nio.file.Path = this.resolve(target);
         var var5: Array<CopyOption> = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING};
      } catch (NoSuchFileException var6) {
         throw new FileNotFoundException(var6.getMessage());
      } catch (UnsupportedOperationException var7) {
         throw new IOException("atomic move not supported");
      }
   }

   public override fun delete(path: Path, mustExist: Boolean) {
      if (Thread.interrupted()) {
         throw new InterruptedIOException("interrupted");
      } else {
         var nioPath: java.nio.file.Path = this.resolve(path);

         try {
            Files.delete(nioPath);
         } catch (NoSuchFileException var5) {
            if (mustExist) {
               throw new FileNotFoundException("no such file: " + path);
            }
         } catch (IOException var6) {
            var var10001: Array<LinkOption> = new LinkOption[0];
            if (Files.exists(nioPath, Arrays.copyOf(var10001, var10001.length))) {
               throw new IOException("failed to delete " + path);
            }
         }
      }
   }

   public override fun createSymlink(source: Path, target: Path) {
      var var10000: java.nio.file.Path = this.resolve(source);
      var var10001: java.nio.file.Path = this.resolve(target);
      var var10002: Array<FileAttribute> = new FileAttribute[0];
   }

   public override fun toString(): String {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.vineflower.kotlin.expr.KInvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.FieldExprent (org.vineflower.kotlin.expr.KInvocationExprent is in unnamed module of loader org.jetbrains.java.decompiler.main.plugins.InJarClassLoader @707f7052; org.jetbrains.java.decompiler.modules.decompiler.exps.FieldExprent is in unnamed module of loader 'app')
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:96)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:810)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1018)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:154)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield okio/NioFileSystemWrappingFileSystem.nioFileSystem Ljava/nio/file/FileSystem;
      // 04: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 07: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // 0a: invokeinterface kotlin/reflect/KClass.getSimpleName ()Ljava/lang/String; 1
      // 0f: dup
      // 10: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 13: areturn
   }
}
