package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface ScopedBindingBuilder<T> extends BindingBuilder<T> {
   ScopedBindingBuilder<T> to(Class<? super T> var1);

   ScopedBindingBuilder<T> to(TypeLiteral<?> var1);

   ScopedBindingBuilder<T> loadedBy(HK2Loader var1);

   ScopedBindingBuilder<T> withMetadata(String var1, String var2);

   ScopedBindingBuilder<T> withMetadata(String var1, List<String> var2);

   ScopedBindingBuilder<T> qualifiedBy(Annotation var1);

   ScopedNamedBindingBuilder<T> named(String var1);

   void ranked(int var1);

   ScopedBindingBuilder<T> proxy(boolean var1);

   ScopedBindingBuilder<T> proxyForSameScope(boolean var1);

   ScopedBindingBuilder<T> analyzeWith(String var1);
}
