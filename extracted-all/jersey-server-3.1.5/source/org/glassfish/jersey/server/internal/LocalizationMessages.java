package org.glassfish.jersey.server.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.server.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.server.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableRESOURCE_MERGE_CONFLICT_LOCATORS(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("resource.merge.conflict.locators", new Object[]{arg0, arg1, arg2});
   }

   public static String RESOURCE_MERGE_CONFLICT_LOCATORS(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableRESOURCE_MERGE_CONFLICT_LOCATORS(arg0, arg1, arg2));
   }

   public static Localizable localizableRESOURCE_MODEL_VALIDATION_FAILED_AT_INIT() {
      return MESSAGE_FACTORY.getMessage("resource.model.validation.failed.at.init", new Object[0]);
   }

   public static String RESOURCE_MODEL_VALIDATION_FAILED_AT_INIT() {
      return LOCALIZER.localize(localizableRESOURCE_MODEL_VALIDATION_FAILED_AT_INIT());
   }

   public static Localizable localizableWARNING_MONITORING_FEATURE_DISABLED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("warning.monitoring.feature.disabled", new Object[]{arg0});
   }

   public static String WARNING_MONITORING_FEATURE_DISABLED(Object arg0) {
      return LOCALIZER.localize(localizableWARNING_MONITORING_FEATURE_DISABLED(arg0));
   }

   public static Localizable localizableERROR_MARSHALLING_JAXB(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.marshalling.jaxb", new Object[]{arg0});
   }

   public static String ERROR_MARSHALLING_JAXB(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MARSHALLING_JAXB(arg0));
   }

   public static Localizable localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("multiple.http.method.designators", new Object[]{arg0, arg1});
   }

   public static String MULTIPLE_HTTP_METHOD_DESIGNATORS(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(arg0, arg1));
   }

   public static Localizable localizablePARAM_NULL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("param.null", new Object[]{arg0});
   }

   public static String PARAM_NULL(Object arg0) {
      return LOCALIZER.localize(localizablePARAM_NULL(arg0));
   }

   public static Localizable localizableERROR_MSG(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.msg", new Object[]{arg0});
   }

   public static String ERROR_MSG(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MSG(arg0));
   }

   public static Localizable localizableRESOURCE_IMPLEMENTS_PROVIDER(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("resource.implements.provider", new Object[]{arg0, arg1});
   }

   public static String RESOURCE_IMPLEMENTS_PROVIDER(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableRESOURCE_IMPLEMENTS_PROVIDER(arg0, arg1));
   }

   public static Localizable localizableERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE() {
      return MESSAGE_FACTORY.getMessage("error.request.set.entity.stream.in.response.phase", new Object[0]);
   }

   public static String ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE() {
      return LOCALIZER.localize(localizableERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE());
   }

   public static Localizable localizableEVENT_SINK_RETURNS_TYPE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("event.sink.returns.type", new Object[]{arg0});
   }

   public static String EVENT_SINK_RETURNS_TYPE(Object arg0) {
      return LOCALIZER.localize(localizableEVENT_SINK_RETURNS_TYPE(arg0));
   }

   public static Localizable localizableCOLLECTION_EXTRACTOR_TYPE_UNSUPPORTED() {
      return MESSAGE_FACTORY.getMessage("collection.extractor.type.unsupported", new Object[0]);
   }

   public static String COLLECTION_EXTRACTOR_TYPE_UNSUPPORTED() {
      return LOCALIZER.localize(localizableCOLLECTION_EXTRACTOR_TYPE_UNSUPPORTED());
   }

   public static Localizable localizableUSER_NOT_AUTHORIZED() {
      return MESSAGE_FACTORY.getMessage("user.not.authorized", new Object[0]);
   }

   public static String USER_NOT_AUTHORIZED() {
      return LOCALIZER.localize(localizableUSER_NOT_AUTHORIZED());
   }

   public static Localizable localizableSUSPEND_SCHEDULING_ERROR() {
      return MESSAGE_FACTORY.getMessage("suspend.scheduling.error", new Object[0]);
   }

   public static String SUSPEND_SCHEDULING_ERROR() {
      return LOCALIZER.localize(localizableSUSPEND_SCHEDULING_ERROR());
   }

   public static Localizable localizableERROR_RESPONSE_ALREADY_COMMITED() {
      return MESSAGE_FACTORY.getMessage("error.response.already.commited", new Object[0]);
   }

   public static String ERROR_RESPONSE_ALREADY_COMMITED() {
      return LOCALIZER.localize(localizableERROR_RESPONSE_ALREADY_COMMITED());
   }

   public static Localizable localizableWADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(Object arg0, Object arg1, Object arg2, Object arg3) {
      return MESSAGE_FACTORY.getMessage("wadl.resourcedoc.ambiguous.method.entries", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String WADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(Object arg0, Object arg1, Object arg2, Object arg3) {
      return LOCALIZER.localize(localizableWADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableAMBIGUOUS_RMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3) {
      return MESSAGE_FACTORY.getMessage("ambiguous.rms.out", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String AMBIGUOUS_RMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3) {
      return LOCALIZER.localize(localizableAMBIGUOUS_RMS_OUT(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableMETHOD_EMPTY_PATH_ANNOTATION(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("method.empty.path.annotation", new Object[]{arg0, arg1});
   }

   public static String METHOD_EMPTY_PATH_ANNOTATION(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableMETHOD_EMPTY_PATH_ANNOTATION(arg0, arg1));
   }

   public static Localizable localizableJAR_SCANNER_UNABLE_TO_READ_ENTRY() {
      return MESSAGE_FACTORY.getMessage("jar.scanner.unable.to.read.entry", new Object[0]);
   }

   public static String JAR_SCANNER_UNABLE_TO_READ_ENTRY() {
      return LOCALIZER.localize(localizableJAR_SCANNER_UNABLE_TO_READ_ENTRY());
   }

   public static Localizable localizableSUSPEND_NOT_SUSPENDED() {
      return MESSAGE_FACTORY.getMessage("suspend.not.suspended", new Object[0]);
   }

   public static String SUSPEND_NOT_SUSPENDED() {
      return LOCALIZER.localize(localizableSUSPEND_NOT_SUSPENDED());
   }

   public static Localizable localizableERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE() {
      return MESSAGE_FACTORY.getMessage("error.suspending.chunked.output.response", new Object[0]);
   }

   public static String ERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE() {
      return LOCALIZER.localize(localizableERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE());
   }

   public static Localizable localizableWADL_JAXB_CONTEXT_FALLBACK() {
      return MESSAGE_FACTORY.getMessage("wadl.jaxb.context.fallback", new Object[0]);
   }

   public static String WADL_JAXB_CONTEXT_FALLBACK() {
      return LOCALIZER.localize(localizableWADL_JAXB_CONTEXT_FALLBACK());
   }

   public static Localizable localizableCALLBACK_ARRAY_NULL() {
      return MESSAGE_FACTORY.getMessage("callback.array.null", new Object[0]);
   }

   public static String CALLBACK_ARRAY_NULL() {
      return LOCALIZER.localize(localizableCALLBACK_ARRAY_NULL());
   }

   public static Localizable localizableERROR_CLOSING_FINDER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.closing.finder", new Object[]{arg0});
   }

   public static String ERROR_CLOSING_FINDER(Object arg0) {
      return LOCALIZER.localize(localizableERROR_CLOSING_FINDER(arg0));
   }

   public static Localizable localizableAMBIGUOUS_SRLS_PATH_PATTERN(Object arg0) {
      return MESSAGE_FACTORY.getMessage("ambiguous.srls.pathPattern", new Object[]{arg0});
   }

   public static String AMBIGUOUS_SRLS_PATH_PATTERN(Object arg0) {
      return LOCALIZER.localize(localizableAMBIGUOUS_SRLS_PATH_PATTERN(arg0));
   }

   public static Localizable localizableERROR_PARAMETER_INVALID_CHAR_VALUE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.parameter.invalid.char.value", new Object[]{arg0});
   }

   public static String ERROR_PARAMETER_INVALID_CHAR_VALUE(Object arg0) {
      return LOCALIZER.localize(localizableERROR_PARAMETER_INVALID_CHAR_VALUE(arg0));
   }

   public static Localizable localizableERROR_RESOURCE_JAVA_METHOD_INVOCATION() {
      return MESSAGE_FACTORY.getMessage("error.resource.java.method.invocation", new Object[0]);
   }

   public static String ERROR_RESOURCE_JAVA_METHOD_INVOCATION() {
      return LOCALIZER.localize(localizableERROR_RESOURCE_JAVA_METHOD_INVOCATION());
   }

   public static Localizable localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("sub.res.method.treated.as.res.method", new Object[]{arg0, arg1});
   }

   public static String SUB_RES_METHOD_TREATED_AS_RES_METHOD(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(arg0, arg1));
   }

   public static Localizable localizableWARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("warning.monitoring.mbeans.bean.already.registered", new Object[]{arg0});
   }

   public static String WARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(Object arg0) {
      return LOCALIZER.localize(localizableWARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(arg0));
   }

   public static Localizable localizableCOULD_NOT_BIND_TO_ANY_PORT() {
      return MESSAGE_FACTORY.getMessage("could.not.bind.to.any.port", new Object[0]);
   }

   public static String COULD_NOT_BIND_TO_ANY_PORT() {
      return LOCALIZER.localize(localizableCOULD_NOT_BIND_TO_ANY_PORT());
   }

   public static Localizable localizableERROR_MONITORING_STATISTICS_LISTENER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.monitoring.statistics.listener", new Object[]{arg0});
   }

   public static String ERROR_MONITORING_STATISTICS_LISTENER(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MONITORING_STATISTICS_LISTENER(arg0));
   }

   public static Localizable localizableERROR_WADL_GENERATOR_CONFIGURE() {
      return MESSAGE_FACTORY.getMessage("error.wadl.generator.configure", new Object[0]);
   }

   public static String ERROR_WADL_GENERATOR_CONFIGURE() {
      return LOCALIZER.localize(localizableERROR_WADL_GENERATOR_CONFIGURE());
   }

   public static Localizable localizableRESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(Object arg0) {
      return MESSAGE_FACTORY.getMessage("resource.updated.method.does.not.exist", new Object[]{arg0});
   }

   public static String RESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(Object arg0) {
      return LOCALIZER.localize(localizableRESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(arg0));
   }

   public static Localizable localizableERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE() {
      return MESSAGE_FACTORY.getMessage("error.request.set.security.context.in.response.phase", new Object[0]);
   }

   public static String ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE() {
      return LOCALIZER.localize(localizableERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE());
   }

   public static Localizable localizableFORM_PARAM_CONTENT_TYPE_ERROR() {
      return MESSAGE_FACTORY.getMessage("form.param.content-type.error", new Object[0]);
   }

   public static String FORM_PARAM_CONTENT_TYPE_ERROR() {
      return LOCALIZER.localize(localizableFORM_PARAM_CONTENT_TYPE_ERROR());
   }

   public static Localizable localizableEXCEPTION_MAPPING_WAE_NO_ENTITY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("exception.mapping.wae.no.entity", new Object[]{arg0});
   }

   public static String EXCEPTION_MAPPING_WAE_NO_ENTITY(Object arg0) {
      return LOCALIZER.localize(localizableEXCEPTION_MAPPING_WAE_NO_ENTITY(arg0));
   }

   public static Localizable localizableCHUNKED_OUTPUT_CLOSED() {
      return MESSAGE_FACTORY.getMessage("chunked.output.closed", new Object[0]);
   }

   public static String CHUNKED_OUTPUT_CLOSED() {
      return LOCALIZER.localize(localizableCHUNKED_OUTPUT_CLOSED());
   }

   public static Localizable localizableLOGGING_GLOBAL_REQUEST_FILTERS() {
      return MESSAGE_FACTORY.getMessage("logging.global.request.filters", new Object[0]);
   }

   public static String LOGGING_GLOBAL_REQUEST_FILTERS() {
      return LOCALIZER.localize(localizableLOGGING_GLOBAL_REQUEST_FILTERS());
   }

   public static Localizable localizableRESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("resource.contains.res.methods.and.locator", new Object[]{arg0, arg1});
   }

   public static String RESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableRESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(arg0, arg1));
   }

   public static Localizable localizableWADL_FEATURE_DISABLED_NOJAXB() {
      return MESSAGE_FACTORY.getMessage("wadl.feature.disabled.nojaxb", new Object[0]);
   }

   public static String WADL_FEATURE_DISABLED_NOJAXB() {
      return LOCALIZER.localize(localizableWADL_FEATURE_DISABLED_NOJAXB());
   }

   public static Localizable localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("new.ar.created.by.introspection.modeler", new Object[]{arg0});
   }

   public static String NEW_AR_CREATED_BY_INTROSPECTION_MODELER(Object arg0) {
      return LOCALIZER.localize(localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(arg0));
   }

   public static Localizable localizableSUBRES_LOC_RETURNS_VOID(Object arg0) {
      return MESSAGE_FACTORY.getMessage("subres.loc.returns.void", new Object[]{arg0});
   }

   public static String SUBRES_LOC_RETURNS_VOID(Object arg0) {
      return LOCALIZER.localize(localizableSUBRES_LOC_RETURNS_VOID(arg0));
   }

   public static Localizable localizableERROR_MONITORING_QUEUE_FLOODED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.monitoring.queue.flooded", new Object[]{arg0});
   }

   public static String ERROR_MONITORING_QUEUE_FLOODED(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MONITORING_QUEUE_FLOODED(arg0));
   }

   public static Localizable localizableSINGLETON_INJECTS_PARAMETER(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("singleton.injects.parameter", new Object[]{arg0, arg1});
   }

   public static String SINGLETON_INJECTS_PARAMETER(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableSINGLETON_INJECTS_PARAMETER(arg0, arg1));
   }

   public static Localizable localizableSUBRES_LOC_HAS_ENTITY_PARAM(Object arg0) {
      return MESSAGE_FACTORY.getMessage("subres.loc.has.entity.param", new Object[]{arg0});
   }

   public static String SUBRES_LOC_HAS_ENTITY_PARAM(Object arg0) {
      return LOCALIZER.localize(localizableSUBRES_LOC_HAS_ENTITY_PARAM(arg0));
   }

   public static Localizable localizableERROR_WADL_EXTERNAL_GRAMMAR() {
      return MESSAGE_FACTORY.getMessage("error.wadl.external.grammar", new Object[0]);
   }

   public static String ERROR_WADL_EXTERNAL_GRAMMAR() {
      return LOCALIZER.localize(localizableERROR_WADL_EXTERNAL_GRAMMAR());
   }

   public static Localizable localizableINIT_MSG(Object arg0) {
      return MESSAGE_FACTORY.getMessage("init.msg", new Object[]{arg0});
   }

   public static String INIT_MSG(Object arg0) {
      return LOCALIZER.localize(localizableINIT_MSG(arg0));
   }

   public static Localizable localizableERROR_REQUEST_ABORT_IN_RESPONSE_PHASE() {
      return MESSAGE_FACTORY.getMessage("error.request.abort.in.response.phase", new Object[0]);
   }

   public static String ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE() {
      return LOCALIZER.localize(localizableERROR_REQUEST_ABORT_IN_RESPONSE_PHASE());
   }

   public static Localizable localizableERROR_MONITORING_QUEUE_RESPONSE() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.queue.response", new Object[0]);
   }

   public static String ERROR_MONITORING_QUEUE_RESPONSE() {
      return LOCALIZER.localize(localizableERROR_MONITORING_QUEUE_RESPONSE());
   }

   public static Localizable localizableERROR_SCANNING_CLASS_NOT_FOUND(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.scanning.class.not.found", new Object[]{arg0});
   }

   public static String ERROR_SCANNING_CLASS_NOT_FOUND(Object arg0) {
      return LOCALIZER.localize(localizableERROR_SCANNING_CLASS_NOT_FOUND(arg0));
   }

   public static Localizable localizableRESOURCE_AMBIGUOUS(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("resource.ambiguous", new Object[]{arg0, arg1, arg2});
   }

   public static String RESOURCE_AMBIGUOUS(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableRESOURCE_AMBIGUOUS(arg0, arg1, arg2));
   }

   public static Localizable localizableEXCEPTION_MAPPING_START() {
      return MESSAGE_FACTORY.getMessage("exception.mapping.start", new Object[0]);
   }

   public static String EXCEPTION_MAPPING_START() {
      return LOCALIZER.localize(localizableEXCEPTION_MAPPING_START());
   }

   public static Localizable localizableERROR_VALIDATION_SUBRESOURCE() {
      return MESSAGE_FACTORY.getMessage("error.validation.subresource", new Object[0]);
   }

   public static String ERROR_VALIDATION_SUBRESOURCE() {
      return LOCALIZER.localize(localizableERROR_VALIDATION_SUBRESOURCE());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_METHOD(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.method", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_METHOD(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_METHOD(arg0, arg1));
   }

   public static Localizable localizableSECURITY_CONTEXT_WAS_NOT_SET() {
      return MESSAGE_FACTORY.getMessage("security.context.was.not.set", new Object[0]);
   }

   public static String SECURITY_CONTEXT_WAS_NOT_SET() {
      return LOCALIZER.localize(localizableSECURITY_CONTEXT_WAS_NOT_SET());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_REQUEST(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.request", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_REQUEST(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_REQUEST(arg0, arg1));
   }

   public static Localizable localizableEXCEPTION_MAPPING_WAE_ENTITY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("exception.mapping.wae.entity", new Object[]{arg0});
   }

   public static String EXCEPTION_MAPPING_WAE_ENTITY(Object arg0) {
      return LOCALIZER.localize(localizableEXCEPTION_MAPPING_WAE_ENTITY(arg0));
   }

   public static Localizable localizableLOGGING_NAME_BOUND_REQUEST_FILTERS() {
      return MESSAGE_FACTORY.getMessage("logging.name.bound.request.filters", new Object[0]);
   }

   public static String LOGGING_NAME_BOUND_REQUEST_FILTERS() {
      return LOCALIZER.localize(localizableLOGGING_NAME_BOUND_REQUEST_FILTERS());
   }

   public static Localizable localizableERROR_MONITORING_QUEUE_MAPPER() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.queue.mapper", new Object[0]);
   }

   public static String ERROR_MONITORING_QUEUE_MAPPER() {
      return LOCALIZER.localize(localizableERROR_MONITORING_QUEUE_MAPPER());
   }

   public static Localizable localizableWADL_DOC_SIMPLE_WADL(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("wadl.doc.simple.wadl", new Object[]{arg0, arg1});
   }

   public static String WADL_DOC_SIMPLE_WADL(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableWADL_DOC_SIMPLE_WADL(arg0, arg1));
   }

   public static Localizable localizableLOGGING_NAME_BOUND_READER_INTERCEPTORS() {
      return MESSAGE_FACTORY.getMessage("logging.name.bound.reader.interceptors", new Object[0]);
   }

   public static String LOGGING_NAME_BOUND_READER_INTERCEPTORS() {
      return LOCALIZER.localize(localizableLOGGING_NAME_BOUND_READER_INTERCEPTORS());
   }

   public static Localizable localizableLOGGING_MESSAGE_BODY_READERS() {
      return MESSAGE_FACTORY.getMessage("logging.message.body.readers", new Object[0]);
   }

   public static String LOGGING_MESSAGE_BODY_READERS() {
      return LOCALIZER.localize(localizableLOGGING_MESSAGE_BODY_READERS());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_PARAM(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.param", new Object[]{arg0, arg1, arg2});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_PARAM(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_PARAM(arg0, arg1, arg2));
   }

   public static Localizable localizableNON_PUB_SUB_RES_METHOD(Object arg0) {
      return MESSAGE_FACTORY.getMessage("non.pub.sub.res.method", new Object[]{arg0});
   }

   public static String NON_PUB_SUB_RES_METHOD(Object arg0) {
      return LOCALIZER.localize(localizableNON_PUB_SUB_RES_METHOD(arg0));
   }

   public static Localizable localizableWARNING_MSG(Object arg0) {
      return MESSAGE_FACTORY.getMessage("warning.msg", new Object[]{arg0});
   }

   public static String WARNING_MSG(Object arg0) {
      return LOCALIZER.localize(localizableWARNING_MSG(arg0));
   }

   public static Localizable localizableWARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(Object arg0) {
      return MESSAGE_FACTORY.getMessage("warning.too.many.external.req.scopes", new Object[]{arg0});
   }

   public static String WARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(Object arg0) {
      return LOCALIZER.localize(localizableWARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(arg0));
   }

   public static Localizable localizableRESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("resource.multiple.scope.annotations", new Object[]{arg0});
   }

   public static String RESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(Object arg0) {
      return LOCALIZER.localize(localizableRESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(arg0));
   }

   public static Localizable localizableUNABLE_TO_LOAD_CLASS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("unable.to.load.class", new Object[]{arg0});
   }

   public static String UNABLE_TO_LOAD_CLASS(Object arg0) {
      return LOCALIZER.localize(localizableUNABLE_TO_LOAD_CLASS(arg0));
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.resource", new Object[]{arg0});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE(Object arg0) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE(arg0));
   }

   public static Localizable localizableGET_CONSUMES_ENTITY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("get.consumes.entity", new Object[]{arg0});
   }

   public static String GET_CONSUMES_ENTITY(Object arg0) {
      return LOCALIZER.localize(localizableGET_CONSUMES_ENTITY(arg0));
   }

   public static Localizable localizableMETHOD_UNEXPECTED_ANNOTATION(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("method.unexpected.annotation", new Object[]{arg0, arg1, arg2});
   }

   public static String METHOD_UNEXPECTED_ANNOTATION(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableMETHOD_UNEXPECTED_ANNOTATION(arg0, arg1, arg2));
   }

   public static Localizable localizableWARNING_MONITORING_FEATURE_ENABLED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("warning.monitoring.feature.enabled", new Object[]{arg0});
   }

   public static String WARNING_MONITORING_FEATURE_ENABLED(Object arg0) {
      return LOCALIZER.localize(localizableWARNING_MONITORING_FEATURE_ENABLED(arg0));
   }

   public static Localizable localizableLOGGING_DYNAMIC_FEATURES() {
      return MESSAGE_FACTORY.getMessage("logging.dynamic.features", new Object[0]);
   }

   public static String LOGGING_DYNAMIC_FEATURES() {
      return LOCALIZER.localize(localizableLOGGING_DYNAMIC_FEATURES());
   }

   public static Localizable localizableLOGGING_GLOBAL_WRITER_INTERCEPTORS() {
      return MESSAGE_FACTORY.getMessage("logging.global.writer.interceptors", new Object[0]);
   }

   public static String LOGGING_GLOBAL_WRITER_INTERCEPTORS() {
      return LOCALIZER.localize(localizableLOGGING_GLOBAL_WRITER_INTERCEPTORS());
   }

   public static Localizable localizableLOGGING_APPLICATION_INITIALIZED() {
      return MESSAGE_FACTORY.getMessage("logging.application.initialized", new Object[0]);
   }

   public static String LOGGING_APPLICATION_INITIALIZED() {
      return LOCALIZER.localize(localizableLOGGING_APPLICATION_INITIALIZED());
   }

   public static Localizable localizableERROR_WRITING_RESPONSE_ENTITY_CHUNK() {
      return MESSAGE_FACTORY.getMessage("error.writing.response.entity.chunk", new Object[0]);
   }

   public static String ERROR_WRITING_RESPONSE_ENTITY_CHUNK() {
      return LOCALIZER.localize(localizableERROR_WRITING_RESPONSE_ENTITY_CHUNK());
   }

   public static Localizable localizableERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.sub.resource.locator.more.resources", new Object[]{arg0});
   }

   public static String ERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(Object arg0) {
      return LOCALIZER.localize(localizableERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(arg0));
   }

   public static Localizable localizableAMBIGUOUS_SRLS(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("ambiguous.srls", new Object[]{arg0, arg1});
   }

   public static String AMBIGUOUS_SRLS(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableAMBIGUOUS_SRLS(arg0, arg1));
   }

   public static Localizable localizableMETHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("method.parameter.cannot.be.null.or.empty", new Object[]{arg0});
   }

   public static String METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(Object arg0) {
      return LOCALIZER.localize(localizableMETHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(arg0));
   }

   public static Localizable localizableGET_CONSUMES_FORM_PARAM(Object arg0) {
      return MESSAGE_FACTORY.getMessage("get.consumes.form.param", new Object[]{arg0});
   }

   public static String GET_CONSUMES_FORM_PARAM(Object arg0) {
      return LOCALIZER.localize(localizableGET_CONSUMES_FORM_PARAM(arg0));
   }

   public static Localizable localizableERROR_UNMARSHALLING_JAXB(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.unmarshalling.jaxb", new Object[]{arg0});
   }

   public static String ERROR_UNMARSHALLING_JAXB(Object arg0) {
      return LOCALIZER.localize(localizableERROR_UNMARSHALLING_JAXB(arg0));
   }

   public static Localizable localizableMETHOD_PARAMETER_CANNOT_BE_NULL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("method.parameter.cannot.be.null", new Object[]{arg0});
   }

   public static String METHOD_PARAMETER_CANNOT_BE_NULL(Object arg0) {
      return LOCALIZER.localize(localizableMETHOD_PARAMETER_CANNOT_BE_NULL(arg0));
   }

   public static Localizable localizableWADL_FEATURE_DISABLED() {
      return MESSAGE_FACTORY.getMessage("wadl.feature.disabled", new Object[0]);
   }

   public static String WADL_FEATURE_DISABLED() {
      return LOCALIZER.localize(localizableWADL_FEATURE_DISABLED());
   }

   public static Localizable localizableSUBRES_LOC_CACHE_LOAD_FAILED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("subres.loc.cache.load.failed", new Object[]{arg0});
   }

   public static String SUBRES_LOC_CACHE_LOAD_FAILED(Object arg0) {
      return LOCALIZER.localize(localizableSUBRES_LOC_CACHE_LOAD_FAILED(arg0));
   }

   public static Localizable localizableERROR_PARAMETER_TYPE_PROCESSING(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.parameter.type.processing", new Object[]{arg0});
   }

   public static String ERROR_PARAMETER_TYPE_PROCESSING(Object arg0) {
      return LOCALIZER.localize(localizableERROR_PARAMETER_TYPE_PROCESSING(arg0));
   }

   public static Localizable localizableERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR() {
      return MESSAGE_FACTORY.getMessage("error.exception.mapping.processed.response.error", new Object[0]);
   }

   public static String ERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR() {
      return LOCALIZER.localize(localizableERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR());
   }

   public static Localizable localizableERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.mbeans.unregistration.destroy", new Object[0]);
   }

   public static String ERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY() {
      return LOCALIZER.localize(localizableERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY());
   }

   public static Localizable localizableERROR_WADL_RESOURCE_APPLICATION_WADL() {
      return MESSAGE_FACTORY.getMessage("error.wadl.resource.application.wadl", new Object[0]);
   }

   public static String ERROR_WADL_RESOURCE_APPLICATION_WADL() {
      return LOCALIZER.localize(localizableERROR_WADL_RESOURCE_APPLICATION_WADL());
   }

   public static Localizable localizableFORM_PARAM_METHOD_ERROR() {
      return MESSAGE_FACTORY.getMessage("form.param.method.error", new Object[0]);
   }

   public static String FORM_PARAM_METHOD_ERROR() {
      return LOCALIZER.localize(localizableFORM_PARAM_METHOD_ERROR());
   }

   public static Localizable localizableINJECTED_WEBTARGET_URI_INVALID(Object arg0) {
      return MESSAGE_FACTORY.getMessage("injected.webtarget.uri.invalid", new Object[]{arg0});
   }

   public static String INJECTED_WEBTARGET_URI_INVALID(Object arg0) {
      return LOCALIZER.localize(localizableINJECTED_WEBTARGET_URI_INVALID(arg0));
   }

   public static Localizable localizableAMBIGUOUS_NON_ANNOTATED_PARAMETER(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("ambiguous.non.annotated.parameter", new Object[]{arg0, arg1});
   }

   public static String AMBIGUOUS_NON_ANNOTATED_PARAMETER(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableAMBIGUOUS_NON_ANNOTATED_PARAMETER(arg0, arg1));
   }

   public static Localizable localizableNON_INSTANTIABLE_COMPONENT(Object arg0) {
      return MESSAGE_FACTORY.getMessage("non.instantiable.component", new Object[]{arg0});
   }

   public static String NON_INSTANTIABLE_COMPONENT(Object arg0) {
      return LOCALIZER.localize(localizableNON_INSTANTIABLE_COMPONENT(arg0));
   }

   public static Localizable localizableLOGGING_GLOBAL_RESPONSE_FILTERS() {
      return MESSAGE_FACTORY.getMessage("logging.global.response.filters", new Object[0]);
   }

   public static String LOGGING_GLOBAL_RESPONSE_FILTERS() {
      return LOCALIZER.localize(localizableLOGGING_GLOBAL_RESPONSE_FILTERS());
   }

   public static Localizable localizableSUSPEND_HANDLER_EXECUTION_FAILED() {
      return MESSAGE_FACTORY.getMessage("suspend.handler.execution.failed", new Object[0]);
   }

   public static String SUSPEND_HANDLER_EXECUTION_FAILED() {
      return LOCALIZER.localize(localizableSUSPEND_HANDLER_EXECUTION_FAILED());
   }

   public static Localizable localizableRESOURCE_ADD_CHILD_ALREADY_CHILD() {
      return MESSAGE_FACTORY.getMessage("resource.add.child.already.child", new Object[0]);
   }

   public static String RESOURCE_ADD_CHILD_ALREADY_CHILD() {
      return LOCALIZER.localize(localizableRESOURCE_ADD_CHILD_ALREADY_CHILD());
   }

   public static Localizable localizableUNSUPPORTED_URI_INJECTION_TYPE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("unsupported.uri.injection.type", new Object[]{arg0});
   }

   public static String UNSUPPORTED_URI_INJECTION_TYPE(Object arg0) {
      return LOCALIZER.localize(localizableUNSUPPORTED_URI_INJECTION_TYPE(arg0));
   }

   public static Localizable localizableERROR_CLOSING_COMMIT_OUTPUT_STREAM() {
      return MESSAGE_FACTORY.getMessage("error.closing.commit.output.stream", new Object[0]);
   }

   public static String ERROR_CLOSING_COMMIT_OUTPUT_STREAM() {
      return LOCALIZER.localize(localizableERROR_CLOSING_COMMIT_OUTPUT_STREAM());
   }

   public static Localizable localizableINVALID_CONFIG_PROPERTY_VALUE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("invalid.config.property.value", new Object[]{arg0, arg1});
   }

   public static String INVALID_CONFIG_PROPERTY_VALUE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableINVALID_CONFIG_PROPERTY_VALUE(arg0, arg1));
   }

   public static Localizable localizableERROR_RESOURCES_CANNOT_MERGE() {
      return MESSAGE_FACTORY.getMessage("error.resources.cannot.merge", new Object[0]);
   }

   public static String ERROR_RESOURCES_CANNOT_MERGE() {
      return LOCALIZER.localize(localizableERROR_RESOURCES_CANNOT_MERGE());
   }

   public static Localizable localizableGET_RETURNS_VOID(Object arg0) {
      return MESSAGE_FACTORY.getMessage("get.returns.void", new Object[]{arg0});
   }

   public static String GET_RETURNS_VOID(Object arg0) {
      return LOCALIZER.localize(localizableGET_RETURNS_VOID(arg0));
   }

   public static Localizable localizableERROR_PRIMITIVE_TYPE_NULL() {
      return MESSAGE_FACTORY.getMessage("error.primitive.type.null", new Object[0]);
   }

   public static String ERROR_PRIMITIVE_TYPE_NULL() {
      return LOCALIZER.localize(localizableERROR_PRIMITIVE_TYPE_NULL());
   }

   public static Localizable localizableRELEASING_REQUEST_PROCESSING_RESOURCES_FAILED() {
      return MESSAGE_FACTORY.getMessage("releasing.request.processing.resources.failed", new Object[0]);
   }

   public static String RELEASING_REQUEST_PROCESSING_RESOURCES_FAILED() {
      return LOCALIZER.localize(localizableRELEASING_REQUEST_PROCESSING_RESOURCES_FAILED());
   }

   public static Localizable localizableERROR_PROCESSING_METHOD(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.processing.method", new Object[]{arg0, arg1});
   }

   public static String ERROR_PROCESSING_METHOD(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_PROCESSING_METHOD(arg0, arg1));
   }

   public static Localizable localizableERROR_WRITING_RESPONSE_ENTITY() {
      return MESSAGE_FACTORY.getMessage("error.writing.response.entity", new Object[0]);
   }

   public static String ERROR_WRITING_RESPONSE_ENTITY() {
      return LOCALIZER.localize(localizableERROR_WRITING_RESPONSE_ENTITY());
   }

   public static Localizable localizableRESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(Object arg0) {
      return MESSAGE_FACTORY.getMessage("resource.replaced.child.does.not.exist", new Object[]{arg0});
   }

   public static String RESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(Object arg0) {
      return LOCALIZER.localize(localizableRESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(arg0));
   }

   public static Localizable localizableCALLBACK_ARRAY_ELEMENT_NULL() {
      return MESSAGE_FACTORY.getMessage("callback.array.element.null", new Object[0]);
   }

   public static String CALLBACK_ARRAY_ELEMENT_NULL() {
      return LOCALIZER.localize(localizableCALLBACK_ARRAY_ELEMENT_NULL());
   }

   public static Localizable localizableERROR_COMMITTING_OUTPUT_STREAM() {
      return MESSAGE_FACTORY.getMessage("error.committing.output.stream", new Object[0]);
   }

   public static String ERROR_COMMITTING_OUTPUT_STREAM() {
      return LOCALIZER.localize(localizableERROR_COMMITTING_OUTPUT_STREAM());
   }

   public static Localizable localizableERROR_WADL_GENERATOR_CONFIG_LOADER(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.wadl.generator.config.loader", new Object[]{arg0});
   }

   public static String ERROR_WADL_GENERATOR_CONFIG_LOADER(Object arg0) {
      return LOCALIZER.localize(localizableERROR_WADL_GENERATOR_CONFIG_LOADER(arg0));
   }

   public static Localizable localizableCLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(Object arg0) {
      return MESSAGE_FACTORY.getMessage("closeable.injected.request.context.null", new Object[]{arg0});
   }

   public static String CLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(Object arg0) {
      return LOCALIZER.localize(localizableCLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(arg0));
   }

   public static Localizable localizableRESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS() {
      return MESSAGE_FACTORY.getMessage("resource.config.error.null.applicationclass", new Object[0]);
   }

   public static String RESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS() {
      return LOCALIZER.localize(localizableRESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS());
   }

   public static Localizable localizableSUBRES_LOC_CACHE_INVALID_SIZE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("subres.loc.cache.invalid.size", new Object[]{arg0, arg1});
   }

   public static String SUBRES_LOC_CACHE_INVALID_SIZE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableSUBRES_LOC_CACHE_INVALID_SIZE(arg0, arg1));
   }

   public static Localizable localizableERROR_MONITORING_QUEUE_REQUEST() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.queue.request", new Object[0]);
   }

   public static String ERROR_MONITORING_QUEUE_REQUEST() {
      return LOCALIZER.localize(localizableERROR_MONITORING_QUEUE_REQUEST());
   }

   public static Localizable localizableMETHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY() {
      return MESSAGE_FACTORY.getMessage("method.invocable.from.prematch.filters.only", new Object[0]);
   }

   public static String METHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY() {
      return LOCALIZER.localize(localizableMETHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESPONSE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.response", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_RESPONSE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_RESPONSE(arg0, arg1));
   }

   public static Localizable localizableEXCEPTION_MAPPER_THROWS_EXCEPTION(Object arg0) {
      return MESSAGE_FACTORY.getMessage("exception.mapper.throws.exception", new Object[]{arg0});
   }

   public static String EXCEPTION_MAPPER_THROWS_EXCEPTION(Object arg0) {
      return LOCALIZER.localize(localizableEXCEPTION_MAPPER_THROWS_EXCEPTION(arg0));
   }

   public static Localizable localizableNON_PUB_RES_METHOD(Object arg0) {
      return MESSAGE_FACTORY.getMessage("non.pub.res.method", new Object[]{arg0});
   }

   public static String NON_PUB_RES_METHOD(Object arg0) {
      return LOCALIZER.localize(localizableNON_PUB_RES_METHOD(arg0));
   }

   public static Localizable localizableERROR_MONITORING_SHUTDOWN_INTERRUPTED() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.shutdown.interrupted", new Object[0]);
   }

   public static String ERROR_MONITORING_SHUTDOWN_INTERRUPTED() {
      return LOCALIZER.localize(localizableERROR_MONITORING_SHUTDOWN_INTERRUPTED());
   }

   public static Localizable localizableINVALID_MAPPING_KEY_EMPTY(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("invalid.mapping.key.empty", new Object[]{arg0, arg1});
   }

   public static String INVALID_MAPPING_KEY_EMPTY(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableINVALID_MAPPING_KEY_EMPTY(arg0, arg1));
   }

   public static Localizable localizablePROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("property.value.tostring.throws.exception", new Object[]{arg0, arg1});
   }

   public static String PROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizablePROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(arg0, arg1));
   }

   public static Localizable localizablePARAMETER_UNRESOLVABLE(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("parameter.unresolvable", new Object[]{arg0, arg1, arg2});
   }

   public static String PARAMETER_UNRESOLVABLE(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizablePARAMETER_UNRESOLVABLE(arg0, arg1, arg2));
   }

   public static Localizable localizableILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("illegal.client.config.class.property.value", new Object[]{arg0, arg1, arg2});
   }

   public static String ILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(arg0, arg1, arg2));
   }

   public static Localizable localizableERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER() {
      return MESSAGE_FACTORY.getMessage("error.exception.mapping.thrown.to.container", new Object[0]);
   }

   public static String ERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER() {
      return LOCALIZER.localize(localizableERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER());
   }

   public static Localizable localizableERROR_MONITORING_STATISTICS_GENERATION() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.statistics.generation", new Object[0]);
   }

   public static String ERROR_MONITORING_STATISTICS_GENERATION() {
      return LOCALIZER.localize(localizableERROR_MONITORING_STATISTICS_GENERATION());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.resource.locator", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(arg0, arg1));
   }

   public static Localizable localizableLOGGING_PROVIDER_BOUND(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("logging.provider.bound", new Object[]{arg0, arg1});
   }

   public static String LOGGING_PROVIDER_BOUND(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableLOGGING_PROVIDER_BOUND(arg0, arg1));
   }

   public static Localizable localizableRESOURCE_EMPTY(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("resource.empty", new Object[]{arg0, arg1});
   }

   public static String RESOURCE_EMPTY(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableRESOURCE_EMPTY(arg0, arg1));
   }

   public static Localizable localizableJAR_SCANNER_UNABLE_TO_CLOSE_FILE() {
      return MESSAGE_FACTORY.getMessage("jar.scanner.unable.to.close.file", new Object[0]);
   }

   public static String JAR_SCANNER_UNABLE_TO_CLOSE_FILE() {
      return LOCALIZER.localize(localizableJAR_SCANNER_UNABLE_TO_CLOSE_FILE());
   }

   public static Localizable localizablePREMATCHING_ALSO_NAME_BOUND(Object arg0) {
      return MESSAGE_FACTORY.getMessage("prematching.also.name.bound", new Object[]{arg0});
   }

   public static String PREMATCHING_ALSO_NAME_BOUND(Object arg0) {
      return LOCALIZER.localize(localizablePREMATCHING_ALSO_NAME_BOUND(arg0));
   }

   public static Localizable localizableERROR_UNSUPPORTED_ENCODING(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.unsupported.encoding", new Object[]{arg0, arg1});
   }

   public static String ERROR_UNSUPPORTED_ENCODING(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_UNSUPPORTED_ENCODING(arg0, arg1));
   }

   public static Localizable localizableERROR_MONITORING_MBEANS_REGISTRATION(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.monitoring.mbeans.registration", new Object[]{arg0});
   }

   public static String ERROR_MONITORING_MBEANS_REGISTRATION(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MONITORING_MBEANS_REGISTRATION(arg0));
   }

   public static Localizable localizableMULTIPLE_EVENT_SINK_INJECTION(Object arg0) {
      return MESSAGE_FACTORY.getMessage("multiple.event.sink.injection", new Object[]{arg0});
   }

   public static String MULTIPLE_EVENT_SINK_INJECTION(Object arg0) {
      return LOCALIZER.localize(localizableMULTIPLE_EVENT_SINK_INJECTION(arg0));
   }

   public static Localizable localizableERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION() {
      return MESSAGE_FACTORY.getMessage("error.processing.response.from.already.mapped.exception", new Object[0]);
   }

   public static String ERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION() {
      return LOCALIZER.localize(localizableERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION());
   }

   public static Localizable localizableCONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("contract.cannot.be.bound.to.resource.method", new Object[]{arg0, arg1});
   }

   public static String CONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableCONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(arg0, arg1));
   }

   public static Localizable localizableCLOSEABLE_UNABLE_TO_CLOSE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("closeable.unable.to.close", new Object[]{arg0});
   }

   public static String CLOSEABLE_UNABLE_TO_CLOSE(Object arg0) {
      return LOCALIZER.localize(localizableCLOSEABLE_UNABLE_TO_CLOSE(arg0));
   }

   public static Localizable localizableBROADCASTER_LISTENER_EXCEPTION(Object arg0) {
      return MESSAGE_FACTORY.getMessage("broadcaster.listener.exception", new Object[]{arg0});
   }

   public static String BROADCASTER_LISTENER_EXCEPTION(Object arg0) {
      return LOCALIZER.localize(localizableBROADCASTER_LISTENER_EXCEPTION(arg0));
   }

   public static Localizable localizableERROR_WADL_JAXB_CONTEXT() {
      return MESSAGE_FACTORY.getMessage("error.wadl.jaxb.context", new Object[0]);
   }

   public static String ERROR_WADL_JAXB_CONTEXT() {
      return LOCALIZER.localize(localizableERROR_WADL_JAXB_CONTEXT());
   }

   public static Localizable localizableEXCEPTION_MAPPER_FAILED_FOR_EXCEPTION() {
      return MESSAGE_FACTORY.getMessage("exception.mapper.failed.for.exception", new Object[0]);
   }

   public static String EXCEPTION_MAPPER_FAILED_FOR_EXCEPTION() {
      return LOCALIZER.localize(localizableEXCEPTION_MAPPER_FAILED_FOR_EXCEPTION());
   }

   public static Localizable localizableRESOURCE_LOOKUP_FAILED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("resource.lookup.failed", new Object[]{arg0});
   }

   public static String RESOURCE_LOOKUP_FAILED(Object arg0) {
      return LOCALIZER.localize(localizableRESOURCE_LOOKUP_FAILED(arg0));
   }

   public static Localizable localizableERROR_WADL_GENERATOR_LOAD() {
      return MESSAGE_FACTORY.getMessage("error.wadl.generator.load", new Object[0]);
   }

   public static String ERROR_WADL_GENERATOR_LOAD() {
      return LOCALIZER.localize(localizableERROR_WADL_GENERATOR_LOAD());
   }

   public static Localizable localizableRC_NOT_MODIFIABLE() {
      return MESSAGE_FACTORY.getMessage("rc.not.modifiable", new Object[0]);
   }

   public static String RC_NOT_MODIFIABLE() {
      return LOCALIZER.localize(localizableRC_NOT_MODIFIABLE());
   }

   public static Localizable localizableERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.scheduler.destroy.timeout", new Object[0]);
   }

   public static String ERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT() {
      return LOCALIZER.localize(localizableERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT());
   }

   public static Localizable localizableERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION() {
      return MESSAGE_FACTORY.getMessage("error.exception.mapping.original.exception", new Object[0]);
   }

   public static String ERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION() {
      return LOCALIZER.localize(localizableERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION());
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(Object arg0, Object arg1, Object arg2) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.request.media.type", new Object[]{arg0, arg1, arg2});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(Object arg0, Object arg1, Object arg2) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(arg0, arg1, arg2));
   }

   public static Localizable localizableRESOURCE_CONFIG_UNABLE_TO_PROCESS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("resource.config.unable.to.process", new Object[]{arg0});
   }

   public static String RESOURCE_CONFIG_UNABLE_TO_PROCESS(Object arg0) {
      return LOCALIZER.localize(localizableRESOURCE_CONFIG_UNABLE_TO_PROCESS(arg0));
   }

   public static Localizable localizableAMBIGUOUS_PARAMETER(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("ambiguous.parameter", new Object[]{arg0, arg1});
   }

   public static String AMBIGUOUS_PARAMETER(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableAMBIGUOUS_PARAMETER(arg0, arg1));
   }

   public static Localizable localizableAMBIGUOUS_FATAL_RMS(Object arg0, Object arg1, Object arg2, Object arg3) {
      return MESSAGE_FACTORY.getMessage("ambiguous.fatal.rms", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String AMBIGUOUS_FATAL_RMS(Object arg0, Object arg1, Object arg2, Object arg3) {
      return LOCALIZER.localize(localizableAMBIGUOUS_FATAL_RMS(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableERROR_WADL_GRAMMAR_ALREADY_CONTAINS() {
      return MESSAGE_FACTORY.getMessage("error.wadl.grammar.already.contains", new Object[0]);
   }

   public static String ERROR_WADL_GRAMMAR_ALREADY_CONTAINS() {
      return LOCALIZER.localize(localizableERROR_WADL_GRAMMAR_ALREADY_CONTAINS());
   }

   public static Localizable localizableLOGGING_ROOT_RESOURCE_CLASSES() {
      return MESSAGE_FACTORY.getMessage("logging.root.resource.classes", new Object[0]);
   }

   public static String LOGGING_ROOT_RESOURCE_CLASSES() {
      return LOCALIZER.localize(localizableLOGGING_ROOT_RESOURCE_CLASSES());
   }

   public static Localizable localizableINVALID_MAPPING_TYPE(Object arg0) {
      return MESSAGE_FACTORY.getMessage("invalid.mapping.type", new Object[]{arg0});
   }

   public static String INVALID_MAPPING_TYPE(Object arg0) {
      return LOCALIZER.localize(localizableINVALID_MAPPING_TYPE(arg0));
   }

   public static Localizable localizableERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.generator.config.loader.property", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(arg0, arg1));
   }

   public static Localizable localizableIGNORE_SEBOOTSTRAP_CONFIGURATION_PROPERTY(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("ignore.sebootstrap.configuration.property", new Object[]{arg0, arg1});
   }

   public static String IGNORE_SEBOOTSTRAP_CONFIGURATION_PROPERTY(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableIGNORE_SEBOOTSTRAP_CONFIGURATION_PROPERTY(arg0, arg1));
   }

   public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_METHOD(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("default.could.not.process.method", new Object[]{arg0, arg1});
   }

   public static String DEFAULT_COULD_NOT_PROCESS_METHOD(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableDEFAULT_COULD_NOT_PROCESS_METHOD(arg0, arg1));
   }

   public static Localizable localizableERROR_SUSPENDING_ASYNC_REQUEST() {
      return MESSAGE_FACTORY.getMessage("error.suspending.async.request", new Object[0]);
   }

   public static String ERROR_SUSPENDING_ASYNC_REQUEST() {
      return LOCALIZER.localize(localizableERROR_SUSPENDING_ASYNC_REQUEST());
   }

   public static Localizable localizableNON_PUB_SUB_RES_LOC(Object arg0) {
      return MESSAGE_FACTORY.getMessage("non.pub.sub.res.loc", new Object[]{arg0});
   }

   public static String NON_PUB_SUB_RES_LOC(Object arg0) {
      return LOCALIZER.localize(localizableNON_PUB_SUB_RES_LOC(arg0));
   }

   public static Localizable localizableAMBIGUOUS_RMS_IN(Object arg0, Object arg1, Object arg2, Object arg3) {
      return MESSAGE_FACTORY.getMessage("ambiguous.rms.in", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String AMBIGUOUS_RMS_IN(Object arg0, Object arg1, Object arg2, Object arg3) {
      return LOCALIZER.localize(localizableAMBIGUOUS_RMS_IN(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableTYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("type.of.method.not.resolvable.to.concrete.type", new Object[]{arg0, arg1});
   }

   public static String TYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableTYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(arg0, arg1));
   }

   public static Localizable localizableERROR_MONITORING_QUEUE_APP() {
      return MESSAGE_FACTORY.getMessage("error.monitoring.queue.app", new Object[0]);
   }

   public static String ERROR_MONITORING_QUEUE_APP() {
      return LOCALIZER.localize(localizableERROR_MONITORING_QUEUE_APP());
   }

   public static Localizable localizableSUBRES_LOC_URI_PATH_INVALID(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("subres.loc.uri.path.invalid", new Object[]{arg0, arg1});
   }

   public static String SUBRES_LOC_URI_PATH_INVALID(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableSUBRES_LOC_URI_PATH_INVALID(arg0, arg1));
   }

   public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.wadl.builder.generation.resource.path", new Object[]{arg0, arg1});
   }

   public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(arg0, arg1));
   }

   public static Localizable localizableINVALID_MAPPING_VALUE_EMPTY(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("invalid.mapping.value.empty", new Object[]{arg0, arg1});
   }

   public static String INVALID_MAPPING_VALUE_EMPTY(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableINVALID_MAPPING_VALUE_EMPTY(arg0, arg1));
   }

   public static Localizable localizableWADL_DOC_EXTENDED_WADL(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("wadl.doc.extended.wadl", new Object[]{arg0, arg1});
   }

   public static String WADL_DOC_EXTENDED_WADL(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableWADL_DOC_EXTENDED_WADL(arg0, arg1));
   }

   public static Localizable localizableERROR_ASYNC_CALLBACK_FAILED(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.async.callback.failed", new Object[]{arg0});
   }

   public static String ERROR_ASYNC_CALLBACK_FAILED(Object arg0) {
      return LOCALIZER.localize(localizableERROR_ASYNC_CALLBACK_FAILED(arg0));
   }

   public static Localizable localizableLOGGING_MESSAGE_BODY_WRITERS() {
      return MESSAGE_FACTORY.getMessage("logging.message.body.writers", new Object[0]);
   }

   public static String LOGGING_MESSAGE_BODY_WRITERS() {
      return LOCALIZER.localize(localizableLOGGING_MESSAGE_BODY_WRITERS());
   }

   public static Localizable localizableLOGGING_GLOBAL_READER_INTERCEPTORS() {
      return MESSAGE_FACTORY.getMessage("logging.global.reader.interceptors", new Object[0]);
   }

   public static String LOGGING_GLOBAL_READER_INTERCEPTORS() {
      return LOCALIZER.localize(localizableLOGGING_GLOBAL_READER_INTERCEPTORS());
   }

   public static Localizable localizableERROR_WADL_RESOURCE_MARSHAL() {
      return MESSAGE_FACTORY.getMessage("error.wadl.resource.marshal", new Object[0]);
   }

   public static String ERROR_WADL_RESOURCE_MARSHAL() {
      return LOCALIZER.localize(localizableERROR_WADL_RESOURCE_MARSHAL());
   }

   public static Localizable localizableERROR_PARAMETER_MISSING_VALUE_PROVIDER(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("error.parameter.missing.value.provider", new Object[]{arg0, arg1});
   }

   public static String ERROR_PARAMETER_MISSING_VALUE_PROVIDER(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableERROR_PARAMETER_MISSING_VALUE_PROVIDER(arg0, arg1));
   }

   public static Localizable localizableWADL_FEATURE_DISABLED_NOTRANSFORM() {
      return MESSAGE_FACTORY.getMessage("wadl.feature.disabled.notransform", new Object[0]);
   }

   public static String WADL_FEATURE_DISABLED_NOTRANSFORM() {
      return LOCALIZER.localize(localizableWADL_FEATURE_DISABLED_NOTRANSFORM());
   }

   public static Localizable localizableLOGGING_PRE_MATCH_FILTERS() {
      return MESSAGE_FACTORY.getMessage("logging.pre.match.filters", new Object[0]);
   }

   public static String LOGGING_PRE_MATCH_FILTERS() {
      return LOCALIZER.localize(localizableLOGGING_PRE_MATCH_FILTERS());
   }

   public static Localizable localizableAMBIGUOUS_RESOURCE_METHOD(Object arg0) {
      return MESSAGE_FACTORY.getMessage("ambiguous.resource.method", new Object[]{arg0});
   }

   public static String AMBIGUOUS_RESOURCE_METHOD(Object arg0) {
      return LOCALIZER.localize(localizableAMBIGUOUS_RESOURCE_METHOD(arg0));
   }

   public static Localizable localizableERROR_MONITORING_STATISTICS_LISTENER_DESTROY(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.monitoring.statistics.listener.destroy", new Object[]{arg0});
   }

   public static String ERROR_MONITORING_STATISTICS_LISTENER_DESTROY(Object arg0) {
      return LOCALIZER.localize(localizableERROR_MONITORING_STATISTICS_LISTENER_DESTROY(arg0));
   }

   public static Localizable localizableINVALID_MAPPING_FORMAT(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("invalid.mapping.format", new Object[]{arg0, arg1});
   }

   public static String INVALID_MAPPING_FORMAT(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableINVALID_MAPPING_FORMAT(arg0, arg1));
   }

   public static Localizable localizableLOGGING_NAME_BOUND_RESPONSE_FILTERS() {
      return MESSAGE_FACTORY.getMessage("logging.name.bound.response.filters", new Object[0]);
   }

   public static String LOGGING_NAME_BOUND_RESPONSE_FILTERS() {
      return LOCALIZER.localize(localizableLOGGING_NAME_BOUND_RESPONSE_FILTERS());
   }

   public static Localizable localizableLOGGING_NAME_BOUND_WRITER_INTERCEPTORS() {
      return MESSAGE_FACTORY.getMessage("logging.name.bound.writer.interceptors", new Object[0]);
   }

   public static String LOGGING_NAME_BOUND_WRITER_INTERCEPTORS() {
      return LOCALIZER.localize(localizableLOGGING_NAME_BOUND_WRITER_INTERCEPTORS());
   }

   public static Localizable localizableERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR() {
      return MESSAGE_FACTORY.getMessage("error.wadl.resource.external.grammar", new Object[0]);
   }

   public static String ERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR() {
      return LOCALIZER.localize(localizableERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR());
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.server.internal.localization", locale);
      }
   }
}
