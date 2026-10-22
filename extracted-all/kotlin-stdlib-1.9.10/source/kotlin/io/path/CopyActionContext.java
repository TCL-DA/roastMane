package kotlin.io.path

import java.nio.file.Path

@ExperimentalPathApi
@SinceKotlin(version = "1.8")
interface CopyActionContext {
   public abstract fun Path.copyToIgnoringExistingDirectory(target: Path, followLinks: Boolean): CopyActionResult {
   }
}
