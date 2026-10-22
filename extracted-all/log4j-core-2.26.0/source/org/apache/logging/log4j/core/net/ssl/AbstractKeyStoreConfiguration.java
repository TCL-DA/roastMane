package org.apache.logging.log4j.core.net.ssl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.util.NetUtils;

public class AbstractKeyStoreConfiguration extends StoreConfiguration<KeyStore> {
   private final String keyStoreType;
   private final transient KeyStore keyStore;

   public AbstractKeyStoreConfiguration(final String location, final PasswordProvider passwordProvider, final String keyStoreType) throws StoreConfigurationException {
      super(location, passwordProvider);
      this.keyStoreType = keyStoreType == null ? SslConfigurationDefaults.KEYSTORE_TYPE : keyStoreType;
      this.keyStore = this.load();
   }

   @Deprecated
   public AbstractKeyStoreConfiguration(final String location, final char[] password, final String keyStoreType) throws StoreConfigurationException {
      this(location, new MemoryPasswordProvider(password), keyStoreType);
   }

   @Deprecated
   public AbstractKeyStoreConfiguration(final String location, final String password, final String keyStoreType) throws StoreConfigurationException {
      this(location, new MemoryPasswordProvider(password == null ? null : password.toCharArray()), keyStoreType);
   }

   protected KeyStore load() throws StoreConfigurationException {
      String loadLocation = this.getLocation();
      char[] password = this.getPasswordAsCharArray();
      LOGGER.debug("Loading keystore from location {}", loadLocation);

      KeyStore fin;
      try {
         KeyStore ks = KeyStore.getInstance(this.keyStoreType);
         if (loadLocation != null) {
            InputStream finx = openInputStream(loadLocation);

            KeyStore var5;
            try {
               ks.load(finx, password);
               LOGGER.debug("KeyStore successfully loaded from location {}", loadLocation);
               var5 = ks;
            } catch (Throwable var17) {
               if (finx != null) {
                  try {
                     finx.close();
                  } catch (Throwable var16) {
                     var17.addSuppressed(var16);
                  }
               }

               throw var17;
            }

            if (finx != null) {
               finx.close();
            }

            return var5;
         }

         if (this.keyStoreType.equalsIgnoreCase("JKS") || this.keyStoreType.equalsIgnoreCase("PKCS12")) {
            throw new IOException("The location is null");
         }

         ks.load(null, password);
         LOGGER.debug("KeyStore successfully loaded");
         fin = ks;
      } catch (CertificateException var18) {
         LOGGER.error("No Provider supports a KeyStoreSpi implementation for the specified type {} for location {}", this.keyStoreType, loadLocation, var18);
         throw new StoreConfigurationException(loadLocation, var18);
      } catch (NoSuchAlgorithmException var19) {
         LOGGER.error("The algorithm used to check the integrity of the keystore cannot be found for location {}", loadLocation, var19);
         throw new StoreConfigurationException(loadLocation, var19);
      } catch (KeyStoreException var20) {
         LOGGER.error("KeyStoreException for location {}", loadLocation, var20);
         throw new StoreConfigurationException(loadLocation, var20);
      } catch (FileNotFoundException var21) {
         LOGGER.error("The keystore file {} is not found", loadLocation, var21);
         throw new StoreConfigurationException(loadLocation, var21);
      } catch (IOException var22) {
         LOGGER.error("Something is wrong with the format of the keystore or the given password for location {}", loadLocation, var22);
         throw new StoreConfigurationException(loadLocation, var22);
      } finally {
         if (password != null) {
            Arrays.fill(password, '\u0000');
         }
      }

      return fin;
   }

   private static InputStream openInputStream(final String filePathOrUri) {
      return ConfigurationSource.fromUri(NetUtils.toURI(filePathOrUri)).getInputStream();
   }

   public KeyStore getKeyStore() {
      return this.keyStore;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = super.hashCode();
      return 31 * result + (this.keyStoreType == null ? 0 : this.keyStoreType.hashCode());
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AbstractKeyStoreConfiguration other = (AbstractKeyStoreConfiguration)obj;
         return Objects.equals(this.keyStoreType, other.keyStoreType);
      }
   }

   public String getKeyStoreType() {
      return this.keyStoreType;
   }
}
