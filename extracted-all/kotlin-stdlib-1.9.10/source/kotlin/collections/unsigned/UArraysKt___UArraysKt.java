package kotlin.collections.unsigned

import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\n_UArrays.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _UArrays.kt\nkotlin/collections/unsigned/UArraysKt___UArraysKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,10733:1\n3775#1:10734\n3783#1:10735\n3791#1:10736\n3799#1:10737\n3775#1:10738\n3783#1:10739\n3791#1:10740\n3799#1:10741\n3775#1:10742\n3783#1:10743\n3791#1:10744\n3799#1:10745\n3775#1:10795\n3783#1:10796\n3791#1:10797\n3799#1:10798\n3775#1:10799\n3783#1:10800\n3791#1:10801\n3799#1:10802\n3775#1:10803\n3783#1:10804\n3791#1:10805\n3799#1:10806\n3775#1:10863\n3783#1:10864\n3791#1:10865\n3799#1:10866\n3775#1:10867\n3783#1:10868\n3791#1:10869\n3799#1:10870\n3775#1:10871\n3783#1:10872\n3791#1:10873\n3799#1:10874\n3775#1:10875\n3783#1:10876\n3791#1:10877\n3799#1:10878\n3775#1:10879\n3783#1:10880\n3791#1:10881\n3799#1:10882\n3775#1:10883\n3783#1:10884\n3791#1:10885\n3799#1:10886\n3775#1:10887\n3783#1:10888\n3791#1:10889\n3799#1:10890\n3775#1:10891\n3783#1:10892\n3791#1:10893\n3799#1:10894\n3775#1:10895\n3783#1:10896\n3791#1:10897\n3799#1:10898\n3775#1:10899\n3783#1:10900\n3791#1:10901\n3799#1:10902\n3775#1:10903\n3783#1:10904\n3791#1:10905\n3799#1:10906\n3775#1:10907\n3783#1:10908\n3791#1:10909\n3799#1:10910\n3775#1:10911\n3783#1:10912\n3791#1:10913\n3799#1:10914\n3775#1:10915\n3783#1:10916\n3791#1:10917\n3799#1:10918\n3775#1:10919\n3783#1:10920\n3791#1:10921\n3799#1:10922\n3775#1:10923\n3783#1:10924\n3791#1:10925\n3799#1:10926\n3775#1:10927\n3783#1:10928\n3791#1:10929\n3799#1:10930\n3775#1:10931\n3783#1:10932\n3791#1:10933\n3799#1:10934\n3775#1:10935\n3783#1:10936\n3791#1:10937\n3799#1:10938\n3775#1:10939\n3783#1:10940\n3791#1:10941\n3799#1:10942\n3775#1:10943\n3783#1:10944\n3791#1:10945\n3799#1:10946\n3775#1:10947\n3783#1:10948\n3791#1:10949\n3799#1:10950\n3775#1:10951\n3783#1:10952\n3791#1:10953\n3799#1:10954\n3775#1:10955\n3783#1:10956\n3791#1:10957\n3799#1:10958\n3775#1:10959\n3783#1:10960\n3791#1:10961\n3799#1:10962\n3775#1:10963\n3783#1:10964\n3791#1:10965\n3799#1:10966\n3775#1:10967\n3783#1:10968\n3791#1:10969\n3799#1:10970\n3775#1:10971\n3783#1:10972\n3791#1:10973\n3799#1:10974\n3775#1:10975\n3783#1:10976\n3791#1:10977\n3799#1:10978\n3775#1:10979\n3783#1:10980\n3791#1:10981\n3799#1:10982\n3775#1:10983\n3783#1:10984\n3791#1:10985\n3799#1:10986\n3775#1:10987\n3783#1:10988\n3791#1:10989\n3799#1:10990\n3775#1:10991\n3783#1:10992\n3791#1:10993\n3799#1:10994\n3775#1:10995\n3783#1:10996\n3791#1:10997\n3799#1:10998\n3775#1:10999\n3783#1:11000\n3791#1:11001\n3799#1:11002\n3775#1:11003\n3783#1:11004\n3791#1:11005\n3799#1:11006\n3775#1:11007\n3783#1:11008\n3791#1:11009\n3799#1:11010\n3775#1:11011\n3783#1:11012\n3791#1:11013\n3799#1:11014\n1663#2,6:10746\n1675#2,6:10752\n1639#2,6:10758\n1651#2,6:10764\n1771#2,6:10770\n1783#2,6:10776\n1747#2,6:10782\n1759#2,6:10788\n1#3:10794\n372#4,7:10807\n372#4,7:10814\n372#4,7:10821\n372#4,7:10828\n372#4,7:10835\n372#4,7:10842\n372#4,7:10849\n372#4,7:10856\n*S KotlinDebug\n*F\n+ 1 _UArrays.kt\nkotlin/collections/unsigned/UArraysKt___UArraysKt\n*L\n326#1:10734\n338#1:10735\n350#1:10736\n362#1:10737\n692#1:10738\n702#1:10739\n712#1:10740\n722#1:10741\n733#1:10742\n744#1:10743\n755#1:10744\n766#1:10745\n1718#1:10795\n1735#1:10796\n1752#1:10797\n1769#1:10798\n2546#1:10799\n2563#1:10800\n2580#1:10801\n2597#1:10802\n2913#1:10803\n2929#1:10804\n2945#1:10805\n2961#1:10806\n5573#1:10863\n5593#1:10864\n5613#1:10865\n5633#1:10866\n5654#1:10867\n5676#1:10868\n5698#1:10869\n5720#1:10870\n5833#1:10871\n5852#1:10872\n5871#1:10873\n5890#1:10874\n5912#1:10875\n5941#1:10876\n5970#1:10877\n5999#1:10878\n6024#1:10879\n6049#1:10880\n6074#1:10881\n6099#1:10882\n6129#1:10883\n6152#1:10884\n6175#1:10885\n6198#1:10886\n6221#1:10887\n6244#1:10888\n6267#1:10889\n6290#1:10890\n6311#1:10891\n6334#1:10892\n6357#1:10893\n6380#1:10894\n6403#1:10895\n6424#1:10896\n6445#1:10897\n6466#1:10898\n6487#1:10899\n6508#1:10900\n6529#1:10901\n6550#1:10902\n6569#1:10903\n6590#1:10904\n6611#1:10905\n6632#1:10906\n6655#1:10907\n6678#1:10908\n6701#1:10909\n6724#1:10910\n6745#1:10911\n6766#1:10912\n6787#1:10913\n6808#1:10914\n6825#1:10915\n6840#1:10916\n6855#1:10917\n6870#1:10918\n6889#1:10919\n6908#1:10920\n6927#1:10921\n6946#1:10922\n6961#1:10923\n6976#1:10924\n6991#1:10925\n7006#1:10926\n7025#1:10927\n7044#1:10928\n7063#1:10929\n7082#1:10930\n7104#1:10931\n7133#1:10932\n7162#1:10933\n7191#1:10934\n7216#1:10935\n7241#1:10936\n7266#1:10937\n7291#1:10938\n7321#1:10939\n7344#1:10940\n7367#1:10941\n7390#1:10942\n7413#1:10943\n7436#1:10944\n7459#1:10945\n7482#1:10946\n7503#1:10947\n7526#1:10948\n7549#1:10949\n7572#1:10950\n7595#1:10951\n7616#1:10952\n7637#1:10953\n7658#1:10954\n7679#1:10955\n7700#1:10956\n7721#1:10957\n7742#1:10958\n7761#1:10959\n7782#1:10960\n7803#1:10961\n7824#1:10962\n7847#1:10963\n7870#1:10964\n7893#1:10965\n7916#1:10966\n7937#1:10967\n7958#1:10968\n7979#1:10969\n8000#1:10970\n8017#1:10971\n8032#1:10972\n8047#1:10973\n8062#1:10974\n8081#1:10975\n8100#1:10976\n8119#1:10977\n8138#1:10978\n8153#1:10979\n8168#1:10980\n8183#1:10981\n8198#1:10982\n8416#1:10983\n8441#1:10984\n8466#1:10985\n8491#1:10986\n8516#1:10987\n8541#1:10988\n8566#1:10989\n8591#1:10990\n8615#1:10991\n8639#1:10992\n8663#1:10993\n8687#1:10994\n8712#1:10995\n8737#1:10996\n8762#1:10997\n8787#1:10998\n8809#1:10999\n8834#1:11000\n8859#1:11001\n8884#1:11002\n8909#1:11003\n8935#1:11004\n8961#1:11005\n8987#1:11006\n9012#1:11007\n9037#1:11008\n9062#1:11009\n9087#1:11010\n9113#1:11011\n9138#1:11012\n9163#1:11013\n9188#1:11014\n816#1:10746,6\n826#1:10752,6\n836#1:10758,6\n846#1:10764,6\n856#1:10770,6\n866#1:10776,6\n876#1:10782,6\n886#1:10788,6\n4791#1:10807,7\n4811#1:10814,7\n4831#1:10821,7\n4851#1:10828,7\n4872#1:10835,7\n4893#1:10842,7\n4914#1:10849,7\n4935#1:10856,7\n*E\n"])
// $VF: Class flags could not be determined
internal class UArraysKt___UArraysKt : UArraysKt___UArraysJvmKt {
   public final val indices: IntRange
      public final inline get() {
         return ArraysKt.getIndices(`$this$indices`);
      }

   public final val indices: IntRange
      public final inline get() {
         return ArraysKt.getIndices(`$this$indices`);
      }

   public final val indices: IntRange
      public final inline get() {
         return ArraysKt.getIndices(`$this$indices`);
      }

   public final val indices: IntRange
      public final inline get() {
         return ArraysKt.getIndices(`$this$indices`);
      }

   public final val lastIndex: Int
      public final inline get() {
         return ArraysKt.getLastIndex(`$this$lastIndex`);
      }

   public final val lastIndex: Int
      public final inline get() {
         return ArraysKt.getLastIndex(`$this$lastIndex`);
      }

   public final val lastIndex: Int
      public final inline get() {
         return ArraysKt.getLastIndex(`$this$lastIndex`);
      }

   public final val lastIndex: Int
      public final inline get() {
         return ArraysKt.getLastIndex(`$this$lastIndex`);
      }


   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.component1(): UInt {
      return UIntArray.get-pVg5ArA(`$this$component1_u2d_u2dajY_u2d9A`, 0);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.component1(): ULong {
      return ULongArray.get-s-VKNKU(`$this$component1_u2dQwZRm1k`, 0);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.component1(): UByte {
      return UByteArray.get-w2LRezQ(`$this$component1_u2dGBYM_sE`, 0);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.component1(): UShort {
      return UShortArray.get-Mh2AYeg(`$this$component1_u2drL5Bavg`, 0);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.component2(): UInt {
      return UIntArray.get-pVg5ArA(`$this$component2_u2d_u2dajY_u2d9A`, 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.component2(): ULong {
      return ULongArray.get-s-VKNKU(`$this$component2_u2dQwZRm1k`, 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.component2(): UByte {
      return UByteArray.get-w2LRezQ(`$this$component2_u2dGBYM_sE`, 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.component2(): UShort {
      return UShortArray.get-Mh2AYeg(`$this$component2_u2drL5Bavg`, 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.component3(): UInt {
      return UIntArray.get-pVg5ArA(`$this$component3_u2d_u2dajY_u2d9A`, 2);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.component3(): ULong {
      return ULongArray.get-s-VKNKU(`$this$component3_u2dQwZRm1k`, 2);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.component3(): UByte {
      return UByteArray.get-w2LRezQ(`$this$component3_u2dGBYM_sE`, 2);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.component3(): UShort {
      return UShortArray.get-Mh2AYeg(`$this$component3_u2drL5Bavg`, 2);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.component4(): UInt {
      return UIntArray.get-pVg5ArA(`$this$component4_u2d_u2dajY_u2d9A`, 3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.component4(): ULong {
      return ULongArray.get-s-VKNKU(`$this$component4_u2dQwZRm1k`, 3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.component4(): UByte {
      return UByteArray.get-w2LRezQ(`$this$component4_u2dGBYM_sE`, 3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.component4(): UShort {
      return UShortArray.get-Mh2AYeg(`$this$component4_u2drL5Bavg`, 3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.component5(): UInt {
      return UIntArray.get-pVg5ArA(`$this$component5_u2d_u2dajY_u2d9A`, 4);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.component5(): ULong {
      return ULongArray.get-s-VKNKU(`$this$component5_u2dQwZRm1k`, 4);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.component5(): UByte {
      return UByteArray.get-w2LRezQ(`$this$component5_u2dGBYM_sE`, 4);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.component5(): UShort {
      return UShortArray.get-Mh2AYeg(`$this$component5_u2drL5Bavg`, 4);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.elementAtOrElse(index: Int, defaultValue: (Int) -> UInt): UInt {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse_u2dQxvSvLU`))
         UIntArray.get-pVg5ArA(`$this$elementAtOrElse_u2dQxvSvLU`, index)
         else
         (defaultValue.invoke(index) as UInt).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.elementAtOrElse(index: Int, defaultValue: (Int) -> ULong): ULong {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse_u2dXw8i6dc`))
         ULongArray.get-s-VKNKU(`$this$elementAtOrElse_u2dXw8i6dc`, index)
         else
         (defaultValue.invoke(index) as ULong).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.elementAtOrElse(index: Int, defaultValue: (Int) -> UByte): UByte {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse_u2dcO_u2dVybQ`))
         UByteArray.get-w2LRezQ(`$this$elementAtOrElse_u2dcO_u2dVybQ`, index)
         else
         (defaultValue.invoke(index) as UByte).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.elementAtOrElse(index: Int, defaultValue: (Int) -> UShort): UShort {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse_u2dCVVdw08`))
         UShortArray.get-Mh2AYeg(`$this$elementAtOrElse_u2dCVVdw08`, index)
         else
         (defaultValue.invoke(index) as UShort).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.elementAtOrNull(index: Int): UInt? {
      return UArraysKt.getOrNull-qFRl0hI(`$this$elementAtOrNull_u2dqFRl0hI`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.elementAtOrNull(index: Int): ULong? {
      return UArraysKt.getOrNull-r7IrZao(`$this$elementAtOrNull_u2dr7IrZao`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.elementAtOrNull(index: Int): UByte? {
      return UArraysKt.getOrNull-PpDY95g(`$this$elementAtOrNull_u2dPpDY95g`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.elementAtOrNull(index: Int): UShort? {
      return UArraysKt.getOrNull-nggk6HY(`$this$elementAtOrNull_u2dnggk6HY`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.find(predicate: (UInt) -> Boolean): UInt? {
      var var2: IntArray = `$this$find_u2djgv0xPQ`;
      var var3: Int = 0;
      var var4: Int = UIntArray.getSize-impl(`$this$find_u2djgv0xPQ`);

      var var10000: UInt;
      while (true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         var var5: Int = UIntArray.get-pVg5ArA(var2, var3);
         if (predicate.invoke(UInt.box-impl(var5)) as java.lang.Boolean) {
            var10000 = UInt.box-impl(var5);
            break;
         }

         var3++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.find(predicate: (ULong) -> Boolean): ULong? {
      var var2: LongArray = `$this$find_u2dMShoTSo`;
      var var3: Int = 0;
      var var4: Int = ULongArray.getSize-impl(`$this$find_u2dMShoTSo`);

      var var10000: ULong;
      while (true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         var var5: Long = ULongArray.get-s-VKNKU(var2, var3);
         if (predicate.invoke(ULong.box-impl(var5)) as java.lang.Boolean) {
            var10000 = ULong.box-impl(var5);
            break;
         }

         var3++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.find(predicate: (UByte) -> Boolean): UByte? {
      var var2: ByteArray = `$this$find_u2dJOV_ifY`;
      var var3: Int = 0;
      var var4: Int = UByteArray.getSize-impl(`$this$find_u2dJOV_ifY`);

      var var10000: UByte;
      while (true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         var var5: Byte = UByteArray.get-w2LRezQ(var2, var3);
         if (predicate.invoke(UByte.box-impl(var5)) as java.lang.Boolean) {
            var10000 = UByte.box-impl(var5);
            break;
         }

         var3++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.find(predicate: (UShort) -> Boolean): UShort? {
      var var2: ShortArray = `$this$find_u2dxTcfx_M`;
      var var3: Int = 0;
      var var4: Int = UShortArray.getSize-impl(`$this$find_u2dxTcfx_M`);

      var var10000: UShort;
      while (true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         var var5: Short = UShortArray.get-Mh2AYeg(var2, var3);
         if (predicate.invoke(UShort.box-impl(var5)) as java.lang.Boolean) {
            var10000 = UShort.box-impl(var5);
            break;
         }

         var3++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.findLast(predicate: (UInt) -> Boolean): UInt? {
      var var2: IntArray = `$this$findLast_u2djgv0xPQ`;
      var var3: Int = UIntArray.getSize-impl(`$this$findLast_u2djgv0xPQ`) + -1;
      if (0 <= var3) {
         do {
            var var5: Int = UIntArray.get-pVg5ArA(var2, var3--);
            if (predicate.invoke(UInt.box-impl(var5)) as java.lang.Boolean) {
               return UInt.box-impl(var5);
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.findLast(predicate: (ULong) -> Boolean): ULong? {
      var var2: LongArray = `$this$findLast_u2dMShoTSo`;
      var var3: Int = ULongArray.getSize-impl(`$this$findLast_u2dMShoTSo`) + -1;
      if (0 <= var3) {
         do {
            var var5: Long = ULongArray.get-s-VKNKU(var2, var3--);
            if (predicate.invoke(ULong.box-impl(var5)) as java.lang.Boolean) {
               return ULong.box-impl(var5);
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.findLast(predicate: (UByte) -> Boolean): UByte? {
      var var2: ByteArray = `$this$findLast_u2dJOV_ifY`;
      var var3: Int = UByteArray.getSize-impl(`$this$findLast_u2dJOV_ifY`) + -1;
      if (0 <= var3) {
         do {
            var var5: Byte = UByteArray.get-w2LRezQ(var2, var3--);
            if (predicate.invoke(UByte.box-impl(var5)) as java.lang.Boolean) {
               return UByte.box-impl(var5);
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.findLast(predicate: (UShort) -> Boolean): UShort? {
      var var2: ShortArray = `$this$findLast_u2dxTcfx_M`;
      var var3: Int = UShortArray.getSize-impl(`$this$findLast_u2dxTcfx_M`) + -1;
      if (0 <= var3) {
         do {
            var var5: Short = UShortArray.get-Mh2AYeg(var2, var3--);
            if (predicate.invoke(UShort.box-impl(var5)) as java.lang.Boolean) {
               return UShort.box-impl(var5);
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.first(): UInt {
      return UInt.constructor-impl(ArraysKt.first(`$this$first_u2d_u2dajY_u2d9A`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.first(): ULong {
      return ULong.constructor-impl(ArraysKt.first(`$this$first_u2dQwZRm1k`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.first(): UByte {
      return UByte.constructor-impl(ArraysKt.first(`$this$first_u2dGBYM_sE`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.first(): UShort {
      return UShort.constructor-impl(ArraysKt.first(`$this$first_u2drL5Bavg`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.first(predicate: (UInt) -> Boolean): UInt {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$first_u2djgv0xPQ); var2 < var3; var2++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$first_u2djgv0xPQ`, var2);
         if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.first(predicate: (ULong) -> Boolean): ULong {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$first_u2dMShoTSo); var2 < var3; var2++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$first_u2dMShoTSo`, var2);
         if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.first(predicate: (UByte) -> Boolean): UByte {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$first_u2dJOV_ifY); var2 < var3; var2++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$first_u2dJOV_ifY`, var2);
         if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.first(predicate: (UShort) -> Boolean): UShort {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$first_u2dxTcfx_M); var2 < var3; var2++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$first_u2dxTcfx_M`, var2);
         if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.firstOrNull(): UInt? {
      return if (UIntArray.isEmpty-impl(`$this$firstOrNull_u2d_u2dajY_u2d9A`))
         null
         else
         UInt.box-impl(UIntArray.get-pVg5ArA(`$this$firstOrNull_u2d_u2dajY_u2d9A`, 0));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.firstOrNull(): ULong? {
      return if (ULongArray.isEmpty-impl(`$this$firstOrNull_u2dQwZRm1k`)) null else ULong.box-impl(ULongArray.get-s-VKNKU(`$this$firstOrNull_u2dQwZRm1k`, 0));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.firstOrNull(): UByte? {
      return if (UByteArray.isEmpty-impl(`$this$firstOrNull_u2dGBYM_sE`)) null else UByte.box-impl(UByteArray.get-w2LRezQ(`$this$firstOrNull_u2dGBYM_sE`, 0));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.firstOrNull(): UShort? {
      return if (UShortArray.isEmpty-impl(`$this$firstOrNull_u2drL5Bavg`))
         null
         else
         UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$firstOrNull_u2drL5Bavg`, 0));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.firstOrNull(predicate: (UInt) -> Boolean): UInt? {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$firstOrNull_u2djgv0xPQ); var2 < var3; var2++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$firstOrNull_u2djgv0xPQ`, var2);
         if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            return UInt.box-impl(element);
         }
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.firstOrNull(predicate: (ULong) -> Boolean): ULong? {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$firstOrNull_u2dMShoTSo); var2 < var3; var2++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$firstOrNull_u2dMShoTSo`, var2);
         if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            return ULong.box-impl(element);
         }
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.firstOrNull(predicate: (UByte) -> Boolean): UByte? {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$firstOrNull_u2dJOV_ifY); var2 < var3; var2++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$firstOrNull_u2dJOV_ifY`, var2);
         if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            return UByte.box-impl(element);
         }
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.firstOrNull(predicate: (UShort) -> Boolean): UShort? {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$firstOrNull_u2dxTcfx_M); var2 < var3; var2++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$firstOrNull_u2dxTcfx_M`, var2);
         if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            return UShort.box-impl(element);
         }
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.getOrElse(index: Int, defaultValue: (Int) -> UInt): UInt {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse_u2dQxvSvLU`))
         UIntArray.get-pVg5ArA(`$this$getOrElse_u2dQxvSvLU`, index)
         else
         (defaultValue.invoke(index) as UInt).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.getOrElse(index: Int, defaultValue: (Int) -> ULong): ULong {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse_u2dXw8i6dc`))
         ULongArray.get-s-VKNKU(`$this$getOrElse_u2dXw8i6dc`, index)
         else
         (defaultValue.invoke(index) as ULong).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.getOrElse(index: Int, defaultValue: (Int) -> UByte): UByte {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse_u2dcO_u2dVybQ`))
         UByteArray.get-w2LRezQ(`$this$getOrElse_u2dcO_u2dVybQ`, index)
         else
         (defaultValue.invoke(index) as UByte).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.getOrElse(index: Int, defaultValue: (Int) -> UShort): UShort {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse_u2dCVVdw08`))
         UShortArray.get-Mh2AYeg(`$this$getOrElse_u2dCVVdw08`, index)
         else
         (defaultValue.invoke(index) as UShort).unbox-impl();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.getOrNull(index: Int): UInt? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull_u2dqFRl0hI`))
         UInt.box-impl(UIntArray.get-pVg5ArA(`$this$getOrNull_u2dqFRl0hI`, index))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.getOrNull(index: Int): ULong? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull_u2dr7IrZao`))
         ULong.box-impl(ULongArray.get-s-VKNKU(`$this$getOrNull_u2dr7IrZao`, index))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.getOrNull(index: Int): UByte? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull_u2dPpDY95g`))
         UByte.box-impl(UByteArray.get-w2LRezQ(`$this$getOrNull_u2dPpDY95g`, index))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.getOrNull(index: Int): UShort? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull_u2dnggk6HY`))
         UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$getOrNull_u2dnggk6HY`, index))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.indexOf(element: UInt): Int {
      return ArraysKt.indexOf(`$this$indexOf_u2duWY9BYg`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.indexOf(element: ULong): Int {
      return ArraysKt.indexOf(`$this$indexOf_u2d3uqUaXg`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.indexOf(element: UByte): Int {
      return ArraysKt.indexOf(`$this$indexOf_u2dgMuBH34`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.indexOf(element: UShort): Int {
      return ArraysKt.indexOf(`$this$indexOf_u2dXzdR7RA`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.indexOfFirst(predicate: (UInt) -> Boolean): Int {
      var `$this$indexOfFirst$iv`: IntArray = `$this$indexOfFirst_u2djgv0xPQ`;
      var `index$iv`: Int = 0;
      var var5: Int = `$this$indexOfFirst_u2djgv0xPQ`.length;

      var var10000: Int;
      while (true) {
         if (`index$iv` >= var5) {
            var10000 = -1;
            break;
         }

         if (predicate.invoke(UInt.box-impl(UInt.constructor-impl(`$this$indexOfFirst$iv`[`index$iv`]))) as java.lang.Boolean) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.indexOfFirst(predicate: (ULong) -> Boolean): Int {
      var `$this$indexOfFirst$iv`: LongArray = `$this$indexOfFirst_u2dMShoTSo`;
      var `index$iv`: Int = 0;
      var var5: Int = `$this$indexOfFirst_u2dMShoTSo`.length;

      var var10000: Int;
      while (true) {
         if (`index$iv` >= var5) {
            var10000 = -1;
            break;
         }

         if (predicate.invoke(ULong.box-impl(ULong.constructor-impl(`$this$indexOfFirst$iv`[`index$iv`]))) as java.lang.Boolean) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.indexOfFirst(predicate: (UByte) -> Boolean): Int {
      var `$this$indexOfFirst$iv`: ByteArray = `$this$indexOfFirst_u2dJOV_ifY`;
      var `index$iv`: Int = 0;
      var var5: Int = `$this$indexOfFirst_u2dJOV_ifY`.length;

      var var10000: Int;
      while (true) {
         if (`index$iv` >= var5) {
            var10000 = -1;
            break;
         }

         if (predicate.invoke(UByte.box-impl(UByte.constructor-impl(`$this$indexOfFirst$iv`[`index$iv`]))) as java.lang.Boolean) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.indexOfFirst(predicate: (UShort) -> Boolean): Int {
      var `$this$indexOfFirst$iv`: ShortArray = `$this$indexOfFirst_u2dxTcfx_M`;
      var `index$iv`: Int = 0;
      var var5: Int = `$this$indexOfFirst_u2dxTcfx_M`.length;

      var var10000: Int;
      while (true) {
         if (`index$iv` >= var5) {
            var10000 = -1;
            break;
         }

         if (predicate.invoke(UShort.box-impl(UShort.constructor-impl(`$this$indexOfFirst$iv`[`index$iv`]))) as java.lang.Boolean) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.indexOfLast(predicate: (UInt) -> Boolean): Int {
      var `$this$indexOfLast$iv`: IntArray = `$this$indexOfLast_u2djgv0xPQ`;
      var var4: Int = `$this$indexOfLast_u2djgv0xPQ`.length + -1;
      if (0 <= `$this$indexOfLast_u2djgv0xPQ`.length + -1) {
         do {
            var `index$iv`: Int = var4--;
            if (predicate.invoke(UInt.box-impl(UInt.constructor-impl(`$this$indexOfLast$iv`[`index$iv`]))) as java.lang.Boolean) {
               return `index$iv`;
            }
         } while (0 <= var4);
      }

      return -1;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.indexOfLast(predicate: (ULong) -> Boolean): Int {
      var `$this$indexOfLast$iv`: LongArray = `$this$indexOfLast_u2dMShoTSo`;
      var var4: Int = `$this$indexOfLast_u2dMShoTSo`.length + -1;
      if (0 <= `$this$indexOfLast_u2dMShoTSo`.length + -1) {
         do {
            var `index$iv`: Int = var4--;
            if (predicate.invoke(ULong.box-impl(ULong.constructor-impl(`$this$indexOfLast$iv`[`index$iv`]))) as java.lang.Boolean) {
               return `index$iv`;
            }
         } while (0 <= var4);
      }

      return -1;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.indexOfLast(predicate: (UByte) -> Boolean): Int {
      var `$this$indexOfLast$iv`: ByteArray = `$this$indexOfLast_u2dJOV_ifY`;
      var var4: Int = `$this$indexOfLast_u2dJOV_ifY`.length + -1;
      if (0 <= `$this$indexOfLast_u2dJOV_ifY`.length + -1) {
         do {
            var `index$iv`: Int = var4--;
            if (predicate.invoke(UByte.box-impl(UByte.constructor-impl(`$this$indexOfLast$iv`[`index$iv`]))) as java.lang.Boolean) {
               return `index$iv`;
            }
         } while (0 <= var4);
      }

      return -1;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.indexOfLast(predicate: (UShort) -> Boolean): Int {
      var `$this$indexOfLast$iv`: ShortArray = `$this$indexOfLast_u2dxTcfx_M`;
      var var4: Int = `$this$indexOfLast_u2dxTcfx_M`.length + -1;
      if (0 <= `$this$indexOfLast_u2dxTcfx_M`.length + -1) {
         do {
            var `index$iv`: Int = var4--;
            if (predicate.invoke(UShort.box-impl(UShort.constructor-impl(`$this$indexOfLast$iv`[`index$iv`]))) as java.lang.Boolean) {
               return `index$iv`;
            }
         } while (0 <= var4);
      }

      return -1;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.last(): UInt {
      return UInt.constructor-impl(ArraysKt.last(`$this$last_u2d_u2dajY_u2d9A`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.last(): ULong {
      return ULong.constructor-impl(ArraysKt.last(`$this$last_u2dQwZRm1k`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.last(): UByte {
      return UByte.constructor-impl(ArraysKt.last(`$this$last_u2dGBYM_sE`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.last(): UShort {
      return UShort.constructor-impl(ArraysKt.last(`$this$last_u2drL5Bavg`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.last(predicate: (UInt) -> Boolean): UInt {
      var var2: Int = UIntArray.getSize-impl(`$this$last_u2djgv0xPQ`) + -1;
      if (0 <= var2) {
         do {
            var element: Int = UIntArray.get-pVg5ArA(`$this$last_u2djgv0xPQ`, var2--);
            if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var2);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.last(predicate: (ULong) -> Boolean): ULong {
      var var2: Int = ULongArray.getSize-impl(`$this$last_u2dMShoTSo`) + -1;
      if (0 <= var2) {
         do {
            var element: Long = ULongArray.get-s-VKNKU(`$this$last_u2dMShoTSo`, var2--);
            if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var2);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.last(predicate: (UByte) -> Boolean): UByte {
      var var2: Int = UByteArray.getSize-impl(`$this$last_u2dJOV_ifY`) + -1;
      if (0 <= var2) {
         do {
            var element: Byte = UByteArray.get-w2LRezQ(`$this$last_u2dJOV_ifY`, var2--);
            if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var2);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.last(predicate: (UShort) -> Boolean): UShort {
      var var2: Int = UShortArray.getSize-impl(`$this$last_u2dxTcfx_M`) + -1;
      if (0 <= var2) {
         do {
            var element: Short = UShortArray.get-Mh2AYeg(`$this$last_u2dxTcfx_M`, var2--);
            if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var2);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.lastIndexOf(element: UInt): Int {
      return ArraysKt.lastIndexOf(`$this$lastIndexOf_u2duWY9BYg`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.lastIndexOf(element: ULong): Int {
      return ArraysKt.lastIndexOf(`$this$lastIndexOf_u2d3uqUaXg`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.lastIndexOf(element: UByte): Int {
      return ArraysKt.lastIndexOf(`$this$lastIndexOf_u2dgMuBH34`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.lastIndexOf(element: UShort): Int {
      return ArraysKt.lastIndexOf(`$this$lastIndexOf_u2dXzdR7RA`, element);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.lastOrNull(): UInt? {
      return if (UIntArray.isEmpty-impl(`$this$lastOrNull_u2d_u2dajY_u2d9A`))
         null
         else
         UInt.box-impl(UIntArray.get-pVg5ArA(`$this$lastOrNull_u2d_u2dajY_u2d9A`, UIntArray.getSize-impl(`$this$lastOrNull_u2d_u2dajY_u2d9A`) - 1));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.lastOrNull(): ULong? {
      return if (ULongArray.isEmpty-impl(`$this$lastOrNull_u2dQwZRm1k`))
         null
         else
         ULong.box-impl(ULongArray.get-s-VKNKU(`$this$lastOrNull_u2dQwZRm1k`, ULongArray.getSize-impl(`$this$lastOrNull_u2dQwZRm1k`) - 1));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.lastOrNull(): UByte? {
      return if (UByteArray.isEmpty-impl(`$this$lastOrNull_u2dGBYM_sE`))
         null
         else
         UByte.box-impl(UByteArray.get-w2LRezQ(`$this$lastOrNull_u2dGBYM_sE`, UByteArray.getSize-impl(`$this$lastOrNull_u2dGBYM_sE`) - 1));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.lastOrNull(): UShort? {
      return if (UShortArray.isEmpty-impl(`$this$lastOrNull_u2drL5Bavg`))
         null
         else
         UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$lastOrNull_u2drL5Bavg`, UShortArray.getSize-impl(`$this$lastOrNull_u2drL5Bavg`) - 1));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.lastOrNull(predicate: (UInt) -> Boolean): UInt? {
      var var2: Int = UIntArray.getSize-impl(`$this$lastOrNull_u2djgv0xPQ`) + -1;
      if (0 <= var2) {
         do {
            var element: Int = UIntArray.get-pVg5ArA(`$this$lastOrNull_u2djgv0xPQ`, var2--);
            if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
               return UInt.box-impl(element);
            }
         } while (0 <= var2);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.lastOrNull(predicate: (ULong) -> Boolean): ULong? {
      var var2: Int = ULongArray.getSize-impl(`$this$lastOrNull_u2dMShoTSo`) + -1;
      if (0 <= var2) {
         do {
            var element: Long = ULongArray.get-s-VKNKU(`$this$lastOrNull_u2dMShoTSo`, var2--);
            if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
               return ULong.box-impl(element);
            }
         } while (0 <= var2);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.lastOrNull(predicate: (UByte) -> Boolean): UByte? {
      var var2: Int = UByteArray.getSize-impl(`$this$lastOrNull_u2dJOV_ifY`) + -1;
      if (0 <= var2) {
         do {
            var element: Byte = UByteArray.get-w2LRezQ(`$this$lastOrNull_u2dJOV_ifY`, var2--);
            if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
               return UByte.box-impl(element);
            }
         } while (0 <= var2);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.lastOrNull(predicate: (UShort) -> Boolean): UShort? {
      var var2: Int = UShortArray.getSize-impl(`$this$lastOrNull_u2dxTcfx_M`) + -1;
      if (0 <= var2) {
         do {
            var element: Short = UShortArray.get-Mh2AYeg(`$this$lastOrNull_u2dxTcfx_M`, var2--);
            if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
               return UShort.box-impl(element);
            }
         } while (0 <= var2);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.random(): UInt {
      return UArraysKt.random-2D5oskM(`$this$random_u2d_u2dajY_u2d9A`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.random(): ULong {
      return UArraysKt.random-JzugnMA(`$this$random_u2dQwZRm1k`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.random(): UByte {
      return UArraysKt.random-oSF2wD8(`$this$random_u2dGBYM_sE`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.random(): UShort {
      return UArraysKt.random-s5X_as8(`$this$random_u2drL5Bavg`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.random(random: Random): UInt {
      if (UIntArray.isEmpty-impl(`$this$random_u2d2D5oskM`)) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return UIntArray.get-pVg5ArA(`$this$random_u2d2D5oskM`, random.nextInt(UIntArray.getSize-impl(`$this$random_u2d2D5oskM`)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.random(random: Random): ULong {
      if (ULongArray.isEmpty-impl(`$this$random_u2dJzugnMA`)) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return ULongArray.get-s-VKNKU(`$this$random_u2dJzugnMA`, random.nextInt(ULongArray.getSize-impl(`$this$random_u2dJzugnMA`)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.random(random: Random): UByte {
      if (UByteArray.isEmpty-impl(`$this$random_u2doSF2wD8`)) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return UByteArray.get-w2LRezQ(`$this$random_u2doSF2wD8`, random.nextInt(UByteArray.getSize-impl(`$this$random_u2doSF2wD8`)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.random(random: Random): UShort {
      if (UShortArray.isEmpty-impl(`$this$random_u2ds5X_as8`)) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return UShortArray.get-Mh2AYeg(`$this$random_u2ds5X_as8`, random.nextInt(UShortArray.getSize-impl(`$this$random_u2ds5X_as8`)));
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.randomOrNull(): UInt? {
      return UArraysKt.randomOrNull-2D5oskM(`$this$randomOrNull_u2d_u2dajY_u2d9A`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.randomOrNull(): ULong? {
      return UArraysKt.randomOrNull-JzugnMA(`$this$randomOrNull_u2dQwZRm1k`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.randomOrNull(): UByte? {
      return UArraysKt.randomOrNull-oSF2wD8(`$this$randomOrNull_u2dGBYM_sE`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.randomOrNull(): UShort? {
      return UArraysKt.randomOrNull-s5X_as8(`$this$randomOrNull_u2drL5Bavg`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun UIntArray.randomOrNull(random: Random): UInt? {
      return if (UIntArray.isEmpty-impl(`$this$randomOrNull_u2d2D5oskM`))
         null
         else
         UInt.box-impl(UIntArray.get-pVg5ArA(`$this$randomOrNull_u2d2D5oskM`, random.nextInt(UIntArray.getSize-impl(`$this$randomOrNull_u2d2D5oskM`))));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun ULongArray.randomOrNull(random: Random): ULong? {
      return if (ULongArray.isEmpty-impl(`$this$randomOrNull_u2dJzugnMA`))
         null
         else
         ULong.box-impl(ULongArray.get-s-VKNKU(`$this$randomOrNull_u2dJzugnMA`, random.nextInt(ULongArray.getSize-impl(`$this$randomOrNull_u2dJzugnMA`))));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun UByteArray.randomOrNull(random: Random): UByte? {
      return if (UByteArray.isEmpty-impl(`$this$randomOrNull_u2doSF2wD8`))
         null
         else
         UByte.box-impl(UByteArray.get-w2LRezQ(`$this$randomOrNull_u2doSF2wD8`, random.nextInt(UByteArray.getSize-impl(`$this$randomOrNull_u2doSF2wD8`))));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun UShortArray.randomOrNull(random: Random): UShort? {
      return if (UShortArray.isEmpty-impl(`$this$randomOrNull_u2ds5X_as8`))
         null
         else
         UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$randomOrNull_u2ds5X_as8`, random.nextInt(UShortArray.getSize-impl(`$this$randomOrNull_u2ds5X_as8`))));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.single(): UInt {
      return UInt.constructor-impl(ArraysKt.single(`$this$single_u2d_u2dajY_u2d9A`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.single(): ULong {
      return ULong.constructor-impl(ArraysKt.single(`$this$single_u2dQwZRm1k`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.single(): UByte {
      return UByte.constructor-impl(ArraysKt.single(`$this$single_u2dGBYM_sE`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.single(): UShort {
      return UShort.constructor-impl(ArraysKt.single(`$this$single_u2drL5Bavg`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.single(predicate: (UInt) -> Boolean): UInt {
      var single: UInt = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$single_u2djgv0xPQ); var4 < var5; var4++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$single_u2djgv0xPQ`, var4);
         if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = UInt.box-impl(element);
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single.unbox-impl();
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.single(predicate: (ULong) -> Boolean): ULong {
      var single: ULong = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$single_u2dMShoTSo); var4 < var5; var4++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$single_u2dMShoTSo`, var4);
         if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = ULong.box-impl(element);
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single.unbox-impl();
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.single(predicate: (UByte) -> Boolean): UByte {
      var single: UByte = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$single_u2dJOV_ifY); var4 < var5; var4++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$single_u2dJOV_ifY`, var4);
         if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = UByte.box-impl(element);
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single.unbox-impl();
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.single(predicate: (UShort) -> Boolean): UShort {
      var single: UShort = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$single_u2dxTcfx_M); var4 < var5; var4++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$single_u2dxTcfx_M`, var4);
         if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = UShort.box-impl(element);
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single.unbox-impl();
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.singleOrNull(): UInt? {
      return if (UIntArray.getSize-impl(`$this$singleOrNull_u2d_u2dajY_u2d9A`) == 1)
         UInt.box-impl(UIntArray.get-pVg5ArA(`$this$singleOrNull_u2d_u2dajY_u2d9A`, 0))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.singleOrNull(): ULong? {
      return if (ULongArray.getSize-impl(`$this$singleOrNull_u2dQwZRm1k`) == 1)
         ULong.box-impl(ULongArray.get-s-VKNKU(`$this$singleOrNull_u2dQwZRm1k`, 0))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.singleOrNull(): UByte? {
      return if (UByteArray.getSize-impl(`$this$singleOrNull_u2dGBYM_sE`) == 1)
         UByte.box-impl(UByteArray.get-w2LRezQ(`$this$singleOrNull_u2dGBYM_sE`, 0))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.singleOrNull(): UShort? {
      return if (UShortArray.getSize-impl(`$this$singleOrNull_u2drL5Bavg`) == 1)
         UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$singleOrNull_u2drL5Bavg`, 0))
         else
         null;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.singleOrNull(predicate: (UInt) -> Boolean): UInt? {
      var single: UInt = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$singleOrNull_u2djgv0xPQ); var4 < var5; var4++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$singleOrNull_u2djgv0xPQ`, var4);
         if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = UInt.box-impl(element);
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.singleOrNull(predicate: (ULong) -> Boolean): ULong? {
      var single: ULong = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$singleOrNull_u2dMShoTSo); var4 < var5; var4++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$singleOrNull_u2dMShoTSo`, var4);
         if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = ULong.box-impl(element);
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.singleOrNull(predicate: (UByte) -> Boolean): UByte? {
      var single: UByte = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$singleOrNull_u2dJOV_ifY); var4 < var5; var4++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$singleOrNull_u2dJOV_ifY`, var4);
         if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = UByte.box-impl(element);
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.singleOrNull(predicate: (UShort) -> Boolean): UShort? {
      var single: UShort = null;
      var found: Boolean = false;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$singleOrNull_u2dxTcfx_M); var4 < var5; var4++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$singleOrNull_u2dxTcfx_M`, var4);
         if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = UShort.box-impl(element);
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.drop(n: Int): List<UInt> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.takeLast-qFRl0hI(`$this$drop_u2dqFRl0hI`, RangesKt.coerceAtLeast(UIntArray.getSize-impl(`$this$drop_u2dqFRl0hI`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.drop(n: Int): List<ULong> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.takeLast-r7IrZao(`$this$drop_u2dr7IrZao`, RangesKt.coerceAtLeast(ULongArray.getSize-impl(`$this$drop_u2dr7IrZao`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.drop(n: Int): List<UByte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.takeLast-PpDY95g(`$this$drop_u2dPpDY95g`, RangesKt.coerceAtLeast(UByteArray.getSize-impl(`$this$drop_u2dPpDY95g`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.drop(n: Int): List<UShort> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.takeLast-nggk6HY(`$this$drop_u2dnggk6HY`, RangesKt.coerceAtLeast(UShortArray.getSize-impl(`$this$drop_u2dnggk6HY`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.dropLast(n: Int): List<UInt> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.take-qFRl0hI(`$this$dropLast_u2dqFRl0hI`, RangesKt.coerceAtLeast(UIntArray.getSize-impl(`$this$dropLast_u2dqFRl0hI`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.dropLast(n: Int): List<ULong> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.take-r7IrZao(`$this$dropLast_u2dr7IrZao`, RangesKt.coerceAtLeast(ULongArray.getSize-impl(`$this$dropLast_u2dr7IrZao`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.dropLast(n: Int): List<UByte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.take-PpDY95g(`$this$dropLast_u2dPpDY95g`, RangesKt.coerceAtLeast(UByteArray.getSize-impl(`$this$dropLast_u2dPpDY95g`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.dropLast(n: Int): List<UShort> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return UArraysKt.take-nggk6HY(`$this$dropLast_u2dnggk6HY`, RangesKt.coerceAtLeast(UShortArray.getSize-impl(`$this$dropLast_u2dnggk6HY`) - n, 0));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.dropLastWhile(predicate: (UInt) -> Boolean): List<UInt> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile_u2djgv0xPQ); -1 < index; index--) {
         if (!predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$dropLastWhile_u2djgv0xPQ`, index))) as java.lang.Boolean) {
            return UArraysKt.take-qFRl0hI(`$this$dropLastWhile_u2djgv0xPQ`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.dropLastWhile(predicate: (ULong) -> Boolean): List<ULong> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile_u2dMShoTSo); -1 < index; index--) {
         if (!predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$dropLastWhile_u2dMShoTSo`, index))) as java.lang.Boolean) {
            return UArraysKt.take-r7IrZao(`$this$dropLastWhile_u2dMShoTSo`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.dropLastWhile(predicate: (UByte) -> Boolean): List<UByte> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile_u2dJOV_ifY); -1 < index; index--) {
         if (!predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$dropLastWhile_u2dJOV_ifY`, index))) as java.lang.Boolean) {
            return UArraysKt.take-PpDY95g(`$this$dropLastWhile_u2dJOV_ifY`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.dropLastWhile(predicate: (UShort) -> Boolean): List<UShort> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile_u2dxTcfx_M); -1 < index; index--) {
         if (!predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$dropLastWhile_u2dxTcfx_M`, index))) as java.lang.Boolean) {
            return UArraysKt.take-nggk6HY(`$this$dropLastWhile_u2dxTcfx_M`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.dropWhile(predicate: (UInt) -> Boolean): List<UInt> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$dropWhile_u2djgv0xPQ); var4 < var5; var4++) {
         var item: Int = UIntArray.get-pVg5ArA(`$this$dropWhile_u2djgv0xPQ`, var4);
         if (yielding) {
            list.add(UInt.box-impl(item));
         } else if (!predicate.invoke(UInt.box-impl(item)) as java.lang.Boolean) {
            list.add(UInt.box-impl(item));
            yielding = true;
         }
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.dropWhile(predicate: (ULong) -> Boolean): List<ULong> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$dropWhile_u2dMShoTSo); var4 < var5; var4++) {
         var item: Long = ULongArray.get-s-VKNKU(`$this$dropWhile_u2dMShoTSo`, var4);
         if (yielding) {
            list.add(ULong.box-impl(item));
         } else if (!predicate.invoke(ULong.box-impl(item)) as java.lang.Boolean) {
            list.add(ULong.box-impl(item));
            yielding = true;
         }
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.dropWhile(predicate: (UByte) -> Boolean): List<UByte> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$dropWhile_u2dJOV_ifY); var4 < var5; var4++) {
         var item: Byte = UByteArray.get-w2LRezQ(`$this$dropWhile_u2dJOV_ifY`, var4);
         if (yielding) {
            list.add(UByte.box-impl(item));
         } else if (!predicate.invoke(UByte.box-impl(item)) as java.lang.Boolean) {
            list.add(UByte.box-impl(item));
            yielding = true;
         }
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.dropWhile(predicate: (UShort) -> Boolean): List<UShort> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$dropWhile_u2dxTcfx_M); var4 < var5; var4++) {
         var item: Short = UShortArray.get-Mh2AYeg(`$this$dropWhile_u2dxTcfx_M`, var4);
         if (yielding) {
            list.add(UShort.box-impl(item));
         } else if (!predicate.invoke(UShort.box-impl(item)) as java.lang.Boolean) {
            list.add(UShort.box-impl(item));
            yielding = true;
         }
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.filter(predicate: (UInt) -> Boolean): List<UInt> {
      var var2: IntArray = `$this$filter_u2djgv0xPQ`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$filter_u2djgv0xPQ); var4 < var5; var4++) {
         var var6: Int = UIntArray.get-pVg5ArA(var2, var4);
         if (predicate.invoke(UInt.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UInt.box-impl(var6));
         }
      }

      return var3 as java.util.List<UInt>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.filter(predicate: (ULong) -> Boolean): List<ULong> {
      var var2: LongArray = `$this$filter_u2dMShoTSo`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$filter_u2dMShoTSo); var4 < var5; var4++) {
         var var6: Long = ULongArray.get-s-VKNKU(var2, var4);
         if (predicate.invoke(ULong.box-impl(var6)) as java.lang.Boolean) {
            var3.add(ULong.box-impl(var6));
         }
      }

      return var3 as java.util.List<ULong>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.filter(predicate: (UByte) -> Boolean): List<UByte> {
      var var2: ByteArray = `$this$filter_u2dJOV_ifY`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$filter_u2dJOV_ifY); var4 < var5; var4++) {
         var var6: Byte = UByteArray.get-w2LRezQ(var2, var4);
         if (predicate.invoke(UByte.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UByte.box-impl(var6));
         }
      }

      return var3 as java.util.List<UByte>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.filter(predicate: (UShort) -> Boolean): List<UShort> {
      var var2: ShortArray = `$this$filter_u2dxTcfx_M`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$filter_u2dxTcfx_M); var4 < var5; var4++) {
         var var6: Short = UShortArray.get-Mh2AYeg(var2, var4);
         if (predicate.invoke(UShort.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UShort.box-impl(var6));
         }
      }

      return var3 as java.util.List<UShort>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.filterIndexed(predicate: (Int, UInt) -> Boolean): List<UInt> {
      var var3: java.util.Collection = new ArrayList();
      var var4: IntArray = `$this$filterIndexed_u2dWyvcNBI`;
      var var5: Int = 0;
      var var6: Int = 0;

      for (int var7 = UIntArray.getSize-impl($this$filterIndexed_u2dWyvcNBI); var6 < var7; var6++) {
         var var8: Int = UIntArray.get-pVg5ArA(var4, var6);
         if (predicate.invoke(var5++, UInt.box-impl(var8)) as java.lang.Boolean) {
            var3.add(UInt.box-impl(var8));
         }
      }

      return var3 as java.util.List<UInt>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.filterIndexed(predicate: (Int, ULong) -> Boolean): List<ULong> {
      var var3: java.util.Collection = new ArrayList();
      var var4: LongArray = `$this$filterIndexed_u2ds8dVfGU`;
      var var5: Int = 0;
      var var6: Int = 0;

      for (int var7 = ULongArray.getSize-impl($this$filterIndexed_u2ds8dVfGU); var6 < var7; var6++) {
         var var8: Long = ULongArray.get-s-VKNKU(var4, var6);
         if (predicate.invoke(var5++, ULong.box-impl(var8)) as java.lang.Boolean) {
            var3.add(ULong.box-impl(var8));
         }
      }

      return var3 as java.util.List<ULong>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.filterIndexed(predicate: (Int, UByte) -> Boolean): List<UByte> {
      var var3: java.util.Collection = new ArrayList();
      var var4: ByteArray = `$this$filterIndexed_u2dELGow60`;
      var var5: Int = 0;
      var var6: Int = 0;

      for (int var7 = UByteArray.getSize-impl($this$filterIndexed_u2dELGow60); var6 < var7; var6++) {
         var var8: Byte = UByteArray.get-w2LRezQ(var4, var6);
         if (predicate.invoke(var5++, UByte.box-impl(var8)) as java.lang.Boolean) {
            var3.add(UByte.box-impl(var8));
         }
      }

      return var3 as java.util.List<UByte>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.filterIndexed(predicate: (Int, UShort) -> Boolean): List<UShort> {
      var var3: java.util.Collection = new ArrayList();
      var var4: ShortArray = `$this$filterIndexed_u2dxzaTVY8`;
      var var5: Int = 0;
      var var6: Int = 0;

      for (int var7 = UShortArray.getSize-impl($this$filterIndexed_u2dxzaTVY8); var6 < var7; var6++) {
         var var8: Short = UShortArray.get-Mh2AYeg(var4, var6);
         if (predicate.invoke(var5++, UShort.box-impl(var8)) as java.lang.Boolean) {
            var3.add(UShort.box-impl(var8));
         }
      }

      return var3 as java.util.List<UShort>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UInt>> UIntArray.filterIndexedTo(destination: C, predicate: (Int, UInt) -> Boolean): C {
      var var3: IntArray = `$this$filterIndexedTo_u2d_u2d6EtJGI`;
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$filterIndexedTo_u2d_u2d6EtJGI); var5 < var6; var5++) {
         var var7: Int = UIntArray.get-pVg5ArA(var3, var5);
         if (predicate.invoke(var4++, UInt.box-impl(var7)) as java.lang.Boolean) {
            destination.add(UInt.box-impl(var7));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in ULong>> ULongArray.filterIndexedTo(destination: C, predicate: (Int, ULong) -> Boolean): C {
      var var3: LongArray = `$this$filterIndexedTo_u2dpe2Q0Dw`;
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$filterIndexedTo_u2dpe2Q0Dw); var5 < var6; var5++) {
         var var7: Long = ULongArray.get-s-VKNKU(var3, var5);
         if (predicate.invoke(var4++, ULong.box-impl(var7)) as java.lang.Boolean) {
            destination.add(ULong.box-impl(var7));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UByte>> UByteArray.filterIndexedTo(destination: C, predicate: (Int, UByte) -> Boolean): C {
      var var3: ByteArray = `$this$filterIndexedTo_u2deNpIKz8`;
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$filterIndexedTo_u2deNpIKz8); var5 < var6; var5++) {
         var var7: Byte = UByteArray.get-w2LRezQ(var3, var5);
         if (predicate.invoke(var4++, UByte.box-impl(var7)) as java.lang.Boolean) {
            destination.add(UByte.box-impl(var7));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UShort>> UShortArray.filterIndexedTo(destination: C, predicate: (Int, UShort) -> Boolean): C {
      var var3: ShortArray = `$this$filterIndexedTo_u2dQqktQ3k`;
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$filterIndexedTo_u2dQqktQ3k); var5 < var6; var5++) {
         var var7: Short = UShortArray.get-Mh2AYeg(var3, var5);
         if (predicate.invoke(var4++, UShort.box-impl(var7)) as java.lang.Boolean) {
            destination.add(UShort.box-impl(var7));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.filterNot(predicate: (UInt) -> Boolean): List<UInt> {
      var var2: IntArray = `$this$filterNot_u2djgv0xPQ`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$filterNot_u2djgv0xPQ); var4 < var5; var4++) {
         var var6: Int = UIntArray.get-pVg5ArA(var2, var4);
         if (!predicate.invoke(UInt.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UInt.box-impl(var6));
         }
      }

      return var3 as java.util.List<UInt>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.filterNot(predicate: (ULong) -> Boolean): List<ULong> {
      var var2: LongArray = `$this$filterNot_u2dMShoTSo`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$filterNot_u2dMShoTSo); var4 < var5; var4++) {
         var var6: Long = ULongArray.get-s-VKNKU(var2, var4);
         if (!predicate.invoke(ULong.box-impl(var6)) as java.lang.Boolean) {
            var3.add(ULong.box-impl(var6));
         }
      }

      return var3 as java.util.List<ULong>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.filterNot(predicate: (UByte) -> Boolean): List<UByte> {
      var var2: ByteArray = `$this$filterNot_u2dJOV_ifY`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$filterNot_u2dJOV_ifY); var4 < var5; var4++) {
         var var6: Byte = UByteArray.get-w2LRezQ(var2, var4);
         if (!predicate.invoke(UByte.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UByte.box-impl(var6));
         }
      }

      return var3 as java.util.List<UByte>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.filterNot(predicate: (UShort) -> Boolean): List<UShort> {
      var var2: ShortArray = `$this$filterNot_u2dxTcfx_M`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$filterNot_u2dxTcfx_M); var4 < var5; var4++) {
         var var6: Short = UShortArray.get-Mh2AYeg(var2, var4);
         if (!predicate.invoke(UShort.box-impl(var6)) as java.lang.Boolean) {
            var3.add(UShort.box-impl(var6));
         }
      }

      return var3 as java.util.List<UShort>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UInt>> UIntArray.filterNotTo(destination: C, predicate: (UInt) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$filterNotTo_u2dwU5IKMo); var3 < var4; var3++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$filterNotTo_u2dwU5IKMo`, var3);
         if (!predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            destination.add(UInt.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in ULong>> ULongArray.filterNotTo(destination: C, predicate: (ULong) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$filterNotTo_u2dHqK1JgA); var3 < var4; var3++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$filterNotTo_u2dHqK1JgA`, var3);
         if (!predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            destination.add(ULong.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UByte>> UByteArray.filterNotTo(destination: C, predicate: (UByte) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$filterNotTo_u2dwzUQCXU); var3 < var4; var3++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$filterNotTo_u2dwzUQCXU`, var3);
         if (!predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            destination.add(UByte.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UShort>> UShortArray.filterNotTo(destination: C, predicate: (UShort) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$filterNotTo_u2doEOeDjA); var3 < var4; var3++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$filterNotTo_u2doEOeDjA`, var3);
         if (!predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            destination.add(UShort.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UInt>> UIntArray.filterTo(destination: C, predicate: (UInt) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$filterTo_u2dwU5IKMo); var3 < var4; var3++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$filterTo_u2dwU5IKMo`, var3);
         if (predicate.invoke(UInt.box-impl(element)) as java.lang.Boolean) {
            destination.add(UInt.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in ULong>> ULongArray.filterTo(destination: C, predicate: (ULong) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$filterTo_u2dHqK1JgA); var3 < var4; var3++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$filterTo_u2dHqK1JgA`, var3);
         if (predicate.invoke(ULong.box-impl(element)) as java.lang.Boolean) {
            destination.add(ULong.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UByte>> UByteArray.filterTo(destination: C, predicate: (UByte) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$filterTo_u2dwzUQCXU); var3 < var4; var3++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$filterTo_u2dwzUQCXU`, var3);
         if (predicate.invoke(UByte.box-impl(element)) as java.lang.Boolean) {
            destination.add(UByte.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <C : MutableCollection<in UShort>> UShortArray.filterTo(destination: C, predicate: (UShort) -> Boolean): C {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$filterTo_u2doEOeDjA); var3 < var4; var3++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$filterTo_u2doEOeDjA`, var3);
         if (predicate.invoke(UShort.box-impl(element)) as java.lang.Boolean) {
            destination.add(UShort.box-impl(element));
         }
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.slice(indices: IntRange): List<UInt> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         UArraysKt.asList--ajY-9A(UIntArray.constructor-impl(ArraysKt.copyOfRange(`$this$slice_u2dtAntMlw`, indices.getStart(), indices.getEndInclusive() + 1)));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.slice(indices: IntRange): List<ULong> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         UArraysKt.asList-QwZRm1k(
            ULongArray.constructor-impl(ArraysKt.copyOfRange(`$this$slice_u2dZRhS8yI`, indices.getStart(), indices.getEndInclusive() + 1))
         );
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.slice(indices: IntRange): List<UByte> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         UArraysKt.asList-GBYM_sE(
            UByteArray.constructor-impl(ArraysKt.copyOfRange(`$this$slice_u2dc0bezYM`, indices.getStart(), indices.getEndInclusive() + 1))
         );
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.slice(indices: IntRange): List<UShort> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         UArraysKt.asList-rL5Bavg(
            UShortArray.constructor-impl(ArraysKt.copyOfRange(`$this$slice_u2dQ6IL4kU`, indices.getStart(), indices.getEndInclusive() + 1))
         );
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.slice(indices: Iterable<Int>): List<UInt> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$slice_u2dHwE9HBo`, (var4.next() as java.lang.Number).intValue())));
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.slice(indices: Iterable<Int>): List<ULong> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$slice_u2dF7u83W8`, (var4.next() as java.lang.Number).intValue())));
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.slice(indices: Iterable<Int>): List<UByte> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$slice_u2dJQknh5Q`, (var4.next() as java.lang.Number).intValue())));
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.slice(indices: Iterable<Int>): List<UShort> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$slice_u2dJGPC0_u2dM`, (var4.next() as java.lang.Number).intValue())));
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sliceArray(indices: Collection<Int>): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dCFIt9YE`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sliceArray(indices: Collection<Int>): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dkzHmqpY`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sliceArray(indices: Collection<Int>): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dxo_DsdI`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sliceArray(indices: Collection<Int>): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dojwP5H8`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sliceArray(indices: IntRange): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dtAntMlw`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sliceArray(indices: IntRange): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dZRhS8yI`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sliceArray(indices: IntRange): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dc0bezYM`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sliceArray(indices: IntRange): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.sliceArray(`$this$sliceArray_u2dQ6IL4kU`, indices));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.take(n: Int): List<UInt> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= UIntArray.getSize-impl(`$this$take_u2dqFRl0hI`)) {
         return CollectionsKt.toList(UIntArray.box-impl(`$this$take_u2dqFRl0hI`));
      } else if (n == 1) {
         return CollectionsKt.listOf(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$take_u2dqFRl0hI`, 0)));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);
         var var4: Int = 0;

         for (int var5 = UIntArray.getSize-impl($this$take_u2dqFRl0hI); var4 < var5; var4++) {
            list.add(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$take_u2dqFRl0hI`, var4)));
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.take(n: Int): List<ULong> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= ULongArray.getSize-impl(`$this$take_u2dr7IrZao`)) {
         return CollectionsKt.toList(ULongArray.box-impl(`$this$take_u2dr7IrZao`));
      } else if (n == 1) {
         return CollectionsKt.listOf(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$take_u2dr7IrZao`, 0)));
      } else {
         var var8: Int = 0;
         var list: ArrayList = new ArrayList(n);
         var var4: Int = 0;

         for (int var5 = ULongArray.getSize-impl($this$take_u2dr7IrZao); var4 < var5; var4++) {
            list.add(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$take_u2dr7IrZao`, var4)));
            if (++var8 == n) {
               break;
            }
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.take(n: Int): List<UByte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= UByteArray.getSize-impl(`$this$take_u2dPpDY95g`)) {
         return CollectionsKt.toList(UByteArray.box-impl(`$this$take_u2dPpDY95g`));
      } else if (n == 1) {
         return CollectionsKt.listOf(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$take_u2dPpDY95g`, 0)));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);
         var var4: Int = 0;

         for (int var5 = UByteArray.getSize-impl($this$take_u2dPpDY95g); var4 < var5; var4++) {
            list.add(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$take_u2dPpDY95g`, var4)));
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.take(n: Int): List<UShort> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= UShortArray.getSize-impl(`$this$take_u2dnggk6HY`)) {
         return CollectionsKt.toList(UShortArray.box-impl(`$this$take_u2dnggk6HY`));
      } else if (n == 1) {
         return CollectionsKt.listOf(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$take_u2dnggk6HY`, 0)));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);
         var var4: Int = 0;

         for (int var5 = UShortArray.getSize-impl($this$take_u2dnggk6HY); var4 < var5; var4++) {
            list.add(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$take_u2dnggk6HY`, var4)));
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.takeLast(n: Int): List<UInt> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = UIntArray.getSize-impl(`$this$takeLast_u2dqFRl0hI`);
         if (n >= var5) {
            return CollectionsKt.toList(UIntArray.box-impl(`$this$takeLast_u2dqFRl0hI`));
         } else if (n == 1) {
            return CollectionsKt.listOf(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$takeLast_u2dqFRl0hI`, var5 - 1)));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$takeLast_u2dqFRl0hI`, index)));
            }

            return list;
         }
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.takeLast(n: Int): List<ULong> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = ULongArray.getSize-impl(`$this$takeLast_u2dr7IrZao`);
         if (n >= var5) {
            return CollectionsKt.toList(ULongArray.box-impl(`$this$takeLast_u2dr7IrZao`));
         } else if (n == 1) {
            return CollectionsKt.listOf(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$takeLast_u2dr7IrZao`, var5 - 1)));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$takeLast_u2dr7IrZao`, index)));
            }

            return list;
         }
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.takeLast(n: Int): List<UByte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = UByteArray.getSize-impl(`$this$takeLast_u2dPpDY95g`);
         if (n >= var5) {
            return CollectionsKt.toList(UByteArray.box-impl(`$this$takeLast_u2dPpDY95g`));
         } else if (n == 1) {
            return CollectionsKt.listOf(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$takeLast_u2dPpDY95g`, var5 - 1)));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$takeLast_u2dPpDY95g`, index)));
            }

            return list;
         }
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.takeLast(n: Int): List<UShort> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = UShortArray.getSize-impl(`$this$takeLast_u2dnggk6HY`);
         if (n >= var5) {
            return CollectionsKt.toList(UShortArray.box-impl(`$this$takeLast_u2dnggk6HY`));
         } else if (n == 1) {
            return CollectionsKt.listOf(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$takeLast_u2dnggk6HY`, var5 - 1)));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$takeLast_u2dnggk6HY`, index)));
            }

            return list;
         }
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.takeLastWhile(predicate: (UInt) -> Boolean): List<UInt> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile_u2djgv0xPQ); -1 < index; index--) {
         if (!predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$takeLastWhile_u2djgv0xPQ`, index))) as java.lang.Boolean) {
            return UArraysKt.drop-qFRl0hI(`$this$takeLastWhile_u2djgv0xPQ`, index + 1);
         }
      }

      return CollectionsKt.toList(UIntArray.box-impl(`$this$takeLastWhile_u2djgv0xPQ`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.takeLastWhile(predicate: (ULong) -> Boolean): List<ULong> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile_u2dMShoTSo); -1 < index; index--) {
         if (!predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$takeLastWhile_u2dMShoTSo`, index))) as java.lang.Boolean) {
            return UArraysKt.drop-r7IrZao(`$this$takeLastWhile_u2dMShoTSo`, index + 1);
         }
      }

      return CollectionsKt.toList(ULongArray.box-impl(`$this$takeLastWhile_u2dMShoTSo`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.takeLastWhile(predicate: (UByte) -> Boolean): List<UByte> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile_u2dJOV_ifY); -1 < index; index--) {
         if (!predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$takeLastWhile_u2dJOV_ifY`, index))) as java.lang.Boolean) {
            return UArraysKt.drop-PpDY95g(`$this$takeLastWhile_u2dJOV_ifY`, index + 1);
         }
      }

      return CollectionsKt.toList(UByteArray.box-impl(`$this$takeLastWhile_u2dJOV_ifY`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.takeLastWhile(predicate: (UShort) -> Boolean): List<UShort> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile_u2dxTcfx_M); -1 < index; index--) {
         if (!predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$takeLastWhile_u2dxTcfx_M`, index))) as java.lang.Boolean) {
            return UArraysKt.drop-nggk6HY(`$this$takeLastWhile_u2dxTcfx_M`, index + 1);
         }
      }

      return CollectionsKt.toList(UShortArray.box-impl(`$this$takeLastWhile_u2dxTcfx_M`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.takeWhile(predicate: (UInt) -> Boolean): List<UInt> {
      var list: ArrayList = new ArrayList();
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$takeWhile_u2djgv0xPQ); var3 < var4; var3++) {
         var item: Int = UIntArray.get-pVg5ArA(`$this$takeWhile_u2djgv0xPQ`, var3);
         if (!predicate.invoke(UInt.box-impl(item)) as java.lang.Boolean) {
            break;
         }

         list.add(UInt.box-impl(item));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.takeWhile(predicate: (ULong) -> Boolean): List<ULong> {
      var list: ArrayList = new ArrayList();
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$takeWhile_u2dMShoTSo); var3 < var4; var3++) {
         var item: Long = ULongArray.get-s-VKNKU(`$this$takeWhile_u2dMShoTSo`, var3);
         if (!predicate.invoke(ULong.box-impl(item)) as java.lang.Boolean) {
            break;
         }

         list.add(ULong.box-impl(item));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.takeWhile(predicate: (UByte) -> Boolean): List<UByte> {
      var list: ArrayList = new ArrayList();
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$takeWhile_u2dJOV_ifY); var3 < var4; var3++) {
         var item: Byte = UByteArray.get-w2LRezQ(`$this$takeWhile_u2dJOV_ifY`, var3);
         if (!predicate.invoke(UByte.box-impl(item)) as java.lang.Boolean) {
            break;
         }

         list.add(UByte.box-impl(item));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.takeWhile(predicate: (UShort) -> Boolean): List<UShort> {
      var list: ArrayList = new ArrayList();
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$takeWhile_u2dxTcfx_M); var3 < var4; var3++) {
         var item: Short = UShortArray.get-Mh2AYeg(`$this$takeWhile_u2dxTcfx_M`, var3);
         if (!predicate.invoke(UShort.box-impl(item)) as java.lang.Boolean) {
            break;
         }

         list.add(UShort.box-impl(item));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reverse() {
      ArraysKt.reverse(`$this$reverse_u2d_u2dajY_u2d9A`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reverse() {
      ArraysKt.reverse(`$this$reverse_u2dQwZRm1k`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reverse() {
      ArraysKt.reverse(`$this$reverse_u2dGBYM_sE`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reverse() {
      ArraysKt.reverse(`$this$reverse_u2drL5Bavg`);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reverse(fromIndex: Int, toIndex: Int) {
      ArraysKt.reverse(`$this$reverse_u2doBK06Vg`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reverse(fromIndex: Int, toIndex: Int) {
      ArraysKt.reverse(`$this$reverse_u2d_u2dnroSd4`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reverse(fromIndex: Int, toIndex: Int) {
      ArraysKt.reverse(`$this$reverse_u2d4UcCI2c`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reverse(fromIndex: Int, toIndex: Int) {
      ArraysKt.reverse(`$this$reverse_u2dAa5vz7o`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.reversed(): List<UInt> {
      if (UIntArray.isEmpty-impl(`$this$reversed_u2d_u2dajY_u2d9A`)) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = CollectionsKt.toMutableList(UIntArray.box-impl(`$this$reversed_u2d_u2dajY_u2d9A`));
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.reversed(): List<ULong> {
      if (ULongArray.isEmpty-impl(`$this$reversed_u2dQwZRm1k`)) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = CollectionsKt.toMutableList(ULongArray.box-impl(`$this$reversed_u2dQwZRm1k`));
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.reversed(): List<UByte> {
      if (UByteArray.isEmpty-impl(`$this$reversed_u2dGBYM_sE`)) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = CollectionsKt.toMutableList(UByteArray.box-impl(`$this$reversed_u2dGBYM_sE`));
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.reversed(): List<UShort> {
      if (UShortArray.isEmpty-impl(`$this$reversed_u2drL5Bavg`)) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = CollectionsKt.toMutableList(UShortArray.box-impl(`$this$reversed_u2drL5Bavg`));
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reversedArray(): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.reversedArray(`$this$reversedArray_u2d_u2dajY_u2d9A`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reversedArray(): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.reversedArray(`$this$reversedArray_u2dQwZRm1k`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reversedArray(): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.reversedArray(`$this$reversedArray_u2dGBYM_sE`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reversedArray(): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.reversedArray(`$this$reversedArray_u2drL5Bavg`));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.shuffle() {
      UArraysKt.shuffle-2D5oskM(`$this$shuffle_u2d_u2dajY_u2d9A`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.shuffle() {
      UArraysKt.shuffle-JzugnMA(`$this$shuffle_u2dQwZRm1k`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.shuffle() {
      UArraysKt.shuffle-oSF2wD8(`$this$shuffle_u2dGBYM_sE`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.shuffle() {
      UArraysKt.shuffle-s5X_as8(`$this$shuffle_u2drL5Bavg`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle_u2d2D5oskM); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var var5: Int = UIntArray.get-pVg5ArA(`$this$shuffle_u2d2D5oskM`, i);
         UIntArray.set-VXSXFK8(`$this$shuffle_u2d2D5oskM`, i, UIntArray.get-pVg5ArA(`$this$shuffle_u2d2D5oskM`, j));
         UIntArray.set-VXSXFK8(`$this$shuffle_u2d2D5oskM`, j, var5);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle_u2dJzugnMA); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var var6: Long = ULongArray.get-s-VKNKU(`$this$shuffle_u2dJzugnMA`, i);
         ULongArray.set-k8EXiF4(`$this$shuffle_u2dJzugnMA`, i, ULongArray.get-s-VKNKU(`$this$shuffle_u2dJzugnMA`, j));
         ULongArray.set-k8EXiF4(`$this$shuffle_u2dJzugnMA`, j, var6);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle_u2doSF2wD8); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var var5: Byte = UByteArray.get-w2LRezQ(`$this$shuffle_u2doSF2wD8`, i);
         UByteArray.set-VurrAj0(`$this$shuffle_u2doSF2wD8`, i, UByteArray.get-w2LRezQ(`$this$shuffle_u2doSF2wD8`, j));
         UByteArray.set-VurrAj0(`$this$shuffle_u2doSF2wD8`, j, var5);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle_u2ds5X_as8); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var var5: Short = UShortArray.get-Mh2AYeg(`$this$shuffle_u2ds5X_as8`, i);
         UShortArray.set-01HTLdE(`$this$shuffle_u2ds5X_as8`, i, UShortArray.get-Mh2AYeg(`$this$shuffle_u2ds5X_as8`, j));
         UShortArray.set-01HTLdE(`$this$shuffle_u2ds5X_as8`, j, var5);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sortDescending() {
      if (UIntArray.getSize-impl(`$this$sortDescending_u2d_u2dajY_u2d9A`) > 1) {
         UArraysKt.sort--ajY-9A(`$this$sortDescending_u2d_u2dajY_u2d9A`);
         ArraysKt.reverse(`$this$sortDescending_u2d_u2dajY_u2d9A`);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sortDescending() {
      if (ULongArray.getSize-impl(`$this$sortDescending_u2dQwZRm1k`) > 1) {
         UArraysKt.sort-QwZRm1k(`$this$sortDescending_u2dQwZRm1k`);
         ArraysKt.reverse(`$this$sortDescending_u2dQwZRm1k`);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sortDescending() {
      if (UByteArray.getSize-impl(`$this$sortDescending_u2dGBYM_sE`) > 1) {
         UArraysKt.sort-GBYM_sE(`$this$sortDescending_u2dGBYM_sE`);
         ArraysKt.reverse(`$this$sortDescending_u2dGBYM_sE`);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sortDescending() {
      if (UShortArray.getSize-impl(`$this$sortDescending_u2drL5Bavg`) > 1) {
         UArraysKt.sort-rL5Bavg(`$this$sortDescending_u2drL5Bavg`);
         ArraysKt.reverse(`$this$sortDescending_u2drL5Bavg`);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sorted(): List<UInt> {
      var var10000: IntArray = Arrays.copyOf(`$this$sorted_u2d_u2dajY_u2d9A`, `$this$sorted_u2d_u2dajY_u2d9A`.length);
      var var1: IntArray = UIntArray.constructor-impl(var10000);
      UArraysKt.sort--ajY-9A(var1);
      return UArraysKt.asList--ajY-9A(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sorted(): List<ULong> {
      var var10000: LongArray = Arrays.copyOf(`$this$sorted_u2dQwZRm1k`, `$this$sorted_u2dQwZRm1k`.length);
      var var1: LongArray = ULongArray.constructor-impl(var10000);
      UArraysKt.sort-QwZRm1k(var1);
      return UArraysKt.asList-QwZRm1k(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sorted(): List<UByte> {
      var var10000: ByteArray = Arrays.copyOf(`$this$sorted_u2dGBYM_sE`, `$this$sorted_u2dGBYM_sE`.length);
      var var1: ByteArray = UByteArray.constructor-impl(var10000);
      UArraysKt.sort-GBYM_sE(var1);
      return UArraysKt.asList-GBYM_sE(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sorted(): List<UShort> {
      var var10000: ShortArray = Arrays.copyOf(`$this$sorted_u2drL5Bavg`, `$this$sorted_u2drL5Bavg`.length);
      var var1: ShortArray = UShortArray.constructor-impl(var10000);
      UArraysKt.sort-rL5Bavg(var1);
      return UArraysKt.asList-rL5Bavg(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sortedArray(): UIntArray {
      if (UIntArray.isEmpty-impl(`$this$sortedArray_u2d_u2dajY_u2d9A`)) {
         return `$this$sortedArray_u2d_u2dajY_u2d9A`;
      } else {
         var var10000: IntArray = Arrays.copyOf(`$this$sortedArray_u2d_u2dajY_u2d9A`, `$this$sortedArray_u2d_u2dajY_u2d9A`.length);
         var var1: IntArray = UIntArray.constructor-impl(var10000);
         UArraysKt.sort--ajY-9A(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sortedArray(): ULongArray {
      if (ULongArray.isEmpty-impl(`$this$sortedArray_u2dQwZRm1k`)) {
         return `$this$sortedArray_u2dQwZRm1k`;
      } else {
         var var10000: LongArray = Arrays.copyOf(`$this$sortedArray_u2dQwZRm1k`, `$this$sortedArray_u2dQwZRm1k`.length);
         var var1: LongArray = ULongArray.constructor-impl(var10000);
         UArraysKt.sort-QwZRm1k(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sortedArray(): UByteArray {
      if (UByteArray.isEmpty-impl(`$this$sortedArray_u2dGBYM_sE`)) {
         return `$this$sortedArray_u2dGBYM_sE`;
      } else {
         var var10000: ByteArray = Arrays.copyOf(`$this$sortedArray_u2dGBYM_sE`, `$this$sortedArray_u2dGBYM_sE`.length);
         var var1: ByteArray = UByteArray.constructor-impl(var10000);
         UArraysKt.sort-GBYM_sE(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sortedArray(): UShortArray {
      if (UShortArray.isEmpty-impl(`$this$sortedArray_u2drL5Bavg`)) {
         return `$this$sortedArray_u2drL5Bavg`;
      } else {
         var var10000: ShortArray = Arrays.copyOf(`$this$sortedArray_u2drL5Bavg`, `$this$sortedArray_u2drL5Bavg`.length);
         var var1: ShortArray = UShortArray.constructor-impl(var10000);
         UArraysKt.sort-rL5Bavg(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sortedArrayDescending(): UIntArray {
      if (UIntArray.isEmpty-impl(`$this$sortedArrayDescending_u2d_u2dajY_u2d9A`)) {
         return `$this$sortedArrayDescending_u2d_u2dajY_u2d9A`;
      } else {
         var var10000: IntArray = Arrays.copyOf(`$this$sortedArrayDescending_u2d_u2dajY_u2d9A`, `$this$sortedArrayDescending_u2d_u2dajY_u2d9A`.length);
         var var1: IntArray = UIntArray.constructor-impl(var10000);
         UArraysKt.sortDescending--ajY-9A(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sortedArrayDescending(): ULongArray {
      if (ULongArray.isEmpty-impl(`$this$sortedArrayDescending_u2dQwZRm1k`)) {
         return `$this$sortedArrayDescending_u2dQwZRm1k`;
      } else {
         var var10000: LongArray = Arrays.copyOf(`$this$sortedArrayDescending_u2dQwZRm1k`, `$this$sortedArrayDescending_u2dQwZRm1k`.length);
         var var1: LongArray = ULongArray.constructor-impl(var10000);
         UArraysKt.sortDescending-QwZRm1k(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sortedArrayDescending(): UByteArray {
      if (UByteArray.isEmpty-impl(`$this$sortedArrayDescending_u2dGBYM_sE`)) {
         return `$this$sortedArrayDescending_u2dGBYM_sE`;
      } else {
         var var10000: ByteArray = Arrays.copyOf(`$this$sortedArrayDescending_u2dGBYM_sE`, `$this$sortedArrayDescending_u2dGBYM_sE`.length);
         var var1: ByteArray = UByteArray.constructor-impl(var10000);
         UArraysKt.sortDescending-GBYM_sE(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sortedArrayDescending(): UShortArray {
      if (UShortArray.isEmpty-impl(`$this$sortedArrayDescending_u2drL5Bavg`)) {
         return `$this$sortedArrayDescending_u2drL5Bavg`;
      } else {
         var var10000: ShortArray = Arrays.copyOf(`$this$sortedArrayDescending_u2drL5Bavg`, `$this$sortedArrayDescending_u2drL5Bavg`.length);
         var var1: ShortArray = UShortArray.constructor-impl(var10000);
         UArraysKt.sortDescending-rL5Bavg(var1);
         return var1;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sortedDescending(): List<UInt> {
      var var10000: IntArray = Arrays.copyOf(`$this$sortedDescending_u2d_u2dajY_u2d9A`, `$this$sortedDescending_u2d_u2dajY_u2d9A`.length);
      var var1: IntArray = UIntArray.constructor-impl(var10000);
      UArraysKt.sort--ajY-9A(var1);
      return UArraysKt.reversed--ajY-9A(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sortedDescending(): List<ULong> {
      var var10000: LongArray = Arrays.copyOf(`$this$sortedDescending_u2dQwZRm1k`, `$this$sortedDescending_u2dQwZRm1k`.length);
      var var1: LongArray = ULongArray.constructor-impl(var10000);
      UArraysKt.sort-QwZRm1k(var1);
      return UArraysKt.reversed-QwZRm1k(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sortedDescending(): List<UByte> {
      var var10000: ByteArray = Arrays.copyOf(`$this$sortedDescending_u2dGBYM_sE`, `$this$sortedDescending_u2dGBYM_sE`.length);
      var var1: ByteArray = UByteArray.constructor-impl(var10000);
      UArraysKt.sort-GBYM_sE(var1);
      return UArraysKt.reversed-GBYM_sE(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sortedDescending(): List<UShort> {
      var var10000: ShortArray = Arrays.copyOf(`$this$sortedDescending_u2drL5Bavg`, `$this$sortedDescending_u2drL5Bavg`.length);
      var var1: ShortArray = UShortArray.constructor-impl(var10000);
      UArraysKt.sort-rL5Bavg(var1);
      return UArraysKt.reversed-rL5Bavg(var1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.asByteArray(): ByteArray {
      return `$this$asByteArray_u2dGBYM_sE`;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.asIntArray(): IntArray {
      return `$this$asIntArray_u2d_u2dajY_u2d9A`;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.asLongArray(): LongArray {
      return `$this$asLongArray_u2dQwZRm1k`;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.asShortArray(): ShortArray {
      return `$this$asShortArray_u2drL5Bavg`;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.asUByteArray(): UByteArray {
      return UByteArray.constructor-impl(`$this$asUByteArray`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.asUIntArray(): UIntArray {
      return UIntArray.constructor-impl(`$this$asUIntArray`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.asULongArray(): ULongArray {
      return ULongArray.constructor-impl(`$this$asULongArray`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.asUShortArray(): UShortArray {
      return UShortArray.constructor-impl(`$this$asUShortArray`);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UIntArray?.contentEquals(other: UIntArray?): Boolean {
      var var10000: IntArray = `$this$contentEquals_u2dKJPZfPQ`;
      if (`$this$contentEquals_u2dKJPZfPQ` == null) {
         var10000 = null;
      }

      var var10001: IntArray = other;
      if (other == null) {
         var10001 = null;
      }

      return Arrays.equals(var10000, var10001);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun ULongArray?.contentEquals(other: ULongArray?): Boolean {
      var var10000: LongArray = `$this$contentEquals_u2dlec5QzE`;
      if (`$this$contentEquals_u2dlec5QzE` == null) {
         var10000 = null;
      }

      var var10001: LongArray = other;
      if (other == null) {
         var10001 = null;
      }

      return Arrays.equals(var10000, var10001);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UByteArray?.contentEquals(other: UByteArray?): Boolean {
      var var10000: ByteArray = `$this$contentEquals_u2dkV0jMPg`;
      if (`$this$contentEquals_u2dkV0jMPg` == null) {
         var10000 = null;
      }

      var var10001: ByteArray = other;
      if (other == null) {
         var10001 = null;
      }

      return Arrays.equals(var10000, var10001);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UShortArray?.contentEquals(other: UShortArray?): Boolean {
      var var10000: ShortArray = `$this$contentEquals_u2dFGO6Aew`;
      if (`$this$contentEquals_u2dFGO6Aew` == null) {
         var10000 = null;
      }

      var var10001: ShortArray = other;
      if (other == null) {
         var10001 = null;
      }

      return Arrays.equals(var10000, var10001);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray?.contentHashCode(): Int {
      var var10000: IntArray = `$this$contentHashCode_u2dXUkPCBk`;
      if (`$this$contentHashCode_u2dXUkPCBk` == null) {
         var10000 = null;
      }

      return Arrays.hashCode(var10000);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray?.contentHashCode(): Int {
      var var10000: LongArray = `$this$contentHashCode_u2duLth9ew`;
      if (`$this$contentHashCode_u2duLth9ew` == null) {
         var10000 = null;
      }

      return Arrays.hashCode(var10000);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray?.contentHashCode(): Int {
      var var10000: ByteArray = `$this$contentHashCode_u2d2csIQuQ`;
      if (`$this$contentHashCode_u2d2csIQuQ` == null) {
         var10000 = null;
      }

      return Arrays.hashCode(var10000);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray?.contentHashCode(): Int {
      var var10000: ShortArray = `$this$contentHashCode_u2dd_u2d6D3K8`;
      if (`$this$contentHashCode_u2dd_u2d6D3K8` == null) {
         var10000 = null;
      }

      return Arrays.hashCode(var10000);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray?.contentToString(): String {
      if (`$this$contentToString_u2dXUkPCBk` != null) {
         var var10000: java.lang.String = CollectionsKt.joinToString$default(
            UIntArray.box-impl(`$this$contentToString_u2dXUkPCBk`), ", ", "[", "]", 0, null, null, 56, null
         );
         if (var10000 != null) {
            return var10000;
         }
      }

      return "null";
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray?.contentToString(): String {
      if (`$this$contentToString_u2duLth9ew` != null) {
         var var10000: java.lang.String = CollectionsKt.joinToString$default(
            ULongArray.box-impl(`$this$contentToString_u2duLth9ew`), ", ", "[", "]", 0, null, null, 56, null
         );
         if (var10000 != null) {
            return var10000;
         }
      }

      return "null";
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray?.contentToString(): String {
      if (`$this$contentToString_u2d2csIQuQ` != null) {
         var var10000: java.lang.String = CollectionsKt.joinToString$default(
            UByteArray.box-impl(`$this$contentToString_u2d2csIQuQ`), ", ", "[", "]", 0, null, null, 56, null
         );
         if (var10000 != null) {
            return var10000;
         }
      }

      return "null";
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray?.contentToString(): String {
      if (`$this$contentToString_u2dd_u2d6D3K8` != null) {
         var var10000: java.lang.String = CollectionsKt.joinToString$default(
            UShortArray.box-impl(`$this$contentToString_u2dd_u2d6D3K8`), ", ", "[", "]", 0, null, null, 56, null
         );
         if (var10000 != null) {
            return var10000;
         }
      }

      return "null";
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.copyInto(destination: UIntArray, destinationOffset: Int, startIndex: Int, endIndex: Int): UIntArray {
      ArraysKt.copyInto(`$this$copyInto_u2dsIZ3KeM`, destination, destinationOffset, startIndex, endIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.copyInto(destination: ULongArray, destinationOffset: Int, startIndex: Int, endIndex: Int): ULongArray {
      ArraysKt.copyInto(`$this$copyInto_u2d_u2dB0_u2dL2c`, destination, destinationOffset, startIndex, endIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.copyInto(destination: UByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): UByteArray {
      ArraysKt.copyInto(`$this$copyInto_u2dFUQE5sA`, destination, destinationOffset, startIndex, endIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.copyInto(destination: UShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int): UShortArray {
      ArraysKt.copyInto(`$this$copyInto_u2d9_u2dak10g`, destination, destinationOffset, startIndex, endIndex);
      return destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.copyOf(): UIntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$copyOf_u2d_u2dajY_u2d9A`, `$this$copyOf_u2d_u2dajY_u2d9A`.length);
      return UIntArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.copyOf(): ULongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$copyOf_u2dQwZRm1k`, `$this$copyOf_u2dQwZRm1k`.length);
      return ULongArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.copyOf(): UByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$copyOf_u2dGBYM_sE`, `$this$copyOf_u2dGBYM_sE`.length);
      return UByteArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.copyOf(): UShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$copyOf_u2drL5Bavg`, `$this$copyOf_u2drL5Bavg`.length);
      return UShortArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.copyOf(newSize: Int): UIntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$copyOf_u2dqFRl0hI`, newSize);
      return UIntArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.copyOf(newSize: Int): ULongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$copyOf_u2dr7IrZao`, newSize);
      return ULongArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.copyOf(newSize: Int): UByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$copyOf_u2dPpDY95g`, newSize);
      return UByteArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.copyOf(newSize: Int): UShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$copyOf_u2dnggk6HY`, newSize);
      return UShortArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.copyOfRange(fromIndex: Int, toIndex: Int): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.copyOfRange(`$this$copyOfRange_u2doBK06Vg`, fromIndex, toIndex));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.copyOfRange(fromIndex: Int, toIndex: Int): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.copyOfRange(`$this$copyOfRange_u2d_u2dnroSd4`, fromIndex, toIndex));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.copyOfRange(fromIndex: Int, toIndex: Int): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.copyOfRange(`$this$copyOfRange_u2d4UcCI2c`, fromIndex, toIndex));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.copyOfRange(fromIndex: Int, toIndex: Int): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.copyOfRange(`$this$copyOfRange_u2dAa5vz7o`, fromIndex, toIndex));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.fill(element: UInt, fromIndex: Int, toIndex: Int) {
      ArraysKt.fill(`$this$fill_u2d2fe2U9s`, element, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.fill(element: ULong, fromIndex: Int, toIndex: Int) {
      ArraysKt.fill(`$this$fill_u2dK6DWlUc`, element, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.fill(element: UByte, fromIndex: Int, toIndex: Int) {
      ArraysKt.fill(`$this$fill_u2dWpHrYlw`, element, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.fill(element: UShort, fromIndex: Int, toIndex: Int) {
      ArraysKt.fill(`$this$fill_u2dEtDCXyQ`, element, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.plus(element: UInt): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.plus(`$this$plus_u2duWY9BYg`, element));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.plus(element: ULong): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.plus(`$this$plus_u2d3uqUaXg`, element));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.plus(element: UByte): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dgMuBH34`, element));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.plus(element: UShort): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dXzdR7RA`, element));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public operator fun UIntArray.plus(elements: Collection<UInt>): UIntArray {
      var index: Int = UIntArray.getSize-impl(`$this$plus_u2dCFIt9YE`);
      var var10000: IntArray = Arrays.copyOf(`$this$plus_u2dCFIt9YE`, UIntArray.getSize-impl(`$this$plus_u2dCFIt9YE`) + elements.size());
      var result: IntArray = var10000;
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as UInt).unbox-impl();
      }

      return UIntArray.constructor-impl(result);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public operator fun ULongArray.plus(elements: Collection<ULong>): ULongArray {
      var index: Int = ULongArray.getSize-impl(`$this$plus_u2dkzHmqpY`);
      var var10000: LongArray = Arrays.copyOf(`$this$plus_u2dkzHmqpY`, ULongArray.getSize-impl(`$this$plus_u2dkzHmqpY`) + elements.size());
      var result: LongArray = var10000;
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as ULong).unbox-impl();
      }

      return ULongArray.constructor-impl(result);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public operator fun UByteArray.plus(elements: Collection<UByte>): UByteArray {
      var index: Int = UByteArray.getSize-impl(`$this$plus_u2dxo_DsdI`);
      var var10000: ByteArray = Arrays.copyOf(`$this$plus_u2dxo_DsdI`, UByteArray.getSize-impl(`$this$plus_u2dxo_DsdI`) + elements.size());
      var result: ByteArray = var10000;
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as UByte).unbox-impl();
      }

      return UByteArray.constructor-impl(result);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public operator fun UShortArray.plus(elements: Collection<UShort>): UShortArray {
      var index: Int = UShortArray.getSize-impl(`$this$plus_u2dojwP5H8`);
      var var10000: ShortArray = Arrays.copyOf(`$this$plus_u2dojwP5H8`, UShortArray.getSize-impl(`$this$plus_u2dojwP5H8`) + elements.size());
      var result: ShortArray = var10000;
      var var4: java.util.Iterator = elements.iterator();

      while (var4.hasNext()) {
         result[index++] = (var4.next() as UShort).unbox-impl();
      }

      return UShortArray.constructor-impl(result);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntArray.plus(elements: UIntArray): UIntArray {
      return UIntArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dctEhBpI`, elements));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongArray.plus(elements: ULongArray): ULongArray {
      return ULongArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dus8wMrg`, elements));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UByteArray.plus(elements: UByteArray): UByteArray {
      return UByteArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dkdPth3s`, elements));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline operator fun UShortArray.plus(elements: UShortArray): UShortArray {
      return UShortArray.constructor-impl(ArraysKt.plus(`$this$plus_u2dmazbYpA`, elements));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sort() {
      if (UIntArray.getSize-impl(`$this$sort_u2d_u2dajY_u2d9A`) > 1) {
         UArraySortingKt.sortArray-oBK06Vg(`$this$sort_u2d_u2dajY_u2d9A`, 0, UIntArray.getSize-impl(`$this$sort_u2d_u2dajY_u2d9A`));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sort() {
      if (ULongArray.getSize-impl(`$this$sort_u2dQwZRm1k`) > 1) {
         UArraySortingKt.sortArray--nroSd4(`$this$sort_u2dQwZRm1k`, 0, ULongArray.getSize-impl(`$this$sort_u2dQwZRm1k`));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sort() {
      if (UByteArray.getSize-impl(`$this$sort_u2dGBYM_sE`) > 1) {
         UArraySortingKt.sortArray-4UcCI2c(`$this$sort_u2dGBYM_sE`, 0, UByteArray.getSize-impl(`$this$sort_u2dGBYM_sE`));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sort() {
      if (UShortArray.getSize-impl(`$this$sort_u2drL5Bavg`) > 1) {
         UArraySortingKt.sortArray-Aa5vz7o(`$this$sort_u2drL5Bavg`, 0, UShortArray.getSize-impl(`$this$sort_u2drL5Bavg`));
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sort(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UIntArray.getSize-impl(`$this$sort_u2doBK06Vg`));
      UArraySortingKt.sortArray-oBK06Vg(`$this$sort_u2doBK06Vg`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sort(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, ULongArray.getSize-impl(`$this$sort_u2d_u2dnroSd4`));
      UArraySortingKt.sortArray--nroSd4(`$this$sort_u2d_u2dnroSd4`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sort(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UByteArray.getSize-impl(`$this$sort_u2d4UcCI2c`));
      UArraySortingKt.sortArray-4UcCI2c(`$this$sort_u2d4UcCI2c`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sort(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UShortArray.getSize-impl(`$this$sort_u2dAa5vz7o`));
      UArraySortingKt.sortArray-Aa5vz7o(`$this$sort_u2dAa5vz7o`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.sortDescending(fromIndex: Int, toIndex: Int) {
      UArraysKt.sort-oBK06Vg(`$this$sortDescending_u2doBK06Vg`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending_u2doBK06Vg`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.sortDescending(fromIndex: Int, toIndex: Int) {
      UArraysKt.sort--nroSd4(`$this$sortDescending_u2d_u2dnroSd4`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending_u2d_u2dnroSd4`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.sortDescending(fromIndex: Int, toIndex: Int) {
      UArraysKt.sort-4UcCI2c(`$this$sortDescending_u2d4UcCI2c`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending_u2d4UcCI2c`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.sortDescending(fromIndex: Int, toIndex: Int) {
      UArraysKt.sort-Aa5vz7o(`$this$sortDescending_u2dAa5vz7o`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending_u2dAa5vz7o`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.toByteArray(): ByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$toByteArray_u2dGBYM_sE`, `$this$toByteArray_u2dGBYM_sE`.length);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.toIntArray(): IntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$toIntArray_u2d_u2dajY_u2d9A`, `$this$toIntArray_u2d_u2dajY_u2d9A`.length);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.toLongArray(): LongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$toLongArray_u2dQwZRm1k`, `$this$toLongArray_u2dQwZRm1k`.length);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.toShortArray(): ShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$toShortArray_u2drL5Bavg`, `$this$toShortArray_u2drL5Bavg`.length);
      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.toTypedArray(): Array<UInt> {
      var var1: Int = 0;
      var var2: Int = UIntArray.getSize-impl(`$this$toTypedArray_u2d_u2dajY_u2d9A`);

      var var3: Array<UInt>;
      for (var3 = new UInt[var2]; var1 < var2; var1++) {
         var3[var1] = UInt.box-impl(UIntArray.get-pVg5ArA(`$this$toTypedArray_u2d_u2dajY_u2d9A`, var1));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.toTypedArray(): Array<ULong> {
      var var1: Int = 0;
      var var2: Int = ULongArray.getSize-impl(`$this$toTypedArray_u2dQwZRm1k`);

      var var3: Array<ULong>;
      for (var3 = new ULong[var2]; var1 < var2; var1++) {
         var3[var1] = ULong.box-impl(ULongArray.get-s-VKNKU(`$this$toTypedArray_u2dQwZRm1k`, var1));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.toTypedArray(): Array<UByte> {
      var var1: Int = 0;
      var var2: Int = UByteArray.getSize-impl(`$this$toTypedArray_u2dGBYM_sE`);

      var var3: Array<UByte>;
      for (var3 = new UByte[var2]; var1 < var2; var1++) {
         var3[var1] = UByte.box-impl(UByteArray.get-w2LRezQ(`$this$toTypedArray_u2dGBYM_sE`, var1));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.toTypedArray(): Array<UShort> {
      var var1: Int = 0;
      var var2: Int = UShortArray.getSize-impl(`$this$toTypedArray_u2drL5Bavg`);

      var var3: Array<UShort>;
      for (var3 = new UShort[var2]; var1 < var2; var1++) {
         var3[var1] = UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$toTypedArray_u2drL5Bavg`, var1));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Array<out UByte>.toUByteArray(): UByteArray {
      var var1: Int = `$this$toUByteArray`.length;
      var var2: Int = 0;

      var var3: ByteArray;
      for (var3 = new byte[var1]; var2 < var1; var2++) {
         var3[var2] = `$this$toUByteArray`[var2].unbox-impl();
      }

      return UByteArray.constructor-impl(var3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.toUByteArray(): UByteArray {
      var var10000: ByteArray = Arrays.copyOf(`$this$toUByteArray`, `$this$toUByteArray`.length);
      return UByteArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Array<out UInt>.toUIntArray(): UIntArray {
      var var1: Int = `$this$toUIntArray`.length;
      var var2: Int = 0;

      var var3: IntArray;
      for (var3 = new int[var1]; var2 < var1; var2++) {
         var3[var2] = `$this$toUIntArray`[var2].unbox-impl();
      }

      return UIntArray.constructor-impl(var3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.toUIntArray(): UIntArray {
      var var10000: IntArray = Arrays.copyOf(`$this$toUIntArray`, `$this$toUIntArray`.length);
      return UIntArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Array<out ULong>.toULongArray(): ULongArray {
      var var1: Int = `$this$toULongArray`.length;
      var var2: Int = 0;

      var var3: LongArray;
      for (var3 = new long[var1]; var2 < var1; var2++) {
         var3[var2] = `$this$toULongArray`[var2].unbox-impl();
      }

      return ULongArray.constructor-impl(var3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.toULongArray(): ULongArray {
      var var10000: LongArray = Arrays.copyOf(`$this$toULongArray`, `$this$toULongArray`.length);
      return ULongArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Array<out UShort>.toUShortArray(): UShortArray {
      var var1: Int = `$this$toUShortArray`.length;
      var var2: Int = 0;

      var var3: ShortArray;
      for (var3 = new short[var1]; var2 < var1; var2++) {
         var3[var2] = `$this$toUShortArray`[var2].unbox-impl();
      }

      return UShortArray.constructor-impl(var3);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.toUShortArray(): UShortArray {
      var var10000: ShortArray = Arrays.copyOf(`$this$toUShortArray`, `$this$toUShortArray`.length);
      return UShortArray.constructor-impl(var10000);
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UIntArray.associateWith(valueSelector: (UInt) -> V): Map<UInt, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(UIntArray.getSize-impl(`$this$associateWith_u2djgv0xPQ`)), 16));
      var var3: IntArray = `$this$associateWith_u2djgv0xPQ`;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$associateWith_u2djgv0xPQ); var4 < var5; var4++) {
         var var6: Int = UIntArray.get-pVg5ArA(var3, var4);
         result.put(UInt.box-impl(var6), valueSelector.invoke(UInt.box-impl(var6)));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> ULongArray.associateWith(valueSelector: (ULong) -> V): Map<ULong, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(ULongArray.getSize-impl(`$this$associateWith_u2dMShoTSo`)), 16));
      var var3: LongArray = `$this$associateWith_u2dMShoTSo`;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$associateWith_u2dMShoTSo); var4 < var5; var4++) {
         var var6: Long = ULongArray.get-s-VKNKU(var3, var4);
         result.put(ULong.box-impl(var6), valueSelector.invoke(ULong.box-impl(var6)));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UByteArray.associateWith(valueSelector: (UByte) -> V): Map<UByte, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(UByteArray.getSize-impl(`$this$associateWith_u2dJOV_ifY`)), 16));
      var var3: ByteArray = `$this$associateWith_u2dJOV_ifY`;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$associateWith_u2dJOV_ifY); var4 < var5; var4++) {
         var var6: Byte = UByteArray.get-w2LRezQ(var3, var4);
         result.put(UByte.box-impl(var6), valueSelector.invoke(UByte.box-impl(var6)));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UShortArray.associateWith(valueSelector: (UShort) -> V): Map<UShort, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(UShortArray.getSize-impl(`$this$associateWith_u2dxTcfx_M`)), 16));
      var var3: ShortArray = `$this$associateWith_u2dxTcfx_M`;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$associateWith_u2dxTcfx_M); var4 < var5; var4++) {
         var var6: Short = UShortArray.get-Mh2AYeg(var3, var4);
         result.put(UShort.box-impl(var6), valueSelector.invoke(UShort.box-impl(var6)));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in UInt, in V>> UIntArray.associateWithTo(destination: M, valueSelector: (UInt) -> V): M {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$associateWithTo_u2d4D70W2E); var3 < var4; var3++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$associateWithTo_u2d4D70W2E`, var3);
         destination.put(UInt.box-impl(element), valueSelector.invoke(UInt.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in ULong, in V>> ULongArray.associateWithTo(destination: M, valueSelector: (ULong) -> V): M {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$associateWithTo_u2dX6OPwNk); var3 < var4; var3++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$associateWithTo_u2dX6OPwNk`, var3);
         destination.put(ULong.box-impl(element), valueSelector.invoke(ULong.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in UByte, in V>> UByteArray.associateWithTo(destination: M, valueSelector: (UByte) -> V): M {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$associateWithTo_u2dH21X9dk); var3 < var4; var3++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$associateWithTo_u2dH21X9dk`, var3);
         destination.put(UByte.box-impl(element), valueSelector.invoke(UByte.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in UShort, in V>> UShortArray.associateWithTo(destination: M, valueSelector: (UShort) -> V): M {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$associateWithTo_u2dciTST_u2d8); var3 < var4; var3++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$associateWithTo_u2dciTST_u2d8`, var3);
         destination.put(UShort.box-impl(element), valueSelector.invoke(UShort.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.flatMap(transform: (UInt) -> Iterable<R>): List<R> {
      var var2: IntArray = `$this$flatMap_u2djgv0xPQ`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$flatMap_u2djgv0xPQ); var4 < var5; var4++) {
         CollectionsKt.addAll(var3, transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(var2, var4))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.flatMap(transform: (ULong) -> Iterable<R>): List<R> {
      var var2: LongArray = `$this$flatMap_u2dMShoTSo`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$flatMap_u2dMShoTSo); var4 < var5; var4++) {
         CollectionsKt.addAll(var3, transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(var2, var4))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.flatMap(transform: (UByte) -> Iterable<R>): List<R> {
      var var2: ByteArray = `$this$flatMap_u2dJOV_ifY`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$flatMap_u2dJOV_ifY); var4 < var5; var4++) {
         CollectionsKt.addAll(var3, transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(var2, var4))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.flatMap(transform: (UShort) -> Iterable<R>): List<R> {
      var var2: ShortArray = `$this$flatMap_u2dxTcfx_M`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$flatMap_u2dxTcfx_M); var4 < var5; var4++) {
         CollectionsKt.addAll(var3, transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var4))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.flatMapIndexed(transform: (Int, UInt) -> Iterable<R>): List<R> {
      var var2: IntArray = `$this$flatMapIndexed_u2dWyvcNBI`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$flatMapIndexed_u2dWyvcNBI); var5 < var6; var5++) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, UInt.box-impl(UIntArray.get-pVg5ArA(var2, var5))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.flatMapIndexed(transform: (Int, ULong) -> Iterable<R>): List<R> {
      var var2: LongArray = `$this$flatMapIndexed_u2ds8dVfGU`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$flatMapIndexed_u2ds8dVfGU); var5 < var6; var5++) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, ULong.box-impl(ULongArray.get-s-VKNKU(var2, var5))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.flatMapIndexed(transform: (Int, UByte) -> Iterable<R>): List<R> {
      var var2: ByteArray = `$this$flatMapIndexed_u2dELGow60`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$flatMapIndexed_u2dELGow60); var5 < var6; var5++) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, UByte.box-impl(UByteArray.get-w2LRezQ(var2, var5))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.flatMapIndexed(transform: (Int, UShort) -> Iterable<R>): List<R> {
      var var2: ShortArray = `$this$flatMapIndexed_u2dxzaTVY8`;
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$flatMapIndexed_u2dxzaTVY8); var5 < var6; var5++) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var5))) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UIntArray.flatMapIndexedTo(destination: C, transform: (Int, UInt) -> Iterable<R>): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$flatMapIndexedTo_u2d_u2d6EtJGI); var4 < var5; var4++) {
         CollectionsKt.addAll(
            destination, transform.invoke(index++, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$flatMapIndexedTo_u2d_u2d6EtJGI`, var4))) as java.lang.Iterable
         );
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ULongArray.flatMapIndexedTo(destination: C, transform: (Int, ULong) -> Iterable<R>): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$flatMapIndexedTo_u2dpe2Q0Dw); var4 < var5; var4++) {
         CollectionsKt.addAll(
            destination, transform.invoke(index++, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$flatMapIndexedTo_u2dpe2Q0Dw`, var4))) as java.lang.Iterable
         );
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UByteArray.flatMapIndexedTo(destination: C, transform: (Int, UByte) -> Iterable<R>): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$flatMapIndexedTo_u2deNpIKz8); var4 < var5; var4++) {
         CollectionsKt.addAll(
            destination, transform.invoke(index++, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$flatMapIndexedTo_u2deNpIKz8`, var4))) as java.lang.Iterable
         );
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UShortArray.flatMapIndexedTo(destination: C, transform: (Int, UShort) -> Iterable<R>): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$flatMapIndexedTo_u2dQqktQ3k); var4 < var5; var4++) {
         CollectionsKt.addAll(
            destination, transform.invoke(index++, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$flatMapIndexedTo_u2dQqktQ3k`, var4))) as java.lang.Iterable
         );
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UIntArray.flatMapTo(destination: C, transform: (UInt) -> Iterable<R>): C {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$flatMapTo_u2dwU5IKMo); var3 < var4; var3++) {
         CollectionsKt.addAll(destination, transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$flatMapTo_u2dwU5IKMo`, var3))) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ULongArray.flatMapTo(destination: C, transform: (ULong) -> Iterable<R>): C {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$flatMapTo_u2dHqK1JgA); var3 < var4; var3++) {
         CollectionsKt.addAll(destination, transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$flatMapTo_u2dHqK1JgA`, var3))) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UByteArray.flatMapTo(destination: C, transform: (UByte) -> Iterable<R>): C {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$flatMapTo_u2dwzUQCXU); var3 < var4; var3++) {
         CollectionsKt.addAll(destination, transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$flatMapTo_u2dwzUQCXU`, var3))) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UShortArray.flatMapTo(destination: C, transform: (UShort) -> Iterable<R>): C {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$flatMapTo_u2doEOeDjA); var3 < var4; var3++) {
         CollectionsKt.addAll(destination, transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$flatMapTo_u2doEOeDjA`, var3))) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K> UIntArray.groupBy(keySelector: (UInt) -> K): Map<K, List<UInt>> {
      var var2: IntArray = `$this$groupBy_u2djgv0xPQ`;
      var var3: java.util.Map = new LinkedHashMap();
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$groupBy_u2djgv0xPQ); var4 < var5; var4++) {
         var var6: Int = UIntArray.get-pVg5ArA(var2, var4);
         var var7: Any = keySelector.invoke(UInt.box-impl(var6));
         var var10000: Any = var3.get(var7);
         if (var10000 == null) {
            var var9: java.util.List = new ArrayList();
            var3.put(var7, var9);
            var10000 = var9;
         }

         (var10000 as java.util.List).add(UInt.box-impl(var6));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K> ULongArray.groupBy(keySelector: (ULong) -> K): Map<K, List<ULong>> {
      var var2: LongArray = `$this$groupBy_u2dMShoTSo`;
      var var3: java.util.Map = new LinkedHashMap();
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$groupBy_u2dMShoTSo); var4 < var5; var4++) {
         var var6: Long = ULongArray.get-s-VKNKU(var2, var4);
         var var8: Any = keySelector.invoke(ULong.box-impl(var6));
         var var10000: Any = var3.get(var8);
         if (var10000 == null) {
            var var10: java.util.List = new ArrayList();
            var3.put(var8, var10);
            var10000 = var10;
         }

         (var10000 as java.util.List).add(ULong.box-impl(var6));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K> UByteArray.groupBy(keySelector: (UByte) -> K): Map<K, List<UByte>> {
      var var2: ByteArray = `$this$groupBy_u2dJOV_ifY`;
      var var3: java.util.Map = new LinkedHashMap();
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$groupBy_u2dJOV_ifY); var4 < var5; var4++) {
         var var6: Byte = UByteArray.get-w2LRezQ(var2, var4);
         var var7: Any = keySelector.invoke(UByte.box-impl(var6));
         var var10000: Any = var3.get(var7);
         if (var10000 == null) {
            var var9: java.util.List = new ArrayList();
            var3.put(var7, var9);
            var10000 = var9;
         }

         (var10000 as java.util.List).add(UByte.box-impl(var6));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K> UShortArray.groupBy(keySelector: (UShort) -> K): Map<K, List<UShort>> {
      var var2: ShortArray = `$this$groupBy_u2dxTcfx_M`;
      var var3: java.util.Map = new LinkedHashMap();
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$groupBy_u2dxTcfx_M); var4 < var5; var4++) {
         var var6: Short = UShortArray.get-Mh2AYeg(var2, var4);
         var var7: Any = keySelector.invoke(UShort.box-impl(var6));
         var var10000: Any = var3.get(var7);
         if (var10000 == null) {
            var var9: java.util.List = new ArrayList();
            var3.put(var7, var9);
            var10000 = var9;
         }

         (var10000 as java.util.List).add(UShort.box-impl(var6));
      }

      return var3;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> UIntArray.groupBy(keySelector: (UInt) -> K, valueTransform: (UInt) -> V): Map<K, List<V>> {
      var var3: IntArray = `$this$groupBy_u2dL4rlFek`;
      var var4: java.util.Map = new LinkedHashMap();
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$groupBy_u2dL4rlFek); var5 < var6; var5++) {
         var var7: Int = UIntArray.get-pVg5ArA(var3, var5);
         var var8: Any = keySelector.invoke(UInt.box-impl(var7));
         var var10000: Any = var4.get(var8);
         if (var10000 == null) {
            var var10: java.util.List = new ArrayList();
            var4.put(var8, var10);
            var10000 = var10;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UInt.box-impl(var7)));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> ULongArray.groupBy(keySelector: (ULong) -> K, valueTransform: (ULong) -> V): Map<K, List<V>> {
      var var3: LongArray = `$this$groupBy_u2d_u2d_j2Y_u2dQ`;
      var var4: java.util.Map = new LinkedHashMap();
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$groupBy_u2d_u2d_j2Y_u2dQ); var5 < var6; var5++) {
         var var7: Long = ULongArray.get-s-VKNKU(var3, var5);
         var var9: Any = keySelector.invoke(ULong.box-impl(var7));
         var var10000: Any = var4.get(var9);
         if (var10000 == null) {
            var var11: java.util.List = new ArrayList();
            var4.put(var9, var11);
            var10000 = var11;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(ULong.box-impl(var7)));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> UByteArray.groupBy(keySelector: (UByte) -> K, valueTransform: (UByte) -> V): Map<K, List<V>> {
      var var3: ByteArray = `$this$groupBy_u2dbBsjw1Y`;
      var var4: java.util.Map = new LinkedHashMap();
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$groupBy_u2dbBsjw1Y); var5 < var6; var5++) {
         var var7: Byte = UByteArray.get-w2LRezQ(var3, var5);
         var var8: Any = keySelector.invoke(UByte.box-impl(var7));
         var var10000: Any = var4.get(var8);
         if (var10000 == null) {
            var var10: java.util.List = new ArrayList();
            var4.put(var8, var10);
            var10000 = var10;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UByte.box-impl(var7)));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> UShortArray.groupBy(keySelector: (UShort) -> K, valueTransform: (UShort) -> V): Map<K, List<V>> {
      var var3: ShortArray = `$this$groupBy_u2d3bBvP4M`;
      var var4: java.util.Map = new LinkedHashMap();
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$groupBy_u2d3bBvP4M); var5 < var6; var5++) {
         var var7: Short = UShortArray.get-Mh2AYeg(var3, var5);
         var var8: Any = keySelector.invoke(UShort.box-impl(var7));
         var var10000: Any = var4.get(var8);
         if (var10000 == null) {
            var var10: java.util.List = new ArrayList();
            var4.put(var8, var10);
            var10000 = var10;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UShort.box-impl(var7)));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<UInt>>> UIntArray.groupByTo(destination: M, keySelector: (UInt) -> K): M {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$groupByTo_u2d4D70W2E); var3 < var4; var3++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$groupByTo_u2d4D70W2E`, var3);
         var key: Any = keySelector.invoke(UInt.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var12: Any = new ArrayList();
            destination.put(key, var12);
            var10000 = var12;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(UInt.box-impl(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<ULong>>> ULongArray.groupByTo(destination: M, keySelector: (ULong) -> K): M {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$groupByTo_u2dX6OPwNk); var3 < var4; var3++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$groupByTo_u2dX6OPwNk`, var3);
         var key: Any = keySelector.invoke(ULong.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(ULong.box-impl(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<UByte>>> UByteArray.groupByTo(destination: M, keySelector: (UByte) -> K): M {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$groupByTo_u2dH21X9dk); var3 < var4; var3++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$groupByTo_u2dH21X9dk`, var3);
         var key: Any = keySelector.invoke(UByte.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var12: Any = new ArrayList();
            destination.put(key, var12);
            var10000 = var12;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(UByte.box-impl(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<UShort>>> UShortArray.groupByTo(destination: M, keySelector: (UShort) -> K): M {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$groupByTo_u2dciTST_u2d8); var3 < var4; var3++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$groupByTo_u2dciTST_u2d8`, var3);
         var key: Any = keySelector.invoke(UShort.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var12: Any = new ArrayList();
            destination.put(key, var12);
            var10000 = var12;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(UShort.box-impl(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UIntArray.groupByTo(destination: M, keySelector: (UInt) -> K, valueTransform: (UInt) -> V): M {
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$groupByTo_u2dJM6gNCM); var4 < var5; var4++) {
         var element: Int = UIntArray.get-pVg5ArA(`$this$groupByTo_u2dJM6gNCM`, var4);
         var key: Any = keySelector.invoke(UInt.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UInt.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ULongArray.groupByTo(destination: M, keySelector: (ULong) -> K, valueTransform: (ULong) -> V): M {
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$groupByTo_u2dQxgOkWg); var4 < var5; var4++) {
         var element: Long = ULongArray.get-s-VKNKU(`$this$groupByTo_u2dQxgOkWg`, var4);
         var key: Any = keySelector.invoke(ULong.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(ULong.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UByteArray.groupByTo(destination: M, keySelector: (UByte) -> K, valueTransform: (UByte) -> V): M {
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$groupByTo_u2dqOZmbk8); var4 < var5; var4++) {
         var element: Byte = UByteArray.get-w2LRezQ(`$this$groupByTo_u2dqOZmbk8`, var4);
         var key: Any = keySelector.invoke(UByte.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UByte.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> UShortArray.groupByTo(
      destination: M,
      keySelector: (UShort) -> K,
      valueTransform: (UShort) -> V
   ): M {
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$groupByTo_u2dq8RuPII); var4 < var5; var4++) {
         var element: Short = UShortArray.get-Mh2AYeg(`$this$groupByTo_u2dq8RuPII`, var4);
         var key: Any = keySelector.invoke(UShort.box-impl(element));
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(UShort.box-impl(element)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.map(transform: (UInt) -> R): List<R> {
      var var2: IntArray = `$this$map_u2djgv0xPQ`;
      var var3: java.util.Collection = new ArrayList(UIntArray.getSize-impl(`$this$map_u2djgv0xPQ`));
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$map_u2djgv0xPQ); var4 < var5; var4++) {
         var3.add(transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(var2, var4))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.map(transform: (ULong) -> R): List<R> {
      var var2: LongArray = `$this$map_u2dMShoTSo`;
      var var3: java.util.Collection = new ArrayList(ULongArray.getSize-impl(`$this$map_u2dMShoTSo`));
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$map_u2dMShoTSo); var4 < var5; var4++) {
         var3.add(transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(var2, var4))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.map(transform: (UByte) -> R): List<R> {
      var var2: ByteArray = `$this$map_u2dJOV_ifY`;
      var var3: java.util.Collection = new ArrayList(UByteArray.getSize-impl(`$this$map_u2dJOV_ifY`));
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$map_u2dJOV_ifY); var4 < var5; var4++) {
         var3.add(transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(var2, var4))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.map(transform: (UShort) -> R): List<R> {
      var var2: ShortArray = `$this$map_u2dxTcfx_M`;
      var var3: java.util.Collection = new ArrayList(UShortArray.getSize-impl(`$this$map_u2dxTcfx_M`));
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$map_u2dxTcfx_M); var4 < var5; var4++) {
         var3.add(transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var4))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.mapIndexed(transform: (Int, UInt) -> R): List<R> {
      var var2: IntArray = `$this$mapIndexed_u2dWyvcNBI`;
      var var3: java.util.Collection = new ArrayList(UIntArray.getSize-impl(`$this$mapIndexed_u2dWyvcNBI`));
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$mapIndexed_u2dWyvcNBI); var5 < var6; var5++) {
         var3.add(transform.invoke(var4++, UInt.box-impl(UIntArray.get-pVg5ArA(var2, var5))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.mapIndexed(transform: (Int, ULong) -> R): List<R> {
      var var2: LongArray = `$this$mapIndexed_u2ds8dVfGU`;
      var var3: java.util.Collection = new ArrayList(ULongArray.getSize-impl(`$this$mapIndexed_u2ds8dVfGU`));
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$mapIndexed_u2ds8dVfGU); var5 < var6; var5++) {
         var3.add(transform.invoke(var4++, ULong.box-impl(ULongArray.get-s-VKNKU(var2, var5))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.mapIndexed(transform: (Int, UByte) -> R): List<R> {
      var var2: ByteArray = `$this$mapIndexed_u2dELGow60`;
      var var3: java.util.Collection = new ArrayList(UByteArray.getSize-impl(`$this$mapIndexed_u2dELGow60`));
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$mapIndexed_u2dELGow60); var5 < var6; var5++) {
         var3.add(transform.invoke(var4++, UByte.box-impl(UByteArray.get-w2LRezQ(var2, var5))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.mapIndexed(transform: (Int, UShort) -> R): List<R> {
      var var2: ShortArray = `$this$mapIndexed_u2dxzaTVY8`;
      var var3: java.util.Collection = new ArrayList(UShortArray.getSize-impl(`$this$mapIndexed_u2dxzaTVY8`));
      var var4: Int = 0;
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$mapIndexed_u2dxzaTVY8); var5 < var6; var5++) {
         var3.add(transform.invoke(var4++, UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var5))));
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UIntArray.mapIndexedTo(destination: C, transform: (Int, UInt) -> R): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$mapIndexedTo_u2d_u2d6EtJGI); var4 < var5; var4++) {
         destination.add(transform.invoke(index++, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$mapIndexedTo_u2d_u2d6EtJGI`, var4))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ULongArray.mapIndexedTo(destination: C, transform: (Int, ULong) -> R): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$mapIndexedTo_u2dpe2Q0Dw); var4 < var5; var4++) {
         destination.add(transform.invoke(index++, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$mapIndexedTo_u2dpe2Q0Dw`, var4))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UByteArray.mapIndexedTo(destination: C, transform: (Int, UByte) -> R): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$mapIndexedTo_u2deNpIKz8); var4 < var5; var4++) {
         destination.add(transform.invoke(index++, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$mapIndexedTo_u2deNpIKz8`, var4))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UShortArray.mapIndexedTo(destination: C, transform: (Int, UShort) -> R): C {
      var index: Int = 0;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$mapIndexedTo_u2dQqktQ3k); var4 < var5; var4++) {
         destination.add(transform.invoke(index++, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$mapIndexedTo_u2dQqktQ3k`, var4))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UIntArray.mapTo(destination: C, transform: (UInt) -> R): C {
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$mapTo_u2dwU5IKMo); var3 < var4; var3++) {
         destination.add(transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$mapTo_u2dwU5IKMo`, var3))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ULongArray.mapTo(destination: C, transform: (ULong) -> R): C {
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$mapTo_u2dHqK1JgA); var3 < var4; var3++) {
         destination.add(transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$mapTo_u2dHqK1JgA`, var3))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UByteArray.mapTo(destination: C, transform: (UByte) -> R): C {
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$mapTo_u2dwzUQCXU); var3 < var4; var3++) {
         destination.add(transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$mapTo_u2dwzUQCXU`, var3))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> UShortArray.mapTo(destination: C, transform: (UShort) -> R): C {
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$mapTo_u2doEOeDjA); var3 < var4; var3++) {
         destination.add(transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$mapTo_u2doEOeDjA`, var3))));
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.withIndex(): Iterable<IndexedValue<UInt>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends UInt>>(`$this$withIndex_u2d_u2dajY_u2d9A`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<UInt> invoke() {
            return UIntArray.iterator-impl(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends UInt>>);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.withIndex(): Iterable<IndexedValue<ULong>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends ULong>>(`$this$withIndex_u2dQwZRm1k`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<ULong> invoke() {
            return ULongArray.iterator-impl(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends ULong>>);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.withIndex(): Iterable<IndexedValue<UByte>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends UByte>>(`$this$withIndex_u2dGBYM_sE`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<UByte> invoke() {
            return UByteArray.iterator-impl(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends UByte>>);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.withIndex(): Iterable<IndexedValue<UShort>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends UShort>>(`$this$withIndex_u2drL5Bavg`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<UShort> invoke() {
            return UShortArray.iterator-impl(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends UShort>>);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.all(predicate: (UInt) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$all_u2djgv0xPQ); var2 < var3; var2++) {
         if (!predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$all_u2djgv0xPQ`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.all(predicate: (ULong) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$all_u2dMShoTSo); var2 < var3; var2++) {
         if (!predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$all_u2dMShoTSo`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.all(predicate: (UByte) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$all_u2dJOV_ifY); var2 < var3; var2++) {
         if (!predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$all_u2dJOV_ifY`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.all(predicate: (UShort) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$all_u2dxTcfx_M); var2 < var3; var2++) {
         if (!predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$all_u2dxTcfx_M`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.any(): Boolean {
      return ArraysKt.any(`$this$any_u2d_u2dajY_u2d9A`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.any(): Boolean {
      return ArraysKt.any(`$this$any_u2dQwZRm1k`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.any(): Boolean {
      return ArraysKt.any(`$this$any_u2dGBYM_sE`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.any(): Boolean {
      return ArraysKt.any(`$this$any_u2drL5Bavg`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.any(predicate: (UInt) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$any_u2djgv0xPQ); var2 < var3; var2++) {
         if (predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$any_u2djgv0xPQ`, var2))) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.any(predicate: (ULong) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$any_u2dMShoTSo); var2 < var3; var2++) {
         if (predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$any_u2dMShoTSo`, var2))) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.any(predicate: (UByte) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$any_u2dJOV_ifY); var2 < var3; var2++) {
         if (predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$any_u2dJOV_ifY`, var2))) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.any(predicate: (UShort) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$any_u2dxTcfx_M); var2 < var3; var2++) {
         if (predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$any_u2dxTcfx_M`, var2))) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.count(predicate: (UInt) -> Boolean): Int {
      var count: Int = 0;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$count_u2djgv0xPQ); var3 < var4; var3++) {
         if (predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$count_u2djgv0xPQ`, var3))) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.count(predicate: (ULong) -> Boolean): Int {
      var count: Int = 0;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$count_u2dMShoTSo); var3 < var4; var3++) {
         if (predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$count_u2dMShoTSo`, var3))) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.count(predicate: (UByte) -> Boolean): Int {
      var count: Int = 0;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$count_u2dJOV_ifY); var3 < var4; var3++) {
         if (predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$count_u2dJOV_ifY`, var3))) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.count(predicate: (UShort) -> Boolean): Int {
      var count: Int = 0;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$count_u2dxTcfx_M); var3 < var4; var3++) {
         if (predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$count_u2dxTcfx_M`, var3))) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.fold(initial: R, operation: (R, UInt) -> R): R {
      var accumulator: Any = initial;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$fold_u2dzi1B2BA); var4 < var5; var4++) {
         accumulator = operation.invoke(accumulator, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$fold_u2dzi1B2BA`, var4)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.fold(initial: R, operation: (R, ULong) -> R): R {
      var accumulator: Any = initial;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$fold_u2dA8wKCXQ); var4 < var5; var4++) {
         accumulator = operation.invoke(accumulator, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$fold_u2dA8wKCXQ`, var4)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.fold(initial: R, operation: (R, UByte) -> R): R {
      var accumulator: Any = initial;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$fold_u2dyXmHNn8); var4 < var5; var4++) {
         accumulator = operation.invoke(accumulator, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$fold_u2dyXmHNn8`, var4)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.fold(initial: R, operation: (R, UShort) -> R): R {
      var accumulator: Any = initial;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$fold_u2dzww5nb8); var4 < var5; var4++) {
         accumulator = operation.invoke(accumulator, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$fold_u2dzww5nb8`, var4)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.foldIndexed(initial: R, operation: (Int, R, UInt) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$foldIndexed_u2dyVwIW0Q); var5 < var6; var5++) {
         accumulator = operation.invoke(index++, accumulator, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$foldIndexed_u2dyVwIW0Q`, var5)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.foldIndexed(initial: R, operation: (Int, R, ULong) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$foldIndexed_u2dmwnnOCs); var5 < var6; var5++) {
         accumulator = operation.invoke(index++, accumulator, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$foldIndexed_u2dmwnnOCs`, var5)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.foldIndexed(initial: R, operation: (Int, R, UByte) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$foldIndexed_u2d3iWJZGE); var5 < var6; var5++) {
         accumulator = operation.invoke(index++, accumulator, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$foldIndexed_u2d3iWJZGE`, var5)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.foldIndexed(initial: R, operation: (Int, R, UShort) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$foldIndexed_u2dbzxtMww); var5 < var6; var5++) {
         accumulator = operation.invoke(index++, accumulator, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$foldIndexed_u2dbzxtMww`, var5)));
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.foldRight(initial: R, operation: (UInt, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight_u2dzi1B2BA`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$foldRight_u2dzi1B2BA`, index--)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.foldRight(initial: R, operation: (ULong, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight_u2dA8wKCXQ`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$foldRight_u2dA8wKCXQ`, index--)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.foldRight(initial: R, operation: (UByte, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight_u2dyXmHNn8`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$foldRight_u2dyXmHNn8`, index--)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.foldRight(initial: R, operation: (UShort, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight_u2dzww5nb8`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$foldRight_u2dzww5nb8`, index--)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.foldRightIndexed(initial: R, operation: (Int, UInt, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed_u2dyVwIW0Q`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$foldRightIndexed_u2dyVwIW0Q`, index)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.foldRightIndexed(initial: R, operation: (Int, ULong, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed_u2dmwnnOCs`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$foldRightIndexed_u2dmwnnOCs`, index)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.foldRightIndexed(initial: R, operation: (Int, UByte, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed_u2d3iWJZGE`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$foldRightIndexed_u2d3iWJZGE`, index)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.foldRightIndexed(initial: R, operation: (Int, UShort, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed_u2dbzxtMww`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$foldRightIndexed_u2dbzxtMww`, index)), accumulator);
      }

      return (R)accumulator;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.forEach(action: (UInt) -> Unit) {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$forEach_u2djgv0xPQ); var2 < var3; var2++) {
         action.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$forEach_u2djgv0xPQ`, var2)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.forEach(action: (ULong) -> Unit) {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$forEach_u2dMShoTSo); var2 < var3; var2++) {
         action.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$forEach_u2dMShoTSo`, var2)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.forEach(action: (UByte) -> Unit) {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$forEach_u2dJOV_ifY); var2 < var3; var2++) {
         action.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$forEach_u2dJOV_ifY`, var2)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.forEach(action: (UShort) -> Unit) {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$forEach_u2dxTcfx_M); var2 < var3; var2++) {
         action.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$forEach_u2dxTcfx_M`, var2)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.forEachIndexed(action: (Int, UInt) -> Unit) {
      var index: Int = 0;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$forEachIndexed_u2dWyvcNBI); var3 < var4; var3++) {
         action.invoke(index++, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$forEachIndexed_u2dWyvcNBI`, var3)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.forEachIndexed(action: (Int, ULong) -> Unit) {
      var index: Int = 0;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$forEachIndexed_u2ds8dVfGU); var3 < var4; var3++) {
         action.invoke(index++, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$forEachIndexed_u2ds8dVfGU`, var3)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.forEachIndexed(action: (Int, UByte) -> Unit) {
      var index: Int = 0;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$forEachIndexed_u2dELGow60); var3 < var4; var3++) {
         action.invoke(index++, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$forEachIndexed_u2dELGow60`, var3)));
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.forEachIndexed(action: (Int, UShort) -> Unit) {
      var index: Int = 0;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$forEachIndexed_u2dxzaTVY8); var3 < var4; var3++) {
         action.invoke(index++, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$forEachIndexed_u2dxzaTVY8`, var3)));
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.max(): UInt {
      if (UIntArray.isEmpty-impl(`$this$max_u2d_u2dajY_u2d9A`)) {
         throw new NoSuchElementException();
      } else {
         var max: Int = UIntArray.get-pVg5ArA(`$this$max_u2d_u2dajY_u2d9A`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max_u2d_u2dajY_u2d9A`)).iterator();

         while (var2.hasNext()) {
            var var5: Int = UIntArray.get-pVg5ArA(`$this$max_u2d_u2dajY_u2d9A`, var2.nextInt());
            if (Integer.compareUnsigned(max, var5) < 0) {
               max = var5;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.max(): ULong {
      if (ULongArray.isEmpty-impl(`$this$max_u2dQwZRm1k`)) {
         throw new NoSuchElementException();
      } else {
         var max: Long = ULongArray.get-s-VKNKU(`$this$max_u2dQwZRm1k`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max_u2dQwZRm1k`)).iterator();

         while (var3.hasNext()) {
            var var7: Long = ULongArray.get-s-VKNKU(`$this$max_u2dQwZRm1k`, var3.nextInt());
            if (java.lang.Long.compareUnsigned(max, var7) < 0) {
               max = var7;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.max(): UByte {
      if (UByteArray.isEmpty-impl(`$this$max_u2dGBYM_sE`)) {
         throw new NoSuchElementException();
      } else {
         var max: Byte = UByteArray.get-w2LRezQ(`$this$max_u2dGBYM_sE`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max_u2dGBYM_sE`)).iterator();

         while (var2.hasNext()) {
            var var5: Byte = UByteArray.get-w2LRezQ(`$this$max_u2dGBYM_sE`, var2.nextInt());
            if (Intrinsics.compare(max and 255, var5 and 255) < 0) {
               max = var5;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.max(): UShort {
      if (UShortArray.isEmpty-impl(`$this$max_u2drL5Bavg`)) {
         throw new NoSuchElementException();
      } else {
         var max: Short = UShortArray.get-Mh2AYeg(`$this$max_u2drL5Bavg`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max_u2drL5Bavg`)).iterator();

         while (var2.hasNext()) {
            var var5: Short = UShortArray.get-Mh2AYeg(`$this$max_u2drL5Bavg`, var2.nextInt());
            if (Intrinsics.compare(max and '\uffff', var5 and '\uffff') < 0) {
               max = var5;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.maxBy(selector: (UInt) -> R): UInt {
      if (UIntArray.isEmpty-impl(`$this$maxBy_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Int = UIntArray.get-pVg5ArA(`$this$maxBy_u2djgv0xPQ`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy_u2djgv0xPQ`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UInt.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Int = UIntArray.get-pVg5ArA(`$this$maxBy_u2djgv0xPQ`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UInt.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.maxBy(selector: (ULong) -> R): ULong {
      if (ULongArray.isEmpty-impl(`$this$maxBy_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Long = ULongArray.get-s-VKNKU(`$this$maxBy_u2dMShoTSo`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy_u2dMShoTSo`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(ULong.box-impl(maxElem)) as java.lang.Comparable;
            var var11: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var11.hasNext()) {
               var e: Long = ULongArray.get-s-VKNKU(`$this$maxBy_u2dMShoTSo`, var11.nextInt());
               var v: java.lang.Comparable = selector.invoke(ULong.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.maxBy(selector: (UByte) -> R): UByte {
      if (UByteArray.isEmpty-impl(`$this$maxBy_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Byte = UByteArray.get-w2LRezQ(`$this$maxBy_u2dJOV_ifY`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy_u2dJOV_ifY`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UByte.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Byte = UByteArray.get-w2LRezQ(`$this$maxBy_u2dJOV_ifY`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UByte.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.maxBy(selector: (UShort) -> R): UShort {
      if (UShortArray.isEmpty-impl(`$this$maxBy_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Short = UShortArray.get-Mh2AYeg(`$this$maxBy_u2dxTcfx_M`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy_u2dxTcfx_M`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UShort.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Short = UShortArray.get-Mh2AYeg(`$this$maxBy_u2dxTcfx_M`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UShort.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.maxByOrNull(selector: (UInt) -> R): UInt? {
      if (UIntArray.isEmpty-impl(`$this$maxByOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var maxElem: Int = UIntArray.get-pVg5ArA(`$this$maxByOrNull_u2djgv0xPQ`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull_u2djgv0xPQ`);
         if (lastIndex == 0) {
            return UInt.box-impl(maxElem);
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UInt.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Int = UIntArray.get-pVg5ArA(`$this$maxByOrNull_u2djgv0xPQ`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UInt.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return UInt.box-impl(maxElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.maxByOrNull(selector: (ULong) -> R): ULong? {
      if (ULongArray.isEmpty-impl(`$this$maxByOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var maxElem: Long = ULongArray.get-s-VKNKU(`$this$maxByOrNull_u2dMShoTSo`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull_u2dMShoTSo`);
         if (lastIndex == 0) {
            return ULong.box-impl(maxElem);
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(ULong.box-impl(maxElem)) as java.lang.Comparable;
            var var11: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var11.hasNext()) {
               var e: Long = ULongArray.get-s-VKNKU(`$this$maxByOrNull_u2dMShoTSo`, var11.nextInt());
               var v: java.lang.Comparable = selector.invoke(ULong.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return ULong.box-impl(maxElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.maxByOrNull(selector: (UByte) -> R): UByte? {
      if (UByteArray.isEmpty-impl(`$this$maxByOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var maxElem: Byte = UByteArray.get-w2LRezQ(`$this$maxByOrNull_u2dJOV_ifY`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull_u2dJOV_ifY`);
         if (lastIndex == 0) {
            return UByte.box-impl(maxElem);
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UByte.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Byte = UByteArray.get-w2LRezQ(`$this$maxByOrNull_u2dJOV_ifY`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UByte.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return UByte.box-impl(maxElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.maxByOrNull(selector: (UShort) -> R): UShort? {
      if (UShortArray.isEmpty-impl(`$this$maxByOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var maxElem: Short = UShortArray.get-Mh2AYeg(`$this$maxByOrNull_u2dxTcfx_M`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull_u2dxTcfx_M`);
         if (lastIndex == 0) {
            return UShort.box-impl(maxElem);
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(UShort.box-impl(maxElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Short = UShortArray.get-Mh2AYeg(`$this$maxByOrNull_u2dxTcfx_M`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UShort.box-impl(e)) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return UShort.box-impl(maxElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.maxOf(selector: (UInt) -> Double): Double {
      if (UIntArray.isEmpty-impl(`$this$maxOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2djgv0xPQ`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.maxOf(selector: (ULong) -> Double): Double {
      if (ULongArray.isEmpty-impl(`$this$maxOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dMShoTSo`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.maxOf(selector: (UByte) -> Double): Double {
      if (UByteArray.isEmpty-impl(`$this$maxOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dJOV_ifY`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.maxOf(selector: (UShort) -> Double): Double {
      if (UShortArray.isEmpty-impl(`$this$maxOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dxTcfx_M`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.maxOf(selector: (UInt) -> Float): Float {
      if (UIntArray.isEmpty-impl(`$this$maxOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.maxOf(selector: (ULong) -> Float): Float {
      if (ULongArray.isEmpty-impl(`$this$maxOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.maxOf(selector: (UByte) -> Float): Float {
      if (UByteArray.isEmpty-impl(`$this$maxOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.maxOf(selector: (UShort) -> Float): Float {
      if (UShortArray.isEmpty-impl(`$this$maxOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue, (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.maxOf(selector: (UInt) -> R): R {
      if (UIntArray.isEmpty-impl(`$this$maxOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOf_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.maxOf(selector: (ULong) -> R): R {
      if (ULongArray.isEmpty-impl(`$this$maxOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOf_u2dMShoTSo`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.maxOf(selector: (UByte) -> R): R {
      if (UByteArray.isEmpty-impl(`$this$maxOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOf_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.maxOf(selector: (UShort) -> R): R {
      if (UShortArray.isEmpty-impl(`$this$maxOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOf_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.maxOfOrNull(selector: (UInt) -> Double): Double? {
      if (UIntArray.isEmpty-impl(`$this$maxOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2djgv0xPQ`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.maxOfOrNull(selector: (ULong) -> Double): Double? {
      if (ULongArray.isEmpty-impl(`$this$maxOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dMShoTSo`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.maxOfOrNull(selector: (UByte) -> Double): Double? {
      if (UByteArray.isEmpty-impl(`$this$maxOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dJOV_ifY`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.maxOfOrNull(selector: (UShort) -> Double): Double? {
      if (UShortArray.isEmpty-impl(`$this$maxOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Number)
            .doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dxTcfx_M`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.maxOfOrNull(selector: (UInt) -> Float): Float? {
      if (UIntArray.isEmpty-impl(`$this$maxOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.maxOfOrNull(selector: (ULong) -> Float): Float? {
      if (ULongArray.isEmpty-impl(`$this$maxOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.maxOfOrNull(selector: (UByte) -> Float): Float? {
      if (UByteArray.isEmpty-impl(`$this$maxOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.maxOfOrNull(selector: (UShort) -> Float): Float? {
      if (UShortArray.isEmpty-impl(`$this$maxOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(
               maxValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.maxOfOrNull(selector: (UInt) -> R): R? {
      if (UIntArray.isEmpty-impl(`$this$maxOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfOrNull_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.maxOfOrNull(selector: (ULong) -> R): R? {
      if (ULongArray.isEmpty-impl(`$this$maxOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfOrNull_u2dMShoTSo`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.maxOfOrNull(selector: (UByte) -> R): R? {
      if (UByteArray.isEmpty-impl(`$this$maxOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfOrNull_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.maxOfOrNull(selector: (UShort) -> R): R? {
      if (UShortArray.isEmpty-impl(`$this$maxOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfOrNull_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Comparable;
            if (maxValue.compareTo(var6) < 0) {
               maxValue = var6;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.maxOfWith(comparator: Comparator<in R>, selector: (UInt) -> R): R {
      if (UIntArray.isEmpty-impl(`$this$maxOfWith_u2dmyNOsp4`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfWith_u2dmyNOsp4`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith_u2dmyNOsp4`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfWith_u2dmyNOsp4`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.maxOfWith(comparator: Comparator<in R>, selector: (ULong) -> R): R {
      if (ULongArray.isEmpty-impl(`$this$maxOfWith_u2d5NtCtWE`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfWith_u2d5NtCtWE`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith_u2d5NtCtWE`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfWith_u2d5NtCtWE`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.maxOfWith(comparator: Comparator<in R>, selector: (UByte) -> R): R {
      if (UByteArray.isEmpty-impl(`$this$maxOfWith_u2dLTi4i_s`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfWith_u2dLTi4i_s`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith_u2dLTi4i_s`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfWith_u2dLTi4i_s`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.maxOfWith(comparator: Comparator<in R>, selector: (UShort) -> R): R {
      if (UShortArray.isEmpty-impl(`$this$maxOfWith_u2dl8EHGbQ`)) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfWith_u2dl8EHGbQ`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith_u2dl8EHGbQ`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfWith_u2dl8EHGbQ`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (UInt) -> R): R? {
      if (UIntArray.isEmpty-impl(`$this$maxOfWithOrNull_u2dmyNOsp4`)) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfWithOrNull_u2dmyNOsp4`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull_u2dmyNOsp4`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$maxOfWithOrNull_u2dmyNOsp4`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (ULong) -> R): R? {
      if (ULongArray.isEmpty-impl(`$this$maxOfWithOrNull_u2d5NtCtWE`)) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfWithOrNull_u2d5NtCtWE`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull_u2d5NtCtWE`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$maxOfWithOrNull_u2d5NtCtWE`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (UByte) -> R): R? {
      if (UByteArray.isEmpty-impl(`$this$maxOfWithOrNull_u2dLTi4i_s`)) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfWithOrNull_u2dLTi4i_s`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull_u2dLTi4i_s`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$maxOfWithOrNull_u2dLTi4i_s`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (UShort) -> R): R? {
      if (UShortArray.isEmpty-impl(`$this$maxOfWithOrNull_u2dl8EHGbQ`)) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfWithOrNull_u2dl8EHGbQ`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull_u2dl8EHGbQ`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$maxOfWithOrNull_u2dl8EHGbQ`, var4.nextInt())));
            if (comparator.compare(maxValue, var7) < 0) {
               maxValue = var7;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.maxOrNull(): UInt? {
      if (UIntArray.isEmpty-impl(`$this$maxOrNull_u2d_u2dajY_u2d9A`)) {
         return null;
      } else {
         var max: Int = UIntArray.get-pVg5ArA(`$this$maxOrNull_u2d_u2dajY_u2d9A`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull_u2d_u2dajY_u2d9A`)).iterator();

         while (var2.hasNext()) {
            var var5: Int = UIntArray.get-pVg5ArA(`$this$maxOrNull_u2d_u2dajY_u2d9A`, var2.nextInt());
            if (Integer.compareUnsigned(max, var5) < 0) {
               max = var5;
            }
         }

         return UInt.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.maxOrNull(): ULong? {
      if (ULongArray.isEmpty-impl(`$this$maxOrNull_u2dQwZRm1k`)) {
         return null;
      } else {
         var max: Long = ULongArray.get-s-VKNKU(`$this$maxOrNull_u2dQwZRm1k`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull_u2dQwZRm1k`)).iterator();

         while (var3.hasNext()) {
            var var7: Long = ULongArray.get-s-VKNKU(`$this$maxOrNull_u2dQwZRm1k`, var3.nextInt());
            if (java.lang.Long.compareUnsigned(max, var7) < 0) {
               max = var7;
            }
         }

         return ULong.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.maxOrNull(): UByte? {
      if (UByteArray.isEmpty-impl(`$this$maxOrNull_u2dGBYM_sE`)) {
         return null;
      } else {
         var max: Byte = UByteArray.get-w2LRezQ(`$this$maxOrNull_u2dGBYM_sE`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull_u2dGBYM_sE`)).iterator();

         while (var2.hasNext()) {
            var var5: Byte = UByteArray.get-w2LRezQ(`$this$maxOrNull_u2dGBYM_sE`, var2.nextInt());
            if (Intrinsics.compare(max and 255, var5 and 255) < 0) {
               max = var5;
            }
         }

         return UByte.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.maxOrNull(): UShort? {
      if (UShortArray.isEmpty-impl(`$this$maxOrNull_u2drL5Bavg`)) {
         return null;
      } else {
         var max: Short = UShortArray.get-Mh2AYeg(`$this$maxOrNull_u2drL5Bavg`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull_u2drL5Bavg`)).iterator();

         while (var2.hasNext()) {
            var var5: Short = UShortArray.get-Mh2AYeg(`$this$maxOrNull_u2drL5Bavg`, var2.nextInt());
            if (Intrinsics.compare(max and '\uffff', var5 and '\uffff') < 0) {
               max = var5;
            }
         }

         return UShort.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.maxWith(comparator: Comparator<in UInt>): UInt {
      if (UIntArray.isEmpty-impl(`$this$maxWith_u2dYmdZ_VM`)) {
         throw new NoSuchElementException();
      } else {
         var max: Int = UIntArray.get-pVg5ArA(`$this$maxWith_u2dYmdZ_VM`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith_u2dYmdZ_VM`)).iterator();

         while (var3.hasNext()) {
            var var6: Int = UIntArray.get-pVg5ArA(`$this$maxWith_u2dYmdZ_VM`, var3.nextInt());
            if (comparator.compare(UInt.box-impl(max), UInt.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.maxWith(comparator: Comparator<in ULong>): ULong {
      if (ULongArray.isEmpty-impl(`$this$maxWith_u2dzrEWJaI`)) {
         throw new NoSuchElementException();
      } else {
         var max: Long = ULongArray.get-s-VKNKU(`$this$maxWith_u2dzrEWJaI`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith_u2dzrEWJaI`)).iterator();

         while (var4.hasNext()) {
            var var8: Long = ULongArray.get-s-VKNKU(`$this$maxWith_u2dzrEWJaI`, var4.nextInt());
            if (comparator.compare(ULong.box-impl(max), ULong.box-impl(var8)) < 0) {
               max = var8;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.maxWith(comparator: Comparator<in UByte>): UByte {
      if (UByteArray.isEmpty-impl(`$this$maxWith_u2dXMRcp5o`)) {
         throw new NoSuchElementException();
      } else {
         var max: Byte = UByteArray.get-w2LRezQ(`$this$maxWith_u2dXMRcp5o`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith_u2dXMRcp5o`)).iterator();

         while (var3.hasNext()) {
            var var6: Byte = UByteArray.get-w2LRezQ(`$this$maxWith_u2dXMRcp5o`, var3.nextInt());
            if (comparator.compare(UByte.box-impl(max), UByte.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.maxWith(comparator: Comparator<in UShort>): UShort {
      if (UShortArray.isEmpty-impl(`$this$maxWith_u2deOHTfZs`)) {
         throw new NoSuchElementException();
      } else {
         var max: Short = UShortArray.get-Mh2AYeg(`$this$maxWith_u2deOHTfZs`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith_u2deOHTfZs`)).iterator();

         while (var3.hasNext()) {
            var var6: Short = UShortArray.get-Mh2AYeg(`$this$maxWith_u2deOHTfZs`, var3.nextInt());
            if (comparator.compare(UShort.box-impl(max), UShort.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.maxWithOrNull(comparator: Comparator<in UInt>): UInt? {
      if (UIntArray.isEmpty-impl(`$this$maxWithOrNull_u2dYmdZ_VM`)) {
         return null;
      } else {
         var max: Int = UIntArray.get-pVg5ArA(`$this$maxWithOrNull_u2dYmdZ_VM`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull_u2dYmdZ_VM`)).iterator();

         while (var3.hasNext()) {
            var var6: Int = UIntArray.get-pVg5ArA(`$this$maxWithOrNull_u2dYmdZ_VM`, var3.nextInt());
            if (comparator.compare(UInt.box-impl(max), UInt.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return UInt.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.maxWithOrNull(comparator: Comparator<in ULong>): ULong? {
      if (ULongArray.isEmpty-impl(`$this$maxWithOrNull_u2dzrEWJaI`)) {
         return null;
      } else {
         var max: Long = ULongArray.get-s-VKNKU(`$this$maxWithOrNull_u2dzrEWJaI`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull_u2dzrEWJaI`)).iterator();

         while (var4.hasNext()) {
            var var8: Long = ULongArray.get-s-VKNKU(`$this$maxWithOrNull_u2dzrEWJaI`, var4.nextInt());
            if (comparator.compare(ULong.box-impl(max), ULong.box-impl(var8)) < 0) {
               max = var8;
            }
         }

         return ULong.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.maxWithOrNull(comparator: Comparator<in UByte>): UByte? {
      if (UByteArray.isEmpty-impl(`$this$maxWithOrNull_u2dXMRcp5o`)) {
         return null;
      } else {
         var max: Byte = UByteArray.get-w2LRezQ(`$this$maxWithOrNull_u2dXMRcp5o`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull_u2dXMRcp5o`)).iterator();

         while (var3.hasNext()) {
            var var6: Byte = UByteArray.get-w2LRezQ(`$this$maxWithOrNull_u2dXMRcp5o`, var3.nextInt());
            if (comparator.compare(UByte.box-impl(max), UByte.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return UByte.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.maxWithOrNull(comparator: Comparator<in UShort>): UShort? {
      if (UShortArray.isEmpty-impl(`$this$maxWithOrNull_u2deOHTfZs`)) {
         return null;
      } else {
         var max: Short = UShortArray.get-Mh2AYeg(`$this$maxWithOrNull_u2deOHTfZs`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull_u2deOHTfZs`)).iterator();

         while (var3.hasNext()) {
            var var6: Short = UShortArray.get-Mh2AYeg(`$this$maxWithOrNull_u2deOHTfZs`, var3.nextInt());
            if (comparator.compare(UShort.box-impl(max), UShort.box-impl(var6)) < 0) {
               max = var6;
            }
         }

         return UShort.box-impl(max);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.min(): UInt {
      if (UIntArray.isEmpty-impl(`$this$min_u2d_u2dajY_u2d9A`)) {
         throw new NoSuchElementException();
      } else {
         var min: Int = UIntArray.get-pVg5ArA(`$this$min_u2d_u2dajY_u2d9A`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min_u2d_u2dajY_u2d9A`)).iterator();

         while (var2.hasNext()) {
            var var5: Int = UIntArray.get-pVg5ArA(`$this$min_u2d_u2dajY_u2d9A`, var2.nextInt());
            if (Integer.compareUnsigned(min, var5) > 0) {
               min = var5;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.min(): ULong {
      if (ULongArray.isEmpty-impl(`$this$min_u2dQwZRm1k`)) {
         throw new NoSuchElementException();
      } else {
         var min: Long = ULongArray.get-s-VKNKU(`$this$min_u2dQwZRm1k`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min_u2dQwZRm1k`)).iterator();

         while (var3.hasNext()) {
            var var7: Long = ULongArray.get-s-VKNKU(`$this$min_u2dQwZRm1k`, var3.nextInt());
            if (java.lang.Long.compareUnsigned(min, var7) > 0) {
               min = var7;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.min(): UByte {
      if (UByteArray.isEmpty-impl(`$this$min_u2dGBYM_sE`)) {
         throw new NoSuchElementException();
      } else {
         var min: Byte = UByteArray.get-w2LRezQ(`$this$min_u2dGBYM_sE`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min_u2dGBYM_sE`)).iterator();

         while (var2.hasNext()) {
            var var5: Byte = UByteArray.get-w2LRezQ(`$this$min_u2dGBYM_sE`, var2.nextInt());
            if (Intrinsics.compare(min and 255, var5 and 255) > 0) {
               min = var5;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.min(): UShort {
      if (UShortArray.isEmpty-impl(`$this$min_u2drL5Bavg`)) {
         throw new NoSuchElementException();
      } else {
         var min: Short = UShortArray.get-Mh2AYeg(`$this$min_u2drL5Bavg`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min_u2drL5Bavg`)).iterator();

         while (var2.hasNext()) {
            var var5: Short = UShortArray.get-Mh2AYeg(`$this$min_u2drL5Bavg`, var2.nextInt());
            if (Intrinsics.compare(min and '\uffff', var5 and '\uffff') > 0) {
               min = var5;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.minBy(selector: (UInt) -> R): UInt {
      if (UIntArray.isEmpty-impl(`$this$minBy_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var minElem: Int = UIntArray.get-pVg5ArA(`$this$minBy_u2djgv0xPQ`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy_u2djgv0xPQ`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UInt.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Int = UIntArray.get-pVg5ArA(`$this$minBy_u2djgv0xPQ`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UInt.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.minBy(selector: (ULong) -> R): ULong {
      if (ULongArray.isEmpty-impl(`$this$minBy_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var minElem: Long = ULongArray.get-s-VKNKU(`$this$minBy_u2dMShoTSo`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy_u2dMShoTSo`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(ULong.box-impl(minElem)) as java.lang.Comparable;
            var var11: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var11.hasNext()) {
               var e: Long = ULongArray.get-s-VKNKU(`$this$minBy_u2dMShoTSo`, var11.nextInt());
               var v: java.lang.Comparable = selector.invoke(ULong.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.minBy(selector: (UByte) -> R): UByte {
      if (UByteArray.isEmpty-impl(`$this$minBy_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var minElem: Byte = UByteArray.get-w2LRezQ(`$this$minBy_u2dJOV_ifY`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy_u2dJOV_ifY`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UByte.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Byte = UByteArray.get-w2LRezQ(`$this$minBy_u2dJOV_ifY`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UByte.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow-U")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.minBy(selector: (UShort) -> R): UShort {
      if (UShortArray.isEmpty-impl(`$this$minBy_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var minElem: Short = UShortArray.get-Mh2AYeg(`$this$minBy_u2dxTcfx_M`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy_u2dxTcfx_M`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UShort.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Short = UShortArray.get-Mh2AYeg(`$this$minBy_u2dxTcfx_M`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UShort.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.minByOrNull(selector: (UInt) -> R): UInt? {
      if (UIntArray.isEmpty-impl(`$this$minByOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var minElem: Int = UIntArray.get-pVg5ArA(`$this$minByOrNull_u2djgv0xPQ`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull_u2djgv0xPQ`);
         if (lastIndex == 0) {
            return UInt.box-impl(minElem);
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UInt.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Int = UIntArray.get-pVg5ArA(`$this$minByOrNull_u2djgv0xPQ`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UInt.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return UInt.box-impl(minElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.minByOrNull(selector: (ULong) -> R): ULong? {
      if (ULongArray.isEmpty-impl(`$this$minByOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var minElem: Long = ULongArray.get-s-VKNKU(`$this$minByOrNull_u2dMShoTSo`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull_u2dMShoTSo`);
         if (lastIndex == 0) {
            return ULong.box-impl(minElem);
         } else {
            var minValue: java.lang.Comparable = selector.invoke(ULong.box-impl(minElem)) as java.lang.Comparable;
            var var11: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var11.hasNext()) {
               var e: Long = ULongArray.get-s-VKNKU(`$this$minByOrNull_u2dMShoTSo`, var11.nextInt());
               var v: java.lang.Comparable = selector.invoke(ULong.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return ULong.box-impl(minElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.minByOrNull(selector: (UByte) -> R): UByte? {
      if (UByteArray.isEmpty-impl(`$this$minByOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var minElem: Byte = UByteArray.get-w2LRezQ(`$this$minByOrNull_u2dJOV_ifY`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull_u2dJOV_ifY`);
         if (lastIndex == 0) {
            return UByte.box-impl(minElem);
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UByte.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Byte = UByteArray.get-w2LRezQ(`$this$minByOrNull_u2dJOV_ifY`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UByte.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return UByte.box-impl(minElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.minByOrNull(selector: (UShort) -> R): UShort? {
      if (UShortArray.isEmpty-impl(`$this$minByOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var minElem: Short = UShortArray.get-Mh2AYeg(`$this$minByOrNull_u2dxTcfx_M`, 0);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull_u2dxTcfx_M`);
         if (lastIndex == 0) {
            return UShort.box-impl(minElem);
         } else {
            var minValue: java.lang.Comparable = selector.invoke(UShort.box-impl(minElem)) as java.lang.Comparable;
            var var9: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var9.hasNext()) {
               var e: Short = UShortArray.get-Mh2AYeg(`$this$minByOrNull_u2dxTcfx_M`, var9.nextInt());
               var v: java.lang.Comparable = selector.invoke(UShort.box-impl(e)) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return UShort.box-impl(minElem);
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.minOf(selector: (UInt) -> Double): Double {
      if (UIntArray.isEmpty-impl(`$this$minOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2djgv0xPQ`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.minOf(selector: (ULong) -> Double): Double {
      if (ULongArray.isEmpty-impl(`$this$minOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dMShoTSo`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.minOf(selector: (UByte) -> Double): Double {
      if (UByteArray.isEmpty-impl(`$this$minOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dJOV_ifY`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.minOf(selector: (UShort) -> Double): Double {
      if (UShortArray.isEmpty-impl(`$this$minOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dxTcfx_M`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.minOf(selector: (UInt) -> Float): Float {
      if (UIntArray.isEmpty-impl(`$this$minOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.minOf(selector: (ULong) -> Float): Float {
      if (ULongArray.isEmpty-impl(`$this$minOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.minOf(selector: (UByte) -> Float): Float {
      if (UByteArray.isEmpty-impl(`$this$minOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.minOf(selector: (UShort) -> Float): Float {
      if (UShortArray.isEmpty-impl(`$this$minOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue, (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.minOf(selector: (UInt) -> R): R {
      if (UIntArray.isEmpty-impl(`$this$minOf_u2djgv0xPQ`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOf_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.minOf(selector: (ULong) -> R): R {
      if (ULongArray.isEmpty-impl(`$this$minOf_u2dMShoTSo`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOf_u2dMShoTSo`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.minOf(selector: (UByte) -> R): R {
      if (UByteArray.isEmpty-impl(`$this$minOf_u2dJOV_ifY`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOf_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.minOf(selector: (UShort) -> R): R {
      if (UShortArray.isEmpty-impl(`$this$minOf_u2dxTcfx_M`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOf_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.minOfOrNull(selector: (UInt) -> Double): Double? {
      if (UIntArray.isEmpty-impl(`$this$minOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2djgv0xPQ`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.minOfOrNull(selector: (ULong) -> Double): Double? {
      if (ULongArray.isEmpty-impl(`$this$minOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dMShoTSo`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.minOfOrNull(selector: (UByte) -> Double): Double? {
      if (UByteArray.isEmpty-impl(`$this$minOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dJOV_ifY`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.minOfOrNull(selector: (UShort) -> Double): Double? {
      if (UShortArray.isEmpty-impl(`$this$minOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Number)
            .doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dxTcfx_M`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, var4.nextInt()))) as java.lang.Number).doubleValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.minOfOrNull(selector: (UInt) -> Float): Float? {
      if (UIntArray.isEmpty-impl(`$this$minOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.minOfOrNull(selector: (ULong) -> Float): Float? {
      if (ULongArray.isEmpty-impl(`$this$minOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.minOfOrNull(selector: (UByte) -> Float): Float? {
      if (UByteArray.isEmpty-impl(`$this$minOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.minOfOrNull(selector: (UShort) -> Float): Float? {
      if (UShortArray.isEmpty-impl(`$this$minOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(
               minValue,
               (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Number).floatValue()
            );
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UIntArray.minOfOrNull(selector: (UInt) -> R): R? {
      if (UIntArray.isEmpty-impl(`$this$minOfOrNull_u2djgv0xPQ`)) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2djgv0xPQ`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfOrNull_u2djgv0xPQ`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ULongArray.minOfOrNull(selector: (ULong) -> R): R? {
      if (ULongArray.isEmpty-impl(`$this$minOfOrNull_u2dMShoTSo`)) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dMShoTSo`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfOrNull_u2dMShoTSo`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UByteArray.minOfOrNull(selector: (UByte) -> R): R? {
      if (UByteArray.isEmpty-impl(`$this$minOfOrNull_u2dJOV_ifY`)) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dJOV_ifY`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfOrNull_u2dJOV_ifY`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> UShortArray.minOfOrNull(selector: (UShort) -> R): R? {
      if (UShortArray.isEmpty-impl(`$this$minOfOrNull_u2dxTcfx_M`)) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, 0))) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull_u2dxTcfx_M`)).iterator();

         while (var3.hasNext()) {
            var var6: java.lang.Comparable = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfOrNull_u2dxTcfx_M`, var3.nextInt()))) as java.lang.Comparable;
            if (minValue.compareTo(var6) > 0) {
               minValue = var6;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.minOfWith(comparator: Comparator<in R>, selector: (UInt) -> R): R {
      if (UIntArray.isEmpty-impl(`$this$minOfWith_u2dmyNOsp4`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfWith_u2dmyNOsp4`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith_u2dmyNOsp4`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfWith_u2dmyNOsp4`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.minOfWith(comparator: Comparator<in R>, selector: (ULong) -> R): R {
      if (ULongArray.isEmpty-impl(`$this$minOfWith_u2d5NtCtWE`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfWith_u2d5NtCtWE`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith_u2d5NtCtWE`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfWith_u2d5NtCtWE`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.minOfWith(comparator: Comparator<in R>, selector: (UByte) -> R): R {
      if (UByteArray.isEmpty-impl(`$this$minOfWith_u2dLTi4i_s`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfWith_u2dLTi4i_s`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith_u2dLTi4i_s`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfWith_u2dLTi4i_s`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.minOfWith(comparator: Comparator<in R>, selector: (UShort) -> R): R {
      if (UShortArray.isEmpty-impl(`$this$minOfWith_u2dl8EHGbQ`)) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfWith_u2dl8EHGbQ`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith_u2dl8EHGbQ`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfWith_u2dl8EHGbQ`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (UInt) -> R): R? {
      if (UIntArray.isEmpty-impl(`$this$minOfWithOrNull_u2dmyNOsp4`)) {
         return null;
      } else {
         var minValue: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfWithOrNull_u2dmyNOsp4`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull_u2dmyNOsp4`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$minOfWithOrNull_u2dmyNOsp4`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (ULong) -> R): R? {
      if (ULongArray.isEmpty-impl(`$this$minOfWithOrNull_u2d5NtCtWE`)) {
         return null;
      } else {
         var minValue: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfWithOrNull_u2d5NtCtWE`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull_u2d5NtCtWE`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$minOfWithOrNull_u2d5NtCtWE`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (UByte) -> R): R? {
      if (UByteArray.isEmpty-impl(`$this$minOfWithOrNull_u2dLTi4i_s`)) {
         return null;
      } else {
         var minValue: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfWithOrNull_u2dLTi4i_s`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull_u2dLTi4i_s`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$minOfWithOrNull_u2dLTi4i_s`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (UShort) -> R): R? {
      if (UShortArray.isEmpty-impl(`$this$minOfWithOrNull_u2dl8EHGbQ`)) {
         return null;
      } else {
         var minValue: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfWithOrNull_u2dl8EHGbQ`, 0)));
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull_u2dl8EHGbQ`)).iterator();

         while (var4.hasNext()) {
            var var7: Any = selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$minOfWithOrNull_u2dl8EHGbQ`, var4.nextInt())));
            if (comparator.compare(minValue, var7) > 0) {
               minValue = var7;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.minOrNull(): UInt? {
      if (UIntArray.isEmpty-impl(`$this$minOrNull_u2d_u2dajY_u2d9A`)) {
         return null;
      } else {
         var min: Int = UIntArray.get-pVg5ArA(`$this$minOrNull_u2d_u2dajY_u2d9A`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull_u2d_u2dajY_u2d9A`)).iterator();

         while (var2.hasNext()) {
            var var5: Int = UIntArray.get-pVg5ArA(`$this$minOrNull_u2d_u2dajY_u2d9A`, var2.nextInt());
            if (Integer.compareUnsigned(min, var5) > 0) {
               min = var5;
            }
         }

         return UInt.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.minOrNull(): ULong? {
      if (ULongArray.isEmpty-impl(`$this$minOrNull_u2dQwZRm1k`)) {
         return null;
      } else {
         var min: Long = ULongArray.get-s-VKNKU(`$this$minOrNull_u2dQwZRm1k`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull_u2dQwZRm1k`)).iterator();

         while (var3.hasNext()) {
            var var7: Long = ULongArray.get-s-VKNKU(`$this$minOrNull_u2dQwZRm1k`, var3.nextInt());
            if (java.lang.Long.compareUnsigned(min, var7) > 0) {
               min = var7;
            }
         }

         return ULong.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.minOrNull(): UByte? {
      if (UByteArray.isEmpty-impl(`$this$minOrNull_u2dGBYM_sE`)) {
         return null;
      } else {
         var min: Byte = UByteArray.get-w2LRezQ(`$this$minOrNull_u2dGBYM_sE`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull_u2dGBYM_sE`)).iterator();

         while (var2.hasNext()) {
            var var5: Byte = UByteArray.get-w2LRezQ(`$this$minOrNull_u2dGBYM_sE`, var2.nextInt());
            if (Intrinsics.compare(min and 255, var5 and 255) > 0) {
               min = var5;
            }
         }

         return UByte.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.minOrNull(): UShort? {
      if (UShortArray.isEmpty-impl(`$this$minOrNull_u2drL5Bavg`)) {
         return null;
      } else {
         var min: Short = UShortArray.get-Mh2AYeg(`$this$minOrNull_u2drL5Bavg`, 0);
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull_u2drL5Bavg`)).iterator();

         while (var2.hasNext()) {
            var var5: Short = UShortArray.get-Mh2AYeg(`$this$minOrNull_u2drL5Bavg`, var2.nextInt());
            if (Intrinsics.compare(min and '\uffff', var5 and '\uffff') > 0) {
               min = var5;
            }
         }

         return UShort.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.minWith(comparator: Comparator<in UInt>): UInt {
      if (UIntArray.isEmpty-impl(`$this$minWith_u2dYmdZ_VM`)) {
         throw new NoSuchElementException();
      } else {
         var min: Int = UIntArray.get-pVg5ArA(`$this$minWith_u2dYmdZ_VM`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith_u2dYmdZ_VM`)).iterator();

         while (var3.hasNext()) {
            var var6: Int = UIntArray.get-pVg5ArA(`$this$minWith_u2dYmdZ_VM`, var3.nextInt());
            if (comparator.compare(UInt.box-impl(min), UInt.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.minWith(comparator: Comparator<in ULong>): ULong {
      if (ULongArray.isEmpty-impl(`$this$minWith_u2dzrEWJaI`)) {
         throw new NoSuchElementException();
      } else {
         var min: Long = ULongArray.get-s-VKNKU(`$this$minWith_u2dzrEWJaI`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith_u2dzrEWJaI`)).iterator();

         while (var4.hasNext()) {
            var var8: Long = ULongArray.get-s-VKNKU(`$this$minWith_u2dzrEWJaI`, var4.nextInt());
            if (comparator.compare(ULong.box-impl(min), ULong.box-impl(var8)) > 0) {
               min = var8;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.minWith(comparator: Comparator<in UByte>): UByte {
      if (UByteArray.isEmpty-impl(`$this$minWith_u2dXMRcp5o`)) {
         throw new NoSuchElementException();
      } else {
         var min: Byte = UByteArray.get-w2LRezQ(`$this$minWith_u2dXMRcp5o`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith_u2dXMRcp5o`)).iterator();

         while (var3.hasNext()) {
            var var6: Byte = UByteArray.get-w2LRezQ(`$this$minWith_u2dXMRcp5o`, var3.nextInt());
            if (comparator.compare(UByte.box-impl(min), UByte.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow-U")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.minWith(comparator: Comparator<in UShort>): UShort {
      if (UShortArray.isEmpty-impl(`$this$minWith_u2deOHTfZs`)) {
         throw new NoSuchElementException();
      } else {
         var min: Short = UShortArray.get-Mh2AYeg(`$this$minWith_u2deOHTfZs`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith_u2deOHTfZs`)).iterator();

         while (var3.hasNext()) {
            var var6: Short = UShortArray.get-Mh2AYeg(`$this$minWith_u2deOHTfZs`, var3.nextInt());
            if (comparator.compare(UShort.box-impl(min), UShort.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.minWithOrNull(comparator: Comparator<in UInt>): UInt? {
      if (UIntArray.isEmpty-impl(`$this$minWithOrNull_u2dYmdZ_VM`)) {
         return null;
      } else {
         var min: Int = UIntArray.get-pVg5ArA(`$this$minWithOrNull_u2dYmdZ_VM`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull_u2dYmdZ_VM`)).iterator();

         while (var3.hasNext()) {
            var var6: Int = UIntArray.get-pVg5ArA(`$this$minWithOrNull_u2dYmdZ_VM`, var3.nextInt());
            if (comparator.compare(UInt.box-impl(min), UInt.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return UInt.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.minWithOrNull(comparator: Comparator<in ULong>): ULong? {
      if (ULongArray.isEmpty-impl(`$this$minWithOrNull_u2dzrEWJaI`)) {
         return null;
      } else {
         var min: Long = ULongArray.get-s-VKNKU(`$this$minWithOrNull_u2dzrEWJaI`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull_u2dzrEWJaI`)).iterator();

         while (var4.hasNext()) {
            var var8: Long = ULongArray.get-s-VKNKU(`$this$minWithOrNull_u2dzrEWJaI`, var4.nextInt());
            if (comparator.compare(ULong.box-impl(min), ULong.box-impl(var8)) > 0) {
               min = var8;
            }
         }

         return ULong.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.minWithOrNull(comparator: Comparator<in UByte>): UByte? {
      if (UByteArray.isEmpty-impl(`$this$minWithOrNull_u2dXMRcp5o`)) {
         return null;
      } else {
         var min: Byte = UByteArray.get-w2LRezQ(`$this$minWithOrNull_u2dXMRcp5o`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull_u2dXMRcp5o`)).iterator();

         while (var3.hasNext()) {
            var var6: Byte = UByteArray.get-w2LRezQ(`$this$minWithOrNull_u2dXMRcp5o`, var3.nextInt());
            if (comparator.compare(UByte.box-impl(min), UByte.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return UByte.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.minWithOrNull(comparator: Comparator<in UShort>): UShort? {
      if (UShortArray.isEmpty-impl(`$this$minWithOrNull_u2deOHTfZs`)) {
         return null;
      } else {
         var min: Short = UShortArray.get-Mh2AYeg(`$this$minWithOrNull_u2deOHTfZs`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull_u2deOHTfZs`)).iterator();

         while (var3.hasNext()) {
            var var6: Short = UShortArray.get-Mh2AYeg(`$this$minWithOrNull_u2deOHTfZs`, var3.nextInt());
            if (comparator.compare(UShort.box-impl(min), UShort.box-impl(var6)) > 0) {
               min = var6;
            }
         }

         return UShort.box-impl(min);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.none(): Boolean {
      return UIntArray.isEmpty-impl(`$this$none_u2d_u2dajY_u2d9A`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.none(): Boolean {
      return ULongArray.isEmpty-impl(`$this$none_u2dQwZRm1k`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.none(): Boolean {
      return UByteArray.isEmpty-impl(`$this$none_u2dGBYM_sE`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.none(): Boolean {
      return UShortArray.isEmpty-impl(`$this$none_u2drL5Bavg`);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.none(predicate: (UInt) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UIntArray.getSize-impl($this$none_u2djgv0xPQ); var2 < var3; var2++) {
         if (predicate.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$none_u2djgv0xPQ`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.none(predicate: (ULong) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = ULongArray.getSize-impl($this$none_u2dMShoTSo); var2 < var3; var2++) {
         if (predicate.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$none_u2dMShoTSo`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.none(predicate: (UByte) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UByteArray.getSize-impl($this$none_u2dJOV_ifY); var2 < var3; var2++) {
         if (predicate.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$none_u2dJOV_ifY`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.none(predicate: (UShort) -> Boolean): Boolean {
      var var2: Int = 0;

      for (int var3 = UShortArray.getSize-impl($this$none_u2dxTcfx_M); var2 < var3; var2++) {
         if (predicate.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$none_u2dxTcfx_M`, var2))) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.onEach(action: (UInt) -> Unit): UIntArray {
      var `$this$onEach_jgv0xPQ_u24lambda_u2456`: IntArray = `$this$onEach_u2djgv0xPQ`;
      var var5: Int = 0;

      for (int var6 = UIntArray.getSize-impl($this$onEach_u2djgv0xPQ); var5 < var6; var5++) {
         action.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$onEach_jgv0xPQ_u24lambda_u2456`, var5)));
      }

      return `$this$onEach_u2djgv0xPQ`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.onEach(action: (ULong) -> Unit): ULongArray {
      var `$this$onEach_MShoTSo_u24lambda_u2457`: LongArray = `$this$onEach_u2dMShoTSo`;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl($this$onEach_u2dMShoTSo); var5 < var6; var5++) {
         action.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$onEach_MShoTSo_u24lambda_u2457`, var5)));
      }

      return `$this$onEach_u2dMShoTSo`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.onEach(action: (UByte) -> Unit): UByteArray {
      var `$this$onEach_JOV_ifY_u24lambda_u2458`: ByteArray = `$this$onEach_u2dJOV_ifY`;
      var var5: Int = 0;

      for (int var6 = UByteArray.getSize-impl($this$onEach_u2dJOV_ifY); var5 < var6; var5++) {
         action.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$onEach_JOV_ifY_u24lambda_u2458`, var5)));
      }

      return `$this$onEach_u2dJOV_ifY`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.onEach(action: (UShort) -> Unit): UShortArray {
      var `$this$onEach_xTcfx_M_u24lambda_u2459`: ShortArray = `$this$onEach_u2dxTcfx_M`;
      var var5: Int = 0;

      for (int var6 = UShortArray.getSize-impl($this$onEach_u2dxTcfx_M); var5 < var6; var5++) {
         action.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$onEach_xTcfx_M_u24lambda_u2459`, var5)));
      }

      return `$this$onEach_u2dxTcfx_M`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.onEachIndexed(action: (Int, UInt) -> Unit): UIntArray {
      var var5: IntArray = `$this$onEachIndexed_u2dWyvcNBI`;
      var var6: Int = 0;
      var var7: Int = 0;

      for (int var8 = UIntArray.getSize-impl($this$onEachIndexed_u2dWyvcNBI); var7 < var8; var7++) {
         action.invoke(var6++, UInt.box-impl(UIntArray.get-pVg5ArA(var5, var7)));
      }

      return `$this$onEachIndexed_u2dWyvcNBI`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.onEachIndexed(action: (Int, ULong) -> Unit): ULongArray {
      var var5: LongArray = `$this$onEachIndexed_u2ds8dVfGU`;
      var var6: Int = 0;
      var var7: Int = 0;

      for (int var8 = ULongArray.getSize-impl($this$onEachIndexed_u2ds8dVfGU); var7 < var8; var7++) {
         action.invoke(var6++, ULong.box-impl(ULongArray.get-s-VKNKU(var5, var7)));
      }

      return `$this$onEachIndexed_u2ds8dVfGU`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.onEachIndexed(action: (Int, UByte) -> Unit): UByteArray {
      var var5: ByteArray = `$this$onEachIndexed_u2dELGow60`;
      var var6: Int = 0;
      var var7: Int = 0;

      for (int var8 = UByteArray.getSize-impl($this$onEachIndexed_u2dELGow60); var7 < var8; var7++) {
         action.invoke(var6++, UByte.box-impl(UByteArray.get-w2LRezQ(var5, var7)));
      }

      return `$this$onEachIndexed_u2dELGow60`;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.onEachIndexed(action: (Int, UShort) -> Unit): UShortArray {
      var var5: ShortArray = `$this$onEachIndexed_u2dxzaTVY8`;
      var var6: Int = 0;
      var var7: Int = 0;

      for (int var8 = UShortArray.getSize-impl($this$onEachIndexed_u2dxzaTVY8); var7 < var8; var7++) {
         action.invoke(var6++, UShort.box-impl(UShortArray.get-Mh2AYeg(var5, var7)));
      }

      return `$this$onEachIndexed_u2dxzaTVY8`;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduce(operation: (UInt, UInt) -> UInt): UInt {
      if (UIntArray.isEmpty-impl(`$this$reduce_u2dWyvcNBI`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduce_u2dWyvcNBI`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce_u2dWyvcNBI`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(UInt.box-impl(accumulator), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduce_u2dWyvcNBI`, var3.nextInt()))) as UInt)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduce(operation: (ULong, ULong) -> ULong): ULong {
      if (ULongArray.isEmpty-impl(`$this$reduce_u2ds8dVfGU`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduce_u2ds8dVfGU`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce_u2ds8dVfGU`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(ULong.box-impl(accumulator), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduce_u2ds8dVfGU`, var4.nextInt()))) as ULong)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduce(operation: (UByte, UByte) -> UByte): UByte {
      if (UByteArray.isEmpty-impl(`$this$reduce_u2dELGow60`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduce_u2dELGow60`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce_u2dELGow60`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(UByte.box-impl(accumulator), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduce_u2dELGow60`, var3.nextInt()))) as UByte)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduce(operation: (UShort, UShort) -> UShort): UShort {
      if (UShortArray.isEmpty-impl(`$this$reduce_u2dxzaTVY8`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduce_u2dxzaTVY8`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce_u2dxzaTVY8`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(UShort.box-impl(accumulator), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduce_u2dxzaTVY8`, var3.nextInt()))) as UShort)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceIndexed(operation: (Int, UInt, UInt) -> UInt): UInt {
      if (UIntArray.isEmpty-impl(`$this$reduceIndexed_u2dD40WMg8`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduceIndexed_u2dD40WMg8`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed_u2dD40WMg8`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(index, UInt.box-impl(accumulator), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceIndexed_u2dD40WMg8`, index))) as UInt)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceIndexed(operation: (Int, ULong, ULong) -> ULong): ULong {
      if (ULongArray.isEmpty-impl(`$this$reduceIndexed_u2dz1zDJgo`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduceIndexed_u2dz1zDJgo`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed_u2dz1zDJgo`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, ULong.box-impl(accumulator), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceIndexed_u2dz1zDJgo`, index))) as ULong)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceIndexed(operation: (Int, UByte, UByte) -> UByte): UByte {
      if (UByteArray.isEmpty-impl(`$this$reduceIndexed_u2dEOyYB1Y`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduceIndexed_u2dEOyYB1Y`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed_u2dEOyYB1Y`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(index, UByte.box-impl(accumulator), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceIndexed_u2dEOyYB1Y`, index))) as UByte)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceIndexed(operation: (Int, UShort, UShort) -> UShort): UShort {
      if (UShortArray.isEmpty-impl(`$this$reduceIndexed_u2daLgx1Fo`)) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduceIndexed_u2daLgx1Fo`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed_u2daLgx1Fo`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(
                  index, UShort.box-impl(accumulator), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceIndexed_u2daLgx1Fo`, index))
               ) as UShort)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceIndexedOrNull(operation: (Int, UInt, UInt) -> UInt): UInt? {
      if (UIntArray.isEmpty-impl(`$this$reduceIndexedOrNull_u2dD40WMg8`)) {
         return null;
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduceIndexedOrNull_u2dD40WMg8`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull_u2dD40WMg8`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(
                  index, UInt.box-impl(accumulator), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceIndexedOrNull_u2dD40WMg8`, index))
               ) as UInt)
               .unbox-impl();
         }

         return UInt.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceIndexedOrNull(operation: (Int, ULong, ULong) -> ULong): ULong? {
      if (ULongArray.isEmpty-impl(`$this$reduceIndexedOrNull_u2dz1zDJgo`)) {
         return null;
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduceIndexedOrNull_u2dz1zDJgo`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull_u2dz1zDJgo`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(
                  index, ULong.box-impl(accumulator), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceIndexedOrNull_u2dz1zDJgo`, index))
               ) as ULong)
               .unbox-impl();
         }

         return ULong.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceIndexedOrNull(operation: (Int, UByte, UByte) -> UByte): UByte? {
      if (UByteArray.isEmpty-impl(`$this$reduceIndexedOrNull_u2dEOyYB1Y`)) {
         return null;
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduceIndexedOrNull_u2dEOyYB1Y`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull_u2dEOyYB1Y`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(
                  index, UByte.box-impl(accumulator), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceIndexedOrNull_u2dEOyYB1Y`, index))
               ) as UByte)
               .unbox-impl();
         }

         return UByte.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceIndexedOrNull(operation: (Int, UShort, UShort) -> UShort): UShort? {
      if (UShortArray.isEmpty-impl(`$this$reduceIndexedOrNull_u2daLgx1Fo`)) {
         return null;
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduceIndexedOrNull_u2daLgx1Fo`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull_u2daLgx1Fo`)).iterator();

         while (var3.hasNext()) {
            var index: Int = var3.nextInt();
            accumulator = (operation.invoke(
                  index, UShort.box-impl(accumulator), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceIndexedOrNull_u2daLgx1Fo`, index))
               ) as UShort)
               .unbox-impl();
         }

         return UShort.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceOrNull(operation: (UInt, UInt) -> UInt): UInt? {
      if (UIntArray.isEmpty-impl(`$this$reduceOrNull_u2dWyvcNBI`)) {
         return null;
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduceOrNull_u2dWyvcNBI`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull_u2dWyvcNBI`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(UInt.box-impl(accumulator), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceOrNull_u2dWyvcNBI`, var3.nextInt()))) as UInt)
               .unbox-impl();
         }

         return UInt.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceOrNull(operation: (ULong, ULong) -> ULong): ULong? {
      if (ULongArray.isEmpty-impl(`$this$reduceOrNull_u2ds8dVfGU`)) {
         return null;
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduceOrNull_u2ds8dVfGU`, 0);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull_u2ds8dVfGU`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(
                  ULong.box-impl(accumulator), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceOrNull_u2ds8dVfGU`, var4.nextInt()))
               ) as ULong)
               .unbox-impl();
         }

         return ULong.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceOrNull(operation: (UByte, UByte) -> UByte): UByte? {
      if (UByteArray.isEmpty-impl(`$this$reduceOrNull_u2dELGow60`)) {
         return null;
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduceOrNull_u2dELGow60`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull_u2dELGow60`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(
                  UByte.box-impl(accumulator), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceOrNull_u2dELGow60`, var3.nextInt()))
               ) as UByte)
               .unbox-impl();
         }

         return UByte.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceOrNull(operation: (UShort, UShort) -> UShort): UShort? {
      if (UShortArray.isEmpty-impl(`$this$reduceOrNull_u2dxzaTVY8`)) {
         return null;
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduceOrNull_u2dxzaTVY8`, 0);
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull_u2dxzaTVY8`)).iterator();

         while (var3.hasNext()) {
            accumulator = (operation.invoke(
                  UShort.box-impl(accumulator), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceOrNull_u2dxzaTVY8`, var3.nextInt()))
               ) as UShort)
               .unbox-impl();
         }

         return UShort.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceRight(operation: (UInt, UInt) -> UInt): UInt {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight_u2dWyvcNBI`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduceRight_u2dWyvcNBI`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceRight_u2dWyvcNBI`, index--)), UInt.box-impl(accumulator)) as UInt)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceRight(operation: (ULong, ULong) -> ULong): ULong {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight_u2ds8dVfGU`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduceRight_u2ds8dVfGU`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceRight_u2ds8dVfGU`, index--)), ULong.box-impl(accumulator)) as ULong)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceRight(operation: (UByte, UByte) -> UByte): UByte {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight_u2dELGow60`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduceRight_u2dELGow60`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceRight_u2dELGow60`, index--)), UByte.box-impl(accumulator)) as UByte)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceRight(operation: (UShort, UShort) -> UShort): UShort {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight_u2dxzaTVY8`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduceRight_u2dxzaTVY8`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceRight_u2dxzaTVY8`, index--)), UShort.box-impl(accumulator)) as UShort)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceRightIndexed(operation: (Int, UInt, UInt) -> UInt): UInt {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed_u2dD40WMg8`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int;
         for (accumulator = UIntArray.get-pVg5ArA($this$reduceRightIndexed_u2dD40WMg8, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceRightIndexed_u2dD40WMg8`, index)), UInt.box-impl(accumulator)
               ) as UInt)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceRightIndexed(operation: (Int, ULong, ULong) -> ULong): ULong {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed_u2dz1zDJgo`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long;
         for (accumulator = ULongArray.get-s-VKNKU($this$reduceRightIndexed_u2dz1zDJgo, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceRightIndexed_u2dz1zDJgo`, index)), ULong.box-impl(accumulator)
               ) as ULong)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceRightIndexed(operation: (Int, UByte, UByte) -> UByte): UByte {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed_u2dEOyYB1Y`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte;
         for (accumulator = UByteArray.get-w2LRezQ($this$reduceRightIndexed_u2dEOyYB1Y, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceRightIndexed_u2dEOyYB1Y`, index)), UByte.box-impl(accumulator)
               ) as UByte)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceRightIndexed(operation: (Int, UShort, UShort) -> UShort): UShort {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed_u2daLgx1Fo`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short;
         for (accumulator = UShortArray.get-Mh2AYeg($this$reduceRightIndexed_u2daLgx1Fo, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceRightIndexed_u2daLgx1Fo`, index)), UShort.box-impl(accumulator)
               ) as UShort)
               .unbox-impl();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceRightIndexedOrNull(operation: (Int, UInt, UInt) -> UInt): UInt? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull_u2dD40WMg8`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Int;
         for (accumulator = UIntArray.get-pVg5ArA($this$reduceRightIndexedOrNull_u2dD40WMg8, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceRightIndexedOrNull_u2dD40WMg8`, index)), UInt.box-impl(accumulator)
               ) as UInt)
               .unbox-impl();
         }

         return UInt.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceRightIndexedOrNull(operation: (Int, ULong, ULong) -> ULong): ULong? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull_u2dz1zDJgo`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Long;
         for (accumulator = ULongArray.get-s-VKNKU($this$reduceRightIndexedOrNull_u2dz1zDJgo, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceRightIndexedOrNull_u2dz1zDJgo`, index)), ULong.box-impl(accumulator)
               ) as ULong)
               .unbox-impl();
         }

         return ULong.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceRightIndexedOrNull(operation: (Int, UByte, UByte) -> UByte): UByte? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull_u2dEOyYB1Y`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Byte;
         for (accumulator = UByteArray.get-w2LRezQ($this$reduceRightIndexedOrNull_u2dEOyYB1Y, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceRightIndexedOrNull_u2dEOyYB1Y`, index)), UByte.box-impl(accumulator)
               ) as UByte)
               .unbox-impl();
         }

         return UByte.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceRightIndexedOrNull(operation: (Int, UShort, UShort) -> UShort): UShort? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull_u2daLgx1Fo`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Short;
         for (accumulator = UShortArray.get-Mh2AYeg($this$reduceRightIndexedOrNull_u2daLgx1Fo, index--); index >= 0; index--) {
            accumulator = (operation.invoke(
                  index, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceRightIndexedOrNull_u2daLgx1Fo`, index)), UShort.box-impl(accumulator)
               ) as UShort)
               .unbox-impl();
         }

         return UShort.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.reduceRightOrNull(operation: (UInt, UInt) -> UInt): UInt? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull_u2dWyvcNBI`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Int = UIntArray.get-pVg5ArA(`$this$reduceRightOrNull_u2dWyvcNBI`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$reduceRightOrNull_u2dWyvcNBI`, index--)), UInt.box-impl(accumulator)) as UInt)
               .unbox-impl();
         }

         return UInt.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.reduceRightOrNull(operation: (ULong, ULong) -> ULong): ULong? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull_u2ds8dVfGU`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Long = ULongArray.get-s-VKNKU(`$this$reduceRightOrNull_u2ds8dVfGU`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$reduceRightOrNull_u2ds8dVfGU`, index--)), ULong.box-impl(accumulator)) as ULong)
               .unbox-impl();
         }

         return ULong.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.reduceRightOrNull(operation: (UByte, UByte) -> UByte): UByte? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull_u2dELGow60`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Byte = UByteArray.get-w2LRezQ(`$this$reduceRightOrNull_u2dELGow60`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$reduceRightOrNull_u2dELGow60`, index--)), UByte.box-impl(accumulator)) as UByte)
               .unbox-impl();
         }

         return UByte.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.reduceRightOrNull(operation: (UShort, UShort) -> UShort): UShort? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull_u2dxzaTVY8`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Short = UShortArray.get-Mh2AYeg(`$this$reduceRightOrNull_u2dxzaTVY8`, index--);

         while (index >= 0) {
            accumulator = (operation.invoke(
                  UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$reduceRightOrNull_u2dxzaTVY8`, index--)), UShort.box-impl(accumulator)
               ) as UShort)
               .unbox-impl();
         }

         return UShort.box-impl(accumulator);
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.runningFold(initial: R, operation: (R, UInt) -> R): List<R> {
      if (UIntArray.isEmpty-impl(`$this$runningFold_u2dzi1B2BA`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$runningFold_u2dzi1B2BA`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;
         var `$this$runningFold_zi1B2BA_u24lambda_u2464`: Int = 0;

         for (int var9 = UIntArray.getSize-impl($this$runningFold_u2dzi1B2BA);
            $this$runningFold_zi1B2BA_u24lambda_u2464 < var9;
            $this$runningFold_zi1B2BA_u24lambda_u2464++
         ) {
            var8 = operation.invoke(var8, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$runningFold_u2dzi1B2BA`, `$this$runningFold_zi1B2BA_u24lambda_u2464`)));
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.runningFold(initial: R, operation: (R, ULong) -> R): List<R> {
      if (ULongArray.isEmpty-impl(`$this$runningFold_u2dA8wKCXQ`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$runningFold_u2dA8wKCXQ`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var9: Any = initial;
         var `$this$runningFold_A8wKCXQ_u24lambda_u2465`: Int = 0;

         for (int var10 = ULongArray.getSize-impl($this$runningFold_u2dA8wKCXQ);
            $this$runningFold_A8wKCXQ_u24lambda_u2465 < var10;
            $this$runningFold_A8wKCXQ_u24lambda_u2465++
         ) {
            var9 = operation.invoke(var9, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$runningFold_u2dA8wKCXQ`, `$this$runningFold_A8wKCXQ_u24lambda_u2465`)));
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.runningFold(initial: R, operation: (R, UByte) -> R): List<R> {
      if (UByteArray.isEmpty-impl(`$this$runningFold_u2dyXmHNn8`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$runningFold_u2dyXmHNn8`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;
         var `$this$runningFold_yXmHNn8_u24lambda_u2466`: Int = 0;

         for (int var9 = UByteArray.getSize-impl($this$runningFold_u2dyXmHNn8);
            $this$runningFold_yXmHNn8_u24lambda_u2466 < var9;
            $this$runningFold_yXmHNn8_u24lambda_u2466++
         ) {
            var8 = operation.invoke(var8, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$runningFold_u2dyXmHNn8`, `$this$runningFold_yXmHNn8_u24lambda_u2466`)));
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.runningFold(initial: R, operation: (R, UShort) -> R): List<R> {
      if (UShortArray.isEmpty-impl(`$this$runningFold_u2dzww5nb8`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$runningFold_u2dzww5nb8`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;
         var `$this$runningFold_zww5nb8_u24lambda_u2467`: Int = 0;

         for (int var9 = UShortArray.getSize-impl($this$runningFold_u2dzww5nb8);
            $this$runningFold_zww5nb8_u24lambda_u2467 < var9;
            $this$runningFold_zww5nb8_u24lambda_u2467++
         ) {
            var8 = operation.invoke(var8, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$runningFold_u2dzww5nb8`, `$this$runningFold_zww5nb8_u24lambda_u2467`)));
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.runningFoldIndexed(initial: R, operation: (Int, R, UInt) -> R): List<R> {
      if (UIntArray.isEmpty-impl(`$this$runningFoldIndexed_u2dyVwIW0Q`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$runningFoldIndexed_u2dyVwIW0Q`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = UIntArray.getSize-impl($this$runningFoldIndexed_u2dyVwIW0Q); index < var8; index++) {
            var7 = operation.invoke(index, var7, UInt.box-impl(UIntArray.get-pVg5ArA(`$this$runningFoldIndexed_u2dyVwIW0Q`, index)));
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.runningFoldIndexed(initial: R, operation: (Int, R, ULong) -> R): List<R> {
      if (ULongArray.isEmpty-impl(`$this$runningFoldIndexed_u2dmwnnOCs`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$runningFoldIndexed_u2dmwnnOCs`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = ULongArray.getSize-impl($this$runningFoldIndexed_u2dmwnnOCs); index < var8; index++) {
            var7 = operation.invoke(index, var7, ULong.box-impl(ULongArray.get-s-VKNKU(`$this$runningFoldIndexed_u2dmwnnOCs`, index)));
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.runningFoldIndexed(initial: R, operation: (Int, R, UByte) -> R): List<R> {
      if (UByteArray.isEmpty-impl(`$this$runningFoldIndexed_u2d3iWJZGE`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$runningFoldIndexed_u2d3iWJZGE`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = UByteArray.getSize-impl($this$runningFoldIndexed_u2d3iWJZGE); index < var8; index++) {
            var7 = operation.invoke(index, var7, UByte.box-impl(UByteArray.get-w2LRezQ(`$this$runningFoldIndexed_u2d3iWJZGE`, index)));
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.runningFoldIndexed(initial: R, operation: (Int, R, UShort) -> R): List<R> {
      if (UShortArray.isEmpty-impl(`$this$runningFoldIndexed_u2dbzxtMww`)) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$runningFoldIndexed_u2dbzxtMww`) + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = UShortArray.getSize-impl($this$runningFoldIndexed_u2dbzxtMww); index < var8; index++) {
            var7 = operation.invoke(index, var7, UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$runningFoldIndexed_u2dbzxtMww`, index)));
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.runningReduce(operation: (UInt, UInt) -> UInt): List<UInt> {
      if (UIntArray.isEmpty-impl(`$this$runningReduce_u2dWyvcNBI`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Int = UIntArray.get-pVg5ArA(`$this$runningReduce_u2dWyvcNBI`, 0);
         var index: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$runningReduce_u2dWyvcNBI`));
         index.add(UInt.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_WyvcNBI_u24lambda_u2472 = UIntArray.getSize-impl($this$runningReduce_u2dWyvcNBI);
            index < $this$runningReduce_WyvcNBI_u24lambda_u2472;
            index++
         ) {
            var7 = (operation.invoke(UInt.box-impl(var7), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$runningReduce_u2dWyvcNBI`, var8))) as UInt).unbox-impl();
            result.add(UInt.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.runningReduce(operation: (ULong, ULong) -> ULong): List<ULong> {
      if (ULongArray.isEmpty-impl(`$this$runningReduce_u2ds8dVfGU`)) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Long = ULongArray.get-s-VKNKU(`$this$runningReduce_u2ds8dVfGU`, 0);
         var index: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$runningReduce_u2ds8dVfGU`));
         index.add(ULong.box-impl(var9));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_s8dVfGU_u24lambda_u2473 = ULongArray.getSize-impl($this$runningReduce_u2ds8dVfGU);
            index < $this$runningReduce_s8dVfGU_u24lambda_u2473;
            index++
         ) {
            var9 = (operation.invoke(ULong.box-impl(var9), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$runningReduce_u2ds8dVfGU`, var8))) as ULong)
               .unbox-impl();
            result.add(ULong.box-impl(var9));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.runningReduce(operation: (UByte, UByte) -> UByte): List<UByte> {
      if (UByteArray.isEmpty-impl(`$this$runningReduce_u2dELGow60`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Byte = UByteArray.get-w2LRezQ(`$this$runningReduce_u2dELGow60`, 0);
         var index: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$runningReduce_u2dELGow60`));
         index.add(UByte.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_ELGow60_u24lambda_u2474 = UByteArray.getSize-impl($this$runningReduce_u2dELGow60);
            index < $this$runningReduce_ELGow60_u24lambda_u2474;
            index++
         ) {
            var7 = (operation.invoke(UByte.box-impl(var7), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$runningReduce_u2dELGow60`, var8))) as UByte)
               .unbox-impl();
            result.add(UByte.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.runningReduce(operation: (UShort, UShort) -> UShort): List<UShort> {
      if (UShortArray.isEmpty-impl(`$this$runningReduce_u2dxzaTVY8`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Short = UShortArray.get-Mh2AYeg(`$this$runningReduce_u2dxzaTVY8`, 0);
         var index: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$runningReduce_u2dxzaTVY8`));
         index.add(UShort.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_xzaTVY8_u24lambda_u2475 = UShortArray.getSize-impl($this$runningReduce_u2dxzaTVY8);
            index < $this$runningReduce_xzaTVY8_u24lambda_u2475;
            index++
         ) {
            var7 = (operation.invoke(UShort.box-impl(var7), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$runningReduce_u2dxzaTVY8`, var8))) as UShort)
               .unbox-impl();
            result.add(UShort.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.runningReduceIndexed(operation: (Int, UInt, UInt) -> UInt): List<UInt> {
      if (UIntArray.isEmpty-impl(`$this$runningReduceIndexed_u2dD40WMg8`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Int = UIntArray.get-pVg5ArA(`$this$runningReduceIndexed_u2dD40WMg8`, 0);
         var index: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$runningReduceIndexed_u2dD40WMg8`));
         index.add(UInt.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_D40WMg8_u24lambda_u2476 = UIntArray.getSize-impl($this$runningReduceIndexed_u2dD40WMg8);
            index < $this$runningReduceIndexed_D40WMg8_u24lambda_u2476;
            index++
         ) {
            var7 = (operation.invoke(var8, UInt.box-impl(var7), UInt.box-impl(UIntArray.get-pVg5ArA(`$this$runningReduceIndexed_u2dD40WMg8`, var8))) as UInt)
               .unbox-impl();
            result.add(UInt.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.runningReduceIndexed(operation: (Int, ULong, ULong) -> ULong): List<ULong> {
      if (ULongArray.isEmpty-impl(`$this$runningReduceIndexed_u2dz1zDJgo`)) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Long = ULongArray.get-s-VKNKU(`$this$runningReduceIndexed_u2dz1zDJgo`, 0);
         var index: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$runningReduceIndexed_u2dz1zDJgo`));
         index.add(ULong.box-impl(var9));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_z1zDJgo_u24lambda_u2477 = ULongArray.getSize-impl($this$runningReduceIndexed_u2dz1zDJgo);
            index < $this$runningReduceIndexed_z1zDJgo_u24lambda_u2477;
            index++
         ) {
            var9 = (operation.invoke(var8, ULong.box-impl(var9), ULong.box-impl(ULongArray.get-s-VKNKU(`$this$runningReduceIndexed_u2dz1zDJgo`, var8))) as ULong)
               .unbox-impl();
            result.add(ULong.box-impl(var9));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.runningReduceIndexed(operation: (Int, UByte, UByte) -> UByte): List<UByte> {
      if (UByteArray.isEmpty-impl(`$this$runningReduceIndexed_u2dEOyYB1Y`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Byte = UByteArray.get-w2LRezQ(`$this$runningReduceIndexed_u2dEOyYB1Y`, 0);
         var index: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$runningReduceIndexed_u2dEOyYB1Y`));
         index.add(UByte.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_EOyYB1Y_u24lambda_u2478 = UByteArray.getSize-impl($this$runningReduceIndexed_u2dEOyYB1Y);
            index < $this$runningReduceIndexed_EOyYB1Y_u24lambda_u2478;
            index++
         ) {
            var7 = (operation.invoke(var8, UByte.box-impl(var7), UByte.box-impl(UByteArray.get-w2LRezQ(`$this$runningReduceIndexed_u2dEOyYB1Y`, var8))) as UByte)
               .unbox-impl();
            result.add(UByte.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.runningReduceIndexed(operation: (Int, UShort, UShort) -> UShort): List<UShort> {
      if (UShortArray.isEmpty-impl(`$this$runningReduceIndexed_u2daLgx1Fo`)) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Short = UShortArray.get-Mh2AYeg(`$this$runningReduceIndexed_u2daLgx1Fo`, 0);
         var index: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$runningReduceIndexed_u2daLgx1Fo`));
         index.add(UShort.box-impl(var7));
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_aLgx1Fo_u24lambda_u2479 = UShortArray.getSize-impl($this$runningReduceIndexed_u2daLgx1Fo);
            index < $this$runningReduceIndexed_aLgx1Fo_u24lambda_u2479;
            index++
         ) {
            var7 = (operation.invoke(var8, UShort.box-impl(var7), UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$runningReduceIndexed_u2daLgx1Fo`, var8))) as UShort)
               .unbox-impl();
            result.add(UShort.box-impl(var7));
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.scan(initial: R, operation: (R, UInt) -> R): List<R> {
      var var3: IntArray = `$this$scan_u2dzi1B2BA`;
      var var10000: java.util.List;
      if (UIntArray.isEmpty-impl(`$this$scan_u2dzi1B2BA`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$scan_u2dzi1B2BA`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;
         var var5: Int = 0;

         for (int var7 = UIntArray.getSize-impl($this$scan_u2dzi1B2BA); var5 < var7; var5++) {
            var9 = operation.invoke(var9, UInt.box-impl(UIntArray.get-pVg5ArA(var3, var5)));
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.scan(initial: R, operation: (R, ULong) -> R): List<R> {
      var var3: LongArray = `$this$scan_u2dA8wKCXQ`;
      var var10000: java.util.List;
      if (ULongArray.isEmpty-impl(`$this$scan_u2dA8wKCXQ`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$scan_u2dA8wKCXQ`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var10: Any = initial;
         var var5: Int = 0;

         for (int var7 = ULongArray.getSize-impl($this$scan_u2dA8wKCXQ); var5 < var7; var5++) {
            var10 = operation.invoke(var10, ULong.box-impl(ULongArray.get-s-VKNKU(var3, var5)));
            var6.add(var10);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.scan(initial: R, operation: (R, UByte) -> R): List<R> {
      var var3: ByteArray = `$this$scan_u2dyXmHNn8`;
      var var10000: java.util.List;
      if (UByteArray.isEmpty-impl(`$this$scan_u2dyXmHNn8`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$scan_u2dyXmHNn8`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;
         var var5: Int = 0;

         for (int var7 = UByteArray.getSize-impl($this$scan_u2dyXmHNn8); var5 < var7; var5++) {
            var9 = operation.invoke(var9, UByte.box-impl(UByteArray.get-w2LRezQ(var3, var5)));
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.scan(initial: R, operation: (R, UShort) -> R): List<R> {
      var var3: ShortArray = `$this$scan_u2dzww5nb8`;
      var var10000: java.util.List;
      if (UShortArray.isEmpty-impl(`$this$scan_u2dzww5nb8`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$scan_u2dzww5nb8`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;
         var var5: Int = 0;

         for (int var7 = UShortArray.getSize-impl($this$scan_u2dzww5nb8); var5 < var7; var5++) {
            var9 = operation.invoke(var9, UShort.box-impl(UShortArray.get-Mh2AYeg(var3, var5)));
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UIntArray.scanIndexed(initial: R, operation: (Int, R, UInt) -> R): List<R> {
      var var3: IntArray = `$this$scanIndexed_u2dyVwIW0Q`;
      var var10000: java.util.List;
      if (UIntArray.isEmpty-impl(`$this$scanIndexed_u2dyVwIW0Q`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UIntArray.getSize-impl(`$this$scanIndexed_u2dyVwIW0Q`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = UIntArray.getSize-impl($this$scanIndexed_u2dyVwIW0Q); var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, UInt.box-impl(UIntArray.get-pVg5ArA(var3, var5)));
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ULongArray.scanIndexed(initial: R, operation: (Int, R, ULong) -> R): List<R> {
      var var3: LongArray = `$this$scanIndexed_u2dmwnnOCs`;
      var var10000: java.util.List;
      if (ULongArray.isEmpty-impl(`$this$scanIndexed_u2dmwnnOCs`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(ULongArray.getSize-impl(`$this$scanIndexed_u2dmwnnOCs`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = ULongArray.getSize-impl($this$scanIndexed_u2dmwnnOCs); var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, ULong.box-impl(ULongArray.get-s-VKNKU(var3, var5)));
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UByteArray.scanIndexed(initial: R, operation: (Int, R, UByte) -> R): List<R> {
      var var3: ByteArray = `$this$scanIndexed_u2d3iWJZGE`;
      var var10000: java.util.List;
      if (UByteArray.isEmpty-impl(`$this$scanIndexed_u2d3iWJZGE`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UByteArray.getSize-impl(`$this$scanIndexed_u2d3iWJZGE`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = UByteArray.getSize-impl($this$scanIndexed_u2d3iWJZGE); var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, UByte.box-impl(UByteArray.get-w2LRezQ(var3, var5)));
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> UShortArray.scanIndexed(initial: R, operation: (Int, R, UShort) -> R): List<R> {
      var var3: ShortArray = `$this$scanIndexed_u2dbzxtMww`;
      var var10000: java.util.List;
      if (UShortArray.isEmpty-impl(`$this$scanIndexed_u2dbzxtMww`)) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(UShortArray.getSize-impl(`$this$scanIndexed_u2dbzxtMww`) + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = UShortArray.getSize-impl($this$scanIndexed_u2dbzxtMww); var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, UShort.box-impl(UShortArray.get-Mh2AYeg(var3, var5)));
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumBy(selector: (UInt) -> UInt): UInt {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$sumBy_u2djgv0xPQ); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumBy_u2djgv0xPQ`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumBy(selector: (ULong) -> UInt): UInt {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$sumBy_u2dMShoTSo); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumBy_u2dMShoTSo`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumBy(selector: (UByte) -> UInt): UInt {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sumBy_u2dJOV_ifY); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumBy_u2dJOV_ifY`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumBy(selector: (UShort) -> UInt): UInt {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sumBy_u2dxTcfx_M); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumBy_u2dxTcfx_M`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumByDouble(selector: (UInt) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$sumByDouble_u2djgv0xPQ); var4 < var5; var4++) {
         sum += (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumByDouble_u2djgv0xPQ`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumByDouble(selector: (ULong) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$sumByDouble_u2dMShoTSo); var4 < var5; var4++) {
         sum += (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumByDouble_u2dMShoTSo`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumByDouble(selector: (UByte) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$sumByDouble_u2dJOV_ifY); var4 < var5; var4++) {
         sum += (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumByDouble_u2dJOV_ifY`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumByDouble(selector: (UShort) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$sumByDouble_u2dxTcfx_M); var4 < var5; var4++) {
         sum += (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumByDouble_u2dxTcfx_M`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var4 < var5; var4++) {
         sum += (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var4 < var5; var4++) {
         sum += (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var4 < var5; var4++) {
         sum += (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> Double): Double {
      var sum: Double = 0.0;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var4 < var5; var4++) {
         sum += (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var4))) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> Int): Int {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var3 < var4; var3++) {
         sum += (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var3))) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> Int): Int {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var3 < var4; var3++) {
         sum += (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var3))) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> Int): Int {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var3 < var4; var3++) {
         sum += (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var3))) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> Int): Int {
      var sum: Int = 0;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var3 < var4; var3++) {
         sum += (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var3))) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> Long): Long {
      var sum: Long = 0L;
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var4 < var5; var4++) {
         sum += (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var4))) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> Long): Long {
      var sum: Long = 0L;
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var4 < var5; var4++) {
         sum += (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var4))) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> Long): Long {
      var sum: Long = 0L;
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var4 < var5; var4++) {
         sum += (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var4))) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> Long): Long {
      var sum: Long = 0L;
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var4 < var5; var4++) {
         sum += (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var4))) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var3 < var4; var3++) {
         sum = UInt.constructor-impl(sum + (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var3))) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);
      var var4: Int = 0;

      for (int var5 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var4 < var5; var4++) {
         sum = ULong.constructor-impl(sum + (selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var4))) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);
      var var4: Int = 0;

      for (int var5 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var4 < var5; var4++) {
         sum = ULong.constructor-impl(sum + (selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var4))) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);
      var var4: Int = 0;

      for (int var5 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var4 < var5; var4++) {
         sum = ULong.constructor-impl(sum + (selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var4))) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @ExperimentalUnsignedTypes
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);
      var var4: Int = 0;

      for (int var5 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var4 < var5; var4++) {
         sum = ULong.constructor-impl(sum + (selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var4))) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UIntArray.zip(other: Array<out R>): List<Pair<UInt, R>> {
      var var2: IntArray = `$this$zip_u2dC_u2dE_24M`;
      var var3: Int = Math.min(UIntArray.getSize-impl(`$this$zip_u2dC_u2dE_24M`), other.length);
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UInt.box-impl(UIntArray.get-pVg5ArA(var2, var5)), other[var5]));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> ULongArray.zip(other: Array<out R>): List<Pair<ULong, R>> {
      var var2: LongArray = `$this$zip_u2df7H3mmw`;
      var var3: Int = Math.min(ULongArray.getSize-impl(`$this$zip_u2df7H3mmw`), other.length);
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(ULong.box-impl(ULongArray.get-s-VKNKU(var2, var5)), other[var5]));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UByteArray.zip(other: Array<out R>): List<Pair<UByte, R>> {
      var var2: ByteArray = `$this$zip_u2dnl983wc`;
      var var3: Int = Math.min(UByteArray.getSize-impl(`$this$zip_u2dnl983wc`), other.length);
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UByte.box-impl(UByteArray.get-w2LRezQ(var2, var5)), other[var5]));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UShortArray.zip(other: Array<out R>): List<Pair<UShort, R>> {
      var var2: ShortArray = `$this$zip_u2duaTIQ5s`;
      var var3: Int = Math.min(UShortArray.getSize-impl(`$this$zip_u2duaTIQ5s`), other.length);
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var5)), other[var5]));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UIntArray.zip(other: Array<out R>, transform: (UInt, R) -> V): List<V> {
      var size: Int = Math.min(UIntArray.getSize-impl(`$this$zip_u2dZjwqOic`), other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$zip_u2dZjwqOic`, i)), other[i]));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> ULongArray.zip(other: Array<out R>, transform: (ULong, R) -> V): List<V> {
      var size: Int = Math.min(ULongArray.getSize-impl(`$this$zip_u2d8LME4QE`), other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$zip_u2d8LME4QE`, i)), other[i]));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UByteArray.zip(other: Array<out R>, transform: (UByte, R) -> V): List<V> {
      var size: Int = Math.min(UByteArray.getSize-impl(`$this$zip_u2dLuipOMY`), other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$zip_u2dLuipOMY`, i)), other[i]));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UShortArray.zip(other: Array<out R>, transform: (UShort, R) -> V): List<V> {
      var size: Int = Math.min(UShortArray.getSize-impl(`$this$zip_u2dePBmRWY`), other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$zip_u2dePBmRWY`, i)), other[i]));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UIntArray.zip(other: Iterable<R>): List<Pair<UInt, R>> {
      var var2: IntArray = `$this$zip_u2dHwE9HBo`;
      var var3: Int = UIntArray.getSize-impl(`$this$zip_u2dHwE9HBo`);
      var var4: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), var3));
      var var5: Int = 0;

      for (Object var7 : other) {
         if (var5 >= var3) {
            break;
         }

         var4.add(TuplesKt.to(UInt.box-impl(UIntArray.get-pVg5ArA(var2, var5++)), var7));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> ULongArray.zip(other: Iterable<R>): List<Pair<ULong, R>> {
      var var2: LongArray = `$this$zip_u2dF7u83W8`;
      var var3: Int = ULongArray.getSize-impl(`$this$zip_u2dF7u83W8`);
      var var4: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), var3));
      var var5: Int = 0;

      for (Object var7 : other) {
         if (var5 >= var3) {
            break;
         }

         var4.add(TuplesKt.to(ULong.box-impl(ULongArray.get-s-VKNKU(var2, var5++)), var7));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UByteArray.zip(other: Iterable<R>): List<Pair<UByte, R>> {
      var var2: ByteArray = `$this$zip_u2dJQknh5Q`;
      var var3: Int = UByteArray.getSize-impl(`$this$zip_u2dJQknh5Q`);
      var var4: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), var3));
      var var5: Int = 0;

      for (Object var7 : other) {
         if (var5 >= var3) {
            break;
         }

         var4.add(TuplesKt.to(UByte.box-impl(UByteArray.get-w2LRezQ(var2, var5++)), var7));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun <R> UShortArray.zip(other: Iterable<R>): List<Pair<UShort, R>> {
      var var2: ShortArray = `$this$zip_u2dJGPC0_u2dM`;
      var var3: Int = UShortArray.getSize-impl(`$this$zip_u2dJGPC0_u2dM`);
      var var4: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), var3));
      var var5: Int = 0;

      for (Object var7 : other) {
         if (var5 >= var3) {
            break;
         }

         var4.add(TuplesKt.to(UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var5++)), var7));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UIntArray.zip(other: Iterable<R>, transform: (UInt, R) -> V): List<V> {
      var arraySize: Int = UIntArray.getSize-impl(`$this$zip_u2d7znnbtw`);
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), arraySize));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$zip_u2d7znnbtw`, i++)), element));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> ULongArray.zip(other: Iterable<R>, transform: (ULong, R) -> V): List<V> {
      var arraySize: Int = ULongArray.getSize-impl(`$this$zip_u2dTUPTUsU`);
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), arraySize));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$zip_u2dTUPTUsU`, i++)), element));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UByteArray.zip(other: Iterable<R>, transform: (UByte, R) -> V): List<V> {
      var arraySize: Int = UByteArray.getSize-impl(`$this$zip_u2dUCnP4_w`);
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), arraySize));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$zip_u2dUCnP4_w`, i++)), element));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <R, V> UShortArray.zip(other: Iterable<R>, transform: (UShort, R) -> V): List<V> {
      var arraySize: Int = UShortArray.getSize-impl(`$this$zip_u2dkBb4a_u2ds`);
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), arraySize));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$zip_u2dkBb4a_u2ds`, i++)), element));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UIntArray.zip(other: UIntArray): List<Pair<UInt, UInt>> {
      var var2: IntArray = `$this$zip_u2dctEhBpI`;
      var var3: Int = Math.min(UIntArray.getSize-impl(`$this$zip_u2dctEhBpI`), UIntArray.getSize-impl(other));
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UInt.box-impl(UIntArray.get-pVg5ArA(var2, var5)), UInt.box-impl(UIntArray.get-pVg5ArA(other, var5))));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun ULongArray.zip(other: ULongArray): List<Pair<ULong, ULong>> {
      var var2: LongArray = `$this$zip_u2dus8wMrg`;
      var var3: Int = Math.min(ULongArray.getSize-impl(`$this$zip_u2dus8wMrg`), ULongArray.getSize-impl(other));
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(ULong.box-impl(ULongArray.get-s-VKNKU(var2, var5)), ULong.box-impl(ULongArray.get-s-VKNKU(other, var5))));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UByteArray.zip(other: UByteArray): List<Pair<UByte, UByte>> {
      var var2: ByteArray = `$this$zip_u2dkdPth3s`;
      var var3: Int = Math.min(UByteArray.getSize-impl(`$this$zip_u2dkdPth3s`), UByteArray.getSize-impl(other));
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UByte.box-impl(UByteArray.get-w2LRezQ(var2, var5)), UByte.box-impl(UByteArray.get-w2LRezQ(other, var5))));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public infix fun UShortArray.zip(other: UShortArray): List<Pair<UShort, UShort>> {
      var var2: ShortArray = `$this$zip_u2dmazbYpA`;
      var var3: Int = Math.min(UShortArray.getSize-impl(`$this$zip_u2dmazbYpA`), UShortArray.getSize-impl(other));
      var var4: ArrayList = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(TuplesKt.to(UShort.box-impl(UShortArray.get-Mh2AYeg(var2, var5)), UShort.box-impl(UShortArray.get-Mh2AYeg(other, var5))));
      }

      return var4;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UIntArray.zip(other: UIntArray, transform: (UInt, UInt) -> V): List<V> {
      var size: Int = Math.min(UIntArray.getSize-impl(`$this$zip_u2dL83TJbI`), UIntArray.getSize-impl(other));
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$zip_u2dL83TJbI`, i)), UInt.box-impl(UIntArray.get-pVg5ArA(other, i))));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> ULongArray.zip(other: ULongArray, transform: (ULong, ULong) -> V): List<V> {
      var size: Int = Math.min(ULongArray.getSize-impl(`$this$zip_u2dPabeH_u2dQ`), ULongArray.getSize-impl(other));
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$zip_u2dPabeH_u2dQ`, i)), ULong.box-impl(ULongArray.get-s-VKNKU(other, i))));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UByteArray.zip(other: UByteArray, transform: (UByte, UByte) -> V): List<V> {
      var size: Int = Math.min(UByteArray.getSize-impl(`$this$zip_u2dJAKpvQM`), UByteArray.getSize-impl(other));
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$zip_u2dJAKpvQM`, i)), UByte.box-impl(UByteArray.get-w2LRezQ(other, i))));
      }

      return list;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun <V> UShortArray.zip(other: UShortArray, transform: (UShort, UShort) -> V): List<V> {
      var size: Int = Math.min(UShortArray.getSize-impl(`$this$zip_u2dgVVukQo`), UShortArray.getSize-impl(other));
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$zip_u2dgVVukQo`, i)), UShort.box-impl(UShortArray.get-Mh2AYeg(other, i))));
      }

      return list;
   }

   @JvmName(name = "sumOfUInt")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Array<out UInt>.sum(): UInt {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum = UInt.constructor-impl(sum + `$this$sum`[var2].unbox-impl());
      }

      return sum;
   }

   @JvmName(name = "sumOfULong")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Array<out ULong>.sum(): ULong {
      var sum: Long = 0L;
      var var3: Int = 0;

      for (int var4 = $this$sum.length; var3 < var4; var3++) {
         sum = ULong.constructor-impl(sum + `$this$sum`[var3].unbox-impl());
      }

      return sum;
   }

   @JvmName(name = "sumOfUByte")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Array<out UByte>.sum(): UInt {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum = UInt.constructor-impl(sum + UInt.constructor-impl(`$this$sum`[var2].unbox-impl() and 255));
      }

      return sum;
   }

   @JvmName(name = "sumOfUShort")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Array<out UShort>.sum(): UInt {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum = UInt.constructor-impl(sum + UInt.constructor-impl(`$this$sum`[var2].unbox-impl() and '\uffff'));
      }

      return sum;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sum(): UInt {
      return UInt.constructor-impl(ArraysKt.sum(`$this$sum_u2d_u2dajY_u2d9A`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sum(): ULong {
      return ULong.constructor-impl(ArraysKt.sum(`$this$sum_u2dQwZRm1k`));
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sum(): UInt {
      var var1: ByteArray = `$this$sum_u2dGBYM_sE`;
      var var2: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sum_u2dGBYM_sE); var3 < var4; var3++) {
         var2 = UInt.constructor-impl(var2 + UInt.constructor-impl(UByteArray.get-w2LRezQ(var1, var3) and 255));
      }

      return var2;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sum(): UInt {
      var var1: ShortArray = `$this$sum_u2drL5Bavg`;
      var var2: Int = UInt.constructor-impl(0);
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sum_u2drL5Bavg); var3 < var4; var3++) {
         var2 = UInt.constructor-impl(var2 + UInt.constructor-impl(UShortArray.get-Mh2AYeg(var1, var3) and '\uffff'));
      }

      return var2;
   }

   open fun UArraysKt___UArraysKt() {
   }
}
