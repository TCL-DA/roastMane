package org.glassfish.jersey.message;

import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class MessageProperties {
   public static final String DEFLATE_WITHOUT_ZLIB = "jersey.config.deflate.nozlib";
   public static final String JAXB_PROCESS_XML_ROOT_ELEMENT = "jersey.config.jaxb.collections.processXmlRootElement";
   public static final String XML_SECURITY_DISABLE = "jersey.config.xml.security.disable";
   public static final String XML_FORMAT_OUTPUT = "jersey.config.xml.formatOutput";
   public static final String IO_BUFFER_SIZE = "jersey.config.io.bufferSize";
   public static final int IO_DEFAULT_BUFFER_SIZE = 8192;
   public static String JSON_MAX_STRING_LENGTH = "jersey.config.json.string.length";
   public static final String LEGACY_WORKERS_ORDERING = "jersey.config.workers.legacyOrdering";

   private MessageProperties() {
   }
}
