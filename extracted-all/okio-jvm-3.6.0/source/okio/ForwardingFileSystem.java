package okio

import java.util.ArrayList
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nForwardingFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ForwardingFileSystem.kt\nokio/ForwardingFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,243:1\n1620#2,3:244\n1620#2,3:247\n*S KotlinDebug\n*F\n+ 1 ForwardingFileSystem.kt\nokio/ForwardingFileSystem\n*L\n166#1:244,3\n174#1:247,3\n*E\n"])
abstract class ForwardingFileSystem : FileSystem {
   public final val delegate: FileSystem

   open fun ForwardingFileSystem(delegate: FileSystem) {
      this.delegate = delegate;
   }

   public open fun onPathParameter(path: Path, functionName: String, parameterName: String): Path {
      return path;
   }

   public open fun onPathResult(path: Path, functionName: String): Path {
      return path;
   }

   @Throws(java/io/IOException::class)
   public override fun canonicalize(path: Path): Path {
      return this.onPathResult(this.delegate.canonicalize(this.onPathParameter(path, "canonicalize", "path")), "canonicalize");
   }

   @Throws(java/io/IOException::class)
   public override fun metadataOrNull(path: Path): FileMetadata? {
      var var10000: FileMetadata = this.delegate.metadataOrNull(this.onPathParameter(path, "metadataOrNull", "path"));
      label11:
      if (var10000 == null) {
         return null;
      } else {
         return if (var10000.getSymlinkTarget() == null)
            var10000
            else
            FileMetadata.copy$default(
               var10000, false, false, this.onPathResult(var10000.getSymlinkTarget(), "metadataOrNull"), null, null, null, null, null, 251, null
            );
      }
   }

   @Throws(java/io/IOException::class)
   public override fun list(dir: Path): List<Path> {
      var `$this$mapTo$iv`: java.lang.Iterable = this.delegate.list(this.onPathParameter(dir, "list", "dir"));
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object item$iv : $this$mapTo$iv) {
         `destination$iv`.add(this.onPathResult(`item$iv` as Path, "list"));
      }

      var paths: java.util.List = `destination$iv` as java.util.List;
      CollectionsKt.sort(`destination$iv` as java.util.List);
      return paths;
   }

   public override fun listOrNull(dir: Path): List<Path>? {
      var var10000: java.util.List = this.delegate.listOrNull(this.onPathParameter(dir, "listOrNull", "dir"));
      if (var10000 == null) {
         return null;
      } else {
         var `$this$mapTo$iv`: java.lang.Iterable = var10000;
         var `destination$iv`: java.util.Collection = new ArrayList();

         for (Object item$iv : $this$mapTo$iv) {
            `destination$iv`.add(this.onPathResult(`item$iv` as Path, "listOrNull"));
         }

         var paths: java.util.List = `destination$iv` as java.util.List;
         CollectionsKt.sort(`destination$iv` as java.util.List);
         return paths;
      }
   }

   public override fun listRecursively(dir: Path, followSymlinks: Boolean): Sequence<Path> {
      return SequencesKt.map(
         this.delegate.listRecursively(this.onPathParameter(dir, "listRecursively", "dir"), followSymlinks), (new Function1<Path, Path>(this) {
            {
               super(1);
               this.this$0 = `$receiver`;
            }

            @NotNull
            public final Path invoke(@NotNull Path it) {
               return this.this$0.onPathResult(it, "listRecursively");
            }
         }) as Function1
      );
   }

   @Throws(java/io/IOException::class)
   public override fun openReadOnly(file: Path): FileHandle {
      return this.delegate.openReadOnly(this.onPathParameter(file, "openReadOnly", "file"));
   }

   @Throws(java/io/IOException::class)
   public override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
      return this.delegate.openReadWrite(this.onPathParameter(file, "openReadWrite", "file"), mustCreate, mustExist);
   }

   @Throws(java/io/IOException::class)
   public override fun source(file: Path): Source {
      return this.delegate.source(this.onPathParameter(file, "source", "file"));
   }

   @Throws(java/io/IOException::class)
   public override fun sink(file: Path, mustCreate: Boolean): Sink {
      return this.delegate.sink(this.onPathParameter(file, "sink", "file"), mustCreate);
   }

   @Throws(java/io/IOException::class)
   public override fun appendingSink(file: Path, mustExist: Boolean): Sink {
      return this.delegate.appendingSink(this.onPathParameter(file, "appendingSink", "file"), mustExist);
   }

   @Throws(java/io/IOException::class)
   public override fun createDirectory(dir: Path, mustCreate: Boolean) {
      this.delegate.createDirectory(this.onPathParameter(dir, "createDirectory", "dir"), mustCreate);
   }

   @Throws(java/io/IOException::class)
   public override fun atomicMove(source: Path, target: Path) {
      this.delegate.atomicMove(this.onPathParameter(source, "atomicMove", "source"), this.onPathParameter(target, "atomicMove", "target"));
   }

   @Throws(java/io/IOException::class)
   public override fun delete(path: Path, mustExist: Boolean) {
      this.delegate.delete(this.onPathParameter(path, "delete", "path"), mustExist);
   }

   @Throws(java/io/IOException::class)
   public override fun createSymlink(source: Path, target: Path) {
      this.delegate.createSymlink(this.onPathParameter(source, "createSymlink", "source"), this.onPathParameter(target, "createSymlink", "target"));
   }

   public override fun toString(): String {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.vineflower.kotlin.expr.KInvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.FieldExprent (org.vineflower.kotlin.expr.KInvocationExprent is in unnamed module of loader org.jetbrains.java.decompiler.main.plugins.InJarClassLoader @707f7052; org.jetbrains.java.decompiler.modules.decompiler.exps.FieldExprent is in unnamed module of loader 'app')
      //   at org.vineflower.kotlin.expr.KFunctionExprent.toJava(KFunctionExprent.java:96)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:810)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:745)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.toJava(FunctionExprent.java:587)
      //
      // Bytecode:
      // 00: new java/lang/StringBuilder
      // 03: dup
      // 04: invokespecial java/lang/StringBuilder.<init> ()V
      // 07: aload 0
      // 08: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 0b: invokestatic kotlin/jvm/internal/Reflection.getOrCreateKotlinClass (Ljava/lang/Class;)Lkotlin/reflect/KClass;
      // 0e: invokeinterface kotlin/reflect/KClass.getSimpleName ()Ljava/lang/String; 1
      // 13: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 16: bipush 40
      // 18: invokevirtual java/lang/StringBuilder.append (C)Ljava/lang/StringBuilder;
      // 1b: aload 0
      // 1c: getfield okio/ForwardingFileSystem.delegate Lokio/FileSystem;
      // 1f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      // 22: bipush 41
      // 24: invokevirtual java/lang/StringBuilder.append (C)Ljava/lang/StringBuilder;
      // 27: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 2a: areturn
   }
}
