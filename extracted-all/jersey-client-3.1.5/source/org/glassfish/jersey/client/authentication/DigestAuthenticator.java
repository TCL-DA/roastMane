package org.glassfish.jersey.client.authentication;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.message.MessageUtils;
import org.glassfish.jersey.uri.UriComponent;

final class DigestAuthenticator {
   private static final char[] HEX_ARRAY = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private static final Pattern KEY_VALUE_PAIR_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|(\\w+))\\s*,?\\s*");
   private static final int CLIENT_NONCE_BYTE_COUNT = 4;
   private final SecureRandom randomGenerator;
   private final HttpAuthenticationFilter.Credentials credentials;
   private final Map<URI, DigestAuthenticator.DigestScheme> digestCache;

   DigestAuthenticator(HttpAuthenticationFilter.Credentials credentials, final int limit) {
      this.credentials = credentials;
      this.digestCache = Collections.synchronizedMap(new LinkedHashMap<URI, DigestAuthenticator.DigestScheme>(limit) {
         private static final long serialVersionUID = 2546245625L;

         @Override
         protected boolean removeEldestEntry(Entry eldest) {
            return this.size() > limit;
         }
      });

      try {
         this.randomGenerator = SecureRandom.getInstance("SHA1PRNG");
      } catch (NoSuchAlgorithmException var4) {
         throw new RequestAuthenticationException(LocalizationMessages.ERROR_DIGEST_FILTER_GENERATOR(), var4);
      }
   }

   boolean filterRequest(ClientRequestContext request) throws IOException {
      DigestAuthenticator.DigestScheme digestScheme = this.digestCache.get(AuthenticationUtil.getCacheKey(request));
      if (digestScheme != null) {
         HttpAuthenticationFilter.Credentials cred = HttpAuthenticationFilter.getCredentials(request, this.credentials, HttpAuthenticationFilter.Type.DIGEST);
         if (cred != null) {
            request.getHeaders().add("Authorization", this.createNextAuthToken(digestScheme, request, cred));
            return true;
         }
      }

      return false;
   }

   public boolean filterResponse(ClientRequestContext request, ClientResponseContext response) throws IOException {
      if (Status.fromStatusCode(response.getStatus()) == Status.UNAUTHORIZED) {
         DigestAuthenticator.DigestScheme digestScheme = this.parseAuthHeaders((List<?>)response.getHeaders().get("WWW-Authenticate"));
         if (digestScheme == null) {
            return false;
         } else {
            HttpAuthenticationFilter.Credentials cred = HttpAuthenticationFilter.getCredentials(request, this.credentials, HttpAuthenticationFilter.Type.DIGEST);
            if (cred == null) {
               if (response.hasEntity()) {
                  AuthenticationUtil.discardInputAndClose(response.getEntityStream());
               }

               throw new ResponseAuthenticationException(null, LocalizationMessages.AUTHENTICATION_CREDENTIALS_MISSING_DIGEST());
            } else {
               boolean success = HttpAuthenticationFilter.repeatRequest(request, response, this.createNextAuthToken(digestScheme, request, cred));
               URI cacheKey = AuthenticationUtil.getCacheKey(request);
               if (success) {
                  this.digestCache.put(cacheKey, digestScheme);
               } else {
                  this.digestCache.remove(cacheKey);
               }

               return success;
            }
         }
      } else {
         return true;
      }
   }

   private DigestAuthenticator.DigestScheme parseAuthHeaders(List<?> headers) throws IOException {
      if (headers == null) {
         return null;
      } else {
         for (Object lineObject : headers) {
            if (lineObject instanceof String) {
               String line = (String)lineObject;
               String[] parts = line.trim().split("\\s+", 2);
               if (parts.length == 2 && "digest".equals(parts[0].toLowerCase(Locale.ROOT))) {
                  String realm = null;
                  String nonce = null;
                  String opaque = null;
                  DigestAuthenticator.QOP qop = DigestAuthenticator.QOP.UNSPECIFIED;
                  DigestAuthenticator.Algorithm algorithm = DigestAuthenticator.Algorithm.UNSPECIFIED;
                  boolean stale = false;
                  Matcher match = KEY_VALUE_PAIR_PATTERN.matcher(parts[1]);

                  while (match.find()) {
                     int nbGroups = match.groupCount();
                     if (nbGroups == 4) {
                        String key = match.group(1);
                        String valNoQuotes = match.group(3);
                        String valQuotes = match.group(4);
                        String val = valNoQuotes == null ? valQuotes : valNoQuotes;
                        if ("qop".equals(key)) {
                           qop = DigestAuthenticator.QOP.parse(val);
                        } else if ("realm".equals(key)) {
                           realm = val;
                        } else if ("nonce".equals(key)) {
                           nonce = val;
                        } else if ("opaque".equals(key)) {
                           opaque = val;
                        } else if ("stale".equals(key)) {
                           stale = Boolean.parseBoolean(val);
                        } else if ("algorithm".equals(key)) {
                           algorithm = DigestAuthenticator.Algorithm.parse(val);
                        }
                     }
                  }

                  return new DigestAuthenticator.DigestScheme(realm, nonce, opaque, qop, algorithm, stale);
               }
            }
         }

         return null;
      }
   }

   private String createNextAuthToken(
      DigestAuthenticator.DigestScheme ds, ClientRequestContext requestContext, HttpAuthenticationFilter.Credentials credentials
   ) throws IOException {
      StringBuilder sb = new StringBuilder(100);
      sb.append("Digest ");
      append(sb, "username", credentials.getUsername());
      append(sb, "realm", ds.getRealm());
      append(sb, "nonce", ds.getNonce());
      append(sb, "opaque", ds.getOpaque());
      append(sb, "algorithm", ds.getAlgorithm().toString(), false);
      append(sb, "qop", ds.getQop().toString(), false);
      String uri = UriComponent.fullRelativeUri(requestContext.getUri());
      append(sb, "uri", uri);
      String ha1;
      if (ds.getAlgorithm() == DigestAuthenticator.Algorithm.MD5_SESS) {
         ha1 = md5(md5(credentials.getUsername(), ds.getRealm(), new String(credentials.getPassword(), MessageUtils.getCharset(requestContext.getMediaType()))));
      } else {
         ha1 = md5(credentials.getUsername(), ds.getRealm(), new String(credentials.getPassword(), MessageUtils.getCharset(requestContext.getMediaType())));
      }

      String ha2 = md5(requestContext.getMethod(), uri);
      String response;
      if (ds.getQop() == DigestAuthenticator.QOP.UNSPECIFIED) {
         response = md5(ha1, ds.getNonce(), ha2);
      } else {
         String cnonce = this.randomBytes(4);
         append(sb, "cnonce", cnonce);
         String nc = String.format("%08x", ds.incrementCounter());
         append(sb, "nc", nc, false);
         response = md5(ha1, ds.getNonce(), nc, cnonce, ds.getQop().toString(), ha2);
      }

      append(sb, "response", response);
      return sb.toString();
   }

   private static void append(StringBuilder sb, String key, String value, boolean useQuote) {
      if (value != null) {
         if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
            sb.append(',');
         }

         sb.append(key);
         sb.append('=');
         if (useQuote) {
            sb.append('"');
         }

         sb.append(value);
         if (useQuote) {
            sb.append('"');
         }
      }
   }

   private static void append(StringBuilder sb, String key, String value) {
      append(sb, key, value, true);
   }

   private static String bytesToHex(byte[] bytes) {
      char[] hexChars = new char[bytes.length * 2];

      for (int j = 0; j < bytes.length; j++) {
         int v = bytes[j] & 255;
         hexChars[j * 2] = HEX_ARRAY[v >>> 4];
         hexChars[j * 2 + 1] = HEX_ARRAY[v & 15];
      }

      return new String(hexChars);
   }

   private static String md5(String... tokens) throws IOException {
      StringBuilder sb = new StringBuilder(100);

      for (String token : tokens) {
         if (sb.length() > 0) {
            sb.append(':');
         }

         sb.append(token);
      }

      MessageDigest md;
      try {
         md = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var6) {
         throw new IOException(var6.getMessage());
      }

      md.update(sb.toString().getBytes(HttpAuthenticationFilter.CHARACTER_SET), 0, sb.length());
      byte[] md5hash = md.digest();
      return bytesToHex(md5hash);
   }

   private String randomBytes(int nbBytes) {
      byte[] bytes = new byte[nbBytes];
      this.randomGenerator.nextBytes(bytes);
      return bytesToHex(bytes);
   }

   static enum Algorithm {
      UNSPECIFIED(null),
      MD5("MD5"),
      MD5_SESS("MD5-sess");

      private final String md;

      private Algorithm(String md) {
         this.md = md;
      }

      @Override
      public String toString() {
         return this.md;
      }

      public static DigestAuthenticator.Algorithm parse(String val) {
         if (val != null && !val.isEmpty()) {
            val = val.trim();
            return !val.contains(MD5_SESS.md) && !val.contains(MD5_SESS.md.toLowerCase(Locale.ROOT)) ? MD5 : MD5_SESS;
         } else {
            return UNSPECIFIED;
         }
      }
   }

   final class DigestScheme {
      private final String realm;
      private final String nonce;
      private final String opaque;
      private final DigestAuthenticator.Algorithm algorithm;
      private final DigestAuthenticator.QOP qop;
      private final boolean stale;
      private volatile int nc;

      DigestScheme(String realm, String nonce, String opaque, DigestAuthenticator.QOP qop, DigestAuthenticator.Algorithm algorithm, boolean stale) {
         this.realm = realm;
         this.nonce = nonce;
         this.opaque = opaque;
         this.qop = qop;
         this.algorithm = algorithm;
         this.stale = stale;
         this.nc = 0;
      }

      public int incrementCounter() {
         return ++this.nc;
      }

      public String getNonce() {
         return this.nonce;
      }

      public String getRealm() {
         return this.realm;
      }

      public String getOpaque() {
         return this.opaque;
      }

      public DigestAuthenticator.Algorithm getAlgorithm() {
         return this.algorithm;
      }

      public DigestAuthenticator.QOP getQop() {
         return this.qop;
      }

      public boolean isStale() {
         return this.stale;
      }

      public int getNc() {
         return this.nc;
      }
   }

   private static enum QOP {
      UNSPECIFIED(null),
      AUTH("auth");

      private final String qop;

      private QOP(String qop) {
         this.qop = qop;
      }

      @Override
      public String toString() {
         return this.qop;
      }

      public static DigestAuthenticator.QOP parse(String val) {
         if (val == null || val.isEmpty()) {
            return UNSPECIFIED;
         } else if (val.contains("auth")) {
            return AUTH;
         } else {
            throw new UnsupportedOperationException(LocalizationMessages.DIGEST_FILTER_QOP_UNSUPPORTED(val));
         }
      }
   }
}
