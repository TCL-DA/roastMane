package okhttp3

import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.internal.ProgressionUtilKt
import kotlin.jvm.internal.SourceDebugExtension

class MediaType private constructor(mediaType: String, type: String, subtype: String, vararg parameterNamesAndValues: Any) {
   @JvmStatic
   public MediaType.Companion Companion = new MediaType.Companion(null);
   private java.lang.String[] parameterNamesAndValues;
   @JvmStatic
   private java.lang.String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
   @JvmStatic
   private java.lang.String QUOTED = "\"([^\"]*)\"";
   @JvmStatic
   private Pattern TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
   @JvmStatic
   private Pattern PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");

   private final val mediaType: String
   private final val parameterNamesAndValues: Array<String>
   public final val subtype: String
   public final val type: String

   init {
      this.mediaType = mediaType;
      this.type = type;
      this.subtype = subtype;
      this.parameterNamesAndValues = parameterNamesAndValues;
   }

   @JvmOverloads
   public fun charset(defaultValue: Charset?): Charset? {
      var var10000: java.lang.String = this.parameter("charset");
      if (var10000 == null) {
         return defaultValue;
      } else {
         var charset: java.lang.String = var10000;

         var var3: Charset;
         try {
            var3 = Charset.forName(charset);
         } catch (IllegalArgumentException var5) {
            var3 = defaultValue;
         }

         return var3;
      }
   }

   public fun parameter(name: String): String? {
      var var2: Int = this.parameterNamesAndValues.length + -1;
      var i: Int = 0;
      var var4: Int = ProgressionUtilKt.getProgressionLastElement(0, var2, 2);
      if (0 <= var4) {
         while (true) {
            if (StringsKt.equals(this.parameterNamesAndValues[i], name, true)) {
               return this.parameterNamesAndValues[i + 1];
            }

            if (i == var4) {
               break;
            }

            i += 2;
         }
      }

      return null;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "type", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_type")
   public fun type(): String {
      return this.type;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "subtype", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_subtype")
   public fun subtype(): String {
      return this.subtype;
   }

   public override fun toString(): String {
      return this.mediaType;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is MediaType && (other as MediaType).mediaType == this.mediaType;
   }

   public override fun hashCode(): Int {
      return this.mediaType.hashCode();
   }

   @JvmOverloads
   fun charset(): Charset? {
      return charset$default(this, null, 1, null);
   }

   @JvmName(name = "get")
   @JvmStatic
   fun java.lang.String.get(): MediaType {
      return Companion.get(`$this$get`);
   }

   @JvmName(name = "parse")
   @JvmStatic
   fun java.lang.String.parse(): MediaType {
      return Companion.parse(`$this$parse`);
   }

   @SourceDebugExtension(["SMAP\nMediaType.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,181:1\n1#2:182\n37#3,2:183\n*S KotlinDebug\n*F\n+ 1 MediaType.kt\nokhttp3/MediaType$Companion\n*L\n148#1:183,2\n*E\n"])
   companion object Companion private constructor() {
      private final val PARAMETER: Pattern
      private const val QUOTED: String
      private const val TOKEN: String
      private final val TYPE_SUBTYPE: Pattern


      @JvmName(name = "get")
      public fun String.toMediaType(): MediaType {
         var typeSubtype: Matcher = MediaType.access$getTYPE_SUBTYPE$cp().matcher(`$this$toMediaType`);
         if (!typeSubtype.lookingAt()) {
            throw new IllegalArgumentException(("No subtype found for: \"" + `$this$toMediaType` + '"').toString());
         } else {
            var var10000: java.lang.String = typeSubtype.group(1);
            var var20: Locale = Locale.US;
            var10000 = var10000.toLowerCase(var20);
            var10000 = typeSubtype.group(2);
            var var23: Locale = Locale.US;
            var10000 = var10000.toLowerCase(var23);
            var var14: java.util.List = new ArrayList();
            var parameter: Matcher = MediaType.access$getPARAMETER$cp().matcher(`$this$toMediaType`);
            var var15: Int = typeSubtype.end();

            while (var15 < $this$toMediaType.length()) {
               parameter.region(var15, `$this$toMediaType`.length());
               if (!parameter.lookingAt()) {
                  var var26: StringBuilder = new StringBuilder().append("Parameter is not formatted correctly: \"");
                  var var10001: java.lang.String = `$this$toMediaType`.substring(var15);
                  throw new IllegalArgumentException(var26.append(var10001).append("\" for: \"").append(`$this$toMediaType`).append('"').toString().toString());
               }

               var `$this$toTypedArray$iv`: java.lang.String = parameter.group(1);
               if (`$this$toTypedArray$iv` == null) {
                  var15 = parameter.end();
               } else {
                  var `$i$f$toTypedArray`: java.lang.String = parameter.group(2);
                  if (`$i$f$toTypedArray` == null) {
                     var10000 = parameter.group(3);
                  } else if (StringsKt.startsWith$default(`$i$f$toTypedArray`, "'", false, 2, null)
                     && StringsKt.endsWith$default(`$i$f$toTypedArray`, "'", false, 2, null)
                     && `$i$f$toTypedArray`.length() > 2) {
                     var10000 = `$i$f$toTypedArray`.substring(1, `$i$f$toTypedArray`.length() - 1);
                  } else {
                     var10000 = `$i$f$toTypedArray`;
                  }

                  var14.add(`$this$toTypedArray$iv`);
                  var14.add(var10000);
                  var15 = parameter.end();
               }
            }

            return new MediaType(`$this$toMediaType`, var10000, var10000, var14.toArray(new java.lang.String[0]), null);
         }
      }

      @JvmName(name = "parse")
      public fun String.toMediaTypeOrNull(): MediaType? {
         var var2: MediaType;
         try {
            var2 = this.get(`$this$toMediaTypeOrNull`);
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "mediaType.toMediaType()", imports = ["okhttp3.MediaType.Companion.toMediaType"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_get")
      public fun get(mediaType: String): MediaType {
         return this.get(mediaType);
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "mediaType.toMediaTypeOrNull()", imports = ["okhttp3.MediaType.Companion.toMediaTypeOrNull"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_parse")
      public fun parse(mediaType: String): MediaType? {
         return this.parse(mediaType);
      }
   }
}
