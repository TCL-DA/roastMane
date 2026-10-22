package com.cropster.util.exception.errordetail;

public interface ErrorDetails {
   Object getId();

   String getCode();

   Class<?> getValidatedType();
}
