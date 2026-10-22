package kotlin.io.path

import java.nio.file.Path

private class PathNode(path: Path, key: Any?, parent: PathNode?) {
   public final var contentIterator: Iterator<PathNode>?
      internal set
   public final val key: Any?
   public final val parent: PathNode?
   public final val path: Path

   init {
      this.path = path;
      this.key = key;
      this.parent = parent;
   }
}
