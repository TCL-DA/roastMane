package org.eclipse.jetty.security.authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestAuthenticator extends LoginAuthenticator {
   private static final Logger LOG = LoggerFactory.getLogger(DigestAuthenticator.class);
   private static final QuotedStringTokenizer TOKENIZER = QuotedStringTokenizer.builder().delimiters("=, ").returnDelimiters().allowEmbeddedQuotes().build();
   private final SecureRandom _random = new SecureRandom();
   private final Queue<DigestAuthenticator.Nonce> _nonceQueue = new ConcurrentLinkedQueue<>();
   private final ConcurrentMap<String, DigestAuthenticator.Nonce> _nonceMap = new ConcurrentHashMap<>();
   private long _maxNonceAgeMs = 60000L;
   private int _maxNC = 1024;

   @Override
   public void setConfiguration(Authenticator.Configuration configuration) {
      super.setConfiguration(configuration);
      String mna = configuration.getParameter("maxNonceAge");
      if (mna != null) {
         this.setMaxNonceAge(Long.parseLong(mna));
      }

      String mnc = configuration.getParameter("maxNonceCount");
      if (mnc != null) {
         this.setMaxNonceCount(Integer.parseInt(mnc));
      }
   }

   public int getMaxNonceCount() {
      return this._maxNC;
   }

   public void setMaxNonceCount(int maxNC) {
      this._maxNC = maxNC;
   }

   public long getMaxNonceAge() {
      return this._maxNonceAgeMs;
   }

   public void setMaxNonceAge(long maxNonceAgeInMillis) {
      this._maxNonceAgeMs = maxNonceAgeInMillis;
   }

   @Override
   public String getAuthenticationType() {
      return "DIGEST";
   }

   @Override
   public AuthenticationState validateRequest(Request req, Response res, Callback callback) throws ServerAuthException {
      String credentials = req.getHeaders().get(HttpHeader.AUTHORIZATION);
      boolean stale = false;
      if (credentials != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Credentials: {}", credentials);
         }

         DigestAuthenticator.Digest digest = new DigestAuthenticator.Digest(req.getMethod());
         String last = null;
         String name = null;
         Iterator<String> i = TOKENIZER.tokenize(credentials);

         while (i.hasNext()) {
            String tok = i.next();
            char c = tok.length() == 1 ? tok.charAt(0) : 0;
            switch (c) {
               case ' ':
                  break;
               case ',':
                  name = null;
                  break;
               case '=':
                  name = last;
                  last = tok;
                  break;
               default:
                  last = tok;
                  if (name != null) {
                     if ("username".equalsIgnoreCase(name)) {
                        digest.username = tok;
                     } else if ("realm".equalsIgnoreCase(name)) {
                        digest.realm = tok;
                     } else if ("nonce".equalsIgnoreCase(name)) {
                        digest.nonce = tok;
                     } else if ("nc".equalsIgnoreCase(name)) {
                        digest.nc = tok;
                     } else if ("cnonce".equalsIgnoreCase(name)) {
                        digest.cnonce = tok;
                     } else if ("qop".equalsIgnoreCase(name)) {
                        digest.qop = tok;
                     } else if ("uri".equalsIgnoreCase(name)) {
                        digest.uri = tok;
                     } else if ("response".equalsIgnoreCase(name)) {
                        digest.response = tok;
                     }

                     name = null;
                  }
            }
         }

         int n = this.checkNonce(digest, req);
         if (n > 0) {
            UserIdentity user = this.login(digest.username, digest, req, res);
            if (user != null) {
               return new LoginAuthenticator.UserAuthenticationSucceeded(this.getAuthenticationType(), user);
            }
         } else if (n == 0) {
            stale = true;
         }
      }

      if (!AuthenticationState.Deferred.isDeferred(res)) {
         String domain = req.getContext().getContextPath();
         if (domain == null) {
            domain = "/";
         }

         res.getHeaders()
            .put(
               HttpHeader.WWW_AUTHENTICATE.asString(),
               "Digest realm=\""
                  + this._loginService.getName()
                  + "\", domain=\""
                  + domain
                  + "\", nonce=\""
                  + this.newNonce(req)
                  + "\", algorithm=MD5, qop=\"auth\", stale="
                  + stale
            );
         Response.writeError(req, res, callback, 401);
         return AuthenticationState.CHALLENGE;
      } else {
         return null;
      }
   }

   @Override
   public UserIdentity login(String username, Object credentials, Request request, Response response) {
      DigestAuthenticator.Digest digest = (DigestAuthenticator.Digest)credentials;
      return !Objects.equals(digest.realm, this._loginService.getName()) ? null : super.login(username, credentials, request, response);
   }

   public String newNonce(Request request) {
      DigestAuthenticator.Nonce nonce;
      do {
         byte[] nounce = new byte[24];
         this._random.nextBytes(nounce);
         nonce = new DigestAuthenticator.Nonce(Base64.getEncoder().encodeToString(nounce), Request.getTimeStamp(request), this.getMaxNonceCount());
      } while (this._nonceMap.putIfAbsent(nonce._nonce, nonce) != null);

      this._nonceQueue.add(nonce);
      return nonce._nonce;
   }

   private int checkNonce(DigestAuthenticator.Digest digest, Request request) {
      long expired = Request.getTimeStamp(request) - this.getMaxNonceAge();

      for (DigestAuthenticator.Nonce nonce = this._nonceQueue.peek(); nonce != null && nonce._ts < expired; nonce = this._nonceQueue.peek()) {
         this._nonceQueue.remove(nonce);
         this._nonceMap.remove(nonce._nonce);
      }

      try {
         DigestAuthenticator.Nonce var9 = this._nonceMap.get(digest.nonce);
         if (var9 == null) {
            return 0;
         } else {
            long count = Long.parseLong(digest.nc, 16);
            if (count >= (long)this._maxNC) {
               return 0;
            } else {
               return var9.seen((int)count) ? -1 : 1;
            }
         }
      } catch (Exception var8) {
         LOG.trace("IGNORED", var8);
         return -1;
      }
   }

   private static class Digest extends Credential {
      private static final long serialVersionUID = -2484639019549527724L;
      final String method;
      String username = "";
      String realm = "";
      String nonce = "";
      String nc = "";
      String cnonce = "";
      String qop = "";
      String uri = "";
      String response = "";

      Digest(String m) {
         this.method = m;
      }

      public boolean check(Object credentials) {
         if (credentials instanceof char[]) {
            credentials = new String((char[])credentials);
         }

         String password = credentials instanceof String ? (String)credentials : credentials.toString();

         try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] ha1;
            if (credentials instanceof MD5) {
               ha1 = ((MD5)credentials).getDigest();
            } else {
               md.update(this.username.getBytes(StandardCharsets.ISO_8859_1));
               md.update((byte)58);
               md.update(this.realm.getBytes(StandardCharsets.ISO_8859_1));
               md.update((byte)58);
               md.update(password.getBytes(StandardCharsets.ISO_8859_1));
               ha1 = md.digest();
            }

            md.reset();
            md.update(this.method.getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(this.uri.getBytes(StandardCharsets.ISO_8859_1));
            byte[] ha2 = md.digest();
            md.update(TypeUtil.toString(ha1, 16).getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(this.nonce.getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(this.nc.getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(this.cnonce.getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(this.qop.getBytes(StandardCharsets.ISO_8859_1));
            md.update((byte)58);
            md.update(TypeUtil.toString(ha2, 16).getBytes(StandardCharsets.ISO_8859_1));
            byte[] digest = md.digest();
            return stringEquals(TypeUtil.toString(digest, 16).toLowerCase(), this.response == null ? null : this.response.toLowerCase());
         } catch (Exception var7) {
            DigestAuthenticator.LOG.warn("Unable to process digest", var7);
            return false;
         }
      }

      public String toString() {
         return this.username + "," + this.response;
      }
   }

   private static class Nonce {
      private final AutoLock _lock = new AutoLock();
      final String _nonce;
      final long _ts;
      final BitSet _seen;

      public Nonce(String nonce, long ts, int size) {
         this._nonce = nonce;
         this._ts = ts;
         this._seen = new BitSet(size);
      }

      public boolean seen(int count) {
         AutoLock ignored = this._lock.lock();

         boolean s;
         label43: {
            boolean var4;
            try {
               if (count >= this._seen.size()) {
                  s = true;
                  break label43;
               }

               s = this._seen.get(count);
               this._seen.set(count);
               var4 = s;
            } catch (Throwable var6) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (ignored != null) {
               ignored.close();
            }

            return var4;
         }

         if (ignored != null) {
            ignored.close();
         }

         return s;
      }
   }
}
