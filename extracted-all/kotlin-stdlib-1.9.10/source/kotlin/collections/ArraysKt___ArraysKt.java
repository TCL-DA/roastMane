package kotlin.collections

import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.ArrayIteratorKt
import kotlin.jvm.internal.ArrayIteratorsKt
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\n_Arrays.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,24423:1\n12524#1,2:24424\n12534#1,2:24426\n1282#1,2:24428\n1290#1,2:24430\n1298#1,2:24432\n1306#1,2:24434\n1314#1,2:24436\n1322#1,2:24438\n1330#1,2:24440\n1338#1,2:24442\n1346#1,2:24444\n2298#1,5:24446\n2311#1,5:24451\n2324#1,5:24456\n2337#1,5:24461\n2350#1,5:24466\n2363#1,5:24471\n2376#1,5:24476\n2389#1,5:24481\n2402#1,5:24486\n4307#1,2:24492\n4317#1,2:24494\n4327#1,2:24496\n4337#1,2:24498\n4347#1,2:24500\n4357#1,2:24502\n4367#1,2:24504\n4377#1,2:24506\n4387#1,2:24508\n3974#1:24510\n13374#1,2:24511\n3975#1,2:24513\n13376#1:24515\n3977#1:24516\n3988#1:24517\n13384#1,2:24518\n3989#1,2:24520\n13386#1:24522\n3991#1:24523\n4002#1:24524\n13394#1,2:24525\n4003#1,2:24527\n13396#1:24529\n4005#1:24530\n4016#1:24531\n13404#1,2:24532\n4017#1,2:24534\n13406#1:24536\n4019#1:24537\n4030#1:24538\n13414#1,2:24539\n4031#1,2:24541\n13416#1:24543\n4033#1:24544\n4044#1:24545\n13424#1,2:24546\n4045#1,2:24548\n13426#1:24550\n4047#1:24551\n4058#1:24552\n13434#1,2:24553\n4059#1,2:24555\n13436#1:24557\n4061#1:24558\n4072#1:24559\n13444#1,2:24560\n4073#1,2:24562\n13446#1:24564\n4075#1:24565\n4086#1:24566\n13454#1,2:24567\n4087#1,2:24569\n13456#1:24571\n4089#1:24572\n13374#1,3:24573\n13384#1,3:24576\n13394#1,3:24579\n13404#1,3:24582\n13414#1,3:24585\n13424#1,3:24588\n13434#1,3:24591\n13444#1,3:24594\n13454#1,3:24597\n4107#1,2:24600\n4217#1,2:24602\n4227#1,2:24604\n4237#1,2:24606\n4247#1,2:24608\n4257#1,2:24610\n4267#1,2:24612\n4277#1,2:24614\n4287#1,2:24616\n4297#1,2:24618\n9088#1,4:24620\n9103#1,4:24624\n9118#1,4:24628\n9133#1,4:24632\n9148#1,4:24636\n9163#1,4:24640\n9178#1,4:24644\n9193#1,4:24648\n9208#1,4:24652\n8801#1,4:24656\n8817#1,4:24660\n8833#1,4:24664\n8849#1,4:24668\n8865#1,4:24672\n8881#1,4:24676\n8897#1,4:24680\n8913#1,4:24684\n8929#1,4:24688\n8945#1,4:24692\n8961#1,4:24696\n8977#1,4:24700\n8993#1,4:24704\n9009#1,4:24708\n9025#1,4:24712\n9041#1,4:24716\n9057#1,4:24720\n9073#1,4:24724\n9376#1,4:24728\n10394#1,5:24732\n10405#1,5:24737\n10416#1,5:24742\n10427#1,5:24747\n10438#1,5:24752\n10449#1,5:24757\n10460#1,5:24762\n10471#1,5:24767\n10482#1,5:24772\n10497#1,5:24777\n10738#1,3:24782\n10741#1,3:24792\n10755#1,3:24795\n10758#1,3:24805\n10772#1,3:24808\n10775#1,3:24818\n10789#1,3:24821\n10792#1,3:24831\n10806#1,3:24834\n10809#1,3:24844\n10823#1,3:24847\n10826#1,3:24857\n10840#1,3:24860\n10843#1,3:24870\n10857#1,3:24873\n10860#1,3:24883\n10874#1,3:24886\n10877#1,3:24896\n10892#1,3:24899\n10895#1,3:24909\n10910#1,3:24912\n10913#1,3:24922\n10928#1,3:24925\n10931#1,3:24935\n10946#1,3:24938\n10949#1,3:24948\n10964#1,3:24951\n10967#1,3:24961\n10982#1,3:24964\n10985#1,3:24974\n11000#1,3:24977\n11003#1,3:24987\n11018#1,3:24990\n11021#1,3:25000\n11036#1,3:25003\n11039#1,3:25013\n11400#1,3:25142\n11410#1,3:25145\n11420#1,3:25148\n11430#1,3:25151\n11440#1,3:25154\n11450#1,3:25157\n11460#1,3:25160\n11470#1,3:25163\n11480#1,3:25166\n11266#1,4:25169\n11279#1,4:25173\n11292#1,4:25177\n11305#1,4:25181\n11318#1,4:25185\n11331#1,4:25189\n11344#1,4:25193\n11357#1,4:25197\n11370#1,4:25201\n11255#1:25205\n13374#1,2:25206\n13376#1:25209\n11256#1:25210\n13374#1,3:25211\n11391#1:25214\n13309#1:25215\n13310#1:25217\n11392#1:25218\n13309#1,2:25219\n13374#1,3:25221\n13384#1,3:25224\n13394#1,3:25227\n13404#1,3:25230\n13414#1,3:25233\n13424#1,3:25236\n13434#1,3:25239\n13444#1,3:25242\n13454#1,3:25245\n20623#1,2:25248\n20625#1,6:25251\n20839#1,2:25257\n20841#1,6:25260\n23035#1,6:25266\n23051#1,6:25272\n23067#1,6:25278\n23083#1,6:25284\n23099#1,6:25290\n23115#1,6:25296\n23131#1,6:25302\n23147#1,6:25308\n23163#1,6:25314\n23269#1,8:25320\n23287#1,8:25328\n23305#1,8:25336\n23323#1,8:25344\n23341#1,8:25352\n23359#1,8:25360\n23377#1,8:25368\n23395#1,8:25376\n23413#1,8:25384\n23511#1,6:25392\n23527#1,6:25398\n23543#1,6:25404\n23559#1,6:25410\n23575#1,6:25416\n23591#1,6:25422\n23607#1,6:25428\n23623#1,6:25434\n1#2:24491\n1#2:25208\n1#2:25216\n1#2:25250\n1#2:25259\n372#3,7:24785\n372#3,7:24798\n372#3,7:24811\n372#3,7:24824\n372#3,7:24837\n372#3,7:24850\n372#3,7:24863\n372#3,7:24876\n372#3,7:24889\n372#3,7:24902\n372#3,7:24915\n372#3,7:24928\n372#3,7:24941\n372#3,7:24954\n372#3,7:24967\n372#3,7:24980\n372#3,7:24993\n372#3,7:25006\n372#3,7:25016\n372#3,7:25023\n372#3,7:25030\n372#3,7:25037\n372#3,7:25044\n372#3,7:25051\n372#3,7:25058\n372#3,7:25065\n372#3,7:25072\n372#3,7:25079\n372#3,7:25086\n372#3,7:25093\n372#3,7:25100\n372#3,7:25107\n372#3,7:25114\n372#3,7:25121\n372#3,7:25128\n372#3,7:25135\n*S KotlinDebug\n*F\n+ 1 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n556#1:24424,2\n565#1:24426,2\n832#1:24428,2\n842#1:24430,2\n852#1:24432,2\n862#1:24434,2\n872#1:24436,2\n882#1:24438,2\n892#1:24440,2\n902#1:24442,2\n912#1:24444,2\n922#1:24446,5\n932#1:24451,5\n942#1:24456,5\n952#1:24461,5\n962#1:24466,5\n972#1:24471,5\n982#1:24476,5\n992#1:24481,5\n1002#1:24486,5\n3792#1:24492,2\n3801#1:24494,2\n3810#1:24496,2\n3819#1:24498,2\n3828#1:24500,2\n3837#1:24502,2\n3846#1:24504,2\n3855#1:24506,2\n3864#1:24508,2\n3875#1:24510\n3875#1:24511,2\n3875#1:24513,2\n3875#1:24515\n3875#1:24516\n3886#1:24517\n3886#1:24518,2\n3886#1:24520,2\n3886#1:24522\n3886#1:24523\n3897#1:24524\n3897#1:24525,2\n3897#1:24527,2\n3897#1:24529\n3897#1:24530\n3908#1:24531\n3908#1:24532,2\n3908#1:24534,2\n3908#1:24536\n3908#1:24537\n3919#1:24538\n3919#1:24539,2\n3919#1:24541,2\n3919#1:24543\n3919#1:24544\n3930#1:24545\n3930#1:24546,2\n3930#1:24548,2\n3930#1:24550\n3930#1:24551\n3941#1:24552\n3941#1:24553,2\n3941#1:24555,2\n3941#1:24557\n3941#1:24558\n3952#1:24559\n3952#1:24560,2\n3952#1:24562,2\n3952#1:24564\n3952#1:24565\n3963#1:24566\n3963#1:24567,2\n3963#1:24569,2\n3963#1:24571\n3963#1:24572\n3974#1:24573,3\n3988#1:24576,3\n4002#1:24579,3\n4016#1:24582,3\n4030#1:24585,3\n4044#1:24588,3\n4058#1:24591,3\n4072#1:24594,3\n4086#1:24597,3\n4098#1:24600,2\n4117#1:24602,2\n4126#1:24604,2\n4135#1:24606,2\n4144#1:24608,2\n4153#1:24610,2\n4162#1:24612,2\n4171#1:24614,2\n4180#1:24616,2\n4189#1:24618,2\n8407#1:24620,4\n8422#1:24624,4\n8437#1:24628,4\n8452#1:24632,4\n8467#1:24636,4\n8482#1:24640,4\n8497#1:24644,4\n8512#1:24648,4\n8527#1:24652,4\n8542#1:24656,4\n8557#1:24660,4\n8572#1:24664,4\n8587#1:24668,4\n8602#1:24672,4\n8617#1:24676,4\n8632#1:24680,4\n8647#1:24684,4\n8662#1:24688,4\n8676#1:24692,4\n8690#1:24696,4\n8704#1:24700,4\n8718#1:24704,4\n8732#1:24708,4\n8746#1:24712,4\n8760#1:24716,4\n8774#1:24720,4\n8788#1:24724,4\n9227#1:24728,4\n9972#1:24732,5\n9981#1:24737,5\n9990#1:24742,5\n9999#1:24747,5\n10008#1:24752,5\n10017#1:24757,5\n10026#1:24762,5\n10035#1:24767,5\n10044#1:24772,5\n10057#1:24777,5\n10513#1:24782,3\n10513#1:24792,3\n10525#1:24795,3\n10525#1:24805,3\n10537#1:24808,3\n10537#1:24818,3\n10549#1:24821,3\n10549#1:24831,3\n10561#1:24834,3\n10561#1:24844,3\n10573#1:24847,3\n10573#1:24857,3\n10585#1:24860,3\n10585#1:24870,3\n10597#1:24873,3\n10597#1:24883,3\n10609#1:24886,3\n10609#1:24896,3\n10622#1:24899,3\n10622#1:24909,3\n10635#1:24912,3\n10635#1:24922,3\n10648#1:24925,3\n10648#1:24935,3\n10661#1:24938,3\n10661#1:24948,3\n10674#1:24951,3\n10674#1:24961,3\n10687#1:24964,3\n10687#1:24974,3\n10700#1:24977,3\n10700#1:24987,3\n10713#1:24990,3\n10713#1:25000,3\n10726#1:25003,3\n10726#1:25013,3\n11065#1:25142,3\n11075#1:25145,3\n11085#1:25148,3\n11095#1:25151,3\n11105#1:25154,3\n11115#1:25157,3\n11125#1:25160,3\n11135#1:25163,3\n11145#1:25166,3\n11155#1:25169,4\n11165#1:25173,4\n11175#1:25177,4\n11185#1:25181,4\n11195#1:25185,4\n11205#1:25189,4\n11215#1:25193,4\n11225#1:25197,4\n11235#1:25201,4\n11245#1:25205\n11245#1:25206,2\n11245#1:25209\n11245#1:25210\n11255#1:25211,3\n11383#1:25214\n11383#1:25215\n11383#1:25217\n11383#1:25218\n11391#1:25219,2\n18891#1:25221,3\n18903#1:25224,3\n18915#1:25227,3\n18927#1:25230,3\n18939#1:25233,3\n18951#1:25236,3\n18963#1:25239,3\n18975#1:25242,3\n18987#1:25245,3\n21455#1:25248,2\n21455#1:25251,6\n21617#1:25257,2\n21617#1:25260,6\n22944#1:25266,6\n22954#1:25272,6\n22964#1:25278,6\n22974#1:25284,6\n22984#1:25290,6\n22994#1:25296,6\n23004#1:25302,6\n23014#1:25308,6\n23024#1:25314,6\n23178#1:25320,8\n23188#1:25328,8\n23198#1:25336,8\n23208#1:25344,8\n23218#1:25352,8\n23228#1:25360,8\n23238#1:25368,8\n23248#1:25376,8\n23258#1:25384,8\n23430#1:25392,6\n23440#1:25398,6\n23450#1:25404,6\n23460#1:25410,6\n23470#1:25416,6\n23480#1:25422,6\n23490#1:25428,6\n23500#1:25434,6\n11245#1:25208\n11383#1:25216\n21455#1:25250\n21617#1:25259\n10513#1:24785,7\n10525#1:24798,7\n10537#1:24811,7\n10549#1:24824,7\n10561#1:24837,7\n10573#1:24850,7\n10585#1:24863,7\n10597#1:24876,7\n10609#1:24889,7\n10622#1:24902,7\n10635#1:24915,7\n10648#1:24928,7\n10661#1:24941,7\n10674#1:24954,7\n10687#1:24967,7\n10700#1:24980,7\n10713#1:24993,7\n10726#1:25006,7\n10740#1:25016,7\n10757#1:25023,7\n10774#1:25030,7\n10791#1:25037,7\n10808#1:25044,7\n10825#1:25051,7\n10842#1:25058,7\n10859#1:25065,7\n10876#1:25072,7\n10894#1:25079,7\n10912#1:25086,7\n10930#1:25093,7\n10948#1:25100,7\n10966#1:25107,7\n10984#1:25114,7\n11002#1:25121,7\n11020#1:25128,7\n11038#1:25135,7\n*E\n"])
// $VF: Class flags could not be determined
internal class ArraysKt___ArraysKt : ArraysKt___ArraysJvmKt {
   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val indices: IntRange
      public final get() {
         return new IntRange(0, ArraysKt.getLastIndex(`$this$indices`));
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }

   public final val lastIndex: Int
      public final get() {
         return `$this$lastIndex`.length - 1;
      }


   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Array<out T>.component1(): T {
      return (T)`$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ByteArray.component1(): Byte {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ShortArray.component1(): Short {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntArray.component1(): Int {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongArray.component1(): Long {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun FloatArray.component1(): Float {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun DoubleArray.component1(): Double {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BooleanArray.component1(): Boolean {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharArray.component1(): Char {
      return `$this$component1`[0];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Array<out T>.component2(): T {
      return (T)`$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ByteArray.component2(): Byte {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ShortArray.component2(): Short {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntArray.component2(): Int {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongArray.component2(): Long {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun FloatArray.component2(): Float {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun DoubleArray.component2(): Double {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BooleanArray.component2(): Boolean {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharArray.component2(): Char {
      return `$this$component2`[1];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Array<out T>.component3(): T {
      return (T)`$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ByteArray.component3(): Byte {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ShortArray.component3(): Short {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntArray.component3(): Int {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongArray.component3(): Long {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun FloatArray.component3(): Float {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun DoubleArray.component3(): Double {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BooleanArray.component3(): Boolean {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharArray.component3(): Char {
      return `$this$component3`[2];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Array<out T>.component4(): T {
      return (T)`$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ByteArray.component4(): Byte {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ShortArray.component4(): Short {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntArray.component4(): Int {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongArray.component4(): Long {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun FloatArray.component4(): Float {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun DoubleArray.component4(): Double {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BooleanArray.component4(): Boolean {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharArray.component4(): Char {
      return `$this$component4`[3];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Array<out T>.component5(): T {
      return (T)`$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ByteArray.component5(): Byte {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun ShortArray.component5(): Short {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntArray.component5(): Int {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongArray.component5(): Long {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun FloatArray.component5(): Float {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun DoubleArray.component5(): Double {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BooleanArray.component5(): Boolean {
      return `$this$component5`[4];
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun CharArray.component5(): Char {
      return `$this$component5`[4];
   }

   @JvmStatic
   public operator fun <T> Array<out T>.contains(element: T): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun ByteArray.contains(element: Byte): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun ShortArray.contains(element: Short): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun IntArray.contains(element: Int): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun LongArray.contains(element: Long): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun BooleanArray.contains(element: Boolean): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public operator fun CharArray.contains(element: Char): Boolean {
      return ArraysKt.indexOf(`$this$contains`, element) >= 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
      return (T)(if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`)) `$this$elementAtOrElse`[index] else defaultValue.invoke(index));
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Byte): Byte {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).byteValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Short): Short {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).shortValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Int): Int {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).intValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Long): Long {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).longValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Float): Float {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).floatValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Double): Double {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).doubleValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Boolean): Boolean {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         defaultValue.invoke(index) as java.lang.Boolean;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.elementAtOrElse(index: Int, defaultValue: (Int) -> Char): Char {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`[index]
         else
         defaultValue.invoke(index) as Character;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.elementAtOrNull(index: Int): T? {
      return (T)ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.elementAtOrNull(index: Int): Byte? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.elementAtOrNull(index: Int): Short? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.elementAtOrNull(index: Int): Int? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.elementAtOrNull(index: Int): Long? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.elementAtOrNull(index: Int): Float? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.elementAtOrNull(index: Int): Double? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.elementAtOrNull(index: Int): Boolean? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.elementAtOrNull(index: Int): Char? {
      return ArraysKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.find(predicate: (T) -> Boolean): T? {
      var `$this$firstOrNull$iv`: Array<Any> = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: Any;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Any = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return (T)var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.find(predicate: (Byte) -> Boolean): Byte? {
      var `$this$firstOrNull$iv`: ByteArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Byte;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Byte = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.find(predicate: (Short) -> Boolean): Short? {
      var `$this$firstOrNull$iv`: ShortArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Short;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Short = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.find(predicate: (Int) -> Boolean): Int? {
      var `$this$firstOrNull$iv`: IntArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: Int;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Int = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.find(predicate: (Long) -> Boolean): Long? {
      var `$this$firstOrNull$iv`: LongArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Long;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Long = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.find(predicate: (Float) -> Boolean): Float? {
      var `$this$firstOrNull$iv`: FloatArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Float;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Float = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.find(predicate: (Double) -> Boolean): Double? {
      var `$this$firstOrNull$iv`: DoubleArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Double;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Double = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.find(predicate: (Boolean) -> Boolean): Boolean? {
      var `$this$firstOrNull$iv`: BooleanArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: java.lang.Boolean;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Boolean = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.find(predicate: (Char) -> Boolean): Char? {
      var `$this$firstOrNull$iv`: CharArray = `$this$find`;
      var var4: Int = 0;
      var var5: Int = `$this$find`.length;

      var var10000: Character;
      while (true) {
         if (var4 >= var5) {
            var10000 = null;
            break;
         }

         var `element$iv`: Char = `$this$firstOrNull$iv`[var4];
         if (predicate.invoke(`$this$firstOrNull$iv`[var4]) as java.lang.Boolean) {
            var10000 = `element$iv`;
            break;
         }

         var4++;
      }

      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.findLast(predicate: (T) -> Boolean): T? {
      var `$this$lastOrNull$iv`: Array<Any> = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Any = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return (T)`element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.findLast(predicate: (Byte) -> Boolean): Byte? {
      var `$this$lastOrNull$iv`: ByteArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Byte = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.findLast(predicate: (Short) -> Boolean): Short? {
      var `$this$lastOrNull$iv`: ShortArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Short = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.findLast(predicate: (Int) -> Boolean): Int? {
      var `$this$lastOrNull$iv`: IntArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Int = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.findLast(predicate: (Long) -> Boolean): Long? {
      var `$this$lastOrNull$iv`: LongArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Long = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.findLast(predicate: (Float) -> Boolean): Float? {
      var `$this$lastOrNull$iv`: FloatArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Float = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.findLast(predicate: (Double) -> Boolean): Double? {
      var `$this$lastOrNull$iv`: DoubleArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Double = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.findLast(predicate: (Boolean) -> Boolean): Boolean? {
      var `$this$lastOrNull$iv`: BooleanArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Boolean = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.findLast(predicate: (Char) -> Boolean): Char? {
      var `$this$lastOrNull$iv`: CharArray = `$this$findLast`;
      var var4: Int = `$this$findLast`.length + -1;
      if (0 <= `$this$findLast`.length + -1) {
         do {
            var `element$iv`: Char = `$this$lastOrNull$iv`[var4--];
            if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
               return `element$iv`;
            }
         } while (0 <= var4);
      }

      return null;
   }

   @JvmStatic
   public fun <T> Array<out T>.first(): T {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return (T)`$this$first`[0];
      }
   }

   @JvmStatic
   public fun ByteArray.first(): Byte {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun ShortArray.first(): Short {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun IntArray.first(): Int {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun LongArray.first(): Long {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun FloatArray.first(): Float {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun DoubleArray.first(): Double {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun BooleanArray.first(): Boolean {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public fun CharArray.first(): Char {
      if (`$this$first`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$first`[0];
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.first(predicate: (T) -> Boolean): T {
      for (Object element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun ByteArray.first(predicate: (Byte) -> Boolean): Byte {
      for (byte element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun ShortArray.first(predicate: (Short) -> Boolean): Short {
      for (short element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun IntArray.first(predicate: (Int) -> Boolean): Int {
      for (int element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun LongArray.first(predicate: (Long) -> Boolean): Long {
      for (long element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun FloatArray.first(predicate: (Float) -> Boolean): Float {
      for (float element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun DoubleArray.first(predicate: (Double) -> Boolean): Double {
      for (double element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun BooleanArray.first(predicate: (Boolean) -> Boolean): Boolean {
      for (boolean element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun CharArray.first(predicate: (Char) -> Boolean): Char {
      for (char element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Array<out T>.firstNotNullOf(transform: (T) -> R?): R {
      var var2: Array<Any> = `$this$firstNotNullOf`;
      var var3: Int = 0;
      var var4: Int = `$this$firstNotNullOf`.length;

      var var10000: Any;
      while (true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         var10000 = transform.invoke(var2[var3]);
         if (var10000 != null) {
            break;
         }

         var3++;
      }

      if (var10000 == null) {
         throw new NoSuchElementException("No element of the array was transformed to a non-null value.");
      } else {
         return (R)var10000;
      }
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Array<out T>.firstNotNullOfOrNull(transform: (T) -> R?): R? {
      for (Object element : $this$firstNotNullOfOrNull) {
         var result: Any = transform.invoke(element);
         if (result != null) {
            return (R)result;
         }
      }

      return null;
   }

   @JvmStatic
   public fun <T> Array<out T>.firstOrNull(): T? {
      return (T)(if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0]);
   }

   @JvmStatic
   public fun ByteArray.firstOrNull(): Byte? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun ShortArray.firstOrNull(): Short? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun IntArray.firstOrNull(): Int? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun LongArray.firstOrNull(): Long? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun FloatArray.firstOrNull(): Float? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun DoubleArray.firstOrNull(): Double? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun BooleanArray.firstOrNull(): Boolean? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public fun CharArray.firstOrNull(): Char? {
      return if (`$this$firstOrNull`.length == 0) null else `$this$firstOrNull`[0];
   }

   @JvmStatic
   public inline fun <T> Array<out T>.firstOrNull(predicate: (T) -> Boolean): T? {
      for (Object element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun ByteArray.firstOrNull(predicate: (Byte) -> Boolean): Byte? {
      for (byte element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun ShortArray.firstOrNull(predicate: (Short) -> Boolean): Short? {
      for (short element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun IntArray.firstOrNull(predicate: (Int) -> Boolean): Int? {
      for (int element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun LongArray.firstOrNull(predicate: (Long) -> Boolean): Long? {
      for (long element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun FloatArray.firstOrNull(predicate: (Float) -> Boolean): Float? {
      for (float element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun DoubleArray.firstOrNull(predicate: (Double) -> Boolean): Double? {
      for (double element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun BooleanArray.firstOrNull(predicate: (Boolean) -> Boolean): Boolean? {
      for (boolean element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @JvmStatic
   public inline fun CharArray.firstOrNull(predicate: (Char) -> Boolean): Char? {
      for (char element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return element;
         }
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.getOrElse(index: Int, defaultValue: (Int) -> T): T {
      return (T)(if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`)) `$this$getOrElse`[index] else defaultValue.invoke(index));
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.getOrElse(index: Int, defaultValue: (Int) -> Byte): Byte {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).byteValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.getOrElse(index: Int, defaultValue: (Int) -> Short): Short {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).shortValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.getOrElse(index: Int, defaultValue: (Int) -> Int): Int {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).intValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.getOrElse(index: Int, defaultValue: (Int) -> Long): Long {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).longValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.getOrElse(index: Int, defaultValue: (Int) -> Float): Float {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).floatValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.getOrElse(index: Int, defaultValue: (Int) -> Double): Double {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`))
         `$this$getOrElse`[index]
         else
         (defaultValue.invoke(index) as java.lang.Number).doubleValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.getOrElse(index: Int, defaultValue: (Int) -> Boolean): Boolean {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`)) `$this$getOrElse`[index] else defaultValue.invoke(index) as java.lang.Boolean;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.getOrElse(index: Int, defaultValue: (Int) -> Char): Char {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrElse`)) `$this$getOrElse`[index] else defaultValue.invoke(index) as Character;
   }

   @JvmStatic
   public fun <T> Array<out T>.getOrNull(index: Int): T? {
      return (T)(if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null);
   }

   @JvmStatic
   public fun ByteArray.getOrNull(index: Int): Byte? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun ShortArray.getOrNull(index: Int): Short? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun IntArray.getOrNull(index: Int): Int? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun LongArray.getOrNull(index: Int): Long? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun FloatArray.getOrNull(index: Int): Float? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun DoubleArray.getOrNull(index: Int): Double? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun BooleanArray.getOrNull(index: Int): Boolean? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun CharArray.getOrNull(index: Int): Char? {
      return if (index >= 0 && index <= ArraysKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`[index] else null;
   }

   @JvmStatic
   public fun <T> Array<out T>.indexOf(element: T): Int {
      if (element == null) {
         var index: Int = 0;

         for (int var3 = $this$indexOf.length; index < var3; index++) {
            if (`$this$indexOf`[index] == null) {
               return index;
            }
         }
      } else {
         var var4: Int = 0;

         for (int var5 = $this$indexOf.length; index < var5; index++) {
            if (element == `$this$indexOf`[var4]) {
               return var4;
            }
         }
      }

      return -1;
   }

   @JvmStatic
   public fun ByteArray.indexOf(element: Byte): Int {
      var index: Int = 0;

      for (int var3 = $this$indexOf.length; index < var3; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public fun ShortArray.indexOf(element: Short): Int {
      var index: Int = 0;

      for (int var3 = $this$indexOf.length; index < var3; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public fun IntArray.indexOf(element: Int): Int {
      var index: Int = 0;

      for (int var3 = $this$indexOf.length; index < var3; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public fun LongArray.indexOf(element: Long): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOf.length; index < var4; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public fun BooleanArray.indexOf(element: Boolean): Int {
      var index: Int = 0;

      for (int var3 = $this$indexOf.length; index < var3; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public fun CharArray.indexOf(element: Char): Int {
      var index: Int = 0;

      for (int var3 = $this$indexOf.length; index < var3; index++) {
         if (element == `$this$indexOf`[index]) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.indexOfFirst(predicate: (T) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun ByteArray.indexOfFirst(predicate: (Byte) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun ShortArray.indexOfFirst(predicate: (Short) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun IntArray.indexOfFirst(predicate: (Int) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun LongArray.indexOfFirst(predicate: (Long) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun FloatArray.indexOfFirst(predicate: (Float) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun DoubleArray.indexOfFirst(predicate: (Double) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun BooleanArray.indexOfFirst(predicate: (Boolean) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun CharArray.indexOfFirst(predicate: (Char) -> Boolean): Int {
      var index: Int = 0;

      for (int var4 = $this$indexOfFirst.length; index < var4; index++) {
         if (predicate.invoke(`$this$indexOfFirst`[index]) as java.lang.Boolean) {
            return index;
         }
      }

      return -1;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.indexOfLast(predicate: (T) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun ByteArray.indexOfLast(predicate: (Byte) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun ShortArray.indexOfLast(predicate: (Short) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun IntArray.indexOfLast(predicate: (Int) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun LongArray.indexOfLast(predicate: (Long) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun FloatArray.indexOfLast(predicate: (Float) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun DoubleArray.indexOfLast(predicate: (Double) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun BooleanArray.indexOfLast(predicate: (Boolean) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public inline fun CharArray.indexOfLast(predicate: (Char) -> Boolean): Int {
      var var3: Int = `$this$indexOfLast`.length + -1;
      if (0 <= `$this$indexOfLast`.length + -1) {
         do {
            var index: Int = var3--;
            if (predicate.invoke(`$this$indexOfLast`[index]) as java.lang.Boolean) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public fun <T> Array<out T>.last(): T {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return (T)`$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun ByteArray.last(): Byte {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun ShortArray.last(): Short {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun IntArray.last(): Int {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun LongArray.last(): Long {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun FloatArray.last(): Float {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun DoubleArray.last(): Double {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun BooleanArray.last(): Boolean {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public fun CharArray.last(): Char {
      if (`$this$last`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$last`[ArraysKt.getLastIndex(`$this$last`)];
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.last(predicate: (T) -> Boolean): T {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Any = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return (T)element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun ByteArray.last(predicate: (Byte) -> Boolean): Byte {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Byte = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun ShortArray.last(predicate: (Short) -> Boolean): Short {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Short = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun IntArray.last(predicate: (Int) -> Boolean): Int {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Int = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun LongArray.last(predicate: (Long) -> Boolean): Long {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Long = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun FloatArray.last(predicate: (Float) -> Boolean): Float {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Float = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun DoubleArray.last(predicate: (Double) -> Boolean): Double {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Double = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun BooleanArray.last(predicate: (Boolean) -> Boolean): Boolean {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Boolean = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public inline fun CharArray.last(predicate: (Char) -> Boolean): Char {
      var var3: Int = `$this$last`.length + -1;
      if (0 <= `$this$last`.length + -1) {
         do {
            var element: Char = `$this$last`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      throw new NoSuchElementException("Array contains no element matching the predicate.");
   }

   @JvmStatic
   public fun <T> Array<out T>.lastIndexOf(element: T): Int {
      if (element == null) {
         var var2: Int = `$this$lastIndexOf`.length + -1;
         if (0 <= `$this$lastIndexOf`.length + -1) {
            do {
               var index: Int = var2--;
               if (`$this$lastIndexOf`[index] == null) {
                  return index;
               }
            } while (0 <= var2);
         }
      } else {
         var var4: Int = `$this$lastIndexOf`.length + -1;
         if (0 <= `$this$lastIndexOf`.length + -1) {
            do {
               var var5: Int = var4--;
               if (element == `$this$lastIndexOf`[var5]) {
                  return var5;
               }
            } while (0 <= var4);
         }
      }

      return -1;
   }

   @JvmStatic
   public fun ByteArray.lastIndexOf(element: Byte): Int {
      var var2: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var2--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var2);
      }

      return -1;
   }

   @JvmStatic
   public fun ShortArray.lastIndexOf(element: Short): Int {
      var var2: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var2--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var2);
      }

      return -1;
   }

   @JvmStatic
   public fun IntArray.lastIndexOf(element: Int): Int {
      var var2: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var2--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var2);
      }

      return -1;
   }

   @JvmStatic
   public fun LongArray.lastIndexOf(element: Long): Int {
      var var3: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var3--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var3);
      }

      return -1;
   }

   @JvmStatic
   public fun BooleanArray.lastIndexOf(element: Boolean): Int {
      var var2: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var2--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var2);
      }

      return -1;
   }

   @JvmStatic
   public fun CharArray.lastIndexOf(element: Char): Int {
      var var2: Int = `$this$lastIndexOf`.length + -1;
      if (0 <= `$this$lastIndexOf`.length + -1) {
         do {
            var index: Int = var2--;
            if (element == `$this$lastIndexOf`[index]) {
               return index;
            }
         } while (0 <= var2);
      }

      return -1;
   }

   @JvmStatic
   public fun <T> Array<out T>.lastOrNull(): T? {
      return (T)(if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1]);
   }

   @JvmStatic
   public fun ByteArray.lastOrNull(): Byte? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun ShortArray.lastOrNull(): Short? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun IntArray.lastOrNull(): Int? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun LongArray.lastOrNull(): Long? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun FloatArray.lastOrNull(): Float? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun DoubleArray.lastOrNull(): Double? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun BooleanArray.lastOrNull(): Boolean? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public fun CharArray.lastOrNull(): Char? {
      return if (`$this$lastOrNull`.length == 0) null else `$this$lastOrNull`[`$this$lastOrNull`.length - 1];
   }

   @JvmStatic
   public inline fun <T> Array<out T>.lastOrNull(predicate: (T) -> Boolean): T? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Any = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return (T)element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun ByteArray.lastOrNull(predicate: (Byte) -> Boolean): Byte? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Byte = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun ShortArray.lastOrNull(predicate: (Short) -> Boolean): Short? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Short = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun IntArray.lastOrNull(predicate: (Int) -> Boolean): Int? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Int = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun LongArray.lastOrNull(predicate: (Long) -> Boolean): Long? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Long = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun FloatArray.lastOrNull(predicate: (Float) -> Boolean): Float? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Float = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun DoubleArray.lastOrNull(predicate: (Double) -> Boolean): Double? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Double = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun BooleanArray.lastOrNull(predicate: (Boolean) -> Boolean): Boolean? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Boolean = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @JvmStatic
   public inline fun CharArray.lastOrNull(predicate: (Char) -> Boolean): Char? {
      var var3: Int = `$this$lastOrNull`.length + -1;
      if (0 <= `$this$lastOrNull`.length + -1) {
         do {
            var element: Char = `$this$lastOrNull`[var3--];
            if (predicate.invoke(element) as java.lang.Boolean) {
               return element;
            }
         } while (0 <= var3);
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.random(): T {
      return (T)ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.random(): Byte {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.random(): Short {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.random(): Int {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.random(): Long {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.random(): Float {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.random(): Double {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.random(): Boolean {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.random(): Char {
      return ArraysKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Array<out T>.random(random: Random): T {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return (T)`$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun ByteArray.random(random: Random): Byte {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun ShortArray.random(random: Random): Short {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun IntArray.random(random: Random): Int {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun LongArray.random(random: Random): Long {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun FloatArray.random(random: Random): Float {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun DoubleArray.random(random: Random): Double {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun BooleanArray.random(random: Random): Boolean {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun CharArray.random(random: Random): Char {
      if (`$this$random`.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return `$this$random`[random.nextInt(`$this$random`.length)];
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.randomOrNull(): T? {
      return (T)ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.randomOrNull(): Byte? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.randomOrNull(): Short? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.randomOrNull(): Int? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.randomOrNull(): Long? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.randomOrNull(): Float? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.randomOrNull(): Double? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.randomOrNull(): Boolean? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.randomOrNull(): Char? {
      return ArraysKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> Array<out T>.randomOrNull(random: Random): T? {
      return (T)(if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)]);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun ByteArray.randomOrNull(random: Random): Byte? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun ShortArray.randomOrNull(random: Random): Short? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun IntArray.randomOrNull(random: Random): Int? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun LongArray.randomOrNull(random: Random): Long? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun FloatArray.randomOrNull(random: Random): Float? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun DoubleArray.randomOrNull(random: Random): Double? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun BooleanArray.randomOrNull(random: Random): Boolean? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun CharArray.randomOrNull(random: Random): Char? {
      return if (`$this$randomOrNull`.length == 0) null else `$this$randomOrNull`[random.nextInt(`$this$randomOrNull`.length)];
   }

   @JvmStatic
   public fun <T> Array<out T>.single(): T {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return (T)`$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun ByteArray.single(): Byte {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun ShortArray.single(): Short {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun IntArray.single(): Int {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun LongArray.single(): Long {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun FloatArray.single(): Float {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun DoubleArray.single(): Double {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun BooleanArray.single(): Boolean {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public fun CharArray.single(): Char {
      switch ($this$single.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return `$this$single`[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.single(predicate: (T) -> Boolean): T {
      var single: Any = null;
      var found: Boolean = false;

      for (Object element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return (T)single;
      }
   }

   @JvmStatic
   public inline fun ByteArray.single(predicate: (Byte) -> Boolean): Byte {
      var single: java.lang.Byte = null;
      var found: Boolean = false;

      for (byte element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun ShortArray.single(predicate: (Short) -> Boolean): Short {
      var single: java.lang.Short = null;
      var found: Boolean = false;

      for (short element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun IntArray.single(predicate: (Int) -> Boolean): Int {
      var single: Int = null;
      var found: Boolean = false;

      for (int element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun LongArray.single(predicate: (Long) -> Boolean): Long {
      var single: java.lang.Long = null;
      var found: Boolean = false;

      for (long element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun FloatArray.single(predicate: (Float) -> Boolean): Float {
      var single: java.lang.Float = null;
      var found: Boolean = false;

      for (float element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun DoubleArray.single(predicate: (Double) -> Boolean): Double {
      var single: java.lang.Double = null;
      var found: Boolean = false;

      for (double element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun BooleanArray.single(predicate: (Boolean) -> Boolean): Boolean {
      var single: java.lang.Boolean = null;
      var found: Boolean = false;

      for (boolean element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public inline fun CharArray.single(predicate: (Char) -> Boolean): Char {
      var single: Character = null;
      var found: Boolean = false;

      for (char element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Array contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Array contains no element matching the predicate.");
      } else {
         return single;
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.singleOrNull(): T? {
      return (T)(if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null);
   }

   @JvmStatic
   public fun ByteArray.singleOrNull(): Byte? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun ShortArray.singleOrNull(): Short? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun IntArray.singleOrNull(): Int? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun LongArray.singleOrNull(): Long? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun FloatArray.singleOrNull(): Float? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun DoubleArray.singleOrNull(): Double? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun BooleanArray.singleOrNull(): Boolean? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public fun CharArray.singleOrNull(): Char? {
      return if (`$this$singleOrNull`.length == 1) `$this$singleOrNull`[0] else null;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.singleOrNull(predicate: (T) -> Boolean): T? {
      var single: Any = null;
      var found: Boolean = false;

      for (Object element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return (T)(if (!found) null else single);
   }

   @JvmStatic
   public inline fun ByteArray.singleOrNull(predicate: (Byte) -> Boolean): Byte? {
      var single: java.lang.Byte = null;
      var found: Boolean = false;

      for (byte element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun ShortArray.singleOrNull(predicate: (Short) -> Boolean): Short? {
      var single: java.lang.Short = null;
      var found: Boolean = false;

      for (short element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun IntArray.singleOrNull(predicate: (Int) -> Boolean): Int? {
      var single: Int = null;
      var found: Boolean = false;

      for (int element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun LongArray.singleOrNull(predicate: (Long) -> Boolean): Long? {
      var single: java.lang.Long = null;
      var found: Boolean = false;

      for (long element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun FloatArray.singleOrNull(predicate: (Float) -> Boolean): Float? {
      var single: java.lang.Float = null;
      var found: Boolean = false;

      for (float element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun DoubleArray.singleOrNull(predicate: (Double) -> Boolean): Double? {
      var single: java.lang.Double = null;
      var found: Boolean = false;

      for (double element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun BooleanArray.singleOrNull(predicate: (Boolean) -> Boolean): Boolean? {
      var single: java.lang.Boolean = null;
      var found: Boolean = false;

      for (boolean element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public inline fun CharArray.singleOrNull(predicate: (Char) -> Boolean): Char? {
      var single: Character = null;
      var found: Boolean = false;

      for (char element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return if (!found) null else single;
   }

   @JvmStatic
   public fun <T> Array<out T>.drop(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return (java.util.List<T>)ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun ByteArray.drop(n: Int): List<Byte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun ShortArray.drop(n: Int): List<Short> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun IntArray.drop(n: Int): List<Int> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun LongArray.drop(n: Int): List<Long> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun FloatArray.drop(n: Int): List<Float> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun DoubleArray.drop(n: Int): List<Double> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun BooleanArray.drop(n: Int): List<Boolean> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun CharArray.drop(n: Int): List<Char> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.takeLast(`$this$drop`, RangesKt.coerceAtLeast(`$this$drop`.length - n, 0));
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.dropLast(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return (java.util.List<T>)ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun ByteArray.dropLast(n: Int): List<Byte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun ShortArray.dropLast(n: Int): List<Short> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun IntArray.dropLast(n: Int): List<Int> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun LongArray.dropLast(n: Int): List<Long> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun FloatArray.dropLast(n: Int): List<Float> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun DoubleArray.dropLast(n: Int): List<Double> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun BooleanArray.dropLast(n: Int): List<Boolean> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public fun CharArray.dropLast(n: Int): List<Char> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return ArraysKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.length - n, 0));
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.dropLastWhile(predicate: (T) -> Boolean): List<T> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return (java.util.List<T>)ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun ByteArray.dropLastWhile(predicate: (Byte) -> Boolean): List<Byte> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun ShortArray.dropLastWhile(predicate: (Short) -> Boolean): List<Short> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun IntArray.dropLastWhile(predicate: (Int) -> Boolean): List<Int> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun LongArray.dropLastWhile(predicate: (Long) -> Boolean): List<Long> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun FloatArray.dropLastWhile(predicate: (Float) -> Boolean): List<Float> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun DoubleArray.dropLastWhile(predicate: (Double) -> Boolean): List<Double> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun BooleanArray.dropLastWhile(predicate: (Boolean) -> Boolean): List<Boolean> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun CharArray.dropLastWhile(predicate: (Char) -> Boolean): List<Char> {
      for (int index = ArraysKt.getLastIndex($this$dropLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$dropLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.take(`$this$dropLastWhile`, index + 1);
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun <T> Array<out T>.dropWhile(predicate: (T) -> Boolean): List<T> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (Object item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun ByteArray.dropWhile(predicate: (Byte) -> Boolean): List<Byte> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (byte item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun ShortArray.dropWhile(predicate: (Short) -> Boolean): List<Short> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (short item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun IntArray.dropWhile(predicate: (Int) -> Boolean): List<Int> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (int item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun LongArray.dropWhile(predicate: (Long) -> Boolean): List<Long> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (long item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun FloatArray.dropWhile(predicate: (Float) -> Boolean): List<Float> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (float item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun DoubleArray.dropWhile(predicate: (Double) -> Boolean): List<Double> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (double item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun BooleanArray.dropWhile(predicate: (Boolean) -> Boolean): List<Boolean> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (boolean item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun CharArray.dropWhile(predicate: (Char) -> Boolean): List<Char> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (char item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.filter(predicate: (T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public inline fun ByteArray.filter(predicate: (Byte) -> Boolean): List<Byte> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (byte element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Byte>;
   }

   @JvmStatic
   public inline fun ShortArray.filter(predicate: (Short) -> Boolean): List<Short> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (short element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Short>;
   }

   @JvmStatic
   public inline fun IntArray.filter(predicate: (Int) -> Boolean): List<Int> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (int element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<Integer>;
   }

   @JvmStatic
   public inline fun LongArray.filter(predicate: (Long) -> Boolean): List<Long> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (long element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Long>;
   }

   @JvmStatic
   public inline fun FloatArray.filter(predicate: (Float) -> Boolean): List<Float> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (float element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Float>;
   }

   @JvmStatic
   public inline fun DoubleArray.filter(predicate: (Double) -> Boolean): List<Double> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (double element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Double>;
   }

   @JvmStatic
   public inline fun BooleanArray.filter(predicate: (Boolean) -> Boolean): List<Boolean> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (boolean element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Boolean>;
   }

   @JvmStatic
   public inline fun CharArray.filter(predicate: (Char) -> Boolean): List<Char> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (char element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<Character>;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.filterIndexed(predicate: (Int, T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (Object item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public inline fun ByteArray.filterIndexed(predicate: (Int, Byte) -> Boolean): List<Byte> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (byte item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Byte>;
   }

   @JvmStatic
   public inline fun ShortArray.filterIndexed(predicate: (Int, Short) -> Boolean): List<Short> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (short item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Short>;
   }

   @JvmStatic
   public inline fun IntArray.filterIndexed(predicate: (Int, Int) -> Boolean): List<Int> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (int item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<Integer>;
   }

   @JvmStatic
   public inline fun LongArray.filterIndexed(predicate: (Int, Long) -> Boolean): List<Long> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (long item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Long>;
   }

   @JvmStatic
   public inline fun FloatArray.filterIndexed(predicate: (Int, Float) -> Boolean): List<Float> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (float item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Float>;
   }

   @JvmStatic
   public inline fun DoubleArray.filterIndexed(predicate: (Int, Double) -> Boolean): List<Double> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (double item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Double>;
   }

   @JvmStatic
   public inline fun BooleanArray.filterIndexed(predicate: (Int, Boolean) -> Boolean): List<Boolean> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (boolean item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Boolean>;
   }

   @JvmStatic
   public inline fun CharArray.filterIndexed(predicate: (Int, Char) -> Boolean): List<Char> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (char item$iv$iv : $this$filterIndexed) {
         if (predicate.invoke(`index$iv$iv`++, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<Character>;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterIndexedTo(destination: C, predicate: (Int, T) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Byte>> ByteArray.filterIndexedTo(destination: C, predicate: (Int, Byte) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (byte item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Short>> ShortArray.filterIndexedTo(destination: C, predicate: (Int, Short) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (short item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Int>> IntArray.filterIndexedTo(destination: C, predicate: (Int, Int) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (int item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Long>> LongArray.filterIndexedTo(destination: C, predicate: (Int, Long) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (long item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Float>> FloatArray.filterIndexedTo(destination: C, predicate: (Int, Float) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (float item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Double>> DoubleArray.filterIndexedTo(destination: C, predicate: (Int, Double) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (double item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterIndexedTo(destination: C, predicate: (Int, Boolean) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (boolean item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Char>> CharArray.filterIndexedTo(destination: C, predicate: (Int, Char) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (char item$iv : $this$filterIndexedTo) {
         if (predicate.invoke(`index$iv`++, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.filterNot(predicate: (T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public inline fun ByteArray.filterNot(predicate: (Byte) -> Boolean): List<Byte> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (byte element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Byte>;
   }

   @JvmStatic
   public inline fun ShortArray.filterNot(predicate: (Short) -> Boolean): List<Short> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (short element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Short>;
   }

   @JvmStatic
   public inline fun IntArray.filterNot(predicate: (Int) -> Boolean): List<Int> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (int element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<Integer>;
   }

   @JvmStatic
   public inline fun LongArray.filterNot(predicate: (Long) -> Boolean): List<Long> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (long element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Long>;
   }

   @JvmStatic
   public inline fun FloatArray.filterNot(predicate: (Float) -> Boolean): List<Float> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (float element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Float>;
   }

   @JvmStatic
   public inline fun DoubleArray.filterNot(predicate: (Double) -> Boolean): List<Double> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (double element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Double>;
   }

   @JvmStatic
   public inline fun BooleanArray.filterNot(predicate: (Boolean) -> Boolean): List<Boolean> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (boolean element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<java.lang.Boolean>;
   }

   @JvmStatic
   public inline fun CharArray.filterNot(predicate: (Char) -> Boolean): List<Char> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (char element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<Character>;
   }

   @JvmStatic
   public fun <T : Any> Array<out T?>.filterNotNull(): List<T> {
      return ArraysKt.filterNotNullTo(`$this$filterNotNull`, new ArrayList()) as java.util.List<T>;
   }

   @JvmStatic
   public fun <C : MutableCollection<in T>, T : Any> Array<out T?>.filterNotNullTo(destination: C): C {
      for (Object element : $this$filterNotNullTo) {
         if (element != null) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterNotTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Byte>> ByteArray.filterNotTo(destination: C, predicate: (Byte) -> Boolean): C {
      for (byte element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Short>> ShortArray.filterNotTo(destination: C, predicate: (Short) -> Boolean): C {
      for (short element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Int>> IntArray.filterNotTo(destination: C, predicate: (Int) -> Boolean): C {
      for (int element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Long>> LongArray.filterNotTo(destination: C, predicate: (Long) -> Boolean): C {
      for (long element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Float>> FloatArray.filterNotTo(destination: C, predicate: (Float) -> Boolean): C {
      for (float element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Double>> DoubleArray.filterNotTo(destination: C, predicate: (Double) -> Boolean): C {
      for (double element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterNotTo(destination: C, predicate: (Boolean) -> Boolean): C {
      for (boolean element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Char>> CharArray.filterNotTo(destination: C, predicate: (Char) -> Boolean): C {
      for (char element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Array<out T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Byte>> ByteArray.filterTo(destination: C, predicate: (Byte) -> Boolean): C {
      for (byte element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Short>> ShortArray.filterTo(destination: C, predicate: (Short) -> Boolean): C {
      for (short element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Int>> IntArray.filterTo(destination: C, predicate: (Int) -> Boolean): C {
      for (int element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Long>> LongArray.filterTo(destination: C, predicate: (Long) -> Boolean): C {
      for (long element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Float>> FloatArray.filterTo(destination: C, predicate: (Float) -> Boolean): C {
      for (float element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Double>> DoubleArray.filterTo(destination: C, predicate: (Double) -> Boolean): C {
      for (double element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Boolean>> BooleanArray.filterTo(destination: C, predicate: (Boolean) -> Boolean): C {
      for (boolean element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <C : MutableCollection<in Char>> CharArray.filterTo(destination: C, predicate: (Char) -> Boolean): C {
      for (char element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Array<out T>.slice(indices: IntRange): List<T> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun ByteArray.slice(indices: IntRange): List<Byte> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun ShortArray.slice(indices: IntRange): List<Short> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun IntArray.slice(indices: IntRange): List<Int> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun LongArray.slice(indices: IntRange): List<Long> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun FloatArray.slice(indices: IntRange): List<Float> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun DoubleArray.slice(indices: IntRange): List<Double> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun BooleanArray.slice(indices: IntRange): List<Boolean> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun CharArray.slice(indices: IntRange): List<Char> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         ArraysKt.asList(ArraysKt.copyOfRange(`$this$slice`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun <T> Array<out T>.slice(indices: Iterable<Int>): List<T> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun ByteArray.slice(indices: Iterable<Int>): List<Byte> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun ShortArray.slice(indices: Iterable<Int>): List<Short> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun IntArray.slice(indices: Iterable<Int>): List<Int> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun LongArray.slice(indices: Iterable<Int>): List<Long> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun FloatArray.slice(indices: Iterable<Int>): List<Float> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun DoubleArray.slice(indices: Iterable<Int>): List<Double> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun BooleanArray.slice(indices: Iterable<Int>): List<Boolean> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun CharArray.slice(indices: Iterable<Int>): List<Char> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`[(var4.next() as java.lang.Number).intValue()]);
         }

         return list;
      }
   }

   @JvmStatic
   public fun <T> Array<T>.sliceArray(indices: Collection<Int>): Array<T> {
      var result: Array<Any> = ArraysKt.arrayOfNulls(`$this$sliceArray`, indices.size());
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return (T[])result;
   }

   @JvmStatic
   public fun ByteArray.sliceArray(indices: Collection<Int>): ByteArray {
      var result: ByteArray = new byte[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun ShortArray.sliceArray(indices: Collection<Int>): ShortArray {
      var result: ShortArray = new short[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun IntArray.sliceArray(indices: Collection<Int>): IntArray {
      var result: IntArray = new int[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun LongArray.sliceArray(indices: Collection<Int>): LongArray {
      var result: LongArray = new long[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun FloatArray.sliceArray(indices: Collection<Int>): FloatArray {
      var result: FloatArray = new float[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun DoubleArray.sliceArray(indices: Collection<Int>): DoubleArray {
      var result: DoubleArray = new double[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun BooleanArray.sliceArray(indices: Collection<Int>): BooleanArray {
      var result: BooleanArray = new boolean[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun CharArray.sliceArray(indices: Collection<Int>): CharArray {
      var result: CharArray = new char[indices.size()];
      var targetIndex: Int = 0;
      var var4: java.util.Iterator = indices.iterator();

      while (var4.hasNext()) {
         result[targetIndex++] = `$this$sliceArray`[(var4.next() as java.lang.Number).intValue()];
      }

      return result;
   }

   @JvmStatic
   public fun <T> Array<T>.sliceArray(indices: IntRange): Array<T> {
      return (T[])(if (indices.isEmpty())
         ArraysKt.copyOfRange(`$this$sliceArray`, 0, 0)
         else
         ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun ByteArray.sliceArray(indices: IntRange): ByteArray {
      return if (indices.isEmpty()) new byte[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun ShortArray.sliceArray(indices: IntRange): ShortArray {
      return if (indices.isEmpty()) new short[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun IntArray.sliceArray(indices: IntRange): IntArray {
      return if (indices.isEmpty()) new int[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun LongArray.sliceArray(indices: IntRange): LongArray {
      return if (indices.isEmpty()) new long[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun FloatArray.sliceArray(indices: IntRange): FloatArray {
      return if (indices.isEmpty()) new float[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun DoubleArray.sliceArray(indices: IntRange): DoubleArray {
      return if (indices.isEmpty()) new double[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun BooleanArray.sliceArray(indices: IntRange): BooleanArray {
      return if (indices.isEmpty()) new boolean[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun CharArray.sliceArray(indices: IntRange): CharArray {
      return if (indices.isEmpty()) new char[0] else ArraysKt.copyOfRange(`$this$sliceArray`, indices.getStart(), indices.getEndInclusive() + 1);
   }

   @JvmStatic
   public fun <T> Array<out T>.take(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return (java.util.List<T>)ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return (java.util.List<T>)CollectionsKt.listOf(`$this$take`[0]);
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (Object item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun ByteArray.take(n: Int): List<Byte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Byte.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (byte item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun ShortArray.take(n: Int): List<Short> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Short.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (short item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun IntArray.take(n: Int): List<Int> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(Integer.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (int item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun LongArray.take(n: Int): List<Long> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Long.valueOf(`$this$take`[0]));
      } else {
         var var8: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (long item : $this$take) {
            list.add(item);
            if (++var8 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun FloatArray.take(n: Int): List<Float> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Float.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (float item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun DoubleArray.take(n: Int): List<Double> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Double.valueOf(`$this$take`[0]));
      } else {
         var var8: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (double item : $this$take) {
            list.add(item);
            if (++var8 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun BooleanArray.take(n: Int): List<Boolean> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(java.lang.Boolean.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (boolean item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun CharArray.take(n: Int): List<Char> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else if (n >= `$this$take`.length) {
         return ArraysKt.toList(`$this$take`);
      } else if (n == 1) {
         return CollectionsKt.listOf(Character.valueOf(`$this$take`[0]));
      } else {
         var var7: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (char item : $this$take) {
            list.add(item);
            if (++var7 == n) {
               break;
            }
         }

         return list;
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.takeLast(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return (java.util.List<T>)ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return (java.util.List<T>)CollectionsKt.listOf(`$this$takeLast`[var5 - 1]);
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun ByteArray.takeLast(n: Int): List<Byte> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Byte.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun ShortArray.takeLast(n: Int): List<Short> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Short.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun IntArray.takeLast(n: Int): List<Int> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(Integer.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun LongArray.takeLast(n: Int): List<Long> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Long.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun FloatArray.takeLast(n: Int): List<Float> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Float.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun DoubleArray.takeLast(n: Int): List<Double> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Double.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun BooleanArray.takeLast(n: Int): List<Boolean> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(java.lang.Boolean.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public fun CharArray.takeLast(n: Int): List<Char> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var5: Int = `$this$takeLast`.length;
         if (n >= `$this$takeLast`.length) {
            return ArraysKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return CollectionsKt.listOf(Character.valueOf(`$this$takeLast`[var5 - 1]));
         } else {
            var list: ArrayList = new ArrayList(n);

            for (int index = size - n; index < size; index++) {
               list.add(`$this$takeLast`[index]);
            }

            return list;
         }
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.takeLastWhile(predicate: (T) -> Boolean): List<T> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return (java.util.List<T>)ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return (java.util.List<T>)ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun ByteArray.takeLastWhile(predicate: (Byte) -> Boolean): List<Byte> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun ShortArray.takeLastWhile(predicate: (Short) -> Boolean): List<Short> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun IntArray.takeLastWhile(predicate: (Int) -> Boolean): List<Int> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun LongArray.takeLastWhile(predicate: (Long) -> Boolean): List<Long> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun FloatArray.takeLastWhile(predicate: (Float) -> Boolean): List<Float> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun DoubleArray.takeLastWhile(predicate: (Double) -> Boolean): List<Double> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun BooleanArray.takeLastWhile(predicate: (Boolean) -> Boolean): List<Boolean> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun CharArray.takeLastWhile(predicate: (Char) -> Boolean): List<Char> {
      for (int index = ArraysKt.getLastIndex($this$takeLastWhile); -1 < index; index--) {
         if (!predicate.invoke(`$this$takeLastWhile`[index]) as java.lang.Boolean) {
            return ArraysKt.drop(`$this$takeLastWhile`, index + 1);
         }
      }

      return ArraysKt.toList(`$this$takeLastWhile`);
   }

   @JvmStatic
   public inline fun <T> Array<out T>.takeWhile(predicate: (T) -> Boolean): List<T> {
      var list: ArrayList = new ArrayList();

      for (Object item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun ByteArray.takeWhile(predicate: (Byte) -> Boolean): List<Byte> {
      var list: ArrayList = new ArrayList();

      for (byte item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun ShortArray.takeWhile(predicate: (Short) -> Boolean): List<Short> {
      var list: ArrayList = new ArrayList();

      for (short item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun IntArray.takeWhile(predicate: (Int) -> Boolean): List<Int> {
      var list: ArrayList = new ArrayList();

      for (int item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun LongArray.takeWhile(predicate: (Long) -> Boolean): List<Long> {
      var list: ArrayList = new ArrayList();

      for (long item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun FloatArray.takeWhile(predicate: (Float) -> Boolean): List<Float> {
      var list: ArrayList = new ArrayList();

      for (float item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun DoubleArray.takeWhile(predicate: (Double) -> Boolean): List<Double> {
      var list: ArrayList = new ArrayList();

      for (double item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun BooleanArray.takeWhile(predicate: (Boolean) -> Boolean): List<Boolean> {
      var list: ArrayList = new ArrayList();

      for (boolean item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public inline fun CharArray.takeWhile(predicate: (Char) -> Boolean): List<Char> {
      var list: ArrayList = new ArrayList();

      for (char item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun <T> Array<T>.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Any = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun ByteArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Byte = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun ShortArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Short = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun IntArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Int = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun LongArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Long = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun FloatArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Float = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun DoubleArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Double = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun BooleanArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Boolean = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @JvmStatic
   public fun CharArray.reverse() {
      var midPoint: Int = `$this$reverse`.length / 2 - 1;
      if (`$this$reverse`.length / 2 - 1 >= 0) {
         var reverseIndex: Int = ArraysKt.getLastIndex(`$this$reverse`);

         for (IntIterator var3 = new IntRange(0, midPoint).iterator(); var3.hasNext(); reverseIndex--) {
            var index: Int = var3.nextInt();
            var tmp: Char = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Array<T>.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Any = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Byte = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Short = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Int = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Long = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Float = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Double = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun BooleanArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Boolean = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.reverse(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, `$this$reverse`.length);
      var midPoint: Int = (fromIndex + toIndex) / 2;
      if (fromIndex != (fromIndex + toIndex) / 2) {
         var reverseIndex: Int = toIndex - 1;

         for (int index = fromIndex; index < midPoint; index++) {
            var tmp: Char = `$this$reverse`[index];
            `$this$reverse`[index] = `$this$reverse`[reverseIndex];
            `$this$reverse`[reverseIndex] = tmp;
            reverseIndex--;
         }
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.reversed(): List<T> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun ByteArray.reversed(): List<Byte> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun ShortArray.reversed(): List<Short> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun IntArray.reversed(): List<Int> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun LongArray.reversed(): List<Long> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun FloatArray.reversed(): List<Float> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun DoubleArray.reversed(): List<Double> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun BooleanArray.reversed(): List<Boolean> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun CharArray.reversed(): List<Char> {
      if (`$this$reversed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: java.util.List = ArraysKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @JvmStatic
   public fun <T> Array<T>.reversedArray(): Array<T> {
      if (`$this$reversedArray`.length == 0) {
         return (T[])`$this$reversedArray`;
      } else {
         var result: Array<Any> = ArraysKt.arrayOfNulls(`$this$reversedArray`, `$this$reversedArray`.length);
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return (T[])result;
      }
   }

   @JvmStatic
   public fun ByteArray.reversedArray(): ByteArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: ByteArray = new byte[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun ShortArray.reversedArray(): ShortArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: ShortArray = new short[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun IntArray.reversedArray(): IntArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: IntArray = new int[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun LongArray.reversedArray(): LongArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: LongArray = new long[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun FloatArray.reversedArray(): FloatArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: FloatArray = new float[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun DoubleArray.reversedArray(): DoubleArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: DoubleArray = new double[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun BooleanArray.reversedArray(): BooleanArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: BooleanArray = new boolean[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @JvmStatic
   public fun CharArray.reversedArray(): CharArray {
      if (`$this$reversedArray`.length == 0) {
         return `$this$reversedArray`;
      } else {
         var result: CharArray = new char[`$this$reversedArray`.length];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$reversedArray`);
         var var3: IntIterator = new IntRange(0, lastIndex).iterator();

         while (var3.hasNext()) {
            var i: Int = var3.nextInt();
            result[lastIndex - i] = `$this$reversedArray`[i];
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Array<T>.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun BooleanArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.shuffle() {
      ArraysKt.shuffle(`$this$shuffle`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Array<T>.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Any = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Byte = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Short = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Int = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Long = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Float = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Double = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun BooleanArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Boolean = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.shuffle(random: Random) {
      for (int i = ArraysKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         var copy: Char = `$this$shuffle`[i];
         `$this$shuffle`[i] = `$this$shuffle`[j];
         `$this$shuffle`[j] = copy;
      }
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.sortBy(crossinline selector: (T) -> R?) {
      if (`$this$sortBy`.length > 1) {
         ArraysKt.sortWith(`$this$sortBy`, new Comparator(selector) {
            {
               this.$selector = `$selector`;
            }

            @Override
            public final int compare(T a, T b) {
               var var3: Function1 = this.$selector;
               return ComparisonsKt.compareValues(this.$selector.invoke((T)a), var3.invoke(b) as java.lang.Comparable<?>);
            }
         });
      }
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.sortByDescending(crossinline selector: (T) -> R?) {
      if (`$this$sortByDescending`.length > 1) {
         ArraysKt.sortWith(`$this$sortByDescending`, new Comparator(selector) {
            {
               this.$selector = `$selector`;
            }

            @Override
            public final int compare(T a, T b) {
               var var3: Function1 = this.$selector;
               return ComparisonsKt.compareValues(this.$selector.invoke((T)b), var3.invoke(a) as java.lang.Comparable<?>);
            }
         });
      }
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.sortDescending() {
      ArraysKt.sortWith(`$this$sortDescending`, ComparisonsKt.reverseOrder());
   }

   @JvmStatic
   public fun ByteArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun ShortArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun IntArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun LongArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun FloatArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun DoubleArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun CharArray.sortDescending() {
      if (`$this$sortDescending`.length > 1) {
         ArraysKt.sort(`$this$sortDescending`);
         ArraysKt.reverse(`$this$sortDescending`);
      }
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.sorted(): List<T> {
      return (java.util.List<T>)ArraysKt.asList(ArraysKt.sortedArray(`$this$sorted`));
   }

   @JvmStatic
   public fun ByteArray.sorted(): List<Byte> {
      var var1: Array<java.lang.Byte> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun ShortArray.sorted(): List<Short> {
      var var1: Array<java.lang.Short> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun IntArray.sorted(): List<Int> {
      var var1: Array<Int> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun LongArray.sorted(): List<Long> {
      var var1: Array<java.lang.Long> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun FloatArray.sorted(): List<Float> {
      var var1: Array<java.lang.Float> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun DoubleArray.sorted(): List<Double> {
      var var1: Array<java.lang.Double> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun CharArray.sorted(): List<Char> {
      var var1: Array<Character> = ArraysKt.toTypedArray(`$this$sorted`);
      ArraysKt.sort(var1);
      return ArraysKt.asList(var1);
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<T>.sortedArray(): Array<T> {
      if (`$this$sortedArray`.length == 0) {
         return (T[])`$this$sortedArray`;
      } else {
         var var10000: Array<Any> = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000 as java.lang.Comparable[]);
         return (T[])(var10000 as java.lang.Comparable[]);
      }
   }

   @JvmStatic
   public fun ByteArray.sortedArray(): ByteArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: ByteArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun ShortArray.sortedArray(): ShortArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: ShortArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun IntArray.sortedArray(): IntArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: IntArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun LongArray.sortedArray(): LongArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: LongArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun FloatArray.sortedArray(): FloatArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: FloatArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun DoubleArray.sortedArray(): DoubleArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: DoubleArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun CharArray.sortedArray(): CharArray {
      if (`$this$sortedArray`.length == 0) {
         return `$this$sortedArray`;
      } else {
         var var10000: CharArray = Arrays.copyOf(`$this$sortedArray`, `$this$sortedArray`.length);
         ArraysKt.sort(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<T>.sortedArrayDescending(): Array<T> {
      if (`$this$sortedArrayDescending`.length == 0) {
         return (T[])`$this$sortedArrayDescending`;
      } else {
         var var10000: Array<Any> = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortWith(var10000 as java.lang.Comparable[], ComparisonsKt.reverseOrder());
         return (T[])(var10000 as java.lang.Comparable[]);
      }
   }

   @JvmStatic
   public fun ByteArray.sortedArrayDescending(): ByteArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: ByteArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun ShortArray.sortedArrayDescending(): ShortArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: ShortArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun IntArray.sortedArrayDescending(): IntArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: IntArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun LongArray.sortedArrayDescending(): LongArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: LongArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun FloatArray.sortedArrayDescending(): FloatArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: FloatArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun DoubleArray.sortedArrayDescending(): DoubleArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: DoubleArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun CharArray.sortedArrayDescending(): CharArray {
      if (`$this$sortedArrayDescending`.length == 0) {
         return `$this$sortedArrayDescending`;
      } else {
         var var10000: CharArray = Arrays.copyOf(`$this$sortedArrayDescending`, `$this$sortedArrayDescending`.length);
         ArraysKt.sortDescending(var10000);
         return var10000;
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.sortedArrayWith(comparator: Comparator<in T>): Array<out T> {
      if (`$this$sortedArrayWith`.length == 0) {
         return (T[])`$this$sortedArrayWith`;
      } else {
         var var10000: Array<Any> = Arrays.copyOf(`$this$sortedArrayWith`, `$this$sortedArrayWith`.length);
         ArraysKt.sortWith(var10000, comparator);
         return (T[])var10000;
      }
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.sortedBy(crossinline selector: (T) -> R?): List<T> {
      return (java.util.List<T>)ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.sortedBy(crossinline selector: (Byte) -> R?): List<Byte> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.sortedBy(crossinline selector: (Short) -> R?): List<Short> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.sortedBy(crossinline selector: (Int) -> R?): List<Int> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.sortedBy(crossinline selector: (Long) -> R?): List<Long> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.sortedBy(crossinline selector: (Float) -> R?): List<Float> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.sortedBy(crossinline selector: (Double) -> R?): List<Double> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.sortedBy(crossinline selector: (Boolean) -> R?): List<Boolean> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.sortedBy(crossinline selector: (Char) -> R?): List<Char> {
      return ArraysKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.sortedByDescending(crossinline selector: (T) -> R?): List<T> {
      return (java.util.List<T>)ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.sortedByDescending(crossinline selector: (Byte) -> R?): List<Byte> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.sortedByDescending(crossinline selector: (Short) -> R?): List<Short> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.sortedByDescending(crossinline selector: (Int) -> R?): List<Int> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.sortedByDescending(crossinline selector: (Long) -> R?): List<Long> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.sortedByDescending(crossinline selector: (Float) -> R?): List<Float> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.sortedByDescending(crossinline selector: (Double) -> R?): List<Double> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.sortedByDescending(crossinline selector: (Boolean) -> R?): List<Boolean> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.sortedByDescending(crossinline selector: (Char) -> R?): List<Char> {
      return ArraysKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.sortedDescending(): List<T> {
      return (java.util.List<T>)ArraysKt.sortedWith(`$this$sortedDescending`, ComparisonsKt.reverseOrder());
   }

   @JvmStatic
   public fun ByteArray.sortedDescending(): List<Byte> {
      var var10000: ByteArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun ShortArray.sortedDescending(): List<Short> {
      var var10000: ShortArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun IntArray.sortedDescending(): List<Int> {
      var var10000: IntArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun LongArray.sortedDescending(): List<Long> {
      var var10000: LongArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun FloatArray.sortedDescending(): List<Float> {
      var var10000: FloatArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun DoubleArray.sortedDescending(): List<Double> {
      var var10000: DoubleArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun CharArray.sortedDescending(): List<Char> {
      var var10000: CharArray = Arrays.copyOf(`$this$sortedDescending`, `$this$sortedDescending`.length);
      ArraysKt.sort(var10000);
      return ArraysKt.reversed(var10000);
   }

   @JvmStatic
   public fun <T> Array<out T>.sortedWith(comparator: Comparator<in T>): List<T> {
      return (java.util.List<T>)ArraysKt.asList(ArraysKt.sortedArrayWith(`$this$sortedWith`, comparator));
   }

   @JvmStatic
   public fun ByteArray.sortedWith(comparator: Comparator<in Byte>): List<Byte> {
      var var2: Array<java.lang.Byte> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun ShortArray.sortedWith(comparator: Comparator<in Short>): List<Short> {
      var var2: Array<java.lang.Short> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun IntArray.sortedWith(comparator: Comparator<in Int>): List<Int> {
      var var2: Array<Int> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun LongArray.sortedWith(comparator: Comparator<in Long>): List<Long> {
      var var2: Array<java.lang.Long> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun FloatArray.sortedWith(comparator: Comparator<in Float>): List<Float> {
      var var2: Array<java.lang.Float> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun DoubleArray.sortedWith(comparator: Comparator<in Double>): List<Double> {
      var var2: Array<java.lang.Double> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun BooleanArray.sortedWith(comparator: Comparator<in Boolean>): List<Boolean> {
      var var2: Array<java.lang.Boolean> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @JvmStatic
   public fun CharArray.sortedWith(comparator: Comparator<in Char>): List<Char> {
      var var2: Array<Character> = ArraysKt.toTypedArray(`$this$sortedWith`);
      ArraysKt.sortWith(var2, comparator);
      return ArraysKt.asList(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.isEmpty(): Boolean {
      return `$this$isEmpty`.length == 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.isNotEmpty(): Boolean {
      return `$this$isNotEmpty`.length != 0;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sortWith(`$this$sortDescending`, ComparisonsKt.reverseOrder(), fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.sortDescending(fromIndex: Int, toIndex: Int) {
      ArraysKt.sort(`$this$sortDescending`, fromIndex, toIndex);
      ArraysKt.reverse(`$this$sortDescending`, fromIndex, toIndex);
   }

   @JvmStatic
   public fun Array<out Boolean>.toBooleanArray(): BooleanArray {
      var var1: Int = 0;
      var var2: Int = `$this$toBooleanArray`.length;

      var var3: BooleanArray;
      for (var3 = new boolean[$this$toBooleanArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toBooleanArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Byte>.toByteArray(): ByteArray {
      var var1: Int = 0;
      var var2: Int = `$this$toByteArray`.length;

      var var3: ByteArray;
      for (var3 = new byte[$this$toByteArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toByteArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Char>.toCharArray(): CharArray {
      var var1: Int = 0;
      var var2: Int = `$this$toCharArray`.length;

      var var3: CharArray;
      for (var3 = new char[$this$toCharArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toCharArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Double>.toDoubleArray(): DoubleArray {
      var var1: Int = 0;
      var var2: Int = `$this$toDoubleArray`.length;

      var var3: DoubleArray;
      for (var3 = new double[$this$toDoubleArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toDoubleArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Float>.toFloatArray(): FloatArray {
      var var1: Int = 0;
      var var2: Int = `$this$toFloatArray`.length;

      var var3: FloatArray;
      for (var3 = new float[$this$toFloatArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toFloatArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Int>.toIntArray(): IntArray {
      var var1: Int = 0;
      var var2: Int = `$this$toIntArray`.length;

      var var3: IntArray;
      for (var3 = new int[$this$toIntArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toIntArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Long>.toLongArray(): LongArray {
      var var1: Int = 0;
      var var2: Int = `$this$toLongArray`.length;

      var var3: LongArray;
      for (var3 = new long[$this$toLongArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toLongArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public fun Array<out Short>.toShortArray(): ShortArray {
      var var1: Int = 0;
      var var2: Int = `$this$toShortArray`.length;

      var var3: ShortArray;
      for (var3 = new short[$this$toShortArray.length]; var1 < var2; var1++) {
         var3[var1] = `$this$toShortArray`[var1];
      }

      return var3;
   }

   @JvmStatic
   public inline fun <T, K, V> Array<out T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (Object element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ByteArray.associate(transform: (Byte) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (byte element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ShortArray.associate(transform: (Short) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (short element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> IntArray.associate(transform: (Int) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (int element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> LongArray.associate(transform: (Long) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (long element$iv : $this$associate) {
         var var12: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var12.getFirst(), var12.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> FloatArray.associate(transform: (Float) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (float element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> DoubleArray.associate(transform: (Double) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (double element$iv : $this$associate) {
         var var12: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var12.getFirst(), var12.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> BooleanArray.associate(transform: (Boolean) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (boolean element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> CharArray.associate(transform: (Char) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associate`.length), 16));

      for (char element$iv : $this$associate) {
         var var11: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var11.getFirst(), var11.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K> Array<out T>.associateBy(keySelector: (T) -> K): Map<K, T> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> ByteArray.associateBy(keySelector: (Byte) -> K): Map<K, Byte> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (byte element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> ShortArray.associateBy(keySelector: (Short) -> K): Map<K, Short> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (short element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> IntArray.associateBy(keySelector: (Int) -> K): Map<K, Int> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (int element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> LongArray.associateBy(keySelector: (Long) -> K): Map<K, Long> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (long element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> FloatArray.associateBy(keySelector: (Float) -> K): Map<K, Float> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (float element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> DoubleArray.associateBy(keySelector: (Double) -> K): Map<K, Double> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (double element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> BooleanArray.associateBy(keySelector: (Boolean) -> K): Map<K, Boolean> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (boolean element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> CharArray.associateBy(keySelector: (Char) -> K): Map<K, Char> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (char element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, V> Array<out T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ByteArray.associateBy(keySelector: (Byte) -> K, valueTransform: (Byte) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (byte element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ShortArray.associateBy(keySelector: (Short) -> K, valueTransform: (Short) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (short element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> IntArray.associateBy(keySelector: (Int) -> K, valueTransform: (Int) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (int element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> LongArray.associateBy(keySelector: (Long) -> K, valueTransform: (Long) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (long element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> FloatArray.associateBy(keySelector: (Float) -> K, valueTransform: (Float) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (float element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> DoubleArray.associateBy(keySelector: (Double) -> K, valueTransform: (Double) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (double element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> BooleanArray.associateBy(keySelector: (Boolean) -> K, valueTransform: (Boolean) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (boolean element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> CharArray.associateBy(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateBy`.length), 16));

      for (char element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, M : MutableMap<in K, in T>> Array<out T>.associateByTo(destination: M, keySelector: (T) -> K): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Byte>> ByteArray.associateByTo(destination: M, keySelector: (Byte) -> K): M {
      for (byte element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Short>> ShortArray.associateByTo(destination: M, keySelector: (Short) -> K): M {
      for (short element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Int>> IntArray.associateByTo(destination: M, keySelector: (Int) -> K): M {
      for (int element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Long>> LongArray.associateByTo(destination: M, keySelector: (Long) -> K): M {
      for (long element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Float>> FloatArray.associateByTo(destination: M, keySelector: (Float) -> K): M {
      for (float element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Double>> DoubleArray.associateByTo(destination: M, keySelector: (Double) -> K): M {
      for (double element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Boolean>> BooleanArray.associateByTo(destination: M, keySelector: (Boolean) -> K): M {
      for (boolean element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, in Char>> CharArray.associateByTo(destination: M, keySelector: (Char) -> K): M {
      for (char element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Array<out T>.associateByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> ByteArray.associateByTo(destination: M, keySelector: (Byte) -> K, valueTransform: (Byte) -> V): M {
      for (byte element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> ShortArray.associateByTo(destination: M, keySelector: (Short) -> K, valueTransform: (Short) -> V): M {
      for (short element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> IntArray.associateByTo(destination: M, keySelector: (Int) -> K, valueTransform: (Int) -> V): M {
      for (int element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> LongArray.associateByTo(destination: M, keySelector: (Long) -> K, valueTransform: (Long) -> V): M {
      for (long element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> FloatArray.associateByTo(destination: M, keySelector: (Float) -> K, valueTransform: (Float) -> V): M {
      for (float element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> DoubleArray.associateByTo(destination: M, keySelector: (Double) -> K, valueTransform: (Double) -> V): M {
      for (double element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> BooleanArray.associateByTo(destination: M, keySelector: (Boolean) -> K, valueTransform: (Boolean) -> V): M {
      for (boolean element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> CharArray.associateByTo(destination: M, keySelector: (Char) -> K, valueTransform: (Char) -> V): M {
      for (char element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Array<out T>.associateTo(destination: M, transform: (T) -> Pair<K, V>): M {
      for (Object element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> ByteArray.associateTo(destination: M, transform: (Byte) -> Pair<K, V>): M {
      for (byte element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> ShortArray.associateTo(destination: M, transform: (Short) -> Pair<K, V>): M {
      for (short element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> IntArray.associateTo(destination: M, transform: (Int) -> Pair<K, V>): M {
      for (int element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> LongArray.associateTo(destination: M, transform: (Long) -> Pair<K, V>): M {
      for (long element : $this$associateTo) {
         var var9: Pair = transform.invoke(element) as Pair;
         destination.put(var9.getFirst(), var9.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> FloatArray.associateTo(destination: M, transform: (Float) -> Pair<K, V>): M {
      for (float element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> DoubleArray.associateTo(destination: M, transform: (Double) -> Pair<K, V>): M {
      for (double element : $this$associateTo) {
         var var9: Pair = transform.invoke(element) as Pair;
         destination.put(var9.getFirst(), var9.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> BooleanArray.associateTo(destination: M, transform: (Boolean) -> Pair<K, V>): M {
      for (boolean element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> CharArray.associateTo(destination: M, transform: (Char) -> Pair<K, V>): M {
      for (char element : $this$associateTo) {
         var var8: Pair = transform.invoke(element) as Pair;
         destination.put(var8.getFirst(), var8.getSecond());
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <K, V> Array<out K>.associateWith(valueSelector: (K) -> V): Map<K, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (Object element$iv : $this$associateWith) {
         result.put(`element$iv`, valueSelector.invoke(`element$iv`));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> ByteArray.associateWith(valueSelector: (Byte) -> V): Map<Byte, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (byte var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> ShortArray.associateWith(valueSelector: (Short) -> V): Map<Short, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (short var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> IntArray.associateWith(valueSelector: (Int) -> V): Map<Int, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (int var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> LongArray.associateWith(valueSelector: (Long) -> V): Map<Long, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (long var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> FloatArray.associateWith(valueSelector: (Float) -> V): Map<Float, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (float var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> DoubleArray.associateWith(valueSelector: (Double) -> V): Map<Double, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (double var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> BooleanArray.associateWith(valueSelector: (Boolean) -> V): Map<Boolean, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith`.length), 16));

      for (boolean var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V> CharArray.associateWith(valueSelector: (Char) -> V): Map<Char, V> {
      var result: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$associateWith`.length, 128)), 16));

      for (char var6 : $this$associateWith) {
         result.put(var6, valueSelector.invoke(var6));
      }

      return result;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> Array<out K>.associateWithTo(destination: M, valueSelector: (K) -> V): M {
      for (Object element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Byte, in V>> ByteArray.associateWithTo(destination: M, valueSelector: (Byte) -> V): M {
      for (byte element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Short, in V>> ShortArray.associateWithTo(destination: M, valueSelector: (Short) -> V): M {
      for (short element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Int, in V>> IntArray.associateWithTo(destination: M, valueSelector: (Int) -> V): M {
      for (int element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Long, in V>> LongArray.associateWithTo(destination: M, valueSelector: (Long) -> V): M {
      for (long element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Float, in V>> FloatArray.associateWithTo(destination: M, valueSelector: (Float) -> V): M {
      for (float element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Double, in V>> DoubleArray.associateWithTo(destination: M, valueSelector: (Double) -> V): M {
      for (double element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Boolean, in V>> BooleanArray.associateWithTo(destination: M, valueSelector: (Boolean) -> V): M {
      for (boolean element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <V, M : MutableMap<in Char, in V>> CharArray.associateWithTo(destination: M, valueSelector: (Char) -> V): M {
      for (char element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public fun <T, C : MutableCollection<in T>> Array<out T>.toCollection(destination: C): C {
      for (Object item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Byte>> ByteArray.toCollection(destination: C): C {
      for (byte item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Short>> ShortArray.toCollection(destination: C): C {
      for (short item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Int>> IntArray.toCollection(destination: C): C {
      for (int item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Long>> LongArray.toCollection(destination: C): C {
      for (long item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Float>> FloatArray.toCollection(destination: C): C {
      for (float item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Double>> DoubleArray.toCollection(destination: C): C {
      for (double item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Boolean>> BooleanArray.toCollection(destination: C): C {
      for (boolean item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <C : MutableCollection<in Char>> CharArray.toCollection(destination: C): C {
      for (char item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Array<out T>.toHashSet(): HashSet<T> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet(MapsKt.mapCapacity(`$this$toHashSet`.length))) as HashSet<T>;
   }

   @JvmStatic
   public fun ByteArray.toHashSet(): HashSet<Byte> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun ShortArray.toHashSet(): HashSet<Short> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun IntArray.toHashSet(): HashSet<Int> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun LongArray.toHashSet(): HashSet<Long> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun FloatArray.toHashSet(): HashSet<Float> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun DoubleArray.toHashSet(): HashSet<Double> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun BooleanArray.toHashSet(): HashSet<Boolean> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(`$this$toHashSet`.length)));
   }

   @JvmStatic
   public fun CharArray.toHashSet(): HashSet<Char> {
      return ArraysKt.toCollection(`$this$toHashSet`, new HashSet<>(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$toHashSet`.length, 128))));
   }

   @JvmStatic
   public fun <T> Array<out T>.toList(): List<T> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(`$this$toList`[0]);
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun ByteArray.toList(): List<Byte> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Byte.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun ShortArray.toList(): List<Short> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Short.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun IntArray.toList(): List<Int> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(Integer.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun LongArray.toList(): List<Long> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Long.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun FloatArray.toList(): List<Float> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Float.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun DoubleArray.toList(): List<Double> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Double.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun BooleanArray.toList(): List<Boolean> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(java.lang.Boolean.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun CharArray.toList(): List<Char> {
      var var10000: java.util.List;
      switch ($this$toList.length) {
         case 0:
            var10000 = CollectionsKt.emptyList();
            break;
         case 1:
            var10000 = CollectionsKt.listOf(Character.valueOf(`$this$toList`[0]));
            break;
         default:
            var10000 = ArraysKt.toMutableList(`$this$toList`);
      }

      return var10000;
   }

   @JvmStatic
   public fun <T> Array<out T>.toMutableList(): MutableList<T> {
      return (java.util.List<T>)(new ArrayList<>(CollectionsKt.asCollection(`$this$toMutableList`)));
   }

   @JvmStatic
   public fun ByteArray.toMutableList(): MutableList<Byte> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (byte item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun ShortArray.toMutableList(): MutableList<Short> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (short item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun IntArray.toMutableList(): MutableList<Int> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (int item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun LongArray.toMutableList(): MutableList<Long> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (long item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun FloatArray.toMutableList(): MutableList<Float> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (float item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun DoubleArray.toMutableList(): MutableList<Double> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (double item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun BooleanArray.toMutableList(): MutableList<Boolean> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (boolean item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun CharArray.toMutableList(): MutableList<Char> {
      var list: ArrayList = new ArrayList(`$this$toMutableList`.length);

      for (char item : $this$toMutableList) {
         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun <T> Array<out T>.toSet(): Set<T> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(`$this$toSet`[0]);
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun ByteArray.toSet(): Set<Byte> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Byte.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun ShortArray.toSet(): Set<Short> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Short.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun IntArray.toSet(): Set<Int> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(Integer.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun LongArray.toSet(): Set<Long> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Long.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun FloatArray.toSet(): Set<Float> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Float.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun DoubleArray.toSet(): Set<Double> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Double.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun BooleanArray.toSet(): Set<Boolean> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(java.lang.Boolean.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toSet`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun CharArray.toSet(): Set<Char> {
      var var10000: java.util.Set;
      switch ($this$toSet.length) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(Character.valueOf(`$this$toSet`[0]));
            break;
         default:
            var10000 = ArraysKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$toSet`.length, 128))));
      }

      return var10000;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.flatMap(transform: (T) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ByteArray.flatMap(transform: (Byte) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (byte element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ShortArray.flatMap(transform: (Short) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (short element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> IntArray.flatMap(transform: (Int) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (int element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> LongArray.flatMap(transform: (Long) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (long element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> FloatArray.flatMap(transform: (Float) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (float element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.flatMap(transform: (Double) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (double element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.flatMap(transform: (Boolean) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (boolean element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> CharArray.flatMap(transform: (Char) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (char element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequence")
   @JvmStatic
   public inline fun <T, R> Array<out T>.flatMap(transform: (T) -> Sequence<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as Sequence);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.flatMapIndexed(transform: (Int, T) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (Object var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.flatMapIndexed(transform: (Int, Byte) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (byte var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.flatMapIndexed(transform: (Int, Short) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (short var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.flatMapIndexed(transform: (Int, Int) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (int var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.flatMapIndexed(transform: (Int, Long) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (long var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.flatMapIndexed(transform: (Int, Float) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (float var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.flatMapIndexed(transform: (Int, Double) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (double var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.flatMapIndexed(transform: (Int, Boolean) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (boolean var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.flatMapIndexed(transform: (Int, Char) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (char var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedSequence")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.flatMapIndexed(transform: (Int, T) -> Sequence<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (Object var7 : $this$flatMapIndexed) {
         CollectionsKt.addAll(var3, transform.invoke(var4++, var7) as Sequence);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Iterable<R>): C {
      var index: Int = 0;

      for (Object element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ByteArray.flatMapIndexedTo(destination: C, transform: (Int, Byte) -> Iterable<R>): C {
      var index: Int = 0;

      for (byte element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ShortArray.flatMapIndexedTo(destination: C, transform: (Int, Short) -> Iterable<R>): C {
      var index: Int = 0;

      for (short element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> IntArray.flatMapIndexedTo(destination: C, transform: (Int, Int) -> Iterable<R>): C {
      var index: Int = 0;

      for (int element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> LongArray.flatMapIndexedTo(destination: C, transform: (Int, Long) -> Iterable<R>): C {
      var index: Int = 0;

      for (long element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> FloatArray.flatMapIndexedTo(destination: C, transform: (Int, Float) -> Iterable<R>): C {
      var index: Int = 0;

      for (float element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> DoubleArray.flatMapIndexedTo(destination: C, transform: (Int, Double) -> Iterable<R>): C {
      var index: Int = 0;

      for (double element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> BooleanArray.flatMapIndexedTo(destination: C, transform: (Int, Boolean) -> Iterable<R>): C {
      var index: Int = 0;

      for (boolean element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharArray.flatMapIndexedTo(destination: C, transform: (Int, Char) -> Iterable<R>): C {
      var index: Int = 0;

      for (char element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedSequenceTo")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Sequence<R>): C {
      var index: Int = 0;

      for (Object element : $this$flatMapIndexedTo) {
         CollectionsKt.addAll(destination, transform.invoke(index++, element) as Sequence);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapTo(destination: C, transform: (T) -> Iterable<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ByteArray.flatMapTo(destination: C, transform: (Byte) -> Iterable<R>): C {
      for (byte element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ShortArray.flatMapTo(destination: C, transform: (Short) -> Iterable<R>): C {
      for (short element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> IntArray.flatMapTo(destination: C, transform: (Int) -> Iterable<R>): C {
      for (int element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> LongArray.flatMapTo(destination: C, transform: (Long) -> Iterable<R>): C {
      for (long element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> FloatArray.flatMapTo(destination: C, transform: (Float) -> Iterable<R>): C {
      for (float element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> DoubleArray.flatMapTo(destination: C, transform: (Double) -> Iterable<R>): C {
      for (double element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> BooleanArray.flatMapTo(destination: C, transform: (Boolean) -> Iterable<R>): C {
      for (boolean element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharArray.flatMapTo(destination: C, transform: (Char) -> Iterable<R>): C {
      for (char element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequenceTo")
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.flatMapTo(destination: C, transform: (T) -> Sequence<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as Sequence);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, K> Array<out T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> ByteArray.groupBy(keySelector: (Byte) -> K): Map<K, List<Byte>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (byte element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> ShortArray.groupBy(keySelector: (Short) -> K): Map<K, List<Short>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (short element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> IntArray.groupBy(keySelector: (Int) -> K): Map<K, List<Int>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (int element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> LongArray.groupBy(keySelector: (Long) -> K): Map<K, List<Long>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (long element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> FloatArray.groupBy(keySelector: (Float) -> K): Map<K, List<Float>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (float element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> DoubleArray.groupBy(keySelector: (Double) -> K): Map<K, List<Double>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (double element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> BooleanArray.groupBy(keySelector: (Boolean) -> K): Map<K, List<Boolean>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (boolean element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K> CharArray.groupBy(keySelector: (Char) -> K): Map<K, List<Char>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (char element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, V> Array<out T>.groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ByteArray.groupBy(keySelector: (Byte) -> K, valueTransform: (Byte) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (byte element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> ShortArray.groupBy(keySelector: (Short) -> K, valueTransform: (Short) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (short element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> IntArray.groupBy(keySelector: (Int) -> K, valueTransform: (Int) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (int element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> LongArray.groupBy(keySelector: (Long) -> K, valueTransform: (Long) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (long element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var17: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var17);
            var10000 = var17;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> FloatArray.groupBy(keySelector: (Float) -> K, valueTransform: (Float) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (float element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> DoubleArray.groupBy(keySelector: (Double) -> K, valueTransform: (Double) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (double element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var17: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var17);
            var10000 = var17;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> BooleanArray.groupBy(keySelector: (Boolean) -> K, valueTransform: (Boolean) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (boolean element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> CharArray.groupBy(keySelector: (Char) -> K, valueTransform: (Char) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (char element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var16: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var16);
            var10000 = var16;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Array<out T>.groupByTo(destination: M, keySelector: (T) -> K): M {
      for (Object element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Byte>>> ByteArray.groupByTo(destination: M, keySelector: (Byte) -> K): M {
      for (byte element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Short>>> ShortArray.groupByTo(destination: M, keySelector: (Short) -> K): M {
      for (short element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Int>>> IntArray.groupByTo(destination: M, keySelector: (Int) -> K): M {
      for (int element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Long>>> LongArray.groupByTo(destination: M, keySelector: (Long) -> K): M {
      for (long element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Float>>> FloatArray.groupByTo(destination: M, keySelector: (Float) -> K): M {
      for (float element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Double>>> DoubleArray.groupByTo(destination: M, keySelector: (Double) -> K): M {
      for (double element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Boolean>>> BooleanArray.groupByTo(destination: M, keySelector: (Boolean) -> K): M {
      for (boolean element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, M : MutableMap<in K, MutableList<Char>>> CharArray.groupByTo(destination: M, keySelector: (Char) -> K): M {
      for (char element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Array<out T>.groupByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
      for (Object element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ByteArray.groupByTo(destination: M, keySelector: (Byte) -> K, valueTransform: (Byte) -> V): M {
      for (byte element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> ShortArray.groupByTo(destination: M, keySelector: (Short) -> K, valueTransform: (Short) -> V): M {
      for (short element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> IntArray.groupByTo(destination: M, keySelector: (Int) -> K, valueTransform: (Int) -> V): M {
      for (int element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> LongArray.groupByTo(destination: M, keySelector: (Long) -> K, valueTransform: (Long) -> V): M {
      for (long element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var15: Any = new ArrayList();
            destination.put(key, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> FloatArray.groupByTo(destination: M, keySelector: (Float) -> K, valueTransform: (Float) -> V): M {
      for (float element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> DoubleArray.groupByTo(
      destination: M,
      keySelector: (Double) -> K,
      valueTransform: (Double) -> V
   ): M {
      for (double element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var15: Any = new ArrayList();
            destination.put(key, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> BooleanArray.groupByTo(
      destination: M,
      keySelector: (Boolean) -> K,
      valueTransform: (Boolean) -> V
   ): M {
      for (boolean element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, MutableList<V>>> CharArray.groupByTo(destination: M, keySelector: (Char) -> K, valueTransform: (Char) -> V): M {
      for (char element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var14: Any = new ArrayList();
            destination.put(key, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K> Array<out T>.groupingBy(crossinline keySelector: (T) -> K): Grouping<T, K> {
      return new Grouping<T, K>(`$this$groupingBy`, keySelector) {
         {
            this.$this_groupingBy = (T[])`$receiver`;
            this.$keySelector = `$keySelector`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> sourceIterator() {
            return ArrayIteratorKt.iterator(this.$this_groupingBy);
         }

         @Override
         public K keyOf(T element) {
            return (K)this.$keySelector.invoke(element);
         }
      };
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.map(transform: (T) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (Object item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ByteArray.map(transform: (Byte) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (byte item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ShortArray.map(transform: (Short) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (short item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> IntArray.map(transform: (Int) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (int item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> LongArray.map(transform: (Long) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (long item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> FloatArray.map(transform: (Float) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (float item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.map(transform: (Double) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (double item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.map(transform: (Boolean) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (boolean item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> CharArray.map(transform: (Char) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.length);

      for (char item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.mapIndexed(transform: (Int, T) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ByteArray.mapIndexed(transform: (Int, Byte) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (byte item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> ShortArray.mapIndexed(transform: (Int, Short) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (short item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> IntArray.mapIndexed(transform: (Int, Int) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (int item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> LongArray.mapIndexed(transform: (Int, Long) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (long item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> FloatArray.mapIndexed(transform: (Int, Float) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (float item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.mapIndexed(transform: (Int, Double) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (double item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.mapIndexed(transform: (Int, Boolean) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (boolean item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <R> CharArray.mapIndexed(transform: (Int, Char) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$mapIndexed`.length);
      var `index$iv`: Int = 0;

      for (char item$iv : $this$mapIndexed) {
         `destination$iv`.add(transform.invoke(`index$iv`++, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any> Array<out T>.mapIndexedNotNull(transform: (Int, T) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (Object item$iv$iv : $this$mapIndexedNotNull) {
         var var18: Any = transform.invoke(`index$iv$iv`++, `item$iv$iv`);
         if (var18 != null) {
            `destination$iv`.add(var18);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Array<out T>.mapIndexedNotNullTo(destination: C, transform: (Int, T) -> R?): C {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$mapIndexedNotNullTo) {
         var var16: Any = transform.invoke(`index$iv`++, `item$iv`);
         if (var16 != null) {
            destination.add(var16);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.mapIndexedTo(destination: C, transform: (Int, T) -> R): C {
      var index: Int = 0;

      for (Object item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ByteArray.mapIndexedTo(destination: C, transform: (Int, Byte) -> R): C {
      var index: Int = 0;

      for (byte item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ShortArray.mapIndexedTo(destination: C, transform: (Int, Short) -> R): C {
      var index: Int = 0;

      for (short item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> IntArray.mapIndexedTo(destination: C, transform: (Int, Int) -> R): C {
      var index: Int = 0;

      for (int item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> LongArray.mapIndexedTo(destination: C, transform: (Int, Long) -> R): C {
      var index: Int = 0;

      for (long item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> FloatArray.mapIndexedTo(destination: C, transform: (Int, Float) -> R): C {
      var index: Int = 0;

      for (float item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> DoubleArray.mapIndexedTo(destination: C, transform: (Int, Double) -> R): C {
      var index: Int = 0;

      for (double item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> BooleanArray.mapIndexedTo(destination: C, transform: (Int, Boolean) -> R): C {
      var index: Int = 0;

      for (boolean item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharArray.mapIndexedTo(destination: C, transform: (Int, Char) -> R): C {
      var index: Int = 0;

      for (char item : $this$mapIndexedTo) {
         destination.add(transform.invoke(index++, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R : Any> Array<out T>.mapNotNull(transform: (T) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$mapNotNull) {
         var var10000: Any = transform.invoke(`element$iv$iv`);
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Array<out T>.mapNotNullTo(destination: C, transform: (T) -> R?): C {
      for (Object element$iv : $this$mapNotNullTo) {
         var var10000: Any = transform.invoke(`element$iv`);
         if (var10000 != null) {
            destination.add(var10000);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Array<out T>.mapTo(destination: C, transform: (T) -> R): C {
      for (Object item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ByteArray.mapTo(destination: C, transform: (Byte) -> R): C {
      for (byte item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> ShortArray.mapTo(destination: C, transform: (Short) -> R): C {
      for (short item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> IntArray.mapTo(destination: C, transform: (Int) -> R): C {
      for (int item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> LongArray.mapTo(destination: C, transform: (Long) -> R): C {
      for (long item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> FloatArray.mapTo(destination: C, transform: (Float) -> R): C {
      for (float item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> DoubleArray.mapTo(destination: C, transform: (Double) -> R): C {
      for (double item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> BooleanArray.mapTo(destination: C, transform: (Boolean) -> R): C {
      for (boolean item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <R, C : MutableCollection<in R>> CharArray.mapTo(destination: C, transform: (Char) -> R): C {
      for (char item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Array<out T>.withIndex(): Iterable<IndexedValue<T>> {
      return new IndexingIterable((new Function0<java.util.Iterator<? extends T>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = (T[])`$receiver`;
         }

         @NotNull
         public final java.util.Iterator<T> invoke() {
            return ArrayIteratorKt.iterator(this.$this_withIndex);
         }
      }) as Function0);
   }

   @JvmStatic
   public fun ByteArray.withIndex(): Iterable<IndexedValue<Byte>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Byte>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Byte> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Byte>>);
   }

   @JvmStatic
   public fun ShortArray.withIndex(): Iterable<IndexedValue<Short>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Short>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Short> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Short>>);
   }

   @JvmStatic
   public fun IntArray.withIndex(): Iterable<IndexedValue<Int>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends Integer>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<Integer> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends Integer>>);
   }

   @JvmStatic
   public fun LongArray.withIndex(): Iterable<IndexedValue<Long>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Long>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Long> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Long>>);
   }

   @JvmStatic
   public fun FloatArray.withIndex(): Iterable<IndexedValue<Float>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Float>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Float> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Float>>);
   }

   @JvmStatic
   public fun DoubleArray.withIndex(): Iterable<IndexedValue<Double>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Double>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Double> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Double>>);
   }

   @JvmStatic
   public fun BooleanArray.withIndex(): Iterable<IndexedValue<Boolean>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends java.lang.Boolean>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<java.lang.Boolean> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends java.lang.Boolean>>);
   }

   @JvmStatic
   public fun CharArray.withIndex(): Iterable<IndexedValue<Char>> {
      return new IndexingIterable<>((new Function0<java.util.Iterator<? extends Character>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<Character> invoke() {
            return ArrayIteratorsKt.iterator(this.$this_withIndex);
         }
      }) as Function0<? extends java.util.Iterator<? extends Character>>);
   }

   @JvmStatic
   public fun <T> Array<out T>.distinct(): List<T> {
      return (java.util.List<T>)CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun ByteArray.distinct(): List<Byte> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun ShortArray.distinct(): List<Short> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun IntArray.distinct(): List<Int> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun LongArray.distinct(): List<Long> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun FloatArray.distinct(): List<Float> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun DoubleArray.distinct(): List<Double> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun BooleanArray.distinct(): List<Boolean> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public fun CharArray.distinct(): List<Char> {
      return CollectionsKt.toList(ArraysKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public inline fun <T, K> Array<out T>.distinctBy(selector: (T) -> K): List<T> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (Object e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> ByteArray.distinctBy(selector: (Byte) -> K): List<Byte> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (byte e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> ShortArray.distinctBy(selector: (Short) -> K): List<Short> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (short e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> IntArray.distinctBy(selector: (Int) -> K): List<Int> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (int e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> LongArray.distinctBy(selector: (Long) -> K): List<Long> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (long e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> FloatArray.distinctBy(selector: (Float) -> K): List<Float> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (float e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> DoubleArray.distinctBy(selector: (Double) -> K): List<Double> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (double e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> BooleanArray.distinctBy(selector: (Boolean) -> K): List<Boolean> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (boolean e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <K> CharArray.distinctBy(selector: (Char) -> K): List<Char> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (char e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public infix fun <T> Array<out T>.intersect(other: Iterable<T>): Set<T> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ByteArray.intersect(other: Iterable<Byte>): Set<Byte> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ShortArray.intersect(other: Iterable<Short>): Set<Short> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun IntArray.intersect(other: Iterable<Int>): Set<Int> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun LongArray.intersect(other: Iterable<Long>): Set<Long> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun FloatArray.intersect(other: Iterable<Float>): Set<Float> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun DoubleArray.intersect(other: Iterable<Double>): Set<Double> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun BooleanArray.intersect(other: Iterable<Boolean>): Set<Boolean> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun CharArray.intersect(other: Iterable<Char>): Set<Char> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun <T> Array<out T>.subtract(other: Iterable<T>): Set<T> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ByteArray.subtract(other: Iterable<Byte>): Set<Byte> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ShortArray.subtract(other: Iterable<Short>): Set<Short> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun IntArray.subtract(other: Iterable<Int>): Set<Int> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun LongArray.subtract(other: Iterable<Long>): Set<Long> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun FloatArray.subtract(other: Iterable<Float>): Set<Float> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun DoubleArray.subtract(other: Iterable<Double>): Set<Double> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun BooleanArray.subtract(other: Iterable<Boolean>): Set<Boolean> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun CharArray.subtract(other: Iterable<Char>): Set<Char> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public fun <T> Array<out T>.toMutableSet(): MutableSet<T> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet(MapsKt.mapCapacity(`$this$toMutableSet`.length))) as java.util.Set<T>;
   }

   @JvmStatic
   public fun ByteArray.toMutableSet(): MutableSet<Byte> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun ShortArray.toMutableSet(): MutableSet<Short> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun IntArray.toMutableSet(): MutableSet<Int> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun LongArray.toMutableSet(): MutableSet<Long> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun FloatArray.toMutableSet(): MutableSet<Float> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun DoubleArray.toMutableSet(): MutableSet<Double> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun BooleanArray.toMutableSet(): MutableSet<Boolean> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(`$this$toMutableSet`.length)));
   }

   @JvmStatic
   public fun CharArray.toMutableSet(): MutableSet<Char> {
      return ArraysKt.toCollection(`$this$toMutableSet`, new LinkedHashSet<>(MapsKt.mapCapacity(RangesKt.coerceAtMost(`$this$toMutableSet`.length, 128))));
   }

   @JvmStatic
   public infix fun <T> Array<out T>.union(other: Iterable<T>): Set<T> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ByteArray.union(other: Iterable<Byte>): Set<Byte> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun ShortArray.union(other: Iterable<Short>): Set<Short> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun IntArray.union(other: Iterable<Int>): Set<Int> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun LongArray.union(other: Iterable<Long>): Set<Long> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun FloatArray.union(other: Iterable<Float>): Set<Float> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun DoubleArray.union(other: Iterable<Double>): Set<Double> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun BooleanArray.union(other: Iterable<Boolean>): Set<Boolean> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun CharArray.union(other: Iterable<Char>): Set<Char> {
      var set: java.util.Set = ArraysKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.all(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun ByteArray.all(predicate: (Byte) -> Boolean): Boolean {
      for (byte element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun ShortArray.all(predicate: (Short) -> Boolean): Boolean {
      for (short element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun IntArray.all(predicate: (Int) -> Boolean): Boolean {
      for (int element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun LongArray.all(predicate: (Long) -> Boolean): Boolean {
      for (long element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun FloatArray.all(predicate: (Float) -> Boolean): Boolean {
      for (float element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun DoubleArray.all(predicate: (Double) -> Boolean): Boolean {
      for (double element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun BooleanArray.all(predicate: (Boolean) -> Boolean): Boolean {
      for (boolean element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun CharArray.all(predicate: (Char) -> Boolean): Boolean {
      for (char element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public fun <T> Array<out T>.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun ByteArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun ShortArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun IntArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun LongArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun FloatArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun DoubleArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun BooleanArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public fun CharArray.any(): Boolean {
      return `$this$any`.length != 0;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.any(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun ByteArray.any(predicate: (Byte) -> Boolean): Boolean {
      for (byte element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun ShortArray.any(predicate: (Short) -> Boolean): Boolean {
      for (short element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun IntArray.any(predicate: (Int) -> Boolean): Boolean {
      for (int element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun LongArray.any(predicate: (Long) -> Boolean): Boolean {
      for (long element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun FloatArray.any(predicate: (Float) -> Boolean): Boolean {
      for (float element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun DoubleArray.any(predicate: (Double) -> Boolean): Boolean {
      for (double element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun BooleanArray.any(predicate: (Boolean) -> Boolean): Boolean {
      for (boolean element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public inline fun CharArray.any(predicate: (Char) -> Boolean): Boolean {
      for (char element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun IntArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun LongArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.count(): Int {
      return `$this$count`.length;
   }

   @InlineOnly
   @JvmStatic
   public inline fun CharArray.count(): Int {
      return `$this$count`.length;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.count(predicate: (T) -> Boolean): Int {
      var count: Int = 0;

      for (Object element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun ByteArray.count(predicate: (Byte) -> Boolean): Int {
      var count: Int = 0;

      for (byte element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun ShortArray.count(predicate: (Short) -> Boolean): Int {
      var count: Int = 0;

      for (short element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun IntArray.count(predicate: (Int) -> Boolean): Int {
      var count: Int = 0;

      for (int element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun LongArray.count(predicate: (Long) -> Boolean): Int {
      var count: Int = 0;

      for (long element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun FloatArray.count(predicate: (Float) -> Boolean): Int {
      var count: Int = 0;

      for (float element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun DoubleArray.count(predicate: (Double) -> Boolean): Int {
      var count: Int = 0;

      for (double element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun BooleanArray.count(predicate: (Boolean) -> Boolean): Int {
      var count: Int = 0;

      for (boolean element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun CharArray.count(predicate: (Char) -> Boolean): Int {
      var count: Int = 0;

      for (char element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            count++;
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.fold(initial: R, operation: (R, T) -> R): R {
      var accumulator: Any = initial;

      for (Object element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ByteArray.fold(initial: R, operation: (R, Byte) -> R): R {
      var accumulator: Any = initial;

      for (byte element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ShortArray.fold(initial: R, operation: (R, Short) -> R): R {
      var accumulator: Any = initial;

      for (short element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> IntArray.fold(initial: R, operation: (R, Int) -> R): R {
      var accumulator: Any = initial;

      for (int element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> LongArray.fold(initial: R, operation: (R, Long) -> R): R {
      var accumulator: Any = initial;

      for (long element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> FloatArray.fold(initial: R, operation: (R, Float) -> R): R {
      var accumulator: Any = initial;

      for (float element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.fold(initial: R, operation: (R, Double) -> R): R {
      var accumulator: Any = initial;

      for (double element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.fold(initial: R, operation: (R, Boolean) -> R): R {
      var accumulator: Any = initial;

      for (boolean element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharArray.fold(initial: R, operation: (R, Char) -> R): R {
      var accumulator: Any = initial;

      for (char element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.foldIndexed(initial: R, operation: (Int, R, T) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (Object element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ByteArray.foldIndexed(initial: R, operation: (Int, R, Byte) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (byte element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ShortArray.foldIndexed(initial: R, operation: (Int, R, Short) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (short element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> IntArray.foldIndexed(initial: R, operation: (Int, R, Int) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (int element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> LongArray.foldIndexed(initial: R, operation: (Int, R, Long) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (long element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> FloatArray.foldIndexed(initial: R, operation: (Int, R, Float) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (float element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.foldIndexed(initial: R, operation: (Int, R, Double) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (double element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.foldIndexed(initial: R, operation: (Int, R, Boolean) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (boolean element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharArray.foldIndexed(initial: R, operation: (Int, R, Char) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (char element : $this$foldIndexed) {
         accumulator = operation.invoke(index++, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.foldRight(initial: R, operation: (T, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ByteArray.foldRight(initial: R, operation: (Byte, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ShortArray.foldRight(initial: R, operation: (Short, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> IntArray.foldRight(initial: R, operation: (Int, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> LongArray.foldRight(initial: R, operation: (Long, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> FloatArray.foldRight(initial: R, operation: (Float, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.foldRight(initial: R, operation: (Double, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.foldRight(initial: R, operation: (Boolean, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharArray.foldRight(initial: R, operation: (Char, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRight`);
      var accumulator: Any = initial;

      while (index >= 0) {
         accumulator = operation.invoke(`$this$foldRight`[index--], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> Array<out T>.foldRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ByteArray.foldRightIndexed(initial: R, operation: (Int, Byte, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> ShortArray.foldRightIndexed(initial: R, operation: (Int, Short, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> IntArray.foldRightIndexed(initial: R, operation: (Int, Int, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> LongArray.foldRightIndexed(initial: R, operation: (Int, Long, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> FloatArray.foldRightIndexed(initial: R, operation: (Int, Float, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> DoubleArray.foldRightIndexed(initial: R, operation: (Int, Double, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> BooleanArray.foldRightIndexed(initial: R, operation: (Int, Boolean, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <R> CharArray.foldRightIndexed(initial: R, operation: (Int, Char, R) -> R): R {
      var index: Int = ArraysKt.getLastIndex(`$this$foldRightIndexed`);

      var accumulator: Any;
      for (accumulator = initial; index >= 0; index--) {
         accumulator = operation.invoke(index, `$this$foldRightIndexed`[index], accumulator);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.forEach(action: (T) -> Unit) {
      for (Object element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun ByteArray.forEach(action: (Byte) -> Unit) {
      for (byte element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun ShortArray.forEach(action: (Short) -> Unit) {
      for (short element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun IntArray.forEach(action: (Int) -> Unit) {
      for (int element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun LongArray.forEach(action: (Long) -> Unit) {
      for (long element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun FloatArray.forEach(action: (Float) -> Unit) {
      for (float element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun DoubleArray.forEach(action: (Double) -> Unit) {
      for (double element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun BooleanArray.forEach(action: (Boolean) -> Unit) {
      for (boolean element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun CharArray.forEach(action: (Char) -> Unit) {
      for (char element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun <T> Array<out T>.forEachIndexed(action: (Int, T) -> Unit) {
      var index: Int = 0;

      for (Object item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun ByteArray.forEachIndexed(action: (Int, Byte) -> Unit) {
      var index: Int = 0;

      for (byte item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun ShortArray.forEachIndexed(action: (Int, Short) -> Unit) {
      var index: Int = 0;

      for (short item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun IntArray.forEachIndexed(action: (Int, Int) -> Unit) {
      var index: Int = 0;

      for (int item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun LongArray.forEachIndexed(action: (Int, Long) -> Unit) {
      var index: Int = 0;

      for (long item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun FloatArray.forEachIndexed(action: (Int, Float) -> Unit) {
      var index: Int = 0;

      for (float item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun DoubleArray.forEachIndexed(action: (Int, Double) -> Unit) {
      var index: Int = 0;

      for (double item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun BooleanArray.forEachIndexed(action: (Int, Boolean) -> Unit) {
      var index: Int = 0;

      for (boolean item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @JvmStatic
   public inline fun CharArray.forEachIndexed(action: (Int, Char) -> Unit) {
      var index: Int = 0;

      for (char item : $this$forEachIndexed) {
         action.invoke(index++, item);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun Array<out Double>.max(): Double {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Double = `$this$max`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var3.hasNext()) {
            max = Math.max(max, `$this$max`[var3.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun Array<out Float>.max(): Float {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Float = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            max = Math.max(max, `$this$max`[var2.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.max(): T {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: java.lang.Comparable = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: java.lang.Comparable = `$this$max`[var2.nextInt()];
            if (max.compareTo(e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun ByteArray.max(): Byte {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Byte = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: Byte = `$this$max`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun ShortArray.max(): Short {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Short = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: Short = `$this$max`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun IntArray.max(): Int {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Int = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: Int = `$this$max`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun LongArray.max(): Long {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Long = `$this$max`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var3.hasNext()) {
            var e: Long = `$this$max`[var3.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun FloatArray.max(): Float {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Float = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            max = Math.max(max, `$this$max`[var2.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun DoubleArray.max(): Double {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Double = `$this$max`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var3.hasNext()) {
            max = Math.max(max, `$this$max`[var3.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun CharArray.max(): Char {
      if (`$this$max`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Char = `$this$max`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$max`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$max`[var2.nextInt()];
            if (Intrinsics.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.maxBy(selector: (T) -> R): T {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Any = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return (T)maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Any = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return (T)maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.maxBy(selector: (Byte) -> R): Byte {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Byte = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Byte = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.maxBy(selector: (Short) -> R): Short {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Short = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Short = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.maxBy(selector: (Int) -> R): Int {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Int = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Int = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.maxBy(selector: (Long) -> R): Long {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Long = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Long = `$this$maxBy`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.maxBy(selector: (Float) -> R): Float {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Float = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Float = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.maxBy(selector: (Double) -> R): Double {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Double = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Double = `$this$maxBy`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.maxBy(selector: (Boolean) -> R): Boolean {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Boolean = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Boolean = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.maxBy(selector: (Char) -> R): Char {
      if (`$this$maxBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Char = `$this$maxBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxBy`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$maxBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.maxByOrNull(selector: (T) -> R): T? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Any = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return (T)maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Any = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            }

            return (T)maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.maxByOrNull(selector: (Byte) -> R): Byte? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Byte = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Byte = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.maxByOrNull(selector: (Short) -> R): Short? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Short = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Short = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.maxByOrNull(selector: (Int) -> R): Int? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Int = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Int = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.maxByOrNull(selector: (Long) -> R): Long? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Long = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Long = `$this$maxByOrNull`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.maxByOrNull(selector: (Float) -> R): Float? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Float = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Float = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.maxByOrNull(selector: (Double) -> R): Double? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Double = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Double = `$this$maxByOrNull`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.maxByOrNull(selector: (Boolean) -> R): Boolean? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Boolean = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Boolean = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.maxByOrNull(selector: (Char) -> R): Char? {
      if (`$this$maxByOrNull`.length == 0) {
         return null;
      } else {
         var maxElem: Char = `$this$maxByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$maxByOrNull`);
         if (lastIndex == 0) {
            return maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$maxByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.maxOf(selector: (T) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.maxOf(selector: (Byte) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.maxOf(selector: (Short) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.maxOf(selector: (Int) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.maxOf(selector: (Long) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.maxOf(selector: (Float) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.maxOf(selector: (Double) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.maxOf(selector: (Boolean) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.maxOf(selector: (Char) -> Double): Double {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.maxOf(selector: (T) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.maxOf(selector: (Byte) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.maxOf(selector: (Short) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.maxOf(selector: (Int) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.maxOf(selector: (Long) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.maxOf(selector: (Float) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.maxOf(selector: (Double) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.maxOf(selector: (Boolean) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.maxOf(selector: (Char) -> Float): Float {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.maxOf(selector: (T) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.maxOf(selector: (Byte) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.maxOf(selector: (Short) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.maxOf(selector: (Int) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.maxOf(selector: (Long) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.maxOf(selector: (Float) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.maxOf(selector: (Double) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.maxOf(selector: (Boolean) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.maxOf(selector: (Char) -> R): R {
      if (`$this$maxOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOf`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.maxOfOrNull(selector: (T) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.maxOfOrNull(selector: (Byte) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.maxOfOrNull(selector: (Short) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.maxOfOrNull(selector: (Int) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.maxOfOrNull(selector: (Long) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.maxOfOrNull(selector: (Float) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.maxOfOrNull(selector: (Double) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.maxOfOrNull(selector: (Boolean) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.maxOfOrNull(selector: (Char) -> Double): Double? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var4.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.maxOfOrNull(selector: (T) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.maxOfOrNull(selector: (Byte) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.maxOfOrNull(selector: (Short) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.maxOfOrNull(selector: (Int) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.maxOfOrNull(selector: (Long) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.maxOfOrNull(selector: (Float) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.maxOfOrNull(selector: (Double) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.maxOfOrNull(selector: (Boolean) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.maxOfOrNull(selector: (Char) -> Float): Float? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.maxOfOrNull(selector: (T) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.maxOfOrNull(selector: (Byte) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.maxOfOrNull(selector: (Short) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.maxOfOrNull(selector: (Int) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.maxOfOrNull(selector: (Long) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.maxOfOrNull(selector: (Float) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.maxOfOrNull(selector: (Double) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.maxOfOrNull(selector: (Boolean) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.maxOfOrNull(selector: (Char) -> R): R? {
      if (`$this$maxOfOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$maxOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.maxOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.maxOfWith(comparator: Comparator<in R>, selector: (Byte) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.maxOfWith(comparator: Comparator<in R>, selector: (Short) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.maxOfWith(comparator: Comparator<in R>, selector: (Int) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.maxOfWith(comparator: Comparator<in R>, selector: (Long) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.maxOfWith(comparator: Comparator<in R>, selector: (Float) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.maxOfWith(comparator: Comparator<in R>, selector: (Double) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.maxOfWith(comparator: Comparator<in R>, selector: (Boolean) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.maxOfWith(comparator: Comparator<in R>, selector: (Char) -> R): R {
      if (`$this$maxOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWith`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.maxOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Byte) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Short) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Int) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Long) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Float) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Double) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Boolean) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Char) -> R): R? {
      if (`$this$maxOfWithOrNull`.length == 0) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(`$this$maxOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$maxOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Array<out Double>.maxOrNull(): Double? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Double = `$this$maxOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var3.hasNext()) {
            max = Math.max(max, `$this$maxOrNull`[var3.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Array<out Float>.maxOrNull(): Float? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Float = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            max = Math.max(max, `$this$maxOrNull`[var2.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.maxOrNull(): T? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: java.lang.Comparable = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: java.lang.Comparable = `$this$maxOrNull`[var2.nextInt()];
            if (max.compareTo(e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.maxOrNull(): Byte? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Byte = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Byte = `$this$maxOrNull`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.maxOrNull(): Short? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Short = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Short = `$this$maxOrNull`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.maxOrNull(): Int? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Int = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Int = `$this$maxOrNull`[var2.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.maxOrNull(): Long? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Long = `$this$maxOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Long = `$this$maxOrNull`[var3.nextInt()];
            if (max < e) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.maxOrNull(): Float? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Float = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            max = Math.max(max, `$this$maxOrNull`[var2.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.maxOrNull(): Double? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Double = `$this$maxOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var3.hasNext()) {
            max = Math.max(max, `$this$maxOrNull`[var3.nextInt()]);
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.maxOrNull(): Char? {
      if (`$this$maxOrNull`.length == 0) {
         return null;
      } else {
         var max: Char = `$this$maxOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$maxOrNull`[var2.nextInt()];
            if (Intrinsics.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun <T> Array<out T>.maxWith(comparator: Comparator<in T>): T {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Any = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Any = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun ByteArray.maxWith(comparator: Comparator<in Byte>): Byte {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Byte = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Byte = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun ShortArray.maxWith(comparator: Comparator<in Short>): Short {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Short = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Short = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun IntArray.maxWith(comparator: Comparator<in Int>): Int {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Int = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Int = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun LongArray.maxWith(comparator: Comparator<in Long>): Long {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Long = `$this$maxWith`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var4.hasNext()) {
            var e: Long = `$this$maxWith`[var4.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun FloatArray.maxWith(comparator: Comparator<in Float>): Float {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Float = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Float = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun DoubleArray.maxWith(comparator: Comparator<in Double>): Double {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Double = `$this$maxWith`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var4.hasNext()) {
            var e: Double = `$this$maxWith`[var4.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun BooleanArray.maxWith(comparator: Comparator<in Boolean>): Boolean {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Boolean = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Boolean = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun CharArray.maxWith(comparator: Comparator<in Char>): Char {
      if (`$this$maxWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var max: Char = `$this$maxWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWith`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$maxWith`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Array<out T>.maxWithOrNull(comparator: Comparator<in T>): T? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Any = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Any = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.maxWithOrNull(comparator: Comparator<in Byte>): Byte? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Byte = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Byte = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.maxWithOrNull(comparator: Comparator<in Short>): Short? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Short = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Short = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.maxWithOrNull(comparator: Comparator<in Int>): Int? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Int = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Int = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.maxWithOrNull(comparator: Comparator<in Long>): Long? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Long = `$this$maxWithOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var e: Long = `$this$maxWithOrNull`[var4.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.maxWithOrNull(comparator: Comparator<in Float>): Float? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Float = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Float = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.maxWithOrNull(comparator: Comparator<in Double>): Double? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Double = `$this$maxWithOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var e: Double = `$this$maxWithOrNull`[var4.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun BooleanArray.maxWithOrNull(comparator: Comparator<in Boolean>): Boolean? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Boolean = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Boolean = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.maxWithOrNull(comparator: Comparator<in Char>): Char? {
      if (`$this$maxWithOrNull`.length == 0) {
         return null;
      } else {
         var max: Char = `$this$maxWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$maxWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$maxWithOrNull`[var3.nextInt()];
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun Array<out Double>.min(): Double {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Double = `$this$min`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var3.hasNext()) {
            min = Math.min(min, `$this$min`[var3.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun Array<out Float>.min(): Float {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Float = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            min = Math.min(min, `$this$min`[var2.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.min(): T {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: java.lang.Comparable = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: java.lang.Comparable = `$this$min`[var2.nextInt()];
            if (min.compareTo(e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun ByteArray.min(): Byte {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Byte = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: Byte = `$this$min`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun ShortArray.min(): Short {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Short = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: Short = `$this$min`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun IntArray.min(): Int {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Int = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: Int = `$this$min`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun LongArray.min(): Long {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Long = `$this$min`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var3.hasNext()) {
            var e: Long = `$this$min`[var3.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun FloatArray.min(): Float {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Float = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            min = Math.min(min, `$this$min`[var2.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun DoubleArray.min(): Double {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Double = `$this$min`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var3.hasNext()) {
            min = Math.min(min, `$this$min`[var3.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun CharArray.min(): Char {
      if (`$this$min`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Char = `$this$min`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$min`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$min`[var2.nextInt()];
            if (Intrinsics.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.minBy(selector: (T) -> R): T {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Any = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return (T)minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Any = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return (T)minElem;
         }
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.minBy(selector: (Byte) -> R): Byte {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Byte = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Byte = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.minBy(selector: (Short) -> R): Short {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Short = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Short = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.minBy(selector: (Int) -> R): Int {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Int = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Int = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.minBy(selector: (Long) -> R): Long {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Long = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Long = `$this$minBy`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.minBy(selector: (Float) -> R): Float {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Float = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Float = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.minBy(selector: (Double) -> R): Double {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Double = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Double = `$this$minBy`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.minBy(selector: (Boolean) -> R): Boolean {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Boolean = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Boolean = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.minBy(selector: (Char) -> R): Char {
      if (`$this$minBy`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minElem: Char = `$this$minBy`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minBy`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$minBy`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.minByOrNull(selector: (T) -> R): T? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Any = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return (T)minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Any = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            }

            return (T)minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.minByOrNull(selector: (Byte) -> R): Byte? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Byte = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Byte = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.minByOrNull(selector: (Short) -> R): Short? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Short = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Short = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.minByOrNull(selector: (Int) -> R): Int? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Int = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Int = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.minByOrNull(selector: (Long) -> R): Long? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Long = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Long = `$this$minByOrNull`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.minByOrNull(selector: (Float) -> R): Float? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Float = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Float = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.minByOrNull(selector: (Double) -> R): Double? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Double = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var7: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var7.hasNext()) {
               var e: Double = `$this$minByOrNull`[var7.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.minByOrNull(selector: (Boolean) -> R): Boolean? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Boolean = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Boolean = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.minByOrNull(selector: (Char) -> R): Char? {
      if (`$this$minByOrNull`.length == 0) {
         return null;
      } else {
         var minElem: Char = `$this$minByOrNull`[0];
         var lastIndex: Int = ArraysKt.getLastIndex(`$this$minByOrNull`);
         if (lastIndex == 0) {
            return minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;
            var var6: IntIterator = new IntRange(1, lastIndex).iterator();

            while (var6.hasNext()) {
               var e: Char = `$this$minByOrNull`[var6.nextInt()];
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
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
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.minOf(selector: (T) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.minOf(selector: (Byte) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.minOf(selector: (Short) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.minOf(selector: (Int) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.minOf(selector: (Long) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.minOf(selector: (Float) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.minOf(selector: (Double) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.minOf(selector: (Boolean) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.minOf(selector: (Char) -> Double): Double {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.minOf(selector: (T) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.minOf(selector: (Byte) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.minOf(selector: (Short) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.minOf(selector: (Int) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.minOf(selector: (Long) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.minOf(selector: (Float) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.minOf(selector: (Double) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.minOf(selector: (Boolean) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.minOf(selector: (Char) -> Float): Float {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(`$this$minOf`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.minOf(selector: (T) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.minOf(selector: (Byte) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.minOf(selector: (Short) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.minOf(selector: (Int) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.minOf(selector: (Long) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.minOf(selector: (Float) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.minOf(selector: (Double) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.minOf(selector: (Boolean) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.minOf(selector: (Char) -> R): R {
      if (`$this$minOf`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOf`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOf`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOf`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.minOfOrNull(selector: (T) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.minOfOrNull(selector: (Byte) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.minOfOrNull(selector: (Short) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.minOfOrNull(selector: (Int) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.minOfOrNull(selector: (Long) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.minOfOrNull(selector: (Float) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.minOfOrNull(selector: (Double) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.minOfOrNull(selector: (Boolean) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.minOfOrNull(selector: (Char) -> Double): Double? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).doubleValue();
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var4.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var4.nextInt()]) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.minOfOrNull(selector: (T) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.minOfOrNull(selector: (Byte) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.minOfOrNull(selector: (Short) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.minOfOrNull(selector: (Int) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.minOfOrNull(selector: (Long) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.minOfOrNull(selector: (Float) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.minOfOrNull(selector: (Double) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.minOfOrNull(selector: (Boolean) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.minOfOrNull(selector: (Char) -> Float): Float? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Number).floatValue();
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Array<out T>.minOfOrNull(selector: (T) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ByteArray.minOfOrNull(selector: (Byte) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> ShortArray.minOfOrNull(selector: (Short) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> IntArray.minOfOrNull(selector: (Int) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> LongArray.minOfOrNull(selector: (Long) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> FloatArray.minOfOrNull(selector: (Float) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> DoubleArray.minOfOrNull(selector: (Double) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> BooleanArray.minOfOrNull(selector: (Boolean) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R : Comparable<R>> CharArray.minOfOrNull(selector: (Char) -> R): R? {
      if (`$this$minOfOrNull`.length == 0) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[0]) as java.lang.Comparable;
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfOrNull`)).iterator();

         while (var3.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(`$this$minOfOrNull`[var3.nextInt()]) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.minOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.minOfWith(comparator: Comparator<in R>, selector: (Byte) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.minOfWith(comparator: Comparator<in R>, selector: (Short) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.minOfWith(comparator: Comparator<in R>, selector: (Int) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.minOfWith(comparator: Comparator<in R>, selector: (Long) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.minOfWith(comparator: Comparator<in R>, selector: (Float) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.minOfWith(comparator: Comparator<in R>, selector: (Double) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.minOfWith(comparator: Comparator<in R>, selector: (Boolean) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.minOfWith(comparator: Comparator<in R>, selector: (Char) -> R): R {
      if (`$this$minOfWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWith`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWith`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWith`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Array<out T>.minOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Byte) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Short) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Int) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Long) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Float) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Double) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Boolean) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.minOfWithOrNull(comparator: Comparator<in R>, selector: (Char) -> R): R? {
      if (`$this$minOfWithOrNull`.length == 0) {
         return null;
      } else {
         var minValue: Any = selector.invoke(`$this$minOfWithOrNull`[0]);
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOfWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var v: Any = selector.invoke(`$this$minOfWithOrNull`[var4.nextInt()]);
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Array<out Double>.minOrNull(): Double? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Double = `$this$minOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var3.hasNext()) {
            min = Math.min(min, `$this$minOrNull`[var3.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Array<out Float>.minOrNull(): Float? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Float = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            min = Math.min(min, `$this$minOrNull`[var2.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Array<out T>.minOrNull(): T? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: java.lang.Comparable = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: java.lang.Comparable = `$this$minOrNull`[var2.nextInt()];
            if (min.compareTo(e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.minOrNull(): Byte? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Byte = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Byte = `$this$minOrNull`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.minOrNull(): Short? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Short = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Short = `$this$minOrNull`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.minOrNull(): Int? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Int = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Int = `$this$minOrNull`[var2.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.minOrNull(): Long? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Long = `$this$minOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Long = `$this$minOrNull`[var3.nextInt()];
            if (min > e) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.minOrNull(): Float? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Float = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            min = Math.min(min, `$this$minOrNull`[var2.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.minOrNull(): Double? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Double = `$this$minOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var3.hasNext()) {
            min = Math.min(min, `$this$minOrNull`[var3.nextInt()]);
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.minOrNull(): Char? {
      if (`$this$minOrNull`.length == 0) {
         return null;
      } else {
         var min: Char = `$this$minOrNull`[0];
         var var2: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minOrNull`)).iterator();

         while (var2.hasNext()) {
            var e: Char = `$this$minOrNull`[var2.nextInt()];
            if (Intrinsics.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun <T> Array<out T>.minWith(comparator: Comparator<in T>): T {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Any = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Any = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun ByteArray.minWith(comparator: Comparator<in Byte>): Byte {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Byte = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Byte = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun ShortArray.minWith(comparator: Comparator<in Short>): Short {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Short = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Short = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun IntArray.minWith(comparator: Comparator<in Int>): Int {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Int = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Int = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun LongArray.minWith(comparator: Comparator<in Long>): Long {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Long = `$this$minWith`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var4.hasNext()) {
            var e: Long = `$this$minWith`[var4.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun FloatArray.minWith(comparator: Comparator<in Float>): Float {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Float = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Float = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun DoubleArray.minWith(comparator: Comparator<in Double>): Double {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Double = `$this$minWith`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var4.hasNext()) {
            var e: Double = `$this$minWith`[var4.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun BooleanArray.minWith(comparator: Comparator<in Boolean>): Boolean {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Boolean = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Boolean = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun CharArray.minWith(comparator: Comparator<in Char>): Char {
      if (`$this$minWith`.length == 0) {
         throw new NoSuchElementException();
      } else {
         var min: Char = `$this$minWith`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWith`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$minWith`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Array<out T>.minWithOrNull(comparator: Comparator<in T>): T? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Any = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Any = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ByteArray.minWithOrNull(comparator: Comparator<in Byte>): Byte? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Byte = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Byte = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun ShortArray.minWithOrNull(comparator: Comparator<in Short>): Short? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Short = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Short = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun IntArray.minWithOrNull(comparator: Comparator<in Int>): Int? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Int = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Int = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun LongArray.minWithOrNull(comparator: Comparator<in Long>): Long? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Long = `$this$minWithOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var e: Long = `$this$minWithOrNull`[var4.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun FloatArray.minWithOrNull(comparator: Comparator<in Float>): Float? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Float = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Float = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun DoubleArray.minWithOrNull(comparator: Comparator<in Double>): Double? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Double = `$this$minWithOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var4.hasNext()) {
            var e: Double = `$this$minWithOrNull`[var4.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun BooleanArray.minWithOrNull(comparator: Comparator<in Boolean>): Boolean? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Boolean = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Boolean = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun CharArray.minWithOrNull(comparator: Comparator<in Char>): Char? {
      if (`$this$minWithOrNull`.length == 0) {
         return null;
      } else {
         var min: Char = `$this$minWithOrNull`[0];
         var var3: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$minWithOrNull`)).iterator();

         while (var3.hasNext()) {
            var e: Char = `$this$minWithOrNull`[var3.nextInt()];
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return min;
      }
   }

   @JvmStatic
   public fun <T> Array<out T>.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun ByteArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun ShortArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun IntArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun LongArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun FloatArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun DoubleArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun BooleanArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public fun CharArray.none(): Boolean {
      return `$this$none`.length == 0;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.none(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun ByteArray.none(predicate: (Byte) -> Boolean): Boolean {
      for (byte element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun ShortArray.none(predicate: (Short) -> Boolean): Boolean {
      for (short element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun IntArray.none(predicate: (Int) -> Boolean): Boolean {
      for (int element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun LongArray.none(predicate: (Long) -> Boolean): Boolean {
      for (long element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun FloatArray.none(predicate: (Float) -> Boolean): Boolean {
      for (float element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun DoubleArray.none(predicate: (Double) -> Boolean): Boolean {
      for (double element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun BooleanArray.none(predicate: (Boolean) -> Boolean): Boolean {
      for (boolean element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public inline fun CharArray.none(predicate: (Char) -> Boolean): Boolean {
      for (char element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.onEach(action: (T) -> Unit): Array<out T> {
      for (Object element : $this$onEach) {
         action.invoke(element);
      }

      return (T[])`$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.onEach(action: (Byte) -> Unit): ByteArray {
      for (byte element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.onEach(action: (Short) -> Unit): ShortArray {
      for (short element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.onEach(action: (Int) -> Unit): IntArray {
      for (int element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.onEach(action: (Long) -> Unit): LongArray {
      for (long element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.onEach(action: (Float) -> Unit): FloatArray {
      for (float element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.onEach(action: (Double) -> Unit): DoubleArray {
      for (double element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.onEach(action: (Boolean) -> Unit): BooleanArray {
      for (boolean element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.onEach(action: (Char) -> Unit): CharArray {
      for (char element : $this$onEach) {
         action.invoke(element);
      }

      return `$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.onEachIndexed(action: (Int, T) -> Unit): Array<out T> {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return (T[])`$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.onEachIndexed(action: (Int, Byte) -> Unit): ByteArray {
      var `index$iv`: Int = 0;

      for (byte item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.onEachIndexed(action: (Int, Short) -> Unit): ShortArray {
      var `index$iv`: Int = 0;

      for (short item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.onEachIndexed(action: (Int, Int) -> Unit): IntArray {
      var `index$iv`: Int = 0;

      for (int item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.onEachIndexed(action: (Int, Long) -> Unit): LongArray {
      var `index$iv`: Int = 0;

      for (long item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.onEachIndexed(action: (Int, Float) -> Unit): FloatArray {
      var `index$iv`: Int = 0;

      for (float item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.onEachIndexed(action: (Int, Double) -> Unit): DoubleArray {
      var `index$iv`: Int = 0;

      for (double item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.onEachIndexed(action: (Int, Boolean) -> Unit): BooleanArray {
      var `index$iv`: Int = 0;

      for (boolean item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.onEachIndexed(action: (Int, Char) -> Unit): CharArray {
      var `index$iv`: Int = 0;

      for (char item$iv : $this$onEachIndexed) {
         action.invoke(`index$iv`++, `item$iv`);
      }

      return `$this$onEachIndexed`;
   }

   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduce(operation: (S, T) -> S): S {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Any = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]);
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun ByteArray.reduce(operation: (Byte, Byte) -> Byte): Byte {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun ShortArray.reduce(operation: (Short, Short) -> Short): Short {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun IntArray.reduce(operation: (Int, Int) -> Int): Int {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun LongArray.reduce(operation: (Long, Long) -> Long): Long {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = `$this$reduce`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var5.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var5.nextInt()]) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun FloatArray.reduce(operation: (Float, Float) -> Float): Float {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Float = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun DoubleArray.reduce(operation: (Double, Double) -> Double): Double {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Double = `$this$reduce`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var5.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduce`[var5.nextInt()]) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun BooleanArray.reduce(operation: (Boolean, Boolean) -> Boolean): Boolean {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Boolean = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharArray.reduce(operation: (Char, Char) -> Char): Char {
      if (`$this$reduce`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduce`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduce`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduce`[var4.nextInt()]) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceIndexed(operation: (Int, S, T) -> S): S {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Any = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexed`[index]);
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun ByteArray.reduceIndexed(operation: (Int, Byte, Byte) -> Byte): Byte {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun ShortArray.reduceIndexed(operation: (Int, Short, Short) -> Short): Short {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun IntArray.reduceIndexed(operation: (Int, Int, Int) -> Int): Int {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun LongArray.reduceIndexed(operation: (Int, Long, Long) -> Long): Long {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = `$this$reduceIndexed`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var5.hasNext()) {
            var index: Int = var5.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun FloatArray.reduceIndexed(operation: (Int, Float, Float) -> Float): Float {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Float = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun DoubleArray.reduceIndexed(operation: (Int, Double, Double) -> Double): Double {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Double = `$this$reduceIndexed`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var5.hasNext()) {
            var index: Int = var5.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun BooleanArray.reduceIndexed(operation: (Int, Boolean, Boolean) -> Boolean): Boolean {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Boolean = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharArray.reduceIndexed(operation: (Int, Char, Char) -> Char): Char {
      if (`$this$reduceIndexed`.length == 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduceIndexed`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexed`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexed`[index]) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceIndexedOrNull(operation: (Int, S, T) -> S): S? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Any = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun ByteArray.reduceIndexedOrNull(operation: (Int, Byte, Byte) -> Byte): Byte? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Byte = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun ShortArray.reduceIndexedOrNull(operation: (Int, Short, Short) -> Short): Short? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Short = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun IntArray.reduceIndexedOrNull(operation: (Int, Int, Int) -> Int): Int? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Int = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun LongArray.reduceIndexedOrNull(operation: (Int, Long, Long) -> Long): Long? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Long = `$this$reduceIndexedOrNull`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var5.hasNext()) {
            var index: Int = var5.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun FloatArray.reduceIndexedOrNull(operation: (Int, Float, Float) -> Float): Float? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Float = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun DoubleArray.reduceIndexedOrNull(operation: (Int, Double, Double) -> Double): Double? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Double = `$this$reduceIndexedOrNull`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var5.hasNext()) {
            var index: Int = var5.nextInt();
            accumulator = (operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun BooleanArray.reduceIndexedOrNull(operation: (Int, Boolean, Boolean) -> Boolean): Boolean? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Boolean = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharArray.reduceIndexedOrNull(operation: (Int, Char, Char) -> Char): Char? {
      if (`$this$reduceIndexedOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceIndexedOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceIndexedOrNull`)).iterator();

         while (var4.hasNext()) {
            var index: Int = var4.nextInt();
            accumulator = operation.invoke(index, accumulator, `$this$reduceIndexedOrNull`[index]) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceOrNull(operation: (S, T) -> S): S? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Any = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun ByteArray.reduceOrNull(operation: (Byte, Byte) -> Byte): Byte? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Byte = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun ShortArray.reduceOrNull(operation: (Short, Short) -> Short): Short? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Short = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun IntArray.reduceOrNull(operation: (Int, Int) -> Int): Int? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Int = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun LongArray.reduceOrNull(operation: (Long, Long) -> Long): Long? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Long = `$this$reduceOrNull`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var5.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var5.nextInt()]) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun FloatArray.reduceOrNull(operation: (Float, Float) -> Float): Float? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Float = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun DoubleArray.reduceOrNull(operation: (Double, Double) -> Double): Double? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Double = `$this$reduceOrNull`[0];
         var var5: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var5.hasNext()) {
            accumulator = (operation.invoke(accumulator, `$this$reduceOrNull`[var5.nextInt()]) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun BooleanArray.reduceOrNull(operation: (Boolean, Boolean) -> Boolean): Boolean? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Boolean = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun CharArray.reduceOrNull(operation: (Char, Char) -> Char): Char? {
      if (`$this$reduceOrNull`.length == 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceOrNull`[0];
         var var4: IntIterator = new IntRange(1, ArraysKt.getLastIndex(`$this$reduceOrNull`)).iterator();

         while (var4.hasNext()) {
            accumulator = operation.invoke(accumulator, `$this$reduceOrNull`[var4.nextInt()]) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceRight(operation: (T, S) -> S): S {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Any = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRight`[index--], accumulator);
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun ByteArray.reduceRight(operation: (Byte, Byte) -> Byte): Byte {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun ShortArray.reduceRight(operation: (Short, Short) -> Short): Short {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun IntArray.reduceRight(operation: (Int, Int) -> Int): Int {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun LongArray.reduceRight(operation: (Long, Long) -> Long): Long {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun FloatArray.reduceRight(operation: (Float, Float) -> Float): Float {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Float = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun DoubleArray.reduceRight(operation: (Double, Double) -> Double): Double {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Double = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun BooleanArray.reduceRight(operation: (Boolean, Boolean) -> Boolean): Boolean {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Boolean = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRight`[index--], accumulator) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharArray.reduceRight(operation: (Char, Char) -> Char): Char {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRight`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Char = `$this$reduceRight`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRight`[index--], accumulator) as Character;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceRightIndexed(operation: (Int, T, S) -> S): S {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Any;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator);
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun ByteArray.reduceRightIndexed(operation: (Int, Byte, Byte) -> Byte): Byte {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Byte;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun ShortArray.reduceRightIndexed(operation: (Int, Short, Short) -> Short): Short {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Short;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun IntArray.reduceRightIndexed(operation: (Int, Int, Int) -> Int): Int {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Int;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun LongArray.reduceRightIndexed(operation: (Int, Long, Long) -> Long): Long {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Long;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun FloatArray.reduceRightIndexed(operation: (Int, Float, Float) -> Float): Float {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Float;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun DoubleArray.reduceRightIndexed(operation: (Int, Double, Double) -> Double): Double {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Double;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun BooleanArray.reduceRightIndexed(operation: (Int, Boolean, Boolean) -> Boolean): Boolean {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Boolean;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @JvmStatic
   public inline fun CharArray.reduceRightIndexed(operation: (Int, Char, Char) -> Char): Char {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexed`);
      if (index < 0) {
         throw new UnsupportedOperationException("Empty array can't be reduced.");
      } else {
         var accumulator: Char;
         for (accumulator = $this$reduceRightIndexed[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexed`[index], accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceRightIndexedOrNull(operation: (Int, T, S) -> S): S? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Any;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun ByteArray.reduceRightIndexedOrNull(operation: (Int, Byte, Byte) -> Byte): Byte? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Byte;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun ShortArray.reduceRightIndexedOrNull(operation: (Int, Short, Short) -> Short): Short? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Short;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun IntArray.reduceRightIndexedOrNull(operation: (Int, Int, Int) -> Int): Int? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Int;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun LongArray.reduceRightIndexedOrNull(operation: (Int, Long, Long) -> Long): Long? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Long;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun FloatArray.reduceRightIndexedOrNull(operation: (Int, Float, Float) -> Float): Float? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Float;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun DoubleArray.reduceRightIndexedOrNull(operation: (Int, Double, Double) -> Double): Double? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Double;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = (operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun BooleanArray.reduceRightIndexedOrNull(operation: (Int, Boolean, Boolean) -> Boolean): Boolean? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Boolean;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun CharArray.reduceRightIndexedOrNull(operation: (Int, Char, Char) -> Char): Char? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightIndexedOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Char;
         for (accumulator = $this$reduceRightIndexedOrNull[index--]; index >= 0; index--) {
            accumulator = operation.invoke(index, `$this$reduceRightIndexedOrNull`[index], accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.reduceRightOrNull(operation: (T, S) -> S): S? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Any = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRightOrNull`[index--], accumulator);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun ByteArray.reduceRightOrNull(operation: (Byte, Byte) -> Byte): Byte? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Byte = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).byteValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun ShortArray.reduceRightOrNull(operation: (Short, Short) -> Short): Short? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Short = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).shortValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun IntArray.reduceRightOrNull(operation: (Int, Int) -> Int): Int? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Int = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).intValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun LongArray.reduceRightOrNull(operation: (Long, Long) -> Long): Long? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Long = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).longValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun FloatArray.reduceRightOrNull(operation: (Float, Float) -> Float): Float? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Float = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).floatValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun DoubleArray.reduceRightOrNull(operation: (Double, Double) -> Double): Double? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Double = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = (operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Number).doubleValue();
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun BooleanArray.reduceRightOrNull(operation: (Boolean, Boolean) -> Boolean): Boolean? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Boolean = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as java.lang.Boolean;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun CharArray.reduceRightOrNull(operation: (Char, Char) -> Char): Char? {
      var index: Int = ArraysKt.getLastIndex(`$this$reduceRightOrNull`);
      if (index < 0) {
         return null;
      } else {
         var accumulator: Char = `$this$reduceRightOrNull`[index--];

         while (index >= 0) {
            accumulator = operation.invoke(`$this$reduceRightOrNull`[index--], accumulator) as Character;
         }

         return accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R> Array<out T>.runningFold(initial: R, operation: (R, T) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var9: Any = initial;

         for (Object element : $this$runningFold) {
            var9 = operation.invoke(var9, element);
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.runningFold(initial: R, operation: (R, Byte) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (byte element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.runningFold(initial: R, operation: (R, Short) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (short element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.runningFold(initial: R, operation: (R, Int) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (int element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.runningFold(initial: R, operation: (R, Long) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var9: Any = initial;

         for (long element : $this$runningFold) {
            var9 = operation.invoke(var9, element);
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.runningFold(initial: R, operation: (R, Float) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (float element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.runningFold(initial: R, operation: (R, Double) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var9: Any = initial;

         for (double element : $this$runningFold) {
            var9 = operation.invoke(var9, element);
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.runningFold(initial: R, operation: (R, Boolean) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (boolean element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.runningFold(initial: R, operation: (R, Char) -> R): List<R> {
      if (`$this$runningFold`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFold`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;

         for (char element : $this$runningFold) {
            var8 = operation.invoke(var8, element);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R> Array<out T>.runningFoldIndexed(initial: R, operation: (Int, R, T) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var8: Any = initial;
         var index: Int = 0;

         for (int var9 = $this$runningFoldIndexed.length; index < var9; index++) {
            var8 = operation.invoke(index, var8, `$this$runningFoldIndexed`[index]);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.runningFoldIndexed(initial: R, operation: (Int, R, Byte) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.runningFoldIndexed(initial: R, operation: (Int, R, Short) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.runningFoldIndexed(initial: R, operation: (Int, R, Int) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.runningFoldIndexed(initial: R, operation: (Int, R, Long) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.runningFoldIndexed(initial: R, operation: (Int, R, Float) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.runningFoldIndexed(initial: R, operation: (Int, R, Double) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.runningFoldIndexed(initial: R, operation: (Int, R, Boolean) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.runningFoldIndexed(initial: R, operation: (Int, R, Char) -> R): List<R> {
      if (`$this$runningFoldIndexed`.length == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(`$this$runningFoldIndexed`.length + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var7: Any = initial;
         var index: Int = 0;

         for (int var8 = $this$runningFoldIndexed.length; index < var8; index++) {
            var7 = operation.invoke(index, var7, `$this$runningFoldIndexed`[index]);
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.runningReduce(operation: (S, T) -> S): List<S> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var8: Any = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var8);
         var result: ArrayList = index;
         var var9: Int = 1;

         for (int $this$runningReduce_u24lambda_u24144 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24144; index++) {
            var8 = operation.invoke(var8, `$this$runningReduce`[var9]);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.runningReduce(operation: (Byte, Byte) -> Byte): List<Byte> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Byte = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24145 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24145; index++) {
            var7 = (operation.invoke(var7, `$this$runningReduce`[var8]) as java.lang.Number).byteValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.runningReduce(operation: (Short, Short) -> Short): List<Short> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Short = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24146 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24146; index++) {
            var7 = (operation.invoke(var7, `$this$runningReduce`[var8]) as java.lang.Number).shortValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.runningReduce(operation: (Int, Int) -> Int): List<Int> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Int = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24147 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24147; index++) {
            var7 = (operation.invoke(var7, `$this$runningReduce`[var8]) as java.lang.Number).intValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.runningReduce(operation: (Long, Long) -> Long): List<Long> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Long = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var9);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24148 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24148; index++) {
            var9 = (operation.invoke(var9, `$this$runningReduce`[var8]) as java.lang.Number).longValue();
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.runningReduce(operation: (Float, Float) -> Float): List<Float> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Float = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24149 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24149; index++) {
            var7 = (operation.invoke(var7, `$this$runningReduce`[var8]) as java.lang.Number).floatValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.runningReduce(operation: (Double, Double) -> Double): List<Double> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Double = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var9);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24150 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24150; index++) {
            var9 = (operation.invoke(var9, `$this$runningReduce`[var8]) as java.lang.Number).doubleValue();
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.runningReduce(operation: (Boolean, Boolean) -> Boolean): List<Boolean> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Boolean = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24151 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24151; index++) {
            var7 = operation.invoke(var7, `$this$runningReduce`[var8]) as java.lang.Boolean;
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.runningReduce(operation: (Char, Char) -> Char): List<Char> {
      if (`$this$runningReduce`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Char = `$this$runningReduce`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduce`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduce_u24lambda_u24152 = $this$runningReduce.length; index < $this$runningReduce_u24lambda_u24152; index++) {
            var7 = operation.invoke(var7, `$this$runningReduce`[var8]) as Character;
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> Array<out T>.runningReduceIndexed(operation: (Int, S, T) -> S): List<S> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var8: Any = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var8);
         var result: ArrayList = index;
         var var9: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24153 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24153; index++) {
            var8 = operation.invoke(var9, var8, `$this$runningReduceIndexed`[var9]);
            result.add(var8);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.runningReduceIndexed(operation: (Int, Byte, Byte) -> Byte): List<Byte> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Byte = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24154 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24154; index++) {
            var7 = (operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as java.lang.Number).byteValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.runningReduceIndexed(operation: (Int, Short, Short) -> Short): List<Short> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Short = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24155 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24155; index++) {
            var7 = (operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as java.lang.Number).shortValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.runningReduceIndexed(operation: (Int, Int, Int) -> Int): List<Int> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Int = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24156 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24156; index++) {
            var7 = (operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as java.lang.Number).intValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.runningReduceIndexed(operation: (Int, Long, Long) -> Long): List<Long> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Long = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var9);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24157 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24157; index++) {
            var9 = (operation.invoke(var8, var9, `$this$runningReduceIndexed`[var8]) as java.lang.Number).longValue();
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.runningReduceIndexed(operation: (Int, Float, Float) -> Float): List<Float> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Float = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24158 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24158; index++) {
            var7 = (operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as java.lang.Number).floatValue();
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.runningReduceIndexed(operation: (Int, Double, Double) -> Double): List<Double> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Double = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var9);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24159 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24159; index++) {
            var9 = (operation.invoke(var8, var9, `$this$runningReduceIndexed`[var8]) as java.lang.Number).doubleValue();
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.runningReduceIndexed(operation: (Int, Boolean, Boolean) -> Boolean): List<Boolean> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Boolean = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24160 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24160; index++) {
            var7 = operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as java.lang.Boolean;
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.runningReduceIndexed(operation: (Int, Char, Char) -> Char): List<Char> {
      if (`$this$runningReduceIndexed`.length == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var7: Char = `$this$runningReduceIndexed`[0];
         var index: ArrayList = new ArrayList(`$this$runningReduceIndexed`.length);
         index.add(var7);
         var result: ArrayList = index;
         var var8: Int = 1;

         for (int $this$runningReduceIndexed_u24lambda_u24161 = $this$runningReduceIndexed.length; index < $this$runningReduceIndexed_u24lambda_u24161; index++) {
            var7 = operation.invoke(var8, var7, `$this$runningReduceIndexed`[var8]) as Character;
            result.add(var7);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <T, R> Array<out T>.scan(initial: R, operation: (R, T) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `accumulator$iv`: ArrayList = new ArrayList(`$this$scan`.length + 1);
         `accumulator$iv`.add(initial);
         var `result$iv`: ArrayList = `accumulator$iv`;
         var var11: Any = initial;

         for (Object element$iv : $this$scan) {
            var11 = operation.invoke(var11, `element$iv`);
            `result$iv`.add(var11);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.scan(initial: R, operation: (R, Byte) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (byte var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.scan(initial: R, operation: (R, Short) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (short var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.scan(initial: R, operation: (R, Int) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (int var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.scan(initial: R, operation: (R, Long) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var10: Any = initial;

         for (long var8 : $this$scan) {
            var10 = operation.invoke(var10, var8);
            var6.add(var10);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.scan(initial: R, operation: (R, Float) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (float var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.scan(initial: R, operation: (R, Double) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var10: Any = initial;

         for (double var8 : $this$scan) {
            var10 = operation.invoke(var10, var8);
            var6.add(var10);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.scan(initial: R, operation: (R, Boolean) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (boolean var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.scan(initial: R, operation: (R, Char) -> R): List<R> {
      var var10000: java.util.List;
      if (`$this$scan`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scan`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var9: Any = initial;

         for (char var8 : $this$scan) {
            var9 = operation.invoke(var9, var8);
            var6.add(var9);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <T, R> Array<out T>.scanIndexed(initial: R, operation: (Int, R, T) -> R): List<R> {
      var `$this$runningFoldIndexed$iv`: Array<Any> = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `accumulator$iv`: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         `accumulator$iv`.add(initial);
         var `result$iv`: ArrayList = `accumulator$iv`;
         var var10: Any = initial;
         var `index$iv`: Int = 0;

         for (int var11 = $this$scanIndexed.length; index$iv < var11; index$iv++) {
            var10 = operation.invoke(`index$iv`, var10, `$this$runningFoldIndexed$iv`[`index$iv`]);
            `result$iv`.add(var10);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ByteArray.scanIndexed(initial: R, operation: (Int, R, Byte) -> R): List<R> {
      var var3: ByteArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> ShortArray.scanIndexed(initial: R, operation: (Int, R, Short) -> R): List<R> {
      var var3: ShortArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> IntArray.scanIndexed(initial: R, operation: (Int, R, Int) -> R): List<R> {
      var var3: IntArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> LongArray.scanIndexed(initial: R, operation: (Int, R, Long) -> R): List<R> {
      var var3: LongArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> FloatArray.scanIndexed(initial: R, operation: (Int, R, Float) -> R): List<R> {
      var var3: FloatArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> DoubleArray.scanIndexed(initial: R, operation: (Int, R, Double) -> R): List<R> {
      var var3: DoubleArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> BooleanArray.scanIndexed(initial: R, operation: (Int, R, Boolean) -> R): List<R> {
      var var3: BooleanArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <R> CharArray.scanIndexed(initial: R, operation: (Int, R, Char) -> R): List<R> {
      var var3: CharArray = `$this$scanIndexed`;
      var var10000: java.util.List;
      if (`$this$scanIndexed`.length == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var var4: ArrayList = new ArrayList(`$this$scanIndexed`.length + 1);
         var4.add(initial);
         var var6: ArrayList = var4;
         var var8: Any = initial;
         var var5: Int = 0;

         for (int var7 = $this$scanIndexed.length; var5 < var7; var5++) {
            var8 = operation.invoke(var5, var8, var3[var5]);
            var6.add(var8);
         }

         var10000 = var6;
      }

      return var10000;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Array<out T>.sumBy(selector: (T) -> Int): Int {
      var sum: Int = 0;

      for (Object element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun ByteArray.sumBy(selector: (Byte) -> Int): Int {
      var sum: Int = 0;

      for (byte element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun ShortArray.sumBy(selector: (Short) -> Int): Int {
      var sum: Int = 0;

      for (short element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun IntArray.sumBy(selector: (Int) -> Int): Int {
      var sum: Int = 0;

      for (int element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun LongArray.sumBy(selector: (Long) -> Int): Int {
      var sum: Int = 0;

      for (long element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun FloatArray.sumBy(selector: (Float) -> Int): Int {
      var sum: Int = 0;

      for (float element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun DoubleArray.sumBy(selector: (Double) -> Int): Int {
      var sum: Int = 0;

      for (double element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun BooleanArray.sumBy(selector: (Boolean) -> Int): Int {
      var sum: Int = 0;

      for (boolean element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun CharArray.sumBy(selector: (Char) -> Int): Int {
      var sum: Int = 0;

      for (char element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Array<out T>.sumByDouble(selector: (T) -> Double): Double {
      var sum: Double = 0.0;

      for (Object element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun ByteArray.sumByDouble(selector: (Byte) -> Double): Double {
      var sum: Double = 0.0;

      for (byte element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun ShortArray.sumByDouble(selector: (Short) -> Double): Double {
      var sum: Double = 0.0;

      for (short element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun IntArray.sumByDouble(selector: (Int) -> Double): Double {
      var sum: Double = 0.0;

      for (int element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun LongArray.sumByDouble(selector: (Long) -> Double): Double {
      var sum: Double = 0.0;

      for (long element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun FloatArray.sumByDouble(selector: (Float) -> Double): Double {
      var sum: Double = 0.0;

      for (float element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun DoubleArray.sumByDouble(selector: (Double) -> Double): Double {
      var sum: Double = 0.0;

      for (double element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun BooleanArray.sumByDouble(selector: (Boolean) -> Double): Double {
      var sum: Double = 0.0;

      for (boolean element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun CharArray.sumByDouble(selector: (Char) -> Double): Double {
      var sum: Double = 0.0;

      for (char element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> Double): Double {
      var sum: Double = 0.0;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> Double): Double {
      var sum: Double = 0.0;

      for (byte element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> Double): Double {
      var sum: Double = 0.0;

      for (short element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> Double): Double {
      var sum: Double = 0.0;

      for (int element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> Double): Double {
      var sum: Double = 0.0;

      for (long element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> Double): Double {
      var sum: Double = 0.0;

      for (float element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> Double): Double {
      var sum: Double = 0.0;

      for (double element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> Double): Double {
      var sum: Double = 0.0;

      for (boolean element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> Double): Double {
      var sum: Double = 0.0;

      for (char element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> Int): Int {
      var sum: Int = 0;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> Int): Int {
      var sum: Int = 0;

      for (byte element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> Int): Int {
      var sum: Int = 0;

      for (short element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> Int): Int {
      var sum: Int = 0;

      for (int element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> Int): Int {
      var sum: Int = 0;

      for (long element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> Int): Int {
      var sum: Int = 0;

      for (float element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> Int): Int {
      var sum: Int = 0;

      for (double element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> Int): Int {
      var sum: Int = 0;

      for (boolean element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> Int): Int {
      var sum: Int = 0;

      for (char element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> Long): Long {
      var sum: Long = 0L;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> Long): Long {
      var sum: Long = 0L;

      for (byte element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> Long): Long {
      var sum: Long = 0L;

      for (short element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> Long): Long {
      var sum: Long = 0L;

      for (int element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> Long): Long {
      var sum: Long = 0L;

      for (long element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> Long): Long {
      var sum: Long = 0L;

      for (float element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> Long): Long {
      var sum: Long = 0L;

      for (double element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> Long): Long {
      var sum: Long = 0L;

      for (boolean element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> Long): Long {
      var sum: Long = 0L;

      for (char element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (Object element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (byte element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (short element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (int element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (long element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (float element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (double element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (boolean element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (char element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Array<out T>.sumOf(selector: (T) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (Object element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.sumOf(selector: (Byte) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (byte element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ShortArray.sumOf(selector: (Short) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (short element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun IntArray.sumOf(selector: (Int) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (int element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun LongArray.sumOf(selector: (Long) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (long element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun FloatArray.sumOf(selector: (Float) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (float element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun DoubleArray.sumOf(selector: (Double) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (double element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun BooleanArray.sumOf(selector: (Boolean) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (boolean element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharArray.sumOf(selector: (Char) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (char element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @JvmStatic
   public fun <T : Any> Array<T?>.requireNoNulls(): Array<T> {
      for (Object element : $this$requireNoNulls) {
         if (element == null) {
            throw new IllegalArgumentException("null element found in " + `$this$requireNoNulls` + '.');
         }
      }

      return (T[])`$this$requireNoNulls`;
   }

   @JvmStatic
   public inline fun <T> Array<out T>.partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (Object element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun ByteArray.partition(predicate: (Byte) -> Boolean): Pair<List<Byte>, List<Byte>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (byte element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun ShortArray.partition(predicate: (Short) -> Boolean): Pair<List<Short>, List<Short>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (short element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun IntArray.partition(predicate: (Int) -> Boolean): Pair<List<Int>, List<Int>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (int element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun LongArray.partition(predicate: (Long) -> Boolean): Pair<List<Long>, List<Long>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (long element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun FloatArray.partition(predicate: (Float) -> Boolean): Pair<List<Float>, List<Float>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (float element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun DoubleArray.partition(predicate: (Double) -> Boolean): Pair<List<Double>, List<Double>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (double element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun BooleanArray.partition(predicate: (Boolean) -> Boolean): Pair<List<Boolean>, List<Boolean>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (boolean element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public inline fun CharArray.partition(predicate: (Char) -> Boolean): Pair<List<Char>, List<Char>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (char element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public infix fun <T, R> Array<out T>.zip(other: Array<out R>): List<Pair<T, R>> {
      var `$this$zip$iv`: Array<Any> = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> ByteArray.zip(other: Array<out R>): List<Pair<Byte, R>> {
      var `$this$zip$iv`: ByteArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> ShortArray.zip(other: Array<out R>): List<Pair<Short, R>> {
      var `$this$zip$iv`: ShortArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> IntArray.zip(other: Array<out R>): List<Pair<Int, R>> {
      var `$this$zip$iv`: IntArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> LongArray.zip(other: Array<out R>): List<Pair<Long, R>> {
      var `$this$zip$iv`: LongArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> FloatArray.zip(other: Array<out R>): List<Pair<Float, R>> {
      var `$this$zip$iv`: FloatArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> DoubleArray.zip(other: Array<out R>): List<Pair<Double, R>> {
      var `$this$zip$iv`: DoubleArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> BooleanArray.zip(other: Array<out R>): List<Pair<Boolean, R>> {
      var `$this$zip$iv`: BooleanArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> CharArray.zip(other: Array<out R>): List<Pair<Char, R>> {
      var `$this$zip$iv`: CharArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <T, R, V> Array<out T>.zip(other: Array<out R>, transform: (T, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> ByteArray.zip(other: Array<out R>, transform: (Byte, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> ShortArray.zip(other: Array<out R>, transform: (Short, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> IntArray.zip(other: Array<out R>, transform: (Int, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> LongArray.zip(other: Array<out R>, transform: (Long, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> FloatArray.zip(other: Array<out R>, transform: (Float, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> DoubleArray.zip(other: Array<out R>, transform: (Double, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> BooleanArray.zip(other: Array<out R>, transform: (Boolean, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> CharArray.zip(other: Array<out R>, transform: (Char, R) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public infix fun <T, R> Array<out T>.zip(other: Iterable<R>): List<Pair<T, R>> {
      var `$this$zip$iv`: Array<Any> = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> ByteArray.zip(other: Iterable<R>): List<Pair<Byte, R>> {
      var `$this$zip$iv`: ByteArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> ShortArray.zip(other: Iterable<R>): List<Pair<Short, R>> {
      var `$this$zip$iv`: ShortArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> IntArray.zip(other: Iterable<R>): List<Pair<Int, R>> {
      var `$this$zip$iv`: IntArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> LongArray.zip(other: Iterable<R>): List<Pair<Long, R>> {
      var `$this$zip$iv`: LongArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> FloatArray.zip(other: Iterable<R>): List<Pair<Float, R>> {
      var `$this$zip$iv`: FloatArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> DoubleArray.zip(other: Iterable<R>): List<Pair<Double, R>> {
      var `$this$zip$iv`: DoubleArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> BooleanArray.zip(other: Iterable<R>): List<Pair<Boolean, R>> {
      var `$this$zip$iv`: BooleanArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun <R> CharArray.zip(other: Iterable<R>): List<Pair<Char, R>> {
      var `$this$zip$iv`: CharArray = `$this$zip`;
      var `arraySize$iv`: Int = `$this$zip`.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : other) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`++], `element$iv`));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <T, R, V> Array<out T>.zip(other: Iterable<R>, transform: (T, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> ByteArray.zip(other: Iterable<R>, transform: (Byte, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> ShortArray.zip(other: Iterable<R>, transform: (Short, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> IntArray.zip(other: Iterable<R>, transform: (Int, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> LongArray.zip(other: Iterable<R>, transform: (Long, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> FloatArray.zip(other: Iterable<R>, transform: (Float, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> DoubleArray.zip(other: Iterable<R>, transform: (Double, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> BooleanArray.zip(other: Iterable<R>, transform: (Boolean, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public inline fun <R, V> CharArray.zip(other: Iterable<R>, transform: (Char, R) -> V): List<V> {
      var arraySize: Int = `$this$zip`.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(other, 10), `$this$zip`.length));
      var i: Int = 0;

      for (Object element : other) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(`$this$zip`[i++], element));
      }

      return list;
   }

   @JvmStatic
   public infix fun ByteArray.zip(other: ByteArray): List<Pair<Byte, Byte>> {
      var `$this$zip$iv`: ByteArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun ShortArray.zip(other: ShortArray): List<Pair<Short, Short>> {
      var `$this$zip$iv`: ShortArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun IntArray.zip(other: IntArray): List<Pair<Int, Int>> {
      var `$this$zip$iv`: IntArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun LongArray.zip(other: LongArray): List<Pair<Long, Long>> {
      var `$this$zip$iv`: LongArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun FloatArray.zip(other: FloatArray): List<Pair<Float, Float>> {
      var `$this$zip$iv`: FloatArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun DoubleArray.zip(other: DoubleArray): List<Pair<Double, Double>> {
      var `$this$zip$iv`: DoubleArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun BooleanArray.zip(other: BooleanArray): List<Pair<Boolean, Boolean>> {
      var `$this$zip$iv`: BooleanArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public infix fun CharArray.zip(other: CharArray): List<Pair<Char, Char>> {
      var `$this$zip$iv`: CharArray = `$this$zip`;
      var `size$iv`: Int = Math.min(`$this$zip`.length, other.length);
      var `list$iv`: ArrayList = new ArrayList(`size$iv`);

      for (int i$iv = 0; i$iv < size$iv; i$iv++) {
         `list$iv`.add(TuplesKt.to(`$this$zip$iv`[`i$iv`], other[`i$iv`]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <V> ByteArray.zip(other: ByteArray, transform: (Byte, Byte) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> ShortArray.zip(other: ShortArray, transform: (Short, Short) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> IntArray.zip(other: IntArray, transform: (Int, Int) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> LongArray.zip(other: LongArray, transform: (Long, Long) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> FloatArray.zip(other: FloatArray, transform: (Float, Float) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> DoubleArray.zip(other: DoubleArray, transform: (Double, Double) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> BooleanArray.zip(other: BooleanArray, transform: (Boolean, Boolean) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public inline fun <V> CharArray.zip(other: CharArray, transform: (Char, Char) -> V): List<V> {
      var size: Int = Math.min(`$this$zip`.length, other.length);
      var list: ArrayList = new ArrayList(size);

      for (int i = 0; i < size; i++) {
         list.add(transform.invoke(`$this$zip`[i], other[i]));
      }

      return list;
   }

   @JvmStatic
   public fun <T, A : Appendable> Array<out T>.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((T) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (Object element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         StringsKt.appendElement(buffer, element, transform);
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> ByteArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Byte) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (byte element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf((int)element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> ShortArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Short) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (short element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf((int)element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> IntArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Int) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (int element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf(element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> LongArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Long) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (long element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf(element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> FloatArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Float) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (float element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf(element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> DoubleArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Double) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (double element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf(element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> BooleanArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Boolean) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (boolean element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(java.lang.String.valueOf(element));
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <A : Appendable> CharArray.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Char) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (char element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         if (transform != null) {
            buffer.append(transform.invoke(element) as java.lang.CharSequence);
         } else {
            buffer.append(element);
         }
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <T> Array<out T>.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((T) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun ByteArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Byte) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun ShortArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Short) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun IntArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Int) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun LongArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Long) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun FloatArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Float) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun DoubleArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Double) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun BooleanArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Boolean) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun CharArray.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((Char) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = ArraysKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun <T> Array<out T>.asIterable(): Iterable<T> {
      return (java.lang.Iterable<T>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$1(`$this$asIterable`));
   }

   @JvmStatic
   public fun ByteArray.asIterable(): Iterable<Byte> {
      return (java.lang.Iterable<java.lang.Byte>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$2(`$this$asIterable`));
   }

   @JvmStatic
   public fun ShortArray.asIterable(): Iterable<Short> {
      return (java.lang.Iterable<java.lang.Short>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$3(`$this$asIterable`));
   }

   @JvmStatic
   public fun IntArray.asIterable(): Iterable<Int> {
      return (java.lang.Iterable<Integer>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$4(`$this$asIterable`));
   }

   @JvmStatic
   public fun LongArray.asIterable(): Iterable<Long> {
      return (java.lang.Iterable<java.lang.Long>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$5(`$this$asIterable`));
   }

   @JvmStatic
   public fun FloatArray.asIterable(): Iterable<Float> {
      return (java.lang.Iterable<java.lang.Float>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$6(`$this$asIterable`));
   }

   @JvmStatic
   public fun DoubleArray.asIterable(): Iterable<Double> {
      return (java.lang.Iterable<java.lang.Double>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$7(`$this$asIterable`));
   }

   @JvmStatic
   public fun BooleanArray.asIterable(): Iterable<Boolean> {
      return (java.lang.Iterable<java.lang.Boolean>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$8(`$this$asIterable`));
   }

   @JvmStatic
   public fun CharArray.asIterable(): Iterable<Char> {
      return (java.lang.Iterable<Character>)(if (`$this$asIterable`.length == 0)
         CollectionsKt.emptyList()
         else
         new ArraysKt___ArraysKt$asIterable$$inlined$Iterable$9(`$this$asIterable`));
   }

   @JvmStatic
   public fun <T> Array<out T>.asSequence(): Sequence<T> {
      return (Sequence<T>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1(`$this$asSequence`));
   }

   @JvmStatic
   public fun ByteArray.asSequence(): Sequence<Byte> {
      return (Sequence<java.lang.Byte>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$2(`$this$asSequence`));
   }

   @JvmStatic
   public fun ShortArray.asSequence(): Sequence<Short> {
      return (Sequence<java.lang.Short>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$3(`$this$asSequence`));
   }

   @JvmStatic
   public fun IntArray.asSequence(): Sequence<Int> {
      return (Sequence<Integer>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$4(`$this$asSequence`));
   }

   @JvmStatic
   public fun LongArray.asSequence(): Sequence<Long> {
      return (Sequence<java.lang.Long>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$5(`$this$asSequence`));
   }

   @JvmStatic
   public fun FloatArray.asSequence(): Sequence<Float> {
      return (Sequence<java.lang.Float>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$6(`$this$asSequence`));
   }

   @JvmStatic
   public fun DoubleArray.asSequence(): Sequence<Double> {
      return (Sequence<java.lang.Double>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$7(`$this$asSequence`));
   }

   @JvmStatic
   public fun BooleanArray.asSequence(): Sequence<Boolean> {
      return (Sequence<java.lang.Boolean>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$8(`$this$asSequence`));
   }

   @JvmStatic
   public fun CharArray.asSequence(): Sequence<Char> {
      return (Sequence<Character>)(if (`$this$asSequence`.length == 0)
         SequencesKt.emptySequence()
         else
         new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$9(`$this$asSequence`));
   }

   @JvmName(name = "averageOfByte")
   @JvmStatic
   public fun Array<out Byte>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += (double)`$this$average`[var4].byteValue();
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfShort")
   @JvmStatic
   public fun Array<out Short>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += (double)`$this$average`[var4].shortValue();
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfInt")
   @JvmStatic
   public fun Array<out Int>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += (double)`$this$average`[var4].intValue();
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfLong")
   @JvmStatic
   public fun Array<out Long>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += (double)`$this$average`[var4].longValue();
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfFloat")
   @JvmStatic
   public fun Array<out Float>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += (double)`$this$average`[var4].floatValue();
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfDouble")
   @JvmStatic
   public fun Array<out Double>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: Int = 0;

      for (int var5 = $this$average.length; var4 < var5; var4++) {
         sum += `$this$average`[var4];
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun ByteArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (byte element : $this$average) {
         sum += (double)element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun ShortArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (short element : $this$average) {
         sum += (double)element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun IntArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (int element : $this$average) {
         sum += (double)element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun LongArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (long element : $this$average) {
         sum += (double)element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun FloatArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (float element : $this$average) {
         sum += (double)element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmStatic
   public fun DoubleArray.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;

      for (double element : $this$average) {
         sum += element;
         count++;
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "sumOfByte")
   @JvmStatic
   public fun Array<out Byte>.sum(): Int {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum += `$this$sum`[var2];
      }

      return sum;
   }

   @JvmName(name = "sumOfShort")
   @JvmStatic
   public fun Array<out Short>.sum(): Int {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum += `$this$sum`[var2];
      }

      return sum;
   }

   @JvmName(name = "sumOfInt")
   @JvmStatic
   public fun Array<out Int>.sum(): Int {
      var sum: Int = 0;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum += `$this$sum`[var2];
      }

      return sum;
   }

   @JvmName(name = "sumOfLong")
   @JvmStatic
   public fun Array<out Long>.sum(): Long {
      var sum: Long = 0L;
      var var3: Int = 0;

      for (int var4 = $this$sum.length; var3 < var4; var3++) {
         sum += `$this$sum`[var3];
      }

      return sum;
   }

   @JvmName(name = "sumOfFloat")
   @JvmStatic
   public fun Array<out Float>.sum(): Float {
      var sum: Float = 0.0F;
      var var2: Int = 0;

      for (int var3 = $this$sum.length; var2 < var3; var2++) {
         sum += `$this$sum`[var2];
      }

      return sum;
   }

   @JvmName(name = "sumOfDouble")
   @JvmStatic
   public fun Array<out Double>.sum(): Double {
      var sum: Double = 0.0;
      var var3: Int = 0;

      for (int var4 = $this$sum.length; var3 < var4; var3++) {
         sum += `$this$sum`[var3];
      }

      return sum;
   }

   @JvmStatic
   public fun ByteArray.sum(): Int {
      var sum: Int = 0;

      for (byte element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   @JvmStatic
   public fun ShortArray.sum(): Int {
      var sum: Int = 0;

      for (short element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   @JvmStatic
   public fun IntArray.sum(): Int {
      var sum: Int = 0;

      for (int element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   @JvmStatic
   public fun LongArray.sum(): Long {
      var sum: Long = 0L;

      for (long element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   @JvmStatic
   public fun FloatArray.sum(): Float {
      var sum: Float = 0.0F;

      for (float element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   @JvmStatic
   public fun DoubleArray.sum(): Double {
      var sum: Double = 0.0;

      for (double element : $this$sum) {
         sum += element;
      }

      return sum;
   }

   open fun ArraysKt___ArraysKt() {
   }
}
