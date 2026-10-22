package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
   private static final Pattern CERT_PATTERN = Pattern.compile(
      "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2
   );
   private static final Pattern KEY_PATTERN = Pattern.compile(
      "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2
   );

   static ByteBuf[] readCertificates(File file) throws CertificateException {
      try {
         InputStream in = new FileInputStream(file);

         ByteBuf[] var2;
         try {
            var2 = readCertificates(in);
         } finally {
            safeClose(in);
         }

         return var2;
      } catch (FileNotFoundException var7) {
         throw new CertificateException("could not find certificate file: " + file);
      }
   }

   static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
      String content;
      try {
         content = readContent(in);
      } catch (IOException var7) {
         throw new CertificateException("failed to read certificate input stream", var7);
      }

      List<ByteBuf> certs = new ArrayList<>();
      Matcher m = CERT_PATTERN.matcher(content);

      for (int start = 0; m.find(start); start = m.end()) {
         ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
         ByteBuf der = Base64.decode(base64);
         base64.release();
         certs.add(der);
      }

      if (certs.isEmpty()) {
         throw new CertificateException("found no certificates in input stream");
      } else {
         return certs.toArray(new ByteBuf[0]);
      }
   }

   static ByteBuf readPrivateKey(File file) throws KeyException {
      try {
         InputStream in = new FileInputStream(file);

         ByteBuf var2;
         try {
            var2 = readPrivateKey(in);
         } finally {
            safeClose(in);
         }

         return var2;
      } catch (FileNotFoundException var7) {
         throw new KeyException("could not find key file: " + file);
      }
   }

   static ByteBuf readPrivateKey(InputStream in) throws KeyException {
      String content;
      try {
         content = readContent(in);
      } catch (IOException var5) {
         throw new KeyException("failed to read key input stream", var5);
      }

      Matcher m = KEY_PATTERN.matcher(content);
      if (!m.find()) {
         throw new KeyException(
            "could not find a PKCS #8 private key in input stream (see https://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)"
         );
      } else {
         ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
         ByteBuf der = Base64.decode(base64);
         base64.release();
         return der;
      }
   }

   private static String readContent(InputStream in) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try {
         byte[] buf = new byte[8192];

         while (true) {
            int ret = in.read(buf);
            if (ret < 0) {
               return out.toString(CharsetUtil.US_ASCII.name());
            }

            out.write(buf, 0, ret);
         }
      } finally {
         safeClose(out);
      }
   }

   private static void safeClose(InputStream in) {
      try {
         in.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", var2);
      }
   }

   private static void safeClose(OutputStream out) {
      try {
         out.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", var2);
      }
   }

   private PemReader() {
   }
}
