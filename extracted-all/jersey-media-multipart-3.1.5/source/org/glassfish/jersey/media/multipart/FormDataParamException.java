package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.server.ParamException;

public final class FormDataParamException extends ParamException {
   public FormDataParamException(Throwable cause, String name, String defaultStringValue) {
      super(cause, Status.BAD_REQUEST, FormDataParam.class, name, defaultStringValue);
   }
}
