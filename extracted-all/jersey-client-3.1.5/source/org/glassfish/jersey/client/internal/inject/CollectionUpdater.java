package org.glassfish.jersey.client.internal.inject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.ParamConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.glassfish.jersey.client.inject.ParameterUpdater;
import org.glassfish.jersey.client.internal.LocalizationMessages;

abstract class CollectionUpdater<T> extends AbstractParamValueUpdater<T> implements ParameterUpdater<Collection<T>, Collection<String>> {
   protected CollectionUpdater(ParamConverter<T> converter, String parameterName, String defaultValue) {
      super(converter, parameterName, defaultValue);
   }

   public Collection<String> update(Collection<T> values) {
      Collection<String> results = Collections.EMPTY_LIST;
      if (values != null) {
         results = values.stream().map(item -> this.toString((T)item)).collect(Collectors.toList());
      } else if (this.isDefaultValueRegistered()) {
         results = Collections.singletonList(this.getDefaultValueString());
      }

      return results;
   }

   protected abstract Collection<String> newCollection();

   public static <T> CollectionUpdater getInstance(Class<?> collectionType, ParamConverter<T> converter, String parameterName, String defaultValue) {
      if (List.class == collectionType) {
         return new CollectionUpdater.ListValueOf(converter, parameterName, defaultValue);
      } else if (Set.class == collectionType) {
         return new CollectionUpdater.SetValueOf(converter, parameterName, defaultValue);
      } else if (SortedSet.class == collectionType) {
         return new CollectionUpdater.SortedSetValueOf(converter, parameterName, defaultValue);
      } else {
         throw new ProcessingException(LocalizationMessages.COLLECTION_UPDATER_TYPE_UNSUPPORTED());
      }
   }

   private static final class ListValueOf<T> extends CollectionUpdater<T> {
      ListValueOf(ParamConverter<T> converter, String parameter, String defaultValue) {
         super(converter, parameter, defaultValue);
      }

      protected List<String> newCollection() {
         return new ArrayList<>();
      }
   }

   private static final class SetValueOf<T> extends CollectionUpdater<T> {
      SetValueOf(ParamConverter<T> converter, String parameter, String defaultValue) {
         super(converter, parameter, defaultValue);
      }

      protected Set<String> newCollection() {
         return new HashSet<>();
      }
   }

   private static final class SortedSetValueOf<T> extends CollectionUpdater<T> {
      SortedSetValueOf(ParamConverter<T> converter, String parameter, String defaultValue) {
         super(converter, parameter, defaultValue);
      }

      protected SortedSet<String> newCollection() {
         return new TreeSet<>();
      }
   }
}
