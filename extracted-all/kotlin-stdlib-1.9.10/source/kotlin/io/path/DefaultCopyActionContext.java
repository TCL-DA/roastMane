package kotlin.io.path

import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Arrays

@ExperimentalPathApi
private object DefaultCopyActionContext private constructor() : CopyActionContext {
   @JvmStatic
   public DefaultCopyActionContext INSTANCE = new DefaultCopyActionContext();


   public override fun Path.copyToIgnoringExistingDirectory(target: Path, followLinks: Boolean): CopyActionResult {
      var options: Array<LinkOption> = LinkFollowing.INSTANCE.toLinkOptions(followLinks);
      var var10001: Array<LinkOption> = Arrays.copyOf(options, options.length);
      if (Files.isDirectory(`$this$copyToIgnoringExistingDirectory`, Arrays.copyOf(var10001, var10001.length))) {
         var var6: Array<LinkOption> = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
         if (Files.isDirectory(target, Arrays.copyOf(var6, var6.length))) {
            return CopyActionResult.CONTINUE;
         }
      }

      var var10002: Array<CopyOption> = Arrays.copyOf(options, options.length);
      return CopyActionResult.CONTINUE;
   }
}
