package org.apache.logging.slf4j.message;

import java.util.Arrays;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;

public final class ThrowableConsumingMessageFactory implements MessageFactory2 {
   private Message newParameterizedMessage(final Object throwable, final String pattern, final Object... args) {
      return new ParameterizedMessage(pattern, args, (Throwable)throwable);
   }

   public Message newMessage(final Object message) {
      return new ObjectMessage(message);
   }

   public Message newMessage(final String message) {
      return new SimpleMessage(message);
   }

   public Message newMessage(final String message, final Object... params) {
      if (params != null && params.length > 0) {
         Object lastArg = params[params.length - 1];
         return lastArg instanceof Throwable
            ? this.newParameterizedMessage(lastArg, message, Arrays.copyOf(params, params.length - 1))
            : this.newParameterizedMessage(null, message, params);
      } else {
         return new SimpleMessage(message);
      }
   }

   public Message newMessage(final CharSequence charSequence) {
      return new SimpleMessage(charSequence);
   }

   public Message newMessage(final String message, final Object p0) {
      return p0 instanceof Throwable ? this.newParameterizedMessage(p0, message) : this.newParameterizedMessage(null, message, p0);
   }

   public Message newMessage(final String message, final Object p0, final Object p1) {
      return p1 instanceof Throwable ? this.newParameterizedMessage(p1, message, p0) : this.newParameterizedMessage(null, message, p0, p1);
   }

   public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
      return p2 instanceof Throwable ? this.newParameterizedMessage(p2, message, p0, p1) : this.newParameterizedMessage(null, message, p0, p1, p2);
   }

   public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
      return p3 instanceof Throwable ? this.newParameterizedMessage(p3, message, p0, p1, p2) : this.newParameterizedMessage(null, message, p0, p1, p2, p3);
   }

   public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
      return p4 instanceof Throwable
         ? this.newParameterizedMessage(p4, message, p0, p1, p2, p3)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4);
   }

   public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
      return p5 instanceof Throwable
         ? this.newParameterizedMessage(p5, message, p0, p1, p2, p3, p4)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4, p5);
   }

   public Message newMessage(
      final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6
   ) {
      return p6 instanceof Throwable
         ? this.newParameterizedMessage(p6, message, p0, p1, p2, p3, p4, p5)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4, p5, p6);
   }

   public Message newMessage(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7
   ) {
      return p7 instanceof Throwable
         ? this.newParameterizedMessage(p7, message, p0, p1, p2, p3, p4, p5, p6)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4, p5, p6, p7);
   }

   public Message newMessage(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7,
      final Object p8
   ) {
      return p8 instanceof Throwable
         ? this.newParameterizedMessage(p8, message, p0, p1, p2, p3, p4, p5, p6, p7)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
   }

   public Message newMessage(
      final String message,
      final Object p0,
      final Object p1,
      final Object p2,
      final Object p3,
      final Object p4,
      final Object p5,
      final Object p6,
      final Object p7,
      final Object p8,
      final Object p9
   ) {
      return p9 instanceof Throwable
         ? this.newParameterizedMessage(p9, message, p0, p1, p2, p3, p4, p5, p6, p7, p8)
         : this.newParameterizedMessage(null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
   }
}
