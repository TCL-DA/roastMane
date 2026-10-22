package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlNameProcessor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.Stax2WriterAdapter;
import org.codehaus.stax2.util.StreamWriter2Delegate;

public class Log4jXmlObjectMapper extends XmlMapper {
   private static final long serialVersionUID = 1L;

   public Log4jXmlObjectMapper() {
      this(true, false);
   }

   public Log4jXmlObjectMapper(final boolean includeStacktrace, final boolean stacktraceAsString) {
      super(new Log4jXmlObjectMapper.SanitizingXmlFactory(), new Log4jXmlModule(includeStacktrace, stacktraceAsString));
      this.setSerializationInclusion(Include.NON_EMPTY);
   }

   private static final class SanitizingWriter extends StreamWriter2Delegate {
      private static final char REPLACEMENT_CHAR = '�';

      SanitizingWriter(final XMLStreamWriter2 delegate) {
         super(delegate);
         this.setParent(delegate);
      }

      public void writeAttribute(final String localName, final String value) throws XMLStreamException {
         super.writeAttribute(localName, sanitizeXml10(value));
      }

      public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
         super.writeAttribute(namespaceURI, localName, sanitizeXml10(value));
      }

      public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
         super.writeAttribute(prefix, namespaceURI, localName, sanitizeXml10(value));
      }

      public void writeCData(String text) throws XMLStreamException {
         super.writeCData(sanitizeXml10(text));
      }

      public void writeCData(char[] text, int start, int len) throws XMLStreamException {
         super.writeCData(sanitizeXml10(text, start, len));
      }

      public void writeCharacters(final String text) throws XMLStreamException {
         super.writeCharacters(sanitizeXml10(text));
      }

      public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
         super.writeCharacters(sanitizeXml10(text, start, len));
      }

      public void writeComment(String text) throws XMLStreamException {
         super.writeComment(sanitizeXml10(text));
      }

      private static String sanitizeXml10(final String input) {
         if (input == null) {
            return null;
         } else {
            int length = input.length();
            int i = 0;

            while (i < length) {
               int cp = input.codePointAt(i);
               if (!isValidXml10(cp)) {
                  StringBuilder out = new StringBuilder(length);
                  out.append(input, 0, i);
                  appendSanitized(input, i, length, out);
                  return out.toString();
               }

               i += Character.charCount(cp);
            }

            return input;
         }
      }

      private static String sanitizeXml10(final char[] input, final int start, final int len) {
         return sanitizeXml10(new String(input, start, len));
      }

      private static void appendSanitized(final String input, int i, final int length, final StringBuilder out) {
         while (i < length) {
            int cp = input.codePointAt(i);
            out.appendCodePoint(isValidXml10(cp) ? cp : '�');
            i += Character.charCount(cp);
         }
      }

      private static boolean isValidXml10(final int codePoint) {
         assert codePoint >= 0 && codePoint <= 1114111;

         return codePoint >= 32 && codePoint < 55296
            || codePoint == 9
            || codePoint == 10
            || codePoint == 13
            || codePoint > 57343 && codePoint <= 65533
            || codePoint >= 65536;
      }
   }

   private static final class SanitizingXmlFactory extends XmlFactory {
      private static final long serialVersionUID = 1L;

      public SanitizingXmlFactory() {
      }

      private SanitizingXmlFactory(
         ObjectCodec oc, int xpFeatures, int xgFeatures, XMLInputFactory xmlIn, XMLOutputFactory xmlOut, String nameForTextElem, XmlNameProcessor nameProcessor
      ) {
         super(oc, xpFeatures, xgFeatures, xmlIn, xmlOut, nameForTextElem, nameProcessor);
      }

      protected XMLStreamWriter _createXmlWriter(final IOContext ctxt, final Writer w) throws IOException {
         return new Log4jXmlObjectMapper.SanitizingWriter(Stax2WriterAdapter.wrapIfNecessary(super._createXmlWriter(ctxt, w)));
      }

      protected XMLStreamWriter _createXmlWriter(final IOContext ctxt, final OutputStream out) throws IOException {
         return new Log4jXmlObjectMapper.SanitizingWriter(Stax2WriterAdapter.wrapIfNecessary(super._createXmlWriter(ctxt, out)));
      }

      public XmlFactory copy() {
         this._checkInvalidCopy(Log4jXmlObjectMapper.SanitizingXmlFactory.class);
         return new Log4jXmlObjectMapper.SanitizingXmlFactory(
            this._objectCodec,
            this._xmlParserFeatures,
            this._xmlGeneratorFeatures,
            this._xmlInputFactory,
            this._xmlOutputFactory,
            this._cfgNameForTextElement,
            this._nameProcessor
         );
      }
   }
}
