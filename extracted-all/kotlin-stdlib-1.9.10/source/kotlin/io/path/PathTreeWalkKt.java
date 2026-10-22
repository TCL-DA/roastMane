package kotlin.io.path

import java.io.IOException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.Arrays

private fun keyOf(path: Path, linkOptions: Array<LinkOption>): Any? {
   var var2: Any;
   try {
      var exception: Array<LinkOption> = Arrays.copyOf(linkOptions, linkOptions.length);
      var var10000: BasicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, Arrays.copyOf(exception, exception.length));
      var2 = var10000.fileKey();
   } catch (java.lang.Throwable var4) {
      var2 = null;
   }

   return var2;
}

private fun PathNode.createsCycle(): Boolean {
   for (PathNode ancestor = $this$createsCycle.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
      if (ancestor.getKey() != null && `$this$createsCycle`.getKey() != null) {
         if (ancestor.getKey() == `$this$createsCycle`.getKey()) {
            return true;
         }
      } else {
         try {
            if (Files.isSameFile(ancestor.getPath(), `$this$createsCycle`.getPath())) {
               return true;
            }
         } catch (IOException var3) {
         } catch (SecurityException var4) {
         }
      }
   }

   return false;
}

@JvmSynthetic
fun `access$createsCycle`(`$receiver`: PathNode): Boolean {
   return createsCycle(`$receiver`);
}

@JvmSynthetic
fun `access$keyOf`(path: Path, linkOptions: Array<LinkOption>): Any {
   return keyOf(path, linkOptions);
}

