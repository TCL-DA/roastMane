package kotlin.jvm.internal

import kotlin.reflect.KDeclarationContainer

interface ClassBasedDeclarationContainer : KDeclarationContainer {
   public val jClass: Class<*>
}
