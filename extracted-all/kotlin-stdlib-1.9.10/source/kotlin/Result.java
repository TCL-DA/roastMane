package kotlin

import java.io.Serializable
import kotlin.internal.InlineOnly

@JvmInline
@SinceKotlin(version = "1.3")
inline class Result<T> : Serializable {
   @JvmStatic
   public Result.Companion Companion = new Result.Companion(null);

   public final val isFailure: Boolean
      public final get() {
         return arg0 is Result.Failure;
      }

   public final val isSuccess: Boolean
      public final get() {
         return arg0 !is Result.Failure;
      }

   internal final val value: Any?

   @InlineOnly
   @JvmStatic
   public inline fun getOrNull(): Any? {
      return (T)(if (isFailure-impl(arg0)) null else arg0);
   }

   @JvmStatic
   public fun exceptionOrNull(): Throwable? {
      return if (arg0 is Result.Failure) (arg0 as Result.Failure).exception else null;
   }

   @JvmStatic
   public open fun toString(): String {
      return if (arg0 is Result.Failure) (arg0 as Result.Failure).toString() else "Success(" + arg0 + ')';
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.value);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return if (arg0 == null) 0 else arg0.hashCode();
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.value);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is Result) {
         return false;
      } else {
         return arg0 == (other as Result).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.value, other);
   }

   @PublishedApi
   @JvmStatic
   fun <T> `constructor-impl`(value: Any?): Any {
      return value;
   }

   @JvmStatic
   fun `equals-impl0`(p1: Any, p2: Any): Boolean {
      return p1 == p2;
   }

   companion object Companion private constructor() {
      @InlineOnly
      @JvmName(name = "success")
      public inline fun <T> success(value: T): Result<T> {
         return Result.constructor-impl(value);
      }

      @InlineOnly
      @JvmName(name = "failure")
      public inline fun <T> failure(exception: Throwable): Result<T> {
         return Result.constructor-impl(ResultKt.createFailure(exception));
      }
   }

   internal class Failure(exception: Throwable) : Serializable {
      public final val exception: Throwable

      init {
         this.exception = exception;
      }

      public override operator fun equals(other: Any?): Boolean {
         return other is Result.Failure && this.exception == (other as Result.Failure).exception;
      }

      public override fun hashCode(): Int {
         return this.exception.hashCode();
      }

      public override fun toString(): String {
         return "Failure(" + this.exception + ')';
      }
   }
}
