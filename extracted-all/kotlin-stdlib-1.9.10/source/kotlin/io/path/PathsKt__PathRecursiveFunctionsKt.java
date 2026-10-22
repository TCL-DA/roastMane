package kotlin.io.path

import java.io.Closeable
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.FileSystemException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.SecureDirectoryStream
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.util.Arrays
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.SpreadBuilder
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nPathRecursiveFunctions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathRecursiveFunctions.kt\nkotlin/io/path/PathsKt__PathRecursiveFunctionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,420:1\n336#1,2:424\n344#1:426\n344#1:427\n338#1,4:428\n336#1,2:432\n344#1:434\n338#1,4:435\n344#1:439\n336#1,6:440\n336#1,2:446\n344#1:448\n338#1,4:449\n1#2:421\n1855#3,2:422\n*S KotlinDebug\n*F\n+ 1 PathRecursiveFunctions.kt\nkotlin/io/path/PathsKt__PathRecursiveFunctionsKt\n*L\n352#1:424,2\n361#1:426\n364#1:427\n352#1:428,4\n372#1:432,2\n373#1:434\n372#1:435,4\n384#1:439\n392#1:440,6\n410#1:446,2\n411#1:448\n410#1:449,4\n274#1:422,2\n*E\n"])
// $VF: Class flags could not be determined
internal class PathsKt__PathRecursiveFunctionsKt : PathsKt__PathReadWriteKt {
   @ExperimentalPathApi
   @SinceKotlin(version = "1.8")
   @JvmStatic
   public fun Path.copyToRecursively(target: Path, onError: (Path, Path, Exception) -> OnErrorResult, followLinks: Boolean, overwrite: Boolean): Path {
      return if (overwrite)
         PathsKt.copyToRecursively(
            `$this$copyToRecursively`, target, onError, followLinks, (new Function3<CopyActionContext, Path, Path, CopyActionResult>(followLinks) {
               {
                  super(3);
                  this.$followLinks = `$followLinks`;
               }

               @NotNull
               public final CopyActionResult invoke(@NotNull CopyActionContext $this$copyToRecursively, @NotNull Path src, @NotNull Path dst) {
                  var options: Array<LinkOption> = LinkFollowing.INSTANCE.toLinkOptions(this.$followLinks);
                  var var7: Array<LinkOption> = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                  var dstIsDirectory: Boolean = Files.isDirectory(dst, Arrays.copyOf(var7, var7.length));
                  var var10001: Array<LinkOption> = Arrays.copyOf(options, options.length);
                  if (!Files.isDirectory(src, Arrays.copyOf(var10001, var10001.length)) || !dstIsDirectory) {
                     if (dstIsDirectory) {
                        PathsKt.deleteRecursively(dst);
                     }

                     var var8: SpreadBuilder = new SpreadBuilder(2);
                     var8.addSpread(options);
                     var8.add(StandardCopyOption.REPLACE_EXISTING);
                     var var9: Array<CopyOption> = var8.toArray(new CopyOption[var8.size()]) as CopyOption[];
                  }

                  return CopyActionResult.CONTINUE;
               }
            }) as Function3<? super CopyActionContext, ? super Path, ? super Path, ? extends CopyActionResult>
         )
         else
         PathsKt.copyToRecursively$default(`$this$copyToRecursively`, target, onError, followLinks, null, 8, null);
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.8")
   @JvmStatic
   public fun Path.copyToRecursively(
      target: Path,
      onError: (Path, Path, Exception) -> OnErrorResult,
      followLinks: Boolean,
      copyAction: (CopyActionContext, Path, Path) -> CopyActionResult
   ): Path {
      var isSubdirectory: Array<LinkOption> = LinkFollowing.INSTANCE.toLinkOptions(followLinks);
      isSubdirectory = Arrays.copyOf(isSubdirectory, isSubdirectory.length);
      if (!Files.exists(`$this$copyToRecursively`, Arrays.copyOf(isSubdirectory, isSubdirectory.length))) {
         throw new NoSuchFileException(`$this$copyToRecursively`.toString(), target.toString(), "The source file doesn't exist.");
      } else {
         var var10001: Array<LinkOption> = new LinkOption[0];
         if (Files.exists(`$this$copyToRecursively`, Arrays.copyOf(var10001, var10001.length))
            && (followLinks || !Files.isSymbolicLink(`$this$copyToRecursively`))) {
            var10001 = new LinkOption[0];
            var targetExistsAndNotSymlink: Boolean = Files.exists(target, Arrays.copyOf(var10001, var10001.length)) && !Files.isSymbolicLink(target);
            if (!targetExistsAndNotSymlink || !Files.isSameFile(`$this$copyToRecursively`, target)) {
               var var11: Boolean;
               if (!(`$this$copyToRecursively`.getFileSystem() == target.getFileSystem())) {
                  var11 = false;
               } else if (targetExistsAndNotSymlink) {
                  var11 = target.toRealPath().startsWith(`$this$copyToRecursively`.toRealPath());
               } else {
                  var var10000: Path = target.getParent();
                  if (var10000 == null) {
                     var11 = false;
                  } else {
                     var10001 = new LinkOption[0];
                     var11 = Files.exists(var10000, Arrays.copyOf(var10001, var10001.length))
                        && var10000.toRealPath().startsWith(`$this$copyToRecursively`.toRealPath());
                  }
               }

               if (var11) {
                  throw new FileSystemException(
                     `$this$copyToRecursively`.toString(), target.toString(), "Recursively copying a directory into its subdirectory is prohibited."
                  );
               }
            }
         }

         PathsKt.visitFileTree$default(
            `$this$copyToRecursively`,
            0,
            followLinks,
            (
               new Function1<FileVisitorBuilder, Unit>(copyAction, `$this$copyToRecursively`, target, onError) {
                  {
                     super(1);
                     this.$copyAction = `$copyAction`;
                     this.$this_copyToRecursively = `$receiver`;
                     this.$target = `$target`;
                     this.$onError = `$onError`;
                  }

                  public final void invoke(@NotNull FileVisitorBuilder $this$visitFileTree) {
                     `$this$visitFileTree`.onPreVisitDirectory(
                        (
                           new Function2<Path, BasicFileAttributes, FileVisitResult>(
                              this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError
                           ) {
                              {
                                 super(
                                    2,
                                    Intrinsics.Kotlin::class.java,
                                    "copy",
                                    "copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/attribute/BasicFileAttributes;)Ljava/nio/file/FileVisitResult;",
                                    0
                                 );
                                 this.$copyAction = `$copyAction`;
                                 this.$this_copyToRecursively = `$receiver`;
                                 this.$target = `$target`;
                                 this.$onError = `$onError`;
                              }

                              @NotNull
                              public final FileVisitResult invoke(@NotNull Path p0, @NotNull BasicFileAttributes p1) {
                                 return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$copy(
                                    this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError, p0, p1
                                 );
                              }
                           }
                        ) as Function2<? super Path, ? super BasicFileAttributes, ? extends FileVisitResult>
                     );
                     `$this$visitFileTree`.onVisitFile(
                        (
                           new Function2<Path, BasicFileAttributes, FileVisitResult>(
                              this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError
                           ) {
                              {
                                 super(
                                    2,
                                    Intrinsics.Kotlin::class.java,
                                    "copy",
                                    "copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/attribute/BasicFileAttributes;)Ljava/nio/file/FileVisitResult;",
                                    0
                                 );
                                 this.$copyAction = `$copyAction`;
                                 this.$this_copyToRecursively = `$receiver`;
                                 this.$target = `$target`;
                                 this.$onError = `$onError`;
                              }

                              @NotNull
                              public final FileVisitResult invoke(@NotNull Path p0, @NotNull BasicFileAttributes p1) {
                                 return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$copy(
                                    this.$copyAction, this.$this_copyToRecursively, this.$target, this.$onError, p0, p1
                                 );
                              }
                           }
                        ) as Function2<? super Path, ? super BasicFileAttributes, ? extends FileVisitResult>
                     );
                     `$this$visitFileTree`.onVisitFileFailed(
                        (
                           new Function2<Path, Exception, FileVisitResult>(this.$onError, this.$this_copyToRecursively, this.$target) {
                              {
                                 super(
                                    2,
                                    Intrinsics.Kotlin::class.java,
                                    "error",
                                    "copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt(Lkotlin/jvm/functions/Function3;Ljava/nio/file/Path;Ljava/nio/file/Path;Ljava/nio/file/Path;Ljava/lang/Exception;)Ljava/nio/file/FileVisitResult;",
                                    0
                                 );
                                 this.$onError = `$onError`;
                                 this.$this_copyToRecursively = `$receiver`;
                                 this.$target = `$target`;
                              }

                              @NotNull
                              public final FileVisitResult invoke(@NotNull Path p0, @NotNull Exception p1) {
                                 return PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$error(
                                    this.$onError, this.$this_copyToRecursively, this.$target, p0, p1
                                 );
                              }
                           }
                        ) as Function2<? super Path, ? super IOException, ? extends FileVisitResult>
                     );
                     `$this$visitFileTree`.onPostVisitDirectory(
                        (
                           new Function2<Path, IOException, FileVisitResult>(this.$onError, this.$this_copyToRecursively, this.$target) {
                              {
                                 super(2);
                                 this.$onError = `$onError`;
                                 this.$this_copyToRecursively = `$receiver`;
                                 this.$target = `$target`;
                              }

                              @NotNull
                              public final FileVisitResult invoke(@NotNull Path directory, @Nullable IOException exception) {
                                 return if (exception == null)
                                    FileVisitResult.CONTINUE
                                    else
                                    PathsKt__PathRecursiveFunctionsKt.access$copyToRecursively$error(
                                       this.$onError, this.$this_copyToRecursively, this.$target, directory, exception
                                    );
                              }
                           }
                        ) as Function2<? super Path, ? super IOException, ? extends FileVisitResult>
                     );
                  }
               }
            ) as Function1,
            1,
            null
         );
         return target;
      }
   }

   @ExperimentalPathApi
   @JvmStatic
   private fun CopyActionResult.toFileVisitResult(): FileVisitResult {
      var var10000: FileVisitResult;
      switch (PathsKt__PathRecursiveFunctionsKt.WhenMappings.$EnumSwitchMapping$0[$this$toFileVisitResult.ordinal()]) {
         case 1:
            var10000 = FileVisitResult.CONTINUE;
            break;
         case 2:
            var10000 = FileVisitResult.TERMINATE;
            break;
         case 3:
            var10000 = FileVisitResult.SKIP_SUBTREE;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @ExperimentalPathApi
   @JvmStatic
   private fun OnErrorResult.toFileVisitResult(): FileVisitResult {
      var var10000: FileVisitResult;
      switch (PathsKt__PathRecursiveFunctionsKt.WhenMappings.$EnumSwitchMapping$1[$this$toFileVisitResult.ordinal()]) {
         case 1:
            var10000 = FileVisitResult.TERMINATE;
            break;
         case 2:
            var10000 = FileVisitResult.SKIP_SUBTREE;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @ExperimentalPathApi
   @SinceKotlin(version = "1.8")
   @JvmStatic
   public fun Path.deleteRecursively() {
      if (!deleteRecursivelyImpl$PathsKt__PathRecursiveFunctionsKt(`$this$deleteRecursively`).isEmpty()) {
         var var2: FileSystemException = new FileSystemException("Failed to delete one or more files. See suppressed exceptions for details.");
         var `$this$deleteRecursively_u24lambda_u242`: FileSystemException = var2;

         var `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            ExceptionsKt.addSuppressed(`$this$deleteRecursively_u24lambda_u242`, `element$iv` as Exception);
         }

         throw var2 as java.lang.Throwable;
      }
   }

   @JvmStatic
   private fun Path.deleteRecursivelyImpl(): List<Exception> {
      label45: {
         var collector: ExceptionsCollector = new ExceptionsCollector(0, 1, null);
         var var16: Boolean = true;
         var var10000: Path = `$this$deleteRecursivelyImpl`.getParent();
         if (var10000 != null) {
            var parent: Path = var10000;

            var var5: DirectoryStream;
            try {
               var5 = Files.newDirectoryStream(parent);
            } catch (java.lang.Throwable var10) {
               var5 = null;
            }

            if (var5 != null) {
               var var17: Closeable = var5;
               var _: java.lang.Throwable = null;

               try {
                  try {
                     var stream: DirectoryStream = var17 as DirectoryStream;
                     if (var17 as DirectoryStream is SecureDirectoryStream) {
                        var16 = false;
                        collector.setPath(parent);
                        var var19: SecureDirectoryStream = stream as SecureDirectoryStream;
                        var var10001: Path = `$this$deleteRecursivelyImpl`.getFileName();
                        handleEntry$PathsKt__PathRecursiveFunctionsKt(var19, var10001, collector);
                     }
                  } catch (java.lang.Throwable var11) {
                     _ = var11;
                     throw var11;
                  }
               } catch (java.lang.Throwable var12) {
                  CloseableKt.closeFinally(var17, _);
               }

               CloseableKt.closeFinally(var17, null);
            }
         }

         if (var16) {
            insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt(`$this$deleteRecursivelyImpl`, collector);
         }

         return collector.getCollectedExceptions();
      }
   }

   @JvmStatic
   private inline fun collectIfThrows(collector: ExceptionsCollector, function: () -> Unit) {
      try {
         function.invoke();
      } catch (Exception var4) {
         collector.collect(var4);
      }
   }

   @JvmStatic
   private inline fun <R> tryIgnoreNoSuchFileException(function: () -> R): R? {
      var var2: Any;
      try {
         var2 = function.invoke();
      } catch (NoSuchFileException var4) {
         var2 = null;
      }

      return (R)var2;
   }

   @JvmStatic
   private fun SecureDirectoryStream<Path>.handleEntry(name: Path, collector: ExceptionsCollector) {
      collector.enterEntry(name);

      try {
         if (isDirectory$PathsKt__PathRecursiveFunctionsKt(`$this$handleEntry`, name, LinkOption.NOFOLLOW_LINKS)) {
            var var14: Int = collector.getTotalExceptions();
            enterDirectory$PathsKt__PathRecursiveFunctionsKt(`$this$handleEntry`, name, collector);
            if (var14 == collector.getTotalExceptions()) {
               try {
                  `$this$handleEntry`.deleteDirectory(name);
               } catch (NoSuchFileException var12) {
               }
            }
         } else {
            try {
               `$this$handleEntry`.deleteFile(name);
            } catch (NoSuchFileException var11) {
            }
         }
      } catch (Exception var13) {
         collector.collect(var13);
      }

      collector.exitEntry(name);
   }

   @JvmStatic
   private fun SecureDirectoryStream<Path>.enterDirectory(name: Path, collector: ExceptionsCollector) {
      label50: {
         try {
            var var8: SecureDirectoryStream;
            try {
               var8 = `$this$enterDirectory`.newDirectoryStream(name, LinkOption.NOFOLLOW_LINKS);
            } catch (NoSuchFileException var13) {
               var8 = null;
            }

            if (var8 != null) {
               var var21: Closeable = var8;
               var var22: java.lang.Throwable = null;

               try {
                  try {
                     var var23: SecureDirectoryStream = var21 as SecureDirectoryStream;

                     for (Path entry : directoryStream) {
                        var var10001: Path = entry.getFileName();
                        handleEntry$PathsKt__PathRecursiveFunctionsKt(var23, var10001, collector);
                     }
                  } catch (java.lang.Throwable var14) {
                     var22 = var14;
                     throw var14;
                  }
               } catch (java.lang.Throwable var15) {
                  CloseableKt.closeFinally(var21, var22);
               }

               CloseableKt.closeFinally(var21, null);
            }
         } catch (Exception var16) {
            collector.collect(var16);
         }
      }
   }

   @JvmStatic
   private fun SecureDirectoryStream<Path>.isDirectory(entryName: Path, vararg options: LinkOption): Boolean {
      var var5: java.lang.Boolean;
      try {
         var5 = `$this$isDirectory`.getFileAttributeView(entryName, BasicFileAttributeView.class, Arrays.copyOf(options, options.length))
            .readAttributes()
            .isDirectory();
      } catch (NoSuchFileException var7) {
         var5 = null;
      }

      return var5 != null && var5;
   }

   @JvmStatic
   private fun insecureHandleEntry(entry: Path, collector: ExceptionsCollector) {
      try {
         var var5: Array<LinkOption> = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
         if (Files.isDirectory(entry, Arrays.copyOf(var5, var5.length))) {
            var preEnterTotalExceptions: Int = collector.getTotalExceptions();
            insecureEnterDirectory$PathsKt__PathRecursiveFunctionsKt(entry, collector);
            if (preEnterTotalExceptions == collector.getTotalExceptions()) {
               Files.deleteIfExists(entry);
            }
         } else {
            Files.deleteIfExists(entry);
         }
      } catch (Exception var7) {
         collector.collect(var7);
      }
   }

   @JvmStatic
   private fun insecureEnterDirectory(path: Path, collector: ExceptionsCollector) {
      label50: {
         try {
            var directoryStream: DirectoryStream;
            try {
               directoryStream = Files.newDirectoryStream(path);
            } catch (NoSuchFileException var12) {
               directoryStream = null;
            }

            if (directoryStream != null) {
               var var20: Closeable = directoryStream;
               var var21: java.lang.Throwable = null;

               try {
                  try {
                     for (Path entry : directoryStream) {
                        insecureHandleEntry$PathsKt__PathRecursiveFunctionsKt(entry, collector);
                     }
                  } catch (java.lang.Throwable var13) {
                     var21 = var13;
                     throw var13;
                  }
               } catch (java.lang.Throwable var14) {
                  CloseableKt.closeFinally(var20, var21);
               }

               CloseableKt.closeFinally(var20, null);
            }
         } catch (Exception var15) {
            collector.collect(var15);
         }
      }
   }

   @JvmStatic
   fun `copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt`(`$this_copyToRecursively`: Path, `$target`: Path, source: Path): Path {
      var var10000: Path = `$target`.resolve(PathsKt.relativeTo(source, `$this_copyToRecursively`).toString());
      return var10000;
   }

   @JvmStatic
   fun `copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt`(
      `$onError`: (Path?, Path?, Exception?) -> OnErrorResult, `$this_copyToRecursively`: Path, `$target`: Path, source: Path, exception: Exception
   ): FileVisitResult {
      return toFileVisitResult$PathsKt__PathRecursiveFunctionsKt(
         `$onError`.invoke(source, copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt(`$this_copyToRecursively`, `$target`, source), exception) as OnErrorResult
      );
   }

   @JvmStatic
   fun `copyToRecursively$copy$PathsKt__PathRecursiveFunctionsKt`(
      `$copyAction`: (CopyActionContext?, Path?, Path?) -> CopyActionResult,
      `$this_copyToRecursively`: Path,
      `$target`: Path,
      `$onError`: (Path?, Path?, Exception?) -> OnErrorResult,
      source: Path,
      attributes: BasicFileAttributes
   ): FileVisitResult {
      var var6: FileVisitResult;
      try {
         var6 = toFileVisitResult$PathsKt__PathRecursiveFunctionsKt(
            `$copyAction`.invoke(
               DefaultCopyActionContext.INSTANCE,
               source,
               copyToRecursively$destination$PathsKt__PathRecursiveFunctionsKt(`$this_copyToRecursively`, `$target`, source)
            ) as CopyActionResult
         );
      } catch (Exception var8) {
         var6 = copyToRecursively$error$PathsKt__PathRecursiveFunctionsKt(`$onError`, `$this_copyToRecursively`, `$target`, source, var8);
      }

      return var6;
   }

   open fun PathsKt__PathRecursiveFunctionsKt() {
   }
}
