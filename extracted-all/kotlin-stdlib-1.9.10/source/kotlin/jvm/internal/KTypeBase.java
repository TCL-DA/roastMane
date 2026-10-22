package kotlin.jvm.internal

import java.lang.reflect.Type
import kotlin.reflect.KType

@SinceKotlin(version = "1.4")
interface KTypeBase : KType {
   public val javaType: Type?
}
