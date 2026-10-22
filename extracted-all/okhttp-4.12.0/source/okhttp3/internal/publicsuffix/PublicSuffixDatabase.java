package okhttp3.internal.publicsuffix

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.net.IDN
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.Ref.ObjectRef
import okhttp3.internal.Util
import okhttp3.internal.platform.Platform
import okio.BufferedSource
import okio.GzipSource
import okio.Okio
import okio.Source

class PublicSuffixDatabase {
   @JvmStatic
   public PublicSuffixDatabase.Companion Companion = new PublicSuffixDatabase.Companion(null);
   @JvmStatic
   public java.lang.String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
   @JvmStatic
   private byte[] WILDCARD_LABEL = new byte[]{42};
   @JvmStatic
   private java.util.List<java.lang.String> PREVAILING_RULE = CollectionsKt.listOf("*");
   @JvmStatic
   private char EXCEPTION_MARKER = '!';
   @JvmStatic
   private PublicSuffixDatabase instance = new PublicSuffixDatabase();

   private final val listRead: AtomicBoolean = new AtomicBoolean(false)
   private final lateinit var publicSuffixExceptionListBytes: ByteArray
   private final lateinit var publicSuffixListBytes: ByteArray
   private final val readCompleteLatch: CountDownLatch = new CountDownLatch(1)


   public fun getEffectiveTldPlusOne(domain: String): String? {
      var unicodeDomain: java.lang.String = IDN.toUnicode(domain);
      var domainLabels: java.util.List = this.splitDomain(unicodeDomain);
      var rule: java.util.List = this.findMatchingRule(domainLabels);
      return if (domainLabels.size() == rule.size() && (rule.get(0) as java.lang.String).charAt(0) != '!')
         null
         else
         SequencesKt.joinToString$default(
            SequencesKt.drop(
               CollectionsKt.asSequence(this.splitDomain(domain)),
               if ((rule.get(0) as java.lang.String).charAt(0) == '!') domainLabels.size() - rule.size() else domainLabels.size() - (rule.size() + 1)
            ),
            ".",
            null,
            null,
            0,
            null,
            null,
            62,
            null
         );
   }

   private fun splitDomain(domain: String): List<String> {
      var domainLabels: java.util.List = StringsKt.split$default(domain, new char[]{'.'}, false, 0, 6, null);
      return if (CollectionsKt.last(domainLabels) == "") CollectionsKt.dropLast(domainLabels, 1) else domainLabels;
   }

   private fun findMatchingRule(domainLabels: List<String>): List<String> {
      if (!this.listRead.get() && this.listRead.compareAndSet(false, true)) {
         this.readTheListUninterruptibly();
      } else {
         try {
            this.readCompleteLatch.await();
         } catch (InterruptedException var11) {
            Thread.currentThread().interrupt();
         }
      }

      if (this.publicSuffixListBytes == null) {
         throw new IllegalStateException("Unable to load publicsuffixes.gz resource from the classpath.".toString());
      } else {
         var exactMatch: Int = 0;
         var wildcardMatch: Int = domainLabels.size();

         var exception: Array<ByteArray>;
         for (labelsWithWildcard = new byte[i][]; exactMatch < i; exactMatch++) {
            var wildcardRuleLabels: java.lang.String = domainLabels.get(exactMatch) as java.lang.String;
            var var10002: Charset = StandardCharsets.UTF_8;
            var var35: ByteArray = wildcardRuleLabels.getBytes(var10002);
            exception[exactMatch] = var35;
         }

         var domainLabelsUtf8Bytes: Array<ByteArray> = exception;
         var var12: java.lang.String = null;
         wildcardMatch = 0;

         for (int var17 = ((Object[])labelsWithWildcard).length; i < var17; i++) {
            var var10001: ByteArray = this.publicSuffixListBytes;
            if (this.publicSuffixListBytes == null) {
               Intrinsics.throwUninitializedPropertyAccessException("publicSuffixListBytes");
               var10001 = null;
            }

            var exactRuleLabels: java.lang.String = PublicSuffixDatabase.Companion.access$binarySearch(
               Companion, var10001, domainLabelsUtf8Bytes, wildcardMatch
            );
            if (exactRuleLabels != null) {
               var12 = exactRuleLabels;
               break;
            }
         }

         var var16: java.lang.String = null;
         if ((domainLabelsUtf8Bytes as Object[]).length > 1) {
            exception = (domainLabelsUtf8Bytes as Object[]).clone() as byte[][];
            var var21: Int = 0;

            for (int var25 = ((Object[])labelsWithWildcard).length - 1; labelIndex < var25; labelIndex++) {
               exception[var21] = WILDCARD_LABEL;
               var var33: ByteArray = this.publicSuffixListBytes;
               if (this.publicSuffixListBytes == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("publicSuffixListBytes");
                  var33 = null;
               }

               var rule: java.lang.String = PublicSuffixDatabase.Companion.access$binarySearch(Companion, var33, exception, var21);
               if (rule != null) {
                  var16 = rule;
                  break;
               }
            }
         }

         var var19: java.lang.String = null;
         if (var16 != null) {
            var var22: Int = 0;

            for (int var26 = ((Object[])domainLabelsUtf8Bytes).length - 1; labelIndex < var26; labelIndex++) {
               var var34: ByteArray = this.publicSuffixExceptionListBytes;
               if (this.publicSuffixExceptionListBytes == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("publicSuffixExceptionListBytes");
                  var34 = null;
               }

               var var28: java.lang.String = PublicSuffixDatabase.Companion.access$binarySearch(Companion, var34, domainLabelsUtf8Bytes, var22);
               if (var28 != null) {
                  var19 = var28;
                  break;
               }
            }
         }

         if (var19 != null) {
            return StringsKt.split$default('!' + var19, new char[]{'.'}, false, 0, 6, null);
         } else if (var12 == null && var16 == null) {
            return PREVAILING_RULE;
         } else {
            var var29: java.util.List;
            label78: {
               if (var12 != null) {
                  var29 = StringsKt.split$default(var12, new char[]{'.'}, false, 0, 6, null);
                  if (var29 != null) {
                     break label78;
                  }
               }

               var29 = CollectionsKt.emptyList();
            }

            label73: {
               if (var16 != null) {
                  var29 = StringsKt.split$default(var16, new char[]{'.'}, false, 0, 6, null);
                  if (var29 != null) {
                     break label73;
                  }
               }

               var29 = CollectionsKt.emptyList();
            }

            return if (var29.size() > var29.size()) var29 else var29;
         }
      }
   }

   private fun readTheListUninterruptibly() {
      label43: {
         var interrupted: Boolean = false;

         label44: {
            try {
               while (true) {
                  try {
                     this.readTheList();
                     break label44;
                  } catch (InterruptedIOException var3) {
                     Thread.interrupted();
                     interrupted = true;
                  } catch (IOException var4) {
                     Platform.Companion.get().log("Failed to read public suffix list", 5, var4);
                     break;
                  }
               }
            } catch (java.lang.Throwable var5) {
               if (interrupted) {
                  Thread.currentThread().interrupt();
               }
            }

            if (interrupted) {
               Thread.currentThread().interrupt();
            }
         }

         if (interrupted) {
            Thread.currentThread().interrupt();
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readTheList() {
      label41: {
         label40: {
            try {
               var publicSuffixListBytes: ObjectRef = new ObjectRef();
               var publicSuffixExceptionListBytes: ObjectRef = new ObjectRef();
               var var10000: InputStream = PublicSuffixDatabase.class.getResourceAsStream("publicsuffixes.gz");
               if (var10000 == null) {
                  break label40;
               }

               var var4: Closeable = Okio.buffer((new GzipSource(Okio.source(var10000))) as Source) as Closeable;
               var var5: java.lang.Throwable = null;

               try {
                  try {
                     publicSuffixListBytes.element = (var4 as BufferedSource).readByteArray((long)(var4 as BufferedSource).readInt());
                     publicSuffixExceptionListBytes.element = (var4 as BufferedSource).readByteArray((long)(var4 as BufferedSource).readInt());
                  } catch (java.lang.Throwable var11) {
                     var5 = var11;
                     throw var11;
                  }
               } catch (java.lang.Throwable var12) {
                  CloseableKt.closeFinally(var4, var5);
               }

               CloseableKt.closeFinally(var4, null) {
                  var var10001: Any = publicSuffixListBytes.element;
                  this.publicSuffixListBytes = var10001 as byte[];
                  var10001 = publicSuffixExceptionListBytes.element;
                  this.publicSuffixExceptionListBytes = var10001 as byte[];
               }
            } catch (java.lang.Throwable var13) {
               this.readCompleteLatch.countDown();
            }

            this.readCompleteLatch.countDown();
         }

         this.readCompleteLatch.countDown();
      }
   }

   public fun setListBytes(publicSuffixListBytes: ByteArray, publicSuffixExceptionListBytes: ByteArray) {
      this.publicSuffixListBytes = publicSuffixListBytes;
      this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
      this.listRead.set(true);
      this.readCompleteLatch.countDown();
   }

   companion object Companion private constructor() {
      private const val EXCEPTION_MARKER: Char
      private final val PREVAILING_RULE: List<String>
      public const val PUBLIC_SUFFIX_RESOURCE: String
      private final val WILDCARD_LABEL: ByteArray
      private final val instance: PublicSuffixDatabase


      public fun get(): PublicSuffixDatabase {
         return PublicSuffixDatabase.access$getInstance$cp();
      }

      private fun ByteArray.binarySearch(labels: Array<ByteArray>, labelIndex: Int): String? {
         var low: Int = 0;
         var high: Int = `$this$binarySearch`.length;
         var match: java.lang.String = null;

         while (low < high) {
            var mid: Int = (low + high) / 2;

            while (mid > -1 && $this$binarySearch[mid] != 10) {
               mid--;
            }

            mid++;
            var end: Int = 1;

            while ($this$binarySearch[mid + end] != 10) {
               end++;
            }

            var publicSuffixLength: Int = mid + end - mid;
            var currentLabelIndex: Int = labelIndex;
            var currentLabelByteIndex: Int = 0;
            var publicSuffixByteIndex: Int = 0;
            var expectDot: Boolean = false;

            var var20: Int;
            while (true) {
               var var21: Int;
               if (expectDot) {
                  var21 = 46;
                  expectDot = false;
               } else {
                  var21 = Util.and(labels[currentLabelIndex][currentLabelByteIndex], 255);
               }

               var20 = var21 - Util.and(`$this$binarySearch`[mid + publicSuffixByteIndex], 255);
               if (var20 != 0) {
                  break;
               }

               publicSuffixByteIndex++;
               currentLabelByteIndex++;
               if (publicSuffixByteIndex == publicSuffixLength) {
                  break;
               }

               if (labels[currentLabelIndex].length == currentLabelByteIndex) {
                  if (currentLabelIndex == (labels as Object[]).length - 1) {
                     break;
                  }

                  currentLabelIndex++;
                  currentLabelByteIndex = -1;
                  expectDot = true;
               }
            }

            if (var20 < 0) {
               high = mid - 1;
            } else if (var20 > 0) {
               low = mid + end + 1;
            } else {
               var var22: Int = publicSuffixLength - publicSuffixByteIndex;
               var var23: Int = labels[currentLabelIndex].length - currentLabelByteIndex;
               var i: Int = currentLabelIndex + 1;

               for (int var18 = ((Object[])labels).length; i < var18; i++) {
                  var23 += labels[i].length;
               }

               if (var23 < var22) {
                  high = mid - 1;
               } else {
                  if (var23 <= var22) {
                     var var10000: Charset = StandardCharsets.UTF_8;
                     match = new java.lang.String(`$this$binarySearch`, mid, publicSuffixLength, var10000);
                     break;
                  }

                  low = mid + end + 1;
               }
            }
         }

         return match;
      }
   }
}
