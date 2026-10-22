package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Value;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface VisibilityChecker<T extends VisibilityChecker<T>> {
   T with(JsonAutoDetect var1);

   T withOverrides(Value var1);

   T with(Visibility var1);

   T withVisibility(PropertyAccessor var1, Visibility var2);

   T withGetterVisibility(Visibility var1);

   T withIsGetterVisibility(Visibility var1);

   T withSetterVisibility(Visibility var1);

   T withCreatorVisibility(Visibility var1);

   T withFieldVisibility(Visibility var1);

   boolean isGetterVisible(Method var1);

   boolean isGetterVisible(AnnotatedMethod var1);

   boolean isIsGetterVisible(Method var1);

   boolean isIsGetterVisible(AnnotatedMethod var1);

   boolean isSetterVisible(Method var1);

   boolean isSetterVisible(AnnotatedMethod var1);

   boolean isCreatorVisible(Member var1);

   boolean isCreatorVisible(AnnotatedMember var1);

   boolean isFieldVisible(Field var1);

   boolean isFieldVisible(AnnotatedField var1);

   public static class Std implements VisibilityChecker<VisibilityChecker.Std>, Serializable {
      private static final long serialVersionUID = 1L;
      protected static final VisibilityChecker.Std DEFAULT = new VisibilityChecker.Std(
         Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.ANY, Visibility.ANY, Visibility.PUBLIC_ONLY
      );
      protected static final VisibilityChecker.Std ALL_PUBLIC = new VisibilityChecker.Std(
         Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY, Visibility.PUBLIC_ONLY
      );
      protected final Visibility _getterMinLevel;
      protected final Visibility _isGetterMinLevel;
      protected final Visibility _setterMinLevel;
      protected final Visibility _creatorMinLevel;
      protected final Visibility _fieldMinLevel;

      public static VisibilityChecker.Std defaultInstance() {
         return DEFAULT;
      }

      public static VisibilityChecker.Std allPublicInstance() {
         return ALL_PUBLIC;
      }

      public Std(JsonAutoDetect ann) {
         this._getterMinLevel = ann.getterVisibility();
         this._isGetterMinLevel = ann.isGetterVisibility();
         this._setterMinLevel = ann.setterVisibility();
         this._creatorMinLevel = ann.creatorVisibility();
         this._fieldMinLevel = ann.fieldVisibility();
      }

      public Std(Visibility getter, Visibility isGetter, Visibility setter, Visibility creator, Visibility field) {
         this._getterMinLevel = getter;
         this._isGetterMinLevel = isGetter;
         this._setterMinLevel = setter;
         this._creatorMinLevel = creator;
         this._fieldMinLevel = field;
      }

      public Std(Visibility v) {
         if (v == Visibility.DEFAULT) {
            this._getterMinLevel = DEFAULT._getterMinLevel;
            this._isGetterMinLevel = DEFAULT._isGetterMinLevel;
            this._setterMinLevel = DEFAULT._setterMinLevel;
            this._creatorMinLevel = DEFAULT._creatorMinLevel;
            this._fieldMinLevel = DEFAULT._fieldMinLevel;
         } else {
            this._getterMinLevel = v;
            this._isGetterMinLevel = v;
            this._setterMinLevel = v;
            this._creatorMinLevel = v;
            this._fieldMinLevel = v;
         }
      }

      public static VisibilityChecker.Std construct(Value vis) {
         return DEFAULT.withOverrides(vis);
      }

      protected VisibilityChecker.Std _with(Visibility g, Visibility isG, Visibility s, Visibility cr, Visibility f) {
         return g == this._getterMinLevel
               && isG == this._isGetterMinLevel
               && s == this._setterMinLevel
               && cr == this._creatorMinLevel
               && f == this._fieldMinLevel
            ? this
            : new VisibilityChecker.Std(g, isG, s, cr, f);
      }

      public VisibilityChecker.Std with(JsonAutoDetect ann) {
         return ann != null
            ? this._with(
               this._defaultOrOverride(this._getterMinLevel, ann.getterVisibility()),
               this._defaultOrOverride(this._isGetterMinLevel, ann.isGetterVisibility()),
               this._defaultOrOverride(this._setterMinLevel, ann.setterVisibility()),
               this._defaultOrOverride(this._creatorMinLevel, ann.creatorVisibility()),
               this._defaultOrOverride(this._fieldMinLevel, ann.fieldVisibility())
            )
            : this;
      }

      public VisibilityChecker.Std withOverrides(Value vis) {
         return vis != null
            ? this._with(
               this._defaultOrOverride(this._getterMinLevel, vis.getGetterVisibility()),
               this._defaultOrOverride(this._isGetterMinLevel, vis.getIsGetterVisibility()),
               this._defaultOrOverride(this._setterMinLevel, vis.getSetterVisibility()),
               this._defaultOrOverride(this._creatorMinLevel, vis.getCreatorVisibility()),
               this._defaultOrOverride(this._fieldMinLevel, vis.getFieldVisibility())
            )
            : this;
      }

      private Visibility _defaultOrOverride(Visibility defaults, Visibility override) {
         return override == Visibility.DEFAULT ? defaults : override;
      }

      public VisibilityChecker.Std with(Visibility v) {
         return v == Visibility.DEFAULT ? DEFAULT : new VisibilityChecker.Std(v);
      }

      public VisibilityChecker.Std withVisibility(PropertyAccessor method, Visibility v) {
         switch (method) {
            case GETTER:
               return this.withGetterVisibility(v);
            case SETTER:
               return this.withSetterVisibility(v);
            case CREATOR:
               return this.withCreatorVisibility(v);
            case FIELD:
               return this.withFieldVisibility(v);
            case IS_GETTER:
               return this.withIsGetterVisibility(v);
            case ALL:
               return this.with(v);
            default:
               return this;
         }
      }

      public VisibilityChecker.Std withGetterVisibility(Visibility v) {
         if (v == Visibility.DEFAULT) {
            v = DEFAULT._getterMinLevel;
         }

         return this._getterMinLevel == v
            ? this
            : new VisibilityChecker.Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withIsGetterVisibility(Visibility v) {
         if (v == Visibility.DEFAULT) {
            v = DEFAULT._isGetterMinLevel;
         }

         return this._isGetterMinLevel == v
            ? this
            : new VisibilityChecker.Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withSetterVisibility(Visibility v) {
         if (v == Visibility.DEFAULT) {
            v = DEFAULT._setterMinLevel;
         }

         return this._setterMinLevel == v
            ? this
            : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withCreatorVisibility(Visibility v) {
         if (v == Visibility.DEFAULT) {
            v = DEFAULT._creatorMinLevel;
         }

         return this._creatorMinLevel == v
            ? this
            : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
      }

      public VisibilityChecker.Std withFieldVisibility(Visibility v) {
         if (v == Visibility.DEFAULT) {
            v = DEFAULT._fieldMinLevel;
         }

         return this._fieldMinLevel == v
            ? this
            : new VisibilityChecker.Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
      }

      @Override
      public boolean isCreatorVisible(Member m) {
         return this._creatorMinLevel.isVisible(m);
      }

      @Override
      public boolean isCreatorVisible(AnnotatedMember m) {
         return this.isCreatorVisible(m.getMember());
      }

      @Override
      public boolean isFieldVisible(Field f) {
         return this._fieldMinLevel.isVisible(f);
      }

      @Override
      public boolean isFieldVisible(AnnotatedField f) {
         return this.isFieldVisible(f.getAnnotated());
      }

      @Override
      public boolean isGetterVisible(Method m) {
         return this._getterMinLevel.isVisible(m);
      }

      @Override
      public boolean isGetterVisible(AnnotatedMethod m) {
         return this.isGetterVisible(m.getAnnotated());
      }

      @Override
      public boolean isIsGetterVisible(Method m) {
         return this._isGetterMinLevel.isVisible(m);
      }

      @Override
      public boolean isIsGetterVisible(AnnotatedMethod m) {
         return this.isIsGetterVisible(m.getAnnotated());
      }

      @Override
      public boolean isSetterVisible(Method m) {
         return this._setterMinLevel.isVisible(m);
      }

      @Override
      public boolean isSetterVisible(AnnotatedMethod m) {
         return this.isSetterVisible(m.getAnnotated());
      }

      @Override
      public String toString() {
         return String.format(
            "[Visibility: getter=%s,isGetter=%s,setter=%s,creator=%s,field=%s]",
            this._getterMinLevel,
            this._isGetterMinLevel,
            this._setterMinLevel,
            this._creatorMinLevel,
            this._fieldMinLevel
         );
      }
   }
}
