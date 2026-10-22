package com.segment.analytics.messages;

import java.util.Date;
import java.util.List;
import java.util.Map;

final class AutoValue_Batch extends Batch {
   private final List<Message> batch;
   private final Date sentAt;
   private final Map<String, ?> context;
   private final int sequence;
   private final String writeKey;

   AutoValue_Batch(List<Message> batch, Date sentAt, Map<String, ?> context, int sequence, String writeKey) {
      if (batch == null) {
         throw new NullPointerException("Null batch");
      } else {
         this.batch = batch;
         if (sentAt == null) {
            throw new NullPointerException("Null sentAt");
         } else {
            this.sentAt = sentAt;
            if (context == null) {
               throw new NullPointerException("Null context");
            } else {
               this.context = context;
               this.sequence = sequence;
               if (writeKey == null) {
                  throw new NullPointerException("Null writeKey");
               } else {
                  this.writeKey = writeKey;
               }
            }
         }
      }
   }

   @Override
   public List<Message> batch() {
      return this.batch;
   }

   @Override
   public Date sentAt() {
      return this.sentAt;
   }

   @Override
   public Map<String, ?> context() {
      return this.context;
   }

   @Override
   public int sequence() {
      return this.sequence;
   }

   @Override
   public String writeKey() {
      return this.writeKey;
   }

   @Override
   public String toString() {
      return "Batch{batch="
         + this.batch
         + ", sentAt="
         + this.sentAt
         + ", context="
         + this.context
         + ", sequence="
         + this.sequence
         + ", writeKey="
         + this.writeKey
         + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Batch)) {
         return false;
      } else {
         Batch that = (Batch)o;
         return this.batch.equals(that.batch())
            && this.sentAt.equals(that.sentAt())
            && this.context.equals(that.context())
            && this.sequence == that.sequence()
            && this.writeKey.equals(that.writeKey());
      }
   }

   @Override
   public int hashCode() {
      int h$ = 1;
      h$ *= 1000003;
      h$ ^= this.batch.hashCode();
      h$ *= 1000003;
      h$ ^= this.sentAt.hashCode();
      h$ *= 1000003;
      h$ ^= this.context.hashCode();
      h$ *= 1000003;
      h$ ^= this.sequence;
      h$ *= 1000003;
      return h$ ^ this.writeKey.hashCode();
   }
}
