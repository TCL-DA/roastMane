package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.internal.util.collection.LRU;

public abstract class HttpHeaderReader {
   private static final HttpHeaderReader.ListElementCreator<MatchingEntityTag> MATCHING_ENTITY_TAG_CREATOR = new HttpHeaderReader.ListElementCreator<MatchingEntityTag>() {
      public MatchingEntityTag create(HttpHeaderReader reader) throws ParseException {
         return MatchingEntityTag.valueOf(reader);
      }
   };
   private static final HttpHeaderReader.MediaTypeListReader MEDIA_TYPE_LIST_READER = new HttpHeaderReader.MediaTypeListReader();
   private static final HttpHeaderReader.AcceptableMediaTypeListReader ACCEPTABLE_MEDIA_TYPE_LIST_READER = new HttpHeaderReader.AcceptableMediaTypeListReader();
   private static final HttpHeaderReader.QualitySourceMediaTypeListReader QUALITY_SOURCE_MEDIA_TYPE_LIST_READER = new HttpHeaderReader.QualitySourceMediaTypeListReader(
      
   );
   private static final HttpHeaderReader.AcceptableTokenListReader ACCEPTABLE_TOKEN_LIST_READER = new HttpHeaderReader.AcceptableTokenListReader();
   private static final HttpHeaderReader.AcceptableLanguageTagListReader ACCEPTABLE_LANGUAGE_TAG_LIST_READER = new HttpHeaderReader.AcceptableLanguageTagListReader(
      
   );
   private static final HttpHeaderReader.StringListReader STRING_LIST_READER = new HttpHeaderReader.StringListReader();

   public abstract boolean hasNext();

   public abstract boolean hasNextSeparator(char var1, boolean var2);

   public abstract HttpHeaderReader.Event next() throws ParseException;

   public abstract HttpHeaderReader.Event next(boolean var1) throws ParseException;

   protected abstract HttpHeaderReader.Event next(boolean var1, boolean var2) throws ParseException;

   protected abstract CharSequence nextSeparatedString(char var1, char var2) throws ParseException;

   protected abstract HttpHeaderReader.Event getEvent();

   public abstract CharSequence getEventValue();

   public abstract CharSequence getRemainder();

   public abstract int getIndex();

   public final CharSequence nextToken() throws ParseException {
      HttpHeaderReader.Event e = this.next(false);
      if (e != HttpHeaderReader.Event.Token) {
         throw new ParseException("Next event is not a Token", this.getIndex());
      } else {
         return this.getEventValue();
      }
   }

   public final void nextSeparator(char c) throws ParseException {
      HttpHeaderReader.Event e = this.next(false);
      if (e != HttpHeaderReader.Event.Separator) {
         throw new ParseException("Next event is not a Separator", this.getIndex());
      } else if (c != this.getEventValue().charAt(0)) {
         throw new ParseException("Expected separator '" + c + "' instead of '" + this.getEventValue().charAt(0) + "'", this.getIndex());
      }
   }

   public final CharSequence nextQuotedString() throws ParseException {
      HttpHeaderReader.Event e = this.next(false);
      if (e != HttpHeaderReader.Event.QuotedString) {
         throw new ParseException("Next event is not a Quoted String", this.getIndex());
      } else {
         return this.getEventValue();
      }
   }

   public final CharSequence nextTokenOrQuotedString() throws ParseException {
      return this.nextTokenOrQuotedString(false);
   }

   private CharSequence nextTokenOrQuotedString(boolean preserveBackslash) throws ParseException {
      HttpHeaderReader.Event e = this.next(false, preserveBackslash);
      if (e != HttpHeaderReader.Event.Token && e != HttpHeaderReader.Event.QuotedString) {
         throw new ParseException("Next event is not a Token or a Quoted String, " + this.getEventValue(), this.getIndex());
      } else {
         return this.getEventValue();
      }
   }

   public static HttpHeaderReader newInstance(String header) {
      return new HttpHeaderReaderImpl(header);
   }

   public static HttpHeaderReader newInstance(String header, boolean processComments) {
      return new HttpHeaderReaderImpl(header, processComments);
   }

   public static Date readDate(String date) throws ParseException {
      return HttpDateFormat.readDate(date);
   }

   public static int readQualityFactor(CharSequence q) throws ParseException {
      if (q != null && q.length() != 0) {
         int index = 0;
         int length = q.length();
         if (length > 5) {
            throw new ParseException("Quality value is greater than the maximum length, 5", 0);
         } else {
            char wholeNumber;
            char c = wholeNumber = q.charAt(index++);
            if (c != '0' && c != '1') {
               if (c != '.') {
                  throw new ParseException("Error parsing Quality value: a decimal numeral '0' or '1' is expected rather than '" + c + "'", index);
               }

               if (index == length) {
                  throw new ParseException("Error parsing Quality value: a decimal numeral is expected after the decimal point", index);
               }
            } else {
               if (index == length) {
                  return (c - 48) * 1000;
               }

               c = q.charAt(index++);
               if (c != '.') {
                  throw new ParseException("Error parsing Quality value: a decimal place is expected rather than '" + c + "'", index);
               }

               if (index == length) {
                  return (c - 48) * 1000;
               }
            }

            int value = 0;

            for (int exponent = 100; index < length; exponent /= 10) {
               c = q.charAt(index++);
               if (c < '0' || c > '9') {
                  throw new ParseException("Error parsing Quality value: a decimal numeral is expected rather than '" + c + "'", index);
               }

               value += (c - '0') * exponent;
            }

            if (wholeNumber != '1') {
               return value;
            } else if (value > 0) {
               throw new ParseException("The Quality value, " + q + ", is greater than 1", index);
            } else {
               return 1000;
            }
         }
      } else {
         throw new ParseException("Quality value cannot be null or an empty String", 0);
      }
   }

   public static int readQualityFactorParameter(HttpHeaderReader reader) throws ParseException {
      while (reader.hasNext()) {
         reader.nextSeparator(';');
         if (!reader.hasNext()) {
            return 1000;
         }

         CharSequence name = reader.nextToken();
         reader.nextSeparator('=');
         CharSequence value = reader.nextTokenOrQuotedString();
         if (name.length() == 1 && (name.charAt(0) == 'q' || name.charAt(0) == 'Q')) {
            return readQualityFactor(value);
         }
      }

      return 1000;
   }

   public static Map<String, String> readParameters(HttpHeaderReader reader) throws ParseException {
      return readParameters(reader, false);
   }

   public static Map<String, String> readParameters(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
      Map<String, String> m = null;

      while (reader.hasNext()) {
         reader.nextSeparator(';');

         while (reader.hasNextSeparator(';', true)) {
            reader.next();
         }

         if (!reader.hasNext()) {
            break;
         }

         String name = reader.nextToken().toString().toLowerCase(Locale.ROOT);
         reader.nextSeparator('=');
         String value;
         if ("filename".equals(name) && fileNameFix) {
            value = reader.nextTokenOrQuotedString(true).toString();
            value = value.substring(value.lastIndexOf(92) + 1);
         } else {
            value = reader.nextTokenOrQuotedString(false).toString();
         }

         if (m == null) {
            m = new LinkedHashMap<>();
         }

         m.put(name, value);
      }

      return m;
   }

   public static Map<String, Cookie> readCookies(String header) {
      return CookiesParser.parseCookies(header);
   }

   public static Cookie readCookie(String header) {
      return CookiesParser.parseCookie(header);
   }

   public static NewCookie readNewCookie(String header) {
      return CookiesParser.parseNewCookie(header);
   }

   public static Set<MatchingEntityTag> readMatchingEntityTag(String header) throws ParseException {
      if ("*".equals(header)) {
         return MatchingEntityTag.ANY_MATCH;
      } else {
         HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
         Set<MatchingEntityTag> l = new HashSet<>(1);
         HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);

         while (reader.hasNext()) {
            l.add(MATCHING_ENTITY_TAG_CREATOR.create(adapter));
            adapter.reset();
            if (reader.hasNext()) {
               reader.next();
            }
         }

         return l;
      }
   }

   public static List<MediaType> readMediaTypes(List<MediaType> l, String header) throws ParseException {
      return MEDIA_TYPE_LIST_READER.readList(l, header);
   }

   public static List<AcceptableMediaType> readAcceptMediaType(String header) throws ParseException {
      return ACCEPTABLE_MEDIA_TYPE_LIST_READER.readList(header);
   }

   public static List<QualitySourceMediaType> readQualitySourceMediaType(String header) throws ParseException {
      return QUALITY_SOURCE_MEDIA_TYPE_LIST_READER.readList(header);
   }

   public static List<QualitySourceMediaType> readQualitySourceMediaType(String[] header) throws ParseException {
      if (header.length < 2) {
         return readQualitySourceMediaType(header[0]);
      } else {
         StringBuilder sb = new StringBuilder();

         for (String h : header) {
            if (sb.length() > 0) {
               sb.append(",");
            }

            sb.append(h);
         }

         return readQualitySourceMediaType(sb.toString());
      }
   }

   public static List<AcceptableMediaType> readAcceptMediaType(String header, List<QualitySourceMediaType> priorityMediaTypes) throws ParseException {
      return new HttpHeaderReader.AcceptMediaTypeListReader(priorityMediaTypes).readList(header);
   }

   public static List<AcceptableToken> readAcceptToken(String header) throws ParseException {
      return ACCEPTABLE_TOKEN_LIST_READER.readList(header);
   }

   public static List<AcceptableLanguageTag> readAcceptLanguage(String header) throws ParseException {
      return ACCEPTABLE_LANGUAGE_TAG_LIST_READER.readList(header);
   }

   public static List<String> readStringList(String header) throws ParseException {
      return STRING_LIST_READER.readList(header);
   }

   private static class AcceptMediaTypeListReader extends HttpHeaderReader.QualifiedListReader<AcceptableMediaType> {
      private static final HttpHeaderReader.ListElementCreator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_CREATOR = new HttpHeaderReader.ListElementCreator<AcceptableMediaType>() {
         public AcceptableMediaType create(HttpHeaderReader reader) throws ParseException {
            return AcceptableMediaType.valueOf(reader);
         }
      };

      AcceptMediaTypeListReader(List<QualitySourceMediaType> priorityMediaTypes) {
         super(ACCEPTABLE_MEDIA_TYPE_CREATOR, new HttpHeaderReader.AcceptMediaTypeListReader.AcceptableMediaTypeComparator(priorityMediaTypes));
      }

      private static class AcceptableMediaTypeComparator implements Comparator<AcceptableMediaType> {
         private final List<QualitySourceMediaType> priorityMediaTypes;

         private AcceptableMediaTypeComparator(List<QualitySourceMediaType> priorityMediaTypes) {
            this.priorityMediaTypes = priorityMediaTypes;
         }

         public int compare(AcceptableMediaType o1, AcceptableMediaType o2) {
            boolean q_o1_set = false;
            int q_o1 = 0;
            boolean q_o2_set = false;
            int q_o2 = 0;

            for (QualitySourceMediaType priorityType : this.priorityMediaTypes) {
               if (!q_o1_set && MediaTypes.typeEqual(o1, priorityType)) {
                  q_o1 = o1.getQuality() * priorityType.getQuality();
                  q_o1_set = true;
               } else if (!q_o2_set && MediaTypes.typeEqual(o2, priorityType)) {
                  q_o2 = o2.getQuality() * priorityType.getQuality();
                  q_o2_set = true;
               }
            }

            int i = q_o2 - q_o1;
            if (i != 0) {
               return i;
            } else {
               i = o2.getQuality() - o1.getQuality();
               return i != 0 ? i : MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
            }
         }
      }
   }

   private static class AcceptableLanguageTagListReader extends HttpHeaderReader.QualifiedListReader<AcceptableLanguageTag> {
      private static final HttpHeaderReader.ListElementCreator<AcceptableLanguageTag> LANGUAGE_CREATOR = new HttpHeaderReader.ListElementCreator<AcceptableLanguageTag>() {
         public AcceptableLanguageTag create(HttpHeaderReader reader) throws ParseException {
            return new AcceptableLanguageTag(reader);
         }
      };

      private AcceptableLanguageTagListReader() {
         super(LANGUAGE_CREATOR);
      }
   }

   private static class AcceptableMediaTypeListReader extends HttpHeaderReader.QualifiedListReader<AcceptableMediaType> {
      private static final HttpHeaderReader.ListElementCreator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_CREATOR = new HttpHeaderReader.ListElementCreator<AcceptableMediaType>() {
         public AcceptableMediaType create(HttpHeaderReader reader) throws ParseException {
            return AcceptableMediaType.valueOf(reader);
         }
      };

      private AcceptableMediaTypeListReader() {
         super(ACCEPTABLE_MEDIA_TYPE_CREATOR, AcceptableMediaType.COMPARATOR);
      }
   }

   private static class AcceptableTokenListReader extends HttpHeaderReader.QualifiedListReader<AcceptableToken> {
      private static final HttpHeaderReader.ListElementCreator<AcceptableToken> ACCEPTABLE_TOKEN_CREATOR = new HttpHeaderReader.ListElementCreator<AcceptableToken>() {
         public AcceptableToken create(HttpHeaderReader reader) throws ParseException {
            return new AcceptableToken(reader);
         }
      };

      private AcceptableTokenListReader() {
         super(ACCEPTABLE_TOKEN_CREATOR);
      }
   }

   public static enum Event {
      Token,
      QuotedString,
      Comment,
      Separator,
      Control;
   }

   private interface ListElementCreator<T> {
      T create(HttpHeaderReader var1) throws ParseException;
   }

   private abstract static class ListReader<T> {
      private final LRU<String, List<T>> LIST_CACHE = LRU.create();
      protected final HttpHeaderReader.ListElementCreator<T> creator;

      protected ListReader(HttpHeaderReader.ListElementCreator<T> creator) {
         this.creator = creator;
      }

      protected List<T> readList(String header) throws ParseException {
         return this.readList(new ArrayList<>(), header);
      }

      private List<T> readList(List<T> l, String header) throws ParseException {
         List<T> list = this.LIST_CACHE.getIfPresent(header);
         if (list == null) {
            synchronized (this.LIST_CACHE) {
               list = this.LIST_CACHE.getIfPresent(header);
               if (list == null) {
                  HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
                  HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
                  list = new LinkedList<>();

                  while (reader.hasNext()) {
                     list.add(this.creator.create(adapter));
                     adapter.reset();
                     if (reader.hasNext()) {
                        reader.next();
                     }
                  }

                  this.LIST_CACHE.put(header, list);
               }
            }
         }

         l.addAll(list);
         return l;
      }
   }

   private static class MediaTypeListReader extends HttpHeaderReader.ListReader<MediaType> {
      private static final HttpHeaderReader.ListElementCreator<MediaType> MEDIA_TYPE_CREATOR = new HttpHeaderReader.ListElementCreator<MediaType>() {
         public MediaType create(HttpHeaderReader reader) throws ParseException {
            return MediaTypeProvider.valueOf(reader);
         }
      };

      @Override
      List<MediaType> readList(List<MediaType> l, String header) throws ParseException {
         return super.readList(l, header);
      }

      private MediaTypeListReader() {
         super(MEDIA_TYPE_CREATOR);
      }
   }

   private abstract static class QualifiedListReader<T extends Qualified> extends HttpHeaderReader.ListReader<T> {
      private final Comparator<T> comparator;

      @Override
      public List<T> readList(String header) throws ParseException {
         List<T> l = super.readList(header);
         Collections.sort(l, this.comparator);
         return l;
      }

      private QualifiedListReader(HttpHeaderReader.ListElementCreator<T> creator) {
         this(creator, (Comparator<T>)Quality.QUALIFIED_COMPARATOR);
      }

      protected QualifiedListReader(HttpHeaderReader.ListElementCreator<T> creator, Comparator<T> comparator) {
         super(creator);
         this.comparator = comparator;
      }
   }

   private static class QualitySourceMediaTypeListReader extends HttpHeaderReader.QualifiedListReader<QualitySourceMediaType> {
      private static final HttpHeaderReader.ListElementCreator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_CREATOR = new HttpHeaderReader.ListElementCreator<QualitySourceMediaType>() {
         public QualitySourceMediaType create(HttpHeaderReader reader) throws ParseException {
            return QualitySourceMediaType.valueOf(reader);
         }
      };

      private QualitySourceMediaTypeListReader() {
         super(QUALITY_SOURCE_MEDIA_TYPE_CREATOR, QualitySourceMediaType.COMPARATOR);
      }
   }

   private static class StringListReader extends HttpHeaderReader.ListReader<String> {
      private static final HttpHeaderReader.ListElementCreator<String> listElementCreator = new HttpHeaderReader.ListElementCreator<String>() {
         public String create(HttpHeaderReader reader) throws ParseException {
            reader.hasNext();
            return reader.nextToken().toString();
         }
      };

      private StringListReader() {
         super(listElementCreator);
      }
   }
}
