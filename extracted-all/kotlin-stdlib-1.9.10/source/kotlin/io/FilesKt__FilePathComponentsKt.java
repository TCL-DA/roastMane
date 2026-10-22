package kotlin.io

import java.io.File
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nFilePathComponents.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FilePathComponents.kt\nkotlin/io/FilesKt__FilePathComponentsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,148:1\n1549#2:149\n1620#2,3:150\n*S KotlinDebug\n*F\n+ 1 FilePathComponents.kt\nkotlin/io/FilesKt__FilePathComponentsKt\n*L\n133#1:149\n133#1:150,3\n*E\n"])
// $VF: Class flags could not be determined
internal class FilesKt__FilePathComponentsKt {
   public final val isRooted: Boolean
      public final get() {
         var var10000: java.lang.String = `$this$isRooted`.getPath();
         return getRootLength$FilesKt__FilePathComponentsKt(var10000) > 0;
      }

   internal final val root: File
      internal final get() {
         return new File(FilesKt.getRootName(`$this$root`));
      }

   internal final val rootName: String
      internal final get() {
         var var10000: java.lang.String = `$this$rootName`.getPath();
         var var4: java.lang.String = `$this$rootName`.getPath();
         var10000 = var10000.substring(0, getRootLength$FilesKt__FilePathComponentsKt(var4));
         return var10000;
      }


   @JvmStatic
   private fun String.getRootLength(): Int {
      var first: Int = StringsKt.indexOf$default(`$this$getRootLength`, File.separatorChar, 0, false, 4, null);
      if (first == 0) {
         if (`$this$getRootLength`.length() > 1 && `$this$getRootLength`.charAt(1) == File.separatorChar) {
            first = StringsKt.indexOf$default(`$this$getRootLength`, File.separatorChar, 2, false, 4, null);
            if (first >= 0) {
               first = StringsKt.indexOf$default(`$this$getRootLength`, File.separatorChar, first + 1, false, 4, null);
               if (first >= 0) {
                  return first + 1;
               }

               return `$this$getRootLength`.length();
            }
         }

         return 1;
      } else if (first > 0 && `$this$getRootLength`.charAt(first - 1) == ':') {
         return first + 1;
      } else {
         return if (first == -1 && StringsKt.endsWith$default(`$this$getRootLength`, (char)58, false, 2, null)) `$this$getRootLength`.length() else 0;
      }
   }

   @JvmStatic
   internal fun File.toComponents(): FilePathComponents {
      var path: java.lang.String = `$this$toComponents`.getPath();
      var rootLength: Int = getRootLength$FilesKt__FilePathComponentsKt(path);
      var var10000: java.lang.String = path.substring(0, rootLength);
      var10000 = path.substring(rootLength);
      var var19: java.util.List;
      if (var10000.length() == 0) {
         var19 = CollectionsKt.emptyList();
      } else {
         var var17: java.lang.Iterable = StringsKt.split$default(var10000, new char[]{File.separatorChar}, false, 0, 6, null);
         var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var17, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(new File(`item$iv$iv` as java.lang.String));
         }

         var19 = `destination$iv$iv` as java.util.List;
      }

      return new FilePathComponents(new File(var10000), var19);
   }

   @JvmStatic
   internal fun File.subPath(beginIndex: Int, endIndex: Int): File {
      return FilesKt.toComponents(`$this$subPath`).subPath(beginIndex, endIndex);
   }

   open fun FilesKt__FilePathComponentsKt() {
   }
}
