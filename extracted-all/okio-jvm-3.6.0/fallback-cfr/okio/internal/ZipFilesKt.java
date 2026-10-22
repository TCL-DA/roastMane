/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$BooleanRef
 *  kotlin.jvm.internal.Ref$LongRef
 *  kotlin.jvm.internal.Ref$ObjectRef
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.CharsKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package okio.internal;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okio.BufferedSource;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.ZipFileSystem;
import okio.internal.EocdRecord;
import okio.internal.ZipEntry;
import okio.internal.ZipFilesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000j\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\"\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0002\u001a\u001f\u0010\u0018\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0001H\u0002\u00a2\u0006\u0002\u0010\u001b\u001a.\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020 2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020#0\"H\u0000\u001a\f\u0010$\u001a\u00020\u0015*\u00020%H\u0000\u001a\f\u0010&\u001a\u00020'*\u00020%H\u0002\u001a.\u0010(\u001a\u00020)*\u00020%2\u0006\u0010*\u001a\u00020\u00012\u0018\u0010+\u001a\u0014\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020)0,H\u0002\u001a\u0014\u0010-\u001a\u00020.*\u00020%2\u0006\u0010/\u001a\u00020.H\u0000\u001a\u0018\u00100\u001a\u0004\u0018\u00010.*\u00020%2\b\u0010/\u001a\u0004\u0018\u00010.H\u0002\u001a\u0014\u00101\u001a\u00020'*\u00020%2\u0006\u00102\u001a\u00020'H\u0002\u001a\f\u00103\u001a\u00020)*\u00020%H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0080T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0018\u0010\u000e\u001a\u00020\u000f*\u00020\u00018BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011\u00a8\u00064"}, d2={"BIT_FLAG_ENCRYPTED", "", "BIT_FLAG_UNSUPPORTED_MASK", "CENTRAL_FILE_HEADER_SIGNATURE", "COMPRESSION_METHOD_DEFLATED", "COMPRESSION_METHOD_STORED", "END_OF_CENTRAL_DIRECTORY_SIGNATURE", "HEADER_ID_EXTENDED_TIMESTAMP", "HEADER_ID_ZIP64_EXTENDED_INFO", "LOCAL_FILE_HEADER_SIGNATURE", "MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE", "", "ZIP64_EOCD_RECORD_SIGNATURE", "ZIP64_LOCATOR_SIGNATURE", "hex", "", "getHex", "(I)Ljava/lang/String;", "buildIndex", "", "Lokio/Path;", "Lokio/internal/ZipEntry;", "entries", "", "dosDateTimeToEpochMillis", "date", "time", "(II)Ljava/lang/Long;", "openZip", "Lokio/ZipFileSystem;", "zipPath", "fileSystem", "Lokio/FileSystem;", "predicate", "Lkotlin/Function1;", "", "readEntry", "Lokio/BufferedSource;", "readEocdRecord", "Lokio/internal/EocdRecord;", "readExtra", "", "extraSize", "block", "Lkotlin/Function2;", "readLocalHeader", "Lokio/FileMetadata;", "basicMetadata", "readOrSkipLocalHeader", "readZip64EocdRecord", "regularRecord", "skipLocalHeader", "okio"})
@SourceDebugExtension(value={"SMAP\nZipFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,459:1\n1045#2:460\n*S KotlinDebug\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n*L\n156#1:460\n*E\n"})
public final class ZipFilesKt {
    private static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
    private static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
    private static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 101010256;
    private static final int ZIP64_LOCATOR_SIGNATURE = 117853008;
    private static final int ZIP64_EOCD_RECORD_SIGNATURE = 101075792;
    public static final int COMPRESSION_METHOD_DEFLATED = 8;
    public static final int COMPRESSION_METHOD_STORED = 0;
    private static final int BIT_FLAG_ENCRYPTED = 1;
    private static final int BIT_FLAG_UNSUPPORTED_MASK = 1;
    private static final long MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE = 0xFFFFFFFFL;
    private static final int HEADER_ID_ZIP64_EXTENDED_INFO = 1;
    private static final int HEADER_ID_EXTENDED_TIMESTAMP = 21589;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public static final ZipFileSystem openZip(@NotNull Path zipPath, @NotNull FileSystem fileSystem, @NotNull Function1<? super ZipEntry, Boolean> predicate) throws IOException {
        Intrinsics.checkNotNullParameter((Object)zipPath, (String)"zipPath");
        Intrinsics.checkNotNullParameter((Object)fileSystem, (String)"fileSystem");
        Intrinsics.checkNotNullParameter(predicate, (String)"predicate");
        Closeable closeable = fileSystem.openReadOnly(zipPath);
        Throwable throwable = null;
        try {
            Throwable throwable2;
            Object object;
            String comment;
            EocdRecord record;
            long eocdOffset;
            FileHandle fileHandle;
            block31: {
                fileHandle = (FileHandle)closeable;
                boolean bl = false;
                long scanOffset = fileHandle.size() - (long)22;
                if (scanOffset < 0L) {
                    throw new IOException("not a zip: size=" + fileHandle.size());
                }
                long stopOffset = Math.max(scanOffset - 65536L, 0L);
                eocdOffset = 0L;
                record = null;
                comment = null;
                do {
                    try (BufferedSource source2 = Okio.buffer(fileHandle.source(scanOffset));){
                        if (source2.readIntLe() != 101010256) continue;
                        eocdOffset = scanOffset;
                        record = ZipFilesKt.readEocdRecord(source2);
                        comment = source2.readUtf8(record.getCommentByteCount());
                        break block31;
                    }
                } while ((scanOffset += -1L) >= stopOffset);
                throw new IOException("not a zip: end of central directory signature not found");
            }
            long zip64LocatorOffset = eocdOffset - (long)20;
            if (zip64LocatorOffset > 0L) {
                Closeable closeable2 = Okio.buffer(fileHandle.source(zip64LocatorOffset));
                object = null;
                try {
                    BufferedSource zip64LocatorSource = (BufferedSource)closeable2;
                    boolean bl = false;
                    if (zip64LocatorSource.readIntLe() == 117853008) {
                        int diskWithCentralDir = zip64LocatorSource.readIntLe();
                        long zip64EocdRecordOffset = zip64LocatorSource.readLongLe();
                        int numDisks = zip64LocatorSource.readIntLe();
                        if (numDisks != 1 || diskWithCentralDir != 0) {
                            throw new IOException("unsupported zip: spanned");
                        }
                        Closeable closeable3 = Okio.buffer(fileHandle.source(zip64EocdRecordOffset));
                        Throwable throwable3 = null;
                        try {
                            BufferedSource zip64EocdSource = (BufferedSource)closeable3;
                            boolean bl2 = false;
                            int zip64EocdSignature = zip64EocdSource.readIntLe();
                            if (zip64EocdSignature != 101075792) {
                                throw new IOException("bad zip: expected " + ZipFilesKt.getHex(101075792) + " but was " + ZipFilesKt.getHex(zip64EocdSignature));
                            }
                            record = ZipFilesKt.readZip64EocdRecord(zip64EocdSource, record);
                            Unit unit = Unit.INSTANCE;
                        }
                        catch (Throwable throwable4) {
                            throwable3 = throwable4;
                            throw throwable4;
                        }
                        finally {
                            CloseableKt.closeFinally((Closeable)closeable3, (Throwable)throwable3);
                        }
                    }
                    throwable2 = Unit.INSTANCE;
                }
                catch (Throwable throwable5) {
                    object = throwable5;
                    throw throwable5;
                }
                finally {
                    CloseableKt.closeFinally((Closeable)closeable2, (Throwable)object);
                }
            }
            List entries = new ArrayList();
            object = Okio.buffer(fileHandle.source(record.getCentralDirectoryOffset()));
            throwable2 = null;
            try {
                BufferedSource source3 = (BufferedSource)object;
                boolean bl = false;
                long l = record.getEntryCount();
                for (long i = 0L; i < l; ++i) {
                    ZipEntry entry = ZipFilesKt.readEntry(source3);
                    if (entry.getOffset() >= record.getCentralDirectoryOffset()) {
                        throw new IOException("bad zip: local file header offset >= central directory offset");
                    }
                    if (!((Boolean)predicate.invoke((Object)entry)).booleanValue()) continue;
                    ((Collection)entries).add(entry);
                }
                Unit unit = Unit.INSTANCE;
            }
            catch (Throwable throwable6) {
                throwable2 = throwable6;
                throw throwable6;
            }
            finally {
                CloseableKt.closeFinally((Closeable)object, (Throwable)throwable2);
            }
            Map<Path, ZipEntry> index = ZipFilesKt.buildIndex(entries);
            ZipFileSystem zipFileSystem = new ZipFileSystem(zipPath, fileSystem, index, comment);
            return zipFileSystem;
        }
        catch (Throwable throwable7) {
            throwable = throwable7;
            throw throwable7;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
    }

    public static /* synthetic */ ZipFileSystem openZip$default(Path path, FileSystem fileSystem, Function1 function1, int n, Object object) throws IOException {
        if ((n & 4) != 0) {
            function1 = openZip.1.INSTANCE;
        }
        return ZipFilesKt.openZip(path, fileSystem, (Function1<? super ZipEntry, Boolean>)function1);
    }

    private static final Map<Path, ZipEntry> buildIndex(List<ZipEntry> entries) {
        Path root = Path.Companion.get$default(Path.Companion, "/", false, 1, null);
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)root, (Object)new ZipEntry(root, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null))};
        Map result = MapsKt.mutableMapOf((Pair[])pairArray);
        Iterable $this$sortedBy$iv = entries;
        boolean $i$f$sortedBy = false;
        block0: for (ZipEntry entry : CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(){

            public final int compare(T a, T b) {
                ZipEntry it = (ZipEntry)a;
                boolean bl = false;
                Comparable comparable = it.getCanonicalPath();
                it = (ZipEntry)b;
                Comparable comparable2 = comparable;
                bl = false;
                return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)it.getCanonicalPath());
            }
        })) {
            ZipEntry replaced = result.put(entry.getCanonicalPath(), entry);
            if (replaced != null) continue;
            ZipEntry child = entry;
            while (child.getCanonicalPath().parent() != null) {
                Path parentPath;
                ZipEntry parentEntry = (ZipEntry)result.get(parentPath);
                if (parentEntry != null) {
                    ((Collection)parentEntry.getChildren()).add(child.getCanonicalPath());
                    continue block0;
                }
                parentEntry = new ZipEntry(parentPath, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null);
                result.put(parentPath, parentEntry);
                ((Collection)parentEntry.getChildren()).add(child.getCanonicalPath());
                child = parentEntry;
            }
        }
        return result;
    }

    @NotNull
    public static final ZipEntry readEntry(@NotNull BufferedSource $this$readEntry) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$readEntry, (String)"<this>");
        int signature = $this$readEntry.readIntLe();
        if (signature != 33639248) {
            throw new IOException("bad zip: expected " + ZipFilesKt.getHex(33639248) + " but was " + ZipFilesKt.getHex(signature));
        }
        $this$readEntry.skip(4L);
        int bitFlag = $this$readEntry.readShortLe() & 0xFFFF;
        if ((bitFlag & 1) != 0) {
            throw new IOException("unsupported zip: general purpose bit flag=" + ZipFilesKt.getHex(bitFlag));
        }
        int compressionMethod = $this$readEntry.readShortLe() & 0xFFFF;
        int time = $this$readEntry.readShortLe() & 0xFFFF;
        int date = $this$readEntry.readShortLe() & 0xFFFF;
        Long lastModifiedAtMillis = ZipFilesKt.dosDateTimeToEpochMillis(date, time);
        long crc = (long)$this$readEntry.readIntLe() & 0xFFFFFFFFL;
        Ref.LongRef compressedSize = new Ref.LongRef();
        compressedSize.element = (long)$this$readEntry.readIntLe() & 0xFFFFFFFFL;
        Ref.LongRef size = new Ref.LongRef();
        size.element = (long)$this$readEntry.readIntLe() & 0xFFFFFFFFL;
        int nameSize = $this$readEntry.readShortLe() & 0xFFFF;
        int extraSize = $this$readEntry.readShortLe() & 0xFFFF;
        int commentByteCount = $this$readEntry.readShortLe() & 0xFFFF;
        $this$readEntry.skip(8L);
        Ref.LongRef offset = new Ref.LongRef();
        offset.element = (long)$this$readEntry.readIntLe() & 0xFFFFFFFFL;
        String name = $this$readEntry.readUtf8(nameSize);
        if (StringsKt.contains$default((CharSequence)name, (char)'\u0000', (boolean)false, (int)2, null)) {
            throw new IOException("bad zip: filename contains 0x00");
        }
        BufferedSource $this$readEntry_u24lambda_u245 = $this$readEntry;
        boolean bl = false;
        long result = 0L;
        if (size.element == 0xFFFFFFFFL) {
            result += (long)8;
        }
        if (compressedSize.element == 0xFFFFFFFFL) {
            result += (long)8;
        }
        if (offset.element == 0xFFFFFFFFL) {
            result += (long)8;
        }
        long requiredZip64ExtraSize = result;
        Ref.BooleanRef hasZip64Extra = new Ref.BooleanRef();
        ZipFilesKt.readExtra($this$readEntry, extraSize, (Function2<? super Integer, ? super Long, Unit>)((Function2)new Function2<Integer, Long, Unit>(hasZip64Extra, requiredZip64ExtraSize, size, $this$readEntry, compressedSize, offset){
            final /* synthetic */ Ref.BooleanRef $hasZip64Extra;
            final /* synthetic */ long $requiredZip64ExtraSize;
            final /* synthetic */ Ref.LongRef $size;
            final /* synthetic */ BufferedSource $this_readEntry;
            final /* synthetic */ Ref.LongRef $compressedSize;
            final /* synthetic */ Ref.LongRef $offset;
            {
                this.$hasZip64Extra = $hasZip64Extra;
                this.$requiredZip64ExtraSize = $requiredZip64ExtraSize;
                this.$size = $size;
                this.$this_readEntry = $receiver;
                this.$compressedSize = $compressedSize;
                this.$offset = $offset;
                super(2);
            }

            public final void invoke(int headerId, long dataSize) {
                if (headerId == 1) {
                    if (this.$hasZip64Extra.element) {
                        throw new IOException("bad zip: zip64 extra repeated");
                    }
                    this.$hasZip64Extra.element = true;
                    if (dataSize < this.$requiredZip64ExtraSize) {
                        throw new IOException("bad zip: zip64 extra too short");
                    }
                    this.$size.element = this.$size.element == 0xFFFFFFFFL ? this.$this_readEntry.readLongLe() : this.$size.element;
                    this.$compressedSize.element = this.$compressedSize.element == 0xFFFFFFFFL ? this.$this_readEntry.readLongLe() : 0L;
                    this.$offset.element = this.$offset.element == 0xFFFFFFFFL ? this.$this_readEntry.readLongLe() : 0L;
                }
            }
        }));
        if (requiredZip64ExtraSize > 0L && !hasZip64Extra.element) {
            throw new IOException("bad zip: zip64 extra required but absent");
        }
        String comment = $this$readEntry.readUtf8(commentByteCount);
        Path canonicalPath = Path.Companion.get$default(Path.Companion, "/", false, 1, null).resolve(name);
        boolean isDirectory = StringsKt.endsWith$default((String)name, (String)"/", (boolean)false, (int)2, null);
        return new ZipEntry(canonicalPath, isDirectory, comment, crc, compressedSize.element, size.element, compressionMethod, lastModifiedAtMillis, offset.element);
    }

    private static final EocdRecord readEocdRecord(BufferedSource $this$readEocdRecord) throws IOException {
        long totalEntryCount;
        int diskNumber = $this$readEocdRecord.readShortLe() & 0xFFFF;
        int diskWithCentralDir = $this$readEocdRecord.readShortLe() & 0xFFFF;
        long entryCount = $this$readEocdRecord.readShortLe() & 0xFFFF;
        if (entryCount != (totalEntryCount = (long)($this$readEocdRecord.readShortLe() & 0xFFFF)) || diskNumber != 0 || diskWithCentralDir != 0) {
            throw new IOException("unsupported zip: spanned");
        }
        $this$readEocdRecord.skip(4L);
        long centralDirectoryOffset = (long)$this$readEocdRecord.readIntLe() & 0xFFFFFFFFL;
        int commentByteCount = $this$readEocdRecord.readShortLe() & 0xFFFF;
        return new EocdRecord(entryCount, centralDirectoryOffset, commentByteCount);
    }

    private static final EocdRecord readZip64EocdRecord(BufferedSource $this$readZip64EocdRecord, EocdRecord regularRecord) throws IOException {
        $this$readZip64EocdRecord.skip(12L);
        int diskNumber = $this$readZip64EocdRecord.readIntLe();
        int diskWithCentralDirStart = $this$readZip64EocdRecord.readIntLe();
        long entryCount = $this$readZip64EocdRecord.readLongLe();
        long totalEntryCount = $this$readZip64EocdRecord.readLongLe();
        if (entryCount != totalEntryCount || diskNumber != 0 || diskWithCentralDirStart != 0) {
            throw new IOException("unsupported zip: spanned");
        }
        $this$readZip64EocdRecord.skip(8L);
        long centralDirectoryOffset = $this$readZip64EocdRecord.readLongLe();
        return new EocdRecord(entryCount, centralDirectoryOffset, regularRecord.getCommentByteCount());
    }

    private static final void readExtra(BufferedSource $this$readExtra, int extraSize, Function2<? super Integer, ? super Long, Unit> block) {
        long dataSize;
        for (long remaining = (long)extraSize; remaining != 0L; remaining -= dataSize) {
            if (remaining < 4L) {
                throw new IOException("bad zip: truncated header in extra field");
            }
            int headerId = $this$readExtra.readShortLe() & 0xFFFF;
            dataSize = (long)$this$readExtra.readShortLe() & 0xFFFFL;
            if ((remaining -= (long)4) < dataSize) {
                throw new IOException("bad zip: truncated value in extra field");
            }
            $this$readExtra.require(dataSize);
            long sizeBefore = $this$readExtra.getBuffer().size();
            block.invoke((Object)headerId, (Object)dataSize);
            long fieldRemaining = dataSize + $this$readExtra.getBuffer().size() - sizeBefore;
            if (fieldRemaining < 0L) {
                throw new IOException("unsupported zip: too many bytes processed for " + headerId);
            }
            if (fieldRemaining <= 0L) continue;
            $this$readExtra.getBuffer().skip(fieldRemaining);
        }
    }

    public static final void skipLocalHeader(@NotNull BufferedSource $this$skipLocalHeader) {
        Intrinsics.checkNotNullParameter((Object)$this$skipLocalHeader, (String)"<this>");
        ZipFilesKt.readOrSkipLocalHeader($this$skipLocalHeader, null);
    }

    @NotNull
    public static final FileMetadata readLocalHeader(@NotNull BufferedSource $this$readLocalHeader, @NotNull FileMetadata basicMetadata) {
        Intrinsics.checkNotNullParameter((Object)$this$readLocalHeader, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)basicMetadata, (String)"basicMetadata");
        FileMetadata fileMetadata = ZipFilesKt.readOrSkipLocalHeader($this$readLocalHeader, basicMetadata);
        Intrinsics.checkNotNull((Object)fileMetadata);
        return fileMetadata;
    }

    private static final FileMetadata readOrSkipLocalHeader(BufferedSource $this$readOrSkipLocalHeader, FileMetadata basicMetadata) {
        Ref.ObjectRef lastModifiedAtMillis = new Ref.ObjectRef();
        FileMetadata fileMetadata = basicMetadata;
        lastModifiedAtMillis.element = fileMetadata != null ? fileMetadata.getLastModifiedAtMillis() : null;
        Ref.ObjectRef lastAccessedAtMillis = new Ref.ObjectRef();
        Ref.ObjectRef createdAtMillis = new Ref.ObjectRef();
        int signature = $this$readOrSkipLocalHeader.readIntLe();
        if (signature != 67324752) {
            throw new IOException("bad zip: expected " + ZipFilesKt.getHex(67324752) + " but was " + ZipFilesKt.getHex(signature));
        }
        $this$readOrSkipLocalHeader.skip(2L);
        int bitFlag = $this$readOrSkipLocalHeader.readShortLe() & 0xFFFF;
        if ((bitFlag & 1) != 0) {
            throw new IOException("unsupported zip: general purpose bit flag=" + ZipFilesKt.getHex(bitFlag));
        }
        $this$readOrSkipLocalHeader.skip(18L);
        long fileNameLength = (long)$this$readOrSkipLocalHeader.readShortLe() & 0xFFFFL;
        int extraSize = $this$readOrSkipLocalHeader.readShortLe() & 0xFFFF;
        $this$readOrSkipLocalHeader.skip(fileNameLength);
        if (basicMetadata == null) {
            $this$readOrSkipLocalHeader.skip(extraSize);
            return null;
        }
        ZipFilesKt.readExtra($this$readOrSkipLocalHeader, extraSize, (Function2<? super Integer, ? super Long, Unit>)((Function2)new Function2<Integer, Long, Unit>($this$readOrSkipLocalHeader, (Ref.ObjectRef<Long>)lastModifiedAtMillis, (Ref.ObjectRef<Long>)lastAccessedAtMillis, (Ref.ObjectRef<Long>)createdAtMillis){
            final /* synthetic */ BufferedSource $this_readOrSkipLocalHeader;
            final /* synthetic */ Ref.ObjectRef<Long> $lastModifiedAtMillis;
            final /* synthetic */ Ref.ObjectRef<Long> $lastAccessedAtMillis;
            final /* synthetic */ Ref.ObjectRef<Long> $createdAtMillis;
            {
                this.$this_readOrSkipLocalHeader = $receiver;
                this.$lastModifiedAtMillis = $lastModifiedAtMillis;
                this.$lastAccessedAtMillis = $lastAccessedAtMillis;
                this.$createdAtMillis = $createdAtMillis;
                super(2);
            }

            public final void invoke(int headerId, long dataSize) {
                if (headerId == 21589) {
                    long requiredSize;
                    if (dataSize < 1L) {
                        throw new IOException("bad zip: extended timestamp extra too short");
                    }
                    int flags = this.$this_readOrSkipLocalHeader.readByte() & 0xFF;
                    boolean hasLastModifiedAtMillis = (flags & 1) == 1;
                    boolean hasLastAccessedAtMillis = (flags & 2) == 2;
                    boolean hasCreatedAtMillis = (flags & 4) == 4;
                    BufferedSource $this$invoke_u24lambda_u240 = this.$this_readOrSkipLocalHeader;
                    boolean bl = false;
                    long result = 1L;
                    if (hasLastModifiedAtMillis) {
                        result += 4L;
                    }
                    if (hasLastAccessedAtMillis) {
                        result += 4L;
                    }
                    if (hasCreatedAtMillis) {
                        result += 4L;
                    }
                    if (dataSize < (requiredSize = result)) {
                        throw new IOException("bad zip: extended timestamp extra too short");
                    }
                    if (hasLastModifiedAtMillis) {
                        this.$lastModifiedAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                    }
                    if (hasLastAccessedAtMillis) {
                        this.$lastAccessedAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                    }
                    if (hasCreatedAtMillis) {
                        this.$createdAtMillis.element = (long)this.$this_readOrSkipLocalHeader.readIntLe() * 1000L;
                    }
                }
            }
        }));
        return new FileMetadata(basicMetadata.isRegularFile(), basicMetadata.isDirectory(), null, basicMetadata.getSize(), (Long)createdAtMillis.element, (Long)lastModifiedAtMillis.element, (Long)lastAccessedAtMillis.element, null, 128, null);
    }

    private static final Long dosDateTimeToEpochMillis(int date, int time) {
        if (time == -1) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(14, 0);
        int year = 1980 + (date >> 9 & 0x7F);
        int month = date >> 5 & 0xF;
        int day = date & 0x1F;
        int hour = time >> 11 & 0x1F;
        int minute = time >> 5 & 0x3F;
        int second = (time & 0x1F) << 1;
        cal.set(year, month - 1, day, hour, minute, second);
        return cal.getTime().getTime();
    }

    private static final String getHex(int $this$hex) {
        StringBuilder stringBuilder = new StringBuilder().append("0x");
        String string = Integer.toString($this$hex, CharsKt.checkRadix((int)16));
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(this, checkRadix(radix))");
        return stringBuilder.append(string).toString();
    }
}

