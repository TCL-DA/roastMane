package org.glassfish.jersey.http;

import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.Response.Status.Family;

public final class ResponseStatus {
   public static class ClientError4xx {
      public static final StatusType BAD_REQUEST_400 = new ResponseStatus.ResponseStatusImpl(400, "Bad Request");
      public static final StatusType UNAUTHORIZED_401 = new ResponseStatus.ResponseStatusImpl(401, "Unauthorized");
      public static final StatusType PAYMENT_REQUIRED_402 = new ResponseStatus.ResponseStatusImpl(402, "Payment Required");
      public static final StatusType FORBIDDEN_403 = new ResponseStatus.ResponseStatusImpl(403, "Forbidden");
      public static final StatusType NOT_FOUND_404 = new ResponseStatus.ResponseStatusImpl(404, "Not Found");
      public static final StatusType METHOD_NOT_ALLOWED_405 = new ResponseStatus.ResponseStatusImpl(405, "Method Not Allowed");
      public static final StatusType NOT_ACCEPTABLE_406 = new ResponseStatus.ResponseStatusImpl(406, "Not Acceptable");
      public static final StatusType PROXY_AUTHENTICATION_REQUIRED_407 = new ResponseStatus.ResponseStatusImpl(407, "Proxy Authentication Required");
      public static final StatusType REQUEST_TIMEOUT_408 = new ResponseStatus.ResponseStatusImpl(408, "Request Timeout");
      public static final StatusType CONFLICT_409 = new ResponseStatus.ResponseStatusImpl(409, "Conflict");
      public static final StatusType GONE_410 = new ResponseStatus.ResponseStatusImpl(410, "Gone");
      public static final StatusType LENGTH_REQUIRED_411 = new ResponseStatus.ResponseStatusImpl(411, "Length Required");
      public static final StatusType PRECONDITION_FAILED_412 = new ResponseStatus.ResponseStatusImpl(412, "Precondition Failed");
      public static final StatusType REQUEST_ENTITY_TOO_LARGE_413 = new ResponseStatus.ResponseStatusImpl(413, "Request Entity Too Large");
      public static final StatusType REQUEST_URI_TOO_LONG_414 = new ResponseStatus.ResponseStatusImpl(414, "Request-URI Too Long");
      public static final StatusType UNSUPPORTED_MEDIA_TYPE_415 = new ResponseStatus.ResponseStatusImpl(415, "Unsupported Media Type");
      public static final StatusType REQUESTED_RANGE_NOT_SATISFIABLE_416 = new ResponseStatus.ResponseStatusImpl(416, "Requested Range Not Satisfiable");
      public static final StatusType EXPECTATION_FAILED_417 = new ResponseStatus.ResponseStatusImpl(417, "Expectation Failed");
      public static final StatusType I_AM_A_TEAPOT_418 = new ResponseStatus.ResponseStatusImpl(418, "I'm a teapot");
      public static final StatusType MISDIRECTED_REQUEST_421 = new ResponseStatus.ResponseStatusImpl(421, "Misdirected Request");
      public static final StatusType UNPROCESSABLE_CONTENT_422 = new ResponseStatus.ResponseStatusImpl(422, "Unprocessable Content");
      public static final StatusType LOCKED_423 = new ResponseStatus.ResponseStatusImpl(423, "Locked");
      public static final StatusType FAILED_DEPENDENCY_424 = new ResponseStatus.ResponseStatusImpl(424, "Failed Dependency");
      public static final StatusType TOO_EARLY_425 = new ResponseStatus.ResponseStatusImpl(425, "Too Early");
      public static final StatusType UPGRADE_REQUIRED_426 = new ResponseStatus.ResponseStatusImpl(426, "Upgrade Required");
      public static final StatusType PRECONDITION_REQUIRED_428 = new ResponseStatus.ResponseStatusImpl(428, "Precondition Required");
      public static final StatusType TOO_MANY_REQUESTS_429 = new ResponseStatus.ResponseStatusImpl(429, "Too Many Requests");
      public static final StatusType REQUEST_HEADER_FIELDS_TOO_LARGE_431 = new ResponseStatus.ResponseStatusImpl(431, "Request Header Fields Too Large");
      public static final StatusType UNAVAILABLE_FOR_LEGAL_REASONS_451 = new ResponseStatus.ResponseStatusImpl(451, "Unavailable For Legal Reasons");
   }

   public static class Info1xx {
      public static final StatusType CONTINUE_100 = new ResponseStatus.ResponseStatusImpl(100, "Continue");
      public static final StatusType SWITCHING_PROTOCOLS_101 = new ResponseStatus.ResponseStatusImpl(101, "Switching Protocols");
      public static final StatusType PROCESSING_102 = new ResponseStatus.ResponseStatusImpl(102, "Processing");
      public static final StatusType EARLY_HINTS_103 = new ResponseStatus.ResponseStatusImpl(103, "Early Hints");
   }

   public static class Redirect3xx {
      public static final StatusType MULTIPLE_CHOICES_300 = new ResponseStatus.ResponseStatusImpl(300, "Multiple Choices");
      public static final StatusType MOVED_PERMANENTLY_301 = new ResponseStatus.ResponseStatusImpl(301, "Moved Permanently");
      public static final StatusType FOUND_302 = new ResponseStatus.ResponseStatusImpl(302, "Found");
      public static final StatusType SEE_OTHER_303 = new ResponseStatus.ResponseStatusImpl(303, "See Other");
      public static final StatusType NOT_MODIFIED_304 = new ResponseStatus.ResponseStatusImpl(304, "Not Modified");
      public static final StatusType USE_PROXY_305 = new ResponseStatus.ResponseStatusImpl(305, "Use Proxy");
      public static final StatusType TEMPORARY_REDIRECT_307 = new ResponseStatus.ResponseStatusImpl(307, "Temporary Redirect");
      public static final StatusType PERMANENT_REDIRECT_308 = new ResponseStatus.ResponseStatusImpl(308, "Permanent Redirect");
   }

   private static class ResponseStatusImpl implements StatusType {
      private final int statusCode;
      private final String reasonPhrase;
      private final Family family;

      private ResponseStatusImpl(int statusCode, String reasonPhrase) {
         this.statusCode = statusCode;
         this.reasonPhrase = reasonPhrase;
         this.family = Family.familyOf(statusCode);
      }

      public int getStatusCode() {
         return this.statusCode;
      }

      public Family getFamily() {
         return this.family;
      }

      public String getReasonPhrase() {
         return this.reasonPhrase;
      }
   }

   public static class ServerError5xx {
      public static final StatusType INTERNAL_SERVER_ERROR_500 = new ResponseStatus.ResponseStatusImpl(500, "Internal Server Error");
      public static final StatusType NOT_IMPLEMENTED_501 = new ResponseStatus.ResponseStatusImpl(501, "Not Implemented");
      public static final StatusType BAD_GATEWAY_502 = new ResponseStatus.ResponseStatusImpl(502, "Bad Gateway");
      public static final StatusType SERVICE_UNAVAILABLE_503 = new ResponseStatus.ResponseStatusImpl(503, "Service Unavailable");
      public static final StatusType GATEWAY_TIMEOUT_504 = new ResponseStatus.ResponseStatusImpl(504, "Gateway Timeout");
      public static final StatusType HTTP_VERSION_NOT_SUPPORTED_505 = new ResponseStatus.ResponseStatusImpl(505, "HTTP Version Not Supported");
      public static final StatusType VARIANT_ALSO_NEGOTIATES_506 = new ResponseStatus.ResponseStatusImpl(506, "Variant Also Negotiates");
      public static final StatusType INSUFFICIENT_STORAGE_507 = new ResponseStatus.ResponseStatusImpl(507, "Insufficient Storage");
      public static final StatusType LOOP_DETECTED_508 = new ResponseStatus.ResponseStatusImpl(508, "Loop Detected");
      public static final StatusType NOT_EXTENDED_510 = new ResponseStatus.ResponseStatusImpl(510, "Not Extended");
      public static final StatusType NETWORK_AUTHENTICATION_REQUIRED_511 = new ResponseStatus.ResponseStatusImpl(511, "Network Authentication Required");
   }

   public static class Success2xx {
      public static final StatusType OK_200 = new ResponseStatus.ResponseStatusImpl(200, "OK");
      public static final StatusType CREATED_201 = new ResponseStatus.ResponseStatusImpl(201, "Created");
      public static final StatusType ACCEPTED_202 = new ResponseStatus.ResponseStatusImpl(202, "Accepted");
      public static final StatusType NON_AUTHORITATIVE_INFORMATION_203 = new ResponseStatus.ResponseStatusImpl(203, "Non-Authoritative Information");
      public static final StatusType NO_CONTENT_204 = new ResponseStatus.ResponseStatusImpl(204, "No Content");
      public static final StatusType RESET_CONTENT_205 = new ResponseStatus.ResponseStatusImpl(205, "Reset Content");
      public static final StatusType PARTIAL_CONTENT_206 = new ResponseStatus.ResponseStatusImpl(206, "Partial Content");
      public static final StatusType MULTI_STATUS_207 = new ResponseStatus.ResponseStatusImpl(207, "Multi-Status");
      public static final StatusType ALREADY_REPORTED_208 = new ResponseStatus.ResponseStatusImpl(208, "Already Reported");
      public static final StatusType IM_USED_226 = new ResponseStatus.ResponseStatusImpl(226, "IM used");
   }
}
