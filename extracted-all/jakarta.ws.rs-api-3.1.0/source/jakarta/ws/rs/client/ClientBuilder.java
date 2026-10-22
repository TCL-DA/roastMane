package jakarta.ws.rs.client;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import java.net.URL;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public abstract class ClientBuilder implements Configurable<ClientBuilder> {
   public static final String JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY = "jakarta.ws.rs.client.ClientBuilder";

   protected ClientBuilder() {
   }

   public static ClientBuilder newBuilder() {
      try {
         Object delegate = FactoryFinder.find("jakarta.ws.rs.client.ClientBuilder", ClientBuilder.class);
         if (!(delegate instanceof ClientBuilder)) {
            ClientBuilder.CreateErrorMessageAction action = new ClientBuilder.CreateErrorMessageAction(delegate);
            String errorMessage;
            if (System.getSecurityManager() == null) {
               errorMessage = action.run();
            } else {
               errorMessage = AccessController.doPrivileged(action);
            }

            throw new LinkageError(errorMessage);
         } else {
            return (ClientBuilder)delegate;
         }
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static Client newClient() {
      return newBuilder().build();
   }

   public static Client newClient(Configuration configuration) {
      return newBuilder().withConfig(configuration).build();
   }

   public abstract ClientBuilder withConfig(Configuration var1);

   public abstract ClientBuilder sslContext(SSLContext var1);

   public abstract ClientBuilder keyStore(KeyStore var1, char[] var2);

   public ClientBuilder keyStore(KeyStore keyStore, String password) {
      return this.keyStore(keyStore, password.toCharArray());
   }

   public abstract ClientBuilder trustStore(KeyStore var1);

   public abstract ClientBuilder hostnameVerifier(HostnameVerifier var1);

   public abstract ClientBuilder executorService(ExecutorService var1);

   public abstract ClientBuilder scheduledExecutorService(ScheduledExecutorService var1);

   public abstract ClientBuilder connectTimeout(long var1, TimeUnit var3);

   public abstract ClientBuilder readTimeout(long var1, TimeUnit var3);

   public abstract Client build();

   private static final class CreateErrorMessageAction implements PrivilegedAction<String> {
      private final Object delegate;

      private CreateErrorMessageAction(Object delegate) {
         this.delegate = delegate;
      }

      public String run() {
         Class<?> pClass = ClientBuilder.class;
         String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
         ClassLoader loader = pClass.getClassLoader();
         if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
         }

         URL targetTypeURL = loader.getResource(classnameAsResource);
         return "ClassCastException: attempting to cast" + this.delegate.getClass().getClassLoader().getResource(classnameAsResource) + " to " + targetTypeURL;
      }
   }
}
