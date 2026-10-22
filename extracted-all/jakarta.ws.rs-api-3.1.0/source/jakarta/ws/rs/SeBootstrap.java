package jakarta.ws.rs;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.net.ssl.SSLContext;

public interface SeBootstrap {
   static CompletionStage<SeBootstrap.Instance> start(Application application, SeBootstrap.Configuration configuration) {
      return RuntimeDelegate.getInstance().bootstrap(application, configuration);
   }

   static CompletionStage<SeBootstrap.Instance> start(Application application) {
      SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().build();
      return start(application, configuration);
   }

   static CompletionStage<SeBootstrap.Instance> start(Class<? extends Application> clazz, SeBootstrap.Configuration configuration) {
      return RuntimeDelegate.getInstance().bootstrap(clazz, configuration);
   }

   static CompletionStage<SeBootstrap.Instance> start(Class<? extends Application> clazz) {
      SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().build();
      return start(clazz, configuration);
   }

   public interface Configuration {
      String PROTOCOL = "jakarta.ws.rs.SeBootstrap.Protocol";
      String HOST = "jakarta.ws.rs.SeBootstrap.Host";
      String PORT = "jakarta.ws.rs.SeBootstrap.Port";
      String ROOT_PATH = "jakarta.ws.rs.SeBootstrap.RootPath";
      String SSL_CONTEXT = "jakarta.ws.rs.SeBootstrap.SSLContext";
      String SSL_CLIENT_AUTHENTICATION = "jakarta.ws.rs.SeBootstrap.SSLClientAuthentication";
      int FREE_PORT = 0;
      int DEFAULT_PORT = -1;

      Object property(String var1);

      default boolean hasProperty(String name) {
         return this.property(name) != null;
      }

      default String protocol() {
         return (String)this.property("jakarta.ws.rs.SeBootstrap.Protocol");
      }

      default String host() {
         return (String)this.property("jakarta.ws.rs.SeBootstrap.Host");
      }

      default int port() {
         return (Integer)this.property("jakarta.ws.rs.SeBootstrap.Port");
      }

      default String rootPath() {
         return (String)this.property("jakarta.ws.rs.SeBootstrap.RootPath");
      }

      default SSLContext sslContext() {
         return (SSLContext)this.property("jakarta.ws.rs.SeBootstrap.SSLContext");
      }

      default SeBootstrap.Configuration.SSLClientAuthentication sslClientAuthentication() {
         return (SeBootstrap.Configuration.SSLClientAuthentication)this.property("jakarta.ws.rs.SeBootstrap.SSLClientAuthentication");
      }

      default UriBuilder baseUriBuilder() {
         return UriBuilder.newInstance().scheme(this.protocol().toLowerCase()).host(this.host()).port(this.port()).path(this.rootPath());
      }

      default URI baseUri() {
         return this.baseUriBuilder().build();
      }

      static SeBootstrap.Configuration.Builder builder() {
         return RuntimeDelegate.getInstance().createConfigurationBuilder();
      }

      public interface Builder {
         SeBootstrap.Configuration build();

         SeBootstrap.Configuration.Builder property(String var1, Object var2);

         default SeBootstrap.Configuration.Builder protocol(String protocol) {
            return this.property("jakarta.ws.rs.SeBootstrap.Protocol", protocol);
         }

         default SeBootstrap.Configuration.Builder host(String host) {
            return this.property("jakarta.ws.rs.SeBootstrap.Host", host);
         }

         default SeBootstrap.Configuration.Builder port(Integer port) {
            return this.property("jakarta.ws.rs.SeBootstrap.Port", port);
         }

         default SeBootstrap.Configuration.Builder rootPath(String rootPath) {
            return this.property("jakarta.ws.rs.SeBootstrap.RootPath", rootPath);
         }

         default SeBootstrap.Configuration.Builder sslContext(SSLContext sslContext) {
            return this.property("jakarta.ws.rs.SeBootstrap.SSLContext", sslContext);
         }

         default SeBootstrap.Configuration.Builder sslClientAuthentication(SeBootstrap.Configuration.SSLClientAuthentication sslClientAuthentication) {
            return this.property("jakarta.ws.rs.SeBootstrap.SSLClientAuthentication", sslClientAuthentication);
         }

         <T> SeBootstrap.Configuration.Builder from(BiFunction<String, Class<T>, Optional<T>> var1);

         default SeBootstrap.Configuration.Builder from(Object externalConfig) {
            return this;
         }
      }

      public static enum SSLClientAuthentication {
         NONE,
         OPTIONAL,
         MANDATORY;
      }
   }

   public interface Instance {
      SeBootstrap.Configuration configuration();

      CompletionStage<SeBootstrap.Instance.StopResult> stop();

      <T> T unwrap(Class<T> var1);

      default void stopOnShutdown(Consumer<SeBootstrap.Instance.StopResult> consumer) {
         Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop().thenAccept(consumer)));
      }

      public interface StopResult {
         <T> T unwrap(Class<T> var1);
      }
   }
}
