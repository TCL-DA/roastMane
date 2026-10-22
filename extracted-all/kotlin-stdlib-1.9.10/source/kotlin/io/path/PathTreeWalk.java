package kotlin.io.path

import java.nio.file.FileSystemLoopException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@ExperimentalPathApi
internal class PathTreeWalk(start: Path, vararg options: Any) : Sequence<Path> {
   private PathWalkOption[] options;

   private final val followLinks: Boolean
      private final get() {
         return ArraysKt.contains(this.options, PathWalkOption.FOLLOW_LINKS);
      }

   private final val includeDirectories: Boolean
      private final get() {
         return ArraysKt.contains(this.options, PathWalkOption.INCLUDE_DIRECTORIES);
      }

   private final val isBFS: Boolean
      private final get() {
         return ArraysKt.contains(this.options, PathWalkOption.BREADTH_FIRST);
      }

   private final val linkOptions: Array<LinkOption>
      private final get() {
         return LinkFollowing.INSTANCE.toLinkOptions(this.getFollowLinks());
      }

   private final val options: Array<out PathWalkOption>
   private final val start: Path

   init {
      this.start = start;
      this.options = options;
   }

   public override operator fun iterator(): Iterator<Path> {
      return if (this.isBFS()) this.bfsIterator() else this.dfsIterator();
   }

   private suspend inline fun SequenceScope<Path>.yieldIfNeeded(node: PathNode, entriesReader: DirectoryEntriesReader, entriesAction: (List<PathNode>) -> Unit) {
      var path: Path = node.getPath();
      var var9: Array<LinkOption> = access$getLinkOptions(this);
      var9 = Arrays.copyOf(var9, var9.length);
      if (Files.isDirectory(path, Arrays.copyOf(var9, var9.length))) {
         if (PathTreeWalkKt.access$createsCycle(node)) {
            throw new FileSystemLoopException(path.toString());
         }

         if (access$getIncludeDirectories(this)) {
            InlineMarker.mark(0);
            `$this$yieldIfNeeded`.yield(path, `$completion`);
            InlineMarker.mark(1);
         }

         var9 = access$getLinkOptions(this);
         var9 = Arrays.copyOf(var9, var9.length);
         if (Files.isDirectory(path, Arrays.copyOf(var9, var9.length))) {
            entriesAction.invoke(entriesReader.readEntries(node));
         }
      } else {
         var9 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
         if (Files.exists(path, Arrays.copyOf(var9, var9.length))) {
            InlineMarker.mark(0);
            `$this$yieldIfNeeded`.yield(path, `$completion`);
            InlineMarker.mark(1);
            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   private fun dfsIterator(): Iterator<Path> {
      return SequencesKt.iterator(
         (
            new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(this, null) {
               Object L$1;
               Object L$2;
               Object L$3;
               Object L$4;
               Object L$5;
               int label;

               {
                  super(2, `$completion`);
                  this.this$0 = `$receiver`;
               }

               // $VF: Irreducible bytecode was duplicated to produce valid code
               @Nullable
               @Override
               public final Object invokeSuspend(@NotNull Object $result) {
                  var var17: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var `$this$iterator`: SequenceScope;
                  var stack: ArrayDeque;
                  var entriesReader: DirectoryEntriesReader;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        `$this$iterator` = this.L$0 as SequenceScope;
                        stack = new ArrayDeque();
                        entriesReader = new DirectoryEntriesReader(PathTreeWalk.access$getFollowLinks(this.this$0));
                        var startNodex: PathNode = new PathNode(
                           PathTreeWalk.access$getStart$p(this.this$0),
                           PathTreeWalkKt.access$keyOf(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalk.access$getLinkOptions(this.this$0)),
                           null
                        );
                        var var19: PathTreeWalk = this.this$0;
                        var `path$ivx`: Path = startNodex.getPath();
                        var var31: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(var19);
                        var31 = Arrays.copyOf(var31, var31.length);
                        if (Files.isDirectory(`path$ivx`, Arrays.copyOf(var31, var31.length))) {
                           if (PathTreeWalkKt.access$createsCycle(startNodex)) {
                              throw new FileSystemLoopException(`path$ivx`.toString());
                           }

                           if (PathTreeWalk.access$getIncludeDirectories(var19)) {
                              var var10002: Continuation = this;
                              this.L$0 = `$this$iterator`;
                              this.L$1 = stack;
                              this.L$2 = entriesReader;
                              this.L$3 = startNodex;
                              this.L$4 = var19;
                              this.L$5 = `path$ivx`;
                              this.label = 1;
                              if (`$this$iterator`.yield(`path$ivx`, var10002) === var17) {
                                 return var17;
                              }
                           }

                           var31 = PathTreeWalk.access$getLinkOptions(var19);
                           var31 = Arrays.copyOf(var31, var31.length);
                           if (Files.isDirectory(`path$ivx`, Arrays.copyOf(var31, var31.length))) {
                              startNodex.setContentIterator(entriesReader.readEntries(startNodex).iterator());
                              stack.addLast(startNodex);
                           }
                        } else {
                           var31 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                           if (Files.exists(`path$ivx`, Arrays.copyOf(var31, var31.length))) {
                              var var49: Continuation = this;
                              this.L$0 = `$this$iterator`;
                              this.L$1 = stack;
                              this.L$2 = entriesReader;
                              this.label = 2;
                              if (`$this$iterator`.yield(`path$ivx`, var49) === var17) {
                                 return var17;
                              }
                           }
                        }
                        break;
                     case 1:
                        var var25: Path = this.L$5 as Path;
                        var topNode: PathTreeWalk = this.L$4 as PathTreeWalk;
                        var startNode: PathNode = this.L$3 as PathNode;
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        stack = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        var var29: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(topNode);
                        var29 = Arrays.copyOf(var29, var29.length);
                        if (Files.isDirectory(var25, Arrays.copyOf(var29, var29.length))) {
                           startNode.setContentIterator(entriesReader.readEntries(startNode).iterator());
                           stack.addLast(startNode);
                        }
                        break;
                     case 2:
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        stack = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        break;
                     case 3:
                        var `path$ivxx`: Path = this.L$5 as Path;
                        var `this_$ivx`: PathTreeWalk = this.L$4 as PathTreeWalk;
                        var pathNode: PathNode = this.L$3 as PathNode;
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        stack = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        var var14: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(`this_$ivx`);
                        var14 = Arrays.copyOf(var14, var14.length);
                        if (Files.isDirectory(`path$ivxx`, Arrays.copyOf(var14, var14.length))) {
                           pathNode.setContentIterator(entriesReader.readEntries(pathNode).iterator());
                           stack.addLast(pathNode);
                        }
                        break;
                     case 4:
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        stack = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  while (!stack.isEmpty()) {
                     var var10000: java.util.Iterator = (stack.last() as PathNode).getContentIterator();
                     if (var10000.hasNext()) {
                        var var24: PathNode = var10000.next() as PathNode;
                        var var27: PathTreeWalk = this.this$0;
                        var `path$ivxx`: Path = var24.getPath();
                        var var42: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(var27);
                        var42 = Arrays.copyOf(var42, var42.length);
                        if (Files.isDirectory(`path$ivxx`, Arrays.copyOf(var42, var42.length))) {
                           if (PathTreeWalkKt.access$createsCycle(var24)) {
                              throw new FileSystemLoopException(`path$ivxx`.toString());
                           }

                           if (PathTreeWalk.access$getIncludeDirectories(var27)) {
                              var var50: Continuation = this;
                              this.L$0 = `$this$iterator`;
                              this.L$1 = stack;
                              this.L$2 = entriesReader;
                              this.L$3 = var24;
                              this.L$4 = var27;
                              this.L$5 = `path$ivxx`;
                              this.label = 3;
                              if (`$this$iterator`.yield(`path$ivxx`, var50) === var17) {
                                 return var17;
                              }
                           }

                           var42 = PathTreeWalk.access$getLinkOptions(var27);
                           var42 = Arrays.copyOf(var42, var42.length);
                           if (Files.isDirectory(`path$ivxx`, Arrays.copyOf(var42, var42.length))) {
                              var24.setContentIterator(entriesReader.readEntries(var24).iterator());
                              stack.addLast(var24);
                           }
                        } else {
                           var42 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                           if (Files.exists(`path$ivxx`, Arrays.copyOf(var42, var42.length))) {
                              var var51: Continuation = this;
                              this.L$0 = `$this$iterator`;
                              this.L$1 = stack;
                              this.L$2 = entriesReader;
                              this.L$3 = null;
                              this.L$4 = null;
                              this.L$5 = null;
                              this.label = 4;
                              if (`$this$iterator`.yield(`path$ivxx`, var51) === var17) {
                                 return var17;
                              }
                           }
                        }
                     } else {
                        stack.removeLast();
                     }
                  }

                  return Unit.INSTANCE;
               }

               @NotNull
               @Override
               public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                  var var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               @Nullable
               public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2<? super SequenceScope<? super Path>, ? super Continuation<? super Unit>, ? extends Object>
      );
   }

   private fun bfsIterator(): Iterator<Path> {
      return SequencesKt.iterator(
         (
            new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>(this, null) {
               Object L$1;
               Object L$2;
               Object L$3;
               Object L$4;
               Object L$5;
               int label;

               {
                  super(2, `$completion`);
                  this.this$0 = `$receiver`;
               }

               // $VF: Irreducible bytecode was duplicated to produce valid code
               @Nullable
               @Override
               public final Object invokeSuspend(@NotNull Object $result) {
                  var var14: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var `$this$iterator`: SequenceScope;
                  var queue: ArrayDeque;
                  var entriesReader: DirectoryEntriesReader;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        `$this$iterator` = this.L$0 as SequenceScope;
                        queue = new ArrayDeque();
                        entriesReader = new DirectoryEntriesReader(PathTreeWalk.access$getFollowLinks(this.this$0));
                        queue.addLast(
                           new PathNode(
                              PathTreeWalk.access$getStart$p(this.this$0),
                              PathTreeWalkKt.access$keyOf(PathTreeWalk.access$getStart$p(this.this$0), PathTreeWalk.access$getLinkOptions(this.this$0)),
                              null
                           )
                        );
                        if (queue.isEmpty()) {
                           return Unit.INSTANCE;
                        }
                        break;
                     case 1:
                        var `path$iv`: Path = this.L$5 as Path;
                        var `this_$iv`: PathTreeWalk = this.L$4 as PathTreeWalk;
                        var pathNode: PathNode = this.L$3 as PathNode;
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        queue = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        var var11: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(`this_$iv`);
                        var11 = Arrays.copyOf(var11, var11.length);
                        if (Files.isDirectory(`path$iv`, Arrays.copyOf(var11, var11.length))) {
                           queue.addAll(entriesReader.readEntries(pathNode));
                        }

                        if (queue.isEmpty()) {
                           return Unit.INSTANCE;
                        }
                        break;
                     case 2:
                        entriesReader = this.L$2 as DirectoryEntriesReader;
                        queue = this.L$1 as ArrayDeque;
                        `$this$iterator` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        if (queue.isEmpty()) {
                           return Unit.INSTANCE;
                        }
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  while (true) {
                     var pathNodex: PathNode = queue.removeFirst() as PathNode;
                     var var16: PathTreeWalk = this.this$0;
                     var `path$ivx`: Path = pathNodex.getPath();
                     var var21: Array<LinkOption> = PathTreeWalk.access$getLinkOptions(var16);
                     var21 = Arrays.copyOf(var21, var21.length);
                     if (Files.isDirectory(`path$ivx`, Arrays.copyOf(var21, var21.length))) {
                        if (PathTreeWalkKt.access$createsCycle(pathNodex)) {
                           throw new FileSystemLoopException(`path$ivx`.toString());
                        }

                        if (PathTreeWalk.access$getIncludeDirectories(var16)) {
                           var var28: Continuation = this;
                           this.L$0 = `$this$iterator`;
                           this.L$1 = queue;
                           this.L$2 = entriesReader;
                           this.L$3 = pathNodex;
                           this.L$4 = var16;
                           this.L$5 = `path$ivx`;
                           this.label = 1;
                           if (`$this$iterator`.yield(`path$ivx`, var28) === var14) {
                              return var14;
                           }
                        }

                        var21 = PathTreeWalk.access$getLinkOptions(var16);
                        var21 = Arrays.copyOf(var21, var21.length);
                        if (Files.isDirectory(`path$ivx`, Arrays.copyOf(var21, var21.length))) {
                           queue.addAll(entriesReader.readEntries(pathNodex));
                        }

                        if (queue.isEmpty()) {
                           return Unit.INSTANCE;
                        }
                     } else {
                        var21 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
                        if (Files.exists(`path$ivx`, Arrays.copyOf(var21, var21.length))) {
                           var var10002: Continuation = this;
                           this.L$0 = `$this$iterator`;
                           this.L$1 = queue;
                           this.L$2 = entriesReader;
                           this.L$3 = null;
                           this.L$4 = null;
                           this.L$5 = null;
                           this.label = 2;
                           if (`$this$iterator`.yield(`path$ivx`, var10002) === var14) {
                              return var14;
                           }
                        }

                        if (queue.isEmpty()) {
                           return Unit.INSTANCE;
                        }
                     }
                  }
               }

               @NotNull
               @Override
               public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                  var var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               @Nullable
               public final Object invoke(@NotNull SequenceScope<? super Path> p1, @Nullable Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2<? super SequenceScope<? super Path>, ? super Continuation<? super Unit>, ? extends Object>
      );
   }
}
