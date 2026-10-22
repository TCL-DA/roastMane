package org.eclipse.jetty.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5 {
   public static final byte VERSION = 5;
   public static final byte COMMAND_CONNECT = 1;
   public static final byte RESERVED = 0;
   public static final byte ADDRESS_TYPE_IPV4 = 1;
   public static final byte ADDRESS_TYPE_DOMAIN = 3;
   public static final byte ADDRESS_TYPE_IPV6 = 4;

   private Socks5() {
   }

   public interface Authentication {
      void authenticate(EndPoint var1, Callback var2);

      public interface Factory {
         byte getMethod();

         Socks5.Authentication newAuthentication();
      }
   }

   public static class NoAuthenticationFactory implements Socks5.Authentication.Factory {
      public static final byte METHOD = 0;

      @Override
      public byte getMethod() {
         return 0;
      }

      @Override
      public Socks5.Authentication newAuthentication() {
         return (endPoint, callback) -> callback.succeeded();
      }
   }

   public static class UsernamePasswordAuthenticationFactory implements Socks5.Authentication.Factory {
      public static final byte METHOD = 2;
      public static final byte VERSION = 1;
      private static final Logger LOG = LoggerFactory.getLogger(Socks5.UsernamePasswordAuthenticationFactory.class);
      private final String userName;
      private final String password;
      private final Charset charset;

      public UsernamePasswordAuthenticationFactory(String userName, String password) {
         this(userName, password, StandardCharsets.US_ASCII);
      }

      public UsernamePasswordAuthenticationFactory(String userName, String password, Charset charset) {
         this.userName = Objects.requireNonNull(userName);
         this.password = Objects.requireNonNull(password);
         this.charset = Objects.requireNonNull(charset);
      }

      @Override
      public byte getMethod() {
         return 2;
      }

      @Override
      public Socks5.Authentication newAuthentication() {
         return new Socks5.UsernamePasswordAuthenticationFactory.UsernamePasswordAuthentication(this);
      }

      private static class UsernamePasswordAuthentication implements Socks5.Authentication, Callback {
         private final ByteBuffer byteBuffer = BufferUtil.allocate(2);
         private final Socks5.UsernamePasswordAuthenticationFactory factory;
         private EndPoint endPoint;
         private Callback callback;

         private UsernamePasswordAuthentication(Socks5.UsernamePasswordAuthenticationFactory factory) {
            this.factory = factory;
         }

         @Override
         public void authenticate(EndPoint endPoint, Callback callback) {
            this.endPoint = endPoint;
            this.callback = callback;
            byte[] userNameBytes = this.factory.userName.getBytes(this.factory.charset);
            byte[] passwordBytes = this.factory.password.getBytes(this.factory.charset);
            ByteBuffer byteBuffer = ByteBuffer.allocate(3 + userNameBytes.length + passwordBytes.length)
               .put((byte)1)
               .put((byte)userNameBytes.length)
               .put(userNameBytes)
               .put((byte)passwordBytes.length)
               .put(passwordBytes)
               .flip();
            endPoint.write(Callback.from(this::authenticationSent, this::failed), new ByteBuffer[]{byteBuffer});
         }

         private void authenticationSent() {
            if (Socks5.UsernamePasswordAuthenticationFactory.LOG.isDebugEnabled()) {
               Socks5.UsernamePasswordAuthenticationFactory.LOG.debug("Written SOCKS5 username/password authentication request");
            }

            this.endPoint.fillInterested(this);
         }

         public void succeeded() {
            try {
               int filled = this.endPoint.fill(this.byteBuffer);
               if (filled < 0) {
                  throw new ClosedChannelException();
               }

               if (this.byteBuffer.remaining() < 2) {
                  this.endPoint.fillInterested(this);
                  return;
               }

               if (Socks5.UsernamePasswordAuthenticationFactory.LOG.isDebugEnabled()) {
                  Socks5.UsernamePasswordAuthenticationFactory.LOG.debug("Received SOCKS5 username/password authentication response");
               }

               byte version = this.byteBuffer.get();
               if (version != 1) {
                  throw new IOException("Unsupported username/password authentication version: " + version);
               }

               byte status = this.byteBuffer.get();
               if (status != 0) {
                  throw new IOException("SOCK5 username/password authentication failure");
               }

               if (Socks5.UsernamePasswordAuthenticationFactory.LOG.isDebugEnabled()) {
                  Socks5.UsernamePasswordAuthenticationFactory.LOG.debug("SOCKS5 username/password authentication succeeded");
               }

               this.callback.succeeded();
            } catch (Throwable var4) {
               this.failed(var4);
            }
         }

         public void failed(Throwable x) {
            this.callback.failed(x);
         }

         public InvocationType getInvocationType() {
            return InvocationType.NON_BLOCKING;
         }
      }
   }
}
