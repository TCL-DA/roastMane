package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

public interface NamedBindingBuilder<T> extends BindingBuilder<T> {
   NamedBindingBuilder<T> to(Class<? super T> var1);

   NamedBindingBuilder<T> to(TypeLiteral<?> var1);

   NamedBindingBuilder<T> loadedBy(HK2Loader var1);

   NamedBindingBuilder<T> withMetadata(String var1, String var2);

   NamedBindingBuilder<T> withMetadata(String var1, List<String> var2);

   NamedBindingBuilder<T> qualifiedBy(Annotation var1);

   ScopedNamedBindingBuilder<T> in(Class<? extends Annotation> var1);

   void ranked(int var1);

   NamedBindingBuilder<T> proxy(boolean var1);

   NamedBindingBuilder<T> asType(Type var1);
}
