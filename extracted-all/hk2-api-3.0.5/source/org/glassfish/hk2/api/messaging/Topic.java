package org.glassfish.hk2.api.messaging;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public interface Topic<T> {
   void publish(T var1);

   Topic<T> named(String var1);

   <U> Topic<U> ofType(Type var1);

   Topic<T> qualifiedWith(Annotation... var1);

   Type getTopicType();

   Set<Annotation> getTopicQualifiers();
}
