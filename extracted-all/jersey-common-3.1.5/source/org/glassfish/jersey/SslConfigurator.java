package org.glassfish.jersey;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.PropertiesHelper;

public final class SslConfigurator {
   public static final String TRUST_STORE_PROVIDER = "javax.net.ssl.trustStoreProvider";
   public static final String KEY_STORE_PROVIDER = "javax.net.ssl.keyStoreProvider";
   public static final String TRUST_STORE_FILE = "javax.net.ssl.trustStore";
   public static final String KEY_STORE_FILE = "javax.net.ssl.keyStore";
   public static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
   public static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
   public static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
   public static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
   public static final String KEY_MANAGER_FACTORY_ALGORITHM = "ssl.keyManagerFactory.algorithm";
   public static final String KEY_MANAGER_FACTORY_PROVIDER = "ssl.keyManagerFactory.provider";
   public static final String TRUST_MANAGER_FACTORY_ALGORITHM = "ssl.trustManagerFactory.algorithm";
   public static final String TRUST_MANAGER_FACTORY_PROVIDER = "ssl.trustManagerFactory.provider";
   private static final SslConfigurator DEFAULT_CONFIG_NO_PROPS = new SslConfigurator(false);
   private static final Logger LOGGER = Logger.getLogger(SslConfigurator.class.getName());
   private KeyStore keyStore;
   private KeyStore trustStore;
   private String trustStoreProvider;
   private String keyStoreProvider;
   private String trustStoreType;
   private String keyStoreType;
   private char[] trustStorePass;
   private char[] keyStorePass;
   private char[] keyPass;
   private String trustStoreFile;
   private String keyStoreFile;
   private byte[] trustStoreBytes;
   private byte[] keyStoreBytes;
   private String trustManagerFactoryAlgorithm;
   private String keyManagerFactoryAlgorithm;
   private String trustManagerFactoryProvider;
   private String keyManagerFactoryProvider;
   private String securityProtocol = "TLS";

   public static SSLContext getDefaultContext() {
      return getDefaultContext(true);
   }

   public static SSLContext getDefaultContext(boolean readSystemProperties) {
      return readSystemProperties ? new SslConfigurator(true).createSSLContext() : DEFAULT_CONFIG_NO_PROPS.createSSLContext();
   }

   public static SslConfigurator newInstance() {
      return new SslConfigurator(false);
   }

   public static SslConfigurator newInstance(boolean readSystemProperties) {
      return new SslConfigurator(readSystemProperties);
   }

   private SslConfigurator(boolean readSystemProperties) {
      if (readSystemProperties) {
         this.retrieve();
      }
   }

   private SslConfigurator(SslConfigurator that) {
      this.keyStore = that.keyStore;
      this.trustStore = that.trustStore;
      this.trustStoreProvider = that.trustStoreProvider;
      this.keyStoreProvider = that.keyStoreProvider;
      this.trustStoreType = that.trustStoreType;
      this.keyStoreType = that.keyStoreType;
      this.trustStorePass = that.trustStorePass;
      this.keyStorePass = that.keyStorePass;
      this.keyPass = that.keyPass;
      this.trustStoreFile = that.trustStoreFile;
      this.keyStoreFile = that.keyStoreFile;
      this.trustStoreBytes = that.trustStoreBytes;
      this.keyStoreBytes = that.keyStoreBytes;
      this.trustManagerFactoryAlgorithm = that.trustManagerFactoryAlgorithm;
      this.keyManagerFactoryAlgorithm = that.keyManagerFactoryAlgorithm;
      this.trustManagerFactoryProvider = that.trustManagerFactoryProvider;
      this.keyManagerFactoryProvider = that.keyManagerFactoryProvider;
      this.securityProtocol = that.securityProtocol;
   }

   public SslConfigurator copy() {
      return new SslConfigurator(this);
   }

   public SslConfigurator trustStoreProvider(String trustStoreProvider) {
      this.trustStoreProvider = trustStoreProvider;
      return this;
   }

   public SslConfigurator keyStoreProvider(String keyStoreProvider) {
      this.keyStoreProvider = keyStoreProvider;
      return this;
   }

   public SslConfigurator trustStoreType(String trustStoreType) {
      this.trustStoreType = trustStoreType;
      return this;
   }

   public SslConfigurator keyStoreType(String keyStoreType) {
      this.keyStoreType = keyStoreType;
      return this;
   }

   public SslConfigurator trustStorePassword(String password) {
      this.trustStorePass = password.toCharArray();
      return this;
   }

   public SslConfigurator keyStorePassword(String password) {
      this.keyStorePass = password.toCharArray();
      return this;
   }

   public SslConfigurator keyStorePassword(char[] password) {
      this.keyStorePass = (char[])password.clone();
      return this;
   }

   public SslConfigurator keyPassword(String password) {
      this.keyPass = password.toCharArray();
      return this;
   }

   public SslConfigurator keyPassword(char[] password) {
      this.keyPass = (char[])password.clone();
      return this;
   }

   public SslConfigurator trustStoreFile(String fileName) {
      this.trustStoreFile = fileName;
      this.trustStoreBytes = null;
      this.trustStore = null;
      return this;
   }

   public SslConfigurator trustStoreBytes(byte[] payload) {
      this.trustStoreBytes = (byte[])payload.clone();
      this.trustStoreFile = null;
      this.trustStore = null;
      return this;
   }

   public SslConfigurator keyStoreFile(String fileName) {
      this.keyStoreFile = fileName;
      this.keyStoreBytes = null;
      this.keyStore = null;
      return this;
   }

   public SslConfigurator keyStoreBytes(byte[] payload) {
      this.keyStoreBytes = (byte[])payload.clone();
      this.keyStoreFile = null;
      this.keyStore = null;
      return this;
   }

   public SslConfigurator trustManagerFactoryAlgorithm(String algorithm) {
      this.trustManagerFactoryAlgorithm = algorithm;
      return this;
   }

   public SslConfigurator keyManagerFactoryAlgorithm(String algorithm) {
      this.keyManagerFactoryAlgorithm = algorithm;
      return this;
   }

   public SslConfigurator trustManagerFactoryProvider(String provider) {
      this.trustManagerFactoryProvider = provider;
      return this;
   }

   public SslConfigurator keyManagerFactoryProvider(String provider) {
      this.keyManagerFactoryProvider = provider;
      return this;
   }

   public SslConfigurator securityProtocol(String protocol) {
      this.securityProtocol = protocol;
      return this;
   }

   KeyStore getKeyStore() {
      return this.keyStore;
   }

   public SslConfigurator keyStore(KeyStore keyStore) {
      this.keyStore = keyStore;
      this.keyStoreFile = null;
      this.keyStoreBytes = null;
      return this;
   }

   KeyStore getTrustStore() {
      return this.trustStore;
   }

   public SslConfigurator trustStore(KeyStore trustStore) {
      this.trustStore = trustStore;
      this.trustStoreFile = null;
      this.trustStoreBytes = null;
      return this;
   }

   public SSLContext createSSLContext() {
      TrustManagerFactory trustManagerFactory = null;
      KeyManagerFactory keyManagerFactory = null;
      KeyStore _keyStore = this.keyStore;
      if (_keyStore == null && (this.keyStoreBytes != null || this.keyStoreFile != null)) {
         try {
            if (this.keyStoreProvider != null) {
               _keyStore = KeyStore.getInstance(this.keyStoreType != null ? this.keyStoreType : KeyStore.getDefaultType(), this.keyStoreProvider);
            } else {
               _keyStore = KeyStore.getInstance(this.keyStoreType != null ? this.keyStoreType : KeyStore.getDefaultType());
            }

            InputStream keyStoreInputStream = null;

            try {
               if (this.keyStoreBytes != null) {
                  keyStoreInputStream = new ByteArrayInputStream(this.keyStoreBytes);
               } else if (!this.keyStoreFile.equals("NONE")) {
                  keyStoreInputStream = Files.newInputStream(new File(this.keyStoreFile).toPath());
               }

               _keyStore.load(keyStoreInputStream, this.keyStorePass);
            } finally {
               try {
                  if (keyStoreInputStream != null) {
                     keyStoreInputStream.close();
                  }
               } catch (IOException var64) {
               }
            }
         } catch (KeyStoreException var82) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_IMPL_NOT_FOUND(), var82);
         } catch (CertificateException var83) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_CERT_LOAD_ERROR(), var83);
         } catch (FileNotFoundException var84) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_FILE_NOT_FOUND(this.keyStoreFile), var84);
         } catch (IOException var85) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_LOAD_ERROR(this.keyStoreFile), var85);
         } catch (NoSuchProviderException var86) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_PROVIDERS_NOT_REGISTERED(), var86);
         } catch (NoSuchAlgorithmException var87) {
            throw new IllegalStateException(LocalizationMessages.SSL_KS_INTEGRITY_ALGORITHM_NOT_FOUND(), var87);
         }
      }

      if (_keyStore != null) {
         String kmfAlgorithm = this.keyManagerFactoryAlgorithm;
         if (kmfAlgorithm == null) {
            kmfAlgorithm = AccessController.doPrivileged(
               PropertiesHelper.getSystemProperty("ssl.keyManagerFactory.algorithm", KeyManagerFactory.getDefaultAlgorithm())
            );
         }

         try {
            if (this.keyManagerFactoryProvider != null) {
               keyManagerFactory = KeyManagerFactory.getInstance(kmfAlgorithm, this.keyManagerFactoryProvider);
            } else {
               keyManagerFactory = KeyManagerFactory.getInstance(kmfAlgorithm);
            }

            char[] password = this.keyPass != null ? this.keyPass : this.keyStorePass;
            if (password != null) {
               keyManagerFactory.init(_keyStore, password);
            } else {
               String ksName = this.keyStoreProvider != null
                  ? LocalizationMessages.SSL_KMF_NO_PASSWORD_FOR_PROVIDER_BASED_KS()
                  : (this.keyStoreBytes != null ? LocalizationMessages.SSL_KMF_NO_PASSWORD_FOR_BYTE_BASED_KS() : this.keyStoreFile);
               LOGGER.config(LocalizationMessages.SSL_KMF_NO_PASSWORD_SET(ksName));
               keyManagerFactory = null;
            }
         } catch (KeyStoreException var77) {
            throw new IllegalStateException(LocalizationMessages.SSL_KMF_INIT_FAILED(), var77);
         } catch (UnrecoverableKeyException var78) {
            throw new IllegalStateException(LocalizationMessages.SSL_KMF_UNRECOVERABLE_KEY(), var78);
         } catch (NoSuchAlgorithmException var79) {
            throw new IllegalStateException(LocalizationMessages.SSL_KMF_ALGORITHM_NOT_SUPPORTED(), var79);
         } catch (NoSuchProviderException var80) {
            throw new IllegalStateException(LocalizationMessages.SSL_KMF_PROVIDER_NOT_REGISTERED(), var80);
         }
      }

      KeyStore _trustStore = this.trustStore;
      if (_trustStore == null && (this.trustStoreBytes != null || this.trustStoreFile != null)) {
         try {
            if (this.trustStoreProvider != null) {
               _trustStore = KeyStore.getInstance(this.trustStoreType != null ? this.trustStoreType : KeyStore.getDefaultType(), this.trustStoreProvider);
            } else {
               _trustStore = KeyStore.getInstance(this.trustStoreType != null ? this.trustStoreType : KeyStore.getDefaultType());
            }

            InputStream trustStoreInputStream = null;

            try {
               if (this.trustStoreBytes != null) {
                  trustStoreInputStream = new ByteArrayInputStream(this.trustStoreBytes);
               } else if (!this.trustStoreFile.equals("NONE")) {
                  trustStoreInputStream = Files.newInputStream(new File(this.trustStoreFile).toPath());
               }

               _trustStore.load(trustStoreInputStream, this.trustStorePass);
            } finally {
               try {
                  if (trustStoreInputStream != null) {
                     trustStoreInputStream.close();
                  }
               } catch (IOException var63) {
               }
            }
         } catch (KeyStoreException var71) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_IMPL_NOT_FOUND(), var71);
         } catch (CertificateException var72) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_CERT_LOAD_ERROR(), var72);
         } catch (FileNotFoundException var73) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_FILE_NOT_FOUND(this.trustStoreFile), var73);
         } catch (IOException var74) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_LOAD_ERROR(this.trustStoreFile), var74);
         } catch (NoSuchProviderException var75) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_PROVIDERS_NOT_REGISTERED(), var75);
         } catch (NoSuchAlgorithmException var76) {
            throw new IllegalStateException(LocalizationMessages.SSL_TS_INTEGRITY_ALGORITHM_NOT_FOUND(), var76);
         }
      }

      if (_trustStore != null) {
         String tmfAlgorithm = this.trustManagerFactoryAlgorithm;
         if (tmfAlgorithm == null) {
            tmfAlgorithm = AccessController.doPrivileged(
               PropertiesHelper.getSystemProperty("ssl.trustManagerFactory.algorithm", TrustManagerFactory.getDefaultAlgorithm())
            );
         }

         try {
            if (this.trustManagerFactoryProvider != null) {
               trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm, this.trustManagerFactoryProvider);
            } else {
               trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            }

            trustManagerFactory.init(_trustStore);
         } catch (KeyStoreException var67) {
            throw new IllegalStateException(LocalizationMessages.SSL_TMF_INIT_FAILED(), var67);
         } catch (NoSuchAlgorithmException var68) {
            throw new IllegalStateException(LocalizationMessages.SSL_TMF_ALGORITHM_NOT_SUPPORTED(), var68);
         } catch (NoSuchProviderException var69) {
            throw new IllegalStateException(LocalizationMessages.SSL_TMF_PROVIDER_NOT_REGISTERED(), var69);
         }
      }

      try {
         String secProtocol = "TLS";
         if (this.securityProtocol != null) {
            secProtocol = this.securityProtocol;
         }

         SSLContext sslContext = SSLContext.getInstance(secProtocol);
         sslContext.init(
            keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null,
            trustManagerFactory != null ? trustManagerFactory.getTrustManagers() : null,
            null
         );
         return sslContext;
      } catch (KeyManagementException var65) {
         throw new IllegalStateException(LocalizationMessages.SSL_CTX_INIT_FAILED(), var65);
      } catch (NoSuchAlgorithmException var66) {
         throw new IllegalStateException(LocalizationMessages.SSL_CTX_ALGORITHM_NOT_SUPPORTED(), var66);
      }
   }

   public SslConfigurator retrieve(Properties props) {
      this.trustStoreProvider = props.getProperty("javax.net.ssl.trustStoreProvider");
      this.keyStoreProvider = props.getProperty("javax.net.ssl.keyStoreProvider");
      this.trustManagerFactoryProvider = props.getProperty("ssl.trustManagerFactory.provider");
      this.keyManagerFactoryProvider = props.getProperty("ssl.keyManagerFactory.provider");
      this.trustStoreType = props.getProperty("javax.net.ssl.trustStoreType");
      this.keyStoreType = props.getProperty("javax.net.ssl.keyStoreType");
      if (props.getProperty("javax.net.ssl.trustStorePassword") != null) {
         this.trustStorePass = props.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
      } else {
         this.trustStorePass = null;
      }

      if (props.getProperty("javax.net.ssl.keyStorePassword") != null) {
         this.keyStorePass = props.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
      } else {
         this.keyStorePass = null;
      }

      this.trustStoreFile = props.getProperty("javax.net.ssl.trustStore");
      this.keyStoreFile = props.getProperty("javax.net.ssl.keyStore");
      this.trustStoreBytes = null;
      this.keyStoreBytes = null;
      this.trustStore = null;
      this.keyStore = null;
      this.securityProtocol = "TLS";
      return this;
   }

   public SslConfigurator retrieve() {
      this.trustStoreProvider = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.trustStoreProvider"));
      this.keyStoreProvider = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.keyStoreProvider"));
      this.trustManagerFactoryProvider = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("ssl.trustManagerFactory.provider"));
      this.keyManagerFactoryProvider = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("ssl.keyManagerFactory.provider"));
      this.trustStoreType = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.trustStoreType"));
      this.keyStoreType = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.keyStoreType"));
      String trustStorePassword = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.trustStorePassword"));
      if (trustStorePassword != null) {
         this.trustStorePass = trustStorePassword.toCharArray();
      } else {
         this.trustStorePass = null;
      }

      String keyStorePassword = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.keyStorePassword"));
      if (keyStorePassword != null) {
         this.keyStorePass = keyStorePassword.toCharArray();
      } else {
         this.keyStorePass = null;
      }

      this.trustStoreFile = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.trustStore"));
      this.keyStoreFile = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("javax.net.ssl.keyStore"));
      this.trustStoreBytes = null;
      this.keyStoreBytes = null;
      this.trustStore = null;
      this.keyStore = null;
      this.securityProtocol = "TLS";
      return this;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SslConfigurator that = (SslConfigurator)o;
         if (this.keyManagerFactoryAlgorithm != null
            ? this.keyManagerFactoryAlgorithm.equals(that.keyManagerFactoryAlgorithm)
            : that.keyManagerFactoryAlgorithm == null) {
            if (this.keyManagerFactoryProvider != null
               ? this.keyManagerFactoryProvider.equals(that.keyManagerFactoryProvider)
               : that.keyManagerFactoryProvider == null) {
               if (!Arrays.equals(this.keyPass, that.keyPass)) {
                  return false;
               } else if (this.keyStore != null ? this.keyStore.equals(that.keyStore) : that.keyStore == null) {
                  if (!Arrays.equals(this.keyStoreBytes, that.keyStoreBytes)) {
                     return false;
                  } else if (this.keyStoreFile != null ? this.keyStoreFile.equals(that.keyStoreFile) : that.keyStoreFile == null) {
                     if (!Arrays.equals(this.keyStorePass, that.keyStorePass)) {
                        return false;
                     } else if (this.keyStoreProvider != null ? this.keyStoreProvider.equals(that.keyStoreProvider) : that.keyStoreProvider == null) {
                        if (this.keyStoreType != null ? this.keyStoreType.equals(that.keyStoreType) : that.keyStoreType == null) {
                           if (this.securityProtocol != null ? this.securityProtocol.equals(that.securityProtocol) : that.securityProtocol == null) {
                              if (this.trustManagerFactoryAlgorithm != null
                                 ? this.trustManagerFactoryAlgorithm.equals(that.trustManagerFactoryAlgorithm)
                                 : that.trustManagerFactoryAlgorithm == null) {
                                 if (this.trustManagerFactoryProvider != null
                                    ? this.trustManagerFactoryProvider.equals(that.trustManagerFactoryProvider)
                                    : that.trustManagerFactoryProvider == null) {
                                    if (this.trustStore != null ? this.trustStore.equals(that.trustStore) : that.trustStore == null) {
                                       if (!Arrays.equals(this.trustStoreBytes, that.trustStoreBytes)) {
                                          return false;
                                       } else if (this.trustStoreFile != null ? this.trustStoreFile.equals(that.trustStoreFile) : that.trustStoreFile == null) {
                                          if (!Arrays.equals(this.trustStorePass, that.trustStorePass)) {
                                             return false;
                                          } else if (this.trustStoreProvider != null
                                             ? this.trustStoreProvider.equals(that.trustStoreProvider)
                                             : that.trustStoreProvider == null) {
                                             return this.trustStoreType != null ? this.trustStoreType.equals(that.trustStoreType) : that.trustStoreType == null;
                                          } else {
                                             return false;
                                          }
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.keyStore != null ? this.keyStore.hashCode() : 0;
      result = 31 * result + (this.trustStore != null ? this.trustStore.hashCode() : 0);
      result = 31 * result + (this.trustStoreProvider != null ? this.trustStoreProvider.hashCode() : 0);
      result = 31 * result + (this.keyStoreProvider != null ? this.keyStoreProvider.hashCode() : 0);
      result = 31 * result + (this.trustStoreType != null ? this.trustStoreType.hashCode() : 0);
      result = 31 * result + (this.keyStoreType != null ? this.keyStoreType.hashCode() : 0);
      result = 31 * result + (this.trustStorePass != null ? Arrays.hashCode(this.trustStorePass) : 0);
      result = 31 * result + (this.keyStorePass != null ? Arrays.hashCode(this.keyStorePass) : 0);
      result = 31 * result + (this.keyPass != null ? Arrays.hashCode(this.keyPass) : 0);
      result = 31 * result + (this.trustStoreFile != null ? this.trustStoreFile.hashCode() : 0);
      result = 31 * result + (this.keyStoreFile != null ? this.keyStoreFile.hashCode() : 0);
      result = 31 * result + (this.trustStoreBytes != null ? Arrays.hashCode(this.trustStoreBytes) : 0);
      result = 31 * result + (this.keyStoreBytes != null ? Arrays.hashCode(this.keyStoreBytes) : 0);
      result = 31 * result + (this.trustManagerFactoryAlgorithm != null ? this.trustManagerFactoryAlgorithm.hashCode() : 0);
      result = 31 * result + (this.keyManagerFactoryAlgorithm != null ? this.keyManagerFactoryAlgorithm.hashCode() : 0);
      result = 31 * result + (this.trustManagerFactoryProvider != null ? this.trustManagerFactoryProvider.hashCode() : 0);
      result = 31 * result + (this.keyManagerFactoryProvider != null ? this.keyManagerFactoryProvider.hashCode() : 0);
      return 31 * result + (this.securityProtocol != null ? this.securityProtocol.hashCode() : 0);
   }
}
