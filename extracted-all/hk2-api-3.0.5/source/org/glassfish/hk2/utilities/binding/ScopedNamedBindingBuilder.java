package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface ScopedNamedBindingBuilder<T> extends BindingBuilder<T> {
   ScopedNamedBindingBuilder<T> to(Class<? super T> var1);

   ScopedNamedBindingBuilder<T> to(TypeLiteral<?> var1);

   ScopedNamedBindingBuilder<T> loadedBy(HK2Loader var1);

   ScopedNamedBindingBuilder<T> withMetadata(String var1, String var2);

   ScopedNamedBindingBuilder<T> withMetadata(String var1, List<String> var2);

   ScopedNamedBindingBuilder<T> qualifiedBy(Annotation var1);

   void ranked(int var1);

   ScopedNamedBindingBuilder<T> proxy(boolean var1);

   ScopedNamedBindingBuilder<T> analyzeWith(String var1);
}
