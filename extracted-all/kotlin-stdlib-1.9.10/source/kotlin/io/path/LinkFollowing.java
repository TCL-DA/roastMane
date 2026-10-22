package kotlin.io.path

import java.nio.file.FileVisitOption
import java.nio.file.LinkOption
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPathTreeWalk.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathTreeWalk.kt\nkotlin/io/path/LinkFollowing\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,177:1\n26#2:178\n*S KotlinDebug\n*F\n+ 1 PathTreeWalk.kt\nkotlin/io/path/LinkFollowing\n*L\n142#1:178\n*E\n"])
internal object LinkFollowing private constructor() {
   @JvmStatic
   public LinkFollowing INSTANCE = new LinkFollowing();
   @JvmStatic
   private LinkOption[] nofollowLinkOption = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
   @JvmStatic
   private LinkOption[] followLinkOption = new LinkOption[0];

   private final val followLinkOption: Array<LinkOption>
   private final val followVisitOption: Set<FileVisitOption> = SetsKt.setOf(FileVisitOption.FOLLOW_LINKS)
   private final val nofollowLinkOption: Array<LinkOption>
   private final val nofollowVisitOption: Set<FileVisitOption> = SetsKt.emptySet()


   public fun toLinkOptions(followLinks: Boolean): Array<LinkOption> {
      return if (followLinks) followLinkOption else nofollowLinkOption;
   }

   public fun toVisitOptions(followLinks: Boolean): Set<FileVisitOption> {
      return if (followLinks) followVisitOption else nofollowVisitOption;
   }
}
