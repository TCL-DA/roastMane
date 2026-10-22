package kotlin.contracts

import kotlin.internal.ContractsDsl
import kotlin.internal.InlineOnly

@ContractsDsl
@ExperimentalContracts
@InlineOnly
@SinceKotlin(version = "1.3")
public inline fun contract(builder: (ContractBuilder) -> Unit) {
}

