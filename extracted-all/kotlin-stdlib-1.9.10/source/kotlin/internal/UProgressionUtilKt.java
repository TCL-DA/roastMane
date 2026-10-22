package kotlin.internal

private fun differenceModulo(a: UInt, b: UInt, c: UInt): UInt {
   var ac: Int = Integer.remainderUnsigned(a, c);
   var bc: Int = Integer.remainderUnsigned(b, c);
   return if (Integer.compareUnsigned(ac, bc) >= 0) UInt.constructor-impl(ac - bc) else UInt.constructor-impl(UInt.constructor-impl(ac - bc) + c);
}

private fun differenceModulo(a: ULong, b: ULong, c: ULong): ULong {
   var ac: Long = java.lang.Long.remainderUnsigned(a, c);
   var bc: Long = java.lang.Long.remainderUnsigned(b, c);
   return if (java.lang.Long.compareUnsigned(ac, bc) >= 0) ULong.constructor-impl(ac - bc) else ULong.constructor-impl(ULong.constructor-impl(ac - bc) + c);
}

@PublishedApi
@SinceKotlin(version = "1.3")
internal fun getProgressionLastElement(start: UInt, end: UInt, step: Int): UInt {
   var var10000: Int;
   if (step > 0) {
      var10000 = if (Integer.compareUnsigned(start, end) >= 0)
         end
         else
         UInt.constructor-impl(end - differenceModulo-WZ9TVnA(end, start, UInt.constructor-impl(step)));
   } else {
      if (step >= 0) {
         throw new IllegalArgumentException("Step is zero.");
      }

      var10000 = if (Integer.compareUnsigned(start, end) <= 0)
         end
         else
         UInt.constructor-impl(end + differenceModulo-WZ9TVnA(start, end, UInt.constructor-impl(-step)));
   }

   return var10000;
}

@PublishedApi
@SinceKotlin(version = "1.3")
internal fun getProgressionLastElement(start: ULong, end: ULong, step: Long): ULong {
   var var10000: Long;
   if (step > 0L) {
      var10000 = if (java.lang.Long.compareUnsigned(start, end) >= 0)
         end
         else
         ULong.constructor-impl(end - differenceModulo-sambcqE(end, start, ULong.constructor-impl(step)));
   } else {
      if (step >= 0L) {
         throw new IllegalArgumentException("Step is zero.");
      }

      var10000 = if (java.lang.Long.compareUnsigned(start, end) <= 0)
         end
         else
         ULong.constructor-impl(end + differenceModulo-sambcqE(start, end, ULong.constructor-impl(-step)));
   }

   return var10000;
}

