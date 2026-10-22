package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.enums.EnumEntries

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
@SinceKotlin(version = "1.3")
annotation class RequiresOptIn(
   val message: String = "",
   val level: kotlin.RequiresOptIn.Level = RequiresOptIn.Level.ERROR
) {
   enum class Level {
      @JvmStatic
      WARNING,
      @JvmStatic
      ERROR;


      @JvmStatic
      fun getEntries(): EnumEntries<RequiresOptIn.Level> {
         return $ENTRIES;
      }
   }
}
