package org.glassfish.jersey.client.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.client.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.client.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableCHUNKED_INPUT_STREAM_CLOSING_ERROR() {
      return MESSAGE_FACTORY.getMessage("chunked.input.stream.closing.error", new Object[0]);
   }

   public static String CHUNKED_INPUT_STREAM_CLOSING_ERROR() {
      return LOCALIZER.localize(localizableCHUNKED_INPUT_STREAM_CLOSING_ERROR());
   }

   public static Localizable localizableCLIENT_TARGET_LINK_NULL() {
      return MESSAGE_FACTORY.getMessage("client.target.link.null", new Object[0]);
   }

   public static String CLIENT_TARGET_LINK_NULL() {
      return LOCALIZER.localize(localizableCLIENT_TARGET_LINK_NULL());
   }

   public static Localizable localizableNONINJECT_AMBIGUOUS_SERVICES(Object arg0) {
      return MESSAGE_FACTORY.getMessage("noninject.ambiguous.services", new Object[]{arg0});
   }

   public static String NONINJECT_AMBIGUOUS_SERVICES(Object arg0) {
      return LOCALIZER.localize(localizableNONINJECT_AMBIGUOUS_SERVICES(arg0));
   }

   public static Localizable localizableRESTRICTED_HEADER_PROPERTY_SETTING_TRUE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("restricted.header.property.setting.true", new Object[]{arg0});
   }

   public static String RESTRICTED_HEADER_PROPERTY_SETTING_TRUE(Object arg0) {
      return LOCALIZER.localize(localizableRESTRICTED_HEADER_PROPERTY_SETTING_TRUE(arg0));
   }

   public static Localizable localizableCLIENT_URI_BUILDER_NULL() {
      return MESSAGE_FACTORY.getMessage("client.uri.builder.null", new Object[0]);
   }

   public static String CLIENT_URI_BUILDER_NULL() {
      return LOCALIZER.localize(localizableCLIENT_URI_BUILDER_NULL());
   }

   public static Localizable localizableNEGATIVE_CHUNK_SIZE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("negative.chunk.size", new Object[]{arg0, arg1});
   }

   public static String NEGATIVE_CHUNK_SIZE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableNEGATIVE_CHUNK_SIZE(arg0, arg1));
   }

   public static Localizable localizableERROR_LISTENER_INIT(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.listener.init", new Object[]{arg0});
   }

   public static String ERROR_LISTENER_INIT(Object arg0) {
      return LOCALIZER.localize(localizableERROR_LISTENER_INIT(arg0));
   }

   public static Localizable localizableERROR_CLOSING_OUTPUT_STREAM() {
      return MESSAGE_FACTORY.getMessage("error.closing.output.stream", new Object[0]);
   }

   public static String ERROR_CLOSING_OUTPUT_STREAM() {
      return LOCALIZER.localize(localizableERROR_CLOSING_OUTPUT_STREAM());
   }

   public static Localizable localizableERROR_PARAMETER_TYPE_PROCESSING(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.parameter.type.processing", new Object[]{arg0});
   }

   public static String ERROR_PARAMETER_TYPE_PROCESSING(Object arg0) {
      return LOCALIZER.localize(localizableERROR_PARAMETER_TYPE_PROCESSING(arg0));
   }

   public static Localizable localizableNONINJECT_NO_CONSTRUCTOR(Object arg0) {
      return MESSAGE_FACTORY.getMessage("noninject.no.constructor", new Object[]{arg0});
   }

   public static String NONINJECT_NO_CONSTRUCTOR(Object arg0) {
      return LOCALIZER.localize(localizableNONINJECT_NO_CONSTRUCTOR(arg0));
   }

   public static Localizable localizableUSE_ENCODING_IGNORED(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("use.encoding.ignored", new Object[]{arg0, arg1, arg2});
   }

   public static String USE_ENCODING_IGNORED(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableUSE_ENCODING_IGNORED(arg0, arg1, arg2));
   }

   public static Localizable localizableSNI_ON_SSLENGINE() {
      return MESSAGE_FACTORY.getMessage("sni.on.sslengine", new Object[0]);
   }

   public static String SNI_ON_SSLENGINE() {
      return LOCALIZER.localize(localizableSNI_ON_SSLENGINE());
   }

   public static Localizable localizableCLIENT_RX_PROVIDER_NOT_REGISTERED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("client.rx.provider.not.registered", new Object[]{arg0});
   }

   public static String CLIENT_RX_PROVIDER_NOT_REGISTERED(Object arg0) {
      return LOCALIZER.localize(localizableCLIENT_RX_PROVIDER_NOT_REGISTERED(arg0));
   }

   public static Localizable localizableCLIENT_INSTANCE_CLOSED() {
      return MESSAGE_FACTORY.getMessage("client.instance.closed", new Object[0]);
   }

   public static String CLIENT_INSTANCE_CLOSED() {
      return LOCALIZER.localize(localizableCLIENT_INSTANCE_CLOSED());
   }

   public static Localizable localizableUSING_FIXED_ASYNC_THREADPOOL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("using.fixed.async.threadpool", new Object[]{arg0});
   }

   public static String USING_FIXED_ASYNC_THREADPOOL(Object arg0) {
      return LOCALIZER.localize(localizableUSING_FIXED_ASYNC_THREADPOOL(arg0));
   }

   public static Localizable localizableNULL_SSL_CONTEXT() {
      return MESSAGE_FACTORY.getMessage("null.ssl.context", new Object[0]);
   }

   public static String NULL_SSL_CONTEXT() {
      return LOCALIZER.localize(localizableNULL_SSL_CONTEXT());
   }

   public static Localizable localizableREQUEST_ENTITY_WRITER_NULL() {
      return MESSAGE_FACTORY.getMessage("request.entity.writer.null", new Object[0]);
   }

   public static String REQUEST_ENTITY_WRITER_NULL() {
      return LOCALIZER.localize(localizableREQUEST_ENTITY_WRITER_NULL());
   }

   public static Localizable localizableAUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED() {
      return MESSAGE_FACTORY.getMessage("authentication.credentials.request.password.unsupported", new Object[0]);
   }

   public static String AUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED() {
      return LOCALIZER.localize(localizableAUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED());
   }

   public static Localizable localizableNEGATIVE_INPUT_PARAMETER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("negative.input.parameter", new Object[]{arg0});
   }

   public static String NEGATIVE_INPUT_PARAMETER(Object arg0) {
      return LOCALIZER.localize(localizableNEGATIVE_INPUT_PARAMETER(arg0));
   }

   public static Localizable localizableUNEXPECTED_ERROR_RESPONSE_PROCESSING() {
      return MESSAGE_FACTORY.getMessage("unexpected.error.response.processing", new Object[0]);
   }

   public static String UNEXPECTED_ERROR_RESPONSE_PROCESSING() {
      return LOCALIZER.localize(localizableUNEXPECTED_ERROR_RESPONSE_PROCESSING());
   }

   public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.service.locator.provider.instance.request", new Object[]{arg0});
   }

   public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(Object arg0) {
      return LOCALIZER.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(arg0));
   }

   public static Localizable localizableCLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE() {
      return MESSAGE_FACTORY.getMessage("client.response.resolved.uri.not.absolute", new Object[0]);
   }

   public static String CLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE() {
      return LOCALIZER.localize(localizableCLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE());
   }

   public static Localizable localizableRESPONSE_TO_EXCEPTION_CONVERSION_FAILED() {
      return MESSAGE_FACTORY.getMessage("response.to.exception.conversion.failed", new Object[0]);
   }

   public static String RESPONSE_TO_EXCEPTION_CONVERSION_FAILED() {
      return LOCALIZER.localize(localizableRESPONSE_TO_EXCEPTION_CONVERSION_FAILED());
   }

   public static Localizable localizableCLIENT_RESPONSE_RESOLVED_URI_NULL() {
      return MESSAGE_FACTORY.getMessage("client.response.resolved.uri.null", new Object[0]);
   }

   public static String CLIENT_RESPONSE_RESOLVED_URI_NULL() {
      return LOCALIZER.localize(localizableCLIENT_RESPONSE_RESOLVED_URI_NULL());
   }

   public static Localizable localizableERROR_DIGEST_FILTER_GENERATOR() {
      return MESSAGE_FACTORY.getMessage("error.digest.filter.generator", new Object[0]);
   }

   public static String ERROR_DIGEST_FILTER_GENERATOR() {
      return LOCALIZER.localize(localizableERROR_DIGEST_FILTER_GENERATOR());
   }

   public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.service.locator.provider.instance.response", new Object[]{arg0});
   }

   public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(Object arg0) {
      return LOCALIZER.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(arg0));
   }

   public static Localizable localizableRESTRICTED_HEADER_PROPERTY_SETTING_FALSE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("restricted.header.property.setting.false", new Object[]{arg0});
   }

   public static String RESTRICTED_HEADER_PROPERTY_SETTING_FALSE(Object arg0) {
      return LOCALIZER.localize(localizableRESTRICTED_HEADER_PROPERTY_SETTING_FALSE(arg0));
   }

   public static Localizable localizableERROR_COMMITTING_OUTPUT_STREAM() {
      return MESSAGE_FACTORY.getMessage("error.committing.output.stream", new Object[0]);
   }

   public static String ERROR_COMMITTING_OUTPUT_STREAM() {
      return LOCALIZER.localize(localizableERROR_COMMITTING_OUTPUT_STREAM());
   }

   public static Localizable localizableAUTHENTICATION_CREDENTIALS_MISSING_BASIC() {
      return MESSAGE_FACTORY.getMessage("authentication.credentials.missing.basic", new Object[0]);
   }

   public static String AUTHENTICATION_CREDENTIALS_MISSING_BASIC() {
      return LOCALIZER.localize(localizableAUTHENTICATION_CREDENTIALS_MISSING_BASIC());
   }

   public static Localizable localizablePREINVOCATION_INTERCEPTOR_MULTIPLE_ABORTIONS() {
      return MESSAGE_FACTORY.getMessage("preinvocation.interceptor.multiple.abortions", new Object[0]);
   }

   public static String PREINVOCATION_INTERCEPTOR_MULTIPLE_ABORTIONS() {
      return LOCALIZER.localize(localizablePREINVOCATION_INTERCEPTOR_MULTIPLE_ABORTIONS());
   }

   public static Localizable localizableWRONG_PROXY_URI_TYPE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("wrong.proxy.uri.type", new Object[]{arg0});
   }

   public static String WRONG_PROXY_URI_TYPE(Object arg0) {
      return LOCALIZER.localize(localizableWRONG_PROXY_URI_TYPE(arg0));
   }

   public static Localizable localizableNULL_EXECUTOR_SERVICE() {
      return MESSAGE_FACTORY.getMessage("null.executor.service", new Object[0]);
   }

   public static String NULL_EXECUTOR_SERVICE() {
      return LOCALIZER.localize(localizableNULL_EXECUTOR_SERVICE());
   }

   public static Localizable localizableNONINJECT_SHUTDOWN() {
      return MESSAGE_FACTORY.getMessage("noninject.shutdown", new Object[0]);
   }

   public static String NONINJECT_SHUTDOWN() {
      return LOCALIZER.localize(localizableNONINJECT_SHUTDOWN());
   }

   public static Localizable localizableCLIENT_URI_TEMPLATE_NULL() {
      return MESSAGE_FACTORY.getMessage("client.uri.template.null", new Object[0]);
   }

   public static String CLIENT_URI_TEMPLATE_NULL() {
      return LOCALIZER.localize(localizableCLIENT_URI_TEMPLATE_NULL());
   }

   public static Localizable localizableNULL_INVOCATION_BUILDER() {
      return MESSAGE_FACTORY.getMessage("null.invocation.builder", new Object[0]);
   }

   public static String NULL_INVOCATION_BUILDER() {
      return LOCALIZER.localize(localizableNULL_INVOCATION_BUILDER());
   }

   public static Localizable localizableNONINJECT_FALLBACK() {
      return MESSAGE_FACTORY.getMessage("noninject.fallback", new Object[0]);
   }

   public static String NONINJECT_FALLBACK() {
      return LOCALIZER.localize(localizableNONINJECT_FALLBACK());
   }

   public static Localizable localizableHTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY() {
      return MESSAGE_FACTORY.getMessage("httpurlconnection.replaces.get.with.entity", new Object[0]);
   }

   public static String HTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY() {
      return LOCALIZER.localize(localizableHTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY());
   }

   public static Localizable localizableERROR_LISTENER_CLOSE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.listener.close", new Object[]{arg0});
   }

   public static String ERROR_LISTENER_CLOSE(Object arg0) {
      return LOCALIZER.localize(localizableERROR_LISTENER_CLOSE(arg0));
   }

   public static Localizable localizableCLIENT_RX_PROVIDER_NULL() {
      return MESSAGE_FACTORY.getMessage("client.rx.provider.null", new Object[0]);
   }

   public static String CLIENT_RX_PROVIDER_NULL() {
      return LOCALIZER.localize(localizableCLIENT_RX_PROVIDER_NULL());
   }

   public static Localizable localizableIGNORED_ASYNC_THREADPOOL_SIZE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("ignored.async.threadpool.size", new Object[]{arg0});
   }

   public static String IGNORED_ASYNC_THREADPOOL_SIZE(Object arg0) {
      return LOCALIZER.localize(localizableIGNORED_ASYNC_THREADPOOL_SIZE(arg0));
   }

   public static Localizable localizableCHUNKED_INPUT_CLOSED() {
      return MESSAGE_FACTORY.getMessage("chunked.input.closed", new Object[0]);
   }

   public static String CHUNKED_INPUT_CLOSED() {
      return LOCALIZER.localize(localizableCHUNKED_INPUT_CLOSED());
   }

   public static Localizable localizableAUTHENTICATION_CREDENTIALS_MISSING_DIGEST() {
      return MESSAGE_FACTORY.getMessage("authentication.credentials.missing.digest", new Object[0]);
   }

   public static String AUTHENTICATION_CREDENTIALS_MISSING_DIGEST() {
      return LOCALIZER.localize(localizableAUTHENTICATION_CREDENTIALS_MISSING_DIGEST());
   }

   public static Localizable localizableNULL_KEYSTORE() {
      return MESSAGE_FACTORY.getMessage("null.keystore", new Object[0]);
   }

   public static String NULL_KEYSTORE() {
      return LOCALIZER.localize(localizableNULL_KEYSTORE());
   }

   public static Localizable localizablePOSTINVOCATION_INTERCEPTOR_MULTIPLE_RESOLVES() {
      return MESSAGE_FACTORY.getMessage("postinvocation.interceptor.multiple.resolves", new Object[0]);
   }

   public static String POSTINVOCATION_INTERCEPTOR_MULTIPLE_RESOLVES() {
      return LOCALIZER.localize(localizablePOSTINVOCATION_INTERCEPTOR_MULTIPLE_RESOLVES());
   }

   public static Localizable localizableCHUNKED_INPUT_MEDIA_TYPE_NULL() {
      return MESSAGE_FACTORY.getMessage("chunked.input.media.type.null", new Object[0]);
   }

   public static String CHUNKED_INPUT_MEDIA_TYPE_NULL() {
      return LOCALIZER.localize(localizableCHUNKED_INPUT_MEDIA_TYPE_NULL());
   }

   public static Localizable localizableNULL_TRUSTSTORE() {
      return MESSAGE_FACTORY.getMessage("null.truststore", new Object[0]);
   }

   public static String NULL_TRUSTSTORE() {
      return LOCALIZER.localize(localizableNULL_TRUSTSTORE());
   }

   public static Localizable localizableREQUEST_ENTITY_ALREADY_WRITTEN() {
      return MESSAGE_FACTORY.getMessage("request.entity.already.written", new Object[0]);
   }

   public static String REQUEST_ENTITY_ALREADY_WRITTEN() {
      return LOCALIZER.localize(localizableREQUEST_ENTITY_ALREADY_WRITTEN());
   }

   public static Localizable localizableNONINJECT_REQUESTSCOPE_CREATED() {
      return MESSAGE_FACTORY.getMessage("noninject.requestscope.created", new Object[0]);
   }

   public static String NONINJECT_REQUESTSCOPE_CREATED() {
      return LOCALIZER.localize(localizableNONINJECT_REQUESTSCOPE_CREATED());
   }

   public static Localizable localizableERROR_SHUTDOWNHOOK_CLOSE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.shutdownhook.close", new Object[]{arg0});
   }

   public static String ERROR_SHUTDOWNHOOK_CLOSE(Object arg0) {
      return LOCALIZER.localize(localizableERROR_SHUTDOWNHOOK_CLOSE(arg0));
   }

   public static Localizable localizableNULL_KEYSTORE_PASWORD() {
      return MESSAGE_FACTORY.getMessage("null.keystore.pasword", new Object[0]);
   }

   public static String NULL_KEYSTORE_PASWORD() {
      return LOCALIZER.localize(localizableNULL_KEYSTORE_PASWORD());
   }

   public static Localizable localizablePOSTINVOCATION_INTERCEPTOR_RESOLVE() {
      return MESSAGE_FACTORY.getMessage("postinvocation.interceptor.resolve", new Object[0]);
   }

   public static String POSTINVOCATION_INTERCEPTOR_RESOLVE() {
      return LOCALIZER.localize(localizablePOSTINVOCATION_INTERCEPTOR_RESOLVE());
   }

   public static Localizable localizableNULL_CONNECTOR_PROVIDER() {
      return MESSAGE_FACTORY.getMessage("null.connector.provider", new Object[0]);
   }

   public static String NULL_CONNECTOR_PROVIDER() {
      return LOCALIZER.localize(localizableNULL_CONNECTOR_PROVIDER());
   }

   public static Localizable localizableERROR_HTTP_METHOD_ENTITY_NULL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.http.method.entity.null", new Object[]{arg0});
   }

   public static String ERROR_HTTP_METHOD_ENTITY_NULL(Object arg0) {
      return LOCALIZER.localize(localizableERROR_HTTP_METHOD_ENTITY_NULL(arg0));
   }

   public static Localizable localizableDIGEST_FILTER_QOP_UNSUPPORTED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("digest.filter.qop.unsupported", new Object[]{arg0});
   }

   public static String DIGEST_FILTER_QOP_UNSUPPORTED(Object arg0) {
      return LOCALIZER.localize(localizableDIGEST_FILTER_QOP_UNSUPPORTED(arg0));
   }

   public static Localizable localizableCLIENT_URI_NULL() {
      return MESSAGE_FACTORY.getMessage("client.uri.null", new Object[0]);
   }

   public static String CLIENT_URI_NULL() {
      return LOCALIZER.localize(localizableCLIENT_URI_NULL());
   }

   public static Localizable localizableNONINJECT_NO_BINDING(Object arg0) {
      return MESSAGE_FACTORY.getMessage("noninject.no.binding", new Object[]{arg0});
   }

   public static String NONINJECT_NO_BINDING(Object arg0) {
      return LOCALIZER.localize(localizableNONINJECT_NO_BINDING(arg0));
   }

   public static Localizable localizablePREINVOCATION_INTERCEPTOR_EXCEPTION() {
      return MESSAGE_FACTORY.getMessage("preinvocation.interceptor.exception", new Object[0]);
   }

   public static String PREINVOCATION_INTERCEPTOR_EXCEPTION() {
      return LOCALIZER.localize(localizablePREINVOCATION_INTERCEPTOR_EXCEPTION());
   }

   public static Localizable localizableNULL_INPUT_PARAMETER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("null.input.parameter", new Object[]{arg0});
   }

   public static String NULL_INPUT_PARAMETER(Object arg0) {
      return LOCALIZER.localize(localizableNULL_INPUT_PARAMETER(arg0));
   }

   public static Localizable localizableSNI_URI_REPLACED(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("sni.uri.replaced", new Object[]{arg0, arg1});
   }

   public static String SNI_URI_REPLACED(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableSNI_URI_REPLACED(arg0, arg1));
   }

   public static Localizable localizableERROR_REQUEST_CANCELLED() {
      return MESSAGE_FACTORY.getMessage("error.request.cancelled", new Object[0]);
   }

   public static String ERROR_REQUEST_CANCELLED() {
      return LOCALIZER.localize(localizableERROR_REQUEST_CANCELLED());
   }

   public static Localizable localizableSNI_ON_SSLSOCKET() {
      return MESSAGE_FACTORY.getMessage("sni.on.sslsocket", new Object[0]);
   }

   public static String SNI_ON_SSLSOCKET() {
      return LOCALIZER.localize(localizableSNI_ON_SSLSOCKET());
   }

   public static Localizable localizableCOLLECTION_UPDATER_TYPE_UNSUPPORTED() {
      return MESSAGE_FACTORY.getMessage("collection.updater.type.unsupported", new Object[0]);
   }

   public static String COLLECTION_UPDATER_TYPE_UNSUPPORTED() {
      return LOCALIZER.localize(localizableCOLLECTION_UPDATER_TYPE_UNSUPPORTED());
   }

   public static Localizable localizableERROR_HTTP_METHOD_ENTITY_NOT_NULL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.http.method.entity.not.null", new Object[]{arg0});
   }

   public static String ERROR_HTTP_METHOD_ENTITY_NOT_NULL(Object arg0) {
      return LOCALIZER.localize(localizableERROR_HTTP_METHOD_ENTITY_NOT_NULL(arg0));
   }

   public static Localizable localizableNONINJECT_UNSATISFIED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("noninject.unsatisfied", new Object[]{arg0});
   }

   public static String NONINJECT_UNSATISFIED(Object arg0) {
      return LOCALIZER.localize(localizableNONINJECT_UNSATISFIED(arg0));
   }

   public static Localizable localizableCLIENT_RESPONSE_STATUS_NULL() {
      return MESSAGE_FACTORY.getMessage("client.response.status.null", new Object[0]);
   }

   public static String CLIENT_RESPONSE_STATUS_NULL() {
      return LOCALIZER.localize(localizableCLIENT_RESPONSE_STATUS_NULL());
   }

   public static Localizable localizableCLIENT_INVOCATION_LINK_NULL() {
      return MESSAGE_FACTORY.getMessage("client.invocation.link.null", new Object[0]);
   }

   public static String CLIENT_INVOCATION_LINK_NULL() {
      return LOCALIZER.localize(localizableCLIENT_INVOCATION_LINK_NULL());
   }

   public static Localizable localizablePREINVOCATION_INTERCEPTOR_ABORT_WITH() {
      return MESSAGE_FACTORY.getMessage("preinvocation.interceptor.abortWith", new Object[0]);
   }

   public static String PREINVOCATION_INTERCEPTOR_ABORT_WITH() {
      return LOCALIZER.localize(localizablePREINVOCATION_INTERCEPTOR_ABORT_WITH());
   }

   public static Localizable localizablePOSTINVOCATION_INTERCEPTOR_EXCEPTION() {
      return MESSAGE_FACTORY.getMessage("postinvocation.interceptor.exception", new Object[0]);
   }

   public static String POSTINVOCATION_INTERCEPTOR_EXCEPTION() {
      return LOCALIZER.localize(localizablePOSTINVOCATION_INTERCEPTOR_EXCEPTION());
   }

   public static Localizable localizableEXCEPTION_SUPPRESSED() {
      return MESSAGE_FACTORY.getMessage("exception.suppressed", new Object[0]);
   }

   public static String EXCEPTION_SUPPRESSED() {
      return LOCALIZER.localize(localizableEXCEPTION_SUPPRESSED());
   }

   public static Localizable localizableAUTHENTICATION_CREDENTIALS_NOT_PROVIDED_BASIC() {
      return MESSAGE_FACTORY.getMessage("authentication.credentials.not.provided.basic", new Object[0]);
   }

   public static String AUTHENTICATION_CREDENTIALS_NOT_PROVIDED_BASIC() {
      return LOCALIZER.localize(localizableAUTHENTICATION_CREDENTIALS_NOT_PROVIDED_BASIC());
   }

   public static Localizable localizableRESTRICTED_HEADER_POSSIBLY_IGNORED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("restricted.header.possibly.ignored", new Object[]{arg0});
   }

   public static String RESTRICTED_HEADER_POSSIBLY_IGNORED(Object arg0) {
      return LOCALIZER.localize(localizableRESTRICTED_HEADER_POSSIBLY_IGNORED(arg0));
   }

   public static Localizable localizableNULL_SCHEDULED_EXECUTOR_SERVICE() {
      return MESSAGE_FACTORY.getMessage("null.scheduled.executor.service", new Object[0]);
   }

   public static String NULL_SCHEDULED_EXECUTOR_SERVICE() {
      return LOCALIZER.localize(localizableNULL_SCHEDULED_EXECUTOR_SERVICE());
   }

   public static Localizable localizableRESPONSE_TYPE_IS_NULL() {
      return MESSAGE_FACTORY.getMessage("response.type.is.null", new Object[0]);
   }

   public static String RESPONSE_TYPE_IS_NULL() {
      return LOCALIZER.localize(localizableRESPONSE_TYPE_IS_NULL());
   }

   public static Localizable localizableSNI_UPDATE_SSLPARAMS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("sni.update.sslparams", new Object[]{arg0});
   }

   public static String SNI_UPDATE_SSLPARAMS(Object arg0) {
      return LOCALIZER.localize(localizableSNI_UPDATE_SSLPARAMS(arg0));
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.client.internal.localization", locale);
      }
   }
}
