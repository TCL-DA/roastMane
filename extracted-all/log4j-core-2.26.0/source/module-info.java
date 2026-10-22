module org.apache.logging.log4j.core {
   requires static biz.aQute.bnd.annotation;
   requires static com.conversantmedia.disruptor;
   requires static com.fasterxml.jackson.annotation;
   requires static com.fasterxml.jackson.core;
   requires static com.fasterxml.jackson.databind;
   requires static com.fasterxml.jackson.dataformat.xml;
   requires static com.fasterxml.jackson.dataformat.yaml;
   requires static com.google.errorprone.annotations;
   requires static com.lmax.disruptor;
   requires static java.activation;
   requires transitive java.base;
   requires transitive java.compiler;
   requires java.desktop;
   requires static java.logging;
   requires static java.management;
   requires static java.naming;
   requires java.rmi;
   requires transitive java.scripting;
   requires static java.sql;
   requires transitive java.xml;
   requires static javax.jms.api;
   requires static javax.mail.api;
   requires static kafka.clients;
   requires static org.apache.commons.compress;
   requires static org.apache.commons.csv;
   requires transitive org.apache.logging.log4j;
   requires static org.codehaus.stax2;
   requires static org.jctools.core;
   requires static org.jspecify;
   requires static org.osgi.annotation.bundle;
   requires static org.osgi.annotation.versioning;
   requires static org.osgi.core;
   requires static org.zeromq.jeromq;

   exports org.apache.logging.log4j.core;
   exports org.apache.logging.log4j.core.appender;
   exports org.apache.logging.log4j.core.appender.db;
   exports org.apache.logging.log4j.core.appender.db.jdbc;
   exports org.apache.logging.log4j.core.appender.mom;
   exports org.apache.logging.log4j.core.appender.mom.jeromq;
   exports org.apache.logging.log4j.core.appender.mom.kafka;
   exports org.apache.logging.log4j.core.appender.nosql;
   exports org.apache.logging.log4j.core.appender.rewrite;
   exports org.apache.logging.log4j.core.appender.rolling;
   exports org.apache.logging.log4j.core.appender.rolling.action;
   exports org.apache.logging.log4j.core.appender.routing;
   exports org.apache.logging.log4j.core.async;
   exports org.apache.logging.log4j.core.config;
   exports org.apache.logging.log4j.core.config.arbiters;
   exports org.apache.logging.log4j.core.config.builder.api;
   exports org.apache.logging.log4j.core.config.builder.impl;
   exports org.apache.logging.log4j.core.config.composite;
   exports org.apache.logging.log4j.core.config.json;
   exports org.apache.logging.log4j.core.config.plugins;
   exports org.apache.logging.log4j.core.config.plugins.convert;
   exports org.apache.logging.log4j.core.config.plugins.processor;
   exports org.apache.logging.log4j.core.config.plugins.util;
   exports org.apache.logging.log4j.core.config.plugins.validation;
   exports org.apache.logging.log4j.core.config.plugins.validation.constraints;
   exports org.apache.logging.log4j.core.config.plugins.validation.validators;
   exports org.apache.logging.log4j.core.config.plugins.visitors;
   exports org.apache.logging.log4j.core.config.properties;
   exports org.apache.logging.log4j.core.config.status;
   exports org.apache.logging.log4j.core.config.xml;
   exports org.apache.logging.log4j.core.config.yaml;
   exports org.apache.logging.log4j.core.filter;
   exports org.apache.logging.log4j.core.filter.mutable;
   exports org.apache.logging.log4j.core.impl;
   exports org.apache.logging.log4j.core.jackson;
   exports org.apache.logging.log4j.core.jmx;
   exports org.apache.logging.log4j.core.layout;
   exports org.apache.logging.log4j.core.lookup;
   exports org.apache.logging.log4j.core.net;
   exports org.apache.logging.log4j.core.net.ssl;
   exports org.apache.logging.log4j.core.osgi;
   exports org.apache.logging.log4j.core.parser;
   exports org.apache.logging.log4j.core.pattern;
   exports org.apache.logging.log4j.core.script;
   exports org.apache.logging.log4j.core.selector;
   exports org.apache.logging.log4j.core.time;
   exports org.apache.logging.log4j.core.tools;
   exports org.apache.logging.log4j.core.util;
   exports org.apache.logging.log4j.core.util.datetime;
   exports org.apache.logging.log4j.core.util.internal.instant to
      org.apache.logging.log4j.layout.template.json;

   uses org.apache.logging.log4j.core.util.ContextDataProvider;
   uses org.apache.logging.log4j.core.util.WatchEventService;

   provides javax.annotation.processing.Processor with
      org.apache.logging.log4j.core.config.plugins.processor.GraalVmProcessor,
      org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor;
   provides org.apache.logging.log4j.core.util.ContextDataProvider with
      org.apache.logging.log4j.core.impl.ThreadContextDataProvider;
   provides org.apache.logging.log4j.message.ThreadDumpMessage.ThreadInfoFactory with
      org.apache.logging.log4j.core.message.ExtendedThreadInfoFactory;
   provides org.apache.logging.log4j.spi.Provider with
      org.apache.logging.log4j.core.impl.Log4jProvider;
}
