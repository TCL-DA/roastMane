package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import java.util.Locale;

public class LanguageTag {
   String tag;
   String primaryTag;
   String subTags;

   protected LanguageTag() {
   }

   public static LanguageTag valueOf(String s) throws IllegalArgumentException {
      LanguageTag lt = new LanguageTag();

      try {
         lt.parse(s);
         return lt;
      } catch (ParseException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public LanguageTag(String primaryTag, String subTags) {
      if (subTags != null && subTags.length() > 0) {
         this.tag = primaryTag + "-" + subTags;
      } else {
         this.tag = primaryTag;
      }

      this.primaryTag = primaryTag;
      this.subTags = subTags;
   }

   public LanguageTag(String header) throws ParseException {
      this(HttpHeaderReader.newInstance(header));
   }

   public LanguageTag(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      this.tag = reader.nextToken().toString();
      if (reader.hasNext()) {
         throw new ParseException("Invalid Language tag", reader.getIndex());
      } else {
         this.parse(this.tag);
      }
   }

   public final boolean isCompatible(Locale tag) {
      if (this.tag.equals("*")) {
         return true;
      } else {
         return this.subTags == null
            ? this.primaryTag.equalsIgnoreCase(tag.getLanguage())
            : this.primaryTag.equalsIgnoreCase(tag.getLanguage()) && this.subTags.equalsIgnoreCase(tag.getCountry());
      }
   }

   public final Locale getAsLocale() {
      return this.subTags == null ? new Locale(this.primaryTag) : new Locale(this.primaryTag, this.subTags);
   }

   protected final void parse(String languageTag) throws ParseException {
      if (!this.isValid(languageTag)) {
         throw new ParseException("String, " + languageTag + ", is not a valid language tag", 0);
      } else {
         int index = languageTag.indexOf(45);
         if (index == -1) {
            this.primaryTag = languageTag;
            this.subTags = null;
         } else {
            this.primaryTag = languageTag.substring(0, index);
            this.subTags = languageTag.substring(index + 1, languageTag.length());
         }
      }
   }

   private boolean isValid(String tag) {
      int alphanumCount = 0;
      int dash = 0;

      for (int i = 0; i < tag.length(); i++) {
         char c = tag.charAt(i);
         if (c == '-') {
            if (alphanumCount == 0) {
               return false;
            }

            alphanumCount = 0;
            dash++;
         } else {
            if (('A' > c || c > 'Z') && ('a' > c || c > 'z') && (dash <= 0 || '0' > c || c > '9')) {
               return false;
            }

            if (++alphanumCount > 8) {
               return false;
            }
         }
      }

      return alphanumCount != 0;
   }

   public final String getTag() {
      return this.tag;
   }

   public final String getPrimaryTag() {
      return this.primaryTag;
   }

   public final String getSubTags() {
      return this.subTags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof LanguageTag && o.getClass() == this.getClass()) {
         LanguageTag that = (LanguageTag)o;
         if (this.primaryTag != null ? this.primaryTag.equals(that.primaryTag) : that.primaryTag == null) {
            if (this.subTags != null ? this.subTags.equals(that.subTags) : that.subTags == null) {
               return this.tag != null ? this.tag.equals(that.tag) : that.tag == null;
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
      int result = this.tag != null ? this.tag.hashCode() : 0;
      result = 31 * result + (this.primaryTag != null ? this.primaryTag.hashCode() : 0);
      return 31 * result + (this.subTags != null ? this.subTags.hashCode() : 0);
   }

   @Override
   public String toString() {
      return this.primaryTag + (this.subTags == null ? "" : this.subTags);
   }
}
