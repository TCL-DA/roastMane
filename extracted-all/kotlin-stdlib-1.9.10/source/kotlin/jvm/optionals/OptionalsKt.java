package kotlin.jvm.optionals

import java.util.Optional

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T : Any> Optional<T>.getOrNull(): T? {
   return (T)`$this$getOrNull`.orElse(null);
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T> Optional<out T>.getOrDefault(defaultValue: T): T {
   return (T)(if (`$this$getOrDefault`.isPresent()) `$this$getOrDefault`.get() else defaultValue);
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public inline fun <T> Optional<out T>.getOrElse(defaultValue: () -> T): T {
   return (T)(if (`$this$getOrElse`.isPresent()) `$this$getOrElse`.get() else defaultValue.invoke());
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T : Any, C : MutableCollection<in T>> Optional<T>.toCollection(destination: C): C {
   if (`$this$toCollection`.isPresent()) {
      var var10001: Any = `$this$toCollection`.get();
      destination.add(var10001);
   }

   return (C)destination;
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T : Any> Optional<out T>.toList(): List<T> {
   return (java.util.List<T>)(if (`$this$toList`.isPresent()) CollectionsKt.listOf(`$this$toList`.get()) else CollectionsKt.emptyList());
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T : Any> Optional<out T>.toSet(): Set<T> {
   return (java.util.Set<T>)(if (`$this$toSet`.isPresent()) SetsKt.setOf(`$this$toSet`.get()) else SetsKt.emptySet());
}

@SinceKotlin(version = "1.8")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public fun <T : Any> Optional<out T>.asSequence(): Sequence<T> {
   return (Sequence<T>)(if (`$this$asSequence`.isPresent()) SequencesKt.sequenceOf(new Object[]{`$this$asSequence`.get()}) else SequencesKt.emptySequence());
}

