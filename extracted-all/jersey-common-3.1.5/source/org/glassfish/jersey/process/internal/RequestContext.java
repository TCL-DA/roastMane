package org.glassfish.jersey.process.internal;

public interface RequestContext {
   RequestContext getReference();

   void release();
}
