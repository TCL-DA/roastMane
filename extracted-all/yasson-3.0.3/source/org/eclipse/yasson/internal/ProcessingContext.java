package org.eclipse.yasson.internal;

import java.util.HashSet;
import java.util.Set;

public abstract class ProcessingContext {
   private final JsonbContext jsonbContext;
   private final Set<Object> currentlyProcessedObjects = new HashSet<>();

   public ProcessingContext(JsonbContext jsonbContext) {
      this.jsonbContext = jsonbContext;
   }

   public JsonbContext getJsonbContext() {
      return this.jsonbContext;
   }

   public MappingContext getMappingContext() {
      return this.getJsonbContext().getMappingContext();
   }

   public boolean addProcessedObject(Object object) {
      return this.currentlyProcessedObjects.add(object);
   }

   public boolean removeProcessedObject(Object object) {
      return this.currentlyProcessedObjects.remove(object);
   }
}
