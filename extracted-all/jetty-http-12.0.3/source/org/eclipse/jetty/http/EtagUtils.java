package org.eclipse.jetty.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Base64.Encoder;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.resource.Resource;

public final class EtagUtils {
   public static final String ETAG_SEPARATOR = System.getProperty(EtagUtils.class.getName() + ".ETAG_SEPARATOR", "--");

   private EtagUtils() {
   }

   public static HttpField createWeakEtagField(Resource resource) {
      return createWeakEtagField(resource, null);
   }

   public static HttpField createWeakEtagField(Resource resource, String etagSuffix) {
      Path path = resource.getPath();
      if (path == null) {
         return null;
      } else {
         String etagValue = computeWeakEtag(path, etagSuffix);
         return etagValue != null ? new PreEncodedHttpField(HttpHeader.ETAG, etagValue) : null;
      }
   }

   public static String computeWeakEtag(Resource resource) {
      return computeWeakEtag(resource, null);
   }

   public static String computeWeakEtag(Resource resource, String etagSuffix) {
      if (resource != null && resource.exists() && !resource.isDirectory()) {
         Path path = resource.getPath();
         if (path != null) {
            return computeWeakEtag(path, etagSuffix);
         } else {
            String name = resource.getName();
            Instant lastModified = resource.lastModified();
            long size = resource.length();
            return computeWeakEtag(name, lastModified, size, etagSuffix);
         }
      } else {
         return null;
      }
   }

   public static String computeWeakEtag(Path path) {
      return computeWeakEtag(path, null);
   }

   public static String computeWeakEtag(Path path, String suffix) {
      if (path == null) {
         return null;
      } else {
         String name = path.toAbsolutePath().toString();
         Instant lastModified = Instant.EPOCH;

         try {
            lastModified = Files.getLastModifiedTime(path).toInstant();
         } catch (IOException var8) {
         }

         long size = -1L;

         try {
            size = Files.size(path);
         } catch (IOException var7) {
         }

         return computeWeakEtag(name, lastModified, size, suffix);
      }
   }

   private static String computeWeakEtag(String name, Instant lastModified, long size, String etagSuffix) {
      StringBuilder b = new StringBuilder(32);
      b.append("W/\"");
      Encoder encoder = Base64.getEncoder().withoutPadding();
      long lastModifiedMillis = lastModified.toEpochMilli();
      int lhash = Objects.hashCode(name);
      b.append(encoder.encodeToString(longToBytes(lastModifiedMillis ^ (long)lhash)));
      b.append(encoder.encodeToString(longToBytes(size ^ (long)lhash)));
      if (etagSuffix != null) {
         b.append(etagSuffix);
      }

      b.append('"');
      return b.toString();
   }

   public static String rewriteWithSuffix(String etag, String newSuffix) {
      StringBuilder ret = new StringBuilder();
      boolean weak = etag.startsWith("W/");
      int start = 0;
      if (etag.startsWith("W/")) {
         weak = true;
         start = 2;
      }

      while (etag.charAt(start) == '"') {
         start++;
      }

      int end = etag.length();

      while (etag.charAt(end - 1) == '"') {
         end--;
      }

      int suffixIdx = etag.lastIndexOf(45, end);
      if (suffixIdx - 1 >= 0 && suffixIdx - 1 >= start && etag.charAt(suffixIdx - 1) == '-') {
         end = suffixIdx - 1;
      } else if (suffixIdx >= 0 && suffixIdx >= start) {
         end = suffixIdx;
      }

      if (weak) {
         ret.append("W/");
      }

      ret.append('"');
      ret.append(etag, start, end);
      ret.append(newSuffix);
      ret.append('"');
      return ret.toString();
   }

   public static boolean isWeak(String etag) {
      return etag.startsWith("W/");
   }

   public static boolean isStrong(String etag) {
      return !isWeak(etag);
   }

   public static boolean matches(String etag, String etagWithOptionalSuffix) {
      if (etag.equals(etagWithOptionalSuffix)) {
         return true;
      } else if (StringUtil.isEmpty(ETAG_SEPARATOR)) {
         return false;
      } else {
         boolean etagQuoted = etag.endsWith("\"");
         boolean etagSuffixQuoted = etagWithOptionalSuffix.endsWith("\"");
         int separator = etagWithOptionalSuffix.lastIndexOf(ETAG_SEPARATOR);
         if (etagQuoted != etagSuffixQuoted) {
            if (!etagWithOptionalSuffix.startsWith("W/") && !etag.startsWith("W/")) {
               etag = etagQuoted ? QuotedCSV.unquote(etag) : etag;
               etagWithOptionalSuffix = etagSuffixQuoted ? QuotedCSV.unquote(etagWithOptionalSuffix) : etagWithOptionalSuffix;
               separator = etagWithOptionalSuffix.lastIndexOf(ETAG_SEPARATOR);
               return separator > 0 ? etag.regionMatches(0, etagWithOptionalSuffix, 0, separator) : Objects.equals(etag, etagWithOptionalSuffix);
            } else {
               return false;
            }
         } else {
            return separator > 0 && etag.regionMatches(0, etagWithOptionalSuffix, 0, separator);
         }
      }
   }

   private static byte[] longToBytes(long value) {
      byte[] result = new byte[8];

      for (int i = 7; i >= 0; i--) {
         result[i] = (byte)((int)(value & 255L));
         value >>= 8;
      }

      return result;
   }
}
