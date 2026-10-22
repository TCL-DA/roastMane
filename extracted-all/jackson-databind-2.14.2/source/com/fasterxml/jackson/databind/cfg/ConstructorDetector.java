package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;

public final class ConstructorDetector implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final ConstructorDetector DEFAULT = new ConstructorDetector(ConstructorDetector.SingleArgConstructor.HEURISTIC);
   public static final ConstructorDetector USE_PROPERTIES_BASED = new ConstructorDetector(ConstructorDetector.SingleArgConstructor.PROPERTIES);
   public static final ConstructorDetector USE_DELEGATING = new ConstructorDetector(ConstructorDetector.SingleArgConstructor.DELEGATING);
   public static final ConstructorDetector EXPLICIT_ONLY = new ConstructorDetector(ConstructorDetector.SingleArgConstructor.REQUIRE_MODE);
   protected final ConstructorDetector.SingleArgConstructor _singleArgMode;
   protected final boolean _requireCtorAnnotation;
   protected final boolean _allowJDKTypeCtors;

   protected ConstructorDetector(ConstructorDetector.SingleArgConstructor singleArgMode, boolean requireCtorAnnotation, boolean allowJDKTypeCtors) {
      this._singleArgMode = singleArgMode;
      this._requireCtorAnnotation = requireCtorAnnotation;
      this._allowJDKTypeCtors = allowJDKTypeCtors;
   }

   protected ConstructorDetector(ConstructorDetector.SingleArgConstructor singleArgMode) {
      this(singleArgMode, false, false);
   }

   public ConstructorDetector withSingleArgMode(ConstructorDetector.SingleArgConstructor singleArgMode) {
      return new ConstructorDetector(singleArgMode, this._requireCtorAnnotation, this._allowJDKTypeCtors);
   }

   public ConstructorDetector withRequireAnnotation(boolean state) {
      return new ConstructorDetector(this._singleArgMode, state, this._allowJDKTypeCtors);
   }

   public ConstructorDetector withAllowJDKTypeConstructors(boolean state) {
      return new ConstructorDetector(this._singleArgMode, this._requireCtorAnnotation, state);
   }

   public ConstructorDetector.SingleArgConstructor singleArgMode() {
      return this._singleArgMode;
   }

   public boolean requireCtorAnnotation() {
      return this._requireCtorAnnotation;
   }

   public boolean allowJDKTypeConstructors() {
      return this._allowJDKTypeCtors;
   }

   public boolean singleArgCreatorDefaultsToDelegating() {
      return this._singleArgMode == ConstructorDetector.SingleArgConstructor.DELEGATING;
   }

   public boolean singleArgCreatorDefaultsToProperties() {
      return this._singleArgMode == ConstructorDetector.SingleArgConstructor.PROPERTIES;
   }

   public boolean shouldIntrospectorImplicitConstructors(Class<?> rawType) {
      return this._requireCtorAnnotation ? false : this._allowJDKTypeCtors || !ClassUtil.isJDKClass(rawType) || Throwable.class.isAssignableFrom(rawType);
   }

   public static enum SingleArgConstructor {
      DELEGATING,
      PROPERTIES,
      HEURISTIC,
      REQUIRE_MODE;
   }
}
