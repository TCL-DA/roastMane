package kotlin.jvm.internal

private fun notSupportedError(): Nothing {
   throw new UnsupportedOperationException("Not supported for local property reference.");
}

@JvmSynthetic
fun `access$notSupportedError`(): Void {
   return notSupportedError();
}

