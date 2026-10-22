package okhttp3.internal.connection

import java.io.IOException
import java.io.InterruptedIOException
import java.net.ProtocolException
import java.net.UnknownServiceException
import java.security.cert.CertificateException
import java.util.Arrays
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocket
import okhttp3.ConnectionSpec

internal class ConnectionSpecSelector(connectionSpecs: List<ConnectionSpec>) {
   private final val connectionSpecs: List<ConnectionSpec>
   private final var isFallback: Boolean
   private final var isFallbackPossible: Boolean
   private final var nextModeIndex: Int

   init {
      this.connectionSpecs = connectionSpecs;
   }

   @Throws(java/io/IOException::class)
   public fun configureSecureSocket(sslSocket: SSLSocket): ConnectionSpec {
      var tlsConfiguration: ConnectionSpec = null;
      var i: Int = this.nextModeIndex;

      for (int var4 = this.connectionSpecs.size(); i < var4; i++) {
         var connectionSpec: ConnectionSpec = this.connectionSpecs.get(i);
         if (connectionSpec.isCompatible(sslSocket)) {
            tlsConfiguration = connectionSpec;
            this.nextModeIndex = i + 1;
            break;
         }
      }

      if (tlsConfiguration == null) {
         var var10002: StringBuilder = new StringBuilder()
            .append("Unable to find acceptable protocols. isFallback=")
            .append(this.isFallback)
            .append(", modes=")
            .append(this.connectionSpecs)
            .append(", supported protocols=");
         var var10003: Array<java.lang.String> = sslSocket.getEnabledProtocols();
         var var6: java.lang.String = Arrays.toString((Object[])var10003);
         throw new UnknownServiceException(var10002.append(var6).toString());
      } else {
         this.isFallbackPossible = this.isFallbackPossible(sslSocket);
         tlsConfiguration.apply$okhttp(sslSocket, this.isFallback);
         return tlsConfiguration;
      }
   }

   public fun connectionFailed(e: IOException): Boolean {
      this.isFallback = true;
      return this.isFallbackPossible
         && e !is ProtocolException
         && e !is InterruptedIOException
         && (e !is SSLHandshakeException || e.getCause() !is CertificateException)
         && e !is SSLPeerUnverifiedException
         && e is SSLException;
   }

   private fun isFallbackPossible(socket: SSLSocket): Boolean {
      var i: Int = this.nextModeIndex;

      for (int var3 = this.connectionSpecs.size(); i < var3; i++) {
         if (this.connectionSpecs.get(i).isCompatible(socket)) {
            return true;
         }
      }

      return false;
   }
}
