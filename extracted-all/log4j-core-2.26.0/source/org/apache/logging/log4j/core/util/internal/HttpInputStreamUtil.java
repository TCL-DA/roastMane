package org.apache.logging.log4j.core.util.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.Instant;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AuthorizationProvider;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public final class HttpInputStreamUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int NOT_MODIFIED = 304;
   private static final int NOT_AUTHORIZED = 401;
   private static final int FORBIDDEN = 403;
   private static final int NOT_FOUND = 404;
   private static final int OK = 200;
   private static final int BUF_SIZE = 1024;

   public static HttpInputStreamUtil.Result getInputStream(final LastModifiedSource source, final AuthorizationProvider authorizationProvider) {
      HttpInputStreamUtil.Result result = new HttpInputStreamUtil.Result();

      try {
         long lastModified = source.getLastModified();
         HttpURLConnection connection = UrlConnectionFactory.createConnection(
            source.getURI().toURL(), lastModified, SslConfigurationFactory.getSslConfiguration(), authorizationProvider
         );
         connection.connect();

         HttpInputStreamUtil.Result var31;
         try {
            int code = connection.getResponseCode();
            switch (code) {
               case 200:
                  try {
                     InputStream is = connection.getInputStream();

                     try {
                        source.setLastModified(connection.getLastModified());
                        LOGGER.debug(
                           "{} resource {}: last modified on {}",
                           new Supplier[]{formatProtocol(source), () -> source, () -> Instant.ofEpochMilli(connection.getLastModified())}
                        );
                        result.status = Status.SUCCESS;
                        result.bytes = readStream(is);
                        var31 = result;
                     } catch (Throwable var24) {
                        if (is != null) {
                           try {
                              is.close();
                           } catch (Throwable var21) {
                              var24.addSuppressed(var21);
                           }
                        }

                        throw var24;
                     }

                     if (is != null) {
                        is.close();
                     }
                     break;
                  } catch (IOException var25) {
                     IOException e = var25;

                     try {
                        InputStream es = connection.getErrorStream();

                        try {
                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug("Error accessing {} resource at {}: {}", formatProtocol(source).get(), source, readStream(es), e);
                           }
                        } catch (Throwable var22) {
                           if (es != null) {
                              try {
                                 es.close();
                              } catch (Throwable var20) {
                                 var22.addSuppressed(var20);
                              }
                           }

                           throw var22;
                        }

                        if (es != null) {
                           es.close();
                        }
                     } catch (IOException var23) {
                        LOGGER.debug("Error accessing {} resource at {}", new Supplier[]{formatProtocol(source), () -> source, () -> var25});
                     }

                     throw new ConfigurationException("Unable to access " + source, var25);
                  }
               case 304:
                  LOGGER.debug(
                     "{} resource {}: not modified since {}", new Supplier[]{formatProtocol(source), () -> source, () -> Instant.ofEpochMilli(lastModified)}
                  );
                  result.status = Status.NOT_MODIFIED;
                  return result;
               case 401:
                  throw new ConfigurationException("Authentication required for " + source);
               case 403:
                  throw new ConfigurationException("Access denied to " + source);
               case 404:
                  LOGGER.debug("{} resource {}: not found", new Supplier[]{formatProtocol(source), () -> source});
                  result.status = Status.NOT_FOUND;
                  return result;
               default:
                  if (code < 0) {
                     LOGGER.debug("{} resource {}: invalid response code", formatProtocol(source), source);
                  } else {
                     LOGGER.debug("{} resource {}: unexpected response code {}", formatProtocol(source), source, code);
                  }

                  throw new ConfigurationException("Unable to access " + source);
            }
         } finally {
            connection.disconnect();
         }

         return var31;
      } catch (IOException var27) {
         LOGGER.debug("Error accessing {} resource at {}", formatProtocol(source), source, var27);
         throw new ConfigurationException("Unable to access " + source, var27);
      }
   }

   private static Supplier<String> formatProtocol(Source source) {
      return () -> Strings.toRootUpperCase(source.getURI().getScheme());
   }

   public static byte[] readStream(final InputStream is) throws IOException {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];

      int length;
      while ((length = is.read(buffer)) != -1) {
         result.write(buffer, 0, length);
      }

      return result.toByteArray();
   }

   @NullMarked
   public static class Result {
      @Nullable
      private byte[] bytes = null;
      private Status status;

      public Result() {
         this(Status.ERROR);
      }

      public Result(final Status status) {
         this.status = status;
      }

      @Nullable
      public InputStream getInputStream() {
         return this.bytes != null ? new ByteArrayInputStream(this.bytes) : null;
      }

      public Status getStatus() {
         return this.status;
      }
   }
}
