/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.ExceptionsKt
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.IntIterator
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmName
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.ArrayIteratorKt
 *  kotlin.jvm.internal.InlineMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.jvm.internal.TypeIntrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  okio.Buffer
 *  okio.BufferedSink
 *  okio.BufferedSource
 *  okio.ByteString
 *  okio.Options
 *  okio.Source
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package okhttp3.internal;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;
import okhttp3.internal.io.FileSystem;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Options;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 8, 0}, k=2, xi=48, d1={"\u0000\u00b8\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\f\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\b\n\u0002\u0010\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a \u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019\u001a\u001e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u001e\u001a\u00020\u0017\u001a'\u0010\u001f\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u00112\u0012\u0010 \u001a\n\u0012\u0006\b\u0001\u0012\u00020\"0!\"\u00020\"\u00a2\u0006\u0002\u0010#\u001a\u001a\u0010$\u001a\u00020\u001b2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u001b0&H\u0086\b\u00f8\u0001\u0000\u001a-\u0010'\u001a\b\u0012\u0004\u0012\u0002H)0(\"\u0004\b\u0000\u0010)2\u0012\u0010*\u001a\n\u0012\u0006\b\u0001\u0012\u0002H)0!\"\u0002H)H\u0007\u00a2\u0006\u0002\u0010+\u001a\u000e\u0010,\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0011\u001a1\u0010-\u001a\u0004\u0018\u0001H)\"\u0004\b\u0000\u0010)2\u0006\u0010.\u001a\u00020\"2\f\u0010/\u001a\b\u0012\u0004\u0012\u0002H)002\u0006\u00101\u001a\u00020\u0011\u00a2\u0006\u0002\u00102\u001a\u0016\u00103\u001a\u0002042\u0006\u0010\u0015\u001a\u00020\u00112\u0006\u00105\u001a\u00020\u000f\u001a\"\u00106\u001a\u00020\u001b2\u0006\u0010\u0015\u001a\u00020\u00112\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u001b0&H\u0086\b\u00f8\u0001\u0000\u001a%\u00107\u001a\u00020\u001b\"\u0004\b\u0000\u00108*\b\u0012\u0004\u0012\u0002H8092\u0006\u0010:\u001a\u0002H8H\u0000\u00a2\u0006\u0002\u0010;\u001a\u0015\u0010<\u001a\u00020\u0014*\u00020=2\u0006\u0010>\u001a\u00020\u0014H\u0086\u0004\u001a\u0015\u0010<\u001a\u00020\u0017*\u00020\u00142\u0006\u0010>\u001a\u00020\u0017H\u0086\u0004\u001a\u0015\u0010<\u001a\u00020\u0014*\u00020?2\u0006\u0010>\u001a\u00020\u0014H\u0086\u0004\u001a\n\u0010@\u001a\u00020A*\u00020B\u001a\r\u0010C\u001a\u00020\u001b*\u00020\"H\u0080\b\u001a\r\u0010D\u001a\u00020\u001b*\u00020\"H\u0080\b\u001a\n\u0010E\u001a\u00020\u000f*\u00020\u0011\u001a\u0012\u0010F\u001a\u00020\u000f*\u00020G2\u0006\u0010H\u001a\u00020G\u001a\n\u0010I\u001a\u00020\u001b*\u00020J\u001a\n\u0010I\u001a\u00020\u001b*\u00020K\u001a\n\u0010I\u001a\u00020\u001b*\u00020L\u001a#\u0010M\u001a\b\u0012\u0004\u0012\u00020\u00110!*\b\u0012\u0004\u0012\u00020\u00110!2\u0006\u0010N\u001a\u00020\u0011\u00a2\u0006\u0002\u0010O\u001a&\u0010P\u001a\u00020\u0014*\u00020\u00112\u0006\u0010Q\u001a\u00020R2\b\b\u0002\u0010S\u001a\u00020\u00142\b\b\u0002\u0010T\u001a\u00020\u0014\u001a&\u0010P\u001a\u00020\u0014*\u00020\u00112\u0006\u0010U\u001a\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u00142\b\b\u0002\u0010T\u001a\u00020\u0014\u001a\u001a\u0010V\u001a\u00020\u000f*\u00020W2\u0006\u0010X\u001a\u00020\u00142\u0006\u0010Y\u001a\u00020\u0019\u001a;\u0010Z\u001a\b\u0012\u0004\u0012\u0002H)0(\"\u0004\b\u0000\u0010)*\b\u0012\u0004\u0012\u0002H)0[2\u0017\u0010\\\u001a\u0013\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u00020\u000f0]\u00a2\u0006\u0002\b^H\u0086\b\u00f8\u0001\u0000\u001a5\u0010_\u001a\u00020\u000f*\b\u0012\u0004\u0012\u00020\u00110!2\u000e\u0010H\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010!2\u000e\u0010`\u001a\n\u0012\u0006\b\u0000\u0012\u00020\u00110a\u00a2\u0006\u0002\u0010b\u001a\n\u0010c\u001a\u00020\u0017*\u00020d\u001a+\u0010e\u001a\u00020\u0014*\b\u0012\u0004\u0012\u00020\u00110!2\u0006\u0010N\u001a\u00020\u00112\f\u0010`\u001a\b\u0012\u0004\u0012\u00020\u00110a\u00a2\u0006\u0002\u0010f\u001a\n\u0010g\u001a\u00020\u0014*\u00020\u0011\u001a\u001e\u0010h\u001a\u00020\u0014*\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u00142\b\b\u0002\u0010T\u001a\u00020\u0014\u001a\u001e\u0010i\u001a\u00020\u0014*\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u00142\b\b\u0002\u0010T\u001a\u00020\u0014\u001a\u0014\u0010j\u001a\u00020\u0014*\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u0014\u001a9\u0010k\u001a\b\u0012\u0004\u0012\u00020\u00110!*\b\u0012\u0004\u0012\u00020\u00110!2\f\u0010H\u001a\b\u0012\u0004\u0012\u00020\u00110!2\u000e\u0010`\u001a\n\u0012\u0006\b\u0000\u0012\u00020\u00110a\u00a2\u0006\u0002\u0010l\u001a\u0012\u0010m\u001a\u00020\u000f*\u00020n2\u0006\u0010o\u001a\u00020p\u001a\u0012\u0010q\u001a\u00020\u000f*\u00020L2\u0006\u0010r\u001a\u00020s\u001a\r\u0010t\u001a\u00020\u001b*\u00020\"H\u0086\b\u001a\r\u0010u\u001a\u00020\u001b*\u00020\"H\u0086\b\u001a\n\u0010v\u001a\u00020\u0014*\u00020R\u001a\n\u0010w\u001a\u00020\u0011*\u00020L\u001a\u0012\u0010x\u001a\u00020y*\u00020s2\u0006\u0010z\u001a\u00020y\u001a\n\u0010{\u001a\u00020\u0014*\u00020s\u001a\u0012\u0010|\u001a\u00020\u0014*\u00020}2\u0006\u0010~\u001a\u00020=\u001a\u001a\u0010|\u001a\u00020\u000f*\u00020W2\u0006\u0010\u0016\u001a\u00020\u00142\u0006\u0010Y\u001a\u00020\u0019\u001a\u0011\u0010\u007f\u001a\t\u0012\u0005\u0012\u00030\u0080\u00010(*\u00020\u0003\u001a\u0012\u0010\u0081\u0001\u001a\u00020\u0003*\t\u0012\u0005\u0012\u00030\u0080\u00010(\u001a\u000b\u0010\u0082\u0001\u001a\u00020\u0011*\u00020\u0014\u001a\u000b\u0010\u0082\u0001\u001a\u00020\u0011*\u00020\u0017\u001a\u0016\u0010\u0083\u0001\u001a\u00020\u0011*\u00020G2\t\b\u0002\u0010\u0084\u0001\u001a\u00020\u000f\u001a\u001d\u0010\u0085\u0001\u001a\b\u0012\u0004\u0012\u0002H)0(\"\u0004\b\u0000\u0010)*\b\u0012\u0004\u0012\u0002H)0(\u001a7\u0010\u0086\u0001\u001a\u0011\u0012\u0005\u0012\u0003H\u0088\u0001\u0012\u0005\u0012\u0003H\u0089\u00010\u0087\u0001\"\u0005\b\u0000\u0010\u0088\u0001\"\u0005\b\u0001\u0010\u0089\u0001*\u0011\u0012\u0005\u0012\u0003H\u0088\u0001\u0012\u0005\u0012\u0003H\u0089\u00010\u0087\u0001\u001a\u0014\u0010\u008a\u0001\u001a\u00020\u0017*\u00020\u00112\u0007\u0010\u008b\u0001\u001a\u00020\u0017\u001a\u0016\u0010\u008c\u0001\u001a\u00020\u0014*\u0004\u0018\u00010\u00112\u0007\u0010\u008b\u0001\u001a\u00020\u0014\u001a\u001f\u0010\u008d\u0001\u001a\u00020\u0011*\u00020\u00112\b\b\u0002\u0010S\u001a\u00020\u00142\b\b\u0002\u0010T\u001a\u00020\u0014\u001a\u000e\u0010\u008e\u0001\u001a\u00020\u001b*\u00020\"H\u0086\b\u001a'\u0010\u008f\u0001\u001a\u00030\u0090\u0001*\b0\u0091\u0001j\u0003`\u0092\u00012\u0013\u0010\u0093\u0001\u001a\u000e\u0012\n\u0012\b0\u0091\u0001j\u0003`\u0092\u00010(\u001a\u0015\u0010\u0094\u0001\u001a\u00020\u001b*\u00030\u0095\u00012\u0007\u0010\u0096\u0001\u001a\u00020\u0014\"\u0010\u0010\u0000\u001a\u00020\u00018\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u000e\u001a\u00020\u000f8\u0000X\u0081\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0010\u001a\u00020\u00118\u0000X\u0081\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0012\u001a\u00020\u0011X\u0086T\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\u0097\u0001"}, d2={"EMPTY_BYTE_ARRAY", "", "EMPTY_HEADERS", "Lokhttp3/Headers;", "EMPTY_REQUEST", "Lokhttp3/RequestBody;", "EMPTY_RESPONSE", "Lokhttp3/ResponseBody;", "UNICODE_BOMS", "Lokio/Options;", "UTC", "Ljava/util/TimeZone;", "VERIFY_AS_IP_ADDRESS", "Lkotlin/text/Regex;", "assertionsEnabled", "", "okHttpName", "", "userAgent", "checkDuration", "", "name", "duration", "", "unit", "Ljava/util/concurrent/TimeUnit;", "checkOffsetAndCount", "", "arrayLength", "offset", "count", "format", "args", "", "", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", "ignoreIoExceptions", "block", "Lkotlin/Function0;", "immutableListOf", "", "T", "elements", "([Ljava/lang/Object;)Ljava/util/List;", "isSensitiveHeader", "readFieldOrNull", "instance", "fieldType", "Ljava/lang/Class;", "fieldName", "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", "threadFactory", "Ljava/util/concurrent/ThreadFactory;", "daemon", "threadName", "addIfAbsent", "E", "", "element", "(Ljava/util/List;Ljava/lang/Object;)V", "and", "", "mask", "", "asFactory", "Lokhttp3/EventListener$Factory;", "Lokhttp3/EventListener;", "assertThreadDoesntHoldLock", "assertThreadHoldsLock", "canParseAsIpAddress", "canReuseConnectionFor", "Lokhttp3/HttpUrl;", "other", "closeQuietly", "Ljava/io/Closeable;", "Ljava/net/ServerSocket;", "Ljava/net/Socket;", "concat", "value", "([Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", "delimiterOffset", "delimiter", "", "startIndex", "endIndex", "delimiters", "discard", "Lokio/Source;", "timeout", "timeUnit", "filterList", "", "predicate", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "hasIntersection", "comparator", "Ljava/util/Comparator;", "([Ljava/lang/String;[Ljava/lang/String;Ljava/util/Comparator;)Z", "headersContentLength", "Lokhttp3/Response;", "indexOf", "([Ljava/lang/String;Ljava/lang/String;Ljava/util/Comparator;)I", "indexOfControlOrNonAscii", "indexOfFirstNonAsciiWhitespace", "indexOfLastNonAsciiWhitespace", "indexOfNonWhitespace", "intersect", "([Ljava/lang/String;[Ljava/lang/String;Ljava/util/Comparator;)[Ljava/lang/String;", "isCivilized", "Lokhttp3/internal/io/FileSystem;", "file", "Ljava/io/File;", "isHealthy", "source", "Lokio/BufferedSource;", "notify", "notifyAll", "parseHexDigit", "peerName", "readBomAsCharset", "Ljava/nio/charset/Charset;", "default", "readMedium", "skipAll", "Lokio/Buffer;", "b", "toHeaderList", "Lokhttp3/internal/http2/Header;", "toHeaders", "toHexString", "toHostHeader", "includeDefaultPort", "toImmutableList", "toImmutableMap", "", "K", "V", "toLongOrDefault", "defaultValue", "toNonNegativeInt", "trimSubstring", "wait", "withSuppressed", "", "Ljava/lang/Exception;", "Lkotlin/Exception;", "suppressed", "writeMedium", "Lokio/BufferedSink;", "medium", "okhttp"})
@JvmName(name="Util")
@SourceDebugExtension(value={"SMAP\nUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Util.kt\nokhttp3/internal/Util\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,636:1\n37#2,2:637\n1627#3,6:639\n1#4:645\n1549#5:646\n1620#5,3:647\n*S KotlinDebug\n*F\n+ 1 Util.kt\nokhttp3/internal/Util\n*L\n127#1:637,2\n167#1:639,6\n300#1:646\n300#1:647,3\n*E\n"})
public final class Util {
    @JvmField
    @NotNull
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    @JvmField
    @NotNull
    public static final Headers EMPTY_HEADERS = Headers.Companion.of(new String[0]);
    @JvmField
    @NotNull
    public static final ResponseBody EMPTY_RESPONSE = ResponseBody.Companion.create$default(ResponseBody.Companion, EMPTY_BYTE_ARRAY, null, 1, null);
    @JvmField
    @NotNull
    public static final RequestBody EMPTY_REQUEST = RequestBody.Companion.create$default(RequestBody.Companion, EMPTY_BYTE_ARRAY, null, 0, 0, 7, null);
    @NotNull
    private static final Options UNICODE_BOMS;
    @JvmField
    @NotNull
    public static final TimeZone UTC;
    @NotNull
    private static final Regex VERIFY_AS_IP_ADDRESS;
    @JvmField
    public static final boolean assertionsEnabled;
    @JvmField
    @NotNull
    public static final String okHttpName;
    @NotNull
    public static final String userAgent = "okhttp/4.12.0";

    public static final void checkOffsetAndCount(long arrayLength, long offset, long count) {
        if ((offset | count) < 0L || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @NotNull
    public static final ThreadFactory threadFactory(@NotNull String name, boolean daemon) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return arg_0 -> Util.threadFactory$lambda$1(name, daemon, arg_0);
    }

    @NotNull
    public static final String[] intersect(@NotNull String[] $this$intersect, @NotNull String[] other, @NotNull Comparator<? super String> comparator) {
        Intrinsics.checkNotNullParameter((Object)$this$intersect, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        Intrinsics.checkNotNullParameter(comparator, (String)"comparator");
        List result = new ArrayList();
        block0: for (String a : $this$intersect) {
            for (String b : other) {
                if (comparator.compare(a, b) != 0) continue;
                result.add(a);
                continue block0;
            }
        }
        Collection $this$toTypedArray$iv = result;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = $this$toTypedArray$iv;
        return thisCollection$iv.toArray(new String[0]);
    }

    public static final boolean hasIntersection(@NotNull String[] $this$hasIntersection, @Nullable String[] other, @NotNull Comparator<? super String> comparator) {
        Intrinsics.checkNotNullParameter((Object)$this$hasIntersection, (String)"<this>");
        Intrinsics.checkNotNullParameter(comparator, (String)"comparator");
        if ($this$hasIntersection.length == 0 || other == null || other.length == 0) {
            return false;
        }
        for (String a : $this$hasIntersection) {
            Iterator iterator = ArrayIteratorKt.iterator((Object[])other);
            while (iterator.hasNext()) {
                String b = (String)iterator.next();
                if (comparator.compare(a, b) != 0) continue;
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static final String toHostHeader(@NotNull HttpUrl $this$toHostHeader, boolean includeDefaultPort) {
        Intrinsics.checkNotNullParameter((Object)$this$toHostHeader, (String)"<this>");
        String host = StringsKt.contains$default((CharSequence)$this$toHostHeader.host(), (CharSequence)":", (boolean)false, (int)2, null) ? '[' + $this$toHostHeader.host() + ']' : $this$toHostHeader.host();
        return includeDefaultPort || $this$toHostHeader.port() != HttpUrl.Companion.defaultPort($this$toHostHeader.scheme()) ? host + ':' + $this$toHostHeader.port() : host;
    }

    public static /* synthetic */ String toHostHeader$default(HttpUrl httpUrl, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = false;
        }
        return Util.toHostHeader(httpUrl, bl);
    }

    public static final int indexOf(@NotNull String[] $this$indexOf, @NotNull String value, @NotNull Comparator<String> comparator) {
        int n;
        block1: {
            Intrinsics.checkNotNullParameter((Object)$this$indexOf, (String)"<this>");
            Intrinsics.checkNotNullParameter((Object)value, (String)"value");
            Intrinsics.checkNotNullParameter(comparator, (String)"comparator");
            String[] $this$indexOfFirst$iv = $this$indexOf;
            boolean $i$f$indexOfFirst = false;
            int n2 = $this$indexOfFirst$iv.length;
            for (int index$iv = 0; index$iv < n2; ++index$iv) {
                String it = $this$indexOfFirst$iv[index$iv];
                boolean bl = false;
                if (!(comparator.compare(it, value) == 0)) continue;
                n = index$iv;
                break block1;
            }
            n = -1;
        }
        return n;
    }

    @NotNull
    public static final String[] concat(@NotNull String[] $this$concat, @NotNull String value) {
        Intrinsics.checkNotNullParameter((Object)$this$concat, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)value, (String)"value");
        Object[] objectArray = Arrays.copyOf($this$concat, $this$concat.length + 1);
        Intrinsics.checkNotNullExpressionValue((Object)objectArray, (String)"copyOf(this, newSize)");
        Object[] result = objectArray;
        result[ArraysKt.getLastIndex((Object[])result)] = value;
        return result;
    }

    public static final int indexOfFirstNonAsciiWhitespace(@NotNull String $this$indexOfFirstNonAsciiWhitespace, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$indexOfFirstNonAsciiWhitespace, (String)"<this>");
        for (int i = startIndex; i < endIndex; ++i) {
            char c = $this$indexOfFirstNonAsciiWhitespace.charAt(i);
            if ((((c == '\t' ? true : c == '\n') ? true : c == '\f') ? true : c == '\r') ? true : c == ' ') continue;
            return i;
        }
        return endIndex;
    }

    public static /* synthetic */ int indexOfFirstNonAsciiWhitespace$default(String string, int n, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = 0;
        }
        if ((n3 & 2) != 0) {
            n2 = string.length();
        }
        return Util.indexOfFirstNonAsciiWhitespace(string, n, n2);
    }

    public static final int indexOfLastNonAsciiWhitespace(@NotNull String $this$indexOfLastNonAsciiWhitespace, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$indexOfLastNonAsciiWhitespace, (String)"<this>");
        int i = endIndex - 1;
        if (startIndex <= i) {
            while (true) {
                char c;
                if (!(((((c = $this$indexOfLastNonAsciiWhitespace.charAt(i)) == '\t' ? true : c == '\n') ? true : c == '\f') ? true : c == '\r') ? true : c == ' ')) {
                    return i + 1;
                }
                if (i == startIndex) break;
                --i;
            }
        }
        return startIndex;
    }

    public static /* synthetic */ int indexOfLastNonAsciiWhitespace$default(String string, int n, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = 0;
        }
        if ((n3 & 2) != 0) {
            n2 = string.length();
        }
        return Util.indexOfLastNonAsciiWhitespace(string, n, n2);
    }

    @NotNull
    public static final String trimSubstring(@NotNull String $this$trimSubstring, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$trimSubstring, (String)"<this>");
        int start = Util.indexOfFirstNonAsciiWhitespace($this$trimSubstring, startIndex, endIndex);
        int end = Util.indexOfLastNonAsciiWhitespace($this$trimSubstring, start, endIndex);
        String string = $this$trimSubstring.substring(start, end);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String\u2026ing(startIndex, endIndex)");
        return string;
    }

    public static /* synthetic */ String trimSubstring$default(String string, int n, int n2, int n3, Object object) {
        if ((n3 & 1) != 0) {
            n = 0;
        }
        if ((n3 & 2) != 0) {
            n2 = string.length();
        }
        return Util.trimSubstring(string, n, n2);
    }

    public static final int delimiterOffset(@NotNull String $this$delimiterOffset, @NotNull String delimiters, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$delimiterOffset, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)delimiters, (String)"delimiters");
        for (int i = startIndex; i < endIndex; ++i) {
            if (!StringsKt.contains$default((CharSequence)delimiters, (char)$this$delimiterOffset.charAt(i), (boolean)false, (int)2, null)) continue;
            return i;
        }
        return endIndex;
    }

    public static /* synthetic */ int delimiterOffset$default(String string, String string2, int n, int n2, int n3, Object object) {
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = string.length();
        }
        return Util.delimiterOffset(string, string2, n, n2);
    }

    public static final int delimiterOffset(@NotNull String $this$delimiterOffset, char delimiter, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$delimiterOffset, (String)"<this>");
        for (int i = startIndex; i < endIndex; ++i) {
            if ($this$delimiterOffset.charAt(i) != delimiter) continue;
            return i;
        }
        return endIndex;
    }

    public static /* synthetic */ int delimiterOffset$default(String string, char c, int n, int n2, int n3, Object object) {
        if ((n3 & 2) != 0) {
            n = 0;
        }
        if ((n3 & 4) != 0) {
            n2 = string.length();
        }
        return Util.delimiterOffset(string, c, n, n2);
    }

    public static final int indexOfControlOrNonAscii(@NotNull String $this$indexOfControlOrNonAscii) {
        Intrinsics.checkNotNullParameter((Object)$this$indexOfControlOrNonAscii, (String)"<this>");
        int n = $this$indexOfControlOrNonAscii.length();
        for (int i = 0; i < n; ++i) {
            char c = $this$indexOfControlOrNonAscii.charAt(i);
            if (Intrinsics.compare((int)c, (int)31) > 0 && Intrinsics.compare((int)c, (int)127) < 0) continue;
            return i;
        }
        return -1;
    }

    public static final boolean canParseAsIpAddress(@NotNull String $this$canParseAsIpAddress) {
        Intrinsics.checkNotNullParameter((Object)$this$canParseAsIpAddress, (String)"<this>");
        return VERIFY_AS_IP_ADDRESS.matches((CharSequence)$this$canParseAsIpAddress);
    }

    public static final boolean isSensitiveHeader(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return StringsKt.equals((String)name, (String)"Authorization", (boolean)true) || StringsKt.equals((String)name, (String)"Cookie", (boolean)true) || StringsKt.equals((String)name, (String)"Proxy-Authorization", (boolean)true) || StringsKt.equals((String)name, (String)"Set-Cookie", (boolean)true);
    }

    @NotNull
    public static final String format(@NotNull String format, Object ... args) {
        Intrinsics.checkNotNullParameter((Object)format, (String)"format");
        Intrinsics.checkNotNullParameter((Object)args, (String)"args");
        Locale locale = Locale.US;
        Object[] objectArray = Arrays.copyOf(args, args.length);
        String string = String.format(locale, format, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"format(locale, format, *args)");
        return string;
    }

    @NotNull
    public static final Charset readBomAsCharset(@NotNull BufferedSource $this$readBomAsCharset, @NotNull Charset charset) throws IOException {
        Charset charset2;
        Intrinsics.checkNotNullParameter((Object)$this$readBomAsCharset, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)charset, (String)"default");
        switch ($this$readBomAsCharset.select(UNICODE_BOMS)) {
            case 0: {
                Charset charset3 = StandardCharsets.UTF_8;
                charset2 = charset3;
                Intrinsics.checkNotNullExpressionValue((Object)charset3, (String)"UTF_8");
                break;
            }
            case 1: {
                Charset charset4 = StandardCharsets.UTF_16BE;
                charset2 = charset4;
                Intrinsics.checkNotNullExpressionValue((Object)charset4, (String)"UTF_16BE");
                break;
            }
            case 2: {
                Charset charset5 = StandardCharsets.UTF_16LE;
                charset2 = charset5;
                Intrinsics.checkNotNullExpressionValue((Object)charset5, (String)"UTF_16LE");
                break;
            }
            case 3: {
                charset2 = Charsets.INSTANCE.UTF32_BE();
                break;
            }
            case 4: {
                charset2 = Charsets.INSTANCE.UTF32_LE();
                break;
            }
            case -1: {
                charset2 = charset;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return charset2;
    }

    public static final int checkDuration(@NotNull String name, long duration, @Nullable TimeUnit unit) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        if (!(duration >= 0L)) {
            boolean $i$a$-check-Util$checkDuration$32 = false;
            String $i$a$-check-Util$checkDuration$32 = name + " < 0";
            throw new IllegalStateException($i$a$-check-Util$checkDuration$32.toString());
        }
        if (!(unit != null)) {
            boolean bl = false;
            String string = "unit == null";
            throw new IllegalStateException(string.toString());
        }
        long millis = unit.toMillis(duration);
        if (!(millis <= Integer.MAX_VALUE)) {
            boolean $i$a$-require-Util$checkDuration$52 = false;
            String $i$a$-require-Util$checkDuration$52 = name + " too large.";
            throw new IllegalArgumentException($i$a$-require-Util$checkDuration$52.toString());
        }
        if (!(millis != 0L || duration <= 0L)) {
            boolean bl = false;
            String string = name + " too small.";
            throw new IllegalArgumentException(string.toString());
        }
        return (int)millis;
    }

    public static final int parseHexDigit(char $this$parseHexDigit) {
        int n;
        block1: {
            char c;
            block0: {
                c = $this$parseHexDigit;
                boolean bl = '0' <= c ? c < ':' : false;
                if (!bl) break block0;
                n = $this$parseHexDigit - 48;
                break block1;
            }
            n = ('a' <= c ? c < 'g' : false) ? $this$parseHexDigit - 97 + 10 : (('A' <= c ? c < 'G' : false) ? $this$parseHexDigit - 65 + 10 : -1);
        }
        return n;
    }

    @NotNull
    public static final Headers toHeaders(@NotNull List<Header> $this$toHeaders) {
        Intrinsics.checkNotNullParameter($this$toHeaders, (String)"<this>");
        Headers.Builder builder = new Headers.Builder();
        for (Header header : $this$toHeaders) {
            ByteString name = header.component1();
            ByteString value = header.component2();
            builder.addLenient$okhttp(name.utf8(), value.utf8());
        }
        return builder.build();
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final List<Header> toHeaderList(@NotNull Headers $this$toHeaderList) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)$this$toHeaderList, (String)"<this>");
        Iterable $this$map$iv = (Iterable)RangesKt.until((int)0, (int)$this$toHeaderList.size());
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.iterator();
        while (iterator.hasNext()) {
            void it;
            int item$iv$iv;
            int n = item$iv$iv = ((IntIterator)iterator).nextInt();
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new Header($this$toHeaderList.name((int)it), $this$toHeaderList.value((int)it)));
        }
        return (List)destination$iv$iv;
    }

    public static final boolean canReuseConnectionFor(@NotNull HttpUrl $this$canReuseConnectionFor, @NotNull HttpUrl other) {
        Intrinsics.checkNotNullParameter((Object)$this$canReuseConnectionFor, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)other, (String)"other");
        return Intrinsics.areEqual((Object)$this$canReuseConnectionFor.host(), (Object)other.host()) && $this$canReuseConnectionFor.port() == other.port() && Intrinsics.areEqual((Object)$this$canReuseConnectionFor.scheme(), (Object)other.scheme());
    }

    @NotNull
    public static final EventListener.Factory asFactory(@NotNull EventListener $this$asFactory) {
        Intrinsics.checkNotNullParameter((Object)$this$asFactory, (String)"<this>");
        return arg_0 -> Util.asFactory$lambda$8($this$asFactory, arg_0);
    }

    public static final int and(byte $this$and, int mask) {
        return $this$and & mask;
    }

    public static final int and(short $this$and, int mask) {
        return $this$and & mask;
    }

    public static final long and(int $this$and, long mask) {
        return (long)$this$and & mask;
    }

    public static final void writeMedium(@NotNull BufferedSink $this$writeMedium, int medium) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$writeMedium, (String)"<this>");
        $this$writeMedium.writeByte(medium >>> 16 & 0xFF);
        $this$writeMedium.writeByte(medium >>> 8 & 0xFF);
        $this$writeMedium.writeByte(medium & 0xFF);
    }

    public static final int readMedium(@NotNull BufferedSource $this$readMedium) throws IOException {
        Intrinsics.checkNotNullParameter((Object)$this$readMedium, (String)"<this>");
        return Util.and($this$readMedium.readByte(), 255) << 16 | Util.and($this$readMedium.readByte(), 255) << 8 | Util.and($this$readMedium.readByte(), 255);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final boolean skipAll(@NotNull Source $this$skipAll, int duration, @NotNull TimeUnit timeUnit) throws IOException {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)$this$skipAll, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)((Object)timeUnit), (String)"timeUnit");
        long nowNs = System.nanoTime();
        long originalDurationNs = $this$skipAll.timeout().hasDeadline() ? $this$skipAll.timeout().deadlineNanoTime() - nowNs : Long.MAX_VALUE;
        $this$skipAll.timeout().deadlineNanoTime(nowNs + Math.min(originalDurationNs, timeUnit.toNanos(duration)));
        try {
            Buffer skipBuffer = new Buffer();
            while ($this$skipAll.read(skipBuffer, 8192L) != -1L) {
                skipBuffer.clear();
            }
            bl = true;
        }
        catch (InterruptedIOException _) {
            bl = false;
        }
        finally {
            if (originalDurationNs == Long.MAX_VALUE) {
                $this$skipAll.timeout().clearDeadline();
            } else {
                $this$skipAll.timeout().deadlineNanoTime(nowNs + originalDurationNs);
            }
        }
        return bl;
    }

    public static final boolean discard(@NotNull Source $this$discard, int timeout2, @NotNull TimeUnit timeUnit) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)$this$discard, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)((Object)timeUnit), (String)"timeUnit");
        try {
            bl = Util.skipAll($this$discard, timeout2, timeUnit);
        }
        catch (IOException _) {
            bl = false;
        }
        return bl;
    }

    @NotNull
    public static final String peerName(@NotNull Socket $this$peerName) {
        String string;
        Intrinsics.checkNotNullParameter((Object)$this$peerName, (String)"<this>");
        SocketAddress address = $this$peerName.getRemoteSocketAddress();
        if (address instanceof InetSocketAddress) {
            String string2 = ((InetSocketAddress)address).getHostName();
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"address.hostName");
        } else {
            string = address.toString();
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final boolean isHealthy(@NotNull Socket $this$isHealthy, @NotNull BufferedSource source) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)$this$isHealthy, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)source, (String)"source");
        try {
            boolean bl2;
            int readTimeout = $this$isHealthy.getSoTimeout();
            try {
                $this$isHealthy.setSoTimeout(1);
                bl2 = !source.exhausted();
            }
            finally {
                $this$isHealthy.setSoTimeout(readTimeout);
            }
            bl = bl2;
        }
        catch (SocketTimeoutException _) {
            bl = true;
        }
        catch (IOException _) {
            bl = false;
        }
        return bl;
    }

    public static final void ignoreIoExceptions(@NotNull Function0<Unit> block) {
        Intrinsics.checkNotNullParameter(block, (String)"block");
        boolean $i$f$ignoreIoExceptions = false;
        try {
            block.invoke();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void threadName(@NotNull String name, @NotNull Function0<Unit> block) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter(block, (String)"block");
        boolean $i$f$threadName = false;
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();
        currentThread.setName(name);
        try {
            block.invoke();
        }
        finally {
            InlineMarker.finallyStart((int)1);
            currentThread.setName(oldName);
            InlineMarker.finallyEnd((int)1);
        }
    }

    public static final int skipAll(@NotNull Buffer $this$skipAll, byte b) {
        Intrinsics.checkNotNullParameter((Object)$this$skipAll, (String)"<this>");
        int count = 0;
        while (!$this$skipAll.exhausted() && $this$skipAll.getByte(0L) == b) {
            ++count;
            $this$skipAll.readByte();
        }
        return count;
    }

    public static final int indexOfNonWhitespace(@NotNull String $this$indexOfNonWhitespace, int startIndex) {
        Intrinsics.checkNotNullParameter((Object)$this$indexOfNonWhitespace, (String)"<this>");
        int n = $this$indexOfNonWhitespace.length();
        for (int i = startIndex; i < n; ++i) {
            char c = $this$indexOfNonWhitespace.charAt(i);
            if (c == ' ' || c == '\t') continue;
            return i;
        }
        return $this$indexOfNonWhitespace.length();
    }

    public static /* synthetic */ int indexOfNonWhitespace$default(String string, int n, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = 0;
        }
        return Util.indexOfNonWhitespace(string, n);
    }

    public static final long headersContentLength(@NotNull Response $this$headersContentLength) {
        Intrinsics.checkNotNullParameter((Object)$this$headersContentLength, (String)"<this>");
        String string = $this$headersContentLength.headers().get("Content-Length");
        return string != null ? Util.toLongOrDefault(string, -1L) : -1L;
    }

    public static final long toLongOrDefault(@NotNull String $this$toLongOrDefault, long defaultValue) {
        long l;
        Intrinsics.checkNotNullParameter((Object)$this$toLongOrDefault, (String)"<this>");
        try {
            l = Long.parseLong($this$toLongOrDefault);
        }
        catch (NumberFormatException _) {
            l = defaultValue;
        }
        return l;
    }

    public static final int toNonNegativeInt(@Nullable String $this$toNonNegativeInt, int defaultValue) {
        try {
            String string = $this$toNonNegativeInt;
            if (string == null) {
                return defaultValue;
            }
            long value = Long.parseLong(string);
            return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (value < 0L ? 0 : (int)value);
        }
        catch (NumberFormatException _) {
            return defaultValue;
        }
    }

    @NotNull
    public static final <T> List<T> toImmutableList(@NotNull List<? extends T> $this$toImmutableList) {
        Intrinsics.checkNotNullParameter($this$toImmutableList, (String)"<this>");
        List list = Collections.unmodifiableList(CollectionsKt.toMutableList((Collection)$this$toImmutableList));
        Intrinsics.checkNotNullExpressionValue(list, (String)"unmodifiableList(toMutableList())");
        return list;
    }

    @SafeVarargs
    @NotNull
    public static final <T> List<T> immutableListOf(T ... elements) {
        Intrinsics.checkNotNullParameter(elements, (String)"elements");
        Object[] objectArray = (Object[])elements.clone();
        List list = Collections.unmodifiableList(CollectionsKt.listOf((Object[])Arrays.copyOf(objectArray, objectArray.length)));
        Intrinsics.checkNotNullExpressionValue(list, (String)"unmodifiableList(listOf(*elements.clone()))");
        return list;
    }

    @NotNull
    public static final <K, V> Map<K, V> toImmutableMap(@NotNull Map<K, ? extends V> $this$toImmutableMap) {
        Map map;
        Intrinsics.checkNotNullParameter($this$toImmutableMap, (String)"<this>");
        if ($this$toImmutableMap.isEmpty()) {
            map = MapsKt.emptyMap();
        } else {
            Map map2 = Collections.unmodifiableMap((Map)new LinkedHashMap<K, V>($this$toImmutableMap));
            map = map2;
            Intrinsics.checkNotNullExpressionValue(map2, (String)"{\n    Collections.unmodi\u2026(LinkedHashMap(this))\n  }");
        }
        return map;
    }

    public static final void closeQuietly(@NotNull Closeable $this$closeQuietly) {
        Intrinsics.checkNotNullParameter((Object)$this$closeQuietly, (String)"<this>");
        try {
            $this$closeQuietly.close();
        }
        catch (RuntimeException rethrown) {
            throw rethrown;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static final void closeQuietly(@NotNull Socket $this$closeQuietly) {
        Intrinsics.checkNotNullParameter((Object)$this$closeQuietly, (String)"<this>");
        try {
            $this$closeQuietly.close();
        }
        catch (AssertionError e) {
            throw e;
        }
        catch (RuntimeException rethrown) {
            if (Intrinsics.areEqual((Object)rethrown.getMessage(), (Object)"bio == null")) {
                return;
            }
            throw rethrown;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static final void closeQuietly(@NotNull ServerSocket $this$closeQuietly) {
        Intrinsics.checkNotNullParameter((Object)$this$closeQuietly, (String)"<this>");
        try {
            $this$closeQuietly.close();
        }
        catch (RuntimeException rethrown) {
            throw rethrown;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * Exception decompiling
     */
    public static final boolean isCivilized(@NotNull FileSystem $this$isCivilized, @NotNull File file) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @NotNull
    public static final String toHexString(long $this$toHexString) {
        String string = Long.toHexString($this$toHexString);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toHexString(this)");
        return string;
    }

    @NotNull
    public static final String toHexString(int $this$toHexString) {
        String string = Integer.toHexString($this$toHexString);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toHexString(this)");
        return string;
    }

    public static final void wait(@NotNull Object $this$wait) {
        Intrinsics.checkNotNullParameter((Object)$this$wait, (String)"<this>");
        boolean $i$f$wait = false;
        $this$wait.wait();
    }

    public static final void notify(@NotNull Object $this$notify) {
        Intrinsics.checkNotNullParameter((Object)$this$notify, (String)"<this>");
        boolean $i$f$notify = false;
        $this$notify.notify();
    }

    public static final void notifyAll(@NotNull Object $this$notifyAll) {
        Intrinsics.checkNotNullParameter((Object)$this$notifyAll, (String)"<this>");
        boolean $i$f$notifyAll = false;
        $this$notifyAll.notifyAll();
    }

    @Nullable
    public static final <T> T readFieldOrNull(@NotNull Object instance, @NotNull Class<T> fieldType, @NotNull String fieldName) {
        Object delegate;
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter(fieldType, (String)"fieldType");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        Class<?> c = instance.getClass();
        while (!Intrinsics.areEqual(c, Object.class)) {
            try {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(instance);
                return !fieldType.isInstance(value) ? null : (T)fieldType.cast(value);
            }
            catch (NoSuchFieldException field) {
                Intrinsics.checkNotNullExpressionValue(c.getSuperclass(), (String)"c.superclass");
            }
        }
        if (!Intrinsics.areEqual((Object)fieldName, (Object)"delegate") && (delegate = Util.readFieldOrNull(instance, Object.class, "delegate")) != null) {
            return Util.readFieldOrNull(delegate, fieldType, fieldName);
        }
        return null;
    }

    public static final <E> void addIfAbsent(@NotNull List<E> $this$addIfAbsent, E element) {
        Intrinsics.checkNotNullParameter($this$addIfAbsent, (String)"<this>");
        if (!$this$addIfAbsent.contains(element)) {
            $this$addIfAbsent.add(element);
        }
    }

    public static final void assertThreadHoldsLock(@NotNull Object $this$assertThreadHoldsLock) {
        Intrinsics.checkNotNullParameter((Object)$this$assertThreadHoldsLock, (String)"<this>");
        boolean $i$f$assertThreadHoldsLock = false;
        if (assertionsEnabled && !Thread.holdsLock($this$assertThreadHoldsLock)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertThreadHoldsLock));
        }
    }

    public static final void assertThreadDoesntHoldLock(@NotNull Object $this$assertThreadDoesntHoldLock) {
        Intrinsics.checkNotNullParameter((Object)$this$assertThreadDoesntHoldLock, (String)"<this>");
        boolean $i$f$assertThreadDoesntHoldLock = false;
        if (assertionsEnabled && Thread.holdsLock($this$assertThreadDoesntHoldLock)) {
            throw new AssertionError((Object)("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertThreadDoesntHoldLock));
        }
    }

    @NotNull
    public static final Throwable withSuppressed(@NotNull Exception $this$withSuppressed, @NotNull List<? extends Exception> suppressed) {
        Exception exception;
        Intrinsics.checkNotNullParameter((Object)$this$withSuppressed, (String)"<this>");
        Intrinsics.checkNotNullParameter(suppressed, (String)"suppressed");
        Exception $this$withSuppressed_u24lambda_u2410 = exception = $this$withSuppressed;
        boolean bl = false;
        for (Exception exception2 : suppressed) {
            ExceptionsKt.addSuppressed((Throwable)$this$withSuppressed_u24lambda_u2410, (Throwable)exception2);
        }
        return exception;
    }

    @NotNull
    public static final <T> List<T> filterList(@NotNull Iterable<? extends T> $this$filterList, @NotNull Function1<? super T, Boolean> predicate) {
        Intrinsics.checkNotNullParameter($this$filterList, (String)"<this>");
        Intrinsics.checkNotNullParameter(predicate, (String)"predicate");
        boolean $i$f$filterList = false;
        List result = CollectionsKt.emptyList();
        for (T i : $this$filterList) {
            if (!((Boolean)predicate.invoke(i)).booleanValue()) continue;
            if (result.isEmpty()) {
                result = new ArrayList();
            }
            List list = result;
            Intrinsics.checkNotNull((Object)list, (String)"null cannot be cast to non-null type kotlin.collections.MutableList<T of okhttp3.internal.Util.filterList>");
            TypeIntrinsics.asMutableList((Object)list).add(i);
        }
        return result;
    }

    private static final Thread threadFactory$lambda$1(String $name, boolean $daemon, Runnable runnable2) {
        Thread thread;
        Intrinsics.checkNotNullParameter((Object)$name, (String)"$name");
        Thread $this$threadFactory_u24lambda_u241_u24lambda_u240 = thread = new Thread(runnable2, $name);
        boolean bl = false;
        $this$threadFactory_u24lambda_u241_u24lambda_u240.setDaemon($daemon);
        return thread;
    }

    private static final EventListener asFactory$lambda$8(EventListener $this_asFactory, Call it) {
        Intrinsics.checkNotNullParameter((Object)$this_asFactory, (String)"$this_asFactory");
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return $this_asFactory;
    }

    static {
        ByteString[] byteStringArray = new ByteString[]{ByteString.Companion.decodeHex("efbbbf"), ByteString.Companion.decodeHex("feff"), ByteString.Companion.decodeHex("fffe"), ByteString.Companion.decodeHex("0000ffff"), ByteString.Companion.decodeHex("ffff0000")};
        UNICODE_BOMS = Options.Companion.of(byteStringArray);
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Intrinsics.checkNotNull((Object)timeZone);
        UTC = timeZone;
        VERIFY_AS_IP_ADDRESS = new Regex("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
        assertionsEnabled = OkHttpClient.class.desiredAssertionStatus();
        String string = OkHttpClient.class.getName();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"OkHttpClient::class.java.name");
        okHttpName = StringsKt.removeSuffix((String)StringsKt.removePrefix((String)string, (CharSequence)"okhttp3."), (CharSequence)"Client");
    }
}

