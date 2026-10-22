package kotlin.contracts

import kotlin.internal.ContractsDsl

@ContractsDsl
@ExperimentalContracts
@SinceKotlin(version = "1.3")
interface SimpleEffect : Effect {
   @ContractsDsl
   @ExperimentalContracts
   public abstract infix fun implies(booleanExpression: Boolean): ConditionalEffect {
   }
}
