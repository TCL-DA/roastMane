package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ParamConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.glassfish.jersey.server.internal.LocalizationMessages;

abstract class CollectionExtractor<T> extends AbstractParamValueExtractor<T> implements MultivaluedParameterExtractor<Collection<T>> {
   protected CollectionExtractor(ParamConverter<T> converter, String parameterName, String defaultStringValue) {
      super(converter, parameterName, defaultStringValue);
   }

   public Collection<T> extract(MultivaluedMap<String, String> parameters) {
      List<String> stringList = (List<String>)parameters.get(this.getName());
      Collection<T> valueList = this.newCollection();
      if (stringList != null) {
         for (String v : stringList) {
            valueList.add(this.fromString(v));
         }
      } else if (this.isDefaultValueRegistered()) {
         valueList.add(this.defaultValue());
      }

      return valueList;
   }

   protected abstract Collection<T> newCollection();

   public static <T> CollectionExtractor getInstance(Class<?> collectionType, ParamConverter<T> converter, String parameterName, String defaultValueString) {
      if (List.class == collectionType) {
         return new CollectionExtractor.ListValueOf(converter, parameterName, defaultValueString);
      } else if (Set.class == collectionType) {
         return new CollectionExtractor.SetValueOf(converter, parameterName, defaultValueString);
      } else if (SortedSet.class == collectionType) {
         return new CollectionExtractor.SortedSetValueOf(converter, parameterName, defaultValueString);
      } else {
         throw new ProcessingException(LocalizationMessages.COLLECTION_EXTRACTOR_TYPE_UNSUPPORTED());
      }
   }

   private static final class ListValueOf<T> extends CollectionExtractor<T> {
      ListValueOf(ParamConverter<T> converter, String parameter, String defaultValueString) {
         super(converter, parameter, defaultValueString);
      }

      protected List<T> newCollection() {
         return new ArrayList<>();
      }
   }

   private static final class SetValueOf<T> extends CollectionExtractor<T> {
      SetValueOf(ParamConverter<T> converter, String parameter, String defaultValueString) {
         super(converter, parameter, defaultValueString);
      }

      protected Set<T> newCollection() {
         return new HashSet<>();
      }
   }

   private static final class SortedSetValueOf<T> extends CollectionExtractor<T> {
      SortedSetValueOf(ParamConverter<T> converter, String parameter, String defaultValueString) {
         super(converter, parameter, defaultValueString);
      }

      protected SortedSet<T> newCollection() {
         return new TreeSet<>();
      }
   }
}
