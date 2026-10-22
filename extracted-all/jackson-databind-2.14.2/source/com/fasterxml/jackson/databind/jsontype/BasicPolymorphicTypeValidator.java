package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class BasicPolymorphicTypeValidator extends PolymorphicTypeValidator.Base implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Set<Class<?>> _invalidBaseTypes;
   protected final BasicPolymorphicTypeValidator.TypeMatcher[] _baseTypeMatchers;
   protected final BasicPolymorphicTypeValidator.NameMatcher[] _subTypeNameMatchers;
   protected final BasicPolymorphicTypeValidator.TypeMatcher[] _subClassMatchers;

   protected BasicPolymorphicTypeValidator(
      Set<Class<?>> invalidBaseTypes,
      BasicPolymorphicTypeValidator.TypeMatcher[] baseTypeMatchers,
      BasicPolymorphicTypeValidator.NameMatcher[] subTypeNameMatchers,
      BasicPolymorphicTypeValidator.TypeMatcher[] subClassMatchers
   ) {
      this._invalidBaseTypes = invalidBaseTypes;
      this._baseTypeMatchers = baseTypeMatchers;
      this._subTypeNameMatchers = subTypeNameMatchers;
      this._subClassMatchers = subClassMatchers;
   }

   public static BasicPolymorphicTypeValidator.Builder builder() {
      return new BasicPolymorphicTypeValidator.Builder();
   }

   @Override
   public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> ctxt, JavaType baseType) {
      Class<?> rawBase = baseType.getRawClass();
      if (this._invalidBaseTypes != null && this._invalidBaseTypes.contains(rawBase)) {
         return PolymorphicTypeValidator.Validity.DENIED;
      } else {
         if (this._baseTypeMatchers != null) {
            for (BasicPolymorphicTypeValidator.TypeMatcher m : this._baseTypeMatchers) {
               if (m.match(ctxt, rawBase)) {
                  return PolymorphicTypeValidator.Validity.ALLOWED;
               }
            }
         }

         return PolymorphicTypeValidator.Validity.INDETERMINATE;
      }
   }

   @Override
   public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> ctxt, JavaType baseType, String subClassName) throws JsonMappingException {
      if (this._subTypeNameMatchers != null) {
         for (BasicPolymorphicTypeValidator.NameMatcher m : this._subTypeNameMatchers) {
            if (m.match(ctxt, subClassName)) {
               return PolymorphicTypeValidator.Validity.ALLOWED;
            }
         }
      }

      return PolymorphicTypeValidator.Validity.INDETERMINATE;
   }

   @Override
   public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> ctxt, JavaType baseType, JavaType subType) throws JsonMappingException {
      if (this._subClassMatchers != null) {
         Class<?> subClass = subType.getRawClass();

         for (BasicPolymorphicTypeValidator.TypeMatcher m : this._subClassMatchers) {
            if (m.match(ctxt, subClass)) {
               return PolymorphicTypeValidator.Validity.ALLOWED;
            }
         }
      }

      return PolymorphicTypeValidator.Validity.INDETERMINATE;
   }

   public static class Builder {
      protected Set<Class<?>> _invalidBaseTypes;
      protected List<BasicPolymorphicTypeValidator.TypeMatcher> _baseTypeMatchers;
      protected List<BasicPolymorphicTypeValidator.NameMatcher> _subTypeNameMatchers;
      protected List<BasicPolymorphicTypeValidator.TypeMatcher> _subTypeClassMatchers;

      protected Builder() {
      }

      public BasicPolymorphicTypeValidator.Builder allowIfBaseType(final Class<?> baseOfBase) {
         return this._appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, Class<?> clazz) {
               return baseOfBase.isAssignableFrom(clazz);
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfBaseType(final Pattern patternForBase) {
         return this._appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, Class<?> clazz) {
               return patternForBase.matcher(clazz.getName()).matches();
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfBaseType(final String prefixForBase) {
         return this._appendBaseMatcher(new BasicPolymorphicTypeValidator.TypeMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, Class<?> clazz) {
               return clazz.getName().startsWith(prefixForBase);
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
         return this._appendBaseMatcher(matcher);
      }

      public BasicPolymorphicTypeValidator.Builder denyForExactBaseType(Class<?> baseTypeToDeny) {
         if (this._invalidBaseTypes == null) {
            this._invalidBaseTypes = new HashSet<>();
         }

         this._invalidBaseTypes.add(baseTypeToDeny);
         return this;
      }

      public BasicPolymorphicTypeValidator.Builder allowIfSubType(final Class<?> subTypeBase) {
         return this._appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, Class<?> clazz) {
               return subTypeBase.isAssignableFrom(clazz);
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfSubType(final Pattern patternForSubType) {
         return this._appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, String clazzName) {
               return patternForSubType.matcher(clazzName).matches();
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfSubType(final String prefixForSubType) {
         return this._appendSubNameMatcher(new BasicPolymorphicTypeValidator.NameMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, String clazzName) {
               return clazzName.startsWith(prefixForSubType);
            }
         });
      }

      public BasicPolymorphicTypeValidator.Builder allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
         return this._appendSubClassMatcher(matcher);
      }

      public BasicPolymorphicTypeValidator.Builder allowIfSubTypeIsArray() {
         return this._appendSubClassMatcher(new BasicPolymorphicTypeValidator.TypeMatcher() {
            @Override
            public boolean match(MapperConfig<?> config, Class<?> clazz) {
               return clazz.isArray();
            }
         });
      }

      public BasicPolymorphicTypeValidator build() {
         return new BasicPolymorphicTypeValidator(
            this._invalidBaseTypes,
            this._baseTypeMatchers == null ? null : this._baseTypeMatchers.toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0]),
            this._subTypeNameMatchers == null ? null : this._subTypeNameMatchers.toArray(new BasicPolymorphicTypeValidator.NameMatcher[0]),
            this._subTypeClassMatchers == null ? null : this._subTypeClassMatchers.toArray(new BasicPolymorphicTypeValidator.TypeMatcher[0])
         );
      }

      protected BasicPolymorphicTypeValidator.Builder _appendBaseMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
         if (this._baseTypeMatchers == null) {
            this._baseTypeMatchers = new ArrayList<>();
         }

         this._baseTypeMatchers.add(matcher);
         return this;
      }

      protected BasicPolymorphicTypeValidator.Builder _appendSubNameMatcher(BasicPolymorphicTypeValidator.NameMatcher matcher) {
         if (this._subTypeNameMatchers == null) {
            this._subTypeNameMatchers = new ArrayList<>();
         }

         this._subTypeNameMatchers.add(matcher);
         return this;
      }

      protected BasicPolymorphicTypeValidator.Builder _appendSubClassMatcher(BasicPolymorphicTypeValidator.TypeMatcher matcher) {
         if (this._subTypeClassMatchers == null) {
            this._subTypeClassMatchers = new ArrayList<>();
         }

         this._subTypeClassMatchers.add(matcher);
         return this;
      }
   }

   public abstract static class NameMatcher {
      public abstract boolean match(MapperConfig<?> var1, String var2);
   }

   public abstract static class TypeMatcher {
      public abstract boolean match(MapperConfig<?> var1, Class<?> var2);
   }
}
