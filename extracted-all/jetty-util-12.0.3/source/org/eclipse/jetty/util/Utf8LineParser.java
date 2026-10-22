package org.eclipse.jetty.util;

import java.nio.ByteBuffer;

public class Utf8LineParser {
   private Utf8LineParser.State state = Utf8LineParser.State.START;
   private Utf8StringBuilder utf;

   public String parse(ByteBuffer buf) {
      while (buf.remaining() > 0) {
         byte b = buf.get();
         if (this.parseByte(b)) {
            this.state = Utf8LineParser.State.START;
            return this.utf.takeCompleteString(Utf8StringBuilder.Utf8IllegalArgumentException::new);
         }
      }

      return null;
   }

   private boolean parseByte(byte b) {
      switch (this.state) {
         case START:
            this.utf = new Utf8StringBuilder();
            this.state = Utf8LineParser.State.PARSE;
            return this.parseByte(b);
         case PARSE:
            if (!this.utf.isComplete() || b != 13 && b != 10) {
               this.utf.append(b);
               return false;
            } else {
               this.state = Utf8LineParser.State.END;
               return this.parseByte(b);
            }
         case END:
            if (b == 10) {
               this.state = Utf8LineParser.State.START;
               return true;
            }

            return false;
         default:
            throw new IllegalStateException();
      }
   }

   private static enum State {
      START,
      PARSE,
      END;
   }
}
