package org.glassfish.jersey.server;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import java.lang.annotation.Annotation;

public abstract class ParamException extends WebApplicationException {
   private static final long serialVersionUID = -2359567574412607846L;
   private final Class<? extends Annotation> parameterType;
   private final String name;
   private final String defaultStringValue;

   protected ParamException(Throwable cause, StatusType status, Class<? extends Annotation> parameterType, String name, String defaultStringValue) {
      super(cause, status.getStatusCode());
      this.parameterType = parameterType;
      this.name = name;
      this.defaultStringValue = defaultStringValue;
   }

   public Class<? extends Annotation> getParameterType() {
      return this.parameterType;
   }

   public String getParameterName() {
      return this.name;
   }

   public String getDefaultStringValue() {
      return this.defaultStringValue;
   }

   public static class CookieParamException extends ParamException {
      private static final long serialVersionUID = -5288504201234567266L;

      public CookieParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, Status.BAD_REQUEST, CookieParam.class, name, defaultStringValue);
      }
   }

   public static class FormParamException extends ParamException {
      private static final long serialVersionUID = -1704379792199980689L;

      public FormParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, Status.BAD_REQUEST, FormParam.class, name, defaultStringValue);
      }
   }

   public static class HeaderParamException extends ParamException {
      private static final long serialVersionUID = 6508174603506313274L;

      public HeaderParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, Status.BAD_REQUEST, HeaderParam.class, name, defaultStringValue);
      }
   }

   public static class MatrixParamException extends ParamException.UriParamException {
      private static final long serialVersionUID = -5849392883623736362L;

      public MatrixParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, MatrixParam.class, name, defaultStringValue);
      }
   }

   public static class PathParamException extends ParamException.UriParamException {
      private static final long serialVersionUID = -2708538214692835633L;

      public PathParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, PathParam.class, name, defaultStringValue);
      }
   }

   public static class QueryParamException extends ParamException.UriParamException {
      private static final long serialVersionUID = -4822407467792322910L;

      public QueryParamException(Throwable cause, String name, String defaultStringValue) {
         super(cause, QueryParam.class, name, defaultStringValue);
      }
   }

   public abstract static class UriParamException extends ParamException {
      private static final long serialVersionUID = 44233528459885541L;

      protected UriParamException(Throwable cause, Class<? extends Annotation> parameterType, String name, String defaultStringValue) {
         super(cause, Status.NOT_FOUND, parameterType, name, defaultStringValue);
      }
   }
}
