package org.glassfish.jersey.server.wadl.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WadlUtils {
   public static final String DETAILED_WADL_QUERY_PARAM = "detail";

   public static <T> T unmarshall(InputStream inputStream, SAXParserFactory saxParserFactory, Class<T> resultClass) throws JAXBException, ParserConfigurationException, SAXException {
      JAXBContext jaxbContext = null;

      try {
         jaxbContext = JAXBContext.newInstance(new Class[]{resultClass});
      } catch (JAXBException var8) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_JAXB_CONTEXT(), var8);
      }

      SAXParser saxParser = saxParserFactory.newSAXParser();
      SAXSource source = new SAXSource(saxParser.getXMLReader(), new InputSource(inputStream));
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      Object result = unmarshaller.unmarshal(source);
      return resultClass.cast(result);
   }

   public static boolean isDetailedWadlRequested(UriInfo uriInfo) {
      List<String> simple = (List<String>)uriInfo.getQueryParameters().get("detail");
      if (simple != null) {
         if (simple.size() == 0) {
            return true;
         } else {
            String value = simple.get(0).trim();
            return value.isEmpty() || value.toUpperCase(Locale.ROOT).equals("TRUE");
         }
      } else {
         return false;
      }
   }
}
