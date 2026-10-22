package org.eclipse.jetty.websocket.core.internal;

import java.nio.charset.CharacterCodingException;
import java.util.function.Supplier;
import org.eclipse.jetty.util.Utf8StringBuilder;

public class NullAppendable extends Utf8StringBuilder {
   public NullAppendable() {
      super(null);
   }

   public void append(CharSequence chars, int offset, int length) {
      this.checkCharAppend();
   }

   public void append(char c) {
      this.checkCharAppend();
   }

   public void append(String s) {
      this.checkCharAppend();
   }

   public void append(String s, int offset, int length) {
      this.checkCharAppend();
   }

   protected void bufferAppend(char c) {
   }

   protected void bufferReset() {
   }

   public String toPartialString() {
      return null;
   }

   public String toCompleteString() {
      this.complete();
      return null;
   }

   public <X extends Throwable> String takeCompleteString(Supplier<X> onCodingError) throws X {
      this.complete();
      return this.takePartialString(onCodingError);
   }

   public <X extends Throwable> String takePartialString(Supplier<X> onCodingError) throws X {
      if (onCodingError != null && this.hasCodingErrors()) {
         X x = (X)onCodingError.get();
         if (x != null) {
            throw x;
         }
      }

      return null;
   }

   public String build() throws CharacterCodingException {
      this.complete();
      if (this.hasCodingErrors()) {
         throw new CharacterCodingException();
      } else {
         return null;
      }
   }
}
