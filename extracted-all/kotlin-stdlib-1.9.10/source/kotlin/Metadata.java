package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.CLASS])
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.3")
annotation class Metadata(
   val kind: Int = 1,
   val metadataVersion: IntArray = {},
   val bytecodeVersion: IntArray = {1, 0, 3},
   val data1: Array<String> = {},
   val data2: Array<String> = {},
   val extraString: String = "",
   val packageName: String = "",
   val extraInt: Int = 0
) {
   // $VF: Class flags could not be determined
   internal class DefaultImpls {
   }
}
