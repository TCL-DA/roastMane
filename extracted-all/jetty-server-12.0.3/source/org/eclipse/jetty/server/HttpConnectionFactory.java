package org.eclipse.jetty.server;

import java.util.Objects;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.internal.HttpConnection;
import org.eclipse.jetty.util.annotation.Name;

public class HttpConnectionFactory extends AbstractConnectionFactory implements HttpConfiguration.ConnectionFactory {
   private final HttpConfiguration _config;
   private boolean _recordHttpComplianceViolations;
   private boolean _useInputDirectByteBuffers;
   private boolean _useOutputDirectByteBuffers;

   public HttpConnectionFactory() {
      this(new HttpConfiguration());
   }

   public HttpConnectionFactory(@Name("config") HttpConfiguration config) {
      super(HttpVersion.HTTP_1_1.asString());
      this._config = Objects.requireNonNull(config);
      this.addBean(this._config);
      this.setUseInputDirectByteBuffers(this._config.isUseInputDirectByteBuffers());
      this.setUseOutputDirectByteBuffers(this._config.isUseOutputDirectByteBuffers());
   }

   @Override
   public HttpConfiguration getHttpConfiguration() {
      return this._config;
   }

   public boolean isRecordHttpComplianceViolations() {
      return this._recordHttpComplianceViolations;
   }

   public void setRecordHttpComplianceViolations(boolean recordHttpComplianceViolations) {
      this._recordHttpComplianceViolations = recordHttpComplianceViolations;
   }

   public boolean isUseInputDirectByteBuffers() {
      return this._useInputDirectByteBuffers;
   }

   public void setUseInputDirectByteBuffers(boolean useInputDirectByteBuffers) {
      this._useInputDirectByteBuffers = useInputDirectByteBuffers;
   }

   public boolean isUseOutputDirectByteBuffers() {
      return this._useOutputDirectByteBuffers;
   }

   public void setUseOutputDirectByteBuffers(boolean useOutputDirectByteBuffers) {
      this._useOutputDirectByteBuffers = useOutputDirectByteBuffers;
   }

   @Override
   public Connection newConnection(Connector connector, EndPoint endPoint) {
      HttpConnection connection = new HttpConnection(this._config, connector, endPoint, this.isRecordHttpComplianceViolations());
      connection.setUseInputDirectByteBuffers(this.isUseInputDirectByteBuffers());
      connection.setUseOutputDirectByteBuffers(this.isUseOutputDirectByteBuffers());
      return this.configure(connection, connector, endPoint);
   }
}
