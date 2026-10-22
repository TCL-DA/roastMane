package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;

public interface ActiveDescriptorBuilder {
   ActiveDescriptorBuilder named(String var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder to(Type var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder in(Annotation var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder in(Class<? extends Annotation> var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder qualifiedBy(Annotation var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder has(String var1, String var2) throws IllegalArgumentException;

   ActiveDescriptorBuilder has(String var1, List<String> var2) throws IllegalArgumentException;

   ActiveDescriptorBuilder ofRank(int var1);

   ActiveDescriptorBuilder localOnly();

   ActiveDescriptorBuilder visibility(DescriptorVisibility var1);

   ActiveDescriptorBuilder proxy();

   ActiveDescriptorBuilder proxy(boolean var1);

   ActiveDescriptorBuilder proxyForSameScope();

   ActiveDescriptorBuilder proxyForSameScope(boolean var1);

   ActiveDescriptorBuilder andLoadWith(HK2Loader var1) throws IllegalArgumentException;

   ActiveDescriptorBuilder analyzeWith(String var1);

   ActiveDescriptorBuilder asType(Type var1);

   <T> AbstractActiveDescriptor<T> build() throws IllegalArgumentException;

   @Deprecated
   <T> AbstractActiveDescriptor<T> buildFactory() throws IllegalArgumentException;

   <T> AbstractActiveDescriptor<T> buildProvideMethod() throws IllegalArgumentException;
}
