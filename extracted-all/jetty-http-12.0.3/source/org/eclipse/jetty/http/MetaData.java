package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.jetty.util.NanoTime;

public class MetaData implements Iterable<HttpField> {
   private final HttpVersion _httpVersion;
   private final HttpFields _httpFields;
   private final long _contentLength;
   private final Supplier<HttpFields> _trailers;

   public static boolean isTunnel(String method, int status) {
      return HttpMethod.CONNECT.is(method) && HttpStatus.isSuccess(status);
   }

   public MetaData(HttpVersion version, HttpFields fields) {
      this(version, fields, -1L);
   }

   public MetaData(HttpVersion version, HttpFields fields, long contentLength) {
      this(version, fields, contentLength, null);
   }

   public MetaData(HttpVersion version, HttpFields headers, long contentLength, Supplier<HttpFields> trailersSupplier) {
      this._httpVersion = Objects.requireNonNull(version);
      this._httpFields = headers == null ? HttpFields.EMPTY : headers.asImmutable();
      this._contentLength = contentLength;
      this._trailers = trailersSupplier;
   }

   public boolean isRequest() {
      return false;
   }

   public boolean isResponse() {
      return false;
   }

   public HttpVersion getHttpVersion() {
      return this._httpVersion;
   }

   public HttpFields getHttpFields() {
      return this._httpFields;
   }

   public Supplier<HttpFields> getTrailersSupplier() {
      return this._trailers;
   }

   public long getContentLength() {
      return this._contentLength;
   }

   @Override
   public Iterator<HttpField> iterator() {
      return this._httpFields.iterator();
   }

   @Override
   public String toString() {
      return this._httpFields.toString();
   }

   public static class ConnectRequest extends MetaData.Request {
      private final String _protocol;

      public ConnectRequest(HttpScheme scheme, HostPortHttpField authority, String path, HttpFields headers, String protocol) {
         this(NanoTime.now(), scheme == null ? null : scheme.asString(), authority, path, headers, protocol);
      }

      public ConnectRequest(long beginNanoTime, HttpScheme scheme, HostPortHttpField authority, String path, HttpFields headers, String protocol) {
         this(beginNanoTime, scheme == null ? null : scheme.asString(), authority, path, headers, protocol);
      }

      public ConnectRequest(String scheme, HostPortHttpField authority, String path, HttpFields headers, String protocol) {
         this(NanoTime.now(), scheme, authority, path, headers, protocol);
      }

      public ConnectRequest(long beginNanoTime, String scheme, HostPortHttpField authority, String path, HttpFields headers, String protocol) {
         super(
            beginNanoTime,
            HttpMethod.CONNECT.asString(),
            HttpURI.build()
               .scheme(scheme)
               .host(authority == null ? null : authority.getHost())
               .port(authority == null ? -1 : authority.getPort())
               .pathQuery(path),
            HttpVersion.HTTP_2,
            headers,
            -1L,
            null
         );
         this._protocol = protocol;
      }

      @Override
      public String getProtocol() {
         return this._protocol;
      }
   }

   public static class Request extends MetaData {
      private final String _method;
      private final HttpURI _uri;
      private final long _beginNanoTime;

      public Request(String method, HttpURI uri, HttpVersion version, HttpFields headers) {
         this(NanoTime.now(), method, uri, version, headers, -1L);
      }

      public Request(long beginNanoTime, String method, HttpURI uri, HttpVersion version, HttpFields headers) {
         this(beginNanoTime, method, uri, version, headers, -1L);
      }

      public Request(String method, String scheme, HostPortHttpField authority, String uri, HttpVersion version, HttpFields headers, long contentLength) {
         this(
            NanoTime.now(),
            method,
            HttpURI.build()
               .scheme(scheme)
               .host(authority == null ? null : authority.getHost())
               .port(authority == null ? -1 : authority.getPort())
               .pathQuery(uri),
            version,
            headers,
            contentLength
         );
      }

      public Request(
         long beginNanoTime, String method, String scheme, HostPortHttpField authority, String uri, HttpVersion version, HttpFields headers, long contentLength
      ) {
         this(
            beginNanoTime,
            method,
            HttpURI.build()
               .scheme(scheme)
               .host(authority == null ? null : authority.getHost())
               .port(authority == null ? -1 : authority.getPort())
               .pathQuery(uri),
            version,
            headers,
            contentLength
         );
      }

      public Request(String method, HttpURI uri, HttpVersion version, HttpFields headers, long contentLength) {
         this(NanoTime.now(), method, uri, version, headers, contentLength, null);
      }

      public Request(long beginNanoTime, String method, HttpURI uri, HttpVersion version, HttpFields headers, long contentLength) {
         this(beginNanoTime, method, uri, version, headers, contentLength, null);
      }

      public Request(String method, HttpURI uri, HttpVersion version, HttpFields headers, long contentLength, Supplier<HttpFields> trailers) {
         this(NanoTime.now(), method, uri, version, headers, contentLength, trailers);
      }

      public Request(long beginNanoTime, String method, HttpURI uri, HttpVersion version, HttpFields headers, long contentLength, Supplier<HttpFields> trailers) {
         super(version, headers, contentLength, trailers);
         this._method = Objects.requireNonNull(method);
         this._uri = Objects.requireNonNull(uri);
         this._beginNanoTime = beginNanoTime;
      }

      @Override
      public boolean isRequest() {
         return true;
      }

      public long getBeginNanoTime() {
         return this._beginNanoTime;
      }

      public String getMethod() {
         return this._method;
      }

      public HttpURI getHttpURI() {
         return this._uri;
      }

      public String getProtocol() {
         return null;
      }

      @Override
      public String toString() {
         HttpFields headers = this.getHttpFields();
         return String.format(
            "%s{u=%s,%s,h=%d,cl=%d,p=%s}",
            this.getMethod(),
            this.getHttpURI(),
            this.getHttpVersion(),
            headers.size(),
            this.getContentLength(),
            this.getProtocol()
         );
      }
   }

   public static class Response extends MetaData {
      private final int _status;
      private final String _reason;

      public Response(int status, String reason, HttpVersion version, HttpFields headers) {
         this(status, reason, version, headers, -1L);
      }

      public Response(int status, String reason, HttpVersion version, HttpFields headers, long contentLength) {
         this(status, reason, version, headers, contentLength, null);
      }

      public Response(int status, String reason, HttpVersion version, HttpFields headers, long contentLength, Supplier<HttpFields> trailers) {
         super(version, headers, contentLength, trailers);
         this._status = status;
         this._reason = reason;
      }

      @Override
      public boolean isResponse() {
         return true;
      }

      public int getStatus() {
         return this._status;
      }

      public String getReason() {
         return this._reason;
      }

      @Override
      public String toString() {
         HttpFields headers = this.getHttpFields();
         return String.format("%s{s=%d,h=%d,cl=%d}", this.getHttpVersion(), this.getStatus(), headers.size(), this.getContentLength());
      }
   }
}
