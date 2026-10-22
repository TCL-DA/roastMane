package kotlin.collections

import kotlin.jvm.internal.Intrinsics

@ExperimentalUnsignedTypes
private fun partition(array: UByteArray, left: Int, right: Int): Int {
   var i: Int = left;
   var j: Int = right;
   var pivot: Byte = UByteArray.get-w2LRezQ(array, (left + right) / 2);

   while (i <= j) {
      while (Intrinsics.compare(UByteArray.get-w2LRezQ(array, i) & 255, pivot & 255) < 0) {
         i++;
      }

      while (Intrinsics.compare(UByteArray.get-w2LRezQ(array, j) & 255, pivot & 255) > 0) {
         j--;
      }

      if (i <= j) {
         var tmp: Byte = UByteArray.get-w2LRezQ(array, i);
         UByteArray.set-VurrAj0(array, i, UByteArray.get-w2LRezQ(array, j));
         UByteArray.set-VurrAj0(array, j, tmp);
         i++;
         j--;
      }
   }

   return i;
}

@ExperimentalUnsignedTypes
private fun quickSort(array: UByteArray, left: Int, right: Int) {
   var index: Int = partition-4UcCI2c(array, left, right);
   if (left < index - 1) {
      quickSort-4UcCI2c(array, left, index - 1);
   }

   if (index < right) {
      quickSort-4UcCI2c(array, index, right);
   }
}

@ExperimentalUnsignedTypes
private fun partition(array: UShortArray, left: Int, right: Int): Int {
   var i: Int = left;
   var j: Int = right;
   var pivot: Short = UShortArray.get-Mh2AYeg(array, (left + right) / 2);

   while (i <= j) {
      while (Intrinsics.compare(UShortArray.get-Mh2AYeg(array, i) & '\uffff', pivot & '\uffff') < 0) {
         i++;
      }

      while (Intrinsics.compare(UShortArray.get-Mh2AYeg(array, j) & '\uffff', pivot & '\uffff') > 0) {
         j--;
      }

      if (i <= j) {
         var tmp: Short = UShortArray.get-Mh2AYeg(array, i);
         UShortArray.set-01HTLdE(array, i, UShortArray.get-Mh2AYeg(array, j));
         UShortArray.set-01HTLdE(array, j, tmp);
         i++;
         j--;
      }
   }

   return i;
}

@ExperimentalUnsignedTypes
private fun quickSort(array: UShortArray, left: Int, right: Int) {
   var index: Int = partition-Aa5vz7o(array, left, right);
   if (left < index - 1) {
      quickSort-Aa5vz7o(array, left, index - 1);
   }

   if (index < right) {
      quickSort-Aa5vz7o(array, index, right);
   }
}

@ExperimentalUnsignedTypes
private fun partition(array: UIntArray, left: Int, right: Int): Int {
   var i: Int = left;
   var j: Int = right;
   var pivot: Int = UIntArray.get-pVg5ArA(array, (left + right) / 2);

   while (i <= j) {
      while (Integer.compareUnsigned(UIntArray.get-pVg5ArA(array, i), pivot) < 0) {
         i++;
      }

      while (Integer.compareUnsigned(UIntArray.get-pVg5ArA(array, j), pivot) > 0) {
         j--;
      }

      if (i <= j) {
         var tmp: Int = UIntArray.get-pVg5ArA(array, i);
         UIntArray.set-VXSXFK8(array, i, UIntArray.get-pVg5ArA(array, j));
         UIntArray.set-VXSXFK8(array, j, tmp);
         i++;
         j--;
      }
   }

   return i;
}

@ExperimentalUnsignedTypes
private fun quickSort(array: UIntArray, left: Int, right: Int) {
   var index: Int = partition-oBK06Vg(array, left, right);
   if (left < index - 1) {
      quickSort-oBK06Vg(array, left, index - 1);
   }

   if (index < right) {
      quickSort-oBK06Vg(array, index, right);
   }
}

@ExperimentalUnsignedTypes
private fun partition(array: ULongArray, left: Int, right: Int): Int {
   var i: Int = left;
   var j: Int = right;
   var pivot: Long = ULongArray.get-s-VKNKU(array, (left + right) / 2);

   while (i <= j) {
      while (java.lang.Long.compareUnsigned(ULongArray.get-s-VKNKU(array, i), pivot) < 0) {
         i++;
      }

      while (java.lang.Long.compareUnsigned(ULongArray.get-s-VKNKU(array, j), pivot) > 0) {
         j--;
      }

      if (i <= j) {
         var tmp: Long = ULongArray.get-s-VKNKU(array, i);
         ULongArray.set-k8EXiF4(array, i, ULongArray.get-s-VKNKU(array, j));
         ULongArray.set-k8EXiF4(array, j, tmp);
         i++;
         j--;
      }
   }

   return i;
}

@ExperimentalUnsignedTypes
private fun quickSort(array: ULongArray, left: Int, right: Int) {
   var index: Int = partition--nroSd4(array, left, right);
   if (left < index - 1) {
      quickSort--nroSd4(array, left, index - 1);
   }

   if (index < right) {
      quickSort--nroSd4(array, index, right);
   }
}

@ExperimentalUnsignedTypes
internal fun sortArray(array: UByteArray, fromIndex: Int, toIndex: Int) {
   quickSort-4UcCI2c(array, fromIndex, toIndex - 1);
}

@ExperimentalUnsignedTypes
internal fun sortArray(array: UShortArray, fromIndex: Int, toIndex: Int) {
   quickSort-Aa5vz7o(array, fromIndex, toIndex - 1);
}

@ExperimentalUnsignedTypes
internal fun sortArray(array: UIntArray, fromIndex: Int, toIndex: Int) {
   quickSort-oBK06Vg(array, fromIndex, toIndex - 1);
}

@ExperimentalUnsignedTypes
internal fun sortArray(array: ULongArray, fromIndex: Int, toIndex: Int) {
   quickSort--nroSd4(array, fromIndex, toIndex - 1);
}

