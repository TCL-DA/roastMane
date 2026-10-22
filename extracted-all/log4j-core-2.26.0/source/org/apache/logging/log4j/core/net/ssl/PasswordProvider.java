package org.apache.logging.log4j.core.net.ssl;

@FunctionalInterface
public interface PasswordProvider {
   char[] getPassword();
}
