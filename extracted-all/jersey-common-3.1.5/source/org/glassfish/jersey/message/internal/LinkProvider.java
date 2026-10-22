package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Link;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.Tokenizer;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class LinkProvider implements HeaderDelegateProvider<Link> {
   private static final Logger LOGGER = Logger.getLogger(LinkProvider.class.getName());

   @Override
   public boolean supports(Class<?> type) {
      return Link.class.isAssignableFrom(type);
   }

   public Link fromString(String value) throws IllegalArgumentException {
      return initBuilder(new JerseyLink.Builder(), value).build();
   }

   static JerseyLink.Builder initBuilder(JerseyLink.Builder lb, String value) {
      Utils.throwIllegalArgumentExceptionIfNull(value, LocalizationMessages.LINK_IS_NULL());

      try {
         value = value.trim();
         if (!value.startsWith("<")) {
            throw new IllegalArgumentException("Missing starting token < in " + value);
         }

         int gtIndex = value.indexOf(62);
         if (gtIndex == -1) {
            throw new IllegalArgumentException("Missing token > in " + value);
         }

         lb.uri(value.substring(1, gtIndex).trim());
         String params = value.substring(gtIndex + 1).trim();
         StringTokenizer var7 = new StringTokenizer(params, ";=\"", true);

         while (var7.hasMoreTokens()) {
            checkToken(var7, ";");
            String n = var7.nextToken().trim();
            checkToken(var7, "=");
            String v = nextNonEmptyToken(var7);
            if (v.equals("\"")) {
               v = var7.nextToken();
               checkToken(var7, "\"");
            }

            lb.param(n, v);
         }
      } catch (Throwable var6) {
         if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER, "Error parsing link value '" + value + "'", var6);
         }

         lb = null;
      }

      if (lb == null) {
         throw new IllegalArgumentException("Unable to parse link " + value);
      } else {
         return lb;
      }
   }

   private static String nextNonEmptyToken(StringTokenizer st) throws IllegalArgumentException {
      String token;
      do {
         token = st.nextToken().trim();
      } while (token.length() == 0);

      return token;
   }

   private static void checkToken(StringTokenizer st, String expected) throws IllegalArgumentException {
      String token;
      do {
         token = st.nextToken().trim();
      } while (token.length() == 0);

      if (!token.equals(expected)) {
         throw new IllegalArgumentException("Expected token " + expected + " but found " + token);
      }
   }

   public String toString(Link value) {
      return stringfy(value);
   }

   static String stringfy(Link value) {
      Utils.throwIllegalArgumentExceptionIfNull(value, LocalizationMessages.LINK_IS_NULL());
      Map<String, String> map = value.getParams();
      StringBuilder sb = new StringBuilder();
      sb.append('<').append(value.getUri()).append('>');

      for (Entry<String, String> entry : map.entrySet()) {
         sb.append("; ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
      }

      return sb.toString();
   }

   static List<String> getLinkRelations(String rel) {
      return rel == null ? null : Arrays.asList(Tokenizer.tokenize(rel, "\" "));
   }
}
