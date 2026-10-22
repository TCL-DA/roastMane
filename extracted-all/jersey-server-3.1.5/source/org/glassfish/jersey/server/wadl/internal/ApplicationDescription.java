package org.glassfish.jersey.server.wadl.internal;

import com.sun.research.ws.wadl.Application;
import jakarta.ws.rs.core.MediaType;
import java.util.Set;
import javax.xml.namespace.QName;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class ApplicationDescription {
   private Application _application;
   private WadlGenerator.ExternalGrammarDefinition _externalGrammarDefinition;

   ApplicationDescription(Application application, WadlGenerator.ExternalGrammarDefinition externalGrammarDefinition) {
      this._application = application;
      this._externalGrammarDefinition = externalGrammarDefinition;
   }

   public Application getApplication() {
      return this._application;
   }

   public QName resolve(Class type) {
      return this._externalGrammarDefinition.resolve(type);
   }

   public ApplicationDescription.ExternalGrammar getExternalGrammar(String path) {
      return this._externalGrammarDefinition.map.get(path);
   }

   public Set<String> getExternalMetadataKeys() {
      return this._externalGrammarDefinition.map.keySet();
   }

   public static class ExternalGrammar {
      private MediaType _type;
      private byte[] _content;

      public ExternalGrammar(MediaType type, byte[] content) {
         this._type = type;
         this._content = (byte[])content.clone();
      }

      public MediaType getType() {
         return this._type;
      }

      public byte[] getContent() {
         return (byte[])this._content.clone();
      }
   }
}
