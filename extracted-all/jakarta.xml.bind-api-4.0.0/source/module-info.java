module jakarta.xml.bind {
   requires transitive jakarta.activation;
   requires transitive java.xml;
   requires java.logging;

   exports jakarta.xml.bind;
   exports jakarta.xml.bind.annotation;
   exports jakarta.xml.bind.annotation.adapters;
   exports jakarta.xml.bind.attachment;
   exports jakarta.xml.bind.helpers;
   exports jakarta.xml.bind.util;

   uses jakarta.xml.bind.JAXBContextFactory;
}
