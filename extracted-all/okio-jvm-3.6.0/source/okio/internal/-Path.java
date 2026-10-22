@file:JvmName(name = "-Path")

@file:SourceDebugExtension(["SMAP\nPath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Path.kt\nokio/internal/-Path\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,406:1\n59#1,22:407\n209#1:433\n209#1:434\n1549#2:429\n1620#2,3:430\n*S KotlinDebug\n*F\n+ 1 Path.kt\nokio/internal/-Path\n*L\n53#1:407,22\n199#1:433\n204#1:434\n53#1:429\n53#1:430,3\n*E\n"])

package okio.internal

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import okio.Buffer
import okio.ByteString
import okio.Path

private final val ANY_SLASH: ByteString = ByteString.Companion.encodeUtf8("/\\")
private final val BACKSLASH: ByteString = ByteString.Companion.encodeUtf8("\\")
private final val DOT: ByteString = ByteString.Companion.encodeUtf8(".")
private final val DOT_DOT: ByteString = ByteString.Companion.encodeUtf8("..")
private final val SLASH: ByteString = ByteString.Companion.encodeUtf8("/")
private final val indexOfLastSlash: Int
   private final get() {
      var lastSlash: Int = ByteString.lastIndexOf$default(`$this$indexOfLastSlash`.getBytes$okio(), SLASH, 0, 2, null);
      return if (lastSlash != -1) lastSlash else ByteString.lastIndexOf$default(`$this$indexOfLastSlash`.getBytes$okio(), BACKSLASH, 0, 2, null);
   }

private final val slash: ByteString?
   private final get() {
      return if (ByteString.indexOf$default(`$this$slash`.getBytes$okio(), SLASH, 0, 2, null) != -1)
         SLASH
         else
         (if (ByteString.indexOf$default(`$this$slash`.getBytes$okio(), BACKSLASH, 0, 2, null) != -1) BACKSLASH else null);
   }


/** @deprecated */
@JvmSynthetic
fun `getSLASH$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `getBACKSLASH$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `getANY_SLASH$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `getDOT$annotations`() {
}

/** @deprecated */
@JvmSynthetic
fun `getDOT_DOT$annotations`() {
}

internal inline fun Path.commonRoot(): Path? {
   var rootLength: Int = access$rootLength(`$this$commonRoot`);
   return if (rootLength == -1) null else new Path(`$this$commonRoot`.getBytes$okio().substring(0, rootLength));
}

internal inline fun Path.commonSegments(): List<String> {
   var `$this$map$iv`: Path = `$this$commonSegments`;
   var `$this$mapTo$iv$iv`: java.util.List = new ArrayList();
   var `destination$iv$iv`: Int = access$rootLength(`$this$commonSegments`);
   if (`destination$iv$iv` == -1) {
      `destination$iv$iv` = 0;
   } else if (`destination$iv$iv` < `$this$commonSegments`.getBytes$okio().size() && `$this$commonSegments`.getBytes$okio().getByte(`destination$iv$iv`) == 92) {
      `destination$iv$iv`++;
   }

   var `$i$f$mapTo`: Int = `destination$iv$iv`;

   for (int var7 = $this$commonSegments.getBytes$okio().size(); i$iv < var7; i$iv++) {
      if (`$this$map$iv`.getBytes$okio().getByte(`$i$f$mapTo`) == 47 || `$this$map$iv`.getBytes$okio().getByte(`$i$f$mapTo`) == 92) {
         `$this$mapTo$iv$iv`.add(`$this$map$iv`.getBytes$okio().substring(`destination$iv$iv`, `$i$f$mapTo`));
         `destination$iv$iv` = `$i$f$mapTo` + 1;
      }
   }

   if (`destination$iv$iv` < `$this$map$iv`.getBytes$okio().size()) {
      `$this$mapTo$iv$iv`.add(`$this$map$iv`.getBytes$okio().substring(`destination$iv$iv`, `$this$map$iv`.getBytes$okio().size()));
   }

   var var12: java.lang.Iterable = `$this$mapTo$iv$iv`;
   var var14: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$mapTo$iv$iv`, 10));

   for (Object item$iv$iv : var12) {
      var14.add((`item$iv$iv` as ByteString).utf8());
   }

   return var14 as java.util.List<java.lang.String>;
}

internal inline fun Path.commonSegmentsBytes(): List<ByteString> {
   var result: java.util.List = new ArrayList();
   var segmentStart: Int = access$rootLength(`$this$commonSegmentsBytes`);
   if (segmentStart == -1) {
      segmentStart = 0;
   } else if (segmentStart < `$this$commonSegmentsBytes`.getBytes$okio().size() && `$this$commonSegmentsBytes`.getBytes$okio().getByte(segmentStart) == 92) {
      segmentStart++;
   }

   var i: Int = segmentStart;

   for (int var5 = $this$commonSegmentsBytes.getBytes$okio().size(); i < var5; i++) {
      if (`$this$commonSegmentsBytes`.getBytes$okio().getByte(i) == 47 || `$this$commonSegmentsBytes`.getBytes$okio().getByte(i) == 92) {
         result.add(`$this$commonSegmentsBytes`.getBytes$okio().substring(segmentStart, i));
         segmentStart = i + 1;
      }
   }

   if (segmentStart < `$this$commonSegmentsBytes`.getBytes$okio().size()) {
      result.add(`$this$commonSegmentsBytes`.getBytes$okio().substring(segmentStart, `$this$commonSegmentsBytes`.getBytes$okio().size()));
   }

   return result;
}

private fun Path.rootLength(): Int {
   if (`$this$rootLength`.getBytes$okio().size() == 0) {
      return -1;
   } else if (`$this$rootLength`.getBytes$okio().getByte(0) == 47) {
      return 1;
   } else if (`$this$rootLength`.getBytes$okio().getByte(0) == 92) {
      if (`$this$rootLength`.getBytes$okio().size() > 2 && `$this$rootLength`.getBytes$okio().getByte(1) == 92) {
         var var2: Int = `$this$rootLength`.getBytes$okio().indexOf(BACKSLASH, 2);
         if (var2 == -1) {
            var2 = `$this$rootLength`.getBytes$okio().size();
         }

         return var2;
      } else {
         return 1;
      }
   } else if (`$this$rootLength`.getBytes$okio().size() > 2
      && `$this$rootLength`.getBytes$okio().getByte(1) == 58
      && `$this$rootLength`.getBytes$okio().getByte(2) == 92) {
      var c: Char = (char)`$this$rootLength`.getBytes$okio().getByte(0);
      return if ((97 > c || c >= 123) && (65 > c || c >= 91)) -1 else 3;
   } else {
      return -1;
   }
}

internal inline fun Path.commonIsAbsolute(): Boolean {
   return access$rootLength(`$this$commonIsAbsolute`) != -1;
}

internal inline fun Path.commonIsRelative(): Boolean {
   return access$rootLength(`$this$commonIsRelative`) == -1;
}

internal inline fun Path.commonVolumeLetter(): Char? {
   if (ByteString.indexOf$default(`$this$commonVolumeLetter`.getBytes$okio(), access$getSLASH$p(), 0, 2, null) != -1) {
      return null;
   } else if (`$this$commonVolumeLetter`.getBytes$okio().size() < 2) {
      return null;
   } else if (`$this$commonVolumeLetter`.getBytes$okio().getByte(1) != 58) {
      return null;
   } else {
      var c: Char = (char)`$this$commonVolumeLetter`.getBytes$okio().getByte(0);
      return if (('a' > c || c >= '{') && ('A' > c || c >= '[')) null else c;
   }
}

internal inline fun Path.commonNameBytes(): ByteString {
   var lastSlash: Int = access$getIndexOfLastSlash(`$this$commonNameBytes`);
   return if (lastSlash != -1)
      ByteString.substring$default(`$this$commonNameBytes`.getBytes$okio(), lastSlash + 1, 0, 2, null)
      else
      (
         if (`$this$commonNameBytes`.volumeLetter() != null && `$this$commonNameBytes`.getBytes$okio().size() == 2)
            ByteString.EMPTY
            else
            `$this$commonNameBytes`.getBytes$okio()
      );
}

internal inline fun Path.commonName(): String {
   return `$this$commonName`.nameBytes().utf8();
}

internal inline fun Path.commonParent(): Path? {
   if (!(`$this$commonParent`.getBytes$okio() == access$getDOT$p())
      && !(`$this$commonParent`.getBytes$okio() == access$getSLASH$p())
      && !(`$this$commonParent`.getBytes$okio() == access$getBACKSLASH$p())
      && !access$lastSegmentIsDotDot(`$this$commonParent`)) {
      var lastSlash: Int = access$getIndexOfLastSlash(`$this$commonParent`);
      if (lastSlash == 2 && `$this$commonParent`.volumeLetter() != null) {
         return if (`$this$commonParent`.getBytes$okio().size() == 3)
            null
            else
            new Path(ByteString.substring$default(`$this$commonParent`.getBytes$okio(), 0, 3, 1, null));
      } else if (lastSlash == 1 && `$this$commonParent`.getBytes$okio().startsWith(access$getBACKSLASH$p())) {
         return null;
      } else if (lastSlash == -1 && `$this$commonParent`.volumeLetter() != null) {
         return if (`$this$commonParent`.getBytes$okio().size() == 2)
            null
            else
            new Path(ByteString.substring$default(`$this$commonParent`.getBytes$okio(), 0, 2, 1, null));
      } else if (lastSlash == -1) {
         return new Path(access$getDOT$p());
      } else {
         return if (lastSlash == 0)
            new Path(ByteString.substring$default(`$this$commonParent`.getBytes$okio(), 0, 1, 1, null))
            else
            new Path(ByteString.substring$default(`$this$commonParent`.getBytes$okio(), 0, lastSlash, 1, null));
      }
   } else {
      return null;
   }
}

private fun Path.lastSegmentIsDotDot(): Boolean {
   if (`$this$lastSegmentIsDotDot`.getBytes$okio().endsWith(DOT_DOT)) {
      if (`$this$lastSegmentIsDotDot`.getBytes$okio().size() == 2) {
         return true;
      }

      if (`$this$lastSegmentIsDotDot`.getBytes$okio().rangeEquals(`$this$lastSegmentIsDotDot`.getBytes$okio().size() - 3, SLASH, 0, 1)) {
         return true;
      }

      if (`$this$lastSegmentIsDotDot`.getBytes$okio().rangeEquals(`$this$lastSegmentIsDotDot`.getBytes$okio().size() - 3, BACKSLASH, 0, 1)) {
         return true;
      }
   }

   return false;
}

internal inline fun Path.commonIsRoot(): Boolean {
   return access$rootLength(`$this$commonIsRoot`) == `$this$commonIsRoot`.getBytes$okio().size();
}

internal inline fun Path.commonResolve(child: String, normalize: Boolean): Path {
   return commonResolve(`$this$commonResolve`, toPath(new Buffer().writeUtf8(child), false), normalize);
}

internal inline fun Path.commonResolve(child: ByteString, normalize: Boolean): Path {
   return commonResolve(`$this$commonResolve`, toPath(new Buffer().write(child), false), normalize);
}

internal inline fun Path.commonResolve(child: Buffer, normalize: Boolean): Path {
   return commonResolve(`$this$commonResolve`, toPath(child, false), normalize);
}

internal fun Path.commonResolve(child: Path, normalize: Boolean): Path {
   if (!child.isAbsolute() && child.volumeLetter() == null) {
      var var10000: ByteString = getSlash(`$this$commonResolve`);
      if (var10000 == null) {
         var10000 = getSlash(child);
         if (var10000 == null) {
            var10000 = toSlash(Path.DIRECTORY_SEPARATOR);
         }
      }

      var buffer: Buffer = new Buffer();
      buffer.write(`$this$commonResolve`.getBytes$okio());
      if (buffer.size() > 0L) {
         buffer.write(var10000);
      }

      buffer.write(child.getBytes$okio());
      return toPath(buffer, normalize);
   } else {
      return child;
   }
}

internal inline fun Path.commonRelativeTo(other: Path): Path {
   if (!(`$this$commonRelativeTo`.getRoot() == other.getRoot())) {
      throw new IllegalArgumentException(
         ("Paths of different roots cannot be relative to each other: " + `$this$commonRelativeTo` + " and " + other).toString()
      );
   } else {
      var thisSegments: java.util.List = `$this$commonRelativeTo`.getSegmentsBytes();
      var otherSegments: java.util.List = other.getSegmentsBytes();
      var firstNewSegmentIndex: Int = 0;
      var minSegmentsSize: Int = Math.min(thisSegments.size(), otherSegments.size());

      while (firstNewSegmentIndex < minSegmentsSize && thisSegments.get(firstNewSegmentIndex) == otherSegments.get(firstNewSegmentIndex)) {
         firstNewSegmentIndex++;
      }

      if (firstNewSegmentIndex == minSegmentsSize && `$this$commonRelativeTo`.getBytes$okio().size() == other.getBytes$okio().size()) {
         return Path.Companion.get$default(Path.Companion, ".", false, 1, null);
      } else if (otherSegments.subList(firstNewSegmentIndex, otherSegments.size()).indexOf(access$getDOT_DOT$p()) != -1) {
         throw new IllegalArgumentException(("Impossible relative path to resolve: " + `$this$commonRelativeTo` + " and " + other).toString());
      } else {
         var buffer: Buffer = new Buffer();
         var var10000: ByteString = access$getSlash(other);
         if (var10000 == null) {
            var10000 = access$getSlash(`$this$commonRelativeTo`);
            if (var10000 == null) {
               var10000 = access$toSlash(Path.DIRECTORY_SEPARATOR);
            }
         }

         var slash: ByteString = var10000;
         var i: Int = firstNewSegmentIndex;

         for (int var10 = otherSegments.size(); i < var10; i++) {
            buffer.write(access$getDOT_DOT$p());
            buffer.write(slash);
         }

         i = firstNewSegmentIndex;

         for (int var16 = thisSegments.size(); i < var16; i++) {
            buffer.write(thisSegments.get(i) as ByteString);
            buffer.write(slash);
         }

         return toPath(buffer, false);
      }
   }
}

internal inline fun Path.commonNormalized(): Path {
   return Path.Companion.get(`$this$commonNormalized`.toString(), true);
}

internal inline fun Path.commonCompareTo(other: Path): Int {
   return `$this$commonCompareTo`.getBytes$okio().compareTo(other.getBytes$okio());
}

internal inline fun Path.commonEquals(other: Any?): Boolean {
   return other is Path && (other as Path).getBytes$okio() == `$this$commonEquals`.getBytes$okio();
}

internal inline fun Path.commonHashCode(): Int {
   return `$this$commonHashCode`.getBytes$okio().hashCode();
}

internal inline fun Path.commonToString(): String {
   return `$this$commonToString`.getBytes$okio().utf8();
}

internal fun String.commonToPath(normalize: Boolean): Path {
   return toPath(new Buffer().writeUtf8(`$this$commonToPath`), normalize);
}

internal fun Buffer.toPath(normalize: Boolean): Path {
   var slash: ByteString = null;
   var result: Buffer = new Buffer();

   var leadingSlashCount: Int;
   for (leadingSlashCount = 0; $this$toPath.rangeEquals(0L, SLASH) || $this$toPath.rangeEquals(0L, BACKSLASH); leadingSlashCount++) {
      var windowsUncPath: Byte = `$this$toPath`.readByte();
      var var10000: ByteString = slash;
      if (slash == null) {
         var10000 = toSlash(windowsUncPath);
      }

      slash = var10000;
   }

   var var11: Boolean = leadingSlashCount >= 2 && slash == BACKSLASH;
   if (var11) {
      result.write(slash);
      result.write(slash);
   } else if (leadingSlashCount > 0) {
      result.write(slash);
   } else {
      var absolute: Long = `$this$toPath`.indexOfElement(ANY_SLASH);
      var var15: ByteString = slash;
      if (slash == null) {
         var15 = if (absolute == -1L) toSlash(Path.DIRECTORY_SEPARATOR) else toSlash(`$this$toPath`.getByte(absolute));
      }

      slash = var15;
      if (startsWithVolumeLetterAndColon(`$this$toPath`, var15)) {
         if (absolute == 2L) {
            result.write(`$this$toPath`, 3L);
         } else {
            result.write(`$this$toPath`, 2L);
         }
      }
   }

   var var12: Boolean = result.size() > 0L;
   var canonicalParts: java.util.List = new ArrayList();

   while (!$this$toPath.exhausted()) {
      var limitx: Long = `$this$toPath`.indexOfElement(ANY_SLASH);
      var var14: ByteString;
      if (limitx == -1L) {
         var14 = `$this$toPath`.readByteString();
      } else {
         var14 = `$this$toPath`.readByteString(limitx);
         `$this$toPath`.readByte();
      }

      if (var14 == DOT_DOT) {
         if (!var12 || !canonicalParts.isEmpty()) {
            if (normalize && (var12 || !canonicalParts.isEmpty() && !(CollectionsKt.last(canonicalParts) == DOT_DOT))) {
               if (!var11 || canonicalParts.size() != 1) {
                  CollectionsKt.removeLastOrNull(canonicalParts);
               }
            } else {
               canonicalParts.add(var14);
            }
         }
      } else if (!(var14 == DOT) && !(var14 == ByteString.EMPTY)) {
         canonicalParts.add(var14);
      }
   }

   var var13: Int = 0;

   for (int var9 = canonicalParts.size(); i < var9; i++) {
      if (var13 > 0) {
         result.write(slash);
      }

      result.write(canonicalParts.get(var13) as ByteString);
   }

   if (result.size() == 0L) {
      result.write(DOT);
   }

   return new Path(result.readByteString());
}

private fun String.toSlash(): ByteString {
   var var10000: ByteString;
   if (`$this$toSlash` == "/") {
      var10000 = SLASH;
   } else {
      if (!(`$this$toSlash` == "\\")) {
         throw new IllegalArgumentException("not a directory separator: " + `$this$toSlash`);
      }

      var10000 = BACKSLASH;
   }

   return var10000;
}

private fun Byte.toSlash(): ByteString {
   var var10000: ByteString;
   switch ($this$toSlash) {
      case 47:
         var10000 = SLASH;
         break;
      case 92:
         var10000 = BACKSLASH;
         break;
      default:
         throw new IllegalArgumentException("not a directory separator: " + `$this$toSlash`);
   }

   return var10000;
}

private fun Buffer.startsWithVolumeLetterAndColon(slash: ByteString): Boolean {
   if (!(slash == BACKSLASH)) {
      return false;
   } else if (`$this$startsWithVolumeLetterAndColon`.size() < 2L) {
      return false;
   } else if (`$this$startsWithVolumeLetterAndColon`.getByte(1L) != 58) {
      return false;
   } else {
      var b: Char = (char)`$this$startsWithVolumeLetterAndColon`.getByte(0L);
      return 'a' <= b && b < '{' || 'A' <= b && b < '[';
   }
}

@JvmSynthetic
fun `access$rootLength`(`$receiver`: Path): Int {
   return rootLength(`$receiver`);
}

@JvmSynthetic
fun `access$getSLASH$p`(): ByteString {
   return SLASH;
}

@JvmSynthetic
fun `access$getIndexOfLastSlash`(`$receiver`: Path): Int {
   return getIndexOfLastSlash(`$receiver`);
}

@JvmSynthetic
fun `access$getDOT$p`(): ByteString {
   return DOT;
}

@JvmSynthetic
fun `access$getBACKSLASH$p`(): ByteString {
   return BACKSLASH;
}

@JvmSynthetic
fun `access$lastSegmentIsDotDot`(`$receiver`: Path): Boolean {
   return lastSegmentIsDotDot(`$receiver`);
}

@JvmSynthetic
fun `access$getDOT_DOT$p`(): ByteString {
   return DOT_DOT;
}

@JvmSynthetic
fun `access$getSlash`(`$receiver`: Path): ByteString {
   return getSlash(`$receiver`);
}

@JvmSynthetic
fun `access$toSlash`(`$receiver`: java.lang.String): ByteString {
   return toSlash(`$receiver`);
}

