package kotlin.text

import kotlin.enums.EnumEntries

enum class CharDirectionality {
   @JvmStatic
   UNDEFINED(-1),
   @JvmStatic
   LEFT_TO_RIGHT(0),
   @JvmStatic
   RIGHT_TO_LEFT(1),
   @JvmStatic
   RIGHT_TO_LEFT_ARABIC(2),
   @JvmStatic
   EUROPEAN_NUMBER(3),
   @JvmStatic
   EUROPEAN_NUMBER_SEPARATOR(4),
   @JvmStatic
   EUROPEAN_NUMBER_TERMINATOR(5),
   @JvmStatic
   ARABIC_NUMBER(6),
   @JvmStatic
   COMMON_NUMBER_SEPARATOR(7),
   @JvmStatic
   NONSPACING_MARK(8),
   @JvmStatic
   BOUNDARY_NEUTRAL(9),
   @JvmStatic
   PARAGRAPH_SEPARATOR(10),
   @JvmStatic
   SEGMENT_SEPARATOR(11),
   @JvmStatic
   WHITESPACE(12),
   @JvmStatic
   OTHER_NEUTRALS(13),
   @JvmStatic
   LEFT_TO_RIGHT_EMBEDDING(14),
   @JvmStatic
   LEFT_TO_RIGHT_OVERRIDE(15),
   @JvmStatic
   RIGHT_TO_LEFT_EMBEDDING(16),
   @JvmStatic
   RIGHT_TO_LEFT_OVERRIDE(17),
   @JvmStatic
   POP_DIRECTIONAL_FORMAT(18);
   @JvmStatic
   public CharDirectionality.Companion Companion = new CharDirectionality.Companion(null);
   private int value;
   @JvmStatic
   private Lazy<java.util.Map<Integer, CharDirectionality>> directionalityMap$delegate = LazyKt.lazy(<unrepresentable>.INSTANCE);

   public final val value: Int

   init {
      this.value = value;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<CharDirectionality> {
      return $ENTRIES;
   }

   companion object Companion private constructor() {
      private final val directionalityMap: Map<Int, CharDirectionality>
         private final get() {
            return CharDirectionality.access$getDirectionalityMap$delegate$cp().getValue() as java.util.Map<Integer, CharDirectionality>;
         }



      public fun valueOf(directionality: Int): CharDirectionality {
         var var10000: CharDirectionality = this.getDirectionalityMap().get(directionality);
         if (var10000 == null) {
            throw new IllegalArgumentException("Directionality #" + directionality + " is not defined.");
         } else {
            return var10000;
         }
      }
   }
}
