package org.glassfish.jersey.message.internal;

import java.text.ParseException;

public class Token {
   protected String token;

   protected Token() {
   }

   public Token(String header) throws ParseException {
      this(HttpHeaderReader.newInstance(header));
   }

   public Token(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      this.token = reader.nextToken().toString();
      if (reader.hasNext()) {
         throw new ParseException("Invalid token", reader.getIndex());
      }
   }

   public String getToken() {
      return this.token;
   }

   public final boolean isCompatible(String token) {
      return this.token.equals("*") ? true : this.token.equals(token);
   }
}
