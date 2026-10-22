package kotlin.contracts

import kotlin.enums.EnumEntries
import kotlin.internal.ContractsDsl

@ContractsDsl
@ExperimentalContracts
@SinceKotlin(version = "1.3")
enum class InvocationKind {
   @ContractsDsl
   @JvmStatic
   AT_MOST_ONCE,
   @ContractsDsl
   @JvmStatic
   AT_LEAST_ONCE,
   @ContractsDsl
   @JvmStatic
   EXACTLY_ONCE,
   @ContractsDsl
   @JvmStatic
   UNKNOWN;


   @JvmStatic
   fun getEntries(): EnumEntries<InvocationKind> {
      return $ENTRIES;
   }
}
