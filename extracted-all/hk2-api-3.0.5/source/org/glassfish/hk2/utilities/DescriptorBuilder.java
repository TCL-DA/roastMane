package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.HK2Loader;

public interface DescriptorBuilder {
   DescriptorBuilder named(String var1) throws IllegalArgumentException;

   DescriptorBuilder to(Class<?> var1) throws IllegalArgumentException;

   DescriptorBuilder to(String var1) throws IllegalArgumentException;

   DescriptorBuilder in(Class<? extends Annotation> var1) throws IllegalArgumentException;

   DescriptorBuilder in(String var1) throws IllegalArgumentException;

   DescriptorBuilder qualifiedBy(Annotation var1) throws IllegalArgumentException;

   DescriptorBuilder qualifiedBy(String var1) throws IllegalArgumentException;

   DescriptorBuilder has(String var1, String var2) throws IllegalArgumentException;

   DescriptorBuilder has(String var1, List<String> var2) throws IllegalArgumentException;

   DescriptorBuilder ofRank(int var1);

   DescriptorBuilder proxy();

   DescriptorBuilder proxy(boolean var1);

   DescriptorBuilder proxyForSameScope();

   DescriptorBuilder proxyForSameScope(boolean var1);

   DescriptorBuilder localOnly();

   DescriptorBuilder visibility(DescriptorVisibility var1);

   DescriptorBuilder andLoadWith(HK2Loader var1) throws IllegalArgumentException;

   DescriptorBuilder analyzeWith(String var1);

   DescriptorImpl build() throws IllegalArgumentException;

   FactoryDescriptors buildFactory() throws IllegalArgumentException;

   FactoryDescriptors buildFactory(String var1) throws IllegalArgumentException;

   FactoryDescriptors buildFactory(Class<? extends Annotation> var1) throws IllegalArgumentException;
}
