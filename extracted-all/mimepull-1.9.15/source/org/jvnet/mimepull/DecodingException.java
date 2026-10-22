package org.jvnet.mimepull;

import java.io.IOException;

public final class DecodingException extends IOException {
   private static final long serialVersionUID = 1L;

   public DecodingException(String message) {
      super(message);
   }
}
