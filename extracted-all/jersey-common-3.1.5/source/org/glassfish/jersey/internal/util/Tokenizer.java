package org.glassfish.jersey.internal.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public final class Tokenizer {
   public static final String COMMON_DELIMITERS = " ,;\n";

   private Tokenizer() {
   }

   public static String[] tokenize(String[] entries) {
      return tokenize(entries, " ,;\n");
   }

   public static String[] tokenize(String[] entries, String delimiters) {
      List<String> tokens = new LinkedList<>();

      for (String entry : entries) {
         if (entry != null && !entry.isEmpty()) {
            entry = entry.trim();
            if (!entry.isEmpty()) {
               tokenize(entry, delimiters, tokens);
            }
         }
      }

      return tokens.toArray(new String[tokens.size()]);
   }

   public static String[] tokenize(String entry) {
      return tokenize(entry, " ,;\n");
   }

   public static String[] tokenize(String entry, String delimiters) {
      Collection<String> tokens = tokenize(entry, delimiters, new LinkedList<>());
      return tokens.toArray(new String[tokens.size()]);
   }

   private static Collection<String> tokenize(String entry, String delimiters, Collection<String> tokens) {
      StringBuilder regexpBuilder = new StringBuilder(delimiters.length() * 3);
      regexpBuilder.append('[');

      for (char c : delimiters.toCharArray()) {
         regexpBuilder.append(Pattern.quote(String.valueOf(c)));
      }

      regexpBuilder.append(']');
      String[] tokenArray = entry.split(regexpBuilder.toString());

      for (String token : tokenArray) {
         if (token != null && !token.isEmpty()) {
            token = token.trim();
            if (!token.isEmpty()) {
               tokens.add(token);
            }
         }
      }

      return tokens;
   }
}
