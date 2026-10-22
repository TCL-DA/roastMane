package org.glassfish.jersey.message.internal;

import java.text.ParseException;

public class AcceptableToken extends Token implements Qualified {
   protected int quality = 1000;

   public AcceptableToken(String header) throws ParseException {
      this(HttpHeaderReader.newInstance(header));
   }

   public AcceptableToken(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      this.token = reader.nextToken().toString();
      if (reader.hasNext()) {
         this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
      }
   }

   @Override
   public int getQuality() {
      return this.quality;
   }
}
