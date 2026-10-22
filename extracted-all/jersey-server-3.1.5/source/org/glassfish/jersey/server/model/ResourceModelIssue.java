package org.glassfish.jersey.server.model;

import org.glassfish.jersey.Severity;

public final class ResourceModelIssue {
   private final Object source;
   private final String message;
   private final Severity severity;

   public ResourceModelIssue(Object source, String message) {
      this(source, message, Severity.WARNING);
   }

   public ResourceModelIssue(Object source, String message, Severity severity) {
      this.source = source;
      this.message = message;
      this.severity = severity;
   }

   public String getMessage() {
      return this.message;
   }

   public Severity getSeverity() {
      return this.severity;
   }

   public Object getSource() {
      return this.source;
   }

   @Override
   public String toString() {
      return "[" + this.severity + "] " + this.message + "; source='" + this.source + '\'';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ResourceModelIssue that = (ResourceModelIssue)o;
         if (this.message != null ? this.message.equals(that.message) : that.message == null) {
            if (this.severity != that.severity) {
               return false;
            } else {
               return this.source != null ? this.source.equals(that.source) : that.source == null;
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
      int result = this.source != null ? this.source.hashCode() : 0;
      result = 31 * result + (this.message != null ? this.message.hashCode() : 0);
      return 31 * result + (this.severity != null ? this.severity.hashCode() : 0);
   }
}
