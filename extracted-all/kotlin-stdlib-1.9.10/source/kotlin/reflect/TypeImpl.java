package kotlin.reflect

import java.lang.reflect.Type

@ExperimentalStdlibApi
private interface TypeImpl : Type {
   public abstract override fun getTypeName(): String {
   }
}
