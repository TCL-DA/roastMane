package kotlin.text

import kotlin.internal.InlineOnly

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
class HexFormat internal constructor(upperCase: Boolean, bytes: kotlin.text.HexFormat.BytesHexFormat, number: kotlin.text.HexFormat.NumberHexFormat) {
   @JvmStatic
   public HexFormat.Companion Companion = new HexFormat.Companion(null);
   @JvmStatic
   private HexFormat Default = new HexFormat(
      false, HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib(), HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib()
   );
   @JvmStatic
   private HexFormat UpperCase = new HexFormat(
      true, HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib(), HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib()
   );

   public final val bytes: kotlin.text.HexFormat.BytesHexFormat
   public final val number: kotlin.text.HexFormat.NumberHexFormat
   public final val upperCase: Boolean

   init {
      this.upperCase = upperCase;
      this.bytes = bytes;
      this.number = number;
   }

   public override fun toString(): String {
      var var1: StringBuilder = new StringBuilder();
      var var10000: StringBuilder = var1.append("HexFormat(");
      var10000 = var1.append("    upperCase = ").append(this.upperCase);
      var10000 = var10000.append(",");
      var10000 = var1.append("    bytes = BytesHexFormat(");
      var10000 = var1.append("    ),");
      var10000 = var1.append("    number = NumberHexFormat(");
      var10000 = var1.append("    )");
      var1.append(")");
      var var11: java.lang.String = var1.toString();
      return var11;
   }

   class Builder @PublishedApi  internal constructor() {
      private final var _bytes: kotlin.text.HexFormat.BytesHexFormat.Builder?
      private final var _number: kotlin.text.HexFormat.NumberHexFormat.Builder?
      public final val bytes: kotlin.text.HexFormat.BytesHexFormat.Builder
         public final get() {
            if (this._bytes == null) {
               this._bytes = new HexFormat.BytesHexFormat.Builder();
            }

            var var10000: HexFormat.BytesHexFormat.Builder = this._bytes;
            return var10000;
         }

      public final val number: kotlin.text.HexFormat.NumberHexFormat.Builder
         public final get() {
            if (this._number == null) {
               this._number = new HexFormat.NumberHexFormat.Builder();
            }

            var var10000: HexFormat.NumberHexFormat.Builder = this._number;
            return var10000;
         }

      public final var upperCase: Boolean = HexFormat.Companion.getDefault().getUpperCase()
         internal set


      @InlineOnly
      public inline fun bytes(builderAction: (kotlin.text.HexFormat.BytesHexFormat.Builder) -> Unit) {
         builderAction.invoke(this.getBytes());
      }

      @InlineOnly
      public inline fun number(builderAction: (kotlin.text.HexFormat.NumberHexFormat.Builder) -> Unit) {
         builderAction.invoke(this.getNumber());
      }

      @PublishedApi
      internal fun build(): HexFormat {
         var var10000: HexFormat;
         var var10002: Boolean;
         var var10003: HexFormat.BytesHexFormat;
         label19: {
            var10000 = new HexFormat;
            var10002 = this.upperCase;
            if (this._bytes != null) {
               var10003 = this._bytes.build$kotlin_stdlib();
               if (var10003 != null) {
                  break label19;
               }
            }

            var10003 = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib();
         }

         var var10004: HexFormat.NumberHexFormat;
         label14: {
            if (this._number != null) {
               var10004 = this._number.build$kotlin_stdlib();
               if (var10004 != null) {
                  break label14;
               }
            }

            var10004 = HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib();
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002, var10003, var10004);
         return var10000;
      }
   }

   class BytesHexFormat internal constructor(bytesPerLine: Int,
      bytesPerGroup: Int,
      groupSeparator: String,
      byteSeparator: String,
      bytePrefix: String,
      byteSuffix: String
   ) {
      @JvmStatic
      public HexFormat.BytesHexFormat.Companion Companion = new HexFormat.BytesHexFormat.Companion(null);
      @JvmStatic
      private HexFormat.BytesHexFormat Default = new HexFormat.BytesHexFormat(Integer.MAX_VALUE, Integer.MAX_VALUE, "  ", "", "", "");

      public final val bytePrefix: String
      public final val byteSeparator: String
      public final val byteSuffix: String
      public final val bytesPerGroup: Int
      public final val bytesPerLine: Int
      public final val groupSeparator: String

      init {
         this.bytesPerLine = bytesPerLine;
         this.bytesPerGroup = bytesPerGroup;
         this.groupSeparator = groupSeparator;
         this.byteSeparator = byteSeparator;
         this.bytePrefix = bytePrefix;
         this.byteSuffix = byteSuffix;
      }

      public override fun toString(): String {
         var var1: StringBuilder = new StringBuilder();
         var var10000: StringBuilder = var1.append("BytesHexFormat(");
         var1.append(")");
         var var4: java.lang.String = var1.toString();
         return var4;
      }

      internal fun appendOptionsTo(sb: StringBuilder, indent: String): StringBuilder {
         var var10000: StringBuilder = sb.append(indent).append("bytesPerLine = ").append(this.bytesPerLine);
         var10000 = var10000.append(",");
         var10000 = sb.append(indent).append("bytesPerGroup = ").append(this.bytesPerGroup);
         var10000 = var10000.append(",");
         var10000 = sb.append(indent).append("groupSeparator = \"").append(this.groupSeparator);
         var10000 = var10000.append("\",");
         var10000 = sb.append(indent).append("byteSeparator = \"").append(this.byteSeparator);
         var10000 = var10000.append("\",");
         var10000 = sb.append(indent).append("bytePrefix = \"").append(this.bytePrefix);
         var10000 = var10000.append("\",");
         sb.append(indent).append("byteSuffix = \"").append(this.byteSuffix).append("\"");
         return sb;
      }

      class Builder internal constructor() {
         public final var bytePrefix: String = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getBytePrefix()
            internal final set(value) {
               if (!StringsKt.contains$default(value, '\n', false, 2, null) && !StringsKt.contains$default(value, '\r', false, 2, null)) {
                  this.bytePrefix = value;
               } else {
                  throw new IllegalArgumentException("LF and CR characters are prohibited in bytePrefix, but was " + value);
               }
            }

         public final var byteSeparator: String = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getByteSeparator()
            internal final set(value) {
               if (!StringsKt.contains$default(value, '\n', false, 2, null) && !StringsKt.contains$default(value, '\r', false, 2, null)) {
                  this.byteSeparator = value;
               } else {
                  throw new IllegalArgumentException("LF and CR characters are prohibited in byteSeparator, but was " + value);
               }
            }

         public final var byteSuffix: String = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getByteSuffix()
            internal final set(value) {
               if (!StringsKt.contains$default(value, '\n', false, 2, null) && !StringsKt.contains$default(value, '\r', false, 2, null)) {
                  this.byteSuffix = value;
               } else {
                  throw new IllegalArgumentException("LF and CR characters are prohibited in byteSuffix, but was " + value);
               }
            }

         public final var bytesPerGroup: Int = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getBytesPerGroup()
            internal final set(value) {
               if (value <= 0) {
                  throw new IllegalArgumentException("Non-positive values are prohibited for bytesPerGroup, but was " + value);
               } else {
                  this.bytesPerGroup = value;
               }
            }

         public final var bytesPerLine: Int = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getBytesPerLine()
            internal final set(value) {
               if (value <= 0) {
                  throw new IllegalArgumentException("Non-positive values are prohibited for bytesPerLine, but was " + value);
               } else {
                  this.bytesPerLine = value;
               }
            }

         public final var groupSeparator: String = HexFormat.BytesHexFormat.Companion.getDefault$kotlin_stdlib().getGroupSeparator()
            internal set


         internal fun build(): kotlin.text.HexFormat.BytesHexFormat {
            return new HexFormat.BytesHexFormat(
               this.bytesPerLine, this.bytesPerGroup, this.groupSeparator, this.byteSeparator, this.bytePrefix, this.byteSuffix
            );
         }
      }

      internal companion object Companion private constructor() {
         internal final val Default: kotlin.text.HexFormat.BytesHexFormat

      }
   }

   companion object Companion private constructor() {
      public final val Default: HexFormat
      public final val UpperCase: HexFormat

   }

   class NumberHexFormat internal constructor(prefix: String, suffix: String, removeLeadingZeros: Boolean) {
      @JvmStatic
      public HexFormat.NumberHexFormat.Companion Companion = new HexFormat.NumberHexFormat.Companion(null);
      @JvmStatic
      private HexFormat.NumberHexFormat Default = new HexFormat.NumberHexFormat("", "", false);

      public final val prefix: String
      public final val removeLeadingZeros: Boolean
      public final val suffix: String

      init {
         this.prefix = prefix;
         this.suffix = suffix;
         this.removeLeadingZeros = removeLeadingZeros;
      }

      public override fun toString(): String {
         var var1: StringBuilder = new StringBuilder();
         var var10000: StringBuilder = var1.append("NumberHexFormat(");
         var1.append(")");
         var var4: java.lang.String = var1.toString();
         return var4;
      }

      internal fun appendOptionsTo(sb: StringBuilder, indent: String): StringBuilder {
         var var10000: StringBuilder = sb.append(indent).append("prefix = \"").append(this.prefix);
         var10000 = var10000.append("\",");
         var10000 = sb.append(indent).append("suffix = \"").append(this.suffix);
         var10000 = var10000.append("\",");
         sb.append(indent).append("removeLeadingZeros = ").append(this.removeLeadingZeros);
         return sb;
      }

      class Builder internal constructor() {
         public final var prefix: String = HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib().getPrefix()
            internal final set(value) {
               if (!StringsKt.contains$default(value, '\n', false, 2, null) && !StringsKt.contains$default(value, '\r', false, 2, null)) {
                  this.prefix = value;
               } else {
                  throw new IllegalArgumentException("LF and CR characters are prohibited in prefix, but was " + value);
               }
            }

         public final var removeLeadingZeros: Boolean = HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib().getRemoveLeadingZeros()
            internal set
         public final var suffix: String = HexFormat.NumberHexFormat.Companion.getDefault$kotlin_stdlib().getSuffix()
            internal final set(value) {
               if (!StringsKt.contains$default(value, '\n', false, 2, null) && !StringsKt.contains$default(value, '\r', false, 2, null)) {
                  this.suffix = value;
               } else {
                  throw new IllegalArgumentException("LF and CR characters are prohibited in suffix, but was " + value);
               }
            }



         internal fun build(): kotlin.text.HexFormat.NumberHexFormat {
            return new HexFormat.NumberHexFormat(this.prefix, this.suffix, this.removeLeadingZeros);
         }
      }

      internal companion object Companion private constructor() {
         internal final val Default: kotlin.text.HexFormat.NumberHexFormat

      }
   }
}
