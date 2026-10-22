package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Comparator;
import javafx.beans.NamedArg;

public class RoastingComment implements Cloneable {
   private final long _time;
   private final String _event;
   private String _text;

   public RoastingComment(
      @JsonProperty("time") @NamedArg("time") long time,
      @JsonProperty("event") @NamedArg("event") String event,
      @JsonProperty("text") @NamedArg("text") String text
   ) {
      this._time = time;
      this._event = event;
      if (text != null && text.trim().length() > 0) {
         this._text = text.replaceAll("[\\n\\t]", " ");
      } else {
         this._text = null;
      }
   }

   public long getTime() {
      return this._time;
   }

   @JsonInclude(Include.NON_NULL)
   public String getEvent() {
      return this._event;
   }

   public String getText() {
      return this._text;
   }

   public void setText(String text) {
      this._text = text;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (int)(this._time ^ this._time >>> 32);
      result = 31 * result + (this._event == null ? 0 : this._event.hashCode());
      return 31 * result + (this._text == null ? 0 : this._text.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         RoastingComment other = (RoastingComment)obj;
         if (this._time != other._time) {
            return false;
         } else {
            if (this._event == null) {
               if (other._event != null) {
                  return false;
               }
            } else if (!this._event.equals(other._event)) {
               return false;
            }

            if (this._text == null) {
               if (other._text != null) {
                  return false;
               }
            } else if (!this._text.equals(other._text)) {
               return false;
            }

            return true;
         }
      }
   }

   @Override
   public String toString() {
      String s = "";
      if (this._event != null) {
         s = this._event;
      }

      if (this._text != null) {
         if (s.length() > 0) {
            s = s + " - ";
         }

         s = s + this._text;
      }

      return s;
   }

   public RoastingComment clone() {
      try {
         return (RoastingComment)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException("Should never happen", var2);
      }
   }

   public static class TimeComparator implements Comparator<RoastingComment> {
      public int compare(RoastingComment o1, RoastingComment o2) {
         return (int)(o1.getTime() - o2.getTime());
      }
   }
}
