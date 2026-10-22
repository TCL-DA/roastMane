package kotlin.io

import java.io.File
import java.io.IOException
import java.util.ArrayDeque
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

class FileTreeWalk private constructor(start: File,
      direction: FileWalkDirection,
      onEnter: ((File) -> Boolean)?,
      onLeave: ((File) -> Unit)?,
      onFail: ((File, IOException) -> Unit)?,
      maxDepth: Int
   ) :
   Sequence<File> {
   private final val direction: FileWalkDirection
   private final val maxDepth: Int
   private final val onEnter: ((File) -> Boolean)?
   private final val onFail: ((File, IOException) -> Unit)?
   private final val onLeave: ((File) -> Unit)?
   private final val start: File

   init {
      this.start = start;
      this.direction = direction;
      this.onEnter = onEnter;
      this.onLeave = onLeave;
      this.onFail = onFail;
      this.maxDepth = maxDepth;
   }

   internal constructor(start: File, direction: FileWalkDirection) : this(start, direction, null, null, null, 0, 32, null)
   public override operator fun iterator(): Iterator<File> {
      return new FileTreeWalk.FileTreeWalkIterator(this);
   }

   public fun onEnter(function: (File) -> Boolean): FileTreeWalk {
      return new FileTreeWalk(this.start, this.direction, function, this.onLeave, this.onFail, this.maxDepth);
   }

   public fun onLeave(function: (File) -> Unit): FileTreeWalk {
      return new FileTreeWalk(this.start, this.direction, this.onEnter, function, this.onFail, this.maxDepth);
   }

   public fun onFail(function: (File, IOException) -> Unit): FileTreeWalk {
      return new FileTreeWalk(this.start, this.direction, this.onEnter, this.onLeave, function, this.maxDepth);
   }

   public fun maxDepth(depth: Int): FileTreeWalk {
      if (depth <= 0) {
         throw new IllegalArgumentException("depth must be positive, but was " + depth + '.');
      } else {
         return new FileTreeWalk(this.start, this.direction, this.onEnter, this.onLeave, this.onFail, depth);
      }
   }

   @SourceDebugExtension(["SMAP\nFileTreeWalk.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileTreeWalk.kt\nkotlin/io/FileTreeWalk$DirectoryState\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,273:1\n1#2:274\n*E\n"])
   private abstract class DirectoryState : FileTreeWalk.WalkState {
      open fun DirectoryState(rootDir: File) {
         super(rootDir);
         if (_Assertions.ENABLED && _Assertions.ENABLED && !rootDir.isDirectory()) {
            throw new AssertionError("rootDir must be verified to be directory beforehand.");
         }
      }
   }

   private inner class FileTreeWalkIterator : AbstractIterator<File> {
      private final val state: ArrayDeque<kotlin.io.FileTreeWalk.WalkState>

      init {
         this.this$0 = `this$0`;
         this.state = new ArrayDeque<>();
         if (FileTreeWalk.access$getStart$p(this.this$0).isDirectory()) {
            this.state.push(this.directoryState(FileTreeWalk.access$getStart$p(this.this$0)));
         } else if (FileTreeWalk.access$getStart$p(this.this$0).isFile()) {
            this.state.push(new FileTreeWalk.FileTreeWalkIterator.SingleFileState(this, FileTreeWalk.access$getStart$p(this.this$0)));
         } else {
            this.done();
         }
      }

      protected override fun computeNext() {
         var nextFile: File = this.gotoNext();
         if (nextFile != null) {
            this.setNext(nextFile);
         } else {
            this.done();
         }
      }

      private fun directoryState(root: File): kotlin.io.FileTreeWalk.DirectoryState {
         var var10000: FileTreeWalk.DirectoryState;
         switch (FileTreeWalk.FileTreeWalkIterator.WhenMappings.$EnumSwitchMapping$0[FileTreeWalk.access$getDirection$p(this.this$0).ordinal()]) {
            case 1:
               var10000 = new FileTreeWalk.FileTreeWalkIterator.TopDownDirectoryState(this, root);
               break;
            case 2:
               var10000 = new FileTreeWalk.FileTreeWalkIterator.BottomUpDirectoryState(this, root);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         return var10000;
      }

      private tailrec fun gotoNext(): File? {
         while (true) {
            var var10000: FileTreeWalk.WalkState = this.state.peek();
            if (var10000 == null) {
               return null;
            }

            var file: File = var10000.step();
            if (file == null) {
               this.state.pop();
               this = this;
            } else {
               if (file == var10000.getRoot() || !file.isDirectory() || this.state.size() >= FileTreeWalk.access$getMaxDepth$p(this.this$0)) {
                  return file;
               }

               this.state.push(this.directoryState(file));
               this = this;
            }
         }
      }

      private inner class BottomUpDirectoryState(rootDir: File) : FileTreeWalk.DirectoryState(rootDir) {
         private File[] fileList;

         private final var failed: Boolean
         private final var fileIndex: Int
         private final var fileList: Array<File>?
         private final var rootVisited: Boolean

         init {
            this.this$0 = `this$0`;
         }

         public override fun step(): File? {
            if (!this.failed && this.fileList == null) {
               var var10000: Function1 = FileTreeWalk.access$getOnEnter$p(this.this$0.this$0);
               if (var10000 != null && !var10000.invoke(this.getRoot()) as java.lang.Boolean) {
                  return null;
               }

               this.fileList = this.getRoot().listFiles();
               if (this.fileList == null) {
                  var var2: Function2 = FileTreeWalk.access$getOnFail$p(this.this$0.this$0);
                  if (var2 != null) {
                     var2.invoke(this.getRoot(), new AccessDeniedException(this.getRoot(), null, "Cannot list files in a directory", 2, null));
                  }

                  this.failed = true;
               }
            }

            if (this.fileList != null) {
               var var3: Int = this.fileIndex;
               var var10001: Array<File> = this.fileList;
               if (var3 < var10001.length) {
                  var var5: Array<File> = this.fileList;
                  return var5[this.fileIndex++];
               }
            }

            if (!this.rootVisited) {
               this.rootVisited = true;
               return this.getRoot();
            } else {
               var var4: Function1 = FileTreeWalk.access$getOnLeave$p(this.this$0.this$0);
               if (var4 != null) {
                  var4.invoke(this.getRoot());
               }

               return null;
            }
         }
      }

      @SourceDebugExtension(["SMAP\nFileTreeWalk.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileTreeWalk.kt\nkotlin/io/FileTreeWalk$FileTreeWalkIterator$SingleFileState\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,273:1\n1#2:274\n*E\n"])
      private inner class SingleFileState(rootFile: File) : FileTreeWalk.WalkState(rootFile) {
         private final var visited: Boolean

         init {
            this.this$0 = `this$0`;
            if (_Assertions.ENABLED && _Assertions.ENABLED && !rootFile.isFile()) {
               throw new AssertionError("rootFile must be verified to be file beforehand.");
            }
         }

         public override fun step(): File? {
            if (this.visited) {
               return null;
            } else {
               this.visited = true;
               return this.getRoot();
            }
         }
      }

      private inner class TopDownDirectoryState(rootDir: File) : FileTreeWalk.DirectoryState(rootDir) {
         private File[] fileList;

         private final var fileIndex: Int
         private final var fileList: Array<File>?
         private final var rootVisited: Boolean

         init {
            this.this$0 = `this$0`;
         }

         public override fun step(): File? {
            if (!this.rootVisited) {
               var var7: Function1 = FileTreeWalk.access$getOnEnter$p(this.this$0.this$0);
               if (var7 != null && !var7.invoke(this.getRoot()) as java.lang.Boolean) {
                  return null;
               } else {
                  this.rootVisited = true;
                  return this.getRoot();
               }
            } else {
               if (this.fileList != null) {
                  var var10000: Int = this.fileIndex;
                  var var10001: Array<File> = this.fileList;
                  if (var10000 >= var10001.length) {
                     var var6: Function1 = FileTreeWalk.access$getOnLeave$p(this.this$0.this$0);
                     if (var6 != null) {
                        var6.invoke(this.getRoot());
                     }

                     return null;
                  }
               }

               label50:
               if (this.fileList == null) {
                  this.fileList = this.getRoot().listFiles();
                  if (this.fileList == null) {
                     var var2: Function2 = FileTreeWalk.access$getOnFail$p(this.this$0.this$0);
                     if (var2 != null) {
                        var2.invoke(this.getRoot(), new AccessDeniedException(this.getRoot(), null, "Cannot list files in a directory", 2, null));
                     }
                  }

                  if (this.fileList != null) {
                     var var3: Array<File> = this.fileList;
                     if (var3.length != 0) {
                        break label50;
                     }
                  }

                  var var4: Function1 = FileTreeWalk.access$getOnLeave$p(this.this$0.this$0);
                  if (var4 != null) {
                     var4.invoke(this.getRoot());
                  }

                  return null;
               }

               var var5: Array<File> = this.fileList;
               return var5[this.fileIndex++];
            }
         }
      }
   }

   private abstract class WalkState {
      public final val root: File

      open fun WalkState(root: File) {
         this.root = root;
      }

      public abstract fun step(): File? {
      }
   }
}
