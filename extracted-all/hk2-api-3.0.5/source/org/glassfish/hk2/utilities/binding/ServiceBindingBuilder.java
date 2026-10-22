package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface ServiceBindingBuilder<T> extends BindingBuilder<T> {
   ServiceBindingBuilder<T> to(Class<? super T> var1);

   ServiceBindingBuilder<T> to(TypeLiteral<?> var1);

   ServiceBindingBuilder<T> to(Type var1);

   ServiceBindingBuilder<T> loadedBy(HK2Loader var1);

   ServiceBindingBuilder<T> withMetadata(String var1, String var2);

   ServiceBindingBuilder<T> withMetadata(String var1, List<String> var2);

   ServiceBindingBuilder<T> qualifiedBy(Annotation var1);

   ScopedBindingBuilder<T> in(Annotation var1);

   ScopedBindingBuilder<T> in(Class<? extends Annotation> var1);

   NamedBindingBuilder<T> named(String var1);

   void ranked(int var1);

   ServiceBindingBuilder<T> proxy(boolean var1);

   ServiceBindingBuilder<T> proxyForSameScope(boolean var1);

   ServiceBindingBuilder<T> analyzeWith(String var1);

   ServiceBindingBuilder<T> asType(Type var1);
}
