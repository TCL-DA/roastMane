package io.github.classgraph;

public class ClassGraphException extends IllegalArgumentException {
   static final long serialVersionUID = 1L;

   ClassGraphException(String message) {
      super(message);
   }

   ClassGraphException(String message, Throwable cause) {
      super(message, cause);
   }
}
