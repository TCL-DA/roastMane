package org.glassfish.jersey.process.internal;

public interface ChainableStage<DATA> extends Stage<DATA> {
   void setDefaultNext(Stage<DATA> var1);
}
