package org.eclipse.jetty.util.ssl;

import java.net.InetAddress;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X509 {
   private static final Logger LOG = LoggerFactory.getLogger(X509.class);
   private static final int KEY_USAGE__KEY_CERT_SIGN = 5;
   private static final int SUBJECT_ALTERNATIVE_NAMES__DNS_NAME = 2;
   private static final int SUBJECT_ALTERNATIVE_NAMES__IP_ADDRESS = 7;
   private static final String IPV4 = "([0-9]{1,3})(\\.[0-9]{1,3}){3}";
   private static final Pattern IPV4_REGEXP = Pattern.compile("^([0-9]{1,3})(\\.[0-9]{1,3}){3}$");
   private static final Pattern IPV6_REGEXP = Pattern.compile("(?=.*:.*:)^([0-9a-fA-F:\\[\\]]+)(:([0-9]{1,3})(\\.[0-9]{1,3}){3})?$");
   private final X509Certificate _x509;
   private final String _alias;
   private final Set<String> _hosts = new LinkedHashSet<>();
   private final Set<String> _wilds = new LinkedHashSet<>();
   private final Set<InetAddress> _addresses = new LinkedHashSet<>();

   public static boolean isCertSign(X509Certificate x509) {
      boolean[] keyUsage = x509.getKeyUsage();
      return keyUsage != null && keyUsage.length > 5 ? keyUsage[5] : false;
   }

   public X509(String alias, X509Certificate x509) {
      this._alias = alias;
      this._x509 = x509;

      try {
         Collection<List<?>> altNames = x509.getSubjectAlternativeNames();
         if (altNames != null) {
            for (List<?> list : altNames) {
               int nameType = ((Number)list.get(0)).intValue();
               switch (nameType) {
                  case 2:
                     String name = list.get(1).toString();
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Certificate alias={} SAN dns={} in {}", new Object[]{alias, name, this});
                     }

                     this.addName(name);
                     break;
                  case 7:
                     String address = list.get(1).toString();
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Certificate alias={} SAN ip={} in {}", new Object[]{alias, address, this});
                     }

                     this.addAddress(address);
               }
            }
         }

         LdapName name = new LdapName(x509.getSubjectX500Principal().getName("RFC2253"));

         for (Rdn rdn : name.getRdns()) {
            if (rdn.getType().equalsIgnoreCase("CN")) {
               String cn = rdn.getValue().toString();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Certificate CN alias={} CN={} in {}", new Object[]{alias, cn, this});
               }

               this.addName(cn);
            }
         }
      } catch (Exception var8) {
         throw new IllegalArgumentException(var8);
      }
   }

   protected void addName(String cn) {
      if (cn != null) {
         cn = StringUtil.asciiToLowerCase(cn);
         if (cn.startsWith("*.")) {
            this._wilds.add(cn.substring(2));
         } else {
            this._hosts.add(cn);
         }
      }
   }

   private void addAddress(String host) {
      InetAddress address = this.toInetAddress(host);
      if (address != null) {
         this._addresses.add(address);
      }
   }

   private InetAddress toInetAddress(String address) {
      try {
         return InetAddress.getByName(address);
      } catch (Throwable var3) {
         LOG.trace("IGNORED", var3);
         return null;
      }
   }

   public String getAlias() {
      return this._alias;
   }

   public X509Certificate getCertificate() {
      return this._x509;
   }

   public Set<String> getHosts() {
      return Collections.unmodifiableSet(this._hosts);
   }

   public Set<String> getWilds() {
      return Collections.unmodifiableSet(this._wilds);
   }

   public boolean matches(String host) {
      host = StringUtil.asciiToLowerCase(host);
      if (!this._hosts.contains(host) && !this._wilds.contains(host)) {
         int dot = host.indexOf(46);
         if (dot >= 0) {
            String domain = host.substring(dot + 1);
            if (this._wilds.contains(domain)) {
               return true;
            }
         }

         if (seemsIPAddress(host)) {
            InetAddress address = this.toInetAddress(host);
            if (address != null) {
               return this._addresses.contains(address);
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean seemsIPAddress(String host) {
      return IPV4_REGEXP.matcher(host).matches() || IPV6_REGEXP.matcher(host).matches();
   }

   @Override
   public String toString() {
      return String.format("%s@%x(%s,h=%s,a=%s,w=%s)", this.getClass().getSimpleName(), this.hashCode(), this._alias, this._hosts, this._addresses, this._wilds);
   }
}
