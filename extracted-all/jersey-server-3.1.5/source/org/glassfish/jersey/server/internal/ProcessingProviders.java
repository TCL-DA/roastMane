package org.glassfish.jersey.server.internal;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.util.List;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.internal.RankedProvider;

public class ProcessingProviders {
   private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundRequestFilters;
   private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters;
   private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors;
   private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors;
   private final MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundRequestFiltersInverse;
   private final MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundResponseFiltersInverse;
   private final MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse;
   private final MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse;
   private final Iterable<RankedProvider<ContainerRequestFilter>> globalRequestFilters;
   private final Iterable<ContainerRequestFilter> sortedGlobalRequestFilters;
   private final List<RankedProvider<ContainerRequestFilter>> preMatchFilters;
   private final Iterable<RankedProvider<ContainerResponseFilter>> globalResponseFilters;
   private final Iterable<ContainerResponseFilter> sortedGlobalResponseFilters;
   private final Iterable<RankedProvider<ReaderInterceptor>> globalReaderInterceptors;
   private final Iterable<ReaderInterceptor> sortedGlobalReaderInterceptors;
   private final Iterable<RankedProvider<WriterInterceptor>> globalWriterInterceptors;
   private final Iterable<WriterInterceptor> sortedGlobalWriterInterceptors;
   private final Iterable<DynamicFeature> dynamicFeatures;

   public ProcessingProviders(
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundRequestFilters,
      MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundRequestFiltersInverse,
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters,
      MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundResponseFiltersInverse,
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors,
      MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse,
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors,
      MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse,
      Iterable<RankedProvider<ContainerRequestFilter>> globalRequestFilters,
      List<RankedProvider<ContainerRequestFilter>> preMatchFilters,
      Iterable<RankedProvider<ContainerResponseFilter>> globalResponseFilters,
      Iterable<RankedProvider<ReaderInterceptor>> globalReaderInterceptors,
      Iterable<RankedProvider<WriterInterceptor>> globalWriterInterceptors,
      Iterable<DynamicFeature> dynamicFeatures
   ) {
      this.nameBoundReaderInterceptors = nameBoundReaderInterceptors;
      this.nameBoundReaderInterceptorsInverse = nameBoundReaderInterceptorsInverse;
      this.nameBoundRequestFilters = nameBoundRequestFilters;
      this.nameBoundRequestFiltersInverse = nameBoundRequestFiltersInverse;
      this.nameBoundResponseFilters = nameBoundResponseFilters;
      this.nameBoundResponseFiltersInverse = nameBoundResponseFiltersInverse;
      this.nameBoundWriterInterceptors = nameBoundWriterInterceptors;
      this.nameBoundWriterInterceptorsInverse = nameBoundWriterInterceptorsInverse;
      this.globalRequestFilters = globalRequestFilters;
      this.preMatchFilters = preMatchFilters;
      this.globalResponseFilters = globalResponseFilters;
      this.globalReaderInterceptors = globalReaderInterceptors;
      this.globalWriterInterceptors = globalWriterInterceptors;
      this.dynamicFeatures = dynamicFeatures;
      this.sortedGlobalReaderInterceptors = Providers.sortRankedProviders(new RankedComparator(), globalReaderInterceptors);
      this.sortedGlobalWriterInterceptors = Providers.sortRankedProviders(new RankedComparator(), globalWriterInterceptors);
      this.sortedGlobalRequestFilters = Providers.sortRankedProviders(new RankedComparator(), globalRequestFilters);
      this.sortedGlobalResponseFilters = Providers.sortRankedProviders(new RankedComparator(), globalResponseFilters);
   }

   public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> getNameBoundRequestFilters() {
      return this.nameBoundRequestFilters;
   }

   public MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> getNameBoundRequestFiltersInverse() {
      return this.nameBoundRequestFiltersInverse;
   }

   public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> getNameBoundResponseFilters() {
      return this.nameBoundResponseFilters;
   }

   public MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> getNameBoundResponseFiltersInverse() {
      return this.nameBoundResponseFiltersInverse;
   }

   public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> getNameBoundReaderInterceptors() {
      return this.nameBoundReaderInterceptors;
   }

   public MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> getNameBoundReaderInterceptorsInverse() {
      return this.nameBoundReaderInterceptorsInverse;
   }

   public MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> getNameBoundWriterInterceptors() {
      return this.nameBoundWriterInterceptors;
   }

   public MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> getNameBoundWriterInterceptorsInverse() {
      return this.nameBoundWriterInterceptorsInverse;
   }

   public Iterable<RankedProvider<ContainerRequestFilter>> getGlobalRequestFilters() {
      return this.globalRequestFilters;
   }

   public Iterable<RankedProvider<ContainerResponseFilter>> getGlobalResponseFilters() {
      return this.globalResponseFilters;
   }

   public Iterable<ContainerRequestFilter> getSortedGlobalRequestFilters() {
      return this.sortedGlobalRequestFilters;
   }

   public Iterable<ContainerResponseFilter> getSortedGlobalResponseFilters() {
      return this.sortedGlobalResponseFilters;
   }

   public Iterable<RankedProvider<ReaderInterceptor>> getGlobalReaderInterceptors() {
      return this.globalReaderInterceptors;
   }

   public Iterable<RankedProvider<WriterInterceptor>> getGlobalWriterInterceptors() {
      return this.globalWriterInterceptors;
   }

   public Iterable<ReaderInterceptor> getSortedGlobalReaderInterceptors() {
      return this.sortedGlobalReaderInterceptors;
   }

   public Iterable<WriterInterceptor> getSortedGlobalWriterInterceptors() {
      return this.sortedGlobalWriterInterceptors;
   }

   public Iterable<DynamicFeature> getDynamicFeatures() {
      return this.dynamicFeatures;
   }

   public List<RankedProvider<ContainerRequestFilter>> getPreMatchFilters() {
      return this.preMatchFilters;
   }
}
