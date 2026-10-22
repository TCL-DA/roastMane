package org.glassfish.jersey.client;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.uri.JerseyQueryParamStyle;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

public class JerseyWebTarget implements WebTarget, Initializable<JerseyWebTarget> {
   private final ClientConfig config;
   private final UriBuilder targetUri;

   JerseyWebTarget(String uri, JerseyClient parent) {
      this(UriBuilder.fromUri(uri), parent.getConfiguration());
   }

   JerseyWebTarget(URI uri, JerseyClient parent) {
      this(UriBuilder.fromUri(uri), parent.getConfiguration());
   }

   JerseyWebTarget(UriBuilder uriBuilder, JerseyClient parent) {
      this(uriBuilder.clone(), parent.getConfiguration());
   }

   JerseyWebTarget(Link link, JerseyClient parent) {
      this(UriBuilder.fromUri(link.getUri()), parent.getConfiguration());
   }

   protected JerseyWebTarget(UriBuilder uriBuilder, JerseyWebTarget that) {
      this(uriBuilder, that.config);
   }

   protected JerseyWebTarget(UriBuilder uriBuilder, ClientConfig clientConfig) {
      clientConfig.checkClient();
      this.targetUri = uriBuilder;
      this.config = clientConfig.snapshot();
   }

   public URI getUri() {
      this.checkNotClosed();

      try {
         return this.targetUri.build(new Object[0]);
      } catch (IllegalArgumentException var2) {
         throw new IllegalStateException(var2.getMessage(), var2);
      }
   }

   private void checkNotClosed() {
      this.config.getClient().checkNotClosed();
   }

   public UriBuilder getUriBuilder() {
      this.checkNotClosed();
      return this.targetUri.clone();
   }

   public JerseyWebTarget path(String path) throws NullPointerException {
      this.checkNotClosed();
      Preconditions.checkNotNull(path, "path is 'null'.");
      return new JerseyWebTarget(this.getUriBuilder().path(path), this);
   }

   public JerseyWebTarget matrixParam(String name, Object... values) throws NullPointerException {
      this.checkNotClosed();
      Preconditions.checkNotNull(name, "Matrix parameter name must not be 'null'.");
      if (values != null && values.length != 0 && (values.length != 1 || values[0] != null)) {
         checkForNullValues(name, values);
         return new JerseyWebTarget(this.getUriBuilder().matrixParam(name, values), this);
      } else {
         return new JerseyWebTarget(this.getUriBuilder().replaceMatrixParam(name, (Object[])null), this);
      }
   }

   public JerseyWebTarget queryParam(String name, Object... values) throws NullPointerException {
      this.checkNotClosed();
      UriBuilder uriBuilder = this.getUriBuilder();
      Object queryParamProperty = this.getConfiguration().getProperty("jersey.config.client.uri.query.param.style");
      if (queryParamProperty instanceof JerseyQueryParamStyle && uriBuilder instanceof JerseyUriBuilder) {
         ((JerseyUriBuilder)uriBuilder).setQueryParamStyle((JerseyQueryParamStyle)queryParamProperty);
      }

      return new JerseyWebTarget(setQueryParam(uriBuilder, name, values), this);
   }

   private static UriBuilder setQueryParam(UriBuilder uriBuilder, String name, Object[] values) {
      if (values != null && values.length != 0 && (values.length != 1 || values[0] != null)) {
         checkForNullValues(name, values);
         return uriBuilder.queryParam(name, values);
      } else {
         return uriBuilder.replaceQueryParam(name, (Object[])null);
      }
   }

   private static void checkForNullValues(String name, Object[] values) {
      Preconditions.checkNotNull(name, "name is 'null'.");
      List<Integer> indexes = new LinkedList<>();

      for (int i = 0; i < values.length; i++) {
         if (values[i] == null) {
            indexes.add(i);
         }
      }

      int failedIndexCount = indexes.size();
      if (failedIndexCount > 0) {
         String valueTxt;
         String indexTxt;
         if (failedIndexCount == 1) {
            valueTxt = "value";
            indexTxt = "index";
         } else {
            valueTxt = "values";
            indexTxt = "indexes";
         }

         throw new NullPointerException(String.format("'null' %s detected for parameter '%s' on %s : %s", valueTxt, name, indexTxt, indexes.toString()));
      }
   }

   public JerseyInvocation.Builder request() {
      this.checkNotClosed();
      JerseyInvocation.Builder b = new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
      return onBuilder(b);
   }

   public JerseyInvocation.Builder request(String... acceptedResponseTypes) {
      this.checkNotClosed();
      JerseyInvocation.Builder b = new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
      onBuilder(b).request().accept(acceptedResponseTypes);
      return b;
   }

   public JerseyInvocation.Builder request(MediaType... acceptedResponseTypes) {
      this.checkNotClosed();
      JerseyInvocation.Builder b = new JerseyInvocation.Builder(this.getUri(), this.config.snapshot());
      onBuilder(b).request().accept(acceptedResponseTypes);
      return b;
   }

   public JerseyWebTarget resolveTemplate(String name, Object value) throws NullPointerException {
      return this.resolveTemplate(name, value, true);
   }

   public JerseyWebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) throws NullPointerException {
      this.checkNotClosed();
      Preconditions.checkNotNull(name, "name is 'null'.");
      Preconditions.checkNotNull(value, "value is 'null'.");
      return new JerseyWebTarget(this.getUriBuilder().resolveTemplate(name, value, encodeSlashInPath), this);
   }

   public JerseyWebTarget resolveTemplateFromEncoded(String name, Object value) throws NullPointerException {
      this.checkNotClosed();
      Preconditions.checkNotNull(name, "name is 'null'.");
      Preconditions.checkNotNull(value, "value is 'null'.");
      return new JerseyWebTarget(this.getUriBuilder().resolveTemplateFromEncoded(name, value), this);
   }

   public JerseyWebTarget resolveTemplates(Map<String, Object> templateValues) throws NullPointerException {
      return this.resolveTemplates(templateValues, true);
   }

   public JerseyWebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws NullPointerException {
      this.checkNotClosed();
      this.checkTemplateValues(templateValues);
      return templateValues.isEmpty() ? this : new JerseyWebTarget(this.getUriBuilder().resolveTemplates(templateValues, encodeSlashInPath), this);
   }

   public JerseyWebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) throws NullPointerException {
      this.checkNotClosed();
      this.checkTemplateValues(templateValues);
      return templateValues.isEmpty() ? this : new JerseyWebTarget(this.getUriBuilder().resolveTemplatesFromEncoded(templateValues), this);
   }

   private void checkTemplateValues(Map<String, Object> templateValues) throws NullPointerException {
      Preconditions.checkNotNull(templateValues, "templateValues is 'null'.");

      for (Entry entry : templateValues.entrySet()) {
         Preconditions.checkNotNull(entry.getKey(), "name is 'null'.");
         Preconditions.checkNotNull(entry.getValue(), "value is 'null'.");
      }
   }

   public JerseyWebTarget register(Class<?> providerClass) {
      this.checkNotClosed();
      this.config.register(providerClass);
      return this;
   }

   public JerseyWebTarget register(Object provider) {
      this.checkNotClosed();
      this.config.register(provider);
      return this;
   }

   public JerseyWebTarget register(Class<?> providerClass, int bindingPriority) {
      this.checkNotClosed();
      this.config.register(providerClass, bindingPriority);
      return this;
   }

   public JerseyWebTarget register(Class<?> providerClass, Class<?>... contracts) {
      this.checkNotClosed();
      this.config.register(providerClass, contracts);
      return this;
   }

   public JerseyWebTarget register(Class<?> providerClass, Map<Class<?>, Integer> contracts) {
      this.checkNotClosed();
      this.config.register(providerClass, contracts);
      return this;
   }

   public JerseyWebTarget register(Object provider, int bindingPriority) {
      this.checkNotClosed();
      this.config.register(provider, bindingPriority);
      return this;
   }

   public JerseyWebTarget register(Object provider, Class<?>... contracts) {
      this.checkNotClosed();
      this.config.register(provider, contracts);
      return this;
   }

   public JerseyWebTarget register(Object provider, Map<Class<?>, Integer> contracts) {
      this.checkNotClosed();
      this.config.register(provider, contracts);
      return this;
   }

   public JerseyWebTarget property(String name, Object value) {
      this.checkNotClosed();
      this.config.property(name, value);
      return this;
   }

   @Override
   public ClientConfig getConfiguration() {
      this.checkNotClosed();
      return this.config.getConfiguration();
   }

   public JerseyWebTarget preInitialize() {
      this.config.preInitialize();
      return this;
   }

   @Override
   public String toString() {
      return "JerseyWebTarget { " + this.targetUri.toTemplate() + " }";
   }

   private static JerseyInvocation.Builder onBuilder(JerseyInvocation.Builder builder) {
      builder.request().getClientRuntime().getInvocationBuilderListenerStage().invokeListener(builder);
      return builder;
   }
}
