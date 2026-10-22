package okhttp3.internal.http2

import java.io.IOException
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.LinkedHashMap
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import okio.Source

object Hpack private constructor() {
   @JvmStatic
   public Hpack INSTANCE = new Hpack();
   @JvmStatic
   private Header[] STATIC_HEADER_TABLE = new Header[]{
      new Header(Header.TARGET_AUTHORITY, ""),
      new Header(Header.TARGET_METHOD, "GET"),
      new Header(Header.TARGET_METHOD, "POST"),
      new Header(Header.TARGET_PATH, "/"),
      new Header(Header.TARGET_PATH, "/index.html"),
      new Header(Header.TARGET_SCHEME, "http"),
      new Header(Header.TARGET_SCHEME, "https"),
      new Header(Header.RESPONSE_STATUS, "200"),
      new Header(Header.RESPONSE_STATUS, "204"),
      new Header(Header.RESPONSE_STATUS, "206"),
      new Header(Header.RESPONSE_STATUS, "304"),
      new Header(Header.RESPONSE_STATUS, "400"),
      new Header(Header.RESPONSE_STATUS, "404"),
      new Header(Header.RESPONSE_STATUS, "500"),
      new Header("accept-charset", ""),
      new Header("accept-encoding", "gzip, deflate"),
      new Header("accept-language", ""),
      new Header("accept-ranges", ""),
      new Header("accept", ""),
      new Header("access-control-allow-origin", ""),
      new Header("age", ""),
      new Header("allow", ""),
      new Header("authorization", ""),
      new Header("cache-control", ""),
      new Header("content-disposition", ""),
      new Header("content-encoding", ""),
      new Header("content-language", ""),
      new Header("content-length", ""),
      new Header("content-location", ""),
      new Header("content-range", ""),
      new Header("content-type", ""),
      new Header("cookie", ""),
      new Header("date", ""),
      new Header("etag", ""),
      new Header("expect", ""),
      new Header("expires", ""),
      new Header("from", ""),
      new Header("host", ""),
      new Header("if-match", ""),
      new Header("if-modified-since", ""),
      new Header("if-none-match", ""),
      new Header("if-range", ""),
      new Header("if-unmodified-since", ""),
      new Header("last-modified", ""),
      new Header("link", ""),
      new Header("location", ""),
      new Header("max-forwards", ""),
      new Header("proxy-authenticate", ""),
      new Header("proxy-authorization", ""),
      new Header("range", ""),
      new Header("referer", ""),
      new Header("refresh", ""),
      new Header("retry-after", ""),
      new Header("server", ""),
      new Header("set-cookie", ""),
      new Header("strict-transport-security", ""),
      new Header("transfer-encoding", ""),
      new Header("user-agent", ""),
      new Header("vary", ""),
      new Header("via", ""),
      new Header("www-authenticate", "")
   };

   public final val NAME_TO_FIRST_INDEX: Map<ByteString, Int> = INSTANCE.nameToFirstIndex()
   private const val PREFIX_4_BITS: Int = 15
   private const val PREFIX_5_BITS: Int = 31
   private const val PREFIX_6_BITS: Int = 63
   private const val PREFIX_7_BITS: Int = 127
   private const val SETTINGS_HEADER_TABLE_SIZE: Int = 4096
   private const val SETTINGS_HEADER_TABLE_SIZE_LIMIT: Int = 16384
   public final val STATIC_HEADER_TABLE: Array<Header>


   private fun nameToFirstIndex(): Map<ByteString, Int> {
      var result: LinkedHashMap = new LinkedHashMap(STATIC_HEADER_TABLE.length);
      var i: Int = 0;

      for (int var3 = STATIC_HEADER_TABLE.length; i < var3; i++) {
         if (!result.containsKey(STATIC_HEADER_TABLE[i].name)) {
            result.put(STATIC_HEADER_TABLE[i].name, i);
         }
      }

      var var10000: java.util.Map = Collections.unmodifiableMap(result);
      return var10000;
   }

   @Throws(java/io/IOException::class)
   public fun checkLowercase(name: ByteString): ByteString {
      var i: Int = 0;

      for (int var3 = name.size(); i < var3; i++) {
         var var4: Byte = name.getByte(i);
         if (65 <= var4 && var4 < 91) {
            throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
         }
      }

      return name;
   }

   class Reader @JvmOverloads  public constructor(source: Source, headerTableSizeSetting: Int, maxDynamicTableByteCount: Int) {
      @JvmField
      open public Header[] dynamicTable;

      public final var dynamicTable: Array<Header?>
         private set
      public final var dynamicTableByteCount: Int
         private set
      public final var headerCount: Int
         private set
      private final val headerList: MutableList<Header>
      private final val headerTableSizeSetting: Int
      private final var maxDynamicTableByteCount: Int
      private final var nextHeaderIndex: Int
      private final val source: BufferedSource

      init {
         this.headerTableSizeSetting = headerTableSizeSetting;
         this.maxDynamicTableByteCount = maxDynamicTableByteCount;
         this.headerList = new ArrayList<>();
         this.source = Okio.buffer(source);
         this.dynamicTable = new Header[8];
         this.nextHeaderIndex = this.dynamicTable.length - 1;
      }

      public fun getAndResetHeaderList(): List<Header> {
         var result: java.util.List = CollectionsKt.toList(this.headerList);
         this.headerList.clear();
         return result;
      }

      public fun maxDynamicTableByteCount(): Int {
         return this.maxDynamicTableByteCount;
      }

      private fun adjustDynamicTableByteCount() {
         if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
            if (this.maxDynamicTableByteCount == 0) {
               this.clearDynamicTable();
            } else {
               this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
            }
         }
      }

      private fun clearDynamicTable() {
         ArraysKt.fill$default(this.dynamicTable, null, 0, 0, 6, null);
         this.nextHeaderIndex = this.dynamicTable.length - 1;
         this.headerCount = 0;
         this.dynamicTableByteCount = 0;
      }

      private fun evictToRecoverBytes(bytesToRecover: Int): Int {
         var bytesToRecoverx: Int = bytesToRecover;
         var entriesToEvict: Int = 0;
         if (bytesToRecover > 0) {
            for (int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecoverx > 0; j--) {
               var var10000: Header = this.dynamicTable[j];
               bytesToRecoverx -= var10000.hpackSize;
               this.dynamicTableByteCount = this.dynamicTableByteCount - var10000.hpackSize;
               this.headerCount += -1;
               entriesToEvict++;
            }

            System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + entriesToEvict, this.headerCount);
            this.nextHeaderIndex += entriesToEvict;
         }

         return entriesToEvict;
      }

      @Throws(java/io/IOException::class)
      public fun readHeaders() {
         while (!this.source.exhausted()) {
            var b: Int = Util.and(this.source.readByte(), 255);
            if (b == 128) {
               throw new IOException("index == 0");
            }

            if ((b and 128) == 128) {
               this.readIndexedHeader(this.readInt(b, 127) - 1);
            } else if (b == 64) {
               this.readLiteralHeaderWithIncrementalIndexingNewName();
            } else if ((b and 64) == 64) {
               this.readLiteralHeaderWithIncrementalIndexingIndexedName(this.readInt(b, 63) - 1);
            } else if ((b and 32) == 32) {
               this.maxDynamicTableByteCount = this.readInt(b, 31);
               if (this.maxDynamicTableByteCount < 0 || this.maxDynamicTableByteCount > this.headerTableSizeSetting) {
                  throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
               }

               this.adjustDynamicTableByteCount();
            } else if (b != 16 && b != 0) {
               this.readLiteralHeaderWithoutIndexingIndexedName(this.readInt(b, 15) - 1);
            } else {
               this.readLiteralHeaderWithoutIndexingNewName();
            }
         }
      }

      @Throws(java/io/IOException::class)
      private fun readIndexedHeader(index: Int) {
         if (this.isStaticHeader(index)) {
            this.headerList.add(Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index]);
         } else {
            var var3: Int = this.dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (var3 < 0 || var3 >= this.dynamicTable.length) {
               throw new IOException("Header index too large " + (index + 1));
            }

            var var10000: java.util.Collection = this.headerList;
            var var10001: Header = this.dynamicTable[var3];
            var10000.add(var10001);
         }
      }

      private fun dynamicTableIndex(index: Int): Int {
         return this.nextHeaderIndex + 1 + index;
      }

      @Throws(java/io/IOException::class)
      private fun readLiteralHeaderWithoutIndexingIndexedName(index: Int) {
         this.headerList.add(new Header(this.getName(index), this.readByteString()));
      }

      @Throws(java/io/IOException::class)
      private fun readLiteralHeaderWithoutIndexingNewName() {
         this.headerList.add(new Header(Hpack.INSTANCE.checkLowercase(this.readByteString()), this.readByteString()));
      }

      @Throws(java/io/IOException::class)
      private fun readLiteralHeaderWithIncrementalIndexingIndexedName(nameIndex: Int) {
         this.insertIntoDynamicTable(-1, new Header(this.getName(nameIndex), this.readByteString()));
      }

      @Throws(java/io/IOException::class)
      private fun readLiteralHeaderWithIncrementalIndexingNewName() {
         this.insertIntoDynamicTable(-1, new Header(Hpack.INSTANCE.checkLowercase(this.readByteString()), this.readByteString()));
      }

      @Throws(java/io/IOException::class)
      private fun getName(index: Int): ByteString {
         var var10000: ByteString;
         if (this.isStaticHeader(index)) {
            var10000 = Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index].name;
         } else {
            var dynamicTableIndex: Int = this.dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex < 0 || dynamicTableIndex >= this.dynamicTable.length) {
               throw new IOException("Header index too large " + (index + 1));
            }

            var var3: Header = this.dynamicTable[dynamicTableIndex];
            var10000 = var3.name;
         }

         return var10000;
      }

      private fun isStaticHeader(index: Int): Boolean {
         return index >= 0 && index <= Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length - 1;
      }

      private fun insertIntoDynamicTable(index: Int, entry: Header) {
         this.headerList.add(entry);
         var delta: Int = entry.hpackSize;
         if (index != -1) {
            var var10001: Header = this.dynamicTable[this.dynamicTableIndex(index)];
            delta -= var10001.hpackSize;
         }

         if (delta > this.maxDynamicTableByteCount) {
            this.clearDynamicTable();
         } else {
            var entriesEvicted: Int = this.evictToRecoverBytes(this.dynamicTableByteCount + delta - this.maxDynamicTableByteCount);
            if (index == -1) {
               if (this.headerCount + 1 > this.dynamicTable.length) {
                  var doubled: Array<Header> = new Header[this.dynamicTable.length * 2];
                  System.arraycopy(this.dynamicTable, 0, doubled, this.dynamicTable.length, this.dynamicTable.length);
                  this.nextHeaderIndex = this.dynamicTable.length - 1;
                  this.dynamicTable = doubled;
               }

               var var8: Int = this.nextHeaderIndex;
               this.nextHeaderIndex += -1;
               this.dynamicTable[var8] = entry;
               var8 = this.headerCount++;
            } else {
               this.dynamicTable[index + this.dynamicTableIndex(index) + entriesEvicted] = entry;
            }

            this.dynamicTableByteCount += delta;
         }
      }

      @Throws(java/io/IOException::class)
      private fun readByte(): Int {
         return Util.and(this.source.readByte(), 255);
      }

      @Throws(java/io/IOException::class)
      public fun readInt(firstByte: Int, prefixMask: Int): Int {
         var prefix: Int = firstByte and prefixMask;
         if ((firstByte and prefixMask) < prefixMask) {
            return prefix;
         } else {
            var result: Int = prefixMask;
            var shift: Int = 0;

            while (true) {
               var b: Int = this.readByte();
               if ((b and 128) == 0) {
                  return result + (b shl shift);
               }

               result += (b and 127) shl shift;
               shift += 7;
            }
         }
      }

      @Throws(java/io/IOException::class)
      public fun readByteString(): ByteString {
         var firstByte: Int = this.readByte();
         var huffmanDecode: Boolean = (firstByte and 128) == 128;
         var length: Long = (long)this.readInt(firstByte, 127);
         var var10000: ByteString;
         if (huffmanDecode) {
            var decodeBuffer: Buffer = new Buffer();
            Huffman.INSTANCE.decode(this.source, length, decodeBuffer as BufferedSink);
            var10000 = decodeBuffer.readByteString();
         } else {
            var10000 = this.source.readByteString(length);
         }

         return var10000;
      }

      @JvmOverloads
      fun Reader(source: Source, headerTableSizeSetting: Int) {
         this(source, headerTableSizeSetting, 0, 4, null);
      }
   }

   class Writer @JvmOverloads  public constructor(headerTableSizeSetting: Int, useCompression: Boolean, out: Buffer) {
      @JvmField
      open public Header[] dynamicTable;

      public final var dynamicTable: Array<Header?>
         private set
      public final var dynamicTableByteCount: Int
         private set
      private final var emitDynamicTableSizeUpdate: Boolean
      public final var headerCount: Int
         private set
      public final var headerTableSizeSetting: Int
         private set
      public final var maxDynamicTableByteCount: Int
         private set
      private final var nextHeaderIndex: Int
      private final val out: Buffer
      private final var smallestHeaderTableSizeSetting: Int
      private final val useCompression: Boolean

      init {
         this.headerTableSizeSetting = headerTableSizeSetting;
         this.useCompression = useCompression;
         this.out = out;
         this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
         this.maxDynamicTableByteCount = this.headerTableSizeSetting;
         this.dynamicTable = new Header[8];
         this.nextHeaderIndex = this.dynamicTable.length - 1;
      }

      private fun clearDynamicTable() {
         ArraysKt.fill$default(this.dynamicTable, null, 0, 0, 6, null);
         this.nextHeaderIndex = this.dynamicTable.length - 1;
         this.headerCount = 0;
         this.dynamicTableByteCount = 0;
      }

      private fun evictToRecoverBytes(bytesToRecover: Int): Int {
         var bytesToRecoverx: Int = bytesToRecover;
         var entriesToEvict: Int = 0;
         if (bytesToRecover > 0) {
            for (int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecoverx > 0; j--) {
               var var10001: Header = this.dynamicTable[j];
               bytesToRecoverx -= var10001.hpackSize;
               var var6: Int = this.dynamicTableByteCount;
               var var10002: Header = this.dynamicTable[j];
               this.dynamicTableByteCount = var6 - var10002.hpackSize;
               this.headerCount += -1;
               entriesToEvict++;
            }

            System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + entriesToEvict, this.headerCount);
            Arrays.fill(this.dynamicTable, this.nextHeaderIndex + 1, this.nextHeaderIndex + 1 + entriesToEvict, null);
            this.nextHeaderIndex += entriesToEvict;
         }

         return entriesToEvict;
      }

      private fun insertIntoDynamicTable(entry: Header) {
         var delta: Int = entry.hpackSize;
         if (entry.hpackSize > this.maxDynamicTableByteCount) {
            this.clearDynamicTable();
         } else {
            this.evictToRecoverBytes(this.dynamicTableByteCount + entry.hpackSize - this.maxDynamicTableByteCount);
            if (this.headerCount + 1 > this.dynamicTable.length) {
               var index: Array<Header> = new Header[this.dynamicTable.length * 2];
               System.arraycopy(this.dynamicTable, 0, index, this.dynamicTable.length, this.dynamicTable.length);
               this.nextHeaderIndex = this.dynamicTable.length - 1;
               this.dynamicTable = index;
            }

            var var5: Int = this.nextHeaderIndex;
            this.nextHeaderIndex += -1;
            this.dynamicTable[var5] = entry;
            var5 = this.headerCount++;
            this.dynamicTableByteCount += delta;
         }
      }

      @Throws(java/io/IOException::class)
      public fun writeHeaders(headerBlock: List<Header>) {
         if (this.emitDynamicTableSizeUpdate) {
            if (this.smallestHeaderTableSizeSetting < this.maxDynamicTableByteCount) {
               this.writeInt(this.smallestHeaderTableSizeSetting, 31, 32);
            }

            this.emitDynamicTableSizeUpdate = false;
            this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
            this.writeInt(this.maxDynamicTableByteCount, 31, 32);
         }

         var i: Int = 0;

         for (int var3 = headerBlock.size(); i < var3; i++) {
            var header: Header = headerBlock.get(i) as Header;
            var name: ByteString = header.name.toAsciiLowercase();
            var value: ByteString = header.value;
            var headerIndex: Int = -1;
            var headerNameIndex: Int = -1;
            var staticIndex: Int = Hpack.INSTANCE.getNAME_TO_FIRST_INDEX().get(name);
            if (staticIndex != null) {
               headerNameIndex = staticIndex + 1;
               if (2 <= headerNameIndex && headerNameIndex < 8) {
                  if (Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex - 1].value == value) {
                     headerIndex = headerNameIndex;
                  } else if (Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex].value == value) {
                     headerIndex = headerNameIndex + 1;
                  }
               }
            }

            if (headerIndex == -1) {
               var j: Int = this.nextHeaderIndex + 1;

               for (int var11 = this.dynamicTable.length; j < var11; j++) {
                  var var10000: Header = this.dynamicTable[j];
                  if (var10000.name == name) {
                     var10000 = this.dynamicTable[j];
                     if (var10000.value == value) {
                        headerIndex = j - this.nextHeaderIndex + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                        break;
                     }

                     if (headerNameIndex == -1) {
                        headerNameIndex = j - this.nextHeaderIndex + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                     }
                  }
               }
            }

            if (headerIndex != -1) {
               this.writeInt(headerIndex, 127, 128);
            } else if (headerNameIndex == -1) {
               this.out.writeByte(64);
               this.writeByteString(name);
               this.writeByteString(value);
               this.insertIntoDynamicTable(header);
            } else if (name.startsWith(Header.PSEUDO_PREFIX) && !(Header.TARGET_AUTHORITY == name)) {
               this.writeInt(headerNameIndex, 15, 0);
               this.writeByteString(value);
            } else {
               this.writeInt(headerNameIndex, 63, 64);
               this.writeByteString(value);
               this.insertIntoDynamicTable(header);
            }
         }
      }

      public fun writeInt(value: Int, prefixMask: Int, bits: Int) {
         if (value < prefixMask) {
            this.out.writeByte(bits or value);
         } else {
            this.out.writeByte(bits or prefixMask);

            var valuex: Int;
            for (valuex = value - prefixMask; valuex >= 128; valuex >>>= 7) {
               this.out.writeByte(valuex and 127 or 128);
            }

            this.out.writeByte(valuex);
         }
      }

      @Throws(java/io/IOException::class)
      public fun writeByteString(data: ByteString) {
         if (this.useCompression && Huffman.INSTANCE.encodedLength(data) < data.size()) {
            var huffmanBuffer: Buffer = new Buffer();
            Huffman.INSTANCE.encode(data, huffmanBuffer as BufferedSink);
            var huffmanBytes: ByteString = huffmanBuffer.readByteString();
            this.writeInt(huffmanBytes.size(), 127, 128);
            this.out.write(huffmanBytes);
         } else {
            this.writeInt(data.size(), 127, 0);
            this.out.write(data);
         }
      }

      public fun resizeHeaderTable(headerTableSizeSetting: Int) {
         this.headerTableSizeSetting = headerTableSizeSetting;
         var effectiveHeaderTableSize: Int = Math.min(headerTableSizeSetting, 16384);
         if (this.maxDynamicTableByteCount != effectiveHeaderTableSize) {
            if (effectiveHeaderTableSize < this.maxDynamicTableByteCount) {
               this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, effectiveHeaderTableSize);
            }

            this.emitDynamicTableSizeUpdate = true;
            this.maxDynamicTableByteCount = effectiveHeaderTableSize;
            this.adjustDynamicTableByteCount();
         }
      }

      private fun adjustDynamicTableByteCount() {
         if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
            if (this.maxDynamicTableByteCount == 0) {
               this.clearDynamicTable();
            } else {
               this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
            }
         }
      }

      @JvmOverloads
      fun Writer(headerTableSizeSetting: Int, out: Buffer) {
         this(headerTableSizeSetting, false, out, 2, null);
      }

      @JvmOverloads
      fun Writer(out: Buffer) {
         this(0, false, out, 3, null);
      }
   }
}
