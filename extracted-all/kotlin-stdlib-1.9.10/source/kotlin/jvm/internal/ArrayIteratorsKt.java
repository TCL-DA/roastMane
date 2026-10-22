package kotlin.jvm.internal

public fun iterator(array: ByteArray): ByteIterator {
   return new ArrayByteIterator(array);
}

public fun iterator(array: CharArray): CharIterator {
   return new ArrayCharIterator(array);
}

public fun iterator(array: ShortArray): ShortIterator {
   return new ArrayShortIterator(array);
}

public fun iterator(array: IntArray): IntIterator {
   return new ArrayIntIterator(array);
}

public fun iterator(array: LongArray): LongIterator {
   return new ArrayLongIterator(array);
}

public fun iterator(array: FloatArray): FloatIterator {
   return new ArrayFloatIterator(array);
}

public fun iterator(array: DoubleArray): DoubleIterator {
   return new ArrayDoubleIterator(array);
}

public fun iterator(array: BooleanArray): BooleanIterator {
   return new ArrayBooleanIterator(array);
}

