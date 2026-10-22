package jakarta.xml.bind.helpers;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class AbstractUnmarshallerImpl implements Unmarshaller {
   private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
   private XMLReader reader = null;

   protected AbstractUnmarshallerImpl() {
   }

   protected XMLReader getXMLReader() throws JAXBException {
      if (this.reader == null) {
         try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.setValidating(false);
            this.reader = parserFactory.newSAXParser().getXMLReader();
         } catch (SAXException | ParserConfigurationException var2) {
            throw new JAXBException(var2);
         }
      }

      return this.reader;
   }

   @Override
   public Object unmarshal(Source source) throws JAXBException {
      if (source == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source"));
      } else if (source instanceof SAXSource) {
         return this.unmarshal((SAXSource)source);
      } else if (source instanceof StreamSource) {
         return this.unmarshal(streamSourceToInputSource((StreamSource)source));
      } else if (source instanceof DOMSource) {
         return this.unmarshal(((DOMSource)source).getNode());
      } else {
         throw new IllegalArgumentException();
      }
   }

   private Object unmarshal(SAXSource source) throws JAXBException {
      XMLReader r = source.getXMLReader();
      if (r == null) {
         r = this.getXMLReader();
      }

      return this.unmarshal(r, source.getInputSource());
   }

   protected abstract Object unmarshal(XMLReader var1, InputSource var2) throws JAXBException;

   @Override
   public final Object unmarshal(InputSource source) throws JAXBException {
      if (source == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source"));
      } else {
         return this.unmarshal(this.getXMLReader(), source);
      }
   }

   private Object unmarshal(String url) throws JAXBException {
      return this.unmarshal(new InputSource(url));
   }

   @Override
   public final Object unmarshal(URL url) throws JAXBException {
      if (url == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "url"));
      } else {
         return this.unmarshal(url.toExternalForm());
      }
   }

   @Override
   public final Object unmarshal(File f) throws JAXBException {
      if (f == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "file"));
      } else {
         try {
            return this.unmarshal(new BufferedInputStream(new FileInputStream(f)));
         } catch (FileNotFoundException var3) {
            throw new IllegalArgumentException(var3.getMessage());
         }
      }
   }

   @Override
   public final Object unmarshal(InputStream is) throws JAXBException {
      if (is == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "is"));
      } else {
         InputSource isrc = new InputSource(is);
         return this.unmarshal(isrc);
      }
   }

   @Override
   public final Object unmarshal(Reader reader) throws JAXBException {
      if (reader == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "reader"));
      } else {
         InputSource isrc = new InputSource(reader);
         return this.unmarshal(isrc);
      }
   }

   private static InputSource streamSourceToInputSource(StreamSource ss) {
      InputSource is = new InputSource();
      is.setSystemId(ss.getSystemId());
      is.setByteStream(ss.getInputStream());
      is.setCharacterStream(ss.getReader());
      return is;
   }

   @Override
   public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
      if (handler == null) {
         this.eventHandler = new DefaultValidationEventHandler();
      } else {
         this.eventHandler = handler;
      }
   }

   @Override
   public ValidationEventHandler getEventHandler() throws JAXBException {
      return this.eventHandler;
   }

   protected UnmarshalException createUnmarshalException(SAXException e) {
      Exception nested = e.getException();
      if (nested instanceof UnmarshalException) {
         return (UnmarshalException)nested;
      } else if (nested instanceof RuntimeException) {
         throw (RuntimeException)nested;
      } else {
         return nested != null ? new UnmarshalException(nested) : new UnmarshalException(e);
      }
   }

   @Override
   public void setProperty(String name, Object value) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
      } else {
         throw new PropertyException(name, value);
      }
   }

   @Override
   public Object getProperty(String name) throws PropertyException {
      if (name == null) {
         throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name"));
      } else {
         throw new PropertyException(name);
      }
   }

   @Override
   public Object unmarshal(XMLEventReader reader) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object unmarshal(XMLStreamReader reader) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setSchema(Schema schema) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Schema getSchema() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <A extends XmlAdapter<?, ?>> void setAdapter(A adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException();
      } else {
         this.setAdapter(adapter.getClass(), adapter);
      }
   }

   @Override
   public <A extends XmlAdapter<?, ?>> void setAdapter(Class<A> type, A adapter) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <A extends XmlAdapter<?, ?>> A getAdapter(Class<A> type) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
      throw new UnsupportedOperationException();
   }

   @Override
   public AttachmentUnmarshaller getAttachmentUnmarshaller() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setListener(Unmarshaller.Listener listener) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Unmarshaller.Listener getListener() {
      throw new UnsupportedOperationException();
   }
}
