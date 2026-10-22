package com.cropster.util.exception;

public abstract class MessageIdentifyingException extends RuntimeException {
   private static final long serialVersionUID = 1L;
   private final String code;
   private final int status;
   private final Object id;

   public MessageIdentifyingException() {
      this(null, null, 400);
   }

   public MessageIdentifyingException(String message, String code) {
      this(message, code, 400);
   }

   public MessageIdentifyingException(String message, String code, int status) {
      this(null, message, code, status);
   }

   public MessageIdentifyingException(Object id, String message, String code, int status) {
      super(message);
      this.id = id;
      this.code = code;
      this.status = status;
   }

   public Object getId() {
      return this.id;
   }

   public String getCode() {
      return this.code;
   }

   public int getStatus() {
      return this.status;
   }
}
