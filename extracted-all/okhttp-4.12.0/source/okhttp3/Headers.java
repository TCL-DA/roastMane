package okhttp3

import java.time.Instant
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TreeMap
import java.util.TreeSet
import java.util.Map.Entry
import kotlin.internal.ProgressionUtilKt
import kotlin.jvm.internal.ArrayIteratorKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.StringCompanionObject
import kotlin.jvm.internal.markers.KMappedMarker
import okhttp3.internal.Util
import okhttp3.internal.http.DatesKt
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

class Headers private constructor(vararg namesAndValues: Any) : java.lang.Iterable<Pair<? extends java.lang.String, ? extends java.lang.String>>, KMappedMarker {
   @JvmStatic
   public Headers.Companion Companion = new Headers.Companion(null);
   private java.lang.String[] namesAndValues;

   private final val namesAndValues: Array<String>
   public final val size: Int
      public final get() {
         return this.namesAndValues.length / 2;
      }


   init {
      this.namesAndValues = namesAndValues;
   }

   public operator fun get(name: String): String? {
      return Headers.Companion.access$get(Companion, this.namesAndValues, name);
   }

   public fun getDate(name: String): Date? {
      var var10000: java.lang.String = this.get(name);
      return if (var10000 != null) DatesKt.toHttpDateOrNull(var10000) else null;
   }

   @IgnoreJRERequirement
   public fun getInstant(name: String): Instant? {
      var value: Date = this.getDate(name);
      return if (value != null) value.toInstant() else null;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "size", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_size")
   public fun size(): Int {
      return this.size();
   }

   public fun name(index: Int): String {
      return this.namesAndValues[index * 2];
   }

   public fun value(index: Int): String {
      return this.namesAndValues[index * 2 + 1];
   }

   public fun names(): Set<String> {
      var result: TreeSet = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
      var i: Int = 0;

      for (int var3 = this.size(); i < var3; i++) {
         result.add(this.name(i));
      }

      var var10000: java.util.Set = Collections.unmodifiableSet(result);
      return var10000;
   }

   public fun values(name: String): List<String> {
      var result: java.util.List = null;
      var i: Int = 0;

      for (int var4 = this.size(); i < var4; i++) {
         if (StringsKt.equals(name, this.name(i), true)) {
            if (result == null) {
               result = new ArrayList(2);
            }

            result.add(this.value(i));
         }
      }

      var var10000: java.util.List;
      if (result != null) {
         var10000 = Collections.unmodifiableList(result);
      } else {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   public fun byteCount(): Long {
      var result: Long = (long)(this.namesAndValues.length * 2);
      var i: Int = 0;

      for (int var4 = this.namesAndValues.length; i < var4; i++) {
         result += (long)this.namesAndValues[i].length();
      }

      return result;
   }

   public override operator fun iterator(): Iterator<Pair<String, String>> {
      var var1: Int = 0;
      var var2: Int = this.size();

      var var3: Array<Pair>;
      for (var3 = new Pair[var2]; var1 < var2; var1++) {
         var3[var1] = TuplesKt.to(this.name(var1), this.value(var1));
      }

      return ArrayIteratorKt.iterator(var3);
   }

   public fun newBuilder(): okhttp3.Headers.Builder {
      var result: Headers.Builder = new Headers.Builder();
      CollectionsKt.addAll(result.getNamesAndValues$okhttp(), this.namesAndValues);
      return result;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Headers && Arrays.equals((Object[])this.namesAndValues, (Object[])(other as Headers).namesAndValues);
   }

   public override fun hashCode(): Int {
      return Arrays.hashCode((Object[])this.namesAndValues);
   }

   public override fun toString(): String {
      var var1: StringBuilder = new StringBuilder();
      var `$this$toString_u24lambda_u240`: StringBuilder = var1;
      var i: Int = 0;

      for (int var5 = this.size(); i < var5; i++) {
         var name: java.lang.String = this.name(i);
         var value: java.lang.String = this.value(i);
         `$this$toString_u24lambda_u240`.append(name);
         `$this$toString_u24lambda_u240`.append(": ");
         `$this$toString_u24lambda_u240`.append(if (Util.isSensitiveHeader(name)) "██" else value);
         `$this$toString_u24lambda_u240`.append("\n");
      }

      var var10000: java.lang.String = var1.toString();
      return var10000;
   }

   public fun toMultimap(): Map<String, List<String>> {
      var result: TreeMap = new TreeMap(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
      var i: Int = 0;

      for (int var3 = this.size(); i < var3; i++) {
         var values: java.lang.String = this.name(i);
         var var10000: Locale = Locale.US;
         var var7: java.lang.String = values.toLowerCase(var10000);
         var var6: java.util.List = result.get(var7) as java.util.List;
         if (var6 == null) {
            var6 = new ArrayList(2);
            result.put(var7, var6);
         }

         var6.add(this.value(i));
      }

      return result;
   }

   @JvmName(name = "of")
   @JvmStatic
   fun of(vararg namesAndValues: java.lang.String): Headers {
      return Companion.of(namesAndValues);
   }

   @JvmName(name = "of")
   @JvmStatic
   fun MutableMap<java.lang.String, java.lang.String>.of(): Headers {
      return Companion.of(`$this$of`);
   }

   @SourceDebugExtension(["SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,458:1\n1#2:459\n37#3,2:460\n*S KotlinDebug\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Builder\n*L\n359#1:460,2\n*E\n"])
   class Builder {
      internal final val namesAndValues: MutableList<String> = (new ArrayList(20)) as java.util.List


      internal fun addLenient(line: String): okhttp3.Headers.Builder {
         var `$this$addLenient_u24lambda_u240`: Headers.Builder = this;
         var index: Int = StringsKt.indexOf$default(line, ':', 1, false, 4, null);
         if (index != -1) {
            var var10001: java.lang.String = line.substring(0, index);
            var var10002: java.lang.String = line.substring(index + 1);
            `$this$addLenient_u24lambda_u240`.addLenient$okhttp(var10001, var10002);
         } else if (line.charAt(0) == ':') {
            var var6: java.lang.String = line.substring(1);
            `$this$addLenient_u24lambda_u240`.addLenient$okhttp("", var6);
         } else {
            `$this$addLenient_u24lambda_u240`.addLenient$okhttp("", line);
         }

         return this;
      }

      public fun add(line: String): okhttp3.Headers.Builder {
         var `$this$add_u24lambda_u242`: Headers.Builder = this;
         var index: Int = StringsKt.indexOf$default(line, ':', 0, false, 6, null);
         if (index == -1) {
            throw new IllegalArgumentException(("Unexpected header: " + line).toString());
         } else {
            var var10001: java.lang.String = line.substring(0, index);
            var10001 = StringsKt.trim(var10001).toString();
            var var10002: java.lang.String = line.substring(index + 1);
            `$this$add_u24lambda_u242`.add(var10001, var10002);
            return this;
         }
      }

      public fun add(name: String, value: String): okhttp3.Headers.Builder {
         var `$this$add_u24lambda_u243`: Headers.Builder = this;
         Headers.Companion.access$checkName(Headers.Companion, name);
         Headers.Companion.access$checkValue(Headers.Companion, value, name);
         `$this$add_u24lambda_u243`.addLenient$okhttp(name, value);
         return this;
      }

      public fun addUnsafeNonAscii(name: String, value: String): okhttp3.Headers.Builder {
         var `$this$addUnsafeNonAscii_u24lambda_u244`: Headers.Builder = this;
         Headers.Companion.access$checkName(Headers.Companion, name);
         `$this$addUnsafeNonAscii_u24lambda_u244`.addLenient$okhttp(name, value);
         return this;
      }

      public fun addAll(headers: Headers): okhttp3.Headers.Builder {
         var `$this$addAll_u24lambda_u245`: Headers.Builder = this;
         var i: Int = 0;

         for (int var6 = headers.size(); i < var6; i++) {
            `$this$addAll_u24lambda_u245`.addLenient$okhttp(headers.name(i), headers.value(i));
         }

         return this;
      }

      public fun add(name: String, value: Date): okhttp3.Headers.Builder {
         this.add(name, DatesKt.toHttpDateString(value));
         return this;
      }

      @IgnoreJRERequirement
      public fun add(name: String, value: Instant): okhttp3.Headers.Builder {
         this.add(name, new Date(value.toEpochMilli()));
         return this;
      }

      public operator fun set(name: String, value: Date): okhttp3.Headers.Builder {
         this.set(name, DatesKt.toHttpDateString(value));
         return this;
      }

      @IgnoreJRERequirement
      public operator fun set(name: String, value: Instant): okhttp3.Headers.Builder {
         return this.set(name, new Date(value.toEpochMilli()));
      }

      internal fun addLenient(name: String, value: String): okhttp3.Headers.Builder {
         var `$this$addLenient_u24lambda_u2410`: Headers.Builder = this;
         this.namesAndValues.add(name);
         `$this$addLenient_u24lambda_u2410`.namesAndValues.add(StringsKt.trim(value).toString());
         return this;
      }

      public fun removeAll(name: String): okhttp3.Headers.Builder {
         var `$this$removeAll_u24lambda_u2411`: Headers.Builder = this;

         for (int i = 0; i < $this$removeAll_u24lambda_u2411.namesAndValues.size(); i += 2) {
            if (StringsKt.equals(name, `$this$removeAll_u24lambda_u2411`.namesAndValues.get(i), true)) {
               `$this$removeAll_u24lambda_u2411`.namesAndValues.remove(i);
               `$this$removeAll_u24lambda_u2411`.namesAndValues.remove(i);
               i -= 2;
            }
         }

         return this;
      }

      public operator fun set(name: String, value: String): okhttp3.Headers.Builder {
         var `$this$set_u24lambda_u2412`: Headers.Builder = this;
         Headers.Companion.access$checkName(Headers.Companion, name);
         Headers.Companion.access$checkValue(Headers.Companion, value, name);
         `$this$set_u24lambda_u2412`.removeAll(name);
         `$this$set_u24lambda_u2412`.addLenient$okhttp(name, value);
         return this;
      }

      public operator fun get(name: String): String? {
         var var2: Int = this.namesAndValues.size() - 2;
         var i: Int = var2;
         var var4: Int = ProgressionUtilKt.getProgressionLastElement(var2, 0, -2);
         if (var4 <= var2) {
            while (true) {
               if (StringsKt.equals(name, this.namesAndValues.get(i), true)) {
                  return this.namesAndValues.get(i + 1);
               }

               if (i == var4) {
                  break;
               }

               i -= 2;
            }
         }

         return null;
      }

      public fun build(): Headers {
         return new Headers(this.namesAndValues.toArray(new java.lang.String[0]), null);
      }
   }

   @SourceDebugExtension(["SMAP\nHeaders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Headers.kt\nokhttp3/Headers$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,458:1\n1#2:459\n*E\n"])
   companion object Companion private constructor() {
      private fun get(namesAndValues: Array<String>, name: String): String? {
         var var3: Int = namesAndValues.length - 2;
         var i: Int = namesAndValues.length - 2;
         var var5: Int = ProgressionUtilKt.getProgressionLastElement(namesAndValues.length - 2, 0, -2);
         if (var5 <= var3) {
            while (true) {
               if (StringsKt.equals(name, namesAndValues[i], true)) {
                  return namesAndValues[i + 1];
               }

               if (i == var5) {
                  break;
               }

               i -= 2;
            }
         }

         return null;
      }

      @JvmName(name = "of")
      public fun headersOf(vararg namesAndValues: String): Headers {
         if (namesAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Expected alternating header names and values".toString());
         } else {
            var namesAndValuesx: Array<java.lang.String> = namesAndValues.clone() as java.lang.String[];
            var i: Int = 0;

            for (int ix = namesAndValuesx.length; i < ix; i++) {
               if (namesAndValuesx[i] == null) {
                  throw new IllegalArgumentException("Headers cannot be null".toString());
               }

               namesAndValuesx[i] = StringsKt.trim(namesAndValuesx[i]).toString();
            }

            i = namesAndValuesx.length + -1;
            var ix: Int = 0;
            var var5: Int = ProgressionUtilKt.getProgressionLastElement(0, i, 2);
            if (0 <= var5) {
               while (true) {
                  var var13: java.lang.String = namesAndValuesx[ix];
                  var value: java.lang.String = namesAndValuesx[ix + 1];
                  this.checkName(var13);
                  this.checkValue(value, var13);
                  if (ix == var5) {
                     break;
                  }

                  ix += 2;
               }
            }

            return new Headers(namesAndValuesx, null);
         }
      }

      @Deprecated(message = "function name changed", replaceWith = @ReplaceWith(expression = "headersOf(*namesAndValues)", imports = []), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_of")
      public fun of(vararg namesAndValues: String): Headers {
         return this.of(Arrays.copyOf(namesAndValues, namesAndValues.length));
      }

      @JvmName(name = "of")
      public fun Map<String, String>.toHeaders(): Headers {
         var namesAndValues: Array<java.lang.String> = new java.lang.String[`$this$toHeaders`.size() * 2];
         var i: Int = 0;

         for (Entry var5 : $this$toHeaders.entrySet()) {
            var name: java.lang.String = StringsKt.trim(var5.getKey() as java.lang.String).toString();
            var value: java.lang.String = StringsKt.trim(var5.getValue() as java.lang.String).toString();
            this.checkName(name);
            this.checkValue(value, name);
            namesAndValues[i] = name;
            namesAndValues[i + 1] = value;
            i += 2;
         }

         return new Headers(namesAndValues, null);
      }

      @Deprecated(message = "function moved to extension", replaceWith = @ReplaceWith(expression = "headers.toHeaders()", imports = []), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_of")
      public fun of(headers: Map<String, String>): Headers {
         return this.of(headers);
      }

      private fun checkName(name: String) {
         if (name.length() <= 0) {
            throw new IllegalArgumentException("name is empty".toString());
         } else {
            var i: Int = 0;

            for (int var3 = name.length(); i < var3; i++) {
               var c: Char = name.charAt(i);
               if ('!' > c || c >= 127) {
                  throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", Integer.valueOf(c), i, name).toString());
               }
            }
         }
      }

      private fun checkValue(value: String, name: String) {
         var i: Int = 0;

         for (int var4 = value.length(); i < var4; i++) {
            var c: Char = value.charAt(i);
            if (c != '\t' && (' ' > c || c >= 127)) {
               throw new IllegalArgumentException(
                  (Util.format("Unexpected char %#04x at %d in %s value", Integer.valueOf(c), i, name)
                        + (if (Util.isSensitiveHeader(name)) "" else ": " + value))
                     .toString()
               );
            }
         }
      }
   }
}
