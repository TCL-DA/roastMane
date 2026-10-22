@file:SourceDebugExtension(["SMAP\nResult.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Result.kt\nkotlin/ResultKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,340:1\n1#2:341\n*E\n"])

package kotlin

import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@PublishedApi
@SinceKotlin(version = "1.3")
internal fun createFailure(exception: Throwable): Any {
   return new Result.Failure(exception);
}

@PublishedApi
@SinceKotlin(version = "1.3")
internal fun Result<*>.throwOnFailure() {
   if (`$this$throwOnFailure` is Result.Failure) {
      throw (`$this$throwOnFailure` as Result.Failure).exception;
   }
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R> runCatching(block: () -> R): Result<R> {
   var var1: Any;
   try {
      var1 = Result.constructor-impl(block.invoke());
   } catch (java.lang.Throwable var3) {
      var1 = Result.constructor-impl(createFailure(var3));
   }

   return var1;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <T, R> T.runCatching(block: (T) -> R): Result<R> {
   var var2: Any;
   try {
      var2 = Result.constructor-impl(block.invoke(`$this$runCatching`));
   } catch (java.lang.Throwable var4) {
      var2 = Result.constructor-impl(createFailure(var4));
   }

   return var2;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <T> Result<T>.getOrThrow(): T {
   throwOnFailure(`$this$getOrThrow`);
   return (T)`$this$getOrThrow`;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T : R> Result<T>.getOrElse(onFailure: (Throwable) -> R): R {
   contract {
      callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
   }

   var exception: java.lang.Throwable = Result.exceptionOrNull-impl(`$this$getOrElse`);
   return (R)(if (exception == null) `$this$getOrElse` else onFailure.invoke(exception));
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T : R> Result<T>.getOrDefault(defaultValue: R): R {
   return (R)(if (Result.isFailure-impl(`$this$getOrDefault`)) defaultValue else `$this$getOrDefault`);
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T> Result<T>.fold(onSuccess: (T) -> R, onFailure: (Throwable) -> R): R {
   contract {
      callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
      callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
   }

   var exception: java.lang.Throwable = Result.exceptionOrNull-impl(`$this$fold`);
   return (R)(if (exception == null) onSuccess.invoke(`$this$fold`) else onFailure.invoke(exception));
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T> Result<T>.map(transform: (T) -> R): Result<R> {
   contract {
      callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
   }

   return if (Result.isSuccess-impl(`$this$map`)) Result.constructor-impl(transform.invoke(`$this$map`)) else Result.constructor-impl(`$this$map`);
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T> Result<T>.mapCatching(transform: (T) -> R): Result<R> {
   var var10000: Any;
   if (Result.isSuccess-impl(`$this$mapCatching`)) {
      var var2: Any = `$this$mapCatching`;

      var `$this$mapCatching_u24lambda_u243`: Any;
      try {
         `$this$mapCatching_u24lambda_u243` = Result.constructor-impl(transform.invoke(var2));
      } catch (java.lang.Throwable var5) {
         `$this$mapCatching_u24lambda_u243` = Result.constructor-impl(createFailure(var5));
      }

      var10000 = `$this$mapCatching_u24lambda_u243`;
   } else {
      var10000 = Result.constructor-impl(`$this$mapCatching`);
   }

   return var10000;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T : R> Result<T>.recover(transform: (Throwable) -> R): Result<R> {
   contract {
      callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
   }

   var exception: java.lang.Throwable = Result.exceptionOrNull-impl(`$this$recover`);
   return if (exception == null) `$this$recover` else Result.constructor-impl(transform.invoke(exception));
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <R, T : R> Result<T>.recoverCatching(transform: (Throwable) -> R): Result<R> {
   var exception: java.lang.Throwable = Result.exceptionOrNull-impl(`$this$recoverCatching`);
   var var10000: Any;
   if (exception == null) {
      var10000 = `$this$recoverCatching`;
   } else {
      var `$this$recoverCatching_u24lambda_u245`: Any;
      try {
         `$this$recoverCatching_u24lambda_u245` = Result.constructor-impl(transform.invoke(exception));
      } catch (java.lang.Throwable var6) {
         `$this$recoverCatching_u24lambda_u245` = Result.constructor-impl(createFailure(var6));
      }

      var10000 = `$this$recoverCatching_u24lambda_u245`;
   }

   return var10000;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <T> Result<T>.onFailure(action: (Throwable) -> Unit): Result<T> {
   contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
   }

   var var10000: java.lang.Throwable = Result.exceptionOrNull-impl(`$this$onFailure`);
   if (var10000 != null) {
      action.invoke(var10000);
   }

   return `$this$onFailure`;
}

@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
   contract {
      callsInPlace(action, InvocationKind.AT_MOST_ONCE)
   }

   if (Result.isSuccess-impl(`$this$onSuccess`)) {
      action.invoke(`$this$onSuccess`);
   }

   return `$this$onSuccess`;
}

