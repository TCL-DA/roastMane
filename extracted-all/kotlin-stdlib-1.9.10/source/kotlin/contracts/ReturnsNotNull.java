package kotlin.contracts

import kotlin.internal.ContractsDsl

@ContractsDsl
@ExperimentalContracts
@SinceKotlin(version = "1.3")
interface ReturnsNotNull : SimpleEffect {
}
