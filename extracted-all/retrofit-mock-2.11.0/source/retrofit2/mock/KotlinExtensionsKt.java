package retrofit2.mock

import kotlin.jvm.internal.Intrinsics

@JvmSynthetic
public inline fun <reified T> MockRetrofit.create(): BehaviorDelegate<T> {
   Intrinsics.reifiedOperationMarker(4, "T");
   var var10000: BehaviorDelegate = `$this$create`.create(Object::class.java);
   return var10000;
}

