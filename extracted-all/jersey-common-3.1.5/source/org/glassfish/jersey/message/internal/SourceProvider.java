package org.glassfish.jersey.message.internal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Singleton
public final class SourceProvider {
   @Produces({"application/xml", "text/xml", "*/*"})
   @Consumes({"application/xml", "text/xml", "*/*"})
   @Singleton
   public static final class DomSourceReader implements MessageBodyReader<DOMSource> {
      private final Provider<DocumentBuilderFactory> dbf;

      @Inject
      public DomSourceReader(@Context Provider<DocumentBuilderFactory> dbf) {
         this.dbf = dbf;
      }

      public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
         return DOMSource.class == t;
      }

      public DOMSource readFrom(
         Class<DOMSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
      ) throws IOException {
         try {
            Document d = ((DocumentBuilderFactory)this.dbf.get()).newDocumentBuilder().parse(entityStream);
            return new DOMSource(d);
         } catch (SAXParseException var8) {
            throw new BadRequestException(var8);
         } catch (SAXException var9) {
            throw new InternalServerErrorException(var9);
         } catch (ParserConfigurationException var10) {
            throw new InternalServerErrorException(var10);
         }
      }
   }

   @Produces({"application/xml", "text/xml", "*/*"})
   @Consumes({"application/xml", "text/xml", "*/*"})
   @Singleton
   public static final class SaxSourceReader implements MessageBodyReader<SAXSource> {
      private final Provider<SAXParserFactory> spf;

      @Inject
      public SaxSourceReader(@Context Provider<SAXParserFactory> spf) {
         this.spf = spf;
      }

      public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
         return SAXSource.class == t;
      }

      public SAXSource readFrom(
         Class<SAXSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
      ) throws IOException {
         try {
            return new SAXSource(((SAXParserFactory)this.spf.get()).newSAXParser().getXMLReader(), new InputSource(entityStream));
         } catch (SAXParseException var8) {
            throw new BadRequestException(var8);
         } catch (SAXException var9) {
            throw new InternalServerErrorException(var9);
         } catch (ParserConfigurationException var10) {
            throw new InternalServerErrorException(var10);
         }
      }
   }

   @Produces({"application/xml", "text/xml", "*/*"})
   @Consumes({"application/xml", "text/xml", "*/*"})
   @Singleton
   public static final class SourceWriter implements MessageBodyWriter<Source> {
      private final Provider<SAXParserFactory> saxParserFactory;
      private final Provider<TransformerFactory> transformerFactory;

      @Inject
      public SourceWriter(@Context Provider<SAXParserFactory> spf, @Context Provider<TransformerFactory> tf) {
         this.saxParserFactory = spf;
         this.transformerFactory = tf;
      }

      public boolean isWriteable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
         return Source.class.isAssignableFrom(t);
      }

      public long getSize(Source o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
         return -1L;
      }

      public void writeTo(
         Source source, Class<?> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream
      ) throws IOException {
         try {
            if (source instanceof StreamSource) {
               StreamSource stream = (StreamSource)source;
               InputSource inputStream = new InputSource(stream.getInputStream());
               inputStream.setCharacterStream(inputStream.getCharacterStream());
               inputStream.setPublicId(stream.getPublicId());
               inputStream.setSystemId(source.getSystemId());
               source = new SAXSource(((SAXParserFactory)this.saxParserFactory.get()).newSAXParser().getXMLReader(), inputStream);
            }

            StreamResult sr = new StreamResult(entityStream);
            ((TransformerFactory)this.transformerFactory.get()).newTransformer().transform(source, sr);
         } catch (SAXException var10) {
            throw new InternalServerErrorException(var10);
         } catch (ParserConfigurationException var11) {
            throw new InternalServerErrorException(var11);
         } catch (TransformerException var12) {
            throw new InternalServerErrorException(var12);
         }
      }
   }

   @Produces({"application/xml", "text/xml", "*/*"})
   @Consumes({"application/xml", "text/xml", "*/*"})
   @Singleton
   public static final class StreamSourceReader implements MessageBodyReader<StreamSource> {
      public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
         return StreamSource.class == t || Source.class == t;
      }

      public StreamSource readFrom(
         Class<StreamSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
      ) throws IOException {
         return new StreamSource(entityStream);
      }
   }
}
