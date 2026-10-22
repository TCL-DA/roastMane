package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
   private static final String EXIT_DEFAULT_PREFIX = "Exit";
   private static final String ENTRY_DEFAULT_PREFIX = "Enter";
   private static final long serialVersionUID = 8578655591131397576L;
   public static final FlowMessageFactory INSTANCE = new DefaultFlowMessageFactory();
   private final String entryText;
   private final String exitText;

   public DefaultFlowMessageFactory() {
      this("Enter", "Exit");
   }

   public DefaultFlowMessageFactory(final String entryText, final String exitText) {
      this.entryText = entryText;
      this.exitText = exitText;
   }

   public String getEntryText() {
      return this.entryText;
   }

   public String getExitText() {
      return this.exitText;
   }

   @Override
   public EntryMessage newEntryMessage(final String format, final Object... params) {
      boolean hasFormat = Strings.isNotEmpty(format);
      Message message;
      if (params != null && params.length != 0) {
         if (hasFormat) {
            message = ParameterizedMessageFactory.INSTANCE.newMessage(format, params);
         } else {
            StringBuilder sb = new StringBuilder("params(");

            for (int i = 0; i < params.length; i++) {
               if (i > 0) {
                  sb.append(", ");
               }

               sb.append("{}");
            }

            sb.append(")");
            message = ParameterizedMessageFactory.INSTANCE.newMessage(sb.toString(), params);
         }
      } else {
         message = hasFormat ? ParameterizedMessageFactory.INSTANCE.newMessage(format) : null;
      }

      return this.newEntryMessage(message);
   }

   @Override
   public EntryMessage newEntryMessage(final Message message) {
      return new DefaultFlowMessageFactory.SimpleEntryMessage(this.entryText, this.makeImmutable(message));
   }

   private Message makeImmutable(final Message message) {
      return message instanceof ReusableMessage ? ((ReusableMessage)message).memento() : message;
   }

   @Override
   public ExitMessage newExitMessage(final String format, final Object result) {
      boolean hasFormat = Strings.isNotEmpty(format);
      Message message;
      if (result == null) {
         message = hasFormat ? ParameterizedMessageFactory.INSTANCE.newMessage(format) : null;
      } else {
         message = ParameterizedMessageFactory.INSTANCE.newMessage(hasFormat ? format : "with({})", result);
      }

      return this.newExitMessage(message);
   }

   @Override
   public ExitMessage newExitMessage(Message message) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, message);
   }

   @Override
   public ExitMessage newExitMessage(final EntryMessage message) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, message);
   }

   @Override
   public ExitMessage newExitMessage(final Object result, final EntryMessage message) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, result, message);
   }

   @Override
   public ExitMessage newExitMessage(final Object result, final Message message) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, result, message);
   }

   private static class AbstractFlowMessage implements FlowMessage, StringBuilderFormattable {
      private static final long serialVersionUID = 1L;
      private final Message message;
      private final String text;

      AbstractFlowMessage(final String text, final Message message) {
         this.message = message;
         this.text = text;
      }

      @Override
      public String getFormattedMessage() {
         return this.message != null ? this.text + " " + this.message.getFormattedMessage() : this.text;
      }

      @Override
      public String getFormat() {
         return this.message != null ? this.text + " " + this.message.getFormat() : this.text;
      }

      @Override
      public Object[] getParameters() {
         return this.message != null ? this.message.getParameters() : null;
      }

      @Override
      public Throwable getThrowable() {
         return this.message != null ? this.message.getThrowable() : null;
      }

      @Override
      public Message getMessage() {
         return this.message;
      }

      @Override
      public String getText() {
         return this.text;
      }

      @Override
      public void formatTo(final StringBuilder buffer) {
         buffer.append(this.text);
         if (this.message != null) {
            buffer.append(" ");
            StringBuilders.appendValue(buffer, this.message);
         }
      }
   }

   private static final class SimpleEntryMessage extends DefaultFlowMessageFactory.AbstractFlowMessage implements EntryMessage {
      private static final long serialVersionUID = 1L;

      SimpleEntryMessage(final String entryText, final Message message) {
         super(entryText, message);
      }
   }

   private static final class SimpleExitMessage extends DefaultFlowMessageFactory.AbstractFlowMessage implements ExitMessage {
      private static final long serialVersionUID = 1L;
      private final Object result;
      private final boolean isVoid;

      SimpleExitMessage(final String exitText, final EntryMessage message) {
         this(exitText, message.getMessage());
      }

      SimpleExitMessage(final String exitText, final Message message) {
         super(exitText, message);
         this.result = null;
         this.isVoid = true;
      }

      SimpleExitMessage(final String exitText, final Object result, final EntryMessage message) {
         this(exitText, result, message.getMessage());
      }

      SimpleExitMessage(final String exitText, final Object result, final Message message) {
         super(exitText, message);
         this.result = result;
         this.isVoid = false;
      }

      @Override
      public String getFormattedMessage() {
         String formattedMessage = super.getFormattedMessage();
         return this.isVoid ? formattedMessage : formattedMessage + ": " + this.result;
      }
   }
}
