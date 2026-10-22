package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public class DOMSerializer extends StdSerializer<Node> {
   protected final TransformerFactory transformerFactory;

   public DOMSerializer() {
      super(Node.class);

      try {
         this.transformerFactory = TransformerFactory.newInstance();
         this.transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      } catch (Exception var2) {
         throw new IllegalStateException("Could not instantiate `TransformerFactory`: " + var2.getMessage(), var2);
      }
   }

   public void serialize(Node value, JsonGenerator g, SerializerProvider provider) throws IOException {
      try {
         Transformer transformer = this.transformerFactory.newTransformer();
         transformer.setOutputProperty("omit-xml-declaration", "yes");
         transformer.setOutputProperty("indent", "no");
         StreamResult result = new StreamResult(new StringWriter());
         transformer.transform(new DOMSource(value), result);
         g.writeString(result.getWriter().toString());
      } catch (TransformerConfigurationException var6) {
         throw new IllegalStateException("Could not create XML Transformer for writing DOM `Node` value: " + var6.getMessage(), var6);
      } catch (TransformerException var7) {
         provider.reportMappingProblem(var7, "DOM `Node` value serialization failed: %s", var7.getMessage());
      }
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode("string", true);
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (visitor != null) {
         visitor.expectAnyFormat(typeHint);
      }
   }
}
