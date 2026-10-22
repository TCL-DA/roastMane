package org.aopalliance.reflect;

public interface ProgramUnit {
   UnitLocator getLocator();

   Metadata getMetadata(Object var1);

   Metadata[] getMetadatas();

   void addMetadata(Metadata var1);

   void removeMetadata(Object var1);
}
