package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ByteRange(long first, long last) {
   private static final Logger LOG = LoggerFactory.getLogger(ByteRange.class);

   private ByteRange coalesce(ByteRange r) {
      return new ByteRange(Math.min(this.first, r.first), Math.max(this.last, r.last));
   }

   private boolean overlaps(ByteRange range) {
      return range.first >= this.first && range.first <= this.last
         || range.last >= this.first && range.last <= this.last
         || range.first < this.first && range.last > this.last;
   }

   public long getLength() {
      return this.last - this.first + 1L;
   }

   public String toHeaderValue(long length) {
      return "bytes %d-%d/%d".formatted(this.first, this.last, length);
   }

   public static List<ByteRange> parse(List<String> headers, long length) {
      long end = length - 1L;
      List<ByteRange> ranges = null;
      String prefix = "bytes=";

      for (String header : headers) {
         String value = header.trim();
         if (value.startsWith(prefix)) {
            value = value.substring(prefix.length());

            for (String range : value.split(",")) {
               try {
                  range = range.trim();
                  if (range.startsWith(prefix)) {
                     range = range.substring(prefix.length());
                  }

                  long first = -1L;
                  long last = -1L;
                  int dash = range.indexOf(45);
                  if (dash < 0 || range.indexOf("-", dash + 1) >= 0) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("bad range format: {}", range);
                     }
                     break;
                  }

                  if (dash > 0) {
                     first = Long.parseLong(range.substring(0, dash).trim());
                  }

                  if (dash < range.length() - 1) {
                     last = Long.parseLong(range.substring(dash + 1).trim());
                  }

                  if (first == -1L) {
                     if (last == -1L) {
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("bad range format: {}", range);
                        }
                        break;
                     }

                     if (last == 0L) {
                        continue;
                     }

                     first = Math.max(0L, end - last + 1L);
                     last = end;
                  } else {
                     if (first > end) {
                        continue;
                     }

                     if (last == -1L || last > end) {
                        last = end;
                     }
                  }

                  if (last < first) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("bad range format: {}", range);
                     }
                     break;
                  }

                  ByteRange byteRange = new ByteRange(first, last);
                  if (ranges == null) {
                     ranges = new ArrayList<>();
                  }

                  ranges.add(byteRange);
               } catch (Throwable var20) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("could not parse range {}", header, var20);
                  }
               }
            }
         }
      }

      if (ranges == null) {
         return List.of();
      } else if (ranges.size() == 1) {
         return ranges;
      } else {
         List<ByteRange> result = new ArrayList<>();
         ranges.sort(Comparator.comparingLong(ByteRange::first));
         ByteRange range1 = ranges.get(0);

         for (int i = 1; i < ranges.size(); i++) {
            ByteRange range2 = ranges.get(i);
            if (range1.overlaps(range2)) {
               range1 = range1.coalesce(range2);
            } else {
               result.add(range1);
               range1 = range2;
            }
         }

         result.add(range1);
         return result;
      }
   }

   public static String toNonSatisfiableHeaderValue(long length) {
      return "bytes */" + length;
   }
}
