package okhttp3.internal.http2

class Settings {
   @JvmStatic
   public Settings.Companion Companion = new Settings.Companion(null);
   @JvmStatic
   public int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
   @JvmStatic
   public int HEADER_TABLE_SIZE = 1;
   @JvmStatic
   public int ENABLE_PUSH = 2;
   @JvmStatic
   public int MAX_CONCURRENT_STREAMS = 4;
   @JvmStatic
   public int MAX_FRAME_SIZE = 5;
   @JvmStatic
   public int MAX_HEADER_LIST_SIZE = 6;
   @JvmStatic
   public int INITIAL_WINDOW_SIZE = 7;
   @JvmStatic
   public int COUNT = 10;

   public final val headerTableSize: Int
      public final get() {
         return if ((2 and this.set) != 0) this.values[1] else -1;
      }

   public final val initialWindowSize: Int
      public final get() {
         return if ((128 and this.set) != 0) this.values[7] else 65535;
      }

   private final var set: Int
   private final val values: IntArray = new int[10]


   public fun clear() {
      this.set = 0;
      ArraysKt.fill$default(this.values, 0, 0, 0, 6, null);
   }

   public operator fun set(id: Int, value: Int): Settings {
      if (id >= 0 && id < this.values.length) {
         this.set |= 1 shl id;
         this.values[id] = value;
         return this;
      } else {
         return this;
      }
   }

   public fun isSet(id: Int): Boolean {
      return (this.set and 1 shl id) != 0;
   }

   public operator fun get(id: Int): Int {
      return this.values[id];
   }

   public fun size(): Int {
      return Integer.bitCount(this.set);
   }

   public fun getEnablePush(defaultValue: Boolean): Boolean {
      return if ((4 and this.set) != 0) this.values[2] == 1 else defaultValue;
   }

   public fun getMaxConcurrentStreams(): Int {
      return if ((16 and this.set) != 0) this.values[4] else Integer.MAX_VALUE;
   }

   public fun getMaxFrameSize(defaultValue: Int): Int {
      return if ((32 and this.set) != 0) this.values[5] else defaultValue;
   }

   public fun getMaxHeaderListSize(defaultValue: Int): Int {
      return if ((64 and this.set) != 0) this.values[6] else defaultValue;
   }

   public fun merge(other: Settings) {
      for (int i = 0; i < 10; i++) {
         if (other.isSet(i)) {
            this.set(i, other.get(i));
         }
      }
   }

   companion object Companion private constructor() {
      public const val COUNT: Int
      public const val DEFAULT_INITIAL_WINDOW_SIZE: Int
      public const val ENABLE_PUSH: Int
      public const val HEADER_TABLE_SIZE: Int
      public const val INITIAL_WINDOW_SIZE: Int
      public const val MAX_CONCURRENT_STREAMS: Int
      public const val MAX_FRAME_SIZE: Int
      public const val MAX_HEADER_LIST_SIZE: Int

   }
}
